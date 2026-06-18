# 032_JWT_Authentication — The Signed Identity Envelope Model

## Core Mental Model

Do not imagine JWT authentication as:

```text
Decode token and trust the JSON.
```

That is dangerous.

The better mental model is:

> **A JWT is a signed identity envelope. The server must verify the signature and claims before trusting what is inside.**

```text
HTTP Request
Authorization: Bearer <jwt>
        |
        v
+-----------------------------+
| JWT Authentication Filter   |
|-----------------------------|
| 1. Extract token            |
| 2. Verify signature         |
| 3. Validate claims          |
| 4. Convert claims to user   |
| 5. Store Authentication     |
+-----------------------------+
        |
        v
Controller if allowed
```

This chapter teaches exactly one idea:

> **JWT authentication is stateless request authentication: each request carries a signed token, Spring Security verifies it, builds an Authentication, and then authorization rules decide access.**

If you remember only one sentence:

> **JWT is not trusted because it is readable; it is trusted only after signature and claims are verified.**

---

## Why This Exists

Traditional server-side login often works like this:

```text
User logs in
Server creates session
Browser stores session cookie
Server stores session data
Every request sends cookie
Server looks up session
```

That works well, but distributed systems often need stateless authentication.

Example:

```text
Mobile App
  |
  v
API Gateway
  |
  v
Order Service
Payment Service
User Service
```

You do not want every service to call a central session database for every request.

JWT solves this by carrying identity information inside a signed token.

```text
Token contains:
  user id
  issuer
  expiry
  roles/scopes
  audience
```

Each service can verify the token locally if it has the signing key or public key.

But the risk is huge:

```text
JWT payload is Base64URL encoded, not encrypted by default.
Anyone can read it.
Anyone can create fake JSON.
Only signature verification proves it came from trusted issuer.
```

So JWT authentication exists to safely convert a signed token into a trusted identity.

---

## Problem Statement

A request arrives:

```http
GET /api/orders
Authorization: Bearer eyJhbGciOiJSUzI1NiIs...
```

The application must answer:

```text
1. Is the token present?
2. Is the token structurally valid?
3. Was it signed by a trusted issuer?
4. Is it expired?
5. Is it meant for this API?
6. Is issuer correct?
7. What user does it represent?
8. What authorities should the user get?
9. Should request continue?
```

The core problem:

> **How can a stateless backend trust identity information sent by the client on every request?**

JWT authentication solves it by:

```text
1. Issuer signs token.
2. Client sends token with request.
3. API verifies signature.
4. API validates claims.
5. API maps claims to Authentication.
6. Spring Security stores it in SecurityContext.
7. Authorization checks decide access.
```

---

## Real World Analogy

Imagine a sealed passport envelope.

The envelope contains:

```text
Name
Nationality
Expiry date
Permissions
```

Anyone can read a photocopy of the contents.

But border control trusts it only if:

```text
seal/signature is valid
issuing authority is trusted
passport is not expired
passport is meant for this purpose
```

Mapping:

```text
Passport envelope             JWT
Passport contents             claims
Government seal/signature     JWT signature
Border officer                JWT authentication filter
Trusted government            issuer
Expiry date                   exp claim
Allowed country               audience/scope
Entry stamp                   Authentication in SecurityContext
```

Important:

> **Reading claims is not authentication. Verifying the signature and claims is authentication.**

---

## The One Mental Model

JWT authentication has two phases:

```text
Phase 1: Trust establishment
  verify token signature and claims

Phase 2: Identity creation
  convert trusted claims into Authentication
```

ASCII:

```text
Bearer Token
    |
    v
Parse header/payload/signature
    |
    v
Verify signature
    |
    v
Validate claims
    |
    v
Extract subject/roles/scopes
    |
    v
Create Authentication
    |
    v
Store SecurityContext
    |
    v
Authorize request
```

If any trust check fails:

```text
return 401 Unauthorized
```

If trust succeeds but permission fails:

```text
return 403 Forbidden
```

---

## Core Concepts

## JWT

JWT means JSON Web Token.

It usually has three parts:

```text
header.payload.signature
```

Example shape:

