# Spring Boot Step-by-Step: Maven, Docker, Jenkins, and Kubernetes

_A practical quick reference based on **Continuous Delivery for Java Apps**._

This version focuses only on the build-and-delivery path you asked for: **Maven → Docker → Jenkins → Kubernetes**. The book’s Notepad example follows the same progression: build with Maven, package and distribute with Docker, automate with Jenkins Pipeline, and deploy to Kubernetes environments, with later promotion to staging and production. fileciteturn4file0 fileciteturn4file1 fileciteturn4file3

---

## 1. Big picture

Your delivery flow should look like this:

```text
Write Spring Boot code
        ↓
Build + test with Maven
        ↓
Build Docker image
        ↓
Push Docker image to registry
        ↓
Run Jenkins pipeline
        ↓
Deploy to Kubernetes
        ↓
Verify and promote
```

The book’s pipeline applies this idea to a Spring Boot Notepad application and treats the pipeline as a sequence of automated stages. fileciteturn0file0 fileciteturn4file1

---

## 2. What you need installed first

For a clean local setup, install:

- Java 21
- Maven 3.9+
- Docker
- kubectl
- A Kubernetes cluster
  - local options: Docker Desktop Kubernetes, Minikube, Kind
- Jenkins
  - local container is fine for learning
- A Docker registry account
  - Docker Hub is the simplest starter option

The book uses Docker Hub as the image registry and uses Kubernetes plus Jenkins to execute and deploy pipelines. fileciteturn4file0 fileciteturn4file1

---

## 3. Start with a minimal Spring Boot app

Use a structure like this:

```text
notes-app/
├── pom.xml
├── Dockerfile
├── Jenkinsfile
├── k8s/
│   ├── deployment.yaml
│   └── service.yaml
└── src/
    ├── main/
    │   ├── java/com/example/notes/
    │   │   └── NotesApplication.java
    │   └── resources/
    │       └── application.yml
    └── test/
        └── java/com/example/notes/
```

Your first milestone is simple: make sure the app runs locally before adding pipeline tooling.

---

## 4. Step 1: Maven

The book treats Maven as the core build tool and uses it for packaging, running tests, profiles, and publishing artifacts. It also separates unit and integration test concerns with Surefire and Failsafe, and shows Maven deploy/release flow. fileciteturn0file0

### 4.1 What Maven is responsible for

Use Maven to:

- compile the app
- run tests
- package the JAR
- optionally publish artifacts
- standardize builds in Jenkins

### 4.2 Minimal `pom.xml`

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

### 4.3 Minimal app class

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

### 4.4 Build commands you should know

```bash
mvn clean
mvn test
mvn package
mvn clean package
mvn clean install
```

For CI, your default starter command is:

```bash
mvn clean package
```

If you later add an artifact repository, use:

```bash
mvn clean deploy
```

That aligns with the book’s Jenkins examples, which frequently run `mvn clean package` or `mvn clean deploy` inside pipeline stages. fileciteturn4file1 fileciteturn4file3

### 4.5 Maven learning sequence

Follow this order:

1. Run `mvn test`
2. Run `mvn package`
3. Confirm `target/*.jar` is created
4. Run the JAR locally

```bash
java -jar target/notes-app-0.0.1-SNAPSHOT.jar
```

If this works, Maven is ready.

---

## 5. Step 2: Docker

The book uses Docker to make the app reproducible across environments and to distribute releases as images. It also explains container vs image, environment variables, volumes, networking, image tags, Docker Hub, and Dockerfile basics. fileciteturn4file4 fileciteturn4file0 fileciteturn4file2

### 5.1 Why Docker comes after Maven

First Maven gives you a JAR.
Then Docker wraps that JAR into a portable runtime image.

So the order is:

```text
source code → Maven build → JAR → Docker image → container
```

### 5.2 Minimal `Dockerfile`

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/notes-app-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

The book’s Docker chapter explains core Dockerfile instructions like `FROM`, `ENV`, `RUN`, `WORKDIR`, `COPY`, `EXPOSE`, `ENTRYPOINT`, and `VOLUME`. fileciteturn0file0

