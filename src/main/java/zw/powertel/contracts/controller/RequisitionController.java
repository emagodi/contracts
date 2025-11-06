package zw.powertel.contracts.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/create")
    @Operation(summary = "Create a new requisition")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'PROCUREMENTMANAGER')")
    public ResponseEntity<RequisitionResponse> create(@RequestBody RequisitionRequest req) {
        return ResponseEntity.ok(requisitionService.createRequisition(req));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update requisition (partial update: nulls ignored)")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR' , 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'PROCUREMENTMANAGER')")
    public ResponseEntity<RequisitionResponse> update(@PathVariable Long id, @RequestBody RequisitionRequest req) {
        return ResponseEntity.ok(requisitionService.updateRequisition(id, req));
    }

    @GetMapping("/all")
    @Operation(summary = "List all requisitions")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'PROCUREMENTMANAGER')")
    public ResponseEntity<List<RequisitionResponse>> list() {
        return ResponseEntity.ok(requisitionService.getAllRequisitions());
    }

    @GetMapping("/find/{id}")
    @Operation(summary = "Get requisition by ID")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'PROCUREMENTMANAGER')")
    public ResponseEntity<RequisitionResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(requisitionService.getRequisition(id));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete requisition by ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'PROCUREMENTMANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        requisitionService.deleteRequisition(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/approval")
    @Operation(summary = "Add approval to a requisition")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'PROCUREMENTMANAGER')")
    public ResponseEntity<ApprovalResponse> addApprovalToRequisition(
            @PathVariable Long id,
            @RequestBody ApprovalRequest approvalRequest) {

        ApprovalResponse approvalResponse = requisitionService.addApprovalToRequisition(id, approvalRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(approvalResponse);
    }

    @GetMapping("/by-creator/{createdBy}")
    @Operation(summary = "Get requisitions by creator")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'PROCUREMENTMANAGER')")
    public ResponseEntity<List<RequisitionResponse>> getByCreator(@PathVariable String createdBy) {
        List<RequisitionResponse> requisitions = requisitionService.getRequisitionsByCreator(createdBy);
        return ResponseEntity.ok(requisitions);
    }

    @GetMapping("/by-status/{status}")
    @Operation(summary = "Get requisitions by status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'PROCUREMENTMANAGER')")
    public ResponseEntity<List<RequisitionResponse>> getByStatus(@PathVariable RequisitionStatus status) {
        List<RequisitionResponse> requisitions = requisitionService.getRequisitionsByStatus(status);
        return ResponseEntity.ok(requisitions);
    }

    // 🟢 List all contracts due for payment
    @GetMapping("/contracts-expiry")
    @Operation(summary = "Get due contracts for renewal")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'PROCUREMENTMANAGER')")
    public ResponseEntity<List<RequisitionResponse>> getContractsDueForRenewal() {
        List<RequisitionResponse> contracts = requisitionService.getContractsDueForRenewal();
        return ResponseEntity.ok(contracts);
    }

    // 🟢 Count only
    @GetMapping("/contracts-expiry/count")
    @Operation(summary = "Get count of contracts due for renewal")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'PROCUREMENTMANAGER')")
    public ResponseEntity<Long> countContractsDueForRenewal() {
        Long count = requisitionService.countContractsDueForRenewal();
        return ResponseEntity.ok(count);
    }

    // 🟢 List all contracts due for initial payment
    @GetMapping("/contracts-due")
    @Operation(summary = "Get contracts due for payment (Deposit Due within 30 days)")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'PROCUREMENTMANAGER')")
    public ResponseEntity<List<RequisitionResponse>> getContractsDueForPayment() {
        List<RequisitionResponse> contracts = requisitionService.getContractsDueForPayment();
        return ResponseEntity.ok(contracts);
    }

    // 🟢 Count only
    @GetMapping("/contracts-due/count")
    @Operation(summary = "Get count of contracts due for payment")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'PROCUREMENTMANAGER')")
    public ResponseEntity<Long> countContractsDueForPayment() {
        Long count = requisitionService.countContractsDueForPayment();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{id}/approval")
    @Operation(summary = "Get approval details by requisition ID")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'PROCUREMENTMANAGER')")
    public ResponseEntity<ApprovalResponse> getApprovalByRequisitionId(@PathVariable Long id) {
        ApprovalResponse approvalResponse = requisitionService.getApprovalByRequisitionId(id);
        return ResponseEntity.ok(approvalResponse); // Return the ApprovalResponse
    }


    @GetMapping("/summary/status")
    @Operation(summary = "Get requisition summary by status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'PROCUREMENTMANAGER')")
    public ResponseEntity<Map<String, Long>> getRequisitionSummaryByStatus() {
        Map<String, Long> summary = requisitionService.getRequisitionSummaryByStatus();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/count/status/{status}")
    @Operation(summary = "Count requisitions by status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'PROCUREMENTMANAGER')")
    public ResponseEntity<Long> countRequisitionsByStatus(@PathVariable RequisitionStatus status) {
        Long count = requisitionService.countRequisitionsByStatus(status);
        return ResponseEntity.ok(count);
    }



}
