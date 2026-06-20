# 10 — Design a Web Crawler

> Goal: design a scalable, polite, fault-tolerant web crawler that discovers and downloads web pages for search indexing.

---

# Step 0 — What is a Web Crawler?

A web crawler, also called a spider or robot, starts from a set of seed URLs, downloads pages, extracts links, and repeats the process.

```text
Seed URLs
   |
   v
Download page
   |
   v
Extract links
   |
   v
Filter + dedupe links
   |
   v
Add new links to URL frontier
   |
   v
Repeat
```

Example:

```text
a.com
 |
 +--> b.com
 |
 +--> c.com
 |
 +--> a.com/page1
         |
         +--> a.com/page2
```

---

# Step 1 — Clarify Requirements

## Functional Requirements

- Crawl web pages starting from seed URLs.
- Download HTML pages.
- Extract links from downloaded pages.
- Discover newly added or updated pages.
- Store crawled HTML pages.
- Ignore duplicate content.
- Respect `robots.txt`.
- Support checkpointing and restart after failure.

## Non-functional Requirements

- Scalable: crawl billions of pages.
- Robust: handle bad HTML, timeouts, failures, malicious links.
- Polite: avoid overloading websites.
- Extensible: support PDFs, images, videos, FTP, etc. later.
- Fault tolerant.
- Efficient dedupe for URLs and page content.

## Scope Assumption

For this design:

```text
Content type: HTML only
Protocol: HTTP/HTTPS
Use case: Search engine indexing
Storage retention: 5 years
```

---

# Step 2 — Back-of-the-envelope Estimation

## Version A: 1 Billion Pages / Month

```text
Pages per month = 1B
Average page size = 500KB
Retention = 5 years

QPS:
1B / 30 / 24 / 3600 ≈ 400 pages/sec

Peak QPS:
2 * 400 = 800 pages/sec

Storage per month:
1B * 500KB = 500TB/month

Storage for 5 years:
500TB * 12 * 5 = 30PB
```

## Version B: 15 Billion Pages / 4 Weeks

```text
Pages to crawl = 15B
Time = 4 weeks

Pages/sec:
15B / (4 * 7 * 86400) ≈ 6200 pages/sec

Average page size = 100KB
Metadata = 500 bytes

Storage:
15B * (100KB + 500 bytes) ≈ 1.5PB

With 70% capacity usage:
1.5PB / 0.7 ≈ 2.14PB
```

## Dedupe Storage

Use 64-bit checksum per URL/content.

```text
15B URLs * 8 bytes = 120GB
15B document checksums * 8 bytes = 120GB
```

Interview line:

> The scale is large enough that URL frontier, dedupe stores, and content storage must be distributed.

---

# Step 3 — Basic Crawler Algorithm

```text
1. Start with seed URLs.
2. Pick a URL from URL frontier.
3. Resolve hostname using DNS.
4. Download page.
5. Parse page.
6. Extract links.
7. Normalize links to absolute URLs.
8. Filter unwanted links.
9. Check URL dedupe.
10. Add new URLs back to frontier.
11. Store downloaded content if not duplicate.
```

---

# Step 4 — Basic High-Level Architecture

```text
+-----------+
| Seed URLs |
+-----+-----+
      |
      v
+-------------+
| URL Frontier|
+------+------+ 
       |
       v
+-----------------+       +--------------+
| HTML Downloader | ----> | DNS Resolver |
+--------+--------+       +--------------+
         |
         v
+----------------+
| Content Parser |
+--------+-------+
         |
         v
+----------------+
| Content Seen?  |
+--------+-------+
         |
         v
+----------------+
| Content Store  |
+--------+-------+
         |
         v
+----------------+
| Link Extractor |
+--------+-------+
         |
         v
+-------------+
| URL Filter  |
+------+------+
       |
       v
+-------------+
| URL Seen?   |
+------+------+
       |
       v
+-------------+
| URL Storage |
+------+------+
       |
       v
+-------------+
| URL Frontier|
+-------------+
```

---

# Step 5 — Components One by One

## 1. Seed URLs

Seed URLs are the starting points.

Examples:

