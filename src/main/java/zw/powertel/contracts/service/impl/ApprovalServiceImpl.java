package zw.powertel.contracts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.powertel.contracts.entities.Approval;
import zw.powertel.contracts.enums.ApprovalStatus;
import zw.powertel.contracts.exception.NotFoundException;
import zw.powertel.contracts.payload.request.ApprovalRequest;
import zw.powertel.contracts.payload.response.ApprovalResponse;
import zw.powertel.contracts.repository.ApprovalRepository;
import zw.powertel.contracts.service.ApprovalService;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRepository approvalRepository;

    @Override
    public ApprovalResponse createApproval(ApprovalRequest request) {
        Approval approval = mapToEntity(request);
        Approval saved = approvalRepository.save(approval);
        return mapToResponse(saved);
    }

    @Override
    public ApprovalResponse updateApproval(Long id, ApprovalRequest request) {
        Approval existing = approvalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Approval not found with id: " + id));

        Approval updates = mapToEntity(request);
        copyNonNullProperties(updates, existing);

        Approval updated = approvalRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public ApprovalResponse getApproval(Long id) {
        Approval approval = approvalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Approval not found with id: " + id));
        return mapToResponse(approval);
    }

    @Override
    public List<ApprovalResponse> getAllApprovals() {
        List<Approval> approvals = approvalRepository.findAll();
        if (approvals.isEmpty()) {
            throw new NotFoundException("No approvals found");
        }
        return approvals.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteApproval(Long id) {
        if (!approvalRepository.existsById(id)) {
            throw new NotFoundException("Approval not found with id: " + id);
        }
        approvalRepository.deleteById(id);
    }

    @Override
    public List<ApprovalResponse> getApprovalsByStatus(ApprovalStatus status) {
        List<Approval> approvals = approvalRepository.findByApprovalStatus(status);
        if (approvals.isEmpty()) {
            throw new NotFoundException("No approvals found with status: " + status);
        }
        return approvals.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // --------------------------------------------------------------------------------------------
    // Utility: Copy only non-null properties (works for wrapper types)
    // --------------------------------------------------------------------------------------------
    private void copyNonNullProperties(Object source, Object target) {
        BeanWrapper src = new BeanWrapperImpl(source);
        BeanWrapper trg = new BeanWrapperImpl(target);

        for (PropertyDescriptor pd : src.getPropertyDescriptors()) {
            String propName = pd.getName();
            if ("class".equals(propName) || "id".equals(propName)) continue;

            Object providedValue = src.getPropertyValue(propName);
            if (providedValue != null) {
                trg.setPropertyValue(propName, providedValue);
            }
        }
    }

    // --------------------------------------------------------------------------------------------
    // Mapping helpers
    // --------------------------------------------------------------------------------------------
    private Approval mapToEntity(ApprovalRequest req) {
        return Approval.builder()
                .approvalTo(req.getApprovalTo())
                .approvalDate(req.getApprovalDate())
                .approvalReference(req.getApprovalReference())
                .legalSignature(req.getLegalSignature())
                .legalSignatureDate(req.getLegalSignatureDate())
                .legalComments(req.getLegalComments())
                .technicalSignature(req.getTechnicalSignature())
                .technicalSignatureDate(req.getTechnicalSignatureDate())
                .technicalComments(req.getTechnicalComments())
                .financialSignature(req.getFinancialSignature())
                .financialSignatureDate(req.getFinancialSignatureDate())
                .financialComments(req.getFinancialComments())
                .commercialSignature(req.getCommercialSignature())
                .commercialSignatureDate(req.getCommercialSignatureDate())
                .commercialComments(req.getCommercialComments())
                .businessDevelopmentSignature(req.getBusinessDevelopmentSignature())
                .businessDevelopmentSignatureDate(req.getBusinessDevelopmentSignatureDate())
                .businessDevelopmentComments(req.getBusinessDevelopmentComments())
                .procurementSignature(req.getProcurementSignature())
                .procurementSignatureDate(req.getProcurementSignatureDate())
                .procurementComments(req.getProcurementComments())
                .approvalStatus(req.getApprovalStatus())
                .build();
    }

    private ApprovalResponse mapToResponse(Approval approval) {
        return ApprovalResponse.builder()
                .id(approval.getId())
                .approvalTo(approval.getApprovalTo())
                .approvalDate(approval.getApprovalDate())
                .approvalReference(approval.getApprovalReference())
                .legalSignature(approval.getLegalSignature())
                .legalSignatureDate(approval.getLegalSignatureDate())
                .legalComments(approval.getLegalComments())
                .technicalSignature(approval.getTechnicalSignature())
                .technicalSignatureDate(approval.getTechnicalSignatureDate())
                .technicalComments(approval.getTechnicalComments())
                .financialSignature(approval.getFinancialSignature())
                .financialSignatureDate(approval.getFinancialSignatureDate())
                .financialComments(approval.getFinancialComments())
                .commercialSignature(approval.getCommercialSignature())
                .commercialSignatureDate(approval.getCommercialSignatureDate())
                .commercialComments(approval.getCommercialComments())
                .businessDevelopmentSignature(approval.getBusinessDevelopmentSignature())
                .businessDevelopmentSignatureDate(approval.getBusinessDevelopmentSignatureDate())
                .businessDevelopmentComments(approval.getBusinessDevelopmentComments())
                .procurementSignature(approval.getProcurementSignature())
                .procurementSignatureDate(approval.getProcurementSignatureDate())
                .procurementComments(approval.getProcurementComments())
                .approvalStatus(approval.getApprovalStatus())
                .createdAt(approval.getCreatedAt())
                .createdBy(approval.getCreatedBy())
                .updatedAt(approval.getUpdatedAt())
                .updatedBy(approval.getUpdatedBy())
                .build();
    }
}

