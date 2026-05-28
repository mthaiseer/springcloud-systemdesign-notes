# MiniAuth / IAM Phase 17 — MFA OTP Login

> NOTE:
>
> This phase includes:
>
> - Clickable index
> - Step-by-step Java code comments
> - Production-style formatting
> - MFA / OTP login flow
> - OTP expiry + replay prevention
> - Complete runnable driver
> - Security and system design discussion

---

# 1. Goal

In this phase, we add:

```text
MFA (Multi-Factor Authentication)
OTP login verification
```

Before this phase:

```text
password alone authenticates user
```

After this phase:

```text
password verified
    -> OTP challenge generated
    -> user submits OTP
    -> final authentication succeeds
```

Main objective:

```text
Require second-factor verification after password login.
```

---

# 2. Why MFA Matters

Passwords can be:

```text
guessed
stolen
phished
reused
leaked
```

MFA adds another security layer.

Even if attacker knows password:

```text
OTP still required
```

---

# 3. MFA Architecture

```text
+----------------------+
| Password Login       |
+----------+-----------+
           |
           v
+----------------------+
| Generate OTP         |
| store temporary OTP  |
+----------+-----------+
           |
           v
+----------------------+
| Send OTP             |
| SMS / Email / App    |
+----------+-----------+
           |
           v
+----------------------+
| Verify OTP           |
+----------+-----------+
           |
           v
+----------------------+
| Authentication Done  |
+----------------------+
```

---

# 4. Folder Structure

```text
MiniAuth/
└── src/
    └── main/
        └── java/
            └── com/
                └── miniauth/
                    ├── mfa/
                    │   ├── OtpCode.java
                    │   ├── OtpStore.java
                    │   ├── OtpGenerator.java
                    │   ├── OtpService.java
                    │   └── MfaAuthenticationService.java
                    └── driver/
                        └── Phase017Driver.java
```

---

# 5. Core Concepts

## 5.1 OTP

OTP means:

```text
One-Time Password
```

Properties:

```text
short-lived
single-use
random
time-bound
```

---

## 5.2 MFA

Authentication requires:

```text
something user knows
+ something user has
```

Example:

```text
password + OTP
```

---

## 5.3 OTP Expiry

OTP should expire quickly.

Typical:

```text
30 seconds
60 seconds
5 minutes
```

---

## 5.4 Replay Protection

OTP should work only once.

After verification:

```text
mark used
delete
```

---

# 6. Complete Java Code

---

## 6.1 `OtpCode.java`

```java
package com.miniauth.mfa;

import java.time.Instant;

public class OtpCode {

    private final String userId;
    private final String code;
    private final Instant expiresAt;
    private boolean used;

    public OtpCode(
            String userId,
            String code,
            Instant expiresAt
    ) {

        // =====================================================
        // Store OTP metadata
        // =====================================================

        this.userId = userId;
        this.code = code;
        this.expiresAt = expiresAt;
        this.used = false;
    }

    public String getUserId() {
        return userId;
    }

    public String getCode() {
        return code;
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
        return "OtpCode{" +
                "userId='" + userId + '\'' +
                ", code='" + code + '\'' +
                ", expiresAt=" + expiresAt +
                ", used=" + used +
                '}';
    }
}
```

---

## 6.2 `OtpStore.java`

```java
package com.miniauth.mfa;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OtpStore {

    private final Map<String, OtpCode> otpByUserId =
            new HashMap<>();

    public void save(OtpCode otpCode) {

        // userId -> OTP

        otpByUserId.put(
                otpCode.getUserId(),
                otpCode
        );
    }

    public Optional<OtpCode> findByUserId(
            String userId
    ) {
        return Optional.ofNullable(
                otpByUserId.get(userId)
        );
    }

    public void delete(String userId) {
        otpByUserId.remove(userId);
    }

    public int size() {
        return otpByUserId.size();
    }
}
```

---

## 6.3 `OtpGenerator.java`

```java
package com.miniauth.mfa;

import java.security.SecureRandom;

public class OtpGenerator {

    private final SecureRandom random =
            new SecureRandom();

    public String generateOtp() {

        // =====================================================
        // Generate 6-digit OTP
        // =====================================================

        int value =
                100000 + random.nextInt(900000);

        return String.valueOf(value);
    }
}
```

