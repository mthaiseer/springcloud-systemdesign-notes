# 017_ConfigMap_Secret_Env.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why ConfigMap, Secret, and Env Exist

Most beginners first deploy a Spring Boot app like this:

```text
java -jar order-service.jar
```

Then configuration starts growing:

```text
server.port=8080
DB_HOST=postgres
DB_USER=order_user
DB_PASSWORD=secret
REDIS_HOST=redis
KAFKA_BOOTSTRAP_SERVERS=kafka:9092
PAYMENT_TIMEOUT_MS=3000
FEATURE_NEW_CHECKOUT=true
```

Bad beginner model:

```text
Put everything inside application.properties.
Build image.
Deploy image.
```

This creates a problem.

If one value changes, you rebuild the image.

```text
Change DB host
   |
   v
Rebuild image
   |
   v
Push image
   |
   v
Redeploy
```

That is not a clean production model.

Correct model:

```text
Application Code  = stable
Container Image   = stable package
Configuration     = environment-specific
Secrets           = sensitive values
```

One picture:

```text
Same image
   |
   +--> dev config
   +--> staging config
   +--> prod config
```

Kubernetes gives three main tools:

```text
ConfigMap = non-sensitive configuration
Secret    = sensitive configuration
Env       = how values enter the running container
```

Do not memorize:

```text
ConfigMap is key-value.
Secret is encoded key-value.
```

Understand the production problem:

```text
How can the same app image run safely in different environments without rebuilding?
```

---

# 2. The Wrong Way To Think About Configuration

Wrong model:

```text
Configuration belongs inside code.
```

Example:

```java
String dbUrl = "jdbc:postgresql://prod-db:5432/orders";
String password = "ProdPassword123";
```

This is dangerous.

Problems:

```text
1. Code becomes environment-specific.
2. Developers may see production passwords.
3. Image must be rebuilt for config changes.
4. Rollback becomes confusing.
5. Secrets may leak to GitHub.
6. Local/dev/staging/prod become inconsistent.
```

Another wrong model:

```text
One Docker image per environment.
```

```text
order-service:dev
order-service:staging
order-service:prod
```

This looks simple but breaks the artifact principle.

Better:

```text
Build once, configure at runtime.
```

ASCII:

```text
BAD MODEL

Source Code + Config + Secrets
          |
          v
    Docker Image
          |
          v
   Environment-specific image


GOOD MODEL

Source Code
    |
    v
Docker Image
    |
    +-- runtime config from ConfigMap
    |
    +-- runtime secret from Secret
```

The image should answer:

```text
What application is this?
```

Config should answer:

```text
How should this application behave here?
```

Secret should answer:

```text
What sensitive credential should it use here?
```

---

# 3. Real World Analogy: Same Car, Different Drivers

Think of a container image as a car.

```text
Car = application package
Driver settings = configuration
Private key = secret
```

The same car can be driven in different cities.

```text
Same Car
  |
  +--> Driver A sets seat height, mirrors, route
  +--> Driver B sets seat height, mirrors, route
```

You do not manufacture a new car just because the mirror position changes.

Similarly:

```text
Same Spring Boot image
  |
  +--> dev DB, dev Redis, debug logging
  +--> staging DB, staging Redis, info logging
  +--> prod DB, prod Redis, warning logging
```

Diagram:

```text
                 SAME IMAGE
              order-service:1.0
                    |
     +--------------+--------------+
     |              |              |
     v              v              v
   DEV           STAGING          PROD
 ConfigMap       ConfigMap       ConfigMap
 Secret          Secret          Secret
```

The image is the car.

ConfigMap is the driving setup.

Secret is the private key.

Environment variables are the dashboard wires that pass values into the running engine.

---

# 4. Real World Analogy: Restaurant Menu vs Safe Locker

Imagine a restaurant.

Public operating instructions:

```text
Opening time = 9 AM
Closing time = 10 PM
Delivery radius = 5 km
Max table booking = 8 people
```

These are not secret.

They can be stored in a normal instruction board.

```text
Instruction Board = ConfigMap
```

Private sensitive items:

```text
Bank PIN
Supplier portal password
Safe locker code
Payment terminal key
```

These should not be on the wall.

```text
Safe Locker = Secret
```

Kubernetes model:

```text
ConfigMap:
  LOG_LEVEL=INFO
  PAYMENT_TIMEOUT_MS=3000
  FEATURE_NEW_CHECKOUT=true

Secret:
  DB_PASSWORD=...
  JWT_SIGNING_KEY=...
  STRIPE_API_KEY=...
```

ASCII:

```text
+---------------------------+
| ConfigMap                 |
| Non-sensitive settings    |
| safe to expose to app team|
+---------------------------+

+---------------------------+
| Secret                    |
| Sensitive values          |
| must be protected         |
+---------------------------+
```

Important:

```text
Secret does not mean magically invisible.
Secret means Kubernetes treats it as sensitive object type.
You still need RBAC, encryption at rest, and careful access control.
```

---

# 5. Core Mental Model

The core model:

```text
ConfigMap / Secret live in Kubernetes API
              |
              v
Pod spec references them
              |
              v
Kubelet injects values into container
              |
              v
Spring Boot reads them at startup/runtime
```

Diagram:

```text
Developer
   |
   | kubectl apply configmap.yaml
   v
API Server
   |
   v
etcd stores ConfigMap / Secret
   |
   v
Pod references them
   |
   v
Kubelet prepares container environment
   |
   v
Container starts
   |
   v
Spring Boot reads environment variables
```

A ConfigMap does not automatically enter every Pod.

A Secret does not automatically enter every Pod.

You must connect them.

```text
ConfigMap exists
Pod does not reference it
Result: app cannot see it
```

Think in two objects:

```text
1. Data object:
   ConfigMap / Secret stores values.

2. Consumer object:
   Pod / Deployment tells Kubernetes how to inject those values.
```

ASCII:

```text
ConfigMap
  key: LOG_LEVEL=INFO
       |
       | referenced by
       v
Deployment Pod template
       |
       v
Container env
       |
       v
Spring Boot property
```

---

# 6. ConfigMap Mental Model

