# Leader Election in Distributed Systems – One-Stop Reference

> Concise but practical notes on **leader election**, **split-brain prevention**, **Raft**, **ZooKeeper/etcd/Kafka/Redis Sentinel**, and **working Spring Boot / SQL-style examples** you can implement from scratch.

---

## Why Leader Election Matters

In many distributed systems, **all replicas can serve reads, but only one should accept writes**.

If:
- **multiple nodes accept writes** -> conflicting data, corruption
- **no node accepts writes** -> system unavailable

So the system needs:

```text
Exactly one leader at a time
```

Not zero. Not two. Exactly one.

---

# 1) What Is Leader Election?

Leader election is the process of choosing **one node as leader** and making all others followers.

The leader usually:
- coordinates writes
- serializes ordered operations
- assigns work
- manages cluster-wide decisions

Followers:
- replicate state
- defer critical decisions to the leader

---

## Core guarantees

A correct leader election mechanism should provide:

### Safety
```text
At most one leader at a time
```

### Liveness
```text
If the current leader fails, a new leader is eventually elected
```

These two are the heart of leader election.

---

# 2) Why Distributed Systems Need a Leader

Not all systems need one, but many do.

## Benefits

### 1. Simpler coordination
Without a leader:
- every node must coordinate with every other node
- communication grows roughly as **O(n²)**

With a leader:
- followers only coordinate with the leader
- communication becomes roughly **O(n)**

### 2. Stronger consistency
For ordered operations:
- one authority decides
- less chance of conflicting writes

### 3. Fewer conflicts
In leader-based databases:
- one node writes
- replicas apply changes in order
- conflict resolution becomes easier or unnecessary

---

# 3) The Split-Brain Problem

This is the most dangerous failure mode.

## What is split-brain?
Split-brain happens when **two nodes both believe they are leader**.

That can happen due to:
- network partitions
- slow heartbeats
- clock skew
- bugs
- stale lease holders continuing to write

## Why it is dangerous
Example:
- Leader A writes `X=1`
- Leader B writes `X=2`

Now two “authoritative” values exist.

This can cause:
- data corruption
- diverging replicas
- lost writes
- very difficult recovery

### Rule
```text
The real challenge of leader election is preventing split-brain.
```

---

# 4) Common Leader Election Algorithms

# 4.1 Bully Algorithm

## Idea
The node with the **highest ID wins**.

## How it works
1. A node notices leader failure
2. It sends `ELECTION` messages to higher-ID nodes
3. If no higher node responds, it becomes leader
4. If a higher node responds, that node continues the election
5. The winner sends `COORDINATOR` message to everyone

## Pros
- simple
- deterministic
- easy to explain

## Cons
- many messages in worst case: **O(n²)**
- highest ID always wins
- weak under partitions
- uncommon in modern production systems

## Best for
- teaching / interviews
- simple controlled systems
- not ideal for production critical infrastructure

---

# 4.2 Ring Algorithm

## Idea
Nodes form a logical ring.
An election message circulates around the ring collecting live node IDs.

## How it works
1. Node starts election with its own ID
2. Message is passed to next node
3. Each node appends its ID
4. When it returns, highest ID wins

## Pros
- fewer messages than bully: **O(n)**
- simple logical structure

## Cons
- slower
- one failed node can complicate the ring
- still weak under partitions
- not common in production critical systems

---

# 4.3 Consensus-Based Election (Raft)

This is the modern practical answer.

## Why Raft matters
Raft is used in:
- etcd
- CockroachDB
- TiKV
- many coordination systems

It is designed to be:
- understandable
- safe under partitions
- practical for production

---

## Raft states

Each node is always in one of:

- **Follower**
- **Candidate**
- **Leader**

---

## Raft leader election flow

