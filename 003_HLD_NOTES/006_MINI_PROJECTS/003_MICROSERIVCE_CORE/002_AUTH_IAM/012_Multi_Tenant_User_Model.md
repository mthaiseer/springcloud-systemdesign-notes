# MiniAuth / IAM Phase 12 — Multi Tenant User Model


> NOTE:
>
> This phase now includes:
>
> - Step-by-step Java code comments
> - Production-style formatting
> - Logic explanation before important classes
> - Clean spacing for readability
> - Interview-oriented inline explanations


## Clickable Index

- [1. Goal](#1-goal)
- [2. Why This Phase Matters](#2-why-this-phase-matters)
- [3. What We Built Previously](#3-what-we-built-previously)
- [4. Previous Limitation](#4-previous-limitation)
- [5. What We Build](#5-what-we-build)
- [6. Current Architecture](#6-current-architecture)
- [7. Folder Structure](#7-folder-structure)
- [8. Core Concepts](#8-core-concepts)
- [9. Tenant Isolation Flow](#9-tenant-isolation-flow)
- [10. Complete Java Code](#10-complete-java-code)
  - [10.1 Tenant.java](#101-tenantjava)
  - [10.2 User.java](#102-userjava)
  - [10.3 AuthenticatedPrincipal.java](#103-authenticatedprincipaljava)
  - [10.4 TenantResource.java](#104-tenantresourcejava)
  - [10.5 TenantAccessRequest.java](#105-tenantaccessrequestjava)
  - [10.6 TenantAccessDecision.java](#106-tenantaccessdecisionjava)
  - [10.7 TenantRepository.java](#107-tenantrepositoryjava)
  - [10.8 UserRepository.java](#108-userrepositoryjava)
  - [10.9 TenantAuthorizationService.java](#109-tenantauthorizationservicejava)
  - [10.10 TenantAwareController.java](#1010-tenantawarecontrollerjava)
  - [10.11 Phase012Driver.java](#1011-phase012driverjava)
- [11. How To Run](#11-how-to-run)
- [12. Dry Run](#12-dry-run)
- [13. What Changed From Phase 11](#13-what-changed-from-phase-11)
- [14. What This Phase Does NOT Do Yet](#14-what-this-phase-does-not-do-yet)
- [15. DSA / CP Concepts Used](#15-dsa--cp-concepts-used)
- [16. System Design Relevance](#16-system-design-relevance)
- [17. Production-Grade Concepts](#17-production-grade-concepts)
- [18. Common Bugs](#18-common-bugs)
- [19. Interview Notes](#19-interview-notes)
- [20. Next Step](#20-next-step)

---

# 1. Goal

In this phase, we move from:

```text
single organization auth
```

to:

```text
multi-tenant IAM
```

Before this phase:

```text
all users belong to one global system
```

After this phase:

```text
users belong to tenants
resources belong to tenants
authorization becomes tenant-aware
```

Main objective:

```text
Prevent cross-tenant data access.
```

---

# 2. Why This Phase Matters

Most SaaS systems are multi-tenant.

Examples:

```text
GitHub organizations
Slack workspaces
Jira projects
Shopify stores
AWS accounts
```

Problem:

```text
tenant A user must never access tenant B data
```

Without tenant isolation:

```text
massive security vulnerability
```

---

# 3. What We Built Previously

## Phase 9

```text
RBAC
```

## Phase 10

```text
PBAC
```

## Phase 11

```text
Authorization filter
```

Phase 11 checked:

```text
does user have permission?
```

But it did NOT check:

```text
does resource belong to same tenant?
```

Phase 12 adds tenant isolation.

---

# 4. Previous Limitation

Previously:

```text
user with permission could access all resources
```

Danger:

```text
Tenant A admin accesses Tenant B invoices
```

Need:

```text
tenant-aware authorization
```

---

# 5. What We Build

We add:

```text
Tenant
TenantResource
TenantAccessRequest
TenantAccessDecision
TenantAuthorizationService
TenantAwareController
```

Flow:

```text
principal
    -> tenant validation
    -> permission validation
    -> allow / deny
```

---

# 6. Current Architecture

```text
+--------------------------+
| AuthenticatedPrincipal   |
| userId                   |
| tenantId                 |
+-------------+------------+
              |
              v
+--------------------------+
| TenantAuthorization      |
| compare tenant ids       |
| check permission         |
+-------------+------------+
              |
              v
+--------------------------+
| Tenant Resource          |
| invoice/project/report   |
| belongs to tenant        |
+-------------+------------+
              |
              v
+--------------------------+
| ALLOW / DENY             |
+--------------------------+
```

Security rule:

```text
principal.tenantId must match resource.tenantId
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
                    ├── tenant/
                    │   ├── Tenant.java
                    │   ├── TenantResource.java
                    │   ├── TenantAccessRequest.java
                    │   ├── TenantAccessDecision.java
                    │   ├── TenantRepository.java
                    │   └── TenantAuthorizationService.java
                    ├── model/
                    │   └── User.java
                    ├── repository/
                    │   └── UserRepository.java
                    ├── security/
                    │   └── AuthenticatedPrincipal.java
                    ├── controller/
                    │   └── TenantAwareController.java
                    └── driver/
                        └── Phase012Driver.java
```

---

# 8. Core Concepts

## 8.1 Tenant

Tenant represents an organization/account/workspace.

Examples:

```text
company A
company B
workspace X
workspace Y
```

---

## 8.2 Tenant Isolation

Rule:

```text
users can only access resources in their tenant
```

This is critical for SaaS security.

---

## 8.3 Tenant-Aware Principal

Authenticated principal now includes:

```text
userId
email
tenantId
```

---

## 8.4 Tenant Resource

Every protected resource belongs to a tenant.

Examples:

```text
invoice belongs to tenant
report belongs to tenant
document belongs to tenant
```

---

# 9. Tenant Isolation Flow

```text
Request
   |
   v
JWT principal
   |
   v
Extract tenantId
   |
   v
Load resource
   |
   v
Compare:
principal.tenantId
==
resource.tenantId
   |
   +-- mismatch -> DENY
   |
   +-- match
         |
         v
   permission check
         |
         v
       ALLOW
```

---

# 10. Complete Java Code

---

## 10.1 `Tenant.java`

```java
package com.miniauth.tenant;

import java.util.UUID;

public class Tenant {

    private final String id;
    private final String name;

    public Tenant(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
```

---

## 10.2 `User.java`

```java
package com.miniauth.model;

import java.util.UUID;

public class User {

    private final String id;
    private final String email;
    private final String tenantId;

    public User(
            String email,
            String tenantId
    ) {
        this.id = UUID.randomUUID().toString();
        this.email = email.toLowerCase().trim();
        this.tenantId = tenantId;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getTenantId() {
        return tenantId;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", tenantId='" + tenantId + '\'' +
                '}';
    }
}
```

---

## 10.3 `AuthenticatedPrincipal.java`

```java
package com.miniauth.security;

public class AuthenticatedPrincipal {

    private final String userId;
    private final String email;
    private final String tenantId;

    public AuthenticatedPrincipal(
            String userId,
            String email,
            String tenantId
    ) {
        this.userId = userId;
        this.email = email;
        this.tenantId = tenantId;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getTenantId() {
        return tenantId;
    }

    @Override
    public String toString() {
        return "AuthenticatedPrincipal{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", tenantId='" + tenantId + '\'' +
                '}';
    }
}
```

---

## 10.4 `TenantResource.java`

### Logic before this class

Every protected resource belongs to a tenant.

```java
package com.miniauth.tenant;

public class TenantResource {

    private final String resourceId;
    private final String tenantId;
    private final String resourceType;

    public TenantResource(
            String resourceId,
            String tenantId,
            String resourceType
    ) {
        this.resourceId = resourceId;
        this.tenantId = tenantId;
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getResourceType() {
        return resourceType;
    }

    @Override
    public String toString() {
        return "TenantResource{" +
                "resourceId='" + resourceId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", resourceType='" + resourceType + '\'' +
                '}';
    }
}
```

---

## 10.5 `TenantAccessRequest.java`

```java
package com.miniauth.tenant;

import com.miniauth.security.AuthenticatedPrincipal;

public class TenantAccessRequest {

    private final AuthenticatedPrincipal principal;
    private final TenantResource resource;

    public TenantAccessRequest(
            AuthenticatedPrincipal principal,
            TenantResource resource
    ) {
        this.principal = principal;
        this.resource = resource;
    }

    public AuthenticatedPrincipal getPrincipal() {
        return principal;
    }

    public TenantResource getResource() {
        return resource;
    }
}
```

---

## 10.6 `TenantAccessDecision.java`

```java
package com.miniauth.tenant;

public class TenantAccessDecision {

    private final boolean allowed;
    private final String reason;

    private TenantAccessDecision(
            boolean allowed,
            String reason
    ) {
        this.allowed = allowed;
        this.reason = reason;
    }

    public static TenantAccessDecision allow() {
        return new TenantAccessDecision(
                true,
                "Tenant access granted"
        );
    }

    public static TenantAccessDecision deny(
            String reason
    ) {
        return new TenantAccessDecision(
                false,
                reason
        );
    }

    public boolean isAllowed() {
        return allowed;
    }

    @Override
    public String toString() {
        return "TenantAccessDecision{" +
                "allowed=" + allowed +
                ", reason='" + reason + '\'' +
                '}';
    }
}
```

---

## 10.7 `TenantRepository.java`

```java
package com.miniauth.tenant;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TenantRepository {

    private final Map<String, Tenant> tenants =
            new HashMap<>();

    public void save(Tenant tenant) {
        tenants.put(tenant.getId(), tenant);
    }

    public Optional<Tenant> findById(String tenantId) {
        return Optional.ofNullable(
                tenants.get(tenantId)
        );
    }
}
```

---

## 10.8 `UserRepository.java`

```java
package com.miniauth.repository;

import com.miniauth.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserRepository {

    private final Map<String, User> users =
            new HashMap<>();

    public void save(User user) {
        users.put(user.getId(), user);
    }

    public Optional<User> findById(String userId) {
        return Optional.ofNullable(
                users.get(userId)
        );
    }
}
```

---

## 10.9 `TenantAuthorizationService.java`

### Logic before this class

This service enforces tenant isolation.

Algorithm:

```text
1. Validate principal.
2. Validate resource.
3. Compare tenant ids.
4. Allow if same tenant.
5. Deny otherwise.
```

```java
package com.miniauth.tenant;

import com.miniauth.security.AuthenticatedPrincipal;

public class TenantAuthorizationService {

    public TenantAccessDecision authorize(
            TenantAccessRequest request
    ) {

        // =====================================================
        // STEP 1: Validate request object
        // =====================================================

        if (request == null) {

            return TenantAccessDecision.deny(
                    "Request is null"
            );
        }

        // =====================================================
        // STEP 2: Extract authenticated principal
        // =====================================================

        AuthenticatedPrincipal principal =
                request.getPrincipal();

        if (principal == null) {

            return TenantAccessDecision.deny(
                    "Principal missing"
            );
        }

        // =====================================================
        // STEP 3: Extract tenant resource
        // =====================================================

        TenantResource resource =
                request.getResource();

        if (resource == null) {

            return TenantAccessDecision.deny(
                    "Resource missing"
            );
        }

        // =====================================================
        // STEP 4: Enforce tenant isolation
        // =====================================================
        //
        // VERY IMPORTANT SECURITY CHECK
        //
        // principal tenant
        // MUST MATCH
        // resource tenant
        //
        // Prevents:
        // tenant A user -> tenant B data access
        //
        // =====================================================

        if (!principal.getTenantId()
                .equals(resource.getTenantId())) {

            return TenantAccessDecision.deny(
                    "Cross-tenant access denied"
            );
        }

        // =====================================================
        // STEP 5: Access granted
        // =====================================================

        return TenantAccessDecision.allow();
    }
}
```

---

## 10.10 `TenantAwareController.java`

### Logic before this class

This simulates tenant-protected APIs.

```java
package com.miniauth.controller;

import com.miniauth.security.AuthenticatedPrincipal;
import com.miniauth.tenant.TenantAccessDecision;
import com.miniauth.tenant.TenantAccessRequest;
import com.miniauth.tenant.TenantAuthorizationService;
import com.miniauth.tenant.TenantResource;

public class TenantAwareController {

    private final TenantAuthorizationService
            authorizationService;

    public TenantAwareController(
            TenantAuthorizationService authorizationService
    ) {
        this.authorizationService =
                authorizationService;
    }

    public void accessInvoice(
            AuthenticatedPrincipal principal,
            TenantResource invoice
    ) {

        TenantAccessDecision decision =
                authorizationService.authorize(
                        new TenantAccessRequest(
                                principal,
                                invoice
                        )
                );

        System.out.println();
        System.out.println(
                "Accessing invoice: " +
                        invoice.getResourceId()
        );

        if (decision.isAllowed()) {

            System.out.println(
                    "200 OK - invoice returned"
            );

        } else {

            System.out.println(
                    "403 Forbidden"
            );

            System.out.println(decision);
        }
    }
}
```

---

## 10.11 `Phase012Driver.java`

### Logic before this class

This driver proves:

```text
1. Same-tenant access succeeds.
2. Cross-tenant access fails.
3. Tenant isolation works.
```

```java
package com.miniauth.driver;

import com.miniauth.controller.TenantAwareController;
import com.miniauth.model.User;
import com.miniauth.security.AuthenticatedPrincipal;
import com.miniauth.tenant.Tenant;
import com.miniauth.tenant.TenantAuthorizationService;
import com.miniauth.tenant.TenantResource;

public class Phase012Driver {

    public static void main(String[] args) {

        Tenant tenantA =
                new Tenant("Acme Corp");

        Tenant tenantB =
                new Tenant("Globex Inc");

        User userA =
                new User(
                        "mohamed@acme.com",
                        tenantA.getId()
                );

        User userB =
                new User(
                        "john@globex.com",
                        tenantB.getId()
                );

        AuthenticatedPrincipal principalA =
                new AuthenticatedPrincipal(
                        userA.getId(),
                        userA.getEmail(),
                        userA.getTenantId()
                );

        AuthenticatedPrincipal principalB =
                new AuthenticatedPrincipal(
                        userB.getId(),
                        userB.getEmail(),
                        userB.getTenantId()
                );

        TenantResource invoiceA =
                new TenantResource(
                        "invoice-100",
                        tenantA.getId(),
                        "INVOICE"
                );

        TenantResource invoiceB =
                new TenantResource(
                        "invoice-200",
                        tenantB.getId(),
                        "INVOICE"
                );

        TenantAuthorizationService authService =
                new TenantAuthorizationService();

        TenantAwareController controller =
                new TenantAwareController(
                        authService
                );

        System.out.println("Tenant A:");
        System.out.println(tenantA);

        System.out.println();
        System.out.println("Tenant B:");
        System.out.println(tenantB);

        System.out.println();
        System.out.println("Principal A:");
        System.out.println(principalA);

        System.out.println();
        System.out.println("Principal B:");
        System.out.println(principalB);

        controller.accessInvoice(
                principalA,
                invoiceA
        );

        controller.accessInvoice(
                principalA,
                invoiceB
        );

        controller.accessInvoice(
                principalB,
                invoiceB
        );

        controller.accessInvoice(
                principalB,
                invoiceA
        );
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
Phase012Driver
```

---

## Command Line

Compile:

```bash
javac -d out src/main/java/com/miniauth/**/*.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase012Driver
```

---

# 12. Dry Run

## Tenant A User -> Tenant A Resource

Principal:

```text
tenantId = tenant-A
```

Resource:

```text
tenantId = tenant-A
```

Result:

```text
ALLOW
```

---

## Tenant A User -> Tenant B Resource

Principal:

```text
tenantId = tenant-A
```

Resource:

```text
tenantId = tenant-B
```

Result:

```text
DENY
```

Reason:

```text
Cross-tenant access denied
```

---

# 13. What Changed From Phase 11

## Phase 11

```text
authorization based on permission
```

## Phase 12

```text
authorization based on tenant + permission
```

New classes:

```text
Tenant
TenantResource
TenantAuthorizationService
TenantAccessDecision
```

New capability:

```text
cross-tenant isolation
```

---

# 14. What This Phase Does NOT Do Yet

This phase does not yet include:

```text
tenant roles
tenant invitations
tenant switching
organization hierarchy
cross-tenant sharing
resource ownership
database row-level security
tenant-scoped caches
tenant quotas
billing integration
```

These come later.

---

# 15. DSA / CP Concepts Used

## Equality Check

Core security logic:

```text
principal.tenantId == resource.tenantId
```

This simple check is extremely important.

---

## HashMap Lookup

Repositories use:

```text
tenantId -> tenant
userId -> user
```

Expected complexity:

```text
O(1)
```

---

## Resource Graph Thinking

Conceptually:

```text
Tenant
  -> Users
  -> Resources
```

This models ownership boundaries.

---

# 16. System Design Relevance

Multi-tenancy is critical for:

```text
SaaS
cloud IAM
enterprise apps
B2B products
workspace systems
project management systems
```

Typical architecture:

```text
JWT includes tenantId
Gateway extracts tenantId
Service validates tenant boundary
DB queries scoped by tenantId
```

---

# 17. Production-Grade Concepts

## Database Isolation Models

### Shared Database Shared Tables

```text
all tenants in same table
tenant_id column used
```

Most common SaaS model.

---

### Shared Database Separate Schema

```text
tenant A schema
tenant B schema
```

---

### Separate Database Per Tenant

Highest isolation.

More operational complexity.

---

## Tenant-Aware Indexing

Always index:

```text
tenant_id
```

Common query:

```sql
SELECT *
FROM invoices
WHERE tenant_id = ?
```

---

## JWT Tenant Claim

Production JWT often includes:

```json
{
  "tenant_id": "tenant-123"
}
```

---

## Tenant-Aware Caching

Cache key should include tenant:

Bad:

```text
invoice:100
```

Good:

```text
tenantA:invoice:100
```

---

# 18. Common Bugs

## Bug 1 — Forgetting Tenant Filter In SQL

Bad:

```sql
SELECT * FROM invoices WHERE id = ?
```

Good:

```sql
SELECT *
FROM invoices
WHERE tenant_id = ?
AND id = ?
```

---

## Bug 2 — Cache Key Without Tenant

Can leak cross-tenant data.

---

## Bug 3 — Trusting Client Tenant ID

Never trust:

```text
tenantId from frontend
```

Use:

```text
tenantId from verified JWT
```

---

## Bug 4 — Admin Bypass Without Audit

Cross-tenant admin access must be audited carefully.

---

# 19. Interview Notes

If interviewer asks:

```text
How do you design multi-tenant authorization?
```

Answer:

```text
1. Every user belongs to a tenant.
2. Every protected resource belongs to a tenant.
3. JWT contains tenantId.
4. Service validates principal tenant matches resource tenant.
5. DB queries are tenant-scoped.
6. Cache keys include tenant.
7. Authorization checks combine:
   tenant boundary + permission.
8. Deny cross-tenant access by default.
```

Strong follow-up:

```text
Tenant isolation is one of the most critical SaaS security requirements.
```

---

# 20. Next Step

Next file:

```text
013_Organization_Roles_And_Team_Model.md
```

In the next phase, we add:

```text
organizations
teams
team membership
team-scoped roles
organization hierarchy
```
