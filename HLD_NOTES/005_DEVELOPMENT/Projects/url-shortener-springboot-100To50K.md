# URL Shortener in Spring Boot: Monolith to 50K RPS

This document is a **production-oriented baseline** for building a URL shortener in **Spring Boot** from scratch and evolving it in stages:

- **Stage 0:** Monolith baseline
- **Stage 1:** ~100 RPS
- **Stage 2:** ~1K RPS
- **Stage 3:** ~10K RPS
- **Stage 4:** ~20K RPS
- **Stage 5:** ~50K RPS

It includes:

- architecture progression
- data model
- API design
- Spring Boot code samples
- Redis cache
- Kafka analytics pipeline
- background cleanup queue
- replication and sharding strategy
- deployment guidance
- performance checklist

## Important scope note

A single markdown file cannot realistically contain every source file for a real 50K RPS production system. What this gives you is a **working, production-style blueprint with runnable code skeletons**, concrete class structures, and stage-by-stage upgrades that you can directly turn into a repository.

---

# 1. Core Requirements

## Functional

- Create short URL from long URL
- Redirect short code to original URL
- Optional custom alias
- Expiration support
- Delete URL
- Analytics tracking

## Non-functional

- High availability
- Low latency redirect path
- Horizontally scalable
- Non-guessable keys
- Observability and abuse protection

---

# 2. Recommended Tech Stack

## Application

- Java 21
- Spring Boot 3.x
- Spring Web
- Spring Data JPA / JDBC
- Spring Validation
- Spring Security (later stages)
- Spring Data Redis
- Spring for Apache Kafka
- Micrometer + Prometheus
- Resilience4j

## Storage

### Stage 0 to 1
- PostgreSQL primary DB

### Stage 2+
- PostgreSQL with read replicas, or CockroachDB/YugabyteDB for easier horizontal scale

### Stage 4+
- Sharded storage:
  - PostgreSQL shards by `short_code_hash`
  - or Cassandra / DynamoDB if you want easier write/read scale

## Cache

- Redis

## Messaging

- Kafka for analytics + async cleanup + async cache warming

## Infra

- Docker Compose for local dev
- Kubernetes for production
- Nginx / Envoy / ALB

---

# 3. API Contract

## Create Short URL

`POST /api/v1/urls`

Request:

```json
{
  "originalUrl": "https://example.com/very/long/path",
  "customAlias": null,
  "expiresAt": "2027-01-01T00:00:00Z"
}
```

Response:

```json
{
  "shortCode": "aZ91Kd",
  "shortUrl": "https://sho.rt/aZ91Kd",
  "originalUrl": "https://example.com/very/long/path",
  "expiresAt": "2027-01-01T00:00:00Z",
  "createdAt": "2026-01-01T10:00:00Z"
}
```

## Redirect

`GET /{shortCode}`

Response:
- `302 Found` with `Location` header
- `404 Not Found`
- `410 Gone` if expired

## Delete

`DELETE /api/v1/urls/{shortCode}`

## Analytics

`GET /api/v1/urls/{shortCode}/analytics`

---

# 4. Data Model

## URL mapping table

```sql
CREATE TABLE short_urls (
    id BIGSERIAL PRIMARY KEY,
    short_code VARCHAR(16) NOT NULL UNIQUE,
    original_url TEXT NOT NULL,
    url_hash VARCHAR(64),
    user_id VARCHAR(64),
    is_custom BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ,
    last_accessed_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_short_urls_expires_at ON short_urls(expires_at);
CREATE INDEX idx_short_urls_status ON short_urls(status);
CREATE INDEX idx_short_urls_user_id_created_at ON short_urls(user_id, created_at DESC);
```

## Analytics aggregation table

```sql
CREATE TABLE url_analytics_daily (
    short_code VARCHAR(16) NOT NULL,
    day DATE NOT NULL,
    click_count BIGINT NOT NULL DEFAULT 0,
    unique_visitors BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (short_code, day)
);
```

## Optional raw event sink table

```sql
CREATE TABLE url_click_events (
    id BIGSERIAL PRIMARY KEY,
    short_code VARCHAR(16) NOT NULL,
    event_time TIMESTAMPTZ NOT NULL,
    ip VARCHAR(64),
    user_agent TEXT,
    referer TEXT,
    country VARCHAR(8)
);
```

---

# 5. Key Generation Strategy

For production, prefer **random Base62 IDs**, not sequential IDs.

## Why Base62

- short
- URL-safe
- harder to guess than sequence IDs

With 8 chars:

- `62^8 ~= 218 trillion`

## Java key generator

```java
package com.example.shortener.core;

import java.security.SecureRandom;

public class Base62KeyGenerator {
    private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generate(int length) {
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = BASE62[RANDOM.nextInt(BASE62.length)];
        }
        return new String(chars);
    }
}
```

Collision handling: generate and retry on unique constraint violation.

---

# 6. Project Structure

```text
url-shortener/
├── src/main/java/com/example/shortener/
│   ├── UrlShortenerApplication.java
│   ├── api/
│   │   ├── UrlController.java
│   │   ├── RedirectController.java
│   │   └── AnalyticsController.java
│   ├── config/
│   │   ├── RedisConfig.java
│   │   ├── KafkaConfig.java
│   │   ├── AsyncConfig.java
│   │   └── WebConfig.java
│   ├── domain/
│   │   ├── ShortUrl.java
│   │   └── UrlStatus.java
│   ├── dto/
│   │   ├── CreateShortUrlRequest.java
│   │   ├── ShortUrlResponse.java
│   │   └── AnalyticsResponse.java
│   ├── repository/
│   │   ├── ShortUrlRepository.java
│   │   └── AnalyticsRepository.java
│   ├── service/
│   │   ├── UrlShorteningService.java
│   │   ├── RedirectService.java
│   │   ├── AnalyticsService.java
│   │   └── CleanupService.java
│   ├── messaging/
│   │   ├── ClickEventProducer.java
│   │   ├── ClickEventConsumer.java
│   │   └── CleanupConsumer.java
│   ├── security/
│   │   ├── RateLimitFilter.java
│   │   └── ApiKeyFilter.java
│   └── support/
│       ├── Base62KeyGenerator.java
│       ├── UrlValidator.java
│       └── Hashing.java
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/
├── docker-compose.yml
└── k8s/
```

---

# 7. Stage 0: Monolith Baseline

Use this to get correct behavior before optimizing.

## Architecture

- single Spring Boot app
- PostgreSQL
- no Redis yet
- no Kafka yet
- synchronous analytics count update

## Suitable load

- dev / QA / initial internal release
- maybe tens of RPS

## Entity

```java
package com.example.shortener.domain;

public enum UrlStatus {
    ACTIVE,
    EXPIRED,
    DELETED
}
```

```java
package com.example.shortener.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "short_urls")
public class ShortUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code", nullable = false, unique = true, length = 16)
    private String shortCode;

    @Column(name = "original_url", nullable = false, columnDefinition = "TEXT")
    private String originalUrl;

    @Column(name = "url_hash", length = 64)
    private String urlHash;

    @Column(name = "user_id", length = 64)
    private String userId;

    @Column(name = "is_custom", nullable = false)
    private boolean custom;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private UrlStatus status = UrlStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "last_accessed_at")
    private Instant lastAccessedAt;

    @Version
    private Long version;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(Instant.now());
    }

    // getters and setters omitted for brevity
}
```

## Repository

```java
package com.example.shortener.repository;

import com.example.shortener.domain.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    Optional<ShortUrl> findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);
}
```

## DTOs

```java
package com.example.shortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.Instant;

public record CreateShortUrlRequest(
    @NotBlank String originalUrl,
    @Pattern(regexp = "^[a-zA-Z0-9_-]{0,16}$", message = "Invalid custom alias")
    String customAlias,
    Instant expiresAt
) {}
```

```java
package com.example.shortener.dto;

import java.time.Instant;

public record ShortUrlResponse(
    String shortCode,
    String shortUrl,
    String originalUrl,
    Instant expiresAt,
    Instant createdAt
) {}
```

## Service

