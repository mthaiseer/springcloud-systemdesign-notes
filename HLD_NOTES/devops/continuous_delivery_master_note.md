# Continuous Delivery for Java Apps — Master Note

A compact, implementation-first study note distilled from the uploaded book. It is designed to replace a first pass through the full book and give you one practical reference to learn the ideas, tools, and end-to-end implementation flow.

This note follows the book’s structure: Agile and delivery concepts, automated testing, Maven, Flyway, Docker, Jenkins, Kubernetes, and the hands-on multi-stage delivery pipeline for the sample Notepad app.

---

## 0. What you are trying to build

The final target is a delivery system where:

1. Developers work in small tasks and integrate to mainline frequently.
2. Every change is verified by automated tests.
3. The application is packaged with Maven.
4. Database changes are versioned with Flyway.
5. The app is wrapped into a Docker image.
6. Jenkins runs the pipeline as code.
7. Jenkins uses Kubernetes agents for isolated builds and test environments.
8. The app is deployed through testing, staging, canary, and production.
9. Releases are low-risk because canary, smoke tests, and monitoring reduce blast radius.

That is the core message of the book: **continuous delivery is not one tool; it is a chain of engineering practices plus automation**.

---

## 1. Big picture mental model

### 1.1 Continuous Integration vs Continuous Delivery vs Continuous Deployment

- **Continuous Integration (CI)**: integrate code to a shared mainline frequently, ideally at least daily, and verify every integration with automated build + test.
- **Continuous Delivery (CD)**: keep the mainline always deployable, so production deployment can happen safely on demand.
- **Continuous Deployment**: every passing change is automatically deployed to production with no manual approval gate.

### 1.2 Deployed vs Released

These are not the same.

- **Deployed** = code exists in an environment.
- **Released** = feature is visible to users.

You can deploy code to production and still keep it off with a feature flag.

### 1.3 Canary release vs A/B testing vs feature flags

- **Canary release**: send a small percentage of traffic to a new version for minutes or hours to reduce deployment risk.
- **A/B test**: compare feature variants for days or longer to learn which performs better.
- **Feature flag**: switch features on or off without redeploying.

Rule of thumb:

- Use **canary** for release safety.
- Use **feature flags** for controlled exposure.
- Use **A/B tests** for product learning.

---

## 2. Agile, Scrum, and XP — only the parts that matter for delivery

### 2.1 Agile in one sentence

Agile is not “move fast.” It is **adapt safely to change**.

### 2.2 Scrum essentials

Scrum gives you an iteration model:

- Product Backlog
- Sprint Backlog
- Sprint Planning
- Daily Scrum
- Sprint Review
- Sprint Retrospective

### 2.3 Why Scrum affects CI/CD

The most important delivery lesson from the book is this:

> If tasks are too big, CI breaks. If CI breaks, CD is impossible.

So in Sprint Planning:

- split work into tasks that can be coded, tested, and integrated in about a day or less
- avoid long-lived branches
- treat automation as part of the task, not “later work”

### 2.4 XP practices you need for CD

The book emphasizes two XP practices as non-negotiable:

- **Automated tests**
- **Continuous integration**

Other helpful practices:

- TDD
- refactoring
- pair programming
- simple design

These improve quality, but the first two are the minimum foundation.

---

## 3. The delivery mindset you must keep

### Non-negotiables

- Small tasks
- Frequent integration
- Automated tests for every change
- Mainline always releasable
- Build once, promote the same artifact
- Infrastructure and pipelines defined as code
- Fast feedback
- Reproducible environments

### Common failure patterns

- Feature branches live for a week
- Manual testing is the only safety net
- “Release day” becomes merge day
- Build works only on one machine
- Dev, test, and prod behave differently
- Database schema changes are manual
- Rollback means panic

---

## 4. Reference architecture from the book

The book’s example system uses:

- **Java / Spring Boot** for the application
- **Maven** for build and dependency management
- **Flyway** for DB migrations
- **JUnit / Spring test support** for unit and integration tests
- **Selenium** for acceptance tests
- **Gatling** for performance tests
- **Docker** for packaging
- **Artifactory** for Maven artifacts
- **Docker Hub** for container images
- **Jenkins** for CI/CD orchestration
- **Kubernetes** for runtime and Jenkins agents
- **Slack / Hubot** for ChatOps
- **Vagrant** for local cluster setup in the book’s lab setup

---

## 5. Learning path: study in this order

Do not try to master everything at once.

### Phase 1 — core ideas
1. CI/CD vocabulary
2. deployed vs released
3. canary vs A/B vs feature flags
4. why small tasks matter

### Phase 2 — application quality
1. unit tests
2. integration tests
3. acceptance tests
4. smoke tests
5. performance tests
6. Flyway migrations

### Phase 3 — packaging
1. Maven lifecycle
2. snapshots vs releases
3. Artifactory publishing
4. Dockerfile and image publishing

