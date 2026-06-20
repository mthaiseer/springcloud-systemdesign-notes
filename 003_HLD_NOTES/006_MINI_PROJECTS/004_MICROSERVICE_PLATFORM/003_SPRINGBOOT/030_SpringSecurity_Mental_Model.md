# 030_SpringSecurity_Mental_Model — The Security Gatekeeper Filter Chain

## Core Mental Model

Do not imagine Spring Security as:

```text
A login library.
```

That is too small.

The better mental model is:

> **Spring Security is a gatekeeper filter chain in front of your controllers. It decides who the request belongs to and whether that identity is allowed to continue.**

```text
HTTP Request
     |
     v
+---------------------------+
| Spring Security Filters   |
|---------------------------|
| 1. Extract credentials    |
| 2. Authenticate identity  |
| 3. Store SecurityContext  |
| 4. Authorize access       |
| 5. Continue or reject     |
+---------------------------+
     |
     v
Controller
```

This chapter teaches exactly one idea:

> **Spring Security protects the request pipeline before business code runs, using filters that authenticate and authorize each request.**

If you remember only one sentence:

> **Spring Security is a filter-chain gate: authenticate the user, store the identity, authorize the request, then either pass to controller or return 401/403.**

---

## Why This Exists

A Spring Boot application exposes endpoints:

```text
GET  /api/products
POST /api/orders
GET  /api/admin/users
POST /api/payments
```

Not every request should be allowed.

Some endpoints are public:

```text
GET /api/products
```

Some require login:

```text
POST /api/orders
```

Some require admin role:

```text
GET /api/admin/users
```

Some require ownership:

```text
GET /api/users/{id}/orders
```

Without a security layer, every controller must manually check identity and permissions:

```java
@GetMapping("/admin/users")
public List<UserResponse> users(HttpServletRequest request) {
    String token = request.getHeader("Authorization");

    if (token == null) {
        throw new UnauthorizedException();
    }

    User user = tokenService.parse(token);

    if (!user.hasRole("ADMIN")) {
        throw new ForbiddenException();
    }

    return userService.listUsers();
}
```

This becomes dangerous:

```text
one controller forgets check
one endpoint has wrong role
one token parsing bug copied everywhere
one missing ownership check leaks data
```

Spring Security exists to centralize security decisions in the request pipeline.

```text
Controller should handle business HTTP translation.
Security filter chain should handle authentication and authorization.
```

---

## Problem Statement

A request arrives:

```http
GET /api/admin/users
Authorization: Bearer eyJhbGciOi...
```

The system must answer:

```text
1. Is there a credential?
2. Is the credential valid?
3. Who is this user?
4. What authorities/roles does this user have?
5. Is this endpoint allowed for this identity?
6. If not, should response be 401 or 403?
7. If yes, how does controller access the user?
```

The core problem:

> **How does Spring Boot consistently protect every HTTP request before it reaches controller logic?**

Spring Security solves this by inserting a filter chain into the servlet request flow:

```text
Tomcat
  |
  v
Servlet Filter Chain
  |
  v
DelegatingFilterProxy
  |
  v
Spring Security FilterChainProxy
  |
  v
SecurityFilterChain
  |
  v
Controller if allowed
```

Security is not an afterthought.
It is part of request flow.

---

## Real World Analogy

Imagine a company building.

```text
Visitor arrives at front gate.
Security guard checks ID card.
Guard verifies ID in system.
Guard assigns visitor badge.
Guard checks whether badge allows entry to requested floor.
If allowed, visitor enters.
If not, guard denies entry.
```

Mapping:

```text
Building gate                 Spring Security filter chain
ID card/token                 credentials
Verify ID                     authentication
Visitor badge                 Authentication object
Access floors                 authorities/roles
Allowed to enter              authorization
Denied at gate                401/403 response
Office room                   controller endpoint
```

Important:

> **The office worker should not check everyone’s passport. The front gate should.**

Controller is the office worker.
Spring Security is the front gate.

---

## The One Mental Model

Spring Security answers two questions for every protected request:

```text
Authentication:
  Who are you?

Authorization:
  Are you allowed?
```

Flow:

