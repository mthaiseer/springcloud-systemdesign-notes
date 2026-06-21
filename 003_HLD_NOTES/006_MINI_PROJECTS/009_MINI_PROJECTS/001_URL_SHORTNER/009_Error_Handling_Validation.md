# 009_Error_Handling_Validation.md
# MiniURLShortener — Error Handling & Validation

> Core mental model: **Error handling and validation are the safety boundary of the API. Validation prevents bad input from entering the system, and error handling converts failures into predictable, debuggable, client-friendly responses.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Validation vs Error Handling](#4-validation-vs-error-handling)
- [5. Validation Layers Mental Model](#5-validation-layers-mental-model)
- [6. Error Categories](#6-error-categories)
- [7. HTTP Status Code Mapping](#7-http-status-code-mapping)
- [8. API Error Response Contract](#8-api-error-response-contract)
- [9. Request DTO Validation](#9-request-dto-validation)
- [10. Business Validation](#10-business-validation)
- [11. Database Constraint Validation](#11-database-constraint-validation)
- [12. Domain Exception Design](#12-domain-exception-design)
- [13. Global Exception Handler](#13-global-exception-handler)
- [14. Validation Error Formatting](#14-validation-error-formatting)
- [15. Create API Error Flow](#15-create-api-error-flow)
- [16. Redirect API Error Flow](#16-redirect-api-error-flow)
- [17. Security And Abuse Validation](#17-security-and-abuse-validation)
- [18. Logging And Correlation ID Mindset](#18-logging-and-correlation-id-mindset)
- [19. Step-by-Step Dry Runs](#19-step-by-step-dry-runs)
- [20. Internal Execution Walkthrough](#20-internal-execution-walkthrough)
- [21. Testing Strategy](#21-testing-strategy)
- [22. Production Failure Stories](#22-production-failure-stories)
- [23. Debugging Mindset](#23-debugging-mindset)
- [24. Common Mistakes](#24-common-mistakes)
- [25. Interview-Ready Explanation](#25-interview-ready-explanation)
- [26. Senior Engineer Checklist](#26-senior-engineer-checklist)
- [27. One-Page Cheat Sheet](#27-one-page-cheat-sheet)
- [28. One Picture To Remember](#28-one-picture-to-remember)

---

## 1. Why This Exists

By now MiniURLShortener has the foundation:

```text
Create API:
POST /api/v1/urls

Redirect API:
GET /{shortCode}
```

But production APIs are not only about happy paths.

Users will send:

```text
empty URLs
invalid URLs
expired timestamps
bad aliases
duplicate aliases
unknown short codes
blocked short codes
malicious inputs
oversized payloads
wrong JSON
```

Infrastructure will also fail:

```text
database unavailable
constraint violation
timeout
unexpected null
bad configuration
```

If the API handles errors badly, clients see:

```text
500 Internal Server Error
stack traces
inconsistent response shapes
unclear messages
wrong status codes
HTML error pages from backend APIs
```

Good APIs behave predictably.

Example bad error:

```json
{
  "error": "something went wrong"
}
```

Example better error:

```json
{
  "timestamp": "2026-06-21T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "INVALID_URL",
  "message": "longUrl must be a valid http or https URL",
  "path": "/api/v1/urls"
}
```

Production mental model:

```text
Validation protects the system.
Error handling teaches the client what went wrong.
Logging teaches engineers why it happened.
```

---

## 2. The One Core Mental Model

Error handling and validation are the:

```text
API SAFETY BOUNDARY
```

They sit between messy external input and clean internal business logic.

ASCII:

```text
                OUTSIDE WORLD
                     |
                     v
          +----------------------+
          | Validation Boundary  |
          | reject bad input     |
          +----------------------+
                     |
                     v
          +----------------------+
          | Business Logic       |
          | assumes cleaner data |
          +----------------------+
                     |
                     v
          +----------------------+
          | Database Constraints |
          | protect invariants   |
          +----------------------+
                     |
                     v
          +----------------------+
          | Error Handler        |
          | predictable response |
          +----------------------+
                     |
                     v
                  CLIENT
```

One-line memory:

```text
Validation decides what is allowed in; error handling decides how failures go out.
```

For MiniURLShortener:

```text
Bad longUrl should not enter service deeply.
Bad alias should not reach database blindly.
Duplicate short_code should not corrupt data.
Unknown redirect code should return 404, not 500.
Expired redirect code should return 410, not random JSON.
```

---

## 3. Problem Statement

Build a clean error handling and validation model for MiniURLShortener.

It must support:

```text
1. Request DTO validation errors.
2. Business validation errors.
3. Custom alias conflicts.
4. Generated short code failure.
5. Redirect not found.
6. Redirect expired.
7. Redirect blocked.
8. Invalid short code format.
9. Database constraint violations.
10. Unexpected errors.
```

It should produce:

```text
consistent JSON error response
correct HTTP status code
stable machine-readable error code
human-readable message
request path
timestamp
optional field errors
```

It should avoid:

```text
stack trace leakage
raw SQL error leakage
inconsistent shapes
wrong status codes
overly detailed security errors
```

Out of scope for this chapter:

```text
1. Full observability stack.
2. Distributed tracing implementation.
3. Rate limiting implementation.
4. UI error pages.
5. Internationalization.
```

This chapter creates the production-shaped error contract.

---

## 4. Validation vs Error Handling

Validation and error handling are related but different.

Validation:

```text
Checks whether input or state is acceptable.
```

Error handling:

```text
Converts failures into controlled responses.
```

Example validation:

```text
longUrl must not be blank
customAlias must match pattern
expiresAt must be future
shortCode must be valid format
```

Example error handling:

```text
InvalidUrlException -> 400 Bad Request
CustomAliasAlreadyExistsException -> 409 Conflict
ShortCodeNotFoundException -> 404 Not Found
ShortCodeExpiredException -> 410 Gone
```

ASCII:

```text
Request
  |
  v
Validation
  |
  +-- valid ----> service continues
  |
  +-- invalid --> exception
                  |
                  v
             Error Handler
                  |
                  v
             JSON response
```

Mental separation:

```text
Validation finds the problem.
Error handler explains the problem.
```

---

## 5. Validation Layers Mental Model

Validation should happen in layers.

```text
Layer 1: DTO validation
Layer 2: business validation
Layer 3: database validation
Layer 4: security validation
```

ASCII:

```text
HTTP JSON
   |
   v
+-------------------------------+
| Layer 1: DTO validation       |
| @NotBlank, @Size, @Future     |
+-------------------------------+
   |
   v
+-------------------------------+
| Layer 2: Business validation  |
| URL scheme, alias rules       |
| reserved words                |
+-------------------------------+
   |
   v
+-------------------------------+
| Layer 3: DB constraints       |
| NOT NULL, UNIQUE, CHECK       |
+-------------------------------+
   |
   v
+-------------------------------+
| Layer 4: Security validation  |
| block private IPs later       |
| malware/phishing later        |
+-------------------------------+
```

Why not only one layer?

Because each layer protects a different boundary.

DTO validation:

```text
cheap shape checks
```

Business validation:

```text
domain-specific rules
```

Database constraints:

```text
final invariant protection
```

Security validation:

```text
abuse prevention
```

Example:

```text
@NotBlank catches empty string.
Business validation rejects javascript:alert(1).
Database UNIQUE rejects duplicate alias under race.
Security validation later blocks internal IP URLs.
```

Rule:

```text
Never rely only on frontend validation.
Never rely only on service validation for database invariants.
```

---

## 6. Error Categories

Common MiniURLShortener errors:

```text
Client input errors:
    INVALID_URL
    INVALID_ALIAS
    INVALID_SHORT_CODE
    VALIDATION_FAILED

Business conflict:
    ALIAS_ALREADY_EXISTS

Redirect lifecycle:
    SHORT_CODE_NOT_FOUND
    SHORT_CODE_EXPIRED
    SHORT_CODE_BLOCKED

Generation:
    SHORT_CODE_GENERATION_FAILED

Infrastructure:
    DATABASE_ERROR
    INTERNAL_ERROR
```

Error category table:

```text
+-------------------------------+----------------------+-------------+
| Situation                     | Error Code           | HTTP Status |
+-------------------------------+----------------------+-------------+
| blank longUrl                 | VALIDATION_FAILED    | 400         |
| invalid URL scheme            | INVALID_URL          | 400         |
| invalid alias characters      | INVALID_ALIAS        | 400         |
| custom alias already exists   | ALIAS_ALREADY_EXISTS | 409         |
| short code not found          | SHORT_CODE_NOT_FOUND | 404         |
| short code expired            | SHORT_CODE_EXPIRED   | 410         |
| short code blocked            | SHORT_CODE_BLOCKED   | 403         |
| generated retries exhausted   | GENERATION_FAILED    | 500         |
| unexpected exception          | INTERNAL_ERROR       | 500         |
+-------------------------------+----------------------+-------------+
```

A stable error code is useful because clients can program against it.

Human messages may change.
Error codes should be stable.

---

## 7. HTTP Status Code Mapping

Use HTTP status codes intentionally.

### 400 Bad Request

Use when client input is invalid.

```text
invalid longUrl
invalid alias
invalid shortCode format
expiresAt in past
malformed JSON
```

### 404 Not Found

Use when requested short code does not exist.

```text
GET /unknown123
```

Also use for deleted links if you do not want to reveal previous existence.

### 403 Forbidden

Use when short code exists but is blocked.

```text
status = BLOCKED
```

### 409 Conflict

Use when request conflicts with existing resource.

```text
customAlias already exists
```

### 410 Gone

Use when short code existed but is expired.

```text
expiresAt < now
```

### 429 Too Many Requests

Later for rate limiting.

### 500 Internal Server Error

Use for unexpected server bugs.

Do not use 500 for known domain errors.

ASCII decision:

```text
Is it client input problem?
    yes -> 400

Is it duplicate custom alias?
    yes -> 409

Is redirect code missing?
    yes -> 404

Is redirect blocked?
    yes -> 403

Is redirect expired?
    yes -> 410

Unexpected server failure?
    yes -> 500
```

---

## 8. API Error Response Contract

Create one standard error response.

File:

```text
src/main/java/com/miniurl/shortener/common/error/ApiErrorResponse.java
```

Code:

```java
package com.miniurl.shortener.common.error;

import java.time.Instant;
import java.util.List;

public class ApiErrorResponse {

    private Instant timestamp;
    private int status;
    private String error;
    private String code;
    private String message;
    private String path;
    private List<FieldErrorDetail> fieldErrors;

    public ApiErrorResponse(
            Instant timestamp,
            int status,
            String error,
            String code,
            String message,
            String path,
            List<FieldErrorDetail> fieldErrors
    ) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.code = code;
        this.message = message;
        this.path = path;
        this.fieldErrors = fieldErrors;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public List<FieldErrorDetail> getFieldErrors() {
        return fieldErrors;
    }
}
```

Field error DTO:

```java
package com.miniurl.shortener.common.error;

public class FieldErrorDetail {

    private String field;
    private String message;

    public FieldErrorDetail(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}
```

Example single error:

```json
{
  "timestamp": "2026-06-21T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "INVALID_URL",
  "message": "longUrl must start with http or https",
  "path": "/api/v1/urls",
  "fieldErrors": []
}
```

Example validation error:

```json
{
  "timestamp": "2026-06-21T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "VALIDATION_FAILED",
  "message": "Request validation failed",
  "path": "/api/v1/urls",
  "fieldErrors": [
    {
      "field": "longUrl",
      "message": "longUrl is required"
    }
  ]
}
```

---

## 9. Request DTO Validation

Create API request:

```java
package com.miniurl.shortener.url.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class CreateShortUrlRequest {

    @NotBlank(message = "longUrl is required")
    @Size(max = 2048, message = "longUrl must not exceed 2048 characters")
    private String longUrl;

    @Size(min = 4, max = 32, message = "customAlias must be between 4 and 32 characters")
    private String customAlias;

    @Future(message = "expiresAt must be in the future")
    private Instant expiresAt;

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
```

Controller must use:

```java
@Valid @RequestBody CreateShortUrlRequest request
```

Without `@Valid`, annotations do not trigger.

Common mistake:

```java
public CreateShortUrlResponse create(@RequestBody CreateShortUrlRequest request)
```

This accepts invalid fields unless manually validated.

Correct:

```java
public CreateShortUrlResponse create(@Valid @RequestBody CreateShortUrlRequest request)
```

DTO validation is good for:

```text
required fields
length limits
future timestamp
basic shape
```

DTO validation is not enough for:

```text
URL scheme
reserved aliases
private IP blocking
database uniqueness
```

---

## 10. Business Validation

Business validation lives in service/helper classes.

Example:

```java
package com.miniurl.shortener.url.service;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class UrlValidatorService {

    private static final Pattern ALIAS_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_-]{4,32}$");

    private static final Set<String> RESERVED_ALIASES = Set.of(
            "api",
            "admin",
            "actuator",
            "health",
            "metrics",
            "login",
            "logout"
    );

    public void validateLongUrl(String longUrl) {
        if (longUrl == null || longUrl.isBlank()) {
            throw new InvalidUrlException("longUrl is required");
        }

        URI uri;
        try {
            uri = URI.create(longUrl.trim());
        } catch (IllegalArgumentException ex) {
            throw new InvalidUrlException("longUrl must be a valid URL");
        }

        String scheme = uri.getScheme();

        if (scheme == null ||
                !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
            throw new InvalidUrlException("longUrl must start with http or https");
        }

        if (uri.getHost() == null || uri.getHost().isBlank()) {
            throw new InvalidUrlException("longUrl must contain a valid host");
        }
    }

    public void validateCustomAlias(String alias) {
        if (!ALIAS_PATTERN.matcher(alias).matches()) {
            throw new InvalidAliasException(
                    "customAlias may contain only letters, numbers, '-' and '_' and must be 4-32 chars"
            );
        }

        if (RESERVED_ALIASES.contains(alias.toLowerCase())) {
            throw new InvalidAliasException("customAlias is reserved");
        }
    }
}
```

Why not only regex on DTO?

Because URL validation and reserved alias rules are business behavior.

They may evolve.

Examples:

```text
Today:
    block api, admin, health

Later:
    block brand names
    block phishing words
    block internal domains
```

Business validation belongs in service-level code.

---

## 11. Database Constraint Validation

Database constraints protect final invariants.

From schema:

```sql
CONSTRAINT uk_short_urls_short_code UNIQUE (short_code),
CONSTRAINT chk_short_urls_status
    CHECK (status IN ('ACTIVE', 'BLOCKED', 'DELETED')),
CONSTRAINT chk_short_urls_short_code_length
    CHECK (char_length(short_code) BETWEEN 4 AND 32)
```

Why DB validation still matters:

```text
Application may have bugs.
Multiple app pods may race.
Manual DB scripts may insert data.
Future services may write to same table.
```

Most important example:

```text
UNIQUE(short_code)
```

Race:

```text
Pod A checks alias available.
Pod B checks alias available.
Pod A inserts.
Pod B inserts.
```

Without DB unique:

```text
corruption
```

With DB unique:

```text
one succeeds
one fails safely
```

ASCII:

```text
Application validation
        |
        v
looks good
        |
        v
Database constraint
        |
        +-- passes -> commit
        |
        +-- fails  -> controlled error
```

Rule:

```text
Application validation improves UX.
Database constraints protect truth.
```

---

## 12. Domain Exception Design

Create a base domain exception.

```java
package com.miniurl.shortener.common.error;

import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    protected ApiException(String code, HttpStatus status, String message) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
```

Specific exceptions:

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.common.error.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidUrlException extends ApiException {

    public InvalidUrlException(String message) {
        super("INVALID_URL", HttpStatus.BAD_REQUEST, message);
    }
}
```

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.common.error.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidAliasException extends ApiException {

    public InvalidAliasException(String message) {
        super("INVALID_ALIAS", HttpStatus.BAD_REQUEST, message);
    }
}
```

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.common.error.ApiException;
import org.springframework.http.HttpStatus;

public class CustomAliasAlreadyExistsException extends ApiException {

    public CustomAliasAlreadyExistsException(String alias) {
        super("ALIAS_ALREADY_EXISTS", HttpStatus.CONFLICT,
                "customAlias already exists: " + alias);
    }
}
```

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.common.error.ApiException;
import org.springframework.http.HttpStatus;

public class ShortCodeNotFoundException extends ApiException {

    public ShortCodeNotFoundException(String shortCode) {
        super("SHORT_CODE_NOT_FOUND", HttpStatus.NOT_FOUND,
                "shortCode not found: " + shortCode);
    }
}
```

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.common.error.ApiException;
import org.springframework.http.HttpStatus;

public class ShortCodeExpiredException extends ApiException {

    public ShortCodeExpiredException(String shortCode) {
        super("SHORT_CODE_EXPIRED", HttpStatus.GONE,
                "shortCode expired: " + shortCode);
    }
}
```

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.common.error.ApiException;
import org.springframework.http.HttpStatus;

public class ShortCodeBlockedException extends ApiException {

    public ShortCodeBlockedException(String shortCode) {
        super("SHORT_CODE_BLOCKED", HttpStatus.FORBIDDEN,
                "shortCode blocked: " + shortCode);
    }
}
```

Why use `ApiException`?

```text
1. Centralizes status and error code.
2. Makes global handler simple.
3. Keeps domain errors explicit.
4. Avoids scattered @ResponseStatus everywhere.
```

---

## 13. Global Exception Handler

Create:

```text
src/main/java/com/miniurl/shortener/common/error/GlobalExceptionHandler.java
```

Code:

```java
package com.miniurl.shortener.common.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(
            ApiException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = ex.getStatus();

        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getCode(),
                ex.getMessage(),
                request.getRequestURI(),
                List.of()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<FieldErrorDetail> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorDetail(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();

        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "VALIDATION_FAILED",
                "Request validation failed",
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
            Exception ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "INTERNAL_ERROR",
                "Unexpected server error",
                request.getRequestURI(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

Why catch generic `Exception`?

```text
To avoid leaking stack traces to clients.
```

But also:

```text
Log it internally.
```

In real production, generic handler should log:

```text
error message
stack trace
correlation ID
path
method
```

Never send stack trace to client.

---

## 14. Validation Error Formatting

Validation errors may include multiple fields.

Bad response:

```text
Validation failed
```

Better:

```json
{
  "code": "VALIDATION_FAILED",
  "message": "Request validation failed",
  "fieldErrors": [
    {
      "field": "longUrl",
      "message": "longUrl is required"
    },
    {
      "field": "expiresAt",
      "message": "expiresAt must be in the future"
    }
  ]
}
```

Why field errors matter:

```text
Frontend can highlight exact field.
API client can fix exact issue.
Tests can assert exact behavior.
```

ASCII:

```text
Invalid Request
     |
     v
Bean Validation
     |
     v
Field errors list
     |
     v
GlobalExceptionHandler
     |
     v
Consistent JSON response
```

For MiniURLShortener, common field errors:

```text
longUrl blank
longUrl too long
customAlias too short
customAlias too long
expiresAt in past
```

Business errors are usually not field-error lists.

Example:

```text
INVALID_URL
ALIAS_ALREADY_EXISTS
```

---

## 15. Create API Error Flow

Create API can fail in multiple places.

```text
POST /api/v1/urls
```

ASCII flow:

```text
Request
  |
  v
DTO validation
  |
  +-- fails --> 400 VALIDATION_FAILED
  |
  v
Business validation
  |
  +-- invalid URL   --> 400 INVALID_URL
  +-- invalid alias --> 400 INVALID_ALIAS
  |
  v
DB insert
  |
  +-- custom alias duplicate --> 409 ALIAS_ALREADY_EXISTS
  +-- generated collision    --> retry
  +-- retry exhausted        --> 500 GENERATION_FAILED
  |
  v
201 Created
```

Create error table:

```text
+-----------------------------+-------------------------+--------+
| Problem                     | Error Code              | Status |
+-----------------------------+-------------------------+--------+
| blank longUrl               | VALIDATION_FAILED       | 400    |
| ftp URL                     | INVALID_URL             | 400    |
| alias has spaces            | INVALID_ALIAS           | 400    |
| alias is reserved           | INVALID_ALIAS           | 400    |
| alias already exists        | ALIAS_ALREADY_EXISTS    | 409    |
| generated retries exhausted | GENERATION_FAILED       | 500    |
+-----------------------------+-------------------------+--------+
```

Key distinction:

```text
Generated collision is internal retry.
Custom alias collision is client conflict.
```

---

## 16. Redirect API Error Flow

Redirect API can fail based on short code state.

```text
GET /{shortCode}
```

ASCII flow:

```text
GET /code
  |
  v
shortCode shape validation
  |
  +-- invalid --> 400 INVALID_SHORT_CODE
  |
  v
DB lookup
  |
  +-- not found --> 404 SHORT_CODE_NOT_FOUND
  |
  v
status check
  |
  +-- BLOCKED --> 403 SHORT_CODE_BLOCKED
  +-- DELETED --> 404 SHORT_CODE_NOT_FOUND
  |
  v
expiry check
  |
  +-- expired --> 410 SHORT_CODE_EXPIRED
  |
  v
302 Location
```

Redirect error table:

```text
+------------------------+------------------------+--------+
| Problem                | Error Code             | Status |
+------------------------+------------------------+--------+
| invalid format         | INVALID_SHORT_CODE     | 400    |
| no DB row              | SHORT_CODE_NOT_FOUND   | 404    |
| status BLOCKED         | SHORT_CODE_BLOCKED     | 403    |
| status DELETED         | SHORT_CODE_NOT_FOUND   | 404    |
| expiresAt in past      | SHORT_CODE_EXPIRED     | 410    |
+------------------------+------------------------+--------+
```

Why not return 500?

Because these are expected business outcomes.

500 is for unexpected server failure.

---

## 17. Security And Abuse Validation

A URL shortener can be abused.

Validation should protect against obvious abuse early.

For v1:

```text
1. Accept only http/https.
2. Reject javascript:, file:, ftp:.
3. Reject blank host.
4. Restrict custom alias characters.
5. Block reserved aliases.
6. Limit longUrl length.
```

Later production security:

```text
1. Block localhost/internal IP targets.
2. Block cloud metadata IPs.
3. Add rate limiting.
4. Add phishing/malware detection.
5. Add admin blocklist.
6. Add abuse monitoring.
```

Danger examples:

```text
javascript:alert(1)
file:///etc/passwd
http://localhost:8080/admin
http://127.0.0.1:5432
http://169.254.169.254/latest/meta-data
```

Important nuance:

```text
Redirect-only systems are safer than systems that fetch the target URL server-side.
```

But if later you add URL preview or title extraction:

```text
Server fetches target URL.
SSRF risk becomes serious.
```

Security mental model:

```text
Validation is not only correctness.
Validation is also abuse reduction.
```

---

## 18. Logging And Correlation ID Mindset

Error response is for clients.

Logs are for engineers.

Good error log should answer:

```text
What failed?
Which request?
Which shortCode?
Which error code?
Which path?
How long did it take?
What correlation ID?
```

Do not log:

```text
stack trace to client
full long URLs with secrets
raw passwords/tokens
excessive personal data
```

Example log fields:

```text
correlationId
method
path
errorCode
httpStatus
shortCode
latencyMs
exceptionClass
```

ASCII:

```text
Client sees:
    clean JSON error

Engineer sees:
    structured log with stack trace and correlationId
```

Future correlation ID flow:

```text
Request
  |
  v
CorrelationIdFilter
  |
  v
MDC contains correlationId
  |
  v
Logs include correlationId
  |
  v
Response header X-Correlation-Id
```

This will be deepened in observability phase.

For now, remember:

```text
Never confuse client error response with internal debug log.
```

---

## 19. Step-by-Step Dry Runs

### Dry Run 1: Blank long URL

Request:

```json
{
  "longUrl": ""
}
```

Flow:

```text
1. Controller receives request.
2. @Valid triggers Bean Validation.
3. @NotBlank fails.
4. MethodArgumentNotValidException is thrown.
5. GlobalExceptionHandler catches it.
6. Handler builds ApiErrorResponse.
7. Client receives 400 VALIDATION_FAILED.
```

Response:

```json
{
  "status": 400,
  "error": "Bad Request",
  "code": "VALIDATION_FAILED",
  "message": "Request validation failed",
  "fieldErrors": [
    {
      "field": "longUrl",
      "message": "longUrl is required"
    }
  ]
}
```

---

### Dry Run 2: Invalid URL scheme

Request:

```json
{
  "longUrl": "ftp://example.com/file"
}
```

Flow:

```text
1. DTO validation passes because longUrl is not blank.
2. Service validates URL.
3. Scheme is ftp.
4. Service throws InvalidUrlException.
5. GlobalExceptionHandler catches ApiException.
6. Response status is 400.
7. Error code is INVALID_URL.
```

---

### Dry Run 3: Reserved custom alias

Request:

```json
{
  "longUrl": "https://example.com",
  "customAlias": "admin"
}
```

Flow:

```text
1. DTO validation passes.
2. Business validator checks alias.
3. alias lower-case is admin.
4. admin is reserved.
5. InvalidAliasException thrown.
6. Response 400 INVALID_ALIAS.
```

---

### Dry Run 4: Duplicate custom alias

Existing:

```text
short_code = mohamed
```

Request:

```json
{
  "longUrl": "https://another.com",
  "customAlias": "mohamed"
}
```

Flow:

```text
1. DTO validation passes.
2. Business validation passes.
3. Service attempts DB insert.
4. DB UNIQUE(short_code) fails.
5. Service maps duplicate to CustomAliasAlreadyExistsException.
6. Handler returns 409 ALIAS_ALREADY_EXISTS.
```

---

### Dry Run 5: Unknown redirect code

Request:

```http
GET /wrong99
```

Flow:

```text
1. Controller extracts wrong99.
2. Service validates shape.
3. Repository returns empty.
4. Service throws ShortCodeNotFoundException.
5. Handler returns 404 SHORT_CODE_NOT_FOUND.
```

---

### Dry Run 6: Expired redirect code

Row:

```text
short_code = sale
expires_at = 2026-01-01T00:00:00Z
```

Current time:

```text
2026-06-21T10:00:00Z
```

Flow:

```text
1. Row exists.
2. Status is ACTIVE.
3. expiresAt is before now.
4. Service throws ShortCodeExpiredException.
5. Handler returns 410 SHORT_CODE_EXPIRED.
```

---

## 20. Internal Execution Walkthrough

Create API validation path:

```text
1. HTTP request enters Tomcat.
2. DispatcherServlet routes to controller.
3. Jackson deserializes JSON into DTO.
4. Bean Validation runs because @Valid is present.
5. If DTO invalid, MethodArgumentNotValidException happens before service.
6. If DTO valid, service runs business validation.
7. Business validation may throw ApiException.
8. Repository may trigger DataIntegrityViolationException.
9. Service maps known DB conflict to domain exception.
10. GlobalExceptionHandler converts exception to ApiErrorResponse.
```

ASCII:

```text
+--------+      +-------------+      +------------+      +---------+
| Client | ---> | Controller  | ---> | Service    | ---> | DB      |
+--------+      +-------------+      +------------+      +---------+
                     |                  |                 |
                     | @Valid           | business rules  | constraints
                     v                  v                 v
              validation error     domain error      DB error
                     |                  |                 |
                     +------------------+-----------------+
                                        |
                                        v
                              GlobalExceptionHandler
                                        |
                                        v
                              ApiErrorResponse JSON
```

Important:

```text
Not all errors happen in the same layer.
A clean handler gives them one external shape.
```

---

## 21. Testing Strategy

Test error behavior like happy behavior.

### DTO validation tests

Use MockMvc:

```text
blank longUrl -> 400 VALIDATION_FAILED
past expiresAt -> 400 VALIDATION_FAILED
customAlias too short -> 400 VALIDATION_FAILED
```

### Business validation tests

```text
ftp URL -> 400 INVALID_URL
javascript URL -> 400 INVALID_URL
reserved alias -> 400 INVALID_ALIAS
alias with space -> 400 INVALID_ALIAS
```

### Conflict tests

```text
duplicate custom alias -> 409 ALIAS_ALREADY_EXISTS
```

### Redirect error tests

```text
missing code -> 404 SHORT_CODE_NOT_FOUND
blocked code -> 403 SHORT_CODE_BLOCKED
expired code -> 410 SHORT_CODE_EXPIRED
```

### Response shape tests

Always assert:

```text
status
error
code
message
path
fieldErrors where relevant
```

Example MockMvc assertion:

```java
mockMvc.perform(post("/api/v1/urls")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"longUrl\":\"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
        .andExpect(jsonPath("$.fieldErrors[0].field").value("longUrl"));
```

Testing rule:

```text
Do not only assert status code.
Assert error code too.
```

---

## 22. Production Failure Stories

### Failure Story 1: Stack trace leaked to client

An unexpected exception occurs.

API returns:

```text
org.postgresql.util.PSQLException...
full stack trace...
table names...
SQL...
```

Root cause:

```text
No global exception handler and debug error exposure.
```

Fix:

```text
Generic Exception handler returns INTERNAL_ERROR.
Stack trace goes to logs only.
```

Lesson:

```text
Clients need clean errors, engineers need logs.
```

---

### Failure Story 2: All errors return 500

Invalid URL:

```text
javascript:alert(1)
```

Response:

```http
500 Internal Server Error
```

Root cause:

```text
Domain validation exceptions not mapped to 400.
```

Fix:

```text
Domain exceptions carry HTTP status.
Global handler maps them.
```

Lesson:

```text
Known business failures are not server failures.
```

---

### Failure Story 3: Duplicate alias returns 500

DB throws unique constraint error.

API returns 500.

Root cause:

```text
Service did not translate DB constraint violation into business conflict.
```

Fix:

```text
Catch duplicate for custom alias and throw ALIAS_ALREADY_EXISTS.
```

Lesson:

```text
Database errors often need domain translation.
```

---

### Failure Story 4: Inconsistent error shape breaks frontend

One endpoint returns:

```json
{"message":"bad url"}
```

Another returns:

```json
{"error":"Invalid input"}
```

Another returns HTML.

Frontend breaks.

Root cause:

```text
No standard error response contract.
```

Fix:

```text
ApiErrorResponse for all errors.
```

Lesson:

```text
Consistency is a feature.
```

---

### Failure Story 5: Sensitive URL logged

User shortens:

```text
https://example.com/reset?token=secret
```

Application logs full URL.

Logs are exported to third-party monitoring.

Root cause:

```text
Careless logging.
```

Fix:

```text
Log domain, length, hash, not full sensitive URL.
```

Lesson:

```text
Logs are data exposure surfaces.
```

---

## 23. Debugging Mindset

When an error occurs, ask:

```text
What HTTP status?
What error code?
Which layer threw it?
Is it expected business failure or unexpected bug?
Did validation catch it early?
Did DB constraint catch it?
Is response safe for client?
Is stack trace available in logs?
Is correlation ID present?
```

Debug map:

```text
400 VALIDATION_FAILED:
    DTO annotation failed

400 INVALID_URL / INVALID_ALIAS:
    business validation failed

409 ALIAS_ALREADY_EXISTS:
    DB unique conflict translated

404 SHORT_CODE_NOT_FOUND:
    redirect lookup empty or deleted

410 SHORT_CODE_EXPIRED:
    expiry check failed

403 SHORT_CODE_BLOCKED:
    status blocked

500 INTERNAL_ERROR:
    unexpected bug or unmapped infrastructure problem
```

Useful SQL:

```sql
SELECT id, short_code, long_url, status, expires_at
FROM short_urls
WHERE short_code = 'abc123';
```

Useful logs:

```text
errorCode
exceptionClass
path
method
correlationId
shortCode
latencyMs
```

Golden rule:

```text
A good error should tell clients what they can fix and tell engineers where to investigate.
```

---

## 24. Common Mistakes

### Mistake 1: No standard error response

Wrong:

```text
Every controller returns its own error format.
```

Correct:

```text
GlobalExceptionHandler returns ApiErrorResponse.
```

### Mistake 2: Returning 500 for validation

Wrong:

```text
Invalid user input -> 500
```

Correct:

```text
Invalid user input -> 400
```

### Mistake 3: Trusting frontend validation

Wrong:

```text
Frontend already validates, backend can trust it.
```

Correct:

```text
Backend validates always.
```

### Mistake 4: No DB constraints

Wrong:

```text
Service checks uniqueness, DB does not.
```

Correct:

```text
Service validates for UX, DB enforces invariant.
```

### Mistake 5: Exposing raw exception messages

Wrong:

```text
Return ex.getMessage() for every exception.
```

Correct:

```text
Return safe message for generic errors.
Log details internally.
```

### Mistake 6: Mixing business logic in exception handler

Wrong:

```text
Handler decides whether URL is expired.
```

Correct:

```text
Service decides.
Handler maps exception to response.
```

### Mistake 7: Not testing error responses

Wrong:

```text
Only happy path tests.
```

Correct:

```text
Test validation, conflict, not found, expired, blocked.
```

---

## 25. Interview-Ready Explanation

If interviewer asks:

```text
How do you handle validation and errors in your URL shortener API?
```

Strong answer:

```text
I use layered validation. Request DTO annotations handle simple shape checks like
required longUrl, max length, and future expiresAt. Business validation in the
service checks URL scheme, host, custom alias pattern, and reserved aliases.
Database constraints enforce final invariants such as UNIQUE(short_code), because
application checks alone are unsafe under concurrency. For errors, I use domain
exceptions with stable error codes and HTTP status mappings, and a global
@RestControllerAdvice converts them into one consistent ApiErrorResponse shape.
Invalid input returns 400, duplicate custom alias returns 409, missing short code
returns 404, blocked returns 403, expired returns 410, and unexpected bugs return a
safe 500 without leaking stack traces. Internally, logs should include correlation
ID and exception details, but client responses should stay clean and safe.
```

Why this is strong:

```text
1. Explains layered validation.
2. Mentions DB constraints.
3. Uses correct HTTP status codes.
4. Uses stable error codes.
5. Separates client response from internal logs.
6. Shows concurrency awareness.
7. Shows security mindset.
8. Shows production debugging mindset.
```

Senior one-liner:

```text
Validation protects the system from bad input, and global error handling turns every expected failure into a stable API contract.
```

---

## 26. Senior Engineer Checklist

Before moving to Production Coding phase, confirm:

```text
[ ] ApiErrorResponse exists
[ ] FieldErrorDetail exists
[ ] ApiException base class exists
[ ] Domain exceptions have stable codes
[ ] GlobalExceptionHandler handles ApiException
[ ] GlobalExceptionHandler handles MethodArgumentNotValidException
[ ] Generic Exception handler hides internals
[ ] Create DTO uses @Valid annotations
[ ] Controller uses @Valid
[ ] Business URL validation exists
[ ] Business alias validation exists
[ ] Reserved aliases blocked
[ ] DB UNIQUE(short_code) exists
[ ] Duplicate custom alias maps to 409
[ ] Missing short code maps to 404
[ ] Expired short code maps to 410
[ ] Blocked short code maps to 403
[ ] Validation errors include fieldErrors
[ ] Tests assert error code and status
[ ] Full long URLs are not logged carelessly
```

If these are checked, your API failure model is production-shaped.

---

## 27. One-Page Cheat Sheet

```text
Core mental model:
Validation decides what enters.
Error handling decides how failures leave.

Validation layers:
1. DTO annotations
2. Business validation
3. Database constraints
4. Security validation later

Standard error response:
timestamp
status
error
code
message
path
fieldErrors

Main error codes:
VALIDATION_FAILED
INVALID_URL
INVALID_ALIAS
INVALID_SHORT_CODE
ALIAS_ALREADY_EXISTS
SHORT_CODE_NOT_FOUND
SHORT_CODE_EXPIRED
SHORT_CODE_BLOCKED
GENERATION_FAILED
INTERNAL_ERROR

HTTP mapping:
400 invalid input
403 blocked
404 not found/deleted
409 alias conflict
410 expired
500 unexpected

Create API:
blank URL -> 400
bad URL -> 400
bad alias -> 400
duplicate alias -> 409
generated collision -> retry

Redirect API:
bad code -> 400
missing -> 404
blocked -> 403
expired -> 410
active -> 302

Production rules:
Do not expose stack traces.
Do not log sensitive full URLs.
Do not rely only on frontend validation.
Do not rely only on application uniqueness checks.
```

---

## 28. One Picture To Remember

```text
              ERROR HANDLING & VALIDATION MENTAL MODEL

                         "Bad input stops here"

External Client
      |
      v
+-----------------------------+
| DTO Validation              |
| @NotBlank @Size @Future     |
+-----------------------------+
      |
      +-- fail --> 400 VALIDATION_FAILED
      |
      v
+-----------------------------+
| Business Validation         |
| URL scheme, alias rules     |
+-----------------------------+
      |
      +-- fail --> 400 INVALID_URL / INVALID_ALIAS
      |
      v
+-----------------------------+
| Service Logic               |
| create / redirect decision  |
+-----------------------------+
      |
      v
+-----------------------------+
| Database Constraints        |
| UNIQUE, NOT NULL, CHECK     |
+-----------------------------+
      |
      +-- alias duplicate --> 409 ALIAS_ALREADY_EXISTS
      |
      v
+-----------------------------+
| Domain Errors               |
| not found / blocked / expired
+-----------------------------+
      |
      +-- 404 / 403 / 410
      |
      v
+-----------------------------+
| GlobalExceptionHandler      |
| one response shape          |
+-----------------------------+
      |
      v
Client receives predictable JSON


FINAL MEMORY:

Validation protects the inside.
Error handling explains failure outside.
Logs help engineers investigate.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Validation prevents bad input from entering business logic.
2. Error handling converts known failures into predictable API responses.
3. DTO validation, business validation, and DB constraints protect different layers.
4. Domain exceptions should map to stable error codes and correct HTTP statuses.
5. Stack traces belong in logs, not client responses.
```

After this chapter, Phase 1 Core Backend is complete:

```text
001 Problem Requirements
002 URL Shortener Mental Model
003 Spring Boot Project Setup
004 Postgres Schema Design
005 Create Short URL API
006 Redirect API
007 Base62 Encoding
008 ID Generation Strategies
009 Error Handling & Validation
```

Next phase:

```text
PHASE 2 — Production Coding
010_Clean_Architecture.md