```text
https://www.wikipedia.org
https://www.apple.com
https://www.bbc.com
```

Seed selection can be based on geography, topics, popularity, and known high-quality domains.

---

## 2. URL Frontier

The URL frontier stores URLs waiting to be crawled.

```text
To be crawled:
[ url1, url2, url3, ... ]
```

It should support enqueue, dequeue, prioritization, politeness, persistence, and checkpointing.

---

## 3. HTML Downloader

Downloads pages using HTTP/HTTPS.

Responsibilities:

- DNS lookup
- robots.txt check
- HTTP request
- timeout
- retry
- content-type check
- size limit

---

## 4. DNS Resolver

Converts hostname to IP.

```text
www.wikipedia.org -> 198.35.26.96
```

DNS can be slow, so use DNS cache.

```text
hostname -> IP cache
```

---

## 5. Content Parser

Parses downloaded HTML.

Responsibilities:

- validate HTML
- extract title/body/meta
- ignore malformed pages
- pass clean content to next stage

---

## 6. Content Seen?

Detect duplicate content.

```text
contentHash = hash(normalizedHtml)
```

If content hash already exists, discard page. Otherwise, store content and process links.

---

## 7. Link Extractor

Extracts links from HTML.

Example:

```html
<a href="/wiki/System_design">System Design</a>
```

Convert relative URL to absolute URL:

```text
Base: https://en.wikipedia.org
Relative: /wiki/System_design
Absolute: https://en.wikipedia.org/wiki/System_design
```

---

## 8. URL Filter

Filters unwanted URLs.

```text
Block:
- non-HTTP links
- logout links
- admin paths
- unsupported file types
- blacklisted domains
- very long URLs
- spider traps
```

---

## 9. URL Seen?

Avoid adding the same URL multiple times.

Use hash set, Bloom filter, or persistent checksum store.

```text
if URL already seen:
    ignore
else:
    add to frontier
```

---

# Step 6 — BFS vs DFS

The web can be modeled as a directed graph.

```text
Page = node
Link = edge
```

## DFS

```text
a.com -> page1 -> page2 -> page3 -> ...
```

Problem:

- can go very deep
- may get stuck in a site
- bad for broad discovery

## BFS

```text
a.com
 |
 +-- page1
 +-- page2
 +-- page3
```

Better for crawlers because it discovers a broad set of pages and works naturally with FIFO frontier.

Interview answer:

> BFS is generally preferred, but within a host, limited DFS can reduce connection overhead.

---

# Step 7 — URL Frontier Deep Dive

A simple FIFO queue is not enough.

Problems:

1. It may crawl one host too aggressively.
2. It does not prioritize important pages.
3. It does not manage freshness.
4. It must persist hundreds of millions of URLs.

So URL Frontier needs:

```text
Priority + Politeness + Freshness + Persistence
```

---

# Step 8 — Politeness Design

Crawler should not overload a website.

Rule:

```text
Only one worker should crawl a host at a time.
Add delay between requests to same host.
```

## Visual

```text
                  +--------------+
Input URLs -----> | Queue Router |
                  +------+-------+
                         |
             +-----------+-----------+
             |           |           |
             v           v           v
          +-----+     +-----+     +-----+
          | b1  |     | b2  |     | bn  |
          +--+--+     +--+--+     +--+--+
             |           |           |
             v           v           v
        Worker 1    Worker 2    Worker N
```

Each back queue contains URLs from one host.

```text
wikipedia.org -> b1
apple.com     -> b2
nike.com      -> b3
```

---

# Step 9 — Priority Design

Not all pages are equally important.

Prioritize by:

- PageRank
- domain authority
- update frequency
- traffic
- freshness
- business rules

## Visual

```text
Input URLs
    |
    v
+-------------+
| Prioritizer |
+------+------+ 
       |
+------+------+------+
|      |      |      |
v      v      v      v
f1     f2     f3     fn
high   med    low    ...
priority queues
```

High-priority queues are selected more often.

---

# Step 10 — Combined URL Frontier

Front queues handle priority. Back queues handle politeness.

