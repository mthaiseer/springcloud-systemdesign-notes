# MiniAuth / IAM Phase 10 — Permission Based Access Control

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why This Phase Matters](#2-why-this-phase-matters)
- [3. What We Built Previously](#3-what-we-built-previously)
- [4. Previous Limitation](#4-previous-limitation)
- [5. What We Build](#5-what-we-build)
- [6. Current Architecture](#6-current-architecture)
- [7. Folder Structure](#7-folder-structure)
- [8. Core Concepts](#8-core-concepts)
- [9. RBAC vs PBAC](#9-rbac-vs-pbac)
- [10. Complete Java Code](#10-complete-java-code)
  - [10.1 Permission.java](#101-permissionjava)
  - [10.2 ResourceType.java](#102-resourcetypejava)
  - [10.3 ResourceAction.java](#103-resourceactionjava)
  - [10.4 User.java](#104-userjava)
  - [10.5 AuthenticatedPrincipal.java](#105-authenticatedprincipaljava)
  - [10.6 PermissionGrant.java](#106-permissiongrantjava)
  - [10.7 PermissionStore.java](#107-permissionstorejava)
  - [10.8 PermissionRequest.java](#108-permissionrequestjava)
  - [10.9 PermissionDecision.java](#109-permissiondecisionjava)
  - [10.10 PermissionAuthorizationService.java](#1010-permissionauthorizationservicejava)
  - [10.11 ResourceApiSimulator.java](#1011-resourceapisimulatorjava)
  - [10.12 Phase010Driver.java](#1012-phase010driverjava)
- [11. How To Run](#11-how-to-run)
- [12. Dry Run](#12-dry-run)
- [13. What Changed From Phase 9](#13-what-changed-from-phase-9)
- [14. What This Phase Does NOT Do Yet](#14-what-this-phase-does-not-do-yet)
- [15. DSA / CP Concepts Used](#15-dsa--cp-concepts-used)
- [16. System Design Relevance](#16-system-design-relevance)
- [17. Production-Grade Concepts](#17-production-grade-concepts)
- [18. Common Bugs](#18-common-bugs)
- [19. Interview Notes](#19-interview-notes)
- [20. Next Step](#20-next-step)

---

# 1. Goal

In this phase, we move from role-based access control to permission-based access control.

Before this phase:

```text
User -> Role -> Permissions
```

After this phase:

```text
User -> Direct Permissions
User -> Resource Permissions
```

Main objective:

```text
Allow fine-grained access decisions using explicit permissions.
```

---

# 2. Why This Phase Matters

RBAC is simple and powerful.

But real systems often need more precise control.

Example:

```text
USER can update own profile
SUPPORT can read customer profile
FINANCE can refund payment
AUDITOR can view reports only
```

Sometimes role alone is too broad.

Permission-based access control allows:

```text
user-specific permissions
resource-specific permissions
action-specific permissions
```

---

# 3. What We Built Previously

## Phase 9

We built RBAC:

```text
Role -> Permission Set
Principal -> Roles
AuthorizationService checks permission
```

Example:

```text
ADMIN -> all permissions
USER -> profile permissions
```

Now Phase 10 adds direct permission grants.

---

# 4. Previous Limitation

In RBAC, access is decided through roles.

Problem:

```text
What if one USER needs PAYMENT_REFUND temporarily?
```

Options:

```text
create new role
give ADMIN role
hardcode special case
```

All are poor choices.

Better:

```text
grant specific permission directly
```

---

# 5. What We Build

We add:

```text
PermissionGrant
PermissionStore
PermissionRequest
PermissionDecision
PermissionAuthorizationService
ResourceApiSimulator
```

Flow:

```text
principal
    -> permission request
    -> permission store lookup
    -> allow / deny
```

---

# 6. Current Architecture

```text
+---------------------------+
| AuthenticatedPrincipal    |
+-------------+-------------+
              |
              v
+---------------------------+
| PermissionRequest         |
| resource + action         |
+-------------+-------------+
              |
              v
+---------------------------+
| PermissionAuthorization   |
| direct permission lookup  |
| wildcard permission check |
+-------------+-------------+
              |
              v
+---------------------------+
| PermissionDecision        |
| ALLOW / DENY              |
+---------------------------+
```

Protected API:

```text
request user wants PAYMENT:REFUND
        |
        v
check direct permission
        |
        v
allow or deny
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
                    ├── authz/
                    │   ├── Permission.java
                    │   ├── ResourceType.java
                    │   ├── ResourceAction.java
                    │   ├── PermissionGrant.java
                    │   ├── PermissionStore.java
                    │   ├── PermissionRequest.java
                    │   ├── PermissionDecision.java
                    │   ├── PermissionAuthorizationService.java
                    │   └── ResourceApiSimulator.java
                    ├── model/
                    │   └── User.java
                    ├── security/
                    │   └── AuthenticatedPrincipal.java
                    └── driver/
                        └── Phase010Driver.java
```

---

# 8. Core Concepts

## 8.1 Permission

A permission represents:

```text
resource + action
```

Example:

```text
PROFILE:READ
PROFILE:UPDATE
PAYMENT:REFUND
USER:DELETE
```

---

## 8.2 Permission Grant

A permission grant says:

```text
user X is allowed to do action Y on resource Z
```

Example:

```text
user-123 -> PAYMENT:REFUND
```

---

## 8.3 Resource Type

Resource type is the object being protected.

Examples:

```text
PROFILE
USER
PAYMENT
REPORT
SYSTEM
```

---

## 8.4 Resource Action

Action is what user wants to do.

Examples:

```text
READ
CREATE
UPDATE
DELETE
REFUND
APPROVE
```

---

# 9. RBAC vs PBAC

## RBAC

```text
User -> Role -> Permission
```

Good for:

```text
simple admin/user/support systems
```

## PBAC

```text
User -> Permission
```

Good for:

```text
fine-grained access
temporary grants
enterprise IAM
custom SaaS permissions
```

## Combined Model

Real systems often use both:

```text
effective permissions = role permissions + direct permissions
```

This phase focuses on direct permission grants.

---

# 10. Complete Java Code

---

## 10.1 `Permission.java`

```java
package com.miniauth.authz;

import java.util.Objects;

public class Permission {

    private final ResourceType resourceType;
    private final ResourceAction action;

    public Permission(
            ResourceType resourceType,
            ResourceAction action
    ) {
        this.resourceType = resourceType;
        this.action = action;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public ResourceAction getAction() {
        return action;
    }

    public static Permission of(
            ResourceType resourceType,
            ResourceAction action
    ) {
        return new Permission(resourceType, action);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Permission)) {
            return false;
        }

        Permission that = (Permission) o;

        return resourceType == that.resourceType &&
                action == that.action;
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceType, action);
    }

    @Override
    public String toString() {
        return resourceType + ":" + action;
    }
}
```

---

## 10.2 `ResourceType.java`

```java
package com.miniauth.authz;

public enum ResourceType {

    PROFILE,
    USER,
    PAYMENT,
    REPORT,
    SYSTEM
}
```

---

## 10.3 `ResourceAction.java`

```java
package com.miniauth.authz;

public enum ResourceAction {

    READ,
    CREATE,
    UPDATE,
    DELETE,
    REFUND,
    APPROVE,
    CONFIGURE
}
```

---

## 10.4 `User.java`

```java
package com.miniauth.model;

import java.util.UUID;

public class User {

    private final String id;
    private final String email;

    public User(String email) {
        this.id = UUID.randomUUID().toString();
        this.email = email.toLowerCase().trim();
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
```

---

## 10.5 `AuthenticatedPrincipal.java`

```java
package com.miniauth.security;

public class AuthenticatedPrincipal {

    private final String userId;
    private final String email;

    public AuthenticatedPrincipal(
            String userId,
            String email
    ) {
        this.userId = userId;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "AuthenticatedPrincipal{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
```

---

## 10.6 `PermissionGrant.java`

### Logic before this class

This class represents one direct permission assigned to one user.

```java
package com.miniauth.authz;

public class PermissionGrant {

    private final String userId;
    private final Permission permission;

    public PermissionGrant(
            String userId,
            Permission permission
    ) {
        this.userId = userId;
        this.permission = permission;
    }

    public String getUserId() {
        return userId;
    }

    public Permission getPermission() {
        return permission;
    }

    @Override
    public String toString() {
        return "PermissionGrant{" +
                "userId='" + userId + '\'' +
                ", permission=" + permission +
                '}';
    }
}
```

---

## 10.7 `PermissionStore.java`

### Logic before this class

This is the in-memory permission database.

It maps:

```text
userId -> Set<Permission>
```

```java
package com.miniauth.authz;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PermissionStore {

    private final Map<String, Set<Permission>> permissionsByUserId =
            new HashMap<>();

    public void grant(
            String userId,
            Permission permission
    ) {
        permissionsByUserId
                .computeIfAbsent(
                        userId,
                        id -> new HashSet<>()
                )
                .add(permission);
    }

    public void revoke(
            String userId,
            Permission permission
    ) {
        Set<Permission> permissions =
                permissionsByUserId.get(userId);

        if (permissions == null) {
            return;
        }

        permissions.remove(permission);

        if (permissions.isEmpty()) {
            permissionsByUserId.remove(userId);
        }
    }

    public boolean hasPermission(
            String userId,
            Permission permission
    ) {
        Set<Permission> permissions =
                permissionsByUserId.get(userId);

        if (permissions == null) {
            return false;
        }

        return permissions.contains(permission);
    }

    public Set<Permission> getPermissions(String userId) {
        return permissionsByUserId.getOrDefault(
                userId,
                Set.of()
        );
    }
}
```

---

## 10.8 `PermissionRequest.java`

```java
package com.miniauth.authz;

import com.miniauth.security.AuthenticatedPrincipal;

public class PermissionRequest {

    private final AuthenticatedPrincipal principal;
    private final Permission permission;

    public PermissionRequest(
            AuthenticatedPrincipal principal,
            Permission permission
    ) {
        this.principal = principal;
        this.permission = permission;
    }

    public AuthenticatedPrincipal getPrincipal() {
        return principal;
    }

    public Permission getPermission() {
        return permission;
    }
}
```

---

## 10.9 `PermissionDecision.java`

```java
package com.miniauth.authz;

public class PermissionDecision {

    private final boolean allowed;
    private final String reason;

    private PermissionDecision(
            boolean allowed,
            String reason
    ) {
        this.allowed = allowed;
        this.reason = reason;
    }

    public static PermissionDecision allow() {
        return new PermissionDecision(
                true,
                "Access granted"
        );
    }

    public static PermissionDecision deny(String reason) {
        return new PermissionDecision(
                false,
                reason
        );
    }

    public boolean isAllowed() {
        return allowed;
    }

    @Override
    public String toString() {
        return "PermissionDecision{" +
                "allowed=" + allowed +
                ", reason='" + reason + '\'' +
                '}';
    }
}
```

---

## 10.10 `PermissionAuthorizationService.java`

### Logic before this class

This service checks direct permissions.

Algorithm:

```text
1. Validate principal.
2. Validate requested permission.
3. Lookup user permissions.
4. Allow if permission exists.
5. Otherwise deny.
```

```java
package com.miniauth.authz;

import com.miniauth.security.AuthenticatedPrincipal;

public class PermissionAuthorizationService {

    private final PermissionStore permissionStore;

    public PermissionAuthorizationService(
            PermissionStore permissionStore
    ) {
        this.permissionStore = permissionStore;
    }

    public PermissionDecision authorize(
            PermissionRequest request
    ) {
        if (request == null) {
            return PermissionDecision.deny(
                    "Request is null"
            );
        }

        AuthenticatedPrincipal principal =
                request.getPrincipal();

        if (principal == null) {
            return PermissionDecision.deny(
                    "Principal is missing"
            );
        }

        Permission permission =
                request.getPermission();

        if (permission == null) {
            return PermissionDecision.deny(
                    "Permission is missing"
            );
        }

        boolean allowed =
                permissionStore.hasPermission(
                        principal.getUserId(),
                        permission
                );

        if (allowed) {
            return PermissionDecision.allow();
        }

        return PermissionDecision.deny(
                "Missing permission: " + permission
        );
    }
}
```

---

## 10.11 `ResourceApiSimulator.java`

```java
package com.miniauth.authz;

import com.miniauth.security.AuthenticatedPrincipal;

public class ResourceApiSimulator {

    private final PermissionAuthorizationService authorizationService;

    public ResourceApiSimulator(
            PermissionAuthorizationService authorizationService
    ) {
        this.authorizationService = authorizationService;
    }

    public void readProfile(
            AuthenticatedPrincipal principal
    ) {
        simulate(
                "READ PROFILE API",
                principal,
                Permission.of(
                        ResourceType.PROFILE,
                        ResourceAction.READ
                )
        );
    }

    public void refundPayment(
            AuthenticatedPrincipal principal
    ) {
        simulate(
                "REFUND PAYMENT API",
                principal,
                Permission.of(
                        ResourceType.PAYMENT,
                        ResourceAction.REFUND
                )
        );
    }

    public void deleteUser(
            AuthenticatedPrincipal principal
    ) {
        simulate(
                "DELETE USER API",
                principal,
                Permission.of(
                        ResourceType.USER,
                        ResourceAction.DELETE
                )
        );
    }

    public void configureSystem(
            AuthenticatedPrincipal principal
    ) {
        simulate(
                "CONFIGURE SYSTEM API",
                principal,
                Permission.of(
                        ResourceType.SYSTEM,
                        ResourceAction.CONFIGURE
                )
        );
    }

    private void simulate(
            String apiName,
            AuthenticatedPrincipal principal,
            Permission requiredPermission
    ) {
        PermissionDecision decision =
                authorizationService.authorize(
                        new PermissionRequest(
                                principal,
                                requiredPermission
                        )
                );

        System.out.println();
        System.out.println(apiName);

        if (decision.isAllowed()) {
            System.out.println("200 OK");
            System.out.println("Action completed");
        } else {
            System.out.println("403 Forbidden");
            System.out.println(decision);
        }
    }
}
```

---

## 10.12 `Phase010Driver.java`

### Logic before this class

This driver proves:

```text
1. A user can receive direct permissions.
2. User can access APIs matching granted permissions.
3. User is denied for missing permissions.
4. Permission can be revoked.
```

```java
package com.miniauth.driver;

import com.miniauth.authz.Permission;
import com.miniauth.authz.PermissionAuthorizationService;
import com.miniauth.authz.PermissionStore;
import com.miniauth.authz.ResourceAction;
import com.miniauth.authz.ResourceApiSimulator;
import com.miniauth.authz.ResourceType;
import com.miniauth.model.User;
import com.miniauth.security.AuthenticatedPrincipal;

public class Phase010Driver {

    public static void main(String[] args) {

        PermissionStore permissionStore =
                new PermissionStore();

        PermissionAuthorizationService authorizationService =
                new PermissionAuthorizationService(
                        permissionStore
                );

        ResourceApiSimulator apiSimulator =
                new ResourceApiSimulator(
                        authorizationService
                );

        User financeUser =
                new User("finance@example.com");

        AuthenticatedPrincipal financePrincipal =
                new AuthenticatedPrincipal(
                        financeUser.getId(),
                        financeUser.getEmail()
                );

        Permission profileRead =
                Permission.of(
                        ResourceType.PROFILE,
                        ResourceAction.READ
                );

        Permission paymentRefund =
                Permission.of(
                        ResourceType.PAYMENT,
                        ResourceAction.REFUND
                );

        Permission userDelete =
                Permission.of(
                        ResourceType.USER,
                        ResourceAction.DELETE
                );

        permissionStore.grant(
                financeUser.getId(),
                profileRead
        );

        permissionStore.grant(
                financeUser.getId(),
                paymentRefund
        );

        System.out.println("Finance principal:");
        System.out.println(financePrincipal);

        System.out.println();
        System.out.println("Granted permissions:");
        System.out.println(
                permissionStore.getPermissions(
                        financeUser.getId()
                )
        );

        apiSimulator.readProfile(financePrincipal);

        apiSimulator.refundPayment(financePrincipal);

        apiSimulator.deleteUser(financePrincipal);

        permissionStore.revoke(
                financeUser.getId(),
                paymentRefund
        );

        System.out.println();
        System.out.println("After revoking PAYMENT:REFUND");

        apiSimulator.refundPayment(financePrincipal);

        permissionStore.grant(
                financeUser.getId(),
                userDelete
        );

        System.out.println();
        System.out.println("After granting USER:DELETE");

        apiSimulator.deleteUser(financePrincipal);
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
Phase010Driver
```

---

## Command Line

Compile:

```bash
javac -d out src/main/java/com/miniauth/**/*.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase010Driver
```

---

# 12. Dry Run

User:

```text
finance@example.com
```

Granted permissions:

```text
PROFILE:READ
PAYMENT:REFUND
```

Call:

```text
READ PROFILE API
```

Required:

```text
PROFILE:READ
```

Decision:

```text
ALLOW
```

Call:

```text
DELETE USER API
```

Required:

```text
USER:DELETE
```

Decision:

```text
DENY
```

Then grant:

```text
USER:DELETE
```

Decision becomes:

```text
ALLOW
```

---

# 13. What Changed From Phase 9

## Phase 9

```text
Role -> Permission
```

## Phase 10

```text
User -> Direct Permission
```

New capability:

```text
grant specific permission to a specific user
revoke specific permission from a specific user
```

---

# 14. What This Phase Does NOT Do Yet

This phase does not yet include:

```text
resource ownership check
tenant isolation
conditions
time-based access
approval workflows
policy language
hierarchical permission inheritance
field-level permission
row-level permission
```

These come later.

---

# 15. DSA / CP Concepts Used

## HashMap

Permission store uses:

```text
userId -> Set<Permission>
```

Lookup:

```text
O(1)
```

---

## HashSet

Permissions are stored in a set.

This avoids duplicates.

```text
grant same permission twice -> still one permission
```

---

## Composite Key

Permission is effectively:

```text
(resourceType, action)
```

This requires:

```text
equals()
hashCode()
```

---

## Set Operations

Permission grants and revokes are basic set operations:

```text
add
remove
contains
```

---

# 16. System Design Relevance

PBAC appears in:

```text
enterprise IAM
SaaS admin panels
banking systems
GitHub permissions
cloud IAM systems
document sharing apps
payment systems
```

HLD:

```text
User
  -> Permission Service
  -> Policy Store
  -> Authorization Decision
```

At scale:

```text
permission cache
policy database
audit logs
event-driven permission updates
```

---

# 17. Production-Grade Concepts

## Combine RBAC + PBAC

Real systems often use:

```text
effective permissions =
    role permissions
    + direct user permissions
    + group permissions
```

---

## Permission Cache

Authorization happens on every request.

Use cache:

```text
userId -> effective permission set
```

Invalidate cache when permission changes.

---

## Audit Permission Changes

Every grant/revoke should log:

```text
who changed it
what permission changed
target user
timestamp
reason
```

---

## Least Privilege

Grant only what is needed.

Avoid:

```text
temporary admin role
```

Prefer:

```text
specific permission
```

---

# 18. Common Bugs

## Bug 1 — Permission Without equals/hashCode

If permission is used in HashSet, missing equals/hashCode breaks lookup.

---

## Bug 2 — Allow By Default

Bad:

```text
missing permission -> allow
```

Good:

```text
missing permission -> deny
```

---

## Bug 3 — Too Many Direct Permissions

Too many direct grants become hard to manage.

Fix:

```text
combine roles, groups, and permissions
```

---

## Bug 4 — No Audit Trail

Permission changes are security-sensitive.

Always audit.

---

# 19. Interview Notes

If interviewer asks:

```text
How do you design permission-based access control?
```

Answer:

```text
1. Model permission as resource + action.
2. Store grants per user/group/role.
3. On request, build required permission.
4. Resolve effective user permissions.
5. Check contains(requiredPermission).
6. Deny by default.
7. Cache effective permissions.
8. Audit grant/revoke events.
9. Support tenant/resource ownership later.
```

Strong follow-up:

```text
RBAC is simple for broad access.
PBAC is better for fine-grained exceptions.
```

---

# 20. Next Step

Next file:

```text
011_Authorization_Filter.md
```

In the next phase, we add:

```text
authorization filter
request pipeline
JWT principal extraction
permission check before controller
```
