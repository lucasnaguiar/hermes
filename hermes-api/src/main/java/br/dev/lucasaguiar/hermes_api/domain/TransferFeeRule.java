package br.dev.lucasaguiar.hermes_api.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "transfer_fee_rule")
public class TransferFeeRule {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "days_range_from", nullable = false)
    private Integer daysRangeFrom;

    @Column(name = "days_range_to", nullable = false)
    private Integer daysRangeTo;

    @Column(name = "fixed_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal fixedAmount;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal rate;
}