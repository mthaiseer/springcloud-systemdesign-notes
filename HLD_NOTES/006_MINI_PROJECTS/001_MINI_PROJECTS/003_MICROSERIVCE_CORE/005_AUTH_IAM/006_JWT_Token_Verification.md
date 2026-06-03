# MiniAuth / IAM Phase 6 — JWT Token Verification

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why This Phase Matters](#2-why-this-phase-matters)
- [3. What We Built Previously](#3-what-we-built-previously)
- [4. Previous Limitation](#4-previous-limitation)
- [5. What We Build](#5-what-we-build)
- [6. Current Architecture](#6-current-architecture)
- [7. Folder Structure](#7-folder-structure)
- [8. Core Concepts](#8-core-concepts)
- [9. Complete Java Code](#9-complete-java-code)
  - [9.1 User.java](#91-userjava)
  - [9.2 UserRepository.java](#92-userrepositoryjava)
  - [9.3 PasswordEncoder.java](#93-passwordencoderjava)
  - [9.4 LoginRequest.java](#94-loginrequestjava)
  - [9.5 LoginResponse.java](#95-loginresponsejava)
  - [9.6 AuthenticatedPrincipal.java](#96-authenticatedprincipaljava)
  - [9.7 JwtTokenService.java](#97-jwttokenservicejava)
  - [9.8 AuthenticationService.java](#98-authenticationservicejava)
  - [9.9 Phase006Driver.java](#99-phase006driverjava)
- [10. How To Run](#10-how-to-run)
- [11. Dry Run](#11-dry-run)
- [12. What Changed From Phase 5](#12-what-changed-from-phase-5)
- [13. What This Phase Does NOT Do Yet](#13-what-this-phase-does-not-do-yet)
- [14. DSA / CP Concepts Used](#14-dsa--cp-concepts-used)
- [15. System Design Relevance](#15-system-design-relevance)
- [16. Production-Grade Concepts](#16-production-grade-concepts)
- [17. Common Bugs](#17-common-bugs)
- [18. Interview Notes](#18-interview-notes)
- [19. Next Step](#19-next-step)

---

# 1. Goal

In this phase, we move from:

```text
generate JWT
```

to:

```text
verify JWT
```

Before this phase:

```text
login success -> JWT access token
```

After this phase:

```text
JWT access token -> authenticated principal
```

Main objective:

```text
Verify token signature, check expiry, and extract user identity.
```

---

# 2. Why This Phase Matters

JWT generation is only half of the story.

A protected API must answer:

```text
Is this token valid?
Was this token created by our system?
Was this token modified?
Has this token expired?
Which user does this token represent?
```

Without verification, anyone could send fake tokens.

---

# 3. What We Built Previously

## Phase 5

We built:

```text
JwtTokenService.generateAccessToken(user)
```

It created:

```text
header.payload.signature
```

Now Phase 6 adds:

```text
JwtTokenService.verifyAccessToken(token)
```

---

# 4. Previous Limitation

Phase 5 generated a token, but no service could verify it.

That means this token was not useful for protecting APIs yet.

Example:

```text
Authorization: Bearer <jwt>
```

The server needs to verify this token before allowing access.

---

# 5. What We Build

We add:

```text
AuthenticatedPrincipal
JwtTokenService.verifyAccessToken()
```

Verification flow:

```text
token
  -> split into 3 parts
  -> recompute signature
  -> compare signature
  -> decode payload
  -> check exp
  -> extract sub/email
  -> return AuthenticatedPrincipal
```

---

# 6. Current Architecture

```text
+-------------------------+
| Client Request          |
| Authorization: Bearer   |
+------------+------------+
             |
             v
+-------------------------+
| JwtTokenService         |
| - split token           |
| - verify signature      |
| - decode payload        |
| - check expiry          |
| - extract claims        |
+------------+------------+
             |
             v
+-------------------------+
| AuthenticatedPrincipal  |
| - userId                |
| - email                 |
| - issuer                |
| - expiresAt             |
+-------------------------+
```

Full auth path:

```text
Login:
email + password -> access token

Protected API:
access token -> verify -> principal -> allow request
```

---

# 7. Folder Structure

```text
MiniAuth/
└── src/
    └── main/
        └── java/
            └── com/
                └── miniauth/
                    ├── model/
                    │   └── User.java
                    ├── dto/
                    │   ├── LoginRequest.java
                    │   └── LoginResponse.java
                    ├── repository/
                    │   └── UserRepository.java
                    ├── security/
                    │   ├── PasswordEncoder.java
                    │   └── AuthenticatedPrincipal.java
                    ├── jwt/
                    │   └── JwtTokenService.java
                    ├── service/
                    │   └── AuthenticationService.java
                    └── driver/
                        └── Phase006Driver.java
```

---

# 8. Core Concepts

## 8.1 Token Verification

JWT verification means:

```text
Do not trust the token until signature and expiry are checked.
```

---

## 8.2 Signature Check

For HS256-style token:

```text
expectedSignature = HMAC(secret, header.payload)
```

Then compare:

```text
expectedSignature == tokenSignature
```

If payload is modified, signature mismatch happens.

---

## 8.3 Expiry Check

JWT has:

```text
exp
```

Example:

```json
{
  "exp": 1710000900
}
```

If:

```text
now > exp
```

token is expired.

---

## 8.4 Principal

A principal is the authenticated user context.

Example:

```text
userId
email
issuer
expiresAt
```

Protected services usually use principal to make authorization decisions.

---

# 9. Complete Java Code

---

## 9.1 `User.java`

```java
package com.miniauth.model;

import java.time.Instant;
import java.util.UUID;

public class User {

    private final String id;
    private final String email;
    private final String passwordHash;
    private final Instant createdAt;

    public User(String email, String passwordHash) {
        this.id = UUID.randomUUID().toString();
        this.email = email.toLowerCase().trim();
        this.passwordHash = passwordHash;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
```

---

## 9.2 `UserRepository.java`

```java
package com.miniauth.repository;

import com.miniauth.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserRepository {

    private final Map<String, User> usersByEmail = new HashMap<>();

    public void save(User user) {
        usersByEmail.put(user.getEmail(), user);
    }

    public Optional<User> findByEmail(String email) {
        String normalizedEmail = normalizeEmail(email);
        return Optional.ofNullable(usersByEmail.get(normalizedEmail));
    }

    private String normalizeEmail(String email) {
        return email.toLowerCase().trim();
    }
}
```

---

## 9.3 `PasswordEncoder.java`

```java
package com.miniauth.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordEncoder {

    private final SecureRandom random = new SecureRandom();

    public String hash(String rawPassword) {
        String salt = generateSalt();
        String hash = sha256(salt + rawPassword);
        return salt + ":" + hash;
    }

    public boolean matches(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) {
            return false;
        }

        String[] parts = storedHash.split(":");

        if (parts.length != 2) {
            return false;
        }

        String salt = parts[0];
        String expectedHash = parts[1];
        String actualHash = sha256(salt + rawPassword);

        return expectedHash.equals(actualHash);
    }

    private String generateSalt() {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String sha256(String input) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hashedBytes =
                    digest.digest(input.getBytes(StandardCharsets.UTF_8));

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hashedBytes);

        } catch (Exception ex) {
            throw new RuntimeException("Hashing failed", ex);
        }
    }
}
```

---

## 9.4 `LoginRequest.java`

```java
package com.miniauth.dto;

public class LoginRequest {

    private final String email;
    private final String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
```

---

## 9.5 `LoginResponse.java`

```java
package com.miniauth.dto;

public class LoginResponse {

    private final boolean authenticated;
    private final String userId;
    private final String email;
    private final String accessToken;
    private final long expiresInSeconds;
    private final String message;

    public LoginResponse(
            boolean authenticated,
            String userId,
            String email,
            String accessToken,
            long expiresInSeconds,
            String message
    ) {
        this.authenticated = authenticated;
        this.userId = userId;
        this.email = email;
        this.accessToken = accessToken;
        this.expiresInSeconds = expiresInSeconds;
        this.message = message;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "authenticated=" + authenticated +
                ", userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", expiresInSeconds=" + expiresInSeconds +
                ", message='" + message + '\'' +
                '}';
    }
}
```

---

## 9.6 `AuthenticatedPrincipal.java`

### Logic before this class

This represents the authenticated user extracted from a valid JWT.

```java
package com.miniauth.security;

public class AuthenticatedPrincipal {

    private final String userId;
    private final String email;
    private final String issuer;
    private final long issuedAt;
    private final long expiresAt;

    public AuthenticatedPrincipal(
            String userId,
            String email,
            String issuer,
            long issuedAt,
            long expiresAt
    ) {
        this.userId = userId;
        this.email = email;
        this.issuer = issuer;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getIssuer() {
        return issuer;
    }

    public long getIssuedAt() {
        return issuedAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    @Override
    public String toString() {
        return "AuthenticatedPrincipal{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", issuer='" + issuer + '\'' +
                ", issuedAt=" + issuedAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
```

---

## 9.7 `JwtTokenService.java`

### Logic before this class

This service now does two jobs:

```text
1. Generate JWT
2. Verify JWT
```

Verification checks:

```text
token format
signature
expiry
required claims
```

```java
package com.miniauth.jwt;

import com.miniauth.model.User;
import com.miniauth.security.AuthenticatedPrincipal;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

public class JwtTokenService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final String issuer;
    private final String secret;
    private final long accessTokenTtlSeconds;

    public JwtTokenService(
            String issuer,
            String secret,
            long accessTokenTtlSeconds
    ) {
        this.issuer = issuer;
        this.secret = secret;
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
    }

    public String generateAccessToken(User user) {

        long issuedAt = Instant.now().getEpochSecond();
        long expiresAt = issuedAt + accessTokenTtlSeconds;

        String headerJson = "{"
                + "\"alg\":\"HS256\","
                + "\"typ\":\"JWT\""
                + "}";

        String payloadJson = "{"
                + "\"iss\":\"" + issuer + "\","
                + "\"sub\":\"" + user.getId() + "\","
                + "\"email\":\"" + user.getEmail() + "\","
                + "\"iat\":" + issuedAt + ","
                + "\"exp\":" + expiresAt
                + "}";

        String encodedHeader = base64UrlEncode(headerJson);
        String encodedPayload = base64UrlEncode(payloadJson);

        String unsignedToken = encodedHeader + "." + encodedPayload;
        String signature = sign(unsignedToken);

        return unsignedToken + "." + signature;
    }

    public Optional<AuthenticatedPrincipal> verifyAccessToken(String token) {

        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        String[] parts = token.split("\\.");

        if (parts.length != 3) {
            return Optional.empty();
        }

        String encodedHeader = parts[0];
        String encodedPayload = parts[1];
        String providedSignature = parts[2];

        String unsignedToken = encodedHeader + "." + encodedPayload;
        String expectedSignature = sign(unsignedToken);

        if (!expectedSignature.equals(providedSignature)) {
            return Optional.empty();
        }

        String payloadJson = base64UrlDecode(encodedPayload);

        String tokenIssuer = extractStringClaim(payloadJson, "iss");
        String userId = extractStringClaim(payloadJson, "sub");
        String email = extractStringClaim(payloadJson, "email");
        long issuedAt = extractLongClaim(payloadJson, "iat");
        long expiresAt = extractLongClaim(payloadJson, "exp");

        if (tokenIssuer == null || !tokenIssuer.equals(issuer)) {
            return Optional.empty();
        }

        if (userId == null || email == null) {
            return Optional.empty();
        }

        long now = Instant.now().getEpochSecond();

        if (now > expiresAt) {
            return Optional.empty();
        }

        return Optional.of(
                new AuthenticatedPrincipal(
                        userId,
                        email,
                        tokenIssuer,
                        issuedAt,
                        expiresAt
                )
        );
    }

    public long getAccessTokenTtlSeconds() {
        return accessTokenTtlSeconds;
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);

            SecretKeySpec keySpec =
                    new SecretKeySpec(
                            secret.getBytes(StandardCharsets.UTF_8),
                            HMAC_ALGORITHM
                    );

            mac.init(keySpec);

            byte[] signatureBytes =
                    mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(signatureBytes);

        } catch (Exception ex) {
            throw new RuntimeException("JWT signing failed", ex);
        }
    }

    private String base64UrlEncode(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String base64UrlDecode(String value) {
        byte[] decoded =
                Base64.getUrlDecoder().decode(value);

        return new String(decoded, StandardCharsets.UTF_8);
    }

    private String extractStringClaim(String json, String claimName) {
        String pattern = "\"" + claimName + "\":\"";
        int start = json.indexOf(pattern);

        if (start == -1) {
            return null;
        }

        start += pattern.length();

        int end = json.indexOf("\"", start);

        if (end == -1) {
            return null;
        }

        return json.substring(start, end);
    }

    private long extractLongClaim(String json, String claimName) {
        String pattern = "\"" + claimName + "\":";
        int start = json.indexOf(pattern);

        if (start == -1) {
            return -1;
        }

        start += pattern.length();

        int end = start;

        while (end < json.length() &&
                Character.isDigit(json.charAt(end))) {
            end++;
        }

        if (end == start) {
            return -1;
        }

        return Long.parseLong(json.substring(start, end));
    }
}
```

---

## 9.8 `AuthenticationService.java`

```java
package com.miniauth.service;

import com.miniauth.dto.LoginRequest;
import com.miniauth.dto.LoginResponse;
import com.miniauth.jwt.JwtTokenService;
import com.miniauth.model.User;
import com.miniauth.repository.UserRepository;
import com.miniauth.security.PasswordEncoder;

public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthenticationService(
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponse login(LoginRequest request) {

        String normalizedEmail =
                request.getEmail().toLowerCase().trim();

        User user = repository.findByEmail(normalizedEmail)
                .orElse(null);

        if (user == null) {
            return failedResponse();
        }

        boolean passwordMatched =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPasswordHash()
                );

        if (!passwordMatched) {
            return failedResponse();
        }

        String accessToken =
                jwtTokenService.generateAccessToken(user);

        return new LoginResponse(
                true,
                user.getId(),
                user.getEmail(),
                accessToken,
                jwtTokenService.getAccessTokenTtlSeconds(),
                "Login successful"
        );
    }

    private LoginResponse failedResponse() {
        return new LoginResponse(
                false,
                null,
                null,
                null,
                0,
                "Invalid email or password"
        );
    }
}
```

---

## 9.9 `Phase006Driver.java`

### Logic before this class

This driver proves:

```text
1. Login generates JWT.
2. Valid JWT becomes AuthenticatedPrincipal.
3. Modified JWT fails verification.
4. Malformed JWT fails verification.
```

```java
package com.miniauth.driver;

import com.miniauth.dto.LoginRequest;
import com.miniauth.dto.LoginResponse;
import com.miniauth.jwt.JwtTokenService;
import com.miniauth.model.User;
import com.miniauth.repository.UserRepository;
import com.miniauth.security.AuthenticatedPrincipal;
import com.miniauth.security.PasswordEncoder;
import com.miniauth.service.AuthenticationService;

import java.util.Optional;

public class Phase006Driver {

    public static void main(String[] args) {

        UserRepository userRepository = new UserRepository();
        PasswordEncoder passwordEncoder = new PasswordEncoder();

        JwtTokenService jwtTokenService =
                new JwtTokenService(
                        "MiniAuth",
                        "change-this-secret-in-production",
                        15 * 60
                );

        String passwordHash =
                passwordEncoder.hash("StrongPass123");

        User user = new User(
                "mohamed@example.com",
                passwordHash
        );

        userRepository.save(user);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        jwtTokenService
                );

        LoginResponse loginResponse =
                authenticationService.login(
                        new LoginRequest(
                                "mohamed@example.com",
                                "StrongPass123"
                        )
                );

        String token = loginResponse.getAccessToken();

        System.out.println("Generated token:");
        System.out.println(token);

        Optional<AuthenticatedPrincipal> principal =
                jwtTokenService.verifyAccessToken(token);

        System.out.println();
        System.out.println("Valid token principal:");
        System.out.println(principal.orElse(null));

        String modifiedToken = token.replace("a", "b");

        Optional<AuthenticatedPrincipal> modifiedPrincipal =
                jwtTokenService.verifyAccessToken(modifiedToken);

        System.out.println();
        System.out.println("Modified token accepted?");
        System.out.println(modifiedPrincipal.isPresent());

        Optional<AuthenticatedPrincipal> malformedPrincipal =
                jwtTokenService.verifyAccessToken("not.a.valid.jwt");

        System.out.println();
        System.out.println("Malformed token accepted?");
        System.out.println(malformedPrincipal.isPresent());
    }
}
```

---

# 10. How To Run

## IntelliJ

1. Create Java project `MiniAuth`
2. Create package:

```text
com.miniauth
```

3. Add the folder structure shown above.
4. Copy all Java classes.
5. Run:

```text
Phase006Driver
```

---

## Command Line

From project root:

```bash
javac -d out src/main/java/com/miniauth/**/*.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase006Driver
```

Windows CMD example:

```cmd
javac -d out ^
src/main/java/com/miniauth/model/User.java ^
src/main/java/com/miniauth/dto/LoginRequest.java ^
src/main/java/com/miniauth/dto/LoginResponse.java ^
src/main/java/com/miniauth/repository/UserRepository.java ^
src/main/java/com/miniauth/security/PasswordEncoder.java ^
src/main/java/com/miniauth/security/AuthenticatedPrincipal.java ^
src/main/java/com/miniauth/jwt/JwtTokenService.java ^
src/main/java/com/miniauth/service/AuthenticationService.java ^
src/main/java/com/miniauth/driver/Phase006Driver.java
```

Run:

```cmd
java -cp out com.miniauth.driver.Phase006Driver
```

---

# 11. Dry Run

Input token:

```text
header.payload.signature
```

Step-by-step:

```text
1. Split token by dot.
2. Ensure token has 3 parts.
3. Rebuild unsigned token:
   header.payload
4. Recompute signature using server secret.
5. Compare recomputed signature with token signature.
6. Decode payload.
7. Extract iss, sub, email, iat, exp.
8. Validate issuer.
9. Validate expiry.
10. Return AuthenticatedPrincipal.
```

Valid token:

```text
returns principal
```

Modified token:

```text
signature mismatch
returns empty
```

Expired token:

```text
expiry failed
returns empty
```

---

# 12. What Changed From Phase 5

## Phase 5

```text
generate JWT access token
```

## Phase 6

```text
verify JWT access token
```

New class:

```text
AuthenticatedPrincipal
```

New method:

```text
verifyAccessToken(token)
```

New capability:

```text
Authorization: Bearer token
    -> authenticated user context
```

---

# 13. What This Phase Does NOT Do Yet

This phase does not yet include:

```text
JWT filter
HTTP request middleware
role claims
permission claims
refresh token
token revocation
key rotation
RS256 public/private keys
JWK endpoint
OAuth2/OIDC standards
```

These come in later phases.

---

# 14. DSA / CP Concepts Used

## String Parsing

JWT verification uses:

```text
split by dot
extract claims from JSON string
```

---

## Hash / HMAC Verification

Concept:

```text
same data + same secret -> same signature
modified data -> different signature
```

---

## Guard Clauses

Verification has many early exits:

```text
if invalid format -> reject
if bad signature -> reject
if expired -> reject
```

This is like defensive programming in CP and backend code.

---

## Time-Based Validity

Expiry check:

```text
now <= exp
```

---

# 15. System Design Relevance

JWT verification is used in:

```text
API Gateway
Auth middleware
Resource servers
Microservices
Mobile APIs
SPA backends
```

Architecture:

```text
Client
  -> API Gateway
  -> JWT verification
  -> Service receives user context
```

At scale:

```text
JWT verification can be local and stateless
```

This avoids session database lookup for every request.

---

# 16. Production-Grade Concepts

## Use JWT Libraries

Do not manually parse JWT in production.

Use:

```text
Spring Security OAuth2 Resource Server
Nimbus JOSE + JWT
jjwt
java-jwt
```

---

## Constant-Time Signature Comparison

Production should avoid timing leaks.

This mini version uses:

```java
expectedSignature.equals(providedSignature)
```

Better:

```text
constant-time comparison
```

---

## Validate Claims

Production should validate:

```text
iss
aud
sub
exp
nbf
iat
scope
roles
tenant
```

---

## Clock Skew

Distributed systems may allow small clock skew:

```text
30 seconds
60 seconds
```

---

## Key Rotation

Production should support:

```text
kid header
multiple active keys
old key verification during rotation
new key signing
```

---

# 17. Common Bugs

## Bug 1 — Trusting Payload Without Signature Check

Bad:

```text
decode JWT payload and trust userId
```

Good:

```text
verify signature first
```

---

## Bug 2 — Not Checking Expiry

Bad:

```text
token valid forever
```

Good:

```text
reject now > exp
```

---

## Bug 3 — Accepting Wrong Issuer

Bad:

```text
any token signed by any service is accepted
```

Good:

```text
validate iss claim
```

---

## Bug 4 — Storing Secrets In Token

JWT payload can be decoded.

Never store:

```text
password
credit card
private keys
OTP
refresh token
```

---

# 18. Interview Notes

If interviewer asks:

```text
How do you verify JWT?
```

Answer:

```text
1. Extract token from Authorization header.
2. Split into header, payload, signature.
3. Verify algorithm and key.
4. Recompute signature.
5. Compare signature safely.
6. Decode claims.
7. Validate expiry.
8. Validate issuer and audience.
9. Build authenticated principal.
10. Use principal for authorization.
```

Strong follow-up:

```text
JWT is not encrypted by default.
Signature proves integrity, not secrecy.
```

---

# 19. Next Step

Next file:

```text
007_Refresh_Token_Flow.md
```

In the next phase, we add:

```text
short-lived access token
long-lived refresh token
token rotation
refresh endpoint
```
