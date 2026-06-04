package br.dev.lucasaguiar.hermes_api.repository;

import br.dev.lucasaguiar.hermes_api.domain.Account;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByAccountNumber(String accountNumber);
}
