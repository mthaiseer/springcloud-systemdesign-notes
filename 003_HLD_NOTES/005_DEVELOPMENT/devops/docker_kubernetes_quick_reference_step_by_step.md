# Docker + Kubernetes Quick Reference for Building and Scaling a Spring Boot Application

This note is a **separate hands-on reference** for learning **Docker and Kubernetes from basics to expert level** while building a **Spring Boot application from scratch**.

It is designed so you can:
- start from zero,
- understand **what each command does**,
- add pieces one by one,
- containerize your app,
- run it locally,
- deploy it to Kubernetes,
- and scale toward **high-throughput production systems**.

## Important reality check

A reference guide alone cannot guarantee **50k requests/second**. That depends on:
- application code efficiency,
- database design,
- caching,
- payload size,
- JVM tuning,
- CPU and memory,
- network,
- number of replicas,
- autoscaling strategy,
- and traffic shape.

What this guide **does** give you is the step-by-step path and command reference to build a **production-capable Spring Boot service** using Docker and Kubernetes.

---

# 1. Learning path

Use this order:

1. Build a basic Spring Boot app.
2. Run it locally without containers.
3. Add Docker.
4. Run the app as a container.
5. Add a database container if needed.
6. Learn core Docker commands.
7. Add Kubernetes manifests.
8. Deploy to a cluster.
9. Add health checks.
10. Add config and secrets.
11. Scale horizontally.
12. Add rolling updates and canary patterns.
13. Add metrics, load testing, and autoscaling.
14. Optimize for higher throughput.

---

# 2. Mental model

## Docker in one sentence
Docker packages your application and its runtime into an **image**, then runs that image as a **container**.

## Kubernetes in one sentence
Kubernetes runs and manages containers across machines, handling **deployment, scaling, service discovery, restarts, and rollouts**.

## Core flow

```text
Spring Boot code
   -> jar
   -> Docker image
   -> Docker container
   -> Kubernetes Pod
   -> Kubernetes Deployment
   -> Kubernetes Service
   -> scalable production system
```

---

# 3. Build a minimal Spring Boot app first

Before Docker or Kubernetes, make sure the app works locally.

## Example controller

```java
package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/healthz")
    public String health() {
        return "ok";
    }
}
```

## Why this matters
- `/hello` proves the app works.
- `/healthz` gives you a simple endpoint for probes and quick tests.

## Run locally

```bash
./mvnw spring-boot:run
```

What it does:
- compiles your code,
- starts the Spring Boot application,
- serves traffic on the configured port.

## Test locally

```bash
curl http://localhost:8080/hello
curl http://localhost:8080/healthz
```

---

# 4. Docker basics you must master

## 4.1 Key terms

### Image
A read-only package with your app and runtime.

### Container
A running instance of an image.

### Dockerfile
A recipe to build an image.

### Registry
A place where images are stored, like Docker Hub or a private registry.

### Tag
A version label for an image, such as `myapp:1.0.0` or `myapp:latest`.

---

# 5. Dockerfile: beginner version

## Simple Dockerfile for Spring Boot

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## What each line does

### `FROM eclipse-temurin:21-jre`
Starts from a base image with Java 21 runtime.

### `WORKDIR /app`
Sets `/app` as the working directory inside the container.

### `COPY target/demo-0.0.1-SNAPSHOT.jar app.jar`
Copies your built jar from your machine into the image.

### `EXPOSE 8080`
Documents that the app listens on port 8080.

### `ENTRYPOINT ["java", "-jar", "app.jar"]`
Defines the command that runs when the container starts.

---

# 6. Docker build and run: step by step

## Step 1: package the app

```bash
./mvnw clean package
```

What it does:
- cleans old build artifacts,
- compiles the code,
- runs tests,
- creates the jar in `target/`.

## Step 2: build the Docker image

```bash
docker build -t demo-app:1.0.0 .
```

What it does:
- reads the `Dockerfile` in the current directory,
- builds an image,
- tags it as `demo-app:1.0.0`.

## Step 3: see your image

```bash
docker images
```

