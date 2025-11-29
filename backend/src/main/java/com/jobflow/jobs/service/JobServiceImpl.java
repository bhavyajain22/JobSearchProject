package com.jobflow.jobs.service;

import com.jobflow.common.dto.PageResponse;
import com.jobflow.jobs.dto.FacetsResponse;
import com.jobflow.jobs.dto.JobDto;
import com.jobflow.jobs.mapper.JobMapper;
import com.jobflow.jobs.model.Preferences;
import com.jobflow.preferences.dto.PreferenceDto;
import com.jobflow.preferences.service.PreferenceService;
import com.jobflow.sources.model.NormalizedJob;
import com.jobflow.sources.service.SourceOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
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
            stream = stream.filter(j -> {
                String postedAtStr = j.getPostedAt();
                if (postedAtStr == null || postedAtStr.isBlank()) return false;
                try {
                    Instant postedAtInstant = Instant.parse(postedAtStr);
                    return postedAtInstant.toEpochMilli() >= cutoff;
                } catch (Exception e) {
                    return false; // skip invalid timestamps
                }
            });        }

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

    private static Instant safeParse(String s) {
        try {
            return (s == null || s.isBlank()) ? null : Instant.parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public FacetsResponse facets(
            String prefId,
            String companyContains, String sortBy
    ) {
        final PreferenceDto pref = preferenceService.getById(prefId);

        final int MAX_TOTAL = 200;
        List<NormalizedJob> all = orchestrator.fetchAll(
                pref.getJobTitle(),
                pref.getLocation(),
                pref.isRemoteOnly(),
                MAX_TOTAL
        );

        var st = all.stream();

        if (companyContains != null && !companyContains.isBlank()) {
            final String q = companyContains.toLowerCase();
            st = st.filter(j -> j.getCompany() != null && j.getCompany().toLowerCase().contains(q));
        }

        List<NormalizedJob> filtered = st.toList();

        if ("recency".equalsIgnoreCase(sortBy)) {
            filtered = filtered.stream()
                    .sorted(Comparator.comparing(
                            NormalizedJob::getPostedAt,
                            Comparator.nullsLast(Comparator.reverseOrder())
                    ))
                    .toList();
        }

        // Source counts
        Map<String, Integer> sourceCounts = filtered.stream()
                .collect(Collectors.groupingBy(
                        j -> j.getSource() == null ? "unknown" : j.getSource().toLowerCase(),
                        Collectors.reducing(0, e -> 1, Integer::sum)
                ));
        // ensure missing keys appear as 0 for UI
        for (String s : List.of("adzuna", "remotive", "naukri")) sourceCounts.putIfAbsent(s, 0);

        // Recency buckets: any, 1,3,7,14,30 (by postedAt)
        Map<String, Integer> recency = new LinkedHashMap<>();
        recency.put("1",  0);
        recency.put("3",  0);
        recency.put("7",  0);
        recency.put("14", 0);
        recency.put("30", 0);
        recency.put("any", filtered.size());

        long now = System.currentTimeMillis();
        for (NormalizedJob j : filtered) {
            Instant p = safeParse(j.getPostedAt());;
            if (p == null) continue;
            long days = (now - p.toEpochMilli()) / (24L * 60L * 60L * 1000L);
            if (days <= 1)  recency.computeIfPresent("1",  (k, v) -> v + 1);
            if (days <= 3)  recency.computeIfPresent("3",  (k, v) -> v + 1);
            if (days <= 7)  recency.computeIfPresent("7",  (k, v) -> v + 1);
            if (days <= 14) recency.computeIfPresent("14", (k, v) -> v + 1);
            if (days <= 30) recency.computeIfPresent("30", (k, v) -> v + 1);
        }

        return new FacetsResponse(sourceCounts, recency, filtered.size());
    }
}
