# AlgoZenith — Monotonic Deque Mastery

> **Core idea:** A sliding window changes at both ends.  
> Remove **expired** indices from the **front** and **dominated** candidates from the **back**.

## Table of Contents

- [1. What Is a Monotonic Deque?](#1-what-is-a-monotonic-deque)
- [2. Maximum in Window — Maang](#2-maximum-in-window--maang)
- [3. Sliding Window Minimum](#3-sliding-window-minimum)
- [4. LC 2762 — Continuous Subarrays](#4-lc-2762--continuous-subarrays)
- [5. LC 862 — Shortest Subarray With Sum at Least K](#5-lc-862--shortest-subarray-with-sum-at-least-k)
- [6. Templates](#6-templates)
- [7. Common Mistakes](#7-common-mistakes)
- [8. Final Pattern Map](#8-final-pattern-map)

---

# 1. What Is a Monotonic Deque?

A deque supports:

```text
push_front / pop_front
push_back  / pop_back
```

A **monotonic deque** stores only candidates that may still become an answer.

For sliding-window maximum:

```text
indices → increasing
values  → decreasing
```

So:

```text
dq.front() = index of current maximum
```

## Front vs Back

```text
FRONT → remove elements that are too old
BACK  → remove elements that are too weak
```

Store **indices**, not only values:

```cpp
deque<int> dq;
```

because an index gives both:

```text
value  = a[index]
expiry = index position
```

## Dominance

Suppose:

```text
j < i
a[j] <= a[i]
```

Then `j` is useless for future maximum queries because `i` is:

```text
at least as large
AND
newer
```

Therefore:

```cpp
while (!dq.empty() && a[dq.back()] <= a[i])
    dq.pop_back();
```

## Why O(n)?

Every index is:

```text
pushed once
popped at most once
```

Hence:

```text
Time = O(n)
```

---

# 2. Maximum in Window — Maang

**Problem:** [Maximum in Window](https://maang.in/problems/Maximum-in-Window-77)

## Problem in Simple Words

Given:

```text
array a
window size k
```

find the maximum in every contiguous window of size `k`.

Example:

```text
a = [1,3,-1,-3,5,3,6,7]
k = 3
```

Answer:

```text
[3,3,5,5,6,7]
```

## Window Algebra

For a window ending at index `i`:

```text
right = i
left  = i-k+1
```

An index `j` is expired when:

```text
j < i-k+1
```

Equivalent:

```text
j <= i-k
```

Therefore:

```cpp
while (!dq.empty() && dq.front() <= i-k)
    dq.pop_front();
```

## Monotonic Rule

For maximum:

```text
remove from back while

a[dq.back()] <= a[i]
```

Then values in the deque are decreasing:

```text
a[dq[0]] > a[dq[1]] > ...
```

So:

```text
maximum = a[dq.front()]
```

## Algorithm

For every `i`:

```text
1. Remove expired indices from FRONT.
2. Remove dominated values from BACK.
3. Push i at BACK.
4. If i >= k-1, front is the answer.
```

## Dry Run

```text
a = [1,3,-1,-3,5]
k = 3
```

| `i` | `a[i]` | Action | Deque `index:value` | Output |
|---:|---:|---|---|---:|
| 0 | 1 | push | `[0:1]` | — |
| 1 | 3 | pop `1`, push | `[1:3]` | — |
| 2 | -1 | push | `[1:3,2:-1]` | 3 |
| 3 | -3 | push | `[1:3,2:-1,3:-3]` | 3 |
| 4 | 5 | expire `1`, pop `-3,-1`, push | `[4:5]` | 5 |

At `i=4`:

```text
window = [2..4]

left = i-k+1
     = 4-3+1
     = 2
```

Index `1` expires.

Then:

```text
-3 <= 5 → pop
-1 <= 5 → pop
```

Only `5` remains.

## C++

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> maximumInWindow(const vector<int>& a, int k) {
    deque<int> dq;
    vector<int> ans;

    for (int i = 0; i < (int)a.size(); i++) {

        // 1. Expired
        while (!dq.empty() && dq.front() <= i - k)
            dq.pop_front();

        // 2. Dominated
        while (!dq.empty() && a[dq.back()] <= a[i])
            dq.pop_back();

        // 3. Insert current
        dq.push_back(i);

        // 4. Window complete
        if (i >= k - 1)
            ans.push_back(a[dq.front()]);
    }

    return ans;
}
```

```text
Time  = O(n)
Space = O(k)
```

---

# 3. Sliding Window Minimum

Same algorithm; reverse the dominance comparison.

For maximum:

```cpp
while (!dq.empty() && a[dq.back()] <= a[i])
    dq.pop_back();
```

For minimum:

```cpp
while (!dq.empty() && a[dq.back()] >= a[i])
    dq.pop_back();
```

Now deque values are increasing:

```text
a[dq[0]] < a[dq[1]] < ...
```

and:

```text
minimum = a[dq.front()]
```

## Dry Run

```text
a = [4,2,5,1]
k = 2
```

Windows:

```text
[4,2] → 2
[2,5] → 2
[5,1] → 1
```

Answer:

```text
[2,2,1]
```

## C++

```cpp
vector<int> minimumInWindow(const vector<int>& a, int k) {
    deque<int> dq;
    vector<int> ans;

    for (int i = 0; i < (int)a.size(); i++) {

        while (!dq.empty() && dq.front() <= i - k)
            dq.pop_front();

        while (!dq.empty() && a[dq.back()] >= a[i])
            dq.pop_back();

        dq.push_back(i);

        if (i >= k - 1)
            ans.push_back(a[dq.front()]);
    }

    return ans;
}
```

---

# 4. LC 2762 — Continuous Subarrays

**Problem:** [LeetCode 2762 — Continuous Subarrays](https://leetcode.com/problems/continuous-subarrays/)

## Problem in Simple Words

Count subarrays where:

```text
max - min <= 2
```

Example:

```text
a = [5,4,2,4]
```

Answer:

```text
8
```

## Algebraic Derivation

The condition:

```text
|a[x] - a[y]| <= 2
```

for every pair inside the window is equivalent to:

```text
maximum(window) - minimum(window) <= 2
```

So we need both extremes efficiently.

Use:

```text
maxDQ → decreasing → front = maximum
minDQ → increasing → front = minimum
```

If:

```text
a[maxDQ.front()] - a[minDQ.front()] > 2
```

move the left boundary until valid again.

For each right endpoint `r`, once `[l..r]` is valid:

```text
valid subarrays ending at r
= r-l+1
```

because all of these are valid:

```text
[l..r]
[l+1..r]
...
[r..r]
```

Therefore:

```text
answer += r-l+1
```

## Dry Run — `[5,4,2,4]`

### `r=0`

```text
window = [5]

max-min = 0
count = 1
```

```text
ans = 1
```

### `r=1`

```text
window = [5,4]

5-4 = 1 <= 2

count = 2
ans = 3
```

### `r=2`

Initially:

```text
[5,4,2]

5-2 = 3 > 2
```

Move `l`:

```text
[4,2]

4-2 = 2
```

Valid endings:

```text
[4,2]
[2]

count = 2
ans = 5
```

### `r=3`

```text
[4,2,4]

4-2 = 2

count = 3
ans = 8
```

## C++

```cpp
class Solution {
public:
    long long continuousSubarrays(vector<int>& a) {
        deque<int> maxDQ, minDQ;

        long long ans = 0;
        int l = 0;

        for (int r = 0; r < (int)a.size(); r++) {

            while (!maxDQ.empty() &&
                   a[maxDQ.back()] <= a[r])
                maxDQ.pop_back();

            maxDQ.push_back(r);

            while (!minDQ.empty() &&
                   a[minDQ.back()] >= a[r])
                minDQ.pop_back();

            minDQ.push_back(r);

            while (a[maxDQ.front()] -
                   a[minDQ.front()] > 2) {

                if (maxDQ.front() == l)
                    maxDQ.pop_front();

                if (minDQ.front() == l)
                    minDQ.pop_front();

                l++;
            }

            ans += r - l + 1;
        }

        return ans;
    }
};
```

```text
Time  = O(n)
Space = O(n) worst case
```

---

# 5. LC 862 — Shortest Subarray With Sum at Least K

**Problem:** [LeetCode 862 — Shortest Subarray with Sum at Least K](https://leetcode.com/problems/shortest-subarray-with-sum-at-least-k/)

## Problem in Simple Words

Find the shortest non-empty subarray whose sum is at least `K`.

Numbers may be negative, so ordinary two pointers do not work.

## Step 1 — Prefix Sum

Define:

```text
P[0] = 0
P[i] = sum of first i elements
```

Subarray `[j ... i-1]` has sum:

```text
P[i] - P[j]
```

We need:

```text
P[i] - P[j] >= K
```

Rearrange:

```text
P[j] <= P[i] - K
```

and we want to minimize:

```text
i-j
```

## Why an Increasing Deque?

Suppose:

```text
j1 < j2
P[j1] >= P[j2]
```

Then `j1` is dominated by `j2`.

Why?

```text
P[j2] is smaller/equal
→ easier to satisfy P[i]-P[j] >= K

j2 is newer
→ i-j2 is shorter
```

Therefore `j1` can never be better.

Maintain:

```text
P[dq[0]] < P[dq[1]] < ...
```

## Two Pop Rules

### Front — We Found a Valid Subarray

If:

```text
P[i] - P[dq.front()] >= K
```

then:

```text
length = i-dq.front()
```

Record it and pop front.

Why continue popping?

A newer front may also satisfy the condition and give an even shorter subarray.

### Back — Dominated Prefix

If:

```text
P[dq.back()] >= P[i]
```

the old prefix is worse:

```text
larger prefix value
AND
older index
```

so pop it.

## Dry Run

```text
a = [2,-1,2]
K = 3
```

Prefix:

```text
P = [0,2,1,3]
```

Process:

```text
i=0, P=0
dq = [0]
```

```text
i=1, P=2
2-0 < 3
dq = [0,1]
```

```text
i=2, P=1

P[1]=2 >= 1
→ pop back 1

dq = [0,2]
```

```text
i=3, P=3

3-P[0]
= 3-0
= 3 >= K

length = 3-0 = 3
```

Answer:

```text
3
```

## C++

```cpp
class Solution {
public:
    int shortestSubarray(vector<int>& nums, int k) {
        int n = nums.size();

        vector<long long> prefix(n + 1, 0);

        for (int i = 0; i < n; i++)
            prefix[i + 1] = prefix[i] + nums[i];

        deque<int> dq;
        int ans = n + 1;

        for (int i = 0; i <= n; i++) {

            // Valid → try shortest
            while (!dq.empty() &&
                   prefix[i] - prefix[dq.front()] >= k) {

                ans = min(ans, i - dq.front());
                dq.pop_front();
            }

            // Dominated prefix
            while (!dq.empty() &&
                   prefix[dq.back()] >= prefix[i]) {

                dq.pop_back();
            }

            dq.push_back(i);
        }

        return ans == n + 1 ? -1 : ans;
    }
};
```

```text
Time  = O(n)
Space = O(n)
```

This is the important advanced form:

```text
Monotonic Deque
+
Prefix Sum
+
Algebraic Transformation
```

---

# 6. Templates

## Sliding Window Maximum

```cpp
deque<int> dq;

for (int i = 0; i < n; i++) {

    while (!dq.empty() && dq.front() <= i-k)
        dq.pop_front();

    while (!dq.empty() && a[dq.back()] <= a[i])
        dq.pop_back();

    dq.push_back(i);

    if (i >= k-1)
        cout << a[dq.front()] << ' ';
}
```

## Sliding Window Minimum

```cpp
deque<int> dq;

for (int i = 0; i < n; i++) {

    while (!dq.empty() && dq.front() <= i-k)
        dq.pop_front();

    while (!dq.empty() && a[dq.back()] >= a[i])
        dq.pop_back();

    dq.push_back(i);

    if (i >= k-1)
        cout << a[dq.front()] << ' ';
}
```

## Dynamic Window With Max + Min

```cpp
deque<int> maxDQ, minDQ;
int l = 0;

for (int r = 0; r < n; r++) {

    while (!maxDQ.empty() &&
           a[maxDQ.back()] <= a[r])
        maxDQ.pop_back();

    maxDQ.push_back(r);

    while (!minDQ.empty() &&
           a[minDQ.back()] >= a[r])
        minDQ.pop_back();

    minDQ.push_back(r);

    while (/* window invalid */) {

        if (maxDQ.front() == l)
            maxDQ.pop_front();

        if (minDQ.front() == l)
            minDQ.pop_front();

        l++;
    }

    // [l..r] is now valid
}
```

---

# 7. Common Mistakes

## 1. Storing Values Instead of Indices

Bad:

```cpp
deque<int> dq; // values only
```

You cannot reliably know when a value expires.

Prefer:

```cpp
deque<int> dq; // indices
```

---

## 2. Wrong Expiration Formula

Current window ending at `i`:

```text
[i-k+1 ... i]
```

Expired:

```text
index < i-k+1
```

Equivalent:

```text
index <= i-k
```

---

## 3. Mixing Front and Back Responsibilities

```text
FRONT → expiry / valid-window movement

BACK → dominance / monotonic order
```

---

## 4. Wrong Comparison

Maximum:

```cpp
a[dq.back()] <= a[i]
```

Minimum:

```cpp
a[dq.back()] >= a[i]
```

---

## 5. Output Before Window Is Complete

First complete size-`k` window ends at:

```text
i = k-1
```

So:

```cpp
if (i >= k-1)
```

---

## 6. Thinking Nested `while` Means O(n²)

It does not.

```text
each index enters once
each index leaves once
```

Therefore:

```text
O(n) amortized
```

---

# 8. Final Pattern Map

| Pattern | Deque Order | Front Gives | Back Pop |
|---|---|---|---|
| Window maximum | decreasing | maximum | `<= current` |
| Window minimum | increasing | minimum | `>= current` |
| Max-min bounded window | two deques | max + min | both rules |
| Prefix optimization | increasing prefix | best oldest candidate | `prefix.back >= currentPrefix` |

## Core Mental Model

```text
NEW ELEMENT ARRIVES
        ↓
remove expired candidates from FRONT
        ↓
remove dominated candidates from BACK
        ↓
push current index
        ↓
FRONT = best surviving candidate
```

For fixed-size windows:

```text
window = [i-k+1, i]

expired
⇔ index < i-k+1
⇔ index <= i-k
```

For counting valid dynamic windows:

```text
valid subarrays ending at r
= r-l+1
```

For prefix-sum deque problems:

```text
subarray sum
= P[r] - P[l]
```

Then algebra tells us what kind of prefix candidate the deque should preserve.

> **One sentence to remember:**  
> **Front = too old / ready to answer. Back = too weak.**
