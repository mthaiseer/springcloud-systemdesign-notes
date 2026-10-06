# Greedy Algorithms — Level 3
## Greedy 1 — Compact Proof + Dry-Run Notes

> **Goal:** understand *why* each greedy choice is safe.
>
> **Flow for each problem:** **what it asks → simplified idea → greedy claim → proof + dry run side by side → C++ → complexity → recognition**.
>
> **Core habit:** do not memorize “sort and pick.” Learn how to **exchange, bound, swap, replace, or balance** competing choices.
>
> Display equations use fenced `math` blocks to avoid rendering issues.

---

# Clickable Table of Contents

- [0. Prerequisites](#0-prerequisites)
  - [0.1 Optimization](#01-optimization)
  - [0.2 Sorted-Order Notation](#02-sorted-order-notation)
  - [0.3 G and O — Comparing Greedy With Another Choice](#03-g-and-o--comparing-greedy-with-another-choice)
  - [0.4 Exchange Argument — General Template](#04-exchange-argument--general-template)
  - [0.5 How to Apply Exchange Argument to Any Greedy Problem](#05-how-to-apply-exchange-argument-to-any-greedy-problem)
  - [0.6 Other Proof Patterns](#06-other-proof-patterns)
  - [0.7 Floor, Ceiling, Dot Product, Quotient](#07-floor-ceiling-dot-product-quotient)
- [1. Greedy = Claim + Proof](#1-greedy--claim--proof)
- [2. Maximum Sum of K Elements](#2-maximum-sum-of-k-elements)
- [3. Maximum Difference Between Two Elements](#3-maximum-difference-between-two-elements)
- [4. Minimum Difference Between Two Elements](#4-minimum-difference-between-two-elements)
- [5. Maximize Sum of i × a[i]](#5-maximize-sum-of-i--ai)
- [6. Coin Change — When Largest First Is Safe](#6-coin-change--when-largest-first-is-safe)
- [7. Why Coin Greedy Can Fail](#7-why-coin-greedy-can-fail)
- [8. Maximum Product With Fixed Sum](#8-maximum-product-with-fixed-sum)
- [9. Minimum Dot Product](#9-minimum-dot-product)
- [10. Proof Pattern Summary](#10-proof-pattern-summary)
- [11. Recognition Checklist](#11-recognition-checklist)
- [12. Compact Revision Card](#12-compact-revision-card)

---

# 0. Prerequisites

## 0.1 Optimization

Greedy problems normally have:

```text
1. Constraint  → what must remain valid?
2. Objective   → what must be minimized/maximized?
```

Example:

```text
Constraint:
choose exactly K elements

Objective:
maximize their sum
```

Notation:

```math
\max(\text{value})
```

means:

```text
make the value as large as possible
```

and:

```math
\min(\text{value})
```

means:

```text
make the value as small as possible
```

---

## 0.2 Sorted-Order Notation

If:

```math
a_1\le a_2\le\cdots\le a_n
```

then:

```text
a1 = smallest
an = largest
```

If:

```text
i < j
```

then in this sorted array:

```math
a_i\le a_j
```

This simple fact drives several proofs below.

---

## 0.3 G and O — Comparing Greedy With Another Choice

We use:

```text
G = greedy contribution / greedy answer
O = another feasible contribution / answer
```

For maximization, prove:

```math
G\ge O
```

A convenient equivalent test:

```math
G-O\ge0
```

For minimization, prove:

```math
G\le O
```

or equivalently:

```math
G-O\le0
```

### Tiny Example

Greedy uses:

```text
10
```

instead of:

```text
7
```

in a maximization problem.

Then:

```text
G-O
= 10-7
= 3 >= 0
```

So the exchange cannot make the answer worse.

---

## 0.4 Exchange Argument — General Template

### Concept Simplified

An exchange argument says:

```text
Take any optimal solution.
If it disagrees with the greedy choice,
swap one part of it with the greedy choice.
Show the solution remains valid
and the answer does not become worse.
```

Generic structure:

```text
Greedy wants q
Other/optimal solution uses p
           |
           v
      exchange p → q
           |
           v
still feasible?
           |
           v
objective non-worse?
           |
           v
YES → an optimal solution can contain q
```

### Mathematical Skeleton

For maximization:

```math
\Delta=G-O
```

Prove:

```math
\Delta\ge0
```

For minimization:

```math
\Delta=G-O
```

Prove:

```math
\Delta\le0
```

---

## 0.5 How to Apply Exchange Argument to Any Greedy Problem

This is the reusable proof checklist.

### Step 1 — State the Greedy Choice

Example:

```text
Choose the largest remaining value.
```

or:

```text
Put the larger value on the larger weight.
```

---

### Step 2 — Assume an Optimal Solution Disagrees

Suppose the optimal solution uses:

```text
p
```

where greedy uses:

```text
q
```

or suppose two values appear in the wrong relative order.

---

### Step 3 — Exchange Only the Disagreement

Do **not** rebuild the entire solution.

Swap just:

```text
p ↔ q
```

or:

```text
two misplaced items
```

---

### Step 4 — Check Feasibility

Ask:

```text
Does the swapped solution still obey every constraint?
```

If not, the exchange proof fails.

---

### Step 5 — Compare Objective Before vs After

Typical forms:

```math
q-p
```

or:

```math
(a_i-a_j)(w_i-w_j)
```

or:

```math
\text{new cost}-\text{old cost}
```

---

### Step 6 — Use the Sign

For maximization:

```text
change >= 0
→ greedy is no worse
```

For minimization:

```text
change <= 0
→ greedy is no worse
```

---

### Step 7 — Repeat

If one exchange fixes one disagreement:

```text
repeat exchanges
until the whole optimal solution
has greedy structure
```

Then greedy is optimal.

---

### Generic Mini Dry Run

Suppose greedy says:

```text
larger value should receive larger weight
```

Values:

```text
6 < 10
```

Weights:

```text
1 < 3
```

Greedy:

```text
6×1 + 10×3 = 36
```

Wrong order:

```text
10×1 + 6×3 = 28
```

Exchange benefit:

```text
36-28 = 8 >= 0
```

So the inversion should be removed.

> **Use this exact template whenever you suspect a sorting-based greedy proof.**

---

## 0.6 Other Proof Patterns

Not every greedy proof is an exchange proof.

### Bound / Extreme Proof

Show global extremes dominate every candidate.

Example:

```text
maximum difference
→ global max - global min
```

---

### Adjacent-Swap Proof

Find a wrong pair:

```text
... x ... y ...
```

Swap it.

Show the objective improves or stays equal.

Repeat until no wrong pairs remain.

---

### Replacement Proof

Replace:

```text
many small objects
```

with:

```text
one larger object
```

without changing feasibility but improving the objective.

Used in coin change.

---

### Balancing Proof

Compare the balanced solution with one `k` steps away from balance.

Used for:

```text
A+B=N
maximize AB
```

---

## 0.7 Floor, Ceiling, Dot Product, Quotient

### Floor / Ceiling

```math
\left\lfloor x\right\rfloor
```

= greatest integer `<= x`.

```math
\left\lceil x\right\rceil
```

= smallest integer `>= x`.

Example:

```text
N = 5

floor(N/2) = 2
ceil(N/2)  = 3
```

---

### Dot Product

```math
A\cdot B
=
\sum_{i=1}^{n}a_ib_i
```

Example:

```text
A = [2,3]
B = [5,7]

2×5 + 3×7 = 31
```

---

### Quotient / Remainder

```math
X=qD+r
```

where:

```text
q = X / D
r = X % D
```

Example:

```text
256 / 100 = 2
256 % 100 = 56
```

So:

```text
use two 100-coins
continue with 56
```

---

# 1. Greedy = Claim + Proof

Greedy means:

```text
observe structure
      ↓
make a local-choice claim
      ↓
try to break it
      ↓
prove it is safe
      ↓
commit and continue
```

The two essential pieces are:

```text
CLAIM
+
PROOF
```

Not:

```text
sort + hope
```

A good contest workflow:

```text
1. Guess the greedy rule.
2. Test small/adversarial examples.
3. Search for a counterexample.
4. If it survives, prove it.
5. Only then code.
```

---

# 2. Maximum Sum of K Elements

## 2.1 What It Asks

Choose exactly `K` elements and maximize:

```math
\sum_{i\in S}a_i
```

with:

```math
|S|=K
```

---

## 2.2 Concept Simplified

If a chosen value is smaller than an unchosen value:

```text
replace the smaller by the larger
```

The sum cannot decrease.

So:

```text
take the K largest values
```

---

## 2.3 Greedy Claim

After sorting:

```math
a_1\le a_2\le\cdots\le a_n
```

take the final `K` elements.

---

## 2.4 Exchange Proof + Dry Run Side by Side

Suppose another solution contains smaller value `p`, while larger value `q` is unselected.

```math
q\ge p
```

Exchange:

```text
p → q
```

| Proof step | Symbolic | Dry run |
|---|---|---|
| Selected smaller | `p` | `7` |
| Unselected larger | `q` | `10` |
| Exchange | `p → q` | `7 → 10` |
| Change in sum | `q-p` | `10-7=3` |
| Sign | `q-p >= 0` | `3 >= 0` |
| Conclusion | sum does not decrease | sum improves by `3` |

Therefore we can repeatedly exchange smaller selected values for larger unselected ones until the selected set is exactly the `K` largest.

### Quick Full Example

```text
a = [11,6,7,2,0,2,9,10]
K = 2
```

Sorted:

```text
[0,2,2,6,7,9,10,11]
```

Answer:

```text
11 + 10 = 21
```

---

## 2.5 C++

```cpp
long long maxKSum(vector<long long> a, int k) {
    sort(a.rbegin(), a.rend());

    long long ans = 0;

    for (int i = 0; i < k; ++i)
        ans += a[i];

    return ans;
}
```

Complexity:

```text
O(N log N)
```

Recognition:

```text
choose exactly K independent values
+
maximize sum
→ K largest
```

---

# 3. Maximum Difference Between Two Elements

## 3.1 What It Asks

Maximize:

```math
a_{\text{high}}-a_{\text{low}}
```

with no original-index ordering restriction.

---

## 3.2 Concept Simplified

Make:

```text
high as large as possible
low  as small as possible
```

So:

```text
answer = max - min
```

---

## 3.3 Bound Proof + Dry Run Side by Side

For any pair:

```math
a_p\le a_q
```

global minimum gives:

```math
a_1\le a_p
```

global maximum gives:

```math
a_q\le a_n
```

| Step | Symbolic | Dry run |
|---|---|---|
| Candidate pair | `a_q-a_p` | `15-8=7` |
| Lower low-end to global min | `a_q-a_1 >= a_q-a_p` | `15-4=11 >= 7` |
| Raise high-end to global max | `a_n-a_1 >= a_q-a_1` | `16-4=12 >= 11` |
| Final | `a_n-a_1` dominates every pair | `12` |

Therefore:

```math
a_n-a_1\ge a_q-a_p
```

for every candidate pair.

---

## 3.4 C++

```cpp
long long maximumDifference(
    const vector<long long>& a
) {
    auto [mn, mx] =
        minmax_element(a.begin(), a.end());

    return *mx - *mn;
}
```

Complexity:

```text
O(N)
```

Recognition:

```text
unrestricted maximum difference
→ global max - global min
```

---

# 4. Minimum Difference Between Two Elements

## 4.1 What It Asks

Minimize:

```math
|a_i-a_j|
```

for two distinct elements.

---

## 4.2 Concept Simplified

Sort first.

Then numerically closest values become neighbors.

So check only adjacent gaps.

---

## 4.3 Proof + Dry Run Side by Side

Take a non-adjacent pair:

```text
a_j ... a_(i-1), a_i
```

Sorted order:

```math
a_j\le a_{i-1}\le a_i
```

Thus:

```math
a_i-a_j\ge a_i-a_{i-1}
```

| Step | Symbolic | Dry run |
|---|---|---|
| Sorted values | `a_j <= a_(i-1) <= a_i` | `3 <= 9 <= 10` |
| Non-adjacent gap | `a_i-a_j` | `10-3=7` |
| Adjacent gap | `a_i-a_(i-1)` | `10-9=1` |
| Comparison | non-adjacent `>=` adjacent | `7 >= 1` |

Therefore a non-adjacent pair cannot be uniquely better than all adjacent pairs.

So some optimum appears among adjacent pairs.

### Example

```text
[20,3,17,9,10]
```

Sort:

```text
[3,9,10,17,20]
```

Gaps:

```text
6,1,7,3
```

Answer:

```text
1
```

---

## 4.4 C++

```cpp
long long minimumDifference(vector<long long> a) {
    sort(a.begin(), a.end());

    long long ans = LLONG_MAX;

    for (int i = 1; i < (int)a.size(); ++i)
        ans = min(ans, a[i] - a[i - 1]);

    return ans;
}
```

Complexity:

```text
O(N log N)
```

Recognition:

```text
minimum absolute difference
→ sort + adjacent pairs
```

---

# 5. Maximize Sum of i × a[i]

## 5.1 What It Asks

Rearrange values to maximize:

```math
\sum_{i=1}^{n}i\cdot a_i
```

Weights:

```text
1,2,...,n
```

increase.

---

## 5.2 Concept Simplified

For maximum weighted sum:

```text
small value → small weight
large value → large weight
```

Therefore sort values ascending.

---

## 5.3 Exchange Proof + Dry Run Side by Side

Take:

```text
i < j
```

and sorted values:

```math
a_i\le a_j
```

Greedy pairing:

```text
a_i with i
a_j with j
```

Swapped pairing:

```text
a_j with i
a_i with j
```

Use:

```text
i = 1, j = 3
a_i = 6, a_j = 10
```

| Step | Symbolic | Dry run |
|---|---|---|
| Greedy | `G=a_i*i+a_j*j` | `6×1+10×3=36` |
| Swapped | `O=a_j*i+a_i*j` | `10×1+6×3=28` |
| Difference | `G-O` | `36-28=8` |
| Factor | `(a_i-a_j)(i-j)` | `(6-10)(1-3)` |
| Signs | `(-)×(-)` | `(-4)×(-2)` |
| Result | `G-O >= 0` | `8 >= 0` |

### Algebra Expansion

```math
G-O
=
a_i i+a_j j-a_j i-a_i j
```

Group:

```math
G-O
=
a_i(i-j)+a_j(j-i)
```

Since:

```math
j-i=-(i-j)
```

then:

```math
G-O
=
a_i(i-j)-a_j(i-j)
```

Factor:

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

Therefore:

```math
G-O\ge0
```

So:

```math
G\ge O
```

Every inversion can be exchanged away without decreasing the answer.

Thus ascending order is optimal.

---

## 5.4 C++

```cpp
long long maximumWeightedSum(vector<long long> a) {
    sort(a.begin(), a.end());

    long long ans = 0;

    for (int i = 0; i < (int)a.size(); ++i)
        ans += 1LL * (i + 1) * a[i];

    return ans;
}
```

Complexity:

```text
O(N log N)
```

Recognition:

```text
maximize value × increasing weight
→ same order
```

---

# 6. Coin Change — When Largest First Is Safe

## 6.1 What It Asks

Coins:

```text
1,5,10,50,100
```

Unlimited copies.

Minimize number of coins used to form `X`.

---

## 6.2 Concept Simplified

Larger coins exactly replace several smaller coins:

```text
5×1   → 1×5
2×5   → 1×10
5×10  → 1×50
2×50  → 1×100
```

Every replacement reduces coin count.

---

## 6.3 Replacement Proof + Dry Run Side by Side

Suppose:

```math
d_{i+1}=r\,d_i
```

with integer:

```text
r >= 2
```

Then:

```text
r copies of d_i
```

can be replaced by:

```text
1 copy of d_(i+1)
```

| Proof | Dry run |
|---|---|
| `d_(i+1)=r*d_i` | `10=2×5` |
| `r` lower coins | `5+5` |
| same value | `10` |
| replacement | `2 coins → 1 coin` |
| objective | number of coins decreases | 

Therefore an optimum never needs enough lower coins to form an available larger denomination.

So process coins largest → smallest.

---

## 6.4 Dry Run — X = 256

```text
256 / 100 = 2, remainder 56
56  / 50  = 1, remainder 6
6   / 10  = 0
6   / 5   = 1, remainder 1
1   / 1   = 1
```

Coins:

```text
100 + 100 + 50 + 5 + 1
```

Count:

```text
5
```

---

## 6.5 C++

```cpp
long long minCoins(long long x) {
    vector<long long> coin = {
        100, 50, 10, 5, 1
    };

    long long ans = 0;

    for (long long d : coin) {
        ans += x / d;
        x %= d;
    }

    return ans;
}
```

Complexity:

```text
O(number of denominations)
```

Recognition:

```text
higher denomination exactly replaces
multiple lower denominations
→ largest first
```

---

# 7. Why Coin Greedy Can Fail

Coins:

```text
[1,8,10]
```

Target:

```text
16
```

Greedy:

```text
10 + 1 + 1 + 1 + 1 + 1 + 1
= 7 coins
```

Optimal:

```text
8 + 8
= 2 coins
```

Why did the proof fail?

```text
10 % 8 != 0
```

A `10` does not cleanly replace a fixed number of `8`s.

Choosing `10` creates bad remainder:

```text
16-10 = 6
```

which needs six `1`s.

Recognition:

```text
coin change
        |
        v
largest-first idea
        |
        v
can I prove replacement?
   +----+----+
   |         |
 YES        NO
   |         |
greedy     test counterexample /
safe       use DP or another method
```

---

# 8. Maximum Product With Fixed Sum

## 8.1 What It Asks

Find integers `A,B` such that:

```math
A+B=N
```

and maximize:

```math
AB
```

---

## 8.2 Concept Simplified

With fixed sum:

```text
closer numbers → larger product
```

Example:

```text
N = 12

1×11 = 11
2×10 = 20
3×9  = 27
4×8  = 32
5×7  = 35
6×6  = 36
```

So use the most balanced split.

---

## 8.3 Balancing Proof + Dry Run Side by Side

Define:

```math
L=\left\lfloor\frac N2\right\rfloor
```

```math
R=\left\lceil\frac N2\right\rceil
```

Greedy:

```math
G=LR
```

Move `k` away from balance:

```math
A=L-k
```

```math
B=R+k
```

Other product:

```math
O=(L-k)(R+k)
```

Expand:

```math
O
=
LR+Lk-Rk-k^2
```

Therefore:

```math
G-O
=
k^2+k(R-L)
```

Now:

```text
k >= 0
R-L is 0 or 1
```

So:

```math
G-O\ge0
```

Balanced product is optimal.

### Side-by-Side Dry Run — N = 8

| Step | Symbolic | Dry run |
|---|---|---|
| Balanced halves | `L,R` | `4,4` |
| Greedy product | `G=LR` | `4×4=16` |
| Move away by `k=1` | `(L-k,R+k)` | `(3,5)` |
| Other product | `O` | `3×5=15` |
| Difference formula | `k²+k(R-L)` | `1²+1(4-4)=1` |
| Check | `G-O >= 0` | `16-15=1` |

---

## 8.4 C++

```cpp
long long maximumProduct(long long n) {
    long long a = n / 2;
    long long b = n - a;

    return a * b;
}
```

Complexity:

```text
O(1)
```

Recognition:

```text
two variables
+
fixed sum
+
maximize product
→ balance them
```

---

# 9. Minimum Dot Product

## 9.1 What It Asks

Rearrange arrays `A` and `B` to minimize:

```math
\sum_{i=1}^{n}a_ib_i
```

---

## 9.2 Concept Simplified

For minimum sum of products:

```text
small value ↔ large value
large value ↔ small value
```

So:

```text
A ascending
B descending
```

---

## 9.3 Exchange Proof + Dry Run Side by Side

For the local proof, write two values from each array in ascending order:

```math
a_1\le a_2
```

```math
b_1\le b_2
```

Compare:

```text
same order:
a1 with b1
a2 with b2

opposite order:
a1 with b2
a2 with b1
```

Use:

```text
a1 = 2, a2 = 7
b1 = 3, b2 = 10
```

| Step | Symbolic | Dry run |
|---|---|---|
| Same order | `X=a1*b1+a2*b2` | `2×3+7×10=76` |
| Opposite order | `Y=a1*b2+a2*b1` | `2×10+7×3=41` |
| Difference | `X-Y` | `76-41=35` |
| Factor | `(a1-a2)(b1-b2)` | `(2-7)(3-10)` |
| Signs | `(-)×(-)` | `(-5)×(-7)` |
| Result | `X-Y >= 0` | `35 >= 0` |

Therefore:

```math
X\ge Y
```

So opposite pairing is no larger and is therefore better for minimization.

---

### Algebra Expansion Side by Side

| Symbolic step | Numerical step |
|---|---|
| `X-Y = a1*b1+a2*b2-a1*b2-a2*b1` | `76-41` |
| `= a1(b1-b2)+a2(b2-b1)` | `= 2(3-10)+7(10-3)` |
| `b2-b1 = -(b1-b2)` | `10-3 = -(3-10)` |
| `= (a1-a2)(b1-b2)` | `= (2-7)(3-10)` |
| `= (-)(-) >= 0` | `=(-5)(-7)=35` |

That is the whole proof in one view.

---

## 9.4 Why This Proves the Whole Array

Suppose `A` is sorted ascending but some two `B` values are also ascending:

```text
a_i <= a_j
b_i <= b_j
```

The two-element proof says:

```text
swap b_i and b_j
```

to create the opposite pairing.

The dot product does not increase.

So:

```text
find same-direction pair
→ swap it
→ answer non-increasing
→ repeat
```

Eventually:

```text
A ascending
B descending
```

Therefore opposite sorting is optimal.

This is the **exchange argument** for the full problem.

---

## 9.5 Full Example

```text
A = [-1,3,-2]
B = [-10,1,5]
```

Sort:

```text
A ascending  = [-2,-1,3]
B descending = [5,1,-10]
```

Dot product:

```text
-2×5 + -1×1 + 3×(-10)
= -10 - 1 - 30
= -41
```

---

## 9.6 C++

```cpp
long long minimumDotProduct(
    vector<long long> a,
    vector<long long> b
) {
    sort(a.begin(), a.end());
    sort(b.rbegin(), b.rend());

    long long ans = 0;

    for (int i = 0; i < (int)a.size(); ++i)
        ans += a[i] * b[i];

    return ans;
}
```

Complexity:

```text
O(N log N)
```

Recognition:

```text
minimize sum of pairwise products
→ opposite order
```

For maximum dot product:

```text
same order
```

---

# 10. Proof Pattern Summary

| Problem | Greedy claim | Proof technique |
|---|---|---|
| Maximum K-sum | K largest | exchange smaller ↔ larger |
| Maximum difference | max - min | global-bound/extreme proof |
| Minimum difference | adjacent after sort | containment/bound proof |
| Max `Σ i*a[i]` | same ordering | exchange / adjacent swap |
| Coin change | largest coin first | replacement proof |
| Fixed-sum product | balance values | deviation/balancing proof |
| Minimum dot product | opposite ordering | exchange / adjacent swap |

---

## Exchange Pattern

```text
optimal solution disagrees
        |
        v
isolate one disagreement
        |
        v
exchange it
        |
        v
check feasibility
        |
        v
compute G-O
        |
        v
sign proves non-worse
        |
        v
repeat
```

---

## Bound Pattern

```text
arbitrary candidate
        |
        v
replace with global extreme
        |
        v
objective can only improve
```

---

## Replacement Pattern

```text
several smaller choices
        |
        v
one larger equivalent choice
        |
        v
same feasibility
better objective
```

---

## Balancing Pattern

```text
fixed total
        |
        v
start at balanced solution
        |
        v
move k away
        |
        v
expand G-O
        |
        v
show G-O >= 0
```

---

# 11. Recognition Checklist

Before coding a greedy solution:

```text
1. What is the objective?
2. What constraints define feasibility?
3. Does sorting reveal structure?
4. What is my exact greedy claim?
5. Can I construct an optimal solution that disagrees?
6. Can I exchange only the disagreement?
7. Does feasibility survive the exchange?
8. What is G-O?
9. Does its sign prove the greedy choice is non-worse?
10. Can I find a counterexample?
```

If you cannot prove the local choice is safe:

```text
do not trust the greedy rule yet
```

---

# 12. Compact Revision Card

```text
GREEDY
======
claim + proof


EXCHANGE ARGUMENT
=================
1. State greedy choice.
2. Assume OPT disagrees.
3. Swap one disagreement.
4. Keep feasibility.
5. Compare G-O.
6. Prove non-worse.
7. Repeat.


MAX K-SUM
=========
K largest

exchange:
q-p >= 0


MAX DIFFERENCE
==============
global max - global min


MIN DIFFERENCE
==============
sort + adjacent gaps


MAX Σ i*a[i]
==============
same order

G-O
=
(a_i-a_j)(i-j)
>= 0


COIN CHANGE
===========
largest first only when
replacement structure is provable

r small coins
→ 1 larger equivalent coin


COUNTEREXAMPLE
==============
[1,8,10], X=16

greedy = 7 coins
optimal = 2 coins


FIXED SUM MAX PRODUCT
=====================
A+B=N

balance:

A=floor(N/2)
B=ceil(N/2)

G-O
=
k²+k(R-L)
>= 0


MINIMUM DOT PRODUCT
===================
A ascending
B descending

same - opposite
=
(a1-a2)(b1-b2)
>= 0

therefore:
opposite <= same
```

---

# Final Mental Model

```text
Optimization
     |
     v
Greedy claim
     |
     v
Try to break it
     |
  +--+--+
  |     |
breaks survives
  |     |
reject prove
        |
        +--------------------------+
        |        |        |        |
     exchange   bound replacement balance
        |
        v
     safe choice
```

> **Core lesson:** the important skill is not noticing that a solution sorts. It is being able to explain **why any disagreement with the greedy order can be exchanged away without improving the answer**.