---

## 6.4 `OtpService.java`

```java
package com.miniauth.mfa;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class OtpService {

    private final OtpStore otpStore;
    private final OtpGenerator otpGenerator;

    public OtpService(
            OtpStore otpStore,
            OtpGenerator otpGenerator
    ) {
        this.otpStore = otpStore;
        this.otpGenerator = otpGenerator;
    }

    public String generateOtpForUser(
            String userId
    ) {

        // =====================================================
        // STEP 1: Generate random OTP
        // =====================================================

        String code =
                otpGenerator.generateOtp();

        // =====================================================
        // STEP 2: Configure expiry
        // =====================================================

        Instant expiresAt =
                Instant.now()
                        .plus(5, ChronoUnit.MINUTES);

        // =====================================================
        // STEP 3: Create OTP object
        // =====================================================

        OtpCode otpCode =
                new OtpCode(
                        userId,
                        code,
                        expiresAt
                );

        // =====================================================
        // STEP 4: Save OTP
        // =====================================================

        otpStore.save(otpCode);

        // =====================================================
        // STEP 5: Simulate OTP sending
        // =====================================================

        System.out.println();
        System.out.println("OTP SENT");
        System.out.println("User: " + userId);
        System.out.println("OTP: " + code);

        return code;
    }

    public boolean verifyOtp(
            String userId,
            String submittedCode
    ) {

        // =====================================================
        // STEP 1: Load OTP
        // =====================================================

        OtpCode storedOtp =
                otpStore.findByUserId(userId)
                        .orElse(null);

        if (storedOtp == null) {
            return false;
        }

        // =====================================================
        // STEP 2: Validate usability
        // =====================================================

        if (!storedOtp.isUsable()) {

            otpStore.delete(userId);
            return false;
        }

        // =====================================================
        // STEP 3: Compare OTP values
        // =====================================================

        if (!storedOtp.getCode()
                .equals(submittedCode)) {
            return false;
        }

        // =====================================================
        // STEP 4: Prevent replay attack
        // =====================================================

        storedOtp.markUsed();

        otpStore.delete(userId);

        return true;
    }
}
```

---

## 6.5 `MfaAuthenticationService.java`

```java
package com.miniauth.mfa;

public class MfaAuthenticationService {

    private final OtpService otpService;

    public MfaAuthenticationService(
            OtpService otpService
    ) {
        this.otpService = otpService;
    }

    public boolean passwordLogin(
            String email,
            String password
    ) {

        // =====================================================
        // Simulate password validation
        // =====================================================

        return "StrongPass123".equals(password);
    }

    public void startMfaChallenge(
            String userId
    ) {

        // =====================================================
        // Generate and send OTP challenge
        // =====================================================

        otpService.generateOtpForUser(userId);
    }

    public boolean completeMfaLogin(
            String userId,
            String otp
    ) {

        // =====================================================
        // Verify OTP
        // =====================================================

        return otpService.verifyOtp(
                userId,
                otp
        );
    }
}
```

---

## 6.6 `Phase017Driver.java`

