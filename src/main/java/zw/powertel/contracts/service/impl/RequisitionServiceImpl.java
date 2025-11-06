package zw.powertel.contracts.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.powertel.contracts.entities.Approval;
import zw.powertel.contracts.entities.Requisition;
import zw.powertel.contracts.entities.User;
import zw.powertel.contracts.enums.IsRenewable;
import zw.powertel.contracts.enums.PaymentStatus;
import zw.powertel.contracts.enums.RequisitionStatus;
import zw.powertel.contracts.exception.NotFoundException;
import zw.powertel.contracts.payload.request.RequisitionRequest;
import zw.powertel.contracts.payload.response.RequisitionResponse;
import zw.powertel.contracts.payload.response.ApprovalResponse;
import zw.powertel.contracts.payload.request.ApprovalRequest;
import zw.powertel.contracts.repository.RequisitionRepository;
import zw.powertel.contracts.repository.ApprovalRepository;
import zw.powertel.contracts.service.RequisitionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
public class RequisitionServiceImpl implements RequisitionService {

    private final RequisitionRepository requisitionRepository;
    private final ApprovalRepository approvalRepository;

    @Override
    public RequisitionResponse createRequisition(RequisitionRequest request) {
        Requisition requisition = mapToEntity(request);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = (User) authentication.getPrincipal();
        String email = loggedInUser.getEmail();
        requisition.setCreatedBy(email);
        requisition.setUpdatedBy(email);
        Requisition saved = requisitionRepository.save(requisition);
        return mapToResponse(saved);
    }

    @Override
    public RequisitionResponse updateRequisition(Long id, RequisitionRequest request) {
        // --- Find existing requisition ---
        Requisition existingRequisition = requisitionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Requisition not found with id: " + id));

        // --- Map incoming request to a temporary Requisition entity ---
        Requisition updates = mapToEntity(request);

        // --- Copy non-null properties from updates to existing requisition ---
        copyNonNullProperties(updates, existingRequisition);

        // --- Save updated requisition ---
        Requisition updatedRequisition = requisitionRepository.save(existingRequisition);

        return mapToResponse(updatedRequisition);
    }

    @Override
    public RequisitionResponse getRequisition(Long id) {
        Requisition req = requisitionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Requisition not found with id: " + id));
        return mapToResponse(req);
    }

