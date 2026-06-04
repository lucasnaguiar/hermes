package br.dev.lucasaguiar.hermes_api.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "transfer_schedule")
public class TransferSchedule {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id", nullable = false)
    private Account sourceAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_account_id", nullable = false)
    private Account targetAccount;

    @Column(name = "transfer_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal transferAmount;

    @Column(name = "transfer_date", nullable = false)
    private LocalDate transferDate;

    @Column(name = "scheduling_date", nullable = false)
    private LocalDate schedulingDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransferStatus status;

    @Column(nullable = false, length = 64, unique = true)
    private String fingerprint;

    @Embedded
    private AppliedFeeSnapshot appliedFee;
}