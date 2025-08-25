// backend/src/main/java/com/jobflow/sources/adapters/NaukriProperties.java
package com.jobflow.sources.adapters;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sources.naukri")
public class NaukriProperties {
    /**
     * Base hostname (change if they redirect).
     * e.g., https://www.naukri.com
     */
    private String baseUrl = "https://www.naukri.com";

    /** Cache TTL in minutes */
    private int cacheTtlMinutes = 15;

    /** Max results per source (safety cap) */
    private int maxResults = 50;

    /** Optional tiny delay (ms) to be polite */
    private int minDelayMs = 300;

    /** Custom UA */
    private String userAgent = "JobFlowBot/0.1 (learning project; contact: you@example.com)";

    // getters/setters
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public int getCacheTtlMinutes() { return cacheTtlMinutes; }
    public void setCacheTtlMinutes(int cacheTtlMinutes) { this.cacheTtlMinutes = cacheTtlMinutes; }
    public int getMaxResults() { return maxResults; }
    public void setMaxResults(int maxResults) { this.maxResults = maxResults; }
    public int getMinDelayMs() { return minDelayMs; }
    public void setMinDelayMs(int minDelayMs) { this.minDelayMs = minDelayMs; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
}
