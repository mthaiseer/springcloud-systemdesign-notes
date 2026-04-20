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
