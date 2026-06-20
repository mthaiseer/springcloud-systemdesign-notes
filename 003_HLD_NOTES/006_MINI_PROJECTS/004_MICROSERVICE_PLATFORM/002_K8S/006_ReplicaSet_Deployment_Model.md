# 006_ReplicaSet_Deployment_Model.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why ReplicaSet And Deployment Exist

Most developers first meet Kubernetes through this command:

```bash
kubectl apply -f deployment.yaml
```

Then they see objects like:

```text
Deployment
ReplicaSet
Pod
```

At first this feels like unnecessary complexity.

Why not just run Pods directly?

Because production does not need "one container started once".

Production needs this:

```text
Always keep N healthy copies of this application running.
Replace unhealthy copies.
Roll out new versions safely.
Rollback if the new version is bad.
Do not send traffic to broken Pods.
Do not require a human at 3 AM to restart everything.
```

That is why Kubernetes separates responsibilities:

```text
Pod        = one runnable unit
ReplicaSet = keep correct number of Pods
Deployment = manage versions and rollout strategy
```

One picture:

```text
Deployment
   |
   | controls rollout/version
   v
ReplicaSet
   |
   | controls count
   v
Pods
   |
   | run containers
   v
Spring Boot Process
```

If you remember only one thing from this chapter, remember this:

```text
Deployment is for application lifecycle.
ReplicaSet is for replica count.
Pod is for runtime execution.
```

Do not memorize definitions.

Understand the production problem each object solves.

---

# 2. The Wrong Mental Model

Wrong model:

```text
Deployment creates Pod directly.
ReplicaSet is some hidden object.
Pod is just a container.
```

This creates confusion when debugging.

For example:

```bash
kubectl get deploy
kubectl get rs
kubectl get pods
```

You may see:

```text
NAME                     READY
order-service            3/3

NAME                                DESIRED CURRENT READY
order-service-7c9d9f6b5              3       3       3

NAME                                      READY STATUS
order-service-7c9d9f6b5-abcde             1/1   Running
order-service-7c9d9f6b5-fghij             1/1   Running
order-service-7c9d9f6b5-klmno             1/1   Running
```

If you do not understand ownership, this looks random.

Correct model:

```text
Deployment owns ReplicaSet.
ReplicaSet owns Pods.
Kubelet runs containers inside Pods.
```

ASCII:

```text
+-----------------------------+
| Deployment                  |
| name: order-service         |
| image: order-service:1.0.0  |
| replicas: 3                 |
+--------------+--------------+
               |
               | creates/manages
               v
+-----------------------------+
| ReplicaSet                  |
| name: order-service-7c9d... |
| desired pods: 3             |
+--------------+--------------+
               |
               | creates/manages
               v
+-----------------------------+
| Pods                        |
| order-service-...-abcde     |
| order-service-...-fghij     |
| order-service-...-klmno     |
+-----------------------------+
```

Do not think of these as YAML objects only.

Think of them as production managers with different responsibilities.

---

# 3. Real World Analogy: Restaurant Chain

Imagine a restaurant chain.

The company owner says:

```text
For the Bucharest branch, always keep 3 chefs available for lunch service.
When we introduce a new recipe, replace chefs gradually after training.
If the new recipe fails customer quality checks, stop rollout.
```

There are three levels:

```text
Business Rollout Manager
    |
    v
Shift Manager
    |
    v
Chefs
```

Mapping:

```text
Business Rollout Manager = Deployment
Shift Manager            = ReplicaSet
Chefs                    = Pods
Food cooking process     = Container/Spring Boot app
```

Diagram:

```text
Restaurant Company
   |
   | decides version/recipe rollout
   v
Branch Rollout Manager
   |
   | creates shift plan
   v
Shift Manager
   |
   | keeps 3 chefs active
   v
Chefs
   |
   | cook food
   v
Customers
```

Kubernetes:

```text
Deployment
   |
   | decides version/rollout
   v
ReplicaSet
   |
   | keeps 3 Pods active
   v
Pods
   |
   | run app containers
   v
Users
```

Important distinction:

```text
ReplicaSet does not understand rollout strategy deeply.
It mainly cares about count.

Deployment understands application version transitions.
It manages old ReplicaSet and new ReplicaSet during rollout.
```

This is why both exist.

---

# 4. The Core Picture

```text
                         Developer
                             |
                             | kubectl apply
                             v
                    +-------------------+
                    | Deployment YAML   |
                    | replicas: 3       |
                    | image: v1         |
                    +---------+---------+
                              |
                              v
                    +-------------------+
                    | API Server        |
                    +---------+---------+
                              |
                              v
                    +-------------------+
                    | etcd stores spec  |
                    +---------+---------+
                              |
                              v
                    +-------------------+
                    | Deployment Ctrl   |
                    | creates RS        |
                    +---------+---------+
                              |
                              v
                    +-------------------+
                    | ReplicaSet Ctrl   |
                    | creates Pods      |
                    +---------+---------+
                              |
                              v
                    +-------------------+
                    | Scheduler         |
                    | chooses Nodes     |
                    +---------+---------+
                              |
                              v
                    +-------------------+
                    | Kubelet           |
                    | starts containers |
                    +---------+---------+
                              |
                              v
                    +-------------------+
                    | Running App       |
                    +-------------------+
```

This chapter is about the middle part:

```text
Deployment -> ReplicaSet -> Pod
```

If you understand this chain, many Kubernetes commands become obvious.

---

# 5. Pod Is Not Enough

A Pod is the smallest deployable unit.

You can create a Pod directly:

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: order-service
spec:
  containers:
    - name: order-service
      image: order-service:1.0.0
      ports:
        - containerPort: 8080
```

This creates one Pod.

But production problems begin immediately:

```text
What if the Pod is deleted?
What if the Node dies?
How do we keep 3 copies?
How do we update image v1 to v2?
How do we rollback?
How do we avoid downtime?
```

A bare Pod is like starting one Java process manually:

```bash
java -jar order-service.jar
```

It may run.

But it does not provide a production operating model.

Diagram:

```text
Bare Pod
   |
   v
