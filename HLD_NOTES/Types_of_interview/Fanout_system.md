# Fanout in System Design – Quick Notes

> Short, crisp interview notes on fanout-on-write, fanout-on-read, and hybrid feed design.

---

## Why Fanout Matters
Fanout is the pattern used when **one event must reach many recipients**.

Example:
- One user posts once
- Thousands or millions of followers may need:
    - feed update
    - notification
    - counter refresh
    - analytics event
    - recommendation refresh

So the real challenge is not storing the post.  
It is expanding **1 write into many outputs** without breaking the system.

---

## Where Fanout Shows Up
- Twitter / Instagram feed
- Facebook / LinkedIn activity feed
- Reddit / forum subscriptions
- Notification systems
- Email alerts
- News aggregators

---

# 1) What is Fanout?

## Definition
Fanout is a pattern where **one message is delivered to many recipients**.

Example:
```text
1 post by User A
-> delivered to User B, C, D ... N
```

If User A has **1000 followers**:
```text
1 write -> 1000 downstream writes
```

That is a **1:1000 fanout ratio**.

## Core Question
The main design choice is:

```text
When should distribution happen?
- At write time?
- At read time?
```

That leads to two models:
- **Fanout-on-Write (push)**
- **Fanout-on-Read (pull)**

---

# 2) Fanout-on-Write (Push Model)

## Idea
When a post is created, immediately push it into every follower’s feed cache.

## Flow
```text
User A posts
-> get followers of A
-> write post_id into each follower feed cache
-> later reads are instant
```

## Example
```text
Post Created: {post_id: 123, user_id: A}

Followers of A = [B, C, D]

Write:
feed:B -> add 123
feed:C -> add 123
feed:D -> add 123
```

## Data Structure
Usually:
- **Redis Sorted Sets**
- key = `feed:userId`
- value = `post_id`
- score = timestamp

Example:
```text
feed:userB
- post_123 (1703001000)
- post_456 (1703000500)
- post_789 (1703000000)
```

## Pros
- Very fast feed reads
- Predictable read latency
- Simple read path
- Great for read-heavy systems

## Cons
- Slow / expensive writes
- Duplicate storage across many feeds
- Wasted work for inactive users
- Big celebrity problem

## Best For
- Read-heavy social feeds
- Systems where most users have moderate follower counts

---

# 3) Celebrity Problem

## Problem
If a celebrity with **50M followers** posts:

```text
1 post -> 50,000,000 feed writes
```

That can:
- overload workers
- create queue backlog
- delay other users’ posts
- increase write amplification massively

This is the biggest weakness of fanout-on-write.

---

# 4) Fanout-on-Read (Pull Model)

## Idea
Store the post once.  
Build the feed only when the user opens the app.

## Flow
```text
User A posts
-> store post once

User B opens feed
-> get accounts B follows
-> fetch recent posts from them
-> merge + sort by timestamp
-> return top N
```

## Example
```text
User B follows [A, C, D]

A posts: [123, 120, 115]
C posts: [122, 118]
D posts: [121, 119, 116]

Merge -> [123, 122, 121, 120, 119, 118, 116, 115]
```

## Query Pattern
Naive SQL:
```sql
SELECT p.*
FROM posts p
JOIN follows f ON p.user_id = f.following_id
WHERE f.follower_id = 'B'
ORDER BY p.created_at DESC
LIMIT 100;
```

In practice:
- query per followed shard/user
- merge results in app/service layer
- often parallelized

## Pros
- Fast writes
- No wasted fanout work
- No celebrity problem
- Lower storage usage

## Cons
- Slower reads
- Complex merge logic
- Higher DB/query load
- Users following many accounts see worse latency

## Best For
- Write-heavy systems
- Systems with high follower-count variance
- Celebrity-heavy traffic

---

# 5) Write vs Read Comparison

| Aspect | Fanout-on-Write | Fanout-on-Read |
|---|---|---|
| Write latency | High | Low |
| Read latency | Low | High |
| Storage | High | Low |
| Compute cost | At write time | At read time |
| Celebrity handling | Bad | Good |
| Freshness | Can be stale if fanout delayed | Always fresh |
| Best for | Read-heavy | Write-heavy / high variance |

## Shortcut
```text
Write model = expensive writes, cheap reads
Read model  = cheap writes, expensive reads
```

---

# 6) Hybrid Approach (What Real Systems Use)

