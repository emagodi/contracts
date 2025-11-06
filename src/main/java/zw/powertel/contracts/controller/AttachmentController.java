package zw.powertel.contracts.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zw.powertel.contracts.payload.response.AttachmentResponse;
import zw.powertel.contracts.service.AttachmentService;
import org.springframework.data.domain.Page;

@Tag(name = "Attachments Endpoints", description = "Endpoints for managing requisitions attachments")
@RestController
@RequestMapping("/api/v1/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    // ✅ Paginated list of attachments for a requisition
    @GetMapping("/requisition/{reqId}")
    @Operation(summary = "Paginated list of attachments for a requisition")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'PROCUREMENTMANAGER')")
    public ResponseEntity<Page<AttachmentResponse>> list(
            @PathVariable Long reqId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(attachmentService.listByRequisition(reqId, page, size));
    }

    // ✅ Rename attachment (version increment)
    @PatchMapping("/{id}/rename")
    @Operation(summary = "Rename attachment (version increment)")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'PROCUREMENTMANAGER')")
    public ResponseEntity<AttachmentResponse> rename(
            @PathVariable Long id,
            @RequestParam String newName
    ) {
        return ResponseEntity.ok(attachmentService.rename(id, newName));
    }

    // ✅ Download file (forces browser download)
    @GetMapping("/{id}/download")
    @Operation(summary = "Download file (forces browser download")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'PROCUREMENTMANAGER')")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Resource resource = attachmentService.download(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // ✅ View file inline (browser preview)
    @GetMapping("/{id}/view")
    @Operation(summary = "View file inline (browser preview")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'PROCUREMENTMANAGER')")
    public ResponseEntity<Resource> view(@PathVariable Long id) {
        Resource resource = attachmentService.view(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // ✅ Delete attachment (DB + physical file)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete attachment (DB + physical file")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('USER','ADMIN', 'PALEGAL', 'TECHNICALDIRECTOR', 'FINANCEDIRECTOR', 'COMMERCIALDIRECTOR', 'PROCUREMENTMANAGER')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(attachmentService.delete(id));
    }
}
