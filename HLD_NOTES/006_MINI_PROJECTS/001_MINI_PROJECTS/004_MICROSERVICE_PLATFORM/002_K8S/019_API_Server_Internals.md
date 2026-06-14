# 019_API_Server_Internals.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why API Server Internals Matter

Most Kubernetes learners say:

```text
kubectl talks to API Server.
API Server stores data in etcd.
```

That is true, but too shallow.

In production, many hard Kubernetes problems are API Server problems:

```text
kubectl apply is slow
controller is not reacting
webhook is blocking deployments
RBAC denies a valid request
CRD validation rejects objects
etcd latency makes cluster slow
watch stream disconnects
API Priority and Fairness throttles clients
```

If you memorize:

```text
API Server = front door
```

you will pass beginner questions.

If you understand the internals, you can debug real clusters.

The API Server is not just a REST endpoint.

It is the central traffic controller for Kubernetes state.

```text
Every desired-state change
Every status update
Every controller watch
Every scheduler bind
Every kubelet report
Every admission decision
Every RBAC check

passes through API Server.
```

One picture:

```text
                 EVERYTHING
                     |
                     v
              +-------------+
              | API SERVER  |
              +------+------+
                     |
                     v
                   etcd
```

Mental model:

```text
API Server = Kubernetes state gateway + policy gate + consistency boundary
```

If this component is unhealthy, the cluster may still run existing Pods, but changing the cluster becomes painful.

---

# 2. The Wrong Way To Think About API Server

Wrong model:

```text
kubectl sends YAML
API Server saves YAML
Done
```

Better model:

```text
Request comes in
    |
    v
Authenticate
    |
    v
Authorize
    |
    v
Admission chain
    |
    v
Validation
    |
    v
Defaulting
    |
    v
Storage conversion
    |
    v
etcd write
    |
    v
Watch event fan-out
```

The API Server is a pipeline.

```text
Client Request
      |
      v
+------------------------+
| Kubernetes API Pipeline |
+------------------------+
      |
      v
State Change / Read / Watch
```

Do not think of the API Server as a passive database proxy.

It actively decides whether the request is valid, allowed, mutated, rejected, converted, persisted, and broadcast.

---

# 3. Real World Analogy: Government Office

Imagine a government office where every official document must pass through one counter.

You cannot directly update the national land registry.

You must go through:

```text
Identity check
Permission check
Form validation
Stamping
Legal review
Record update
Notification to departments
```

Kubernetes is similar.

You cannot directly edit etcd.

You go through API Server.

```text
Developer / Controller / Kubelet
            |
            v
     Kubernetes API Server
            |
            v
           etcd
```

The API Server is the official counter.

etcd is the official record room.

Controllers are departments watching changes.

```text
Public Counter       = API Server
Record Room          = etcd
Departments          = Controllers
Citizen Application  = kubectl apply
Legal Review         = Admission Webhook
Identity Card Check  = Authentication
Permission Check     = Authorization
```

Mental hook:

```text
Kubernetes state changes are official only after API Server accepts them.
```

---

# 4. API Server In The Full Control Plane

```text
                         USER
                          |
                          | kubectl apply/get/watch
                          v
                  +------------------+
                  |   API SERVER     |
                  +----+--------+----+
                       |        |
                       |        | watch/list
                       |        v
                       |  +-------------+
                       |  | Controllers |
                       |  +-------------+
                       |
                       | scheduler watches/binds
                       v
                  +-------------+
                  |    etcd     |
                  +-------------+

Worker side:

+-------------+       reports status / watches assigned pods
|   Kubelet   |  <------------------------------------------+
+-------------+                                             |
        |                                                   |
        +-----------------------> API Server <--------------+
```

Every major component depends on API Server:

```text
kubectl              -> create/read/update/delete/watch objects
controller-manager   -> watch objects, update status, create children
scheduler            -> watch pending Pods, bind Pods to Nodes
kubelet              -> watch Pods assigned to node, update Pod status
operators            -> watch CRDs, reconcile custom resources
admission webhooks   -> receive admission review requests
```

The API Server is the control plane meeting point.

---

# 5. What API Server Stores And What It Does Not Store

The API Server itself is mostly stateless.

Important:

```text
API Server does not permanently store cluster data inside itself.
```

Persistent state lives in etcd.

API Server does:

```text
accept requests
validate objects
apply defaults
enforce authz
run admission plugins
convert versions
read/write etcd
serve watches
serve discovery
aggregate APIs
expose metrics
```

etcd does:

```text
store Kubernetes objects durably
maintain revisions
support consistent reads
support watch history
```

Pods do not run inside API Server.

Controllers do not run inside API Server.

Spring Boot apps do not run inside API Server.

API Server is the gate.

```text
Client
  |
  v
API Server
  |
  v
etcd record
```

Think:

```text
API Server = brain's communication gateway
etcd       = memory
controllers= decision loops
nodes      = execution
```

---

# 6. API Request Lifecycle: Big Picture

A normal create request follows this path:

```text
kubectl apply -f deployment.yaml
        |
        v
HTTP request to API Server
        |
        v
Authentication
        |
        v
Authorization
        |
        v
Admission mutation
        |
        v
Object schema validation
        |
        v
Admission validation
        |
        v
Storage version conversion
        |
        v
etcd write
        |
        v
Response to client
        |
        v
Watch event to controllers
```

ASCII:

