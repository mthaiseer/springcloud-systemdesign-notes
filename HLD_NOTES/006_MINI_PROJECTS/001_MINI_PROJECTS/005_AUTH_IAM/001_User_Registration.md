# MiniAuth / IAM Phase 1 — User Registration

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why This Phase Matters](#2-why-this-phase-matters)
- [3. What We Build](#3-what-we-build)
- [4. Current Architecture](#4-current-architecture)
- [5. Folder Structure](#5-folder-structure)
- [6. Core Concepts](#6-core-concepts)
- [7. Complete Java Code](#7-complete-java-code)
  - [7.1 User.java](#71-userjava)
  - [7.2 RegisterRequest.java](#72-registerrequestjava)
  - [7.3 RegisterResponse.java](#73-registerresponsejava)
  - [7.4 UserRepository.java](#74-userrepositoryjava)
  - [7.5 UserRegistrationService.java](#75-userregistrationservicejava)
  - [7.6 Phase001Driver.java](#76-phase001driverjava)
- [8. How To Run](#8-how-to-run)
- [9. Dry Run](#9-dry-run)
- [10. What This Phase Does NOT Do Yet](#10-what-this-phase-does-not-do-yet)
- [11. DSA / CP Concepts Used](#11-dsa--cp-concepts-used)
- [12. System Design Relevance](#12-system-design-relevance)
- [13. Production-Grade Concepts](#13-production-grade-concepts)
- [14. Common Bugs](#14-common-bugs)
- [15. Interview Notes](#15-interview-notes)
- [16. Next Step](#16-next-step)

---

# 1. Goal

In this phase, we build the first foundation of MiniAuth/IAM:

```text
User Registration
```

The purpose is simple:

```text
Create a user account with email + password.
```

At the end of this phase, we should be able to:

```text
1. Accept a registration request.
2. Validate user input.
3. Normalize email.
4. Check duplicate email.
5. Store the user in memory.
6. Return a safe response.
```

---

# 2. Why This Phase Matters

Every IAM system starts with identity creation.

Before login, JWT, sessions, roles, OAuth2, or MFA, the system must answer:

```text
Who is this user?
Can this email be registered?
Where do we store this identity?
What response is safe to return?
```

This phase creates the first building block.

Real systems like these all need this concept:

```text
Auth0
Okta
Keycloak
AWS Cognito
Google login
GitHub login
Banking apps
SaaS platforms
```

---

# 3. What We Build

We build a simple in-memory registration system.

Flow:

```text
RegisterRequest
    -> UserRegistrationService
        -> UserRepository
            -> RegisterResponse
```

Input:

```text
email
password
```

Output:

```text
userId
email
message
```

Important rule:

```text
Never return password in response.
```

---

# 4. Current Architecture

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
| - create user               |
+--------------+--------------+
               |
               v
+-----------------------------+
| UserRepository              |
| - save user                 |
| - find by email             |
| - check email exists        |
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

# 5. Folder Structure

Create this structure:

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
                    ├── service/
                    │   └── UserRegistrationService.java
                    └── driver/
                        └── Phase001Driver.java
```

---

# 6. Core Concepts

## 6.1 Identity

A user identity usually contains:

```text
id
email
password credential
createdAt
status
roles
tenant
```

In this first phase, we keep it minimal:

```text
id
email
password
createdAt
```

Later phases will improve this.

---

## 6.2 Email Normalization

This:

```text
Mohamed@Example.com
```

and this:

```text
mohamed@example.com
```

should be treated as the same email.

So we normalize:

```java
email.toLowerCase().trim()
```

---

## 6.3 Duplicate Email Check

A real IAM system should not allow duplicate registered emails.

Example:

```text
First request:
mohamed@example.com -> allowed

Second request:
mohamed@example.com -> rejected
```

---

# 7. Complete Java Code

---

## 7.1 `User.java`

### Logic before this class

`User` represents the identity stored inside the system.

For now, it stores raw password only because this is Phase 1.

In Phase 2, we replace raw password with password hash.

```java
package com.miniauth.model;

import java.time.Instant;
import java.util.UUID;

public class User {

    private final String id;
    private final String email;
    private final String password;
    private final Instant createdAt;

    public User(String email, String password) {
        this.id = UUID.randomUUID().toString();
        this.email = email.toLowerCase().trim();
        this.password = password;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
```

---

## 7.2 `RegisterRequest.java`

### Logic before this class

This DTO represents input from the client.

A real API would receive this as JSON.

Example JSON:

```json
{
  "email": "mohamed@example.com",
  "password": "StrongPass123"
}
```

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

## 7.3 `RegisterResponse.java`

### Logic before this class

This DTO represents safe output.

Important:

```text
Do not return password.
Do not return internal security details.
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

## 7.4 `UserRepository.java`

### Logic before this class

Repository stores users by normalized email.

For now we use:

```text
HashMap<String, User>
```

Later this becomes:

```text
PostgreSQL users table
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

## 7.5 `UserRegistrationService.java`

### Logic before this class

This service owns registration business logic.

It should:

```text
1. Validate email.
2. Validate password.
3. Check duplicate email.
4. Create user.
5. Save user.
6. Return safe response.
```

```java
package com.miniauth.service;

import com.miniauth.dto.RegisterRequest;
import com.miniauth.dto.RegisterResponse;
import com.miniauth.model.User;
import com.miniauth.repository.UserRepository;

public class UserRegistrationService {

    private final UserRepository repository;

    public UserRegistrationService(UserRepository repository) {
        this.repository = repository;
    }

    public RegisterResponse register(RegisterRequest request) {

        validate(request);

        String normalizedEmail = request.getEmail().toLowerCase().trim();

        if (repository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User(
                normalizedEmail,
                request.getPassword()
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

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (!request.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }

        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }
}
```

---

## 7.6 `Phase001Driver.java`

### Logic before this class

Driver proves registration works.

It tests:

```text
successful registration
duplicate email rejection
```

```java
package com.miniauth.driver;

import com.miniauth.dto.RegisterRequest;
import com.miniauth.dto.RegisterResponse;
import com.miniauth.repository.UserRepository;
import com.miniauth.service.UserRegistrationService;

public class Phase001Driver {

    public static void main(String[] args) {

        UserRepository repository = new UserRepository();
        UserRegistrationService service =
                new UserRegistrationService(repository);

        RegisterRequest request = new RegisterRequest(
                "Mohamed@Example.com",
                "StrongPass123"
        );

        RegisterResponse response = service.register(request);

        System.out.println(response);

        try {
            RegisterRequest duplicateRequest = new RegisterRequest(
                    "mohamed@example.com",
                    "AnotherPass123"
            );

            service.register(duplicateRequest);

        } catch (IllegalArgumentException ex) {
            System.out.println("Duplicate registration blocked: " + ex.getMessage());
        }
    }
}
```

---

# 8. How To Run

## IntelliJ

1. Create Java project `MiniAuth`
2. Create package:

```text
com.miniauth
```

3. Add the folder structure shown above.
4. Copy the Java classes.
5. Run:

```text
Phase001Driver
```

---

## Command Line

From project root:

```bash
javac -d out src/main/java/com/miniauth/**/*.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase001Driver
```

If your terminal does not support `**`, compile manually:

```bash
javac -d out ^
src/main/java/com/miniauth/model/User.java ^
src/main/java/com/miniauth/dto/RegisterRequest.java ^
src/main/java/com/miniauth/dto/RegisterResponse.java ^
src/main/java/com/miniauth/repository/UserRepository.java ^
src/main/java/com/miniauth/service/UserRegistrationService.java ^
src/main/java/com/miniauth/driver/Phase001Driver.java
```

Windows PowerShell run:

```powershell
java -cp out com.miniauth.driver.Phase001Driver
```

---

# 9. Dry Run

Input:

```text
email    = Mohamed@Example.com
password = StrongPass123
```

Step-by-step:

```text
1. Driver creates RegisterRequest.
2. Service receives request.
3. Service validates email and password.
4. Email is normalized:
   Mohamed@Example.com -> mohamed@example.com
5. Repository checks duplicate email.
6. No duplicate found.
7. User object is created.
8. User is saved in HashMap.
9. Safe RegisterResponse is returned.
```

State:

```text
usersByEmail
{
  "mohamed@example.com" -> User(id, email, password, createdAt)
}
```

Duplicate request:

```text
mohamed@example.com
```

Repository finds existing email.

Result:

```text
Email already registered
```

---

# 10. What This Phase Does NOT Do Yet

This phase does not yet include:

```text
password hashing
login authentication
sessions
JWT
refresh token
logout
RBAC
permissions
MFA
OAuth2
OIDC
audit logs
rate limiting
```

That is intentional.

MiniAuth grows step by step.

---

# 11. DSA / CP Concepts Used

## HashMap

We use:

```java
Map<String, User> usersByEmail
```

Why?

```text
email -> user lookup
```

Expected complexity:

```text
insert -> O(1)
lookup -> O(1)
exists -> O(1)
```

This is the same idea as many DSA problems:

```text
Two Sum
Frequency Map
Duplicate Detection
Group By Key
```

---

## String Normalization

We use:

```java
toLowerCase()
trim()
```

This prevents duplicate identity bugs.

Example:

```text
"Mohamed@Example.com"
" mohamed@example.com "
"mohamed@example.com"
```

All should map to:

```text
mohamed@example.com
```

---

## Validation

Validation is like guard clauses in CP:

```text
bad input -> reject early
valid input -> continue
```

This avoids deeper bugs.

---

# 12. System Design Relevance

In real HLD, this phase maps to:

```text
User Service
Identity Service
Auth Service
Registration API
User DB
```

Typical real architecture:

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
  -> PostgreSQL primary
  -> Read replicas
  -> Kafka audit events
```

---

# 13. Production-Grade Concepts

In production, improve this phase with:

```text
PostgreSQL users table
unique email index
password hashing
input validation library
email verification
audit logging
rate limiting
captcha for abuse
PII protection
structured logs
metrics
```

Database table idea:

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

Important production rule:

```text
Duplicate email protection must be enforced by database UNIQUE constraint.
```

Why?

Because this code is unsafe under concurrency:

```text
if email not exists
then insert
```

Two requests can pass the check at the same time.

---

# 14. Common Bugs

## Bug 1 — Storing Raw Password

In Phase 1, raw password is stored only for learning.

Production must never do this.

Phase 2 fixes this.

---

## Bug 2 — Returning Password In Response

Never return:

```text
password
password hash
refresh token
secret key
OTP
```

---

## Bug 3 — No Email Normalization

Without normalization:

```text
Mohamed@Example.com
mohamed@example.com
```

could become two different users.

---

## Bug 4 — Check-Then-Insert Race Condition

Bad production pattern:

```text
check if email exists
insert user
```

Correct production protection:

```text
database unique constraint
transaction handling
duplicate key exception handling
```

---

# 15. Interview Notes

If interviewer asks:

```text
Design user registration.
```

Answer structure:

```text
1. API contract
2. Validation
3. Email normalization
4. Password hashing
5. Unique email constraint
6. User DB schema
7. Email verification
8. Rate limiting
9. Audit log
10. Monitoring
```

Good API:

```http
POST /auth/register
```

Request:

```json
{
  "email": "mohamed@example.com",
  "password": "StrongPass123"
}
```

Response:

```json
{
  "userId": "uuid",
  "email": "mohamed@example.com",
  "message": "User registered successfully"
}
```

---

# 16. Next Step

Next file:

```text
002_Password_Hashing_BCrypt.md
```

In the next phase, we fix the biggest weakness of Phase 1:

```text
raw password storage
```

We will replace it with:

```text
salted password hash
```
