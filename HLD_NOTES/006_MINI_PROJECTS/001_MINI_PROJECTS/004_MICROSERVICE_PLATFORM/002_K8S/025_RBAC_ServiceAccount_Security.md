# 025_RBAC_ServiceAccount_Security.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why RBAC And ServiceAccount Exist

Most beginners think Kubernetes security means:

```text
Use HTTPS
Use Secrets
Do not run as root
```

Those are important, but they are not the full Kubernetes security model.

Kubernetes is an API-driven system.

Almost every meaningful action goes through the API Server:

```text
create Pod
read Secret
delete Deployment
watch logs
list Services
update ConfigMap
scale Deployment
exec into container
```

So the core security question becomes:

```text
Who is allowed to call which Kubernetes API operation?
```

That is why RBAC exists.

```text
RBAC = Role-Based Access Control
```

It answers:

```text
Subject  -> who?
Verb     -> can do what?
Resource -> on which Kubernetes object?
Scope    -> in which namespace or cluster-wide?
```

One picture:

```text
User / Pod / Controller
        |
        | request: list pods
        v
+----------------------+
| Kubernetes API Server|
+----------+-----------+
           |
           | authn + authz
           v
+----------------------+
| RBAC Rules           |
| allow or deny        |
+----------------------+
```

If you remember only one thing:

```text
Kubernetes security starts at the API Server.
RBAC decides what an identity can do.
ServiceAccount gives identity to Pods.
```

Do not memorize YAML first.

Understand the security conversation first.

---

# 2. The Wrong Way To Think About Kubernetes Security

Wrong mental model:

```text
My Pod is inside the cluster, so it can talk to Kubernetes safely.
```

This is dangerous.

A Pod running inside the cluster may be able to reach the API Server.
If that Pod has a powerful ServiceAccount token, an attacker who compromises the app may control Kubernetes objects.

Bad flow:

```text
Bug in Spring Boot app
        |
        v
Remote code execution / SSRF / leaked token
        |
        v
Attacker reads mounted ServiceAccount token
        |
        v
Attacker calls Kubernetes API
        |
        v
Reads Secrets / creates Pods / escalates
```

Kubernetes security is not only about external users.

It is also about workloads.

```text
Human user security  -> kubectl access
Pod security         -> ServiceAccount access
Controller security  -> controller permissions
CI/CD security       -> deployment automation permissions
```

Bad model:

```text
Cluster internal = trusted
```

Better model:

```text
Every identity must have the minimum permission needed.
```

This is called least privilege.

---

# 3. Real World Analogy: Office Access Cards

Imagine a company office.

People do not all get the same access card.

```text
Receptionist -> lobby + visitor desk
Developer    -> engineering floor
Finance      -> finance room
Admin        -> server room
Security     -> cameras + gates
```

If everyone gets master access, one lost card becomes a disaster.

Kubernetes is the same.

```text
Developer user       -> maybe read Pods in dev
CI/CD system         -> update Deployments in app namespace
Monitoring agent     -> read Pods and metrics
Spring Boot app      -> maybe read one ConfigMap
Cluster admin        -> manage everything
```

ASCII:

```text
Office Card Model

Person / System
      |
      v
Access Card
      |
      v
Allowed Rooms

Kubernetes Model

User / Pod / Controller
      |
      v
Identity
      |
      v
RBAC Rules
      |
      v
Allowed API actions
```

Do not give the Spring Boot app a master key if it only needs to read one ConfigMap.

---

# 4. Kubernetes Auth Flow: Authentication Then Authorization

When a request reaches the API Server, Kubernetes asks two big questions.

```text
1. Authentication: Who are you?
2. Authorization: Are you allowed to do this?
```

Authentication identifies the subject.

Authorization checks permissions.

Diagram:

```text
Request
  |
  v
+--------------------+
| API Server         |
+---------+----------+
          |
          v
+--------------------+
| Authentication     |
| Who are you?       |
+---------+----------+
          |
          v
+--------------------+
| Authorization      |
| Can you do this?   |
+---------+----------+
          |
          v
+--------------------+
| Admission Control  |
| Is object allowed? |
+---------+----------+
          |
          v
+--------------------+
| Store / Execute    |
+--------------------+
```

Example:

```text
Request:
GET /api/v1/namespaces/prod/secrets

Authentication:
subject = system:serviceaccount:prod:order-service-sa

Authorization:
Can this ServiceAccount list secrets in prod?

Result:
allow or deny
```

Important:

```text
Authentication proves identity.
RBAC decides permissions.
Admission policies validate the object or request behavior.
```

---

# 5. RBAC Vocabulary Without Memorization

RBAC has four main Kubernetes objects.

```text
Role
ClusterRole
RoleBinding
ClusterRoleBinding
```

Do not memorize names blindly.

Use this mental model:

```text
Role                = permissions inside one namespace
ClusterRole         = permissions reusable cluster-wide or for cluster resources
RoleBinding         = attach permissions to identity inside one namespace
ClusterRoleBinding  = attach cluster-wide permissions to identity
```

ASCII:

```text
Permissions Object          Binding Object

Role                  +     RoleBinding
ClusterRole           +     RoleBinding
ClusterRole           +     ClusterRoleBinding
```

