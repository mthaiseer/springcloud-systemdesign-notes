# MiniAuth / IAM — 000_INDEX.md

## Clickable Tree Index

```text
MiniAuth/
├── 000_INDEX.md
├── 002_Password_Hashing_BCrypt.md
├── 003_Login_Authentication.md
├── 004_Session_Management.md
├── 005_JWT_Generation.md
├── 006_JWT_Verification.md
├── 007_JWT_Expiration_Refresh_Token.md
├── 008_RBAC_Roles.md
├── 009_Permissions_And_Scopes.md
├── 010_API_Key_Authentication.md
├── 011_Redis_Session_Store.md
├── 012_Login_Rate_Limiter.md
├── 013_Token_Blacklisting.md
├── 014_Refresh_Token_Rotation.md
├── 015_Email_OTP_Verification.md
├── 016_Two_Factor_Authentication.md
├── 017_TOTP_Google_Authenticator.md
├── 018_Password_Reset_Flow.md
├── 019_OAuth2_Authorization_Server.md
├── 020_OAuth2_Client_Credentials.md
├── 021_OAuth2_Authorization_Code.md
├── 022_OAuth2_PKCE.md
├── 023_OpenID_Connect_OIDC.md
├── 024_SSO_Single_Sign_On.md
├── 025_Social_Login_Google_GitHub.md
├── 026_API_Gateway_Authentication.md
├── 027_Microservice_Token_Propagation.md
├── 028_Distributed_Token_Revocation.md
├── 029_Audit_Logging.md
├── 030_Device_Management.md
├── 031_Concurrent_Session_Control.md
├── 032_Security_Event_Monitoring.md
├── 033_RSA_JWK_Key_Rotation.md
├── 034_Multi_Tenant_IAM.md
├── 035_SAML_Basics.md
└── 036_Production_MiniAuth_IAM.md
```

## Phase Links

- [000_INDEX.md](./000_INDEX.md) — MiniAuth / IAM Master Index
- [002_Password_Hashing_BCrypt.md](./002_Password_Hashing_BCrypt.md) — Password Hashing BCrypt
- [003_Login_Authentication.md](./003_Login_Authentication.md) — Login Authentication
- [004_Session_Management.md](./004_Session_Management.md) — Session Management
- [005_JWT_Generation.md](./005_JWT_Generation.md) — JWT Generation
- [006_JWT_Verification.md](./006_JWT_Verification.md) — JWT Verification
- [007_JWT_Expiration_Refresh_Token.md](./007_JWT_Expiration_Refresh_Token.md) — JWT Expiration And Refresh Token
- [008_RBAC_Roles.md](./008_RBAC_Roles.md) — RBAC Roles
- [009_Permissions_And_Scopes.md](./009_Permissions_And_Scopes.md) — Permissions And Scopes
- [010_API_Key_Authentication.md](./010_API_Key_Authentication.md) — API Key Authentication
- [011_Redis_Session_Store.md](./011_Redis_Session_Store.md) — Redis Session Store
- [012_Login_Rate_Limiter.md](./012_Login_Rate_Limiter.md) — Login Rate Limiter
- [013_Token_Blacklisting.md](./013_Token_Blacklisting.md) — Token Blacklisting
- [014_Refresh_Token_Rotation.md](./014_Refresh_Token_Rotation.md) — Refresh Token Rotation
- [015_Email_OTP_Verification.md](./015_Email_OTP_Verification.md) — Email OTP Verification
- [016_Two_Factor_Authentication.md](./016_Two_Factor_Authentication.md) — Two Factor Authentication
- [017_TOTP_Google_Authenticator.md](./017_TOTP_Google_Authenticator.md) — TOTP Google Authenticator
- [018_Password_Reset_Flow.md](./018_Password_Reset_Flow.md) — Password Reset Flow
- [019_OAuth2_Authorization_Server.md](./019_OAuth2_Authorization_Server.md) — OAuth2 Authorization Server
- [020_OAuth2_Client_Credentials.md](./020_OAuth2_Client_Credentials.md) — OAuth2 Client Credentials
- [021_OAuth2_Authorization_Code.md](./021_OAuth2_Authorization_Code.md) — OAuth2 Authorization Code
- [022_OAuth2_PKCE.md](./022_OAuth2_PKCE.md) — OAuth2 PKCE
- [023_OpenID_Connect_OIDC.md](./023_OpenID_Connect_OIDC.md) — OpenID Connect OIDC
- [024_SSO_Single_Sign_On.md](./024_SSO_Single_Sign_On.md) — SSO Single Sign On
- [025_Social_Login_Google_GitHub.md](./025_Social_Login_Google_GitHub.md) — Social Login Google GitHub
- [026_API_Gateway_Authentication.md](./026_API_Gateway_Authentication.md) — API Gateway Authentication
- [027_Microservice_Token_Propagation.md](./027_Microservice_Token_Propagation.md) — Microservice Token Propagation
- [028_Distributed_Token_Revocation.md](./028_Distributed_Token_Revocation.md) — Distributed Token Revocation
- [029_Audit_Logging.md](./029_Audit_Logging.md) — Audit Logging
- [030_Device_Management.md](./030_Device_Management.md) — Device Management
- [031_Concurrent_Session_Control.md](./031_Concurrent_Session_Control.md) — Concurrent Session Control
- [032_Security_Event_Monitoring.md](./032_Security_Event_Monitoring.md) — Security Event Monitoring
- [033_RSA_JWK_Key_Rotation.md](./033_RSA_JWK_Key_Rotation.md) — RSA JWK Key Rotation
- [034_Multi_Tenant_IAM.md](./034_Multi_Tenant_IAM.md) — Multi Tenant IAM
- [035_SAML_Basics.md](./035_SAML_Basics.md) — SAML Basics
- [036_Production_MiniAuth_IAM.md](./036_Production_MiniAuth_IAM.md) — Production MiniAuth IAM