```text
xxxxx.yyyyy.zzzzz
```

## Header

The header describes token type and signing algorithm.

Example:

```json
{
  "alg": "RS256",
  "typ": "JWT",
  "kid": "key-1"
}
```

Important fields:

```text
alg:
  signing algorithm

kid:
  key id used to find verification key
```

## Payload

The payload contains claims.

Example:

```json
{
  "sub": "user-42",
  "iss": "https://auth.example.com",
  "aud": "orders-api",
  "exp": 1893456000,
  "iat": 1893452400,
  "scope": "orders:read orders:write",
  "roles": ["USER"]
}
```

Claims are readable.

Do not put secrets in JWT payload.

## Signature

The signature proves integrity and issuer authenticity.

Conceptually:

```text
signature = sign(base64url(header) + "." + base64url(payload), privateKey)
```

API verifies using trusted key.

If payload changes, signature fails.

## Claims

Claims are statements about the token.

Common:

```text
sub:
  subject/user id

iss:
  issuer

aud:
  audience/intended API

exp:
  expiration time

iat:
  issued-at time

nbf:
  not-before time

scope/scp:
  permissions/scopes

roles:
  roles
```

## Authentication

Spring Security object representing authenticated identity.

```text
principal = user-42
authorities = SCOPE_orders:read, ROLE_USER
authenticated = true
```

## SecurityContext

Stores Authentication for current request.

```text
SecurityContextHolder.getContext().getAuthentication()
```

In a web request, Spring Security manages this lifecycle.

## 401 vs 403

```text
401:
  token missing, invalid, expired, wrong issuer, bad signature

403:
  token valid, user authenticated, but lacks permission
```

---

## Internal Architecture

```text
Client
  |
  | Authorization: Bearer JWT
  v
Spring Security Filter Chain
  |
  v
BearerTokenAuthenticationFilter
  |
  v
AuthenticationManager
  |
  v
JwtAuthenticationProvider
  |
  v
JwtDecoder
  |
  +--> verify signature
  +--> validate claims
  |
  v
JwtAuthenticationConverter
  |
  v
Authentication object
  |
  v
SecurityContext
  |
  v
AuthorizationFilter
  |
  v
Controller if allowed
```

With Spring Boot resource server:

```text
oauth2ResourceServer(oauth2 -> oauth2.jwt())
```

Boot wires much of this infrastructure if dependencies/properties exist.

---

## Internal Working