```text
+---------+    +-------------+    +-------------+    +-------------+
| Client  | -> | AuthN       | -> | AuthZ       | -> | Admission   |
+---------+    +-------------+    +-------------+    +-------------+
                                                        |
                                                        v
+---------+    +-------------+    +-------------+    +-------------+
| Watch   | <- | etcd write  | <- | Validation  | <- | Defaulting  |
+---------+    +-------------+    +-------------+    +-------------+
```

A request does not become cluster truth until persisted.

```text
Accepted by HTTP != stored in etcd
Stored in etcd  = visible cluster state
```

---

# 7. Authentication: Who Are You?

Authentication answers:

```text
Who is making this request?
```

Common identities:

```text
human user
service account
kubelet
controller manager
scheduler
operator
CI/CD pipeline
```

Example:

```bash
kubectl get pods
```

API Server asks:

```text
What credentials came with this request?
```

Possible credential types:

```text
client certificate
bearer token
service account token
OIDC token
bootstrap token
proxy authentication
```

Mental model:

```text
Authentication attaches identity to request.
```

ASCII:

```text
Request
  |
  | token/cert
  v
API Server
  |
  v
Identity:
  user = alice@example.com
  groups = ["developers", "system:authenticated"]
```

Spring Boot analogy:

```text
JWT authentication filter extracts user from token.
Kubernetes authentication extracts user from request credentials.
```

Authentication does not decide permission.

It only says who you are.

---

# 8. Authorization: Are You Allowed?

Authorization answers:

```text
Can this identity perform this action on this resource?
```

Kubernetes checks:

```text
verb      = get/list/watch/create/update/patch/delete
resource  = pods/deployments/secrets/configmaps
namespace = dev/prod/default
name      = optional object name
apiGroup  = core/apps/batch/networking.k8s.io
```

Example:

```text
User: mohamed
Verb: delete
Resource: pods
Namespace: prod
```

RBAC says yes or no.

ASCII:

```text
Authenticated Request
        |
        v
+------------------+
| Authorization    |
+------------------+
        |
        +--> allowed -> continue
        |
        +--> denied  -> 403 Forbidden
```

RBAC mental model:

```text
Role        = permissions inside namespace
ClusterRole = permissions cluster-wide or reusable permission set
RoleBinding = attach role to user/service account
```

Example:

```yaml
kind: Role
rules:
  - apiGroups: [""]
    resources: ["pods"]
    verbs: ["get", "list", "watch"]
```

Do not memorize RBAC.

Think:

```text
Who can do what, to which object, where?
```

---

# 9. Admission Control: Should The Request Be Changed Or Blocked?

After authn/authz, admission control runs.

Admission answers:

```text
Even if you are allowed, is this object acceptable for cluster policy?
```

Admission has two major types:

```text
Mutating admission  = can modify object before storage
Validating admission = can reject object
```

Examples:

```text
Add default labels
Inject sidecar container
Require resource limits
Block privileged containers
Require image registry allowlist
Enforce namespace policy
Validate custom business rules
```

ASCII:

```text
Incoming Pod
   |
   v
Mutating Admission
   |
   | add sidecar / labels / defaults
   v
Validating Admission
   |
   | approve or reject
   v
etcd
```

Real production example:

```text
Developer creates Pod without resource limits.
Admission policy rejects it.
```

Error:

```text
admission webhook "policy.example.com" denied the request
```

Mental hook:

```text
RBAC asks: are you allowed?
Admission asks: is the object acceptable?
```

---

# 10. Mutating Webhook Example: Sidecar Injection

Service mesh tools often inject sidecars using mutating admission webhooks.

Original Pod:

```yaml
containers:
  - name: order-service
    image: order-service:1.0.0
```

After mutation:

```yaml
containers:
  - name: order-service
    image: order-service:1.0.0
  - name: envoy
    image: envoyproxy/envoy:v1
```

Flow:

```text
kubectl apply pod
      |
      v
API Server
      |
      v
Mutating webhook call
      |
      v
Webhook returns JSON patch
      |
      v
API Server applies patch
      |
      v
Stores mutated Pod in etcd
```

ASCII:

```text
Pod Before Admission

+------------------+
| order-service    |
+------------------+

Pod After Admission

+------------------+
| order-service    |
| envoy sidecar    |
+------------------+
```

Debug mindset:

```text
The YAML you applied may not be exactly the object stored.
```

Check:

```bash
kubectl get pod order -o yaml
```

---

# 11. Validating Webhook Example: Enforcing Production Rules

A validating webhook can reject unsafe objects.

Policy:

```text
All production Pods must define:
- cpu request
- memory request
- readiness probe
- non-root security context
```

Request:

```yaml
containers:
  - name: order-service
    image: order-service:1.0.0
```

Webhook response:

```text
Rejected:
container order-service must set resources.requests.memory
```

ASCII:

```text
Pod request
    |
    v
API Server
    |
    v
Validating Webhook
    |
    +--> safe   -> store
    |
    +--> unsafe -> reject
```

Production story:

```text
A team deployed a Spring Boot service without memory limits.
During traffic spike it consumed too much memory.
Cluster instability followed.
Policy was added:
no production Pod without requests/limits.
```

Admission turns operational lessons into automated guardrails.

---

# 12. Defaulting: Kubernetes Adds Missing Safe Values

Many Kubernetes objects receive default values.

Example:

```yaml
restartPolicy: Always
```

is often defaulted for Pods created by Deployments.

You may not write every field, but the stored object contains more detail.

Mental model:

```text
You submit partial spec.
API Server completes parts using defaults.
```

ASCII:

```text
Input Object
  replicas: missing
       |
       v
Defaulting
       |
       v
Stored Object
  replicas: 1
```

