# 099_Final_Kubernetes_CheatSheet.md

> MiniK8s Deep Production Mode  
> Final Cheat Sheet • Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 0. How To Use This Cheat Sheet

This is not a dictionary of Kubernetes words.

This is a **production memory map**.

Do not read Kubernetes like:

```text
Pod = definition
Deployment = definition
Service = definition
Ingress = definition
```

That becomes memorization.

Read Kubernetes like this:

```text
Production Problem
      |
      v
Kubernetes Object That Solves It
      |
      v
Runtime Flow
      |
      v
Debugging Path
```

One picture:

```text
Problem First Thinking

Need app running safely        -> Deployment
Need multiple copies           -> ReplicaSet / Deployment replicas
Need stable network identity   -> Service
Need external HTTP entry       -> Ingress / Gateway
Need config outside image      -> ConfigMap / Secret
Need persistent disk           -> PVC / PV / StorageClass
Need controlled access         -> RBAC / ServiceAccount
Need autoscaling               -> HPA / VPA / ClusterAutoscaler
Need production diagnosis      -> logs + metrics + traces + events
```

Final rule:

```text
Kubernetes is not YAML knowledge.
Kubernetes is production operations encoded as desired state.
```

---

# 1. The One-Line Kubernetes Mental Model

If you remember only one sentence:

```text
Kubernetes continuously reconciles actual cluster reality toward desired state.
```

ASCII:

```text
You write spec
     |
     v
API Server stores desired state
     |
     v
Controllers compare desired vs actual
     |
     v
Scheduler + kubelet + runtime make it real
     |
     v
Status reports observed reality
```

Do not think:

```text
kubectl apply starts containers
```

Think:

```text
kubectl apply writes desired state.
Kubernetes components react.
```

---

# 2. Kubernetes Core Loop

Every Kubernetes feature is some version of this loop:

```text
while true:
    desired = read_spec()
    actual  = read_status()

    if desired != actual:
        act()
```

Diagram:

```text
+------------------+
| Desired State    |
| spec.replicas=3  |
+--------+---------+
         |
         v
+------------------+
| Controller       |
| watch/compare/act|
+--------+---------+
         |
         v
+------------------+
| Actual State     |
| available=2      |
+--------+---------+
         |
         v
+------------------+
| Action           |
| create 1 pod     |
+------------------+
```

This is why Kubernetes self-heals.

---

# 3. Control Plane vs Data Plane

```text
CONTROL PLANE = makes decisions
DATA PLANE    = runs user traffic
```

```text
                 CONTROL PLANE

     +------------+   +-------------+   +-------------+
     | API Server |   | Controllers |   | Scheduler   |
     +------+-----+   +------+------+   +------+------+
            |                |                 |
            +----------------+-----------------+
                             |
                             v
                          +------+
                          | etcd |
                          +------+

                             |
                             v

                   DATA PLANE / WORKER NODES

     +----------------+     +----------------+     +----------------+
     | Node-1         |     | Node-2         |     | Node-3         |
     | kubelet        |     | kubelet        |     | kubelet        |
     | kube-proxy/CNI |     | kube-proxy/CNI |     | kube-proxy/CNI |
     | Pods           |     | Pods           |     | Pods           |
     +----------------+     +----------------+     +----------------+
```

Interview answer:

```text
The control plane coordinates the cluster. The data plane runs application workloads. User traffic normally goes to pods on worker nodes, not through the API server.
```

---

# 4. API Server Cheat Sheet

Mental model:

```text
API Server = front door + validation + authorization + watch hub
```

Everything talks through it:

```text
kubectl
controllers
scheduler
kubelet
operators
CI/CD

   |
   v
API Server
   |
   v
etcd
```

Common failure thinking:

```text
kubectl command fails
   |
   +--> auth problem?
   +--> RBAC denied?
   +--> API server unavailable?
   +--> bad YAML schema?
   +--> admission webhook rejected?
```

Useful commands:

```bash
kubectl auth can-i create pods
kubectl auth can-i get secrets -n prod
kubectl explain deployment.spec
kubectl api-resources
kubectl get events -A --sort-by=.lastTimestamp
```

---

# 5. etcd Cheat Sheet

Mental model:

```text
etcd = Kubernetes memory
```

It stores:

```text
Deployments
ReplicaSets
Pods
Services
ConfigMaps
Secrets
Nodes
RBAC objects
Status information
```

It does **not** run containers.

```text
Wrong:
Pod runs inside etcd

Correct:
etcd stores pod object.
Kubelet runs container on node.
```

Production warning:

```text
If etcd is unhealthy, the cluster may keep existing pods running,
but control-plane operations become unsafe or unavailable.
```

---

# 6. Scheduler Cheat Sheet

Mental model:

```text
Scheduler answers: where should this pending Pod run?
```

It does not start containers.

