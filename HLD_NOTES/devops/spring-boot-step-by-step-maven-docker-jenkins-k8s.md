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
