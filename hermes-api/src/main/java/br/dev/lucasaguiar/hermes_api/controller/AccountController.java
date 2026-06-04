package br.dev.lucasaguiar.hermes_api.controller;

import br.dev.lucasaguiar.hermes_api.dto.response.AccountResponse;
import br.dev.lucasaguiar.hermes_api.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> find(
        @PathVariable(required = false) String accountNumber) {
        return ResponseEntity.ok(accountService.findByAccountNumber(accountNumber));
    }
}