```text
Pending Pod
    |
    v
Scheduler filters nodes
    |
    +--> enough CPU/memory?
    +--> taints tolerated?
    +--> nodeSelector matches?
    +--> affinity rules satisfied?
    +--> volume available in zone?
    |
    v
Scheduler scores nodes
    |
    v
Bind Pod to selected Node
    |
    v
Kubelet starts it
```

Debug scheduling:

```bash
kubectl describe pod <pod>
kubectl get nodes
kubectl describe node <node>
kubectl get events -n <ns> --sort-by=.lastTimestamp
```

Common causes of `Pending`:

```text
Insufficient CPU/memory
Untolerated taint
Wrong nodeSelector
PVC not bound
No node in required zone
Pod anti-affinity too strict
Image pull is not scheduling; it happens after scheduling
```

---

# 7. Kubelet Cheat Sheet

Mental model:

```text
Kubelet = node-level production supervisor
```

Kubelet does:

```text
Watches API server for pods assigned to its node
Pulls images through runtime
Starts containers
Mounts volumes
Runs probes
Reports pod/node status
Restarts containers when restartPolicy allows
```

```text
Node
+------------------------------------------------+
| kubelet                                        |
|                                                |
| API watch -> Pod assigned to me?               |
| image pull -> container runtime                |
| volume mount -> CNI network                    |
| readiness/liveness/startup probes              |
| status report back to API server               |
+------------------------------------------------+
```

Common kubelet-level symptoms:

```text
ImagePullBackOff
CrashLoopBackOff
CreateContainerConfigError
ContainerCreating stuck
Readiness probe failed
Liveness probe failed
Volume mount timeout
```

---

# 8. Deployment -> ReplicaSet -> Pod -> Container

Do not memorize definitions.

Remember responsibility chain:

```text
Deployment  = rollout brain
ReplicaSet  = replica count keeper
Pod         = runtime wrapper
Container   = actual app process
```

Diagram:

```text
Deployment
  |
  | creates/owns
  v
ReplicaSet
  |
  | creates/owns
  v
Pod
  |
  | contains
  v
Container
  |
  | runs
  v
java -jar order-service.jar
```

Production thinking:

```text
App version problem?       -> Deployment / ReplicaSet
Replica count problem?     -> ReplicaSet / HPA
Container crash problem?   -> Pod logs / kubelet / app config
Traffic problem?           -> Service / EndpointSlice / readiness / ingress
```

---

# 9. Pod Cheat Sheet

Mental model:

```text
Pod = smallest schedulable runtime unit in Kubernetes
```

A Pod is not just a container.

```text
Pod
+----------------------------------------+
| shared network namespace               |
| shared volumes                          |
| one or more containers                  |
| one IP address                          |
| lifecycle controlled as one unit        |
+----------------------------------------+
```

Common pod phases:

```text
Pending      = accepted, not fully running yet
Running      = at least one container running/starting/restarting
Succeeded    = all containers exited successfully
Failed       = all containers terminated, at least one failed
Unknown      = node communication problem
```

Container states:

```text
Waiting
Running
Terminated
```

Important distinction:

```text
Pod Running != App Ready
```

---

# 10. Pod Creation End-To-End

```text
kubectl apply -f deployment.yaml
        |
        v
API Server validates + stores Deployment
        |
        v
Deployment Controller creates ReplicaSet
        |
        v
ReplicaSet Controller creates Pod objects
        |
        v
Scheduler assigns each Pod to a Node
        |
        v
Kubelet on selected Node notices assigned Pod
        |
        v
CNI creates pod network
        |
        v
Container runtime pulls image
        |
        v
Volumes mounted
        |
        v
Container process starts
        |
        v
Spring Boot starts
        |
        v
Startup/readiness probes pass
        |
        v
EndpointSlice includes Pod
        |
        v
Service can route traffic
```

One-line memory:

```text
Apply stores intent; controllers create objects; scheduler places; kubelet runs; readiness admits traffic.
```

---

# 11. Service Cheat Sheet

Problem:

```text
Pods are temporary. Pod IPs change.
```

Solution:

```text
Service = stable identity + load balancing to matching ready pods
```

```text
Client Pod
   |
   | http://order-service
   v
Service ClusterIP
   |
   +--> Ready Pod A
   +--> Ready Pod B
   +--> Ready Pod C
```

Service types:

```text
ClusterIP     = internal stable virtual IP
NodePort      = exposes port on each node
LoadBalancer  = cloud external load balancer
ExternalName  = DNS alias
```

Most backend microservices use:

```text
ClusterIP behind internal calls
Ingress/Gateway for external HTTP
```

---

# 12. Labels And Selectors

Labels connect objects.

```text
Deployment creates Pods with labels
Service selects Pods by labels
ReplicaSet owns Pods by labels
NetworkPolicy selects Pods by labels
```

