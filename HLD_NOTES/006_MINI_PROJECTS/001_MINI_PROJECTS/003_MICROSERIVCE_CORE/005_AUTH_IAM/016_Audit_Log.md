# MiniAuth / IAM Phase 16 — Audit Log

> NOTE:
>
> This phase includes:
>
> - Clickable index
> - Step-by-step Java code comments
> - Production-style formatting
> - Security audit events
> - Append-only audit trail
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
- [9. Audit Log Flow](#9-audit-log-flow)
- [10. Complete Java Code](#10-complete-java-code)
  - [10.1 AuditEventType.java](#101-auditeventtypejava)
  - [10.2 AuditEvent.java](#102-auditeventjava)
  - [10.3 AuditLogStore.java](#103-auditlogstorejava)
  - [10.4 AuditLogService.java](#104-auditlogservicejava)
  - [10.5 AuthenticationService.java](#105-authenticationservicejava)
  - [10.6 PermissionService.java](#106-permissionservicejava)
  - [10.7 Phase016Driver.java](#107-phase016driverjava)
- [11. How To Run](#11-how-to-run)
- [12. Dry Run](#12-dry-run)
- [13. What Changed From Phase 15](#13-what-changed-from-phase-15)
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
security audit logging
```

Before this phase:

```text
security events happen but are not recorded
```

After this phase:

```text
login attempts
logout
password reset
permission changes
token revocation
are recorded as audit events
```

Main objective:

```text
Create an append-only audit trail for IAM security events.
```

---

# 2. Why This Phase Matters

IAM systems must answer:

```text
Who did what?
When?
From where?
Was it successful?
What changed?
```

Audit logs are important for:

```text
security investigation
compliance
fraud detection
incident response
admin accountability
```

---

# 3. What We Built Previously

Previous phases added:

```text
login
JWT
refresh tokens
logout
RBAC
PBAC
authorization filter
multi-tenancy
email verification
password reset
login rate limiting
```

Now we record important security actions.

---

# 4. Previous Limitation

Previously, if something suspicious happened:

```text
10 failed logins
password reset
permission grant
logout
```

the system had no durable record.

That is dangerous.

We need:

```text
audit trail
```

---

# 5. What We Build

We add:

```text
AuditEventType
AuditEvent
AuditLogStore
AuditLogService
```

We also simulate services that emit audit logs:

```text
AuthenticationService
PermissionService
```

---

# 6. Current Architecture

```text
+----------------------+
| IAM Operation        |
| login/reset/grant    |
+----------+-----------+
           |
           v
+----------------------+
| AuditLogService      |
| creates AuditEvent   |
+----------+-----------+
           |
           v
+----------------------+
| AuditLogStore        |
| append-only list     |
+----------------------+
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
                    ├── audit/
                    │   ├── AuditEventType.java
                    │   ├── AuditEvent.java
                    │   ├── AuditLogStore.java
                    │   └── AuditLogService.java
                    ├── service/
                    │   ├── AuthenticationService.java
                    │   └── PermissionService.java
                    └── driver/
                        └── Phase016Driver.java
```

---

# 8. Core Concepts

## 8.1 Audit Event

An audit event records a security-relevant action.

Example:

```text
LOGIN_SUCCESS
LOGIN_FAILURE
PASSWORD_RESET_REQUESTED
PERMISSION_GRANTED
TOKEN_REVOKED
```

---

## 8.2 Actor

Actor means:

```text
who performed the action
```

Example:

```text
userId
adminId
system
```

---

## 8.3 Target

Target means:

```text
what was affected
```

Example:

```text
target user
permission
token
tenant
```

---

## 8.4 Append-Only

Audit logs should generally be append-only.

Do not update old audit events.

Do not delete casually.

---

# 9. Audit Log Flow

```text
security action happens
    |
    v
build audit event
    |
    v
append to audit log
    |
    v
search later for investigation
```

---

# 10. Complete Java Code

---

## 10.1 `AuditEventType.java`

```java
package com.miniauth.audit;

public enum AuditEventType {

    LOGIN_SUCCESS,
    LOGIN_FAILURE,
    LOGOUT,

    PASSWORD_RESET_REQUESTED,
    PASSWORD_RESET_COMPLETED,

    EMAIL_VERIFICATION_SENT,
    EMAIL_VERIFICATION_COMPLETED,

    TOKEN_REFRESHED,
    TOKEN_REVOKED,

    PERMISSION_GRANTED,
    PERMISSION_REVOKED,

    ACCESS_DENIED
}
```

---

## 10.2 `AuditEvent.java`

```java
package com.miniauth.audit;

import java.time.Instant;
import java.util.UUID;

public class AuditEvent {

    private final String eventId;
    private final AuditEventType eventType;
    private final String actorId;
    private final String targetId;
    private final String ipAddress;
    private final boolean success;
    private final String details;
    private final Instant createdAt;

    public AuditEvent(
            AuditEventType eventType,
            String actorId,
            String targetId,
            String ipAddress,
            boolean success,
            String details
    ) {

        // =====================================================
        // STEP 1: Generate unique audit event id
        // =====================================================

        this.eventId = UUID.randomUUID().toString();

        // =====================================================
        // STEP 2: Store security event metadata
        // =====================================================

        this.eventType = eventType;
        this.actorId = actorId;
        this.targetId = targetId;
        this.ipAddress = ipAddress;
        this.success = success;
        this.details = details;

        // =====================================================
        // STEP 3: Timestamp event creation
        // =====================================================

        this.createdAt = Instant.now();
    }

    public String getEventId() {
        return eventId;
    }

    public AuditEventType getEventType() {
        return eventType;
    }

    public String getActorId() {
        return actorId;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getDetails() {
        return details;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "AuditEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType=" + eventType +
                ", actorId='" + actorId + '\'' +
                ", targetId='" + targetId + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", success=" + success +
                ", details='" + details + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
```

---

## 10.3 `AuditLogStore.java`

```java
package com.miniauth.audit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AuditLogStore {

    private final List<AuditEvent> events =
            new ArrayList<>();

    public void append(AuditEvent event) {

        // =====================================================
        // Append-only write.
        // Do not modify old events.
        // =====================================================

        events.add(event);
    }

    public List<AuditEvent> findAll() {
        return new ArrayList<>(events);
    }

    public List<AuditEvent> findByActorId(
            String actorId
    ) {
        return events.stream()
                .filter(event ->
                        actorId.equals(event.getActorId()))
                .collect(Collectors.toList());
    }

    public List<AuditEvent> findByType(
            AuditEventType type
    ) {
        return events.stream()
                .filter(event ->
                        event.getEventType() == type)
                .collect(Collectors.toList());
    }

    public int size() {
        return events.size();
    }
}
```

---

## 10.4 `AuditLogService.java`

```java
package com.miniauth.audit;

import java.util.List;

public class AuditLogService {

    private final AuditLogStore store;

    public AuditLogService(AuditLogStore store) {
        this.store = store;
    }

    public void record(
            AuditEventType eventType,
            String actorId,
            String targetId,
            String ipAddress,
            boolean success,
            String details
    ) {

        // =====================================================
        // STEP 1: Create normalized audit event
        // =====================================================

        AuditEvent event =
                new AuditEvent(
                        eventType,
                        actorId,
                        targetId,
                        ipAddress,
                        success,
                        details
                );

        // =====================================================
        // STEP 2: Append event to store
        // =====================================================

        store.append(event);
    }

    public List<AuditEvent> findAll() {
        return store.findAll();
    }

    public List<AuditEvent> findByActorId(String actorId) {
        return store.findByActorId(actorId);
    }

    public List<AuditEvent> findByType(AuditEventType type) {
        return store.findByType(type);
    }
}
```

---

## 10.5 `AuthenticationService.java`

```java
package com.miniauth.service;

import com.miniauth.audit.AuditEventType;
import com.miniauth.audit.AuditLogService;

public class AuthenticationService {

    private final AuditLogService auditLogService;

    public AuthenticationService(
            AuditLogService auditLogService
    ) {
        this.auditLogService = auditLogService;
    }

    public boolean login(
            String userId,
            String email,
            String password,
            String ipAddress
    ) {

        // =====================================================
        // STEP 1: Simulate password verification
        // =====================================================

        boolean success =
                "StrongPass123".equals(password);

        // =====================================================
        // STEP 2: Record audit event
        // =====================================================

        auditLogService.record(
                success
                        ? AuditEventType.LOGIN_SUCCESS
                        : AuditEventType.LOGIN_FAILURE,
                userId,
                email,
                ipAddress,
                success,
                success
                        ? "Login successful"
                        : "Invalid credentials"
        );

        return success;
    }

    public void logout(
            String userId,
            String ipAddress
    ) {

        // =====================================================
        // Logout is a security event.
        // =====================================================

        auditLogService.record(
                AuditEventType.LOGOUT,
                userId,
                userId,
                ipAddress,
                true,
                "User logged out"
        );
    }

    public void passwordResetCompleted(
            String userId,
            String ipAddress
    ) {

        auditLogService.record(
                AuditEventType.PASSWORD_RESET_COMPLETED,
                userId,
                userId,
                ipAddress,
                true,
                "Password reset completed"
        );
    }
}
```

---

## 10.6 `PermissionService.java`

```java
package com.miniauth.service;

import com.miniauth.audit.AuditEventType;
import com.miniauth.audit.AuditLogService;

public class PermissionService {

    private final AuditLogService auditLogService;

    public PermissionService(
            AuditLogService auditLogService
    ) {
        this.auditLogService = auditLogService;
    }

    public void grantPermission(
            String adminId,
            String targetUserId,
            String permission,
            String ipAddress
    ) {

        // =====================================================
        // Permission grant must always be auditable.
        // =====================================================

        auditLogService.record(
                AuditEventType.PERMISSION_GRANTED,
                adminId,
                targetUserId,
                ipAddress,
                true,
                "Granted permission: " + permission
        );
    }

    public void revokePermission(
            String adminId,
            String targetUserId,
            String permission,
            String ipAddress
    ) {

        auditLogService.record(
                AuditEventType.PERMISSION_REVOKED,
                adminId,
                targetUserId,
                ipAddress,
                true,
                "Revoked permission: " + permission
        );
    }

    public void accessDenied(
            String userId,
            String permission,
            String ipAddress
    ) {

        auditLogService.record(
                AuditEventType.ACCESS_DENIED,
                userId,
                userId,
                ipAddress,
                false,
                "Missing permission: " + permission
        );
    }
}
```

---

## 10.7 `Phase016Driver.java`

```java
package com.miniauth.driver;

import com.miniauth.audit.AuditEvent;
import com.miniauth.audit.AuditEventType;
import com.miniauth.audit.AuditLogService;
import com.miniauth.audit.AuditLogStore;
import com.miniauth.service.AuthenticationService;
import com.miniauth.service.PermissionService;

public class Phase016Driver {

    public static void main(String[] args) {

        // =====================================================
        // STEP 1: Create audit infrastructure
        // =====================================================

        AuditLogStore store =
                new AuditLogStore();

        AuditLogService auditLogService =
                new AuditLogService(store);

        // =====================================================
        // STEP 2: Create services that emit audit events
        // =====================================================

        AuthenticationService authService =
                new AuthenticationService(
                        auditLogService
                );

        PermissionService permissionService =
                new PermissionService(
                        auditLogService
                );

        String userId = "user-123";
        String adminId = "admin-999";
        String ip = "10.0.0.10";

        // =====================================================
        // STEP 3: Simulate security actions
        // =====================================================

        authService.login(
                userId,
                "mohamed@example.com",
                "WrongPass",
                ip
        );

        authService.login(
                userId,
                "mohamed@example.com",
                "StrongPass123",
                ip
        );

        permissionService.grantPermission(
                adminId,
                userId,
                "PAYMENT:REFUND",
                ip
        );

        permissionService.accessDenied(
                userId,
                "USER:DELETE",
                ip
        );

        authService.passwordResetCompleted(
                userId,
                ip
        );

        authService.logout(
                userId,
                ip
        );

        // =====================================================
        // STEP 4: Print all audit events
        // =====================================================

        System.out.println("All audit events:");
        for (AuditEvent event : auditLogService.findAll()) {
            System.out.println(event);
        }

        // =====================================================
        // STEP 5: Query by actor
        // =====================================================

        System.out.println();
        System.out.println("Events by actor user-123:");
        for (AuditEvent event :
                auditLogService.findByActorId(userId)) {
            System.out.println(event);
        }

        // =====================================================
        // STEP 6: Query by event type
        // =====================================================

        System.out.println();
        System.out.println("Login failure events:");
        for (AuditEvent event :
                auditLogService.findByType(
                        AuditEventType.LOGIN_FAILURE
                )) {
            System.out.println(event);
        }

        System.out.println();
        System.out.println("Total audit events:");
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
Phase016Driver
```

---

## Command Line

Compile:

```bash
javac -d out src/main/java/com/miniauth/**/*.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase016Driver
```

---

# 12. Dry Run

Security actions:

```text
failed login
successful login
permission granted
access denied
password reset completed
logout
```

Each action creates:

```text
AuditEvent
```

Audit store contains:

```text
append-only list of events
```

Query examples:

```text
find all
find by actor id
find by event type
```

---

# 13. What Changed From Phase 15

## Phase 15

```text
login brute-force protection
```

## Phase 16

```text
record security events
```

New classes:

```text
AuditEventType
AuditEvent
AuditLogStore
AuditLogService
```

---

# 14. What This Phase Does NOT Do Yet

This phase does not yet include:

```text
persistent audit DB
Kafka audit pipeline
tamper-proof logs
hash chained audit events
SIEM integration
Elasticsearch search
retention policy
PII redaction
```

These come later.

---

# 15. DSA / CP Concepts Used

## Append-Only List

Audit store uses:

```text
List<AuditEvent>
```

Append:

```text
O(1)
```

---

## Filtering

Queries use stream filtering:

```text
filter by actor
filter by type
```

Complexity:

```text
O(N)
```

---

## Indexing Idea

Production can index by:

```text
actorId
eventType
createdAt
tenantId
```

to avoid scanning.

---

# 16. System Design Relevance

Audit logs are critical for:

```text
IAM
banking
payments
admin systems
enterprise SaaS
cloud IAM
security platforms
```

Production architecture:

```text
Auth Service
  -> Audit Event
  -> Kafka
  -> Audit Consumer
  -> Elasticsearch / ClickHouse / S3
```

---

# 17. Production-Grade Concepts

## Append-Only Storage

Audit logs should be append-only.

Avoid:

```text
update old audit events
delete suspicious events
```

---

## Tamper Evidence

Advanced systems use:

```text
hash chains
WORM storage
signed logs
immutable S3 buckets
```

---

## Avoid Secrets

Never log:

```text
password
refresh token
access token
OTP
private key
```

---

## Retention Policy

Examples:

```text
90 days
1 year
7 years
```

depends on compliance.

---

## Search And Alerting

Audit logs feed:

```text
SIEM
alerts
fraud detection
incident response
```

---

# 18. Common Bugs

## Bug 1 — Logging Secrets

Bad:

```text
password reset token
JWT token
password
```

Good:

```text
event type + actor + target + metadata
```

---

## Bug 2 — No Failed Event Logging

Failures are often more important than successes.

---

## Bug 3 — Audit Logs Stored Only In Memory

Production needs durable storage.

---

## Bug 4 — No Tenant ID

Multi-tenant systems should include tenantId in audit events.

---

# 19. Interview Notes

If interviewer asks:

```text
How do you design audit logging for IAM?
```

Answer:

```text
1. Identify security events.
2. Create standard audit event schema.
3. Record actor, target, IP, time, success/failure.
4. Never log secrets.
5. Store logs append-only.
6. Send logs asynchronously via Kafka.
7. Index for search.
8. Add retention policy.
9. Alert on suspicious patterns.
10. Make logs tamper-evident for high-security systems.
```

Strong follow-up:

```text
Audit logs are part of the security boundary, not just debugging logs.
```

---

# 20. Next Step

Next file:

```text
017_MFA_OTP_Login.md
```

In the next phase, we add:

```text
MFA
OTP generation
OTP verification
step-up authentication
