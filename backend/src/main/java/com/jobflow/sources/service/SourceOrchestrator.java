package com.jobflow.sources.service;

import com.jobflow.sources.model.NormalizedJob;
import com.jobflow.sources.model.RawJob;
import com.jobflow.sources.ports.JobFetchPort;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SourceOrchestrator {

    private static final int DEFAULT_TTL_MIN = 10;

    private final List<JobFetchPort> adapters;

    public SourceOrchestrator(List<JobFetchPort> adapters) {
        this.adapters = adapters;
    }

    private static class CacheEntry {
        final List<NormalizedJob> items;
        final Instant expiresAt;
        CacheEntry(List<NormalizedJob> items, Instant expiresAt) {
            this.items = List.copyOf(items);
            this.expiresAt = expiresAt;
        }
    }

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private String key(String t, String l, boolean r) {
        return (t + "|" + l + "|" + r).toLowerCase();
    }


    public List<NormalizedJob> fetchAll(String jobTitle, String location, boolean remoteOnly, int maxPerSource) {
        String key = (jobTitle + "|" + location + "|" + remoteOnly).toLowerCase();
        CacheEntry hit = cache.get(key);
        if (hit != null && hit.expiresAt.isAfter(Instant.now())) {
            // serve from cache if it already has enough items
            if (hit.items.size() >= maxPerSource) return hit.items.subList(0, maxPerSource);
        }
        List<NormalizedJob> all = new ArrayList<>();
        for (JobFetchPort a : adapters) {
            List<RawJob> raws = a.fetch(jobTitle, location, remoteOnly, maxPerSource);
            List<NormalizedJob> normalized = raws.stream().map(this::normalize).toList();
            all.addAll(normalized);
        }

        List<NormalizedJob> merged = all.stream()
                .distinct()
                .sorted(Comparator.comparing(
                        NormalizedJob::getPostedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();


        List<NormalizedJob> toCache = merged.size() > DEFAULT_TTL_MIN
                ? new ArrayList<>(merged.subList(0, DEFAULT_TTL_MIN)) // avoid huge cache; copy to be safe
                : merged;
        cache.put(key, new CacheEntry(toCache, Instant.now().plus(Duration.ofMinutes(10))));

        return merged.size() > maxPerSource ? merged.subList(0, maxPerSource) : merged;


    }

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
            return UUID.randomUUID().toString();
        }
    }
}
