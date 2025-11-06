package zw.powertel.contracts.payload.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.powertel.contracts.enums.ApprovalStatus;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequest {

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

}