```java
package com.miniauth.driver;

import com.miniauth.mfa.MfaAuthenticationService;
import com.miniauth.mfa.OtpGenerator;
import com.miniauth.mfa.OtpService;
import com.miniauth.mfa.OtpStore;

public class Phase017Driver {

    public static void main(String[] args) {

        // =====================================================
        // STEP 1: Create MFA dependencies
        // =====================================================

        OtpStore otpStore =
                new OtpStore();

        OtpGenerator otpGenerator =
                new OtpGenerator();

        OtpService otpService =
                new OtpService(
                        otpStore,
                        otpGenerator
                );

        MfaAuthenticationService authService =
                new MfaAuthenticationService(
                        otpService
                );

        String userId = "user-123";

        // =====================================================
        // STEP 2: Simulate password login
        // =====================================================

        boolean passwordSuccess =
                authService.passwordLogin(
                        "mohamed@example.com",
                        "StrongPass123"
                );

        System.out.println("Password success?");
        System.out.println(passwordSuccess);

        if (!passwordSuccess) {
            return;
        }

        // =====================================================
        // STEP 3: Start MFA challenge
        // =====================================================

        String generatedOtp =
                otpService.generateOtpForUser(userId);

        System.out.println();
        System.out.println("OTP store size:");
        System.out.println(otpStore.size());

        // =====================================================
        // STEP 4: Wrong OTP attempt
        // =====================================================

        boolean wrongOtp =
                authService.completeMfaLogin(
                        userId,
                        "111111"
                );

        System.out.println();
        System.out.println("Wrong OTP success?");
        System.out.println(wrongOtp);

        // =====================================================
        // STEP 5: Correct OTP attempt
        // =====================================================

        boolean correctOtp =
                authService.completeMfaLogin(
                        userId,
                        generatedOtp
                );

        System.out.println();
        System.out.println("Correct OTP success?");
        System.out.println(correctOtp);

        // =====================================================
        // STEP 6: Replay same OTP
        // =====================================================

        boolean replayOtp =
                authService.completeMfaLogin(
                        userId,
                        generatedOtp
                );

        System.out.println();
        System.out.println("Replay same OTP success?");
        System.out.println(replayOtp);

        System.out.println();
        System.out.println("OTP store size:");
        System.out.println(otpStore.size());
    }
}
```

---

# 7. How To Run

## IntelliJ

1. Create Java project `MiniAuth`
2. Create packages shown above.
3. Add all classes.
4. Run:

```text
Phase017Driver
```

---

## Command Line

Compile:

```bash
javac -d out src/main/java/com/miniauth/**/*.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase017Driver
```

---

# 8. Dry Run

Login:

```text
password verified
```

OTP:

```text
generated
stored
expires after 5 minutes
```

Wrong OTP:

```text
fails
```

Correct OTP:

```text
succeeds
```

Replay same OTP:

```text
fails
```

---

# 9. DSA / CP Concepts Used

## HashMap

OTP lookup:

```text
userId -> OTP
```

Expected complexity:

```text
O(1)
```

---

## TTL / Expiry

OTP valid until:

```text
expiresAt
```

---

## State Machine

OTP lifecycle:

```text
ACTIVE -> USED
ACTIVE -> EXPIRED
```

---

# 10. System Design Relevance

MFA used in:

```text
banking
AWS
Google
GitHub
enterprise IAM
payment systems
```

Production architecture:

```text
Auth Service
  -> OTP Service
  -> Redis
  -> SMS/Email Provider
```

---

# 11. Production-Grade Concepts

## TOTP

Production apps often use:

```text
Google Authenticator
Authy
RFC 6238 TOTP
```

instead of email OTP.

---

## Redis

Store OTP in:

```text
Redis with TTL
```

instead of in-memory JVM.

---

## Rate Limiting

Protect OTP endpoints from:

```text
spam
brute force
OTP bombing
```

---

## OTP Attempt Limits

Limit wrong OTP submissions.

Example:

```text
max 5 attempts
```

---

## Device Trust

Trusted device can reduce MFA prompts.

---

# 12. Common Bugs

## Bug 1 — OTP Never Expires

Bad:

```text
OTP valid forever
```

Good:

```text
short expiry
```

---

## Bug 2 — OTP Reuse

Bad:

```text
same OTP works multiple times
```

Good:

```text
single-use OTP
```

---

## Bug 3 — Logging OTP

Never log real OTP in production.

---

## Bug 4 — Predictable OTP

Avoid:

```text
Random with weak seed
```

Use:

```text
SecureRandom
```

---

# 13. Interview Notes

If interviewer asks:

```text
How do you design MFA login?
```

Answer:

```text
1. Verify password first.
2. Generate short-lived OTP.
3. Store OTP with expiry.
4. Send via SMS/email/authenticator app.
5. Verify OTP.
6. Invalidate after use.
7. Limit OTP attempts.
8. Rate limit OTP generation.
9. Prefer TOTP or WebAuthn in production.
```

Strong follow-up:

```text
MFA significantly reduces account takeover risk even if passwords leak.
```

---

# 14. Next Step

Next file:

```text
018_Refresh_Token_Rotation.md
```

In the next phase, we add:

```text
refresh token rotation
refresh token replay detection
session hijack prevention
