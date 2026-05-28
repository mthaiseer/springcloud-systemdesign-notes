# MiniAuth / IAM Phase 3 — Login Authentication

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
  - [9.1 PasswordEncoder.java](#91-passwordencoderjava)
  - [9.2 User.java](#92-userjava)
  - [9.3 UserRepository.java](#93-userrepositoryjava)
  - [9.4 LoginRequest.java](#94-loginrequestjava)
  - [9.5 LoginResponse.java](#95-loginresponsejava)
  - [9.6 AuthenticationService.java](#96-authenticationservicejava)
  - [9.7 Phase003Driver.java](#97-phase003driverjava)
- [10. How To Run](#10-how-to-run)
- [11. Dry Run](#11-dry-run)
- [12. What Changed From Phase 2](#12-what-changed-from-phase-2)
- [13. What This Phase Does NOT Do Yet](#13-what-this-phase-does-not-do-yet)
- [14. DSA / CP Concepts Used](#14-dsa--cp-concepts-used)
- [15. System Design Relevance](#15-system-design-relevance)
- [16. Production-Grade Concepts](#16-production-grade-concepts)
- [17. Common Bugs](#17-common-bugs)
- [18. Interview Notes](#18-interview-notes)
- [19. Next Step](#19-next-step)

---

# 1. Goal

In this phase, we add real login authentication.

Before this phase:

```text
users can register
passwords are hashed
but users cannot login
```

After this phase:

```text
email + password -> authenticated user decision
```

Main objective:

```text
Verify entered password against stored password hash.
```

---

# 2. Why This Phase Matters

Authentication answers one core question:

```text
Is this user really who they claim to be?
```

Registration creates identity.

Password hashing protects credentials.

Login authentication verifies credentials.

This phase connects the previous two phases into a usable auth flow.

---

# 3. What We Built Previously

## Phase 1

```text
User Registration
```

We created users and stored them in memory.

## Phase 2

```text
Password Hashing
```

We replaced raw password storage with:

```text
salt:hash
```

Now Phase 3 uses that stored hash to verify login.

---

# 4. Previous Limitation

Phase 2 could create secure stored credentials.

But it could not answer:

```text
Can this user login?
```

Because Phase 2 only had:

```text
hash(rawPassword)
```

Now we need:

```text
matches(rawPassword, storedPasswordHash)
```

---

# 5. What We Build

We add:

```text
LoginRequest
LoginResponse
AuthenticationService
PasswordEncoder.matches()
```

Login flow:

```text
LoginRequest
    -> AuthenticationService
        -> UserRepository.findByEmail()
        -> PasswordEncoder.matches()
        -> LoginResponse
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
| email + password   |
+---------+----------+
          |
          v
+-----------------------------+
| AuthenticationService       |
| - validate request          |
| - normalize email           |
| - find user                 |
| - verify password hash      |
| - return login decision     |
+--------------+--------------+
               |
               v
+-----------------------------+
| UserRepository              |
| - find user by email        |
+--------------+--------------+
               |
               v
+-----------------------------+
| PasswordEncoder             |
| - split salt:hash           |
| - recompute hash            |
| - compare hash              |
+--------------+--------------+
               |
               v
+-----------------------------+
| LoginResponse               |
| - authenticated             |
| - userId                    |
| - email                     |
| - message                   |
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
                    ├── service/
                    │   └── AuthenticationService.java
                    └── driver/
                        └── Phase003Driver.java
```

---

# 8. Core Concepts

## 8.1 Authentication

Authentication means:

```text
verify identity
```

Example:

```text
email = mohamed@example.com
password = StrongPass123
```

System checks:

```text
1. Does user exist?
2. Does entered password match stored password hash?
```

---

## 8.2 Authentication vs Authorization

Authentication:

```text
Who are you?
```

Authorization:

```text
What are you allowed to access?
```

This phase is authentication only.

Authorization comes later with:

```text
RBAC
permissions
roles
policies
```

---

## 8.3 Password Verification

Stored value:

```text
salt:hash
```

Login password:

```text
StrongPass123
```

Verification:

```text
1. Extract salt from stored hash.
2. Compute hash(salt + enteredPassword).
3. Compare computed hash with stored hash.
```

---

# 9. Complete Java Code

---

## 9.1 `PasswordEncoder.java`

### Logic before this class

Phase 2 only needed:

```text
hash(rawPassword)
```

Phase 3 adds:

```text
matches(rawPassword, storedHash)
```

This is required for login.

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

## 9.2 `User.java`

### Logic before this class

User stores the password hash created during registration.

Login never needs raw password from database.

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

## 9.3 `UserRepository.java`

### Logic before this class

Authentication needs fast lookup by email.

For now:

```text
HashMap<String, User>
```

Production:

```text
PostgreSQL users table with unique email index
```

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

## 9.4 `LoginRequest.java`

### Logic before this class

This DTO represents login input.

Example JSON:

```json
{
  "email": "mohamed@example.com",
  "password": "StrongPass123"
}
```

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

Login response should tell whether login succeeded.

It must not return:

```text
password
password hash
salt
internal security data
```

```java
package com.miniauth.dto;

public class LoginResponse {

    private final boolean authenticated;
    private final String userId;
    private final String email;
    private final String message;

    public LoginResponse(
            boolean authenticated,
            String userId,
            String email,
            String message
    ) {
        this.authenticated = authenticated;
        this.userId = userId;
        this.email = email;
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

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "authenticated=" + authenticated +
                ", userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
```

---

## 9.6 `AuthenticationService.java`

### Logic before this class

This is the main new class in Phase 3.

It owns login authentication logic.

Responsibilities:

```text
1. Validate login request.
2. Normalize email.
3. Find user by email.
4. Compare raw password with stored hash.
5. Return safe login response.
```

```java
package com.miniauth.service;

import com.miniauth.dto.LoginRequest;
import com.miniauth.dto.LoginResponse;
import com.miniauth.model.User;
import com.miniauth.repository.UserRepository;
import com.miniauth.security.PasswordEncoder;

public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(
            UserRepository repository,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
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

        return new LoginResponse(
                true,
                user.getId(),
                user.getEmail(),
                "Login successful"
        );
    }

    private LoginResponse failedResponse() {
        return new LoginResponse(
                false,
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

## 9.7 `Phase003Driver.java`

### Logic before this class

The driver proves three scenarios:

```text
1. Correct email + correct password -> success
2. Correct email + wrong password -> failure
3. Unknown email -> failure
```

```java
package com.miniauth.driver;

import com.miniauth.dto.LoginRequest;
import com.miniauth.dto.LoginResponse;
import com.miniauth.model.User;
import com.miniauth.repository.UserRepository;
import com.miniauth.security.PasswordEncoder;
import com.miniauth.service.AuthenticationService;

public class Phase003Driver {

    public static void main(String[] args) {

        UserRepository repository = new UserRepository();
        PasswordEncoder passwordEncoder = new PasswordEncoder();

        String passwordHash =
                passwordEncoder.hash("StrongPass123");

        User user = new User(
                "mohamed@example.com",
                passwordHash
        );

        repository.save(user);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        repository,
                        passwordEncoder
                );

        LoginResponse success =
                authenticationService.login(
                        new LoginRequest(
                                "mohamed@example.com",
                                "StrongPass123"
                        )
                );

        LoginResponse wrongPassword =
                authenticationService.login(
                        new LoginRequest(
                                "mohamed@example.com",
                                "WrongPass123"
                        )
                );

        LoginResponse unknownEmail =
                authenticationService.login(
                        new LoginRequest(
                                "unknown@example.com",
                                "StrongPass123"
                        )
                );

        System.out.println(success);
        System.out.println(wrongPassword);
        System.out.println(unknownEmail);
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
Phase003Driver
```

---

## Command Line

From project root:

```bash
javac -d out src/main/java/com/miniauth/**/*.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase003Driver
```

Windows CMD example:

```cmd
javac -d out ^
src/main/java/com/miniauth/model/User.java ^
src/main/java/com/miniauth/dto/LoginRequest.java ^
src/main/java/com/miniauth/dto/LoginResponse.java ^
src/main/java/com/miniauth/repository/UserRepository.java ^
src/main/java/com/miniauth/security/PasswordEncoder.java ^
src/main/java/com/miniauth/service/AuthenticationService.java ^
src/main/java/com/miniauth/driver/Phase003Driver.java
```

Run:

```cmd
java -cp out com.miniauth.driver.Phase003Driver
```

---

# 11. Dry Run

Stored user:

```text
email        = mohamed@example.com
passwordHash = salt:hash
```

Login input:

```text
email    = mohamed@example.com
password = StrongPass123
```

Step-by-step:

```text
1. Driver creates LoginRequest.
2. AuthenticationService validates request.
3. Email is normalized.
4. Repository searches user by email.
5. User is found.
6. PasswordEncoder extracts salt from stored hash.
7. PasswordEncoder computes hash(salt + enteredPassword).
8. Computed hash is compared with stored hash.
9. If match, login succeeds.
10. LoginResponse is returned.
```

Success response:

```text
authenticated = true
userId = user id
email = mohamed@example.com
message = Login successful
```

Failure response:

```text
authenticated = false
userId = null
email = null
message = Invalid email or password
```

---

# 12. What Changed From Phase 2

## Phase 2

```text
hash password during registration
```

## Phase 3

```text
verify password during login
```

New classes:

```text
LoginRequest
LoginResponse
AuthenticationService
```

New method:

```text
PasswordEncoder.matches()
```

New flow:

```text
email + password
    -> find user
    -> verify password
    -> login decision
```

---

# 13. What This Phase Does NOT Do Yet

This phase does not yet create:

```text
session id
JWT access token
refresh token
cookie
logout
token revocation
RBAC
permissions
MFA
OAuth2
OIDC
audit log
login rate limiting
```

That comes in later phases.

For now, login only returns:

```text
authenticated true/false
```

---

# 14. DSA / CP Concepts Used

## HashMap Lookup

```java
repository.findByEmail(email)
```

Expected complexity:

```text
O(1)
```

Used for:

```text
email -> user
```

---

## String Split

Stored hash format:

```text
salt:hash
```

Verification needs:

```java
storedHash.split(":")
```

Complexity:

```text
O(N)
```

where `N` is stored hash length.

---

## Hash Verification

```text
actualHash == expectedHash
```

This is a deterministic check.

Same input:

```text
same salt + same password -> same hash
```

Wrong password:

```text
same salt + wrong password -> different hash
```

---

# 15. System Design Relevance

This phase maps to:

```text
Login API
Authentication Service
Credential verification
```

Typical HLD:

```text
Client
  -> API Gateway
  -> Auth Service
  -> User DB
```

At scale:

```text
Client
  -> Load Balancer
  -> API Gateway
  -> Auth Service replicas
  -> User DB
  -> Redis for login attempts
  -> Kafka for audit/security events
```

---

# 16. Production-Grade Concepts

## Generic Failure Message

Do not return:

```text
User not found
Wrong password
```

Return:

```text
Invalid email or password
```

Why?

To prevent email enumeration.

---

## Constant-Time Comparison

Production systems should use constant-time comparison to reduce timing attack risk.

This mini phase uses:

```java
expectedHash.equals(actualHash)
```

For learning, this is okay.

Production should use safe library functions.

---

## Rate Limit Login

Login endpoint must be protected against brute force.

Future phase:

```text
015_Login_Rate_Limiting.md
```

---

## Audit Logs

Login attempts should be logged safely:

```text
timestamp
email
ip
success/failure
reason category
device metadata
```

Never log password.

---

# 17. Common Bugs

## Bug 1 — Different Failure Messages

Bad:

```text
Email does not exist
Password is wrong
```

Good:

```text
Invalid email or password
```

---

## Bug 2 — Logging Raw Password

Never log:

```text
request.password
```

---

## Bug 3 — Comparing Raw Password With Hash

Bad:

```java
request.getPassword().equals(user.getPasswordHash())
```

Good:

```java
passwordEncoder.matches(
    request.getPassword(),
    user.getPasswordHash()
)
```

---

## Bug 4 — No Rate Limiting

Without rate limiting:

```text
attacker can try many passwords
```

Fix later:

```text
login attempt counter
IP/user rate limit
temporary lockout
captcha
MFA
```

---

# 18. Interview Notes

If interviewer asks:

```text
Design login authentication.
```

Answer structure:

```text
1. Accept email + password.
2. Normalize email.
3. Fetch user by email.
4. Use generic failure response.
5. Verify password using BCrypt/Argon2.
6. On success, issue session/JWT.
7. Add rate limiting.
8. Add audit logging.
9. Add MFA for risky login.
10. Monitor failures and abuse.
```

Good endpoint:

```http
POST /auth/login
```

Request:

```json
{
  "email": "mohamed@example.com",
  "password": "StrongPass123"
}
```

Response before JWT phase:

```json
{
  "authenticated": true,
  "userId": "uuid",
  "email": "mohamed@example.com",
  "message": "Login successful"
}
```

Later JWT response:

```json
{
  "accessToken": "...",
  "refreshToken": "...",
  "expiresIn": 900
}
```

---

# 19. Next Step

Next file:

```text
004_Session_Management.md
```

In the next phase, we move from:

```text
login success boolean
```

to:

```text
server-side session management
```

Flow:

```text
email + password
    -> authenticated user
    -> session id
    -> session store
```