For a Deployment, if replicas is omitted:

```yaml
spec:
  replicas: 1
```

This matters when debugging.

```text
What you wrote != full object API Server stores
```

Command:

```bash
kubectl get deployment order-service -o yaml
```

Use this to see reality.

---

# 13. Validation: Is The Object Shape Legal?

Validation checks schema and object rules.

Examples:

```text
kind must exist
apiVersion must be supported
field types must be correct
required fields must exist
selector must match template labels for Deployment
container name must be valid
port must be valid number
```

Bad YAML:

```yaml
spec:
  replicas: "three"
```

Error:

```text
cannot unmarshal string into Go struct field replicas of type int32
```

Mental model:

```text
Validation protects etcd from invalid cluster records.
```

ASCII:

```text
Object
  |
  v
Schema validation
  |
  +--> valid   -> continue
  |
  +--> invalid -> reject
```

Important:

```text
YAML syntax valid does not mean Kubernetes object valid.
```

---

# 14. Versioning: apiVersion Is Not Decoration

Kubernetes APIs are versioned.

Examples:

```text
apps/v1
batch/v1
networking.k8s.io/v1
apiextensions.k8s.io/v1
```

apiVersion tells API Server:

```text
Which API group and version should parse this object?
```

ASCII:

```text
YAML apiVersion: apps/v1
          |
          v
API Server routes to apps API group
          |
          v
Deployment schema v1
```

Wrong version:

```text
no matches for kind "Ingress" in version "extensions/v1beta1"
```

This happens when old tutorials use deprecated versions.

Mental hook:

```text
apiVersion selects the contract between your YAML and Kubernetes.
```

---

# 15. Storage Version And Conversion

The version you submit may not be the exact internal storage version.

API Server can convert objects between versions.

Example mental model:

```text
Client sends v1beta1
API Server converts to internal representation
API Server stores chosen storage version
Client reads v1
API Server converts back to requested version
```

ASCII:

```text
Client Version
     |
     v
External API Object
     |
     v
Internal Object
     |
     v
Storage Version
     |
     v
etcd
```

For CRDs, conversion webhooks may be needed when multiple versions exist.

Why it matters:

```text
API evolution is possible without breaking every client immediately.
```

Debugging:

```bash
kubectl api-resources
kubectl explain deployment
kubectl get crd <name> -o yaml
```

---

# 16. etcd Write Path

When a write is accepted, API Server persists the object to etcd.

etcd stores key-value records.

Simplified key:

```text
/registry/pods/default/order-service-abc
```

Value:

```text
serialized Pod object
```

ASCII:

```text
API Server
   |
   | PUT /registry/pods/default/order-service-abc
   v
+-----------------------------+
| etcd                        |
| revision: 10501             |
| key: /registry/pods/...     |
| value: Pod object           |
+-----------------------------+
```

Every successful change increases etcd revision.

Watches use revisions to know what changed.

Mental model:

```text
etcd revision = timeline number for cluster state changes
```

If etcd is slow, writes are slow.

If etcd is unhealthy, API Server cannot reliably persist state.

---

# 17. ResourceVersion: Object Timeline Marker

Every Kubernetes object has metadata.resourceVersion.

Example:

```yaml
metadata:
  resourceVersion: "10501"
```

This means:

```text
This object was observed at a particular etcd revision.
```

Controllers use resourceVersion for efficient watches.

Mental model:

```text
resourceVersion is not business version.
It is cluster storage timeline version.
```

ASCII:

```text
Revision 100: Pod created
Revision 101: Pod scheduled
Revision 102: Pod running
Revision 103: Pod ready
```

A watch can say:

```text
Show me changes after revision 101.
```

Why you care:

```text
Stale reads and update conflicts are handled using resourceVersion.
```

---

# 18. Optimistic Concurrency: Update Conflicts

Two clients update the same object.

```text
Client A reads ConfigMap version 10
Client B reads ConfigMap version 10
Client A updates -> version 11
Client B updates based on old version 10 -> conflict
```

Error:

```text
the object has been modified; please apply your changes to the latest version
```

ASCII:

```text
Read v10 ---- Client A ---- Write v11 OK
   |
   +------- Client B ---- Write based on v10 FAIL
```

Mental model:

```text
Kubernetes prevents blind overwrite of newer state.
```

This is similar to optimistic locking in databases.

Java analogy:

```java
// Like JPA @Version optimistic locking
// If version changed before update, update fails.
```

Controller code must handle conflicts by retrying.

---

# 19. List And Watch: How Controllers Stay Informed

Controllers do not poll the whole cluster every second.

They use list + watch.

Typical pattern:

```text
1. LIST current objects
2. Remember resourceVersion
3. WATCH changes after that version
4. Reconcile changed objects
```

ASCII:

```text
Controller
    |
    | LIST deployments
    v
API Server
    |
    | full current snapshot
    v
Controller cache
    |
    | WATCH from resourceVersion
    v
Stream of events:
  ADDED
  MODIFIED
  DELETED
```

This is why Kubernetes scales better than naive polling.

Common watch events:

```text
ADDED
MODIFIED
DELETED
BOOKMARK
ERROR
```

Mental hook:

```text
Controllers are event-driven, but reconciliation is still state-based.
```

---

# 20. Informers And Local Cache

Kubernetes controllers often use informers.

Informer mental model:

```text
API watch stream
      |
      v
local cache
      |
      v
event handlers
      |
      v
work queue
      |
      v
reconcile loop
```

ASCII:

