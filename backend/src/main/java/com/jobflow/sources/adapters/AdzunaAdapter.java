// backend/src/main/java/com/jobflow/sources/adapters/AdzunaAdapter.java
package com.jobflow.sources.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobflow.sources.model.RawJob;
import com.jobflow.sources.ports.JobFetchPort;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class AdzunaAdapter implements JobFetchPort {

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final AdzunaProperties props;

    public AdzunaAdapter(AdzunaProperties props) {
        this.props = props;
    }

    @Override
    public String sourceKey() { return "adzuna"; }

    @Override
    public List<RawJob> fetch(String jobTitle, String location, boolean remoteOnly, int max) {
        if (!props.isEnabled() || isBlank(props.getAppId()) || isBlank(props.getAppKey())) {
            System.out.println("[AdzunaAdapter] disabled or missing keys — skipping");
            return List.of();
        }

        try {
            int page = 1; // Adzuna pages start at 1
            int rpp = Math.min(props.getResultsPerPage(), Math.max(1, 50)); // keep per-page sane
            List<RawJob> out = new ArrayList<>();

            while (out.size() < max && page <= 10) { // safety page cap
                String url = buildUrl(page, rpp, jobTitle, location, remoteOnly);
                HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                        .header("User-Agent", props.getUserAgent())
                        .GET()
                        .build();

                HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
                if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                    System.out.printf("[AdzunaAdapter] HTTP %d for %s%n", resp.statusCode(), url);
                    break;
                }

                JsonNode root = mapper.readTree(resp.body());
                JsonNode results = root.get("results");
                int before = out.size();
                if (results != null && results.isArray()) {
                    for (JsonNode r : results) {
                        String title = getText(r, "title");
                        String company = getText(r.path("company"), "display_name");
                        String locDisp = getText(r.path("location"), "display_name");
                        String applyUrl = getText(r, "redirect_url");
                        String created = getText(r, "created");
                        String postedAt = null;
                        try { if (created != null) postedAt = String.valueOf(ZonedDateTime.parse(created).toInstant()); } catch (Exception ignored) {}

                        if (title == null || applyUrl == null) continue;
                        out.add(new RawJob("adzuna", title, nonNull(company), nonNull(locDisp), applyUrl, postedAt, null));
                        if (out.size() >= max) break;
                    }
                }
                int added = out.size() - before;
                if (added == 0) break; // no more results
                page++;
            }
            System.out.printf("[AdzunaAdapter] returning %d items for (%s, %s, remote=%s)%n",
                    out.size(), jobTitle, location, remoteOnly);
            return out;
        } catch (Exception e) {
            System.out.println("[AdzunaAdapter] error: " + e.getMessage());
            return List.of();
        }
    }


    private String buildUrl(int page, int rpp, String jobTitle, String location, boolean remoteOnly) {
        String base = props.getBaseUrl();
        // Example: https://api.adzuna.com/v1/api/jobs/in/search/1?app_id=...&app_key=...&results_per_page=20&what=java+developer&where=Bengaluru
        StringBuilder sb = new StringBuilder();
        sb.append(base.endsWith("/") ? base : base + "/").append(page)
                .append("?app_id=").append(url(props.getAppId()))
                .append("&app_key=").append(url(props.getAppKey()))
                .append("&results_per_page=").append(rpp)
                .append("&content-type=application/json");

        if (!isBlank(jobTitle)) sb.append("&what=").append(url(jobTitle));
        if (!isBlank(location)) sb.append("&where=").append(url(location));
        if (remoteOnly) sb.append("&what_and=remote"); // loose filter

        return sb.toString();
    }

    private static String url(String s) { return URLEncoder.encode(Objects.toString(s, ""), StandardCharsets.UTF_8); }
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static String nonNull(String s) { return s == null ? "" : s.trim(); }
    private static String getText(JsonNode node, String field) {
        JsonNode x = node.get(field);
        return (x != null && !x.isNull()) ? x.asText() : null;
    }
}