Meaning:

```text
Permission says: what is allowed?
Binding says: who receives it?
```

Like this:

```text
Role / ClusterRole
  verbs: get, list
  resources: pods

RoleBinding / ClusterRoleBinding
  subject: serviceaccount/order-service-sa
```

One sentence:

```text
RBAC separates permission definition from permission assignment.
```

---

# 6. Subject, Verb, Resource, Scope

Every RBAC rule can be understood through this simple table.

```text
Who?        Subject
Can do?     Verb
On what?    Resource
Where?      Scope
```

Example:

```text
Who?
order-service ServiceAccount

Can do?
get, list

On what?
configmaps

Where?
orders namespace
```

ASCII:

```text
+--------------------+
| Subject            |
| order-service-sa   |
+---------+----------+
          |
          | allowed verbs
          v
+--------------------+
| get, list          |
+---------+----------+
          |
          | resources
          v
+--------------------+
| configmaps         |
+---------+----------+
          |
          | namespace scope
          v
+--------------------+
| orders namespace   |
+--------------------+
```

Common verbs:

```text
get       -> read one object
list      -> list many objects
watch     -> stream changes
create    -> create object
update    -> modify whole object
patch     -> modify part of object
delete    -> delete object
```

Dangerous verbs:

```text
*         -> all verbs
create pods
create deployments
get/list secrets
create rolebindings
impersonate
```

In production, avoid wildcard permissions unless you know exactly why.

---

# 7. ServiceAccount Mental Model

A ServiceAccount is an identity for a Pod.

Human users usually authenticate from outside the cluster.

Pods need an identity inside the cluster.

That identity is the ServiceAccount.

```text
Pod
 |
 | uses
 v
ServiceAccount
 |
 | receives permissions through RBAC binding
 v
Kubernetes API permissions
```

When a Pod runs, Kubernetes can mount a ServiceAccount token into the Pod.

The application or sidecar can use that token to call the API Server.

Diagram:

```text
+-----------------------------+
| Pod: order-service          |
|                             |
|  Container: Spring Boot     |
|                             |
|  Mounted token:             |
|  /var/run/secrets/...       |
+-------------+---------------+
              |
              | HTTPS + Bearer token
              v
+-----------------------------+
| Kubernetes API Server       |
+-----------------------------+
```

Important:

```text
ServiceAccount is not a user password.
It is a Kubernetes identity assigned to workloads.
```

If your Spring Boot app does not need Kubernetes API access, do not give it powerful permissions.

---

# 8. Default ServiceAccount Problem

Every namespace gets a default ServiceAccount.

If you create a Pod and do not specify a ServiceAccount, Kubernetes uses the default one.

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: order-service
spec:
  containers:
    - name: app
      image: order-service:1.0.0
```

Implicitly:

```text
serviceAccountName: default
```

This sounds harmless, but it can become dangerous if someone binds too many permissions to the default ServiceAccount.

Bad production pattern:

```text
Namespace default ServiceAccount
          |
          v
Powerful RoleBinding
          |
          v
Every Pod without explicit ServiceAccount inherits access
```

Better pattern:

```text
Each workload gets its own ServiceAccount.
Each ServiceAccount gets minimum permissions.
```

ASCII:

```text
Bad

Pod A ----\
Pod B -----+--> default ServiceAccount --> broad permissions
Pod C ----/

Good

order-service Pod   --> order-service-sa   --> read config only
payment-service Pod --> payment-service-sa --> no secret access
metrics-agent Pod   --> metrics-sa         --> list pods only
```

Rule:

```text
Never treat default ServiceAccount as a safe security boundary.
```

---

# 9. Role Example: Read ConfigMaps Only

Suppose your Spring Boot app needs to read ConfigMaps in the `orders` namespace.

You can create a Role:

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  namespace: orders
  name: configmap-reader
rules:
  - apiGroups: [""]
    resources: ["configmaps"]
    verbs: ["get", "list", "watch"]
```

Meaning:

```text
Inside namespace orders,
allow get/list/watch on ConfigMaps.
```

Diagram:

```text
Role: configmap-reader

Scope: orders namespace

Allows:
  get configmaps
  list configmaps
  watch configmaps

Does NOT allow:
  read secrets
  create pods
  delete deployments
```

Important:

```text
Role defines permissions.
It does not assign them to anyone yet.
```

You still need a binding.

---

# 10. ServiceAccount + RoleBinding Example

Create a ServiceAccount:

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: order-service-sa
  namespace: orders
```

Bind the Role to the ServiceAccount:

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: order-service-configmap-reader
  namespace: orders
subjects:
  - kind: ServiceAccount
    name: order-service-sa
    namespace: orders
roleRef:
  kind: Role
  name: configmap-reader
  apiGroup: rbac.authorization.k8s.io
```

Use it in Deployment:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
  namespace: orders
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
      serviceAccountName: order-service-sa
      containers:
        - name: order-service
          image: order-service:1.0.0
          ports:
            - containerPort: 8080
```

ASCII flow:

```text
Deployment
   |
   v
Pod template uses serviceAccountName: order-service-sa
   |
   v