```text
+-------------+     watch      +-------------+
| API Server  | -------------> | Informer    |
+-------------+                +------+------+
                                      |
                                      v
                               +-------------+
                               | Local Cache |
                               +------+------+
                                      |
                                      v
                               +-------------+
                               | Work Queue  |
                               +------+------+
                                      |
                                      v
                               +-------------+
                               | Reconciler  |
                               +-------------+
```

Important:

```text
Controllers usually read from cache, not directly from API Server every time.
```

This reduces API load.

Production problem:

```text
If watch is broken or cache stale, controllers may lag.
```

---

# 21. Watch Cache Inside API Server

The API Server itself maintains a watch cache for many resources.

Without cache:

```text
Every list/watch would hit etcd heavily.
```

With cache:

```text
API Server can serve many reads and watches efficiently.
```

ASCII:

```text
              +----------------+
Client LIST ->| API Server     |
              |                |
              | Watch Cache    |
              +-------+--------+
                      |
                      v
                    etcd
```

Mental model:

```text
API Server protects etcd from excessive read/watch pressure.
```

But cache does not make API Server magic.

If clients request huge lists often, API Server CPU and memory can spike.

Bad client behavior:

```text
list all pods in all namespaces every second
```

Better behavior:

```text
use watch/informer with resourceVersion
```

---

# 22. API Priority And Fairness

In large clusters, not all API traffic should be treated equally.

Example traffic:

```text
kubelet status updates
scheduler operations
controllers
humans running kubectl
bad automation script listing everything repeatedly
```

API Priority and Fairness helps classify and limit requests.

Mental model:

```text
API Server has queues and priority lanes.
```

ASCII:

```text
Incoming Requests
      |
      v
+----------------------+
| Priority / Fairness  |
+----------+-----------+
           |
  +--------+---------+
  |        |         |
  v        v         v
system   controllers humans
high     medium      lower
```

Symptoms of throttling:

```text
client-side throttling
request timeout
slow kubectl
controller lag
```

Debug:

```bash
kubectl get --raw /metrics | grep apiserver_flowcontrol
```

Lesson:

```text
A noisy client can hurt cluster control-plane responsiveness.
```

---

# 23. Aggregated API Servers

Kubernetes can extend APIs using aggregation.

Example:

```text
metrics.k8s.io
custom.metrics.k8s.io
external.metrics.k8s.io
```

Flow:

```text
kubectl top pods
      |
      v
API Server
      |
      v
Aggregated metrics API
      |
      v
metrics-server
```

ASCII:

```text
Client
  |
  v
Main API Server
  |
  +--> Core APIs
  |
  +--> Aggregated APIService
          |
          v
      Extension API Server
```

If aggregated API is broken:

```text
kubectl top fails
HPA cannot read metrics
API discovery can become slow
```

Debug:

```bash
kubectl get apiservice
kubectl describe apiservice v1beta1.metrics.k8s.io
```

Mental model:

```text
Not every API path is served directly by core API Server storage.
Some are proxied to extension API servers.
```

---

# 24. CRDs: Teaching Kubernetes New Object Types

A CRD lets you add a new resource type.

Example:

```text
kind: RedisCluster
kind: Certificate
kind: KafkaTopic
kind: ServiceMonitor
```

CRD registers schema with API Server.

```text
Apply CRD
    |
    v
API Server learns new resource type
    |
    v
Users create custom objects
    |
    v
Operator watches and reconciles them
```

ASCII:

```text
CRD: RedisCluster
      |
      v
API Server can store RedisCluster objects
      |
      v
Redis Operator watches RedisCluster
      |
      v
Creates StatefulSet / Service / ConfigMap
```

Important:

```text
CRD only adds API storage and validation.
Controller/operator adds behavior.
```

Without operator:

```text
Custom object exists but nothing happens.
```

---

# 25. API Discovery

kubectl needs to know which resources exist.

Discovery endpoints expose available APIs.

Commands:

```bash
kubectl api-resources
kubectl api-versions
kubectl explain pod.spec.containers
```

Flow:

```text
kubectl
  |
  | asks discovery
  v
API Server
  |
  v
available groups, versions, resources, schemas
```

ASCII:

```text
API Server
  |
  +-- core/v1: pods, services, configmaps
  +-- apps/v1: deployments, replicasets
  +-- batch/v1: jobs, cronjobs
  +-- custom.example.com/v1: widgets
```

Production issue:

```text
Broken APIService can slow discovery.
kubectl commands feel slow before doing real work.
```

Debug:

```bash
kubectl get apiservice
```

---

# 26. Status Subresource

Many resources separate spec and status.

```text
spec   = desired state
status = observed state
```

Status is often updated through a status subresource.

Example:

```text
PUT /apis/apps/v1/namespaces/default/deployments/order/status
```

Why?

```text
Human/controller changing desired state should not accidentally overwrite status.
Controller updating status should not accidentally change spec.
```

ASCII:

```text
User updates spec:
replicas: 5

Controller updates status:
availableReplicas: 4

Different responsibility lanes.
```

Operator design lesson:

```text
Your controller should update status to explain reality.
```

Good status helps debugging.

Bad status makes operators mysterious.

---

# 27. Server-Side Apply And Field Ownership

Server-Side Apply tracks which manager owns which fields.

Example managers:

```text
kubectl
helm
operator
autoscaler
```

Problem without ownership:

```text
Tool A sets replicas
Tool B sets replicas
Who owns the final value?
```

Server-Side Apply tracks managedFields.

ASCII:

```text
Object Fields

spec.replicas        owned by hpa-controller
spec.template.image  owned by kubectl
metadata.labels      owned by helm
```

