# Traffic Spikes in System Design – Last-Minute Notes

> A clean, interview-ready guide to handling **predictable, unpredictable, and self-inflicted traffic spikes** without losing the key trade-offs.

---

## Why Traffic Spikes Matter
A system that works perfectly at **1,000 RPS** may completely fail at **50,000 RPS**.

Why? Because spikes expose every weak point at once:
- slow DB queries get much slower
- connection pools exhaust
- retries amplify load
- caches miss all at once
- services fail in a cascade

### Core idea
```text
Spikes do not just increase load.
They expose bottlenecks + amplify failure modes.
```

---

## Where This Shows Up
- Black Friday / Prime Day
- Flash sales
- Ticket releases
- Viral tweets / trending topics
- Live sports / streaming
- Breaking news
- Product launches
- Celebrity mentions

---

# 1) Understanding Traffic Spikes

## 1.1 The 3 Types of Spikes

### A) Predictable spikes
You know they are coming.
- flash sales
- product launches
- sports finals
- scheduled marketing campaigns

### B) Unpredictable spikes
No warning.
- viral content
- breaking news
- celebrity mention
- DDoS-like external surge

### C) Self-inflicted spikes
Your system causes the problem.
- cache expiration
- retry storms
- service restarts
- cron overlap
- connection floods

### Why this matters
```text
Predictable   -> prepare ahead
Unpredictable -> detect + react automatically
Self-inflicted -> fix architecture + control retries
```

---

## 1.2 Spike Lifecycle

Traffic spikes have phases:

1. **Normal**
2. **Ramp-up**
3. **Peak**
4. **Sustained load**
5. **Decline**
6. **Recovery**

### Important insight
- **Ramp-up** is dangerous because auto-scaling has not caught up yet
- **Sustained load** kills systems slowly (threads, memory, queues, connections)
- **Recovery** is also risky because backlogs remain even after traffic falls

---

## 1.3 Why Spikes Are Dangerous

When one component overloads, everything else follows.

### Example failure chain
```text
DB slows down
-> app threads block waiting for DB
-> thread pool exhausts
-> request queue grows
-> load balancer times out
-> users retry
-> load increases again
```

### Overload math is brutal
If capacity = **10K RPS**
and incoming = **15K RPS**

```text
Extra backlog = 5K requests/sec
After 1 minute = 300K queued
After 5 minutes = 1.5M queued
```

You are not “just 50% slower” — you are falling irrecoverably behind.

---

# 2) Strategy 1: Auto-Scaling

## Idea
Automatically add/remove servers based on metrics.

### Common metrics
- CPU
- memory
- request latency
- queue depth

### Policies

| Policy | Best For |
|---|---|
| Target tracking | general steady growth |
| Step scaling | sudden variable spikes |
| Scheduled scaling | known events |
| Predictive scaling | repeatable patterns with history |

### Interview default
Use **step scaling** for spikes and **scheduled scaling** for known events.

---

## The core problem: scaling lag
Auto-scaling is slow.

### Timeline
```text
Traffic spike
-> metrics collected
-> scaling decision
-> instance launch
-> app boot
-> instance ready
```

Typical delay:
```text
3–7 minutes
```

### Key point
```text
Auto-scaling is necessary, but not enough.
It is your second line of defense.
```

### Pros
- automatic
- cost-efficient
- good for gradual growth

### Cons
- too slow for instant spikes
- cold starts hurt
- does not save you in first minutes

---

# 3) Strategy 2: Load Shedding

## Idea
Reject excess traffic early instead of letting the whole system collapse.

### Principle
```text
Fast failure > slow timeout
```

A quick `503 Retry-After` is better than letting requests hang for 30 seconds.

---

## Priority-based shedding
Not all traffic matters equally.

| Priority | Examples | Shed When |
|---|---|---|
| P0 Critical | payments, auth, checkout completion | almost never |
| P1 Important | product pages, cart, search | very high load |
| P2 Enhancement | recommendations, reviews, personalization | high load |
| P3 Background | analytics, metrics, batch work | moderate load |

### Load levels
- `<70%` → accept all
- `70–85%` → shed P3
- `85–95%` → shed P2 + P3
- `>95%` → only P0

### Example logic
```python
def should_accept_request(request):
    current_load = get_system_load()

    if current_load < 0.70:
        return True
    if current_load < 0.85:
        return request.priority <= P2
    if current_load < 0.95:
        return request.priority <= P1
    return request.priority == P0
```

### Pros
- instant protection
- prevents cascading failure
- protects core flows

### Cons
- some users get errors
- needs pre-defined priorities
- bad clients may retry aggressively

---

# 4) Strategy 3: Rate Limiting

## Idea
Control how much traffic one client can send.

### Difference from load shedding
- **Rate limiting** = fairness / abuse prevention
- **Load shedding** = system survival under overload

You need both.

---