What it does:
- lists Docker images stored locally.

## Step 4: run the container

```bash
docker run --name demo-app -p 8080:8080 demo-app:1.0.0
```

What it does:
- starts a container named `demo-app`,
- maps your machine port `8080` to container port `8080`,
- runs the image `demo-app:1.0.0`.

## Step 5: test it

```bash
curl http://localhost:8080/hello
```

## Step 6: stop it

```bash
docker stop demo-app
```

What it does:
- gracefully stops the running container.

## Step 7: remove it

```bash
docker rm demo-app
```

What it does:
- deletes the stopped container.

---

# 7. Docker quick reference with easy explanations

## Images

```bash
docker images
```
Lists local images.

```bash
docker rmi demo-app:1.0.0
```
Deletes an image.

```bash
docker pull nginx:latest
```
Downloads an image from a registry.

## Containers

```bash
docker ps
```
Shows running containers.

```bash
docker ps -a
```
Shows all containers, including stopped ones.

```bash
docker logs demo-app
```
Shows container logs.

```bash
docker exec -it demo-app sh
```
Opens a shell inside the running container.

```bash
docker inspect demo-app
```
Shows detailed JSON metadata about the container.

```bash
docker restart demo-app
```
Restarts the container.

## Build and tagging

```bash
docker build -t demo-app:dev .
```
Builds and tags an image.

```bash
docker tag demo-app:dev myrepo/demo-app:dev
```
Adds another tag, usually for a registry.

## Push to registry

```bash
docker login
```
Authenticates to the registry.

```bash
docker push myrepo/demo-app:dev
```
Pushes the image to the registry.

## Cleanup

```bash
docker system df
```
Shows disk usage.

```bash
docker system prune
```
Removes unused containers, networks, and dangling images.

Use carefully.

---

# 8. Better Dockerfile: production-friendly version

## Multi-stage build

```dockerfile
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -q clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]
```

## Why it is better
- builds the jar in one stage,
- copies only the final artifact to runtime image,
- creates a smaller final image,
- avoids shipping Maven into production image.

---

# 9. Docker Compose for local development

Use this when your Spring Boot app needs MySQL, Redis, or other services locally.

## Example `docker-compose.yml`

```yaml
version: '3.9'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/appdb
      SPRING_DATASOURCE_USERNAME: appuser
      SPRING_DATASOURCE_PASSWORD: apppass
    depends_on:
      - db

  db:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: appdb
      MYSQL_USER: appuser
      MYSQL_PASSWORD: apppass
      MYSQL_ROOT_PASSWORD: rootpass
    ports:
      - "3306:3306"
```

## Commands

```bash
docker compose up --build
```
Builds images if needed and starts services.

```bash
docker compose down
```
Stops and removes the services.

---

# 10. Docker networking, ports, volumes, env vars

## Ports

```bash
docker run -p 8080:8080 demo-app:1.0.0
```
Maps host port 8080 to container port 8080.

Format:

```text
hostPort:containerPort
```

## Environment variables

```bash
docker run -e SPRING_PROFILES_ACTIVE=prod demo-app:1.0.0
```
Passes environment variables into the container.

## Volumes

```bash
docker run -v $(pwd)/logs:/app/logs demo-app:1.0.0
```
Mounts a local directory into the container.

Use volumes for:
- local dev data,
- logs,
- persistent storage in simple setups.

---

# 11. Docker best practices for Spring Boot

## Do
- use small base images,
- use multi-stage builds,
- pin versions,
- externalize config,
- run one process per container,
- keep containers stateless,
- expose health endpoints,
- use non-root user when possible.

## Avoid
- baking secrets into images,
- storing database data inside app containers,
- relying on `latest` tag in production,
- making very large images,
- writing state to container filesystem.

---

# 12. Kubernetes basics you must master

## Core objects

### Pod
The smallest deployable unit. Usually runs one container.

### Deployment
Manages Pods and rolling updates.

### Service
Gives stable networking to a set of Pods.

### ConfigMap
Stores non-secret config.

### Secret
Stores sensitive config.