Conflict example:

```text
kubectl apply tries to change field owned by another manager
```

Mental model:

```text
Server-Side Apply turns object updates into collaborative field management.
```

Debug:

```bash
kubectl get deployment order-service -o yaml
```

Look at:

```yaml
metadata:
  managedFields:
```

---

# 28. Patch vs Update vs Apply

Different operations behave differently.

```text
create = new object
update = replace object version
patch  = modify selected fields
apply  = declarative field management
delete = remove object
watch  = observe changes
```

ASCII:

```text
UPDATE:
old object -> new full object

PATCH:
old object + small change -> new object

APPLY:
desired config + field ownership -> merged object
```

Production lesson:

```text
A bad full update can accidentally remove fields.
Patch/apply is often safer for automation.
```

Controller lesson:

```text
Use status update for status.
Use patch for small controlled changes.
Handle conflicts.
```

---

# 29. Delete Path: Grace, Finalizers, Garbage Collection

Delete is not always immediate.

Delete request:

```text
kubectl delete deployment order-service
```

API Server may set:

```yaml
metadata:
  deletionTimestamp: ...
```

Object may remain while finalizers run.

Finalizer mental model:

```text
Before object disappears, someone must clean external resources.
```

Example:

```text
Cloud load balancer
DNS record
Persistent disk
External database
```

ASCII:

```text
Delete request
    |
    v
Object marked for deletion
    |
    v
Finalizers run cleanup
    |
    v
Finalizers removed
    |
    v
Object disappears from etcd
```

Owner references enable garbage collection.

```text
Deployment owns ReplicaSet
ReplicaSet owns Pods
```

When owner is deleted, children can be cleaned.

---

# 30. Dry Run: kubectl apply Deployment

You apply a Deployment.

```text
1. kubectl reads local YAML.
2. kubectl discovers API schema.
3. kubectl sends request to API Server.
4. API Server authenticates user.
5. API Server authorizes create/update on deployments.apps.
6. Mutating admission may add labels/sidecars/defaults.
7. API Server validates final object.
8. Validating admission checks policies.
9. API Server converts object to storage format.
10. API Server writes object to etcd.
11. etcd assigns new revision.
12. API Server returns success.
13. Deployment controller receives watch event.
14. Controller creates ReplicaSet through API Server.
15. ReplicaSet controller creates Pods through API Server.
```

Picture:

```text
kubectl
  |
  v
API pipeline
  |
  v
etcd revision N
  |
  v
watch event
  |
  v
controller reaction
```

Key insight:

```text
kubectl apply does not create Pods directly.
It creates/updates API objects.
Controllers create the next objects.
```

---

# 31. Dry Run: Scheduler Binding A Pod

A Pod is created without nodeName.

```yaml
spec:
  nodeName: ""
```

Scheduler watches pending Pods.

Flow:

```text
1. Scheduler sees unscheduled Pod.
2. Scheduler filters feasible Nodes.
3. Scheduler scores Nodes.
4. Scheduler chooses node-2.
5. Scheduler sends bind/update request to API Server.
6. API Server persists binding in etcd.
7. Kubelet on node-2 sees Pod assigned to it.
8. Kubelet starts containers.
```

ASCII:

```text
Pending Pod in etcd
      |
      v
Scheduler watches
      |
      v
Bind Pod -> node-2
      |
      v
API Server stores update
      |
      v
Kubelet node-2 watches
      |
      v
Container starts
```

Scheduler does not SSH into nodes.

It changes API state.

---

# 32. Dry Run: Kubelet Status Update

Kubelet starts container and reports status.

```text
1. Kubelet watches Pods assigned to its Node.
2. It starts containers via runtime.
3. It runs liveness/readiness/startup probes.
4. It updates Pod status through API Server.
5. API Server validates status update.
6. API Server writes status to etcd.
7. Services/endpoints controllers observe readiness changes.
```

ASCII:

```text
Node kubelet
   |
   | Pod status: Running, Ready=True
   v
API Server
   |
   v
etcd
   |
   v
EndpointSlice controller
   |
   v
Service sends traffic
```

Important:

```text
Pod readiness is not just local node knowledge.
It becomes cluster-visible through API Server status.
```

---

# 33. Java/Spring Boot Analogy: API Gateway + Security Filter + Repository

You can map API Server to a Spring Boot application architecture.

```text
Spring Boot API
  Controller endpoint
  Security filter
  Validation
  Service logic
  Repository
  Database

Kubernetes API Server
  REST endpoint
  Authentication
  Authorization
  Admission/Validation
  Storage layer
  etcd
```

ASCII:

```text
HTTP Request
    |
    v
Spring Security Filter
    |
    v
Controller
    |
    v
Validator
    |
    v
Service
    |
    v
Repository
    |
    v
Database
```

Kubernetes equivalent:

```text
Kubernetes Request
    |
    v
Authentication
    |
    v
Authorization
    |
    v
Admission
    |
    v
Registry/Storage
    |
    v
etcd
```

This analogy is not perfect, but helps.

The API Server is a highly-specialized, policy-heavy, versioned, consistent API gateway for cluster state.

---

# 34. Java Example: Controller Watching Kubernetes API

A Java service can watch Kubernetes resources using the Kubernetes client.

Conceptual example:

