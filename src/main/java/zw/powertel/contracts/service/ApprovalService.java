package zw.powertel.contracts.service;

import zw.powertel.contracts.enums.ApprovalStatus;
import zw.powertel.contracts.payload.request.ApprovalRequest;
import zw.powertel.contracts.payload.response.ApprovalResponse;

import java.util.List;

public interface ApprovalService {

    ApprovalResponse createApproval(ApprovalRequest request);

    ApprovalResponse updateApproval(Long id, ApprovalRequest request);

    ApprovalResponse getApproval(Long id);

    List<ApprovalResponse> getAllApprovals();

    void deleteApproval(Long id);

    List<ApprovalResponse> getApprovalsByStatus(ApprovalStatus status);
}
