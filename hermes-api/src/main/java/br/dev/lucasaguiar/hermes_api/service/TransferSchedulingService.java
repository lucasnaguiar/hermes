package br.dev.lucasaguiar.hermes_api.service;

import br.dev.lucasaguiar.hermes_api.domain.Account;
import br.dev.lucasaguiar.hermes_api.domain.AppliedFeeSnapshot;
import br.dev.lucasaguiar.hermes_api.domain.TransferSchedule;
import br.dev.lucasaguiar.hermes_api.domain.TransferStatus;
import br.dev.lucasaguiar.hermes_api.dto.request.TransferRequest;
import br.dev.lucasaguiar.hermes_api.exception.AccountNotFoundException;
import br.dev.lucasaguiar.hermes_api.exception.DuplicateTransferException;
import br.dev.lucasaguiar.hermes_api.exception.InsufficientBalanceException;
import br.dev.lucasaguiar.hermes_api.repository.AccountRepository;
import br.dev.lucasaguiar.hermes_api.repository.TransferScheduleRepository;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferSchedulingService {

    private final TransferScheduleRepository transferScheduleRepository;
    private final AccountRepository accountRepository;
    private final FeeCalculationService feeCalculationService;

    @Transactional
    public void schedule(TransferRequest transferRequest) {
        Account sourceAccount = accountRepository.findByAccountNumber(transferRequest.getSourceAccount()).orElseThrow(() -> new AccountNotFoundException(transferRequest.getSourceAccount()));
        Account targetAccount = accountRepository.findByAccountNumber(transferRequest.getTargetAccount()).orElseThrow(() ->new AccountNotFoundException(transferRequest.getTargetAccount()));

        String fingerprint = generateFingerprint(sourceAccount.getAccountNumber(), targetAccount.getAccountNumber(), transferRequest.getTransferAmount(), transferRequest.getTransferDate());

        if (transferScheduleRepository.existsByFingerprint(fingerprint)) {
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
        schedule.setSchedulingDate(LocalDate.now());
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
