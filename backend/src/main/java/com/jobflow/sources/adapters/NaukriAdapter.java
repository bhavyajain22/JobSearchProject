// backend/src/main/java/com/jobflow/sources/adapters/NaukriAdapter.java
package com.jobflow.sources.adapters;

import com.jobflow.sources.model.RawJob;
import com.jobflow.sources.ports.JobFetchPort;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;


@Component
public class NaukriAdapter implements JobFetchPort {

    private final HttpClient http;
    private final NaukriProperties props;
    private final CookieManager cookieManager ;

    // Simple in-memory cache: key -> Entry(list, expiresAt)
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public NaukriAdapter(NaukriProperties props) {
        this.props = props;
        this.cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        this.http = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

    }

    @Override
    public String sourceKey() { return "naukri"; }

    private void bootstrapCookies() {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(props.getBaseUrl()))
                    .timeout(Duration.ofSeconds(10))
                    .header("User-Agent", props.getUserAgent())
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Upgrade-Insecure-Requests", "1")
                    // DO NOT set "Connection" / "Host" / "Content-Length" here
                    .GET()
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.printf("[NaukriAdapter] bootstrap %s -> %d%n", props.getBaseUrl(), resp.statusCode());
        } catch (Exception e) {
            System.out.println("[NaukriAdapter] bootstrap failed: " + e.getMessage());
        }
    }

    @Override
    public List<RawJob> fetch(String jobTitle, String location, boolean remoteOnly, int max) {
        bootstrapCookies();
        int hardCap = Math.min(max, props.getMaxResults());
        String key = cacheKey(jobTitle, location, remoteOnly, hardCap);

        // 1) Cache hit?
        CacheEntry hit = cache.get(key);
        if (hit != null && hit.expiresAt.isAfter(Instant.now())) {
            return hit.items;
        }

        // 2) Build URLs (try path style first, then query style)
        String urlA = buildPathStyleUrl(jobTitle, location, remoteOnly);
        String urlB = buildQueryStyleUrl(jobTitle, location, remoteOnly);

        List<RawJob> out = new ArrayList<>();
        try {
            politeDelay();

            // Try A
            String htmlA = get(urlA);
            List<RawJob> a = parse(htmlA, jobTitle, location, hardCap);
            out.addAll(a);

            if (out.isEmpty()) {
                // Try B if A yielded nothing
                politeDelay();
                String htmlB = get(urlB);
                List<RawJob> b = parse(htmlB, jobTitle, location, hardCap);
                out.addAll(b);
            }
        } catch (Exception e) {
            // Log and continue with empty list
            System.err.println("[NaukriAdapter] fetch error: " + e.getMessage());
        }

        // 3) Cache
        List<RawJob> limited = out.size() > hardCap ? out.subList(0, hardCap) : out;
        cache.put(key, new CacheEntry(limited, Instant.now().plus(Duration.ofMinutes(props.getCacheTtlMinutes()))));
        return limited;
    }

    // ----------------- helpers -----------------

    private String cacheKey(String t, String l, boolean r, int m) {
        return String.join("|", Optional.ofNullable(t).orElse(""),
                Optional.ofNullable(l).orElse(""), String.valueOf(r), String.valueOf(m));
    }

    private String buildPathStyleUrl(String title, String loc, boolean remoteOnly) {
        // https://www.naukri.com/java-developer-jobs-in-gurgaon
        String qt = normalizeForPath(title);
        String ql = (loc == null || loc.isBlank()) ? "" : "-in-" + normalizeForPath(loc);
        String rem = remoteOnly ? "-remote" : "";
        return props.getBaseUrl() + "/" + qt + "-jobs" + ql + rem;
    }

    private String buildQueryStyleUrl(String title, String loc, boolean remoteOnly) {
        // https://www.naukri.com/jobs?k=java%20developer&l=gurgaon
        String k = urlEncode(Objects.toString(title, "").trim());
        String l = urlEncode(Objects.toString(loc, "").trim());
        String base = props.getBaseUrl() + "/jobs?k=" + k;
        if (!l.isBlank()) base += "&l=" + l;
        if (remoteOnly) base += "&remote=1"; // if they support a remote param
        return base;
    }

    private String normalizeForPath(String s) {
        return Objects.toString(s, "")
                .trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    private String urlEncode(String s) {
        return URLEncoder.encode(Objects.toString(s, ""), StandardCharsets.UTF_8);
    }

    private String get(String url) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .header("User-Agent", props.getUserAgent())
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Referer", props.getBaseUrl())
                .header("Upgrade-Insecure-Requests", "1")
                // DO NOT set "Connection", "Host", "Content-Length" (restricted)
                .GET()
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        int sc = resp.statusCode();
        String body = resp.body();
        System.out.printf("[NaukriAdapter] GET %s -> %d (%d bytes)%n", url, sc, body == null ? 0 : body.length());

        if (sc == 403) {
            // Log a short preview so we know if it's a bot wall
            String preview = body == null ? "" : body.substring(0, Math.min(400, body.length())).replaceAll("\\s+"," ");
            System.out.println("[NaukriAdapter] 403 body preview: " + preview);
        }
        if (sc >= 200 && sc < 300) return body;
        throw new IOException("HTTP " + sc + " for " + url);
    }


    private void politeDelay() {
        int ms = Math.max(0, props.getMinDelayMs());
        if (ms > 0) try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    private List<RawJob> parse(String html, String jobTitle, String location, int cap) {
        if (html == null || html.isBlank()) return List.of();
        Document doc = Jsoup.parse(html);

        // We’ll attempt multiple layouts. If one yields 0, try the next.

        List<RawJob> results = new ArrayList<>();

        // Layout A: common job tuple cards
        results.addAll(parseLayoutA(doc, cap));

        // Layout B: alternative card structure
        if (results.isEmpty()) {
            results.addAll(parseLayoutB(doc, cap));
        }

        // Layout C: very generic fallback (anchors likely to be job links)
        if (results.isEmpty()) {
            results.addAll(parseFallback(doc, jobTitle, location, cap));
        }

        return results;
    }

    // ---- Layout A (example selectors; tweak if site changes) ----
    private List<RawJob> parseLayoutA(Document doc, int cap) {
        List<RawJob> out = new ArrayList<>();
        // Try a specific container that looks like a job card
        Elements cards = doc.select("article.jobTuple, div.jobTuple, div.list > article");
        for (Element card : cards) {
            String title = textOr(card.selectFirst("a.title, a[title]"));
            String company = textOr(card.selectFirst(".subTitle, .companyInfo span, a.company"));
            String loc = textOr(card.selectFirst(".ellipsis.loc, .location, .loc"));
            String url = hrefOr(card.selectFirst("a.title, a[href*=\"/job-\"], a[href*=\"/jobs/\"]"));
            Instant postedAt = parsePostedAt(card.selectFirst(".type br + span, .type, .date, .posted, time"));

            if (url == null || title == null) continue;
            out.add(new RawJob("naukri", title, nonNull(company), nonNull(loc), absolutize(url), postedAt, null));
            if (out.size() >= cap) break;
        }
        return out;
    }

    // ---- Layout B (another common set of selectors) ----
    private List<RawJob> parseLayoutB(Document doc, int cap) {
        List<RawJob> out = new ArrayList<>();
        Elements cards = doc.select("div.jobTuple_bg, li.listing-item, div.srp-jobtuple");
        for (Element card : cards) {
            String title = textOr(card.selectFirst("a.title, a[title], a[href*=\"/job-\"]"));
            String company = textOr(card.selectFirst(".subTitle, .company, .companyName"));
            String loc = textOr(card.selectFirst(".location, .loc"));
            String url = hrefOr(card.selectFirst("a[href*=\"/job-\"], a[href*=\"/jobs/\"]"));
            Instant postedAt = parsePostedAt(card.selectFirst("time, .date, .posted"));

            if (url == null || title == null) continue;
            out.add(new RawJob("naukri", title, nonNull(company), nonNull(loc), absolutize(url), postedAt, null));
            if (out.size() >= cap) break;
        }
        return out;
    }

    // ---- Layout C (generic fallback) ----
    private List<RawJob> parseFallback(Document doc, String jobTitle, String location, int cap) {
        List<RawJob> out = new ArrayList<>();
        // Look for likely job links: anchors containing job terms and pointing within naukri
        Elements anchors = doc.select("a[href*=\"naukri.com/\"]");
        for (Element a : anchors) {
            String text = a.text();
            String href = a.absUrl("href");
            if (text == null || text.isBlank()) continue;
            if (href == null || href.isBlank()) continue;
            // Heuristic: likely job links contain '/job-' or '/jobs/' and have meaningful text
            if ((href.contains("/job-") || href.contains("/jobs/")) && text.length() > 5) {
                out.add(new RawJob("naukri", text, "", nonNull(location), href, null, null));
                if (out.size() >= cap) break;
            }
        }
        return out;
    }

    private String textOr(Element el) { return el == null ? null : el.text().trim(); }
    private String hrefOr(Element el) { return el == null ? null : el.hasAttr("href") ? el.attr("href").trim() : null; }
    private String nonNull(String s) { return s == null ? "" : s.trim(); }

    private String absolutize(String href) {
        if (href == null) return null;
        if (href.startsWith("http://") || href.startsWith("https://")) return href;
        if (href.startsWith("//")) return "https:" + href;
        if (href.startsWith("/")) return props.getBaseUrl() + href;
        return href;
    }

    private Instant parsePostedAt(Element el) {
        if (el == null) return null;
        String t = el.text().toLowerCase(Locale.ROOT);
        // Handle simple forms like '2 days ago', '1 day ago', '30+ days ago'
        try {
            if (t.contains("day")) {
                int n = extractNumber(t);
                if (n <= 0) n = 1;
                return Instant.now().minus(Duration.ofDays(n));
            }
            if (t.contains("hour")) {
                int n = extractNumber(t);
                if (n <= 0) n = 1;
                return Instant.now().minus(Duration.ofHours(n));
            }
            if (t.contains("minute")) {
                int n = extractNumber(t);
                if (n <= 0) n = 5;
                return Instant.now().minus(Duration.ofMinutes(n));
            }
            // ISO time tag fallback: <time datetime="...">
            String dt = el.hasAttr("datetime") ? el.attr("datetime") : null;
            if (dt != null && !dt.isBlank()) {
                try { return ZonedDateTime.parse(dt).toInstant(); } catch (DateTimeParseException ignored) {}
            }
        } catch (Exception ignored) { }
        return null;
    }

    private int extractNumber(String s) {
        // First number in string, e.g., "Posted 3 days ago" => 3
        try {
            var m = Pattern.compile("(\\d+)").matcher(s);
            if (m.find()) return Integer.parseInt(m.group(1));
        } catch (Exception ignored) {}
        return -1;
    }

    private static class CacheEntry {
        final List<RawJob> items;
        final Instant expiresAt;
        CacheEntry(List<RawJob> items, Instant expiresAt) {
            this.items = List.copyOf(items);
            this.expiresAt = expiresAt;
        }
    }
}
