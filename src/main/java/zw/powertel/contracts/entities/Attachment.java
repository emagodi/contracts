package zw.powertel.contracts.entities;

import jakarta.persistence.*;
import lombok.*;
import zw.powertel.contracts.handlers.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment extends BaseEntity {

    private String fileName;       // user-friendly name
    private String filePath;       // full absolute path
    private String fileType;       // MIME type

    private Integer version = 1;   // v1, v2, v3...

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisition_id")
    private Requisition requisition;
}

