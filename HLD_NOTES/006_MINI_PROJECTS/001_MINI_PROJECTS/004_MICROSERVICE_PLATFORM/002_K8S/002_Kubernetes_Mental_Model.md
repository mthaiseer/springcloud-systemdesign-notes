# 002_Kubernetes_Mental_Model.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why Kubernetes Needs A Mental Model

Most people learn Kubernetes in the wrong order.

They start with:

```text
kubectl
YAML
Pod
Deployment
Service
Namespace
Ingress
```

Then they feel Kubernetes is a collection of random objects.

That is memorization.

The correct order is:

```text
Production Problem
      |
      v
Desired State
      |
      v
Control Loop
      |
      v
Actual State
      |
      v
Self-Healing System
```

Kubernetes is not mainly about YAML.

Kubernetes is about operating applications using a desired-state control system.

One picture:

```text
You describe what you want
          |
          v
Kubernetes keeps making reality match it
```

If you remember only one thing from this chapter, remember this:

```text
Kubernetes = Desired State + Continuous Reconciliation
```

Everything else is a detail.

---

# 2. The Wrong Way To Think About Kubernetes

Bad mental model:

```text
kubectl run creates container
kubectl apply creates pod
kubectl delete removes pod
```

This sounds like Docker commands.

But Kubernetes is not Docker with extra syntax.

Docker style is often imperative:

```text
Run this container now.
Stop this container now.
Restart this container now.
```

Kubernetes style is declarative:

```text
I want 3 healthy replicas of order-service.
Keep them running forever.
Replace bad ones automatically.
```

Diagram:

```text
Imperative Thinking

Human
  |
  | start container
  | stop container
  | restart container
  v
Machine


Declarative Thinking

Human
  |
  | desired state
  v
Kubernetes
  |
  | continuous actions
  v
Cluster
```

Do not memorize objects first.

Understand the operating style first.

---

# 3. Real World Analogy: Thermostat

A thermostat does not ask you to manually turn the heater on and off every minute.

You say:

```text
Desired Temperature = 22°C
```

The room is:

```text
Actual Temperature = 19°C
```

Thermostat compares:

```text
Desired 22
Actual  19
```

Action:

```text
Turn heater ON
```

Later:

```text
Desired 22
Actual  22
```

Action:

```text
Do nothing
```

Kubernetes is similar.

You say:

```text
Desired Replicas = 3
```

Cluster has:

```text
Actual Replicas = 2
```

Kubernetes action:

```text
Create 1 more Pod
```

Mental model:

```text
Thermostat
  Desired Temperature
  Actual Temperature
  Heater

Kubernetes
  Desired Cluster State
  Actual Cluster State
  Controllers
```

ASCII:

```text
+-------------------+
| Desired State     |
| replicas = 3      |
+---------+---------+
          |
          v
+-------------------+
| Compare           |
+---------+---------+
          |
          v
+-------------------+
| Actual State      |
| replicas = 2      |
+---------+---------+
          |
          v
+-------------------+
| Action            |
| create one pod    |
+-------------------+
```

---

# 4. Real World Analogy: Restaurant Manager

Imagine a restaurant.

The owner says:

```text
Always keep 5 chefs working in kitchen.
Always keep 20 tables available.
Always keep delivery counter open.
```

The owner does not say every minute:

```text
Chef-1, stand here.
Chef-2, cook this.
Cleaner, clean table 7.
```

The manager continuously checks reality.

```text
Expected:
5 chefs

Actual:
4 chefs
```

Manager calls one backup chef.

```text
Expected:
20 tables

Actual:
18 clean tables
```

Manager sends cleaning staff.

Kubernetes is that manager.

You are the owner.

Your YAML is the instruction.

Controllers are managers.

Pods are workers.

Nodes are buildings.

Services are reception desks.

Ingress is the main entrance.

```text
Restaurant Owner
      |
      v
Desired Operations
      |
      v
Manager
      |
      v
Kitchen Reality
```

