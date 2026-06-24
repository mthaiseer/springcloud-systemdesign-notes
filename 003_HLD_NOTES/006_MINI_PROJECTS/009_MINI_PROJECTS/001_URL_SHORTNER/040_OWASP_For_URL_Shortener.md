# 040_OWASP_For_URL_Shortener.md
# MiniURLShortener — OWASP For URL Shortener

> Core mental model: **OWASP for a URL shortener means treating every user-controlled value as a possible attack path: the long URL, short code, custom alias, headers, API key, owner ID, redirect behavior, logs, admin actions, and third-party dependency chain.**

This chapter is written in the same learning style as `009_Error_Handling_Validation.md`: understanding-first, production-focused, ASCII-heavy, with Spring Boot examples, dry runs, failure stories, debugging mindset, interview answers, cheat sheet, and one-picture retention.

Official OWASP references used for this chapter:

```text
1. OWASP Top 10 Web Application Security Risks
2. OWASP API Security Top 10 2023
3. OWASP SSRF Prevention Cheat Sheet
4. OWASP CSRF Prevention Cheat Sheet
```

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Why URL Shorteners Are Special](#3-why-url-shorteners-are-special)
- [4. OWASP Map For MiniURLShortener](#4-owasp-map-for-miniurlshortener)
- [5. URL Shortener Attack Surface](#5-url-shortener-attack-surface)
- [6. A01 Broken Access Control](#6-a01-broken-access-control)
- [7. API1 Broken Object Level Authorization](#7-api1-broken-object-level-authorization)
- [8. A02 Cryptographic Failures](#8-a02-cryptographic-failures)
- [9. A03 Injection](#9-a03-injection)
- [10. A04 Insecure Design](#10-a04-insecure-design)
- [11. A05 Security Misconfiguration](#11-a05-security-misconfiguration)
- [12. A06 Vulnerable And Outdated Components](#12-a06-vulnerable-and-outdated-components)
- [13. A07 Authentication Failures](#13-a07-authentication-failures)
- [14. A08 Software And Data Integrity Failures](#14-a08-software-and-data-integrity-failures)
- [15. A09 Logging And Monitoring Failures](#15-a09-logging-and-monitoring-failures)
- [16. A10 SSRF](#16-a10-ssrf)
- [17. Open Redirect And Phishing Abuse](#17-open-redirect-and-phishing-abuse)
- [18. API Key Abuse Protection](#18-api-key-abuse-protection)
- [19. Admin Endpoint Protection](#19-admin-endpoint-protection)
- [20. Secure Redirect Design](#20-secure-redirect-design)
- [21. Secure URL Validation Service](#21-secure-url-validation-service)
- [22. Spring Boot Security Configuration](#22-spring-boot-security-configuration)
- [23. Security Headers](#23-security-headers)
- [24. Abuse Detection Model](#24-abuse-detection-model)
- [25. Database Security Rules](#25-database-security-rules)
- [26. Logging Rules For Security](#26-logging-rules-for-security)
- [27. Step-by-Step Dry Runs](#27-step-by-step-dry-runs)
- [28. Production Failure Stories](#28-production-failure-stories)
- [29. Debugging Mindset](#29-debugging-mindset)
- [30. Testing Strategy](#30-testing-strategy)
- [31. Interview-Ready Explanation](#31-interview-ready-explanation)
- [32. Senior Engineer Checklist](#32-senior-engineer-checklist)
- [33. One-Page Cheat Sheet](#33-one-page-cheat-sheet)
- [34. One Picture To Remember](#34-one-picture-to-remember)

---

## 1. Why This Exists

A URL shortener looks simple:

```text
POST /api/v1/urls
GET /{shortCode}
```

But attackers see something else:

```text
Can I hide phishing links?
Can I use your trusted domain to redirect victims?
Can I create millions of links?
Can I guess private links?
Can I read another user's analytics?
Can I abuse admin endpoints?
Can I make your server fetch internal URLs?
Can I leak tokens through logs?
Can I bypass validation using encoded URLs?
```

A production URL shortener is not only a data structure problem. It is a security boundary.

Simple mental model:

```text
A URL shortener accepts untrusted destinations and gives them a trusted-looking short domain.
```

That makes it attractive for abuse.

Bad security design:

```text
User submits any URL
System stores it
System redirects blindly
Logs everything
Admin APIs are weak
Analytics are public by ID
```

Production security design:

```text
Validate target URL
Reject dangerous schemes
Protect ownership boundaries
Hash secrets
Rate-limit creation
Detect abuse patterns
Avoid sensitive logs
Protect admin actions
Monitor suspicious redirects
```

OWASP gives a map of common failure classes. Your job is to translate the map into MiniURLShortener engineering decisions.

---

## 2. The One Core Mental Model

OWASP for URL shortener =

```text
UNTRUSTED INPUT -> SECURITY BOUNDARIES -> SAFE BEHAVIOR
```

ASCII:

```text
                   OUTSIDE WORLD
                        |
                        v
          +-----------------------------+
          | Untrusted Inputs            |
          | longUrl, alias, headers     |
          | shortCode, API key, ownerId |
          +-----------------------------+
                        |
                        v
          +-----------------------------+
          | Security Boundaries         |
          | validation, auth, rate limit|
          | ownership, logging rules    |
          +-----------------------------+
                        |
                        v
          +-----------------------------+
          | Safe Internal System        |
          | clean data, safe redirects  |
          | protected analytics/admin   |
          +-----------------------------+
                        |
                        v
                    USER / CLIENT
```

One-line memory:

```text
Every value from the client is hostile until it passes the right boundary.
```

For MiniURLShortener, the most dangerous values are:

```text
longUrl       -> may become phishing, SSRF, malware redirect
customAlias   -> may impersonate system routes or brands
shortCode     -> may enumerate private links or analytics
apiKey        -> may be stolen or brute-forced
ownerId       -> may cause BOLA if trusted from client
headers       -> may spoof IP, host, scheme, correlation
```

---

## 3. Why URL Shorteners Are Special

Most APIs only expose their own domain data.

A URL shortener does something extra:

```text
It takes a user-controlled destination and makes it easier to share.
```

ASCII:

```text
Attacker URL:
    https://evil.example/login

Shortener returns:
    https://sho.rt/bank-login

Victim sees:
    trusted short domain
```

This creates a trust problem.

Important difference:

```text
Normal CRUD app:
    bad input mostly hurts your app

URL shortener:
    bad input can hurt external users too
```

URL shortener risks:

```text
1. Phishing redirection.
2. Malware distribution.
3. Open redirect reputation damage.
4. SSRF if server fetches target URLs.
5. Analytics privacy leakage.
6. Link enumeration.
7. Brand impersonation via custom alias.
8. Abuse of free API quota.
9. Sensitive longUrl leakage in logs.
10. Admin moderation bypass.
```

So security must be part of the design, not a final patch.

---

## 4. OWASP Map For MiniURLShortener

OWASP Web Top 10 and OWASP API Top 10 overlap. For this project, map them to concrete features.

```text
+-------------------------------+------------------------------------------+
| OWASP Risk                    | MiniURLShortener Example                 |
+-------------------------------+------------------------------------------+
| Broken Access Control         | User reads another user's analytics      |
| Broken Object Authorization   | /urls/{id} without owner check           |
| Cryptographic Failures        | API keys stored in plaintext             |
| Injection                     | Unsafe SQL, log injection, header abuse  |
| Insecure Design               | No abuse model, guessable private links  |
| Security Misconfiguration     | Actuator exposed, CORS open, debug true  |
| Vulnerable Components         | Old Spring/Jackson/Postgres driver       |
| Auth Failures                 | Weak JWT/API key validation              |
| Integrity Failures            | Untrusted build/dependency pipeline      |
| Logging Failures              | No alert for phishing spike              |
| SSRF                          | URL preview fetches internal resources   |
+-------------------------------+------------------------------------------+
```

The goal is not to memorize OWASP names.

The goal is to ask:

```text
Where can an attacker cross a boundary?
What data can they control?
What trust assumption can they break?
What should block, detect, or log it?
```

---

## 5. URL Shortener Attack Surface

Attack surface means:

```text
All places where external users can influence system behavior.
```

MiniURLShortener attack surface:

```text
POST /api/v1/urls
GET /{shortCode}
GET /api/v1/urls/{id}/analytics
DELETE /api/v1/urls/{id}
PATCH /api/v1/urls/{id}
POST /api/v1/admin/urls/{id}/block
API keys
JWT tokens
request headers
longUrl
customAlias
shortCode path variable
query params
logs
Kafka click events
Redis cache keys
Actuator endpoints
Docker/Kubernetes configs
```

ASCII:

```text
                    Attacker
                       |
      +----------------+----------------+
      |                |                |
      v                v                v
  Create API       Redirect API      Analytics API
      |                |                |
      v                v                v
  longUrl/alias    shortCode        urlId/ownerId
      |                |                |
      +----------------+----------------+
                       |
                       v
              Security Boundaries
                       |
       +---------------+---------------+
       |               |               |
       v               v               v
   Validation       Ownership       Rate Limit
       |               |               |
       v               v               v
     DB             Redis/Kafka       Logs
```

Security starts by listing attack surfaces. If you cannot list surfaces, you cannot defend them.

---

## 6. A01 Broken Access Control

Broken access control means:

```text
User can access or modify something they should not.
```

MiniURLShortener examples:

```text
User A can delete User B's short URL.
User A can read User B's analytics.
Normal user can call admin block endpoint.
Unauthenticated user can list all URLs.
Private links can be guessed and accessed.
```

Bad controller:

```java
@GetMapping("/api/v1/urls/{id}/analytics")
public UrlAnalyticsResponse analytics(@PathVariable Long id) {
    return analyticsService.getAnalytics(id);
}
```

Problem:

```text
The API trusts the URL ID but does not check ownership.
```

Correct mental model:

```text
Every object access needs subject + object + permission check.
```

ASCII:

```text
Request: userId=10 wants urlId=99

        +---------+        +---------+
        | Subject |        | Object  |
        | user 10 |        | url 99  |
        +---------+        +---------+
              \              /
               \            /
                v          v
             +----------------+
             | Authorization  |
             | owner? admin?  |
             +----------------+
                    |
        +-----------+-----------+
        |                       |
        v                       v
      allow                   deny
```

Service-level check:

```java
public UrlAnalyticsResponse getAnalytics(Long urlId, Long currentUserId) {
    ShortUrl url = shortUrlRepository.findById(urlId)
            .orElseThrow(() -> new ShortUrlNotFoundException("URL not found"));

    if (!url.getOwnerId().equals(currentUserId)) {
        throw new AccessDeniedException("You do not own this URL");
    }

    return analyticsRepository.getAnalytics(urlId);
}
```

Better repository pattern:

```java
public UrlAnalyticsResponse getAnalytics(Long urlId, Long currentUserId) {
    ShortUrl url = shortUrlRepository.findByIdAndOwnerId(urlId, currentUserId)
            .orElseThrow(() -> new ShortUrlNotFoundException("URL not found"));

    return analyticsRepository.getAnalytics(url.getId());
}
```

Why return 404 instead of 403 sometimes?

```text
If you return 403, attacker learns the object exists.
If you return 404, you hide existence.
```

Production rule:

```text
For user-owned resources, prefer findByIdAndOwnerId and return 404 when not found.
For admin APIs, use role checks and audit logs.
```

---

## 7. API1 Broken Object Level Authorization

BOLA is the API-specific form of broken access control.

Pattern:

```text
GET /api/v1/urls/100
GET /api/v1/urls/101
GET /api/v1/urls/102
```

If the backend only checks login but not ownership, attacker can change IDs.

Bad flow:

```text
JWT valid? yes
urlId exists? yes
return analytics
```

Correct flow:

```text
JWT valid? yes
urlId belongs to user? yes
return analytics
```

ASCII:

```text
Wrong:

JWT valid
   |
   v
Return object by id

Correct:

JWT valid
   |
   v
Extract currentUserId
   |
   v
Query object by id AND ownerId
   |
   +-- found ----> return
   |
   +-- missing --> 404
```

Repository:

```java
public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    Optional<ShortUrl> findByIdAndOwnerId(Long id, Long ownerId);

    boolean existsByShortCode(String shortCode);
}
```

Controller should not accept ownerId from request body:

```java
// Wrong
public ResponseEntity<?> create(@RequestBody CreateRequest request) {
    Long ownerId = request.getOwnerId();
}
```

Correct:

```java
// Correct
public ResponseEntity<?> create(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody CreateShortUrlRequest request
) {
    Long ownerId = principal.getUserId();
    return ResponseEntity.ok(service.create(ownerId, request));
}
```

Golden rule:

```text
User identity comes from authenticated context, never from client-supplied ownerId.
```

---

## 8. A02 Cryptographic Failures

Cryptographic failures happen when sensitive data or secrets are poorly protected.

MiniURLShortener examples:

```text
API keys stored in plaintext.
JWT secret hardcoded in GitHub.
Password reset URL stored fully in logs.
HTTPS not enforced.
Weak random short codes used for private links.
Admin tokens sent through query parameters.
```

API key storage rule:

```text
Never store raw API keys.
Store a hash.
Show raw key only once at creation time.
```

ASCII:

```text
Create API Key
     |
     v
Generate random key
     |
     +-- return raw key once to user
     |
     v
Hash key with strong hash/HMAC
     |
     v
Store only hash in DB
```

Bad table:

```sql
api_key = 'mk_live_abc123...'
```

Better table:

```sql
key_prefix = 'mk_live_7H9A'
key_hash   = 'hashed-value'
```

Why prefix?

```text
Prefix helps lookup and debugging without storing the full secret.
```

Spring Boot config rule:

```text
Secrets must come from environment variables, secret manager, or Kubernetes Secret.
Never commit secrets to repo.
```

Example:

```yaml
security:
  jwt:
    secret: ${JWT_SECRET}
```

Private link randomness:

```text
Public short links can be short and collision-checked.
Private/security-sensitive links need high entropy and should not be enumerable.
```

Bad private short code:

```text
abc123
```

Better private code:

```text
a8F9kLm2QpZ7xT
```

---

## 9. A03 Injection

Injection means attacker input is interpreted as code or control syntax.

For MiniURLShortener, injection can happen in:

```text
SQL queries
JPQL queries
log lines
HTTP headers
redirect Location header
HTML admin dashboard
Kafka event payloads
metrics labels
```

SQL injection bad example:

```java
String sql = "SELECT * FROM short_urls WHERE short_code = '" + shortCode + "'";
```

Attack input:

```text
abc' OR '1'='1
```

Correct JPA:

```java
Optional<ShortUrl> findByShortCode(String shortCode);
```

Correct JDBC:

```java
jdbcTemplate.queryForObject(
        "SELECT * FROM short_urls WHERE short_code = ?",
        rowMapper,
        shortCode
);
```

ASCII:

```text
Bad:
user input + SQL string = executable attack

Good:
user input -> parameter binding -> treated as value
```

Log injection example:

```text
shortCode = "abc\nERROR admin logged in"
```

If logged raw:

```text
INFO redirect abc
ERROR admin logged in
```

Fix:

```java
private String sanitizeForLog(String value) {
    if (value == null) return null;
    return value.replace("\n", "_").replace("\r", "_");
}
```

Header injection rule:

```text
Never place unsanitized user input into response headers.
```

For redirects:

```java
return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(validatedLongUrl))
        .build();
```

But `validatedLongUrl` must already pass scheme and host validation.

---

## 10. A04 Insecure Design

Insecure design means the system works as designed, but the design itself is unsafe.

Examples:

```text
No abuse model.
Unlimited link creation.
Guessable private links.
No owner check on analytics.
No admin audit trail.
No phishing reporting flow.
No rate limits.
No blocklist.
No safe preview warning for suspicious links.
```

Important distinction:

```text
Bug:
    Developer forgot @Valid.

Insecure design:
    Product allows unlimited anonymous creation with no abuse controls.
```

URL shortener secure design questions:

```text
Who can create links?
How many links per minute?
Can anonymous users create links?
Can links be private?
Can short codes be guessed?
Can users report abuse?
Can admins block malicious links?
Can blocked links stay cached?
Can analytics expose personal data?
```

ASCII design boundary:

```text
Create Link
   |
   v
+-------------------+
| Auth / API Key?   |
+-------------------+
   |
   v
+-------------------+
| Rate Limit        |
+-------------------+
   |
   v
+-------------------+
| URL Validation    |
+-------------------+
   |
   v
+-------------------+
| Abuse Scoring     |
+-------------------+
   |
   +-- high risk --> pending/reject/block
   |
   v
Create short URL
```

Senior rule:

```text
Security is not only code correctness. It is product behavior under abuse.
```

---

## 11. A05 Security Misconfiguration

Security misconfiguration means safe tools are configured unsafely.

MiniURLShortener examples:

```text
Spring Boot actuator exposed publicly.
CORS allows every origin with credentials.
Debug errors expose stack traces.
Default admin password.
Swagger open in production.
H2 console enabled in production.
Management endpoints exposed.
Docker runs as root.
Kubernetes secrets printed in env dump.
TLS not enforced.
```

Bad production config:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
server:
  error:
    include-stacktrace: always
```

Better:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "health,prometheus"
  endpoint:
    health:
      show-details: never
server:
  error:
    include-stacktrace: never
```

CORS bad:

```java
config.setAllowedOrigins(List.of("*"));
config.setAllowCredentials(true);
```

Better:

```java
config.setAllowedOrigins(List.of("https://app.miniurl.com"));
config.setAllowedMethods(List.of("GET", "POST", "DELETE"));
config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Api-Key"));
config.setAllowCredentials(true);
```

ASCII:

```text
Misconfiguration often means:

Powerful feature
      |
      v
Wrong production exposure
      |
      v
Security incident
```

Production checklist:

```text
[ ] actuator locked down
[ ] stack traces hidden
[ ] Swagger disabled or protected
[ ] CORS restricted
[ ] HTTPS enforced
[ ] secrets not in repo
[ ] DB not publicly exposed
[ ] containers non-root
```

---

## 12. A06 Vulnerable And Outdated Components

Your application is only as safe as its dependencies.

MiniURLShortener uses:

```text
Spring Boot
Spring Security
Jackson
Hibernate
PostgreSQL driver
Redis client
Kafka client
Docker base image
Nginx
Kubernetes images
```

Risk:

```text
A vulnerable dependency may allow RCE, deserialization attacks, auth bypass, or data leakage.
```

Defense:

```text
1. Use dependency lock/version control.
2. Run dependency scanning.
3. Keep base images updated.
4. Remove unused dependencies.
5. Track CVEs in CI/CD.
6. Upgrade Spring Boot patch versions regularly.
```

Maven plugin example:

```xml
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>10.0.4</version>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

CI idea:

```text
Pull Request
    |
    v
Build + Tests
    |
    v
Dependency Scan
    |
    +-- critical CVE -> fail pipeline
    |
    v
Deploy
```

Rule:

```text
Security is not finished when code compiles. Dependencies must also be safe.
```

---

## 13. A07 Authentication Failures

Authentication failure means the system cannot reliably identify the caller.

MiniURLShortener examples:

```text
Weak API key validation.
Expired JWT accepted.
JWT signature not verified.
Refresh tokens never rotate.
Admin endpoint only hidden by URL.
API key allowed after revocation.
No rate limit on login/API key attempts.
```

Correct authentication chain:

```text
Request
  |
  v
Extract token/API key
  |
  v
Verify signature/hash/expiry/status
  |
  v
Load principal
  |
  v
Attach user identity to security context
```

ASCII:

```text
Authentication answers:
    Who are you?

Authorization answers:
    What are you allowed to do?
```

API key validation:

```java
public ApiKeyPrincipal authenticate(String rawKey) {
    String prefix = extractPrefix(rawKey);

    ApiKeyRecord record = apiKeyRepository.findByPrefix(prefix)
            .orElseThrow(() -> new BadCredentialsException("Invalid API key"));

    if (record.isRevoked()) {
        throw new BadCredentialsException("Invalid API key");
    }

    if (!apiKeyHasher.matches(rawKey, record.getKeyHash())) {
        throw new BadCredentialsException("Invalid API key");
    }

    return new ApiKeyPrincipal(record.getOwnerId(), record.getScopes());
}
```

Notice the message:

```text
Invalid API key
```

Do not reveal:

```text
key exists but revoked
key prefix valid but hash wrong
key belongs to inactive user
```

Security rule:

```text
Authentication failures should be boring and non-revealing.
```

---

## 14. A08 Software And Data Integrity Failures

Integrity means the code, configuration, and data you run are the ones you trust.

MiniURLShortener examples:

```text
Using unverified Docker images.
Deploying unsigned artifacts.
Running migrations from unknown source.
Accepting untrusted webhook payloads.
No checksum/signature validation.
CI pipeline can be modified by anyone.
```

Simple CI/CD integrity flow:

```text
Developer PR
    |
    v
Code review
    |
    v
Tests + security scan
    |
    v
Build artifact
    |
    v
Sign / tag image
    |
    v
Deploy only approved image
```

ASCII:

```text
Source Code -> Build -> Image -> Registry -> Kubernetes
     |          |        |         |          |
     v          v        v         v          v
  review      test    scan      access     policy
```

Practical rules:

```text
1. Protect main branch.
2. Require code review.
3. Pin action versions in CI.
4. Use trusted Docker base images.
5. Scan images.
6. Restrict production deploy permissions.
7. Keep migration scripts in version control.
```

For URL shortener, integrity matters because malicious code could:

```text
steal API keys
redirect all links to attacker domain
leak analytics
disable abuse detection
modify admin blocklist
```

---

## 15. A09 Logging And Monitoring Failures

Security logging failure means attacks happen but nobody notices.

MiniURLShortener must log security-significant events:

```text
API key authentication failure
rate limit exceeded
mass link creation
blocked domain attempt
reserved alias attempt
admin blocks/unblocks link
suspicious redirect spike
repeated 404 enumeration
SSRF blocked target
```

Bad logging:

```text
Only application errors are logged.
Security events are invisible.
```

Better logging:

```text
timestamp
correlationId
actorId or apiKeyId
ipHash
eventType
shortCode
riskScore
decision
```

Do not log raw secrets:

```text
API keys
JWTs
passwords
reset tokens
full URLs containing tokens
Authorization headers
```

ASCII:

```text
Request
  |
  v
Security decision
  |
  +-- allow -> normal log sample
  |
  +-- deny  -> security event log
  |
  +-- abuse -> alert candidate
```

Alert examples:

```text
1000 create requests from one API key in 1 minute
500 invalid shortCode requests from one IP in 5 minutes
many links to same blocked domain
admin block endpoint used outside office hours
```

Rule:

```text
If an attack is not logged, it effectively did not happen from an operations perspective.
```

---

## 16. A10 SSRF

SSRF means:

```text
Server-Side Request Forgery
```

It happens when your server fetches a user-supplied URL without strong validation.

Important nuance for URL shortener:

```text
Plain redirect does not require your server to fetch the target URL.
```

Redirect flow:

```text
Client -> Shortener -> 302 Location header -> Client fetches longUrl
```

SSRF danger appears if you add:

```text
URL preview
title extraction
screenshot generation
malware scanning by fetching URL
Open Graph metadata fetch
favicon fetch
```

Danger example:

```text
longUrl = http://169.254.169.254/latest/meta-data
```

If server fetches it:

```text
attacker may access cloud metadata through your server
```

ASCII:

```text
Attacker submits internal URL
          |
          v
Your server fetches URL
          |
          v
Internal network / metadata service
          |
          v
Sensitive data exposed
```

SSRF defense layers:

```text
1. Allow only http/https.
2. Parse URL safely.
3. Resolve DNS carefully.
4. Block private, loopback, link-local, metadata IPs.
5. Block redirects to internal IPs.
6. Use outbound network egress rules.
7. Timeout aggressively.
8. Do not return fetched content blindly.
```

Blocked targets:

```text
localhost
127.0.0.1
0.0.0.0
::1
10.0.0.0/8
172.16.0.0/12
192.168.0.0/16
169.254.0.0/16
169.254.169.254
internal Kubernetes service DNS
```

Production mental model:

```text
URL validation alone is not enough if the server fetches the URL.
You need network-level and DNS-aware SSRF protection.
```

---

## 17. Open Redirect And Phishing Abuse

A URL shortener is intentionally a redirect service.

So is it an open redirect?

```text
Technically it redirects to user-controlled destinations by design.
```

The security problem is abuse:

```text
attackers use your trusted domain to hide malicious destinations.
```

Example:

```text
https://miniurl.com/paypal-security
    -> https://evil-login.example
```

Defense options:

```text
1. Block known malicious domains.
2. Detect brand impersonation aliases.
3. Add interstitial warning for suspicious links.
4. Add report abuse endpoint.
5. Rate-limit anonymous creation.
6. Require authentication for custom aliases.
7. Scan high-risk links asynchronously.
8. Block redirects quickly after admin decision.
```

ASCII:

```text
Create short URL
      |
      v
Risk scoring
      |
      +-- low risk -----> active
      |
      +-- medium risk --> active + monitor
      |
      +-- high risk ----> pending / blocked / warning page
```

Alias impersonation examples:

```text
paypal-login
google-security
microsoft-reset
bank-verify
```

Business rule:

```text
Custom aliases are more sensitive than random short codes because they can create trust illusions.
```

---

## 18. API Key Abuse Protection

API keys are powerful because they automate link creation.

Threats:

```text
Leaked key creates millions of spam links.
Stolen key reads analytics.
Weak key can be brute-forced.
Old key never expires.
Key has too many permissions.
```

API key defense model:

```text
+----------------------+
| Random high entropy  |
+----------------------+
          |
          v
+----------------------+
| Store hash only      |
+----------------------+
          |
          v
+----------------------+
| Prefix lookup        |
+----------------------+
          |
          v
+----------------------+
| Scopes + owner       |
+----------------------+
          |
          v
+----------------------+
| Rate limit + audit   |
+----------------------+
```

Scopes:

```text
url:create
url:read
url:delete
analytics:read
admin:block
```

Never give this to normal user key:

```text
admin:block
```

Spring-style authorization:

```java
public void requireScope(ApiKeyPrincipal principal, String requiredScope) {
    if (!principal.getScopes().contains(requiredScope)) {
        throw new AccessDeniedException("Missing required scope");
    }
}
```

Rate-limit by:

```text
API key ID
owner ID
IP hash
route
```

Rule:

```text
API key authentication without scopes, rate limits, revocation, and audit logs is incomplete.
```

---

## 19. Admin Endpoint Protection

Admin endpoints are dangerous.

Examples:

```text
POST /api/v1/admin/urls/{id}/block
POST /api/v1/admin/urls/{id}/unblock
GET /api/v1/admin/abuse-reports
GET /api/v1/admin/users/{id}/links
```

Threats:

```text
normal user blocks competitor links
stolen token mass-unblocks phishing links
no audit trail for moderation
CSRF against admin if cookie auth is used
```

Admin defense:

```text
1. Require authentication.
2. Require ADMIN role.
3. Require MFA for sensitive admin systems.
4. Audit every action.
5. Use CSRF protection if browser cookie auth.
6. Restrict admin UI by network/VPN if possible.
7. Never expose admin actuator endpoints.
```

Spring method security:

```java
@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/api/v1/admin/urls/{id}/block")
public ResponseEntity<Void> blockUrl(@PathVariable Long id) {
    adminService.blockUrl(id);
    return ResponseEntity.noContent().build();
}
```

Audit log:

```java
securityAuditLogger.logAdminAction(
        adminUserId,
        "BLOCK_URL",
        "shortUrlId=" + id
);
```

ASCII:

```text
Admin request
    |
    v
Authenticated?
    |
    v
Has ADMIN role?
    |
    v
Action allowed?
    |
    v
Perform action
    |
    v
Audit log
```

---

## 20. Secure Redirect Design

Redirect path must be fast and safe.

Basic redirect flow:

```text
GET /abc123
  |
  v
validate shortCode format
  |
  v
lookup cache/DB
  |
  v
check status
  |
  v
check expiry
  |
  v
return 302 Location
```

Security checks:

```text
shortCode format
not blocked
not deleted
not expired
not abuse flagged
safe Location header construction
no caching of blocked decision incorrectly
```

ASCII:

```text
GET /code
   |
   v
+-----------------------+
| ShortCode validation  |
+-----------------------+
   |
   v
+-----------------------+
| Cache / DB lookup     |
+-----------------------+
   |
   v
+-----------------------+
| Status check          |
| ACTIVE/BLOCKED/DELETED|
+-----------------------+
   |
   +-- BLOCKED -> 403 or warning
   +-- DELETED -> 404
   |
   v
+-----------------------+
| Expiry check          |
+-----------------------+
   |
   +-- expired -> 410
   |
   v
302 Location: longUrl
```

Important cache rule:

```text
When admin blocks a link, evict or update Redis cache immediately.
```

Bad failure:

```text
DB says BLOCKED.
Redis still has ACTIVE target.
Redirect continues for 10 minutes.
```

Fix:

```text
Admin block transaction -> DB update -> cache eviction -> Kafka security event
```

---

## 21. Secure URL Validation Service

This service validates target URLs at creation time.

```java
package com.miniurl.shortener.security;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Locale;
import java.util.Set;

@Service
public class SecureUrlValidationService {

    private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https");

    private static final Set<String> BLOCKED_HOSTS = Set.of(
            "localhost",
            "127.0.0.1",
            "0.0.0.0",
            "169.254.169.254"
    );

    public URI validateForRedirect(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            throw new SecurityValidationException("URL is required");
        }

        URI uri;
        try {
            uri = URI.create(rawUrl.trim());
        } catch (IllegalArgumentException ex) {
            throw new SecurityValidationException("URL is malformed");
        }

        String scheme = uri.getScheme();
        if (scheme == null || !ALLOWED_SCHEMES.contains(scheme.toLowerCase(Locale.ROOT))) {
            throw new SecurityValidationException("Only http and https URLs are allowed");
        }

        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new SecurityValidationException("URL must contain a valid host");
        }

        String normalizedHost = host.toLowerCase(Locale.ROOT);
        if (BLOCKED_HOSTS.contains(normalizedHost)) {
            throw new SecurityValidationException("This destination is not allowed");
        }

        return uri;
    }
}
```

Exception:

```java
package com.miniurl.shortener.security;

import com.miniurl.shortener.common.error.ApiException;
import org.springframework.http.HttpStatus;

public class SecurityValidationException extends ApiException {

    public SecurityValidationException(String message) {
        super("SECURITY_VALIDATION_FAILED", HttpStatus.BAD_REQUEST, message);
    }
}
```

Important:

```text
This basic validation is enough for redirect-only v1.
It is not enough for server-side fetching features.
```

For URL preview, add DNS/IP validation and egress controls.

---

## 22. Spring Boot Security Configuration

Example baseline security config:

```java
package com.miniurl.shortener.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/actuator/health").permitAll()
                    .requestMatchers("/actuator/**").hasRole("ADMIN")
                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/v1/urls/**").authenticated()
                    .requestMatchers("/**").permitAll()
            )
            .headers(headers -> headers
                    .contentSecurityPolicy(csp -> csp
                            .policyDirectives("default-src 'none'; frame-ancestors 'none'")
                    )
                    .frameOptions(frame -> frame.deny())
            );

        return http.build();
    }
}
```

Why `/** permitAll`?

```text
Redirect endpoint GET /{shortCode} is public.
```

But be careful:

```text
Do not accidentally permit /api/v1/admin/** before admin rule.
Ordering matters.
```

CSRF note:

```text
If your API uses Authorization header or API keys and is stateless, CSRF is usually disabled.
If your admin UI uses browser cookies, enable CSRF for state-changing admin actions.
```

---

## 23. Security Headers

Security headers reduce browser-side risk.

Useful headers:

```text
Content-Security-Policy
X-Content-Type-Options
X-Frame-Options or frame-ancestors
Referrer-Policy
Strict-Transport-Security
Cache-Control for sensitive APIs
```

For API JSON responses:

```text
X-Content-Type-Options: nosniff
Cache-Control: no-store for sensitive user/admin APIs
```

For redirect response:

```text
Location: https://target.example/page
Cache-Control: no-store or controlled cache depending on product
```

Why avoid leaking referrer?

```text
Long URLs may contain tokens.
If you redirect with full referrer, destination may learn source path/query.
```

Possible policy:

```text
Referrer-Policy: no-referrer
```

ASCII:

```text
Browser receives response
       |
       v
Security headers instruct browser:
       |
       +-- do not sniff content
       +-- do not frame page
       +-- restrict referrer
       +-- enforce HTTPS
```

---

## 24. Abuse Detection Model

A URL shortener needs abuse detection beyond simple validation.

Signals:

```text
creator account age
API key age
links per minute
same domain repeated
suspicious alias words
blocked domain match
redirect spike
high 404 enumeration
user reports
malware scan result
```

Risk score example:

```text
+30 new account
+40 blocked domain match
+20 brand keyword in alias
+15 many links in short time
+10 suspicious TLD
```

Decision:

```text
0-30   allow
31-60  allow + monitor
61-80  interstitial warning
81-100 block or manual review
```

ASCII:

```text
Create request
     |
     v
Extract signals
     |
     v
Risk score
     |
     +-- low -------> ACTIVE
     +-- medium ----> ACTIVE + monitor
     +-- high ------> WARNING / PENDING
     +-- critical --> BLOCKED
```

Simple Java shape:

```java
public AbuseDecision evaluate(CreateShortUrlRequest request, UserPrincipal user) {
    int score = 0;

    if (user.isNewAccount()) score += 30;
    if (blockedDomainService.matches(request.getLongUrl())) score += 80;
    if (brandKeywordService.matches(request.getCustomAlias())) score += 20;
    if (rateSignalService.tooManyLinksRecently(user.getId())) score += 25;

    if (score >= 80) return AbuseDecision.BLOCK;
    if (score >= 60) return AbuseDecision.WARNING;
    if (score >= 30) return AbuseDecision.MONITOR;
    return AbuseDecision.ALLOW;
}
```

Rule:

```text
Validation catches invalid input. Abuse detection catches valid-looking malicious behavior.
```

---

## 25. Database Security Rules

Database constraints also support security.

Important constraints:

```sql
ALTER TABLE short_urls
ADD CONSTRAINT uk_short_code UNIQUE (short_code);

ALTER TABLE short_urls
ADD CONSTRAINT chk_status
CHECK (status IN ('ACTIVE', 'BLOCKED', 'DELETED', 'PENDING_REVIEW'));

ALTER TABLE short_urls
ADD CONSTRAINT chk_short_code_length
CHECK (char_length(short_code) BETWEEN 4 AND 64);
```

Ownership index:

```sql
CREATE INDEX idx_short_urls_owner_id
ON short_urls(owner_id);
```

Analytics access pattern:

```sql
SELECT *
FROM short_urls
WHERE id = :urlId
  AND owner_id = :currentUserId;
```

Why DB matters:

```text
Application code can have bugs.
DB constraints protect final invariants.
```

Sensitive data rule:

```text
Do not store API keys raw.
Do not store unnecessary personal data.
Consider hashing IPs for analytics.
```

IP analytics option:

```text
raw_ip        -> high privacy risk
salted_hash   -> lower risk, enough for abuse grouping
country/city  -> often enough for dashboard
```

---

## 26. Logging Rules For Security

Logs are both useful and dangerous.

Good to log:

```text
event type
user ID
API key ID
short URL ID
short code
status decision
correlation ID
latency
exception class
risk score
```

Avoid logging:

```text
raw API key
JWT
Authorization header
full longUrl with query string
password reset token
email verification token
cookies
```

Safe URL logging:

```java
public String safeUrlForLog(URI uri) {
    return uri.getScheme() + "://" + uri.getHost() + "/...";
}
```

Example security event:

```json
{
  "eventType": "SSRF_TARGET_BLOCKED",
  "correlationId": "c-123",
  "actorId": 42,
  "host": "169.254.169.254",
  "decision": "DENY"
}
```

ASCII:

```text
Sensitive request
      |
      v
Sanitize
      |
      v
Structured log
      |
      v
Alert / dashboard
```

Rule:

```text
Logs should help investigate attacks without becoming a second data breach.
```

---

## 27. Step-by-Step Dry Runs

### Dry Run 1: User tries to read another user's analytics

Request:

```http
GET /api/v1/urls/99/analytics
Authorization: Bearer userA-token
```

DB:

```text
urlId=99 ownerId=userB
```

Flow:

```text
1. JWT is valid.
2. Security context says currentUserId=userA.
3. Service queries findByIdAndOwnerId(99, userA).
4. No row found.
5. Service returns 404.
6. Attacker does not learn whether urlId=99 exists.
```

Correct result:

```json
{
  "status": 404,
  "code": "SHORT_URL_NOT_FOUND",
  "message": "Short URL not found"
}
```

---

### Dry Run 2: Attacker submits javascript URL

Request:

```json
{
  "longUrl": "javascript:alert(1)"
}
```

Flow:

```text
1. DTO validation sees non-blank string.
2. SecureUrlValidationService parses URI.
3. Scheme is javascript.
4. Scheme is not http/https.
5. Request rejected with 400 SECURITY_VALIDATION_FAILED.
```

---

### Dry Run 3: Attacker uses brand impersonation alias

Request:

```json
{
  "longUrl": "https://evil.example/login",
  "customAlias": "paypal-login"
}
```

Flow:

```text
1. Basic validation passes.
2. Alias pattern passes.
3. Abuse service detects brand keyword.
4. Risk score increases.
5. Domain reputation check is suspicious.
6. Decision becomes PENDING_REVIEW or BLOCKED.
```

Lesson:

```text
Valid format does not mean safe behavior.
```

---

### Dry Run 4: URL preview SSRF attempt

Request:

```json
{
  "longUrl": "http://169.254.169.254/latest/meta-data"
}
```

If app is redirect-only:

```text
1. App stores URL only if policy allows.
2. App does not fetch URL server-side.
3. SSRF does not execute through server.
```

If app has preview fetch:

```text
1. Preview service wants to fetch target.
2. SSRF filter resolves host/IP.
3. IP is metadata/link-local.
4. Fetch is blocked.
5. Security event is logged.
```

---

### Dry Run 5: API key leaked and abused

Signal:

```text
same API key creates 10,000 URLs in 5 minutes
```

Flow:

```text
1. API key authentication succeeds.
2. Rate limiter counts key usage.
3. Limit exceeded.
4. API returns 429.
5. Security event logged.
6. Alert triggers.
7. User/admin can revoke key.
```

Lesson:

```text
Authentication does not mean unlimited trust.
```

---

## 28. Production Failure Stories

### Failure Story 1: Analytics BOLA leak

A user changes:

```text
/api/v1/urls/100/analytics
```

to:

```text
/api/v1/urls/101/analytics
```

They see another user's click data.

Root cause:

```text
API checked JWT but not object ownership.
```

Fix:

```text
Use findByIdAndOwnerId, not findById.
```

Lesson:

```text
Authentication is not authorization.
```

---

### Failure Story 2: Blocked phishing link still redirects

Admin blocks a short URL.

DB status:

```text
BLOCKED
```

But Redis still contains:

```text
shortCode -> longUrl
```

Redirect continues.

Root cause:

```text
Cache was not evicted on moderation update.
```

Fix:

```text
Admin block operation must update DB and evict cache in one controlled flow.
```

Lesson:

```text
Security state must invalidate cached allow decisions.
```

---

### Failure Story 3: Full reset token leaked in logs

User shortens:

```text
https://app.example/reset?token=SECRET
```

Application logs full URL.

Root cause:

```text
Raw longUrl logged in request logs.
```

Fix:

```text
Log host, URL hash, and short URL ID; avoid full query string.
```

Lesson:

```text
Logs can become a data breach.
```

---

### Failure Story 4: Actuator endpoint exposed

Production exposes:

```text
/actuator/env
/actuator/heapdump
```

Attacker reads environment values.

Root cause:

```text
management.endpoints.web.exposure.include=* in production.
```

Fix:

```text
Expose only health/prometheus and protect management endpoints.
```

Lesson:

```text
Developer convenience settings can become production vulnerabilities.
```

---

### Failure Story 5: URL preview caused SSRF

Product adds preview feature:

```text
Show title and image for long URL.
```

Attacker submits:

```text
http://169.254.169.254/latest/meta-data
```

Server fetches it.

Root cause:

```text
Preview service fetched user URLs without SSRF protection.
```

Fix:

```text
Block internal IP ranges, validate redirects, enforce egress firewall, and avoid returning raw fetched data.
```

Lesson:

```text
Redirect-only is different from server-fetching. Server-fetching changes the threat model.
```

---

## 29. Debugging Mindset

When investigating a security issue, ask:

```text
Which boundary failed?
Was the caller authenticated?
Was the object ownership checked?
Was the action authorized?
Was input validated?
Was the decision cached?
Was the cache invalidated after security state changed?
Was sensitive data logged?
Was there an alert?
Was the same attack repeated before?
```

Debug map:

```text
403 ACCESS_DENIED:
    role/scope/ownership failed

404 for user-owned object:
    object missing or hidden because not owned

400 SECURITY_VALIDATION_FAILED:
    URL/alias/security rule rejected input

429 TOO_MANY_REQUESTS:
    rate limit caught abuse

500 INTERNAL_ERROR:
    unexpected bug; check logs, avoid leaking details
```

Useful security logs:

```text
correlationId
actorId
apiKeyId
route
decision
riskScore
eventType
shortCode
ownerId
ipHash
userAgentHash
```

Golden rule:

```text
Find the failed trust assumption.
```

---

## 30. Testing Strategy

Security behavior must be tested.

### Access control tests

```text
User cannot read another user's analytics.
User cannot delete another user's URL.
Normal user cannot call admin block endpoint.
Unauthenticated user cannot create protected URL.
```

### URL validation tests

```text
javascript:alert(1) -> rejected
file:///etc/passwd -> rejected
ftp://example.com -> rejected
http://localhost:8080 -> rejected depending on policy
http://169.254.169.254 -> rejected for server-fetch features
valid https URL -> accepted
```

### API key tests

```text
missing key -> 401
invalid key -> 401
revoked key -> 401
valid key without scope -> 403
valid key over limit -> 429
```

### Misconfiguration tests

```text
/actuator/health -> allowed
/actuator/env -> denied
/admin endpoint -> admin only
Swagger -> disabled or protected in production
```

### Cache security tests

```text
1. Create active link.
2. Redirect once to populate cache.
3. Admin blocks link.
4. Redirect again.
5. Must not redirect.
```

MockMvc example:

```java
@Test
void userCannotReadAnotherUsersAnalytics() throws Exception {
    mockMvc.perform(get("/api/v1/urls/99/analytics")
            .header("Authorization", userAToken()))
            .andExpect(status().isNotFound());
}
```

Rule:

```text
Security tests should verify deny paths, not only happy paths.
```

---

## 31. Interview-Ready Explanation

If interviewer asks:

```text
How would you apply OWASP security principles to a URL shortener?
```

Strong answer:

```text
I start by identifying the URL shortener-specific attack surface: longUrl,
customAlias, shortCode, API keys, analytics endpoints, admin moderation, logs,
cache, and redirect behavior. Then I map OWASP risks to concrete controls. For
access control and BOLA, every user-owned URL operation uses the authenticated
user ID from the security context and queries by id plus ownerId, never by id
alone. API keys are high entropy, stored as hashes, scoped, revocable, audited,
and rate-limited. For injection, I use parameterized queries/JPA methods and
sanitize log/header usage. For insecure design, I add rate limits, abuse scoring,
reserved aliases, report abuse, and admin block flows. For misconfiguration, I
lock down actuator, CORS, Swagger, stack traces, TLS, and secrets. For SSRF, I
remember that redirect-only does not require server-side fetching, but preview or
metadata fetching does; in that case I block internal IP ranges, validate DNS and
redirects, use timeouts, and enforce egress controls. I also avoid logging raw
API keys, JWTs, and full URLs containing tokens, and I emit security events for
rate-limit spikes, blocked domains, admin actions, and enumeration attempts.
```

Senior one-liner:

```text
For a URL shortener, OWASP is not theory: every client-controlled URL, ID, alias, and key is a trust boundary that must be validated, authorized, rate-limited, logged safely, and monitored.
```

---

## 32. Senior Engineer Checklist

```text
[ ] Long URL accepts only http/https
[ ] Dangerous schemes rejected
[ ] Custom alias pattern enforced
[ ] Reserved aliases blocked
[ ] Brand impersonation considered
[ ] User-owned APIs check ownerId
[ ] Repository uses findByIdAndOwnerId where needed
[ ] Admin endpoints require ADMIN role
[ ] Admin actions are audited
[ ] API keys are hashed, not stored raw
[ ] API keys have prefixes, scopes, status, expiry
[ ] API keys can be revoked
[ ] API key usage is rate-limited
[ ] JWT/API key errors reveal no sensitive detail
[ ] Actuator locked down
[ ] Stack traces hidden from clients
[ ] Swagger disabled/protected in production
[ ] CORS restricted
[ ] HTTPS enforced
[ ] Secrets externalized
[ ] Dependencies scanned
[ ] Docker image scanned
[ ] Logs do not contain raw secrets
[ ] Full longUrl query strings are not logged
[ ] Blocked links evict cache
[ ] Security events are logged
[ ] Abuse spikes alert engineers
[ ] SSRF protection exists before URL preview/fetch
[ ] Tests cover deny paths
```

---

## 33. One-Page Cheat Sheet

```text
Core mental model:
Every user-controlled value is hostile until it crosses the right security boundary.

Most dangerous inputs:
longUrl
customAlias
shortCode
ownerId
apiKey
JWT
headers
query params

OWASP mapping:
Broken Access Control -> analytics/delete/admin ownership failures
BOLA -> /urls/{id} without ownerId check
Cryptographic Failures -> raw API keys, hardcoded JWT secret
Injection -> SQL, log, header injection
Insecure Design -> no abuse controls, guessable private links
Misconfiguration -> actuator/CORS/debug exposed
Vulnerable Components -> old Spring/Jackson/base images
Auth Failures -> weak JWT/API key validation
Integrity Failures -> unsafe CI/CD, untrusted images
Logging Failures -> no security event visibility
SSRF -> server fetches user URL internally

URL shortener special risks:
phishing
malware redirect
brand alias impersonation
private link enumeration
analytics leakage
SSRF through preview
cache serving blocked links

Secure defaults:
http/https only
owner checks
scoped API keys
rate limits
safe logging
admin audit
blocked cache eviction
actuator locked
secrets externalized
SSRF protection before server-side fetch
```

---

## 34. One Picture To Remember

```text
                    OWASP FOR URL SHORTENER

                         Attacker / Client
                                |
                                v
              +----------------------------------+
              | User-Controlled Inputs           |
              | longUrl alias shortCode apiKey   |
              | headers ownerId query params     |
              +----------------------------------+
                                |
                                v
              +----------------------------------+
              | Security Boundaries              |
              | validate URL / alias             |
              | authenticate JWT/API key         |
              | authorize owner/admin/scope      |
              | rate limit / abuse score         |
              | sanitize logs / headers          |
              +----------------------------------+
                                |
              +-----------------+----------------+
              |                                  |
              v                                  v
      +-------------------+              +-------------------+
      | Safe Create API   |              | Safe Redirect API |
      | clean URL         |              | active only       |
      | owner assigned    |              | blocked denied    |
      | abuse checked     |              | cache consistent  |
      +-------------------+              +-------------------+
              |                                  |
              v                                  v
      +-------------------+              +-------------------+
      | DB Constraints    |              | Security Logs     |
      | owner/status      |              | audit/alerts      |
      | unique code       |              | correlation ID    |
      +-------------------+              +-------------------+

FINAL MEMORY:

A URL shortener converts untrusted destinations into trusted-looking links.
So security must protect input, ownership, secrets, redirects, cache, logs, and abuse flows.
```

---

## Final Retention Summary

Remember these seven sentences:

```text
1. A URL shortener is security-sensitive because it redirects users to client-controlled destinations.
2. Authentication proves who the caller is; authorization proves they can access this URL object.
3. Never trust ownerId from the request body; derive identity from JWT/API key context.
4. API keys must be random, hashed, scoped, revocable, rate-limited, and audited.
5. Redirect-only is not SSRF, but server-side preview/fetch features create SSRF risk.
6. Blocked links must invalidate cache immediately, or the cache becomes a security bypass.
7. Logs must reveal enough for investigation without leaking secrets or full sensitive URLs.
```

Next chapter:

```text
041_Security_Testing_Checklist.md
```
