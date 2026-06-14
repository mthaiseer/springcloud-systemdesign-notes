# 021_Watch_Informer_Reconcile_Loop.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why This Chapter Exists

Most people learn Kubernetes controllers like this:

```text
Controller watches objects.
Controller reconciles state.
Informer caches objects.
```

That is correct, but it is not useful unless you can feel the machinery.

The real mental model is:

```text
Kubernetes does not constantly scan the whole cluster randomly.
Kubernetes receives change events, caches current knowledge,
and runs small repair loops until reality matches desired state.
```

If you understand this chapter, Kubernetes controllers stop feeling magical.

You will understand why:

```text
kubectl apply does not directly create everything
controller-manager reacts later
watch streams can break
informers can resync
work queues retry failed operations
operators are just custom controllers
```

One picture:

```text
API Server event stream
        |
        v
Informer cache
        |
        v
Work Queue
        |
        v
Reconcile Loop
        |
        v
API Server writes actions
```

Memory hook:

```text
Watch tells you something changed.
Informer remembers what the world looks like.
Queue decides what needs work.
Reconcile repairs reality.
```

Do not memorize `watch`, `informer`, and `reconcile` as separate words.
See them as one production control pipeline.

---

# 2. The Wrong Way To Think About Controllers

Bad mental model:

```text
Controller wakes up every second.
Controller lists all Deployments.
Controller lists all Pods.
Controller compares everything.
Controller creates missing Pods.
```

This would be expensive for large clusters.

Imagine a cluster with:

```text
50,000 Pods
5,000 Deployments
10,000 Services
30,000 ConfigMaps
```

If every controller repeatedly scanned everything, the API Server and etcd would be overloaded.

Better model:

```text
API Server provides event stream.
Controller maintains local cache.
Controller reacts to changed keys.
Controller only reconciles affected objects.
```

ASCII:

```text
Bad Model
---------

Controller
   |
   | repeatedly list everything
   v
API Server
   |
   v
etcd

Result: expensive, noisy, slow


Better Model
------------

API Server
   |
   | watch events
   v
Informer
   |
   | enqueue affected object key
   v
Controller Worker

Result: event-driven + cache-based
```

Kubernetes is not built like a naive polling script.

It is built like a distributed event-driven repair system.

---

# 3. Real World Analogy: Hotel Reception + Maintenance Team

Imagine a hotel.

Guests report problems:

```text
Room 302 AC broken
Room 105 water leaking
Room 411 key card not working
```

The reception desk receives events.

The maintenance board stores current known issues.

Workers pick jobs from the queue.

They do not inspect every room every minute.

```text
Guest event
    |
    v
Reception desk
    |
    v
Maintenance board
    |
    v
Work queue
    |
    v
Technician fixes room
```

Kubernetes controller model:

```text
Object change event
    |
    v
API Server watch
    |
    v
Informer cache
    |
    v
Work queue
    |
    v
Reconcile worker fixes object state
```

The key lesson:

```text
Events tell the system what changed.
Cache remembers known state.
Queue prevents overload.
Worker repairs state.
```

This is why controllers scale.

They do not panic and inspect the whole hotel every time one room reports a problem.

---

# 4. Real World Analogy: Restaurant Kitchen Tickets

A restaurant does not cook by asking every customer repeatedly:

```text
Do you need food?
Do you need food?
Do you need food?
```

Instead:

```text
Customer places order.
Ticket appears.
Kitchen works ticket.
Ticket completed.
```

If cooking fails:

```text
Ticket returns to queue.
Retry later.
```

Kubernetes controller:

```text
Deployment changes.
Event appears.
Object key goes to queue.
Worker reconciles.
If error, retry later.
```

ASCII:

```text
Restaurant
----------
Order Event -> Ticket Board -> Chef -> Meal

Kubernetes
----------
Object Event -> Work Queue -> Controller -> API Update
```

Important difference:

The chef does not blindly trust the ticket text forever.
Before cooking, the chef checks the current order.

Kubernetes reconcile also does this.

```text
Queue item says: deployment/order-service changed
Worker fetches latest deployment state from cache/API
Then acts based on current state
```

Why?

Because events may be old, duplicated, or compressed.

Reconcile must be idempotent.

---

# 5. Core Picture

```text
                         +--------------------+
                         |    API Server      |
                         | list / watch API   |
                         +---------+----------+
                                   |
                                   | watch events
                                   v
+-------------------+     +--------------------+     +-------------------+
| etcd              |<--->| Informer           |---->| Local Cache       |
| source of truth   |     | event receiver     |     | fast reads        |
+-------------------+     +---------+----------+     +-------------------+
                                   |
                                   | add key
                                   v
                         +--------------------+
                         | Work Queue         |
                         | namespace/name     |
                         +---------+----------+
                                   |
                                   | worker pops key
                                   v
                         +--------------------+
                         | Reconcile Loop     |
                         | compare + act      |
                         +---------+----------+
                                   |
                                   | create/update/delete
                                   v
                         +--------------------+
                         | API Server         |
                         +--------------------+
```

Core statement:

```text
Informer observes.
Cache remembers.
Queue schedules work.
Reconcile makes changes.
```

This architecture is everywhere in Kubernetes.

Deployment controller, ReplicaSet controller, EndpointSlice controller, Job controller, custom operators — all follow this pattern.

---

# 6. What Is A Watch?

A watch is a long-running request to the API Server.

Instead of repeatedly asking:

```text
Give me all Pods now.
Give me all Pods now.
Give me all Pods now.
```

A controller asks:

```text
Tell me when Pods change.
```

Simple picture:

```text
Controller ---------------------> API Server
           watch pods

API Server ---------------------> Controller
           ADDED pod-a
           MODIFIED pod-a
           DELETED pod-b
```

