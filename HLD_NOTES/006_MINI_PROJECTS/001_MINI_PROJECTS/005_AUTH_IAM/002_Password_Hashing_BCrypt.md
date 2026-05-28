# 002_Password_Hashing_BCrypt.md

# MiniAuth / IAM Phase 2 — Password Hashing BCrypt

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why This Phase Matters](#2-why-this-phase-matters)
- [3. What We Built Previously](#3-what-we-built-previously)
- [4. Previous Limitation](#4-previous-limitation)
- [5. What We Build](#5-what-we-build)
- [6. Current Architecture](#6-current-architecture)
- [7. Folder Structure](#7-folder-structure)
- [8. Complete Java Code](#8-complete-java-code)
- [9. How To Run](#9-how-to-run)
- [10. Dry Run](#10-dry-run)
- [11. DSA / CP Concepts Used](#11-dsa--cp-concepts-used)
- [12. System Design Relevance](#12-system-design-relevance)
- [13. IAM Connection With This Phase](#13-iam-connection-with-this-phase)
- [14. Production-Grade Concepts](#14-production-grade-concepts)
- [15. Scalability Discussion](#15-scalability-discussion)
- [16. Interview Notes](#16-interview-notes)
- [17. Common Bugs](#17-common-bugs)
- [18. Security Warning](#18-security-warning)
- [19. Next Step](#19-next-step)

---

# 1. Goal

In this phase, we build:

```text
Password Hashing BCrypt
```

Purpose:

```text
Replace plain-text password storage with salted password hashing using a BCrypt-style password encoder.
```

This phase continues MiniAuth/IAM from simple identity creation toward a production-grade authentication and authorization platform.

---

# 2. Why This Phase Matters

IAM systems are built layer by layer.

A real system needs:

```text
registration
password security
login
sessions
JWT
refresh tokens
roles
permissions
MFA
OAuth2
OIDC
SSO
audit logs
distributed revocation
```

This phase adds one of those layers.

Without this phase, later production capabilities would be incomplete.

---

# 3. What We Built Previously

Earlier phases created the foundation:

```text
User registration
  -> identity record
  -> email uniqueness
  -> input validation
```

Current mental model:

```text
Client request
  -> DTO
  -> Service
  -> Repository / Security component
  -> Response
```

---

# 4. Previous Limitation

```text
Plain password storage is unsafe.
```

Why this matters:

```text
Security systems fail when one small assumption is wrong.
```

So every phase fixes one real-world weakness.

---

# 5. What We Build

We build:

```text
PasswordEncoder
```

Core flow:

```text
password -> salted hash
```

Feature behavior:

```text
input
  -> validate
  -> apply IAM rule
  -> update state or return decision
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
| Request DTO        |
+---------+----------+
          |
          v
+--------------------+
| IAM Service        |
| Password Hashing BCrypt |
+---------+----------+
          |
          v
+--------------------+
| Repository / Store |
+---------+----------+
          |
          v
+--------------------+
| Safe Response      |
+--------------------+
```

Detailed flow:

```text
1. Input arrives.
2. Service validates input.
3. Security component applies rule.
4. State is saved or checked.
5. Response is returned without leaking secrets.
```

---

# 7. Folder Structure

Recommended structure:

```text
MiniAuth/
└── src/
    └── main/
        └── java/
            └── com/
                └── miniauth/
                    ├── model/
                    ├── dto/
                    ├── repository/
                    ├── security/
                    ├── service/
                    ├── jwt/
                    ├── session/
                    ├── authz/
                    ├── metrics/
                    └── driver/
```

For this phase, create only the packages used by the code.

---

# 8. Complete Java Code


## 8.1 `PasswordEncoder.java`

### Logic before this class

This class hides password hashing logic.

In production use BCrypt/Argon2 from a trusted library.

For this mini phase, we use SHA-256 with salt to explain the idea.

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
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }
}
```

---

## 8.2 `User.java`

### Logic before this class

The user now stores `passwordHash`, not raw password.

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

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Instant getCreatedAt() { return createdAt; }
}
```

---

## 8.3 `UserRepository.java`

### Logic before this class

Repository stores users by normalized email.

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
        return Optional.ofNullable(usersByEmail.get(email.toLowerCase().trim()));
    }

    public boolean existsByEmail(String email) {
        return usersByEmail.containsKey(email.toLowerCase().trim());
    }
}
```

---

## 8.4 `Phase002Driver.java`

### Logic before this class

The driver proves that raw passwords are never stored.

It also proves login verification can compare raw password against stored hash.

```java
package com.miniauth.driver;

import com.miniauth.model.User;
import com.miniauth.repository.UserRepository;
import com.miniauth.security.PasswordEncoder;

public class Phase002Driver {
    public static void main(String[] args) {
        UserRepository repository = new UserRepository();
        PasswordEncoder encoder = new PasswordEncoder();

        String rawPassword = "StrongPass123";
        String passwordHash = encoder.hash(rawPassword);

        User user = new User("mohamed@example.com", passwordHash);
        repository.save(user);

        System.out.println("Stored hash = " + user.getPasswordHash());
        System.out.println("Login correct password = " +
                encoder.matches("StrongPass123", user.getPasswordHash()));
        System.out.println("Login wrong password = " +
                encoder.matches("WrongPass123", user.getPasswordHash()));
    }
}
```


---

# 9. How To Run

## IntelliJ

1. Create Java project `MiniAuth`
2. Create the package structure shown above
3. Add the classes from this file
4. Run the phase driver class

## Command line

Compile from project root:

```bash
javac -d out src/main/java/com/miniauth/**/*.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase002Driver
```

If your shell does not support `**`, compile specific files manually.

---

# 10. Dry Run

Example flow for this phase:

```text
Input:
Password Hashing BCrypt

Step 1:
Driver creates input.

Step 2:
Service/component validates input.

Step 3:
IAM rule is applied.

Step 4:
Repository/store is updated or queried.

Step 5:
Safe output is printed.
```

State transition:

```text
Before:
Plain password storage is unsafe.

After:
password -> salted hash
```

Visual:

```text
Request
  -> validate
  -> security decision
  -> update state
  -> response
```

---

# 11. DSA / CP Concepts Used

```text
Hashing, salting, one-way functions
```

Complexity thinking:

```text
Lookup        -> usually O(1)
Validation    -> depends on input size
Token/session -> usually O(1)
Audit scan    -> can become O(N) unless indexed
```

This connects DSA to real backend security.

---

# 12. System Design Relevance

This phase maps to:

```text
user database credential security
```

In HLD, this often appears as:

```text
Client
  -> API Gateway
  -> IAM/Auth Service
  -> User DB / Redis / Token Store
  -> Protected Service
```

---

# 13. IAM Connection With This Phase

Real IAM platforms use this concept in:

```text
Okta
Auth0
Keycloak
AWS Cognito
Azure AD
Google login
GitHub OAuth
enterprise SSO
banking apps
SaaS platforms
```

MiniAuth version:

```text
simple Java implementation
```

Production version:

```text
Spring Security
PostgreSQL
Redis
Kafka audit events
HSM/KMS key management
OAuth2/OIDC standards
monitoring and alerting
```

---

# 14. Production-Grade Concepts

Production concerns:

```text
secret leakage
token expiry
replay attacks
brute force
session hijacking
key rotation
audit trails
PII protection
tenant isolation
rate limiting
monitoring
```

Always ask:

```text
Can this be replayed?
Can this be brute-forced?
Can this leak secrets?
Can this be revoked?
Can this be audited?
Can this scale across nodes?
```

---

# 15. Scalability Discussion

Small scale:

```text
single JVM
in-memory maps
simple drivers
```

Production scale:

```text
PostgreSQL for durable users
Redis for sessions/tokens/rate limits
Kafka for audit/security events
API Gateway for centralized auth
Prometheus/Grafana for metrics
KMS/HSM for signing keys
```

Bottlenecks:

```text
password hashing CPU
token verification cost
database unique indexes
Redis hot keys
audit event volume
gateway latency
revocation lookup latency
```

---

# 16. Interview Notes

Good answer structure:

```text
Requirement
  -> Threat model
  -> Data model
  -> Flow
  -> Failure cases
  -> Scaling path
```

Common follow-up questions:

```text
How do you revoke JWT?
How do you rotate keys?
How do you prevent refresh token replay?
How do you rate-limit login?
How do you support SSO?
How do you isolate tenants?
```

---

# 17. Common Bugs

## Bug 1 — Returning secrets

Never return:

```text
password
password hash
refresh token in logs
private key
OTP code
```

---

## Bug 2 — No expiry

Security objects must expire:

```text
OTP
session
refresh token
reset token
auth code
```

---

## Bug 3 — Missing normalization

Normalize:

```text
email
tenant id
client id
scope names
roles
```

---

## Bug 4 — Race conditions

Common issue:

```text
check then insert
```

Fix:

```text
unique constraints
atomic operations
transactions
locks where needed
```

---

# 18. Security Warning

This mini project is for learning.

Production IAM should use:

```text
battle-tested libraries
Spring Security
OAuth2/OIDC compliant libraries
BCrypt/Argon2
KMS/HSM
secure cookies
TLS everywhere
security reviews
penetration testing
```

Do not invent crypto in real production systems.

---

# 19. Next Step

Next file:

```text
003_Login_Authentication.md
```

Continue the MiniAuth/IAM roadmap step by step.
