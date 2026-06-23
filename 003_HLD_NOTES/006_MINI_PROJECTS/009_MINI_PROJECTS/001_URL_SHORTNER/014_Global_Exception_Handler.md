# 014_Global_Exception_Handler.md
# MiniURLShortener — Global Exception Handler

> Core mental model: **A global exception handler is the API failure translator. It catches failures from controller, service, validation, and infrastructure layers, then converts them into one predictable, safe, client-friendly error contract.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Where Global Exception Handler Sits](#4-where-global-exception-handler-sits)
- [5. Without Global Exception Handler](#5-without-global-exception-handler)
- [6. With Global Exception Handler](#6-with-global-exception-handler)
- [7. Controller vs Service vs Handler Responsibility](#7-controller-vs-service-vs-handler-responsibility)
- [8. Standard Error Contract](#8-standard-error-contract)
- [9. Error Code Design](#9-error-code-design)
- [10. Domain Exception Base Class](#10-domain-exception-base-class)
- [11. MiniURL Domain Exceptions](#11-miniurl-domain-exceptions)
- [12. GlobalExceptionHandler Implementation](#12-globalexceptionhandler-implementation)
- [13. Handling DTO Validation Errors](#13-handling-dto-validation-errors)
- [14. Handling Business Exceptions](#14-handling-business-exceptions)
- [15. Handling Database Constraint Exceptions](#15-handling-database-constraint-exceptions)
- [16. Handling Malformed JSON](#16-handling-malformed-json)
- [17. Handling Path Variable Errors](#17-handling-path-variable-errors)
- [18. Handling Unexpected 500 Errors](#18-handling-unexpected-500-errors)
- [19. Create API Exception Flow](#19-create-api-exception-flow)
- [20. Redirect API Exception Flow](#20-redirect-api-exception-flow)
- [21. Step-by-Step Dry Runs](#21-step-by-step-dry-runs)
- [22. Internal Spring Execution Walkthrough](#22-internal-spring-execution-walkthrough)
- [23. Logging Boundary Mindset](#23-logging-boundary-mindset)
- [24. Correlation ID Mindset](#24-correlation-id-mindset)
- [25. Testing Strategy](#25-testing-strategy)
- [26. Production Failure Stories](#26-production-failure-stories)
- [27. Debugging Mindset](#27-debugging-mindset)
- [28. Common Mistakes](#28-common-mistakes)
- [29. Interview-Ready Explanation](#29-interview-ready-explanation)
- [30. Senior Engineer Checklist](#30-senior-engineer-checklist)
- [31. One-Page Cheat Sheet](#31-one-page-cheat-sheet)
- [32. One Picture To Remember](#32-one-picture-to-remember)

---

## 1. Why This Exists

MiniURLShortener now has multiple API flows:

```text
POST /api/v1/urls
GET  /{shortCode}
```

These APIs can fail in many ways.

Examples:

```text
User sends blank longUrl.
User sends invalid URL.
User sends duplicate customAlias.
User requests missing shortCode.
User requests expired shortCode.
User requests blocked shortCode.
Client sends broken JSON.
Database unique constraint fails.
Database is down.
A bug throws NullPointerException.
```

Without a global exception handler, these failures may produce:

```text
random JSON
HTML error pages
stack traces
inconsistent status codes
duplicate try/catch blocks
500 for every problem
sensitive internal details leaked to clients
```

That is not production quality.

A production API should fail predictably.

Bad response:

```json
{
  "message": "could not execute statement; SQL [n/a]"
}
```

Better response:

```json
{
  "timestamp": "2026-06-23T10:00:00Z",
  "status": 409,
  "error": "Conflict",
  "code": "ALIAS_ALREADY_EXISTS",
  "message": "customAlias already exists",
  "path": "/api/v1/urls",
  "fieldErrors": []
}
```

The global handler gives every failure one external shape.

Mental rule:

```text
Business code throws meaningful exceptions.
Global handler translates them into HTTP responses.
```

---

## 2. The One Core Mental Model

The global exception handler is the:

```text
FAILURE TRANSLATOR
```

It does not usually decide business rules.

It translates exceptions into API responses.

ASCII:

```text
                 Failure happens anywhere

+-------------+      +------------+      +-------------+
| Controller  | ---> | Service    | ---> | Repository  |
+-------------+      +------------+      +-------------+
      |                    |                    |
      | validation error   | domain error       | DB error
      v                    v                    v
      +--------------------+--------------------+
                           |
                           v
              +-----------------------------+
              | Global Exception Handler    |
              | exception -> HTTP response  |
              +-----------------------------+
                           |
                           v
              +-----------------------------+
              | Consistent JSON Error       |
              +-----------------------------+
```

One-line memory:

```text
Exceptions are internal signals; API errors are external contracts.
```

For MiniURLShortener:

```text
InvalidUrlException               -> 400 INVALID_URL
InvalidAliasException             -> 400 INVALID_ALIAS
CustomAliasAlreadyExistsException -> 409 ALIAS_ALREADY_EXISTS
ShortCodeNotFoundException        -> 404 SHORT_CODE_NOT_FOUND
ShortCodeExpiredException         -> 410 SHORT_CODE_EXPIRED
ShortCodeBlockedException         -> 403 SHORT_CODE_BLOCKED
MethodArgumentNotValidException   -> 400 VALIDATION_FAILED
Exception                         -> 500 INTERNAL_ERROR
```

The handler is the single place where the outside world learns about failure.

---

## 3. Problem Statement

Build a global exception handling model for MiniURLShortener.

It must support:

```text
1. One consistent error response shape.
2. DTO validation errors with field-level details.
3. Domain/business exceptions with stable error codes.
4. Duplicate alias conflicts.
5. Redirect not found, expired, and blocked cases.
6. Malformed JSON request bodies.
7. Invalid request path or variable format.
8. Database constraint failures.
9. Unexpected server errors.
10. Safe client responses and useful internal logs.
```

It should avoid:

```text
try/catch in every controller
stack trace leakage
raw SQL error leakage
wrong HTTP status code
inconsistent field names
business decisions inside handler
hiding every error as 500
```

Out of scope:

```text
full distributed tracing
centralized log aggregation
alerting rules
rate limiting
security exception handling deep dive
```

This chapter focuses on the clean Spring Boot exception handling boundary.

---

## 4. Where Global Exception Handler Sits

Spring MVC request flow:

```text
Client
  |
  v
Tomcat
  |
  v
DispatcherServlet
  |
  v
Controller
  |
  v
Service
  |
  v
Repository
  |
  v
Database
```

Exception flow:

```text
Any layer throws exception
        |
        v
Spring MVC exception resolver
        |
        v
@RestControllerAdvice
        |
        v
ResponseEntity<ApiErrorResponse>
```

ASCII:

```text
HTTP Request
    |
    v
+---------------------+
| DispatcherServlet   |
+---------------------+
    |
    v
+---------------------+
| UrlController       |
+---------------------+
    |
    v
+---------------------+
| UrlService          |
+---------------------+
    |
    v
+---------------------+
| UrlRepository       |
+---------------------+
    |
    v
+---------------------+
| PostgreSQL          |
+---------------------+

If exception is thrown:
    |
    v
+-----------------------------+
| GlobalExceptionHandler      |
| @RestControllerAdvice       |
+-----------------------------+
    |
    v
JSON error response
```

Important:

```text
The handler is outside the normal happy path.
It is activated when Spring sees an exception from the MVC flow.
```

---

## 5. Without Global Exception Handler

Controller-level try/catch usually becomes messy.

Bad controller:

```java
@PostMapping("/api/v1/urls")
public ResponseEntity<?> create(@RequestBody CreateShortUrlRequest request) {
    try {
        CreateShortUrlResponse response = urlService.create(request);
        return ResponseEntity.status(201).body(response);
    } catch (InvalidUrlException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    } catch (CustomAliasAlreadyExistsException ex) {
        return ResponseEntity.status(409).body(Map.of("message", ex.getMessage()));
    } catch (Exception ex) {
        return ResponseEntity.status(500).body(Map.of("error", "server failed"));
    }
}
```

Problems:

```text
1. Controller becomes noisy.
2. Error response shapes differ.
3. Every endpoint repeats mapping.
4. Developers forget cases.
5. Stack traces may leak.
6. Service logic becomes mixed with HTTP details.
7. Tests become harder.
```

ASCII mess:

```text
Controller A:
  try/catch INVALID_URL -> {"error": "..."}
Controller B:
  try/catch NOT_FOUND   -> {"message": "..."}
Controller C:
  no catch              -> default 500 HTML

Result:
  inconsistent API
```

Rule:

```text
Controllers should not become exception-mapping factories.
```

---

## 6. With Global Exception Handler

Good controller:

```java
@PostMapping("/api/v1/urls")
public ResponseEntity<CreateShortUrlResponse> create(
        @Valid @RequestBody CreateShortUrlRequest request
) {
    CreateShortUrlResponse response = urlService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

Good service:

```java
public CreateShortUrlResponse create(CreateShortUrlRequest request) {
    validator.validateLongUrl(request.getLongUrl());

    if (aliasAlreadyExists(request.getCustomAlias())) {
        throw new CustomAliasAlreadyExistsException(request.getCustomAlias());
    }

    // create and save
}
```

Good handler:

```java
@ExceptionHandler(ApiException.class)
public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
    // convert exception into standard JSON
}
```

Clean flow:

```text
Controller:
    receive request
    call service
    return success

Service:
    enforce business rules
    throw meaningful exception

Handler:
    translate exception to HTTP response
```

ASCII:

```text
+-------------+       +-------------+       +----------------------+
| Controller  | ----> | Service     | ----> | Repository           |
| thin        |       | business    |       | persistence          |
+-------------+       +-------------+       +----------------------+
                            |
                            | throws domain exception
                            v
                  +--------------------------+
                  | GlobalExceptionHandler   |
                  | maps to API error JSON   |
                  +--------------------------+
```

This is production-shaped separation.

---

## 7. Controller vs Service vs Handler Responsibility

Do not mix responsibilities.

```text
Controller:
    HTTP input/output boundary

Service:
    business rules and transaction boundary

Repository:
    persistence boundary

Global Exception Handler:
    failure-to-response boundary
```

Table:

```text
+------------------+--------------------------------------------+
| Layer            | Responsibility                             |
+------------------+--------------------------------------------+
| Controller       | HTTP mapping, DTO, status for success      |
| Service          | Business decisions, throws domain errors   |
| Repository       | DB access, persistence errors              |
| Handler          | Exception -> ApiErrorResponse              |
+------------------+--------------------------------------------+
```

Example:

```text
Should alias "admin" be blocked?
    Service/validator decides.

Should InvalidAliasException return 400?
    Handler maps.

Should response contain timestamp/code/path?
    Handler builds.

Should controller know all error codes?
    No.
```

ASCII memory:

```text
Controller asks.
Service decides.
Repository stores.
Handler explains failure.
```

---

## 8. Standard Error Contract

Use one standard response for all API errors.

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

    public Instant getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public List<FieldErrorDetail> getFieldErrors() { return fieldErrors; }
}
```

Field error:

```java
package com.miniurl.shortener.common.error;

public class FieldErrorDetail {

    private String field;
    private String message;

    public FieldErrorDetail(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() { return field; }
    public String getMessage() { return message; }
}
```

Example response:

```json
{
  "timestamp": "2026-06-23T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "INVALID_URL",
  "message": "longUrl must start with http or https",
  "path": "/api/v1/urls",
  "fieldErrors": []
}
```

Why these fields?

```text
timestamp:
    when it happened

status:
    HTTP status as number

error:
    HTTP reason phrase

code:
    stable machine-readable business code

message:
    human-readable safe explanation

path:
    which endpoint failed

fieldErrors:
    exact DTO validation failures
```

---

## 9. Error Code Design

HTTP status is not enough.

Example:

```text
400 Bad Request
```

This could mean:

```text
blank longUrl
invalid URL scheme
bad alias pattern
past expiresAt
malformed JSON
invalid shortCode format
```

So add stable error codes.

MiniURL error codes:

```text
VALIDATION_FAILED
INVALID_URL
INVALID_ALIAS
INVALID_SHORT_CODE
ALIAS_ALREADY_EXISTS
SHORT_CODE_NOT_FOUND
SHORT_CODE_EXPIRED
SHORT_CODE_BLOCKED
MALFORMED_JSON
DATABASE_ERROR
INTERNAL_ERROR
```

Mapping table:

```text
+-------------------------------+----------------------+-------------+
| Situation                     | Code                 | HTTP Status |
+-------------------------------+----------------------+-------------+
| Bean validation failed        | VALIDATION_FAILED    | 400         |
| URL scheme not http/https     | INVALID_URL          | 400         |
| alias has invalid chars       | INVALID_ALIAS        | 400         |
| shortCode format invalid      | INVALID_SHORT_CODE   | 400         |
| duplicate custom alias        | ALIAS_ALREADY_EXISTS | 409         |
| shortCode missing             | SHORT_CODE_NOT_FOUND | 404         |
| shortCode expired             | SHORT_CODE_EXPIRED   | 410         |
| shortCode blocked             | SHORT_CODE_BLOCKED   | 403         |
| broken JSON request           | MALFORMED_JSON       | 400         |
| unexpected bug                | INTERNAL_ERROR       | 500         |
+-------------------------------+----------------------+-------------+
```

Rule:

```text
HTTP status tells category.
Error code tells exact reason.
```

---

## 10. Domain Exception Base Class

Create one base exception for known API/domain failures.

File:

```text
src/main/java/com/miniurl/shortener/common/error/ApiException.java
```

Code:

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

    public String getCode() { return code; }
    public HttpStatus getStatus() { return status; }
}
```

Why RuntimeException?

```text
Spring transaction rollback works naturally for unchecked exceptions.
Service methods stay clean.
Domain failures can bubble to the global handler.
```

Why not checked exceptions everywhere?

```text
They pollute service signatures.
They force noisy try/catch.
They do not add much value for expected API domain failures.
```

Mental model:

```text
ApiException = known failure with known API mapping.
Exception     = unknown failure, return safe 500.
```

ASCII:

```text
Known business failure:
    ApiException(code, status, message)
        |
        v
    predictable response

Unknown bug:
    Exception
        |
        v
    safe INTERNAL_ERROR
```

---

## 11. MiniURL Domain Exceptions

Invalid URL:

```java
package com.miniurl.shortener.url.exception;

import com.miniurl.shortener.common.error.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidUrlException extends ApiException {

    public InvalidUrlException(String message) {
        super("INVALID_URL", HttpStatus.BAD_REQUEST, message);
    }
}
```

Invalid alias:

```java
package com.miniurl.shortener.url.exception;

import com.miniurl.shortener.common.error.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidAliasException extends ApiException {

    public InvalidAliasException(String message) {
        super("INVALID_ALIAS", HttpStatus.BAD_REQUEST, message);
    }
}
```

Duplicate alias:

```java
package com.miniurl.shortener.url.exception;

import com.miniurl.shortener.common.error.ApiException;
import org.springframework.http.HttpStatus;

public class CustomAliasAlreadyExistsException extends ApiException {

    public CustomAliasAlreadyExistsException(String alias) {
        super("ALIAS_ALREADY_EXISTS", HttpStatus.CONFLICT,
                "customAlias already exists: " + alias);
    }
}
```

Short code not found:

```java
package com.miniurl.shortener.url.exception;

import com.miniurl.shortener.common.error.ApiException;
import org.springframework.http.HttpStatus;

public class ShortCodeNotFoundException extends ApiException {

    public ShortCodeNotFoundException(String shortCode) {
        super("SHORT_CODE_NOT_FOUND", HttpStatus.NOT_FOUND,
                "shortCode not found: " + shortCode);
    }
}
```

Expired:

```java
package com.miniurl.shortener.url.exception;

import com.miniurl.shortener.common.error.ApiException;
import org.springframework.http.HttpStatus;

public class ShortCodeExpiredException extends ApiException {

    public ShortCodeExpiredException(String shortCode) {
        super("SHORT_CODE_EXPIRED", HttpStatus.GONE,
                "shortCode expired: " + shortCode);
    }
}
```

Blocked:

```java
package com.miniurl.shortener.url.exception;

import com.miniurl.shortener.common.error.ApiException;
import org.springframework.http.HttpStatus;

public class ShortCodeBlockedException extends ApiException {

    public ShortCodeBlockedException(String shortCode) {
        super("SHORT_CODE_BLOCKED", HttpStatus.FORBIDDEN,
                "shortCode blocked: " + shortCode);
    }
}
```

Invalid short code:

```java
package com.miniurl.shortener.url.exception;

import com.miniurl.shortener.common.error.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidShortCodeException extends ApiException {

    public InvalidShortCodeException(String shortCode) {
        super("INVALID_SHORT_CODE", HttpStatus.BAD_REQUEST,
                "shortCode format is invalid: " + shortCode);
    }
}
```

Design rule:

```text
Every expected business failure gets its own explicit exception.
```

---

## 12. GlobalExceptionHandler Implementation

File:

```text
src/main/java/com/miniurl/shortener/common/error/GlobalExceptionHandler.java
```

Implementation:

```java
package com.miniurl.shortener.common.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "MALFORMED_JSON",
                "Request body is missing or malformed",
                request.getRequestURI(),
                List.of()
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                "DATABASE_CONSTRAINT_VIOLATION",
                "Request conflicts with existing data",
                request.getRequestURI(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
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

Important order:

```text
Specific handlers first.
Generic Exception handler last.
```

Why?

```text
Spring chooses the most specific matching handler, but humans should still structure code clearly.
The generic handler is a safety net, not the main design.
```

---

## 13. Handling DTO Validation Errors

DTO:

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

    public String getLongUrl() { return longUrl; }
    public void setLongUrl(String longUrl) { this.longUrl = longUrl; }

    public String getCustomAlias() { return customAlias; }
    public void setCustomAlias(String customAlias) { this.customAlias = customAlias; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
```

Controller:

```java
@PostMapping("/api/v1/urls")
public ResponseEntity<CreateShortUrlResponse> create(
        @Valid @RequestBody CreateShortUrlRequest request
) {
    CreateShortUrlResponse response = urlService.createShortUrl(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

Invalid request:

```json
{
  "longUrl": "",
  "customAlias": "a"
}
```

Spring throws:

```text
MethodArgumentNotValidException
```

Handler converts to:

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
    },
    {
      "field": "customAlias",
      "message": "customAlias must be between 4 and 32 characters"
    }
  ]
}
```

ASCII:

```text
JSON body
   |
   v
Jackson creates DTO
   |
   v
@Valid checks annotations
   |
   +-- fail --> MethodArgumentNotValidException
                    |
                    v
             GlobalExceptionHandler
                    |
                    v
             400 VALIDATION_FAILED
```

Common mistake:

```text
Forgot @Valid.
```

Then validation annotations do nothing.

---

## 14. Handling Business Exceptions

Business exception example:

```java
public void validateLongUrl(String longUrl) {
    URI uri = URI.create(longUrl);

    String scheme = uri.getScheme();

    if (scheme == null ||
            !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
        throw new InvalidUrlException("longUrl must start with http or https");
    }
}
```

Handler path:

```text
InvalidUrlException extends ApiException.
ApiException has code + status.
GlobalExceptionHandler handles ApiException.
```

ASCII:

```text
Service detects invalid business rule
        |
        v
throw InvalidUrlException
        |
        v
handleApiException()
        |
        v
400 INVALID_URL
```

Why service throws, not controller?

```text
Business validation may be reused by:
    REST API
    batch job
    admin operation
    future message consumer

So business rules belong in service/domain layer.
```

The controller should not contain:

```java
if (!url.startsWith("http")) {
    return ResponseEntity.badRequest()...
}
```

That scatters rules.

---

## 15. Handling Database Constraint Exceptions

Database constraints protect final truth.

Example:

```sql
UNIQUE(short_code)
```

Race scenario:

```text
Request A checks alias "sale" available.
Request B checks alias "sale" available.
Request A inserts "sale".
Request B inserts "sale".
Database rejects B.
```

ASCII:

```text
Pod A                      Database                     Pod B
 |                             |                          |
 | check sale                  |                          |
 |---------------------------->|                          |
 | available                   |                          |
 |<----------------------------|                          |
 |                             |<-------------------------|
 |                             | check sale               |
 |                             |------------------------->|
 |                             | available                |
 |                             |<-------------------------|
 | insert sale                 |                          |
 |---------------------------->|                          |
 | success                     |                          |
 |<----------------------------|                          |
 |                             | insert sale              |
 |                             |<-------------------------|
 |                             | UNIQUE violation         |
```

Best design:

```text
For custom alias duplicate:
    service should translate to ALIAS_ALREADY_EXISTS.

For unknown DB integrity problem:
    global handler returns DATABASE_CONSTRAINT_VIOLATION or safe 409.
```

Service-level translation example:

```java
@Transactional
public CreateShortUrlResponse createShortUrl(CreateShortUrlRequest request) {
    try {
        ShortUrl entity = new ShortUrl();
        entity.setLongUrl(request.getLongUrl());
        entity.setShortCode(resolveShortCode(request));
        entity.setStatus("ACTIVE");

        ShortUrl saved = shortUrlRepository.saveAndFlush(entity);
        return mapper.toResponse(saved);

    } catch (DataIntegrityViolationException ex) {
        if (request.getCustomAlias() != null && !request.getCustomAlias().isBlank()) {
            throw new CustomAliasAlreadyExistsException(request.getCustomAlias());
        }

        throw ex;
    }
}
```

Why `saveAndFlush`?

```text
Because DB constraint may be checked at flush/commit time.
saveAndFlush forces SQL earlier inside the try/catch.
```

Important:

```text
The global handler can catch DataIntegrityViolationException,
but service can provide better business-specific meaning.
```

---

## 16. Handling Malformed JSON

Invalid JSON example:

```json
{
  "longUrl": "https://example.com",
```

Missing closing brace.

Spring cannot deserialize it.

Exception:

```text
HttpMessageNotReadableException
```

Response:

```json
{
  "status": 400,
  "error": "Bad Request",
  "code": "MALFORMED_JSON",
  "message": "Request body is missing or malformed",
  "path": "/api/v1/urls",
  "fieldErrors": []
}
```

ASCII:

```text
Raw HTTP body
   |
   v
Jackson parser
   |
   +-- invalid JSON --> HttpMessageNotReadableException
                           |
                           v
                    GlobalExceptionHandler
                           |
                           v
                    400 MALFORMED_JSON
```

Why not expose parser message?

Bad:

```text
Unexpected character: was expecting double-quote to start field name
```

Better:

```text
Request body is missing or malformed
```

Client gets safe clear message.

Logs can store detailed parser error if needed.

---

## 17. Handling Path Variable Errors

Redirect endpoint:

```java
@GetMapping("/{shortCode}")
public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
    RedirectTarget target = urlService.resolveRedirect(shortCode);
    return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(target.longUrl()))
            .build();
}
```

Service validates shape:

```java
public void validateShortCode(String shortCode) {
    if (shortCode == null || !shortCode.matches("^[a-zA-Z0-9_-]{4,32}$")) {
        throw new InvalidShortCodeException(shortCode);
    }
}
```

Why validate shape?

```text
GET /%%%%
GET /../../../admin
GET /a
GET /very-long-string-with-invalid-chars
```

Invalid path variable should produce:

```json
{
  "status": 400,
  "code": "INVALID_SHORT_CODE",
  "message": "shortCode format is invalid"
}
```

ASCII:

```text
GET /bad!!!
   |
   v
Controller extracts shortCode
   |
   v
Service validates shortCode
   |
   +-- invalid --> InvalidShortCodeException
                       |
                       v
                400 INVALID_SHORT_CODE
```

Do not let invalid path variables reach DB query blindly.

---

## 18. Handling Unexpected 500 Errors

Unexpected exceptions happen.

Examples:

```text
NullPointerException
IllegalStateException
bug in mapper
database connection failure
timeout
bad configuration
third-party failure
```

Generic handler:

```java
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
```

Client sees:

```json
{
  "status": 500,
  "error": "Internal Server Error",
  "code": "INTERNAL_ERROR",
  "message": "Unexpected server error"
}
```

Logs should contain:

```text
exception class
stack trace
path
method
correlationId
request id
user id if available
```

Never return:

```text
ex.toString()
stack trace
SQL statement
table name details
secret URL tokens
```

ASCII:

```text
Unknown bug
   |
   v
Generic handler
   |
   +-- client: safe 500
   |
   +-- logs: full stack trace
```

One-line rule:

```text
Clients need safety; engineers need details.
```

---

## 19. Create API Exception Flow

Create API:

```text
POST /api/v1/urls
```

Flow:

```text
Request body
   |
   v
Jackson parse
   |
   +-- malformed JSON -> 400 MALFORMED_JSON
   |
   v
DTO validation
   |
   +-- invalid field -> 400 VALIDATION_FAILED
   |
   v
Service business validation
   |
   +-- invalid URL   -> 400 INVALID_URL
   +-- invalid alias -> 400 INVALID_ALIAS
   |
   v
Repository save
   |
   +-- duplicate custom alias -> 409 ALIAS_ALREADY_EXISTS
   +-- DB constraint unknown  -> 409 DATABASE_CONSTRAINT_VIOLATION
   |
   v
Success 201 Created
```

ASCII:

```text
POST /api/v1/urls
       |
       v
+-------------------------+
| JSON parse              |
+-------------------------+
       |
       +-- fail --> 400 MALFORMED_JSON
       |
       v
+-------------------------+
| @Valid DTO validation   |
+-------------------------+
       |
       +-- fail --> 400 VALIDATION_FAILED
       |
       v
+-------------------------+
| Business validation     |
+-------------------------+
       |
       +-- fail --> 400 INVALID_URL / INVALID_ALIAS
       |
       v
+-------------------------+
| DB insert               |
+-------------------------+
       |
       +-- fail --> 409 ALIAS_ALREADY_EXISTS
       |
       v
201 Created
```

The handler ensures all failures leave as same error shape.

---

## 20. Redirect API Exception Flow

Redirect API:

```text
GET /{shortCode}
```

Flow:

```text
Path variable
   |
   v
shortCode shape validation
   |
   +-- invalid -> 400 INVALID_SHORT_CODE
   |
   v
Repository lookup
   |
   +-- missing -> 404 SHORT_CODE_NOT_FOUND
   |
   v
Status check
   |
   +-- BLOCKED -> 403 SHORT_CODE_BLOCKED
   +-- DELETED -> 404 SHORT_CODE_NOT_FOUND
   |
   v
Expiry check
   |
   +-- expired -> 410 SHORT_CODE_EXPIRED
   |
   v
302 Found Location: longUrl
```

ASCII:

```text
GET /abc123
   |
   v
+----------------------+
| Validate shortCode   |
+----------------------+
   |
   +-- bad --> 400 INVALID_SHORT_CODE
   |
   v
+----------------------+
| Find by shortCode    |
+----------------------+
   |
   +-- empty --> 404 SHORT_CODE_NOT_FOUND
   |
   v
+----------------------+
| Check status         |
+----------------------+
   |
   +-- blocked --> 403 SHORT_CODE_BLOCKED
   |
   v
+----------------------+
| Check expiry         |
+----------------------+
   |
   +-- expired --> 410 SHORT_CODE_EXPIRED
   |
   v
302 Redirect
```

Important:

```text
Not found, blocked, and expired are not 500 errors.
They are expected business outcomes.
```

---

## 21. Step-by-Step Dry Runs

### Dry Run 1: Blank longUrl

Request:

```json
{
  "longUrl": ""
}
```

Execution:

```text
1. Client calls POST /api/v1/urls.
2. Jackson converts JSON to CreateShortUrlRequest.
3. @Valid checks longUrl.
4. @NotBlank fails.
5. Spring throws MethodArgumentNotValidException.
6. GlobalExceptionHandler.handleValidationException runs.
7. It extracts fieldErrors from BindingResult.
8. It builds ApiErrorResponse.
9. Client receives 400 VALIDATION_FAILED.
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

### Dry Run 2: Invalid URL Scheme

Request:

```json
{
  "longUrl": "ftp://example.com/file"
}
```

Execution:

```text
1. DTO validation passes.
2. Controller calls UrlService.
3. UrlService calls UrlValidatorService.
4. Validator sees scheme = ftp.
5. Validator throws InvalidUrlException.
6. Handler catches ApiException.
7. Handler reads status = 400, code = INVALID_URL.
8. Client receives standard JSON response.
```

Response:

```json
{
  "status": 400,
  "error": "Bad Request",
  "code": "INVALID_URL",
  "message": "longUrl must start with http or https"
}
```

---

### Dry Run 3: Duplicate Custom Alias

Existing DB row:

```text
short_code = "sale"
```

Request:

```json
{
  "longUrl": "https://example.com/campaign",
  "customAlias": "sale"
}
```

Execution:

```text
1. DTO validation passes.
2. Business alias validation passes.
3. Service tries to insert row.
4. DB UNIQUE(short_code) rejects duplicate.
5. Repository throws DataIntegrityViolationException.
6. Service catches and translates to CustomAliasAlreadyExistsException.
7. Handler catches ApiException.
8. Client receives 409 ALIAS_ALREADY_EXISTS.
```

Response:

```json
{
  "status": 409,
  "error": "Conflict",
  "code": "ALIAS_ALREADY_EXISTS",
  "message": "customAlias already exists: sale"
}
```

---

### Dry Run 4: Malformed JSON

Request body:

```json
{
  "longUrl": "https://example.com"
```

Execution:

```text
1. Client sends broken JSON.
2. Jackson fails before controller method runs.
3. Spring throws HttpMessageNotReadableException.
4. Handler catches it.
5. Handler returns 400 MALFORMED_JSON.
```

Important:

```text
Controller is not entered.
Service is not called.
Repository is not touched.
```

---

### Dry Run 5: Unknown Short Code

Request:

```http
GET /unknown99
```

Execution:

```text
1. Controller receives shortCode = unknown99.
2. Service validates shortCode format.
3. Repository findByShortCode returns Optional.empty().
4. Service throws ShortCodeNotFoundException.
5. Handler maps to 404 SHORT_CODE_NOT_FOUND.
```

---

### Dry Run 6: Expired Short Code

DB row:

```text
short_code = offer1
status = ACTIVE
expires_at = 2026-01-01T00:00:00Z
```

Current time:

```text
2026-06-23T10:00:00Z
```

Execution:

```text
1. Repository finds row.
2. Service checks status ACTIVE.
3. Service checks expiresAt.
4. expiresAt is before now.
5. Service throws ShortCodeExpiredException.
6. Handler maps to 410 SHORT_CODE_EXPIRED.
```

---

### Dry Run 7: Unexpected NullPointerException

Bug:

```java
String domain = entity.getLongUrl().toLowerCase();
```

But longUrl is unexpectedly null.

Execution:

```text
1. NullPointerException occurs.
2. No domain-specific handler matches.
3. Generic Exception handler catches it.
4. Client receives safe 500 INTERNAL_ERROR.
5. Logs should contain stack trace.
```

Client response:

```json
{
  "status": 500,
  "error": "Internal Server Error",
  "code": "INTERNAL_ERROR",
  "message": "Unexpected server error"
}
```

---

## 22. Internal Spring Execution Walkthrough

Spring Boot error handling flow:

```text
1. HTTP request reaches embedded Tomcat.
2. Tomcat passes request to DispatcherServlet.
3. DispatcherServlet finds matching controller method.
4. Jackson deserializes request body.
5. Bean Validation may validate DTO.
6. Controller method executes.
7. Service/repository may throw exception.
8. Exception bubbles back to DispatcherServlet.
9. Spring HandlerExceptionResolver looks for matching @ExceptionHandler.
10. @RestControllerAdvice method is selected.
11. Handler returns ResponseEntity<ApiErrorResponse>.
12. Jackson serializes ApiErrorResponse to JSON.
13. Client receives HTTP error response.
```

ASCII:

```text
Client
  |
  v
Tomcat
  |
  v
DispatcherServlet
  |
  v
HandlerMapping finds controller
  |
  v
Argument resolution
  |
  +-- JSON parse error -> handler
  |
  v
Bean validation
  |
  +-- validation error -> handler
  |
  v
Controller method
  |
  v
Service
  |
  +-- domain error -> handler
  |
  v
Repository
  |
  +-- DB error -> handler
  |
  v
Success response
```

Important:

```text
Some exceptions happen before controller method runs.
Some happen inside service.
Some happen during DB flush/commit.
The global handler covers all MVC-visible failures.
```

---

## 23. Logging Boundary Mindset

The error response is not the log.

Client response should be:

```text
safe
stable
minimal
actionable
```

Internal logs should be:

```text
detailed
searchable
correlated
debuggable
```

Bad client response:

```json
{
  "message": "org.postgresql.util.PSQLException: ERROR duplicate key value violates unique constraint uk_short_urls_short_code"
}
```

Good client response:

```json
{
  "code": "ALIAS_ALREADY_EXISTS",
  "message": "customAlias already exists"
}
```

Good internal log fields:

```text
level=ERROR
correlationId=abc-123
method=POST
path=/api/v1/urls
status=409
errorCode=ALIAS_ALREADY_EXISTS
exceptionClass=CustomAliasAlreadyExistsException
latencyMs=32
```

For 500:

```text
Log full stack trace.
Return safe message.
```

For 400 user input errors:

```text
Usually log at INFO or WARN, not ERROR.
```

Why?

```text
Invalid user input is not necessarily server failure.
```

---

## 24. Correlation ID Mindset

In production, one user request may produce many logs.

Example:

```text
Controller log
Service log
Repository log
Error handler log
SQL timing log
```

Without correlation ID:

```text
Hard to know which logs belong to the same request.
```

With correlation ID:

```text
All logs contain x-correlation-id = req-123.
```

ASCII:

```text
Request X-Correlation-Id: req-123
        |
        v
+-----------------------+
| CorrelationIdFilter   |
| puts req-123 in MDC   |
+-----------------------+
        |
        v
Controller log: req-123
Service log:    req-123
Handler log:    req-123
        |
        v
Response header X-Correlation-Id: req-123
```

Simple future filter:

```java
package com.miniurl.shortener.common.web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter implements Filter {

    private static final String HEADER = "X-Correlation-Id";
    private static final String MDC_KEY = "correlationId";

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String correlationId = request.getHeader(HEADER);

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        try {
            MDC.put(MDC_KEY, correlationId);
            response.setHeader(HEADER, correlationId);
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}
```

This chapter does not require full observability implementation, but the mindset matters.

---

## 25. Testing Strategy

Test exception handling with MockMvc.

### Test 1: Validation error

```java
@Test
void createShortUrl_whenLongUrlBlank_returnsValidationFailed() throws Exception {
    mockMvc.perform(post("/api/v1/urls")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "longUrl": ""
                }
                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("longUrl"));
}
```

### Test 2: Invalid URL

```java
@Test
void createShortUrl_whenUrlSchemeInvalid_returnsInvalidUrl() throws Exception {
    mockMvc.perform(post("/api/v1/urls")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "longUrl": "ftp://example.com/file"
                }
                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_URL"));
}
```

### Test 3: Duplicate alias

```java
@Test
void createShortUrl_whenAliasAlreadyExists_returnsConflict() throws Exception {
    mockMvc.perform(post("/api/v1/urls")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "longUrl": "https://example.com",
                  "customAlias": "sale"
                }
                """))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("ALIAS_ALREADY_EXISTS"));
}
```

### Test 4: Malformed JSON

```java
@Test
void createShortUrl_whenJsonMalformed_returnsMalformedJson() throws Exception {
    mockMvc.perform(post("/api/v1/urls")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "longUrl": "https://example.com"
                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("MALFORMED_JSON"));
}
```

### Test 5: Short code not found

```java
@Test
void redirect_whenShortCodeMissing_returnsNotFound() throws Exception {
    mockMvc.perform(get("/missing99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("SHORT_CODE_NOT_FOUND"));
}
```

Testing rule:

```text
Assert both HTTP status and error code.
```

Why?

```text
Status checks category.
Code checks exact API contract.
```

---

## 26. Production Failure Stories

### Failure Story 1: Stack Trace Exposed

Client receives:

```text
java.lang.NullPointerException
    at com.company.UrlService.createShortUrl...
```

Root cause:

```text
No generic exception handler.
Default error response exposed too much.
```

Fix:

```text
Add @ExceptionHandler(Exception.class).
Return INTERNAL_ERROR.
Log stack trace internally.
```

Lesson:

```text
Stack traces belong to logs, not clients.
```

---

### Failure Story 2: Duplicate Alias Returned 500

User tries customAlias:

```text
sale
```

Another user already owns it.

API returns:

```text
500 Internal Server Error
```

Root cause:

```text
Database unique violation was not translated.
```

Fix:

```text
Service catches DataIntegrityViolationException for custom alias.
Throws CustomAliasAlreadyExistsException.
Handler returns 409.
```

Lesson:

```text
Database constraints need domain translation when possible.
```

---

### Failure Story 3: Frontend Broke Because Error Shape Changed

Endpoint A returns:

```json
{"error": "Invalid URL"}
```

Endpoint B returns:

```json
{"message": "Not found"}
```

Endpoint C returns:

```json
{"code": "EXPIRED", "details": "..."}
```

Root cause:

```text
Each controller handled errors differently.
```

Fix:

```text
One ApiErrorResponse contract.
One GlobalExceptionHandler.
```

Lesson:

```text
Consistency is part of API design.
```

---

### Failure Story 4: Handler Became Business Layer

Global handler contains:

```java
if (ex.getMessage().contains("expired")) {
    return 410;
}
```

Root cause:

```text
Business decisions leaked into exception handler.
```

Fix:

```text
Service throws ShortCodeExpiredException.
Handler only maps exception status/code.
```

Lesson:

```text
Handler translates. Service decides.
```

---

### Failure Story 5: Generic Handler Swallowed Important Error Meaning

All exceptions returned:

```json
{
  "code": "INTERNAL_ERROR"
}
```

Even invalid URL.

Root cause:

```text
Only generic Exception handler existed.
```

Fix:

```text
Add specific handlers for ApiException, validation, malformed JSON, DB constraints.
```

Lesson:

```text
Generic catch-all is safety net, not the main design.
```

---

## 27. Debugging Mindset

When an API error happens, ask:

```text
1. What HTTP status did client receive?
2. What error code did client receive?
3. Which layer threw the exception?
4. Was it expected business failure or unexpected bug?
5. Did handler select the right @ExceptionHandler?
6. Did service throw specific domain exception?
7. Did DB throw DataIntegrityViolationException?
8. Was exception thrown before controller method?
9. Is stack trace in logs?
10. Is response safe?
```

Debug map:

```text
400 VALIDATION_FAILED:
    @Valid failed on DTO field.

400 MALFORMED_JSON:
    Jackson could not parse body.

400 INVALID_URL:
    service/business validation failed.

409 ALIAS_ALREADY_EXISTS:
    duplicate custom alias.

404 SHORT_CODE_NOT_FOUND:
    repository returned empty or deleted status hidden.

403 SHORT_CODE_BLOCKED:
    row exists but status is BLOCKED.

410 SHORT_CODE_EXPIRED:
    row exists but expiresAt is past.

500 INTERNAL_ERROR:
    unknown bug or infrastructure failure.
```

Useful local debugging:

```text
Check controller has @Valid.
Check handler has @RestControllerAdvice.
Check exception extends ApiException.
Check service does not catch and swallow exception.
Check saveAndFlush if expecting DB exception inside try/catch.
Check logs for stack trace and correlation ID.
```

ASCII:

```text
Wrong status?
   |
   v
Did correct exception get thrown?
   |
   +-- no --> fix service/validator
   |
   +-- yes
        |
        v
Did handler catch correct type?
   |
   +-- no --> add/fix @ExceptionHandler
   |
   +-- yes
        |
        v
Check response contract mapping
```

---

## 28. Common Mistakes

### Mistake 1: Try/catch in every controller

Wrong:

```text
Every controller manually maps exceptions.
```

Correct:

```text
Use @RestControllerAdvice globally.
```

---

### Mistake 2: Handler contains business rules

Wrong:

```text
Handler checks if shortCode is expired.
```

Correct:

```text
Service checks expiry and throws ShortCodeExpiredException.
```

---

### Mistake 3: Returning raw exception message for 500

Wrong:

```java
message = ex.getMessage()
```

Correct:

```text
Client message = Unexpected server error.
Detailed exception = logs.
```

---

### Mistake 4: No stable error codes

Wrong:

```text
Only return HTTP status.
```

Correct:

```text
Return HTTP status + machine-readable code.
```

---

### Mistake 5: Not handling validation errors separately

Wrong:

```text
MethodArgumentNotValidException becomes generic 500.
```

Correct:

```text
Map to 400 VALIDATION_FAILED with fieldErrors.
```

---

### Mistake 6: Not handling malformed JSON

Wrong:

```text
Broken JSON returns default Spring error body.
```

Correct:

```text
Map HttpMessageNotReadableException to 400 MALFORMED_JSON.
```

---

### Mistake 7: Logging every 400 as ERROR

Wrong:

```text
Invalid user input floods error logs.
```

Correct:

```text
4xx often INFO/WARN.
5xx ERROR.
```

---

### Mistake 8: Catching exception and returning null

Wrong:

```java
try {
    return service.create(request);
} catch (Exception ex) {
    return null;
}
```

Correct:

```text
Let exception bubble to handler or translate to domain exception.
```

---

### Mistake 9: No tests for error contract

Wrong:

```text
Only test happy path.
```

Correct:

```text
Test status, code, message, path, fieldErrors.
```

---

## 29. Interview-Ready Explanation

If interviewer asks:

```text
How do you design global exception handling in a Spring Boot API?
```

Strong answer:

```text
I keep controllers thin and avoid try/catch blocks in every endpoint. Business
logic throws explicit domain exceptions, usually extending a base ApiException
that carries a stable error code and an HTTP status. A @RestControllerAdvice
class has @ExceptionHandler methods for ApiException, validation failures,
malformed JSON, database constraint violations, and a final generic Exception
fallback. All handlers return the same ApiErrorResponse shape with timestamp,
status, error, code, message, path, and fieldErrors when applicable. DTO validation
errors return 400 VALIDATION_FAILED, invalid URL or alias returns 400, duplicate
custom alias returns 409, missing short code returns 404, blocked returns 403,
expired returns 410, and unexpected errors return a safe 500 without exposing stack
traces. Detailed stack traces go to logs with correlation IDs, not to clients.
```

Why this is strong:

```text
1. Separates controller/service/handler roles.
2. Mentions @RestControllerAdvice.
3. Uses domain exceptions.
4. Uses stable machine-readable error codes.
5. Handles validation separately.
6. Handles malformed JSON.
7. Handles DB constraints.
8. Protects stack traces.
9. Shows observability mindset.
10. Maps MiniURL business cases correctly.
```

Senior one-liner:

```text
A global exception handler turns internal failures into a stable external API contract while keeping business decisions in the service layer.
```

---

## 30. Senior Engineer Checklist

Before moving to the next chapter, confirm:

```text
[ ] ApiErrorResponse exists.
[ ] FieldErrorDetail exists.
[ ] ApiException base class exists.
[ ] Domain exceptions extend ApiException.
[ ] Error codes are stable and uppercase.
[ ] @RestControllerAdvice class exists.
[ ] ApiException handler returns status/code/message.
[ ] MethodArgumentNotValidException returns fieldErrors.
[ ] HttpMessageNotReadableException returns MALFORMED_JSON.
[ ] DataIntegrityViolationException is handled safely.
[ ] Generic Exception handler returns safe INTERNAL_ERROR.
[ ] Controller uses @Valid.
[ ] Controller has no repetitive try/catch.
[ ] Service throws domain exceptions.
[ ] Handler does not contain business rules.
[ ] Stack traces are not sent to clients.
[ ] 4xx and 5xx logging levels are thought through.
[ ] Tests assert both status and error code.
[ ] Duplicate alias returns 409.
[ ] Missing short code returns 404.
[ ] Expired short code returns 410.
[ ] Blocked short code returns 403.
```

If this checklist passes, your API failure boundary is production-shaped.

---

## 31. One-Page Cheat Sheet

```text
Core mental model:
Global exception handler = failure translator.

Internal:
    exceptions

External:
    stable JSON error contract

Controller:
    no noisy try/catch
    uses @Valid
    returns success response

Service:
    business rules
    throws domain exceptions

Repository:
    DB access
    may throw DB exceptions

Handler:
    maps exceptions to HTTP responses

Main annotations:
    @RestControllerAdvice
    @ExceptionHandler

Main exception mappings:
    ApiException                    -> status from exception
    MethodArgumentNotValidException -> 400 VALIDATION_FAILED
    HttpMessageNotReadableException -> 400 MALFORMED_JSON
    DataIntegrityViolationException -> 409 DATABASE_CONSTRAINT_VIOLATION
    Exception                       -> 500 INTERNAL_ERROR

MiniURL mapping:
    INVALID_URL              -> 400
    INVALID_ALIAS            -> 400
    INVALID_SHORT_CODE       -> 400
    ALIAS_ALREADY_EXISTS     -> 409
    SHORT_CODE_NOT_FOUND     -> 404
    SHORT_CODE_BLOCKED       -> 403
    SHORT_CODE_EXPIRED       -> 410
    INTERNAL_ERROR           -> 500

Golden rules:
    Handler translates.
    Service decides.
    Logs contain details.
    Client response stays safe.
    Error code is part of API contract.
```

---

## 32. One Picture To Remember

```text
                    GLOBAL EXCEPTION HANDLER MENTAL MODEL

                              "Failure Translator"

Client Request
     |
     v
+--------------------------+
| Controller               |
| HTTP boundary            |
+--------------------------+
     |
     | @Valid failure
     |--------------------------------------+
     |                                      |
     v                                      |
+--------------------------+                |
| Service                  |                |
| business decisions       |                |
+--------------------------+                |
     |                                      |
     | domain exception                     |
     |----------------------------------+   |
     |                                  |   |
     v                                  |   |
+--------------------------+            |   |
| Repository / Database    |            |   |
| persistence boundary     |            |   |
+--------------------------+            |   |
     |                                  |   |
     | DB exception                     |   |
     |------------------------------+   |   |
                                    |   |   |
                                    v   v   v
                         +----------------------------+
                         | GlobalExceptionHandler     |
                         | @RestControllerAdvice      |
                         |                            |
                         | Exception -> API Contract  |
                         +----------------------------+
                                    |
                                    v
                         +----------------------------+
                         | ApiErrorResponse JSON      |
                         | timestamp                  |
                         | status                     |
                         | error                      |
                         | code                       |
                         | message                    |
                         | path                       |
                         | fieldErrors                |
                         +----------------------------+
                                    |
                                    v
                              Client understands


FINAL MEMORY:

Exceptions are internal signals.
ApiErrorResponse is the external contract.
Service decides what failed.
Global handler explains it safely.
Logs keep the full debugging story.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. A global exception handler converts internal exceptions into a stable API error contract.
2. Controllers should stay thin and should not repeat try/catch mapping logic.
3. Services throw meaningful domain exceptions because services own business decisions.
4. Validation, malformed JSON, domain errors, DB conflicts, and unknown bugs need separate mappings.
5. Clients get safe predictable JSON; engineers get detailed logs and correlation IDs.
```

After this chapter, your MiniURLShortener production coding phase has a clean failure boundary:

```text
010_Clean_Architecture.md
011_Controller_Service_Repository.md
012_JPA_vs_JDBC.md
013_Transaction_Boundaries.md
014_Global_Exception_Handler.md
```

Next chapter:

```text
015_Configuration_Properties.md
```
