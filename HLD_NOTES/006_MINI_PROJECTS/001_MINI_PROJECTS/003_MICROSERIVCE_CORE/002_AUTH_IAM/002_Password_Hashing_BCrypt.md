# MiniAuth / IAM Phase 2 — Password Hashing BCrypt

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
  - [9.3 RegisterRequest.java](#93-registerrequestjava)
  - [9.4 RegisterResponse.java](#94-registerresponsejava)
  - [9.5 UserRepository.java](#95-userrepositoryjava)
  - [9.6 UserRegistrationService.java](#96-userregistrationservicejava)
  - [9.7 Phase002Driver.java](#97-phase002driverjava)
- [10. How To Run](#10-how-to-run)
- [11. Dry Run](#11-dry-run)
- [12. What Changed From Phase 1](#12-what-changed-from-phase-1)
- [13. What This Phase Does NOT Do Yet](#13-what-this-phase-does-not-do-yet)
- [14. DSA / CP Concepts Used](#14-dsa--cp-concepts-used)
- [15. System Design Relevance](#15-system-design-relevance)
- [16. Production-Grade Concepts](#16-production-grade-concepts)
- [17. Common Bugs](#17-common-bugs)
- [18. Interview Notes](#18-interview-notes)
- [19. Next Step](#19-next-step)

---

# 1. Goal

In this phase, we improve MiniAuth registration by replacing raw password storage with password hashing.

Before this phase:

```text
email -> raw password
```

After this phase:

```text
email -> salted password hash
```

Main objective:

```text
Never store plain-text passwords.
```

---

# 2. Why This Phase Matters

Password storage is one of the most important parts of IAM.

If a database leaks and passwords are stored as plain text, all user accounts are immediately compromised.

A safer system stores only:

```text
salt + password hash
```

So even if the database leaks, attackers do not directly get user passwords.

Real systems use:

```text
BCrypt
Argon2
PBKDF2
scrypt
```

In this learning phase, we simulate the idea using Java built-in hashing.

Important:

```text
This mini implementation is for learning.
Production should use BCrypt or Argon2 libraries.
```

---

# 3. What We Built Previously

In Phase 1, we built:

```text
User Registration
```

Phase 1 flow:

```text
RegisterRequest
    -> UserRegistrationService
        -> UserRepository
            -> RegisterResponse
```

But Phase 1 had one major weakness:

```text
raw password was stored inside User
```

---

# 4. Previous Limitation

Phase 1 `User.java` had:

```java
private final String password;
```

That means the system stored the password directly.

Example:

```text
mohamed@example.com -> StrongPass123
```

This is dangerous.

If the user database leaks:

```text
attacker sees every password
```

Phase 2 fixes this.

---

# 5. What We Build

We add a new security component:

```text
PasswordEncoder
```

Responsibility:

```text
raw password -> salted password hash
```

New registration flow:

```text
RegisterRequest
    -> validate input
    -> hash password
    -> create User with passwordHash
    -> save User
    -> return safe response
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
| RegisterRequest    |
+---------+----------+
          |
          v
+-----------------------------+
| UserRegistrationService     |
| - validate input            |
| - normalize email           |
| - check duplicate email     |
| - hash password             |
| - create user               |
+--------------+--------------+
               |
               v
+-----------------------------+
| PasswordEncoder             |
| - generate salt             |
| - hash password             |
| - return salt:hash          |
+--------------+--------------+
               |
               v
+-----------------------------+
| UserRepository              |
| - save user                 |
| - find by email             |
+--------------+--------------+
               |
               v
+-----------------------------+
| RegisterResponse            |
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
                    │   ├── RegisterRequest.java
                    │   └── RegisterResponse.java
                    ├── repository/
                    │   └── UserRepository.java
                    ├── security/
                    │   └── PasswordEncoder.java
                    ├── service/
                    │   └── UserRegistrationService.java
                    └── driver/
                        └── Phase002Driver.java
```

---

# 8. Core Concepts

## 8.1 Hashing

Hashing converts input into fixed-size output.

Example:

```text
StrongPass123 -> xYzHashValue...
```

A good password hash should be:

```text
one-way
slow
salted
hard to brute force
```

---

## 8.2 Salt

A salt is random data added before hashing.

Without salt:

```text
same password -> same hash
```

With salt:

```text
same password -> different hash
```

Example:

```text
password = StrongPass123

user1 salt = abc
hash = hash(abc + StrongPass123)

user2 salt = xyz
hash = hash(xyz + StrongPass123)
```

Even though both users use the same password, their stored hashes are different.

---

## 8.3 Stored Format

We store:

```text
salt:hash
```

Example:

```text
lq92asX1:abc123HashValue
```

Why store salt?

Because during login, we need to recompute:

```text
hash(storedSalt + enteredPassword)
```

Login verification comes in Phase 3.

---

# 9. Complete Java Code

---

## 9.1 `PasswordEncoder.java`

### Logic before this class

This class hides password hashing logic.

For learning, we use:

```text
SHA-256 + random salt
```

Production should use:

```text
BCryptPasswordEncoder
Argon2PasswordEncoder
PBKDF2
```

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

In Phase 1, `User` stored raw password.

Now it stores:

```text
passwordHash
```

This is the main model-level improvement in Phase 2.

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

## 9.3 `RegisterRequest.java`

### Logic before this class

The request still receives raw password from the client.

That is normal.

But raw password should only live briefly in memory.

It must not be stored.

```java
package com.miniauth.dto;

public class RegisterRequest {

    private final String email;
    private final String password;

    public RegisterRequest(String email, String password) {
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

## 9.4 `RegisterResponse.java`

### Logic before this class

The response must never expose:

```text
raw password
password hash
salt
security internals
```

```java
package com.miniauth.dto;

public class RegisterResponse {

    private final String userId;
    private final String email;
    private final String message;

    public RegisterResponse(String userId, String email, String message) {
        this.userId = userId;
        this.email = email;
        this.message = message;
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
        return "RegisterResponse{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
```

---

## 9.5 `UserRepository.java`

### Logic before this class

Repository still stores users by email.

Only the stored user object changed:

```text
password -> passwordHash
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

## 9.6 `UserRegistrationService.java`

### Logic before this class

This service now hashes password before creating the user.

Important difference from Phase 1:

```text
request.password is not stored directly
```

Instead:

```text
passwordHash = passwordEncoder.hash(request.password)
```

```java
package com.miniauth.service;

import com.miniauth.dto.RegisterRequest;
import com.miniauth.dto.RegisterResponse;
import com.miniauth.model.User;
import com.miniauth.repository.UserRepository;
import com.miniauth.security.PasswordEncoder;

public class UserRegistrationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(
            UserRepository repository,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse register(RegisterRequest request) {

        validate(request);

        String normalizedEmail =
                request.getEmail().toLowerCase().trim();

        if (repository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email already registered");
        }

        String passwordHash =
                passwordEncoder.hash(request.getPassword());

        User user = new User(
                normalizedEmail,
                passwordHash
        );

        repository.save(user);

        return new RegisterResponse(
                user.getId(),
                user.getEmail(),
                "User registered successfully"
        );
    }

    private void validate(RegisterRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (request.getEmail() == null ||
                request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (!request.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }

        if (request.getPassword() == null ||
                request.getPassword().length() < 8) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters"
            );
        }
    }
}
```

---

## 9.7 `Phase002Driver.java`

### Logic before this class

The driver proves that registration now stores a password hash, not raw password.

It also proves that two users with the same password get different hashes because salt is random.

This phase does not authenticate login yet.

```java
package com.miniauth.driver;

import com.miniauth.dto.RegisterRequest;
import com.miniauth.dto.RegisterResponse;
import com.miniauth.model.User;
import com.miniauth.repository.UserRepository;
import com.miniauth.security.PasswordEncoder;
import com.miniauth.service.UserRegistrationService;

public class Phase002Driver {

    public static void main(String[] args) {

        UserRepository repository = new UserRepository();
        PasswordEncoder passwordEncoder = new PasswordEncoder();

        UserRegistrationService service =
                new UserRegistrationService(
                        repository,
                        passwordEncoder
                );

        RegisterRequest request1 = new RegisterRequest(
                "mohamed@example.com",
                "StrongPass123"
        );

        RegisterResponse response1 = service.register(request1);

        System.out.println(response1);

        User storedUser1 =
                repository.findByEmail("mohamed@example.com")
                        .orElseThrow();

        System.out.println("Stored password hash:");
        System.out.println(storedUser1.getPasswordHash());

        System.out.println();
        System.out.println("Raw password was:");
        System.out.println("StrongPass123");

        System.out.println();
        System.out.println("Is raw password stored directly?");
        System.out.println(
                "StrongPass123".equals(storedUser1.getPasswordHash())
        );

        RegisterRequest request2 = new RegisterRequest(
                "samepassword@example.com",
                "StrongPass123"
        );

        service.register(request2);

        User storedUser2 =
                repository.findByEmail("samepassword@example.com")
                        .orElseThrow();

        System.out.println();
        System.out.println("Same raw password, different stored hashes?");
        System.out.println(
                !storedUser1.getPasswordHash()
                        .equals(storedUser2.getPasswordHash())
        );
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
4. Copy all classes.
5. Run:

```text
Phase002Driver
```

---

## Command Line

From project root:

```bash
javac -d out src/main/java/com/miniauth/**/*.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase002Driver
```

If your shell does not support `**`, compile manually.

Windows CMD example:

```cmd
javac -d out ^
src/main/java/com/miniauth/model/User.java ^
src/main/java/com/miniauth/dto/RegisterRequest.java ^
src/main/java/com/miniauth/dto/RegisterResponse.java ^
src/main/java/com/miniauth/repository/UserRepository.java ^
src/main/java/com/miniauth/security/PasswordEncoder.java ^
src/main/java/com/miniauth/service/UserRegistrationService.java ^
src/main/java/com/miniauth/driver/Phase002Driver.java
```

Run:

```cmd
java -cp out com.miniauth.driver.Phase002Driver
```

---

# 11. Dry Run

Input:

```text
email    = mohamed@example.com
password = StrongPass123
```

Step-by-step:

```text
1. Driver creates RegisterRequest.
2. Registration service validates request.
3. Email is normalized.
4. Repository checks duplicate email.
5. PasswordEncoder generates random salt.
6. PasswordEncoder computes hash(salt + password).
7. PasswordEncoder returns salt:hash.
8. User is created with passwordHash.
9. User is saved.
10. Safe response is returned.
```

Stored state:

```text
usersByEmail
{
  "mohamed@example.com" -> User(
      id = uuid,
      email = mohamed@example.com,
      passwordHash = salt:hash,
      createdAt = time
  )
}
```

Important result:

```text
raw password is NOT stored
```

---

# 12. What Changed From Phase 1

## Phase 1

```text
User.email
User.password
```

## Phase 2

```text
User.email
User.passwordHash
```

## Phase 1 service

```text
create User(email, rawPassword)
```

## Phase 2 service

```text
passwordHash = passwordEncoder.hash(rawPassword)
create User(email, passwordHash)
```

## Phase 1 risk

```text
database leak exposes passwords
```

## Phase 2 improvement

```text
database leak exposes only salt + hash
```

---

# 13. What This Phase Does NOT Do Yet

This phase does not yet do:

```text
login authentication
password verification
sessions
JWT
refresh token
logout
RBAC
permissions
MFA
OAuth2
OIDC
rate limiting
audit logs
```

Login comes in Phase 3.

In Phase 3, we add:

```text
PasswordEncoder.matches(rawPassword, storedHash)
AuthenticationService
```

---

# 14. DSA / CP Concepts Used

## Hashing

Password hashing maps:

```text
input string -> fixed output string
```

This is conceptually related to hashing in DSA.

But security hashing is different from HashMap hashing.

```text
DSA hash:
fast lookup

Password hash:
slow and hard to brute force
```

---

## Randomization

Salt uses randomness.

```java
SecureRandom
```

This prevents same password from always producing same stored value.

---

## String Processing

We build stored hash as:

```text
salt:hash
```

Later, Phase 3 will split it:

```java
storedHash.split(":")
```

---

## Complexity

For registration:

```text
email validation     -> O(L)
email lookup         -> O(1)
salt generation      -> O(1)
password hashing     -> O(P)
repository save      -> O(1)
```

Where:

```text
L = email length
P = password length
```

---

# 15. System Design Relevance

This phase maps directly to:

```text
credential storage
identity security
user registration backend
```

In HLD, this is part of:

```text
Client
  -> API Gateway
  -> Auth Service
  -> User DB
```

Production storage:

```text
users table
credentials table
password_hash column
created_at
updated_at
status
```

Example:

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

---

# 16. Production-Grade Concepts

## Use BCrypt / Argon2

Do not use plain SHA-256 for passwords in production.

Why?

```text
SHA-256 is too fast.
Fast hashing helps attackers brute force passwords.
```

Better:

```text
BCrypt
Argon2id
PBKDF2
scrypt
```

---

## Cost Factor

BCrypt has a cost factor.

Higher cost means:

```text
slower hash
more CPU cost
harder brute force
```

Example Spring Boot production style:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
}
```

---

## Unique Email Constraint

Even with HashMap duplicate check, production needs:

```text
database UNIQUE constraint
```

Because concurrent requests can bypass application-level check.

---

## Avoid Logging Passwords

Never log:

```text
raw password
password hash
salt
reset token
refresh token
OTP
```

---

# 17. Common Bugs

## Bug 1 — Using Same Salt For Everyone

Bad:

```text
globalSalt + password
```

Good:

```text
unique random salt per password
```

---

## Bug 2 — Storing Raw Password Alongside Hash

Bad:

```text
password
passwordHash
```

Good:

```text
passwordHash only
```

---

## Bug 3 — Returning Password Hash In API

Bad response:

```json
{
  "email": "mohamed@example.com",
  "passwordHash": "salt:hash"
}
```

Good response:

```json
{
  "userId": "uuid",
  "email": "mohamed@example.com",
  "message": "User registered successfully"
}
```

---

## Bug 4 — Using Fast Hash In Production

Bad:

```text
MD5
SHA-1
SHA-256 alone
```

Good:

```text
BCrypt
Argon2id
PBKDF2
```

---

# 18. Interview Notes

If interviewer asks:

```text
How do you store passwords securely?
```

Answer:

```text
1. Never store raw passwords.
2. Use BCrypt or Argon2.
3. Use unique random salt per password.
4. Store only password hash.
5. Tune cost factor.
6. Add rate limiting on login.
7. Add MFA for sensitive accounts.
8. Never log secrets.
9. Use TLS.
10. Use database access controls.
```

Good explanation:

```text
During registration:
raw password -> password encoder -> salted hash -> DB

During login:
entered password + stored salt -> recompute hash -> compare safely
```

But login verification is Phase 3.

---

# 19. Next Step

Next file:

```text
003_Login_Authentication.md
```

In the next phase, we add real login authentication:

```text
email + password
    -> find user
    -> verify password hash
    -> return authentication result
```