```java
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;

public class PodWatcher {
    public static void main(String[] args) throws Exception {
        ApiClient client = Config.defaultClient();
        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();

        Watch<V1Pod> watch = Watch.createWatch(
                client,
                api.listPodForAllNamespacesCall(
                        null, null, null, null,
                        null, null, null, null,
                        null, null, null),
                new com.google.gson.reflect.TypeToken<Watch.Response<V1Pod>>(){}.getType()
        );

        for (Watch.Response<V1Pod> event : watch) {
            System.out.println(event.type + " " +
                    event.object.getMetadata().getNamespace() + "/" +
                    event.object.getMetadata().getName());
        }
    }
}
```

Mental model:

```text
Java client is not reading etcd.
Java client watches API Server.
```

Production warning:

```text
Do not build clients that repeatedly list all objects.
Use watch/informer-style logic.
```

---

# 35. Spring Boot Example: Admission Webhook Shape

A validating webhook is just an HTTPS service that receives AdmissionReview objects.

High-level Spring Boot controller:

```java
@RestController
@RequestMapping("/validate")
public class ValidationWebhookController {

    @PostMapping
    public AdmissionReviewResponse validate(@RequestBody AdmissionReviewRequest request) {
        String kind = request.getRequest().getKind().getKind();

        if ("Pod".equals(kind)) {
            boolean hasResources = checkResources(request);

            if (!hasResources) {
                return AdmissionReviewResponse.denied(
                        request.getRequest().getUid(),
                        "Pods must define CPU and memory requests");
            }
        }

        return AdmissionReviewResponse.allowed(request.getRequest().getUid());
    }

    private boolean checkResources(AdmissionReviewRequest request) {
        // Parse Pod spec and verify resources.requests are present.
        // Real implementation must carefully handle nulls and JSON structure.
        return true;
    }
}
```

Webhook flow:

```text
API Server
   |
   | HTTPS AdmissionReview
   v
Spring Boot Webhook
   |
   | allowed / denied / patch
   v
API Server
```

Production warning:

```text
Webhook downtime can block deployments if failurePolicy is Fail.
```

Design webhooks like critical infrastructure.

---

# 36. Production Story: Webhook Blocks All Deployments

Symptoms:

```text
kubectl apply hangs or fails
Error from server: failed calling webhook
```

Root cause:

```text
Validating webhook service is down.
API Server cannot complete admission call.
failurePolicy: Fail
```

ASCII:

```text
Deployment request
      |
      v
API Server
      |
      v
Webhook Service X
      |
      v
No response
      |
      v
Request fails
```

Debug:

```bash
kubectl get validatingwebhookconfigurations
kubectl describe validatingwebhookconfiguration <name>
kubectl get svc -A | grep webhook
kubectl get endpoints -A | grep webhook
kubectl logs -n <namespace> <webhook-pod>
```

Fix options:

```text
restore webhook service
adjust timeoutSeconds
change failurePolicy carefully
exclude kube-system if appropriate
rollback bad webhook config
```

Mental hook:

```text
Admission webhooks sit directly in the write path.
A bad webhook can become a cluster-wide deployment outage.
```

---

# 37. Production Story: kubectl Slow But Pods Still Running

Symptoms:

```text
Applications still serve traffic.
kubectl get pods is slow.
Deployments take long time.
Controllers lag.
```

Possible causes:

```text
API Server CPU high
etcd latency high
too many watches
bad automation listing objects repeatedly
APF throttling
audit logging overhead
network latency to etcd
large objects or too many events
```

ASCII:

```text
Existing traffic path:

Client -> Service -> Pod

does not need API Server for every request.

Control path:

kubectl/controllers/kubelet -> API Server -> etcd
```

Important:

```text
API Server outage does not instantly kill running Pods.
But cluster management and reconciliation suffer.
```

Debug:

```bash
kubectl get --raw /readyz?verbose
kubectl get --raw /livez?verbose
kubectl top pods -n kube-system
kubectl logs -n kube-system kube-apiserver-<node>
```

Managed clusters expose this differently, but the mindset remains.

---

# 38. Production Story: RBAC Denied

Error:

```text
Error from server (Forbidden): pods is forbidden:
User "system:serviceaccount:dev:ci-bot" cannot list resource "pods"
in API group "" in the namespace "prod"
```

Read the error like a sentence:

```text
Identity:
system:serviceaccount:dev:ci-bot

Verb:
list

Resource:
pods

Namespace:
prod
```

Fix:

```text
Create/adjust Role or ClusterRole.
Bind it to the service account.
Use least privilege.
```

Debug:

```bash
kubectl auth can-i list pods --as=system:serviceaccount:dev:ci-bot -n prod
kubectl auth can-i create deployments -n prod
```

ASCII:

```text
Request
  |
  v
AuthN: serviceaccount dev/ci-bot
  |
  v
AuthZ: can list pods in prod?
  |
  +--> no -> Forbidden
```

Do not randomly give cluster-admin.

Understand the missing permission.

---

# 39. Production Story: Update Conflict In Controller

Controller error:

```text
Operation cannot be fulfilled on deployments.apps "order-service":
the object has been modified
```

Meaning:

```text
Controller tried to update an old copy of the object.
Someone else updated it first.
```

Common actors:

```text
kubectl
operator
HPA
GitOps controller
another controller
```

ASCII:

```text
Controller cache sees version 50
Actual object becomes version 51
Controller writes version 50 update
API Server rejects
```

Correct controller behavior:

```text
requeue
fetch latest
merge desired change
retry with backoff
```

Bad behavior:

```text
crash
tight retry loop
overwrite fields blindly
```

Mental hook:

```text
Kubernetes is shared-state coordination.
Conflicts are normal, not always fatal.
```

---

# 40. Debugging API Server Problems: Layer By Layer

Use this order:

