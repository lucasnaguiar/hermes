package br.dev.lucasaguiar.hermes_api.dto.response;

import br.dev.lucasaguiar.hermes_api.domain.Account;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class AccountResponse {
    private UUID id;
    private String accountNumber;
    private BigDecimal balance;
    private BigDecimal committedBalance;
    private BigDecimal availableBalance;

    public static AccountResponse from(Account account) {
        AccountResponse response = new AccountResponse();
        response.id = account.getId();
        response.accountNumber = account.getAccountNumber();
        response.balance = account.getBalance();
        response.committedBalance = account.getCommittedBalance();
        response.availableBalance = account.getAvailableBalance();
        return response;
    }
}