1. Follower waits for heartbeat from leader
2. If heartbeat timeout expires, it becomes **Candidate**
3. Candidate increments **term**
4. Candidate requests votes from all nodes
5. Each node votes for **at most one candidate per term**
6. Candidate with **majority** becomes leader
7. New leader starts heartbeats immediately

---

## Why Raft prevents split-brain

### Majority quorum
A leader must get votes from a **majority** of nodes.

In a 5-node cluster:
- majority = 3

If network partitions happen:
- one partition with 3 nodes can elect leader
- partition with 2 nodes cannot

So:
```text
At most one partition can elect a leader
```

That is the key safety property.

---

## Pros
- formally reasoned and widely trusted
- prevents split-brain with quorum
- production proven

## Cons
- more complex than bully/ring
- needs majority available
- not ideal for extremely large consensus groups

---

# 4.4 Lease-Based Leader Election

## Idea
Instead of electing a leader forever, give a **time-limited lease**.

The leader must renew the lease before expiry.

If it fails:
- lease expires
- another node can acquire leadership

---

## Pros
- simpler operationally
- bounded failure detection
- common with coordination systems (ZooKeeper, etcd, Consul)

## Cons
- sensitive to clock skew
- stale leaders may still think they are valid
- requires fencing tokens to be truly safe

---

# 5) The Clock Skew Problem

Lease systems are dangerous if clocks differ.

Example:
- coordination service says lease expired
- new leader gets lease
- old leader’s clock runs slow and still thinks lease is valid

Now both can act like leader.

---

# 6) Fencing Tokens

## What are they?
A fencing token is a **monotonically increasing number** attached to leadership.

Every time leadership changes:
- new leader gets a higher token

Every resource (DB, storage, lock target) must reject operations with **older tokens**.

### Example
- old leader token = 5
- new leader token = 6
- storage has already seen token 6
- storage rejects token 5 writes

## Why fencing matters
Even if an old leader is slow, partitioned, or clock-skewed:
- it cannot corrupt state
- downstream systems reject stale writes

### Rule
```text
If you use lease-based leadership, always add fencing tokens.
```

---

# 7) How Real Systems Handle Leader Election

# 7.1 ZooKeeper

ZooKeeper commonly uses **sequential ephemeral znodes**.

## Pattern
Each node creates:
```text
/election/node_0001
/election/node_0002
/election/node_0003
```

The node with the **lowest sequence number** is leader.

## Why it works
- **ephemeral** node disappears if session dies
- **sequential** node gives deterministic ordering
- followers usually watch only their predecessor, reducing herd effect

## Used by
- Kafka (legacy controller election)
- HBase
- Hadoop components

---

# 7.2 etcd / Kubernetes

etcd uses **Raft internally**.

Kubernetes uses etcd-backed leases for:
- kube-scheduler leader election
- kube-controller-manager leader election
- other HA control-plane components

## Pattern
- one instance acquires lease
- others remain standby
- if leader fails, standby tries to take lease

---

# 7.3 Kafka

Kafka has a cluster controller (now using **KRaft** instead of ZooKeeper in modern setups).

The controller manages:
- partition leadership
- broker changes
- metadata updates

Only one controller should be active.

---

# 7.4 Redis Sentinel

Redis Sentinel performs failover with a voting/quorum model.

Flow:
1. Sentinels monitor current primary
2. If quorum agrees primary is down, failover begins
3. Sentinels elect a failover leader
4. One replica is promoted to new primary
5. clients/replicas are redirected

This is effectively a **two-level election**:
- Sentinel leader election
- Redis primary selection

---

# 8) Common Failure Scenarios

# 8.1 Network Partitions
Most important case.

Question:
```text
What if the old leader is still alive but cannot talk to the majority?
```

Correct behavior in quorum systems:
- old leader loses majority
- cannot safely continue writing
- majority elects new leader

---

# 8.2 Flapping Leaders
Leader changes too often.

Causes:
- too-short timeouts
- network jitter
- overloaded leader
- heartbeat delays