## Core Idea
Use different strategies for different users:

- **Regular users** -> fanout-on-write
- **Celebrities** -> fanout-on-read

Example threshold:
```text
< 10K followers -> push
> 10K followers -> pull
```

## Why Hybrid Works
- Normal users still get instant feed loads
- Celebrity posts do not trigger massive write storms
- Balances read performance and write scalability

## Hybrid Read Flow
When User B opens feed:

```text
1. Read precomputed feed cache
   (contains posts from regular users)

2. Query recent celebrity posts
   (from celebrities B follows)

3. Merge by timestamp

4. Return final feed
```

## Threshold Choice
The threshold depends on:
- fanout worker capacity
- posting frequency
- follower activity
- latency requirements

Typical range:
```text
10K to 100K followers
```

Make it tunable, not hardcoded forever.

---

# 7) Edge Cases

## 1. Celebrity Posting Frequently
If a celebrity posts every minute:
- even hybrid gets stressed
- use:
    - rate limiting
    - delayed fanout
    - sampling / prioritization

## 2. Follower Count Changes
A user may cross the celebrity threshold.

Use:
- periodic reclassification
- background migration
- temporary mixed strategy during transition

## 3. Deletions / Updates
If a post was already fanned out:
- mark deleted in DB
- remove asynchronously from feed caches
- also filter deleted content at read time

## 4. Read-Your-Writes
User posts, refreshes feed, and doesn’t see own post.

Fix with:
- always inject recent own posts
- optimistic UI updates
- stronger consistency for self-feed

---

# 8) Performance Optimizations

## Batching
Do not write follower feeds one by one.

Instead:
- batch followers in chunks
- bulk write to Redis

Example:
```text
10,000 followers
-> 10 batches of 1,000
```

## Async Processing
Never block user post creation on full fanout.

Flow:
```text
Post Service
-> store post
-> publish event to queue
-> return success fast

Fanout Workers
-> consume queue
-> update feed caches
```

Benefits:
- faster post confirmation
- retry support
- horizontal scaling with more workers

## Multi-Layer Caching

| Layer | Purpose | Typical Tech |
|---|---|---|
| Feed cache | Precomputed feed IDs | Redis Sorted Sets |
| Post cache | Hydrated post content | Redis / Memcached |
| User cache | Follower lists / metadata | Redis |
| CDN | Images / static media | CloudFront / Cloudflare |

## Feed Cache Limits
You do not need infinite feed cache.

Typical policy:
- keep last **500–1000 posts**
- evict older than **7 days**
- fetch older data from DB only when needed

---

# 9) Quick Examples

## Example 1: Twitter-like Feed
Need:
- fast feed reads
- huge read traffic
- some celebrity users

**Choice:** Hybrid

Why:
- push for normal users
- pull for celebrities
- async queue for fanout

---

## Example 2: Notification System
Need:
- one event to many subscribed users
- fast delivery
- often simple recipient lists

**Choice:** Fanout-on-write + queue

Why:
- notifications are read immediately
- precomputing recipients is acceptable

---

## Example 3: Reddit / Forum Subscriptions
Need:
- distribute posts to many subscribers
- follower counts vary
- freshness matters

**Choice:** Often hybrid or pull-heavy

Why:
- avoids heavy writes for massive subreddits/forums

---

# 10) Interview Answer Template

If asked “How would you design a feed fanout system?”:

```text
I’d first clarify whether the system is more read-heavy or write-heavy,
and whether follower counts are fairly uniform or highly skewed.

If reads dominate and follower counts are moderate, fanout-on-write is better
because it gives fast feed reads.

If follower counts vary a lot, especially with celebrity users, fanout-on-read
avoids massive write amplification.

In practice, I’d use a hybrid model:
push for regular users, pull for celebrities, and use async workers + queues
to decouple post creation from feed distribution.
```

---

# Key Takeaways

- Fanout = **1 event -> many recipients**
- Main choice = **pay cost at write time or read time**
- **Fanout-on-write** gives fast reads but expensive writes
- **Fanout-on-read** gives fast writes but expensive reads
- **Hybrid** is what production systems usually use
- **Async queues + batching** are essential
- Always design for the **celebrity problem**

---

## Final Shortcut

```text
Fanout-on-write = push now, read fast later
Fanout-on-read  = store once, compute on demand
Hybrid          = push for normal users, pull for celebrities
```
