# 013 — DP Framework & 0/1 Knapsack
## Recursion → Caching → DP State Design → Transition Design

> **Self-study goal:** Build the correct mental model for Dynamic Programming before learning more DP patterns.
>
> This note follows the lecture screenshots in the same order:
>
> **Recursion Tree → Repeated States → Caching / Memoization → Recursion DAG → Counting vs Optimization → Optimal Substructure → Overlapping Subproblems → DP Framework → 0/1 Knapsack**
>
> **Code-format rule:** compact C++17, 4-space indentation, same-line braces, related variables together, short logical blocks, and simple transitions kept visually easy to scan.

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [What Dynamic Programming Is Solving](#2-what-dynamic-programming-is-solving)
3. [Recursion First](#3-recursion-first)
4. [Why Plain Recursion Becomes Slow](#4-why-plain-recursion-becomes-slow)
5. [Recursion Tree → Recursion DAG](#5-recursion-tree--recursion-dag)
6. [Caching / Memoization](#6-caching--memoization)
7. [Fibonacci Example](#7-fibonacci-example)
8. [When DP Is a Good Fit](#8-when-dp-is-a-good-fit)
9. [Counting Problems vs Optimization Problems](#9-counting-problems-vs-optimization-problems)
10. [Two Core DP Properties](#10-two-core-dp-properties)
11. [The Lecture's 4-Step DP Framework](#11-the-lectures-4-step-dp-framework)
12. [Step 1 — Decide the Form](#12-step-1--decide-the-form)
13. [Form 1 — Choose a Subset](#13-form-1--choose-a-subset)
14. [0/1 Knapsack Problem](#14-01-knapsack-problem)
15. [Why Brute Force Is Exponential](#15-why-brute-force-is-exponential)
16. [Step 2 — State Space Formulation](#16-step-2--state-space-formulation)
17. [Meaning of `rec(level, weightLeft)`](#17-meaning-of-reclevel-weightleft)
18. [Step 3 — Transition Design](#18-step-3--transition-design)
19. [Base Case](#19-base-case)
20. [Memoization](#20-memoization)
21. [Complete Top-Down C++17](#21-complete-top-down-c17)
22. [Dry Run](#22-dry-run)
23. [Step 4 — Time Complexity Check](#23-step-4--time-complexity-check)
24. [Why DP Is Faster Than Subset Enumeration](#24-why-dp-is-faster-than-subset-enumeration)
25. [State vs Transition vs Answer](#25-state-vs-transition-vs-answer)
26. [Common Mistakes](#26-common-mistakes)
27. [Contest Recognition Checklist](#27-contest-recognition-checklist)
28. [Final Mental Model](#28-final-mental-model)

---

# 1. Prerequisites

Before DP, be comfortable with these ideas.

---

## 1.1 Recursion

A recursive function solves a problem by calling itself on a smaller problem.

Example:

```cpp
int fib(int n) {
    if (n <= 1) return n;

    return fib(n - 1) + fib(n - 2);
}
```

The important question is:

```text
What does fib(n) mean?
```

Answer:

```text
fib(n)
=
the nth Fibonacci value
```

Every DP state must have the same kind of clear meaning.

---

## 1.2 Base case

A recursive function needs a smallest problem whose answer is already known.

For Fibonacci:

```cpp
if (n <= 1) return n;
```

For Knapsack:

```cpp
if (level == n) return 0;
```

Base case means:

```text
There are no more decisions left.
```

---

## 1.3 Recursion tree

Each recursive call may create more calls.

Example:

```text
fib(5)
├── fib(4)
│   ├── fib(3)
│   └── fib(2)
└── fib(3)
    ├── fib(2)
    └── fib(1)
```

Notice:

```text
fib(3)
```

appears multiple times.

That repeated work is the reason plain recursion can become slow.

---

## 1.4 Arrays / vectors

Memoization stores already-computed answers.

Typical storage:

```cpp
vector<int> dp(n + 1, -1);
```

For two state variables:

```cpp
vector<vector<int>> dp(
    n,
    vector<int>(W + 1, -1)
);
```

---

## 1.5 `-1` as "not computed"

If all valid answers are non-negative, we may initialize:

```text
dp[state] = -1
```

Meaning:

```text
This state has not been solved yet.
```

Then:

```cpp
if (dp[state] != -1) {
    return dp[state];
}
```

---

## 1.6 Maximum / minimum

Optimization DP usually combines choices using:

```cpp
max(...)
```

or:

```cpp
min(...)
```

For 0/1 Knapsack:

```text
maximize total value
```

so the transition uses:

```cpp
max(notTake, take);
```

---

## 1.7 Subset

A subset chooses some elements from a set.

For:

```text
{1,2,3}
```

possible subsets include:

```text
{}
{1}
{2}
{3}
{1,2}
{1,3}
{2,3}
{1,2,3}
```

Every element has two decisions:

```text
take
or
not take
```

So the total number of subsets is:

```text
2^N
```

This is directly connected to 0/1 Knapsack.

---

# 2. What Dynamic Programming Is Solving

The lecture starts from:

```text
Dynamic Programming
        ↓
      Caching
```

DP is not just "write a table."

The central idea is:

```text
If the same subproblem appears again,
do not solve it again.
```

Instead:

```text
solve once
store answer
reuse answer
```

That is caching / memoization.

---

# 3. Recursion First

A useful way to learn top-down DP is:

```text
First design the recursive solution.

Then identify repeated states.

Then cache them.
```

So the progression is:

```text
Recursion
    ↓
Correct state meaning
    ↓
Correct transition
    ↓
Repeated subproblems?
    ↓
Memoization
    ↓
Dynamic Programming
```

---

# 4. Why Plain Recursion Becomes Slow

The lecture visualizes Fibonacci as a large recursion tree.

For:

```text
fib(10)
```

we repeatedly calculate states such as:

```text
fib(8)
fib(7)
fib(6)
...
```

from different branches.

Example:

```text
fib(5)
├── fib(4)
│   ├── fib(3)
│   └── fib(2)
└── fib(3)
```

The second:

```text
fib(3)
```

does not need to be calculated again.

Its answer is identical.

---

## 4.1 Important observation

A recursive call is identified by its parameters.

For:

```cpp
fib(3)
```

every call with argument:

```text
3
```

represents the same subproblem.

Similarly, later in Knapsack:

```cpp
rec(level, weightLeft)
```

two calls with the same:

```text
level
weightLeft
```

represent exactly the same future problem.

---

# 5. Recursion Tree → Recursion DAG

Without memoization:

```text
same state can appear many times
```

so visualization looks like a tree.

With memoization:

```text
equal states are solved only once
```

Conceptually, repeated nodes collapse into one node.

The structure becomes a:

```text
DAG
=
Directed Acyclic Graph
```

Mental picture:

```text
Before memoization:

       A
      / \
     B   C
    / \ /
   D  E E

E is computed twice.


After memoization:

       A
      / \
     B   C
      \ /
       E

E is one state.
```

This is the key visual idea shown in the lecture.

---

# 6. Caching / Memoization

Memoization pattern:

```cpp
int solve(int state) {
    if (baseCase) return baseAnswer;

    if (dp[state] != -1) {
        return dp[state];
    }

    int answer = ...;

    return dp[state] = answer;
}
```

The order is important:

```text
1. Base case
2. Cache check
3. Compute transition
4. Save answer
5. Return answer
```

---

# 7. Fibonacci Example

## 7.1 Plain recursion

```cpp
long long fib(int n) {
    if (n <= 1) return n;

    return fib(n - 1) + fib(n - 2);
}
```

Problem:

```text
many states are recalculated
```

---

## 7.2 Memoized recursion

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<long long> dp;

long long fib(int n) {
    if (n <= 1) return n;

    long long &ans = dp[n];
    if (ans != -1) return ans;

    return ans = fib(n - 1) + fib(n - 2);
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    dp.assign(n + 1, -1);

    cout << fib(n) << '\n';

    return 0;
}
```

Now every:

```text
fib(x)
```

is computed only once.

---

## 7.3 Complexity idea

Plain recursion creates exponentially many calls.

Memoized version has only:

```text
n + 1
```

different states:

```text
0,1,2,...,n
```

Each state performs constant work.

So:

```text
Time = O(n)
Space = O(n)
```

---

# 8. When DP Is a Good Fit

The lecture separates problems into broad categories such as:

```text
Counting Problems
```

and:

```text
Optimization Problems
```

Both can often lead to DP.

---

# 9. Counting Problems vs Optimization Problems

## 9.1 Counting problem

Question form:

```text
How many ways?
```

Examples:

```text
How many paths?
How many valid sequences?
How many subsets?
```

Transitions often use:

```text
sum of ways
```

Example shape:

```text
dp[state]
=
dp[next1]
+
dp[next2]
+
...
```

---

## 9.2 Optimization problem

Question form:

```text
What is the minimum?
What is the maximum?
```

Examples:

```text
maximum value
minimum cost
minimum moves
maximum profit
```

Transitions often use:

```text
min(...)
```

or:

```text
max(...)
```

Knapsack is:

```text
maximum total value
```

So it is an optimization DP.

---

# 10. Two Core DP Properties

The lecture highlights two important ideas.

---

## 10.1 Optimal substructure

A problem has optimal substructure when the optimal answer can be built from optimal answers to smaller subproblems.

Knapsack example:

```text
Best answer from item `level`
```

comes from the best of:

```text
do not take current item

or

take current item
+
best answer for remaining items
```

So the optimal answer uses optimal answers of smaller states.

---

## 10.2 Overlapping subproblems

Different recursive paths can reach the same state.

Example:

```text
rec(level, weightLeft)
```

may be reached from different earlier decisions.

If:

```text
level
and
weightLeft
```

are the same, the future answer is the same.

So instead of solving repeatedly:

```text
cache it
```

---

## 10.3 DP sweet spot

The lecture's mental model is:

```text
Optimal Substructure
+
Overlapping Subproblems
=
Strong DP signal
```

---

# 11. The Lecture's 4-Step DP Framework

The screenshots summarize the DP-solving process as:

```text
1. Decide the Form

2. State Space Formulation

3. Transition Design

4. Time Complexity Check
```

This is an excellent contest framework.

Do not jump directly into code.

---

# 12. Step 1 — Decide the Form

Before creating:

```text
dp[i][j]
```

ask:

```text
What kind of decisions am I making?
```

Examples:

```text
take / not take

move left / right

choose next index

partition interval

select subset
```

The lecture's first major form is:

```text
Choose a subset
with some satisfying condition.
```

That leads naturally to:

```text
take / not take
```

---

# 13. Form 1 — Choose a Subset

Suppose we have:

```text
N items
```

For each item:

```text
take it
or
skip it
```

The decision tree is:

```text
                       item 0
                     /        \
                  TAKE        SKIP
                   /            \
               item 1          item 1
              /    \          /    \
          TAKE    SKIP    TAKE    SKIP
             ...
```

Without DP:

```text
2 choices per item
```

so:

```text
2^N subsets
```

This is the basic subset-recursion form shown in the lecture.

---

# 14. 0/1 Knapsack Problem

We have:

```text
N items
```

Each item `i` has:

```text
weight[i]
value[i]
```

The bag capacity is:

```text
W
```

We must choose a subset such that:

```text
totalWeight <= W
```

and maximize:

```text
totalValue
```

The lecture labels this:

```text
0/1 Knapsack
```

because every item is either:

```text
0 → not taken
1 → taken
```

An item cannot be taken multiple times.

---

## 14.1 Input model

```text
Item:      0    1    2    ...   N-1

Weight:   w0   w1   w2   ...   wN-1

Value:    v0   v1   v2   ...   vN-1

Capacity: W
```

---

# 15. Why Brute Force Is Exponential

Every item has:

```text
2 choices
```

For `N` items:

```text
2 × 2 × 2 × ... × 2
=
2^N
```

So brute-force subset enumeration is:

```text
O(2^N)
```

For large `N`, this is too slow.

But many different subsets lead to the same future state.

That overlap lets DP compress the exponential tree.

---

# 16. Step 2 — State Space Formulation

The lecture chooses two pieces of information:

```text
level
weightLeft
```

Why these two?

Because future decisions depend on:

```text
which items are still available
```

and:

```text
how much capacity remains
```

Nothing else about the past matters.

---

# 17. Meaning of `rec(level, weightLeft)`

Define:

```text
rec(level, weightLeft)
=
maximum value we can obtain
using items [level ... N-1]

when remaining capacity
is `weightLeft`
```

This sentence is the most important part of the solution.

---

## 17.1 `level`

```text
level
```

means:

```text
current item index
```

Items before `level` have already been decided.

Available items are:

```text
[level ... N-1]
```

---

## 17.2 `weightLeft`

```text
weightLeft
```

means:

```text
how much bag capacity is still available
```

Initially:

```text
weightLeft = W
```

---

## 17.3 Final subproblem

The original problem is:

```text
start from item 0
with full capacity W
```

Therefore:

```text
answer = rec(0, W)
```

This appears directly in the lecture screenshots.

---

# 18. Step 3 — Transition Design

At:

```text
rec(level, weightLeft)
```

we are deciding what to do with:

```text
item `level`
```

There are two choices.

---

## 18.1 Choice 1 — Do NOT take the item

We gain:

```text
0
```

value now.

Capacity does not change.

Move to next item:

```text
notTake
=
rec(level + 1, weightLeft)
```

---

## 18.2 Choice 2 — Take the item

We may take it only if:

```text
weight[level] <= weightLeft
```

If we take it:

```text
gain value[level]
```

and remaining capacity becomes:

```text
weightLeft - weight[level]
```

So:

```text
take
=
value[level]
+
rec(
    level + 1,
    weightLeft - weight[level]
)
```

---

## 18.3 Final transition

We want maximum value:

```text
rec(level, weightLeft)
=
max(
    notTake,
    take
)
```

Compact lecture-style form:

```text
notTake =
rec(level + 1, weightLeft)

take =
value[level]
+
rec(level + 1, weightLeft - weight[level])

answer =
max(notTake, take)
```

But:

```text
take
```

is considered only when:

```text
weight[level] <= weightLeft
```

---

# 19. Base Case

When:

```text
level == N
```

there are no items left.

No more value can be added.

Therefore:

```text
rec(N, weightLeft)
=
0
```

C++:

```cpp
if (level == n) return 0;
```

Notice:

```text
weightLeft
```

does not matter anymore because there are no remaining items.

---

# 20. Memoization

The state has two parameters:

```text
level
weightLeft
```

So memoization table is:

```text
dp[level][weightLeft]
```

Before solving:

```cpp
int &ans = dp[level][weightLeft];

if (ans != -1) {
    return ans;
}
```

After computing:

```cpp
return ans = max(notTake, take);
```

---

## 20.1 Why memoization is correct

Suppose two different decision paths both reach:

```text
rec(5, 7)
```

The history may be different.

But both states mean:

```text
items available:
[5 ... N-1]

remaining capacity:
7
```

Therefore the future answer is identical.

So:

```text
solve rec(5,7) once
```

and reuse it.

---

# 21. Complete Top-Down C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int n, W;
vector<int> weightValue, itemValue;
vector<vector<int>> dp;

int rec(int level, int weightLeft) {
    if (level == n) return 0;

    int &ans = dp[level][weightLeft];
    if (ans != -1) return ans;

    int notTake = rec(level + 1, weightLeft);
    int take = 0;

    if (weightValue[level] <= weightLeft) {
        take = itemValue[level]
             + rec(level + 1, weightLeft - weightValue[level]);
    }

    return ans = max(notTake, take);
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n >> W;

    weightValue.resize(n);
    itemValue.resize(n);

    for (int i = 0; i < n; i++) {
        cin >> weightValue[i];
    }

    for (int i = 0; i < n; i++) {
        cin >> itemValue[i];
    }

    dp.assign(n, vector<int>(W + 1, -1));

    cout << rec(0, W) << '\n';

    return 0;
}
```

---

## 21.1 Code structure from the lecture

The lecture code follows this order:

```text
1. Pruning / validity if needed

2. Base case

3. Cache check

4. Transition

5. Save and return
```

For this Knapsack form:

```text
Base case
→ cache check
→ not take
→ take if possible
→ max
→ cache
```

---

# 22. Dry Run

Use this **small supporting example** to understand the state mechanics:

```text
N = 3
W = 5

weights = [2, 3, 4]
values  = [4, 5, 7]
```

We want:

```text
rec(0, 5)
```

---

## 22.1 At item 0

Item:

```text
weight = 2
value = 4
```

Two choices.

### Not take

```text
rec(1, 5)
```

### Take

```text
4 + rec(1, 3)
```

Therefore:

```text
rec(0,5)
=
max(
    rec(1,5),
    4 + rec(1,3)
)
```

---

## 22.2 State `rec(1,3)`

Current item:

```text
weight = 3
value = 5
```

### Not take

```text
rec(2,3)
```

### Take

```text
5 + rec(2,0)
```

So:

```text
rec(1,3)
=
max(
    rec(2,3),
    5 + rec(2,0)
)
```

---

## 22.3 Why overlapping can happen

In larger inputs, different decision paths can reach the same:

```text
(level, weightLeft)
```

Example shape:

```text
Path A
→ rec(5,7)

Path B
→ rec(5,7)
```

Without memoization:

```text
solve twice
```

With DP:

```text
solve once
reuse cached answer
```

---

# 23. Step 4 — Time Complexity Check

The lecture explicitly ends the framework with:

```text
TC Check
```

For memoized DP:

```text
Time Complexity
=
Number of states
×
Work per state
```

This is one of the most useful DP formulas.

---

## 23.1 Number of states

`level` can be:

```text
0 ... N-1
```

So:

```text
N possibilities
```

`weightLeft` can be:

```text
0 ... W
```

So:

```text
W + 1 possibilities
```

Total states:

```text
N × (W + 1)
```

which is:

```text
O(NW)
```

---

## 23.2 Work per state

Every state has at most:

```text
2 transitions
```

```text
take
not take
```

So transition work is:

```text
O(1)
```

---

## 23.3 Final complexity

```text
Time
=
O(NW)
```

Memo table:

```text
Space
=
O(NW)
```

Recursion stack:

```text
O(N)
```

So overall main DP memory is:

```text
O(NW)
```

---

# 24. Why DP Is Faster Than Subset Enumeration

## Brute force

Decision tree:

```text
2 choices per item
```

Total leaves:

```text
2^N
```

Time:

```text
O(2^N)
```

---

## Memoized DP

Instead of remembering the exact subset chosen so far, ask:

```text
What information from the past
actually affects the future?
```

Only:

```text
level
weightLeft
```

matter.

So exponential histories collapse into:

```text
O(NW)
```

states.

This is the central DP compression idea.

---

# 25. State vs Transition vs Answer

These three ideas must stay separate.

---

## 25.1 State

```text
rec(level, weightLeft)
```

describes:

```text
one smaller problem
```

---

## 25.2 Transition

```text
take
or
not take
```

describes:

```text
how this state moves to smaller states
```

---

## 25.3 Final answer

```text
rec(0, W)
```

describes:

```text
which state represents the original problem
```

A lot of DP confusion disappears when you explicitly write all three.

---

# 26. Common Mistakes

## Mistake 1 — Writing `dp[i][j]` before defining it

Wrong approach:

```text
I think this is 2D DP.
Let's make dp[i][j].
```

Correct approach:

```text
Write the English meaning first.
```

Example:

```text
dp[level][weightLeft]
=
maximum value from items [level...N-1]
with remaining capacity weightLeft
```

---

## Mistake 2 — Storing unnecessary history

Do not store:

```text
which exact items were previously chosen
```

if the future only needs:

```text
level
weightLeft
```

DP state should contain:

```text
only information that affects future decisions
```

---

## Mistake 3 — Taking an item without checking capacity

Wrong:

```cpp
take = itemValue[level]
     + rec(level + 1, weightLeft - weightValue[level]);
```

when:

```text
weightValue[level] > weightLeft
```

Correct:

```cpp
if (weightValue[level] <= weightLeft) {
    ...
}
```

---

## Mistake 4 — Forgetting cache check

Without:

```cpp
if (ans != -1) return ans;
```

the code is still exponential recursion.

---

## Mistake 5 — Wrong state meaning

These are different states:

```text
current used weight
```

and:

```text
remaining weight
```

Both can work if designed consistently.

The lecture uses:

```text
weightLeft
```

so the take transition subtracts:

```text
weight[level]
```

---

## Mistake 6 — Forgetting the final subproblem

After writing `rec(...)`, always ask:

```text
What arguments represent the original problem?
```

For Knapsack:

```text
level = 0
weightLeft = W
```

So:

```text
rec(0,W)
```

---

## Mistake 7 — Complexity from recursion branches instead of states

After memoization, do not say:

```text
2 branches → O(2^N)
```

Instead use:

```text
number of unique states
×
transition work per state
```

For Knapsack:

```text
N × W × 2
=
O(NW)
```

---

# 27. Contest Recognition Checklist

When reading a new problem, ask in this order.

---

## 27.1 What type of problem is it?

```text
Counting?
Optimization?
Feasibility?
```

---

## 27.2 What is the decision form?

Examples:

```text
take / skip

choose next

partition

move to neighbor

select subset
```

If it is:

```text
choose a subset
```

take/skip recursion is a strong first model.

---

## 27.3 What information affects the future?

Ask:

```text
If two different histories produce
the same future situation,
what values are identical?
```

Those values should become your:

```text
state parameters
```

Knapsack:

```text
level
weightLeft
```

---

## 27.4 Write the state in English

Before code:

```text
rec(level, weightLeft)
=
...
```

If you cannot finish the sentence clearly, state design is not ready.

---

## 27.5 Write all choices

Knapsack:

```text
not take
take
```

---

## 27.6 Write transition

```text
notTake = rec(level + 1, weightLeft)

take =
value[level]
+
rec(level + 1, weightLeft - weight[level])

answer = max(notTake, take)
```

---

## 27.7 Write base case

```text
No items left
→ 0 additional value
```

---

## 27.8 Check overlap

Can different recursion paths reach the same state?

If yes:

```text
memoize
```

---

## 27.9 Check complexity

Use:

```text
#states × transitions/state
```

If too large:

```text
redesign / optimize state
```

---

# 28. Final Mental Model

## Dynamic Programming

```text
RECURSION
    ↓
same subproblem appears again
    ↓
CACHE answer
    ↓
reuse result
    ↓
DP
```

---

## Tree → DAG

```text
Plain recursion:

same state
appears many times


Memoized recursion:

one unique state
one computation
many references
```

---

## DP conditions from the lecture

```text
1. Optimal Substructure

2. Overlapping Subproblems
```

---

## Lecture framework

```text
1. Decide the Form
        ↓
2. State Space Formulation
        ↓
3. Transition Design
        ↓
4. Time Complexity Check
```

---

## 0/1 Knapsack

```text
FORM:
choose subset

STATE:
rec(level, weightLeft)

CHOICES:
take / not take

BASE:
level == N

FINAL:
rec(0, W)
```

Transition:

```text
notTake =
rec(level + 1, weightLeft)

take =
value[level]
+
rec(level + 1, weightLeft - weight[level])

answer =
max(notTake, take)
```

only if the item fits.

---

# Final Revision Box

```text
DP = RECURSION + CACHING
────────────────────────────────────

Repeated recursive state?
→ Solve once
→ Cache
→ Reuse


WHEN DP?
────────────────────────────────────

Strong signs:

Optimal substructure
+
Overlapping subproblems


PROBLEM TYPES
────────────────────────────────────

Counting:
"How many?"

Optimization:
"Minimum / Maximum?"


4-STEP FRAMEWORK
────────────────────────────────────

1. Decide the Form

2. State Space Formulation

3. Transition Design

4. Time Complexity Check


FORM 1 — CHOOSE SUBSET
────────────────────────────────────

For every item:

TAKE
or
NOT TAKE


0/1 KNAPSACK
────────────────────────────────────

N items

weight[i]
value[i]

Capacity = W

Goal:
maximum value
with total weight <= W


STATE
────────────────────────────────────

rec(level, weightLeft)

=
maximum value obtainable
from items [level ... N-1]

with `weightLeft`
capacity remaining


BASE
────────────────────────────────────

level == N

return 0


TRANSITION
────────────────────────────────────

notTake =
rec(level + 1, weightLeft)

if weight[level] <= weightLeft:

    take =
    value[level]
    +
    rec(
        level + 1,
        weightLeft - weight[level]
    )

answer =
max(notTake, take)


FINAL SUBPROBLEM
────────────────────────────────────

rec(0, W)


MEMOIZATION
────────────────────────────────────

dp[level][weightLeft]


COMPLEXITY
────────────────────────────────────

States:
N × (W + 1)

Transitions/state:
2

Time:
O(NW)

Space:
O(NW)


CORE QUESTION
────────────────────────────────────

"What information from the past
actually changes the future?"

That information becomes
the DP state.
```
