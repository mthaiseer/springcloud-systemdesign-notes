# System Design Estimation – Ultra Quick Notes

> Minimal formulas + short worked examples for interviews

---

## 🧮 Core Formula Chain

```text
Daily Requests = DAU × Actions per User per Day

Average QPS = Daily Requests / 86,400
            ≈ Daily Requests / 100,000

Peak QPS = Average QPS × Peak Multiplier
(default: 3x for consumer apps)
```

---

## ⚡ Example 1: Social Feed QPS

Given:
- 100M DAU
- 50 actions/user/day

```text
Daily Requests = 100M × 50 = 5B

Average QPS (precise) = 5B / 86,400 ≈ 57,870
Quick QPS ≈ 5B / 100K = 50,000

Peak QPS ≈ 50K × 3 = 150K
```

**Takeaway:** high read traffic → add cache + read replicas

---

## 📊 Read vs Write Split

```text
Write QPS = Total QPS / (Read:Write ratio + 1)
Read QPS = Total QPS - Write QPS
```

### Example 2: 100:1 Read/Write Ratio
Given:
- Total QPS = 58,000
- Read:Write = 100:1

```text
Write QPS = 58,000 / 101 ≈ 575
Read QPS = 58,000 - 575 ≈ 57,425

Peak Write QPS ≈ 575 × 3 = 1,725
Peak Read QPS ≈ 57,425 × 3 ≈ 172,000
```

**Takeaway:** reads scale with cache/replicas, writes are harder

---

## 💾 Storage

```text
Total Storage = Records × Size × Retention × Overhead
Use 4–5x overhead for indexes + replicas + backups
```

### Example 3: URL Shortener Storage
Given:
- 100M new URLs/month
- 500 bytes/record
- 5 years retention

```text
Monthly data = 100M × 500B = 50GB
Yearly = 50GB × 12 = 600GB
5 years = 600GB × 5 = 3TB raw

With overhead ≈ 15–20TB provisioned
```

**Takeaway:** raw storage looks small, total provisioned is much bigger

---

## 🖼️ Example 4: Photo App Storage
Given:
- 10M photos/day
- Original = 2MB
- Thumbnail = 0.2MB

```text
Daily = 10M × 2.2MB = 22TB/day
Monthly ≈ 660TB
Yearly ≈ 8PB
```

**Takeaway:** media dominates → use object storage + CDN

---

## 🌐 Bandwidth

```text
Bandwidth = QPS × Response Size
```

### Example 5: API Bandwidth
Given:
- 50K QPS
- 10KB average response

```text
50K × 10KB = 500MB/s ≈ 4Gbps
With overhead ≈ 5Gbps
```

**Takeaway:** even “small” APIs can need multi-Gbps networking

---

## 🎥 Example 6: Video Streaming
Given:
- 10M concurrent viewers
- Mixed quality traffic

```text
SD:   2M × 2 Mbps  = 4 Tbps
HD:   5M × 4 Mbps  = 20 Tbps
FHD:  2.5M × 6 Mbps = 15 Tbps
4K:   0.5M × 20 Mbps = 10 Tbps

Total ≈ 49 Tbps
```

**Takeaway:** CDN is mandatory for video delivery

---

## 🧠 Key Defaults

- Peak multiplier = **3x**
- Storage overhead = **4–5x**
- Read-heavy → **cache**
- Write-heavy → **shard / queue**
- 80/20 rule → cache hot data

---

## 🎯 One-Line Strategy

```text
DAU → Actions → Daily Requests → Average QPS → Peak QPS
→ Read/Write Split → Storage → Bandwidth → Architecture
```

---

## ✅ Quick Mapping

- High read QPS → cache, replicas
- High write QPS → sharding, async queue
- Large media storage → object storage
- High bandwidth → CDN
- Tight latency → cache + avoid cross-region calls

---

💡 Goal: **Order of magnitude, not perfect math**
