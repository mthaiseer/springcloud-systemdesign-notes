# 016 — DP with Caching Across Queries
## Subset Sum → Redesign the State → Reuse Memoization for Many Targets

> **Main lecture goal:** Learn an important DP design lesson:
>
> ```text
> A state must contain ALL information
> that can change the future answer.
> ```
>
> If a hidden global value changes between queries, the same-looking DP state may no longer mean the same problem.
>
> The lecture starts with normal **Subset Sum**, first designs:
>
> ```text
> dp(level, sumTaken)
> ```
>
> then realizes this is awkward for caching across many target queries because the answer also depends on the current target `T`.
>
> The better state is:
>
> ```text
> dp(level, sumLeft)
> ```
>
> Now the target is completely represented by `sumLeft`, so one memo table can be reused across all queries.
>
> ---
>
> # Fixed DP Framework
>
> ```text
> 1. Prerequisites
> 2. Decide the Form
> 3. State Space Formulation
> 4. Transition Design
> 5. Time Complexity
> 6. C++17 Code
> ```
>
> # Fixed Recursive Code Structure
>
> ```text
> 1. Pruning
> 2. Base Case
> 3. Cache Check
> 4. Compute / Transition
> 5. Save and Return
> ```
>
> **Code-format rule:** simple transitions stay on one readable line.
>
> Preferred:
>
> ```cpp
> take = rec(level + 1, sumLeft - value[level]);
> ```

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Problem Statement](#2-problem-statement)
3. [Example](#3-example)
4. [Step 1 — Decide the Form](#4-step-1--decide-the-form)
5. [First State Idea — `dp(level, sumTaken)`](#5-first-state-idea--dplevel-sumtaken)
6. [Transition for `sumTaken`](#6-transition-for-sumtaken)
7. [Why This Works for One Query](#7-why-this-works-for-one-query)
8. [Why Caching Breaks Across Queries](#8-why-caching-breaks-across-queries)
9. [The Hidden-State Problem](#9-the-hidden-state-problem)
10. [Redesign the State](#10-redesign-the-state)
11. [Final State — `dp(level, sumLeft)`](#11-final-state--dplevel-sumleft)
12. [Transition Design](#12-transition-design)
13. [Pruning](#13-pruning)
14. [Base Case](#14-base-case)
15. [Cache Check](#15-cache-check)
16. [Complete Recurrence](#16-complete-recurrence)
17. [Dry Run](#17-dry-run)
18. [Why Memoization Can Be Shared Across Queries](#18-why-memoization-can-be-shared-across-queries)
19. [Time Complexity](#19-time-complexity)
20. [C++17 — Caching Across Queries](#20-c17--caching-across-queries)
21. [Why `memset(dp,-1)` Must Be Outside the Query Loop](#21-why-memsetdp-1-must-be-outside-the-query-loop)
22. [Comparison — `sumTaken` vs `sumLeft`](#22-comparison--sumtaken-vs-sumleft)
23. [State Design Lesson](#23-state-design-lesson)
24. [Common Mistakes](#24-common-mistakes)
25. [Contest Checklist](#25-contest-checklist)
26. [Final Revision Box](#26-final-revision-box)

---

# 1. Prerequisites

You should already know:

```text
recursion
memoization
subset / take-not-take form
boolean DP
subset sum
multiple queries
```

---

## 1.1 Boolean DP

This problem is not asking:

```text
maximum value
```

or:

```text
minimum cost
```

It asks:

```text
Does a valid subset exist?
```

So each state returns:

```text
0 → impossible
1 → possible
```

In C++ we can use:

```cpp
int
```

or:

```cpp
bool
```

---

## 1.2 Take / Not Take

For every item:

```text
either take it
or skip it
```

This is the standard subset form.

---

## 1.3 Why memoization helps

Without DP:

```text
2 choices per item
```

so:

```text
2^N
```

possible subsets.

With memoization:

```text
many different decision paths
reach the same logical state
```

so we solve each state once.

---

# 2. Problem Statement

There are:

```text
N items
```

with values:

```text
x1, x2, x3, ..., xN
```

For each query, we are given a target:

```text
T
```

Question:

```text
Does there exist a subset
whose sum is exactly T?
```

Typical lecture constraints:

```text
N <= 100
```

and values / targets are bounded so a pseudo-polynomial DP is possible.

There may be:

```text
Q queries
```

with different targets.

Goal:

```text
answer all queries efficiently
```

without rebuilding the whole DP from scratch every time.

---

# 3. Example

Items:

```text
{1, 2, 5, 9}
```

Queries:

```text
T = 3
T = 4
T = 16
```

---

## Query 1

```text
T = 3
```

Subset:

```text
{1,2}
```

Sum:

```text
1 + 2 = 3
```

Answer:

```text
YES
```

---

## Query 2

```text
T = 4
```

No subset sums to:

```text
4
```

Answer:

```text
NO
```

---

## Query 3

```text
T = 16
```

Subset:

```text
{2,5,9}
```

Sum:

```text
2 + 5 + 9 = 16
```

Answer:

```text
YES
```

---

# 4. Step 1 — Decide the Form

Ask:

```text
What decision do we make for each item?
```

Answer:

```text
TAKE
or
NOT TAKE
```

So:

```text
Form = Choose a subset
```

Decision tree:

```text
                    x1
                 /      \
              TAKE      SKIP
              /           \
             x2           x2
           /   \         /   \
        TAKE  SKIP    TAKE  SKIP
                  ...
```

Without memoization:

```text
O(2^N)
```

---

# 5. First State Idea — `dp(level, sumTaken)`

A natural first state is:

```text
dp(level, sumTaken)
```

Meaning:

```text
0/1 whether it is possible
to reach target T

using items [level ... N]

when sumTaken has already been selected
from earlier items
```

This works for a **single fixed target `T`**.

---

## 5.1 Initial state

At the beginning:

```text
level = 1
sumTaken = 0
```

So:

```text
rec(1, 0)
```

---

# 6. Transition for `sumTaken`

At item:

```text
x[level]
```

we have two choices.

---

## Do not take

```text
notTake = rec(level + 1, sumTaken)
```

---

## Take

```text
take = rec(level + 1, sumTaken + x[level])
```

---

## Combine

Because this is existence:

```text
answer = notTake || take
```

---

# 7. Why This Works for One Query

Suppose the target is fixed:

```text
T = 16
```

Then the state:

```text
rec(level, sumTaken)
```

always means:

```text
Can I eventually reach 16?
```

Base case:

```text
if all items are processed:
    return sumTaken == T
```

So for one fixed query, this is correct.

---

# 8. Why Caching Breaks Across Queries

Now suppose we answer:

```text
T = 3
```

and later:

```text
T = 4
```

Consider cached state:

```text
dp(level, sumTaken)
```

Example:

```text
dp(3, 3)
```

For:

```text
T = 3
```

this may be:

```text
1
```

because we have already reached the target.

But for:

```text
T = 4
```

the same state:

```text
dp(3,3)
```

means something different:

```text
Can we reach 4 from current sum 3?
```

The answer may differ.

So:

```text
same dp indices
```

do **not** represent the same logical problem across queries.

---

# 9. The Hidden-State Problem

The real state in the first design is secretly:

```text
(level, sumTaken, T)
```

because the answer depends on:

```text
T
```

even though `T` is not inside the memo-table indices.

That is dangerous.

The lecture's key state-design lesson is:

```text
If a value can change the answer,
it must either:

1. be part of the state

or

2. be transformed away
```

We do not want:

```text
dp[level][sumTaken][T]
```

because that adds another dimension.

Instead, redesign the state.

---

# 10. Redesign the State

Instead of asking:

```text
How much have we already taken?
```

ask:

```text
How much is still left to create?
```

Define:

```text
sumLeft
=
target still required
```

Initially:

```text
sumLeft = T
```

When we take value:

```text
x[level]
```

the remaining target becomes:

```text
sumLeft - x[level]
```

This naturally absorbs the query target into the state.

---

# 11. Final State — `dp(level, sumLeft)`

Define:

```text
rec(level, sumLeft)
=
0/1 whether we can create
exactly `sumLeft`

using items [level ... N]
```

This state contains everything needed for the future.

Important:

```text
T no longer appears
inside the recurrence.
```

The query only decides the starting state:

```text
rec(1, T)
```

That makes the DP reusable across all queries.

---

# 12. Transition Design

At:

```text
rec(level, sumLeft)
```

current item is:

```text
x[level]
```

Two choices.

---

## 12.1 Do Not Take

Remaining target does not change.

```text
notTake = rec(level + 1, sumLeft)
```

---

## 12.2 Take

If we take current value:

```text
sumLeft
```

decreases by:

```text
x[level]
```

Transition:

```text
take = rec(level + 1, sumLeft - x[level])
```

---

## 12.3 Final transition

```text
answer = notTake || take
```

Compact:

```text
notTake = rec(level + 1, sumLeft)

take = rec(level + 1, sumLeft - x[level])

answer = notTake || take
```

---

# 13. Pruning

If:

```text
sumLeft < 0
```

we exceeded the required sum.

Because all values in this lecture setup are positive:

```text
we cannot recover back to zero
```

So:

```cpp
if (sumLeft < 0) return 0;
```

This is the lecture's pruning step.

---

# 14. Base Case

When:

```text
level == N + 1
```

there are no items left.

The target is achievable only if:

```text
sumLeft == 0
```

So:

```cpp
if (level == n + 1) return sumLeft == 0;
```

Equivalent:

```text
sumLeft == 0
→ YES

sumLeft != 0
→ NO
```

---

# 15. Cache Check

Memo table:

```text
dp[level][sumLeft]
```

Before computing:

```cpp
int &ans = dp[level][sumLeft];
if (ans != -1) return ans;
```

The important part is:

```text
This cached answer is independent
of which query first created it.
```

That is why it can be shared.

---

# 16. Complete Recurrence

```text
rec(level, sumLeft)
```

Steps:

```text
1. if sumLeft < 0
       return 0

2. if level == N+1
       return sumLeft == 0

3. if cached
       return cached value

4. notTake = rec(level + 1, sumLeft)

5. take = rec(level + 1, sumLeft - x[level])

6. answer = notTake || take

7. cache and return
```

---

# 17. Dry Run

Items:

```text
{1,2,5,9}
```

Query:

```text
T = 3
```

Start:

```text
rec(1,3)
```

Current:

```text
x[1] = 1
```

---

## Skip 1

```text
rec(2,3)
```

---

## Take 1

```text
rec(2,2)
```

At:

```text
rec(2,2)
```

current value is:

```text
2
```

Take it:

```text
rec(3,0)
```

Once:

```text
sumLeft = 0
```

we already know the required sum has been formed.

The recursion can eventually return:

```text
1
```

So:

```text
T = 3
```

is possible.

---

# 18. Why Memoization Can Be Shared Across Queries

Suppose one query computes:

```text
rec(3,7)
```

Later, another query also reaches:

```text
rec(3,7)
```

Both states mean exactly:

```text
Can values from index 3 onward
form sum 7?
```

That question has nothing to do with:

```text
which original target query we started from
```

So the answer is identical.

This is the core idea of:

```text
caching across queries
```

---

## 18.1 Query 1

```text
T = 16
```

May compute:

```text
rec(3,7)
```

and cache it.

---

## 18.2 Query 2

```text
T = 9
```

May later need:

```text
rec(3,7)
```

Now:

```text
return cached answer immediately
```

No recomputation.

---

# 19. Time Complexity

Let:

```text
MAX_T
=
maximum target among all queries
```

State dimensions:

```text
level   → N + 1
sumLeft → 0 ... MAX_T
```

So:

```text
#states
=
O(N × MAX_T)
```

Every state has:

```text
2 transitions
```

Therefore total DP computation across **all queries** is:

```text
O(N × MAX_T)
```

Each query itself only makes an initial call:

```text
rec(1,T)
```

Once the necessary states are cached, lookup is fast.

For:

```text
Q queries
```

overall:

```text
O(N × MAX_T + Q)
```

This is the final lecture complexity idea.

---

## 19.1 Without caching across queries

If we clear the DP for every query:

```text
Q × O(N × MAX_T)
```

giving:

```text
O(Q × N × MAX_T)
```

---

## 19.2 With caching across queries

Build states lazily and reuse:

```text
O(N × MAX_T + Q)
```

This can be a huge improvement.

---

# 20. C++17 — Caching Across Queries

```cpp
#include <bits/stdc++.h>
using namespace std;

const int MAX_N = 105;
const int MAX_SUM = 10005;

int n;
int value[MAX_N];
int dp[MAX_N][MAX_SUM];

int rec(int level, int sumLeft) {
    // 1. Pruning
    if (sumLeft < 0) return 0;

    // 2. Base Case
    if (level == n + 1) return sumLeft == 0;

    // 3. Cache Check
    int &ans = dp[level][sumLeft];
    if (ans != -1) return ans;

    // 4. Compute / Transition
    int notTake = rec(level + 1, sumLeft);
    int take = rec(level + 1, sumLeft - value[level]);

    ans = notTake || take;

    // 5. Save and Return
    return ans;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n;

    for (int i = 1; i <= n; i++) {
        cin >> value[i];
    }

    memset(dp, -1, sizeof(dp));

    int q;
    cin >> q;

    while (q--) {
        int target;
        cin >> target;

        cout << rec(1, target) << '\n';
    }

    return 0;
}
```

---

# 21. Why `memset(dp,-1)` Must Be Outside the Query Loop

Correct:

```cpp
memset(dp, -1, sizeof(dp));

while (q--) {
    cin >> target;
    cout << rec(1, target) << '\n';
}
```

This preserves memoized states between queries.

---

## Wrong

```cpp
while (q--) {
    cin >> target;

    memset(dp, -1, sizeof(dp));

    cout << rec(1, target) << '\n';
}
```

This destroys all previous work.

Then every query starts from zero again.

Complexity goes back toward:

```text
O(Q × N × MAX_T)
```

So the whole purpose of the lecture is lost.

---

# 22. Comparison — `sumTaken` vs `sumLeft`

| Design | State | Depends on current query target? | Reusable across queries? |
|---|---|---:|---:|
| First idea | `dp(level, sumTaken)` | Yes, through hidden `T` | No |
| Better design | `dp(level, sumLeft)` | No hidden dependency | Yes |

---

## 22.1 First design

```text
rec(level, sumTaken)
```

Base:

```text
return sumTaken == T
```

Problem:

```text
T is external hidden state
```

If `T` changes:

```text
cached values may become wrong
```

---

## 22.2 Better design

```text
rec(level, sumLeft)
```

Base:

```text
return sumLeft == 0
```

No global target is needed.

The query only chooses:

```text
initial sumLeft
```

So:

```text
cache is universal
```

for all queries within the same item array.

---

# 23. State Design Lesson

This lecture is really about more than subset sum.

The general lesson is:

```text
A memoized state is valid only when
its parameters uniquely determine
the future subproblem.
```

If the answer also depends on a changing global variable:

```text
your state is incomplete
```

---

## 23.1 Diagnostic question

Ask:

```text
If two calls have identical DP indices,
can they return different answers
because some external value changed?
```

If:

```text
YES
```

then either:

```text
1. Add that value to the state

or

2. Redesign the state so it is absorbed
   into existing parameters
```

The lecture chooses option 2.

---

## 23.2 Hidden global state is dangerous

Bad memoization pattern:

```cpp
int target;

int rec(int level, int sumTaken) {
    ...
    if (level == n + 1) {
        return sumTaken == target;
    }
}
```

If `target` changes between queries while `dp` remains cached:

```text
same dp[level][sumTaken]
may now mean a different problem
```

---

## 23.3 Better state

```text
sumLeft = target - sumTaken
```

Now target information is embedded directly in the state.

This is a very useful DP transformation.

---

# 24. Common Mistakes

## Mistake 1 — Clearing cache for every query

This removes the benefit of cross-query caching.

---

## Mistake 2 — Reusing `sumTaken` DP when `T` changes

If target is a hidden global dependency, cached answers are not safe.

---

## Mistake 3 — Wrong base case

For `sumLeft` state:

```text
valid iff sumLeft == 0
```

not:

```text
sumTaken == target
```

because `sumTaken` is no longer in the state.

---

## Mistake 4 — Wrong take transition

Correct:

```text
take = rec(level + 1, sumLeft - value[level])
```

Taking an item reduces the remaining required sum.

---

## Mistake 5 — Forgetting negative pruning

With positive item values:

```text
sumLeft < 0
```

can never return to zero.

So prune immediately.

---

## Mistake 6 — Complexity per query instead of total

With shared memoization:

```text
states are computed once globally
```

not once per query.

Final complexity:

```text
O(N × MAX_T + Q)
```

---

# 25. Contest Checklist

For multiple-query DP:

---

## Step 1 — Look for the Form

Here:

```text
subset
→ take / not take
```

---

## Step 2 — Decide State & Meaning

First attempt:

```text
dp(level, sumTaken)
```

Then ask:

```text
Does this state fully determine the future
for every query?
```

No.

Redesign:

```text
dp(level, sumLeft)
```

Meaning:

```text
Can items [level ... N]
form exactly sumLeft?
```

---

## Step 3 — Decide Transitions

```text
notTake = rec(level + 1, sumLeft)
```

```text
take = rec(level + 1, sumLeft - value[level])
```

```text
answer = notTake || take
```

---

## Step 4 — Check Complexity

```text
states = N × MAX_T

work/state = O(1)

queries = Q
```

So:

```text
O(N × MAX_T + Q)
```

---

## Step 5 — Code

Always:

```text
Pruning
Base Case
Cache Check
Compute / Transition
Save and Return
```

And:

```text
initialize memo table once
outside the query loop
```

---

# 26. Final Revision Box

```text
DP CACHING ACROSS QUERIES
────────────────────────────────────

Problem:

N values

For each query T:

Does any subset sum to T?


FORM
────────────────────────────────────

Take / Not Take


FIRST STATE
────────────────────────────────────

dp(level, sumTaken)

Problem:

answer also depends on T

T changes across queries

So cached state is not universal.


HIDDEN STATE
────────────────────────────────────

Real dependency was:

(level, sumTaken, T)


BETTER STATE
────────────────────────────────────

dp(level, sumLeft)

=

Can items [level ... N]
create exactly sumLeft?


INITIAL CALL
────────────────────────────────────

rec(1, T)


NOT TAKE
────────────────────────────────────

notTake = rec(level + 1, sumLeft)


TAKE
────────────────────────────────────

take = rec(level + 1, sumLeft - value[level])


ANSWER
────────────────────────────────────

answer = notTake || take


PRUNING
────────────────────────────────────

if (sumLeft < 0)
    return 0


BASE CASE
────────────────────────────────────

if (level == N + 1)
    return sumLeft == 0


CACHE
────────────────────────────────────

dp[level][sumLeft]


IMPORTANT
────────────────────────────────────

Initialize dp ONCE.

Do not clear it for every query.


COMPLEXITY
────────────────────────────────────

States:

O(N × MAX_T)

Queries:

Q

Total:

O(N × MAX_T + Q)


CORE STATE-DESIGN LESSON
────────────────────────────────────

A DP state must include
all information that can change
the future answer.

If a changing global variable
affects the result:

either

add it to the state

or

redesign the state
so the dependency disappears.
```