    @Override
    public List<RequisitionResponse> getAllRequisitions() {
        List<Requisition> requisitions = requisitionRepository.findAll();
        if (requisitions.isEmpty()) {
            throw new NotFoundException("No requisitions found");
        }
        return requisitions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRequisition(Long id) {
        if (!requisitionRepository.existsById(id)) {
            throw new NotFoundException("Requisition not found with id: " + id);
        }
        requisitionRepository.deleteById(id);
    }



    @Override
    public List<RequisitionResponse> getContractsDueForRenewal() {
        Date fiveDaysFromNow = Date.from(LocalDate.now()
                .plusDays(5)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());

        List<Requisition> dueContracts = requisitionRepository.findContractsDueForPayment(
                IsRenewable.YES,
                RequisitionStatus.CONTRACT,
                fiveDaysFromNow
        );

        if (dueContracts.isEmpty()) {
            throw new NotFoundException("No renewable contracts due for payment within 5 days.");
        }

        return dueContracts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Long countContractsDueForRenewal() {
        Date fiveDaysFromNow = Date.from(LocalDate.now()
                .plusDays(5)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());

        return requisitionRepository.countContractsDueForPayment(
                IsRenewable.YES,
                RequisitionStatus.CONTRACT,
                fiveDaysFromNow
        );
    }


    @Override
    public List<RequisitionResponse> getContractsDueForPayment() {
        Date thirtyDaysFromNow = Date.from(LocalDate.now()
                .plusDays(30)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());

        List<Requisition> dueContracts = requisitionRepository.findContractsDueForPayment(
                RequisitionStatus.CONTRACT,
                PaymentStatus.DEPOSIT_DUE,
                thirtyDaysFromNow
        );

        if (dueContracts.isEmpty()) {
            throw new NotFoundException("No contracts found that are due for payment within the next 30 days.");
        }

        return dueContracts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Long countContractsDueForPayment() {
        Date thirtyDaysFromNow = Date.from(LocalDate.now()
                .plusDays(30)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());

        return requisitionRepository.countContractsDueForPayment(
                RequisitionStatus.CONTRACT,
                PaymentStatus.DEPOSIT_DUE,
                thirtyDaysFromNow
        );
    }

    @Override
    public Long countRequisitionsByStatus(RequisitionStatus status) {
        return requisitionRepository.countByRequisitionStatus(status);
    }


    // --------------------------------------------------------------------------------------------
    // Utility: Copy only non-null properties
    // --------------------------------------------------------------------------------------------
    private void copyNonNullProperties(Object source, Object target) {
        if (source == null || target == null) return;

        BeanWrapper src = new BeanWrapperImpl(source);
        BeanWrapper trg = new BeanWrapperImpl(target);

        for (PropertyDescriptor propertyDescriptor : src.getPropertyDescriptors()) {
            String propertyName = propertyDescriptor.getName();

            // skip Spring/Java internals and id - we don't want to overwrite identity
            if ("class".equals(propertyName) || "id".equals(propertyName)) continue;

            Object providedValue = src.getPropertyValue(propertyName);
            if (providedValue != null) {
                trg.setPropertyValue(propertyName, providedValue);
            }
        }
    }

    // --------------------------------------------------------------------------------------------
    // Mapping helpers for Requisition <-> RequisitionRequest/Response
    // --------------------------------------------------------------------------------------------
    private Requisition mapToEntity(RequisitionRequest req) {
        if (req == null) return null;

        Requisition r = Requisition.builder()
                .requisitionTo(req.getRequisitionTo())
                .requisitionFrom(req.getRequisitionFrom())
                .description(req.getDescription())
                .vendorRegistedName(req.getVendorRegistedName())
                .vendorEmail(req.getVendorEmail())
                .vendorPhoneNumber(req.getVendorPhoneNumber())
                .vendorTradingName(req.getVendorTradingName())
                .vendorAddress(req.getVendorAddress())
                .vendorContactPerson(req.getVendorContactPerson())
                .contactNumber(req.getContactNumber())
                .contactPersonCapacity(req.getContactPersonCapacity())
                .justification(req.getJustification())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .durationDays(req.getDurationDays())
                .durationWeeks(req.getDurationWeeks())
                .durationMonths(req.getDurationMonths())
                .durationYears(req.getDurationYears())
                .isRenewable(req.getIsRenewable())
                .renewalDays(req.getRenewalDays())
                .renewalWeeks(req.getRenewalWeeks())
                .renewalMonths(req.getRenewalMonths())
                .renewalYears(req.getRenewalYears())
                .contractPrice(req.getContractPrice())
                .vat(req.getVat())
                .totalContractPrice(req.getTotalContractPrice())
                .totalOnsignature(req.getTotalOnsignature())
                .downPayment(req.getDownPayment())
                .balancePayment(req.getBalancePayment())
                .deliveryDays(req.getDeliveryDays())
                .deliveryWeeks(req.getDeliveryWeeks())
                .deliveryMonths(req.getDeliveryMonths())
                .deliveryNA(req.getDeliveryNA())
                .penalties(req.getPenalties())
                .acceptanceConditions(req.getAcceptanceConditions())
                .warrantyDays(req.getWarrantyDays())
                .warrantyWeeks(req.getWarrantyWeeks())
                .warrantyMonths(req.getWarrantyMonths())
                .warrantyNA(req.getWarrantyNA())
                .serviceSupport(req.getServiceSupport())
                .specialIssues(req.getSpecialIssues())
                .fundingAvailable(req.getFundingAvailable())
                .procurementComplied(req.getProcurementComplied())
                .financeDirector(req.getFinanceDirector())
                .financeDate(req.getFinanceDate())
                .procurementManager(req.getProcurementManager())
                .procurementDate(req.getProcurementDate())
                .headOfDept(req.getHeadOfDept())
                .headDate(req.getHeadDate())
                .companySecretary(req.getCompanySecretary())
                .secretaryDate(req.getSecretaryDate())
                .requisitionStatus(req.getRequisitionStatus())
                .paymentStatus(req.getPaymentStatus())

                .build();

        if (req.getApproval() != null) {
            r.setApproval(mapApprovalToEntity(req.getApproval()));
        }

        return r;
    }


    @Override
    public List<RequisitionResponse> getRequisitionsByCreator(String createdBy) {
        List<Requisition> requisitions = requisitionRepository.findByCreatedBy(createdBy);
        if (requisitions.isEmpty()) {
            throw new NotFoundException("No requisitions found for user: " + createdBy);
        }
        return requisitions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequisitionResponse> getRequisitionsByStatus(RequisitionStatus status) {
        List<Requisition> requisitions = requisitionRepository.findByRequisitionStatus(status);
        if (requisitions.isEmpty()) {
            throw new NotFoundException("No requisitions found with status: " + status);
        }
        return requisitions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ApprovalResponse getApprovalByRequisitionId(Long requisitionId) {
        Requisition requisition = requisitionRepository.findById(requisitionId)
                .orElseThrow(() -> new EntityNotFoundException("Requisition not found with id: " + requisitionId));

        Approval approval = requisition.getApproval();
        if (approval == null) {
            throw new EntityNotFoundException("Approval not found for requisition with id: " + requisitionId);
        }

        // Map Approval to ApprovalResponse
        ApprovalResponse approvalResponse = ApprovalResponse.builder()
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

        return approvalResponse;
    }


    private RequisitionResponse mapToResponse(Requisition req) {
        if (req == null) return null;

        RequisitionResponse response = RequisitionResponse.builder()
                .id(req.getId())
                .requisitionTo(req.getRequisitionTo())
                .requisitionFrom(req.getRequisitionFrom())
                .description(req.getDescription())
                .vendorRegistedName(req.getVendorRegistedName())
                .vendorEmail(req.getVendorEmail())
                .vendorPhoneNumber(req.getVendorPhoneNumber())
                .vendorTradingName(req.getVendorTradingName())
                .vendorAddress(req.getVendorAddress())
                .vendorContactPerson(req.getVendorContactPerson())
                .contactNumber(req.getContactNumber())
                .contactPersonCapacity(req.getContactPersonCapacity())
                .justification(req.getJustification())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .durationDays(req.getDurationDays())
                .durationWeeks(req.getDurationWeeks())
                .durationMonths(req.getDurationMonths())
                .durationYears(req.getDurationYears())
                .isRenewable(req.getIsRenewable())
                .renewalDays(req.getRenewalDays())
                .renewalWeeks(req.getRenewalWeeks())
                .renewalMonths(req.getRenewalMonths())
                .renewalYears(req.getRenewalYears())
                .contractPrice(req.getContractPrice())
                .vat(req.getVat())
                .totalContractPrice(req.getTotalContractPrice())
                .totalOnsignature(req.getTotalOnsignature())
                .downPayment(req.getDownPayment())
                .balancePayment(req.getBalancePayment())
                .deliveryDays(req.getDeliveryDays())
                .deliveryWeeks(req.getDeliveryWeeks())
                .deliveryMonths(req.getDeliveryMonths())
                .deliveryNA(req.getDeliveryNA())
                .penalties(req.getPenalties())
                .acceptanceConditions(req.getAcceptanceConditions())
                .warrantyDays(req.getWarrantyDays())
                .warrantyWeeks(req.getWarrantyWeeks())
                .warrantyMonths(req.getWarrantyMonths())
                .warrantyNA(req.getWarrantyNA())
                .serviceSupport(req.getServiceSupport())
                .specialIssues(req.getSpecialIssues())
                .fundingAvailable(req.getFundingAvailable())
                .procurementComplied(req.getProcurementComplied())
                .financeDirector(req.getFinanceDirector())
                .financeDate(req.getFinanceDate())
                .procurementManager(req.getProcurementManager())
                .procurementDate(req.getProcurementDate())
                .headOfDept(req.getHeadOfDept())
                .headDate(req.getHeadDate())
                .companySecretary(req.getCompanySecretary())
                .secretaryDate(req.getSecretaryDate())
                .requisitionStatus(req.getRequisitionStatus())
                .paymentStatus(req.getPaymentStatus())
                .createdAt(req.getCreatedAt())
                .createdBy(req.getCreatedBy())
                .updatedAt(req.getUpdatedAt())
                .updatedBy(req.getUpdatedBy())
                .build();

        if (req.getApproval() != null) {
            response.setApproval(mapApprovalToResponse(req.getApproval()));
        }

        return response;
    }

    @Override
    public ApprovalResponse addApprovalToRequisition(Long requisitionId, ApprovalRequest approvalRequest) {
        // --- Find existing requisition ---
        Requisition existingRequisition = requisitionRepository.findById(requisitionId)
                .orElseThrow(() -> new NotFoundException("Requisition not found with id: " + requisitionId));

        Approval existingApproval = existingRequisition.getApproval();

        // If an approval already exists, update it; otherwise create a new one
        if (existingApproval != null) {
            // Update existing approval with non-null properties
            Approval updates = mapApprovalToEntity(approvalRequest);
            copyNonNullProperties(updates, existingApproval);
            Approval updatedApproval = approvalRepository.save(existingApproval); // Save updated approval
            return mapApprovalToResponse(updatedApproval); // Return updated approval details
        } else {
            // Map incoming ApprovalRequest to Approval entity
            Approval newApproval = mapApprovalToEntity(approvalRequest);
            // Save the new approval
            Approval savedApproval = approvalRepository.save(newApproval);
            // Update the requisition to link to the saved approval
            existingRequisition.setApproval(savedApproval);
            requisitionRepository.save(existingRequisition); // Save the updated requisition
            return mapApprovalToResponse(savedApproval); // Return new approval details
        }
    }

    // --------------------------------------------------------------------------------------------
    // Approval Mappers
    // --------------------------------------------------------------------------------------------
    private Approval mapApprovalToEntity(ApprovalRequest request) {
        if (request == null) return null;

        return Approval.builder()
                .approvalTo(request.getApprovalTo())
                .approvalDate(request.getApprovalDate())
                .approvalReference(request.getApprovalReference())
                .legalSignature(request.getLegalSignature())
                .legalSignatureDate(request.getLegalSignatureDate())
                .legalComments(request.getLegalComments())
                .technicalSignature(request.getTechnicalSignature())
                .technicalSignatureDate(request.getTechnicalSignatureDate())
                .technicalComments(request.getTechnicalComments())
                .financialSignature(request.getFinancialSignature())
                .financialSignatureDate(request.getFinancialSignatureDate())
                .financialComments(request.getFinancialComments())
                .commercialSignature(request.getCommercialSignature())
                .commercialSignatureDate(request.getCommercialSignatureDate())
                .commercialComments(request.getCommercialComments())
                .businessDevelopmentSignature(request.getBusinessDevelopmentSignature())
                .businessDevelopmentSignatureDate(request.getBusinessDevelopmentSignatureDate())
                .businessDevelopmentComments(request.getBusinessDevelopmentComments())
                .procurementSignature(request.getProcurementSignature())
                .procurementSignatureDate(request.getProcurementSignatureDate())
                .procurementComments(request.getProcurementComments())
                .approvalStatus(request.getApprovalStatus())
                .build();
    }

    private ApprovalResponse mapApprovalToResponse(Approval approval) {
        if (approval == null) return null;

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

    @Override
    public Map<String, Long> getRequisitionSummaryByStatus() {
        List<Object[]> results = requisitionRepository.countRequisitionsGroupedByStatus();

        Map<String, Long> summary = new HashMap<>();
        for (Object[] row : results) {
            RequisitionStatus status = (RequisitionStatus) row[0];
            Long count = (Long) row[1];
            summary.put(status.name(), count);
        }

        return summary;
    }

}