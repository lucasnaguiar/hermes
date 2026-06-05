package br.dev.lucasaguiar.hermes_api.repository;

import br.dev.lucasaguiar.hermes_api.domain.Account;
import br.dev.lucasaguiar.hermes_api.domain.TransferSchedule;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferScheduleRepository extends JpaRepository<TransferSchedule, UUID> {
    boolean existsByFingerprint(String transferFingerprint);
}