```text
1. Is API Server reachable?
2. Is authentication working?
3. Is authorization denying?
4. Is admission webhook blocking?
5. Is schema/version invalid?
6. Is etcd slow/unhealthy?
7. Are watches delayed?
8. Are clients throttled?
9. Is API discovery broken?
10. Are controllers reacting after write?
```

Commands:

```bash
kubectl cluster-info
kubectl get --raw /livez
kubectl get --raw /readyz?verbose
kubectl auth can-i create deployments -n prod
kubectl api-resources
kubectl get apiservice
kubectl get events -A --sort-by=.lastTimestamp
kubectl get --raw /metrics | grep apiserver_request
```

For object-level debugging:

```bash
kubectl get <resource> <name> -o yaml
kubectl describe <resource> <name>
kubectl get events -n <namespace>
```

Mindset:

```text
Separate API acceptance from controller reconciliation.
A successful write only means object stored.
It does not mean workload is already running.
```

---

# 41. Common API Server Metrics To Know

Useful metric families:

```text
apiserver_request_total
apiserver_request_duration_seconds
apiserver_current_inflight_requests
apiserver_storage_objects
apiserver_watch_events_total
apiserver_flowcontrol_rejected_requests_total
apiserver_admission_webhook_rejection_count
etcd_request_duration_seconds
```

Mental model:

```text
Latency can come from:
client -> API Server
API pipeline
admission webhook
etcd write/read
watch fan-out
```

ASCII:

```text
Request latency
   |
   +-- auth/authz
   +-- admission
   +-- validation
   +-- etcd
   +-- response serialization
```

Production symptoms mapping:

```text
High request duration        -> slow API operations
High inflight requests       -> saturation
Webhook rejection spike      -> policy blocking changes
Flowcontrol rejected/throttle -> APF pressure
etcd latency high            -> storage bottleneck
```

---

# 42. API Server Security Mindset

The API Server is security-critical.

Protect:

```text
authentication credentials
service account tokens
kubeconfig files
RBAC permissions
admission policies
network access to API endpoint
audit logs
etcd encryption at rest
```

High-risk permissions:

```text
create pods
exec into pods
read secrets
create rolebindings
impersonate users
update validatingwebhookconfigurations
update mutatingwebhookconfigurations
```

Why create pods is powerful:

```text
A user who can create Pods may mount service account tokens,
run privileged containers if policy allows,
or access internal network resources.
```

ASCII:

```text
API Server Access
      |
      v
Cluster Power
      |
      v
Production Risk
```

Security principle:

```text
Least privilege + admission guardrails + audit logs.
```

---

# 43. Audit Logging

Audit logs answer:

```text
Who did what, when, from where, and was it allowed?
```

Example questions:

```text
Who deleted the namespace?
Who changed the deployment image?
Who read the secret?
Which service account created this Pod?
Which webhook denied the request?
```

Audit pipeline:

```text
Request enters API Server
      |
      v
Audit event generated at stages
      |
      v
Audit backend / log sink
```

Stages can include:

```text
RequestReceived
ResponseStarted
ResponseComplete
Panic
```

Production lesson:

```text
Without audit logs, cluster incidents become guesswork.
```

Debugging story:

```text
A production Deployment scaled to zero.
kubectl history did not explain why.
Audit logs showed a CI service account patched replicas at 02:14.
```

---

# 44. High Availability API Server

Production clusters usually run multiple API Server replicas.

```text
          +------------------+
kubectl ->| Load Balancer    |
          +---+----------+---+
              |          |
              v          v
        API Server 1  API Server 2
              \          /
               \        /
                v      v
                  etcd
```

API Servers are mostly stateless.

They share etcd.

Why HA matters:

```text
one API Server can fail
clients continue through another
rolling upgrades possible
more read/watch capacity
```

But:

```text
If etcd quorum is unhealthy, multiple API Servers cannot save you.
```

Mental model:

```text
API Server replicas scale the gateway.
etcd quorum protects the memory.
```

---

# 45. What Happens If API Server Goes Down?

Existing Pods:

```text
continue running
continue serving traffic
```

But:

```text
kubectl cannot manage cluster
new deployments fail
scheduler cannot bind new Pods
controllers cannot observe/update state
kubelet status updates fail
HPA cannot update replicas
new Service endpoints may not update
```

ASCII:

```text
Data Plane:

Client -> Service -> Pod
        usually continues

Control Plane:

kubectl -> API Server X
controllers -> API Server X
scheduler -> API Server X
```

Important distinction:

```text
Kubernetes control plane failure is not the same as app process failure.
```

But long outage is dangerous because reconciliation stops.

---

# 46. Beginner Mistakes

```text
Mistake 1:
Thinking API Server runs Pods.
Correct:
Kubelet runs Pods. API Server stores and serves state.

Mistake 2:
Thinking etcd should be accessed directly.
Correct:
All normal clients use API Server.

Mistake 3:
Ignoring admission webhooks.
Correct:
They can mutate or block writes.

Mistake 4:
Giving cluster-admin to fix RBAC quickly.
Correct:
Understand verb/resource/namespace.

Mistake 5:
Polling list calls repeatedly.
Correct:
Use watch/informer pattern.

Mistake 6:
Thinking successful kubectl apply means app is running.
Correct:
It means desired state was accepted. Controllers and kubelets act afterward.

Mistake 7:
Ignoring resourceVersion conflicts.
Correct:
Handle optimistic concurrency.

Mistake 8:
Forgetting API discovery.
Correct:
kubectl and clients rely on discovery to understand resources.
```

---

# 47. Interview Answers