Event types:

```text
ADDED      object created or first observed
MODIFIED   object changed
DELETED    object removed
BOOKMARK   progress marker for watch stream
ERROR      watch problem
```

Mental model:

```text
Watch = subscribe to object changes
```

But watch is not enough by itself.

Why?

Because a controller also needs current state.

If the controller starts after objects already exist, it needs an initial list.

That gives us the list-watch pattern.

---

# 7. List-Watch Pattern

Kubernetes clients usually do not start with watch only.

They do:

```text
1. LIST current objects
2. remember resourceVersion
3. WATCH changes after that version
```

ASCII:

```text
Controller starts
      |
      v
LIST deployments
      |
      | gets all current deployments
      | gets resourceVersion = 1200
      v
WATCH deployments from version 1200
      |
      | receive changes after list point
      v
Keep local cache updated
```

Why list first?

Because watch only tells future changes.

If this Deployment already exists:

```text
order-service
payment-service
inventory-service
```

A pure watch may not tell you about them unless they change.

So controller first builds a baseline.

```text
LIST = current snapshot
WATCH = continuous updates
```

This is the foundation of informers.

---

# 8. What Is resourceVersion?

Every Kubernetes object has metadata.

One important field is:

```text
metadata.resourceVersion
```

Simple mental model:

```text
resourceVersion = version marker of cluster state for that object/list
```

It helps the API Server answer:

```text
Give me changes after this point.
```

Flow:

```text
LIST pods returns:
  pod-a rv=100
  pod-b rv=105
  list rv=110

WATCH from rv=110 returns:
  MODIFIED pod-a rv=111
  ADDED pod-c rv=112
```

ASCII:

```text
Time ------------------------------------------------>

rv=100   rv=105   rv=110   rv=111   rv=112
pod-a    pod-b    LIST     pod-a    pod-c
created  changed  point    modified added

Controller says:
Watch from rv=110
```

Do not treat resourceVersion as a global timestamp you manually calculate.

For controller mental model:

```text
It is a safe continuation marker for watches.
```

---

# 9. Why Watch Can Break

A watch is a network stream.

Network streams are not immortal.

They can break because:

```text
API Server restart
Load balancer timeout
Network interruption
Client slow consumer
resourceVersion too old
etcd compaction
```

If the controller falls too far behind, the API Server may say:

```text
410 Gone: too old resource version
```

Meaning:

```text
The history you want is no longer available.
Do a fresh LIST and start again.
```

ASCII:

```text
Controller watched from rv=100
       |
       | controller disconnected for too long
       v
etcd compacted old history
       |
       v
Controller asks from rv=100
       |
       v
API Server: too old, relist
```

This is why Kubernetes clients need robust list-watch logic.

They cannot assume one watch connection lasts forever.

---

# 10. What Is An Informer?

An informer is a Kubernetes client-side helper that does several things:

```text
1. Lists objects initially
2. Watches object changes
3. Maintains local cache
4. Calls event handlers
5. Helps controllers enqueue work
```

Mental model:

```text
Informer = list-watch + local cache + event callback system
```

ASCII:

```text
                    +-------------------+
                    | API Server        |
                    +---------+---------+
                              |
                              | list + watch
                              v
+------------------+  events  +-------------------+
| Event Handlers   |<---------| Informer          |
+--------+---------+          +---------+---------+
         |                              |
         | enqueue key                  | update cache
         v                              v
+------------------+          +-------------------+
| Work Queue       |          | Local Store/Cache |
+------------------+          +-------------------+
```

An informer is not the controller itself.

It feeds the controller.

```text
Informer observes and caches.
Controller reconciles.
```

---

# 11. Informer Cache Mental Model

The informer cache is a local memory copy of Kubernetes objects.

Why have cache?

Because controllers read objects frequently.

Without cache:

```text
Every reconcile reads API Server.
API Server reads etcd.
Large clusters suffer.
```

With cache:

```text
Informer keeps local copy updated.
Reconcile reads from memory.
API Server load reduces.
```

ASCII:

```text
Without Cache
-------------
Reconcile -> API Server -> etcd
Reconcile -> API Server -> etcd
Reconcile -> API Server -> etcd

With Informer Cache
-------------------
API Server -> Informer -> Cache
                         ^
                         |
                    Reconcile reads
```

Important production lesson:

```text
Cache is eventually consistent with API Server.
```

It is usually very fresh, but not magically synchronous.

Controllers must tolerate slightly stale reads.

That is why reconcile logic should be idempotent and retry-safe.

---

# 12. Event Handlers

Informer event handlers usually respond to:

```text
OnAdd
OnUpdate
OnDelete
```

But handler should not do heavy work directly.

Bad:

```text
OnUpdate receives Deployment
OnUpdate creates Pods immediately
OnUpdate calls external APIs
OnUpdate blocks for 30 seconds
```

Good:

```text
OnUpdate receives Deployment
OnUpdate computes key namespace/name
OnUpdate adds key to queue
Worker processes queue later
```

ASCII:

```text
Bad Handler
-----------
Event -> heavy logic -> slow watch -> missed events risk

Good Handler
------------
Event -> enqueue key -> return fast
                       |
                       v
                    worker later
```

Why keep event handlers light?

Because they sit on the event path.

If they block, the informer may fall behind.

Production controller design:

```text
Event handler = quick notification
Reconcile worker = real work
```

---

# 13. What Goes Into The Work Queue?

Usually not the whole object.

Usually a key:

```text
namespace/name
```

Example:

```text
default/order-service
prod/payment-service
```

Why only key?

Because the object may change again before the worker processes it.

