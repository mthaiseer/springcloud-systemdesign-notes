# MiniAuth / IAM Phase 15 — Login Rate Limiting

> NOTE:
>
> This phase includes:
>
> - Clickable index
> - Step-by-step Java code comments
> - Production-style formatting
> - Brute-force protection
> - Per-email and per-IP rate limiting
> - Temporary lockout
> - Complete runnable driver

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
- [9. Login Rate Limit Flow](#9-login-rate-limit-flow)
- [10. Complete Java Code](#10-complete-java-code)
  - [10.1 LoginAttempt.java](#101-loginattemptjava)
  - [10.2 LoginAttemptStore.java](#102-loginattemptstorejava)
  - [10.3 LoginRateLimitDecision.java](#103-loginratelimitdecisionjava)
  - [10.4 LoginRateLimiter.java](#104-loginratelimiterjava)
  - [10.5 LoginRequest.java](#105-loginrequestjava)
  - [10.6 LoginResponse.java](#106-loginresponsejava)
  - [10.7 AuthenticationService.java](#107-authenticationservicejava)
  - [10.8 Phase015Driver.java](#108-phase015driverjava)
- [11. How To Run](#11-how-to-run)
- [12. Dry Run](#12-dry-run)
- [13. DSA / CP Concepts Used](#13-dsa--cp-concepts-used)
- [14. System Design Relevance](#14-system-design-relevance)
- [15. Production-Grade Concepts](#15-production-grade-concepts)
- [16. Common Bugs](#16-common-bugs)
- [17. Interview Notes](#17-interview-notes)
- [18. Next Step](#18-next-step)

---

# 1. Goal

In this phase, we protect login from brute-force attacks.

Before this phase:

```text
attacker can try unlimited passwords
```

After this phase:

```text
too many failed login attempts -> temporary lockout
```

Main objective:

```text
Rate limit login attempts by email and IP.
```

---

# 2. Why This Phase Matters

Login is one of the most attacked endpoints.

Attackers try:

```text
password guessing
credential stuffing
brute-force attacks
bot login attempts
```

Without rate limiting, the system allows unlimited guesses.

A secure IAM system should limit login attempts.

---

# 3. What We Built Previously

Previous phases added:

```text
registration
password hashing
login authentication
sessions
JWT
refresh token
logout
RBAC
PBAC
authorization filter
multi-tenancy
email verification
password reset
```

Now we protect the login endpoint itself.

---

# 4. Previous Limitation

Previously:

```text
login(email, password)
```

would always check password.

Problem:

```text
attacker can call login thousands of times
```

Need:

```text
track failed attempts
block temporarily
```

---

# 5. What We Build

We add:

```text
LoginAttempt
LoginAttemptStore
LoginRateLimitDecision
LoginRateLimiter
```

Login flow becomes:

```text
login request
    -> check rate limiter
    -> if blocked, reject
    -> verify password
    -> on failure, record failed attempt
    -> on success, clear attempts
```

---

# 6. Current Architecture

```text
+-------------------------+
| Login Request           |
| email + ip              |
+------------+------------+
             |
             v
+-------------------------+
| LoginRateLimiter        |
| check email/IP attempts |
+------------+------------+
             |
   allowed?  | blocked?
             |
             v
+-------------------------+
| AuthenticationService   |
| verify password         |
+------------+------------+
             |
             v
+-------------------------+
| record success/failure  |
+-------------------------+
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
                    ├── ratelimit/
                    │   ├── LoginAttempt.java
                    │   ├── LoginAttemptStore.java
                    │   ├── LoginRateLimitDecision.java
                    │   └── LoginRateLimiter.java
                    ├── dto/
                    │   ├── LoginRequest.java
                    │   └── LoginResponse.java
                    ├── service/
                    │   └── AuthenticationService.java
                    └── driver/
                        └── Phase015Driver.java
```

---

# 8. Core Concepts

## 8.1 Failed Attempt Counter

Track failed login attempts for:

```text
email
IP address
email + IP combination
```

---

## 8.2 Temporary Lockout

After too many failures:

```text
block login for 5 minutes
```

---

## 8.3 Reset On Success

If login succeeds:

```text
clear failed attempts
```

---

## 8.4 Generic Error Message

Do not say:

```text
too many attempts for this existing email
```

in a way that leaks whether email exists.

Use safe messages.

---

# 9. Login Rate Limit Flow

```text
Request comes in
    |
    v
Check if email/IP is blocked
    |
    +-- blocked -> reject
    |
    +-- allowed -> verify password
                      |
                      +-- success -> clear attempts
                      |
                      +-- failure -> increment attempts
```

---

# 10. Complete Java Code

---

## 10.1 `LoginAttempt.java`

```java
package com.miniauth.ratelimit;

import java.time.Instant;

public class LoginAttempt {

    private int failedCount;
    private Instant firstFailedAt;
    private Instant lockedUntil;

    public LoginAttempt() {

        // =====================================================
        // New attempt bucket starts with zero failed attempts.
        // =====================================================

        this.failedCount = 0;
        this.firstFailedAt = null;
        this.lockedUntil = null;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public Instant getFirstFailedAt() {
        return firstFailedAt;
    }

    public Instant getLockedUntil() {
        return lockedUntil;
    }

    public void recordFailure() {

        // =====================================================
        // First failure starts the observation window.
        // =====================================================

        if (failedCount == 0) {
            firstFailedAt = Instant.now();
        }

        failedCount++;
    }

    public void lockUntil(Instant lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public boolean isLocked() {
        return lockedUntil != null &&
                Instant.now().isBefore(lockedUntil);
    }

    public void reset() {
        failedCount = 0;
        firstFailedAt = null;
        lockedUntil = null;
    }
}
```

---

## 10.2 `LoginAttemptStore.java`

```java
package com.miniauth.ratelimit;

import java.util.HashMap;
import java.util.Map;

public class LoginAttemptStore {

    private final Map<String, LoginAttempt> attemptsByKey =
            new HashMap<>();

    public LoginAttempt getOrCreate(String key) {

        // =====================================================
        // key can be:
        // email:mohamed@example.com
        // ip:10.0.0.1
        // email-ip:mohamed@example.com:10.0.0.1
        // =====================================================

        return attemptsByKey.computeIfAbsent(
                key,
                ignored -> new LoginAttempt()
        );
    }

    public void reset(String key) {
        attemptsByKey.remove(key);
    }

    public int size() {
        return attemptsByKey.size();
    }
}
```

---

## 10.3 `LoginRateLimitDecision.java`

```java
package com.miniauth.ratelimit;

public class LoginRateLimitDecision {

    private final boolean allowed;
    private final String reason;

    private LoginRateLimitDecision(
            boolean allowed,
            String reason
    ) {
        this.allowed = allowed;
        this.reason = reason;
    }

    public static LoginRateLimitDecision allow() {
        return new LoginRateLimitDecision(
                true,
                "Login attempt allowed"
        );
    }

    public static LoginRateLimitDecision deny(String reason) {
        return new LoginRateLimitDecision(
                false,
                reason
        );
    }

    public boolean isAllowed() {
        return allowed;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "LoginRateLimitDecision{" +
                "allowed=" + allowed +
                ", reason='" + reason + '\'' +
                '}';
    }
}
```

---

## 10.4 `LoginRateLimiter.java`

```java
package com.miniauth.ratelimit;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class LoginRateLimiter {

    private final LoginAttemptStore store;
    private final int maxFailures;
    private final int lockMinutes;

    public LoginRateLimiter(
            LoginAttemptStore store,
            int maxFailures,
            int lockMinutes
    ) {
        this.store = store;
        this.maxFailures = maxFailures;
        this.lockMinutes = lockMinutes;
    }

    public LoginRateLimitDecision checkAllowed(
            String email,
            String ipAddress
    ) {

        // =====================================================
        // STEP 1: Build rate limit keys
        // =====================================================

        String emailKey = emailKey(email);
        String ipKey = ipKey(ipAddress);

        // =====================================================
        // STEP 2: Check email lock
        // =====================================================

        if (store.getOrCreate(emailKey).isLocked()) {
            return LoginRateLimitDecision.deny(
                    "Too many failed attempts for this account"
            );
        }

        // =====================================================
        // STEP 3: Check IP lock
        // =====================================================

        if (store.getOrCreate(ipKey).isLocked()) {
            return LoginRateLimitDecision.deny(
                    "Too many failed attempts from this IP"
            );
        }

        return LoginRateLimitDecision.allow();
    }

    public void recordFailure(
            String email,
            String ipAddress
    ) {

        // =====================================================
        // STEP 1: Increment failure counters
        // =====================================================

        LoginAttempt emailAttempt =
                store.getOrCreate(emailKey(email));

        LoginAttempt ipAttempt =
                store.getOrCreate(ipKey(ipAddress));

        emailAttempt.recordFailure();
        ipAttempt.recordFailure();

        // =====================================================
        // STEP 2: Lock if threshold exceeded
        // =====================================================

        if (emailAttempt.getFailedCount() >= maxFailures) {
            emailAttempt.lockUntil(
                    Instant.now().plus(
                            lockMinutes,
                            ChronoUnit.MINUTES
                    )
            );
        }

        if (ipAttempt.getFailedCount() >= maxFailures) {
            ipAttempt.lockUntil(
                    Instant.now().plus(
                            lockMinutes,
                            ChronoUnit.MINUTES
                    )
            );
        }
    }

    public void recordSuccess(
            String email,
            String ipAddress
    ) {

        // =====================================================
        // Successful login clears failed attempts.
        // =====================================================

        store.reset(emailKey(email));
        store.reset(ipKey(ipAddress));
    }

    private String emailKey(String email) {
        return "email:" + email.toLowerCase().trim();
    }

    private String ipKey(String ipAddress) {
        return "ip:" + ipAddress;
    }
}
```

---

## 10.5 `LoginRequest.java`

```java
package com.miniauth.dto;

public class LoginRequest {

    private final String email;
    private final String password;
    private final String ipAddress;

    public LoginRequest(
            String email,
            String password,
            String ipAddress
    ) {
        this.email = email;
        this.password = password;
        this.ipAddress = ipAddress;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
```

---

## 10.6 `LoginResponse.java`

```java
package com.miniauth.dto;

public class LoginResponse {

    private final boolean authenticated;
    private final String message;

    public LoginResponse(
            boolean authenticated,
            String message
    ) {
        this.authenticated = authenticated;
        this.message = message;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "authenticated=" + authenticated +
                ", message='" + message + '\'' +
                '}';
    }
}
```

---

## 10.7 `AuthenticationService.java`

```java
package com.miniauth.service;

import com.miniauth.dto.LoginRequest;
import com.miniauth.dto.LoginResponse;
import com.miniauth.ratelimit.LoginRateLimitDecision;
import com.miniauth.ratelimit.LoginRateLimiter;

public class AuthenticationService {

    private final LoginRateLimiter rateLimiter;

    public AuthenticationService(
            LoginRateLimiter rateLimiter
    ) {
        this.rateLimiter = rateLimiter;
    }

    public LoginResponse login(LoginRequest request) {

        // =====================================================
        // STEP 1: Check rate limiter before password verification
        // =====================================================

        LoginRateLimitDecision decision =
                rateLimiter.checkAllowed(
                        request.getEmail(),
                        request.getIpAddress()
                );

        if (!decision.isAllowed()) {
            return new LoginResponse(
                    false,
                    decision.getReason()
            );
        }

        // =====================================================
        // STEP 2: Simulate password verification
        // =====================================================
        //
        // In earlier phases, this calls PasswordEncoder.matches().
        // Here we isolate rate limiting behavior.
        // =====================================================

        boolean passwordCorrect =
                "StrongPass123".equals(
                        request.getPassword()
                );

        // =====================================================
        // STEP 3: Record success or failure
        // =====================================================

        if (passwordCorrect) {

            rateLimiter.recordSuccess(
                    request.getEmail(),
                    request.getIpAddress()
            );

            return new LoginResponse(
                    true,
                    "Login successful"
            );
        }

        rateLimiter.recordFailure(
                request.getEmail(),
                request.getIpAddress()
        );

        return new LoginResponse(
                false,
                "Invalid email or password"
        );
    }
}
```

---

## 10.8 `Phase015Driver.java`

```java
package com.miniauth.driver;

import com.miniauth.dto.LoginRequest;
import com.miniauth.dto.LoginResponse;
import com.miniauth.ratelimit.LoginAttemptStore;
import com.miniauth.ratelimit.LoginRateLimiter;
import com.miniauth.service.AuthenticationService;

public class Phase015Driver {

    public static void main(String[] args) {

        // =====================================================
        // STEP 1: Create rate limiter dependencies
        // =====================================================

        LoginAttemptStore store =
                new LoginAttemptStore();

        LoginRateLimiter rateLimiter =
                new LoginRateLimiter(
                        store,
                        3,
                        5
                );

        AuthenticationService authService =
                new AuthenticationService(rateLimiter);

        String email = "mohamed@example.com";
        String ip = "10.0.0.10";

        // =====================================================
        // STEP 2: Try wrong password multiple times
        // =====================================================

        for (int i = 1; i <= 4; i++) {

            LoginResponse response =
                    authService.login(
                            new LoginRequest(
                                    email,
                                    "WrongPass" + i,
                                    ip
                            )
                    );

            System.out.println();
            System.out.println("Attempt " + i);
            System.out.println(response);
        }

        // =====================================================
        // STEP 3: Try correct password after lock
        // =====================================================

        LoginResponse correctAfterLock =
                authService.login(
                        new LoginRequest(
                                email,
                                "StrongPass123",
                                ip
                        )
                );

        System.out.println();
        System.out.println("Correct password after lock:");
        System.out.println(correctAfterLock);

        // =====================================================
        // STEP 4: Different IP with same locked email
        // =====================================================

        LoginResponse differentIp =
                authService.login(
                        new LoginRequest(
                                email,
                                "StrongPass123",
                                "10.0.0.20"
                        )
                );

        System.out.println();
        System.out.println("Same email from different IP:");
        System.out.println(differentIp);

        System.out.println();
        System.out.println("Attempt store size:");
        System.out.println(store.size());
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
Phase015Driver
```

---

## Command Line

Compile:

```bash
javac -d out src/main/java/com/miniauth/**/*.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase015Driver
```

---

# 12. Dry Run

Configuration:

```text
maxFailures = 3
lockMinutes = 5
```

Attempts:

```text
Attempt 1 wrong password -> record failure
Attempt 2 wrong password -> record failure
Attempt 3 wrong password -> lock email/IP
Attempt 4 blocked
Correct password blocked while lock active
```

---

# 13. DSA / CP Concepts Used

## HashMap

Rate limiter uses:

```text
key -> LoginAttempt
```

Expected:

```text
O(1)
```

---

## Counter

Failed attempts use counter logic.

```text
failedCount++
```

---

## Time Window / TTL

Lock is valid until:

```text
lockedUntil
```

---

## State Machine

Login attempt state:

```text
NORMAL -> WARNING -> LOCKED -> RESET
```

---

# 14. System Design Relevance

Login rate limiting protects:

```text
Auth service
IAM
OAuth2 login
Admin portals
Banking apps
SaaS login
```

At scale:

```text
API Gateway
  -> Redis rate limiter
  -> Auth Service
```

---

# 15. Production-Grade Concepts

## Use Redis

In-memory limiter works only on one JVM.

Production should use:

```text
Redis
Lua script
atomic counters
TTL
```

---

## Multiple Dimensions

Rate limit by:

```text
email
IP
email + IP
device fingerprint
tenant
ASN/country
```

---

## Progressive Delay

Instead of immediate hard lock:

```text
1s delay
5s delay
30s delay
temporary lock
```

---

## Avoid Account Enumeration

Messages should not reveal too much.

---

## Add CAPTCHA / MFA

After suspicious attempts:

```text
captcha
MFA challenge
risk-based authentication
```

---

# 16. Common Bugs

## Bug 1 — Only Rate Limit By Email

Attackers can attack many emails from one IP.

Also rate limit by IP.

---

## Bug 2 — Only Rate Limit By IP

Attackers can use many IPs.

Also rate limit by email.

---

## Bug 3 — No TTL Cleanup

Attempt data can grow forever.

Use Redis TTL or scheduled cleanup.

---

## Bug 4 — Lockout Abuse

Attacker can intentionally lock victim account.

Mitigate with:

```text
risk scoring
captcha
progressive delay
MFA
```

---

# 17. Interview Notes

If interviewer asks:

```text
How do you protect login from brute force?
```

Answer:

```text
1. Track failed login attempts.
2. Rate limit by email and IP.
3. Lock temporarily after threshold.
4. Use generic error messages.
5. Reset counter on success.
6. Use Redis for distributed counters.
7. Add CAPTCHA/MFA for suspicious behavior.
8. Audit failed attempts.
9. Alert on abnormal patterns.
```

Strong follow-up:

```text
Login rate limiting must balance security with account lockout abuse prevention.
```

---

# 18. Next Step

Next file:

```text
016_Audit_Log.md
```

In the next phase, we add:

```text
security audit logs
login events
token events
permission changes
append-only audit trail
```
