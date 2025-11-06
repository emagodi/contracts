package zw.powertel.contracts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.powertel.contracts.entities.ContractDraft;

import java.util.Optional;

public interface ContractDraftRepository extends JpaRepository<ContractDraft, Long> {
    Optional<ContractDraft> findByRequisitionId(Long requisitionId);
}
