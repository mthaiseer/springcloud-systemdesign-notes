# Spring Boot From Scratch Quick Reference

_A practical Markdown starter guide based on the book **Continuous Delivery for Java Apps** by Jorge Acetozi._

This guide distills the book into one opinionated path you can start from scratch and build on. The book’s running example is a **Spring Boot “Notepad” application** backed by **MySQL**, database migrations with **Flyway**, builds with **Maven**, containerization with **Docker**, automation with **Jenkins**, deployment to **Kubernetes**, and gradual rollout with **canary release**. fileciteturn0file0 fileciteturn1file0 fileciteturn3file3

---

## 1. What this book is really teaching

The book is not just “how to write a Spring Boot app.” It teaches how to keep the app **always deployable** by combining:

- small changes merged frequently
- automated tests
- Maven build and release flow
- Flyway migrations
- Docker images
- Jenkins pipelines
- Kubernetes environments
- safe rollout patterns like canary deployment

A core message in the book is that **continuous delivery depends on continuous integration**, and continuous integration depends on **small tasks** and **automated tests**. fileciteturn0file0

---

## 2. The shortest path to success

If you want to be productive quickly, build in this order:

1. Create a simple Spring Boot CRUD app.
2. Add persistence with Spring Data JPA.
3. Add Flyway so the database is reproducible.
4. Add unit and integration tests.
5. Package with Maven.
6. Containerize with Docker.
7. Add a Jenkins pipeline.
8. Deploy to Kubernetes.
9. Promote through testing, staging, and production.
10. Use canary deployment for safer production rollout.

That flow mirrors the structure of the book and the Notepad example. fileciteturn0file0

---

## 3. Starter architecture to copy

Use this basic application structure:

```text
notes-app/
├── pom.xml
├── Dockerfile
├── Jenkinsfile
├── k8s/
│   ├── deployment.yaml
│   └── service.yaml
├── src/
│   ├── main/
│   │   ├── java/com/example/notes/
│   │   │   ├── NotesApplication.java
│   │   │   ├── note/
│   │   │   │   ├── Note.java
│   │   │   │   ├── NoteRepository.java
│   │   │   │   ├── NoteService.java
│   │   │   │   ├── NoteController.java
│   │   │   │   └── NoteDto.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/
│   │           └── V1__init.sql
│   └── test/
│       └── java/com/example/notes/
│           ├── NoteServiceTest.java
│           └── NoteControllerTest.java
```

---

## 4. Recommended modernized stack

The book’s sample uses Spring Boot 1.5.x and Java 8 in its appendix and examples. For a fresh project today, use the same ideas but a modern baseline:

- Java 21
- Spring Boot 3.x
- Maven
- Spring Web
- Spring Data JPA
- Flyway
- MySQL for real environments
- H2 for lightweight local tests if you want speed
- JUnit 5 + Spring Boot Test

This keeps the design faithful to the book while avoiding starting from an older framework baseline.

---

## 5. Complete working Spring Boot example

### 5.1 `pom.xml`

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

    <groupId>com.example</groupId>
    <artifactId>notes-app</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>notes-app</name>
    <description>Spring Boot starter app inspired by the Notepad example</description>

    <properties>
        <java.version>21</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>

        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### 5.2 `src/main/java/com/example/notes/NotesApplication.java`

```java
package com.example.notes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotesApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotesApplication.class, args);
    }
}
```

### 5.3 `src/main/java/com/example/notes/note/Note.java`

```java
package com.example.notes.note;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "note")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    public Note() {
    }

    public Note(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
```

### 5.4 `src/main/java/com/example/notes/note/NoteDto.java`

```java
package com.example.notes.note;

import jakarta.validation.constraints.NotBlank;

public class NoteDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
```

### 5.5 `src/main/java/com/example/notes/note/NoteRepository.java`

```java
package com.example.notes.note;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
}
```

### 5.6 `src/main/java/com/example/notes/note/NoteService.java`

```java
package com.example.notes.note;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<Note> findAll() {
        return noteRepository.findAll();
    }

    public Optional<Note> findById(Long id) {
        return noteRepository.findById(id);
    }

    public Note create(NoteDto dto) {
        Note note = new Note();
        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        return noteRepository.save(note);
    }

    public Note update(Long id, NoteDto dto) {
        Note note = noteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Note not found: " + id));

        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        return noteRepository.save(note);
    }

    public void delete(Long id) {
        noteRepository.deleteById(id);
    }
}
```