```yaml
metadata:
  labels:
    app: order-service
```

```yaml
selector:
  app: order-service
```

Broken traffic diagram:

```text
Service selector: app=order-service

Pod labels:
  app=order

Result:
  Service exists
  Pod exists
  endpoints = none
  traffic fails
```

Debug:

```bash
kubectl get pods --show-labels
kubectl describe svc order-service
kubectl get endpoints order-service
kubectl get endpointslice -l kubernetes.io/service-name=order-service
```

---

# 13. Ingress / Gateway Cheat Sheet

Mental model:

```text
Ingress/Gateway = HTTP entrance routing rules
Service         = stable backend identity
Pod             = actual app runtime
```

```text
Internet Client
      |
      v
Cloud Load Balancer
      |
      v
Ingress Controller / Gateway
      |
      | host/path routing
      v
Service
      |
      v
Ready Pods
```

Ingress object is only a rule.

Ingress Controller is the real running component that enforces the rule.

```text
Ingress YAML without controller = traffic rule nobody executes
```

Debug external traffic:

```bash
kubectl get ingress -A
kubectl describe ingress <name>
kubectl get svc -n ingress-nginx
kubectl logs -n ingress-nginx deploy/ingress-nginx-controller
kubectl get endpoints <backend-service>
```

---

# 14. Request Flow End-To-End

Example request:

```text
GET https://api.example.com/orders/101
```

Flow:

```text
Browser / Client
    |
    v
DNS resolves api.example.com
    |
    v
Cloud Load Balancer public IP
    |
    v
Ingress Controller Pod
    |
    v
Ingress rule matches host/path
    |
    v
Service ClusterIP
    |
    v
kube-proxy / CNI chooses backend Pod
    |
    v
Pod IP:8080
    |
    v
Spring Boot Controller
    |
    v
Service layer
    |
    v
Database / Redis / Kafka
    |
    v
Response returns back same path
```

ASCII:

```text
Client
  |
  v
DNS
  |
  v
External LB
  |
  v
Ingress Controller
  |
  v
Service
  |
  v
EndpointSlice
  |
  v
Pod IP
  |
  v
Spring Boot
```

Debug order:

```text
DNS -> LB -> Ingress -> Service -> Endpoints -> Pod -> Logs -> Dependency
```

---

# 15. Spring Boot Kubernetes Minimum App

```java
package com.example.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}

@RestController
class OrderController {
    @GetMapping("/orders/{id}")
    public String getOrder(@PathVariable String id) {
        return "order=" + id;
    }
}
```

Actuator readiness config:

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
  health:
    readinessstate:
      enabled: true
    livenessstate:
      enabled: true
```

Kubernetes probe:

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
  initialDelaySeconds: 30
  periodSeconds: 10
```

Production rule:

```text
Readiness protects traffic.
Liveness restarts stuck containers.
Startup probe protects slow startup.
```

---

# 16. ConfigMap Cheat Sheet

Problem:

```text
Same image should run in dev, staging, prod with different config.
```

Solution:

```text
ConfigMap = non-secret configuration outside image
```

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: order-config
data:
  SPRING_PROFILES_ACTIVE: prod
  REDIS_HOST: redis.default.svc.cluster.local
```

Use as env:

```yaml
envFrom:
  - configMapRef:
      name: order-config
```

Mental model:

```text
Docker image = immutable app package
ConfigMap    = environment-specific behavior
```

Do not store passwords in ConfigMap.

---

# 17. Secret Cheat Sheet

Problem:

```text
App needs passwords/tokens/certs.
```

Solution:

```text
Secret = sensitive configuration object
```

But remember:

```text
Secret is not magic security.
RBAC, encryption at rest, external secret managers, and least privilege still matter.
```

```yaml
env:
  - name: DB_PASSWORD
    valueFrom:
      secretKeyRef:
        name: db-secret
        key: password
```

Common production mistake:

```text
Too many service accounts can read all secrets in namespace.
```

Debug:

```bash
kubectl get secret
kubectl describe secret db-secret
kubectl auth can-i get secrets --as=system:serviceaccount:prod:order-sa
```

---

# 18. Volume / PVC / PV / StorageClass

Problem:

```text
Pods are temporary, but data may need to survive pod restart/reschedule.
```

Mental chain:

```text
Pod asks for storage using PVC
PVC binds to PV
PV is provisioned manually or by StorageClass
StorageClass talks to storage backend
```

```text
Pod
 |
 v
PVC: I need 20Gi
 |
 v
PV: Here is disk volume
 |
 v
StorageClass: create disk dynamically
 |
 v