If you store full old object, you may reconcile stale data.

Better:

```text
Queue stores key.
Worker fetches latest object from cache.
```

ASCII:

```text
Event says: Deployment changed
        |
        v
Queue stores: default/order-service
        |
        v
Worker pops key
        |
        v
Read latest Deployment from cache
        |
        v
Reconcile current truth
```

This is a big controller design principle.

```text
Queue is not truth.
Queue is only a reminder to check truth.
```

---

# 14. Why Work Queue Is Needed

Why not reconcile directly from event handler?

Because production systems need:

```text
backpressure
retry
deduplication
rate limiting
parallel workers
failure isolation
```

Work queue provides a buffer.

ASCII:

```text
Events may arrive fast:

ADDED order-service
MODIFIED order-service
MODIFIED order-service
MODIFIED order-service

Queue can collapse into:

default/order-service
```

If reconcile fails:

```text
Worker error
   |
   v
requeue with delay
   |
   v
try again later
```

This matters because controllers often depend on temporary conditions:

```text
API Server conflict
resource quota delay
node not ready
webhook timeout
external cloud API slow
```

A controller should not crash because one reconciliation failed.

It should retry safely.

---

# 15. Reconcile Loop Mental Model

Reconcile is the heart of Kubernetes control.

It answers:

```text
Given desired object X,
what should reality look like,
and what small action moves reality closer?
```

Pseudo-code:

```text
reconcile(key):
    desired = read object from cache

    if desired does not exist:
        cleanup external/child resources if needed
        return success

    actual = read related current state

    if actual != desired:
        create/update/delete something

    update status if needed

    return success or retryable error
```

ASCII:

```text
+-------------------+
| Get desired state |
+---------+---------+
          |
          v
+-------------------+
| Get actual state  |
+---------+---------+
          |
          v
+-------------------+
| Compare           |
+---------+---------+
          |
          v
+-------------------+
| Act if needed     |
+---------+---------+
          |
          v
+-------------------+
| Requeue/retry?    |
+-------------------+
```

Reconcile should be boring.

Boring is good.

A good reconcile loop can run 100 times and still produce the same safe result.

---

# 16. Idempotency: The Most Important Controller Skill

Idempotent means:

```text
Running the same operation many times is safe.
```

Example bad logic:

```text
Every reconcile creates one Pod.
```

If reconcile runs 3 times:

```text
3 extra Pods created
```

Good logic:

```text
Check how many Pods exist.
If fewer than desired, create only missing number.
If already enough, do nothing.
```

ASCII:

```text
Bad Reconcile
-------------
reconcile -> create pod
reconcile -> create pod
reconcile -> create pod

Good Reconcile
--------------
reconcile -> desired=3 actual=2 -> create 1
reconcile -> desired=3 actual=3 -> do nothing
reconcile -> desired=3 actual=3 -> do nothing
```

Why does this matter?

Because Kubernetes can call reconcile many times for the same object.

Reasons:

```text
duplicate events
resync
status updates
related object changes
retry after error
controller restart
```

Controller rule:

```text
Never assume reconcile runs exactly once.
Assume reconcile runs many times.
Design it to be safe.
```

---

# 17. Reconcile Is Level-Based, Not Edge-Based

This is a deep Kubernetes concept.

Edge-based thinking:

```text
Event happened, so perform exactly the event action.
```

Level-based thinking:

```text
Look at current desired state and current actual state.
Make reality match now.
```

Kubernetes controllers should be level-based.

Example:

```text
Event 1: Deployment replicas changed 2 -> 3
Event 2: Deployment replicas changed 3 -> 5
```

If controller misses Event 1 but sees current state replicas=5, it is fine.

It does not need the full history.

ASCII:

```text
Bad edge-based controller:
Needs every event perfectly.
Miss one event = wrong behavior.

Good level-based controller:
Reads current state.
Miss old event = still okay.
```

This is why queue stores a key, not detailed action.

```text
The key means: check this object.
It does not mean: perform this exact historical event.
```

---

# 18. Deployment Controller Example

Desired Deployment:

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
```

Deployment controller reconcile mental model:

```text
1. Read Deployment.
2. Check desired ReplicaSet for current pod template.
3. Create ReplicaSet if missing.
4. Scale ReplicaSets according to rollout strategy.
5. Update Deployment status.
```

ASCII:

```text
Deployment changed
       |
       v
Informer event
       |
       v
Queue key: default/order-service
       |
       v
Reconcile Deployment
       |
       +--> ensure ReplicaSet exists
       +--> ensure replica counts are correct
       +--> update rollout status
```

The controller does not directly start containers.

It creates/updates ReplicaSets.

Then ReplicaSet controller creates Pods.

Then Scheduler places Pods.

Then Kubelet starts containers.

This is chained reconciliation.

---

# 19. ReplicaSet Controller Example

ReplicaSet desired state:

```text
replicas = 3
selector = app=order-service
```

Actual Pods:

```text
Pod A app=order-service Running
Pod B app=order-service Running
```

ReplicaSet reconcile:

```text
desired = 3
actual matching pods = 2
missing = 1
create one Pod
```

ASCII:

```text
ReplicaSet
 replicas=3
 selector app=order-service
        |
        v
List matching Pods from cache
        |
        v
Actual=2
        |
        v
Create 1 Pod object
```

If actual is too high:

```text
desired = 3
actual = 5
extra = 2
delete 2 Pods
```

Important:

```text
ReplicaSet manages Pod count.
It does not care about Service traffic.
It does not choose Nodes.
It does not pull images.
```

Each controller owns a narrow responsibility.

---

# 20. EndpointSlice Controller Example

Services send traffic to Pods through endpoints.

Service selector:

```yaml
selector:
  app: order-service