ConfigMap is for non-sensitive configuration.

Example:

```text
LOG_LEVEL=INFO
APP_MODE=production
PAYMENT_TIMEOUT_MS=3000
FEATURE_CHECKOUT_V2=true
```

YAML:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: order-service-config
data:
  LOG_LEVEL: "INFO"
  APP_MODE: "production"
  PAYMENT_TIMEOUT_MS: "3000"
  FEATURE_CHECKOUT_V2: "true"
```

Mental model:

```text
ConfigMap = named bag of plain configuration values
```

It lives in the namespace.

```text
namespace: prod
configmap: order-service-config
```

A Pod in another namespace cannot automatically use it by simple local name.

Diagram:

```text
Namespace: prod

+-------------------------------+
| ConfigMap                     |
| order-service-config          |
|                               |
| LOG_LEVEL=INFO                |
| PAYMENT_TIMEOUT_MS=3000       |
+-------------------------------+
```

ConfigMap values are strings.

Even numbers and booleans are usually passed as strings:

```yaml
PAYMENT_TIMEOUT_MS: "3000"
FEATURE_CHECKOUT_V2: "true"
```

Spring Boot can bind them into typed fields.

Kubernetes stores them as text data.

---

# 7. Secret Mental Model

Secret is for sensitive values.

Examples:

```text
DB_PASSWORD
JWT_SIGNING_KEY
OAUTH_CLIENT_SECRET
PAYMENT_PROVIDER_KEY
```

YAML using `stringData`:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: order-service-secret
type: Opaque
stringData:
  DB_USERNAME: order_user
  DB_PASSWORD: very-strong-password
  JWT_SECRET: super-secret-signing-key
```

`stringData` is easier for humans.

Kubernetes converts it internally to base64 encoded `data`.

Important:

```text
Base64 encoding is not encryption.
```

Do not think:

```text
Secret = encrypted password by default in every cluster
```

Correct thinking:

```text
Secret = Kubernetes object intended for sensitive data.
Cluster security must protect it.
```

ASCII:

```text
Plain secret value
      |
      v
Kubernetes Secret object
      |
      v
etcd storage
      |
      v
Mounted/injected only into Pods that reference it
```

Security layers:

```text
RBAC:
  who can read Secret?

etcd encryption:
  is Secret encrypted at rest?

Namespace design:
  which apps can reference it?

Pod security:
  can attackers exec into container?

Logging discipline:
  are secrets printed accidentally?
```

---

# 8. Env Mental Model

Environment variables are one way to pass ConfigMap and Secret values into a container.

Container receives values like:

```text
LOG_LEVEL=INFO
DB_HOST=postgres
DB_PASSWORD=...
```

Spring Boot reads them using property binding.

Example:

```yaml
env:
  - name: LOG_LEVEL
    valueFrom:
      configMapKeyRef:
        name: order-service-config
        key: LOG_LEVEL
```

Secret example:

```yaml
env:
  - name: DB_PASSWORD
    valueFrom:
      secretKeyRef:
        name: order-service-secret
        key: DB_PASSWORD
```

Diagram:

```text
ConfigMap key
   |
   v
Container env var
   |
   v
Spring Boot Environment
   |
   v
@ConfigurationProperties / @Value
```

Environment variables are simple and common.

But they have limitations:

```text
1. Usually loaded when container starts.
2. Changing ConfigMap does not automatically restart Pod.
3. Env vars may appear in process environments.
4. Large structured configs are awkward.
```

Good use:

```text
Small values
Feature flags
URLs
Timeouts
Credentials passed at startup
```

Less ideal:

```text
Large config files
Certificates
Many nested properties
Dynamic config reload without restart
```

---

# 9. Three Ways To Consume ConfigMap and Secret

Kubernetes gives common consumption methods:

```text
1. Single env var
2. envFrom all keys
3. Mounted files as volume
```

ASCII:

```text
ConfigMap / Secret
       |
       +--> env: one key -> one env var
       |
       +--> envFrom: all keys -> env vars
       |
       +--> volume: keys become files
```

## 1. Single Key

```yaml
env:
  - name: PAYMENT_TIMEOUT_MS
    valueFrom:
      configMapKeyRef:
        name: order-service-config
        key: PAYMENT_TIMEOUT_MS
```

Use when you want explicit mapping.

## 2. envFrom

```yaml
envFrom:
  - configMapRef:
      name: order-service-config
  - secretRef:
      name: order-service-secret
```

Use when many keys should become env vars.

Risk:

```text
A bad key name or accidental key becomes visible to app.
```

## 3. Volume Mount

```yaml
volumes:
  - name: app-config
    configMap:
      name: order-service-config

containers:
  - name: order-service
    volumeMounts:
      - name: app-config
        mountPath: /config
```

Result:

```text
/config/LOG_LEVEL
/config/PAYMENT_TIMEOUT_MS
```

Volume mount is useful for files:

```text
application.yml
certificates
truststore config
nginx config
feature rules
```

---

# 10. Full Spring Boot Example

Suppose your Spring Boot app needs:

```text
server.port
database URL
database username
database password
timeout
feature flag
```

Java config class:

```java
package com.example.order.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "order")
public class OrderProperties {
    private int paymentTimeoutMs;
    private boolean checkoutV2Enabled;

    public int getPaymentTimeoutMs() {
        return paymentTimeoutMs;
    }

    public void setPaymentTimeoutMs(int paymentTimeoutMs) {
        this.paymentTimeoutMs = paymentTimeoutMs;
    }

    public boolean isCheckoutV2Enabled() {
        return checkoutV2Enabled;
    }

    public void setCheckoutV2Enabled(boolean checkoutV2Enabled) {
        this.checkoutV2Enabled = checkoutV2Enabled;
    }
}
```

Enable properties:

```java
package com.example.order;

import com.example.order.config.OrderProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(OrderProperties.class)
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

Service using config:

```java
package com.example.order.service;

import com.example.order.config.OrderProperties;
import org.springframework.stereotype.Service;

@Service
public class PaymentClient {
    private final OrderProperties properties;

    public PaymentClient(OrderProperties properties) {
        this.properties = properties;
    }

