# 054_EKS_Deployment.md
# MiniHighScaleBackend — EKS Deployment

> Core mental model: **EKS deployment is Kubernetes deployment plus AWS production boundaries. Kubernetes decides how Pods run; AWS decides how networking, IAM, load balancing, nodes, scaling, and managed control plane behave.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. EKS vs Self-Managed Kubernetes](#3-eks-vs-self-managed-kubernetes)
- [4. What We Are Deploying](#4-what-we-are-deploying)
- [5. EKS Architecture Mental Model](#5-eks-architecture-mental-model)
- [6. AWS Objects Behind EKS](#6-aws-objects-behind-eks)
- [7. Deployment Flow From Laptop To Pod](#7-deployment-flow-from-laptop-to-pod)
- [8. Container Image Build And Push](#8-container-image-build-and-push)
- [9. ECR Repository](#9-ecr-repository)
- [10. EKS Cluster Creation Mental Model](#10-eks-cluster-creation-mental-model)
- [11. Node Groups](#11-node-groups)
- [12. IAM Roles Mental Model](#12-iam-roles-mental-model)
- [13. Pod Identity And IRSA](#13-pod-identity-and-irsa)
- [14. Kubernetes Manifests](#14-kubernetes-manifests)
- [15. ConfigMap And Secret](#15-configmap-and-secret)
- [16. Service And AWS Load Balancer](#16-service-and-aws-load-balancer)
- [17. Ingress With AWS Load Balancer Controller](#17-ingress-with-aws-load-balancer-controller)
- [18. Spring Boot Production Deployment YAML](#18-spring-boot-production-deployment-yaml)
- [19. Readiness, Liveness, Startup Probes](#19-readiness-liveness-startup-probes)
- [20. Resource Requests And Limits](#20-resource-requests-and-limits)
- [21. HPA Autoscaling](#21-hpa-autoscaling)
- [22. Rolling Update Dry Run](#22-rolling-update-dry-run)
- [23. Database And Cache Connectivity](#23-database-and-cache-connectivity)
- [24. Observability On EKS](#24-observability-on-eks)
- [25. Security Checklist](#25-security-checklist)
- [26. Production Failure Stories](#26-production-failure-stories)
- [27. Debugging Mindset](#27-debugging-mindset)
- [28. Step-by-Step Commands](#28-step-by-step-commands)
- [29. Interview-Ready Explanation](#29-interview-ready-explanation)
- [30. One-Page Cheat Sheet](#30-one-page-cheat-sheet)
- [31. One Picture To Remember](#31-one-picture-to-remember)

---

## 1. Why This Exists

You already understand Docker, Docker Compose, Kubernetes Deployment, Service, Ingress, HPA, RDS, ElastiCache, and MSK. But local Kubernetes and production EKS are not the same game.

Local Kubernetes asks:

```text
Can my container run?
```

EKS asks:

```text
Can my container run safely, privately, observably, scalably, and repeatably inside AWS?
```

A Spring Boot service in production needs more than a Deployment YAML.

It needs:

```text
1. ECR image repository
2. EKS cluster
3. managed worker nodes or Fargate
4. VPC subnets and security groups
5. IAM permissions
6. Service or Ingress load balancer
7. ConfigMap and Secret
8. readiness/liveness probes
9. resource requests and limits
10. autoscaling
11. logs and metrics
12. rollback plan
13. database/cache/message broker access
```

Without this mental model, EKS feels like random AWS magic.

With the right model, EKS becomes simple:

```text
Docker image goes to ECR.
Kubernetes manifest goes to EKS API server.
Scheduler places Pods on worker nodes.
AWS load balancer exposes traffic.
IAM controls AWS access.
CloudWatch/Prometheus shows health.
```

---

## 2. The One Core Mental Model

EKS is a managed Kubernetes control plane connected to AWS infrastructure.

```text
Kubernetes controls containers.
AWS provides the physical world around them.
```

ASCII:

```text
                    Developer / CI/CD
                          |
                          | kubectl apply / helm upgrade
                          v
+-------------------------------------------------------------+
|                       EKS CONTROL PLANE                     |
|                                                             |
|  API Server  Scheduler  Controller Manager  Authenticator   |
|                                                             |
|  Managed by AWS                                             |
+----------------------------+--------------------------------+
                             |
                             | schedules Pods
                             v
+-------------------------------------------------------------+
|                         WORKER NODES                        |
|                                                             |
|  EC2 Node 1              EC2 Node 2              EC2 Node 3  |
|  +---------+             +---------+             +---------+ |
|  | Pods    |             | Pods    |             | Pods    | |
|  | kubelet |             | kubelet |             | kubelet | |
|  +---------+             +---------+             +---------+ |
+----------------------------+--------------------------------+
                             |
                             v
+-------------------------------------------------------------+
|                         AWS SERVICES                        |
| ECR | ALB/NLB | VPC | IAM | RDS | ElastiCache | MSK | CWLogs |
+-------------------------------------------------------------+
```

One-line memory:

```text
EKS is Kubernetes brain managed by AWS, running your Pods on AWS compute, surrounded by AWS networking and IAM.
```

---

## 3. EKS vs Self-Managed Kubernetes

Self-managed Kubernetes means you operate everything.

```text
You manage API server.
You manage etcd.
You manage control plane upgrades.
You manage high availability.
You manage certificates.
```

EKS means AWS manages the Kubernetes control plane.

```text
AWS manages API server availability.
AWS manages etcd durability.
AWS integrates IAM authentication.
AWS exposes managed cluster endpoint.
You manage workloads, nodes, add-ons, scaling, security, deployment quality.
```

But EKS is not zero-ops.

You still own:

```text
1. cluster version upgrades
2. worker node upgrades
3. add-on upgrades
4. application manifests
5. IAM least privilege
6. security groups
7. subnet design
8. resource sizing
9. app observability
10. incident response
```

ASCII:

```text
Self-managed K8s:

You own:
  API server + etcd + scheduler + nodes + apps

EKS:

AWS owns:
  API server + etcd + control plane HA

You own:
  node groups + add-ons + apps + IAM + networking choices
```

Senior mindset:

```text
Managed service reduces undifferentiated control-plane work, not production responsibility.
```

---

## 4. What We Are Deploying

Example service:

```text
MiniURLShortener Spring Boot API
```

Runtime dependencies:

```text
Postgres on RDS
Redis on ElastiCache
Kafka on MSK or external Kafka
Docker image in ECR
Application exposed through ALB Ingress
Logs sent to CloudWatch or OpenTelemetry pipeline
```

Request path:

```text
Client
  -> Route 53 DNS
  -> AWS ALB
  -> Kubernetes Ingress
  -> Kubernetes Service
  -> Spring Boot Pod
  -> RDS / Redis / Kafka
```

ASCII:

```text
User Browser
    |
    v
+-----------+       +----------------+       +----------------+
| Route 53  | ----> | AWS ALB        | ----> | Ingress        |
+-----------+       +----------------+       +----------------+
                                                        |
                                                        v
                                             +---------------------+
                                             | K8s Service         |
                                             +---------------------+
                                                        |
                                     +------------------+------------------+
                                     |                  |                  |
                                     v                  v                  v
                               +-----------+      +-----------+      +-----------+
                               | Pod API 1 |      | Pod API 2 |      | Pod API 3 |
                               +-----------+      +-----------+      +-----------+
                                     |                  |                  |
                                     +------------------+------------------+
                                                        |
                             +--------------------------+-----------------------+
                             |                          |                       |
                             v                          v                       v
                          RDS Postgres            ElastiCache Redis             MSK
```

---

## 5. EKS Architecture Mental Model

There are two planes:

```text
1. Control plane
2. Data plane
```

Control plane:

```text
API server
scheduler
controller manager
etcd
AWS IAM authenticator integration
```

Data plane:

```text
EC2 worker nodes
kubelet
container runtime
Pods
Services
Ingress controller
DaemonSets
```

ASCII:

```text
                    CONTROL PLANE
             +------------------------+
             | Desired state lives    |
             | here                   |
             |                        |
             | Deployment says:       |
             | replicas = 3           |
             +-----------+------------+
                         |
                         | reconcile
                         v
                    DATA PLANE
             +------------------------+
             | Real containers run    |
             | here                   |
             |                        |
             | Pod 1 Pod 2 Pod 3      |
             +------------------------+
```

Production thinking:

```text
Control plane stores intent.
Data plane executes intent.
Controllers continuously close the gap between desired and actual state.
```

Example:

```text
Desired: 3 Pods
Actual: 2 Pods because one crashed
Controller: creates replacement Pod
Scheduler: chooses a node
Kubelet: starts container
```

---

## 6. AWS Objects Behind EKS

When you deploy to EKS, many AWS objects are involved.

```text
EKS Cluster
VPC
Public subnets
Private subnets
Route tables
NAT gateway
Security groups
IAM roles
Node group
Auto Scaling Group
Launch template
ECR repository
ALB or NLB
CloudWatch log groups
```

ASCII:

```text
AWS Account
  |
  +-- VPC
  |    |
  |    +-- Public Subnets
  |    |     +-- ALB
  |    |
  |    +-- Private Subnets
  |          +-- EKS Worker Nodes
  |          +-- Pods
  |
  +-- EKS Control Plane
  |
  +-- IAM
  |    +-- Cluster Role
  |    +-- Node Role
  |    +-- Pod Role
  |
  +-- ECR
  |    +-- Docker images
  |
  +-- RDS / ElastiCache / MSK
```

Key production rule:

```text
Keep worker nodes and databases in private subnets.
Expose only ALB/NLB publicly when required.
```

---

## 7. Deployment Flow From Laptop To Pod

The deployment pipeline has two artifacts:

```text
1. Docker image
2. Kubernetes manifests
```

Flow:

```text
Code
  -> build jar
  -> build Docker image
  -> push image to ECR
  -> update Deployment image tag
  -> apply manifest or Helm chart
  -> EKS creates new ReplicaSet
  -> scheduler places Pods
  -> kubelet pulls image from ECR
  -> readiness probe passes
  -> Service sends traffic
```

ASCII:

```text
Git Commit
   |
   v
CI Pipeline
   |
   +-- mvn test package
   |
   +-- docker build
   |
   +-- docker push to ECR
   |
   +-- kubectl apply / helm upgrade
                  |
                  v
              EKS API Server
                  |
                  v
              Deployment Controller
                  |
                  v
              ReplicaSet
                  |
                  v
              Pods on Nodes
```

Memory:

```text
Image is what to run.
Manifest is how to run it.
EKS is where it runs.
```

---

## 8. Container Image Build And Push

Spring Boot Dockerfile example:

```dockerfile
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/mini-url-shortener.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Better production JVM command:

```dockerfile
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY target/mini-url-shortener.jar app.jar

EXPOSE 8080

ENTRYPOINT [
  "java",
  "-XX:MaxRAMPercentage=75",
  "-XX:+ExitOnOutOfMemoryError",
  "-jar",
  "app.jar"
]
```

Why `MaxRAMPercentage`?

```text
In Kubernetes, memory is limited by cgroup.
The JVM must respect container memory.
Otherwise heap may grow too much and Pod gets OOMKilled.
```

Build:

```bash
mvn clean package -DskipTests

docker build -t mini-url-shortener:1.0.0 .
```

Tag for ECR:

```bash
docker tag mini-url-shortener:1.0.0 \
  123456789012.dkr.ecr.eu-central-1.amazonaws.com/mini-url-shortener:1.0.0
```

Push:

```bash
docker push 123456789012.dkr.ecr.eu-central-1.amazonaws.com/mini-url-shortener:1.0.0
```

Production tag rule:

```text
Avoid deploying :latest.
Use immutable tags: git SHA, release number, or build number.
```

Bad:

```yaml
image: mini-url-shortener:latest
```

Good:

```yaml
image: 123456789012.dkr.ecr.eu-central-1.amazonaws.com/mini-url-shortener:2026-06-24-a1b2c3d
```

---

## 9. ECR Repository

ECR is AWS container registry.

```text
Docker image storage for EKS workloads.
```

Create repository:

```bash
aws ecr create-repository \
  --repository-name mini-url-shortener \
  --region eu-central-1
```

Login Docker to ECR:

```bash
aws ecr get-login-password --region eu-central-1 \
  | docker login --username AWS --password-stdin \
    123456789012.dkr.ecr.eu-central-1.amazonaws.com
```

Mental model:

```text
ECR is like Docker Hub inside your AWS account.
EKS worker node pulls images from ECR using node IAM permissions.
```

ASCII:

```text
CI Pipeline
   |
   | docker push
   v
+-------------------+
| ECR Repository    |
+-------------------+
   ^
   | docker pull
   |
+-------------------+
| EKS Worker Node   |
+-------------------+
```

Common failure:

```text
ImagePullBackOff
```

Possible causes:

```text
wrong image tag
ECR login problem
node role missing ECR pull permission
repository in different region/account
private networking cannot reach ECR endpoint
```

---

## 10. EKS Cluster Creation Mental Model

You can create EKS with:

```text
eksctl
Terraform
AWS CDK
AWS Console
```

For learning, `eksctl` gives quick understanding.

Example config:

```yaml
apiVersion: eksctl.io/v1alpha5
kind: ClusterConfig

metadata:
  name: mini-prod-eks
  region: eu-central-1
  version: "1.30"

vpc:
  nat:
    gateway: Single

managedNodeGroups:
  - name: app-nodes
    instanceType: t3.medium
    desiredCapacity: 2
    minSize: 2
    maxSize: 5
    privateNetworking: true
```

Create:

```bash
eksctl create cluster -f eks-cluster.yaml
```

What happens internally:

```text
1. EKS control plane is created.
2. VPC/subnets/security groups are created or attached.
3. IAM roles are created or attached.
4. Managed node group creates EC2 instances.
5. Nodes join the cluster.
6. kubectl context is configured.
```

ASCII:

```text
eksctl create cluster
       |
       v
+---------------------+
| EKS Control Plane   |
+---------------------+
       |
       v
+---------------------+
| Managed Node Group  |
+---------------------+
       |
       v
+---------------------+
| EC2 Worker Nodes    |
+---------------------+
       |
       v
+---------------------+
| Ready Kubernetes    |
+---------------------+
```

Production note:

```text
For real teams, Terraform/CDK is preferred so infrastructure is versioned and reviewable.
```

---

## 11. Node Groups

Worker nodes are where Pods run.

EKS common compute choices:

```text
1. Managed node groups
2. Self-managed node groups
3. Fargate profiles
4. EKS Auto Mode where available
```

For your high-scale Spring Boot backend, start with managed node groups.

Managed node group benefits:

```text
AWS manages node provisioning integration.
AWS creates Auto Scaling Group.
AWS helps with node updates.
Simpler than self-managed nodes.
```

Node group mental model:

```text
A node group is a pool of similar EC2 machines for Kubernetes Pods.
```

ASCII:

```text
EKS Cluster
  |
  +-- NodeGroup: app-on-demand
  |      +-- m6i.large
  |      +-- m6i.large
  |
  +-- NodeGroup: app-spot
  |      +-- m6i.large spot
  |      +-- m5.large spot
  |
  +-- NodeGroup: system
         +-- t3.medium
```

Production layout:

```text
system node group:
    CoreDNS, metrics-server, controllers

app on-demand node group:
    stable critical APIs

app spot node group:
    async workers, tolerant workloads
```

Taints/tolerations can keep system components separate:

```text
system nodes should not be filled by heavy app Pods.
```

---

## 12. IAM Roles Mental Model

EKS uses IAM in three major places:

```text
1. Cluster IAM role
2. Node IAM role
3. Pod/application IAM role
```

Cluster role:

```text
Allows EKS control plane to manage AWS resources needed by the cluster.
```

Node role:

```text
Attached to EC2 worker nodes.
Allows kubelet/node to talk to AWS APIs and pull images from ECR.
```

Pod role:

```text
Attached to Kubernetes ServiceAccount through EKS Pod Identity or IRSA.
Allows a specific app Pod to access AWS services.
```

ASCII:

```text
+-------------------+        +---------------------+
| EKS Control Plane | -----> | Cluster IAM Role    |
+-------------------+        +---------------------+

+-------------------+        +---------------------+
| EC2 Worker Node   | -----> | Node IAM Role       |
+-------------------+        +---------------------+

+-------------------+        +---------------------+
| App Pod           | -----> | Pod IAM Role        |
+-------------------+        +---------------------+
```

Bad production pattern:

```text
Give node role broad S3/RDS/MSK permissions.
Then every Pod on that node can potentially access too much.
```

Good production pattern:

```text
Give each workload its own IAM role through ServiceAccount identity.
```

---

## 13. Pod Identity And IRSA

A Spring Boot Pod may need AWS access:

```text
S3 upload
Secrets Manager
SQS
DynamoDB
MSK IAM auth
CloudWatch
```

Do not put AWS access keys in Kubernetes Secrets.

Use workload identity.

Two common EKS options:

```text
1. EKS Pod Identity
2. IAM Roles for Service Accounts, also called IRSA
```

Mental model:

```text
Kubernetes ServiceAccount becomes the identity of the Pod.
AWS maps that identity to an IAM role.
AWS SDK inside the Pod receives temporary credentials.
```

ASCII:

```text
Spring Boot Pod
    |
    v
Kubernetes ServiceAccount: mini-url-sa
    |
    v
EKS identity mapping
    |
    v
IAM Role: mini-url-s3-role
    |
    v
Temporary AWS credentials
    |
    v
S3 / Secrets Manager / MSK
```

ServiceAccount example:

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: mini-url-shortener-sa
  namespace: mini-prod
```

Deployment references it:

```yaml
spec:
  template:
    spec:
      serviceAccountName: mini-url-shortener-sa
```

Rule:

```text
Node role is for node infrastructure.
Pod role is for application permissions.
```

---

## 14. Kubernetes Manifests

Minimum production-shaped objects:

```text
Namespace
ServiceAccount
ConfigMap
Secret
Deployment
Service
Ingress
HPA
PodDisruptionBudget
```

ASCII:

```text
Namespace: mini-prod
   |
   +-- ServiceAccount
   +-- ConfigMap
   +-- Secret
   +-- Deployment
   |      +-- ReplicaSet
   |             +-- Pods
   |
   +-- Service
   |
   +-- Ingress
   |
   +-- HPA
   |
   +-- PDB
```

Why namespace?

```text
Separate environments and policies.
```

Example:

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: mini-prod
```

Apply:

```bash
kubectl apply -f namespace.yaml
```

---

## 15. ConfigMap And Secret

ConfigMap stores non-sensitive configuration.

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: mini-url-config
  namespace: mini-prod
data:
  SPRING_PROFILES_ACTIVE: "prod"
  SERVER_PORT: "8080"
  DB_HOST: "mini-prod-db.xxxxxx.eu-central-1.rds.amazonaws.com"
  DB_NAME: "miniurl"
  REDIS_HOST: "mini-prod-redis.xxxxxx.cache.amazonaws.com"
```

Secret stores sensitive values.

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: mini-url-secret
  namespace: mini-prod
type: Opaque
stringData:
  DB_USERNAME: "miniurl_app"
  DB_PASSWORD: "replace-me"
```

Better production option:

```text
Use AWS Secrets Manager + External Secrets Operator.
```

Why?

```text
Kubernetes Secrets are base64-encoded, not magically encrypted at the app level.
Secrets Manager improves rotation and central management.
```

Deployment environment:

```yaml
envFrom:
  - configMapRef:
      name: mini-url-config
  - secretRef:
      name: mini-url-secret
```

Mental model:

```text
ConfigMap changes behavior.
Secret proves access.
```

---

## 16. Service And AWS Load Balancer

Kubernetes Service gives stable access to Pods.

```text
Pods are temporary.
Service is stable.
```

ClusterIP service:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: mini-url-service
  namespace: mini-prod
spec:
  type: ClusterIP
  selector:
    app: mini-url-shortener
  ports:
    - name: http
      port: 80
      targetPort: 8080
```

ASCII:

```text
Ingress / Internal Client
        |
        v
+---------------------+
| Service ClusterIP   |
+---------------------+
        |
        +--------+--------+--------+
        |        |        |
        v        v        v
      Pod 1    Pod 2    Pod 3
```

LoadBalancer service can create AWS NLB/ELB directly.

But for HTTP APIs, prefer Ingress with AWS Load Balancer Controller because it gives:

```text
host/path routing
TLS termination
ALB integration
multiple services behind one ALB
health checks
annotations
```

---

## 17. Ingress With AWS Load Balancer Controller

AWS Load Balancer Controller watches Kubernetes Ingress objects and creates AWS ALBs.

Flow:

```text
kubectl apply Ingress
  -> controller sees Ingress
  -> controller calls AWS APIs
  -> AWS creates ALB, listener, target group
  -> ALB routes traffic to Service/Pods
```

ASCII:

```text
Kubernetes Ingress YAML
        |
        v
AWS Load Balancer Controller
        |
        v
AWS ALB + Target Groups
        |
        v
Kubernetes Service
        |
        v
Pods
```

Ingress example:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: mini-url-ingress
  namespace: mini-prod
  annotations:
    kubernetes.io/ingress.class: alb
    alb.ingress.kubernetes.io/scheme: internet-facing
    alb.ingress.kubernetes.io/target-type: ip
    alb.ingress.kubernetes.io/healthcheck-path: /actuator/health/readiness
spec:
  rules:
    - host: api.example.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: mini-url-service
                port:
                  number: 80
```

Production TLS:

```yaml
metadata:
  annotations:
    alb.ingress.kubernetes.io/certificate-arn: arn:aws:acm:eu-central-1:123456789012:certificate/abc
    alb.ingress.kubernetes.io/listen-ports: '[{"HTTP":80},{"HTTPS":443}]'
    alb.ingress.kubernetes.io/ssl-redirect: '443'
```

---

## 18. Spring Boot Production Deployment YAML

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mini-url-shortener
  namespace: mini-prod
  labels:
    app: mini-url-shortener
spec:
  replicas: 3
  revisionHistoryLimit: 5
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 0
      maxSurge: 1
  selector:
    matchLabels:
      app: mini-url-shortener
  template:
    metadata:
      labels:
        app: mini-url-shortener
    spec:
      serviceAccountName: mini-url-shortener-sa
      containers:
        - name: app
          image: 123456789012.dkr.ecr.eu-central-1.amazonaws.com/mini-url-shortener:1.0.0
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
          envFrom:
            - configMapRef:
                name: mini-url-config
            - secretRef:
                name: mini-url-secret
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 15
            periodSeconds: 5
            timeoutSeconds: 2
            failureThreshold: 3
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 60
            periodSeconds: 10
            timeoutSeconds: 2
            failureThreshold: 3
          startupProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            periodSeconds: 5
            failureThreshold: 30
          resources:
            requests:
              cpu: "500m"
              memory: "768Mi"
            limits:
              cpu: "1000m"
              memory: "1024Mi"
```

Why `maxUnavailable: 0`?

```text
During rollout, keep old Pods serving until new Pods are ready.
```

Why `maxSurge: 1`?

```text
Create one extra Pod temporarily during rollout.
```

Why `revisionHistoryLimit`?

```text
Keep old ReplicaSets for rollback, but do not keep unlimited history.
```

---

## 19. Readiness, Liveness, Startup Probes

Probe mental model:

```text
Readiness: should this Pod receive traffic?
Liveness: should this Pod be restarted?
Startup: give slow app enough time to start.
```

ASCII:

```text
Pod starts
   |
   v
Startup probe passes?
   |
   +-- no  -> wait, do not kill too early
   |
   v
Liveness probe active
   |
   +-- fails repeatedly -> restart container
   |
   v
Readiness probe active
   |
   +-- passes -> send traffic
   +-- fails  -> remove from Service endpoints
```

Spring Boot actuator config:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      probes:
        enabled: true
      show-details: never
```

Production warning:

```text
Do not make liveness depend on database availability.
```

Why?

```text
If DB has a temporary issue, every Pod may restart.
That creates a restart storm and worsens recovery.
```

Better:

```text
readiness may fail when DB is unavailable.
liveness should only fail when the app process is broken.
```

---

## 20. Resource Requests And Limits

Requests:

```text
minimum resources Kubernetes reserves for scheduling.
```

Limits:

```text
maximum resources container can use.
```

Example:

```yaml
resources:
  requests:
    cpu: "500m"
    memory: "768Mi"
  limits:
    cpu: "1000m"
    memory: "1024Mi"
```

ASCII:

```text
Node capacity: 4 CPU, 8 Gi memory

Pod request: 0.5 CPU, 768 Mi

Scheduler asks:
Can this node reserve requested resources?

Runtime asks:
Did container exceed memory limit?
If yes -> OOMKilled
```

Spring Boot sizing thinking:

```text
Memory limit = heap + metaspace + thread stacks + direct buffers + native overhead
```

Bad:

```text
Memory limit 512Mi
JVM heap 512Mi
```

Why bad?

```text
JVM needs memory outside heap too.
Pod may OOMKill even when heap looks okay.
```

Better:

```text
memory limit 1024Mi
MaxRAMPercentage 75
heap around 768Mi
room for non-heap/native memory
```

---

## 21. HPA Autoscaling

HPA scales Pods based on metrics.

Example:

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: mini-url-hpa
  namespace: mini-prod
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: mini-url-shortener
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

ASCII:

```text
Traffic increases
      |
      v
CPU rises above target
      |
      v
HPA increases replicas
      |
      v
Deployment creates Pods
      |
      v
Scheduler places Pods
      |
      v
Service load balances to more Pods
```

Important:

```text
HPA needs metrics-server or external metrics.
```

For real production, CPU-only is often not enough.

Better scaling signals:

```text
HTTP RPS per Pod
p95 latency
Kafka consumer lag
queue depth
CPU
memory pressure
```

For URL shortener redirect API:

```text
CPU/RPS works for API scaling.
Redis latency and DB pool saturation must be monitored separately.
```

---

## 22. Rolling Update Dry Run

Current state:

```text
Deployment v1
replicas = 3
Pods: v1-a, v1-b, v1-c
```

You deploy v2.

With:

```yaml
maxUnavailable: 0
maxSurge: 1
```

Step-by-step:

```text
1. Deployment image changes from v1 to v2.
2. New ReplicaSet v2 is created.
3. Kubernetes creates one extra v2 Pod.
4. v2 Pod starts but receives no traffic until readiness passes.
5. v2 readiness passes.
6. Service endpoint includes v2 Pod.
7. One old v1 Pod is terminated.
8. Repeat until all Pods are v2.
```

ASCII:

```text
Before:
  v1 v1 v1

Step 1 surge:
  v1 v1 v1 v2-starting

Step 2 ready:
  v1 v1 v1 v2-ready

Step 3 remove old:
  v1 v1 v2

Final:
  v2 v2 v2
```

If v2 readiness fails:

```text
v1 Pods remain serving.
Rollout stalls.
No bad traffic if readiness is correct.
```

Rollback:

```bash
kubectl rollout undo deployment/mini-url-shortener -n mini-prod
```

Check rollout:

```bash
kubectl rollout status deployment/mini-url-shortener -n mini-prod
kubectl rollout history deployment/mini-url-shortener -n mini-prod
```

---

## 23. Database And Cache Connectivity

EKS Pods usually connect to RDS, ElastiCache, and MSK through private networking.

Network path:

```text
Pod IP
  -> Node ENI
  -> VPC route
  -> RDS security group
  -> RDS endpoint
```

ASCII:

```text
Private Subnet A                  Private Subnet B
+----------------+                +----------------+
| EKS Node       |                | RDS Postgres   |
|  Pod           | -------------> | port 5432      |
+----------------+                +----------------+
        |
        v
+----------------+
| Redis 6379     |
+----------------+
```

Security group rule:

```text
RDS inbound 5432 from EKS node/pod security group.
Redis inbound 6379 from EKS node/pod security group.
MSK inbound broker ports from EKS node/pod security group.
```

Spring Boot datasource:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:5432/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 2000
```

Production warning:

```text
If HPA scales Pods from 3 to 30 and each Pod has Hikari max 20,
you may create 600 DB connections.
```

DB connection formula:

```text
total possible connections = pod count * max pool size
```

Safer:

```text
Set Hikari pool based on RDS capacity.
Use PgBouncer/RDS Proxy when needed.
Scale read-heavy traffic with cache/read replicas.
```

---

## 24. Observability On EKS

Minimum observability:

```text
1. container logs
2. application metrics
3. Kubernetes events
4. node metrics
5. ALB metrics
6. RDS/Redis/MSK metrics
7. traces for slow requests
```

Commands:

```bash
kubectl logs deployment/mini-url-shortener -n mini-prod
kubectl describe pod <pod-name> -n mini-prod
kubectl get events -n mini-prod --sort-by=.lastTimestamp
kubectl top pods -n mini-prod
kubectl top nodes
```

Production signals:

```text
HTTP 5xx rate
HTTP 4xx rate
p95/p99 latency
CPU throttling
memory usage
restarts
OOMKilled count
readiness failures
DB pool active/idle/pending
Redis latency
Kafka consumer lag
ALB target 5xx
```

ASCII:

```text
Request slow?
   |
   +-- Check ALB latency
   +-- Check Pod CPU throttling
   +-- Check JVM GC
   +-- Check Hikari pending threads
   +-- Check DB slow query
   +-- Check Redis latency
```

Golden rule:

```text
In EKS, debugging is not only kubectl. You also inspect AWS metrics.
```

---

## 25. Security Checklist

Production checklist:

```text
[ ] Worker nodes in private subnets
[ ] Public internet only reaches ALB
[ ] RDS/Redis/MSK are private
[ ] IAM least privilege for Pods
[ ] No AWS static keys in Kubernetes Secrets
[ ] NetworkPolicies considered if using compatible CNI/policy engine
[ ] Security groups restrict database access
[ ] TLS enabled at ALB
[ ] Secrets managed through AWS Secrets Manager or External Secrets
[ ] Image scanning enabled in ECR
[ ] Containers run as non-root where possible
[ ] Resource limits are configured
[ ] RBAC least privilege
[ ] kubectl access restricted
[ ] Audit logs enabled if required
[ ] Pod disruption budget for important APIs
```

Pod security context example:

```yaml
securityContext:
  runAsNonRoot: true
  runAsUser: 10001
  allowPrivilegeEscalation: false
  readOnlyRootFilesystem: true
  capabilities:
    drop:
      - ALL
```

Note:

```text
Some Java apps need writable temp directory.
If readOnlyRootFilesystem is true, mount emptyDir for /tmp.
```

---

## 26. Production Failure Stories

### Failure Story 1: ImagePullBackOff after deployment

Symptom:

```text
Pods stuck in ImagePullBackOff.
```

Cause:

```text
Wrong ECR image tag or node role cannot pull image.
```

Debug:

```bash
kubectl describe pod <pod> -n mini-prod
kubectl get events -n mini-prod --sort-by=.lastTimestamp
```

Fix:

```text
Verify image exists in ECR.
Verify region/account.
Verify node role has ECR pull permissions.
Use immutable CI-generated image tags.
```

Lesson:

```text
Deployment YAML can be valid while image delivery is broken.
```

---

### Failure Story 2: Pods restart during DB outage

Symptom:

```text
All Pods restart repeatedly when RDS has a short outage.
```

Cause:

```text
Liveness probe depends on DB.
```

Fix:

```text
Liveness should check app process health.
Readiness should fail when DB dependency is unavailable.
```

Lesson:

```text
Bad probes convert dependency failures into application restart storms.
```

---

### Failure Story 3: DB connection storm after autoscaling

Symptom:

```text
Traffic spike causes HPA scale-out.
RDS max connections exceeded.
API latency increases.
```

Cause:

```text
Each Pod has Hikari maxPoolSize 30.
HPA scales to 30 Pods.
Potential connections = 900.
RDS cannot handle it.
```

Fix:

```text
Reduce pool size.
Use PgBouncer/RDS Proxy.
Cache redirect reads.
Set HPA max replicas based on DB capacity.
```

Lesson:

```text
Autoscaling app Pods can overload downstream systems.
```

---

### Failure Story 4: Rollout sends traffic to unready app

Symptom:

```text
New deployment causes 502/503 from ALB.
```

Cause:

```text
Readiness probe too weak or missing.
Service sends traffic before Spring Boot is ready.
```

Fix:

```text
Use /actuator/health/readiness.
Set startup probe for slow start.
Tune initial delays and failure thresholds.
```

Lesson:

```text
A rollout is only zero-downtime if readiness is truthful.
```

---

### Failure Story 5: Pods cannot connect to RDS

Symptom:

```text
Connection timeout to database.
```

Possible causes:

```text
RDS security group does not allow EKS node/pod SG.
Wrong VPC or subnet routing.
DNS resolution problem.
Network ACL issue.
RDS is public/private mismatch.
```

Debug:

```bash
kubectl run debug --rm -it --image=busybox:1.36 -n mini-prod -- sh
nslookup <rds-endpoint>
nc -vz <rds-endpoint> 5432
```

Lesson:

```text
In EKS, many app failures are actually VPC/security group failures.
```

---

## 27. Debugging Mindset

When EKS deployment fails, ask in layers.

```text
Layer 1: Did Kubernetes accept the manifest?
Layer 2: Did Deployment create ReplicaSet?
Layer 3: Did ReplicaSet create Pods?
Layer 4: Did scheduler place Pods on nodes?
Layer 5: Did kubelet pull image?
Layer 6: Did container start?
Layer 7: Did probes pass?
Layer 8: Did Service get endpoints?
Layer 9: Did Ingress/ALB route traffic?
Layer 10: Did app reach DB/Redis/Kafka?
```

ASCII:

```text
Request failing
   |
   v
ALB healthy targets?
   |
   +-- no -> Ingress / target group / readiness / Service endpoints
   |
   v
Service endpoints exist?
   |
   +-- no -> selector mismatch or readiness failing
   |
   v
Pods running?
   |
   +-- no -> describe pod / events / image / scheduling
   |
   v
App logs clean?
   |
   +-- no -> Spring config / DB / Redis / Kafka
```

Commands:

```bash
kubectl get deploy -n mini-prod
kubectl get rs -n mini-prod
kubectl get pods -n mini-prod -o wide
kubectl describe pod <pod> -n mini-prod
kubectl logs <pod> -n mini-prod
kubectl get svc -n mini-prod
kubectl get endpoints mini-url-service -n mini-prod
kubectl get ingress -n mini-prod
kubectl describe ingress mini-url-ingress -n mini-prod
kubectl get events -n mini-prod --sort-by=.lastTimestamp
```

Common messages:

```text
Pending:
    insufficient CPU/memory
    node selector mismatch
    taint not tolerated

ImagePullBackOff:
    bad image tag
    ECR permission issue

CrashLoopBackOff:
    app starts and exits repeatedly

Running but not Ready:
    readiness probe failing

Service has no endpoints:
    selector mismatch or Pods not Ready

ALB target unhealthy:
    health check path wrong or Pod not reachable
```

---

## 28. Step-by-Step Commands

### Step 1: Build app

```bash
mvn clean package -DskipTests
```

### Step 2: Build image

```bash
docker build -t mini-url-shortener:1.0.0 .
```

### Step 3: Create ECR repo

```bash
aws ecr create-repository \
  --repository-name mini-url-shortener \
  --region eu-central-1
```

### Step 4: Login to ECR

```bash
aws ecr get-login-password --region eu-central-1 \
  | docker login --username AWS --password-stdin \
    123456789012.dkr.ecr.eu-central-1.amazonaws.com
```

### Step 5: Tag and push

```bash
docker tag mini-url-shortener:1.0.0 \
  123456789012.dkr.ecr.eu-central-1.amazonaws.com/mini-url-shortener:1.0.0

docker push 123456789012.dkr.ecr.eu-central-1.amazonaws.com/mini-url-shortener:1.0.0
```

### Step 6: Create namespace

```bash
kubectl apply -f namespace.yaml
```

### Step 7: Apply config and secrets

```bash
kubectl apply -f configmap.yaml
kubectl apply -f secret.yaml
```

### Step 8: Deploy app

```bash
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl apply -f ingress.yaml
kubectl apply -f hpa.yaml
```

### Step 9: Verify

```bash
kubectl get pods -n mini-prod
kubectl rollout status deployment/mini-url-shortener -n mini-prod
kubectl get svc -n mini-prod
kubectl get ingress -n mini-prod
```

### Step 10: Smoke test

```bash
curl -i https://api.example.com/actuator/health/readiness
curl -i https://api.example.com/api/v1/urls
```

---

## 29. Interview-Ready Explanation

If interviewer asks:

```text
How would you deploy a Spring Boot microservice to EKS?
```

Strong answer:

```text
I would package the Spring Boot application into a Docker image, push it to ECR with an immutable version tag, and deploy it to EKS using Kubernetes manifests or Helm. EKS gives a managed Kubernetes control plane, while my workloads run on managed node groups in private subnets. I would expose the API using an Ingress backed by the AWS Load Balancer Controller and ALB, with TLS through ACM. The app would receive non-sensitive config through ConfigMaps and sensitive values through Secrets or AWS Secrets Manager via External Secrets. For AWS permissions, I would avoid static keys and use EKS Pod Identity or IRSA so the application ServiceAccount maps to a least-privilege IAM role. The Deployment would have readiness, liveness, and startup probes, resource requests and limits, rolling update settings, an HPA, and a PodDisruptionBudget. I would monitor logs, metrics, ALB target health, JVM metrics, database pool usage, Redis latency, and Kafka lag. For debugging, I would move from Ingress to Service to endpoints to Pods to events to logs, and then into AWS security groups and downstream services.
```

Why this is strong:

```text
1. Covers image registry.
2. Covers EKS control plane vs worker nodes.
3. Covers networking and ALB.
4. Covers config and secrets.
5. Covers IAM correctly.
6. Covers probes and rolling deployment.
7. Covers scaling.
8. Covers observability.
9. Covers debugging across Kubernetes and AWS.
```

Senior one-liner:

```text
EKS deployment is not just kubectl apply; it is the safe handoff between container image, Kubernetes desired state, AWS networking, IAM, and production observability.
```

---

## 30. One-Page Cheat Sheet

```text
Core mental model:
EKS = managed Kubernetes control plane + AWS data plane integration.

Deployment flow:
code -> jar -> Docker image -> ECR -> Deployment YAML -> EKS -> Pods -> Service -> Ingress -> ALB

Main AWS pieces:
ECR: image registry
EKS: Kubernetes control plane
EC2 node group: worker machines
ALB: HTTP entry point
IAM: permissions
VPC/subnets/SG: networking boundary
RDS/Redis/MSK: dependencies

K8s objects:
Namespace
ServiceAccount
ConfigMap
Secret
Deployment
Service
Ingress
HPA
PDB

Probes:
startup = app had enough time to boot?
liveness = should container restart?
readiness = should Pod receive traffic?

Scaling:
HPA scales Pods.
Cluster Autoscaler/Karpenter scales nodes.
Downstream DB/cache/broker capacity must be protected.

IAM:
cluster role = EKS control plane
node role = EC2 worker nodes
pod role = application permissions through ServiceAccount

Common failures:
ImagePullBackOff = image/tag/ECR permission
CrashLoopBackOff = app exits repeatedly
Pending = scheduling/resource/taint issue
Not Ready = readiness probe failing
No endpoints = selector mismatch or Pods unready
ALB unhealthy = health path/network/target issue
DB timeout = security group/VPC/DNS/downstream problem

Production rules:
Use immutable image tags.
Run nodes in private subnets.
Expose through ALB only.
Use least-privilege Pod IAM.
Do not put AWS keys in Secrets.
Tune probes carefully.
Set requests and limits.
Protect DB connection count.
Monitor app + Kubernetes + AWS.
```

---

## 31. One Picture To Remember

```text
                         EKS DEPLOYMENT MENTAL MODEL

                               Git Commit
                                   |
                                   v
                          CI builds Docker image
                                   |
                                   v
                              Push to ECR
                                   |
                                   v
                         kubectl / Helm applies YAML
                                   |
                                   v
+-------------------------------------------------------------------+
|                         EKS CONTROL PLANE                         |
|                                                                   |
|  API Server receives desired state                                |
|  Deployment Controller creates ReplicaSet                         |
|  Scheduler chooses worker node                                    |
+------------------------------+------------------------------------+
                               |
                               v
+-------------------------------------------------------------------+
|                         EKS WORKER NODES                          |
|                                                                   |
|  kubelet pulls image from ECR                                      |
|  container starts Spring Boot                                      |
|  startup probe waits                                              |
|  readiness probe allows traffic                                   |
|  liveness probe restarts broken container                         |
+------------------------------+------------------------------------+
                               |
                               v
+-------------------------------------------------------------------+
|                         TRAFFIC PATH                              |
|                                                                   |
|  Route 53 -> ALB -> Ingress -> Service -> Ready Pods               |
+------------------------------+------------------------------------+
                               |
                               v
+-------------------------------------------------------------------+
|                         AWS DEPENDENCIES                          |
|                                                                   |
|  RDS Postgres | ElastiCache Redis | MSK Kafka | Secrets | CW Logs  |
+-------------------------------------------------------------------+

FINAL MEMORY:

Image is what runs.
Manifest is how it runs.
EKS is where it runs.
AWS networking and IAM decide whether it works safely in production.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. EKS is Kubernetes control plane managed by AWS, but you still own workload production quality.
2. A production deployment needs image registry, node groups, IAM, networking, probes, scaling, and observability.
3. ServiceAccount-based IAM is safer than giving broad AWS permissions to node roles.
4. Readiness controls traffic; liveness controls restarts; startup protects slow booting apps.
5. Debug EKS from ALB to Ingress to Service to endpoints to Pods to logs, then AWS security groups and dependencies.
```

After this chapter, you can deploy MiniURLShortener-style Spring Boot services on EKS with a production mental model instead of memorizing random commands.
