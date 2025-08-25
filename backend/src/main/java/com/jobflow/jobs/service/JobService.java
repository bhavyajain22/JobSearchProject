package com.jobflow.jobs.service;

import com.jobflow.common.dto.PageResponse;
import com.jobflow.jobs.dto.JobDto;

public interface JobService {
    PageResponse<JobDto> search(String jobTitle, String location, boolean remoteOnly, int page, int size);
}