### Namespace
Logical isolation inside the cluster.

### Ingress
Routes external HTTP traffic into cluster services.

### HPA
Horizontal Pod Autoscaler. Scales Pods based on metrics.

---

# 13. Kubernetes mental model

```text
Docker image
   -> Pod
   -> Deployment manages Pods
   -> Service exposes Pods
   -> Ingress exposes Service externally
   -> HPA scales Deployment
```

---

# 14. First Kubernetes manifests

## 14.1 Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: demo-app
spec:
  replicas: 2
  selector:
    matchLabels:
      app: demo-app
  template:
    metadata:
      labels:
        app: demo-app
    spec:
      containers:
        - name: demo-app
          image: myrepo/demo-app:1.0.0
          ports:
            - containerPort: 8080
```

## What it does
- runs 2 replicas,
- ensures Pods match label `app: demo-app`,
- starts your container in each Pod.

## 14.2 Service

```yaml
apiVersion: v1
kind: Service
metadata:
  name: demo-app-service
spec:
  selector:
    app: demo-app
  ports:
    - port: 80
      targetPort: 8080
  type: ClusterIP
```

## What it does
- creates a stable internal address,
- forwards traffic from service port 80 to pod port 8080,
- load balances across matching Pods.

---

# 15. Kubernetes commands: beginner to expert quick reference

## Cluster info

```bash
kubectl cluster-info
```
Shows where the control plane and core services are running.

```bash
kubectl version
```
Shows client and server version.

## Namespaces

```bash
kubectl get namespaces
```
Lists namespaces.

```bash
kubectl create namespace demo
```
Creates namespace `demo`.

```bash
kubectl config set-context --current --namespace=demo
```
Sets your current default namespace.

## Apply resources

```bash
kubectl apply -f deployment.yaml
```
Creates or updates resources from a file.

```bash
kubectl apply -f k8s/
```
Applies all manifests in a directory.

## View resources

```bash
kubectl get pods
```
Lists Pods.

```bash
kubectl get deployments
```
Lists Deployments.

```bash
kubectl get services
```
Lists Services.

```bash
kubectl get all
```
Shows common resources like Pods, Services, Deployments, ReplicaSets.

```bash
kubectl get pods -o wide
```
Shows more details, such as node and IP.

```bash
kubectl get pod demo-app-xxxx -o yaml
```
Shows full YAML for a resource.

## Describe resources

```bash
kubectl describe pod demo-app-xxxx
```
Shows detailed human-readable information, events, and errors.

```bash
kubectl describe deployment demo-app
```
Useful for rollout and replica issues.

## Logs

```bash
kubectl logs demo-app-xxxx
```
Shows logs from a Pod.

```bash
kubectl logs -f demo-app-xxxx
```
Streams logs live.

```bash
kubectl logs demo-app-xxxx -c demo-app
```
Gets logs from a specific container in a multi-container Pod.

## Exec into Pod

```bash
kubectl exec -it demo-app-xxxx -- sh
```
Opens a shell inside the container.

Useful for:
- checking files,
- testing DNS,
- making HTTP requests,
- debugging runtime issues.

## Delete resources

```bash
kubectl delete -f deployment.yaml
```
Deletes resources defined in the file.

```bash
kubectl delete pod demo-app-xxxx
```
Deletes one Pod. Deployment usually recreates it.

## Rollouts

```bash
kubectl rollout status deployment/demo-app
```
Watches rollout progress.

```bash
kubectl rollout history deployment/demo-app
```
Shows rollout revisions.

```bash
kubectl rollout undo deployment/demo-app
```
Rolls back to previous revision.

## Scaling

```bash
kubectl scale deployment demo-app --replicas=5
```
Sets the Deployment to 5 replicas.

## Port forwarding

```bash
kubectl port-forward service/demo-app-service 8080:80
```
Exposes the Service on your local machine temporarily.

Now you can call:

```bash
curl http://localhost:8080/hello
```

## Watch changes live

```bash
kubectl get pods -w
```
Keeps watching Pod status changes.

---

# 16. Deploy your first Spring Boot app to Kubernetes

## Step 1: build and push image

```bash
./mvnw clean package
docker build -t myrepo/demo-app:1.0.0 .
docker push myrepo/demo-app:1.0.0
```

## Step 2: create namespace

```bash
kubectl create namespace demo
kubectl config set-context --current --namespace=demo
```

## Step 3: apply Deployment and Service

```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

