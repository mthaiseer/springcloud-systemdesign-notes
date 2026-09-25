# MiniRealWorldSTL — STL Patterns to Real-World Backend Problems

> Goal: learn how competitive-programming STL patterns become real production backend building blocks.
>
> This file intentionally avoids advanced graph/DP. It focuses on **STL + real-world systems thinking**: queues, stacks, maps, sets, heaps, deques, intervals, prefix sums, sliding windows, monotonic structures, and top-K patterns.

---

## 0. How To Use This File

Use this file after `001_STL_Foundation.md`.

```text
CP STL pattern
      |
      v
Backend primitive
      |
      v
Production use case
      |
      v
Failure mode
      |
      v
Interview story
```

For each topic, learn five things:

```text
1. What STL pattern is this?
2. What real-world problem does it model?
3. What invariant must always be true?
4. What breaks in production?
5. How to explain it in an interview?
```

---

## 1. Master Map — STL to Real World

```text
vector          -> batch processing, log chunks, API results
string          -> log parsing, request parsing, tokenization
stack           -> parsers, undo/redo, nested validation
queue           -> FIFO jobs, event buffering, async workers
deque           -> sliding windows, rate limiters, rolling metrics
priority_queue  -> schedulers, retries, top-K dashboards
map             -> ordered index, timeline, sorted lookup
unordered_map   -> cache, frequency table, metadata lookup
set             -> uniqueness, active sessions, ordered dedupe
multiset        -> live median, dynamic leaderboard, latency windows
pair/tuple      -> composite keys, events, heap states
prefix sum      -> cumulative metrics, billing, analytics
sliding window  -> rate limiting, fraud windows, rolling errors
monotonic stack -> boundary detection, cleanup, compression
interval set    -> booking, availability, IP ranges, feature windows
```

ASCII one-picture:

```text
                REAL SYSTEM
                    |
      +-------------+-------------+
      |                           |
  fast lookup                  ordered behavior
      |                           |
unordered_map                set / map / heap
      |
      +-- cache, session, metadata

stream/order
      |
 queue / deque / stack
      |
 events, windows, parsers

ranges/time
      |
 intervals + prefix + sweep
      |
 bookings, billing, monitoring
```

---

# PART 1 — Core Containers as Backend Primitives

---

## 2. vector -> Batch Processing Engine

### CP Pattern

`vector` stores contiguous data and supports random access, iteration, sorting, prefix sums, and batch operations.

### Real-World Problem

A service receives 10,000 payment events from Kafka and processes them in batches before writing to the database.

```text
Kafka messages
      |
      v
vector<Event> batch
      |
      +-- validate all
      +-- sort by accountId
      +-- group by accountId
      +-- bulk insert/update
```

### Example Problem Description

You receive a batch of payment events:

```text
[id=1, user=U1, amount=100]
[id=2, user=U2, amount=50]
[id=3, user=U1, amount=70]
```

You need total amount per user.

### ASCII Diagram

```text
Raw batch vector:

index:     0        1        2
        +------+ +------+ +------+
event:  | U1   | | U2   | | U1   |
amount: | 100  | | 50   | | 70   |
        +------+ +------+ +------+

Process:

vector<Event> -> unordered_map<user,total>

U1 -> 170
U2 -> 50
```

### C++ Pattern

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Event {
    string userId;
    long long amount;
};

unordered_map<string, long long> aggregateBatch(vector<Event>& events) {
    unordered_map<string, long long> total;
    for (auto& e : events) {
        total[e.userId] += e.amount;
    }
    return total;
}
```

### Dry Run

```text
events = [(U1,100), (U2,50), (U1,70)]

after event 1: U1 -> 100
after event 2: U1 -> 100, U2 -> 50
after event 3: U1 -> 170, U2 -> 50
```

### Production Use

```text
Batch ETL
Bulk DB inserts
Search indexing
Kafka consumer batches
Log aggregation
API pagination results
```

### Production Failure Story

```text
Bug:
Batch kept growing without clearing after commit.

Effect:
Memory slowly increased, service restarted under load.

Fix:
Clear vector after successful flush and cap batch size.
```

### Interview Answer

```text
I use vector when the data is naturally batch-oriented and I need fast scan, sort, random access, or bulk operations. In real systems this maps to Kafka batch consumers, log chunks, and bulk database writes.
```

---

## 3. string -> Log Parsing and Request Parsing

### CP Pattern

Strings are parsed using loops, delimiters, tokenization, stack, or state machines.

### Real-World Problem

Parse a log line:

```text
2026-06-27 INFO userId=42 action=LOGIN latency=120ms
```

Extract:

```text
userId = 42
action = LOGIN
latency = 120
```

### ASCII Diagram

```text
raw log line
   |
   v
split by spaces
   |
   +-- timestamp
   +-- level
   +-- key=value
   +-- key=value
   +-- key=value
```

### C++ Pattern

```cpp
#include <bits/stdc++.h>
using namespace std;

