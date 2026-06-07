# MiniAuth / IAM Phase 5 — JWT Token Generation

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why This Phase Matters](#2-why-this-phase-matters)
- [3. What We Built Previously](#3-what-we-built-previously)
- [4. Previous Limitation](#4-previous-limitation)
- [5. What We Build](#5-what-we-build)
- [6. Current Architecture](#6-current-architecture)
- [7. Folder Structure](#7-folder-structure)
- [8. Core Concepts](#8-core-concepts)
- [9. JWT Structure](#9-jwt-structure)
- [10. Complete Java Code](#10-complete-java-code)
  - [10.1 User.java](#101-userjava)
  - [10.2 UserRepository.java](#102-userrepositoryjava)
  - [10.3 PasswordEncoder.java](#103-passwordencoderjava)
  - [10.4 LoginRequest.java](#104-loginrequestjava)
  - [10.5 LoginResponse.java](#105-loginresponsejava)
  - [10.6 JwtTokenService.java](#106-jwttokenservicejava)
  - [10.7 AuthenticationService.java](#107-authenticationservicejava)
  - [10.8 Phase005Driver.java](#108-phase005driverjava)
- [11. How To Run](#11-how-to-run)
- [12. Dry Run](#12-dry-run)
- [13. What Changed From Phase 4](#13-what-changed-from-phase-4)
- [14. What This Phase Does NOT Do Yet](#14-what-this-phase-does-not-do-yet)
- [15. DSA / CP Concepts Used](#15-dsa--cp-concepts-used)
- [16. System Design Relevance](#16-system-design-relevance)
- [17. Production-Grade Concepts](#17-production-grade-concepts)
- [18. Common Bugs](#18-common-bugs)
- [19. Interview Notes](#19-interview-notes)
- [20. Next Step](#20-next-step)

---

# 1. Goal

In this phase, we move from server-side session-only login to JWT access token generation.

Before this phase:

```text
email + password -> sessionId
```

After this phase:

```text
email + password -> signed JWT access token
```

Main objective:

```text
Generate a signed token after successful login.
```

---

# 2. Why This Phase Matters

JWT is commonly used in modern APIs and microservices.

A JWT lets the client send identity information on each request using:

```http
Authorization: Bearer <token>
```

The server can verify the token signature without looking up a session in a database.

This helps in distributed systems where many services need to know:

```text
who is the user?
what is the user id?
when does the token expire?
```

---

# 3. What We Built Previously

## Phase 1

```text
User Registration
```

## Phase 2

```text
Password Hashing
```

## Phase 3

```text
Login Authentication
```

## Phase 4

```text
Session Management
```

Phase 4 created:

```text
server-side sessionId
```

Phase 5 creates:

```text
signed JWT access token
```

---

# 4. Previous Limitation

Session-based auth stores state on the server:

```text
sessionId -> Session
```

This is good for logout and revocation.

But it requires a session lookup.

JWT-style access token allows:

```text
stateless verification
```

That means:

```text
no session lookup needed for every request
```

Later, JWT verification will be added in Phase 6.

---

# 5. What We Build

We add:

```text
JwtTokenService
```

It creates JWT-like tokens.

For this mini project, we implement JWT manually using Java standard libraries:

```text
Base64Url(header) + "." + Base64Url(payload) + "." + HMAC-SHA256 signature
```

Important:

```text
This is for learning.
Production should use a battle-tested JWT library.
```

---

# 6. Current Architecture

```text
+--------------------+
| Client / Driver    |
+---------+----------+
          |
          v
+--------------------+
| LoginRequest       |
+---------+----------+
          |
          v
+-----------------------------+
| AuthenticationService       |
| - validate credentials      |
| - create JWT token          |
+--------------+--------------+
               |
               v
+-----------------------------+
| JwtTokenService             |
| - create header             |
| - create payload            |
| - sign token                |
+--------------+--------------+
               |
               v
+-----------------------------+
| LoginResponse               |
| - authenticated             |
| - userId                    |
| - accessToken               |
| - expiresInSeconds          |
+-----------------------------+
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
                    │   └── PasswordEncoder.java
                    ├── jwt/
                    │   └── JwtTokenService.java
                    ├── service/
                    │   └── AuthenticationService.java
                    └── driver/
                        └── Phase005Driver.java
```

---

# 8. Core Concepts

## 8.1 JWT

JWT means:

```text
JSON Web Token
```

It is a signed token containing claims.

Common claims:

```text
sub -> subject / user id
email -> user email
iat -> issued at
exp -> expiry time
iss -> issuer
```

---

## 8.2 Stateless Authentication

With JWT:

```text
server signs token
client stores token
client sends token on each request
server verifies signature
```

The server does not need to store every access token.

---

## 8.3 Signature

Signature proves:

```text
token was created by trusted server
token was not modified
```

If someone changes the payload, signature verification fails.

---

## 8.4 Access Token

An access token is usually short-lived.

Example:

```text
15 minutes
```

Why short-lived?

```text
If stolen, damage window is small.
```

---

# 9. JWT Structure

JWT has three parts:

```text
header.payload.signature
```

Example:

```text
xxxxx.yyyyy.zzzzz
```

## Header

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

## Payload

```json
{
  "sub": "user-id",
  "email": "mohamed@example.com",
  "iat": 1710000000,
  "exp": 1710000900
}
```

## Signature

```text
HMACSHA256(
    base64Url(header) + "." + base64Url(payload),
    secret
)
```

---

# 10. Complete Java Code

---

## 10.1 `User.java`

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

## 10.2 `UserRepository.java`

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

    public boolean existsByEmail(String email) {
        String normalizedEmail = normalizeEmail(email);
        return usersByEmail.containsKey(normalizedEmail);
    }

    private String normalizeEmail(String email) {
        return email.toLowerCase().trim();
    }
}
```

---

## 10.3 `PasswordEncoder.java`

```java
package com.miniauth.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordEncoder {

    private final SecureRandom random = new SecureRandom();

    public String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

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

## 10.4 `LoginRequest.java`

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

## 10.5 `LoginResponse.java`

### Logic before this class

Phase 5 response returns:

```text
accessToken
expiresInSeconds
```

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

## 10.6 `JwtTokenService.java`

### Logic before this class

This service creates a JWT-like access token.

It builds:

```text
header
payload
signature
```

Then returns:

```text
header.payload.signature
```

```java
package com.miniauth.jwt;

import com.miniauth.model.User;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

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

        String encodedHeader = base64Url(headerJson);
        String encodedPayload = base64Url(payloadJson);

        String unsignedToken =
                encodedHeader + "." + encodedPayload;

        String signature = sign(unsignedToken);

        return unsignedToken + "." + signature;
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

    private String base64Url(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
```

---

## 10.7 `AuthenticationService.java`

### Logic before this class

Phase 4 created server-side session ID.

Phase 5 creates a JWT access token after successful login.

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

        validate(request);

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

    private void validate(LoginRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (request.getEmail() == null ||
                request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (request.getPassword() == null ||
                request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
    }
}
```

---

## 10.8 `Phase005Driver.java`

### Logic before this class

This driver proves:

```text
1. Login succeeds.
2. JWT access token is generated.
3. Token has three parts.
4. Token is returned to client.
```

```java
package com.miniauth.driver;

import com.miniauth.dto.LoginRequest;
import com.miniauth.dto.LoginResponse;
import com.miniauth.jwt.JwtTokenService;
import com.miniauth.model.User;
import com.miniauth.repository.UserRepository;
import com.miniauth.security.PasswordEncoder;
import com.miniauth.service.AuthenticationService;

public class Phase005Driver {

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

        System.out.println("Login response:");
        System.out.println(loginResponse);

        String token = loginResponse.getAccessToken();

        System.out.println();
        System.out.println("JWT token:");
        System.out.println(token);

        System.out.println();
        System.out.println("JWT parts count:");
        System.out.println(token.split("\\.").length);
    }
}
```

---

# 11. How To Run

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
Phase005Driver
```

---

## Command Line

From project root:

```bash
javac -d out src/main/java/com/miniauth/**/*.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase005Driver
```

Windows CMD example:

```cmd
javac -d out ^
src/main/java/com/miniauth/model/User.java ^
src/main/java/com/miniauth/dto/LoginRequest.java ^
src/main/java/com/miniauth/dto/LoginResponse.java ^
src/main/java/com/miniauth/repository/UserRepository.java ^
src/main/java/com/miniauth/security/PasswordEncoder.java ^
src/main/java/com/miniauth/jwt/JwtTokenService.java ^
src/main/java/com/miniauth/service/AuthenticationService.java ^
src/main/java/com/miniauth/driver/Phase005Driver.java
```

Run:

```cmd
java -cp out com.miniauth.driver.Phase005Driver
```

---

# 12. Dry Run

Stored user:

```text
email = mohamed@example.com
passwordHash = salt:hash
```

Login input:

```text
email = mohamed@example.com
password = StrongPass123
```

Step-by-step:

```text
1. AuthenticationService receives login request.
2. Email and password are validated.
3. User is loaded from repository.
4. PasswordEncoder verifies password.
5. Password is correct.
6. JwtTokenService creates header JSON.
7. JwtTokenService creates payload JSON.
8. Header and payload are Base64Url encoded.
9. Service signs header.payload using HMAC-SHA256.
10. Token is returned as header.payload.signature.
11. LoginResponse returns accessToken.
```

Output shape:

```text
LoginResponse(
    authenticated = true,
    userId = uuid,
    email = mohamed@example.com,
    accessToken = xxx.yyy.zzz,
    expiresInSeconds = 900
)
```

---

# 13. What Changed From Phase 4

## Phase 4

```text
login success -> create sessionId
```

## Phase 5

```text
login success -> create JWT access token
```

New class:

```text
JwtTokenService
```

Updated class:

```text
LoginResponse now contains accessToken
AuthenticationService calls JwtTokenService
```

---

# 14. What This Phase Does NOT Do Yet

This phase only generates JWT.

It does not yet:

```text
verify JWT
parse claims
check expiry
protect APIs using JWT
refresh expired access token
revoke JWT
rotate signing keys
support RS256 public/private keys
```

These come later.

Next phase adds:

```text
JWT token verification
```

---

# 15. DSA / CP Concepts Used

## String Construction

JWT is built from strings:

```text
header + "." + payload + "." + signature
```

---

## Base64 Encoding

Header and payload are encoded.

This is not encryption.

Important:

```text
Anyone can decode JWT header and payload.
```

So do not store secrets in JWT payload.

---

## Hash-Based Message Authentication Code

HMAC signs data using a secret key.

Conceptually:

```text
signature = HMAC(secret, data)
```

If data changes:

```text
signature changes
```

---

## Time / Expiry

JWT contains:

```text
iat
exp
```

This connects to TTL/cache expiry patterns.

---

# 16. System Design Relevance

JWT generation appears in:

```text
microservices
mobile APIs
single page apps
API gateways
OAuth2 access tokens
OIDC ID tokens
```

HLD view:

```text
Client
  -> API Gateway
  -> Auth Service
  -> User DB
  -> JWT issued
```

Future request:

```text
Client
  -> API Gateway with Bearer token
  -> token verified
  -> request forwarded to service
```

---

# 17. Production-Grade Concepts

## Use A JWT Library

Production Java should use libraries like:

```text
Nimbus JOSE + JWT
java-jwt
jjwt
Spring Security OAuth2 Resource Server
```

Do not manually build JWT in production.

---

## Keep Access Token Short-Lived

Typical access token TTL:

```text
5 minutes
15 minutes
30 minutes
```

Long-lived access tokens are risky.

---

## Do Not Store Secrets In Payload

JWT payload is Base64Url encoded, not encrypted.

Bad:

```json
{
  "password": "secret",
  "creditCard": "..."
}
```

Good:

```json
{
  "sub": "user-id",
  "email": "user@example.com",
  "roles": ["USER"]
}
```

---

## Secret Management

Do not hardcode:

```text
JWT secret
```

Production should use:

```text
environment variables
vault
KMS
secret manager
key rotation
```

---

## HS256 vs RS256

HS256:

```text
same secret signs and verifies token
```

RS256:

```text
private key signs
public key verifies
```

For many microservices, RS256 is better because services can verify using public key only.

---

# 18. Common Bugs

## Bug 1 — Thinking JWT Is Encrypted

JWT payload can be decoded.

Never put sensitive secrets inside it.

---

## Bug 2 — No Expiry

Bad:

```text
JWT valid forever
```

Good:

```text
short exp claim
```

---

## Bug 3 — Weak Secret

Bad:

```text
secret = "abc"
```

Good:

```text
long random secret from secret manager
```

---

## Bug 4 — Logging Access Tokens

Avoid logging:

```text
Authorization header
access token
refresh token
```

---

# 19. Interview Notes

If interviewer asks:

```text
How does JWT login work?
```

Answer:

```text
1. User logs in with email and password.
2. Auth service verifies password.
3. Auth service creates claims.
4. Claims include sub, iat, exp, issuer.
5. Auth service signs token.
6. Client stores token.
7. Client sends Authorization: Bearer token.
8. Server verifies signature and expiry.
9. Server extracts user id from claims.
10. Short access token + refresh token is preferred.
```

Good comparison:

```text
Session:
server-side state, easy revocation

JWT:
stateless, scalable, harder revocation
```

---

# 20. Next Step

Next file:

```text
006_JWT_Token_Verification.md
```

In the next phase, we move from:

```text
generate JWT
```

to:

```text
verify JWT signature
parse claims
check expiry
authenticate request
```