```java
package com.example.shortener.service;

import com.example.shortener.core.Base62KeyGenerator;
import com.example.shortener.domain.ShortUrl;
import com.example.shortener.domain.UrlStatus;
import com.example.shortener.dto.CreateShortUrlRequest;
import com.example.shortener.dto.ShortUrlResponse;
import com.example.shortener.repository.ShortUrlRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.Instant;

@Service
public class UrlShorteningService {

    private final ShortUrlRepository repository;
    private final Base62KeyGenerator keyGenerator = new Base62KeyGenerator();

    @Value("${app.base-url}")
    private String baseUrl;

    public UrlShorteningService(ShortUrlRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ShortUrlResponse create(CreateShortUrlRequest request, String userId) {
        validateUrl(request.originalUrl());

        String shortCode = request.customAlias();
        boolean custom = shortCode != null && !shortCode.isBlank();

        if (custom && repository.existsByShortCode(shortCode)) {
            throw new IllegalArgumentException("Custom alias already exists");
        }

        int attempts = 0;
        while (attempts++ < 5) {
            try {
                if (!custom) {
                    shortCode = keyGenerator.generate(8);
                }

                ShortUrl shortUrl = new ShortUrl();
                shortUrl.setShortCode(shortCode);
                shortUrl.setOriginalUrl(request.originalUrl());
                shortUrl.setUserId(userId);
                shortUrl.setCustom(custom);
                shortUrl.setStatus(UrlStatus.ACTIVE);
                shortUrl.setExpiresAt(request.expiresAt());

                ShortUrl saved = repository.save(shortUrl);

                return new ShortUrlResponse(
                    saved.getShortCode(),
                    baseUrl + "/" + saved.getShortCode(),
                    saved.getOriginalUrl(),
                    saved.getExpiresAt(),
                    saved.getCreatedAt()
                );
            } catch (DataIntegrityViolationException ex) {
                if (custom) {
                    throw new IllegalArgumentException("Custom alias already exists");
                }
            }
        }

        throw new IllegalStateException("Failed to generate unique short code");
    }

    private void validateUrl(String originalUrl) {
        URI uri = URI.create(originalUrl);
        if (uri.getScheme() == null || uri.getHost() == null) {
            throw new IllegalArgumentException("Invalid URL");
        }
    }
}
```

## Redirect service

```java
package com.example.shortener.service;

import com.example.shortener.domain.ShortUrl;
import com.example.shortener.domain.UrlStatus;
import com.example.shortener.repository.ShortUrlRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class RedirectService {

    private final ShortUrlRepository repository;
    private final AnalyticsService analyticsService;

    public RedirectService(ShortUrlRepository repository, AnalyticsService analyticsService) {
        this.repository = repository;
        this.analyticsService = analyticsService;
    }

    @Transactional
    public String resolve(String shortCode, String ip, String userAgent, String referer) {
        ShortUrl shortUrl = repository.findByShortCode(shortCode)
            .orElseThrow(() -> new ResourceNotFoundException("Short code not found"));

        if (shortUrl.getStatus() == UrlStatus.DELETED) {
            throw new ResourceNotFoundException("Short code deleted");
        }

        if (shortUrl.isExpired()) {
            shortUrl.setStatus(UrlStatus.EXPIRED);
            throw new LinkExpiredException("Short code expired");
        }

        shortUrl.setLastAccessedAt(java.time.Instant.now());
        analyticsService.recordClick(shortCode, ip, userAgent, referer);
        return shortUrl.getOriginalUrl();
    }
}
```

## Controllers

```java
package com.example.shortener.api;

import com.example.shortener.dto.CreateShortUrlRequest;
import com.example.shortener.dto.ShortUrlResponse;
import com.example.shortener.service.UrlShorteningService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {

    private final UrlShorteningService service;

    public UrlController(UrlShorteningService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShortUrlResponse create(@Valid @RequestBody CreateShortUrlRequest request) {
        return service.create(request, "anonymous");
    }
}
```

```java
package com.example.shortener.api;

import com.example.shortener.service.RedirectService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class RedirectController {

    private final RedirectService redirectService;

    public RedirectController(RedirectService redirectService) {
        this.redirectService = redirectService;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request) {
        String target = redirectService.resolve(
            shortCode,
            request.getRemoteAddr(),
            request.getHeader("User-Agent"),
            request.getHeader("Referer")
        );

        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(target))
            .header(HttpHeaders.CACHE_CONTROL, "no-store")
            .build();
    }
}
```

## application.yml

```yaml
server:
  port: 8080
  tomcat:
    threads:
      max: 200
      min-spare: 20

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/shortener
    username: shortener
    password: shortener
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        jdbc:
          batch_size: 50
        order_inserts: true
        order_updates: true
  flyway:
    enabled: true

app:
  base-url: http://localhost:8080
```

---

# 8. Stage 1: 100 RPS Production-Ready Monolith

At 100 RPS, correctness and basic resilience matter more than advanced distributed architecture.

## Additions over Stage 0

- Redis cache for redirects
- basic rate limiting
- async analytics write
- connection pooling tuning
- structured logging
- health checks
- Docker deployment

## Architecture

- 2 app instances behind load balancer
- PostgreSQL primary
- Redis single node

## Redirect path

1. check Redis `short:{code}`
2. if hit, redirect immediately
3. if miss, read DB and populate Redis
4. publish analytics async

## Redis config

```java
package com.example.shortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}
```

## Cached redirect service

```java
package com.example.shortener.service;

import com.example.shortener.domain.ShortUrl;
import com.example.shortener.domain.UrlStatus;
import com.example.shortener.repository.ShortUrlRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedirectService {

    private final ShortUrlRepository repository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ClickEventProducer clickEventProducer;

    public RedirectService(
        ShortUrlRepository repository,
        RedisTemplate<String, String> redisTemplate,
        ClickEventProducer clickEventProducer
    ) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
        this.clickEventProducer = clickEventProducer;
    }

    public String resolve(String shortCode, String ip, String userAgent, String referer) {
        String cacheKey = "short:" + shortCode;
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            clickEventProducer.publish(shortCode, ip, userAgent, referer);
            return cached;
        }

        ShortUrl shortUrl = repository.findByShortCode(shortCode)
            .orElseThrow(() -> new ResourceNotFoundException("Short code not found"));

        if (shortUrl.getStatus() == UrlStatus.DELETED) {
            throw new ResourceNotFoundException("Deleted");
        }
        if (shortUrl.isExpired()) {
            throw new LinkExpiredException("Expired");
        }

        redisTemplate.opsForValue().set(cacheKey, shortUrl.getOriginalUrl(), Duration.ofHours(24));
        clickEventProducer.publish(shortCode, ip, userAgent, referer);
        return shortUrl.getOriginalUrl();
    }
}
```

## Simple rate limit filter

```java
package com.example.shortener.security;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ip, k -> Bucket.builder()
            .addLimit(Bandwidth.classic(200, Refill.greedy(200, Duration.ofMinutes(1))))
            .build());

        if (!bucket.tryConsume(1)) {
            response.setStatus(429);
            response.getWriter().write("Too Many Requests");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
```

## Docker Compose

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: shortener
      POSTGRES_USER: shortener
      POSTGRES_PASSWORD: shortener
    ports:
      - "5432:5432"

  redis:
    image: redis:7
    ports:
      - "6379:6379"

  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - redis
```

## At this stage

Use:
- Redis cache
- app-level rate limiting
- single primary DB
- synchronous create path
- async click events optional but recommended

---

# 9. Stage 2: 1K RPS

At 1K RPS, split hot read path from write-heavy analytics.

## Additions

- Kafka for click events
- Redis mandatory for hot path
- DB read replica
- HikariCP tuning
- async cleanup workers
- idempotent analytics consumer
- better observability

## Architecture

- 3 to 6 app instances
- PostgreSQL primary + 1 read replica
- Redis primary + replica or Redis Sentinel
- Kafka cluster (3 brokers recommended in prod)

## Request flows

### Create URL
- write to primary DB
- write-through cache optional

### Redirect
- read from Redis
- fallback to DB replica
- publish click event to Kafka
- analytics aggregation happens asynchronously

## Kafka event model

```java
package com.example.shortener.messaging;

import java.time.Instant;

public record ClickEvent(
    String shortCode,
    Instant eventTime,
    String ip,
    String userAgent,
    String referer
) {}
```

## Producer

```java
package com.example.shortener.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ClickEventProducer {

    private final KafkaTemplate<String, ClickEvent> kafkaTemplate;

    public ClickEventProducer(KafkaTemplate<String, ClickEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String shortCode, String ip, String userAgent, String referer) {
        ClickEvent event = new ClickEvent(shortCode, Instant.now(), ip, userAgent, referer);
        kafkaTemplate.send("url-click-events", shortCode, event);
    }
}
```

## Consumer

```java
package com.example.shortener.messaging;

