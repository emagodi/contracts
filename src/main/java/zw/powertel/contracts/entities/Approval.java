package zw.powertel.contracts.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import zw.powertel.contracts.enums.ApprovalStatus;
import zw.powertel.contracts.handlers.BaseEntity;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "approvals")
public class Approval extends BaseEntity {

    private String approvalTo;
    private Date approvalDate;
    private String approvalReference;
    private String legalSignature;
    private Date legalSignatureDate;
    private String legalComments;
    private String technicalSignature;
    private Date technicalSignatureDate;
    private String technicalComments;
    private String financialSignature;
    private Date financialSignatureDate;
    private String financialComments;
    private String commercialSignature;
    private Date commercialSignatureDate;
    private String commercialComments;
    private String businessDevelopmentSignature;
    private Date businessDevelopmentSignatureDate;
    private String businessDevelopmentComments;
    private String procurementSignature;
    private Date procurementSignatureDate;
    private String procurementComments;
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    // ✅ link back to Requisition (inverse side)
    @OneToOne(mappedBy = "approval")
    @JsonIgnore
    private Requisition requisition;
}