Kubernetes:

```text
Developer
      |
      v
YAML Desired State
      |
      v
Controllers
      |
      v
Cluster Reality
```

---

# 5. The Core Kubernetes Picture

```text
                 USER / DEVELOPER
                       |
                       | kubectl apply
                       v
             +----------------------+
             | Kubernetes API Server|
             +----------+-----------+
                        |
                        v
             +----------------------+
             | Cluster Desired State|
             | stored in etcd       |
             +----------+-----------+
                        |
                        v
             +----------------------+
             | Controllers          |
             | watch + compare      |
             +----------+-----------+
                        |
                        v
             +----------------------+
             | Scheduler / Kubelet  |
             | make it real         |
             +----------+-----------+
                        |
                        v
             +----------------------+
             | Actual Running Pods  |
             +----------------------+
```

This is the heart of Kubernetes.

Not YAML.

Not commands.

This loop.

---

# 6. Kubernetes Is A State Machine

A state machine has:

```text
Current State
Input
Transition
New State
```

Kubernetes has:

```text
Current Cluster State
Desired YAML
Controllers
New Cluster State
```

Example:

```yaml
replicas: 3
```

Current state:

```text
0 pods
```

Transition:

```text
Deployment controller creates ReplicaSet
ReplicaSet controller creates Pod objects
Scheduler assigns Pods to Nodes
Kubelet starts containers
```

New state:

```text
3 running pods
```

Diagram:

```text
YAML
 |
 v
Desired State Written
 |
 v
Controller Transition
 |
 v
Cluster State Changed
 |
 v
Pods Running
```

This is why Kubernetes feels powerful.

You do not manually perform every transition.

You declare the goal.

Kubernetes performs the transitions.

---

# 7. Spring Boot Example: Manual World

You have an Order Service.

```java
package com.example.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

Manual deployment:

```bash
mvn clean package
scp target/order-service.jar server1:/app
ssh server1
java -jar /app/order-service.jar
```

Now you want 3 instances.

```bash
ssh server1
java -jar order-service.jar --server.port=8081

ssh server2
java -jar order-service.jar --server.port=8081

ssh server3
java -jar order-service.jar --server.port=8081
```

Then you configure load balancer manually.

```text
Load Balancer
   |
   +--> server1:8081
   +--> server2:8081
   +--> server3:8081
```

Problems:

```text
Who restarts failed process?
Who updates load balancer?
Who removes bad instance?
Who rolls out version 2?
Who rolls back version 1?
```

This is not a scalable operating model.

---

# 8. Spring Boot Example: Kubernetes World

You build an image:

```text
order-service:1.0.0
```

You declare:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: order-service
  template:
    metadata:
      labels:
        app: order-service
    spec:
      containers:
        - name: order-service
          image: order-service:1.0.0
          ports:
            - containerPort: 8080
```

This means:

```text
Dear Kubernetes,

Always keep 3 running copies of this container.
Each copy should run the order-service image.
Each copy exposes port 8080.
```

It does not mean:

```text
Start 3 containers once and forget.
```

It means:

```text
Continuously protect this desired state.
```

Flow:

```text
Deployment YAML
      |
      v
API Server
      |
      v
etcd stores desired state
      |
      v
Deployment Controller notices
      |
      v
ReplicaSet created
      |
      v
Pods created
      |
      v
Scheduler assigns nodes
      |
      v
Kubelet starts containers
```

---

# 9. Object Mental Model

Kubernetes objects are records of desired state.

They are not just files.

```text
YAML file
   |
   v
API object
   |
   v
Stored in etcd
   |
   v
Watched by controllers
```

Common objects:

```text
Deployment = desired app rollout
ReplicaSet = desired number of pod copies
Pod        = desired container runtime unit
Service    = stable network name and load balancer
ConfigMap  = external configuration
Secret     = sensitive configuration
Ingress    = HTTP entry routing
```

