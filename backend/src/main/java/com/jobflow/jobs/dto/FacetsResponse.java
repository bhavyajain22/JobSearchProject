package com.jobflow.jobs.dto;

import java.util.Map;

public class FacetsResponse {
    private Map<String, Integer> sourceCounts;   // e.g., {"adzuna": 42, "remotive": 5, "naukri": 0}
    private Map<String, Integer> recencyCounts;  // e.g., {"1": 3, "3": 10, "7": 25, "14": 35, "30": 52, "any": 60}
    private int total;

    public FacetsResponse() {}

    public FacetsResponse(Map<String, Integer> sourceCounts, Map<String, Integer> recencyCounts, int total) {
        this.sourceCounts = sourceCounts;
        this.recencyCounts = recencyCounts;
        this.total = total;
    }

    public Map<String, Integer> getSourceCounts() { return sourceCounts; }
    public void setSourceCounts(Map<String, Integer> sourceCounts) { this.sourceCounts = sourceCounts; }

    public Map<String, Integer> getRecencyCounts() { return recencyCounts; }
    public void setRecencyCounts(Map<String, Integer> recencyCounts) { this.recencyCounts = recencyCounts; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
}