```text
Input URLs
    |
    v
+-------------+
| Prioritizer |
+------+------+
       |
       v
+----------------+
| Front Queues   |
| f1, f2, ...,fn |
+------+---------+
       |
       v
+----------------------+
| Front Queue Selector |
+----------+-----------+
           |
           v
+-------------------+
| Back Queue Router |
+----------+--------+
           |
           v
+----------------+
| Back Queues    |
| b1,b2,...,bn   |
+------+---------+
       |
       v
+---------------------+
| Back Queue Selector |
+----------+----------+
           |
           v
     Worker Threads
```

Interview line:

> The URL frontier is the heart of the crawler because it controls priority, politeness, and freshness.

---

# Step 11 — Freshness / Recrawling

Pages change frequently.

Strategies:

```text
Important pages -> recrawl more often
Frequently changing pages -> recrawl more often
Rarely changing pages -> recrawl less often
```

Example:

```text
News homepage: every few minutes
Popular blog: every few hours
Static docs: every few days/weeks
```

Freshness score can depend on previous update frequency, page importance, last crawl time, and HTTP cache headers.

---

# Step 12 — Robots.txt

Before crawling a site, fetch:

```text
https://example.com/robots.txt
```

Example:

```text
User-agent: *
Disallow: /private/
Disallow: /admin/
Crawl-delay: 10
```

Crawler should cache robots.txt rules per host, refresh periodically, follow disallow rules, and respect crawl delay.

## Visual

```text
Downloader
   |
   v
Check robots cache
   |
   +-- allowed --> download page
   |
   +-- blocked --> skip URL
```

---

# Step 13 — Storage Design

Crawler stores three major things:

```text
1. URL frontier data
2. URL seen checksums
3. Document/content data
```

## URL Frontier Storage

Hybrid approach:

```text
Memory buffer + Disk queue
```

```text
Enqueue Buffer -> flush to disk
Dequeue Buffer <- load from disk
```

Why?

- memory is fast
- disk is durable and large
- frontier can contain hundreds of millions of URLs

## Content Storage

Store HTML content.

```text
Hot/recent content -> memory/cache
Most content       -> distributed object storage / disk
```

Options:

- HDFS
- S3-like object storage
- distributed file system
- blob store

---

# Step 14 — Dedupe Design

There are two kinds of dedupe.

## 1. URL Dedupe

Avoid crawling same URL twice.

```text
normalizedUrl -> hash -> seen?
```

## 2. Content Dedupe

Avoid storing same content from different URLs.

```text
htmlContent -> checksum -> seen?
```

## Visual

```text
Downloaded Page
      |
      v
Compute content hash
      |
      v
+----------------+
| Content Seen?  |
+-------+--------+
        |
   +----+----+
   |         |
  yes        no
   |         |
discard    store content
```

## Bloom Filter

Bloom filter is useful for URL seen checks.

Pros:

- memory efficient
- fast lookup

Cons:

- false positives possible
- no false negatives

Meaning:

```text
A URL may be incorrectly treated as already seen.
```

Interview line:

> Bloom filters are good for scale, but false positives mean some pages may never be crawled.

---

# Step 15 — Document Input Stream (DIS)

Problem:

Multiple modules may need to process the same downloaded document.

Solution:

```text
Document Input Stream
```

It caches downloaded content so modules can re-read it.

```text
Fetcher -> DIS -> Parser
              -> Dedupe
              -> Link Extractor
              -> Indexer
```

Small documents are cached in memory. Large documents are written to a temporary backing file.

---

# Step 16 — Distributed Crawling

To scale, distribute crawl work across many servers.

```text
                   +--------------+
                   | URL Frontier |
                   +------+-------+
                          |
        +-----------------+-----------------+
        |                 |                 |
        v                 v                 v
+--------------+   +--------------+   +--------------+
| Downloader 1 |   | Downloader 2 |   | Downloader N |
+--------------+   +--------------+   +--------------+
```

Partition URL space by hostname.

```text
server = hash(hostname) % numberOfCrawlerServers
```

Better:

```text
consistent hashing
```

Why consistent hashing?

- easier to add/remove crawler servers
- less URL redistribution
- helps fault tolerance

---

# Step 17 — Performance Optimizations