Do not memorize definitions.

Map each object to a production need:

```text
Need multiple app copies       -> Deployment
Need stable access             -> Service
Need app configuration         -> ConfigMap
Need passwords/tokens          -> Secret
Need external HTTP routing     -> Ingress
Need persistent disk           -> PVC
```

---

# 10. Object Relationship Diagram

```text
Deployment
    |
    | creates/manages
    v
ReplicaSet
    |
    | creates/manages
    v
Pod
    |
    | contains
    v
Container
    |
    | runs
    v
Spring Boot Process
```

Expanded:

```text
+-----------------------------+
| Deployment                  |
| replicas: 3                 |
+-------------+---------------+
              |
              v
+-----------------------------+
| ReplicaSet                  |
| maintain 3 matching pods    |
+-------------+---------------+
              |
              v
+-----------------------------+
| Pods                        |
| order-abc                   |
| order-def                   |
| order-ghi                   |
+-------------+---------------+
              |
              v
+-----------------------------+
| Containers                  |
| java -jar app.jar           |
+-----------------------------+
```

If a Pod dies, the ReplicaSet wants the count back.

If you update image version, Deployment manages rollout.

Each object has a responsibility.

---

# 11. Desired State vs Actual State

Desired State:

```text
What you asked for
```

Actual State:

```text
What currently exists
```

Example:

```text
Desired:
3 order-service pods

Actual:
2 running pods
1 crashed pod
```

Kubernetes response:

```text
Replace bad pod
```

Diagram:

```text
Desired State                      Actual State

order-service replicas = 3          Pod A running
                                    Pod B running
                                    Pod C crashed

        |                                  |
        +---------------+------------------+
                        |
                        v
                Controller detects mismatch
                        |
                        v
                New Pod gets created
```

This comparison happens continuously.

That is why Kubernetes can self-heal.

---

# 12. Controllers Are The Brain Workers

A controller is a loop.

Pseudo-code:

```text
while true:
    desired = read_desired_state()
    actual  = read_actual_state()

    if actual != desired:
        take_action()
```

Example controller logic:

```text
Deployment Controller:
    Does the correct ReplicaSet exist?
    If not, create/update it.

ReplicaSet Controller:
    Are enough Pods running?
    If not, create more Pods.

Node Controller:
    Are Nodes healthy?
    If not, mark unavailable.

Endpoint Controller:
    Which Pods are ready behind this Service?
    Update endpoints.
```

ASCII:

```text
+-------------------+
| Watch             |
+---------+---------+
          |
          v
+-------------------+
| Compare           |
+---------+---------+
          |
          v
+-------------------+
| Act               |
+---------+---------+
          |
          v
+-------------------+
| Repeat Forever    |
+-------------------+
```

This is the controller pattern.

---

# 13. Kubernetes Is Not Magic

When you apply YAML, Kubernetes does not instantly create everything with one magical command.

It creates objects.

Controllers react.

Each component does its job.

Example:

```text
kubectl apply -f deployment.yaml
```

Actual internal flow:

```text
kubectl
  |
  v
API Server validates request
  |
  v
etcd stores Deployment object
  |
  v
Deployment Controller sees Deployment
  |
  v
ReplicaSet object created
  |
  v
ReplicaSet Controller sees ReplicaSet
  |
  v
Pod objects created
  |
  v
Scheduler sees unscheduled Pods
  |
  v
Node selected
  |
  v
Kubelet sees Pod assigned to its Node
  |
  v
Container runtime starts container
```

This is the real story.

---

# 14. Control Plane vs Data Plane Mental Model

Control Plane:

```text
Decision making
```

Data Plane:

```text
Actual application running
```

Diagram:

```text
CONTROL PLANE

API Server
Scheduler
Controller Manager
etcd

        |
        | decisions
        v

DATA PLANE

Nodes
Kubelet
Pods
Containers
Services
```

Analogy:

```text
Airport Control Tower = Control Plane
Airplanes + Runways   = Data Plane
```

The tower does not carry passengers.

It coordinates.

Kubernetes control plane does not serve your customer traffic directly.

Your Pods do.

---

# 15. API Server Mental Model

The API Server is the front door of Kubernetes.

Everything talks through it.

```text
kubectl
controllers
scheduler
kubelet
operators

      |
      v

API Server
```

Nobody directly edits etcd.

Correct model:

```text
All cluster changes go through API Server
```

Diagram:

```text
User
 |
 v
kubectl
 |
 v
API Server
 |
 v
etcd
```

Controllers also watch through API Server.

```text
Controller
    |
    | watch deployments
    v
API Server
```

This makes Kubernetes consistent and auditable.

---

# 16. etcd Mental Model

etcd is the cluster state database.

It stores objects like:

```text
Deployments
ReplicaSets
Pods
Services
ConfigMaps
Secrets
Nodes
```

Mental model:

```text
etcd = Kubernetes memory
```

If etcd loses data, Kubernetes loses its memory.

But etcd does not run your application.

It stores desired and observed cluster state.

Diagram:

```text
+-----------------------------+
| etcd                        |
|                             |
| /deployments/order-service  |
| /pods/order-abc             |
| /services/order-service     |
| /nodes/node-1               |
+-----------------------------+
```

Never think:

```text
Pod runs inside etcd
```

Wrong.

Pod runs on Node.

etcd stores Pod record.

---

# 17. Scheduler Mental Model

Scheduler answers:

```text
Where should this Pod run?
```

It does not start the container.

It selects a Node.

Input:

```text
Pod without node
```

Output:

```text
Pod assigned to node-2
```

Diagram:

```text
Pending Pod
    |
    v
Scheduler checks:
  - CPU available?
  - Memory available?
  - Node labels?
  - Taints?
  - Affinity?
    |
    v
Choose Node
    |
    v
Bind Pod to Node
```

Then Kubelet on that Node starts the Pod.

---

# 18. Kubelet Mental Model

Kubelet is the node agent.

It runs on every worker node.

```text
Node
+--------------------------------+
| kubelet                        |
|                                |
| watches API Server             |
| starts/stops containers        |
| reports status                 |
+--------------------------------+
```

Kubelet asks:

```text
Which Pods are assigned to my node?
```

Then:

```text
Start containers
Mount volumes
Run health checks
Report status
Restart failed containers
```

Kubelet is like a local manager on each machine.

Control plane decides.

Kubelet executes locally.

---

# 19. Service Mental Model

Pods are temporary.

They come and go.

```text
Pod A: 10.1.1.5
Pod B: 10.1.1.9
Pod C: 10.1.1.12
```

After restart:

```text
Pod A gone
New Pod D: 10.1.1.30
```

Clients should not track Pod IPs.

Service gives stable identity.

```text
order-service.default.svc.cluster.local
```

Diagram:

```text
Client Pod
   |
   | http://order-service
   v
Service
   |
   +--> Pod A
   +--> Pod B
   +--> Pod C
```

Service means:

```text
Stable name + stable virtual IP + load balancing to ready Pods
```

---

# 20. Label Selector Mental Model

Kubernetes connects objects using labels.

A Service does not manually list Pods.

It selects Pods by labels.

Pod labels:

```yaml
labels:
  app: order-service
```

Service selector:

```yaml
selector:
  app: order-service
```

Diagram:

```text
Service selector:
app = order-service

        |
        v

Matching Pods:

Pod A [app=order-service]
Pod B [app=order-service]
Pod C [app=order-service]
```

Wrong labels cause traffic failure.

```text
Service exists
Pods exist
But selector does not match
Result: no endpoints
```

This is a common production bug.

---

# 21. Namespace Mental Model

Namespace is a logical boundary.

Not a full security wall by itself.

Think:

```text
Cluster
  |
  +-- dev namespace
  +-- staging namespace
  +-- prod namespace
```

