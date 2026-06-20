# 023_Health_Checks_Readiness_Liveness_Startup.md

> MiniK8s Deep Production Mode  
> Understanding First • ASCII Visual Learning • Real World Mental Model • Do Not Memorize • Java + Spring Boot + Kubernetes + Production Debugging

---

# 1. Why Health Checks Exist

In Kubernetes, a Pod can be in `Running` state while the application inside it is not usable.

That sentence is the whole reason health checks exist.

A container may be alive because the Java process exists:

```text
PID 1 = java -jar order-service.jar
```

But the service may still be unable to handle real user traffic:

```text
Database connection not ready
Kafka consumer not assigned yet
Redis cache warming
Spring context still starting
Migration running
Thread pool exhausted
App deadlocked but process still alive
```

Without health checks, Kubernetes only knows:

```text
Container process exists
```

But production traffic needs a stronger question:

```text
Can this application safely serve requests now?
```

Health checks are how Kubernetes learns the difference between:

```text
Process exists
Application is ready
Application is alive
Application has completed startup
```

One picture:

```text
Pod Running
   |
   +--> Process exists?              yes
   +--> App fully started?           maybe
   +--> Can receive traffic?         maybe
   +--> Should be restarted?         maybe
```

Kubernetes health checks are not decorative YAML.

They are production safety signals.

If you configure them badly, Kubernetes may:

```text
Send traffic too early
Kill healthy-but-slow apps
Keep deadlocked apps forever
Break rolling updates
Cause restart storms
Hide real failures
```

So do not memorize probe fields.

Understand the three questions Kubernetes asks.

```text
startupProbe   = Has the app finished booting?
readinessProbe = Can the app receive traffic now?
livenessProbe  = Is the app so broken it must be restarted?
```

---

# 2. The Wrong Way To Think About Probes

Bad mental model:

```text
readinessProbe and livenessProbe are both health checks.
Just point both to /health.
```

This is dangerous.

Why?

Because readiness and liveness have different consequences.

```text
Readiness fails:
  Pod is removed from Service endpoints.
  Traffic stops.
  Container is NOT restarted.

Liveness fails:
  Container is killed and restarted.
```

Diagram:

```text
Same endpoint used wrongly

/actuator/health
      |
      +--> readinessProbe failure -> remove traffic
      |
      +--> livenessProbe failure  -> restart app
```

If your `/health` endpoint checks database connectivity and the database has a short outage, then liveness may fail.

Kubernetes may restart every app instance even though the app itself is not broken.

```text
DB temporary outage
        |
        v
/health returns DOWN
        |
        v
livenessProbe fails
        |
        v
Kubernetes restarts app
        |
        v
All pods restart
        |
        v
Outage becomes worse
```

Correct thinking:

```text
Readiness can depend on dependencies.
Liveness should usually be narrow and local.
Startup protects slow boot.
```

Do not copy probe YAML from Stack Overflow.

Probe design is architecture.

---

# 3. Real World Analogy: Restaurant Opening

Imagine a restaurant.

The building exists.

The lights are on.

The chef is inside.

But can customers enter?

Maybe not.

```text
Building exists        = container running
Chef awake             = process alive
Kitchen prepared       = startup complete
Tables ready           = readiness true
Chef collapsed         = liveness false
```

Opening flow:

```text
Restaurant staff enters
        |
        v
Kitchen setup
        |
        v
Ingredients checked
        |
        v
Tables cleaned
        |
        v
Door opens to customers
```

Kubernetes equivalent:

```text
Container starts
        |
        v
Spring Boot context loads
        |
        v
DB pool initialized
        |
        v
Cache warmed
        |
        v
readiness passes
        |
        v
Service sends traffic
```

Now imagine the chef faints.

That is not "temporarily not ready".

That is "restart/replace the worker".

```text
Chef fainted
    |
    v
Manager removes chef
    |
    v
New chef comes
```

Kubernetes:

```text
livenessProbe fails
    |
    v
kubelet kills container
    |
    v
container restarts
```

Restaurant lesson:

```text
Do not open the door before kitchen is ready.
Do not fire the chef just because the supplier is late.
```

That is readiness vs liveness.

---

# 4. The Three Kubernetes Probe Questions

Kubernetes probes answer three separate questions.

```text
+----------------+----------------------------------+----------------------+
| Probe          | Question                         | Failure Action       |
+----------------+----------------------------------+----------------------+
| startupProbe   | Has the app finished booting?    | Kill/restart if too  |
|                |                                  | slow or stuck        |
+----------------+----------------------------------+----------------------+
| readinessProbe | Can the app receive traffic now? | Remove from Service  |
|                |                                  | endpoints            |
+----------------+----------------------------------+----------------------+
| livenessProbe  | Is the app alive/recoverable?    | Restart container    |
+----------------+----------------------------------+----------------------+
```

Mental model:

```text
startupProbe:
  "Do not judge me by liveness yet. I am still starting."

readinessProbe:
  "Do not send me traffic until I say yes."

livenessProbe:
  "If I am permanently broken, restart me."
```

Flow:

```text
Container created
      |
      v
startupProbe active
      |
      | passes
      v
readiness + liveness active
      |
      +--> readiness controls traffic
      |
      +--> liveness controls restart
```

Important:

```text
Startup probe disables liveness/readiness failure handling until startup succeeds.
```

This is especially useful for Spring Boot apps that have slow cold starts.

Without startupProbe, liveness may kill the app before it finishes booting.

```text
Slow Spring Boot startup
      |
      v
livenessProbe starts too early
      |
      v
probe fails
      |
      v
container killed
      |
      v
app never gets time to start
```

This is a classic production failure.

---

# 5. Pod Phase vs Container State vs Readiness

Kubernetes has multiple layers of status.

Do not confuse them.

```text
Pod Phase:
  Pending / Running / Succeeded / Failed / Unknown

Container State:
  Waiting / Running / Terminated

Readiness:
  Ready / NotReady

Application Health:
  healthy / degraded / deadlocked / overloaded
```

Diagram:

```text
Pod
+------------------------------------------------+
| Phase: Running                                 |
|                                                |
| Container                                      |
|   State: Running                               |
|                                                |
| Readiness Condition                            |
|   Ready: False                                 |
|                                                |
| App Reality                                    |
|   Spring Boot alive, DB not connected yet      |
+------------------------------------------------+
```

A Pod can be:

```text
Running but NotReady
Running but failing liveness soon
Running but not receiving Service traffic
Running but application-level degraded
```

That is why `kubectl get pods` must be read carefully.

Example:

```text
NAME                    READY   STATUS    RESTARTS
order-service-abc       0/1     Running   0
```

