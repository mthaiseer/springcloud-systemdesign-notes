# MiniAuth / IAM Phase 4 — Session Management

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
  - [9.6 Session.java](#96-sessionjava)
  - [9.7 SessionStore.java](#97-sessionstorejava)
  - [9.8 SessionService.java](#98-sessionservicejava)
  - [9.9 AuthenticationService.java](#99-authenticationservicejava)
  - [9.10 Phase004Driver.java](#910-phase004driverjava)
- [10. How To Run](#10-how-to-run)
- [11. Dry Run](#11-dry-run)
- [12. What Changed From Phase 3](#12-what-changed-from-phase-3)
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
login success boolean
```

to:

```text
login success + server-side session
```

Before this phase:

```text
email + password -> authenticated true/false
```

After this phase:

```text
email + password -> sessionId
sessionId -> authenticated user
```

Main objective:

```text
Create and validate server-side sessions.
```

---

# 2. Why This Phase Matters

After login succeeds, the user should not send email and password again for every request.

Instead, the system creates a session.

The client sends:

```text
sessionId
```

on future requests.

The server checks:

```text
Is this session valid?
Who does this session belong to?
Has this session expired?
```

This is how traditional web authentication works.

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

Phase 3 answered:

```text
Is the password correct?
```

Phase 4 answers:

```text
How does the system remember the logged-in user?
```

---

# 4. Previous Limitation

Phase 3 returned only:

```text
authenticated = true
```

Problem:

```text
After login, the system had no memory of user state.
```

Every protected request would need login again.

That is not practical.

So Phase 4 introduces:

```text
Session Management
```

---

# 5. What We Build

We add:

```text
Session
SessionStore
SessionService
```

New login flow:

```text
email + password
    -> verify credentials
    -> create session
    -> return sessionId
```

New protected request flow:

```text
sessionId
    -> SessionService.validate()
    -> userId
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
| - verify password           |
| - create session            |
+--------------+--------------+
               |
               v
+-----------------------------+
| SessionService              |
| - create session            |
| - validate session          |
| - expire session            |
+--------------+--------------+
               |
               v
+-----------------------------+
| SessionStore                |
| sessionId -> Session        |
+--------------+--------------+
               |
               v
+-----------------------------+
| LoginResponse               |
| - authenticated             |
| - userId                    |
| - sessionId                 |
+-----------------------------+
```

Protected request:

```text
Client sends sessionId
        |
        v
SessionService.validate(sessionId)
        |
        v
User is authenticated if session is valid
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
                    │   └── Session.java
                    ├── dto/
                    │   ├── LoginRequest.java
                    │   └── LoginResponse.java
                    ├── repository/
                    │   └── UserRepository.java
                    ├── security/
                    │   └── PasswordEncoder.java
                    ├── session/
                    │   ├── SessionStore.java
                    │   └── SessionService.java
                    ├── service/
                    │   └── AuthenticationService.java
                    └── driver/
                        └── Phase004Driver.java
```

---

# 8. Core Concepts

## 8.1 Session

A session represents a logged-in user.

Minimal session fields:

```text
sessionId
userId
createdAt
expiresAt
```

---

## 8.2 Session ID

A session ID is a random token.

Example:

```text
6a7f2a2e-f4b6-4e1e-9bd2-7a86c08a9180
```

The client stores it in:

```text
cookie
header
mobile secure storage
```

In this mini project, we just print it.

---

## 8.3 Session Store

A session store maps:

```text
sessionId -> Session
```

In this mini phase:

```text
HashMap
```

Production:

```text
Redis
database
distributed cache
```

---

## 8.4 Session Expiry

Every session should expire.

Example:

```text
createdAt = now
expiresAt = now + 30 minutes
```

If current time is after expiresAt:

```text
session is invalid
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

### Logic before this class

Phase 3 response returned login status.

Phase 4 response adds:

```text
sessionId
```

```java
package com.miniauth.dto;

public class LoginResponse {

    private final boolean authenticated;
    private final String userId;
    private final String email;
    private final String sessionId;
    private final String message;

    public LoginResponse(
            boolean authenticated,
            String userId,
            String email,
            String sessionId,
            String message
    ) {
        this.authenticated = authenticated;
        this.userId = userId;
        this.email = email;
        this.sessionId = sessionId;
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

    public String getSessionId() {
        return sessionId;
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
                ", sessionId='" + sessionId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
```

---

## 9.6 `Session.java`

### Logic before this class

This model represents one active login session.

```java
package com.miniauth.model;

import java.time.Instant;

public class Session {

    private final String sessionId;
    private final String userId;
    private final Instant createdAt;
    private final Instant expiresAt;

    public Session(
            String sessionId,
            String userId,
            Instant createdAt,
            Instant expiresAt
    ) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public String getSessionId() {
        return sessionId;
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

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    @Override
    public String toString() {
        return "Session{" +
                "sessionId='" + sessionId + '\'' +
                ", userId='" + userId + '\'' +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
```

---

## 9.7 `SessionStore.java`

### Logic before this class

This is the in-memory session database.

It maps:

```text
sessionId -> Session
```

```java
package com.miniauth.session;

import com.miniauth.model.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionStore {

    private final Map<String, Session> sessionsById = new HashMap<>();

    public void save(Session session) {
        sessionsById.put(session.getSessionId(), session);
    }

    public Optional<Session> findById(String sessionId) {
        return Optional.ofNullable(sessionsById.get(sessionId));
    }

    public void delete(String sessionId) {
        sessionsById.remove(sessionId);
    }

    public int size() {
        return sessionsById.size();
    }
}
```

---

## 9.8 `SessionService.java`

### Logic before this class

This service owns session lifecycle.

Responsibilities:

```text
1. Create session after successful login.
2. Validate session on future requests.
3. Delete expired sessions.
```

```java
package com.miniauth.session;

import com.miniauth.model.Session;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

public class SessionService {

    private final SessionStore sessionStore;

    public SessionService(SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    public Session createSession(String userId) {

        String sessionId = UUID.randomUUID().toString();

        Instant createdAt = Instant.now();
        Instant expiresAt = createdAt.plus(30, ChronoUnit.MINUTES);

        Session session = new Session(
                sessionId,
                userId,
                createdAt,
                expiresAt
        );

        sessionStore.save(session);

        return session;
    }

    public Optional<Session> validateSession(String sessionId) {

        if (sessionId == null || sessionId.isBlank()) {
            return Optional.empty();
        }

        Optional<Session> optionalSession =
                sessionStore.findById(sessionId);

        if (optionalSession.isEmpty()) {
            return Optional.empty();
        }

        Session session = optionalSession.get();

        if (session.isExpired()) {
            sessionStore.delete(sessionId);
            return Optional.empty();
        }

        return Optional.of(session);
    }

    public void logout(String sessionId) {
        sessionStore.delete(sessionId);
    }
}
```

---

## 9.9 `AuthenticationService.java`

### Logic before this class

This phase changes authentication service.

Phase 3:

```text
password match -> LoginResponse(true)
```

Phase 4:

```text
password match -> create session -> LoginResponse(true, sessionId)
```

```java
package com.miniauth.service;

import com.miniauth.dto.LoginRequest;
import com.miniauth.dto.LoginResponse;
import com.miniauth.model.Session;
import com.miniauth.model.User;
import com.miniauth.repository.UserRepository;
import com.miniauth.security.PasswordEncoder;
import com.miniauth.session.SessionService;

public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;

    public AuthenticationService(
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            SessionService sessionService
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.sessionService = sessionService;
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

        Session session =
                sessionService.createSession(user.getId());

        return new LoginResponse(
                true,
                user.getId(),
                user.getEmail(),
                session.getSessionId(),
                "Login successful"
        );
    }

    private LoginResponse failedResponse() {
        return new LoginResponse(
                false,
                null,
                null,
                null,
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

## 9.10 `Phase004Driver.java`

### Logic before this class

This driver proves:

```text
1. Login creates session.
2. Session ID can be validated.
3. Logout deletes session.
4. Deleted session becomes invalid.
```

```java
package com.miniauth.driver;

import com.miniauth.dto.LoginRequest;
import com.miniauth.dto.LoginResponse;
import com.miniauth.model.Session;
import com.miniauth.model.User;
import com.miniauth.repository.UserRepository;
import com.miniauth.security.PasswordEncoder;
import com.miniauth.service.AuthenticationService;
import com.miniauth.session.SessionService;
import com.miniauth.session.SessionStore;

import java.util.Optional;

public class Phase004Driver {

    public static void main(String[] args) {

        UserRepository userRepository = new UserRepository();
        PasswordEncoder passwordEncoder = new PasswordEncoder();

        SessionStore sessionStore = new SessionStore();
        SessionService sessionService =
                new SessionService(sessionStore);

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
                        sessionService
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

        String sessionId = loginResponse.getSessionId();

        Optional<Session> validSession =
                sessionService.validateSession(sessionId);

        System.out.println();
        System.out.println("Session valid?");
        System.out.println(validSession.isPresent());

        validSession.ifPresent(session ->
                System.out.println("Session belongs to userId: " +
                        session.getUserId())
        );

        sessionService.logout(sessionId);

        Optional<Session> afterLogout =
                sessionService.validateSession(sessionId);

        System.out.println();
        System.out.println("Session valid after logout?");
        System.out.println(afterLogout.isPresent());

        System.out.println();
        System.out.println("Session store size:");
        System.out.println(sessionStore.size());
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
Phase004Driver
```

---

## Command Line

From project root:

```bash
javac -d out src/main/java/com/miniauth/**/*.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase004Driver
```

Windows CMD example:

```cmd
javac -d out ^
src/main/java/com/miniauth/model/User.java ^
src/main/java/com/miniauth/model/Session.java ^
src/main/java/com/miniauth/dto/LoginRequest.java ^
src/main/java/com/miniauth/dto/LoginResponse.java ^
src/main/java/com/miniauth/repository/UserRepository.java ^
src/main/java/com/miniauth/security/PasswordEncoder.java ^
src/main/java/com/miniauth/session/SessionStore.java ^
src/main/java/com/miniauth/session/SessionService.java ^
src/main/java/com/miniauth/service/AuthenticationService.java ^
src/main/java/com/miniauth/driver/Phase004Driver.java
```

Run:

```cmd
java -cp out com.miniauth.driver.Phase004Driver
```

---

# 11. Dry Run

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
1. Login request reaches AuthenticationService.
2. Email and password are validated.
3. User is found by email.
4. PasswordEncoder verifies password.
5. Password is correct.
6. SessionService creates new sessionId.
7. Session is saved in SessionStore.
8. LoginResponse returns sessionId.
```

Session store:

```text
sessionsById
{
  "session-uuid" -> Session(
      sessionId = session-uuid,
      userId = user-id,
      createdAt = now,
      expiresAt = now + 30 minutes
  )
}
```

Protected request:

```text
client sends sessionId
```

Validation:

```text
1. SessionService receives sessionId.
2. Looks up session in HashMap.
3. Checks expiry.
4. If not expired, returns session.
```

Logout:

```text
sessionStore.remove(sessionId)
```

After logout:

```text
same sessionId no longer works
```

---

# 12. What Changed From Phase 3

## Phase 3

```text
email + password -> authenticated true/false
```

## Phase 4

```text
email + password -> sessionId
sessionId -> authenticated user
```

New classes:

```text
Session
SessionStore
SessionService
```

Updated class:

```text
LoginResponse now contains sessionId
AuthenticationService creates session after successful login
```

---

# 13. What This Phase Does NOT Do Yet

This phase does not yet include:

```text
JWT
refresh token
secure cookie flags
CSRF protection
distributed Redis session store
session cleanup scheduler
device tracking
session rotation
RBAC
permissions
MFA
OAuth2
OIDC
audit logs
```

These come in later phases.

---

# 14. DSA / CP Concepts Used

## HashMap

Session storage uses:

```java
Map<String, Session>
```

Mapping:

```text
sessionId -> Session
```

Operations:

```text
create session -> O(1)
find session   -> O(1)
delete session -> O(1)
```

---

## TTL / Expiry

Session validity depends on time.

Conceptually:

```text
if now > expiresAt:
    invalid
else:
    valid
```

This is similar to cache TTL systems like Redis.

---

## UUID / Random ID

Session ID must be unpredictable.

```java
UUID.randomUUID()
```

In production, use cryptographically strong tokens.

---

# 15. System Design Relevance

Session management appears in many systems:

```text
web login
admin dashboard
banking portal
SaaS app
e-commerce checkout
internal tools
```

HLD view:

```text
Client
  -> API Gateway
  -> Auth Service
  -> Session Store
  -> User DB
```

At scale:

```text
Client
  -> Load Balancer
  -> API Gateway
  -> Auth Service replicas
  -> Redis Cluster for sessions
  -> PostgreSQL for users
```

Why Redis?

```text
fast lookup
TTL support
distributed across app instances
easy logout/revocation
```

---

# 16. Production-Grade Concepts

## Store Session ID In Secure Cookie

Production web apps usually send:

```http
Set-Cookie: SESSION_ID=abc; HttpOnly; Secure; SameSite=Lax
```

Important flags:

```text
HttpOnly -> JavaScript cannot read it
Secure   -> HTTPS only
SameSite -> CSRF protection support
```

---

## Session Expiry

Use:

```text
absolute expiry
idle timeout
refresh-on-activity
```

Example:

```text
absolute expiry = 12 hours
idle timeout = 30 minutes
```

---

## Session Rotation

After login, privilege change, or sensitive action:

```text
rotate sessionId
```

This reduces session fixation risk.

---

## Distributed Session Store

In-memory HashMap works only for one JVM.

If there are multiple app instances:

```text
instance A creates session
instance B cannot see it
```

Fix:

```text
Redis
database
distributed cache
```

---

# 17. Common Bugs

## Bug 1 — Session Never Expires

Bad:

```text
session valid forever
```

Good:

```text
expiresAt check
```

---

## Bug 2 — Predictable Session ID

Bad:

```text
sessionId = userId + timestamp
```

Good:

```text
random secure token
```

---

## Bug 3 — Session Stored Only In One Server

Problem:

```text
load balancer sends next request to another server
session not found
```

Fix:

```text
shared session store like Redis
```

---

## Bug 4 — No Logout

Without logout:

```text
stolen session remains valid until expiry
```

Fix:

```text
delete session on logout
```

---

# 18. Interview Notes

If interviewer asks:

```text
How do you manage sessions after login?
```

Answer:

```text
1. Verify credentials.
2. Generate random session ID.
3. Store session server-side.
4. Return session ID in secure HttpOnly cookie.
5. Validate session on each request.
6. Expire sessions.
7. Support logout by deleting session.
8. Use Redis for distributed session store.
9. Add session rotation.
10. Add audit logs and suspicious login detection.
```

Session vs JWT:

```text
Session:
server stores state

JWT:
client stores signed token
```

Session advantages:

```text
easy logout
easy revocation
server-side control
```

JWT advantages:

```text
stateless verification
good for distributed APIs
less server lookup
```

---

# 19. Next Step

Next file:

```text
005_JWT_Token_Generation.md
```

In the next phase, we move from:

```text
server-side sessionId
```

to:

```text
signed JWT access token
```

Flow:

```text
login success
    -> generate JWT
    -> client sends JWT in Authorization header
