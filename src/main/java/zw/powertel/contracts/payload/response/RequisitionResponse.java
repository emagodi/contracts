package zw.powertel.contracts.payload.response;

import lombok.*;
import zw.powertel.contracts.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequisitionResponse {

    private Long id;

    private String requisitionTo;
    private String requisitionFrom;

    private LocalDate date;

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

    private LocalDate startDate;
    private LocalDate endDate;

    private String durationDays;
    private String durationWeeks;
    private String durationMonths;
    private String durationYears;

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
    private LocalDate financeDate;

    private String procurementManager;
    private LocalDate procurementDate;

    private String headOfDept;
    private LocalDate headDate;

    private String companySecretary;
    private LocalDate secretaryDate;

    private RequisitionStatus requisitionStatus;
    private PaymentStatus paymentStatus;

    // One-to-one approval
    private ApprovalResponse approval;

    // Attachments
    private List<String> attachments;

    // BaseEntity fields
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