    public void callPaymentProvider() {
        int timeout = properties.getPaymentTimeoutMs();

        if (properties.isCheckoutV2Enabled()) {
            System.out.println("Using checkout V2 with timeout " + timeout);
        } else {
            System.out.println("Using checkout V1 with timeout " + timeout);
        }
    }
}
```

Spring Boot property mapping from env:

```text
ORDER_PAYMENT_TIMEOUT_MS -> order.payment-timeout-ms
ORDER_CHECKOUT_V2_ENABLED -> order.checkout-v2-enabled
SPRING_DATASOURCE_URL -> spring.datasource.url
```

This is relaxed binding.

---

# 11. ConfigMap YAML For Spring Boot

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: order-service-config
data:
  SERVER_PORT: "8080"
  SPRING_PROFILES_ACTIVE: "prod"
  LOGGING_LEVEL_ROOT: "INFO"

  SPRING_DATASOURCE_URL: "jdbc:postgresql://postgres.orders.svc.cluster.local:5432/orders"
  SPRING_REDIS_HOST: "redis.orders.svc.cluster.local"
  SPRING_KAFKA_BOOTSTRAP_SERVERS: "kafka.orders.svc.cluster.local:9092"

  ORDER_PAYMENT_TIMEOUT_MS: "3000"
  ORDER_CHECKOUT_V2_ENABLED: "true"
```

Diagram:

```text
ConfigMap key
SPRING_DATASOURCE_URL
      |
      v
Env variable
SPRING_DATASOURCE_URL
      |
      v
Spring property
spring.datasource.url
      |
      v
Hikari / JDBC connection
```

Why this is powerful:

```text
The same Docker image can connect to:
dev-postgres
staging-postgres
prod-postgres
```

by only changing ConfigMap.

Not code.

Not image.

---

# 12. Secret YAML For Spring Boot

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: order-service-secret
type: Opaque
stringData:
  SPRING_DATASOURCE_USERNAME: "order_user"
  SPRING_DATASOURCE_PASSWORD: "change-me-in-real-prod"
  JWT_SIGNING_KEY: "very-long-random-signing-key"
```

Secret injected as env:

```yaml
envFrom:
  - secretRef:
      name: order-service-secret
```

Spring Boot receives:

```text
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
JWT_SIGNING_KEY
```

Important:

```text
Never print environment variables during startup.
Never expose /actuator/env publicly.
Never log full datasource URL if it contains passwords.
```

Bad code:

```java
System.out.println(System.getenv());
```

Production danger:

```text
All env vars may appear in logs.
Secrets leak to log aggregation.
Many people can read logs.
```

Good mindset:

```text
A secret is only secret if every path that touches it protects it.
```

---

# 13. Deployment Using ConfigMap and Secret

Complete Deployment:

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
          image: registry.example.com/order-service:1.0.0
          ports:
            - containerPort: 8080

          envFrom:
            - configMapRef:
                name: order-service-config
            - secretRef:
                name: order-service-secret

          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 20
            periodSeconds: 10

          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 40
            periodSeconds: 20
```

Flow:

```text
Deployment
   |
   v
Pod template references ConfigMap + Secret
   |
   v
ReplicaSet creates Pods
   |
   v
Kubelet resolves ConfigMap + Secret
   |
   v
Container starts with env vars
   |
   v
Spring Boot binds properties
```

ASCII:

```text
+------------------+       +------------------+
| ConfigMap        |       | Secret           |
| non-sensitive    |       | sensitive        |
+---------+--------+       +---------+--------+
          |                          |
          +------------+-------------+
                       |
                       v
              +----------------+
              | Pod Container  |
              | env variables |
              +-------+--------+
                      |
                      v
              +----------------+
              | Spring Boot    |
              +----------------+
```

---

# 14. Dry Run: Pod Startup With Env Injection

You apply:

```bash
kubectl apply -f configmap.yaml
kubectl apply -f secret.yaml
kubectl apply -f deployment.yaml
```

Dry run:

```text
1. API Server stores ConfigMap object.

2. API Server stores Secret object.

3. API Server stores Deployment object.

4. Deployment Controller creates ReplicaSet.

5. ReplicaSet creates Pod objects.

6. Scheduler assigns Pod to Node.

7. Kubelet on the Node sees the Pod spec.

8. Kubelet notices envFrom references:
   - order-service-config
   - order-service-secret

9. Kubelet fetches referenced values from API Server.

10. Kubelet prepares container environment.

11. Container runtime starts container.

12. Java process starts.

13. Spring Boot reads environment variables.

14. Spring Boot converts:
    SPRING_DATASOURCE_URL -> spring.datasource.url

15. Hikari tries DB connection.

16. Actuator readiness becomes UP.

17. Service can route traffic to Pod.
```

Picture:

```text
ConfigMap + Secret
      |
      v
Kubelet builds env
      |
      v
Container starts
      |
      v
Spring Boot binds properties
      |
      v
App becomes Ready
```

This is the real story.

---

# 15. What Happens If ConfigMap Is Missing?

Deployment references:

```yaml
envFrom:
  - configMapRef:
      name: order-service-config
```

But ConfigMap does not exist.

Possible result:

```text
Pod cannot start properly.
Kubelet reports config not found.
```

Debug:

```bash
kubectl describe pod <pod-name>
```

You may see event:

```text
Error: configmap "order-service-config" not found
```

Mental model:

```text
Pod spec says: give me this config.
Kubelet says: I cannot find it.
Container cannot be prepared correctly.
```

ASCII:

```text
Pod wants ConfigMap
       |
       v
API Server lookup
       |
       v
Not found
       |
       v
Pod startup blocked / failed
```

Fix:

```bash
kubectl get configmap
kubectl apply -f configmap.yaml
kubectl rollout restart deployment order-service
```

Important:

```text
Create config before creating Pods that depend on it.
```

---

# 16. What Happens If Secret Key Is Missing?

Secret exists:

```yaml
stringData:
  SPRING_DATASOURCE_USERNAME: order_user
```

Deployment expects:

```yaml
env:
  - name: SPRING_DATASOURCE_PASSWORD
    valueFrom:
      secretKeyRef:
        name: order-service-secret
        key: SPRING_DATASOURCE_PASSWORD
```

