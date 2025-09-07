package com.jobflow.jobs.service;

import com.jobflow.common.dto.PageResponse;
import com.jobflow.jobs.dto.JobDto;
import com.jobflow.jobs.mapper.JobMapper;
import com.jobflow.jobs.model.Preferences;
import com.jobflow.preferences.dto.PreferenceDto;
import com.jobflow.preferences.service.PreferenceService;
import com.jobflow.sources.model.NormalizedJob;
import com.jobflow.sources.service.SourceOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final PreferenceService preferenceService;
    private final SourceOrchestrator orchestrator;
    private final JobMapper mapper;

    public JobServiceImpl(PreferenceService preferenceService, SourceOrchestrator orchestrator, JobMapper mapper) {
        this.preferenceService = preferenceService;
        this.orchestrator = orchestrator;
        this.mapper = mapper;
    }

    @Override
    public PageResponse<JobDto> search(
            String prefId, int page, int size,
            String source, Integer postedWithinDays, String companyContains, String sortBy
    ) {
        // 1) Load preference
        final PreferenceDto pref = preferenceService.getById(prefId);

        // 2) Fetch a generous pool (cached by orchestrator)
        final int MAX_TOTAL = 200;
        List<NormalizedJob> all = orchestrator.fetchAll(
                pref.getJobTitle(),
                pref.getLocation(),
                pref.isRemoteOnly(),
                MAX_TOTAL
        );

        // 3) Apply filters over the FULL set
        var stream = all.stream();

        // source filter
        if (source != null && !"all".equalsIgnoreCase(source)) {
            final String src = source.toLowerCase();
            stream = stream.filter(j -> j.getSource() != null && j.getSource().equalsIgnoreCase(src));
        }

        // postedWithinDays (keep undated jobs to avoid hiding data; switch predicate to exclude nulls if you prefer)
        if (postedWithinDays != null && postedWithinDays > 0) {
            long cutoff = System.currentTimeMillis() - postedWithinDays * 24L * 60L * 60L * 1000L;
            stream = stream.filter(j -> j.getPostedAt() == null || j.getPostedAt().toEpochMilli() >= cutoff);
        }

        // companyContains (case-insensitive)
        if (companyContains != null && !companyContains.isBlank()) {
            final String q = companyContains.toLowerCase();
            stream = stream.filter(j -> (j.getCompany() != null) && j.getCompany().toLowerCase().contains(q));
        }

        List<NormalizedJob> filtered = stream.toList();

        // 4) Sort
        if ("recency".equalsIgnoreCase(sortBy)) {
            filtered = filtered.stream()
                    .sorted(Comparator.comparing(
                            NormalizedJob::getPostedAt,
                            Comparator.nullsLast(Comparator.reverseOrder())
                    ))
                    .toList();
        } // relevance => keep orchestrator order

        // 5) Slice page & map
        int total = filtered.size();
        int from = Math.min(page * size, total);
        int to = Math.min(from + size, total);

        List<JobDto> items = filtered.subList(from, to)
                .stream()
                .map(mapper::toDto)
                .toList();

        return PageResponse.of(items, page, size, total);
    }
}
