package br.dev.lucasaguiar.hermes_api.service;

import br.dev.lucasaguiar.hermes_api.domain.AppliedFeeSnapshot;
import br.dev.lucasaguiar.hermes_api.domain.TransferFeeRule;
import br.dev.lucasaguiar.hermes_api.exception.NoApplicableFeeRuleException;
import br.dev.lucasaguiar.hermes_api.repository.TransferFeeRuleRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;

@Service
public class FeeCalculationService {

    private final TransferFeeRuleRepository transferFeeRuleRepository;

    public FeeCalculationService(TransferFeeRuleRepository transferFeeRuleRepository) {
        this.transferFeeRuleRepository = transferFeeRuleRepository;
    }

    public AppliedFeeSnapshot calculateTransferFee(BigDecimal transferAmount, LocalDate transferDate) {
        int days = (int) ChronoUnit.DAYS.between(LocalDate.now(), transferDate);

        TransferFeeRule rule = transferFeeRuleRepository.findApplicableRule(days).orElseThrow(() -> new NoApplicableFeeRuleException(days));

        BigDecimal feeAmount = transferAmount
            .multiply(rule.getRate().divide(BigDecimal.valueOf(100)))
            .add(rule.getFixedAmount())
            .setScale(2, RoundingMode.HALF_EVEN);

        return new AppliedFeeSnapshot(
            feeAmount,
            rule.getRate(),
            rule.getFixedAmount(),
            rule.getDaysRangeFrom(),
            rule.getDaysRangeTo()
        );
    }
}