import com.example.shortener.service.AnalyticsService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ClickEventConsumer {

    private final AnalyticsService analyticsService;

    public ClickEventConsumer(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @KafkaListener(topics = "url-click-events", groupId = "analytics-consumers")
    public void consume(ClickEvent event) {
        analyticsService.recordClick(event);
    }
}
```

## Analytics service with daily aggregation

```java
package com.example.shortener.service;

import com.example.shortener.messaging.ClickEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class AnalyticsService {

    private final JdbcTemplate jdbcTemplate;

    public AnalyticsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void recordClick(ClickEvent event) {
        jdbcTemplate.update("""
            INSERT INTO url_analytics_daily(short_code, day, click_count, unique_visitors)
            VALUES (?, ?, 1, 0)
            ON CONFLICT (short_code, day)
            DO UPDATE SET click_count = url_analytics_daily.click_count + 1
            """,
            event.shortCode(),
            Date.valueOf(event.eventTime().atZone(java.time.ZoneOffset.UTC).toLocalDate())
        );
    }
}
```

## Read replica routing

Use separate read and write data sources.

```java
public enum DataSourceType {
    READ, WRITE
}
```

```java
public class DataSourceContextHolder {
    private static final ThreadLocal<DataSourceType> CONTEXT = new ThreadLocal<>();

    public static void set(DataSourceType type) { CONTEXT.set(type); }
    public static DataSourceType get() { return CONTEXT.get(); }
    public static void clear() { CONTEXT.remove(); }
}
```

Then use routing datasource for read methods. In simpler deployments, keep read replica use at repository layer only for redirect lookup.

## Cleanup queue

Expired links should be cleaned asynchronously.

```java
public record CleanupEvent(String shortCode) {}
```

When expired link is found:
- return `410 Gone`
- publish cleanup event
- worker updates DB status and evicts Redis key

---

# 10. Stage 3: 10K RPS

At 10K RPS, the main challenge is **hot-path latency and DB protection**.

## Additions

- Redis cluster
- local in-memory cache (Caffeine) in each app node
- Kafka analytics fully decoupled
- bulk/batch consumer writes
- idempotency protection
- read replicas scaled out
- CDN or edge caching for redirect responses where safe
- aggressive metrics and tracing

## Architecture

- 10+ app instances
- Redis cluster
- primary DB + multiple replicas
- Kafka 3-5 brokers
- analytics worker pool

## Multi-level cache

### L1 cache: Caffeine inside app
- tiny latency
- protects Redis on hottest keys

### L2 cache: Redis
- shared cache across nodes

### L3: DB replica

## Caffeine config

```java
package com.example.shortener.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("shortUrls");
        manager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1_000_000)
            .expireAfterWrite(10, TimeUnit.MINUTES));
        return manager;
    }
}
```

## Optimized redirect service

```java
package com.example.shortener.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedirectService {

    private final ShortUrlReadRepository readRepository;
    private final StringRedisTemplate redisTemplate;
    private final ClickEventProducer clickEventProducer;
    private final Cache<String, String> localCache = Caffeine.newBuilder()
        .maximumSize(1_000_000)
        .expireAfterWrite(Duration.ofMinutes(10))
        .build();

    public RedirectService(
        ShortUrlReadRepository readRepository,
        StringRedisTemplate redisTemplate,
        ClickEventProducer clickEventProducer
    ) {
        this.readRepository = readRepository;
        this.redisTemplate = redisTemplate;
        this.clickEventProducer = clickEventProducer;
    }

    public String resolve(String shortCode, String ip, String userAgent, String referer) {
        String local = localCache.getIfPresent(shortCode);
        if (local != null) {
            clickEventProducer.publish(shortCode, ip, userAgent, referer);
            return local;
        }

        String redisKey = "short:" + shortCode;
        String redisValue = redisTemplate.opsForValue().get(redisKey);
        if (redisValue != null) {
            localCache.put(shortCode, redisValue);
            clickEventProducer.publish(shortCode, ip, userAgent, referer);
            return redisValue;
        }

        ShortUrlProjection row = readRepository.findActive(shortCode)
            .orElseThrow(() -> new ResourceNotFoundException("Not found"));

        if (row.isExpired()) {
            throw new LinkExpiredException("Expired");
        }

        redisTemplate.opsForValue().set(redisKey, row.originalUrl(), Duration.ofHours(24));
        localCache.put(shortCode, row.originalUrl());
        clickEventProducer.publish(shortCode, ip, userAgent, referer);
        return row.originalUrl();
    }
}
```

## Batch Kafka consumption

Use `max.poll.records`, consumer concurrency, and batch inserts.

```yaml
spring:
  kafka:
    listener:
      type: batch
      concurrency: 6
    consumer:
      max-poll-records: 1000
      properties:
        fetch.min.bytes: 1048576
