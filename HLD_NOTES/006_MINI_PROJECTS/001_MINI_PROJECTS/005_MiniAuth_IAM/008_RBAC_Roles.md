# 008_RBAC_Roles.md

# MiniAuth / IAM Phase 8 — RBAC Roles

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
RBAC Roles
```

Purpose:

```text
Add role-based access control using USER, ADMIN, and MANAGER roles.
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
All users have same access.
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
RoleService
```

Core flow:

```text
user -> roles -> access decision
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
| RBAC Roles         |
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


## 8.1 `AuthorizationService.java`

### Logic before this class

Authorization checks whether a user can perform an action.

RBAC checks roles.

Permission-based auth checks fine-grained permissions.

```java
package com.miniauth.authz;

import java.util.*;

public class AuthorizationService {
    private final Map<String, Set<String>> userRoles = new HashMap<>();
    private final Map<String, Set<String>> rolePermissions = new HashMap<>();

    public AuthorizationService() {
        rolePermissions.put("ADMIN", Set.of("USER_READ", "USER_WRITE", "ORDER_REFUND"));
        rolePermissions.put("USER", Set.of("PROFILE_READ"));
        rolePermissions.put("MANAGER", Set.of("USER_READ", "REPORT_READ"));
    }

    public void assignRole(String userId, String role) {
        userRoles.computeIfAbsent(userId, id -> new HashSet<>()).add(role);
    }

    public boolean hasRole(String userId, String role) {
        return userRoles.getOrDefault(userId, Set.of()).contains(role);
    }

    public boolean hasPermission(String userId, String permission) {
        for (String role : userRoles.getOrDefault(userId, Set.of())) {
            if (rolePermissions.getOrDefault(role, Set.of()).contains(permission)) {
                return true;
            }
        }
        return false;
    }
}
```

---

## 8.2 `Phase008Driver.java`

### Logic before this class

The driver assigns roles and checks role/permission decisions.

```java
package com.miniauth.driver;

import com.miniauth.authz.AuthorizationService;

public class Phase008Driver {
    public static void main(String[] args) {
        AuthorizationService authz = new AuthorizationService();

        authz.assignRole("user-1", "ADMIN");
        authz.assignRole("user-2", "USER");

        System.out.println("user-1 ADMIN = " + authz.hasRole("user-1", "ADMIN"));
        System.out.println("user-1 USER_WRITE = " + authz.hasPermission("user-1", "USER_WRITE"));
        System.out.println("user-2 USER_WRITE = " + authz.hasPermission("user-2", "USER_WRITE"));
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
java -cp out com.miniauth.driver.Phase008Driver
```

If your shell does not support `**`, compile specific files manually.

---

# 10. Dry Run

Example flow for this phase:

```text
Input:
RBAC Roles

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
All users have same access.

After:
user -> roles -> access decision
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
Set membership
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
admin/user access
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
009_Permissions_And_Scopes.md
```

Continue the MiniAuth/IAM roadmap step by step.