```

Pods:

```text
Pod A app=order-service Ready
Pod B app=order-service Ready
Pod C app=order-service NotReady
```

EndpointSlice controller watches:

```text
Services
Pods
EndpointSlices
```

Reconcile result:

```text
EndpointSlice contains Pod A and Pod B
Pod C excluded until Ready
```

ASCII:

```text
Service selector app=order-service
          |
          v
Match ready Pods
          |
          v
EndpointSlice
   - Pod A IP
   - Pod B IP
```

Production debugging lesson:

```text
If Service exists and Pods exist but traffic fails,
check whether EndpointSlice/endpoints were reconciled correctly.
```

Commands:

```bash
kubectl get svc order-service
kubectl get endpoints order-service
kubectl get endpointslice -l kubernetes.io/service-name=order-service
kubectl get pods --show-labels
```

---

# 21. Custom Controller / Operator Mental Model

An operator is not magic.

It is usually:

```text
Custom Resource Definition + Controller
```

Example custom resource:

```yaml
apiVersion: apps.example.com/v1
kind: MiniDatabase
metadata:
  name: orders-db
spec:
  replicas: 3
  storage: 20Gi
```

Operator reconcile:

```text
Read MiniDatabase desired state.
Ensure StatefulSet exists.
Ensure Service exists.
Ensure PVCs exist.
Ensure backup CronJob exists.
Update MiniDatabase status.
```

ASCII:

```text
MiniDatabase CR
       |
       v
Operator Reconcile
       |
       +--> StatefulSet
       +--> Service
       +--> PVC
       +--> Backup CronJob
       +--> Status
```

Operator memory hook:

```text
Operator = human operational knowledge encoded as reconcile loop
```

If a DBA would manually create replicas, backups, failover rules, and status checks, an operator automates those actions.

---

# 22. Spring Boot Analogy: Event Listener vs Repair Worker

In Spring Boot, you may have an event listener:

```java
@Component
class OrderEventListener {
    @EventListener
    public void onOrderCreated(OrderCreated event) {
        // bad if this does heavy work synchronously
    }
}
```

Better production pattern:

```text
Event listener receives event.
Listener writes task to queue.
Worker processes task with retries.
```

Kubernetes controller pattern is similar:

```text
Informer handler receives object event.
Handler writes key to work queue.
Worker reconciles with retries.
```

ASCII:

```text
Spring Boot
-----------
Domain Event -> Listener -> Queue -> Worker -> DB/API update

Kubernetes
----------
Object Event -> Handler  -> Queue -> Reconcile -> API update
```

This analogy helps Java developers understand why Kubernetes does not put heavy logic inside the watch handler.

The event path must remain fast.

The worker path can retry, rate-limit, and observe failures.

---

# 23. Java-Style Mini Controller Pseudo Code

This is not production Kubernetes client code.

It is a learning model.

```java
public class MiniController {

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private final LocalCache cache = new LocalCache();
    private final KubernetesApi api = new KubernetesApi();

    public void start() {
        listInitialObjects();
        startWatchThread();
        startWorkerThreads(4);
    }

    private void listInitialObjects() {
        List<K8sObject> objects = api.list("Deployment");
        for (K8sObject obj : objects) {
            cache.put(obj.key(), obj);
            queue.add(obj.key());
        }
    }

    private void startWatchThread() {
        new Thread(() -> {
            api.watch("Deployment", event -> {
                K8sObject obj = event.object();

                if (event.type() == EventType.DELETED) {
                    cache.delete(obj.key());
                } else {
                    cache.put(obj.key(), obj);
                }

                queue.add(obj.key());
            });
        }).start();
    }

    private void startWorkerThreads(int count) {
        for (int i = 0; i < count; i++) {
            new Thread(this::workerLoop).start();
        }
    }

    private void workerLoop() {
        while (true) {
            try {
                String key = queue.take();
                reconcile(key);
            } catch (Exception e) {
                // real controller would rate-limit and retry
            }
        }
    }

    private void reconcile(String key) {
        K8sObject desired = cache.get(key);

        if (desired == null) {
            cleanupChildren(key);
            return;
        }

        ensureReplicaSetExists(desired);
        ensureReplicaCount(desired);
        updateStatus(desired);
    }
}
```

Mental model from code:

```text
listInitialObjects = LIST
startWatchThread   = WATCH
cache              = informer store
queue              = work queue
reconcile          = control loop
```

---

# 24. Java-Style Rate Limited Retry Queue

A real controller must retry failures carefully.

Bad retry:

```text
Failure -> immediately retry forever -> API Server overload
```

Good retry:

```text
Failure -> wait -> retry -> longer wait -> retry later
```

Java-style pseudo code:

```java
class RateLimitedQueue {
    private final BlockingQueue<String> ready = new LinkedBlockingQueue<>();
    private final Map<String, Integer> failures = new ConcurrentHashMap<>();

    public void add(String key) {
        ready.add(key);
    }

    public void addRateLimited(String key) {
        int count = failures.merge(key, 1, Integer::sum);
        long delayMs = Math.min(30_000, (long) Math.pow(2, count) * 100L);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> ready.add(key), delayMs, TimeUnit.MILLISECONDS);
    }

    public void forget(String key) {
        failures.remove(key);
    }

    public String take() throws InterruptedException {
        return ready.take();
    }
}
```

Controller worker idea:

```java
try {
    reconcile(key);
    queue.forget(key);
} catch (RetryableException e) {
    queue.addRateLimited(key);
}
```

Production lesson:

```text
Retries are necessary.
Uncontrolled retries are outages.
```

---

# 25. Why Controllers Update Status

Kubernetes objects often have:

```text
spec   = desired state
status = observed state
```

For a custom resource:

```yaml
spec:
  replicas: 3
