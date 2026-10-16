# TLE Eliminators — Sliding Windows Introduction
## Fixed-Size Sliding Window, Maps, Queues, and Monotonic Deques
### TLE-Style Self-Study Notes

> **Source:** supplied TLE Eliminators PDF — *Sliding Windows Introduction*.
>
> The lecture introduces two broad categories:
>
> ```text
> Sliding Windows
> │
> ├── Fixed-size sliding windows
> └── Dynamic-size sliding windows
> ```
>
> This lecture mainly develops the **fixed-size** form:
>
> ```text
> every considered subarray has exactly K elements
> ```
>
> Core idea:
>
> ```text
> consecutive windows overlap heavily
> → do not rebuild the whole window
> → add the entering element
> → remove the expired element
> → reuse the maintained state
> ```

---

# Table of Contents

- [1. What Is a Sliding Window?](#1-what-is-a-sliding-window)
- [2. Prerequisite 1 — Subarray vs Subsequence](#2-prerequisite-1--subarray-vs-subsequence)
- [3. Prerequisite 2 — Fixed Window Mathematics](#3-prerequisite-2--fixed-window-mathematics)
- [4. Prerequisite 3 — The Add / Remove Invariant](#4-prerequisite-3--the-add--remove-invariant)
- [5. Prerequisite 4 — Choosing the Window Data Structure](#5-prerequisite-4--choosing-the-window-data-structure)
- [6. Prerequisite 5 — Map and Duplicate Frequencies](#6-prerequisite-5--map-and-duplicate-frequencies)
- [7. Prerequisite 6 — `set<pair<value,index>>`](#7-prerequisite-6--setpairvalueindex)
- [8. Prerequisite 7 — Queue of Useful Indices](#8-prerequisite-7--queue-of-useful-indices)
- [9. Prerequisite 8 — Monotonic Deque](#9-prerequisite-8--monotonic-deque)
- [10. Master Fixed-Window Template](#10-master-fixed-window-template)
- [11. Problem 1 — Maximum Sum of a Subarray of Size K](#11-problem-1--maximum-sum-of-a-subarray-of-size-k)
- [12. Problem 2 — Maximum Distinct Elements in a Window of Size K](#12-problem-2--maximum-distinct-elements-in-a-window-of-size-k)
- [13. Problem 3 — Maximum Sum of a Size-K Window with All Distinct Elements](#13-problem-3--maximum-sum-of-a-size-k-window-with-all-distinct-elements)
- [14. Queue / Deque Optimization Idea](#14-queue--deque-optimization-idea)
- [15. Lecture Problem 5 — Index of the Leftmost Minimum in Every Window](#15-lecture-problem-5--index-of-the-leftmost-minimum-in-every-window)
- [16. O(N log K) Solution — Ordered Set](#16-on-log-k-solution--ordered-set)
- [17. O(N) Solution — Monotonic Deque](#17-on-solution--monotonic-deque)
- [18. Lecture Problem 4 — First Negative Element in Every Window](#18-lecture-problem-4--first-negative-element-in-every-window)
- [19. Complexity Comparison](#19-complexity-comparison)
- [20. Common Mistakes](#20-common-mistakes)
- [21. Recognition Table](#21-recognition-table)
- [22. Final Mental Model](#22-final-mental-model)

---

# 1. What Is a Sliding Window?

Sliding Window is an optimization technique for array-based problems where we repeatedly process contiguous ranges.

The lecture's key recognition signal is:

```text
"for every fixed-length subarray"
```

or:

```text
"subarray of size K"
```

Instead of solving each window independently:

```text
window 1 → recompute everything
window 2 → recompute everything
window 3 → recompute everything
```

we reuse the overlap:

```text
old window
[ a b c d ]

next window
  [ b c d e ]

only:
a leaves
e enters
```

That is the entire idea.

---

# 2. Prerequisite 1 — Subarray vs Subsequence

A **subarray** is contiguous.

Example:

```text
arr = [1,4,2,10,23]
```

Valid subarrays:

```text
[1,4,2]
[4,2,10]
[2,10,23]
```

Not a subarray:

```text
[1,2,23]
```

because positions were skipped.

Sliding Window is naturally about:

```text
contiguous ranges
```

---

# 3. Prerequisite 2 — Fixed Window Mathematics

Suppose:

```text
window size = K
head = rightmost index
```

Then:

```text
start = head - K + 1
```

and the index that just expired is:

```text
expired = head - K
```

Example:

```text
K = 4
head = 6
```

Then:

```text
start
=
6 - 4 + 1
=
3
```

Window:

```text
[3,4,5,6]
```

Expired index:

```text
6 - 4
=
2
```

ASCII:

```text
index:  0   1   2   3   4   5   6
              X   [-----------]
              |    current K
           expired

start = 3
head  = 6
```

---

## When Is the First Window Ready?

With zero-based indexing:

```text
first complete window appears at head = K-1
```

Example:

```text
K = 3

head=0 → 1 element
head=1 → 2 elements
head=2 → 3 elements → READY
```

So:

```cpp
if (head >= k - 1) {
    processWindow();
}
```

---

# 4. Prerequisite 3 — The Add / Remove Invariant

For every `head`, maintain information for the current window.

General lifecycle:

```text
1. ADD arr[head]

2. REMOVE arr[head-K] if it exists

3. PROCESS once K elements are present
```

Example with a sum:

```text
arr = [1,2,6,2,4,1]
K = 3
```

First window:

```text
[1,2,6]
sum = 9
```

Next:

```text
[2,6,2]
```

Do not recompute:

```text
2 + 6 + 2
```

Instead:

```text
newSum
=
oldSum
- leaving
+ entering

=
9 - 1 + 2
=
10
```

Next:

```text
[6,2,4]

newSum
=
10 - 2 + 4
=
12
```

So each shift needs only:

```text
O(1)
```

state updates when the maintained information supports it.

---

# 5. Prerequisite 4 — Choosing the Window Data Structure

The window pattern is the same, but the maintained data structure depends on the question.

| Need from each window | Useful state/data structure |
|---|---|
| Sum | `long long windowSum` |
| Count of a property | integer counter |
| Distinct elements | frequency `map` |
| First negative | `queue<int>` of indices |
| Minimum / maximum | ordered `set` or monotonic `deque` |
| Leftmost minimum index | `set<pair<value,index>>` or carefully designed deque |

The window movement does not fundamentally change.

Only:

```text
ADD
REMOVE
QUERY
```

change.

---

# 6. Prerequisite 5 — Map and Duplicate Frequencies

The lecture uses a `map` when duplicates matter.

Suppose window is:

```text
[2,2,2,1]
```

A plain `set` contains only:

```text
{1,2}
```

That tells us the distinct values, but it does not tell us how many `2`s remain when one leaves.

A frequency map stores:

```text
1 → 1
2 → 3
```

When an element enters:

```cpp
frequency[value]++;
```

When it leaves:

```cpp
frequency[value]--;
```

If frequency becomes zero:

```cpp
frequency.erase(value);
```

Then:

```cpp
frequency.size()
```

is exactly the number of distinct values currently in the window.

---

## Tiny Example

Window:

```text
[1,1,2,1]
```

Frequency:

```text
1 → 3
2 → 1
```

Distinct:

```text
2
```

Suppose the leftmost `1` leaves:

```text
1 → 2
2 → 1
```

Distinct is still:

```text
2
```

This is why frequency, not just membership, matters.

---

## Complexity

The lecture analyzes ordered `map` operations as:

```text
O(log K)
```

because a fixed window contains at most `K` distinct keys.

Across `N` positions:

```text
O(N log K)
```

The lecture also notes that `unordered_map` can suffer from hash collisions in competitive-programming settings, so it presents ordered `map` as the predictable option.

---

# 7. Prerequisite 6 — `set<pair<value,index>>`

C++ ordered pairs are compared lexicographically:

```text
first compare .first
if equal, compare .second
```

Example:

```text
(1,2)
(1,5)
(2,1)
```

Sorted order:

```text
(1,2)
(1,5)
(2,1)
```

So with:

```cpp
set<pair<int, int>> window;
```

where each pair is:

```text
(value, index)
```

then:

```cpp
*window.begin()
```

gives:

```text
smallest value
```

and among equal smallest values:

```text
smallest index
```

That is exactly useful for:

```text
leftmost minimum
```

---

# 8. Prerequisite 7 — Queue of Useful Indices

The lecture observes:

```text
elements enter the window in order
and expire in the same order
```

That is FIFO behavior.

Therefore a queue can be useful when we only need a special subset of indices.

Example:

```text
find the first negative element
in every size-K window
```

Instead of storing every value, store only:

```text
indices where arr[i] < 0
```

Example:

```text
arr = [1,-2,4,-5,6]
```

Queue of negative indices:

```text
[1,3]
```

For each window:

```text
front()
```

is the earliest negative still inside the window.

Expired negative indices leave from the front.

---

# 9. Prerequisite 8 — Monotonic Deque

For every-window minimum, a normal queue is not enough.

We want to discard values that can never become minimum.

Suppose:

```text
... 4 ... 2 ...
```

and `2` appears later.

As long as `2` is in the window:

```text
4 can never be minimum
```

because:

```text
2 < 4
```

and `2` expires later.

So `4` is dominated and can be removed.

A monotonic deque stores only useful candidate indices.

For minimum:

```text
values represented by deque
are maintained in increasing order
```

Then:

```cpp
arr[dq.front()]
```

is the minimum.

---

## Why Store Indices?

Because we must know when a candidate expires.

Current window:

```text
[head-K+1, head]
```

An index is expired if:

```text
index <= head-K
```

Values alone cannot tell us this.

---

## Amortized O(N)

Every index is:

```text
pushed once
popped at most once
```

So total deque work is:

```text
O(N)
```

even though the code contains `while` loops.

---

# 10. Master Fixed-Window Template

This is the compact template to memorize:

```cpp
for (int head = 0; head < n; head++) {
    add(arr[head]);

    if (head >= k) {
        remove(arr[head - k]);
    }

    if (head >= k - 1) {
        processWindow();
    }
}
```

Mental model:

```text
HEAD moves one step
       |
       v
ADD new element
       |
       v
REMOVE expired element
       |
       v
window complete?
       |
      YES
       |
       v
PROCESS
```

This pattern stays almost unchanged across the lecture problems.

---

# 11. Problem 1 — Maximum Sum of a Subarray of Size K

## What It Asks

Find:

```text
maximum sum
```

among all contiguous windows of exactly:

```text
K
```

elements.

Lecture example:

```text
arr = [1,4,2,10,23,3,1,0,20]
K = 4
```

Expected:

```text
39
```

from:

```text
[4,2,10,23]
```

because:

```text
4 + 2 + 10 + 23 = 39
```

---

## Brute Force

For every window:

```text
sum K elements
```

Cost:

```text
O(NK)
```

---

## Sliding Window Observation

Consecutive windows differ by exactly:

```text
one removed element
one added element
```

So maintain:

```text
windowSum
```

Update:

```text
windowSum += arr[head]
windowSum -= arr[head-K]
```

---

## Compact Dry Run

```text
arr = [1,4,2,10,23]
K = 4
```

```text
head=0 → sum=1               not ready
head=1 → sum=5               not ready
head=2 → sum=7               not ready
head=3 → sum=17              first window → ans=17
head=4 → add23, remove1
       → sum=39              ans=39
```

---

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

long long maxSumOfSizeK(const vector<int>& arr, int k) {
    int n = arr.size();

    long long windowSum = 0;
    long long answer = LLONG_MIN;

    for (int head = 0; head < n; head++) {
        windowSum += arr[head];

        if (head >= k) {
            windowSum -= arr[head - k];
        }

        if (head >= k - 1) {
            answer = max(answer, windowSum);
        }
    }

    return answer;
}
```

Complexity:

```text
Time  = O(N)
Space = O(1)
```

---

# 12. Problem 2 — Maximum Distinct Elements in a Window of Size K

## What It Asks

Find the maximum number of distinct values among all windows of size `K`.

Lecture example:

```text
arr = [1,1,2,1,3,2,1,1,2]
K = 4
```

Expected:

```text
3
```

One such window:

```text
[1,3,2,1]
```

Distinct values:

```text
{1,2,3}
```

Count:

```text
3
```

---

## Why a Plain Set Is Awkward

Suppose:

```text
window = [2,2,2,1]
```

If one `2` leaves, `2` must still remain in the structure.

A frequency map naturally handles this.

---

## Invariant

```text
frequency
=
counts of values inside current window
```

Therefore:

```text
frequency.size()
=
number of distinct values
```

---

## Compact Dry Run

```text
arr = [1,1,2,1,3]
K = 4
```

First complete window:

```text
[1,1,2,1]

frequency:
1 → 3
2 → 1

distinct = 2
```

Slide:

```text
remove old 1:
1 → 2

add 3:
3 → 1
```

New:

```text
[1,2,1,3]

keys = {1,2,3}
distinct = 3
```

---

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxDistinctInSizeK(const vector<int>& arr, int k) {
    int n = arr.size();

    map<int, int> frequency;
    int answer = 0;

    for (int head = 0; head < n; head++) {
        frequency[arr[head]]++;

        if (head >= k) {
            int expired = arr[head - k];

            frequency[expired]--;

            if (frequency[expired] == 0) {
                frequency.erase(expired);
            }
        }

        if (head >= k - 1) {
            answer = max(answer, int(frequency.size()));
        }
    }

    return answer;
}
```

Complexity:

```text
Time  = O(N log K)
Space = O(K)
```

---

# 13. Problem 3 — Maximum Sum of a Size-K Window with All Distinct Elements

## What It Asks

Among all size-`K` windows:

```text
all K elements must be distinct
```

and among those valid windows:

```text
maximize the sum
```

Lecture example:

```text
arr = [1,1,2,1,3,2,1,1,2]
K = 3
```

Expected:

```text
6
```

from:

```text
[1,3,2]
```

because:

```text
1 + 3 + 2 = 6
```

and all three values are distinct.

---

## Maintained State

We need both:

```text
windowSum
frequency map
```

Validity for a complete size-`K` window:

```text
frequency.size() == K
```

Why?

The window already contains exactly:

```text
K elements
```

If there are also:

```text
K distinct keys
```

then every element must be unique.

---

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

long long maxDistinctWindowSum(const vector<int>& arr, int k) {
    int n = arr.size();

    map<int, int> frequency;
    long long windowSum = 0;
    long long answer = LLONG_MIN;

    for (int head = 0; head < n; head++) {
        windowSum += arr[head];
        frequency[arr[head]]++;

        if (head >= k) {
            int expired = arr[head - k];

            windowSum -= expired;
            frequency[expired]--;

            if (frequency[expired] == 0) {
                frequency.erase(expired);
            }
        }

        if (head >= k - 1 && int(frequency.size()) == k) {
            answer = max(answer, windowSum);
        }
    }

    return answer;
}
```

Complexity:

```text
Time  = O(N log K)
Space = O(K)
```

---

# 14. Queue / Deque Optimization Idea

The lecture then asks:

```text
Can some O(N log K) window structures
be replaced by queues/deques to reach O(N)?
```

The important observation is:

```text
oldest elements expire first
```

So if the query only needs a carefully chosen subset of candidates, we may not need a fully ordered set.

Examples:

```text
first negative
→ queue of negative indices

minimum / maximum
→ monotonic deque
```

This is a major Sliding Window pattern:

```text
store only useful indices
```

instead of storing every element in a heavy ordered structure.

---

# 15. Lecture Problem 5 — Index of the Leftmost Minimum in Every Window

## What It Asks

For every size-`K` window:

```text
find the index of the minimum element
```

The lecture specifically annotates:

```text
leftmost minimum
```

Lecture example:

```text
arr = [1,3,1,2,4,5,1,7,2]
K = 3
```

Using **1-based indices**, expected output is:

```text
1 3 3 4 7 7 7
```

---

## Example

First window:

```text
[1,3,1]
```

Minimum value:

```text
1
```

It occurs twice:

```text
index 1
index 3
```

Need:

```text
leftmost
```

so answer:

```text
1
```

---

# 16. O(N log K) Solution — Ordered Set

Use:

```cpp
set<pair<int, int>>
```

Store:

```text
(value, index)
```

Because pairs are sorted lexicographically:

```cpp
window.begin()
```

gives:

```text
minimum value
+
leftmost index among ties
```

---

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> leftmostMinimumIndicesSet(const vector<int>& arr, int k) {
    int n = arr.size();

    set<pair<int, int>> window;
    vector<int> answer;

    for (int head = 0; head < n; head++) {
        window.insert({arr[head], head});

        if (head >= k) {
            int expiredIndex = head - k;
            window.erase({arr[expiredIndex], expiredIndex});
        }

        if (head >= k - 1) {
            answer.push_back(window.begin()->second + 1);
        }
    }

    return answer;
}
```

`+1` converts the zero-based C++ index to the lecture's one-based output.

Complexity:

```text
Time  = O(N log K)
Space = O(K)
```

---

# 17. O(N) Solution — Monotonic Deque

Store candidate indices in increasing-value order.

Core operations:

```text
1. remove expired front indices
2. remove dominated back indices
3. push current index
4. front is minimum
```

---

## Tie Handling for "Leftmost Minimum"

For a normal minimum-value problem, many implementations remove while:

```cpp
arr[dq.back()] >= arr[head]
```

That discards an older equal value and keeps the newer one.

But this problem asks for the:

```text
leftmost minimum index
```

So for equal values, we should preserve the older index.

Therefore use:

```cpp
arr[dq.back()] > arr[head]
```

not:

```cpp
arr[dq.back()] >= arr[head]
```

This is an explicit tie-handling detail for the leftmost-index requirement.

---

## Tiny Tie Example

```text
arr = [1,3,1]
K = 3
```

When the second `1` arrives:

```text
old candidate = index 0, value 1
new candidate = index 2, value 1
```

If we pop on equality:

```text
index 0 disappears
```

and we incorrectly return the newer index.

So preserve equal values:

```text
pop only if old value > new value
```

---

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> leftmostMinimumIndicesDeque(const vector<int>& arr, int k) {
    int n = arr.size();

    deque<int> dq;
    vector<int> answer;

    for (int head = 0; head < n; head++) {
        while (!dq.empty() && dq.front() <= head - k) {
            dq.pop_front();
        }

        while (!dq.empty() && arr[dq.back()] > arr[head]) {
            dq.pop_back();
        }

        dq.push_back(head);

        if (head >= k - 1) {
            answer.push_back(dq.front() + 1);
        }
    }

    return answer;
}
```

Complexity:

```text
Time  = O(N)
Space = O(K)
```

---

## Why O(N)?

Each index:

```text
enters deque once
leaves deque once
```

So total deque operations are bounded by roughly:

```text
2N
```

Hence:

```text
O(N)
```

---

# 18. Lecture Problem 4 — First Negative Element in Every Window

## What It Asks

For every size-`K` subarray:

```text
find the index of the first negative element
```

Lecture example:

```text
arr = [1,1,2,-1,3,2,-3,-4,2]
K = 3
```

Using the lecture's **1-based indices**, output is:

```text
-1 4 4 4 7 7 7
```

Here:

```text
-1
```

means:

```text
no negative value in that window
```

---

## Key Observation

We do not need to store every element.

Store only:

```text
indices of negative elements
```

in a queue.

Because indices arrive in increasing order:

```text
q.front()
```

is always the earliest negative candidate.

---

## Expiry

An index is expired when:

```text
index <= head-K
```

So:

```cpp
while (!negativeIndices.empty() &&
       negativeIndices.front() <= head - k) {
    negativeIndices.pop();
}
```

---

## Compact Dry Run

```text
arr = [1,1,2,-1,3]
K = 3
```

First window:

```text
[1,1,2]

queue = []
answer = -1
```

Next window:

```text
[1,2,-1]
```

When `-1` at index `3` arrives:

```text
queue = [3]
```

Lecture output uses one-based index:

```text
4
```

Next window:

```text
[2,-1,3]
```

Index `3` is still inside.

So answer remains:

```text
4
```

---

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> firstNegativeIndices(const vector<int>& arr, int k) {
    int n = arr.size();

    queue<int> negativeIndices;
    vector<int> answer;

    for (int head = 0; head < n; head++) {
        if (arr[head] < 0) {
            negativeIndices.push(head);
        }

        while (!negativeIndices.empty() &&
               negativeIndices.front() <= head - k) {
            negativeIndices.pop();
        }

        if (head >= k - 1) {
            if (negativeIndices.empty()) {
                answer.push_back(-1);
            } else {
                answer.push_back(negativeIndices.front() + 1);
            }
        }
    }

    return answer;
}
```

Complexity:

```text
Time  = O(N)
Space = O(K)
```

---

# 19. Complexity Comparison

| Problem | Maintained State | Time |
|---|---|---:|
| Maximum sum of size `K` | running sum | `O(N)` |
| Maximum distinct count | ordered frequency map | `O(N log K)` |
| Max sum with all distinct | sum + ordered map | `O(N log K)` |
| Leftmost minimum index — set | `set<pair<value,index>>` | `O(N log K)` |
| Leftmost minimum index — deque | monotonic deque | `O(N)` |
| First negative index | queue of negative indices | `O(N)` |

---

# 20. Common Mistakes

## Mistake 1 — Recomputing Every Window

Wrong idea:

```text
for each start:
    scan K elements again
```

That gives:

```text
O(NK)
```

Instead:

```text
ADD entering
REMOVE expired
```

---

## Mistake 2 — Removing `head-K+1`

That index is still inside the current window.

Correct:

```text
start   = head-K+1
expired = head-K
```

---

## Mistake 3 — Processing Before K Elements Exist

First complete window:

```text
head = K-1
```

Use:

```cpp
if (head >= k - 1) {
    ...
}
```

---

## Mistake 4 — Keeping Zero-Frequency Keys in the Map

If:

```text
frequency[x] == 0
```

but `x` remains in the map, then:

```cpp
frequency.size()
```

is no longer the number of distinct values.

Erase the key.

---

## Mistake 5 — Using Only a Set for Duplicate-Sensitive Removal

A set tracks membership, not frequency.

When duplicates matter, use:

```text
frequency map
```

or a structure storing `(value,index)`.

---

## Mistake 6 — Storing Values Instead of Indices in Queue/Deque

Window expiry is positional.

Indices let us test:

```text
index <= head-K
```

---

## Mistake 7 — Wrong Tie Rule for Leftmost Minimum

If you need the leftmost minimum:

```text
do not discard older equal minima
```

Use:

```cpp
arr[dq.back()] > arr[head]
```

for back removal.

---

# 21. Recognition Table

| Statement Signal | Think |
|---|---|
| "every subarray of size K" | Fixed Sliding Window |
| "maximum sum of K consecutive" | Running sum |
| "number of distinct in every K" | Frequency map |
| "all K elements distinct" | `frequency.size() == K` |
| "first negative in each K-window" | Queue of special indices |
| "minimum/maximum in every K-window" | Monotonic deque |
| "minimum value + leftmost index" | `set<pair<value,index>>` or tie-aware deque |

---

# 22. Final Mental Model

The lecture's fixed-size Sliding Window can be reduced to one skeleton:

```text
               FIXED WINDOW OF SIZE K
                       |
                       v
                HEAD moves right
                       |
                       v
                  ADD current
                       |
                       v
                REMOVE expired
                index = head-K
                       |
                       v
                 window ready?
                head >= K-1
                       |
                       v
                    QUERY
```

Code:

```cpp
for (int head = 0; head < n; head++) {
    add(arr[head]);

    if (head >= k) {
        remove(arr[head - k]);
    }

    if (head >= k - 1) {
        processWindow();
    }
}
```

Then choose the state based on the query:

```text
SUM
→ running variable

DISTINCT
→ frequency map

FIRST SPECIAL ELEMENT
→ queue of indices

MIN / MAX
→ monotonic deque
```

### 60-Second Recognition

```text
"Every contiguous subarray of exact size K"

            ↓

FIXED SLIDING WINDOW

            ↓

ADD entering
REMOVE expired
QUERY current window
```
