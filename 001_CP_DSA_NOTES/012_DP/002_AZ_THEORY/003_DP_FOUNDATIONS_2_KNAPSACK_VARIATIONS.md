# 014 — DP Foundations 2: Knapsack Variations
## How Small Problem Changes Modify State, Transition, Base Case, and Complexity

> **Main goal:** Learn how to modify a known DP when the problem adds one extra condition.
>
> The lecture starts from **0/1 Knapsack** and asks several **“What if...?”** questions:
>
> 1. What if we must **print the chosen solution**?
> 2. What if **0/1 becomes 0/∞** — an item can be taken unlimited times?
> 3. What if **at most K items** can be taken?
> 4. What if the **sum of selected weights must be divisible by M**?
> 5. What if taking item `i` means we **cannot take item `i+1`**?
>
> The key lesson is:
>
> ```text
> Problem condition changes
>         ↓
> Ask what extra information affects the future
>         ↓
> Modify STATE / TRANSITION / BASE CASE
> ```
>
> ---
>
> ## Fixed DP Study Framework — use this for future notes
>
> **1. Prerequisites**  
> **2. Decide the Form**  
> **3. State Space Formulation**  
> **4. Transition Design**  
> **5. Time Complexity**  
> **6. C++17 Code**
>
> ---
>
> ## Fixed Recursive Code Structure
>
> ```text
> 1. Pruning
> 2. Base Case
> 3. Cache Check
> 4. Compute / Transition
> 5. Save and Return
> ```
>
> All C++ code below follows the compact visual format used in your lecture screenshots.

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Baseline — 0/1 Knapsack Recap](#2-baseline--01-knapsack-recap)
3. [The Core DP Modification Rule](#3-the-core-dp-modification-rule)
4. [Variation 1 — Print the Chosen Solution](#4-variation-1--print-the-chosen-solution)
5. [Variation 2 — 0/1 to 0/∞ Knapsack](#5-variation-2--01-to-0-knapsack)
6. [Unlimited Knapsack — First Transition](#6-unlimited-knapsack--first-transition)
7. [Unlimited Knapsack — Transition Optimization](#7-unlimited-knapsack--transition-optimization)
8. [Variation 3 — At Most K Items](#8-variation-3--at-most-k-items)
9. [Variation 4 — Selected Weight Divisible by M](#9-variation-4--selected-weight-divisible-by-m)
10. [Variation 5 — Cannot Take Adjacent Items](#10-variation-5--cannot-take-adjacent-items)
11. [Combining Multiple Conditions](#11-combining-multiple-conditions)
12. [How to Decide Whether a New State Is Needed](#12-how-to-decide-whether-a-new-state-is-needed)
13. [Complexity Pattern](#13-complexity-pattern)
14. [Final Comparison Table](#14-final-comparison-table)
15. [Contest Checklist](#15-contest-checklist)
16. [Final Revision Box](#16-final-revision-box)

---

# 1. Prerequisites

Before these variations, be comfortable with the basic 0/1 Knapsack state.

---

## 1.1 Baseline problem

We have:

```text
N items

weight[i]
value[i]

bag capacity = W
```

Each item can be used:

```text
0 times
or
1 time
```

Goal:

```text
maximize total value
while total selected weight <= W
```

---

## 1.2 Baseline state

Define:

```text
rec(i, x)
=
maximum value obtainable
using items [i ... N-1]

when remaining capacity is x
```

State parameters:

```text
i → current item

x → weight/capacity left
```

---

## 1.3 Baseline choices

At item `i`:

```text
1. Do not take it

2. Take it
   if weight[i] <= x
```

Transition:

```text
notTake =
rec(i + 1, x)

take =
value[i]
+
rec(i + 1, x - weight[i])

answer =
max(notTake, take)
```

---

## 1.4 Baseline complexity

States:

```text
N × (W + 1)
```

Transitions per state:

```text
2
```

Therefore:

```text
Time  = O(NW)
Space = O(NW)
```

---

# 2. Baseline — 0/1 Knapsack Recap

Use this as the reference implementation.

```cpp
#include <bits/stdc++.h>
using namespace std;

const int NEG_INF = -1e9;

int n, W;
vector<int> weightValue, itemValue;
vector<vector<int>> dp;

int rec(int i, int x) {
    // 1. Pruning

    // 2. Base Case
    if (i == n) return 0;

    // 3. Cache Check
    int &ans = dp[i][x];
    if (ans != -1) return ans;

    // 4. Compute / Transition
    int notTake = rec(i + 1, x);
    int take = NEG_INF;

    if (weightValue[i] <= x) {
        take = itemValue[i] + rec(i + 1, x - weightValue[i]);
    }

    ans = max(notTake, take);

    // 5. Save and Return
    return ans;
}
```

The important question for every new condition is:

```text
Does this condition change:

STATE?
TRANSITION?
BASE CASE?
or only answer reconstruction?
```

---

# 3. The Core DP Modification Rule

When a problem changes, do **not** immediately create another DP dimension.

Ask:

```text
What extra information is required
to make the next decision correctly?
```

There are three possibilities.

---

## Case A — No new state needed

Example:

```text
Need to print the chosen items
```

The optimal value DP is unchanged.

We reconstruct the decisions afterward.

---

## Case B — Existing transition changes

Example:

```text
An item may be used unlimited times
```

The state can remain:

```text
rec(i, x)
```

but the **take** transition changes.

---

## Case C — New information truly affects future decisions

Example:

```text
At most K items may be selected
```

Now future decisions depend on:

```text
how many item slots remain
```

So add:

```text
kLeft
```

to the state.

---

# 4. Variation 1 — Print the Chosen Solution

The lecture's first “What if?” is:

```text
Print Solution
```

The normal DP gives only:

```text
maximum value
```

Now we also want:

```text
which items were selected?
```

---

## 4.1 Decide the Form

Still:

```text
Choose a subset
```

Each item has:

```text
Take
or
Not Take
```

No change to the original optimization problem.

---

## 4.2 State Space Formulation

The DP state is unchanged:

```text
rec(i, x)
=
maximum value obtainable
from items [i ... N-1]
with x capacity left
```

We do **not** need a new DP dimension only to print the answer.

---

## 4.3 Transition Design

At state:

```text
(i, x)
```

recompute the two values:

```text
notTake =
rec(i + 1, x)

take =
value[i]
+
rec(i + 1, x - weight[i])
```

Then compare them.

If:

```text
take > notTake
```

the optimal solution takes item `i`.

Otherwise:

```text
skip item i
```

This matches the lecture's separate `generate(i, x)` reconstruction function.

---

## 4.4 Reconstruction Logic

```text
generate(i, x)

if i == N:
    stop

notTake = rec(i+1, x)

if item fits:
    take = value[i] + rec(i+1, x-weight[i])

if take is better:
    record i
    generate(i+1, x-weight[i])

else:
    generate(i+1, x)
```

---

## 4.5 Time Complexity

DP calculation:

```text
O(NW)
```

Reconstruction follows only one path through the items:

```text
O(N)
```

Total remains:

```text
O(NW)
```

Space:

```text
O(NW)
```

plus:

```text
O(N)
```

for the chosen indices.

---

## 4.6 C++17 Code

```cpp
#include <bits/stdc++.h>
using namespace std;

const int NEG_INF = -1e9;

int n, W;
vector<int> weightValue, itemValue, solution;
vector<vector<int>> dp;

int rec(int i, int x) {
    // 1. Pruning

    // 2. Base Case
    if (i == n) return 0;

    // 3. Cache Check
    int &ans = dp[i][x];
    if (ans != -1) return ans;

    // 4. Compute / Transition
    int notTake = rec(i + 1, x);
    int take = NEG_INF;

    if (weightValue[i] <= x) {
        take = itemValue[i] + rec(i + 1, x - weightValue[i]);
    }

    ans = max(notTake, take);

    // 5. Save and Return
    return ans;
}

void generate(int i, int x) {
    // 1. Pruning

    // 2. Base Case
    if (i == n) return;

    // 3. Cache Check
    // rec() already contains cached answers.

    // 4. Compute / Transition
    int notTake = rec(i + 1, x);
    int take = NEG_INF;

    if (weightValue[i] <= x) {
        take = itemValue[i] + rec(i + 1, x - weightValue[i]);
    }

    if (take > notTake) {
        solution.push_back(i);
        generate(i + 1, x - weightValue[i]);
    } else {
        generate(i + 1, x);
    }

    // 5. Save and Return
}
```

---

## 4.7 Key Lesson

```text
Finding the optimal VALUE
and
constructing the optimal SOLUTION

are two separate jobs.
```

DP:

```text
calculates best value
```

`generate()`:

```text
walks through the optimal decisions
```

---

# 5. Variation 2 — 0/1 to 0/∞ Knapsack

The lecture's second variation is:

```text
0-1
→
0-∞
```

Meaning:

```text
each item can now be taken
0, 1, 2, 3, ... times
```

This is **unbounded Knapsack**.

---

## 5.1 Decide the Form

We are still:

```text
choosing quantities of items
```

But now the decision for one item is no longer:

```text
Take / Not Take
```

It can be:

```text
take 0 copies
take 1 copy
take 2 copies
...
```

subject to capacity.

---

## 5.2 State Space Formulation

We can still use:

```text
rec(i, x)
=
maximum value obtainable
using item types [i ... N-1]

with x capacity remaining
```

No new state variable is required.

Why?

Because the current item can be taken again by staying at the same:

```text
i
```

after taking it.

---

# 6. Unlimited Knapsack — First Transition

The lecture first analyzes the direct transition.

For item `i`:

```text
weight = weight[i]
value  = value[i]
```

Number of copies we may take:

```text
t = 0 ... floor(x / weight[i])
```

For each `t`:

```text
candidate
=
t * value[i]
+
rec(i + 1, x - t * weight[i])
```

So:

```text
rec(i, x)
=
max over all valid t
```

---

## 6.1 Transition

```text
answer = 0

for t = 0 ... x / weight[i]:

    answer =
    max(
        answer,
        t * value[i]
        +
        rec(i + 1, x - t * weight[i])
    )
```

---

## 6.2 Time Complexity

States:

```text
N × W
```

Per state, the number of possible copies may be:

```text
O(W)
```

Therefore worst-case:

```text
O(NW²)
```

This is the complexity shown in the lecture before transition optimization.

---

# 7. Unlimited Knapsack — Transition Optimization

The lecture then makes an important observation.

Instead of choosing:

```text
0,1,2,3,... copies
```

all at once, use only two transitions.

---

## 7.1 Do not take current item

Move to next item:

```text
notTake =
rec(i + 1, x)
```

---

## 7.2 Take one copy

Take one copy:

```text
gain value[i]
```

Capacity becomes:

```text
x - weight[i]
```

But because the item can be used again:

```text
stay at index i
```

So:

```text
take =
value[i]
+
rec(i, x - weight[i])
```

Notice the critical difference:

### 0/1 Knapsack

```text
take
→ rec(i + 1, ...)
```

### Unbounded Knapsack

```text
take
→ rec(i, ...)
```

---

## 7.3 Optimized Transition

```text
notTake =
rec(i + 1, x)

take =
value[i]
+
rec(i, x - weight[i])

answer =
max(notTake, take)
```

---

## 7.4 Time Complexity

States:

```text
N × W
```

Transitions per state:

```text
2
```

Therefore:

```text
Time  = O(NW)
Space = O(NW)
```

We improved:

```text
O(NW²)
```

to:

```text
O(NW)
```

by redesigning the transition.

---

## 7.5 C++17 Code

```cpp
int rec(int i, int x) {
    // 1. Pruning

    // 2. Base Case
    if (i == n) return 0;

    // 3. Cache Check
    int &ans = dp[i][x];
    if (ans != -1) return ans;

    // 4. Compute / Transition
    int notTake = rec(i + 1, x);
    int take = NEG_INF;

    if (weightValue[i] <= x) {
        take = itemValue[i] + rec(i, x - weightValue[i]);
    }

    ans = max(notTake, take);

    // 5. Save and Return
    return ans;
}
```

---

# 8. Variation 3 — At Most K Items

The lecture's third variation:

```text
max K items can be taken
```

Now total selected items must satisfy:

```text
countSelected <= K
```

---

## 8.1 Decide the Form

Still:

```text
Choose a subset
```

Each item:

```text
Take
or
Not Take
```

But now we have an extra resource:

```text
number of items we may still select
```

---

## 8.2 State Space Formulation

Add:

```text
itemLeft
```

Define:

```text
rec(i, x, itemLeft)
=
maximum value obtainable
from items [i ... N-1]

with:
x capacity left

and at most
itemLeft more items
allowed
```

The lecture code shows this extra parameter directly.

---

## 8.3 Transition Design

### Do not take

```text
notTake =
rec(i + 1, x, itemLeft)
```

### Take

Allowed only when:

```text
weight[i] <= x
and
itemLeft > 0
```

Then:

```text
take =
value[i]
+
rec(
    i + 1,
    x - weight[i],
    itemLeft - 1
)
```

Final:

```text
answer =
max(notTake, take)
```

---

## 8.4 Base Case

If:

```text
i == N
```

no items remain:

```text
return 0
```

Also, if:

```text
itemLeft == 0
```

we cannot take any more items.

We may return:

```text
0
```

immediately.

---

## 8.5 Time Complexity

State dimensions:

```text
i        → N
x        → W + 1
itemLeft → K + 1
```

States:

```text
O(NWK)
```

Transitions per state:

```text
2
```

Therefore:

```text
Time  = O(NWK)
Space = O(NWK)
```

This matches the `dp(i, x, kLeft)` state shown in the lecture.

---

## 8.6 C++17 Code

```cpp
int rec(int i, int x, int itemLeft) {
    // 1. Pruning
    if (itemLeft < 0) return NEG_INF;

    // 2. Base Case
    if (i == n || itemLeft == 0) return 0;

    // 3. Cache Check
    int &ans = dp[i][x][itemLeft];
    if (ans != -1) return ans;

    // 4. Compute / Transition
    int notTake = rec(i + 1, x, itemLeft);
    int take = NEG_INF;

    if (weightValue[i] <= x) {
        take = itemValue[i]
             + rec(i + 1, x - weightValue[i], itemLeft - 1);
    }

    ans = max(notTake, take);

    // 5. Save and Return
    return ans;
}
```

---

# 9. Variation 4 — Selected Weight Divisible by M

The lecture's next condition is:

```text
sum of weights of selected items
must be divisible by M
```

The important insight is that we may **not need another state dimension**.

---

## 9.1 Decide the Form

Still:

```text
0/1 subset selection
```

But only solutions whose selected total weight satisfies:

```text
selectedWeight % M == 0
```

are valid.

---

## 9.2 State Space Formulation

Suppose original capacity is:

```text
W
```

and current remaining capacity is:

```text
x
```

Then total weight already selected is:

```text
selectedWeight
=
W - x
```

So the selected weight is already derivable from:

```text
x
```

We do not need to store:

```text
selectedWeight
```

as another parameter.

This is an important **state elimination** idea.

---

## 9.3 Transition Design

The take/not-take transition stays the same.

```text
notTake =
rec(i + 1, x)

take =
value[i]
+
rec(i + 1, x - weight[i])
```

The divisibility condition is checked when the selection is complete.

---

## 9.4 Base Case

At:

```text
i == N
```

calculate:

```text
selectedWeight = W - x
```

If:

```text
selectedWeight % M == 0
```

this is a valid completed selection:

```text
return 0
```

Otherwise:

```text
return -INF
```

Why `-INF`?

Because we are maximizing.

An invalid solution must never beat a valid one.

---

## 9.5 C++17 Code

```cpp
int rec(int i, int x) {
    // 1. Pruning

    // 2. Base Case
    if (i == n) {
        int selectedWeight = W - x;

        if (selectedWeight % M == 0) {
            return 0;
        }

        return NEG_INF;
    }

    // 3. Cache Check
    int &ans = dp[i][x];
    if (ans != -1) return ans;

    // 4. Compute / Transition
    int notTake = rec(i + 1, x);
    int take = NEG_INF;

    if (weightValue[i] <= x) {
        int next = rec(i + 1, x - weightValue[i]);

        if (next != NEG_INF) {
            take = itemValue[i] + next;
        }
    }

    ans = max(notTake, take);

    // 5. Save and Return
    return ans;
}
```

---

## 9.6 Time Complexity

No new state dimension was added.

Therefore:

```text
Time  = O(NW)
Space = O(NW)
```

The modulo check costs:

```text
O(1)
```

at the base case.

---

## 9.7 Important Recognition

Before adding:

```text
dp[i][x][sum]
```

ask:

```text
Can sum be reconstructed
from information already in the state?
```

Here:

```text
selectedWeight = W - x
```

So the extra dimension is unnecessary.

---

# 10. Variation 5 — Cannot Take Adjacent Items

The lecture's last condition says:

```text
if you take item i,
you cannot take item i+1
```

This means future eligibility depends on the previous decision.

---

## 10.1 Decide the Form

Still:

```text
take / not take
```

But taking one item changes whether the next item may be taken.

So the future needs one extra piece of information:

```text
Was the previous item taken?
```

---

## 10.2 State Space Formulation

The lecture later shows a state including:

```text
lastTaken
```

Define:

```text
rec(i, x, lastTaken)
=
maximum value from items [i ... N-1]

with x capacity left

where:
lastTaken = 1
means item i-1 was selected
```

State range:

```text
lastTaken ∈ {0,1}
```

---

## 10.3 Transition Design

### Not take item `i`

Always allowed:

```text
notTake =
rec(i + 1, x, 0)
```

Because current item is not selected, the next item is free to be selected.

---

### Take item `i`

Allowed only if:

```text
lastTaken == 0
```

and:

```text
weight[i] <= x
```

Then:

```text
take =
value[i]
+
rec(
    i + 1,
    x - weight[i],
    1
)
```

---

## 10.4 Final Transition

```text
answer =
max(notTake, take)
```

with `take` available only when:

```text
lastTaken == 0
```

---

## 10.5 Time Complexity

States:

```text
N × W × 2
```

Therefore:

```text
Time  = O(NW)
Space = O(NW)
```

The factor `2` is constant.

---

## 10.6 C++17 Code

```cpp
int rec(int i, int x, int lastTaken) {
    // 1. Pruning

    // 2. Base Case
    if (i == n) return 0;

    // 3. Cache Check
    int &ans = dp[i][x][lastTaken];
    if (ans != -1) return ans;

    // 4. Compute / Transition
    int notTake = rec(i + 1, x, 0);
    int take = NEG_INF;

    if (!lastTaken && weightValue[i] <= x) {
        take = itemValue[i]
             + rec(i + 1, x - weightValue[i], 1);
    }

    ans = max(notTake, take);

    // 5. Save and Return
    return ans;
}
```

---

## 10.7 Alternative Form

There is another equivalent way:

If we take `i`, directly jump:

```text
i + 2
```

Then:

```text
take =
value[i]
+
rec(i + 2, x - weight[i])
```

This avoids `lastTaken`.

However, the lecture screenshots explicitly show the more general:

```text
lastTaken
```

state, which is useful when several conditions are combined.

---

# 11. Combining Multiple Conditions

Near the end, the lecture code shows a combined state with parameters resembling:

```text
rec(
    i,
    x,
    itemLeft,
    lastTaken
)
```

and a DP table containing dimensions for:

```text
i
x
itemLeft
lastTaken
```

It also checks the divisibility condition at the base case using:

```text
W - x
```

This demonstrates a very important lesson:

```text
Every INDEPENDENT condition
that affects future choices
may add one state parameter.

But a condition that can be derived
from existing state
does not need a new dimension.
```

---

## 11.1 Combined State Meaning

```text
rec(i, x, itemLeft, lastTaken)
=
maximum value obtainable
from items [i ... N-1]

subject to:

x
=
remaining capacity

itemLeft
=
maximum number of additional items
we may select

lastTaken
=
whether item i-1 was selected
```

At the end, validity also requires:

```text
(W - x) % M == 0
```

---

## 11.2 Combined Transition

### Skip current item

```text
notTake =
rec(
    i + 1,
    x,
    itemLeft,
    0
)
```

### Take current item

Allowed only if:

```text
weight[i] <= x

itemLeft > 0

lastTaken == 0
```

Then:

```text
take =
value[i]
+
rec(
    i + 1,
    x - weight[i],
    itemLeft - 1,
    1
)
```

---

## 11.3 Combined Base Case

When:

```text
i == N
```

compute:

```text
selectedWeight = W - x
```

Valid only when:

```text
selectedWeight % M == 0
```

Return:

```text
0
```

if valid.

Otherwise:

```text
NEG_INF
```

---

## 11.4 Combined Complexity

State count:

```text
N
× W
× K
× 2
```

Therefore:

```text
O(NWK)
```

ignoring the constant `2`.

This matches the lecture idea of extending:

```text
dp(i,x)
```

to:

```text
dp(i,x,kLeft,...)
```

only when the new condition genuinely changes the future.

---

## 11.5 Combined C++17 Code

```cpp
int rec(int i, int x, int itemLeft, int lastTaken) {
    // 1. Pruning
    if (itemLeft < 0) return NEG_INF;

    // 2. Base Case
    if (i == n) {
        int selectedWeight = W - x;

        if (selectedWeight % M == 0) {
            return 0;
        }

        return NEG_INF;
    }

    // 3. Cache Check
    int &ans = dp[i][x][itemLeft][lastTaken];
    if (ans != UNVISITED) return ans;

    // 4. Compute / Transition
    int notTake = rec(i + 1, x, itemLeft, 0);
    int take = NEG_INF;

    if (!lastTaken && itemLeft > 0 && weightValue[i] <= x) {
        int next = rec(
            i + 1,
            x - weightValue[i],
            itemLeft - 1,
            1
        );

        if (next != NEG_INF) {
            take = itemValue[i] + next;
        }
    }

    ans = max(notTake, take);

    // 5. Save and Return
    return ans;
}
```

---

# 12. How to Decide Whether a New State Is Needed

This is the main lesson of the lecture.

For every new condition, ask:

```text
If two recursive calls have the same
current DP state parameters,

can their future answers still differ
because of this new condition?
```

---

## Example 1 — Print solution

Does printing affect the future optimal value?

```text
No
```

So:

```text
no new state
```

---

## Example 2 — Unlimited copies

Does the future require extra history?

```text
No
```

We can change:

```text
take → stay at i
```

So:

```text
same state dimensions
```

---

## Example 3 — At most K items

Can two states with same:

```text
i, x
```

but different remaining item counts have different answers?

```text
Yes
```

So add:

```text
itemLeft
```

---

## Example 4 — Weight divisible by M

Do we need to separately store selected total weight?

```text
No
```

because:

```text
selectedWeight = W - x
```

It is derivable.

---

## Example 5 — No adjacent items

Can two states with same:

```text
i, x
```

behave differently depending on whether `i-1` was taken?

```text
Yes
```

So add:

```text
lastTaken
```

---

# 13. Complexity Pattern

For memoized DP:

```text
Time
=
Number of States
×
Transitions per State
```

Use this after every modification.

---

## Baseline

```text
State:
(i, x)

#states:
N × W

transitions:
2

Time:
O(NW)
```

---

## Unlimited — naive copies loop

```text
State:
(i, x)

#states:
N × W

transitions/state:
O(W)

Time:
O(NW²)
```

---

## Unlimited — optimized

```text
State:
(i, x)

#states:
N × W

transitions:
2

Time:
O(NW)
```

---

## At most K items

```text
State:
(i, x, kLeft)

#states:
N × W × K

transitions:
2

Time:
O(NWK)
```

---

## No adjacent item selection

```text
State:
(i, x, lastTaken)

#states:
N × W × 2

transitions:
2

Time:
O(NW)
```

---

## Combined

```text
State:
(i, x, kLeft, lastTaken)

#states:
N × W × K × 2

Time:
O(NWK)
```

---

# 14. Final Comparison Table

| Variation | State | Main Change | Time |
|---|---|---|---:|
| Basic 0/1 | `rec(i,x)` | Take / skip | `O(NW)` |
| Print solution | `rec(i,x)` | Add reconstruction | `O(NW)` |
| Unlimited — naive | `rec(i,x)` | Try `t` copies | `O(NW²)` |
| Unlimited — optimized | `rec(i,x)` | Take and stay at `i` | `O(NW)` |
| At most K items | `rec(i,x,kLeft)` | Add item count | `O(NWK)` |
| Weight divisible by M | `rec(i,x)` | Validate `W-x` at base | `O(NW)` |
| No adjacent items | `rec(i,x,lastTaken)` | Add previous-choice state | `O(NW)` |
| Combined constraints | `rec(i,x,kLeft,lastTaken)` | Combine independent state info | `O(NWK)` |

---

# 15. Contest Checklist

When a familiar DP problem gets one extra condition:

```text
DO NOT rewrite everything immediately.
```

Use this sequence.

---

## Step 1 — Decide Form

Ask:

```text
Is this still:

Take / Skip?
Choose subset?
Choose count?
Choose next?
```

---

## Step 2 — State Space Formulation

For every condition ask:

```text
Does the future depend on this information?
```

If yes:

```text
add state
```

If it can be derived:

```text
do not add state
```

---

## Step 3 — Transition Design

Write the choices clearly.

Example:

```text
notTake = ...

take = ...

answer = max(notTake, take)
```

For unlimited items:

```text
TAKE keeps the same item index
```

---

## Step 4 — Time Complexity

Always calculate:

```text
#states
×
#transitions per state
```

Do this **before coding**.

---

## Step 5 — Code Structure

Every memoized recursion should visually follow:

```cpp
int rec(...) {
    // 1. Pruning

    // 2. Base Case

    // 3. Cache Check

    // 4. Compute / Transition

    // 5. Save and Return
}
```

This makes DP code easier to debug and review.

---

# 16. Final Revision Box

```text
DP FOUNDATIONS 2
────────────────────────────────────

Start from a known DP.

Then ask:

"What changed?"


FIXED DP FRAMEWORK
────────────────────────────────────

1. Decide the Form

2. State Space Formulation

3. Transition Design

4. Time Complexity

5. Code


FIXED CODE STRUCTURE
────────────────────────────────────

1. Pruning

2. Base Case

3. Cache Check

4. Compute / Transition

5. Save and Return


PRINT SOLUTION
────────────────────────────────────

DP state does not change.

Use a second function:

generate(i, x)

Compare:

take
vs
notTake

Follow the optimal branch.


0/1 → 0/∞
────────────────────────────────────

0/1 take:

rec(i+1, x-w[i])

Unlimited take:

rec(i, x-w[i])

Stay at i
because item i may be used again.


UNLIMITED COMPLEXITY
────────────────────────────────────

Try all copy counts:
O(NW²)

Optimized 2 transitions:
O(NW)


MAX K ITEMS
────────────────────────────────────

Need:

itemLeft

State:

rec(i, x, itemLeft)

Time:

O(NWK)


WEIGHT SUM DIVISIBLE BY M
────────────────────────────────────

Selected weight:

W - x

So no new state needed.

Base:

if ((W-x) % M == 0)
    valid
else
    invalid


NO ADJACENT ITEMS
────────────────────────────────────

Need to know:

Was previous item taken?

State:

rec(i, x, lastTaken)


CORE STATE-DESIGN QUESTION
────────────────────────────────────

"If two calls have the same
current state parameters,

can their future answer differ
because of this condition?"

YES
→ add state

NO
→ do not add state


MOST IMPORTANT DP RULE
────────────────────────────────────

Store only INDEPENDENT information
that changes future decisions.
```