status:
  readyReplicas: 2
  phase: Progressing
```

Reconcile updates status after observing reality.

ASCII:

```text
User writes spec
      |
      v
Controller reads spec
      |
      v
Controller checks actual child resources
      |
      v
Controller writes status
```

Why status matters:

```text
kubectl get shows progress
other controllers can react
automation can wait for readiness
humans can debug safely
```

Important rule:

```text
Status should describe observed truth.
Spec should describe desired intent.
```

Do not put desired changes in status.

Do not put observed runtime data in spec.

---

# 26. Owner References And Garbage Collection

When a controller creates child objects, it often sets owner references.

Example:

```text
Deployment owns ReplicaSet
ReplicaSet owns Pods
```

ASCII:

```text
Deployment
    |
    | ownerReference
    v
ReplicaSet
    |
    | ownerReference
    v
Pods
```

Why this matters:

If the parent object is deleted, Kubernetes garbage collector can delete child objects.

```text
Delete Deployment
       |
       v
ReplicaSet deleted
       |
       v
Pods deleted
```

Controller lesson:

```text
When creating children, record ownership.
```

Otherwise orphan resources may remain.

Production symptom:

```text
Old Pods or Services remain after custom resource deletion.
```

Debug:

```bash
kubectl get pod <pod> -o yaml | grep -A20 ownerReferences
kubectl get rs <rs> -o yaml | grep -A20 ownerReferences
```

---

# 27. Finalizers: Cleanup Before Delete

Some resources need cleanup before deletion.

Example custom resource:

```text
MiniDatabase creates cloud disk snapshot
MiniDatabase creates DNS record
MiniDatabase creates external backup bucket
```

If the Kubernetes object is deleted immediately, external resources may leak.

Finalizer pattern:

```text
1. Object has finalizer.
2. User deletes object.
3. Object gets deletionTimestamp but remains visible.
4. Controller sees deletionTimestamp.
5. Controller cleans external resources.
6. Controller removes finalizer.
7. Kubernetes completes deletion.
```

ASCII:

```text
kubectl delete MiniDatabase
          |
          v
metadata.deletionTimestamp set
          |
          v
Controller cleanup
          |
          v
Remove finalizer
          |
          v
Object disappears
```

Production warning:

```text
Broken finalizer can make objects stuck in Terminating.
```

Debug:

```bash
kubectl get <resource> <name> -o yaml
```

Look for:

```yaml
metadata:
  deletionTimestamp: ...
  finalizers:
    - example.com/finalizer
```

---

# 28. Resync: Why Controllers Requeue Even Without Events

Informers may support periodic resync.

Resync does not mean relisting everything from API Server every second.

Mental model:

```text
Periodically re-send cached objects to handlers
so controller gets another chance to reconcile.
```

Why useful?

```text
missed external side effects
controller bug recovery
manual external modification
status drift
backup safety
```

ASCII:

```text
Normal event path:
API change -> event -> queue -> reconcile

Resync path:
Cache item -> queue -> reconcile again
```

Important:

Reconcile must be idempotent because resync may enqueue objects even if nothing changed.

Bad controller:

```text
resync causes duplicate external resources
```

Good controller:

```text
resync checks existence and does nothing if already correct
```

Memory hook:

```text
Resync is a safety net, not the main engine.
```

---

# 29. Conflict Handling

Kubernetes objects can be updated by multiple actors.

Example:

```text
Controller A updates status.
User updates spec.
Another controller updates annotation.
```

If you update based on stale resourceVersion, API Server may reject with conflict.

```text
409 Conflict
```

Correct controller behavior:

```text
Refetch latest object.
Recompute patch/update.
Try again.
```

ASCII:

```text
Controller reads object rv=100
        |
        | user updates object rv=101
        v
Controller tries update based on rv=100
        |
        v
API Server: conflict
        |
        v
Controller requeues and retries with latest state
```

Do not treat conflict as fatal.

It is normal in distributed control systems.

Production lesson:

```text
Conflicts mean the object changed while you were working.
Retry using current state.
```

---

# 30. Watch + Informer + Reconcile Full Dry Run

You run:

```bash
kubectl apply -f deployment.yaml
```

Deployment object:

```text
name: order-service
replicas: 3
image: order-service:1.0.0
```

Dry run:

```text
1. kubectl sends request to API Server.

2. API Server validates and stores Deployment in etcd.

3. API Server sends watch event:
   ADDED Deployment default/order-service.

4. Deployment informer receives event.

5. Informer updates local cache.

6. Event handler enqueues key:
   default/order-service.

7. Worker pops key from queue.

8. Reconcile reads Deployment from cache.

9. Reconcile checks if matching ReplicaSet exists.

10. ReplicaSet missing, so controller creates ReplicaSet through API Server.

11. API Server stores ReplicaSet in etcd.

12. ReplicaSet informer receives ADDED event.

13. ReplicaSet controller enqueues key.

14. ReplicaSet reconcile sees desired replicas=3.

15. It creates 3 Pod objects.

16. Pod informer/scheduler sees Pending Pods.

17. Scheduler binds Pods to Nodes.

18. Kubelet starts containers.

19. Status updates flow back through API Server.

20. Controllers update Deployment status.
```

ASCII:

```text
kubectl apply
    |
    v
API Server -> etcd
    |
    | watch event
    v
Deployment Informer -> Queue -> Reconcile
                              |
                              v
                       create ReplicaSet
                              |
                              v
ReplicaSet Informer -> Queue -> Reconcile
                              |
                              v
                         create Pods
                              |
                              v