## Common algorithms

| Algorithm | Behavior | Best For |
|---|---|---|
| Token bucket | allows short bursts | most APIs |
| Leaky bucket | smooth output | constant rate processing |
| Fixed window | simple quotas | simple systems |
| Sliding window | more accurate | stricter control |

### Best default
Use **token bucket** for user/API rate limits.

---

## Where to apply limits

| Layer | Example |
|---|---|
| Global | 100K req/sec total |
| Per-user | 100 req/min/user |
| Per-IP | 1,000 req/min/IP |
| Per-endpoint | expensive endpoints stricter |
| Per-API-key | paid tier control |

### Helpful response
```http
HTTP/1.1 429 Too Many Requests
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 0
Retry-After: 30
```

### Pros
- prevents abuse
- improves fairness
- useful even under normal traffic

### Cons
- does not solve flash crowds alone
- needs shared/distributed counters
- may frustrate legitimate heavy users

---

# 5) Strategy 4: Caching

## Why caching is your biggest leverage
A strong cache can absorb **95–99%** of reads.

### Core value
```text
Cache hit = backend never sees the request
```

---

## Cache hierarchy
```text
Browser Cache
-> CDN Edge Cache
-> App Server Cache
-> Redis / Distributed Cache
-> Database
```

| Layer | Best For | Typical TTL |
|---|---|---|
| Browser | static assets, some APIs | hours to days |
| CDN | images, videos, pages, cached APIs | minutes to hours |
| App cache | local computed/session data | seconds to minutes |
| Redis | DB query results | seconds to hours |

---

## Cache warming for predictable spikes
Before a flash sale:
1. identify hot products/pages
2. load Redis with product data
3. prime CDN with pages/assets
4. warm app caches
5. verify hit rate > 95%

### Rule
```text
Do not let a known spike hit a cold cache
```

---

## Preventing cache stampede
When a hot key expires, many requests can hit DB at once.

### Fixes
- **staggered TTLs**  
  `300 + random(60)`
- **background refresh**
- **distributed locking**

### Example
```python
def get_from_cache(key):
    value = cache.get(key)
    if value:
        return value

    if cache.acquire_lock(key, timeout=5):
        try:
            value = database.query(key)
            cache.set(key, value, ttl=300 + random.randint(0, 60))
            return value
        finally:
            cache.release_lock(key)
    else:
        sleep(0.1)
        return get_from_cache(key)
```

### Pros
- huge throughput increase
- microsecond latency
- protects DB

### Cons
- cache invalidation complexity
- stale data risk
- cold cache after restart is dangerous

---

# 6) Strategy 5: Queue-Based Load Leveling

## Idea
Separate **accepting work** from **doing work**.

```text
API accepts request quickly
-> enqueue
-> return 202 Accepted
-> workers process at sustainable rate
```

### Flow
1. request arrives
2. validate quickly
3. push to queue
4. return accepted / async status
5. workers process later

### Best for
- order processing
- email sending
- notifications
- exports / reports
- bulk operations

### Not good for
- real-time search
- interactive low-latency reads
- operations needing instant completion

---

## Queue types

| Strategy | Use |
|---|---|
| FIFO | fair normal processing |
| Priority queue | VIP / premium traffic |
| Delay queue | retries, scheduled work |
| Dead letter queue | failed messages |

---

## Backpressure on the queue
Queues are not infinite.

### Queue depth response
- `<50%` → normal
- `50–80%` → alert
- `80–95%` → slow acceptance / scale workers
- `>95%` → reject new work with `503 Retry-After`

### Pros
- absorbs bursts naturally
- smooths DB load
- survives worker crashes

### Cons
- async user experience
- more infrastructure
- must monitor backlog carefully

---

# 7) Strategy 6: Graceful Degradation

## Idea
When overloaded, disable non-critical features to preserve core flows.

### Feature priority matrix

| Level | Examples |
|---|---|
| P0 Critical | checkout, payment, auth |
| P1 Important | search, product pages, cart |
| P2 Enhancement | recommendations, reviews, personalization |
| P3 Optional | analytics, A/B tests, non-critical integrations |

---

## Degradation levels

| Load | Action |
|---|---|
| Normal | everything on |
| Elevated | disable P3 |
| High | disable P2 + P3 |
| Critical | only P0 + P1 |
| Survival | only P0 |

### Example fallback
```python
def get_recommendations(user_id):
    try:
        return recommendation_service.get(user_id, timeout=0.1)
    except TimeoutException:
        return POPULAR_ITEMS_FALLBACK
    except ServiceUnavailable:
        return []
```

### Pros
- partial service is better than full outage
- protects revenue-critical paths
- buys time for scaling

### Cons
- more code paths to test
- features disappear under stress
- must plan ahead

---

# 8) Strategy 7: Database Protection

## Why DB protection matters
The database is often the hardest thing to scale quickly during a spike.

