# AlgoZenith — Binary Search Application 1
## Painter Partition / Minimize the Maximum
### TLE-Style Self-Study Notes

> **Source:** supplied AlgoZenith lecture PDF — **Binary Search Application 1**.
>
> The lecture develops the classical **Painter Partition** pattern:
>
> ```text
> ordered work items
> + K painters
> + each painter gets a contiguous block
> + time of a painter = sum of assigned work
> + all painters work in parallel
> → minimize the maximum painter time
> ```
>
> This is a standard **Binary Search on Answer** problem.

---

# Table of Contents

- [1. What the Problem Is Asking](#1-what-the-problem-is-asking)
- [2. Small Example First](#2-small-example-first)
- [3. Story Removed](#3-story-removed)
- [4. Core Optimization Idea](#4-core-optimization-idea)
- [5. Why This Is Binary Search on Answer](#5-why-this-is-binary-search-on-answer)
- [6. Search Space](#6-search-space)
- [7. Monotonicity](#7-monotonicity)
- [8. Designing `check(time)`](#8-designing-checktime)
- [9. Greedy Sweep Inside `check`](#9-greedy-sweep-inside-check)
- [10. Full Dry Run of `check`](#10-full-dry-run-of-check)
- [11. Compact Binary Search Dry Run](#11-compact-binary-search-dry-run)
- [12. Correctness Intuition](#12-correctness-intuition)
- [13. Properly Formatted C++17](#13-properly-formatted-c17)
- [14. Complexity](#14-complexity)
- [15. Common Mistakes](#15-common-mistakes)
- [16. Recognition Pattern](#16-recognition-pattern)
- [17. Final Mental Model](#17-final-mental-model)

---

# 1. What the Problem Is Asking

We have:

```text
N work items / walls / boards

time needed:
t1, t2, t3, ... , tN

K painters
```

Each painter must paint a **contiguous** block.

Example:

```text
t1 t2 t3 t4 t5
|--|--|--|--|--|
```

With `K = 3`, one possible assignment is:

```text
Painter 1       Painter 2       Painter 3
[t1]            [t2+t3+t4]      [t5]
```

All painters work at the same time.

So total completion time is not:

```text
sum of every painter time
```

It is:

```text
maximum painter time
```

because we finish only when the slowest painter finishes.

---

# 2. Small Example First

Lecture example:

```text
arr = [2,7,1,8,3,4,5]
K = 3
```

One good partition is:

```text
Painter 1: [2,7,1] → 10
Painter 2: [8,3]   → 11
Painter 3: [4,5]   → 9
```

ASCII:

```text
[ 2  7  1 ] [ 8  3 ] [ 4  5 ]
     10          11         9
```

All three work in parallel.

Therefore completion time is:

```text
max(10,11,9)
=
11
```

The lecture reaches:

```text
answer = 11
```

---

# 3. Story Removed

Forget painters and walls.

The mathematical problem is:

```text
Given:
arr[0...N-1]
K

Split the array into
at most K contiguous groups.

For each group:
groupCost = sum(group)

Find:

minimum possible value of

max(
    groupCost1,
    groupCost2,
    ...
)
```

Short form:

```text
minimize the maximum contiguous partition sum
```

This phrase should trigger the pattern immediately.

---

# 4. Core Optimization Idea

The lecture explicitly highlights:

```text
minimize the maximum
```

This is a very common Binary Search signal.

Other related forms are:

```text
maximize the minimum
```

For this problem:

```text
we are not directly searching a painter
or an array index.

We are searching the answer:
maximum allowed time T.
```

---

# 5. Why This Is Binary Search on Answer

Suppose we guess:

```text
T = 13
```

Ask:

```text
Can all work be completed
in <= 13 time
using <= K painters?
```

If YES, maybe we can do even better:

```text
12?
11?
10?
```

If NO, we need more time:

```text
14?
15?
...
```

So instead of directly calculating the optimal answer:

```text
guess T
→ test T
→ binary-search T
```

---

# 6. Search Space

We need lower and upper bounds for `T`.

## Lower Bound

At minimum:

```text
T >= max(arr)
```

Why?

A single item cannot be split.

For:

```text
arr = [2,7,1,8,3,4,5]
```

largest item:

```text
8
```

Therefore:

```text
low = 8
```

## Upper Bound

Worst case:

```text
one painter does everything
```

So:

```text
T <= sum(arr)
```

For the lecture example:

```text
2+7+1+8+3+4+5
=
30
```

Therefore:

```text
high = 30
```

Final search range:

```text
8 <= T <= 30
```

General formula:

```text
max(arr) <= answer <= sum(arr)
```

---

# 7. Monotonicity

Binary Search needs a monotonic answer space.

Define:

```text
check(T)
=
Can we finish all work
within T time
using at most K painters?
```

Suppose:

```text
check(11) = true
```

Then:

```text
check(12) = true
check(13) = true
check(14) = true
...
```

because giving painters more allowed time cannot make the assignment harder.

But smaller values may fail:

```text
T:       8  9 10 11 12 13 14 ...
check:   N  N  N  Y  Y  Y  Y ...
                  ^
                  |
              first YES
```

We need:

```text
first feasible T
```

So this is:

```text
first True / first Yes
```

---

# 8. Designing `check(time)`

The most important sentence in the problem is:

```text
check(time)
=
Can we complete all painting
in <= time
using <= K painters?
```

That is the complete feasibility question.

The outer Binary Search is easy once this function is correct.

---

# 9. Greedy Sweep Inside `check`

For a fixed allowed time:

```text
time
```

start with painter 1.

Give that painter as much consecutive work as possible.

When the next item does not fit:

```text
start a new painter
```

Why?

Because if the current painter still has capacity, starting another painter earlier gives no advantage for feasibility.

## Remaining-Time View

The lecture code uses:

```text
remainingTime
```

Initialize:

```text
remainingTime = time
```

For each item:

```text
if item fits:
    remainingTime -= item

else:
    start new painter
    remainingTime = time - item
```

If painters needed becomes:

```text
> K
```

then:

```text
check(time) = false
```

Otherwise:

```text
true
```

---

# 10. Full Dry Run of `check`

Use:

```text
arr = [2,7,1,8,3,4,5]
K = 3
```

## Check `T = 13`

```text
2 fits  → remaining=11
7 fits  → remaining=4
1 fits  → remaining=3
8 no    → painter=2, remaining=5
3 fits  → remaining=2
4 no    → painter=3, remaining=9
5 fits  → remaining=4

painters used = 3
check(13) = true
```

Partition:

```text
[2,7,1] [8,3] [4,5]
    10      11      9
```

## Check `T = 10`

```text
2 fits  → remaining=8
7 fits  → remaining=1
1 fits  → remaining=0
8 no    → painter=2, remaining=2
3 no    → painter=3, remaining=7
4 fits  → remaining=3
5 no    → painter=4

painters used = 4 > K
check(10) = false
```

---

# 11. Compact Binary Search Dry Run

Lecture values:

```text
low = 8
high = 30
```

```text
Iter 1: low=8,  high=30 → mid=19 → check(19)=YES → ans=19 → high=18
Iter 2: low=8,  high=18 → mid=13 → check(13)=YES → ans=13 → high=12
Iter 3: low=8,  high=12 → mid=10 → check(10)=NO  → low=11
Iter 4: low=11, high=12 → mid=11 → check(11)=YES → ans=11 → high=10

answer = 11
```

---

# 12. Correctness Intuition

## 12.1 Why the Greedy Checker Works

For a fixed limit `T`:

```text
keep adding consecutive items while they fit
```

This packs the current painter as much as possible.

Starting a new painter earlier leaves unused capacity and cannot reduce the number of painters needed later.

Therefore the greedy sweep gives the minimum number of painters needed for that fixed `T`.

## 12.2 Why Binary Search Works

Feasibility is monotonic:

```text
if T works
then every larger time also works
```

So the answer space is:

```text
NO NO NO ... YES YES YES ...
```

We want:

```text
first YES
```

Binary Search finds that boundary.

---

# 13. Properly Formatted C++17

The lecture code, reformatted cleanly while keeping the same core logic:

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

bool check(ll allowedTime, const vector<ll>& arr, ll k) {
    ll paintersNeeded = 1;
    ll remainingTime = allowedTime;

    for (ll work : arr) {
        if (remainingTime - work >= 0) {
            remainingTime -= work;
        } else {
            paintersNeeded++;

            if (paintersNeeded > k) {
                return false;
            }

            remainingTime = allowedTime - work;
        }
    }

    return true;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    ll n, k;
    cin >> n >> k;

    vector<ll> arr(n);

    ll maximumItem = 0;
    ll totalSum = 0;

    for (ll i = 0; i < n; i++) {
        cin >> arr[i];

        maximumItem = max(maximumItem, arr[i]);
        totalSum += arr[i];
    }

    ll low = maximumItem;
    ll high = totalSum;
    ll answer = totalSum;

    while (low <= high) {
        ll mid = low + (high - low) / 2;

        if (check(mid, arr, k)) {
            answer = mid;
            high = mid - 1;
        } else {
            low = mid + 1;
        }
    }

    cout << answer << '\n';

    return 0;
}
```

## Same Logic — Compact CP Style

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

bool check(ll mid, const vector<ll>& arr, ll k) {
    ll painters = 1;
    ll remaining = mid;

    for (ll work : arr) {
        if (remaining >= work) {
            remaining -= work;
        } else {
            painters++;

            if (painters > k) {
                return false;
            }

            remaining = mid - work;
        }
    }

    return true;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    ll n, k;
    cin >> n >> k;

    vector<ll> arr(n);

    ll low = 0;
    ll high = 0;

    for (ll& x : arr) {
        cin >> x;
        low = max(low, x);
        high += x;
    }

    ll answer = high;

    while (low <= high) {
        ll mid = low + (high - low) / 2;

        if (check(mid, arr, k)) {
            answer = mid;
            high = mid - 1;
        } else {
            low = mid + 1;
        }
    }

    cout << answer << '\n';

    return 0;
}
```

---

# 14. Complexity

Let:

```text
S = sum(arr)
M = max(arr)
```

Each `check(mid)`:

```text
O(N)
```

Binary Search iterations:

```text
O(log(S-M+1))
```

Total:

```text
O(N log(S-M+1))
```

Usually written as:

```text
O(N log S)
```

Extra space:

```text
O(1)
```

apart from input storage.

---

# 15. Common Mistakes

## Mistake 1 — Searching Array Indices

Wrong:

```text
binary-search painter position
```

Correct:

```text
binary-search answer T
```

## Mistake 2 — Wrong Lower Bound

Use:

```text
low = max(arr)
```

because an item cannot be split.

## Mistake 3 — Wrong Upper Bound

Use:

```text
high = sum(arr)
```

because one painter can do everything.

## Mistake 4 — Forgetting Contiguity

Valid:

```text
[2,7,1] [8,3] [4,5]
```

The groups must remain contiguous.

## Mistake 5 — Going Right After `check(mid)=true`

We want the minimum feasible time.

Therefore:

```text
check(mid)=true
→ save mid
→ search LEFT
```

## Mistake 6 — Starting With Zero Painters

Start with:

```text
paintersNeeded = 1
```

The first group already belongs to painter 1.

## Mistake 7 — Using `int` for Large Sums

Prefer:

```cpp
long long
```

for sums and answer-space values.

---

# 16. Recognition Pattern

When a problem says:

```text
split ordered items into K groups
```

and asks:

```text
minimize the maximum group sum
```

think:

```text
Binary Search on Answer
+
Greedy Sweep
```

Common variants:

```text
Painter Partition
Book Allocation
Array Division
Split Array Largest Sum
Minimum Capacity
Workload Distribution
```

The story changes.

The mathematical form remains:

```text
contiguous partition
+
minimize maximum
```

---

# 17. Final Mental Model

```text
Painter Partition
      |
      v
minimize maximum painter time
      |
      v
search answer T
      |
      v
low  = max(arr)
high = sum(arr)
      |
      v
check(T)
=
can we finish with <= K painters?
      |
      v
greedy left-to-right sweep
      |
      v
NO NO NO YES YES YES
         ^
         |
     first YES
      |
      v
Binary Search
```

Remember:

```text
Guess the maximum allowed load T,
then ask whether K workers are enough.
```

Once that checker is monotonic:

```text
Binary Search finds the minimum feasible T.
```
