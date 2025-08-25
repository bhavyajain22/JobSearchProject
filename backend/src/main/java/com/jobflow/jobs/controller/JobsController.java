// backend/src/main/java/com/jobflow/jobs/controller/JobsController.java
package com.jobflow.jobs.controller;

import com.jobflow.common.dto.PageResponse;
import com.jobflow.jobs.dto.JobDto;
import com.jobflow.jobs.service.JobService;
import com.jobflow.preferences.dto.PreferenceDto;
import com.jobflow.preferences.service.PreferenceService;
import com.jobflow.common.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")   // <—— NO /api here

public class JobsController {


    private final JobService jobService;
    private final PreferenceService preferenceService;

    public JobsController(JobService jobService, PreferenceService preferenceService) {
        this.jobService = jobService;
        this.preferenceService = preferenceService;
    }

    @GetMapping
    public PageResponse<JobDto> search(
            @RequestParam String prefId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PreferenceDto pref = preferenceService.get(prefId);
        if (pref == null) throw AppException.notFound("Preference not found: " + prefId);

        return jobService.search(
                pref.getJobTitle(),
                pref.getLocation(),
                pref.isRemoteOnly(),
                page, size
        );
    }
}