Cloud disk / network storage
```

Common Pending PVC causes:

```text
No StorageClass
Wrong access mode
No available PV
Zone mismatch
Provisioner problem
```

Debug:

```bash
kubectl get pvc
kubectl describe pvc <name>
kubectl get pv
kubectl get storageclass
kubectl describe pod <pod>
```

---

# 19. Namespace Cheat Sheet

Mental model:

```text
Namespace = logical grouping boundary
```

Use for:

```text
team separation
environment separation
RBAC scope
ResourceQuota
LimitRange
naming isolation
```

Not enough by itself for:

```text
hard network isolation
strong tenant isolation
secret isolation if RBAC is poor
```

Diagram:

```text
Cluster
  |
  +-- dev
  |    +-- order-service
  |    +-- payment-service
  |
  +-- staging
  |    +-- order-service
  |
  +-- prod
       +-- order-service
```

---

# 20. RBAC / ServiceAccount Cheat Sheet

Mental model:

```text
ServiceAccount = identity used by pod
Role           = permissions inside namespace
ClusterRole    = permissions cluster-wide or reusable role
RoleBinding    = attaches role to identity
ClusterRoleBinding = attaches cluster role broadly
```

Diagram:

```text
Pod
 |
 v
ServiceAccount: order-sa
 |
 v
RoleBinding
 |
 v
Role: can get configmaps, cannot read secrets
```

Production rule:

```text
Give pods only permissions they need.
Never casually bind cluster-admin.
```

Useful commands:

```bash
kubectl auth can-i get pods --as=system:serviceaccount:prod:order-sa -n prod
kubectl auth can-i list secrets --as=system:serviceaccount:prod:order-sa -n prod
kubectl get role,rolebinding -n prod
kubectl get clusterrole,clusterrolebinding
```

---

# 21. Requests, Limits, QoS

Mental model:

```text
request = scheduling reservation
limit   = runtime ceiling
```

```yaml
resources:
  requests:
    cpu: "500m"
    memory: "512Mi"
  limits:
    cpu: "1"
    memory: "1Gi"
```

Scheduler uses requests:

```text
Node capacity - sum(requests) >= pod request?
```

Runtime enforces limits:

```text
Memory limit exceeded -> OOMKilled
CPU limit reached     -> throttling
```

QoS classes:

```text
Guaranteed = requests == limits for CPU and memory
Burstable  = some requests/limits set
BestEffort = no requests/limits
```

Production warning:

```text
Too low memory limit causes OOMKilled.
Too low CPU limit causes latency due to throttling.
No requests causes bad scheduling.
```

---

# 22. HPA / VPA / Cluster Autoscaler

Mental model:

```text
HPA scales pod count.
VPA adjusts pod resource requests.
Cluster Autoscaler scales node count.
```

Diagram:

```text
Traffic increases
    |
    v
CPU/RPS/latency metric rises
    |
    v
HPA increases replicas
    |
    v
More Pods need space
    |
    v
Cluster Autoscaler may add Nodes
```

Important:

```text
HPA cannot help if the cluster has no capacity unless nodes can scale.
Cluster Autoscaler does not scale pods.
VPA may restart pods to apply recommendations.
```

Debug:

```bash
kubectl get hpa
kubectl describe hpa <name>
kubectl top pods
kubectl top nodes
kubectl describe pod <pending-pod>
```

---

# 23. Health Checks

Three probes:

```text
startupProbe   = is slow-starting app now started?
readinessProbe = can app receive traffic?
livenessProbe  = is app stuck and needs restart?
```

Diagram:

```text
Container starts
    |
    v
startupProbe passes
    |
    v
readinessProbe controls Service endpoints
    |
    v
livenessProbe watches for stuck process
```

Wrong probe consequences:

```text
readiness too strict  -> no traffic, endpoints empty
readiness too weak    -> broken pod receives traffic
liveness too strict   -> restart storm
startup missing       -> slow app killed before startup
```

Spring Boot rule:

```text
Use actuator health groups. Do not make liveness depend on external DB.
```

---

# 24. Rollout / Rollback

Mental model:

```text
Deployment safely changes ReplicaSets over time.
```

```text
Version 1:
Old Old Old

Rolling update:
New Old Old
New New Old
New New New
```

Commands:

```bash
kubectl rollout status deployment/order-service
kubectl rollout history deployment/order-service
kubectl rollout undo deployment/order-service
kubectl describe deployment order-service
```

Production checklist before rollout:

```text
[ ] readiness probe correct
[ ] maxUnavailable safe
[ ] maxSurge safe
[ ] app backward-compatible with DB schema
[ ] logs include version/build id
[ ] metrics dashboard ready
[ ] rollback command known
```

---

# 25. Networking Mental Model

Kubernetes networking assumptions:

```text
Every Pod gets an IP.
Pods can communicate across nodes.
Services provide stable virtual IP/name.
CNI implements networking.
kube-proxy or eBPF implements service routing.
```

```text
Pod A on Node-1
   |
   | Pod IP to Pod IP
   v