Objects with same name can exist in different namespaces.

```text
dev/order-service
prod/order-service
```

Mental model:

```text
Namespace = folder-like grouping for Kubernetes objects
```

Use for:

```text
Environment separation
Team separation
Resource quotas
Access control scope
```

Do not assume namespaces automatically isolate all network traffic.

NetworkPolicy is needed for network isolation.

---

# 22. YAML Mental Model

YAML is not the core idea.

YAML is only the way you describe desired state.

Most Kubernetes YAML follows this structure:

```yaml
apiVersion:
kind:
metadata:
spec:
status:
```

Mental model:

```text
apiVersion = which API shape?
kind       = what object type?
metadata   = name, labels, annotations
spec       = desired state
status     = observed state
```

Important:

```text
You write spec.
Kubernetes writes status.
```

Diagram:

```text
Human writes:

spec:
  replicas: 3

Kubernetes writes:

status:
  availableReplicas: 2
```

This is a powerful mental model.

---

# 23. Spec vs Status

Spec:

```text
What you want
```

Status:

```text
What Kubernetes sees
```

Example:

```yaml
spec:
  replicas: 3

status:
  replicas: 3
  availableReplicas: 2
```

Meaning:

```text
You want 3.
Kubernetes knows only 2 are ready.
```

Controller action:

```text
Keep working until availableReplicas = 3
```

ASCII:

```text
spec.replicas = 3
       |
       v
Controller
       |
       v
status.availableReplicas = 2
       |
       v
Not done yet
```

This appears everywhere in Kubernetes.

---

# 24. Kubernetes Request Lifecycle: Simple Version

```text
kubectl apply
      |
      v
API Server
      |
      v
etcd
      |
      v
Controller
      |
      v
Scheduler
      |
      v
Kubelet
      |
      v
Container Runtime
      |
      v
Running Spring Boot App
```

This is the minimum mental model.

Later chapters will deepen each step.

For now, understand the sequence.

```text
Apply does not directly start container.
Apply stores desired state.
Controllers and node agents make it real.
```

---

# 25. Full Dry Run: Deploy Order Service

You apply:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: order-service
  template:
    metadata:
      labels:
        app: order-service
    spec:
      containers:
        - name: order-service
          image: order-service:1.0.0
          ports:
            - containerPort: 8080
```

Dry run:

```text
1. kubectl sends YAML to API Server.

2. API Server validates:
   - Is apiVersion valid?
   - Is kind valid?
   - Is schema correct?
   - Is user allowed?

3. API Server stores Deployment in etcd.

4. Deployment Controller sees new Deployment.

5. Deployment Controller creates ReplicaSet.

6. ReplicaSet Controller sees desired replicas = 3.

7. ReplicaSet Controller creates 3 Pod objects.

8. Scheduler sees 3 unscheduled Pods.

9. Scheduler chooses Nodes.

10. Kubelet on each chosen Node sees assigned Pod.

11. Kubelet asks container runtime to pull image.

12. Container runtime starts container.

13. Spring Boot starts embedded Tomcat.

14. Kubelet runs readiness probe.

15. Pod becomes Ready.

16. Service can send traffic.
```

Picture:

```text
Developer
   |
   v
Deployment
   |
   v
ReplicaSet
   |
   v
Pods
   |
   v
Containers
   |
   v
Spring Boot
```

---

# 26. Dry Run: Pod Crashes

Initial:

```text
Desired:
3 Pods

Actual:
3 Running Pods
```

Then:

```text
Pod B crashes
```

Actual:

```text
Pod A Running
Pod B Failed
Pod C Running
```

Kubernetes reaction:

```text
Kubelet detects container failure
      |
      v
Restart container if policy allows
```

If Pod is gone:

```text
ReplicaSet detects only 2 active Pods
      |
      v
Creates replacement Pod
```

Diagram:

```text
Pod B dies
   |
   v
