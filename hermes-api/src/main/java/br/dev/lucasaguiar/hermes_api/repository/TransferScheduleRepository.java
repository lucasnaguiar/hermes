package br.dev.lucasaguiar.hermes_api.repository;

import br.dev.lucasaguiar.hermes_api.domain.TransferSchedule;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransferScheduleRepository extends JpaRepository<TransferSchedule, UUID> {
    boolean existsByFingerprintAndCreatedAtAfter(String transferFingerprint, LocalDateTime createdAt);

    @EntityGraph(attributePaths = {"sourceAccount", "targetAccount"})
    @Query(
        "select ts from TransferSchedule ts " +
        "where (:sourceAccountNumber is null or ts.sourceAccount.accountNumber = :sourceAccountNumber) " +
        "and (:targetAccountNumber is null or ts.targetAccount.accountNumber = :targetAccountNumber)"
    )
    Page<TransferSchedule> findAllByFilters(
        @Param("sourceAccountNumber") String sourceAccountNumber,
        @Param("targetAccountNumber") String targetAccountNumber,
        Pageable pageable
    );
}
