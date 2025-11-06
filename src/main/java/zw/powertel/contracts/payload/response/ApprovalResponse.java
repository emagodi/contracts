package zw.powertel.contracts.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.powertel.contracts.enums.ApprovalStatus;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalResponse {

    private Long id;
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
    private ApprovalStatus approvalStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

}
