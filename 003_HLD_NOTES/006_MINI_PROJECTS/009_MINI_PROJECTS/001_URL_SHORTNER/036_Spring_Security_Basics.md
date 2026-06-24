# 036_Spring_Security_Basics.md
# MiniURLShortener — Spring Security Basics

> Core mental model: **Spring Security is the security checkpoint chain in front of your application. Every HTTP request must pass through ordered filters before it is allowed to reach your controller. Authentication answers “who are you?”, authorization answers “are you allowed?”, and secure defaults make unsafe access impossible unless you explicitly open it.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Security Is Not One Feature](#4-security-is-not-one-feature)
- [5. Authentication vs Authorization](#5-authentication-vs-authorization)
- [6. Spring Security Big Picture](#6-spring-security-big-picture)
- [7. Request Flow Mental Model](#7-request-flow-mental-model)
- [8. Filter Chain Mental Model](#8-filter-chain-mental-model)
- [9. SecurityContext Mental Model](#9-securitycontext-mental-model)
- [10. Stateless vs Stateful Security](#10-stateless-vs-stateful-security)
- [11. MiniURLShortener Endpoint Security](#11-miniurlshortener-endpoint-security)
- [12. Minimal Security Configuration](#12-minimal-security-configuration)
- [13. Public Redirect Endpoint](#13-public-redirect-endpoint)
- [14. Protected Create API](#14-protected-create-api)
- [15. Password Encoding Basics](#15-password-encoding-basics)
- [16. UserDetailsService Basics](#16-userdetailsservice-basics)
- [17. HTTP Basic for Learning](#17-http-basic-for-learning)
- [18. JWT Preview Mental Model](#18-jwt-preview-mental-model)
- [19. CSRF Basics](#19-csrf-basics)
- [20. CORS Basics](#20-cors-basics)
- [21. Security Error Handling](#21-security-error-handling)
- [22. Method-Level Security](#22-method-level-security)
- [23. Security Logging Mindset](#23-security-logging-mindset)
- [24. Step-by-Step Dry Runs](#24-step-by-step-dry-runs)
- [25. Internal Execution Walkthrough](#25-internal-execution-walkthrough)
- [26. Testing Strategy](#26-testing-strategy)
- [27. Production Failure Stories](#27-production-failure-stories)
- [28. Debugging Mindset](#28-debugging-mindset)
- [29. Common Mistakes](#29-common-mistakes)
- [30. Interview-Ready Explanation](#30-interview-ready-explanation)
- [31. Senior Engineer Checklist](#31-senior-engineer-checklist)
- [32. One-Page Cheat Sheet](#32-one-page-cheat-sheet)
- [33. One Picture To Remember](#33-one-picture-to-remember)

---

## 1. Why This Exists

MiniURLShortener now has important backend pieces:

```text
POST /api/v1/urls        create short URL
GET /{shortCode}         redirect to long URL
GET /actuator/health     health check
```

Without security, every endpoint is open.

That means any anonymous user could:

```text
create unlimited short URLs
try malicious long URLs
hit admin endpoints
discover internal actuator data
spam your database
abuse your redirect service
```

A URL shortener looks simple, but in production it becomes a security-sensitive system.

Attackers may use it for:

```text
phishing links
spam campaigns
malware redirects
brand impersonation
traffic amplification
actuator endpoint discovery
credential stuffing if login exists
```

Bad mental model:

```text
Security is added at the controller.
```

Better mental model:

```text
Security wraps the whole HTTP request before controller logic starts.
```

ASCII:

```text
Unsafe design:

Client
  |
  v
Controller directly
  |
  v
Service / DB


Secure design:

Client
  |
  v
Spring Security Filter Chain
  |
  +-- reject unauthenticated request
  +-- reject unauthorized request
  +-- block unsafe request
  |
  v
Controller
  |
  v
Service / DB
```

Production memory:

```text
Controllers should not be your first security boundary.
Spring Security should be.
```

---

## 2. The One Core Mental Model

Spring Security is:

```text
A CHECKPOINT CHAIN BEFORE YOUR CONTROLLER
```

Each request passes through a list of filters.

Each filter has one small job:

```text
read credentials
validate credentials
create authenticated user
store user in SecurityContext
check authorization rules
handle security errors
continue or stop request
```

ASCII:

```text
HTTP Request
    |
    v
+-----------------------------+
| Security Filter Chain       |
|                             |
| 1. CORS / CSRF checks       |
| 2. Authentication filter    |
| 3. SecurityContext setup    |
| 4. Authorization filter     |
| 5. Exception translation    |
+-----------------------------+
    |
    +-- denied --> 401 / 403
    |
    v
Controller
    |
    v
Service
    |
    v
Database
```

One-line memory:

```text
Spring Security is not magic; it is ordered filters deciding whether a request may continue.
```

For MiniURLShortener:

```text
GET /abc123 should be public.
POST /api/v1/urls should require login or token.
GET /actuator/health may be public.
GET /actuator/env should never be public.
Admin endpoints should require ADMIN role.
```

---

## 3. Problem Statement

Build a clean security foundation for MiniURLShortener.

It must answer:

```text
1. Which endpoints are public?
2. Which endpoints require authentication?
3. Which endpoints require ADMIN?
4. How does Spring Security intercept requests?
5. What is SecurityContext?
6. What is the difference between 401 and 403?
7. When should CSRF be enabled or disabled?
8. How do we avoid exposing actuator endpoints?
9. How do we test security rules?
10. How do we prepare for JWT later?
```

This chapter focuses on basics.

Out of scope for this chapter:

```text
1. Full JWT implementation.
2. OAuth2 login with Google/GitHub.
3. Refresh token rotation.
4. Multi-tenant authorization.
5. Fine-grained ownership checks.
6. Production WAF/CDN rules.
7. Complete abuse detection pipeline.
```

This chapter builds the mental model that makes later JWT and OAuth2 easy.

---

## 4. Security Is Not One Feature

Security is many decisions working together.

```text
Authentication:
    who is making the request?

Authorization:
    what can this user do?

Transport security:
    is traffic encrypted?

Input validation:
    is request data safe?

Rate limiting:
    is request volume abusive?

Auditing:
    who did what?

Secrets management:
    are passwords/tokens protected?

Error handling:
    do responses leak information?
```

ASCII:

```text
                  SECURITY
                     |
     +---------------+---------------+
     |               |               |
Authentication   Authorization    Validation
     |               |               |
"who?"          "allowed?"       "safe input?"
     |
     +---------------+---------------+
                     |
              Logging / Auditing
                     |
              "what happened?"
```

In this chapter, focus on:

```text
HTTP request security
authentication
authorization
security filters
basic configuration
security error handling
```

Important senior mindset:

```text
Security is not only login.
Security is controlling every path into the system.
```

---

## 5. Authentication vs Authorization

Authentication and authorization are often confused.

Authentication:

```text
Who are you?
```

Authorization:

```text
Are you allowed to do this?
```

Example:

```text
User sends username/password.
System verifies credentials.
User is authenticated as mohamed.
System checks if mohamed can create short URLs.
```

ASCII:

```text
Request
  |
  v
Authentication
"Who is this?"
  |
  +-- no valid identity --> 401 Unauthorized
  |
  v
Authorization
"Can this identity access this endpoint?"
  |
  +-- not allowed --> 403 Forbidden
  |
  v
Controller
```

Status code memory:

```text
401 = I do not know who you are.
403 = I know who you are, but you are not allowed.
```

MiniURLShortener examples:

```text
Anonymous POST /api/v1/urls
    -> 401

Authenticated USER calls /api/v1/admin/blocked-links
    -> 403

Authenticated ADMIN calls /api/v1/admin/blocked-links
    -> allowed
```

Interview one-liner:

```text
Authentication establishes identity; authorization checks permissions for that identity.
```

---

## 6. Spring Security Big Picture

Spring Security integrates with the Servlet filter mechanism.

Before a request reaches `DispatcherServlet`, it passes through filters.

Spring MVC path:

```text
Tomcat
  |
  v
Servlet Filters
  |
  v
DispatcherServlet
  |
  v
Controller
```

Spring Security inserts a special filter:

```text
DelegatingFilterProxy
```

That delegates to Spring-managed security filters.

ASCII:

```text
HTTP Request
    |
    v
+--------------------+
| Tomcat             |
+--------------------+
    |
    v
+--------------------+
| DelegatingFilterProxy
| "ask Spring Security"
+--------------------+
    |
    v
+-----------------------------+
| Spring Security FilterChain  |
+-----------------------------+
    |
    v
+--------------------+
| DispatcherServlet  |
+--------------------+
    |
    v
+--------------------+
| Controller         |
+--------------------+
```

Why this design matters:

```text
Security runs before controller code.
Security can stop the request early.
Security is centralized.
Security rules are not scattered across controllers.
```

Bad approach:

```java
@PostMapping("/api/v1/urls")
public ResponseEntity<?> create(...) {
    if (!isLoggedIn()) {
        return ResponseEntity.status(401).build();
    }
    ...
}
```

Better approach:

```java
.authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.POST, "/api/v1/urls").authenticated()
)
```

Controller should focus on business use case.

Security configuration should decide access.

---

## 7. Request Flow Mental Model

A secured request has two possible paths:

```text
happy path
blocked path
```

Happy path:

```text
Request has valid credentials.
Spring Security authenticates user.
Authorization rule allows endpoint.
Controller executes.
```

Blocked path:

```text
Request has no credentials or insufficient role.
Spring Security stops request.
Controller never runs.
```

ASCII:

```text
POST /api/v1/urls
Authorization: Basic ...

        |
        v
+------------------------+
| Security filters       |
+------------------------+
        |
        v
Credentials valid?
        |
   +----+----+
   |         |
  no        yes
   |         |
 401         v
       Role/permission allowed?
              |
         +----+----+
         |         |
        no        yes
         |         |
        403        v
              Controller runs
```

Key point:

```text
If security denies the request, controller method is not called.
```

This is important for debugging.

If breakpoint inside controller is not hit:

```text
maybe request was blocked by security before MVC.
```

---

## 8. Filter Chain Mental Model

Spring Security uses many filters.

You do not need to memorize every filter at first.

You need the mental model:

```text
Filters are ordered checkpoints.
```

Common filter responsibilities:

```text
CorsFilter:
    handles cross-origin browser requests

CsrfFilter:
    checks CSRF token for state-changing browser requests

BasicAuthenticationFilter:
    reads Authorization: Basic header

BearerTokenAuthenticationFilter:
    reads Authorization: Bearer token

UsernamePasswordAuthenticationFilter:
    handles form login

AnonymousAuthenticationFilter:
    gives anonymous identity if no login exists

ExceptionTranslationFilter:
    converts security exceptions into 401/403

AuthorizationFilter:
    checks access rules
```

ASCII:

```text
Request
  |
  v
+-----------------------------+
| 1. CORS                     |
+-----------------------------+
  |
  v
+-----------------------------+
| 2. CSRF                     |
+-----------------------------+
  |
  v
+-----------------------------+
| 3. Authentication           |
| Basic / JWT / Form          |
+-----------------------------+
  |
  v
+-----------------------------+
| 4. Anonymous fallback       |
+-----------------------------+
  |
  v
+-----------------------------+
| 5. Authorization            |
| permitAll / authenticated   |
| hasRole / hasAuthority      |
+-----------------------------+
  |
  v
Controller or 401/403
```

Memory:

```text
Authentication filters build the user.
Authorization filters check the user.
Exception filters translate failures.
```

---

## 9. SecurityContext Mental Model

After authentication succeeds, Spring Security stores the authenticated user in:

```text
SecurityContext
```

The current request can access it.

ASCII:

```text
Authorization header
      |
      v
Authentication filter
      |
      v
Authentication object
      |
      v
SecurityContextHolder
      |
      v
Controller / Service can read current user
```

`Authentication` usually contains:

```text
principal      -> user identity
credentials    -> password/token, often removed after auth
authorities    -> roles/permissions
authenticated  -> true/false
```

Example:

```text
principal: mohamed
authorities: ROLE_USER
authenticated: true
```

Access current user:

```java
@GetMapping("/api/v1/me")
public String me(Authentication authentication) {
    return authentication.getName();
}
```

Or:

```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
```

Important mental model:

```text
SecurityContext is request-scoped identity memory.
```

In a typical servlet app, this context is bound to the current thread during request processing.

ASCII:

```text
Request Thread
    |
    +-- SecurityContext
            |
            +-- Authentication
                    |
                    +-- username
                    +-- roles
```

Common mistake:

```text
Assuming SecurityContext is a global permanent login store.
```

It is not.

It is the current request's security state.

---

## 10. Stateless vs Stateful Security

Stateful security:

```text
Server stores login session.
Browser stores session cookie.
Each request sends cookie.
Server looks up session.
```

Stateless security:

```text
Server does not store session.
Client sends token every request.
Server validates token every request.
```

ASCII:

```text
Stateful session:

Login
  |
  v
Server creates session
  |
  v
Browser gets JSESSIONID cookie
  |
  v
Next request sends cookie
  |
  v
Server finds session


Stateless JWT:

Login
  |
  v
Server returns signed token
  |
  v
Client stores token
  |
  v
Next request sends Bearer token
  |
  v
Server validates signature
```

For REST APIs:

```text
stateless security is common
```

For server-rendered web apps:

```text
stateful session is common
```

MiniURLShortener direction:

```text
Phase now:
    HTTP Basic for learning and testing

Later:
    JWT Bearer token for production API

Session policy:
    STATELESS for REST API
```

Configuration idea:

```java
.sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```

Memory:

```text
Stateful remembers login on server.
Stateless proves identity on every request.
```

---

## 11. MiniURLShortener Endpoint Security

Define endpoint rules clearly.

Recommended v1:

```text
Public:
    GET /{shortCode}
    GET /actuator/health
    GET /actuator/info

Authenticated:
    POST /api/v1/urls
    GET /api/v1/urls/my
    DELETE /api/v1/urls/{id}

Admin only:
    GET /api/v1/admin/**
    POST /api/v1/admin/block/{shortCode}

Never public:
    /actuator/env
    /actuator/beans
    /actuator/configprops
    /actuator/heapdump
```

ASCII:

```text
                 MiniURLShortener Routes

+-------------------------------+-------------------------+
| Endpoint                      | Access                  |
+-------------------------------+-------------------------+
| GET /abc123                   | public                  |
| GET /actuator/health          | public                  |
| POST /api/v1/urls             | authenticated USER      |
| GET /api/v1/urls/my           | authenticated USER      |
| DELETE /api/v1/urls/{id}      | owner or ADMIN later    |
| /api/v1/admin/**              | ADMIN only              |
| sensitive actuator endpoints  | blocked/restricted      |
+-------------------------------+-------------------------+
```

Important design decision:

```text
Redirect endpoint must stay public.
Create endpoint should not stay unlimited-public in production.
```

Why?

```text
Redirect is product behavior.
Create is abuse surface.
```

One-liner:

```text
Reading a public short link is public; creating links should be controlled.
```

---

## 12. Minimal Security Configuration

Add dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Create config:

```text
src/main/java/com/miniurl/shortener/security/SecurityConfig.java
```

Code:

```java
package com.miniurl.shortener.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // REST API usually does not use browser form CSRF tokens.
            .csrf(csrf -> csrf.disable())

            // For API, do not create server-side sessions.
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Endpoint authorization rules.
            .authorizeHttpRequests(auth -> auth

                    // Public redirect endpoint. Keep specific API rules before broad patterns.
                    .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/info").permitAll()

                    // Public redirect examples. In production, avoid matching too broadly
                    // if it conflicts with frontend/static routes.
                    .requestMatchers(HttpMethod.GET, "/{shortCode:[a-zA-Z0-9_-]{4,32}}").permitAll()

                    // Create short URL requires authenticated user.
                    .requestMatchers(HttpMethod.POST, "/api/v1/urls").authenticated()

                    // Admin endpoints need ADMIN role.
                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                    // Everything else needs authentication by default.
                    .anyRequest().authenticated()
            )

            // HTTP Basic is simple for learning and local testing.
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
```

Why this is good for learning:

```text
1. Shows permitAll.
2. Shows authenticated.
3. Shows hasRole.
4. Shows stateless API mode.
5. Shows basic auth.
6. Keeps default-deny mindset.
```

Security rule mindset:

```text
Open only what must be open.
Protect everything else.
```

---

## 13. Public Redirect Endpoint

Redirect endpoint should be public:

```text
GET /abc123
```

Because users clicking a short link usually are not logged in.

Flow:

```text
Browser
  |
  v
GET /abc123
  |
  v
Spring Security checks rule
  |
  v
permitAll
  |
  v
RedirectController
  |
  v
302 Location: longUrl
```

ASCII:

```text
GET /sale2026
     |
     v
+-------------------+
| Security rule     |
| permitAll         |
+-------------------+
     |
     v
+-------------------+
| RedirectController|
+-------------------+
     |
     v
302 redirect
```

Important risk:

```text
Do not accidentally make all GET /** public.
```

Bad:

```java
.requestMatchers(HttpMethod.GET, "/**").permitAll()
```

This may expose:

```text
GET /api/v1/admin/reports
GET /api/v1/users/me
GET /actuator/env
```

Better:

```text
permit only shortCode-shaped redirect paths
permit only safe actuator endpoints
protect all API endpoints by default
```

---

## 14. Protected Create API

Create endpoint should require authentication:

```text
POST /api/v1/urls
```

Why?

```text
1. Prevent anonymous abuse.
2. Track ownership.
3. Apply user quotas later.
4. Support analytics per user.
5. Allow link management later.
```

ASCII:

```text
POST /api/v1/urls
     |
     v
Has Authorization header?
     |
 +---+---+
 |       |
no      yes
 |       |
401      v
     Credentials valid?
          |
      +---+---+
      |       |
     no      yes
      |       |
     401      v
          Controller creates URL
```

Controller can read current user:

```java
@PostMapping("/api/v1/urls")
public ResponseEntity<CreateShortUrlResponse> create(
        @Valid @RequestBody CreateShortUrlRequest request,
        Authentication authentication
) {
    String username = authentication.getName();

    CreateShortUrlResponse response =
            urlService.createShortUrl(request, username);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

Service now can store owner:

```text
created_by = username
```

This enables future features:

```text
my URLs
delete my URL
per-user rate limit
billing plans
admin moderation
```

Production mindset:

```text
Authentication is not only for blocking users.
It creates accountability.
```

---

## 15. Password Encoding Basics

Never store raw passwords.

Wrong:

```text
username = mohamed
password = mypassword123
```

If database leaks, all passwords leak.

Correct:

```text
store password hash
```

Spring Security uses `PasswordEncoder`.

Recommended general encoder:

```java
@Bean
PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
}
```

Or BCrypt directly:

```java
@Bean
PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

ASCII:

```text
Registration:
plain password
      |
      v
PasswordEncoder
      |
      v
hashed password
      |
      v
Database


Login:
plain password from request
      |
      v
PasswordEncoder.matches(raw, hash)
      |
      v
true / false
```

Important:

```text
Hashing is one-way.
You do not decrypt a password.
You compare raw input against stored hash.
```

Example in-memory user for learning:

```java
@Bean
UserDetailsService userDetailsService(PasswordEncoder encoder) {
    UserDetails user = User.builder()
            .username("user")
            .password(encoder.encode("password"))
            .roles("USER")
            .build();

    UserDetails admin = User.builder()
            .username("admin")
            .password(encoder.encode("adminpass"))
            .roles("ADMIN")
            .build();

    return new InMemoryUserDetailsManager(user, admin);
}
```

Never do this in production:

```java
.password("{noop}password")
```

`{noop}` means no password encoding.

Use it only in tiny demos if needed, not real apps.

---

## 16. UserDetailsService Basics

Spring Security needs a way to load users.

That contract is:

```text
UserDetailsService
```

It answers:

```text
Given username, load user details.
```

ASCII:

```text
Basic auth username/password
        |
        v
AuthenticationProvider
        |
        v
UserDetailsService.loadUserByUsername(username)
        |
        v
UserDetails
        |
        v
PasswordEncoder.matches(...)
        |
        v
Authentication success/failure
```

`UserDetails` contains:

```text
username
password hash
authorities
enabled/disabled flags
```

Learning version:

```java
@Bean
UserDetailsService userDetailsService(PasswordEncoder encoder) {
    UserDetails user = User.builder()
            .username("mohamed")
            .password(encoder.encode("secret"))
            .roles("USER")
            .build();

    return new InMemoryUserDetailsManager(user);
}
```

Production version later:

```text
Database users table
UserRepository
CustomUserDetailsService
PasswordEncoder
roles/authorities from DB
```

Simple custom service shape:

```java
@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public DatabaseUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .roles(user.getRole())
                .disabled(!user.isEnabled())
                .build();
    }
}
```

Mental model:

```text
UserDetailsService loads identity data.
PasswordEncoder validates password.
SecurityContext stores authenticated result.
```

---

## 17. HTTP Basic for Learning

HTTP Basic sends credentials in the header.

Example:

```http
Authorization: Basic base64(username:password)
```

Important:

```text
Base64 is not encryption.
HTTPS is mandatory.
```

Use Basic only for:

```text
local learning
internal tools with HTTPS
simple smoke testing
temporary admin endpoints
```

Do not use Basic as final public product auth unless you have a strong reason.

Curl example:

```bash
curl -u user:password \
  -H "Content-Type: application/json" \
  -d '{"longUrl":"https://example.com"}' \
  http://localhost:8080/api/v1/urls
```

ASCII:

```text
Client
  |
  | Authorization: Basic ...
  v
BasicAuthenticationFilter
  |
  v
Decode username/password
  |
  v
UserDetailsService
  |
  v
PasswordEncoder
  |
  +-- fail -> 401
  |
  v
SecurityContext authenticated
  |
  v
Controller
```

What happens without credentials?

```text
401 Unauthorized
WWW-Authenticate header may be returned
```

What happens with wrong role?

```text
403 Forbidden
```

Basic auth is a learning bridge.

Later, replace with:

```text
Authorization: Bearer <JWT>
```

---

## 18. JWT Preview Mental Model

JWT is not implemented deeply in this chapter.

But understand the mental model.

JWT is:

```text
a signed identity document carried by the client
```

It usually contains:

```text
subject/userId
roles/authorities
issued time
expiry time
signature
```

ASCII:

```text
Login
  |
  v
Server verifies username/password
  |
  v
Server signs JWT
  |
  v
Client stores token


API request
  |
  v
Authorization: Bearer <jwt>
  |
  v
JWT filter validates signature + expiry
  |
  v
SecurityContext created
  |
  v
Controller
```

Important:

```text
JWT is stateless.
Server does not need to store session for every user.
```

But JWT has tradeoffs:

```text
harder immediate logout
token theft risk
must manage expiry
refresh token complexity
role changes may not apply until token refresh
```

MiniURLShortener future:

```text
036 Spring Security Basics
037 JWT Authentication
038 Ownership Authorization
039 Admin Moderation Security
```

Memory:

```text
Basic auth proves identity using password each request.
JWT proves identity using signed token each request.
```

---

## 19. CSRF Basics

CSRF means:

```text
Cross-Site Request Forgery
```

It matters mainly for browser cookie-based sessions.

Attack example:

```text
User is logged into your site with session cookie.
User visits evil.com.
evil.com submits POST request to your site.
Browser automatically sends cookies.
Your site thinks request came from user.
```

ASCII:

```text
Victim browser
   |
   | logged into yoursite.com with cookie
   v

evil.com page
   |
   | hidden POST to yoursite.com
   v

Browser sends yoursite.com cookie automatically
   |
   v
Your server may accept forged request
```

CSRF protection uses a token that evil.com cannot know.

For stateless REST APIs using Authorization header:

```text
CSRF is often disabled
```

Because attacker cannot automatically attach your Bearer token or Basic header from another site in the same way cookies are attached.

MiniURLShortener API config:

```java
.csrf(csrf -> csrf.disable())
```

But remember:

```text
Disable CSRF only when your authentication model is suitable for it.
```

Rule:

```text
Cookie session browser app:
    keep CSRF enabled

Stateless REST API with Authorization header:
    commonly disable CSRF
```

Senior answer:

```text
CSRF protects state-changing browser requests that rely on automatically attached cookies.
```

---

## 20. CORS Basics

CORS means:

```text
Cross-Origin Resource Sharing
```

It controls which browser origins can call your API.

Example origins:

```text
https://app.example.com
http://localhost:3000
https://admin.example.com
```

CORS is browser-enforced.

It is not authentication.

ASCII:

```text
Browser frontend
origin: http://localhost:3000
        |
        v
API http://localhost:8080
        |
        v
CORS policy says:
    allowed or blocked by browser
```

Common local development need:

```text
React app on localhost:3000 calls Spring API on localhost:8080
```

Basic CORS config:

```java
@Bean
CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    config.setAllowedOrigins(List.of("http://localhost:3000"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    config.setAllowCredentials(false);

    UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", config);

    return source;
}
```

Enable in security:

```java
.cors(Customizer.withDefaults())
```

Important mistakes:

```text
CORS does not secure your API from server-to-server clients.
CORS does not replace authentication.
CORS only controls browser cross-origin behavior.
```

Memory:

```text
CORS answers: which browser frontends may call me?
Authentication answers: who is calling me?
Authorization answers: what can they do?
```

---

## 21. Security Error Handling

Spring Security errors often happen before controller.

Your `@RestControllerAdvice` may not catch them in the same way.

Security has special handlers:

```text
AuthenticationEntryPoint:
    handles 401 unauthenticated

AccessDeniedHandler:
    handles 403 forbidden
```

ASCII:

```text
Security filter chain
       |
       +-- not logged in
       |       |
       |       v
       | AuthenticationEntryPoint -> 401
       |
       +-- logged in but not allowed
               |
               v
          AccessDeniedHandler -> 403
```

JSON 401 example:

```json
{
  "status": 401,
  "error": "Unauthorized",
  "code": "AUTHENTICATION_REQUIRED",
  "message": "Authentication is required",
  "path": "/api/v1/urls"
}
```

JSON 403 example:

```json
{
  "status": 403,
  "error": "Forbidden",
  "code": "ACCESS_DENIED",
  "message": "You do not have permission to access this resource",
  "path": "/api/v1/admin/blocked-links"
}
```

Basic custom handlers:

```java
@Bean
AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
    return (request, response, authException) -> {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                401,
                "Unauthorized",
                "AUTHENTICATION_REQUIRED",
                "Authentication is required",
                request.getRequestURI(),
                List.of()
        );

        objectMapper.writeValue(response.getOutputStream(), body);
    };
}
```

```java
@Bean
AccessDeniedHandler accessDeniedHandler(ObjectMapper objectMapper) {
    return (request, response, accessDeniedException) -> {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                403,
                "Forbidden",
                "ACCESS_DENIED",
                "You do not have permission to access this resource",
                request.getRequestURI(),
                List.of()
        );

        objectMapper.writeValue(response.getOutputStream(), body);
    };
}
```

Attach:

```java
.exceptionHandling(ex -> ex
        .authenticationEntryPoint(authenticationEntryPoint)
        .accessDeniedHandler(accessDeniedHandler)
)
```

Why this matters:

```text
Without custom handlers, REST clients may receive default security responses that do not match your API error contract.
```

---

## 22. Method-Level Security

Endpoint security protects URLs.

Method-level security protects service methods.

Enable:

```java
@EnableMethodSecurity
```

Use:

```java
@PreAuthorize("hasRole('ADMIN')")
```

Example:

```java
@Service
public class AdminModerationService {

    @PreAuthorize("hasRole('ADMIN')")
    public void blockShortCode(String shortCode) {
        // block link
    }
}
```

ASCII:

```text
HTTP rule allows request
       |
       v
Controller
       |
       v
Service method
       |
       v
@PreAuthorize check
       |
       +-- fail -> 403
       |
       v
business logic
```

Why use both?

```text
URL security:
    protects web entry point

Method security:
    protects business operation even if called from another path later
```

For ownership later:

```java
@PreAuthorize("@urlSecurity.isOwner(#urlId, authentication.name) or hasRole('ADMIN')")
public void deleteUrl(Long urlId) {
    ...
}
```

Senior mindset:

```text
Protect sensitive business operations close to where they happen.
```

Do not overuse method security everywhere at first.

Use it for:

```text
admin operations
ownership-sensitive operations
money/billing operations
dangerous state changes
```

---

## 23. Security Logging Mindset

Security logs should answer:

```text
Who tried?
From where?
What endpoint?
Was it allowed?
Why was it denied?
```

Useful fields:

```text
correlationId
username if authenticated
anonymous if unauthenticated
method
path
status
errorCode
remoteIp
userAgent
```

Do not log:

```text
raw passwords
full Authorization header
JWT token
session cookie
password reset token
secret query params
```

ASCII:

```text
Client request
    |
    v
Security filter
    |
    +-- denied
    |     |
    |     v
    |  security log:
    |  user=anonymous path=/api/v1/urls status=401
    |
    +-- allowed
          |
          v
       application log:
       user=mohamed created shortUrl id=123
```

Production security events worth monitoring:

```text
many 401s from same IP
many 403s from authenticated user
many create attempts
admin endpoint access
actuator access attempt
JWT expired spikes
login failure spikes
```

Memory:

```text
Security logs are attack visibility.
Application logs are behavior visibility.
```

---

## 24. Step-by-Step Dry Runs

### Dry Run 1: Public redirect

Request:

```http
GET /abc123
```

Flow:

```text
1. Request enters Tomcat.
2. Spring Security filter chain runs.
3. Authorization rule matches GET /{shortCode}.
4. Rule is permitAll.
5. Controller executes.
6. Service loads shortCode.
7. Response is 302 redirect.
```

Result:

```text
Controller runs without authentication.
```

---

### Dry Run 2: Anonymous create URL

Request:

```http
POST /api/v1/urls
Content-Type: application/json

{
  "longUrl": "https://example.com"
}
```

Flow:

```text
1. Request enters security filters.
2. No Authorization header exists.
3. Endpoint rule requires authenticated().
4. Spring Security rejects request.
5. AuthenticationEntryPoint returns 401.
6. Controller is never called.
```

Response:

```json
{
  "status": 401,
  "code": "AUTHENTICATION_REQUIRED",
  "message": "Authentication is required"
}
```

---

### Dry Run 3: Authenticated create URL

Request:

```bash
curl -u user:password \
  -H "Content-Type: application/json" \
  -d '{"longUrl":"https://example.com"}' \
  http://localhost:8080/api/v1/urls
```

Flow:

```text
1. BasicAuthenticationFilter reads Authorization header.
2. It decodes username/password.
3. UserDetailsService loads user.
4. PasswordEncoder verifies password.
5. Authentication object is created.
6. SecurityContext stores authenticated user.
7. Authorization rule authenticated() passes.
8. Controller receives Authentication parameter.
9. Service creates URL owned by user.
```

Result:

```text
201 Created
```

---

### Dry Run 4: USER calls admin endpoint

Request:

```http
GET /api/v1/admin/blocked-links
Authorization: Basic user:password
```

Flow:

```text
1. Authentication succeeds.
2. User has ROLE_USER.
3. Endpoint requires ROLE_ADMIN.
4. Authorization fails.
5. AccessDeniedHandler returns 403.
6. Controller is never called.
```

Response:

```json
{
  "status": 403,
  "code": "ACCESS_DENIED",
  "message": "You do not have permission to access this resource"
}
```

---

### Dry Run 5: ADMIN calls admin endpoint

Request:

```http
GET /api/v1/admin/blocked-links
Authorization: Basic admin:adminpass
```

Flow:

```text
1. Authentication succeeds.
2. User has ROLE_ADMIN.
3. Authorization rule hasRole("ADMIN") passes.
4. Controller runs.
5. Admin data returned.
```

Result:

```text
200 OK
```

---

### Dry Run 6: Wrong password

Request:

```http
POST /api/v1/urls
Authorization: Basic user:wrong
```

Flow:

```text
1. BasicAuthenticationFilter extracts username/password.
2. UserDetailsService loads user.
3. PasswordEncoder.matches returns false.
4. Authentication fails.
5. Security returns 401.
6. Controller is never called.
```

Result:

```text
401 Unauthorized
```

---

## 25. Internal Execution Walkthrough

Full request path:

```text
1. HTTP request reaches embedded Tomcat.
2. Servlet filter chain starts.
3. DelegatingFilterProxy delegates to Spring Security.
4. Security filters process request in order.
5. Authentication filter tries to identify user.
6. If credentials are valid, Authentication object is created.
7. SecurityContextHolder stores Authentication for current request.
8. AuthorizationFilter checks endpoint rules.
9. If allowed, request continues to DispatcherServlet.
10. DispatcherServlet calls controller.
11. Controller may access Authentication.
12. After request, SecurityContext is cleared.
```

ASCII:

```text
+--------+
| Client |
+--------+
    |
    v
+--------+
| Tomcat |
+--------+
    |
    v
+-----------------------+
| Servlet Filter Chain  |
+-----------------------+
    |
    v
+-------------------------------+
| Spring Security Filter Chain  |
|                               |
| read credentials              |
| authenticate                  |
| store SecurityContext         |
| authorize endpoint            |
+-------------------------------+
    |
    +-- fail --> 401 / 403
    |
    v
+-------------------+
| DispatcherServlet |
+-------------------+
    |
    v
+------------+
| Controller |
+------------+
    |
    v
+---------+
| Service |
+---------+
```

Most important debugging clue:

```text
If controller is not reached, check SecurityFilterChain first.
```

---

## 26. Testing Strategy

Security must be tested.

Do not only manually test with Postman.

Use `spring-security-test`.

Dependency:

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

MockMvc examples:

```java
@Test
void createUrl_withoutLogin_returns401() throws Exception {
    mockMvc.perform(post("/api/v1/urls")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"longUrl\":\"https://example.com\"}"))
            .andExpect(status().isUnauthorized());
}
```

```java
@Test
@WithMockUser(username = "mohamed", roles = "USER")
void createUrl_withUser_returnsCreated() throws Exception {
    mockMvc.perform(post("/api/v1/urls")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"longUrl\":\"https://example.com\"}"))
            .andExpect(status().isCreated());
}
```

```java
@Test
@WithMockUser(username = "mohamed", roles = "USER")
void adminEndpoint_withUser_returns403() throws Exception {
    mockMvc.perform(get("/api/v1/admin/blocked-links"))
            .andExpect(status().isForbidden());
}
```

```java
@Test
@WithMockUser(username = "admin", roles = "ADMIN")
void adminEndpoint_withAdmin_returns200() throws Exception {
    mockMvc.perform(get("/api/v1/admin/blocked-links"))
            .andExpect(status().isOk());
}
```

Test checklist:

```text
public redirect works anonymously
create API rejects anonymous
create API allows authenticated USER
admin API rejects USER
admin API allows ADMIN
sensitive actuator endpoints are not public
security errors use JSON shape
```

Testing rule:

```text
Security rules are code. Code needs tests.
```

---

## 27. Production Failure Stories

### Failure Story 1: Everything becomes protected

Team adds Spring Security dependency.

Suddenly all endpoints return 401.

Root cause:

```text
Spring Security secure defaults protect all endpoints unless configured.
```

Fix:

```text
Explicitly permit public endpoints like redirect and health.
```

Lesson:

```text
Adding security changes request behavior immediately.
```

---

### Failure Story 2: Redirect endpoint accidentally requires login

Users click short links and see login popup.

Root cause:

```text
GET /{shortCode} not marked permitAll.
```

Fix:

```text
Add specific public redirect matcher.
```

Lesson:

```text
Public product endpoints must be intentionally opened.
```

---

### Failure Story 3: Admin data exposed

Developer uses:

```java
.requestMatchers(HttpMethod.GET, "/**").permitAll()
```

Now admin GET endpoints are public.

Root cause:

```text
Too broad permitAll rule.
```

Fix:

```text
Use narrow matchers and default deny.
```

Lesson:

```text
Broad security rules are dangerous.
```

---

### Failure Story 4: Actuator env leaked

Public internet can access:

```text
/actuator/env
/actuator/configprops
```

Root cause:

```text
Actuator exposure not restricted.
```

Fix:

```text
Expose only health/info publicly.
Restrict or disable sensitive endpoints.
```

Lesson:

```text
Operational endpoints can leak secrets and topology.
```

---

### Failure Story 5: 403 returned for unauthenticated users

Anonymous user receives 403 instead of 401.

Root cause:

```text
Custom security handlers or anonymous auth confusion.
```

Fix:

```text
Use AuthenticationEntryPoint for unauthenticated.
Use AccessDeniedHandler for authenticated-but-forbidden.
```

Lesson:

```text
401 and 403 must be semantically correct for clients.
```

---

### Failure Story 6: Raw Authorization header logged

Logs contain:

```text
Authorization: Bearer eyJ...
```

Root cause:

```text
Request logging middleware logged all headers.
```

Fix:

```text
Mask Authorization, Cookie, Set-Cookie, tokens, passwords.
```

Lesson:

```text
Security logs must not leak security secrets.
```

---

## 28. Debugging Mindset

When security fails, ask in order:

```text
1. Did the request reach the controller?
2. Which URL pattern matched?
3. Was endpoint permitAll, authenticated, or role-protected?
4. Was Authorization header present?
5. Did authentication succeed?
6. What authorities were assigned?
7. Is role prefix correct? ROLE_ADMIN vs ADMIN?
8. Is CSRF blocking POST?
9. Is CORS blocking browser request?
10. Is custom error handler returning correct JSON?
```

Debug map:

```text
401:
    missing credentials
    invalid credentials
    expired/invalid token later

403:
    authenticated but missing role/authority
    method security denied
    CSRF may also cause forbidden in browser-session apps

Controller breakpoint not hit:
    security rejected before MVC

Postman works but browser fails:
    likely CORS or CSRF

GET works but POST fails:
    possible CSRF, auth, or method matcher issue

Admin user still forbidden:
    check roles and ROLE_ prefix
```

Role prefix warning:

```java
.hasRole("ADMIN")
```

means:

```text
requires authority ROLE_ADMIN
```

But:

```java
.hasAuthority("ADMIN")
```

means exactly:

```text
ADMIN
```

Common confusion:

```text
roles("ADMIN") creates ROLE_ADMIN.
hasRole("ADMIN") checks ROLE_ADMIN.
hasAuthority("ADMIN") checks ADMIN.
```

---

## 29. Common Mistakes

### Mistake 1: Thinking security starts in controller

Wrong:

```text
Controller checks login manually.
```

Correct:

```text
SecurityFilterChain checks before controller.
```

### Mistake 2: Opening too much

Wrong:

```java
.requestMatchers("/**").permitAll()
```

Correct:

```java
permit exact public endpoints
protect everything else
```

### Mistake 3: Confusing 401 and 403

Wrong:

```text
Every security failure returns 403.
```

Correct:

```text
No/invalid identity -> 401.
Known identity but not allowed -> 403.
```

### Mistake 4: Disabling CSRF without understanding

Wrong:

```text
Disable CSRF everywhere because StackOverflow said so.
```

Correct:

```text
Disable for stateless Authorization-header REST APIs.
Keep for cookie-session browser apps.
```

### Mistake 5: Treating CORS as security

Wrong:

```text
CORS protects API from all attackers.
```

Correct:

```text
CORS is browser policy. Authentication still required.
```

### Mistake 6: Using `{noop}` passwords

Wrong:

```java
.password("{noop}password")
```

Correct:

```java
.password(passwordEncoder.encode("password"))
```

### Mistake 7: Logging tokens

Wrong:

```text
log Authorization header
```

Correct:

```text
mask tokens and secrets
```

### Mistake 8: Exposing actuator

Wrong:

```text
Expose all actuator endpoints publicly.
```

Correct:

```text
health/info public, sensitive endpoints restricted.
```

### Mistake 9: No security tests

Wrong:

```text
Manual Postman checks only.
```

Correct:

```text
MockMvc security tests for 401/403/200.
```

### Mistake 10: Not understanding filter order

Wrong:

```text
Assume controller advice handles every security error.
```

Correct:

```text
Security errors may happen before MVC and need security handlers.
```

---

## 30. Interview-Ready Explanation

If interviewer asks:

```text
How does Spring Security protect a Spring Boot REST API?
```

Strong answer:

```text
Spring Security protects the application using a servlet filter chain that runs
before the request reaches the DispatcherServlet and controller. The filters
perform authentication, create an Authentication object, store it in the
SecurityContext for the current request, and then apply authorization rules such
as permitAll, authenticated, hasRole, or method-level @PreAuthorize. For a REST
API, I usually make the API stateless, explicitly permit only public endpoints
such as health and public redirect links, protect write endpoints like POST
/api/v1/urls, and restrict admin routes to ADMIN. If the request has no valid
identity, Spring returns 401 through an AuthenticationEntryPoint. If the user is
authenticated but lacks permission, it returns 403 through an AccessDeniedHandler.
I also avoid exposing sensitive actuator endpoints, use PasswordEncoder instead
of raw passwords, disable CSRF only for stateless Authorization-header APIs, and
test the rules with spring-security-test.
```

Why this is strong:

```text
1. Explains filters before controller.
2. Separates authentication and authorization.
3. Mentions SecurityContext.
4. Uses correct 401/403 semantics.
5. Explains stateless REST security.
6. Shows endpoint rule thinking.
7. Mentions actuator safety.
8. Mentions PasswordEncoder.
9. Mentions CSRF nuance.
10. Mentions tests.
```

Senior one-liner:

```text
Spring Security is a pre-controller filter chain that turns identity and permissions into explicit request access decisions.
```

---

## 31. Senior Engineer Checklist

Before moving deeper into JWT, confirm:

```text
[ ] Spring Security dependency added
[ ] SecurityFilterChain configured
[ ] Public redirect endpoint permitAll
[ ] Health/info actuator endpoints permitAll if needed
[ ] Sensitive actuator endpoints not public
[ ] POST /api/v1/urls requires authentication
[ ] Admin endpoints require ADMIN
[ ] Default rule is authenticated or deny-by-default
[ ] Stateless session policy selected for REST API
[ ] CSRF decision is intentional
[ ] CORS configured only for allowed frontend origins
[ ] PasswordEncoder bean exists
[ ] No raw passwords in DB or config
[ ] UserDetailsService exists for learning or DB users
[ ] 401 handler returns API JSON
[ ] 403 handler returns API JSON
[ ] Security tests cover anonymous/user/admin flows
[ ] Authorization header is masked in logs
[ ] Controller can read Authentication when ownership is needed
[ ] Roles and authorities are understood
```

If these are checked, your security foundation is production-shaped.

---

## 32. One-Page Cheat Sheet

```text
Core mental model:
Spring Security is a checkpoint chain before controller.

Authentication:
Who are you?

Authorization:
Are you allowed?

401:
No valid identity.

403:
Identity exists but not enough permission.

Security flow:
Request
 -> Security filters
 -> Authentication
 -> SecurityContext
 -> Authorization
 -> Controller or 401/403

MiniURLShortener rules:
GET /{shortCode} public
GET /actuator/health public
POST /api/v1/urls authenticated
/api/v1/admin/** ADMIN
sensitive actuator endpoints not public

Stateless API:
client sends credentials/token every request
server does not create session

Password:
never store raw password
use PasswordEncoder/BCrypt

CSRF:
important for cookie-session browser apps
commonly disabled for stateless Authorization-header REST APIs

CORS:
browser cross-origin policy
not authentication
not full security

Security errors:
AuthenticationEntryPoint -> 401
AccessDeniedHandler -> 403

Debug:
controller not hit usually means filter chain blocked request
Postman works but browser fails usually means CORS/CSRF
USER blocked from admin means missing ROLE_ADMIN
```

---

## 33. One Picture To Remember

```text
                 SPRING SECURITY BASICS MENTAL MODEL

                         "Checkpoint before controller"

External Client
      |
      v
+------------------------------------------------+
| Spring Security Filter Chain                   |
|                                                |
|  1. CORS / CSRF decision                       |
|  2. Read credentials                           |
|  3. Authenticate user                          |
|  4. Store Authentication in SecurityContext    |
|  5. Authorize endpoint                         |
|                                                |
+------------------------------------------------+
      |
      +-- no identity ------------------> 401 Unauthorized
      |
      +-- identity but not allowed ------> 403 Forbidden
      |
      v
+-----------------------------+
| Controller                  |
| business request handling   |
+-----------------------------+
      |
      v
+-----------------------------+
| Service                     |
| create URL / redirect URL   |
+-----------------------------+
      |
      v
+-----------------------------+
| Database                    |
+-----------------------------+


MiniURLShortener security memory:

GET /abc123
    public redirect

POST /api/v1/urls
    authenticated user

/api/v1/admin/**
    admin only

Everything else:
    protected by default


FINAL MEMORY:

Authentication says who.
Authorization says allowed or not.
SecurityFilterChain decides before controller.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Spring Security is an ordered filter chain that runs before controllers.
2. Authentication identifies the user; authorization checks what the user can do.
3. SecurityContext stores the authenticated identity for the current request.
4. 401 means no valid identity; 403 means valid identity but insufficient permission.
5. Public endpoints must be opened intentionally, and everything else should be protected by default.
```

After this chapter, you have the foundation for:

```text
037_JWT_Authentication.md
038_User_Ownership_Authorization.md
039_Admin_Moderation_Security.md
040_Security_Testing_And_Hardening.md
```
