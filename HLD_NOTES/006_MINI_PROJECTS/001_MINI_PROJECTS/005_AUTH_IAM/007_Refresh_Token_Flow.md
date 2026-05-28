# MiniAuth / IAM Phase 7 — Refresh Token Flow

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
  - [9.6 RefreshRequest.java](#96-refreshrequestjava)
  - [9.7 RefreshResponse.java](#97-refreshresponsejava)
  - [9.8 RefreshToken.java](#98-refreshtokenjava)
  - [9.9 RefreshTokenStore.java](#99-refreshtokenstorejava)
  - [9.10 RefreshTokenService.java](#910-refreshtokenservicejava)
  - [9.11 JwtTokenService.java](#911-jwttokenservicejava)
  - [9.12 AuthenticationService.java](#912-authenticationservicejava)
  - [9.13 Phase007Driver.java](#913-phase007driverjava)
- [10. How To Run](#10-how-to-run)
- [11. Dry Run](#11-dry-run)
- [12. What Changed From Phase 6](#12-what-changed-from-phase-6)
- [13. What This Phase Does NOT Do Yet](#13-what-this-phase-does-not-do-yet)
- [14. DSA / CP Concepts Used](#14-dsa--cp-concepts-used)
- [15. System Design Relevance](#15-system-design-relevance)
- [16. Production-Grade Concepts](#16-production-grade-concepts)
- [17. Common Bugs](#17-common-bugs)
- [18. Interview Notes](#18-interview-notes)
- [19. Next Step](#19-next-step)

---

# 1. Goal

In this phase, we add refresh token flow.

Before this phase:

```text
login -> access token
```

After this phase:

```text
login -> access token + refresh token

access token expires
refresh token -> new access token
```

Main objective:

```text
Use short-lived access tokens and long-lived refresh tokens.
```

---

# 2. Why This Phase Matters

Access tokens should be short-lived.

Example:

```text
access token TTL = 15 minutes
```

But users should not login every 15 minutes.

So we issue a refresh token.

Refresh token allows the client to get a new access token without sending password again.

This improves both:

```text
security
user experience
```

---

# 3. What We Built Previously

## Phase 5

```text
JWT access token generation
```

## Phase 6

```text
JWT access token verification
```

Phase 6 allowed protected APIs to verify:

```text
Authorization: Bearer <access-token>
```

Now Phase 7 adds:

```text
refresh token
```

---

# 4. Previous Limitation

With only access token:

```text
token expires -> user must login again
```

If we make access token very long-lived:

```text
stolen token remains useful for long time
```

Better design:

```text
short-lived access token
longer-lived refresh token
```

---

# 5. What We Build

We add:

```text
RefreshToken
RefreshTokenStore
RefreshTokenService
RefreshRequest
RefreshResponse
```

Login flow:

```text
email + password
    -> access token
    -> refresh token
```

Refresh flow:

```text
refresh token
    -> validate token
    -> rotate refresh token
    -> issue new access token
    -> issue new refresh token
```

---

# 6. Current Architecture

```text
+-------------------------+
| Login Request           |
+------------+------------+
             |
             v
+-------------------------+
| AuthenticationService   |
| verify credentials      |
+------------+------------+
             |
             v
+-------------------------+
| JwtTokenService         |
| create access token     |
+------------+------------+
             |
             v
+-------------------------+
| RefreshTokenService     |
| create refresh token    |
| rotate refresh token    |
+------------+------------+
             |
             v
+-------------------------+
| RefreshTokenStore       |
| token -> RefreshToken   |
+-------------------------+
```

Refresh request:

```text
Client refresh token
        |
        v
RefreshTokenService
        |
        v
validate + rotate
        |
        v
new access token + new refresh token
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
                    │   ├── User.java
                    │   └── RefreshToken.java
                    ├── dto/
                    │   ├── LoginRequest.java
                    │   ├── LoginResponse.java
                    │   ├── RefreshRequest.java
                    │   └── RefreshResponse.java
                    ├── repository/
                    │   └── UserRepository.java
                    ├── security/
                    │   └── PasswordEncoder.java
                    ├── jwt/
                    │   └── JwtTokenService.java
                    ├── refresh/
                    │   ├── RefreshTokenStore.java
                    │   └── RefreshTokenService.java
                    ├── service/
                    │   └── AuthenticationService.java
                    └── driver/
                        └── Phase007Driver.java
```

---

# 8. Core Concepts

## 8.1 Access Token

Access token is used for protected APIs.

Usually:

```text
short-lived
sent often
stored carefully
verified by API gateway/resource server
```

Example:

```text
15 minutes
```

---

## 8.2 Refresh Token

Refresh token is used only to get new access tokens.

Usually:

```text
longer-lived
stored server-side
can be revoked
rotated after use
```

Example:

```text
7 days
30 days
```

---

## 8.3 Refresh Token Rotation

Refresh token rotation means:

```text
old refresh token is consumed
new refresh token is issued
```

This helps detect token theft.

If an old refresh token is reused:

```text
possible replay attack
```

---

## 8.4 Server-Side Refresh Token Store

Unlike access tokens, refresh tokens should usually be stored server-side.

Mapping:

```text
refreshTokenValue -> RefreshToken record
```

In this phase:

```text
HashMap
```

Production:

```text
PostgreSQL / Redis
```

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
    private final Map<String, User> usersById = new HashMap<>();

    public void save(User user) {
        usersByEmail.put(user.getEmail(), user);
        usersById.put(user.getId(), user);
    }

    public Optional<User> findByEmail(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        return Optional.ofNullable(usersByEmail.get(normalizedEmail));
    }

    public Optional<User> findById(String userId) {
        return Optional.ofNullable(usersById.get(userId));
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
    private final String refreshToken;
    private final long accessTokenExpiresInSeconds;
    private final String message;

    public LoginResponse(
            boolean authenticated,
            String userId,
            String email,
            String accessToken,
            String refreshToken,
            long accessTokenExpiresInSeconds,
            String message
    ) {
        this.authenticated = authenticated;
        this.userId = userId;
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresInSeconds = accessTokenExpiresInSeconds;
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

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getAccessTokenExpiresInSeconds() {
        return accessTokenExpiresInSeconds;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "authenticated=" + authenticated +
                ", userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", accessTokenExpiresInSeconds=" + accessTokenExpiresInSeconds +
                ", message='" + message + '\'' +
                '}';
    }
}
```

---

## 9.6 `RefreshRequest.java`

```java
package com.miniauth.dto;

public class RefreshRequest {

    private final String refreshToken;

    public RefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
```

---

## 9.7 `RefreshResponse.java`

```java
package com.miniauth.dto;

public class RefreshResponse {

    private final boolean success;
    private final String accessToken;
    private final String refreshToken;
    private final long accessTokenExpiresInSeconds;
    private final String message;

    public RefreshResponse(
            boolean success,
            String accessToken,
            String refreshToken,
            long accessTokenExpiresInSeconds,
            String message
    ) {
        this.success = success;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresInSeconds = accessTokenExpiresInSeconds;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getAccessTokenExpiresInSeconds() {
        return accessTokenExpiresInSeconds;
    }

    @Override
    public String toString() {
        return "RefreshResponse{" +
                "success=" + success +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", accessTokenExpiresInSeconds=" + accessTokenExpiresInSeconds +
                ", message='" + message + '\'' +
                '}';
    }
}
```

---

## 9.8 `RefreshToken.java`

```java
package com.miniauth.model;

import java.time.Instant;

public class RefreshToken {

    private final String token;
    private final String userId;
    private final Instant createdAt;
    private final Instant expiresAt;
    private boolean revoked;

    public RefreshToken(
            String token,
            String userId,
            Instant createdAt,
            Instant expiresAt
    ) {
        this.token = token;
        this.userId = userId;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.revoked = false;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void revoke() {
        this.revoked = true;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isUsable() {
        return !revoked && !isExpired();
    }
}
```

---

## 9.9 `RefreshTokenStore.java`

```java
package com.miniauth.refresh;

import com.miniauth.model.RefreshToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RefreshTokenStore {

    private final Map<String, RefreshToken> tokensByValue = new HashMap<>();

    public void save(RefreshToken refreshToken) {
        tokensByValue.put(refreshToken.getToken(), refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return Optional.ofNullable(tokensByValue.get(token));
    }

    public void delete(String token) {
        tokensByValue.remove(token);
    }

    public int size() {
        return tokensByValue.size();
    }
}
```

---

## 9.10 `RefreshTokenService.java`

```java
package com.miniauth.refresh;

import com.miniauth.model.RefreshToken;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;

public class RefreshTokenService {

    private final RefreshTokenStore store;
    private final SecureRandom random = new SecureRandom();
    private final long refreshTokenTtlDays;

    public RefreshTokenService(
            RefreshTokenStore store,
            long refreshTokenTtlDays
    ) {
        this.store = store;
        this.refreshTokenTtlDays = refreshTokenTtlDays;
    }

    public RefreshToken createRefreshToken(String userId) {

        String tokenValue = generateSecureToken();

        Instant createdAt = Instant.now();
        Instant expiresAt =
                createdAt.plus(refreshTokenTtlDays, ChronoUnit.DAYS);

        RefreshToken refreshToken =
                new RefreshToken(
                        tokenValue,
                        userId,
                        createdAt,
                        expiresAt
                );

        store.save(refreshToken);

        return refreshToken;
    }

    public Optional<RefreshToken> validate(String tokenValue) {

        if (tokenValue == null || tokenValue.isBlank()) {
            return Optional.empty();
        }

        Optional<RefreshToken> optional =
                store.findByToken(tokenValue);

        if (optional.isEmpty()) {
            return Optional.empty();
        }

        RefreshToken refreshToken = optional.get();

        if (!refreshToken.isUsable()) {
            store.delete(tokenValue);
            return Optional.empty();
        }

        return Optional.of(refreshToken);
    }

    public RefreshToken rotate(RefreshToken oldToken) {

        oldToken.revoke();
        store.delete(oldToken.getToken());

        return createRefreshToken(oldToken.getUserId());
    }

    private String generateSecureToken() {

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}
```

---

## 9.11 `JwtTokenService.java`

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

        String encodedHeader = base64UrlEncode(headerJson);
        String encodedPayload = base64UrlEncode(payloadJson);

        String unsignedToken = encodedHeader + "." + encodedPayload;
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

    private String base64UrlEncode(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
```

---

## 9.12 `AuthenticationService.java`

```java
package com.miniauth.service;

import com.miniauth.dto.LoginRequest;
import com.miniauth.dto.LoginResponse;
import com.miniauth.dto.RefreshRequest;
import com.miniauth.dto.RefreshResponse;
import com.miniauth.jwt.JwtTokenService;
import com.miniauth.model.RefreshToken;
import com.miniauth.model.User;
import com.miniauth.refresh.RefreshTokenService;
import com.miniauth.repository.UserRepository;
import com.miniauth.security.PasswordEncoder;

public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenService = refreshTokenService;
    }

    public LoginResponse login(LoginRequest request) {

        String normalizedEmail =
                request.getEmail().toLowerCase().trim();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElse(null);

        if (user == null) {
            return failedLoginResponse();
        }

        boolean passwordMatched =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPasswordHash()
                );

        if (!passwordMatched) {
            return failedLoginResponse();
        }

        String accessToken =
                jwtTokenService.generateAccessToken(user);

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user.getId());

        return new LoginResponse(
                true,
                user.getId(),
                user.getEmail(),
                accessToken,
                refreshToken.getToken(),
                jwtTokenService.getAccessTokenTtlSeconds(),
                "Login successful"
        );
    }

    public RefreshResponse refresh(RefreshRequest request) {

        if (request == null ||
                request.getRefreshToken() == null ||
                request.getRefreshToken().isBlank()) {
            return failedRefreshResponse();
        }

        RefreshToken oldRefreshToken =
                refreshTokenService
                        .validate(request.getRefreshToken())
                        .orElse(null);

        if (oldRefreshToken == null) {
            return failedRefreshResponse();
        }

        User user =
                userRepository
                        .findById(oldRefreshToken.getUserId())
                        .orElse(null);

        if (user == null) {
            return failedRefreshResponse();
        }

        RefreshToken newRefreshToken =
                refreshTokenService.rotate(oldRefreshToken);

        String newAccessToken =
                jwtTokenService.generateAccessToken(user);

        return new RefreshResponse(
                true,
                newAccessToken,
                newRefreshToken.getToken(),
                jwtTokenService.getAccessTokenTtlSeconds(),
                "Token refreshed successfully"
        );
    }

    private LoginResponse failedLoginResponse() {
        return new LoginResponse(
                false,
                null,
                null,
                null,
                null,
                0,
                "Invalid email or password"
        );
    }

    private RefreshResponse failedRefreshResponse() {
        return new RefreshResponse(
                false,
                null,
                null,
                0,
                "Invalid refresh token"
        );
    }
}
```

---

## 9.13 `Phase007Driver.java`

```java
package com.miniauth.driver;

import com.miniauth.dto.LoginRequest;
import com.miniauth.dto.LoginResponse;
import com.miniauth.dto.RefreshRequest;
import com.miniauth.dto.RefreshResponse;
import com.miniauth.jwt.JwtTokenService;
import com.miniauth.model.User;
import com.miniauth.refresh.RefreshTokenService;
import com.miniauth.refresh.RefreshTokenStore;
import com.miniauth.repository.UserRepository;
import com.miniauth.security.PasswordEncoder;
import com.miniauth.service.AuthenticationService;

public class Phase007Driver {

    public static void main(String[] args) {

        UserRepository userRepository = new UserRepository();
        PasswordEncoder passwordEncoder = new PasswordEncoder();

        JwtTokenService jwtTokenService =
                new JwtTokenService(
                        "MiniAuth",
                        "change-this-secret-in-production",
                        15 * 60
                );

        RefreshTokenStore refreshTokenStore =
                new RefreshTokenStore();

        RefreshTokenService refreshTokenService =
                new RefreshTokenService(
                        refreshTokenStore,
                        30
                );

        String passwordHash =
                passwordEncoder.hash("StrongPass123");

        User user = new User(
                "mohamed@example.com",
                passwordHash
        );

        userRepository.save(user);

        AuthenticationService authService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        jwtTokenService,
                        refreshTokenService
                );

        LoginResponse loginResponse =
                authService.login(
                        new LoginRequest(
                                "mohamed@example.com",
                                "StrongPass123"
                        )
                );

        System.out.println("Login response:");
        System.out.println(loginResponse);

        RefreshResponse refreshResponse =
                authService.refresh(
                        new RefreshRequest(
                                loginResponse.getRefreshToken()
                        )
                );

        System.out.println();
        System.out.println("Refresh response:");
        System.out.println(refreshResponse);

        RefreshResponse replayOldToken =
                authService.refresh(
                        new RefreshRequest(
                                loginResponse.getRefreshToken()
                        )
                );

        System.out.println();
        System.out.println("Replay old refresh token:");
        System.out.println(replayOldToken);

        System.out.println();
        System.out.println("Refresh token store size:");
        System.out.println(refreshTokenStore.size());
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
Phase007Driver
```

---

## Command Line

From project root:

```bash
javac -d out src/main/java/com/miniauth/**/*.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase007Driver
```

Windows CMD example:

```cmd
javac -d out ^
src/main/java/com/miniauth/model/User.java ^
src/main/java/com/miniauth/model/RefreshToken.java ^
src/main/java/com/miniauth/dto/LoginRequest.java ^
src/main/java/com/miniauth/dto/LoginResponse.java ^
src/main/java/com/miniauth/dto/RefreshRequest.java ^
src/main/java/com/miniauth/dto/RefreshResponse.java ^
src/main/java/com/miniauth/repository/UserRepository.java ^
src/main/java/com/miniauth/security/PasswordEncoder.java ^
src/main/java/com/miniauth/jwt/JwtTokenService.java ^
src/main/java/com/miniauth/refresh/RefreshTokenStore.java ^
src/main/java/com/miniauth/refresh/RefreshTokenService.java ^
src/main/java/com/miniauth/service/AuthenticationService.java ^
src/main/java/com/miniauth/driver/Phase007Driver.java
```

Run:

```cmd
java -cp out com.miniauth.driver.Phase007Driver
```

---

# 11. Dry Run

Login input:

```text
email = mohamed@example.com
password = StrongPass123
```

Login steps:

```text
1. User logs in.
2. Password is verified.
3. Access token is generated.
4. Refresh token is generated.
5. Refresh token is stored server-side.
6. Login response returns both tokens.
```

Refresh input:

```text
refreshToken = old-refresh-token
```

Refresh steps:

```text
1. Client sends refresh token.
2. RefreshTokenService checks token exists.
3. It checks token is not expired.
4. It checks token is not revoked.
5. User is loaded by userId.
6. Old refresh token is revoked/deleted.
7. New refresh token is created.
8. New access token is generated.
9. Refresh response returns new tokens.
```

Replay attack:

```text
old refresh token used again
```

Result:

```text
invalid refresh token
```

Because the old token was rotated.

---

# 12. What Changed From Phase 6

## Phase 6

```text
JWT access token can be verified
```

## Phase 7

```text
expired access token can be renewed using refresh token
```

New classes:

```text
RefreshToken
RefreshTokenStore
RefreshTokenService
RefreshRequest
RefreshResponse
```

New auth operation:

```text
refresh(refreshToken)
```

---

# 13. What This Phase Does NOT Do Yet

This phase does not yet include:

```text
logout revocation for refresh token family
device-level sessions
refresh token reuse detection with family invalidation
hashed refresh token storage
secure HttpOnly cookies
CSRF protection
IP/device fingerprinting
audit logs
rate limiting refresh endpoint
```

These come later.

---

# 14. DSA / CP Concepts Used

## HashMap

Refresh token store uses:

```text
refreshToken -> RefreshToken
```

Operations:

```text
save   -> O(1)
lookup -> O(1)
delete -> O(1)
```

---

## Rotation State Machine

Refresh token lifecycle:

```text
ACTIVE -> USED/REVOKED -> INVALID
ACTIVE -> EXPIRED -> INVALID
```

---

## Random Token Generation

We generate 32 random bytes.

This creates a high-entropy token.

---

## Replay Detection

If an old token is used after rotation:

```text
store lookup fails
```

This models replay protection.

---

# 15. System Design Relevance

Refresh token flow is used in:

```text
OAuth2
OIDC
mobile apps
SPA auth
enterprise IAM
banking apps
SaaS apps
```

HLD:

```text
Client
  -> Auth Service
  -> Access Token
  -> Refresh Token Store
  -> User DB
```

At scale:

```text
Auth Service replicas
Redis/PostgreSQL refresh token store
JWT access token verification at gateway
Kafka audit events
risk engine for suspicious refresh
```

---

# 16. Production-Grade Concepts

## Store Refresh Token Securely

Production should store refresh token in:

```text
HttpOnly Secure SameSite cookie
```

For mobile:

```text
secure enclave / keystore
```

---

## Store Hash Of Refresh Token

Do not store raw refresh token in DB.

Better:

```text
hash(refreshToken) -> DB
```

Same idea as password storage.

---

## Rotate Refresh Token

Always prefer:

```text
refresh token rotation
```

Instead of reusing same refresh token forever.

---

## Detect Reuse

If an already-used refresh token appears:

```text
possible theft
```

Production should revoke entire token family.

---

## Separate Access And Refresh TTL

Example:

```text
access token  = 15 minutes
refresh token = 30 days
```

---

# 17. Common Bugs

## Bug 1 — Refresh Token Never Expires

Bad:

```text
refresh token valid forever
```

Good:

```text
expiresAt
```

---

## Bug 2 — No Rotation

Bad:

```text
same refresh token reused forever
```

Good:

```text
old token invalidated, new token issued
```

---

## Bug 3 — Store Raw Refresh Token

Better production approach:

```text
store hash of refresh token
```

---

## Bug 4 — Refresh Token In LocalStorage

Risk:

```text
XSS can steal token
```

Better for web:

```text
HttpOnly Secure cookie
```

---

# 18. Interview Notes

If interviewer asks:

```text
How do access and refresh tokens work?
```

Answer:

```text
1. User logs in with credentials.
2. Auth service issues short-lived access token.
3. Auth service issues long-lived refresh token.
4. Access token is used for APIs.
5. Refresh token is used only at refresh endpoint.
6. Refresh token is stored server-side.
7. On refresh, validate token.
8. Rotate refresh token.
9. Issue new access token.
10. Reuse detection can revoke token family.
```

Strong follow-up:

```text
Access token can be stateless.
Refresh token should be stateful and revocable.
```

---

# 19. Next Step

Next file:

```text
008_Logout_And_Token_Revocation.md
```

In the next phase, we add:

```text
logout
refresh token revocation
access token blacklist idea
token invalidation strategy
```
