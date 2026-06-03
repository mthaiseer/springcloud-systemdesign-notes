# MiniAuth / IAM Phase 14 — Password Reset Token

> NOTE:
>
> This phase includes:
>
> - Clickable index
> - Step-by-step Java code comments
> - Clean production-style formatting
> - Complete runnable driver
> - Token expiry + single-use reset flow
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
- [9. Password Reset Flow](#9-password-reset-flow)
- [10. Complete Java Code](#10-complete-java-code)
  - [10.1 User.java](#101-userjava)
  - [10.2 PasswordEncoder.java](#102-passwordencoderjava)
  - [10.3 PasswordResetToken.java](#103-passwordresettokenjava)
  - [10.4 UserRepository.java](#104-userrepositoryjava)
  - [10.5 PasswordResetTokenStore.java](#105-passwordresettokenstorejava)
  - [10.6 EmailService.java](#106-emailservicejava)
  - [10.7 PasswordResetService.java](#107-passwordresetservicejava)
  - [10.8 Phase014Driver.java](#108-phase014driverjava)
- [11. How To Run](#11-how-to-run)
- [12. Dry Run](#12-dry-run)
- [13. What Changed From Phase 13](#13-what-changed-from-phase-13)
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
forgot password
password reset token
reset password flow
```

Before this phase:

```text
user forgets password -> no recovery flow
```

After this phase:

```text
user requests password reset
system sends reset token
user submits new password
system updates password hash
token becomes invalid
```

Main objective:

```text
Allow secure password recovery without exposing old password.
```

---

# 2. Why This Phase Matters

Password reset is a critical IAM feature.

Almost every real system needs:

```text
Forgot password?
Reset password
Email reset link
Single-use token
Expiry
Password update
```

But password reset is security-sensitive.

A bad reset implementation can lead to:

```text
account takeover
token replay
email enumeration
credential compromise
```

---

# 3. What We Built Previously

## Phase 13

We built:

```text
Email Verification
```

That introduced:

```text
email tokens
expiry
single-use token behavior
email sending simulation
```

Phase 14 uses a similar idea but for password reset.

---

# 4. Previous Limitation

Previously:

```text
if user forgets password
there is no recovery path
```

Also:

```text
admin/manual password reset is not scalable
```

We need a self-service secure flow.

---

# 5. What We Build

We add:

```text
PasswordResetToken
PasswordResetTokenStore
PasswordResetService
EmailService
```

Flow:

```text
forgot password request
    -> find user by email
    -> generate reset token
    -> send reset link
    -> user submits token + new password
    -> validate token
    -> hash new password
    -> update user password hash
    -> invalidate token
```

---

# 6. Current Architecture

```text
+-----------------------------+
| Forgot Password Request     |
| email                       |
+--------------+--------------+
               |
               v
+-----------------------------+
| PasswordResetService        |
| - generate token            |
| - store token               |
| - send email                |
+--------------+--------------+
               |
               v
+-----------------------------+
| User clicks reset link      |
| token + new password        |
+--------------+--------------+
               |
               v
+-----------------------------+
| PasswordResetService        |
| - validate token            |
| - hash new password         |
| - update user               |
| - delete token              |
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
                    │   ├── User.java
                    │   └── PasswordResetToken.java
                    ├── repository/
                    │   └── UserRepository.java
                    ├── security/
                    │   └── PasswordEncoder.java
                    ├── reset/
                    │   ├── PasswordResetTokenStore.java
                    │   └── PasswordResetService.java
                    ├── email/
                    │   └── EmailService.java
                    └── driver/
                        └── Phase014Driver.java
```

---

# 8. Core Concepts

## 8.1 Password Reset Token

A reset token should be:

```text
random
hard to guess
single-use
short-lived
stored server-side
```

---

## 8.2 Reset Link

Example:

```text
https://app.example.com/reset-password?token=abc123
```

---

## 8.3 Token Expiry

Password reset tokens should expire quickly.

Common values:

```text
10 minutes
15 minutes
30 minutes
1 hour
```

---

## 8.4 Single Use

Once a token resets password:

```text
delete token
```

Replay should fail.

---

# 9. Password Reset Flow

```text
1. User submits email.
2. System finds user.
3. System creates reset token.
4. System sends email.
5. User clicks reset link.
6. User submits new password.
7. System validates token.
8. System hashes new password.
9. System updates user password hash.
10. System deletes reset token.
```

Important:

```text
forgot password response should not reveal if email exists.
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
    private String passwordHash;
    private final Instant createdAt;

    public User(
            String email,
            String passwordHash
    ) {

        // =====================================================
        // STEP 1: Assign stable user id
        // =====================================================

        this.id = UUID.randomUUID().toString();

        // =====================================================
        // STEP 2: Normalize email
        // =====================================================

        this.email = email.toLowerCase().trim();

        // =====================================================
        // STEP 3: Store only password hash
        // =====================================================

        this.passwordHash = passwordHash;

        // =====================================================
        // STEP 4: Store account creation time
        // =====================================================

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

    public void updatePasswordHash(
            String newPasswordHash
    ) {

        // =====================================================
        // Password reset updates credential atomically in memory.
        // In production this should be a DB transaction.
        // =====================================================

        this.passwordHash = newPasswordHash;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
```

---

## 10.2 `PasswordEncoder.java`

```java
package com.miniauth.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordEncoder {

    private final SecureRandom random = new SecureRandom();

    public String hash(String rawPassword) {

        // =====================================================
        // STEP 1: Validate password input
        // =====================================================

        if (rawPassword == null ||
                rawPassword.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters"
            );
        }

        // =====================================================
        // STEP 2: Generate unique salt
        // =====================================================

        String salt = generateSalt();

        // =====================================================
        // STEP 3: Hash salt + password
        // =====================================================

        String hash = sha256(salt + rawPassword);

        // =====================================================
        // STEP 4: Store salt and hash together
        // =====================================================

        return salt + ":" + hash;
    }

    public boolean matches(
            String rawPassword,
            String storedHash
    ) {

        if (rawPassword == null ||
                storedHash == null) {
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
                    digest.digest(
                            input.getBytes(StandardCharsets.UTF_8)
                    );

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hashedBytes);

        } catch (Exception ex) {
            throw new RuntimeException(
                    "Hashing failed",
                    ex
            );
        }
    }
}
```

---

## 10.3 `PasswordResetToken.java`

```java
package com.miniauth.model;

import java.time.Instant;

public class PasswordResetToken {

    private final String token;
    private final String userId;
    private final Instant createdAt;
    private final Instant expiresAt;
    private boolean used;

    public PasswordResetToken(
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

    private final Map<String, User> usersById =
            new HashMap<>();

    private final Map<String, User> usersByEmail =
            new HashMap<>();

    public void save(User user) {

        usersById.put(user.getId(), user);
        usersByEmail.put(user.getEmail(), user);
    }

    public Optional<User> findById(String userId) {
        return Optional.ofNullable(
                usersById.get(userId)
        );
    }

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(
                usersByEmail.get(
                        email.toLowerCase().trim()
                )
        );
    }
}
```

---

## 10.5 `PasswordResetTokenStore.java`

```java
package com.miniauth.reset;

import com.miniauth.model.PasswordResetToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PasswordResetTokenStore {

    private final Map<String, PasswordResetToken> tokens =
            new HashMap<>();

    public void save(PasswordResetToken token) {

        // token value -> token metadata

        tokens.put(token.getToken(), token);
    }

    public Optional<PasswordResetToken> findByToken(
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

```java
package com.miniauth.email;

public class EmailService {

    public void sendPasswordResetEmail(
            String email,
            String resetLink
    ) {

        // =====================================================
        // In this mini project, email sending is simulated.
        // Production would use SES, SendGrid, SMTP, etc.
        // =====================================================

        System.out.println();
        System.out.println("PASSWORD RESET EMAIL SENT");
        System.out.println("To: " + email);
        System.out.println("Subject: Reset your password");
        System.out.println("Link: " + resetLink);
    }
}
```

---

## 10.7 `PasswordResetService.java`

```java
package com.miniauth.reset;

import com.miniauth.email.EmailService;
import com.miniauth.model.PasswordResetToken;
import com.miniauth.model.User;
import com.miniauth.repository.UserRepository;
import com.miniauth.security.PasswordEncoder;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenStore tokenStore;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SecureRandom random = new SecureRandom();

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenStore tokenStore,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.tokenStore = tokenStore;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public String requestPasswordReset(String email) {

        // =====================================================
        // STEP 1: Avoid email enumeration
        // =====================================================
        //
        // Production API should always return a generic message:
        //
        // "If this email exists, reset instructions were sent."
        //
        // For the mini project, we return token for driver demo.
        // =====================================================

        if (email == null || email.isBlank()) {
            return null;
        }

        User user =
                userRepository.findByEmail(email)
                        .orElse(null);

        if (user == null) {

            // Do not reveal that the email does not exist.

            return null;
        }

        // =====================================================
        // STEP 2: Generate secure reset token
        // =====================================================

        String tokenValue = generateSecureToken();

        // =====================================================
        // STEP 3: Create token metadata
        // =====================================================

        Instant createdAt = Instant.now();
        Instant expiresAt =
                createdAt.plus(15, ChronoUnit.MINUTES);

        PasswordResetToken token =
                new PasswordResetToken(
                        tokenValue,
                        user.getId(),
                        createdAt,
                        expiresAt
                );

        // =====================================================
        // STEP 4: Store token server-side
        // =====================================================

        tokenStore.save(token);

        // =====================================================
        // STEP 5: Send reset link
        // =====================================================

        String resetLink =
                "https://app.example.com/reset-password?token=" +
                        tokenValue;

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                resetLink
        );

        return tokenValue;
    }

    public boolean resetPassword(
            String tokenValue,
            String newPassword
    ) {

        // =====================================================
        // STEP 1: Validate input
        // =====================================================

        if (tokenValue == null ||
                tokenValue.isBlank()) {
            return false;
        }

        if (newPassword == null ||
                newPassword.length() < 8) {
            return false;
        }

        // =====================================================
        // STEP 2: Load reset token
        // =====================================================

        PasswordResetToken token =
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
        // STEP 4: Load user
        // =====================================================

        User user =
                userRepository.findById(token.getUserId())
                        .orElse(null);

        if (user == null) {
            tokenStore.delete(tokenValue);
            return false;
        }

        // =====================================================
        // STEP 5: Hash new password
        // =====================================================

        String newPasswordHash =
                passwordEncoder.hash(newPassword);

        // =====================================================
        // STEP 6: Update user password hash
        // =====================================================

        user.updatePasswordHash(newPasswordHash);

        // =====================================================
        // STEP 7: Invalidate token
        // =====================================================

        token.markUsed();
        tokenStore.delete(tokenValue);

        return true;
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

## 10.8 `Phase014Driver.java`

```java
package com.miniauth.driver;

import com.miniauth.email.EmailService;
import com.miniauth.model.User;
import com.miniauth.repository.UserRepository;
import com.miniauth.reset.PasswordResetService;
import com.miniauth.reset.PasswordResetTokenStore;
import com.miniauth.security.PasswordEncoder;

public class Phase014Driver {

    public static void main(String[] args) {

        // =====================================================
        // STEP 1: Create dependencies
        // =====================================================

        UserRepository userRepository =
                new UserRepository();

        PasswordResetTokenStore tokenStore =
                new PasswordResetTokenStore();

        PasswordEncoder passwordEncoder =
                new PasswordEncoder();

        EmailService emailService =
                new EmailService();

        PasswordResetService resetService =
                new PasswordResetService(
                        userRepository,
                        tokenStore,
                        passwordEncoder,
                        emailService
                );

        // =====================================================
        // STEP 2: Create existing user
        // =====================================================

        String oldPasswordHash =
                passwordEncoder.hash("OldPass123");

        User user =
                new User(
                        "mohamed@example.com",
                        oldPasswordHash
                );

        userRepository.save(user);

        System.out.println("User before password reset:");
        System.out.println(user);

        // =====================================================
        // STEP 3: Request password reset
        // =====================================================

        String resetToken =
                resetService.requestPasswordReset(
                        "mohamed@example.com"
                );

        System.out.println();
        System.out.println("Token store size:");
        System.out.println(tokenStore.size());

        // =====================================================
        // STEP 4: Reset password using token
        // =====================================================

        boolean resetSuccess =
                resetService.resetPassword(
                        resetToken,
                        "NewStrongPass123"
                );

        System.out.println();
        System.out.println("Password reset success?");
        System.out.println(resetSuccess);

        // =====================================================
        // STEP 5: Verify old password fails and new password works
        // =====================================================

        System.out.println();
        System.out.println("Old password still works?");
        System.out.println(
                passwordEncoder.matches(
                        "OldPass123",
                        user.getPasswordHash()
                )
        );

        System.out.println();
        System.out.println("New password works?");
        System.out.println(
                passwordEncoder.matches(
                        "NewStrongPass123",
                        user.getPasswordHash()
                )
        );

        System.out.println();
        System.out.println("Token store size after reset:");
        System.out.println(tokenStore.size());

        // =====================================================
        // STEP 6: Replay same reset token
        // =====================================================

        boolean replaySuccess =
                resetService.resetPassword(
                        resetToken,
                        "AnotherPass123"
                );

        System.out.println();
        System.out.println("Replay same reset token success?");
        System.out.println(replaySuccess);
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
Phase014Driver
```

---

## Command Line

Compile:

```bash
javac -d out src/main/java/com/miniauth/**/*.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase014Driver
```

---

# 12. Dry Run

Existing user:

```text
email = mohamed@example.com
password = OldPass123
```

Forgot password:

```text
requestPasswordReset(email)
```

System creates:

```text
reset token
expiresAt = now + 15 minutes
```

User resets password:

```text
resetPassword(token, NewStrongPass123)
```

Checks:

```text
token exists
token not expired
token not used
user exists
new password valid
```

Then:

```text
password hash updated
token deleted
```

Replay:

```text
same token again
```

Result:

```text
fails
```

---

# 13. What Changed From Phase 13

## Phase 13

```text
verify email ownership
```

## Phase 14

```text
recover account by resetting password
```

New classes:

```text
PasswordResetToken
PasswordResetTokenStore
PasswordResetService
```

---

# 14. What This Phase Does NOT Do Yet

This phase does not yet include:

```text
reset token hashing
rate limiting
captcha
audit logs
email provider integration
invalidate all sessions after reset
notify user after password reset
MFA challenge before reset
```

These come later.

---

# 15. DSA / CP Concepts Used

## HashMap

Token lookup:

```text
token -> PasswordResetToken
```

Expected complexity:

```text
O(1)
```

---

## State Machine

Token lifecycle:

```text
ACTIVE -> USED
ACTIVE -> EXPIRED
```

---

## TTL

Reset token is valid only until:

```text
expiresAt
```

---

## Random Token Generation

SecureRandom creates high-entropy token.

---

# 16. System Design Relevance

Password reset is part of:

```text
IAM
Auth0
Okta
Keycloak
AWS Cognito
banking apps
SaaS platforms
```

HLD:

```text
Client
  -> Auth Service
  -> User DB
  -> Reset Token Store
  -> Email Provider
```

At scale:

```text
Auth Service
  -> Kafka reset email event
  -> Email Worker
  -> SES/SendGrid
```

---

# 17. Production-Grade Concepts

## Generic Response

Do not reveal email existence.

Return:

```text
If this email exists, reset instructions were sent.
```

---

## Store Token Hash

Production should store:

```text
hash(resetToken)
```

not raw token.

---

## Invalidate Sessions

After password reset:

```text
revoke refresh tokens
logout all devices
```

---

## Notify User

After successful reset:

```text
send security notification email
```

---

## Rate Limit

Limit reset requests by:

```text
email
IP
device
tenant
```

---

# 18. Common Bugs

## Bug 1 — Token Reuse

Bad:

```text
same reset token works multiple times
```

Good:

```text
single-use token
```

---

## Bug 2 — No Expiry

Bad:

```text
reset token valid forever
```

Good:

```text
short expiry
```

---

## Bug 3 — Email Enumeration

Bad:

```text
email not found
```

Good:

```text
generic response
```

---

## Bug 4 — Old Sessions Still Active

After password reset, production should revoke active sessions.

---

# 19. Interview Notes

If interviewer asks:

```text
How do you design forgot password?
```

Answer:

```text
1. User submits email.
2. Always return generic response.
3. If user exists, generate secure random token.
4. Store token hash with expiry.
5. Send reset link by email.
6. User submits token + new password.
7. Validate token exists, not expired, not used.
8. Hash new password.
9. Update password.
10. Delete token and revoke sessions.
```

Strong follow-up:

```text
Password reset is an account takeover path, so it must be rate-limited, audited, and single-use.
```

---

# 20. Next Step

Next file:

```text
015_Login_Rate_Limiting.md
```

In the next phase, we add:

```text
login attempt counter
IP/user rate limiting
temporary lockout
brute-force protection
```