```

## Batch analytics consumer

```java
@KafkaListener(topics = "url-click-events", groupId = "analytics-consumers", containerFactory = "batchKafkaListenerContainerFactory")
public void consumeBatch(List<ClickEvent> events) {
    analyticsService.recordBatch(events);
}
```

## Batch write strategy

Instead of 1 DB update per click:
- aggregate in memory for 1-5 seconds
- flush grouped counts in batch

That is critical at 10K+ RPS.

---

# 11. Stage 4: 20K RPS

At 20K RPS, a single relational cluster becomes a risk unless the read hit rate is extremely high.

## Additions

- sharding for URL metadata
- dedicated redirect read service
- dedicated write service
- separate analytics pipeline and store
- Redis cluster with replica groups
- circuit breakers and bulkheads
- regional deployments if needed

## Service split

### url-write-service
- creates URLs
- validates custom aliases
- writes to primary shard

### redirect-service
- ultra-lean service for GET `/{shortCode}`
- only cache + DB lookup + Kafka publish

### analytics-service
- consumes Kafka
- writes aggregated stats

### cleanup-service
- expires old URLs
- evicts cache

## Sharding strategy

Shard by hash of `short_code`.

```java
package com.example.shortener.sharding;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public class ShardResolver {

    private final int shardCount;

    public ShardResolver(int shardCount) {
        this.shardCount = shardCount;
    }

    public int resolve(String shortCode) {
        CRC32 crc32 = new CRC32();
        crc32.update(shortCode.getBytes(StandardCharsets.UTF_8));
        return (int) (crc32.getValue() % shardCount);
    }
}
```

## Why shard by short code

- redirect lookup key is `short_code`
- easy routing
- even distribution if keys are random

## Shard-aware repository pattern

```java
public interface ShortUrlShardClient {
    Optional<ShortUrlProjection> findByShortCode(String shortCode);
    void save(ShortUrl shortUrl);
}
```

Implementation chooses datasource based on `ShardResolver.resolve(shortCode)`.

## Redis key design

```text
short:{shortCode}              -> originalUrl
meta:{shortCode}               -> JSON blob with expiry/status
rate:create:{userId}:{minute}  -> counter
rate:redirect:{ip}:{minute}    -> counter
```

## Circuit breaker example

```java
package com.example.shortener.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .slowCallRateThreshold(50)
            .slowCallDurationThreshold(Duration.ofMillis(200))
            .minimumNumberOfCalls(20)
            .waitDurationInOpenState(Duration.ofSeconds(10))
            .build();
    }

    @Bean
    public TimeLimiterConfig timeLimiterConfig() {
        return TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofMillis(100))
            .build();
    }
}
```

---

# 12. Stage 5: 50K RPS

At 50K RPS, success depends on **very high cache hit rate**, **separation of concerns**, and **careful data topology**.

## Target architecture

- API Gateway / LB
- Redirect service fleet (stateless)
- Write service fleet
- Redis Cluster
- Sharded URL storage with replicas
- Kafka cluster
- Analytics stream processors
- Cleanup workers
- Observability stack

## Assumptions for 50K RPS

You do **not** want 50K RPS constantly hitting the DB.

Target:
- **95%+ redirects served by L1/L2 cache**
- DB only for misses and writes
- analytics completely async

## Recommended split

### Redirect Service
Critical path only:
- validate short code
- L1 cache
- Redis lookup
- shard fallback
- publish click event
- return 302

### URL Command Service
- create/delete/update URLs
- owns write path

### Analytics Pipeline
- Kafka topic with partition key = `shortCode`
- stream processor aggregates per minute/hour/day
- writes to ClickHouse / Pinot / Druid / BigQuery or PostgreSQL aggregate tables

### Admin Service
- user dashboard
- analytics queries

## Suggested infra sizing

This varies by latency target and cache hit rate, but a baseline could be:

- 20–40 redirect service pods
- 6–12 write service pods
- Redis cluster with multiple primary/replica groups
- 8–16 DB shards, each with replicas
- Kafka 5+ brokers

## Why separate analytics store

At 50K RPS, querying click analytics from the same OLTP store as redirect metadata is a bad idea.

Use:
- ClickHouse for event analytics
- Pinot/Druid for low-latency analytics dashboard
- PostgreSQL only for aggregate counters if dashboard load is low

## Example click event schema for Kafka

```json
{
  "shortCode": "aZ91Kd2Q",
  "eventTime": "2026-01-01T12:10:00Z",
  "ip": "203.0.113.11",
  "userAgent": "Mozilla/5.0",
  "referer": "https://google.com",
  "country": "US"
}
```

## Redirect controller optimized response

```java
@GetMapping("/{shortCode}")
public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request) {
    String target = redirectService.resolve(
        shortCode,
        extractClientIp(request),
        request.getHeader("User-Agent"),
        request.getHeader("Referer")
    );

    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(target))
        .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
        .build();
}
```

## Extract real client IP behind LB

```java
private String extractClientIp(HttpServletRequest request) {
    String xff = request.getHeader("X-Forwarded-For");
    if (xff != null && !xff.isBlank()) {
        return xff.split(",")[0].trim();
    }
    return request.getRemoteAddr();
}
```

## Cache stampede protection

At high scale, many concurrent misses on a hot key can flood DB.

Use Redis lock or request coalescing.

```java
public String resolveWithStampedeProtection(String shortCode) {
    String cacheKey = "short:" + shortCode;
    String value = redisTemplate.opsForValue().get(cacheKey);
    if (value != null) return value;

    String lockKey = "lock:" + shortCode;
    Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", Duration.ofSeconds(2));

    if (Boolean.TRUE.equals(locked)) {
        try {
            value = redisTemplate.opsForValue().get(cacheKey);
            if (value != null) return value;

            ShortUrlProjection row = shardRepository.findActive(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));

            redisTemplate.opsForValue().set(cacheKey, row.originalUrl(), Duration.ofHours(24));
            return row.originalUrl();
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    try {
        Thread.sleep(25);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }

    value = redisTemplate.opsForValue().get(cacheKey);
    if (value != null) return value;

    return shardRepository.findActive(shortCode)
        .map(ShortUrlProjection::originalUrl)
        .orElseThrow(() -> new ResourceNotFoundException("Not found"));
}
```

## Write path idempotency

For public APIs, add idempotency keys for create requests.

```text
Idempotency-Key: 4d6f6e6f2d6372656174652d31
```

Store request hash + response in Redis for short time window.

---

# 13. Working Redis + Kafka + Sharding Progression by Stage

## 100 RPS

- Redis: optional but recommended
- Kafka: optional
- Message queue: optional, start with async executor
- Replication: none required, maybe DB backup only
- Sharding: no

## 1K RPS

- Redis: yes, shared cache
- Kafka: yes for click events
- Message queue: Kafka for analytics + cleanup
- Replication: DB read replica, Redis replica
- Sharding: no

## 10K RPS

- Redis: mandatory, possibly cluster
- Kafka: mandatory
- Message queue: Kafka + batch consumers
- Replication: primary + multiple replicas
- Sharding: maybe not yet if cache hit rate is high, but prepare abstraction

## 20K RPS

- Redis: cluster
- Kafka: mandatory with tuned partitions
- Message queue: Kafka plus DLQ
- Replication: per-shard replicas
- Sharding: yes

## 50K RPS

- Redis: multi-node cluster
- Kafka: mandatory, partitioned at high throughput
- Message queue: Kafka + separate processing groups
- Replication: shard replicas across AZs
- Sharding: mandatory or use naturally distributed data store

---

# 14. Production Configuration Examples

## Kafka config

```java
package com.example.shortener.config;

import com.example.shortener.messaging.ClickEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, ClickEvent> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        props.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, ClickEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```

## Redis production config ideas

```yaml
spring:
  data:
    redis:
      host: redis
      port: 6379
      timeout: 100ms
      lettuce:
        pool:
          max-active: 64
          max-idle: 16
          min-idle: 8
```

## HikariCP tuning example

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 40
      minimum-idle: 10
      idle-timeout: 300000
      max-lifetime: 1800000
      connection-timeout: 1000
```

---

# 15. Security, Abuse Prevention, and Validation

## Must-have protections

- rate limiting by IP / API key / user
- URL validation
- block internal/private IP targets if needed
- malware / phishing integration for public systems
- namespace reservation for admin paths
- custom alias validation
- audit logs for delete/admin actions

## Prevent SSRF-like abuse

If your service previews URLs or does validation by connecting to targets, do not allow internal network access.

## Reserved aliases

Never allow user aliases like:

```text
api
admin
login
health
metrics
actuator
favicon.ico
```

---

# 16. Cleanup Service

Expired links should not be removed synchronously on hot path except minimal status handling.

## Scheduled cleanup

```java
package com.example.shortener.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CleanupService {

    private final JdbcTemplate jdbcTemplate;
    private final StringRedisTemplate redisTemplate;

    public CleanupService(JdbcTemplate jdbcTemplate, StringRedisTemplate redisTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(fixedDelay = 60000)
    public void cleanupExpired() {
        List<String> expiredCodes = jdbcTemplate.queryForList("""
            SELECT short_code
            FROM short_urls
            WHERE status = 'ACTIVE'
              AND expires_at IS NOT NULL
              AND expires_at < NOW()
            LIMIT 1000
            """, String.class);

        for (String code : expiredCodes) {
            jdbcTemplate.update("UPDATE short_urls SET status = 'EXPIRED' WHERE short_code = ?", code);
            redisTemplate.delete("short:" + code);
            redisTemplate.delete("meta:" + code);
        }
    }
}
```

At scale, replace scheduler scan with:
- expiration topic
- partitioned workers
- or a time-wheel / delay queue design

---

# 17. Observability

## Metrics to track

- redirect RPS
- create RPS
- P50/P95/P99 redirect latency
- Redis hit rate
- DB fallback rate
- Kafka produce latency
- Kafka consumer lag
- expired link rate
- collision retry count
- shard skew

## Spring Boot Actuator

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      probes:
        enabled: true
```

## Prometheus counters example

```java
package com.example.shortener.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class UrlMetrics {
    private final Counter redirectCounter;
    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;

    public UrlMetrics(MeterRegistry registry) {
        this.redirectCounter = registry.counter("shortener.redirect.requests");
        this.cacheHitCounter = registry.counter("shortener.redirect.cache.hit");
        this.cacheMissCounter = registry.counter("shortener.redirect.cache.miss");
    }

    public void redirect() { redirectCounter.increment(); }
    public void cacheHit() { cacheHitCounter.increment(); }
    public void cacheMiss() { cacheMissCounter.increment(); }
}
```

---

# 18. Testing Strategy

## Unit tests

- key generator
- URL validation
- alias validation
- service behavior

## Integration tests

- create and resolve flow
- expiration behavior
- Redis fallback
- Kafka event publishing

## Performance tests

Use k6 or Gatling.

### k6 redirect test

```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  scenarios: {
    redirects: {
      executor: 'constant-arrival-rate',
      rate: 1000,
      timeUnit: '1s',
      duration: '2m',
      preAllocatedVUs: 200,
      maxVUs: 1000,
    },
  },
};

export default function () {
  const res = http.get('http://localhost:8080/abc123xy', { redirects: 0 });
  check(res, { 'status is 302': r => r.status === 302 || r.status === 404 || r.status === 410 });
  sleep(0.1);
}
```

---

# 19. Kubernetes Deployment Skeleton

## redirect-service deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: redirect-service
spec:
  replicas: 6
  selector:
    matchLabels:
      app: redirect-service
  template:
    metadata:
      labels:
        app: redirect-service
    spec:
      containers:
        - name: app
          image: your-registry/redirect-service:1.0.0
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "500m"
              memory: "512Mi"
            limits:
              cpu: "2"
              memory: "2Gi"
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
```

## service

```yaml
apiVersion: v1
kind: Service
metadata:
  name: redirect-service
spec:
  selector:
    app: redirect-service
  ports:
    - port: 80
      targetPort: 8080
```

---

# 20. Stage-by-Stage Build Plan

## Phase A: Monolith baseline

Build first:
- Spring Boot app
- PostgreSQL schema
- create API
- redirect API
- delete API
- expiration check
- Flyway migrations
- tests

## Phase B: 100 RPS

Add:
- Redis cache
- rate limiting
- health checks
- Docker Compose
- structured logs

## Phase C: 1K RPS

Add:
- Kafka click events
- analytics consumer
- DB replica for reads
- cleanup worker

## Phase D: 10K RPS

Add:
- L1 + L2 cache
- consumer batching
- retry / circuit breaker
- split redirect and write modules

## Phase E: 20K RPS

Add:
- sharding abstraction
- dedicated services
- Redis cluster
- shard routing

## Phase F: 50K RPS

Add:
- full shard topology
- dedicated analytics store
- high cache hit optimization
- multi-AZ deployment
- autoscaling and SLO-based ops

---

# 21. Practical Recommendations

## Best choice if you want fastest path to working system

Start with:
- Spring Boot monolith
- PostgreSQL
- Redis
- Kafka

Then grow into:
- split redirect service
- read replica
- shard only when needed

## Best choice if you want easiest hyperscale path

Start with:
- Spring Boot services
- Redis
- Kafka
- DynamoDB/Cassandra for mapping
- ClickHouse for analytics

This reduces operational pain for 20K–50K RPS.

---

# 22. Minimal Production TODO Checklist

