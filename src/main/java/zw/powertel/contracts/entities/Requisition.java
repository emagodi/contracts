package zw.powertel.contracts.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import zw.powertel.contracts.enums.*;
import zw.powertel.contracts.handlers.BaseEntity;

import java.util.Date;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "requisitions")
public class Requisition extends BaseEntity {

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
    @Enumerated(EnumType.STRING)
    private DeliveryNA deliveryNA;
    private String penalties;
    private String acceptanceConditions;
    private String warrantyDays;
    private String warrantyWeeks;
    private String warrantyMonths;
    @Enumerated(EnumType.STRING)
    private WarrantyNA warrantyNA;
    @Enumerated(EnumType.STRING)
    private ServiceSupport serviceSupport;
    private String specialIssues;
    @Enumerated(EnumType.STRING)
    private FundingAvailable fundingAvailable;
    @Enumerated(EnumType.STRING)
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

    // 🔗 New One-to-One relation with Approval
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "approval_id", referencedColumnName = "id")
    @JsonIgnore
    private Approval approval;

    @OneToOne(mappedBy = "requisition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private ContractDraft contractDraft;

}
