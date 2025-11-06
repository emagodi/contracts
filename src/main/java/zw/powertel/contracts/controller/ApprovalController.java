package zw.powertel.contracts.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zw.powertel.contracts.enums.ApprovalStatus;
import zw.powertel.contracts.payload.request.ApprovalRequest;
import zw.powertel.contracts.payload.response.ApprovalResponse;
import zw.powertel.contracts.service.ApprovalService;

import java.util.List;

@Tag(name = "Approval Endpoints", description = "Endpoints for managing approvals")
@RestController
@RequestMapping("/api/v1/approvals")
@RequiredArgsConstructor
@Slf4j
public class ApprovalController {

    private final ApprovalService approvalService;

    @PostMapping("/create")
    @Operation(summary = "Create a new approval")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('WRITE_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'PROCUREMENTMANAGER')")
        public ResponseEntity<ApprovalResponse> create(@RequestBody ApprovalRequest req) {
        log.info("Creating approval for: {}", req.getApprovalTo());
        return ResponseEntity.ok(approvalService.createApproval(req));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update approval (partial update: nulls ignored)")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('WRITE_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'PROCUREMENTMANAGER')")
    public ResponseEntity<ApprovalResponse> update(@PathVariable Long id, @RequestBody ApprovalRequest req) {
        log.info("Updating approval with id: {}", id);
        return ResponseEntity.ok(approvalService.updateApproval(id, req));
    }

    @GetMapping("/all")
    @Operation(summary = "List all approvals")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'PROCUREMENTMANAGER')")
    public ResponseEntity<List<ApprovalResponse>> list() {
        log.info("Fetching all approvals");
        return ResponseEntity.ok(approvalService.getAllApprovals());
    }

    @GetMapping("/find/{id}")
    @Operation(summary = "Get approval by ID")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'PROCUREMENTMANAGER')")
    public ResponseEntity<ApprovalResponse> get(@PathVariable Long id) {
        log.info("Fetching approval with id: {}", id);
        return ResponseEntity.ok(approvalService.getApproval(id));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete approval by ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('WRITE_PRIVILEGE') and hasAnyRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deleting approval with id: {}", id);
        approvalService.deleteApproval(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-status/{status}")
    @Operation(summary = "Get approvals by status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'PROCUREMENTMANAGER')")
    public ResponseEntity<List<ApprovalResponse>> getByStatus(@PathVariable ApprovalStatus status) {
        log.info("Fetching approvals with status: {}", status);
        List<ApprovalResponse> approvals = approvalService.getApprovalsByStatus(status);
        return ResponseEntity.ok(approvals);
    }
}