Scheduler -> Kubelet -> Containers
```

---

# 31. Dry Run: Pod Deleted Manually

Initial state:

```text
Deployment desired replicas = 3
Actual Pods = 3
```

Human does:

```bash
kubectl delete pod order-service-abc
```

Flow:

```text
1. Pod deletion stored through API Server.
2. Pod watch event reaches ReplicaSet controller.
3. Pod informer updates cache.
4. ReplicaSet-related key is enqueued.
5. ReplicaSet reconcile counts matching Pods.
6. Actual active Pods = 2.
7. Desired replicas = 3.
8. Controller creates one replacement Pod.
```

ASCII:

```text
Human deletes Pod
       |
       v
Pod event DELETED
       |
       v
ReplicaSet controller wakes
       |
       v
desired=3 actual=2
       |
       v
create replacement Pod
```

Lesson:

```text
Kubernetes does not remember the deleted Pod personally.
It remembers the desired replica count.
```

The controller repairs the level, not the specific deleted instance.

---

# 32. Dry Run: Watch Disconnects

Controller is running.

```text
Informer watching Pods from rv=5000
```

Network breaks.

```text
Watch stream disconnected
```

Controller logic:

```text
1. Try reconnecting watch.
2. If resourceVersion still valid, continue.
3. If too old, relist objects.
4. Rebuild cache.
5. Resume watch from new version.
```

ASCII:

```text
Watch stream
   |
   X network break
   |
   v
Reconnect
   |
   +--> success: continue watch
   |
   +--> too old: LIST again -> rebuild cache -> WATCH
```

Production meaning:

```text
Temporary watch disconnect is normal.
Permanent relist storms are dangerous.
```

If many controllers repeatedly relist large resources, API Server load can spike.

This is why client-go informers implement backoff, watch recovery, and caching.

---

# 33. Production Story: Controller Falls Behind

Symptoms:

```text
Objects updated, but controller reacts slowly.
Work queue length increasing.
Reconcile latency high.
API Server requests rising.
```

Possible causes:

```text
Too few worker threads
slow reconcile logic
external API timeout
event handler doing heavy work
rate limit too aggressive
hot object updated too frequently
API Server watch instability
```

ASCII:

```text
Events arrive faster than workers process

API Server events
   |
   v
Informer
   |
   v
Queue: 10 -> 100 -> 1000 -> 10000
   |
   v
Workers overloaded
```

Debug mindset:

```text
Is the watch receiving events?
Is cache syncing?
Is queue growing?
Are workers failing?
Are retries exploding?
```

Useful metrics in controller systems:

```text
workqueue_depth
workqueue_adds_total
workqueue_retries_total
workqueue_queue_duration_seconds
workqueue_work_duration_seconds
reconcile_errors_total
```

Even if you are not writing controllers, this helps you understand Kubernetes control-plane behavior.

---

# 34. Production Story: Bad Reconcile Creates Duplicate Resources

A custom operator manages a backup CronJob.

Bad reconcile logic:

```text
Every reconcile creates a new CronJob with generated name.
```

After several resyncs:

```text
backup-abc
backup-def
backup-ghi
backup-jkl
```

Problem:

```text
Multiple backups run.
Storage cost increases.
Database load spikes.
```

Correct logic:

```text
Expected CronJob name = <database-name>-backup
Check if it exists.
If missing, create.
If exists but spec differs, update.
If correct, do nothing.
```

ASCII:

```text
Bad:
reconcile -> create random child
reconcile -> create random child
reconcile -> create random child

Good:
reconcile -> ensure named child exists and matches desired spec
```

Controller rule:

```text
Use deterministic names or labels.
Always check before creating.
```

---

# 35. Production Story: Stuck Finalizer

A custom resource is deleted:

```bash
kubectl delete minidatabase orders-db
```

But it remains:

```text
orders-db   Terminating
```

YAML shows:

```yaml
metadata:
  deletionTimestamp: "2026-06-14T10:00:00Z"
  finalizers:
    - database.example.com/finalizer
```

Meaning:

```text
Kubernetes is waiting for controller cleanup.
```

Possible causes:

```text
operator not running
operator lacks RBAC permission
external cleanup API failing
bug in deletion reconcile path
finalizer name changed across versions
```

Debug:

```bash
kubectl get pods -n operator-system
kubectl logs deployment/minidatabase-operator -n operator-system
kubectl describe minidatabase orders-db
kubectl auth can-i update minidatabases/finalizers
```

Emergency manual finalizer removal can delete the object, but may leak external resources.

Production mindset:

```text
Finalizer stuck means cleanup contract is not completed.
Do not remove blindly unless you understand what cleanup is skipped.
```

---

# 36. Production Story: Status Update Loop

Bad controller behavior:

```text
Every reconcile writes status with current timestamp.
```

Example:

```yaml
status:
  lastCheckedAt: every second
```

Each status update creates a new watch event.

That event triggers reconcile again.

Loop:

```text
Reconcile
   |
   v
Update status
   |
   v
Watch event
   |
   v
Queue
   |
   v
Reconcile again
```

Result:

```text
High API Server write load
High etcd write load
Controller CPU usage
No real state change
```

Correct behavior:

```text
Only update status when meaningful fields changed.
Use conditions carefully.
Avoid noisy timestamps.
```

Memory hook:

```text
A controller can DOS itself by writing unnecessary status updates.
```

---

# 37. Production Story: RBAC Blocks Reconcile

A controller tries to create a child Service.

Logs:

```text
services is forbidden: User "system:serviceaccount:operators:my-operator"
cannot create resource "services" in API group "" in namespace "default"
```

Meaning:

```text
Reconcile logic is correct, but permission is missing.
```

ASCII:

```text
Controller reconcile
        |
        v