One App Instance
   |
   v
No rollout manager
No replica manager
No version history
```

That is why you almost never deploy long-running production services as bare Pods.

You use Deployments.

---

# 6. ReplicaSet Mental Model

ReplicaSet answers one question:

```text
Are the correct number of matching Pods running?
```

Example desired state:

```text
replicas = 3
selector = app=order-service
```

Actual state:

```text
2 matching Pods running
```

ReplicaSet action:

```text
Create 1 more Pod
```

Pseudo-code:

```text
while true:
    desired = 3
    actual = count_pods_matching_selector(app=order-service)

    if actual < desired:
        create_pods(desired - actual)

    if actual > desired:
        delete_extra_pods(actual - desired)
```

ASCII:

```text
+--------------------------+
| ReplicaSet               |
| desired replicas = 3     |
| selector: app=order      |
+------------+-------------+
             |
             v
+--------------------------+
| Count matching Pods      |
+------------+-------------+
             |
             v
+--------------------------+
| Actual = 2               |
+------------+-------------+
             |
             v
+--------------------------+
| Create 1 Pod             |
+--------------------------+
```

ReplicaSet is a count-protection controller.

It is not mainly a rollout tool.

---

# 7. Deployment Mental Model

Deployment answers a bigger question:

```text
How should this application version be operated over time?
```

It controls:

```text
Replica count
Pod template
Rolling update
Rollback
Version transition
ReplicaSet history
Availability during update
```

When you create a Deployment, Kubernetes creates a ReplicaSet.

When you change the Pod template, Kubernetes creates a new ReplicaSet.

Example:

```text
Deployment image: order-service:1.0.0
        |
        v
ReplicaSet A -> Pods running v1
```

After update:

```text
Deployment image: order-service:1.1.0
        |
        +--> ReplicaSet A -> old Pods v1 scaled down
        |
        +--> ReplicaSet B -> new Pods v2 scaled up
```

Diagram:

```text
Before update:

Deployment
   |
   v
ReplicaSet-v1
   |
   +--> Pod v1
   +--> Pod v1
   +--> Pod v1

During update:

Deployment
   |
   +--> ReplicaSet-v1  desired: 2
   |       +--> Pod v1
   |       +--> Pod v1
   |
   +--> ReplicaSet-v2  desired: 1
           +--> Pod v2

After update:

Deployment
   |
   +--> ReplicaSet-v1  desired: 0
   |
   +--> ReplicaSet-v2  desired: 3
           +--> Pod v2
           +--> Pod v2
           +--> Pod v2
```

Deployment is the real object you should usually write.

---

# 8. Spring Boot Example: Why One Instance Is Not Enough

Suppose you have this Spring Boot service:

```java
package com.example.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}

@RestController
class OrderController {
    @GetMapping("/orders/health-business")
    public String businessHealth() {
        return "order-service-business-ok";
    }
}
```

Manual deployment:

```bash
java -jar order-service.jar
```

This creates one process.

If it crashes:

```text
User traffic fails until human restarts it.
```

If traffic increases:

```text
One instance may be overloaded.
```

If you need deployment without downtime:

```text
You must manually start new version, update load balancer, drain old version.
```

Kubernetes Deployment changes the operating model:

```text
I want 3 copies of order-service.
I want rolling update when image changes.
I want rollback capability.
I want readiness-aware traffic.
```

This is not just container execution.

This is application operation.

---

# 9. Deployment YAML Deep Walkthrough

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
  labels:
    app: order-service
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

Mental model:

```text
apiVersion/kind:
  What type of object is this?

metadata:
  What is this Deployment called?

spec.replicas:
  How many Pods should exist?

spec.selector:
  Which Pods belong to this Deployment/ReplicaSet?

spec.template:
  What should each Pod look like?
```

Very important:

```text
Deployment does not create containers directly.
Deployment creates ReplicaSet.
ReplicaSet creates Pods from template.
Kubelet starts containers from Pod spec.
```

Diagram:

```text
Deployment.spec.template
        |
        v
ReplicaSet copies template
        |
        v
Pod objects created
        |
        v
Container runtime starts image
```

The Pod template is like a stamp.

ReplicaSet stamps out Pods from it.

---

# 10. Selector And Template Labels

This is one of the most important concepts.

```yaml
selector:
  matchLabels:
    app: order-service

template:
  metadata:
    labels:
      app: order-service
```

The selector and Pod template labels must match.

Why?

Because ReplicaSet uses selector to find Pods it owns.

```text
ReplicaSet selector:
app = order-service

Pod label:
app = order-service

Result:
ReplicaSet recognizes this Pod.
```

ASCII:

```text
ReplicaSet
selector: app=order-service
        |
        v
Search Pods
        |
        +--> Pod A [app=order-service] MATCH
        +--> Pod B [app=payment-service] NO MATCH
        +--> Pod C [app=order-service] MATCH
```

Production mistake:

```yaml
selector:
  matchLabels:
    app: order-service

template:
  metadata:
    labels:
      app: order
```

Result:

```text
ReplicaSet cannot manage the Pods correctly.
Deployment may be rejected or traffic may break depending on object mismatch.
```

Mental model:

```text
Labels are ownership wires.
Wrong label means invisible wire is broken.
```

---

# 11. Owner References

Kubernetes tracks parent-child relationships.

```text
Deployment owns ReplicaSet.
ReplicaSet owns Pods.
```

This is stored using owner references.

You can see this indirectly:

```bash
kubectl describe pod <pod-name>
```

You may see:

```text
Controlled By: ReplicaSet/order-service-7c9d9f6b5
```

For ReplicaSet:

```bash
kubectl describe rs order-service-7c9d9f6b5
```

You may see:

```text
Controlled By: Deployment/order-service
```

ASCII:

```text
Deployment/order-service
        |
        | owner reference
        v
ReplicaSet/order-service-7c9d9f6b5
        |
        | owner reference
        v
