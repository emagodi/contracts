package zw.powertel.contracts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zw.powertel.contracts.entities.Requisition;
import zw.powertel.contracts.enums.IsRenewable;
import zw.powertel.contracts.enums.PaymentStatus;
import zw.powertel.contracts.enums.RequisitionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RequisitionRepository extends JpaRepository<Requisition, Long> {

    List<Requisition> findByCreatedBy(String createdBy);
    List<Requisition> findByRequisitionStatus(RequisitionStatus status);
    Long countByRequisitionStatus(RequisitionStatus status);

    // List of requisitions by status, creator, and date range
    List<Requisition> findByRequisitionStatusAndCreatedByAndCreatedAtBetween(RequisitionStatus status, String createdBy, LocalDateTime startDate, LocalDateTime endDate);

    // Summary of requisitions by status, creator, and date range
    // Custom query to count requisitions group by status
    @Query("SELECT r.requisitionStatus, COUNT(r) FROM Requisition r WHERE r.createdBy = ?1 AND r.createdAt BETWEEN ?2 AND ?3 GROUP BY r.requisitionStatus")
    List<Object[]> countByCreatedByAndCreatedAtBetweenGroupByStatus(String createdBy, LocalDateTime startDate, LocalDateTime endDate);

    // List contracts due for renewal (renewable + fully approved/converted)
    @Query("""
        SELECT r
        FROM Requisition r
        WHERE r.isRenewable = :isRenewable
          AND r.requisitionStatus = :status
          AND r.endDate BETWEEN CURRENT_DATE AND :fiveDaysFromNow
    """)
    List<Requisition> findContractsDueForPayment(IsRenewable isRenewable,
                                                 RequisitionStatus status,
                                                 LocalDate fiveDaysFromNow);

    @Query("""
        SELECT COUNT(r)
        FROM Requisition r
        WHERE r.isRenewable = :isRenewable
          AND r.requisitionStatus = :status
          AND r.endDate BETWEEN CURRENT_DATE AND :fiveDaysFromNow
    """)
    Long countContractsDueForPayment(IsRenewable isRenewable,
                                     RequisitionStatus status,
                                     LocalDate fiveDaysFromNow);

    // List contracts due for payment within the next 30 days
    @Query("""
        SELECT r
        FROM Requisition r
        WHERE r.requisitionStatus = :status
          AND r.paymentStatus = :paymentStatus
          AND r.startDate BETWEEN CURRENT_DATE AND :thirtyDaysFromNow
    """)
    List<Requisition> findContractsDueForPayment(
            RequisitionStatus status,
            PaymentStatus paymentStatus,
            LocalDate thirtyDaysFromNow);

    @Query("""
        SELECT COUNT(r)
        FROM Requisition r
        WHERE r.requisitionStatus = :status
          AND r.paymentStatus = :paymentStatus
          AND r.startDate BETWEEN CURRENT_DATE AND :thirtyDaysFromNow
    """)
    Long countContractsDueForPayment(
            RequisitionStatus status,
            PaymentStatus paymentStatus,
            LocalDate thirtyDaysFromNow);

    // Count grouped by status
    @Query("SELECT r.requisitionStatus AS status, COUNT(r) AS count " +
            "FROM Requisition r " +
            "GROUP BY r.requisitionStatus")
    List<Object[]> countRequisitionsGroupedByStatus();


    @Query("""
    SELECT r
    FROM Requisition r
    WHERE r.approval IS NULL AND r.requisitionStatus = 'COMPANYSECRETARY_APPROVED'
""")
    List<Requisition> findByApprovalIsNullAndStatusApproved();


    @Query("SELECT r FROM Requisition r " +
            "WHERE r.requisitionStatus = :status " +
            "AND r.isRenewable = :renewable " +
            "AND r.endDate BETWEEN :today AND :sevenDays")
    List<Requisition> findRenewableContractsExpiringSoon(
            @Param("status") RequisitionStatus status,
            @Param("renewable") IsRenewable renewable,
            @Param("today") LocalDate today,
            @Param("sevenDays") LocalDate sevenDays
    );


}