Pod B on Node-2

CNI makes this possible.
```

Service routing:

```text
Client -> Service IP -> backend Pod IP
```

DNS:

```text
order-service.default.svc.cluster.local
```

Debug:

```bash
kubectl exec -it <pod> -- nslookup order-service
kubectl exec -it <pod> -- curl http://order-service:8080/actuator/health
kubectl get svc
kubectl get endpoints
kubectl get pods -o wide
```

---

# 26. CoreDNS Cheat Sheet

Mental model:

```text
CoreDNS turns service names into ClusterIP addresses.
```

```text
App calls http://payment-service
       |
       v
DNS query
       |
       v
CoreDNS
       |
       v
payment-service ClusterIP
```

Common DNS issues:

```text
Wrong namespace
Service name typo
CoreDNS pods unhealthy
NetworkPolicy blocks DNS
Using short name from different namespace
```

Debug:

```bash
kubectl get pods -n kube-system -l k8s-app=kube-dns
kubectl logs -n kube-system deploy/coredns
kubectl exec -it <pod> -- nslookup kubernetes.default
kubectl exec -it <pod> -- nslookup payment-service.prod.svc.cluster.local
```

---

# 27. Observability Cheat Sheet

Three signals:

```text
Logs    = what happened inside one process
Metrics = numeric health over time
Traces  = request journey across services
```

Diagram:

```text
One failing request
    |
    +--> Trace shows path and slow span
    +--> Logs show exception/context
    +--> Metrics show impact and trend
```

Golden signals:

```text
Latency
Traffic
Errors
Saturation
```

Kubernetes signals:

```text
Pod restarts
CPU/memory usage
OOMKilled
Pending pods
HPA scaling
Node pressure
Readiness failures
Ingress 4xx/5xx
```

Production rule:

```text
Never debug only from logs.
Correlate logs + metrics + traces + events.
```

---

# 28. Events Cheat Sheet

Kubernetes events explain recent operational changes.

```bash
kubectl get events -n prod --sort-by=.lastTimestamp
kubectl describe pod <pod>
```

Events can reveal:

```text
FailedScheduling
FailedMount
Pulling image
Failed to pull image
Back-off restarting failed container
Readiness probe failed
Killing container
Node not ready
```

Production mindset:

```text
When pod state looks confusing, read events before guessing.
```

---

# 29. Common Pod Status Debug Map

```text
Pending
  -> scheduling/resource/PVC/taint issue

ContainerCreating
  -> image pull, volume mount, CNI, runtime setup

ImagePullBackOff
  -> image tag/registry/auth/network issue

CreateContainerConfigError
  -> missing ConfigMap/Secret/env/invalid container config

CrashLoopBackOff
  -> app starts then exits repeatedly

Running 0/1
  -> container alive but readiness failing

OOMKilled
  -> memory limit exceeded

Terminating stuck
  -> finalizers, volume detach, preStop, node issue
```

---

# 30. Debugging Ladder: Deployment To Traffic

Use this exact ladder:

```text
1. Does Deployment exist?
2. Does ReplicaSet exist?
3. Are desired replicas created?
4. Are Pods scheduled?
5. Did image pull?
6. Did container start?
7. Are probes passing?
8. Is Pod Ready?
9. Is Service selector correct?
10. Are endpoints populated?
11. Does DNS resolve?
12. Does ingress route correctly?
13. Does app dependency work?
```

Commands:

```bash
kubectl get deploy order-service
kubectl describe deploy order-service
kubectl get rs
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl logs <pod> --previous
kubectl get svc order-service
kubectl describe svc order-service
kubectl get endpoints order-service
kubectl get ingress
```

Memory hook:

```text
Object -> Child object -> Node placement -> Container -> Health -> Service -> Traffic
```

---

# 31. Production Failure: 502 / 503 From Ingress

Symptoms:

```text
Client gets 502 or 503
Pods may look Running
```

Possible causes:

```text
Ingress backend points to wrong Service
Service has no endpoints
Pods not Ready
Wrong targetPort
App not listening on expected port
Readiness passes but app endpoint fails
Ingress controller problem
```

Debug path:

```text
Ingress rule
  -> Service name/port
  -> Service selector
  -> Endpoints
  -> Pod readiness
  -> Pod logs
  -> App port