---

## Connection pooling
Without pooling, app servers can open too many DB connections.

### Use
- PgBouncer
- ProxySQL
- DB-side pooling

### Why
```text
100 app servers × 10 connections = 1000 DB connections
Pooler can reduce this to a safe number
```

---

## Read replicas
Separate reads from writes:
- writes → primary
- reads → replicas

This increases read capacity and protects the primary.

---

## Circuit breakers
When DB is failing, stop sending more requests.

### States
- **Closed** → normal
- **Open** → reject calls, serve cache/fallback
- **Half-open** → test if DB is healthy again

### Why
```text
Do not hammer a struggling database
```

---

## Query timeouts
Kill slow queries before they pile up.

### Rule
- set max query timeout (e.g. 5s)
- return cached/fallback data if timeout
- never wait forever

---

# 9) Strategy 8: Pre-Warming and Capacity Planning

## Capacity planning example
```text
Expected peak traffic:   100,000 RPS
Current capacity:         20,000 RPS
Safety buffer:             1.5x
Required capacity:       150,000 RPS
Scale factor:              7.5x
```

That drives:
- app servers
- read replicas
- cache memory
- queue throughput
- CDN warm-up scope

---

## Prep timeline

### Day -7
- estimate peak
- plan capacity
- order resources
- identify worst-case scenario

### Day -3
- scale infra
- add replicas
- grow cache cluster
- verify auto-scaling

### Day -1
- warm caches
- prime CDN
- run load tests
- prepare static fallbacks

### Hour -2
- war room ready
- escalation paths reviewed
- dashboards checked
- communication channels tested

### Event start
- monitor continuously
- keep feature flags ready
- scale further if needed
- document issues

---

## Scheduled scaling
For known spikes, do not wait for auto-scaling.

### Example
- Event -2h → scale to 10x
- Event start → hold capacity
- Event +1h → evaluate
- Event +4h → gradual scale-down
- Event +24h → back to normal

### Rule
```text
For predictable spikes, pre-scale first.
Auto-scaling is backup, not primary defense.
```

---

# 10) Putting It All Together

## Defense-in-depth architecture
```text
CDN
-> Browser/App Cache
-> Rate Limiter
-> Load Shedder
-> App Servers with Auto-Scaling
-> Queue for async writes
-> Graceful Degradation / Fallbacks
-> DB with Pooling + Replicas + Circuit Breakers
```

## Mental model
```text
Absorb -> Control -> Shed -> Degrade -> Protect -> Recover
```

---

# 11) Interview Answer Template

```text
To handle traffic spikes, I’d first classify the spike as predictable, unpredictable, or self-inflicted, because the response strategy differs.

For predictable spikes, I’d pre-scale capacity, warm caches, prime the CDN, and have the team and runbooks ready.
For unpredictable spikes, I’d rely on strong caching, rate limiting, auto-scaling, and load shedding to survive the first few minutes.
For self-inflicted spikes like cache stampedes or retry storms, I’d fix the architecture with staggered TTLs, distributed locking, exponential backoff, and jitter.

I’d protect the database aggressively using connection pooling, read replicas, query timeouts, and circuit breakers.
For write-heavy paths, I’d use queues to absorb bursts and process work at a sustainable rate.
Finally, I’d implement graceful degradation so that under extreme load we preserve checkout, auth, and payments while turning off non-critical features.
```

---

# 12) Polished Key Takeaways

- **Traffic spikes expose every weakness at once**  
  Design for your worst day, not your average day.

- **Classify the spike correctly**  
  Predictable spikes need preparation, unpredictable spikes need automation, and self-inflicted spikes need architectural fixes.

- **Auto-scaling is necessary but too slow by itself**  
  New servers take minutes, while overload happens in seconds.

- **Load shedding protects the system**  
  It is better to reject some traffic quickly than let everyone time out slowly.

- **Rate limiting preserves fairness**  
  It stops a few clients from consuming more than their share, even before the system is overloaded.

- **Caching is your highest-leverage defense**  
  A warm cache can absorb the vast majority of read traffic and keep your backend alive.

- **Queues level write-heavy spikes**  
  Accept work fast, process it steadily, and use backpressure when queues get too deep.

- **Graceful degradation keeps the business alive**  
  Disable optional features to preserve critical ones like auth, checkout, and payments.

- **Protect the database like a scarce resource**  
  Pool connections, route reads to replicas, time out bad queries, and use circuit breakers.

- **Preparation wins for known events**  
  Capacity planning, pre-warming, scheduled scaling, and clear runbooks are what separate survival from outage.

- **Real resilience comes from layers, not one trick**  
  Use CDN + cache + rate limiting + load shedding + queues + graceful degradation together.

---

## Final 1-Line Shortcut
```text
Pre-scale -> Cache -> Rate Limit -> Shed -> Queue -> Degrade -> Protect DB
```