### 5.3 Build and run locally

```bash
mvn clean package
docker build -t yourdockerid/notes-app:local .
docker run -p 8080:8080 yourdockerid/notes-app:local
```

Open the app on port 8080.

### 5.4 Tagging strategy

The book emphasizes image tags and shows that tags are explicit version markers, not magic “latest means newest” guarantees. fileciteturn4file0

Use tags like:

```text
yourdockerid/notes-app:local
yourdockerid/notes-app:main
yourdockerid/notes-app:1.0.0
yourdockerid/notes-app:build-42
```

Good beginner rule:

- use `local` for your workstation
- use Git branch name for feature/test builds
- use release version for production

The book’s example tags Docker images based on the Git branch in Jenkins pipeline stages. fileciteturn0file0

### 5.5 Push to Docker Hub

```bash
docker login
docker push yourdockerid/notes-app:local
```

The book’s hands-on flow includes creating a Docker Hub account and publishing images there for later deployment. fileciteturn4file0

### 5.6 What you should complete before moving on

You are done with Docker when you can do this without errors:

```bash
mvn clean package
docker build -t yourdockerid/notes-app:local .
docker run -p 8080:8080 yourdockerid/notes-app:local
```

---

## 6. Step 3: Jenkins

The book uses Jenkins as the CI/CD server and then moves from basic pipeline to pipeline-as-code, Docker-based steps, and Kubernetes-backed dynamic agents. Jenkins is the automation engine that turns your manual commands into repeatable stages. fileciteturn0file0 fileciteturn4file1

### 6.1 What Jenkins should do for you

Jenkins should automate this:

1. checkout code
2. run Maven build
3. build Docker image
4. push image to registry
5. deploy to Kubernetes

### 6.2 Minimal Jenkins pipeline

Create a `Jenkinsfile`:

```groovy
pipeline {
    agent any

    environment {
        IMAGE_NAME = 'yourdockerid/notes-app'
        IMAGE_TAG = 'latest'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .'
            }
        }

        stage('Push Docker Image') {
            steps {
                sh 'docker push ${IMAGE_NAME}:${IMAGE_TAG}'
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh 'kubectl apply -f k8s/'
            }
        }
    }
}
```

This is the simplest useful version of what the book expands into multiple specialized pipeline jobs. The book also shows Jenkins stages that run Maven, Docker, and `kubectl` commands in separate containers. fileciteturn0file0 fileciteturn4file3

### 6.3 Credentials you will need in Jenkins

Set these up before the first real pipeline run:

- Git access credentials if needed
- Docker Hub username/password or token
- Kubernetes config or service account credentials

The book’s pipeline examples inject secrets and environment variables for Docker Hub and Maven settings into pipeline execution. fileciteturn0file0

### 6.4 Better branch-aware tagging

A better next step is to tag the Docker image with the branch or build number.

Example:

```groovy
environment {
    IMAGE_NAME = 'yourdockerid/notes-app'
    IMAGE_TAG = "${env.BUILD_NUMBER}"
}
```

Or use branch names in multibranch pipelines.

The book’s example uses a `GIT_BRANCH` parameter and builds tagged images from that branch. fileciteturn4file3

### 6.5 Jenkins learning sequence

Learn Jenkins in this order:

1. run `mvn clean package`
2. add Docker build
3. add Docker push
4. add `kubectl apply`
5. add environment-specific deployment
6. add test stages and promotion rules

Do not start with a huge pipeline.

---

## 7. Step 4: Kubernetes

The book’s Kubernetes material covers pods, labels, replica sets, services, config maps, secrets, deployments, readiness probes, liveness probes, and canary release. That maps directly to how a Spring Boot app should be deployed. fileciteturn0file0

### 7.1 What Kubernetes is responsible for

Use Kubernetes to:

- run your app containers
- expose them with a service
- scale replicas
- restart failed containers
- manage rollout updates
- separate environments

