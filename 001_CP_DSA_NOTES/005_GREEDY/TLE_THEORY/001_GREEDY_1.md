# Greedy Algorithms — Level 3
## Greedy 1 — Claims, Proofs, Sorting Greedy, Coin Change, Balanced Product & Minimum Dot Product

> **Goal:** learn to *make a greedy claim and prove it*, not just memorize “sort and pick.”
>
> **Study flow for every section/problem:**  
> **prerequisites → mathematical notation → concept simplified → what it asks → observation/claim → derivation/proof → step-by-step example → C++ → complexity → recognition model**
>
> **Lecture basis:** *Greedy Algorithms 1 — Priyansh Agarwal*.  
> Supporting explanations and extra examples are added only to make the lecture ideas easier to self-study.
>
> **Math rendering:** display equations use fenced `math` blocks only.

---

# Clickable Table of Contents

- [0. Prerequisites & Mathematical Notation](#0-prerequisites--mathematical-notation)
  - [0.1 Optimization Language](#01-optimization-language)
  - [0.2 Sorted Array Notation](#02-sorted-array-notation)
  - [0.3 Greedy Answer vs Optimal Answer](#03-greedy-answer-vs-optimal-answer)
  - [0.4 Exchange Argument](#04-exchange-argument)
  - [0.5 Floor and Ceiling](#05-floor-and-ceiling)
  - [0.6 Dot Product and Sigma](#06-dot-product-and-sigma)
  - [0.7 Division, Quotient and Remainder](#07-division-quotient-and-remainder)
- [1. What Is a Greedy Strategy?](#1-what-is-a-greedy-strategy)
- [2. How to Prove a Greedy Claim](#2-how-to-prove-a-greedy-claim)
- [3. Example 1 — Maximum Sum of K Elements](#3-example-1--maximum-sum-of-k-elements)
- [4. Example 2 — Maximum Difference Between Two Elements](#4-example-2--maximum-difference-between-two-elements)
- [5. Example 3 — Minimum Difference Between Two Elements](#5-example-3--minimum-difference-between-two-elements)
- [6. Example 4 — Maximize Sum of i × a[i]](#6-example-4--maximize-sum-of-i--ai)
- [7. Coin Change — Greedy With Divisible Denominations](#7-coin-change--greedy-with-divisible-denominations)
- [8. Why Coin Greedy Can Fail](#8-why-coin-greedy-can-fail)
- [9. Maximum Product With Fixed Sum](#9-maximum-product-with-fixed-sum)
- [10. Minimum Dot Product](#10-minimum-dot-product)
- [11. Core Proof Patterns Learned](#11-core-proof-patterns-learned)
- [12. Greedy Recognition Checklist](#12-greedy-recognition-checklist)
- [13. Compact Revision Card](#13-compact-revision-card)

---

# 0. Prerequisites & Mathematical Notation

Before the lecture problems, make these symbols and proof ideas automatic.

---

## 0.1 Optimization Language

Greedy problems usually ask us to optimize something.

### Maximum

```math
\max(\text{value})
```

means:

```text
make the value as large as possible
```

Example:

```text
maximize sum of selected K elements
```

---

### Minimum

```math
\min(\text{value})
```

means:

```text
make the value as small as possible
```

Example:

```text
minimum difference between any two elements
```

---

### Constraint

A constraint tells us which solutions are legal.

Example:

```text
choose exactly K elements
```

The objective may be:

```text
maximize their sum
```

but we cannot choose:

```text
K+1 elements
```

because that violates the constraint.

---

### Concept Simplified

Always separate:

```text
WHAT MUST BE VALID?
        ↓
constraint

WHAT DO I WANT BEST?
        ↓
objective
```

---

## 0.2 Sorted Array Notation

If an array is sorted ascending:

```math
a_1\le a_2\le a_3\le\cdots\le a_n
```

then:

```text
a1 = smallest
an = largest
```

For indices:

```math
i<j
```

we know:

```math
a_i\le a_j
```

This tiny fact powers several proofs in the lecture.

---

### Example

Array:

```text
[11, 6, 7, 2, 0, 2, 9, 10]
```

Sorted:

```text
[0, 2, 2, 6, 7, 9, 10, 11]
```

Therefore:

```text
smallest = 0
largest  = 11
```

and adjacent differences are:

```text
2, 0, 4, 1, 2, 1, 1
```

---

## 0.3 Greedy Answer vs Optimal Answer

The lecture annotations repeatedly compare:

```text
GA = Greedy Answer
OA = some other / optimal answer
```

We will use:

```math
GA
```

for the value produced by our greedy claim.

And:

```math
OA
```

for the value of another feasible solution, especially an optimal one.

For a **maximization** problem, we want to prove:

```math
GA\ge OA
```

For a **minimization** problem, we want:

```math
GA\le OA
```

---

### Example

Suppose greedy sum is:

```text
GA = 25
```

and after exchanging one choice in another solution we get:

```text
OA = 22
```

Then:

```text
GA >= OA
```

so greedy is no worse for a maximization problem.

---

## 0.4 Exchange Argument

This is the central proof idea used throughout the lecture.

### Concept Simplified

Assume another solution makes a different choice.

Replace one of its choices with the greedy choice.

Then prove:

```text
the new solution stays valid
AND
the objective does not get worse
```

---

### Generic Maximization Proof

Suppose greedy chooses:

```text
q
```

while another solution chooses:

```text
p
```

and:

```math
q\ge p
```

If we replace `p` by `q`, the change is:

```math
q-p\ge0
```

So the total value cannot decrease.

---

### Generic Minimization Proof

If replacing another choice by greedy changes cost by:

```math
\Delta\le0
```

then greedy does not increase the cost.

---

### Recognition

When you see:

```text
sort
then choose the largest/smallest
```

ask:

```text
Can I swap a “wrong” chosen element
with the greedy element
without making the answer worse?
```

---

## 0.5 Floor and Ceiling

Used later in the Maximum Product problem.

### Floor

```math
\left\lfloor x\right\rfloor
```

means:

```text
greatest integer <= x
```

Examples:

```text
floor(2.5) = 2
floor(3.0) = 3
```

---

### Ceiling

```math
\left\lceil x\right\rceil
```

means:

```text
smallest integer >= x
```

Examples:

```text
ceil(2.5) = 3
ceil(3.0) = 3
```

---

### For N / 2

If `N` is even:

```text
N = 8

floor(N/2) = 4
ceil(N/2)  = 4
```

If `N` is odd:

```text
N = 5

floor(N/2) = 2
ceil(N/2)  = 3
```

So these two numbers are the two integers closest to splitting `N` equally.

---

## 0.6 Dot Product and Sigma

Given vectors:

```text
A = [a1,a2,...,an]
B = [b1,b2,...,bn]
```

their dot product is:

```math
A\cdot B
=
\sum_{i=1}^{n}a_ib_i
```

The sigma:

```math
\sum_{i=1}^{n}
```

means:

```text
add the expression for i = 1,2,...,n
```

So:

```math
\sum_{i=1}^{3}a_ib_i
=
a_1b_1+a_2b_2+a_3b_3
```

---

### Example

```text
A = [2,3]
B = [5,7]
```

Dot product:

```text
2×5 + 3×7
= 10 + 21
= 31
```

---

## 0.7 Division, Quotient and Remainder

Used heavily in greedy coin change.

For positive integers:

```math
X=qD+r
```

where:

```text
q = X / D
r = X % D
0 <= r < D
```

Example:

```text
X = 256
D = 100
```

Then:

```text
q = 2
r = 56
```

Meaning:

```text
use 100 two times
remaining = 56
```

This is exactly what greedy coin change does.

---

# 1. What Is a Greedy Strategy?

The lecture describes greedy as a strategy that assumes the best answer can be found by exploring only **some carefully chosen possibilities**, rather than trying every possible solution.

It consists of two parts:

```text
1. Make a greedy claim.
2. Prove the claim.
```

---

## 1.1 Concept Simplified

Brute force:

```text
Try everything
        ↓
pick best answer
```

Greedy:

```text
Observe structure
        ↓
make a claim:
"the optimal solution can be chosen this way"
        ↓
ignore many impossible/unnecessary choices
        ↓
prove claim
```

---

## 1.2 Why the Proof Matters

A rule such as:

```text
"always choose the biggest"
```

is not automatically greedy-correct.

It is only a **guess** until we prove:

```text
choosing the biggest now
cannot make the final answer worse
```

The lecture later shows a coin system where “take largest coin first” fails.

So:

```text
greedy = claim + proof
```

not:

```text
greedy = sort + hope
```

---

## 1.3 Example

Problem:

```text
Choose K elements with maximum sum.
```

Claim:

```text
Choose the K largest elements.
```

Why is that believable?

If our selected set contains a smaller value `p` while an unselected larger value `q` exists:

```text
q > p
```

swap:

```text
p → q
```

The sum increases.

So an optimal answer can always be transformed toward the K largest values.

---

## 1.4 Recognition Model

```text
Optimization problem
       |
       v
Trying all choices is expensive
       |
       v
Can I identify a choice
that an optimal answer can safely contain?
       |
       v
Make claim
       |
       v
Try to prove or disprove it
```

---

# 2. How to Prove a Greedy Claim

The lecture highlights three practical ways to build confidence in a greedy strategy.

---

## 2.1 Formal Mathematical Proof

This is strongest.

Examples from this lecture use:

```text
GA - OA >= 0
```

for maximization, or:

```text
GA - OA <= 0
```

for minimization.

---

### Example

Greedy picks `q` instead of `p`.

Suppose:

```math
q\ge p
```

Then:

```math
GA-OA=q-p\ge0
```

Therefore greedy is not worse.

---

## 2.2 Intuitive Proof / Common Sense

Sometimes the structure is obvious enough to first build intuition.

Example:

```text
maximum difference in a sorted array
```

The largest gap should use:

```text
smallest value
and
largest value
```

That intuition guides the formal proof.

---

## 2.3 Try Hard to Disprove the Claim

The lecture also recommends trying many cases and searching for a counterexample.

This is especially useful before investing time in a proof.

---

### Example — Coin Change

Claim:

```text
always take the largest possible coin
```

Works for:

```text
[1,5,10,50,100]
```

But test:

```text
[1,8,10]
X = 16
```

Greedy:

```text
10 + 1 + 1 + 1 + 1 + 1 + 1
```

7 coins.

Better:

```text
8 + 8
```

2 coins.

Counterexample found.

Claim is false for arbitrary denominations.

---

## 2.4 Practical Contest Workflow

```text
Make claim
   |
   +----------------------+
   |                      |
small tests           proof attempt
   |                      |
counterexample?       exchange/algebra?
   |
 YES → reject claim
 NO  → confidence rises,
       but still prove when possible
```

---

# 3. Example 1 — Maximum Sum of K Elements

## 3.1 What It Asks

Given an array of `N` numbers:

```text
choose exactly K elements
```

such that their sum is maximum.

---

## 3.2 Mathematical Model

Choose a subset `S` satisfying:

```math
|S|=K
```

and maximize:

```math
\sum_{i\in S}a_i
```

Notation:

```math
|S|
```

means:

```text
number of elements in set S
```

---

## 3.3 Concept Simplified

Every selected position contributes only its value.

There is no interaction between elements.

So if we selected a smaller value while leaving a larger value outside:

```text
we can improve the answer by swapping them.
```

This screams:

```text
take the K largest values
```

---

## 3.4 Greedy Claim

Sort ascending:

```math
a_1\le a_2\le\cdots\le a_n
```

Greedy selects:

```text
a[n-K+1], ..., a[n]
```

the last `K` elements.

---

## 3.5 Exchange Proof

Suppose another solution `OA` selects some element:

```math
a_p
```

but does not select a larger greedy element:

```math
a_q
```

where:

```math
p<q
```

Because array is sorted:

```math
a_p\le a_q
```

Swap:

```text
remove a_p
add    a_q
```

Difference:

```math
GA-OA
=
a_q-a_p
```

Since:

```math
a_q-a_p\ge0
```

the replacement never decreases the sum.

Repeat this exchange until the selected set is exactly the `K` largest elements.

Therefore the greedy choice is optimal.

---

## 3.6 Step-by-Step Example

Array:

```text
[11,6,7,2,0,2,9,10]
```

Let:

```text
K = 2
```

Sort:

```text
[0,2,2,6,7,9,10,11]
```

Greedy picks:

```text
11 and 10
```

Sum:

```text
21
```

Suppose another answer picks:

```text
11 and 7
```

Sum:

```text
18
```

Swap:

```text
7 → 10
```

Improvement:

```text
10 - 7 = 3
```

New sum:

```text
21
```

---

## 3.7 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

long long maxKSum(vector<long long> a, int k) {
    sort(a.begin(), a.end());

    long long ans = 0;

    for (int i = (int)a.size() - k;
         i < (int)a.size();
         ++i) {
        ans += a[i];
    }

    return ans;
}
```

Alternative:

```cpp
sort(a.rbegin(), a.rend());

long long ans = 0;

for (int i = 0; i < k; ++i)
    ans += a[i];
```

---

## 3.8 Complexity

Sorting:

```text
O(N log N)
```

Summing `K` values:

```text
O(K)
```

Total:

```text
O(N log N)
```

---

## 3.9 Recognition Model

```text
Choose exactly K items
+
objective is sum of independent values
+
no extra interaction/constraint
        |
        v
take K largest for maximum
take K smallest for minimum
```

---

# 4. Example 2 — Maximum Difference Between Two Elements

## 4.1 What It Asks

Given an array, choose two elements to maximize their difference.

Conceptually:

```math
\max(a_j-a_i)
```

where we are free to choose the larger value as the first term.

---

## 4.2 Concept Simplified

To make:

```text
large - small
```

as large as possible:

```text
make first number as large as possible
make second number as small as possible
```

So choose:

```text
maximum element - minimum element
```

---

## 4.3 Greedy Claim

After sorting:

```math
a_1\le a_2\le\cdots\le a_n
```

answer:

```math
a_n-a_1
```

---

## 4.4 Proof by Bounds

Take any two sorted-array values:

```math
a_p\le a_q
```

Because:

```math
a_1\le a_p
```

we have:

```math
a_q-a_1\ge a_q-a_p
```

And because:

```math
a_q\le a_n
```

we have:

```math
a_n-a_1\ge a_q-a_1
```

Therefore:

```math
a_n-a_1\ge a_q-a_p
```

for every possible pair.

So maximum difference is:

```math
a_n-a_1
```

---

## 4.5 Step-by-Step Example

Array:

```text
[8,4,15,16]
```

Sorted:

```text
[4,8,15,16]
```

Possible notable differences:

```text
8 - 4  = 4
15 - 4 = 11
16 - 8 = 8
16 - 4 = 12
```

Maximum:

```text
12
```

using:

```text
16 - 4
```

---

## 4.6 C++

No sorting is actually necessary if we only need min and max.

```cpp
long long maximumDifference(
    const vector<long long>& a
) {
    auto [mnIt, mxIt] =
        minmax_element(a.begin(), a.end());

    return *mxIt - *mnIt;
}
```

---

## 4.7 Complexity

One scan:

```text
O(N)
```

Extra space:

```text
O(1)
```

---

## 4.8 Recognition Model

```text
maximize unrestricted difference
        |
        v
largest - smallest
```

> If the problem requires `i < j` in the **original array**, this simple rule may no longer be enough; then order matters.

---

# 5. Example 3 — Minimum Difference Between Two Elements

## 5.1 What It Asks

Choose two different array elements with minimum absolute difference.

```math
\min_{i\ne j}|a_i-a_j|
```

---

## 5.2 Concept Simplified

In unsorted order, close values may be far apart in the array.

After sorting, values that are numerically closest become neighbors.

So we only need to check:

```text
adjacent pairs
```

---

## 5.3 Greedy Claim

Sort:

```math
a_1\le a_2\le\cdots\le a_n
```

Then:

```math
\text{answer}
=
\min_{2\le i\le n}(a_i-a_{i-1})
```

---

## 5.4 Why Only Adjacent Pairs?

Take any non-adjacent pair:

```math
a_j,\ a_i
```

with:

```text
j < i-1
```

Because sorted order gives:

```math
a_j\le a_{i-1}\le a_i
```

then:

```math
a_i-a_j
\ge
a_i-a_{i-1}
```

So the non-adjacent pair cannot be better than the adjacent pair ending at `a_i`.

Thus some optimal pair must be adjacent after sorting.

---

## 5.5 Step-by-Step Example

Array:

```text
[8,9,15,16]
```

Already sorted.

Adjacent differences:

```text
9 - 8  = 1
15 - 9 = 6
16 - 15 = 1
```

Minimum:

```text
1
```

Checking non-adjacent pair:

```text
15 - 8 = 7
```

is obviously worse than:

```text
9 - 8 = 1
```

---

## 5.6 Supporting Example

Array:

```text
[20, 3, 17, 9, 10]
```

Sort:

```text
[3,9,10,17,20]
```

Adjacent gaps:

```text
6,1,7,3
```

Answer:

```text
1
```

from:

```text
9 and 10
```

---

## 5.7 C++

```cpp
long long minimumDifference(
    vector<long long> a
) {
    sort(a.begin(), a.end());

    long long ans = LLONG_MAX;

    for (int i = 1; i < (int)a.size(); ++i) {
        ans = min(ans, a[i] - a[i - 1]);
    }

    return ans;
}
```

---

## 5.8 Complexity

Sorting:

```text
O(N log N)
```

Scan:

```text
O(N)
```

Total:

```text
O(N log N)
```

---

## 5.9 Recognition Model

```text
minimum absolute difference
between any two values
        |
        v
sort
        |
        v
only adjacent values can be optimal
```

---

# 6. Example 4 — Maximize Sum of i × a[i]

## 6.1 What It Asks

Rearrange the array to maximize:

```math
\sum_{i=1}^{n}i\cdot a_i
```

The positions have increasing weights:

```text
1,2,3,...,n
```

We can permute the values.

---

## 6.2 Mathematical Interpretation

We are pairing:

```text
array values
```

with:

```text
position weights
```

Weights are already sorted ascending:

```math
1<2<3<\cdots<n
```

Question:

```text
Which values should receive the largest weights?
```

Intuition:

```text
large values should receive large weights.
```

---

## 6.3 Greedy Claim

Sort values ascending:

```math
a_1\le a_2\le\cdots\le a_n
```

Then pair:

```text
smallest value × smallest index
...
largest value × largest index
```

---

## 6.4 Exchange Proof

Suppose positions:

```text
i < j
```

but another arrangement places:

```text
larger value a_j at smaller index i
smaller value a_i at larger index j
```

where:

```math
a_i\le a_j
```

Greedy contribution:

```math
G
=
a_i i+a_j j
```

Swapped contribution:

```math
O
=
a_j i+a_i j
```

Difference:

```math
G-O
=
a_i i+a_j j-a_j i-a_i j
```

Group terms:

```math
G-O
=
a_i(i-j)+a_j(j-i)
```

Equivalent:

```math
G-O
=
(a_i-a_j)(i-j)
```

Now:

```math
a_i-a_j\le0
```

and:

```math
i-j<0
```

Product of two non-positive values:

```math
(a_i-a_j)(i-j)\ge0
```

Therefore:

```math
G\ge O
```

So swapping an inversion toward sorted order never decreases the objective.

Repeatedly remove inversions.

Final optimal arrangement is ascending.

---

## 6.5 Step-by-Step Example

Array:

```text
[10,6,7]
```

Ascending:

```text
[6,7,10]
```

Greedy weighted sum:

```text
1×6 + 2×7 + 3×10
= 6 + 14 + 30
= 50
```

Try:

```text
[10,7,6]
```

Sum:

```text
1×10 + 2×7 + 3×6
= 10 + 14 + 18
= 42
```

Greedy is larger.

---

## 6.6 Supporting Example With Negative Values

Values:

```text
[-5,2,10]
```

Ascending:

```text
[-5,2,10]
```

Sum:

```text
1×(-5) + 2×2 + 3×10
= -5 + 4 + 30
= 29
```

If largest value gets smallest weight:

```text
[10,2,-5]
```

Sum:

```text
10 + 4 - 15
= -1
```

The exchange proof still works with negative numbers.

---

## 6.7 C++

```cpp
long long maximumWeightedSum(
    vector<long long> a
) {
    sort(a.begin(), a.end());

    long long ans = 0;

    for (int i = 0; i < (int)a.size(); ++i) {
        ans += 1LL * (i + 1) * a[i];
    }

    return ans;
}
```

---

## 6.8 Complexity

Sorting:

```text
O(N log N)
```

Scan:

```text
O(N)
```

---

## 6.9 Recognition Model

```text
rearrange values
to maximize sum(value × weight)
        |
        v
weights increase
        |
        v
pair large with large
small with small
```

This is the same rearrangement principle that later appears again in the dot-product problem.

---

# 7. Coin Change — Greedy With Divisible Denominations

## 7.1 What It Asks

Lecture denominations:

```text
[1,5,10,50,100]
```

Unlimited supply.

Given:

```text
X
```

find the minimum number of coins whose sum is `X`.

Examples from the lecture:

```text
X = 125
= 100 + 10 + 10 + 5
```

and:

```text
X = 256
= 100 + 100 + 50 + 5 + 1
```

---

## 7.2 Concept Simplified

If one large coin can replace several smaller coins:

```text
using the large coin is never worse
```

Example:

```text
5 ones
```

can be replaced by:

```text
one 5
```

Coin count changes:

```text
5 coins → 1 coin
```

Better.

Similarly:

```text
2 × 5 → 1 × 10
5 × 10 → 1 × 50
2 × 50 → 1 × 100
```

This is the exchange structure behind the lecture's denomination system.

---

## 7.3 Greedy Claim

Process denominations from largest to smallest.

For each denomination `d`:

```text
use as many d-coins as possible
```

Count:

```text
X / d
```

Remaining amount:

```text
X % d
```

Then continue with the next smaller denomination.

---

## 7.4 Why It Works for This Structure

Sorted denominations:

```math
d_1<d_2<\cdots<d_m
```

The lecture's sufficient structure is that the next denomination can be made exactly from copies of the previous denomination:

```math
d_{i+1}\bmod d_i=0
```

For the lecture coins:

```text
5 % 1   = 0
10 % 5  = 0
50 % 10 = 0
100 % 50 = 0
```

---

### Exchange Argument

Suppose:

```math
d_{i+1}=r\cdot d_i
```

for integer:

```text
r >= 2
```

If a solution uses at least `r` coins of `d_i`:

```text
r copies of d_i
```

have total value:

```math
r\cdot d_i=d_{i+1}
```

Replace them with:

```text
1 coin of d_{i+1}
```

Coin count improves:

```text
r coins → 1 coin
```

Therefore an optimal solution never needs `r` or more copies of `d_i` when a `d_{i+1}` coin can be used instead.

This is exactly what the largest-first greedy representation enforces.

---

## 7.5 Step-by-Step Dry Run — X = 256

Denominations descending:

```text
100,50,10,5,1
```

### 100

```text
256 / 100 = 2
```

Use:

```text
2 × 100
```

Remaining:

```text
56
```

---

### 50

```text
56 / 50 = 1
```

Use:

```text
1 × 50
```

Remaining:

```text
6
```

---

### 10

```text
6 / 10 = 0
```

Use none.

---

### 5

```text
6 / 5 = 1
```

Remaining:

```text
1
```

---

### 1

Use:

```text
1
```

Final:

```text
100 + 100 + 50 + 5 + 1
```

Number of coins:

```text
5
```

---

## 7.6 Step-by-Step Dry Run — X = 152

```text
152 / 100 = 1
remaining = 52

52 / 50 = 1
remaining = 2

2 / 10 = 0
2 / 5  = 0
2 / 1  = 2
```

Coins:

```text
100 + 50 + 1 + 1
```

Count:

```text
4
```

---

## 7.7 C++

For the lecture denomination set:

```cpp
#include <bits/stdc++.h>
using namespace std;

long long minCoinsLecture(long long x) {
    vector<long long> coin = {
        100, 50, 10, 5, 1
    };

    long long count = 0;

    for (long long d : coin) {
        count += x / d;
        x %= d;
    }

    return count;
}
```

---

## 7.8 Generic Divisibility-Chain Version

```cpp
long long greedyCoins(
    long long x,
    vector<long long> coins
) {
    sort(coins.rbegin(), coins.rend());

    long long count = 0;

    for (long long d : coins) {
        count += x / d;
        x %= d;
    }

    return count;
}
```

> The code always runs, but optimality requires a proven property of the denomination system. The lecture gives the divisible-chain structure as the reason it is safe for its example.

---

## 7.9 Complexity

If number of denominations is `M`:

```text
O(M)
```

after denominations are already ordered.

If sorting is required:

```text
O(M log M)
```

---

## 7.10 Recognition Model

```text
minimize number of coins
+
unlimited coins
+
higher denomination exactly replaces
several lower-denomination coins
        |
        v
largest denomination first
        |
        v
quotient = number used
remainder = next subproblem
```

---

# 8. Why Coin Greedy Can Fail

This is one of the most important lessons in the lecture.

---

## 8.1 Counterexample From the Lecture

Denominations:

```text
[1,8,10]
```

Target:

```text
16
```

Largest-first greedy:

```text
10
```

remaining:

```text
6
```

Only `1`s can finish:

```text
10 + 1 + 1 + 1 + 1 + 1 + 1
```

Count:

```text
7
```

But optimal:

```text
8 + 8
```

Count:

```text
2
```

So greedy fails badly.

---

## 8.2 Why the Exchange Proof Breaks

For the lecture's good system:

```text
10 = 2 × 5
```

so enough `5`s can be exchanged for one `10`.

But in:

```text
[1,8,10]
```

`10` is not an integer multiple of `8`.

```text
10 % 8 != 0
```

Using a `10` can leave a remainder that is expensive to construct.

For:

```text
16
```

greedy creates remainder:

```text
6
```

which requires six `1`s.

---

## 8.3 Another Lecture-Annotation Style Example

With denominations:

```text
[1,8,20]
```

target:

```text
27
```

Largest-first:

```text
20 + 7×1
```

Count:

```text
8
```

But:

```text
3×8 + 3×1
= 27
```

Count:

```text
6
```

Again, larger coin is not automatically better.

---

## 8.4 Lesson

Never memorize:

```text
coin change → largest coin first
```

Correct mental model:

```text
coin change
        |
        v
make greedy claim
        |
        v
prove denomination structure supports exchanges
        |
   +----+----+
   |         |
 proof     counterexample
   |         |
 greedy      use DP /
 safe        another method
```

---

# 9. Maximum Product With Fixed Sum

## 9.1 What It Asks

Given integer:

```text
N
```

find integers `A` and `B` such that:

```math
A+B=N
```

and:

```math
A\cdot B
```

is maximum.

Lecture examples include:

```text
N = 5
```

and:

```text
N = 8
```

---

## 9.2 Concept Simplified

If one number is very small and the other very large:

```text
product is not as good
```

For a fixed sum, move the two numbers closer together.

Example for `N = 12`:

```text
1×11 = 11
2×10 = 20
3×9  = 27
4×8  = 32
5×7  = 35
6×6  = 36
```

Then values decrease symmetrically.

So best split is as equal as possible.

---

## 9.3 Greedy Claim

```math
A=
\left\lfloor\frac N2\right\rfloor
```

and:

```math
B=
\left\lceil\frac N2\right\rceil
```

Maximum product:

```math
\left\lfloor\frac N2\right\rfloor
\left\lceil\frac N2\right\rceil
```

---

## 9.4 Proof Using Deviation k

Let:

```math
L=
\left\lfloor\frac N2\right\rfloor
```

and:

```math
R=
\left\lceil\frac N2\right\rceil
```

Greedy product:

```math
GA=LR
```

Any more unbalanced split can be written as:

```math
A=L-k
```

```math
B=R+k
```

for:

```text
k >= 0
```

Other product:

```math
OA=(L-k)(R+k)
```

Expand:

```math
OA
=
LR+Lk-Rk-k^2
```

So:

```math
GA-OA
=
LR-(LR+Lk-Rk-k^2)
```

Simplify:

```math
GA-OA
=
k^2+k(R-L)
```

Now:

```text
k >= 0
```

and:

```text
R-L is either 0 or 1
```

Therefore:

```math
GA-OA\ge0
```

Hence:

```text
balanced split is optimal
```

---

## 9.5 Step-by-Step Example — N = 5

```text
floor(5/2) = 2
ceil(5/2)  = 3
```

Product:

```text
2×3 = 6
```

Other choices:

```text
1×4 = 4
4×1 = 4
```

Maximum:

```text
6
```

---

## 9.6 Step-by-Step Example — N = 8

```text
A = 4
B = 4
```

Product:

```text
16
```

Nearby:

```text
3×5 = 15
2×6 = 12
1×7 = 7
```

Balanced pair wins.

---

## 9.7 C++

```cpp
pair<long long,long long>
bestProductPair(long long n) {
    long long a = n / 2;
    long long b = n - a;

    return {a, b};
}

long long maximumProduct(long long n) {
    long long a = n / 2;
    long long b = n - a;

    return a * b;
}
```

---

## 9.8 Complexity

```text
O(1)
```

---

## 9.9 Recognition Model

```text
two numbers
+
fixed sum
+
maximize product
        |
        v
make the numbers as equal as possible
```

---

# 10. Minimum Dot Product

## 10.1 What It Asks

Given two vectors:

```text
A = [a1,a2,...,an]
B = [b1,b2,...,bn]
```

we may rearrange the elements.

Minimize:

```math
\sum_{i=1}^{n}a_ib_i
```

---

## 10.2 Lecture Example

Original:

```text
A = [-1,3,-2]
B = [-10,1,5]
```

One pairing:

```text
(-1)(-10) + 3(1) + (-2)(5)
```

```text
= 10 + 3 - 10
= 3
```

A much better pairing is:

```text
A ascending:
[-2,-1,3]

B descending:
[5,1,-10]
```

Dot product:

```text
(-2)(5) + (-1)(1) + 3(-10)
```

```text
= -10 - 1 - 30
= -41
```

---

## 10.3 Concept Simplified

To make the sum small:

```text
pair a large positive value
with a small / very negative value

pair a small / negative value
with a large positive value
```

So:

```text
sort one ascending
sort the other descending
```

This is “opposite ordering.”

---

## 10.4 Greedy Claim

Sort:

```math
a_1\le a_2\le\cdots\le a_n
```

and:

```math
b_1\ge b_2\ge\cdots\ge b_n
```

Then pair:

```text
a1 with b1
a2 with b2
...
an with bn
```

This minimizes the dot product.

---

## 10.5 Two-Element Intuition

Let:

```math
a_1\le a_2
```

and:

```math
b_1\le b_2
```

Same-order pairing:

```math
X=a_1b_1+a_2b_2
```

Opposite-order pairing:

```math
Y=a_1b_2+a_2b_1
```

Difference:

```math
X-Y
=
a_1b_1+a_2b_2-a_1b_2-a_2b_1
```

Factor:

```math
X-Y
=
(a_1-a_2)(b_1-b_2)
```

Both factors are:

```text
<= 0
```

so:

```math
X-Y\ge0
```

Therefore:

```math
X\ge Y
```

So opposite pairing is no larger.

### Sign-check note

The lecture's slide 31 writes an intermediate identity with the opposite sign, but the later adjacent-swap proof and the final greedy claim are consistent with the identity above:

```math
X-Y=(a_1-a_2)(b_1-b_2)\ge0
```

The correct conclusion remains:

```text
opposite sorting minimizes the dot product.
```

---

## 10.6 Adjacent-Swap Proof

Assume `A` is ascending:

```math
a_i\le a_j
```

for:

```text
i<j
```

In the greedy arrangement, `B` is descending:

```math
b_i\ge b_j
```

Greedy contribution from positions `i,j`:

```math
G=a_ib_i+a_jb_j
```

Suppose another arrangement swaps the two `B` values:

```math
O=a_ib_j+a_jb_i
```

Difference:

```math
G-O
=
a_ib_i+a_jb_j-a_ib_j-a_jb_i
```

Factor:

```math
G-O
=
(a_i-a_j)(b_i-b_j)
```

Now:

```math
a_i-a_j\le0
```

and:

```math
b_i-b_j\ge0
```

Therefore:

```math
G-O\le0
```

So:

```math
G\le O
```

The greedy ordering is never worse for minimization.

Any inversion away from opposite ordering can be swapped back without increasing the dot product.

Therefore opposite sorting is optimal.

---

## 10.7 Step-by-Step Dry Run

```text
A = [-1,3,-2]
B = [-10,1,5]
```

Sort `A` ascending:

```text
[-2,-1,3]
```

Sort `B` descending:

```text
[5,1,-10]
```

Products:

```text
-2 × 5   = -10
-1 × 1   = -1
 3 × -10 = -30
```

Total:

```text
-10 - 1 - 30
= -41
```

---

## 10.8 Supporting Example

```text
A = [1,2,7]
B = [3,4,10]
```

Ascending `A`:

```text
[1,2,7]
```

Descending `B`:

```text
[10,4,3]
```

Dot product:

```text
1×10 + 2×4 + 7×3
= 10 + 8 + 21
= 39
```

Same-order product:

```text
1×3 + 2×4 + 7×10
= 3 + 8 + 70
= 81
```

Opposite pairing is much smaller.

---

## 10.9 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

long long minimumDotProduct(
    vector<long long> a,
    vector<long long> b
) {
    sort(a.begin(), a.end());
    sort(b.rbegin(), b.rend());

    long long ans = 0;

    for (int i = 0; i < (int)a.size(); ++i) {
        ans += a[i] * b[i];
    }

    return ans;
}
```

---

## 10.10 Complexity

Two sorts:

```text
O(N log N)
```

Dot-product scan:

```text
O(N)
```

Total:

```text
O(N log N)
```

---

## 10.11 Recognition Model

```text
rearrange two arrays
+
minimize sum of pairwise products
        |
        v
pair extremes oppositely
        |
        v
one ascending
other descending
```

For maximum dot product:

```text
same ordering
```

is the corresponding direction.

---

# 11. Core Proof Patterns Learned

This first greedy lecture already gives several reusable proof patterns.

---

## 11.1 Replace a Smaller Selected Value by a Larger One

Used in:

```text
maximum sum of K elements
```

Model:

```math
q\ge p
```

then:

```math
q-p\ge0
```

So for maximization:

```text
replacement cannot hurt
```

---

## 11.2 Extremes Bound Every Other Choice

Used in:

```text
maximum difference
```

Model:

```text
global max >= every candidate high
global min <= every candidate low
```

Therefore:

```text
max - min
```

dominates every other difference.

---

## 11.3 Sorting Makes the Optimal Candidate Local

Used in:

```text
minimum difference
```

After sorting:

```text
a non-adjacent difference contains
at least one adjacent gap inside it
```

So only neighbors matter.

---

## 11.4 Remove Inversions by Swapping

Used in:

```text
maximize i×a[i]
minimum dot product
```

Proof pattern:

```text
find a pair in the wrong relative order
        |
        v
swap them
        |
        v
compute change in objective
        |
        v
prove swap is non-worse
        |
        v
repeat until sorted structure
```

This is a very important greedy/rearrangement proof technique.

---

## 11.5 Replace Many Small Objects With One Large Object

Used in:

```text
coin change with divisible denominations
```

Model:

```math
d_{i+1}=r\,d_i
```

Then:

```text
r smaller coins
→ 1 larger coin
```

reduces count.

---

## 11.6 Balance Two Variables Under a Fixed Sum

Used in:

```text
maximum product A×B
with A+B=N
```

Model:

```text
moving away from equal split
reduces product
```

---

# 12. Greedy Recognition Checklist

Before coding a greedy idea, ask:

```text
1. What exactly is being maximized/minimized?

2. What are the legal choices?

3. Can sorting expose an order?

4. What greedy claim feels natural?

5. Can I compare greedy with another solution?

6. Can I swap one choice?
   If yes, what is GA - OA?

7. Is GA - OA always:
   >= 0 for maximization?
   <= 0 for minimization?

8. Can I remove inversions one by one?

9. Can I replace many small choices
   with one bigger choice?

10. Can I find a counterexample?
```

---

## Fast Pattern Map

| Signal | First Greedy Idea to Test |
|---|---|
| choose K values, maximize sum | K largest |
| choose K values, minimize sum | K smallest |
| max difference, no order restriction | max - min |
| min difference between any pair | sort + adjacent |
| maximize value × increasing weights | sort same direction |
| minimize dot product | opposite sorting |
| fixed sum, maximize product of two values | balance them |
| denomination chain where higher coin replaces lower coins | largest coin first |
| greedy coin rule has no structural proof | search for counterexample / DP |

---

# 13. Compact Revision Card

```text
GREEDY
======
claim + proof

Do NOT think:
"greedy = take largest"

Think:
"what choice can an optimal answer
safely be transformed to contain?"


PROOF TOOL 1 — EXCHANGE
=======================
Greedy chooses q
Other answer chooses p

swap p → q

maximize:
prove q-p >= 0

minimize:
prove change <= 0


MAX K-SUM
=========
sort
take K largest

proof:
replace smaller selected p
with larger unselected q


MAX DIFFERENCE
==============
max - min


MIN DIFFERENCE
==============
sort
check adjacent gaps only


MAX Σ i*a[i]
==============
weights 1..n increase

sort a ascending

large value × large weight

proof:
(a_i-a_j)(i-j) >= 0


COIN CHANGE
===========
For lecture coins:
1,5,10,50,100

higher coin exactly replaces
multiple lower coins

take largest possible first

BUT:
arbitrary coin systems can fail

[1,8,10], X=16:
greedy = 10 + six 1s = 7 coins
optimal = 8 + 8 = 2 coins


FIXED SUM, MAX PRODUCT
======================
A+B=N

best:
A=floor(N/2)
B=ceil(N/2)

make numbers as equal as possible


MINIMUM DOT PRODUCT
===================
A ascending
B descending

small × large
large × small

adjacent-swap proof:
(a_i-a_j)(b_i-b_j) <= 0
```

---

# Final Mental Model

```text
Problem
  |
  v
Optimization?
  |
  v
Make a claim
  |
  v
Try small cases / counterexamples
  |
  v
Can I prove by exchange?
  |
  +------------------------------+
  |                              |
YES                            NO / counterexample
  |                              |
sort / choose safely          greedy not justified
  |                              |
  v                              v
commit choice               try another model
```

> **Core lesson from Greedy 1:**  
> A greedy idea becomes an algorithm only after you can explain **why every competing choice can be exchanged, bounded, balanced, or reordered without producing a better answer**.
