package br.dev.lucasaguiar.hermes_api.repository;

import br.dev.lucasaguiar.hermes_api.domain.TransferFeeRule;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferFeeRuleRepository extends JpaRepository<TransferFeeRule, UUID> {
    @Query("SELECT r FROM TransferFeeRule r WHERE :days BETWEEN r.daysRangeFrom AND r.daysRangeTo")
    Optional<TransferFeeRule> findApplicableRule(int days);
}