Pod identity = system:serviceaccount:orders:order-service-sa
   |
   v
RoleBinding attaches configmap-reader Role
   |
   v
Pod can read ConfigMaps in orders namespace
```

This is least privilege.

---

# 11. ClusterRole Mental Model

A Role is namespaced.

But some resources are cluster-scoped.

Examples:

```text
nodes
persistentvolumes
namespaces
clusterroles
storageclasses
```

A namespaced Role cannot grant access to cluster-scoped resources.

That is why ClusterRole exists.

```text
ClusterRole = permission object that can describe cluster-level permissions
```

ClusterRole can also be reused inside namespaces through RoleBinding.

Two use cases:

```text
1. Cluster-wide access:
   ClusterRole + ClusterRoleBinding

2. Reusable namespaced permissions:
   ClusterRole + RoleBinding
```

ASCII:

```text
ClusterRole
   |
   +--> ClusterRoleBinding  -> applies cluster-wide
   |
   +--> RoleBinding         -> applies only in that namespace
```

Example:

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: pod-reader
rules:
  - apiGroups: [""]
    resources: ["pods"]
    verbs: ["get", "list", "watch"]
```

This defines a reusable pod-reader permission set.

---

# 12. RoleBinding To ClusterRole: Reuse Without Global Access

You can bind a ClusterRole using a RoleBinding.

This gives the permissions only inside the RoleBinding namespace.

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: read-pods-in-orders
  namespace: orders
subjects:
  - kind: ServiceAccount
    name: metrics-agent-sa
    namespace: monitoring
roleRef:
  kind: ClusterRole
  name: pod-reader
  apiGroup: rbac.authorization.k8s.io
```

Meaning:

```text
metrics-agent-sa can read pods,
but only in orders namespace.
```

Diagram:

```text
ClusterRole: pod-reader
  verbs: get/list/watch
  resources: pods

RoleBinding namespace: orders
  subject: monitoring/metrics-agent-sa

Effective permission:
  monitoring/metrics-agent-sa can read pods in orders only
```

This is common for monitoring agents that need controlled access.

Do not assume ClusterRole always means cluster-wide access.

The binding decides the scope.

---

# 13. ClusterRoleBinding: Powerful And Dangerous

ClusterRoleBinding grants permissions cluster-wide.

Example:

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: metrics-agent-cluster-reader
subjects:
  - kind: ServiceAccount
    name: metrics-agent-sa
    namespace: monitoring
roleRef:
  kind: ClusterRole
  name: pod-reader
  apiGroup: rbac.authorization.k8s.io
```

Meaning:

```text
metrics-agent-sa can read Pods in all namespaces.
```

ASCII:

```text
ServiceAccount: monitoring/metrics-agent-sa
        |
        v
ClusterRoleBinding
        |
        v
ClusterRole: pod-reader
        |
        v
All namespaces
  dev pods
  staging pods
  prod pods
```

This may be needed for cluster monitoring.

But avoid it for application workloads unless necessary.

Dangerous pattern:

```text
Application Pod
   |
   v
ClusterRoleBinding to cluster-admin
   |
   v
Full cluster compromise if app is hacked
```

Rule:

```text
Use ClusterRoleBinding only when the identity truly needs cluster-wide access.
```

---

# 14. Spring Boot App Calling Kubernetes API

Most Spring Boot services do not need to call the Kubernetes API.

They usually talk to:

```text
PostgreSQL
Redis
Kafka
Other services
External APIs
```

But some internal platform services may need Kubernetes API access.

Examples:

```text
custom deployment dashboard
internal operator
job launcher
tenant provisioning service
config watcher
service discovery helper
```

Java example using Kubernetes Java client style:

```java
package com.example.k8s;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.util.Config;

public class ConfigMapReader {

    public String readConfigMapValue(String namespace, String name, String key) throws Exception {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        V1ConfigMap configMap = api.readNamespacedConfigMap(name, namespace).execute();

        if (configMap.getData() == null) {
            return null;
        }

        return configMap.getData().get(key);
    }
}
```

This app only needs:

```text
get configmaps
```

Not:

```text
list secrets
create pods
delete deployments
```

Security model:

```text
Java code ability should match RBAC permission.
```

---

# 15. Spring Boot Security Mistake: Reading Secrets From Kubernetes API

Bad design:

```text
Spring Boot app calls Kubernetes API to list Secrets.
```

Why dangerous?

```text
If app is compromised, attacker can read secrets too.
```

Better options:

```text
1. Mount only required Secret as environment variable or file.
2. Use external secret manager.
3. Give app no permission to list all Secrets.
4. Avoid wildcard secret access.
```

Bad RBAC:

```yaml
rules:
  - apiGroups: [""]
    resources: ["secrets"]
    verbs: ["get", "list", "watch"]
```

Better:

```text
Prefer no API access to Secrets from application code.
If absolutely needed, restrict to specific resourceNames.
```

Example with resourceNames:

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  namespace: orders
  name: order-db-secret-reader
rules:
  - apiGroups: [""]
    resources: ["secrets"]
    resourceNames: ["order-db-secret"]
    verbs: ["get"]