```

Commands:

```bash
kubectl describe ingress api-ingress
kubectl describe svc order-service
kubectl get endpoints order-service
kubectl get pods -l app=order-service
kubectl logs deploy/order-service
```

---

# 32. Production Failure: CrashLoopBackOff

Meaning:

```text
Container starts, exits, Kubernetes retries with backoff.
```

Common Spring Boot causes:

```text
Missing env variable
Bad DB password
Cannot connect to DB during startup
Flyway/Liquibase migration failure
Wrong Spring profile
Port binding issue
OutOfMemoryError
```

Debug:

```bash
kubectl logs <pod>
kubectl logs <pod> --previous
kubectl describe pod <pod>
kubectl get configmap,secret
```

Mental model:

```text
Kubernetes restarts the process.
It cannot fix broken application startup logic.
```

---

# 33. Production Failure: OOMKilled

Meaning:

```text
Container exceeded memory limit.
Kernel killed it.
```

Symptoms:

```bash
kubectl describe pod <pod>
# Last State: Terminated
# Reason: OOMKilled
```

Causes:

```text
Memory leak
JVM heap too large for container limit
Traffic spike
Large request payload
Too many threads
Cache unbounded
Native memory overhead ignored
```

Spring Boot JVM rule:

```text
Do not set -Xmx equal to container memory limit.
Leave room for metaspace, threads, direct buffers, native memory.
```

Example:

```yaml
env:
  - name: JAVA_TOOL_OPTIONS
    value: "-XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"
```

---

# 34. Production Failure: Readiness Failing

Meaning:

```text
Pod may be Running, but Service will not send traffic to it.
```

Debug:

```bash
kubectl describe pod <pod>
kubectl logs <pod>
kubectl get endpoints order-service
kubectl exec -it <pod> -- curl localhost:8080/actuator/health/readiness
```

Causes:

```text
DB down
Redis down
wrong actuator path
app slow to warm up
probe timeout too low
Spring profile missing readiness endpoint
```

Production rule:

```text
Readiness should represent traffic safety.
It can depend on critical dependencies, but avoid overly fragile checks.
```

---

# 35. Production Failure: Service Has No Endpoints

Symptoms:

```bash
kubectl get endpoints order-service
# <none>
```

Causes:

```text
Service selector does not match Pod labels
Pods are not Ready
Pods are in wrong namespace
Service targetPort mismatch can break traffic even if endpoints exist
```

Debug:

```bash
kubectl get pods --show-labels
kubectl describe svc order-service
kubectl get endpoints order-service
kubectl get pods -n prod -l app=order-service
```

Mental model:

```text
Service does not send traffic to all pods.
It sends traffic to selected ready pods.
```

---

# 36. Production Failure: Pending Pods After Scaling

Scenario:

```text
HPA increased replicas from 5 to 20.
New pods are Pending.
```

Causes:

```text
Not enough node CPU/memory
Cluster Autoscaler not configured
Taints not tolerated
PVC cannot be created
Topology spread constraints too strict
```

Debug:

```bash
kubectl describe pod <pending-pod>
kubectl get nodes
kubectl top nodes
kubectl get events -n prod --sort-by=.lastTimestamp
kubectl describe hpa order-service
```

Memory hook:

```text
HPA creates demand. Cluster Autoscaler creates supply.
```

---

# 37. Real-World Spring Boot Deployment Template

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
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
      app: order-service
  template:
    metadata:
      labels:
        app: order-service
    spec:
      serviceAccountName: order-sa
      containers:
        - name: order-service
          image: registry.example.com/order-service:1.0.0
          ports:
            - containerPort: 8080
          env:
            - name: JAVA_TOOL_OPTIONS
              value: "-XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"
          envFrom:
            - configMapRef:
                name: order-config
          resources:
            requests:
              cpu: "500m"
              memory: "512Mi"
            limits:
              cpu: "1"
              memory: "1Gi"
          startupProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            failureThreshold: 30
            periodSeconds: 5
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            periodSeconds: 5
            timeoutSeconds: 2
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            periodSeconds: 10
            timeoutSeconds: 2
```

---

# 38. Service Template

```yaml
apiVersion: v1
kind: Service
metadata:
  name: order-service
spec:
  type: ClusterIP
  selector:
    app: order-service
  ports:
    - name: http
      port: 80
      targetPort: 8080
```

Mental model:

```text
port       = Service port clients call
targetPort = Pod container port receiving traffic
```

Diagram:

```text
Client calls order-service:80
          |
          v
Service port 80
          |
          v
Pod targetPort 8080
```

---

# 39. Ingress Template

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: order-ingress
spec:
  rules:
    - host: api.example.com
      http:
        paths:
          - path: /orders
            pathType: Prefix
            backend:
              service:
                name: order-service
                port:
                  number: 80
```

Mental model:

```text
Ingress routes HTTP to Service.
Service routes to ready Pods.
```

---

# 40. HPA Template

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: order-service
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: order-service
  minReplicas: 3
  maxReplicas: 20
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
```

Production thought:

```text
HPA is not a magic performance fix.
It only helps if more pods can reduce bottleneck.
If DB is bottleneck, HPA may increase pressure.
```

---

# 41. NetworkPolicy Memory

Default Kubernetes networking usually allows broad pod-to-pod communication unless NetworkPolicy is enforced by CNI.

Mental model:

```text
NetworkPolicy = firewall rules for selected pods
```

```text
Without policy:
Pod A can often call Pod B

With policy:
Only allowed sources/ports can call selected pods
```

Debug warning:

```text
If DNS suddenly fails, check whether NetworkPolicy blocks egress to CoreDNS.
```

---

# 42. Node Failure Mental Model

```text
Node heartbeat stops
    |
    v
Node marked NotReady
    |
    v
Pods on node become unavailable
    |
    v
ReplicaSet creates replacement pods elsewhere
    |
    v
Scheduler picks healthy nodes
    |
    v
Kubelet starts replacements
```

Important:

```text
Kubernetes does not move a running container.
It creates replacement pods.
```

---

# 43. Rolling Update Failure Mental Model

```text
Old version healthy
    |
    v
New version deployed
    |
    v
New pods fail readiness
    |
    v
Deployment rollout pauses / unavailable
    |
    v
Old pods may remain depending on strategy
```

Debug:

```bash
kubectl rollout status deploy/order-service
kubectl describe deploy order-service
kubectl get rs
kubectl get pods
kubectl logs <new-pod>
kubectl rollout undo deploy/order-service
```

Rule:

```text
A safe rollout depends on readiness being honest.
```

---

# 44. Production Debugging Mindset

Bad debugging:

```text
Randomly restart pods.
Randomly change YAML.
Randomly blame Kubernetes.
```

Good debugging:

```text
Follow the request or object lifecycle.
Find the first broken link.
```

For deployment issue:

```text
Deployment -> ReplicaSet -> Pod -> Container -> Probe -> Service
```

For traffic issue:

```text
DNS -> LB -> Ingress -> Service -> Endpoint -> Pod -> App -> Dependency
```

For scaling issue:

```text
Metric -> HPA -> Replica count -> Scheduling -> Node capacity -> App bottleneck
```

---

# 45. The 20 Commands You Must Know

```bash
kubectl get pods -A
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl logs <pod> --previous

kubectl get deploy
kubectl describe deploy <deploy>
kubectl rollout status deploy/<deploy>
kubectl rollout history deploy/<deploy>
kubectl rollout undo deploy/<deploy>

kubectl get rs
kubectl get svc
kubectl describe svc <svc>
kubectl get endpoints <svc>
kubectl get ingress -A

kubectl get events -A --sort-by=.lastTimestamp
kubectl top pods
kubectl top nodes
kubectl exec -it <pod> -- sh
kubectl auth can-i <verb> <resource>
kubectl explain <resource>.spec
```

---

# 46. Interview Answers

## What is Kubernetes?

Kubernetes is a desired-state orchestration platform. You declare the desired state of workloads and infrastructure objects, and Kubernetes continuously reconciles the actual cluster state to match that desired state.

## What happens when you create a Pod?

The API server validates and stores the Pod object. The scheduler assigns it to a node. The kubelet on that node observes the assignment, sets up networking and volumes, asks the container runtime to pull and start containers, runs probes, and reports status back to the API server.

## What is the difference between Deployment and Pod?

A Pod is the runtime unit that contains containers. A Deployment manages rollout and desired replica state by creating ReplicaSets, which then create Pods. In production, we usually manage applications using Deployments, not raw Pods.

## Why do we need Service if Pods already have IPs?

Pod IPs are temporary. A Service gives a stable name and virtual IP, and routes traffic to matching ready Pods using labels/selectors.

## What is readiness probe?

Readiness probe tells Kubernetes whether a Pod should receive traffic. If readiness fails, the Pod can remain Running but is removed from Service endpoints.

## What is liveness probe?

Liveness probe tells Kubernetes whether the container is stuck and should be restarted. It should not depend on external systems like databases.

## Why can a Service have no endpoints?

Usually because its selector does not match Pod labels, or because matching Pods are not Ready.

## What is CrashLoopBackOff?

It means the container starts and exits repeatedly. Kubernetes retries with increasing backoff. The root cause is usually application startup failure, missing config, bad dependency, or OOM.

## What is ImagePullBackOff?

It means the kubelet cannot pull the container image. Causes include wrong image tag, missing registry credentials, network failure, or unavailable registry.

## What is the scheduler responsible for?

The scheduler chooses a node for pending Pods based on resource requests, constraints, taints, tolerations, affinity, and other policies. It does not start containers.

## What is kubelet responsible for?

Kubelet runs on each node. It watches assigned Pods, starts containers through the runtime, mounts volumes, runs probes, and reports status.

## What is etcd?

etcd is the strongly consistent key-value store that stores Kubernetes cluster state. It is the memory of the cluster.

## Difference between request and limit?

Request is the resource amount used for scheduling. Limit is the maximum runtime amount enforced by the container runtime/cgroups.

## What is HPA?