- [ ] Flyway migrations
- [ ] input validation
- [ ] custom alias uniqueness
- [ ] reserved alias blocklist
- [ ] Redis cache
- [ ] Kafka analytics
- [ ] cleanup worker
- [ ] rate limiting
- [ ] auth for management APIs
- [ ] metrics + dashboards
- [ ] load test scripts
- [ ] backups and DR
- [ ] canary deployment
- [ ] shard routing abstraction
- [ ] cache stampede protection
- [ ] idempotent create requests

---

# 23. Final Architecture Summary by RPS

## 100 RPS
- Spring Boot monolith
- PostgreSQL primary
- Redis optional
- no shard
- no replica required

## 1K RPS
- Spring Boot app cluster
- PostgreSQL primary + replica
- Redis shared cache
- Kafka for click events
- background cleanup

## 10K RPS
- redirect path optimized
- L1 cache + Redis
- Kafka batch analytics
- multiple DB replicas
- service split recommended

## 20K RPS
- separate redirect/write/analytics services
- Redis cluster
- sharded URL metadata
- per-shard replicas
- robust resiliency patterns

## 50K RPS
- cache-first architecture
- dedicated analytics store
- sharded metadata store
- high-throughput Kafka
- multi-AZ deployment
- strict observability and autoscaling

---

# 24. Next Step

Use this file as your baseline implementation plan. The best real development sequence is:

1. implement Stage 0 fully in one repository
2. add Redis and rate limiting
3. add Kafka analytics
4. split redirect path
5. add shard abstraction before actual sharding
6. scale infra only after load testing


---

# 22. Docker Setup (Production-Oriented)

This section gives you a practical Docker setup you can use as the baseline for local development, CI builds, and containerized deployment to Kubernetes.

## 22.1 Multi-stage Dockerfile for Spring Boot service

Use the same Dockerfile pattern for `url-shortener-app`, `redirect-service`, `write-service`, `analytics-consumer`, and `cleanup-worker`.

```dockerfile
# syntax=docker/dockerfile:1.7

FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn -q -DskipTests clean package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=builder /workspace/target/url-shortener-app-*.jar app.jar

EXPOSE 8080
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
```

## 22.2 Dockerfile for analytics-consumer

```dockerfile
# syntax=docker/dockerfile:1.7

FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests clean package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=builder /workspace/target/analytics-consumer-*.jar app.jar
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
```

## 22.3 `.dockerignore`

```gitignore
.git
.idea
.vscode
*.iml
target/
node_modules/
README.md
k6/
*.log
Dockerfile*
docker-compose*.yml
```

## 22.4 Docker Compose for local full stack

This compose stack lets you run the platform locally with PostgreSQL primary/replica placeholders, Redis, Kafka, Zookeeper, the app, analytics consumer, and cleanup worker.

```yaml
version: '3.9'

services:
  postgres-primary:
    image: postgres:16
    container_name: postgres-primary
    environment:
      POSTGRES_DB: shortener
      POSTGRES_USER: shortener
      POSTGRES_PASSWORD: shortener
    ports:
      - "5432:5432"
    volumes:
      - postgres_primary_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U shortener -d shortener"]
      interval: 10s
      timeout: 5s
      retries: 10

  postgres-replica:
    image: postgres:16
    container_name: postgres-replica
    environment:
      POSTGRES_DB: shortener
      POSTGRES_USER: shortener
      POSTGRES_PASSWORD: shortener
    ports:
      - "5433:5432"
    volumes:
      - postgres_replica_data:/var/lib/postgresql/data
    depends_on:
      postgres-primary:
        condition: service_healthy

  redis:
    image: redis:7.2-alpine
    container_name: redis
    command: ["redis-server", "--appendonly", "yes"]
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 10

  zookeeper:
    image: confluentinc/cp-zookeeper:7.6.1
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.6.1
    container_name: kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "true"

  app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: url-shortener-app
    depends_on:
      postgres-primary:
        condition: service_healthy
      redis:
        condition: service_healthy
      kafka:
        condition: service_started
    environment:
      SPRING_PROFILES_ACTIVE: docker
      DB_WRITE_URL: jdbc:postgresql://postgres-primary:5432/shortener
      DB_READ_URL: jdbc:postgresql://postgres-replica:5432/shortener
      DB_USERNAME: shortener
      DB_PASSWORD: shortener
      REDIS_HOST: redis
      REDIS_PORT: 6379
      KAFKA_BOOTSTRAP_SERVERS: kafka:9092
    ports:
      - "8080:8080"

  analytics-consumer:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: analytics-consumer
    depends_on:
      kafka:
        condition: service_started
      postgres-primary:
        condition: service_healthy
    environment:
      SPRING_PROFILES_ACTIVE: docker
      APP_ROLE: analytics-consumer
      DB_WRITE_URL: jdbc:postgresql://postgres-primary:5432/shortener
      DB_USERNAME: shortener
      DB_PASSWORD: shortener
      KAFKA_BOOTSTRAP_SERVERS: kafka:9092

  cleanup-worker:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: cleanup-worker
    depends_on:
      postgres-primary:
        condition: service_healthy
      redis:
        condition: service_healthy
    environment:
      SPRING_PROFILES_ACTIVE: docker
      APP_ROLE: cleanup-worker
      DB_WRITE_URL: jdbc:postgresql://postgres-primary:5432/shortener
      DB_USERNAME: shortener
      DB_PASSWORD: shortener
      REDIS_HOST: redis
      REDIS_PORT: 6379

volumes:
  postgres_primary_data:
  postgres_replica_data:
  redis_data:
```

## 22.5 Build and run commands

```bash
docker compose up -d --build
docker compose ps
curl -X POST http://localhost:8080/api/v1/urls \
  -H 'Content-Type: application/json' \
  -d '{"originalUrl":"https://example.com/very/long/path"}'
```

## 22.6 Image tagging in CI/CD

```bash
export IMAGE_TAG=$(git rev-parse --short HEAD)
docker build -t your-registry/url-shortener-app:${IMAGE_TAG} .
docker push your-registry/url-shortener-app:${IMAGE_TAG}
```

---

# 23. Kubernetes Setup (Production Baseline)

This section gives you a deployable Kubernetes baseline for moving from monolith to 50K RPS. Keep stateful systems like PostgreSQL, Redis Cluster, and Kafka in managed services when possible. Run stateless Spring Boot services on Kubernetes.

## 23.1 Recommended namespace

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: url-shortener
```

## 23.2 ConfigMap for application settings

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: shortener-config
  namespace: url-shortener
data:
  SPRING_PROFILES_ACTIVE: "k8s"
  REDIS_HOST: "redis-master.url-shortener.svc.cluster.local"
  REDIS_PORT: "6379"
  KAFKA_BOOTSTRAP_SERVERS: "kafka.url-shortener.svc.cluster.local:9092"
  DB_WRITE_URL: "jdbc:postgresql://postgres-primary.url-shortener.svc.cluster.local:5432/shortener"
  DB_READ_URL: "jdbc:postgresql://postgres-replica.url-shortener.svc.cluster.local:5432/shortener"
  DB_USERNAME: "shortener"
```

## 23.3 Secret for database credentials

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: shortener-secret
  namespace: url-shortener
type: Opaque
stringData:
  DB_PASSWORD: shortener
```

## 23.4 Redirect service Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: redirect-service
  namespace: url-shortener
spec:
  replicas: 6
  revisionHistoryLimit: 3
  selector:
    matchLabels:
      app: redirect-service
  template:
    metadata:
      labels:
        app: redirect-service
    spec:
      containers:
        - name: redirect-service
          image: your-registry/redirect-service:1.0.0
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
          envFrom:
            - configMapRef:
                name: shortener-config
            - secretRef:
                name: shortener-secret
          env:
            - name: JAVA_OPTS
              value: "-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
          resources:
            requests:
              cpu: "500m"
              memory: "512Mi"
            limits:
              cpu: "2"
              memory: "2Gi"
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 15
            periodSeconds: 10
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 20
          startupProbe:
            httpGet:
              path: /actuator/health
              port: 8080
            failureThreshold: 30
            periodSeconds: 10
```

## 23.5 Write service Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: write-service
  namespace: url-shortener
spec:
  replicas: 3
  selector:
    matchLabels:
      app: write-service
  template:
    metadata:
      labels:
        app: write-service
    spec:
      containers:
        - name: write-service
          image: your-registry/write-service:1.0.0
          ports:
            - containerPort: 8080
          envFrom:
            - configMapRef:
                name: shortener-config
            - secretRef:
                name: shortener-secret
          resources:
            requests:
              cpu: "300m"
              memory: "512Mi"
            limits:
              cpu: "1"
              memory: "1Gi"
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
```

## 23.6 Analytics consumer Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: analytics-consumer
  namespace: url-shortener
spec:
  replicas: 3
  selector:
    matchLabels:
      app: analytics-consumer
  template:
    metadata:
      labels:
        app: analytics-consumer
    spec:
      containers:
        - name: analytics-consumer
          image: your-registry/analytics-consumer:1.0.0
          ports:
            - containerPort: 8080
          envFrom:
            - configMapRef:
                name: shortener-config
            - secretRef:
                name: shortener-secret
          resources:
            requests:
              cpu: "300m"
              memory: "512Mi"
            limits:
              cpu: "1"
              memory: "1Gi"
```

