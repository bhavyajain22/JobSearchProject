package com.jobflow.alerts.service;

import com.jobflow.alerts.model.AlertChannel;
import com.jobflow.alerts.model.SavedSearch;
import com.jobflow.jobs.dto.JobDto;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final EmailService emailService;
    private final WhatsAppService whatsAppService;

    public NotificationServiceImpl(EmailService emailService, WhatsAppService whatsAppService) {
        this.emailService = emailService;
        this.whatsAppService = whatsAppService;
    }

    @Override
    public void sendNotification(SavedSearch savedSearch, List<JobDto> jobs) {
        if (savedSearch == null || jobs == null || jobs.isEmpty()) {
            System.out.println("[NotificationService] Skipping send — invalid input or no jobs.");
            return;
        }

        AlertChannel ch = savedSearch.getChannel();
        String contact = savedSearch.getContact();
        String jobTitle = "Your Job Alerts"; // Default title
        try {
            switch (ch) {
                case EMAIL -> {
                    emailService.sendJobAlert(contact, jobs, jobTitle);
                    System.out.printf("[NotificationService] Email alert sent to %s (%d jobs)%n", contact, jobs.size());
                }
                case WHATSAPP -> {
                    whatsAppService.sendJobAlert(contact, jobs, jobTitle);
                    System.out.printf("[NotificationService] WhatsApp alert sent to %s (%d jobs)%n", contact, jobs.size());
                }
                default -> System.out.printf("[NotificationService] Unsupported channel: %s%n", ch);
            }
        } catch (Exception e) {
            System.err.printf("[NotificationService] Failed to send notification to %s (%s): %s%n",
                    contact, ch, e.getMessage());
        }
    }
}
