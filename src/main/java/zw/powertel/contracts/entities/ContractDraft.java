package zw.powertel.contracts.entities;

import jakarta.persistence.*;
import lombok.*;
import zw.powertel.contracts.handlers.BaseEntity;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "contract_drafts")
public class ContractDraft extends BaseEntity {

    private String title;
    private String author;
    private String version;
    private String status;
    private String fileUrl;
    private String summary;

    @OneToOne
    @JoinColumn(name = "requisition_id", referencedColumnName = "id")
    private Requisition requisition;
}

