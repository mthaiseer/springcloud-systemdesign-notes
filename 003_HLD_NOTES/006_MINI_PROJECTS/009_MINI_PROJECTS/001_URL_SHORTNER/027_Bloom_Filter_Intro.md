# 027_Bloom_Filter_Intro.md
# MiniURLShortener — Bloom Filter Intro

> Core mental model: **A Bloom filter is a memory-efficient “maybe exists / definitely does not exist” gate. It helps protect PostgreSQL and Redis from repeated lookups for short codes that never existed. It can have false positives, but it should never have false negatives when used correctly.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Cache Penetration Problem](#4-cache-penetration-problem)
- [5. Bloom Filter In One Picture](#5-bloom-filter-in-one-picture)
- [6. Bit Array Mental Model](#6-bit-array-mental-model)
- [7. Hash Functions Mental Model](#7-hash-functions-mental-model)
- [8. Add Operation](#8-add-operation)
- [9. Might-Contain Operation](#9-might-contain-operation)
- [10. False Positive](#10-false-positive)
- [11. No False Negative](#11-no-false-negative)
- [12. Where Bloom Filter Fits In URL Shortener](#12-where-bloom-filter-fits-in-url-shortener)
- [13. Redirect Flow With Bloom Filter](#13-redirect-flow-with-bloom-filter)
- [14. Bloom Filter vs Redis Negative Cache](#14-bloom-filter-vs-redis-negative-cache)
- [15. Bloom Filter vs HashSet](#15-bloom-filter-vs-hashset)
- [16. Size And False Positive Tradeoff](#16-size-and-false-positive-tradeoff)
- [17. Initialization And Rebuild Strategy](#17-initialization-and-rebuild-strategy)
- [18. Create API Integration](#18-create-api-integration)
- [19. Delete And Update Limitations](#19-delete-and-update-limitations)
- [20. Java Implementation Sketch](#20-java-implementation-sketch)
- [21. RedisBloom / Production Option](#21-redisbloom--production-option)
- [22. Step-by-Step Dry Runs](#22-step-by-step-dry-runs)
- [23. Internal Execution Walkthrough](#23-internal-execution-walkthrough)
- [24. Testing Strategy](#24-testing-strategy)
- [25. Metrics And Observability](#25-metrics-and-observability)
- [26. Production Failure Stories](#26-production-failure-stories)
- [27. Debugging Mindset](#27-debugging-mindset)
- [28. Common Mistakes](#28-common-mistakes)
- [29. Interview-Ready Explanation](#29-interview-ready-explanation)
- [30. Senior Engineer Checklist](#30-senior-engineer-checklist)
- [31. One-Page Cheat Sheet](#31-one-page-cheat-sheet)
- [32. One Picture To Remember](#32-one-picture-to-remember)

---

## 1. Why This Exists

MiniURLShortener redirect API receives public traffic:

```http
GET /{shortCode}
```

Valid examples:

```text
/abc123
/sale2026
/mohamed
```

But attackers, bots, crawlers, and broken clients may request random codes:

```text
/a1b2c3
/unknown999
/admin
/zzzzzz
/random1
/random2
/random3
```

If each unknown code follows normal cache-aside flow:

```text
Redis miss
PostgreSQL miss
return 404
```

then random traffic can still pressure PostgreSQL.

ASCII:

```text
Bot random codes
      |
      v
+-------------------+
| Redis             |
| miss miss miss    |
+---------+---------+
          |
          v
+-------------------+
| PostgreSQL        |
| useless lookups   |
+-------------------+
```

This is called:

```text
cache penetration
```

Negative caching helps repeated unknown codes.

But if bots generate always-new random codes, negative cache may not help enough.

Bloom filter helps by answering:

```text
Has this shortCode possibly ever existed?
```

If Bloom filter says:

```text
definitely no
```

we can reject early with 404 before Redis/DB lookup.

Production memory:

```text
Bloom filter is a cheap existence gate before expensive lookup.
```

---

## 2. The One Core Mental Model

The core mental model:

```text
BLOOM FILTER = MAYBE EXISTS / DEFINITELY NOT EXISTS
```

It does not store full short codes.

It stores bits.

For a short code:

```text
abc123
```

Bloom filter applies multiple hash functions.

Each hash gives a bit position.

If all required bits are 1:

```text
maybe exists
```

If any required bit is 0:

```text
definitely does not exist
```

ASCII:

```text
shortCode = abc123

hash1(abc123) -> 2
hash2(abc123) -> 7
hash3(abc123) -> 11

Bit array:
index:  0 1 2 3 4 5 6 7 8 9 10 11
bits:   0 0 1 0 0 0 0 1 0 0 0  1

All 3 bits are 1:
    maybe exists
```

One-line memory:

```text
A Bloom filter can confidently say “no”, but only cautiously says “maybe”.
```

This is the most important idea.

---

## 3. Problem Statement

Design an introductory Bloom filter for MiniURLShortener redirect protection.

It must support:

```text
1. Quickly reject short codes that definitely do not exist.
2. Reduce PostgreSQL lookups for random unknown codes.
3. Work before Redis cache-aside DB fallback.
4. Add newly created short codes to the filter.
5. Accept false positives safely.
6. Avoid false negatives for existing short codes.
7. Explain tradeoff between memory and false positive rate.
8. Support rebuild from DB if needed.
9. Expose metrics for rejected/maybe/false-positive behavior.
10. Fit into production system design interview answers.
```

Non-goals:

```text
full RedisBloom setup
counting Bloom filter deep dive
Cuckoo filter deep dive
cryptographic hash theory
multi-region rebuild strategy
```

This chapter teaches the mental model and practical placement.

---

## 4. Cache Penetration Problem

Cache penetration means requests ask for data that does not exist.

Flow without Bloom filter:

```text
GET /random999
   |
   v
Redis miss
   |
   v
PostgreSQL lookup
   |
   v
DB miss
   |
   v
404
```

If one bad code repeats, negative cache helps.

But if every bad code is new:

```text
/random001
/random002
/random003
...
```

negative cache stores many misses, but every first miss still hits DB.

ASCII:

```text
Bot:
    /x1 -> Redis miss -> DB miss
    /x2 -> Redis miss -> DB miss
    /x3 -> Redis miss -> DB miss
    /x4 -> Redis miss -> DB miss
```

Bloom filter adds an early gate:

```text
GET /random999
   |
   v
Bloom filter says definitely not exists
   |
   v
404 immediately
```

ASCII:

```text
Bot random code
      |
      v
+----------------------+
| Bloom Filter         |
| definitely no        |
+----------+-----------+
           |
           v
          404
```

Result:

```text
Redis and PostgreSQL are protected from many invalid random lookups.
```

---

## 5. Bloom Filter In One Picture

Bloom filter has:

```text
bit array
multiple hash functions
```

ASCII:

```text
Bit Array:
index:  0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
bits:   0 1 0 0 1 0 0 1 0 0  1  0  0  0  1  0


Add "abc123":

h1 -> 4
h2 -> 7
h3 -> 14

set those bits to 1


Check "xyz999":

h1 -> 4
h2 -> 8
h3 -> 14

bit 8 is 0
therefore definitely not exists
```

Decision table:

```text
+--------------------------+-----------------------------+
| Check Result             | Meaning                     |
+--------------------------+-----------------------------+
| any required bit is 0    | definitely not exists       |
| all required bits are 1  | maybe exists                |
+--------------------------+-----------------------------+
```

Why only maybe?

Because different values can set overlapping bits.

---

## 6. Bit Array Mental Model

A bit array is a compact array of zeros and ones.

Example:

```text
size = 16 bits
```

Initial:

```text
index: 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
bits:  0 0 0 0 0 0 0 0 0 0  0  0  0  0  0  0
```

After adding values:

```text
bits become 1 at hashed positions
```

Important:

```text
Bloom filter does not store actual values.
```

It does not store:

```text
abc123
sale2026
mohamed
```

It only stores:

```text
bit positions set by hashes
```

Memory advantage:

```text
millions of short codes can be represented with much less memory than HashSet
```

Tradeoff:

```text
you lose exactness
```

You can ask:

```text
might contain?
```

You cannot ask:

```text
give me all values
delete this exact value safely in standard Bloom filter
```

---

## 7. Hash Functions Mental Model

A hash function turns input into a number.

Example:

```text
hash("abc123") -> 918273
```

Bloom filter converts hash output into bit index:

```text
index = hash(value) % bitArraySize
```

Using multiple hash functions:

```text
h1("abc123") -> 2
h2("abc123") -> 7
h3("abc123") -> 11
```

ASCII:

```text
abc123
  |
  +-- h1 -> 2
  +-- h2 -> 7
  +-- h3 -> 11
```

Add operation:

```text
set bit 2
set bit 7
set bit 11
```

Check operation:

```text
check bit 2
check bit 7
check bit 11
```

If any is 0:

```text
definitely not exists
```

If all are 1:

```text
maybe exists
```

Number of hash functions matters.

Too few:

```text
higher false positives
```

Too many:

```text
more CPU
bits fill faster
```

---

## 8. Add Operation

To add shortCode:

```text
shortCode = abc123
```

Steps:

```text
1. Calculate hash positions.
2. Set all those bit positions to 1.
```

Example:

```text
h1(abc123) = 2
h2(abc123) = 7
h3(abc123) = 11
```

Before:

```text
index: 0 1 2 3 4 5 6 7 8 9 10 11
bits:  0 0 0 0 0 0 0 0 0 0 0  0
```

After:

```text
index: 0 1 2 3 4 5 6 7 8 9 10 11
bits:  0 0 1 0 0 0 0 1 0 0 0  1
```

ASCII:

```text
add abc123
   |
   v
set bits [2, 7, 11]
```

For MiniURLShortener:

```text
When create API successfully inserts shortCode into PostgreSQL,
add shortCode to Bloom filter.
```

Important order:

```text
DB insert succeeds first.
Then add to Bloom filter.
```

Why?

```text
Do not add a code that failed to persist.
```

---

## 9. Might-Contain Operation

To check a short code:

```text
shortCode = xyz999
```

Steps:

```text
1. Calculate same hash positions.
2. Read those bits.
3. If any bit is 0, return definitely no.
4. If all bits are 1, return maybe.
```

Example:

```text
h1(xyz999) = 2
h2(xyz999) = 5
h3(xyz999) = 11
```

Bit array:

```text
index: 0 1 2 3 4 5 6 7 8 9 10 11
bits:  0 0 1 0 0 0 0 1 0 0 0  1
```

Bit 5 is 0.

Therefore:

```text
xyz999 definitely does not exist
```

ASCII:

```text
check xyz999
   |
   +-- bit 2 = 1
   +-- bit 5 = 0  -> definitely no
   +-- bit 11 = 1
```

For redirect:

```text
definitely no -> return 404 before Redis/DB
maybe -> continue Redis/cache-aside flow
```

---

## 10. False Positive

False positive means Bloom filter says:

```text
maybe exists
```

but actual DB says:

```text
does not exist
```

How can this happen?

Different short codes may set overlapping bits.

Example:

```text
abc123 sets bits 2, 7, 11
sale99 sets bits 4, 5, 8
hello1 sets bits 1, 9, 10
```

Another code:

```text
ghost9 hashes to bits 2, 5, 10
```

All bits are already 1 because of other values.

Bloom filter says:

```text
maybe exists
```

But `ghost9` was never inserted.

ASCII:

```text
ghost9 checks:
bit 2 = 1 from abc123
bit 5 = 1 from sale99
bit 10 = 1 from hello1

All 1 -> maybe exists
But DB may still say no.
```

False positive is safe.

It only causes:

```text
extra Redis/DB lookup
```

It does not cause wrong redirect.

Because DB remains source of truth.

---

## 11. No False Negative

False negative would mean:

```text
Bloom filter says definitely not exists
but value actually exists
```

A correctly maintained standard Bloom filter should not have false negatives.

Why?

When a code is added:

```text
all its hash bits are set to 1
```

Later check computes same positions.

Those bits should still be 1.

ASCII:

```text
add abc123:
    set 2,7,11

check abc123:
    read 2,7,11
    all are 1
```

False negatives happen only because of bugs or operational mistakes:

```text
Bloom filter not initialized from DB
create API forgot to add new code
filter reset during deployment
wrong hash seed/version
wrong key namespace
bit array lost
```

This is serious.

If Bloom filter falsely says no:

```text
valid short URL returns 404
```

Therefore production rule:

```text
If Bloom filter state is uncertain, bypass it or rebuild it.
```

Safety option:

```text
Bloom disabled mode:
    skip Bloom filter and use Redis/DB flow
```

---

## 12. Where Bloom Filter Fits In URL Shortener

Recommended redirect flow:

```text
GET /{shortCode}
   |
   v
Validate shortCode format
   |
   v
Bloom filter check
   |
   +-- definitely no -> 404
   |
   +-- maybe -> Redis cache-aside flow
```

ASCII:

```text
Client
  |
  v
+----------------------+
| Validate format      |
+----------+-----------+
           |
           v
+----------------------+
| Bloom Filter         |
+------+---------------+
       |
       +-- definitely no -> 404
       |
       +-- maybe
              |
              v
+----------------------+
| Redis Cache          |
+------+---------------+
       |
       +-- hit -> 302
       |
       +-- miss
              |
              v
+----------------------+
| PostgreSQL           |
+----------------------+
```

Why Bloom before Redis?

Because random invalid codes will likely not exist.

Bloom can reject them without even Redis cache lookup.

But in some designs, Redis negative cache can come before Bloom.

Simple recommended order:

```text
format validation
Bloom filter
Redis cache
PostgreSQL
```

---

## 13. Redirect Flow With Bloom Filter

Detailed flow:

```text
1. Client requests /abc123.
2. Controller/service validates shortCode shape.
3. Bloom filter checks abc123.
4. If definitely not exists:
       return 404.
5. If maybe exists:
       check Redis redirect cache.
6. If Redis hit:
       validate status/expiry.
       return 302 or domain error.
7. If Redis miss:
       query PostgreSQL.
8. If DB row found:
       fill Redis.
       return redirect or domain error.
9. If DB row missing:
       optional negative cache.
       return 404.
```

ASCII:

```text
GET /code
  |
  v
Bloom?
  |
  +-- NO  -> 404
  |
  +-- MAYBE
        |
        v
      Redis?
        |
        +-- HIT -> 302/error
        |
        +-- MISS
              |
              v
             DB
              |
              +-- found -> cache -> 302/error
              |
              +-- missing -> 404
```

This protects DB from many impossible requests.

---

## 14. Bloom Filter vs Redis Negative Cache

Both reduce invalid lookups, but they solve different parts.

Negative cache:

```text
remembers recent misses
```

Bloom filter:

```text
remembers approximate set of possible existing codes
```

Comparison:

```text
+------------------+----------------------------+----------------------------+
| Feature          | Bloom Filter               | Negative Cache             |
+------------------+----------------------------+----------------------------+
| Rejects new bad  | yes, often                 | no, first miss hits DB     |
| Rejects repeated | yes                        | yes                        |
| Memory           | compact bit array          | grows with miss keys       |
| Accuracy         | false positives possible   | exact while key exists     |
| Expiry           | usually rebuilt/versioned  | TTL per key                |
| Use case         | massive random invalids    | repeated invalids          |
+------------------+----------------------------+----------------------------+
```

Best design:

```text
Use both.
```

ASCII:

```text
random invalid code:
    Bloom rejects early

repeated invalid false positive:
    DB miss once
    negative cache catches repeat
```

Production memory:

```text
Bloom filter stops most impossible keys.
Negative cache catches repeated misses that pass Bloom.
```

---

## 15. Bloom Filter vs HashSet

Why not store all short codes in a HashSet?

HashSet:

```text
exact membership
no false positives
large memory
harder to share across pods
```

Bloom filter:

```text
approximate membership
false positives possible
very memory efficient
```

ASCII:

```text
HashSet:
+-------------------+
| abc123            |
| sale2026          |
| mohamed           |
| ... full strings  |
+-------------------+


Bloom Filter:
+-------------------+
| 0101011101010010  |
| bits only         |
+-------------------+
```

If you have 100 million short codes:

```text
HashSet may need many GB depending representation
Bloom filter may need far less memory
```

Tradeoff:

```text
HashSet says exact yes/no.
Bloom says definitely no / maybe.
```

For DB protection:

```text
maybe is acceptable because DB verifies.
```

---

## 16. Size And False Positive Tradeoff

Bloom filter has tradeoffs:

```text
bit array size
number of hash functions
expected number of items
false positive rate
```

If bit array is too small:

```text
many bits become 1
false positives increase
```

ASCII:

```text
Sparse filter:
bits: 0 0 1 0 0 1 0 0 0 1
many zeros -> many definite no answers

Full filter:
bits: 1 1 1 1 1 1 1 1 1 1
all checks become maybe
useless
```

False positive effect:

```text
more false positives -> more Redis/DB fallback
```

But correctness remains safe.

Sizing goal:

```text
keep false positive rate low enough to protect DB
```

Example target:

```text
1% false positive rate
```

Meaning:

```text
out of 100 truly missing codes, about 1 may pass Bloom and hit Redis/DB
```

Production rule:

```text
Size Bloom filter based on expected number of short codes and acceptable false positive rate.
```

---

## 17. Initialization And Rebuild Strategy

Bloom filter must know existing short codes.

On new deployment:

```text
Bloom filter starts empty
```

If used immediately:

```text
every valid shortCode may look definitely not exists
```

That would cause false negatives.

Therefore initialization matters.

Strategies:

```text
1. Rebuild from DB at startup before enabling Bloom.
2. Store Bloom filter in Redis/persistent service.
3. Versioned rebuild in background.
4. Disable Bloom until loaded.
```

ASCII:

```text
Startup
  |
  v
Load all short_code from DB
  |
  v
Add to Bloom filter
  |
  v
Mark Bloom READY
  |
  v
Use in redirect path
```

For huge tables:

```text
rebuilding from DB may be expensive
```

Options:

```text
batch loading
background rebuild
snapshot file
RedisBloom persistent filter
event-driven updates
```

Learning version:

```text
Load active short codes in batches at startup for small project.
```

Production version:

```text
Use RedisBloom or managed shared filter with rebuild process.
```

---

## 18. Create API Integration

When a new short URL is created:

```text
POST /api/v1/urls
```

Flow:

```text
1. Validate request.
2. Generate or accept shortCode.
3. Insert into PostgreSQL.
4. After DB success, add shortCode to Bloom filter.
5. Optionally write-through Redis redirect cache.
6. Return response.
```

ASCII:

```text
Create short URL
      |
      v
PostgreSQL INSERT succeeds
      |
      v
BloomFilter.add(shortCode)
      |
      v
Redis SET optional
      |
      v
201 Created
```

Important:

```text
Do not add to Bloom before DB insert commits.
```

If transaction rolls back but Bloom was updated:

```text
Bloom may say maybe for non-existing code
```

This is false positive, not fatal.

But too many such cases reduce filter quality.

Best:

```text
add after commit
```

In Spring:

```text
TransactionSynchronization afterCommit
```

or event after successful create.

---

## 19. Delete And Update Limitations

Standard Bloom filter does not support safe deletion.

Why?

Because bits are shared by many values.

Example:

```text
abc123 sets bit 7
sale99 also sets bit 7
```

If deleting abc123 clears bit 7:

```text
sale99 may become false negative
```

ASCII:

```text
bit 7 used by:
    abc123
    sale99

clear bit 7 for abc123?
    breaks sale99
```

Therefore standard Bloom filter:

```text
supports add
supports mightContain
does not safely delete
```

For URL shortener deleted links:

```text
Bloom may still say maybe
```

Then Redis/DB handles deleted state.

This is safe.

For many deletes:

```text
false positive rate may grow
```

Solutions:

```text
periodic rebuild
counting Bloom filter
Cuckoo filter
keep status check in DB/cache
```

For MiniURLShortener:

```text
standard Bloom filter is okay for intro.
Deleted codes may remain maybe.
```

---

## 20. Java Implementation Sketch

Simple local Bloom filter for learning:

```java
package com.miniurl.shortener.bloom;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;

@Component
public class ShortCodeBloomFilter {

    private static final int BIT_SIZE = 1_000_000;
    private static final int HASH_COUNT = 4;

    private final BitSet bitSet = new BitSet(BIT_SIZE);

    public void add(String shortCode) {
        int[] positions = positions(shortCode);

        for (int position : positions) {
            bitSet.set(position);
        }
    }

    public boolean mightContain(String shortCode) {
        int[] positions = positions(shortCode);

        for (int position : positions) {
            if (!bitSet.get(position)) {
                return false;
            }
        }

        return true;
    }

    private int[] positions(String value) {
        int[] result = new int[HASH_COUNT];

        int hash1 = value.hashCode();
        int hash2 = murmurLikeHash(value);

        for (int i = 0; i < HASH_COUNT; i++) {
            int combined = hash1 + i * hash2;
            result[i] = Math.floorMod(combined, BIT_SIZE);
        }

        return result;
    }

    private int murmurLikeHash(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        int hash = 0x9747b28c;

        for (byte b : bytes) {
            hash ^= b;
            hash *= 0x5bd1e995;
            hash ^= hash >>> 15;
        }

        return hash;
    }
}
```

Important:

```text
This is learning code, not perfect production hashing.
```

Production options:

```text
Guava BloomFilter
RedisBloom
custom tested implementation
```

---

## 21. RedisBloom / Production Option

In multi-pod production, local in-memory Bloom filters have problems:

```text
each pod has its own filter
new code added on one pod may not exist in another pod's filter
pod restart loses filter
rebuild needed per pod
memory duplicated
```

ASCII:

```text
Pod A Bloom has abc123
Pod B Bloom does not
Pod C Bloom does not

Request /abc123 hits Pod B
false 404 risk
```

Solutions:

```text
1. Shared RedisBloom filter.
2. Broadcast create events to all pods.
3. Rebuild local filters on startup.
4. Disable Bloom until ready.
```

RedisBloom commands conceptually:

```text
BF.ADD shortcodes abc123
BF.EXISTS shortcodes abc123
```

Benefits:

```text
shared across pods
atomic operations
centralized filter
```

For learning project:

```text
start with local Guava/BitSet
understand concept
```

For production-shaped design:

```text
prefer RedisBloom or robust shared strategy
```

---

## 22. Step-by-Step Dry Runs

### Dry Run 1: Existing Code

Existing short code:

```text
abc123
```

Bloom bits already set.

Request:

```http
GET /abc123
```

Flow:

```text
1. Validate shortCode format.
2. Bloom mightContain returns true.
3. Continue to Redis.
4. Redis hit or DB fallback returns actual redirect.
5. User gets 302.
```

ASCII:

```text
abc123 -> Bloom maybe -> Redis/DB -> 302
```

---

### Dry Run 2: Definitely Missing Code

Request:

```http
GET /zzzzzz
```

Bloom check:

```text
one required bit is 0
```

Flow:

```text
1. Bloom returns false.
2. Service knows code definitely does not exist.
3. Return 404.
4. Redis is not called.
5. DB is not called.
```

ASCII:

```text
zzzzzz -> Bloom no -> 404
```

---

### Dry Run 3: False Positive

Request:

```http
GET /ghost9
```

Bloom check:

```text
all bits are 1 due to other codes
```

Flow:

```text
1. Bloom returns maybe.
2. Redis miss.
3. DB lookup.
4. DB says no row.
5. Return 404.
6. Optional negative cache stores miss.
```

ASCII:

```text
ghost9 -> Bloom maybe -> Redis miss -> DB miss -> 404
```

This is safe.

Only cost:

```text
one extra lookup
```

---

### Dry Run 4: Bloom Not Ready

Application starts.

Bloom filter is empty.

Request:

```http
GET /abc123
```

If Bloom is used immediately:

```text
Bloom says no
valid link gets 404
```

This is wrong.

Correct flow:

```text
if Bloom not ready:
    skip Bloom
    use Redis/DB flow
```

ASCII:

```text
Bloom status = NOT_READY
       |
       v
bypass Bloom
       |
       v
Redis/DB
```

Rule:

```text
Never enforce Bloom before it is loaded.
```

---

### Dry Run 5: Deleted Code

Code existed:

```text
old1
```

It was added to Bloom.

Later deleted in DB.

Bloom still says:

```text
maybe
```

Flow:

```text
1. Bloom maybe.
2. Redis miss or deleted marker.
3. DB says DELETED or missing.
4. Return 404.
```

This is okay.

Standard Bloom filter cannot delete safely.

---

## 23. Internal Execution Walkthrough

Redirect with Bloom:

```text
1. HTTP request enters controller.
2. shortCode is extracted.
3. Format validation runs.
4. Service checks Bloom readiness.
5. If ready, service checks mightContain.
6. If false, service throws ShortCodeNotFoundException.
7. GlobalExceptionHandler returns 404.
8. If maybe, service continues to Redis cache-aside.
9. Redis hit returns cached redirect metadata.
10. Redis miss queries PostgreSQL.
11. DB verifies source of truth.
12. Response is 302, 403, 404, or 410.
```

ASCII:

```text
Controller
   |
   v
Validate format
   |
   v
Bloom ready?
   |
   +-- no -> Redis/DB
   |
   +-- yes
         |
         v
     mightContain?
         |
         +-- false -> 404
         |
         +-- true  -> Redis/DB
```

Important:

```text
Bloom filter is not the final truth.
It is only a pre-check.
```

---

## 24. Testing Strategy

### Unit Tests

Test Bloom behavior:

```text
added code returns maybe true
clearly missing code often returns false
false positives are possible, so do not assert all missing are false
```

Example:

```java
bloom.add("abc123");

assertThat(bloom.mightContain("abc123")).isTrue();
```

Do not write fragile test:

```java
assertThat(bloom.mightContain("xyz999")).isFalse();
```

Because false positives are allowed.

Better:

```text
test many inserted values have no false negatives
```

### Service Tests

```text
Bloom false -> repository not called
Bloom maybe -> Redis/DB flow continues
Bloom not ready -> Redis/DB flow continues
```

### Integration Tests

```text
1. Create short URL.
2. Add to Bloom after create.
3. Redirect works.
4. Unknown code rejected early if Bloom says no.
```

### Failure Tests

```text
Bloom reset
Bloom disabled
Bloom rebuild in progress
```

Expected:

```text
system falls back safely to Redis/DB
```

Testing principle:

```text
Never allow Bloom uncertainty to cause false 404 for valid codes.
```

---

## 25. Metrics And Observability

Metrics:

```text
bloom.check.total
bloom.definitely_not_found
bloom.maybe_exists
bloom.not_ready_bypass
bloom.false_positive_estimate
bloom.rebuild.duration
bloom.rebuild.status
redirect.db.lookup.after_bloom_maybe
```

Useful ratios:

```text
bloom rejection rate:
    definitely_not_found / checks

maybe rate:
    maybe_exists / checks

false positive estimate:
    DB misses after Bloom maybe / Bloom maybe checks
```

ASCII:

```text
Request
  |
  +-- Bloom no counter
  |
  +-- Bloom maybe counter
  |
  +-- Bloom bypass counter
```

Healthy signs:

```text
many random invalid requests rejected by Bloom
DB miss load drops
no valid-link false 404 reports
false positive rate acceptable
```

Danger signs:

```text
Bloom rejection suddenly 100%
Bloom maybe suddenly 100%
Bloom not ready forever
DB miss rate high after Bloom maybe
```

Alert:

```text
Bloom not ready after startup grace period
```

---

## 26. Production Failure Stories

### Failure Story 1: Empty Bloom Filter Caused Valid Links To 404

Deployment started with empty local Bloom.

Traffic arrived before rebuild.

Valid codes were rejected.

Root cause:

```text
Bloom filter used before ready.
```

Fix:

```text
ready flag
bypass Bloom until rebuild completes
health check includes Bloom readiness if required
```

Lesson:

```text
Bloom filter must fail safe, not fail closed, when uninitialized.
```

---

### Failure Story 2: Local Bloom Filters Diverged Across Pods

Create request hit Pod A.

Pod A added short code to its local Bloom.

Redirect request hit Pod B.

Pod B did not know the code.

It returned 404.

Root cause:

```text
local per-pod Bloom without synchronization
```

Fix:

```text
shared RedisBloom or broadcast updates/rebuild all pods
```

Lesson:

```text
Distributed systems need shared or synchronized filter state.
```

---

### Failure Story 3: Bloom Filter Too Small Became Useless

Too many short codes were inserted into a tiny bit array.

Most bits became 1.

Every random code returned maybe.

Root cause:

```text
false positive rate too high
```

Fix:

```text
increase bit array size
rebuild filter
choose correct expected insert count and false positive target
```

Lesson:

```text
A saturated Bloom filter becomes an expensive always-maybe filter.
```

---

### Failure Story 4: Delete Expected To Remove From Bloom

Team tried to clear bits for deleted codes.

Other valid codes sharing those bits started returning false no.

Root cause:

```text
standard Bloom filter does not support deletion
```

Fix:

```text
do not delete from standard Bloom
use rebuild, counting Bloom, or Cuckoo filter
```

Lesson:

```text
Clearing shared bits can create false negatives.
```

---

### Failure Story 5: Bloom Filter Hid Metrics Problem

Bloom rejected many invalid requests.

DB looked healthy.

But attack traffic was still huge.

Root cause:

```text
only DB metrics were monitored, not Bloom rejection metrics
```

Fix:

```text
monitor Bloom rejected count and upstream attack rate
```

Lesson:

```text
Rejected traffic is still important security signal.
```

---

## 27. Debugging Mindset

When Bloom behavior looks wrong, ask:

```text
Is Bloom ready?
Was it initialized from DB?
Is it shared across pods?
Was new shortCode added after create?
Are hash seeds consistent across versions?
Is bit array size enough?
Is false positive rate too high?
Is a valid code rejected?
Is Bloom bypass mode available?
Are deletes incorrectly clearing bits?
Is DB still source of truth after maybe?
```

Debug map:

```text
valid link returns 404:
    Bloom not ready
    code not added
    local pod divergence
    hash version mismatch
    accidental bit clearing

Bloom rejects nothing:
    filter saturated
    too small bit array
    too many inserted items
    hash issue

DB misses still high:
    false positive rate high
    random codes passing Bloom
    negative cache missing
    Bloom disabled

Different behavior per pod:
    local Bloom state mismatch
```

Golden rule:

```text
For any valid-link 404, first check whether Bloom filter said definitely no.
```

---

## 28. Common Mistakes

### Mistake 1: Treating Bloom As Source Of Truth

Wrong:

```text
Bloom says maybe, so redirect without DB/cache.
```

Correct:

```text
Bloom maybe only allows normal Redis/DB lookup.
```

### Mistake 2: Using Bloom Before It Is Ready

Wrong:

```text
empty filter rejects valid codes.
```

Correct:

```text
bypass until ready.
```

### Mistake 3: Expecting No False Positives

Wrong:

```text
Bloom maybe means definitely exists.
```

Correct:

```text
Bloom maybe means check Redis/DB.
```

### Mistake 4: Deleting From Standard Bloom

Wrong:

```text
clear bits for deleted code.
```

Correct:

```text
do not delete; rebuild or use counting Bloom.
```

### Mistake 5: Local Filter In Multi-Pod Without Sync

Wrong:

```text
each pod has different Bloom state.
```

Correct:

```text
shared RedisBloom or synchronized rebuild/update.
```

### Mistake 6: Too Small Filter

Wrong:

```text
bit array too small for millions of codes.
```

Correct:

```text
size by expected item count and target false positive rate.
```

### Mistake 7: No Metrics

Wrong:

```text
cannot tell rejection rate or saturation.
```

Correct:

```text
monitor no/maybe/bypass/false-positive estimates.
```

### Mistake 8: Ignoring Negative Cache

Wrong:

```text
Bloom alone handles all misses.
```

Correct:

```text
Bloom + negative cache work together.
```

---

## 29. Interview-Ready Explanation

If interviewer asks:

```text
How can a Bloom filter help a URL shortener?
```

Strong answer:

```text
A URL shortener can receive many random invalid short-code requests from bots. Without protection,
each unknown code may miss Redis and hit PostgreSQL, which is cache penetration. A Bloom filter
acts as a memory-efficient pre-check before Redis and DB. It stores approximate membership of
created short codes using a bit array and multiple hash functions. If any required bit is zero,
the code definitely does not exist and we can return 404 immediately. If all bits are one, the
code may exist, so we continue with Redis cache-aside and PostgreSQL as the source of truth.
False positives are acceptable because they only cause extra lookup. False negatives are not
acceptable, so the filter must be initialized correctly, updated after successful creates, and
bypassed when not ready. In multi-pod production, I would use shared RedisBloom or a synchronized
rebuild/update strategy.
```

Senior version:

```text
Bloom filter is not a cache replacement. It is an existence gate that reduces useless lookups.
It is valuable when invalid-key traffic is large and random, because negative cache only helps
after the first miss, while Bloom can reject many never-existing keys before Redis and DB.
```

Why this is strong:

```text
1. Names cache penetration.
2. Explains maybe/no semantics.
3. Explains false positive safety.
4. Explains false negative danger.
5. Places Bloom before Redis/DB.
6. Keeps DB as source of truth.
7. Mentions create integration.
8. Mentions multi-pod production concern.
9. Mentions negative cache relationship.
```

---

## 30. Senior Engineer Checklist

Before calling Bloom filter production-shaped, confirm:

```text
[ ] Bloom filter is only a pre-check
[ ] DB remains source of truth
[ ] Bloom can be bypassed if not ready
[ ] filter is initialized from existing DB data
[ ] create API adds shortCode after successful commit
[ ] no standard Bloom deletion is attempted
[ ] false positives are expected and safe
[ ] false negatives are treated as serious bugs
[ ] multi-pod state strategy exists
[ ] bit size is chosen for expected volume
[ ] target false positive rate is defined
[ ] negative cache still exists
[ ] metrics for no/maybe/bypass exist
[ ] rebuild process exists
[ ] tests cover added values and not-ready bypass
```

---

## 31. One-Page Cheat Sheet

```text
Core mental model:
Bloom filter = maybe exists / definitely does not exist.

Data structure:
bit array + multiple hash functions

Add:
hash value to positions
set bits to 1

Check:
hash value to same positions
if any bit is 0 -> definitely no
if all bits are 1 -> maybe

False positive:
Bloom says maybe, DB says no
safe, only extra lookup

False negative:
Bloom says no, DB has value
bad, caused by bug/ops issue

URL shortener use:
validate format
Bloom check
no -> 404
maybe -> Redis cache-aside -> DB if miss

Protects against:
cache penetration
random invalid short codes
bot scans

Does not replace:
Redis cache
PostgreSQL truth
rate limiting
WAF

Limitations:
standard Bloom cannot delete safely
must be initialized
multi-pod state must be shared/synced
can saturate if too small
```

---

## 32. One Picture To Remember

```text
                BLOOM FILTER FOR URL SHORTENER

                 "Definitely no / maybe yes"

GET /randomCode
      |
      v
+----------------------+
| Validate format      |
+----------+-----------+
           |
           v
+----------------------+
| Bloom Filter         |
| bit array + hashes   |
+------+---------------+
       |
       +-- any bit 0
       |       |
       |       v
       |   DEFINITELY NO
       |       |
       |       v
       |      404
       |
       +-- all bits 1
               |
               v
            MAYBE
               |
               v
+----------------------+
| Redis Cache          |
+------+---------------+
       |
       +-- hit -> 302/error
       |
       +-- miss
              |
              v
+----------------------+
| PostgreSQL Truth     |
+----------------------+


FINAL MEMORY:

Bloom filter confidently rejects impossible codes.
It never confirms existence.
Database still decides truth.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. A Bloom filter protects against cache penetration by rejecting short codes that definitely do not exist.
2. It uses a bit array and multiple hash functions to answer “maybe exists” or “definitely does not exist.”
3. False positives are acceptable because they only cause Redis/DB lookup; false negatives are dangerous because valid links may return 404.
4. In MiniURLShortener, Bloom filter should sit before Redis cache-aside and PostgreSQL fallback.
5. Production use requires correct initialization, post-create updates, no unsafe deletion, multi-pod shared/synced state, metrics, and rebuild strategy.
```

Next chapter:

```text
028_Circuit_Breaker_For_Dependencies.md
```