### 7.2 Minimal deployment file

Create `k8s/deployment.yaml`:

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
          image: yourdockerid/notes-app:latest
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: /actuator/health
              port: 8080
            initialDelaySeconds: 10
            periodSeconds: 5
          livenessProbe:
            httpGet:
              path: /actuator/health
              port: 8080
            initialDelaySeconds: 20
            periodSeconds: 10
```

The book explicitly covers deployments, readiness probes, and liveness probes as essential runtime controls. fileciteturn0file0

### 7.3 Minimal service file

Create `k8s/service.yaml`:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: notes-app
spec:
  selector:
    app: notes-app
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
  type: ClusterIP
```

The book explains services and Kubernetes service discovery as a standard way to let components talk to each other. fileciteturn0file0 fileciteturn4file1

### 7.4 Deploy manually first

Before Jenkins does it, do it yourself once:

```bash
kubectl apply -f k8s/
kubectl get deployments
kubectl get pods
kubectl get services
```

If the app fails, inspect it:

```bash
kubectl describe pod <pod-name>
kubectl logs <pod-name>
```

### 7.5 Namespace pattern

As you grow, split environments like this:

```text
dev
testing
staging
production
```

The book uses separate environments and repeatedly deploys the same application into testing, staging, and production contexts. fileciteturn0file0

### 7.6 Secrets and config

As soon as you add databases or API keys, move configuration out of the image.

Use:

- ConfigMap for non-secret config
- Secret for passwords/tokens

The book covers both config maps and secrets for Kubernetes-managed configuration. fileciteturn0file0

---

## 8. Jenkins + Kubernetes together

One of the strongest patterns in the book is running Jenkins jobs on Kubernetes-backed dynamic agents, where each build gets a fresh pod with the needed tools and containers. fileciteturn4file1

### 8.1 Why this matters

Instead of installing everything on one Jenkins server, you can run pipeline work in temporary pods.

The book shows a `podTemplate` using containers such as:

- `maven`
- `mysql`
- `docker`
- `kubectl`

and then executes stages inside the right container. fileciteturn0file0 fileciteturn4file1

### 8.2 Mental model

Think of it like this:

```text
Jenkins orchestrates
Kubernetes provides temporary build pods
Each pod has the tools needed for that pipeline
```

### 8.3 Beginner recommendation

Do this in phases:

- Phase 1: Jenkins on one machine, pipeline runs on the Jenkins node
- Phase 2: Jenkins deploys to Kubernetes
- Phase 3: Jenkins itself uses Kubernetes agents

That is much easier than trying the most advanced model on day one.

---

## 9. Full step-by-step learning plan

Here is the exact order I recommend.

### Phase A: Maven only

Goal: create a working JAR.

1. create Spring Boot project
2. run `mvn test`
3. run `mvn package`
4. run the JAR locally

Success check:

```bash
java -jar target/notes-app-0.0.1-SNAPSHOT.jar
```

### Phase B: Add Docker

Goal: run the app as a container.

1. create `Dockerfile`
2. run `mvn clean package`
3. run `docker build`
4. run `docker run -p 8080:8080 ...`
5. verify the app works

Success check:

```bash
docker ps
```

### Phase C: Add Docker registry

Goal: make image deployable from anywhere.

1. create Docker Hub repo
2. login with `docker login`
3. push image
4. verify the image exists remotely

Success check:

```bash
docker push yourdockerid/notes-app:local
```

### Phase D: Add Jenkins

Goal: automate build and image publishing.

1. install Jenkins
2. connect repository
3. create pipeline job
4. add `Jenkinsfile`
5. run Maven build in Jenkins
6. run Docker build in Jenkins
7. push image in Jenkins

Success check:

A Jenkins build can produce and push an image without you running commands manually.

### Phase E: Add Kubernetes

Goal: deploy the app cluster-side.

1. create deployment manifest
2. create service manifest
3. deploy with `kubectl apply -f k8s/`
4. inspect pods and logs
5. update image and rollout again

Success check:

```bash
kubectl rollout status deployment/notes-app
```

### Phase F: Connect Jenkins to Kubernetes

Goal: one-button delivery.

1. store registry and k8s credentials in Jenkins
2. add deploy stage to `Jenkinsfile`
3. let Jenkins run `kubectl apply`
4. confirm app updates automatically after pipeline success

Success check:

A commit triggers build → image → deploy.

---

## 10. Golden rules for each tool

### Maven

- keep the build reproducible
- always run tests in CI
- do not rely on “works only on my laptop”

### Docker

- image should contain only what runtime needs
- tag images clearly
- never hardcode secrets into images

### Jenkins

- pipeline should be code, not only UI clicks
- keep stages small and readable
- fail fast on build/test errors

### Kubernetes

- use deployments, not raw pods for apps
- use services for stable access
- use readiness and liveness probes
- keep config outside the image

These themes closely match the book’s progression from build, to image, to pipeline, to cluster operations. fileciteturn0file0

---

## 11. Simplest working command chain

This is the shortest scratch-to-cluster flow:

```bash
mvn clean package
docker build -t yourdockerid/notes-app:latest .
docker push yourdockerid/notes-app:latest
kubectl apply -f k8s/
```

Jenkins eventually automates those same commands.

---

## 12. What to add next after this baseline

Once the above works, add these in order:

1. Actuator health endpoint
2. MySQL container or managed DB
3. Flyway migrations
4. branch-based image tags
5. staging namespace
6. production namespace
7. smoke tests
8. canary deployment

The book’s full pipeline adds richer testing, artifact publishing, staging and production promotion, and canary rollout in production. fileciteturn0file0

---

## 13. Fast recap

If you are starting from zero, remember this:

- **Maven** builds the app
- **Docker** packages the app
- **Jenkins** automates the flow
- **Kubernetes** runs the app reliably

And the practical order is always:

```text
Maven first
Docker second
Jenkins third
Kubernetes fourth
```

That is the cleanest way to learn the stack without getting lost.

---

## 14. Full working Spring Boot simple API project you can start from

This section **keeps the earlier content intact** and adds a **fully working starter project** you can build, containerize, run in Jenkins, and deploy to Kubernetes.

It is intentionally simple, but it is a good base for a future **high-scale system design** project because it already gives you:

- a clean layered Spring Boot structure
- health endpoints
- externalized configuration
- Docker packaging
- Jenkins pipeline automation
- Kubernetes deployment and service
- room to later add database, queue, cache, auth, tracing, and autoscaling

---

## 15. What this sample API does

We will build a simple **Notes API** with these endpoints:

- `GET /api/v1/notes` → list notes
- `GET /api/v1/notes/{id}` → get one note
- `POST /api/v1/notes` → create note
- `DELETE /api/v1/notes/{id}` → delete note
- `GET /api/v1/ping` → lightweight API check
- `GET /actuator/health` → Kubernetes health check

For simplicity, this first version uses an **in-memory repository** based on `ConcurrentHashMap`.

That is good for learning the delivery flow first.
Later, for your high-scale project, replace the repository with:

- PostgreSQL / MySQL for transactions
- Redis for caching
- Kafka / RabbitMQ for async workflows
- Elasticsearch / OpenSearch for search

---

## 16. Final project structure

Create this exact structure:

```text
notes-app/
├── pom.xml
├── Dockerfile
├── Jenkinsfile
├── .dockerignore
├── k8s/
│   ├── deployment.yaml
│   └── service.yaml
└── src/
    ├── main/
    │   ├── java/com/example/notes/
    │   │   ├── NotesApplication.java
    │   │   ├── controller/
    │   │   │   ├── NoteController.java
    │   │   │   └── PingController.java
    │   │   ├── dto/
    │   │   │   ├── CreateNoteRequest.java
    │   │   │   └── NoteResponse.java
    │   │   ├── model/
    │   │   │   └── Note.java
    │   │   ├── repository/
    │   │   │   └── NoteRepository.java
    │   │   └── service/
    │   │       └── NoteService.java
    │   └── resources/
    │       └── application.yml
    └── test/
        └── java/com/example/notes/
            └── controller/
                └── NoteControllerTest.java
```

