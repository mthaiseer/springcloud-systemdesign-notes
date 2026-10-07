# Greedy Algorithms — Level 3
## Greedy 1 — Step-by-Step Mathematical Proof Edition

> **Goal:** understand every greedy proof from first principles.
>
> **For every problem:**  
> **what it asks → simplified idea → greedy claim → define `G` and `O` → algebra line by line → why each step is valid → numerical dry run → conclusion → C++ → complexity → recognition**
>
> **Equation convention:** after each important symbolic equation, the same line is immediately shown with the actual numbers from that problem's dry run. This lets you see exactly how the symbols map to numbers.
>
> **Important:** you do **not** need advanced algebra. Most proofs below use only:
>
> ```text
> remove brackets
> rearrange terms
> take common factor
> compare signs
> use known inequalities
> ```
>
> Display equations use fenced `math` blocks only to avoid Markdown/LaTeX rendering issues.

---

# Clickable Table of Contents

- [0. Prerequisites](#0-prerequisites)
  - [0.1 Greedy Proof Goal](#01-greedy-proof-goal)
  - [0.2 G and O](#02-g-and-o)
  - [0.3 Exchange Argument](#03-exchange-argument)
  - [0.4 Algebra Rules Used in These Notes](#04-algebra-rules-used-in-these-notes)
  - [0.5 Universal Greedy Mathematical-Proof Template](#05-universal-greedy-mathematical-proof-template)
  - [0.6 Required Notation](#06-required-notation)
- [1. Maximum Sum of K Elements](#1-maximum-sum-of-k-elements)
- [2. Maximum Difference Between Two Elements](#2-maximum-difference-between-two-elements)
- [3. Minimum Difference Between Two Elements](#3-minimum-difference-between-two-elements)
- [4. Maximize Sum of i × a[i]](#4-maximize-sum-of-i--ai)
- [5. Coin Change — When Greedy Is Safe](#5-coin-change--when-greedy-is-safe)
- [6. Coin Change — Counterexample](#6-coin-change--counterexample)
- [7. Maximum Product With Fixed Sum](#7-maximum-product-with-fixed-sum)
- [8. Minimum Dot Product](#8-minimum-dot-product)
- [9. Proof Pattern Summary](#9-proof-pattern-summary)
- [10. Recognition Checklist](#10-recognition-checklist)
- [11. Compact Revision Card](#11-compact-revision-card)

---

# 0. Prerequisites

## 0.1 Greedy Proof Goal

A greedy solution is:

```text
CLAIM
+
PROOF
```

Example claim:

```text
Choose the K largest values.
```

The proof must answer:

```text
Why can an optimal solution
be changed to use this greedy choice
without becoming worse?
```

Visual:

```text
Greedy choice G
      |
      v
Competing / OPT choice O
      |
      v
Compare them mathematically
      |
      v
Greedy non-worse?
   /       \
 YES        NO
  |          |
safe       claim fails
```

---

## 0.2 G and O

We use:

```text
G = greedy answer / greedy local contribution
O = other answer / competing contribution
```

For a **maximization** problem:

```math
G\ge O
```

A convenient proof is:

```math
G-O\ge0
```

For a **minimization** problem:

```math
G\le O
```

A convenient proof is:

```math
G-O\le0
```

### Tiny Example

```text
G = 10
O = 7
```

Then:

```math
G-O
=
10-7
=
3
```

Since:

```math
3\ge0
```

we know:

```math
G\ge O
```

---

## 0.3 Exchange Argument

### Concept Simplified

```text
Greedy wants q
OPT uses p
      |
      v
replace p by q
      |
      v
still legal?
      |
     YES
      |
      v
answer non-worse?
      |
     YES
      |
      v
OPT can be changed
to include q
```

To use an exchange argument:

```text
1. Find one place where OPT differs from greedy.
2. Change only that place.
3. Prove constraints still hold.
4. Prove objective does not get worse.
5. Repeat if needed.
```

---

## 0.4 Algebra Rules Used in These Notes

These are enough for almost every proof in this file.

### Rule 1 — Remove Brackets

```text
A - (B + C)
= A - B - C
```

Example:

```text
20 - (7 + 3)
= 20 - 7 - 3
= 10
```

---

### Rule 2 — Rearrange Terms

Addition can be reordered:

```text
ax + by - ay - bx
```

can become:

```text
ax - ay + by - bx
```

We are only moving terms, not changing them.

---

### Rule 3 — Factor a Common Term

```text
ax - ay
```

Both terms contain `a`.

So:

```text
ax - ay
= a(x-y)
```

Reverse check:

```text
a(x-y)
= ax-ay
```

---

### Rule 4 — Reverse a Difference

```text
y-x
=
-(x-y)
```

Example:

```text
3-1 = 2
1-3 = -2

therefore:
3-1 = -(1-3)
```

---

### Rule 5 — Cancel Equal Terms

```text
A+B-A
= B
```

Example:

```text
18 - 7 + 10 - 18
= 10 - 7
```

because:

```text
+18 and -18 cancel
```

---

### Rule 6 — Sign Reasoning

```text
positive × positive = positive
negative × negative = positive
positive × negative = negative
```

Also:

```math
k^2\ge0
```

for every `k`.

---

### Rule 7 — Use Known Ordering

If:

```math
a\le b
```

then:

```math
a-b\le0
```

and:

```math
b-a\ge0
```

Example:

```text
6 <= 10

6-10 = -4 <= 0
10-6 = 4 >= 0
```

---

## 0.5 Universal Greedy Mathematical-Proof Template

For every greedy problem:

```text
STEP 1
State exactly what greedy chooses.

STEP 2
Construct one competing choice.

STEP 3
Define:
G = greedy objective value
O = competing objective value

STEP 4
Write:
G - O

STEP 5
Remove brackets.

STEP 6
Rearrange / factor / cancel.

STEP 7
Use known inequalities.

STEP 8
Find the sign.

For maximization:
G-O >= 0

For minimization:
G-O <= 0

STEP 9
Check feasibility did not break.

STEP 10
Repeat the exchange if needed.
```

---

## 0.6 Required Notation

### Sorted Array

```math
a_1\le a_2\le\cdots\le a_n
```

means:

```text
a1 = smallest
an = largest
```

---

### Floor and Ceiling

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

2×5 + 3×7
= 31
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

---

# 1. Maximum Sum of K Elements

## 1.1 What It Asks

Choose exactly `K` elements and maximize:

```math
\sum_{i\in S}a_i
```

with:

```math
|S|=K
```

---

## 1.2 Concept Simplified

If the chosen set contains a smaller value `p` while a larger value `q` is outside:

```text
replace p by q
```

The sum cannot become smaller.

So:

```text
take the K largest elements
```

---

## 1.3 Greedy Claim

After sorting:

```math
a_1\le a_2\le\cdots\le a_n
```

select the final `K` elements.

---

## 1.4 Mathematical Proof — Every Step Explained

Suppose:

```text
O = sum of another valid K-element selection
```

That selection contains:

```text
p
```

while a larger value:

```text
q
```

is unselected.

We know:

```math
q\ge p
```

After exchanging:

```text
remove p
add q
```

the new sum is:

```math
G=O-p+q
```

Actual example:

```text
G = 18 - 7 + 10
  = 21
```

### Step 1 — Subtract the old answer

```math
G-O
=
(O-p+q)-O
```

Actual example:

```text
21 - 18
=
(18 - 7 + 10) - 18
```

**Why?**

We want to know:

```text
how much better/worse is G than O?
```

So calculate:

```text
new - old
```

---

### Step 2 — Remove the bracket

```math
G-O
=
O-p+q-O
```

Actual example:

```text
21 - 18
=
18 - 7 + 10 - 18
```

**Why?**

Subtracting `O` means:

```text
-O
```

is added to the expression.

---

### Step 3 — Cancel equal terms

```math
G-O
=
q-p
```

Actual example:

```text
21 - 18
=
10 - 7

3 = 3
```

because:

```text
+O and -O cancel
```

---

### Step 4 — Use the known inequality

We know:

```math
q\ge p
```

Therefore:

```math
q-p\ge0
```

Actual example:

```text
10 - 7
= 3
>= 0
```

So:

```math
G-O\ge0
```

Hence:

```math
G\ge O
```

The exchange cannot hurt a maximization answer.

---

## 1.5 Numerical Dry Run

Suppose:

```text
O = 18
p = 7
q = 10
```

Exchange:

```text
7 → 10
```

New answer:

```text
G
= 18 - 7 + 10
= 21
```

Difference:

```text
G-O
= 21-18
= 3
```

Formula:

```text
q-p
= 10-7
= 3
```

Same result.

---

## 1.6 Conclusion

```text
If a smaller chosen value exists
while a larger unchosen value exists,
exchange them.

Repeat.

Eventually the chosen set
is exactly the K largest elements.
```

---

## 1.7 C++

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

# 2. Maximum Difference Between Two Elements

## 2.1 What It Asks

Maximize:

```math
a_{\text{high}}-a_{\text{low}}
```

with no original-index ordering restriction.

---

## 2.2 Concept Simplified

For:

```text
high - low
```

make:

```text
high = global maximum
low  = global minimum
```

---

## 2.3 Greedy Claim

Let:

```text
M = global maximum
m = global minimum
```

Greedy:

```math
G=M-m
```

Actual example:

```text
G = 16 - 4
  = 12
```

Take any competing pair:

```text
x = candidate high
y = candidate low
```

Other answer:

```math
O=x-y
```

Actual example:

```text
O = 15 - 8
  = 7
```

---

## 2.4 Mathematical Proof — Every Step Explained

Because `M` is global maximum:

```math
M\ge x
```

Because `m` is global minimum:

```math
m\le y
```

Now compare:

```math
G-O
=
(M-m)-(x-y)
```

Actual example:

```text
12 - 7
=
(16 - 4) - (15 - 8)
```

---

### Step 1 — Remove the second bracket

```math
G-O
=
M-m-x+y
```

Actual example:

```text
5
=
16 - 4 - 15 + 8
```

**Why?**

```text
-(x-y)
= -x+y
```

The minus changes both signs.

---

### Step 2 — Rearrange into useful differences

```math
G-O
=
(M-x)+(y-m)
```

Actual example:

```text
5
=
(16 - 15) + (8 - 4)

= 1 + 4
= 5
```

**How?**

Start:

```text
M - m - x + y
```

Move terms:

```text
M - x + y - m
```

Group:

```text
(M-x) + (y-m)
```

---

### Step 3 — Determine signs

Since:

```math
M\ge x
```

we have:

```math
M-x\ge0
```

Actual example:

```text
16 - 15
= 1
>= 0
```

Since:

```math
y\ge m
```

we have:

```math
y-m\ge0
```

Actual example:

```text
8 - 4
= 4
>= 0
```

So:

```math
(M-x)+(y-m)\ge0
```

Therefore:

```math
G-O\ge0
```

Hence:

```math
G\ge O
```

---

## 2.5 Numerical Dry Run

Suppose:

```text
M = 16
m = 4

x = 15
y = 8
```

Greedy:

```text
G
= 16-4
= 12
```

Other:

```text
O
= 15-8
= 7
```

Difference:

```text
G-O
= 12-7
= 5
```

Derived formula:

```text
(M-x)+(y-m)

= (16-15)+(8-4)

= 1+4

= 5
```

Same result.

---

## 2.6 Conclusion

```text
global maximum - global minimum
is at least as large as every other pair difference
```

---

## 2.7 C++

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
→ max - min
```

---

# 3. Minimum Difference Between Two Elements

## 3.1 What It Asks

Minimize:

```math
|a_i-a_j|
```

for two distinct elements.

---

## 3.2 Concept Simplified

After sorting:

```text
closest values must appear next to each other
```

So only adjacent differences need to be checked.

---

## 3.3 Greedy Claim

Sort:

```math
a_1\le a_2\le\cdots\le a_n
```

The answer is:

```text
minimum adjacent gap
```

---

## 3.4 Mathematical Proof — Every Step Explained

Choose any non-adjacent pair:

```text
a_j and a_i
```

where:

```text
j < i-1
```

Because the array is sorted:

```math
a_j\le a_{i-1}\le a_i
```

Define:

```text
O = non-adjacent gap
G = adjacent gap ending at a_i
```

So:

```math
O=a_i-a_j
```

Actual example:

```text
O = 10 - 3
  = 7
```

and:

```math
G=a_i-a_{i-1}
```

Actual example:

```text
G = 10 - 9
  = 1
```

For a minimization proof, we want:

```text
G <= O
```

Equivalent:

```text
O-G >= 0
```

Compute:

```math
O-G
=
(a_i-a_j)-(a_i-a_{i-1})
```

Actual example:

```text
7 - 1
=
(10 - 3) - (10 - 9)
```

---

### Step 1 — Remove the second bracket

```math
O-G
=
a_i-a_j-a_i+a_{i-1}
```

Actual example:

```text
6
=
10 - 3 - 10 + 9
```

**Why?**

```text
-(a_i-a_(i-1))
=
-a_i+a_(i-1)
```

---

### Step 2 — Cancel equal terms

```math
O-G
=
a_{i-1}-a_j
```

Actual example:

```text
6
=
9 - 3
```

because:

```text
+a_i and -a_i cancel
```

---

### Step 3 — Use sorted order

We know:

```math
a_{i-1}\ge a_j
```

Therefore:

```math
a_{i-1}-a_j\ge0
```

So:

```math
O-G\ge0
```

Hence:

```math
O\ge G
```

Therefore the non-adjacent pair cannot beat this adjacent pair.

---

## 3.5 Numerical Dry Run

Sorted:

```text
[3,9,10]
```

Choose non-adjacent:

```text
3 and 10
```

```text
O
= 10-3
= 7
```

Adjacent pair:

```text
9 and 10
```

```text
G
= 10-9
= 1
```

Difference:

```text
O-G
= 7-1
= 6
```

Formula:

```text
a_(i-1)-a_j
= 9-3
= 6
```

Same result.

---

## 3.6 Conclusion

```text
Every non-adjacent gap
is at least as large as
an adjacent gap inside it.

So an optimal minimum pair is adjacent.
```

---

## 3.7 C++

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
→ sort + adjacent gaps
```

---

# 4. Maximize Sum of i × a[i]

## 4.1 What It Asks

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

## 4.2 Concept Simplified

Use simpler names first:

```text
small value = a
large value = b

small weight = x
large weight = y
```

with:

```text
a <= b
x <= y
```

Greedy says:

```text
small value × small weight
+
large value × large weight
```

---

## 4.3 Greedy Claim

Greedy pairing:

```math
G=ax+by
```

Actual example:

```text
G
= 6×1 + 10×3
= 36
```

Swapped pairing:

```math
O=ay+bx
```

Actual example:

```text
O
= 6×3 + 10×1
= 28
```

We want to prove:

```math
G\ge O
```

---

## 4.4 Mathematical Proof — Every Step Explained

Start:

```math
G-O
=
(ax+by)-(ay+bx)
```

Actual example:

```text
36 - 28
=
(6×1 + 10×3) - (6×3 + 10×1)
```

---

### Step 1 — Remove the minus bracket

```math
G-O
=
ax+by-ay-bx
```

Actual example:

```text
8
=
6×1 + 10×3 - 6×3 - 10×1
```

**Why?**

The minus in front of:

```text
(ay+bx)
```

changes both signs:

```text
-ay-bx
```

---

### Step 2 — Put similar terms together

```math
G-O
=
ax-ay+by-bx
```

Actual example:

```text
8
=
6×1 - 6×3 + 10×3 - 10×1
```

**Why?**

We only rearranged addition/subtraction so the `a` terms and `b` terms are together.

---

### Step 3 — Factor `a` and `b`

```math
G-O
=
a(x-y)+b(y-x)
```

Actual example:

```text
8
=
6(1-3) + 10(3-1)

= 6(-2) + 10(2)

= -12 + 20
= 8
```

Because:

```text
ax-ay = a(x-y)
by-bx = b(y-x)
```

---

### Step 4 — Make both brackets use the same difference

We know:

```math
y-x=-(x-y)
```

So:

```math
b(y-x)
=
-b(x-y)
```

Therefore:

```math
G-O
=
a(x-y)-b(x-y)
```

Actual example:

```text
8
=
6(1-3) - 10(1-3)

= 6(-2) - 10(-2)

= -12 + 20
= 8
```

---

### Step 5 — Factor the common `(x-y)`

```math
G-O
=
(a-b)(x-y)
```

Actual example:

```text
8
=
(6-10)(1-3)

= (-4)(-2)

= 8
```

because:

```text
a(x-y)-b(x-y)
=
(a-b)(x-y)
```

---

### Step 6 — Determine signs

Since:

```text
a <= b
```

we have:

```math
a-b\le0
```

Since:

```text
x <= y
```

we have:

```math
x-y\le0
```

Therefore:

```text
negative × negative
=
non-negative
```

So:

```math
G-O\ge0
```

Hence:

```math
G\ge O
```

Greedy is no worse.

---

## 4.5 Numerical Dry Run

Use:

```text
a = 6
b = 10
x = 1
y = 3
```

Greedy:

```text
G
= 6×1 + 10×3
= 6 + 30
= 36
```

Swapped:

```text
O
= 6×3 + 10×1
= 18 + 10
= 28
```

Difference:

```text
G-O
= 36-28
= 8
```

Factor formula:

```text
(a-b)(x-y)

= (6-10)(1-3)

= (-4)(-2)

= 8
```

Same result.

---

## 4.6 Map Back to the Original Problem

Original weights are:

```text
i < j
```

Values are:

```math
a_i\le a_j
```

So identify:

```text
a = a_i
b = a_j
x = i
y = j
```

Therefore:

```math
G-O
=
(a_i-a_j)(i-j)
```

Both factors are non-positive, so:

```math
G-O\ge0
```

Thus ascending values matched with ascending indices maximize the weighted sum.

---

## 4.7 C++

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
→ same ordering
```

---

# 5. Coin Change — When Greedy Is Safe

## 5.1 What It Asks

Coins:

```text
1,5,10,50,100
```

Unlimited copies.

Minimize number of coins needed to make `X`.

---

## 5.2 Concept Simplified

Suppose:

```math
d_{i+1}=r\,d_i
```

Actual example:

```text
10 = 2×5

so:
d_(i+1) = 10
d_i     = 5
r       = 2
```

Then:

```text
r small coins
```

have exactly the same value as:

```text
1 larger coin
```

but use more coins.

---

## 5.3 Greedy Claim

Use as many large-denomination coins as possible.

---

## 5.4 Mathematical Proof — Every Step Explained

Value of `r` smaller coins:

```math
r\,d_i
```

But:

```math
d_{i+1}=r\,d_i
```

Actual example:

```text
10 = 2×5

so:
d_(i+1) = 10
d_i     = 5
r       = 2
```

Therefore:

```text
r smaller coins
and
1 larger coin
have equal MONEY VALUE
```

So feasibility is preserved.

Now compare **coin count**.

Other solution:

```math
O=r
```

Actual example:

```text
O = 2

(two coins of 5)
```

Greedy:

```math
G=1
```

Actual example:

```text
G = 1

(one coin of 10)
```

For minimization, calculate:

```math
G-O
=
1-r
```

Actual example:

```text
G-O
= 1-2
= -1
```

Since:

```text
r >= 2
```

subtracting `r` from `1` gives:

```math
1-r\le -1
```

Therefore:

```math
G-O\le0
```

Hence:

```math
G\le O
```

So using one larger coin is no worse and usually strictly better.

---

## 5.5 Numerical Dry Run

For:

```text
5 and 10
```

we have:

```text
10 = 2×5
```

So:

```text
r = 2
```

Other:

```text
O = 2 coins
```

Greedy:

```text
G = 1 coin
```

Difference:

```text
G-O
= 1-2
= -1
```

Since:

```text
-1 <= 0
```

greedy is better for minimization.

---

## 5.6 Full Amount Dry Run

```text
X = 256
```

```text
256 / 100 = 2, remainder 56
56  / 50  = 1, remainder 6
6   / 10  = 0
6   / 5   = 1, remainder 1
1   / 1   = 1
```

Answer:

```text
100 + 100 + 50 + 5 + 1

5 coins
```

---

## 5.7 C++

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
larger denomination exactly replaces
multiple lower coins
→ largest first
```

---

# 6. Coin Change — Counterexample

## 6.1 What It Shows

Largest coin first is **not** always correct.

Coins:

```text
[1,8,10]
```

Target:

```text
16
```

---

## 6.2 Compare Greedy and Optimal Mathematically

Greedy:

```text
10 + six 1s
```

Coin count:

```math
G=7
```

Optimal:

```text
8 + 8
```

Coin count:

```math
O=2
```

For minimization, greedy would need:

```math
G-O\le0
```

But:

```math
G-O
=
7-2
=
5
```

So:

```math
G-O>0
```

Therefore:

```math
G>O
```

Greedy is worse.

---

## 6.3 Why the Previous Proof Cannot Be Used

With `5` and `10`:

```text
10 = 2×5
```

Clean replacement exists.

But:

```text
10 % 8 != 0
```

There is no integer `r` such that:

```text
10 = r×8
```

So the replacement proof breaks.

Choosing `10` also creates bad remainder:

```text
16-10 = 6
```

which requires six `1`s.

---

## 6.4 Recognition

```text
coin change
      |
      v
largest-first claim
      |
      v
can I prove clean replacement?
   /       \
 YES        NO
  |          |
safe       test counterexample /
           use DP or another method
```

---

# 7. Maximum Product With Fixed Sum

## 7.1 What It Asks

Find integers `A,B` such that:

```math
A+B=N
```

and maximize:

```math
AB
```

---

## 7.2 Concept Simplified

Fixed sum:

```text
closer numbers
→ larger product
```

So split `N` as evenly as possible.

---

## 7.3 Greedy Claim

Define:

```math
L=\left\lfloor\frac N2\right\rfloor
```

and:

```math
R=\left\lceil\frac N2\right\rceil
```

Greedy:

```math
G=LR
```

Actual example for `N=8`:

```text
L = 4
R = 4

G
= 4×4
= 16
```

Any more unbalanced pair can be written:

```math
A=L-k
```

```math
B=R+k
```

for some:

```text
k >= 0
```

---

## 7.4 Mathematical Proof — Every Step Explained

Other product:

```math
O=(L-k)(R+k)
```

Actual example:

```text
L = 4
R = 4
k = 1

O
= (4-1)(4+1)
= 3×5
= 15
```

---

### Step 1 — Expand the brackets

Use:

```text
(A-B)(C+D)
= AC + AD - BC - BD
```

So:

```math
O
=
LR+Lk-Rk-k^2
```

Actual example:

```text
15
=
4×4 + 4×1 - 4×1 - 1²

= 16 + 4 - 4 - 1

= 15
```

Why?

```text
L×R   = LR
L×k   = Lk
(-k)×R = -Rk
(-k)×k = -k²
```

---

### Step 2 — Compare greedy and other

```math
G-O
=
LR-(LR+Lk-Rk-k^2)
```

Actual example:

```text
16 - 15
=
16 - (16 + 4 - 4 - 1)
```

---

### Step 3 — Remove the minus bracket

```math
G-O
=
LR-LR-Lk+Rk+k^2
```

Actual example:

```text
1
=
16 - 16 - 4 + 4 + 1
```

The minus changes every sign inside the bracket.

---

### Step 4 — Cancel equal terms

```math
G-O
=
-Lk+Rk+k^2
```

Actual example:

```text
1
=
-4×1 + 4×1 + 1²

= -4 + 4 + 1
= 1
```

because:

```text
+LR and -LR cancel
```

---

### Step 5 — Factor `k`

```math
G-O
=
k(R-L)+k^2
```

Actual example:

```text
1
=
1(4-4) + 1²

= 0 + 1

= 1
```

because:

```text
-Lk+Rk
= k(-L+R)
= k(R-L)
```

---

### Step 6 — Determine signs

We know:

```text
k >= 0
```

Also:

```text
R-L is either 0 or 1
```

Therefore:

```math
k(R-L)\ge0
```

and:

```math
k^2\ge0
```

So:

```math
G-O\ge0
```

Hence:

```math
G\ge O
```

Balanced split is optimal.

---

## 7.5 Numerical Dry Run — Even N

```text
N = 8
L = 4
R = 4
```

Greedy:

```text
G
= 4×4
= 16
```

Move:

```text
k = 1
```

Other pair:

```text
3 and 5
```

```text
O
= 3×5
= 15
```

Formula:

```text
G-O
= k(R-L)+k²

= 1(4-4)+1²

= 0+1

= 1
```

Check:

```text
16-15 = 1
```

---

## 7.6 Numerical Dry Run — Odd N

```text
N = 5
L = 2
R = 3
```

Greedy:

```text
G
= 2×3
= 6
```

Take:

```text
k = 1
```

Other:

```text
1 and 4
```

```text
O
= 1×4
= 4
```

Formula:

```text
G-O
= 1(3-2)+1²

= 1+1

= 2
```

Check:

```text
6-4 = 2
```

---

## 7.7 C++

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

# 8. Minimum Dot Product

## 8.1 What It Asks

Rearrange arrays `A` and `B` to minimize:

```math
\sum_{i=1}^{n}a_ib_i
```

---

## 8.2 Concept Simplified

Use simple names:

```text
small A value = a
large A value = b

small B value = x
large B value = y
```

with:

```text
a <= b
x <= y
```

For minimum dot product:

```text
small ↔ large
large ↔ small
```

---

## 8.3 Greedy Claim

Same-order pairing:

```math
X=ax+by
```

Actual example:

```text
X
= 2×3 + 7×10
= 6 + 70
= 76
```

Opposite-order pairing:

```math
Y=ay+bx
```

Actual example:

```text
Y
= 2×10 + 7×3
= 20 + 21
= 41
```

We want to prove:

```math
Y\le X
```

Equivalent:

```math
X-Y\ge0
```

---

## 8.4 Mathematical Proof — Every Step Explained

Start:

```math
X-Y
=
(ax+by)-(ay+bx)
```

Actual example:

```text
76 - 41
=
(2×3 + 7×10) - (2×10 + 7×3)
```

---

### Step 1 — Remove the minus bracket

```math
X-Y
=
ax+by-ay-bx
```

Actual example:

```text
35
=
2×3 + 7×10 - 2×10 - 7×3
```

Why?

```text
-(ay+bx)
=
-ay-bx
```

---

### Step 2 — Group similar terms

```math
X-Y
=
ax-ay+by-bx
```

Actual example:

```text
35
=
2×3 - 2×10 + 7×10 - 7×3
```

Group:

```text
a terms together
b terms together
```

---

### Step 3 — Factor `a` and `b`

```math
X-Y
=
a(x-y)+b(y-x)
```

Actual example:

```text
35
=
2(3-10) + 7(10-3)

= 2(-7) + 7(7)

= -14 + 49

= 35
```

because:

```text
ax-ay = a(x-y)

by-bx = b(y-x)
```

---

### Step 4 — Rewrite `y-x`

```math
y-x=-(x-y)
```

Therefore:

```math
b(y-x)
=
-b(x-y)
```

So:

```math
X-Y
=
a(x-y)-b(x-y)
```

Actual example:

```text
35
=
2(3-10) - 7(3-10)

= 2(-7) - 7(-7)

= -14 + 49

= 35
```

---

### Step 5 — Factor the common bracket

```math
X-Y
=
(a-b)(x-y)
```

Actual example:

```text
35
=
(2-7)(3-10)

= (-5)(-7)

= 35
```

because:

```text
a(x-y)-b(x-y)
=
(a-b)(x-y)
```

---

### Step 6 — Determine signs

Since:

```text
a <= b
```

```math
a-b\le0
```

Since:

```text
x <= y
```

```math
x-y\le0
```

Therefore:

```text
negative × negative
=
non-negative
```

So:

```math
X-Y\ge0
```

Hence:

```math
X\ge Y
```

Therefore:

```text
opposite pairing Y
is no larger than
same-order pairing X
```

So opposite order is better for minimization.

---

## 8.5 Numerical Dry Run

Use:

```text
a = 2
b = 7
x = 3
y = 10
```

Same order:

```text
X
= 2×3 + 7×10
= 6 + 70
= 76
```

Opposite order:

```text
Y
= 2×10 + 7×3
= 20 + 21
= 41
```

Difference:

```text
X-Y
= 76-41
= 35
```

Factor formula:

```text
(a-b)(x-y)

= (2-7)(3-10)

= (-5)(-7)

= 35
```

Same result.

---

## 8.6 Why the Two-Element Proof Proves the Whole Array

Suppose:

```text
A is ascending
```

but two corresponding values in `B` are also ascending.

Then that pair is in the wrong direction for minimization.

Swap those two `B` values.

The proof above says:

```text
dot product cannot increase
```

Repeat every such swap.

Eventually:

```text
A ascending
B descending
```

So opposite sorting is optimal.

---

## 8.7 Full Example

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

## 8.8 C++

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

# 9. Proof Pattern Summary

| Problem | Greedy Claim | Proof Comparison | Main Algebra |
|---|---|---|---|
| Maximum K-sum | choose K largest | new sum vs old sum | `G-O=q-p` |
| Maximum difference | max - min | global extremes vs candidate | `G-O=(M-x)+(y-m)` |
| Minimum difference | adjacent after sort | adjacent vs non-adjacent | `O-G=a_(i-1)-a_j` |
| Max weighted sum | same ordering | same vs swapped pair | `G-O=(a-b)(x-y)` |
| Coin change | use larger equivalent coin | 1 large vs `r` small | `G-O=1-r` |
| Coin failure | largest first fails | greedy vs optimum | `G-O=7-2=5` |
| Fixed-sum product | balance values | balanced vs `k` away | `G-O=k(R-L)+k²` |
| Minimum dot product | opposite ordering | same vs opposite pair | `X-Y=(a-b)(x-y)` |

---

## 9.1 What the Algebra Is Really Doing

Do not memorize long equations.

Each proof asks only:

```text
What changes
if I replace the competing local choice
with the greedy local choice?
```

Then compute:

```text
new - old
```

or:

```text
old - new
```

whichever gives an easy sign.

---

# 10. Recognition Checklist

For a new greedy problem:

```text
1. What exactly am I optimizing?

2. What must remain feasible?

3. What is my greedy claim?

4. What is the smallest possible disagreement
   between greedy and another solution?

5. Define G and O for only that local disagreement.

6. Write G-O.

7. Remove brackets carefully.

8. Group similar terms.

9. Factor common terms.

10. Use known inequalities.

11. Is the final sign correct?
    max → G-O >= 0
    min → G-O <= 0

12. Does the exchange preserve feasibility?

13. Can this local exchange be repeated?

14. Can I find a counterexample?
```

---

# 11. Compact Revision Card

```text
GREEDY MATH PROOF
=================
G = greedy local value
O = competing local value

max:
prove G-O >= 0

min:
prove G-O <= 0


ALGEBRA
=======
1. remove brackets
2. rearrange
3. cancel
4. factor
5. inspect signs


MAX K-SUM
=========
G = O-p+q

G-O
= q-p
>= 0


MAX DIFFERENCE
==============
G = M-m
O = x-y

G-O
= (M-x)+(y-m)
>= 0


MIN DIFFERENCE
==============
O = a_i-a_j
G = a_i-a_(i-1)

O-G
= a_(i-1)-a_j
>= 0

therefore:
G <= O


MAX WEIGHTED SUM
================
G = ax+by
O = ay+bx

G-O
= (a-b)(x-y)
>= 0


COIN CHANGE
===========
d_next = r*d

G = 1
O = r

G-O
= 1-r
<= 0


COIN FAILURE
============
coins [1,8,10]
X=16

G=7
O=2

G-O=5>0

greedy fails


FIXED SUM MAX PRODUCT
=====================
G = LR
O = (L-k)(R+k)

G-O
= k(R-L)+k²
>= 0


MIN DOT PRODUCT
===============
same:
X=ax+by

opposite:
Y=ay+bx

X-Y
= (a-b)(x-y)
>= 0

therefore:
Y <= X
```

---

# Final Mental Model

```text
                GREEDY CLAIM
                     |
                     v
             competing choice
                     |
                     v
               define G / O
                     |
                     v
                write G-O
                     |
                     v
       remove brackets / group / factor
                     |
                     v
                inspect sign
                     |
            +--------+--------+
            |                 |
       correct sign        wrong sign
            |                 |
            v                 v
   check feasibility       claim fails
            |
            v
      exchange is safe
            |
            v
       repeat if needed
```

> **Core lesson:** most algebraic greedy proofs are not about doing complicated mathematics. They are about **measuring exactly what changes when one local choice is replaced by another**.
