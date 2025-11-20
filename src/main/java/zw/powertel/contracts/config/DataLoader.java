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
        createMd();
        createBusiness();
        createCommercial();
        createTechnical();
        createFinance();
        createProcurement();
        createSecretary();
        createHod();

        // Load 200 requisitions with approvals
         // loadRequisitions();

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

    private void createSecretary() {
        if (!userRepository.findByEmail("secretary@powertel.co.zw").isPresent()) {
            User secretary = new User();
            secretary.setFirstname("Secretary");
            secretary.setLastname("Secretary");
            secretary.setEmail("secretary@powertel.co.zw");
            secretary.setPassword(passwordEncoder.encode("Password@123"));
            secretary.setRole(Role.COMPANYSECRETARY);
            secretary.setTemporaryPassword(false);

            userRepository.save(secretary);
            System.out.println("Secretary user created.");
        } else {
            System.out.println("Secretary user already exists.");
        }
    }

    private void createProcurement() {
        if (!userRepository.findByEmail("procurement@powertel.co.zw").isPresent()) {
            User procurement = new User();
            procurement.setFirstname("Procurement");
            procurement.setLastname("Procurement");
            procurement.setEmail("procurement@powertel.co.zw");
            procurement.setPassword(passwordEncoder.encode("Password@123"));
            procurement.setRole(Role.PROCUREMENTMANAGER);
            procurement.setTemporaryPassword(false);

            userRepository.save(procurement);
            System.out.println("Procurement user created.");
        } else {
            System.out.println("Procurement user already exists.");
        }
    }

    private void createFinance() {
        if (!userRepository.findByEmail("finance@powertel.co.zw").isPresent()) {
            User finance = new User();
            finance.setFirstname("Finance");
            finance.setLastname("Finance");
            finance.setEmail("finance@powertel.co.zw");
            finance.setPassword(passwordEncoder.encode("Password@123"));
            finance.setRole(Role.FINANCEDIRECTOR);
            finance.setTemporaryPassword(false);

            userRepository.save(finance);
            System.out.println("Finance user created.");
        } else {
            System.out.println("Finance user already exists.");
        }
    }

    private void createTechnical() {
        if (!userRepository.findByEmail("technical@powertel.co.zw").isPresent()) {
            User technical = new User();
            technical.setFirstname("Technical");
            technical.setLastname("Technical");
            technical.setEmail("technical@powertel.co.zw");
            technical.setPassword(passwordEncoder.encode("Password@123"));
            technical.setRole(Role.TECHNICALDIRECTOR);
            technical.setTemporaryPassword(false);

            userRepository.save(technical);
            System.out.println("Technical user created.");
        } else {
            System.out.println("Technical user already exists.");
        }
    }

    private void createCommercial() {
        if (!userRepository.findByEmail("commercial@powertel.co.zw").isPresent()) {
            User commercial = new User();
            commercial.setFirstname("Commercial");
            commercial.setLastname("Commercial");
            commercial.setEmail("commercial@powertel.co.zw");
            commercial.setPassword(passwordEncoder.encode("Password@123"));
            commercial.setRole(Role.COMMERCIALDIRECTOR);
            commercial.setTemporaryPassword(false);

            userRepository.save(commercial);
            System.out.println("Commercial user created.");
        } else {
            System.out.println("Commercial user already exists.");
        }
    }

    private void createBusiness() {
        if (!userRepository.findByEmail("business@powertel.co.zw").isPresent()) {
            User business = new User();
            business.setFirstname("Business");
            business.setLastname("Business");
            business.setEmail("business@powertel.co.zw");
            business.setPassword(passwordEncoder.encode("Password@123"));
            business.setRole(Role.BUSINESSMANAGER);
            business.setTemporaryPassword(false);

            userRepository.save(business);
            System.out.println("Business user created.");
        } else {
            System.out.println("Business user already exists.");
        }
    }

    private void createHod() {
        if (!userRepository.findByEmail("hod@powertel.co.zw").isPresent()) {
            User hod = new User();
            hod.setFirstname("Hod");
            hod.setLastname("Hod");
            hod.setEmail("hod@powertel.co.zw");
            hod.setPassword(passwordEncoder.encode("Password@123"));
            hod.setRole(Role.HOD);
            hod.setTemporaryPassword(false);

            userRepository.save(hod);
            System.out.println("HOD user created.");
        } else {
            System.out.println("HOD user already exists.");
        }
    }

    private void createMd() {
        if (!userRepository.findByEmail("md@powertel.co.zw").isPresent()) {
            User mod = new User();
            mod.setFirstname("Md");
            mod.setLastname("Md");
            mod.setEmail("md@powertel.co.zw");
            mod.setPassword(passwordEncoder.encode("Password@123"));
            mod.setRole(Role.MANAGINGDIRECTOR);
            mod.setTemporaryPassword(false);

            userRepository.save(mod);
            System.out.println("MD user created.");
        } else {
            System.out.println("MD user already exists.");
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
                    .date(LocalDate.from(currentDate))
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