Given config:

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/public/**").permitAll()
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt())
            .build();
}
```

Request:

```http
GET /api/orders
Authorization: Bearer <jwt>
```

Flow:

```text
1. Request enters servlet filter chain.
2. Spring Security filter chain runs.
3. BearerTokenAuthenticationFilter checks Authorization header.
4. Token is extracted.
5. JwtAuthenticationProvider asks JwtDecoder to decode/verify.
6. JwtDecoder verifies signature using configured key/JWK.
7. JwtDecoder validates exp/nbf/iss/aud if configured.
8. Token is trusted.
9. Claims are converted to authorities.
10. Authentication object is created.
11. SecurityContext stores Authentication.
12. Authorization rules check endpoint.
13. If allowed, request reaches controller.
14. After request, SecurityContext is cleared.
```

Important:

```text
Controller receives authenticated request only after JWT verification succeeds.
```

---

## Rich ASCII Diagram — JWT Request Flow

```text
HTTP Request
Authorization: Bearer header.payload.signature
        |
        v
+------------------------------------+
| BearerTokenAuthenticationFilter    |
| extract token from header          |
+----------------+-------------------+
                 |
                 v
+------------------------------------+
| JwtDecoder                         |
| parse token                        |
| verify signature                   |
| validate exp/iss/aud               |
+----------------+-------------------+
                 |
                 v
+------------------------------------+
| JwtAuthenticationConverter         |
| claims -> authorities              |
+----------------+-------------------+
                 |
                 v
+------------------------------------+
| SecurityContext                    |
| Authentication stored              |
+----------------+-------------------+
                 |
                 v
+------------------------------------+
| Authorization                      |
| endpoint rule check                |
+----------------+-------------------+
                 |
          +------+------+
          |             |
          v             v
       allowed        denied
          |             |
          v             v
      controller      401/403
```

---

## Rich ASCII Diagram — JWT Structure

```text
JWT = header.payload.signature

+-------------------+   +-------------------+   +-------------------+
| Header            | . | Payload           | . | Signature         |
|-------------------|   |-------------------|   |-------------------|
| alg = RS256       |   | sub = user-42     |   | proves token was  |
| typ = JWT         |   | iss = auth server |   | signed by trusted |
| kid = key-1       |   | exp = timestamp   |   | key and unchanged |
+-------------------+   +-------------------+   +-------------------+

Readable does not mean trusted.
Signature verification makes it trustworthy.
```

---

## Step-by-Step Dry Run — Valid JWT

Token claims:

```json
{
  "sub": "user-42",
  "iss": "https://auth.example.com",
  "aud": "orders-api",
  "exp": 1893456000,
  "scope": "orders:read orders:write",
  "roles": ["USER"]
}
```

Request:

```http
GET /api/orders
Authorization: Bearer valid-token
```

Flow:

```text
1. Filter extracts token.
2. Token has three parts.
3. Decoder reads header and finds kid.
4. Public key is selected.
5. Signature verification succeeds.
6. exp is in future.
7. issuer is trusted.
8. audience matches orders-api.
9. scope is converted to authorities:
   SCOPE_orders:read
   SCOPE_orders:write
10. Authentication is created for user-42.
11. Endpoint requires authenticated user.
12. Request reaches controller.
```

Result:

```http
200 OK
```

---

## Step-by-Step Dry Run — Missing Token

Request:

```http
GET /api/orders
```

Endpoint rule:

```text
authenticated()
```

Flow:

```text
1. Security filter chain runs.
2. No Bearer token found.
3. No Authentication created.
4. Authorization requires authenticated user.
5. AuthenticationEntryPoint returns 401.
6. Controller is not called.
```

Result:

```http
401 Unauthorized
```

---

## Step-by-Step Dry Run — Expired Token

Token:

```json
{
  "sub": "user-42",
  "exp": 1000
}
```

Flow:

```text
1. Token extracted.
2. Signature may be valid.
3. exp is in the past.
4. JwtDecoder rejects token.
5. Authentication fails.
6. Security returns 401.
7. Controller is not called.
```

Result:

```http
401 Unauthorized
```

Important:

```text
A correctly signed expired token is still invalid.
```

---

## Step-by-Step Dry Run — Valid Token, Missing Role

Request:

```http
GET /api/admin/users
Authorization: Bearer valid-user-token
```

Token authorities:

```text
ROLE_USER
```

Endpoint requires:

```text
ROLE_ADMIN
```

Flow:

```text
1. Token signature valid.
2. Claims valid.
3. Authentication created.
4. Authorization checks admin rule.
5. ROLE_ADMIN missing.
6. AccessDeniedHandler returns 403.
```

Result:

```http
403 Forbidden
```

Authentication succeeded.
Authorization failed.

---

## Java/Spring Boot Example — Resource Server

Maven dependencies conceptually:

```text
spring-boot-starter-security
spring-boot-starter-oauth2-resource-server
```

Security configuration:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/orders/**").hasAuthority("SCOPE_orders:read")
                        .requestMatchers(HttpMethod.POST, "/api/orders/**").hasAuthority("SCOPE_orders:write")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt())
                .build();
    }
}
```

Properties:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://auth.example.com
```

Mental model:

```text
issuer-uri tells Spring where trusted tokens come from.
Spring can discover JWK keys.
JWTs signed by that issuer can be verified.
```

---

## Java/Spring Boot Example — Controller Accessing JWT

```java
@RestController
@RequestMapping("/api/me")
public class MeController {

    @GetMapping
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "userId", jwt.getSubject(),
                "issuer", jwt.getIssuer().toString(),
                "scopes", jwt.getClaimAsString("scope")
        );
    }
}
```

Alternative:

```java
@GetMapping("/api/me")
public String me(Authentication authentication) {
    return authentication.getName();
}
```

Execution:

```text
JWT filter validates token.
Jwt becomes principal.
Controller reads trusted principal.
```

Do not parse token manually in controller.

---

## Java/Spring Boot Example — Custom Authority Mapping

JWT claim:

```json
{
  "roles": ["ADMIN", "USER"]
}
```

Spring may not automatically map this to `ROLE_ADMIN`.

Custom converter:

```java
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter scopesConverter =
                new JwtGrantedAuthoritiesConverter();

        scopesConverter.setAuthorityPrefix("SCOPE_");
        scopesConverter.setAuthoritiesClaimName("scope");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            authorities.addAll(scopesConverter.convert(jwt));

            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles != null) {
                for (String role : roles) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
            }

            return authorities;
        });

        return converter;
    }
}
```

Why this matters:

```text
Token claim format must be mapped into Spring authorities.
Wrong mapping causes unexpected 403.
```

---

## Spring Boot Login vs Resource Server

JWT authentication can appear in two common architectures.

## Authorization Server Issues JWT

```text
User login
   |
   v
Auth Server
   |
   v
JWT issued
   |
   v
Client stores token
   |
   v
API receives Bearer token
```

API is resource server.

```text
API validates JWT.
API does not usually issue JWT.
```

## Custom App Issues JWT

Some apps create their own JWT after username/password login.

Flow:

```text
POST /login
  username/password
  app validates credentials
  app signs JWT
  client uses JWT later
```

This can work, but you must handle:

```text
secure signing key
token expiry
refresh tokens
revocation
rotation
password reset
logout semantics
role change invalidation
```

For production systems, external identity providers or authorization servers are common.

---

## Production Scale Example

At high scale, every request must validate token.

```text
20,000 RPS
each request has JWT
```

Costs:

```text
parse token
verify signature
validate claims
map authorities
run authorization rules
```

Optimizations:

```text
cache JWK/public keys
avoid remote call per request
keep tokens reasonably small
avoid DB lookup per request unless required
use short timeouts for JWK refresh
monitor auth failures and latency
```

Common production issue:

```text
JWK endpoint unavailable
new key rotation happens
APIs cannot fetch new public key
valid tokens start failing
```

Mitigation:

```text
cache keys
overlap old/new keys during rotation
monitor JWK fetch failures
avoid aggressive key removal
```

---

## Production Failure Story

A team used JWT roles:

```json
{
  "roles": ["ADMIN"]
}
```

Security rule:

```java
.requestMatchers("/api/admin/**").hasRole("ADMIN")
```

But all admin requests returned 403.

Root cause:

```text
Spring Security was reading scope claim by default.
roles claim was ignored.
Authentication existed, but authorities did not include ROLE_ADMIN.
```

The token was valid.
Authentication succeeded.
Authorization failed.

Fix:

```text
Add JwtAuthenticationConverter mapping roles -> ROLE_ADMIN.
Add integration test for admin token.
Log authorities safely in lower environment.
```

Lesson:

> **A valid JWT is not enough. Claims must be correctly mapped into Spring authorities.**

---

## Debugging Mindset

When JWT authentication fails, ask:

```text
1. Is Authorization header present?
2. Does it start with Bearer?
3. Is token structurally valid?
4. Is signature valid?
5. Is token expired?
6. Is issuer correct?
7. Is audience correct?
8. Are claims mapped to authorities?
9. Is endpoint rule expecting ROLE_ or SCOPE_?
10. Is failure 401 or 403?
```

### Symptom Map

```text
401 missing token
  -> no Authorization header
  -> wrong header format

401 invalid token
  -> bad signature
  -> expired token
  -> wrong issuer
  -> wrong audience
  -> JWK/key problem

403 with valid token
  -> missing authority
  -> wrong role mapping
  -> rule mismatch
  -> method security failure

Works locally, fails prod
  -> issuer URI difference
  -> audience config
  -> clock skew
  -> key rotation
  -> proxy stripping Authorization header

Controller not called
  -> security rejected before MVC
```

---

## Common Misconceptions

## Misconception 1 — “JWT is encrypted”

Usually no.

JWT payload is Base64URL encoded, not encrypted by default.

Anyone with token can read claims.

Do not store secrets in JWT.

## Misconception 2 — “If JWT decodes, it is valid”

No.

Decoding only reads JSON.
Validation requires signature and claim checks.

## Misconception 3 — “JWT logout is simple”

JWT is stateless.

If token is valid until expiry, server does not automatically forget it.

Logout/revocation needs strategy:

```text
short access token TTL
refresh token rotation
revocation list
token version
session store
```

## Misconception 4 — “Roles in token automatically work”

No.

Claims must be mapped to Spring authorities.

## Misconception 5 — “JWT replaces authorization”

No.

JWT authenticates identity and carries claims.
Authorization rules still decide access.

## Misconception 6 — “Long-lived JWTs are convenient”

They are risky.

If stolen, long-lived tokens remain useful for a long time.

Use short-lived access tokens and refresh-token strategy.

---

## Security Considerations

Never put sensitive secrets in JWT payload:

```text
password
credit card
private address if not needed
internal secrets
API keys
```

Use HTTPS always.

Protect signing keys.

Validate:

```text
signature
issuer
audience
expiry
not-before
algorithm
```

Be careful with algorithm confusion.

Do not accept unsigned tokens.

Do not blindly trust `alg` from token header without proper decoder configuration.

---

## Performance Considerations

JWT verification is usually cheaper than DB session lookup, but not free.

Costs:

```text
signature verification
claim parsing
authority conversion
authorization expressions
```

High RPS guidance:

```text
Use local verification.
Cache public keys.
Avoid per-request auth server calls.
Keep tokens small.
Avoid excessive authorities.
Monitor auth latency.
```

If every request also loads user from DB:

```text
JWT benefit reduces.
DB becomes auth bottleneck.
```

Load user only when necessary.

---

## Scalability Considerations

JWT works well for horizontally scaled services because:

```text
no central session lookup required per request
any service instance can validate token
load balancer does not need sticky session
```

But tradeoffs:

```text
revocation harder
claim changes not immediate
token size adds network cost
key rotation must be managed
authorization data can become stale
```

For role changes:

```text
user loses ADMIN role
old JWT still has ADMIN until expiry
```

Mitigations:

```text
short token TTL
token version check
revocation list for high-risk cases
force refresh after role change
```

---

## Failure Investigation Playbook

## Step 1 — Determine 401 vs 403

```text
401 -> authentication problem
403 -> authorization problem
```

## Step 2 — Inspect token safely

In lower environment:

```text
header alg/kid
issuer
audience
expiry
subject
roles/scopes
```

Do not paste production user tokens into random websites.

## Step 3 — Check Spring Security logs

Lower env:

```properties
logging.level.org.springframework.security=DEBUG
```

Look for:

```text
token validation failure
authority list
access denied
request matcher
```

## Step 4 — Check key/issuer config

```text
issuer-uri
jwk-set-uri
audience validator
clock skew
key rotation
network access to JWK endpoint
```

## Step 5 — Check authority mapping

Compare token:

```json
"roles": ["ADMIN"]
```

With Spring authority:

```text
ROLE_ADMIN
```

Or scopes:

```text
SCOPE_orders:read
```

## Step 6 — Add integration test

Test:

```text
no token -> 401
user token -> allowed user endpoint
user token -> 403 admin endpoint
admin token -> 200 admin endpoint
expired token -> 401
```

---

## Interview Q&A

### Q1. What is JWT authentication?

Strong answer:

> JWT authentication is stateless request authentication where the client sends a signed token with each request. The server verifies the token signature and claims, converts trusted claims into an Authentication object, stores it in the SecurityContext, and then authorization rules decide access.

### Q2. Is JWT encrypted?

Strong answer:

> Usually no. A normal JWT is Base64URL encoded and signed, not encrypted. Anyone with the token can read the payload, so we should not store secrets in it. Trust comes from signature verification, not from hidden content.

### Q3. What does JWT signature prove?

Strong answer:

> It proves the token was signed by a trusted key and that the header/payload were not modified after signing. If the payload changes, signature verification fails.

### Q4. What claims should be validated?

Strong answer:

> At minimum expiry and signature must be validated. In production, issuer and audience should also be validated, and sometimes not-before, algorithm, tenant, or authorized party depending on architecture.

### Q5. Why can a valid JWT still get 403?

Strong answer:

> Because authentication succeeded but authorization failed. The token may not contain the required role/scope, or claims may not be mapped correctly to Spring authorities.

### Q6. How does Spring Security process JWT?

Strong answer:

> In resource server mode, the Bearer token filter extracts the token, the JwtDecoder verifies signature and claims, a converter maps claims to authorities, an Authentication is created and stored in SecurityContext, and then authorization rules are evaluated.

### Q7. What are the drawbacks of JWT?

Strong answer:

> Revocation is harder because tokens are stateless. Role changes may not take effect until token expiry. Token theft is dangerous until expiry. Key rotation and claim mapping must be managed carefully.

---

## Production Checklist

```text
Token Validation
[ ] Signature verified
[ ] Expiry validated
[ ] Issuer validated
[ ] Audience validated
[ ] Algorithm restricted
[ ] Clock skew configured carefully

Claims
[ ] Subject present
[ ] Roles/scopes mapped correctly
[ ] No secrets in payload
[ ] Token size reasonable
[ ] Tenant claim validated if multi-tenant

Spring Security
[ ] Resource server configured
[ ] Public endpoints explicit
[ ] Admin endpoints protected
[ ] 401/403 tests exist
[ ] Method security tested if used

Operations
[ ] JWK cache works
[ ] Key rotation tested
[ ] Auth failure metrics monitored
[ ] 401/403 rate monitored
[ ] Debug logs available in lower env

Risk
[ ] Access tokens short-lived
[ ] Refresh strategy designed
[ ] Revocation strategy for high-risk cases
[ ] HTTPS enforced
[ ] Authorization header not logged
```

---

## One-Page Cheat Sheet

```text
JWT Authentication
==================

Core Idea
---------
JWT is a signed identity envelope.

JWT Parts
---------
header.payload.signature

Header
  algorithm, key id

Payload
  claims: sub, iss, aud, exp, scope, roles

Signature
  proves token was signed and unchanged

Authentication Flow
-------------------
Extract Bearer token
Verify signature
Validate claims
Map claims to authorities
Create Authentication
Store SecurityContext
Authorize request

401
---
Missing/invalid/expired token.

403
---
Valid token but not enough permission.

Golden Rule
-----------
Decoded does not mean trusted.
Only verified token is trusted.

Common Bugs
-----------
wrong issuer
wrong audience
expired token
JWK rotation problem
roles not mapped
ROLE_ prefix mismatch
Authorization header stripped
```

---

## Last-Minute Interview Revision

Do not say:

```text
JWT is decoded and user is logged in.
```

Say:

```text
JWT authentication means the request carries a signed token. Spring Security extracts the Bearer token, verifies its signature, validates claims like expiry, issuer and audience, converts trusted claims into authorities, stores an Authentication in the SecurityContext, and then authorization rules decide whether the request can continue.
```

Senior version:

```text
I treat JWT as a signed identity envelope, not a session replacement without tradeoffs. I validate signature and claims, map claims carefully to authorities, keep access tokens short-lived, handle key rotation, and design revocation/role-change strategy based on business risk.
```

---

## One Picture To Remember

```text
                    JWT AUTHENTICATION

Authorization: Bearer header.payload.signature
                         |
                         v
              +----------------------+
              | Extract token        |
              +----------------------+
                         |
                         v
              +----------------------+
              | Verify signature     |
              +----------------------+
                         |
                         v
              +----------------------+
              | Validate claims      |
              | exp / iss / aud      |
              +----------------------+
                         |
                         v
              +----------------------+
              | Map claims           |
              | roles/scopes         |
              +----------------------+
                         |
                         v
              +----------------------+
              | Authentication       |
              | SecurityContext      |
              +----------------------+
                         |
                         v
              +----------------------+
              | Authorization rules  |
              +----------------------+
                         |
             +-----------+-----------+
             |                       |
             v                       v
        Controller                 401/403
```

Final retention sentence:

> **JWT authentication means verifying a signed identity envelope on every request, then turning trusted claims into Spring Security Authentication.**
