# 008_Range_Update_Queries.md — MiniPrefixSumDifferenceEngine

# Range Update Queries

> Range Update Queries are one of the most important applications of Difference Arrays.
>
> Instead of updating:
>
> ```text
> every index inside [L,R]
> ```
>
> we update only:
>
> ```text
> boundaries
> ```
>
> and later rebuild the final array using prefix sum.

---

# Clickable Index

1. What Are Range Update Queries?
2. Why This Topic Matters
3. Brute Force Range Updates
4. Difference Array Optimization
5. Core Formula
6. Why Boundary Updates Work
7. Step-by-Step Dry Run — Single Query
8. Step-by-Step Dry Run — Multiple Queries
9. Rebuilding Final Array
10. Problem Form 1 — Range Addition
11. Problem Form 2 — Increment Operations
12. Problem Form 3 — Range Decrement
13. Problem Form 4 — Frequency Accumulation
14. Problem Form 5 — Timeline Event Processing
15. Real World Model 1 — Traffic Burst Processing
16. Real World Model 2 — Salary Batch Updates
17. Real World Model 3 — Kafka Delta Aggregation
18. Real World Model 4 — Active Session Timeline
19. Decision Tree
20. Common Mistakes
21. Complexity
22. Reusable C++ Templates
23. CP / FAANG Problem Forms
24. Practice Checklist
25. Next Step

---

# 1. What Are Range Update Queries?

Suppose:

```text
Array:
[0,0,0,0,0]
```

Query:

```text
add +5 on range [1,3]
```

Brute force:

```text
a[1]+=5
a[2]+=5
a[3]+=5
```

Difference array:

```text
diff[1]+=5
diff[4]-=5
```

Then prefix rebuild automatically spreads the update.

---

# 2. Why This Topic Matters

Suppose:

```text
n = 200000
q = 200000
```

Brute force:

```text
O(n*q)
```

Too slow.

Difference array:

```text
O(q+n)
```

Huge optimization.

---

# 3. Brute Force Range Updates

Naive code:

```cpp
for each query:
    for i from L to R:
        a[i] += val;
```

If ranges are large:

```text
too expensive
```

---

# 4. Difference Array Optimization

Instead of touching every element:

```text
mark where effect starts
mark where effect stops
```

Formula:

```text
diff[L] += val
diff[R+1] -= val
```

Prefix sum rebuild propagates effect.

---

# 5. Core Formula

Inclusive interval:

```text
[L,R]
```

Apply:

```cpp
diff[L] += val;
diff[R + 1] -= val;
```

Then:

```cpp
running += diff[i];
final[i] = running;
```

---

# 6. Why Boundary Updates Work

Example:

```text
add +3 on [2,4]
```

Diff:

```text
diff[2]+=3
diff[5]-=3
```

Prefix rebuild:

```text
0 0 3 3 3 0
```

Effect automatically continues until cancellation point.

---

# 7. Step-by-Step Dry Run — Single Query

Initial:

```text
[0,0,0,0,0,0]
```

Query:

```text
add +4 on [1,4]
```

Diff:

```text
[0,4,0,0,0,-4,0]
```

Prefix rebuild:

```text
0
4
4
4
4
0
```

Final:

```text
[0,4,4,4,4,0]
```

---

# 8. Step-by-Step Dry Run — Multiple Queries

Queries:

```text
1) add +2 on [1,3]
2) add +5 on [2,5]
3) add +1 on [0,2]
```

Initial diff:

```text
[0,0,0,0,0,0,0]
```

After query 1:

```text
[0,2,0,0,-2,0,0]
```

After query 2:

```text
[0,2,5,0,-2,0,-5]
```

After query 3:

```text
[1,2,5,-1,-2,0,-5]
```

---

# 9. Rebuilding Final Array

Prefix accumulation:

```text
i=0 -> 1
i=1 -> 3
i=2 -> 8
i=3 -> 7
i=4 -> 5
i=5 -> 5
```

Final array:

```text
[1,3,8,7,5,5]
```

---

# 10. Problem Form 1 — Range Addition

## Problem

Apply many queries:

```text
[L,R,val]
```

Return final array.

---

## Pattern

```text
Difference Array
```

---

## Problem Simulation

```text
n = 5

queries:
[1,3,+2]
[2,4,+3]
```

Expected final:

```text
0 2 5 5 3
```

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    int n = 5;

    vector<tuple<int,int,int>> queries = {
        {1,3,2},
        {2,4,3}
    };

    vector<long long> diff(n + 1, 0);

    // Apply all range updates in O(1).
    for (auto [l, r, val] : queries) {

        diff[l] += val;

        diff[r + 1] -= val;
    }

    vector<long long> finalArray(n);

    long long running = 0;

    // Prefix rebuild.
    for (int i = 0; i < n; i++) {

        running += diff[i];

        finalArray[i] = running;
    }

    for (long long x : finalArray)
        cout << x << " ";

    return 0;
}
```

---

# 11. Problem Form 2 — Increment Operations

## Problem

Each query increments all values in range by 1.

---

## Problem Simulation

Queries:

```text
[0,2]
[1,4]
[2,3]
```

Final frequencies:

```text
1 2 3 2 1
```

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    int n = 5;

    vector<pair<int,int>> queries = {
        {0,2},
        {1,4},
        {2,3}
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

# 12. Problem Form 3 — Range Decrement

## Problem

Each query decreases all values inside a range.

---

## Problem Simulation

Initial:

```text
10 10 10 10 10
```

Queries:

```text
subtract 3 from [1,3]
subtract 2 from [0,2]
```

Final:

```text
8 5 5 7 10
```

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    vector<int> base = {10,10,10,10,10};

    int n = base.size();

    vector<int> diff(n + 1, 0);

    vector<tuple<int,int,int>> queries = {
        {1,3,-3},
        {0,2,-2}
    };

    for (auto [l, r, val] : queries) {

        diff[l] += val;

        diff[r + 1] -= val;
    }

    int running = 0;

    for (int i = 0; i < n; i++) {

        running += diff[i];

        cout << base[i] + running << " ";
    }

    return 0;
}
```

---

# 13. Problem Form 4 — Frequency Accumulation

## Problem

Many ranges represent active participation.

Need frequency of coverage for each index.

---

## Problem Simulation

Ranges:

```text
[1,3]
[2,5]
[0,2]
```

Coverage count:

```text
1 2 3 2 1 1
```

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    int n = 6;

    vector<pair<int,int>> ranges = {
        {1,3},
        {2,5},
        {0,2}
    };

    vector<int> diff(n + 1, 0);

    for (auto [l, r] : ranges) {

        diff[l]++;

        diff[r + 1]--;
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

# 14. Problem Form 5 — Timeline Event Processing

## Problem

Events affect continuous intervals of time.

Need active load at every moment.

---

## Problem Simulation

Events:

```text
+5 load from t=1 to t=4
+3 load from t=3 to t=6
```

Final timeline:

```text
0 5 5 8 8 3 3
```

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    int maxTime = 7;

    vector<vector<int>> events = {
        {1,4,5},
        {3,6,3}
    };

    vector<int> diff(maxTime + 2, 0);

    for (auto &e : events) {

        int start = e[0];
        int end   = e[1];
        int load  = e[2];

        diff[start] += load;

        diff[end + 1] -= load;
    }

    int running = 0;

    for (int t = 0; t <= maxTime; t++) {

        running += diff[t];

        cout << running << " ";
    }

    return 0;
}
```

---

# 15. Real World Model 1 — Traffic Burst Processing

## Scenario

Traffic spikes affect ranges of servers.

Example:

```text
+100 req/sec on servers [1000,5000]
```

Instead of updating every server:

```text
store boundary changes
```

---

## Problem Simulation

Events:

```text
+100 on [1,4]
+50 on [3,6]
```

Server loads:

```text
0 100 100 150 150 50 50
```

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    int servers = 7;

    vector<vector<int>> bursts = {
        {1,4,100},
        {3,6,50}
    };

    vector<int> diff(servers + 1, 0);

    for (auto &b : bursts) {

        diff[b[0]] += b[2];

        diff[b[1] + 1] -= b[2];
    }

    int load = 0;

    for (int i = 0; i < servers; i++) {

        load += diff[i];

        cout << load << " ";
    }

    return 0;
}
```

---

# 16. Real World Model 2 — Salary Batch Updates

## Scenario

Company gives salary increments to employee ID ranges.

---

## Problem Simulation

```text
+500 to employees [100,200]
+300 to employees [150,250]
```

Employees in overlap receive:

```text
800
```

---

## Backend Mapping

Used in:

```text
HR systems
bulk payroll correction
regional compensation updates
```

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    int employees = 10;

    vector<vector<int>> raises = {
        {2,6,500},
        {5,8,300}
    };

    vector<int> diff(employees + 1, 0);

    for (auto &r : raises) {

        diff[r[0]] += r[2];

        diff[r[1] + 1] -= r[2];
    }

    int running = 0;

    for (int i = 0; i < employees; i++) {

        running += diff[i];

        cout << running << " ";
    }

    return 0;
}
```

---

# 17. Real World Model 3 — Kafka Delta Aggregation

## Scenario

Kafka stores delta events:

```text
+10 users
-3 users
+5 users
```

State reconstructed using prefix accumulation.

---

## Problem Simulation

Events:

```text
t=1 +5
t=3 +2
t=5 -4
```

Active users timeline:

```text
0 5 5 7 7 3
```

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    int T = 6;

    vector<vector<int>> events = {
        {1,5},
        {3,2},
        {5,-4}
    };

    vector<int> diff(T + 1, 0);

    for (auto &e : events) {

        diff[e[0]] += e[1];
    }

    int users = 0;

    for (int t = 0; t < T; t++) {

        users += diff[t];

        cout << users << " ";
    }

    return 0;
}
```

---

# 18. Real World Model 4 — Active Session Timeline

## Scenario

User sessions start/end over intervals.

Need concurrent active session count.

---

## Problem Simulation

Sessions:

```text
UserA -> [1,5]
UserB -> [3,7]
```

Concurrent sessions:

```text
0 1 1 2 2 1 1
```

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    int T = 8;

    vector<pair<int,int>> sessions = {
        {1,5},
        {3,7}
    };

    vector<int> diff(T + 1, 0);

    for (auto [start, end] : sessions) {

        diff[start]++;

        diff[end + 1]--;
    }

    int active = 0;

    for (int t = 0; t < T; t++) {

        active += diff[t];

        cout << active << " ";
    }

    return 0;
}
```

---

# 19. Decision Tree

```text
Range problem?
|
+-- Many range updates?
|   |
|   +-- Need final array only?
|       |
|       +-- Difference Array
|
+-- Need online queries?
|   |
|   +-- Fenwick / Segment Tree
|
+-- Timeline events?
    |
    +-- Difference Array
```

---

# 20. Common Mistakes

## Mistake 1

Wrong:

```cpp
diff[r] -= val;
```

Correct:

```cpp
diff[r + 1] -= val;
```

---

## Mistake 2

Forgetting prefix rebuild.

---

## Mistake 3

Using small array size.

Use:

```text
n+1 or n+2
```

---

## Mistake 4

Using for dynamic queries.

Difference array is best for:

```text
offline updates
```

---

# 21. Complexity

For:

```text
n elements
q updates
```

Complexity:

```text
Apply updates -> O(q)
Rebuild array -> O(n)
```

Total:

```text
O(n+q)
```

---

# 22. Reusable C++ Templates

## Template 1 — Range Add

```cpp
void rangeAdd(
    vector<long long>& diff,
    int l,
    int r,
    long long val
) {

    diff[l] += val;

    diff[r + 1] -= val;
}
```

---

## Template 2 — Rebuild Final Array

```cpp
vector<long long> rebuild(
    vector<long long>& diff,
    int n
) {

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

# 23. CP / FAANG Problem Forms

## Problem 1 — Range Addition

Recognition:

```text
many [L,R,val] updates
```

Pattern:

```text
difference array
```

---

## Problem 2 — Frequency Counting

Recognition:

```text
count interval coverage
```

Pattern:

```text
difference array
```

---

## Problem 3 — Timeline Events

Recognition:

```text
start/end events
```

Pattern:

```text
delta accumulation
```

---

## Problem 4 — Concurrent Sessions

Recognition:

```text
active intervals
```

Pattern:

```text
difference array
```

---

## Problem 5 — Batch Increment Queries

Recognition:

```text
many range increments
```

Pattern:

```text
offline range update
```

---

# 24. Practice Checklist

Before using range-update optimization:

```text
1. Many range updates?
2. Need final array only?
3. Inclusive range?
4. Did I use r+1?
5. Did I rebuild with prefix?
6. Need long long?
7. Static or dynamic queries?
8. Would Fenwick Tree be better?
```

---

# 25. Next Step

```text
009_2D_Difference_Array.md
```

Next extends range updates into matrices:

```text
rectangle updates
2D delta propagation
heatmaps
grid simulations
