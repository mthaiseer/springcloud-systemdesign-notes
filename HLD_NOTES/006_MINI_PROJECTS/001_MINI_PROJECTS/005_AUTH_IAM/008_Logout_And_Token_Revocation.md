# MiniAuth / IAM Phase 8 — Logout And Token Revocation

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
  - [9.2 RefreshToken.java](#92-refreshtokenjava)
  - [9.3 RevokedAccessToken.java](#93-revokedaccesstokenjava)
  - [9.4 LogoutRequest.java](#94-logoutrequestjava)
  - [9.5 LogoutResponse.java](#95-logoutresponsejava)
  - [9.6 RefreshTokenStore.java](#96-refreshtokenstorejava)
  - [9.7 RevokedTokenStore.java](#97-revokedtokenstorejava)
  - [9.8 RefreshTokenService.java](#98-refreshtokenservicejava)
  - [9.9 AccessTokenRevocationService.java](#99-accesstokenrevocationservicejava)
  - [9.10 LogoutService.java](#910-logoutservicejava)
  - [9.11 Phase008Driver.java](#911-phase008driverjava)
- [10. How To Run](#10-how-to-run)
- [11. Dry Run](#11-dry-run)
- [12. What Changed From Phase 7](#12-what-changed-from-phase-7)
- [13. What This Phase Does NOT Do Yet](#13-what-this-phase-does-not-do-yet)
- [14. DSA / CP Concepts Used](#14-dsa--cp-concepts-used)
- [15. System Design Relevance](#15-system-design-relevance)
- [16. Production-Grade Concepts](#16-production-grade-concepts)
- [17. Common Bugs](#17-common-bugs)
- [18. Interview Notes](#18-interview-notes)
- [19. Next Step](#19-next-step)

---

# 1. Goal

In this phase, we add:

```text
logout
refresh token revocation
access token invalidation strategy
```

Before this phase:

```text
refresh token exists until expiry
access token remains valid until expiry
```

After this phase:

```text
logout revokes refresh token
logout can invalidate access token
revoked tokens are tracked
```

Main objective:

```text
Safely terminate authentication state.
```

---

# 2. Why This Phase Matters

Without logout/revocation:

```text
stolen token stays usable
```

Even after user clicks logout.

A secure IAM system must support:

```text
manual logout
forced logout
token revocation
session invalidation
account compromise response
```

---

# 3. What We Built Previously

## Phase 5

```text
JWT access token generation
```

## Phase 6

```text
JWT verification
```

## Phase 7

```text
refresh token flow
rotation
refresh token storage
```

Phase 7 allowed long-lived refresh tokens.

Phase 8 adds:

```text
logout + revocation
```

---

# 4. Previous Limitation

In Phase 7:

```text
refresh token stays active until expiry
```

Problem:

```text
user logs out
but attacker with stolen refresh token can still refresh
```

Also:

```text
JWT access tokens are stateless
```

So logout cannot instantly invalidate them unless we add revocation strategy.

---

# 5. What We Build

We add:

```text
LogoutRequest
LogoutResponse
RevokedAccessToken
RevokedTokenStore
AccessTokenRevocationService
LogoutService
```

Logout flow:

```text
client sends:
- refresh token
- access token

system:
- revoke refresh token
- blacklist access token
- clear auth state
```

---

# 6. Current Architecture

```text
+-------------------------+
| Logout Request          |
| access token            |
| refresh token           |
+------------+------------+
             |
             v
+-------------------------+
| LogoutService           |
| revoke refresh token    |
| revoke access token     |
+------------+------------+
             |
             v
+-------------------------+
| RefreshTokenStore       |
| refreshToken -> state   |
+------------+------------+
             |
             v
+-------------------------+
| RevokedTokenStore       |
| accessToken -> revoked  |
+-------------------------+
```

Protected request:

```text
Authorization: Bearer token
        |
        v
check revoked token blacklist
        |
        v
reject if revoked
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
                    │   ├── RefreshToken.java
                    │   └── RevokedAccessToken.java
                    ├── dto/
                    │   ├── LogoutRequest.java
                    │   └── LogoutResponse.java
                    ├── refresh/
                    │   ├── RefreshTokenStore.java
                    │   └── RefreshTokenService.java
                    ├── revoke/
                    │   ├── RevokedTokenStore.java
                    │   └── AccessTokenRevocationService.java
                    ├── service/
                    │   └── LogoutService.java
                    └── driver/
                        └── Phase008Driver.java
```

---

# 8. Core Concepts

## 8.1 Logout

Logout means:

```text
invalidate authentication state
```

This may involve:

```text
delete session
revoke refresh token
invalidate access token
clear cookies
```

---

## 8.2 Refresh Token Revocation

Refresh token is stateful.

That means server can mark it:

```text
revoked = true
```

Then refresh endpoint rejects it.

---

## 8.3 Access Token Blacklist

JWT access token is stateless.

Normally:

```text
valid until exp
```

To invalidate early:

```text
store revoked token ids
```

This creates a blacklist.

---

## 8.4 Tradeoff

Stateless JWT advantage:

```text
no DB lookup
```

Blacklist disadvantage:

```text
requires lookup
```

So systems choose tradeoffs carefully.

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

## 9.2 `RefreshToken.java`

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

## 9.3 `RevokedAccessToken.java`

```java
package com.miniauth.model;

import java.time.Instant;

public class RevokedAccessToken {

    private final String token;
    private final Instant revokedAt;
    private final Instant expiresAt;

    public RevokedAccessToken(
            String token,
            Instant revokedAt,
            Instant expiresAt
    ) {
        this.token = token;
        this.revokedAt = revokedAt;
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
```

---

## 9.4 `LogoutRequest.java`

```java
package com.miniauth.dto;

public class LogoutRequest {

    private final String accessToken;
    private final String refreshToken;

    public LogoutRequest(
            String accessToken,
            String refreshToken
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
```

---

## 9.5 `LogoutResponse.java`

```java
package com.miniauth.dto;

public class LogoutResponse {

    private final boolean success;
    private final String message;

    public LogoutResponse(
            boolean success,
            String message
    ) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "LogoutResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
```

---

## 9.6 `RefreshTokenStore.java`

```java
package com.miniauth.refresh;

import com.miniauth.model.RefreshToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RefreshTokenStore {

    private final Map<String, RefreshToken> tokens = new HashMap<>();

    public void save(RefreshToken token) {
        tokens.put(token.getToken(), token);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return Optional.ofNullable(tokens.get(token));
    }

    public void delete(String token) {
        tokens.remove(token);
    }

    public int size() {
        return tokens.size();
    }
}
```

---

## 9.7 `RevokedTokenStore.java`

```java
package com.miniauth.revoke;

import com.miniauth.model.RevokedAccessToken;

import java.util.HashMap;
import java.util.Map;

public class RevokedTokenStore {

    private final Map<String, RevokedAccessToken> revokedTokens =
            new HashMap<>();

    public void save(RevokedAccessToken token) {
        revokedTokens.put(token.getToken(), token);
    }

    public boolean isRevoked(String token) {

        RevokedAccessToken revoked =
                revokedTokens.get(token);

        if (revoked == null) {
            return false;
        }

        if (revoked.isExpired()) {
            revokedTokens.remove(token);
            return false;
        }

        return true;
    }

    public int size() {
        return revokedTokens.size();
    }
}
```

---

## 9.8 `RefreshTokenService.java`

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

    public RefreshTokenService(RefreshTokenStore store) {
        this.store = store;
    }

    public RefreshToken create(String userId) {

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        String token =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(bytes);

        RefreshToken refreshToken =
                new RefreshToken(
                        token,
                        userId,
                        Instant.now(),
                        Instant.now().plus(30, ChronoUnit.DAYS)
                );

        store.save(refreshToken);

        return refreshToken;
    }

    public Optional<RefreshToken> validate(String token) {

        Optional<RefreshToken> optional =
                store.findByToken(token);

        if (optional.isEmpty()) {
            return Optional.empty();
        }

        RefreshToken refreshToken = optional.get();

        if (!refreshToken.isUsable()) {
            store.delete(token);
            return Optional.empty();
        }

        return Optional.of(refreshToken);
    }

    public void revoke(String token) {

        Optional<RefreshToken> optional =
                store.findByToken(token);

        optional.ifPresent(refreshToken -> {
            refreshToken.revoke();
            store.delete(token);
        });
    }
}
```

---

## 9.9 `AccessTokenRevocationService.java`

```java
package com.miniauth.revoke;

import com.miniauth.model.RevokedAccessToken;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class AccessTokenRevocationService {

    private final RevokedTokenStore revokedTokenStore;

    public AccessTokenRevocationService(
            RevokedTokenStore revokedTokenStore
    ) {
        this.revokedTokenStore = revokedTokenStore;
    }

    public void revoke(String accessToken) {

        if (accessToken == null ||
                accessToken.isBlank()) {
            return;
        }

        RevokedAccessToken revokedToken =
                new RevokedAccessToken(
                        accessToken,
                        Instant.now(),
                        Instant.now().plus(15, ChronoUnit.MINUTES)
                );

        revokedTokenStore.save(revokedToken);
    }

    public boolean isRevoked(String accessToken) {
        return revokedTokenStore.isRevoked(accessToken);
    }
}
```

---

## 9.10 `LogoutService.java`

```java
package com.miniauth.service;

import com.miniauth.dto.LogoutRequest;
import com.miniauth.dto.LogoutResponse;
import com.miniauth.refresh.RefreshTokenService;
import com.miniauth.revoke.AccessTokenRevocationService;

public class LogoutService {

    private final RefreshTokenService refreshTokenService;
    private final AccessTokenRevocationService
            accessTokenRevocationService;

    public LogoutService(
            RefreshTokenService refreshTokenService,
            AccessTokenRevocationService accessTokenRevocationService
    ) {
        this.refreshTokenService = refreshTokenService;
        this.accessTokenRevocationService =
                accessTokenRevocationService;
    }

    public LogoutResponse logout(LogoutRequest request) {

        if (request == null) {
            return new LogoutResponse(
                    false,
                    "Invalid logout request"
            );
        }

        if (request.getRefreshToken() != null) {
            refreshTokenService.revoke(
                    request.getRefreshToken()
            );
        }

        if (request.getAccessToken() != null) {
            accessTokenRevocationService.revoke(
                    request.getAccessToken()
            );
        }

        return new LogoutResponse(
                true,
                "Logout successful"
        );
    }
}
```

---

## 9.11 `Phase008Driver.java`

```java
package com.miniauth.driver;

import com.miniauth.dto.LogoutRequest;
import com.miniauth.dto.LogoutResponse;
import com.miniauth.model.RefreshToken;
import com.miniauth.refresh.RefreshTokenService;
import com.miniauth.refresh.RefreshTokenStore;
import com.miniauth.revoke.AccessTokenRevocationService;
import com.miniauth.revoke.RevokedTokenStore;
import com.miniauth.service.LogoutService;

public class Phase008Driver {

    public static void main(String[] args) {

        RefreshTokenStore refreshTokenStore =
                new RefreshTokenStore();

        RefreshTokenService refreshTokenService =
                new RefreshTokenService(refreshTokenStore);

        RevokedTokenStore revokedTokenStore =
                new RevokedTokenStore();

        AccessTokenRevocationService
                accessTokenRevocationService =
                new AccessTokenRevocationService(
                        revokedTokenStore
                );

        LogoutService logoutService =
                new LogoutService(
                        refreshTokenService,
                        accessTokenRevocationService
                );

        String accessToken =
                "jwt-access-token-example";

        RefreshToken refreshToken =
                refreshTokenService.create("user-123");

        System.out.println("Refresh token created:");
        System.out.println(refreshToken.getToken());

        System.out.println();
        System.out.println("Refresh token usable?");
        System.out.println(
                refreshTokenService
                        .validate(refreshToken.getToken())
                        .isPresent()
        );

        LogoutResponse logoutResponse =
                logoutService.logout(
                        new LogoutRequest(
                                accessToken,
                                refreshToken.getToken()
                        )
                );

        System.out.println();
        System.out.println("Logout response:");
        System.out.println(logoutResponse);

        System.out.println();
        System.out.println("Refresh token usable after logout?");
        System.out.println(
                refreshTokenService
                        .validate(refreshToken.getToken())
                        .isPresent()
        );

        System.out.println();
        System.out.println("Access token revoked?");
        System.out.println(
                accessTokenRevocationService
                        .isRevoked(accessToken)
        );

        System.out.println();
        System.out.println("Refresh token store size:");
        System.out.println(refreshTokenStore.size());

        System.out.println();
        System.out.println("Revoked access token store size:");
        System.out.println(revokedTokenStore.size());
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

3. Add all classes.
4. Run:

```text
Phase008Driver
```

---

## Command Line

From project root:

```bash
javac -d out src/main/java/com/miniauth/**/*.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase008Driver
```

---

# 11. Dry Run

Initial state:

```text
refresh token active
access token active
```

Logout request:

```text
accessToken = jwt-token
refreshToken = refresh-token
```

Steps:

```text
1. LogoutService receives request.
2. RefreshTokenService revokes refresh token.
3. Refresh token removed from store.
4. AccessTokenRevocationService blacklists access token.
5. Logout successful response returned.
```

After logout:

```text
refresh token invalid
access token blacklisted
```

Protected API flow:

```text
incoming access token
    -> check blacklist
    -> reject if revoked
```

---

# 12. What Changed From Phase 7

## Phase 7

```text
refresh token remains valid until expiry
```

## Phase 8

```text
logout can revoke tokens immediately
```

New classes:

```text
LogoutService
RevokedTokenStore
AccessTokenRevocationService
RevokedAccessToken
```

New capabilities:

```text
logout
refresh token revocation
access token blacklist
```

---

# 13. What This Phase Does NOT Do Yet

This phase does not yet include:

```text
distributed blacklist
Redis revocation cache
token family invalidation
global logout across devices
device session management
reuse attack detection
event-driven revocation
Kafka propagation
admin forced logout
```

These come later.

---

# 14. DSA / CP Concepts Used

## HashMap Lookup

Blacklist lookup:

```text
token -> revoked state
```

Complexity:

```text
O(1)
```

---

## TTL Cleanup

Revoked tokens expire automatically.

Logic:

```text
if revoked token expired:
    remove from blacklist
```

---

## State Transitions

Refresh token lifecycle:

```text
ACTIVE -> REVOKED
ACTIVE -> EXPIRED
```

---

## Memory Cleanup

Expired revoked tokens are lazily removed during lookup.

This is similar to:

```text
cache eviction
lazy cleanup
```

---

# 15. System Design Relevance

Logout/revocation is critical for:

```text
banking
enterprise IAM
SSO
admin systems
mobile auth
multi-device auth
```

Production architecture:

```text
Client
  -> API Gateway
  -> Revocation cache
  -> Auth Service
```

At scale:

```text
Redis blacklist
Kafka revocation events
global cache invalidation
device-aware sessions
```

---

# 16. Production-Grade Concepts

## Access Token Revocation Tradeoff

JWT is stateless.

Blacklist makes it partially stateful.

Tradeoff:

```text
stateless speed vs instant revocation
```

Many systems choose:

```text
very short access token TTL
```

instead of blacklist.

---

## Use Redis For Revocation

Production blacklist usually uses:

```text
Redis with TTL
```

Why?

```text
fast lookup
automatic expiry
distributed
```

---

## Global Logout

Production logout may revoke:

```text
all refresh tokens for user
all sessions for user
all devices
```

---

## Device Sessions

Refresh tokens should often track:

```text
device id
browser
IP
location
createdAt
lastUsedAt
```

---

## Event-Driven Revocation

At scale:

```text
logout -> Kafka event
```

Other services consume revocation event.

---

# 17. Common Bugs

## Bug 1 — Logout Only On Client

Bad:

```text
client deletes token locally only
```

Good:

```text
server revokes token too
```

---

## Bug 2 — Stateless JWT Without Short TTL

Problem:

```text
stolen token valid until expiry
```

Fix:

```text
short TTL
or blacklist
```

---

## Bug 3 — No Refresh Token Revocation

Problem:

```text
attacker can keep refreshing forever
```

Fix:

```text
revoke refresh token on logout
```

---

## Bug 4 — Blacklist Never Cleaned

Problem:

```text
memory grows forever
```

Fix:

```text
TTL cleanup
Redis expiry
scheduled cleanup
```

---

# 18. Interview Notes

If interviewer asks:

```text
How do you implement logout with JWT?
```

Answer:

```text
1. Revoke refresh token server-side.
2. Optionally blacklist access token.
3. Remove cookies/client storage.
4. Use short access token TTL.
5. Use Redis blacklist if immediate revocation required.
6. Expire revoked tokens automatically.
7. Support global logout across devices.
```

Strong follow-up:

```text
JWT is easy to issue but revocation is harder because it is stateless.
```

---

# 19. Next Step

Next file:

```text
009_RBAC_Role_Based_Access_Control.md
```

In the next phase, we add:

```text
roles
permissions
authorization
ADMIN / USER role checks
protected endpoints
```
