package br.dev.lucasaguiar.hermes_api.service;

import br.dev.lucasaguiar.hermes_api.domain.Account;
import br.dev.lucasaguiar.hermes_api.domain.AppliedFeeSnapshot;
import br.dev.lucasaguiar.hermes_api.domain.TransferSchedule;
import br.dev.lucasaguiar.hermes_api.domain.TransferStatus;
import br.dev.lucasaguiar.hermes_api.dto.request.TransferRequest;
import br.dev.lucasaguiar.hermes_api.dto.response.TransferScheduleResponse;
import br.dev.lucasaguiar.hermes_api.dto.response.TransferSimulateResponse;
import br.dev.lucasaguiar.hermes_api.exception.AccountNotFoundException;
import br.dev.lucasaguiar.hermes_api.exception.DuplicateTransferException;
import br.dev.lucasaguiar.hermes_api.exception.InsufficientBalanceException;
import br.dev.lucasaguiar.hermes_api.exception.TransferScheduleNotFoundException;
import br.dev.lucasaguiar.hermes_api.repository.AccountRepository;
import br.dev.lucasaguiar.hermes_api.repository.TransferScheduleRepository;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferSchedulingService {

    private final TransferScheduleRepository transferScheduleRepository;
    private final AccountRepository accountRepository;
    private final FeeCalculationService feeCalculationService;

    @Transactional(readOnly = true)
    public Page<TransferScheduleResponse> list(
        String sourceAccountNumber,
        String targetAccountNumber,
        Pageable pageable
    ) {
        return transferScheduleRepository
            .findAllByFilters(sourceAccountNumber, targetAccountNumber, pageable)
            .map(TransferScheduleResponse::from);
    }

    @Transactional(readOnly = true)
    public TransferScheduleResponse findById(UUID id) {
        return transferScheduleRepository.findById(id)
            .map(TransferScheduleResponse::from)
            .orElseThrow(() -> new TransferScheduleNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public TransferSimulateResponse simulate(TransferRequest transferRequest) {
        Account sourceAccount = accountRepository.findByAccountNumber(transferRequest.getSourceAccount())
            .orElseThrow(() -> new AccountNotFoundException(transferRequest.getSourceAccount()));
        Account targetAccount = accountRepository.findByAccountNumber(transferRequest.getTargetAccount())
            .orElseThrow(() -> new AccountNotFoundException(transferRequest.getTargetAccount()));

        if (sourceAccount.getAvailableBalance().compareTo(transferRequest.getTransferAmount()) < 0) {
            throw new InsufficientBalanceException();
        }

        AppliedFeeSnapshot feeSnapshot = feeCalculationService.calculateTransferFee(
            transferRequest.getTransferAmount(),
            transferRequest.getTransferDate()
        );

        return TransferSimulateResponse.from(
            sourceAccount.getAccountNumber(),
            targetAccount.getAccountNumber(),
            transferRequest.getTransferAmount(),
            transferRequest.getTransferDate(),
            feeSnapshot
        );
    }

    @Transactional
    public void schedule(TransferRequest transferRequest) {
        LocalDateTime now = LocalDateTime.now();

        Account sourceAccount = accountRepository.findByAccountNumber(transferRequest.getSourceAccount()).orElseThrow(() -> new AccountNotFoundException(transferRequest.getSourceAccount()));
        Account targetAccount = accountRepository.findByAccountNumber(transferRequest.getTargetAccount()).orElseThrow(() ->new AccountNotFoundException(transferRequest.getTargetAccount()));

        String fingerprint = generateFingerprint(sourceAccount.getAccountNumber(), targetAccount.getAccountNumber(), transferRequest.getTransferAmount(), transferRequest.getTransferDate());

        if (transferScheduleRepository.existsByFingerprintAndCreatedAtAfter(
            fingerprint,
            now.minusMinutes(1)
        )) {
            throw new DuplicateTransferException();
        }

        if (sourceAccount.getAvailableBalance().compareTo(transferRequest.getTransferAmount()) < 0) {
            throw new InsufficientBalanceException();
        }

        AppliedFeeSnapshot feeSnapshot = feeCalculationService.calculateTransferFee(transferRequest.getTransferAmount(), transferRequest.getTransferDate());

        sourceAccount.setCommittedBalance(
            sourceAccount.getCommittedBalance().add(transferRequest.getTransferAmount())
        );

        accountRepository.save(sourceAccount);

        TransferSchedule schedule = new TransferSchedule();

        schedule.setSourceAccount(sourceAccount);
        schedule.setTargetAccount(targetAccount);
        schedule.setTransferAmount(transferRequest.getTransferAmount());
        schedule.setTransferDate(transferRequest.getTransferDate());
        schedule.setSchedulingDate(now.toLocalDate());
        schedule.setCreatedAt(now);
        schedule.setStatus(TransferStatus.SCHEDULED);
        schedule.setFingerprint(fingerprint);
        schedule.setAppliedFee(feeSnapshot);

        transferScheduleRepository.save(schedule);
    }

    private String generateFingerprint(String sourceAccount, String targetAccount,
        BigDecimal transferAmount, LocalDate transferDate) {

        String raw = sourceAccount + targetAccount + transferAmount.toPlainString() + transferDate.toString();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating fingerprint", e);
        }
    }

}
