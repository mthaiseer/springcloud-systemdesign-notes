# 001_User_Registration.md

# MiniAuth / IAM Phase 001 — User Registration

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why This Phase Matters](#2-why-this-phase-matters)
- [3. What We Build](#3-what-we-build)
- [4. Current Architecture](#4-current-architecture)
- [5. Folder Structure](#5-folder-structure)
- [6. Step-by-Step Flow](#6-step-by-step-flow)
- [7. Complete Java Code](#7-complete-java-code)
  - [7.1 User.java](#71-userjava)
  - [7.2 RegisterRequest.java](#72-registerrequestjava)
  - [7.3 RegisterResponse.java](#73-registerresponsejava)
  - [7.4 UserRepository.java](#74-userrepositoryjava)
  - [7.5 PasswordPolicy.java](#75-passwordpolicyjava)
  - [7.6 UserRegistrationService.java](#76-userregistrationservicejava)
  - [7.7 Phase001UserRegistrationDriver.java](#77-phase001userregistrationdriverjava)
- [8. How To Run](#8-how-to-run)
- [9. Dry Run](#9-dry-run)
- [10. DSA / CP Concepts Used](#10-dsa--cp-concepts-used)
- [11. System Design Relevance](#11-system-design-relevance)
- [12. IAM Connection With This Phase](#12-iam-connection-with-this-phase)
- [13. Production-Grade Concepts](#13-production-grade-concepts)
- [14. Scalability Discussion](#14-scalability-discussion)
- [15. Interview Notes](#15-interview-notes)
- [16. Common Bugs](#16-common-bugs)
- [17. Security Warning](#17-security-warning)
- [18. Next Step](#18-next-step)

---

# 1. Goal

In this phase, we build the **first foundation of MiniAuth/IAM**:

```text
User Registration
```

We will create a simple registration system that:

- accepts username, email, and password
- validates input
- checks duplicate email
- checks password rules
- creates a user record
- stores the user in memory
- returns a registration response

This is not production IAM yet, but it is the first building block.

Real IAM systems also start with:

```text
identity creation
```

Before login, JWT, OAuth2, SSO, MFA, RBAC, or permissions can exist, the system must first know:

```text
Who is the user?
```

---

# 2. Why This Phase Matters

Authentication systems start with identity registration.

Before a user can:

```text
login
receive JWT
refresh token
use MFA
access protected APIs
join a tenant
receive roles
use OAuth2
```

the system needs a user profile.

A real registration flow must answer:

```text
1. Is the email valid?
2. Is the password acceptable?
3. Does this email already exist?
4. How is the user stored?
5. What is returned to the client?
6. What must never be returned?
```

This phase teaches the clean foundation.

---

# 3. What We Build

We build an in-memory user registration service.

Example input:

```text
username = mohamed
email    = mohamed@example.com
password = StrongPass123
```

Expected behavior:

```text
User registered successfully
```

Duplicate registration:

```text
email = mohamed@example.com
```

Expected behavior:

```text
Email already exists
```

Weak password:

```text
password = abc
```

Expected behavior:

```text
Password is too weak
```

---

# 4. Current Architecture

```text
+------------------------+
| Registration Request   |
| username/email/pass    |
+-----------+------------+
            |
            v
+------------------------+
| UserRegistrationService|
| validate + create user |
+-----------+------------+
            |
            v
+------------------------+
| PasswordPolicy         |
| password validation    |
+-----------+------------+
            |
            v
+------------------------+
| UserRepository         |
| in-memory storage      |
+-----------+------------+
            |
            v
+------------------------+
| RegisterResponse       |
| success / error        |
+------------------------+
```

## Request Flow

```text
1. Client submits registration request
2. Service validates username/email/password
3. Service checks duplicate email
4. Service creates user object
5. Repository stores user
6. Service returns response
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
                    ├── security/
                    │   └── PasswordPolicy.java
                    ├── service/
                    │   └── UserRegistrationService.java
                    └── driver/
                        └── Phase001UserRegistrationDriver.java
```

Recommended package style:

```text
com.miniauth.model
com.miniauth.dto
com.miniauth.repository
com.miniauth.security
com.miniauth.service
com.miniauth.driver
```

---

# 6. Step-by-Step Flow

## Step 1 — Receive Registration Request

```text
username
email
password
```

Example:

```text
mohamed
mohamed@example.com
StrongPass123
```

---

## Step 2 — Validate Required Fields

The service checks:

```text
username is not blank
email is not blank
password is not blank
```

---

## Step 3 — Validate Email Format

Simple rule:

```text
email must contain @ and .
```

Production systems use stronger validation, but this is enough for the first phase.

---

## Step 4 — Validate Password Policy

Basic password rules:

```text
minimum 8 characters
must contain uppercase
must contain lowercase
must contain digit
```

Later we add:

```text
BCrypt hashing
password breach check
password reset
password history
```

---

## Step 5 — Check Duplicate Email

```java
repository.existsByEmail(email)
```

If email exists:

```text
registration fails
```

---

## Step 6 — Create User

The system creates:

```text
id
username
email
password
createdAt
enabled
```

Important:

```text
In this phase password is stored as plain text only for learning.
In the next phase we replace it with BCrypt hash.
```

---

## Step 7 — Store User

```java
repository.save(user)
```

---

## Step 8 — Return Response

Success response:

```text
success = true
message = User registered successfully
userId = generated user id
```

Failure response:

```text
success = false
message = Email already exists
```

---

# 7. Complete Java Code

---

## 7.1 User.java

### Logic before this class

`User` is the core identity record.

It represents one registered user in the IAM system.

Fields:

```text
id        -> unique user id
username  -> display/login name
email     -> unique login identifier
password  -> currently plain text, later BCrypt hash
createdAt -> account creation time
enabled   -> whether account is active
```

In production, never return the password to the client.

```java
package com.miniauth.model;

import java.time.Instant;
import java.util.UUID;

public class User {

    private final String id;
    private final String username;
    private final String email;
    private final String password;
    private final Instant createdAt;
    private boolean enabled;

    public User(String username, String email, String password) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.email = email.toLowerCase();
        this.password = password;
        this.createdAt = Instant.now();
        this.enabled = true;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void disable() {
        this.enabled = false;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", enabled=" + enabled +
                '}';
    }
}
```

---

## 7.2 RegisterRequest.java

### Logic before this class

This class represents input from the client.

A real API might receive this JSON:

```json
{
  "username": "mohamed",
  "email": "mohamed@example.com",
  "password": "StrongPass123"
}
```

We use a Java class so the service does not deal with raw strings.

```java
package com.miniauth.dto;

public class RegisterRequest {

    private final String username;
    private final String email;
    private final String password;

    public RegisterRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
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

## 7.3 RegisterResponse.java

### Logic before this class

This class represents output to the client.

Important rule:

```text
Never return password.
```

Response contains:

```text
success
message
userId
```

If registration fails, `userId` can be null.

```java
package com.miniauth.dto;

public class RegisterResponse {

    private final boolean success;
    private final String message;
    private final String userId;

    private RegisterResponse(boolean success, String message, String userId) {
        this.success = success;
        this.message = message;
        this.userId = userId;
    }

    public static RegisterResponse success(String message, String userId) {
        return new RegisterResponse(true, message, userId);
    }

    public static RegisterResponse failure(String message) {
        return new RegisterResponse(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "RegisterResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
```

---

## 7.4 UserRepository.java

### Logic before this class

This is the in-memory database for users.

For now, we use:

```java
Map<String, User> usersByEmail
```

Why email as key?

Because email must be unique during registration.

Operations:

```text
save user
find by email
exists by email
count users
```

Later this becomes:

```text
PostgreSQL users table
unique index on email
transactional insert
```

```java
package com.miniauth.repository;

import com.miniauth.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserRepository {

    private final Map<String, User> usersByEmail = new HashMap<>();

    public void save(User user) {
        usersByEmail.put(normalize(user.getEmail()), user);
    }

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(normalize(email)));
    }

    public boolean existsByEmail(String email) {
        return usersByEmail.containsKey(normalize(email));
    }

    public int count() {
        return usersByEmail.size();
    }

    public Collection<User> findAll() {
        return usersByEmail.values();
    }

    private String normalize(String email) {
        return email.toLowerCase().trim();
    }
}
```

---

## 7.5 PasswordPolicy.java

### Logic before this class

This class checks password strength.

For this phase, rules are simple:

```text
at least 8 characters
has uppercase
has lowercase
has digit
```

Why separate class?

Because password policy changes often.

Examples:

```text
minimum length changes
special character required
breached password check added
password history rule added
```

Keeping it separate avoids changing registration service every time.

```java
package com.miniauth.security;

public class PasswordPolicy {

    public boolean isValid(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(ch)) {
                hasLowercase = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            }
        }

        return hasUppercase && hasLowercase && hasDigit;
    }

    public String rules() {
        return "Password must be at least 8 characters and contain uppercase, lowercase, and digit";
    }
}
```

---

## 7.6 UserRegistrationService.java

### Logic before this class

This is the main business service.

It coordinates:

```text
request validation
email uniqueness check
password policy validation
user creation
repository save
response creation
```

Important design rule:

```text
Controller/API layer should call this service.
Repository should not contain business validation.
```

This separation gives clean architecture:

```text
API -> Service -> Repository
```

```java
package com.miniauth.service;

import com.miniauth.dto.RegisterRequest;
import com.miniauth.dto.RegisterResponse;
import com.miniauth.model.User;
import com.miniauth.repository.UserRepository;
import com.miniauth.security.PasswordPolicy;

public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordPolicy passwordPolicy;

    public UserRegistrationService(UserRepository userRepository, PasswordPolicy passwordPolicy) {
        this.userRepository = userRepository;
        this.passwordPolicy = passwordPolicy;
    }

    public RegisterResponse register(RegisterRequest request) {
        String validationError = validateRequest(request);

        if (validationError != null) {
            return RegisterResponse.failure(validationError);
        }

        String normalizedEmail = request.getEmail().toLowerCase().trim();

        if (userRepository.existsByEmail(normalizedEmail)) {
            return RegisterResponse.failure("Email already exists");
        }

        if (!passwordPolicy.isValid(request.getPassword())) {
            return RegisterResponse.failure(passwordPolicy.rules());
        }

        User user = new User(
                request.getUsername().trim(),
                normalizedEmail,
                request.getPassword()
        );

        userRepository.save(user);

        return RegisterResponse.success("User registered successfully", user.getId());
    }

    private String validateRequest(RegisterRequest request) {
        if (request == null) {
            return "Request cannot be null";
        }

        if (isBlank(request.getUsername())) {
            return "Username is required";
        }

        if (isBlank(request.getEmail())) {
            return "Email is required";
        }

        if (!isValidEmail(request.getEmail())) {
            return "Invalid email format";
        }

        if (isBlank(request.getPassword())) {
            return "Password is required";
        }

        return null;
    }

    private boolean isValidEmail(String email) {
        String value = email.trim();

        return value.contains("@") &&
                value.contains(".") &&
                value.indexOf("@") > 0 &&
                value.lastIndexOf(".") > value.indexOf("@");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
```

---

## 7.7 Phase001UserRegistrationDriver.java

### Logic before this class

This driver tests the registration service without HTTP or Spring Boot.

Why?

Because core business logic should be tested before adding web/API complexity.

The driver tests:

```text
valid registration
duplicate email
weak password
invalid email
missing username
```

```java
package com.miniauth.driver;

import com.miniauth.dto.RegisterRequest;
import com.miniauth.dto.RegisterResponse;
import com.miniauth.model.User;
import com.miniauth.repository.UserRepository;
import com.miniauth.security.PasswordPolicy;
import com.miniauth.service.UserRegistrationService;

public class Phase001UserRegistrationDriver {

    public static void main(String[] args) {
        UserRepository repository = new UserRepository();
        PasswordPolicy passwordPolicy = new PasswordPolicy();
        UserRegistrationService service = new UserRegistrationService(repository, passwordPolicy);

        run("Valid user",
                service,
                new RegisterRequest("mohamed", "mohamed@example.com", "StrongPass123"));

        run("Duplicate email",
                service,
                new RegisterRequest("mohamed2", "mohamed@example.com", "StrongPass123"));

        run("Weak password",
                service,
                new RegisterRequest("john", "john@example.com", "abc"));

        run("Invalid email",
                service,
                new RegisterRequest("alice", "alice-email", "StrongPass123"));

        run("Missing username",
                service,
                new RegisterRequest("", "empty@example.com", "StrongPass123"));

        System.out.println("Total users stored: " + repository.count());

        System.out.println();
        System.out.println("Users in repository:");
        for (User user : repository.findAll()) {
            System.out.println(user);
        }
    }

    private static void run(String label, UserRegistrationService service, RegisterRequest request) {
        System.out.println("---- " + label + " ----");

        RegisterResponse response = service.register(request);

        System.out.println(response);
        System.out.println();
    }
}
```

---

# 8. How To Run

## Option A — IntelliJ

1. Create Java project `MiniAuth`
2. Create packages:

```text
com.miniauth.model
com.miniauth.dto
com.miniauth.repository
com.miniauth.security
com.miniauth.service
com.miniauth.driver
```

3. Add each class into its package
4. Run:

```text
Phase001UserRegistrationDriver
```

---

## Option B — Command Line

From project root:

```bash
javac -d out src/main/java/com/miniauth/model/User.java \
             src/main/java/com/miniauth/dto/RegisterRequest.java \
             src/main/java/com/miniauth/dto/RegisterResponse.java \
             src/main/java/com/miniauth/repository/UserRepository.java \
             src/main/java/com/miniauth/security/PasswordPolicy.java \
             src/main/java/com/miniauth/service/UserRegistrationService.java \
             src/main/java/com/miniauth/driver/Phase001UserRegistrationDriver.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase001UserRegistrationDriver
```

---

# 9. Dry Run

## Case 1 — Valid User

Input:

```text
username = mohamed
email = mohamed@example.com
password = StrongPass123
```

Flow:

```text
Driver
  |
  v
UserRegistrationService.register()
  |
  v
validateRequest()
  |
  v
email valid? yes
password blank? no
  |
  v
repository.existsByEmail("mohamed@example.com") -> false
  |
  v
passwordPolicy.isValid("StrongPass123") -> true
  |
  v
new User(...)
  |
  v
repository.save(user)
  |
  v
RegisterResponse.success(...)
```

Storage after registration:

```text
usersByEmail
└── mohamed@example.com
    └── User(id=uuid, username=mohamed, enabled=true)
```

Response:

```text
success=true
message=User registered successfully
userId=<uuid>
```

---

## Case 2 — Duplicate Email

Input:

```text
username = mohamed2
email = mohamed@example.com
password = StrongPass123
```

Flow:

```text
repository.existsByEmail("mohamed@example.com") -> true
```

Response:

```text
success=false
message=Email already exists
```

No new user is created.

---

## Case 3 — Weak Password

Input:

```text
password = abc
```

Password policy check:

```text
length >= 8 ? false
has uppercase ? false
has lowercase ? true
has digit ? false
```

Response:

```text
success=false
message=Password must be at least 8 characters and contain uppercase, lowercase, and digit
```

---

# 10. DSA / CP Concepts Used

This phase uses basic but important data structures.

| Concept | Usage |
|---|---|
| HashMap | Store users by email |
| String validation | Validate email/password |
| Linear scan | Check password characters |
| UUID generation | Unique user id |
| Optional | Safe nullable lookup |

## Complexity

| Operation | Complexity |
|---|---|
| Register user | O(P) |
| Check duplicate email | O(1) average |
| Save user | O(1) average |
| Password validation | O(P) |

Where:

```text
P = password length
```

This is simple, but this is exactly how real services begin.

---

# 11. System Design Relevance

This phase maps to real IAM systems.

Examples:

```text
Google account creation
GitHub signup
Banking app registration
SaaS tenant user signup
Internal employee IAM onboarding
```

In system design, registration usually connects to:

```text
User API
  ->
IAM service
  ->
User database
  ->
Email verification
  ->
Audit log
```

Later phases add:

```text
password hashing
email OTP
login
JWT
refresh tokens
roles
permissions
audit logging
```

---

# 12. IAM Connection With This Phase

Real IAM registration includes:

```text
unique identity
credential creation
profile creation
verification status
audit trail
risk checks
terms acceptance
tenant association
```

Our current MiniAuth:

```text
creates identity
checks duplicate email
validates password
stores user in memory
```

Difference:

| Real IAM | Current Phase |
|---|---|
| Database storage | In-memory HashMap |
| Password hash | Plain password |
| Email verification | Not yet |
| Audit log | Not yet |
| MFA | Not yet |
| Tenant | Not yet |
| Risk checks | Not yet |

Next phase fixes the most important security issue:

```text
plain password storage
```

---

# 13. Production-Grade Concepts

Production registration must consider:

```text
password hashing
unique database constraint
email verification
rate limiting
bot protection
audit logging
PII protection
GDPR deletion
tenant isolation
monitoring
fraud detection
```

## Database Design

Production table:

```text
users
-----
id UUID PRIMARY KEY
username VARCHAR
email VARCHAR UNIQUE
password_hash VARCHAR
enabled BOOLEAN
email_verified BOOLEAN
created_at TIMESTAMP
updated_at TIMESTAMP
```

Important:

```text
Email uniqueness must be enforced by database unique index.
Application-level check alone is not enough.
```

Why?

Two requests can arrive at the same time:

```text
Request A checks email -> not exists
Request B checks email -> not exists
Both insert
Duplicate created
```

Database unique constraint prevents this.

---

# 14. Scalability Discussion

At small scale:

```text
HashMap in memory is enough for learning
```

At production scale:

```text
PostgreSQL primary database
Redis cache for sessions
Kafka audit events
email verification queue
rate limiter
monitoring
```

Signup bottlenecks:

```text
database writes
unique email constraint contention
email sending latency
password hashing CPU
bot/fraud attempts
rate limiter pressure
```

Scaling approach:

```text
1. Keep registration synchronous for user creation.
2. Send verification email asynchronously.
3. Use queue for audit/security events.
4. Add rate limiter by IP/email/device.
5. Add metrics for success/failure.
6. Add alerting for signup abuse spike.
```

---

# 15. Interview Notes

## Q1. Why do we need registration service separate from repository?

Because repository only stores data.

Registration service handles business rules:

```text
validation
duplicate checks
password policy
user creation
response
```

This separation makes code easier to test and modify.

---

## Q2. Why is checking duplicate email in application not enough?

Because of race conditions.

Two requests can pass the check at the same time.

Production fix:

```text
unique index on email in database
transaction handling
catch duplicate key exception
```

---

## Q3. Should we store plain passwords?

No.

Never store plain passwords in production.

This phase stores plain password only to keep Phase 001 simple.

Next phase adds:

```text
BCrypt password hashing
```

---

## Q4. What should registration return?

Return safe data only:

```text
userId
success message
basic profile
```

Never return:

```text
password
password hash
security tokens unless login flow
internal flags
```

---

## Q5. What are common signup abuse protections?

```text
IP rate limiting
email rate limiting
captcha
device fingerprinting
temporary blocklist
audit log
security monitoring
```

---

# 16. Common Bugs

## Bug 1 — Password returned in response

Bad:

```text
RegisterResponse includes password
```

Fix:

```text
Never expose credential fields.
```

---

## Bug 2 — Duplicate emails with different case

Problem:

```text
Mohamed@Example.com
mohamed@example.com
```

Could be treated as different emails.

Fix:

```text
normalize email to lowercase
```

---

## Bug 3 — Weak validation

Problem:

```text
empty username
invalid email
short password
```

Fix:

```text
central validation in service
```

---

## Bug 4 — Race condition on duplicate check

Problem:

```text
existsByEmail -> false
save -> duplicate
```

Fix in production:

```text
database unique index
transaction
catch duplicate key exception
```

---

## Bug 5 — Plain password storage

Problem:

```text
password stored directly
```

Fix:

```text
hash password with BCrypt/Argon2
```

---

# 17. Security Warning

This phase intentionally stores password as plain text only for learning.

Never do this in production.

Bad:

```text
password = StrongPass123
```

Good:

```text
passwordHash = BCrypt.hash(StrongPass123)
```

Next phase fixes this.

---

# 18. Next Step

Next file:

```text
002_Password_Hashing_BCrypt.md
```

In the next phase, we replace plain password storage with password hashing.

Architecture will evolve from:

```text
Register request
  -> validate
  -> store plain password
```

to:

```text
Register request
  -> validate
  -> hash password with BCrypt
  -> store password hash only
```

This is where MiniAuth starts becoming security-aware.