```

Mental model:

```text
Do not give a warehouse key when the app needs one locker.
```

---

# 16. Dry Run: Pod Uses ServiceAccount To Read ConfigMap

Initial objects:

```text
Namespace: orders
ServiceAccount: order-service-sa
Role: configmap-reader
RoleBinding: order-service-configmap-reader
Deployment: order-service
```

Dry run:

```text
1. Deployment creates Pod.

2. Pod spec says:
   serviceAccountName: order-service-sa

3. Kubelet starts container.

4. ServiceAccount token is available to the Pod.

5. Spring Boot app uses Kubernetes client.

6. App calls:
   GET /api/v1/namespaces/orders/configmaps/app-config

7. API Server authenticates token.

8. Subject becomes:
   system:serviceaccount:orders:order-service-sa

9. API Server checks RBAC.

10. RoleBinding connects subject to Role.

11. Role allows get configmaps.

12. Request succeeds.
```

ASCII:

```text
Spring Boot App
      |
      | Bearer token
      v
API Server
      |
      v
Authentication
      |
      v
system:serviceaccount:orders:order-service-sa
      |
      v
RBAC check
      |
      v
RoleBinding -> Role -> get configmaps allowed
      |
      v
ConfigMap returned
```

This is the complete permission chain.

---

# 17. Dry Run: Same Pod Tries To Read Secret

Same Pod now tries:

```text
GET /api/v1/namespaces/orders/secrets/order-db-secret
```

RBAC check:

```text
Subject:
system:serviceaccount:orders:order-service-sa

Allowed:
get/list/watch configmaps

Requested:
get secrets
```

Result:

```text
Forbidden
```

Typical error:

```text
Error from server (Forbidden):
secrets "order-db-secret" is forbidden:
User "system:serviceaccount:orders:order-service-sa"
cannot get resource "secrets" in API group "" in namespace "orders"
```

This error is not random.

It tells you:

```text
who failed
what verb failed
what resource failed
which namespace failed
```

Debug mindset:

```text
Read Forbidden errors like a permission equation.
```

ASCII:

```text
Requested:
  subject  = order-service-sa
  verb     = get
  resource = secrets
  scope    = orders

RBAC has:
  get configmaps only

Decision:
  deny
```

---

# 18. kubectl auth can-i Mental Model

`kubectl auth can-i` is your RBAC truth checker.

Examples:

```bash
kubectl auth can-i get configmaps \
  --as=system:serviceaccount:orders:order-service-sa \
  -n orders
```

Expected:

```text
yes
```

Check Secret access:

```bash
kubectl auth can-i get secrets \
  --as=system:serviceaccount:orders:order-service-sa \
  -n orders
```

Expected:

```text
no
```

Check cluster-wide Pod list:

```bash
kubectl auth can-i list pods \
  --as=system:serviceaccount:monitoring:metrics-agent-sa \
  --all-namespaces
```

Mental model:

```text
can-i = simulate authorization decision
```

ASCII:

```text
Question:
Can subject X do verb Y on resource Z in namespace N?

kubectl auth can-i
        |
        v
API Server authorization check
        |
        v
yes / no
```

This command prevents guessing.

---

# 19. Production Story: App Gets Forbidden In Cluster

Scenario:

A Spring Boot config watcher works locally using kubeconfig.

After deployment to Kubernetes, logs show:

```text
403 Forbidden
User "system:serviceaccount:orders:default"
cannot watch resource "configmaps" in namespace "orders"
```

Symptoms:

```text
App starts
But config reload feature fails
No ConfigMap updates detected
Logs show Kubernetes API Forbidden
```

Root cause:

```text
Deployment forgot serviceAccountName.
Pod used default ServiceAccount.
RBAC binding was created for order-service-sa.
```

Broken Deployment:

```yaml
spec:
  template:
    spec:
      containers:
        - name: order-service
          image: order-service:1.0.0
```

Fixed:

```yaml
spec:
  template:
    spec:
      serviceAccountName: order-service-sa
      containers:
        - name: order-service
          image: order-service:1.0.0
```

Debug commands:

```bash
kubectl get pod order-service-abc -n orders -o yaml | grep serviceAccountName
kubectl auth can-i watch configmaps \
  --as=system:serviceaccount:orders:order-service-sa \
  -n orders
```

Memory hook:

```text
Correct Role is useless if the Pod uses the wrong ServiceAccount.
```

---

# 20. Production Story: Over-Permissive CI/CD Token

A CI/CD pipeline needs to deploy one app into one namespace.

Required actions:

```text
update deployment/order-service
create/update service/order-service
create/update configmap/order-config
read rollout status
```

Bad shortcut:

```text
Bind CI ServiceAccount to cluster-admin.
```

Why this is dangerous:

```text
If CI token leaks,
attacker can control entire cluster.
```

Better:

```text
Create namespace-scoped Role for deployment actions only.
Bind it to CI ServiceAccount.
```

ASCII:

```text
Bad

CI Token
   |
   v
cluster-admin
   |
   v
All namespaces + all resources

Good

CI Token
   |
   v
Role in orders namespace
   |
   v