Pod/order-service-7c9d9f6b5-abcde
```

This matters for deletion.

If you delete the Deployment:

```text
Kubernetes garbage collection can delete owned ReplicaSets and Pods.
```

Do not think objects are independent files.

They form a controlled object graph.

---

# 12. Pod Name Pattern

Deployment Pods often look like this:

```text
order-service-7c9d9f6b5-abcde
```

Breakdown:

```text
order-service      = Deployment name
7c9d9f6b5          = ReplicaSet hash
abcde              = Pod random suffix
```

Diagram:

```text
order-service-7c9d9f6b5-abcde
|------------| |-------| |---|
 Deployment     RS hash   Pod suffix
```

This helps debugging.

If you see multiple ReplicaSet hashes:

```text
order-service-7c9d9f6b5-abcde
order-service-7c9d9f6b5-fghij
order-service-68d4b88c9-klmno
```

It usually means rollout history exists.

```text
7c9d9f6b5 = old or current ReplicaSet
68d4b88c9 = another version ReplicaSet
```

During rolling updates, you may temporarily see Pods from both hashes.

That is normal.

---

# 13. Full Dry Run: Creating A Deployment

You apply:

```bash
kubectl apply -f order-deployment.yaml
```

Internal dry run:

```text
1. kubectl sends Deployment YAML to API Server.

2. API Server validates schema:
   - apps/v1 exists
   - Deployment kind exists
   - selector is valid
   - template is valid

3. API Server checks authorization.

4. Deployment object is stored in etcd.

5. Deployment controller watches Deployments.

6. Deployment controller sees new Deployment.

7. It calculates Pod template hash.

8. It creates ReplicaSet with that hash.

9. ReplicaSet controller sees desired replicas = 3.

10. ReplicaSet controller creates 3 Pod objects.

11. Scheduler sees Pods with no assigned node.

12. Scheduler selects nodes.

13. Kubelet on each selected node sees assigned Pod.

14. Kubelet pulls image.

15. Container runtime starts container.

16. Spring Boot starts.

17. Probes run.

18. Pods become Ready.

19. Deployment status updates availableReplicas.
```

ASCII:

```text
kubectl apply
   |
   v
Deployment stored
   |
   v
Deployment Controller
   |
   v
ReplicaSet created
   |
   v
ReplicaSet Controller
   |
   v
Pods created
   |
   v
Scheduler assigns Nodes
   |
   v
Kubelet starts containers
   |
   v
Deployment becomes Available
```

Notice:

```text
kubectl apply does not start containers directly.
```

It starts a chain of reconciliation.

---

# 14. Full Dry Run: Pod Deleted Manually

Initial state:

```text
Deployment desired replicas: 3
ReplicaSet desired replicas: 3
Actual Pods: 3
```

You run:

```bash
kubectl delete pod order-service-7c9d9f6b5-abcde
```

What happens?

```text
1. Pod is deleted.

2. Actual matching Pods becomes 2.

3. ReplicaSet controller detects mismatch.

4. ReplicaSet still wants 3 Pods.

5. ReplicaSet creates 1 replacement Pod.

6. Scheduler assigns it.

7. Kubelet starts it.
```

Diagram:

```text
Before:
RS desired = 3
Pods = A B C

Delete B:
RS desired = 3
Pods = A C

Controller:
actual 2 < desired 3

After:
Pods = A C D
```

Important lesson:

```text
Deleting a managed Pod is not the same as scaling down.
```

The owner recreates it.

To change count, change Deployment replicas.

---

# 15. Scaling Mental Model

Scaling means changing desired replica count.

```bash
kubectl scale deployment order-service --replicas=5
```

This changes:

```text
Deployment.spec.replicas = 5
```

Then:

```text
Deployment ensures ReplicaSet desired count becomes 5.
ReplicaSet creates more Pods.
```

Flow:

```text
User changes desired replicas
        |
        v
Deployment spec updated
        |
        v
ReplicaSet desired count updated
        |
        v
ReplicaSet creates/deletes Pods
```

Scale up:

```text
3 Pods -> 5 Pods
Create 2 Pods
```

Scale down:

```text
5 Pods -> 2 Pods
Delete 3 Pods
```

ASCII:

```text
Scale Up

Desired: 5
Actual:  3
Action:  +2 Pods

Scale Down

Desired: 2
Actual:  5
Action:  -3 Pods
```

Scaling is not magic.

It is desired count reconciliation.

---

# 16. Spring Boot Scaling Example

Imagine this endpoint:

```java
@RestController
class CheckoutController {
    @GetMapping("/checkout")
    public String checkout() throws InterruptedException {
        Thread.sleep(100);
        return "checkout-ok";
    }
}
```

One instance can handle limited traffic.

Very simplified mental model:

```text
One Pod handles requests using Tomcat worker threads.
More Pods means more independent JVMs.
```

ASCII:

```text
Service
  |
  +--> Pod 1: JVM + Tomcat threads
  +--> Pod 2: JVM + Tomcat threads
  +--> Pod 3: JVM + Tomcat threads
```

When you scale Deployment from 3 to 6:

```text
Service gets more ready endpoints.
Traffic can spread across more Pods.
```

But Kubernetes scaling does not fix everything:

```text
If database is bottleneck, more Pods may overload DB.
If Redis connection limit is low, more Pods may cause connection storms.
If app is stateful in memory, scaling may break behavior.
```

Production mindset:

```text
Deployment scaling increases app instance count.
It does not automatically scale downstream dependencies.
```

---

# 17. Deployment Update Mental Model

A Deployment update happens when the Pod template changes.

Examples that trigger new ReplicaSet:

```text
image changes
container command changes
env var changes
ports change
labels in template change
probe config changes
resource requests/limits change
```

Example:

```bash
kubectl set image deployment/order-service order-service=order-service:1.1.0
```

Before:

```text
Deployment template: image v1
ReplicaSet A: 3 Pods
```

After:

```text
Deployment template: image v2
ReplicaSet B created
ReplicaSet A scaled down gradually
ReplicaSet B scaled up gradually
```

ASCII:

```text
Image v1:
Deployment -> RS-A -> v1 v1 v1