## What is the Kubernetes API Server?

The API Server is the central gateway for Kubernetes cluster state. All clients, controllers, scheduler, kubelets, and operators communicate through it. It authenticates and authorizes requests, runs admission control, validates objects, converts API versions, persists state to etcd, and serves list/watch APIs.

## Does API Server store data?

The API Server itself is mostly stateless. Persistent cluster state is stored in etcd. The API Server reads and writes etcd through its storage layer.

## What is the request flow for creating a Pod?

A create request reaches the API Server, then passes authentication, authorization, mutating admission, validation, validating admission, version conversion, and storage. After persistence in etcd, controllers/scheduler/kubelet observe changes through watch streams and act.

## What is admission control?

Admission control is a set of plugins or webhooks that run after authentication and authorization but before persistence. Mutating admission can modify objects. Validating admission can reject objects based on policy.

## What is the difference between RBAC and admission?

RBAC checks whether an identity can perform an action on a resource. Admission checks whether the object itself is acceptable according to cluster policy.

## What is resourceVersion?

resourceVersion is a metadata field representing the storage timeline version at which an object was observed. It is used for watches and optimistic concurrency.

## How do controllers watch changes?

Controllers usually list resources to get an initial snapshot, then watch from a resourceVersion. Informers maintain local caches and enqueue changed objects for reconciliation.

## What happens if API Server is down?

Existing workloads may continue running, but cluster management stops or degrades. kubectl, controllers, scheduler, kubelets, HPA, and operators cannot reliably read or update state.

## Why can a webhook break deployments?

Admission webhooks are in the API write path. If a webhook is unavailable and its failurePolicy is Fail, API Server rejects or times out requests that require that webhook.

## How do you debug Forbidden errors?

Read the error to identify user/service account, verb, resource, API group, and namespace. Then test using `kubectl auth can-i` and adjust Role/RoleBinding or ClusterRole/ClusterRoleBinding with least privilege.

---

# 48. Cheat Sheet

```text
API Server        = Kubernetes state gateway
etcd              = persistent cluster memory
AuthN             = who are you?
AuthZ             = are you allowed?
RBAC              = permission system
Admission         = mutate or validate object before storage
MutatingWebhook   = can change request object
ValidatingWebhook = can reject request object
Defaulting        = API fills missing standard values
Validation        = object must match schema and rules
apiVersion        = API contract version
Storage version   = version stored in etcd
resourceVersion   = etcd timeline marker
List + Watch      = efficient controller observation pattern
Informer          = watch + local cache + event handler
Watch cache       = API Server cache to reduce etcd load
APF               = API Priority and Fairness
CRD               = custom resource definition
APIService        = aggregated API extension
Status subresource= separate observed state updates
Finalizer         = cleanup hook before deletion
OwnerReference    = parent-child relationship for garbage collection
Audit log         = who did what in the cluster
```

Request path:

```text
Client
  |
  v
API Server
  |
  +-- Authentication
  +-- Authorization
  +-- Mutating Admission
  +-- Validation / Defaulting
  +-- Validating Admission
  +-- Version Conversion
  +-- etcd Storage
  +-- Watch Fan-out
```

---

# 49. One Picture To Remember

```text
                         CLIENTS
        kubectl | controllers | scheduler | kubelet | operators
                            |
                            v
                    +----------------+
                    |  API SERVER    |
                    +-------+--------+
                            |
        +-------------------+-------------------+
        |                   |                   |
        v                   v                   v
+---------------+   +---------------+   +---------------+
| Authentication|   | Authorization |   | Admission     |
| Who are you?  |   | Can you do it?|   | Is it safe?   |
+-------+-------+   +-------+-------+   +-------+-------+
        |                   |                   |
        +-------------------+-------------------+
                            |
                            v
                    +----------------+
                    | Validation     |
                    | Defaulting     |
                    | Conversion     |
                    +-------+--------+
                            |
                            v
                    +----------------+
                    |      etcd      |
                    | cluster memory |
                    +-------+--------+
                            |
                            v
                    +----------------+
                    | Watch Events   |
                    +-------+--------+
                            |
                            v
             controllers / scheduler / kubelet react
```

Rule:

```text
API Server does not run your application.
API Server decides whether cluster state may change,
persists that change,
and tells the rest of Kubernetes that reality should move.
```

---

# 50. Final Production Checklist

```text
[ ] I know every Kubernetes client goes through API Server.
[ ] I know API Server is mostly stateless and etcd stores persistent state.
[ ] I understand authn vs authz vs admission.
[ ] I can read Forbidden errors by identity, verb, resource, namespace.
[ ] I know mutating webhooks can change objects before storage.
[ ] I know validating webhooks can block deployments.
[ ] I understand defaulting and validation.
[ ] I understand apiVersion and storage conversion at a high level.
[ ] I know resourceVersion is used for watches and conflicts.
[ ] I understand list + watch + informer pattern.
[ ] I know why repeated full-list polling hurts API Server.
[ ] I understand status subresource.
[ ] I understand finalizers and deletionTimestamp.
[ ] I know existing Pods may run even if API Server is down.
[ ] I can debug API Server issues layer by layer.
[ ] I can explain API Server internals in interviews without memorizing.
```

---

# 51. Final Memory Hook

Do not memorize API Server as a single box.

Remember it as a request pipeline:

```text
Who are you?
Are you allowed?
Should this object be changed?
Is this object valid?
Which version is stored?
Can etcd persist it?
Who must be notified?
```

Final sentence:

```text
The Kubernetes API Server is the official gate where desired state becomes cluster truth.
```
