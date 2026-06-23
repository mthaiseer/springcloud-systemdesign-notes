# 015_Config_Properties.md
# MiniURLShortener — Config & Properties

> Core mental model: **Configuration is the runtime contract between your code and the environment. Code says what knobs exist; configuration decides their values for local, test, staging, and production.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. What Is Configuration?](#4-what-is-configuration)
- [5. Code vs Configuration](#5-code-vs-configuration)
- [6. Spring Boot Configuration Loading Mental Model](#6-spring-boot-configuration-loading-mental-model)
- [7. Configuration Sources And Precedence](#7-configuration-sources-and-precedence)
- [8. Profiles Mental Model](#8-profiles-mental-model)
- [9. MiniURLShortener Configuration Map](#9-miniurlshortener-configuration-map)
- [10. application.yml Design](#10-applicationyml-design)
- [11. Local Profile](#11-local-profile)
- [12. Test Profile](#12-test-profile)
- [13. Production Profile](#13-production-profile)
- [14. Environment Variables](#14-environment-variables)
- [15. @Value vs @ConfigurationProperties](#15-value-vs-configurationproperties)
- [16. Type-Safe URL Shortener Properties](#16-type-safe-url-shortener-properties)
- [17. Validation For Configuration](#17-validation-for-configuration)
- [18. Using Config In Service Layer](#18-using-config-in-service-layer)
- [19. Secrets And Sensitive Config](#19-secrets-and-sensitive-config)
- [20. Docker Compose Configuration](#20-docker-compose-configuration)
- [21. Kubernetes ConfigMap And Secret Mental Model](#21-kubernetes-configmap-and-secret-mental-model)
- [22. Config Flow End To End](#22-config-flow-end-to-end)
- [23. Step-by-Step Dry Runs](#23-step-by-step-dry-runs)
- [24. Internal Execution Walkthrough](#24-internal-execution-walkthrough)
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

MiniURLShortener cannot run with hardcoded values.

Bad code:

```java
private static final String BASE_URL = "http://localhost:8080";
private static final int SHORT_CODE_LENGTH = 7;
private static final int MAX_RETRY = 5;
```

This looks simple, but it fails immediately when the application moves from laptop to Docker, staging, or production.

Different environments need different values:

```text
Local:
    baseUrl = http://localhost:8080
    database = local Postgres
    logging = debug

Test:
    database = Testcontainers Postgres
    shortCodeLength = predictable value sometimes
    external integrations disabled

Production:
    baseUrl = https://sho.rt
    database = managed Postgres
    logging = info/json
    secrets from secret manager
```

If config is bad, the application may still compile but fail at runtime.

Examples:

```text
wrong DB URL
wrong Redis host
wrong public base URL
missing secret
invalid timeout
wrong active profile
wrong redirect domain
actuator health exposed publicly
```

Production mental model:

```text
Code should be deployable everywhere.
Configuration makes it behave correctly somewhere.
```

---

## 2. The One Core Mental Model

Configuration is an external runtime contract.

ASCII:

```text
                 Same Application JAR
                         |
       +-----------------+-----------------+
       |                 |                 |
       v                 v                 v
  local config       test config       prod config
       |                 |                 |
       v                 v                 v
 local behavior     test behavior     prod behavior
```

The code contains configurable knobs:

```text
server port
base URL
short code length
max generation attempts
database connection
Redis connection
cache TTL
logging level
feature toggles
security exposure
```

The environment provides values:

```text
application.yml
application-local.yml
application-prod.yml
environment variables
Docker Compose env
Kubernetes ConfigMap
Kubernetes Secret
command-line args
```

One-line memory:

```text
Code defines what can change; config defines what changes now.
```

For MiniURLShortener:

```text
The service logic should not know whether it runs on laptop, Docker, staging, or Kubernetes.
It should only read a clean typed config object.
```

---

## 3. Problem Statement

Build a production-shaped configuration model for MiniURLShortener.

It must support:

```text
1. Separate local, test, and production configuration.
2. Type-safe custom properties.
3. Validation for required config.
4. Environment variable overrides.
5. Safe handling of secrets.
6. Docker-friendly configuration.
7. Kubernetes-friendly configuration.
8. Clear debugging when config is wrong.
9. Interview-ready explanation of profiles and config precedence.
```

It should avoid:

```text
hardcoded environment values
scattered @Value annotations
secret values committed to Git
silent fallback to wrong defaults
production running with local profile
large YAML files with no structure
magic strings inside services
```

Out of scope for this chapter:

```text
1. Full Spring Cloud Config Server.
2. Vault integration implementation.
3. Dynamic refresh with /actuator/refresh.
4. Multi-region config management.
```

This chapter creates the configuration foundation needed before scaling the URL shortener.

---

## 4. What Is Configuration?

Configuration is data that controls application behavior without changing code.

Examples:

```yaml
server:
  port: 8080

mini-url:
  public-base-url: http://localhost:8080
  short-code-length: 7
  max-generation-attempts: 5
```

Code reads it:

```java
String publicBaseUrl = properties.getPublicBaseUrl();
int length = properties.getShortCodeLength();
```

Then behavior changes:

```text
short URL returned to client:
http://localhost:8080/abc1234
```

In production:

```yaml
mini-url:
  public-base-url: https://sho.rt
```

Same code returns:

```text
https://sho.rt/abc1234
```

ASCII:

```text
Config value
    |
    v
Application behavior
    |
    v
Different output without code change
```

Configuration is not business data.

Business data:

```text
long URL saved by user
short code generated by system
click count
user account
```

Configuration:

```text
how long short codes should be
where database is
which profile is active
how many retries are allowed
```

---

## 5. Code vs Configuration

A senior engineer knows what belongs in code and what belongs in config.

```text
+-------------------------------+-------------------------------+
| Belongs In Code               | Belongs In Config             |
+-------------------------------+-------------------------------+
| URL creation algorithm         | short code length             |
| validation logic               | max URL length                |
| transaction boundary           | DB URL / username             |
| exception mapping              | logging level                 |
| repository query structure     | cache TTL                     |
| DTO shape                      | public base URL               |
| domain rules                   | feature flags                 |
+-------------------------------+-------------------------------+
```

Example:

```text
Rule in code:
    Only http/https URLs are allowed.

Value in config:
    Maximum longUrl length is 2048.
```

Why?

Because the scheme rule is part of product correctness.
The length limit may change due to product or storage decisions.

Bad mental model:

```text
Put everything in config so we never change code.
```

Better mental model:

```text
Put operational and environment-specific values in config.
Keep core domain rules explicit in code.
```

---

## 6. Spring Boot Configuration Loading Mental Model

Spring Boot starts by building an Environment.

The Environment is a merged view of property sources.

ASCII:

```text
Application starts
      |
      v
+-----------------------------+
| Build Environment           |
| merge property sources      |
+-----------------------------+
      |
      v
+-----------------------------+
| Create ApplicationContext   |
| register beans              |
+-----------------------------+
      |
      v
+-----------------------------+
| Bind @ConfigurationProperties|
| validate config             |
+-----------------------------+
      |
      v
+-----------------------------+
| Beans use typed properties  |
+-----------------------------+
```

Spring Boot can read values from many places:

```text
application.yml
application-{profile}.yml
environment variables
JVM system properties
command-line arguments
```

Your service should not manually parse environment variables.

Wrong:

```java
String baseUrl = System.getenv("PUBLIC_BASE_URL");
```

Better:

```java
private final MiniUrlProperties properties;
```

Why?

Because Spring Boot already solves binding, profile handling, overriding, validation, and type conversion.

---

## 7. Configuration Sources And Precedence

When the same property exists in multiple places, one wins.

Simplified order:

```text
Command-line args          highest
Environment variables
application-prod.yml
application.yml            lowest among these
```

Example:

application.yml:

```yaml
mini-url:
  public-base-url: http://localhost:8080
```

Environment variable:

```bash
MINI_URL_PUBLIC_BASE_URL=https://sho.rt
```

Effective value:

```text
https://sho.rt
```

ASCII:

```text
application.yml
    public-base-url = localhost
           |
           v
application-prod.yml
    public-base-url = staging/prod default
           |
           v
environment variable
    MINI_URL_PUBLIC_BASE_URL = https://sho.rt
           |
           v
FINAL EFFECTIVE VALUE = https://sho.rt
```

Why precedence matters:

```text
Docker images should be immutable.
Runtime env should override values.
```

You build one image.
You run it with different configuration.

---

## 8. Profiles Mental Model

A Spring profile selects environment-specific configuration.

Common profiles:

```text
local
test
staging
prod
```

ASCII:

```text
application.yml
    common defaults
         |
         +-- application-local.yml
         |       local overrides
         |
         +-- application-test.yml
         |       test overrides
         |
         +-- application-prod.yml
                 production overrides
```

Activating profile:

```bash
SPRING_PROFILES_ACTIVE=local
```

or:

```bash
java -jar app.jar --spring.profiles.active=prod
```

Important rule:

```text
application.yml should contain safe common defaults.
application-prod.yml should contain production-specific defaults.
secrets should come from environment/secret manager, not Git.
```

Bad:

```yaml
spring:
  profiles:
    active: prod
```

inside committed `application.yml`.

Why bad?

Because profile activation is an environment decision, not code repository decision.

---

## 9. MiniURLShortener Configuration Map

MiniURLShortener needs configuration groups.

```text
+----------------------+---------------------------------------------+
| Config Group          | Examples                                    |
+----------------------+---------------------------------------------+
| Server                | port                                        |
| Datasource            | JDBC URL, username, password                |
| JPA                   | ddl-auto, show-sql                          |
| Mini URL domain config| public base URL, code length, retry count   |
| Validation limits     | max URL length, alias length                |
| Redirect behavior     | redirect status, cache headers              |
| Actuator              | health exposure                             |
| Logging               | level, pattern                              |
+----------------------+---------------------------------------------+
```

For this chapter, custom domain config:

```text
mini-url:
  public-base-url
  short-code-length
  max-generation-attempts
  default-expiry-days
  max-long-url-length
  custom-alias-min-length
  custom-alias-max-length
```

Why group under `mini-url`?

```text
1. It avoids pollution of global property namespace.
2. It makes config easier to discover.
3. It maps cleanly to one typed properties class.
4. It is easier to validate as one object.
```

---

## 10. application.yml Design

Create common configuration.

File:

```text
src/main/resources/application.yml
```

Code:

```yaml
server:
  port: 8080

spring:
  application:
    name: mini-url-shortener

  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/miniurl}
    username: ${DB_USERNAME:miniurl}
    password: ${DB_PASSWORD:miniurl}

  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        format_sql: true

mini-url:
  public-base-url: ${MINI_URL_PUBLIC_BASE_URL:http://localhost:8080}
  short-code-length: ${MINI_URL_SHORT_CODE_LENGTH:7}
  max-generation-attempts: ${MINI_URL_MAX_GENERATION_ATTEMPTS:5}
  default-expiry-days: ${MINI_URL_DEFAULT_EXPIRY_DAYS:365}
  max-long-url-length: ${MINI_URL_MAX_LONG_URL_LENGTH:2048}
  custom-alias-min-length: ${MINI_URL_CUSTOM_ALIAS_MIN_LENGTH:4}
  custom-alias-max-length: ${MINI_URL_CUSTOM_ALIAS_MAX_LENGTH:32}

management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      probes:
        enabled: true
```

Syntax:

```text
${ENV_NAME:defaultValue}
```

Meaning:

```text
Use ENV_NAME if present.
Otherwise use defaultValue.
```

Example:

```yaml
public-base-url: ${MINI_URL_PUBLIC_BASE_URL:http://localhost:8080}
```

If env exists:

```bash
MINI_URL_PUBLIC_BASE_URL=https://sho.rt
```

Final value:

```text
https://sho.rt
```

If env missing:

```text
http://localhost:8080
```

---

## 11. Local Profile

Local profile is optimized for developer laptop.

File:

```text
src/main/resources/application-local.yml
```

Code:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/miniurl
    username: miniurl
    password: miniurl

  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true

logging:
  level:
    com.miniurl.shortener: DEBUG
    org.hibernate.SQL: DEBUG

mini-url:
  public-base-url: http://localhost:8080
```

Run:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

or:

```bash
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

Local config should help development:

```text
readable logs
local database
safe test domain
no real secrets
no production endpoints
```

Local should not imitate everything about production.
It should be easy and safe.

---

## 12. Test Profile

Test profile should be deterministic.

File:

```text
src/test/resources/application-test.yml
```

Code:

```yaml
spring:
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate

mini-url:
  public-base-url: http://test.local
  short-code-length: 7
  max-generation-attempts: 3
  default-expiry-days: 30
```

Use in tests:

```java
@ActiveProfiles("test")
@SpringBootTest
class CreateShortUrlServiceTest {
}
```

Why test config matters:

```text
1. Tests should not accidentally hit local/prod DB.
2. Tests should use predictable settings.
3. Tests should not depend on developer machine env variables.
4. Tests should fail fast if required config is missing.
```

ASCII:

```text
Test starts
   |
   v
@ActiveProfiles("test")
   |
   v
application.yml + application-test.yml
   |
   v
safe test properties
```

---

## 13. Production Profile

Production config should be safe by default and override secrets from environment.

File:

```text
src/main/resources/application-prod.yml
```

Code:

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

  jpa:
    show-sql: false
    open-in-view: false
    hibernate:
      ddl-auto: validate

logging:
  level:
    root: INFO
    com.miniurl.shortener: INFO
    org.hibernate.SQL: WARN

mini-url:
  public-base-url: ${MINI_URL_PUBLIC_BASE_URL}
  short-code-length: ${MINI_URL_SHORT_CODE_LENGTH:7}
  max-generation-attempts: ${MINI_URL_MAX_GENERATION_ATTEMPTS:5}
  default-expiry-days: ${MINI_URL_DEFAULT_EXPIRY_DAYS:365}

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

Notice:

```yaml
url: ${DB_URL}
```

No default value.

Why?

Because production should fail fast when critical config is missing.

Bad production config:

```yaml
url: ${DB_URL:jdbc:postgresql://localhost:5432/miniurl}
```

This may accidentally run production app against localhost or wrong DB.

Production rule:

```text
For critical values, missing config should crash startup.
```

---

## 14. Environment Variables

Spring Boot maps environment variables to property names using relaxed binding.

Property:

```yaml
mini-url.public-base-url
```

Environment variable:

```bash
MINI_URL_PUBLIC_BASE_URL
```

Property:

```yaml
spring.datasource.url
```

Environment variable:

```bash
SPRING_DATASOURCE_URL
```

Examples:

```bash
export SPRING_PROFILES_ACTIVE=prod
export DB_URL=jdbc:postgresql://prod-db:5432/miniurl
export DB_USERNAME=miniurl_app
export DB_PASSWORD=super-secret
export MINI_URL_PUBLIC_BASE_URL=https://sho.rt
```

ASCII:

```text
ENV VARIABLE
MINI_URL_PUBLIC_BASE_URL
        |
        v
Spring relaxed binding
        |
        v
mini-url.public-base-url
        |
        v
MiniUrlProperties.publicBaseUrl
```

Why env vars are popular:

```text
1. Docker friendly.
2. Kubernetes friendly.
3. CI/CD friendly.
4. No need to rebuild image.
5. Easy to override per deployment.
```

But do not print all environment variables in logs.
They may contain secrets.

---

## 15. @Value vs @ConfigurationProperties

Spring offers two common ways to read config.

### @Value

```java
@Value("${mini-url.public-base-url}")
private String publicBaseUrl;
```

Good for one small value.

But bad when config grows:

```java
@Value("${mini-url.public-base-url}")
private String publicBaseUrl;

@Value("${mini-url.short-code-length}")
private int shortCodeLength;

@Value("${mini-url.max-generation-attempts}")
private int maxGenerationAttempts;
```

Problems:

```text
scattered config
harder to validate as a group
string keys duplicated everywhere
weak discoverability
harder unit testing
```

### @ConfigurationProperties

Better for grouped application config.

```java
@ConfigurationProperties(prefix = "mini-url")
public class MiniUrlProperties {
    private String publicBaseUrl;
    private int shortCodeLength;
    private int maxGenerationAttempts;
}
```

Advantages:

```text
type-safe
centralized
easy to validate
easy to inject
easy to test
easy to document
```

Mental model:

```text
@Value = one wire
@ConfigurationProperties = full control panel
```

For MiniURLShortener, use `@ConfigurationProperties`.

---

## 16. Type-Safe URL Shortener Properties

Create properties class.

File:

```text
src/main/java/com/miniurl/shortener/config/MiniUrlProperties.java
```

Code:

```java
package com.miniurl.shortener.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "mini-url")
public class MiniUrlProperties {

    @NotBlank(message = "mini-url.public-base-url is required")
    private String publicBaseUrl;

    @Min(value = 4, message = "short-code-length must be at least 4")
    @Max(value = 16, message = "short-code-length must not exceed 16")
    private int shortCodeLength = 7;

    @Min(value = 1, message = "max-generation-attempts must be at least 1")
    @Max(value = 20, message = "max-generation-attempts must not exceed 20")
    private int maxGenerationAttempts = 5;

    @Min(value = 1, message = "default-expiry-days must be at least 1")
    @Max(value = 3650, message = "default-expiry-days must not exceed 3650")
    private int defaultExpiryDays = 365;

    @Min(value = 100, message = "max-long-url-length must be at least 100")
    @Max(value = 8192, message = "max-long-url-length must not exceed 8192")
    private int maxLongUrlLength = 2048;

    @Min(value = 3, message = "custom-alias-min-length must be at least 3")
    private int customAliasMinLength = 4;

    @Max(value = 64, message = "custom-alias-max-length must not exceed 64")
    private int customAliasMaxLength = 32;

    public String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    public void setPublicBaseUrl(String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl;
    }

    public int getShortCodeLength() {
        return shortCodeLength;
    }

    public void setShortCodeLength(int shortCodeLength) {
        this.shortCodeLength = shortCodeLength;
    }

    public int getMaxGenerationAttempts() {
        return maxGenerationAttempts;
    }

    public void setMaxGenerationAttempts(int maxGenerationAttempts) {
        this.maxGenerationAttempts = maxGenerationAttempts;
    }

    public int getDefaultExpiryDays() {
        return defaultExpiryDays;
    }

    public void setDefaultExpiryDays(int defaultExpiryDays) {
        this.defaultExpiryDays = defaultExpiryDays;
    }

    public int getMaxLongUrlLength() {
        return maxLongUrlLength;
    }

    public void setMaxLongUrlLength(int maxLongUrlLength) {
        this.maxLongUrlLength = maxLongUrlLength;
    }

    public int getCustomAliasMinLength() {
        return customAliasMinLength;
    }

    public void setCustomAliasMinLength(int customAliasMinLength) {
        this.customAliasMinLength = customAliasMinLength;
    }

    public int getCustomAliasMaxLength() {
        return customAliasMaxLength;
    }

    public void setCustomAliasMaxLength(int customAliasMaxLength) {
        this.customAliasMaxLength = customAliasMaxLength;
    }
}
```

Register properties scanning.

In main class:

```java
package com.miniurl.shortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MiniUrlShortenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniUrlShortenerApplication.class, args);
    }
}
```

Why this matters:

```text
Without @ConfigurationPropertiesScan, Spring may not bind the properties bean.
```

---

## 17. Validation For Configuration

Configuration validation catches bad deployment before traffic arrives.

Example bad env:

```bash
MINI_URL_SHORT_CODE_LENGTH=1
```

Startup should fail.

Why?

Because a length of 1 creates tiny keyspace:

```text
62 possible short codes only
```

That causes collisions and outages.

ASCII:

```text
Bad config
   |
   v
Bind MiniUrlProperties
   |
   v
Validation fails
   |
   v
Application startup fails
   |
   v
No bad pod receives traffic
```

This is much better than:

```text
bad config
   |
   v
app starts
   |
   v
traffic arrives
   |
   v
short code collisions explode
```

Configuration validation protects production startup.

Rule:

```text
Fail fast at startup for invalid mandatory config.
```

---

## 18. Using Config In Service Layer

Use config through constructor injection.

Example response builder:

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.config.MiniUrlProperties;
import com.miniurl.shortener.url.dto.CreateShortUrlResponse;
import org.springframework.stereotype.Service;

@Service
public class ShortUrlResponseMapper {

    private final MiniUrlProperties properties;

    public ShortUrlResponseMapper(MiniUrlProperties properties) {
        this.properties = properties;
    }

    public CreateShortUrlResponse toResponse(ShortUrl shortUrl) {
        String fullShortUrl = properties.getPublicBaseUrl()
                + "/"
                + shortUrl.getShortCode();

        return new CreateShortUrlResponse(
                shortUrl.getShortCode(),
                fullShortUrl,
                shortUrl.getLongUrl(),
                shortUrl.getExpiresAt()
        );
    }
}
```

Example code generator:

```java
package com.miniurl.shortener.url.service;

import com.miniurl.shortener.config.MiniUrlProperties;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortCodeGenerator {

    private static final String ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final SecureRandom random = new SecureRandom();
    private final MiniUrlProperties properties;

    public ShortCodeGenerator(MiniUrlProperties properties) {
        this.properties = properties;
    }

    public String generate() {
        int length = properties.getShortCodeLength();
        StringBuilder builder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHABET.length());
            builder.append(ALPHABET.charAt(index));
        }

        return builder.toString();
    }
}
```

Good service code reads like:

```text
I need a property.
I ask MiniUrlProperties.
I do not care where it came from.
```

---

## 19. Secrets And Sensitive Config

Secrets are configuration values that must not be exposed.

Examples:

```text
DB_PASSWORD
JWT_SECRET
REDIS_PASSWORD
API keys
OAuth client secret
```

Do not commit secrets:

```yaml
spring:
  datasource:
    password: my-real-prod-password
```

Do this instead:

```yaml
spring:
  datasource:
    password: ${DB_PASSWORD}
```

Local development can use `.env` or Docker Compose environment variables.

Production should use:

```text
Kubernetes Secret
cloud secret manager
Vault
CI/CD secret injection
```

Logging rule:

```text
Never log full effective config blindly.
```

Bad:

```java
log.info("Properties: {}", properties);
```

If properties contain secrets later, this leaks.

Better:

```text
Log non-sensitive config only.
Mask secrets.
```

ASCII:

```text
Secret source
   |
   v
Environment / Secret Manager
   |
   v
Spring binding
   |
   v
Bean uses secret
   |
   X
Do not print to logs
```

---

## 20. Docker Compose Configuration

Docker Compose passes config using environment variables.

Example:

```yaml
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: local
      DB_URL: jdbc:postgresql://postgres:5432/miniurl
      DB_USERNAME: miniurl
      DB_PASSWORD: miniurl
      MINI_URL_PUBLIC_BASE_URL: http://localhost:8080
    depends_on:
      - postgres

  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: miniurl
      POSTGRES_USER: miniurl
      POSTGRES_PASSWORD: miniurl
    ports:
      - "5432:5432"
```

Important Docker networking detail:

```text
Inside app container, localhost means app container itself.
Postgres hostname should be postgres, the Compose service name.
```

Wrong inside Docker:

```text
jdbc:postgresql://localhost:5432/miniurl
```

Correct inside Docker Compose:

```text
jdbc:postgresql://postgres:5432/miniurl
```

ASCII:

```text
+--------------------+          +--------------------+
| app container      |          | postgres container |
| localhost = app    |          | service=postgres   |
+---------+----------+          +----------+---------+
          |                                ^
          | jdbc:postgresql://postgres...  |
          +--------------------------------+
```

This is one of the most common beginner Docker config bugs.

---

## 21. Kubernetes ConfigMap And Secret Mental Model

In Kubernetes, split non-sensitive and sensitive config.

ConfigMap:

```text
public base URL
short code length
max attempts
feature flags
logging level
```

Secret:

```text
DB password
JWT secret
API keys
```

ASCII:

```text
+--------------------+       +--------------------+
| ConfigMap          |       | Secret             |
| non-sensitive      |       | sensitive          |
+---------+----------+       +----------+---------+
          |                             |
          v                             v
          +-------------+---------------+
                        |
                        v
              +------------------+
              | Application Pod  |
              | env variables    |
              +------------------+
```

Example ConfigMap:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: miniurl-config
data:
  SPRING_PROFILES_ACTIVE: prod
  MINI_URL_PUBLIC_BASE_URL: https://sho.rt
  MINI_URL_SHORT_CODE_LENGTH: "7"
  MINI_URL_MAX_GENERATION_ATTEMPTS: "5"
```

Example Secret:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: miniurl-secret
type: Opaque
stringData:
  DB_URL: jdbc:postgresql://postgres:5432/miniurl
  DB_USERNAME: miniurl_app
  DB_PASSWORD: change-me
```

Deployment env:

```yaml
envFrom:
  - configMapRef:
      name: miniurl-config
  - secretRef:
      name: miniurl-secret
```

Rule:

```text
ConfigMap controls behavior.
Secret protects credentials.
Pod consumes both as environment.
```

---

## 22. Config Flow End To End

Full flow:

```text
Developer writes MiniUrlProperties
        |
        v
application.yml defines default keys
        |
        v
Environment overrides values
        |
        v
Spring Boot binds values
        |
        v
Validation checks values
        |
        v
Service uses typed config
        |
        v
Correct runtime behavior
```

ASCII:

```text
+-------------------+
| application.yml   |
+-------------------+
          |
          v
+-------------------+
| env variables     |
+-------------------+
          |
          v
+-------------------+
| Spring Environment|
+-------------------+
          |
          v
+---------------------------+
| MiniUrlProperties         |
| typed + validated config  |
+---------------------------+
          |
          v
+---------------------------+
| Services                  |
| generator / mapper / etc. |
+---------------------------+
          |
          v
+---------------------------+
| Runtime behavior          |
+---------------------------+
```

This is the clean boundary:

```text
Infrastructure gives strings.
Spring converts strings into typed values.
Application uses typed values safely.
```

---

## 23. Step-by-Step Dry Runs

### Dry Run 1: Local startup

Environment:

```bash
SPRING_PROFILES_ACTIVE=local
```

Config:

```yaml
mini-url:
  public-base-url: http://localhost:8080
```

Flow:

```text
1. Spring Boot starts.
2. application.yml loads.
3. application-local.yml loads because local profile is active.
4. local values override common values.
5. MiniUrlProperties binds.
6. Validation passes.
7. Service returns local short URL.
```

Output:

```json
{
  "shortCode": "AbC123x",
  "shortUrl": "http://localhost:8080/AbC123x"
}
```

---

### Dry Run 2: Production base URL override

Environment:

```bash
SPRING_PROFILES_ACTIVE=prod
MINI_URL_PUBLIC_BASE_URL=https://sho.rt
```

Flow:

```text
1. application.yml loads common config.
2. application-prod.yml loads prod overrides.
3. env variable MINI_URL_PUBLIC_BASE_URL overrides YAML.
4. MiniUrlProperties.publicBaseUrl becomes https://sho.rt.
5. Create API returns production URL.
```

Output:

```json
{
  "shortUrl": "https://sho.rt/AbC123x"
}
```

---

### Dry Run 3: Missing production DB password

Production config:

```yaml
spring:
  datasource:
    password: ${DB_PASSWORD}
```

Environment:

```text
DB_PASSWORD missing
```

Flow:

```text
1. Spring tries to resolve DB_PASSWORD.
2. No default exists.
3. Placeholder cannot resolve.
4. Application startup fails.
5. Pod does not become ready.
```

This is good.

Why?

```text
Better to fail before traffic than fail while serving users.
```

---

### Dry Run 4: Invalid short code length

Environment:

```bash
MINI_URL_SHORT_CODE_LENGTH=1
```

Flow:

```text
1. Spring binds value 1 to shortCodeLength.
2. @Min(4) validation runs.
3. Validation fails.
4. Application startup fails.
5. Engineer sees clear config validation error.
```

Root cause:

```text
Invalid runtime knob.
```

Fix:

```bash
MINI_URL_SHORT_CODE_LENGTH=7
```

---

### Dry Run 5: Docker DB hostname bug

Config:

```bash
DB_URL=jdbc:postgresql://localhost:5432/miniurl
```

Inside app container:

```text
localhost = app container
```

Flow:

```text
1. App starts in Docker.
2. Hikari tries to connect to localhost:5432.
3. No Postgres runs inside app container.
4. Connection refused.
5. Health check fails.
```

Fix:

```bash
DB_URL=jdbc:postgresql://postgres:5432/miniurl
```

---

## 24. Internal Execution Walkthrough

Spring Boot startup path:

```text
1. main method calls SpringApplication.run().
2. Spring Boot prepares Environment.
3. Config files and env variables are loaded.
4. Active profiles are resolved.
5. Profile-specific files are merged.
6. ApplicationContext is created.
7. @ConfigurationProperties classes are discovered.
8. mini-url.* values are bound to MiniUrlProperties.
9. Bean validation checks MiniUrlProperties.
10. If valid, beans depending on it are created.
11. Services receive MiniUrlProperties through constructor injection.
12. Application starts accepting requests.
```

ASCII:

```text
SpringApplication.run
        |
        v
Load ConfigData
        |
        v
Resolve Profiles
        |
        v
Build Environment
        |
        v
Bind MiniUrlProperties
        |
        +-- invalid --> startup failure
        |
        v
Create service beans
        |
        v
Application ready
```

Important:

```text
Configuration bugs are startup bugs when validation is strong.
Configuration bugs become production incidents when validation is weak.
```

---

## 25. Testing Strategy

Test configuration like production code.

### Test property binding

```java
package com.miniurl.shortener.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class MiniUrlPropertiesTest {

    @Autowired
    private MiniUrlProperties properties;

    @Test
    void shouldBindMiniUrlProperties() {
        assertThat(properties.getPublicBaseUrl()).isEqualTo("http://test.local");
        assertThat(properties.getShortCodeLength()).isEqualTo(7);
        assertThat(properties.getMaxGenerationAttempts()).isEqualTo(3);
    }
}
```

### Test service uses config

```java
@Test
void shouldBuildShortUrlUsingConfiguredBaseUrl() {
    ShortUrl shortUrl = new ShortUrl();
    shortUrl.setShortCode("abc1234");
    shortUrl.setLongUrl("https://example.com");

    CreateShortUrlResponse response = mapper.toResponse(shortUrl);

    assertThat(response.getShortUrl()).isEqualTo("http://test.local/abc1234");
}
```

### Test invalid config

For advanced testing, use `ApplicationContextRunner`.

```java
class MiniUrlPropertiesValidationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
                    "mini-url.public-base-url=http://localhost:8080",
                    "mini-url.short-code-length=1"
            );

    @Test
    void shouldFailWhenShortCodeLengthTooSmall() {
        contextRunner.run(context ->
                assertThat(context).hasFailed()
        );
    }
}
```

Testing rule:

```text
Do not only test business logic.
Test the config that business logic depends on.
```

---

## 26. Production Failure Stories

### Failure Story 1: Production uses localhost base URL

Bug:

```text
Short URLs sent to users are http://localhost:8080/abc1234.
```

Root cause:

```text
MINI_URL_PUBLIC_BASE_URL missing.
application.yml default localhost was used in production.
```

Impact:

```text
All generated links are useless for real users.
```

Fix:

```text
In prod, require MINI_URL_PUBLIC_BASE_URL with no localhost fallback.
Add startup validation to reject localhost in prod.
```

Lesson:

```text
Safe local defaults can be dangerous production defaults.
```

---

### Failure Story 2: Docker app cannot connect to Postgres

Bug:

```text
Connection refused to localhost:5432.
```

Root cause:

```text
DB_URL used localhost inside app container.
```

Fix:

```text
Use Docker Compose service name: postgres.
```

Lesson:

```text
localhost changes meaning inside containers.
```

---

### Failure Story 3: Secret committed to Git

Bug:

```text
Production DB password accidentally committed in application-prod.yml.
```

Impact:

```text
Credential rotation required.
Security incident.
```

Fix:

```text
Remove secret from Git history if needed.
Rotate password.
Use environment/Kubernetes Secret/secret manager.
```

Lesson:

```text
Configuration is not automatically safe. Secrets need a separate path.
```

---

### Failure Story 4: Wrong active profile

Bug:

```text
Production app starts with local profile.
```

Impact:

```text
debug logs enabled
wrong DB target
unsafe actuator exposure
localhost public URL
```

Root cause:

```text
SPRING_PROFILES_ACTIVE not set correctly in deployment.
```

Fix:

```text
Set profile explicitly in deployment.
Add startup log showing active profiles.
Add guard that blocks local profile in production image.
```

Lesson:

```text
Profile is a deployment decision and must be visible.
```

---

### Failure Story 5: Short code collision storm

Bug:

```text
Many create requests fail due to duplicate short_code.
```

Root cause:

```text
SHORT_CODE_LENGTH accidentally set to 3.
```

Impact:

```text
Tiny keyspace.
High collision rate.
More retries.
Higher latency.
Eventually 500 generation failures.
```

Fix:

```text
Validate short-code-length minimum.
Use safer defaults.
Monitor collision rate.
```

Lesson:

```text
Some config values directly affect scalability.
```

---

## 27. Debugging Mindset

When config is wrong, ask:

```text
Which profile is active?
Which property key is wrong?
Is the env variable name correct?
Is YAML indentation correct?
Is the value overridden by a higher-precedence source?
Is a default hiding a missing production value?
Is the value type valid?
Did @ConfigurationProperties bind correctly?
Did validation run?
Is the app inside Docker/Kubernetes where hostnames differ?
```

Useful startup log idea:

```java
@Component
public class StartupConfigLogger implements ApplicationRunner {

    private final Environment environment;
    private final MiniUrlProperties properties;

    public StartupConfigLogger(Environment environment, MiniUrlProperties properties) {
        this.environment = environment;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Active profiles: {}", Arrays.toString(environment.getActiveProfiles()));
        log.info("MiniURL publicBaseUrl: {}", properties.getPublicBaseUrl());
        log.info("MiniURL shortCodeLength: {}", properties.getShortCodeLength());
    }
}
```

Do not log secrets.

Useful actuator endpoint in secure environment:

```text
/actuator/env
```

But be careful:

```text
Do not expose actuator env publicly.
It can leak sensitive configuration.
```

Debug map:

```text
Property not loaded:
    check profile and file name

Env var ignored:
    check relaxed binding name

Wrong DB in Docker:
    check hostname

Startup fails:
    check missing placeholders and validation

Wrong public URL:
    check MINI_URL_PUBLIC_BASE_URL
```

---

## 28. Common Mistakes

### Mistake 1: Hardcoding environment values

Wrong:

```java
String baseUrl = "http://localhost:8080";
```

Correct:

```java
properties.getPublicBaseUrl();
```

### Mistake 2: Scattered @Value

Wrong:

```text
@Value used in controller, service, mapper, generator.
```

Correct:

```text
One MiniUrlProperties class injected where needed.
```

### Mistake 3: Secrets in Git

Wrong:

```yaml
password: real-prod-password
```

Correct:

```yaml
password: ${DB_PASSWORD}
```

### Mistake 4: Dangerous prod defaults

Wrong:

```yaml
DB_PASSWORD: ${DB_PASSWORD:miniurl}
```

Correct:

```yaml
DB_PASSWORD: ${DB_PASSWORD}
```

### Mistake 5: No config validation

Wrong:

```text
short-code-length can be 1.
```

Correct:

```java
@Min(4)
private int shortCodeLength;
```

### Mistake 6: Wrong Docker hostname

Wrong:

```text
jdbc:postgresql://localhost:5432/miniurl
```

Correct in Compose:

```text
jdbc:postgresql://postgres:5432/miniurl
```

### Mistake 7: Setting profile inside application.yml

Wrong:

```yaml
spring:
  profiles:
    active: prod
```

Correct:

```text
Set SPRING_PROFILES_ACTIVE in environment.
```

### Mistake 8: Exposing actuator env publicly

Wrong:

```yaml
management.endpoints.web.exposure.include: "*"
```

Correct:

```text
Expose only required endpoints and secure them.
```

---

## 29. Interview-Ready Explanation

If interviewer asks:

```text
How do you manage configuration in your Spring Boot service?
```

Strong answer:

```text
I treat configuration as the runtime contract between the same application artifact and different environments. Common defaults live in application.yml, environment-specific overrides live in profile files like application-local.yml, application-test.yml, and application-prod.yml, and deployment-specific values come from environment variables, Docker Compose, Kubernetes ConfigMaps, or Secrets. For application-specific settings, I prefer @ConfigurationProperties over scattered @Value because it gives a type-safe, centralized, validated configuration object. Critical production values such as DB credentials and public domain should not have unsafe localhost defaults; missing values should fail startup. I also validate config values like short code length and retry count so bad deployments fail before receiving traffic. Secrets are injected through environment or secret managers and never committed or logged.
```

Why this is strong:

```text
1. Mentions same artifact across environments.
2. Explains profiles.
3. Explains env variable overrides.
4. Chooses @ConfigurationProperties for type safety.
5. Mentions validation and fail-fast startup.
6. Separates secrets from normal config.
7. Shows Docker/Kubernetes production awareness.
```

Senior one-liner:

```text
The app should not be rebuilt for every environment; it should be reconfigured safely for every environment.
```

---

## 30. Senior Engineer Checklist

Before moving forward, confirm:

```text
[ ] application.yml has common safe defaults
[ ] application-local.yml exists
[ ] application-test.yml exists
[ ] application-prod.yml exists
[ ] production secrets are not committed
[ ] critical prod values have no dangerous defaults
[ ] MiniUrlProperties exists
[ ] @ConfigurationPropertiesScan is enabled
[ ] MiniUrlProperties has validation annotations
[ ] services use constructor-injected config
[ ] no hardcoded localhost public URL in service code
[ ] Docker Compose uses service hostname for DB
[ ] Kubernetes separates ConfigMap and Secret
[ ] active profile is controlled by environment
[ ] config tests verify property binding
[ ] startup logs show active profiles safely
[ ] secrets are never printed
```

If these are checked, configuration is production-shaped.

---

## 31. One-Page Cheat Sheet

```text
Core mental model:
Configuration is the runtime contract between code and environment.

Code:
defines knobs

Config:
provides values

Spring config sources:
application.yml
application-{profile}.yml
environment variables
command-line args

Profiles:
local = developer machine
test = deterministic tests
prod = safe production config

Best practice:
Use @ConfigurationProperties for grouped config.
Use @Value only for tiny isolated values.

MiniURL properties:
public-base-url
short-code-length
max-generation-attempts
default-expiry-days
max-long-url-length
custom-alias limits

Secrets:
Never commit.
Never log.
Inject through env, Kubernetes Secret, Vault, or cloud secret manager.

Docker rule:
localhost inside container means same container.
Use Compose service name for DB.

Production rule:
Critical missing config should fail startup.
Bad config should not receive traffic.
```

---

## 32. One Picture To Remember

```text
                    CONFIG & PROPERTIES MENTAL MODEL

                         "Same code, different worlds"

                         Application JAR
                              |
          +-------------------+-------------------+
          |                   |                   |
          v                   v                   v
   application-local.yml application-test.yml application-prod.yml
          |                   |                   |
          v                   v                   v
   local env vars       test properties       prod env vars
          |                   |                   |
          +-------------------+-------------------+
                              |
                              v
                    Spring Environment
                              |
                              v
              @ConfigurationProperties binding
                              |
                              v
                   MiniUrlProperties
                 typed + validated config
                              |
                              v
              +---------------+---------------+
              |               |               |
              v               v               v
       ShortCodeGenerator  ResponseMapper  Validators
              |               |               |
              v               v               v
         correct runtime behavior per environment

FINAL MEMORY:

Code should not know where it runs.
Configuration tells it how to behave there.
Validation prevents bad config from becoming a production incident.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Configuration is the runtime contract between code and environment.
2. Profiles select environment-specific values, but the environment should activate the profile.
3. @ConfigurationProperties is better than scattered @Value for grouped application config.
4. Production secrets must come from environment/secret systems, not Git.
5. Invalid or missing critical config should fail startup before traffic reaches the app.
```

Next chapter:

```text
016_Actuator_Health_Checks.md
```