## 23.7 Cleanup worker CronJob

```yaml
apiVersion: batch/v1
kind: CronJob
metadata:
  name: cleanup-worker
  namespace: url-shortener
spec:
  schedule: "0 */2 * * *"
  successfulJobsHistoryLimit: 2
  failedJobsHistoryLimit: 3
  jobTemplate:
    spec:
      template:
        spec:
          restartPolicy: OnFailure
          containers:
            - name: cleanup-worker
              image: your-registry/cleanup-worker:1.0.0
              envFrom:
                - configMapRef:
                    name: shortener-config
                - secretRef:
                    name: shortener-secret
              resources:
                requests:
                  cpu: "200m"
                  memory: "256Mi"
                limits:
                  cpu: "500m"
                  memory: "512Mi"
```

## 23.8 Internal Services

```yaml
apiVersion: v1
kind: Service
metadata:
  name: redirect-service
  namespace: url-shortener
spec:
  selector:
    app: redirect-service
  ports:
    - name: http
      port: 80
      targetPort: 8080
---
apiVersion: v1
kind: Service
metadata:
  name: write-service
  namespace: url-shortener
spec:
  selector:
    app: write-service
  ports:
    - name: http
      port: 80
      targetPort: 8080
---
apiVersion: v1
kind: Service
metadata:
  name: analytics-consumer
  namespace: url-shortener
spec:
  selector:
    app: analytics-consumer
  ports:
    - name: http
      port: 80
      targetPort: 8080
```

## 23.9 Ingress

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: shortener-ingress
  namespace: url-shortener
  annotations:
    nginx.ingress.kubernetes.io/proxy-read-timeout: "30"
    nginx.ingress.kubernetes.io/proxy-send-timeout: "30"
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
spec:
  ingressClassName: nginx
  rules:
    - host: s.example.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: redirect-service
                port:
                  number: 80
          - path: /api/v1/urls
            pathType: Prefix
            backend:
              service:
                name: write-service
                port:
                  number: 80
```

## 23.10 Horizontal Pod Autoscaler

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: redirect-service-hpa
  namespace: url-shortener
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: redirect-service
  minReplicas: 6
  maxReplicas: 50
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 65
```

## 23.11 PodDisruptionBudget

```yaml
apiVersion: policy/v1
kind: PodDisruptionBudget
metadata:
  name: redirect-service-pdb
  namespace: url-shortener
spec:
  minAvailable: 4
  selector:
    matchLabels:
      app: redirect-service
```

## 23.12 Anti-affinity for high availability

Add this under the pod spec of critical services:

```yaml
affinity:
  podAntiAffinity:
    preferredDuringSchedulingIgnoredDuringExecution:
      - weight: 100
        podAffinityTerm:
          topologyKey: kubernetes.io/hostname
          labelSelector:
            matchLabels:
              app: redirect-service
```

## 23.13 Kustomization example

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
namespace: url-shortener
resources:
  - namespace.yaml
  - configmap.yaml
  - secret.yaml
  - redirect-deployment.yaml
  - write-deployment.yaml
  - analytics-consumer.yaml
  - cleanup-cronjob.yaml
  - services.yaml
  - ingress.yaml
  - hpa.yaml
  - pdb.yaml
images:
  - name: your-registry/redirect-service
    newTag: 1.0.0
  - name: your-registry/write-service
    newTag: 1.0.0
  - name: your-registry/analytics-consumer
    newTag: 1.0.0
  - name: your-registry/cleanup-worker
    newTag: 1.0.0
```

## 23.14 Deploy commands

```bash
kubectl apply -k k8s/
kubectl get pods -n url-shortener
kubectl get svc -n url-shortener
kubectl get ingress -n url-shortener
kubectl rollout status deployment/redirect-service -n url-shortener
```

---

# 24. Kubernetes by Traffic Stage

## 24.1 100 RPS

- single cluster, 2 nodes minimum
- 2 app replicas
- PostgreSQL primary only
- single Redis
- Kafka optional
- use Docker Compose for dev, Kubernetes for smoke tests

## 24.2 1K RPS

- 3 to 4 redirect replicas
- 2 write replicas
- PostgreSQL primary + read replica
- Redis with persistence
- Kafka for analytics
- HPA on redirect service

## 24.3 10K RPS

- split redirect and write services
- 8 to 15 redirect replicas
- dedicated analytics consumer group
- Redis cluster or managed Redis
- managed Kafka recommended
- multiple node pools

## 24.4 20K RPS

- shard router in application layer
- shard-aware write service
- Redis cluster
- separate node pools for redirect, write, and consumers
- PodDisruptionBudgets and anti-affinity required

## 24.5 50K RPS

- multi-AZ Kubernetes cluster
- 20 to 50 redirect replicas, autoscaled
- dedicated ingress tier
- dedicated shard groups or distributed datastore
- managed Kafka with replication factor 3
- Redis cluster across AZs
- blue/green or canary release strategy
- CDN in front of redirect tier if redirect pattern allows caching of negative lookups and metadata

---

# 25. Production Notes for DB Replication and Sharding with Docker/Kubernetes

## 25.1 Replication

For production, prefer managed PostgreSQL/MySQL with built-in replication. In local Docker and basic Kubernetes, keep separate write and read JDBC URLs:

```yaml
DB_WRITE_URL: jdbc:postgresql://postgres-primary.url-shortener.svc.cluster.local:5432/shortener
DB_READ_URL: jdbc:postgresql://postgres-replica.url-shortener.svc.cluster.local:5432/shortener
```

Your Spring routing layer should:
- send create, update, delete to primary
- send redirect lookup to replica when replication lag is acceptable
- fall back to primary if replica fails or lag is too high

## 25.2 Sharding

For production sharding on Kubernetes, do not hardcode only two shards forever. Externalize shard map in config:

```yaml
shards:
  - shardId: 0
    jdbcUrl: jdbc:postgresql://postgres-shard-0:5432/shortener
  - shardId: 1
    jdbcUrl: jdbc:postgresql://postgres-shard-1:5432/shortener
  - shardId: 2
    jdbcUrl: jdbc:postgresql://postgres-shard-2:5432/shortener
  - shardId: 3
    jdbcUrl: jdbc:postgresql://postgres-shard-3:5432/shortener
```

Use the application to route by short key hash:

```java
public int shardFor(String shortCode, int shardCount) {
    return Math.floorMod(shortCode.hashCode(), shardCount);
}
```

At 20K to 50K RPS, strongly consider:
- Vitess for MySQL-based sharding
- Cassandra/ScyllaDB for distributed key-value style scaling
- managed Redis cluster
- Kafka with RF=3 and min ISR tuned for durability

## 25.3 Recommended split

- Kubernetes runs stateless services
- managed cloud services run databases, Redis, Kafka
- Docker Compose stays for local development only

---

# 26. Suggested Repo Layout for Docker + Kubernetes

```text
url-shortener/
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
├── pom.xml
├── src/
├── k8s/
│   ├── namespace.yaml
│   ├── configmap.yaml
│   ├── secret.yaml
│   ├── redirect-deployment.yaml
│   ├── write-deployment.yaml
│   ├── analytics-consumer.yaml
│   ├── cleanup-cronjob.yaml
│   ├── services.yaml
│   ├── ingress.yaml
│   ├── hpa.yaml
│   ├── pdb.yaml
│   └── kustomization.yaml
└── scripts/
    ├── build-images.sh
    ├── deploy-k8s.sh
    └── smoke-test.sh
