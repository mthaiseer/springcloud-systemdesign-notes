# AlgoZenith Greedy Exchange
## Optimized TLE-Style Notes — Rearrangement + Pairwise Exchange + Ratio Scheduling

> **Source:** the supplied AlgoZenith **Greedy Exchange** lecture screenshots.
>
> **Goal:** understand how a greedy sorting order is **derived from an exchange proof**, instead of memorizing the final sort comparator.
>
> **Problem flow used throughout:**
>
> ```text
> What it asks
> → small dry run
> → core observation
> → greedy choice
> → exchange proof with inline numbers
> → one detailed dry run
> → algorithm
> → C++17
> → complexity
> → recognition model
> ```
>
> **Variable rule**
>
> - In explanations/code: descriptive names such as `minimumDotProduct`, `completionTime`, `totalScore`.
> - In proofs: shorter but readable names such as `smallA`, `largeA`, `smallB`, `largeB`, `timeA`, `decayA`.

---

# Clickable Table of Contents

- [0. Class Map](#0-class-map)
- [1. Prerequisites](#1-prerequisites)
  - [1.1 What Exchange Greedy Means](#11-what-exchange-greedy-means)
  - [1.2 Local Choice vs Global Optimum](#12-local-choice-vs-global-optimum)
  - [1.3 Why We Compare Only Two Positions](#13-why-we-compare-only-two-positions)
  - [1.4 Inversion](#14-inversion)
  - [1.5 Dot Product](#15-dot-product)
  - [1.6 Opposite-Order Pairing Intuition](#16-opposite-order-pairing-intuition)
  - [1.7 Algebra Needed for Exchange Proofs](#17-algebra-needed-for-exchange-proofs)
  - [1.8 Sign Reasoning](#18-sign-reasoning)
  - [1.9 Completion Time](#19-completion-time)
  - [1.10 Ratio Comparator + Cross Multiplication](#110-ratio-comparator--cross-multiplication)
  - [1.11 Custom Sort Comparator](#111-custom-sort-comparator)
  - [1.12 Overflow and `long long`](#112-overflow-and-long-long)
  - [1.13 Recognition Checklist](#113-recognition-checklist)
- [2. Pattern 1 — Minimum Dot Product With a Permutation](#2-pattern-1--minimum-dot-product-with-a-permutation)
  - [2.1 What It Asks](#21-what-it-asks)
  - [2.2 Small Dry Run First](#22-small-dry-run-first)
  - [2.3 Core Observation](#23-core-observation)
  - [2.4 Greedy Choice](#24-greedy-choice)
  - [2.5 Exchange Proof — Compact + Inline Example](#25-exchange-proof--compact--inline-example)
  - [2.6 Why This Proves the Whole Array](#26-why-this-proves-the-whole-array)
  - [2.7 Detailed Dry Run](#27-detailed-dry-run)
  - [2.8 Algorithm](#28-algorithm)
  - [2.9 C++17](#29-c17)
  - [2.10 Complexity](#210-complexity)
  - [2.11 Recognition Model](#211-recognition-model)
- [3. Pattern 2 — Score / Decay / Time Scheduling](#3-pattern-2--score--decay--time-scheduling)
  - [3.1 What It Asks](#31-what-it-asks)
  - [3.2 Small Dry Run First](#32-small-dry-run-first)
  - [3.3 Core Observation](#33-core-observation)
  - [3.4 Compare Only Two Adjacent Questions](#34-compare-only-two-adjacent-questions)
  - [3.5 Exchange Proof — Every Algebra Step + Inline Example](#35-exchange-proof--every-algebra-step--inline-example)
  - [3.6 Derive the Sorting Rule](#36-derive-the-sorting-rule)
  - [3.7 Why Initial Score Does Not Affect the Order](#37-why-initial-score-does-not-affect-the-order)
  - [3.8 Why the Pairwise Proof Works Anywhere in the Schedule](#38-why-the-pairwise-proof-works-anywhere-in-the-schedule)
  - [3.9 Detailed Dry Run](#39-detailed-dry-run)
  - [3.10 Algorithm](#310-algorithm)
  - [3.11 C++17](#311-c17)
  - [3.12 Complexity](#312-complexity)
  - [3.13 Common Mistakes](#313-common-mistakes)
  - [3.14 Recognition Model](#314-recognition-model)
- [Final Mental Model](#final-mental-model)

---

# 0. Class Map

The lecture develops **exchange argument greedy** through two sorting problems:

```text
GREEDY EXCHANGE
│
├── Rearrangement / Pairing
│   │
│   └── array + permutation
│       minimize dot product
│       → smaller value pairs with larger value
│       → opposite sorting
│
└── Scheduling
    │
    └── each question has:
        score
        decay
        time
        → compare two adjacent questions
        → derive ratio comparator
```

The common strategy is:

```text
Take any solution
      |
      v
find a locally "wrong" pair
      |
      v
swap the two items
      |
      v
prove answer becomes same or better
      |
      v
repeat swaps
      |
      v
greedy sorted order is optimal
```

---

# 1. Prerequisites

## 1.1 What Exchange Greedy Means

An exchange proof does not begin by saying:

```text
"Sort this way because it looks correct."
```

Instead:

```text
1. Assume some valid solution.

2. Find two items that violate
   the greedy order.

3. Swap only those two items.

4. Show:
   - solution stays valid
   - objective becomes same or better

5. Therefore an optimal solution
   can follow the greedy order.
```

Visual:

```text
OTHER / OPTIMAL SOLUTION
       |
       v
... X ... Y ...
       |
       | swap X and Y
       v
... Y ... X ...
       |
       v
still valid?
   YES
       |
       v
same or better?
   YES
       |
       v
greedy order is safe
```

This is the main proof technique used in both problems.

---

## 1.2 Local Choice vs Global Optimum

A global answer may contain many terms:

```text
term1 + term2 + term3 + ... + termN
```

If we swap only two positions:

```text
i
j
```

then every other contribution stays unchanged.

So the comparison becomes:

```text
old local contribution
vs
new local contribution
```

Example:

```text
whole old answer:
10 + 22 + 7 + 5

whole new answer:
10 + 13 + 7 + 5
```

Only:

```text
22
vs
13
```

matters.

This is why greedy exchange proofs often reduce a large `N`-item problem to only **two items**.

---

## 1.3 Why We Compare Only Two Positions

Suppose positions `i` and `j` currently contribute:

```text
firstOldContribution
+
secondOldContribution
```

After swapping:

```text
firstNewContribution
+
secondNewContribution
```

Everything outside positions `i` and `j` is identical.

So:

```text
wholeOld - wholeNew

=
oldLocal - newLocal
```

We do **not** need to recompute the full objective.

### Example

```text
old:
100 + 22 + 50
=
172

new:
100 + 13 + 50
=
163
```

Difference:

```text
172 - 163
=
22 - 13
=
9
```

Unchanged terms cancel automatically.

---

## 1.4 Inversion

An inversion means a pair appears in the opposite order from the greedy target.

For ascending order:

```text
earlierIndex < laterIndex

but

earlierValue > laterValue
```

Example:

```text
[1, 7, 4, 9]
    ^  ^
    inversion
```

Swap:

```text
[1, 4, 7, 9]
```

For an exchange proof:

```text
if every wrong adjacent pair
can be swapped without worsening the answer

then:

repeat
→ all inversions disappear
→ greedy sorted order
```

In the dot-product problem, the target is **opposite order**, so an increasing pair in the second sequence is the pair we want to eliminate after the first sequence is sorted ascending.

---

## 1.5 Dot Product

For two arrays:

```text
A = [a1, a2, ..., aN]
B = [b1, b2, ..., bN]
```

their dot product is:

```text
A · B

=
a1*b1
+
a2*b2
+
...
+
aN*bN
```

Example:

```text
A = [1, 2, 3]
B = [4, 5, 6]
```

Dot product:

```text
1*4 + 2*5 + 3*6
=
4 + 10 + 18
=
32
```

If `B` may be permuted, changing the order changes which values are multiplied together.

That becomes a greedy pairing problem.

---

## 1.6 Opposite-Order Pairing Intuition

For minimization, compare:

```text
small * small
+
large * large
```

against:

```text
small * large
+
large * small
```

Example:

```text
small values:
1 and 4

other values:
2 and 5
```

Same direction:

```text
1*2 + 4*5
=
2 + 20
=
22
```

Opposite direction:

```text
1*5 + 4*2
=
5 + 8
=
13
```

So:

```text
13 < 22
```

This suggests:

```text
MINIMUM dot product
→ opposite ordering
```

The exchange proof later proves it for arbitrary values, including negative values.

---

## 1.7 Algebra Needed for Exchange Proofs

You only need a few algebra operations.

### Rule 1 — Remove a Subtracted Bracket

```text
X - (Y + Z)
=
X - Y - Z
```

Example:

```text
20 - (3 + 5)
=
20 - 3 - 5
=
12
```

---

### Rule 2 — Rearrange Terms

```text
ab + cd - ad - cb
```

can be regrouped as:

```text
ab - ad + cd - cb
```

because addition can be reordered.

---

### Rule 3 — Factor Common Terms

```text
ab - ad
=
a(b-d)
```

Example:

```text
3*7 - 3*2
=
3*(7-2)
=
15
```

---

### Rule 4 — Factor Again

If:

```text
largeA*(largeB-smallB)
-
smallA*(largeB-smallB)
```

then:

```text
=
(largeA-smallA)
*
(largeB-smallB)
```

This factored form makes the sign obvious.

---

## 1.8 Sign Reasoning

If:

```text
largeA >= smallA
```

then:

```text
largeA - smallA >= 0
```

Likewise:

```text
largeB >= smallB
```

gives:

```text
largeB - smallB >= 0
```

Therefore:

```text
(largeA-smallA)
*
(largeB-smallB)
>= 0
```

Basic sign rules:

```text
positive * positive = positive

negative * negative = positive

positive * negative = negative
```

This is often the final step in an exchange proof.

---

## 1.9 Completion Time

For scheduling, distinguish:

```text
timeNeeded
```

from:

```text
completionTime
```

Suppose:

```text
Question A needs 3 units
Question B needs 1 unit
```

Order:

```text
A → B
```

Timeline:

```text
0---------------3-----4
|       A       |  B  |
                ^     ^
              A ends B ends
```

So:

```text
completionTime(A)
=
3

completionTime(B)
=
3 + 1
=
4
```

If score decays with time:

```text
finalScore
=
initialScore
-
decay * completionTime
```

then a question placed later loses more score.

---

## 1.10 Ratio Comparator + Cross Multiplication

A pairwise proof may produce:

```text
timeA / decayA
<=
timeB / decayB
```

Do not compare with floating point.

Cross multiply:

```text
timeA * decayB
<=
timeB * decayA
```

Example:

```text
Question A:
timeA  = 3
decayA = 1

Question B:
timeB  = 1
decayB = 5
```

Compare:

```text
timeA * decayB
=
3 * 5
=
15

timeB * decayA
=
1 * 1
=
1
```

Since:

```text
15 <= 1
```

is false:

```text
A should NOT come before B
```

Check B before A:

```text
1 * 1
<=
3 * 5

1 <= 15
```

true.

So:

```text
B → A
```

The equivalent view is:

```text
decay / time
```

descending.

---

## 1.11 Custom Sort Comparator

The exchange proof should determine the comparator.

For score-decay scheduling:

```text
first comes before second
iff

first.time * second.decay
<
second.time * first.decay
```

Use a separate comparator function:

```cpp
bool compareQuestions(
    const Question& firstQuestion,
    const Question& secondQuestion
) {
    long long left =
        firstQuestion.timeNeeded * secondQuestion.decay;

    long long right =
        secondQuestion.timeNeeded * firstQuestion.decay;

    return left < right;
}
```

Important:

```text
sort comparator must be strict
```

so use:

```text
<
```

not:

```text
<=
```

For equal ratios, any consistent tie order gives the same pairwise objective. A tie-breaker may be added for deterministic output.

---

## 1.12 Overflow and `long long`

These notes use:

```cpp
long long
```

Example product:

```text
10^9 * 10^9
=
10^18
```

which fits in signed `long long`.

But always check:

```text
maximum product
+
maximum accumulated sum
```

before assuming `long long` is enough.

For comparator multiplication:

```text
time * decay
```

must also fit.

---

## 1.13 Recognition Checklist

When you see a new problem, ask:

```text
1. Can I rearrange / reorder items?

2. Does the objective become a sum
   of local pair contributions?

3. Can I compare two items only?

4. If I swap them,
   do all other terms stay unchanged?

5. Can I write:
      oldContribution - newContribution
   and factor it?

6. Does the sign reveal
   which order is better?

7. If a ratio appears,
   can I cross multiply?

8. Is this a scheduling problem where
   later completion creates more penalty?

9. Can repeated safe swaps
   remove every inversion?
```

---

# 2. Pattern 1 — Minimum Dot Product With a Permutation

## 2.1 What It Asks

Given an array:

```text
A = [a1, a2, ..., aN]
```

create a permutation:

```text
B
```

using the same values.

Objective:

```text
minimize:

A · B

=
sum(
    A[index] * B[index]
)
```

The lecture's key question is:

```text
How should the values be paired
to make the dot product minimum?
```

---

## 2.2 Small Dry Run First

Use:

```text
A
=
[-3, 1, 1]
```

One same-ish ordering:

```text
B
=
[-3, 1, 1]
```

Dot product:

```text
(-3)*(-3)
+
1*1
+
1*1

=
9 + 1 + 1

=
11
```

Now pair opposite directions:

```text
A sorted ascending:
[-3, 1, 1]

B sorted descending:
[1, 1, -3]
```

Dot product:

```text
(-3)*1
+
1*1
+
1*(-3)

=
-3 + 1 - 3

=
-5
```

So opposite pairing is much smaller.

---

## 2.3 Core Observation

After sorting one sequence ascending:

```text
small values -------------------- large values
```

to minimize the sum of products, we want the second sequence to move in the opposite direction:

```text
large values -------------------- small values
```

ASCII:

```text
A ascending:
small ---------------------------- large
  |                                  |
  |                                  |
  v                                  v
large ---------------------------- small
B descending
```

Intuition:

```text
small / negative values
should receive large partners

large values
should receive small partners
```

But intuition is not enough.

We prove it using two positions.

---

## 2.4 Greedy Choice

Suppose:

```text
smallA <= largeA
```

and two available values satisfy:

```text
smallB <= largeB
```

For minimum dot product, greedy wants:

```text
smallA * largeB
+
largeA * smallB
```

rather than:

```text
smallA * smallB
+
largeA * largeB
```

That is:

```text
OPPOSITE pairing
```

---

## 2.5 Exchange Proof — Compact + Inline Example

Let:

```text
sameOrder
=
smallA*smallB
+
largeA*largeB

crossOrder
=
smallA*largeB
+
largeA*smallB
```

We want to prove:

```text
crossOrder <= sameOrder
```

Compare:

| General proof | Inline example: `smallA=-3`, `largeA=1`, `smallB=-3`, `largeB=1` |
|---|---|
| `sameOrder - crossOrder` | `10 - (-6) = 16` |
| `= smallA*smallB + largeA*largeB - smallA*largeB - largeA*smallB` | `= 9 + 1 - (-3) - (-3)` |
| `= largeA*largeB - largeA*smallB - smallA*largeB + smallA*smallB` | `= 1 - (-3) - (-3) + 9` |
| `= largeA*(largeB-smallB) - smallA*(largeB-smallB)` | `= 1*(1-(-3)) - (-3)*(1-(-3))` |
| `= (largeA-smallA)*(largeB-smallB)` | `= (1-(-3))*(1-(-3))` |
| `>= 0` | `= 4*4 = 16 >= 0` |

Why is the final product non-negative?

```text
largeA - smallA >= 0
```

and:

```text
largeB - smallB >= 0
```

Therefore:

```text
sameOrder - crossOrder >= 0
```

So:

```text
sameOrder >= crossOrder
```

Hence:

```text
cross / opposite pairing
is never worse for minimization
```

### Same Proof in the Lecture's `i, j` Form

If:

```text
a_i <= a_j
```

then for minimum dot product we want:

```text
b_i >= b_j
```

because:

```text
a_i*b_i + a_j*b_j
<=
a_i*b_j + a_j*b_i
```

Move everything to one side:

```text
a_i*b_i
-
a_i*b_j
+
a_j*b_j
-
a_j*b_i
<=
0
```

Factor:

```text
a_i*(b_i-b_j)
-
a_j*(b_i-b_j)
<=
0
```

Again:

```text
(a_i-a_j)
*
(b_i-b_j)
<=
0
```

Since:

```text
a_i-a_j <= 0
```

we need:

```text
b_i-b_j >= 0
```

therefore:

```text
b_i >= b_j
```

Meaning:

```text
A ascending
→
B descending
```

---

## 2.6 Why This Proves the Whole Array

Sort `A` ascending.

Suppose `B` contains a wrong pair:

```text
i < j
```

but:

```text
B[i] < B[j]
```

Then both `A` and this pair in `B` move in the same direction:

```text
A[i] <= A[j]
B[i] <  B[j]
```

The exchange proof says swapping `B[i]` and `B[j]` cannot increase the minimum objective.

Visual:

```text
before:

A:   smallA ---------------- largeA
       |                        |
       v                        v
B:   smallB ---------------- largeB


swap B pair


after:

A:   smallA ---------------- largeA
       |                        |
       v                        v
B:   largeB ---------------- smallB
```

After the swap:

```text
dot product
is same or smaller
```

Repeat for every same-direction pair.

Eventually:

```text
A
=
ascending

B
=
descending
```

So the opposite-order arrangement is globally optimal.

---

## 2.7 Detailed Dry Run

Input:

```text
A
=
[4, -2, 1, 3]
```

Copy:

```text
B
=
[4, -2, 1, 3]
```

Sort `A` ascending:

```text
A
=
[-2, 1, 3, 4]
```

Sort `B` descending:

```text
B
=
[4, 3, 1, -2]
```

Pair:

```text
A:  -2    1    3    4
      |    |    |    |
B:   4    3    1   -2
```

Products:

```text
-2 * 4
=
-8

1 * 3
=
3

3 * 1
=
3

4 * -2
=
-8
```

Total:

```text
-8 + 3 + 3 - 8
=
-10
```

Notice the structure:

```text
smallest A
pairs with
largest B

largest A
pairs with
smallest B
```

---

## 2.8 Algorithm

```text
1. Copy the array.

2. Sort first copy ascending.

3. Sort second copy descending.

4. Multiply corresponding values.

5. Add all products.
```

Pseudocode:

```text
ascendingValues = sorted(values)
descendingValues = reverse(sorted(values))

minimumDotProduct = 0

for index from 0 to N-1:
    minimumDotProduct +=
        ascendingValues[index]
        * descendingValues[index]
```

---

## 2.9 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfValues;
    cin >> numberOfValues;

    vector<long long> ascendingValues(numberOfValues);

    for (long long& value : ascendingValues)
        cin >> value;

    vector<long long> descendingValues = ascendingValues;

    sort(ascendingValues.begin(), ascendingValues.end());
    sort(descendingValues.rbegin(), descendingValues.rend());

    long long minimumDotProduct = 0;

    for (int index = 0; index < numberOfValues; ++index)
        minimumDotProduct +=
            ascendingValues[index] * descendingValues[index];

    cout << minimumDotProduct << '\n';

    return 0;
}
```

---

## 2.10 Complexity

Sorting both copies:

```text
O(N log N)
```

Dot-product scan:

```text
O(N)
```

Overall:

```text
O(N log N)
```

Space:

```text
O(N)
```

for the second copy.

---

## 2.11 Recognition Model

When you see:

```text
rearrange / permute values
+
objective is sum of pair products
```

immediately test a two-item exchange.

For minimization:

```text
sameOrder - crossOrder
=
(largeA-smallA)
*
(largeB-smallB)
>= 0
```

Therefore:

```text
MIN dot product
→ opposite order
```

For maximum, the same proof tells you the reverse:

```text
MAX dot product
→ same order
```

Mental chain:

```text
pair products
→ compare 2 positions
→ subtract
→ factor
→ inspect sign
→ derive sorting order
```

Do not memorize only:

```text
one ascending + one descending
```

Remember the factorization that proves it.

---

# 3. Pattern 2 — Score / Decay / Time Scheduling

## 3.1 What It Asks

There are `N` questions / tasks.

Each question has:

```text
initialScore
decayPerTime
timeNeeded
```

If a question finishes at:

```text
completionTime
```

its final score is:

```text
finalScore
=
initialScore
-
decayPerTime * completionTime
```

All questions must be ordered.

Goal:

```text
maximize total final score
```

The greedy problem is:

```text
Which question should come earlier?
```

---

## 3.2 Small Dry Run First

Use the class example:

```text
Question A:
score = 5
decay = 1
time  = 3

Question B:
score = 10
decay = 5
time  = 1
```

### Order A → B

Timeline:

```text
0---------------3---4
|       A       | B |
```

A completes at:

```text
3
```

A score:

```text
5 - 1*3
=
2
```

B completes at:

```text
4
```

B score:

```text
10 - 5*4
=
-10
```

Total:

```text
2 + (-10)
=
-8
```

### Order B → A

Timeline:

```text
0---1---------------4
| B |       A       |
```

B completes at:

```text
1
```

B score:

```text
10 - 5*1
=
5
```

A completes at:

```text
4
```

A score:

```text
5 - 1*4
=
1
```

Total:

```text
5 + 1
=
6
```

Therefore:

```text
B → A
```

is much better.

---

## 3.3 Core Observation

The loss caused by waiting is:

```text
decayPerTime
*
completionTime
```

A question with:

```text
large decay
```

is expensive to delay.

A question with:

```text
large time
```

also delays everything after it.

So we cannot sort by only:

```text
decay
```

or only:

```text
time
```

We need to compare **two questions** and derive the correct combined rule.

---

## 3.4 Compare Only Two Adjacent Questions

Suppose all questions before the pair already consumed:

```text
previousTime
```

Now compare two adjacent questions:

```text
A
B
```

against:

```text
B
A
```

Everything before them is unchanged.

Everything after them begins after the same combined duration:

```text
timeA + timeB
```

in either order.

Therefore only the scores of:

```text
A
and
B
```

need to be compared.

ASCII:

```text
same prefix            compare pair             same suffix

[ previous ]      [ A ][ B ]              [ remaining ]
                     ↕ swap
[ previous ]      [ B ][ A ]              [ remaining ]
```

This is exactly an exchange argument.

---

## 3.5 Exchange Proof — Every Algebra Step + Inline Example

Use shorter proof names:

```text
scoreA, decayA, timeA
scoreB, decayB, timeB
prevTime
```

We want to know when:

```text
A → B
```

is at least as good as:

```text
B → A
```

---

### Step 1 — Write Score for A → B

A finishes at:

```text
prevTime + timeA
```

So:

```text
A score
=
scoreA
-
decayA*(prevTime + timeA)
```

B finishes at:

```text
prevTime + timeA + timeB
```

So:

```text
B score
=
scoreB
-
decayB*(prevTime + timeA + timeB)
```

Together:

```text
AB

=
scoreA
-
decayA*(prevTime + timeA)

+
scoreB
-
decayB*(prevTime + timeA + timeB)
```

---

### Step 2 — Write Score for B → A

B finishes at:

```text
prevTime + timeB
```

A finishes at:

```text
prevTime + timeB + timeA
```

Therefore:

```text
BA

=
scoreB
-
decayB*(prevTime + timeB)

+
scoreA
-
decayA*(prevTime + timeB + timeA)
```

---

### Step 3 — Require A → B to Be Better

```text
AB >= BA
```

Substitute:

```text
scoreA
-
decayA*(prevTime + timeA)

+
scoreB
-
decayB*(prevTime + timeA + timeB)

>=

scoreB
-
decayB*(prevTime + timeB)

+
scoreA
-
decayA*(prevTime + timeB + timeA)
```

---

### Step 4 — Expand

Left:

```text
scoreA
- decayA*prevTime
- decayA*timeA

+ scoreB
- decayB*prevTime
- decayB*timeA
- decayB*timeB
```

Right:

```text
scoreB
- decayB*prevTime
- decayB*timeB

+ scoreA
- decayA*prevTime
- decayA*timeB
- decayA*timeA
```

---

### Step 5 — Cancel Equal Terms

These appear on both sides:

```text
scoreA
scoreB

-decayA*prevTime
-decayB*prevTime

-decayA*timeA
-decayB*timeB
```

Only two cross-delay terms remain:

```text
-decayB*timeA

>=

-decayA*timeB
```

Multiply both sides by `-1`.

The inequality flips:

```text
decayB*timeA

<=

decayA*timeB
```

Reorder:

```text
timeA*decayB

<=

timeB*decayA
```

This is the comparator.

---

### Proof + Inline Numbers

Use the class values:

```text
A:
scoreA = 5
decayA = 1
timeA  = 3

B:
scoreB = 10
decayB = 5
timeB  = 1
```

| General proof | Actual values |
|---|---|
| `AB >= BA` required | Is `-8 >= 6`? No |
| `-decayB*timeA >= -decayA*timeB` | `-5*3 >= -1*1` |
| `-15 >= -1` | false |
| multiply by `-1` | inequality flips |
| `timeA*decayB <= timeB*decayA` | `3*5 <= 1*1` |
| `15 <= 1` | false |

So:

```text
A should not come before B
```

Now test B before A:

```text
timeB*decayA
<=
timeA*decayB

1*1
<=
3*5

1 <= 15
```

true.

Therefore:

```text
B → A
```

which matches the direct dry run:

```text
6 > -8
```

---

### Direct Difference Form

The same proof can be compressed to:

```text
AB - BA

=
decayA*timeB
-
decayB*timeA
```

Example:

```text
=
1*1
-
5*3

=
1 - 15

=
-14
```

And indeed:

```text
AB - BA
=
-8 - 6
=
-14
```

The algebra and the actual scores match exactly.

---

## 3.6 Derive the Sorting Rule

We proved:

```text
A before B
```

when:

```text
timeA*decayB
<=
timeB*decayA
```

If decays are positive, divide by:

```text
decayA * decayB
```

to get:

```text
timeA / decayA
<=
timeB / decayB
```

Therefore sort:

```text
time / decay
```

ascending.

Equivalent form:

```text
decay / time
```

descending.

### Which Form Should We Use in Code?

Do **not** compute floating-point ratios.

Use:

```text
timeA * decayB
<
timeB * decayA
```

This is exactly the cross-product comparator from the lecture.

---

## 3.7 Why Initial Score Does Not Affect the Order

Notice what disappeared during cancellation:

```text
scoreA
scoreB
```

So the pairwise order does **not** depend on initial score.

Example:

```text
Question A score:
5

Question B score:
10
```

These affect the final total value, but not the greedy sorting rule.

Why?

Changing the order does not change either question's base score.

It only changes:

```text
how long each question waits
```

Therefore the ordering decision is controlled by:

```text
decay
and
time
```

not:

```text
initialScore
```

This is an important contest observation.

---

## 3.8 Why the Pairwise Proof Works Anywhere in the Schedule

The two questions may appear after some already elapsed:

```text
prevTime
```

But during the algebra:

```text
-decayA*prevTime
```

and:

```text
-decayB*prevTime
```

appear in **both orders**.

So they cancel.

That means the preferred order between A and B is independent of where the pair appears.

Therefore:

```text
if an adjacent pair violates the comparator
→ swap it
→ score does not decrease
```

Repeat:

```text
remove all inversions
→ array becomes sorted by comparator
→ optimal schedule exists in this order
```

This completes the exchange proof.

---

## 3.9 Detailed Dry Run

Take three questions:

```text
Question A:
score = 5
decay = 1
time  = 3

Question B:
score = 10
decay = 5
time  = 1

Question C:
score = 8
decay = 2
time  = 2
```

Compute cross-order intuition:

```text
time / decay

A:
3 / 1
=
3

B:
1 / 5
=
0.2

C:
2 / 2
=
1
```

Ascending:

```text
B → C → A
```

Use cross multiplication in actual sorting.

---

### Step 1 — Question B

Elapsed time:

```text
0 + 1
=
1
```

Score:

```text
10 - 5*1
=
5
```

Total:

```text
5
```

---

### Step 2 — Question C

Elapsed time:

```text
1 + 2
=
3
```

Score:

```text
8 - 2*3
=
2
```

Total:

```text
5 + 2
=
7
```

---

### Step 3 — Question A

Elapsed time:

```text
3 + 3
=
6
```

Score:

```text
5 - 1*6
=
-1
```

Total:

```text
7 - 1
=
6
```

Final:

```text
B → C → A

total score
=
6
```

ASCII:

```text
time:

0---1-------3------------6
| B |   C   |      A     |
    ^       ^            ^
    1       3            6

B score:
10 - 5*1 = 5

C score:
8 - 2*3 = 2

A score:
5 - 1*6 = -1

total:
5 + 2 - 1 = 6
```

---

## 3.10 Algorithm

```text
1. Read every question:
   score
   decay
   time

2. Sort using pairwise rule:

   first before second if:

   first.time * second.decay
   <
   second.time * first.decay

3. elapsedTime = 0
   totalScore = 0

4. For each question in sorted order:

   elapsedTime += question.time

   finalScore =
       question.score
       -
       question.decay * elapsedTime

   totalScore += finalScore

5. Output totalScore.
```

---

## 3.11 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Question {
    long long initialScore;
    long long decay;
    long long timeNeeded;
    int index;
};

bool compareQuestions(
    const Question& firstQuestion,
    const Question& secondQuestion
) {
    long long left =
        firstQuestion.timeNeeded * secondQuestion.decay;

    long long right =
        secondQuestion.timeNeeded * firstQuestion.decay;

    if (left != right)
        return left < right;

    return firstQuestion.index < secondQuestion.index;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfQuestions;
    cin >> numberOfQuestions;

    vector<Question> questions(numberOfQuestions);

    for (int index = 0; index < numberOfQuestions; ++index) {
        cin >> questions[index].initialScore
            >> questions[index].decay
            >> questions[index].timeNeeded;

        questions[index].index = index;
    }

    sort(questions.begin(), questions.end(), compareQuestions);

    long long elapsedTime = 0;
    long long maximumTotalScore = 0;

    for (const Question& question : questions) {
        elapsedTime += question.timeNeeded;

        maximumTotalScore +=
            question.initialScore
            -
            question.decay * elapsedTime;
    }

    cout << maximumTotalScore << '\n';

    return 0;
}
```

### Comparator Only — Contest Recall

```cpp
bool compareQuestions(const Question& first, const Question& second) {
    return first.timeNeeded * second.decay
         < second.timeNeeded * first.decay;
}
```

Use the tie-breaker version in the full code when deterministic ordering is useful.

---

## 3.12 Complexity

Sorting:

```text
O(N log N)
```

Final score scan:

```text
O(N)
```

Overall:

```text
O(N log N)
```

Space:

```text
O(N)
```

for storing the questions.

---

## 3.13 Common Mistakes

| Wrong idea | Why it fails |
|---|---|
| Sort by highest initial score | initial score cancels from the exchange proof |
| Sort only by highest decay | ignores how long the question itself blocks later work |
| Sort only by shortest time | ignores how expensive delaying the question is |
| Compute `time / decay` using `double` | unnecessary precision risk |
| Use `<=` directly in `std::sort` comparator | comparator must be strict |
| Forget completion time is cumulative | later question finishes after all previous times |
| Derive ratio but memorize it without proof | easy to reverse the comparator direction |

---

## 3.14 Recognition Model

When you see:

```text
reorder jobs/questions
+
each item has duration
+
waiting/completion time causes penalty
```

think:

```text
exchange two adjacent items
```

Write:

```text
score(A→B)
```

and:

```text
score(B→A)
```

Subtract.

After cancellation, the pairwise inequality gives the comparator.

For this lecture:

```text
A before B

iff

timeA*decayB
<=
timeB*decayA
```

So:

```text
sort time/decay ascending
```

or equivalently:

```text
sort decay/time descending
```

Mental chain:

```text
scheduling
→ completion-time penalty
→ compare two jobs
→ cancel common terms
→ cross product appears
→ custom comparator
```

Do not memorize only:

```text
T/D ascending
```

Remember how:

```text
-decayB*timeA
>=
-decayA*timeB
```

becomes:

```text
timeA*decayB
<=
timeB*decayA
```

That derivation prevents comparator mistakes.

---

# Final Mental Model

```text
                     GREEDY EXCHANGE
                           |
                           v
                 compare only two items
                           |
                           v
                 swap their local order
                           |
                           v
              everything else stays same
                           |
                           v
                 subtract the two cases
                           |
                  +--------+--------+
                  |                 |
                  v                 v
             factor signs      ratio appears
                  |                 |
                  v                 v
          opposite pairing      cross multiply
                  |                 |
                  v                 v
        MIN dot product      scheduling comparator
```

The reusable contest method is:

```text
Do not guess the sort order.

Take two items.
Write both possible orders.
Subtract.
Cancel.
Factor.
Read the sign.

The inequality itself
tells you how to sort.
```
