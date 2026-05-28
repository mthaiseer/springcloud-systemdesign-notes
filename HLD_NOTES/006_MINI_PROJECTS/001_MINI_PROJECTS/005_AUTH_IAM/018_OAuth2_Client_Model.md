# MiniAuth / IAM Phase 18 — OAuth2 Client Model

> NOTE:
>
> This phase includes:
>
> - Clickable index
> - Step-by-step Java code comments
> - OAuth2 client registration model
> - Client ID and client secret
> - Redirect URI validation
> - Authorization code creation
> - Complete runnable driver
> - System design and interview notes

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
- [9. OAuth2 Authorization Code Flow](#9-oauth2-authorization-code-flow)
- [10. Complete Java Code](#10-complete-java-code)
  - [10.1 OAuthClient.java](#101-oauthclientjava)
  - [10.2 OAuthClientRepository.java](#102-oauthclientrepositoryjava)
  - [10.3 AuthorizationCode.java](#103-authorizationcodejava)
  - [10.4 AuthorizationCodeStore.java](#104-authorizationcodestorejava)
  - [10.5 OAuthClientService.java](#105-oauthclientservicejava)
  - [10.6 OAuthAuthorizationService.java](#106-oauthauthorizationservicejava)
  - [10.7 Phase018Driver.java](#107-phase018driverjava)
- [11. How To Run](#11-how-to-run)
- [12. Dry Run](#12-dry-run)
- [13. What Changed From Phase 17](#13-what-changed-from-phase-17)
- [14. What This Phase Does NOT Do Yet](#14-what-this-phase-does-not-do-yet)
- [15. DSA / CP Concepts Used](#15-dsa--cp-concepts-used)
- [16. System Design Relevance](#16-system-design-relevance)
- [17. Production-Grade Concepts](#17-production-grade-concepts)
- [18. Common Bugs](#18-common-bugs)
- [19. Interview Notes](#19-interview-notes)
- [20. Next Step](#20-next-step)

---

# 1. Goal

In this phase, we add the OAuth2 client model.

Before this phase:

```text
MiniAuth authenticates first-party users only
```

After this phase:

```text
MiniAuth can register OAuth2 clients
validate redirect URI
issue authorization code
```

Main objective:

```text
Model the foundation of OAuth2 authorization code flow.
```

---

# 2. Why This Phase Matters

OAuth2 is used when one application wants delegated access.

Examples:

```text
Login with Google
Login with GitHub
Connect Slack app
Authorize third-party SaaS integration
```

OAuth2 introduces a new actor:

```text
client application
```

The client needs:

```text
client_id
client_secret
redirect_uri
allowed scopes
```

---

# 3. What We Built Previously

Previous phases covered:

```text
registration
login
JWT
refresh tokens
logout
RBAC / PBAC
authorization filter
multi-tenancy
email verification
password reset
login rate limiting
audit logs
MFA OTP
```

Now we move toward enterprise IAM / SSO concepts.

---

# 4. Previous Limitation

Until now, MiniAuth only had:

```text
user login
token generation
internal authorization
```

But OAuth2 needs:

```text
registered client applications
redirect URI validation
authorization codes
scope requests
```

---

# 5. What We Build

We add:

```text
OAuthClient
OAuthClientRepository
AuthorizationCode
AuthorizationCodeStore
OAuthClientService
OAuthAuthorizationService
```

Flow:

```text
register client
    -> client_id + client_secret

authorize request
    -> validate client
    -> validate redirect URI
    -> validate scope
    -> create authorization code
```

---

# 6. Current Architecture

```text
+---------------------------+
| Third-party App           |
| client_id + redirect_uri  |
+-------------+-------------+
              |
              v
+---------------------------+
| OAuthAuthorizationService |
| validate client           |
| validate redirect URI     |
| create auth code          |
+-------------+-------------+
              |
              v
+---------------------------+
| AuthorizationCodeStore    |
| code -> metadata          |
+-------------+-------------+
              |
              v
+---------------------------+
| Redirect back to client   |
| redirect_uri?code=...     |
+---------------------------+
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
                    ├── oauth/
                    │   ├── OAuthClient.java
                    │   ├── OAuthClientRepository.java
                    │   ├── AuthorizationCode.java
                    │   ├── AuthorizationCodeStore.java
                    │   ├── OAuthClientService.java
                    │   └── OAuthAuthorizationService.java
                    └── driver/
                        └── Phase018Driver.java
```

---

# 8. Core Concepts

## 8.1 OAuth Client

A client is an application that wants to use MiniAuth for delegated authorization.

Example:

```text
calendar-app
billing-dashboard
third-party-reporting-tool
```

---

## 8.2 Client ID

Public identifier of the app.

Example:

```text
client_abc123
```

It is not secret.

---

## 8.3 Client Secret

Private credential of confidential clients.

Example:

```text
server-side web app
backend service
```

Public clients like SPAs/mobile apps cannot safely store secrets.

---

## 8.4 Redirect URI

After authorization, MiniAuth redirects the user back to client.

The redirect URI must be pre-registered.

Never allow arbitrary redirect URI.

---

## 8.5 Authorization Code

Short-lived code created after user authorization.

Later exchanged for tokens.

---

# 9. OAuth2 Authorization Code Flow

```text
1. Client redirects user to auth server.
2. Request includes:
   client_id
   redirect_uri
   scope
   state
3. Auth server validates client.
4. User logs in and consents.
5. Auth server creates authorization code.
6. Auth server redirects:
   redirect_uri?code=...&state=...
7. Client exchanges code for tokens.
```

This phase covers steps:

```text
client validation
redirect URI validation
authorization code generation
```

---

# 10. Complete Java Code

---

## 10.1 `OAuthClient.java`

```java
package com.miniauth.oauth;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class OAuthClient {

    private final String clientId;
    private final String clientSecret;
    private final String clientName;
    private final Set<String> redirectUris =
            new HashSet<>();
    private final Set<String> allowedScopes =
            new HashSet<>();

    public OAuthClient(
            String clientName,
            String clientSecret
    ) {

        // =====================================================
        // STEP 1: Generate public client id
        // =====================================================

        this.clientId =
                "client_" + UUID.randomUUID();

        // =====================================================
        // STEP 2: Store client secret
        // =====================================================
        //
        // In production, store hash of secret, not raw secret.
        // =====================================================

        this.clientSecret = clientSecret;
        this.clientName = clientName;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getClientName() {
        return clientName;
    }

    public Set<String> getRedirectUris() {
        return redirectUris;
    }

    public Set<String> getAllowedScopes() {
        return allowedScopes;
    }

    public void addRedirectUri(String redirectUri) {
        redirectUris.add(redirectUri);
    }

    public void addScope(String scope) {
        allowedScopes.add(scope);
    }

    public boolean isRedirectUriAllowed(
            String redirectUri
    ) {
        return redirectUris.contains(redirectUri);
    }

    public boolean areScopesAllowed(
            Set<String> requestedScopes
    ) {
        return allowedScopes.containsAll(
                requestedScopes
        );
    }

    @Override
    public String toString() {
        return "OAuthClient{" +
                "clientId='" + clientId + '\'' +
                ", clientName='" + clientName + '\'' +
                ", redirectUris=" + redirectUris +
                ", allowedScopes=" + allowedScopes +
                '}';
    }
}
```

---

## 10.2 `OAuthClientRepository.java`

```java
package com.miniauth.oauth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OAuthClientRepository {

    private final Map<String, OAuthClient> clientsById =
            new HashMap<>();

    public void save(OAuthClient client) {

        // clientId -> client metadata

        clientsById.put(
                client.getClientId(),
                client
        );
    }

    public Optional<OAuthClient> findByClientId(
            String clientId
    ) {
        return Optional.ofNullable(
                clientsById.get(clientId)
        );
    }
}
```

---

## 10.3 `AuthorizationCode.java`

```java
package com.miniauth.oauth;

import java.time.Instant;
import java.util.Set;

public class AuthorizationCode {

    private final String code;
    private final String clientId;
    private final String userId;
    private final String redirectUri;
    private final Set<String> scopes;
    private final Instant expiresAt;
    private boolean used;

    public AuthorizationCode(
            String code,
            String clientId,
            String userId,
            String redirectUri,
            Set<String> scopes,
            Instant expiresAt
    ) {
        this.code = code;
        this.clientId = clientId;
        this.userId = userId;
        this.redirectUri = redirectUri;
        this.scopes = scopes;
        this.expiresAt = expiresAt;
        this.used = false;
    }

    public String getCode() {
        return code;
    }

    public String getClientId() {
        return clientId;
    }

    public String getUserId() {
        return userId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public Set<String> getScopes() {
        return scopes;
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
        return "AuthorizationCode{" +
                "code='" + code + '\'' +
                ", clientId='" + clientId + '\'' +
                ", userId='" + userId + '\'' +
                ", redirectUri='" + redirectUri + '\'' +
                ", scopes=" + scopes +
                ", expiresAt=" + expiresAt +
                ", used=" + used +
                '}';
    }
}
```

---

## 10.4 `AuthorizationCodeStore.java`

```java
package com.miniauth.oauth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AuthorizationCodeStore {

    private final Map<String, AuthorizationCode> codes =
            new HashMap<>();

    public void save(AuthorizationCode code) {

        // code value -> authorization code metadata

        codes.put(code.getCode(), code);
    }

    public Optional<AuthorizationCode> findByCode(
            String code
    ) {
        return Optional.ofNullable(
                codes.get(code)
        );
    }

    public void delete(String code) {
        codes.remove(code);
    }

    public int size() {
        return codes.size();
    }
}
```

---

## 10.5 `OAuthClientService.java`

```java
package com.miniauth.oauth;

public class OAuthClientService {

    private final OAuthClientRepository repository;

    public OAuthClientService(
            OAuthClientRepository repository
    ) {
        this.repository = repository;
    }

    public OAuthClient registerClient(
            String clientName,
            String clientSecret
    ) {

        // =====================================================
        // STEP 1: Create new OAuth client
        // =====================================================

        OAuthClient client =
                new OAuthClient(
                        clientName,
                        clientSecret
                );

        // =====================================================
        // STEP 2: Configure allowed redirect URI
        // =====================================================

        client.addRedirectUri(
                "https://client.example.com/callback"
        );

        // =====================================================
        // STEP 3: Configure allowed scopes
        // =====================================================

        client.addScope("profile.read");
        client.addScope("email.read");

        // =====================================================
        // STEP 4: Save client
        // =====================================================

        repository.save(client);

        return client;
    }

    public boolean authenticateClient(
            String clientId,
            String clientSecret
    ) {

        // =====================================================
        // Client authentication for confidential clients.
        // =====================================================

        OAuthClient client =
                repository.findByClientId(clientId)
                        .orElse(null);

        if (client == null) {
            return false;
        }

        return client.getClientSecret()
                .equals(clientSecret);
    }
}
```

---

## 10.6 `OAuthAuthorizationService.java`

```java
package com.miniauth.oauth;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Set;

public class OAuthAuthorizationService {

    private final OAuthClientRepository clientRepository;
    private final AuthorizationCodeStore codeStore;
    private final SecureRandom random =
            new SecureRandom();

    public OAuthAuthorizationService(
            OAuthClientRepository clientRepository,
            AuthorizationCodeStore codeStore
    ) {
        this.clientRepository = clientRepository;
        this.codeStore = codeStore;
    }

    public String authorize(
            String clientId,
            String redirectUri,
            Set<String> requestedScopes,
            String userId,
            String state
    ) {

        // =====================================================
        // STEP 1: Validate client exists
        // =====================================================

        OAuthClient client =
                clientRepository.findByClientId(clientId)
                        .orElse(null);

        if (client == null) {
            throw new IllegalArgumentException(
                    "Invalid client_id"
            );
        }

        // =====================================================
        // STEP 2: Validate exact redirect URI match
        // =====================================================

        if (!client.isRedirectUriAllowed(redirectUri)) {
            throw new IllegalArgumentException(
                    "Invalid redirect_uri"
            );
        }

        // =====================================================
        // STEP 3: Validate requested scopes
        // =====================================================

        if (!client.areScopesAllowed(requestedScopes)) {
            throw new IllegalArgumentException(
                    "Invalid scope requested"
            );
        }

        // =====================================================
        // STEP 4: Generate short-lived authorization code
        // =====================================================

        String codeValue = generateCode();

        AuthorizationCode code =
                new AuthorizationCode(
                        codeValue,
                        clientId,
                        userId,
                        redirectUri,
                        requestedScopes,
                        Instant.now().plus(
                                5,
                                ChronoUnit.MINUTES
                        )
                );

        // =====================================================
        // STEP 5: Store code server-side
        // =====================================================

        codeStore.save(code);

        // =====================================================
        // STEP 6: Build redirect URL
        // =====================================================

        return redirectUri
                + "?code=" + codeValue
                + "&state=" + state;
    }

    private String generateCode() {

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}
```

---

## 10.7 `Phase018Driver.java`

```java
package com.miniauth.driver;

import com.miniauth.oauth.AuthorizationCodeStore;
import com.miniauth.oauth.OAuthAuthorizationService;
import com.miniauth.oauth.OAuthClient;
import com.miniauth.oauth.OAuthClientRepository;
import com.miniauth.oauth.OAuthClientService;

import java.util.Set;

public class Phase018Driver {

    public static void main(String[] args) {

        // =====================================================
        // STEP 1: Create repositories and services
        // =====================================================

        OAuthClientRepository clientRepository =
                new OAuthClientRepository();

        AuthorizationCodeStore codeStore =
                new AuthorizationCodeStore();

        OAuthClientService clientService =
                new OAuthClientService(
                        clientRepository
                );

        OAuthAuthorizationService authorizationService =
                new OAuthAuthorizationService(
                        clientRepository,
                        codeStore
                );

        // =====================================================
        // STEP 2: Register OAuth2 client
        // =====================================================

        OAuthClient client =
                clientService.registerClient(
                        "Demo Reporting App",
                        "super-secret-client-password"
                );

        System.out.println("Registered OAuth client:");
        System.out.println(client);

        // =====================================================
        // STEP 3: Authenticate client using client secret
        // =====================================================

        boolean clientAuthenticated =
                clientService.authenticateClient(
                        client.getClientId(),
                        "super-secret-client-password"
                );

        System.out.println();
        System.out.println("Client authenticated?");
        System.out.println(clientAuthenticated);

        // =====================================================
        // STEP 4: Simulate authorization request
        // =====================================================

        String redirectUrl =
                authorizationService.authorize(
                        client.getClientId(),
                        "https://client.example.com/callback",
                        Set.of("profile.read", "email.read"),
                        "user-123",
                        "csrf-state-xyz"
                );

        System.out.println();
        System.out.println("Redirect URL:");
        System.out.println(redirectUrl);

        System.out.println();
        System.out.println("Authorization code store size:");
        System.out.println(codeStore.size());

        // =====================================================
        // STEP 5: Invalid redirect URI attack
        // =====================================================

        try {
            authorizationService.authorize(
                    client.getClientId(),
                    "https://evil.example.com/callback",
                    Set.of("profile.read"),
                    "user-123",
                    "state"
            );
        } catch (IllegalArgumentException ex) {
            System.out.println();
            System.out.println("Invalid redirect blocked:");
            System.out.println(ex.getMessage());
        }
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
Phase018Driver
```

---

## Command Line

Compile:

```bash
javac -d out src/main/java/com/miniauth/**/*.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase018Driver
```

---

# 12. Dry Run

Client registration:

```text
clientName = Demo Reporting App
clientId generated
clientSecret stored
redirectUri added
scopes added
```

Authorization request:

```text
client_id valid
redirect_uri valid
scope valid
user authenticated
```

System creates:

```text
authorization code
```

Redirect:

```text
https://client.example.com/callback?code=...&state=...
```

Invalid redirect URI:

```text
blocked
```

---

# 13. What Changed From Phase 17

## Phase 17

```text
MFA OTP login
```

## Phase 18

```text
OAuth2 client application model
```

New classes:

```text
OAuthClient
AuthorizationCode
OAuthClientService
OAuthAuthorizationService
```

---

# 14. What This Phase Does NOT Do Yet

This phase does not yet include:

```text
token endpoint
authorization code exchange
PKCE
consent screen
refresh tokens for OAuth clients
OIDC ID token
JWK keys
public/private client distinction
```

These come next.

---

# 15. DSA / CP Concepts Used

## HashMap

Client lookup:

```text
clientId -> OAuthClient
```

Authorization code lookup:

```text
code -> AuthorizationCode
```

Expected:

```text
O(1)
```

---

## Set Validation

Scope validation:

```text
allowedScopes contains all requestedScopes
```

---

## Random Token Generation

Authorization code uses:

```text
SecureRandom
```

---

## TTL

Authorization code expires after:

```text
5 minutes
```

---

# 16. System Design Relevance

OAuth2 client model is used in:

```text
Google OAuth
GitHub OAuth
Auth0
Okta
Keycloak
AWS Cognito
Azure AD
```

Architecture:

```text
Client App
  -> Authorization Server
  -> User Consent
  -> Authorization Code
  -> Token Endpoint
```

---

# 17. Production-Grade Concepts

## Redirect URI Exact Match

Never allow partial matching.

Bad:

```text
startsWith("https://client.example.com")
```

Good:

```text
exact registered URI match
```

---

## Client Secret Hashing

Production should store:

```text
hash(client_secret)
```

not raw secret.

---

## PKCE

Public clients should use:

```text
PKCE
```

because mobile/SPAs cannot protect secrets.

---

## State Parameter

Use state to prevent CSRF.

```text
state generated by client
returned unchanged by auth server
client verifies it
```

---

## Short-Lived Authorization Code

Authorization code should be:

```text
single-use
short-lived
bound to client_id
bound to redirect_uri
```

---

# 18. Common Bugs

## Bug 1 — Open Redirect

Allowing arbitrary redirect URI can leak authorization code to attacker.

---

## Bug 2 — Long-Lived Authorization Code

Authorization code should expire quickly.

---

## Bug 3 — Reusable Authorization Code

Authorization code must be single-use.

---

## Bug 4 — Ignoring State

Ignoring state enables CSRF attacks.

---

# 19. Interview Notes

If interviewer asks:

```text
How does OAuth2 authorization code flow work?
```

Answer:

```text
1. Client is registered with client_id and redirect_uri.
2. Client redirects user to auth server.
3. Auth server validates client_id and redirect_uri.
4. User authenticates and consents.
5. Auth server creates short-lived authorization code.
6. Auth server redirects back with code and state.
7. Client exchanges code at token endpoint.
8. Auth server validates client, code, redirect_uri.
9. Auth server returns access token.
```

Strong follow-up:

```text
Authorization code must be short-lived, single-use, and bound to client and redirect URI.
```

---

# 20. Next Step

Next file:

```text
019_OIDC_ID_Token_Basics.md
```

In the next phase, we add:

```text
OpenID Connect
ID token
identity claims
nonce
user info