Actual != Desired
   |
   v
Controller acts
   |
   v
New Pod appears
```

This is self-healing.

---

# 27. Dry Run: Node Dies

Initial:

```text
Node-1: Pod A
Node-2: Pod B
Node-3: Pod C
```

Failure:

```text
Node-2 X
```

Kubernetes notices:

```text
Node heartbeat missing
```

Eventually:

```text
Pods on Node-2 marked unavailable
```

ReplicaSet sees:

```text
Desired = 3
Available = 2
```

Action:

```text
Create replacement Pod on healthy Node
```

Result:

```text
Node-1: Pod A, Pod D
Node-3: Pod C
```

Diagram:

```text
Node failure
    |
    v
Pod unavailable
    |
    v
ReplicaSet mismatch
    |
    v
New Pod scheduled
    |
    v
Cluster healthy again
```

---

# 28. Dry Run: Rolling Update

Current:

```text
order-service:1.0.0
```

You update:

```text
order-service:1.1.0
```

Kubernetes does not kill all old Pods at once.

It gradually changes.

```text
Step 1:
Old Old Old

Step 2:
New Old Old

Step 3:
New New Old

Step 4:
New New New
```

If new version fails readiness:

```text
New Pod not ready
     |
     v
Traffic not sent
     |
     v
Rollout can pause/fail
```

Mental model:

```text
Deployment controls safe replacement
```

---

# 29. Spring Boot Readiness Mental Model

Spring Boot process may be running.

But app may not be ready.

Example:

```text
Java process started
Tomcat started
Database connection not ready
Cache warmup not done
Migration running
```

Readiness answers:

```text
Can this Pod receive user traffic now?
```

Kubernetes Service should only route to ready Pods.

Flow:

```text
Pod starts
   |
   v
Spring Boot starts
   |
   v
Readiness probe fails
   |
   v
No traffic
   |
   v
DB connected
   |
   v
Readiness probe passes
   |
   v
Traffic allowed
```

Actuator config:

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
```

Kubernetes probe:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
```

---

# 30. Production Story: Wrong Label Breaks Traffic

Deployment:

```yaml
metadata:
  labels:
    app: order
```

Service:

```yaml
selector:
  app: order-service
```

Problem:

```text
Service selector does not match Pod labels
```

Symptoms:

```text
Pod Running
Service Exists
DNS Works
But Requests Fail
```

Debug:

```bash
kubectl get pods --show-labels
kubectl describe service order-service
kubectl get endpoints order-service
```

Expected endpoints:

```text
10.1.1.5:8080
10.1.1.9:8080
```

Broken endpoints:

```text
<none>
```

Mindset:

```text
In Kubernetes, objects connect through labels.
Wrong label = invisible connection broken.
```

---

# 31. Production Story: App Running But Not Ready

Symptoms:

```text
kubectl get pods
order-service-abc   0/1 Running
```

Common cause:

```text
Container process is alive
Readiness probe failing
```

Debug:

```bash
kubectl describe pod order-service-abc
kubectl logs order-service-abc
```

Possible reasons:

```text
/actuator/health/readiness returns DOWN
DB not reachable
Wrong profile
Missing environment variable
App starts too slowly
```

Mental model:

```text
Running does not mean Ready.
Ready means safe for traffic.
```

---

# 32. Production Story: ImagePullBackOff

YAML:

```yaml
image: registry.example.com/order-service:1.0.0
```

Pod status:

```text
ImagePullBackOff
```

Meaning:

```text
Kubelet cannot pull image
```

Possible causes:

```text
Wrong image tag
Registry authentication missing
Image does not exist
Network issue
Registry unavailable
```

Debug:

```bash
kubectl describe pod order-service-abc
```

Look for events:

```text
Failed to pull image
Unauthorized
Not found
```

Mental model:

```text
Pod object exists.
Container cannot start because image is unavailable.
```

---

# 33. Production Story: CrashLoopBackOff

Pod status:

```text
CrashLoopBackOff
```

Meaning:

```text
Container starts
Then crashes
Kubernetes retries
Backoff delay increases
```

Common Spring Boot causes:

```text
Missing DB_HOST
Wrong DB password
Port conflict inside container
Migration failure
Missing config
OutOfMemoryError
```

Debug:

```bash
kubectl logs order-service-abc
kubectl logs order-service-abc --previous
kubectl describe pod order-service-abc
```

Mental model:

```text
Kubernetes can restart your app.
Kubernetes cannot fix bad app config.
```

---

# 34. What Kubernetes Does Not Do

Kubernetes does not automatically:

```text
Fix bad Java code
Fix slow SQL queries
Design your database
Make app stateless
Create correct readiness endpoint
Secure secrets perfectly
Guarantee zero downtime if probes are wrong
```

Kubernetes gives orchestration.

You still need good application engineering.

Bad app on Kubernetes is still bad app.

Diagram:

```text
Bad App
  |
  v
