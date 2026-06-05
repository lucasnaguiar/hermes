package br.dev.lucasaguiar.hermes_api.service;

import br.dev.lucasaguiar.hermes_api.domain.AppliedFeeSnapshot;
import br.dev.lucasaguiar.hermes_api.domain.TransferFeeRule;
import br.dev.lucasaguiar.hermes_api.exception.NoApplicableFeeRuleException;
import br.dev.lucasaguiar.hermes_api.repository.TransferFeeRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeeCalculationServiceTest {

    @Mock
    private TransferFeeRuleRepository transferFeeRuleRepository;

    @InjectMocks
    private FeeCalculationService service;

    private TransferFeeRule buildRule(int from, int to, String fixedAmount, String rate) {
        TransferFeeRule rule = new TransferFeeRule();
        rule.setDaysRangeFrom(from);
        rule.setDaysRangeTo(to);
        rule.setFixedAmount(new BigDecimal(fixedAmount));
        rule.setRate(new BigDecimal(rate));
        return rule;
    }

    @Nested
    class Calculo {

        @Test
        void deveLancarExcecaoQuandoNaoHouverRegraAplicavel() {
            when(transferFeeRuleRepository.findApplicableRule(anyInt())).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                service.calculateTransferFee(new BigDecimal("1000.00"), LocalDate.now().plusDays(60))
            ).isInstanceOf(NoApplicableFeeRuleException.class);
        }

        // Dia 0 — R$ 3,00 fixo + 2,5%
        // Para R$ 1000: 1000 * 0.025 + 3.00 = 28.00
        @Test
        void deveCalcularTaxaParaMesmoDia() {
            when(transferFeeRuleRepository.findApplicableRule(0))
                .thenReturn(Optional.of(buildRule(0, 0, "3.00", "2.50")));

            AppliedFeeSnapshot result = service.calculateTransferFee(
                new BigDecimal("1000.00"), LocalDate.now()
            );

            assertThat(result.getFeeAmount()).isEqualByComparingTo("28.00");
            assertThat(result.getFeeFixedAmount()).isEqualByComparingTo("3.00");
            assertThat(result.getFeeRate()).isEqualByComparingTo("2.50");
            assertThat(result.getFeeDaysRangeFrom()).isEqualTo(0);
            assertThat(result.getFeeDaysRangeTo()).isEqualTo(0);
        }

        // Dias 1-10 — R$ 12,00 fixo, sem percentual
        // Para R$ 1000: 0 + 12.00 = 12.00
        @Test
        void deveCalcularTaxaParaFaixaDe1a10Dias() {
            when(transferFeeRuleRepository.findApplicableRule(5))
                .thenReturn(Optional.of(buildRule(1, 10, "12.00", "0.00")));

            AppliedFeeSnapshot result = service.calculateTransferFee(
                new BigDecimal("1000.00"), LocalDate.now().plusDays(5)
            );

            assertThat(result.getFeeAmount()).isEqualByComparingTo("12.00");
        }

        // Dias 11-20 — 8,2%
        // Para R$ 1000: 1000 * 0.082 + 0 = 82.00
        @Test
        void deveCalcularTaxaParaFaixaDe11a20Dias() {
            when(transferFeeRuleRepository.findApplicableRule(15))
                .thenReturn(Optional.of(buildRule(11, 20, "0.00", "8.20")));

            AppliedFeeSnapshot result = service.calculateTransferFee(
                new BigDecimal("1000.00"), LocalDate.now().plusDays(15)
            );

            assertThat(result.getFeeAmount()).isEqualByComparingTo("82.00");
        }

        // Dias 21-30 — 6,9%
        // Para R$ 1000: 1000 * 0.069 + 0 = 69.00
        @Test
        void deveCalcularTaxaParaFaixaDe21a30Dias() {
            when(transferFeeRuleRepository.findApplicableRule(25))
                .thenReturn(Optional.of(buildRule(21, 30, "0.00", "6.90")));

            AppliedFeeSnapshot result = service.calculateTransferFee(
                new BigDecimal("1000.00"), LocalDate.now().plusDays(25)
            );

            assertThat(result.getFeeAmount()).isEqualByComparingTo("69.00");
        }

        // Dias 31-40 — 4,7%
        // Para R$ 1000: 1000 * 0.047 + 0 = 47.00
        @Test
        void deveCalcularTaxaParaFaixaDe31a40Dias() {
            when(transferFeeRuleRepository.findApplicableRule(35))
                .thenReturn(Optional.of(buildRule(31, 40, "0.00", "4.70")));

            AppliedFeeSnapshot result = service.calculateTransferFee(
                new BigDecimal("1000.00"), LocalDate.now().plusDays(35)
            );

            assertThat(result.getFeeAmount()).isEqualByComparingTo("47.00");
        }

        // Dias 41-50 — 1,7%
        // Para R$ 1000: 1000 * 0.017 + 0 = 17.00
        @Test
        void deveCalcularTaxaParaFaixaDe41a50Dias() {
            when(transferFeeRuleRepository.findApplicableRule(45))
                .thenReturn(Optional.of(buildRule(41, 50, "0.00", "1.70")));

            AppliedFeeSnapshot result = service.calculateTransferFee(
                new BigDecimal("1000.00"), LocalDate.now().plusDays(45)
            );

            assertThat(result.getFeeAmount()).isEqualByComparingTo("17.00");
        }

        // Verifica arredondamento HALF_EVEN em valores com casas decimais
        // Para R$ 100.33 no mesmo dia: 100.33 * 0.025 + 3.00 = 2.50825 + 3.00 = 5.51 (arredondado)
        @Test
        void deveArredondarTaxaCorretamente() {
            when(transferFeeRuleRepository.findApplicableRule(0))
                .thenReturn(Optional.of(buildRule(0, 0, "3.00", "2.50")));

            AppliedFeeSnapshot result = service.calculateTransferFee(
                new BigDecimal("100.33"), LocalDate.now()
            );

            assertThat(result.getFeeAmount()).isEqualByComparingTo("5.51");
        }
    }
}
