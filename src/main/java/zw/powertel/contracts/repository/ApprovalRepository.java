package zw.powertel.contracts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.powertel.contracts.entities.Approval;
import zw.powertel.contracts.enums.ApprovalStatus;

import java.util.List;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    List<Approval> findByApprovalStatus(ApprovalStatus status);
}
