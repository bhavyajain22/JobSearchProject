package com.jobflow.alerts.service;

import com.jobflow.alerts.model.SavedSearch;
import com.jobflow.alerts.repo.SavedSearchRepository;
import com.jobflow.jobs.dto.JobDto;
import com.jobflow.jobs.service.JobService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AlertService {

    private final SavedSearchRepository repo;
    private final JobService jobService;
    private final JavaMailSender mailSender;

    public AlertService(SavedSearchRepository repo, JobService jobService, JavaMailSender mailSender) {
        this.repo = repo;
        this.jobService = jobService;
        this.mailSender = mailSender;
    }

    @Transactional
    public SavedSearch saveSearch(String email, String prefId) {
        SavedSearch s = SavedSearch.builder()
                .userEmail(email)
                .prefId(prefId)
                .createdAt(Instant.now())
                .lastRun(null)
                .build();
        return repo.save(s);
    }

    // runs every hour
    @Scheduled(cron = "0 0 * * * *")
    public void processAlerts() {
        List<SavedSearch> all = repo.findAll();
        for (SavedSearch s : all) {
            // fetch jobs with prefId
            var results = jobService.search(s.getPrefId(), 0, 10, "all", 1, null, "recency");
            // last 24h
            if (!results.getItems().isEmpty()) {
                sendEmail(s.getUserEmail(), results.getItems());
                s.setLastRun(Instant.now());
                repo.save(s);
            }
        }
    }

    private void sendEmail(String to, List<JobDto> jobs) {
        try {
            var msg = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(msg, true);
            helper.setTo(to);
            helper.setSubject("New job alerts for you");
            StringBuilder body = new StringBuilder("<h2>New jobs</h2><ul>");
            for (JobDto j : jobs) {
                body.append("<li>")
                        .append(j.getTitle()).append(" @ ").append(j.getCompany())
                        .append(" — <a href=\"").append(j.getApplyUrl()).append("\">Apply</a></li>");
            }
            body.append("</ul>");
            helper.setText(body.toString(), true);
            mailSender.send(msg);
        } catch (Exception e) {
            System.err.println("[AlertService] email error: " + e.getMessage());
        }
    }
}
