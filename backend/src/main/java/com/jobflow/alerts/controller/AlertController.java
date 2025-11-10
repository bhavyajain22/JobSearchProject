package com.jobflow.alerts.controller;

import com.jobflow.alerts.model.AlertChannel;
import com.jobflow.alerts.model.SavedSearch;
import com.jobflow.alerts.service.AlertService;
import com.jobflow.alerts.service.EmailService;
import com.jobflow.alerts.service.WhatsAppService;
import com.jobflow.jobs.service.JobServiceImpl;
import com.jobflow.preferences.service.PreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    private final AlertService alertService;

    private PreferenceService preferenceService;

    private JobServiceImpl jobService;

    private EmailService emailService;

    public AlertController(AlertService alertService, PreferenceService preferenceService, JobServiceImpl jobService, EmailService emailService, WhatsAppService whatsAppService) {
        this.alertService = alertService;
        this.preferenceService = preferenceService;
        this.jobService = jobService;
        this.emailService = emailService;
        this.whatsAppService = whatsAppService;
    }

    private WhatsAppService whatsAppService;

    // POST /alerts?prefId=...&contact=...&channel=EMAIL
    @PostMapping
    public ResponseEntity<SavedSearch> create(
            @RequestParam String prefId,
            @RequestParam String contact,
            @RequestParam AlertChannel channel,
            @RequestParam(defaultValue = "DAILY") String frequency

    ) {
        SavedSearch s = alertService.saveAlert(prefId, contact, channel, String.valueOf(frequency));
        return ResponseEntity.ok(s);
    }

    @GetMapping
    public List<SavedSearch> listAll() {
        return alertService.getAll();
    }

    @PutMapping("/{id}")
    public SavedSearch update(
            @PathVariable String id,
            @RequestBody SavedSearch updated) {
        return alertService.update(id, updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        alertService.delete(id);
    }

    @GetMapping("/test")
    public String sendTestAlert(@RequestParam String prefId, @RequestParam(defaultValue = "EMAIL") AlertChannel channel) {
        try {
            var pref = preferenceService.getById(prefId);
            if (pref == null) return "Preference not found for prefId=" + prefId;

            // get jobs

            var pageResponse = jobService.search(prefId, 0, 10, "all", null, null, "recency");
            var jobs = pageResponse.getItems();
            if (jobs == null || jobs.isEmpty()) return "No jobs found for testing.";

            // send to a hardcoded test email (or use pref contact)
            String testEmail = "jain.bhavya21@gmail.com";
            String testWhatsApp = "+918239844578";

            if (channel == AlertChannel.EMAIL)
                emailService.sendJobAlert(testEmail, jobs, pref.getJobTitle());
            else
                whatsAppService.sendJobAlert(testWhatsApp, jobs, pref.getJobTitle());

            return "✅ Test alert sent successfully via " + channel;
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Failed to send test alert: " + e.getMessage();
        }
    }

}
