# 019 — DP Form 2 Practice: Partition into K Subarrays
## Minimize the Sum of Subarray Minimums

# Table of Contents

- [1. Prerequisites](#1-prerequisites)
- [2. Quick Form-2 Recap from LIS](#2-quick-form-2-recap-from-lis)
- [3. Problem Statement](#3-problem-statement)
- [4. Understand the Problem Visually](#4-understand-the-problem-visually)
- [5. Step 1 — Decide the Form](#5-step-1--decide-the-form)
- [6. Step 2 — State Space Formulation](#6-step-2--state-space-formulation)
- [7. Why `dp(i, x)` Is Enough](#7-why-dpi-x-is-enough)
- [8. Step 3 — Transition Design](#8-step-3--transition-design)
- [9. Previous Cut `j` — The Main Observation](#9-previous-cut-j--the-main-observation)
- [10. Build the Transition Step by Step](#10-build-the-transition-step-by-step)
- [11. Avoid Recomputing the Subarray Minimum](#11-avoid-recomputing-the-subarray-minimum)
- [12. Pruning](#12-pruning)
- [13. Base Case](#13-base-case)
- [14. Cache Check](#14-cache-check)
- [15. Full Recurrence](#15-full-recurrence)
- [16. Dry Run — `[1,3,2,5,4]`, `K=3`](#16-dry-run--13254-k3)
- [17. Step 4 — Time Complexity](#17-step-4--time-complexity)
- [18. Step 5 — C++17 Code](#18-step-5--c17-code)
- [19. Why This Is Form 2](#19-why-this-is-form-2)
- [20. Common Mistakes](#20-common-mistakes)
- [21. Contest Recognition Checklist](#21-contest-recognition-checklist)
- [22. Final Revision Box](#22-final-revision-box)

---

# 1. Prerequisites

Before this problem, know:

```text
Recursion
Memoization
Array indexing
Subarrays
Partitioning an array
Minimum of a subarray
Optimization DP
Form 2 / Ending Form
```

Also remember the fixed DP workflow:

```text
1. Decide the Form
2. State Space Formulation
3. Transition Design
4. Time Complexity
5. Code
```

And the fixed recursive code structure:

```text
1. Pruning
2. Base Case
3. Cache Check
4. Compute / Transition
5. Save and Return
```

---

# 2. Quick Form-2 Recap from LIS

The lecture first recalls LIS.

For LIS:

```text
dp(i)
=
best increasing subsequence
ending exactly at index i
```

Visual:

```text
array:

index:   0   1   2   3   4   5
value:   1   2   3   5   7   9

                    ending at i
                         ↓
       ────→────→────→ [ 9 ]
```

Transition:

```text
dp(i) = max(dp(j) + 1)
```

for every:

```text
j < i
```

such that:

```text
arr[j] < arr[i]
```

The important Form-2 idea is:

```text
Fix where the answer ENDS.

Then ask:

"What previous state can lead into this ending?"
```

We use the same thinking in the partition problem.

---

# 3. Problem Statement

We have an array:

```text
arr[0 ... N-1]
```

We must divide it into exactly:

```text
K non-empty contiguous subarrays
```

For every subarray, take its minimum value.

Then add those minimums.

Goal:

```text
MINIMIZE the total sum.
```

Mathematically:

```text
min(
    minimum(partition 1)
  + minimum(partition 2)
  + ...
  + minimum(partition K)
)
```

---

# 4. Understand the Problem Visually

Lecture example:

```text
arr = [1, 3, 2, 5, 4]
K = 3
```

One possible partition:

```text
[1] | [3] | [2, 5, 4]
```

Minimum of each part:

```text
min([1])       = 1
min([3])       = 3
min([2,5,4])   = 2
```

Total:

```text
1 + 3 + 2 = 6
```

ASCII view:

```text
index:     0     1     2     3     4
         +---+ +---+ +-------------+
array:   | 1 | | 3 | | 2   5   4   |
         +---+ +---+ +-------------+
           ↓     ↓          ↓
          min   min        min
           1     3          2

Total = 1 + 3 + 2 = 6
```

So for this example:

```text
Answer = 6
```

---

# 5. Step 1 — Decide the Form

This is not a simple:

```text
Take / Not Take
```

problem.

We are building:

```text
a sequence of contiguous partitions
```

The current solution has an ending position.

So use:

```text
Form 2 — Ending Form
```

Main question:

```text
"If my x-th partition ends at index i,
where did the previous partition end?"
```

That previous ending position becomes the transition choice.

---

# 6. Step 2 — State Space Formulation

Define:

```text
dp(i, x)
=
minimum possible sum of subarray minimums

for arr[0 ... i]

using exactly x non-empty partitions
```

Important meaning:

```text
i
=
current prefix ends at index i

x
=
how many partitions are used
```

ASCII:

```text
arr:

0                         i
↓                         ↓
+---+---+---+---+---+---+
|   |   |   |   |   |   |
+---+---+---+---+---+---+
<--------- prefix -------->

dp(i, x)

=
best answer for this entire prefix
using exactly x partitions
```

---

# 7. Why `dp(i, x)` Is Enough

Suppose we are solving:

```text
dp(i, x)
```

We only need to know:

```text
1. where the current prefix ends → i
2. how many partitions we must create → x
```

We do not need to remember every previous cut.

Why?

Because once we choose the last cut:

```text
j
```

the earlier part becomes:

```text
dp(j, x - 1)
```

Everything before `j` is already summarized by that DP state.

This is the optimal-substructure idea.

---

# 8. Step 3 — Transition Design

Suppose:

```text
dp(i, x)
```

is the answer for:

```text
arr[0 ... i]
```

using:

```text
x partitions
```

The last partition must end at:

```text
i
```

Let:

```text
j
```

be the ending index of the previous part.

Then the last partition is:

```text
arr[j + 1 ... i]
```

ASCII:

```text
0                         j   j+1                i
↓                         ↓    ↓                 ↓
+---+---+---+---+---+---+----+------------------+
|       previous prefix       |   last partition |
+---+---+---+---+---+---+----+------------------+

     dp(j, x - 1)              min(arr[j+1 ... i])
```

Therefore candidate answer:

```text
dp(j, x - 1)
+
min(arr[j + 1 ... i])
```

Try every possible:

```text
j
```

and take the minimum.

---

# 9. Previous Cut `j` — The Main Observation

For the last partition ending at `i`, the previous cut can be:

```text
j = i - 1
j = i - 2
j = i - 3
...
j = -1
```

Why allow:

```text
j = -1
```

?

Because that represents:

```text
the last partition starts from index 0
```

Example:

```text
j = -1

last partition =
arr[0 ... i]
```

ASCII:

```text
j = -1
  ↓
  |  0   1   2   ...   i
  | +---+---+---+-----+---+
  +>|        one part      |
    +---+---+---+-----+---+
```

This works nicely with the base state:

```text
dp(-1, 0) = 0
```

---

# 10. Build the Transition Step by Step

We want:

```text
dp(i, x)
```

Try every possible previous cut:

```text
j
```

Then:

```text
previous partitions
=
dp(j, x - 1)
```

and:

```text
cost of current last partition
=
min(arr[j + 1 ... i])
```

So:

```text
candidate
=
dp(j, x - 1)
+
min(arr[j + 1 ... i])
```

Take minimum over all `j`.

Final recurrence:

```text
dp(i, x)
=
min over all j < i
(
    dp(j, x - 1)
    +
    min(arr[j + 1 ... i])
)
```

ASCII:

```text
                    dp(i, x)
                       │
        ┌──────────────┼───────────────┐
        │              │               │
       j=i-1          j=i-2           ...
        │              │
        ▼              ▼

 dp(i-1,x-1)      dp(i-2,x-1)
      +                 +
 min(arr[i..i])   min(arr[i-1..i])

        \              /
         \            /
          \          /
             MIN
              │
              ▼
           dp(i,x)
```

---

# 11. Avoid Recomputing the Subarray Minimum

A naive transition might compute:

```text
min(arr[j + 1 ... i])
```

from scratch for every `j`.

That would add another loop.

Instead, scan `j` backwards and maintain:

```text
minSeen
```

Start:

```text
minSeen = arr[i]
```

Then move `j` left.

Example:

```text
arr = [1, 3, 2, 5, 4]
                     i
                     ↓
                     4
```

Start:

```text
last segment = [4]
minSeen = 4
```

Move left:

```text
[5, 4]

minSeen = min(5,4)
        = 4
```

Move left again:

```text
[2, 5, 4]

minSeen = min(2,4)
        = 2
```

Again:

```text
[3, 2, 5, 4]

minSeen = min(3,2)
        = 2
```

Again:

```text
[1, 3, 2, 5, 4]

minSeen = min(1,2)
        = 1
```

So while moving `j`:

```text
the minimum of arr[j+1 ... i]
is updated in O(1)
```

This avoids an extra inner scan.

---

# 12. Pruning

Invalid condition:

```text
x < 0
```

We cannot use a negative number of partitions.

Because this is a minimization problem, return:

```text
INF
```

C++:

```cpp
if (x < 0) return INF;
```

We can also reject impossible states such as:

```text
x > i + 1
```

because:

```text
i + 1 elements
cannot form more than i + 1
non-empty partitions
```

So:

```cpp
if (x > i + 1) return INF;
```

---

# 13. Base Case

When:

```text
i == -1
```

there are no elements left.

This is valid only if:

```text
x == 0
```

So:

```text
dp(-1, 0) = 0
```

and:

```text
dp(-1, x > 0) = INF
```

C++:

```cpp
if (i == -1) {
    if (x == 0) return 0;
    return INF;
}
```

Visual:

```text
No elements left

+--------------------+
|                    |
+--------------------+

x = 0
→ valid
→ cost 0

x > 0
→ impossible
→ INF
```

---

# 14. Cache Check

State:

```text
dp[i][x]
```

If already computed:

```cpp
if (dp[i][x] != -1) return dp[i][x];
```

This prevents repeated work.

---

# 15. Full Recurrence

Putting everything together:

```text
rec(i, x)

=
minimum cost for arr[0 ... i]
using exactly x partitions
```

Transition:

```text
minSeen = arr[i]

for j = i-1 down to -1:

    candidate =
        rec(j, x - 1)
        +
        minSeen

    answer =
        min(answer, candidate)

    extend last segment one step left
    update minSeen
```

Mathematically:

```text
dp(i, x)
=
min
(
    dp(j, x - 1)
    +
    min(arr[j + 1 ... i])
)
```

for:

```text
-1 <= j <= i-1
```

---

# 16. Dry Run — `[1,3,2,5,4]`, `K=3`

We need:

```text
dp(4, 3)
```

because:

```text
last index = 4
number of partitions = 3
```

Array:

```text
index:   0   1   2   3   4
value:   1   3   2   5   4
```

One optimal partition is:

```text
[1] | [3] | [2,5,4]
```

Cost:

```text
1 + 3 + 2 = 6
```

---

## 16.1 Last partition approach

For:

```text
dp(4,3)
```

try different previous cuts.

### Case A — previous cut `j = 3`

Last partition:

```text
[4]
```

Cost:

```text
dp(3,2) + 4
```

---

### Case B — previous cut `j = 2`

Last partition:

```text
[5,4]
```

Minimum:

```text
4
```

Cost:

```text
dp(2,2) + 4
```

---

### Case C — previous cut `j = 1`

Last partition:

```text
[2,5,4]
```

Minimum:

```text
2
```

Cost:

```text
dp(1,2) + 2
```

Now:

```text
dp(1,2)
```

means:

```text
partition [1,3]
into exactly 2 parts
```

Only possible:

```text
[1] | [3]
```

Cost:

```text
1 + 3 = 4
```

Therefore:

```text
dp(1,2) + 2
=
4 + 2
=
6
```

So:

```text
dp(4,3) = 6
```

ASCII:

```text
Original:

[1, 3, 2, 5, 4]

Choose previous cut j = 1:

[1] | [3] | [2, 5, 4]
                 ↑
             last part

Previous cost:

dp(1,2)
=
min([1]) + min([3])
=
1 + 3
=
4

Last part:

min([2,5,4])
=
2

Total:

4 + 2 = 6
```

---

# 17. Step 4 — Time Complexity

State:

```text
dp(i, x)
```

Possible values:

```text
i → N possibilities
x → K possibilities
```

Number of states:

```text
O(NK)
```

For each state, try all possible previous cuts:

```text
O(N)
```

And because `minSeen` is updated incrementally:

```text
minimum update = O(1)
```

Therefore:

```text
Time
=
O(NK × N)

=
O(N²K)
```

Space:

```text
DP table = O(NK)
```

Recursion depth:

```text
O(N)
```

So:

```text
Space = O(NK)
```

---

# 18. Step 5 — C++17 Code

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long INF = (1LL << 60);

int n, k;
vector<long long> arr;
vector<vector<long long>> dp;

long long rec(int i, int x) {
    // 1. Pruning
    if (x < 0) return INF;
    if (i >= 0 && x > i + 1) return INF;

    // 2. Base Case
    if (i == -1) {
        if (x == 0) return 0;
        return INF;
    }

    // 3. Cache Check
    long long &ans = dp[i][x];
    if (ans != -1) return ans;

    // 4. Compute / Transition
    ans = INF;
    long long minSeen = arr[i];

    for (int j = i - 1; j >= -1; j--) {
        long long previous = rec(j, x - 1);

        if (previous != INF) {
            ans = min(ans, previous + minSeen);
        }

        if (j >= 0) {
            minSeen = min(minSeen, arr[j]);
        }
    }

    // 5. Save and Return
    return ans;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n >> k;

    arr.resize(n);

    for (int i = 0; i < n; i++) {
        cin >> arr[i];
    }

    dp.assign(n, vector<long long>(k + 1, -1));

    long long answer = rec(n - 1, k);

    if (answer == INF) {
        cout << -1 << '\n';
    } else {
        cout << answer << '\n';
    }

    return 0;
}
```

---

# 19. Why This Is Form 2

The key Form-2 pattern is:

```text
Current answer ends at i.
```

Then ask:

```text
Where did the previous piece end?
```

For LIS:

```text
current index = i

choose previous index j
```

For this partition problem:

```text
current prefix ends at i

choose previous partition ending j
```

Visual comparison:

```text
LIS
────────────────────────

... j ............ i
    \_____________/
         extend


Partition DP
────────────────────────

0 ........ j | j+1 ..... i
<-- old --> | <-- new -->
 dp(j,x-1)  | last part
```

Same Form-2 thinking:

```text
fix current ending
→ enumerate valid previous endings
```

---

# 20. Common Mistakes

## Mistake 1 — Using subsequence instead of subarray

Partitions must be:

```text
contiguous
```

Correct:

```text
[1,3] | [2,5] | [4]
```

Wrong:

```text
[1,5] | [3,4] | [2]
```

because elements are not contiguous partitions.

---

## Mistake 2 — Using fewer than exactly K parts

State means:

```text
exactly x partitions
```

not:

```text
at most x
```

That is why:

```text
dp(-1,0)=0
```

but:

```text
dp(-1,x>0)=INF
```

---

## Mistake 3 — Forgetting non-empty partitions

A partition must contain at least one element.

That is why the previous cut satisfies:

```text
j <= i - 1
```

and the new segment is:

```text
[j+1 ... i]
```

---

## Mistake 4 — Recomputing `min(arr[j+1...i])`

Do not scan the entire segment every transition.

Maintain:

```text
minSeen
```

while moving `j` left.

This keeps transition cost:

```text
O(N)
```

instead of:

```text
O(N²)
```

per state.

---

## Mistake 5 — Wrong optimization direction

The problem asks to:

```text
minimize
```

So use:

```cpp
ans = min(ans, candidate);
```

not:

```cpp
max(...)
```

---

## Mistake 6 — Returning zero for impossible states

For minimization:

```text
impossible
```

must return a very large value:

```text
INF
```

Otherwise zero may incorrectly become the best answer.

---

# 21. Contest Recognition Checklist

When you see:

```text
Split an array into K contiguous parts
```

ask these questions.

### 1. What is the ending?

```text
Current prefix ends at i
```

### 2. What extra state is needed?

```text
Number of partitions used / remaining
```

So:

```text
dp(i, x)
```

### 3. What is the transition choice?

```text
Where does the previous partition end?
```

Call it:

```text
j
```

### 4. What is the last-part cost?

Here:

```text
min(arr[j+1 ... i])
```

### 5. Recurrence

```text
dp(i,x)
=
min over j
(
    dp(j,x-1)
    +
    cost(j+1,i)
)
```

This is a very important generic partition-DP pattern.

---

# 22. Final Revision Box

```text
FORM 2 PARTITION DP
────────────────────────────────────

Problem:

Split array into exactly K
non-empty contiguous subarrays.

Cost of each subarray:

minimum element.

Goal:

minimize sum of those minimums.


STATE
────────────────────────────────────

dp(i, x)

=

minimum cost for arr[0 ... i]

using exactly x partitions.


ENDING FORM
────────────────────────────────────

Current solution ends at i.

Choose where the previous
partition ended.


PREVIOUS CUT
────────────────────────────────────

j

Previous prefix:

arr[0 ... j]

Last partition:

arr[j+1 ... i]


TRANSITION
────────────────────────────────────

dp(i,x)

=

min over j
(
    dp(j,x-1)
    +
    min(arr[j+1 ... i])
)


BASE CASE
────────────────────────────────────

dp(-1,0) = 0

dp(-1,x>0) = INF


PRUNING
────────────────────────────────────

x < 0
→ INF

x > i+1
→ INF


MIN-SEEN TRICK
────────────────────────────────────

Scan j from right to left.

Maintain:

minSeen

So:

min(arr[j+1 ... i])

is updated in O(1).


COMPLEXITY
────────────────────────────────────

States:

O(NK)

Transitions/state:

O(N)

Total:

O(N²K)

Space:

O(NK)


EXAMPLE
────────────────────────────────────

arr = [1,3,2,5,4]
K = 3

Optimal:

[1] | [3] | [2,5,4]

Cost:

1 + 3 + 2

= 6


CORE FORM-2 QUESTION
────────────────────────────────────

"If my solution ends here,
where could the previous
piece have ended?"

That previous ending
becomes the transition.
```
