package com.jobflow.sources.service;

import com.jobflow.common.cache.SimpleCache;
import com.jobflow.sources.model.NormalizedJob;
import com.jobflow.sources.model.RawJob;
import com.jobflow.sources.ports.JobFetchPort;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
public class SourceOrchestrator {

    // Cache ~10 minutes; store at most 200 normalized jobs per query key
    private static final int CACHE_TTL_MIN = 10;
    private static final int MAX_CACHE_ITEMS = 200;

    private final List<JobFetchPort> adapters;
    private final SimpleCache<String, List<NormalizedJob>> cache =
            new SimpleCache<>(Duration.ofMinutes(CACHE_TTL_MIN));

    public SourceOrchestrator(List<JobFetchPort> adapters) {
        this.adapters = adapters;
    }

    @PostConstruct
    void logAdapters() {
        System.out.println("[Orchestrator] adapters detected: " +
                adapters.stream().map(JobFetchPort::sourceKey).toList());
    }

    private String key(String title, String location, boolean remoteOnly) {
        String t = Objects.toString(title, "").trim().toLowerCase(Locale.ROOT);
        String l = Objects.toString(location, "").trim().toLowerCase(Locale.ROOT);
        return t + "|" + l + "|" + remoteOnly;
    }

    /**
     * Fan-out to all sources, merge, normalize, de-dup, sort desc by postedAt.
     * Caches a capped, normalized list per (title|location|remoteOnly).
     *
     * @param jobTitle    title to search
     * @param location    location filter (can be blank)
     * @param remoteOnly  remote-only flag
     * @param max         max number of items the caller wants back (cap on the returned list)
     */
    public List<NormalizedJob> fetchAll(String jobTitle, String location, boolean remoteOnly, int max) {
        final int safeMax = Math.max(1, max); // guard against 0/negative

        String cacheKey = key(jobTitle, location, remoteOnly);

        // Compute and cache (if miss/expired)
        List<NormalizedJob> merged = cache.getOrCompute(cacheKey, () -> {
            // ---- Fan-out to adapters ----
            List<RawJob> raw = new ArrayList<>();
            for (JobFetchPort a : adapters) {
                try {
                    // Ask each adapter for up to MAX_CACHE_ITEMS; adapter may page internally
                    List<RawJob> part = a.fetch(jobTitle, location, remoteOnly, MAX_CACHE_ITEMS);
                    System.out.printf("[Orchestrator] %s returned %d items%n",
                            a.sourceKey(), part == null ? 0 : part.size());
                    if (part != null) raw.addAll(part);
                } catch (Exception e) {
                    System.out.printf("[Orchestrator] %s error: %s%n", a.sourceKey(), e.getMessage());
                }
            }

            // ---- Normalize → de-dup → sort (desc by postedAt) ----
            return raw.stream()
                    .map(this::normalize)
                    .distinct()
                    .sorted(Comparator.comparing(
                            NormalizedJob::getPostedAt,
                            Comparator.nullsLast(Comparator.reverseOrder())
                    ))
                    .limit(MAX_CACHE_ITEMS) // cap what we store to avoid huge memory
                    .toList();
        });

        // ---- Return a view capped to the requested max ----
        if (merged.size() > safeMax) {
            // Copy to avoid returning a live subList view
            return new ArrayList<>(merged.subList(0, safeMax));
        }
        return merged;
    }

    // ---- Helpers ----

    private NormalizedJob normalize(RawJob r) {
        String id = sha1(r.getSource() + "|" + r.getApplyUrl());
        return new NormalizedJob(
                id,
                r.getTitle(),
                r.getCompany(),
                r.getLocation(),
                r.getSource(),
                r.getApplyUrl(),
                r.getPostedAt()
        );
    }

    private static String sha1(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            // Extremely unlikely; fall back to random id to avoid breaking flow
            return UUID.randomUUID().toString();
        }
    }
}