Image v2 rollout:
Deployment
   +-> RS-A -> v1 v1
   +-> RS-B -> v2

Later:
Deployment
   +-> RS-A ->
   +-> RS-B -> v2 v2 v2
```

Deployment creates versioned ReplicaSets.

That is the key.

---

# 18. Rolling Update Strategy

Default Deployment strategy is RollingUpdate.

Meaning:

```text
Do not kill all old Pods at once.
Gradually create new Pods and remove old Pods.
```

Important fields:

```yaml
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxSurge: 1
    maxUnavailable: 0
```

Mental model:

```text
maxSurge:
  How many extra Pods can exist temporarily above desired count?

maxUnavailable:
  How many desired Pods can be unavailable during rollout?
```

For replicas = 3:

```text
maxSurge: 1
maxUnavailable: 0
```

Means:

```text
Can temporarily run 4 Pods.
Must keep at least 3 available.
```

Diagram:

```text
Step 0: v1 v1 v1
Step 1: v1 v1 v1 v2    extra Pod allowed
Step 2: v1 v1 v2       remove one old
Step 3: v1 v1 v2 v2    add one new
Step 4: v1 v2 v2       remove one old
Step 5: v1 v2 v2 v2    add one new
Step 6: v2 v2 v2       remove final old
```

This is how Kubernetes tries to avoid downtime.

But readiness probes must be correct.

---

# 19. Recreate Strategy

Another strategy:

```yaml
strategy:
  type: Recreate
```

Meaning:

```text
Delete old Pods first.
Then create new Pods.
```

Diagram:

```text
Before:
v1 v1 v1

Recreate:
--- delete all old ---

Temporary:
<no pods>

After:
v2 v2 v2
```

When useful:

```text
Apps that cannot run old and new versions together
Single-writer workloads
Legacy apps with shared locks
Certain migration-sensitive systems
```

Risk:

```text
Downtime is expected unless protected by external design.
```

For typical stateless Spring Boot services, RollingUpdate is preferred.

---

# 20. Readiness Controls Rollout Safety

Deployment rollout depends heavily on Pod readiness.

A new Pod is not considered available immediately just because the container started.

Flow:

```text
New Pod created
   |
   v
Container starts
   |
   v
Spring Boot starts
   |
   v
Readiness probe checks /actuator/health/readiness
   |
   v
Only if Ready, rollout continues safely
```

Spring Boot Actuator config:

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

Deployment probe:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 20
  periodSeconds: 5
```

Mental model:

```text
Deployment asks:
Is the new Pod actually ready to replace old traffic?
```

If readiness is wrong, rolling update can become dangerous.

```text
Bad readiness = Kubernetes may send traffic too early.
Over-strict readiness = rollout may get stuck.
```

---

# 21. Liveness Is Different From Readiness

Readiness:

```text
Should this Pod receive traffic?
```

Liveness:

```text
Should this container be restarted?
```

Do not mix them.

Diagram:

```text
Readiness failed
   |
   v
Remove from Service endpoints
   |
   v
Do not restart automatically just because of readiness

Liveness failed
   |
   v
Restart container
```

Example:

```text
DB temporarily down:
Readiness may fail because app cannot serve traffic.
But liveness should not necessarily fail.
Restarting all Pods may make outage worse.
```

Bad liveness design:

```text
liveness checks DB
DB hiccup happens
All Pods restart
Cold start storm
Incident gets worse
```

Good model:

```text
Liveness checks if JVM is stuck/dead.
Readiness checks if app can serve traffic.
```

Deployment rollout safety mainly depends on readiness.

---

# 22. minReadySeconds

Deployment can require a Pod to stay ready for some time before counting it as available.

```yaml
spec:
  minReadySeconds: 15
```

Meaning:

```text
A new Pod must be Ready for 15 continuous seconds before it is considered Available.
```

Why useful?

Some apps briefly become ready, then crash.

Without minReadySeconds:

```text
Pod Ready for 1 second
Deployment proceeds
Old Pod removed
New Pod crashes
Availability drops
```

With minReadySeconds:

```text
Pod must prove stability for 15 seconds
Then rollout continues
```

ASCII:

```text
Pod starts
   |
   v
Ready at t=20s
   |
   v
Stay Ready until t=35s
   |
   v
Count as Available
```

This is a small field with big production value.

---

# 23. progressDeadlineSeconds

Deployment should not wait forever during a broken rollout.

```yaml
spec:
  progressDeadlineSeconds: 120
```

Meaning:

```text
If the rollout does not make progress within 120 seconds, mark it as failed.
```

Example failure:

```text
New image has bad DB password.
Pods enter CrashLoopBackOff.
New ReplicaSet never becomes available.
```

Deployment status may show:

```text
ProgressDeadlineExceeded
```

Debug:

```bash
kubectl rollout status deployment/order-service
kubectl describe deployment order-service
kubectl get rs
kubectl get pods
kubectl logs <new-pod> --previous
```

Mental model:

```text
progressDeadlineSeconds is the rollout smoke alarm.
```

It does not automatically fix your application.

It tells you rollout is stuck.

---

# 24. Rollback Mental Model

Because Deployment keeps old ReplicaSets, rollback is possible.

Command:

```bash
kubectl rollout undo deployment/order-service
```

Before rollback:

```text
ReplicaSet v1: desired 0
ReplicaSet v2: desired 3
```

After rollback:

```text
ReplicaSet v1: desired 3
ReplicaSet v2: desired 0
```

Diagram:

```text
Bad rollout:
Deployment
   +-> RS-v1 desired 0
   +-> RS-v2 desired 3   BAD

Rollback:
Deployment
   +-> RS-v1 desired 3   GOOD
   +-> RS-v2 desired 0
```

Important:

```text
Rollback changes Pod template back to previous revision.
It does not rollback database migrations automatically.
It does not rollback external systems.
```

Production rule:

```text
Deployment rollback is safe only if app versions and database changes are backward-compatible.
```

