package com.jobflow.sources.model;

import java.time.Instant;

public class RawJob {
    private String source;
    private String title;
    private String company;
    private String location;
    private String applyUrl;
    private Instant postedAt;
    private String description;

    public RawJob() {}

    public RawJob(String source, String title, String company, String location, String applyUrl, Instant postedAt, String description) {
        this.source = source;
        this.title = title;
        this.company = company;
        this.location = location;
        this.applyUrl = applyUrl;
        this.postedAt = postedAt;
        this.description = description;
    }

    public String getSource() { return source; }
    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public String getLocation() { return location; }
    public String getApplyUrl() { return applyUrl; }
    public Instant getPostedAt() { return postedAt; }
    public String getDescription() { return description; }

    public void setSource(String source) { this.source = source; }
    public void setTitle(String title) { this.title = title; }
    public void setCompany(String company) { this.company = company; }
    public void setLocation(String location) { this.location = location; }
    public void setApplyUrl(String applyUrl) { this.applyUrl = applyUrl; }
    public void setPostedAt(Instant postedAt) { this.postedAt = postedAt; }
    public void setDescription(String description) { this.description = description; }
}
