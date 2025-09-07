package com.jobflow.jobs.controller;

import com.jobflow.common.dto.PageResponse;
import com.jobflow.jobs.dto.JobDto;
import com.jobflow.jobs.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")   // <— important: plain /jobs
@RequiredArgsConstructor
public class JobsController {

    private final JobService jobService;

    public JobsController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public PageResponse<JobDto> search(
            @RequestParam String prefId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            // server-side filters
            @RequestParam(required = false, defaultValue = "all") String source,      // all|adzuna|remotive|naukri
            @RequestParam(required = false) Integer postedWithinDays,                 // 1|3|7|14|30
            @RequestParam(required = false) String companyContains,
            @RequestParam(required = false, defaultValue = "relevance") String sortBy // relevance|recency
    ) {
        return jobService.search(prefId, page, size, source, postedWithinDays, companyContains, sortBy);
    }
}
