# Greedy Algorithms — Level 3
## Greedy 1 — Compact Proof Notes with Dry Runs

> **Goal:** understand every greedy proof by seeing the **symbolic algebra and a concrete dry run together**.
>
> **Problem flow:** **what it asks → concept simplified → greedy claim → proof + dry run → C++ → complexity → recognition**.
>
> **Main rule:** a greedy idea is not complete until we can explain **why the choice is safe**.
>
> Display equations use fenced `math` blocks to avoid rendering issues.

---

# Clickable Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. Greedy = Claim + Proof](#1-greedy--claim--proof)
- [2. Maximum Sum of K Elements](#2-maximum-sum-of-k-elements)
- [3. Maximum Difference Between Two Elements](#3-maximum-difference-between-two-elements)
- [4. Minimum Difference Between Two Elements](#4-minimum-difference-between-two-elements)
- [5. Maximize Sum of i × a[i]](#5-maximize-sum-of-i--ai)
- [6. Coin Change — When Largest First Is Safe](#6-coin-change--when-largest-first-is-safe)
- [7. Coin Change — Why Greedy Can Fail](#7-coin-change--why-greedy-can-fail)
- [8. Maximum Product With Fixed Sum](#8-maximum-product-with-fixed-sum)
- [9. Minimum Dot Product](#9-minimum-dot-product)
- [10. Proof Pattern Summary](#10-proof-pattern-summary)
- [11. Greedy Recognition Checklist](#11-greedy-recognition-checklist)
- [12. Compact Revision Card](#12-compact-revision-card)

---

# 0. Prerequisites

## 0.1 Optimization

Greedy problems usually ask us to optimize something.

```math
\max(\text{value})
```

means:

```text
make the value as large as possible
```

```math
\min(\text{value})
```

means:

```text
make the value as small as possible
```

Example:

```text
Constraint:
choose exactly K elements

Objective:
maximize their sum
```

Always separate:

```text
What must remain valid?
        ↓
constraint

What should become best?
        ↓
objective
```

---

## 0.2 Sorted Order

If:

```math
a_1\le a_2\le\cdots\le a_n
```

then:

```text
a1 = smallest
an = largest
```

Also:

```text
i < j
```

implies:

```math
a_i\le a_j
```

when the array is sorted ascending.

This simple fact is used in almost every proof below.

---

## 0.3 Greedy Answer and Other Answer

We use:

```text
G = greedy contribution / greedy answer
O = another feasible contribution / answer
```

For maximization, prove:

```math
G\ge O
```

For minimization, prove:

```math
G\le O
```

Sometimes the easiest route is to calculate:

```math
G-O
```

For maximization:

```text
G - O >= 0
```

is good.

For minimization:

```text
G - O <= 0
```

is good.

---

## 0.4 Exchange Argument

### Concept Simplified

Suppose another solution makes a different choice.

```text
Other solution chooses p
Greedy wants q
```

Swap:

```text
p → q
```

Then prove:

```text
1. the solution is still valid
2. the objective is not worse
```

If this can be repeated until the solution becomes the greedy arrangement, the greedy strategy is optimal.

---

### Tiny Example

Suppose:

```text
p = 7
q = 10
```

For a maximization problem:

```text
replace 7 by 10
```

Change:

```text
10 - 7 = 3 >= 0
```

So the answer cannot decrease.

---

## 0.5 Floor and Ceiling

```math
\left\lfloor x\right\rfloor
```

means:

```text
greatest integer <= x
```

```math
\left\lceil x\right\rceil
```

means:

```text
smallest integer >= x
```

Example:

```text
N = 5

floor(N/2) = 2
ceil(N/2)  = 3
```

These are the two integers closest to an equal split.

---

## 0.6 Dot Product

For:

```text
A = [a1,a2,...,an]
B = [b1,b2,...,bn]
```

dot product:

```math
A\cdot B
=
\sum_{i=1}^{n}a_ib_i
```

Example:

```text
A = [2,3]
B = [5,7]

dot product
= 2×5 + 3×7
= 31
```

---

## 0.7 Quotient and Remainder

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

q = 2
r = 56
```

Meaning:

```text
use 100 two times
remaining = 56
```

This is exactly the operation used in greedy coin change.

---

# 1. Greedy = Claim + Proof

A greedy algorithm does not try every possibility.

Instead:

```text
observe structure
      ↓
make a claim
      ↓
commit to a local choice
      ↓
prove that choice can be part of an optimum
```

The most important habit is:

```text
Do not ask:
"What looks best?"

Ask:
"What looks best AND can be proved safe?"
```

---

## 1.1 How to Test a Greedy Claim

Use three checks:

```text
1. Intuition
2. Counterexamples
3. Proof
```

Typical proof tools in this lecture:

```text
exchange argument
bound/extreme argument
adjacent-swap argument
balancing argument
replacement argument
```

---

## 1.2 Quick Counterexample Habit

Claim:

```text
"largest coin first always minimizes number of coins"
```

Try:

```text
coins = [1,8,10]
target = 16
```

Greedy:

```text
10 + six 1s
= 7 coins
```

Better:

```text
8 + 8
= 2 coins
```

So the rule is false in general.

This is why:

```text
greedy = claim + proof
```

not:

```text
greedy = sort + hope
```

---

# 2. Maximum Sum of K Elements

## 2.1 What It Asks

Choose exactly `K` elements from an array so that their sum is maximum.

Mathematical model:

```math
|S|=K
```

and maximize:

```math
\sum_{i\in S}a_i
```

---

## 2.2 Concept Simplified

If we selected a smaller value while a larger value is still outside:

```text
swap them
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

choose:

```text
a[n-K+1] ... a[n]
```

---

## 2.4 Proof + Dry Run Side by Side

Suppose another solution contains:

```text
smaller selected value p
```

while a larger greedy value:

```text
q
```

is not selected.

Since:

```math
q\ge p
```

replace:

```text
p → q
```

| Proof step | Symbolic | Dry run |
|---|---|---|
| Smaller selected | `p` | `7` |
| Larger unselected | `q` | `10` |
| Greedy replacement | `p → q` | `7 → 10` |
| Change | `q-p` | `10-7=3` |
| Sign | `q-p >= 0` | `3 >= 0` |
| Result | sum does not decrease | sum increases by `3` |

Therefore every smaller selected value can be exchanged for a larger unselected one.

Repeat until the chosen set is exactly the `K` largest values.

---

## 2.5 Full Example

```text
a = [11,6,7,2,0,2,9,10]
K = 2
```

Sort:

```text
[0,2,2,6,7,9,10,11]
```

Greedy:

```text
11 + 10 = 21
```

Other choice:

```text
11 + 7 = 18
```

Exchange:

```text
7 → 10
```

Improvement:

```text
10 - 7 = 3
```

New answer:

```text
21
```

---

## 2.6 C++

```cpp
long long maxKSum(vector<long long> a, int k) {
    sort(a.rbegin(), a.rend());

    long long ans = 0;

    for (int i = 0; i < k; ++i)
        ans += a[i];

    return ans;
}
```

---

## 2.7 Complexity

```text
Sorting: O(N log N)
Sum K:   O(K)

Total:   O(N log N)
```

---

## 2.8 Recognition Model

```text
choose exactly K
+
independent values
+
maximize sum
        |
        v
take K largest
```

For minimum sum:

```text
take K smallest
```

---

# 3. Maximum Difference Between Two Elements

## 3.1 What It Asks

Choose two elements so that the difference is maximum.

```math
\max(a_{\text{high}}-a_{\text{low}})
```

No original-index ordering restriction is assumed.

---

## 3.2 Concept Simplified

To maximize:

```text
high - low
```

make:

```text
high as large as possible
low  as small as possible
```

Therefore:

```text
maximum - minimum
```

---

## 3.3 Proof + Dry Run Side by Side

Take any pair:

```math
a_p\le a_q
```

Global minimum:

```math
a_1\le a_p
```

Global maximum:

```math
a_q\le a_n
```

Now enlarge the gap in two safe steps.

| Step | Symbolic | Dry run |
|---|---|---|
| Candidate pair | `a_q-a_p` | `15-8=7` |
| Replace low by global min | `a_q-a_1 >= a_q-a_p` | `15-4=11 >= 7` |
| Replace high by global max | `a_n-a_1 >= a_q-a_1` | `16-4=12 >= 11` |
| Final | `a_n-a_1` is best | `12` |

So:

```math
a_n-a_1\ge a_q-a_p
```

for every candidate pair.

---

## 3.4 Example

```text
a = [8,4,15,16]
```

Minimum:

```text
4
```

Maximum:

```text
16
```

Answer:

```text
16 - 4 = 12
```

---

## 3.5 C++

```cpp
long long maximumDifference(
    const vector<long long>& a
) {
    auto [mn, mx] =
        minmax_element(a.begin(), a.end());

    return *mx - *mn;
}
```

---

## 3.6 Complexity

```text
O(N)
```

Extra space:

```text
O(1)
```

---

## 3.7 Recognition Model

```text
maximize unrestricted value difference
        |
        v
global maximum - global minimum
```

> If the problem requires `i < j` in the original array, order matters and this simple proof no longer applies directly.

---

# 4. Minimum Difference Between Two Elements

## 4.1 What It Asks

Choose two distinct values with minimum absolute difference.

```math
\min_{i\ne j}|a_i-a_j|
```

---

## 4.2 Concept Simplified

After sorting:

```text
numerically close values become neighbors
```

So only adjacent pairs need to be checked.

---

## 4.3 Greedy Claim

Sort:

```math
a_1\le a_2\le\cdots\le a_n
```

Answer:

```math
\min_{2\le i\le n}(a_i-a_{i-1})
```

---

## 4.4 Proof + Dry Run Side by Side

Take a non-adjacent pair:

```text
a_j ... a_(i-1), a_i
```

with:

```text
j < i-1
```

Sorted order gives:

```math
a_j\le a_{i-1}\le a_i
```

Therefore:

```math
a_i-a_j\ge a_i-a_{i-1}
```

| Proof step | Symbolic | Dry run |
|---|---|---|
| Sorted values | `a_j <= a_(i-1) <= a_i` | `3 <= 9 <= 10` |
| Non-adjacent gap | `a_i-a_j` | `10-3=7` |
| Adjacent gap | `a_i-a_(i-1)` | `10-9=1` |
| Compare | non-adjacent `>=` adjacent | `7 >= 1` |

So a non-adjacent pair cannot beat every adjacent pair.

At least one optimal pair must be adjacent.

---

## 4.5 Example

```text
a = [20,3,17,9,10]
```

Sort:

```text
[3,9,10,17,20]
```

Adjacent gaps:

```text
9-3   = 6
10-9  = 1
17-10 = 7
20-17 = 3
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

## 4.6 C++

```cpp
long long minimumDifference(
    vector<long long> a
) {
    sort(a.begin(), a.end());

    long long ans = LLONG_MAX;

    for (int i = 1; i < (int)a.size(); ++i)
        ans = min(ans, a[i] - a[i - 1]);

    return ans;
}
```

---

## 4.7 Complexity

```text
Sorting: O(N log N)
Scan:    O(N)

Total:   O(N log N)
```

---

## 4.8 Recognition Model

```text
minimum absolute difference
between any two values
        |
        v
sort
        |
        v
check adjacent gaps
```

---

# 5. Maximize Sum of i × a[i]

## 5.1 What It Asks

Rearrange the array to maximize:

```math
\sum_{i=1}^{n}i\cdot a_i
```

Indices:

```text
1,2,...,n
```

are increasing weights.

---

## 5.2 Concept Simplified

If weights increase:

```text
small values should get small weights
large values should get large weights
```

So sort values ascending.

---

## 5.3 Greedy Claim

```math
a_1\le a_2\le\cdots\le a_n
```

Pair:

```text
a1 with weight 1
a2 with weight 2
...
an with weight n
```

---

## 5.4 Proof + Dry Run Side by Side

Take two positions:

```text
i < j
```

and two values:

```math
a_i\le a_j
```

Greedy:

```text
small value at small index
large value at large index
```

Swapped:

```text
large value at small index
small value at large index
```

Use dry run:

```text
i = 1
j = 3

a_i = 6
a_j = 10
```

| Step | Symbolic | Dry run |
|---|---|---|
| Greedy | `G = a_i i + a_j j` | `6×1 + 10×3 = 36` |
| Swapped | `O = a_j i + a_i j` | `10×1 + 6×3 = 28` |
| Difference | `G-O` | `36-28=8` |
| Factored | `(a_i-a_j)(i-j)` | `(6-10)(1-3)` |
| Signs | `(-)×(-)` | `(-4)×(-2)` |
| Result | `G-O >= 0` | `8 >= 0` |

Now expand the algebra slowly.

```math
G-O
=
a_i i+a_j j-a_j i-a_i j
```

Group terms containing `a_i` and `a_j`:

```math
G-O
=
a_i(i-j)+a_j(j-i)
```

Since:

```math
j-i=-(i-j)
```

we get:

```math
G-O
=
a_i(i-j)-a_j(i-j)
```

Factor `(i-j)`:

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

So:

```text
negative × negative
=
non-negative
```

Therefore:

```math
G-O\ge0
```

Hence:

```math
G\ge O
```

Every inverted pair can be swapped toward ascending order without decreasing the answer.

Repeat until the whole array is ascending.

---

## 5.5 Full Example

```text
a = [10,6,7]
```

Ascending:

```text
[6,7,10]
```

Greedy:

```text
1×6 + 2×7 + 3×10
= 6 + 14 + 30
= 50
```

Bad order:

```text
[10,7,6]
```

Value:

```text
1×10 + 2×7 + 3×6
= 10 + 14 + 18
= 42
```

---

## 5.6 C++

```cpp
long long maximumWeightedSum(
    vector<long long> a
) {
    sort(a.begin(), a.end());

    long long ans = 0;

    for (int i = 0; i < (int)a.size(); ++i)
        ans += 1LL * (i + 1) * a[i];

    return ans;
}
```

---

## 5.7 Complexity

```text
O(N log N)
```

---

## 5.8 Recognition Model

```text
rearrange values
+
weights increase
+
maximize weighted sum
        |
        v
same order:
small-small
large-large
```

---

# 6. Coin Change — When Largest First Is Safe

## 6.1 What It Asks

Denominations:

```text
1,5,10,50,100
```

Unlimited supply.

Given amount `X`, minimize number of coins.

---

## 6.2 Concept Simplified

A larger denomination can exactly replace several smaller coins.

Examples:

```text
5 × 1  → 1 × 5
2 × 5  → 1 × 10
5 × 10 → 1 × 50
2 × 50 → 1 × 100
```

Each replacement reduces the number of coins.

So we should use large coins whenever possible.

---

## 6.3 Greedy Claim

Process coins descending.

For coin `d`:

```text
number used = X / d
remaining   = X % d
```

Then continue with the remainder.

---

## 6.4 Why It Works — Replacement Proof + Dry Run

Suppose two consecutive denominations satisfy:

```math
d_{i+1}=r\,d_i
```

with:

```text
r >= 2
```

If a solution uses:

```text
r copies of d_i
```

their value is:

```math
r\,d_i=d_{i+1}
```

Replace:

```text
r small coins
→ 1 larger coin
```

Coin count decreases.

| Symbolic | Dry run with `5` and `10` |
|---|---|
| `d_(i+1)=r*d_i` | `10=2×5` |
| `r` small coins | `2 coins of 5` |
| Same value | `5+5=10` |
| Replacement | `2×5 → 1×10` |
| Count change | `2 → 1` |

Therefore an optimal solution never needs enough lower coins to form a larger available coin.

This supports largest-first greedy for this divisible denomination chain.

---

## 6.5 Dry Run — X = 256

Coins descending:

```text
100,50,10,5,1
```

### 100

```text
256 / 100 = 2
remainder = 56
```

Use:

```text
100 + 100
```

### 50

```text
56 / 50 = 1
remainder = 6
```

### 10

```text
6 / 10 = 0
```

### 5

```text
6 / 5 = 1
remainder = 1
```

### 1

```text
1 / 1 = 1
```

Final:

```text
100 + 100 + 50 + 5 + 1
```

Coins:

```text
5
```

---

## 6.6 C++

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

---

## 6.7 Complexity

If number of denominations is `M`:

```text
O(M)
```

---

## 6.8 Recognition Model

```text
minimize number of coins
+
unlimited coins
+
higher denomination exactly replaces
a fixed number of lower coins
        |
        v
largest first
```

---

# 7. Coin Change — Why Greedy Can Fail

## 7.1 Counterexample

Coins:

```text
[1,8,10]
```

Target:

```text
16
```

Greedy largest-first:

```text
10
```

Remaining:

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

Optimal:

```text
8 + 8
```

Count:

```text
2
```

---

## 7.2 Why the Previous Proof Fails

Good divisible structure had:

```text
10 = 2×5
```

But here:

```text
10 % 8 != 0
```

So a `10` cannot be exchanged cleanly with some fixed number of `8`s.

The large coin may create an expensive remainder.

Here:

```text
16 - 10 = 6
```

and `6` needs:

```text
six 1-coins
```

---

## 7.3 Recognition Model

Never memorize:

```text
coin change
→ largest first
```

Use:

```text
coin change
        |
        v
largest-first claim
        |
        v
can I prove replacement/exchange?
        |
   +----+----+
   |         |
 YES        NO
   |         |
greedy   search for
safe     counterexample / DP
```

---

# 8. Maximum Product With Fixed Sum

## 8.1 What It Asks

Find integers `A` and `B` such that:

```math
A+B=N
```

and maximize:

```math
AB
```

---

## 8.2 Concept Simplified

For a fixed sum:

```text
making the two values closer
increases the product
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

Best:

```text
most balanced split
```

---

## 8.3 Greedy Claim

```math
A=
\left\lfloor\frac N2\right\rfloor
```

```math
B=
\left\lceil\frac N2\right\rceil
```

---

## 8.4 Proof + Dry Run Side by Side

Let:

```math
L=
\left\lfloor\frac N2\right\rfloor
```

```math
R=
\left\lceil\frac N2\right\rceil
```

Greedy product:

```math
G=LR
```

Move `k` units away from balance:

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
LR-(LR+Lk-Rk-k^2)
```

Cancel `LR`:

```math
G-O
=
-Lk+Rk+k^2
```

Factor `k`:

```math
G-O
=
k(R-L)+k^2
```

So:

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

Therefore:

```math
G-O\ge0
```

So balanced split is never worse.

---

### Dry Run — N = 8

```text
L = 4
R = 4
```

Greedy:

```text
G = 4×4 = 16
```

Take:

```text
k = 1
```

Then:

```text
A = 4-1 = 3
B = 4+1 = 5
```

Other product:

```text
O = 3×5 = 15
```

Difference formula:

```text
G-O
= k² + k(R-L)
= 1² + 1(4-4)
= 1
```

Indeed:

```text
16 - 15 = 1
```

---

### Dry Run — N = 5

```text
L = 2
R = 3
```

Greedy:

```text
G = 2×3 = 6
```

Take:

```text
k = 1
```

Other pair:

```text
1 and 4
```

Product:

```text
O = 4
```

Formula:

```text
G-O
= 1² + 1(3-2)
= 2
```

Indeed:

```text
6 - 4 = 2
```

---

## 8.5 C++

```cpp
long long maximumProduct(long long n) {
    long long a = n / 2;
    long long b = n - a;

    return a * b;
}
```

---

## 8.6 Complexity

```text
O(1)
```

---

## 8.7 Recognition Model

```text
two values
+
fixed sum
+
maximize product
        |
        v
make them as equal as possible
```

---

# 9. Minimum Dot Product

## 9.1 What It Asks

Rearrange two arrays to minimize:

```math
\sum_{i=1}^{n}a_ib_i
```

---

## 9.2 Concept Simplified

To make pairwise products small:

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

## 9.3 Greedy Claim

Sort:

```math
a_1\le a_2\le\cdots\le a_n
```

and:

```math
b_1\ge b_2\ge\cdots\ge b_n
```

Pair by position.

---

## 9.4 Two-Element Proof + Dry Run

For proof, temporarily write both values in ascending order:

```math
a_1\le a_2
```

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

Subtract:

```math
X-Y
=
a_1b_1+a_2b_2-a_1b_2-a_2b_1
```

Group:

```math
X-Y
=
a_1(b_1-b_2)+a_2(b_2-b_1)
```

Since:

```math
b_2-b_1=-(b_1-b_2)
```

we get:

```math
X-Y
=
(a_1-a_2)(b_1-b_2)
```

Now:

```math
a_1-a_2\le0
```

and:

```math
b_1-b_2\le0
```

Therefore:

```math
X-Y\ge0
```

So:

```math
X\ge Y
```

Meaning:

```text
opposite pairing is no larger
```

which is exactly what we need for minimization.

---

### Side-by-Side Dry Run

Use:

```text
a1 = 2
a2 = 7

b1 = 3
b2 = 10
```

| Step | Symbolic | Dry run |
|---|---|---|
| Same order | `X=a1*b1+a2*b2` | `2×3 + 7×10 = 76` |
| Opposite | `Y=a1*b2+a2*b1` | `2×10 + 7×3 = 41` |
| Difference | `X-Y` | `76-41=35` |
| Factored | `(a1-a2)(b1-b2)` | `(2-7)(3-10)` |
| Signs | `(-)×(-)` | `(-5)×(-7)` |
| Result | `X-Y >= 0` | `35 >= 0` |

So opposite pairing is better for minimization.

---

## 9.5 Adjacent-Swap Interpretation

Suppose `A` is ascending but two values in `B` are in the wrong order for minimization.

For positions:

```text
i < j
```

we have:

```math
a_i\le a_j
```

Greedy wants:

```math
b_i\ge b_j
```

Greedy contribution:

```math
G=a_ib_i+a_jb_j
```

Wrong swapped contribution:

```math
O=a_ib_j+a_jb_i
```

Difference:

```math
G-O
=
(a_i-a_j)(b_i-b_j)
```

Now:

```text
a_i-a_j <= 0
b_i-b_j >= 0
```

Therefore:

```math
G-O\le0
```

So:

```math
G\le O
```

Thus fixing an inversion toward opposite sorting never increases the dot product.

Repeatedly fix inversions:

```text
A ascending
B descending
```

becomes optimal.

---

## 9.6 Full Example

```text
A = [-1,3,-2]
B = [-10,1,5]
```

Sort:

```text
A ascending  = [-2,-1,3]
B descending = [5,1,-10]
```

Products:

```text
-2×5   = -10
-1×1   = -1
 3×-10 = -30
```

Total:

```text
-41
```

---

## 9.7 C++

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

---

## 9.8 Complexity

```text
O(N log N)
```

---

## 9.9 Recognition Model

```text
rearrange two arrays
+
minimize pairwise-product sum
        |
        v
opposite order
```

For maximum dot product:

```text
same order
```

---

# 10. Proof Pattern Summary

Instead of memorizing each solution, remember the proof shape.

| Problem | Greedy idea | Proof pattern |
|---|---|---|
| Maximum K-sum | choose K largest | replace smaller by larger |
| Maximum difference | max - min | extremes bound every pair |
| Minimum difference | sort + adjacent | non-adjacent gap ≥ an inner adjacent gap |
| Max `Σ i*a[i]` | same sorted order | adjacent swap / remove inversions |
| Coin change | largest denomination first | replace many smaller coins by one larger coin |
| Fixed sum max product | balance values | deviation from center lowers product |
| Minimum dot product | opposite sorted order | adjacent swap / rearrangement |

---

## 10.1 Exchange / Replacement Pattern

```text
Other answer uses p
Greedy prefers q
        |
        v
replace p with q
        |
        v
compute objective change
        |
        v
show non-worse
```

---

## 10.2 Bound Pattern

```text
candidate uses internal values
        |
        v
replace one side by global extreme
        |
        v
objective can only improve
```

Used in:

```text
maximum difference
```

---

## 10.3 Adjacent-Swap Pattern

```text
find a wrong pair / inversion
        |
        v
swap the pair
        |
        v
compute G-O
        |
        v
prove swap is non-worse
        |
        v
repeat until sorted structure
```

Used in:

```text
weighted sum
minimum dot product
```

---

## 10.4 Balancing Pattern

```text
fixed total
        |
        v
compare balanced solution
with a solution k steps away
        |
        v
expand G-O
        |
        v
prove G-O >= 0
```

Used in:

```text
maximum product with fixed sum
```

---

# 11. Greedy Recognition Checklist

Before coding, ask:

```text
1. What exactly am I maximizing/minimizing?

2. What must remain feasible?

3. Can sorting expose the structure?

4. What is the natural greedy claim?

5. Can I replace another choice with mine?

6. Can I compare G and O algebraically?

7. Can I prove an inversion should be swapped?

8. Can I prove a global extreme dominates all candidates?

9. Can several small objects be replaced by one larger object?

10. Can I create a counterexample?
```

If a counterexample appears:

```text
reject the greedy rule
```

---

# 12. Compact Revision Card

```text
GREEDY
======
claim + proof

Never:
"this looks best"

Instead:
"this looks best AND I can prove it is safe"


MAX K-SUM
=========
take K largest

proof:
replace smaller selected p
with larger q

change = q-p >= 0


MAX DIFFERENCE
==============
max - min

proof:
global extremes dominate every pair


MIN DIFFERENCE
==============
sort
check adjacent gaps

proof:
non-adjacent gap
>= an adjacent gap inside it


MAX Σ i*a[i]
==============
sort ascending

small value × small index
large value × large index

G-O
=
(a_i-a_j)(i-j)
>= 0


COIN CHANGE
===========
largest first is safe only
when the denomination structure supports exchange

d_(i+1) = r*d_i

r small coins
→ 1 larger coin


COIN COUNTEREXAMPLE
===================
[1,8,10], X=16

greedy:
10 + six 1s
= 7 coins

optimal:
8 + 8
= 2 coins


FIXED SUM MAX PRODUCT
=====================
A+B=N

best:
A=floor(N/2)
B=ceil(N/2)

deviation k:

G-O
=
k² + k(R-L)
>= 0


MINIMUM DOT PRODUCT
===================
A ascending
B descending

two-value proof:

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
Optimization problem
        |
        v
Make greedy claim
        |
        v
Try to break it
        |
   +----+----+
   |         |
breaks     survives
   |         |
reject      prove
             |
             +-----------------------+
             |       |       |       |
          exchange  bound   swap   balance
             |
             v
          greedy safe
```

> **Core lesson:** the valuable skill is not recognizing that a solution sorts. It is being able to explain **why the sorted/greedy order cannot be improved by exchanging two choices**.