### Fixes
- heartbeat interval much smaller than election timeout
- election timeout typically **5–10x** heartbeat
- randomized election timeouts
- backoff before re-election

---

# 8.3 Long Elections
If elections take too long:
- system may be unavailable during transition

### Fix
Use **randomized timeouts** so all nodes do not start elections simultaneously.

This reduces:
- split votes
- repeated election rounds

---

# 8.4 Byzantine Failures
Normal leader election usually assumes nodes are fail-stop:
- they either work correctly
- or crash

Byzantine failures mean:
- nodes lie
- send conflicting messages
- behave maliciously

Need BFT algorithms such as:
- PBFT
- HotStuff

These are much more complex and expensive.
Most internal enterprise systems do **not** need BFT.

---

# 9) Best Practices

## 1. Use existing systems
Do not build consensus from scratch unless you absolutely must.

Use:
- ZooKeeper
- etcd
- Consul
- K8s Lease API
- database advisory locks (for simpler cases)

---

## 2. Stop leader work immediately on leadership loss
The most dangerous bug:
```text
old leader continues acting after leadership is lost
```

That must stop immediately.

---

## 3. Always use fencing for external side effects
Especially if leader writes to:
- DB
- storage
- queues
- external APIs with side effects

---

## 4. Monitor elections
Track:
- number of elections
- election duration
- current leader
- heartbeat health
- term / epoch changes
- lease renewal failures

Alert on:
- frequent leader changes
- unusually long election times
- multiple nodes claiming leadership

---

## 5. Test failure modes
Regularly test:
- leader crash
- slow leader
- network partition
- delayed heartbeats
- stale leader recovery

Tools:
- Toxiproxy
- Chaos Monkey
- Pumba
- Kubernetes chaos tooling

---

# 10) Leader-Based vs Leaderless

| Factor | Leader-Based | Leaderless |
|---|---|---|
| Consistency | easier strong consistency | usually eventual |
| Availability during elections | reduced | higher |
| Write throughput | bounded by leader | scales horizontally |
| Conflict handling | simpler | harder |
| Examples | Kafka, MySQL primary-replica, etcd | Cassandra, DynamoDB, Riak |

## Use leader-based when
- strict ordering matters
- strong consistency is required
- conflict resolution is expensive
- one writer is easier to reason about

## Use leaderless when
- availability is more important
- eventual consistency is acceptable
- write throughput must scale horizontally

---

# 11) Quick Reference

## Best practical choices by use case

| Use Case | Good Choice |
|---|---|
| database primary election | Raft / etcd / ZooKeeper |
| Kubernetes app leadership | Lease API / Spring leader election wrapper |
| simple single-DB app | DB advisory lock or lock table |
| Redis HA | Redis Sentinel |
| production coordination platform | etcd / ZooKeeper / Consul |

---

## One-line summary by algorithm

| Algorithm | Quick Take |
|---|---|
| Bully | simple but weak for production |
| Ring | message-efficient but slow |
| Raft | modern production standard |
| Lease-based | practical, but add fencing |
| ZooKeeper ephemeral-sequential | classic coordination pattern |

---

# 12) Spring Boot Implementations

Below are **implementable examples** from simple to production-style.

---

# 12.1 Simple DB-backed Leader Election (from scratch)

This is useful for:
- cron/job leader election
- one active scheduler among many app instances
- simple internal systems

It is **not** a full Raft replacement, but very practical.

## SQL table
```sql
CREATE TABLE leader_lock (
    lock_name VARCHAR(100) PRIMARY KEY,
    owner_id VARCHAR(100) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    fencing_token BIGINT NOT NULL
);
```

---

## JPA Entity
```java
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "leader_lock")
public class LeaderLockEntity {

    @Id
    private String lockName;

    private String ownerId;

    private Instant expiresAt;

    private Long fencingToken;

    public String getLockName() { return lockName; }
    public void setLockName(String lockName) { this.lockName = lockName; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public Long getFencingToken() { return fencingToken; }
    public void setFencingToken(Long fencingToken) { this.fencingToken = fencingToken; }
}
```

