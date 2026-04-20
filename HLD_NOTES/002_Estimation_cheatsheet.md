# System Design Estimation Notes

> Short notes for interview use: formulas, key numbers, examples, and sanity checks.

---

## Before You Start
- Not every interview needs detailed estimation.
- First check whether rough capacity planning is needed.
- Keep it **back-of-envelope**, not calculator-heavy.
- Goal: get **order-of-magnitude** numbers that guide design.

---

## Estimation Framework
1. **Users** → DAU / MAU
2. **Actions per user** → reads, writes, uploads, views
3. **QPS** → convert daily actions to requests/sec
4. **Data per request** → payload / object size
5. **Resources** → storage, bandwidth, compute

---

## Core Formulas

### QPS
```text
Average QPS = Daily Requests / 86,400
Quick math: Daily Requests / 100,000
Peak QPS = Average QPS × Peak Multiplier
```

### Read / Write Split
```text
Read QPS = Total QPS × read_ratio
Write QPS = Total QPS × write_ratio
```

### Storage
```text
Raw Storage = Records × Size × Retention
Total Storage = Raw Storage × Overhead
```

### Bandwidth
```text
Bandwidth = QPS × Data Size per Request
```

### Servers
```text
Servers Needed = Peak QPS / (Server Throughput × Target Utilization)
```

### Cache
```text
Cache Size = Hot Data × Item Size × 1.3
```

### Concurrent Users
```text
Concurrent Users ≈ DAU × (Sessions per Day × Session Duration) / Peak Window
```

---

## Numbers to Remember

### Time
- 1 day = **86,400 sec ≈ 100K**
- 1 month ≈ **2.5M sec**
- 1 year ≈ **30M sec**

### Data Sizes
- UUID = **16 bytes**
- int64 / timestamp = **8 bytes**
- URL = **100–200 bytes**
- Tweet/text record = **~1 KB**
- User profile = **1–5 KB**
- Photo = **200 KB – 2 MB**
- Short video = **5–50 MB**

### Throughput Benchmarks
- Redis = **100K+ ops/sec**
- PostgreSQL = **10K–50K QPS**
- MySQL = **10K–30K QPS**
- Kafka broker = **100K–1M msg/sec**
- App server = **1K–10K RPS**

### Latency Hierarchy
- Memory is **~1000× faster** than SSD
- SSD is **~100× faster** than HDD
- Same datacenter network = **~0.5 ms**
- Cross-region = **50–150 ms**

---

## Peak Multipliers
Use these when interviewer does not specify peak behavior.

| App Type | Peak Multiplier |
|---|---:|
| Enterprise B2B | 2–3x |
| Consumer social | 3–5x |
| E-commerce | 5–10x |
| Gaming | 3–5x |
| Streaming | 5–10x |
| News/media | 10–50x |

**Default shortcut:** use **3x** for normal consumer apps.

---

## Read vs Write Ratios

| System | Typical Ratio |
|---|---:|
| Social feed | 100:1 to 1000:1 |
| URL shortener | 100:1 |
| E-commerce catalog | 100:1 |
| Chat | 1:1 to 10:1 |
| Logging / analytics | 1:10 to 1:100 |
| User profiles | 50:1 |

**Rule:**
- High reads → cache, replicas
- High writes → sharding, async processing

---

## Storage Rules
- Always include **overhead**
- Practical total storage is often:

```text
Total = Raw Data × 4–5x
```

### Why?
- Indexes: +20–50%
- Replication: 2–3x
- Backups: +1–2x
- Fragmentation / metadata

---

## Bandwidth Rules
- Egress often dominates cost
- Internal traffic can be **5–10x** external traffic in microservices
- Media/image/video traffic usually needs **CDN**

---

## Cache Rules
- **80/20 rule**: 20% of data serves 80% of requests
- Cache **hot data**, not everything
- Use cache to reduce DB load and improve latency

---

## Worked Examples

## Example 1: Social Feed
### Assumptions
- 10M DAU
- 10 actions/user/day

### QPS
```text
Daily requests = 10M × 10 = 100M
Average QPS = 100M / 100K ≈ 1,000
Peak QPS = 1,000 × 3 = 3,000
```

### Insight
- Likely read-heavy
- Add **Redis cache**
- DB can handle writes, cache handles feed reads

---

## Example 2: URL Shortener
### Assumptions
- 100M new URLs/month
- Each record = 500 bytes

### Storage
```text
Raw/month = 100M × 500B = 50 GB
With overhead (5x) = 250 GB/month
```

### Insight
- Storage is manageable
- Read-heavy → cache hot URLs
- Simple key-value access pattern

---

## Example 3: Photo Sharing App
### Assumptions
- 5M uploads/day
- Avg photo size = 1 MB

### Storage
```text
Raw/day = 5M × 1 MB = 5 TB/day
Raw/year ≈ 1.8 PB
```

### Insight
- Media dominates storage
- Use **object storage + CDN**
- Do not store photos in relational DB

---

