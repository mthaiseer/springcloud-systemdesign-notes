# System Design Cheat Sheet – High Reads + Fanout (Ultra Short)

---

## Core Idea
```text
Read-heavy systems (100:1+)
→ optimize reads, not writes
```

---

# High Read Traffic

## Strategy Order
```text
Cache → CDN → Replicas → Optimize → Precompute
```

## Caching (BIGGEST IMPACT)
- 90% hit → 10x DB reduction

```python
data = redis.get(key)
if not data:
    data = db.query(...)
    redis.setex(key, 3600, data)
```

### Invalidation
- TTL → simple (default)
- Write-through → strong consistency
- Event-based → scalable systems

---

## Read Replicas
```text
Writes → Primary
Reads  → Replicas
```

```python
if recent_write:
    use_primary()
else:
    use_replica()
```

- Async (common) → fast but stale
- Sync → consistent but slower

---

## CDN (Latency Killer)
```text
User → Edge (20ms) vs Origin (150ms)
```

```http
Cache-Control: public, max-age=60, s-maxage=300
```

Use for:
- static files
- cached APIs

---

## DB Optimization

### Index
```sql
CREATE INDEX idx_customer_status_date
ON orders(customer_id, status, created_at DESC);
```

### Fixes
- slow → index
- joins → denormalize
- large → LIMIT
- aggregates → precompute

---

## Pre-computation
```text
Compute on write → fast reads
```

```sql
CREATE MATERIALIZED VIEW sales AS
SELECT product_id, SUM(amount)
FROM orders GROUP BY product_id;
```

---

# Fanout

## Core
```text
1 write → N users
```

---

## Push (Fanout-on-Write)
```text
Post → write to all feeds
```
✔ fast reads  
✖ expensive writes (celebrity problem)

---

## Pull (Fanout-on-Read)
```text
Read → fetch + merge posts
```
✔ cheap writes  
✖ slow reads

---

## Hybrid (REAL SYSTEMS)
```text
Normal → Push
Celebrities → Pull
```

---

## Fanout Flow
```text
Post → Queue → Workers → Feed Cache
```

---

# Final Cheat Sheet

```text
Reads:
Cache → CDN → Replicas → Optimize → Precompute

Fanout:
Push = fast read
Pull = fast write
Hybrid = production
```
## 12. Key Takeaways

- **Most systems are read-heavy**  
  Design for reads first, then optimize writes.  
  Typical ratio: `Read:Write > 100:1`

- **Caching is your biggest lever**  
  A `90% cache hit rate → ~10x reduction` in DB load. Start here.

- **Use the full caching hierarchy**  
- Browser → CDN → App Cache → DB Buffer Pool → Database

Each layer reduces load on the next.

- **Read replicas scale linearly**  
- 1 replica ≈ 10K QPS
  3 replicas ≈ 30K QPS

Always plan for **replication lag**.

- **CDN is not just for images**  
  Cache API responses at the edge for global low latency.  
  Use **edge computing** for personalization.

- **Pre-compute expensive queries**  
  Move work from **read time → write time** when possible.

- **Every technique has trade-offs**
- Staleness
- Complexity
- Cost  
  Choose based on requirements.

- **Measure everything**  
  Track:
- Cache hit rate
- Latency percentiles (P50, P95, P99)
- Database load  

