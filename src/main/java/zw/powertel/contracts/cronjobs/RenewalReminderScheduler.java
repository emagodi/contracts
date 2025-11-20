package zw.powertel.contracts.cronjobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zw.powertel.contracts.entities.Requisition;
import zw.powertel.contracts.entities.User;
import zw.powertel.contracts.enums.IsRenewable;
import zw.powertel.contracts.enums.RequisitionStatus;
import zw.powertel.contracts.enums.Role;
import zw.powertel.contracts.repository.RequisitionRepository;
import zw.powertel.contracts.repository.UserRepository;
import zw.powertel.contracts.service.impl.SmsQueueProcessor;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RenewalReminderScheduler {

    private final RequisitionRepository requisitionRepository;
    private final UserRepository userRepository;
    private final SmsQueueProcessor smsQueueProcessor;

    /**
     * Runs every day at 10 AM
     */
    @Scheduled(cron = "0 0 11 * * *")
    public void sendRenewalReminders() {
        log.info("Running renewal reminder scheduler...");

        LocalDate today = LocalDate.now();
        LocalDate sevenDaysLater = today.plusDays(7);

        // Get renewable contracts expiring soon
        List<Requisition> expiringContracts =
                requisitionRepository.findRenewableContractsExpiringSoon(
                        RequisitionStatus.CONTRACT,
                        IsRenewable.YES,
                        today,
                        sevenDaysLater
                );

        int totalExpiring = expiringContracts.size();
        log.info("Contracts expiring soon: {}", totalExpiring);

        if (totalExpiring == 0) {
            log.info("No contracts expiring within the next 7 days. Exiting scheduler.");
            return;
        }

        // Get all company secretaries
        List<User> recipients = userRepository.findByRole(Role.COMPANYSECRETARY);
        if (recipients.isEmpty()) {
            log.warn("No COMPANYSECRETARY users found to notify.");
            return;
        }

        String msg = "There are " + totalExpiring +
                " contract(s) expiring within the next 7 days. Please take action.";

        // Queue SMS instead of sending directly
        for (User user : recipients) {
            try {
                smsQueueProcessor.queueSms(user.getPhone(), msg);
                log.info("Queued renewal reminder SMS for {}", user.getEmail());
            } catch (Exception e) {
                log.error("Failed to queue SMS for {}: {}", user.getEmail(), e.getMessage());
            }
        }

        log.info("Renewal reminder scheduler completed.");
    }
}
