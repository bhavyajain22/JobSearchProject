package com.jobflow.jobs.mapper;

import com.jobflow.jobs.dto.JobDto;
import com.jobflow.sources.model.NormalizedJob;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {
    public JobDto toDto(NormalizedJob n) {
        return new JobDto(
            n.getId(),
            n.getTitle(),
            n.getCompany(),
            n.getLocation(),
            n.getSource(),
            n.getApplyUrl(),
            n.getPostedAt()
        );
    }
}