## Step 4: confirm Pods are running

```bash
kubectl get pods
kubectl get deployments
kubectl get services
```

## Step 5: check rollout

```bash
kubectl rollout status deployment/demo-app
```

## Step 6: test the app

```bash
kubectl port-forward service/demo-app-service 8080:80
curl http://localhost:8080/hello
```

---

# 17. Add health checks the right way

Health checks are critical for reliable production systems.

## Spring Boot Actuator dependency

Add Actuator so Kubernetes can check health.

## Example probe configuration

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: demo-app
spec:
  replicas: 2
  selector:
    matchLabels:
      app: demo-app
  template:
    metadata:
      labels:
        app: demo-app
    spec:
      containers:
        - name: demo-app
          image: myrepo/demo-app:1.0.0
          ports:
            - containerPort: 8080
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

## What they do

### Readiness probe
Determines when the Pod is ready to receive traffic.

If readiness fails:
- Pod stays running,
- but Service stops sending traffic to it.

### Liveness probe
Determines whether the app is alive.

If liveness fails:
- Kubernetes restarts the container.

---

# 18. ConfigMaps and Secrets

## ConfigMap example

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: demo-app-config
data:
  SPRING_PROFILES_ACTIVE: prod
  LOG_LEVEL: INFO
```

## Secret example

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: demo-app-secret
type: Opaque
stringData:
  DB_USERNAME: appuser
  DB_PASSWORD: supersecret
```

## Use them in Deployment

```yaml
envFrom:
  - configMapRef:
      name: demo-app-config
  - secretRef:
      name: demo-app-secret
```

## Why this matters
- config stays outside the image,
- secrets are not hardcoded,
- the same image can be reused across environments.

---

# 19. Resources: CPU and memory

Always define resource requests and limits for serious environments.

## Example

```yaml
resources:
  requests:
    cpu: "500m"
    memory: "512Mi"
  limits:
    cpu: "1"
    memory: "1Gi"
```

## What this means

### Requests
The minimum resources Kubernetes reserves for the container.

### Limits
The maximum resources the container may use.

## Why it matters for high throughput
Without proper resources:
- Pods get throttled,
- JVM may be memory-starved,
- autoscaling signals become misleading,
- noisy-neighbor effects increase.

---

# 20. Scaling basics

## Horizontal scaling
Add more Pod replicas.

```bash
kubectl scale deployment demo-app --replicas=10
```

Best when the app is:
- stateless,
- horizontally scalable,
- not dependent on local filesystem state.

## Vertical scaling
Increase CPU and memory per Pod.

Useful when:
- each request is CPU-heavy,
- app uses large heap,
- thread pools are saturating.

In practice, you usually need both.

---

# 21. Horizontal Pod Autoscaler

## Example HPA

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: demo-app-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: demo-app
  minReplicas: 3
  maxReplicas: 30
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 65
```

## What it does
- watches CPU usage,
- increases replicas when average utilization rises above target,
- reduces replicas when traffic falls.

## Useful commands

```bash
kubectl get hpa
kubectl describe hpa demo-app-hpa
```

---

# 22. Ingress for external traffic

## Example Ingress

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: demo-app-ingress
spec:
  rules:
    - host: demo.local
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: demo-app-service
                port:
                  number: 80
```

## What it does
- routes external HTTP traffic,
- maps hostname and path to your Service.

---

# 23. Rolling updates and zero-downtime deployment

## Deployment strategy example

```yaml
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxUnavailable: 0
    maxSurge: 1
```

## What it means
- `maxUnavailable: 0` means no existing Pod should go down before a new one is ready.
- `maxSurge: 1` means one extra Pod can be created during rollout.

This is the base for zero-downtime rollout.

## Update image