```

---

# 25. Addendum: Keep Current Content Intact + Add PostgreSQL, Cassandra, Replication, Sharding, and Maven by Stage

This section **extends** the existing document without replacing any of the earlier architecture, code, Docker, or Kubernetes content.

Use this addendum as the concrete implementation guide for:

- PostgreSQL in relational stages
- Cassandra in NoSQL horizontal-scale stages
- replication setup patterns
- sharding strategy and application routing
- Maven dependencies for every stage

---

# 26. Stage-by-Stage Database Choice

## Stage 0: Monolith baseline

**Database**
- PostgreSQL only

**Why**
- simplest development experience
- strong consistency
- easy schema migration with Flyway
- best fit for create / redirect / delete / expiration basics

**Topology**
- single PostgreSQL instance

**Use for**
- local dev
- first production release
- low operational complexity

---

## Stage 1: ~100 RPS

**Database**
- PostgreSQL only

**Topology**
- single primary PostgreSQL
- Redis optional as read cache

**Why**
- replication is not necessary yet
- focus on correctness, indexes, cache, API contracts, metrics

---

## Stage 2: ~1K RPS

**Database**
- PostgreSQL primary + one or more read replicas

**Topology**
- write traffic -> primary
- redirect/read-heavy traffic -> replicas when acceptable
- Redis in front of DB for hot keys

**Why**
- read/write split becomes valuable
- keep transactional simplicity for writes
- replicas reduce primary load

**Recommended table split**
- `short_urls` on primary and replicas
- `click_events` out of main request path; emit to Kafka instead of updating DB synchronously

---

## Stage 3: ~10K RPS

**Database**
- PostgreSQL primary + multiple read replicas
- optional separate analytics store
- keep URL mapping in PostgreSQL initially

**Topology**
- redirect service uses cache-first lookup
- fallback to read replica
- write service uses primary
- analytics consumer writes asynchronously

**Why**
- still possible to scale with strong cache hit ratios
- avoid sharding too early unless data size or write hot spots force it

---

## Stage 4: ~20K RPS

**Database options**
### Option A: PostgreSQL sharded
- shard by `hash(short_code) % N`
- each shard has:
  - 1 primary
  - 1+ replicas

### Option B: Cassandra for URL mapping
- use Cassandra for `short_code -> original_url` resolution
- keep PostgreSQL for user/account/admin metadata if needed

**When to choose PostgreSQL sharding**
- strong familiarity with relational tooling
- manageable shard count
- moderate write scale
- need SQL and operational control

**When to choose Cassandra**
- very high read/write scale
- predictable partition-key access
- easier horizontal expansion for key-value style lookups
- willing to accept NoSQL operational model and query-first schema design

---

## Stage 5: ~50K RPS

**Preferred production direction**
- redirect path:
  - L1 in-memory cache
  - Redis cluster
  - Cassandra or sharded PostgreSQL
- write path:
  - dedicated write service
  - Kafka for non-critical side effects
- analytics:
  - Kafka -> consumer -> OLAP/analytics store

**Recommended DB strategy**
### Best general-purpose path
- PostgreSQL through 1K/10K
- introduce shard abstraction early
- migrate to sharded PostgreSQL or Cassandra at 20K+

### Best hyperscale path
- PostgreSQL for management metadata
- Cassandra for short URL mapping
- Redis cluster for hot keys
- Kafka for all async telemetry

---

# 27. Maven Dependencies by Stage

Below are **practical `pom.xml` dependency blocks** you can add incrementally.

## Stage 0 Maven dependencies

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>

    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Stage 1 additional Maven dependencies

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-cache</artifactId>
    </dependency>
</dependencies>
```

## Stage 2 additional Maven dependencies

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.retry</groupId>
        <artifactId>spring-retry</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
</dependencies>
```

## Stage 3 additional Maven dependencies

```xml
<dependencies>
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-spring-boot3</artifactId>
        <version>2.2.0</version>
    </dependency>
</dependencies>
```

## Stage 4 PostgreSQL sharding dependencies

```xml
<dependencies>
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
    </dependency>
</dependencies>
```

## Stage 4 / Stage 5 Cassandra dependencies

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-cassandra</artifactId>
    </dependency>
</dependencies>
```

## Optional gRPC / internal service communication dependencies

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
</dependencies>
```

## Suggested full dependency growth by stage

- **Stage 0:** Web + Validation + JPA + PostgreSQL + Flyway + Actuator + Tests
- **Stage 1:** Stage 0 + Redis + Cache
- **Stage 2:** Stage 1 + Kafka + Retry + AOP
- **Stage 3:** Stage 2 + Resilience4j
- **Stage 4:** Stage 3 + Hikari multi-datasource support or Cassandra starter
- **Stage 5:** Stage 4 + tracing/advanced observability libs as needed

---

# 28. PostgreSQL Implementation Details by Stage

## 28.1 Stage 0 PostgreSQL schema

```sql
CREATE TABLE short_urls (
    id BIGSERIAL PRIMARY KEY,
    short_code VARCHAR(16) NOT NULL UNIQUE,
    original_url TEXT NOT NULL,
    custom_alias BOOLEAN NOT NULL DEFAULT FALSE,
    user_id VARCHAR(64),
    expires_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_short_urls_expires_at ON short_urls(expires_at);
CREATE INDEX idx_short_urls_user_id ON short_urls(user_id);
```

## 28.2 Stage 0 `application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/url_shortener
    username: app
    password: app
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        format_sql: true
    open-in-view: false
  flyway:
    enabled: true
    locations: classpath:db/migration
```

## 28.3 Stage 1 indexing guidance

At ~100 RPS, optimize the hottest lookups:

```sql
CREATE UNIQUE INDEX uk_short_urls_short_code ON short_urls(short_code);
CREATE INDEX idx_short_urls_active_lookup ON short_urls(short_code, deleted, expires_at);
```

## 28.4 Stage 2 PostgreSQL replication topology

**Pattern**
- primary for writes
- replica for redirect DB fallback reads
- Redis first, DB second

### Example Docker Compose services for PostgreSQL primary/replica

```yaml
services:
  postgres-primary:
    image: postgres:16
    environment:
      POSTGRES_DB: url_shortener
      POSTGRES_USER: app
      POSTGRES_PASSWORD: app
    ports:
      - "5432:5432"

  postgres-replica:
    image: postgres:16
    environment:
      POSTGRES_DB: url_shortener
      POSTGRES_USER: app
      POSTGRES_PASSWORD: app
    ports:
      - "5433:5432"
```

> For real replication, wire streaming replication, Patroni, CrunchyData operator, or managed cloud replicas. The app-side code below stays valid regardless of the infra choice.

## 28.5 Stage 2 Spring Boot read/write split code

### `DataSourceType.java`

```java
package com.example.shortener.config;

public enum DataSourceType {
    WRITE,
    READ
}
```

### `DataSourceContextHolder.java`

```java
package com.example.shortener.config;

public final class DataSourceContextHolder {

    private static final ThreadLocal<DataSourceType> CONTEXT = new ThreadLocal<>();

    private DataSourceContextHolder() {
    }

    public static void set(DataSourceType type) {
        CONTEXT.set(type);
    }

    public static DataSourceType get() {
        DataSourceType type = CONTEXT.get();
        return type == null ? DataSourceType.WRITE : type;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
```

### `RoutingDataSource.java`

```java
package com.example.shortener.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.get();
    }
}
```

### `ReadOnlyRouteAspect.java`

```java
package com.example.shortener.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
public class ReadOnlyRouteAspect {

    @Around("@annotation(transactional)")
    public Object route(ProceedingJoinPoint pjp, Transactional transactional) throws Throwable {
        try {
            if (transactional.readOnly()) {
                DataSourceContextHolder.set(DataSourceType.READ);
            } else {
                DataSourceContextHolder.set(DataSourceType.WRITE);
            }
            return pjp.proceed();
        } finally {
            DataSourceContextHolder.clear();
        }
    }
}
```

### `DataSourceConfig.java`

```java
package com.example.shortener.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("app.datasource.write")
    public DataSource writeDataSource() {
        return new HikariDataSource();
    }

    @Bean
    @ConfigurationProperties("app.datasource.read")
    public DataSource readDataSource() {
        return new HikariDataSource();
    }

    @Bean
    public DataSource routingDataSource(DataSource writeDataSource, DataSource readDataSource) {
        RoutingDataSource routing = new RoutingDataSource();

        Map<Object, Object> targets = new HashMap<>();
        targets.put(DataSourceType.WRITE, writeDataSource);
        targets.put(DataSourceType.READ, readDataSource);

        routing.setDefaultTargetDataSource(writeDataSource);
        routing.setTargetDataSources(targets);
        return routing;
    }
}
```

### Stage 2 datasource config

```yaml
app:
  datasource:
    write:
      jdbc-url: jdbc:postgresql://postgres-primary:5432/url_shortener
      username: app
      password: app
      maximum-pool-size: 30
    read:
      jdbc-url: jdbc:postgresql://postgres-replica:5432/url_shortener
      username: app
      password: app
      maximum-pool-size: 60
```

### Example service usage

```java
@Service
@RequiredArgsConstructor
public class RedirectLookupService {

    private final ShortUrlRepository repository;

