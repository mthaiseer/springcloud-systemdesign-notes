# MiniAuth / IAM Phase 11 — Authorization Filter

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why This Phase Matters](#2-why-this-phase-matters)
- [3. What We Built Previously](#3-what-we-built-previously)
- [4. Previous Limitation](#4-previous-limitation)
- [5. What We Build](#5-what-we-build)
- [6. Current Architecture](#6-current-architecture)
- [7. Folder Structure](#7-folder-structure)
- [8. Core Concepts](#8-core-concepts)
- [9. Request Pipeline](#9-request-pipeline)
- [10. Complete Java Code](#10-complete-java-code)
  - [10.1 Permission.java](#101-permissionjava)
  - [10.2 ResourceType.java](#102-resourcetypejava)
  - [10.3 ResourceAction.java](#103-resourceactionjava)
  - [10.4 AuthenticatedPrincipal.java](#104-authenticatedprincipaljava)
  - [10.5 HttpRequest.java](#105-httprequestjava)
  - [10.6 HttpResponse.java](#106-httpresponsejava)
  - [10.7 SecurityContext.java](#107-securitycontextjava)
  - [10.8 TokenAuthenticationService.java](#108-tokenauthenticationservicejava)
  - [10.9 PermissionStore.java](#109-permissionstorejava)
  - [10.10 PermissionAuthorizationService.java](#1010-permissionauthorizationservicejava)
  - [10.11 AuthorizationRule.java](#1011-authorizationrulejava)
  - [10.12 AuthorizationFilter.java](#1012-authorizationfilterjava)
  - [10.13 ProtectedController.java](#1013-protectedcontrollerjava)
  - [10.14 Phase011Driver.java](#1014-phase011driverjava)
- [11. How To Run](#11-how-to-run)
- [12. Dry Run](#12-dry-run)
- [13. What Changed From Phase 10](#13-what-changed-from-phase-10)
- [14. What This Phase Does NOT Do Yet](#14-what-this-phase-does-not-do-yet)
- [15. DSA / CP Concepts Used](#15-dsa--cp-concepts-used)
- [16. System Design Relevance](#16-system-design-relevance)
- [17. Production-Grade Concepts](#17-production-grade-concepts)
- [18. Common Bugs](#18-common-bugs)
- [19. Interview Notes](#19-interview-notes)
- [20. Next Step](#20-next-step)

---

# 1. Goal

In this phase, we move from manual permission checks to an authorization filter.

Before this phase:

```text
controller manually calls authorizationService.authorize()
```

After this phase:

```text
request -> auth filter -> controller
```

Main objective:

```text
Block unauthorized requests before business logic runs.
```

---

# 2. Why This Phase Matters

In production systems, authorization should not be scattered everywhere.

Bad design:

```java
if (hasPermission(user, permission)) {
    doBusinessLogic();
}
```

inside every controller.

Better design:

```text
centralized authorization filter
```

The filter:

```text
1. extracts token
2. authenticates user
3. checks permission
4. allows or blocks request
```

---

# 3. What We Built Previously

## Phase 9

```text
Role Based Access Control
```

## Phase 10

```text
Permission Based Access Control
```

Phase 10 could check:

```text
does user have permission?
```

But controller had to call it manually.

Phase 11 moves this into a filter.

---

# 4. Previous Limitation

Manual checks are risky.

Developer may forget:

```text
authorization check
```

Then endpoint becomes exposed.

Example bug:

```text
DELETE /users/{id}
```

works without permission check.

Authorization filter solves this by making security part of the request pipeline.

---

# 5. What We Build

We add:

```text
HttpRequest
HttpResponse
SecurityContext
TokenAuthenticationService
AuthorizationRule
AuthorizationFilter
ProtectedController
```

Flow:

```text
HttpRequest
    -> AuthorizationFilter
        -> authenticate token
        -> resolve required permission
        -> authorize
        -> call controller
```

---

# 6. Current Architecture

```text
+----------------------+
| Client Request       |
| Authorization header |
+----------+-----------+
           |
           v
+----------------------+
| AuthorizationFilter  |
| - extract token      |
| - authenticate       |
| - check permission   |
+----------+-----------+
           |
           v
+----------------------+
| SecurityContext      |
| principal stored     |
+----------+-----------+
           |
           v
+----------------------+
| Controller           |
| business logic       |
+----------------------+
```

Failure path:

```text
missing token  -> 401 Unauthorized
bad permission -> 403 Forbidden
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
                    │   ├── PermissionStore.java
                    │   ├── PermissionAuthorizationService.java
                    │   └── AuthorizationRule.java
                    ├── filter/
                    │   └── AuthorizationFilter.java
                    ├── http/
                    │   ├── HttpRequest.java
                    │   └── HttpResponse.java
                    ├── security/
                    │   ├── AuthenticatedPrincipal.java
                    │   ├── SecurityContext.java
                    │   └── TokenAuthenticationService.java
                    ├── controller/
                    │   └── ProtectedController.java
                    └── driver/
                        └── Phase011Driver.java
```

---

# 8. Core Concepts

## 8.1 Filter

A filter runs before controller logic.

It can:

```text
allow request
reject request
add user context
log request
rate limit request
```

---

## 8.2 Security Context

Security context stores authenticated user for current request.

Example:

```text
current principal = mohamed@example.com
```

Controller can access it without re-parsing token.

---

## 8.3 401 vs 403

401 Unauthorized:

```text
user is not authenticated
```

Examples:

```text
missing token
invalid token
expired token
```

403 Forbidden:

```text
user is authenticated but not allowed
```

Examples:

```text
missing permission
wrong role
tenant mismatch
```

---

## 8.4 Authorization Rule

An authorization rule maps:

```text
HTTP method + path -> required permission
```

Example:

```text
DELETE /users -> USER:DELETE
GET /profile -> PROFILE:READ
```

---

# 9. Request Pipeline

```text
Client
  |
  v
AuthorizationFilter
  |
  +-- missing/invalid token -> 401
  |
  +-- authenticated
        |
        v
     permission check
        |
        +-- missing permission -> 403
        |
        +-- allowed
              |
              v
          Controller
              |
              v
          200 OK
```

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

    public static Permission of(
            ResourceType resourceType,
            ResourceAction action
    ) {
        return new Permission(resourceType, action);
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public ResourceAction getAction() {
        return action;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;

        if (!(other instanceof Permission)) {
            return false;
        }

        Permission that = (Permission) other;

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
    CONFIGURE
}
```

---

## 10.4 `AuthenticatedPrincipal.java`

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

## 10.5 `HttpRequest.java`

```java
package com.miniauth.http;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> headers =
            new HashMap<>();

    public HttpRequest(String method, String path) {
        this.method = method.toUpperCase();
        this.path = path;
    }

    public void addHeader(
            String name,
            String value
    ) {
        headers.put(name.toLowerCase(), value);
    }

    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}
```

---

## 10.6 `HttpResponse.java`

```java
package com.miniauth.http;

public class HttpResponse {

    private final int statusCode;
    private final String body;

    public HttpResponse(
            int statusCode,
            String body
    ) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public static HttpResponse ok(String body) {
        return new HttpResponse(200, body);
    }

    public static HttpResponse unauthorized(String body) {
        return new HttpResponse(401, body);
    }

    public static HttpResponse forbidden(String body) {
        return new HttpResponse(403, body);
    }

    public static HttpResponse notFound(String body) {
        return new HttpResponse(404, body);
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusCode=" + statusCode +
                ", body='" + body + '\'' +
                '}';
    }
}
```

---

## 10.7 `SecurityContext.java`

```java
package com.miniauth.security;

public class SecurityContext {

    private AuthenticatedPrincipal principal;

    public void setPrincipal(
            AuthenticatedPrincipal principal
    ) {
        this.principal = principal;
    }

    public AuthenticatedPrincipal getPrincipal() {
        return principal;
    }

    public void clear() {
        this.principal = null;
    }
}
```

---

## 10.8 `TokenAuthenticationService.java`

### Logic before this class

This simulates JWT verification.

In earlier phases, JWT verification would return principal.

For this phase, we keep a simple token registry.

```java
package com.miniauth.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TokenAuthenticationService {

    private final Map<String, AuthenticatedPrincipal>
            principalsByToken = new HashMap<>();

    public void registerToken(
            String token,
            AuthenticatedPrincipal principal
    ) {
        principalsByToken.put(token, principal);
    }

    public Optional<AuthenticatedPrincipal> authenticate(
            String authorizationHeader
    ) {
        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }

        String token =
                authorizationHeader.substring("Bearer ".length());

        return Optional.ofNullable(
                principalsByToken.get(token)
        );
    }
}
```

---

## 10.9 `PermissionStore.java`

```java
package com.miniauth.authz;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PermissionStore {

    private final Map<String, Set<Permission>>
            permissionsByUserId = new HashMap<>();

    public void grant(
            String userId,
            Permission permission
    ) {
        permissionsByUserId
                .computeIfAbsent(
                        userId,
                        key -> new HashSet<>()
                )
                .add(permission);
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
}
```

---

## 10.10 `PermissionAuthorizationService.java`

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

    public boolean isAllowed(
            AuthenticatedPrincipal principal,
            Permission permission
    ) {
        if (principal == null || permission == null) {
            return false;
        }

        return permissionStore.hasPermission(
                principal.getUserId(),
                permission
        );
    }
}
```

---

## 10.11 `AuthorizationRule.java`

### Logic before this class

This maps an endpoint to a required permission.

```java
package com.miniauth.authz;

public class AuthorizationRule {

    private final String method;
    private final String path;
    private final Permission permission;

    public AuthorizationRule(
            String method,
            String path,
            Permission permission
    ) {
        this.method = method.toUpperCase();
        this.path = path;
        this.permission = permission;
    }

    public boolean matches(
            String method,
            String path
    ) {
        return this.method.equalsIgnoreCase(method)
                && this.path.equals(path);
    }

    public Permission getPermission() {
        return permission;
    }
}
```

---

## 10.12 `AuthorizationFilter.java`

### Logic before this class

This is the main class of Phase 11.

It does:

```text
1. Find endpoint rule.
2. Authenticate token.
3. Put principal into security context.
4. Check permission.
5. Call controller if allowed.
```

```java
package com.miniauth.filter;

import com.miniauth.authz.AuthorizationRule;
import com.miniauth.authz.Permission;
import com.miniauth.authz.PermissionAuthorizationService;
import com.miniauth.controller.ProtectedController;
import com.miniauth.http.HttpRequest;
import com.miniauth.http.HttpResponse;
import com.miniauth.security.AuthenticatedPrincipal;
import com.miniauth.security.SecurityContext;
import com.miniauth.security.TokenAuthenticationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthorizationFilter {

    private final TokenAuthenticationService
            tokenAuthenticationService;

    private final PermissionAuthorizationService
            authorizationService;

    private final SecurityContext securityContext;

    private final ProtectedController controller;

    private final List<AuthorizationRule> rules =
            new ArrayList<>();

    public AuthorizationFilter(
            TokenAuthenticationService tokenAuthenticationService,
            PermissionAuthorizationService authorizationService,
            SecurityContext securityContext,
            ProtectedController controller
    ) {
        this.tokenAuthenticationService =
                tokenAuthenticationService;
        this.authorizationService =
                authorizationService;
        this.securityContext = securityContext;
        this.controller = controller;
    }

    public void addRule(AuthorizationRule rule) {
        rules.add(rule);
    }

    public HttpResponse doFilter(HttpRequest request) {

        securityContext.clear();

        AuthorizationRule matchedRule =
                findRule(request);

        if (matchedRule == null) {
            return HttpResponse.notFound(
                    "No route found"
            );
        }

        Optional<AuthenticatedPrincipal> optionalPrincipal =
                tokenAuthenticationService.authenticate(
                        request.getHeader("Authorization")
                );

        if (optionalPrincipal.isEmpty()) {
            return HttpResponse.unauthorized(
                    "Missing or invalid token"
            );
        }

        AuthenticatedPrincipal principal =
                optionalPrincipal.get();

        securityContext.setPrincipal(principal);

        Permission requiredPermission =
                matchedRule.getPermission();

        boolean allowed =
                authorizationService.isAllowed(
                        principal,
                        requiredPermission
                );

        if (!allowed) {
            return HttpResponse.forbidden(
                    "Missing permission: " +
                            requiredPermission
            );
        }

        return controller.handle(request);
    }

    private AuthorizationRule findRule(
            HttpRequest request
    ) {
        for (AuthorizationRule rule : rules) {
            if (rule.matches(
                    request.getMethod(),
                    request.getPath()
            )) {
                return rule;
            }
        }

        return null;
    }
}
```

---

## 10.13 `ProtectedController.java`

### Logic before this class

Controller now assumes request already passed authorization filter.

```java
package com.miniauth.controller;

import com.miniauth.http.HttpRequest;
import com.miniauth.http.HttpResponse;
import com.miniauth.security.SecurityContext;

public class ProtectedController {

    private final SecurityContext securityContext;

    public ProtectedController(
            SecurityContext securityContext
    ) {
        this.securityContext = securityContext;
    }

    public HttpResponse handle(HttpRequest request) {

        String email =
                securityContext.getPrincipal().getEmail();

        if (request.getMethod().equals("GET") &&
                request.getPath().equals("/profile")) {
            return HttpResponse.ok(
                    "Profile returned for " + email
            );
        }

        if (request.getMethod().equals("DELETE") &&
                request.getPath().equals("/users")) {
            return HttpResponse.ok(
                    "User deleted by " + email
            );
        }

        if (request.getMethod().equals("POST") &&
                request.getPath().equals("/payments/refund")) {
            return HttpResponse.ok(
                    "Payment refunded by " + email
            );
        }

        return HttpResponse.notFound(
                "No controller route found"
        );
    }
}
```

---

## 10.14 `Phase011Driver.java`

### Logic before this class

This driver proves:

```text
1. Missing token -> 401
2. Valid token + permission -> 200
3. Valid token + missing permission -> 403
4. Unknown route -> 404
```

```java
package com.miniauth.driver;

import com.miniauth.authz.AuthorizationRule;
import com.miniauth.authz.Permission;
import com.miniauth.authz.PermissionAuthorizationService;
import com.miniauth.authz.PermissionStore;
import com.miniauth.authz.ResourceAction;
import com.miniauth.authz.ResourceType;
import com.miniauth.controller.ProtectedController;
import com.miniauth.filter.AuthorizationFilter;
import com.miniauth.http.HttpRequest;
import com.miniauth.http.HttpResponse;
import com.miniauth.security.AuthenticatedPrincipal;
import com.miniauth.security.SecurityContext;
import com.miniauth.security.TokenAuthenticationService;

public class Phase011Driver {

    public static void main(String[] args) {

        SecurityContext securityContext =
                new SecurityContext();

        TokenAuthenticationService tokenAuthService =
                new TokenAuthenticationService();

        PermissionStore permissionStore =
                new PermissionStore();

        PermissionAuthorizationService authzService =
                new PermissionAuthorizationService(
                        permissionStore
                );

        ProtectedController controller =
                new ProtectedController(
                        securityContext
                );

        AuthorizationFilter filter =
                new AuthorizationFilter(
                        tokenAuthService,
                        authzService,
                        securityContext,
                        controller
                );

        Permission profileRead =
                Permission.of(
                        ResourceType.PROFILE,
                        ResourceAction.READ
                );

        Permission userDelete =
                Permission.of(
                        ResourceType.USER,
                        ResourceAction.DELETE
                );

        Permission paymentRefund =
                Permission.of(
                        ResourceType.PAYMENT,
                        ResourceAction.REFUND
                );

        filter.addRule(
                new AuthorizationRule(
                        "GET",
                        "/profile",
                        profileRead
                )
        );

        filter.addRule(
                new AuthorizationRule(
                        "DELETE",
                        "/users",
                        userDelete
                )
        );

        filter.addRule(
                new AuthorizationRule(
                        "POST",
                        "/payments/refund",
                        paymentRefund
                )
        );

        AuthenticatedPrincipal principal =
                new AuthenticatedPrincipal(
                        "user-123",
                        "mohamed@example.com"
                );

        String token = "valid-token-123";

        tokenAuthService.registerToken(
                token,
                principal
        );

        permissionStore.grant(
                principal.getUserId(),
                profileRead
        );

        permissionStore.grant(
                principal.getUserId(),
                paymentRefund
        );

        HttpRequest missingTokenRequest =
                new HttpRequest(
                        "GET",
                        "/profile"
                );

        print(
                "Missing token request",
                filter.doFilter(missingTokenRequest)
        );

        HttpRequest profileRequest =
                new HttpRequest(
                        "GET",
                        "/profile"
                );

        profileRequest.addHeader(
                "Authorization",
                "Bearer " + token
        );

        print(
                "Profile request",
                filter.doFilter(profileRequest)
        );

        HttpRequest deleteUserRequest =
                new HttpRequest(
                        "DELETE",
                        "/users"
                );

        deleteUserRequest.addHeader(
                "Authorization",
                "Bearer " + token
        );

        print(
                "Delete user request",
                filter.doFilter(deleteUserRequest)
        );

        HttpRequest refundRequest =
                new HttpRequest(
                        "POST",
                        "/payments/refund"
                );

        refundRequest.addHeader(
                "Authorization",
                "Bearer " + token
        );

        print(
                "Refund request",
                filter.doFilter(refundRequest)
        );

        HttpRequest unknownRoute =
                new HttpRequest(
                        "GET",
                        "/unknown"
                );

        unknownRoute.addHeader(
                "Authorization",
                "Bearer " + token
        );

        print(
                "Unknown route request",
                filter.doFilter(unknownRoute)
        );
    }

    private static void print(
            String title,
            HttpResponse response
    ) {
        System.out.println();
        System.out.println("==== " + title + " ====");
        System.out.println(response);
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
Phase011Driver
```

---

## Command Line

Compile:

```bash
javac -d out src/main/java/com/miniauth/**/*.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase011Driver
```

---

# 12. Dry Run

## Case 1 — Missing Token

Request:

```text
GET /profile
Authorization: missing
```

Filter result:

```text
401 Unauthorized
```

---

## Case 2 — Valid Token + Permission

Request:

```text
GET /profile
Authorization: Bearer valid-token-123
```

User permission:

```text
PROFILE:READ
```

Filter result:

```text
200 OK
```

---

## Case 3 — Valid Token + Missing Permission

Request:

```text
DELETE /users
Authorization: Bearer valid-token-123
```

Required permission:

```text
USER:DELETE
```

User does not have it.

Filter result:

```text
403 Forbidden
```

---

# 13. What Changed From Phase 10

## Phase 10

```text
manual permission checks
```

## Phase 11

```text
centralized authorization filter
```

New classes:

```text
AuthorizationFilter
SecurityContext
HttpRequest
HttpResponse
AuthorizationRule
ProtectedController
```

New capability:

```text
request is blocked before controller logic
```

---

# 14. What This Phase Does NOT Do Yet

This phase does not yet include:

```text
real servlet filter
Spring Security filter chain
JWT parsing from real token
path pattern matching
method annotations
multi-tenant checks
ownership checks
ABAC policies
audit logs
rate limiting
```

These come later.

---

# 15. DSA / CP Concepts Used

## Linear Rule Matching

Rules are checked one by one:

```text
for each rule:
    if rule matches request
```

Complexity:

```text
O(R)
```

where `R` is number of route rules.

---

## HashMap Lookup

Token authentication uses:

```text
token -> principal
```

Permission store uses:

```text
userId -> permissions
```

Expected:

```text
O(1)
```

---

## Pipeline Thinking

Request flows through staged processing:

```text
authn -> authz -> controller
```

This is similar to chain-of-responsibility design.

---

# 16. System Design Relevance

Authorization filters appear in:

```text
Spring Security
API Gateway
Envoy external auth
NGINX auth_request
Kubernetes admission controllers
microservice middleware
```

Typical flow:

```text
Client
  -> API Gateway
  -> Authentication Filter
  -> Authorization Filter
  -> Service
```

---

# 17. Production-Grade Concepts

## Spring Security Equivalent

This phase maps to:

```text
OncePerRequestFilter
SecurityContextHolder
Authentication
GrantedAuthority
AccessDecisionManager
AuthorizationManager
```

---

## Path Pattern Matching

Production systems need:

```text
/users/{id}
/orders/{orderId}/refund
/tenants/{tenantId}/users
```

This phase uses exact path matching only.

---

## Deny By Default

If no rule exists:

```text
deny or 404
```

Never silently allow unknown protected endpoints.

---

## Filter Ordering

Typical order:

```text
CORS
logging
rate limit
authentication
authorization
controller
```

---

# 18. Common Bugs

## Bug 1 — Controller Runs Before Authorization

Bad:

```text
business logic executes before permission check
```

Good:

```text
filter checks before controller
```

---

## Bug 2 — 401/403 Confusion

Wrong:

```text
missing permission -> 401
```

Correct:

```text
missing token -> 401
missing permission -> 403
```

---

## Bug 3 — Missing Route Rule Means Allow

Bad:

```text
no rule -> allow
```

Good:

```text
no rule -> deny or not found
```

---

## Bug 4 — Trusting Client Claims Directly

Never trust:

```text
client supplied userId
client supplied role
client supplied permission
```

Always derive principal from verified token.

---

# 19. Interview Notes

If interviewer asks:

```text
How do you enforce authorization in APIs?
```

Answer:

```text
1. Extract token from request.
2. Verify token.
3. Build authenticated principal.
4. Resolve endpoint required permission.
5. Check user permissions.
6. Return 401 if unauthenticated.
7. Return 403 if unauthorized.
8. Execute controller only if allowed.
9. Deny by default.
10. Add audit logs for denied requests.
```

Strong follow-up:

```text
Authorization should be centralized in middleware/filter, not scattered inside every controller.
```

---

# 20. Next Step

Next file:

```text
012_Multi_Tenant_User_Model.md
```

In the next phase, we add:

```text
tenant id
organization model
tenant-aware users
tenant-aware authorization
cross-tenant protection
```