unordered_map<string,string> parseKeyValueLog(const string& line) {
    unordered_map<string,string> fields;
    stringstream ss(line);
    string token;

    while (ss >> token) {
        auto pos = token.find('=');
        if (pos != string::npos) {
            string key = token.substr(0, pos);
            string value = token.substr(pos + 1);
            fields[key] = value;
        }
    }
    return fields;
}
```

### Dry Run

```text
token = 2026-06-27       no '=' -> ignore
token = INFO             no '=' -> ignore
token = userId=42        store userId -> 42
token = action=LOGIN     store action -> LOGIN
token = latency=120ms    store latency -> 120ms
```

### Production Use

```text
Log parsing
HTTP query parsing
CSV processing
Metrics label extraction
Search tokenization
```

### Production Failure Story

```text
Bug:
Parser assumed every token has '='.

Effect:
Crashes on malformed logs.

Fix:
Check delimiter exists and validate token format.
```

---

## 4. stack -> Parser, Validator, Undo/Redo

### CP Pattern

Stack solves nested structures and last-open-first-close logic.

### Real-World Problem

Validate nested JSON-like brackets in a config file before deploying service config.

```text
{ "rateLimit": { "window": 60, "max": 1000 } }
```

### ASCII Diagram

```text
Input stream:

{  {  }  }
|  |  |  |
push push pop pop

Stack:

read {  -> [{]
read {  -> [{,{]
read }  -> [{]
read }  -> [] valid
```

### C++ Pattern

```cpp
#include <bits/stdc++.h>
using namespace std;

bool validBrackets(const string& s) {
    unordered_map<char,char> match = {{')','('}, {']','['}, {'}','{'}};
    stack<char> st;

    for (char c : s) {
        if (c == '(' || c == '[' || c == '{') st.push(c);
        else if (match.count(c)) {
            if (st.empty() || st.top() != match[c]) return false;
            st.pop();
        }
    }
    return st.empty();
}
```

### Real-World Uses

```text
Expression parsing
JSON/XML validation
Undo/redo operation history
Function call simulation
Browser back stack
Deployment config validation
```

### Production Failure Story

```text
Bug:
Config parser accepted invalid nested braces.

Effect:
Service loaded partial config and wrong limits were applied.

Fix:
Validate bracket structure before parsing semantic values.
```

---

## 5. queue -> Async Job Buffer

### CP Pattern

Queue is FIFO: first in, first out.

### Real-World Problem

A notification service receives email jobs and workers process them in arrival order.

```text
API request -> queue -> worker -> email provider
```

### ASCII Diagram

```text
Queue front                         back
   |                                  |
   v                                  v
+--------+   +--------+   +--------+   +--------+
| job 1  |-->| job 2  |-->| job 3  |-->| job 4  |
+--------+   +--------+   +--------+   +--------+
   |
   v
worker processes job 1 first
```

### C++ Pattern

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Job {
    int id;
    string type;
};

void processJobs(queue<Job>& q) {
    while (!q.empty()) {
        Job job = q.front();
        q.pop();
        cout << "Processing job " << job.id << " type=" << job.type << "\n";
    }
}
```

### Dry Run

```text
push email-1
push email-2
push sms-3

queue = [email-1, email-2, sms-3]

pop -> email-1
pop -> email-2
pop -> sms-3
```

### Production Use

```text
Kafka topic partition
RabbitMQ queue
SQS queue
Background worker queue
BFS-style processing
Request buffering
```

### Production Failure Story

```text
Bug:
Workers were slower than producers.

Effect:
Queue length grew forever, increasing latency.

Fix:
Add backpressure, max queue size, autoscaling, and dead-letter queue.
```

### Interview Answer

```text
A queue models asynchronous decoupling. Producers can accept requests quickly, while consumers process jobs at controlled speed. The main production concerns are backpressure, retries, ordering, and idempotency.
```

---

## 6. deque -> Sliding Window Rate Limiter

### CP Pattern

Deque supports push/pop from both ends and is used for sliding windows.

### Real-World Problem

Allow at most 5 API requests per user in the last 60 seconds.

### ASCII Diagram

```text
Current time = 100
Window = last 60 seconds
Valid timestamps >= 40

Deque for user U1:

front                              back
  |                                  |
  v                                  v
[35, 42, 50, 70, 90, 99]
 ^
 expired because 35 < 40

After cleanup:
[42, 50, 70, 90, 99]
size = 5 -> reject next request
```

### C++ Pattern

```cpp
#include <bits/stdc++.h>
using namespace std;

class SlidingWindowRateLimiter {
    int limit;
    int windowSeconds;
    unordered_map<string, deque<int>> userHits;

public:
    SlidingWindowRateLimiter(int limit, int windowSeconds)
        : limit(limit), windowSeconds(windowSeconds) {}

    bool allow(const string& userId, int now) {
        auto& dq = userHits[userId];

        while (!dq.empty() && dq.front() <= now - windowSeconds) {
            dq.pop_front();
        }

        if ((int)dq.size() >= limit) return false;

        dq.push_back(now);
        return true;
    }
};
```

### Dry Run

```text
limit = 3, window = 10 seconds
requests at t = 1, 2, 3, 12

at 1: dq [] -> [1] allow
at 2: dq [1] -> [1,2] allow
at 3: dq [1,2] -> [1,2,3] allow
at 12: remove <=2 -> [3], push 12 -> [3,12] allow
```

### Production Use

```text
API gateway rate limiting
Login attempt tracking
Fraud detection windows
Rolling error counters
Per-user throttling
```

### Production Failure Story

```text
Bug:
Rate limiter stored timestamps forever.

Effect:
Memory grew for inactive users.

Fix:
Expire old timestamps and remove inactive user keys.
```

---

## 7. priority_queue -> Job Scheduler and Retry Engine

### CP Pattern

Heap gives current min/max/best item repeatedly.

### Real-World Problem

Retry failed payments at the earliest scheduled retry time.

```text
failed job -> retryAt timestamp -> min heap
```

### ASCII Diagram

```text
Min heap ordered by retryAt

          retryAt=10
         /          \
   retryAt=15     retryAt=20
     /    \
retry=30 retry=40

worker checks heap.top()
if retryAt <= now -> process
else sleep/wait
```

### C++ Pattern

```cpp
#include <bits/stdc++.h>
using namespace std;

struct RetryJob {
    int retryAt;
    int jobId;

    bool operator>(const RetryJob& other) const {
        return retryAt > other.retryAt;
    }
};

priority_queue<RetryJob, vector<RetryJob>, greater<RetryJob>> retryHeap;
```

### Example Problem Description

You have failed jobs:

```text
job 1 retryAt 30
job 2 retryAt 10
job 3 retryAt 20
```

Process jobs in retry time order.

### Dry Run

```text
push (30,1)
push (10,2)
push (20,3)

heap top = (10,2)
process job 2
heap top = (20,3)
process job 3
heap top = (30,1)
process job 1
```

### Production Use

```text
Retry scheduler
Delayed job queue
Task priority scheduler
Dijkstra-like routing cost engines
Top K metrics
Leaderboards
```

### Production Failure Story

```text
Bug:
Retry jobs were reinserted without max retry count.

Effect:
Poison messages retried forever.

Fix:
Add attempt count, exponential backoff, and dead-letter queue.
```

---

## 8. unordered_map -> Cache and Metadata Lookup

### CP Pattern

Hash map supports average O(1) key-value lookup.

### Real-World Problem

Map short URL code to long URL.

```text
abc123 -> https://example.com/product/123
```

### ASCII Diagram

```text
Request /abc123
      |
      v
unordered_map cache
      |
      +-- hit  -> return long URL
      +-- miss -> query DB -> update cache
```

### C++ Pattern

```cpp
#include <bits/stdc++.h>
using namespace std;

class UrlCache {
    unordered_map<string, string> cache;
public:
    void put(const string& code, const string& url) {
        cache[code] = url;
    }

    optional<string> get(const string& code) {
        if (!cache.count(code)) return nullopt;
        return cache[code];
    }
};
```

### Dry Run

```text
put abc -> google.com
get abc -> hit
overwrite abc -> openai.com
get abc -> openai.com
get xyz -> miss
```

### Production Use

```text
Redis cache mental model
In-memory metadata
Session lookup
Frequency counter
Idempotency key store
Deduplication index
```

### Production Failure Story

```text
Bug:
Cache had no TTL.

Effect:
User saw stale profile data after update.

Fix:
Use TTL, cache invalidation, or write-through strategy.
```

---

## 9. map -> Ordered Index and Time-Ordered Events

### CP Pattern

`map` keeps keys sorted and supports predecessor/successor queries.

### Real-World Problem

Find the latest price update before a timestamp.

```text
price at t=10 -> 100
price at t=20 -> 110
price at t=40 -> 105
query t=25 -> latest before 25 is t=20 -> 110
```

### ASCII Diagram

```text
ordered map by timestamp:

10 -> 100
20 -> 110
40 -> 105

query 25
  |
  v
upper_bound(25) -> 40
previous        -> 20
answer          -> 110
```

### C++ Pattern

```cpp
#include <bits/stdc++.h>
using namespace std;

class TimeSeriesIndex {
    map<int, int> priceAtTime;
public:
    void update(int time, int price) {
        priceAtTime[time] = price;
    }

    optional<int> getLatest(int time) {
        auto it = priceAtTime.upper_bound(time);
        if (it == priceAtTime.begin()) return nullopt;
        --it;
        return it->second;
    }
};
```

### Production Use

```text
Audit logs
Versioned config
Feature flag timeline
Price history
Ordered event replay
```

### Production Failure Story

```text
Bug:
Used unordered_map for time-versioned config.

Effect:
Could not answer latest-before-time queries efficiently.

Fix:
Use map/TreeMap or database index on timestamp.
```

---

## 10. set -> Deduplication and Active Session Index

### CP Pattern

Set stores unique sorted values.

### Real-World Problem

Prevent processing duplicate event IDs.

### ASCII Diagram

```text
Incoming events:

E1 E2 E1 E3 E2

seen set:

E1 -> not seen -> process -> insert
E2 -> not seen -> process -> insert
E1 -> seen     -> skip
E3 -> not seen -> process -> insert
E2 -> seen     -> skip
```

### C++ Pattern

```cpp
#include <bits/stdc++.h>
using namespace std;

class Deduper {
    unordered_set<string> seen;
public:
    bool shouldProcess(const string& eventId) {
        if (seen.count(eventId)) return false;
        seen.insert(eventId);
        return true;
    }
};
```

### Real-World Uses

```text
Idempotency keys
Event dedupe
Active user sessions
Unique online users
Processed message IDs
```

### Production Failure Story

```text
Bug:
Dedup set was in memory only.

Effect:
After service restart, duplicate payments were processed.

Fix:
Persist idempotency keys in Redis/DB with TTL.
```

---

## 11. multiset -> Live Median and Latency Window

### CP Pattern

Multiset stores sorted values with duplicates and supports erase one occurrence.

### Real-World Problem

Maintain rolling p50 latency of last N requests.

### ASCII Diagram

```text
latencies in current window:

[120, 80, 200, 100, 100]

multiset sorted:

80 100 100 120 200
      ^
    median p50
```

### C++ Pattern — Two Multisets Median

```cpp
#include <bits/stdc++.h>
using namespace std;

class RollingMedian {
    multiset<int> lo, hi;

    void balance() {
        while (lo.size() < hi.size()) {
            auto it = hi.begin();
            lo.insert(*it);
            hi.erase(it);
        }
        while (lo.size() > hi.size() + 1) {
            auto it = prev(lo.end());
            hi.insert(*it);
            lo.erase(it);
        }
    }

public:
    void insert(int x) {
        if (lo.empty() || x <= *lo.rbegin()) lo.insert(x);
        else hi.insert(x);
        balance();
    }

    void eraseOne(int x) {
        auto it = lo.find(x);
        if (it != lo.end()) lo.erase(it);
        else {
            it = hi.find(x);
            if (it != hi.end()) hi.erase(it);
        }
        balance();
    }

    double median() const {
        if (lo.size() > hi.size()) return *lo.rbegin();
        return (*lo.rbegin() + *hi.begin()) / 2.0;
    }
};
```

### Production Use

```text
Rolling p50 latency
Dynamic leaderboard with duplicate scores
Sliding window median
Real-time dashboards
```

### Critical Bug

```text
Wrong:
ms.erase(x);       // removes all copies of x

Right:
ms.erase(ms.find(x));  // removes one copy
```

---

# PART 2 — STL Patterns as Production Algorithms

---

## 12. Prefix Sum -> Billing and Analytics

### CP Pattern

Prefix sum answers range sum queries quickly.

### Real-World Problem

Calculate total API calls for customer C between day L and day R.

### ASCII Diagram

```text
Daily usage:

day:      1   2   3   4   5
calls:   10  20  30  40  50
prefix:  10  30  60 100 150

query day 2..4:
answer = prefix[4] - prefix[1]
       = 100 - 10
       = 90
```

### C++ Pattern

```cpp
vector<long long> buildPrefix(const vector<int>& a) {
    int n = a.size();
    vector<long long> pref(n + 1, 0);
    for (int i = 0; i < n; i++) {
        pref[i + 1] = pref[i] + a[i];
    }
    return pref;
}

long long rangeSum(const vector<long long>& pref, int l, int r) {
    return pref[r + 1] - pref[l];
}
```

### Production Use

```text
Billing calculations
Usage analytics
Daily/weekly/monthly reports
Cumulative counters
Read-heavy dashboards
```

### Production Failure Story

```text
Bug:
Billing used raw daily scan for every invoice.

Effect:
Invoice job became O(customers * days * queries).

Fix:
Precompute cumulative usage per customer.
```

---

## 13. Prefix + Hash Map -> Idempotency and Subarray Analytics

### CP Pattern

Count previous prefix states using a map.

### Real-World Problem

Find suspicious transaction segments where total amount equals exactly 10,000.

### ASCII Diagram

```text
transactions:
[2000, 3000, 5000, -1000, 11000]

target = 10000

prefix running:
2000, 5000, 10000, 9000, 20000

At prefix=10000:
need prefix-target = 0
0 existed before -> segment found
```

### C++ Pattern

```cpp
long long countSegmentsWithSum(vector<int>& a, long long target) {
    unordered_map<long long, long long> freq;
    freq[0] = 1;
    long long pref = 0, ans = 0;

    for (int x : a) {
        pref += x;
        ans += freq[pref - target];
        freq[pref]++;
    }
    return ans;
}
```

### Production Use

```text
Fraud segment detection
Analytics windows with exact target
Cumulative event deltas
Ledger reconciliation
```

---

## 14. Sliding Window -> Rolling Metrics

### CP Pattern

Maintain answer while window moves.

### Real-World Problem

Track error rate in the last 5 minutes.

### ASCII Diagram

```text
Current time = 12:00
Window = 11:55 to 12:00

Events:
11:54 error  -> expired
11:56 ok     -> valid
11:58 error  -> valid
11:59 error  -> valid

rolling error rate = errors / valid events
```

### C++ Pattern

```cpp
struct Event {
    int time;
    bool error;
};

class RollingErrorRate {
    int window;
    deque<Event> dq;
    int errors = 0;

public:
    RollingErrorRate(int window) : window(window) {}

    double add(int now, bool isError) {
        dq.push_back({now, isError});
        if (isError) errors++;

        while (!dq.empty() && dq.front().time <= now - window) {
            if (dq.front().error) errors--;
            dq.pop_front();
        }

        if (dq.empty()) return 0.0;
        return (double)errors / dq.size();
    }
};
```

### Production Use

```text
SLO burn rate
API error-rate alerting
Rolling QPS
Fraud attempts per minute
Login failures
```

---

## 15. Monotonic Deque -> Rolling Minimum/Maximum Dashboard

### CP Pattern

Maintain candidates in sorted order inside the window.

### Real-World Problem

Monitor minimum available inventory in the last 10 readings.

### ASCII Diagram

```text
readings: [40, 35, 50, 20, 25]

min deque after each insert:
40        -> [40]
35        -> pop 40 -> [35]
50        -> [35,50]
20        -> pop 50, pop 35 -> [20]
25        -> [20,25]

front = rolling min
```

### C++ Pattern

```cpp
class MonotonicMinQueue {
    deque<pair<int,int>> dq; // {index, value}
public:
    void push(int index, int value) {
        while (!dq.empty() && dq.back().second > value) dq.pop_back();
        dq.push_back({index, value});
    }

    void expire(int minValidIndex) {
        while (!dq.empty() && dq.front().first < minValidIndex) dq.pop_front();
    }

    int getMin() const {
        return dq.front().second;
    }
};
```

### Production Use

```text
Rolling min/max latency
Inventory low-water mark
CPU peak in last N samples
Network bandwidth spike detection
```

---

## 16. Monotonic Stack -> Boundary Detection and Cleanup

### CP Pattern

Find nearest greater/smaller boundary.

### Real-World Problem

Given daily traffic, find for each day how long until traffic exceeds today.

### ASCII Diagram

```text
traffic = [100, 80, 120, 90]

Day 0 waits for >100 -> day 2
Day 1 waits for >80  -> day 2
Day 2 waits for >120 -> none
Day 3 waits for >90  -> none

Stack unresolved:
[100,80]
current 120 resolves both
```

### C++ Pattern

```cpp
vector<int> nextHigherTraffic(vector<int>& traffic) {
    int n = traffic.size();
    vector<int> ans(n, -1);
    stack<int> st;

    for (int i = 0; i < n; i++) {
        while (!st.empty() && traffic[i] > traffic[st.top()]) {
            ans[st.top()] = i;
            st.pop();
        }
        st.push(i);
    }
    return ans;
}
```

### Production Use

```text
Next threshold crossing
Capacity planning
Price spike detection
Traffic anomaly boundary
Compression of increasing/decreasing states
```

---

## 17. Contribution Technique -> Revenue Attribution

### CP Pattern

Instead of enumerating all subarrays, count how often each element contributes.

### Real-World Problem

Estimate how much each day contributes to all possible reporting windows.

### ASCII Diagram

```text
daily revenue = [10, 20, 30]

All report windows:
[10]
[10,20]
[10,20,30]
[20]
[20,30]
[30]

Contribution count:
day 0 appears in 3 windows
day 1 appears in 4 windows
day 2 appears in 3 windows
```

### Formula

```text
contribution of index i = value[i] * (i + 1) * (n - i)
```

### Production Use

```text
Attribution analytics
Window-based reporting
Impact scoring
Feature usage contribution
```

---

## 18. Top K -> Leaderboards and Heavy Hitters

### CP Pattern

Use heap or ordered set to maintain top K.

### Real-World Problem

Show top 10 most active users in the last hour.

### ASCII Diagram

```text
frequency map:

U1 -> 120
U2 -> 50
U3 -> 300
U4 -> 10

min heap size K=2:

push U1 120 -> [120]
push U2 50  -> [50,120]
push U3 300 -> [50,120,300] -> pop 50 -> [120,300]
push U4 10  -> [10,120,300] -> pop 10 -> [120,300]

top users: U3, U1
```

### C++ Pattern

```cpp
vector<pair<string,int>> topKUsers(unordered_map<string,int>& freq, int k) {
    using P = pair<int,string>; // {count, user}
    priority_queue<P, vector<P>, greater<P>> pq;

    for (auto& [user, count] : freq) {
        pq.push({count, user});
        if ((int)pq.size() > k) pq.pop();
    }

    vector<pair<string,int>> ans;
    while (!pq.empty()) {
        ans.push_back({pq.top().second, pq.top().first});
        pq.pop();
    }
    reverse(ans.begin(), ans.end());
    return ans;
}
```

### Production Use

```text
Top customers
Most viewed products
Hot URLs
Heavy API users
Trending hashtags
```

---

## 19. Two Heaps / Two Multisets -> Streaming Median

### CP Pattern

Maintain lower half and upper half.

### Real-World Problem

Real-time p50 latency dashboard.

### ASCII Diagram

```text
Lower half max heap:  [100, 90, 80]
Upper half min heap:  [110, 120, 150]

median = average(100,110) = 105

Invariant:
max(lower) <= min(upper)
sizes differ by at most 1
```

### C++ Pattern

```cpp
class MedianStream {
    priority_queue<int> lo;
    priority_queue<int, vector<int>, greater<int>> hi;

    void balance() {
        if (lo.size() > hi.size() + 1) {
            hi.push(lo.top()); lo.pop();
        } else if (hi.size() > lo.size()) {
            lo.push(hi.top()); hi.pop();
        }
    }

public:
    void add(int x) {
        if (lo.empty() || x <= lo.top()) lo.push(x);
        else hi.push(x);
        balance();
    }

    double median() const {
        if (lo.size() == hi.size()) return (lo.top() + hi.top()) / 2.0;
        return lo.top();
    }
};
```

### Production Use

```text
p50 latency
Median order value
Median transaction size
Streaming analytics
```

---

## 20. Interval Set -> Booking and Availability System

### CP Pattern

Maintain non-overlapping sorted intervals.

### Real-World Problem

Room booking system: reject overlapping bookings.

### ASCII Diagram

```text
Existing bookings:

[10,12] [14,16]

new booking [12,14]
If half-open intervals [start,end), it is valid:
[10,12) [12,14) [14,16)

new booking [11,13] overlaps [10,12)
reject
```

### C++ Pattern

```cpp
class BookingCalendar {
    set<pair<int,int>> bookings; // [start,end)
public:
    bool book(int start, int end) {
        auto it = bookings.lower_bound({start, end});

        if (it != bookings.end() && it->first < end) return false;

        if (it != bookings.begin()) {
            auto prevIt = prev(it);
            if (prevIt->second > start) return false;
        }

        bookings.insert({start, end});
        return true;
    }
};
```

### Production Use

```text
Hotel room booking
Calendar scheduling
Meeting room allocation
Delivery time slots
Doctor appointment availability
```

### Production Failure Story

```text
Bug:
System used closed intervals [start,end].

Effect:
Booking ending at 10 blocked another booking starting at 10.

Fix:
Use half-open intervals [start,end).
```

---

## 21. Merged Interval Set -> IP Ranges and Feature Windows

### CP Pattern

Insert interval and merge overlaps.

### Real-World Problem

Maintain blocked IP ranges.

### ASCII Diagram

```text
Existing blocked ranges:
[10,20] [30,40]

insert [18,35]

merge [10,20] + [18,35] + [30,40]

result:
[10,40]
```

### C++ Pattern

```cpp
class RangeSet {
    set<pair<int,int>> ranges;
public:
    void addRange(int l, int r) {
        auto it = ranges.lower_bound({l, INT_MIN});

        if (it != ranges.begin()) {
            auto p = prev(it);
            if (p->second + 1 >= l) it = p;
        }

        while (it != ranges.end() && it->first <= r + 1) {
            l = min(l, it->first);
            r = max(r, it->second);
            it = ranges.erase(it);
        }

        ranges.insert({l, r});
    }

    bool contains(int x) {
        auto it = ranges.upper_bound({x, INT_MAX});
        if (it == ranges.begin()) return false;
        --it;
        return it->second >= x;
    }
};
```

### Production Use

```text
Blocked IP ranges
Feature rollout windows
Maintenance windows
Coupon validity windows
Geo range coverage
```

---

## 22. Difference Array -> Bulk Range Updates

### CP Pattern

Range add in O(1), final values by prefix.

### Real-World Problem

Apply promotional credit to all users in ID range `[l,r]`.

### ASCII Diagram

```text
add +10 to [2,5]

diff[2] += 10
diff[6] -= 10

prefix creates final values:
index: 1  2  3  4  5  6
value: 0 10 10 10 10 0
```

### C++ Pattern

```cpp
vector<long long> applyRangeAdds(int n, vector<tuple<int,int,int>>& ops) {
    vector<long long> diff(n + 2, 0);

    for (auto [l, r, val] : ops) {
        diff[l] += val;
        diff[r + 1] -= val;
    }

    vector<long long> ans(n + 1, 0);
    for (int i = 1; i <= n; i++) {
        ans[i] = ans[i - 1] + diff[i];
    }
    return ans;
}
```

### Production Use

```text
Bulk credits
Campaign adjustments
Inventory region updates
Configuration rollout over ID ranges
```

---

## 23. Sweep Line -> Concurrent Users and Max Overlap

### CP Pattern

Convert intervals into start/end events and scan sorted events.

### Real-World Problem

Find maximum concurrent users from session start/end times.

### ASCII Diagram

```text
Sessions:
A [1,5]
B [2,6]
C [4,7]

Events:
1 +1
2 +1
4 +1
5 -1
6 -1
7 -1

scan:
time 1 -> active 1
time 2 -> active 2
time 4 -> active 3 max
time 5 -> active 2
```

### C++ Pattern

```cpp
int maxConcurrent(vector<pair<int,int>>& sessions) {
    vector<pair<int,int>> events;
    for (auto [l, r] : sessions) {
        events.push_back({l, +1});
        events.push_back({r, -1});
    }

    sort(events.begin(), events.end());

    int active = 0, best = 0;
    for (auto [time, delta] : events) {
        active += delta;
        best = max(best, active);
    }
    return best;
}
```

### Production Use

```text
Concurrent users
Server capacity planning
Meeting room count
Maximum overlapping bookings
Peak load analysis
```

### Production Note

```text
For half-open intervals [start,end), process end before start at same time.
For closed intervals [start,end], process start before end at same time.
```

---

## 24. Coordinate Compression -> Huge Sparse IDs

### CP Pattern

Map huge coordinates to compact indexes.

### Real-World Problem

User IDs are large, but only a few are active in a query window.

```text
IDs: 1000000007, 42, 999999999
compressed: 2, 0, 1
```

### ASCII Diagram

```text
original IDs:
[1000000007, 42, 999999999]

sort unique:
[42, 999999999, 1000000007]

mapping:
42         -> 0
999999999  -> 1
1000000007 -> 2
```

### C++ Pattern

```cpp
vector<int> compress(vector<int>& values) {
    vector<int> sorted = values;
    sort(sorted.begin(), sorted.end());
    sorted.erase(unique(sorted.begin(), sorted.end()), sorted.end());

    vector<int> result;
    for (int x : values) {
        int id = lower_bound(sorted.begin(), sorted.end(), x) - sorted.begin();
        result.push_back(id);
    }
    return result;
}
```

### Production Use

```text
Sparse user IDs to array index
Feature vector encoding
Analytics buckets
Compressed metric dimensions
Memory-efficient indexing
```

---

# PART 3 — Real-World Mini Projects Using STL Thinking

---

## 25. Mini Project 1 — URL Shortener Read Path

### Real-World Problem

Resolve short code to long URL quickly.

### STL Mapping

```text
unordered_map -> cache lookup
map           -> sorted audit/version history
queue         -> async click event pipeline
vector        -> batch analytics flush
```

### ASCII Architecture

```text
GET /abc123
    |
    v
cache unordered_map / Redis
    |
    +-- hit  -> redirect
    |
    +-- miss -> DB lookup -> cache put -> redirect

click event
    |
    v
queue / Kafka
    |
    v
vector batch -> analytics DB
```

### Interview Explanation

```text
The core read path is a hash lookup. Click analytics are decoupled using queue semantics and processed in batches using vector-like accumulation.
```

---

## 26. Mini Project 2 — API Gateway Rate Limiter

### STL Mapping

```text
unordered_map<user, deque<timestamp>>
```

### ASCII Architecture

```text
Request(userId)
      |
      v
map user -> deque timestamps
      |
      +-- remove expired timestamps
      +-- if size >= limit reject
      +-- else push now and allow
```

### Production Concerns

```text
Memory cleanup
Distributed rate limiting
Clock skew
Burst handling
Atomic updates in Redis
```

---

## 27. Mini Project 3 — Notification Retry Scheduler

### STL Mapping

```text
priority_queue<retryAt, jobId>
queue for immediate jobs
unordered_set for idempotency
```

### ASCII Architecture

```text
failed notification
      |
      v
compute retryAt
      |
      v
min heap by retryAt
      |
      v
worker picks due jobs
      |
      +-- success -> done
      +-- fail    -> retry with backoff
      +-- max attempts -> DLQ
```

---

## 28. Mini Project 4 — Booking Calendar

### STL Mapping

```text
set<pair<start,end>> bookings
```

### ASCII Architecture

```text
new booking [s,e)
      |
      v
find first booking with start >= s
      |
      +-- check next overlap
      +-- check previous overlap
      |
      v
insert or reject
```

---

## 29. Mini Project 5 — Real-Time Metrics Dashboard

### STL Mapping

```text
vector        -> metric batch
unordered_map -> metric name -> state
deque         -> rolling window
multiset      -> rolling median
priority_queue -> top K endpoints
```

### ASCII Architecture

```text
metric events
     |
     v
batch vector
     |
     v
per endpoint state
     |
     +-- deque for last N events
     +-- multiset for p50
     +-- heap for top K slow endpoints
```

---

# PART 4 — Production Failure Patterns

## 30. STL Pattern Failure Table

| STL Pattern | Real Use | Common Production Failure | Fix |
|---|---|---|---|
| vector batch | Kafka consumer | batch grows forever | flush + clear + cap |
| unordered_map cache | metadata lookup | stale data | TTL/invalidation |
| queue | async jobs | unbounded backlog | backpressure/autoscale |
| deque | rate limiter | inactive users leak memory | TTL cleanup |
| priority_queue | retry scheduler | poison retry forever | max attempts + DLQ |
| multiset | rolling median | erase all duplicates | erase iterator only |
| set intervals | booking | boundary overlap bug | use [start,end) |
| prefix sum | billing | overflow | long long |
| sweep line | concurrency | wrong tie ordering | define interval semantics |

---

# PART 5 — Interview Story Templates

## 31. Explain STL in Backend Terms

```text
In CP, STL gives fast local data structures.
In backend systems, the same patterns become service primitives:

hash map  -> cache/index
queue     -> async pipeline
deque     -> sliding window/rate limiter
heap      -> scheduler/top-K
set       -> ordered index/interval manager
prefix    -> analytics/billing precomputation
```

## 32. Example Interview Answer — Rate Limiter

```text
I model a sliding-window rate limiter using unordered_map from userId to deque of request timestamps. For each request, I remove expired timestamps from the front, check the current size, and either reject or append the new timestamp. This is the same as a CP sliding-window deque pattern. In production, I would implement it in Redis with TTL and atomic Lua/script operations to support multiple gateway instances.
```

## 33. Example Interview Answer — Retry Scheduler

```text
I model retries using a min heap ordered by retryAt timestamp. The worker repeatedly checks the earliest retry job. If it is due, it processes it; otherwise it waits. Failed jobs are reinserted with exponential backoff. Production concerns are max retry count, idempotency, poison messages, and dead-letter queues.
```

## 34. Example Interview Answer — Booking System

```text
I store bookings as sorted half-open intervals [start,end). To insert a new booking, I find the first interval with start >= newStart and check overlap with both the next and previous interval. If no overlap exists, insert. The set gives O(log n) lookup and insertion. The key design choice is using half-open intervals to avoid boundary bugs.
```

---

# PART 6 — Final Cheat Sheet

## 35. Real-World Recognition Table

| Real-World Requirement | Think STL Pattern |
|---|---|
| Fast lookup by ID | unordered_map |
| Unique event IDs | unordered_set |
| Ordered latest-before query | map |
| Active sorted ranges | set<pair<int,int>> |
| Async processing | queue |
| Retry by earliest time | min heap |
| Top K users/products | heap |
| Rolling last N events | deque |
| Rolling p50 | two heaps / multiset |
| Rolling min/max | monotonic deque |
| Bracket/config validation | stack |
| Billing range sum | prefix sum |
| Bulk range update | difference array |
| Max concurrent sessions | sweep line |
| Huge sparse IDs | coordinate compression |

## 36. One Picture Summary

```text
                    REAL WORLD PROBLEM
                            |
                            v
                 What operation repeats?
                            |
       +--------------------+--------------------+
       |                    |                    |
    lookup              ordering              window
       |                    |                    |
unordered_map        set/map/heap           deque/multiset
       |                    |                    |
cache/session      booking/scheduler       rate/metrics

       +--------------------+--------------------+
                            |
                         ranges/time
                            |
              prefix / diff / sweep / intervals
                            |
             billing / analytics / availability
```

## 37. Final Golden Rules

```text
1. If you need fast ID lookup, think hash map.
2. If you need ordering, think map/set/heap.
3. If you need FIFO async processing, think queue.
4. If you need rolling time windows, think deque.
5. If you need top K or earliest retry, think heap.
6. If you need dynamic median, think two heaps or multisets.
7. If you need bookings/availability, think interval set.
8. If you need cumulative reporting, think prefix sum.
9. If you need bulk range updates, think difference array.
10. If you need max overlap, think sweep line.
```

---

# Appendix A — What This File Does Not Cover Yet

These are intentionally excluded and should be covered later in separate notes:

```text
Advanced graph algorithms
Advanced dynamic programming
Segment tree deep dive
Fenwick tree deep dive
Treap/PBDS internals
Distributed systems implementation details
Database indexing internals
Kafka/Redis production configuration
```

---

# Appendix B — Recommended Next Files

```text
001_STL_Foundation.md
002_STL_Pattern_Recognition.md
003_STL_Problem_Ladder.md
004_MiniRealWorldSTL.md
005_MiniBackendDataStructures.md
```