Kubernetes
  |
  v
Still Bad, Just Automated
```

Important lesson:

```text
Kubernetes amplifies good engineering.
It does not replace it.
```

---

# 35. Beginner Mistakes

```text
Mistake 1:
Thinking kubectl apply directly starts containers.
Correct:
It stores desired state. Controllers act later.

Mistake 2:
Thinking Pod IP is stable.
Correct:
Use Service.

Mistake 3:
Thinking Running means traffic-ready.
Correct:
Use readiness.

Mistake 4:
Ignoring labels.
Correct:
Labels connect objects.

Mistake 5:
Putting everything in one YAML without understanding object relationships.
Correct:
Understand Deployment -> ReplicaSet -> Pod -> Container.

Mistake 6:
Using Kubernetes before understanding Docker images.
Correct:
Image must be production-safe first.

Mistake 7:
Thinking Kubernetes replaces system design.
Correct:
It solves orchestration, not product architecture.
```

---

# 36. Debugging Mindset: Layer By Layer

Use this order:

```text
1. Did YAML apply successfully?
2. Does object exist?
3. Is desired state correct?
4. Did controller create child objects?
5. Are Pods scheduled?
6. Did image pull?
7. Did container start?
8. Is app healthy?
9. Is app ready?
10. Does Service select Pod?
11. Does DNS resolve?
12. Does traffic reach app?
```

Commands:

```bash
kubectl get deployment
kubectl describe deployment order-service

kubectl get rs
kubectl get pods -o wide
kubectl describe pod <pod-name>

kubectl logs <pod-name>
kubectl logs <pod-name> --previous

