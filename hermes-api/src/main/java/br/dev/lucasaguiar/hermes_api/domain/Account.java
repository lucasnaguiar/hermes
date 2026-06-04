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
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "account_number", nullable = false, unique = true, length = 10)
    private String accountNumber;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "committed_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal committedBalance;

    public BigDecimal getAvailableBalance() {
        return balance.subtract(committedBalance);
    }
}