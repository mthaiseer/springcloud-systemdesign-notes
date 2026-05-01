# Spring Boot Authentication & Authorization — Visual Reference

> **Goal:** Learn Spring Boot security visually, with small Java examples and step-by-step project setup for multiple ways to secure REST APIs.

---

## Clickable Index

### 0. Foundations
- [What are Authentication and Authorization?](#what-are-authentication-and-authorization)
- [Core Spring Security Flow](#core-spring-security-flow)
- [Project Setup Used in Examples](#project-setup-used-in-examples)
- [Common Demo REST Controller](#common-demo-rest-controller)
- [Common Roles and Access Model](#common-roles-and-access-model)

### 1. Ways to Secure REST APIs
- [Way 1: Public + Protected Endpoints](#way-1-public--protected-endpoints)
- [Way 2: HTTP Basic Auth](#way-2-http-basic-auth)
- [Way 3: Form Login / Session Security](#way-3-form-login--session-security)
- [Way 4: JWT Token Authentication](#way-4-jwt-token-authentication)
- [Way 5: OAuth2 Resource Server](#way-5-oauth2-resource-server)
- [Way 6: API Key Security](#way-6-api-key-security)

### 2. Advanced Topics
- [Method-Level Authorization](#method-level-authorization)
- [Role vs Authority](#role-vs-authority)
- [CSRF: When to Enable or Disable](#csrf-when-to-enable-or-disable)
- [CORS for Frontend Apps](#cors-for-frontend-apps)
- [Password Hashing](#password-hashing)
- [Security Testing with MockMvc](#security-testing-with-mockmvc)
- [Choosing the Right Security Method](#choosing-the-right-security-method)
- [Mini Project Roadmap](#mini-project-roadmap)

---

# What are Authentication and Authorization?

```mermaid
flowchart LR
    User[User / Client] --> AuthN[Authentication]
    AuthN -->|Who are you?| Identity[Identity: alice]
    Identity --> AuthZ[Authorization]
    AuthZ -->|What can you access?| Resource[Protected API]
```

| Term | Meaning | Example |
|---|---|---|
| Authentication | Verifies identity | Login with username/password |
| Authorization | Checks permissions | USER can read profile, ADMIN can delete users |

Simple idea:

```text
Authentication = login
Authorization  = permission check
```

---

# Core Spring Security Flow

```mermaid
sequenceDiagram
    participant Client
    participant Filter as Spring Security Filter Chain
    participant Auth as Authentication Manager
    participant App as Controller

    Client->>Filter: Request /api/private
    Filter->>Filter: Check if endpoint is public
    Filter->>Auth: Validate credentials/token/session
    Auth-->>Filter: Authenticated user + authorities
    Filter->>Filter: Check authorization rules
    Filter->>App: Allow request
    App-->>Client: Response
```

Most Spring Boot security code starts with this bean:

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/public/**").permitAll()
            .anyRequest().authenticated()
        )
        .build();
}
```

---

# Project Setup Used in Examples

## Maven dependencies

Use these for most examples:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

For OAuth2 Resource Server and JWT decoding, add:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

For your own JWT creation, commonly add:

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

## Suggested package structure

```text
src/main/java/com/example/securitydemo
├── SecurityDemoApplication.java
├── controller
│   ├── PublicController.java
│   ├── UserController.java
│   └── AdminController.java
├── security
│   ├── SecurityConfig.java
│   ├── JwtService.java
│   ├── JwtAuthFilter.java
│   └── ApiKeyFilter.java
└── model
    └── LoginRequest.java
```

---

# Common Demo REST Controller

Use this controller for most ways.

```java
package com.example.securitydemo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/public/hello")
    public String publicHello() {
        return "Hello from public API";
    }

    @GetMapping("/user/profile")
    public String userProfile() {
        return "User profile data";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "Admin dashboard data";
    }
}
```

Endpoint map:

```mermaid
flowchart TD
    Client["Client"]
    Public["/api/public/hello"]
    User["/api/user/profile"]
    Admin["/api/admin/dashboard"]
    Anyone["Anyone can access"]
    Login["Requires login"]
    AdminRole["Requires ADMIN role"]

    Client --> Public
    Client --> User
    Client --> Admin

    Public --> Anyone
    User --> Login
    Admin --> AdminRole
```

---

# Common Roles and Access Model

```mermaid
flowchart LR
    ANON[Anonymous] --> PUBLIC[Public endpoints]
    USER[ROLE_USER] --> PUBLIC
    USER --> PROFILE[User profile]
    ADMIN[ROLE_ADMIN] --> PUBLIC
    ADMIN --> PROFILE
    ADMIN --> ADMIN_API[Admin APIs]
```

Recommended test users:

```text
user  / password / ROLE_USER
admin / password / ROLE_ADMIN
```

---

# Way 1: Public + Protected Endpoints

## When to use

Use this when your app has:

- Public APIs such as health, docs, login, signup
- Protected APIs that require login
- Simple role-based access

## Visual flow

```mermaid
flowchart TD
    Request[Incoming Request] --> Match{Endpoint?}
    Match -->|/api/public/**| Permit[Permit All]
    Match -->|/api/user/**| Authenticated[Must be Authenticated]
    Match -->|/api/admin/**| Admin[Must have ADMIN role]
    Permit --> Controller[Controller]
    Authenticated --> Controller
    Admin --> Controller
```

## Step 1: Create project

Add:

```text
spring-boot-starter-web
spring-boot-starter-security
```

## Step 2: Add SecurityConfig

```java
package com.example.securitydemo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/user/**").authenticated()
                .anyRequest().denyAll()
            )
            .httpBasic(basic -> {})
            .build();
    }

    @Bean
    InMemoryUserDetailsManager users(PasswordEncoder encoder) {
        UserDetails user = User.withUsername("user")
            .password(encoder.encode("password"))
            .roles("USER")
            .build();

        UserDetails admin = User.withUsername("admin")
            .password(encoder.encode("password"))
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

## Step 3: Test

```bash
curl http://localhost:8080/api/public/hello

curl -u user:password http://localhost:8080/api/user/profile

curl -u admin:password http://localhost:8080/api/admin/dashboard
```

---

# Way 2: HTTP Basic Auth

## When to use

Use HTTP Basic Auth for:

- Internal tools
- Local development
- Server-to-server APIs over HTTPS
- Simple admin endpoints

Do **not** use it without HTTPS.

## Visual flow

```mermaid
sequenceDiagram
    participant Client
    participant API

    Client->>API: GET /api/user/profile
    API-->>Client: 401 Unauthorized + WWW-Authenticate
    Client->>API: Authorization: Basic base64(user:password)
    API-->>Client: 200 OK
```

## Step 1: Dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

## Step 2: SecurityConfig

```java
@Configuration
public class BasicAuthSecurityConfig {

    @Bean
    SecurityFilterChain basicSecurity(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {})
            .build();
    }

    @Bean
    InMemoryUserDetailsManager users(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
            User.withUsername("user")
                .password(encoder.encode("password"))
                .roles("USER")
                .build()
        );
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

## Step 3: Test

```bash
curl -u user:password http://localhost:8080/api/user/profile
```

## Mental image

```text
Authorization header
└── Basic dXNlcjpwYXNzd29yZA==
    └── base64("user:password")
```

---

# Way 3: Form Login / Session Security

## When to use

Use Form Login for:

- Server-rendered websites
- Admin dashboards
- Browser apps using cookies and sessions

## Visual flow

```mermaid
sequenceDiagram
    participant Browser
    participant Spring
    participant SessionStore

    Browser->>Spring: GET /admin
    Spring-->>Browser: Redirect /login
    Browser->>Spring: POST /login username/password
    Spring->>SessionStore: Create session
    Spring-->>Browser: Set-Cookie JSESSIONID
    Browser->>Spring: GET /admin with cookie
    Spring-->>Browser: Admin page/API
```

## Step 1: Add dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Optional if using Thymeleaf pages:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

## Step 2: SecurityConfig

```java
@Configuration
public class FormLoginSecurityConfig {

    @Bean
    SecurityFilterChain formSecurity(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/css/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
            )
            .build();
    }
}
```

## Step 3: Login page controller

```java
@Controller
public class PageController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    @ResponseBody
    public String home() {
        return "Logged in with session";
    }
}
```

## Step 4: Very small login.html

```html
<form method="post" action="/login">
    <input name="username" placeholder="Username" />
    <input name="password" type="password" placeholder="Password" />
    <button type="submit">Login</button>
</form>
```

## Important note

For browser session security, keep CSRF enabled unless you know why you are disabling it.

```text
Browser + cookies + form login = CSRF protection usually ON
REST API + JWT token = CSRF usually OFF
```

---

# Way 4: JWT Token Authentication

## When to use

Use JWT when:

- Frontend and backend are separate
- Mobile app calls backend APIs
- You want stateless REST APIs
- You want token-based authentication

## Visual flow

```mermaid
sequenceDiagram
    participant Client
    participant AuthAPI
    participant Backend

    Client->>AuthAPI: POST /auth/login username/password
    AuthAPI-->>Client: JWT token
    Client->>Backend: GET /api/user/profile Authorization: Bearer token
    Backend->>Backend: Validate JWT
    Backend-->>Client: Protected data
```

## JWT structure

```mermaid
flowchart LR
    JWT[JWT Token] --> Header[Header]
    JWT --> Payload[Payload / Claims]
    JWT --> Signature[Signature]

    Payload --> Sub[sub: username]
    Payload --> Roles[roles: USER, ADMIN]
    Payload --> Exp[exp: expiry time]
```

## Step 1: Add dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

## Step 2: LoginRequest DTO

```java
package com.example.securitydemo.model;

public record LoginRequest(String username, String password) {}
```

## Step 3: JwtService

```java
package com.example.securitydemo.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final String secret = "change-this-secret-key-change-this-secret-key";
    private final SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    public String createToken(String username, String role) {
        return Jwts.builder()
            .subject(username)
            .claims(Map.of("role", role))
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
            .signWith(key)
            .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    public String extractRole(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("role", String.class);
    }
}
```

## Step 4: AuthController

```java
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        // Demo only. In real apps, validate user from DB using AuthenticationManager.
        if (request.username().equals("admin") && request.password().equals("password")) {
            return Map.of("token", jwtService.createToken("admin", "ROLE_ADMIN"));
        }
        if (request.username().equals("user") && request.password().equals("password")) {
            return Map.of("token", jwtService.createToken("user", "ROLE_USER"));
        }
        throw new RuntimeException("Invalid login");
    }
}
```

## Step 5: JwtAuthFilter

```java
package com.example.securitydemo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String username = jwtService.extractUsername(token);
            String role = jwtService.extractRole(token);

            var auth = new UsernamePasswordAuthenticationToken(
                username,
                null,
                List.of(new SimpleGrantedAuthority(role))
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
```

## Step 6: JWT SecurityConfig

```java
@Configuration
public class JwtSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public JwtSecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    SecurityFilterChain jwtSecurity(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

Required imports:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
```

## Step 7: Test

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}'
```

Then:

```bash
curl http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## JWT mental model

```text
Login once
   ↓
Receive token
   ↓
Send token with every request
   ↓
Backend validates token, no session needed
```

---

# Way 5: OAuth2 Resource Server

## When to use

Use OAuth2 Resource Server when tokens come from an Identity Provider such as:

- Keycloak
- Auth0
- Okta
- Azure AD / Microsoft Entra ID
- Google identity platform
- Custom OAuth2 server

## Visual flow

```mermaid
sequenceDiagram
    participant User
    participant Frontend
    participant IdP as Identity Provider
    participant API as Spring Boot Resource Server

    User->>Frontend: Login
    Frontend->>IdP: Authenticate user
    IdP-->>Frontend: Access Token JWT
    Frontend->>API: Authorization: Bearer JWT
    API->>IdP: Fetch public keys / jwk-set-uri
    API->>API: Validate signature + claims
    API-->>Frontend: Protected API response
```

## Step 1: Add dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

## Step 2: application.yml

Example using issuer URI:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://your-issuer.example.com/realms/demo
```

Or using JWK Set URI:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: https://your-issuer.example.com/.well-known/jwks.json
```

## Step 3: Resource Server SecurityConfig

```java
@Configuration
public class OAuth2ResourceServerSecurityConfig {

    @Bean
    SecurityFilterChain oauth2Security(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasAuthority("SCOPE_admin")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}))
            .build();
    }
}
```

## Step 4: Read user claims

```java
@RestController
@RequestMapping("/api/me")
public class MeController {

    @GetMapping
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
            "subject", jwt.getSubject(),
            "email", jwt.getClaimAsString("email"),
            "scopes", jwt.getClaimAsStringList("scope")
        );
    }
}
```

Import:

```java
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
```

## Step 5: Test

```bash
curl http://localhost:8080/api/me \
  -H "Authorization: Bearer ACCESS_TOKEN_FROM_IDENTITY_PROVIDER"
```

## OAuth2 mental model

```text
Your Spring Boot app does not login users directly.
It trusts signed tokens from an external Identity Provider.
```

---

# Way 6: API Key Security

## When to use

Use API Key security for:

- Partner APIs
- Internal service calls
- Webhooks
- Simple machine-to-machine access

Do not use API keys as a full replacement for user login in complex apps.

## Visual flow

```mermaid
sequenceDiagram
    participant Client
    participant Filter as API Key Filter
    participant API

    Client->>Filter: X-API-KEY: abc123
    Filter->>Filter: Compare key with configured value
    Filter->>API: If valid, continue
    API-->>Client: Response
```

## Step 1: application.yml

```yaml
app:
  security:
    api-key: dev-secret-api-key
```

## Step 2: ApiKeyFilter

```java
package com.example.securitydemo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${app.security.api-key}")
    private String validApiKey;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String apiKey = request.getHeader("X-API-KEY");

        if (validApiKey.equals(apiKey)) {
            var auth = new UsernamePasswordAuthenticationToken(
                "api-client",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_API_CLIENT"))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
```

## Step 3: API Key SecurityConfig

```java
@Configuration
public class ApiKeySecurityConfig {

    private final ApiKeyFilter apiKeyFilter;

    public ApiKeySecurityConfig(ApiKeyFilter apiKeyFilter) {
        this.apiKeyFilter = apiKeyFilter;
    }

    @Bean
    SecurityFilterChain apiKeySecurity(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/partner/**").hasRole("API_CLIENT")
                .anyRequest().authenticated()
            )
            .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

## Step 4: Partner endpoint

```java
@RestController
@RequestMapping("/api/partner")
public class PartnerController {

    @GetMapping("/orders")
    public String partnerOrders() {
        return "Partner orders data";
    }
}
```

## Step 5: Test

```bash
curl http://localhost:8080/api/partner/orders \
  -H "X-API-KEY: dev-secret-api-key"
```

## API Key mental model

```text
Client proves it knows a secret key.
Spring filter converts valid key into an authenticated principal.
```

---

# Method-Level Authorization

Use method security when URL rules are not enough.

## Visual flow

```mermaid
flowchart TD
    Request["Request allowed by URL security"] --> Service["Service method"]
    Service --> Check{"Method annotation"}
    Check -->|"PreAuthorize passes"| Run["Run method"]
    Check -->|"Fails"| Deny["403 Forbidden"]
```

## Step 1: Enable method security

```java
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
}
```

## Step 2: Secure service methods

```java
@Service
public class AccountService {

    @PreAuthorize("hasRole('ADMIN')")
    public String deleteAccount(Long id) {
        return "Deleted account " + id;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String viewAccount(Long id) {
        return "Viewing account " + id;
    }
}
```

---

# Role vs Authority

```mermaid
flowchart LR
    Role["ROLE_ADMIN"] --> HasRole["hasRole ADMIN"]
    Authority["SCOPE_read"] --> HasAuthority["hasAuthority SCOPE_read"]
```

| Concept | Stored as | Check with |
|---|---|---|
| Role | `ROLE_ADMIN` | `hasRole("ADMIN")` |
| Authority | `user:read` or `SCOPE_read` | `hasAuthority("user:read")` |

Small example:

```java
.requestMatchers("/api/admin/**").hasRole("ADMIN")
.requestMatchers("/api/reports/**").hasAuthority("report:read")
```

---

# CSRF: When to Enable or Disable

```mermaid
flowchart TD
    AppType{App type?}
    AppType -->|Browser cookies / session| Enable[Keep CSRF enabled]
    AppType -->|Stateless REST + JWT| Disable[Usually disable CSRF]
    AppType -->|HTTP Basic from browser| Careful[Think carefully]
    AppType -->|Machine-to-machine API| Disable2[Usually disable CSRF]
```

Rule of thumb:

```text
Cookies automatically sent by browser? CSRF matters.
Bearer token manually sent by client? CSRF usually not needed.
```

---

# CORS for Frontend Apps

Use CORS when frontend and backend run on different origins.

Example:

```text
Frontend: http://localhost:3000
Backend:  http://localhost:8080
```

## SecurityConfig with CORS

```java
@Bean
SecurityFilterChain security(HttpSecurity http) throws Exception {
    return http
        .cors(cors -> {})
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/public/**").permitAll()
            .anyRequest().authenticated()
        )
        .build();
}
```

## CORS configuration

```java
@Bean
CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:3000"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-API-KEY"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

Imports:

```java
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
```

---

# Password Hashing

Never store plain passwords.

```mermaid
flowchart LR
    Password[password123] --> Hash[BCrypt hash]
    Hash --> DB[(Database)]
    Login[Login password] --> Compare[BCrypt matches]
    DB --> Compare
```

Use BCrypt:

```java
@Bean
PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Create hashed password:

```java
String hash = passwordEncoder.encode("password");
```

Verify:

```java
boolean ok = passwordEncoder.matches("password", hash);
```

---

# Security Testing with MockMvc

## Dependency

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

## Test role-based access

```java
@SpringBootTest
@AutoConfigureMockMvc
class SecurityTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    void publicEndpointWorksWithoutLogin() throws Exception {
        mockMvc.perform(get("/api/public/hello"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCanAccessProfile() throws Exception {
        mockMvc.perform(get("/api/user/profile"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotAccessAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
            .andExpect(status().isForbidden());
    }
}
```

Imports:

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
```

---

# Choosing the Right Security Method

```mermaid
flowchart TD
    Start[Need security?] --> App{What kind of app?}
    App -->|Public + private API| PublicProtected[Way 1]
    App -->|Internal simple API| Basic[Way 2: HTTP Basic]
    App -->|Server-rendered web app| Form[Way 3: Form Login]
    App -->|SPA / mobile app| JWT[Way 4: JWT]
    App -->|Enterprise login / external IdP| OAuth2[Way 5: OAuth2 Resource Server]
    App -->|Partner / service API| ApiKey[Way 6: API Key]
```

| Way | Best for | State | Common credential |
|---|---|---|---|
| Public + Protected | Basic REST access rules | Depends | Login/basic/JWT |
| HTTP Basic | Internal/simple APIs | Stateless-ish | Username/password header |
| Form Login | Browser web apps | Stateful | Session cookie |
| JWT | SPA/mobile/stateless APIs | Stateless | Bearer token |
| OAuth2 Resource Server | Enterprise/external IdP | Stateless | Bearer access token |
| API Key | Partner/service calls | Stateless | `X-API-KEY` |

---

# Mini Project Roadmap

Build this step by step.

```mermaid
flowchart TD
    S1[Step 1: Public hello API]
    S2[Step 2: Protected user API]
    S3[Step 3: Admin-only API]
    S4[Step 4: Basic Auth]
    S5[Step 5: Form login]
    S6[Step 6: JWT login]
    S7[Step 7: OAuth2 Resource Server]
    S8[Step 8: API key partner API]
    S9[Step 9: Tests]

    S1 --> S2 --> S3 --> S4 --> S5 --> S6 --> S7 --> S8 --> S9
```

## Suggested endpoints

```text
GET  /api/public/hello        anyone
GET  /api/user/profile        logged-in user
GET  /api/admin/dashboard     admin only
POST /auth/login              JWT login
GET  /api/partner/orders      API key client only
```

## Final architecture

```mermaid
flowchart LR
    Browser[Browser App] -->|Form Login / Session| Spring[Spring Boot API]
    Mobile[Mobile App] -->|JWT Bearer Token| Spring
    Partner[Partner Service] -->|X-API-KEY| Spring
    Enterprise[Enterprise Frontend] -->|OAuth2 Access Token| Spring

    Spring --> Security[Spring Security Filter Chain]
    Security --> Controllers[REST Controllers]
    Controllers --> Services[Services]
    Services --> DB[(Database)]
```

---

# Quick Cheat Sheet

## Permit public endpoint

```java
.requestMatchers("/api/public/**").permitAll()
```

## Require login

```java
.anyRequest().authenticated()
```

## Require role

```java
.requestMatchers("/api/admin/**").hasRole("ADMIN")
```

## Enable HTTP Basic

```java
.httpBasic(basic -> {})
```

## Enable Form Login

```java
.formLogin(form -> form.permitAll())
```

## Stateless API

```java
.sessionManagement(session ->
    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```

## Add custom filter

```java
.addFilterBefore(myFilter, UsernamePasswordAuthenticationFilter.class)
```

## OAuth2 Resource Server

```java
.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}))
```

---

# Practice Use Cases

## Use Case 1: Blog API

```text
GET    /posts             public
POST   /posts             USER
DELETE /posts/{id}        ADMIN
```

Best option: JWT or Form Login.

## Use Case 2: Admin Dashboard

```text
GET /admin/users
GET /admin/reports
```

Best option: Form Login or OAuth2.

## Use Case 3: Mobile Banking API

```text
POST /auth/login
GET  /accounts
POST /transfer
```

Best option: JWT or OAuth2.

## Use Case 4: Partner Order API

```text
GET /api/partner/orders
POST /api/partner/status
```

Best option: API Key, OAuth2 client credentials, or mTLS.

## Use Case 5: Enterprise Company App

```text
Login with company account
Access depends on department/role
```

Best option: OAuth2 Resource Server.

---

# Common Mistakes

| Mistake | Fix |
|---|---|
| Storing plain passwords | Use BCrypt |
| Using JWT but keeping sessions | Set stateless session policy |
| Disabling CSRF for form login without reason | Keep CSRF enabled for browser sessions |
| Putting secrets directly in code | Use environment variables or secret manager |
| Using API key for real user login | Use JWT/OAuth2 instead |
| Confusing role and authority | `hasRole("ADMIN")` means `ROLE_ADMIN` |

---

# Final Memory Picture

```mermaid
mindmap
  root((Spring Security))
    Authentication
      Basic Auth
      Form Login
      JWT
      OAuth2
      API Key
    Authorization
      URL rules
      Roles
      Authorities
      Method security
    REST API
      Stateless
      Bearer token
      API key
    Browser App
      Session
      Cookie
      CSRF
```