```text
HTTP Request
   |
   v
Extract credential
   |
   v
Authenticate credential
   |
   v
Build Authentication object
   |
   v
Store in SecurityContext
   |
   v
Check authorization rules
   |
   +-- allowed -> controller
   |
   +-- not authenticated -> 401
   |
   +-- authenticated but not allowed -> 403
```

ASCII:

```text
+------------------------------------------------+
| Spring Security Gate                           |
|------------------------------------------------|
| Credential present?                            |
|   no  -> anonymous or 401                      |
|                                                |
| Credential valid?                              |
|   no  -> 401 Unauthorized                      |
|                                                |
| Authorities enough?                            |
|   no  -> 403 Forbidden                         |
|                                                |
| Allowed?                                       |
|   yes -> controller                            |
+------------------------------------------------+
```

---

## Core Concepts

## Filter Chain

Spring Security runs mostly as servlet filters.

A filter can run before the controller.

It can:

```text
read headers
validate token
set authentication
reject request
continue chain
```

Security filters are ordered.

That order matters.

## DelegatingFilterProxy

The servlet container knows filters.

Spring Security lives as Spring beans.

`DelegatingFilterProxy` bridges them.

```text
Servlet container filter
        |
        v
DelegatingFilterProxy
        |
        v
Spring bean: springSecurityFilterChain
```

Mental model:

```text
DelegatingFilterProxy lets servlet world call Spring-managed security filters.
```

## FilterChainProxy

`FilterChainProxy` is the central Spring Security filter dispatcher.

It chooses which `SecurityFilterChain` applies to a request.

```text
Request /api/admin/users
        |
        v
FilterChainProxy
        |
        v
matching SecurityFilterChain
```

## SecurityFilterChain

A `SecurityFilterChain` is a list of security filters plus request matching rules.