---

# 25. Revision History

Deployment stores rollout history through ReplicaSets.

```yaml
spec:
  revisionHistoryLimit: 5
```

Meaning:

```text
Keep old ReplicaSets for rollback, up to a limit.
```

Check history:

```bash
kubectl rollout history deployment/order-service
```

You may see:

```text
REVISION  CHANGE-CAUSE
1         <none>
2         <none>
3         <none>
```

Better practice:

```bash
kubectl annotate deployment/order-service \
  kubernetes.io/change-cause="deploy order-service:1.1.0"
```

Mental model:

```text
Deployment keeps enough old rollout structure to move backward.
```

But history is not infinite.

If old ReplicaSets are cleaned up, rollback options reduce.

---

# 26. Production Story: Bad Image Tag

You update Deployment:

```yaml
image: registry.example.com/order-service:1.1.0
```

But tag does not exist.

Symptoms:

```text
kubectl get pods
order-service-xxxxx-yyyyy   0/1   ImagePullBackOff
```

Rollout status:

```bash
kubectl rollout status deployment/order-service
```

May hang and later fail.

What happened internally?

```text
Deployment created new ReplicaSet.
ReplicaSet created new Pods.
Kubelet tried to pull image.
Registry returned not found.
Pods never became Ready.
Old Pods may remain depending on rolling update settings.
```

Debug commands:

```bash
kubectl describe pod <pod-name>
kubectl describe deployment order-service
kubectl get rs
```

Look in Events:

```text
Failed to pull image
manifest unknown
unauthorized
```

Mental model:

```text
Deployment rollout can fail below the Deployment layer.
Always debug down the ownership chain.
```

---

# 27. Production Story: New Version Starts But Fails Readiness

New Spring Boot version starts successfully.

Logs:

```text
Started OrderServiceApplication in 12.4 seconds
```

But Pod is not Ready:

```text
0/1 Running
```

Possible reasons:

```text
/actuator/health/readiness returns DOWN
DB connection fails
Kafka dependency unavailable
Redis auth wrong
Readiness path wrong
Management port mismatch
```

Debug:

```bash
kubectl describe pod <pod-name>
kubectl logs <pod-name>
kubectl exec -it <pod-name> -- wget -qO- localhost:8080/actuator/health/readiness
```

Internal result:

```text
New ReplicaSet cannot become available.
Deployment does not fully scale down old ReplicaSet.
Rollout gets stuck.
```

This is good.

It means Kubernetes avoided sending traffic to unready Pods.

Production mindset:

```text
A stuck rollout may be Kubernetes protecting you.
Do not blindly delete old Pods.
Find why new Pods are not Ready.
```

---

# 28. Production Story: Wrong Selector

Selector mistakes are dangerous.

Deployment:

```yaml
spec:
  selector:
    matchLabels:
      app: order-service
  template:
    metadata:
      labels:
        app: order-service-v2
```

In apps/v1, selector/template mismatch is rejected because selector is immutable and must match template labels.

But label mistakes can still happen around Services:

```yaml
Service selector:
  app: order-service

Deployment template labels:
  app: order-service-v2
```

Symptoms:

```text
Pods Running and Ready
Service exists
DNS resolves
But no traffic reaches Pods
```

Debug:

```bash
kubectl get pods --show-labels
kubectl describe svc order-service
kubectl get endpoints order-service
kubectl get endpointslice
```

Broken result:

```text
Endpoints: <none>
```

Mental model:

```text
Deployment labels manage ownership.
Service labels manage traffic.
Both must be correct.
```

---

# 29. Production Story: Scaling Causes Database Pain

Team sees high CPU on order-service.

They scale:

```bash
kubectl scale deployment order-service --replicas=20
```

App CPU improves.

Then database fails.

Why?

Each Spring Boot Pod has Hikari connection pool:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
```

With 20 Pods:

```text
20 Pods * 20 DB connections = 400 possible DB connections
```

Database max connections:

```text
200
```

Incident:

```text
App scale increased DB pressure.
DB connection errors begin.
Latency rises.
Pods may fail readiness.
```

ASCII:

```text
Before:
3 Pods * 20 = 60 DB connections

After:
20 Pods * 20 = 400 DB connections

DB capacity = 200

Result = overload
```

Lesson:

```text
Replica count is connected to downstream capacity.
Deployment scaling is not isolated.
```

---

# 30. Deployment Status Fields

Check:

```bash
kubectl get deployment order-service
```

Example:

```text
NAME            READY   UP-TO-DATE   AVAILABLE   AGE
order-service   2/3     3            2           5m
```

Meaning:

```text
READY:
  ready replicas / desired replicas

UP-TO-DATE:
  replicas updated to latest Pod template

AVAILABLE:
  replicas available according to readiness/minReadySeconds
```

Deeper:

```bash
kubectl describe deployment order-service
```

Look for:

```text
Replicas:               3 desired | 3 updated | 3 total | 2 available | 1 unavailable
StrategyType:           RollingUpdate
MinReadySeconds:        15
RollingUpdateStrategy:  25% max unavailable, 25% max surge
Conditions:
  Available             False
  Progressing           True
```

Mental model:

```text
Deployment status is the controller's report card.
```

Do not only look at Pod status.

Look at Deployment, ReplicaSet, Pod, and Events together.

---

# 31. ReplicaSet Status Fields

Check:

```bash
kubectl get rs
```

Example:

```text
NAME                       DESIRED CURRENT READY AGE
order-service-7c9d9f6b5     3       3       2     5m
```

Meaning:

```text
DESIRED:
  How many Pods this ReplicaSet wants

CURRENT:
  How many Pods exist

READY:
  How many are Ready
