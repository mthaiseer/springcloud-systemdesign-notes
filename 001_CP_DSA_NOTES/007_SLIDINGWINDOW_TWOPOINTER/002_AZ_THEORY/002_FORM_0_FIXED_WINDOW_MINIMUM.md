# Two Pointers — Form 0: Fixed-Size Sliding Window
## Sliding Window Minimum with Multiset and Monotonic Deque
### TLE-Style Self-Study Notes

> **Source:** supplied Form 0 lecture/text.
>
> Form 0 processes every contiguous subarray of an exact size `K`.
>
> The key simplification is:
>
> ```text
> only HEAD moves
> start = head - K + 1
> expired index = head - K
> ```
>
> We will solve:
>
> ```text
> Minimum in Every Subarray of Size K
> ```
>
> using:
>
> ```text
> 1. multiset
> 2. monotonic deque
> ```

---

# Table of Contents

- [1. What Form 0 Means](#1-what-form-0-means)
- [2. Prerequisite 1 — Contiguous Subarray of Fixed Size](#2-prerequisite-1--contiguous-subarray-of-fixed-size)
- [3. Prerequisite 2 — Window Index Mathematics](#3-prerequisite-2--window-index-mathematics)
- [4. Prerequisite 3 — Reuse Previous Work](#4-prerequisite-3--reuse-previous-work)
- [5. Prerequisite 4 — Form 0 Invariant](#5-prerequisite-4--form-0-invariant)
- [6. Prerequisite 5 — Multiset Basics](#6-prerequisite-5--multiset-basics)
- [7. Prerequisite 6 — Deque Basics](#7-prerequisite-6--deque-basics)
- [8. Prerequisite 7 — Monotonic Deque Idea](#8-prerequisite-7--monotonic-deque-idea)
- [9. Prerequisite 8 — Amortized O(N)](#9-prerequisite-8--amortized-on)
- [10. Problem — Minimum in Every Subarray of Size K](#10-problem--minimum-in-every-subarray-of-size-k)
- [11. Approach 1 — Multiset](#11-approach-1--multiset)
- [12. Detailed Multiset Dry Run](#12-detailed-multiset-dry-run)
- [13. C++17 — Multiset Solution](#13-c17--multiset-solution)
- [14. Approach 2 — Monotonic Deque](#14-approach-2--monotonic-deque)
- [15. Detailed Deque Dry Run](#15-detailed-deque-dry-run)
- [16. Why the Deque Is Correct](#16-why-the-deque-is-correct)
- [17. C++17 — Monotonic Deque Solution](#17-c17--monotonic-deque-solution)
- [18. Multiset vs Monotonic Deque](#18-multiset-vs-monotonic-deque)
- [19. Common Mistakes](#19-common-mistakes)
- [20. Recognition Pattern](#20-recognition-pattern)
- [21. Final Mental Model](#21-final-mental-model)

---

# 1. What Form 0 Means

Form 0 is the simplest Two Pointer / Sliding Window form.

We process every contiguous subarray of exactly:

```text
K elements
```

The window size never changes.

Therefore we do **not** need two independent moving pointers.

We use:

```text
head = rightmost element of current window
```

The left boundary is calculated:

```text
start = head - K + 1
```

So Form 0 is effectively:

```text
one moving pointer
+
one derived boundary
```

ASCII:

```text
arr:

[ 3 ][ 1 ][ 4 ][ 2 ][ 8 ][ 6 ][ 5 ]
            ^-----------^
            start      head

K = 3
```

---

# 2. Prerequisite 1 — Contiguous Subarray of Fixed Size

A contiguous subarray uses consecutive positions.

Example:

```text
arr = [3,1,4,2,8]
K = 3
```

All fixed windows are:

```text
[3,1,4]
  [1,4,2]
    [4,2,8]
```

They overlap heavily.

This overlap is exactly why Sliding Window works.

---

## Number of Windows

For:

```text
N elements
window size = K
```

the number of complete windows is:

```text
N - K + 1
```

Example:

```text
N = 8
K = 3
```

Then:

```text
8 - 3 + 1 = 6 windows
```

---

# 3. Prerequisite 2 — Window Index Mathematics

Suppose `head` is the rightmost index.

For window size `K`:

```text
start = head - K + 1
```

Example:

```text
head = 4
K = 3
```

Then:

```text
start
=
4 - 3 + 1
=
2
```

Current window:

```text
[2,3,4]
```

ASCII:

```text
index:  0   1   2   3   4   5
arr:   [a] [b] [c] [d] [e] [f]
             ^-------^
            start   head
```

---

## Expired Index

The first index **outside** the current window is:

```text
head - K
```

Example:

```text
head = 4
K = 3
```

Then:

```text
expired = 4 - 3 = 1
```

ASCII:

```text
index:     0   1   2   3   4
               X   [-------]
               |    current
            expired
```

Important distinction:

```text
head - K + 1
=
first index inside current window

head - K
=
index that just expired
```

---

# 4. Prerequisite 3 — Reuse Previous Work

Suppose current window is:

```text
[a[i], a[i+1], ..., a[i+K-1]]
```

Next window is:

```text
[a[i+1], a[i+2], ..., a[i+K]]
```

Only two things changed:

```text
a[i]   left
a[i+K] entered
```

ASCII:

```text
old:
[ a ][ b ][ c ]

new:
      [ b ][ c ][ d ]

change:
remove a
add d
```

So instead of rebuilding the whole window:

```text
remove expired element
add new element
```

This is the core fixed-window idea.

---

# 5. Prerequisite 4 — Form 0 Invariant

At every `head`:

```text
the maintained data structure
represents exactly the active window
```

Once the first complete window is formed:

```text
window = [head-K+1, head]
```

The three actions are always:

```text
1. Add arr[head]

2. If head-K >= 0:
       remove arr[head-K]

3. If head >= K-1:
       process current window
```

Generic template:

```cpp
for (int head = 0; head < n; head++) {
    add(arr[head]);

    if (head - k >= 0) {
        remove(arr[head - k]);
    }

    if (head >= k - 1) {
        processCurrentWindow();
    }
}
```

---

## Why First Answer Appears at `head = K-1`

Example:

```text
K = 3
```

Indices seen:

```text
head = 0 → 1 element
head = 1 → 2 elements
head = 2 → 3 elements
```

So first complete window appears at:

```text
head = K - 1
```

---

# 6. Prerequisite 5 — Multiset Basics

A `multiset` stores values in sorted order and allows duplicates.

Example:

```text
insert:
4, 2, 8, 2
```

Stored conceptually as:

```text
[2,2,4,8]
```

Minimum is:

```cpp
*window.begin()
```

because `begin()` points to the smallest value.

---

## Why Not `set`?

A normal `set` removes duplicates.

But a window may contain:

```text
[2,2,5]
```

Both `2`s matter.

So we need:

```text
multiset
```

---

## Important Duplicate-Erasure Rule

This is dangerous:

```cpp
window.erase(value);
```

For a `multiset`, that removes **all occurrences** of `value`.

Example:

```text
window = [2,2,5]
```

Doing:

```cpp
window.erase(2);
```

can remove both `2`s.

But only one element may have expired.

Correct:

```cpp
auto it = window.find(value);

if (it != window.end()) {
    window.erase(it);
}
```

That erases exactly one occurrence.

---

# 7. Prerequisite 6 — Deque Basics

A `deque` allows operations from both ends.

Useful operations:

```cpp
dq.push_back(x);

dq.pop_back();

dq.pop_front();

dq.front();

dq.back();
```

In Sliding Window Minimum, the deque stores:

```text
indices
```

not values.

Why indices?

Because expiry depends on position.

We need to know whether:

```text
index <= head - K
```

A value alone cannot tell us that.

---

# 8. Prerequisite 7 — Monotonic Deque Idea

The multiset stores **every** element in the current window.

The monotonic deque stores only:

```text
useful candidates for minimum
```

Suppose we currently have:

```text
4, 2
```

and `2` appears after `4`.

As long as `2` remains in the window:

```text
4 can never become minimum
```

Why?

```text
2 < 4
```

and:

```text
2 is newer
→ it expires later
```

So `4` is permanently dominated.

Discard it.

---

## Dominated Candidate Example

Before inserting `2`:

```text
deque values:
[4]
```

Insert:

```text
2
```

Since:

```text
4 >= 2
```

remove `4`.

Deque becomes:

```text
[2]
```

---

## Deque Invariants

For Sliding Window Minimum:

```text
1. indices are increasing

2. corresponding values are non-decreasing

3. every stored index is inside current window
```

Therefore:

```text
arr[dq.front()]
```

is always the current minimum.

---

## ASCII

```text
indices:     1      3      5
values:      1      2      5
             ^             ^
           front          back

minimum = arr[dq.front()] = 1
```

---

# 9. Prerequisite 8 — Amortized O(N)

The deque code contains `while` loops:

```cpp
while (...) {
    dq.pop_back();
}
```

It may look like:

```text
O(N²)
```

But consider one index.

It is:

```text
inserted once
removed at most once
```

An index can disappear because:

```text
1. it expired
or
2. a newer element dominated it
```

After removal, it never returns.

Across the whole array:

```text
at most N pushes
+
at most N pops
```

So:

```text
O(N)
```

This is amortized analysis.

---

# 10. Problem — Minimum in Every Subarray of Size K

## What It Asks

Given:

```text
arr of size N
K
```

for every contiguous window of exactly `K` elements:

```text
find its minimum
```

---

## What Result Is Expected?

Example:

```text
arr = [3,1,4,2,8,6,5,7]
K = 3
```

Windows:

| Window | Elements | Minimum |
|---|---|---:|
| 1 | `[3,1,4]` | `1` |
| 2 | `[1,4,2]` | `1` |
| 3 | `[4,2,8]` | `2` |
| 4 | `[2,8,6]` | `2` |
| 5 | `[8,6,5]` | `5` |
| 6 | `[6,5,7]` | `5` |

Expected output:

```text
1 1 2 2 5 5
```

---

## Why Brute Force Is Wasteful

For each window, scanning all `K` values costs:

```text
O(K)
```

There are approximately:

```text
N windows
```

So:

```text
O(NK)
```

We want to reuse the overlap.

---

# 11. Approach 1 — Multiset

Maintain all values currently inside the window.

For each `head`:

```text
1. insert arr[head]

2. remove arr[head-K] if expired

3. if window complete:
       minimum = *window.begin()
```

Because the multiset is sorted:

```text
minimum lookup = O(1)
```

Insert and erase:

```text
O(log K)
```

because at most `K` values are stored.

Overall:

```text
O(N log K)
```

---

# 12. Detailed Multiset Dry Run

Use:

```text
arr = [3,1,4,2,8]
K = 3
```

---

## head = 0

Add:

```text
3
```

Multiset:

```text
[3]
```

Not enough elements yet.

---

## head = 1

Add:

```text
1
```

Multiset:

```text
[1,3]
```

Still incomplete.

---

## head = 2

Add:

```text
4
```

Multiset:

```text
[1,3,4]
```

First complete window:

```text
[3,1,4]
```

Minimum:

```text
1
```

---

## head = 3

Add:

```text
2
```

Temporary multiset:

```text
[1,2,3,4]
```

Expired index:

```text
head-K
=
3-3
=
0
```

Expired value:

```text
arr[0] = 3
```

Erase one `3`:

```text
[1,2,4]
```

Current window:

```text
[1,4,2]
```

Minimum:

```text
1
```

---

## head = 4

Add:

```text
8
```

Temporary:

```text
[1,2,4,8]
```

Expired index:

```text
4-3 = 1
```

Expired value:

```text
arr[1] = 1
```

Remove it:

```text
[2,4,8]
```

Current window:

```text
[4,2,8]
```

Minimum:

```text
2
```

Compact trace:

```text
head  add  expired  multiset      min
0     3    -        [3]           -
1     1    -        [1,3]         -
2     4    -        [1,3,4]       1
3     2    3        [1,2,4]       1
4     8    1        [2,4,8]       2
```

---

# 13. C++17 — Multiset Solution

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> minimumsUsingMultiset(const vector<int>& arr, int k) {
    int n = arr.size();
    vector<int> answer;

    if (k <= 0 || k > n) {
        return answer;
    }

    multiset<int> window;

    for (int head = 0; head < n; head++) {
        window.insert(arr[head]);

        if (head - k >= 0) {
            int expired = arr[head - k];

            auto it = window.find(expired);

            if (it != window.end()) {
                window.erase(it);
            }
        }

        if (head >= k - 1) {
            answer.push_back(*window.begin());
        }
    }

    return answer;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, k;
    cin >> n >> k;

    vector<int> arr(n);

    for (int& value : arr) {
        cin >> value;
    }

    vector<int> answer = minimumsUsingMultiset(arr, k);

    for (int value : answer) {
        cout << value << ' ';
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

# 14. Approach 2 — Monotonic Deque

The deque stores only indices that are still useful minimum candidates.

For each `head`:

```text
1. remove expired indices from front

2. remove dominated candidates from back

3. push head

4. if window complete:
       arr[dq.front()] is minimum
```

---

## Action 1 — Remove Expired

Current window starts at:

```text
head - K + 1
```

So an index is outside the window when:

```text
index < head-K+1
```

Equivalent:

```text
index <= head-K
```

Hence:

```cpp
while (!dq.empty() && dq.front() <= head - k) {
    dq.pop_front();
}
```

---

## Action 2 — Remove Dominated Candidates

Before inserting `head`:

```cpp
while (!dq.empty() && arr[dq.back()] >= arr[head]) {
    dq.pop_back();
}
```

Why `>=`?

If old value is:

```text
greater than or equal to new value
```

then the new value is at least as good for minimum and survives longer.

So old one is useless.

---

## Action 3 — Insert

```cpp
dq.push_back(head);
```

---

## Action 4 — Answer

Once:

```text
head >= K-1
```

minimum is:

```cpp
arr[dq.front()]
```

---

# 15. Detailed Deque Dry Run

Use the lecture example:

```text
arr = [3,1,4,2,8,6,5,7]
K = 3
```

Deque stores indices.

We also show values for readability.

---

## head = 0, value = 3

No expired indices.

Deque empty, so push `0`.

```text
dq indices = [0]
dq values  = [3]
```

No complete window yet.

---

## head = 1, value = 1

Remove dominated from back:

```text
arr[0] = 3 >= 1
```

Pop `0`.

Push `1`.

```text
dq indices = [1]
dq values  = [1]
```

---

## head = 2, value = 4

Back:

```text
1 >= 4 ? no
```

Push `2`.

```text
dq indices = [1,2]
dq values  = [1,4]
```

Window complete:

```text
[3,1,4]
```

Minimum:

```text
arr[1] = 1
```

---

## head = 3, value = 2

Expired test:

```text
head-K = 0

dq.front() = 1
1 <= 0 ? no
```

Now remove dominated from back:

```text
arr[2] = 4 >= 2
```

Pop `2`.

Now back is index `1`:

```text
arr[1] = 1 >= 2 ? no
```

Push `3`.

```text
dq indices = [1,3]
dq values  = [1,2]
```

Minimum:

```text
1
```

---

## head = 4, value = 8

Expired:

```text
head-K = 1
```

Front:

```text
1 <= 1
```

So index `1` expired.

Pop front.

Deque:

```text
indices = [3]
values  = [2]
```

Dominated check:

```text
2 >= 8 ? no
```

Push `4`.

```text
indices = [3,4]
values  = [2,8]
```

Minimum:

```text
2
```

---

## head = 5, value = 6

Expired:

```text
head-K = 2
```

Front `3` is still valid.

Remove dominated:

```text
arr[4] = 8 >= 6
```

Pop `4`.

Now:

```text
arr[3] = 2 >= 6 ? no
```

Push `5`.

```text
indices = [3,5]
values  = [2,6]
```

Minimum:

```text
2
```

---

## head = 6, value = 5

Expired:

```text
head-K = 3
```

Front index `3` expires.

Pop front.

Deque:

```text
indices = [5]
values  = [6]
```

Remove dominated:

```text
6 >= 5
```

Pop `5`.

Push `6`.

```text
indices = [6]
values  = [5]
```

Minimum:

```text
5
```

---

## head = 7, value = 7

Expired:

```text
head-K = 4
```

Index `6` is valid.

Dominated:

```text
5 >= 7 ? no
```

Push `7`.

```text
indices = [6,7]
values  = [5,7]
```

Minimum:

```text
5
```

Final answer:

```text
1 1 2 2 5 5
```

Compact trace:

```text
head value   deque values   answer
0     3      [3]            -
1     1      [1]            -
2     4      [1,4]          1
3     2      [1,2]          1
4     8      [2,8]          2
5     6      [2,6]          2
6     5      [5]            5
7     7      [5,7]          5
```

---

# 16. Why the Deque Is Correct

We maintain three invariants.

## Invariant 1 — Indices Increase

New indices are always added at the back.

So:

```text
dq[0] < dq[1] < dq[2] < ...
```

This allows expiry from the front.

---

## Invariant 2 — Values Increase

We remove from the back while:

```text
arr[dq.back()] >= arr[head]
```

Therefore deque values stay:

```text
non-decreasing
```

Example:

```text
[1,2,5,8]
```

So the front is always smallest.

---

## Invariant 3 — Every Index Is Inside Current Window

Expired indices are removed by:

```text
dq.front() <= head-K
```

So every remaining index belongs to:

```text
[head-K+1, head]
```

Combining all three:

```text
dq.front()
=
oldest useful index with smallest value
```

Therefore:

```cpp
arr[dq.front()]
```

is the current window minimum.

---

# 17. C++17 — Monotonic Deque Solution

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> minimumsUsingDeque(const vector<int>& arr, int k) {
    int n = arr.size();
    vector<int> answer;

    if (k <= 0 || k > n) {
        return answer;
    }

    deque<int> dq;

    for (int head = 0; head < n; head++) {
        while (!dq.empty() && dq.front() <= head - k) {
            dq.pop_front();
        }

        while (!dq.empty() && arr[dq.back()] >= arr[head]) {
            dq.pop_back();
        }

        dq.push_back(head);

        if (head >= k - 1) {
            answer.push_back(arr[dq.front()]);
        }
    }

    return answer;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, k;
    cin >> n >> k;

    vector<int> arr(n);

    for (int& value : arr) {
        cin >> value;
    }

    vector<int> answer = minimumsUsingDeque(arr, k);

    for (int value : answer) {
        cout << value << ' ';
    }

    cout << '\n';

    return 0;
}
```

Complexity:

```text
Time  = O(N)
Space = O(K)
```

---

# 18. Multiset vs Monotonic Deque

| Property | Multiset | Monotonic Deque |
|---|---|---|
| Stores | Every value in current window | Only useful candidate indices |
| Minimum lookup | `O(1)` | `O(1)` |
| Insert/remove | `O(log K)` | `O(1)` amortized |
| Total | `O(N log K)` | `O(N)` |
| Space | `O(K)` | `O(K)` |
| Adaptability | More general | Specialized for min/max |
| Difficulty | Easier | More subtle |

Use a multiset when:

```text
you want a simple ordered representation
or need more than only min/max
```

Use monotonic deque when:

```text
problem asks specifically for
minimum/maximum in every fixed window
and linear time matters
```

---

## Sliding Window Maximum

Same deque pattern works.

For minimum we remove while:

```cpp
arr[dq.back()] >= arr[head]
```

For maximum reverse it:

```cpp
arr[dq.back()] <= arr[head]
```

Then the deque values stay non-increasing and:

```cpp
arr[dq.front()]
```

is the maximum.

---

# 19. Common Mistakes

## Mistake 1 — Processing Before Window Is Complete

First answer exists only when:

```text
head >= K-1
```

---

## Mistake 2 — Removing `head-K+1`

Wrong expired index:

```text
head-K+1
```

That is still the first element of current window.

Correct expired index:

```text
head-K
```

---

## Mistake 3 — Erasing All Duplicates from Multiset

Wrong:

```cpp
window.erase(value);
```

Correct:

```cpp
auto it = window.find(value);

if (it != window.end()) {
    window.erase(it);
}
```

---

## Mistake 4 — Storing Values in Deque

Expiry is based on position.

Store:

```text
indices
```

not only values.

---

## Mistake 5 — Removing From Wrong End

Expired:

```text
front
```

Dominated:

```text
back
```

Remember:

```text
front = too old
back  = too weak
```

---

## Mistake 6 — Thinking Nested `while` Means O(N²)

Each index enters once and leaves once.

Total deque operations are linear.

---

# 20. Recognition Pattern

Think **Form 0 Fixed Sliding Window** when the statement says:

```text
every subarray of size K
every segment of exactly K elements
minimum/maximum/sum/count for each K-sized window
```

Then immediately write:

```text
head moves from 0 to N-1

start   = head-K+1
expired = head-K
```

And decide what structure maintains the current window.

For sum/count:

```text
simple variable / frequency array
```

For min/max:

```text
multiset
or
monotonic deque
```

---

# 21. Final Mental Model

```text
FORM 0 — FIXED WINDOW

head moves only
      |
      v

[-------------------- array --------------------]
         [----- K -----]
                       ^
                       |
                      head

start
=
head-K+1

expired
=
head-K
```

At every `head`:

```text
ADD current
REMOVE expired
PROCESS when full
```

For Sliding Window Minimum:

```text
                 FIXED WINDOW MINIMUM
                         |
             +-----------+-----------+
             |                       |
             v                       v
          MULTISET              MONOTONIC DEQUE
             |                       |
     keep all K values       keep useful candidates
             |                       |
      min = begin()            min = dq.front()
             |                       |
        O(N log K)                 O(N)
```

The most important deque idea:

```text
older + larger/equal
=
useless candidate
```

So remove it from the back.

And always remember:

```text
front = expired candidates leave here
back  = dominated candidates leave here
```

### 60-second recognition

```text
Exact window size K
→ Form 0

Need min/max in every window
→ multiset or monotonic deque

Need optimal linear solution
→ monotonic deque
```
