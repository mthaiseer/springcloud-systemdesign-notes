# 044_Config_Server.md
# MiniURLShortener — Spring Cloud Config Server

> Core mental model: **Config Server is the central configuration brain for microservices. Instead of every service carrying its own environment-specific settings, services ask one trusted config source: “What configuration should I run with in this environment?”**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Configuration Without Config Server](#4-configuration-without-config-server)
- [5. Configuration With Config Server](#5-configuration-with-config-server)
- [6. Config Server Architecture](#6-config-server-architecture)
- [7. Spring Cloud Config Request Flow](#7-spring-cloud-config-request-flow)
- [8. Config Repository Structure](#8-config-repository-structure)
- [9. Local File Based Config Server](#9-local-file-based-config-server)
- [10. Git Based Config Server](#10-git-based-config-server)
- [11. Config Client Setup](#11-config-client-setup)
- [12. Profile Based Configuration](#12-profile-based-configuration)
- [13. Shared vs Service-Specific Config](#13-shared-vs-service-specific-config)
- [14. MiniURLShortener Config Design](#14-miniurlshortener-config-design)
- [15. Bootstrap vs Config Import](#15-bootstrap-vs-config-import)
- [16. Refreshing Configuration](#16-refreshing-configuration)
- [17. Secrets And Security Mindset](#17-secrets-and-security-mindset)
- [18. Fail Fast vs Optional Config](#18-fail-fast-vs-optional-config)
- [19. Config Server High Availability](#19-config-server-high-availability)
- [20. Docker Compose Setup](#20-docker-compose-setup)
- [21. Kubernetes Setup Mental Model](#21-kubernetes-setup-mental-model)
- [22. Step-by-Step Dry Runs](#22-step-by-step-dry-runs)
- [23. Internal Execution Walkthrough](#23-internal-execution-walkthrough)
- [24. Production Failure Stories](#24-production-failure-stories)
- [25. Debugging Mindset](#25-debugging-mindset)
- [26. Common Mistakes](#26-common-mistakes)
- [27. Interview-Ready Explanation](#27-interview-ready-explanation)
- [28. Senior Engineer Checklist](#28-senior-engineer-checklist)
- [29. One-Page Cheat Sheet](#29-one-page-cheat-sheet)
- [30. One Picture To Remember](#30-one-picture-to-remember)

---

## 1. Why This Exists

In earlier MiniURLShortener chapters, each service could keep configuration inside its own `application.yml`.

That works when the system is small:

```text
url-service
  application.yml

gateway-service
  application.yml

eureka-server
  application.yml
```

But production systems quickly have many environments:

```text
local
qa
staging
production
```

And many services:

```text
url-service
redirect-service
analytics-worker
api-gateway
eureka-server
notification-service
```

Each service needs configuration:

```text
server port
Postgres URL
Redis URL
Kafka brokers
Eureka URL
logging level
rate-limit settings
feature flags
timeout values
retry values
cache TTL
```

Without a central configuration model, configuration becomes scattered.

```text
Same value copied in many repos.
Production password accidentally committed.
One pod has old timeout.
Another service points to wrong Redis.
Debug logging stays enabled in production.
Changing one value requires rebuilding images.
```

Spring Cloud Config Server solves this by making configuration external, centralized, versioned, and environment-aware.

Core idea:

```text
Build artifact should be same.
Runtime config should change per environment.
```

A Docker image should not be rebuilt just because the Redis host changed.

---

## 2. The One Core Mental Model

Config Server is a:

```text
CENTRAL CONFIG BRAIN
```

Services do not guess their runtime settings.
They ask the config brain.

ASCII:

```text
                         Git / File Config Repo
                    +-----------------------------+
                    | url-service-prod.yml        |
                    | gateway-service-prod.yml    |
                    | application-prod.yml        |
                    +-------------+---------------+
                                  |
                                  v
                         +----------------+
                         | Config Server  |
                         | central brain  |
                         +--------+-------+
                                  |
               services ask:      |      returns merged config
               who am I?          |
               which profile?     |
                                  v
     +----------------+   +----------------+   +----------------+
     | URL Service    |   | Gateway        |   | Analytics      |
     | prod profile   |   | prod profile   |   | prod profile   |
     +----------------+   +----------------+   +----------------+
```

One-line memory:

```text
Config Server separates code from runtime decisions.
```

For MiniURLShortener:

```text
The same url-service.jar can run locally with local Postgres,
in staging with staging Postgres,
and in production with production Postgres,
without changing code or rebuilding the jar.
```

---

## 3. Problem Statement

Build a Spring Cloud Config Server for MiniURLShortener.

It must support:

```text
1. Central configuration for multiple services.
2. Environment-specific profiles.
3. Shared common configuration.
4. Service-specific configuration.
5. Local file backend for learning.
6. Git backend for production-shaped setup.
7. Config clients reading from config server.
8. Optional refresh behavior.
9. Safe handling of secrets.
10. Debugging when config is missing or wrong.
```

Out of scope for this chapter:

```text
1. Full Vault deep dive.
2. Full Kubernetes ConfigMap and Secret deep dive.
3. Complete GitOps pipeline.
4. Spring Cloud Bus deep implementation.
```

This chapter focuses on how Config Server works and how to use it cleanly in a microservice system.

---

## 4. Configuration Without Config Server

Without Config Server, each service owns many config files.

```text
url-service/
  application-local.yml
  application-qa.yml
  application-prod.yml

gateway-service/
  application-local.yml
  application-qa.yml
  application-prod.yml

analytics-worker/
  application-local.yml
  application-qa.yml
  application-prod.yml
```

This creates duplication.

Example duplicated values:

```yaml
spring:
  kafka:
    bootstrap-servers: kafka-prod:9092

eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka
```

If Kafka broker changes, you must update many repositories.

ASCII pain:

```text
Kafka broker changed
       |
       v
+------+-------+    +------+-------+    +------+-------+
| url-service  |    | gateway      |    | analytics    |
| update file  |    | update file  |    | update file  |
+------+-------+    +------+-------+    +------+-------+
       |                    |                    |
       v                    v                    v
   rebuild?             rebuild?             rebuild?
   redeploy?            redeploy?            redeploy?
```

Problems:

```text
1. Config drift between services.
2. Repeated values.
3. Harder environment promotion.
4. Harder auditing.
5. Higher chance of accidental secret leaks.
6. Harder operational changes.
```

For one service, local config is okay.
For many services, central config becomes useful.

---

## 5. Configuration With Config Server

With Config Server, each service asks for configuration at startup.

```text
Service name: url-service
Profile: prod
Label: main branch
```

Request shape:

```http
GET /url-service/prod
```

Config Server loads and merges relevant files:

```text
application.yml
application-prod.yml
url-service.yml
url-service-prod.yml
```

Then returns one environment response.

ASCII:

```text
url-service starts
       |
       v
asks Config Server:
/url-service/prod
       |
       v
Config Server reads repo
       |
       v
merges common + profile + service config
       |
       v
returns properties
       |
       v
url-service starts with correct runtime config
```

This allows:

```text
same artifact
multiple environments
central config history
controlled changes
less duplication
```

Important:

```text
Config Server does not remove the need for environment variables.
It organizes configuration, but secrets still need special care.
```

---

## 6. Config Server Architecture

Components:

```text
1. Config repository
2. Config Server application
3. Config clients
4. Profiles
5. Labels
```

ASCII:

```text
+----------------------------------------------------+
|                    Config Repo                     |
|----------------------------------------------------|
| application.yml                                    |
| application-prod.yml                               |
| url-service.yml                                    |
| url-service-prod.yml                               |
| gateway-service-prod.yml                           |
+-------------------------+--------------------------+
                          |
                          v
+----------------------------------------------------+
|                 Spring Cloud Config Server         |
|----------------------------------------------------|
| exposes HTTP endpoints:                            |
| /{application}/{profile}                           |
| /{application}/{profile}/{label}                   |
+-------------------------+--------------------------+
                          |
       +------------------+------------------+
       |                  |                  |
       v                  v                  v
+--------------+   +--------------+   +--------------+
| url-service  |   | gateway      |   | analytics    |
| config client|   | config client|   | config client|
+--------------+   +--------------+   +--------------+
```

Terms:

```text
application = spring.application.name
profile     = active profile, like local/prod
label       = Git branch/tag, like main/release-v1
```

Example:

```text
/url-service/prod/main
```

Means:

```text
Give config for url-service using prod profile from main branch.
```

---

## 7. Spring Cloud Config Request Flow

At startup, config client runs very early.

```text
1. Spring Boot process starts.
2. It knows spring.application.name.
3. It knows active profile.
4. It knows config server URI.
5. It calls Config Server.
6. Config Server reads backend repo.
7. Config Server returns properties.
8. Client adds properties to Environment.
9. Beans are created using resolved properties.
```

ASCII:

```text
+------------------+
| JVM starts       |
+--------+---------+
         |
         v
+------------------+
| Load early config|
| app name/profile |
+--------+---------+
         |
         v
+-----------------------------+
| Call Config Server          |
| /url-service/prod           |
+--------+--------------------+
         |
         v
+-----------------------------+
| Receive external properties |
+--------+--------------------+
         |
         v
+-----------------------------+
| Build Spring Environment    |
+--------+--------------------+
         |
         v
+-----------------------------+
| Create beans using config   |
+-----------------------------+
```

Why early?

Because many beans need configuration while being created:

```text
DataSource
Redis connection factory
Kafka producer
Eureka client
Hikari pool
logging levels
```

If config comes too late, beans already start with wrong values.

---

## 8. Config Repository Structure

Recommended learning structure:

```text
config-repo/
  application.yml
  application-local.yml
  application-prod.yml

  url-service.yml
  url-service-local.yml
  url-service-prod.yml

  gateway-service.yml
  gateway-service-local.yml
  gateway-service-prod.yml

  analytics-worker.yml
  analytics-worker-local.yml
  analytics-worker-prod.yml
```

Meaning:

```text
application.yml              shared across all services
application-prod.yml         shared across all services in prod
url-service.yml              shared across all url-service profiles
url-service-prod.yml         url-service prod-specific config
```

Merge mental model:

```text
common base
   + common profile
      + service base
         + service profile
```

ASCII:

```text
For url-service + prod:

application.yml
      |
      v
application-prod.yml
      |
      v
url-service.yml
      |
      v
url-service-prod.yml
      |
      v
Final Environment
```

Later files can override earlier values.

Example:

```yaml
# application.yml
logging:
  level:
    root: INFO

# url-service-prod.yml
logging:
  level:
    com.miniurl.shortener: WARN
```

Final result for app package:

```text
WARN
```

---

## 9. Local File Based Config Server

For learning, use native profile and local folder.

Create Config Server project:

```text
config-server/
  src/main/java/com/miniurl/configserver/ConfigServerApplication.java
  src/main/resources/application.yml
```

Dependency:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

Main class:

```java
package com.miniurl.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

Config Server `application.yml`:

```yaml
server:
  port: 8888

spring:
  application:
    name: config-server
  profiles:
    active: native
  cloud:
    config:
      server:
        native:
          search-locations: file:./config-repo
```

Folder:

```text
config-server/
  config-repo/
    application.yml
    url-service.yml
    url-service-local.yml
```

Test endpoint:

```http
GET http://localhost:8888/url-service/local
```

Expected:

```text
Config Server returns property sources for url-service local profile.
```

---

## 10. Git Based Config Server

Production-shaped setup usually uses Git.

Why Git?

```text
1. Version history.
2. Pull requests.
3. Code review.
4. Rollback.
5. Branch/tag based releases.
6. Audit trail.
```

Config Server `application.yml`:

```yaml
server:
  port: 8888

spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-org/miniurl-config-repo.git
          default-label: main
          clone-on-start: true
```

Private Git repository example:

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: git@github.com:your-org/miniurl-config-repo.git
          default-label: main
          clone-on-start: true
```

Important production note:

```text
Do not put plain production passwords in Git.
Use Vault, Kubernetes Secrets, cloud secret managers, or encrypted values.
```

Git label mental model:

```text
main       -> latest approved config
release-v1 -> stable config for release v1
hotfix-x   -> emergency config branch
```

Endpoint:

```http
GET /url-service/prod/main
GET /url-service/prod/release-v1
```

ASCII:

```text
Git branch main
       |
       v
Config Server clones repo
       |
       v
Clients request by app/profile/label
       |
       v
Service receives config from that branch
```

---

## 11. Config Client Setup

For `url-service`, add dependency:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

Client `application.yml`:

```yaml
spring:
  application:
    name: url-service
  profiles:
    active: local
  config:
    import: optional:configserver:http://localhost:8888
```

Meaning:

```text
application name = url-service
active profile   = local
config server    = http://localhost:8888
```

Client asks:

```http
GET http://localhost:8888/url-service/local
```

Example config in config repo:

```yaml
# url-service-local.yml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/miniurl
    username: miniurl
    password: miniurl

app:
  short-url:
    base-url: http://localhost:8080
    code-length: 7
```

Service code using config:

```java
package com.miniurl.shortener.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.short-url")
public class ShortUrlProperties {

    private String baseUrl;
    private int codeLength;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getCodeLength() {
        return codeLength;
    }

    public void setCodeLength(int codeLength) {
        this.codeLength = codeLength;
    }
}
```

Enable properties:

```java
@ConfigurationPropertiesScan
@SpringBootApplication
public class UrlServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UrlServiceApplication.class, args);
    }
}
```

---

## 12. Profile Based Configuration

Profiles answer:

```text
Which environment am I running in?
```

Common profiles:

```text
local
qa
staging
prod
```

Example files:

```text
url-service-local.yml
url-service-prod.yml
```

Local:

```yaml
app:
  short-url:
    base-url: http://localhost:8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/miniurl
```

Prod:

```yaml
app:
  short-url:
    base-url: https://sho.rt

spring:
  datasource:
    url: jdbc:postgresql://prod-postgres:5432/miniurl
```

Same code, different runtime behavior.

ASCII:

```text
same jar
  |
  +-- local profile --> local DB, localhost base URL
  |
  +-- prod profile  --> prod DB, public base URL
```

Golden rule:

```text
Profiles should change environment wiring, not business logic.
```

Bad profile usage:

```text
prod uses different algorithm than local
```

Good profile usage:

```text
prod uses different DB host, Redis host, log level, timeout
```

---

## 13. Shared vs Service-Specific Config

Shared config belongs in `application.yml`.

Example:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus

logging:
  pattern:
    level: "%5p [traceId=%X{traceId:-}, spanId=%X{spanId:-}]"
```

Service-specific config belongs in service file.

```yaml
# url-service.yml
app:
  short-url:
    code-length: 7
    default-expiry-days: 365
```

```yaml
# gateway-service.yml
spring:
  cloud:
    gateway:
      routes:
        - id: url-service
          uri: lb://url-service
          predicates:
            - Path=/api/v1/urls/**
```

Decision table:

```text
+------------------------------+-------------------------+
| Config                       | File                    |
+------------------------------+-------------------------+
| actuator common exposure     | application.yml         |
| common logging pattern       | application.yml         |
| Kafka broker for all services| application-prod.yml    |
| url code length              | url-service.yml         |
| url DB connection            | url-service-prod.yml    |
| gateway routes               | gateway-service.yml     |
+------------------------------+-------------------------+
```

Mental model:

```text
Shared config = everyone needs it.
Service config = only this service understands it.
```

---

## 14. MiniURLShortener Config Design

For current MiniURLShortener system:

```text
config-repo/
  application.yml
  application-local.yml
  application-prod.yml
  eureka-server-local.yml
  gateway-service-local.yml
  url-service-local.yml
  analytics-worker-local.yml
```

`application-local.yml`:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka

management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus
```

`url-service-local.yml`:

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/miniurl
    username: miniurl
    password: miniurl
  jpa:
    hibernate:
      ddl-auto: validate
  data:
    redis:
      host: localhost
      port: 6379

app:
  short-url:
    base-url: http://localhost:8080
    code-length: 7
    cache-ttl-seconds: 3600
```

`gateway-service-local.yml`:

```yaml
server:
  port: 8080

spring:
  cloud:
    gateway:
      routes:
        - id: url-service-create
          uri: lb://url-service
          predicates:
            - Path=/api/v1/urls/**
        - id: url-service-redirect
          uri: lb://url-service
          predicates:
            - Path=/{shortCode}
```

ASCII:

```text
Gateway config:
  routes traffic

URL service config:
  DB, Redis, short-code settings

Application common config:
  Eureka, actuator, logging
```

---

## 15. Bootstrap vs Config Import

Older Spring Cloud used `bootstrap.yml`.

Modern Spring Boot often uses:

```yaml
spring:
  config:
    import: configserver:http://localhost:8888
```

This tells Spring Boot to load Config Server during config data phase.

Recommended simple client config:

```yaml
spring:
  application:
    name: url-service
  profiles:
    active: local
  config:
    import: optional:configserver:http://localhost:8888
```

`optional:` means:

```text
If config server is unavailable, do not fail startup immediately.
```

Without optional:

```yaml
spring:
  config:
    import: configserver:http://localhost:8888
```

Means:

```text
Config server must be available.
```

Production preference often:

```text
fail fast for critical config
```

Local developer preference often:

```text
optional config server for easier startup
```

Decision:

```text
local      -> optional may be okay
production -> fail fast is safer
```

---

## 16. Refreshing Configuration

By default, many config values are read at startup.

If config repo changes:

```text
Service does not automatically know unless refreshed/restarted.
```

Options:

```text
1. Restart service pods.
2. Use actuator refresh endpoint.
3. Use Spring Cloud Bus with message broker.
```

Actuator refresh dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Expose refresh:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,refresh
```

Refresh endpoint:

```http
POST /actuator/refresh
```

Use `@RefreshScope` carefully:

```java
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@RefreshScope
@Component
public class RedirectCacheSettings {

    private final ShortUrlProperties properties;

    public RedirectCacheSettings(ShortUrlProperties properties) {
        this.properties = properties;
    }

    public int cacheTtlSeconds() {
        return properties.getCacheTtlSeconds();
    }
}
```

Important production caution:

```text
Dynamic refresh is powerful but dangerous.
Not every setting should change while the app is running.
```

Safe to refresh:

```text
feature flags
some timeout values
cache TTL
logging level
```

Usually restart instead:

```text
DB URL
Kafka brokers
thread pool core sizes
security-sensitive config
```

---

## 17. Secrets And Security Mindset

Configuration and secrets are not the same thing.

Configuration:

```text
port
timeout
feature flag
cache TTL
service URL
```

Secrets:

```text
DB password
JWT signing key
API key
OAuth client secret
private key
```

Bad:

```yaml
spring:
  datasource:
    password: super-secret-prod-password
```

Better options:

```text
1. Kubernetes Secret mounted as env var.
2. HashiCorp Vault.
3. AWS Secrets Manager / GCP Secret Manager / Azure Key Vault.
4. Encrypted config values with strict key management.
```

Example using environment variable placeholder:

```yaml
spring:
  datasource:
    password: ${DB_PASSWORD}
```

ASCII:

```text
Config Server gives structure
        |
        v
password placeholder ${DB_PASSWORD}
        |
        v
runtime environment injects secret
        |
        v
service receives final value
```

Rule:

```text
Config Server can distribute config.
Secret manager should protect secrets.
```

Do not put raw secrets in Git just because Config Server reads Git.

---

## 18. Fail Fast vs Optional Config

When config server is unavailable, what should service do?

Two models:

```text
1. Fail fast.
2. Start with fallback/local config.
```

Fail fast:

```text
If config is missing, service refuses to start.
```

Good for production because wrong config can be worse than downtime.

Optional config:

```text
If config server is down, service starts with local defaults.
```

Good for local development.
Risky for production.

ASCII:

```text
Production startup
       |
       v
Config Server unavailable?
       |
       +-- yes --> fail startup
       |
       +-- no  --> start with approved config
```

Why fail fast is safer:

```text
A URL service accidentally using local DB settings in production is dangerous.
A gateway missing routes is dangerous.
A service with wrong JWT key is dangerous.
```

Recommended:

```yaml
# local
spring.config.import: optional:configserver:http://localhost:8888

# prod
spring.config.import: configserver:http://config-server:8888
```

---

## 19. Config Server High Availability

Config Server becomes important infrastructure.

If every service needs it at startup, it must be reliable.

HA model:

```text
Run multiple Config Server instances behind load balancer.
Use Git repo as backend.
Cache cloned repo locally.
Use health checks.
Use readiness/liveness probes.
```

ASCII:

```text
                  +------------------+
                  | Config Repo Git  |
                  +---------+--------+
                            |
              +-------------+-------------+
              |                           |
              v                           v
       +--------------+            +--------------+
       | Config Srv 1 |            | Config Srv 2 |
       +------+-------+            +------+-------+
              |                           |
              +-------------+-------------+
                            |
                            v
                    +---------------+
                    | Load Balancer |
                    +-------+-------+
                            |
             +--------------+--------------+
             |              |              |
             v              v              v
       url-service     gateway       analytics
```

Important nuance:

```text
Services usually do not call Config Server for every request.
They call mainly at startup or refresh time.
```

So Config Server outage after services started may not immediately break live traffic.

But it affects:

```text
new deployments
pod restarts
autoscaling new pods
refresh operations
```

Production checklist:

```text
1. Multiple instances.
2. Health endpoint.
3. Git backend availability.
4. Startup fail policy decided.
5. Monitoring and alerts.
6. Config change review process.
```

---

## 20. Docker Compose Setup

Example compose for learning:

```yaml
services:
  config-server:
    build: ./config-server
    ports:
      - "8888:8888"
    volumes:
      - ./config-repo:/config-repo
    environment:
      SPRING_PROFILES_ACTIVE: native
      SPRING_CLOUD_CONFIG_SERVER_NATIVE_SEARCH_LOCATIONS: file:/config-repo

  eureka-server:
    build: ./eureka-server
    ports:
      - "8761:8761"
    depends_on:
      - config-server
    environment:
      SPRING_CONFIG_IMPORT: configserver:http://config-server:8888
      SPRING_PROFILES_ACTIVE: local

  url-service:
    build: ./url-service
    ports:
      - "8081:8081"
    depends_on:
      - config-server
      - eureka-server
      - postgres
      - redis
    environment:
      SPRING_CONFIG_IMPORT: configserver:http://config-server:8888
      SPRING_PROFILES_ACTIVE: local
      DB_PASSWORD: miniurl
```

Important:

```text
inside Docker network, use service name config-server, not localhost.
```

Wrong inside container:

```text
http://localhost:8888
```

Correct inside compose network:

```text
http://config-server:8888
```

ASCII:

```text
Host machine localhost:8888 -> Config Server port mapping
Container url-service      -> http://config-server:8888
```

---

## 21. Kubernetes Setup Mental Model

In Kubernetes, Config Server is another Deployment + Service.

```text
Deployment: config-server
Service: config-server
Port: 8888
```

Other services use:

```text
http://config-server:8888
```

ASCII:

```text
+---------------- Kubernetes Cluster ----------------+
|                                                     |
|  +-------------------+      +--------------------+  |
|  | config-server Pod | ---> | Config Git Repo    |  |
|  +---------+---------+      +--------------------+  |
|            |                                        |
|            v                                        |
|  +-------------------+                              |
|  | Service           |                              |
|  | config-server     |                              |
|  +---------+---------+                              |
|            |                                        |
|   +--------+---------+---------+                    |
|   |                  |         |                    |
|   v                  v         v                    |
| url-service      gateway    analytics               |
|                                                     |
+-----------------------------------------------------+
```

Kubernetes env example:

```yaml
env:
  - name: SPRING_CONFIG_IMPORT
    value: "configserver:http://config-server:8888"
  - name: SPRING_PROFILES_ACTIVE
    value: "prod"
  - name: DB_PASSWORD
    valueFrom:
      secretKeyRef:
        name: url-service-db-secret
        key: password
```

Production idea:

```text
Kubernetes gives runtime secrets.
Config Server gives central non-secret config.
```

---

## 22. Step-by-Step Dry Runs

### Dry Run 1: url-service starts locally

Client config:

```yaml
spring:
  application:
    name: url-service
  profiles:
    active: local
  config:
    import: optional:configserver:http://localhost:8888
```

Flow:

```text
1. url-service JVM starts.
2. Spring sees application name url-service.
3. Spring sees active profile local.
4. Spring calls Config Server /url-service/local.
5. Config Server reads config repo.
6. It merges application.yml + application-local.yml + url-service.yml + url-service-local.yml.
7. url-service receives final properties.
8. DataSource, Redis, Eureka client, and app properties are created.
9. Service starts on configured port.
```

---

### Dry Run 2: wrong service name

Client accidentally uses:

```yaml
spring:
  application:
    name: urls-service
```

But config file is:

```text
url-service-local.yml
```

Flow:

```text
1. Client asks /urls-service/local.
2. Config Server cannot find urls-service-local.yml.
3. Only common application config may load.
4. url-service-specific DB/Redis settings are missing.
5. Service may fail with missing datasource config.
```

Debug clue:

```text
Check spring.application.name spelling.
```

---

### Dry Run 3: Docker localhost mistake

Inside `url-service` container:

```yaml
spring.config.import: configserver:http://localhost:8888
```

Flow:

```text
1. url-service starts inside its own container.
2. localhost means url-service container itself.
3. No Config Server is running inside that container.
4. Connection refused.
5. Service fails startup.
```

Fix:

```yaml
spring.config.import: configserver:http://config-server:8888
```

---

### Dry Run 4: production DB password via env var

Config repo:

```yaml
spring:
  datasource:
    password: ${DB_PASSWORD}
```

Kubernetes Secret injects:

```text
DB_PASSWORD=real-prod-password
```

Flow:

```text
1. Config Server returns property containing placeholder.
2. Spring Environment resolves DB_PASSWORD from runtime env.
3. DataSource gets final password.
4. Secret is not stored as plain Git config.
```

---

### Dry Run 5: config changed but service not refreshed

Change:

```yaml
app.short-url.cache-ttl-seconds: 60
```

Previously:

```yaml
app.short-url.cache-ttl-seconds: 3600
```

Flow:

```text
1. Config repo updated.
2. Config Server can serve new value.
3. Running url-service still has old value.
4. Restart or /actuator/refresh needed.
```

Lesson:

```text
Config repository update does not magically mutate all running beans.
```

---

## 23. Internal Execution Walkthrough

Detailed startup path:

```text
1. Java process starts.
2. SpringApplication prepares Environment.
3. Config data loading phase begins.
4. spring.config.import is evaluated.
5. Config Server client calls remote endpoint.
6. Remote property sources are inserted into Environment.
7. Property precedence is resolved.
8. Auto-configuration begins.
9. Beans bind configuration properties.
10. Application context starts.
```

ASCII:

```text
JVM
 |
 v
SpringApplication
 |
 v
Environment preparation
 |
 v
Config Server import
 |
 v
Remote properties loaded
 |
 v
Property binding
 |
 v
Bean creation
 |
 v
Application ready
```

Why this matters:

```text
If Config Server config is not loaded before bean creation,
DataSource, Redis, Kafka, and Eureka may initialize with missing or wrong values.
```

This is why Config Server integration belongs in early config loading, not random service code.

---

## 24. Production Failure Stories

### Failure Story 1: Wrong profile points to wrong database

A service starts with `local` profile in production.

Result:

```text
Service tries localhost database.
Startup fails or writes to wrong place.
```

Root cause:

```text
SPRING_PROFILES_ACTIVE not set correctly.
```

Fix:

```text
Set profile through deployment config.
Add startup validation logs.
Fail fast if prod required values are missing.
```

Lesson:

```text
Profile selection is a production control plane decision.
```

---

### Failure Story 2: Config Server unavailable during autoscaling

Existing pods are healthy.
Traffic increases.
Kubernetes creates new pods.
New pods cannot reach Config Server.

Result:

```text
Autoscaling fails.
Old pods remain overloaded.
```

Root cause:

```text
Config Server is single instance or unavailable.
```

Fix:

```text
Run multiple Config Server instances.
Monitor health.
Use reliable backend.
```

Lesson:

```text
Config Server outage may break scaling even if current traffic still works.
```

---

### Failure Story 3: Secret committed to Git

A developer commits:

```yaml
jwt:
  signing-key: prod-secret
```

Result:

```text
Secret leaks in Git history.
Even deleting current file does not erase history.
```

Fix:

```text
Rotate secret.
Remove from history if possible.
Use secret manager or env var.
```

Lesson:

```text
Git is excellent for config history, dangerous for raw secrets.
```

---

### Failure Story 4: Config refresh changes unsafe value

A running service refreshes DB URL dynamically.

Result:

```text
Some beans point to old DB pool.
Some logic expects new DB.
Unexpected behavior.
```

Root cause:

```text
Dynamic refresh used for infrastructure wiring.
```

Fix:

```text
Restart pods for heavy infrastructure config.
Use refresh only for safe runtime knobs.
```

Lesson:

```text
Not every config should be hot-reloaded.
```

---

### Failure Story 5: Config drift across environments

Staging has timeout:

```text
500ms
```

Production has timeout:

```text
50ms
```

Nobody notices until production errors.

Root cause:

```text
Config changed manually without review or comparison.
```

Fix:

```text
Use Git PR review.
Document config ownership.
Compare important prod/staging values.
```

Lesson:

```text
Central config helps only if change process is disciplined.
```

---

## 25. Debugging Mindset

When Config Server issues happen, ask:

```text
1. Is Config Server running?
2. Can client reach Config Server URI?
3. Is the service name correct?
4. Is active profile correct?
5. Is the config file name correct?
6. Is Git branch/label correct?
7. Did the value load but get overridden?
8. Is the secret placeholder resolved?
9. Did the service restart or refresh after config change?
10. Is Docker/Kubernetes DNS correct?
```

Useful curl checks:

```bash
curl http://localhost:8888/url-service/local
curl http://localhost:8888/gateway-service/local
curl http://localhost:8888/application/local
```

Useful logs to search:

```text
Fetching config from server
Located environment
Could not locate PropertySource
Connection refused
No spring.config.import property has been defined
Could not resolve placeholder
```

Debug map:

```text
Connection refused
    -> wrong URI, server down, Docker localhost mistake

Empty config
    -> wrong app name or profile

Missing password
    -> env var/secret not injected

Old value still active
    -> service not restarted/refreshed

Wrong branch config
    -> label/default-label mismatch
```

Golden rule:

```text
Always debug app name + profile + label + URI first.
```

---

## 26. Common Mistakes

### Mistake 1: Using localhost inside Docker

Wrong:

```yaml
spring.config.import: configserver:http://localhost:8888
```

Correct inside Docker Compose:

```yaml
spring.config.import: configserver:http://config-server:8888
```

---

### Mistake 2: Wrong application name

Wrong:

```yaml
spring.application.name: urls-service
```

But file:

```text
url-service-prod.yml
```

Correct:

```yaml
spring.application.name: url-service
```

---

### Mistake 3: Putting secrets directly in Git

Wrong:

```yaml
password: real-prod-password
```

Better:

```yaml
password: ${DB_PASSWORD}
```

---

### Mistake 4: Expecting automatic refresh

Wrong assumption:

```text
I changed Git config, so all running services changed immediately.
```

Correct:

```text
Restart, refresh endpoint, or Spring Cloud Bus is needed.
```

---

### Mistake 5: Making all config shared

Wrong:

```text
Put every service setting in application.yml.
```

Correct:

```text
Common config in application.yml.
Service-specific config in service files.
```

---

### Mistake 6: Optional config in production

Risky:

```yaml
spring.config.import: optional:configserver:http://config-server:8888
```

Safer production:

```yaml
spring.config.import: configserver:http://config-server:8888
```

---

### Mistake 7: No config ownership

Wrong:

```text
Anyone changes production timeout without review.
```

Correct:

```text
Config repo uses pull requests, review, rollback, and ownership.
```

---

## 27. Interview-Ready Explanation

If interviewer asks:

```text
Why use Spring Cloud Config Server?
```

Strong answer:

```text
Spring Cloud Config Server externalizes configuration from services and provides a central, versioned source of truth for environment-specific properties. Each service identifies itself using spring.application.name and active profile, then fetches merged configuration from Config Server during startup. This lets us run the same artifact in local, staging, and production with different runtime settings like DB URLs, Redis hosts, Kafka brokers, gateway routes, timeouts, and feature flags. Common config can live in application.yml, while service-specific config lives in files like url-service-prod.yml. In production, I would back Config Server with Git for audit and rollback, run it highly available, fail fast if config is unavailable, and avoid storing raw secrets in Git by using environment variables, Kubernetes Secrets, Vault, or a cloud secret manager. Config changes usually require restart or controlled refresh, because not every property is safe to hot reload.
```

Why this answer is strong:

```text
1. Explains externalized config.
2. Mentions app name + profile.
3. Mentions same artifact across environments.
4. Differentiates shared and service-specific config.
5. Mentions Git audit/rollback.
6. Shows production HA awareness.
7. Shows secret management awareness.
8. Avoids naive hot-refresh assumption.
```

Senior one-liner:

```text
Config Server is the central brain that tells every service how to run in a specific environment without rebuilding the service artifact.
```

---

## 28. Senior Engineer Checklist

Before calling your Config Server setup production-shaped, verify:

```text
[ ] config-server has @EnableConfigServer
[ ] Config Server runs on port 8888 or documented internal port
[ ] native backend works locally
[ ] Git backend is planned for shared environments
[ ] config-repo has application.yml for common config
[ ] service-specific files exist
[ ] profile-specific files exist
[ ] spring.application.name matches config file name
[ ] clients use spring.config.import
[ ] Docker uses config-server hostname, not localhost
[ ] prod does not use optional config import unless intentional
[ ] secrets are not stored raw in Git
[ ] placeholders use environment variables or secret manager
[ ] Config Server health is monitored
[ ] multiple Config Server instances planned for HA
[ ] refresh strategy is documented
[ ] unsafe config requires restart, not refresh
[ ] config changes go through PR/review
[ ] rollback strategy exists
[ ] logs show app name/profile/label during startup
```

---

## 29. One-Page Cheat Sheet

```text
Core mental model:
Config Server = central configuration brain.

Main purpose:
Separate code from runtime configuration.

Client identity:
spring.application.name = service name
spring.profiles.active = environment
label = Git branch/tag

Endpoint:
/{application}/{profile}
/{application}/{profile}/{label}

Config files:
application.yml              common for all
application-prod.yml         common prod
url-service.yml              service common
url-service-prod.yml         service prod

Merge mental model:
common base
+ common profile
+ service base
+ service profile

Local config server:
spring.profiles.active=native
search-locations=file:./config-repo

Git config server:
spring.cloud.config.server.git.uri=...
default-label=main

Client config:
spring.config.import=configserver:http://config-server:8888

Local optional:
optional:configserver:http://localhost:8888

Production:
fail fast unless you intentionally allow fallback

Secrets:
Do not commit raw secrets.
Use ${ENV_VAR}, Kubernetes Secret, Vault, or cloud secret manager.

Refresh:
Git change alone does not update running service.
Restart, /actuator/refresh, or Spring Cloud Bus needed.

Most common bugs:
wrong app name
wrong profile
wrong URI
Docker localhost mistake
missing env secret
old config not refreshed
```

---

## 30. One Picture To Remember

```text
                    SPRING CLOUD CONFIG SERVER

                         Config Repo
          +---------------------------------------+
          | application.yml                       |
          | application-prod.yml                  |
          | url-service.yml                       |
          | url-service-prod.yml                  |
          | gateway-service-prod.yml              |
          +-------------------+-------------------+
                              |
                              v
                    +-------------------+
                    | Config Server     |
                    | central brain     |
                    +---------+---------+
                              |
              app/profile?    |    merged config
                              v
       +----------------------+----------------------+
       |                      |                      |
       v                      v                      v
+--------------+       +--------------+       +--------------+
| url-service  |       | gateway      |       | analytics    |
| name=url     |       | name=gateway |       | name=worker  |
| profile=prod |       | profile=prod |       | profile=prod |
+--------------+       +--------------+       +--------------+

FINAL MEMORY:

Code is packaged once.
Config is selected at runtime.
Config Server is the central brain.
Secrets still need secret management.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Config Server centralizes runtime configuration for multiple services and environments.
2. A service fetches config using spring.application.name, active profile, and optional Git label.
3. Common config belongs in application.yml; service-specific config belongs in service files.
4. Git gives audit and rollback, but raw secrets should not be stored in Git.
5. Config changes do not automatically update every running service unless restart, refresh, or bus is used.
```

After this chapter, MiniURLShortener has a production-shaped central configuration model:

```text
041_Eureka_Service_Discovery
042_Spring_Cloud_Gateway
043_OpenFeign_Client
044_Config_Server
```

Next possible chapter:

```text
045_Resilience4j_Circuit_Breaker.md
```
