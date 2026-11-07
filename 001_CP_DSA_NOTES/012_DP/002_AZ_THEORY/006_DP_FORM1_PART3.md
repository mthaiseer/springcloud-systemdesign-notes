# 017 — Printing Solutions of DP Questions
## Subset Sum Reconstruction by Tracing DP Transitions

> **Main lecture idea:**
>
> ```text
> DP tells us whether a solution exists.
>
> To PRINT one solution:
> trace the transitions that lead to the final valid value.
> ```
>
> The lecture describes this as:
>
> ```text
> Trace the transitions
> that lead to the final value
>
> → Recheck
> → Backtrack / Backpropagate through DP states
> ```
>
> This note continues directly from:
>
> ```text
> dp(level, sumLeft)
> ```
>
> used for multi-query Subset Sum.
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
> **Formatting rule:** keep simple transitions on one readable line.
>
> ```cpp
> take = rec(level + 1, sumLeft - value[level]);
> ```

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Problem Statement](#2-problem-statement)
3. [What Changes from the Previous Lecture](#3-what-changes-from-the-previous-lecture)
4. [Step 1 — Decide the Form](#4-step-1--decide-the-form)
5. [Step 2 — State Space Formulation](#5-step-2--state-space-formulation)
6. [Step 3 — Transition Design](#6-step-3--transition-design)
7. [Subset-Sum DP Recap](#7-subset-sum-dp-recap)
8. [New Goal — Print One Valid Subset](#8-new-goal--print-one-valid-subset)
9. [Core Reconstruction Idea](#9-core-reconstruction-idea)
10. [How to Decide Which Transition Was Used](#10-how-to-decide-which-transition-was-used)
11. [Reconstruction Function](#11-reconstruction-function)
12. [Dry Run — Target 16](#12-dry-run--target-16)
13. [Why Rechecking Is Cheap](#13-why-rechecking-is-cheap)
14. [Step 4 — Time Complexity](#14-step-4--time-complexity)
15. [Complete C++17 Code](#15-complete-c17-code)
16. [Multiple Queries](#16-multiple-queries)
17. [When No Solution Exists](#17-when-no-solution-exists)
18. [Tie / Multiple-Solution Behavior](#18-tie--multiple-solution-behavior)
19. [Alternative — Store Parent Decisions](#19-alternative--store-parent-decisions)
20. [Common Mistakes](#20-common-mistakes)
21. [Contest Checklist](#21-contest-checklist)
22. [Final Revision Box](#22-final-revision-box)

---

# 1. Prerequisites

You should already know the previous subset-sum DP.

Given:

```text
N values:

x1, x2, ..., xN
```

For a target:

```text
T
```

we ask:

```text
Does any subset sum exactly to T?
```

The reusable multi-query state was:

```text
rec(level, sumLeft)
```

Meaning:

```text
Can we create exactly `sumLeft`
using values from [level ... N]?
```

---

## 1.1 Boolean answer

Each state returns:

```text
1 → possible
0 → impossible
```

---

## 1.2 Initial state

For query target:

```text
T
```

we start:

```text
rec(1, T)
```

---

## 1.3 Previous transition

At value:

```text
value[level]
```

we have:

```text
notTake = rec(level + 1, sumLeft)
```

and:

```text
take = rec(level + 1, sumLeft - value[level])
```

Then:

```text
answer = notTake || take
```

---

# 2. Problem Statement

Now the question changes slightly.

We still need to know:

```text
Does a subset summing to T exist?
```

But if it exists, we also want to:

```text
print one valid subset
```

Example:

```text
values = {1, 2, 5, 9}
T = 16
```

One valid subset is:

```text
{2, 5, 9}
```

because:

```text
2 + 5 + 9 = 16
```

The DP already knows that the answer is:

```text
YES
```

Now we must recover the decisions.

---

# 3. What Changes from the Previous Lecture

The DP state does **not** need to change.

We still use:

```text
rec(level, sumLeft)
```

The transition also does not change.

The new task is:

```text
after DP is computed,
follow one sequence of valid transitions
from the initial state
to the successful base state.
```

So printing is a **reconstruction phase**.

---

# 4. Step 1 — Decide the Form

The form remains:

```text
Choose a subset
```

At every item:

```text
TAKE
or
NOT TAKE
```

So printing the solution means printing which of these decisions were TAKE.

---

# 5. Step 2 — State Space Formulation

No change.

```text
rec(level, sumLeft)
=
1 if we can create exactly `sumLeft`
using items [level ... N]

otherwise 0
```

Initial state:

```text
rec(1, T)
```

Successful final state:

```text
sumLeft = 0
```

after processing enough items.

---

# 6. Step 3 — Transition Design

At:

```text
rec(level, sumLeft)
```

two transitions are possible.

---

## Not Take

```text
rec(level + 1, sumLeft)
```

---

## Take

```text
rec(level + 1, sumLeft - value[level])
```

---

## Boolean recurrence

```text
notTake = rec(level + 1, sumLeft)

take = rec(level + 1, sumLeft - value[level])

answer = notTake || take
```

The DP tells us:

```text
whether at least one outgoing transition
can eventually reach a valid final state
```

Printing asks:

```text
which outgoing transition should we follow?
```

---

# 7. Subset-Sum DP Recap

```cpp
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
```

This tells us:

```text
possible / impossible
```

but it does not directly print the subset.

---

# 8. New Goal — Print One Valid Subset

The lecture's new idea is:

```text
Trace the transitions
that lead to the final value.
```

Suppose:

```text
rec(level, sumLeft) = 1
```

Then at least one of these must also be valid:

```text
rec(level + 1, sumLeft)
```

or:

```text
rec(level + 1, sumLeft - value[level])
```

So we simply test them again.

Because `rec()` is memoized, these are usually just cache lookups.

---

# 9. Core Reconstruction Idea

At state:

```text
(level, sumLeft)
```

do this:

```text
1. Check NOT TAKE transition.

2. If it can still reach a solution:
      follow it.

3. Otherwise check TAKE transition.

4. If TAKE works:
      print value[level]
      reduce sumLeft
      follow the take state.
```

This is exactly the lecture's rechecking / tracing idea.

---

# 10. How to Decide Which Transition Was Used

Suppose:

```text
rec(level, sumLeft) = 1
```

First test:

```text
rec(level + 1, sumLeft)
```

If this returns:

```text
1
```

then a solution exists without taking the current item.

So:

```text
do not take
```

and move to:

```text
(level + 1, sumLeft)
```

---

## Otherwise

If:

```text
rec(level + 1, sumLeft) = 0
```

then skipping cannot lead to a solution.

Now test:

```text
rec(level + 1, sumLeft - value[level])
```

If it returns:

```text
1
```

then the current item must be used on this reconstruction path.

So:

```text
print value[level]
```

and move to:

```text
(level + 1, sumLeft - value[level])
```

---

# 11. Reconstruction Function

Lecture-style structure:

```cpp
void printSet(int level, int sumLeft) {
    // Base Case
    if (level == n + 1) return;

    // Find the correct transition
    if (rec(level + 1, sumLeft) == 1) {
        printSet(level + 1, sumLeft);
    } else if (rec(level + 1, sumLeft - value[level]) == 1) {
        cout << value[level] << ' ';
        printSet(level + 1, sumLeft - value[level]);
    }
}
```

Important:

```text
This function does not solve DP again.
```

It calls the already memoized `rec()` function to check which transition remains valid.

---

# 12. Dry Run — Target 16

Use:

```text
values = {1, 2, 5, 9}
T = 16
```

Start:

```text
printSet(1,16)
```

---

## Level 1 — value = 1

Check skip:

```text
rec(2,16)
```

Is 16 possible using:

```text
{2,5,9}?
```

Yes:

```text
2 + 5 + 9 = 16
```

So:

```text
skip 1
```

Move:

```text
printSet(2,16)
```

---

## Level 2 — value = 2

Check skip:

```text
rec(3,16)
```

Can:

```text
{5,9}
```

make:

```text
16?
```

No.

So check take:

```text
rec(3,14)
```

Can:

```text
{5,9}
```

make:

```text
14?
```

Yes.

Therefore:

```text
take 2
```

Print:

```text
2
```

Move:

```text
printSet(3,14)
```

---

## Level 3 — value = 5

Check skip:

```text
rec(4,14)
```

Can:

```text
{9}
```

make 14?

```text
No
```

Take:

```text
rec(4,9)
```

Yes.

Print:

```text
5
```

Move:

```text
printSet(4,9)
```

---

## Level 4 — value = 9

Check skip:

```text
rec(5,9)
```

No items remain and:

```text
sumLeft = 9
```

So:

```text
0
```

Take:

```text
rec(5,0)
```

Base case:

```text
sumLeft == 0
```

So:

```text
1
```

Print:

```text
9
```

---

## Final printed subset

```text
2 5 9
```

Check:

```text
2 + 5 + 9 = 16
```

Correct.

---

# 13. Why Rechecking Is Cheap

During reconstruction we repeatedly call:

```text
rec(...)
```

This may look like we are solving the DP again.

We are not.

Because the states have already been memoized:

```text
rec(state)
→ cache check
→ immediate return
```

So checking a transition is approximately:

```text
O(1)
```

after DP computation.

Reconstruction follows only one path through the levels.

Therefore:

```text
O(N)
```

per printed solution, excluding the unavoidable cost of output itself.

---

# 14. Step 4 — Time Complexity

Let:

```text
MAX_T
=
maximum target supported
```

The feasibility DP has:

```text
O(N × MAX_T)
```

states.

Each state has two constant-time transitions.

So across cached states:

```text
DP time = O(N × MAX_T)
```

---

## Reconstruction

For one query:

```text
at most one state per level
```

So:

```text
O(N)
```

for printing one solution.

---

## Multiple Q queries

If we only answer existence:

```text
O(N × MAX_T + Q)
```

With reconstruction for every successful query:

```text
O(N × MAX_T + QN)
```

in the worst case.

The actual printed output can also itself require:

```text
O(N)
```

values per query.

---

# 15. Complete C++17 Code

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

void printSet(int level, int sumLeft) {
    // 1. Pruning
    if (sumLeft < 0) return;

    // 2. Base Case
    if (level == n + 1) return;

    // 3. Cache Check
    // rec() already contains memoized answers.

    // 4. Compute / Transition
    if (rec(level + 1, sumLeft) == 1) {
        printSet(level + 1, sumLeft);
    } else if (rec(level + 1, sumLeft - value[level]) == 1) {
        cout << value[level] << ' ';
        printSet(level + 1, sumLeft - value[level]);
    }

    // 5. Save and Return
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

        if (rec(1, target)) {
            printSet(1, target);
            cout << '\n';
        } else {
            cout << "no solution\n";
        }
    }

    return 0;
}
```

---

# 16. Multiple Queries

The previous lecture showed that the same:

```text
dp[level][sumLeft]
```

cache can be shared across all targets.

So initialize once:

```cpp
memset(dp, -1, sizeof(dp));
```

before:

```cpp
while (q--) {
    ...
}
```

For every target:

```text
1. Check rec(1,target)

2. If true:
      printSet(1,target)

3. Otherwise:
      print "no solution"
```

---

# 17. When No Solution Exists

Before calling reconstruction:

```text
always check feasibility
```

Correct:

```cpp
if (rec(1, target)) {
    printSet(1, target);
} else {
    cout << "no solution\n";
}
```

If:

```text
rec(1,target) = 0
```

there is no valid path to trace.

So reconstruction should not be called.

---

# 18. Tie / Multiple-Solution Behavior

There may be many valid subsets.

Example:

```text
both NOT TAKE
and TAKE
can lead to answer 1
```

The lecture reconstruction checks:

```text
NOT TAKE first
```

So it prefers skipping the current element whenever skipping can still produce a solution.

Therefore:

```text
printSet()
prints one valid subset,
not necessarily every valid subset.
```

---

## 18.1 If you want to prefer TAKE

Reverse the checks:

```cpp
if (rec(level + 1, sumLeft - value[level]) == 1) {
    cout << value[level] << ' ';
    printSet(level + 1, sumLeft - value[level]);
} else if (rec(level + 1, sumLeft) == 1) {
    printSet(level + 1, sumLeft);
}
```

Both strategies are valid.

They may print different valid subsets.

---

# 19. Alternative — Store Parent Decisions

Another possible reconstruction method is to store:

```text
which transition was selected
```

for each DP state.

Example:

```text
parent[level][sumLeft]
=
0 → not take
1 → take
```

Then reconstruction follows `parent`.

But the lecture's method is simpler:

```text
recheck transitions using memoized DP
```

Advantages:

```text
no extra parent table
simple to implement
reuses existing rec()
```

---

# 20. Common Mistakes

## Mistake 1 — Recomputing DP without cache

Reconstruction should call the same memoized:

```text
rec(...)
```

function.

Otherwise transition rechecking can become expensive.

---

## Mistake 2 — Printing before knowing TAKE is valid

Wrong:

```cpp
cout << value[level];
printSet(level + 1, sumLeft - value[level]);
```

without checking:

```text
rec(level + 1, sumLeft - value[level]) == 1
```

---

## Mistake 3 — Calling printSet when no solution exists

Always check:

```text
rec(1,target)
```

first.

---

## Mistake 4 — Forgetting to update `sumLeft`

If item is taken:

```text
newSumLeft
=
sumLeft - value[level]
```

---

## Mistake 5 — Moving level incorrectly

Because this is 0/1 subset sum:

```text
every item can be used at most once
```

So both transitions move to:

```text
level + 1
```

---

## Mistake 6 — Expecting every valid solution

The basic `printSet()` follows one successful path.

It prints:

```text
one solution
```

not all solutions.

---

# 21. Contest Checklist

When a DP problem asks:

```text
Print / construct / recover one optimal or valid solution
```

use this process.

---

## Step 1 — Solve the DP normally

Find:

```text
best value
or
possible / impossible
```

---

## Step 2 — Start from the original state

Here:

```text
(level, sumLeft)
=
(1,T)
```

---

## Step 3 — Recheck outgoing transitions

For every candidate transition ask:

```text
Does this transition still lead
to the required DP answer?
```

---

## Step 4 — Follow one valid transition

If current item is taken:

```text
record / print it
```

Then move to the next DP state.

---

## Step 5 — Continue until base case

Eventually reach:

```text
sumLeft = 0
```

with no more needed contribution.

---

# 22. Final Revision Box

```text
PRINTING DP SOLUTIONS
────────────────────────────────────

DP computes:

possible / impossible

or

best value


PRINTING PHASE
────────────────────────────────────

Trace transitions
that lead to the final DP value.


SUBSET SUM STATE
────────────────────────────────────

rec(level, sumLeft)

=

Can items [level ... N]
create exactly sumLeft?


NOT TAKE
────────────────────────────────────

rec(level + 1, sumLeft)


TAKE
────────────────────────────────────

rec(level + 1, sumLeft - value[level])


RECONSTRUCTION
────────────────────────────────────

if NOT TAKE gives 1:

    follow NOT TAKE

else if TAKE gives 1:

    print current value
    follow TAKE


WHY RECHECKING IS FAST
────────────────────────────────────

rec() is memoized.

Transition checks become
cached lookups.


EXAMPLE
────────────────────────────────────

values:

1 2 5 9

target:

16

one printed solution:

2 5 9


NO SOLUTION
────────────────────────────────────

if rec(1,target) == 0:

    print "no solution"


MULTIPLE SOLUTIONS
────────────────────────────────────

Basic reconstruction prints
ONE valid solution.

Order of transition checks
decides which one.


COMPLEXITY
────────────────────────────────────

DP:

O(N × MAX_T)

Print one solution:

O(N)

Q queries with printing:

O(N × MAX_T + QN)


CODE STRUCTURE
────────────────────────────────────

DP rec():

1. Pruning
2. Base Case
3. Cache Check
4. Compute / Transition
5. Save and Return


CORE RECONSTRUCTION QUESTION
────────────────────────────────────

"Which outgoing transition
preserves the DP answer?"

Follow that transition.

If it means TAKE,
record the choice.
```