But key missing.

Symptoms:

```text
Pod may fail to start if key is required.
Spring Boot may start and fail when connecting to DB.
```

Debug:

```bash
kubectl describe pod <pod-name>
kubectl get secret order-service-secret -o yaml
```

Be careful:

```text
Do not casually print secrets in shared terminal recordings or screenshots.
```

Better verification:

```bash
kubectl get secret order-service-secret -o jsonpath='{.data.SPRING_DATASOURCE_PASSWORD}'
```

Then only confirm presence, not value.

Mental model:

```text
Secret object exists does not mean every expected key exists.
```

Production checklist:

```text
[ ] Secret name correct?
[ ] Secret namespace correct?
[ ] Secret key correct?
[ ] Deployment references correct key?
[ ] App property name correct?
```

---

# 17. ConfigMap Update Mental Model

Very common misconception:

```text
I changed ConfigMap. My running app immediately got new env value.
```

For env vars, usually false.

Environment variables are injected when container starts.

```text
Container start
   |
   v
Env values fixed for that process
```

If ConfigMap changes later:

```text
Existing Java process does not magically change env values.
```

Diagram:

```text
Time T1:
ConfigMap LOG_LEVEL=INFO
Pod starts with LOG_LEVEL=INFO

Time T2:
ConfigMap LOG_LEVEL=DEBUG
Running Pod still has LOG_LEVEL=INFO

Time T3:
Pod restarted
New Pod has LOG_LEVEL=DEBUG
```

Fix:

```bash
kubectl rollout restart deployment order-service
```

or use checksum annotation pattern with Helm/Kustomize.

Mental model:

```text
ConfigMap update changes stored config.
Pod restart makes app consume it.
```

Exception:

```text
Mounted ConfigMap volumes can update files eventually.
But your app must watch/reload files.
Env vars do not update inside already running process.
```

---

# 18. Mounted ConfigMap File Example

Sometimes file style is better than env variables.

Example `application.yml` inside ConfigMap:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: order-service-file-config
data:
  application.yml: |
    server:
      port: 8080

    spring:
      datasource:
        url: jdbc:postgresql://postgres.orders.svc.cluster.local:5432/orders

    order:
      payment-timeout-ms: 3000
      checkout-v2-enabled: true
```

Mount it:

```yaml
volumes:
  - name: spring-config
    configMap:
      name: order-service-file-config

containers:
  - name: order-service
    image: registry.example.com/order-service:1.0.0
    volumeMounts:
      - name: spring-config
        mountPath: /config
        readOnly: true
```

Spring Boot automatically checks `/config` in many common setups when launched from standard locations, or you can explicitly set:

```yaml
env:
  - name: SPRING_CONFIG_ADDITIONAL_LOCATION
    value: "file:/config/"
```

ASCII:

```text
ConfigMap data:
application.yml
      |
      v
Mounted file:
/config/application.yml
      |
      v
Spring Boot reads config file
```

Use mounted files for:

```text
Large nested YAML
Nginx config
Rules files
Trust config
App configuration files
```

---

# 19. Mounted Secret File Example

Secrets can also be mounted as files.

Example:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: payment-cert-secret
type: Opaque
stringData:
  client.crt: |
    -----BEGIN CERTIFICATE-----
    example-certificate-content
    -----END CERTIFICATE-----
  client.key: |
    -----BEGIN PRIVATE KEY-----
    example-private-key-content
    -----END PRIVATE KEY-----
```

Mount:

```yaml
volumes:
  - name: payment-certs
    secret:
      secretName: payment-cert-secret

containers:
  - name: order-service
    volumeMounts:
      - name: payment-certs
        mountPath: /etc/payment/certs
        readOnly: true
```

Result:

```text
/etc/payment/certs/client.crt
/etc/payment/certs/client.key
```

Diagram:

```text
Secret
  client.crt
  client.key
      |
      v
Volume mount
      |
      v
Container filesystem
      |
      v
Java TLS client uses file path
```

Spring Boot app property:

```yaml
payment:
  tls:
    cert-path: /etc/payment/certs/client.crt
    key-path: /etc/payment/certs/client.key
```

This is often better than env vars for certificates.

---

# 20. Complete Mini Production YAML

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: orders
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: order-service-config
  namespace: orders
data:
  SERVER_PORT: "8080"
  SPRING_PROFILES_ACTIVE: "prod"
  LOGGING_LEVEL_ROOT: "INFO"
  SPRING_DATASOURCE_URL: "jdbc:postgresql://postgres.orders.svc.cluster.local:5432/orders"
  SPRING_REDIS_HOST: "redis.orders.svc.cluster.local"
  ORDER_PAYMENT_TIMEOUT_MS: "3000"
  ORDER_CHECKOUT_V2_ENABLED: "true"
---
apiVersion: v1
kind: Secret
metadata:
  name: order-service-secret
  namespace: orders
type: Opaque
stringData:
  SPRING_DATASOURCE_USERNAME: "order_user"
  SPRING_DATASOURCE_PASSWORD: "replace-with-real-secret-manager-value"
  JWT_SIGNING_KEY: "replace-with-long-random-key"
---
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
      containers:
        - name: order-service
          image: registry.example.com/order-service:1.0.0
          ports:
            - containerPort: 8080
          envFrom:
            - configMapRef:
                name: order-service-config
            - secretRef:
                name: order-service-secret
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 20
            periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: order-service
  namespace: orders
spec:
  selector:
    app: order-service
  ports:
    - port: 80
      targetPort: 8080
```

One picture:

```text
Namespace orders
   |
   +-- ConfigMap: order-service-config
   |
   +-- Secret: order-service-secret
   |
   +-- Deployment: order-service
   |       |
   |       v
   |     Pods consume config + secret
   |
   +-- Service: stable access
```

---

# 21. Spring Boot Controller Example To Verify Config

Never expose secrets.

But for learning, expose safe config only.

```java
package com.example.order.web;

