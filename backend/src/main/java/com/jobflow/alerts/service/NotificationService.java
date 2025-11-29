package com.jobflow.alerts.service;

import com.jobflow.alerts.model.SavedSearch;
import com.jobflow.jobs.dto.JobDto;

import java.util.List;

public interface NotificationService {
    void sendNotification(SavedSearch savedSearch, List<JobDto> jobs);
}