---

## Repository
```java
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaderLockRepository extends JpaRepository<LeaderLockEntity, String> {
}
```

---

## Simple leader election service
```java
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class DbLeaderElectionService {

    private final LeaderLockRepository repository;
    private final String nodeId = UUID.randomUUID().toString();

    private volatile boolean isLeader = false;
    private volatile long fencingToken = -1L;

    public DbLeaderElectionService(LeaderLockRepository repository) {
        this.repository = repository;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public long getFencingToken() {
        return fencingToken;
    }

    @Scheduled(fixedRate = 3000)
    @Transactional
    public void tryAcquireOrRenewLeadership() {
        Instant now = Instant.now();
        Instant newExpiry = now.plusSeconds(10);

        LeaderLockEntity lock = repository.findById("primary-writer").orElse(null);

        if (lock == null) {
            LeaderLockEntity newLock = new LeaderLockEntity();
            newLock.setLockName("primary-writer");
            newLock.setOwnerId(nodeId);
            newLock.setExpiresAt(newExpiry);
            newLock.setFencingToken(1L);
            repository.save(newLock);

            isLeader = true;
            fencingToken = 1L;
            return;
        }

        boolean lockExpired = lock.getExpiresAt().isBefore(now);
        boolean ownedByMe = nodeId.equals(lock.getOwnerId());

        if (ownedByMe) {
            lock.setExpiresAt(newExpiry);
            repository.save(lock);

            isLeader = true;
            fencingToken = lock.getFencingToken();
            return;
        }

        if (lockExpired) {
            lock.setOwnerId(nodeId);
            lock.setExpiresAt(newExpiry);
            lock.setFencingToken(lock.getFencingToken() + 1);
            repository.save(lock);

            isLeader = true;
            fencingToken = lock.getFencingToken();
            return;
        }

        isLeader = false;
    }
}
```

---

## Controller
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leader")
public class LeaderController {

    private final DbLeaderElectionService service;

    public LeaderController(DbLeaderElectionService service) {
        this.service = service;
    }

    @GetMapping("/status")
    public String status() {
        return service.isLeader()
                ? "LEADER token=" + service.getFencingToken()
                : "FOLLOWER";
    }
}
```

---

## Important note
This example is useful for:
- internal active-passive jobs
- one-writer scheduler pattern

But for stronger guarantees under real partitions:
- use etcd / ZooKeeper / Consul / K8s Lease

---

# 12.2 Fencing Token Storage Protection Example

This is the critical piece many implementations miss.

## Example protected storage service
```java
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class FencedStorageService {

    private final AtomicLong highestTokenSeen = new AtomicLong(0);
    private final ConcurrentHashMap<String, String> store = new ConcurrentHashMap<>();

    public synchronized boolean write(long fencingToken, String key, String value) {
        long current = highestTokenSeen.get();

        if (fencingToken < current) {
            return false; // stale leader rejected
        }

        highestTokenSeen.set(fencingToken);
        store.put(key, value);
        return true;
    }

    public String read(String key) {
        return store.get(key);
    }
}
```

---

## Leader-only write endpoint
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/writes")
public class LeaderWriteController {

    private final DbLeaderElectionService electionService;
    private final FencedStorageService storageService;

    public LeaderWriteController(DbLeaderElectionService electionService,
                                 FencedStorageService storageService) {
        this.electionService = electionService;
        this.storageService = storageService;
    }

    @PostMapping
    public String write(@RequestParam String key, @RequestParam String value) {
        if (!electionService.isLeader()) {
            return "NOT_LEADER";
        }

        boolean ok = storageService.write(electionService.getFencingToken(), key, value);
        return ok ? "OK" : "STALE_LEADER_REJECTED";
    }
}
```