import com.example.order.config.OrderProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ConfigDebugController {

    private final OrderProperties properties;

    public ConfigDebugController(OrderProperties properties) {
        this.properties = properties;
    }

    @GetMapping("/internal/config-summary")
    public Map<String, Object> configSummary() {
        return Map.of(
                "paymentTimeoutMs", properties.getPaymentTimeoutMs(),
                "checkoutV2Enabled", properties.isCheckoutV2Enabled()
        );
    }
}
```

This endpoint should be protected or disabled in production.

Good endpoint:

```text
Shows safe non-sensitive config summary.
```

Bad endpoint:

```text
Prints DB password, JWT key, full env map.
```

ASCII:

```text
ConfigMap value
      |
      v
Spring property
      |
      v
Safe debug endpoint
      |
      v
Operator verifies behavior
```

Rule:

```text
Debug configuration behavior, not secret values.
```

---

# 22. Production Story: Wrong Namespace

You create ConfigMap:

```bash
kubectl create configmap order-service-config -n default
```

Deployment runs in:

```text
namespace: orders
```

Pod references:

```yaml
configMapRef:
  name: order-service-config
```

Kubernetes looks inside the Pod namespace:

```text
orders/order-service-config
```

But ConfigMap exists in:

```text
default/order-service-config
```

Symptoms:

```text
Pod fails to start
Event says ConfigMap not found
```

Debug:

```bash
kubectl get configmap -A | grep order-service-config
kubectl describe pod -n orders <pod-name>
```

Diagram:

```text
default namespace
  ConfigMap exists

orders namespace
  Pod looks here
  ConfigMap missing
```

Fix:

```bash
kubectl apply -n orders -f configmap.yaml
```

Mental model:

```text
ConfigMaps and Secrets are namespace-scoped.
Pod consumes them from its own namespace.
```

---

# 23. Production Story: App Starts But DB Fails

Pod status:

```text
Running
```

Readiness:

```text
0/1
```

Logs:

```text
FATAL: password authentication failed for user "order_user"
```

Common causes:

```text
Wrong Secret value
Wrong Secret key mapped
Secret updated but Pod not restarted
Database password rotated but app still uses old env
```

Debug flow:

```bash
kubectl describe pod -n orders <pod-name>
kubectl logs -n orders <pod-name>
kubectl get secret -n orders order-service-secret -o yaml
kubectl rollout restart deployment/order-service -n orders
```

Do not print decoded password in public channels.

ASCII:

```text
Secret updated
    |
    v
Running Pod still old env
    |
    v
DB auth failure continues
    |
    v
Restart Pod
    |
    v
New env picked up
```

Lesson:

```text
When using env vars, secret rotation usually requires Pod restart.
```

---

# 24. Production Story: Feature Flag Change Not Taking Effect

ConfigMap:

```text
ORDER_CHECKOUT_V2_ENABLED=false
```

You change:

```text
ORDER_CHECKOUT_V2_ENABLED=true
```

But app still behaves old way.

Why?

```text
The running Java process has old environment value.
```

Debug:

```bash
kubectl get configmap order-service-config -n orders -o yaml
kubectl exec -n orders <pod-name> -- printenv ORDER_CHECKOUT_V2_ENABLED
```

You may see:

```text
ConfigMap: true
Pod env:   false
```

Fix:

```bash
kubectl rollout restart deployment/order-service -n orders
```

Better production approach:

```text
Use a dedicated dynamic config system if truly runtime change is required.
Examples:
- Spring Cloud Config
- Consul
- LaunchDarkly
- custom DB-backed feature flags
```

Mental model:

```text
Kubernetes ConfigMap is not automatically a dynamic feature flag engine.
```

---

# 25. Production Story: Secret Leaked Through Logs

Bad startup code:

```java
@PostConstruct
void printEnv() {
    System.getenv().forEach((k, v) -> System.out.println(k + "=" + v));
}
```

Log output:

```text
SPRING_DATASOURCE_PASSWORD=prod-password
JWT_SIGNING_KEY=prod-jwt-key
```

Damage path:

```text
Pod log
  |
  v
Fluent Bit / Log agent
  |
  v
Elasticsearch / Loki / Cloud Logging
  |
  v
Many users can search logs
```

ASCII:

```text
Secret in env
    |
    v
Application logs it
    |
    v
Central logging
    |
    v
Leak becomes long-lived
```

Correct practice:

```text
Never log secrets.
Mask sensitive fields.
Disable unsafe debug endpoints.
Restrict log access.
Use secret scanning.
Rotate leaked credentials immediately.
```

Interview sentence:

```text
Kubernetes Secrets reduce accidental exposure compared with ConfigMaps, but application logging and RBAC mistakes can still leak them.
```

---

# 26. Production Story: Config Drift Between Environments

Dev ConfigMap:

```text
PAYMENT_TIMEOUT_MS=10000
```

Prod ConfigMap:

```text
PAYMENT_TIMEOUT_MS=1000
```

App works in dev but fails in prod under slow payment provider.

Problem:

```text
Same code, different runtime config behavior.
```

This is not always bad.

But uncontrolled drift is dangerous.

Good practice:

```text
Use GitOps.
Store manifests in Git.
Review config changes.
Use overlays per environment.
```

Example structure:

```text
k8s/
  base/
    deployment.yaml
    service.yaml
  overlays/
    dev/
      configmap.yaml
    staging/
      configmap.yaml
    prod/
      configmap.yaml
```

ASCII:

```text
Base app manifest
      |
      +--> dev overlay
      +--> staging overlay
      +--> prod overlay
```

Mental model:

```text
Configuration is code.
Treat it with review, versioning, rollback, and ownership.
```

---

# 27. ConfigMap vs Secret Decision Table

```text
Use ConfigMap when:
  - value is non-sensitive
  - losing it does not expose credentials
  - examples: log level, timeout, feature flag, service URL

Use Secret when:
  - value is credential/token/key/password
  - leaking it can cause unauthorized access
  - examples: DB password, API key, JWT signing key, TLS private key
```

ASCII:

```text
Is value sensitive?
      |
      +-- no  --> ConfigMap
      |
      +-- yes --> Secret
