package zw.powertel.contracts.config;


import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import zw.powertel.contracts.entities.Approval;
import zw.powertel.contracts.entities.Requisition;
import zw.powertel.contracts.entities.User;
import zw.powertel.contracts.enums.*;

import zw.powertel.contracts.repository.ApprovalRepository;
import zw.powertel.contracts.repository.RequisitionRepository;
import zw.powertel.contracts.repository.UserRepository;
import zw.powertel.contracts.service.impl.AuthenticationServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationServiceImpl authenticationServiceImpl;

    private final RequisitionRepository requisitionRepository;
    private final ApprovalRepository approvalRepository;

    private final Faker faker = new Faker();


    private final Random random = new Random();


    @Override
    public void run(String... args) throws Exception {
        // Create super admin user
        createSuperAdmin();

        // Load 200 requisitions with approvals
          loadRequisitions();

    }

    private void createSuperAdmin() {
        if (!userRepository.findByEmail("emagodi1@powertel.co.zw").isPresent()) {
            User superAdmin = new User();
            superAdmin.setFirstname("Edwin");
            superAdmin.setLastname("Magodi");
            superAdmin.setEmail("emagodi1@powertel.co.zw");
            superAdmin.setPassword(passwordEncoder.encode("Password@123"));
            superAdmin.setRole(Role.ADMIN);
            superAdmin.setTemporaryPassword(false);

            userRepository.save(superAdmin);
            System.out.println("Default ADMIN user created.");
        } else {
            System.out.println("ADMIN user already exists.");
        }
    }


        private void loadRequisitions() {
            for (int i = 0; i < 200; i++) {
                Requisition requisition = createRequisition(i);
                requisitionRepository.save(requisition);
            }
            System.out.println("200 requisitions with approvals created.");
        }

        private Requisition createRequisition(int index) {
            Approval approval = createApproval(index);

            RequisitionStatus requisitionStatus = getRandomRequisitionStatus();
            ApprovalStatus approvalStatus = getRandomApprovalStatus();

            LocalDateTime currentDate = LocalDateTime.now();
            LocalDateTime startDate = currentDate.minusDays(random.nextInt(10)); // Start date within last 10 days
            LocalDateTime endDate = startDate.plusDays(30); // End date 30 days after start
            LocalDateTime approvalDate = startDate.plusDays(random.nextInt(10));

            return Requisition.builder()
                    .requisitionTo(faker.company().name())
                    .requisitionFrom(faker.company().name())
                    .date(currentDate)
                    .description(faker.lorem().sentence())
                    .vendorRegistedName(faker.company().name())
                    .vendorEmail(faker.internet().emailAddress())
                    .vendorPhoneNumber(faker.phoneNumber().phoneNumber())
                    .vendorTradingName(faker.company().name())
                    .vendorAddress(faker.address().fullAddress())
                    .vendorContactPerson(faker.name().fullName())
                    .contactNumber(faker.phoneNumber().phoneNumber())
                    .contactPersonCapacity(faker.lorem().word())
                    .justification(faker.lorem().sentence())
                    .startDate(LocalDate.from(currentDate))
                    .endDate(LocalDate.from(endDate))
                    .durationDays(String.valueOf(faker.number().randomDigitNotZero()))
                    .durationWeeks(faker.number().digits(1))
                    .durationMonths(faker.number().digits(1))
                    .durationYears(faker.number().digits(1))
                    .isRenewable(getRandomIsRenewable()) // Get value from enum
                    .renewalDays(faker.number().digits(1))
                    .renewalWeeks(faker.number().digits(1))
                    .renewalMonths(faker.number().digits(1))
                    .renewalYears(faker.number().digits(1))
                    .contractPrice(faker.commerce().price())
                    .vat(faker.commerce().price())
                    .totalContractPrice(faker.commerce().price())
                    .totalOnsignature(faker.commerce().price())
                    .downPayment(faker.commerce().price())
                    .balancePayment(faker.commerce().price())
                    .deliveryDays(faker.number().digits(1))
                    .deliveryWeeks(faker.number().digits(1))
                    .deliveryMonths(faker.number().digits(1))
                    .deliveryNA(getRandomDeliveryNA()) // Get value from enum
                    .penalties(faker.lorem().sentence())
                    .acceptanceConditions(faker.lorem().sentence())
                    .warrantyDays(faker.number().digits(1))
                    .warrantyWeeks(faker.number().digits(1))
                    .warrantyMonths(faker.number().digits(1))
                    .warrantyNA(getRandomWarrantyNA()) // Get value from enum
                    .serviceSupport(getRandomServiceSupport()) // Get value from enum
                    .specialIssues(faker.lorem().sentence())
                    .fundingAvailable(getRandomFundingAvailable()) // Get value from enum
                    .procurementComplied(getRandomProcurementComplied()) // Get value from enum
                    .financeDirector(faker.name().fullName())
                    .financeDate(LocalDate.from(currentDate))
                    .procurementManager(faker.name().fullName())
                    .procurementDate(LocalDate.from(currentDate))
                    .headOfDept(faker.name().fullName())
                    .headDate(LocalDate.from(currentDate))
                    .companySecretary(faker.name().fullName())
                    .secretaryDate(LocalDate.from(currentDate))
                    .requisitionStatus(requisitionStatus)
                    .paymentStatus(getRandomPaymentStatus())
                    .approval(approval)
                    .build();
        }

        private Approval createApproval(int index) {
            ApprovalStatus approvalStatus = getRandomApprovalStatus();
            Date currentDate = new Date();

            return Approval.builder()
                    .approvalTo(faker.name().fullName())
                    .approvalDate(currentDate)
                    .approvalReference("REF-" + index)
                    .legalSignature(faker.name().fullName())
                    .legalSignatureDate(currentDate)
                    .legalComments(faker.lorem().sentence())
                    .technicalSignature(faker.name().fullName())
                    .technicalSignatureDate(currentDate)
                    .technicalComments(faker.lorem().sentence())
                    .financialSignature(faker.name().fullName())
                    .financialSignatureDate(currentDate)
                    .financialComments(faker.lorem().sentence())
                    .commercialSignature(faker.name().fullName())
                    .commercialSignatureDate(currentDate)
                    .commercialComments(faker.lorem().sentence())
                    .businessDevelopmentSignature(faker.name().fullName())
                    .businessDevelopmentSignatureDate(currentDate)
                    .businessDevelopmentComments(faker.lorem().sentence())
                    .procurementSignature(faker.name().fullName())
                    .procurementSignatureDate(currentDate)
                    .procurementComments(faker.lorem().sentence())
                    .approvalStatus(approvalStatus)
                    .build();
        }

        // Method to get a random RequisitionStatus
        private RequisitionStatus getRandomRequisitionStatus() {
            RequisitionStatus[] statuses = RequisitionStatus.values();
            return statuses[random.nextInt(statuses.length)];
        }

        // Method to get a random ApprovalStatus
        private ApprovalStatus getRandomApprovalStatus() {
            ApprovalStatus[] statuses = ApprovalStatus.values();
            return statuses[random.nextInt(statuses.length)];
        }

        // Method to get a random IsRenewable enum value
        private IsRenewable getRandomIsRenewable() {
            IsRenewable[] values = IsRenewable.values();
            return values[random.nextInt(values.length)];
        }

        // Method to get a random DeliveryNA enum value
        private DeliveryNA getRandomDeliveryNA() {
            DeliveryNA[] values = DeliveryNA.values();
            return values[random.nextInt(values.length)];
        }

        // Method to get a random WarrantyNA enum value
        private WarrantyNA getRandomWarrantyNA() {
            WarrantyNA[] values = WarrantyNA.values();
            return values[random.nextInt(values.length)];
        }

        // Method to get a random ServiceSupport enum value
        private ServiceSupport getRandomServiceSupport() {
            ServiceSupport[] values = ServiceSupport.values();
            return values[random.nextInt(values.length)];
        }

        // Method to get a random FundingAvailable enum value
        private FundingAvailable getRandomFundingAvailable() {
            FundingAvailable[] values = FundingAvailable.values();
            return values[random.nextInt(values.length)];
        }

        // Method to get a random ProcurementComplied enum value
        private ProcurementComplied getRandomProcurementComplied() {
            ProcurementComplied[] values = ProcurementComplied.values();
            return values[random.nextInt(values.length)];
        }

        // Method to get a random PaymentStatus enum value
        private PaymentStatus getRandomPaymentStatus() {
            PaymentStatus[] values = PaymentStatus.values();
            return values[random.nextInt(values.length)];
        }
    }


