package zw.powertel.contracts.service;


import org.springframework.web.multipart.MultipartFile;
import zw.powertel.contracts.entities.Approval;
import zw.powertel.contracts.enums.RequisitionStatus;
import zw.powertel.contracts.payload.request.ApprovalRequest;
import zw.powertel.contracts.payload.request.RequisitionRequest;
import zw.powertel.contracts.payload.response.ApprovalResponse;
import zw.powertel.contracts.payload.response.RequisitionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface RequisitionService {
    RequisitionResponse createRequisition(RequisitionRequest request);
    RequisitionResponse updateRequisition(Long id, RequisitionRequest request);
    RequisitionResponse getRequisition(Long id);
    List<RequisitionResponse> getAllRequisitions();
    void deleteRequisition(Long id);

    ApprovalResponse addApprovalToRequisition(Long requisitionId, ApprovalRequest approvalRequest);

    public List<RequisitionResponse> getRequisitionsByCreator(String createdBy);

    public List<RequisitionResponse> getRequisitionsByStatus(RequisitionStatus status);

    public List<RequisitionResponse> getContractsDueForRenewal();

    public Long countContractsDueForRenewal();

    public List<RequisitionResponse> getContractsDueForPayment();

    public Long countContractsDueForPayment();

    public ApprovalResponse getApprovalByRequisitionId(Long requisitionId);

    public Map<String, Long> getRequisitionSummaryByStatus();

    Long countRequisitionsByStatus(RequisitionStatus status);

    public String uploadFiles(Long requisitionId, MultipartFile[] files);
}
