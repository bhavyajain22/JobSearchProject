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
public class RemotiveAdapter implements JobFetchPort {

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String sourceKey() { return "remotive"; }

    @Override
    public List<RawJob> fetch(String jobTitle, String location, boolean remoteOnly, int max) {
        try {
            // Remotive API: https://remotive.com/api/remote-jobs?search=java%20developer
            // It’s a remote‑only board; ignore location unless you want to filter client‑side.
            String q = url(Objects.toString(jobTitle, "").trim());
            String url = "https://remotive.com/api/remote-jobs?search=" + q;

            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .header("User-Agent", "JobFlowLearning/0.1")
                    .GET().build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                System.out.printf("[RemotiveAdapter] HTTP %d for %s%n", resp.statusCode(), url);
                return List.of();
            }

            JsonNode root = mapper.readTree(resp.body());
            JsonNode jobs = root.get("jobs");
            List<RawJob> out = new ArrayList<>();
            if (jobs != null && jobs.isArray()) {
                for (JsonNode j : jobs) {
                    String title = text(j, "title");
                    String company = text(j, "company_name");
                    String apply = text(j, "url");
                    String created = text(j, "publication_date"); // ISO
                    Instant postedAt = null;
                    try { if (created != null) postedAt = ZonedDateTime.parse(created).toInstant(); } catch (Exception ignored) {}

                    if (title == null || apply == null) continue;
                    // Remotive is remote by nature; location often empty → keep user’s location if provided
                    String loc = Objects.toString(location, "").trim();
                    out.add(new RawJob("remotive", title, nn(company), nn(loc), apply, postedAt, null));
                    if (out.size() >= max) break;
                }
            }
            System.out.printf("[RemotiveAdapter] returning %d items for (%s, %s, remote=%s)%n",
                    out.size(), jobTitle, location, remoteOnly);
            return out;
        } catch (Exception e) {
            System.out.println("[RemotiveAdapter] error: " + e.getMessage());
            return List.of();
        }
    }

    private static String url(String s) { return URLEncoder.encode(s, StandardCharsets.UTF_8); }
    private static String text(JsonNode n, String f) { var x = n.get(f); return x!=null && !x.isNull() ? x.asText() : null; }
    private static String nn(String s) { return s == null ? "" : s.trim(); }
}