Example:

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/public/**").permitAll()
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated())
            .build();
}
```

## Authentication

`Authentication` represents the current identity.

It contains:

```text
principal
credentials
authorities
authenticated flag
details
```

Example:

```text
principal = user id 42
authorities = ROLE_USER, ORDER_READ
authenticated = true
```

## SecurityContext

`SecurityContext` stores the current `Authentication`.

```text
SecurityContext
   |
   v
Authentication
```

In servlet applications, it is usually associated with the current request/thread.

Controller or service can access it.

## AuthenticationManager

Authenticates credentials.

```text
username/password
JWT token
API key
OAuth2 token
```

It delegates to providers.

## AuthenticationProvider

Knows how to validate a specific credential type.

Examples:

```text
DaoAuthenticationProvider
JwtAuthenticationProvider
custom ApiKeyAuthenticationProvider
```

## UserDetailsService

Loads user details for username/password authentication.

```java
UserDetails loadUserByUsername(String username)
```

For JWT resource servers, user loading may be replaced by token validation and claim mapping.

## GrantedAuthority

Represents permission/role.

Examples:

```text
ROLE_USER
ROLE_ADMIN
ORDER_READ
PAYMENT_WRITE
```

Authorization checks these.

## 401 vs 403

```text
401 Unauthorized:
  user is not authenticated or credential invalid

403 Forbidden:
  user is authenticated but not allowed
```

Very important interview point.

---

## Internal Architecture

```text
HTTP Request
    |
    v
Tomcat
    |
    v
Servlet Filter Chain
    |
    v
DelegatingFilterProxy
    |
    v
FilterChainProxy
    |
    v
SecurityFilterChain
    |
    +--> SecurityContext filter
    +--> Authentication filter
    +--> Authorization filter
    +--> Exception handling filter
    |
    v
DispatcherServlet
    |
    v
Controller
```

JWT-style flow:

```text
Authorization header
        |
        v
Bearer token filter
        |
        v
JwtDecoder / AuthenticationProvider
        |
        v
Authentication object
        |
        v
SecurityContext
        |
        v
AuthorizationManager
        |
        v
Controller if allowed
```

---

## Internal Working

Given this configuration:

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
            .csrf(csrf -> csrf.disable())
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
GET /api/admin/users
Authorization: Bearer valid-jwt-token
```

Runtime:

```text
1. Tomcat receives request.
2. Servlet filter chain begins.
3. DelegatingFilterProxy delegates to Spring Security.
4. FilterChainProxy chooses matching SecurityFilterChain.
5. Bearer token filter extracts JWT from Authorization header.
6. JWT decoder validates signature, expiry, issuer, audience if configured.
7. JWT claims are converted into Authentication.
8. SecurityContext stores Authentication.
9. Authorization filter checks /api/admin/** requires ROLE_ADMIN.
10. If Authentication has ROLE_ADMIN, request continues.
11. DispatcherServlet routes request to controller.
12. Controller executes.
13. Response returns.
14. SecurityContext is cleared after request.
```

If token missing:

```text
401 Unauthorized
```

If token valid but lacks admin:

```text
403 Forbidden
```

---

## Rich ASCII Diagram — Security Gate Before Controller

```text
HTTP Request
GET /api/admin/users
Authorization: Bearer token
        |
        v
+--------------------------------------+
| Spring Security Filter Chain         |
|--------------------------------------|
| 1. Extract token                     |
| 2. Validate token                    |
| 3. Build Authentication              |
| 4. Store SecurityContext             |
| 5. Check required role/authority     |
+--------------------------------------+
        |
        +----------------------+
        |                      |
        v                      v
   Allowed                 Denied
        |                      |
        v                      v
DispatcherServlet       401 or 403
        |
        v
Controller
```

---

## Rich ASCII Diagram — Authentication vs Authorization

```text
Authentication: Who are you?
--------------------------------
Token says:
  subject = user-42
  roles = USER, ADMIN

Spring creates:
  Authentication(principal=user-42,
                 authorities=ROLE_USER, ROLE_ADMIN)


Authorization: Are you allowed?
--------------------------------
Endpoint requires:
  ROLE_ADMIN

User has:
  ROLE_ADMIN

Decision:
  allow
```

If user has only:

```text
ROLE_USER
```

Decision:

```text
deny 403
```

---

## Step-by-Step Dry Run — Public Endpoint

Config:

```java
.requestMatchers("/api/public/**").permitAll()
.anyRequest().authenticated()
```

Request:

```http
GET /api/public/products
```

Flow:

```text
1. Request enters Spring Security.
2. SecurityFilterChain matches.
3. Authorization rule says /api/public/** permitAll.
4. No login required.
5. Request continues to DispatcherServlet.
6. Controller executes.
7. Response returned.
```

Result:

```http
200 OK
```

Important:

```text
permitAll does not mean filters are skipped.
It means authorization allows the request.
```

---

## Step-by-Step Dry Run — Missing Token

Request:

```http
GET /api/orders
```

Config:

```java
.anyRequest().authenticated()
```

Flow:

```text
1. Request enters security chain.
2. No Authorization header found.
3. No authenticated user exists.
4. Endpoint requires authentication.
5. AuthenticationEntryPoint returns 401.
6. DispatcherServlet/controller is not called.
```

Result:

```http
401 Unauthorized
```

Debug clue:

```text
No controller logs because request stopped before controller.
```

---

## Step-by-Step Dry Run — Valid User Token

Request:

```http
GET /api/orders
Authorization: Bearer token-for-user-42
```

Token claims:

```text
sub=user-42
roles=USER
```

Flow:

```text
1. Bearer token filter extracts token.
2. JWT decoder validates token.
3. Authentication created:
   principal=user-42
   authorities=ROLE_USER
4. SecurityContext stores Authentication.
5. Authorization rule requires authenticated.
6. Request allowed.
7. Controller can access principal.
```

Controller:

```java
@GetMapping("/api/orders")
public List<OrderResponse> orders(Authentication authentication) {
    String userId = authentication.getName();
    return orderService.findOrders(userId);
}
```

Result:

```http
200 OK
```

---

## Step-by-Step Dry Run — Valid Token But Wrong Role

Request:

```http
GET /api/admin/users
Authorization: Bearer token-for-user-42
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
1. Token valid.
2. User authenticated.
3. SecurityContext contains user-42.
4. Authorization checks admin rule.
5. ROLE_ADMIN missing.
6. AccessDeniedHandler returns 403.
7. Controller is not called.
```

Result:

```http
403 Forbidden
```

This is not authentication failure.
It is authorization failure.

---

## Java/Spring Boot Example — JWT Resource Server

Dependency conceptually:

```text
spring-boot-starter-security
spring-boot-starter-oauth2-resource-server
```

Configuration:

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
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt())
                .build();
    }
}
```

Properties example:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://issuer.example.com
```

Internal execution:

```text
Spring Boot sees security + resource server dependencies.
Auto-configuration creates JWT decoder if properties are present.
SecurityFilterChain registers bearer token authentication.
Requests with Bearer tokens are validated before controller.
```

---

## Java/Spring Boot Example — Access Current User

Controller parameter:

```java
@GetMapping("/api/me")
public UserProfile me(Authentication authentication) {
    return userService.profile(authentication.getName());
}
```

Using `@AuthenticationPrincipal`:

```java
@GetMapping("/api/me")
public UserProfile me(@AuthenticationPrincipal Jwt jwt) {
    String userId = jwt.getSubject();
    return userService.profile(userId);
}
```

Mental model:

```text
Security filters created Authentication earlier.
Controller reads identity from SecurityContext.
```

Controller should not validate JWT manually.

---

## Java/Spring Boot Example — Method Security

Enable:

```java
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
}
```

Service:

```java
@Service
public class AdminService {

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> listUsers() {
        return List.of();
    }
}
```

Flow:

```text
Request may pass URL security.
Service method has additional guard.
Spring AOP intercepts method call.
Authorization expression evaluated.
If not allowed, AccessDeniedException.
```

Mental model:

```text
URL security protects request path.
Method security protects business operation.
```

Use both carefully.

---

## Spring Security In Request Flow

Full Spring Boot request:

```text
Client
  |
  v
Tomcat
  |
  v
Filter Chain
  |
  v
Spring Security Filters
  |
  +-- reject 401/403
  |
  v
DispatcherServlet
  |
  v
Controller
  |
  v
Service
```

Security sits before MVC controller.

So when debugging:

```text
401/403 usually means security filter chain decision.
```

Not controller bug.

---

## Production Scale Example

Imagine an API service:

```text
20,000 RPS
JWT authentication
Admin endpoints
User endpoints
Service-to-service calls
```

Security cost matters.

Each request may perform:

```text
parse Authorization header
decode JWT
verify signature
convert claims to authorities
evaluate authorization rule
set/clear security context
```

Production concerns:

```text
JWT verification CPU
remote JWK fetching/caching
large tokens increasing request size
incorrect role mapping
security context leakage
too broad permitAll
missing method-level ownership checks
```

Example:

```text
/api/users/{id}/orders requires authenticated
```

But also needs ownership:

```text
user can access only own orders
```

URL rule alone:

```java
.anyRequest().authenticated()
```

is not enough.

Service must enforce:

```java
public List<OrderResponse> getOrders(String authenticatedUserId, Long requestedUserId) {
    if (!authenticatedUserId.equals(requestedUserId.toString())) {
        throw new AccessDeniedException("not owner");
    }
    return orderRepository.findByUserId(requestedUserId);
}
```

Security has layers:

```text
authentication
coarse URL authorization
fine-grained business authorization
```

---

## Production Failure Story

A team added Spring Security dependency:

```text
spring-boot-starter-security
```

They did not add custom config.

After deployment:

```text
All endpoints started returning 401.
Health checks failed.
Kubernetes marked pods unhealthy.
Traffic dropped.
```

Root cause:

```text
Spring Security auto-configuration activated because security classes were on classpath.
Default security required authentication.
Health endpoint was not explicitly permitted.
```

Fix:

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/actuator/health/**").permitAll()
                    .requestMatchers("/api/public/**").permitAll()
                    .anyRequest().authenticated())
            .build();
}
```

Lesson:

> **Adding Spring Security changes the request pipeline. Every endpoint must now have an intentional access rule.**

---

## Debugging Mindset

When Spring Security behaves unexpectedly, ask:

```text
1. Did the request reach Spring Security?
2. Which SecurityFilterChain matched?
3. Is the endpoint permitAll, authenticated, or role-protected?
4. Was a credential extracted?
5. Did authentication succeed?
6. What authorities were created?
7. Did authorization fail?
8. Is failure 401 or 403?
9. Is method security also applied?
10. Is ownership/business authorization missing?
```

### Symptom Map

```text
401 Unauthorized
  -> missing/invalid credentials
  -> authentication failed
  -> AuthenticationEntryPoint triggered

403 Forbidden
  -> authenticated but insufficient authority
  -> AccessDeniedHandler triggered

Controller not called
  -> request stopped in security filter chain

Role check fails unexpectedly
  -> ROLE_ prefix issue
  -> claim-to-authority mapping issue

Public endpoint blocked
  -> request matcher wrong
  -> rule order wrong
  -> default anyRequest authenticated

Security works locally but not prod
  -> issuer/audience/JWK config
  -> proxy/header issue
  -> profile-specific security config
```

---

## Rule Ordering Matters

Example:

```java
.authorizeHttpRequests(auth -> auth
        .anyRequest().authenticated()
        .requestMatchers("/api/public/**").permitAll())
```

This is wrong because broad rule appears first.

Better:

```java
.authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/public/**").permitAll()
        .requestMatchers("/api/admin/**").hasRole("ADMIN")
        .anyRequest().authenticated())
```

Mental model:

```text
Specific rules first.
Catch-all rule last.
```

---

## Common Misconceptions

## Misconception 1 — “Spring Security runs inside controller”

No.

It runs before controller in the servlet filter chain.

## Misconception 2 — “401 and 403 are the same”

No.

```text
401 = not authenticated
403 = authenticated but not allowed
```

## Misconception 3 — “Authenticated means authorized”

No.

Authenticated only means identity is known.

Authorization decides what identity can do.

## Misconception 4 — “Roles and authorities are always obvious”

No.

Spring often expects role checks like `hasRole("ADMIN")` to map to authority `ROLE_ADMIN`.

Claim mapping mistakes are common.

## Misconception 5 — “URL authorization is enough”

Not always.

Business ownership checks often belong in service/domain logic.

Example:

```text
User can access /api/users/{id}/orders only when id matches current user or user is admin.
```

## Misconception 6 — “permitAll skips the security filter chain”

No.

The request still passes through filters.
Authorization decision allows it.

---

## Performance Considerations

Security adds request overhead.

Common costs:

```text
JWT parsing
signature verification
JWK retrieval/cache
database user lookup
password hashing during login
authorization expression evaluation
method security proxies
```

Best practices:

```text
Cache JWKs appropriately.
Avoid DB lookup on every request if JWT contains enough trusted claims.
Keep tokens reasonably small.
Avoid expensive remote calls inside filters.
Use method security where needed, not everywhere blindly.
Monitor 401/403 rates.
```

Password hashing:

```text
BCrypt/Argon2 are intentionally slow.
Good for login.
Bad if accidentally done on every request.
```

JWT verification should be local most of the time, not remote per request.

---

## Scalability Considerations

At scale:

```text
Security filters run on every request.
```

So small overhead multiplies.

Example:

```text
10,000 RPS
5 ms extra auth cost
= 50 seconds of CPU work per second across fleet
```

High-scale concerns:

```text
token size
signature algorithm cost
JWK cache misses
authorization lookup database calls
session storage
security context propagation in async code
method security overhead
```

For microservices:

```text
API Gateway may authenticate externally.
Service still should verify trusted identity/token or internal mTLS depending architecture.
```

Do not blindly trust headers unless gateway boundary is secure.

---

## Failure Investigation Playbook

## Step 1 — Identify status code

```text
401?
403?
404?
500?
```

Security mainly explains 401/403.

## Step 2 — Enable focused logs in lower environment

```properties
logging.level.org.springframework.security=DEBUG
```

Do not leave verbose security logs in production unless carefully controlled.

## Step 3 — Check matched rule

Ask:

```text
Which requestMatcher matched?
Was rule order correct?
Did catch-all rule capture it?
```

## Step 4 — Inspect Authentication

Log safely:

```text
principal id
authorities
authenticated flag
```

Do not log tokens.

## Step 5 — Check token validation

For JWT:

```text
issuer
audience
expiry
signature
JWK endpoint
clock skew
claim mapping
```

## Step 6 — Check business authorization

Ask:

```text
Even if URL allowed, should this user access this resource?
```

Ownership checks may belong in service.

---

## Interview Q&A

### Q1. What is Spring Security?

Strong answer:

> Spring Security is a security framework that protects application requests mainly through a servlet filter chain. It authenticates credentials, stores the resulting identity in the SecurityContext, authorizes access based on rules and authorities, and either allows the request to reach the controller or rejects it with 401/403.

### Q2. Where does Spring Security sit in request flow?

Strong answer:

> It sits before DispatcherServlet and controllers in the servlet filter chain. Requests pass through Spring Security filters first, so authentication or authorization failure can stop the request before controller code runs.

### Q3. Difference between authentication and authorization?

Strong answer:

> Authentication answers “who are you?” by validating credentials and creating an Authentication object. Authorization answers “are you allowed?” by checking that authenticated identity’s authorities against endpoint or method rules.

### Q4. Difference between 401 and 403?

Strong answer:

> 401 means the request is unauthenticated or credentials are invalid. 403 means the user is authenticated but lacks permission for the requested resource.

### Q5. What is SecurityContext?

Strong answer:

> SecurityContext stores the current Authentication for the request. It allows downstream code like controllers or services to know the current principal and authorities.

### Q6. What is SecurityFilterChain?

Strong answer:

> A SecurityFilterChain defines which security filters and authorization rules apply to matching requests. Spring Security can have multiple chains, and FilterChainProxy selects the matching one.

### Q7. Why might adding Spring Security starter break endpoints?

Strong answer:

> Because Spring Security auto-configuration activates when security classes are on the classpath. If no custom rules are defined, default security may require authentication for endpoints that were previously public.

---

## Production Checklist

```text
Request Rules
[ ] Public endpoints explicitly permitAll
[ ] Health/readiness endpoints configured intentionally
[ ] Admin endpoints require admin authority
[ ] Catch-all anyRequest rule is last
[ ] Rule order reviewed

Authentication
[ ] Token issuer/audience validated
[ ] Expiry checked
[ ] Signature validated
[ ] JWK cache configured
[ ] No token logged

Authorization
[ ] Authorities mapped correctly
[ ] ROLE_ prefix understood
[ ] Method security used where needed
[ ] Ownership checks implemented in service/domain
[ ] 401/403 behavior tested

Operations
[ ] Security debug logs available for lower env
[ ] 401/403 metrics monitored
[ ] Auth latency monitored
[ ] Gateway/service trust boundary clear
[ ] Health probes not accidentally blocked

Code Safety
[ ] Controllers do not parse tokens manually
[ ] Security config has tests
[ ] No broad permitAll accidentally exposes private APIs
[ ] Sensitive endpoints covered by integration tests
```

---

## One-Page Cheat Sheet

```text
Spring Security Mental Model
============================

Core Idea
---------
Security filter-chain gate before controller.

Authentication
--------------
Who are you?

Authorization
-------------
Are you allowed?

Main Flow
---------
Request
  -> Security filters
  -> extract credentials
  -> authenticate
  -> create Authentication
  -> store SecurityContext
  -> authorize
  -> controller or 401/403

401
---
Not authenticated or invalid credential.

403
---
Authenticated but not allowed.

SecurityContext
---------------
Holds current Authentication.

SecurityFilterChain
-------------------
Filters + access rules for matching requests.

Rule Ordering
-------------
Specific rules first.
anyRequest last.

Best Sentence
-------------
Spring Security authenticates and authorizes before controller code runs.
```

---

## Last-Minute Interview Revision

Do not say:

```text
Spring Security is for login.
```

Say:

```text
Spring Security is a filter-chain based security framework. It intercepts requests before controllers, authenticates credentials into an Authentication object, stores it in the SecurityContext, authorizes access using roles/authorities/rules, and either continues the request or returns 401/403.
```

Senior version:

```text
I debug Spring Security by separating authentication from authorization: first verify the filter chain matched and credentials became an Authentication, then inspect authorities and matched access rules. For business ownership, I enforce checks in service/domain logic in addition to URL-level rules.
```

---

## One Picture To Remember

```text
                    SPRING SECURITY GATE

HTTP Request
   |
   v
+------------------------------------------------+
| Security Filter Chain                          |
|------------------------------------------------|
| 1. Extract credential                          |
| 2. Validate credential                         |
| 3. Build Authentication                        |
| 4. Store in SecurityContext                    |
| 5. Check authorization rule                    |
+------------------------------------------------+
   |
   +------------------+-------------------+
   |                  |                   |
   v                  v                   v
Allowed              No identity          No permission
   |                  |                   |
   v                  v                   v
Controller          401                  403
```

Final retention sentence:

> **Spring Security is the request gatekeeper: authenticate who is calling, authorize what they can do, then either pass to controller or reject before business code runs.**
