# Two Pointers — Form 1 Advanced
## Shortest Subarray with Sum at Least K
### Positive Numbers → Sliding Window | Negative Numbers → Prefix Sum + Monotonic Deque
### TLE-Style Self-Study Notes

> Problem:
>
> ```text
> Find the length of the shortest, non-empty, contiguous subarray
> whose sum is at least K.
>
> If no such subarray exists, return -1.
> ```
>
> The important lesson is:
>
> ```text
> all values positive/non-negative
> → normal variable sliding window works
>
> negative values allowed
> → normal sliding window can fail
> → use prefix sum + monotonic deque
> ```

---

# Table of Contents

- [1. Problem Statement](#1-problem-statement)
- [2. Prerequisite 1 — Minimum-Length Sliding Window](#2-prerequisite-1--minimum-length-sliding-window)
- [3. Prerequisite 2 — Why Positive Numbers Are Easy](#3-prerequisite-2--why-positive-numbers-are-easy)
- [4. Positive-Only Solution](#4-positive-only-solution)
- [5. Why Negative Numbers Break Sliding Window](#5-why-negative-numbers-break-sliding-window)
- [6. Prerequisite 3 — Prefix Sum](#6-prerequisite-3--prefix-sum)
- [7. Convert the Subarray Condition into Prefix-Sum Math](#7-convert-the-subarray-condition-into-prefix-sum-math)
- [8. What Are We Really Searching For?](#8-what-are-we-really-searching-for)
- [9. Why a Normal Queue Is Not Enough](#9-why-a-normal-queue-is-not-enough)
- [10. Prerequisite 4 — Monotonic Deque](#10-prerequisite-4--monotonic-deque)
- [11. The Two Deque Rules](#11-the-two-deque-rules)
- [12. Full Algorithm](#12-full-algorithm)
- [13. Detailed Dry Run](#13-detailed-dry-run)
- [14. Why Rule 1 Is Correct](#14-why-rule-1-is-correct)
- [15. Why Rule 2 Is Correct](#15-why-rule-2-is-correct)
- [16. Proper C++17 Solution](#16-proper-c17-solution)
- [17. Complexity](#17-complexity)
- [18. Common Mistakes](#18-common-mistakes)
- [19. Recognition Pattern](#19-recognition-pattern)
- [20. Final Mental Model](#20-final-mental-model)

---

# 1. Problem Statement

Given an integer array:

```text
nums
```

and an integer:

```text
K
```

find the shortest non-empty contiguous subarray with:

```text
sum >= K
```

If no such subarray exists:

```text
return -1
```

The lecture example allows negative numbers.

Example:

```text
K = 17

nums = [2, -8, -8, -2, 12, 8, -8, -8]
```

Because negative values exist, this is **not** a normal sliding-window sum problem.

---

# 2. Prerequisite 1 — Minimum-Length Sliding Window

For a normal minimum-length problem with a monotonic lower-bound condition, the reusable template is:

```cpp
int tail = 0;
int answer = INF;

for (int head = 0; head < n; head++) {
    add(arr[head]);

    while (valid()) {
        answer = min(answer, head - tail + 1);

        remove(arr[tail]);
        tail++;
    }
}
```

Mental model:

```text
EXPAND head
     ↓
window becomes valid
     ↓
record answer
     ↓
SHRINK from tail
while still valid
```

This works beautifully for:

```text
sum >= K
```

when all values are positive or non-negative.

---

# 3. Prerequisite 2 — Why Positive Numbers Are Easy

Suppose:

```text
nums = [2, 8, 12, 4, 5, 5, 11]
K = 17
```

All numbers are positive.

As `head` moves right:

```text
window sum can only increase
```

As `tail` moves right:

```text
window sum can only decrease
```

That gives us predictable movement.

---

## Tiny Dry Run

Start:

```text
tail = 0
sum = 0
```

Expand:

```text
head=0 → [2]        sum=2
head=1 → [2,8]      sum=10
head=2 → [2,8,12]   sum=22
```

Now:

```text
22 >= 17
```

Window is valid.

Candidate:

```text
length = 3
```

Shrink:

```text
remove 2
```

Now:

```text
[8,12]
sum = 20
```

Still valid.

Candidate:

```text
length = 2
```

Shrink again:

```text
remove 8
```

Now:

```text
[12]
sum = 12
```

Invalid.

So the shortest valid window seen so far is:

```text
2
```

---

# 4. Positive-Only Solution

For non-negative values:

```cpp
#include <bits/stdc++.h>
using namespace std;

int shortestSubarrayPositive(const vector<int>& nums, long long k) {
    int n = nums.size();

    int tail = 0;
    int answer = n + 1;

    long long windowSum = 0;

    for (int head = 0; head < n; head++) {
        windowSum += nums[head];

        while (windowSum >= k) {
            answer = min(answer, head - tail + 1);

            windowSum -= nums[tail];
            tail++;
        }
    }

    return (answer == n + 1 ? -1 : answer);
}
```

Complexity:

```text
Time  = O(N)
Space = O(1)
```

---

# 5. Why Negative Numbers Break Sliding Window

Now consider the lecture's negative-number case:

```text
nums = [2, -8, -8, -2, 12, 8, -8, -8]
K = 17
```

The key problem is:

```text
expanding does NOT always increase the sum
shrinking does NOT always decrease the sum
```

---

## Example 1 — Adding Can Repair an Invalid Window

Suppose a window is currently below `K`.

With positive values, adding more values only increases the sum predictably.

With negatives:

```text
sum can go:
small → smaller → larger → smaller
```

So "currently invalid" does not tell us enough.

---

## Example 2 — Removing Can Make the Sum Worse

Suppose:

```text
window = [-5, 4]
sum = -1
```

If the condition were:

```text
sum <= 0
```

this window is valid.

Remove `-5`:

```text
window = [4]
sum = 4
```

Now it becomes invalid.

So:

```text
removing from the left
does not necessarily improve the condition
```

That destroys ordinary sliding-window monotonicity.

---

## Conclusion

For arbitrary signed values:

```text
standard sum-based sliding window is unsafe
```

We need a different representation.

That representation is:

```text
PREFIX SUM
```

---

# 6. Prerequisite 3 — Prefix Sum

Define:

```text
prefix[0] = 0
```

and:

```text
prefix[i+1]
=
prefix[i] + nums[i]
```

So:

```text
prefix[i]
=
sum of first i elements
```

---

## Lecture Example

```text
nums = [2, -8, -8, -2, 12, 8, -8, -8]
```

Prefix sums:

```text
prefix[0] = 0
prefix[1] = 2
prefix[2] = -6
prefix[3] = -14
prefix[4] = -16
prefix[5] = -4
prefix[6] = 4
prefix[7] = -4
prefix[8] = -12
```

So:

```text
prefix =
[0, 2, -6, -14, -16, -4, 4, -4, -12]
```

ASCII:

```text
index:    0   1   2    3    4    5   6    7    8
prefix:  [0,  2, -6, -14, -16,  -4,  4,  -4, -12]
```

---

## Subarray Sum Formula

Sum of:

```text
nums[j ... i-1]
```

is:

```text
prefix[i] - prefix[j]
```

This is the bridge from subarray sums to prefix-index pairs.

---

# 7. Convert the Subarray Condition into Prefix-Sum Math

We want:

```text
subarray sum >= K
```

Using prefix sums:

```text
prefix[i] - prefix[j] >= K
```

Rearrange:

```text
prefix[j] <= prefix[i] - K
```

So for each ending prefix index `i`, we need an earlier prefix index `j` such that:

```text
prefix[j] <= prefix[i] - K
```

The subarray length is:

```text
i - j
```

Since we want the shortest subarray:

```text
minimize i - j
```

For fixed `i`, that means:

```text
choose the largest possible valid j
```

subject to:

```text
j < i
prefix[j] <= prefix[i] - K
```

This is the key mathematical observation from the lecture.

---

# 8. What Are We Really Searching For?

For each current prefix index:

```text
i
```

we want to find an earlier:

```text
j
```

such that:

```text
prefix[i] - prefix[j] >= K
```

and among such `j`, we prefer one giving the shortest distance:

```text
i - j
```

So we need a data structure that helps maintain useful previous prefix indices.

A normal queue keeps indices in time order.

But we also care about:

```text
prefix values
```

Specifically, small prefix values are better candidates.

That leads to:

```text
monotonic deque
```

---

# 9. Why a Normal Queue Is Not Enough

Suppose two earlier prefix candidates are:

```text
j1 < j2
```

and:

```text
prefix[j1] >= prefix[j2]
```

Then `j1` is worse than `j2`.

Why?

For any future `i`:

```text
prefix[i] - prefix[j2]
>=
prefix[i] - prefix[j1]
```

So if `j1` can form a valid subarray, `j2` can also form one.

And:

```text
j2 > j1
```

so:

```text
i - j2 < i - j1
```

`j2` gives an equal-or-better sum condition and a shorter length.

Therefore:

```text
j1 can never be useful again
```

We should remove it.

This is the dominance idea behind the monotonic deque.

---

# 10. Prerequisite 4 — Monotonic Deque

We maintain prefix indices in a deque such that their prefix values are:

```text
strictly increasing
```

Conceptually:

```text
prefix[dq[0]]
<
prefix[dq[1]]
<
prefix[dq[2]]
<
...
```

Indices themselves are also increasing because we process from left to right.

So the deque maintains:

```text
increasing index
+
increasing prefix value
```

---

## Example

Suppose deque contains:

```text
indices:  [2, 4, 6]
prefix:   [-6, -4, 4]
```

Now a new prefix value appears:

```text
prefix[i] = -12
```

Since:

```text
-12 <= 4
-12 <= -4
-12 <= -6
```

all old candidates are dominated.

We pop them from the back.

Deque becomes:

```text
[i]
```

---

# 11. The Two Deque Rules

There are exactly two important operations.

---

## Rule 1 — Pop Front When We Found a Valid Subarray

For current `i`, while:

```text
prefix[i] - prefix[dq.front()] >= K
```

we found a valid subarray.

Candidate length:

```text
i - dq.front()
```

Update:

```cpp
answer = min(answer, i - dq.front());
```

Then pop the front.

Why is it safe?

Because for all future `i' > i`:

```text
i' - dq.front()
>
i - dq.front()
```

So this same starting index can never produce a shorter answer later.

Therefore:

```cpp
dq.pop_front();
```

---

## Rule 2 — Pop Back When New Prefix Dominates Old Prefix

Before inserting current `i`, while:

```text
prefix[i] <= prefix[dq.back()]
```

the old back is dominated.

Why?

New index `i` is:

```text
later
```

and has:

```text
smaller or equal prefix sum
```

So it is always at least as good for future subarrays.

Therefore:

```cpp
dq.pop_back();
```

---

## Then Insert Current Index

```cpp
dq.push_back(i);
```

---

# 12. Full Algorithm

Build prefix sums on the fly.

Initialize:

```text
prefix[0] = 0
deque = empty
answer = INF
```

For every prefix index `i`:

```text
1. While current prefix - front prefix >= K:
       update shortest length
       pop front

2. While current prefix <= back prefix:
       pop back

3. Push current prefix index
```

That is the entire pattern.

Pseudo-code:

```text
for i = 0 ... n:

    while deque not empty
          and prefix[i] - prefix[deque.front] >= K:

        answer = min(answer, i - deque.front)
        pop_front

    while deque not empty
          and prefix[i] <= prefix[deque.back]:

        pop_back

    push_back(i)
```

---

# 13. Detailed Dry Run

Use the lecture example:

```text
nums = [2, -8, -8, -2, 12, 8, -8, -8]
K = 17
```

Prefix:

```text
[0, 2, -6, -14, -16, -4, 4, -4, -12]
```

We store prefix indices.

---

## i = 0

```text
prefix[0] = 0
```

Deque empty.

Push:

```text
dq = [0]
```

---

## i = 1

```text
prefix[1] = 2
```

Check front:

```text
2 - 0 = 2 < 17
```

No valid subarray.

Back dominance:

```text
2 <= 0 ? no
```

Push:

```text
dq = [0,1]
```

Prefix values:

```text
[0,2]
```

---

## i = 2

```text
prefix[2] = -6
```

Valid check:

```text
-6 - 0 < 17
```

Now dominance:

```text
-6 <= 2
```

Pop index `1`.

Still:

```text
-6 <= 0
```

Pop index `0`.

Push `2`.

```text
dq = [2]
```

Prefix values:

```text
[-6]
```

---

## i = 3

```text
prefix[3] = -14
```

Dominance:

```text
-14 <= -6
```

Pop `2`.

Push `3`.

```text
dq = [3]
```

---

## i = 4

```text
prefix[4] = -16
```

Dominance:

```text
-16 <= -14
```

Pop `3`.

Push `4`.

```text
dq = [4]
```

---

## i = 5

```text
prefix[5] = -4
```

Check:

```text
-4 - (-16)
=
12
<
17
```

No valid answer.

Back:

```text
-4 <= -16 ? no
```

Push:

```text
dq = [4,5]
```

---

## i = 6

```text
prefix[6] = 4
```

Check front:

```text
4 - (-16)
=
20
>=
17
```

Valid.

Length:

```text
6 - 4 = 2
```

This corresponds to:

```text
nums[4...5] = [12,8]
```

Sum:

```text
20
```

Update:

```text
answer = 2
```

Pop front index `4`.

Check again with new front `5`:

```text
4 - (-4)
=
8
<
17
```

Stop.

Push `6`.

```text
dq = [5,6]
```

Prefix values:

```text
[-4,4]
```

---

## Remaining Prefixes

At:

```text
i = 7
prefix = -4
```

the new `-4` dominates previous larger/equal values at the back.

At:

```text
i = 8
prefix = -12
```

it again removes dominated candidates.

No shorter valid subarray than length `2` appears.

Final answer:

```text
2
```

---

# 14. Why Rule 1 Is Correct

Rule:

```cpp
while (prefix[i] - prefix[dq.front()] >= k) {
    answer = min(answer, i - dq.front());
    dq.pop_front();
}
```

Suppose:

```text
j = dq.front()
```

and current `i` already gives:

```text
prefix[i] - prefix[j] >= K
```

So:

```text
[j ... i-1]
```

is valid.

Any future endpoint:

```text
i' > i
```

would have length:

```text
i' - j
>
i - j
```

Therefore index `j` cannot produce a shorter subarray later.

It is safe to remove.

---

# 15. Why Rule 2 Is Correct

Rule:

```cpp
while (!dq.empty() && prefix[i] <= prefix[dq.back()]) {
    dq.pop_back();
}
```

Suppose:

```text
j = dq.back()
j < i
```

and:

```text
prefix[i] <= prefix[j]
```

Compare the two candidates for any future endpoint `r`.

Using `j`:

```text
sum = prefix[r] - prefix[j]
length = r - j
```

Using `i`:

```text
sum = prefix[r] - prefix[i]
length = r - i
```

Because:

```text
prefix[i] <= prefix[j]
```

we get:

```text
prefix[r] - prefix[i]
>=
prefix[r] - prefix[j]
```

And because:

```text
i > j
```

we get:

```text
r - i
<
r - j
```

So current index `i` is better in both ways:

```text
larger/equal achievable subarray sum
+
shorter length
```

Therefore `j` is useless forever.

Pop it.

---

# 16. Proper C++17 Solution

This uses the compact C++ style from the earlier notes.

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int shortestSubarrayAtLeastK(const vector<int>& nums, ll k) {
    int n = nums.size();

    vector<ll> prefix(n + 1, 0);

    for (int i = 0; i < n; i++) {
        prefix[i + 1] = prefix[i] + nums[i];
    }

    deque<int> dq;

    int answer = n + 1;

    for (int i = 0; i <= n; i++) {
        while (!dq.empty() && prefix[i] - prefix[dq.front()] >= k) {
            answer = min(answer, i - dq.front());
            dq.pop_front();
        }

        while (!dq.empty() && prefix[i] <= prefix[dq.back()]) {
            dq.pop_back();
        }

        dq.push_back(i);
    }

    return (answer == n + 1 ? -1 : answer);
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    ll k;

    cin >> n >> k;

    vector<int> nums(n);

    for (int& value : nums) {
        cin >> value;
    }

    cout << shortestSubarrayAtLeastK(nums, k) << '\n';

    return 0;
}
```

---

## Memory-Optimized Version

We can also compute prefix sum incrementally and store:

```text
(prefixSum, index)
```

inside the deque.

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int shortestSubarrayAtLeastK(const vector<int>& nums, ll k) {
    int n = nums.size();

    deque<pair<ll, int>> dq;

    ll prefixSum = 0;
    int answer = n + 1;

    dq.push_back({0, 0});

    for (int i = 1; i <= n; i++) {
        prefixSum += nums[i - 1];

        while (!dq.empty() && prefixSum - dq.front().first >= k) {
            answer = min(answer, i - dq.front().second);
            dq.pop_front();
        }

        while (!dq.empty() && prefixSum <= dq.back().first) {
            dq.pop_back();
        }

        dq.push_back({prefixSum, i});
    }

    return (answer == n + 1 ? -1 : answer);
}
```

The first version is usually easier for learning.

---

# 17. Complexity

Each prefix index is:

```text
pushed into deque once
```

and removed:

```text
at most once from front
or
at most once from back
```

So total deque operations are:

```text
O(N)
```

Prefix construction:

```text
O(N)
```

Overall:

```text
Time  = O(N)
Space = O(N)
```

This is amortized analysis.

---

# 18. Common Mistakes

## Mistake 1 — Using Normal Sliding Window with Negative Numbers

This is the biggest mistake.

With negative values:

```text
adding can decrease sum
removing can increase sum
```

So standard window monotonicity is gone.

---

## Mistake 2 — Forgetting `prefix[0] = 0`

Without it, subarrays beginning at index `0` are awkward or missed.

Always use:

```text
prefix size = n+1
prefix[0] = 0
```

---

## Mistake 3 — Confusing Prefix Indices with Array Indices

If:

```text
prefix[i] - prefix[j]
```

then the represented subarray is:

```text
nums[j ... i-1]
```

Length:

```text
i - j
```

---

## Mistake 4 — Pop Back Using the Wrong Comparison

For shortest subarray with lower-bound sum, maintain increasing prefix sums.

So remove while:

```cpp
prefix[i] <= prefix[dq.back()]
```

not the opposite.

---

## Mistake 5 — Pop Front Only Once

There may be several valid starting candidates.

Use:

```cpp
while (...)
```

not:

```cpp
if (...)
```

Each popped front gives a valid subarray, and later front indices may produce an even shorter one.

---

## Mistake 6 — Use `int` for Prefix Sum

If array values or `N` are large, prefix sum may overflow `int`.

Use:

```cpp
long long
```

---

# 19. Recognition Pattern

Think of this pattern when you see:

```text
shortest subarray
+
sum >= K
+
negative numbers allowed
```

Normal Form 1 question:

```text
Can I use sum sliding window?
```

Check:

```text
Are all values non-negative?
```

If yes:

```text
normal variable sliding window
```

If no:

```text
prefix sum + monotonic deque
```

Recognition flow:

```text
Shortest subarray sum >= K
            |
            v
    values all non-negative?
        /            \
      YES             NO
       |               |
       v               v
 normal sliding     prefix sum
    window             +
                  monotonic deque
```

---

# 20. Final Mental Model

Start with:

```text
subarray sum >= K
```

Convert:

```text
prefix[i] - prefix[j] >= K
```

Rearrange:

```text
prefix[j] <= prefix[i] - K
```

We want:

```text
minimum i-j
```

So maintain useful earlier `j` values.

The deque performs two jobs:

```text
FRONT
→ candidates that already form valid subarrays
→ use them
→ then discard them

BACK
→ dominated prefix candidates
→ discard them before inserting current index
```

ASCII:

```text
                 MONOTONIC DEQUE
                        |
        +---------------+---------------+
        |                               |
        v                               v
      FRONT                            BACK
        |                               |
        v                               v
prefix[i]-prefix[front] >= K    prefix[i] <= prefix[back]
        |                               |
        v                               v
valid subarray found             old back dominated
update minimum                    remove it
pop front                         pop back
```

The compact code pattern to remember is:

```cpp
for (int i = 0; i <= n; i++) {
    while (!dq.empty() && prefix[i] - prefix[dq.front()] >= k) {
        answer = min(answer, i - dq.front());
        dq.pop_front();
    }

    while (!dq.empty() && prefix[i] <= prefix[dq.back()]) {
        dq.pop_back();
    }

    dq.push_back(i);
}
```

### 60-Second Recognition

```text
Positive/non-negative values:
sum >= K
→ Sliding Window

Negative values allowed:
sum >= K
→ Prefix Sum + Monotonic Deque
```
