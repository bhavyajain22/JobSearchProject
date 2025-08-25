package com.jobflow.jobs.service;

import com.jobflow.common.dto.PageResponse;
import com.jobflow.jobs.dto.JobDto;
import com.jobflow.jobs.mapper.JobMapper;
import com.jobflow.sources.service.SourceOrchestrator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    private final SourceOrchestrator orchestrator;
    private final JobMapper mapper;

    public JobServiceImpl(SourceOrchestrator orchestrator, JobMapper mapper) {
        this.orchestrator = orchestrator;
        this.mapper = mapper;
    }

    @Override
    public PageResponse<JobDto> search(String jobTitle, String location, boolean remoteOnly, int page, int size) {
        final int MAX_TOTAL = 200;
        var list = orchestrator.fetchAll(jobTitle, location, remoteOnly, MAX_TOTAL)
                .stream()
                .map(mapper::toDto)
                .toList();
        int from = Math.min(page * size, list.size());
        int to = Math.min(from + size, list.size());
        List<JobDto> slice = list.subList(from, to);
        return PageResponse.of(slice, page, size, list.size());
    }
}
