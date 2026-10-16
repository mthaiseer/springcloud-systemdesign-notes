# TLE Eliminators — Sliding Window Problem Solving
## Sliding Window Median, Sliding Window Cost, and Maximum Subarray Sum II
### TLE-Style Self-Study Notes

> **Source:** supplied TLE Eliminators PDF — *Sliding Window Problem Solving*.
>
> This lecture moves beyond basic fixed-window sum/count problems and asks:
>
> ```text
> What information must the window maintain
> so that ADD, REMOVE, and QUERY stay efficient?
> ```
>
> The three main problems are:
>
> ```text
> 1. Sliding Window Median
> 2. CSES Sliding Window Cost
> 3. CSES Maximum Subarray Sum II
> ```
>
> The recurring pattern is:
>
> ```text
> fixed window
> +
> non-trivial window query
> +
> choose a data structure that supports
> INSERT / ERASE / QUERY efficiently
> ```

---

# Table of Contents

- [1. Lecture Roadmap](#1-lecture-roadmap)
- [2. Prerequisite 1 — Fixed Sliding Window Skeleton](#2-prerequisite-1--fixed-sliding-window-skeleton)
- [3. Prerequisite 2 — `multiset` and Duplicate-Safe Erase](#3-prerequisite-2--multiset-and-duplicate-safe-erase)
- [4. Prerequisite 3 — Why One Multiset Is Not Enough for Fast Median](#4-prerequisite-3--why-one-multiset-is-not-enough-for-fast-median)
- [5. Prerequisite 4 — Split the Window into Two Halves](#5-prerequisite-4--split-the-window-into-two-halves)
- [6. Prerequisite 5 — Rebalancing the Two Multisets](#6-prerequisite-5--rebalancing-the-two-multisets)
- [7. Prerequisite 6 — Median from the Two Halves](#7-prerequisite-6--median-from-the-two-halves)
- [8. Problem 1 — Sliding Window Median](#8-problem-1--sliding-window-median)
- [9. Detailed Median Dry Run](#9-detailed-median-dry-run)
- [10. C++17 — Two-Multiset Median](#10-c17--two-multiset-median)
- [11. Alternative from the Lecture — PBDS Ordered Set](#11-alternative-from-the-lecture--pbds-ordered-set)
- [12. Problem 2 — CSES Sliding Window Cost](#12-problem-2--cses-sliding-window-cost)
- [13. Prerequisite 7 — Why Median Minimizes Absolute-Deviation Cost](#13-prerequisite-7--why-median-minimizes-absolute-deviation-cost)
- [14. Deriving the O(1) Cost Formula](#14-deriving-the-o1-cost-formula)
- [15. Detailed Cost Dry Run](#15-detailed-cost-dry-run)
- [16. C++17 — Sliding Window Cost](#16-c17--sliding-window-cost)
- [17. Problem 3 — CSES Maximum Subarray Sum II](#17-problem-3--cses-maximum-subarray-sum-ii)
- [18. Prerequisite 8 — Prefix Sum Subarray Formula](#18-prerequisite-8--prefix-sum-subarray-formula)
- [19. Convert Length Range [A,B] into a Prefix-Index Window](#19-convert-length-range-ab-into-a-prefix-index-window)
- [20. Detailed Maximum-Subarray Dry Run](#20-detailed-maximum-subarray-dry-run)
- [21. C++17 — Maximum Subarray Sum II](#21-c17--maximum-subarray-sum-ii)
- [22. Complexity Comparison](#22-complexity-comparison)
- [23. Common Mistakes](#23-common-mistakes)
- [24. Recognition Table](#24-recognition-table)
- [25. Final Mental Model](#25-final-mental-model)

---

# 1. Lecture Roadmap

The lecture uses Sliding Window as a **data-structure problem**.

Basic fixed window:

```text
ADD entering element
REMOVE expired element
QUERY current window
```

For a simple sum:

```text
state = one number
```

For harder queries:

```text
median
minimum cost to equalize
best prefix candidate
```

the challenge is choosing a structure that supports all three operations efficiently.

The lecture problems are:

```text
Sliding Window Median
        ↓
two multisets / PBDS

Sliding Window Cost
        ↓
median + two multisets + sums

Maximum Subarray Sum II
        ↓
prefix sum + sliding window over valid prefix indices
```

---

# 2. Prerequisite 1 — Fixed Sliding Window Skeleton

For a window of exact size `K`:

```text
head = current right index
```

The element that expires is:

```text
head - K
```

The first complete window appears at:

```text
head = K - 1
```

Generic skeleton:

```cpp
for (int head = 0; head < n; head++) {
    add(arr[head]);

    if (head >= k) {
        remove(arr[head - k]);
    }

    if (head >= k - 1) {
        queryWindow();
    }
}
```

ASCII:

```text
old:
[ a b c d ]

new:
  [ b c d e ]

remove a
add e
```

The harder problems in this lecture keep this same movement.

Only the maintained structure changes.

---

# 3. Prerequisite 2 — `multiset` and Duplicate-Safe Erase

A `multiset` stores values in sorted order and allows duplicates.

Example:

```text
insert:
2, 2, 5, 1
```

Conceptually:

```text
[1,2,2,5]
```

Operations:

```cpp
ms.insert(x);
ms.find(x);
ms.begin();
ms.rbegin();
```

Typical complexity:

```text
insert = O(log K)
erase  = O(log K)
find   = O(log K)
```

---

## Important: Remove Only One Copy

Suppose:

```text
ms = [1,2,2,5]
```

The outgoing window element is one `2`.

Do **not** use:

```cpp
ms.erase(2);
```

because that can remove every `2`.

Use:

```cpp
auto it = ms.find(2);

if (it != ms.end()) {
    ms.erase(it);
}
```

Now only one occurrence disappears.

---

# 4. Prerequisite 3 — Why One Multiset Is Not Enough for Fast Median

A multiset keeps the window sorted.

So it seems tempting to:

```text
insert/remove in O(log K)
then walk to the middle
```

But a normal multiset does not provide:

```text
"give me the K/2-th element"
```

in O(1) or O(log K).

If we repeatedly advance an iterator roughly:

```text
K/2 positions
```

for every window, the median query becomes:

```text
O(K)
```

So the total can become roughly:

```text
O(N log K + NK)
```

which loses the benefit for large `K`.

The lecture's main fix:

```text
maintain the middle continuously
```

using:

```text
two multisets
```

---

# 5. Prerequisite 4 — Split the Window into Two Halves

Maintain:

```text
low
high
```

where:

```text
low  = smaller half
high = larger half
```

Invariant:

```text
every value in low <= every value in high
```

Example:

```text
window values:
[1,2,2,3,5]
```

Split:

```text
low  = [1,2,2]
high = [3,5]
```

ASCII:

```text
        LOW             HIGH
   [1, 2, 2]          [3, 5]
           ^          ^
           |          |
      max(low)     min(high)
```

The median is now sitting directly at the boundary.

---

# 6. Prerequisite 5 — Rebalancing the Two Multisets

We maintain:

```text
low.size() == high.size()
```

or:

```text
low.size() == high.size() + 1
```

So `low` is allowed to have exactly one extra element.

---

## Invalid Case 1

```text
high.size() > low.size()
```

Then move the smallest value from `high` to `low`.

```text
high → low
```

C++:

```cpp
auto it = high.begin();
low.insert(*it);
high.erase(it);
```

---

## Invalid Case 2

```text
low.size() > high.size() + 1
```

Then move the largest value from `low` to `high`.

```text
low → high
```

C++:

```cpp
auto it = prev(low.end());
high.insert(*it);
low.erase(it);
```

---

## Tiny Balance Example

Suppose:

```text
low  = [1,2,3]
high = [4]
```

Sizes:

```text
3 and 1
```

Too unbalanced.

Move largest of `low`:

```text
3
```

Result:

```text
low  = [1,2]
high = [3,4]
```

Balanced.

---

# 7. Prerequisite 6 — Median from the Two Halves

If total size is odd:

```text
low has one extra value
```

Median:

```text
max(low)
```

C++:

```cpp
*low.rbegin()
```

---

If total size is even:

```text
low.size() == high.size()
```

Middle values are:

```text
max(low)
min(high)
```

Median:

```text
(max(low) + min(high)) / 2
```

Use floating-point division:

```cpp
(double(*low.rbegin()) + double(*high.begin())) / 2.0
```

---

# 8. Problem 1 — Sliding Window Median

Lecture problem:

```text
LeetCode — Sliding Window Median
```

Goal:

```text
for every size-K window,
output the median
```

Example teaching input:

```text
arr = [1,3,-1,-3,5,3,6,7]
K = 3
```

First windows:

```text
[1,3,-1]   → sorted [-1,1,3]   → median 1
[3,-1,-3]  → sorted [-3,-1,3]  → median -1
[-1,-3,5]  → sorted [-3,-1,5]  → median -1
```

We cannot sort every window independently.

Instead:

```text
ADD new value
REMOVE expired value
BALANCE low/high
READ median
```

---

## Insert Rule

If:

```text
low is empty
```

or:

```text
x <= max(low)
```

insert into `low`.

Otherwise:

```text
insert into high
```

Then rebalance.

---

## Remove Rule

Try to find the outgoing value in `low`.

If found:

```text
erase one copy from low
```

otherwise erase one copy from `high`.

Then rebalance.

Because duplicates are allowed, erase by iterator.

---

# 9. Detailed Median Dry Run

Teaching example:

```text
window = [2,2,3,5,1]
K = 5
```

Insert values one by one.

---

## Insert 2

```text
low = [2]
high = []
```

---

## Insert second 2

It is:

```text
<= max(low)
```

Temporary:

```text
low = [2,2]
high = []
```

Too many in `low`.

Move largest `2` to `high`:

```text
low = [2]
high = [2]
```

---

## Insert 3

Since:

```text
3 > max(low)=2
```

insert into `high`:

```text
low = [2]
high = [2,3]
```

`high` is larger.

Move its smallest `2` to `low`:

```text
low = [2,2]
high = [3]
```

---

## Insert 5

```text
low = [2,2]
high = [3,5]
```

Balanced.

---

## Insert 1

```text
1 <= max(low)=2
```

So:

```text
low = [1,2,2]
high = [3,5]
```

Total size:

```text
5
```

Median:

```text
max(low)
=
2
```

---

# 10. C++17 — Two-Multiset Median

```cpp
#include <bits/stdc++.h>
using namespace std;

class MedianWindow {
    multiset<long long> low;
    multiset<long long> high;

    void balance() {
        while (low.size() > high.size() + 1) {
            auto it = prev(low.end());

            high.insert(*it);
            low.erase(it);
        }

        while (high.size() > low.size()) {
            auto it = high.begin();

            low.insert(*it);
            high.erase(it);
        }
    }

public:
    void add(long long value) {
        if (low.empty() || value <= *low.rbegin()) {
            low.insert(value);
        } else {
            high.insert(value);
        }

        balance();
    }

    void remove(long long value) {
        auto itLow = low.find(value);

        if (itLow != low.end()) {
            low.erase(itLow);
        } else {
            auto itHigh = high.find(value);

            if (itHigh != high.end()) {
                high.erase(itHigh);
            }
        }

        balance();
    }

    double median() const {
        if (low.size() > high.size()) {
            return double(*low.rbegin());
        }

        return (double(*low.rbegin()) + double(*high.begin())) / 2.0;
    }
};

vector<double> slidingWindowMedian(const vector<int>& nums, int k) {
    int n = nums.size();

    MedianWindow window;
    vector<double> answer;

    for (int head = 0; head < n; head++) {
        window.add(nums[head]);

        if (head >= k) {
            window.remove(nums[head - k]);
        }

        if (head >= k - 1) {
            answer.push_back(window.median());
        }
    }

    return answer;
}
```

Complexity:

```text
ADD      = O(log K)
REMOVE   = O(log K)
MEDIAN   = O(1)

Total    = O(N log K)
Space    = O(K)
```

---

# 11. Alternative from the Lecture — PBDS Ordered Set

The lecture also introduces GNU PBDS:

```text
Policy Based Data Structures
```

Important operation:

```cpp
find_by_order(index)
```

which retrieves an element by sorted rank.

That directly solves:

```text
"give me the middle element"
```

---

## Why Store `(value,index)` Pairs?

An ordered set normally does not allow duplicates.

If the window contains:

```text
2,2,2
```

store:

```text
(2,index1)
(2,index2)
(2,index3)
```

Now every item is unique.

---

## PBDS Template

> This is a GNU competitive-programming extension, not standard-library C++.

```cpp
#include <bits/stdc++.h>
#include <ext/pb_ds/assoc_container.hpp>
#include <ext/pb_ds/tree_policy.hpp>

using namespace std;
using namespace __gnu_pbds;

using Item = pair<long long, int>;

using OrderedSet = tree<
    Item,
    null_type,
    less<Item>,
    rb_tree_tag,
    tree_order_statistics_node_update
>;
```

For odd `K`:

```cpp
auto middle = *window.find_by_order(k / 2);
```

For even `K`:

```cpp
auto leftMiddle = *window.find_by_order(k / 2 - 1);
auto rightMiddle = *window.find_by_order(k / 2);
```

The lecture presents this as an alternative to the two-multiset approach.

For learning and interview portability:

```text
two multisets
```

is usually the more transferable template.

---

# 12. Problem 2 — CSES Sliding Window Cost

Lecture problem:

```text
CSES — Sliding Window Cost
```

For every size-`K` window:

```text
make all values equal
```

A change of:

```text
x → y
```

costs:

```text
|x-y|
```

We need the minimum total cost.

The central lecture observation:

```text
To minimize total absolute difference,
make all values equal to a median.
```

So every window needs:

```text
median
+
cost to move everything to median
```

---

# 13. Prerequisite 7 — Why Median Minimizes Absolute-Deviation Cost

Teaching example based on the lecture's picture:

```text
values = [1,2,3,4,7]
```

Median:

```text
3
```

Cost to change everything to `3`:

```text
|1-3| + |2-3| + |3-3| + |4-3| + |7-3|

=
2 + 1 + 0 + 1 + 4

=
8
```

Try target `4`:

```text
3 + 2 + 1 + 0 + 3
=
9
```

So `4` is worse.

The reason is that moving the target away from the median makes the imbalance on one side dominate.

For this lecture, the fact to remember is:

```text
sum of absolute deviations
→ minimized at a median
```

---

# 14. Deriving the O(1) Cost Formula

Suppose the two multisets are:

```text
low  = values <= median side
high = values >= median side
```

Choose:

```text
median = max(low)
```

Maintain:

```text
lowSum  = sum(low)
highSum = sum(high)
```

---

## Left-Side Cost

Every value `x` in `low` must increase to `median`.

Cost:

```text
(median-x)
```

Summed over all `low` values:

```text
median + median + ... + median
-
sum(low)
```

There are:

```text
low.size()
```

copies of `median`.

Therefore:

```text
leftCost
=
low.size() * median - lowSum
```

---

## Right-Side Cost

Every `x` in `high` must decrease to `median`.

Cost:

```text
x - median
```

Summed:

```text
highSum
-
high.size() * median
```

Therefore:

```text
rightCost
=
highSum - high.size() * median
```

---

## Final Formula

```text
cost
=
low.size() * median - lowSum
+
highSum - high.size() * median
```

This turns a full `O(K)` cost scan into:

```text
O(1)
```

once the two halves and their sums are maintained.

---

# 15. Detailed Cost Dry Run

Use:

```text
window = [2,3,4,7,5]
```

Sorted:

```text
[2,3,4,5,7]
```

Median:

```text
4
```

Split:

```text
low  = [2,3,4]
high = [5,7]
```

Sums:

```text
lowSum
=
2+3+4
=
9

highSum
=
5+7
=
12
```

Left cost:

```text
3*4 - 9
=
12 - 9
=
3
```

Right cost:

```text
12 - 2*4
=
12 - 8
=
4
```

Total:

```text
3 + 4
=
7
```

Direct check:

```text
|2-4| + |3-4| + |4-4| + |5-4| + |7-4|

=
2 + 1 + 0 + 1 + 3

=
7
```

Formula matches.

---

# 16. C++17 — Sliding Window Cost

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

class MedianCostWindow {
    multiset<ll> low;
    multiset<ll> high;

    ll lowSum = 0;
    ll highSum = 0;

    void moveLowToHigh() {
        auto it = prev(low.end());
        ll value = *it;

        low.erase(it);
        lowSum -= value;

        high.insert(value);
        highSum += value;
    }

    void moveHighToLow() {
        auto it = high.begin();
        ll value = *it;

        high.erase(it);
        highSum -= value;

        low.insert(value);
        lowSum += value;
    }

    void balance() {
        while (low.size() > high.size() + 1) {
            moveLowToHigh();
        }

        while (high.size() > low.size()) {
            moveHighToLow();
        }
    }

public:
    void add(ll value) {
        if (low.empty() || value <= *low.rbegin()) {
            low.insert(value);
            lowSum += value;
        } else {
            high.insert(value);
            highSum += value;
        }

        balance();
    }

    void remove(ll value) {
        auto itLow = low.find(value);

        if (itLow != low.end()) {
            low.erase(itLow);
            lowSum -= value;
        } else {
            auto itHigh = high.find(value);

            if (itHigh != high.end()) {
                high.erase(itHigh);
                highSum -= value;
            }
        }

        balance();
    }

    ll cost() const {
        ll median = *low.rbegin();

        ll leftCost = ll(low.size()) * median - lowSum;
        ll rightCost = highSum - ll(high.size()) * median;

        return leftCost + rightCost;
    }
};

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, k;
    cin >> n >> k;

    vector<ll> arr(n);

    for (ll& value : arr) {
        cin >> value;
    }

    MedianCostWindow window;

    for (int head = 0; head < n; head++) {
        window.add(arr[head]);

        if (head >= k) {
            window.remove(arr[head - k]);
        }

        if (head >= k - 1) {
            cout << window.cost() << ' ';
        }
    }

    cout << '\n';

    return 0;
}
```

Complexity:

```text
Time  = O(N log K)
Space = O(K)
```

---

# 17. Problem 3 — CSES Maximum Subarray Sum II

Lecture problem:

```text
CSES — Maximum Subarray Sum II
```

The key lecture hint is:

```text
PREFIX SUM
```

The problem asks for a maximum-sum subarray whose length lies in:

```text
[A, B]
```

The lecture's main crux is to convert:

```text
a range of allowed subarray lengths
```

into:

```text
a sliding window of allowed prefix indices
```

---

# 18. Prerequisite 8 — Prefix Sum Subarray Formula

Define:

```text
prefix[0] = 0
```

and:

```text
prefix[i]
=
arr[0] + arr[1] + ... + arr[i-1]
```

Then sum of subarray:

```text
arr[L ... R-1]
```

is:

```text
prefix[R] - prefix[L]
```

Example:

```text
arr = [3,-2,5,1]
```

Prefix:

```text
prefix = [0,3,1,6,7]
```

Sum of:

```text
arr[1...3]
=
[-2,5,1]
```

is:

```text
prefix[4] - prefix[1]

=
7 - 3

=
4
```

---

# 19. Convert Length Range [A,B] into a Prefix-Index Window

Suppose current right prefix index is:

```text
R
```

A subarray starting at prefix index `L` has length:

```text
R - L
```

Required:

```text
A <= R-L <= B
```

Solve for `L`.

From:

```text
R-L >= A
```

we get:

```text
L <= R-A
```

From:

```text
R-L <= B
```

we get:

```text
L >= R-B
```

Therefore allowed `L` values are:

```text
R-B <= L <= R-A
```

This is the key transformation.

---

## Now Maximize the Sum

Subarray sum:

```text
prefix[R] - prefix[L]
```

For fixed `R`:

```text
prefix[R]
```

is constant.

So to maximize:

```text
prefix[R] - prefix[L]
```

we need the:

```text
minimum prefix[L]
```

among:

```text
L ∈ [R-B, R-A]
```

That is a fixed-size/controlled sliding window over prefix indices.

---

## Mental Model

```text
original problem:

choose subarray length in [A,B]
             |
             v
fix right endpoint R
             |
             v
valid left prefix indices:
[R-B ... R-A]
             |
             v
need minimum prefix in that range
             |
             v
answer candidate:
prefix[R] - minimumPrefix
```

---

# 20. Detailed Maximum-Subarray Dry Run

Teaching example:

```text
arr = [3,-2,5,-1,4]
A = 2
B = 3
```

Prefix:

```text
index:   0  1  2  3  4  5
prefix: [0, 3, 1, 6, 5, 9]
```

---

## R = 2

Allowed `L`:

```text
R-B <= L <= R-A

2-3 <= L <= 2-2

-1 <= L <= 0
```

Valid array index:

```text
L = 0
```

Candidate:

```text
prefix[2] - prefix[0]
=
1 - 0
=
1
```

Subarray:

```text
[3,-2]
```

---

## R = 3

Allowed:

```text
0 <= L <= 1
```

Prefix candidates:

```text
prefix[0] = 0
prefix[1] = 3
```

Minimum:

```text
0
```

Candidate:

```text
prefix[3] - 0
=
6
```

Subarray:

```text
[3,-2,5]
```

---

## R = 4

Allowed:

```text
1 <= L <= 2
```

Prefix candidates:

```text
3, 1
```

Minimum:

```text
1
```

Candidate:

```text
prefix[4] - 1
=
5 - 1
=
4
```

---

## R = 5

Allowed:

```text
2 <= L <= 3
```

Prefix candidates:

```text
1, 6
```

Minimum:

```text
1
```

Candidate:

```text
prefix[5] - 1
=
9 - 1
=
8
```

Subarray:

```text
[5,-1,4]
```

Final answer:

```text
8
```

---

# 21. C++17 — Maximum Subarray Sum II

> **Teaching completion of the lecture's final task:**  
> the lecture gives the prefix-sum crux and asks you to implement it.  
> The following implementation directly maintains the valid prefix-index window.

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, a, b;
    cin >> n >> a >> b;

    vector<ll> arr(n);
    vector<ll> prefix(n + 1, 0);

    for (int i = 0; i < n; i++) {
        cin >> arr[i];
        prefix[i + 1] = prefix[i] + arr[i];
    }

    multiset<ll> candidates;

    ll answer = LLONG_MIN;

    for (int right = a; right <= n; right++) {
        candidates.insert(prefix[right - a]);

        int expired = right - b - 1;

        if (expired >= 0) {
            auto it = candidates.find(prefix[expired]);

            if (it != candidates.end()) {
                candidates.erase(it);
            }
        }

        ll minimumPrefix = *candidates.begin();
        ll currentSum = prefix[right] - minimumPrefix;

        answer = max(answer, currentSum);
    }

    cout << answer << '\n';

    return 0;
}
```

Why insertion is:

```text
prefix[right-A]
```

Because it is the newest allowed left index.

Why expiration is:

```text
right-B-1
```

Because any index smaller than:

```text
right-B
```

would make the subarray longer than `B`.

Complexity:

```text
Time  = O(N log(B-A+1))
Space = O(B-A+1)
```

---

# 22. Complexity Comparison

| Problem | Core Structure | Query | Total |
|---|---|---|---:|
| Sliding Window Median — naive one multiset + walk | `multiset` | middle costs `O(K)` | too slow for large K |
| Sliding Window Median — two multisets | `low + high` | median `O(1)` | `O(N log K)` |
| Sliding Window Median — PBDS | ordered set of `(value,index)` | `find_by_order` | `O(N log K)` |
| Sliding Window Cost | two multisets + two sums | cost `O(1)` | `O(N log K)` |
| Maximum Subarray Sum II | prefix + multiset of allowed left prefixes | minimum `O(1)` | `O(N log range)` |

---

# 23. Common Mistakes

## Mistake 1 — Walk to the Middle of a Multiset for Every Window

This can add:

```text
O(K)
```

per window.

Instead maintain the median continuously.

---

## Mistake 2 — Use Priority Queue for Sliding Median

A normal `priority_queue` does not support easy arbitrary erase of the expired value.

The lecture explicitly motivates structures that support:

```text
insert
erase
middle/rank query
```

---

## Mistake 3 — Erase All Duplicate Values

Wrong:

```cpp
ms.erase(value);
```

For a multiset, erase one iterator instead.

---

## Mistake 4 — Break the Balance Invariant

Always restore:

```text
low.size() == high.size()
```

or:

```text
low.size() == high.size()+1
```

after both:

```text
insert
remove
```

---

## Mistake 5 — Use Mean Instead of Median for Absolute Cost

For:

```text
sum |x-target|
```

the minimizing target is a median.

Do not use arithmetic mean.

---

## Mistake 6 — Recompute Sliding Window Cost in O(K)

Once you maintain:

```text
lowSum
highSum
median
```

use:

```text
low.size()*median - lowSum
+
highSum - high.size()*median
```

---

## Mistake 7 — Get the Prefix Window Wrong in Maximum Subarray Sum II

For right prefix index `R`:

```text
allowed left indices
=
[R-B, R-A]
```

Not:

```text
[R-A, R-B]
```

and not array indices directly without accounting for prefix indexing.

---

## Mistake 8 — Forget `long long`

Use `long long` for:

```text
prefix sums
window sums
costs
```

because values can accumulate beyond 32-bit range.

---

# 24. Recognition Table

| Statement Signal | Main Idea |
|---|---|
| Median for every size-K window | Two multisets / PBDS |
| Need insert + arbitrary erase + middle | Two balanced multisets |
| Duplicates with ordered statistics | Store `(value,index)` |
| Minimum cost to make all values equal | Median |
| Repeated absolute-deviation cost | Median + left/right sums |
| Maximum subarray with length in `[A,B]` | Prefix sums |
| Fix right endpoint and allowed lengths | Window over valid left-prefix indices |
| Maximize `prefix[R]-prefix[L]` | Minimize valid `prefix[L]` |

---

# 25. Final Mental Model

The lecture can be reduced to three reusable transformations.

---

## Pattern 1 — Sliding Median

```text
Need middle of dynamic fixed-size window
                |
                v
         split into halves
                |
        +-------+-------+
        |               |
        v               v
       low             high
 smaller half      larger half
        |
        v
median lives at boundary
```

Remember:

```text
low size == high size
or
low size == high size + 1
```

---

## Pattern 2 — Sliding Cost

```text
make all values equal
        |
        v
      MEDIAN
        |
        v
two halves + their sums
        |
        v
cost in O(1)
```

Formula:

```text
low.size()*median - lowSum
+
highSum - high.size()*median
```

---

## Pattern 3 — Bounded-Length Maximum Subarray

```text
subarray length in [A,B]
        |
        v
prefix[R] - prefix[L]
        |
        v
for fixed R:

L ∈ [R-B, R-A]
        |
        v
need minimum prefix[L]
        |
        v
sliding window over prefix indices
```

---

## 60-Second Recognition

```text
Fixed-size window + MEDIAN
→ two multisets

Fixed-size window + absolute cost
→ median + sums

Subarray length constrained to [A,B]
→ prefix sums + window of valid prefix indices
```

The broader lesson is:

```text
Sliding Window is not only about moving pointers.

The real skill is deciding
WHAT STATE THE WINDOW MUST MAINTAIN
so ADD, REMOVE, and QUERY stay efficient.
```
