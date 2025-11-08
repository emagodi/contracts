package zw.powertel.contracts.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import zw.powertel.contracts.entities.Approval;
import zw.powertel.contracts.entities.Attachment;
import zw.powertel.contracts.entities.Requisition;
import zw.powertel.contracts.entities.User;
import zw.powertel.contracts.enums.IsRenewable;
import zw.powertel.contracts.enums.PaymentStatus;
import zw.powertel.contracts.enums.RequisitionStatus;
import zw.powertel.contracts.exception.NotFoundException;
import zw.powertel.contracts.payload.request.ApprovalRequest;
import zw.powertel.contracts.payload.request.RequisitionRequest;
import zw.powertel.contracts.payload.response.ApprovalResponse;
import zw.powertel.contracts.payload.response.RequisitionResponse;
import zw.powertel.contracts.repository.ApprovalRepository;
import zw.powertel.contracts.repository.RequisitionRepository;
import zw.powertel.contracts.service.RequisitionService;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RequisitionServiceImpl implements RequisitionService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final RequisitionRepository requisitionRepository;
    private final ApprovalRepository approvalRepository;

    // ------------------------ CRUD ------------------------
    @Override
    public RequisitionResponse createRequisition(RequisitionRequest request) {
        log.info("Creating requisition with request: {}", request);
        Requisition requisition = mapToEntity(request);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = (User) authentication.getPrincipal();
        String email = loggedInUser.getEmail();
        log.info("Setting createdBy and updatedBy to logged-in user: {}", email);

        requisition.setCreatedBy(email);
        requisition.setUpdatedBy(email);

        Requisition saved = requisitionRepository.save(requisition);
        log.info("Requisition saved with ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Override
    public RequisitionResponse updateRequisition(Long id, RequisitionRequest request) {
        log.info("Updating requisition ID: {} with data: {}", id, request);
        Requisition existing = requisitionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Requisition not found with id: " + id));

        Requisition updates = mapToEntity(request);
        copyNonNullProperties(updates, existing);

        Requisition updated = requisitionRepository.save(existing);
        log.info("Requisition updated with ID: {}", updated.getId());
        return mapToResponse(updated);
    }

    @Override
    public RequisitionResponse getRequisition(Long id) {
        log.info("Fetching requisition with ID: {}", id);
        Requisition requisition = requisitionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Requisition not found with id: " + id));
        log.info("Found requisition: {}", requisition);
        return mapToResponse(requisition);
    }

    @Override
    public List<RequisitionResponse> getAllRequisitions() {
        log.info("Fetching all requisitions");
        List<Requisition> requisitions = requisitionRepository.findAll();
        if (requisitions.isEmpty()) {
            log.warn("No requisitions found");
            throw new NotFoundException("No requisitions found");
        }
        log.info("Found {} requisitions", requisitions.size());
        return requisitions.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public void deleteRequisition(Long id) {
        log.info("Deleting requisition with ID: {}", id);
        if (!requisitionRepository.existsById(id)) {
            log.warn("Requisition not found with ID: {}", id);
            throw new NotFoundException("Requisition not found with id: " + id);
        }
        requisitionRepository.deleteById(id);
        log.info("Requisition deleted with ID: {}", id);
    }

    // ------------------------ FILTERS ------------------------
    @Override
    public List<RequisitionResponse> getRequisitionsByCreator(String createdBy) {
        log.info("Fetching requisitions created by: {}", createdBy);
        List<Requisition> requisitions = requisitionRepository.findByCreatedBy(createdBy);
        if (requisitions.isEmpty()) {
            log.warn("No requisitions found for user: {}", createdBy);
            throw new NotFoundException("No requisitions found for user: " + createdBy);
        }
        return requisitions.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<RequisitionResponse> getRequisitionsByStatus(RequisitionStatus status) {
        log.info("Fetching requisitions with status: {}", status);
        List<Requisition> requisitions = requisitionRepository.findByRequisitionStatus(status);
        if (requisitions.isEmpty()) {
            log.warn("No requisitions found with status: {}", status);
            throw new NotFoundException("No requisitions found with status: " + status);
        }
        return requisitions.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public Long countRequisitionsByStatus(RequisitionStatus status) {
        log.info("Counting requisitions with status: {}", status);
        Long count = requisitionRepository.countByRequisitionStatus(status);
        log.info("Found {} requisitions with status: {}", count, status);
        return count;
    }

    // ------------------------ CONTRACTS ------------------------
    @Override
    public List<RequisitionResponse> getContractsDueForRenewal() {
        LocalDate fiveDaysFromNow = LocalDate.now().plusDays(5);
        log.info("Fetching renewable contracts due within 5 days, up to: {}", fiveDaysFromNow);

        List<Requisition> dueContracts = requisitionRepository.findContractsDueForPayment(
                IsRenewable.YES, RequisitionStatus.CONTRACT, fiveDaysFromNow
        );

        if (dueContracts.isEmpty()) {
            log.warn("No renewable contracts due within 5 days");
            throw new NotFoundException("No renewable contracts due within 5 days");
        }

        log.info("Found {} renewable contracts due within 5 days", dueContracts.size());
        return dueContracts.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public Long countContractsDueForRenewal() {
        LocalDate fiveDaysFromNow = LocalDate.now().plusDays(5);
        log.info("Counting renewable contracts due within 5 days");
        Long count = requisitionRepository.countContractsDueForPayment(
                IsRenewable.YES, RequisitionStatus.CONTRACT, fiveDaysFromNow
        );
        log.info("Found {} renewable contracts due within 5 days", count);
        return count;
    }

    @Override
    public List<RequisitionResponse> getContractsDueForPayment() {
        LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
        log.info("Fetching contracts due for payment within 30 days, up to: {}", thirtyDaysFromNow);

        List<Requisition> dueContracts = requisitionRepository.findContractsDueForPayment(
                RequisitionStatus.CONTRACT, PaymentStatus.DEPOSIT_DUE, thirtyDaysFromNow
        );

        if (dueContracts.isEmpty()) {
            log.warn("No contracts due for payment within 30 days");
            throw new NotFoundException("No contracts due for payment within 30 days");
        }

        log.info("Found {} contracts due for payment within 30 days", dueContracts.size());
        return dueContracts.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public Long countContractsDueForPayment() {
        LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
        log.info("Counting contracts due for payment within 30 days");
        Long count = requisitionRepository.countContractsDueForPayment(
                RequisitionStatus.CONTRACT, PaymentStatus.DEPOSIT_DUE, thirtyDaysFromNow
        );
        log.info("Found {} contracts due for payment", count);
        return count;
    }

    // ------------------------ APPROVAL ------------------------
    @Override
    public ApprovalResponse addApprovalToRequisition(Long requisitionId, ApprovalRequest approvalRequest) {
        log.info("Adding approval to requisition ID: {} with request: {}", requisitionId, approvalRequest);
        Requisition requisition = requisitionRepository.findById(requisitionId)
                .orElseThrow(() -> new NotFoundException("Requisition not found with id: " + requisitionId));

        Approval approval = requisition.getApproval();
        if (approval != null) {
            log.info("Updating existing approval ID: {}", approval.getId());
            Approval updates = mapApprovalToEntity(approvalRequest);
            copyNonNullProperties(updates, approval);
            Approval saved = approvalRepository.save(approval);
            log.info("Updated approval ID: {}", saved.getId());
            return mapApprovalToResponse(saved);
        } else {
            log.info("Creating new approval for requisition ID: {}", requisitionId);
            Approval newApproval = mapApprovalToEntity(approvalRequest);
            Approval savedApproval = approvalRepository.save(newApproval);
            requisition.setApproval(savedApproval);
            requisitionRepository.save(requisition);
            log.info("Created approval ID: {} for requisition ID: {}", savedApproval.getId(), requisitionId);
            return mapApprovalToResponse(savedApproval);
        }
    }

    @Override
    public ApprovalResponse getApprovalByRequisitionId(Long requisitionId) {
        log.info("Fetching approval for requisition ID: {}", requisitionId);
        Requisition requisition = requisitionRepository.findById(requisitionId)
                .orElseThrow(() -> new EntityNotFoundException("Requisition not found with id: " + requisitionId));
        Approval approval = requisition.getApproval();
        if (approval == null) {
            log.warn("Approval not found for requisition ID: {}", requisitionId);
            throw new EntityNotFoundException("Approval not found for requisition id: " + requisitionId);
        }
        log.info("Found approval ID: {} for requisition ID: {}", approval.getId(), requisitionId);
        return mapApprovalToResponse(approval);
    }

    // ------------------------ SUMMARY ------------------------
    @Override
    public Map<String, Long> getRequisitionSummaryByStatus() {
        log.info("Generating requisition summary by status");
        Map<String, Long> summary = new HashMap<>();
        requisitionRepository.countRequisitionsGroupedByStatus().forEach(row -> {
            RequisitionStatus status = (RequisitionStatus) row[0];
            Long count = (Long) row[1];
            summary.put(status.name(), count);
            log.info("Status: {}, Count: {}", status, count);
        });
        return summary;
    }

    // ------------------------ FILE UPLOAD ------------------------
    @PostConstruct
    public void init() {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
            log.info("Created upload directory at: {}", uploadDir);
        } else {
            log.info("Upload directory exists at: {}", uploadDir);
        }
    }

    @Override
    public String uploadFiles(Long requisitionId, MultipartFile[] files) {
        log.info("Uploading {} files for requisition ID: {}", files.length, requisitionId);
        Requisition requisition = requisitionRepository.findById(requisitionId)
                .orElseThrow(() -> new NotFoundException("Requisition not found with id: " + requisitionId));

        StringBuilder uploadedFiles = new StringBuilder();
        for (MultipartFile file : files) {
            try {
                Path path = Paths.get(uploadDir, file.getOriginalFilename());
                Files.copy(file.getInputStream(), path);
                log.info("Saved file {} at {}", file.getOriginalFilename(), path);

                Attachment attachment = new Attachment();
                attachment.setFileName(file.getOriginalFilename());
                attachment.setFilePath(path.toString());
                attachment.setFileType(file.getContentType());
                requisition.addAttachment(attachment);

                uploadedFiles.append(file.getOriginalFilename()).append(", ");
            } catch (IOException e) {
                log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
                throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename(), e);
            }
        }

        requisitionRepository.save(requisition);
        log.info("Uploaded files for requisition ID: {}", requisitionId);
        return uploadedFiles.toString();
    }

    // ------------------------ UTILITIES ------------------------
    private void copyNonNullProperties(Object source, Object target) {
        if (source == null || target == null) return;

        BeanWrapper src = new BeanWrapperImpl(source);
        BeanWrapper trg = new BeanWrapperImpl(target);

        for (PropertyDescriptor pd : src.getPropertyDescriptors()) {
            String name = pd.getName();
            if ("class".equals(name) || "id".equals(name)) continue;
            Object value = src.getPropertyValue(name);
            if (value != null) {
                trg.setPropertyValue(name, value);
                log.debug("Copied property {} with value {}", name, value);
            }
        }
    }

private Requisition mapToEntity(RequisitionRequest req) {
        if (req == null) return null;

        return Requisition.builder()
                .requisitionTo(req.getRequisitionTo())
                .requisitionFrom(req.getRequisitionFrom())
                .date(req.getDate())
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
    }


    private RequisitionResponse mapToResponse(Requisition req) {
        if (req == null) return null;

        return RequisitionResponse.builder()
                .id(req.getId())
                .requisitionTo(req.getRequisitionTo())
                .requisitionFrom(req.getRequisitionFrom())
                .date(req.getDate())
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
                .approval(req.getApproval() != null ? mapApprovalToResponse(req.getApproval()) : null)
                .build();
    }

    private Approval mapApprovalToEntity(ApprovalRequest req) {
        if (req == null) return null;

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
}