Horizontal Pod Autoscaler changes the number of pod replicas based on metrics like CPU, memory, or custom metrics.

## What is Cluster Autoscaler?

Cluster Autoscaler adds or removes nodes when pods cannot be scheduled due to insufficient capacity, or when nodes are underutilized.

---

# 47. Final Kubernetes Cheat Table

```text
Object / Component        Real Production Meaning
---------------------------------------------------------------
Pod                       runtime wrapper around containers
Container                 actual app process
Deployment                app rollout manager
ReplicaSet                keeps pod count
Service                   stable access to ready pods
EndpointSlice             current backend pod addresses
Ingress                   HTTP routing rule
Ingress Controller        runtime that executes ingress rules
ConfigMap                 non-secret config
Secret                    sensitive config object
PVC                       pod's storage request
PV                        actual storage volume
StorageClass              dynamic storage provisioning policy
Namespace                 logical grouping boundary
ServiceAccount            pod identity
Role                      namespace permissions
RoleBinding               connects role to identity
Node                      worker machine
Kubelet                   node agent
Scheduler                 chooses node for pod
Controller Manager        runs reconciliation controllers
API Server                Kubernetes front door
etcd                      cluster state database
CoreDNS                   service name resolution
kube-proxy/eBPF           service traffic routing
CNI                       pod networking implementation
HPA                       scales pods horizontally
VPA                       adjusts pod resources
Cluster Autoscaler        scales nodes
```

---

# 48. One Picture To Remember

```text
                         USER / CI/CD
                              |
                              | kubectl apply / pipeline deploy
                              v
                        +-------------+
                        | API Server  |
                        +------+------+ 
                               |
                               v
                        +-------------+
                        | etcd        |
                        | desired db  |
                        +------+------+ 
                               |
                               v
        +---------------------------------------------+
        | Controllers                                 |
        | Deployment -> ReplicaSet -> Pod objects     |
        +----------------------+----------------------+
                               |
                               v
                        +-------------+
                        | Scheduler   |
                        | choose node |
                        +------+------+ 
                               |
                               v
+------------------------------+------------------------------+
| Worker Node                                                 |
|                                                             |
|  +----------+      +-------------+      +----------------+   |
|  | kubelet  | ---> | runtime     | ---> | Pod            |   |
|  +----------+      +-------------+      | Spring Boot    |   |
|                                        +--------+-------+   |
|                                                 |           |
|  +----------+      +-------------+              |           |
|  | CNI      |      | kube-proxy  | <------------+           |
|  +----------+      +-------------+                          |
+-------------------------------------------------------------+
                               ^
                               |
                        +-------------+
                        | Service     |
                        +------+------+ 
                               ^
                               |
                        +-------------+
                        | Ingress     |
                        +------+------+ 
                               ^
                               |
                            Client
```

Memory sentence:

```text
Kubernetes receives desired state through API server,
remembers it in etcd,
controllers create the right objects,
scheduler places pods,
kubelet runs them,
services and ingress route traffic,
and observability tells you what reality looks like.
```

---

# 49. Final Production Checklist

```text
[ ] Deployment has correct labels and selectors.
[ ] Service selector matches Pod labels.
[ ] Service port and targetPort are correct.
[ ] Readiness, liveness, and startup probes are correct.
[ ] Spring Boot actuator probes are enabled.
[ ] Resource requests and limits are realistic.
[ ] JVM memory respects container memory limit.
[ ] ConfigMap and Secret references exist.
[ ] ServiceAccount has minimum required RBAC.
[ ] Rollout strategy is safe for zero-downtime update.
[ ] Logs include request id / trace id / version.
[ ] Metrics expose latency, errors, traffic, saturation.
[ ] Traces show downstream calls.
[ ] HPA target metric is meaningful.
[ ] Cluster has capacity or autoscaler enabled.
[ ] PVCs are bound if storage is needed.
[ ] Ingress routes to correct Service.
[ ] DNS resolution works from inside pods.
[ ] NetworkPolicy does not block required traffic.
[ ] Rollback command is known before production deploy.
```

---

# 50. Final Memory Hook

Do not memorize Kubernetes as 100 objects.

Memorize five flows:

```text
1. Desired State Flow
   YAML -> API Server -> etcd -> Controllers

2. Pod Creation Flow
   Deployment -> ReplicaSet -> Pod -> Scheduler -> Kubelet -> Runtime

3. Request Flow
   Client -> DNS -> LB -> Ingress -> Service -> Endpoint -> Pod -> App

4. Scaling Flow
   Metrics -> HPA -> More Pods -> Scheduler -> Nodes -> Capacity

5. Debugging Flow
   Events -> Describe -> Logs -> Metrics -> Traces -> First Broken Link
```

Final sentence:

```text
Kubernetes is not a collection of YAML files.
It is a production control system that continuously turns declared intent into running, reachable, observable applications.
```
