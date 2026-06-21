# 003_SpringBoot_Project_Setup.md
# MiniURLShortener — Spring Boot Project Setup

> Core mental model: **A Spring Boot project setup is not just creating folders. It is building the first reliable application skeleton where requests can enter, configuration can load, dependencies can connect, tests can run, and future production features can grow without chaos.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. What We Are Building In This Chapter](#4-what-we-are-building-in-this-chapter)
- [5. Project Setup Big Picture](#5-project-setup-big-picture)
- [6. Technology Choices](#6-technology-choices)
- [7. Project Metadata](#7-project-metadata)
- [8. Dependencies](#8-dependencies)
- [9. Folder Structure Mental Model](#9-folder-structure-mental-model)
- [10. Package Design](#10-package-design)
- [11. Create Project Using Spring Initializr](#11-create-project-using-spring-initializr)
- [12. Maven pom.xml](#12-maven-pomxml)
- [13. Main Application Class](#13-main-application-class)
- [14. First Health API](#14-first-health-api)
- [15. Configuration Files](#15-configuration-files)
- [16. Local Postgres With Docker Compose](#16-local-postgres-with-docker-compose)
- [17. Database Connection Configuration](#17-database-connection-configuration)
- [18. Profiles Mental Model](#18-profiles-mental-model)
- [19. First Run Dry Run](#19-first-run-dry-run)
- [20. Request Flow Dry Run](#20-request-flow-dry-run)
- [21. Build And Test Commands](#21-build-and-test-commands)
- [22. Git Ignore And Repository Hygiene](#22-git-ignore-and-repository-hygiene)
- [23. Production-Ready Setup Mindset](#23-production-ready-setup-mindset)
- [24. Common Setup Mistakes](#24-common-setup-mistakes)
- [25. Debugging Setup Failures](#25-debugging-setup-failures)
- [26. Interview-Ready Explanation](#26-interview-ready-explanation)
- [27. Senior Engineer Checklist](#27-senior-engineer-checklist)
- [28. One-Page Cheat Sheet](#28-one-page-cheat-sheet)
- [29. One Picture To Remember](#29-one-picture-to-remember)

---

## 1. Why This Exists

Before building URL creation, redirect logic, Redis caching, Kafka analytics, security, Kubernetes, or AWS deployment, we need a clean Spring Boot foundation.

A weak project setup creates pain later:

```text
bad package structure
unclear dependency boundaries
hardcoded configuration
no profile separation
no reproducible local database
no test setup
no actuator health visibility
messy naming
```

Then every future chapter becomes harder.

A strong setup gives you this:

```text
1. Application starts reliably.
2. Local database can run reproducibly.
3. Configuration is separated by environment.
4. Packages are understandable.
5. First endpoint proves request flow works.
6. Maven build verifies code quality.
7. Future modules can be added cleanly.
```

For MiniURLShortener, setup is not a boring first step.

It is the foundation for:

```text
Create Short URL API
Redirect API
Postgres schema
Redis cache
Kafka analytics
Security
Spring Cloud
Kubernetes
Observability
CI/CD
```

Mental model:

```text
If setup is messy, production design becomes messy.
If setup is clean, future complexity has a home.
```

---

## 2. The One Core Mental Model

A Spring Boot project setup is a:

```text
RUNNABLE APPLICATION SKELETON
```

It must answer five questions:

```text
1. Where does the application start?
2. Where do HTTP requests enter?
3. Where does business logic live later?
4. Where does configuration come from?
5. How do we prove it runs?
```

ASCII mental model:

```text
                 SPRING BOOT PROJECT SKELETON

+---------------------------------------------------------+
| pom.xml                                                 |
| Defines dependencies, Java version, build lifecycle      |
+---------------------------------------------------------+
                            |
                            v
+---------------------------------------------------------+
| MiniUrlShortenerApplication.java                         |
| Starts Spring Boot, scans components, boots app context  |
+---------------------------------------------------------+
                            |
                            v
+---------------------------------------------------------+
| Controller layer                                         |
| First HTTP endpoint proves request entry works           |
+---------------------------------------------------------+
                            |
                            v
+---------------------------------------------------------+
| application.yml                                          |
| Externalized configuration                               |
+---------------------------------------------------------+
                            |
                            v
+---------------------------------------------------------+
| Docker Compose Postgres                                  |
| Reproducible local infrastructure                        |
+---------------------------------------------------------+
```

One-line memory:

```text
Project setup means creating the smallest clean skeleton that can boot, receive a request, read config, connect to infrastructure, and be extended safely.
```

---

## 3. Problem Statement

Create the starting Spring Boot project for MiniURLShortener.

The project should support:

```text
Java 17+
Spring Boot
Maven
Web API
Validation
PostgreSQL connection
JPA readiness
Actuator readiness
Test readiness
Docker Compose for local Postgres
Clean package structure
Environment profiles
```

The goal is not to implement the full URL shortener yet.

The goal is to prepare a professional backend base where future chapters can plug in cleanly.

What this chapter must produce:

```text
1. Project skeleton
2. pom.xml
3. main application class
4. basic health/check endpoint
5. application.yml
6. application-local.yml
7. docker-compose.yml for Postgres
8. clean package structure
9. run commands
10. debugging checklist
```

---

## 4. What We Are Building In This Chapter

We will create this project:

```text
mini-url-shortener
```

Final folder shape:

```text
mini-url-shortener
│
├── pom.xml
├── docker-compose.yml
├── README.md
├── .gitignore
│
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── miniurl
│   │   │           └── shortener
│   │   │               ├── MiniUrlShortenerApplication.java
│   │   │               ├── health
│   │   │               │   └── HealthCheckController.java
│   │   │               ├── url
│   │   │               │   ├── controller
│   │   │               │   ├── service
│   │   │               │   ├── repository
│   │   │               │   ├── entity
│   │   │               │   └── dto
│   │   │               ├── common
│   │   │               │   ├── error
│   │   │               │   ├── config
│   │   │               │   └── validation
│   │   │               └── analytics
│   │   │                   └── placeholder
│   │   │
│   │   └── resources
│   │       ├── application.yml
│   │       └── application-local.yml
│   │
│   └── test
│       └── java
│           └── com
│               └── miniurl
│                   └── shortener
│                       └── MiniUrlShortenerApplicationTests.java
```

Important:

```text
Some folders may be empty initially.
They show future architecture direction.
```

But avoid overengineering too much.

For this project, it is acceptable because this is a learning roadmap. Each future chapter has a clear destination.

---

## 5. Project Setup Big Picture

The setup has three layers:

```text
1. Application layer
2. Configuration layer
3. Local infrastructure layer
```

ASCII:

```text
+---------------------------------------------------+
|                  Developer Machine                |
|                                                   |
|  +---------------------------------------------+  |
|  | Spring Boot App                             |  |
|  |                                             |  |
|  | Controller -> Service -> Repository later   |  |
|  +---------------------------------------------+  |
|                     |                             |
|                     | JDBC connection             |
|                     v                             |
|  +---------------------------------------------+  |
|  | PostgreSQL Container                        |  |
|  | docker-compose.yml                          |  |
|  +---------------------------------------------+  |
|                                                   |
|  +---------------------------------------------+  |
|  | application-local.yml                       |  |
|  | local config values                         |  |
|  +---------------------------------------------+  |
+---------------------------------------------------+
```

Request flow after setup:

```text
Browser / curl
      |
      v
GET /health/ping
      |
      v
Spring Boot Controller
      |
      v
JSON response
```

Later request flow:

```text
POST /api/v1/urls
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
Postgres
```

Setup gives us the road, then future chapters put traffic on it.

---

## 6. Technology Choices

For MiniURLShortener:

```text
Language: Java 17+
Framework: Spring Boot
Build: Maven
Database: PostgreSQL
Persistence: Spring Data JPA initially
Validation: Jakarta Bean Validation
Local Infra: Docker Compose
Observability Base: Spring Boot Actuator
Testing: JUnit 5 + Spring Boot Test
```

Why Java 17?

```text
1. Stable LTS version.
2. Widely used in enterprise/product backend.
3. Supported well by Spring Boot.
4. Good for interviews and production projects.
```

Why Maven?

```text
1. Common in Java backend companies.
2. Simple lifecycle.
3. Easy for CI/CD.
4. Good dependency visibility.
```

Why Postgres?

```text
1. Reliable relational source of truth.
2. Strong indexing.
3. Unique constraints.
4. ACID transactions.
5. Excellent for first production-grade version.
```

Why Actuator?

```text
1. Gives health endpoint.
2. Helps Kubernetes readiness/liveness later.
3. Supports metrics later.
4. Production systems need visibility.
```

Why Docker Compose?

```text
1. Reproducible local database.
2. Avoid manual installation issues.
3. Same command works on many machines.
4. Easy to extend with Redis and Kafka later.
```

---

## 7. Project Metadata

Use this metadata:

```text
Group: com.miniurl
Artifact: mini-url-shortener
Name: mini-url-shortener
Package: com.miniurl.shortener
Java: 17
Build: Maven
Packaging: Jar
```

Why package naming matters:

```text
Spring Boot scans components from the package of the main application class downward.
```

If main class is:

```text
com.miniurl.shortener.MiniUrlShortenerApplication
```

Then Spring scans:

```text
com.miniurl.shortener.*
```

Good:

```text
com.miniurl.shortener.url.controller
com.miniurl.shortener.url.service
com.miniurl.shortener.common.error
```

Bad:

```text
com.other.package.UrlController
```

Why bad?

```text
Spring may not detect it automatically.
```

Mental model:

```text
Put all application code under the root package.
```

ASCII:

```text
com.miniurl.shortener
        |
        +-- url
        +-- common
        +-- analytics
        +-- security later
        +-- config
```

---

## 8. Dependencies

Initial dependencies:

```text
spring-boot-starter-web
spring-boot-starter-validation
spring-boot-starter-data-jpa
spring-boot-starter-actuator
postgresql
lombok optional
spring-boot-starter-test
```

Dependency purpose:

```text
web:
    Build REST APIs using Spring MVC.

validation:
    Validate request DTOs using annotations like @NotBlank and @Size.

data-jpa:
    Repository abstraction and entity management.

actuator:
    Health, metrics, readiness endpoints later.

postgresql:
    JDBC driver to connect to Postgres.

test:
    JUnit, Spring test utilities, MockMvc later.

lombok:
    Reduces boilerplate, optional. Use carefully.
```

Important learning point:

```text
Do not add Redis, Kafka, Security, Cloud, Kubernetes dependencies now.
```

Why?

```text
Each dependency increases mental load.
Add them when the chapter needs them.
```

Good project setup is not “add everything”.

Good setup is:

```text
Add only what the current phase needs.
Leave clean extension points for later.
```

---

## 9. Folder Structure Mental Model

There are two common package organization styles:

```text
1. Layer-based
2. Feature-based
```

Layer-based:

```text
controller
service
repository
entity
dto
```

Feature-based:

```text
url
  controller
  service
  repository
  entity
  dto

analytics
  producer
  consumer
  dto

common
  error
  config
  validation
```

For MiniURLShortener, prefer feature-based with internal layers.

Why?

Because as the project grows, features remain grouped.

Good:

```text
url/controller
url/service
url/repository
url/entity
url/dto
```

Better mental navigation:

```text
URL feature files stay together.
Analytics files stay together.
Common reusable files stay in common.
```

ASCII:

```text
com.miniurl.shortener
│
├── url
│   ├── controller
│   ├── service
│   ├── repository
│   ├── entity
│   └── dto
│
├── analytics
│   └── later Kafka/click tracking
│
└── common
    ├── error
    ├── config
    └── validation
```

Rule:

```text
Group by business feature first.
Use layers inside the feature.
```

This is close to how production teams keep medium-large services maintainable.

---

## 10. Package Design

Initial packages:

```text
com.miniurl.shortener
```

Inside:

```text
health
url
common
analytics
```

### health

Purpose:

```text
Temporary simple health/ping endpoint.
```

Later actuator will provide real health endpoints, but this endpoint helps us verify controller routing quickly.

### url

Purpose:

```text
Core URL shortener feature.
```

Future classes:

```text
UrlController
UrlService
UrlRepository
ShortUrlEntity
CreateShortUrlRequest
ShortUrlResponse
```

### common

Purpose:

```text
Reusable infrastructure code.
```

Future classes:

```text
GlobalExceptionHandler
ApiErrorResponse
ClockConfig
ValidationUtils
```

### analytics

Purpose:

```text
Click analytics later.
```

Future classes:

```text
ClickEvent
ClickEventProducer
ClickAnalyticsWorker
```

Do not implement all now.

Setup should make the future visible but keep the current code small.

---

## 11. Create Project Using Spring Initializr

You can create from:

```text
https://start.spring.io
```

Choose:

```text
Project: Maven
Language: Java
Spring Boot: latest stable 3.x
Group: com.miniurl
Artifact: mini-url-shortener
Name: mini-url-shortener
Package name: com.miniurl.shortener
Packaging: Jar
Java: 17
```

Add dependencies:

```text
Spring Web
Spring Data JPA
Validation
Actuator
PostgreSQL Driver
Spring Boot Test
Lombok optional
```

After download:

```bash
unzip mini-url-shortener.zip
cd mini-url-shortener
```

Run:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Expected first result may fail if datasource is configured but Postgres is not running.

That is normal once JPA/Postgres config is added.

The setup order:

```text
1. Generate project.
2. Add basic health controller.
3. Run without DB config first if needed.
4. Add Docker Compose Postgres.
5. Add datasource config.
6. Run with local profile.
```

---

## 12. Maven pom.xml

Use a clean `pom.xml`.

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.5</version>
        <relativePath/>
    </parent>

    <groupId>com.miniurl</groupId>
    <artifactId>mini-url-shortener</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>mini-url-shortener</name>
    <description>Production-grade URL Shortener learning project</description>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>

        <!-- Build REST APIs using Spring MVC -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Validate request DTOs using Jakarta Bean Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- JPA/Hibernate + Spring Data repositories -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- Production health, metrics, readiness/liveness later -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- PostgreSQL JDBC driver -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Optional: reduce boilerplate; can be removed if you prefer explicit Java -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Unit and integration test support -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

    </dependencies>

    <build>
        <plugins>

            <!-- Creates executable Spring Boot jar -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>

        </plugins>
    </build>

</project>
```

Important:

```text
Version may be adjusted to your current Spring Boot version.
Keep Java 17 or higher.
```

For learning consistency, do not chase every latest version during the project. Stable and reproducible is better.

---

## 13. Main Application Class

Path:

```text
src/main/java/com/miniurl/shortener/MiniUrlShortenerApplication.java
```

Code:

```java
package com.miniurl.shortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MiniUrlShortenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniUrlShortenerApplication.class, args);
    }
}
```

What `@SpringBootApplication` means:

```text
@SpringBootApplication =
    @SpringBootConfiguration
  + @EnableAutoConfiguration
  + @ComponentScan
```

Mental model:

```text
@SpringBootApplication is the main switch.
It starts auto-configuration and scans your components.
```

ASCII:

```text
main()
  |
  v
SpringApplication.run()
  |
  v
Create ApplicationContext
  |
  v
Scan com.miniurl.shortener.*
  |
  v
Create beans
  |
  v
Start embedded Tomcat
  |
  v
App listens on port 8080
```

Important:

```text
Keep this class at the root package.
```

Good:

```text
com.miniurl.shortener.MiniUrlShortenerApplication
```

Then these are scanned:

```text
com.miniurl.shortener.health
com.miniurl.shortener.url
com.miniurl.shortener.common
```

Bad:

```text
com.miniurl.app.MiniUrlShortenerApplication
com.miniurl.shortener.url.UrlController
```

They may not be under the same scan tree.

---

## 14. First Health API

Create:

```text
src/main/java/com/miniurl/shortener/health/HealthCheckController.java
```

Code:

```java
package com.miniurl.shortener.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class HealthCheckController {

    @GetMapping("/health/ping")
    public Map<String, Object> ping() {
        return Map.of(
                "status", "UP",
                "service", "mini-url-shortener",
                "timestamp", Instant.now().toString()
        );
    }
}
```

Run app:

```bash
./mvnw spring-boot:run
```

Call:

```bash
curl http://localhost:8080/health/ping
```

Expected response:

```json
{
  "status": "UP",
  "service": "mini-url-shortener",
  "timestamp": "2026-06-21T10:00:00Z"
}
```

Why create this endpoint?

```text
1. Proves Spring Boot starts.
2. Proves controller scanning works.
3. Proves embedded server works.
4. Gives quick smoke test before real APIs.
```

Later:

```text
Actuator /actuator/health becomes the production health endpoint.
```

This endpoint is a learning smoke test.

---

## 15. Configuration Files

Create:

```text
src/main/resources/application.yml
```

Use common config:

```yaml
spring:
  application:
    name: mini-url-shortener

server:
  port: 8080

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

Create:

```text
src/main/resources/application-local.yml
```

Local profile config:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/miniurl
    username: miniurl
    password: miniurl

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true

logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.orm.jdbc.bind: TRACE
```

Important about `ddl-auto`:

```text
validate:
    Hibernate checks schema exists but does not create/update it.

update:
    Hibernate tries to alter schema automatically.

create/drop:
    Recreates schema, dangerous for real data.
```

For early local learning, `update` is convenient.
For production mindset, prefer migrations later with Flyway or Liquibase.

For now:

```text
Use validate once schema chapter begins.
Use update only temporarily if you want fast local experimentation.
```

Since chapter 004 is Postgres schema design, keep schema control explicit.

---

## 16. Local Postgres With Docker Compose

Create:

```text
docker-compose.yml
```

Content:

```yaml
services:
  postgres:
    image: postgres:16
    container_name: miniurl-postgres
    environment:
      POSTGRES_DB: miniurl
      POSTGRES_USER: miniurl
      POSTGRES_PASSWORD: miniurl
    ports:
      - "5432:5432"
    volumes:
      - miniurl_postgres_data:/var/lib/postgresql/data

volumes:
  miniurl_postgres_data:
```

Start Postgres:

```bash
docker compose up -d
```

Check containers:

```bash
docker ps
```

Expected:

```text
miniurl-postgres running on 0.0.0.0:5432
```

Connect using psql if installed:

```bash
psql -h localhost -p 5432 -U miniurl -d miniurl
```

Password:

```text
miniurl
```

Stop:

```bash
docker compose down
```

Stop and delete volume:

```bash
docker compose down -v
```

Warning:

```text
down -v deletes database data.
Use carefully.
```

Mental model:

```text
Docker Compose gives your app a local production-like dependency.
```

---

## 17. Database Connection Configuration

Run app with local profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Alternative:

```bash
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="local"
.\mvnw.cmd spring-boot:run
```

What happens internally:

```text
1. Spring Boot loads application.yml.
2. local profile is active.
3. Spring Boot also loads application-local.yml.
4. DataSource auto-configuration sees PostgreSQL URL.
5. HikariCP connection pool is created.
6. Hibernate tries to validate schema.
```

Potential issue:

```text
If ddl-auto=validate and no entity/schema exists yet, app may still start if no entities exist.
Later when entities are added, missing tables fail startup.
```

That is good.

It catches schema mismatch early.

Connection diagram:

```text
Spring Boot App
      |
      | jdbc:postgresql://localhost:5432/miniurl
      v
Postgres Docker Container
      |
      v
Database: miniurl
User: miniurl
Password: miniurl
```

---

## 18. Profiles Mental Model

Profiles separate environment-specific configuration.

Common environments:

```text
local
test
dev
staging
prod
```

Base file:

```text
application.yml
```

Profile file:

```text
application-local.yml
application-prod.yml
```

Mental model:

```text
application.yml = common defaults
application-local.yml = local overrides
application-prod.yml = production overrides
```

ASCII:

```text
              active profile = local

+---------------------+
| application.yml     |
| common config       |
+---------------------+
          |
          v
+---------------------------+
| application-local.yml     |
| local DB/password/logging |
+---------------------------+
          |
          v
+---------------------+
| Final Environment   |
+---------------------+
```

Example:

```yaml
# application.yml
server:
  port: 8080
```

```yaml
# application-local.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/miniurl
```

Why this matters:

```text
Never hardcode production credentials in code.
Never make local and production config the same file.
```

Later Kubernetes uses environment variables and secrets.

---

## 19. First Run Dry Run

### Step 1: Start Postgres

```bash
docker compose up -d
```

Expected:

```text
Postgres container running
```

### Step 2: Run app

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Expected logs:

```text
Tomcat started on port 8080
Started MiniUrlShortenerApplication
```

### Step 3: Call ping endpoint

```bash
curl http://localhost:8080/health/ping
```

Expected:

```json
{
  "status": "UP",
  "service": "mini-url-shortener",
  "timestamp": "..."
}
```

### Step 4: Call actuator health

```bash
curl http://localhost:8080/actuator/health
```

Expected:

```json
{
  "status": "UP"
}
```

If DB health is included, you may see components later.

### Dry run mental trace

```text
Developer runs command
        |
        v
Maven compiles code
        |
        v
Spring Boot starts
        |
        v
ApplicationContext created
        |
        v
Controller bean registered
        |
        v
Tomcat listens on 8080
        |
        v
curl sends request
        |
        v
Controller method executes
        |
        v
JSON response returned
```

---

## 20. Request Flow Dry Run

Request:

```http
GET /health/ping
```

Internal flow:

```text
curl/browser
    |
    v
embedded Tomcat
    |
    v
DispatcherServlet
    |
    v
HandlerMapping finds HealthCheckController.ping()
    |
    v
Controller returns Map
    |
    v
Jackson converts Map to JSON
    |
    v
HTTP 200 response
```

ASCII sequence:

```text
+--------+      +--------+      +-------------------+      +------------+
| curl   |      | Tomcat |      | DispatcherServlet |      | Controller |
+--------+      +--------+      +-------------------+      +------------+
    |               |                    |                       |
    | GET /health   |                    |                       |
    |-------------->|                    |                       |
    |               | dispatch request   |                       |
    |               |------------------->|                       |
    |               |                    | find handler          |
    |               |                    |---------------------->|
    |               |                    | Map response          |
    |               |                    |<----------------------|
    |               | JSON 200           |                       |
    |<--------------|                    |                       |
```

This small request proves a lot:

```text
1. App booted.
2. Server started.
3. Controller scanned.
4. Route mapped.
5. JSON serialization works.
```

Later, the same path handles:

```text
POST /api/v1/urls
GET /{shortCode}
```

---

## 21. Build And Test Commands

Compile:

```bash
./mvnw compile
```

Run tests:

```bash
./mvnw test
```

Package jar:

```bash
./mvnw clean package
```

Run jar:

```bash
java -jar target/mini-url-shortener-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

Skip tests temporarily:

```bash
./mvnw clean package -DskipTests
```

Use `-DskipTests` rarely.

Production mindset:

```text
If tests are failing, the build should fail.
```

Basic generated test:

```java
package com.miniurl.shortener;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MiniUrlShortenerApplicationTests {

    @Test
    void contextLoads() {
    }
}
```

What this test proves:

```text
Spring ApplicationContext can start.
```

It is simple but valuable. It catches broken configuration early.

---

## 22. Git Ignore And Repository Hygiene

Create `.gitignore`:

```gitignore
target/
*.class

# IDE
.idea/
*.iml
.vscode/

# OS
.DS_Store
Thumbs.db

# Logs
*.log

# Environment files
.env
.env.*

# Maven wrapper is usually committed
!.mvn/wrapper/maven-wrapper.jar
```

Should commit:

```text
pom.xml
src/**
docker-compose.yml
README.md
mvnw
mvnw.cmd
.mvn/wrapper/**
```

Should not commit:

```text
target/
IDE files
logs
secrets
local .env files
database volumes
```

Repository hygiene matters because this is a GitHub portfolio project.

A recruiter or interviewer may inspect:

```text
1. README clarity
2. commit structure
3. project organization
4. build reproducibility
5. absence of secrets
```

Bad signal:

```text
passwords committed
target folder committed
messy package naming
no run instructions
```

Good signal:

```text
Clean setup, clear commands, reproducible local environment.
```

---

## 23. Production-Ready Setup Mindset

Even in chapter 003, think like production.

### Principle 1: Externalize config

Bad:

```java
String dbUrl = "jdbc:postgresql://localhost:5432/miniurl";
```

Good:

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
```

Local can use direct values, production should use environment variables/secrets.

### Principle 2: Keep startup observable

At minimum:

```text
/actuator/health
/health/ping for smoke test
startup logs
active profile visible in logs
```

### Principle 3: Keep dependency count controlled

Do not add:

```text
Redis
Kafka
Spring Security
Spring Cloud
AWS SDK
Kubernetes clients
```

until needed.

### Principle 4: Build with test command

Your project should always support:

```bash
./mvnw clean test
```

### Principle 5: Design for future extension

Current setup should allow:

```text
url feature
analytics feature
security feature
common error handling
infrastructure config
```

without restructuring everything later.

---

## 24. Common Setup Mistakes

### Mistake 1: Main class in wrong package

Bad:

```text
com.miniurl.app.Application
com.miniurl.shortener.url.UrlController
```

Controller may not be scanned.

Fix:

```text
Place main class at root:
com.miniurl.shortener
```

---

### Mistake 2: Adding too many dependencies early

Bad:

```text
Add Redis, Kafka, Security, Cloud, AWS on day one.
```

Result:

```text
Startup errors
Mental overload
Unused configs
Hard debugging
```

Fix:

```text
Add dependencies only when implementing that chapter.
```

---

### Mistake 3: Hardcoding credentials in Java

Bad:

```java
private String password = "prod-password";
```

Fix:

```text
Use application.yml for local.
Use env variables/secrets for production.
```

---

### Mistake 4: No local database reproducibility

Bad:

```text
Works only on my machine because Postgres manually installed.
```

Fix:

```text
Use docker-compose.yml.
```

---

### Mistake 5: Ignoring failing contextLoads test

Bad:

```text
Delete the test because it fails.
```

Fix:

```text
Understand why Spring context fails.
```

---

### Mistake 6: Using create-drop casually

Bad:

```yaml
spring.jpa.hibernate.ddl-auto: create-drop
```

Problem:

```text
Data disappears on restart.
Dangerous mental habit.
```

Fix:

```text
Use migrations later.
Use validate/update only consciously in local.
```

---

## 25. Debugging Setup Failures

### Failure 1: Port already in use

Error:

```text
Port 8080 was already in use
```

Check:

```bash
lsof -i :8080
```

Windows:

```powershell
netstat -ano | findstr :8080
```

Fix:

```yaml
server:
  port: 8081
```

or stop the other process.

---

### Failure 2: Cannot connect to Postgres

Error:

```text
Connection refused localhost:5432
```

Checklist:

```text
[ ] Is Docker running?
[ ] Did you run docker compose up -d?
[ ] Is container healthy?
[ ] Is port 5432 mapped?
[ ] Are username/password correct?
[ ] Is DB name correct?
```

Commands:

```bash
docker ps
docker logs miniurl-postgres
```

---

### Failure 3: Authentication failed

Error:

```text
password authentication failed for user "miniurl"
```

Cause:

```text
Existing Docker volume may have old credentials.
```

Fix for local only:

```bash
docker compose down -v
docker compose up -d
```

Warning:

```text
This deletes local DB data.
```

---

### Failure 4: Controller returns 404

Possible causes:

```text
Wrong URL
Controller package not scanned
@GetMapping path mismatch
App not restarted
```

Check:

```text
Is HealthCheckController under com.miniurl.shortener?
Is annotation @RestController present?
Is path /health/ping exactly?
```

---

### Failure 5: Maven build fails

Try:

```bash
./mvnw clean test
```

Common causes:

```text
Wrong Java version
Internet/dependency download issue
Bad pom.xml
Lombok IDE plugin missing
```

Check Java:

```bash
java -version
```

Expected:

```text
17 or higher
```

---

## 26. Interview-Ready Explanation

If an interviewer asks:

```text
How would you start implementing the URL shortener backend?
```

Strong answer:

```text
I would first create a clean Spring Boot application skeleton instead of jumping
directly into business logic. The root package would contain the main application
class so component scanning works correctly. I would organize code by feature, for
example url, analytics, and common, with controller/service/repository layers inside
the url feature. I would add only the dependencies needed for phase one: web,
validation, data-jpa, PostgreSQL, actuator, and tests. I would externalize
configuration through application.yml and profiles, run PostgreSQL locally through
Docker Compose, and verify the app using a simple health endpoint plus actuator
health. This gives a reproducible base that can later grow into Redis caching,
Kafka analytics, security, Kubernetes deployment, and observability without major
restructuring.
```

Why this answer is strong:

```text
1. Shows you do not randomly start coding.
2. Mentions component scanning.
3. Mentions package organization.
4. Mentions minimal dependencies.
5. Mentions externalized config.
6. Mentions Docker Compose.
7. Mentions actuator health.
8. Shows future scalability path.
```

Senior one-liner:

```text
I set up the smallest production-shaped Spring Boot skeleton that can boot, receive HTTP traffic, connect to local infrastructure, and evolve without restructuring.
```

---

## 27. Senior Engineer Checklist

Before moving to chapter 004, confirm:

```text
[ ] Project generated with Maven
[ ] Java 17+ configured
[ ] Root package is com.miniurl.shortener
[ ] Main application class is at root package
[ ] Spring Web dependency added
[ ] Validation dependency added
[ ] Spring Data JPA dependency added
[ ] PostgreSQL driver added
[ ] Actuator dependency added
[ ] Test dependency added
[ ] HealthCheckController works
[ ] application.yml exists
[ ] application-local.yml exists
[ ] Docker Compose Postgres starts
[ ] App can run with local profile
[ ] /health/ping returns UP
[ ] /actuator/health returns UP
[ ] Maven test command works
[ ] .gitignore is clean
[ ] No secrets committed
[ ] Package structure is ready for url feature
```

If all are checked, your foundation is ready.

---

## 28. One-Page Cheat Sheet

```text
Project:
mini-url-shortener

Root package:
com.miniurl.shortener

Main class:
MiniUrlShortenerApplication

Core setup dependencies:
spring-boot-starter-web
spring-boot-starter-validation
spring-boot-starter-data-jpa
spring-boot-starter-actuator
postgresql
spring-boot-starter-test

Local database:
PostgreSQL via Docker Compose

Local DB:
database = miniurl
username = miniurl
password = miniurl
port = 5432

Run Postgres:
docker compose up -d

Run app:
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

Ping:
curl http://localhost:8080/health/ping

Actuator:
curl http://localhost:8080/actuator/health

Build:
./mvnw clean package

Test:
./mvnw test

Package style:
feature-based with internal layers

Main packages:
health
url
common
analytics

Most important setup rule:
Keep main class at root package so component scanning works.

Most important production rule:
Externalize config and keep local infra reproducible.

Most important learning rule:
Do not add Redis/Kafka/Security before their chapters.
```

---

## 29. One Picture To Remember

```text
                     MINI URL SHORTENER PROJECT SETUP

                          Developer Machine
                                 |
                                 v
                    +--------------------------+
                    | ./mvnw spring-boot:run   |
                    +--------------------------+
                                 |
                                 v
                    +--------------------------+
                    | MiniUrlShortenerApp      |
                    | @SpringBootApplication   |
                    +--------------------------+
                                 |
                  component scan com.miniurl.shortener.*
                                 |
        +------------------------+------------------------+
        |                        |                        |
        v                        v                        v
+---------------+        +---------------+        +---------------+
| health        |        | url           |        | common        |
| ping endpoint |        | future APIs   |        | errors/config |
+---------------+        +---------------+        +---------------+
        |
        v
+----------------------+
| GET /health/ping     |
| returns status UP    |
+----------------------+

Configuration:

+----------------------+       +--------------------------+
| application.yml      | ----> | application-local.yml    |
| common config        |       | local Postgres config    |
+----------------------+       +--------------------------+

Infrastructure:

+----------------------+       JDBC       +----------------------+
| Spring Boot App      | --------------> | Postgres Docker      |
| localhost:8080       |                 | localhost:5432       |
+----------------------+                 +----------------------+

Final memory:

A good setup is the smallest production-shaped skeleton:
it boots, receives requests, reads config, connects to infrastructure,
runs tests, and gives every future feature a clean place to live.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Project setup is a runnable application skeleton, not just folder creation.
2. Keep the main Spring Boot class at the root package for component scanning.
3. Add only the dependencies needed for the current phase.
4. Use profiles and external configuration instead of hardcoding values.
5. Use Docker Compose so local infrastructure is reproducible.
```

After this chapter, the next natural step is:

```text
004_Postgres_Schema_Design.md
```

Because now the application skeleton exists, and we can design the durable source of truth for:

```text
shortCode -> longUrl
```
