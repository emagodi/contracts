package zw.powertel.contracts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import zw.powertel.contracts.entities.ContractDraft;
import zw.powertel.contracts.entities.Requisition;
import zw.powertel.contracts.exception.NotFoundException;
import zw.powertel.contracts.payload.response.ContractDraftResponse;
import zw.powertel.contracts.repository.ContractDraftRepository;
import zw.powertel.contracts.repository.RequisitionRepository;
import zw.powertel.contracts.service.ContractDraftService;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ContractDraftServiceImpl implements ContractDraftService {

    private final ContractDraftRepository draftRepository;
    private final RequisitionRepository requisitionRepository;

    // Configure in application.properties as: contracts.drafts.storage-path=uploads/contracts
    @Value("${contracts.drafts.storage-path:uploads/contracts}")
    private String storageDir;

    @Override
    public ContractDraftResponse uploadDraftFile(Long requisitionId, MultipartFile file, String title, String author, String version, String summary) {
        Requisition requisition = requisitionRepository.findById(requisitionId)
                .orElseThrow(() -> new NotFoundException("Requisition not found with ID: " + requisitionId));

        if (file == null || file.isEmpty()) throw new RuntimeException("No file provided");

        try {
            Path storagePath = Paths.get(storageDir).toAbsolutePath().normalize();
            Files.createDirectories(storagePath);

            String ext = getExtension(file.getOriginalFilename(), "docx");
            String filename = "contract_" + requisitionId + "_" + UUID.randomUUID() + "." + ext;
            Path target = storagePath.resolve(filename);

            // Save file to disk
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // Build accessible URL (served by static resource handler)
            String fileUrl = "/files/contracts/" + filename;

            ContractDraft draft = ContractDraft.builder()
                    .title(title)
                    .author(author)
                    .version(version)
                    .status("DRAFT")
                    .fileUrl(fileUrl)
                    .summary(summary)
                    .requisition(requisition)
                    .build();

            ContractDraft saved = draftRepository.save(draft);

            // link back to requisition if not already set
            requisition.setContractDraft(saved);
            requisitionRepository.save(requisition);

            return mapToResponse(saved);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public ContractDraftResponse getDraftByRequisition(Long requisitionId) {
        ContractDraft draft = draftRepository.findByRequisitionId(requisitionId)
                .orElseThrow(() -> new NotFoundException("Draft not found for requisition ID: " + requisitionId));
        return mapToResponse(draft);
    }

    @Override
    public List<ContractDraftResponse> getAllDrafts() {
        return draftRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private ContractDraftResponse mapToResponse(ContractDraft d) {
        return ContractDraftResponse.builder()
                .id(d.getId())
                .title(d.getTitle())
                .author(d.getAuthor())
                .version(d.getVersion())
                .status(d.getStatus())
                .fileUrl(d.getFileUrl())
                .summary(d.getSummary())
                .requisitionId(d.getRequisition() != null ? d.getRequisition().getId() : null)
                .createdAt(d.getCreatedAt())
                .build();
    }

    private String getExtension(String original, String def) {
        if (original == null) return def;
        int i = original.lastIndexOf('.');
        if (i < 0) return def;
        return original.substring(i + 1);
    }
}