---

## 17. Step 1: Create `pom.xml`

Use this complete Maven file:

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
    <description>Simple Spring Boot Notes API</description>

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
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
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

Why this is enough:

- `web` gives REST API support
- `validation` validates input payloads
- `actuator` gives health endpoints for Kubernetes
- `test` gives MockMvc and JUnit testing support

---

## 18. Step 2: Create the main application class

File: `src/main/java/com/example/notes/NotesApplication.java`

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

---

## 19. Step 3: Add the domain model

File: `src/main/java/com/example/notes/model/Note.java`

```java
package com.example.notes.model;

import java.time.Instant;
import java.util.UUID;

public class Note {

    private UUID id;
    private String title;
    private String content;
    private Instant createdAt;

    public Note() {
    }

    public Note(UUID id, String title, String content, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
```

---

## 20. Step 4: Add request and response DTOs

File: `src/main/java/com/example/notes/dto/CreateNoteRequest.java`

```java
package com.example.notes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateNoteRequest {

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 5000)
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

File: `src/main/java/com/example/notes/dto/NoteResponse.java`

```java
package com.example.notes.dto;

import java.time.Instant;
import java.util.UUID;

public class NoteResponse {

    private UUID id;
    private String title;
    private String content;
    private Instant createdAt;

    public NoteResponse() {
    }

    public NoteResponse(UUID id, String title, String content, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
```

---

## 21. Step 5: Add the repository

File: `src/main/java/com/example/notes/repository/NoteRepository.java`

```java
package com.example.notes.repository;

import com.example.notes.model.Note;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class NoteRepository {

    private final ConcurrentMap<UUID, Note> storage = new ConcurrentHashMap<>();

    public List<Note> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(Note::getCreatedAt).reversed())
                .toList();
    }

    public Optional<Note> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Note save(Note note) {
        storage.put(note.getId(), note);
        return note;
    }

    public boolean deleteById(UUID id) {
        return storage.remove(id) != null;
    }
}
```

Why this is a good starter repository:

- thread-safe for local multi-request testing
- no external database needed
- easy to replace later with JPA, JDBC, R2DBC, DynamoDB, Cassandra, or another store

---

## 22. Step 6: Add the service layer

File: `src/main/java/com/example/notes/service/NoteService.java`

```java
package com.example.notes.service;

import com.example.notes.dto.CreateNoteRequest;
import com.example.notes.dto.NoteResponse;
import com.example.notes.model.Note;
import com.example.notes.repository.NoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<NoteResponse> getAll() {
        return noteRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public NoteResponse getById(UUID id) {
        return noteRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
    }

    public NoteResponse create(CreateNoteRequest request) {
        Note note = new Note(
                UUID.randomUUID(),
                request.getTitle().trim(),
                request.getContent().trim(),
                Instant.now()
        );

        return toResponse(noteRepository.save(note));
    }

    public void delete(UUID id) {
        boolean deleted = noteRepository.deleteById(id);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found");
        }
    }

    private NoteResponse toResponse(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getCreatedAt()
        );
    }
}
```

---

## 23. Step 7: Add controllers

File: `src/main/java/com/example/notes/controller/PingController.java`

```java
package com.example.notes.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class PingController {

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("message", "pong");
    }
}
```

File: `src/main/java/com/example/notes/controller/NoteController.java`

```java
package com.example.notes.controller;

import com.example.notes.dto.CreateNoteRequest;
import com.example.notes.dto.NoteResponse;
import com.example.notes.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public List<NoteResponse> getAll() {
        return noteService.getAll();
    }

    @GetMapping("/{id}")
    public NoteResponse getById(@PathVariable UUID id) {
        return noteService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse create(@Valid @RequestBody CreateNoteRequest request) {
        return noteService.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        noteService.delete(id);
    }
}
```

---

## 24. Step 8: Add application configuration

File: `src/main/resources/application.yml`

```yaml
server:
  port: ${SERVER_PORT:8080}

