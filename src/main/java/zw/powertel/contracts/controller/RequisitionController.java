package zw.powertel.contracts.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zw.powertel.contracts.enums.RequisitionStatus;
import zw.powertel.contracts.payload.request.ApprovalRequest;
import zw.powertel.contracts.payload.request.RequisitionRequest;
import zw.powertel.contracts.payload.response.ApprovalResponse;
import zw.powertel.contracts.payload.response.RequisitionResponse;
import zw.powertel.contracts.service.RequisitionService;

import java.util.List;
import java.util.Map;

@Tag(name = "Requisition Endpoints", description = "Endpoints for managing requisitions")
@RestController
@RequestMapping("/api/v1/requisitions")
@RequiredArgsConstructor
@Slf4j
public class RequisitionController {

    private final RequisitionService requisitionService;

    // ------------------------ CRUD ------------------------
    @PostMapping("/create")
    @Operation(summary = "Create a new requisition")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    public ResponseEntity<RequisitionResponse> create(@RequestBody RequisitionRequest req) {
        log.info("Received request to create requisition: {}", req);
        RequisitionResponse response = requisitionService.createRequisition(req);
        log.info("Created requisition with ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update requisition (partial update: nulls ignored)")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    public ResponseEntity<RequisitionResponse> update(@PathVariable Long id, @RequestBody RequisitionRequest req) {
        log.info("Received request to update requisition ID: {} with data: {}", id, req);
        RequisitionResponse response = requisitionService.updateRequisition(id, req);
        log.info("Updated requisition ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @Operation(summary = "List all requisitions")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<RequisitionResponse>> list() {
        log.info("Fetching all requisitions");
        List<RequisitionResponse> requisitions = requisitionService.getAllRequisitions();
        log.info("Found {} requisitions", requisitions.size());
        return ResponseEntity.ok(requisitions);
    }

    @GetMapping("/find/{id}")
    @Operation(summary = "Get requisition by ID")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    public ResponseEntity<RequisitionResponse> get(@PathVariable Long id) {
        log.info("Fetching requisition with ID: {}", id);
        RequisitionResponse response = requisitionService.getRequisition(id);
        log.info("Found requisition: {}", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete requisition by ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deleting requisition with ID: {}", id);
        requisitionService.deleteRequisition(id);
        log.info("Deleted requisition with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    // ------------------------ APPROVAL ------------------------
    @PostMapping("/{id}/approval")
    @Operation(summary = "Add approval to a requisition")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    public ResponseEntity<ApprovalResponse> addApprovalToRequisition(
            @PathVariable Long id,
            @RequestBody ApprovalRequest approvalRequest) {

        log.info("Adding approval to requisition ID: {} with request: {}", id, approvalRequest);
        ApprovalResponse response = requisitionService.addApprovalToRequisition(id, approvalRequest);
        log.info("Added approval ID: {} to requisition ID: {}", response.getId(), id);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/approval")
    @Operation(summary = "Get approval details by requisition ID")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    public ResponseEntity<ApprovalResponse> getApprovalByRequisitionId(@PathVariable Long id) {
        log.info("Fetching approval for requisition ID: {}", id);
        ApprovalResponse response = requisitionService.getApprovalByRequisitionId(id);
        log.info("Fetched approval: {}", response);
        return ResponseEntity.ok(response);
    }

    // ------------------------ FILTERS ------------------------
    @GetMapping("/by-creator/{createdBy}")
    @Operation(summary = "Get requisitions by creator")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<RequisitionResponse>> getByCreator(@PathVariable String createdBy) {
        log.info("Fetching requisitions created by: {}", createdBy);
        List<RequisitionResponse> response = requisitionService.getRequisitionsByCreator(createdBy);
        log.info("Found {} requisitions by creator {}", response.size(), createdBy);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-status/{status}")
    @Operation(summary = "Get requisitions by status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<RequisitionResponse>> getByStatus(@PathVariable RequisitionStatus status) {
        log.info("Fetching requisitions with status: {}", status);
        List<RequisitionResponse> response = requisitionService.getRequisitionsByStatus(status);
        log.info("Found {} requisitions with status {}", response.size(), status);
        return ResponseEntity.ok(response);
    }

    // ------------------------ CONTRACTS ------------------------
    @GetMapping("/contracts-expiry")
    @Operation(summary = "Get due contracts for renewal")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<RequisitionResponse>> getContractsDueForRenewal() {
        log.info("Fetching contracts due for renewal");
        List<RequisitionResponse> response = requisitionService.getContractsDueForRenewal();
        log.info("Found {} contracts due for renewal", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/contracts-expiry/count")
    @Operation(summary = "Get count of contracts due for renewal")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    public ResponseEntity<Long> countContractsDueForRenewal() {
        log.info("Counting contracts due for renewal");
        Long count = requisitionService.countContractsDueForRenewal();
        log.info("Contracts due for renewal count: {}", count);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/contracts-due")
    @Operation(summary = "Get contracts due for payment (Deposit Due within 30 days)")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<RequisitionResponse>> getContractsDueForPayment() {
        log.info("Fetching contracts due for payment");
        List<RequisitionResponse> response = requisitionService.getContractsDueForPayment();
        log.info("Found {} contracts due for payment", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/contracts-due/count")
    @Operation(summary = "Get count of contracts due for payment")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    public ResponseEntity<Long> countContractsDueForPayment() {
        log.info("Counting contracts due for payment");
        Long count = requisitionService.countContractsDueForPayment();
        log.info("Contracts due for payment count: {}", count);
        return ResponseEntity.ok(count);
    }

    // ------------------------ SUMMARY ------------------------
    @GetMapping("/summary/status")
    @Operation(summary = "Get requisition summary by status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    public ResponseEntity<Map<String, Long>> getRequisitionSummaryByStatus() {
        log.info("Fetching requisition summary by status");
        Map<String, Long> summary = requisitionService.getRequisitionSummaryByStatus();
        log.info("Requisition summary: {}", summary);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/count/status/{status}")
    @Operation(summary = "Count requisitions by status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    public ResponseEntity<Long> countRequisitionsByStatus(@PathVariable RequisitionStatus status) {
        log.info("Counting requisitions with status: {}", status);
        Long count = requisitionService.countRequisitionsByStatus(status);
        log.info("Count for status {}: {}", status, count);
        return ResponseEntity.ok(count);
    }

    // ------------------------ FILE UPLOAD ------------------------
    @PostMapping(
            value = "/{id}/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    public ResponseEntity<String> uploadFiles(
            @PathVariable Long id,
            @RequestPart("files") MultipartFile[] files
    ) {
        log.info("Uploading files for requisition ID: {}. Files count: {}", id, files.length);
        String uploadedFiles = requisitionService.uploadFiles(id, files);
        log.info("Uploaded files: {}", uploadedFiles);
        return ResponseEntity.ok("✅ Uploaded: " + uploadedFiles);
    }
}
