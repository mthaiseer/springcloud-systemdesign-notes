# 038_Role_Based_Authorization.md
# MiniURLShortener — Role Based Authorization

> Core mental model: **Authentication proves who the user is; role-based authorization decides what that authenticated user is allowed to do. Roles are coarse-grained permission bundles placed after JWT authentication and before protected business actions.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Authentication vs Authorization](#4-authentication-vs-authorization)
- [5. Role Based Authorization Mental Model](#5-role-based-authorization-mental-model)
- [6. MiniURLShortener Role Model](#6-miniurlshortener-role-model)
- [7. Endpoint Access Matrix](#7-endpoint-access-matrix)
- [8. Database Role Design](#8-database-role-design)
- [9. JWT Claims For Roles](#9-jwt-claims-for-roles)
- [10. Spring Security Authorities Mental Model](#10-spring-security-authorities-mental-model)
- [11. SecurityFilterChain Authorization Rules](#11-securityfilterchain-authorization-rules)
- [12. Method Level Authorization](#12-method-level-authorization)
- [13. Ownership vs Role Authorization](#13-ownership-vs-role-authorization)
- [14. Admin APIs Authorization](#14-admin-apis-authorization)
- [15. User APIs Authorization](#15-user-apis-authorization)
- [16. Public APIs Authorization](#16-public-apis-authorization)
- [17. Custom Access Denied Response](#17-custom-access-denied-response)
- [18. Step-by-Step Dry Runs](#18-step-by-step-dry-runs)
- [19. Internal Execution Walkthrough](#19-internal-execution-walkthrough)
- [20. Production Failure Stories](#20-production-failure-stories)
- [21. Debugging Mindset](#21-debugging-mindset)
- [22. Testing Strategy](#22-testing-strategy)
- [23. Common Mistakes](#23-common-mistakes)
- [24. Interview-Ready Explanation](#24-interview-ready-explanation)
- [25. Senior Engineer Checklist](#25-senior-engineer-checklist)
- [26. One-Page Cheat Sheet](#26-one-page-cheat-sheet)
- [27. One Picture To Remember](#27-one-picture-to-remember)

---

## 1. Why This Exists

MiniURLShortener already has authentication:

```text
Client sends JWT
Spring Security validates token
SecurityContext stores authenticated user
Controller receives request
```

But authentication alone is not enough.

After login, different users should not have equal power.

Example:

```text
Anonymous visitor:
    can redirect using GET /{shortCode}

Logged-in user:
    can create URLs
    can view own URLs
    can update own URLs
    can delete own URLs

Admin:
    can block abusive URLs
    can view system abuse reports
    can disable users
    can inspect suspicious links
```

Without authorization, any logged-in user may call admin endpoints.

Bad system:

```text
User logs in
User gets valid JWT
User calls DELETE /api/v1/admin/users/42
Backend only checks token validity
Request succeeds
```

That is a serious security bug.

Correct system:

```text
User logs in
JWT is valid
Role is USER
Endpoint requires ADMIN
Spring Security returns 403 Forbidden
```

Production memory:

```text
Authentication asks: Who are you?
Authorization asks: Are you allowed to do this?
```

---

## 2. The One Core Mental Model

Role-based authorization is a gate after authentication.

```text
JWT Authentication = identity gate
Role Authorization = permission gate
```

ASCII:

```text
Request
  |
  v
+---------------------------+
| JWT Authentication Filter |
| Is token valid?           |
+---------------------------+
  |
  +-- no  --> 401 Unauthorized
  |
  v
+---------------------------+
| SecurityContext           |
| userId + roles            |
+---------------------------+
  |
  v
+---------------------------+
| Authorization Rules       |
| Is role allowed here?     |
+---------------------------+
  |
  +-- no  --> 403 Forbidden
  |
  v
+---------------------------+
| Controller / Service      |
| perform business action   |
+---------------------------+
```

One-line memory:

```text
A valid token opens the building; a role decides which rooms you can enter.
```

For MiniURLShortener:

```text
JWT proves user identity.
ROLE_USER allows normal user actions.
ROLE_ADMIN allows abuse-control and system-management actions.
Ownership checks protect user-specific resources.
```

---

## 3. Problem Statement

Build role-based authorization for MiniURLShortener.

It must support:

```text
1. Public redirect endpoint.
2. Public registration/login endpoints.
3. USER-only URL creation.
4. USER-only own URL management.
5. ADMIN-only abuse/moderation APIs.
6. Clear 401 vs 403 behavior.
7. Roles loaded from JWT claims.
8. Method-level checks for ownership-sensitive actions.
9. Testable endpoint access rules.
```

It should avoid:

```text
hardcoded role checks inside every controller
trusting userId from request body
allowing USER to call admin APIs
returning 401 when user is authenticated but forbidden
mixing authentication failure with authorization failure
putting too many tiny permissions into a simple role model too early
```

Out of scope for this chapter:

```text
Full permission-based RBAC
ABAC policy engine
OAuth2 authorization server internals
Multi-tenant organization roles
Dynamic runtime policy editor
```

This chapter builds a clean production-shaped role authorization model.

---

## 4. Authentication vs Authorization

Authentication:

```text
Verifies identity.
```

Authorization:

```text
Verifies permission.
```

Example:

```text
JWT is valid:
    authentication passed

User has ROLE_ADMIN:
    admin authorization passed
```

ASCII:

```text
          +-------------------+
Request ->| Authentication    |
          | Who are you?      |
          +-------------------+
                    |
                    v
          +-------------------+
          | Authorization     |
          | Can you do this?  |
          +-------------------+
```

Status code difference:

```text
401 Unauthorized:
    User is not authenticated.
    Missing token, invalid token, expired token.

403 Forbidden:
    User is authenticated but not allowed.
    Valid USER token calling ADMIN endpoint.
```

MiniURLShortener examples:

```text
No token creating URL:
    401

USER token blocking suspicious URL:
    403

ADMIN token blocking suspicious URL:
    allowed
```

Rule:

```text
Never say authorization if identity is unknown.
Never say authentication if identity is known but permission is missing.
```

---

## 5. Role Based Authorization Mental Model

A role is a coarse-grained permission bundle.

```text
ROLE_USER:
    normal product usage

ROLE_ADMIN:
    operational and moderation power
```

ASCII:

```text
+------------------+        +------------------------------+
| ROLE_USER        | -----> | create short URL             |
|                  |        | view own URLs                |
|                  |        | delete own URLs              |
+------------------+        +------------------------------+

+------------------+        +------------------------------+
| ROLE_ADMIN       | -----> | block URLs                   |
|                  |        | view abuse reports           |
|                  |        | disable users                |
|                  |        | inspect system stats         |
+------------------+        +------------------------------+
```

Role-based authorization is good when:

```text
access rules are simple
user groups are clear
product is early/mid stage
interview project needs clean security story
```

Role-based authorization becomes limited when:

```text
permissions become very fine-grained
organizations have custom policies
same user has different power in different tenant
rules depend on object attributes
```

For now, MiniURLShortener uses simple RBAC plus ownership checks.

```text
RBAC answers:
    Is this user a USER or ADMIN?

Ownership answers:
    Is this specific URL owned by this user?
```

---

## 6. MiniURLShortener Role Model

Start with two roles:

```text
USER
ADMIN
```

In Spring Security, these usually become authorities:

```text
ROLE_USER
ROLE_ADMIN
```

Important naming rule:

```text
hasRole("ADMIN") checks for authority "ROLE_ADMIN".
hasAuthority("ROLE_ADMIN") checks exact authority "ROLE_ADMIN".
```

Recommended enum:

```java
package com.miniurl.shortener.auth.domain;

public enum Role {
    USER,
    ADMIN
}
```

Why not store `ROLE_USER` directly in enum?

Both are acceptable, but a clean domain model often stores business role names:

```text
USER
ADMIN
```

Then the security adapter maps them to:

```text
ROLE_USER
ROLE_ADMIN
```

ASCII:

```text
Database role     JWT claim        Spring authority
-------------     ---------        ----------------
USER          ->  USER        ->   ROLE_USER
ADMIN         ->  ADMIN       ->   ROLE_ADMIN
```

Production rule:

```text
Keep domain role names simple.
Map them carefully at the Spring Security boundary.
```

---

## 7. Endpoint Access Matrix

Authorization should be visible as a table before code.

```text
+--------------------------------------+-----------+----------------+
| Endpoint                             | Access    | Reason         |
+--------------------------------------+-----------+----------------+
| POST /api/v1/auth/register           | PUBLIC    | create account |
| POST /api/v1/auth/login              | PUBLIC    | get token      |
| GET /{shortCode}                     | PUBLIC    | redirect link  |
| POST /api/v1/urls                    | USER      | create URL     |
| GET /api/v1/urls/me                  | USER      | own URLs       |
| GET /api/v1/urls/{id}                | OWNER/ADM | own/admin view |
| DELETE /api/v1/urls/{id}             | OWNER/ADM | own/admin del  |
| PATCH /api/v1/admin/urls/{id}/block  | ADMIN     | moderation     |
| GET /api/v1/admin/abuse-reports      | ADMIN     | abuse control  |
| GET /actuator/health                 | PUBLIC    | health check   |
| GET /actuator/metrics                | ADMIN     | sensitive      |
+--------------------------------------+-----------+----------------+
```

This table prevents random security decisions.

Architecture memory:

```text
Before writing security code, write the access matrix.
```

Why?

```text
1. Developers can reason clearly.
2. Test cases become obvious.
3. Product can approve behavior.
4. Security review becomes easier.
```

---

## 8. Database Role Design

Simple user table:

```sql
CREATE TABLE app_users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT chk_app_users_role
        CHECK (role IN ('USER', 'ADMIN')),

    CONSTRAINT chk_app_users_status
        CHECK (status IN ('ACTIVE', 'DISABLED'))
);
```

For a simple project, one role per user is enough.

For later production expansion:

```text
users
roles
user_roles
permissions
role_permissions
```

ASCII simple model:

```text
+----------------+
| app_users      |
+----------------+
| id             |
| email          |
| password_hash  |
| role           |
| status         |
+----------------+
```

ASCII advanced model:

```text
+-------+      +------------+      +-------+
| users | ---> | user_roles | <--- | roles |
+-------+      +------------+      +-------+
                                      |
                                      v
                               +------------------+
                               | role_permissions |
                               +------------------+
                                      |
                                      v
                               +-------------+
                               | permissions |
                               +-------------+
```

For MiniURLShortener phase 1:

```text
Use one role column.
Keep it simple.
Avoid enterprise RBAC rabbit hole.
```

---

## 9. JWT Claims For Roles

JWT should carry enough information for stateless authorization.

Example payload:

```json
{
  "sub": "42",
  "email": "user@example.com",
  "role": "USER",
  "iat": 1760000000,
  "exp": 1760003600
}
```

Admin payload:

```json
{
  "sub": "1",
  "email": "admin@example.com",
  "role": "ADMIN",
  "iat": 1760000000,
  "exp": 1760003600
}
```

JWT role flow:

```text
Login succeeds
  |
  v
Load user from DB
  |
  v
Read role = USER / ADMIN
  |
  v
Put role claim into JWT
  |
  v
Client sends token on future requests
  |
  v
JWT filter extracts role
  |
  v
Spring Security authority created
```

ASCII:

```text
DB user.role
    |
    v
JWT claim: role
    |
    v
JwtAuthenticationFilter
    |
    v
GrantedAuthority: ROLE_USER / ROLE_ADMIN
    |
    v
Authorization decision
```

Important security note:

```text
The server signs JWT.
Clients cannot safely choose their role.
Never trust role from request body.
```

Bad request design:

```json
{
  "email": "x@example.com",
  "role": "ADMIN"
}
```

Correct registration design:

```text
Normal registration always creates USER.
ADMIN creation is controlled manually or through a protected admin flow.
```

---

## 10. Spring Security Authorities Mental Model

Spring Security does not directly care about your domain enum.

It checks authorities.

```text
Domain role:
    USER

Spring authority:
    ROLE_USER
```

Code:

```java
package com.miniurl.shortener.auth.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public final class AuthorityMapper {

    private AuthorityMapper() {
    }

    public static List<GrantedAuthority> fromRole(String role) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }
}
```

JWT filter snippet:

```java
String userId = jwtService.extractSubject(token);
String role = jwtService.extractRole(token);

List<GrantedAuthority> authorities = AuthorityMapper.fromRole(role);

UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(userId, null, authorities);

SecurityContextHolder.getContext().setAuthentication(authentication);
```

Mental model:

```text
Authentication object contains:
    principal  -> user identity
    credentials -> usually null after JWT verification
    authorities -> permissions/roles used by authorization
```

ASCII:

```text
SecurityContext
    |
    v
Authentication
    |
    +-- principal: "42"
    +-- authenticated: true
    +-- authorities: [ROLE_USER]
```

Without authorities:

```text
JWT may authenticate user,
but authorization rules cannot allow protected role endpoints.
```

---

## 11. SecurityFilterChain Authorization Rules

Typical Spring Security configuration:

```java
package com.miniurl.shortener.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            RestAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/urls").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/urls/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/urls/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/{shortCode:[a-zA-Z0-9_-]+}").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
```

Important:

```text
Rule order matters.
More specific rules should appear before broader rules.
```

Bad:

```java
.anyRequest().permitAll()
.requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
```

The admin rule may never protect anything because everything was already permitted.

Good:

```text
public endpoints first
admin endpoints next
user endpoints next
catch-all authenticated last
```

---

## 12. Method Level Authorization

URL-level rules are not enough for ownership-sensitive actions.

Example:

```text
DELETE /api/v1/urls/100
```

Endpoint requires USER.

But which user owns URL 100?

```text
User 42 owns URL 100 -> allow
User 99 owns URL 100 -> deny
Admin owns nothing but can moderate -> allow if intended
```

Use service-level authorization for object ownership.

Example annotation:

```java
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public UrlDetailsResponse getUrlDetails(Long urlId) {
    ...
}
```

But ownership usually needs custom logic:

```java
@PreAuthorize("hasRole('ADMIN') or @urlSecurity.isOwner(#urlId, authentication.name)")
public void deleteUrl(Long urlId) {
    urlService.deleteUrl(urlId);
}
```

Custom security bean:

```java
package com.miniurl.shortener.url.security;

import com.miniurl.shortener.url.repository.ShortUrlRepository;
import org.springframework.stereotype.Component;

@Component("urlSecurity")
public class UrlSecurity {

    private final ShortUrlRepository shortUrlRepository;

    public UrlSecurity(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    public boolean isOwner(Long urlId, String authenticatedUserId) {
        Long userId = Long.valueOf(authenticatedUserId);

        return shortUrlRepository.existsByIdAndOwnerId(urlId, userId);
    }
}
```

Repository method:

```java
boolean existsByIdAndOwnerId(Long id, Long ownerId);
```

ASCII:

```text
Role check:
    Are you USER or ADMIN?

Ownership check:
    Does this exact URL belong to you?

Final decision:
    ADMIN OR owner -> allow
```

Rule:

```text
Endpoint rules protect routes.
Method/object rules protect resources.
```

---

## 13. Ownership vs Role Authorization

This distinction is critical in interviews.

Role authorization:

```text
Can this type of user access this type of endpoint?
```

Ownership authorization:

```text
Can this exact user access this exact resource?
```

Example bug:

```text
GET /api/v1/urls/777
requires ROLE_USER
User has ROLE_USER
API returns URL 777
But URL 777 belongs to another user
```

This is called broken object level authorization.

Correct flow:

```text
1. Authenticate JWT.
2. Confirm role is USER or ADMIN.
3. Load URL by id.
4. If ADMIN, allow.
5. If ownerId == authenticatedUserId, allow.
6. Else return 403.
```

ASCII:

```text
Authenticated USER
      |
      v
Endpoint allows USER?
      |
      +-- no --> 403
      |
      v
Resource owner == current user?
      |
      +-- yes --> allow
      |
      +-- no  --> 403
```

For MiniURLShortener:

```text
Role controls broad areas.
Ownership controls personal data.
Admin bypass is explicit, not accidental.
```

---

## 14. Admin APIs Authorization

Admin APIs are powerful.

Examples:

```text
PATCH /api/v1/admin/urls/{id}/block
PATCH /api/v1/admin/urls/{id}/unblock
GET /api/v1/admin/abuse-reports
GET /api/v1/admin/users/{id}
PATCH /api/v1/admin/users/{id}/disable
```

Controller:

```java
package com.miniurl.shortener.admin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/urls")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUrlController {

    private final AdminUrlService adminUrlService;

    public AdminUrlController(AdminUrlService adminUrlService) {
        this.adminUrlService = adminUrlService;
    }

    @PatchMapping("/{id}/block")
    public void blockUrl(@PathVariable Long id) {
        adminUrlService.blockUrl(id);
    }

    @PatchMapping("/{id}/unblock")
    public void unblockUrl(@PathVariable Long id) {
        adminUrlService.unblockUrl(id);
    }
}
```

Why class-level `@PreAuthorize`?

```text
All methods in this controller are admin-only.
Less repetition.
Lower chance of forgetting one method.
```

Still keep route-level config:

```text
/api/v1/admin/** requires ADMIN
```

Defense in depth:

```text
Filter-chain rule + method-level rule
```

ASCII:

```text
Request /api/v1/admin/urls/10/block
        |
        v
SecurityFilterChain: requires ADMIN
        |
        v
Controller @PreAuthorize: requires ADMIN
        |
        v
Admin service executes
```

---

## 15. User APIs Authorization

User APIs require login.

Examples:

```text
POST /api/v1/urls
GET /api/v1/urls/me
DELETE /api/v1/urls/{id}
```

Controller:

```java
package com.miniurl.shortener.url.controller;

import com.miniurl.shortener.url.dto.CreateShortUrlRequest;
import com.miniurl.shortener.url.dto.CreateShortUrlResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public CreateShortUrlResponse create(
            @Valid @RequestBody CreateShortUrlRequest request,
            Authentication authentication
    ) {
        Long userId = Long.valueOf(authentication.getName());
        return urlService.createShortUrl(request, userId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @urlSecurity.isOwner(#id, authentication.name)")
    public void delete(@PathVariable Long id) {
        urlService.deleteUrl(id);
    }
}
```

Important:

```text
Do not accept ownerId from request body.
```

Bad:

```json
{
  "longUrl": "https://example.com",
  "ownerId": 1
}
```

A malicious user can create URL as someone else.

Correct:

```text
ownerId comes from authenticated principal only.
```

ASCII:

```text
JWT subject = 42
      |
      v
Authentication.name = "42"
      |
      v
service.createShortUrl(request, 42)
      |
      v
DB owner_id = 42
```

---

## 16. Public APIs Authorization

Some endpoints stay public.

```text
POST /api/v1/auth/register
POST /api/v1/auth/login
GET /{shortCode}
GET /actuator/health
```

Public does not mean unsafe.

Public endpoints still need:

```text
validation
rate limiting
abuse detection
logging
safe error responses
```

Redirect endpoint:

```java
@GetMapping("/{shortCode}")
public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
    String longUrl = redirectService.resolve(shortCode);

    return ResponseEntity.status(302)
            .header("Location", longUrl)
            .build();
}
```

Why public redirect?

```text
Anyone with a short URL should be able to use it.
```

But admin management is not public:

```text
Blocking, deleting, or inspecting URLs requires authorization.
```

ASCII:

```text
Public redirect:
    shortCode -> longUrl

Protected management:
    JWT + role + ownership -> mutate/view private data
```

---

## 17. Custom Access Denied Response

Authentication failure should return 401.

Authorization failure should return 403.

Access denied handler:

```java
package com.miniurl.shortener.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniurl.shortener.common.error.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public RestAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "ACCESS_DENIED",
                "You do not have permission to access this resource",
                request.getRequestURI(),
                List.of()
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
```

Authentication entry point:

```java
package com.miniurl.shortener.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniurl.shortener.common.error.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "UNAUTHORIZED",
                "Authentication is required to access this resource",
                request.getRequestURI(),
                List.of()
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
```

Mental model:

```text
Missing or invalid identity -> AuthenticationEntryPoint -> 401
Known identity but missing permission -> AccessDeniedHandler -> 403
```

---

## 18. Step-by-Step Dry Runs

### Dry Run 1: Anonymous user creates URL

Request:

```http
POST /api/v1/urls
Authorization: missing
```

Flow:

```text
1. Request enters SecurityFilterChain.
2. JWT filter finds no token.
3. Endpoint requires USER or ADMIN.
4. Authentication is missing.
5. AuthenticationEntryPoint returns 401.
```

Response:

```json
{
  "status": 401,
  "error": "Unauthorized",
  "code": "UNAUTHORIZED",
  "message": "Authentication is required to access this resource"
}
```

---

### Dry Run 2: USER creates URL

JWT:

```json
{
  "sub": "42",
  "role": "USER"
}
```

Flow:

```text
1. JWT filter validates token.
2. SecurityContext stores principal 42.
3. Authorities contain ROLE_USER.
4. POST /api/v1/urls requires USER or ADMIN.
5. Authorization passes.
6. Controller uses authentication.name as ownerId.
7. URL is saved with owner_id = 42.
```

Result:

```text
201 Created
```

---

### Dry Run 3: USER calls admin block endpoint

Request:

```http
PATCH /api/v1/admin/urls/10/block
Authorization: Bearer valid-user-token
```

Flow:

```text
1. JWT is valid.
2. Authentication succeeds.
3. User has ROLE_USER.
4. Endpoint requires ROLE_ADMIN.
5. Authorization fails.
6. AccessDeniedHandler returns 403 ACCESS_DENIED.
```

Important:

```text
This is not 401.
The user is known but not allowed.
```

---

### Dry Run 4: ADMIN blocks suspicious URL

JWT:

```json
{
  "sub": "1",
  "role": "ADMIN"
}
```

Flow:

```text
1. JWT filter validates token.
2. Authority is ROLE_ADMIN.
3. /api/v1/admin/** requires ADMIN.
4. Filter-chain authorization passes.
5. @PreAuthorize on controller also passes.
6. Admin service marks URL as BLOCKED.
```

Result:

```text
204 No Content
```

---

### Dry Run 5: USER deletes own URL

Data:

```text
url_id = 100
owner_id = 42
JWT subject = 42
role = USER
```

Flow:

```text
1. JWT validates.
2. Role USER passes broad endpoint rule.
3. @urlSecurity.isOwner(100, "42") runs.
4. Repository confirms owner_id = 42.
5. Authorization passes.
6. URL is deleted or marked DELETED.
```

---

### Dry Run 6: USER deletes another user's URL

Data:

```text
url_id = 100
owner_id = 99
JWT subject = 42
role = USER
```

Flow:

```text
1. JWT validates.
2. Role USER passes broad endpoint rule.
3. Ownership check runs.
4. owner_id is 99, not 42.
5. User is not ADMIN.
6. Access denied.
7. Response is 403.
```

This is the most important authorization bug to prevent.

---

## 19. Internal Execution Walkthrough

Full RBAC request path:

```text
1. HTTP request enters servlet container.
2. Spring Security filter chain starts.
3. JwtAuthenticationFilter extracts Bearer token.
4. JwtService validates signature and expiry.
5. JwtService extracts subject and role.
6. Filter maps role to ROLE_USER or ROLE_ADMIN.
7. Authentication object is created.
8. SecurityContext is populated.
9. AuthorizationFilter evaluates endpoint rule.
10. If role is sufficient, controller method is called.
11. @PreAuthorize may run method-level check.
12. Service performs business action.
```

ASCII:

```text
+--------+      +------------+      +-----------------+      +-------------+
| Client | ---> | JWT Filter | ---> | Authorization   | ---> | Controller  |
+--------+      +------------+      +-----------------+      +-------------+
                     |                    |                       |
                     | validates token    | checks role            | method rules
                     v                    v                       v
              SecurityContext       401 / 403 / allow         Service
```

Where failures happen:

```text
Missing token:
    before controller -> 401

Expired token:
    before controller -> 401

Wrong role:
    before controller or at @PreAuthorize -> 403

Wrong owner:
    at method-level authorization -> 403
```

---

## 20. Production Failure Stories

### Failure Story 1: Admin endpoint protected only by frontend

Frontend hides admin button.

But backend endpoint is open:

```text
PATCH /api/v1/admin/urls/10/block
```

A normal user calls it with curl.

Root cause:

```text
Authorization existed only in UI.
```

Fix:

```text
Backend enforces ROLE_ADMIN.
```

Lesson:

```text
Frontend authorization improves UX. Backend authorization provides security.
```

---

### Failure Story 2: USER can delete another user's URL

Endpoint requires login:

```text
DELETE /api/v1/urls/{id}
```

Any USER can delete any id.

Root cause:

```text
Role check exists, ownership check missing.
```

Fix:

```text
Require ADMIN or owner.
```

Lesson:

```text
RBAC does not replace object-level authorization.
```

---

### Failure Story 3: Role claim trusted from request body

Registration request accepts:

```json
{
  "email": "hacker@example.com",
  "password": "pass",
  "role": "ADMIN"
}
```

Root cause:

```text
Client-controlled role assignment.
```

Fix:

```text
Registration always creates USER.
ADMIN is assigned through controlled internal process.
```

Lesson:

```text
Never allow public clients to choose privileged roles.
```

---

### Failure Story 4: 403 returned as 500

AccessDeniedException is not handled cleanly.

Client sees:

```text
500 Internal Server Error
```

Root cause:

```text
No AccessDeniedHandler or incorrect exception handling setup.
```

Fix:

```text
Configure accessDeniedHandler in SecurityFilterChain.
```

Lesson:

```text
Security exceptions need clean API responses too.
```

---

### Failure Story 5: Admin role remains after user is demoted

User has old JWT:

```text
role = ADMIN
exp = 24 hours later
```

Admin is demoted in DB.

Old token still works until expiry.

Root cause:

```text
Stateless JWT contains role snapshot.
```

Fix options:

```text
short access token TTL
refresh token rotation
token version claim
server-side revocation list for high-risk changes
re-check DB for critical admin actions
```

Lesson:

```text
Stateless JWT is fast, but role changes are not instantly reflected unless you design for it.
```

---

## 21. Debugging Mindset

When authorization fails, ask:

```text
Is token missing?
Is token expired?
Is JWT signature valid?
Was SecurityContext populated?
What authorities are present?
Does endpoint rule require ROLE_ADMIN or ADMIN?
Are we using hasRole or hasAuthority correctly?
Did @EnableMethodSecurity exist?
Did @PreAuthorize run?
Did ownership query return false?
Is rule order correct?
```

Debug map:

```text
401 UNAUTHORIZED:
    no valid authentication

403 ACCESS_DENIED:
    authenticated but missing role or ownership

Admin endpoint open:
    rule order or matcher issue

@PreAuthorize ignored:
    missing @EnableMethodSecurity

hasRole("ROLE_ADMIN") failing:
    should usually be hasRole("ADMIN")
```

Useful logging fields:

```text
path
method
authenticatedUserId
authorities
requiredRole
resourceId
ownerId
decision
correlationId
```

Do not log:

```text
raw JWT
passwords
full authorization header
sensitive URLs with tokens
```

Golden rule:

```text
Authorization debugging starts by comparing required authority with actual authority.
```

---

## 22. Testing Strategy

Test authorization as seriously as business logic.

### Endpoint rule tests

```text
anonymous POST /api/v1/urls -> 401
USER POST /api/v1/urls -> allowed
USER PATCH /api/v1/admin/urls/1/block -> 403
ADMIN PATCH /api/v1/admin/urls/1/block -> allowed
```

### Ownership tests

```text
USER deletes own URL -> allowed
USER deletes another user's URL -> 403
ADMIN deletes any URL -> allowed if business permits
```

### JWT role mapping tests

```text
role USER claim -> ROLE_USER authority
role ADMIN claim -> ROLE_ADMIN authority
missing role claim -> reject token or no authority
invalid role claim -> reject token
```

### Method security tests

```text
@PreAuthorize owner expression returns true -> allowed
@PreAuthorize owner expression returns false -> denied
```

Example MockMvc style:

```java
mockMvc.perform(patch("/api/v1/admin/urls/10/block")
        .header("Authorization", "Bearer " + userToken))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
```

Testing rule:

```text
For every protected endpoint, test anonymous, USER, and ADMIN.
```

Authorization test matrix:

```text
+----------+-----------+---------+-------+
| Endpoint | Anonymous | USER    | ADMIN |
+----------+-----------+---------+-------+
| register | allow     | allow   | allow |
| login    | allow     | allow   | allow |
| redirect | allow     | allow   | allow |
| create   | 401       | allow   | allow |
| own list | 401       | allow   | allow |
| admin    | 401       | 403     | allow |
+----------+-----------+---------+-------+
```

---

## 23. Common Mistakes

### Mistake 1: Confusing 401 and 403

Wrong:

```text
Logged-in USER calling admin endpoint -> 401
```

Correct:

```text
Logged-in USER calling admin endpoint -> 403
```

### Mistake 2: Using hasRole incorrectly

Wrong:

```java
hasRole("ROLE_ADMIN")
```

Usually correct:

```java
hasRole("ADMIN")
```

Because Spring adds `ROLE_` prefix for `hasRole`.

### Mistake 3: Missing @EnableMethodSecurity

Wrong:

```text
@PreAuthorize annotations exist but do nothing.
```

Correct:

```java
@EnableMethodSecurity
```

### Mistake 4: Trusting ownerId from request

Wrong:

```text
ownerId comes from JSON body.
```

Correct:

```text
ownerId comes from JWT subject.
```

### Mistake 5: Only role check, no ownership check

Wrong:

```text
ROLE_USER can access any /urls/{id}.
```

Correct:

```text
ROLE_USER can access only own resources.
```

### Mistake 6: Public admin registration

Wrong:

```text
/register accepts role=ADMIN.
```

Correct:

```text
/register creates USER only.
```

### Mistake 7: Overengineering permissions too early

Wrong:

```text
Build full role-permission-policy engine before core app works.
```

Correct:

```text
Start with USER/ADMIN + ownership. Expand only when product needs it.
```

---

## 24. Interview-Ready Explanation

If interviewer asks:

```text
How would you implement role-based authorization in your URL shortener?
```

Strong answer:

```text
I would first separate authentication from authorization. JWT authentication validates the token and places the authenticated user id plus authorities into the SecurityContext. Then Spring Security authorization rules decide which endpoints require public access, USER access, or ADMIN access. In the JWT, I would include a server-signed role claim such as USER or ADMIN and map it to Spring authorities like ROLE_USER and ROLE_ADMIN. Public endpoints include login, registration, health check, and redirect. User endpoints like creating and listing URLs require USER or ADMIN. Admin moderation endpoints like blocking abusive links require ADMIN.

For resource-specific actions, RBAC alone is not enough. A normal USER should only manage their own URLs, so I would add object-level authorization using method security, for example ADMIN or @urlSecurity.isOwner(urlId, authentication.name). I would return 401 for missing or invalid authentication, 403 for authenticated users without permission, and use a consistent JSON error response. I would also test each endpoint with anonymous, USER, and ADMIN tokens.
```

Why this is strong:

```text
1. Separates authentication and authorization.
2. Explains JWT role mapping.
3. Uses Spring Security authorities correctly.
4. Defines public, user, and admin endpoint groups.
5. Mentions object-level authorization.
6. Correctly distinguishes 401 and 403.
7. Includes testing strategy.
8. Avoids overengineering permission systems too early.
```

Senior one-liner:

```text
RBAC protects endpoint classes, and ownership checks protect individual resources.
```

---

## 25. Senior Engineer Checklist

Before moving forward, confirm:

```text
[ ] Access matrix is documented
[ ] USER and ADMIN roles are defined
[ ] DB role constraint exists
[ ] Registration cannot create ADMIN publicly
[ ] JWT contains server-signed role claim
[ ] JWT filter maps role to ROLE_USER / ROLE_ADMIN
[ ] SecurityContext contains authorities
[ ] SecurityFilterChain protects admin endpoints
[ ] User endpoints require USER or ADMIN
[ ] Public endpoints are intentionally public
[ ] @EnableMethodSecurity is enabled
[ ] Admin controllers use @PreAuthorize where useful
[ ] Ownership checks exist for /urls/{id}
[ ] ownerId comes from authentication, not request body
[ ] 401 response is handled by AuthenticationEntryPoint
[ ] 403 response is handled by AccessDeniedHandler
[ ] Tests cover anonymous, USER, ADMIN
[ ] Tests cover owner vs non-owner
[ ] Logs do not expose raw JWT
```

If these are checked, your authorization model is production-shaped.

---

## 26. One-Page Cheat Sheet

```text
Core mental model:
Authentication proves identity.
Authorization checks permission.

401:
No valid authentication.
Missing/invalid/expired token.

403:
Authenticated but not allowed.
Wrong role or not owner.

Roles:
USER  -> ROLE_USER
ADMIN -> ROLE_ADMIN

Spring checks:
hasRole("ADMIN") checks ROLE_ADMIN
hasAuthority("ROLE_ADMIN") checks exact authority

Public:
POST /auth/register
POST /auth/login
GET /{shortCode}
GET /actuator/health

USER/ADMIN:
POST /api/v1/urls
GET /api/v1/urls/me
DELETE own /api/v1/urls/{id}

ADMIN:
/api/v1/admin/**
block/unblock URLs
abuse reports
user disable

Ownership rule:
ADMIN or resource owner

Never trust:
role from request body
ownerId from request body
frontend-only authorization

Testing:
anonymous -> 401
USER on admin -> 403
ADMIN on admin -> allow
USER owner -> allow
USER non-owner -> 403
```

---

## 27. One Picture To Remember

```text
                  ROLE BASED AUTHORIZATION MENTAL MODEL

                         "Who can enter this room?"

Client Request
     |
     v
+------------------------------+
| JWT Authentication           |
| token valid?                 |
+------------------------------+
     |
     +-- no ------------------> 401 UNAUTHORIZED
     |
     v
+------------------------------+
| SecurityContext              |
| principal = userId           |
| authorities = ROLE_USER      |
+------------------------------+
     |
     v
+------------------------------+
| Endpoint RBAC                |
| public / USER / ADMIN        |
+------------------------------+
     |
     +-- wrong role ----------> 403 ACCESS_DENIED
     |
     v
+------------------------------+
| Object Authorization         |
| owner or ADMIN?              |
+------------------------------+
     |
     +-- not owner/admin -----> 403 ACCESS_DENIED
     |
     v
+------------------------------+
| Business Action              |
| create / delete / block URL  |
+------------------------------+

FINAL MEMORY:

Authentication opens the building.
RBAC opens role-specific rooms.
Ownership opens resource-specific lockers.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Authentication proves who the user is; authorization decides what the user can do.
2. JWT roles must be server-signed and mapped to Spring authorities.
3. hasRole("ADMIN") checks for ROLE_ADMIN, not plain ADMIN.
4. RBAC protects endpoint groups, but ownership protects individual resources.
5. Missing/invalid token is 401; valid token with insufficient permission is 403.
```

After this chapter, the security phase becomes stronger:

```text
036 Spring Security Basics
037 JWT Authentication
038 Role Based Authorization
```

Next possible chapters:

```text
039_Ownership_Authorization.md
040_Refresh_Token_Rotation.md
041_Logout_And_Token_Blacklist.md
042_Security_Testing_With_MockMvc.md
```