---

## Phase Grouping

```text
Phase 1: Registration And Password Security
├── 001_User_Registration.md
├── 002_Password_Hashing_BCrypt.md
└── 003_Login_Authentication.md

Phase 2: Sessions And JWT
├── 004_Session_Management.md
├── 005_JWT_Generation.md
├── 006_JWT_Verification.md
└── 007_JWT_Expiration_Refresh_Token.md

Phase 3: Authorization
├── 008_RBAC_Roles.md
└── 009_Permissions_And_Scopes.md

Phase 4: Machine Auth And Sessions
├── 010_API_Key_Authentication.md
├── 011_Redis_Session_Store.md
├── 012_Login_Rate_Limiter.md
├── 013_Token_Blacklisting.md
└── 014_Refresh_Token_Rotation.md

Phase 5: Verification And MFA
├── 015_Email_OTP_Verification.md
├── 016_Two_Factor_Authentication.md
├── 017_TOTP_Google_Authenticator.md
└── 018_Password_Reset_Flow.md

Phase 6: OAuth2 / OIDC / SSO
├── 019_OAuth2_Authorization_Server.md
├── 020_OAuth2_Client_Credentials.md
├── 021_OAuth2_Authorization_Code.md
├── 022_OAuth2_PKCE.md
├── 023_OpenID_Connect_OIDC.md
├── 024_SSO_Single_Sign_On.md
└── 025_Social_Login_Google_GitHub.md

Phase 7: Distributed IAM
├── 026_API_Gateway_Authentication.md
├── 027_Microservice_Token_Propagation.md
└── 028_Distributed_Token_Revocation.md

Phase 8: Enterprise IAM
├── 029_Audit_Logging.md
├── 030_Device_Management.md
├── 031_Concurrent_Session_Control.md
├── 032_Security_Event_Monitoring.md
├── 033_RSA_JWK_Key_Rotation.md
├── 034_Multi_Tenant_IAM.md
├── 035_SAML_Basics.md
└── 036_Production_MiniAuth_IAM.md
```

---

## Recommended Learning Order

Start here:

```text
001_User_Registration.md
002_Password_Hashing_BCrypt.md
003_Login_Authentication.md
004_Session_Management.md
005_JWT_Generation.md
006_JWT_Verification.md
007_JWT_Expiration_Refresh_Token.md
```

These first seven files build the real authentication foundation.

---

## What This Project Teaches

```text
Authentication
Authorization
JWT
Refresh tokens
OAuth2
OIDC
SSO
MFA
RBAC
Permissions
API keys
API Gateway auth
Distributed token revocation
Audit logging
Multi-tenant IAM
Security monitoring
Key rotation
Production identity architecture
```

---

## Real-World Mapping

This project maps to systems like:

```text
Google Account Login
GitHub OAuth Login
Okta
Auth0
Keycloak
AWS Cognito
Azure AD
Enterprise IAM
Banking authentication
SaaS tenant identity
Microservice API security
```