Manage order-service deployment only
```

Example Role idea:

```yaml
rules:
  - apiGroups: ["apps"]
    resources: ["deployments"]
    verbs: ["get", "list", "watch", "patch", "update"]
  - apiGroups: [""]
    resources: ["services", "configmaps"]
    verbs: ["get", "list", "create", "update", "patch"]
```

Production lesson:

```text
CI/CD permissions must be treated like production root access.
```

---

# 21. Production Story: Monitoring Agent Needs Watch Permission

A monitoring agent lists Pods successfully once, but does not receive updates.

Logs:

```text
pods is forbidden: cannot watch resource "pods"
```

Role:

```yaml
verbs: ["get", "list"]
resources: ["pods"]
```

Problem:

```text
Informers and watchers need watch permission.
```

Fix:

```yaml
verbs: ["get", "list", "watch"]
```

Mental model:

```text
get   = read one object
list  = read current collection
watch = subscribe to future changes
```

ASCII:

```text
Without watch

Agent -> list pods -> snapshot only

With watch

Agent -> list pods -> current state
Agent -> watch pods -> future changes stream
```

This is common for:

```text
controllers
operators
monitoring agents
service discovery components
custom schedulers
```

Interview hook:

```text
Controllers usually need get/list/watch on watched resources.
```

---

# 22. Security Boundary: RBAC Is Not NetworkPolicy

RBAC controls Kubernetes API permissions.

It does not automatically control network traffic between Pods.

Example:

```text
RBAC denies Pod from reading Secrets via API.
```

But the same Pod may still connect to:

```text
Redis service
Postgres service
another Pod IP
external API
```

NetworkPolicy controls network traffic.

RBAC controls API actions.

Diagram:

```text
RBAC

Pod ---- Kubernetes API Server
         allow/deny API actions

NetworkPolicy

Pod ---- Pod / Service / External IP
         allow/deny network traffic
```

Do not confuse them.

Production security needs layers:

```text
RBAC
NetworkPolicy
Pod Security
Secret management
Image security
Runtime security
Audit logs
```

One sentence:

```text
RBAC protects the Kubernetes control plane API, not every network path in the cluster.
```

---

# 23. Security Boundary: RBAC Is Not Pod Security

RBAC says what Kubernetes API actions an identity can perform.

Pod Security controls what kind of Pod spec is allowed.

Example dangerous Pod:

```yaml
securityContext:
  privileged: true
```

Or:

```yaml
hostNetwork: true
hostPID: true
```

RBAC may allow a user to create Pods.

But admission policies may deny dangerous Pod specs.

Flow:

```text
Request: create privileged Pod
        |
        v
Authentication: who?
        |
        v
RBAC: can create pods?
        |
        v
Admission: is this Pod allowed?
        |
        v
allow or deny
```

ASCII:

```text
RBAC answers:
Can you create a Pod?

Pod Security answers:
Is this Pod safe enough to create?
```

Both matter.

Dangerous permission:

```text
create pods
```

Why?

Because a user who can create Pods may create a Pod that mounts sensitive volumes, uses powerful ServiceAccount, or runs attack tools if admission is weak.

---

# 24. Token Mounting And automountServiceAccountToken

By default, Pods may get a ServiceAccount token mounted.

If your application does not need Kubernetes API access, disable token mounting.

At ServiceAccount level:

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: order-service-sa
  namespace: orders
automountServiceAccountToken: false
```

At Pod level:

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: order-service
  namespace: orders
spec:
  serviceAccountName: order-service-sa
  automountServiceAccountToken: false
  containers:
    - name: app
      image: order-service:1.0.0
```

Mental model:

```text
No Kubernetes API need -> no token inside container.
```

ASCII:

```text
With token

Compromised App -> reads token -> calls API Server

Without token

Compromised App -> no in-cluster Kubernetes token to steal
```

This does not solve every security problem.

But it removes one useful attacker path.

Production checklist:

```text
[ ] Does this app call Kubernetes API?
[ ] If no, disable token automount.
[ ] If yes, grant minimum RBAC.
```

---

# 25. ResourceNames: Smaller Permission Surface

Sometimes an app needs access to one specific object.

Example:

```text
Read only ConfigMap app-config
```

Instead of allowing all ConfigMaps:

```yaml
resources: ["configmaps"]
verbs: ["get"]
```

Restrict to a specific name:

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  namespace: orders
  name: app-config-reader
rules:
  - apiGroups: [""]
    resources: ["configmaps"]
    resourceNames: ["app-config"]
    verbs: ["get"]
```

Meaning:

```text
Can get configmap/app-config only.
Cannot get other ConfigMaps.
Cannot list ConfigMaps.
```

Important:

```text
resourceNames usually works with get/update/patch/delete.
List/watch are collection operations and need care.
```

ASCII:

```text
All ConfigMaps allowed

app-config
feature-flags
internal-config
migration-config

Specific ConfigMap allowed

app-config only
```

Least privilege often means reducing both verbs and object names.

---

# 26. Dangerous Permissions To Recognize

Some RBAC permissions are especially sensitive.

```text
get/list/watch secrets
create pods
create deployments
update serviceaccounts
create rolebindings
create clusterrolebindings
bind
escalate
impersonate
* on *
```

