# 039_API_Key_Protection.md
# MiniURLShortener — API Key Protection

> Core mental model: **An API key is not a password for a human. It is an identity card for a calling system. API key protection means issuing, storing, checking, rotating, rate-limiting, and auditing that identity card without ever exposing the raw secret again.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. API Key vs JWT vs OAuth2](#4-api-key-vs-jwt-vs-oauth2)
- [5. Threat Model](#5-threat-model)
- [6. API Key Shape](#6-api-key-shape)
- [7. Raw Key vs Key Prefix vs Key Hash](#7-raw-key-vs-key-prefix-vs-key-hash)
- [8. Database Schema](#8-database-schema)
- [9. API Key Creation Flow](#9-api-key-creation-flow)
- [10. API Key Verification Flow](#10-api-key-verification-flow)
- [11. Spring Boot Filter Design](#11-spring-boot-filter-design)
- [12. API Key Context Object](#12-api-key-context-object)
- [13. Repository And Entity](#13-repository-and-entity)
- [14. Service Code](#14-service-code)
- [15. Controller Code](#15-controller-code)
- [16. Protecting MiniURLShortener APIs](#16-protecting-miniurlshortener-apis)
- [17. Error Response Contract](#17-error-response-contract)
- [18. Rate Limiting Per API Key](#18-rate-limiting-per-api-key)
- [19. Rotation And Revocation](#19-rotation-and-revocation)
- [20. Logging And Audit Trail](#20-logging-and-audit-trail)
- [21. Caching API Key Lookups](#21-caching-api-key-lookups)
- [22. Step-by-Step Dry Runs](#22-step-by-step-dry-runs)
- [23. Internal Execution Walkthrough](#23-internal-execution-walkthrough)
- [24. Production Failure Stories](#24-production-failure-stories)
- [25. Debugging Mindset](#25-debugging-mindset)
- [26. Testing Strategy](#26-testing-strategy)
- [27. Common Mistakes](#27-common-mistakes)
- [28. Interview-Ready Explanation](#28-interview-ready-explanation)
- [29. Senior Engineer Checklist](#29-senior-engineer-checklist)
- [30. One-Page Cheat Sheet](#30-one-page-cheat-sheet)
- [31. One Picture To Remember](#31-one-picture-to-remember)

---

## 1. Why This Exists

MiniURLShortener is becoming more production-shaped.

Earlier chapters protected normal user flows:

```text
036 Spring Security Basics
037 JWT Authentication
038 Role Based Authorization
```

JWT answers:

```text
Who is this logged-in user?
```

RBAC answers:

```text
What is this user allowed to do?
```

API key protection answers a different question:

```text
Which external system, partner, service, script, or automation is calling us?
```

Example MiniURLShortener integrations:

```text
1. A marketing tool creates short URLs automatically.
2. A partner application redirects through your service.
3. An internal analytics worker sends click events.
4. A batch migration script creates thousands of links.
5. A customer backend calls POST /api/v1/urls without a browser login.
```

If these systems call your API without protection, anyone can abuse your service.

Bad world:

```text
POST /api/v1/urls
No authentication
No caller identity
No per-client rate limit
No audit trail
No revocation
```

Production world:

```text
POST /api/v1/urls
X-API-Key: mlus_live_xxxxx

Server checks:
1. Does this key exist?
2. Is it active?
3. Is it expired?
4. Is it allowed to call this endpoint?
5. Is it within rate limit?
6. Which owner/client should be audited?
```

Core production rule:

```text
A public API without caller identity becomes someone else's free infrastructure.
```

---

## 2. The One Core Mental Model

API key protection is:

```text
MACHINE IDENTITY AT THE EDGE
```

ASCII:

```text
External System
      |
      |  X-API-Key: mlus_live_abcd...
      v
+-----------------------------+
| API Key Boundary            |
| 1. Extract key              |
| 2. Hash key                 |
| 3. Lookup key record        |
| 4. Check active/expiry      |
| 5. Check scope/permission   |
| 6. Attach client identity   |
+-----------------------------+
      |
      v
+-----------------------------+
| Protected API Logic         |
| create short URL / analytics|
+-----------------------------+
      |
      v
+-----------------------------+
| Audit + Rate Limit          |
| keyId, clientId, endpoint   |
+-----------------------------+
```

One-line memory:

```text
JWT identifies a user session; API key identifies a calling application.
```

API key protection has two halves:

```text
1. Secret handling:
   generate, show once, hash, never store raw key

2. Request enforcement:
   extract, verify, authorize, rate-limit, audit
```

The key idea:

```text
The raw API key is like a password.
The database should store only a hash.
The prefix is only for fast lookup and human debugging.
```

---

## 3. Problem Statement

Build API key protection for MiniURLShortener.

It must support:

```text
1. Create API key for a client/application.
2. Show raw key only once during creation.
3. Store only hashed key in database.
4. Store a safe key prefix for lookup/debugging.
5. Validate incoming X-API-Key header.
6. Reject missing, malformed, inactive, expired, or revoked keys.
7. Attach API key identity to request context.
8. Support scopes such as url:create and analytics:write.
9. Support per-key rate limiting later.
10. Support audit logging.
```

It should protect:

```text
POST /api/v1/urls
GET /api/v1/urls/{id}
POST /api/v1/events/clicks
admin/internal APIs if exposed
```

It should avoid:

```text
storing raw API keys
logging full API keys
putting API keys in query params
using weak random generation
using slow database lookup on every request without cache
returning detailed key existence information to attackers
```

Out of scope for this chapter:

```text
1. Full OAuth2 client credentials flow.
2. External developer portal.
3. Billing system.
4. Advanced quota plans.
5. HMAC signed requests.
```

This chapter creates a production-shaped API key foundation.

---

## 4. API Key vs JWT vs OAuth2

Do not confuse API keys with JWT.

```text
JWT:
    Usually represents a user/session.
    Has claims inside token.
    Often short-lived.
    Used after login.

API key:
    Represents an application/client/system.
    Usually opaque random secret.
    No trusted claims inside.
    Often long-lived but rotatable.
```

ASCII:

```text
Browser User Login
      |
      v
JWT Access Token
      |
      v
User identity + roles

Partner Backend / Script
      |
      v
API Key
      |
      v
Application identity + scopes
```

OAuth2 client credentials is more advanced than simple API keys.

```text
API Key:
    simple secret sent on every request

OAuth2 Client Credentials:
    client_id + client_secret exchanged for access token
```

When API key is enough:

```text
internal services
trusted partner integrations
simple automation scripts
MVP external API
low-complexity machine-to-machine access
```

When OAuth2 client credentials is better:

```text
large partner ecosystem
short-lived access tokens required
fine-grained centralized authorization
standard identity provider integration
enterprise compliance requirement
```

MiniURLShortener v1 can use API keys.

Production maturity path:

```text
No auth
  -> API Key
  -> API Key + scopes + quotas
  -> OAuth2 client credentials
  -> signed requests for high-security integrations
```

---

## 5. Threat Model

Before writing code, ask:

```text
What can go wrong if API keys are weakly protected?
```

Threats:

```text
1. Key leaked in logs.
2. Key committed to GitHub.
3. Key copied from browser dev tools.
4. Key stolen from server environment.
5. Key brute-forced if short or predictable.
6. Key reused forever because no rotation.
7. Key used for endpoints it should not access.
8. Key sends massive traffic and causes cost spike.
9. Key not traceable to client after incident.
10. Raw keys exposed from database backup.
```

ASCII attack flow:

```text
Developer accidentally logs full key
          |
          v
Logs exported to third-party tool
          |
          v
Attacker obtains key
          |
          v
Attacker creates millions of short URLs
          |
          v
Database + Redis + Kafka costs rise
          |
          v
No per-key audit -> hard to identify culprit
```

Protection strategy:

```text
Generate strong random key.
Show raw key once.
Store hash only.
Never log full key.
Use prefix for lookup/debugging.
Enforce active/expiry/scopes.
Rate limit per key.
Audit every sensitive action.
Rotate and revoke quickly.
```

Security mindset:

```text
Assume API keys will eventually leak.
Design so leaked keys can be detected, limited, and revoked.
```

---

## 6. API Key Shape

A good API key should be:

```text
1. Random.
2. Long enough.
3. Unpredictable.
4. Easy to identify as your product's key.
5. Environment-specific.
6. Prefix-searchable without exposing the full secret.
```

Recommended shape:

```text
mlus_live_7JkP9xQ4nA2sD8fGh6LmR3tYpV0zB1cE
mlus_test_8YsP1kLmQ2wErT5uIoPaSdFgHjKlZxCv
```

Breakdown:

```text
mlus      = MiniURLShortener product marker
live/test = environment marker
random    = high-entropy secret
```

ASCII:

```text
mlus_live_7JkP9xQ4nA2sD8fGh6LmR3tYpV0zB1cE
|    |    |
|    |    +-- secret random part
|    +------- environment
+------------ product prefix
```

Why add product prefix?

```text
1. Easier to recognize accidental leaks.
2. Easier for secret scanners.
3. Easier for support/debugging.
4. Easier to distinguish test vs live.
```

What not to do:

```text
api_123
clientA_secret
mohamed_key
UUID-only keys without enough policy
sequential keys
keys based on email/name/time
```

Use secure randomness:

```java
SecureRandom secureRandom = new SecureRandom();
```

Do not use:

```java
new Random()
System.currentTimeMillis()
Math.random()
```

---

## 7. Raw Key vs Key Prefix vs Key Hash

This is the most important section.

Never store the raw API key.

Use three concepts:

```text
Raw key:
    Full secret shown once to client.

Key prefix:
    Safe-ish short visible part for lookup/debugging.

Key hash:
    Cryptographic hash stored for verification.
```

ASCII:

```text
Raw key generated:
mlus_live_7JkP9xQ4nA2sD8fGh6LmR3tYpV0zB1cE

Split storage:

prefix stored:
mlus_live_7JkP

hash stored:
SHA-256(raw key + server pepper)
```

Database stores:

```text
key_prefix = mlus_live_7JkP
key_hash   = b8d9a9f9...long hash...
```

Client receives once:

```text
apiKey = mlus_live_7JkP9xQ4nA2sD8fGh6LmR3tYpV0zB1cE
```

Later request:

```text
Client sends raw key.
Server extracts prefix.
Server finds matching DB row by prefix.
Server hashes incoming raw key.
Server compares hash.
```

Why prefix?

```text
Without prefix:
    hash incoming key then lookup by hash.
    This is also possible.

With prefix:
    support can say key mlus_live_7JkP belongs to Client A.
    audit logs can show prefix only.
    rotation UI can show safe key identity.
```

Important rule:

```text
Prefix is not authentication.
Hash match is authentication.
```

---

## 8. Database Schema

Create table:

```sql
CREATE TABLE api_keys (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    key_prefix VARCHAR(32) NOT NULL,
    key_hash VARCHAR(128) NOT NULL,
    environment VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    scopes TEXT NOT NULL,
    expires_at TIMESTAMPTZ NULL,
    last_used_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    revoked_at TIMESTAMPTZ NULL,

    CONSTRAINT uk_api_keys_prefix UNIQUE (key_prefix),
    CONSTRAINT uk_api_keys_hash UNIQUE (key_hash),
    CONSTRAINT chk_api_keys_status CHECK (status IN ('ACTIVE', 'REVOKED', 'EXPIRED')),
    CONSTRAINT chk_api_keys_environment CHECK (environment IN ('TEST', 'LIVE'))
);

CREATE INDEX idx_api_keys_client_id ON api_keys(client_id);
CREATE INDEX idx_api_keys_status ON api_keys(status);
CREATE INDEX idx_api_keys_expires_at ON api_keys(expires_at);
```

Fields:

```text
client_id:
    owner application/customer/team

name:
    human label, example: marketing-prod-key

key_prefix:
    safe visible identifier

key_hash:
    secret verifier, never raw key

environment:
    TEST or LIVE

status:
    ACTIVE, REVOKED, EXPIRED

scopes:
    comma-separated for simple v1, normalized table later

expires_at:
    optional automatic expiry

last_used_at:
    useful for audit and cleanup
```

ASCII data model:

```text
+-------------------+        +-------------------+
| clients           |        | api_keys          |
+-------------------+        +-------------------+
| id                |<-------| client_id         |
| name              |        | name              |
| plan              |        | key_prefix        |
| status            |        | key_hash          |
+-------------------+        | scopes            |
                             | status            |
                             | expires_at        |
                             | last_used_at      |
                             +-------------------+
```

For MiniURLShortener, a client can have many API keys:

```text
Client: Acme Marketing
    key 1: prod server
    key 2: test automation
    key 3: migration script
```

Why many keys per client?

```text
You can revoke one leaked key without breaking every integration.
```

---

## 9. API Key Creation Flow

Creation flow:

```text
1. Admin/user requests new API key.
2. Server generates strong raw key.
3. Server derives key prefix.
4. Server hashes raw key with pepper.
5. Server stores prefix + hash + metadata.
6. Server returns raw key once.
7. Raw key is never retrievable again.
```

ASCII:

```text
Admin UI / Internal API
        |
        v
Create API Key Request
        |
        v
+-----------------------------+
| Generate raw key            |
| SecureRandom                |
+-----------------------------+
        |
        v
+-----------------------------+
| prefix = first safe chars   |
| hash = SHA-256(raw+pepper)  |
+-----------------------------+
        |
        v
+-----------------------------+
| Store prefix + hash only    |
+-----------------------------+
        |
        v
Return raw key ONCE
```

Response example:

```json
{
  "id": 101,
  "name": "marketing-prod",
  "keyPrefix": "mlus_live_7JkP",
  "apiKey": "mlus_live_7JkP9xQ4nA2sD8fGh6LmR3tYpV0zB1cE",
  "message": "Store this API key now. It will not be shown again."
}
```

Later list response should not include raw key:

```json
{
  "id": 101,
  "name": "marketing-prod",
  "keyPrefix": "mlus_live_7JkP",
  "status": "ACTIVE",
  "lastUsedAt": "2026-06-24T10:30:00Z"
}
```

Golden rule:

```text
Create response may show raw key once.
Every future response shows prefix only.
```

---

## 10. API Key Verification Flow

Incoming request:

```http
POST /api/v1/urls
X-API-Key: mlus_live_7JkP9xQ4nA2sD8fGh6LmR3tYpV0zB1cE
```

Verification steps:

```text
1. Read X-API-Key header.
2. If missing -> 401 API_KEY_MISSING.
3. If malformed -> 401 API_KEY_INVALID.
4. Extract prefix.
5. Lookup api_keys by prefix.
6. If no row -> 401 API_KEY_INVALID.
7. Hash incoming raw key using same pepper.
8. Constant-time compare with stored hash.
9. If mismatch -> 401 API_KEY_INVALID.
10. Check status ACTIVE.
11. Check expiry.
12. Check required scope.
13. Attach ApiKeyPrincipal to request.
14. Continue controller.
```

ASCII:

```text
Request
  |
  v
Header exists?
  | no
  +----> 401 API_KEY_MISSING
  |
  yes
  v
Prefix lookup
  | no row
  +----> 401 API_KEY_INVALID
  |
  row
  v
Hash compare
  | mismatch
  +----> 401 API_KEY_INVALID
  |
  match
  v
Status/expiry/scope
  | fail
  +----> 401/403
  |
  pass
  v
Controller executes
```

Status mapping:

```text
missing key       -> 401 Unauthorized
invalid key       -> 401 Unauthorized
revoked key       -> 401 Unauthorized
expired key       -> 401 Unauthorized
valid but no scope -> 403 Forbidden
rate limit exceeded -> 429 Too Many Requests
```

Security nuance:

```text
For invalid/revoked/unknown keys, keep messages generic.
Do not tell attacker which part was correct.
```

Bad:

```json
{"message":"API key prefix exists but hash mismatch"}
```

Good:

```json
{"code":"API_KEY_INVALID","message":"Invalid API key"}
```

---

## 11. Spring Boot Filter Design

API key verification should happen before controller logic.

Use a filter:

```text
ApiKeyAuthenticationFilter
```

Why filter?

```text
1. It runs once per request.
2. It protects many endpoints consistently.
3. Controllers stay clean.
4. Missing/invalid key never reaches business logic.
```

ASCII:

```text
HTTP Request
    |
    v
Spring Security Filter Chain
    |
    v
ApiKeyAuthenticationFilter
    |
    +-- invalid -> error response
    |
    +-- valid -> set SecurityContext / request attribute
    |
    v
Controller
```

Example filter:

```java
package com.miniurl.shortener.security.apikey;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniurl.shortener.common.error.ApiErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-Key";

    private final ApiKeyService apiKeyService;
    private final ObjectMapper objectMapper;

    public ApiKeyAuthenticationFilter(ApiKeyService apiKeyService,
                                      ObjectMapper objectMapper) {
        this.apiKeyService = apiKeyService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // Public endpoints do not require API key.
        return path.startsWith("/actuator/health")
                || path.startsWith("/public")
                || path.matches("^/[a-zA-Z0-9_-]{4,32}$");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String rawApiKey = request.getHeader(API_KEY_HEADER);

        try {
            ApiKeyPrincipal principal = apiKeyService.authenticate(rawApiKey);
            request.setAttribute("apiKeyPrincipal", principal);
            filterChain.doFilter(request, response);
        } catch (ApiKeyAuthException ex) {
            writeError(response, request, ex.getStatus(), ex.getCode(), ex.getMessage());
        }
    }

    private void writeError(HttpServletResponse response,
                            HttpServletRequest request,
                            HttpStatus status,
                            String code,
                            String message) throws IOException {

        response.setStatus(status.value());
        response.setContentType("application/json");

        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                code,
                message,
                request.getRequestURI(),
                List.of()
        );

        objectMapper.writeValue(response.getWriter(), body);
    }
}
```

Important:

```text
Filter-level errors may not pass through @RestControllerAdvice.
So the filter writes the same ApiErrorResponse shape directly.
```

---

## 12. API Key Context Object

After authentication, the application needs caller identity.

Create:

```java
package com.miniurl.shortener.security.apikey;

import java.util.Set;

public class ApiKeyPrincipal {

    private final Long apiKeyId;
    private final Long clientId;
    private final String keyPrefix;
    private final Set<String> scopes;

    public ApiKeyPrincipal(Long apiKeyId,
                           Long clientId,
                           String keyPrefix,
                           Set<String> scopes) {
        this.apiKeyId = apiKeyId;
        this.clientId = clientId;
        this.keyPrefix = keyPrefix;
        this.scopes = scopes;
    }

    public Long getApiKeyId() {
        return apiKeyId;
    }

    public Long getClientId() {
        return clientId;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public boolean hasScope(String scope) {
        return scopes.contains(scope);
    }
}
```

Mental model:

```text
Raw key is used only at the boundary.
Inside the app, use ApiKeyPrincipal.
```

ASCII:

```text
X-API-Key raw secret
        |
        v
Filter authenticates
        |
        v
ApiKeyPrincipal
        |
        v
Service uses clientId/apiKeyId/scopes
```

Why this matters:

```text
1. Services do not need raw secrets.
2. Audit logs can use apiKeyId and keyPrefix.
3. Rate limits can use apiKeyId.
4. Business logic can associate created URLs with clientId.
```

Example:

```text
Created short URL row:
client_id = principal.clientId
created_by_api_key_id = principal.apiKeyId
```

---

## 13. Repository And Entity

Entity:

```java
package com.miniurl.shortener.security.apikey;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "api_keys")
public class ApiKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 32)
    private String keyPrefix;

    @Column(nullable = false, unique = true, length = 128)
    private String keyHash;

    @Column(nullable = false, length = 20)
    private String environment;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private String scopes;

    private Instant expiresAt;
    private Instant lastUsedAt;
    private Instant createdAt;
    private Instant revokedAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long getId() { return id; }
    public Long getClientId() { return clientId; }
    public String getName() { return name; }
    public String getKeyPrefix() { return keyPrefix; }
    public String getKeyHash() { return keyHash; }
    public String getEnvironment() { return environment; }
    public String getStatus() { return status; }
    public String getScopes() { return scopes; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getLastUsedAt() { return lastUsedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getRevokedAt() { return revokedAt; }

    public void setClientId(Long clientId) { this.clientId = clientId; }
    public void setName(String name) { this.name = name; }
    public void setKeyPrefix(String keyPrefix) { this.keyPrefix = keyPrefix; }
    public void setKeyHash(String keyHash) { this.keyHash = keyHash; }
    public void setEnvironment(String environment) { this.environment = environment; }
    public void setStatus(String status) { this.status = status; }
    public void setScopes(String scopes) { this.scopes = scopes; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public void setLastUsedAt(Instant lastUsedAt) { this.lastUsedAt = lastUsedAt; }
    public void setRevokedAt(Instant revokedAt) { this.revokedAt = revokedAt; }
}
```

Repository:

```java
package com.miniurl.shortener.security.apikey;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKeyEntity, Long> {

    Optional<ApiKeyEntity> findByKeyPrefix(String keyPrefix);
}
```

For v1, scopes can be comma-separated:

```text
url:create,url:read,analytics:write
```

Later production normalization:

```text
api_keys
api_key_scopes
```

Why start simple?

```text
You learn the security boundary first.
You can normalize when scope model grows.
```

---

## 14. Service Code

Exception:

```java
package com.miniurl.shortener.security.apikey;

import org.springframework.http.HttpStatus;

public class ApiKeyAuthException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    public ApiKeyAuthException(String code, HttpStatus status, String message) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public String getCode() { return code; }
    public HttpStatus getStatus() { return status; }

    public static ApiKeyAuthException missing() {
        return new ApiKeyAuthException(
                "API_KEY_MISSING",
                HttpStatus.UNAUTHORIZED,
                "API key is required"
        );
    }

    public static ApiKeyAuthException invalid() {
        return new ApiKeyAuthException(
                "API_KEY_INVALID",
                HttpStatus.UNAUTHORIZED,
                "Invalid API key"
        );
    }

    public static ApiKeyAuthException expired() {
        return new ApiKeyAuthException(
                "API_KEY_EXPIRED",
                HttpStatus.UNAUTHORIZED,
                "API key has expired"
        );
    }

    public static ApiKeyAuthException forbidden() {
        return new ApiKeyAuthException(
                "API_KEY_FORBIDDEN",
                HttpStatus.FORBIDDEN,
                "API key does not have required permission"
        );
    }
}
```

Service:

```java
package com.miniurl.shortener.security.apikey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApiKeyService {

    private static final String LIVE_PREFIX = "mlus_live_";
    private static final String TEST_PREFIX = "mlus_test_";
    private static final int RANDOM_BYTES = 32;
    private static final int LOOKUP_PREFIX_LENGTH = 14;

    private final ApiKeyRepository repository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final String pepper;

    public ApiKeyService(ApiKeyRepository repository,
                         @Value("${security.api-key.pepper}") String pepper) {
        this.repository = repository;
        this.pepper = pepper;
    }

    @Transactional
    public CreatedApiKey createKey(CreateApiKeyCommand command) {
        String rawKey = generateRawKey(command.environment());
        String keyPrefix = rawKey.substring(0, LOOKUP_PREFIX_LENGTH);
        String keyHash = hash(rawKey);

        ApiKeyEntity entity = new ApiKeyEntity();
        entity.setClientId(command.clientId());
        entity.setName(command.name());
        entity.setKeyPrefix(keyPrefix);
        entity.setKeyHash(keyHash);
        entity.setEnvironment(command.environment());
        entity.setStatus("ACTIVE");
        entity.setScopes(String.join(",", command.scopes()));
        entity.setExpiresAt(command.expiresAt());

        ApiKeyEntity saved = repository.save(entity);

        return new CreatedApiKey(
                saved.getId(),
                saved.getName(),
                saved.getKeyPrefix(),
                rawKey
        );
    }

    @Transactional
    public ApiKeyPrincipal authenticate(String rawApiKey) {
        if (rawApiKey == null || rawApiKey.isBlank()) {
            throw ApiKeyAuthException.missing();
        }

        if (!rawApiKey.startsWith(LIVE_PREFIX) && !rawApiKey.startsWith(TEST_PREFIX)) {
            throw ApiKeyAuthException.invalid();
        }

        if (rawApiKey.length() < LOOKUP_PREFIX_LENGTH) {
            throw ApiKeyAuthException.invalid();
        }

        String keyPrefix = rawApiKey.substring(0, LOOKUP_PREFIX_LENGTH);

        ApiKeyEntity entity = repository.findByKeyPrefix(keyPrefix)
                .orElseThrow(ApiKeyAuthException::invalid);

        String incomingHash = hash(rawApiKey);

        if (!constantTimeEquals(incomingHash, entity.getKeyHash())) {
            throw ApiKeyAuthException.invalid();
        }

        if (!"ACTIVE".equals(entity.getStatus())) {
            throw ApiKeyAuthException.invalid();
        }

        if (entity.getExpiresAt() != null && entity.getExpiresAt().isBefore(Instant.now())) {
            throw ApiKeyAuthException.expired();
        }

        entity.setLastUsedAt(Instant.now());

        return new ApiKeyPrincipal(
                entity.getId(),
                entity.getClientId(),
                entity.getKeyPrefix(),
                parseScopes(entity.getScopes())
        );
    }

    public void requireScope(ApiKeyPrincipal principal, String requiredScope) {
        if (!principal.hasScope(requiredScope)) {
            throw ApiKeyAuthException.forbidden();
        }
    }

    private String generateRawKey(String environment) {
        byte[] bytes = new byte[RANDOM_BYTES];
        secureRandom.nextBytes(bytes);

        String randomPart = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        String prefix = "LIVE".equals(environment) ? LIVE_PREFIX : TEST_PREFIX;
        return prefix + randomPart;
    }

    private String hash(String rawKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest((rawKey + pepper).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8)
        );
    }

    private Set<String> parseScopes(String scopes) {
        return Arrays.stream(scopes.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());
    }
}
```

Command/response records:

```java
package com.miniurl.shortener.security.apikey;

import java.time.Instant;
import java.util.Set;

public record CreateApiKeyCommand(
        Long clientId,
        String name,
        String environment,
        Set<String> scopes,
        Instant expiresAt
) {}
```

```java
package com.miniurl.shortener.security.apikey;

public record CreatedApiKey(
        Long id,
        String name,
        String keyPrefix,
        String rawApiKey
) {}
```

Important note:

```text
For very high security, use HMAC-SHA256 with server secret instead of plain SHA-256(raw+pepper), or use a password hashing approach depending on lookup strategy. For this chapter, SHA-256 with strong random keys + server pepper is acceptable for learning the production mental model.
```

---

## 15. Controller Code

Request DTO:

```java
package com.miniurl.shortener.security.apikey;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Set;

public class CreateApiKeyRequest {

    @NotNull(message = "clientId is required")
    private Long clientId;

    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "environment is required")
    private String environment;

    @NotNull(message = "scopes are required")
    private Set<String> scopes;

    private Instant expiresAt;

    public Long getClientId() { return clientId; }
    public String getName() { return name; }
    public String getEnvironment() { return environment; }
    public Set<String> getScopes() { return scopes; }
    public Instant getExpiresAt() { return expiresAt; }

    public void setClientId(Long clientId) { this.clientId = clientId; }
    public void setName(String name) { this.name = name; }
    public void setEnvironment(String environment) { this.environment = environment; }
    public void setScopes(Set<String> scopes) { this.scopes = scopes; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
```

Controller:

```java
package com.miniurl.shortener.security.apikey;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/api-keys")
public class ApiKeyAdminController {

    private final ApiKeyService apiKeyService;

    public ApiKeyAdminController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateApiKeyResponse create(@Valid @RequestBody CreateApiKeyRequest request) {
        CreatedApiKey created = apiKeyService.createKey(
                new CreateApiKeyCommand(
                        request.getClientId(),
                        request.getName(),
                        request.getEnvironment(),
                        request.getScopes(),
                        request.getExpiresAt()
                )
        );

        return new CreateApiKeyResponse(
                created.id(),
                created.name(),
                created.keyPrefix(),
                created.rawApiKey(),
                "Store this API key now. It will not be shown again."
        );
    }
}
```

Response:

```java
package com.miniurl.shortener.security.apikey;

public record CreateApiKeyResponse(
        Long id,
        String name,
        String keyPrefix,
        String apiKey,
        String message
) {}
```

Production rule:

```text
Only admin/authenticated owner should call this endpoint.
Never expose API key creation publicly.
```

---

## 16. Protecting MiniURLShortener APIs

Example protected create short URL endpoint:

```java
@PostMapping("/api/v1/urls")
@ResponseStatus(HttpStatus.CREATED)
public CreateShortUrlResponse create(
        @Valid @RequestBody CreateShortUrlRequest request,
        HttpServletRequest servletRequest
) {
    ApiKeyPrincipal principal = (ApiKeyPrincipal)
            servletRequest.getAttribute("apiKeyPrincipal");

    apiKeyService.requireScope(principal, "url:create");

    return shortUrlService.create(request, principal.getClientId(), principal.getApiKeyId());
}
```

Service method becomes:

```java
public CreateShortUrlResponse create(CreateShortUrlRequest request,
                                     Long clientId,
                                     Long apiKeyId) {
    // validate URL
    // generate short code
    // save row with clientId and apiKeyId
}
```

Short URL table can add:

```sql
ALTER TABLE short_urls
ADD COLUMN client_id BIGINT NULL,
ADD COLUMN created_by_api_key_id BIGINT NULL;
```

Why store this?

```text
1. Which client created this URL?
2. Which API key created this URL?
3. Can we disable URLs from abusive client?
4. Can we generate usage reports per client?
5. Can we bill per client later?
```

ASCII:

```text
API Key Principal
   |
   | clientId=42
   | apiKeyId=101
   v
Create Short URL
   |
   v
short_urls row
   |
   +-- client_id = 42
   +-- created_by_api_key_id = 101
```

Endpoint scope examples:

```text
POST /api/v1/urls              -> url:create
GET  /api/v1/urls/{id}         -> url:read
DELETE /api/v1/urls/{id}       -> url:delete
POST /api/v1/events/clicks     -> analytics:write
GET /api/v1/analytics/summary  -> analytics:read
```

Scope mental model:

```text
Authentication says: this key is real.
Authorization says: this key can do this action.
```

---

## 17. Error Response Contract

Use the same style as the error handling chapter.

Missing key:

```json
{
  "timestamp": "2026-06-24T10:00:00Z",
  "status": 401,
  "error": "Unauthorized",
  "code": "API_KEY_MISSING",
  "message": "API key is required",
  "path": "/api/v1/urls",
  "fieldErrors": []
}
```

Invalid key:

```json
{
  "status": 401,
  "error": "Unauthorized",
  "code": "API_KEY_INVALID",
  "message": "Invalid API key"
}
```

Expired key:

```json
{
  "status": 401,
  "error": "Unauthorized",
  "code": "API_KEY_EXPIRED",
  "message": "API key has expired"
}
```

No scope:

```json
{
  "status": 403,
  "error": "Forbidden",
  "code": "API_KEY_FORBIDDEN",
  "message": "API key does not have required permission"
}
```

Rate limited:

```json
{
  "status": 429,
  "error": "Too Many Requests",
  "code": "RATE_LIMIT_EXCEEDED",
  "message": "Too many requests for this API key"
}
```

Important security detail:

```text
Invalid, revoked, unknown, and bad-hash keys can all return API_KEY_INVALID.
This avoids giving attackers a key-enumeration oracle.
```

---

## 18. Rate Limiting Per API Key

API keys become powerful when combined with rate limits.

Without per-key rate limiting:

```text
One leaked key can flood the entire system.
```

With per-key rate limiting:

```text
Damage is capped by quota.
```

ASCII:

```text
Request with API key
        |
        v
Authenticate key
        |
        v
Rate limit keyId=101
        |
        +-- over limit -> 429
        |
        v
Controller
```

Redis key example:

```text
rate:api-key:101:202606241530
```

Simple fixed window:

```text
Allowed: 1000 requests/minute
Current key: rate:api-key:101:202606241530
INCR key
EXPIRE 60s
if count > 1000 -> reject
```

Better production options:

```text
Token bucket
Sliding window log
Sliding window counter
Distributed Redis Lua script
Plan-based quotas
```

Scope with rate limit:

```text
client plan = FREE
url:create = 60/min
redirect = 1000/min
analytics:write = 500/min
```

Header response:

```http
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 742
X-RateLimit-Reset: 1719221460
```

Mini mental model:

```text
Authentication identifies the key.
Rate limiting protects capacity per identity.
```

---

## 19. Rotation And Revocation

API keys must be rotatable.

Why?

```text
Keys leak.
People leave teams.
Vendors change.
Logs get exported.
GitHub commits happen.
```

Revocation:

```text
Set status = REVOKED
Set revoked_at = now
Reject future requests
```

Rotation flow:

```text
1. Create new key.
2. Client deploys new key.
3. Monitor new key usage.
4. Revoke old key.
5. Confirm no traffic uses old key.
```

ASCII:

```text
Old key ACTIVE       New key not created
      |
      v
Create new key
      |
      v
Old key ACTIVE       New key ACTIVE
      |
      v
Client switches traffic
      |
      v
Old key no traffic   New key ACTIVE
      |
      v
Revoke old key
```

Bad rotation:

```text
Revoke old key before client deploys new key.
Result: production outage.
```

Good rotation:

```text
Overlap both keys briefly.
Audit last_used_at.
Revoke after traffic moves.
```

Emergency revocation:

```text
If key leaked publicly, revoke immediately.
Then notify client and issue replacement.
```

---

## 20. Logging And Audit Trail

Never log full API keys.

Wrong:

```text
Incoming X-API-Key=mlus_live_7JkP9xQ4nA2sD8fGh6LmR3tYpV0zB1cE
```

Correct:

```text
apiKeyPrefix=mlus_live_7JkP
apiKeyId=101
clientId=42
```

Audit fields:

```text
timestamp
apiKeyId
keyPrefix
clientId
method
path
status
latencyMs
sourceIp
userAgent
errorCode
requestId/correlationId
```

ASCII:

```text
Request
  |
  v
API key filter
  |
  +-- validates key
  +-- attaches apiKeyId/clientId
  |
  v
Controller
  |
  v
Audit log
```

Audit examples:

```text
clientId=42 apiKeyId=101 action=url:create status=201 latency=42ms
clientId=42 apiKeyId=101 action=url:create status=429 latency=3ms
unknownKeyPrefix=mlus_live_abcd status=401 reason=invalid
```

Do not log:

```text
full API key
full long URL with reset tokens
Authorization headers
cookies
passwords
JWT raw token
```

Security rule:

```text
Logs are a second database. Treat them like sensitive data.
```

---

## 21. Caching API Key Lookups

At high RPS, checking DB for every API key request is expensive.

Naive flow:

```text
Every request -> Postgres api_keys lookup
```

Problem:

```text
10k RPS = 10k key lookups/sec
DB gets unnecessary load
```

Better:

```text
Cache active key record by prefix or hash.
```

ASCII:

```text
Request
  |
  v
Extract prefix
  |
  v
Redis/local cache lookup
  | hit
  +------> verify hash/status/scope
  |
  miss
  v
Postgres lookup
  |
  v
populate cache with short TTL
```

Cache key:

```text
api-key:prefix:mlus_live_7JkP
```

TTL:

```text
30s to 5 minutes depending on revocation strictness
```

Important tradeoff:

```text
Long TTL improves performance but delays revocation.
Short TTL improves revocation speed but increases DB load.
```

Production pattern:

```text
1. Cache active keys with short TTL.
2. On revocation, delete cache entry.
3. Keep status check in cached value.
4. Use metrics for cache hit ratio.
```

Do not cache raw API key.

Cache safe record:

```text
apiKeyId
clientId
keyPrefix
keyHash
status
expiresAt
scopes
```

---

## 22. Step-by-Step Dry Runs

### Dry Run 1: Valid API key creates URL

Request:

```http
POST /api/v1/urls
X-API-Key: mlus_live_7JkP9xQ4nA2sD8fGh6LmR3tYpV0zB1cE
Content-Type: application/json

{
  "longUrl": "https://example.com/article"
}
```

Flow:

```text
1. Filter reads X-API-Key.
2. Prefix mlus_live_7JkP is extracted.
3. DB row is found.
4. Incoming key hash matches stored hash.
5. status is ACTIVE.
6. key is not expired.
7. principal is attached to request.
8. Controller requires url:create scope.
9. Service creates short URL under clientId/apiKeyId.
10. Response 201 Created.
```

---

### Dry Run 2: Missing API key

Request:

```http
POST /api/v1/urls
Content-Type: application/json

{"longUrl":"https://example.com"}
```

Flow:

```text
1. Filter does not find X-API-Key.
2. ApiKeyAuthException.missing is thrown.
3. Filter writes ApiErrorResponse.
4. Controller is never called.
5. Client receives 401 API_KEY_MISSING.
```

---

### Dry Run 3: Fake key with valid-looking prefix

Request:

```http
X-API-Key: mlus_live_7JkP_fake_fake_fake
```

Flow:

```text
1. Prefix lookup may find a row.
2. Server hashes incoming fake key.
3. Hash mismatch.
4. Server returns 401 API_KEY_INVALID.
5. Message does not reveal prefix was valid.
```

---

### Dry Run 4: Revoked key

Database:

```text
key_prefix = mlus_live_7JkP
status = REVOKED
```

Flow:

```text
1. Header exists.
2. Prefix lookup succeeds.
3. Hash matches.
4. Status check fails.
5. Server returns 401 API_KEY_INVALID.
```

Why not say revoked?

```text
Generic invalid response leaks less information.
```

---

### Dry Run 5: Valid key but missing scope

Key scopes:

```text
url:read
```

Endpoint requires:

```text
url:create
```

Flow:

```text
1. Authentication passes.
2. Principal is attached.
3. Controller/service checks url:create.
4. Scope missing.
5. Server returns 403 API_KEY_FORBIDDEN.
```

Difference:

```text
401 = key identity failed.
403 = key identity is valid but permission failed.
```

---

### Dry Run 6: Rate limited key

Plan:

```text
100 requests/minute
```

Current count:

```text
101 requests in current minute
```

Flow:

```text
1. API key authentication passes.
2. Rate limiter checks apiKeyId=101.
3. Count exceeds limit.
4. Server returns 429 RATE_LIMIT_EXCEEDED.
5. Controller is not called.
```

---

## 23. Internal Execution Walkthrough

Full create URL path:

```text
1. HTTP request enters Tomcat.
2. Spring Security filter chain starts.
3. ApiKeyAuthenticationFilter runs.
4. Header X-API-Key is extracted.
5. Prefix is computed.
6. API key record is loaded from cache/DB.
7. Incoming key hash is compared with stored hash.
8. Status and expiry are checked.
9. ApiKeyPrincipal is attached to request.
10. Rate limiter checks apiKeyId.
11. DispatcherServlet routes to controller.
12. Controller validates DTO.
13. Controller checks required scope.
14. Service creates short URL with clientId/apiKeyId.
15. Audit log records result.
16. Response returns to client.
```

ASCII:

```text
+--------+      +-------------+      +--------------+      +------------+
| Client | ---> | API Key     | ---> | Controller   | ---> | Service    |
+--------+      | Filter      |      +--------------+      +------------+
                +-------------+              |                   |
                       |                     |                   v
                       v                     |              +---------+
                +-------------+              |              | DB      |
                | api_keys DB |              |              +---------+
                +-------------+              |
                       |                     v
                       +-------------> ApiKeyPrincipal
```

Important separation:

```text
Filter authenticates the machine.
Controller validates request body.
Service enforces business rules.
DB enforces final invariants.
Audit logs record identity and result.
```

---

## 24. Production Failure Stories

### Failure Story 1: Raw keys stored in database

A database backup leaks.

Because raw API keys were stored, attacker can immediately call production APIs.

Root cause:

```text
API keys treated like normal strings, not secrets.
```

Fix:

```text
Store only key hash.
Show raw key once.
Rotate all existing keys.
```

Lesson:

```text
Database compromise should not automatically expose usable API keys.
```

---

### Failure Story 2: Full key logged in request logs

A reverse proxy logs all headers.

`X-API-Key` appears in log aggregation.

Root cause:

```text
Sensitive headers not redacted.
```

Fix:

```text
Redact X-API-Key at gateway and application logging layer.
Log only key prefix/apiKeyId.
```

Lesson:

```text
Do not protect secrets only in code. Protect them in logs, proxies, monitoring, and support tools.
```

---

### Failure Story 3: No revocation path

Partner key leaks on GitHub.

Team cannot disable only that key.

Root cause:

```text
Single shared global API key used by all clients.
```

Fix:

```text
Use per-client/per-app keys with status and revoked_at.
```

Lesson:

```text
Every key needs an owner and a kill switch.
```

---

### Failure Story 4: API key has unlimited access

Analytics ingestion key can delete URLs.

Root cause:

```text
API key authentication existed, but no scopes.
```

Fix:

```text
Add scopes and check required scope per endpoint.
```

Lesson:

```text
Authentication without authorization is only half protection.
```

---

### Failure Story 5: Revoked key still works due to cache

Key is revoked in DB but app cache stores ACTIVE state for 1 hour.

Root cause:

```text
Cache TTL too long and no cache invalidation on revoke.
```

Fix:

```text
Use short TTL and evict cache entry during revocation.
```

Lesson:

```text
Security cache design must consider revocation latency.
```

---

## 25. Debugging Mindset

When API key auth fails, ask:

```text
1. Is X-API-Key header present?
2. Is the key using correct environment prefix?
3. Does keyPrefix exist in DB?
4. Does incoming hash match stored hash?
5. Is status ACTIVE?
6. Is expiresAt in the future?
7. Does key have required scope?
8. Is the key rate limited?
9. Was the key recently revoked but still cached?
10. Are logs redacting the raw key?
```

Debug map:

```text
401 API_KEY_MISSING:
    Header absent or blank.

401 API_KEY_INVALID:
    Unknown, malformed, bad hash, or revoked.

401 API_KEY_EXPIRED:
    expiresAt is before now.

403 API_KEY_FORBIDDEN:
    Valid key but missing scope.

429 RATE_LIMIT_EXCEEDED:
    Valid key but quota exceeded.

500 INTERNAL_ERROR:
    DB/cache/hash configuration bug.
```

Useful SQL:

```sql
SELECT id, client_id, name, key_prefix, status, scopes, expires_at, last_used_at
FROM api_keys
WHERE key_prefix = 'mlus_live_7JkP';
```

Do not run:

```sql
SELECT key_hash and paste it into tickets unnecessarily.
```

Useful logs:

```text
requestId
apiKeyPrefix
apiKeyId
clientId
path
status
errorCode
latencyMs
rateLimitRemaining
```

Golden debugging rule:

```text
Debug with prefix, keyId, and clientId. Never debug with raw key.
```

---

## 26. Testing Strategy

Test API key protection at multiple levels.

### Unit tests

```text
generateRawKey creates live/test prefix
hash is stable for same input
hash differs for different input
missing key throws API_KEY_MISSING
malformed key throws API_KEY_INVALID
revoked key throws API_KEY_INVALID
expired key throws API_KEY_EXPIRED
missing scope throws API_KEY_FORBIDDEN
```

### Integration tests with MockMvc

```text
POST /api/v1/urls without key -> 401
POST /api/v1/urls with invalid key -> 401
POST /api/v1/urls with valid key but no scope -> 403
POST /api/v1/urls with valid url:create key -> 201
```

Example MockMvc:

```java
mockMvc.perform(post("/api/v1/urls")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"longUrl\":\"https://example.com\"}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("API_KEY_MISSING"));
```

### Security tests

```text
full API key never appears in logs
raw API key is not stored in database
list API keys endpoint does not return raw key
revoked key stops working
expired key stops working
```

### Rate limit tests

```text
within quota -> allowed
over quota -> 429
separate keys have separate counters
revoked key is rejected before rate limit
```

Testing rule:

```text
Do not test only happy key.
Test key lifecycle: create, use, rotate, revoke, expire.
```

---

## 27. Common Mistakes

### Mistake 1: Storing raw API keys

Wrong:

```text
api_keys.raw_key = mlus_live_abc...
```

Correct:

```text
api_keys.key_hash = SHA-256(raw + pepper)
api_keys.key_prefix = safe lookup prefix
```

### Mistake 2: Logging full keys

Wrong:

```text
log.info("apiKey={}", rawApiKey)
```

Correct:

```text
log.info("apiKeyPrefix={} apiKeyId={}", prefix, apiKeyId)
```

### Mistake 3: Using query parameter keys

Wrong:

```http
GET /api/v1/urls?apiKey=mlus_live_xxx
```

Correct:

```http
X-API-Key: mlus_live_xxx
```

Why query params are worse:

```text
They leak into browser history, access logs, analytics, referrers, and screenshots.
```

### Mistake 4: One global API key

Wrong:

```text
All partners use same secret.
```

Correct:

```text
One key per app/environment/use-case.
```

### Mistake 5: No scopes

Wrong:

```text
Valid key can call every endpoint.
```

Correct:

```text
Valid key still needs required scope.
```

### Mistake 6: No rate limit

Wrong:

```text
A leaked key has unlimited traffic.
```

Correct:

```text
Rate limit by apiKeyId/clientId/plan.
```

### Mistake 7: Long cache TTL for auth

Wrong:

```text
Cache API key status for 1 hour.
```

Correct:

```text
Short TTL + eviction on revoke.
```

### Mistake 8: Returning too much detail

Wrong:

```json
{"message":"Key prefix exists but hash mismatch"}
```

Correct:

```json
{"message":"Invalid API key"}
```

---

## 28. Interview-Ready Explanation

If interviewer asks:

```text
How would you implement API key protection for a URL shortener?
```

Strong answer:

```text
I would treat API keys as machine identity at the API edge. Each client or partner gets its own key, ideally separated by environment and use case. The raw key is generated using SecureRandom and shown only once. In the database I store only a safe prefix for lookup/debugging and a cryptographic hash of the raw key with a server-side pepper, never the raw key itself. Incoming requests send the key in an X-API-Key header, not in query parameters. A OncePerRequestFilter extracts the key, validates shape, looks up the prefix, hashes the incoming raw key, compares it in constant time, checks status, expiry, and scopes, then attaches an ApiKeyPrincipal containing apiKeyId, clientId, keyPrefix, and scopes. Endpoints still check required scopes like url:create or analytics:write. I would also add per-key rate limiting, audit logs using keyId/prefix only, rotation, revocation, lastUsedAt tracking, and cache key metadata carefully with short TTL plus eviction on revoke. Invalid keys return generic 401 responses, valid keys without permission return 403, and rate-limited keys return 429.
```

Why this answer is strong:

```text
1. Separates machine identity from user identity.
2. Protects raw secrets.
3. Mentions prefix + hash pattern.
4. Uses filter boundary.
5. Adds scopes, quotas, audit, rotation.
6. Shows security and production thinking.
7. Mentions constant-time comparison.
8. Avoids query parameter leakage.
```

Senior one-liner:

```text
API key protection is not just checking a string; it is the full lifecycle of machine identity: issue, hash, verify, authorize, rate-limit, audit, rotate, and revoke.
```

---

## 29. Senior Engineer Checklist

Before calling API key protection production-shaped, confirm:

```text
[ ] API keys are generated with SecureRandom
[ ] Raw key is shown only once
[ ] Raw key is never stored in DB
[ ] DB stores key_hash
[ ] DB stores key_prefix for lookup/debugging
[ ] X-API-Key header is used
[ ] Query param API keys are rejected/unsupported
[ ] Filter protects private endpoints
[ ] Public endpoints are explicitly excluded
[ ] Missing key returns 401 API_KEY_MISSING
[ ] Invalid key returns 401 API_KEY_INVALID
[ ] Expired key returns 401 API_KEY_EXPIRED
[ ] Valid key without scope returns 403 API_KEY_FORBIDDEN
[ ] Rate limit returns 429 RATE_LIMIT_EXCEEDED
[ ] Status supports ACTIVE and REVOKED
[ ] expiresAt is checked
[ ] lastUsedAt is updated
[ ] apiKeyId/clientId attached to request
[ ] Services never receive raw key
[ ] Full key is never logged
[ ] Audit logs include keyPrefix/apiKeyId/clientId
[ ] Scopes exist per key
[ ] Per-key rate limit is designed
[ ] Rotation flow exists
[ ] Revocation flow exists
[ ] Cache TTL does not break revocation badly
[ ] Tests cover create/use/revoke/expire/no-scope
```

If these are checked, your API key system is far stronger than a simple header comparison.

---

## 30. One-Page Cheat Sheet

```text
Core mental model:
API key = machine identity at the API edge.

API key lifecycle:
1. Generate strong raw key.
2. Show raw key once.
3. Store prefix + hash only.
4. Verify incoming key at filter.
5. Attach principal.
6. Check scopes.
7. Rate limit.
8. Audit.
9. Rotate/revoke.

Good key shape:
mlus_live_<random>
mlus_test_<random>

Never store:
raw API key

Store:
key_prefix
key_hash
client_id
status
scopes
expires_at
last_used_at

HTTP mapping:
missing key -> 401 API_KEY_MISSING
invalid key -> 401 API_KEY_INVALID
expired key -> 401 API_KEY_EXPIRED
no scope -> 403 API_KEY_FORBIDDEN
quota exceeded -> 429 RATE_LIMIT_EXCEEDED

Security rules:
Do not log full key.
Do not put key in query params.
Do not use weak randomness.
Do not use one global key.
Do not skip scopes.
Do not forget revocation.
Do not cache active status too long.

Production extras:
per-key rate limit
lastUsedAt
audit logs
short TTL cache
cache eviction on revoke
rotation overlap
secret scanning prefix
```

---

## 31. One Picture To Remember

```text
                     API KEY PROTECTION MENTAL MODEL

                         "Machine identity at the edge"

Partner App / Script / Worker
        |
        | X-API-Key: mlus_live_7JkP9xQ4...
        v
+---------------------------------------------------+
| ApiKeyAuthenticationFilter                        |
|                                                   |
|  1. Header exists?                                |
|  2. Prefix valid?                                 |
|  3. Lookup prefix                                 |
|  4. Hash incoming raw key                         |
|  5. Constant-time compare                         |
|  6. ACTIVE? not expired?                          |
|  7. Build ApiKeyPrincipal                         |
+---------------------------------------------------+
        |
        +-- fail --------------------------------+
        |                                        |
        v                                        v
+---------------------------+             401 / 403 / 429
| Rate Limit by apiKeyId    |             clean JSON error
+---------------------------+
        |
        v
+---------------------------+
| Scope Check               |
| url:create?               |
+---------------------------+
        |
        v
+---------------------------+
| MiniURLShortener Service  |
| create URL / analytics    |
+---------------------------+
        |
        v
+---------------------------+
| Audit Log                 |
| apiKeyId, clientId, path  |
+---------------------------+

DATABASE STORES:
    key_prefix + key_hash + status + scopes

DATABASE NEVER STORES:
    raw API key

FINAL MEMORY:
    Generate once.
    Store hash only.
    Verify at edge.
    Authorize with scopes.
    Limit abuse.
    Audit safely.
    Rotate and revoke.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. API keys identify calling systems, not human sessions.
2. The raw API key must be shown once and never stored.
3. Store a key prefix for lookup and a cryptographic hash for verification.
4. A filter should authenticate the key before controller logic runs.
5. Production API key protection includes scopes, rate limits, audit logs, rotation, and revocation.
```

After this chapter, MiniURLShortener security has this flow:

```text
036 Spring Security Basics
037 JWT Authentication
038 Role Based Authorization
039 API Key Protection
```

Next possible chapter:

```text
040_Secret_Management_Config_Protection.md
```
