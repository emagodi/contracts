package zw.powertel.contracts.service;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import zw.powertel.contracts.entities.Attachment;
import zw.powertel.contracts.payload.response.AttachmentResponse;

public interface AttachmentService {

    AttachmentResponse rename(Long attachmentId, String newName);

    Resource download(Long attachmentId);

    Resource view(Long attachmentId);

    String delete(Long attachmentId);

    Page<AttachmentResponse> listByRequisition(Long requisitionId, int page, int size);

    AttachmentResponse toResponse(Attachment attachment);
}