Why `create pods` can be dangerous:

```text
A Pod can run arbitrary image.
A Pod may use a ServiceAccount.
A Pod may mount volumes.
A Pod may reach internal services.
```

Why `get secrets` is dangerous:

```text
Secrets may contain DB passwords, tokens, API keys.
```

Why `create rolebindings` is dangerous:

```text
User may bind powerful roles to themselves or others if not restricted.
```

ASCII:

```text
Small-looking permission
        |
        v
Possible escalation path
        |
        v
Cluster compromise
```

Rule:

```text
Do not review RBAC only by object count.
Review by blast radius.
```

---

# 27. Namespace Security Model

Namespaces help scope RBAC.

Example:

```text
orders namespace
payments namespace
monitoring namespace
```

A RoleBinding in `orders` does not grant access in `payments`.

```text
RoleBinding namespace = orders
        |
        v
Permission applies in orders only
```

Diagram:

```text
Cluster
  |
  +-- orders
  |     RoleBinding: order-service-sa -> configmap-reader
  |
  +-- payments
  |     no binding for order-service-sa
  |
  +-- monitoring
        metrics-agent-sa
```

But namespace is not complete isolation by itself.

You still need:

```text
NetworkPolicy
ResourceQuota
LimitRange
Pod Security Admission
separate ServiceAccounts
separate Secrets
careful RBAC
```

Memory hook:

```text
Namespace is a security scope helper, not a magical wall.
```

---

# 28. Debugging RBAC: Layer By Layer

When you see `Forbidden`, debug in this order.

```text
1. What is the exact subject?
2. What verb is being requested?
3. What resource is being requested?
4. Which namespace?
5. Which ServiceAccount does the Pod use?
6. Does Role or ClusterRole include the rule?
7. Does RoleBinding or ClusterRoleBinding bind it?
8. Is the binding in the right namespace?
9. Is the apiGroup correct?
10. Are resourceNames restricting access?
```

Commands:

```bash
kubectl get pod <pod> -n <ns> -o jsonpath='{.spec.serviceAccountName}'

kubectl get sa -n <ns>

kubectl get role,rolebinding -n <ns>

kubectl describe role <role> -n <ns>

kubectl describe rolebinding <binding> -n <ns>

kubectl auth can-i <verb> <resource> \
  --as=system:serviceaccount:<ns>:<sa> \
  -n <ns>
```

For cluster-wide checks:

```bash
kubectl get clusterrole,clusterrolebinding

kubectl auth can-i list pods \
  --as=system:serviceaccount:monitoring:metrics-agent-sa \
  --all-namespaces
```

Mindset:

```text
Forbidden is not a Kubernetes mystery.
It is a failed permission equation.
```

---

# 29. API Groups Mental Model

RBAC rules include `apiGroups`.

Core resources use empty string:

```yaml
apiGroups: [""]
resources: ["pods", "services", "configmaps", "secrets"]
```

Apps resources use:

```yaml
apiGroups: ["apps"]
resources: ["deployments", "replicasets", "statefulsets", "daemonsets"]
```

Batch resources use:

```yaml
apiGroups: ["batch"]
resources: ["jobs", "cronjobs"]
```

RBAC bug example:

```yaml
apiGroups: [""]
resources: ["deployments"]
verbs: ["get"]
```

This is wrong because Deployments are in `apps`.

Correct:

```yaml
apiGroups: ["apps"]
resources: ["deployments"]
verbs: ["get"]
```

ASCII:

```text
Kubernetes API
  |
  +-- core group: ""
  |     pods, services, secrets, configmaps
  |
  +-- apps
  |     deployments, replicasets, statefulsets
  |
  +-- batch
        jobs, cronjobs
```

Debug hook:

```text
Correct verb and resource can still fail if apiGroup is wrong.
```

---

# 30. Subresources: logs, exec, status, scale

Some Kubernetes actions are subresources.

Examples:

```text
pods/log
pods/exec
deployments/status
deployments/scale
```

Reading Pod logs is not exactly the same as reading Pods.

Example Role for logs:

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  namespace: orders
  name: pod-log-reader
rules:
  - apiGroups: [""]
    resources: ["pods", "pods/log"]
    verbs: ["get", "list"]
```

Exec permission is very sensitive:

```yaml
resources: ["pods/exec"]
verbs: ["create"]
```

Why `pods/exec` is dangerous:

```text
User can enter container shell.
Can inspect files.
May read mounted Secrets.
Can run commands inside app container.
```

ASCII:

```text
pods       -> read Pod object
pods/log   -> read container logs
pods/exec  -> open command session inside container
```

Production rule:

```text
Grant logs broadly only if needed.
Grant exec very carefully.
```

---

# 31. ServiceAccount Identity String

A ServiceAccount identity has a standard format:

```text
system:serviceaccount:<namespace>:<serviceaccount-name>
```

Example:

```text
system:serviceaccount:orders:order-service-sa
```

This appears in errors:

```text
User "system:serviceaccount:orders:order-service-sa"
cannot get resource "secrets" in namespace "orders"
```

Read it like this:

```text
system:serviceaccount  -> this is a ServiceAccount identity
orders                 -> namespace
order-service-sa       -> ServiceAccount name
```

ASCII:

```text
system:serviceaccount:orders:order-service-sa
          |             |          |
          |             |          +-- service account name
          |             +------------- namespace
          +--------------------------- identity type
