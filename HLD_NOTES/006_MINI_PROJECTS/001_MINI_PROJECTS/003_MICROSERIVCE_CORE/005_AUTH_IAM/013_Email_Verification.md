# MiniAuth / IAM Phase 13 — Email Verification

> NOTE:
>
> This phase includes:
>
> - Clickable index
> - Step-by-step Java code comments
> - Clean production-style formatting
> - Logic before important classes
> - Driver class with complete runnable flow
> - DSA / CP and system design relevance

---

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why This Phase Matters](#2-why-this-phase-matters)
- [3. What We Built Previously](#3-what-we-built-previously)
- [4. Previous Limitation](#4-previous-limitation)
- [5. What We Build](#5-what-we-build)
- [6. Current Architecture](#6-current-architecture)
- [7. Folder Structure](#7-folder-structure)
- [8. Core Concepts](#8-core-concepts)
- [9. Email Verification Flow](#9-email-verification-flow)
- [10. Complete Java Code](#10-complete-java-code)
  - [10.1 UserStatus.java](#101-userstatusjava)
  - [10.2 User.java](#102-userjava)
  - [10.3 EmailVerificationToken.java](#103-emailverificationtokenjava)
  - [10.4 UserRepository.java](#104-userrepositoryjava)
  - [10.5 EmailVerificationTokenStore.java](#105-emailverificationtokenstorejava)
  - [10.6 EmailService.java](#106-emailservicejava)
  - [10.7 EmailVerificationService.java](#107-emailverificationservicejava)
  - [10.8 Phase013Driver.java](#108-phase013driverjava)
- [11. How To Run](#11-how-to-run)
- [12. Dry Run](#12-dry-run)
- [13. What Changed From Phase 12](#13-what-changed-from-phase-12)
- [14. What This Phase Does NOT Do Yet](#14-what-this-phase-does-not-do-yet)
- [15. DSA / CP Concepts Used](#15-dsa--cp-concepts-used)
- [16. System Design Relevance](#16-system-design-relevance)
- [17. Production-Grade Concepts](#17-production-grade-concepts)
- [18. Common Bugs](#18-common-bugs)
- [19. Interview Notes](#19-interview-notes)
- [20. Next Step](#20-next-step)

---

# 1. Goal

In this phase, we add:

```text
email verification
```

Before this phase:

```text
user can register immediately as active
```

After this phase:

```text
user registers as PENDING_VERIFICATION
verification email is sent
user clicks token
account becomes ACTIVE
```

Main objective:

```text
Verify that the user controls the email address.
```

---

# 2. Why This Phase Matters

Email verification protects the system from:

```text
fake email registration
typo email registration
account takeover confusion
spam accounts
unverified identities
```

Many IAM systems require email verification before allowing:

```text
login
password reset
sensitive actions
team invitation
billing operations
```

---

# 3. What We Built Previously

Previous phases built:

```text
registration
password hashing
login
sessions
JWT
refresh token
logout
RBAC
PBAC
authorization filter
multi-tenant model
```

Now we improve account lifecycle.

---

# 4. Previous Limitation

Previously, user registration directly created a usable user.

Problem:

```text
mohamed@example.com may not actually belong to the registrant
```

So we need:

```text
email ownership verification
```

---

# 5. What We Build

We add:

```text
UserStatus
EmailVerificationToken
EmailVerificationTokenStore
EmailService
EmailVerificationService
```

Flow:

```text
register user
    -> status = PENDING_VERIFICATION
    -> generate verification token
    -> send verification email
    -> user clicks token
    -> token validated
    -> user status = ACTIVE
```

---

# 6. Current Architecture

```text
+----------------------------+
| User Registration          |
+-------------+--------------+
              |
              v
+----------------------------+
| User created as PENDING    |
+-------------+--------------+
              |
              v
+----------------------------+
| EmailVerificationService   |
| - generate token           |
| - store token              |
| - send email               |
+-------------+--------------+
              |
              v
+----------------------------+
| User clicks token          |
+-------------+--------------+
              |
              v
+----------------------------+
| Verify token               |
| - exists?                  |
| - expired?                 |
| - already used?            |
+-------------+--------------+
              |
              v
+----------------------------+
| User becomes ACTIVE        |
+----------------------------+
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
                    │   ├── UserStatus.java
                    │   └── EmailVerificationToken.java
                    ├── repository/
                    │   └── UserRepository.java
                    ├── verification/
                    │   ├── EmailVerificationTokenStore.java
                    │   └── EmailVerificationService.java
                    ├── email/
                    │   └── EmailService.java
                    └── driver/
                        └── Phase013Driver.java
```

---

# 8. Core Concepts

## 8.1 Pending User

A newly registered user starts as:

```text
PENDING_VERIFICATION
```

They become active only after token verification.

---

## 8.2 Verification Token

A verification token should be:

```text
random
hard to guess
single-use
short-lived
stored server-side
```

---

## 8.3 Token Expiry

Verification tokens should expire.

Example:

```text
15 minutes
1 hour
24 hours
```

---

## 8.4 Single Use

After token is used:

```text
mark used
delete token
```

This prevents replay.

---

# 9. Email Verification Flow

```text
1. User registers.
2. System creates user with PENDING_VERIFICATION.
3. System generates verification token.
4. System sends verification link.
5. User opens link.
6. System validates token.
7. System activates user.
8. Token is marked used or deleted.
```

Example link:

```text
https://app.example.com/verify-email?token=abc123
```

---

# 10. Complete Java Code

---

## 10.1 `UserStatus.java`

```java
package com.miniauth.model;

public enum UserStatus {

    // User exists but email is not verified yet.
    PENDING_VERIFICATION,

    // User email is verified and account can be used.
    ACTIVE,

    // User account is disabled manually or by policy.
    DISABLED
}
```

---

## 10.2 `User.java`

### Logic before this class

`User` now has lifecycle state.

```java
package com.miniauth.model;

import java.time.Instant;
import java.util.UUID;

public class User {

    private final String id;
    private final String email;
    private UserStatus status;
    private final Instant createdAt;

    public User(String email) {

        // =====================================================
        // STEP 1: Assign stable user identity
        // =====================================================

        this.id = UUID.randomUUID().toString();

        // =====================================================
        // STEP 2: Normalize email
        // =====================================================

        this.email = email.toLowerCase().trim();

        // =====================================================
        // STEP 3: New users are not active immediately
        // =====================================================

        this.status = UserStatus.PENDING_VERIFICATION;

        // =====================================================
        // STEP 4: Store creation time
        // =====================================================

        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public void markEmailVerified() {

        // User becomes usable only after verification succeeds.

        this.status = UserStatus.ACTIVE;
    }

    public void disable() {
        this.status = UserStatus.DISABLED;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
```

---

## 10.3 `EmailVerificationToken.java`

### Logic before this class

This model represents one verification token.

```java
package com.miniauth.model;

import java.time.Instant;

public class EmailVerificationToken {

    private final String token;
    private final String userId;
    private final Instant createdAt;
    private final Instant expiresAt;
    private boolean used;

    public EmailVerificationToken(
            String token,
            String userId,
            Instant createdAt,
            Instant expiresAt
    ) {
        this.token = token;
        this.userId = userId;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.used = false;
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

    public boolean isUsed() {
        return used;
    }

    public void markUsed() {
        this.used = true;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isUsable() {
        return !used && !isExpired();
    }

    @Override
    public String toString() {
        return "EmailVerificationToken{" +
                "token='" + token + '\'' +
                ", userId='" + userId + '\'' +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", used=" + used +
                '}';
    }
}
```

---

## 10.4 `UserRepository.java`

```java
package com.miniauth.repository;

import com.miniauth.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserRepository {

    private final Map<String, User> usersById = new HashMap<>();
    private final Map<String, User> usersByEmail = new HashMap<>();

    public void save(User user) {

        // Store by id for token verification flow.

        usersById.put(user.getId(), user);

        // Store by normalized email for registration/login lookup.

        usersByEmail.put(user.getEmail(), user);
    }

    public Optional<User> findById(String userId) {
        return Optional.ofNullable(usersById.get(userId));
    }

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(
                usersByEmail.get(email.toLowerCase().trim())
        );
    }
}
```

---

## 10.5 `EmailVerificationTokenStore.java`

```java
package com.miniauth.verification;

import com.miniauth.model.EmailVerificationToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EmailVerificationTokenStore {

    private final Map<String, EmailVerificationToken> tokens =
            new HashMap<>();

    public void save(EmailVerificationToken token) {

        // tokenValue -> token metadata

        tokens.put(token.getToken(), token);
    }

    public Optional<EmailVerificationToken> findByToken(
            String token
    ) {
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

## 10.6 `EmailService.java`

### Logic before this class

This simulates sending email.

Production will use SendGrid, SES, Mailgun, SMTP, etc.

```java
package com.miniauth.email;

public class EmailService {

    public void sendVerificationEmail(
            String email,
            String verificationLink
    ) {

        // =====================================================
        // In this mini project, we print instead of sending email.
        // =====================================================

        System.out.println();
        System.out.println("EMAIL SENT");
        System.out.println("To: " + email);
        System.out.println("Subject: Verify your email");
        System.out.println("Link: " + verificationLink);
    }
}
```

---

## 10.7 `EmailVerificationService.java`

### Logic before this class

This service owns email verification lifecycle.

```java
package com.miniauth.verification;

import com.miniauth.email.EmailService;
import com.miniauth.model.EmailVerificationToken;
import com.miniauth.model.User;
import com.miniauth.repository.UserRepository;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

public class EmailVerificationService {

    private final EmailVerificationTokenStore tokenStore;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SecureRandom random = new SecureRandom();

    public EmailVerificationService(
            EmailVerificationTokenStore tokenStore,
            UserRepository userRepository,
            EmailService emailService
    ) {
        this.tokenStore = tokenStore;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public String createAndSendVerification(User user) {

        // =====================================================
        // STEP 1: Generate secure random token
        // =====================================================

        String tokenValue = generateSecureToken();

        // =====================================================
        // STEP 2: Create token metadata
        // =====================================================

        Instant createdAt = Instant.now();
        Instant expiresAt = createdAt.plus(24, ChronoUnit.HOURS);

        EmailVerificationToken token =
                new EmailVerificationToken(
                        tokenValue,
                        user.getId(),
                        createdAt,
                        expiresAt
                );

        // =====================================================
        // STEP 3: Store token server-side
        // =====================================================

        tokenStore.save(token);

        // =====================================================
        // STEP 4: Build verification link
        // =====================================================

        String verificationLink =
                "https://app.example.com/verify-email?token=" +
                        tokenValue;

        // =====================================================
        // STEP 5: Send email
        // =====================================================

        emailService.sendVerificationEmail(
                user.getEmail(),
                verificationLink
        );

        // Driver needs this token for demonstration.

        return tokenValue;
    }

    public boolean verifyEmail(String tokenValue) {

        // =====================================================
        // STEP 1: Validate token input
        // =====================================================

        if (tokenValue == null || tokenValue.isBlank()) {
            return false;
        }

        // =====================================================
        // STEP 2: Load token from store
        // =====================================================

        EmailVerificationToken token =
                tokenStore.findByToken(tokenValue)
                        .orElse(null);

        if (token == null) {
            return false;
        }

        // =====================================================
        // STEP 3: Reject used or expired token
        // =====================================================

        if (!token.isUsable()) {

            tokenStore.delete(tokenValue);
            return false;
        }

        // =====================================================
        // STEP 4: Load user attached to token
        // =====================================================

        User user =
                userRepository.findById(token.getUserId())
                        .orElse(null);

        if (user == null) {
            return false;
        }

        // =====================================================
        // STEP 5: Activate user
        // =====================================================

        user.markEmailVerified();

        // =====================================================
        // STEP 6: Mark token used and delete from store
        // =====================================================

        token.markUsed();
        tokenStore.delete(tokenValue);

        return true;
    }

    private String generateSecureToken() {

        // 32 bytes = 256-bit random token

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}
```

---

## 10.8 `Phase013Driver.java`

```java
package com.miniauth.driver;

import com.miniauth.email.EmailService;
import com.miniauth.model.User;
import com.miniauth.repository.UserRepository;
import com.miniauth.verification.EmailVerificationService;
import com.miniauth.verification.EmailVerificationTokenStore;

public class Phase013Driver {

    public static void main(String[] args) {

        // =====================================================
        // STEP 1: Create dependencies
        // =====================================================

        UserRepository userRepository =
                new UserRepository();

        EmailVerificationTokenStore tokenStore =
                new EmailVerificationTokenStore();

        EmailService emailService =
                new EmailService();

        EmailVerificationService verificationService =
                new EmailVerificationService(
                        tokenStore,
                        userRepository,
                        emailService
                );

        // =====================================================
        // STEP 2: Register user as pending
        // =====================================================

        User user = new User("mohamed@example.com");

        userRepository.save(user);

        System.out.println("User after registration:");
        System.out.println(user);

        // =====================================================
        // STEP 3: Create and send verification email
        // =====================================================

        String token =
                verificationService
                        .createAndSendVerification(user);

        System.out.println();
        System.out.println("Token store size:");
        System.out.println(tokenStore.size());

        // =====================================================
        // STEP 4: Simulate clicking verification link
        // =====================================================

        boolean verified =
                verificationService.verifyEmail(token);

        System.out.println();
        System.out.println("Verification success?");
        System.out.println(verified);

        System.out.println();
        System.out.println("User after verification:");
        System.out.println(user);

        System.out.println();
        System.out.println("Token store size after verification:");
        System.out.println(tokenStore.size());

        // =====================================================
        // STEP 5: Try replaying the same token
        // =====================================================

        boolean replay =
                verificationService.verifyEmail(token);

        System.out.println();
        System.out.println("Replay same token success?");
        System.out.println(replay);
    }
}
```

---

# 11. How To Run

## IntelliJ

1. Create Java project `MiniAuth`
2. Create packages shown above.
3. Add all classes.
4. Run:

```text
Phase013Driver
```

---

## Command Line

Compile:

```bash
javac -d out src/main/java/com/miniauth/**/*.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase013Driver
```

---

# 12. Dry Run

Initial user:

```text
email = mohamed@example.com
status = PENDING_VERIFICATION
```

Token created:

```text
token -> userId
expiresAt = now + 24 hours
used = false
```

User clicks link:

```text
verifyEmail(token)
```

Checks:

```text
token exists
token not expired
token not used
user exists
```

Then:

```text
user.status = ACTIVE
token deleted
```

Replay same token:

```text
token not found
verification fails
```

---

# 13. What Changed From Phase 12

## Phase 12

```text
tenant-aware users
tenant isolation
```

## Phase 13

```text
account lifecycle starts as pending
email verification activates account
```

New classes:

```text
UserStatus
EmailVerificationToken
EmailVerificationTokenStore
EmailVerificationService
EmailService
```

---

# 14. What This Phase Does NOT Do Yet

This phase does not yet include:

```text
resend verification email
email rate limiting
email templates
SMTP integration
SendGrid/AWS SES
verification throttling
verification audit logs
email change verification
tenant invitation verification
```

These come later.

---

# 15. DSA / CP Concepts Used

## HashMap

Token lookup:

```text
token -> EmailVerificationToken
```

Expected complexity:

```text
O(1)
```

---

## Random Token Generation

SecureRandom creates hard-to-guess token.

---

## TTL / Expiry

Verification token has:

```text
expiresAt
```

State:

```text
valid if now <= expiresAt
```

---

## State Machine

User lifecycle:

```text
PENDING_VERIFICATION -> ACTIVE
PENDING_VERIFICATION -> DISABLED
ACTIVE -> DISABLED
```

Token lifecycle:

```text
ACTIVE -> USED
ACTIVE -> EXPIRED
```

---

# 16. System Design Relevance

Email verification is used in:

```text
SaaS registration
banking onboarding
social apps
marketplaces
B2B tenant invitations
password reset confirmation
```

HLD:

```text
Client
  -> Auth Service
  -> User DB
  -> Token Store
  -> Email Provider
```

At scale:

```text
Auth Service
  -> DB transaction
  -> Kafka email event
  -> Email Worker
  -> SES/SendGrid
```

---

# 17. Production-Grade Concepts

## Store Token Hash

Production should store:

```text
hash(token)
```

not raw token.

Same reason as password/refresh token security.

---

## Send Email Asynchronously

Instead of sending inline:

```text
registration request waits for email provider
```

Use:

```text
Kafka / RabbitMQ / SQS
email worker
```

---

## Rate Limit Verification Emails

Prevent abuse:

```text
resend verification max 3 times/hour
```

---

## Expiry

Common expiry:

```text
15 minutes
1 hour
24 hours
```

---

## Idempotency

If user is already verified:

```text
return success or safe message
```

Do not leak unnecessary details.

---

# 18. Common Bugs

## Bug 1 — User Active Before Verification

Bad:

```text
new user status = ACTIVE
```

Good:

```text
new user status = PENDING_VERIFICATION
```

---

## Bug 2 — Token Never Expires

Bad:

```text
verification token valid forever
```

Good:

```text
expiresAt
```

---

## Bug 3 — Token Can Be Reused

Bad:

```text
same token verifies multiple times
```

Good:

```text
single use token
```

---

## Bug 4 — Logging Verification Tokens

Avoid logging real verification token in production logs.

---

# 19. Interview Notes

If interviewer asks:

```text
How do you design email verification?
```

Answer:

```text
1. Register user as PENDING_VERIFICATION.
2. Generate secure random token.
3. Store token with userId and expiry.
4. Send verification link via email.
5. User clicks link.
6. Validate token exists, not expired, not used.
7. Mark user ACTIVE.
8. Delete or mark token used.
9. Rate limit resend.
10. Send email asynchronously in production.
```

Strong follow-up:

```text
Verification token should be single-use, short-lived, and stored hashed in production.
```

---

# 20. Next Step

Next file:

```text
014_Password_Reset_Token.md
```

In the next phase, we add:

```text
forgot password
password reset token
reset token expiry
single-use reset flow
```