    @Transactional(readOnly = true)
    public Optional<ShortUrl> findActiveByCode(String code) {
        return repository.findByShortCodeAndDeletedFalse(code)
                .filter(url -> url.getExpiresAt() == null || url.getExpiresAt().isAfter(Instant.now()));
    }
}
```

## 28.6 Stage 3 PostgreSQL operational guidance

At ~10K RPS:
- increase replica count
- reduce direct DB reads with cache-first design
- emit analytics to Kafka instead of updating counters inline
- keep transactions short
- use connection pools per service
- separate redirect and write deployments

---

# 29. PostgreSQL Sharding for Stage 4 / Stage 5

## 29.1 Sharding model

Use application-level sharding by `short_code`.

```text
shard = abs(hash(short_code)) % shardCount
```

### Example
- shard 0 -> postgres-shard-0 primary + replica
- shard 1 -> postgres-shard-1 primary + replica
- shard 2 -> postgres-shard-2 primary + replica
- shard 3 -> postgres-shard-3 primary + replica

## 29.2 Shard router code

### `ShardResolver.java`

```java
package com.example.shortener.shard;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public class ShardResolver {

    private final int shardCount;

    public ShardResolver(int shardCount) {
        this.shardCount = shardCount;
    }

    public int resolve(String shortCode) {
        CRC32 crc32 = new CRC32();
        crc32.update(shortCode.getBytes(StandardCharsets.UTF_8));
        long value = crc32.getValue();
        return (int) (value % shardCount);
    }
}
```

### `ShardContextHolder.java`

```java
package com.example.shortener.shard;

public final class ShardContextHolder {

    private static final ThreadLocal<Integer> CONTEXT = new ThreadLocal<>();

    private ShardContextHolder() {
    }

    public static void set(Integer shardId) {
        CONTEXT.set(shardId);
    }

    public static Integer get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
```

### `ShardRoutingDataSource.java`

```java
package com.example.shortener.shard;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class ShardRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        Integer shardId = ShardContextHolder.get();
        return shardId == null ? 0 : shardId;
    }
}
```

### `ShardAwareLookupService.java`

```java
package com.example.shortener.service;

import com.example.shortener.shard.ShardContextHolder;
import com.example.shortener.shard.ShardResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShardAwareLookupService {

    private final ShardResolver shardResolver;
    private final RedirectLookupService redirectLookupService;

    public String resolveUrl(String shortCode) {
        int shardId = shardResolver.resolve(shortCode);
        try {
            ShardContextHolder.set(shardId);
            return redirectLookupService.findActiveByCode(shortCode)
                    .map(entity -> entity.getOriginalUrl())
                    .orElseThrow(() -> new IllegalArgumentException("Short code not found"));
        } finally {
            ShardContextHolder.clear();
        }
    }
}
```

## 29.3 Multi-shard config example

```yaml
app:
  shard-count: 4

  shards:
    0:
      write-jdbc-url: jdbc:postgresql://postgres-shard-0-primary:5432/url_shortener
      read-jdbc-url: jdbc:postgresql://postgres-shard-0-replica:5432/url_shortener
      username: app
      password: app
    1:
      write-jdbc-url: jdbc:postgresql://postgres-shard-1-primary:5432/url_shortener
      read-jdbc-url: jdbc:postgresql://postgres-shard-1-replica:5432/url_shortener
      username: app
      password: app
    2:
      write-jdbc-url: jdbc:postgresql://postgres-shard-2-primary:5432/url_shortener
      read-jdbc-url: jdbc:postgresql://postgres-shard-2-replica:5432/url_shortener
      username: app
      password: app
    3:
      write-jdbc-url: jdbc:postgresql://postgres-shard-3-primary:5432/url_shortener
      read-jdbc-url: jdbc:postgresql://postgres-shard-3-replica:5432/url_shortener
      username: app
      password: app
```

## 29.4 Sharding notes

- do not shard until you already have a routing abstraction
- keep shard count configurable
- avoid cross-shard joins
- route by `short_code` only
- user management data can stay unsharded
- cross-shard admin reporting should be async or aggregated elsewhere

---

# 30. Cassandra for Stage 4 / Stage 5

## 30.1 When Cassandra makes sense

Cassandra is a strong fit when your access pattern is mostly:

- lookup by exact short code
- write URL mapping once
- read many times
- tolerate eventual consistency for some operations
- want horizontal scale without application-managed SQL shards

## 30.2 Cassandra data model

### Keyspace

```sql
CREATE KEYSPACE IF NOT EXISTS url_shortener
WITH replication = {
  'class': 'NetworkTopologyStrategy',
  'datacenter1': 3
};
```

### URL mapping table

```sql
CREATE TABLE IF NOT EXISTS url_shortener.short_urls (
    short_code text PRIMARY KEY,
    original_url text,
    user_id text,
    expires_at timestamp,
    created_at timestamp,
    custom_alias boolean,
    deleted boolean
);
```

### Optional analytics rollup table

```sql
CREATE TABLE IF NOT EXISTS url_shortener.url_daily_clicks (
    short_code text,
    event_date date,
    click_count counter,
    PRIMARY KEY (short_code, event_date)
);
```

## 30.3 Cassandra replication

Replication is configured at the keyspace level.

Example already shown above:

```sql
WITH replication = {
  'class': 'NetworkTopologyStrategy',
  'datacenter1': 3
};
```

This means:
- data is replicated across 3 nodes in the datacenter
- node failures are tolerated better than single-node SQL setups
- replication is native, not manually coded in the application

## 30.4 Cassandra sharding

In Cassandra, you usually do **not** write manual shard routing code like PostgreSQL application sharding.

Why:
- Cassandra partitions data by partition key
- for this use case, `short_code` as partition key gives natural distribution

That means:
- **replication is cluster-native**
- **sharding/partitioning is cluster-native**
- app code becomes simpler than application-managed PostgreSQL shards

## 30.5 Spring Boot Cassandra config

```yaml
spring:
  cassandra:
    keyspace-name: url_shortener
    contact-points: cassandra-0,cassandra-1,cassandra-2
    port: 9042
    local-datacenter: datacenter1
    schema-action: none
```

## 30.6 Spring Data Cassandra entity

```java
package com.example.shortener.cassandra;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("short_urls")
public class CassandraShortUrl {

    @PrimaryKey
    private String shortCode;

    private String originalUrl;
    private String userId;
    private Instant expiresAt;
    private Instant createdAt;
    private Boolean customAlias;
    private Boolean deleted;
}
```

## 30.7 Cassandra repository

```java
package com.example.shortener.cassandra;

import org.springframework.data.cassandra.repository.CassandraRepository;

public interface CassandraShortUrlRepository extends CassandraRepository<CassandraShortUrl, String> {
}
```

## 30.8 Cassandra service

```java
package com.example.shortener.cassandra;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CassandraShortUrlService {

    private final CassandraShortUrlRepository repository;

    public CassandraShortUrl save(String shortCode, String originalUrl, String userId, Instant expiresAt, boolean customAlias) {
        CassandraShortUrl entity = CassandraShortUrl.builder()
                .shortCode(shortCode)
                .originalUrl(originalUrl)
                .userId(userId)
                .expiresAt(expiresAt)
                .createdAt(Instant.now())
                .customAlias(customAlias)
                .deleted(false)
                .build();

        return repository.save(entity);
    }

    public Optional<CassandraShortUrl> findActive(String shortCode) {
        return repository.findById(shortCode)
                .filter(entity -> !Boolean.TRUE.equals(entity.getDeleted()))
                .filter(entity -> entity.getExpiresAt() == null || entity.getExpiresAt().isAfter(Instant.now()));
    }
}
```

## 30.9 Cassandra usage recommendation

- use Cassandra for **URL mapping only**
- keep complex reporting out of Cassandra
- store analytics in Kafka + OLAP store
- keep user/account/admin data in PostgreSQL if relational queries are needed

---

# 31. Replication and Sharding Summary by Stage

## Stage 0
- PostgreSQL single instance
- no replication
- no sharding

## Stage 1
- PostgreSQL single primary
- Redis for hot reads
- no sharding

## Stage 2
- PostgreSQL primary + replica
- app-level read/write split
- no sharding yet

## Stage 3
- PostgreSQL primary + multiple replicas
- cache-first redirect path
- prepare shard abstraction

## Stage 4
Choose one:
- **PostgreSQL sharded + per-shard replicas**
- **Cassandra cluster with native partitioning + replication**

## Stage 5
- Redis cluster
- Kafka scaled out
- PostgreSQL shards or Cassandra cluster for metadata
- separate analytics pipeline and store
- multi-AZ deployment

---

# 32. Practical Recommendation for Your Baseline

For a clean learning and implementation sequence:

## Best sequence for building from scratch
1. build **Stage 0 and Stage 1** with PostgreSQL only
2. add Redis cache
3. add Kafka click events and cleanup jobs
4. add PostgreSQL read replica support at **Stage 2**
5. introduce a **shard routing abstraction** before actually sharding
6. at **Stage 4**, choose:
   - PostgreSQL sharding if you want to learn app-managed sharding, or
   - Cassandra if you want to learn native distributed NoSQL scaling
7. keep analytics decoupled from redirect path

## Recommended real-world choice
- **up to 10K RPS:** PostgreSQL + Redis + Kafka is usually enough
- **20K+ RPS:** either PostgreSQL sharding or Cassandra becomes justified
- **50K RPS:** Cassandra is often simpler operationally for key-value URL resolution, while PostgreSQL can still work if your team is comfortable with shard management
