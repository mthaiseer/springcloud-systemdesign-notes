# Meet-in-the-Middle — Form a Square Using All Rods
## 4-Way Partition with Recursive State Generation + Complement Tuples
### TLE-Style Self-Study Note — Prerequisites, ASCII Diagrams, Recursion Trees, Dry Run, Proof, and C++17

> **Problem idea**
>
> We are given `N` rods. Every rod must be used exactly once.
> We may join rods together to form the four sides of a square.
>
> We need to decide whether the rods can be partitioned into four groups having exactly the same total length.

---

# Table of Contents

1. [Problem Statement](#1-problem-statement)
2. [First Observation — All Four Sides Must Be Equal](#2-first-observation--all-four-sides-must-be-equal)
3. [Prerequisite 1 — Partitioning Means Assigning Every Rod](#3-prerequisite-1--partitioning-means-assigning-every-rod)
4. [Prerequisite 2 — Why Order Inside a Side Does Not Matter](#4-prerequisite-2--why-order-inside-a-side-does-not-matter)
5. [Prerequisite 3 — Why Every Rod Has Four Choices](#5-prerequisite-3--why-every-rod-has-four-choices)
6. [Brute Force Recursion](#6-brute-force-recursion)
7. [Brute Force Recursion Tree](#7-brute-force-recursion-tree)
8. [Brute Force Complexity](#8-brute-force-complexity)
9. [Meet-in-the-Middle Observation](#9-meet-in-the-middle-observation)
10. [What a Half-State Means](#10-what-a-half-state-means)
11. [Generating All Half-States](#11-generating-all-half-states)
12. [Half-State Recursion Tree](#12-half-state-recursion-tree)
13. [The Key 4-Dimensional Complement Equation](#13-the-key-4-dimensional-complement-equation)
14. [Why a Set Works](#14-why-a-set-works)
15. [Full MITM Algorithm](#15-full-mitm-algorithm)
16. [Step-by-Step Dry Run](#16-step-by-step-dry-run)
17. [C++17 Implementation](#17-c17-implementation)
18. [Code Walkthrough](#18-code-walkthrough)
19. [Correctness Proof](#19-correctness-proof)
20. [Complexity Analysis](#20-complexity-analysis)
21. [Important Pruning](#21-important-pruning)
22. [Symmetry and Duplicate States](#22-symmetry-and-duplicate-states)
23. [Common Mistakes](#23-common-mistakes)
24. [Recognition Pattern](#24-recognition-pattern)
25. [Generalization to K Buckets](#25-generalization-to-k-buckets)
26. [Final Mental Model](#26-final-mental-model)
27. [One-Minute Revision](#27-one-minute-revision)

---

# 1. Problem Statement

We have rod lengths:

```text
arr[0], arr[1], ..., arr[N-1]
```

We must use **every rod exactly once**.

Rods assigned to the same side can be combined.

We want four sides:

```text
Side A
Side B
Side C
Side D
```

such that:

```text
sum(A) = sum(B) = sum(C) = sum(D)
```

---

## Visual picture

Suppose the rods are:

```text
3, 4, 1, 6, 7, 1, 4, 2
```

Total:

```text
3 + 4 + 1 + 6 + 7 + 1 + 4 + 2
= 28
```

A square has four equal sides.

Therefore each side must have length:

```text
28 / 4 = 7
```

One possible grouping is:

```text
Side A: 7

Side B: 6 + 1

Side C: 4 + 3

Side D: 4 + 2 + 1
```

So:

```text
7 = 7 = 7 = 7
```

and a square can be formed.

---

# 2. First Observation — All Four Sides Must Be Equal

Let:

```text
total = sum of all rods
```

If a square can be formed, then:

```text
sideLength = total / 4
```

Therefore the first necessary condition is:

```text
total % 4 == 0
```

If:

```text
total % 4 != 0
```

the answer is immediately:

```text
false
```

---

## Why?

Suppose:

```text
total = 30
```

A square would require:

```text
30 / 4 = 7.5
```

But our side sums come from whole rod lengths.

The four equal integer side sums cannot add to `30`.

So no square is possible.

---

## First filter

```cpp
if (total % 4 != 0) {
    return false;
}
```

Then:

```text
target = total / 4
```

and our real problem becomes:

```text
Can we partition all rods into four groups,
each having sum target?
```

---

# 3. Prerequisite 1 — Partitioning Means Assigning Every Rod

This problem is not:

```text
choose some rods
```

It is:

```text
assign EVERY rod to exactly one of four sides
```

For each rod:

```text
rod i
```

we choose exactly one destination:

```text
A
B
C
D
```

---

## State representation

At any moment we only need to know:

```text
a = current sum of side A
b = current sum of side B
c = current sum of side C
d = current sum of side D
```

So a recursive state can be:

```text
(i, a, b, c, d)
```

Meaning:

```text
we have already assigned rods [0 ... i-1]

current side sums are:
A = a
B = b
C = c
D = d
```

---

# 4. Prerequisite 2 — Why Order Inside a Side Does Not Matter

Suppose side A receives rods:

```text
3, 1, 2
```

The order:

```text
3 + 1 + 2
```

is equivalent to:

```text
2 + 3 + 1
```

because:

```text
3 + 1 + 2 = 2 + 3 + 1 = 6
```

So the state only needs:

```text
the total length assigned to each side
```

not the sequence of rods inside that side.

---

## State compression

Instead of storing:

```text
A = [3,1,2]
B = [4,5]
...
```

we can store:

```text
(A_sum, B_sum, C_sum, D_sum)
```

For example:

```text
(6, 9, 5, 4)
```

This is a major simplification.

---

# 5. Prerequisite 3 — Why Every Rod Has Four Choices

Take one rod:

```text
arr[i] = x
```

We have exactly four possibilities.

---

## Choice 1 — Put it on side A

```text
(a + x, b, c, d)
```

---

## Choice 2 — Put it on side B

```text
(a, b + x, c, d)
```

---

## Choice 3 — Put it on side C

```text
(a, b, c + x, d)
```

---

## Choice 4 — Put it on side D

```text
(a, b, c, d + x)
```

So one state creates **four recursive children**.

---

# 6. Brute Force Recursion

Define:

```text
generate(i, a, b, c, d)
```

Meaning:

```text
Assign rods starting at index i,
given the four current side sums.
```

The recursive transitions are:

```text
generate(i + 1, a + arr[i], b, c, d)
generate(i + 1, a, b + arr[i], c, d)
generate(i + 1, a, b, c + arr[i], d)
generate(i + 1, a, b, c, d + arr[i])
```

---

## Base case

When:

```text
i == N
```

every rod has been assigned.

Now check:

```text
a == b &&
b == c &&
c == d
```

or equivalently, once `target` is known:

```text
a == target
b == target
c == target
d == target
```

---

# 7. Brute Force Recursion Tree

Take just three rods:

```text
x1, x2, x3
```

Each rod has four choices.

At level 0:

```text
                         (0,0,0,0)
```

For `x1`:

```text
             /A            /B            /C            /D
            /             /             /             /
(x1,0,0,0)   (0,x1,0,0)   (0,0,x1,0)   (0,0,0,x1)
```

Each of those again has four children for `x2`.

---

## Branching factor

```text
level 0: 1 state
level 1: 4 states
level 2: 4^2 states
level 3: 4^3 states
...
level N: 4^N states
```

So the number of complete assignments is:

```text
4^N
```

---

# 8. Brute Force Complexity

Each of the `N` rods independently chooses one of four sides.

Therefore:

```text
number of assignments = 4^N
```

Hence brute force is approximately:

```text
Time = O(4^N)
```

The recursion depth is:

```text
O(N)
```

---

## Why this becomes expensive quickly

```text
4^10 = 1,048,576
4^15 ≈ 1.07 * 10^9
4^20 ≈ 1.10 * 10^12
```

So direct brute force becomes impossible surprisingly fast.

We need to reduce the exponent.

That is exactly where Meet-in-the-Middle helps.

---

# 9. Meet-in-the-Middle Observation

Instead of processing all `N` rods together:

```text
N rods
```

split them into two disjoint halves:

```text
Left half
Right half
```

approximately:

```text
N/2 rods
N/2 rods
```

---

## Diagram

```text
Original rods

[ r0 r1 r2 r3 r4 r5 r6 r7 ]

             split

[ r0 r1 r2 r3 ]     [ r4 r5 r6 r7 ]
    Left half             Right half
```

For each half, independently generate every possible distribution among the four sides.

---

## Number of states per half

If a half contains about:

```text
N/2
```

rods, then it has:

```text
4^(N/2)
```

assignments.

So instead of:

```text
4^N
```

we generate approximately:

```text
4^(N/2) + 4^(N/2)
```

states.

This is the exponential reduction.

---

# 10. What a Half-State Means

Suppose the first half produces:

```text
(a,b,c,d)
```

This means:

```text
a = total length from LEFT-HALF rods assigned to side A
b = total length from LEFT-HALF rods assigned to side B
c = total length from LEFT-HALF rods assigned to side C
d = total length from LEFT-HALF rods assigned to side D
```

Similarly, suppose the second half produces:

```text
(p,q,r,s)
```

Then the complete square has side sums:

```text
A = a + p
B = b + q
C = c + r
D = d + s
```

We want every one to equal:

```text
target
```

Therefore:

```text
a + p = target
b + q = target
c + r = target
d + s = target
```

So:

```text
p = target - a
q = target - b
r = target - c
s = target - d
```

This gives us the crucial complement tuple.

---

# 11. Generating All Half-States

For one half:

```text
half = [x1,x2,...,xM]
```

we recursively generate:

```text
(a,b,c,d)
```

for every possible assignment.

---

## Recursive function

```text
generate(index, a, b, c, d)
```

At each rod:

```text
x = half[index]
```

branch four ways:

```text
A: (a+x,b,c,d)
B: (a,b+x,c,d)
C: (a,b,c+x,d)
D: (a,b,c,d+x)
```

When:

```text
index == M
```

store the tuple:

```text
(a,b,c,d)
```

---

# 12. Half-State Recursion Tree

Suppose the half contains two rods:

```text
[1,2]
```

Start:

```text
(0,0,0,0)
```

Place rod `1`.

```text
                            (0,0,0,0)
             /A                /B                /C                /D
            /                 /                 /                 /
       (1,0,0,0)         (0,1,0,0)         (0,0,1,0)         (0,0,0,1)
```

Now process rod `2`.

From:

```text
(1,0,0,0)
```

we get:

```text
A → (3,0,0,0)
B → (1,2,0,0)
C → (1,0,2,0)
D → (1,0,0,2)
```

Likewise for every other parent.

Total leaves:

```text
4^2 = 16
```

---

## Important interpretation

A leaf does **not** represent one side.

It represents a complete assignment of this half's rods across all four sides.

Example:

```text
(1,2,0,0)
```

means:

```text
one rod contributed 1 to A
another rod contributed 2 to B
nothing from this half went to C or D
```

---

# 13. The Key 4-Dimensional Complement Equation

Let the square side length be:

```text
target
```

Suppose the first half generates:

```text
L = (a,b,c,d)
```

We need a second-half state:

```text
R = (p,q,r,s)
```

such that:

```text
a+p = target
b+q = target
c+r = target
d+s = target
```

Therefore:

```text
R =
(
    target-a,
    target-b,
    target-c,
    target-d
)
```

---

## Diagram

```text
Left-half state:

(a, b, c, d)

          +

Right-half state:

(p, q, r, s)

          =

(target, target, target, target)
```

So:

```text
(p,q,r,s)
=
(target-a,
 target-b,
 target-c,
 target-d)
```

This is the exact MITM lookup.

---

# 14. Why a Set Works

We only need to know:

```text
Does this required right-half tuple exist?
```

So store all right-half states in a set.

Conceptually:

```text
rightStates = {
    (p1,q1,r1,s1),
    (p2,q2,r2,s2),
    ...
}
```

For every left tuple:

```text
(a,b,c,d)
```

build:

```text
need = (
    target-a,
    target-b,
    target-c,
    target-d
)
```

Then check:

```text
need in rightStates ?
```

If yes:

```text
square exists
```

---

## Why this is better than a Cartesian product

A naive combination of both halves would test:

```text
every left state × every right state
```

If each side has:

```text
4^(N/2)
```

states, the Cartesian product is:

```text
4^(N/2) * 4^(N/2)
= 4^N
```

which destroys the optimization.

Instead:

```text
store one half
lookup complement from the other half
```

So combination is close to:

```text
O(4^(N/2))
```

rather than:

```text
O(4^N)
```

---

# 15. Full MITM Algorithm

## Step 1 — Compute total

```text
total = sum(arr)
```

If:

```text
total % 4 != 0
```

return false.

---

## Step 2 — Compute target

```text
target = total / 4
```

---

## Step 3 — Split the rods

For example:

```text
mid = N / 2

left  = arr[0 ... mid-1]
right = arr[mid ... N-1]
```

They are disjoint and together contain every rod exactly once.

---

## Step 4 — Generate all states for both halves

```text
leftStates  = generate(left)
rightStates = generate(right)
```

Each state is:

```text
(a,b,c,d)
```

---

## Step 5 — Store right states in a lookup structure

```text
set<array<long long,4>> rightSet
```

---

## Step 6 — Search complements

For every:

```text
(a,b,c,d) in leftStates
```

compute:

```text
need =
(
target-a,
target-b,
target-c,
target-d
)
```

If:

```text
need exists in rightSet
```

return true.

Otherwise false.

---

# 16. Step-by-Step Dry Run

Use a small teaching example:

```text
arr = [1,1,1,1]
```

Total:

```text
4
```

Therefore:

```text
target = 1
```

Split:

```text
left  = [1,1]
right = [1,1]
```

---

## Generate left states

One useful left assignment is:

```text
first 1 -> side A
second 1 -> side B
```

So left tuple:

```text
L = (1,1,0,0)
```

---

## Required complement

We need:

```text
(target,target,target,target)
=
(1,1,1,1)
```

Subtract the left contribution:

```text
need =
(1-1,
 1-1,
 1-0,
 1-0)
```

Therefore:

```text
need = (0,0,1,1)
```

---

## Can the right half make `(0,0,1,1)`?

Yes.

Assign:

```text
first right rod -> C
second right rod -> D
```

Then:

```text
R = (0,0,1,1)
```

Combine:

```text
L + R

(1,1,0,0)
+
(0,0,1,1)

=
(1,1,1,1)
```

Every side equals target.

So answer:

```text
true
```

---

## Original lecture-style rod example

For rods:

```text
3,4,1,6,7,1,4,2
```

total:

```text
28
```

target:

```text
7
```

A valid complete distribution is:

```text
A = 7
B = 6+1
C = 4+3
D = 4+2+1
```

MITM does not need to know this grouping in advance.

It generates partial four-side totals from the two halves and matches complementary tuples.

---

# 17. C++17 Implementation

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;
using State = array<ll, 4>;

void generateStates(const vector<ll>& rods, int index, ll a, ll b, ll c, ll d,
                    ll target, vector<State>& states) {
    if (a > target || b > target || c > target || d > target) {
        return;
    }

    if (index == (int)rods.size()) {
        states.push_back({a, b, c, d});
        return;
    }

    ll value = rods[index];

    generateStates(rods, index + 1, a + value, b, c, d, target, states);
    generateStates(rods, index + 1, a, b + value, c, d, target, states);
    generateStates(rods, index + 1, a, b, c + value, d, target, states);
    generateStates(rods, index + 1, a, b, c, d + value, target, states);
}

bool canFormSquare(const vector<ll>& rods) {
    ll total = accumulate(rods.begin(), rods.end(), 0LL);

    if (total % 4 != 0) {
        return false;
    }

    ll target = total / 4;

    for (ll length : rods) {
        if (length > target) {
            return false;
        }
    }

    int n = rods.size();
    int mid = n / 2;

    vector<ll> left(rods.begin(), rods.begin() + mid);
    vector<ll> right(rods.begin() + mid, rods.end());

    vector<State> leftStates;
    vector<State> rightStates;

    generateStates(left, 0, 0, 0, 0, 0, target, leftStates);
    generateStates(right, 0, 0, 0, 0, 0, target, rightStates);

    set<State> rightSet(rightStates.begin(), rightStates.end());

    for (const State& leftState : leftStates) {
        State need = {
            target - leftState[0],
            target - leftState[1],
            target - leftState[2],
            target - leftState[3]
        };

        if (rightSet.find(need) != rightSet.end()) {
            return true;
        }
    }

    return false;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<ll> rods(n);

    for (ll& length : rods) {
        cin >> length;
    }

    cout << (canFormSquare(rods) ? "YES" : "NO") << '\n';

    return 0;
}
```

---

# 18. Code Walkthrough

## `State`

```cpp
using State = array<ll, 4>;
```

One state represents:

```text
{sideA, sideB, sideC, sideD}
```

for the rods processed in one half.

---

## Recursive generator

```cpp
generateStates(rods, index, a, b, c, d, target, states)
```

Contract:

```text
Generate every valid assignment of rods[index ... end]
given the current four side sums.
```

---

## Four recursive choices

```cpp
generateStates(... a + value, b, c, d ...);
generateStates(... a, b + value, c, d ...);
generateStates(... a, b, c + value, d ...);
generateStates(... a, b, c, d + value ...);
```

These correspond exactly to:

```text
put rod on A
put rod on B
put rod on C
put rod on D
```

---

## Base case

```cpp
if (index == (int)rods.size()) {
    states.push_back({a, b, c, d});
    return;
}
```

All rods from that half have been assigned.

Store the resulting contribution tuple.

---

## Split

```cpp
int mid = n / 2;
```

Then:

```cpp
left  = rods[0 ... mid-1]
right = rods[mid ... n-1]
```

The halves are disjoint.

Therefore every original rod is assigned exactly once when one left state and one right state are combined.

---

## Complement

```cpp
State need = {
    target - leftState[0],
    target - leftState[1],
    target - leftState[2],
    target - leftState[3]
};
```

This is directly derived from:

```text
leftContribution + rightContribution = target
```

for each side.

---

# 19. Correctness Proof

We need to show:

```text
algorithm returns true
iff
the rods can form a square
```

---

## 19.1 If the algorithm returns true, a square exists

Suppose the algorithm finds:

```text
L = (a,b,c,d)
```

from the left half and:

```text
R = (p,q,r,s)
```

from the right half where:

```text
R =
(target-a,
 target-b,
 target-c,
 target-d)
```

Then:

```text
a+p = target
b+q = target
c+r = target
d+s = target
```

The halves are disjoint, so every rod is used exactly once.

Thus all rods are divided among four sides of equal length.

Therefore a square exists.

---

## 19.2 If a square exists, the algorithm will find it

Assume there is some valid assignment of every rod to the four sides.

Take that valid assignment and split its rods according to our MITM split.

The left-half rods contribute some tuple:

```text
L = (a,b,c,d)
```

Our recursive generator enumerates **every** assignment of left-half rods, so it generates `L`.

The right-half rods contribute:

```text
R = (p,q,r,s)
```

Our right generator also enumerates every possible right-half assignment, so it generates `R`.

Because the complete assignment is a square:

```text
L + R
=
(target,target,target,target)
```

Hence:

```text
R =
(target-a,
 target-b,
 target-c,
 target-d)
```

When the algorithm processes `L`, it searches for exactly `R`.

Therefore it finds the valid solution.

---

# 20. Complexity Analysis

Let:

```text
n1 = size of left half
n2 = size of right half
```

with approximately:

```text
n1 ≈ N/2
n2 ≈ N/2
```

---

## Number of left states

Each rod has 4 choices:

```text
4^n1
```

---

## Number of right states

Likewise:

```text
4^n2
```

---

## State generation

Approximately:

```text
O(4^(N/2))
```

per half.

More precisely:

```text
O(4^n1 + 4^n2)
```

---

## Building `set`

Let:

```text
M = 4^n2
```

Inserting `M` states into an ordered `set` costs:

```text
O(M log M)
```

---

## Lookup

For every left state:

```text
O(log M)
```

So:

```text
O(4^n1 log M)
```

---

## Overall with ordered `set`

For balanced halves:

```text
Time ≈ O(4^(N/2) * log(4^(N/2)))
```

Since:

```text
log(4^(N/2)) = O(N)
```

we may write:

```text
O(N * 4^(N/2))
```

up to constant factors and both halves.

Space:

```text
O(4^(N/2))
```

---

## Core comparison

```text
Brute force:
4^N

Meet-in-the-Middle:
roughly 4^(N/2)
```

This is the main win.

---

# 21. Important Pruning

The C++ implementation contains:

```cpp
if (a > target || b > target || c > target || d > target) {
    return;
}
```

This pruning is valid when rod lengths are non-negative.

Why?

If:

```text
a > target
```

and every future rod length is non-negative, side A can never decrease.

Therefore this branch can never become valid.

---

## Example

```text
target = 7

current state:
(8,3,2,1)
```

Even before assigning the remaining rods:

```text
A = 8 > 7
```

No future assignment can reduce `8` to `7`.

So prune immediately.

---

## Another simple check

If one rod itself has:

```text
length > target
```

then whichever side receives it exceeds the target.

Therefore:

```cpp
if (length > target) {
    return false;
}
```

---

# 22. Symmetry and Duplicate States

The four sides of a square are physically interchangeable.

For example:

```text
(3,4,5,6)
```

and:

```text
(4,3,5,6)
```

represent the same multiset of side sums if we ignore side labels.

However, the lecture-style generation labels the sides:

```text
A, B, C, D
```

This may generate symmetrical states multiple times.

That is still correct.

---

## Why correctness is unaffected

Every valid unlabeled square can be labeled:

```text
A,B,C,D
```

in some order.

So labeled generation will include it.

Duplicates only affect efficiency, not correctness.

---

## Optional optimization

One can canonicalize states by sorting the four sums:

```text
(a,b,c,d)
↓
sort
↓
canonical state
```

But be careful:

The complement relation is naturally component-wise with labeled sides.

Canonicalization changes how matching must be designed.

For a clean self-study implementation, keeping labeled sides is easier and safer.

---

# 23. Common Mistakes

## Mistake 1 — Forgetting `total % 4`

Before recursion:

```text
if total is not divisible by 4
→ impossible
```

---

## Mistake 2 — Forgetting that every rod must be used

This is not a subset-selection problem.

Every recursion level must assign the current rod to exactly one of:

```text
A,B,C,D
```

---

## Mistake 3 — Thinking each half chooses one side

No.

A half-state is:

```text
(a,b,c,d)
```

because rods from that half may contribute to all four sides.

---

## Mistake 4 — Comparing only the total of a half

A left half might have total:

```text
14
```

and a right half also `14`.

That does not mean they form a square.

We need **component-wise** completion:

```text
A_left + A_right = target
B_left + B_right = target
C_left + C_right = target
D_left + D_right = target
```

So the state must be four-dimensional.

---

## Mistake 5 — Taking the Cartesian product of the half-state lists

Wrong:

```text
for every L:
    for every R:
        check L + R
```

This gives approximately:

```text
4^(N/2) * 4^(N/2)
= 4^N
```

and loses the MITM advantage.

Correct:

```text
store one half
lookup complement
```

---

## Mistake 6 — Wrong complement

If:

```text
L = (a,b,c,d)
```

the required right state is:

```text
(target-a,
 target-b,
 target-c,
 target-d)
```

not something based on total sum alone.

---

## Mistake 7 — Not using `long long`

Totals may become larger than individual rod values.

Use:

```cpp
long long
```

for:

```text
rod values
side sums
total
target
```

---

## Mistake 8 — Pruning with negative values

The pruning:

```text
if side > target → return
```

is safe only when future additions cannot decrease the side.

With negative rod lengths, that assumption breaks.

For normal rod-length problems, lengths are non-negative.

---

# 24. Recognition Pattern

Think **Meet-in-the-Middle** when you see:

```text
N is too large for full exponential search
but N/2 exponential search is plausible
```

and each element independently has a small number of choices.

Here:

```text
each rod has 4 choices
```

so:

```text
full search = 4^N
```

Splitting gives:

```text
4^(N/2)
```

states per half.

---

## Recognition checklist

Ask:

```text
1. Is the complete search exponential?
2. Can the elements be split into two disjoint halves?
3. Can I summarize one half by a compact state?
4. Can the other half derive exactly what complement is needed?
5. Can I use set/map/hash lookup instead of taking the Cartesian product?
```

If yes, MITM is a strong candidate.

---

# 25. Generalization to K Buckets

The same idea extends beyond squares.

Suppose each item must go into one of:

```text
K buckets
```

Then brute force has:

```text
K^N
```

assignments.

Splitting into two halves gives about:

```text
K^(N/2)
```

states per half.

The state becomes:

```text
(s1,s2,...,sK)
```

and matching may use a complement vector.

For the square problem:

```text
K = 4
```

so the state is:

```text
(a,b,c,d)
```

---

# 26. Final Mental Model

Start from the physical problem:

```text
rods
↓
four equal sides
```

Convert to a partition problem:

```text
every rod chooses one of 4 sides
```

Therefore:

```text
brute force = 4^N
```

Represent a partial assignment using:

```text
(a,b,c,d)
```

Then split the rods:

```text
N
↓
N/2 + N/2
```

Generate:

```text
Left states  ≈ 4^(N/2)
Right states ≈ 4^(N/2)
```

For each left state:

```text
(a,b,c,d)
```

the exact required right state is:

```text
(target-a,
 target-b,
 target-c,
 target-d)
```

Lookup that tuple.

That is Meet-in-the-Middle.

---

## Visual summary

```text
                 ALL N RODS
                     |
             split into halves
              /             \
             /               \
        LEFT HALF         RIGHT HALF
             |               |
     generate 4^(N/2)   generate 4^(N/2)
          states             states
             |               |
             |          store in set
             |               |
             +---- complement lookup ----+
                                         |
                      (target,target,target,target)
                                         |
                                      SQUARE
```

---

# 27. One-Minute Revision

```text
Goal:
use every rod exactly once
to make 4 equal side sums.

total = sum(rods)

If total % 4 != 0:
    impossible

target = total / 4

Every rod has 4 choices:
A / B / C / D

Brute force:
4^N

State:
(a,b,c,d)

Split rods into two halves.

Generate all states:
left  ≈ 4^(N/2)
right ≈ 4^(N/2)

For left state:
(a,b,c,d)

need right state:
(target-a,
 target-b,
 target-c,
 target-d)

Store right states in a set.
Lookup complement.

Do NOT compare every left state
with every right state.

That would return to 4^N.

MITM idea:
generate halves independently
+
match exact complement.
```
