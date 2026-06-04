package br.dev.lucasaguiar.hermes_api.service;

import br.dev.lucasaguiar.hermes_api.exception.AccountNotFoundException;
import br.dev.lucasaguiar.hermes_api.repository.AccountRepository;
import br.dev.lucasaguiar.hermes_api.dto.response.AccountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    AccountRepository accountRepository;

    public AccountResponse findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .map(AccountResponse::from)
            .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }
}