## Example 4: E-commerce
### Assumptions
- 50M monthly visitors
- 20 product views/session
- Black Friday = 10x normal traffic

### Insight
- Reads dominate (catalog browsing)
- Orders are much smaller than page views
- Separate:
    - **Read path** → catalog + cache + CDN
    - **Write path** → orders + payments + queue

---

## Example 5: Video Streaming
### Assumptions
- 1M concurrent viewers
- Avg bitrate = 5 Mbps

### Bandwidth
```text
Total = 1M × 5 Mbps = 5,000,000 Mbps = 5 Tbps
```

### Insight
- Massive bandwidth
- Multi-region **CDN is mandatory**
- Origin alone cannot serve this load

---

## Common Mistakes
- Using **average** instead of **peak**
- Ignoring growth
- Forgetting read amplification
- Ignoring storage overhead
- Assuming linear scaling
- Confusing throughput with latency
- Spending too long on math

---

## Interview Tips
- Round aggressively
- State assumptions clearly
- Sanity-check results
- Use powers of 10
- Show your work
- Stop once numbers are “good enough”

### Good phrasing
- “I’ll assume peak is **3x average** for a consumer app.”
- “This looks **read-heavy**, so I’d add caching.”
- “Media dominates storage, so I’d use object storage instead of DB.”

---

## Sanity Check Checklist
- Did I estimate **avg + peak QPS**?
- Did I split **reads vs writes**?
- Did I estimate **storage with overhead**?
- Did I estimate **bandwidth**?
- Did I map numbers to architecture decisions?
- Did I state my assumptions?

---

## One-Page Cheat Sheet

### Formulas
```text
Average QPS = Daily Requests / 86,400 ≈ /100K
Peak QPS = Average QPS × 3
Storage = Records × Size × Retention × 4–5x
Bandwidth = QPS × Response Size
Servers = Peak QPS / (Server Capacity × Utilization)
Cache = Hot Data × Item Size × 1.3
```

### Defaults
- Consumer peak multiplier = **3x**
- Storage overhead = **4–5x**
- Safe utilization = **60–70%**
- Hot data = **20%**
- Internal traffic amplification = **5–10x**

### Architecture Mapping
- High read QPS → **cache / replicas**
- High write QPS → **sharding / queue**
- Large media storage → **object storage**
- High bandwidth → **CDN**
- Low latency requirement → **cache + local processing**

---

## Final Takeaway
Estimation is not about perfect math. It is about turning vague scale into concrete numbers that drive architecture decisions.


---

## Added Worked Formulas and Quick Reference

## Average QPS Formula
```text
Average QPS = (Daily Active Users × Actions per User per Day) / 86,400 seconds
```

### Example
Given:
- 100 million DAU
- Each user views 50 posts per day

```text
Precise calculation:
Average QPS = (100,000,000 × 50) / 86,400
            = 5,000,000,000 / 86,400
            = 57,870 QPS

Quick estimate:
QPS = (100M × 50) / 100,000 = 50,000 QPS
```

---

## Peak QPS
```text
Peak QPS = Average QPS × Peak Multiplier
```

### Peak Multiplier Table

| Application Type | Peak Multiplier | Why |
|---|---:|---|
| Enterprise B2B | 2–3x | Business hours concentration |
| Consumer social | 3–5x | Evening usage peaks |
| E-commerce | 5–10x | Sales events, flash deals |
| Gaming | 3–5x | Weekend and evening concentration |
| Streaming | 5–10x | Popular releases, live events |
| News/media | 10–50x | Breaking news, viral content |

---

## Read:Write Ratios

| System Type | Read:Write Ratio |
|---|---:|
| Social media feed | 100:1 to 1000:1 |
| URL shortener | 100:1 |
| E-commerce catalog | 100:1 |
| Chat messaging | 1:1 to 10:1 |
| Logging/Analytics | 1:10 to 1:100 |
| User profiles | 50:1 |

### Example Read / Write Split
Given:
- Total QPS: 58,000
- Read:Write ratio = 100:1

```text
Write QPS = 58,000 / 101 ≈ 575 QPS
Read QPS = 58,000 - 575 ≈ 57,425 QPS

At peak (3x):
Peak Write QPS = 575 × 3 ≈ 1,725 QPS
Peak Read QPS = 57,425 × 3 ≈ 172,000 QPS
```

---

## Storage Formula
```text
Total Storage = Data per Record × Records per Day × Days Retained × Overhead Factor
```

### Example: URL Shortener
Given:
- 100 million new URLs shortened per month
- Each record: 500 bytes (short code + long URL + metadata)
- Retention: 5 years

```text
Monthly data = 100M × 500 bytes = 50 GB/month
Annual data = 50 GB × 12 = 600 GB/year
5 year data = 600 GB × 5 = 3 TB raw data

With 3x replication: 3 TB × 3 = 9 TB
With indexes and overhead: 9 TB × 1.3 = ~12 TB
With backups: ~15–20 TB total provisioned
```

---

