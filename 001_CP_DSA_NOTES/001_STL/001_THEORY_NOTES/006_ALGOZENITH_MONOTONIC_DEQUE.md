# AlgoZenith — Monotonic Deque Mastery
## Visual Self-Learning Version

> **Core idea:** A sliding window changes at both ends.  
> Remove **expired / ready** candidates from the **front** and remove **dominated / useless** candidates from the **back**.

# Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. What Is a Monotonic Deque?](#1-what-is-a-monotonic-deque)
- [2. Maximum in Window — Maang](#2-maximum-in-window--maang)
- [3. Sliding Window Minimum](#3-sliding-window-minimum)
- [4. LC 2762 — Continuous Subarrays](#4-lc-2762--continuous-subarrays)
- [5. LC 862 — Shortest Subarray With Sum at Least K](#5-lc-862--shortest-subarray-with-sum-at-least-k)
- [6. Core Templates](#6-core-templates)
- [7. Recognition Checklist](#7-recognition-checklist)
- [8. Common Mistakes](#8-common-mistakes)
- [9. Complexity](#9-complexity)
- [10. Final Pattern Map](#10-final-pattern-map)

---

# 0. Prerequisites

Before monotonic deque, be comfortable with the following ideas.

## 0.1 What Is a Deque?

A deque supports insertion and removal from both ends.

```text
FRONT <----------------------> BACK
```

C++:

```cpp
deque<int> dq;

dq.push_front(x);
dq.push_back(x);

dq.pop_front();
dq.pop_back();

dq.front();
dq.back();
```

---

## 0.2 Why Not Use a Normal Queue?

A normal queue mainly supports:

```text
push at back
pop from front
```

But monotonic deque needs both:

```text
FRONT:
remove expired candidates

BACK:
remove dominated candidates
```

So we need both ends.

---

## 0.3 Sliding Window

A fixed-size window of size `k` ending at index `i` is:

```text
[i-k+1 ... i]
```

Example:

```text
a = [1,3,-1,-3,5]
k = 3

i = 2
window = [0..2]

[1,3,-1]

i = 3
window = [1..3]

[3,-1,-3]
```

---

## 0.4 Expired Index

For a size-`k` window ending at `i`:

```text
left boundary = i-k+1
```

An index `j` is expired when:

```text
j < i-k+1
```

Equivalent:

```text
j <= i-k
```

So:

```cpp
while (!dq.empty() && dq.front() <= i - k) {
    dq.pop_front();
}
```

---

## 0.5 Why Store Indices?

Store:

```cpp
deque<int> dq;
```

where each element is an **index**, not just a value.

Why?

Because index gives us both:

```text
value:
a[index]

expiry information:
is index still inside window?
```

If we stored only values:

```text
we would not know when that occurrence leaves the window
```

---

## 0.6 Dominance

Suppose for maximum queries:

```text
j < i

and

a[j] <= a[i]
```

Then `j` is dominated by `i`.

Why?

```text
i is newer
AND
a[i] is at least as large
```

So `j` can never beat `i` in any future window that contains both.

Therefore:

```text
j is permanently useless
```

Remove it from the back.

---

## 0.7 Monotonic Stack vs Monotonic Deque

Monotonic stack:

```text
usually handles nearest boundary / previous / next
```

Monotonic deque:

```text
usually handles a MOVING WINDOW
```

Key difference:

```text
stack:
one active boundary direction

deque:
both ends matter
```

ASCII:

```text
MONOTONIC STACK
---------------
top
 |
 v
[ candidates ]

MONOTONIC DEQUE
---------------
FRONT                     BACK
  |                         |
  v                         v
[ best ... candidates ... newest ]
```

---

## 0.8 Front vs Back — The Most Important Rule

```text
FRONT
-----
too old
ready to answer
best surviving candidate

BACK
----
too weak
dominated
useless after current arrives
```

Memory sentence:

```text
Front = old / answer
Back  = weak
```

---

# 1. What Is a Monotonic Deque?

A monotonic deque stores only candidates that may still become an answer.

For sliding-window maximum:

```text
indices increase from front to back

values decrease from front to back
```

Example:

```text
indices:
1   4   6

values:
9   7   3
```

So:

```text
front = index 1
value = 9

current maximum = 9
```

---

## 1.1 Why Decreasing Values for Maximum?

Suppose deque values are:

```text
9, 7, 3
```

Maximum is immediately:

```text
front = 9
```

If a new value `8` arrives:

```text
3 <= 8 -> remove 3
7 <= 8 -> remove 7
```

Result:

```text
9, 8
```

Why can `7` and `3` be deleted permanently?

Because `8` is:

```text
newer
AND
larger
```

---

## 1.2 ASCII Dominance Example

```text
old index j            new index i
    |                       |
    v                       v

value = 5               value = 8

5 <= 8
j < i
```

Future window containing both:

```text
[ ... j ... i ... ]

8 is better than 5
AND 8 expires later
```

Therefore:

```text
5 can never be the maximum again
```

Pop it.

---

## 1.3 Why O(n)?

Every index:

```text
enters deque once
leaves deque at most once
```

It may leave through:

```text
front
or
back
```

but never more than once overall.

So total work is:

```text
O(n)
```

---

# 2. Maximum in Window — Maang

**Problem:** [Maximum in Window](https://maang.in/problems/Maximum-in-Window-77)

## 2.1 What Is the Problem Asking?

Given:

```text
array a
window size k
```

find maximum in every contiguous window of length `k`.

Example:

```text
a = [1,3,-1,-3,5,3,6,7]
k = 3
```

Windows:

```text
[1,3,-1]   -> 3
[3,-1,-3]  -> 3
[-1,-3,5]  -> 5
[-3,5,3]   -> 5
[5,3,6]    -> 6
[3,6,7]    -> 7
```

Answer:

```text
[3,3,5,5,6,7]
```

---

## 2.2 Brute Force

For every window:

```text
scan all k elements
take maximum
```

Time:

```text
O(nk)
```

Too slow if both are large.

---

## 2.3 Deque Invariant

For maximum:

```text
deque values stay decreasing
```

Example:

```text
front                    back
  |                        |
  v                        v

[ 9, 7, 4, 1 ]
```

Then:

```text
front = maximum
```

---

## 2.4 Step-by-Step Algorithm

For every index `i`:

```text
Step 1:
Remove expired indices from FRONT.

Step 2:
Remove dominated smaller/equal values from BACK.

Step 3:
Push current index at BACK.

Step 4:
Once first complete window exists,
answer = a[dq.front()].
```

---

## 2.5 Step 1 — Remove Expired

Current window:

```text
[i-k+1 ... i]
```

Expired:

```text
index <= i-k
```

Code:

```cpp
while (!dq.empty() && dq.front() <= i - k) {
    dq.pop_front();
}
```

---

## 2.6 Step 2 — Remove Dominated Values

For maximum:

```cpp
while (!dq.empty() && a[dq.back()] <= a[i]) {
    dq.pop_back();
}
```

Why?

New value is:

```text
newer
AND
at least as large
```

So old candidate is useless.

---

## 2.7 ASCII Dry Run

```text
a = [1,3,-1,-3,5]
k = 3
```

### i = 0, value = 1

No expired index.

No dominated index.

Push 0.

```text
dq:
[0:1]
```

Window not complete.

---

### i = 1, value = 3

Back:

```text
1 <= 3
```

Pop index 0.

Push 1.

```text
dq:
[1:3]
```

Window not complete.

---

### i = 2, value = -1

Nothing expires.

Back:

```text
3 <= -1 ? no
```

Push 2.

```text
dq:
[1:3, 2:-1]
```

First window:

```text
[0..2]
```

Maximum:

```text
a[dq.front()]
=
a[1]
=
3
```

---

### i = 3, value = -3

Current window:

```text
[1..3]
```

Index 1 is still valid.

Back:

```text
-1 <= -3 ? no
```

Push 3.

```text
dq:
[1:3, 2:-1, 3:-3]
```

Maximum:

```text
3
```

---

### i = 4, value = 5

Current window:

```text
[2..4]
```

Front index 1 expires:

```text
1 <= 4-3
1 <= 1
true
```

Pop front.

Now:

```text
[2:-1, 3:-3]
```

Dominance from back:

```text
-3 <= 5 -> pop
-1 <= 5 -> pop
```

Push 4:

```text
[4:5]
```

Maximum:

```text
5
```

---

## 2.8 Front vs Back Picture

At each step:

```text
FRONT
|
v

[ old/best ........ weak/new ]
                         ^
                         |
                        BACK
```

Operations:

```text
FRONT:
remove if outside window

BACK:
remove if current makes it useless
```

---

## 2.9 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> maximumInWindow(const vector<int>& a, int k) {
    deque<int> dq;
    vector<int> ans;

    for (int i = 0; i < (int)a.size(); i++) {
        while (!dq.empty() && dq.front() <= i - k) {
            dq.pop_front();
        }

        while (!dq.empty() && a[dq.back()] <= a[i]) {
            dq.pop_back();
        }

        dq.push_back(i);

        if (i >= k - 1) {
            ans.push_back(a[dq.front()]);
        }
    }

    return ans;
}
```

---

## 2.10 Complexity

```text
Time  = O(n)
Space = O(k)
```

---

## 2.11 Recognition Signal

Think monotonic deque when:

```text
fixed-size sliding window
need max/min in every window
```

---

## 2.12 Common Mistakes

```text
1. Storing values instead of indices.
2. Wrong expiry condition.
3. Popping from front for dominance.
4. Popping from back for expiry.
5. Outputting before i >= k-1.
```

---

# 3. Sliding Window Minimum

Same idea as maximum.

Only reverse the dominance comparison.

---

## 3.1 Maximum vs Minimum

Maximum:

```text
keep values decreasing
front = largest
```

Pop:

```cpp
a[dq.back()] <= a[i]
```

Minimum:

```text
keep values increasing
front = smallest
```

Pop:

```cpp
a[dq.back()] >= a[i]
```

---

## 3.2 ASCII Comparison

Maximum deque:

```text
front              back

9   7   5   2

largest at front
```

Minimum deque:

```text
front              back

1   3   6   8

smallest at front
```

---

## 3.3 Example

```text
a = [4,2,5,1]
k = 2
```

Windows:

```text
[4,2] -> 2
[2,5] -> 2
[5,1] -> 1
```

Answer:

```text
[2,2,1]
```

---

## 3.4 Step-by-Step Dry Run

### i = 0, x = 4

Push:

```text
[0:4]
```

---

### i = 1, x = 2

Back:

```text
4 >= 2
```

Pop 4.

Push 2:

```text
[1:2]
```

First window answer:

```text
2
```

---

### i = 2, x = 5

Current window:

```text
[1..2]
```

Index 1 valid.

Back:

```text
2 >= 5 ? no
```

Push:

```text
[1:2, 2:5]
```

Minimum:

```text
2
```

---

### i = 3, x = 1

Expire index 1:

```text
1 <= 3-2
true
```

Deque:

```text
[2:5]
```

Back dominance:

```text
5 >= 1
```

pop.

Push 3:

```text
[3:1]
```

Minimum:

```text
1
```

---

## 3.5 C++17

```cpp
vector<int> minimumInWindow(const vector<int>& a, int k) {
    deque<int> dq;
    vector<int> ans;

    for (int i = 0; i < (int)a.size(); i++) {
        while (!dq.empty() && dq.front() <= i - k) {
            dq.pop_front();
        }

        while (!dq.empty() && a[dq.back()] >= a[i]) {
            dq.pop_back();
        }

        dq.push_back(i);

        if (i >= k - 1) {
            ans.push_back(a[dq.front()]);
        }
    }

    return ans;
}
```

---

## 3.6 Recognition Signal

If statement says:

```text
minimum in every fixed-size window
```

use increasing deque.

---

# 4. LC 2762 — Continuous Subarrays

**Problem:** [LeetCode 2762 — Continuous Subarrays](https://leetcode.com/problems/continuous-subarrays/)

## 4.1 What Is the Problem Asking?

Count subarrays such that:

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

---

## 4.2 Why One Deque Is Not Enough

Condition depends on both:

```text
maximum
minimum
```

So use:

```text
maxDQ
minDQ
```

`maxDQ`:

```text
decreasing values
front = maximum
```

`minDQ`:

```text
increasing values
front = minimum
```

---

## 4.3 Why the Pairwise Condition Reduces to Max-Min

If every pair must satisfy:

```text
|a[x] - a[y]| <= 2
```

the worst possible pair is:

```text
maximum
minimum
```

So checking:

```text
max - min <= 2
```

is sufficient.

---

## 4.4 Sliding Window Structure

Maintain:

```text
[l ... r]
```

When adding `r`:

```text
update maxDQ
update minDQ
```

If window invalid:

```text
max - min > 2
```

move `l` right until valid.

---

## 4.5 ASCII Visual

Suppose:

```text
window = [5,4,2]
```

Then:

```text
maxDQ.front() -> 5
minDQ.front() -> 2

5 - 2 = 3
```

Invalid.

Move `l`:

```text
window = [4,2]

4 - 2 = 2
```

Valid.

---

## 4.6 Why Add `r-l+1`?

Once `[l..r]` is valid:

```text
all suffixes ending at r are also valid
```

Those are:

```text
[l..r]
[l+1..r]
[l+2..r]
...
[r..r]
```

Count:

```text
r-l+1
```

ASCII:

```text
l              r
|--------------|

start at l      -> valid
start at l+1    -> valid
start at l+2    -> valid
...
start at r      -> valid

number of starts
=
r-l+1
```

---

## 4.7 Step-by-Step Dry Run — `[5,4,2,4]`

### r = 0

```text
window = [5]

max = 5
min = 5

difference = 0
valid
```

Valid endings:

```text
[5]
```

Count:

```text
1
```

```text
ans = 1
```

---

### r = 1

```text
window = [5,4]

max = 5
min = 4

difference = 1
```

Valid.

Ending at `r=1`:

```text
[5,4]
[4]
```

Count:

```text
2
```

```text
ans = 3
```

---

### r = 2

Initial:

```text
[5,4,2]

max = 5
min = 2

difference = 3
```

Invalid.

Move `l`:

```text
[4,2]

max = 4
min = 2

difference = 2
```

Valid.

Valid endings:

```text
[4,2]
[2]
```

Count:

```text
2
```

```text
ans = 5
```

---

### r = 3

```text
[4,2,4]

max = 4
min = 2

difference = 2
```

Valid.

Ending at `r=3`:

```text
[4,2,4]
[2,4]
[4]
```

Count:

```text
3
```

```text
ans = 8
```

---

## 4.8 Deque Roles

```text
maxDQ:
remove back while <= current

minDQ:
remove back while >= current
```

When `l` moves:

```text
if maxDQ.front() == l
    pop front

if minDQ.front() == l
    pop front
```

---

## 4.9 C++17

```cpp
class Solution {
public:
    long long continuousSubarrays(vector<int>& a) {
        deque<int> maxDQ, minDQ;

        long long ans = 0;
        int l = 0;

        for (int r = 0; r < (int)a.size(); r++) {
            while (!maxDQ.empty() &&
                   a[maxDQ.back()] <= a[r]) {
                maxDQ.pop_back();
            }

            maxDQ.push_back(r);

            while (!minDQ.empty() &&
                   a[minDQ.back()] >= a[r]) {
                minDQ.pop_back();
            }

            minDQ.push_back(r);

            while (a[maxDQ.front()] -
                   a[minDQ.front()] > 2) {

                if (maxDQ.front() == l) {
                    maxDQ.pop_front();
                }

                if (minDQ.front() == l) {
                    minDQ.pop_front();
                }

                l++;
            }

            ans += r - l + 1;
        }

        return ans;
    }
};
```

---

## 4.10 Complexity

```text
Time  = O(n)
Space = O(n) worst case
```

---

## 4.11 Recognition Signal

Think **two monotonic deques** when:

```text
dynamic window validity depends on max and min
```

Typical condition:

```text
max - min <= X
```

---

# 5. LC 862 — Shortest Subarray With Sum at Least K

**Problem:** [LeetCode 862 — Shortest Subarray with Sum at Least K](https://leetcode.com/problems/shortest-subarray-with-sum-at-least-k/)

## 5.1 What Is the Problem Asking?

Find shortest non-empty subarray whose sum is at least `K`.

Important:

```text
array may contain negative numbers
```

So ordinary two pointers do not work.

---

## 5.2 Why Normal Sliding Window Fails

With all positive numbers:

```text
increase right -> sum increases
increase left  -> sum decreases
```

Monotonic behavior exists.

With negatives:

```text
adding an element may decrease sum
removing an element may increase sum
```

So ordinary sliding window breaks.

---

## 5.3 Step 1 — Convert to Prefix Sum

Define:

```text
P[0] = 0

P[i] =
sum of first i elements
```

Then subarray:

```text
[j ... i-1]
```

has sum:

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

We also want shortest length:

```text
i - j
```

---

## 5.4 What Makes One Prefix Dominated?

Suppose:

```text
j1 < j2
```

and:

```text
P[j1] >= P[j2]
```

Then `j1` is worse than `j2`.

Why?

### Sum condition

Smaller prefix is better:

```text
P[j2] <= P[j1]
```

So:

```text
P[i] - P[j2]
>=
P[i] - P[j1]
```

`j2` makes reaching `K` easier.

### Length condition

Since:

```text
j2 > j1
```

then:

```text
i-j2 < i-j1
```

So `j2` also gives shorter subarray.

Therefore:

```text
j1 is permanently useless
```

Pop it from back.

---

## 5.5 Deque Invariant

Maintain prefix values increasing:

```text
P[dq[0]] < P[dq[1]] < P[dq[2]] < ...
```

ASCII:

```text
front                           back

small prefix  --->  larger prefix
older candidate      newer candidate
```

---

## 5.6 Two Different Pop Rules

This advanced pattern has two different reasons to pop.

### FRONT — Candidate Is Ready to Answer

If:

```text
P[i] - P[dq.front()] >= K
```

then we found a valid subarray.

Length:

```text
i - dq.front()
```

Record it.

Then pop front.

Why?

A later candidate may also satisfy and produce shorter length.

---

### BACK — Candidate Is Dominated

If:

```text
P[dq.back()] >= P[i]
```

old candidate is worse because current prefix is:

```text
smaller/equal
AND
newer
```

Pop back.

---

## 5.7 ASCII Picture

```text
deque of prefix indices:

front                              back
  |                                  |
  v                                  v

[j1, j2, j3, j4]

prefix values:

P[j1] < P[j2] < P[j3] < P[j4]
```

When current `i` arrives:

```text
FRONT:
while P[i] - P[front] >= K
    valid answer found
    pop front

BACK:
while P[back] >= P[i]
    dominated
    pop back
```

---

## 5.8 Dry Run — `[2,-1,2]`, K = 3

Prefix:

```text
P[0] = 0
P[1] = 2
P[2] = 1
P[3] = 3

P = [0,2,1,3]
```

### i = 0

```text
P = 0

dq empty

push 0

dq = [0]
```

---

### i = 1

```text
P = 2

2 - P[0]
=
2 - 0
=
2 < 3
```

No valid answer.

Back dominance?

```text
P[0] = 0 >= 2 ? no
```

Push:

```text
dq = [0,1]
```

---

### i = 2

```text
P = 1
```

Front:

```text
1 - 0 < 3
```

Back:

```text
P[1] = 2 >= 1
```

Index 1 is dominated.

Pop back.

Now:

```text
dq = [0]
```

Push 2:

```text
dq = [0,2]
```

---

### i = 3

```text
P = 3
```

Front:

```text
3 - P[0]
=
3 - 0
=
3
```

Valid.

Length:

```text
3 - 0
=
3
```

Answer:

```text
3
```

Pop front.

Remaining:

```text
[2]
```

---

## 5.9 C++17

```cpp
class Solution {
public:
    int shortestSubarray(vector<int>& nums, int k) {
        int n = nums.size();

        vector<long long> prefix(n + 1, 0);

        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }

        deque<int> dq;
        int ans = n + 1;

        for (int i = 0; i <= n; i++) {
            while (!dq.empty() &&
                   prefix[i] - prefix[dq.front()] >= k) {

                ans = min(ans, i - dq.front());
                dq.pop_front();
            }

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

---

## 5.10 Complexity

```text
Time  = O(n)
Space = O(n)
```

---

## 5.11 Recognition Signal

Think this advanced form when:

```text
shortest / best subarray
sum constraint
negative numbers exist
prefix sum inequality
need best prefix candidate
```

Mental chain:

```text
subarray sum
    ↓
prefix difference
    ↓
algebra
    ↓
which prefix candidates are dominated?
    ↓
monotonic deque
```

---

# 6. Core Templates

## 6.1 Sliding Window Maximum

```cpp
deque<int> dq;

for (int i = 0; i < n; i++) {
    while (!dq.empty() && dq.front() <= i - k) {
        dq.pop_front();
    }

    while (!dq.empty() && a[dq.back()] <= a[i]) {
        dq.pop_back();
    }

    dq.push_back(i);

    if (i >= k - 1) {
        cout << a[dq.front()] << ' ';
    }
}
```

Mental model:

```text
front -> expired
back  -> dominated
front -> maximum
```

---

## 6.2 Sliding Window Minimum

```cpp
deque<int> dq;

for (int i = 0; i < n; i++) {
    while (!dq.empty() && dq.front() <= i - k) {
        dq.pop_front();
    }

    while (!dq.empty() && a[dq.back()] >= a[i]) {
        dq.pop_back();
    }

    dq.push_back(i);

    if (i >= k - 1) {
        cout << a[dq.front()] << ' ';
    }
}
```

Mental model:

```text
front -> expired
back  -> dominated
front -> minimum
```

---

## 6.3 Dynamic Window With Max + Min

```cpp
deque<int> maxDQ, minDQ;
int l = 0;

for (int r = 0; r < n; r++) {
    while (!maxDQ.empty() &&
           a[maxDQ.back()] <= a[r]) {
        maxDQ.pop_back();
    }

    maxDQ.push_back(r);

    while (!minDQ.empty() &&
           a[minDQ.back()] >= a[r]) {
        minDQ.pop_back();
    }

    minDQ.push_back(r);

    while (/* window invalid */) {
        if (maxDQ.front() == l) {
            maxDQ.pop_front();
        }

        if (minDQ.front() == l) {
            minDQ.pop_front();
        }

        l++;
    }

    // [l..r] is valid
}
```

---

## 6.4 Prefix Optimization Deque

```cpp
deque<int> dq;

for (int i = 0; i <= n; i++) {
    while (!dq.empty() &&
           prefix[i] - prefix[dq.front()] >= k) {

        // front gives a valid answer
        dq.pop_front();
    }

    while (!dq.empty() &&
           prefix[dq.back()] >= prefix[i]) {

        // back is dominated
        dq.pop_back();
    }

    dq.push_back(i);
}
```

This is not a normal fixed-size window.

It is:

```text
prefix optimization
+
dominance
+
best candidate maintenance
```

---

# 7. Recognition Checklist

When you see a window/deque problem, ask:

```text
1. Is there a contiguous window / subarray?

2. Do I need max or min repeatedly?

3. Does the window move?
   fixed size?
   dynamic size?

4. What leaves from the LEFT?
   expired index?
   invalid window boundary?
   already-used candidate?

5. What becomes useless at the BACK?
   smaller value for max?
   larger value for min?
   worse prefix sum?

6. Should deque be:
   decreasing?
   increasing?

7. What does FRONT represent?
   maximum?
   minimum?
   best prefix candidate?

8. Do I need one deque or two?

9. Are there negative numbers?
   If yes, normal two pointers may fail.

10. Can I rewrite subarray condition
    using prefix sums and algebra?
```

---

# 8. Common Mistakes

## 8.1 Storing Values Instead of Indices

Bad for sliding windows:

```cpp
deque<int> dq; // storing only values conceptually
```

You cannot know exactly when that occurrence expires.

Prefer:

```cpp
deque<int> dq; // indices
```

Then:

```text
value  = a[index]
expiry = index position
```

---

## 8.2 Wrong Expiry Formula

Current window:

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

## 8.3 Mixing Front and Back Jobs

Remember:

```text
FRONT
=====
expiry
window movement
ready answer candidate

BACK
====
dominance
monotonic order
```

---

## 8.4 Wrong Comparison

Maximum:

```cpp
a[dq.back()] <= a[i]
```

Minimum:

```cpp
a[dq.back()] >= a[i]
```

---

## 8.5 Output Too Early

First full size-`k` window ends at:

```text
i = k-1
```

So:

```cpp
if (i >= k - 1) {
    ...
}
```

---

## 8.6 Forgetting Two Deques for Max-Min Condition

Condition:

```text
max - min <= X
```

needs both:

```text
maxDQ
minDQ
```

One deque is not enough.

---

## 8.7 Using Ordinary Sliding Window with Negatives

For sum constraints and negative numbers:

```text
sum is not monotonic
```

So classic two pointers may fail.

Consider:

```text
prefix sum
+
monotonic deque
```

---

## 8.8 Thinking Nested `while` Means O(n²)

Wrong intuition:

```text
for + while = O(n²)
```

Not here.

Each index:

```text
enters once
leaves once
```

So amortized total is:

```text
O(n)
```

---

# 9. Complexity

The standard monotonic deque proof:

```text
each index is pushed once

each index is removed at most once
through front or back
```

Therefore:

```text
total push operations <= n
total pop operations  <= n
```

Hence:

```text
Time = O(n)
```

Space:

```text
fixed-size window:
O(k)

dynamic/prefix forms:
O(n) worst case
```

---

# 10. Final Pattern Map

| Pattern | Deque Order | Front Gives | Back Removes |
|---|---|---|---|
| Window maximum | decreasing values | maximum | `<= current` |
| Window minimum | increasing values | minimum | `>= current` |
| Max-min bounded window | two deques | max + min | both rules |
| Prefix optimization | increasing prefix | best oldest candidate | worse prefix |

---

## Final Visual Mental Model

```text
NEW INDEX ARRIVES
        |
        v
+-----------------------------+
| FRONT                       |
| remove too old / ready      |
+-----------------------------+
        |
        v
+-----------------------------+
| BACK                        |
| remove dominated / useless  |
+-----------------------------+
        |
        v
push current index
        |
        v
FRONT = best surviving candidate
```

And the sentence to remember:

```text
FRONT = old / ready / answer
BACK  = weak / dominated
```

For fixed-size windows:

```text
window = [i-k+1, i]

expired
<=> index < i-k+1
<=> index <= i-k
```

For valid dynamic windows:

```text
number of valid subarrays ending at r
=
r-l+1
```

For prefix-sum deque problems:

```text
subarray sum
=
P[r] - P[l]
```

Then use algebra to decide:

```text
which prefix candidates are useful
and which are dominated
```
