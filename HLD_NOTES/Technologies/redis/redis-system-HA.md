
# Redis System Design Notes (Updated with Replication & HA + Spring Boot)

## 6. Replication and High Availability (UPDATED)

### 6.1 Master-Replica Replication

In Redis, one node (master) handles writes and propagates to replicas.

#### Config
```conf
replicaof <master-ip> <master-port>
```

#### How it works
1. Replica connects
2. Master triggers BGSAVE
3. Sends RDB snapshot
4. Streams live updates

#### Trade-off
- Async replication → possible data loss
- Very low latency writes

---

### 6.2 Synchronous Replication (WAIT)

```redis
SET critical:key "value"
WAIT 2 5000
```

- Wait for 2 replicas
- Timeout: 5s

Use only for critical writes.

---

### 6.3 Redis Sentinel (Failover)

Handles:
- monitoring
- automatic failover
- service discovery

#### Config
```conf
sentinel monitor mymaster 127.0.0.1 6379 2
sentinel down-after-milliseconds mymaster 5000
sentinel failover-timeout mymaster 60000
```

#### Failover flow
1. Detect failure
2. Reach quorum
3. Elect leader
4. Promote replica
5. Redirect clients

⏱ Downtime: ~10–30s

---

### 6.4 Replication Topologies

#### Simple
```
Master → Replica1
       → Replica2
```

#### Chained
```
Master → Replica1 → Replica3
       → Replica2 → Replica4
```

#### Cross-DC
```
DC1: Master → Replica1
DC2: Replica2 → Replica3
```

---

### 6.5 Interview Answer

> I would use Redis with 1 master and 2 replicas managed by Sentinel. Writes go to master, reads can be served by replicas. Sentinel handles automatic failover. For critical writes, I’d use WAIT. This ensures HA with minimal downtime.

---

## 12.X Spring Boot (Replication + Sentinel Setup)

### application.yml (Sentinel)

```yaml
spring:
  data:
    redis:
      sentinel:
        master: mymaster
        nodes:
          - 127.0.0.1:26379
          - 127.0.0.1:26380
          - 127.0.0.1:26381
      timeout: 2000ms
```

---

### RedisConfig.java (No change needed)

Spring Boot auto-handles Sentinel via config.

---

### Example: Critical Write with WAIT

```java
public void criticalWrite(StringRedisTemplate redis) {
    redis.opsForValue().set("critical:key", "value");
    redis.execute(connection -> {
        connection.execute("WAIT", "2".getBytes(), "5000".getBytes());
        return null;
    });
}
```

---

### Example: Read from Replica

```yaml
spring:
  data:
    redis:
      client-type: lettuce
      lettuce:
        read-from: replicaPreferred
```

---

### Production Notes

- Use **3 Sentinels minimum**
- Use **replicaPreferred for reads**
- Use **WAIT only for critical paths**
- Monitor replication lag

---

## Final Upgrade

Now your Redis notes include:
- Data structures
- Persistence
- Replication (deep)
- Sentinel failover
- Spring Boot HA config

👉 This is now **production + interview level (50k RPS ready)**.