## Media Storage Reference

| Media Type | Typical Size | Equivalent Text Records (500 bytes) |
|---|---:|---:|
| Profile picture | 200 KB | 400 records |
| Post image (compressed) | 500 KB – 2 MB | 1,000–4,000 records |
| Short video (1 min) | 10–50 MB | 20K–100K records |
| Long video (1 hour) | 500 MB – 2 GB | 1M–4M records |

### Example: Photo Platform
Given:
- 10 million photos uploaded per day
- Store original (2 MB) and thumbnail (200 KB)
- Retention: indefinite

```text
Daily storage = 10M × (2 MB + 0.2 MB) = 10M × 2.2 MB = 22 TB/day
Monthly = 22 TB × 30 = 660 TB/month
Annual = 660 TB × 12 ≈ 8 PB/year

After 3 years: ~24 PB
```

**Implication:** use object storage + CDN, not relational DB for images.

---

## Bandwidth Formula
```text
Bandwidth = QPS × Average Data Size per Request
```

### Example: API Service
Given:
- 50,000 QPS
- Average request: 1 KB (incoming)
- Average response: 10 KB (outgoing)

```text
Ingress = 50,000 × 1 KB = 50 MB/s = 400 Mbps
Egress = 50,000 × 10 KB = 500 MB/s = 4 Gbps

With overhead (headers, retries, ~30%):
Total egress: ~5.2 Gbps
```

---

## Video Streaming Bandwidth

| Quality | Resolution | Bitrate | 1 hour of video |
|---|---|---:|---:|
| SD | 480p | 1.5–2 Mbps | 675–900 MB |
| HD | 720p | 3–4 Mbps | 1.35–1.8 GB |
| Full HD | 1080p | 5–8 Mbps | 2.25–3.6 GB |
| 4K UHD | 2160p | 15–25 Mbps | 6.75–11.25 GB |

### Example: Concurrent Video Viewers
Given:
- 10 million concurrent viewers
- Quality distribution: 20% SD, 50% HD, 25% Full HD, 5% 4K

```text
Bandwidth calculation:
SD:    2M viewers × 2 Mbps  = 4 Tbps
HD:    5M viewers × 4 Mbps  = 20 Tbps
1080p: 2.5M viewers × 6 Mbps = 15 Tbps
4K:    0.5M viewers × 20 Mbps = 10 Tbps

Total: 49 Tbps concurrent bandwidth
```

**Implication:** CDN is mandatory.

---

## Traffic Type Optimization

| Traffic Type | Typical Size | Optimization Strategy |
|---|---|---|
| API/JSON | 1–100 KB | Compression (gzip), pagination |
| Images | 100 KB–5 MB | CDN, responsive images, WebP |
| Video | 2–25 Mbps stream | CDN, adaptive bitrate, chunked delivery |
| File downloads | 10 MB–10 GB | CDN, resumable downloads, regional mirrors |

---

## Database Server Sizing

| Database Type | Single Server Capacity | Scaling Strategy |
|---|---:|---|
| PostgreSQL (OLTP) | 10K–50K simple QPS | Read replicas, then sharding |
| MySQL | 10K–30K QPS | Read replicas, then sharding |
| MongoDB | 20K–50K ops/sec | Sharding from the start |
| Redis | 100K+ ops/sec | Cluster mode for >100K |

**Rule of thumb:**
- Read bottleneck → replicas
- Write bottleneck → sharding

---

## Cache Sizing Formula
```text
Cache Size = Hot Data Items × Item Size × Overhead Factor
```

### Example
Given:
- 100 million total users
- 20 million DAU
- User profile: 2 KB average

```text
If we cache DAU profiles:
Cache size = 20M users × 2 KB × 1.3 overhead = 52 GB
```

This fits in one large Redis instance or a small cluster.

**Result:** around 80% of profile lookups hit cache, reducing DB load significantly.

---

## Percentile Analysis
Averages lie. A system with **50 ms average latency** might still have **P99 = 500 ms**, meaning 1% of users have a much worse experience.

| Percentile | Meaning | Planning Use |
|---|---|---|
| P50 (median) | Half of requests are faster | Typical user experience |
| P90 | 90% of requests are faster | Good experience threshold |
| P95 | 95% of requests are faster | SLA target for most systems |
| P99 | 99% of requests are faster | Tail latency, often 5–10x P50 |
| P99.9 | 99.9% of requests are faster | Critical for high-volume systems |

### Example
```text
System A: Average 100 QPS, P99 is 150 QPS
System B: Average 100 QPS, P99 is 500 QPS

Both have same average, but System B needs 5x more peak capacity.
```

---

## Quick Memory Notes
- **Use /100K** for quick day-to-second QPS math
- **Default peak = 3x** for consumer apps
- **Social feeds are read-heavy**
- **Storage overhead = 4–5x raw**
- **Media dominates both storage and bandwidth**
- **Cache hot 20% to cut 80% of DB load**
- **Plan for P95/P99, not just averages**
