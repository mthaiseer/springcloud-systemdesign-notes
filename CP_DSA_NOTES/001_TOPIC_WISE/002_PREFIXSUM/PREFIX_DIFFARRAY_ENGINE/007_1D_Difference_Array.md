# 007_1D_Difference_Array.md — MiniPrefixSumDifferenceEngine

# 1D Difference Array

> 1D Difference Array is the practical implementation of range-update optimization.
>
> It converts:
>
> ```text
> O(length of range)
> ```
>
> updates into:
>
> ```text
> O(1)
> ```
>
> boundary operations.

---

## Clickable Index

1. [What Is 1D Difference Array?](#1-what-is-1d-difference-array)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Core Mental Model](#3-core-mental-model)
4. [Difference Array Formula](#4-difference-array-formula)
5. [Inclusive vs Exclusive Range](#5-inclusive-vs-exclusive-range)
6. [Why Boundary Marking Works](#6-why-boundary-marking-works)
7. [Difference Array Build Flow](#7-difference-array-build-flow)
8. [Step-by-Step Dry Run — Single Update](#8-step-by-step-dry-run--single-update)
9. [Step-by-Step Dry Run — Multiple Updates](#9-step-by-step-dry-run--multiple-updates)
10. [Step-by-Step Dry Run — Final Prefix Rebuild](#10-step-by-step-dry-run--final-prefix-rebuild)
11. [Building Difference Array From Existing Array](#11-building-difference-array-from-existing-array)
12. [Converting Difference Back To Original Array](#12-converting-difference-back-to-original-array)
13. [Problem Form 1 — Range Addition](#13-problem-form-1--range-addition)
14. [Problem Form 2 — Increment Range Queries](#14-problem-form-2--increment-range-queries)
15. [Problem Form 3 — Corporate Flight Bookings](#15-problem-form-3--corporate-flight-bookings)
16. [Problem Form 4 — Car Pooling](#16-problem-form-4--car-pooling)
17. [Problem Form 5 — Maximum Overlapping Intervals](#17-problem-form-5--maximum-overlapping-intervals)
18. [Real World Model 1 — API Traffic Timeline](#18-real-world-model-1--api-traffic-timeline)
19. [Real World Model 2 — Kafka Event Delta Stream](#19-real-world-model-2--kafka-event-delta-stream)
20. [Real World Model 3 — Batch Inventory Updates](#20-real-world-model-3--batch-inventory-updates)
21. [Real World Model 4 — Concurrent Session Tracking](#21-real-world-model-4--concurrent-session-tracking)
22. [Decision Tree](#22-decision-tree)
23. [Common Mistakes](#23-common-mistakes)
24. [Complexity](#24-complexity)
25. [Reusable C++ Templates](#25-reusable-c-templates)
26. [CP / FAANG Problem Forms](#26-cp--faang-problem-forms)
27. [Practice Checklist](#27-practice-checklist)
28. [Next Step](#28-next-step)

---

## 1. What Is 1D Difference Array?

A 1D Difference Array stores:

```text
where change starts
where change stops
```

Instead of updating every index inside:

```text
[L,R]
```

we only mark boundaries.

For update:

```text
add X on [L,R]
```

we do:

```text
diff[L] += X
diff[R+1] -= X
```

Then prefix sum reconstructs the final array.

---

## 2. Why This Topic Matters

Suppose:

```text
n = 200000
q = 200000
```

Brute force:

```cpp
for each query:
    for i in [L,R]:
        a[i] += X
```

Worst case:

```text
O(n*q)
```

Too slow.

Difference array:

```text
each update -> O(1)
final rebuild -> O(n)
```

Total:

```text
O(n+q)
```

---

## 3. Core Mental Model

Difference array stores:

```text
delta changes
```

Think:

```text
At L:
start adding X

After R:
stop adding X
```

So:

```text
diff[L] += X
diff[R+1] -= X
```

Prefix accumulation spreads the effect automatically.

---

## 4. Difference Array Formula

For inclusive range:

```text
add X on [L,R]
```

Formula:

```cpp
diff[L] += X;

if (R + 1 < n)
    diff[R + 1] -= X;
```

If diff size is:

```text
n+1
```

we can safely do:

```cpp
diff[R+1] -= X;
```

without bounds check.

---

## 5. Inclusive vs Exclusive Range

### Inclusive Interval

```text
[L,R]
```

Use:

```cpp
diff[L] += X;
diff[R+1] -= X;
```

---

### Exclusive Interval

```text
[L,R)
```

Use:

```cpp
diff[L] += X;
diff[R] -= X;
```

Very important distinction.

---

## 6. Why Boundary Marking Works

Suppose:

```text
add 5 on [2,4]
```

We mark:

```text
diff[2] += 5
diff[5] -= 5
```

Diff:

```text
index: 0 1 2 3 4 5 6
diff : 0 0 5 0 0 -5 0
```

Prefix rebuild:

```text
0
0
5
5
5
0
```

Effect automatically continues until cancellation point.

---

## 7. Difference Array Build Flow

```text
updates
   |
   v
mark boundaries in diff
   |
   v
take prefix sum
   |
   v
final updated array
```

Difference array itself is NOT the final array.

It stores only transitions.

---

## 8. Step-by-Step Dry Run — Single Update

Initial:

```text
n = 6
a = [0,0,0,0,0,0]
```

Update:

```text
add 3 on [1,4]
```

Initial diff:

```text
[0,0,0,0,0,0,0]
```

Apply update:

```text
diff[1] += 3
diff[5] -= 3
```

Diff:

```text
[0,3,0,0,0,-3,0]
```

Prefix rebuild:

```text
i=0 -> 0
i=1 -> 3
i=2 -> 3
i=3 -> 3
i=4 -> 3
i=5 -> 0
```

Final:

```text
[0,3,3,3,3,0]
```

---

## 9. Step-by-Step Dry Run — Multiple Updates

Queries:

```text
1) add 2 on [1,3]
2) add 4 on [2,5]
3) add 1 on [0,2]
```

Initial diff:

```text
[0,0,0,0,0,0,0]
```

---

### Query 1

```text
diff[1]+=2
diff[4]-=2
```

```text
[0,2,0,0,-2,0,0]
```

---

### Query 2

```text
diff[2]+=4
diff[6]-=4
```

```text
[0,2,4,0,-2,0,-4]
```

---

### Query 3

```text
diff[0]+=1
diff[3]-=1
```

```text
[1,2,4,-1,-2,0,-4]
```

---

## 10. Step-by-Step Dry Run — Final Prefix Rebuild

Diff:

```text
[1,2,4,-1,-2,0,-4]
```

Prefix rebuild:

### i = 0

```text
running = 1
a[0] = 1
```

### i = 1

```text
running = 3
a[1] = 3
```

### i = 2

```text
running = 7
a[2] = 7
```

### i = 3

```text
running = 6
a[3] = 6
```

### i = 4

```text
running = 4
a[4] = 4
```

### i = 5

```text
running = 4
a[5] = 4
```

Final:

```text
[1,3,7,6,4,4]
```

---

## 11. Building Difference Array From Existing Array

Original array:

```text
a = [5,7,10,10]
```

Difference array:

```text
diff[0] = a[0]

diff[i] = a[i] - a[i-1]
```

Compute:

```text
diff[0] = 5
diff[1] = 2
diff[2] = 3
diff[3] = 0
```

Final diff:

```text
[5,2,3,0]
```

---

## 12. Converting Difference Back To Original Array

Take prefix sum.

Diff:

```text
[5,2,3,0]
```

Prefix:

```text
5
7
10
10
```

Recovered original array.

---

## 13. Problem Form 1 — Range Addition

### Problem

Apply many updates:

```text
[L,R,X]
```

Return final array.

---

### Pattern

```text
1D Difference Array
```

---

### Step-by-Step Working

```text
n = 5
updates:
[1,3,+2]
[2,4,+3]
```

Initial diff:

```text
[0,0,0,0,0,0]
```

Apply first update:

```text
diff[1]+=2
diff[4]-=2
```

```text
[0,2,0,0,-2,0]
```

Apply second update:

```text
diff[2]+=3
diff[5]-=3
```

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
        {1,3,2},
        {2,4,3}
    };

    // Difference array.
    vector<long long> diff(n + 1, 0);

    // Apply updates in O(1).
    for (auto [l, r, val] : updates) {

        // Start effect at l.
        diff[l] += val;

        // Stop effect after r.
        diff[r + 1] -= val;
    }

    // Build final array using prefix sum.
    vector<long long> ans(n);

    long long running = 0;

    for (int i = 0; i < n; i++) {
        running += diff[i];
        ans[i] = running;
    }

    for (long long x : ans) {
        cout << x << " ";
    }

    return 0;
}
```

---

## 14. Problem Form 2 — Increment Range Queries

### Problem

Each query:

```text
increment all elements in [L,R] by 1
```

Return final frequencies.

---

### Pattern

```text
Difference Array with delta = 1
```

---

### Step-by-Step Working

Queries:

```text
[0,2]
[1,3]
[2,4]
```

Diff updates:

```text
diff[0]+=1
diff[3]-=1

diff[1]+=1
diff[4]-=1

diff[2]+=1
diff[5]-=1
```

Diff:

```text
[1,1,1,-1,-1,-1]
```

Prefix:

```text
1
2
3
2
1
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

## 15. Problem Form 3 — Corporate Flight Bookings

### Problem

Bookings:

```text
firstFlight lastFlight seats
```

Add seats to every flight in that range.

---

### Pattern

```text
Difference Array
```

---

### Step-by-Step Working

Bookings:

```text
[1,2,10]
[2,3,20]
[2,5,25]
```

Convert to 0-index:

```text
[0,1,+10]
[1,2,+20]
[1,4,+25]
```

Apply diff:

```text
diff[0]+=10
diff[2]-=10

diff[1]+=20
diff[3]-=20

diff[1]+=25
diff[5]-=25
```

Diff:

```text
[10,45,-10,-20,0,-25]
```

Prefix:

```text
10
55
45
25
25
```

Final:

```text
[10,55,45,25,25]
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> corpFlightBookings(
    vector<vector<int>>& bookings,
    int n
) {

    vector<int> diff(n + 1, 0);

    for (auto &b : bookings) {

        int first = b[0] - 1;
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
```

---

## 16. Problem Form 4 — Car Pooling

### Problem

Trips:

```text
passengers, start, end
```

Passengers enter at start and leave at end.

Need to check:

```text
capacity exceeded?
```

---

### Pattern

```text
Timeline Difference Array
```

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

Apply:

```text
diff[1]+=2
diff[5]-=2

diff[3]+=3
diff[7]-=3
```

Prefix load:

```text
position1 -> 2
position3 -> 5
```

Exceeded.

Answer:

```text
false
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool carPooling(
    vector<vector<int>>& trips,
    int capacity
) {

    const int MAX_POS = 1001;

    vector<int> diff(MAX_POS + 1, 0);

    for (auto &trip : trips) {

        int passengers = trip[0];
        int start = trip[1];
        int end = trip[2];

        // passengers enter
        diff[start] += passengers;

        // passengers leave
        diff[end] -= passengers;
    }

    int current = 0;

    for (int i = 0; i <= MAX_POS; i++) {

        current += diff[i];

        if (current > capacity)
            return false;
    }

    return true;
}
```

---

## 17. Problem Form 5 — Maximum Overlapping Intervals

### Problem

Given intervals:

```text
[L,R]
```

Find maximum overlap count.

---

### Pattern

```text
Difference Array / Sweep Line
```

---

### Step-by-Step Working

Intervals:

```text
[1,4]
[2,5]
[3,6]
```

Apply:

```text
diff[1]+=1
diff[5]-=1

diff[2]+=1
diff[6]-=1

diff[3]+=1
diff[7]-=1
```

Prefix:

```text
time1 -> 1
time2 -> 2
time3 -> 3
time4 -> 3
time5 -> 2
```

Maximum:

```text
3
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    vector<pair<int,int>> intervals = {
        {1,4},
        {2,5},
        {3,6}
    };

    const int MAXT = 10;

    vector<int> diff(MAXT + 2, 0);

    for (auto [l, r] : intervals) {

        diff[l] += 1;
        diff[r + 1] -= 1;
    }

    int running = 0;
    int best = 0;

    for (int t = 0; t <= MAXT; t++) {

        running += diff[t];

        best = max(best, running);
    }

    cout << best << endl;

    return 0;
}
```

---

## 18. Real World Model 1 — API Traffic Timeline

### Scenario

Marketing campaigns temporarily increase traffic:

```text
Campaign A:
+100 req/sec from t=10 to t=20

Campaign B:
+50 req/sec from t=15 to t=25
```

Need:

```text
total traffic boost at every second
```

---

### Difference Array Modeling

Instead of updating every second:

```text
diff[10]+=100
diff[21]-=100

diff[15]+=50
diff[26]-=50
```

Prefix accumulation gives:

```text
0..9   -> 0
10..14 -> 100
15..20 -> 150
21..25 -> 50
26+    -> 0
```

---

### Backend Mapping

Used in:

```text
traffic forecasting
autoscaling prediction
capacity planning
CDN analytics
load simulation
```

---

## 19. Real World Model 2 — Kafka Event Delta Stream

### Scenario

Kafka-like systems often store:

```text
changes
```

instead of full state.

Example:

```text
+5 active users
-2 active users
+10 active users
```

---

### Mental Mapping

Difference array:

```text
store deltas
```

Prefix accumulation:

```text
rebuild state
```

---

### Why This Matters

Distributed systems prefer:

```text
append-only changes
```

instead of rewriting full snapshots repeatedly.

This maps to:

```text
event sourcing
CDC logs
stream processing
metrics accumulation
```

---

## 20. Real World Model 3 — Batch Inventory Updates

### Scenario

E-commerce platform:

```text
Increase inventory for product IDs [L,R]
```

Instead of updating every product immediately:

```text
store compact delta operations
```

Later:

```text
materialize final inventory values
```

---

### Difference Array Mapping

Update:

```text
[L,R,+X]
```

becomes:

```text
diff[L]+=X
diff[R+1]-=X
```

Prefix rebuild applies all inventory updates efficiently.

---

### Backend Use Cases

```text
inventory sync
regional stock update
warehouse adjustments
batch pricing correction
```

---

## 21. Real World Model 4 — Concurrent Session Tracking

### Scenario

Users login/logout over time.

Need:

```text
active sessions at every second
```

---

### Session Events

```text
User A:
login=10
logout=20

User B:
login=15
logout=25
```

Difference representation:

```text
diff[10]+=1
diff[21]-=1

diff[15]+=1
diff[26]-=1
```

Prefix:

```text
10..14 -> 1 session
15..20 -> 2 sessions
21..25 -> 1 session
```

---

### System Mapping

Used in:

```text
concurrency tracking
active websocket count
video stream viewers
room booking overlap
resource scheduling
```

---

## 22. Decision Tree

```text
Range problem?
|
+-- Many range sum queries?
|   |
|   +-- Prefix Sum
|
+-- Many range updates?
|   |
|   +-- Need only final array?
|       |
|       +-- Difference Array
|
+-- Updates + online queries?
|   |
|   +-- Fenwick Tree / Segment Tree
|
+-- Timeline interval overlap?
    |
    +-- Difference Array / Sweep Line
```

---

## 23. Common Mistakes

### Mistake 1 — Forgetting R+1

Wrong:

```cpp
diff[r] -= x;
```

Correct:

```cpp
diff[r + 1] -= x;
```

for inclusive interval.

---

### Mistake 2 — Array Size Too Small

Use:

```cpp
vector<long long> diff(n + 1);
```

or:

```text
n + 2
```

to safely access `r+1`.

---

### Mistake 3 — Forgetting Prefix Rebuild

Difference array is not final answer.

Need prefix accumulation.

---

### Mistake 4 — Mixing Inclusive and Exclusive Range

Inclusive:

```text
[l,r]
```

Exclusive:

```text
[l,r)
```

Very important distinction.

---

### Mistake 5 — Using Difference Array For Dynamic Queries

Difference array is best for:

```text
offline batch updates
```

If queries happen during updates:

```text
Fenwick Tree / Segment Tree
```

---

## 24. Complexity

For:

```text
q updates
array size n
```

Complexity:

```text
Apply updates -> O(q)
Prefix rebuild -> O(n)
Total -> O(n+q)
```

Space:

```text
O(n)
```

---

## 25. Reusable C++ Templates

### Template 1 — Basic Difference Array

```cpp
vector<long long> applyUpdates(
    int n,
    vector<tuple<int,int,long long>>& updates
) {

    vector<long long> diff(n + 1, 0);

    for (auto [l, r, val] : updates) {

        diff[l] += val;
        diff[r + 1] -= val;
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

---

### Template 2 — Existing Array + Updates

```cpp
vector<long long> applyUpdatesToExistingArray(
    vector<long long> a,
    vector<tuple<int,int,long long>>& updates
) {

    int n = a.size();

    vector<long long> diff(n + 1, 0);

    diff[0] = a[0];

    for (int i = 1; i < n; i++) {
        diff[i] = a[i] - a[i - 1];
    }

    for (auto [l, r, val] : updates) {

        diff[l] += val;
        diff[r + 1] -= val;
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

---

## 26. CP / FAANG Problem Forms

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
1. diff[l]+=val
2. diff[r+1]-=val
3. prefix rebuild
```

---

### Problem 2 — Corporate Flight Bookings

#### Recognition

```text
seat bookings over flight ranges
```

#### Pattern

```text
difference array
```

---

### Problem 3 — Car Pooling

#### Recognition

```text
people enter and leave over intervals
```

#### Pattern

```text
timeline difference array
```

---

### Problem 4 — Maximum Interval Overlap

#### Recognition

```text
many intervals
find peak overlap
```

#### Pattern

```text
difference array / sweep line
```

---

### Problem 5 — Traffic Load Timeline

#### Recognition

```text
time intervals add load
```

#### Pattern

```text
delta accumulation
```

---

## 27. Practice Checklist

Before using difference array:

```text
1. Are there many range updates?
2. Do I only need final array?
3. Inclusive or exclusive interval?
4. Did I allocate n+1?
5. Did I do diff[l]+=x?
6. Did I do diff[r+1]-=x?
7. Did I rebuild with prefix sum?
8. Is long long needed?
9. Static batch or dynamic queries?
10. Would Fenwick Tree be better?
```

---

## 28. Next Step

```text
008_2D_Difference_Array.md
```

That file extends difference arrays into grids:

```text
rectangle updates
2D range add
2D prefix rebuild
matrix painting problems