```

Once you can read this string, RBAC errors become much easier.

---

# 32. Minimal Full YAML Example

This is a complete least-privilege example for a Spring Boot app that only reads one ConfigMap.

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: orders
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: order-service-sa
  namespace: orders
automountServiceAccountToken: true
---
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: app-config-reader
  namespace: orders
rules:
  - apiGroups: [""]
    resources: ["configmaps"]
    resourceNames: ["app-config"]
    verbs: ["get"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: order-service-app-config-reader
  namespace: orders
subjects:
  - kind: ServiceAccount
    name: order-service-sa
    namespace: orders
roleRef:
  kind: Role
  name: app-config-reader
  apiGroup: rbac.authorization.k8s.io
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
  namespace: orders
data:
  feature.checkout.enabled: "true"
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
  namespace: orders
spec:
  replicas: 2
  selector:
    matchLabels:
      app: order-service
  template:
    metadata:
      labels:
        app: order-service
    spec:
      serviceAccountName: order-service-sa
      containers:
        - name: order-service
          image: order-service:1.0.0
          ports:
            - containerPort: 8080
```

Picture:

```text
Deployment -> Pod -> ServiceAccount
                      |
                      v
                RoleBinding
                      |
                      v
                    Role
                      |
                      v
              get configmap/app-config
```

---

# 33. When To Use No API Permission

Many production apps should not call the Kubernetes API at all.

Examples:

```text
order-service
payment-service
user-service
inventory-service
notification-service
```

They usually only need runtime config and network access.

For these apps:

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: order-service-sa
  namespace: orders
automountServiceAccountToken: false
```

Deployment:

```yaml
spec:
  template:
    spec:
      serviceAccountName: order-service-sa
      automountServiceAccountToken: false
```

Mental model:

```text
Most business services need no Kubernetes API key inside the container.
```

ASCII:

```text
Spring Boot business service
   |
   +--> PostgreSQL
   +--> Redis
   +--> Kafka
   +--> other services

No need:
   X Kubernetes API Server
```

Security principle:

```text
No permission is better than small permission when permission is unnecessary.
```

---

# 34. Real World Platform Pattern

In a mature platform, permissions are separated by workload type.

```text
Business app ServiceAccount
  - no API access
  - token automount disabled

Config watcher
  - get/list/watch ConfigMaps
  - namespace scoped

Metrics collector
  - get/list/watch Pods
  - maybe cluster-wide read only

CI/CD deployer
  - update deployments/services/configmaps
  - namespace scoped

Operator
  - get/list/watch custom resources
  - create/update child resources
  - carefully scoped
```

ASCII:

```text
Cluster Identities

order-service-sa        -> no API access
payment-service-sa      -> no API access
config-watcher-sa       -> watch configmaps in one namespace
metrics-agent-sa        -> read pods across namespaces
ci-deployer-sa          -> deploy app in namespace
custom-operator-sa      -> manage specific CRDs
```

This is how you avoid one identity becoming the key to the entire cluster.

Production rule:

```text
Design ServiceAccounts like you design database users:
one identity per responsibility.
```

---

# 35. Common Beginner Mistakes

```text
Mistake 1:
Using default ServiceAccount for every workload.

Correct:
Create explicit ServiceAccount per workload.
```

```text
Mistake 2:
Binding default ServiceAccount to powerful Role.

Correct:
Never give broad permissions to default ServiceAccount.
```

```text
Mistake 3:
Using cluster-admin to fix Forbidden quickly.

Correct:
Add the minimum missing verb/resource/scope.
```

```text
Mistake 4:
Forgetting watch permission for controllers.

Correct:
Controllers/informers usually need get/list/watch.
```

```text
Mistake 5:
Wrong apiGroup.

Correct:
core resources use ""; deployments use apps.
```

```text
Mistake 6:
Confusing RBAC with NetworkPolicy.

Correct:
RBAC controls API actions. NetworkPolicy controls traffic.
```

```text
Mistake 7:
Leaving token mounted when app does not need API access.