```bash
kubectl set image deployment/demo-app demo-app=myrepo/demo-app:1.0.1
```

## Check rollout

```bash
kubectl rollout status deployment/demo-app
```

## Roll back if needed

```bash
kubectl rollout undo deployment/demo-app
```

---

# 24. Canary release: simple way to think about it

Canary means releasing a new version to a small percentage first.

## Simple model
- version 1.0.0 serves most traffic,
- version 1.1.0 serves a small portion,
- monitor errors, latency, CPU, memory,
- then increase traffic gradually.

## In plain Kubernetes
Basic canary can be approximated with:
- two Deployments,
- same Service selector strategy or separate traffic routing,
- replica ratio control.

For real canary at scale, tools like these are common:
- service mesh,
- ingress controller with weighted routing,
- progressive delivery tools.

---

# 25. What you need to reach 1k to 50k requests/second

This is the most important practical section.

## 25.1 Application design
Your Spring Boot app should be:
- stateless,
- fast at request processing,
- using connection pools correctly,
- not blocking unnecessarily,
- not doing heavy synchronous work on request path.

## 25.2 Database strategy
Your database is often the real bottleneck.

Use:
- connection pooling,
- proper indexes,
- query optimization,
- read replicas where useful,
- caching for hot reads,
- async processing where possible.

## 25.3 Cache strategy
For high throughput, use cache for:
- repeated reads,
- expensive computed responses,
- session replacement if needed.

## 25.4 JVM and Spring Boot tuning
Look at:
- heap sizing,
- garbage collection,
- thread pools,
- Tomcat thread config,
- request timeouts,
- compression,
- serialization cost.

## 25.5 Container tuning
Right-size:
- CPU requests and limits,
- memory requests and limits,
- number of replicas,
- startup time.

## 25.6 Kubernetes tuning
Use:
- HPA,
- PodDisruptionBudget,
- topology spread,
- anti-affinity,
- readiness probes,
- controlled rollout policies.

## 25.7 Load test every change
Never guess throughput.
Measure it.

---

# 26. Load testing workflow

## Simple workflow
1. Deploy baseline version.
2. Run load test.
3. Record p50, p95, p99 latency.
4. Check CPU, memory, GC, database load.
5. Tune one thing at a time.
6. Repeat.

## Basic targets to watch
- requests/sec,
- error rate,
- p95 latency,
- pod CPU,
- pod memory,
- restart count,
- DB CPU and connection usage.

---

# 27. Beginner-to-expert build roadmap

## Level 1: beginner
You should be able to:
- create a Spring Boot REST endpoint,
- package a jar,
- write a Dockerfile,
- build and run a container,
- push to a registry.

## Level 2: early intermediate
You should be able to:
- write Deployment and Service manifests,
- deploy to Kubernetes,
- inspect Pods and logs,
- add readiness and liveness probes,
- use ConfigMaps and Secrets.

## Level 3: intermediate
You should be able to:
- scale replicas,
- do rolling updates,
- roll back,
- expose traffic via Ingress,
- debug failed Pods and CrashLoopBackOff.

## Level 4: advanced
You should be able to:
- size resources correctly,
- add autoscaling,
- design stateless services,
- optimize startup and memory,
- load test and tune throughput bottlenecks.

## Level 5: expert
You should be able to:
- plan canary rollout,
- design resilient production deployments,
- combine app tuning, infra scaling, and observability,
- understand when the bottleneck is app, DB, network, or orchestration.

---

# 28. Common Docker mistakes

## Mistake: using `latest` everywhere
Why bad:
- unpredictable deployments,
- hard rollbacks.

## Mistake: baking secrets into image
Why bad:
- security risk,
- hard to rotate.

## Mistake: huge images
Why bad:
- slower builds,
- slower deploys,
- slower autoscaling.

## Mistake: stateful app container
Why bad:
- scaling becomes hard,
- restarts lose data.

---

# 29. Common Kubernetes mistakes

## Mistake: no readiness probe
Result:
- traffic hits Pod before app is ready.