### Phase 4 — automation
1. Jenkins job concepts
2. Jenkins Pipeline / Jenkinsfile
3. Docker in Jenkins
4. Kubernetes agents

### Phase 5 — deployment platform
1. Kubernetes objects
2. config and secrets
3. health probes
4. staged environments
5. canary rollout

---

## 6. Quick project skeleton you can build from scratch

Use this as the minimum sample app.

```text
notepad/
├─ pom.xml
├─ src/main/java/com/example/notepad/
│  ├─ NotepadApplication.java
│  ├─ model/Note.java
│  ├─ repository/NoteRepository.java
│  ├─ service/NoteService.java
│  └─ web/NoteController.java
├─ src/main/resources/
│  ├─ application.yml
│  └─ db/migration/
│     └─ V1__create_note_table.sql
├─ src/test/java/com/example/notepad/
│  ├─ model/NoteTest.java
│  ├─ service/NoteServiceTest.java
│  └─ web/NoteControllerTest.java
├─ Dockerfile
└─ Jenkinsfile
```

---

## 7. Spring Boot quick reference

### 7.1 Main application

```java
@SpringBootApplication
public class NotepadApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotepadApplication.class, args);
    }
}
```

### 7.2 Entity/model

```java
@Entity
@Table(name = "note")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 4000)
    private String content;

    protected Note() {}

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
}
```

### 7.3 Repository

```java
public interface NoteRepository extends JpaRepository<Note, Long> {
}
```

### 7.4 Service

```java
@Service
public class NoteService {
    private final NoteRepository repo;

    public NoteService(NoteRepository repo) {
        this.repo = repo;
    }

    public List<Note> findAll() {
        return repo.findAll();
    }

    public Note create(String title, String content) {
        return repo.save(new Note(title, content));
    }
}
```

### 7.5 REST controller

```java
@RestController
@RequestMapping("/notes")
public class NoteController {
    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Note> list() {
        return service.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Note create(@RequestBody CreateNoteRequest req) {
        return service.create(req.title(), req.content());
    }
}

public record CreateNoteRequest(String title, String content) {}
```

### 7.6 application.yml

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/notepad
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
```

### What to understand

- Spring Boot wires your app fast.
- Keep business logic in services, not controllers.
- Let Flyway own schema changes.
- Prefer externalized config for environment-specific values.

---

## 8. Automated testing mastery guide

The book treats tests as the foundation of delivery.

### 8.1 Test pyramid used here

1. **Unit tests** — fastest, most numerous
2. **Integration tests** — verify app components and DB interactions
3. **Acceptance tests** — verify user-visible behavior
4. **Smoke tests** — quick deployment sanity check
5. **Performance tests** — verify latency / throughput under load

### 8.2 Unit test example

Purpose: verify one class in isolation.

```java
class NoteTest {
    @Test
    void creates_note_with_title_and_content() {
        Note note = new Note("todo", "buy milk");

        assertEquals("todo", note.getTitle());
        assertEquals("buy milk", note.getContent());
    }
}
```

### 8.3 Service integration test example

Purpose: verify service + repository + DB behavior.

```java
@SpringBootTest
@Transactional
class NoteServiceTest {
    @Autowired
    private NoteService service;