API Server authorization
        |
        v
DENIED
        |
        v
Requeue with error
```

Debug:

```bash
kubectl auth can-i create services \
  --as system:serviceaccount:operators:my-operator \
  -n default
```

Fix:

```text
Add proper Role/ClusterRole and RoleBinding/ClusterRoleBinding.
```

Lesson:

```text
Controller failure is not always logic failure.
Sometimes the API Server refuses the action.
```

---

# 38. Debugging Mindset: Follow The Event Pipeline

When a controller does not behave, debug in order:

```text
1. Does the desired object exist?
2. Is the spec correct?
3. Is the controller running?
4. Has the informer cache synced?
5. Are watch events arriving?
6. Is the key being enqueued?
7. Is queue depth growing?
8. Is reconcile being called?
9. Is reconcile returning errors?
10. Is API Server rejecting writes?
11. Are status conditions updated?
12. Are child resources owned/labeled correctly?
```

ASCII:

```text
Object Change
     |
     v
Watch Event
     |
     v
Informer Cache
     |
     v
Queue
     |
     v
Worker
     |
     v
API Write
     |
     v
Status / Child Object
```

If behavior breaks, locate which arrow is broken.

Do not randomly edit YAML.

Follow the pipeline.

---

# 39. Useful Kubernetes Commands

For normal app controllers:

```bash
kubectl get deployment order-service -o yaml
kubectl describe deployment order-service
kubectl get rs -l app=order-service
kubectl get pods -l app=order-service -o wide
kubectl describe pod <pod-name>
```

For Service endpoint reconciliation:

```bash
kubectl get svc order-service
kubectl get endpoints order-service
kubectl get endpointslice -l kubernetes.io/service-name=order-service
kubectl get pods --show-labels
```

For custom resources:

```bash
kubectl get crd
kubectl get <custom-resource> <name> -o yaml
kubectl describe <custom-resource> <name>
```

For finalizers:

```bash
kubectl get <resource> <name> -o yaml | grep -A20 finalizers
```

For RBAC:

```bash
kubectl auth can-i create deployments --as system:serviceaccount:ns:sa -n default
kubectl auth can-i update <resource>/status --as system:serviceaccount:ns:sa -n default
```

For controller logs:

```bash
kubectl logs deployment/<controller-name> -n <namespace>
kubectl logs deployment/<controller-name> -n <namespace> --previous
```

---

# 40. How This Connects To API Server And etcd

API Server is the watch and write gateway.

etcd is the durable state store.

Controllers do not watch etcd directly.

Correct flow:

```text
Controller
   |
   | list/watch/write
   v
API Server
   |
   | persist/read
   v
etcd
```

Watch path:

```text
etcd state changes
       |
       v
API Server watch machinery
       |
       v
Informer
```

Write path:

```text
Reconcile action
       |
       v
API Server validation/admission/RBAC
       |
       v
etcd persistence
       |
       v
new watch event
```

ASCII:

```text
              write action
Controller ----------------> API Server ------------> etcd
    ^                          |
    |                          | watch event
    +--------------------------+
```

This forms a feedback loop.

Every action can generate more events.

That is why controllers must avoid noisy writes and must be idempotent.

---

# 41. How This Connects To Spring Boot Apps

Your Spring Boot app is usually not a controller.

But Kubernetes controls it through this machinery.

Example:

```text
You update Deployment image from v1 to v2.
```

Controller chain:

```text
Deployment controller sees spec change.
ReplicaSet controller creates Pods.
Scheduler assigns Nodes.
Kubelet starts containers.
EndpointSlice controller updates Service endpoints when Pods become Ready.
```

Your app participates through:

```text
container startup
readiness probe
liveness probe
logs
metrics
termination handling
```

ASCII:

```text
Deployment image change
       |
       v
Controller reconciliation
       |
       v
New Pod
       |
       v
Spring Boot starts
       |
       v
Readiness passes
       |
       v
EndpointSlice updated
       |
       v
Traffic reaches app
```

Lesson for Java engineers:

```text
Correct readiness/liveness behavior makes Kubernetes reconciliation safe.
Bad probes confuse controllers and traffic routing.
```

---

# 42. Spring Boot Readiness Example

Application config:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      probes:
        enabled: true
```

Kubernetes Deployment probe:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5
  failureThreshold: 3
```

Controller effect:

```text
Pod Running but readiness failing
       |
       v
Pod not Ready
       |
       v
EndpointSlice excludes Pod
       |
       v
Service does not route traffic
```

ASCII:

```text
Spring Boot process alive
        |
        v
Readiness DOWN
        |
        v
Pod Ready=False
        |
        v
Endpoint controller removes endpoint
        |
        v
No user traffic
```

This is reconciliation based on observed status.

---

# 43. Beginner Mistakes

```text
Mistake 1:
Thinking watch means controller never needs list.
Correct:
Controllers usually use list-watch.

Mistake 2:
Doing heavy work in informer event handler.
Correct:
Handler should enqueue key and return fast.

Mistake 3:
Putting full objects in queue.
Correct:
Queue keys; reconcile reads latest state.

Mistake 4:
Assuming every event is delivered forever.
Correct:
Watch can break; relist may be needed.

Mistake 5:
Writing non-idempotent reconcile logic.
Correct:
Reconcile may run many times safely.

Mistake 6:
Updating status every time even when unchanged.
Correct:
Only write meaningful changes.

Mistake 7:
Ignoring finalizers.
Correct:
Deletion may require cleanup before object disappears.