## Mistake: no resource requests
Result:
- unstable scheduling and noisy-neighbor issues.

## Mistake: no limits and no monitoring
Result:
- unpredictable performance.

## Mistake: scaling app without scaling database or cache
Result:
- app scales, but backend collapses.

## Mistake: treating Kubernetes as magic
Reality:
- bad app architecture stays bad at scale.

---

# 30. Troubleshooting quick guide

## Pod not starting
Use:

```bash
kubectl get pods
kubectl describe pod <pod-name>
kubectl logs <pod-name>
```

Look for:
- image pull errors,
- bad command,
- missing env vars,
- failed probes,
- OOMKilled.

## Service not routing traffic
Check:

```bash
kubectl get service
kubectl get endpoints
kubectl get pods --show-labels
```

Most common issue:
- Service selector does not match Pod labels.

## App starts then restarts repeatedly
Check:
- liveness probe path,
- memory limit,
- DB connectivity,
- startup time too slow.

## Throughput too low
Check:
- pod CPU saturation,
- thread pool limits,
- DB bottleneck,
- garbage collection,
- network latency,
- serialization overhead,
- insufficient replicas.

---

# 31. Minimal production-ready example

## Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: demo-app
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 0
      maxSurge: 1
  selector:
    matchLabels:
      app: demo-app
  template:
    metadata:
      labels:
        app: demo-app
    spec:
      containers:
        - name: demo-app
          image: myrepo/demo-app:1.0.0
          ports:
            - containerPort: 8080
          envFrom:
            - configMapRef:
                name: demo-app-config
            - secretRef:
                name: demo-app-secret
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
          resources:
            requests:
              cpu: "500m"
              memory: "512Mi"
            limits:
              cpu: "1"
              memory: "1Gi"
```

## Service

```yaml
apiVersion: v1
kind: Service
metadata:
  name: demo-app-service
spec:
  selector:
    app: demo-app
  ports:
    - port: 80
      targetPort: 8080
  type: ClusterIP
```

## HPA

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: demo-app-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: demo-app
  minReplicas: 3
  maxReplicas: 20
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 65
```

---

# 32. Command cheat sheet

## Docker core

```bash
./mvnw clean package
docker build -t myrepo/demo-app:1.0.0 .
docker run --name demo-app -p 8080:8080 myrepo/demo-app:1.0.0
docker ps
docker logs demo-app
docker exec -it demo-app sh
docker stop demo-app
docker rm demo-app
docker push myrepo/demo-app:1.0.0
```

## Kubernetes core

```bash
kubectl create namespace demo
kubectl config set-context --current --namespace=demo
kubectl apply -f k8s/
kubectl get all
kubectl describe deployment demo-app
kubectl logs -f deployment/demo-app
kubectl rollout status deployment/demo-app
kubectl scale deployment demo-app --replicas=5
kubectl port-forward service/demo-app-service 8080:80
kubectl rollout undo deployment/demo-app
```

---

# 33. What to do next in the right order

## Phase 1
Build a tiny Spring Boot REST app with one endpoint.

## Phase 2
Containerize it with Docker.

## Phase 3
Run it with Docker and inspect logs, exec, ports, env vars.

## Phase 4
Push image to registry.

## Phase 5
Deploy to Kubernetes with Deployment and Service.

## Phase 6
Add readiness and liveness probes.

## Phase 7
Add ConfigMap, Secret, and resources.

## Phase 8
Add HPA and load test.

## Phase 9
Tune app, DB, and replicas.

## Phase 10
Add canary and advanced production rollout.

---

# 34. Final advice

If your goal is to build a Spring Boot application **from scratch** and grow it toward **high scalability**, focus on this order of mastery:

1. app correctness,
2. container correctness,
3. deployment correctness,
4. observability,
5. scaling,
6. performance tuning,
7. safe rollout strategies.

Do not jump to Kubernetes tuning before you can:
- build the jar,
- build the image,
- run the image,
- explain every Dockerfile line,
- explain every field in a Deployment and Service.

That foundation is what turns Docker and Kubernetes from “confusing tools” into “predictable production systems.”
