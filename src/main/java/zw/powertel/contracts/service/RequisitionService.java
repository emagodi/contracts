package zw.powertel.contracts.service;

import org.springframework.web.multipart.MultipartFile;
import zw.powertel.contracts.enums.RequisitionStatus;
import zw.powertel.contracts.payload.request.ApprovalRequest;
import zw.powertel.contracts.payload.request.RequisitionRequest;
import zw.powertel.contracts.payload.response.ApprovalResponse;
import zw.powertel.contracts.payload.response.RequisitionResponse;

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
