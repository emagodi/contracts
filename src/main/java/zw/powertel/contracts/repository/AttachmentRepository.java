package zw.powertel.contracts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import zw.powertel.contracts.entities.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    Page<Attachment> findAllByRequisitionId(Long requisitionId, Pageable pageable);
}