```

More examples:

```text
SPRING_PROFILES_ACTIVE          -> ConfigMap
LOGGING_LEVEL_ROOT              -> ConfigMap
SPRING_DATASOURCE_URL           -> ConfigMap usually
SPRING_DATASOURCE_USERNAME      -> Secret usually
SPRING_DATASOURCE_PASSWORD      -> Secret
JWT_SIGNING_KEY                 -> Secret
REDIS_HOST                      -> ConfigMap
REDIS_PASSWORD                  -> Secret
KAFKA_BOOTSTRAP_SERVERS         -> ConfigMap
SASL_PASSWORD                   -> Secret
```

Important nuance:

```text
A database URL may contain username/password.
If it contains credentials, treat it as secret.
```

---

# 28. Env Vars vs Volume Mounts Decision Table

```text
Use env vars when:
  - simple key-value settings
  - app reads only at startup
  - standard Spring Boot binding is enough
  - small values

Use mounted files when:
  - structured YAML/properties
  - certificates
  - many config fields
  - app expects file paths
  - config should appear as files
```

ASCII:

```text
Config shape?
     |
     +-- simple key/value --> env
     |
     +-- file/cert/yaml  --> volume mount
```

Comparison:

```text
Env vars:
  + simple
  + familiar
  + good for Spring Boot properties
  - restart needed for changes
  - risky if app prints env

Volume mounts:
  + good for files
  + cert-friendly
  + can update mounted files eventually
  - app must read/watch files
  - path management needed
```

Do not overcomplicate.

For most Spring Boot services:

```text
ConfigMap + Secret via envFrom is okay.
```

For certificates:

```text
Secret as volume mount is better.
```

---

# 29. Security Model For Secrets

Secret security is layered.

```text
Kubernetes Secret
      |
      +-- RBAC controls who can read it
      |
      +-- etcd encryption protects stored value
      |
      +-- Pod access controls who can mount it
      |
      +-- App discipline avoids logging it
      |
      +-- External secret manager handles lifecycle
```

Diagram:

```text
Secret value
   |
   v
API Server
   |
   v
etcd
   |
   v
Kubelet
   |
   v
Pod
   |
   v
Application
```

Every arrow is a possible risk boundary.

Good practices:

```text
1. Enable encryption at rest for Secrets.
2. Use RBAC least privilege.
3. Do not give broad list/get secrets access.
4. Avoid storing raw production secrets in Git.
5. Use External Secrets Operator or CSI Secret Store where appropriate.
6. Rotate credentials.
7. Audit who accessed Secrets.
8. Do not expose /actuator/env publicly.
```

Do not memorize tool names.

Understand the direction:

```text
Plain Kubernetes Secret is basic.
Production secret management adds stronger lifecycle and access control.
```

---

# 30. External Secret Manager Mental Model

In many production systems, the source of truth for secrets is not Kubernetes YAML.

It may be:

```text
AWS Secrets Manager
Azure Key Vault
Google Secret Manager
HashiCorp Vault
Doppler
1Password Secrets Automation
```

Kubernetes receives synced secrets.

Common flow:

```text
External Secret Manager
      |
      v
External Secrets Operator
      |
      v
Kubernetes Secret
      |
      v
Pod
```

ASCII:

```text
+--------------------------+
| Cloud Secret Manager     |
| real source of truth     |
+------------+-------------+
             |
             v
+--------------------------+
| External Secrets Operator|
| sync controller          |
+------------+-------------+
             |
             v
+--------------------------+
| Kubernetes Secret        |
+------------+-------------+
             |
             v
+--------------------------+
| Application Pod          |
+--------------------------+
```

Why this helps:

```text
Centralized secret rotation
Audit logs
Cloud IAM integration
Avoid raw secrets in Git
```

But the mental model remains:

```text
App still consumes config/secret through env vars or mounted files.
```

---

# 31. Rollout Restart and Config Checksums

Problem:

```text
ConfigMap changed.
Deployment spec did not change.
Kubernetes does not automatically recreate Pods.
```

Common solution:

```bash
kubectl rollout restart deployment/order-service -n orders
```

This updates Pod template annotation and forces new Pods.

In Helm/Kustomize, teams often add checksum annotation:

```yaml
spec:
  template:
    metadata:
      annotations:
        checksum/config: "hash-of-configmap-content"
```

When ConfigMap changes:

```text
checksum changes
      |
      v
Pod template changes
      |
      v
Deployment creates new ReplicaSet
      |
      v
Pods restart with new config
```

ASCII:

```text
Config change
    |
    v
Checksum changes
    |
    v
Deployment template changes
    |
    v
Rolling update
    |
    v
New Pods get new env
```

Mental model:

```text
Deployment rolls Pods only when Pod template changes.
ConfigMap alone is separate object.
```

---

# 32. Debugging Commands

Basic:

```bash
kubectl get configmap -n orders
kubectl get secret -n orders
kubectl get deployment -n orders
kubectl get pods -n orders
```

Inspect ConfigMap:

```bash
kubectl describe configmap order-service-config -n orders
kubectl get configmap order-service-config -n orders -o yaml
```

Inspect Secret metadata:

```bash
kubectl describe secret order-service-secret -n orders
```

Check if env is present inside Pod:

```bash
kubectl exec -n orders <pod-name> -- printenv | grep ORDER_
kubectl exec -n orders <pod-name> -- printenv SPRING_PROFILES_ACTIVE
```

Careful with secrets:

```bash
kubectl exec -n orders <pod-name> -- printenv SPRING_DATASOURCE_PASSWORD
```

Do not run that in shared environments unless necessary.

Describe Pod events:

```bash
kubectl describe pod -n orders <pod-name>
```

Restart Deployment:

```bash
kubectl rollout restart deployment/order-service -n orders
kubectl rollout status deployment/order-service -n orders
```

Check rollout history:

```bash
kubectl rollout history deployment/order-service -n orders
```

---

# 33. Debugging Mindset: Layer By Layer

Follow this chain:

```text
1. Does ConfigMap exist?
2. Does Secret exist?
3. Are they in the same namespace as Pod?
4. Are required keys present?
5. Does Deployment reference correct names?
6. Does Pod start?
7. Does container env contain expected values?
8. Does Spring Boot bind env to properties?
9. Does readiness pass?
10. Does service route traffic?
```

ASCII:

```text
ConfigMap / Secret
        |
        v