Meaning:

```text
Container is running.
But ready containers = 0 out of 1.
Service should not send traffic.
```

Do not say:

```text
Pod is running, so everything is fine.
```

Correct mindset:

```text
Running is process-level.
Ready is traffic-level.
Healthy is application-level.
```

---

# 6. Service Endpoints Depend On Readiness

Services do not simply send traffic to every Pod matching labels.

They send traffic to ready endpoints.

Simplified model:

```text
Service selector:
  app = order-service

Matching Pods:
  Pod A Ready
  Pod B NotReady
  Pod C Ready

Endpoints:
  Pod A
  Pod C
```

ASCII:

```text
Client
  |
  v
Service: order-service
  |
  +--> Pod A  Ready     yes traffic
  |
  +--> Pod B  NotReady  no traffic
  |
  +--> Pod C  Ready     yes traffic
```

This is the core purpose of readiness.

It protects users from half-started or temporarily unhealthy Pods.

Readiness failure does not restart the container.

It only changes traffic eligibility.

```text
readinessProbe fails
      |
      v
Pod Ready condition = False
      |
      v
Endpoint removed
      |
      v
Service stops routing traffic
```

When readiness passes again:

```text
readinessProbe passes
      |
      v
Pod Ready condition = True
      |
      v
Endpoint added
      |
      v
Traffic resumes
```

This makes readiness useful for:

```text
Slow app startup
Dependency unavailable
Cache warmup
Graceful shutdown
Temporary overload
Manual traffic drain
```

---

# 7. Liveness Is A Restart Decision

Liveness is not "is the database reachable?"

Liveness is:

```text
Is this container still alive enough that keeping it running makes sense?
```

Failure action:

```text
Kubelet kills container
Container runtime restarts it based on restartPolicy
```

Diagram:

```text
livenessProbe fails N times
        |
        v
kubelet decides container unhealthy
        |
        v
SIGTERM sent
        |
        v
grace period
        |
        v
SIGKILL if needed
        |
        v
container restarted
```

Use liveness for failures that restart can fix:

```text
Deadlock
Main event loop stuck
JVM process alive but app not responding
Fatal internal state
Unrecoverable connection pool corruption
Memory leak causing permanent unresponsiveness
```

Do not use liveness for failures restart cannot fix:

```text
Database down
Kafka broker down
External payment API down
DNS outage
Downstream service slow
```

Why?

Because restarting all Pods does not fix the database.

It just creates more load and slower recovery.

Bad liveness design:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
```

If `/actuator/health` includes DB, Redis, Kafka, then dependency outage becomes app restart storm.

Better:

```text
liveness endpoint checks only local process health.
readiness endpoint checks ability to serve traffic.
```

---

# 8. Startup Probe Protects Slow Applications

Some apps need time before they can answer health endpoints.

Spring Boot may take longer when:

```text
Classpath is large
JPA scans many entities
Hibernate validates schema
Flyway/Liquibase migration runs
Connection pool waits for DB
Container CPU is throttled
JVM cold start under low CPU limit
```

Without startupProbe:

```text
Container starts
      |
      v
livenessProbe begins after initialDelaySeconds
      |
      v
App not ready yet
      |
      v
liveness fails
      |
      v
Container killed
      |
      v
Repeat forever
```

This produces:

```text
CrashLoopBackOff
```

But the app is not "bad".

It was killed too early.

Startup probe says:

```text
Give this app a boot window.
Only after startup succeeds should liveness judge it.
```

Example:

```yaml
startupProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  failureThreshold: 30
  periodSeconds: 10
```

This gives:

```text
30 failures * 10 seconds = 300 seconds startup window
```

During this time, liveness failure does not kill the app.

Mental model:

```text
startupProbe = probation period before normal health rules
```

Use it for:

```text
Slow Spring Boot services
Large monoliths
Apps with warmup
Services using JIT warmup
Migration-heavy startup
Legacy apps
```

---

# 9. Probe Types: HTTP, TCP, Exec, gRPC

Kubernetes supports different probe mechanisms.

```text
HTTP GET:
  kubelet calls an HTTP path.

TCP socket:
  kubelet checks if a TCP port accepts connection.

Exec:
  kubelet runs a command inside the container.

gRPC:
  kubelet performs gRPC health check if configured.
```

Mental model:

```text
Probe type = how kubelet asks the health question
Probe purpose = startup/readiness/liveness
```

They are separate concepts.

```text
readinessProbe can use HTTP
readinessProbe can use TCP
readinessProbe can use Exec

livenessProbe can use HTTP
livenessProbe can use TCP
livenessProbe can use Exec
```

HTTP is common for Spring Boot:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
```

TCP is weaker:

```yaml
readinessProbe:
  tcpSocket:
    port: 8080
```

TCP only says:

```text
Port accepts connections.
```

It does not say:

```text
App can process business requests.
DB is reachable.
Thread pool is healthy.
```

Exec is powerful but risky:

```yaml
livenessProbe:
  exec:
    command:
      - sh
      - -c
      - test -f /tmp/healthy
```

Exec depends on shell/tools existing in the image.

Distroless images may not have `sh`.

Production recommendation for Spring Boot:

```text
Use HTTP Actuator probes for most services.
Use TCP only for simple port-level services.
Use Exec carefully.
```

---

# 10. Spring Boot Actuator Probe Model

Spring Boot Actuator can expose Kubernetes-friendly probe endpoints.

Common endpoints:

```text
/actuator/health/liveness
/actuator/health/readiness
```

Application config:

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

Now Kubernetes can use:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
```

The mental model:

```text
Spring Boot knows application internals.
Kubelet knows container lifecycle.
Actuator is the bridge.
```

ASCII:

```text
kubelet
  |
  | HTTP GET
  v
/actuator/health/readiness
  |
  v
Spring Boot HealthIndicator
  |
  +--> DB check
  +--> Redis check
  +--> custom readiness state
  |
  v
UP or DOWN
```

But be careful.

Not every dependency should be in every health group.

For readiness, checking DB may be useful.

For liveness, checking DB is usually wrong.

Correct separation:

```text
liveness:
  local app process health

readiness:
  dependencies required to serve traffic
```

---

# 11. Java Code: Custom Readiness Indicator

Sometimes default health is not enough.

Example:

```text
Order service should not be ready until:
  - Spring context is started
  - Database is reachable
  - Kafka producer is initialized
  - local cache is warmed
```

You can expose a custom readiness condition.

```java
package com.example.order.health;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("cacheWarmup")
public class CacheWarmupHealthIndicator implements HealthIndicator {