## 1. DNS Cache

DNS lookup can be slow.

```text
hostname -> IP
www.example.com -> 93.184.216.34
```

## 2. Short Timeout

Some websites are slow or broken.

```text
connect timeout: 2s
read timeout: 5s
```

If timeout, skip or retry later.

## 3. Geographic Locality

Place crawler servers closer to target regions.

```text
US crawler -> US websites
EU crawler -> EU websites
Asia crawler -> Asia websites
```

## 4. Batch Writes

Batch updates to:

- URL seen store
- Content store
- Frontier checkpoints

---

# Step 18 — Robustness and Fault Tolerance

Failures are common.

## Techniques

- checkpoint crawl state
- persist frontier queues
- retry failed downloads
- use dead-letter queue
- use consistent hashing
- validate data
- catch parsing exceptions
- monitor worker health

## Checkpointing Visual

```text
Crawler State
    |
    v
Periodic Snapshot
    |
    v
Remote Storage
```

If server fails:

```text
New server loads latest checkpoint and continues.
```

---

# Step 19 — Crawler Traps and Bad Content

## Spider Trap Example

```text
http://site.com/foo/bar/foo/bar/foo/bar/...
```

## Detection Techniques

- max URL length
- max path depth
- repeated path pattern detection
- per-host URL count limit
- query parameter normalization
- blacklist suspicious hosts
- manual review for suspicious domains

## Data Noise

Filter:

- ads
- spam pages
- duplicate boilerplate
- low-value pages
- infinite calendars
- session IDs

---

# Step 20 — Extensibility

Design modules by protocol and content type.

```text
Protocol modules:
- HTTP
- HTTPS
- FTP later

Content modules:
- HTML parser
- PDF parser later
- Image downloader later
- Video metadata parser later
```

## Visual

```text
Fetcher
   |
   v
Content Type Router
   |
   +--> HTML Processor
   |
   +--> PDF Processor
   |
   +--> Image Processor
   |
   +--> Video Processor
```

Interview line:

> I would keep protocol fetching and content processing modular so new content types can be plugged in later.

---

# Step 21 — Final Architecture

```text
                              +-------------+
                              | Seed URLs   |
                              +------+------+
                                     |
                                     v
                              +-------------+
                              | URL Frontier|
                              | Priority +  |
                              | Politeness  |
                              +------+------+
                                     |
              +----------------------+----------------------+
              |                      |                      |
              v                      v                      v
       +--------------+       +--------------+       +--------------+
       | Downloader 1 |       | Downloader 2 |       | Downloader N |
       +------+-------+       +------+-------+       +------+-------+
              |                      |                      |
              v                      v                      v
       +--------------+       +--------------+       +--------------+
       | DNS Cache    |       | Robots Cache |       | HTTP Fetcher |
       +--------------+       +--------------+       +--------------+
                                     |
                                     v
                              +-------------+
                              | DIS         |
                              +------+------+
                                     |
                                     v
                              +-------------+
                              | Parser      |
                              +------+------+
                                     |
                                     v
                              +-------------+
                              | Content     |
                              | Dedupe      |
                              +------+------+
                                     |
                    +----------------+----------------+
                    |                                 |
                    v                                 v
             +-------------+                  +---------------+
             | Content     |                  | Link Extractor|
             | Storage     |                  +-------+-------+
             +-------------+                          |
                                                      v
                                               +-------------+
                                               | URL Filter  |
                                               +------+------+
                                                      |
                                                      v
                                               +-------------+
                                               | URL Dedupe  |
                                               +------+------+
                                                      |
                                                      v
                                               +-------------+
                                               | URL Frontier|
                                               +-------------+
```

---

# Step 22 — Final Crawl Flow

```text
1. Seed URLs are added to URL frontier.
2. Frontier prioritizes URLs.
3. Frontier enforces politeness by host.
4. Downloader gets URL.
5. Downloader checks robots.txt.
6. Downloader resolves DNS.
7. Downloader fetches HTML with timeout.
8. Document is placed into DIS.
9. Parser validates and parses HTML.
10. Content dedupe checks if page content was seen.
11. New content is stored.
12. Link extractor extracts links.
13. Links are normalized into absolute URLs.
14. URL filter removes unwanted URLs.
15. URL dedupe checks whether URL was seen.
16. New URLs go back into frontier.
17. Periodic checkpoints save crawler state.
```

