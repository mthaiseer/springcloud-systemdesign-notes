# 006_What_Is_Difference_Array.md — MiniPrefixSumDifferenceEngine

# What Is Difference Array?

> Difference Array is the opposite partner of Prefix Sum.
>
> Prefix Sum helps with:
>
> ```text
> many range sum queries
> ```
>
> Difference Array helps with:
>
> ```text
> many range update queries
> ```

---

## Clickable Index

1. [What Is Difference Array?](#1-what-is-difference-array)
2. [Why Difference Array Matters](#2-why-difference-array-matters)
3. [Prefix Sum vs Difference Array](#3-prefix-sum-vs-difference-array)
4. [Core Mental Model](#4-core-mental-model)
5. [Range Update Problem](#5-range-update-problem)
6. [Brute Force Range Update](#6-brute-force-range-update)
7. [Difference Array Intuition](#7-difference-array-intuition)
8. [Range Add Formula](#8-range-add-formula)
9. [Why diff[L] += X And diff[R+1] -= X Works](#9-why-diffl--x-and-diffr1---x-works)
10. [Step-by-Step Dry Run — Applying Updates](#10-step-by-step-dry-run--applying-updates)
11. [Step-by-Step Dry Run — Rebuilding Final Array](#11-step-by-step-dry-run--rebuilding-final-array)
12. [Difference Array From Existing Array](#12-difference-array-from-existing-array)
13. [Problem Form 1 — Range Add Final Array](#13-problem-form-1--range-add-final-array)
14. [Problem Form 2 — Multiple Increment Queries](#14-problem-form-2--multiple-increment-queries)
15. [Problem Form 3 — Flight Bookings](#15-problem-form-3--flight-bookings)
16. [Problem Form 4 — Car Pooling / Capacity Timeline](#16-problem-form-4--car-pooling--capacity-timeline)
17. [Problem Form 5 — Event Timeline Sweep](#17-problem-form-5--event-timeline-sweep)
18. [Real World Model 1 — Traffic Burst Timeline](#18-real-world-model-1--traffic-burst-timeline)
19. [Real World Model 2 — Kafka-Style Event Delta Accumulation](#19-real-world-model-2--kafka-style-event-delta-accumulation)
20. [Real World Model 3 — Batch Price Update](#20-real-world-model-3--batch-price-update)
21. [Real World Model 4 — Calendar Booking Load](#21-real-world-model-4--calendar-booking-load)
22. [Decision Tree](#22-decision-tree)
23. [Common Mistakes](#23-common-mistakes)
24. [Complexity](#24-complexity)
25. [Reusable C++ Templates](#25-reusable-c-templates)
26. [CP / FAANG Problem Forms](#26-cp--faang-problem-forms)
27. [Practice Checklist](#27-practice-checklist)
28. [Next Step](#28-next-step)

---

## 1. What Is Difference Array?

Difference Array is a technique to apply many range updates efficiently.

Suppose we have an array of size `n` initially all zeros:

```text
a = [0, 0, 0, 0, 0, 0]
```

Query:

```text
add 5 to all indexes from L=2 to R=4
```

Brute force updates:

```text
a[2] += 5
a[3] += 5
a[4] += 5
```

Difference array instead marks only:

```text
diff[2] += 5
diff[5] -= 5
```

Then later, prefix sum of `diff` reconstructs the final array.

---

## 2. Why Difference Array Matters

When there are many range updates, brute force is too slow.

Example:

```text
n = 200000
q = 200000
```

Each update may touch `O(n)` cells.

Brute force:

```text
O(n*q)
```

Difference array:

```text
O(q + n)
```

This is a massive optimization.

---

## 3. Prefix Sum vs Difference Array

| Problem Type | Technique |
|---|---|
| Many range sum queries | Prefix Sum |
| Many range add updates | Difference Array |
| Static array query | Prefix Sum |
| Batch updates then final array | Difference Array |
| Need query after updates dynamically | Fenwick / Segment Tree |

Mental shortcut:

```text
Prefix Sum:
precompute total to answer range query

Difference Array:
mark change points to apply range update
```

---

## 4. Core Mental Model

Difference array stores:

```text
where change starts
where change stops
```

For update:

```text
add X on [L, R]
```

We say:

```text
start adding X at L
stop adding X after R
```

So:

```text
diff[L] += X
diff[R+1] -= X
```

Then prefix sum of `diff` carries the effect forward.

---

## 5. Range Update Problem

Problem:

```text
Array size n = 6
Initially all zeros
Queries:
1) add 1 on [2,4]
2) add 3 on [1,5]
3) add 2 on [0,2]
```

Need final array.

Expected final:

```text
[2, 5, 6, 4, 4, 3]
```

---

## 6. Brute Force Range Update

Brute force:

```cpp
for each query:
    for i from L to R:
        a[i] += X
```

For large input:

```text
too slow
```

Example:

```text
q = 200000
n = 200000
```

Worst case:

```text
40 billion operations
```

---

## 7. Difference Array Intuition

Instead of updating all cells, mark only boundaries.

For:

```text
add X on [L,R]
```

Do:

```text
diff[L] += X
diff[R+1] -= X
```

Meaning:

```text
from L onwards, value increases by X
from R+1 onwards, cancel that X
```

When we prefix sum `diff`, the update is spread automatically.

---

## 8. Range Add Formula

For 0-indexed array:

```text
add X on [L,R]
```

Formula:

```text
diff[L] += X

if R + 1 < n:
    diff[R + 1] -= X
```

Often we allocate:

```text
diff size = n + 1
```

Then we can safely do:

```text
diff[R + 1] -= X
```

even when:

```text
R = n - 1
```

---

## 9. Why diff[L] += X And diff[R+1] -= X Works

Example:

```text
n = 6
add 5 on [2,4]
```

Mark:

```text
diff[2] += 5
diff[5] -= 5
```

Diff:

```text
index:  0  1  2  3  4  5  6
diff : [0, 0, 5, 0, 0,-5, 0]
```

Prefix rebuild:

```text
i=0 -> 0
i=1 -> 0
i=2 -> 5
i=3 -> 5
i=4 -> 5
i=5 -> 0
```

Final:

```text
[0, 0, 5, 5, 5, 0]
```

Exactly `[2,4]` got +5.

---

## 10. Step-by-Step Dry Run — Applying Updates

Given:

```text
n = 6
queries:
1) add 1 on [2,4]
2) add 3 on [1,5]
3) add 2 on [0,2]
```

Initial diff:

```text
index:  0  1  2  3  4  5  6
diff : [0, 0, 0, 0, 0, 0, 0]
```

---

### Query 1: add 1 on [2,4]

```text
diff[2] += 1
diff[5] -= 1
```

State:

```text
diff = [0, 0, 1, 0, 0, -1, 0]
```

---

### Query 2: add 3 on [1,5]

```text
diff[1] += 3
diff[6] -= 3
```

State:

```text
diff = [0, 3, 1, 0, 0, -1, -3]
```

---

### Query 3: add 2 on [0,2]

```text
diff[0] += 2
diff[3] -= 2
```

State:

```text
diff = [2, 3, 1, -2, 0, -1, -3]
```

---

## 11. Step-by-Step Dry Run — Rebuilding Final Array

Now take prefix sum of `diff`.

Diff:

```text
index:  0  1  2   3  4   5   6
diff : [2, 3, 1, -2, 0, -1, -3]
```

Rebuild for first `n=6` positions.

### i = 0

```text
running = 0 + diff[0] = 2
a[0] = 2
```

### i = 1

```text
running = 2 + diff[1] = 5
a[1] = 5
```

### i = 2

```text
running = 5 + diff[2] = 6
a[2] = 6
```

### i = 3

```text
running = 6 + diff[3] = 4
a[3] = 4
```

### i = 4

```text
running = 4 + diff[4] = 4
a[4] = 4
```

### i = 5

```text
running = 4 + diff[5] = 3
a[5] = 3
```

Final:

```text
a = [2, 5, 6, 4, 4, 3]
```

---

## 12. Difference Array From Existing Array

If original array is not all zeros:

```text
a = [5, 7, 10, 10]
```

Difference array:

```text
diff[0] = a[0]
diff[i] = a[i] - a[i-1]
```

Compute:

```text
diff[0] = 5
diff[1] = 7 - 5 = 2
diff[2] = 10 - 7 = 3
diff[3] = 10 - 10 = 0
```

So:

```text
diff = [5, 2, 3, 0]
```

Prefix of diff gives original array:

```text
5
5+2=7
7+3=10
10+0=10
```

---

## 13. Problem Form 1 — Range Add Final Array

### Problem

Given `n` and range updates:

```text
L R X
```

Apply all updates and print final array.

---

### Pattern

```text
Difference Array
```

---

### Step-by-Step Working

Input:

```text
n = 5
updates:
[1,3,+2]
[2,4,+3]
```

Initial:

```text
diff = [0,0,0,0,0,0]
```

Apply `[1,3,+2]`:

```text
diff[1] += 2
diff[4] -= 2
```

```text
diff = [0,2,0,0,-2,0]
```

Apply `[2,4,+3]`:

```text
diff[2] += 3
diff[5] -= 3
```

```text
diff = [0,2,3,0,-2,-3]
```

Prefix rebuild:

```text
a[0] = 0
a[1] = 2
a[2] = 5
a[3] = 5
a[4] = 3
```

Final:

```text
[0,2,5,5,3]
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n = 5;

    vector<tuple<int,int,int>> updates = {
        {1, 3, 2},
        {2, 4, 3}
    };

    // n+1 allows us to safely mark diff[R+1]
    vector<long long> diff(n + 1, 0);

    for (auto [l, r, x] : updates) {

        // Start adding x from l
        diff[l] += x;

        // Stop adding x after r
        diff[r + 1] -= x;
    }

    vector<long long> finalArray(n, 0);

    long long running = 0;

    for (int i = 0; i < n; i++) {
        running += diff[i];
        finalArray[i] = running;
    }

    for (long long x : finalArray) {
        cout << x << " ";
    }

    return 0;
}
```

---

## 14. Problem Form 2 — Multiple Increment Queries

### Problem

You are given many queries:

```text
increment all elements in [L,R] by 1
```

Find final values.

---

### Pattern

```text
Difference Array with X = 1
```

---

### Step-by-Step Working

```text
n = 5
queries:
[0,2]
[1,3]
[2,4]
```

Diff initially:

```text
[0,0,0,0,0,0]
```

After `[0,2]`:

```text
diff[0]+=1
diff[3]-=1
=> [1,0,0,-1,0,0]
```

After `[1,3]`:

```text
diff[1]+=1
diff[4]-=1
=> [1,1,0,-1,-1,0]
```

After `[2,4]`:

```text
diff[2]+=1
diff[5]-=1
=> [1,1,1,-1,-1,-1]
```

Prefix:

```text
[1,2,3,2,1]
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n = 5;

    vector<pair<int,int>> queries = {
        {0,2},
        {1,3},
        {2,4}
    };

    vector<int> diff(n + 1, 0);

    for (auto [l, r] : queries) {
        diff[l] += 1;
        diff[r + 1] -= 1;
    }

    int running = 0;

    for (int i = 0; i < n; i++) {
        running += diff[i];
        cout << running << " ";
    }

    return 0;
}
```

---

## 15. Problem Form 3 — Flight Bookings

### Problem

There are `n` flights.

Each booking is:

```text
firstFlight lastFlight seats
```

Add `seats` to every flight in that range.

Return seats booked for each flight.

---

### Pattern

```text
Difference Array
```

This is the classic:

```text
Corporate Flight Bookings
```

---

### Step-by-Step Working

```text
n = 5
bookings:
[1,2,10]
[2,3,20]
[2,5,25]
```

These are 1-indexed flight numbers.

Convert to 0-indexed:

```text
[0,1,+10]
[1,2,+20]
[1,4,+25]
```

Apply diff:

```text
diff[0]+=10, diff[2]-=10
diff[1]+=20, diff[3]-=20
diff[1]+=25, diff[5]-=25
```

Diff:

```text
[10,45,-10,-20,0,-25]
```

Prefix first 5 cells:

```text
10
55
45
25
25
```

Answer:

```text
[10,55,45,25,25]
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> corpFlightBookings(vector<vector<int>>& bookings, int n) {
    vector<int> diff(n + 1, 0);

    for (auto &b : bookings) {
        int first = b[0] - 1; // convert to 0-index
        int last  = b[1] - 1;
        int seats = b[2];

        diff[first] += seats;
        diff[last + 1] -= seats;
    }

    vector<int> ans(n);

    int running = 0;

    for (int i = 0; i < n; i++) {
        running += diff[i];
        ans[i] = running;
    }

    return ans;
}

int main() {
    vector<vector<int>> bookings = {
        {1,2,10},
        {2,3,20},
        {2,5,25}
    };

    vector<int> ans = corpFlightBookings(bookings, 5);

    for (int x : ans) {
        cout << x << " ";
    }

    return 0;
}
```

---

## 16. Problem Form 4 — Car Pooling / Capacity Timeline

### Problem

Trips are:

```text
passengers, start, end
```

Passengers get in at `start` and get out at `end`.

Check whether capacity is exceeded at any point.

---

### Pattern

```text
Difference Array / Timeline Delta
```

Important:

```text
start += passengers
end -= passengers
```

Because passengers leave at `end`.

---

### Step-by-Step Working

Trips:

```text
[2,1,5]
[3,3,7]
```

Capacity:

```text
4
```

Timeline diff:

```text
diff[1] += 2
diff[5] -= 2
diff[3] += 3
diff[7] -= 3
```

During interval:

```text
at position 1 -> passengers = 2
at position 3 -> passengers = 5
```

Capacity exceeded.

Answer:

```text
false
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool carPooling(vector<vector<int>>& trips, int capacity) {
    const int MAX_POS = 1001;

    vector<int> diff(MAX_POS + 1, 0);

    for (auto &trip : trips) {
        int passengers = trip[0];
        int start = trip[1];
        int end = trip[2];

        // passengers enter at start
        diff[start] += passengers;

        // passengers leave at end
        diff[end] -= passengers;
    }

    int current = 0;

    for (int i = 0; i <= MAX_POS; i++) {
        current += diff[i];

        if (current > capacity) {
            return false;
        }
    }

    return true;
}

int main() {
    vector<vector<int>> trips = {
        {2,1,5},
        {3,3,7}
    };

    int capacity = 4;

    cout << boolalpha << carPooling(trips, capacity) << endl;

    return 0;
}
```

---

## 17. Problem Form 5 — Event Timeline Sweep

### Problem

Given events:

```text
start time
end time
load
```

Find load at every time point.

---

### Pattern

```text
Difference Array / Sweep Line
```

For event:

```text
[start, end, load]
```

If end is inclusive:

```text
diff[start] += load
diff[end+1] -= load
```

If end is exclusive:

```text
diff[start] += load
diff[end] -= load
```

---

### Step-by-Step Working

Events:

```text
[1,3,+5]
[2,4,+2]
```

Inclusive intervals.

Apply:

```text
diff[1]+=5
diff[4]-=5

diff[2]+=2
diff[5]-=2
```

Prefix load:

```text
time 1 -> 5
time 2 -> 7
time 3 -> 7
time 4 -> 2
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int maxTime = 6;

    vector<tuple<int,int,int>> events = {
        {1,3,5},
        {2,4,2}
    };

    vector<int> diff(maxTime + 2, 0);

    for (auto [start, end, load] : events) {
        // Inclusive interval [start, end]
        diff[start] += load;
        diff[end + 1] -= load;
    }

    int current = 0;

    for (int t = 0; t <= maxTime; t++) {
        current += diff[t];

        cout << "time=" << t << " load=" << current << "\n";
    }

    return 0;
}
```

---

## 18. Real World Model 1 — Traffic Burst Timeline

### Scenario

Imagine an ad-tech or CDN platform.

Marketing campaigns temporarily increase traffic:

```text
Campaign A:
+100 requests/sec from t=10 to t=20

Campaign B:
+50 requests/sec from t=15 to t=25
```

We want to know:

```text
Total additional traffic at every second.
```

---

### Naive Approach

For every campaign:

```text
loop from start to end
add traffic
```

If:

```text
millions of campaigns
large timeline
```

this becomes expensive.

---

### Difference Array Mental Model

Instead of updating every second:

```text
Mark when traffic starts
Mark when traffic stops
```

For Campaign A:

```text
diff[10] += 100
diff[21] -= 100
```

For Campaign B:

```text
diff[15] += 50
diff[26] -= 50
```

---

### Step-by-Step Timeline Build

Initial diff:

```text
all zeros
```

After A:

```text
time 10 -> +100
time 21 -> -100
```

After B:

```text
time 15 -> +50
time 26 -> -50
```

Now take prefix sum.

---

### Prefix Accumulation

At time:

```text
0..9 -> 0
10..14 -> 100
15..20 -> 150
21..25 -> 50
26+ -> 0
```

Final traffic contribution:

```text
0   0   0 ...
100 100 ...
150 150 ...
50  50 ...
0
```

---

### Backend System Mapping

This pattern appears in:

```text
traffic forecasting
CDN load estimation
API request modeling
campaign analytics
capacity planning
autoscaling prediction
```

---

### Why This Is Powerful

Instead of:

```text
O(number_of_intervals * interval_length)
```

we get:

```text
O(number_of_intervals + timeline_size)
```

This is huge for large-scale systems.

---

### Production Insight

Many monitoring systems internally process:

```text
state changes
```

instead of continuously storing full values.

Difference-array thinking is:

```text
store deltas
then reconstruct state
```

This idea appears everywhere in distributed systems.

---

## 19. Real World Model 2 — Kafka-Style Event Delta Accumulation

### Scenario

Suppose a Kafka-like event system stores events:

```text
+10 active users
-3 active users
+7 active users
```

Instead of storing:

```text
complete system state every second
```

the system stores:

```text
changes (deltas)
```

---

### Core Mental Model

Difference array is exactly:

```text
delta storage
```

Prefix accumulation is:

```text
state reconstruction
```

---

### Event Stream Example

Kafka topic events:

```text
t=1 : +5
t=3 : +2
t=5 : -4
```

Diff representation:

```text
diff[1] += 5
diff[3] += 2
diff[5] -= 4
```

Prefix reconstruction:

```text
time 0 -> 0
time 1 -> 5
time 2 -> 5
time 3 -> 7
time 4 -> 7
time 5 -> 3
```

---

### Why Event Systems Prefer Deltas

Storing full state repeatedly is expensive.

Instead systems often store:

```text
changes only
```

Advantages:

```text
1. smaller storage
2. append-only log
3. replay capability
4. event sourcing
5. timeline reconstruction
```

---

### Mapping To Distributed Systems

This mental model maps to:

```text
Kafka event streams
CDC logs
event sourcing systems
time-series deltas
metrics accumulation
distributed counters
```

---

### Event Sourcing Parallel

In event sourcing:

```text
current state
=
all past events replayed
```

Difference array works similarly:

```text
final values
=
prefix accumulation of deltas
```

---

### Backend Engineering Insight

This is why understanding difference arrays is valuable beyond CP:

```text
It teaches delta-based thinking.
```

Many scalable systems avoid rewriting entire state.

Instead they store:

```text
incremental changes
```

and rebuild when needed.

---

## 20. Real World Model 3 — Batch Price Update

### Scenario

E-commerce platform:

```text
Increase prices of products in category range [L,R] by X
```

Example:

```text
Products 1000 to 5000
increase price by 3%
```

If there are many batch updates:

```text
Black Friday
regional adjustments
tax updates
currency correction
discount campaigns
```

naive updates become expensive.

---

### Naive Approach

For every update:

```text
loop over all products in range
```

Complexity becomes huge.

---

### Difference Array Solution

Store only:

```text
where increment starts
where increment stops
```

For update:

```text
[L,R,+X]
```

do:

```text
diff[L] += X
diff[R+1] -= X
```

Later:

```text
prefix rebuild => final adjustment per product
```

---

### Step-by-Step Example

Suppose:

```text
5 products
all price adjustments initially 0
```

Updates:

```text
[1,3,+2]
[2,4,+3]
```

Diff after updates:

```text
[0,2,3,0,-2,-3]
```

Prefix rebuild:

```text
0
2
5
5
3
```

Meaning:

```text
product0 -> +0
product1 -> +2
product2 -> +5
product3 -> +5
product4 -> +3
```

---

### Backend System Mapping

This idea maps to:

```text
bulk product updates
inventory adjustment
mass pricing changes
regional pricing rules
promotion systems
batch billing corrections
```

---

### Distributed Systems Insight

Large systems often avoid:

```text
touching every row immediately
```

Instead they:

```text
store compact delta operations
```

and later:

```text
materialize final state
```

Difference arrays teach this exact optimization mindset.

---

## 21. Real World Model 4 — Calendar Booking Load

### Scenario

Meeting rooms receive bookings:

```text
Meeting A:
10:00 -> 11:00

Meeting B:
10:30 -> 12:00

Meeting C:
11:00 -> 11:30
```

Need:

```text
How many active meetings at each time?
```

or:

```text
Did room capacity exceed limit?
```

---

### Difference Array Mental Model

For each meeting:

```text
+1 when meeting starts
-1 when meeting ends
```

---

### Step-by-Step Example

Meetings:

```text
A: [10,11]
B: [10,12]
C: [11,11]
```

Diff timeline:

```text
diff[10] += 1
diff[12] -= 1

diff[10] += 1
diff[13] -= 1

diff[11] += 1
diff[12] -= 1
```

Now prefix accumulate.

---

### Timeline Reconstruction

At time:

```text
10 -> 2 meetings
11 -> 3 meetings
12 -> 0 meetings
```

Peak load:

```text
3
```

---

### Why This Matters

This is a classic:

```text
sweep line / interval overlap
```

problem.

Difference arrays are one of the cleanest ways to solve it.

---

### Real Backend Mapping

This maps to:

```text
calendar scheduling
room booking systems
resource allocation
CPU load timeline
active user sessions
video stream concurrency
ride-sharing trip overlap
```

---

### Scaling Insight

Instead of storing:

```text
active state at every moment
```

systems store:

```text
state transitions
```

Then reconstruct load using cumulative accumulation.

This is exactly:

```text
difference array + prefix sum
```

---

### Connection To Sweep Line

Sweep line problems are essentially:

```text
difference arrays over coordinates/time
```

Very important for:

```text
interval problems
timeline analytics
concurrency estimation
resource scheduling
```

---
## 22. Decision Tree

```text
Array/range problem?
|
+-- Many range sum queries?
|   |
|   +-- Use Prefix Sum
|
+-- Many range updates?
|   |
|   +-- Need final array only?
|   |   |
|   |   +-- Use Difference Array
|   |
|   +-- Need queries during updates?
|       |
|       +-- Use Fenwick / Segment Tree
|
+-- Timeline start/end events?
|   |
|   +-- Use Difference Array / Sweep Line
```

---

## 23. Common Mistakes

### Mistake 1 — Forgetting R+1

Wrong:

```cpp
diff[r] -= x;
```

Correct for inclusive `[l,r]`:

```cpp
diff[r + 1] -= x;
```

---

### Mistake 2 — Array Size Too Small

Use:

```cpp
vector<long long> diff(n + 1, 0);
```

or sometimes:

```cpp
n + 2
```

to safely access `r+1`.

---

### Mistake 3 — Confusing Inclusive vs Exclusive End

For inclusive interval:

```text
[l,r]
diff[l] += x
diff[r+1] -= x
```

For exclusive interval:

```text
[l,r)
diff[l] += x
diff[r] -= x
```

---

### Mistake 4 — Forgetting Final Prefix Rebuild

Difference array is not the final answer.

You must take prefix sum.

---

### Mistake 5 — Using Difference Array For Online Queries

Difference array is best when:

```text
all updates first
then final rebuild
```

If queries happen between updates, use:

```text
Fenwick Tree / Segment Tree
```

---

## 24. Complexity

For `q` updates and array size `n`:

```text
Apply all updates: O(q)
Rebuild final array: O(n)
Total: O(n+q)
```

Space:

```text
O(n)
```

---

## 25. Reusable C++ Templates

### Template 1 — Range Add Final Array

```cpp
vector<long long> applyRangeUpdates(
    int n,
    vector<tuple<int,int,long long>>& updates
) {
    vector<long long> diff(n + 1, 0);

    for (auto [l, r, x] : updates) {
        diff[l] += x;
        diff[r + 1] -= x;
    }

    vector<long long> ans(n);
    long long running = 0;

    for (int i = 0; i < n; i++) {
        running += diff[i];
        ans[i] = running;
    }

    return ans;
}
```

### Template 2 — Existing Array + Range Updates

```cpp
vector<long long> applyUpdatesToExistingArray(
    vector<long long> a,
    vector<tuple<int,int,long long>>& updates
) {
    int n = a.size();

    vector<long long> diff(n + 1, 0);

    // Convert original array into diff representation.
    diff[0] = a[0];

    for (int i = 1; i < n; i++) {
        diff[i] = a[i] - a[i - 1];
    }

    for (auto [l, r, x] : updates) {
        diff[l] += x;
        diff[r + 1] -= x;
    }

    vector<long long> ans(n);
    long long running = 0;

    for (int i = 0; i < n; i++) {
        running += diff[i];
        ans[i] = running;
    }

    return ans;
}
```

### Template 3 — Timeline Events

```cpp
vector<int> buildTimelineLoad(
    int maxTime,
    vector<tuple<int,int,int>>& events
) {
    vector<int> diff(maxTime + 2, 0);

    for (auto [start, end, load] : events) {
        diff[start] += load;
        diff[end + 1] -= load;
    }

    vector<int> loadAt(maxTime + 1);
    int current = 0;

    for (int t = 0; t <= maxTime; t++) {
        current += diff[t];
        loadAt[t] = current;
    }

    return loadAt;
}
```

---

## 26. CP / FAANG Problem Forms

---

### Problem 1 — Range Addition

#### Recognition

```text
many updates [l,r,val]
return final array
```

#### Pattern

```text
difference array
```

#### Steps

```text
1. diff[l] += val
2. diff[r+1] -= val
3. prefix diff to rebuild final array
```

---

### Problem 2 — Corporate Flight Bookings

#### Recognition

```text
book seats over flight range
return seats per flight
```

#### Pattern

```text
difference array with 1-index conversion
```

---

### Problem 3 — Car Pooling

#### Recognition

```text
passengers enter and leave over route positions
check capacity
```

#### Pattern

```text
timeline diff
```

---

### Problem 4 — Calendar Overlap

#### Recognition

```text
events start/end
need active count
```

#### Pattern

```text
sweep line / difference array
```

---

### Problem 5 — Traffic Campaign Load

#### Recognition

```text
range time intervals add load
need final load timeline
```

#### Pattern

```text
difference array over time
```

---

## 27. Practice Checklist

Before using difference array:

```text
1. Are there many range updates?
2. Do I only need final array after all updates?
3. Are intervals inclusive or exclusive?
4. Did I allocate n+1 or n+2?
5. Did I do diff[l] += x?
6. Did I do diff[r+1] -= x for inclusive range?
7. Did I rebuild with prefix sum?
8. Are values large enough for long long?
9. Is input 0-indexed or 1-indexed?
10. If queries are online, should I use Fenwick/Segment Tree instead?
```

---

## 28. Next Step

```text
007_1D_Difference_Array.md
```

That file goes deeper into:

```text
building diff arrays
existing arrays
range updates
final reconstruction
debugging difference arrays