    private final AtomicBoolean warmedUp = new AtomicBoolean(false);

    public void markWarmedUp() {
        warmedUp.set(true);
    }

    @Override
    public Health health() {
        if (warmedUp.get()) {
            return Health.up()
                    .withDetail("cache", "warmed")
                    .build();
        }

        return Health.down()
                .withDetail("cache", "warming")
                .build();
    }
}
```

Warmup service:

```java
package com.example.order.startup;

import com.example.order.health.CacheWarmupHealthIndicator;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class CacheWarmupService {

    private final CacheWarmupHealthIndicator cacheHealth;

    public CacheWarmupService(CacheWarmupHealthIndicator cacheHealth) {
        this.cacheHealth = cacheHealth;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warmUp() {
        // Simulate loading hot product/order metadata into memory.
        // In production this could call DB/Redis carefully with timeout.
        loadImportantData();

        cacheHealth.markWarmedUp();
    }

    private void loadImportantData() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

Mental model:

```text
App controls readiness signal.
Kubernetes controls traffic based on that signal.
```

This is powerful.

The app can say:

```text
I am running, but do not send traffic yet.
```

---

# 12. Java Code: Availability State

Spring Boot has application availability states.

Important concepts:

```text
LivenessState:
  Is the internal app state alive?

ReadinessState:
  Is the app ready to accept traffic?
```

Example component that marks readiness false during maintenance:

```java
package com.example.order.availability;

import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class TrafficSwitchService {

    private final ApplicationContext applicationContext;

    public TrafficSwitchService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void stopReceivingTraffic() {
        AvailabilityChangeEvent.publish(
                applicationContext,
                ReadinessState.REFUSING_TRAFFIC
        );
    }

    public void startReceivingTraffic() {
        AvailabilityChangeEvent.publish(
                applicationContext,
                ReadinessState.ACCEPTING_TRAFFIC
        );
    }
}
```

Controller for manual traffic drain in controlled scenarios:

```java
package com.example.order.admin;

import com.example.order.availability.TrafficSwitchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrafficAdminController {

    private final TrafficSwitchService trafficSwitchService;

    public TrafficAdminController(TrafficSwitchService trafficSwitchService) {
        this.trafficSwitchService = trafficSwitchService;
    }

    @PostMapping("/internal/traffic/drain")
    public String drain() {
        trafficSwitchService.stopReceivingTraffic();
        return "readiness=false";
    }

    @PostMapping("/internal/traffic/accept")
    public String accept() {
        trafficSwitchService.startReceivingTraffic();
        return "readiness=true";
    }
}
```

Production warning:

```text
Do not expose this publicly.
Protect it with internal network rules, authentication, or remove it.
```

Mental model:

```text
Readiness is a traffic switch.
Application can deliberately close the switch.
```

---

# 13. Full Kubernetes YAML Example

A good Spring Boot probe setup:

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
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 0
      maxSurge: 1
  template:
    metadata:
      labels:
        app: order-service
    spec:
      terminationGracePeriodSeconds: 30
      containers:
        - name: order-service
          image: registry.example.com/order-service:1.0.0
          ports:
            - containerPort: 8080

          startupProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            failureThreshold: 30
            periodSeconds: 10
            timeoutSeconds: 2

          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 5
            periodSeconds: 5
            timeoutSeconds: 2
            failureThreshold: 3
            successThreshold: 1

          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            periodSeconds: 10
            timeoutSeconds: 2
            failureThreshold: 3
```

Read it as behavior, not syntax:

```text
startupProbe:
  Give app up to 300 seconds to start.

readinessProbe:
  Check every 5 seconds.
  If it fails 3 times, remove from traffic.

livenessProbe:
  Check every 10 seconds.
  If it fails 3 times, restart container.
```

Traffic safety:

```text
maxUnavailable: 0
  During rolling update, do not intentionally reduce available pods.

maxSurge: 1
  Create one extra new pod during rollout.
```

This matters because readiness controls rollout progress.

---

# 14. Probe Timing Parameters

Probe YAML fields are not random.

They form a timing model.

```text
initialDelaySeconds:
  Wait before starting probe.

periodSeconds:
  How often to probe.

timeoutSeconds:
  How long to wait for one probe response.

failureThreshold:
  How many consecutive failures before action.

successThreshold:
  How many successes required to recover readiness.
```

ASCII:

```text
time --->

initialDelay
    |
    v
probe  probe  probe  probe
  |      |      |      |
  v      v      v      v
 fail   fail   fail   action
```

Example:

```yaml
readinessProbe:
  periodSeconds: 5
  failureThreshold: 3
```

Worst case removal from traffic after:

```text
3 failures * 5 seconds = about 15 seconds
```

Example:

```yaml
livenessProbe:
  periodSeconds: 10
  failureThreshold: 3
```

Restart after about:

```text
3 failures * 10 seconds = about 30 seconds
```

Do not set probe periods too aggressive.

Bad:

```yaml
periodSeconds: 1
timeoutSeconds: 1
failureThreshold: 1
```

This can cause false failures during:

```text
CPU throttling
GC pause
Node pressure
Network hiccup
Temporary load spike
```

Good probe design accepts reality:

```text
Production systems have small pauses.
Do not kill apps for one missed heartbeat.
```

---

# 15. Dry Run: Successful Pod Startup

Timeline:

```text
T+0s   container starts
T+3s   JVM starts
T+8s   Spring context loading
T+18s  Tomcat started
T+22s  DB pool initialized
T+25s  cache warmup complete
T+26s  startupProbe passes
T+27s  readinessProbe passes
T+28s  Pod Ready=True
T+29s  Service endpoint added
T+30s  traffic starts
```

ASCII:

```text
Container Start
      |
      v
JVM Boot
      |
      v
Spring Context
      |
      v
DB Pool
      |
      v
Cache Warmup
      |
      v
Startup OK
      |
      v
Readiness OK
      |
      v
Service Traffic
```

The important point:

```text
Kubernetes should not send traffic at T+3s just because Java process exists.
```

Readiness protects the user.

Startup protects the app from premature liveness killing.

Liveness protects the cluster from permanently broken processes.

Together:

```text
startupProbe   protects boot
readinessProbe protects traffic
livenessProbe  protects recovery
```

---

# 16. Dry Run: Database Down During Startup

Scenario:

```text
Order service starts.
Database is temporarily unavailable.
```

What should happen?

Good behavior:

```text
Container starts
Spring Boot starts partially
Readiness returns DOWN
Pod stays NotReady
Service sends no traffic
App keeps retrying DB connection
DB returns
Readiness becomes UP
Traffic begins
```

ASCII:

```text
DB Down
  |
  v
readiness DOWN
  |
  v
Pod NotReady
  |
  v
No traffic
  |
  v
DB recovers
  |
  v
readiness UP
  |
  v
Traffic allowed
```

Bad behavior:

```text
DB Down
  |
  v
liveness DOWN
  |
  v
Kubernetes restarts all pods
  |
  v
Pods repeatedly restart
  |
  v
DB recovery gets harder
```

Lesson:

```text
Dependency failure is usually readiness, not liveness.
```

The app may be alive and correctly waiting.

Restarting it is not healing.

It is noise.

---

# 17. Dry Run: Deadlock After Running

Scenario:

```text
App starts successfully.
It serves traffic for 2 hours.
Then a bug deadlocks request threads.
```

Reality:

```text
JVM process exists.
Port may still be open.
But HTTP health endpoint times out.
```

Liveness flow:

```text
livenessProbe request
      |
      v
timeout
      |
      v
failure count increments
      |
      v
after threshold reached
      |
      v
kubelet restarts container
```

ASCII:

```text
Java Process Alive
      |
      v
Request Threads Deadlocked
      |
      v
Liveness Timeout
      |
      v
Restart Container
      |
      v
Fresh JVM
```

This is a correct liveness use case.

Why?

Because the failure is local to the process.

A restart may recover the app.

But if every pod enters deadlock due to traffic pattern, liveness only hides the bug temporarily.

You still need:

```text
Thread dumps
Heap dumps
Metrics
Logs
Root cause analysis
```

Kubernetes can restart.

It cannot debug your concurrency bug.

---

# 18. Rolling Update And Readiness

Readiness is central to safe deployments.

Current version:

```text
3 ready old pods
```

Update starts:

```text
Create 1 new pod
```

But new Pod receives traffic only after readiness passes.

Flow:

```text
Old A Ready
Old B Ready
Old C Ready

New D starting
New D NotReady
No traffic to D

New D Ready
Traffic can go to D
Old A can be removed
```

ASCII:

```text
Before:

Service
  +--> Old A Ready
  +--> Old B Ready
  +--> Old C Ready

During:

Service
  +--> Old A Ready
  +--> Old B Ready
  +--> Old C Ready
  X    New D NotReady

After D Ready:

Service
  +--> Old B Ready
  +--> Old C Ready
  +--> New D Ready
```

If readiness is missing, Kubernetes may treat the new Pod as available too early.

Then traffic goes to a half-started application.

Symptoms:

```text
5xx during deployment
Connection refused
Timeouts
Random first-request failures
Rollback confusion
```

Production rule:

```text
No readiness probe = unsafe rolling update for real services.
```

---

# 19. Graceful Shutdown And Readiness

Readiness is not only for startup.

It also matters during shutdown.

When Kubernetes terminates a Pod:

```text
1. Pod marked terminating.
2. Endpoint removal begins.
3. SIGTERM sent to container.
4. App should stop accepting new work.
5. App finishes in-flight requests.
6. Container exits before grace period.
```

ASCII:

```text
kubectl rollout / scale down / node drain
        |
        v
Pod terminating
        |
        v
Remove from endpoints
        |
        v
SIGTERM
        |
        v
Spring Boot graceful shutdown
        |
        v
In-flight requests complete
        |
        v
Exit
```

Spring Boot config:

```yaml
server:
  shutdown: graceful

spring:
  lifecycle:
    timeout-per-shutdown-phase: 25s
```

Kubernetes:

```yaml
terminationGracePeriodSeconds: 30
```

Mental model:

```text
Readiness controls new traffic.
Graceful shutdown protects current traffic.
```

For high-scale services, also consider `preStop`.

```yaml
lifecycle:
  preStop:
    exec:
      command:
        - sh
        - -c
        - sleep 10
```

But do not blindly add sleep.

The goal is to allow endpoint removal to propagate before the process exits.

Better application-level behavior:

```text
On SIGTERM:
  mark readiness false
  stop accepting new requests
  finish in-flight requests
  close resources
  exit cleanly
```

---

# 20. Production Story: Readiness Missing Causes 5xx

A team deploys a Spring Boot payment service.

YAML has:

```yaml
replicas: 4
```

But no readiness probe.

During rollout:

```text
New Pod starts
Container state becomes Running
Kubernetes considers it available
Service routes traffic
Spring Boot still initializing
Payment requests hit unready app
```

Symptoms:

```text
Small burst of 502/503 during every deployment
Only happens during rollout
Logs show DB pool initializing
Pod looks Running
```

Wrong conclusion:

```text
Kubernetes networking is flaky.
```

Correct diagnosis:

```text
Traffic started before app readiness.
```

Fix:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
```

After fix:

```text
New Pod starts
NotReady while initializing
No traffic
Readiness passes
Traffic starts
No rollout 5xx
```

Memory hook:

```text
Running is not Ready.
Ready is the traffic contract.
```

---

# 21. Production Story: Bad Liveness Causes Restart Storm

A team configures:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
```

Their health endpoint includes database status.

At 10:03:

```text
Database has 2-minute failover.
```

At 10:04:

```text
All app pods report /health DOWN.
```

Kubernetes reaction:

```text
liveness fails
containers restarted
```

Now:

```text
All pods restart
connection pools reconnect at same time
DB under failover receives storm
app cold starts
customer outage extends
```

Diagram:

```text
DB failover
    |
    v
/health DOWN
    |
    v
liveness fails everywhere
    |
    v
restart storm
    |
    v
longer outage
```

Correct design:

```text
liveness endpoint:
  Does not depend on DB.

readiness endpoint:
  Can depend on DB if DB is required for traffic.
```

Then DB failover causes:

```text
Pods NotReady
No traffic to broken app
No unnecessary restarts
Recovery is calmer
```

Production lesson:

```text
Liveness should not punish the app for external dependency failure.
```

---

# 22. Production Story: Startup Probe Missing

A large Spring Boot monolith starts in 95 seconds under normal CPU.

In Kubernetes, CPU limit is low.

Cold start becomes 180 seconds.

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

Effective kill time:

```text
30 + 3 * 10 = about 60 seconds
```

But the app needs 180 seconds.

Result:

```text
Container killed before startup completes.
CrashLoopBackOff forever.
```

ASCII:

```text
App needs 180s
       |
       v
Kubernetes kills at 60s
       |
       v
App never reaches Ready
```

Fix:

```yaml
startupProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  periodSeconds: 10
  failureThreshold: 30
```

Startup window:

```text
10 * 30 = 300 seconds
```

Now:

```text
App gets time to boot.
Startup probe passes.
Normal liveness begins.
Readiness controls traffic.
```

Lesson:

```text
Slow startup is not the same as dead app.
Use startupProbe.
```

---

# 23. Production Story: Probe Timeout Too Low

A Java service has normal p99 health response of 80ms.

But during GC pause or CPU throttling, health endpoint sometimes takes 1.5 seconds.

YAML:

```yaml
timeoutSeconds: 1
failureThreshold: 1
periodSeconds: 2
```

Result:

```text
One small pause
    |
    v
Probe timeout
    |
    v
Readiness false or liveness restart
```

Symptoms:

```text
Random NotReady transitions
Intermittent restarts
No real application errors
CPU throttling metrics high
```

Debug commands:

```bash
kubectl describe pod order-service-abc
kubectl get events --sort-by=.lastTimestamp
kubectl top pod
kubectl logs order-service-abc --previous
```

Metrics to inspect:

```text
container_cpu_cfs_throttled_seconds_total
jvm_gc_pause_seconds
http_server_requests_seconds
process_cpu_usage
```

Better config:

```yaml
timeoutSeconds: 2
failureThreshold: 3
periodSeconds: 5
```

Mindset:

```text
Probes should detect real failure, not punish tiny pauses.
```

Production systems are noisy.

Probe design must be tolerant enough to avoid false positives.

---

# 24. Debugging: Pod Running But Not Ready

Symptom:

```bash
kubectl get pods
```

Output:

```text
NAME                    READY   STATUS    RESTARTS
order-service-abc       0/1     Running   0
```

Debug path:

```text
1. Describe pod.
2. Check readiness events.
3. Curl readiness endpoint from inside cluster.
4. Check app logs.
5. Check dependencies.
6. Check Actuator health details.
```

Commands:

```bash
kubectl describe pod order-service-abc

kubectl logs order-service-abc

kubectl exec -it order-service-abc -- \
  wget -qO- http://localhost:8080/actuator/health/readiness
```

If image has no shell/wget, use temporary debug pod:

```bash
kubectl run curl-debug \
  --image=curlimages/curl \
  --rm -it --restart=Never -- sh
```

Inside:

```bash
curl http://order-service:8080/actuator/health/readiness
```

Common causes:

```text
DB not reachable
Wrong DB password
Wrong Spring profile
Health endpoint not exposed
Actuator probes not enabled
App listens on different port
NetworkPolicy blocks dependency
Readiness path typo
```

Mindset:

```text
NotReady is not a random Kubernetes problem.
It is the app saying "do not send traffic yet" or probe config saying the wrong thing.
```

---

# 25. Debugging: CrashLoopBackOff With Probes

Symptom:

```text
order-service-abc   0/1   CrashLoopBackOff   7
```

First question:

```text
Is the app crashing by itself?
Or is Kubernetes killing it because liveness/startup failed?
```

Commands:

```bash
kubectl describe pod order-service-abc
kubectl logs order-service-abc
kubectl logs order-service-abc --previous
```

Look in events:

```text
Liveness probe failed
Startup probe failed
Killing container
Back-off restarting failed container
```

If you see:

```text
Liveness probe failed
```

Then ask:

```text
Was the endpoint wrong?
Was startup too slow?
Was timeout too short?
Was dependency included in liveness?
```

ASCII debug flow:

```text
CrashLoopBackOff
       |
       v
Check previous logs
       |
       v
Check describe events
       |
       +--> App exception?       Fix app/config
       |
       +--> Probe failed?        Fix probe design/timing
       |
       +--> OOMKilled?           Fix memory/limits/leak
```

Common Spring Boot causes:

```text
Missing env variable
Bad DB credentials
Flyway migration failure
Port mismatch
Container memory limit too low
Probe path not exposed
Startup probe absent
```

Never debug CrashLoopBackOff only from `kubectl get pods`.

Use `describe`, `logs --previous`, and events.

---

# 26. Debugging: Service Has No Endpoints

Symptom:

```bash
kubectl get endpoints order-service
```

Output:

```text
NAME            ENDPOINTS   AGE
order-service   <none>      10m
```

Possible causes:

```text
Service selector does not match Pod labels
Pods are NotReady
Pods are in different namespace
Container port mismatch is confusing humans
Readiness probe failing
```

Debug:

```bash
kubectl get pods --show-labels

kubectl describe svc order-service

kubectl get endpointslices \
  -l kubernetes.io/service-name=order-service
```

Mental diagram:

```text
Service selector
 app=order-service
      |
      v
Find matching pods
      |
      v
Only include Ready pods
      |
      v
Create endpoints
```

Two filters must pass:

```text
Label match
Readiness true
```

If labels match but readiness fails:

```text
Service exists
Pods exist
Endpoints empty
Traffic fails
```

This is why readiness debugging and Service debugging are connected.

---

# 27. Probe Design For Different Services

Not all services need identical probes.

## Stateless Spring Boot API

```text
startup:
  liveness endpoint with generous window

readiness:
  actuator readiness, DB required if every request needs DB

liveness:
  actuator liveness, local only
```

## Kafka Consumer Service

Readiness may mean:

```text
Consumer has joined group
Partitions assigned
Can process messages
```

But be careful.

If readiness false removes HTTP traffic but the service is not receiving HTTP traffic, readiness may not control Kafka consumption.

For worker-style apps, health checks are still useful for rollout and visibility, but traffic semantics differ.

## Batch Job

For Kubernetes Job, probes may be unnecessary or different.

A batch process should usually exit success/failure.

## Gateway / Ingress-facing API

Readiness is critical.

It should fail when:

```text
Cannot route requests
Critical config missing
Thread pool saturated beyond recovery threshold
```

## Cache service

TCP may be acceptable if the service protocol is simple, but application-level checks are better where possible.

Lesson:

```text
Probe design follows service responsibility.
Do not copy one probe template everywhere blindly.
```

---

# 28. Readiness And Dependency Checks

Should readiness check dependencies?

Answer:

```text
Only dependencies required to serve traffic.
```

Example: Order API.

If every order request needs PostgreSQL, readiness can depend on PostgreSQL.

```text
PostgreSQL down
    |
    v
order-service cannot serve orders
    |
    v
readiness DOWN
    |
    v
remove from traffic
```

But suppose recommendation service is optional.

If recommendation dependency is down, order checkout can still work.

Then readiness should not fail because recommendations are unavailable.

```text
Optional dependency down
    |
    v
degraded response possible
    |
    v
readiness should maybe stay UP
```

Mental model:

```text
Readiness asks:
  Should this Pod receive normal traffic?

Not:
  Is every external thing in the universe healthy?
```

Bad readiness:

```text
Checks DB + Redis + Kafka + email + payment + recommendation + analytics
```

One optional analytics outage removes all Pods from traffic.

Better:

```text
Critical path dependencies only.
Optional dependencies reported as details/metrics, not traffic blockers.
```

Production design:

```text
readiness = traffic safety
observability = dependency visibility
```

Do not confuse them.

---

# 29. Liveness And Dependency Checks

Liveness should almost never check external dependencies.

Why?

Because liveness failure means:

```text
Restart this container.
```

Ask:

```text
Will restarting this container fix the dependency?
```

If no, do not put it in liveness.

Examples:

```text
DB down:
  Restarting app does not fix DB.

Kafka down:
  Restarting app does not fix Kafka.

Payment provider down:
  Restarting app does not fix provider.

DNS outage:
  Restarting app may not fix DNS.
```

Good liveness checks:

```text
Can the app event loop respond?
Is the internal application state alive?
Is there a fatal unrecoverable state?
Has the main processing thread died?
```

Spring Boot liveness endpoint is usually local.

If your custom liveness includes DB, reconsider.

Memory hook:

```text
Liveness is a restart trigger.
Only fail it when restart is the right medicine.
```

Wrong medicine diagram:

```text
DB Down
  |
  v
Restart App
  |
  v
DB Still Down
  |
  v
Restart Again
```

Correct medicine:

```text
DB Down
  |
  v
Readiness DOWN
  |
  v
No traffic
  |
  v
Wait for DB recovery
```

---

# 30. Probes And Resource Limits

Probe failures often reveal resource problems.

Example:

```text
CPU limit: 250m
Spring Boot startup needs CPU burst
Probe timeout: 1s
```

During startup:

```text
JVM gets throttled
Spring responds slowly
Probe times out
Container killed
```

Diagram:

```text
Low CPU limit
    |
    v
CPU throttling
    |
    v
Slow health response
    |
    v
Probe failure
    |
    v
Restart / NotReady
```

Debug:

```bash
kubectl top pod
kubectl describe pod
kubectl logs --previous
```

Metrics:

```text
CPU throttling
Memory usage
OOMKilled
GC pause
Thread pool saturation
```

Common memory issue:

```text
Container memory limit too low
JVM exceeds limit
Kernel kills process
Pod shows OOMKilled
```

This is not a liveness failure.

It is resource sizing.

Debug event:

```text
Last State: Terminated
Reason: OOMKilled
Exit Code: 137
```

Mindset:

```text
Probe failure can be symptom.
Resource pressure can be cause.
```

Do not only tune probes.

Fix CPU/memory limits when needed.

---

# 31. Probe Endpoints Should Be Cheap

Health endpoints are called frequently.

If you have:

```text
3 probes
periodSeconds 5-10
hundreds of pods
```

That is many internal health calls.

Bad health endpoint:

```text
Runs SELECT COUNT(*) FROM huge_table
Calls 5 downstream services
Does expensive Redis scan
Performs synchronous Kafka metadata lookup
Allocates large objects
```

This can damage your own service.

Good health endpoint:

```text
Fast
Bounded timeout
No heavy queries
No unbounded external calls
No locks that can deadlock
Small response
```

For DB readiness:

```text
Use lightweight connection validation or SELECT 1 with timeout.
```

For cache:

```text
Small ping with timeout.
```

For custom checks:

```text
Use cached health state updated by background checks.
```

ASCII:

```text
kubelet probes every few seconds
        |
        v
health endpoint
        |
        +--> cheap local state       good
        |
        +--> heavy DB query          bad
        |
        +--> slow downstream chain   bad
```

Memory hook:

```text
A health check should not make the app unhealthy.
```

---

# 32. Multi-Container Pod Readiness

A Pod can contain multiple containers.

Pod Ready means:

```text
All containers that must be ready are ready.
```

Example:

```text
Pod:
  app container
  sidecar container
```

Diagram:

```text
Pod Ready?
   |
   +--> app container ready?      yes
   +--> sidecar container ready?  yes
   |
   v
Pod Ready = yes
```

If app is ready but sidecar is not:

```text
Pod Ready = false
```

Service traffic waits.

This matters with:

```text
Service mesh sidecars
Log shippers
Proxy containers
Init-like helper containers
```

Example:

```text
Spring Boot app starts quickly.
Envoy sidecar not ready.
Pod remains NotReady.
No traffic until proxy ready.
```

Mental model:

```text
Pod readiness is a combined traffic contract.
```

For service mesh:

```text
Traffic may enter through sidecar.
If sidecar is not ready, app readiness alone is insufficient.
```

Debug:

```bash
kubectl describe pod <pod>
kubectl logs <pod> -c <container-name>
```

Always specify container name when there are multiple containers.

---

# 33. Init Containers vs Startup Probe

Init containers and startup probes solve different problems.

Init container:

```text
Runs before app container starts.
Must complete successfully.
```

Startup probe:

```text
Checks whether app container has started successfully.
```

Diagram:

```text
Pod creation
   |
   v
Init container 1
   |
   v
Init container 2
   |
   v
App container starts
   |
   v
startupProbe
   |
   v
readiness/liveness
```

Use init container for:

```text
Wait for schema setup
Prepare files
Download config
Run pre-start checks
```

But be careful with "wait for dependency" init containers.

Bad:

```text
Init waits forever for DB
App never starts
Hard to observe app behavior
```

Often better:

```text
App starts
Readiness stays DOWN until DB available
```

Why?

Because modern apps should handle dependency retry.

Mental model:

```text
Init container prepares environment.
Startup probe protects app boot.
Readiness controls traffic.
```

Do not use init containers as a replacement for readiness.

---

# 34. Health Checks And HPA

Horizontal Pod Autoscaler scales based on metrics.

Readiness affects which Pods receive traffic.

Connection:

```text
If many pods become NotReady,
remaining ready pods receive more traffic.
```

This may increase load and trigger HPA.

But there is danger.

Bad readiness condition:

```text
Fail readiness when CPU > 80%
```

Then under load:

```text
Pod becomes NotReady
Traffic shifts to other pods
Other pods get overloaded
They become NotReady
Service loses endpoints
Outage
```

ASCII:

```text
High traffic
   |
   v
Pod A readiness false
   |
   v
Traffic to B/C
   |
   v
B/C overloaded
   |
   v
B/C readiness false
   |
   v
No endpoints
```

Readiness can be used for overload protection, but carefully.

Better:

```text
Use autoscaling, rate limiting, backpressure, circuit breakers.
Use readiness for severe inability to serve, not normal high load.
```

Mindset:

```text
Readiness is a sharp tool.
If every pod refuses traffic at once, the Service has nowhere to send requests.
```

---

# 35. Health Checks And PodDisruptionBudget

PodDisruptionBudget protects availability during voluntary disruptions.

Example:

```yaml
apiVersion: policy/v1
kind: PodDisruptionBudget
metadata:
  name: order-service-pdb
spec:
  minAvailable: 2
  selector:
    matchLabels:
      app: order-service
```

Meaning:

```text
During voluntary disruption, keep at least 2 available pods.
```

Available depends on readiness.

```text
Ready pods count as available.
NotReady pods do not.
```

Diagram:

```text
Replicas: 3

Pod A Ready      available
Pod B Ready      available
Pod C NotReady   not available

Available = 2
```

If another node drain tries to evict Pod A:

```text
Available would become 1
PDB blocks eviction
```

Connection:

```text
Bad readiness can block maintenance.
Good readiness protects availability.
```

Production lesson:

```text
Readiness is not just load balancing.
It affects rollout, disruption, and availability calculations.
```

---

# 36. Health Checks And Deployment Availability

Deployment status depends on readiness.

You may see:

```bash
kubectl rollout status deployment/order-service
```

Output:

```text
Waiting for deployment "order-service" rollout to finish:
  1 of 3 updated replicas are available...
```

Available means:

```text
Pod is Ready for at least minReadySeconds if configured.
```

Deployment YAML:

```yaml
spec:
  minReadySeconds: 10
```

This says:

```text
A Pod must stay Ready for 10 seconds before considered available.
```

Useful when:

```text
App becomes ready briefly then crashes
Readiness flaps
Need stability before progressing rollout
```

ASCII:

```text
Pod Ready=True
      |
      v
Wait minReadySeconds
      |
      v
Count as Available
      |
      v
Rollout continues
```

Bad readiness leads to stuck rollouts.

```text
New pods never Ready
      |
      v
Deployment does not progress
      |
      v
Old pods stay
      |
      v
Rollout timeout
```

This is good.

Kubernetes is protecting you from sending traffic to broken new version.

---

# 37. Health Check Anti-Patterns

## Anti-pattern 1: Same endpoint for everything

```text
/readiness = /health
/liveness  = /health
/startup   = /health
```

Problem:

```text
Different questions need different checks.
```

## Anti-pattern 2: Liveness checks DB

Problem:

```text
DB outage causes restart storm.
```

## Anti-pattern 3: No readiness probe

Problem:

```text
Traffic reaches app before it can serve.
```

## Anti-pattern 4: Probe timeout too aggressive

Problem:

```text
GC pause or CPU throttling causes false failures.
```

## Anti-pattern 5: Heavy health checks

Problem:

```text
Health checks create load.
```

## Anti-pattern 6: Readiness checks optional dependencies

Problem:

```text
Optional service outage removes healthy pods from traffic.
```

## Anti-pattern 7: Ignoring graceful shutdown

Problem:

```text
In-flight requests are killed during rollout.
```

## Anti-pattern 8: Copying probe settings across all services

Problem:

```text
Different apps have different startup time, dependencies, and traffic semantics.
```

Memory hook:

```text
Probe YAML is not boilerplate.
Probe YAML is production behavior.
```

---

# 38. Recommended Defaults For Spring Boot APIs

Start with this mental template, then tune with metrics.

```yaml
startupProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  periodSeconds: 10
  failureThreshold: 30
  timeoutSeconds: 2

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  periodSeconds: 5
  failureThreshold: 3
  timeoutSeconds: 2

livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  periodSeconds: 10
  failureThreshold: 3
  timeoutSeconds: 2
```

Meaning:

```text
Startup:
  allow up to 300 seconds

Readiness:
  remove from traffic after around 15 seconds of failure

Liveness:
  restart after around 30 seconds of local unhealthiness
```

Tune based on:

```text
Actual startup time p95/p99
GC pauses
CPU throttling
Dependency behavior
SLO requirements
Rolling update speed
False positive history
```

Production advice:

```text
Measure first.
Then tighten.
Do not guess from laptop startup time.
```

Laptop startup:

```text
15 seconds
```

Cluster startup under CPU limit:

```text
90 seconds
```

Production reality wins.

---

# 39. End-To-End Mental Model

Put everything together.

```text
Developer writes Deployment
        |
        v
Pod scheduled to Node
        |
        v
Kubelet starts container
        |
        v
startupProbe asks:
  "Has boot completed?"
        |
        v
If yes:
  readinessProbe asks:
    "Can receive traffic?"
  livenessProbe asks:
    "Should this container stay alive?"
        |
        v
Service uses readiness to choose endpoints
        |
        v
Users receive traffic only from Ready pods
        |
        v
If app deadlocks:
  liveness restarts it
        |
        v
If app temporarily loses DB:
  readiness removes traffic
        |
        v
If app recovers:
  readiness adds traffic back
```

One ASCII picture:

```text
             +-------------------+
             | Container Started |
             +---------+---------+
                       |
                       v
             +-------------------+
             | startupProbe      |
             | boot finished?    |
             +----+---------+----+
                  |         |
                 no        yes
                  |         v
          keep checking   +----------------------+
          until limit     | readinessProbe       |
                  |       | safe for traffic?    |
                  |       +----+------------+----+
                  |            |            |
                  v           no           yes
             restart           |            v
                               |     Service endpoint
                               |       receives traffic
                               |
                               v
                         no traffic

             +----------------------+
             | livenessProbe        |
             | restart needed?      |
             +----+------------+----+
                  |            |
                 no           yes
                  |            v
               keep app      restart container
```

This is the complete probe model.

---

# 40. Interview Questions

## What is the difference between readiness and liveness probe?

Readiness decides whether a Pod should receive traffic. If readiness fails, Kubernetes removes the Pod from Service endpoints but does not restart the container. Liveness decides whether the container should be restarted. If liveness fails repeatedly, kubelet kills and restarts the container.

## What is startup probe?

Startup probe gives slow-starting applications time to boot before liveness checks can kill them. Until startup probe succeeds, liveness and readiness failure handling is delayed. It is useful for Spring Boot apps, legacy services, or apps with long warmup.

## Should liveness check database connectivity?

Usually no. Liveness failure causes container restart. Restarting the application does not fix a database outage and can create restart storms. Database connectivity is usually a readiness concern if the app cannot serve traffic without the database.

## Why can a Pod be Running but not Ready?

Running means the container process exists. Ready means the Pod passed readiness checks and can receive traffic. A Spring Boot app can be Running while still initializing database connections, warming cache, or failing readiness.

## What happens when readiness probe fails?

The Pod Ready condition becomes false after the configured failure threshold. Kubernetes removes the Pod from Service endpoints, so normal Service traffic stops going to that Pod. The container continues running.

## What happens when liveness probe fails?

After enough consecutive liveness failures, kubelet treats the container as unhealthy, sends termination signals, and restarts it according to the Pod restart policy.

## Why is using the same endpoint for readiness and liveness risky?

Because the same endpoint may include dependency checks. If a dependency fails and liveness uses that endpoint, Kubernetes may restart the app even though the app itself is alive. Readiness and liveness have different consequences, so they usually need different health groups.

## How do probes affect rolling updates?

During a rolling update, new Pods must become Ready before they are counted as available and before Services send traffic to them. Bad readiness can cause rollout 5xx or stuck deployments. Good readiness enables safe zero-downtime rollout.

## What is `minReadySeconds`?

It is the amount of time a newly created Pod must remain Ready before Kubernetes considers it available for Deployment rollout progress. It helps catch Pods that become Ready briefly and then fail.

## How do you debug a Pod that is Running but not Ready?

Use `kubectl describe pod` to inspect readiness probe events, `kubectl logs` for app errors, call the readiness endpoint from inside the Pod or cluster, verify Actuator probe configuration, check dependencies, and inspect Service endpoints.

---

# 41. Cheat Sheet

```text
startupProbe
  Purpose:
    Protect slow startup.
  Failure action:
    Restart container if startup never succeeds.
  Use for:
    Slow Spring Boot boot, warmup, legacy apps.

readinessProbe
  Purpose:
    Control traffic.
  Failure action:
    Remove Pod from Service endpoints.
  Use for:
    DB unavailable, cache warming, app not ready, graceful drain.

livenessProbe
  Purpose:
    Restart broken container.
  Failure action:
    Kill and restart container.
  Use for:
    Deadlock, unrecoverable local app failure, hung process.
```

Field meanings:

```text
initialDelaySeconds = wait before first probe
periodSeconds       = probe interval
timeoutSeconds      = max wait per probe
failureThreshold    = failures before action
successThreshold    = successes before recovery, mainly readiness
```

Good Spring Boot endpoints:

```text
/actuator/health/liveness
/actuator/health/readiness
```

Good production rules:

```text
[ ] Always configure readiness for user-facing services.
[ ] Use startupProbe for slow Spring Boot apps.
[ ] Keep liveness local and narrow.
[ ] Do not put optional dependencies in readiness.
[ ] Do not put external dependencies in liveness.
[ ] Make health checks cheap and bounded.
[ ] Tune probe timing using production metrics.
[ ] Use graceful shutdown with readiness.
[ ] Debug with describe, logs, events, endpoints.
```

Important commands:

```bash
kubectl get pods
kubectl describe pod <pod>
kubectl logs <pod>
kubectl logs <pod> --previous
kubectl get events --sort-by=.lastTimestamp
kubectl get endpoints <service>
kubectl get endpointslices -l kubernetes.io/service-name=<service>
kubectl rollout status deployment/<name>
kubectl describe deployment <name>
```

---

# 42. One Picture To Remember

```text
                       POD LIFECYCLE HEALTH MODEL

                    +----------------------+
                    | Container Created    |
                    +----------+-----------+
                               |
                               v
                    +----------------------+
                    | startupProbe         |
                    | "Did boot finish?"   |
                    +----+------------+----+
                         |            |
                        no           yes
                         |            v
              keep waiting        +----------------------+
              until threshold     | readinessProbe       |
                         |        | "Can take traffic?" |
                         v        +----+------------+----+
                      restart          |            |
                                      no           yes
                                      |            v
                                      |     +----------------+
                                      |     | Service sends   |
                                      |     | traffic         |
                                      |     +----------------+
                                      |
                                      v
                                no traffic


                    +----------------------+
                    | livenessProbe        |
                    | "Should restart?"    |
                    +----+------------+----+
                         |            |
                        no           yes
                         |            v
                    keep running    restart container
```

Final memory hook:

```text
startupProbe   = give the app time to be born
readinessProbe = decide if users can enter
livenessProbe  = decide if the app must be reborn
```

Do not memorize probe YAML.

Remember the consequence.

```text
Readiness failure removes traffic.
Liveness failure restarts container.
Startup failure means boot never completed.
```

That is the production mental model.

---

# 43. Final Production Checklist

```text
[ ] Does every user-facing service have a readiness probe?
[ ] Does every slow-starting app have a startup probe?
[ ] Is liveness independent of external dependency outages?
[ ] Are readiness dependencies truly required for serving traffic?
[ ] Are health endpoints cheap and timeout-bounded?
[ ] Are probe timings based on real startup and latency data?
[ ] Is graceful shutdown enabled in Spring Boot?
[ ] Is terminationGracePeriodSeconds long enough for in-flight requests?
[ ] Does rolling update use readiness before sending traffic?
[ ] Can I explain Running vs Ready clearly?
[ ] Can I debug NotReady using describe/logs/endpoints?
[ ] Can I debug CrashLoopBackOff using previous logs and events?
[ ] Have I avoided restart storms caused by bad liveness checks?
```

---

# 44. Final Interview Answer In One Minute

Kubernetes health checks are not just "is the app healthy?" They are three different lifecycle decisions. Startup probe gives the container enough time to finish booting before liveness can kill it. Readiness probe decides whether the Pod should receive traffic; if it fails, the Pod is removed from Service endpoints but the container keeps running. Liveness probe decides whether the container is unrecoverably broken; if it fails repeatedly, kubelet restarts it.

For Spring Boot, I usually expose Actuator liveness and readiness endpoints. Readiness may include critical dependencies like the database if the service cannot handle requests without it. Liveness should stay local and should not depend on external systems, because dependency outages should not cause restart storms. During rolling updates, readiness prevents traffic from reaching half-started Pods and helps Kubernetes progress safely only when new Pods are available.

The production mindset is simple: startup protects boot, readiness protects traffic, and liveness protects recovery.

---

# 45. Final Memory Hook

```text
Do not ask:
  "What probe YAML should I copy?"

Ask:
  "What decision should Kubernetes make when this check fails?"
```

Then the design becomes clear.

```text
If failure means "do not send traffic":
  readinessProbe

If failure means "restart this process":
  livenessProbe

If failure means "startup never completed":
  startupProbe
```

Final sentence:

```text
Kubernetes probes are production decision points: traffic, restart, and startup protection.
```
