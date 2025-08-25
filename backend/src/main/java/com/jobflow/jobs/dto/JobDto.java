package com.jobflow.jobs.dto;

import java.time.Instant;

public class JobDto {
    private String id;
    private String title;
    private String company;
    private String location;
    private String source;
    private String applyUrl;
    private Instant postedAt;

    public JobDto() {}

    public JobDto(String id, String title, String company, String location, String source, String applyUrl, Instant postedAt) {
        this.id = id; this.title = title; this.company = company; this.location = location; this.source = source; this.applyUrl = applyUrl; this.postedAt = postedAt;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public String getLocation() { return location; }
    public String getSource() { return source; }
    public String getApplyUrl() { return applyUrl; }
    public Instant getPostedAt() { return postedAt; }

    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setCompany(String company) { this.company = company; }
    public void setLocation(String location) { this.location = location; }
    public void setSource(String source) { this.source = source; }
    public void setApplyUrl(String applyUrl) { this.applyUrl = applyUrl; }
    public void setPostedAt(Instant postedAt) { this.postedAt = postedAt; }
}