```

During rollout:

```text
NAME                       DESIRED CURRENT READY
order-service-oldhash       2       2       2
order-service-newhash       1       1       0
```

This tells you:

```text
New version exists but is not ready yet.
```

Debug next:

```bash
kubectl get pods
kubectl describe pod <newhash-pod>
kubectl logs <newhash-pod>
```

Mental model:

```text
ReplicaSet view tells you version-level health.
```

Deployment view tells you rollout-level health.

Pod view tells you runtime-level health.

---

# 32. Debugging Chain

When Deployment is broken, use this order:

```text
1. Deployment
2. ReplicaSet
3. Pod
4. Container logs
5. Events
6. Service/endpoints
7. App dependency health
```

Commands:

```bash
kubectl get deploy order-service
kubectl describe deploy order-service

kubectl get rs -l app=order-service
kubectl describe rs <rs-name>

kubectl get pods -l app=order-service -o wide
kubectl describe pod <pod-name>

kubectl logs <pod-name>
kubectl logs <pod-name> --previous

kubectl rollout status deployment/order-service
kubectl rollout history deployment/order-service
```

ASCII debug map:

```text
Deployment unhealthy?
   |
   v
Check ReplicaSets
   |
   v
Old RS or New RS problem?
   |
   v
Check Pods
   |
   v
Pending? ImagePull? CrashLoop? NotReady?
   |
   v
Check Events + Logs
```

Never debug randomly.

Follow ownership and reconciliation.

---

# 33. Pending Pods During Scaling

You scale to 10 replicas.

Pods show:

```text
Pending
```

Possible reasons:

```text
Not enough CPU/memory requests available
Node selector mismatch
Taints not tolerated
PVC unavailable
Image pull secret issue may appear later
Cluster autoscaler not adding nodes
```

Deployment state:

```text
Desired = 10
Available = 3
```

ReplicaSet may have created Pod objects successfully.

But scheduler cannot place them.

Diagram:

```text
Deployment wants 10
   |
   v
ReplicaSet creates 10 Pod objects
   |
   v
Scheduler tries placement
   |
   v
Only 3 fit
   |
   v
7 Pending
```

Debug:

```bash
kubectl describe pod <pending-pod>
```

Look for:

```text
0/5 nodes are available: insufficient memory
node(s) had untolerated taint
node(s) didn't match node selector
```

Lesson:

```text
ReplicaSet can create Pod objects.
Scheduler still needs real cluster capacity.
```

---

# 34. CrashLoopBackOff During Rollout

New version has bad configuration.

Pod status:

```text
CrashLoopBackOff
```

Internal flow:

```text
New ReplicaSet created
Pods created
Containers start
Spring Boot crashes
Kubelet restarts with backoff
Pods never Ready
Deployment rollout stuck
```

Spring Boot common causes:

```text
Missing environment variable
Bad datasource URL
Wrong profile
Liquibase/Flyway migration error
Kafka bootstrap unreachable
Redis password mismatch
OutOfMemoryError
```

Debug:

```bash
kubectl logs <pod-name>
kubectl logs <pod-name> --previous
kubectl describe pod <pod-name>
```

Important:

```text
Kubernetes is doing its job.
The app is failing inside the container.
```

Correct response:

```text
Fix config or rollback.
Do not increase replicas blindly.
```

---

# 35. Deployment Is Not A Load Balancer

Deployment creates and manages Pods.

It does not provide stable network access.

For traffic, you need Service.

```text
Deployment
   |
   v
Pods

Service
   |
   v
Selects Ready Pods by labels
```

Diagram:

```text
Client
  |
  v
Service order-service
  |
  +--> Pod A
  +--> Pod B
  +--> Pod C

Deployment order-service
  |
  +--> ReplicaSet
          |
          +--> Pod A
          +--> Pod B
          +--> Pod C
```

Deployment and Service both point to Pods using labels, but for different reasons.

```text
Deployment/ReplicaSet selector = ownership/count
Service selector               = traffic routing
```

This distinction is critical.

---

# 36. Complete YAML: Production-Friendly Spring Boot Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
  labels:
    app: order-service
spec:
  replicas: 3
  revisionHistoryLimit: 5
  minReadySeconds: 15
  progressDeadlineSeconds: 180
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
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
          image: registry.example.com/order-service:1.0.0
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: prod
          resources:
            requests:
              cpu: "250m"
              memory: "512Mi"
            limits:
              cpu: "1000m"
              memory: "1024Mi"
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 20
            periodSeconds: 5
            failureThreshold: 3
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 60
            periodSeconds: 10
            failureThreshold: 3
```

This YAML expresses an operating policy:

```text
Run 3 copies.
Roll out safely.
Keep old history.
Wait until Pods are stable.
Detect stuck rollout.
Declare resource needs.
Use readiness before traffic.
Use liveness for dead process recovery.
```

---

# 37. Complete YAML: Service For Deployment

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
Service exposes stable name:
order-service.default.svc.cluster.local

Service sends traffic to Pods with:
app=order-service
```

Diagram:

```text
Other service
    |
    | http://order-service
    v
ClusterIP Service
    |
    +--> Ready Pod 1 :8080
    +--> Ready Pod 2 :8080
    +--> Ready Pod 3 :8080
```

Deployment ensures Pods exist.

Service ensures clients can reach them.

```text
Deployment = create/manage workers
Service    = stable front desk
```

---

# 38. Local Dry Run With Commands

Apply:

```bash
kubectl apply -f order-deployment.yaml
kubectl apply -f order-service.yaml
```

Watch:

```bash
kubectl get deploy,rs,pods,svc
```

Expected:

```text
deployment.apps/order-service      3/3
replicaset.apps/order-service-xxx  3 desired
pod/order-service-xxx-a            Running
pod/order-service-xxx-b            Running
pod/order-service-xxx-c            Running
service/order-service              ClusterIP
```

Scale:

```bash
kubectl scale deploy order-service --replicas=5
kubectl get pods -l app=order-service
```

Update:

```bash
kubectl set image deploy/order-service order-service=registry.example.com/order-service:1.1.0
kubectl rollout status deploy/order-service
```

History:

```bash
kubectl rollout history deploy/order-service
```

Rollback:

```bash
kubectl rollout undo deploy/order-service
```

Delete one Pod:

```bash
kubectl delete pod <pod-name>
kubectl get pods -w
```

Observe replacement Pod.

That observation teaches more than memorizing commands.

---

# 39. What Happens If You Edit A ReplicaSet Directly?

You usually should not manage ReplicaSets directly when they are owned by a Deployment.

Why?

Because Deployment is the higher-level controller.

If you manually change ReplicaSet replicas:

```bash
kubectl scale rs order-service-xxx --replicas=10
```

Deployment may later reconcile it back according to Deployment desired state.

Mental model:

```text
Do not fight the parent controller.
```

Ownership chain:

```text
Deployment desired state
       |
       v
