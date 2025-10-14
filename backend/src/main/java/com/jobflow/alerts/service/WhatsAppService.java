package com.jobflow.alerts.service;

import com.jobflow.jobs.dto.JobDto;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class WhatsAppService {


    private static final Logger log = LoggerFactory.getLogger(WhatsAppService.class);
    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.fromNumber}")
    private String fromNumber;

    private boolean enabled = false;

    @PostConstruct
    public void init() {
        try {
            if (accountSid == null || accountSid.isBlank() ||
                    authToken == null || authToken.isBlank()) {
                log.warn("[WhatsAppService] Twilio credentials missing. Service disabled.");
                enabled = false;
                return;
            }
            Twilio.init(accountSid, authToken);
            enabled = true;
            log.info("[WhatsAppService] Initialized successfully with fromNumber={}", fromNumber);
        } catch (Exception e) {
            log.error("[WhatsAppService] Initialization failed: {}", e.getMessage());
            enabled = false;
        }
    }

    public void sendJobAlert(String to, List<JobDto> jobs, String jobTitle) {
        if (accountSid == null || accountSid.isBlank()) {
            System.out.println("[WhatsAppService] Twilio not configured; skipping send.");
            return;
        }

        StringBuilder msg = new StringBuilder();
        msg.append("🔥 New ").append(jobTitle).append(" jobs for you!\n\n");
        for (JobDto j : jobs) {
            msg.append("• ").append(j.getTitle()).append(" — ").append(j.getCompany())
                    .append(" (").append(j.getLocation()).append(")\n")
                    .append("Apply: ").append(j.getApplyUrl()).append("\n\n");
        }
        msg.append("💼 Visit our app for more jobs anytime.");

        Message.creator(
                new com.twilio.type.PhoneNumber("whatsapp:" + to),
                new com.twilio.type.PhoneNumber(fromNumber),
                msg.toString()
        ).create();

        System.out.printf("[WhatsAppService] sent %d job(s) to %s%n", jobs.size(), to);
    }


//    public void sendWhatsApp(String to, List<JobDto> jobs) {
//        if (!enabled) {
//            log.warn("[WhatsAppService] Skipped send to {} because Twilio is not configured", to);
//            return;
//        }
//
//        try {
//            StringBuilder body = new StringBuilder("New jobs from JobFlow:\n\n");
//            for (JobDto j : jobs) {
//                body.append(j.getTitle())
//                        .append(" @ ")
//                        .append(j.getCompany())
//                        .append("\n")
//                        .append(j.getApplyUrl())
//                        .append("\n\n");
//            }
//
//            Message.creator(
//                    new PhoneNumber("whatsapp:" + to),
//                    new PhoneNumber(fromNumber),
//                    body.toString()
//            ).create();
//
//            log.info("[WhatsAppService] Sent {} jobs to {}", jobs.size(), to);
//        } catch (Exception e) {
//            log.error("[WhatsAppService] Failed to send WhatsApp message to {}: {}", to, e.getMessage());
//        }
//    }
}
