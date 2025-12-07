package zw.powertel.contracts.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import zw.powertel.contracts.entities.Signature;
import zw.powertel.contracts.exception.NotFoundException;
import zw.powertel.contracts.repository.SignatureRepository;
import zw.powertel.contracts.service.SignatureService;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SignatureServiceImpl implements SignatureService {

    private final SignatureRepository signatureRepository;

    @Value("${file.upload-dir.signatures}")
    private String signatureUploadDir;

    @PostConstruct
    public void init() {
        File directory = new File(signatureUploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
            log.info("Created signature upload directory: {}", signatureUploadDir);
        }
    }

    @Override
    public Signature uploadSignature(String email, MultipartFile file) throws IOException {
        log.info("Uploading signature for email: {}", email);
        if (signatureRepository.existsByEmail(email)) {
            throw new IllegalStateException("Signature already exists for this user");
        }
        return saveSignature(email, file);
    }

    @Override
    public Signature updateSignature(String email, MultipartFile file) throws IOException {
        log.info("Updating signature for email: {}", email);
        Signature existing = signatureRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Signature not found for email: " + email));
        // Delete old file
        Files.deleteIfExists(Paths.get(existing.getFilePath()));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = auth != null ? auth.getName() : "system";

        String fileName = email + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(signatureUploadDir, fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        // Overwrite existing entity fields instead of creating a new record
        existing.setFileName(file.getOriginalFilename());
        existing.setFilePath(path.toString());
        existing.setFileType(file.getContentType());
        existing.setUpdatedBy(currentUserEmail);
        existing.setUpdatedAt(LocalDateTime.now());

        return signatureRepository.save(existing);
    }

    private Signature saveSignature(String email, MultipartFile file) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = auth != null ? auth.getName() : "system";

        String fileName = email + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(signatureUploadDir, fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        Signature signature = Signature.builder()
                .email(email)
                .fileName(file.getOriginalFilename())
                .filePath(path.toString())
                .fileType(file.getContentType())
                .createdBy(currentUserEmail)
                .updatedBy(currentUserEmail)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return signatureRepository.save(signature);
    }

    @Override
    public Optional<Signature> getSignatureByEmail(String email) {
        return signatureRepository.findByEmail(email);
    }

    @Override
    public Signature getSignatureById(Long id) {
        return signatureRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Signature not found with ID: " + id));
    }

    @Override
    public List<Signature> getAllSignatures() {
        List<Signature> signatures = signatureRepository.findAll();
        if (signatures.isEmpty()) {
            throw new NotFoundException("No signatures found");
        }
        return signatures;
    }

    @Override
    public void deleteSignature(Long id) throws IOException {
        Signature signature = getSignatureById(id);
        Files.deleteIfExists(Paths.get(signature.getFilePath()));
        signatureRepository.deleteById(id);
    }

    @Override
    public void deleteSignatureByEmail(String email) throws IOException {
        Signature signature = signatureRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Signature not found for email: " + email));
        Files.deleteIfExists(Paths.get(signature.getFilePath()));
        signatureRepository.deleteByEmail(email);
    }
}