---

# Step 23 — Java Code: Simple URL Normalizer

```java
import java.net.URI;

public class UrlNormalizer {
    public static String normalize(String baseUrl, String link) {
        try {
            URI base = new URI(baseUrl);
            URI resolved = base.resolve(link).normalize();

            String scheme = resolved.getScheme() == null ? "http" : resolved.getScheme().toLowerCase();
            String host = resolved.getHost() == null ? "" : resolved.getHost().toLowerCase();
            int port = resolved.getPort();

            String path = resolved.getPath();
            if (path == null || path.isBlank()) {
                path = "/";
            }

            String query = resolved.getQuery();

            StringBuilder sb = new StringBuilder();
            sb.append(scheme).append("://").append(host);

            if (port != -1 && port != 80 && port != 443) {
                sb.append(":").append(port);
            }

            sb.append(path);

            if (query != null && !query.isBlank()) {
                sb.append("?").append(query);
            }

            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        String base = "https://en.wikipedia.org/wiki/Main_Page";
        String link = "/wiki/System_design";

        System.out.println(normalize(base, link));
        // https://en.wikipedia.org/wiki/System_design
    }
}
```

---

# Step 24 — Java Code: Simple URL Frontier

```java
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleUrlFrontier {
    private final Queue<String> queue = new ConcurrentLinkedQueue<>();

    public void addUrl(String url) {
        if (url != null && !url.isBlank()) {
            queue.offer(url);
        }
    }

    public String nextUrl() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
```

---

# Step 25 — Java Code: URL Seen Dedupe

```java
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UrlSeenStore {
    private final Set<String> seen = ConcurrentHashMap.newKeySet();

    public boolean markIfNew(String url) {
        return seen.add(url);
    }

    public boolean alreadySeen(String url) {
        return seen.contains(url);
    }
}
```

---

# Step 26 — Java Code: Content Checksum

```java
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

public class ContentHasher {
    public static String sha256(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Unable to hash content", e);
        }
    }

    public static void main(String[] args) {
        System.out.println(sha256("<html>Hello</html>"));
    }
}
```

---

# Step 27 — Java Code: Polite Frontier by Host

This is a simple learning version.

```java
import java.net.URI;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PoliteUrlFrontier {
    private final Map<String, Queue<String>> hostQueues = new ConcurrentHashMap<>();
    private final Map<String, Long> nextAllowedTime = new ConcurrentHashMap<>();
    private final long crawlDelayMillis;

    public PoliteUrlFrontier(long crawlDelayMillis) {
        this.crawlDelayMillis = crawlDelayMillis;
    }

    public void addUrl(String url) {
        String host = hostOf(url);
        if (host == null) return;

        hostQueues
                .computeIfAbsent(host, h -> new ConcurrentLinkedQueue<>())
                .offer(url);
    }

    public String nextUrl() {
        long now = System.currentTimeMillis();

        for (Map.Entry<String, Queue<String>> entry : hostQueues.entrySet()) {
            String host = entry.getKey();
            Queue<String> queue = entry.getValue();

            long allowedAt = nextAllowedTime.getOrDefault(host, 0L);

            if (now >= allowedAt) {
                String url = queue.poll();

                if (url != null) {
                    nextAllowedTime.put(host, now + crawlDelayMillis);
                    return url;
                }
            }
        }

        return null;
    }

    private String hostOf(String url) {
        try {
            return new URI(url).getHost().toLowerCase();
        } catch (Exception e) {
            return null;
        }
    }
}
```

---

# Step 28 — Java Code: Basic HTML Link Extractor

This regex version is simplified for interview learning. Production should use a real HTML parser like JSoup.

