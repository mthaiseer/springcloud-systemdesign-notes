# MiniAuth / IAM Phase 19 — OIDC ID Token Basics

> NOTE:
>
> This phase includes:
>
> - Clickable index
> - Step-by-step Java code comments
> - Production-style formatting
> - OAuth2 vs OIDC explanation
> - ID Token generation
> - Identity claims
> - Nonce validation concept
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
- [9. OAuth2 vs OIDC](#9-oauth2-vs-oidc)
- [10. ID Token Flow](#10-id-token-flow)
- [11. Complete Java Code](#11-complete-java-code)
  - [11.1 UserProfile.java](#111-userprofilejava)
  - [11.2 OidcClientRequest.java](#112-oidcclientrequestjava)
  - [11.3 IdTokenClaims.java](#113-idtokenclaimsjava)
  - [11.4 IdTokenService.java](#114-idtokenservicejava)
  - [11.5 UserInfoService.java](#115-userinfoservicejava)
  - [11.6 Phase019Driver.java](#116-phase019driverjava)
- [12. How To Run](#12-how-to-run)
- [13. Dry Run](#13-dry-run)
- [14. What Changed From Phase 18](#14-what-changed-from-phase-18)
- [15. What This Phase Does NOT Do Yet](#15-what-this-phase-does-not-do-yet)
- [16. DSA / CP Concepts Used](#16-dsa--cp-concepts-used)
- [17. System Design Relevance](#17-system-design-relevance)
- [18. Production-Grade Concepts](#18-production-grade-concepts)
- [19. Common Bugs](#19-common-bugs)
- [20. Interview Notes](#20-interview-notes)
- [21. Next Step](#21-next-step)

---

# 1. Goal

In this phase, we add the basics of OpenID Connect.

Before this phase:

```text
OAuth2 gives authorization code / access token
```

After this phase:

```text
OIDC gives ID token containing user identity claims
```

Main objective:

```text
Understand and implement a simple ID Token model.
```

---

# 2. Why This Phase Matters

OAuth2 answers:

```text
Can this client access a resource?
```

OIDC answers:

```text
Who is the logged-in user?
```

OIDC is built on top of OAuth2.

It adds:

```text
ID Token
identity claims
nonce
userinfo endpoint
standard identity layer
```

This is the foundation of:

```text
Login with Google
Login with Microsoft
Login with GitHub-style identity providers
Auth0
Okta
Keycloak
Azure AD
AWS Cognito
```

---

# 3. What We Built Previously

## Phase 18

We built:

```text
OAuth2 Client Model
client_id
client_secret
redirect_uri
authorization code
scope validation
```

Now Phase 19 adds:

```text
ID Token
identity claims
OIDC user info
nonce
```

---

# 4. Previous Limitation

OAuth2 alone does not standardize authentication identity.

Access token is mainly for:

```text
API access
```

But clients also need:

```text
user id
email
name
issuer
login time
```

OIDC adds this through:

```text
ID Token
```

---

# 5. What We Build

We add:

```text
UserProfile
OidcClientRequest
IdTokenClaims
IdTokenService
UserInfoService
```

Flow:

```text
OAuth2 authorization succeeds
    -> generate ID Token
    -> include subject/email/name/issuer/audience/nonce
    -> client validates ID Token
```

---

# 6. Current Architecture

```text
+---------------------------+
| OAuth2 Authorization      |
| user authenticated        |
+-------------+-------------+
              |
              v
+---------------------------+
| IdTokenService            |
| build identity claims     |
| sign ID token             |
+-------------+-------------+
              |
              v
+---------------------------+
| Client receives ID Token  |
| validates claims          |
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
                    ├── oidc/
                    │   ├── UserProfile.java
                    │   ├── OidcClientRequest.java
                    │   ├── IdTokenClaims.java
                    │   ├── IdTokenService.java
                    │   └── UserInfoService.java
                    └── driver/
                        └── Phase019Driver.java
```

---

# 8. Core Concepts

## 8.1 OIDC

OIDC means:

```text
OpenID Connect
```

It is an identity layer on top of OAuth2.

---

## 8.2 ID Token

An ID Token is a signed JWT containing identity claims.

Example claims:

```text
iss
sub
aud
email
name
iat
exp
nonce
```

---

## 8.3 Subject

`sub` means:

```text
unique user id from issuer
```

Important:

```text
sub should be stable and unique per issuer
```

---

## 8.4 Audience

`aud` means:

```text
which client this ID token is for
```

Client must verify:

```text
aud == my client_id
```

---

## 8.5 Nonce

Nonce prevents replay attacks.

Client sends nonce in auth request.

ID token must return same nonce.

---

# 9. OAuth2 vs OIDC

## OAuth2

```text
delegated authorization
```

Used for:

```text
access APIs
```

## OIDC

```text
authentication identity layer
```

Used for:

```text
login
identity claims
SSO
```

Simple difference:

```text
OAuth2 = access
OIDC   = identity
```

---

# 10. ID Token Flow

```text
1. Client starts login with:
   client_id, redirect_uri, scope=openid profile email, nonce

2. User authenticates.

3. Authorization server generates:
   access token
   ID token

4. Client validates ID token:
   signature
   issuer
   audience
   expiry
   nonce

5. Client trusts identity claims.
```

---

# 11. Complete Java Code

---

## 11.1 `UserProfile.java`

```java
package com.miniauth.oidc;

public class UserProfile {

    private final String userId;
    private final String email;
    private final String fullName;

    public UserProfile(
            String userId,
            String email,
            String fullName
    ) {

        // =====================================================
        // STEP 1: Store stable user identity
        // =====================================================

        this.userId = userId;

        // =====================================================
        // STEP 2: Store normalized email
        // =====================================================

        this.email = email.toLowerCase().trim();

        // =====================================================
        // STEP 3: Store display name
        // =====================================================

        this.fullName = fullName;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
```

---

## 11.2 `OidcClientRequest.java`

```java
package com.miniauth.oidc;

import java.util.Set;

public class OidcClientRequest {

    private final String clientId;
    private final Set<String> scopes;
    private final String nonce;

    public OidcClientRequest(
            String clientId,
            Set<String> scopes,
            String nonce
    ) {

        // client requesting identity information

        this.clientId = clientId;
        this.scopes = scopes;
        this.nonce = nonce;
    }

    public String getClientId() {
        return clientId;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public String getNonce() {
        return nonce;
    }

    public boolean isOpenIdRequest() {
        return scopes.contains("openid");
    }
}
```

---

## 11.3 `IdTokenClaims.java`

```java
package com.miniauth.oidc;

public class IdTokenClaims {

    private final String issuer;
    private final String subject;
    private final String audience;
    private final String email;
    private final String name;
    private final long issuedAt;
    private final long expiresAt;
    private final String nonce;

    public IdTokenClaims(
            String issuer,
            String subject,
            String audience,
            String email,
            String name,
            long issuedAt,
            long expiresAt,
            String nonce
    ) {
        this.issuer = issuer;
        this.subject = subject;
        this.audience = audience;
        this.email = email;
        this.name = name;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.nonce = nonce;
    }

    public String toJson() {

        // =====================================================
        // Build simple JSON payload manually for learning.
        // Production should use a JSON library.
        // =====================================================

        return "{"
                + "\"iss\":\"" + issuer + "\","
                + "\"sub\":\"" + subject + "\","
                + "\"aud\":\"" + audience + "\","
                + "\"email\":\"" + email + "\","
                + "\"name\":\"" + name + "\","
                + "\"iat\":" + issuedAt + ","
                + "\"exp\":" + expiresAt + ","
                + "\"nonce\":\"" + nonce + "\""
                + "}";
    }

    @Override
    public String toString() {
        return toJson();
    }
}
```

---

## 11.4 `IdTokenService.java`

```java
package com.miniauth.oidc;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

public class IdTokenService {

    private static final String HMAC_ALGORITHM =
            "HmacSHA256";

    private final String issuer;
    private final String signingSecret;
    private final long idTokenTtlSeconds;

    public IdTokenService(
            String issuer,
            String signingSecret,
            long idTokenTtlSeconds
    ) {
        this.issuer = issuer;
        this.signingSecret = signingSecret;
        this.idTokenTtlSeconds = idTokenTtlSeconds;
    }

    public String generateIdToken(
            UserProfile userProfile,
            OidcClientRequest request
    ) {

        // =====================================================
        // STEP 1: Ensure request is actually OIDC
        // =====================================================

        if (!request.isOpenIdRequest()) {
            throw new IllegalArgumentException(
                    "Missing openid scope"
            );
        }

        // =====================================================
        // STEP 2: Prepare timestamps
        // =====================================================

        long issuedAt =
                Instant.now().getEpochSecond();

        long expiresAt =
                issuedAt + idTokenTtlSeconds;

        // =====================================================
        // STEP 3: Build JWT header
        // =====================================================

        String headerJson = "{"
                + "\"alg\":\"HS256\","
                + "\"typ\":\"JWT\""
                + "}";

        // =====================================================
        // STEP 4: Build ID token claims
        // =====================================================

        IdTokenClaims claims =
                new IdTokenClaims(
                        issuer,
                        userProfile.getUserId(),
                        request.getClientId(),
                        userProfile.getEmail(),
                        userProfile.getFullName(),
                        issuedAt,
                        expiresAt,
                        request.getNonce()
                );

        // =====================================================
        // STEP 5: Base64Url encode header and payload
        // =====================================================

        String encodedHeader =
                base64Url(headerJson);

        String encodedPayload =
                base64Url(claims.toJson());

        // =====================================================
        // STEP 6: Sign header.payload
        // =====================================================

        String unsignedToken =
                encodedHeader + "." + encodedPayload;

        String signature =
                sign(unsignedToken);

        // =====================================================
        // STEP 7: Return ID Token
        // =====================================================

        return unsignedToken + "." + signature;
    }

    private String sign(String data) {

        try {
            Mac mac =
                    Mac.getInstance(HMAC_ALGORITHM);

            SecretKeySpec keySpec =
                    new SecretKeySpec(
                            signingSecret.getBytes(
                                    StandardCharsets.UTF_8
                            ),
                            HMAC_ALGORITHM
                    );

            mac.init(keySpec);

            byte[] signatureBytes =
                    mac.doFinal(
                            data.getBytes(StandardCharsets.UTF_8)
                    );

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(signatureBytes);

        } catch (Exception ex) {
            throw new RuntimeException(
                    "ID Token signing failed",
                    ex
            );
        }
    }

    private String base64Url(String value) {

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        value.getBytes(StandardCharsets.UTF_8)
                );
    }
}
```

---

## 11.5 `UserInfoService.java`

```java
package com.miniauth.oidc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserInfoService {

    private final Map<String, UserProfile> usersById =
            new HashMap<>();

    public void save(UserProfile userProfile) {

        // userId -> profile claims

        usersById.put(
                userProfile.getUserId(),
                userProfile
        );
    }

    public Optional<UserProfile> findByUserId(
            String userId
    ) {
        return Optional.ofNullable(
                usersById.get(userId)
        );
    }
}
```

---

## 11.6 `Phase019Driver.java`

```java
package com.miniauth.driver;

import com.miniauth.oidc.IdTokenService;
import com.miniauth.oidc.OidcClientRequest;
import com.miniauth.oidc.UserInfoService;
import com.miniauth.oidc.UserProfile;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;

public class Phase019Driver {

    public static void main(String[] args) {

        // =====================================================
        // STEP 1: Create user profile
        // =====================================================

        UserProfile userProfile =
                new UserProfile(
                        "user-123",
                        "mohamed@example.com",
                        "Mohamed Thaiseer"
                );

        UserInfoService userInfoService =
                new UserInfoService();

        userInfoService.save(userProfile);

        // =====================================================
        // STEP 2: Create OIDC client request
        // =====================================================

        OidcClientRequest request =
                new OidcClientRequest(
                        "client-reporting-app",
                        Set.of("openid", "profile", "email"),
                        "nonce-abc-123"
                );

        // =====================================================
        // STEP 3: Generate ID Token
        // =====================================================

        IdTokenService idTokenService =
                new IdTokenService(
                        "https://auth.miniauth.local",
                        "change-this-id-token-secret",
                        15 * 60
                );

        String idToken =
                idTokenService.generateIdToken(
                        userProfile,
                        request
                );

        System.out.println("ID Token:");
        System.out.println(idToken);

        // =====================================================
        // STEP 4: Show token parts
        // =====================================================

        String[] parts = idToken.split("\\.");

        System.out.println();
        System.out.println("ID Token parts count:");
        System.out.println(parts.length);

        // =====================================================
        // STEP 5: Decode payload for learning
        // =====================================================

        String payloadJson =
                new String(
                        Base64.getUrlDecoder()
                                .decode(parts[1]),
                        StandardCharsets.UTF_8
                );

        System.out.println();
        System.out.println("Decoded ID Token payload:");
        System.out.println(payloadJson);

        // =====================================================
        // STEP 6: Fetch user info
        // =====================================================

        System.out.println();
        System.out.println("UserInfo endpoint result:");
        System.out.println(
                userInfoService
                        .findByUserId("user-123")
                        .orElse(null)
        );
    }
}
```

---

# 12. How To Run

## IntelliJ

1. Create Java project `MiniAuth`
2. Create packages shown above.
3. Add all classes.
4. Run:

```text
Phase019Driver
```

---

## Command Line

Compile:

```bash
javac -d out src/main/java/com/miniauth/**/*.java
```

Run:

```bash
java -cp out com.miniauth.driver.Phase019Driver
```

---

# 13. Dry Run

OIDC request:

```text
client_id = client-reporting-app
scope = openid profile email
nonce = nonce-abc-123
```

User:

```text
sub = user-123
email = mohamed@example.com
name = Mohamed Thaiseer
```

ID token payload:

```json
{
  "iss": "https://auth.miniauth.local",
  "sub": "user-123",
  "aud": "client-reporting-app",
  "email": "mohamed@example.com",
  "name": "Mohamed Thaiseer",
  "iat": 123,
  "exp": 456,
  "nonce": "nonce-abc-123"
}
```

Client must validate:

```text
signature
issuer
audience
expiry
nonce
```

---

# 14. What Changed From Phase 18

## Phase 18

```text
OAuth2 client model and authorization code
```

## Phase 19

```text
OIDC ID token and identity claims
```

New classes:

```text
UserProfile
OidcClientRequest
IdTokenClaims
IdTokenService
UserInfoService
```

---

# 15. What This Phase Does NOT Do Yet

This phase does not yet include:

```text
real ID token verification
JWK endpoint
RS256 key pair
discovery document
/.well-known/openid-configuration
userinfo endpoint HTTP API
token endpoint integration
consent screen
```

These are production OIDC topics.

---

# 16. DSA / CP Concepts Used

## HashMap

UserInfo storage:

```text
userId -> UserProfile
```

Expected:

```text
O(1)
```

---

## String Encoding

JWT uses:

```text
Base64Url(header)
Base64Url(payload)
```

---

## HMAC Signing

Signature:

```text
HMAC(secret, header.payload)
```

---

## Claim Validation

Client validates claims like a set of guard conditions:

```text
iss valid
aud valid
exp valid
nonce valid
```

---

# 17. System Design Relevance

OIDC is used by:

```text
Google Login
Microsoft Entra ID
Auth0
Okta
Keycloak
AWS Cognito
enterprise SSO
```

Architecture:

```text
Client
  -> Authorization Server
  -> ID Token
  -> Client validates identity
```

---

# 18. Production-Grade Concepts

## Discovery Document

OIDC providers expose:

```text
/.well-known/openid-configuration
```

It tells clients:

```text
issuer
authorization_endpoint
token_endpoint
jwks_uri
userinfo_endpoint
```

---

## JWK Set

Production systems publish public keys through:

```text
jwks_uri
```

Clients verify ID tokens using public keys.

---

## RS256 Preferred

For OIDC, production often uses:

```text
RS256
```

Private key signs.

Public key verifies.

---

## Nonce Validation

Client sends nonce.

ID token returns nonce.

Client checks:

```text
returned nonce == original nonce
```

---

## ID Token vs Access Token

ID token:

```text
for client authentication context
```

Access token:

```text
for API authorization
```

Do not use ID token as API access token.

---

# 19. Common Bugs

## Bug 1 — Using OAuth2 As Login Without OIDC

OAuth2 alone is not an authentication protocol.

OIDC adds authentication layer.

---

## Bug 2 — Not Checking Audience

Client must check:

```text
aud == my client_id
```

---

## Bug 3 — Not Checking Nonce

Can allow replay attacks.

---

## Bug 4 — Putting Secrets In ID Token

ID token is signed, not encrypted.

Do not put secrets inside it.

---

# 20. Interview Notes

If interviewer asks:

```text
What is OIDC?
```

Answer:

```text
OIDC is an identity layer on top of OAuth2.
OAuth2 gives delegated access.
OIDC adds ID Token, user identity claims, nonce, userinfo endpoint, and discovery metadata.
```

If interviewer asks:

```text
What should client validate in ID Token?
```

Answer:

```text
1. Signature.
2. Issuer.
3. Audience.
4. Expiry.
5. Nonce.
6. Subject.
7. Algorithm/key id.
```

Strong follow-up:

```text
ID token is for authentication. Access token is for API authorization.
```

---

# 21. Next Step

Next file:

```text
020_Production_Grade_IAM_Service.md
```

In the next phase, we combine everything into:

```text
production-grade IAM architecture
Spring Security mapping
Redis/PostgreSQL/Kafka integration
scaling path
security hardening checklist