kubectl get svc
kubectl describe svc order-service
kubectl get endpoints order-service
```

Mindset:

```text
Never guess randomly.
Follow the chain.
```

---

# 37. Interview Questions

## What is the core mental model of Kubernetes?

Kubernetes is a desired-state orchestration system. Developers declare the desired state of applications and infrastructure objects. Kubernetes controllers continuously compare desired state with actual state and take actions to reconcile the difference.

## Is Kubernetes imperative or declarative?

Kubernetes is mostly declarative. You describe what you want, such as three replicas of a service, and Kubernetes works continuously to make and keep that true.

## What is reconciliation?

Reconciliation is the loop where Kubernetes compares desired state with actual state and performs actions to reduce the difference. For example, if desired replicas are three but only two Pods are running, Kubernetes creates another Pod.

## What does the API Server do?

The API Server is the front door of the cluster. kubectl, controllers, scheduler, and kubelets communicate through it. It validates requests, enforces access control, and stores state in etcd.

## What is etcd?

etcd is the strongly consistent key-value store used as Kubernetes' state database. It stores cluster objects like Pods, Deployments, Services, ConfigMaps, Secrets, and Nodes.

## What does the scheduler do?

The scheduler chooses which Node should run a pending Pod based on resources, constraints, taints, tolerations, affinity, and other scheduling rules.

## What does kubelet do?

Kubelet runs on each Node. It watches for Pods assigned to its Node, starts containers through the container runtime, monitors health, and reports status back to the API Server.

## Why do we need Services?

Pods are temporary and their IPs can change. A Service gives a stable network identity and load balances traffic to matching ready Pods.

## Why are labels important?

Labels are how Kubernetes connects objects. Services select Pods using labels. ReplicaSets manage Pods using labels. Wrong labels often break traffic routing or ownership relationships.

---

# 38. Cheat Sheet

```text
Kubernetes              = desired-state orchestration system
Desired State           = what you want
Actual State            = what currently exists
Reconciliation          = making actual match desired
Controller              = watch + compare + act loop
API Server              = front door of Kubernetes
etcd                    = cluster state database
Scheduler               = chooses Node for Pod
Kubelet                 = node agent that starts containers
Deployment              = manages app rollout
ReplicaSet              = keeps desired Pod count
Pod                     = smallest deployable runtime unit
Container               = running application process
Service                 = stable network access to Pods
Label                   = identity tag for object selection
Selector                = query that matches labels
Namespace               = logical grouping boundary
Spec                    = desired state
Status                  = observed state
```

Core flow:

```text
kubectl apply
   |
   v
API Server
   |
   v
etcd
   |
   v
Controllers
   |
   v
Scheduler
   |
   v
Kubelet
   |
   v
Container Runtime
   |
   v
Running Application
```

---

# 39. One Picture To Remember

```text
                         DEVELOPER
                            |
                            | writes desired state
                            v
                     +--------------+
                     | YAML / spec  |
                     +------+-------+
                            |
                            | kubectl apply
                            v
                     +--------------+
                     | API Server   |
                     +------+-------+
                            |
                            v
                     +--------------+
                     | etcd         |
                     | cluster db   |
                     +------+-------+
                            |
                            v
             +-----------------------------+
             | Controllers                 |
             | watch -> compare -> act     |
             +-------------+---------------+
                           |
                           v
                     +-------------+
                     | Scheduler   |
                     | choose node |
                     +------+------+
                            |
                            v
                     +--------------+
                     | Kubelet      |
                     | start pod    |
                     +------+-------+
                            |
                            v
                     +--------------+
                     | Pod          |
                     | Container    |
                     | Spring Boot  |
                     +--------------+

Rule:

You do not manually keep the cluster healthy.
You describe the desired state.
Kubernetes continuously reconciles reality toward it.
```

---

# 40. Final Production Checklist

```text
[ ] I understand desired state vs actual state.
[ ] I understand spec vs status.
[ ] I understand Deployment -> ReplicaSet -> Pod -> Container.
[ ] I understand that API Server is the cluster front door.
[ ] I understand that etcd stores cluster state.
[ ] I understand that controllers run reconciliation loops.
[ ] I understand that scheduler chooses nodes but does not start containers.
[ ] I understand that kubelet starts containers on its node.
[ ] I understand that Services give stable access to temporary Pods.
[ ] I understand that labels and selectors connect Kubernetes objects.
[ ] I understand that Running does not mean Ready.
[ ] I understand that Kubernetes restarts bad containers but cannot fix bad app config.
[ ] I can debug from Deployment to Pod to Service to logs to endpoints.
```

---

# 41. Final Memory Hook

Do not memorize Kubernetes as many objects.

Remember it as one machine:

```text
Desired State Machine
```

Everything else is a helper.

```text
Deployment helps express desired app rollout.
ReplicaSet helps maintain pod count.
Pod helps run containers.
Service helps reach changing pods.
Controller helps reconcile.
Kubelet helps execute on a node.
Scheduler helps place work.
API Server helps coordinate.
etcd helps remember.
```

Final sentence:

```text
Kubernetes is not a place where containers run.
Kubernetes is a system that continuously turns desired state into actual running production reality.
```
