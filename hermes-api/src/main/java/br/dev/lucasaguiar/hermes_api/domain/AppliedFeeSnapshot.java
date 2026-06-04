package br.dev.lucasaguiar.hermes_api.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AppliedFeeSnapshot {

    @Column(name = "fee_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal feeAmount;

    @Column(name = "fee_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal feeRate;

    @Column(name = "fee_fixed_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal feeFixedAmount;

    @Column(name = "fee_days_range_from", nullable = false)
    private Integer feeDaysRangeFrom;

    @Column(name = "fee_days_range_to", nullable = false)
    private Integer feeDaysRangeTo;
}