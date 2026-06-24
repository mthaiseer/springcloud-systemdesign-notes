# 049_Kubernetes_Deployment.md
# MiniURLShortener — Kubernetes Deployment

> Core mental model: **A Kubernetes Deployment is the desired-state controller that keeps your Spring Boot application running as the correct number of healthy Pods, updates them safely, and replaces failed Pods automatically.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Docker Compose vs Kubernetes](#4-docker-compose-vs-kubernetes)
- [5. Kubernetes Objects Mental Model](#5-kubernetes-objects-mental-model)
- [6. Pod Mental Model](#6-pod-mental-model)
- [7. Deployment Mental Model](#7-deployment-mental-model)
- [8. ReplicaSet Mental Model](#8-replicaset-mental-model)
- [9. Service Mental Model](#9-service-mental-model)
- [10. ConfigMap Mental Model](#10-configmap-mental-model)
- [11. Secret Mental Model](#11-secret-mental-model)
- [12. Namespace Mental Model](#12-namespace-mental-model)
- [13. MiniURLShortener Kubernetes Architecture](#13-miniurlshortener-kubernetes-architecture)
- [14. Deployment YAML](#14-deployment-yaml)
- [15. Service YAML](#15-service-yaml)
- [16. ConfigMap YAML](#16-configmap-yaml)
- [17. Secret YAML](#17-secret-yaml)
- [18. Complete Local Kubernetes Manifests](#18-complete-local-kubernetes-manifests)
- [19. Environment Variables In Pods](#19-environment-variables-in-pods)
- [20. Readiness Probe](#20-readiness-probe)
- [21. Liveness Probe](#21-liveness-probe)
- [22. Startup Probe](#22-startup-probe)
- [23. Resource Requests And Limits](#23-resource-requests-and-limits)
- [24. Rolling Update Mental Model](#24-rolling-update-mental-model)
- [25. Rollback Mental Model](#25-rollback-mental-model)
- [26. Scaling Replicas](#26-scaling-replicas)
- [27. Image Pull Policy](#27-image-pull-policy)
- [28. Applying The Manifests](#28-applying-the-manifests)
- [29. Common kubectl Commands](#29-common-kubectl-commands)
- [30. Step-by-Step Dry Runs](#30-step-by-step-dry-runs)
- [31. Internal Execution Walkthrough](#31-internal-execution-walkthrough)
- [32. Debugging Mindset](#32-debugging-mindset)
- [33. Production Failure Stories](#33-production-failure-stories)
- [34. Common Mistakes](#34-common-mistakes)
- [35. Interview-Ready Explanation](#35-interview-ready-explanation)
- [36. Senior Engineer Checklist](#36-senior-engineer-checklist)
- [37. One-Page Cheat Sheet](#37-one-page-cheat-sheet)
- [38. One Picture To Remember](#38-one-picture-to-remember)

---

## 1. Why This Exists

In the previous Docker chapters, MiniURLShortener became containerized.

We learned:

```text
Dockerfile:
    build one application image

Docker Compose:
    run app + Postgres + Redis + Kafka locally
```

But real production needs more than running containers.

Production asks:

```text
What if a container dies?
What if we need 5 replicas?
What if we deploy a new version?
What if the new version is broken?
What if the app starts but is not ready?
What if one Pod gets too much memory?
What if traffic must be load balanced?
What if config differs between dev/staging/prod?
```

Kubernetes answers these with controllers and desired state.

Instead of manually saying:

```text
Start this container.
```

You say:

```text
I want 3 healthy replicas of this application.
Expose them through a stable service.
Use this config.
Use these secrets.
Update safely.
```

Kubernetes continuously works to make reality match that desired state.

ASCII:

```text
You declare desired state
          |
          v
+----------------------+
| Kubernetes Control   |
| Plane                |
+----------------------+
          |
          v
Actual cluster state moves toward desired state
```

Production memory:

```text
Docker runs containers.
Kubernetes operates applications.
```

---

## 2. The One Core Mental Model

Kubernetes is a desired-state machine.

You tell it what you want.

Kubernetes keeps checking:

```text
desired state == actual state?
```

If not, it fixes it.

ASCII:

```text
Desired State:
    3 Pods of mini-url-shortener

Actual State:
    2 Pods running

Kubernetes:
    create 1 more Pod
```

Loop:

```text
+-------------------+
| Desired State     |
| replicas = 3      |
+-------------------+
          |
          v
+-------------------+
| Controller Checks |
+-------------------+
          |
          v
+-------------------+
| Actual State      |
| replicas = 2      |
+-------------------+
          |
          v
+-------------------+
| Reconcile         |
| create 1 Pod      |
+-------------------+
```

One-line memory:

```text
Kubernetes is not command execution; it is continuous reconciliation.
```

For MiniURLShortener:

```text
Deployment says:
    Run 3 Pods of app image.

Service says:
    Give stable network name and load balance to Pods.

ConfigMap says:
    Provide non-secret config.

Secret says:
    Provide sensitive config.

Probes say:
    Decide whether Pod is ready/alive.
```

---

## 3. Problem Statement

Deploy MiniURLShortener Spring Boot app to Kubernetes.

We need:

```text
1. Namespace for isolation.
2. Deployment for app Pods.
3. Service for stable access.
4. ConfigMap for app config.
5. Secret for credentials.
6. Readiness probe.
7. Liveness probe.
8. Startup probe.
9. CPU/memory requests and limits.
10. Rolling update strategy.
11. kubectl commands for deployment/debugging.
```

Assumptions:

```text
1. App Docker image already exists.
2. Postgres/Redis/Kafka may run inside cluster or externally.
3. This chapter focuses on app deployment.
4. Database/Kafka production HA is separate topic.
```

Out of scope:

```text
1. Helm deep dive.
2. Ingress deep dive.
3. HPA deep dive.
4. StatefulSet for Postgres.
5. Service mesh.
6. Full observability stack.
```

This chapter teaches the Deployment foundation.

---

## 4. Docker Compose vs Kubernetes

Docker Compose:

```text
Local multi-container environment.
Simple YAML.
Great for development.
```

Kubernetes:

```text
Production-grade orchestration.
Self-healing.
Rolling updates.
Service discovery.
Scaling.
Scheduling.
Resource management.
```

Table:

```text
+----------------------+-----------------------------+-----------------------------+
| Need                 | Docker Compose              | Kubernetes                  |
+----------------------+-----------------------------+-----------------------------+
| Local development    | Excellent                   | Possible but heavier        |
| Self-healing         | Limited                     | Strong                      |
| Rolling update       | Limited                     | Built-in                    |
| Scaling              | Basic                       | Strong                      |
| Scheduling           | Single machine mostly       | Multi-node                  |
| Service discovery    | Service name                | Service + DNS               |
| Config management    | env/env_file                | ConfigMap/Secret            |
| Production standard  | Usually no                  | Usually yes                 |
+----------------------+-----------------------------+-----------------------------+
```

ASCII:

```text
Docker Compose:
    One developer machine
    app + db + redis + kafka

Kubernetes:
    Cluster of nodes
    controllers keep apps healthy
```

Mental transition:

```text
Compose service -> Kubernetes Deployment + Service
Compose env     -> ConfigMap + Secret
Compose network -> Kubernetes Service DNS
Compose volume  -> Kubernetes PersistentVolumeClaim
```

---

## 5. Kubernetes Objects Mental Model

Kubernetes is built from objects.

Important objects for this chapter:

```text
Namespace
Deployment
ReplicaSet
Pod
Service
ConfigMap
Secret
```

ASCII:

```text
Namespace
   |
   +-- Deployment
   |      |
   |      +-- ReplicaSet
   |              |
   |              +-- Pod
   |              +-- Pod
   |              +-- Pod
   |
   +-- Service
   |
   +-- ConfigMap
   |
   +-- Secret
```

Simple meanings:

```text
Namespace:
    logical environment boundary

Pod:
    smallest running unit

Deployment:
    manages app rollout and replicas

ReplicaSet:
    keeps correct number of Pods

Service:
    stable network address for Pods

ConfigMap:
    non-secret configuration

Secret:
    sensitive configuration
```

Kubernetes objects are usually written as YAML manifests.

---

## 6. Pod Mental Model

A Pod is the smallest deployable unit in Kubernetes.

For MiniURLShortener:

```text
one Pod usually contains one Spring Boot container
```

ASCII:

```text
+-----------------------------+
| Pod                         |
|                             |
|  +-----------------------+  |
|  | Spring Boot Container |  |
|  | port 8080             |  |
|  +-----------------------+  |
|                             |
| shared network namespace    |
| shared volumes if any       |
+-----------------------------+
```

Pod gets:

```text
IP address
container(s)
environment variables
volumes
resource limits
health probes
```

Important:

```text
Pods are disposable.
Pod IPs are not stable.
```

If Pod dies:

```text
Deployment creates replacement Pod.
New Pod gets new IP.
```

Therefore clients should not call Pod IP directly.

They call Service.

---

## 7. Deployment Mental Model

Deployment is the object you normally create for stateless apps.

It manages:

```text
replicas
rollout
rollback
Pod template
ReplicaSet creation
```

ASCII:

```text
Deployment: mini-url-shortener
        |
        v
ReplicaSet: version 1
        |
        +-- Pod 1
        +-- Pod 2
        +-- Pod 3
```

When deploying new image:

```text
Deployment creates new ReplicaSet.
Gradually shifts Pods from old version to new version.
```

ASCII:

```text
Before:
Deployment
  |
  +-- ReplicaSet v1
        +-- Pod v1
        +-- Pod v1
        +-- Pod v1

During rollout:
Deployment
  |
  +-- ReplicaSet v1
  |     +-- Pod v1
  |     +-- Pod v1
  |
  +-- ReplicaSet v2
        +-- Pod v2

After:
Deployment
  |
  +-- ReplicaSet v2
        +-- Pod v2
        +-- Pod v2
        +-- Pod v2
```

One-line memory:

```text
Deployment is the production controller for stateless application Pods.
```

---

## 8. ReplicaSet Mental Model

ReplicaSet keeps a fixed number of matching Pods running.

If desired replicas:

```text
3
```

Actual Pods:

```text
2
```

ReplicaSet creates one more.

ASCII:

```text
Desired replicas = 3

Actual:
[Pod][Pod]

ReplicaSet:
create new Pod

Final:
[Pod][Pod][Pod]
```

Usually you do not create ReplicaSet manually.

Deployment creates and manages ReplicaSets.

Think:

```text
Deployment = rollout brain
ReplicaSet = replica counter
Pod = running unit
```

ASCII:

```text
Deployment
   |
   v
ReplicaSet
   |
   v
Pods
```

---

## 9. Service Mental Model

Pods are temporary.

Service gives stable access.

Without Service:

```text
Client must know Pod IPs.
Pod IPs change.
Bad.
```

With Service:

```text
Client calls stable service name.
Service load balances to healthy Pods.
```

ASCII:

```text
Client
  |
  v
Service: mini-url-shortener-service
  |
  +-- Pod A
  +-- Pod B
  +-- Pod C
```

Service selects Pods using labels.

Example:

```yaml
selector:
  app: mini-url-shortener
```

Pods have labels:

```yaml
labels:
  app: mini-url-shortener
```

ASCII:

```text
Service selector:
    app=mini-url-shortener
          |
          v
Find Pods with matching label
          |
          v
Load balance traffic
```

Service types:

```text
ClusterIP:
    internal cluster access

NodePort:
    expose on node port

LoadBalancer:
    cloud load balancer

ExternalName:
    DNS alias
```

For internal app-to-app communication:

```text
ClusterIP
```

For external public access:

```text
Ingress or LoadBalancer
```

---

## 10. ConfigMap Mental Model

ConfigMap stores non-secret configuration.

Examples:

```text
SPRING_PROFILES_ACTIVE
SPRING_DATA_REDIS_HOST
SPRING_KAFKA_BOOTSTRAP_SERVERS
LOG_LEVEL
FEATURE_FLAG
```

ASCII:

```text
ConfigMap
  |
  v
Pod environment variables
  |
  v
Spring Boot application config
```

Example:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: miniurl-config
data:
  SPRING_PROFILES_ACTIVE: "k8s"
  SPRING_DATA_REDIS_HOST: "redis"
  SPRING_KAFKA_BOOTSTRAP_SERVERS: "kafka:9092"
```

Use ConfigMap for:

```text
non-sensitive config
environment names
service hostnames
feature flags
timeouts
log levels
```

Do not use ConfigMap for:

```text
passwords
tokens
private keys
database credentials
```

Use Secret for sensitive data.

---

## 11. Secret Mental Model

Secret stores sensitive configuration.

Examples:

```text
database password
API key
JWT signing secret
OAuth client secret
```

ASCII:

```text
Secret
  |
  v
Pod env variable
  |
  v
Spring Boot reads password
```

Example:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: miniurl-secret
type: Opaque
stringData:
  SPRING_DATASOURCE_USERNAME: "miniurl"
  SPRING_DATASOURCE_PASSWORD: "miniurlpass"
```

`stringData` is convenient because you can write normal strings.

Kubernetes stores Secret data encoded, but:

```text
base64 is not encryption.
```

Production secrets should be protected by:

```text
RBAC
encryption at rest
external secret manager
sealed secrets / external secrets operator
least privilege
```

Do not commit real production secrets to Git.

---

## 12. Namespace Mental Model

Namespace creates logical separation.

Examples:

```text
miniurl-dev
miniurl-staging
miniurl-prod
```

ASCII:

```text
Cluster
  |
  +-- namespace miniurl-dev
  |      +-- app
  |      +-- config
  |
  +-- namespace miniurl-prod
         +-- app
         +-- config
```

Create namespace:

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: miniurl
```

Benefits:

```text
1. Logical isolation.
2. Easier cleanup.
3. Separate RBAC.
4. Separate naming.
5. Environment separation.
```

Command:

```bash
kubectl get pods -n miniurl
```

---

## 13. MiniURLShortener Kubernetes Architecture

Target architecture:

```text
External user
   |
   v
Ingress / LoadBalancer later
   |
   v
Service
   |
   v
Deployment Pods
   |
   +--> Postgres
   +--> Redis
   +--> Kafka
```

ASCII:

```text
KUBERNETES CLUSTER

+------------------------------------------------------+
| Namespace: miniurl                                   |
|                                                      |
|  +-------------------------+                         |
|  | Service                 |                         |
|  | miniurl-service         |                         |
|  +-----------+-------------+                         |
|              |                                       |
|              v                                       |
|  +-------------------------+                         |
|  | Deployment              |                         |
|  | replicas: 3             |                         |
|  +-----------+-------------+                         |
|              |                                       |
|      +-------+-------+-------+                       |
|      v               v       v                       |
|   +------+        +------+  +------+                 |
|   | Pod1 |        | Pod2 |  | Pod3 |                 |
|   +------+        +------+  +------+                 |
|      |               |        |                       |
|      +-----> postgres/redis/kafka services           |
|                                                      |
|  ConfigMap + Secret supply environment variables     |
+------------------------------------------------------+
```

For local Kubernetes, dependencies may be:

```text
inside same cluster
or external Docker Compose services
or managed cloud services
```

In production, database is often managed:

```text
AWS RDS
Cloud SQL
Azure Database
managed Kafka
managed Redis
```

---

## 14. Deployment YAML

Create:

```text
k8s/miniurl-deployment.yml
```

Deployment:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mini-url-shortener
  namespace: miniurl
  labels:
    app: mini-url-shortener
spec:
  replicas: 3
  selector:
    matchLabels:
      app: mini-url-shortener
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 1
      maxSurge: 1
  template:
    metadata:
      labels:
        app: mini-url-shortener
    spec:
      containers:
        - name: mini-url-shortener
          image: mini-url-shortener:049
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
          envFrom:
            - configMapRef:
                name: miniurl-config
            - secretRef:
                name: miniurl-secret
          resources:
            requests:
              cpu: "250m"
              memory: "512Mi"
            limits:
              cpu: "1000m"
              memory: "1Gi"
          startupProbe:
            httpGet:
              path: /actuator/health
              port: 8080
            failureThreshold: 30
            periodSeconds: 5
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 10
            periodSeconds: 5
            failureThreshold: 3
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 10
            failureThreshold: 3
```

Key parts:

```text
replicas:
    number of Pods

selector:
    which Pods Deployment owns

template:
    Pod template

envFrom:
    load ConfigMap and Secret as env vars

resources:
    scheduling and protection

probes:
    health lifecycle checks

strategy:
    rolling update behavior
```

---

## 15. Service YAML

Create:

```text
k8s/miniurl-service.yml
```

Service:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: mini-url-shortener-service
  namespace: miniurl
  labels:
    app: mini-url-shortener
spec:
  type: ClusterIP
  selector:
    app: mini-url-shortener
  ports:
    - name: http
      port: 8080
      targetPort: 8080
```

Meaning:

```text
Service name:
    mini-url-shortener-service

Service port:
    8080

Pod target port:
    8080

Selector:
    send traffic to Pods with app=mini-url-shortener
```

ASCII:

```text
mini-url-shortener-service:8080
          |
          v
selector app=mini-url-shortener
          |
          v
Pod1:8080, Pod2:8080, Pod3:8080
```

Inside cluster, other apps can call:

```text
http://mini-url-shortener-service.miniurl.svc.cluster.local:8080
```

Usually shorter in same namespace:

```text
http://mini-url-shortener-service:8080
```

For local testing without Ingress:

```bash
kubectl port-forward svc/mini-url-shortener-service 8080:8080 -n miniurl
```

Then:

```text
http://localhost:8080
```

---

## 16. ConfigMap YAML

Create:

```text
k8s/miniurl-configmap.yml
```

ConfigMap:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: miniurl-config
  namespace: miniurl
data:
  SPRING_PROFILES_ACTIVE: "k8s"
  SERVER_PORT: "8080"

  SPRING_DATASOURCE_URL: "jdbc:postgresql://postgres-service:5432/miniurl"

  SPRING_DATA_REDIS_HOST: "redis-service"
  SPRING_DATA_REDIS_PORT: "6379"

  SPRING_KAFKA_BOOTSTRAP_SERVERS: "kafka-service:9092"

  MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE: "health,info,metrics,prometheus"
  MANAGEMENT_ENDPOINT_HEALTH_PROBES_ENABLED: "true"
  MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS: "always"

  JAVA_OPTS: "-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"
```

Important:

```text
ConfigMap values are strings.
```

Spring Boot env mapping:

```text
MANAGEMENT_ENDPOINT_HEALTH_PROBES_ENABLED
    -> management.endpoint.health.probes.enabled
```

For Kubernetes probes in Spring Boot:

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
```

This exposes:

```text
/actuator/health/liveness
/actuator/health/readiness
```

---

## 17. Secret YAML

Create:

```text
k8s/miniurl-secret.yml
```

Secret:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: miniurl-secret
  namespace: miniurl
type: Opaque
stringData:
  SPRING_DATASOURCE_USERNAME: "miniurl"
  SPRING_DATASOURCE_PASSWORD: "miniurlpass"
```

Warning:

```text
Use dummy local secrets only.
Do not commit real production secrets.
```

Production alternatives:

```text
External Secrets Operator
Vault
AWS Secrets Manager
GCP Secret Manager
Azure Key Vault
Sealed Secrets
```

ASCII:

```text
Secret
  |
  v
Pod env
  |
  v
Spring DataSource password
```

Remember:

```text
Secret is safer than ConfigMap, but not magically secure.
RBAC and encryption matter.
```

---

## 18. Complete Local Kubernetes Manifests

File layout:

```text
k8s/
  00-namespace.yml
  01-configmap.yml
  02-secret.yml
  03-deployment.yml
  04-service.yml
```

Namespace:

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: miniurl
```

Apply all:

```bash
kubectl apply -f k8s/
```

Order matters less when using apply, but namespace should exist before namespaced resources.

Safe order:

```bash
kubectl apply -f k8s/00-namespace.yml
kubectl apply -f k8s/01-configmap.yml
kubectl apply -f k8s/02-secret.yml
kubectl apply -f k8s/03-deployment.yml
kubectl apply -f k8s/04-service.yml
```

ASCII:

```text
Namespace
   |
   v
ConfigMap + Secret
   |
   v
Deployment
   |
   v
Service
```

---

## 19. Environment Variables In Pods

Deployment:

```yaml
envFrom:
  - configMapRef:
      name: miniurl-config
  - secretRef:
      name: miniurl-secret
```

This loads all key-value pairs as environment variables.

ASCII:

```text
ConfigMap keys
Secret keys
     |
     v
Pod environment variables
     |
     v
Spring Boot property binding
```

Inside container:

```bash
kubectl exec -it deploy/mini-url-shortener -n miniurl -- printenv
```

You should see:

```text
SPRING_PROFILES_ACTIVE=k8s
SPRING_DATASOURCE_URL=...
SPRING_DATA_REDIS_HOST=...
```

Warning:

```text
Anyone with permission to exec into Pod can inspect env vars.
```

This is one reason production secret RBAC matters.

---

## 20. Readiness Probe

Readiness asks:

```text
Should this Pod receive traffic?
```

If readiness fails:

```text
Pod stays running.
Service stops sending traffic to it.
```

ASCII:

```text
Pod running
   |
   v
Readiness check
   |
   +-- pass --> receive traffic
   |
   +-- fail --> removed from Service endpoints
```

YAML:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5
  failureThreshold: 3
```

Use readiness for:

```text
DB connection readiness
Redis readiness
Kafka readiness if critical
application warmup
```

Important:

```text
Readiness failure does not restart the container.
```

It only controls traffic.

Production example:

```text
App starts but Hikari pool cannot connect to DB.
Readiness fails.
Kubernetes does not send traffic yet.
```

---

## 21. Liveness Probe

Liveness asks:

```text
Is this container alive or stuck beyond repair?
```

If liveness fails:

```text
Kubernetes restarts the container.
```

ASCII:

```text
Container running
   |
   v
Liveness check
   |
   +-- pass --> keep running
   |
   +-- fail --> restart container
```

YAML:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
  failureThreshold: 3
```

Use liveness for:

```text
deadlock
app process stuck
internal fatal state
```

Do not make liveness depend on external DB too aggressively.

Bad:

```text
DB temporary down -> liveness fails -> all Pods restart
```

Better:

```text
DB down -> readiness fails
Pod stays alive and can recover
```

Rule:

```text
Readiness protects traffic.
Liveness restarts broken process.
```

---

## 22. Startup Probe

Startup probe asks:

```text
Has the app finished starting?
```

While startup probe is running:

```text
liveness and readiness checks are delayed
```

This is useful for slow Spring Boot startup.

YAML:

```yaml
startupProbe:
  httpGet:
    path: /actuator/health
    port: 8080
  failureThreshold: 30
  periodSeconds: 5
```

Meaning:

```text
Allow up to 30 * 5 = 150 seconds for startup.
```

ASCII:

```text
Container starts
    |
    v
Startup probe
    |
    +-- failing allowed during startup
    |
    +-- passes
            |
            v
     liveness/readiness begin
```

Without startup probe:

```text
Liveness may kill slow-starting app before it finishes booting.
```

This causes CrashLoopBackOff.

---

## 23. Resource Requests And Limits

Requests:

```text
Minimum resources Pod asks scheduler to reserve.
```

Limits:

```text
Maximum resources Pod is allowed to use.
```

YAML:

```yaml
resources:
  requests:
    cpu: "250m"
    memory: "512Mi"
  limits:
    cpu: "1000m"
    memory: "1Gi"
```

Meaning:

```text
CPU request 250m = 0.25 CPU core
CPU limit 1000m = 1 CPU core
Memory request 512Mi
Memory limit 1Gi
```

ASCII:

```text
Node Capacity
+--------------------------------+
| Pod A request 512Mi            |
| Pod B request 512Mi            |
| Pod C request 512Mi            |
| free capacity                  |
+--------------------------------+
```

Scheduler uses requests.

Runtime enforces limits.

If memory exceeds limit:

```text
Pod may be OOMKilled.
```

If CPU exceeds limit:

```text
Container may be throttled.
```

Production rule:

```text
Always set requests.
Set limits carefully.
Monitor real usage.
```

For Java:

```text
Container memory limit must align with JVM heap settings.
```

---

## 24. Rolling Update Mental Model

Rolling update replaces old Pods gradually.

Strategy:

```yaml
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxUnavailable: 1
    maxSurge: 1
```

Meaning for 3 replicas:

```text
maxUnavailable: 1
    at most 1 Pod can be unavailable

maxSurge: 1
    at most 1 extra Pod above desired replicas
```

ASCII:

```text
Initial:
[v1][v1][v1]

Step 1:
[v1][v1][v1][v2]   surge +1

Step 2:
[v1][v1][v2]       remove one old

Step 3:
[v1][v1][v2][v2]   add new

Step 4:
[v1][v2][v2]       remove old

Final:
[v2][v2][v2]
```

Readiness is critical.

Kubernetes only sends traffic to ready Pods.

Bad rollout:

```text
New Pod starts but fails readiness.
Rollout pauses or becomes unhealthy.
Old Pods may remain serving.
```

This protects availability.

---

## 25. Rollback Mental Model

If new version is bad:

```bash
kubectl rollout undo deployment/mini-url-shortener -n miniurl
```

Kubernetes rolls back to previous ReplicaSet.

ASCII:

```text
Deployment
  |
  +-- ReplicaSet v1  old but kept
  |
  +-- ReplicaSet v2  broken current

rollback
  |
  v
scale v1 up
scale v2 down
```

Check rollout history:

```bash
kubectl rollout history deployment/mini-url-shortener -n miniurl
```

Check status:

```bash
kubectl rollout status deployment/mini-url-shortener -n miniurl
```

Production practice:

```text
Use immutable image tags.
Avoid latest.
```

Bad:

```text
image: mini-url-shortener:latest
```

Better:

```text
image: registry.example.com/mini-url-shortener:1.4.2
image: registry.example.com/mini-url-shortener:git-sha
```

Why?

```text
Rollback needs clear previous image versions.
```

---

## 26. Scaling Replicas

Scale manually:

```bash
kubectl scale deployment/mini-url-shortener --replicas=5 -n miniurl
```

Check:

```bash
kubectl get pods -n miniurl
```

ASCII:

```text
Before:
[Pod][Pod][Pod]

Scale to 5:
[Pod][Pod][Pod][Pod][Pod]
```

Deployment updates desired state:

```text
replicas = 5
```

ReplicaSet creates more Pods.

Remember:

```text
Scaling stateless app is easy.
Scaling database is not simple.
```

For MiniURLShortener app:

```text
stateless app Pods can scale horizontally
```

But shared dependencies must handle load:

```text
Postgres connections
Redis capacity
Kafka producers/consumers
external APIs
```

Scaling app from 3 to 30 can break DB if Hikari pool is too large.

Example:

```text
30 Pods * 20 Hikari connections = 600 DB connections
```

Senior thinking:

```text
Scaling app requires scaling dependency capacity too.
```

---

## 27. Image Pull Policy

Image pull policy controls when kubelet pulls image.

Common values:

```text
Always
IfNotPresent
Never
```

YAML:

```yaml
imagePullPolicy: IfNotPresent
```

Meaning:

```text
Use local image if present.
Pull only if missing.
```

For local Minikube/Kind:

```text
IfNotPresent can be useful.
```

For production:

```text
Use immutable tags.
IfNotPresent is usually okay with unique tags.
```

With `latest`, Kubernetes defaults to:

```text
Always
```

But avoid `latest`.

Bad:

```yaml
image: mini-url-shortener:latest
```

Why bad:

```text
Unclear what version is running.
Rollback is confusing.
Cache behavior can surprise you.
```

Better:

```yaml
image: mini-url-shortener:2026-06-24-abc123
```

---

## 28. Applying The Manifests

Apply:

```bash
kubectl apply -f k8s/
```

Check namespace:

```bash
kubectl get ns
```

Check Pods:

```bash
kubectl get pods -n miniurl
```

Check Deployment:

```bash
kubectl get deploy -n miniurl
```

Check Service:

```bash
kubectl get svc -n miniurl
```

Watch rollout:

```bash
kubectl rollout status deployment/mini-url-shortener -n miniurl
```

Port forward:

```bash
kubectl port-forward svc/mini-url-shortener-service 8080:8080 -n miniurl
```

Test:

```bash
curl http://localhost:8080/actuator/health
```

Delete:

```bash
kubectl delete -f k8s/
```

Or delete namespace:

```bash
kubectl delete namespace miniurl
```

Warning:

```text
Deleting namespace deletes all resources inside it.
```

---

## 29. Common kubectl Commands

List Pods:

```bash
kubectl get pods -n miniurl
```

Wide view:

```bash
kubectl get pods -o wide -n miniurl
```

Describe Pod:

```bash
kubectl describe pod <pod-name> -n miniurl
```

Logs:

```bash
kubectl logs <pod-name> -n miniurl
```

Follow logs:

```bash
kubectl logs -f <pod-name> -n miniurl
```

Logs by Deployment:

```bash
kubectl logs -f deployment/mini-url-shortener -n miniurl
```

Exec shell:

```bash
kubectl exec -it <pod-name> -n miniurl -- sh
```

Get events:

```bash
kubectl get events -n miniurl --sort-by=.lastTimestamp
```

Rollout status:

```bash
kubectl rollout status deployment/mini-url-shortener -n miniurl
```

Rollback:

```bash
kubectl rollout undo deployment/mini-url-shortener -n miniurl
```

Scale:

```bash
kubectl scale deployment/mini-url-shortener --replicas=5 -n miniurl
```

Delete Pod to test self-healing:

```bash
kubectl delete pod <pod-name> -n miniurl
```

---

## 30. Step-by-Step Dry Runs

### Dry Run 1: Pod Dies

Desired state:

```text
replicas = 3
```

Actual:

```text
Pod1 running
Pod2 running
Pod3 crashes
```

Flow:

```text
1. Kubelet notices Pod3 container exited.
2. Kubernetes marks Pod unhealthy/terminated.
3. ReplicaSet sees only 2 healthy Pods.
4. ReplicaSet creates replacement Pod.
5. New Pod gets new IP.
6. Service routes traffic to ready Pods only.
```

ASCII:

```text
Before:
[Pod1][Pod2][Pod3]

Pod3 dies:
[Pod1][Pod2][X]

ReplicaSet:
create Pod4

After:
[Pod1][Pod2][Pod4]
```

---

### Dry Run 2: Rolling Update

Initial image:

```text
mini-url-shortener:1.0
```

Update image:

```bash
kubectl set image deployment/mini-url-shortener \
  mini-url-shortener=mini-url-shortener:1.1 \
  -n miniurl
```

Flow:

```text
1. Deployment detects Pod template changed.
2. New ReplicaSet created.
3. One new Pod v1.1 starts.
4. Readiness passes.
5. One old Pod v1.0 removed.
6. Repeat until all Pods are v1.1.
```

ASCII:

```text
[v1][v1][v1]
[v1][v1][v1][v2]
[v1][v1][v2]
[v1][v2][v2]
[v2][v2][v2]
```

---

### Dry Run 3: New Version Broken

New image:

```text
mini-url-shortener:bad
```

Problem:

```text
App starts but readiness fails.
```

Flow:

```text
1. New Pod created.
2. Container starts.
3. Readiness probe fails.
4. Service does not send traffic to new Pod.
5. Rollout does not fully complete.
6. Old Pods continue serving if available.
```

Debug:

```bash
kubectl rollout status deployment/mini-url-shortener -n miniurl
kubectl describe pod <new-pod> -n miniurl
kubectl logs <new-pod> -n miniurl
```

Rollback:

```bash
kubectl rollout undo deployment/mini-url-shortener -n miniurl
```

---

### Dry Run 4: Readiness Fails But Liveness Passes

Scenario:

```text
DB temporarily unavailable.
```

Readiness:

```text
fails because app cannot serve DB-backed requests.
```

Liveness:

```text
passes because process is not deadlocked.
```

Result:

```text
Pod remains running.
Service removes it from endpoints.
When DB returns, readiness passes.
Traffic resumes.
```

ASCII:

```text
DB down
  |
  v
readiness fail -> no traffic
liveness pass  -> no restart
```

This is healthy behavior.

---

### Dry Run 5: Memory Limit Exceeded

Pod limit:

```text
memory: 1Gi
```

App uses:

```text
1.3Gi
```

Result:

```text
Container OOMKilled.
Pod restarts.
```

Commands:

```bash
kubectl describe pod <pod> -n miniurl
```

Look for:

```text
Reason: OOMKilled
Exit Code: 137
```

Fix:

```text
Tune JVM memory.
Increase memory limit if justified.
Fix memory leak if present.
```

---

## 31. Internal Execution Walkthrough

When you apply Deployment:

```text
1. kubectl sends YAML to API Server.
2. API Server validates and stores desired state in etcd.
3. Deployment controller notices new Deployment.
4. Deployment controller creates ReplicaSet.
5. ReplicaSet controller creates Pods.
6. Scheduler assigns Pods to Nodes.
7. Kubelet on each Node pulls image.
8. Kubelet starts container.
9. Probes begin.
10. Endpoint controller adds ready Pods to Service endpoints.
11. kube-proxy/networking routes traffic to Pods.
```

ASCII:

```text
kubectl apply
    |
    v
API Server
    |
    v
etcd desired state
    |
    v
Deployment Controller
    |
    v
ReplicaSet
    |
    v
Pods
    |
    v
Scheduler -> Node
    |
    v
Kubelet starts container
    |
    v
Service routes to ready Pods
```

This is why Kubernetes is powerful:

```text
Many controllers cooperate to move actual state toward desired state.
```

---

## 32. Debugging Mindset

When deployment fails, ask:

```text
Did YAML apply?
Does namespace exist?
Did Pod get scheduled?
Did image pull succeed?
Did container start?
Did app crash?
Did probes fail?
Are env vars correct?
Can app reach DB/Redis/Kafka?
Are resources too low?
Is Service selector matching Pod labels?
Is port target correct?
```

Debug map:

```text
Pod Pending:
    insufficient CPU/memory
    node selector issue
    PVC issue

ImagePullBackOff:
    image name wrong
    registry auth missing
    tag missing

CrashLoopBackOff:
    app starts then crashes
    config error
    missing env var
    bad DB connection if startup fails hard

Running but not Ready:
    readiness probe failing
    app not ready
    actuator path wrong

Service no traffic:
    selector mismatch
    targetPort wrong
    Pods not ready

OOMKilled:
    memory limit too low
    JVM heap too large
    memory leak
```

Commands:

```bash
kubectl describe pod <pod> -n miniurl
kubectl logs <pod> -n miniurl
kubectl get events -n miniurl --sort-by=.lastTimestamp
kubectl get endpoints -n miniurl
kubectl describe svc mini-url-shortener-service -n miniurl
```

Golden question:

```text
Which Kubernetes object is failing: Pod, Deployment, Service, ConfigMap, Secret, or dependency?
```

---

## 33. Production Failure Stories

### Failure Story 1: Service Selector Mismatch

Deployment labels:

```yaml
app: mini-url-shortener
```

Service selector:

```yaml
app: miniurl
```

Result:

```text
Service has no endpoints.
Traffic fails.
```

Debug:

```bash
kubectl get endpoints -n miniurl
```

Fix:

```text
Make selector match Pod labels.
```

Lesson:

```text
Service does not magically find Pods. Labels connect them.
```

---

### Failure Story 2: Liveness Depends On Database

Liveness endpoint checks DB.

DB has temporary issue.

Result:

```text
All Pods fail liveness.
Kubernetes restarts all Pods.
Traffic gets worse.
```

Fix:

```text
Keep liveness focused on process health.
Put dependency readiness in readiness probe.
```

Lesson:

```text
Readiness removes traffic. Liveness restarts containers.
```

---

### Failure Story 3: Latest Image Tag

Deployment uses:

```yaml
image: mini-url-shortener:latest
```

New image pushed with same tag.

Some nodes pull new image.
Some nodes use old cached image.

Result:

```text
Different Pods run different code unexpectedly.
Rollback unclear.
```

Fix:

```text
Use immutable version tags.
```

Lesson:

```text
latest is not production-friendly.
```

---

### Failure Story 4: No Resource Requests

Pods have no requests.

Scheduler packs too many Pods on one node.

Result:

```text
CPU pressure.
Memory pressure.
Random evictions.
Unstable latency.
```

Fix:

```text
Set CPU/memory requests and limits based on measurements.
```

Lesson:

```text
Scheduler cannot make good decisions without requests.
```

---

### Failure Story 5: App Scaled But DB Dies

App scaled:

```text
3 Pods -> 30 Pods
```

Each Pod:

```text
Hikari max pool = 20
```

Potential DB connections:

```text
30 * 20 = 600
```

DB limit:

```text
200 connections
```

Result:

```text
DB exhausted.
App errors increase.
```

Fix:

```text
Tune Hikari per Pod.
Use PgBouncer.
Scale DB capacity.
Use read replicas for reads.
```

Lesson:

```text
Horizontal app scaling multiplies dependency load.
```

---

## 34. Common Mistakes

### Mistake 1: Confusing Pod and Deployment

Wrong:

```text
Manually create Pods for production app.
```

Correct:

```text
Use Deployment.
```

### Mistake 2: No readiness probe

Wrong:

```text
Traffic sent as soon as container starts.
```

Correct:

```text
Use readiness probe.
```

### Mistake 3: Bad liveness probe

Wrong:

```text
Liveness fails when DB is down.
```

Correct:

```text
DB affects readiness, not basic liveness.
```

### Mistake 4: Service selector mismatch

Wrong:

```text
Service labels do not match Pod labels.
```

Correct:

```text
selector must match template labels.
```

### Mistake 5: Using latest tag

Wrong:

```yaml
image: app:latest
```

Correct:

```yaml
image: app:1.0.7
```

### Mistake 6: No resource requests

Wrong:

```text
No CPU/memory requests.
```

Correct:

```text
Set requests and limits.
```

### Mistake 7: Secrets in ConfigMap

Wrong:

```text
DB password in ConfigMap.
```

Correct:

```text
Use Secret or external secret manager.
```

### Mistake 8: Assuming scaling app scales system

Wrong:

```text
Increase replicas, system handles more traffic automatically.
```

Correct:

```text
Check DB, Redis, Kafka, external APIs, connection pools.
```

---

## 35. Interview-Ready Explanation

If interviewer asks:

```text
How would you deploy your Spring Boot URL shortener to Kubernetes?
```

Strong answer:

```text
I would package the Spring Boot app as a Docker image and deploy it using a Kubernetes
Deployment, not raw Pods. The Deployment would define the desired number of replicas,
the container image, environment configuration from ConfigMaps and Secrets, resource
requests and limits, and health probes. A ReplicaSet created by the Deployment keeps
the desired number of Pods running. A Kubernetes Service with a selector matching the
Pod labels gives the app a stable network endpoint and load balances traffic across
ready Pods. I would use readiness probes to decide whether a Pod should receive
traffic, liveness probes to restart a truly stuck process, and startup probes to avoid
killing slow-starting Spring Boot apps. RollingUpdate strategy would deploy new
versions gradually, and if the rollout fails I would use rollout undo to return to the
previous ReplicaSet. I would avoid latest image tags, set resource requests, keep
secrets out of ConfigMaps, and remember that scaling app Pods also multiplies load on
Postgres, Redis, Kafka, and external services.
```

Why this is strong:

```text
1. Uses Deployment not raw Pods.
2. Explains ReplicaSet.
3. Explains Service and labels.
4. Explains ConfigMap/Secret.
5. Explains readiness/liveness/startup.
6. Explains rolling update and rollback.
7. Mentions resources.
8. Mentions dependency scaling.
```

Senior one-liner:

```text
A Kubernetes Deployment is the desired-state controller that safely runs, scales, updates, and repairs stateless application Pods.
```

---

## 36. Senior Engineer Checklist

Before production deployment:

```text
[ ] App uses Deployment, not raw Pod
[ ] replicas >= 2 for availability
[ ] Service selector matches Pod labels
[ ] readiness probe configured
[ ] liveness probe configured carefully
[ ] startup probe configured for Spring Boot
[ ] resource requests set
[ ] memory limit aligned with JVM settings
[ ] ConfigMap used for non-secret config
[ ] Secret/external manager used for sensitive config
[ ] image tag is immutable
[ ] imagePullPolicy intentional
[ ] rolling update strategy configured
[ ] rollback tested
[ ] logs go to stdout/stderr
[ ] app handles SIGTERM gracefully
[ ] Hikari pool sized per replica count
[ ] dependencies can handle replica count
[ ] kubectl debug commands documented
```

If these are checked, your Kubernetes Deployment is production-shaped.

---

## 37. One-Page Cheat Sheet

```text
Core mental model:
Kubernetes = desired-state reconciliation.

Pod:
smallest running unit

Deployment:
manages stateless app replicas and rollout

ReplicaSet:
keeps correct number of Pods

Service:
stable endpoint and load balancing to Pods

ConfigMap:
non-secret config

Secret:
sensitive config

Namespace:
logical isolation

Probes:
startup   -> app finished starting?
readiness -> should receive traffic?
liveness  -> should restart?

Resources:
requests -> scheduler reservation
limits   -> max allowed usage

Rolling update:
replace old Pods gradually

Rollback:
kubectl rollout undo

Commands:
kubectl apply -f k8s/
kubectl get pods -n miniurl
kubectl describe pod <pod> -n miniurl
kubectl logs -f deployment/mini-url-shortener -n miniurl
kubectl rollout status deployment/mini-url-shortener -n miniurl
kubectl rollout undo deployment/mini-url-shortener -n miniurl
kubectl port-forward svc/mini-url-shortener-service 8080:8080 -n miniurl

Golden rules:
Use Deployment for apps.
Use Service for stable access.
Labels connect Service to Pods.
Readiness protects traffic.
Liveness restarts broken process.
Do not use latest in production.
```

---

## 38. One Picture To Remember

```text
              KUBERNETES DEPLOYMENT MENTAL MODEL

                     "Desired state keeps app alive"

You declare:
    replicas = 3
    image = mini-url-shortener:1.0
    config = ConfigMap + Secret
    probes = readiness/liveness/startup

                         |
                         v
+------------------------------------------------------+
| Deployment                                           |
| desired app rollout                                  |
+-------------------------+----------------------------+
                          |
                          v
+------------------------------------------------------+
| ReplicaSet                                            |
| keeps 3 Pods running                                  |
+-------------+----------------+-----------------------+
              |                |
              v                v
          +-------+        +-------+        +-------+
          | Pod 1 |        | Pod 2 |        | Pod 3 |
          +-------+        +-------+        +-------+
              ^                ^                ^
              |                |                |
              +----------------+----------------+
                               |
                               v
+------------------------------------------------------+
| Service                                              |
| stable DNS + load balancing to ready Pods            |
+------------------------------------------------------+

ConfigMap -> non-secret env
Secret    -> sensitive env
Probes    -> traffic/restart decisions
Resources -> scheduling/runtime safety


FINAL MEMORY:

Deployment owns ReplicaSet.
ReplicaSet owns Pods.
Service finds Pods by labels.
Readiness controls traffic.
Liveness controls restart.
Kubernetes continuously reconciles desired state.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Kubernetes Deployment declares and maintains the desired number of application Pods.
2. Service gives stable networking because Pod IPs are temporary.
3. ConfigMap stores non-secret config, while Secret stores sensitive config.
4. Readiness decides traffic, liveness decides restart, and startup protects slow boot.
5. Rolling updates and rollback let you deploy new versions safely.
```

Next possible chapters:

```text
050_Kubernetes_Service_Ingress.md
051_Kubernetes_ConfigMap_Secret.md
052_Kubernetes_HPA_Resource_Tuning.md
053_CI_CD_Docker_Kubernetes.md