spring:
  application:
    name: notes-app

management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      probes:
        enabled: true
  health:
    livenessstate:
      enabled: true
    readinessstate:
      enabled: true
```

This gives you:

- normal app port on 8080
- actuator health checks
- Kubernetes-compatible liveness and readiness behavior

---

## 25. Step 9: Add a test

File: `src/test/java/com/example/notes/controller/NoteControllerTest.java`

```java
package com.example.notes.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void pingShouldReturnPong() throws Exception {
        mockMvc.perform(get("/api/v1/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("pong"));
    }

    @Test
    void createShouldReturnCreated() throws Exception {
        String requestBody = """
                {
                  "title": "First note",
                  "content": "My first content"
                }
                """;

        mockMvc.perform(post("/api/v1/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("First note"))
                .andExpect(jsonPath("$.content").value("My first content"));
    }
}
```

---

## 26. Step 10: Build and run locally with Maven

From the project root:

```bash
mvn clean test
mvn clean package
java -jar target/notes-app-0.0.1-SNAPSHOT.jar
```

Now test the API:

```bash
curl http://localhost:8080/api/v1/ping
```

Expected:

```json
{"message":"pong"}
```

Create a note:

```bash
curl -X POST http://localhost:8080/api/v1/notes \
  -H "Content-Type: application/json" \
  -d '{"title":"hello","content":"world"}'
```

List notes:

```bash
curl http://localhost:8080/api/v1/notes
```

If this works, your Spring Boot app is already fully functional.

---

## 27. Step 11: Add `.dockerignore`

File: `.dockerignore`

```text
target/
.git/
.idea/
.vscode/
*.iml
```

This keeps Docker build context small and fast.

---

## 28. Step 12: Add the Dockerfile

File: `Dockerfile`

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/notes-app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

Build and run it:

```bash
mvn clean package
docker build -t notes-app:local .
docker run -p 8080:8080 notes-app:local
```

Test again:

```bash
curl http://localhost:8080/api/v1/ping
```

---

## 29. Step 13: Tag and push the image

Replace `yourdockerid` with your registry name.

```bash
docker tag notes-app:local yourdockerid/notes-app:0.0.1
docker push yourdockerid/notes-app:0.0.1
```

Later, Jenkins will automate this.

---

## 30. Step 14: Add Kubernetes manifests

File: `k8s/deployment.yaml`

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: notes-app
  labels:
    app: notes-app
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
          image: yourdockerid/notes-app:0.0.1
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
          env:
            - name: SERVER_PORT
              value: "8080"
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 10
            periodSeconds: 10
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 20
            periodSeconds: 20
          resources:
            requests:
              cpu: "100m"
              memory: "128Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
```

File: `k8s/service.yaml`

```yaml
apiVersion: v1
kind: Service
metadata:
  name: notes-app
spec:
  selector:
    app: notes-app
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
  type: ClusterIP
```

Apply them:

```bash
kubectl apply -f k8s/
kubectl get pods
kubectl get svc
kubectl rollout status deployment/notes-app
```

For local testing, port-forward:

```bash
kubectl port-forward service/notes-app 8080:80
```

Then call:

```bash
curl http://localhost:8080/api/v1/ping
```

---

## 31. Step 15: Add the Jenkins pipeline

File: `Jenkinsfile`

```groovy
pipeline {
    agent any

    environment {
        IMAGE_NAME = 'yourdockerid/notes-app'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                sh 'mvn clean test package'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $IMAGE_NAME:$IMAGE_TAG .'
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USERNAME',
                    passwordVariable: 'DOCKER_PASSWORD'
                )]) {
                    sh '''
                      echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin
                      docker push $IMAGE_NAME:$IMAGE_TAG
                    '''
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG_FILE')]) {
                    sh '''
                      export KUBECONFIG=$KUBECONFIG_FILE
                      sed "s#yourdockerid/notes-app:0.0.1#$IMAGE_NAME:$IMAGE_TAG#g" k8s/deployment.yaml | kubectl apply -f -
                      kubectl apply -f k8s/service.yaml
                      kubectl rollout status deployment/notes-app
                    '''
                }
            }
        }
    }
}
```

### Jenkins credentials you need

Create these in Jenkins:

- `dockerhub-creds` → username/password credential
- `kubeconfig` → secret file credential containing kubeconfig

### What this pipeline does

1. checks out code
2. runs Maven tests and package
3. builds Docker image
4. pushes image to registry
5. deploys updated image to Kubernetes

This is already enough for a working CI/CD starter.

---

## 32. Step 16: Full run order from zero

Use this exact order:

### Local application check

```bash
mvn clean test package
java -jar target/notes-app-0.0.1-SNAPSHOT.jar
```

### Local Docker check

```bash
docker build -t notes-app:local .
docker run -p 8080:8080 notes-app:local
```

### Push image

```bash
docker tag notes-app:local yourdockerid/notes-app:0.0.1
docker push yourdockerid/notes-app:0.0.1
```

### Kubernetes deployment

```bash
kubectl apply -f k8s/
kubectl rollout status deployment/notes-app
kubectl port-forward service/notes-app 8080:80
```

### Jenkins automation

1. push code to GitHub
2. create Jenkins pipeline job
3. point Jenkins to repository
4. configure credentials
5. run pipeline

---

## 33. Step 17: Why this is a good base for a high-scale project

This sample is simple, but the structure is already correct.

### What you keep

Keep these parts as-is:

- layered package structure
- controller/service separation
- Docker packaging
- Jenkins pipeline flow
- Kubernetes deployment pattern
- actuator health endpoints

### What you replace next

For a real high-scale system design build, replace or extend these parts next:

#### Persistence

Replace in-memory repository with one of these:

- Spring Data JPA + PostgreSQL
- Spring JDBC + PostgreSQL
- Cassandra for write-heavy scale patterns
- DynamoDB if cloud-native key access is your model

#### Caching

Add Redis for:

- hot reads
- rate limiting
- session/token helpers
- idempotency keys

#### Messaging

Add Kafka or RabbitMQ for:

- async note processing
- notifications
- event-driven integrations
- audit pipelines

#### Observability

Add:

- Micrometer metrics
- Prometheus
- Grafana
- structured JSON logs
- OpenTelemetry tracing

#### Reliability

Add:

- retries and timeouts
- circuit breaking
- graceful shutdown
- horizontal pod autoscaling
- pod disruption budgets

#### Security

Add:

- Spring Security
- OAuth2/JWT
- API gateway
- network policies
- secrets manager integration

---

## 34. Step 18: Next upgrade path for your scale project

A practical order for evolving this starter is:

1. move repository from memory to PostgreSQL
2. add Flyway migrations
3. add Redis cache
4. add pagination for `GET /notes`
5. add update endpoint
6. add optimistic locking/version field
7. add Kafka for domain events
8. add metrics and tracing
9. add HPA in Kubernetes
10. split read and write paths if traffic demands it

That path lets you grow from a simple monolith into a production-grade service without throwing away the first version.

---

## 35. Copy-paste checklist

If you want the shortest possible checklist, do this:

1. create all files above
2. run `mvn clean test package`
3. run `java -jar target/notes-app-0.0.1-SNAPSHOT.jar`
4. test `GET /api/v1/ping`
5. build Docker image
6. push image
7. apply Kubernetes manifests
8. configure Jenkins credentials
9. run Jenkins pipeline

At that point you have:

- a working Spring Boot API
- Maven build
- Docker image
- Jenkins automation
- Kubernetes deployment

---

## 36. Important note before using this for real scale

This sample is a **delivery-ready starter**, not yet a true internet-scale design.

It is the correct place to begin because it teaches the foundation cleanly:

- build correctness
- packaging correctness
- deployment correctness
- health check correctness
- automation correctness

Then you can safely evolve the inside of the application for scale.