```java
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkExtractor {
    private static final Pattern HREF_PATTERN =
            Pattern.compile("href=[\"']([^\"'#]+)[\"']", Pattern.CASE_INSENSITIVE);

    public static List<String> extractLinks(String baseUrl, String html) {
        List<String> links = new ArrayList<>();
        Matcher matcher = HREF_PATTERN.matcher(html);

        while (matcher.find()) {
            String rawLink = matcher.group(1);
            String normalized = UrlNormalizer.normalize(baseUrl, rawLink);

            if (normalized != null) {
                links.add(normalized);
            }
        }

        return links;
    }
}
```

---

# Step 29 — Java Code: Mini Crawler Skeleton

```java
import java.util.List;

public class MiniCrawler {
    private final PoliteUrlFrontier frontier = new PoliteUrlFrontier(1000);
    private final UrlSeenStore urlSeen = new UrlSeenStore();
    private final UrlSeenStore contentSeen = new UrlSeenStore();

    public void addSeed(String url) {
        if (urlSeen.markIfNew(url)) {
            frontier.addUrl(url);
        }
    }

    public void crawlOnce() {
        String url = frontier.nextUrl();

        if (url == null) {
            return;
        }

        try {
            // Simplified. In production use HttpClient with timeout.
            String html = fakeDownload(url);

            String contentHash = ContentHasher.sha256(html);

            if (!contentSeen.markIfNew(contentHash)) {
                return;
            }

            storeContent(url, html);

            List<String> links = LinkExtractor.extractLinks(url, html);

            for (String link : links) {
                if (shouldCrawl(link) && urlSeen.markIfNew(link)) {
                    frontier.addUrl(link);
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to crawl " + url + ": " + e.getMessage());
        }
    }

    private boolean shouldCrawl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    private void storeContent(String url, String html) {
        System.out.println("Stored: " + url);
    }

    private String fakeDownload(String url) {
        return "<html><body>"
                + "<a href=\"/page1\">Page 1</a>"
                + "<a href=\"https://example.com/page2\">Page 2</a>"
                + "</body></html>";
    }

    public static void main(String[] args) {
        MiniCrawler crawler = new MiniCrawler();
        crawler.addSeed("https://example.com");

        for (int i = 0; i < 5; i++) {
            crawler.crawlOnce();
        }
    }
}
```

---

# Step 30 — FAANG Talking Points

Mention these clearly:

1. Crawler starts with seed URLs.
2. URL Frontier controls what to crawl next.
3. BFS is generally preferred over DFS.
4. Politeness prevents overloading a host.
5. Priority queues crawl important pages first.
6. Freshness decides when to recrawl pages.
7. Respect `robots.txt`.
8. DNS cache improves performance.
9. Use URL dedupe to avoid duplicate crawling.
10. Use content dedupe to avoid duplicate storage.
11. Use Bloom filters for memory-efficient dedupe if acceptable.
12. Store frontier mostly on disk with memory buffers.
13. Use consistent hashing to distribute hostnames across crawler servers.
14. Use checkpointing for failure recovery.
15. Detect crawler traps with URL length/depth limits and host limits.
16. Keep modules extensible by protocol and content type.

---

# Step 31 — One-Minute Interview Summary

> I would design the crawler around a distributed URL Frontier. Seed URLs are inserted into the frontier, which manages both priority and politeness. Download workers pull URLs from host-specific queues, respect robots.txt, use DNS cache, and fetch pages with timeouts. Downloaded content is parsed, deduped using content hashes, stored in distributed storage, and passed to a link extractor. Extracted links are normalized, filtered, checked against URL-seen storage or Bloom filters, and then added back to the frontier. For scale, I would distribute crawling by hostname using consistent hashing, persist frontier queues on disk with memory buffers, checkpoint crawler state, and use modular parsers so new content types can be added later.

---

# Quick Revision

```text
Core loop:
Seed -> Frontier -> Downloader -> Parser -> Dedupe -> Store -> Extract links -> Filter -> URL seen -> Frontier

Main design challenges:
Scale, politeness, priority, freshness, dedupe, robots.txt, fault tolerance, crawler traps.

Key components:
URL Frontier
HTML Downloader
DNS Cache
Robots Cache
Content Parser
Content Seen
URL Seen
Content Store
Link Extractor
URL Filter

Best interview phrase:
The URL Frontier is the heart of the crawler.
```