Deployment reference
        |
        v
Pod spec
        |
        v
Kubelet injection
        |
        v
Container env/files
        |
        v
Spring Boot binding
        |
        v
Application behavior
```

Do not start debugging at random.

Example failure:

```text
App cannot connect to DB
```

Possible layers:

```text
ConfigMap DB URL wrong
Secret password wrong
Secret updated but Pod not restarted
Spring property name wrong
DB service DNS wrong
NetworkPolicy blocking DB
Database user missing permission
```

Layered debugging prevents guessing.

---

# 34. Common Mistakes

```text
Mistake 1:
Putting DB password in ConfigMap.
Correct:
Use Secret.

Mistake 2:
Thinking Secret base64 means encrypted.
Correct:
Base64 is encoding, not encryption.

Mistake 3:
Updating ConfigMap and expecting env vars to change in running Pods.
Correct:
Restart Pods or use dynamic config system.

Mistake 4:
Creating ConfigMap in default namespace but Deployment in prod namespace.
Correct:
Create config in same namespace.

Mistake 5:
Using envFrom blindly with many keys.
Correct:
Use explicit env mapping when you need control.

Mistake 6:
Logging all environment variables.
Correct:
Never log secrets.

Mistake 7:
Exposing /actuator/env publicly.
Correct:
Protect or disable sensitive actuator endpoints.

Mistake 8:
Hardcoding prod config inside Docker image.
Correct:
Build once, configure at runtime.

Mistake 9:
Changing Secret but forgetting to rollout restart.
Correct:
Restart Deployment or use checksum/automation.

Mistake 10:
Assuming ConfigMap is feature flag system.
Correct:
Use proper dynamic config when runtime flipping is required.
```

---

# 35. Java/Spring Boot Property Binding Rules

Spring Boot relaxed binding is very helpful.

Examples:

```text
ORDER_PAYMENT_TIMEOUT_MS
  -> order.payment-timeout-ms
  -> order.paymentTimeoutMs

SPRING_DATASOURCE_URL
  -> spring.datasource.url

LOGGING_LEVEL_ROOT
  -> logging.level.root
```

Java:

```java
@ConfigurationProperties(prefix = "order")
public class OrderProperties {
    private int paymentTimeoutMs;
    private boolean checkoutV2Enabled;
}
```

Env:

```text
ORDER_PAYMENT_TIMEOUT_MS=3000
ORDER_CHECKOUT_V2_ENABLED=true
```

Result:

```text
paymentTimeoutMs = 3000
checkoutV2Enabled = true
```

ASCII:

```text
ENV VARIABLE
ORDER_PAYMENT_TIMEOUT_MS
        |
        v
Spring relaxed binding
        |
        v
order.payment-timeout-ms
        |
        v
Java field paymentTimeoutMs
```

Common bug:

```text
Env var name wrong:
ORDER_PAYMENT_TIMEOUT=3000
```

Spring expects:

```text
ORDER_PAYMENT_TIMEOUT_MS
```

Debug:

```text
Check configuration properties binding.
Check startup logs.
Use safe internal config summary.
```

---

# 36. Config Validation In Spring Boot

Do not let invalid config fail later under traffic.

Validate at startup.

Example:

```java
package com.example.order.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "order")
public class OrderProperties {

    @Min(100)
    @Max(30000)
    private int paymentTimeoutMs;

    private boolean checkoutV2Enabled;

    public int getPaymentTimeoutMs() {
        return paymentTimeoutMs;
    }

    public void setPaymentTimeoutMs(int paymentTimeoutMs) {
        this.paymentTimeoutMs = paymentTimeoutMs;
    }

    public boolean isCheckoutV2Enabled() {
        return checkoutV2Enabled;
    }

    public void setCheckoutV2Enabled(boolean checkoutV2Enabled) {
        this.checkoutV2Enabled = checkoutV2Enabled;
    }
}
```

If ConfigMap says:

```text
ORDER_PAYMENT_TIMEOUT_MS=999999
```

App should fail fast.

Mental model:

```text
Bad config should fail during deployment, not during customer checkout.
```

ASCII:

```text
ConfigMap
   |
   v
Spring Boot startup
   |
   v
Validation
   |
   +-- valid   --> Ready
   |
   +-- invalid --> Fail fast
```

This is production-grade behavior.

---

# 37. ConfigMap Immutable Option

ConfigMaps and Secrets can be marked immutable.

Example:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: order-service-config
immutable: true
data:
  LOGGING_LEVEL_ROOT: "INFO"
```

Meaning:

```text
Once created, data cannot be changed.
```

Why useful:

```text
1. Prevent accidental mutation.
2. Encourage versioned config.
3. Improve safety for production rollouts.
```

Pattern:

```text
order-service-config-v1
order-service-config-v2
order-service-config-v3
```

Deployment references specific version.

ASCII:

```text
Deployment
   |
   v
ConfigMap v1

New config:
Deployment updated
   |
   v
ConfigMap v2
```

This is similar to image tags:

```text
Do not mutate production configuration invisibly.
Version it.
```

Tradeoff:

```text
More objects to manage.
Cleaner rollback story.
```

---

# 38. One Full Failure Dry Run

Scenario:

```text
A new production release fails readiness after deployment.
```

Symptoms:

```text
kubectl get pods -n orders
order-service-abc 0/1 Running
```

Logs:

```text
Failed to configure a DataSource: url attribute is not specified
```

Investigation:

```text
1. Pod is running, so image pulled and Java started.

2. Readiness failing, so app is not healthy enough.

3. Error says datasource URL missing.

4. Check ConfigMap:
   kubectl get configmap order-service-config -n orders -o yaml

5. ConfigMap has:
   SPRING_DATASOURCE_URI
   instead of:
   SPRING_DATASOURCE_URL

6. Spring Boot did not bind datasource URL.

7. Fix key name.

8. Apply ConfigMap.

9. Restart Deployment.

10. New Pods start and readiness passes.
```

ASCII:

