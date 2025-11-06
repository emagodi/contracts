package zw.powertel.contracts.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zw.powertel.contracts.payload.response.ContractDraftResponse;
import zw.powertel.contracts.service.ContractDraftService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contract-drafts")
@RequiredArgsConstructor
public class ContractDraftController {

    private final ContractDraftService draftService;

    @PostMapping("/upload/{requisitionId}")
    @Operation(summary = "Upload a .docx contract draft and link to requisition")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN','PALEGAL')")
    public ResponseEntity<ContractDraftResponse> uploadDraft(
            @PathVariable Long requisitionId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "version", required = false) String version,
            @RequestParam(value = "summary", required = false) String summary
    ) {
        return ResponseEntity.ok(draftService.uploadDraftFile(requisitionId, file, title, author, version, summary));
    }

    @GetMapping("/{requisitionId}")
    @Operation(summary = "Get contract draft by requisition")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN','PALEGAL')")
    public ResponseEntity<ContractDraftResponse> getDraft(@PathVariable Long requisitionId) {
        return ResponseEntity.ok(draftService.getDraftByRequisition(requisitionId));
    }

    @GetMapping
    @Operation(summary = "Get all contract drafts")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN','PALEGAL')")
    public ResponseEntity<List<ContractDraftResponse>> getAll() {
        return ResponseEntity.ok(draftService.getAllDrafts());
    }
}