### 5.7 `src/main/java/com/example/notes/note/NoteController.java`

```java
package com.example.notes.note;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public List<Note> findAll() {
        return noteService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> findById(@PathVariable Long id) {
        return noteService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Note> create(@Valid @RequestBody NoteDto dto) {
        Note created = noteService.create(dto);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.getId())
            .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> update(@PathVariable Long id, @Valid @RequestBody NoteDto dto) {
        return ResponseEntity.ok(noteService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

### 5.8 `src/main/resources/application.yml`

```yaml
spring:
  application:
    name: notes-app
  datasource:
    url: jdbc:mysql://localhost:3306/notes_app
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
  flyway:
    enabled: true

server:
  port: 8080
---
spring:
  config:
    activate:
      on-profile: test
  datasource:
    url: jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password:
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  flyway:
    enabled: true
```

### 5.9 `src/main/resources/db/migration/V1__init.sql`

```sql
CREATE TABLE note (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content VARCHAR(2000) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO note (title, content)
VALUES ('Welcome', 'Your Notes app is running.');
```

### 5.10 `src/test/java/com/example/notes/note/NoteServiceTest.java`

```java
package com.example.notes.note;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class NoteServiceTest {

    @Autowired
    private NoteService noteService;

    @Test
    void shouldCreateAndListNotes() {
        NoteDto dto = new NoteDto();
        dto.setTitle("First note");
        dto.setContent("Created in test");

        Note created = noteService.create(dto);
        List<Note> notes = noteService.findAll();

        assertThat(created.getId()).isNotNull();
        assertThat(notes).extracting(Note::getTitle).contains("First note");
    }
}
```

### 5.11 `src/test/java/com/example/notes/note/NoteControllerTest.java`

```java
package com.example.notes.note;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnSeedData() throws Exception {
        mockMvc.perform(get("/api/notes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Welcome"));
    }

    @Test
    void shouldCreateNote() throws Exception {
        mockMvc.perform(post("/api/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      \"title\": \"API Note\",
                      \"content\": \"Created via controller test\"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("API Note"));
    }
}
```

---

## 6. How to run it locally from scratch

### 6.1 Start MySQL with Docker

```bash
docker run --name notes-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=notes_app \
  -p 3306:3306 \
  -d mysql:8.4
```

### 6.2 Run the app

```bash
./mvnw spring-boot:run
```

Or if you do not use the Maven wrapper:

```bash
mvn spring-boot:run
```

### 6.3 Test the API

```bash
curl http://localhost:8080/api/notes
```

Create a note:

```bash
curl -X POST http://localhost:8080/api/notes \
  -H "Content-Type: application/json" \
  -d '{"title":"My first note","content":"Built from scratch"}'
```

### 6.4 Run tests

```bash
mvn clean test
```

---

## 7. Why Flyway matters so much in this book

The book emphasizes that reproducible environments are essential. In the Notepad app, Flyway runs automatically on startup and applies a migration like `V1__init.sql`, creating the `note` table and seeding initial data. That makes it easy to create a fresh database for tests, ephemeral environments, and deployments. fileciteturn3file3

**Practical rule:** never create tables manually in local-only ways. Put schema changes in versioned migrations.

---

## 8. Docker starter

### 8.1 `Dockerfile`

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/notes-app-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

### 8.2 Build and run

```bash
mvn clean package
docker build -t notes-app:local .
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/notes_app \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=root \
  notes-app:local
```

The book’s broader point is that Docker makes environments consistent and versionable through a `Dockerfile`, which fits the “infrastructure as code” mindset used throughout the pipeline. fileciteturn3file2

---

## 9. Maven mindset from the book

The Notepad flow in the book uses Maven to:

- compile
- run tests
- package the application as a jar
- publish artifacts to Artifactory

Its Jenkins pipelines run commands like `mvn clean deploy`, and the appendix includes a full `pom.xml` for the Notepad app. fileciteturn1file1 fileciteturn3file0

For a starter project, use these commands:

```bash
mvn clean test
mvn clean package
mvn clean verify
```

Use `deploy` only when you are publishing artifacts to a repository manager.

---

## 10. Jenkins starter pipeline

The book’s testing pipeline checks out code, runs Maven tests and deploy, builds a Docker image, pushes it, recreates a testing environment on Kubernetes, and then runs acceptance tests. fileciteturn0file0

Here is a clean starter `Jenkinsfile` you can use first:

```groovy
pipeline {
  agent any

  environment {
    IMAGE_NAME = 'notes-app'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Test') {
      steps {
        sh 'mvn clean test'
      }
    }

    stage('Package') {
      steps {
        sh 'mvn clean package -DskipTests'
      }
    }

    stage('Build Docker Image') {
      steps {
        sh 'docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} .'
      }
    }
  }
}
```

Then grow it toward the book’s full model:

- add artifact publication
- add acceptance tests
- add Kubernetes deploy
- add staging promotion
- add canary rollout

---

## 11. Kubernetes starter manifests

The book deploys the application to Kubernetes, wires environment-specific database settings via environment variables, and exposes the application through a Kubernetes Service. It also shows the app resolving MySQL through the service name, for example `mysql-service`, inside the cluster. fileciteturn1file4

### 11.1 `k8s/deployment.yaml`

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: notes-app
spec:
  replicas: 2
  selector:
    matchLabels:
      app: notes-app
  template:
    metadata:
      labels:
        app: notes-app
    spec:
      containers:
        - name: notes-app
          image: your-dockerhub-user/notes-app:latest
          ports:
            - containerPort: 8080
          env:
            - name: SPRING_DATASOURCE_URL
              value: jdbc:mysql://mysql-service:3306/notes_app
            - name: SPRING_DATASOURCE_USERNAME
              value: root
            - name: SPRING_DATASOURCE_PASSWORD
              value: root
```

### 11.2 `k8s/service.yaml`

```yaml
apiVersion: v1
kind: Service
metadata:
  name: notes-app-service
spec:
  selector:
    app: notes-app
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
  type: ClusterIP
```

Deploy:

```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

---

## 12. From simple app to real delivery pipeline

The book’s environments and pipeline flow roughly look like this:

- testing
- acceptance test
- continuous integration
- release
- staging
- performance test
- production
- canary

That is the full delivery model wrapped around the Spring Boot Notepad app. fileciteturn0file0

A practical promotion model is:

```text
commit -> unit/integration tests -> package jar -> build docker image
-> deploy to testing -> acceptance tests -> release artifact/image
-> deploy to staging -> smoke/performance checks -> canary deploy
-> full production rollout
```

---

## 13. What to keep exactly from the book

Keep these ideas unchanged:

1. **Always use automated tests.** The book is explicit: no automated tests means no real CI, and no real CI means no real CD. fileciteturn0file0
2. **Use Flyway for schema control.** Schema and seed data should be reproducible. fileciteturn3file3
3. **Package with Maven.** Build the jar in a standard, repeatable way. fileciteturn1file1
4. **Containerize the app.** The same image should move through environments. fileciteturn3file2
5. **Deploy with pipelines, not by hand.** Jenkins orchestrates the flow in the book. fileciteturn0file0
6. **Roll out safely.** Use canary when production risk matters. fileciteturn0file0

---

## 14. What I would simplify for your first project

Do **not** build the whole platform on day one.

Start with:

- Spring Boot app
- MySQL
- Flyway
- unit + controller tests
- Docker
- a small Jenkins pipeline

Add later:

- Artifactory
- acceptance tests with browser automation
- full Kubernetes promotion flow
- canary automation

That gets you a working app fast while still following the book’s principles.

---

## 15. Minimum commands cheat sheet

```bash
# create project
mvn archetype:generate

# run app
mvn spring-boot:run

# test
mvn clean test

# package
mvn clean package

# build docker image
docker build -t notes-app:local .

# run mysql
docker run --name notes-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=notes_app -p 3306:3306 -d mysql:8.4

# deploy to kubernetes
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

---

## 16. Final takeaway

The fastest way to benefit from this book is to treat it as a layered path:

- first, build a small but correct Spring Boot CRUD app
- second, make it reproducible with Flyway and tests
- third, package and containerize it
- fourth, automate it with Jenkins
- fifth, run it in Kubernetes
- sixth, deploy safely with canary strategy

That is the real lesson of the book: **a deployable Spring Boot app is not just code — it is code plus tests plus packaging plus environment automation plus deployment discipline.**

---

## 17. If you want to expand this starter next

Good next additions are:

- validation error handling
- OpenAPI/Swagger docs
- service layer interfaces
- repository custom queries
- actuator health checks
- readiness and liveness probes
- staging and production profiles
- Docker Compose for local multi-service startup

