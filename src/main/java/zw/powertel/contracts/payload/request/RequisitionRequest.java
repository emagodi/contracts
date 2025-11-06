package zw.powertel.contracts.payload.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.powertel.contracts.enums.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequisitionRequest {

    private String requisitionTo;
    private String requisitionFrom;
    private Date date;
    private String description;
    private String vendorRegistedName;
    private String vendorEmail;
    private String vendorPhoneNumber;
    private String vendorTradingName;
    private String vendorAddress;
    private String vendorContactPerson;
    private String contactNumber;
    private String contactPersonCapacity;
    private String justification;
    private Date startDate;
    private Date endDate;
    private String durationDays;
    private String durationWeeks;
    private String durationMonths;
    private String durationYears;
    @Enumerated(EnumType.STRING)
    private IsRenewable isRenewable;
    private String renewalDays;
    private String renewalWeeks;
    private String renewalMonths;
    private String renewalYears;
    private String contractPrice;
    private String vat;
    private String totalContractPrice;
    private String totalOnsignature;
    private String downPayment;
    private String balancePayment;
    private String deliveryDays;
    private String deliveryWeeks;
    private String deliveryMonths;
    private DeliveryNA deliveryNA;
    private String penalties;
    private String acceptanceConditions;
    private String warrantyDays;
    private String warrantyWeeks;
    private String warrantyMonths;
    private WarrantyNA warrantyNA;
    private ServiceSupport serviceSupport;
    private String specialIssues;
    private FundingAvailable fundingAvailable;
    private ProcurementComplied procurementComplied;
    private String financeDirector;
    private Date financeDate;
    private String procurementManager;
    private Date procurementDate;
    private String headOfDept;
    private Date headDate;
    private String companySecretary;
    private Date secretaryDate;
    @Enumerated(EnumType.STRING)
    private RequisitionStatus requisitionStatus;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private ApprovalRequest approval;
}