ReplicaSet desired state
       |
       v
Pod count
```

If the Deployment says 3, and you force the ReplicaSet to 10, you are modifying the child instead of the source of truth.

Correct:

```bash
kubectl scale deployment order-service --replicas=10
```

Rule:

```text
Change the highest-level object you own.
For normal apps, that is Deployment.
```

---

# 40. What Happens If You Delete ReplicaSet?

If you delete a ReplicaSet owned by a Deployment:

```bash
kubectl delete rs order-service-7c9d9f6b5
```

Deployment notices its required ReplicaSet is missing.

It may recreate a ReplicaSet to match desired template.

Effect:

```text
Pods owned by that ReplicaSet may be deleted.
Deployment works to restore desired state.
```

This can cause disruption.

Do not delete ReplicaSets casually in production.

Correct debugging mindset:

```text
Before deleting anything, ask:
Who owns this object?
What will the owner recreate?
Will deletion reduce availability?
```

Command:

```bash
kubectl describe rs <rs-name>
```

Look for:

```text
Controlled By: Deployment/order-service
```

If controlled by Deployment, modify Deployment instead.

---

# 41. What Happens If You Delete Deployment?

Command:

```bash
kubectl delete deployment order-service
```

Default behavior:

```text
Deployment deleted
Owned ReplicaSets deleted
Owned Pods deleted
```

Service may remain if you do not delete it.

Result:

```text
Service exists but has no endpoints.
Traffic fails.
```

Diagram:

```text
Before:
Service -> Pods
Deployment -> RS -> Pods

Delete Deployment:
Service -> <no endpoints>
Deployment gone
RS gone
Pods gone
```

Production caution:

```text
Deleting Deployment removes the app runtime.
It is not just deleting YAML.
```

Safer actions:

```bash
kubectl scale deployment order-service --replicas=0
```

This intentionally stops Pods while keeping Deployment object.

---

# 42. Horizontal Pod Autoscaler Connection

HPA usually targets Deployment.

```text
HPA changes Deployment.spec.replicas.
Deployment updates ReplicaSet count.
ReplicaSet creates/deletes Pods.
```

ASCII:

```text
Metrics Server
      |
      v
HPA observes CPU/RPS/custom metric
      |
      v
Updates Deployment replicas
      |
      v
ReplicaSet reconciles Pods
```

Example:

```text
CPU high
HPA changes replicas 3 -> 6
Deployment accepts new desired count
ReplicaSet creates 3 more Pods
```

Important:

```text
HPA does not create Pods directly.
It changes desired replicas on a scalable object.
```

Mental model:

```text
Autoscaling is automated desired-state editing.
```

---

# 43. Deployment And Statelessness

Deployment works best for stateless applications.

Stateless means:

```text
Any Pod can handle any request.
Losing a Pod does not lose critical user state.
State lives outside Pods: DB, Redis, Kafka, object storage.
```

Good Deployment app:

```text
Spring Boot REST service
JWT auth
PostgreSQL for orders
Redis for cache
Kafka for async events
```

Bad fit:

```text
App stores user cart only in JVM memory.
App writes uploaded files only to local container disk.
App requires stable identity per replica.
```

For stable identity, consider StatefulSet.

Diagram:

```text
Stateless Deployment:
Request -> any Pod -> DB/Redis/Kafka

Stateful mistake:
Request -> specific Pod memory
Pod dies -> state lost
```

Deployment assumes replaceability.

Design your Spring Boot service accordingly.

---

# 44. Database Migration During Deployment

Common production trap:

```text
Deploy app v2 and run DB migration at same time.
```

During rolling update, old and new versions may run together.

```text
Time window:
Pod v1 still serving traffic
Pod v2 also serving traffic
Database schema changed
```

If v1 cannot work with new schema, outage happens.

Safe strategy:

```text
Expand -> Deploy -> Contract
```

Example:

```text
1. Expand schema:
   Add nullable column. Do not remove old column.

2. Deploy app v2:
   v1 and v2 both work.

3. Backfill data.

4. Later contract:
   Remove old column only after old app gone.
```

ASCII:

```text
Bad:
DB breaking change + rolling update = mixed-version failure

Good:
Backward-compatible DB change
        |
        v
Rolling update safe
        |
        v
Cleanup later
```

Deployment rollback cannot undo unsafe DB migration automatically.

---

# 45. Deployment Availability Math

For replicas = 4:

```yaml
maxUnavailable: 1
maxSurge: 1
```

During rollout:

```text
Minimum available Pods = 3
Maximum total Pods = 5
```

Diagram:

```text
Desired replicas: 4

maxUnavailable 1:
  at least 3 available

maxSurge 1:
  at most 5 total during rollout
```

For strict zero-unavailable rollout:

```yaml
maxUnavailable: 0
maxSurge: 1
```

This requires spare cluster capacity.

If cluster cannot fit surge Pod:

```text
New Pod Pending
Rollout stuck
Old Pods still running
```

Tradeoff:

```text
Higher availability requires extra temporary capacity.
```

Production thinking:

```text
Rollout strategy is capacity planning plus risk management.
```

---

# 46. Common Beginner Mistakes

```text
Mistake 1:
Creating bare Pods for production apps.
Correct:
Use Deployment for long-running stateless services.

Mistake 2:
Thinking Deployment directly runs containers.
Correct:
Deployment -> ReplicaSet -> Pod -> Container.

Mistake 3:
Deleting Pods to scale down.
Correct:
Change Deployment replicas.

