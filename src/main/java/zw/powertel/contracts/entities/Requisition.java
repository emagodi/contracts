package zw.powertel.contracts.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import zw.powertel.contracts.enums.*;
import zw.powertel.contracts.handlers.BaseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    // Requisition date
    private LocalDateTime date;

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
    private LocalDate financeDate;

    private String procurementManager;
    private LocalDate procurementDate;

    private String headOfDept;
    private LocalDate headDate;

    private String companySecretary;
    private LocalDate secretaryDate;

    @Enumerated(EnumType.STRING)
    private RequisitionStatus requisitionStatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    // 🔗 Approval
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "approval_id", referencedColumnName = "id")
    @JsonIgnore
    private Approval approval;

    // 🔗 Contract draft
    @OneToOne(mappedBy = "requisition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private ContractDraft contractDraft;

    // 🔗 Attachments
    @OneToMany(mappedBy = "requisition", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Attachment> attachments = new ArrayList<>();

    // Helper method to maintain bidirectional relationship
    public void addAttachment(Attachment attachment) {
        attachment.setRequisition(this);
        this.attachments.add(attachment);
    }
}