```text
Wrong ConfigMap key
       |
       v
Env var present but wrong name
       |
       v
Spring Boot cannot bind datasource.url
       |
       v
DB config missing
       |
       v
Readiness DOWN
```

Lesson:

```text
Kubernetes delivered the value correctly.
Application expected a different name.
```

---

# 39. Interview Questions

## What is a ConfigMap?

A ConfigMap is a Kubernetes object used to store non-sensitive configuration data as key-value pairs or files. Pods can consume it as environment variables, command arguments, or mounted files.

## What is a Secret?

A Secret is a Kubernetes object intended for sensitive values such as passwords, tokens, TLS keys, and API credentials. It can be injected into Pods as environment variables or mounted as files. Base64 encoding is not encryption, so RBAC and encryption at rest are important.

## ConfigMap vs Secret?

Use ConfigMap for non-sensitive settings like log level, timeout, feature flag, and service URL. Use Secret for credentials, passwords, tokens, signing keys, and private certificates.

## How does a Pod consume ConfigMap values?

A Pod consumes ConfigMap values by referencing them in the Pod spec, commonly through `env`, `envFrom`, or a volume mount. The ConfigMap must exist in the same namespace unless using more advanced patterns.

## Do ConfigMap changes automatically update environment variables in running Pods?

No. Environment variables are set when the container starts. If a ConfigMap changes, running containers usually keep old env values. Restart or roll out Pods to pick up new env values.

## Why did changing a Secret not affect my app?

If the Secret was consumed as environment variables, the running container still has the old values. Restart the Pod or Deployment so Kubernetes injects the updated values into new containers.

## Is Kubernetes Secret encrypted?

Not necessarily. Secret data is base64 encoded in YAML/API representation. Encryption at rest depends on cluster configuration. RBAC, etcd encryption, and access controls are required for real protection.

## Why use `stringData` in Secret YAML?

`stringData` lets humans write plain string values in YAML. Kubernetes converts them to base64 encoded `data` internally. It is easier than manually base64 encoding values.

## When should I mount a Secret as a file?

Mount a Secret as a file for certificates, private keys, trust stores, kubeconfigs, and apps that expect file paths instead of environment variables.

## What is the safest production pattern?

Build one immutable image, keep non-sensitive config in ConfigMaps, keep sensitive data in Secrets or external secret managers, inject them into Pods explicitly, validate config at startup, avoid logging secrets, and restart Pods when startup config changes.

---

# 40. Cheat Sheet

```text
ConfigMap
  Purpose:
    Non-sensitive configuration

  Examples:
    LOG_LEVEL
    TIMEOUT_MS
    FEATURE_FLAG
    SERVICE_URL

Secret
  Purpose:
    Sensitive configuration

  Examples:
    PASSWORD
    API_KEY
    JWT_SECRET
    TLS_PRIVATE_KEY

Env
  Purpose:
    Inject values into container process

Consumption Methods:
  env:
    One key -> one env var

  envFrom:
    All keys -> env vars

  volume:
    Keys -> files
```

Commands:

```bash
kubectl get configmap -n orders
kubectl get secret -n orders

kubectl describe configmap order-service-config -n orders
kubectl describe secret order-service-secret -n orders

kubectl get configmap order-service-config -n orders -o yaml
kubectl describe pod <pod-name> -n orders

kubectl exec -n orders <pod-name> -- printenv SPRING_PROFILES_ACTIVE

kubectl rollout restart deployment/order-service -n orders
kubectl rollout status deployment/order-service -n orders
```

Memory hooks:

```text
ConfigMap = public app settings
Secret    = private app settings
Env       = startup injection pipe
Volume    = config as files
```

---

# 41. One Picture To Remember

```text
                    SAME DOCKER IMAGE
                  order-service:1.0.0
                           |
                           v
                  +----------------+
                  | Deployment     |
                  | Pod template   |
                  +--------+-------+
                           |
             +-------------+-------------+
             |                           |
             v                           v
     +---------------+           +---------------+
     | ConfigMap     |           | Secret        |
     | non-sensitive |           | sensitive     |
     +-------+-------+           +-------+-------+
             |                           |
             +-------------+-------------+
                           |
                           v
                  +----------------+
                  | Kubelet        |
                  | inject env/file|
                  +--------+-------+
                           |
                           v
                  +----------------+
                  | Container      |
                  | env variables |
                  | mounted files |
                  +--------+-------+
                           |
                           v
                  +----------------+
                  | Spring Boot    |
                  | binds config   |
                  | starts app     |
                  +----------------+

Rule:

Build the app image once.
Inject environment-specific behavior at runtime.
Keep normal config in ConfigMaps.
Keep sensitive config in Secrets.
Restart Pods when startup config changes.
Never log secrets.
```

---

# 42. Final Production Checklist

```text
[ ] Same image can run in dev/staging/prod.
[ ] Non-sensitive values are in ConfigMap.
[ ] Sensitive values are in Secret or external secret manager.
[ ] ConfigMap and Secret are in same namespace as the Pod.
[ ] Deployment references correct ConfigMap and Secret names.
[ ] Required keys are present.
[ ] Spring Boot env variable names match expected property names.
[ ] App validates critical config at startup.
[ ] Secrets are not printed in logs.
[ ] /actuator/env is protected or disabled.
[ ] RBAC restricts who can read Secrets.
[ ] etcd encryption at rest is enabled for Secrets in production.
[ ] Config changes trigger rollout restart or checksum-based rollout.
[ ] Secret rotation plan exists.
[ ] Debugging follows ConfigMap/Secret -> Pod -> env/files -> Spring binding -> readiness.
```

---

# 43. Final Memory Hook

Do not memorize ConfigMap, Secret, and Env as YAML syntax.

Remember them as a runtime configuration pipeline:

```text
Configuration source
        |
        v
Kubernetes object
        |
        v
Pod reference
        |
        v
Kubelet injection
        |
        v
Container environment/files
        |
        v
Spring Boot property binding
        |
        v
Application behavior
```

Final sentence:

```text
A production container image should be stable; configuration should be injected; secrets should be protected; and application behavior should be explainable from the runtime config pipeline.
```