Mistake 4:
Ignoring ReplicaSets during rollout debugging.
Correct:
ReplicaSets show old/new version health.

Mistake 5:
Wrong readiness probe.
Correct:
Readiness must represent traffic safety.

Mistake 6:
Liveness checks external dependencies.
Correct:
Avoid restarting all Pods because DB is temporarily down.

Mistake 7:
Scaling app without checking DB/Redis/Kafka capacity.
Correct:
Replica count affects downstream load.

Mistake 8:
Unsafe DB migration during rolling update.
Correct:
Use backward-compatible expand/deploy/contract.
```

---

# 47. Interview Questions

## What is a ReplicaSet?

A ReplicaSet is a Kubernetes controller that ensures a specified number of matching Pods are running. It uses a label selector to identify Pods and creates or deletes Pods to match the desired replica count.

## What is a Deployment?

A Deployment is a higher-level controller for managing stateless applications. It manages ReplicaSets, supports rolling updates, rollbacks, replica scaling, and rollout history.

## Why use Deployment instead of ReplicaSet directly?

ReplicaSet mainly maintains Pod count. Deployment manages application lifecycle, including version changes, rolling updates, rollback, and history. For normal stateless applications, Deployment is the preferred object.

## What happens when a Pod managed by ReplicaSet is deleted?

The ReplicaSet detects that the actual number of matching Pods is below the desired count and creates a replacement Pod.

## What happens when a Deployment image is updated?

The Deployment creates a new ReplicaSet for the new Pod template, scales it up gradually, and scales down the old ReplicaSet according to the rollout strategy.

## What is maxSurge?

maxSurge controls how many extra Pods above the desired replica count can exist temporarily during rolling update.

## What is maxUnavailable?

maxUnavailable controls how many desired Pods can be unavailable during rolling update.

## How does rollback work in Deployment?

Deployment keeps old ReplicaSets as rollout history. Rollback scales the previous ReplicaSet back up and scales the current bad ReplicaSet down.

## Why are labels important for ReplicaSet?

ReplicaSet uses label selectors to identify which Pods it owns. If labels do not match, ownership and reconciliation break.

## Is Deployment a load balancer?

No. Deployment manages Pods and rollout. Service provides stable network access and load balancing to ready Pods.

---

# 48. Cheat Sheet

```text
Pod
  Smallest deployable runtime unit.
  Runs one or more containers.

ReplicaSet
  Keeps desired number of matching Pods.
  Uses label selector.
  Creates/deletes Pods to match count.

Deployment
  Manages ReplicaSets.
  Provides rolling update, rollback, scaling, history.

Deployment -> ReplicaSet -> Pod -> Container

selector
  Label query used to find matching Pods.

template
  Pod blueprint used by ReplicaSet to create Pods.

maxSurge
  Extra Pods allowed during rollout.

maxUnavailable
  Pods allowed to be unavailable during rollout.

readinessProbe
  Determines whether Pod can receive traffic.

livenessProbe
  Determines whether container should be restarted.

minReadySeconds
  Pod must stay Ready before counted Available.

progressDeadlineSeconds
  Time before rollout is marked failed.

revisionHistoryLimit
  Number of old ReplicaSets kept for rollback.
```

Core commands:

```bash
kubectl get deploy
kubectl describe deploy order-service

kubectl get rs
kubectl describe rs <rs-name>

kubectl get pods -l app=order-service
kubectl describe pod <pod-name>
kubectl logs <pod-name>
kubectl logs <pod-name> --previous

kubectl scale deploy order-service --replicas=5
kubectl set image deploy/order-service order-service=order-service:1.1.0
kubectl rollout status deploy/order-service
kubectl rollout history deploy/order-service
kubectl rollout undo deploy/order-service
```

---

# 49. One Picture To Remember

```text
                         YOU
                          |
                          | desired app operation
                          v
                 +------------------+
                 | Deployment       |
                 | replicas: 3      |
                 | image: v2        |
                 | rollout policy   |
                 +--------+---------+
                          |
                          | creates/manages versions
                          v
        +---------------------------------------+
        | ReplicaSets                           |
        |                                       |
        | old RS: image v1, desired 0           |
        | new RS: image v2, desired 3           |
        +------------------+--------------------+
                           |
                           | maintain count
                           v
                 +------------------+
                 | Pods             |
                 | v2 Pod A Ready   |
                 | v2 Pod B Ready   |
                 | v2 Pod C Ready   |
                 +--------+---------+
                          |
                          | kubelet/container runtime
                          v
                 +------------------+
                 | Spring Boot JVM  |
                 | handles traffic  |
                 +------------------+

Service is separate:

Client -> Service -> Ready Pods

Rule:
Deployment manages lifecycle.
ReplicaSet manages count.
Service manages stable traffic.
Pod runs the app.
```

---

# 50. Final Production Checklist

```text
[ ] I know Deployment does not directly run containers.
[ ] I can explain Deployment -> ReplicaSet -> Pod -> Container.
[ ] I know ReplicaSet maintains matching Pod count.
[ ] I know Deployment manages rollout and rollback.
[ ] I know selector and template labels must match.
[ ] I know Service selector is for traffic, not ownership.
[ ] I know deleting a managed Pod causes replacement.
[ ] I know scaling should modify Deployment replicas.
[ ] I know rolling update creates a new ReplicaSet.
[ ] I know old ReplicaSets enable rollback.
[ ] I know readiness controls traffic safety.
[ ] I know liveness controls restart behavior.
[ ] I know maxSurge/maxUnavailable trade capacity for availability.
[ ] I know unsafe DB migration can break rolling updates.
[ ] I can debug Deployment -> ReplicaSet -> Pod -> Logs -> Events -> Service.
```

Final memory hook:

```text
Do not memorize Deployment and ReplicaSet as definitions.
Remember the production operating chain:

Deployment decides application version and rollout.
ReplicaSet keeps the right number of Pods.
Pods run replaceable app instances.
Service sends traffic only to ready Pods.
```

That is the model behind most real Kubernetes application deployments.