    @Test
    void creates_and_reads_note() {
        service.create("todo", "buy milk");

        List<Note> notes = service.findAll();
        assertEquals(1, notes.size());
        assertEquals("todo", notes.get(0).getTitle());
    }
}
```

### 8.4 Controller integration test example

Purpose: verify HTTP layer.

```java
@SpringBootTest
@AutoConfigureMockMvc
class NoteControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void post_note_returns_201() throws Exception {
        mvc.perform(post("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"todo","content":"buy milk"}
                """))
            .andExpect(status().isCreated());
    }
}
```

### 8.5 Acceptance test example with Selenium

Purpose: verify end-user workflow in a browser.

```java
public class NewNotePage {
    private final WebDriver driver;

    public NewNotePage(WebDriver driver) {
        this.driver = driver;
    }

    public void open(String baseUrl) {
        driver.get(baseUrl + "/");
    }

    public void createNote(String title, String content) {
        driver.findElement(By.id("title")).sendKeys(title);
        driver.findElement(By.id("content")).sendKeys(content);
        driver.findElement(By.id("save")).click();
    }
}
```

```java
class CreateNoteTest {
    WebDriver driver;

    @BeforeEach
    void setup() {
        driver = new ChromeDriver();
    }

    @AfterEach
    void teardown() {
        driver.quit();
    }

    @Test
    void user_can_create_note() {
        NewNotePage page = new NewNotePage(driver);
        page.open("http://localhost:8080");
        page.createNote("todo", "buy milk");
        assertTrue(driver.getPageSource().contains("todo"));
    }
}
```

### 8.6 Smoke test example

Purpose: fast confidence after deployment.

```bash
curl -f http://localhost:8080/actuator/health
curl -f http://localhost:8080/notes
```

A smoke test should answer only one question:

> Is the deployment alive enough to continue?

### 8.7 Performance test example with Gatling

```scala
class HomeSimulation extends Simulation {
  val httpProtocol = http.baseUrl("http://localhost:8080")

  val scn = scenario("home")
    .exec(http("health").get("/actuator/health").check(status.is(200)))

  setUp(
    scn.inject(atOnceUsers(10), rampUsers(100).during(30))
  ).protocols(httpProtocol)
}
```

### 8.8 Test execution strategy

- On every commit: unit + integration
- On testing env deploy: acceptance
- On staging/prod deploy: smoke
- Scheduled or pre-release: performance

### 8.9 Test mistakes to avoid

- UI tests for everything
- flaky tests with no ownership
- no separation between unit and integration phases
- long test suites blocking every commit
- shared mutable test environments

---

## 9. Maven quick reference

Maven is the build backbone in the book.

### 9.1 What Maven does

- dependency management
- compile code
- run tests
- package jar/war
- publish artifacts
- manage build lifecycle

### 9.2 Minimal pom.xml

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.example</groupId>
  <artifactId>notepad</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>jar</packaging>

  <properties>
    <java.version>17</java.version>
    <spring.boot.version>3.3.0</spring.boot.version>
  </properties>

  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>${spring.boot.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>

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
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-j</artifactId>
      <scope>runtime</scope>
    </dependency>
    <dependency>
      <groupId>org.flywaydb</groupId>
      <artifactId>flyway-core</artifactId>
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

### 9.3 Lifecycle you must memorize

- `validate`
- `compile`
- `test`
- `package`
- `verify`
- `install`
- `deploy`

### 9.4 Most useful commands

```bash
mvn clean test
mvn clean package
mvn clean verify
mvn clean install
mvn clean deploy
```

### 9.5 Snapshot vs release

- `1.0.0-SNAPSHOT` = mutable, ongoing development version
- `1.0.0` = immutable release version

Use snapshots for active development, releases for versioned promotion.

### 9.6 Surefire vs Failsafe

- **Surefire**: unit tests during `test`
- **Failsafe**: integration tests during `integration-test` and `verify`

Example split:

```xml
<plugin>
  <artifactId>maven-surefire-plugin</artifactId>
  <configuration>
    <includes>
      <include>**/*Test.java</include>
    </includes>
    <excludes>
      <exclude>**/*IT.java</exclude>
    </excludes>
  </configuration>
</plugin>

<plugin>
  <artifactId>maven-failsafe-plugin</artifactId>
  <executions>
    <execution>
      <goals>
        <goal>integration-test</goal>
        <goal>verify</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <includes>
      <include>**/*IT.java</include>
    </includes>
  </configuration>
</plugin>
```

### 9.7 Maven profiles

Use profiles for environment-specific or optional behavior.

```xml
<profiles>
  <profile>
    <id>acceptance</id>
    <properties>
      <test.base.url>http://localhost:8080</test.base.url>
    </properties>
  </profile>
</profiles>
```

Run with:

```bash
mvn verify -Pacceptance
```

### 9.8 Publishing to Artifactory

`distributionManagement` example:

```xml
<distributionManagement>
  <repository>
    <id>releases</id>
    <url>https://artifactory.example.com/libs-release-local</url>
  </repository>
  <snapshotRepository>
    <id>snapshots</id>
    <url>https://artifactory.example.com/libs-snapshot-local</url>
  </snapshotRepository>
</distributionManagement>
```

`settings.xml` credentials example:

```xml
<settings>
  <servers>
    <server>
      <id>releases</id>
      <username>${env.ARTIFACTORY_USER}</username>
      <password>${env.ARTIFACTORY_PASSWORD}</password>
    </server>
    <server>
      <id>snapshots</id>
      <username>${env.ARTIFACTORY_USER}</username>
      <password>${env.ARTIFACTORY_PASSWORD}</password>
    </server>
  </servers>
</settings>
```

Deploy with:

```bash
mvn -B -s settings.xml clean deploy
```

### 9.9 Release plugin mental model

- `release:prepare` — updates version, tags SCM, creates release metadata
- `release:perform` — checks out tagged release and deploys artifacts

In modern teams, some prefer simpler Git tag + pipeline-based versioning, but you still need to understand the release plugin because it appears in the book and many older enterprise pipelines.

---

## 10. Flyway quick reference

Flyway version-controls database schema changes.

### 10.1 Why it matters

Without migration tooling:

- environments drift
- manual SQL gets lost
- deployments become risky

### 10.2 Migration naming

```text
V1__create_note_table.sql
V2__add_note_subtitle.sql
V3__create_index_on_title.sql
```

### 10.3 Example migration

```sql
CREATE TABLE note (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  content TEXT NOT NULL
);
```

Next migration:

```sql
ALTER TABLE note ADD COLUMN subtitle VARCHAR(255) NULL;
```

### 10.4 Good migration rules

- never edit applied migrations in shared environments
- only add new migrations
- make forward changes explicit
- test migrations in CI
- keep app startup compatible with target schema transition

---

## 11. Docker quick reference

Docker is used here to package the app and support isolated runtime and build steps.

### 11.1 Image vs container

- **Image** = immutable template
- **Container** = running instance of an image

### 11.2 Important registry ideas

- Docker Hub is a public registry
- tags identify versions
- push built images after tests pass

### 11.3 Minimal Dockerfile for Spring Boot

```dockerfile
FROM eclipse-temurin:17-jre
ENV APP_HOME=/opt/notepad
WORKDIR $APP_HOME
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 11.4 Dockerfile instructions you must know

- `FROM` — base image
- `ENV` — environment variable
- `RUN` — execute command at build time
- `WORKDIR` — set working directory
- `COPY` — copy local files into image
- `ADD` — like COPY plus extra features; use sparingly
- `EXPOSE` — document container port
- `ENTRYPOINT` — main executable
- `VOLUME` — declare persistent mount point
- `USER` — run as non-root

### 11.5 Better Dockerfile pattern

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8080
USER 10001
ENTRYPOINT ["java","-jar","app.jar"]
```

### 11.6 Build, run, push

```bash
docker build -t youruser/notepad:1.0.0 .
docker run -p 8080:8080 youruser/notepad:1.0.0
docker login
docker push youruser/notepad:1.0.0
```

### 11.7 Environment variables

```bash
docker run -e SPRING_PROFILES_ACTIVE=prod youruser/notepad:1.0.0
```

### 11.8 Volumes

Use volumes for persistent data, not for app binaries.

```bash
docker run -v mysql-data:/var/lib/mysql mysql:8
```

### 11.9 Networking

```bash
docker network create app-net
docker run -d --name db --network app-net mysql:8
docker run -d --name app --network app-net youruser/notepad:1.0.0
```

### 11.10 Commands worth memorizing

```bash
docker ps
docker images
docker logs <container>
docker exec -it <container> sh
docker rm -f <container>
docker rmi <image>
docker network ls
docker volume ls
docker system prune
```

### 11.11 Practical rules

- pin image tags
- keep images small
- prefer non-root user
- externalize config
- one process per container in most app cases
- do not bake secrets into images

---

## 12. Jenkins quick reference

Jenkins is the pipeline orchestrator in the book.

### 12.1 Core concepts

- **Job / Project**: automation unit
- **Build**: one execution of a job
- **Artifact**: output stored from a build
- **Workspace**: build directory
- **Executor**: worker slot
- **Node / Agent**: machine running builds
- **Plugin**: extension system

### 12.2 Why Pipeline as Code matters

Old style Jenkins = click-built jobs.

Better style Jenkins = `Jenkinsfile` in source control.

Benefits:

- reviewable
- versioned
- reproducible
- testable as code

### 12.3 Declarative vs Scripted Pipeline

- **Declarative**: simpler, opinionated, easier for teams
- **Scripted**: Groovy-based, more flexible, used in the book’s examples

### 12.4 Minimal declarative Jenkinsfile

```groovy
pipeline {
  agent any

  stages {
    stage('Build') {
      steps {
        sh 'mvn clean package'
      }
    }
    stage('Test') {
      steps {
        sh 'mvn test'
      }
    }
  }

  post {
    always {
      junit '**/target/surefire-reports/*.xml'
    }
  }
}
```

### 12.5 Minimal scripted Jenkinsfile

```groovy
node {
  try {
    stage('Checkout') {
      checkout scm
    }
    stage('Build') {
      sh 'mvn clean package'
    }
  } finally {
    junit '**/target/surefire-reports/*.xml'
  }
}
```

### 12.6 Typical CD stages

1. Checkout
2. Unit tests
3. Integration tests
4. Package artifact
5. Publish artifact to Artifactory
6. Build Docker image
7. Push Docker image
8. Deploy testing
9. Run acceptance tests
10. Deploy staging
11. Run smoke/performance tests
12. Deploy canary
13. Verify metrics
14. Promote production

### 12.7 Jenkins with Docker

Two common uses:

- run build inside a Docker image
- build Docker images from pipeline

Example:

```groovy
pipeline {
  agent {
    docker {
      image 'maven:3.9-eclipse-temurin-17'
    }
  }
  stages {
    stage('Build') {
      steps {
        sh 'mvn -B clean verify'
      }
    }
  }
}
```

### 12.8 Credentials handling

Never hardcode secrets.

```groovy
withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
  sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'
}
```

### 12.9 Slack notification example

```groovy
post {
  success {
    slackSend message: "Build succeeded: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
  }
  failure {
    slackSend message: "Build failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
  }
}
```

### 12.10 Jenkins design rules

- keep pipeline in repo
- archive test reports
- fail fast on broken builds
- use shared libraries only when repetition is painful
- keep agents disposable
- prefer immutable build environments

---

## 13. ChatOps quick reference

The book uses Slack and Hubot to interact with Jenkins.

### What ChatOps gives you

- shared visibility into builds and deployments
- faster team coordination
- auditable operational history in chat

### Minimal practical version

You do not need full Hubot to get value.

Start with:

- Jenkins Slack notifications
- deployment messages to a shared channel
- links to build logs and dashboards

Then optionally add:

- slash commands
- bot-triggered builds
- environment status lookups

---

## 14. Kubernetes quick reference

Kubernetes is both the deployment platform and the way Jenkins gets scalable, isolated agents in the book.

### 14.1 Why Kubernetes

It gives you:

- scheduling
- self-healing
- service discovery
- declarative desired state
- rolling updates
- horizontal scaling
- environment reproducibility

### 14.2 Objects you must know first

#### Namespace
A logical cluster partition.

```bash
kubectl create namespace testing
```

#### Pod
Smallest deployable unit.

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: notepad
spec:
  containers:
    - name: app
      image: youruser/notepad:1.0.0
      ports:
        - containerPort: 8080
```

#### Labels
Key-value metadata used for grouping and selection.

```yaml
metadata:
  labels:
    app: notepad
    env: testing
```

#### ReplicaSet
Keeps a desired number of identical Pods running.

Usually managed through a Deployment.

#### Service
Stable network endpoint for Pods.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: notepad-service
spec:
  selector:
    app: notepad
  ports:
    - port: 80
      targetPort: 8080
```

#### Deployment
Manages stateless app rollout.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: notepad
spec:
  replicas: 2
  selector:
    matchLabels:
      app: notepad
  template:
    metadata:
      labels:
        app: notepad
    spec:
      containers:
        - name: app
          image: youruser/notepad:1.0.0
          ports:
            - containerPort: 8080
```

### 14.3 Service discovery

Pods discover services by DNS name.

- same namespace: `http://notepad-service`
- cross namespace: `http://notepad-service.testing.svc.cluster.local`

### 14.4 ConfigMap

For non-secret configuration.

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: notepad-config
data:
  SPRING_PROFILES_ACTIVE: testing
  LOG_LEVEL: INFO
```

Use as env:

```yaml
envFrom:
  - configMapRef:
      name: notepad-config
```

### 14.5 Secret

For credentials and sensitive values.

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: db-secret
type: Opaque
stringData:
  DB_USER: root
  DB_PASSWORD: root
```

Use as env:

```yaml
env:
  - name: DB_USER
    valueFrom:
      secretKeyRef:
        name: db-secret
        key: DB_USER
```

Use as files:

```yaml
volumes:
  - name: secret-vol
    secret:
      secretName: db-secret
```

### 14.6 Volumes

Use for persistent or shared data.

Examples:

- database storage
- mounted configs
- mounted secrets

### 14.7 Readiness vs liveness probes

- **Readiness probe**: should this Pod receive traffic?
- **Liveness probe**: should this Pod be restarted?

Example:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5

livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 20
  periodSeconds: 10
```

### 14.8 Rollout commands

```bash
kubectl apply -f k8s/
kubectl get pods
kubectl get svc
kubectl describe deployment notepad
kubectl rollout status deployment/notepad
kubectl logs deploy/notepad
kubectl exec -it deploy/notepad -- sh
```

### 14.9 Local learning choices

The book uses Vagrant to build a local cluster. For learning, any local cluster is fine as long as you practice the same ideas:

- Minikube
- kind
- k3d
- a cloud dev cluster

The platform can differ. The concepts should not.

### 14.10 Architecture terms you should know

Control plane side:

- API server
- etcd
- scheduler
- controller manager

Node side:

- kubelet
- kube-proxy / service proxy
- container runtime

You do not need to master internals before shipping apps, but you should know the names and responsibilities.

---

## 15. Jenkins on Kubernetes pattern

One of the book’s key ideas is running Jenkins jobs on disposable Kubernetes agents.

### Why this is powerful

- clean build environment per run
- scalable parallelism
- no snowflake build servers
- easy multi-container build Pods

### Mental model

Jenkins controller schedules a build.

For that build, Kubernetes creates an agent Pod containing one or more containers, such as:

- Maven container
- Docker CLI container
- kubectl container
- database sidecar for tests

Then the Pod disappears after the job.

### Conceptual agent example

```groovy
podTemplate(label: 'testing', containers: [
  containerTemplate(name: 'maven', image: 'maven:3.9-eclipse-temurin-17', ttyEnabled: true, command: 'cat'),
  containerTemplate(name: 'docker', image: 'docker:27-cli', ttyEnabled: true, command: 'cat'),
  containerTemplate(name: 'kubectl', image: 'bitnami/kubectl:latest', ttyEnabled: true, command: 'cat')
]) {
  node('testing') {
    stage('Build') {
      container('maven') {
        sh 'mvn -B clean verify'
      }
    }
  }
}
```

---

## 16. End-to-end pipeline blueprint

This is the most important part of the note.

## 16.1 Branch flow

A safe working model:

1. Create short-lived feature branch.
2. Commit small changes.
3. Push branch.
4. CI validates branch.
5. Open PR.
6. Merge to mainline quickly.
7. Mainline pipeline builds release candidate.
8. Deploy through environments.

## 16.2 Recommended stages

### Stage A — branch/testing pipeline
Purpose: prove branch works in a realistic environment.

Steps:
1. checkout code
2. run unit tests
3. run integration tests
4. publish snapshot artifact
5. build Docker image tagged by branch
6. spin up testing environment on Kubernetes
7. run acceptance tests against testing env

### Stage B — mainline continuous integration
Purpose: keep mainline healthy.

Steps:
1. checkout mainline
2. run unit + integration tests
3. package artifact
4. publish snapshot artifact
5. build and push Docker image for mainline

### Stage C — release pipeline
Purpose: turn a tested mainline into a versioned release.

Steps:
1. version release
2. create Git tag
3. publish release artifact to Artifactory
4. build/push versioned Docker image

### Stage D — staging pipeline
Purpose: validate release candidate in a near-prod environment.

Steps:
1. deploy release image to staging
2. run smoke tests
3. optionally run performance tests
4. verify observability

### Stage E — canary pipeline
Purpose: reduce production risk.

Steps:
1. deploy release to canary subset
2. route a small percentage of traffic
3. run smoke checks and monitor
4. increase traffic if healthy

### Stage F — production pipeline
Purpose: fully promote release.

Steps:
1. scale up new version
2. scale down old version
3. confirm metrics, logs, health
4. mark deployment complete

---

## 17. Example Jenkinsfile you can adapt

This is a simplified implementation-first pipeline.

```groovy
pipeline {
  agent any

  environment {
    IMAGE_NAME = 'youruser/notepad'
    VERSION = "${env.BUILD_NUMBER}"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Unit + Integration Tests') {
      steps {
        sh 'mvn -B clean verify'
      }
      post {
        always {
          junit '**/target/surefire-reports/*.xml, **/target/failsafe-reports/*.xml'
        }
      }
    }

    stage('Publish Artifact') {
      steps {
        sh 'mvn -B -DskipTests deploy'
      }
    }

    stage('Build Docker Image') {
      steps {
        sh 'docker build -t $IMAGE_NAME:$VERSION .'
      }
    }

    stage('Push Docker Image') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'
          sh 'docker push $IMAGE_NAME:$VERSION'
        }
      }
    }

    stage('Deploy to Testing') {
      steps {
        sh 'kubectl -n testing set image deployment/notepad app=$IMAGE_NAME:$VERSION'
        sh 'kubectl -n testing rollout status deployment/notepad'
      }
    }

    stage('Acceptance Tests') {
      steps {
        sh 'mvn -Pacceptance test -Dtest.base.url=http://notepad.testing'
      }
    }

    stage('Deploy to Staging') {
      steps {
        sh 'kubectl -n staging set image deployment/notepad app=$IMAGE_NAME:$VERSION'
        sh 'kubectl -n staging rollout status deployment/notepad'
      }
    }

    stage('Smoke Tests') {
      steps {
        sh 'curl -f http://notepad.staging/actuator/health'
      }
    }
  }
}
```

---

## 18. Kubernetes manifests you can start with

### 18.1 MySQL secret

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: mysql-secret
  namespace: testing
stringData:
  MYSQL_ROOT_PASSWORD: root
```

### 18.2 MySQL deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mysql
  namespace: testing
spec:
  replicas: 1
  selector:
    matchLabels:
      app: mysql
  template:
    metadata:
      labels:
        app: mysql
    spec:
      containers:
        - name: mysql
          image: mysql:8
          env:
            - name: MYSQL_DATABASE
              value: notepad
            - name: MYSQL_ROOT_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: mysql-secret
                  key: MYSQL_ROOT_PASSWORD
          ports:
            - containerPort: 3306
```

### 18.3 MySQL service

```yaml
apiVersion: v1
kind: Service
metadata:
  name: mysql
  namespace: testing
spec:
  selector:
    app: mysql
  ports:
    - port: 3306
      targetPort: 3306
```

### 18.4 App configmap

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: notepad-config
  namespace: testing
data:
  SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/notepad
  SPRING_DATASOURCE_USERNAME: root
```

### 18.5 App deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: notepad
  namespace: testing
spec:
  replicas: 2
  selector:
    matchLabels:
      app: notepad
  template:
    metadata:
      labels:
        app: notepad
    spec:
      containers:
        - name: app
          image: youruser/notepad:1.0.0
          ports:
            - containerPort: 8080
          envFrom:
            - configMapRef:
                name: notepad-config
          env:
            - name: SPRING_DATASOURCE_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: mysql-secret
                  key: MYSQL_ROOT_PASSWORD
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

### 18.6 App service

```yaml
apiVersion: v1
kind: Service
metadata:
  name: notepad
  namespace: testing
spec:
  selector:
    app: notepad
  ports:
    - port: 80
      targetPort: 8080
```

---

## 19. Canary release implementation pattern

Canary is one of the key production ideas in the book.

### 19.1 Concept

Run old and new versions side-by-side.

Example traffic progression:

- 95% old / 5% new
- 80% old / 20% new
- 50% old / 50% new
- 0% old / 100% new

### 19.2 Practical implementation options

#### Option A — separate canary deployment

- `notepad-stable`
- `notepad-canary`

Scale replicas proportionally to influence traffic share.

#### Option B — ingress/service mesh weighted routing

- cleaner traffic control
- better observability
- more precise percentages

### 19.3 Basic Kubernetes-style canary with separate deployments

Stable:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: notepad-stable
spec:
  replicas: 9
  selector:
    matchLabels:
      app: notepad
      track: stable
  template:
    metadata:
      labels:
        app: notepad
        track: stable
    spec:
      containers:
        - name: app
          image: youruser/notepad:1.0.0
```

Canary:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: notepad-canary
spec:
  replicas: 1
  selector:
    matchLabels:
      app: notepad
      track: canary
  template:
    metadata:
      labels:
        app: notepad
        track: canary
    spec:
      containers:
        - name: app
          image: youruser/notepad:1.1.0
```

Single service selecting both:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: notepad
spec:
  selector:
    app: notepad
  ports:
    - port: 80
      targetPort: 8080
```

Traffic is distributed roughly by replica count.

### 19.4 Canary checklist

Before increasing traffic:

- rollout healthy
- readiness green
- error rate acceptable
- latency acceptable
- logs normal
- smoke tests pass
- DB migration safe

### 19.5 Rollback rule

If canary is unhealthy:

1. route traffic away
2. scale canary down
3. keep stable serving
4. investigate metrics/logs

---

## 20. Feature flags and A/B tests in practice

### 20.1 Feature flag example

```java
@Service
public class NoteFeatureService {
    @Value("${feature.note-subtitle:false}")
    private boolean subtitleEnabled;

    public boolean subtitleEnabled() {
        return subtitleEnabled;
    }
}
```

Usage:

```java
if (featureService.subtitleEnabled()) {
    // render or process subtitle
}
```

### 20.2 Externalized flag

```yaml
feature:
  note-subtitle: false
```

### 20.3 Better real-world flagging

Prefer a proper flag system when you need:

- gradual rollout
- per-user targeting
- audit trail
- percentage release
- runtime changes without restart

### 20.4 A/B test workflow

1. deploy feature off
2. enable for selected segment
3. collect metrics
4. compare control vs variant
5. keep winner
6. remove dead code after conclusion

### 20.5 Important rule

Do not confuse canary with A/B testing.

- canary = release risk control
- A/B = product experiment

---

## 21. Observability and operations checklist

The book highlights that canary only makes sense if you can observe the system well.

### Minimum dashboards

- request rate
- error rate
- latency percentiles
- CPU and memory
- pod restarts
- DB health
- deployment events

### Minimum logs

- application startup
- migration status
- failed requests
- external dependency failures

### Minimum alerts

- health endpoint failures
- elevated 5xx rate
- restart loops
- database unavailability
- long latency spike

---

## 22. Practical commands cheat sheet

### Git

```bash
git checkout -b feature/add-note-subtitle
git add .
git commit -m "Add note subtitle"
git push origin feature/add-note-subtitle
```

### Maven

```bash
mvn clean test
mvn clean verify
mvn clean package
mvn -B -s settings.xml clean deploy
```

### Docker

```bash
docker build -t youruser/notepad:dev .
docker run -p 8080:8080 youruser/notepad:dev
docker push youruser/notepad:dev
```

### Kubernetes

```bash
kubectl apply -f k8s/
kubectl get all -n testing
kubectl rollout status deployment/notepad -n testing
kubectl logs deployment/notepad -n testing
kubectl delete all -l env=testing -n testing
```

### Jenkins pipeline outcome checks

- build logs
- test report archive
- artifact archive
- image pushed
- rollout status successful

---

## 23. From scratch implementation plan

This is the shortest complete path to reproduce the book’s outcomes.

### Step 1 — create app
- build a minimal Spring Boot CRUD app
- keep one entity, one service, one controller
- add actuator

### Step 2 — add database
- use MySQL
- add Flyway
- create first migration
- verify app starts against empty DB and self-migrates

### Step 3 — add tests
- unit tests for domain logic
- integration tests for service and repository
- controller tests for API
- one Selenium acceptance flow
- one smoke script
- one Gatling simulation

### Step 4 — add Maven discipline
- split unit and integration tests
- configure profiles if needed
- make `mvn clean verify` pass locally and in CI

### Step 5 — publish artifacts
- set up Artifactory or equivalent artifact repo
- configure `distributionManagement`
- publish snapshots from mainline and releases from tags

### Step 6 — containerize
- create Dockerfile
- build image locally
- run against MySQL
- push image to registry

### Step 7 — define Kubernetes runtime
- namespace
- secret
- configmap
- mysql deployment/service
- app deployment/service
- health probes

### Step 8 — build CI pipeline
- checkout
- test
- package
- publish artifact
- build/push image

### Step 9 — add test environment automation
- deploy app + DB to testing namespace
- run acceptance tests against service DNS
- destroy/recreate env per run if needed

### Step 10 — add release flow
- version release
- tag source
- publish immutable release artifact/image

### Step 11 — add staging
- deploy release image to staging
- run smoke tests
- optionally run performance tests

### Step 12 — add canary + production
- create stable and canary deployments
- start with low traffic share
- monitor
- promote or rollback

### Step 13 — add ChatOps
- Slack notifications for build and deployment status
- links to logs and dashboards

---

## 24. What to memorize vs what to look up

### Memorize

- CI vs CD vs continuous deployment
- deployed vs released
- canary vs A/B vs feature flags
- Maven lifecycle basics
- Docker image vs container
- Kubernetes Pod / Service / Deployment / ConfigMap / Secret / probes
- Jenkinsfile structure
- why small tasks and automation are essential

### Look up when needed

- detailed plugin syntax
- exact Maven XML options
- exact Kubernetes API fields
- advanced Selenium/Grid setup
- advanced Gatling DSL
- advanced Jenkins shared library patterns

---

## 25. “Mastery checklist” for this note

You have mastered the material when you can do these without guessing:

- explain why large tasks break CI/CD
- describe the difference between deploy and release
- choose between canary, feature flag, and A/B test
- write a small Spring Boot CRUD service
- add Flyway migrations safely
- separate unit and integration tests in Maven
- package and publish artifacts
- build and push Docker images
- write a Jenkinsfile for build/test/deploy
- define basic Kubernetes manifests
- configure readiness and liveness probes correctly
- deploy testing, staging, canary, and production flows
- explain rollback strategy

---

## 26. Interview-style rapid fire review

### What is CI?
Frequent integration to mainline with automated verification.

### What is CD?
Mainline is always in deployable state and can be released safely on demand.

### What is the difference between CD and continuous deployment?
CD needs a human to trigger production deployment; continuous deployment does not.

### Why are automated tests required?
Because you cannot safely automate release of code you do not trust.

### Why is Flyway important?
Because schema change must be versioned and reproducible like code.

### Why Docker?
Because packaging runtime consistently reduces environment drift.

### Why Kubernetes?
Because it provides declarative deployment, scaling, discovery, and self-healing.

### Why Jenkins agents on Kubernetes?
Because each build gets a clean, scalable, disposable execution environment.

### Why canary?
Because it reduces release risk by exposing a new version to limited traffic first.

### Why feature flags?
Because they separate deployment from release.

---

## 27. Final condensed summary

The book’s real lesson is bigger than any tool.

A working delivery system emerges when you combine:

- Agile planning with small tasks
- XP testing discipline
- CI on every change
- reproducible builds with Maven
- schema versioning with Flyway
- immutable packaging with Docker
- pipeline orchestration with Jenkins
- scalable runtime and agents on Kubernetes
- safe rollout patterns like canary
- release control with feature flags

If you build these pieces in order, you do not just “know the tools.”
You get a repeatable system that can take a code change from commit to production safely.

---

## 28. 7-day study plan using this note

### Day 1
Read sections 1–5 and explain CI/CD concepts aloud.

### Day 2
Build the Spring Boot sample app from sections 6–7.

### Day 3
Implement tests from section 8.

### Day 4
Set up Maven + Flyway from sections 9–10.

### Day 5
Containerize with section 11.

### Day 6
Create Jenkins pipeline from sections 12 and 17.

### Day 7
Deploy to Kubernetes with sections 14, 18, and 19.

---

## 29. Personal implementation notes template

Copy this into your own repo and fill it in.

```md
# My CD stack

## App
- Language:
- Framework:
- Build tool:
- DB:

## Tests
- Unit:
- Integration:
- Acceptance:
- Smoke:
- Performance:

## Artifact + image
- Artifact repo:
- Container registry:

## CI/CD
- CI server:
- Pipeline file:
- Secrets strategy:

## Kubernetes
- Namespaces:
- ConfigMaps:
- Secrets:
- Deployments:
- Services:
- Ingress:

## Release strategy
- Branch model:
- Release versioning:
- Canary method:
- Rollback method:
- Feature flag system:
```

