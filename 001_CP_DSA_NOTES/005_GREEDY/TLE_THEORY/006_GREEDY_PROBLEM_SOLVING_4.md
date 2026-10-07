# Greedy Problem Solving 4 — Level 3
## Detailed Self-Study Notes — Prerequisites + Derivations + Inline Examples + ASCII Diagrams + C++17

> **Goal:** understand how the greedy observation is derived and proved instead of memorizing the final code.
>
> **Problem flow used throughout this note:**
>
> ```text
> prerequisites
> → what the problem asks
> → story → variables
> → tiny example first
> → brute-force / obvious thought
> → key observation
> → greedy claim
> → proof with inline numerical example
> → general mathematical derivation
> → full dry run
> → ASCII visualization
> → algorithm
> → C++17
> → complexity
> → edge cases
> → recognition model
> → don't-memorize model
> ```
>
> **Proof style:** symbols are introduced only after the idea is clear with actual numbers.
>
> **Math rendering:** display equations use fenced `math` blocks only.

---

# Clickable Table of Contents

- [0. How to Use This Note](#0-how-to-use-this-note)
- [1. Shared Prerequisites](#1-shared-prerequisites)
  - [1.1 Constraint vs Objective](#11-constraint-vs-objective)
  - [1.2 Greedy Choice](#12-greedy-choice)
  - [1.3 Exchange Argument](#13-exchange-argument)
  - [1.4 Gaps Between Sorted Positions](#14-gaps-between-sorted-positions)
  - [1.5 Base Cost Minus Savings](#15-base-cost-minus-savings)
  - [1.6 Absolute Difference](#16-absolute-difference)
  - [1.7 Squared Error](#17-squared-error)
  - [1.8 Marginal Gain](#18-marginal-gain)
  - [1.9 Max-Heap](#19-max-heap)
  - [1.10 Regular Bracket Sequence](#110-regular-bracket-sequence)
  - [1.11 Prefix Balance](#111-prefix-balance)
  - [1.12 Question-Mark Completion](#112-question-mark-completion)
  - [1.13 Uniqueness by Least-Disruptive Swap](#113-uniqueness-by-least-disruptive-swap)
  - [1.14 Universal Greedy Proof Checklist](#114-universal-greedy-proof-checklist)
- [2. Problem 1 — Tape](#2-problem-1--tape)
- [3. Problem 2 — Minimize The Error](#3-problem-2--minimize-the-error)
- [4. Problem 3 — Recover an RBS](#4-problem-3--recover-an-rbs)
- [5. Pattern Comparison](#5-pattern-comparison)
- [6. Final Recognition Checklist](#6-final-recognition-checklist)
- [7. Compact Revision Card](#7-compact-revision-card)

---

# 0. How to Use This Note

For every problem:

```text
1. Understand what is being optimized.
2. Draw the tiny example.
3. Identify the local choice.
4. Ask why another local choice is no better.
5. Read the numerical proof.
6. Generalize it algebraically.
7. Implement only after the proof is clear.
8. Re-solve later from the Recognition Model.
```

The three lecture problems train different greedy patterns:

```text
Tape
→ one big interval
→ splitting creates savings
→ choose largest gaps

Minimize The Error
→ each operation reduces one difference
→ compare marginal reduction of d²
→ always reduce largest difference

Recover an RBS
→ construct the most prefix-friendly completion
→ test the least-disruptive alternative
→ uniqueness via one boundary swap
```

---

# 1. Shared Prerequisites

## 1.1 Constraint vs Objective

Always separate:

```text
CONSTRAINT
→ what must remain valid

OBJECTIVE
→ what we minimize/maximize
```

### Tape

```text
Constraint:
all broken positions must be covered
using at most k tape pieces

Objective:
minimize total tape length
```

### Minimize The Error

```text
Constraint:
perform exactly k1 operations on A
and exactly k2 operations on B

Objective:
minimize sum of squared differences
```

### Recover an RBS

```text
Constraint:
replace every '?' by '(' or ')'

Objective:
determine whether exactly one completion
forms a Regular Bracket Sequence
```

---

## 1.2 Greedy Choice

A greedy algorithm makes a local decision such as:

```text
cut at the largest gap
```

or:

```text
reduce the largest current difference
```

But the decision is useful only when we can prove:

```text
choosing anything else cannot produce
a better final answer
```

---

## 1.3 Exchange Argument

General structure:

```text
Greedy chooses G
Other solution chooses O
        |
        v
replace O with G
        |
        v
still feasible?
        |
       YES
        |
        v
objective same/better?
        |
       YES
        |
        v
G is safe
```

We use this directly for Tape.

---

## 1.4 Gaps Between Sorted Positions

Suppose marked positions are sorted:

```text
a1 < a2 < a3 < ... < an
```

Between consecutive marked positions:

```text
a[i] and a[i+1]
```

there may be unneeded cells.

Example:

```text
marked:
2 and 7
```

Positions:

```text
2 3 4 5 6 7
* . . . . *
```

Distance:

```text
7-2 = 5
```

If one tape piece connects them, it covers:

```text
2,3,4,5,6,7
```

The useless interior cells are:

```text
3,4,5,6
```

Count:

```math
7-2-1=4
```

So the **saving** from splitting between these two marked positions is:

```math
gapSaving
=
a_{i+1}-a_i-1
```

---

## 1.5 Base Cost Minus Savings

Many interval greedy problems become easier if written as:

```text
answer
=
cost with no cuts
-
savings from chosen cuts
```

Example:

```text
one long tape costs 20
```

Possible cut savings:

```text
2, 7, 4
```

If allowed two cuts, choose:

```text
7 and 4
```

Then:

```text
answer
= 20 - 7 - 4
= 9
```

So minimizing final cost becomes:

```text
maximize total savings
```

This is the core transformation for Tape.

---

## 1.6 Absolute Difference

For arrays `A` and `B`, define:

```math
d_i=|a_i-b_i|
```

Example:

```text
a_i = 6
b_i = 3
```

Then:

```text
d_i
= |6-3|
= 3
```

If one operation changes either side in the helpful direction:

```text
6 → 5
```

or:

```text
3 → 4
```

new difference:

```text
2
```

So one useful operation can change:

```text
d → d-1
```

when `d > 0`.

---

## 1.7 Squared Error

The error is:

```math
E
=
\sum_i d_i^2
```

Example differences:

```text
[3,2,1]
```

Then:

```text
E
= 3² + 2² + 1²
= 9 + 4 + 1
= 14
```

Notice squaring makes large differences expensive.

---

## 1.8 Marginal Gain

Suppose current difference is:

```text
d
```

Before one helpful operation:

```math
E_{\text{before}}=d^2
```

After reducing by one:

```math
E_{\text{after}}=(d-1)^2
```

Improvement:

```math
\Delta
=
d^2-(d-1)^2
```

Expand:

```math
(d-1)^2
=
d^2-2d+1
```

Therefore:

```math
\Delta
=
d^2-(d^2-2d+1)
```

Cancel:

```math
\Delta
=
2d-1
```

### Inline Example — `d = 5`

Before:

```text
5² = 25
```

After:

```text
4² = 16
```

Gain:

```text
25-16
= 9
```

Formula:

```text
2d-1
= 2×5-1
= 9
```

So the larger `d` is, the larger the immediate reduction.

---

## 1.9 Max-Heap

A max-heap lets us repeatedly access:

```text
largest current value
```

C++:

```cpp
priority_queue<long long> pq;
```

Operations:

```text
pq.top()
→ largest

pq.pop()
→ remove largest

pq.push(x)
→ insert x
```

Perfect for:

```text
always reduce the largest current difference
```

---

## 1.10 Regular Bracket Sequence

Map:

```text
'(' → +1
')' → -1
```

An RBS requires:

```text
1. final balance = 0
2. every prefix balance >= 0
```

Example:

```text
s = (())

char:     (  (  )  )
balance:  1  2  1  0
```

Valid.

Invalid:

```text
s = )(

balance:
-1,0
```

The first prefix is negative.

---

## 1.11 Prefix Balance

Define:

```math
balance(i)
=
\#'(' \text{ in prefix}
-
\#')' \text{ in prefix}
```

ASCII view:

```text
valid RBS:

balance
  2 |      *
  1 |  *       *
  0 |______________*
      1   2   3   4

never below 0
```

A negative prefix means:

```text
we closed more brackets
than we had opened
```

which can never be repaired later.

---

## 1.12 Question-Mark Completion

Suppose length is:

```text
n
```

An RBS must contain:

```text
n/2 opens
n/2 closes
```

Let:

```text
openFixed  = existing '(' count
closeFixed = existing ')' count
```

Then question marks must supply:

```math
needOpen
=
\frac{n}{2}-openFixed
```

and:

```math
needClose
=
\frac{n}{2}-closeFixed
```

Example:

```text
n = 8
fixed '(' = 2
fixed ')' = 3
```

Need:

```text
4-2 = 2 more '('
4-3 = 1 more ')'
```

So there must be:

```text
3 question marks
```

with assignment:

```text
2 opens
1 close
```

---

## 1.13 Uniqueness by Least-Disruptive Swap

To create the most prefix-friendly completion:

```text
assign the earliest needed '?' as '('
and all later remaining '?' as ')'
```

Why?

Opening brackets increase prefix balance.

Putting them earlier makes prefixes as large as possible.

Now suppose both types of assigned `?` exist.

Let:

```text
p = last '?' assigned '('
q = first '?' assigned ')'
```

with:

```text
p < q
```

The **closest possible alternative** is to swap:

```text
p: '(' → ')'
q: ')' → '('
```

This preserves total counts.

Its effect on prefix balance is:

```text
before p:
same

from p through q-1:
balance decreases by 2

from q onward:
same again
```

ASCII:

```text
canonical:
... ( ........ ) ...
    p          q

swapped:
... ) ........ ( ...
    p          q

prefix effect:

before p      :  0 change
[p, q-1]      : -2
q onward      :  0 change
```

If even this **least-disruptive alternative** is invalid, every other different assignment is worse for some relevant prefix.

This is the core uniqueness proof in Recover an RBS.

---

## 1.14 Universal Greedy Proof Checklist

Ask:

```text
1. Can I rewrite minimization as:
   base cost - savings?

2. If I can choose cuts,
   what is the saving of one cut?

3. Are cut savings independent?

4. Is my objective convex, like d²?

5. What is the marginal benefit
   of one operation?

6. Does larger state give larger benefit?

7. Can a heap maintain the best next choice?

8. For bracket completion,
   what assignment maximizes prefix balance?

9. What is the smallest possible deviation
   from the canonical completion?

10. Can one swap test uniqueness?
```

---

# 2. Problem 1 — Tape

**Platform:** Codeforces  
**Problem:** 1110B — Tape  
**Link:** https://codeforces.com/problemset/problem/1110/B

---

## 2.1 What the Problem Asks

There are:

```text
n broken positions
```

at sorted coordinates:

```text
a1 < a2 < ... < an
```

We can use at most:

```text
k tape pieces
```

Each tape piece covers one continuous interval.

Goal:

```text
cover every broken position
with minimum total tape length
```

---

## 2.2 Tiny Example First

Broken positions:

```text
2,3,7,8,12,13
```

Let:

```text
k = 3
```

If we use one long tape:

```text
from 2 to 13
```

length:

```text
13-2+1
= 12
```

ASCII:

```text
position:
2 3 4 5 6 7 8 9 10 11 12 13
* * . . . * * .  .  .  *  *

one tape:
[--------------------------]
length = 12
```

But there are large empty gaps:

```text
3 → 7
8 → 12
```

We can split there.

Result:

```text
[2,3]
[7,8]
[12,13]
```

Total length:

```text
2+2+2
= 6
```

---

## 2.3 First Model — Start With One Tape

With one piece covering everything:

```math
base
=
a_n-a_1+1
```

For example:

```text
a1 = 2
an = 13

base
= 13-2+1
= 12
```

Now adding more pieces means:

```text
cut the long interval at some gaps
```

Each cut saves unused tape.

---

## 2.4 Saving From One Cut

Take consecutive broken positions:

```text
3 and 7
```

Without a cut, tape also covers:

```text
4,5,6
```

Those are:

```text
3 useless cells
```

Saving:

```math
7-3-1
=
3
```

General:

```math
saving_i
=
a_{i+1}-a_i-1
```

---

## 2.5 Inline Derivation of the Saving

Suppose one continuous piece spans:

```text
a_i ... a_(i+1)
```

Its contribution across this region is:

```math
a_{i+1}-a_i+1
```

If we cut between them, only endpoints need their own neighboring pieces.

The cells strictly between them can be left uncovered.

Number of interior cells:

```math
(a_{i+1}-1)-(a_i+1)+1
```

Simplify:

```math
a_{i+1}-a_i-1
```

Example:

```text
a_i = 3
a_(i+1) = 7

interior:
4,5,6

count:
7-3-1
= 3
```

---

## 2.6 How Many Cuts Can We Make?

One tape piece:

```text
0 cuts
```

Two pieces:

```text
1 cut
```

Three pieces:

```text
2 cuts
```

So with:

```text
k pieces
```

we may use up to:

```math
k-1
```

cuts.

Since every saving is non-negative, using a cut on a positive gap never hurts.

---

## 2.7 Greedy Observation

Each cut has an independent saving:

```text
gap 1 → save s1
gap 2 → save s2
...
```

We may choose:

```text
k-1 cuts
```

To maximize total saving:

```text
choose the k-1 largest gap savings
```

Then:

```text
minimum tape
=
base
-
sum(largest k-1 savings)
```

---

## 2.8 Proof With Numbers First

Suppose possible savings are:

```text
1,3,7,4
```

and we may choose:

```text
2 cuts
```

Greedy:

```text
7 and 4
```

Total saving:

```text
11
```

Suppose another solution chooses:

```text
7 and 3
```

Saving:

```text
10
```

Replacing the smaller chosen saving:

```text
3
```

with the unchosen larger saving:

```text
4
```

improves by:

```text
4-3
= 1
```

So any solution that skips a larger gap while cutting a smaller gap cannot be optimal.

---

## 2.9 General Exchange Proof

Suppose another solution cuts gap:

```text
x
```

but does not cut a larger gap:

```text
y
```

where:

```math
y>x
```

Swap the cut:

```text
remove cut at x
add cut at y
```

Number of tape pieces stays unchanged.

Coverage remains valid.

Saving changes by:

```math
y-x
```

Since:

```math
y-x>0
```

the new solution uses less tape.

Therefore an optimal solution cannot choose a smaller saving while skipping a larger one.

So:

```text
choose the largest k-1 savings
```

---

## 2.10 Full Dry Run

Broken positions:

```text
2,3,7,8,12,13
```

`n = 6`, `k = 3`.

### Step 1 — Base Cost

```text
base
= 13-2+1
= 12
```

### Step 2 — Gap Savings

Between `2,3`:

```text
3-2-1
= 0
```

Between `3,7`:

```text
7-3-1
= 3
```

Between `7,8`:

```text
8-7-1
= 0
```

Between `8,12`:

```text
12-8-1
= 3
```

Between `12,13`:

```text
13-12-1
= 0
```

Savings:

```text
[0,3,0,3,0]
```

### Step 3 — Number of Cuts

```text
k-1
= 3-1
= 2
```

Choose two largest:

```text
3,3
```

Total saving:

```text
6
```

### Step 4 — Final Cost

```text
12-6
= 6
```

---

## 2.11 ASCII Visualization

```text
positions:
2 3 4 5 6 7 8 9 10 11 12 13
* * . . . * * .  .  .  *  *

one piece:
[--------------------------]
cost = 12


largest empty gaps:

2 3 | 4 5 6 | 7 8 | 9 10 11 | 12 13
* *           * *              *  *

cut here ↑          cut here ↑


three pieces:

[2--3]     [7--8]       [12--13]

lengths:
2 + 2 + 2 = 6
```

---

## 2.12 Equivalent Connection View

Another way:

Start with each broken cell separately.

Cost:

```text
n
```

To reduce number of pieces from `n` to `k`, we need:

```text
n-k
```

connections between adjacent broken positions.

Connecting:

```text
a_i and a_(i+1)
```

adds extra tape:

```math
a_{i+1}-a_i-1
```

So choose the:

```text
n-k smallest connection costs
```

Equivalent to:

```text
cut the k-1 largest gaps
```

Both viewpoints produce the same answer.

---

## 2.13 Algorithm

```text
1. Positions are sorted.

2. base = a[n-1] - a[0] + 1.

3. For each adjacent pair:
      saving = a[i+1] - a[i] - 1.

4. Sort savings descending.

5. Subtract the largest k-1 savings.

6. Print base.
```

---

## 2.14 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m, k;
    cin >> n >> m >> k;

    vector<long long> a(n);

    for (long long& x : a)
        cin >> x;

    if (k >= n) {
        cout << n << '\n';
        return 0;
    }

    long long answer =
        a.back() - a.front() + 1;

    vector<long long> saving;

    for (int i = 0; i + 1 < n; ++i) {
        saving.push_back(
            a[i + 1] - a[i] - 1
        );
    }

    sort(
        saving.rbegin(),
        saving.rend()
    );

    for (int i = 0; i < k - 1; ++i) {
        answer -= saving[i];
    }

    cout << answer << '\n';
}
```

---

## 2.15 Complexity

Build gaps:

```text
O(N)
```

Sort:

```text
O(N log N)
```

Total:

```text
O(N log N)
```

Space:

```text
O(N)
```

---

## 2.16 Edge Cases

### `k = 1`

No cuts.

Answer:

```math
a_n-a_1+1
```

### `k >= n`

Every broken cell can have its own length-1 tape.

Answer:

```text
n
```

### Adjacent broken positions

Example:

```text
5,6
```

Saving:

```text
6-5-1
= 0
```

Cutting there gives no benefit.

---

## 2.17 Recognition Model

```text
sorted marked points
+
cover with limited number of intervals
+
minimize total covered length
        |
        v
start with one large interval
        |
        v
a cut saves an interior gap
        |
        v
choose largest savings
```

---

## 2.18 Don't-Memorize Model

Do not memorize:

```text
sort gaps
subtract k-1 largest
```

Remember:

```text
one tape covers everything

every extra tape lets me create one cut

a cut removes exactly the useless cells
between two consecutive broken positions

therefore:
cut where the useless region is largest
```

---


# 3. Problem 2 — Minimize The Error

**Platform:** Codeforces  
**Problem:** 960B — Minimize The Error  
**Link:** https://codeforces.com/problemset/problem/960/B

---

## 3.1 What the Problem Asks

We have two arrays:

```text
A = [a1, a2, ..., an]
B = [b1, b2, ..., bn]
```

Error:

```math
E
=
(a_1-b_1)^2
+
(a_2-b_2)^2
+\cdots+
(a_n-b_n)^2
```

We must perform:

```text
k1 operations on A
k2 operations on B
```

Each operation changes one chosen element by:

```text
+1 or -1
```

Goal:

```text
minimize final E
```

---

## 3.2 First Transformation — Only Differences Matter

For every index define:

```math
d_i=|a_i-b_i|
```

Then:

```math
E=\sum_i d_i^2
```

So the original values no longer matter directly.

We only need:

```text
d1, d2, ..., dn
```

---

## 3.3 Inline Example

Suppose:

```text
A = [6, 2, 8]
B = [3, 4, 7]
```

Differences:

```text
d1 = |6-3| = 3
d2 = |2-4| = 2
d3 = |8-7| = 1
```

So:

```text
d = [3,2,1]
```

Error:

```text
3² + 2² + 1²
= 9 + 4 + 1
= 14
```

The problem has become:

```text
perform K operations
on the differences
to minimize
Σ d_i²
```

where:

```math
K=k_1+k_2
```

---

## 3.4 Why `k1 + k2` Can Be Combined

Suppose:

```text
a_i > b_i
```

Example:

```text
a_i = 6
b_i = 3
difference = 3
```

An operation on `A` can reduce difference:

```text
6 → 5

|5-3|
= 2
```

An operation on `B` can also reduce difference:

```text
3 → 4

|6-4|
= 2
```

So while difference is positive:

```text
either type of operation
can reduce d by 1
```

Similarly if:

```text
a_i < b_i
```

we can move either side toward the other.

Therefore, for deciding **which index** should receive the next useful operation, only:

```math
K=k_1+k_2
```

matters.

---

## 3.5 Naive Thought

A tempting rule is:

```text
choose any nonzero difference
and reduce it
```

But because the objective uses squares:

```text
large differences hurt much more
```

Example:

```text
reduce 5 → 4

error reduction:
25-16
= 9
```

But:

```text
reduce 2 → 1

error reduction:
4-1
= 3
```

So not all operations are equally valuable.

---

## 3.6 Derive the Benefit of Reducing Difference `d`

Before:

```math
E_{\text{before}}=d^2
```

After:

```math
E_{\text{after}}=(d-1)^2
```

Benefit:

```math
\Delta(d)
=
d^2-(d-1)^2
```

Expand:

```math
(d-1)^2
=
d^2-2d+1
```

Therefore:

```math
\Delta(d)
=
d^2-(d^2-2d+1)
```

Cancel `d²`:

```math
\Delta(d)
=
2d-1
```

---

## 3.7 Inline Example — `d = 5`

Before:

```text
5²
= 25
```

After reducing to `4`:

```text
4²
= 16
```

Benefit:

```text
25-16
= 9
```

Formula:

```text
2×5-1
= 10-1
= 9
```

Matches.

---

## 3.8 Inline Example — Compare `d = 5` and `d = 3`

For `d=5`:

```text
benefit
= 2×5-1
= 9
```

For `d=3`:

```text
benefit
= 2×3-1
= 5
```

So:

```text
9 > 5
```

Reducing the larger difference gives the larger immediate decrease in total error.

---

## 3.9 Greedy Claim

At every useful operation:

```text
reduce the largest current difference
```

because its marginal reduction:

```math
2d-1
```

is maximal.

---

## 3.10 General Comparison Proof

Suppose:

```math
x\ge y>0
```

Greedy reduces `x`.

Benefit:

```math
G
=
x^2-(x-1)^2
```

Alternative reduces `y`.

Benefit:

```math
O
=
y^2-(y-1)^2
```

Using the derived formula:

```math
G=2x-1
```

and:

```math
O=2y-1
```

Subtract:

```math
G-O
=
(2x-1)-(2y-1)
```

Simplify:

```math
G-O
=
2x-2y
```

Factor:

```math
G-O
=
2(x-y)
```

Since:

```math
x\ge y
```

we have:

```math
x-y\ge0
```

Therefore:

```math
G-O\ge0
```

So reducing the larger difference gives at least as much benefit.

---

## 3.11 Same Proof With Numbers

Take:

```text
x = 6
y = 4
```

Greedy benefit:

```text
6²-5²
= 36-25
= 11
```

Alternative:

```text
4²-3²
= 16-9
= 7
```

Difference:

```text
11-7
= 4
```

Formula:

```text
2(x-y)

= 2(6-4)

= 4
```

So the algebra exactly matches the numerical comparison.

---

## 3.12 Why a Max-Heap Fits Perfectly

After reducing the current maximum:

```text
d → d-1
```

it may no longer be the maximum.

So we need to repeatedly:

```text
1. get largest difference
2. reduce it
3. put it back
```

That is exactly:

```text
max-heap
```

---

## 3.13 Max-Heap ASCII Diagram

Suppose differences are:

```text
[7,5,3,2]
```

Heap conceptually:

```text
        7
      /   \
     5     3
    /
   2
```

Take top:

```text
7
```

Reduce:

```text
7 → 6
```

Push back:

```text
        6
      /   \
     5     3
    /
   2
```

Next operation again chooses:

```text
6
```

if still largest.

---

## 3.14 Full Dry Run

Differences:

```text
[4,2,1]
```

Let:

```text
K = 4
```

Initial error:

```text
4²+2²+1²
= 16+4+1
= 21
```

### Operation 1

Largest:

```text
4
```

Reduce:

```text
4 → 3
```

Now:

```text
[3,2,1]
```

Error:

```text
9+4+1
= 14
```

Reduction:

```text
7
```

---

### Operation 2

Largest:

```text
3
```

Reduce:

```text
3 → 2
```

Now:

```text
[2,2,1]
```

Error:

```text
4+4+1
= 9
```

---

### Operation 3

Largest:

```text
2
```

Reduce one:

```text
2 → 1
```

Now:

```text
[2,1,1]
```

Error:

```text
4+1+1
= 6
```

---

### Operation 4

Largest:

```text
2
```

Reduce:

```text
2 → 1
```

Final:

```text
[1,1,1]
```

Error:

```text
1+1+1
= 3
```

Answer:

```text
3
```

---

## 3.15 What Happens When All Differences Reach Zero?

Operations are mandatory.

Suppose:

```text
all d_i = 0
```

and one operation remains.

Any operation changes one equal pair into difference:

```text
1
```

So error becomes:

```text
1
```

With another operation, we can undo it:

```text
1 → 0
```

Therefore extra operations after all-zero behave by parity:

```text
even number remaining
→ final error can return to 0

odd number remaining
→ minimum final error is 1
```

---

## 3.16 Inline Parity Example

All differences:

```text
[0,0,0]
```

One operation:

```text
[1,0,0]
```

Error:

```text
1
```

Second operation:

```text
[0,0,0]
```

Error:

```text
0
```

So:

```text
remaining operations even → 0
remaining operations odd  → 1
```

---

## 3.17 Algorithm

Lecture-style heap approach:

```text
1. K = k1+k2.

2. For every i:
      d = abs(a[i]-b[i])
      push d into max-heap.

3. Repeat K times:
      d = heap.top()
      pop

      if d > 0:
          d--

      else:
          d = 1

      push d back.

4. Answer:
      sum d² over heap.
```

Why `0 → 1`?

Because an operation is mandatory.

If the two values are equal, changing either by `±1` creates absolute difference `1`.

---

## 3.18 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    long long k1, k2;

    cin >> n >> k1 >> k2;

    vector<long long> a(n);
    vector<long long> b(n);

    for (long long& x : a)
        cin >> x;

    for (long long& x : b)
        cin >> x;

    priority_queue<long long> pq;

    for (int i = 0; i < n; ++i) {
        pq.push(
            llabs(a[i] - b[i])
        );
    }

    long long K = k1 + k2;

    while (K--) {
        long long d = pq.top();
        pq.pop();

        if (d > 0)
            --d;
        else
            d = 1;

        pq.push(d);
    }

    long long answer = 0;

    while (!pq.empty()) {
        long long d = pq.top();
        pq.pop();

        answer += d * d;
    }

    cout << answer << '\n';
}
```

---

## 3.19 Complexity

Let:

```text
K = k1+k2
```

Building heap:

```text
O(N)
```

Each operation:

```text
pop + push
= O(log N)
```

Total:

```text
O((N+K) log N)
```

Space:

```text
O(N)
```

---

## 3.20 Edge Cases

### Difference already zero

A mandatory operation makes it:

```text
1
```

### More operations than needed to make all differences zero

Only parity of extra operations matters conceptually.

### Equal maximum differences

Reducing either gives the same marginal benefit.

So heap may choose any of them.

---

## 3.21 Recognition Model

```text
minimize sum of convex costs
+
one operation changes one coordinate by 1
        |
        v
derive marginal improvement
        |
        v
for d²:
benefit = 2d-1
        |
        v
larger d gives larger benefit
        |
        v
max-heap
```

---

## 3.22 Don't-Memorize Model

Do not memorize:

```text
push differences into heap
decrement max
```

Remember:

```text
error contribution = d²

one reduction:
d → d-1

benefit:
d²-(d-1)²
= 2d-1

larger d
→ larger benefit

therefore:
reduce largest difference first
```

---

# 4. Problem 3 — Recover an RBS

**Platform:** Codeforces  
**Problem:** 1709C — Recover an RBS  
**Link:** https://codeforces.com/problemset/problem/1709/C

---

## 4.1 What the Problem Asks

String contains:

```text
'('
')'
'?'
```

Every `?` must become:

```text
'(' or ')'
```

We need to determine whether the string has:

```text
exactly one
```

completion that is a Regular Bracket Sequence.

Output:

```text
YES
```

if the valid RBS completion is unique.

Otherwise:

```text
NO
```

---

## 4.2 RBS Conditions

A valid RBS needs:

```text
1. equal number of '(' and ')'
2. every prefix has at least as many '(' as ')'
```

Using balance:

```text
'(' → +1
')' → -1
```

Conditions:

```text
final balance = 0
```

and:

```text
every prefix balance >= 0
```

---

## 4.3 First Count Requirement

For length:

```text
n
```

an RBS needs:

```text
n/2 opens
n/2 closes
```

Let:

```text
fixedOpen
fixedClose
```

be existing counts.

Then:

```math
needOpen
=
\frac{n}{2}-fixedOpen
```

and:

```math
needClose
=
\frac{n}{2}-fixedClose
```

---

## 4.4 Inline Example

Suppose:

```text
n = 8
```

Existing:

```text
fixedOpen = 2
fixedClose = 3
```

An RBS needs:

```text
4 opens
4 closes
```

Therefore:

```text
needOpen
= 4-2
= 2
```

and:

```text
needClose
= 4-3
= 1
```

So among the 3 question marks:

```text
2 must become '('
1 must become ')'
```

The counts are forced.

Only their positions may vary.

---

## 4.5 Canonical Greedy Completion

To make prefix balances as safe as possible:

```text
put required '(' as early as possible
```

So scan `?` from left to right:

```text
first needOpen question marks → '('
remaining question marks      → ')'
```

This is the **canonical completion**.

Why is it the safest?

Because:

```text
'(' increases balance by 1
')' decreases balance by 1
```

Putting opens earlier maximizes every prefix balance.

---

## 4.6 Tiny Example — Multiple Completions

String:

```text
(??)
```

Length:

```text
4
```

Fixed:

```text
'(' = 1
')' = 1
```

Need:

```text
one more '('
one more ')'
```

Canonical:

```text
( ( ) )
```

So:

```text
(())
```

which is RBS.

But another assignment:

```text
( ) ( )
```

gives:

```text
()()
```

also RBS.

Therefore:

```text
NO
```

because the completion is not unique.

---

## 4.7 Tiny Example — Unique Completion

String:

```text
??()
```

Length:

```text
4
```

Fixed:

```text
'(' = 1
')' = 1
```

Need:

```text
one '('
one ')'
```

Canonical:

```text
()()
```

Alternative swap:

```text
)(()
```

Prefix balance starts:

```text
-1
```

Invalid.

So only one RBS completion exists.

Answer:

```text
YES
```

---

## 4.8 Why the Canonical Completion Is Important

Suppose canonical completion itself has a negative prefix.

Since canonical places opens as early as possible, it has the **maximum possible prefix balance** among all completions with the required total counts.

So if even canonical fails:

```text
no other completion can repair that prefix
```

This provides a useful feasibility perspective.

For the uniqueness logic, the key is the closest alternative to canonical.

---

## 4.9 Identify the Boundary Pair

Canonical question-mark assignments look like:

```text
? ? ? ? ? ?
( ( ( ) ) )
```

There is a boundary:

```text
last assigned '('
first assigned ')'
```

Let:

```text
p = position of last '?' assigned '('
q = position of first '?' assigned ')'
```

By construction:

```text
p < q
```

ASCII:

```text
question marks:
?   ?   ?   ?   ?   ?
|   |   |   |   |   |
(   (   (   )   )   )
        ^   ^
        p   q
```

---

## 4.10 Construct the Least-Disruptive Alternative

Swap only these two assignments:

```text
p:
'(' → ')'

q:
')' → '('
```

Counts remain unchanged.

Canonical:

```text
... ( ........ ) ...
    p          q
```

Alternative:

```text
... ) ........ ( ...
    p          q
```

This is the closest possible different assignment.

---

## 4.11 Prefix-Balance Effect of the Swap

At `p`:

```text
'(' → ')'
```

Change in contribution:

```text
+1 → -1
```

So balance drops by:

```text
2
```

At `q`:

```text
')' → '('
```

Change:

```text
-1 → +1
```

So balance gains back:

```text
2
```

Therefore:

```text
before p:
no change

p through q-1:
canonical balance - 2

q onward:
no change
```

---

## 4.12 Inline Example of the Balance Change

Suppose canonical prefix balances around the boundary are:

```text
index:      ... p   p+1 p+2   q ...
canonical:      4    3   2    3
```

After swap:

```text
index:      ... p   p+1 p+2   q ...
swapped:        2    1   0    3
```

All affected balances stay:

```text
>= 0
```

So swapped string is also an RBS.

Therefore:

```text
at least two valid completions
→ answer NO
```

---

## 4.13 Example Where the Swap Breaks the RBS

Canonical affected balances:

```text
2,1,2
```

Subtract `2` inside the affected interval:

```text
0,-1,0
```

A negative prefix appears.

So swapped assignment is invalid.

If this is the least-disruptive alternative, every other different assignment is at least as damaging somewhere.

Therefore:

```text
canonical completion is unique
→ YES
```

---

## 4.14 Why This Swap Is the Least Disruptive

Canonical assigns:

```text
early '?' → '('
late '?'  → ')'
```

Any different assignment with the same total counts must do both:

```text
some canonical '(' question mark
must become ')'

and

some canonical ')' question mark
must become '('
```

To hurt prefix balances as little as possible:

```text
move the '(' to ')' as late as possible
```

and:

```text
move the ')' to '(' as early as possible
```

Those positions are exactly:

```text
p = last canonical '('
q = first canonical ')'
```

So the boundary swap creates the shortest possible interval where balance is reduced by `2`.

Any more distant swap reduces balance over an interval that is at least as troublesome.

Thus:

```text
if boundary swap is invalid,
no other distinct valid completion exists
```

---

## 4.15 ASCII Visualization of the Uniqueness Proof

```text
Canonical ? assignments:

... ?   ?   ?   ?   ?   ? ...
    (   (   (   )   )   )
            ^   ^
            p   q


Canonical balance effect:

before p          [ unchanged ]
p ........ q-1    [ normal    ]
q onward          [ unchanged ]


Swap boundary pair:

... (   (   )   (   )   ) ...
            ^   ^
            p   q


Balance comparison:

before p:
canonical = alternative

p ... q-1:
alternative = canonical - 2

q onward:
canonical = alternative
```

Decision:

```text
swapped string is RBS?
       |
   +---+---+
   |       |
  YES      NO
   |       |
multiple   unique
   |       |
  NO      YES
(output)  (output)
```

---

## 4.16 Special Case — One Assignment Type Is Missing

If:

```text
needOpen = 0
```

then every `?` must be:

```text
')'
```

No assignment choice exists.

Likewise if:

```text
needClose = 0
```

every `?` must be:

```text
'('
```

So if the resulting sequence is valid:

```text
completion is unique
```

Output:

```text
YES
```

---

## 4.17 Full Dry Run — `(??)`

Input:

```text
(??)
```

### Count

```text
n = 4

fixedOpen  = 1
fixedClose = 1

needOpen
= 2-1
= 1

needClose
= 2-1
= 1
```

### Canonical Fill

First `?`:

```text
'('
```

Second `?`:

```text
')'
```

Canonical:

```text
(())
```

Balance:

```text
1,2,1,0
```

Valid.

### Boundary

```text
p = first ?
q = second ?
```

Swap:

```text
()()
```

Balance:

```text
1,0,1,0
```

Also valid.

Therefore:

```text
multiple RBS completions
```

Answer:

```text
NO
```

---

## 4.18 Full Dry Run — `??()`

Input:

```text
??()
```

Counts require:

```text
one '('
one ')'
```

Canonical:

```text
()()
```

Balance:

```text
1,0,1,0
```

Boundary swap:

```text
)(()
```

Balance:

```text
-1,0,1,0
```

First prefix:

```text
-1
```

Invalid.

Therefore:

```text
unique valid completion
```

Answer:

```text
YES
```

---

## 4.19 RBS Checker

A helper:

```cpp
bool isRBS(const string& s) {
    int balance = 0;

    for (char c : s) {
        if (c == '(')
            ++balance;
        else
            --balance;

        if (balance < 0)
            return false;
    }

    return balance == 0;
}
```

This directly implements:

```text
every prefix >= 0
and
final balance = 0
```

---

## 4.20 Algorithm

```text
1. Count fixed '(' and ')'.

2. Compute:
      needOpen  = n/2 - fixedOpen
      needClose = n/2 - fixedClose

3. Fill question marks canonically:
      earliest needOpen '?' → '('
      remaining '?'          → ')'

4. Record:
      p = last '?' changed to '('
      q = first '?' changed to ')'

5. If p or q does not exist:
      only one assignment by counts
      → YES

6. Otherwise swap assignments at p and q.

7. Check swapped string:

      valid RBS?
      → NO, because another valid completion exists

      invalid?
      → YES, canonical completion is unique
```

---

## 4.21 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isRBS(const string& s) {
    int balance = 0;

    for (char c : s) {
        if (c == '(')
            ++balance;
        else
            --balance;

        if (balance < 0)
            return false;
    }

    return balance == 0;
}

void solve() {
    string s;
    cin >> s;

    int n = (int)s.size();

    int fixedOpen = 0;
    int fixedClose = 0;

    for (char c : s) {
        if (c == '(')
            ++fixedOpen;
        else if (c == ')')
            ++fixedClose;
    }

    int needOpen =
        n / 2 - fixedOpen;

    int needClose =
        n / 2 - fixedClose;

    int lastOpenQuestion = -1;
    int firstCloseQuestion = -1;

    for (int i = 0; i < n; ++i) {
        if (s[i] != '?')
            continue;

        if (needOpen > 0) {
            s[i] = '(';
            --needOpen;
            lastOpenQuestion = i;
        } else {
            s[i] = ')';

            if (firstCloseQuestion == -1)
                firstCloseQuestion = i;
        }
    }

    // No boundary between assigned '(' and assigned ')'.
    // Therefore the counts force every '?' uniquely.
    if (
        lastOpenQuestion == -1 ||
        firstCloseQuestion == -1
    ) {
        cout << "YES\n";
        return;
    }

    swap(
        s[lastOpenQuestion],
        s[firstCloseQuestion]
    );

    if (isRBS(s))
        cout << "NO\n";
    else
        cout << "YES\n";
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        solve();
    }
}
```

---

## 4.22 Complexity

For each test:

```text
count:
O(N)

canonical fill:
O(N)

RBS validation:
O(N)
```

Total:

```text
O(N)
```

Space:

```text
O(1)
```

apart from the mutable string.

---

## 4.23 Edge Cases

### No `?`

The string is already fixed.

If the problem guarantees recoverability, its completion is trivially unique.

### `needOpen = 0`

All `?` are forced to `)`.

Unique.

### `needClose = 0`

All `?` are forced to `(`.

Unique.

### Boundary swap remains valid

At least two valid completions.

Answer:

```text
NO
```

### Boundary swap creates a negative prefix

Canonical is unique.

Answer:

```text
YES
```

---

## 4.24 Recognition Model

```text
string with '?'
+
need unique valid bracket completion
        |
        v
count required opens/closes
        |
        v
canonical:
opens as early as possible
        |
        v
find boundary:
last assigned '('
first assigned ')'
        |
        v
swap them
        |
        v
still RBS?
   /           \
 YES            NO
  |              |
not unique      unique
```

---

## 4.25 Don't-Memorize Model

Do not memorize:

```text
fill opens first
swap two positions
check
```

Understand:

```text
opens placed early
maximize prefix balance

Any different valid assignment must
move some early assigned '(' later.

The least harmful alternative is:
swap the latest such '('
with the earliest assigned ')'.

If even that alternative breaks
prefix validity,
every more disruptive alternative breaks too.
```

---

# 5. Pattern Comparison

| Problem | Core Signal | Greedy Pattern | Proof Style |
|---|---|---|---|
| Tape | sorted points + limited intervals | largest gap savings | exchange argument |
| Minimize The Error | sum of squares + unit operations | largest marginal benefit | algebraic marginal-gain proof |
| Recover an RBS | wildcard brackets + uniqueness | canonical extreme + nearest alternative | prefix-dominance proof |

---

## 5.1 Tape Pattern

```text
one big interval
      |
      v
each cut removes an empty gap
      |
      v
saving = gap size
      |
      v
choose largest savings
```

---

## 5.2 Minimize Error Pattern

```text
error = Σd²
      |
      v
one operation:
d → d-1
      |
      v
benefit:
2d-1
      |
      v
largest d
gives largest benefit
      |
      v
max-heap
```

---

## 5.3 Recover RBS Pattern

```text
required counts
      |
      v
put '(' as early as possible
      |
      v
canonical maximum-prefix completion
      |
      v
test closest different assignment
      |
      v
boundary swap
      |
      v
RBS?
```

---

# 6. Final Recognition Checklist

```text
1. Can I start with one expensive global solution
   and interpret extra choices as savings?

2. What is the exact saving from one split?

3. Are those savings independent?

4. Does my cost contain squares or another convex function?

5. What is the marginal improvement of one unit operation?

6. Is the marginal improvement monotonic?

7. Do I need a max-heap for repeated best choices?

8. Does the problem ask whether a wildcard completion is unique?

9. Can I build an extreme/canonical completion
   that maximizes all prefix balances?

10. What is the least-disruptive different completion?

11. Can one swap test whether another valid solution exists?
```

---

# 7. Compact Revision Card

```text
GREEDY PROBLEM SOLVING 4
========================


1. TAPE
=======
base:
a[n-1]-a[0]+1

cut between:
a[i], a[i+1]

saving:
a[i+1]-a[i]-1

k pieces
→ k-1 cuts

greedy:
take largest k-1 savings

answer:
base - selected savings


2. MINIMIZE THE ERROR
=====================
difference:
d[i] = |a[i]-b[i]|

error:
Σ d[i]²

one useful operation:
d → d-1

benefit:
d²-(d-1)²
= 2d-1

larger d
→ larger benefit

greedy:
reduce maximum difference

data structure:
max-heap

if all differences are zero
and operations remain:
parity matters


3. RECOVER AN RBS
=================
RBS:
final balance = 0
all prefixes >= 0

need:
n/2 opens
n/2 closes

canonical:
first needOpen '?' → '('
rest → ')'

boundary:
p = last assigned '('
q = first assigned ')'

swap p and q

effect:
balance -2 only on [p,q-1]

swapped still RBS?
YES → another completion exists → NO

swapped invalid?
→ canonical is unique → YES
```

---

# Final Mental Model

```text
             GREEDY PROBLEM SOLVING 4
                        |
        +---------------+----------------+
        |               |                |
    interval gaps   convex error     wildcard RBS
        |               |                |
        v               v                v
  compute savings   marginal gain   extreme completion
        |               |                |
        v               v                v
 largest gaps       max difference   closest alternative
        |               |                |
        v               v                v
 exchange proof       max-heap       boundary swap
```

> **Core lesson:** before writing a greedy rule, identify the exact quantity that one local decision changes: **gap saving**, **marginal error reduction**, or **prefix-balance damage**. Once that quantity is explicit, the greedy choice becomes much easier to prove.
