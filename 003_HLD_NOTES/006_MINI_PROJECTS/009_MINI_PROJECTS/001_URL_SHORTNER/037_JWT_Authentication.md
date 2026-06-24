# 037_JWT_Authentication.md
# MiniURLShortener / MiniSpringSecurity — JWT Authentication

> Core mental model: **JWT authentication is stateless identity proof. The server signs a compact token after login, and every later request carries that token so Spring Security can rebuild the authenticated user without using an HTTP session.**

This chapter follows the same learning style as the reference `009_Error_Handling_Validation.md`: safety boundary thinking, production flow, ASCII diagrams, dry runs, Spring Boot code, debugging mindset, interview answers, and one-picture retention.

---

## Clickable Index

- [1. Why JWT Authentication Exists](#1-why-jwt-authentication-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Session Authentication vs JWT Authentication](#4-session-authentication-vs-jwt-authentication)
- [5. JWT Structure](#5-jwt-structure)
- [6. JWT Request Flow](#6-jwt-request-flow)
- [7. Spring Security JWT Mental Model](#7-spring-security-jwt-mental-model)
- [8. Authentication vs Authorization](#8-authentication-vs-authorization)
- [9. Login Flow](#9-login-flow)
- [10. Protected API Flow](#10-protected-api-flow)
- [11. JWT Claims Design](#11-jwt-claims-design)
- [12. Access Token vs Refresh Token](#12-access-token-vs-refresh-token)
- [13. Project Package Structure](#13-project-package-structure)
- [14. Maven Dependencies](#14-maven-dependencies)
- [15. User Entity](#15-user-entity)
- [16. User Repository](#16-user-repository)
- [17. UserDetailsService](#17-userdetailsservice)
- [18. JWT Service](#18-jwt-service)
- [19. Login Request And Response DTOs](#19-login-request-and-response-dtos)
- [20. Auth Controller](#20-auth-controller)
- [21. JWT Authentication Filter](#21-jwt-authentication-filter)
- [22. Security Configuration](#22-security-configuration)
- [23. Password Encoding](#23-password-encoding)
- [24. Error Handling For JWT](#24-error-handling-for-jwt)
- [25. Short URL Protected Endpoints](#25-short-url-protected-endpoints)
- [26. Step-by-Step Dry Runs](#26-step-by-step-dry-runs)
- [27. Internal Execution Walkthrough](#27-internal-execution-walkthrough)
- [28. ASCII Sequence Diagrams](#28-ascii-sequence-diagrams)
- [29. Production Security Rules](#29-production-security-rules)
- [30. Token Expiry Strategy](#30-token-expiry-strategy)
- [31. Token Revocation Problem](#31-token-revocation-problem)
- [32. Common JWT Attacks](#32-common-jwt-attacks)
- [33. Testing Strategy](#33-testing-strategy)
- [34. Production Failure Stories](#34-production-failure-stories)
- [35. Debugging Mindset](#35-debugging-mindset)
- [36. Common Mistakes](#36-common-mistakes)
- [37. Interview-Ready Explanation](#37-interview-ready-explanation)
- [38. Senior Engineer Checklist](#38-senior-engineer-checklist)
- [39. One-Page Cheat Sheet](#39-one-page-cheat-sheet)
- [40. One Picture To Remember](#40-one-picture-to-remember)

---

## 1. Why JWT Authentication Exists

A backend API needs to know:

```text
Who is calling this endpoint?
Is the caller logged in?
What permissions does the caller have?
Can this caller create, read, update, or delete this resource?
```

For MiniURLShortener, anonymous users may be allowed to redirect:

```http
GET /abc123
```

But authenticated users may be required for:

```http
POST /api/v1/urls
GET  /api/v1/me/urls
DELETE /api/v1/urls/{id}
GET /api/v1/analytics/{shortCode}
```

Without authentication, anyone could:

```text
create unlimited short URLs
view another user's analytics
delete someone else's short URL
abuse the API with scripts
pretend to be another user
```

Traditional server-side sessions work well for browser apps, but high-scale REST APIs often prefer stateless authentication:

```text
No server-side session lookup per request.
Each request carries proof of identity.
Backend verifies signature and expiry.
```

That proof is commonly a JWT.

Production memory:

```text
JWT is not login itself.
JWT is the signed receipt produced after login.
```

---

## 2. The One Core Mental Model

JWT authentication is:

```text
SIGNED IDENTITY PASS
```

Think of it like an airport boarding pass.

```text
Login = passport check at airline counter
JWT = boarding pass issued after verification
Every protected gate = API endpoint
Gate scanner = JWT filter
Signature = anti-forgery seal
Expiry = boarding pass validity time
```

ASCII:

```text
                 LOGIN TIME

User credentials
      |
      v
+-------------------------+
| Authentication Manager  |
| verifies password       |
+-------------------------+
      |
      v
+-------------------------+
| JWT Service             |
| signs identity token    |
+-------------------------+
      |
      v
Client stores token


              EVERY REQUEST AFTER LOGIN

Client request with Authorization header
      |
      v
+-------------------------+
| JWT Authentication      |
| Filter                  |
+-------------------------+
      |
      +-- invalid token -> 401
      |
      v
+-------------------------+
| SecurityContext         |
| authenticated user      |
+-------------------------+
      |
      v
Controller executes
```

One-line memory:

```text
Password proves identity once; JWT proves identity repeatedly.
```

---

## 3. Problem Statement

Build JWT authentication for a Spring Boot URL shortener.

It must support:

```text
1. User login using email and password.
2. Password verification using BCrypt.
3. JWT access token generation.
4. JWT validation on protected endpoints.
5. Stateless Spring Security configuration.
6. SecurityContext population from token.
7. Clean 401 Unauthorized behavior.
8. Role/authority support for future admin APIs.
9. Token expiry.
10. Debuggable failure model.
```

Out of scope for this chapter:

```text
1. Full OAuth2 authorization server.
2. Social login.
3. Multi-factor authentication.
4. Refresh token persistence implementation.
5. Distributed token revocation service.
```

This chapter creates the production-shaped JWT foundation.

---

## 4. Session Authentication vs JWT Authentication

### Session Authentication

Classic login flow:

```text
1. User sends username/password.
2. Server verifies credentials.
3. Server creates session in memory/Redis/database.
4. Browser receives SESSIONID cookie.
5. Every request sends cookie.
6. Server looks up session.
```

ASCII:

```text
Client ---- SESSIONID ----> Server
                              |
                              v
                        Session Store
                              |
                              v
                        User identity
```

Session authentication is stateful because server must remember sessions.

### JWT Authentication

JWT flow:

```text
1. User sends email/password.
2. Server verifies credentials.
3. Server signs JWT containing user identity.
4. Client stores token.
5. Every request sends Authorization: Bearer token.
6. Server verifies token signature and expiry.
```

ASCII:

```text
Client ---- Bearer JWT ----> Server
                              |
                              v
                     Verify signature locally
                              |
                              v
                        User identity
```

Comparison:

```text
+----------------------+----------------------+----------------------+
| Feature              | Session              | JWT                  |
+----------------------+----------------------+----------------------+
| Server-side state    | Yes                  | Usually no           |
| Common for           | Browser apps         | REST APIs/mobile     |
| Logout easy          | Yes                  | Harder               |
| Scale horizontally   | Needs shared session | Easier               |
| Token size           | Small cookie id      | Larger token         |
| Revocation           | Simple               | Needs strategy       |
+----------------------+----------------------+----------------------+
```

Important senior nuance:

```text
JWT is not automatically better than sessions.
JWT is useful when stateless API scaling and cross-service identity propagation matter.
```

---

## 5. JWT Structure

A JWT has three parts:

```text
header.payload.signature
```

Example shape:

```text
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb2hhbWVkQGV4YW1wbGUuY29tIn0.XYZ_SIGNATURE
```

ASCII:

```text
+----------------+   +----------------+   +----------------+
| Header         | . | Payload        | . | Signature      |
+----------------+   +----------------+   +----------------+
| alg, typ       |   | claims         |   | signed proof   |
+----------------+   +----------------+   +----------------+
```

### Header

Usually says algorithm and token type:

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

### Payload

Contains claims:

```json
{
  "sub": "mohamed@example.com",
  "userId": 101,
  "roles": ["ROLE_USER"],
  "iat": 1719000000,
  "exp": 1719003600
}
```

### Signature

For HS256:

```text
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

The signature proves:

```text
1. Token was produced by someone with the secret key.
2. Header and payload were not modified.
```

Very important:

```text
JWT payload is encoded, not encrypted.
Do not put passwords, secrets, OTPs, or sensitive personal data inside JWT payload.
```

---

## 6. JWT Request Flow

JWT authentication has two separate flows.

### Flow A: Login

```text
email + password -> verify -> generate JWT -> return token
```

ASCII:

```text
Client
  |
  | POST /api/v1/auth/login
  | { email, password }
  v
AuthController
  |
  v
AuthenticationManager
  |
  v
UserDetailsService + PasswordEncoder
  |
  +-- bad credentials -> 401
  |
  v
JwtService
  |
  v
200 OK { accessToken }
```

### Flow B: Protected API Request

```text
Authorization: Bearer <token> -> validate -> set SecurityContext -> controller
```

ASCII:

```text
Client
  |
  | GET /api/v1/me/urls
  | Authorization: Bearer JWT
  v
JwtAuthenticationFilter
  |
  +-- missing token -> continue anonymous / maybe 401 later
  +-- invalid token -> 401
  +-- valid token   -> SecurityContext authenticated
  |
  v
Controller
```

Mental separation:

```text
Login creates token.
Filter consumes token.
```

---

## 7. Spring Security JWT Mental Model

Spring Security works like a chain of guards.

```text
HTTP request enters application.
Before it reaches controller, it passes security filters.
Each filter can inspect, reject, or enrich the request.
```

JWT authentication is usually implemented as a custom filter:

```text
OncePerRequestFilter
```

ASCII:

```text
HTTP Request
   |
   v
+-------------------------------+
| Security Filter Chain         |
+-------------------------------+
   |
   +--> CORS Filter
   |
   +--> Logout Filter
   |
   +--> JWT Authentication Filter
   |        |
   |        +-- read Authorization header
   |        +-- extract token
   |        +-- validate signature/expiry
   |        +-- load user
   |        +-- set SecurityContext
   |
   +--> Authorization Filter
   |        |
   |        +-- is endpoint permitted?
   |        +-- is user authenticated?
   |
   v
Controller
```

The most important object:

```text
SecurityContextHolder
```

It stores authentication for the current request thread.

```text
SecurityContextHolder
    -> SecurityContext
        -> Authentication
            -> Principal/UserDetails
            -> Authorities/Roles
```

One-line memory:

```text
JWT filter converts a token string into a Spring Security Authentication object.
```

---

## 8. Authentication vs Authorization

Authentication asks:

```text
Who are you?
```

Authorization asks:

```text
What are you allowed to do?
```

Example:

```text
Authentication:
    Mohamed is logged in.

Authorization:
    Mohamed can create his own short URLs.
    Mohamed cannot delete another user's short URL.
    Admin can block malicious short URLs.
```

ASCII:

```text
Request with JWT
      |
      v
Authentication
      |
      +-- invalid identity -> 401 Unauthorized
      |
      v
Authorization
      |
      +-- no permission -> 403 Forbidden
      |
      v
Endpoint executes
```

Status code distinction:

```text
401 Unauthorized = not authenticated or bad token
403 Forbidden    = authenticated but not allowed
```

Interview memory:

```text
JWT mainly solves authentication. Authorization still needs roles, ownership checks, and method-level rules.
```

---

## 9. Login Flow

Login endpoint:

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "mohamed@example.com",
  "password": "secret123"
}
```

Happy response:

```json
{
  "tokenType": "Bearer",
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresInSeconds": 900
}
```

Bad password:

```http
401 Unauthorized
```

ASCII:

```text
Login Request
    |
    v
AuthController
    |
    v
UsernamePasswordAuthenticationToken(email, password)
    |
    v
AuthenticationManager
    |
    v
DaoAuthenticationProvider
    |
    +-- load user by email
    +-- compare raw password with BCrypt hash
    |
    +-- fail -> BadCredentialsException -> 401
    |
    v
JwtService.generateToken(user)
    |
    v
Return token
```

Key point:

```text
During login, password is used.
After login, password is not sent again.
```

---

## 10. Protected API Flow

Example request:

```http
GET /api/v1/me/urls
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

Flow:

```text
1. Request enters Spring Security filter chain.
2. JWT filter checks Authorization header.
3. Header must start with Bearer.
4. Filter extracts token.
5. JWT service extracts subject/email.
6. UserDetailsService loads user.
7. JWT service validates signature and expiry.
8. Filter creates UsernamePasswordAuthenticationToken.
9. Filter stores it in SecurityContextHolder.
10. Authorization filter sees authenticated user.
11. Controller executes.
```

ASCII:

```text
Bearer token
    |
    v
Extract email from token
    |
    v
Load user from DB
    |
    v
Validate token against user
    |
    v
Create Authentication object
    |
    v
SecurityContextHolder.setAuthentication(...)
    |
    v
Controller can use @AuthenticationPrincipal
```

Controller can now access user:

```java
@GetMapping("/me/urls")
public List<MyUrlResponse> myUrls(@AuthenticationPrincipal UserDetails user) {
    return urlService.findByOwnerEmail(user.getUsername());
}
```

---

## 11. JWT Claims Design

JWT claims should be small and stable.

Recommended access-token claims:

```text
sub       = email or user id
userId    = internal id
roles     = authorities
iat       = issued at
exp       = expiry time
iss       = issuer, optional
jti       = token id, optional
```

Do not put:

```text
password
password hash
OTP
credit card
full address
large profile object
mutable permissions if they change frequently
sensitive secrets
```

Good payload:

```json
{
  "sub": "mohamed@example.com",
  "userId": 101,
  "roles": ["ROLE_USER"],
  "iat": 1719000000,
  "exp": 1719000900
}
```

Bad payload:

```json
{
  "sub": "mohamed@example.com",
  "password": "plain-text",
  "resetToken": "secret",
  "fullProfile": "huge mutable data"
}
```

Production rule:

```text
JWT payload should contain identity hints, not sensitive private state.
```

---

## 12. Access Token vs Refresh Token

Access token:

```text
short-lived token used to call APIs
```

Refresh token:

```text
longer-lived token used to obtain a new access token
```

ASCII:

```text
Login
  |
  +--> access token  -> short expiry -> API calls
  |
  +--> refresh token -> longer expiry -> get new access token
```

Why not make access token valid for 30 days?

```text
If stolen, attacker has 30 days of API access.
```

Better:

```text
Access token: 5-15 minutes
Refresh token: days/weeks, stored more carefully, revocable
```

For this chapter, implement access token only.

Future production:

```text
1. Store refresh token hash in DB/Redis.
2. Rotate refresh token on use.
3. Revoke refresh tokens on logout.
4. Detect reuse.
```

---

## 13. Project Package Structure

Recommended structure:

```text
src/main/java/com/miniurl/shortener
|
+-- auth
|   +-- controller
|   |   +-- AuthController.java
|   |
|   +-- dto
|   |   +-- LoginRequest.java
|   |   +-- LoginResponse.java
|   |
|   +-- security
|   |   +-- JwtAuthenticationFilter.java
|   |   +-- JwtService.java
|   |   +-- SecurityConfig.java
|   |   +-- CustomUserDetailsService.java
|   |
|   +-- user
|       +-- AppUser.java
|       +-- AppUserRepository.java
|
+-- common
|   +-- error
|       +-- ApiErrorResponse.java
|       +-- GlobalExceptionHandler.java
|
+-- url
    +-- controller
    +-- service
    +-- repository
```

Why this structure?

```text
auth/security contains authentication infrastructure
url package contains business logic
common/error contains shared error contract
```

Rule:

```text
Do not mix JWT parsing logic inside URL service.
```

---

## 14. Maven Dependencies

Spring Security:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Validation:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

JJWT:

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
```

Configuration:

```yaml
app:
  security:
    jwt:
      secret: "change-this-to-a-long-base64-secret-in-real-prod"
      access-token-expiration-seconds: 900
```

Production rule:

```text
Never hardcode real JWT secrets in GitHub.
Use environment variables or a secret manager.
```

---

## 15. User Entity

```java
package com.miniurl.shortener.auth.user;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "app_users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 50)
    private String role;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
```

SQL table:

```sql
CREATE TABLE app_users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

Important:

```text
Store password hash, never raw password.
```

---

## 16. User Repository

```java
package com.miniurl.shortener.auth.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmail(String email);
}
```

Why email?

```text
For login, email is the username.
Spring Security UserDetailsService can load by email.
```

Index:

```sql
CREATE UNIQUE INDEX uk_app_users_email ON app_users(email);
```

Production note:

```text
Normalize email casing before storing and searching.
Example: lower-case email.
```

---

## 17. UserDetailsService

Spring Security expects user information through `UserDetailsService`.

```java
package com.miniurl.shortener.auth.security;

import com.miniurl.shortener.auth.user.AppUser;
import com.miniurl.shortener.auth.user.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;

    public CustomUserDetailsService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(user.getRole())
                .disabled(!user.isEnabled())
                .build();
    }
}
```

Mental model:

```text
AppUser is your database entity.
UserDetails is Spring Security's authentication shape.
```

ASCII:

```text
Database row
    |
    v
AppUser entity
    |
    v
CustomUserDetailsService
    |
    v
Spring Security UserDetails
```

---

## 18. JWT Service

```java
package com.miniurl.shortener.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long accessTokenExpirationSeconds;

    public JwtService(
            @Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.access-token-expiration-seconds}") long accessTokenExpirationSeconds
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationSeconds = accessTokenExpirationSeconds;
    }

    public String generateAccessToken(UserDetails userDetails) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(accessTokenExpirationSeconds);

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claims(Map.of("roles", roles))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
```

Important secret length warning:

```text
HS256 needs a sufficiently long secret key.
Use a long random secret, preferably base64-managed through environment variables.
```

Simple dev secret example length:

```text
my-super-long-development-secret-key-at-least-32-bytes
```

Do not use:

```text
secret
password
123456
```

---

## 19. Login Request And Response DTOs

```java
package com.miniurl.shortener.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @Email(message = "email must be valid")
    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "password is required")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
```

```java
package com.miniurl.shortener.auth.dto;

public class LoginResponse {

    private String tokenType;
    private String accessToken;
    private long expiresInSeconds;

    public LoginResponse(String tokenType, String accessToken, long expiresInSeconds) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }
}
```

Why DTO validation matters:

```text
Bad login request should fail before authentication logic.
Missing email/password should return 400 validation error.
Wrong credentials should return 401.
```

---

## 20. Auth Controller

```java
package com.miniurl.shortener.auth.controller;

import com.miniurl.shortener.auth.dto.LoginRequest;
import com.miniurl.shortener.auth.dto.LoginResponse;
import com.miniurl.shortener.auth.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final long expiresInSeconds;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            @Value("${app.security.jwt.access-token-expiration-seconds}") long expiresInSeconds
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.expiresInSeconds = expiresInSeconds;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateAccessToken(userDetails);

        return ResponseEntity.ok(
                new LoginResponse("Bearer", token, expiresInSeconds)
        );
    }
}
```

Mental model:

```text
Controller does not manually compare passwords.
AuthenticationManager delegates that job to Spring Security provider.
```

---

## 21. JWT Authentication Filter

```java
package com.miniurl.shortener.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtService.extractUsername(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
                    {
                      "status": 401,
                      "error": "Unauthorized",
                      "code": "INVALID_TOKEN",
                      "message": "Invalid or expired token"
                    }
                    """);
        }
    }
}
```

Important:

```text
If token is missing, filter does not immediately reject.
It continues. Later authorization rules decide whether endpoint requires authentication.
```

Why?

```text
Some endpoints are public:
GET /{shortCode}
POST /api/v1/auth/login
```

---

## 22. Security Configuration

```java
package com.miniurl.shortener.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService userDetailsService
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/{shortCode}").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
```

Important settings:

```text
csrf.disable()                       -> common for stateless APIs not using cookies
SessionCreationPolicy.STATELESS      -> do not create HTTP sessions
addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                                      -> parse JWT before username/password filter
```

Security chain memory:

```text
JWT filter authenticates.
Authorization rules decide access.
```

---

## 23. Password Encoding

Never store raw passwords.

Wrong:

```sql
email='mohamed@example.com', password='secret123'
```

Correct:

```sql
email='mohamed@example.com', password_hash='$2a$10$....'
```

BCrypt properties:

```text
1. One-way hashing.
2. Salt included.
3. Slow by design.
4. Configurable cost.
```

ASCII:

```text
Raw password
    |
    v
BCrypt hash
    |
    v
Store hash only

Login later:
raw input password + stored hash -> BCrypt.matches()
```

Seed test user example:

```java
String hash = passwordEncoder.encode("secret123");
```

Manual SQL seed is possible only after generating hash:

```sql
INSERT INTO app_users(email, password_hash, role, enabled)
VALUES (
  'mohamed@example.com',
  '$2a$10$REPLACE_WITH_REAL_BCRYPT_HASH',
  'ROLE_USER',
  true
);
```

---

## 24. Error Handling For JWT

JWT-related errors:

```text
missing token on protected endpoint -> 401 UNAUTHORIZED
invalid signature                  -> 401 INVALID_TOKEN
expired token                      -> 401 TOKEN_EXPIRED
malformed token                    -> 401 INVALID_TOKEN
authenticated but forbidden         -> 403 FORBIDDEN
```

Better error response shape should match your common API error contract.

Example:

```json
{
  "timestamp": "2026-06-24T10:00:00Z",
  "status": 401,
  "error": "Unauthorized",
  "code": "INVALID_TOKEN",
  "message": "Invalid or expired token",
  "path": "/api/v1/me/urls"
}
```

ASCII:

```text
Protected request
    |
    +-- no Authorization header -> anonymous
    |                              |
    |                              v
    |                        endpoint requires auth
    |                              |
    |                              v
    |                            401
    |
    +-- bad Bearer token --------> 401
    |
    +-- valid token -------------> controller
```

Production improvement:

```text
Use AuthenticationEntryPoint for consistent 401 response.
Use AccessDeniedHandler for consistent 403 response.
Avoid writing JSON directly in filter long term.
```

---

## 25. Short URL Protected Endpoints

Public endpoint:

```text
GET /{shortCode}
```

Protected endpoints:

```text
POST /api/v1/urls
GET /api/v1/me/urls
DELETE /api/v1/urls/{id}
GET /api/v1/analytics/{shortCode}
```

Ownership rule:

```text
Authenticated user can manage only their own short URLs.
```

ASCII:

```text
JWT says user = 101
      |
      v
DELETE /api/v1/urls/555
      |
      v
DB row owner_user_id = 202
      |
      v
403 Forbidden
```

Important:

```text
Role-based authorization is not enough for ownership.
ROLE_USER can be valid but still not own the resource.
```

Service-level check:

```java
public void deleteUrl(Long urlId, String currentUserEmail) {
    ShortUrl url = repository.findById(urlId)
            .orElseThrow(() -> new ShortUrlNotFoundException(urlId));

    if (!url.getOwnerEmail().equals(currentUserEmail)) {
        throw new AccessDeniedException("You do not own this short URL");
    }

    repository.delete(url);
}
```

Senior memory:

```text
JWT proves caller identity; business code still proves ownership.
```

---

## 26. Step-by-Step Dry Runs

### Dry Run 1: Successful Login

Request:

```json
{
  "email": "mohamed@example.com",
  "password": "secret123"
}
```

Flow:

```text
1. AuthController receives request.
2. @Valid validates email and password are present.
3. AuthenticationManager receives UsernamePasswordAuthenticationToken.
4. DaoAuthenticationProvider calls CustomUserDetailsService.
5. User is loaded from DB by email.
6. BCrypt compares raw password with stored password hash.
7. Authentication succeeds.
8. JwtService creates signed token.
9. Controller returns 200 with Bearer token.
```

Result:

```json
{
  "tokenType": "Bearer",
  "accessToken": "eyJhbGciOi...",
  "expiresInSeconds": 900
}
```

---

### Dry Run 2: Wrong Password

Request:

```json
{
  "email": "mohamed@example.com",
  "password": "wrong"
}
```

Flow:

```text
1. DTO validation passes.
2. User is loaded from DB.
3. BCrypt.matches(raw, hash) returns false.
4. Spring throws BadCredentialsException.
5. Security error handler returns 401.
```

Expected response:

```text
401 Unauthorized
```

Do not return:

```text
User exists but password wrong
```

Why?

```text
It helps attackers enumerate accounts.
```

---

### Dry Run 3: Missing Token On Protected Endpoint

Request:

```http
GET /api/v1/me/urls
```

Flow:

```text
1. JWT filter sees no Authorization header.
2. Filter continues without authentication.
3. Authorization rule says endpoint requires authenticated user.
4. Spring returns 401.
```

Expected:

```text
401 Unauthorized
```

---

### Dry Run 4: Valid Token On Protected Endpoint

Request:

```http
GET /api/v1/me/urls
Authorization: Bearer valid.jwt.token
```

Flow:

```text
1. JWT filter extracts token.
2. JwtService validates signature.
3. JwtService checks expiry.
4. Email is extracted.
5. User is loaded.
6. Authentication object is built.
7. SecurityContext is populated.
8. Authorization passes.
9. Controller returns current user's URLs.
```

---

### Dry Run 5: Expired Token

Request:

```http
GET /api/v1/me/urls
Authorization: Bearer expired.jwt.token
```

Flow:

```text
1. JWT filter extracts token.
2. Parser validates signature.
3. Parser sees exp is before current time.
4. ExpiredJwtException occurs.
5. Filter clears SecurityContext.
6. Response is 401.
```

Expected:

```json
{
  "status": 401,
  "code": "INVALID_TOKEN",
  "message": "Invalid or expired token"
}
```

---

### Dry Run 6: User Tries To Delete Another User's URL

Token:

```text
sub = user1@example.com
```

DB row:

```text
url_id = 55
owner = user2@example.com
```

Flow:

```text
1. JWT authentication succeeds.
2. User is authenticated as user1@example.com.
3. DELETE endpoint calls service.
4. Service loads URL row.
5. Owner mismatch found.
6. Service throws AccessDeniedException.
7. Response is 403 Forbidden.
```

Key lesson:

```text
Authentication success does not mean business authorization success.
```

---

## 27. Internal Execution Walkthrough

Protected request internals:

```text
1. Tomcat receives HTTP request.
2. Request enters Spring Security filter chain.
3. JwtAuthenticationFilter runs once for the request.
4. Filter reads Authorization header.
5. Filter extracts Bearer token.
6. JwtService parses signed claims.
7. Signature is verified using secret key.
8. Expiration is checked.
9. Subject/email is extracted.
10. UserDetailsService loads the user.
11. Authentication token is created.
12. SecurityContextHolder stores authentication.
13. AuthorizationFilter checks endpoint rule.
14. DispatcherServlet routes to controller.
15. Controller accesses authenticated principal.
```

ASCII:

```text
+--------+      +-----------------+      +--------------------+
| Client | ---> | Security Chain  | ---> | DispatcherServlet  |
+--------+      +-----------------+      +--------------------+
                       |
                       v
              JwtAuthenticationFilter
                       |
                       v
              SecurityContextHolder
                       |
                       v
              AuthorizationFilter
                       |
                       v
                  Controller
```

Thread-local concept:

```text
SecurityContextHolder is usually backed by ThreadLocal.
Authentication is available during the current request thread.
```

Production caution:

```text
If you start async work manually, SecurityContext may not automatically propagate unless configured.
```

---

## 28. ASCII Sequence Diagrams

### Login Sequence

```text
Client        AuthController       AuthManager       UserDetailsService       DB       JwtService
  |                 |                   |                   |                 |            |
  | POST /login     |                   |                   |                 |            |
  |---------------->|                   |                   |                 |            |
  |                 | authenticate()    |                   |                 |            |
  |                 |------------------>|                   |                 |            |
  |                 |                   | loadUser(email)   |                 |            |
  |                 |                   |------------------>|                 |            |
  |                 |                   |                   | SELECT user     |            |
  |                 |                   |                   |---------------->|            |
  |                 |                   |                   | user row        |            |
  |                 |                   |<------------------|<----------------|            |
  |                 | auth success      |                   |                 |            |
  |                 |<------------------|                   |                 |            |
  |                 | generate token    |                   |                 |            |
  |                 |------------------------------------------------------>|            |
  |                 | token             |                   |                 |            |
  |                 |<------------------------------------------------------|            |
  | 200 token       |                   |                   |                 |            |
  |<----------------|                   |                   |                 |            |
```

### Protected API Sequence

```text
Client        JWT Filter        JwtService       UserDetailsService       SecurityContext       Controller
  |               |                 |                    |                    |                 |
  | GET /me/urls  |                 |                    |                    |                 |
  | Bearer token  |                 |                    |                    |                 |
  |-------------->|                 |                    |                    |                 |
  |               | parse token     |                    |                    |                 |
  |               |---------------->|                    |                    |                 |
  |               | email           |                    |                    |                 |
  |               |<----------------|                    |                    |                 |
  |               | load user       |                    |                    |                 |
  |               |------------------------------------>|                    |                 |
  |               | user details    |                    |                    |                 |
  |               |<------------------------------------|                    |                 |
  |               | validate token  |                    |                    |                 |
  |               |---------------->|                    |                    |                 |
  |               | valid           |                    |                    |                 |
  |               |<----------------|                    |                    |                 |
  |               | set auth        |                    |                    |                 |
  |               |------------------------------------------------------>|                 |
  |               | continue chain  |                    |                    |                 |
  |               |-------------------------------------------------------------------->|
  | 200 response  |                 |                    |                    |                 |
  |<------------------------------------------------------------------------------------|
```

---

## 29. Production Security Rules

JWT production rules:

```text
1. Use HTTPS only.
2. Keep access tokens short-lived.
3. Do not store secrets in JWT payload.
4. Use strong signing secrets or asymmetric keys.
5. Rotate keys carefully.
6. Validate issuer/audience if used.
7. Handle expiry consistently.
8. Never log full tokens.
9. Do not put JWT in URL query parameters.
10. Use refresh-token rotation for long sessions.
```

Token storage options:

```text
Browser localStorage:
    easy but vulnerable to XSS token theft

HttpOnly secure cookie:
    safer from JavaScript theft but needs CSRF design

Mobile secure storage:
    common for mobile apps
```

For pure API learning project:

```text
Authorization: Bearer token is acceptable.
```

For production browser apps:

```text
Think deeply about XSS, CSRF, SameSite, and HttpOnly cookies.
```

---

## 30. Token Expiry Strategy

Why expire tokens?

```text
A stolen token remains usable until it expires.
Short expiry reduces damage window.
```

Common values:

```text
Access token: 5 to 15 minutes
Refresh token: 7 to 30 days depending on risk
```

ASCII:

```text
Time ------------------------------------------------->

Access token issued
|================= valid =================| expired
0 min                                  15 min
```

User experience problem:

```text
If access token expires every 15 minutes, user should not manually log in again every time.
```

Solution:

```text
Use refresh token endpoint.
```

Future flow:

```text
Access token expires
      |
      v
Client calls /auth/refresh using refresh token
      |
      v
Server validates refresh token
      |
      v
New access token returned
```

---

## 31. Token Revocation Problem

JWT is stateless by default.

That means:

```text
Once issued, token remains valid until expiry unless server keeps extra state.
```

Logout problem:

```text
User clicks logout.
Client deletes token.
But if attacker copied token earlier, server may still accept it until expiry.
```

Revocation strategies:

```text
1. Short access token expiry.
2. Store refresh tokens and revoke those.
3. Maintain blacklist for access token jti until expiry.
4. Store token version on user and include version in JWT.
5. Rotate signing keys in emergency.
```

ASCII:

```text
Stateless JWT validation:
    token signature valid + not expired = accepted

Revocable JWT validation:
    token signature valid + not expired + not blacklisted + user version matches = accepted
```

Tradeoff:

```text
More revocation control usually means more server-side state.
```

Senior memory:

```text
JWT removes session lookup, but revocation reintroduces state when you need stronger control.
```

---

## 32. Common JWT Attacks

### Attack 1: Token Theft

Cause:

```text
XSS reads token from localStorage.
```

Impact:

```text
Attacker sends Bearer token and becomes user until token expires.
```

Protection:

```text
short expiry
XSS prevention
HttpOnly cookies where appropriate
never log tokens
```

### Attack 2: Weak Secret

Cause:

```text
secret = "secret"
```

Impact:

```text
Attacker brute-forces secret and signs fake tokens.
```

Protection:

```text
long random secret
secret manager
environment variables
rotation plan
```

### Attack 3: Sensitive Payload

Cause:

```text
JWT payload contains private data.
```

Impact:

```text
Anyone with token can base64-decode payload.
```

Protection:

```text
payload contains minimal identity claims only
```

### Attack 4: Algorithm Confusion

Cause:

```text
Improper parser accepts unexpected algorithm.
```

Protection:

```text
use mature library
verify with expected key and algorithm family
avoid manual JWT parsing
```

### Attack 5: Token In URL

Cause:

```text
GET /api/v1/me/urls?token=abc
```

Impact:

```text
token appears in browser history, logs, proxies, analytics
```

Protection:

```text
use Authorization header
```

---

## 33. Testing Strategy

### Unit Tests

JwtService tests:

```text
generate token -> subject can be extracted
generate token -> roles claim exists
expired token -> invalid
wrong secret -> invalid
malformed token -> exception
```

### Integration Tests

Login tests:

```text
valid credentials -> 200 + accessToken
wrong password -> 401
unknown email -> 401
missing email -> 400 VALIDATION_FAILED
missing password -> 400 VALIDATION_FAILED
```

Protected endpoint tests:

```text
no token -> 401
invalid token -> 401
expired token -> 401
valid token -> 200
valid user token but admin endpoint -> 403
```

MockMvc example:

```java
mockMvc.perform(get("/api/v1/me/urls"))
        .andExpect(status().isUnauthorized());
```

With token:

```java
mockMvc.perform(get("/api/v1/me/urls")
        .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk());
```

Testing rule:

```text
Do not only test login.
Test the filter path on real protected APIs.
```

---

## 34. Production Failure Stories

### Failure Story 1: API Accidentally Uses Sessions

Symptom:

```text
One pod works, another pod rejects requests.
Sticky sessions required unexpectedly.
```

Root cause:

```text
SessionCreationPolicy.STATELESS missing.
```

Fix:

```text
Set session management to STATELESS.
Use JWT on every request.
```

Lesson:

```text
JWT architecture must be truly stateless, not accidentally session-backed.
```

---

### Failure Story 2: Token Never Expires

Symptom:

```text
Old leaked token still works months later.
```

Root cause:

```text
No exp claim or huge expiry.
```

Fix:

```text
Short access-token expiry.
Refresh-token strategy.
```

Lesson:

```text
A token without expiry is almost a password.
```

---

### Failure Story 3: Secret Committed To GitHub

Symptom:

```text
Attackers can generate valid admin tokens.
```

Root cause:

```text
Production JWT secret committed to repository.
```

Fix:

```text
Rotate secret immediately.
Invalidate old tokens.
Move secret to secret manager or environment variable.
```

Lesson:

```text
JWT security depends heavily on key secrecy.
```

---

### Failure Story 4: 403 Returned For Missing Token

Symptom:

```text
Client receives 403 when not logged in.
Frontend shows wrong message.
```

Root cause:

```text
AuthenticationEntryPoint and AccessDeniedHandler not configured clearly.
```

Fix:

```text
401 for unauthenticated.
403 for authenticated but not authorized.
```

Lesson:

```text
401 and 403 are different debugging signals.
```

---

### Failure Story 5: User Deletes Another User's URL

Symptom:

```text
Any logged-in user can delete URL by id.
```

Root cause:

```text
Endpoint only checked authenticated(), not ownership.
```

Fix:

```text
Service checks owner_user_id against authenticated user.
```

Lesson:

```text
Authentication is not enough. Business authorization matters.
```

---

## 35. Debugging Mindset

When JWT auth fails, ask:

```text
Is Authorization header present?
Does it start with Bearer plus space?
Is token malformed?
Is token signed with the correct secret?
Is token expired?
Does subject/email exist in DB?
Is user disabled?
Was SecurityContext populated?
Does endpoint require authentication?
Does user have required role?
Does business ownership check pass?
```

Debug map:

```text
400 on login:
    DTO validation failed

401 on login:
    bad credentials or disabled user

401 on protected endpoint:
    missing, invalid, expired token

403 on protected endpoint:
    authenticated but lacks role/ownership

500:
    bug in filter/service/configuration
```

Useful log fields:

```text
path
method
hasAuthorizationHeader
tokenValidationResult
subject
userFound
roles
exceptionClass
correlationId
```

Do not log:

```text
full JWT
raw password
password hash
```

Golden rule:

```text
Debug JWT by following the token from header -> parser -> user load -> SecurityContext -> authorization rule.
```

---

## 36. Common Mistakes

### Mistake 1: Thinking JWT Payload Is Secret

Wrong:

```text
Put sensitive data inside token because it looks unreadable.
```

Correct:

```text
JWT payload is base64url encoded, not encrypted.
```

### Mistake 2: Missing Bearer Prefix

Wrong:

```http
Authorization: eyJhbGciOi...
```

Correct:

```http
Authorization: Bearer eyJhbGciOi...
```

### Mistake 3: Not Setting SecurityContext

Wrong:

```text
Token validated, but controller still sees anonymous user.
```

Cause:

```text
Filter did not call SecurityContextHolder.getContext().setAuthentication(...)
```

### Mistake 4: No Stateless Configuration

Wrong:

```text
JWT used but sessions still created.
```

Correct:

```java
.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```

### Mistake 5: Very Long Access Token Expiry

Wrong:

```text
Access token valid for 1 year.
```

Correct:

```text
Short access token + refresh token.
```

### Mistake 6: Hardcoded Weak Secret

Wrong:

```yaml
secret: secret
```

Correct:

```text
long random secret from environment/secret manager
```

### Mistake 7: Role Check But No Ownership Check

Wrong:

```text
ROLE_USER can delete any URL.
```

Correct:

```text
ROLE_USER can delete only own URL.
```

### Mistake 8: Returning 500 For Expired Token

Wrong:

```text
ExpiredJwtException bubbles to generic handler.
```

Correct:

```text
Expired/invalid token -> 401.
```

---

## 37. Interview-Ready Explanation

If interviewer asks:

```text
How does JWT authentication work in your Spring Boot API?
```

Strong answer:

```text
In my Spring Boot API, login is handled through AuthenticationManager. The user sends email and password, Spring Security loads the user through UserDetailsService, and BCrypt verifies the password against the stored hash. If authentication succeeds, a JwtService creates a signed short-lived access token containing minimal claims like subject, roles, issued-at, and expiry.

For later requests, the client sends Authorization: Bearer <token>. A custom OncePerRequestFilter runs before the controller, extracts the token, verifies the signature and expiry, loads the user, creates a UsernamePasswordAuthenticationToken, and stores it in SecurityContextHolder. Then Spring Security authorization rules decide whether the endpoint is allowed. Missing or invalid tokens return 401, while authenticated users without permission get 403.

JWT makes the API stateless because the server does not need to look up an HTTP session per request. But I still keep tokens short-lived, avoid sensitive payload data, store secrets outside Git, use HTTPS, and handle ownership checks in business logic because authentication alone does not prove resource ownership.
```

Why this answer is strong:

```text
1. Explains login and request flows separately.
2. Mentions AuthenticationManager and UserDetailsService.
3. Mentions BCrypt.
4. Explains JWT filter and SecurityContext.
5. Distinguishes 401 and 403.
6. Explains statelessness.
7. Mentions production security tradeoffs.
8. Mentions ownership authorization.
```

Senior one-liner:

```text
JWT authentication converts a signed Bearer token into a Spring Security Authentication object for each request, without relying on server-side sessions.
```

---

## 38. Senior Engineer Checklist

Before calling JWT authentication production-shaped, confirm:

```text
[ ] Passwords stored with BCrypt hash only
[ ] AuthenticationManager handles login
[ ] UserDetailsService loads users from DB
[ ] JWT contains minimal claims
[ ] JWT has expiry
[ ] JWT secret is strong
[ ] JWT secret is not committed to Git
[ ] SecurityFilterChain is stateless
[ ] JWT filter runs before UsernamePasswordAuthenticationFilter
[ ] Missing token on protected API returns 401
[ ] Invalid token returns 401
[ ] Expired token returns 401
[ ] Authenticated but unauthorized returns 403
[ ] Public endpoints are explicitly permitAll
[ ] Admin endpoints require ROLE_ADMIN
[ ] User endpoints require authentication
[ ] Ownership checks exist in service layer
[ ] Full JWT is not logged
[ ] Raw password is not logged
[ ] HTTPS is required in production
[ ] Tests cover login, protected API, invalid token, expired token, role failure
```

---

## 39. One-Page Cheat Sheet

```text
Core mental model:
JWT = signed identity pass.
Password proves identity once.
JWT proves identity repeatedly.

JWT parts:
header.payload.signature

Header:
algorithm and token type

Payload:
claims like sub, roles, iat, exp

Signature:
prevents tampering

Login flow:
email/password
-> AuthenticationManager
-> UserDetailsService
-> BCrypt password check
-> JwtService generates token
-> client receives Bearer token

Protected request flow:
Authorization: Bearer token
-> JWT filter
-> verify signature
-> verify expiry
-> load user
-> set SecurityContext
-> authorization rules
-> controller

401 vs 403:
401 = not authenticated / bad token
403 = authenticated but not allowed

Access token:
short-lived API token

Refresh token:
longer-lived token used to get new access token

Security rules:
Use HTTPS.
Use short expiry.
Never log JWT.
Never put secrets in payload.
Never commit signing secret.
Never rely only on roles for ownership.

Spring objects:
AuthenticationManager = verifies login
UserDetailsService = loads user
PasswordEncoder = verifies password hash
JwtService = creates/parses token
JwtAuthenticationFilter = converts token to Authentication
SecurityContextHolder = stores current request authentication
SecurityFilterChain = defines endpoint access rules
```

---

## 40. One Picture To Remember

```text
                    JWT AUTHENTICATION MENTAL MODEL

                         "Signed identity pass"


LOGIN TIME
----------

Client
  |
  | email + password
  v
+---------------------------+
| AuthenticationManager     |
| checks credentials        |
+---------------------------+
  |
  +-- bad password --> 401
  |
  v
+---------------------------+
| JwtService                |
| creates signed token      |
+---------------------------+
  |
  v
Client stores Bearer token


EVERY PROTECTED REQUEST
-----------------------

Client
  |
  | Authorization: Bearer JWT
  v
+---------------------------+
| JwtAuthenticationFilter   |
| reads token               |
+---------------------------+
  |
  +-- missing/invalid/expired --> 401
  |
  v
+---------------------------+
| Verify Signature + Expiry |
+---------------------------+
  |
  v
+---------------------------+
| Load UserDetails          |
+---------------------------+
  |
  v
+---------------------------+
| SecurityContextHolder     |
| authenticated user        |
+---------------------------+
  |
  v
+---------------------------+
| Authorization Rules       |
| role + ownership checks   |
+---------------------------+
  |
  +-- no permission --> 403
  |
  v
Controller executes


FINAL MEMORY:

JWT is not a session.
JWT is not encrypted.
JWT is not authorization by itself.
JWT is a signed proof of identity that Spring Security converts into Authentication for each request.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. JWT is a signed identity pass used after login.
2. Login verifies password; later requests verify token signature and expiry.
3. The JWT filter converts Authorization: Bearer token into SecurityContext authentication.
4. 401 means missing/invalid authentication; 403 means authenticated but not allowed.
5. JWT gives stateless scaling, but expiry, secret management, token storage, and ownership checks decide production safety.
```

After this chapter, you should understand how JWT fits after Spring Security basics and before deeper topics like refresh tokens, OAuth2, method security, and production authorization.
