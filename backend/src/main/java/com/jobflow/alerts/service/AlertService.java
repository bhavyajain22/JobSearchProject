package com.jobflow.alerts.service;

import com.jobflow.alerts.model.AlertChannel;
import com.jobflow.alerts.model.SavedSearch;
import com.jobflow.alerts.repo.SavedSearchRepository;
import com.jobflow.jobs.dto.JobDto;
import com.jobflow.jobs.service.JobService;
import com.jobflow.preferences.service.PreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private final SavedSearchRepository repo;
    private final JobService jobService;
    private final PreferenceService preferenceService;
    private final NotificationService notificationService;

    public AlertService(SavedSearchRepository repo, JobService jobService, PreferenceService preferenceService, NotificationService notificationService) {
        this.repo = repo;
        this.jobService = jobService;
        this.preferenceService = preferenceService;
        this.notificationService = notificationService;
    }

    public List<SavedSearch> getAll() {
        return repo.findAll();
    }

    @Transactional
    public SavedSearch update(String id, SavedSearch updated) {
        return repo.findById(id).map(existing -> {
            existing.setChannel(updated.getChannel());
            existing.setFrequency(updated.getFrequency());
            existing.setContact(updated.getContact());
            return repo.save(existing);
        }).orElseThrow(() -> new RuntimeException("Alert not found: " + id));
    }

    @Transactional
    public void delete(String id) {
        repo.deleteById(id);
    }

    @Transactional
    public SavedSearch saveAlert(String prefId, String contact, AlertChannel channel, String frequencyStr) {
        SavedSearch.AlertFrequency frequency = SavedSearch.AlertFrequency.DAILY;
        try {
            if (frequencyStr != null) {
                frequency = SavedSearch.AlertFrequency.valueOf(frequencyStr.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            System.out.printf("[AlertService] invalid frequency: %s, defaulting to DAILY%n", frequencyStr);
        }

        SavedSearch s = new SavedSearch();
        s.setPrefId(prefId);
        s.setContact(contact);
        s.setChannel(channel);
        s.setFrequency(frequency);
        s.setCreatedAt(Instant.now());

        return repo.save(s);
    }



    private boolean shouldSendNow(SavedSearch s) {
        if (s.getLastSentAt() == null) return true;

        long daysSinceLast = Duration.between(s.getLastSentAt(), Instant.now()).toDays();
        return switch (s.getFrequency()) {
            case DAILY -> daysSinceLast >= 1;
            case EVERY_3_DAYS -> daysSinceLast >= 3;
            case WEEKLY -> daysSinceLast >= 7;
        };
    }

    // run every 30 minutes (configurable)
    @Scheduled(fixedRateString = "${alerts.poll.rate-ms:6000}")
    public void processAlerts() {

        List<SavedSearch> list = repo.findAll();
        if (list.isEmpty()) {
            System.out.println("[AlertService] no saved searches to process.");
            return;
        }

        System.out.printf("[AlertService] processing %d saved searches%n", list.size());

        for (SavedSearch s : list) {
            if (!shouldSendNow(s)) {
                System.out.printf("[AlertService] skipping %s (next due later)%n", s.getId());
                continue;
            }

            try {
                handleOne(s);
                s.setLastSentAt(Instant.now());
                repo.save(s);

            } catch (Exception ex) {
                System.err.printf("[AlertService] failed for %s: %s%n", s.getId(), ex.getMessage());
            }
        }
    }

    private static Instant safeParse(String s) {
        try {
            return (s == null || s.isBlank()) ? null : Instant.parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    private void handleOne(SavedSearch s) {
        try {
            var pref = preferenceService.getById(s.getPrefId());
            if (pref == null) {
                System.out.printf("[AlertService] preference not found for savedSearch.id=%s prefId=%s. Removing saved search.%n",
                        s.getId(), s.getPrefId());

                try {
                    repo.deleteById(s.getId());
                    System.out.printf("[AlertService] deleted savedSearch %s because pref %s missing%n", s.getId(), s.getPrefId());
                } catch (Exception ex) {
                    System.err.printf("[AlertService] failed to delete savedSearch %s: %s%n", s.getId(), ex.getMessage());
                }
                return;
            }

            // ✅ Fetch jobs based on preference
            final int MAX = 200;
            var pageResponse = jobService.search(
                    s.getPrefId(), 0, MAX, "all", null, null, "recency"
            );

            List<JobDto> results = pageResponse.getItems() == null ? List.of() : pageResponse.getItems();

            // ✅ Filter new jobs
            Instant last = s.getLastSentAt();
            List<JobDto> newJobs;
            if (last == null) {
                // first time sending
                newJobs = results.stream().limit(10).collect(Collectors.toList());
            } else {
                newJobs = results.stream()
                        .filter(j -> {
                            String postedAtStr = j.getPostedAt();
                            if (postedAtStr == null || postedAtStr.isBlank()) return false;
                            try {
                                Instant p = Instant.parse(postedAtStr);
                                return p.isAfter(last);
                            } catch (Exception e) {
                                return false;
                            }
                        })
                        .collect(Collectors.toList());
            }

            // ✅ Send notifications (email/whatsapp) using notificationService
            if (!newJobs.isEmpty()) {
                try {
                    notificationService.sendNotification(s, newJobs);
                    s.setLastSentAt(Instant.now());
                    repo.save(s);
                    System.out.printf("[AlertService] sent %d jobs for savedSearch=%s%n", newJobs.size(), s.getId());
                } catch (Exception ex) {
                    System.err.printf("[AlertService] failed to send notifications for %s: %s%n", s.getId(), ex.getMessage());
                }
            } else {
                System.out.printf("[AlertService] no new jobs to send for savedSearch=%s%n", s.getId());
            }

        } catch (Exception e) {
            System.err.printf("[AlertService] unexpected error processing savedSearch %s (prefId=%s): %s%n",
                    s.getId(), s.getPrefId(), e.getMessage());
        }
    }


}
