package zw.powertel.contracts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import zw.powertel.contracts.entities.Attachment;
import zw.powertel.contracts.exception.AttachmentNotFoundException;
import zw.powertel.contracts.exception.FileStorageException;
import zw.powertel.contracts.payload.response.AttachmentResponse;
import zw.powertel.contracts.repository.AttachmentRepository;
import zw.powertel.contracts.service.AttachmentService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Override
    public AttachmentResponse rename(Long attachmentId, String newName) {
        Attachment att = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        att.setFileName(newName);
        att.setVersion(att.getVersion() + 1); // versioning
        attachmentRepository.save(att);

        return toResponse(att);
    }

    @Override
    public Resource download(Long attachmentId) {
        Attachment att = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException("Attachment not found: " + attachmentId));

        try {
            Resource resource = new UrlResource(Paths.get(att.getFilePath()).toUri());

            if (!resource.exists()) {
                throw new FileStorageException("File not found on server: " + att.getFilePath());
            }

            return resource;
        } catch (Exception e) {
            throw new FileStorageException("Could not load file: " + att.getFilePath());
        }
    }

    @Override
    public Resource view(Long attachmentId) {
        return download(attachmentId);
    }

    @Override
    public String delete(Long attachmentId) {
        Attachment att = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        try {
            Files.deleteIfExists(Paths.get(att.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file from disk");
        }

        attachmentRepository.delete(att);
        return "Deleted successfully";
    }

    @Override
    public Page<AttachmentResponse> listByRequisition(Long requisitionId, int page, int size) {
        Page<Attachment> attachments = attachmentRepository.findAllByRequisitionId(
                requisitionId, PageRequest.of(page, size)
        );
        return attachments.map(this::toResponse);
    }

    @Override
    public AttachmentResponse toResponse(Attachment a) {
        return AttachmentResponse.builder()
                .id(a.getId())
                .fileName(a.getFileName())
                .fileType(a.getFileType())
                .filePath(a.getFilePath())
                .version(a.getVersion())
                .createdAt(a.getCreatedAt())
                .createdBy(a.getCreatedBy())
                .updatedAt(a.getUpdatedAt())
                .updatedBy(a.getUpdatedBy())
                .build();
    }
}
