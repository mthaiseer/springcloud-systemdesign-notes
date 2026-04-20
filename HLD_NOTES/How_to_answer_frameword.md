# 🧠 System Design Interview Guide

> A concise, practical framework + worked example to start from scratch in any system design interview.

---

## 🚀 How to Use This
1. Follow the **7 phases** in order
2. Speak your thoughts out loud
3. Keep moving (don’t get stuck)

---

# 🧩 The 7-Phase Framework

| Phase | Goal | Time |
|------|------|------|
| 1. Requirements | Define problem | 5–7 min |
| 2. Estimation | Understand scale | 3–5 min |
| 3. API Design | Define interfaces | 3–5 min |
| 4. High-Level | Architecture | 8–10 min |
| 5. Database | Data model | 5–7 min |
| 6. Deep Dive | Show expertise | 12–18 min |
| 7. Wrap-Up | Improve & conclude | 3–5 min |

---

# 1️⃣ Requirements

### Ask
- Core features?
- Users?
- Scale?
- Latency?
- Consistency?

### Example (Instagram)
- Post photo
- Follow users
- View feed
- ❌ Exclude messaging/search

👉 Output: **Clear scope + constraints**

---

# 2️⃣ Estimation

### Quick Math
- Avg QPS = daily / 100k
- Peak QPS = ×3

### Example
- 10M users × 10 actions/day = 100M
- Avg ≈ 1k QPS
- Peak ≈ 3k QPS

👉 Insight:
- Read-heavy → caching
- Large data → sharding

---

# 3️⃣ API Design

### Define core endpoints
```http
POST /posts
GET /feed
POST /follow
```

### Mention
- Pagination (cursor)
- Rate limiting

👉 Output: **System boundary defined**

---

# 4️⃣ High-Level Design

### Build incrementally
1. Client → Server → DB
2. + Load Balancer
3. + Cache
4. + Queue

### Core Components
- Load balancer
- API servers
- Cache (Redis)
- DB
- Message queue
- CDN

👉 Always say **WHY each exists**

---

# 5️⃣ Database Design

### Choose wisely
- SQL → consistency
- NoSQL → scale

### Example
- Users → SQL
- Posts → NoSQL
- Feed → Cache

👉 Rule: **Design for access patterns**

---

# 6️⃣ Deep Dive

### Structure
1. Problem
2. Options
3. Trade-offs
4. Decision

### Example: Feed
- Push → fast reads, heavy writes
- Pull → slow reads, cheap writes
- Hybrid → best ✅

---

# 7️⃣ Wrap-Up

### Cover
- Summary
- Bottlenecks
- Improvements

---

# 📦 Worked Example: Design Instagram (Short)

## 1. Requirements
- Features: post photo, follow, feed
- Scale: ~10M DAU
- Latency: <200ms feed
- Consistency: eventual OK

---

## 2. Estimation
- 100M req/day → ~1k QPS avg
- Peak ~3k QPS
- Read-heavy → caching needed

---

## 3. APIs
```http
POST /posts
GET /feed
POST /follow
```

---

## 4. High-Level Design

Flow:
1. Client → Load Balancer → API
2. Write post → DB
3. Send event → Queue
4. Workers update feed cache

Components:
- LB
- API services
- Redis (feed cache)
- DB (posts/users)
- Queue (fan-out)
- CDN (images)

---

## 5. Database

**Users (SQL)**
- user_id, followers_count

**Posts (NoSQL)**
- post_id, user_id, timestamp

**Feed (Redis)**
- user_id → list(post_ids)

---

## 6. Deep Dive: Feed Fan-out

Problem: updating followers feed

Solution:
- Normal users → push
- Celebrities → pull

👉 Hybrid balances read/write

---

## 7. Wrap-Up

**Bottlenecks**
- Fan-out workers
- Cache load

**Improvements**
- Auto-scale workers
- Better caching strategy

---

# ❌ Common Mistakes
- Jumping into design
- Over/under engineering
- Ignoring trade-offs
- Not speaking thoughts

---

# ✅ Final Checklist

Before finishing, ask yourself:
- Did I clarify requirements?
- Did I estimate scale?
- Did I justify components?
- Did I discuss trade-offs?

---

💡 *Practice this flow until it becomes automatic.*