Mistake 8:
Confusing operator with magic.
Correct:
Operator = CRD + reconcile loop.
```

---

# 44. Interview Questions

## What is a Kubernetes watch?

A watch is a long-running API Server request that streams object changes to a client. Instead of repeatedly listing objects, a controller can watch resources and receive events such as ADDED, MODIFIED, and DELETED.

## Why do controllers use list-watch?

A controller needs the current state and future changes. LIST provides the initial snapshot. WATCH streams changes after that snapshot using a resourceVersion.

## What is an informer?

An informer is a client-side mechanism that performs list-watch, maintains a local cache of objects, and invokes event handlers when objects change. Controllers commonly use informers to avoid repeatedly querying the API Server.

## Why do informers use a local cache?

Controllers read objects often. A local cache reduces API Server and etcd load. The informer keeps the cache updated using watch events.

## What is a work queue?

A work queue stores object keys that need reconciliation. It provides buffering, deduplication, retries, rate limiting, and worker concurrency.

## Why store keys in the queue instead of full objects?

The full object may become stale before processing. Storing the key lets the worker fetch the latest state from cache before reconciling.

## What is reconcile?

Reconcile is the controller function that compares desired state with actual state and performs actions to move actual state toward desired state.

## Why must reconcile be idempotent?

Reconcile can run multiple times for the same object because of duplicate events, resyncs, retries, status changes, or controller restarts. Running it repeatedly must not create incorrect duplicate side effects.

## What does level-based reconciliation mean?

Level-based reconciliation means the controller looks at current desired state and current actual state, rather than relying on every historical event. This makes controllers robust to missed or duplicate events.

## What is an operator?

An operator is a custom controller plus usually a custom resource definition. It encodes operational knowledge into a reconcile loop that manages resources like databases, queues, or applications.

## What are finalizers?

Finalizers block object deletion until a controller performs cleanup. After cleanup, the controller removes the finalizer and Kubernetes completes deletion.

## What causes a resource to be stuck in Terminating?

A resource can be stuck if it has a finalizer and the responsible controller cannot or does not remove it. Causes include controller crash, RBAC problems, external cleanup failure, or bugs.

---

# 45. Cheat Sheet

```text
Watch
  Long-running stream of object changes from API Server.

List-Watch
  LIST current objects, then WATCH changes after resourceVersion.

resourceVersion
  Continuation/version marker used for consistent watches.

Informer
  List-watch helper + local cache + event handlers.

Cache
  Local in-memory copy of Kubernetes objects.

Event Handler
  OnAdd/OnUpdate/OnDelete callback; should enqueue quickly.

Work Queue
  Buffer of object keys needing reconciliation.

Key
  Usually namespace/name. Reminder to check latest object state.

Reconcile
  Compare desired vs actual, then act.

Idempotent Reconcile
  Safe to run many times.

Level-Based
  React to current state, not historical event sequence.

Resync
  Periodic re-enqueue from cache as safety net.

Finalizer
  Deletion blocker for cleanup.

OwnerReference
  Parent-child ownership metadata for garbage collection.

Operator
  CRD + controller + reconcile loop.
```

Core pipeline:

```text
API Server Watch
      |
      v
Informer
      |
      v
Local Cache
      |
      v
Event Handler
      |
      v
Work Queue
      |
      v
Reconcile Worker
      |
      v
API Server Write
      |
      v
New State / New Event
```

---

# 46. One Picture To Remember

```text
                            KUBERNETES CONTROL LOOP

                           +----------------------+
                           |      API Server      |
                           | list / watch / write |
                           +----------+-----------+
                                      |
                                      | watch stream
                                      v
                           +----------------------+
                           |      Informer        |
                           | list-watch machinery |
                           +----------+-----------+
                                      |
                       update cache  |  call handler
                         +------------+------------+
                         |                         |
                         v                         v
              +--------------------+     +----------------------+
              | Local Cache        |     | Event Handler        |
              | current known view |     | enqueue key fast     |
              +--------------------+     +----------+-----------+
                                                    |
                                                    v
                                         +----------------------+
                                         | Work Queue           |
                                         | namespace/name keys  |
                                         +----------+-----------+
                                                    |
                                                    v
                                         +----------------------+
                                         | Reconcile Worker     |
                                         | desired vs actual    |
                                         +----------+-----------+
                                                    |
                                                    | create/update/delete
                                                    v
                                         +----------------------+
                                         | API Server           |
                                         +----------------------+

Rule:

Events do not directly mean actions.
Events mean: something changed, please check current truth.

Reconcile is the repair function that makes reality match desired state.
```

---

# 47. Final Production Checklist

```text
[ ] I understand watch as an API Server event stream.
[ ] I understand list-watch: snapshot first, then changes.
[ ] I understand resourceVersion as a watch continuation marker.
[ ] I understand informer as list-watch + cache + handlers.
[ ] I understand why local cache reduces API Server load.
[ ] I understand why handlers should enqueue, not do heavy work.
[ ] I understand why queue stores keys, not full old objects.
[ ] I understand work queue retry and rate limiting.
[ ] I understand reconcile as desired vs actual repair logic.
[ ] I understand idempotency.
[ ] I understand level-based reconciliation.
[ ] I understand resync as a safety net.
[ ] I understand owner references and garbage collection.
[ ] I understand finalizers and stuck Terminating resources.
[ ] I can debug controller behavior from watch to queue to reconcile.
[ ] I can explain operators as CRD + reconcile loop.
```

---

# 48. Final Memory Hook

Do not memorize this chapter as definitions.

Remember the production machine:

```text
Watch = hear that something changed
Informer = keep local memory updated
Queue = remember what needs checking
Reconcile = repair the world safely
```

Final sentence:

```text
Kubernetes controllers are not magic scripts.
They are event-driven, cache-backed, retry-safe repair loops that continuously turn desired state into actual production reality.
```
