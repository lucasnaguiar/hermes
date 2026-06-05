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
import br.dev.lucasaguiar.hermes_api.exception.SameAccountTransferException;
import br.dev.lucasaguiar.hermes_api.exception.TransferScheduleNotFoundException;
import br.dev.lucasaguiar.hermes_api.repository.AccountRepository;
import br.dev.lucasaguiar.hermes_api.repository.TransferScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferSchedulingServiceTest {

    @Mock
    private TransferScheduleRepository transferScheduleRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private FeeCalculationService feeCalculationService;

    @InjectMocks
    private TransferSchedulingService service;

    private Account sourceAccount;
    private Account targetAccount;
    private TransferRequest request;
    private AppliedFeeSnapshot feeSnapshot;

    @BeforeEach
    void setUp() {
        sourceAccount = new Account();
        sourceAccount.setAccountNumber("1234567890");
        sourceAccount.setBalance(new BigDecimal("10000.00"));
        sourceAccount.setCommittedBalance(new BigDecimal("0.00"));

        targetAccount = new Account();
        targetAccount.setAccountNumber("0987654321");
        targetAccount.setBalance(new BigDecimal("5000.00"));
        targetAccount.setCommittedBalance(new BigDecimal("0.00"));

        request = new TransferRequest();
        request.setSourceAccount("1234567890");
        request.setTargetAccount("0987654321");
        request.setTransferAmount(new BigDecimal("500.00"));
        request.setTransferDate(LocalDate.now().plusDays(5));

        feeSnapshot = new AppliedFeeSnapshot(
            new BigDecimal("12.00"),
            BigDecimal.ZERO,
            new BigDecimal("12.00"),
            1, 10
        );
    }

    // -------------------------------------------------------------------------
    // schedule()
    // -------------------------------------------------------------------------

    @Nested
    class Schedule {

        @Test
        void deveAgendarTransferenciaComSucesso() {
            when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(sourceAccount));
            when(accountRepository.findByAccountNumber("0987654321")).thenReturn(Optional.of(targetAccount));
            when(transferScheduleRepository.existsByFingerprintAndCreatedAtAfter(anyString(), any())).thenReturn(false);
            when(feeCalculationService.calculateTransferFee(any(), any())).thenReturn(feeSnapshot);

            service.schedule(request);

            ArgumentCaptor<TransferSchedule> captor = ArgumentCaptor.forClass(TransferSchedule.class);
            verify(transferScheduleRepository).save(captor.capture());

            TransferSchedule saved = captor.getValue();
            assertThat(saved.getTransferAmount()).isEqualByComparingTo("500.00");
            assertThat(saved.getStatus()).isEqualTo(TransferStatus.SCHEDULED);
            assertThat(saved.getAppliedFee()).isEqualTo(feeSnapshot);
        }

        @Test
        void deveAtualizarSaldoComprometidoDaContaOrigem() {
            when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(sourceAccount));
            when(accountRepository.findByAccountNumber("0987654321")).thenReturn(Optional.of(targetAccount));
            when(transferScheduleRepository.existsByFingerprintAndCreatedAtAfter(anyString(), any())).thenReturn(false);
            when(feeCalculationService.calculateTransferFee(any(), any())).thenReturn(feeSnapshot);

            service.schedule(request);

            ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
            verify(accountRepository).save(captor.capture());
            assertThat(captor.getValue().getCommittedBalance()).isEqualByComparingTo("500.00");
        }

        @Test
        void deveLancarExcecaoQuandoContasForemIguais() {
            request.setTargetAccount("1234567890");

            assertThatThrownBy(() -> service.schedule(request))
                .isInstanceOf(SameAccountTransferException.class);

            verify(accountRepository, never()).findByAccountNumber(anyString());
        }

        @Test
        void deveLancarExcecaoQuandoContaOrigemNaoEncontrada() {
            when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.schedule(request))
                .isInstanceOf(AccountNotFoundException.class);

            verify(transferScheduleRepository, never()).save(any());
        }

        @Test
        void deveLancarExcecaoQuandoContaDestinoNaoEncontrada() {
            when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(sourceAccount));
            when(accountRepository.findByAccountNumber("0987654321")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.schedule(request))
                .isInstanceOf(AccountNotFoundException.class);

            verify(transferScheduleRepository, never()).save(any());
        }

        @Test
        void deveLancarExcecaoQuandoTransferenciaForDuplicada() {
            when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(sourceAccount));
            when(accountRepository.findByAccountNumber("0987654321")).thenReturn(Optional.of(targetAccount));
            when(transferScheduleRepository.existsByFingerprintAndCreatedAtAfter(anyString(), any())).thenReturn(true);

            assertThatThrownBy(() -> service.schedule(request))
                .isInstanceOf(DuplicateTransferException.class);

            verify(transferScheduleRepository, never()).save(any());
        }

        @Test
        void deveLancarExcecaoQuandoSaldoInsuficiente() {
            request.setTransferAmount(new BigDecimal("15000.00"));

            when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(sourceAccount));
            when(accountRepository.findByAccountNumber("0987654321")).thenReturn(Optional.of(targetAccount));
            when(transferScheduleRepository.existsByFingerprintAndCreatedAtAfter(anyString(), any())).thenReturn(false);

            assertThatThrownBy(() -> service.schedule(request))
                .isInstanceOf(InsufficientBalanceException.class);

            verify(transferScheduleRepository, never()).save(any());
        }

        @Test
        void deveLancarExcecaoQuandoSaldoDisponiveInsuficienteComSaldoComprometido() {
            sourceAccount.setCommittedBalance(new BigDecimal("9600.00")); // disponível: 400.00

            when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(sourceAccount));
            when(accountRepository.findByAccountNumber("0987654321")).thenReturn(Optional.of(targetAccount));
            when(transferScheduleRepository.existsByFingerprintAndCreatedAtAfter(anyString(), any())).thenReturn(false);

            assertThatThrownBy(() -> service.schedule(request))
                .isInstanceOf(InsufficientBalanceException.class);
        }
    }

    // -------------------------------------------------------------------------
    // simulate()
    // -------------------------------------------------------------------------

    @Nested
    class Simulate {

        @Test
        void deveRetornarSimulacaoComSucesso() {
            when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(sourceAccount));
            when(accountRepository.findByAccountNumber("0987654321")).thenReturn(Optional.of(targetAccount));
            when(feeCalculationService.calculateTransferFee(any(), any())).thenReturn(feeSnapshot);

            TransferSimulateResponse response = service.simulate(request);

            assertThat(response.getSourceAccount()).isEqualTo("1234567890");
            assertThat(response.getTargetAccount()).isEqualTo("0987654321");
            assertThat(response.getTransferAmount()).isEqualByComparingTo("500.00");
            assertThat(response.getFeeAmount()).isEqualByComparingTo("12.00");
        }

        @Test
        void naoDevePersistarNadaNaSimulacao() {
            when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(sourceAccount));
            when(accountRepository.findByAccountNumber("0987654321")).thenReturn(Optional.of(targetAccount));
            when(feeCalculationService.calculateTransferFee(any(), any())).thenReturn(feeSnapshot);

            service.simulate(request);

            verify(transferScheduleRepository, never()).save(any());
            verify(accountRepository, never()).save(any());
        }

        @Test
        void deveLancarExcecaoQuandoContasForemIguaisNaSimulacao() {
            request.setTargetAccount("1234567890");

            assertThatThrownBy(() -> service.simulate(request))
                .isInstanceOf(SameAccountTransferException.class);
        }

        @Test
        void deveLancarExcecaoQuandoContaOrigemNaoEncontradaNaSimulacao() {
            when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.simulate(request))
                .isInstanceOf(AccountNotFoundException.class);
        }

        @Test
        void deveLancarExcecaoQuandoSaldoInsuficienteNaSimulacao() {
            request.setTransferAmount(new BigDecimal("15000.00"));
            when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(sourceAccount));
            when(accountRepository.findByAccountNumber("0987654321")).thenReturn(Optional.of(targetAccount));

            assertThatThrownBy(() -> service.simulate(request))
                .isInstanceOf(InsufficientBalanceException.class);
        }
    }

    // -------------------------------------------------------------------------
    // findById()
    // -------------------------------------------------------------------------

    @Nested
    class FindById {

        @Test
        void deveRetornarAgendamentoPorId() {
            UUID id = UUID.randomUUID();

            TransferSchedule schedule = new TransferSchedule();
            schedule.setSourceAccount(sourceAccount);
            schedule.setTargetAccount(targetAccount);
            schedule.setTransferAmount(new BigDecimal("500.00"));
            schedule.setTransferDate(LocalDate.now().plusDays(5));
            schedule.setSchedulingDate(LocalDate.now());
            schedule.setCreatedAt(LocalDateTime.now());
            schedule.setStatus(TransferStatus.SCHEDULED);
            schedule.setFingerprint("abc123");
            schedule.setAppliedFee(feeSnapshot);

            when(transferScheduleRepository.findById(id)).thenReturn(Optional.of(schedule));

            TransferScheduleResponse response = service.findById(id);

            assertThat(response.getSourceAccount()).isEqualTo("1234567890");
            assertThat(response.getStatus()).isEqualTo(TransferStatus.SCHEDULED);
        }

        @Test
        void deveLancarExcecaoQuandoAgendamentoNaoEncontrado() {
            UUID id = UUID.randomUUID();
            when(transferScheduleRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(TransferScheduleNotFoundException.class);
        }
    }
}
