package zw.powertel.contracts.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zw.powertel.contracts.entities.Signature;
import zw.powertel.contracts.service.SignatureService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/v1/signature")
@RequiredArgsConstructor
@Slf4j
public class SignatureController {

    private final SignatureService signatureService;

    // ------------------------ UPLOAD ------------------------
    @PostMapping(
            value = "/upload/{email}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    @Operation(summary = "Upload user signature by email")
    public ResponseEntity<String> uploadSignature(
            @PathVariable String email,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        log.info("Uploading signature for user: {}", email);
        Signature saved = signatureService.uploadSignature(email, file);
        String url = "/api/v1/signature/file/" + saved.getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(url);
    }

    // ------------------------ UPDATE ------------------------
    @PutMapping(
            value = "/update/{email}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    @Operation(summary = "Update existing signature by email")
    public ResponseEntity<String> updateSignature(
            @PathVariable String email,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        log.info("Updating signature for user: {}", email);
        Signature updated = signatureService.updateSignature(email, file);
        String url = "/api/v1/signature/file/" + updated.getId();
        return ResponseEntity.ok(url);
    }

    // ------------------------ GET ------------------------
    @GetMapping("/user/email/{email}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    @Operation(summary = "Get signature by email")
    public ResponseEntity<String> getSignatureByEmail(@PathVariable String email) {
        log.info("Fetching signature for user: {}", email);
        return signatureService.getSignatureByEmail(email)
                .map(sig -> ResponseEntity.ok("/api/v1/signature/file/" + sig.getId()))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("❌ Signature not found for email: " + email));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    @Operation(summary = "Get signature by ID")
    public ResponseEntity<String> getSignatureById(@PathVariable Long id) {
        log.info("Fetching signature with ID: {}", id);
        Signature sig = signatureService.getSignatureById(id);
        return ResponseEntity.ok("/api/v1/signature/file/" + sig.getId());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    @Operation(summary = "Get all signatures")
    public ResponseEntity<List<String>> getAllSignatures() {
        log.info("Fetching all signatures");
        List<String> urls = signatureService.getAllSignatures()
                .stream()
                .map(sig -> "/api/v1/signature/file/" + sig.getId())
                .toList();
        return ResponseEntity.ok(urls);
    }

    // ------------------------ DELETE ------------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    @Operation(summary = "Delete signature by ID")
    public ResponseEntity<Void> deleteSignature(@PathVariable Long id) throws IOException {
        log.info("Deleting signature with ID: {}", id);
        signatureService.deleteSignature(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/email/{email}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    @Operation(summary = "Delete signature by email")
    public ResponseEntity<Void> deleteSignatureByEmail(@PathVariable String email) throws IOException {
        log.info("Deleting signature for email: {}", email);
        signatureService.deleteSignatureByEmail(email);
        return ResponseEntity.noContent().build();
    }

    // ------------------------ FILE SERVE ------------------------
    @GetMapping("/file/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'PALEGAL', 'COMPANYSECRETARY', 'MANAGINGDIRECTOR', 'PROCUREMENTMANAGER', 'FINANCEDIRECTOR', 'TECHNICALDIRECTOR', 'COMMERCIALDIRECTOR', 'BUSINESSMANAGER', 'HOD', 'USER')")
    @Operation(summary = "Serve signature file")
    public ResponseEntity<byte[]> getSignatureFile(@PathVariable Long id) throws IOException {
        Signature sig = signatureService.getSignatureById(id);
        Path path = Paths.get(sig.getFilePath());
        byte[] content = Files.readAllBytes(path);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(sig.getFileType()))
                .body(content);
    }
}
