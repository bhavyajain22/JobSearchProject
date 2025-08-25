// backend/src/main/java/com/jobflow/sources/adapters/AdzunaProperties.java
package com.jobflow.sources.adapters;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sources.adzuna")
public class AdzunaProperties {
    private boolean enabled = true;
    private String baseUrl = "https://api.adzuna.com/v1/api/jobs/in/search";
    private String appId;
    private String appKey;
    private int resultsPerPage = 20;
    private String userAgent = "JobFlowLearning/0.1";

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getAppKey() { return appKey; }
    public void setAppKey(String appKey) { this.appKey = appKey; }
    public int getResultsPerPage() { return resultsPerPage; }
    public void setResultsPerPage(int resultsPerPage) { this.resultsPerPage = resultsPerPage; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
}
