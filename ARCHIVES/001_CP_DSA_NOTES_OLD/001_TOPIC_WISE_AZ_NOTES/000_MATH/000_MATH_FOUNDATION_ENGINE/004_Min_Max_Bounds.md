# 004_Min_Max_Bounds.md

# Min / Max Bounds Thinking For Competitive Programming

---

# 1. Why Min / Max Bounds Matter

Many CP and FAANG problems become easier when you first ask:

```text
What is the minimum possible answer?
What is the maximum possible answer?
```

This helps in:
- impossible case detection
- greedy proof
- binary search on answer
- constructive problems
- optimization problems
- reducing brute force

Bounds thinking is one of the strongest foundations for:
- adhoc
- constructive
- greedy
- math
- binary search

---

# 2. Core Mental Model

Before solving, think:

```text
Answer must lie between LOW and HIGH.
```

Then ask:

```text
Can I prove lower bound?
Can I prove upper bound?
Can I reach that bound?
```

If yes, often the problem is solved.

---

# 3. What Is A Lower Bound?

Lower bound means:

```text
answer can never be smaller than this.
```

Example:

```text
Need to process n items.
Each operation processes at most 1 item.
```

Minimum operations:

```text
n
```

because one operation cannot process more than one item.

---

# 4. What Is An Upper Bound?

Upper bound means:

```text
answer can never be larger than this.
```

Example:

```text
Need to connect n nodes.
```

At most:

```text
n - 1 edges
```

are needed in a tree if connection is possible.

---

# 5. Why Bounds Are Powerful

If you can show:

```text
lower bound = upper bound
```

then the answer is proven.

Example:

```text
Minimum number of operations is at least k.
I can construct solution in exactly k operations.
Therefore answer = k.
```

This is common in:
- greedy
- constructive
- CP proofs

---

# 6. Simple Example — Maximum Pair Count

Problem:

```text
Given n elements, how many unordered pairs can be formed?
```

Observation:

```text
Choose any 2 elements.
```

Formula:

```text
n * (n - 1) / 2
```

C++:

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countPairs(long long n) {
    return n * (n - 1) / 2;
}
```

---

# 7. Simple Example — Minimum Positive Sum

Problem:

```text
Find minimum possible sum of n positive integers.
```

Smallest positive integer:

```text
1
```

So minimum sum:

```text
n
```

C++:

```cpp
#include <bits/stdc++.h>
using namespace std;

long long minimumPositiveSum(long long n) {
    return n;
}
```

---

# 8. Bounds In Impossible Cases

Bounds often detect impossibility.

Example:

```text
Can sum S be made using n positive integers?
```

Minimum possible sum:

```text
n
```

So if:

```text
S < n
```

answer is impossible.

C++:

```cpp
#include <bits/stdc++.h>
using namespace std;

bool possiblePositiveSum(long long n, long long S) {
    return S >= n;
}
```

---

# 9. Bounds In Constructive Problems

Constructive problem often asks:

```text
Print any valid answer.
```

Use bounds to check:

```text
Is construction possible?
```

Example:

```text
Need n positive integers with sum S.
```

Minimum sum:

```text
n
```

If `S >= n`, construction:

```text
1 1 1 ... remaining
```

C++:

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<long long> constructPositiveSum(long long n, long long S) {

    if (S < n) return {};

    vector<long long> ans(n, 1);

    ans[n - 1] += S - n;

    return ans;
}
```

---

# 10. Bounds In Greedy

Greedy often works when local choice reaches a bound.

Example:

```text
Minimum number of coins if coin system is canonical.
```

Greedy tries to use largest coin first.

But warning:

```text
Greedy is correct only when proof exists.
```

Bounds help prove:
```text
No solution can use fewer than X choices.
Greedy uses X choices.
```

---

# 11. Bounds In Binary Search On Answer

If answer lies between:

```text
low and high
```

and feasibility is monotonic:

```text
false false false true true true
```

then binary search on answer.

Examples:
- minimum maximum load
- minimum time
- maximum minimum distance
- capacity to ship packages

---

# 12. Binary Search Bound Template

```cpp
#include <bits/stdc++.h>
using namespace std;

long long binarySearchAnswer(long long low, long long high) {

    while (low < high) {

        long long mid = low + (high - low) / 2;

        if (/* can(mid) */ false) {
            high = mid;
        } else {
            low = mid + 1;
        }
    }

    return low;
}
```

---

# 13. Common Lower Bound Patterns

| Problem Type | Lower Bound |
|---|---|
| Process n items, 1 per operation | n |
| Need sum using positive integers | n |
| Need max element at least | max(a) |
| Split array into k parts | max element lower bound |
| Total work W, speed x | ceil(W / x) |
| Need connect n nodes | n - 1 edges |
| Need cover length L with segment len k | ceil(L / k) |

---

# 14. Common Upper Bound Patterns

| Problem Type | Upper Bound |
|---|---|
| Process n items one by one | n |
| Shortest path in unweighted graph | n - 1 edges |
| Sum of positive array | total sum |
| Maximum pairs | n(n-1)/2 |
| Binary search answer | large enough feasible value |
| Construct n positive integers sum S | S |