Correct:
Set automountServiceAccountToken: false.
```

---

# 36. Interview Questions

## What is RBAC in Kubernetes?

RBAC stands for Role-Based Access Control. It controls which authenticated subjects can perform which actions on Kubernetes resources. RBAC rules are evaluated by the API Server during authorization.

## What is a ServiceAccount?

A ServiceAccount is a Kubernetes identity used by Pods and workloads. Pods can use a ServiceAccount token to authenticate to the Kubernetes API Server. RBAC bindings decide what that ServiceAccount is allowed to do.

## Difference between Role and ClusterRole?

A Role defines permissions inside one namespace. A ClusterRole defines permissions that can be used for cluster-scoped resources or reused across namespaces. A ClusterRole becomes cluster-wide only when attached using a ClusterRoleBinding.

## Difference between RoleBinding and ClusterRoleBinding?

A RoleBinding grants permissions inside a namespace. It can reference either a Role or ClusterRole. A ClusterRoleBinding grants ClusterRole permissions cluster-wide.

## Can a RoleBinding reference a ClusterRole?

Yes. A RoleBinding can reference a ClusterRole and restrict those permissions to the RoleBinding namespace. This is useful for reusing common permission sets without granting cluster-wide access.

## Why is cluster-admin dangerous for applications?

If an application using cluster-admin is compromised, the attacker may gain full control of the cluster. Business applications should usually have no Kubernetes API access or only narrow namespace-scoped permissions.

## Why is get/list secrets dangerous?

Secrets often contain database passwords, tokens, certificates, and API keys. If a workload can list Secrets and is compromised, attackers may steal credentials for many systems.

## What does kubectl auth can-i do?

It checks whether a subject can perform a particular action on a Kubernetes resource. It is useful for debugging RBAC authorization decisions.

## What does automountServiceAccountToken: false do?

It prevents Kubernetes from automatically mounting a ServiceAccount token into the Pod. This is useful for applications that do not need to call the Kubernetes API.

## Is RBAC enough for Kubernetes security?

No. RBAC controls API permissions. Production security also needs NetworkPolicy, Pod Security, Secret management, image scanning, runtime security, audit logs, and least-privilege workload design.

---

# 37. Debugging Cheat Sheet

```bash
# See ServiceAccount used by a Pod
kubectl get pod <pod> -n <namespace> -o jsonpath='{.spec.serviceAccountName}'

# List ServiceAccounts
kubectl get sa -n <namespace>

# List Roles and RoleBindings
kubectl get role,rolebinding -n <namespace>

# Describe Role
kubectl describe role <role-name> -n <namespace>

# Describe RoleBinding
kubectl describe rolebinding <binding-name> -n <namespace>

# List ClusterRoles and ClusterRoleBindings
kubectl get clusterrole,clusterrolebinding

# Check permission as a ServiceAccount
kubectl auth can-i get configmaps \
  --as=system:serviceaccount:<namespace>:<serviceaccount> \
  -n <namespace>

# Check all permissions for current user in namespace
kubectl auth can-i --list -n <namespace>

# Check cluster-wide permission
kubectl auth can-i list pods \
  --as=system:serviceaccount:monitoring:metrics-agent-sa \
  --all-namespaces
```

Forbidden error reading:

```text
User "system:serviceaccount:orders:order-service-sa"
cannot get resource "secrets"
in API group ""
in namespace "orders"
```

Translate:

```text
Subject  = orders/order-service-sa
Verb     = get
Resource = secrets
APIGroup = core
Scope    = orders namespace
Decision = denied
```

---

# 38. RBAC Design Checklist

```text
[ ] Does this workload need Kubernetes API access?
[ ] If no, disable automountServiceAccountToken.
[ ] If yes, create a dedicated ServiceAccount.
[ ] Avoid using default ServiceAccount.
[ ] Avoid cluster-admin.
[ ] Prefer RoleBinding over ClusterRoleBinding.
[ ] Prefer namespace-scoped access.
[ ] Use get/list/watch only when needed.
[ ] Be careful with secrets access.
[ ] Be careful with pods/exec access.
[ ] Be careful with create pods permission.
[ ] Check apiGroups carefully.
[ ] Use resourceNames when access to one object is enough.
[ ] Test with kubectl auth can-i.
[ ] Read Forbidden errors as subject + verb + resource + scope.
[ ] Review CI/CD tokens like production credentials.
```

---

# 39. One Picture To Remember

```text
                         Request
                            |
                            v
                  +-------------------+
                  | API Server        |
                  +---------+---------+
                            |
                            v
                  +-------------------+
                  | Authentication    |
                  | Who are you?      |
                  +---------+---------+
                            |
                            v
          system:serviceaccount:orders:order-service-sa
                            |
                            v
                  +-------------------+
                  | Authorization     |
                  | Are you allowed?  |
                  +---------+---------+
                            |
                            v
       +---------------------------------------------+
       | RBAC                                       |
       |                                             |
       | Role / ClusterRole = what permissions?      |
       | Binding            = who gets them?         |
       +---------------------+-----------------------+
                             |
                             v
                    allow or deny
```

Memory sentence:

```text
ServiceAccount gives identity to a Pod.
RBAC attaches permissions to that identity.
API Server enforces the decision.
```

---

# 40. Final Memory Hook

Do not memorize RBAC as YAML objects.

Remember it as a security equation:

```text
Can SUBJECT do VERB on RESOURCE in SCOPE?
```

Examples:

```text
Can order-service-sa get configmaps in orders? yes
Can order-service-sa get secrets in orders? no
Can metrics-agent-sa list pods in all namespaces? maybe
Can ci-deployer-sa update deployments in payments? only if bound
```

Final picture:

```text
Pod
 |
 v
ServiceAccount
 |
 v
RoleBinding / ClusterRoleBinding
 |
 v
Role / ClusterRole
 |
 v
Allowed Kubernetes API actions
```

Final sentence:

```text
Kubernetes RBAC is not about memorizing Role YAML.
It is about protecting the API Server with least-privilege identities.
```