This demonstrates:
- leader-only action
- fencing-based stale write rejection

---

# 12.3 Spring Scheduling + Leadership Listener Pattern

A nice application pattern is:
- only the leader runs scheduled jobs
- followers stay idle

## Example scheduled leader-only job
```java
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LeaderOnlyJob {

    private final DbLeaderElectionService electionService;

    public LeaderOnlyJob(DbLeaderElectionService electionService) {
        this.electionService = electionService;
    }

    @Scheduled(fixedRate = 5000)
    public void runIfLeader() {
        if (!electionService.isLeader()) {
            return;
        }

        System.out.println("Running leader-only job");
    }
}
```

This is a very common real-world use case.

---

# 12.4 Kubernetes Lease API (practical production direction)

If deploying on Kubernetes, prefer K8s-native leader election rather than inventing your own.

Common libraries:
- Spring Cloud Kubernetes
- Fabric8 Kubernetes client
- direct Lease resource usage

## High-level idea
- app instances compete for a Lease object
- only one holds it
- leader renews periodically
- if renewal stops, another instance acquires it

### Example concept
```text
Lease name: my-service-leader
HolderIdentity: pod-abc
LeaseDurationSeconds: 15
RenewTime: current timestamp
```

For production K8s systems, this is usually the cleanest path.

---

# 12.5 SQL-Only Try-Acquire Query Pattern

If you want a direct SQL approach, here is the pattern.

## Try acquire expired or missing lock
PostgreSQL style:
```sql
INSERT INTO leader_lock(lock_name, owner_id, expires_at, fencing_token)
VALUES ('primary-writer', 'node-a', NOW() + INTERVAL '10 seconds', 1)
ON CONFLICT (lock_name)
DO UPDATE
SET owner_id = EXCLUDED.owner_id,
    expires_at = EXCLUDED.expires_at,
    fencing_token = leader_lock.fencing_token + 1
WHERE leader_lock.expires_at < NOW();
```

If row updated/inserted:
- lock acquired

If no row updated:
- someone else still owns valid lock

---

# 13) Minimal Spring Boot App Setup

## Maven dependencies
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

## Main app
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LeaderElectionApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeaderElectionApplication.class, args);
    }
}
```

## application.properties
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
```

---

# 14) Practical Recommendation

## If you need something simple and internal
Use:
- DB lock table
- lease + fencing
- one active node pattern

## If you need production-grade distributed coordination
Use:
- etcd
- ZooKeeper
- Consul
- Kubernetes Lease API

## If you need replicated storage leader election
Use:
- Raft-based system
- not custom bully/ring

---

# 15) Quick Reference Cheat Sheet

## Do this
- use quorum or leases
- always add fencing for writes
- stop leader work immediately on leadership loss
- randomize election timeout
- monitor election churn
- chaos test network failures

## Avoid this
- two leaders writing to same data
- lease without fencing
- building your own consensus lightly
- continuing leader tasks after lease loss
- assuming “leader crash” is the only failure mode

---

# 16) Final Summary

- **Leader election chooses exactly one coordinator**  
  One leader simplifies consistency and serialization.

- **Split-brain is the main danger**  
  The hardest part is preventing two nodes from acting as leader.

- **Raft is the practical gold standard**  
  Majority quorum ensures only one partition can elect a leader.

- **Lease-based leadership is common and useful**  
  But leases alone are not enough—add fencing tokens.

- **Real systems rely on battle-tested coordinators**  
  ZooKeeper, etcd, Consul, Kubernetes Lease API.

- **Leadership loss must stop work immediately**  
  This is essential to avoid stale leaders corrupting data.

- **Always test failure scenarios**  
  Crashes, slow nodes, network partitions, delayed messages.

---

## Final 1-Line Shortcut
```text
Simple internal active-passive -> DB lease + fencing | Real distributed production control plane -> Raft / etcd / ZooKeeper / K8s Lease
```