---

# 15. CP / FAANG Problem Forms

---

# Form 1 — Minimum Operations

## Problem

Given `n` tasks. One operation can complete at most one task.
Find minimum operations.

---

## Step-by-Step Working

```text
Each operation completes <= 1 task.
Need complete n tasks.
Therefore operations >= n.
Can do one task per operation.
Therefore operations = n.
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long minOperations(long long n) {
    return n;
}
```

---

# Form 2 — Minimum Capacity

## Problem

Given packages, ship all within D days.
Find minimum ship capacity.

---

## Bounds

Lower bound:

```text
max(package weight)
```

because capacity must carry largest package.

Upper bound:

```text
sum(all weights)
```

because one day can carry all.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canShip(vector<int>& w, int days, long long cap) {

    int usedDays = 1;
    long long current = 0;

    for (int x : w) {

        if (current + x > cap) {
            usedDays++;
            current = 0;
        }

        current += x;
    }

    return usedDays <= days;
}

long long minShipCapacity(vector<int>& w, int days) {

    long long low = 0;
    long long high = 0;

    for (int x : w) {
        low = max(low, (long long)x);
        high += x;
    }

    while (low < high) {

        long long mid = low + (high - low) / 2;

        if (canShip(w, days, mid)) {
            high = mid;
        } else {
            low = mid + 1;
        }
    }

    return low;
}
```

---

# Form 3 — Maximum Minimum Distance

## Problem

Place k cows in stalls so minimum distance is maximized.

---

## Bounds

Lower bound:

```text
0 or 1
```

Upper bound:

```text
max_position - min_position
```

---

## Pattern

```text
Binary search answer
+
greedy feasibility
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canPlace(vector<int>& stalls, int k, int dist) {

    int placed = 1;
    int last = stalls[0];

    for (int i = 1; i < stalls.size(); i++) {

        if (stalls[i] - last >= dist) {
            placed++;
            last = stalls[i];
        }
    }

    return placed >= k;
}

int maxMinDistance(vector<int>& stalls, int k) {

    sort(stalls.begin(), stalls.end());

    int low = 0;
    int high = stalls.back() - stalls.front();

    while (low < high) {

        int mid = low + (high - low + 1) / 2;

        if (canPlace(stalls, k, mid)) {
            low = mid;
        } else {
            high = mid - 1;
        }
    }

    return low;
}
```

---

# Form 4 — Construct With Sum Bounds

## Problem

Construct n positive integers whose sum is S.

---

## Step-by-Step Working

```text
Smallest positive integer = 1.
Minimum sum for n numbers = n.
If S < n → impossible.
Otherwise:
put 1 in every position.
add remaining S - n to last element.
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<long long> constructArray(long long n, long long S) {

    if (S < n) return {};

    vector<long long> ans(n, 1);

    ans.back() += S - n;

    return ans;
}
```

---

# Form 5 — Pair Count Bound

## Problem

Maximum number of handshakes among n people.

---

## Step-by-Step Working

```text
Every handshake selects 2 different people.
Number of ways to choose 2 from n:
n(n-1)/2
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long maxHandshakes(long long n) {
    return n * (n - 1) / 2;
}
```

---

# 16. Real World Applications

| Real System | Bounds Usage |
|---|---|
| Load balancer | max server load |
| Cloud autoscaling | minimum servers needed |
| Delivery system | minimum trips |
| Video streaming | max bitrate under bandwidth |
| Database sharding | max rows per shard |
| Rate limiter | maximum requests per window |
| Scheduling | minimum time/resources |

---

# 17. Real World Example — Server Capacity

Problem:

```text
Total requests = R
Each server handles C requests/sec
How many servers needed?
```

Formula:

```text
ceil(R / C)
```

C++:

```cpp
#include <bits/stdc++.h>
using namespace std;

long long serversNeeded(long long R, long long C) {
    return (R + C - 1) / C;
}
```

---

# 18. Decision Tree

```text
Need minimum / maximum?
→ Think bounds

Need prove answer?
→ lower bound + construction

Need impossible condition?
→ compare required value with min/max possible

Need optimize monotonic answer?
→ binary search on answer

Need allocate resources?
→ capacity bounds
```

---

# 19. Common Traps

```text
1. Weak lower bound
2. Weak upper bound
3. Overflow in formulas
4. Wrong ceil division
5. Assuming bound is achievable without proof
6. Forgetting impossible cases
7. Wrong binary search direction
8. Not sorting before greedy feasibility
9. Off-by-one in answer search
```

---

# 20. Final Checklist

Before solving:

```text
1. What is the minimum possible answer?
2. What is the maximum possible answer?
3. Is answer always within [low, high]?
4. Can I prove lower bound?
5. Can I construct upper bound?
6. Are lower and upper equal?
7. Is feasibility monotonic?
8. Can binary search on answer work?
9. Are there impossible cases?
10. Can bounds reduce brute force?
```

---

# 21. Final Mental Shortcut

```text
Min / Max Bounds
=
Limit of what is possible
+
Proof of what is achievable
```
