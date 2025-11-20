package zw.powertel.contracts.service;

import org.springframework.web.multipart.MultipartFile;
import zw.powertel.contracts.enums.RequisitionStatus;
import zw.powertel.contracts.payload.request.ApprovalRequest;
import zw.powertel.contracts.payload.request.RequisitionRequest;
import zw.powertel.contracts.payload.response.ApprovalResponse;
import zw.powertel.contracts.payload.response.RequisitionResponse;
import zw.powertel.contracts.payload.response.StatusSummaryResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface RequisitionService {

    // ------------------------ CRUD ------------------------
    RequisitionResponse createRequisition(RequisitionRequest request);
    RequisitionResponse updateRequisition(Long id, RequisitionRequest request);
    RequisitionResponse getRequisition(Long id);
    List<RequisitionResponse> getAllRequisitions();
    void deleteRequisition(Long id);

    // ------------------------ APPROVAL ------------------------
    ApprovalResponse addApprovalToRequisition(Long requisitionId, ApprovalRequest approvalRequest);
    ApprovalResponse getApprovalByRequisitionId(Long requisitionId);

    // ------------------------ FILTERS ------------------------
    List<RequisitionResponse> getRequisitionsByCreator(String createdBy);
    List<RequisitionResponse> getRequisitionsByStatus(RequisitionStatus status);
    List<RequisitionResponse> getRequisitionsWithoutApproval();
    List<RequisitionResponse> getRequisitionsByFilters(RequisitionStatus status, String createdBy, LocalDateTime startDate, LocalDateTime endDate);

    // Summary of requisitions by status, creator, and date range
    List<StatusSummaryResponse> getStatusSummary(String createdBy, LocalDateTime startDate, LocalDateTime endDate);

    // ------------------------ CONTRACTS ------------------------
    List<RequisitionResponse> getContractsDueForRenewal();
    Long countContractsDueForRenewal();
    List<RequisitionResponse> getContractsDueForPayment();
    Long countContractsDueForPayment();

    // ------------------------ SUMMARY ------------------------
    Map<String, Long> getRequisitionSummaryByStatus();
    Long countRequisitionsByStatus(RequisitionStatus status);

    // ------------------------ FILE UPLOAD ------------------------
    String uploadFiles(Long requisitionId, MultipartFile[] files);
}
