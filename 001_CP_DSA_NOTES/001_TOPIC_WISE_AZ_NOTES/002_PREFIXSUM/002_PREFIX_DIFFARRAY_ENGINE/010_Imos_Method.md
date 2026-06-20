# 010_Imos_Method.md — MiniPrefixSumDifferenceEngine

# Imos Method

> Imos Method is a range-update / sweep-line technique built on top of Difference Array.
>
> It is commonly used for:
>
> ```text
> interval coverage
> range add
> timeline load
> booking overlap
> event accumulation
> grid painting
> ```
>
> Mental model:
>
> ```text
> mark starts and stops
> then prefix accumulate
> ```

---

## Clickable Index

1. [What Is Imos Method?](#1-what-is-imos-method)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Core Mental Model](#3-core-mental-model)
4. [Imos vs Difference Array](#4-imos-vs-difference-array)
5. [1D Imos Formula](#5-1d-imos-formula)
6. [Inclusive vs Exclusive Interval](#6-inclusive-vs-exclusive-interval)
7. [Step-by-Step Dry Run — Timeline Coverage](#7-step-by-step-dry-run--timeline-coverage)
8. [Step-by-Step Dry Run — Range Add](#8-step-by-step-dry-run--range-add)
9. [Finding Maximum Overlap](#9-finding-maximum-overlap)
10. [2D Imos Preview](#10-2d-imos-preview)
11. [Problem Form 1 — Count Covered Points](#11-problem-form-1--count-covered-points)
12. [Problem Form 2 — Maximum Overlapping Intervals](#12-problem-form-2--maximum-overlapping-intervals)
13. [Problem Form 3 — Car Pooling / Capacity](#13-problem-form-3--car-pooling--capacity)
14. [Problem Form 4 — Booking Rooms / Active Meetings](#14-problem-form-4--booking-rooms--active-meetings)
15. [Problem Form 5 — Range Add Final Array](#15-problem-form-5--range-add-final-array)
16. [Real World Model 1 — API Load Timeline](#16-real-world-model-1--api-load-timeline)
17. [Real World Model 2 — Calendar Resource Load](#17-real-world-model-2--calendar-resource-load)
18. [Real World Model 3 — CDN Regional Burst Window](#18-real-world-model-3--cdn-regional-burst-window)
19. [Real World Model 4 — Metrics Rollup From Events](#19-real-world-model-4--metrics-rollup-from-events)
20. [Decision Tree](#20-decision-tree)
21. [Common Mistakes](#21-common-mistakes)
22. [Complexity](#22-complexity)
23. [Reusable C++ Templates](#23-reusable-c-templates)
24. [CP / FAANG Problem Forms](#24-cp--faang-problem-forms)
25. [Practice Checklist](#25-practice-checklist)
26. [Next Step](#26-next-step)

---

## 1. What Is Imos Method?

Imos Method is a technique for applying many interval updates efficiently.

Instead of updating every point inside an interval:

```text
[L, R]
```

we mark:

```text
start at L
stop after R
```

Then one prefix sum reconstructs the actual values.

---

## 2. Why This Topic Matters

Many problems look like this:

```text
Given many intervals, count coverage at every point.
```

or:

```text
Given many events with start/end, find max active events.
```

Naive approach:

```text
for every interval:
    update every point inside interval
```

This can be too slow.

Imos Method:

```text
each interval -> O(1)
final prefix -> O(N)
```

---

## 3. Core Mental Model

Imos is:

```text
event delta + prefix reconstruction
```

For an interval:

```text
start contributes +value
end contributes -value
```

Prefix accumulation gives:

```text
active value at each point
```

---

## 4. Imos vs Difference Array

They are almost the same idea.

| Name | Common Usage |
|---|---|
| Difference Array | range updates on arrays |
| Imos Method | interval coverage / sweep-line / timeline |
| Sweep Line | sorted event version of Imos |

Think:

```text
Difference Array = data structure view
Imos Method      = algorithmic interval/event view
```

---

## 5. 1D Imos Formula

For inclusive interval:

```text
[L, R]
```

Add value:

```text
X
```

Do:

```cpp
imos[L] += X;
imos[R + 1] -= X;
```

Then prefix:

```cpp
running += imos[i];
value[i] = running;
```

---

## 6. Inclusive vs Exclusive Interval

### Inclusive Interval

```text
[L, R]
```

Use:

```cpp
imos[L] += x;
imos[R + 1] -= x;
```

---

### Exclusive Interval

```text
[L, R)
```

Use:

```cpp
imos[L] += x;
imos[R] -= x;
```

This is very common for timelines:

```text
start inclusive
end exclusive
```

Example:

```text
meeting from 10 to 11
```

Usually active on:

```text
[10, 11)
```

So:

```cpp
imos[10] += 1;
imos[11] -= 1;
```

---

## 7. Step-by-Step Dry Run — Timeline Coverage

Intervals:

```text
[1, 4]
[2, 5]
[3, 6]
```

Inclusive intervals.

Initial:

```text
imos = [0,0,0,0,0,0,0,0]
```

Apply `[1,4]`:

```text
imos[1] += 1
imos[5] -= 1
```

Apply `[2,5]`:

```text
imos[2] += 1
imos[6] -= 1
```

Apply `[3,6]`:

```text
imos[3] += 1
imos[7] -= 1
```

Final delta:

```text
index: 0 1 2 3 4 5 6 7
imos : 0 1 1 1 0 -1 -1 -1
```

Prefix accumulation:

```text
i=0 -> 0
i=1 -> 1
i=2 -> 2
i=3 -> 3
i=4 -> 3
i=5 -> 2
i=6 -> 1
```

Coverage:

```text
[0,1,2,3,3,2,1]
```

Maximum overlap:

```text
3
```

---

## 8. Step-by-Step Dry Run — Range Add

Array size:

```text
n = 6
```

Updates:

```text
+5 on [1,3]
+2 on [2,5]
```

Delta:

```text
imos[1]+=5
imos[4]-=5

imos[2]+=2
imos[6]-=2
```

Delta array:

```text
[0,5,2,0,-5,0,-2]
```

Prefix final:

```text
index 0 -> 0
index 1 -> 5
index 2 -> 7
index 3 -> 7
index 4 -> 2
index 5 -> 2
```

Final:

```text
[0,5,7,7,2,2]
```

---

## 9. Finding Maximum Overlap

Once we build the active count timeline:

```text
take max over prefix values
```

Example:

```text
coverage = [0,1,2,3,3,2,1]
```

Max:

```text
3
```

This solves:

```text
minimum meeting rooms
max active sessions
max overlapping intervals
peak server load
```

---

## 10. 2D Imos Preview

2D Imos is used for rectangle updates.

For rectangle:

```text
(r1,c1) -> (r2,c2)
```

Add:

```text
X
```

Four-corner update:

```text
diff[r1][c1] += X
diff[r1][c2+1] -= X
diff[r2+1][c1] -= X
diff[r2+1][c2+1] += X
```

Then 2D prefix rebuild.

This is covered deeply in:

```text
009_2D_Difference_Array.md
```

---

## 11. Problem Form 1 — Count Covered Points

### Problem

Given intervals on a line, find how many points are covered at least once.

---

### Pattern

```text
Imos Method / Difference Array
```

---

### Problem Simulation

Intervals:

```text
[1,3]
[2,5]
[7,8]
```

Coverage:

```text
point 1 -> covered
point 2 -> covered by 2 intervals
point 3 -> covered by 2 intervals
point 4 -> covered
point 5 -> covered
point 7 -> covered
point 8 -> covered
```

Covered points:

```text
7
```

---

### Step-by-Step

Apply intervals:

```text
[1,3] -> imos[1]+=1, imos[4]-=1
[2,5] -> imos[2]+=1, imos[6]-=1
[7,8] -> imos[7]+=1, imos[9]-=1
```

Prefix and count positions where:

```text
active > 0
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int maxPoint = 10;

    vector<pair<int,int>> intervals = {
        {1,3},
        {2,5},
        {7,8}
    };

    vector<int> imos(maxPoint + 2, 0);

    for (auto [l, r] : intervals) {
        // inclusive [l,r]
        imos[l] += 1;
        imos[r + 1] -= 1;
    }

    int active = 0;
    int coveredCount = 0;

    for (int x = 0; x <= maxPoint; x++) {
        active += imos[x];

        if (active > 0) {
            coveredCount++;
        }
    }

    cout << coveredCount << "\n";

    return 0;
}
```

---

### Complexity

```text
O(number_of_intervals + max_coordinate)
```

---

## 12. Problem Form 2 — Maximum Overlapping Intervals

### Problem

Given intervals, find maximum number of intervals active at the same point.

---

### Pattern

```text
Imos / Sweep Line
```

---

### Problem Simulation

Intervals:

```text
[1,4]
[2,5]
[3,6]
```

Coverage:

```text
point 1 -> 1
point 2 -> 2
point 3 -> 3
point 4 -> 3
point 5 -> 2
point 6 -> 1
```

Maximum overlap:

```text
3
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int maxPoint = 10;

    vector<pair<int,int>> intervals = {
        {1,4},
        {2,5},
        {3,6}
    };

    vector<int> imos(maxPoint + 2, 0);

    for (auto [l, r] : intervals) {
        imos[l] += 1;
        imos[r + 1] -= 1;
    }

    int active = 0;
    int best = 0;

    for (int x = 0; x <= maxPoint; x++) {
        active += imos[x];

        best = max(best, active);
    }

    cout << best << "\n";

    return 0;
}
```

---

### Real Interview Mapping

This maps to:

```text
minimum meeting rooms
max concurrent users
max active trips
peak load
```

---

## 13. Problem Form 3 — Car Pooling / Capacity

### Problem

Trips:

```text
passengers, start, end
```

Passengers enter at `start`.

Passengers leave at `end`.

Return whether capacity is ever exceeded.

---

### Pattern

```text
Exclusive-end Imos
```

Since passengers leave at `end`, interval is:

```text
[start, end)
```

So:

```text
imos[start] += passengers
imos[end] -= passengers
```

---

### Problem Simulation

Trips:

```text
[2,1,5]
[3,3,7]
```

Capacity:

```text
4
```

Timeline:

```text
t=1 -> +2 active
t=3 -> +3 active, total 5
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
    int maxPos = 1000;

    vector<int> imos(maxPos + 2, 0);

    for (auto &trip : trips) {
        int passengers = trip[0];
        int start = trip[1];
        int end = trip[2];

        // Trip is active on [start, end)
        imos[start] += passengers;
        imos[end] -= passengers;
    }

    int active = 0;

    for (int pos = 0; pos <= maxPos; pos++) {
        active += imos[pos];

        if (active > capacity) {
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

    cout << boolalpha << carPooling(trips, capacity) << "\n";

    return 0;
}
```

---

## 14. Problem Form 4 — Booking Rooms / Active Meetings

### Problem

Given meetings:

```text
[start, end)
```

Find the maximum number of rooms needed.

---

### Pattern

```text
Imos Method / Sweep Line
```

---

### Problem Simulation

Meetings:

```text
[10, 20)
[15, 25)
[20, 30)
```

Timeline:

```text
10 -> active 1
15 -> active 2
20 -> first meeting ends and third starts
```

If end is exclusive:

```text
at time 20:
-1 +1
active remains 2
```

Maximum rooms:

```text
2
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int minMeetingRooms(vector<pair<int,int>>& meetings) {
    int maxTime = 100;

    vector<int> imos(maxTime + 2, 0);

    for (auto [start, end] : meetings) {
        // active on [start, end)
        imos[start] += 1;
        imos[end] -= 1;
    }

    int active = 0;
    int rooms = 0;

    for (int t = 0; t <= maxTime; t++) {
        active += imos[t];
        rooms = max(rooms, active);
    }

    return rooms;
}

int main() {
    vector<pair<int,int>> meetings = {
        {10,20},
        {15,25},
        {20,30}
    };

    cout << minMeetingRooms(meetings) << "\n";

    return 0;
}
```

---

### Note

If time coordinates are large, use:

```text
coordinate compression
```

or event sorting.

---

## 15. Problem Form 5 — Range Add Final Array

### Problem

Apply many range additions and return final array.

---

### Pattern

```text
Difference Array / Imos
```

---

### Problem Simulation

Array size:

```text
n = 6
```

Updates:

```text
+5 on [1,3]
+2 on [2,5]
```

Final:

```text
[0,5,7,7,2,2]
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<long long> rangeAdd(
    int n,
    vector<tuple<int,int,long long>>& updates
) {
    vector<long long> imos(n + 1, 0);

    for (auto [l, r, val] : updates) {
        imos[l] += val;
        imos[r + 1] -= val;
    }

    vector<long long> ans(n);

    long long running = 0;

    for (int i = 0; i < n; i++) {
        running += imos[i];
        ans[i] = running;
    }

    return ans;
}

int main() {
    int n = 6;

    vector<tuple<int,int,long long>> updates = {
        {1,3,5},
        {2,5,2}
    };

    vector<long long> ans = rangeAdd(n, updates);

    for (long long x : ans) {
        cout << x << " ";
    }

    return 0;
}
```

---

## 16. Real World Model 1 — API Load Timeline

### Scenario

A backend API receives temporary load spikes.

Events:

```text
Campaign A:
+100 RPS from t=10 to t=20

Campaign B:
+50 RPS from t=15 to t=25
```

Need:

```text
load at every second
peak load
```

---

### Simulation

Use exclusive end:

```text
A active on [10,21)
B active on [15,26)
```

Delta:

```text
imos[10] += 100
imos[21] -= 100
imos[15] += 50
imos[26] -= 50
```

Final load:

```text
0..9    -> 0
10..14  -> 100
15..20  -> 150
21..25  -> 50
26+     -> 0
```

Peak:

```text
150 RPS
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int maxTime = 30;

    vector<tuple<int,int,int>> campaigns = {
        {10,20,100},
        {15,25,50}
    };

    vector<int> imos(maxTime + 2, 0);

    for (auto [start, end, rps] : campaigns) {
        // inclusive [start,end]
        imos[start] += rps;
        imos[end + 1] -= rps;
    }

    int load = 0;
    int peak = 0;

    for (int t = 0; t <= maxTime; t++) {
        load += imos[t];
        peak = max(peak, load);

        cout << "time=" << t << " load=" << load << "\n";
    }

    cout << "peak=" << peak << "\n";

    return 0;
}
```

---

### Backend Mapping

Used for:

```text
capacity planning
autoscaling prediction
CDN load simulation
campaign traffic modeling
incident replay
```

---

## 17. Real World Model 2 — Calendar Resource Load

### Scenario

Meeting room bookings:

```text
Meeting A: [10,20)
Meeting B: [15,25)
Meeting C: [20,30)
```

Need:

```text
minimum rooms required
```

---

### Simulation

Delta:

```text
imos[10]+=1
imos[20]-=1

imos[15]+=1
imos[25]-=1

imos[20]+=1
imos[30]-=1
```

At time 20:

```text
-1 and +1 happen together
```

Active count remains correct.

Peak:

```text
2
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<pair<int,int>> meetings = {
        {10,20},
        {15,25},
        {20,30}
    };

    int maxTime = 30;

    vector<int> imos(maxTime + 2, 0);

    for (auto [start, end] : meetings) {
        imos[start] += 1;
        imos[end] -= 1;
    }

    int active = 0;
    int rooms = 0;

    for (int t = 0; t <= maxTime; t++) {
        active += imos[t];
        rooms = max(rooms, active);
    }

    cout << rooms << "\n";

    return 0;
}
```

---

## 18. Real World Model 3 — CDN Regional Burst Window

### Scenario

A CDN sees regional bursts over time.

Each burst:

```text
region-load active from start to end
```

For one region, this becomes a 1D timeline Imos problem.

---

### Simulation

Burst windows:

```text
+500 Mbps [2,6]
+300 Mbps [4,8]
+200 Mbps [7,9]
```

Final load:

```text
t=2 -> 500
t=4 -> 800
t=7 -> 500
t=9 -> 200
```

Peak:

```text
800 Mbps
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int maxTime = 10;

    vector<tuple<int,int,int>> bursts = {
        {2,6,500},
        {4,8,300},
        {7,9,200}
    };

    vector<int> imos(maxTime + 2, 0);

    for (auto [l, r, mbps] : bursts) {
        imos[l] += mbps;
        imos[r + 1] -= mbps;
    }

    int load = 0;
    int peak = 0;

    for (int t = 0; t <= maxTime; t++) {
        load += imos[t];
        peak = max(peak, load);
    }

    cout << "peak CDN load = " << peak << "\n";

    return 0;
}
```

---

## 19. Real World Model 4 — Metrics Rollup From Events

### Scenario

A metrics system stores event deltas:

```text
service instance starts
service instance stops
request burst starts
request burst ends
```

Need to reconstruct active load timeline.

---

### Simulation

Events:

```text
+10 at t=1
+20 at t=3
-5 at t=5
-25 at t=7
```

Prefix accumulation:

```text
t=1 -> 10
t=3 -> 30
t=5 -> 25
t=7 -> 0
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int maxTime = 8;

    vector<pair<int,int>> deltas = {
        {1, 10},
        {3, 20},
        {5, -5},
        {7, -25}
    };

    vector<int> imos(maxTime + 1, 0);

    for (auto [time, delta] : deltas) {
        imos[time] += delta;
    }

    int current = 0;

    for (int t = 0; t <= maxTime; t++) {
        current += imos[t];

        cout << "time=" << t << " metric=" << current << "\n";
    }

    return 0;
}
```

---

## 20. Decision Tree

```text
Interval / timeline problem?
|
+-- Need count active intervals?
|   |
|   +-- Imos / Sweep Line
|
+-- Need max overlap?
|   |
|   +-- Imos + max prefix
|
+-- Need range add final array?
|   |
|   +-- Difference Array / Imos
|
+-- Coordinate range huge?
|   |
|   +-- Coordinate Compression / Ordered Map Sweep
|
+-- Need online queries?
    |
    +-- Fenwick / Segment Tree
```

---

## 21. Common Mistakes

### Mistake 1 — Wrong End Handling

Inclusive:

```cpp
imos[r + 1] -= x;
```

Exclusive:

```cpp
imos[r] -= x;
```

---

### Mistake 2 — Forgetting Coordinate Compression

If coordinates are huge:

```text
1e9
```

do not allocate array of size 1e9.

Use:

```text
map / coordinate compression
```

---

### Mistake 3 — Forgetting Prefix Accumulation

Imos array stores only deltas.

Need prefix to get actual values.

---

### Mistake 4 — Wrong Array Size

Allocate:

```text
maxCoordinate + 2
```

to safely write:

```text
r+1
```

---

## 22. Complexity

For `N` coordinate range and `Q` intervals:

```text
Apply intervals: O(Q)
Prefix scan: O(N)
Total: O(N+Q)
```

If using coordinate compression:

```text
O(Q log Q)
```

---

## 23. Reusable C++ Templates

### Template 1 — Inclusive Interval Imos

```cpp
vector<long long> buildInclusiveImos(
    int n,
    vector<tuple<int,int,long long>>& intervals
) {
    vector<long long> imos(n + 1, 0);

    for (auto [l, r, val] : intervals) {
        imos[l] += val;
        imos[r + 1] -= val;
    }

    vector<long long> ans(n);
    long long running = 0;

    for (int i = 0; i < n; i++) {
        running += imos[i];
        ans[i] = running;
    }

    return ans;
}
```

---

### Template 2 — Exclusive Interval Imos

```cpp
vector<long long> buildExclusiveImos(
    int n,
    vector<tuple<int,int,long long>>& intervals
) {
    vector<long long> imos(n + 1, 0);

    for (auto [l, r, val] : intervals) {
        imos[l] += val;
        imos[r] -= val;
    }

    vector<long long> ans(n);
    long long running = 0;

    for (int i = 0; i < n; i++) {
        running += imos[i];
        ans[i] = running;
    }

    return ans;
}
```

---

### Template 3 — Maximum Overlap

```cpp
int maxOverlap(int maxPoint, vector<pair<int,int>>& intervals) {
    vector<int> imos(maxPoint + 2, 0);

    for (auto [l, r] : intervals) {
        imos[l] += 1;
        imos[r + 1] -= 1;
    }

    int active = 0;
    int best = 0;

    for (int x = 0; x <= maxPoint; x++) {
        active += imos[x];
        best = max(best, active);
    }

    return best;
}
```

---

## 24. CP / FAANG Problem Forms

### Problem 1 — Maximum Overlap

Recognition:

```text
many intervals
find peak active count
```

Pattern:

```text
Imos / sweep line
```

---

### Problem 2 — Car Pooling

Recognition:

```text
passengers enter/leave
capacity constraint
```

Pattern:

```text
exclusive-end Imos
```

---

### Problem 3 — Meeting Rooms

Recognition:

```text
start/end meetings
minimum rooms
```

Pattern:

```text
event delta / Imos
```

---

### Problem 4 — Covered Points

Recognition:

```text
count points covered by at least one interval
```

Pattern:

```text
Imos coverage count
```

---

### Problem 5 — Range Add Final Array

Recognition:

```text
many range updates
final values only
```

Pattern:

```text
Difference Array / Imos
```

---

## 25. Practice Checklist

Before using Imos:

```text
1. Is this interval/timeline/range update?
2. Inclusive or exclusive end?
3. Need final array/timeline?
4. Need max overlap?
5. Can coordinate size fit in array?
6. Need coordinate compression?
7. Did I allocate max+2?
8. Did I prefix accumulate?
9. Online or offline?
10. Would sweep-line map be better?
```

---

## 26. Next Step

```text
011_Subarray_Sum_Equal_K.md
```

Next moves from range updates to prefix-hash subarray patterns:

```text
prefix sum + hashmap
target transformation
count subarrays
FAANG high-frequency pattern
```
