package com.jobflow.jobs.service;

import com.jobflow.common.dto.PageResponse;
import com.jobflow.jobs.dto.FacetsResponse;
import com.jobflow.jobs.dto.JobDto;

public interface JobService {
    PageResponse<JobDto> search(
            String prefId, int page, int size,
            String source, Integer postedWithinDays, String companyContains, String sortBy
    );

    FacetsResponse facets(
            String prefId,
            // optional “partial filters” to scope the facet universe (e.g. companyContains)
            String companyContains, String sortBy
    );
}
