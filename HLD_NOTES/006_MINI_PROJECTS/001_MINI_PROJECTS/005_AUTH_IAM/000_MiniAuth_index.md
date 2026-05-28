# MiniAuth / IAM — Tree Style Index

> Complete phase-by-phase index for the MiniAuth / IAM learning journey.

---

## 1. Full Tree

```text
MiniAuth-IAM/
├── 00_INDEX.md
│
├── 01_Foundation/
│   ├── 001_User_Registration.md
│   ├── 002_Password_Hashing_BCrypt.md
│   ├── 003_Login_Authentication.md
│   └── 004_Session_Management.md
│
├── 02_Token_Based_Authentication/
│   ├── 005_JWT_Token_Generation.md
│   ├── 006_JWT_Token_Verification.md
│   ├── 007_Refresh_Token_Flow.md
│   └── 008_Logout_And_Token_Revocation.md
│
├── 03_Authorization/
│   ├── 009_Role_Based_Access_Control_RBAC.md
│   ├── 010_Permission_Based_Access_Control.md
│   └── 011_Authorization_Filter.md
│
├── 04_Multi_Tenancy/
│   └── 012_Multi_Tenant_User_Model.md
│
├── 05_Account_Security/
│   ├── 013_Email_Verification.md
│   ├── 014_Password_Reset_Token.md
│   ├── 015_Login_Rate_Limiting.md
│   ├── 016_Audit_Log.md
│   └── 017_MFA_OTP_Login.md
│
├── 06_OAuth2_OIDC/
│   ├── 018_OAuth2_Client_Model.md
│   └── 019_OIDC_ID_Token_Basics.md
│
└── 07_Production/
    └── 020_Production_Grade_IAM_Service.md
```

---

## 2. Recommended Learning Path

```text
Foundation
001 -> 002 -> 003 -> 004

Token Security
005 -> 006 -> 007 -> 008

Authorization
009 -> 010 -> 011

SaaS / Multi-Tenant IAM
012

Account Security
013 -> 014 -> 015 -> 016 -> 017

Enterprise SSO
018 -> 019

Production Architecture
020
```

---

## 3. Phase Summary

| Phase | File | Main Concept |
|---:|---|---|
| 001 | `001_User_Registration.md` | Create user identity |
| 002 | `002_Password_Hashing_BCrypt.md` | Store password securely |
| 003 | `003_Login_Authentication.md` | Verify credentials |
| 004 | `004_Session_Management.md` | Server-side session |
| 005 | `005_JWT_Token_Generation.md` | Generate access token |
| 006 | `006_JWT_Token_Verification.md` | Verify JWT |
| 007 | `007_Refresh_Token_Flow.md` | Refresh access token |
| 008 | `008_Logout_And_Token_Revocation.md` | Logout and revoke tokens |
| 009 | `009_Role_Based_Access_Control_RBAC.md` | Role-based authorization |
| 010 | `010_Permission_Based_Access_Control.md` | Fine-grained permissions |
| 011 | `011_Authorization_Filter.md` | Request authorization pipeline |
| 012 | `012_Multi_Tenant_User_Model.md` | Tenant isolation |
| 013 | `013_Email_Verification.md` | Verify email ownership |
| 014 | `014_Password_Reset_Token.md` | Forgot/reset password |
| 015 | `015_Login_Rate_Limiting.md` | Brute-force protection |
| 016 | `016_Audit_Log.md` | Security audit trail |
| 017 | `017_MFA_OTP_Login.md` | MFA and OTP |
| 018 | `018_OAuth2_Client_Model.md` | OAuth2 client and auth code |
| 019 | `019_OIDC_ID_Token_Basics.md` | OIDC ID token |
| 020 | `020_Production_Grade_IAM_Service.md` | Production IAM architecture |

---

## 4. Mental Model

```text
Identity
  └── Who is the user?
      ├── Registration
      ├── Password
      ├── Login
      └── Email verification

Authentication
  └── Is this user valid now?
      ├── Session
      ├── JWT
      ├── Refresh token
      ├── Logout
      └── MFA

Authorization
  └── What can this user do?
      ├── RBAC
      ├── PBAC
      ├── Authorization filter
      └── Tenant boundary

Security Operations
  └── How do we protect and observe?
      ├── Rate limiting
      ├── Password reset
      ├── Audit logs
      └── Token revocation

Enterprise IAM
  └── How do apps integrate?
      ├── OAuth2
      ├── OIDC
      └── Production architecture
```

---

## 5. Real-World Mapping

```text
MiniAuth concept          Real-world equivalent
-------------------------------------------------------
User registration         Signup API
Password hashing          BCrypt / Argon2
Login authentication      Spring Security AuthenticationManager
Session management        Redis session store
JWT generation            Authorization Server
JWT verification          Resource Server / API Gateway
Refresh token             OAuth2 refresh flow
Logout/revocation         Token blacklist / session revocation
RBAC                      Spring GrantedAuthority / AWS IAM roles
PBAC                      Fine-grained IAM permissions
Authorization filter      OncePerRequestFilter
Multi-tenancy             SaaS tenant isolation
Email verification        Account activation
Password reset            Forgot password flow
Login rate limiting       Redis rate limiter
Audit log                 SIEM / compliance logging
MFA OTP                   Step-up authentication
OAuth2 client             Registered client app
OIDC ID token             Login identity token
Production IAM            Auth0 / Okta / Keycloak style platform
```

---

## 6. Final Completion Checklist

```text
[ ] Can explain registration flow
[ ] Can explain password hashing
[ ] Can implement login service
[ ] Can explain session vs JWT
[ ] Can generate and verify JWT
[ ] Can explain refresh token rotation
[ ] Can implement logout/revocation
[ ] Can explain RBAC
[ ] Can explain PBAC
[ ] Can build authorization filter
[ ] Can explain multi-tenant isolation
[ ] Can implement email verification
[ ] Can implement password reset
[ ] Can design login rate limiter
[ ] Can design audit log pipeline
[ ] Can explain MFA OTP
[ ] Can explain OAuth2 authorization code flow
[ ] Can explain OIDC ID token
[ ] Can design production IAM service
```

---

## 7. Best Next Mini Projects

After MiniAuth, continue with:

```text
MiniGateway
MiniRateLimiter
MiniRedis
MiniKafka
MiniServiceDiscovery
MiniScheduler
MiniObservability
MiniPaymentSystem
```

Reason:

```text
MiniAuth + MiniGateway + MiniRateLimiter + MiniRedis + MiniKafka
= strong foundation for real production distributed systems.
```
