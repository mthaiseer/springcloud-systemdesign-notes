# MiniAuth / IAM Phase 9 — Role Based Access Control (RBAC)

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why This Phase Matters](#2-why-this-phase-matters)
- [3. What We Built Previously](#3-what-we-built-previously)
- [4. Previous Limitation](#4-previous-limitation)
- [5. What We Build](#5-what-we-build)
- [6. Current Architecture](#6-current-architecture)
- [7. Folder Structure](#7-folder-structure)
- [8. Core Concepts](#8-core-concepts)
- [9. RBAC Flow](#9-rbac-flow)
- [10. Complete Java Code](#10-complete-java-code)
  - [10.1 Permission.java](#101-permissionjava)
  - [10.2 Role.java](#102-rolejava)
  - [10.3 User.java](#103-userjava)
  - [10.4 AuthenticatedPrincipal.java](#104-authenticatedprincipaljava)
  - [10.5 AuthorizationRequest.java](#105-authorizationrequestjava)
  - [10.6 AuthorizationResponse.java](#106-authorizationresponsejava)
  - [10.7 RolePermissionRegistry.java](#107-rolepermissionregistryjava)
  - [10.8 AuthorizationService.java](#108-authorizationservicejava)
  - [10.9 ProtectedApiSimulator.java](#109-protectedapisimulatorjava)
  - [10.10 Phase009Driver.java](#1010-phase009driverjava)
- [11. How To Run](#11-how-to-run)
- [12. Dry Run](#12-dry-run)
- [13. What Changed From Phase 8](#13-what-changed-from-phase-8)
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
authentication
```

to:

```text
authorization
```

Before this phase:

```text
system knows WHO user is
```

After this phase:

```text
system knows WHAT user can do
```

Main objective:

```text
Implement Role Based Access Control (RBAC).
```

---

# 2. Why This Phase Matters

Authentication answers:

```text
Who are you?
```

Authorization answers:

```text
What are you allowed to do?
```

Example:

```text
USER can read profile
ADMIN can delete users
SUPPORT can reset password
```

Without authorization:

```text
every authenticated user can do everything
```

Which is dangerous.

---

# 3. What We Built Previously

## Phase 5

```text
JWT access token generation
```

## Phase 6

```text
JWT verification
```

## Phase 7

```text
refresh token flow
```

## Phase 8

```text
logout + token revocation
```

Now Phase 9 adds:

```text
roles
permissions
access checks
```

---

# 4. Previous Limitation

Previously:

```text
authenticated user == fully trusted user
```

Problem:

```text
normal user could access admin endpoint
```

Need:

```text
fine-grained authorization
```

---

# 5. What We Build

We add:

```text
Role enum
Permission enum
RolePermissionRegistry
AuthorizationService
ProtectedApiSimulator
```

Flow:

```text
authenticated principal
    -> roles
    -> permissions
    -> authorization decision
```

---

# 6. Current Architecture

```text
+---------------------------+
| JWT Verification          |
| returns principal         |
+-------------+-------------+
              |
              v
+---------------------------+
| AuthorizationService      |
| - check roles             |
| - check permissions       |
+-------------+-------------+
              |
              v
+---------------------------+
| Protected API             |
| ADMIN endpoint            |
| USER endpoint             |
+---------------------------+
```

Authorization path:

```text
token
  -> principal
  -> principal roles
  -> permission lookup
  -> allow / deny
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
                    │   ├── Role.java
                    │   ├── RolePermissionRegistry.java
                    │   ├── AuthorizationRequest.java
                    │   ├── AuthorizationResponse.java
                    │   ├── AuthorizationService.java
                    │   └── ProtectedApiSimulator.java
                    ├── security/
                    │   └── AuthenticatedPrincipal.java
                    ├── model/
                    │   └── User.java
                    └── driver/
                        └── Phase009Driver.java
```

---

# 8. Core Concepts

## 8.1 Authentication vs Authorization

Authentication:

```text
verify identity
```

Authorization:

```text
verify access rights
```

---

## 8.2 Role

A role is a group of permissions.

Examples:

```text
ADMIN
USER
SUPPORT
AUDITOR
```

---

## 8.3 Permission

A permission represents an action.

Examples:

```text
USER_READ
USER_DELETE
PAYMENT_REFUND
PROFILE_UPDATE
```

---

## 8.4 RBAC

RBAC means:

```text
roles -> permissions
users -> roles
```

Instead of:

```text
user -> many direct permissions
```

This simplifies management.

---

# 9. RBAC Flow

```text
User logs in
    |
    v
JWT verified
    |
    v
AuthenticatedPrincipal
    |
    v
Roles loaded
    |
    v
RolePermissionRegistry
    |
    v
Permissions resolved
    |
    v
AuthorizationService
    |
    v
ALLOW or DENY
```

Example:

```text
ADMIN
    -> USER_READ
    -> USER_DELETE
    -> PAYMENT_REFUND

USER
    -> PROFILE_READ
    -> PROFILE_UPDATE
```

---

# 10. Complete Java Code

---

## 10.1 `Permission.java`

```java
package com.miniauth.authz;

public enum Permission {

    PROFILE_READ,
    PROFILE_UPDATE,

    USER_READ,
    USER_DELETE,

    PAYMENT_READ,
    PAYMENT_REFUND,

    REPORT_VIEW,

    SYSTEM_CONFIG_UPDATE
}
```

---

## 10.2 `Role.java`

```java
package com.miniauth.authz;

public enum Role {

    USER,
    SUPPORT,
    ADMIN,
    AUDITOR
}
```

---

## 10.3 `User.java`

```java
package com.miniauth.model;

import com.miniauth.authz.Role;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User {

    private final String id;
    private final String email;
    private final Set<Role> roles = new HashSet<>();

    public User(String email) {
        this.id = UUID.randomUUID().toString();
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                '}';
    }
}
```

---

## 10.4 `AuthenticatedPrincipal.java`

```java
package com.miniauth.security;

import com.miniauth.authz.Role;

import java.util.Set;

public class AuthenticatedPrincipal {

    private final String userId;
    private final String email;
    private final Set<Role> roles;

    public AuthenticatedPrincipal(
            String userId,
            String email,
            Set<Role> roles
    ) {
        this.userId = userId;
        this.email = email;
        this.roles = roles;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return "AuthenticatedPrincipal{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                '}';
    }
}
```

---

## 10.5 `AuthorizationRequest.java`

```java
package com.miniauth.authz;

import com.miniauth.security.AuthenticatedPrincipal;

public class AuthorizationRequest {

    private final AuthenticatedPrincipal principal;
    private final Permission requiredPermission;

    public AuthorizationRequest(
            AuthenticatedPrincipal principal,
            Permission requiredPermission
    ) {
        this.principal = principal;
        this.requiredPermission = requiredPermission;
    }

    public AuthenticatedPrincipal getPrincipal() {
        return principal;
    }

    public Permission getRequiredPermission() {
        return requiredPermission;
    }
}
```

---

## 10.6 `AuthorizationResponse.java`

```java
package com.miniauth.authz;

public class AuthorizationResponse {

    private final boolean allowed;
    private final String reason;

    public AuthorizationResponse(
            boolean allowed,
            String reason
    ) {
        this.allowed = allowed;
        this.reason = reason;
    }

    public boolean isAllowed() {
        return allowed;
    }

    @Override
    public String toString() {
        return "AuthorizationResponse{" +
                "allowed=" + allowed +
                ", reason='" + reason + '\'' +
                '}';
    }
}
```

---

## 10.7 `RolePermissionRegistry.java`

### Logic before this class

This maps:

```text
Role -> Set<Permission>
```

```java
package com.miniauth.authz;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class RolePermissionRegistry {

    private final Map<Role, Set<Permission>> rolePermissions =
            new EnumMap<>(Role.class);

    public RolePermissionRegistry() {

        rolePermissions.put(
                Role.USER,
                EnumSet.of(
                        Permission.PROFILE_READ,
                        Permission.PROFILE_UPDATE
                )
        );

        rolePermissions.put(
                Role.SUPPORT,
                EnumSet.of(
                        Permission.PROFILE_READ,
                        Permission.USER_READ,
                        Permission.REPORT_VIEW
                )
        );

        rolePermissions.put(
                Role.AUDITOR,
                EnumSet.of(
                        Permission.REPORT_VIEW,
                        Permission.PAYMENT_READ
                )
        );

        rolePermissions.put(
                Role.ADMIN,
                EnumSet.allOf(Permission.class)
        );
    }

    public Set<Permission> getPermissions(Role role) {
        return rolePermissions.getOrDefault(
                role,
                EnumSet.noneOf(Permission.class)
        );
    }
}
```

---

## 10.8 `AuthorizationService.java`

### Logic before this class

Authorization algorithm:

```text
1. Read user roles.
2. Expand roles to permissions.
3. Check required permission.
4. Return allow/deny.
```

```java
package com.miniauth.authz;

import com.miniauth.security.AuthenticatedPrincipal;

import java.util.HashSet;
import java.util.Set;

public class AuthorizationService {

    private final RolePermissionRegistry registry;

    public AuthorizationService(
            RolePermissionRegistry registry
    ) {
        this.registry = registry;
    }

    public AuthorizationResponse authorize(
            AuthorizationRequest request
    ) {

        if (request == null) {
            return deny("Request is null");
        }

        AuthenticatedPrincipal principal =
                request.getPrincipal();

        if (principal == null) {
            return deny("Principal is missing");
        }

        Permission requiredPermission =
                request.getRequiredPermission();

        if (requiredPermission == null) {
            return deny("Required permission missing");
        }

        Set<Permission> effectivePermissions =
                resolvePermissions(principal);

        if (effectivePermissions.contains(requiredPermission)) {
            return allow();
        }

        return deny(
                "Missing permission: " +
                        requiredPermission
        );
    }

    private Set<Permission> resolvePermissions(
            AuthenticatedPrincipal principal
    ) {

        Set<Permission> permissions =
                new HashSet<>();

        for (Role role : principal.getRoles()) {

            permissions.addAll(
                    registry.getPermissions(role)
            );
        }

        return permissions;
    }

    private AuthorizationResponse allow() {
        return new AuthorizationResponse(
                true,
                "Access granted"
        );
    }

    private AuthorizationResponse deny(String reason) {
        return new AuthorizationResponse(
                false,
                reason
        );
    }
}
```

---

## 10.9 `ProtectedApiSimulator.java`

### Logic before this class

This simulates protected endpoints.

```java
package com.miniauth.authz;

import com.miniauth.security.AuthenticatedPrincipal;

public class ProtectedApiSimulator {

    private final AuthorizationService authorizationService;

    public ProtectedApiSimulator(
            AuthorizationService authorizationService
    ) {
        this.authorizationService =
                authorizationService;
    }

    public void deleteUserApi(
            AuthenticatedPrincipal principal
    ) {

        AuthorizationResponse response =
                authorizationService.authorize(
                        new AuthorizationRequest(
                                principal,
                                Permission.USER_DELETE
                        )
                );

        System.out.println();
        System.out.println("DELETE USER API");

        if (response.isAllowed()) {
            System.out.println("User deleted successfully");
        } else {
            System.out.println("403 Forbidden");
            System.out.println(response);
        }
    }

    public void profileApi(
            AuthenticatedPrincipal principal
    ) {

        AuthorizationResponse response =
                authorizationService.authorize(
                        new AuthorizationRequest(
                                principal,
                                Permission.PROFILE_READ
                        )
                );

        System.out.println();
        System.out.println("PROFILE API");

        if (response.isAllowed()) {
            System.out.println("Profile data returned");
        } else {
            System.out.println("403 Forbidden");
            System.out.println(response);
        }
    }

    public void refundPaymentApi(
            AuthenticatedPrincipal principal
    ) {

        AuthorizationResponse response =
                authorizationService.authorize(
                        new AuthorizationRequest(
                                principal,
                                Permission.PAYMENT_REFUND
                        )
                );

        System.out.println();
        System.out.println("REFUND PAYMENT API");

        if (response.isAllowed()) {
            System.out.println("Payment refunded");
        } else {
            System.out.println("403 Forbidden");
            System.out.println(response);
        }
    }
}
```

---

## 10.10 `Phase009Driver.java`

### Logic before this class

This driver proves:

```text
1. USER role has limited access.
2. ADMIN role has full access.
3. Permission checks work correctly.
```

```java
package com.miniauth.driver;

import com.miniauth.authz.AuthorizationService;
import com.miniauth.authz.ProtectedApiSimulator;
import com.miniauth.authz.Role;
import com.miniauth.authz.RolePermissionRegistry;
import com.miniauth.model.User;
import com.miniauth.security.AuthenticatedPrincipal;

public class Phase009Driver {

    public static void main(String[] args) {

        RolePermissionRegistry registry =
                new RolePermissionRegistry();

        AuthorizationService authorizationService =
                new AuthorizationService(registry);

        ProtectedApiSimulator apiSimulator =
                new ProtectedApiSimulator(
                        authorizationService
                );

        User normalUser =
                new User("user@example.com");

        normalUser.addRole(Role.USER);

        User adminUser =
                new User("admin@example.com");

        adminUser.addRole(Role.ADMIN);

        AuthenticatedPrincipal userPrincipal =
                new AuthenticatedPrincipal(
                        normalUser.getId(),
                        normalUser.getEmail(),
                        normalUser.getRoles()
                );

        AuthenticatedPrincipal adminPrincipal =
                new AuthenticatedPrincipal(
                        adminUser.getId(),
                        adminUser.getEmail(),
                        adminUser.getRoles()
                );

        System.out.println("USER principal:");
        System.out.println(userPrincipal);

        apiSimulator.profileApi(userPrincipal);

        apiSimulator.deleteUserApi(userPrincipal);

        apiSimulator.refundPaymentApi(userPrincipal);

        System.out.println();
        System.out.println(
                "================================="
        );

        System.out.println();
        System.out.println("ADMIN principal:");
        System.out.println(adminPrincipal);

        apiSimulator.profileApi(adminPrincipal);

        apiSimulator.deleteUserApi(adminPrincipal);

        apiSimulator.refundPaymentApi(adminPrincipal);
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
Phase009Driver
```

---

## Command Line

Compile:

```bash
javac -d out src/main/java/com/miniauth/**/*.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase009Driver
```

---

# 12. Dry Run

User:

```text
roles = [USER]
```

Permissions resolved:

```text
PROFILE_READ
PROFILE_UPDATE
```

Profile API:

```text
requires PROFILE_READ
```

Result:

```text
ALLOW
```

Delete User API:

```text
requires USER_DELETE
```

Result:

```text
DENY
```

Admin:

```text
roles = [ADMIN]
```

Admin permissions:

```text
all permissions
```

Result:

```text
all APIs allowed
```

---

# 13. What Changed From Phase 8

## Phase 8

```text
system knows authenticated user
```

## Phase 9

```text
system knows allowed actions
```

New classes:

```text
Role
Permission
AuthorizationService
RolePermissionRegistry
```

New capability:

```text
authorization checks
```

---

# 14. What This Phase Does NOT Do Yet

This phase does not yet include:

```text
attribute-based access control (ABAC)
resource ownership checks
multi-tenant isolation
hierarchical roles
dynamic permissions
database-driven policies
OPA policy engine
field-level permissions
row-level security
```

These come later.

---

# 15. DSA / CP Concepts Used

## EnumSet

Permissions use:

```java
EnumSet
```

Efficient for enums.

Operations:

```text
contains -> O(1)
add      -> O(1)
```

---

## Set Union

Authorization resolves permissions:

```text
role1 permissions
UNION
role2 permissions
```

---

## Graph Thinking

Conceptually:

```text
User -> Roles -> Permissions
```

This is like a graph relationship model.

---

## Lookup Optimization

Registry precomputes:

```text
Role -> PermissionSet
```

Fast authorization lookup.

---

# 16. System Design Relevance

RBAC is used in:

```text
AWS IAM
Kubernetes RBAC
GitHub org permissions
banking systems
admin portals
enterprise SaaS
```

HLD:

```text
API Gateway
    -> Authentication
    -> Authorization
    -> Business service
```

At scale:

```text
central policy service
permission cache
distributed RBAC evaluation
```

---

# 17. Production-Grade Concepts

## RBAC vs ABAC

RBAC:

```text
role-based
```

ABAC:

```text
attribute-based
```

ABAC example:

```text
doctor can view patients only in same hospital
```

---

## Hierarchical Roles

Example:

```text
SUPER_ADMIN inherits ADMIN
ADMIN inherits USER
```

---

## Multi-Tenant Authorization

SaaS systems need:

```text
tenant-aware permissions
```

Example:

```text
ADMIN in tenant A
is not ADMIN in tenant B
```

---

## Policy Caching

Large IAM systems cache:

```text
resolved permissions
JWT claims
policy decisions
```

---

## External Policy Engines

Production systems may use:

```text
OPA
Cedar
AWS IAM policy engine
```

---

# 18. Common Bugs

## Bug 1 — Authentication Without Authorization

Problem:

```text
all logged-in users become admins
```

---

## Bug 2 — Hardcoded Admin Checks Everywhere

Bad:

```java
if (role == ADMIN)
```

everywhere.

Better:

```text
centralized authorization service
```

---

## Bug 3 — Missing Deny-By-Default

Bad:

```text
if permission unknown -> allow
```

Good:

```text
deny by default
```

---

## Bug 4 — Trusting Client Role

Never trust:

```text
frontend role field
```

Always validate server-side.

---

# 19. Interview Notes

If interviewer asks:

```text
How does RBAC work?
```

Answer:

```text
1. User authenticates.
2. System loads user roles.
3. Roles map to permissions.
4. Protected API requires permission.
5. Authorization service checks permission.
6. Allow or deny access.
7. Default deny.
8. Cache permissions for performance.
```

Strong follow-up:

```text
Authentication identifies user.
Authorization limits user actions.
```

---

# 20. Next Step

Next file:

```text
010_Permission_Based_Access_Control_PBAC.md
```

In the next phase, we add:

```text
fine-grained permissions
resource-level authorization
dynamic permission checks
custom policies
```
