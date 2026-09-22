# Part 15 — Prefix Mathematics

> **Goal:** convert repeated work over the beginning of an array into a mathematical state that can be reused.
>
> **Core transformation:** `Repeated range work → cumulative prefix state → subtract/cancel prefixes → O(1) or near-O(1) queries`
>
> **Recognition question:** **Can the answer for `[L,R]` be obtained from information about `[0,R]` and `[0,L-1]`?**

## Table of Contents

- [15.0 Prefix Mathematics Mental Model](#150-prefix-mathematics-mental-model)
- [15.1 Prefix Sum Definition](#151-prefix-sum-definition)
- [15.2 Range Sum by Prefix Cancellation](#152-range-sum-by-prefix-cancellation)
- [15.3 Why Use a Prefix Array of Size n+1](#153-why-use-a-prefix-array-of-size-n1)
- [15.4 Prefix as State Accumulation](#154-prefix-as-state-accumulation)
- [15.5 Prefix Count and Frequency](#155-prefix-count-and-frequency)
- [15.6 Prefix XOR](#156-prefix-xor)
- [15.7 Prefix Product and Its Limitations](#157-prefix-product-and-its-limitations)
- [15.8 Prefix Min and Max](#158-prefix-min-and-max)
- [15.9 Prefix and Suffix Decomposition](#159-prefix-and-suffix-decomposition)
- [15.10 Prefix Difference and Balance](#1510-prefix-difference-and-balance)
- [15.11 Prefix Sum plus Hash Map](#1511-prefix-sum-plus-hash-map)
- [15.12 Subarray Sum Equals K](#1512-subarray-sum-equals-k)
- [15.13 Prefix Modulo](#1513-prefix-modulo)
- [15.14 Equal Prefix Remainders](#1514-equal-prefix-remainders)
- [15.15 Prefix Parity](#1515-prefix-parity)
- [15.16 Prefix Contribution and Weighted Prefix](#1516-prefix-contribution-and-weighted-prefix)
- [15.17 Two-Dimensional Prefix Sum](#1517-two-dimensional-prefix-sum)
- [15.18 Difference Array as Reverse Prefix Thinking](#1518-difference-array-as-reverse-prefix-thinking)
- [15.19 Prefix of Transformed Values](#1519-prefix-of-transformed-values)
- [15.20 When Prefix Mathematics Is Not Enough](#1520-when-prefix-mathematics-is-not-enough)
- [15.21 60-Second Discovery Workflow](#1521-60-second-discovery-workflow)
- [15.22 Codeforces Recognition Map](#1522-codeforces-recognition-map)
- [15.23 Common Mistakes](#1523-common-mistakes)
- [15.24 Fast Revision Card](#1524-fast-revision-card)

---

## 15.0 Prefix Mathematics Mental Model

A prefix is everything from the beginning up to some boundary.

```text
array:
a0  a1  a2  a3  a4  a5
|-----------|
   prefix
```

Instead of recomputing:

```text
a0+a1+...+ai
```

again and again, store it.

### Real-world example — bank balance

Transactions:

```text
+100, -20, +50, -10
```

Starting from `0`:

```text
after transaction 1: 100
after transaction 2:  80
after transaction 3: 130
after transaction 4: 120
```

Prefix state:

```text
0 -> 100 -> 80 -> 130 -> 120
```

### Mathematical model

```text
pref[i+1] = pref[i] + a[i]
```

Therefore:

```text
pref[i]
= a[0] + a[1] + ... + a[i-1]
```

### Core idea

```text
REPEATED HISTORY
      ↓
STORE CUMULATIVE STATE
      ↓
REUSE IT
```

---

## 15.1 Prefix Sum Definition

For:

```text
a = [3,1,4,2]
```

define:

```text
pref[0] = 0
pref[1] = 3
pref[2] = 3+1     = 4
pref[3] = 3+1+4   = 8
pref[4] = 3+1+4+2 = 10
```

So:

```text
pref = [0,3,4,8,10]
```

### Step-by-step table

| i | a[i] | pref[i] | pref[i+1] |
|---:|---:|---:|---:|
| 0 | 3 | 0 | 3 |
| 1 | 1 | 3 | 4 |
| 2 | 4 | 4 | 8 |
| 3 | 2 | 8 | 10 |

### Real-world example — monthly savings

Savings:

```text
Jan 300
Feb 100
Mar 400
Apr 200
```

Cumulative savings:

```text
before Jan = 0
after Jan  = 300
after Feb  = 400
after Mar  = 800
after Apr  = 1000
```

Exactly the same recurrence:

```text
new cumulative
=
old cumulative + current month
```

---

## 15.2 Range Sum by Prefix Cancellation

For 0-based inclusive range `[L,R]`:

```text
sum(L,R)
=
pref[R+1] - pref[L]
```

### Why?

```text
pref[R+1]
= a[0]+a[1]+...+a[L-1]+a[L]+...+a[R]

pref[L]
= a[0]+a[1]+...+a[L-1]
```

Subtract:

```text
pref[R+1] - pref[L]

= (a0+...+a[L-1]+a[L]+...+a[R])
  -(a0+...+a[L-1])

= a[L]+...+a[R]
```

Everything before `L` cancels.

### Visual dry run

```text
a = [-2,0,3,-5,2,-1]

pref = [0,-2,-2,1,-4,-2,-3]
```

Query:

```text
L=2
R=5
```

Then:

```text
pref[R+1] - pref[L]
= pref[6] - pref[2]
= -3 - (-2)
= -1
```

Direct:

```text
3 + (-5) + 2 + (-1)
= -1
```

### Real-world example — electricity usage between March and May

Cumulative meter consumption:

```text
before Jan = 0
after Jan  = 100
after Feb  = 230
after Mar  = 350
after Apr  = 500
after May  = 620
```

Consumption March-May:

```text
meter after May - meter before March
= 620 - 230
= 390
```

This is exactly prefix cancellation.

---

## 15.3 Why Use a Prefix Array of Size n+1

Preferred definition:

```text
pref[0]=0
pref[i+1]=pref[i]+a[i]
```

### Why the extra zero?

It makes every range use one formula:

```text
sum(L,R)=pref[R+1]-pref[L]
```

Even:

```text
L=0
```

works:

```text
sum(0,R)
= pref[R+1]-pref[0]
= pref[R+1]
```

### Visual

```text
a index:      0    1    2    3
a:            3    1    4    2

pref index: 0    1    2    3    4
pref:       0    3    4    8   10
            ^
      empty prefix
```

### Memory hook

```text
pref[i] = information about first i elements
```

not necessarily information "at index i".

This definition removes many off-by-one special cases.

---

## 15.4 Prefix as State Accumulation

Prefix mathematics is broader than sums.

General recurrence:

```text
state[i+1]
=
combine(state[i], a[i])
```

Examples:

```text
sum   -> +
xor   -> XOR
count -> + condition
max   -> max(...)
min   -> min(...)
```

### Real-world example — running number of customers

Arrivals per hour:

```text
2,5,1,4
```

Cumulative customers:

```text
0
0+2 = 2
2+5 = 7
7+1 = 8
8+4 = 12
```

Prefix state:

```text
[0,2,7,8,12]
```

### CP mindset

Do not memorize only "prefix sum".

Ask:

```text
"What cumulative information about the first i elements
would make future questions easy?"
```

---

## 15.5 Prefix Count and Frequency

Suppose we want number of even values in ranges.

Transform:

```text
b[i] = 1 if a[i] is even
       0 otherwise
```

Then prefix-sum `b`.

### Example

```text
a = [5,2,8,3,6]

even indicator:
b = [0,1,1,0,1]
```

Prefix:

```text
pref = [0,0,1,2,2,3]
```

Number of evens in `[1,4]`:

```text
pref[5]-pref[1]
=3-0
=3
```

### Real-world example — attendance

```text
P A P P A P
```

Map:

```text
P -> 1
A -> 0
```

Then:

```text
1,0,1,1,0,1
```

Prefix count answers:

```text
"How many days was the employee present
between day L and day R?"
```

### Pattern

```text
property
   ↓ convert to 0/1
prefix sum
   ↓
range count
```

---

## 15.6 Prefix XOR

Define:

```text
px[0]=0
px[i+1]=px[i] XOR a[i]
```

Because:

```text
x XOR x = 0
0 XOR y = y
```

range XOR becomes:

```text
xor(L,R)
=
px[R+1] XOR px[L]
```

### Step-by-step example

```text
a = [5,2,7,3]
```

Prefix XOR:

```text
px[0] = 0
px[1] = 0 XOR 5 = 5
px[2] = 5 XOR 2 = 7
px[3] = 7 XOR 7 = 0
px[4] = 0 XOR 3 = 3
```

So:

```text
px = [0,5,7,0,3]
```

Query `[1,3]`:

```text
2 XOR 7 XOR 3
```

Using prefixes:

```text
px[4] XOR px[1]
= 3 XOR 5
= 6
```

Direct:

```text
2 XOR 7 XOR 3
=5 XOR 3
=6
```

### Why cancellation works

```text
(prefix before L) XOR
(prefix before L) = 0
```

This is the XOR version of prefix subtraction.

---

## 15.7 Prefix Product and Its Limitations

You may define:

```text
prod[i+1]=prod[i]*a[i]
```

and hope:

```text
range product
= prod[R+1]/prod[L]
```

This works only when division is valid.

### Real-world numerical example

```text
a = [2,3,5,7]
prod = [1,2,6,30,210]
```

Product `[1,3]`:

```text
210 / 2 = 105
```

Direct:

```text
3*5*7=105
```

### But zero breaks division

```text
a=[2,0,5]
prod=[1,2,0,0]
```

Trying to recover a range may require:

```text
0/0
```

which is invalid.

### Modular warning

Under modulo `M`:

```text
A/B mod M
```

requires a modular inverse of `B`, and that inverse may not exist.

### Lesson

Prefix cancellation needs an operation with a valid inverse/cancellation mechanism.

```text
sum -> subtraction
XOR -> XOR itself
product -> division only under suitable conditions
min/max -> generally no inverse
```

---

## 15.8 Prefix Min and Max

Prefix maximum:

```text
prefMax[i]
=
maximum among first i+1 elements
```

Recurrence:

```text
prefMax[i]
=
max(prefMax[i-1], a[i])
```

### Real-world example — hottest temperature so far

```text
temperatures:
18,23,20,27,24
```

Prefix max:

```text
18
max(18,23)=23
max(23,20)=23
max(23,27)=27
max(27,24)=27
```

Result:

```text
[18,23,23,27,27]
```

### Important limitation

Unlike sums:

```text
rangeMax(L,R)
```

cannot generally be recovered from:

```text
prefMax[R] and prefMax[L-1]
```

because `max` has no subtraction-like inverse.

Example:

```text
a = [100,2,3]
prefMax = [100,100,100]
```

Range `[1,2]` has max `3`, but both relevant prefix maxima are `100`.

---

## 15.9 Prefix and Suffix Decomposition

Sometimes split at index `i`.

Build:

```text
prefix information from left
suffix information from right
```

### Real-world example — split workload between two teams

Daily jobs:

```text
[3,1,4,2]
```

Prefix sums:

```text
[3,4,8,10]
```

Total:

```text
10
```

Split after index `1`:

```text
left  = 3+1 = 4
right = 4+2 = 6
```

Difference:

```text
|4-6|=2
```

### Mathematical shortcut

If:

```text
left = prefix
total = S
```

then:

```text
right = S-left
```

Difference:

```text
|left-right|
= |left-(S-left)|
= |2*left-S|
```

### CF recognition

```text
split array
left vs right
prefix + suffix
minimum partition difference
```

---

## 15.10 Prefix Difference and Balance

Map opposing categories to `+1` and `-1`.

### Real-world example — income vs expense events

Suppose:

```text
income  -> +1
expense -> -1
```

Events:

```text
I E I I E
```

Transform:

```text
+1,-1,+1,+1,-1
```

Prefix balance:

```text
0,1,0,1,2,1
```

Interpretation:

```text
balance = incomes - expenses
```

If prefix balance returns to the same previous value, the segment between them has equal numbers of incomes and expenses.

### Mathematical model

If:

```text
pref[j]=pref[i]
```

then:

```text
pref[j]-pref[i]=0
```

so the segment contribution is balanced.

This is a powerful transformation for binary/two-category problems.

---

## 15.11 Prefix Sum plus Hash Map

A subarray sum can be expressed as difference of two prefixes.

Let:

```text
P[j] = sum of first j elements
```

For subarray `[i,j-1]`:

```text
sum = P[j]-P[i]
```

Suppose desired sum is `K`:

```text
P[j]-P[i]=K
```

Rearrange:

```text
P[i]=P[j]-K
```

### Recognition transformation

Instead of asking:

```text
"Which subarray sums to K?"
```

ask:

```text
"Have I previously seen prefix = currentPrefix-K?"
```

This converts a subarray problem into a lookup problem.

---

## 15.12 Subarray Sum Equals K

Example:

```text
a = [1,2,1,2]
K = 3
```

Start:

```text
prefix=0
freq[0]=1
answer=0
```

Process `1`:

```text
prefix=1
need=1-3=-2
not seen
freq[1]++
```

Process `2`:

```text
prefix=3
need=3-3=0
freq[0]=1

answer += 1
```

Process `1`:

```text
prefix=4
need=4-3=1
freq[1]=1

answer += 1
```

Process `2`:

```text
prefix=6
need=6-3=3
freq[3]=1

answer += 1
```

Final:

```text
answer=3
```

Subarrays:

```text
[1,2] at indices 0..1
[2,1] at indices 1..2
[1,2] at indices 2..3
```

### Real-world analogy — net transactions

If cumulative balance is currently `600` and you want an interval whose net change is `200`, search for an earlier cumulative balance:

```text
600-200=400
```

If an earlier prefix was `400`, the transactions between that point and now sum to `200`.

---

## 15.13 Prefix Modulo

For divisibility by `m`, track:

```text
prefixSum mod m
```

because:

```text
(A-B) mod m
```

depends only on the remainders of `A` and `B`.

### Example

```text
a = [3,1,2,7]
m = 3
```

Prefix sums:

```text
0,3,4,6,13
```

Prefix remainders:

```text
0,0,1,0,1
```

The state has been compressed from potentially huge sums into only:

```text
0..m-1
```

### Real-world example — weekly cycle

If only day-of-week matters, absolute day count can be reduced modulo `7`.

```text
day 100
100 mod 7 = 2
```

The quotient is irrelevant to the cyclic state.

---

## 15.14 Equal Prefix Remainders

A subarray sum is divisible by `m` if two prefix sums have the same remainder.

Suppose:

```text
P[j] mod m = P[i] mod m
```

Then:

```text
P[j] = q1*m + r
P[i] = q2*m + r
```

Subtract:

```text
P[j]-P[i]
= (q1-q2)*m
```

Therefore divisible by `m`.

### Step-by-step example

```text
a = [3,1,2,7]
m=3

prefix sums:       0,3,4,6,13
prefix remainders: 0,0,1,0,1
```

Remainder `0` occurs three times.

Choose any two such prefixes:

```text
C(3,2)=3
```

Each pair defines a subarray whose sum is divisible by `3`.

Remainder `1` occurs twice:

```text
C(2,2)=1
```

Total divisible subarrays:

```text
3+1=4
```

### Frequency connection

```text
answer
= Σ C(freq[remainder],2)
```

Prefix mathematics + modulo + frequency modeling combine here.

---

## 15.15 Prefix Parity

Sometimes only even/odd sum matters.

Track:

```text
prefixSum mod 2
```

Possible states:

```text
0 = even
1 = odd
```

### Key identities

Subarray sum is even when prefix parities are equal:

```text
even-even = even
odd-odd   = even
```

Subarray sum is odd when prefix parities differ:

```text
even-odd = odd
odd-even = odd
```

### Real-world example

```text
a = [1,2,3]
```

Prefix sums:

```text
0,1,3,6
```

Parities:

```text
E,O,O,E
```

Even-prefix count:

```text
2
```

Odd-prefix count:

```text
2
```

Even-sum subarrays:

```text
C(2,2)+C(2,2)
=1+1
=2
```

Odd-sum subarrays:

```text
2*2=4
```

Total:

```text
2+4=6=C(4,2)
```

---

## 15.16 Prefix Contribution and Weighted Prefix

Sometimes we need:

```text
Σ i*a[i]
```

or repeated formulas involving both values and indices.

Build:

```text
prefValue[i+1]
= prefValue[i] + a[i]

prefWeighted[i+1]
= prefWeighted[i] + i*a[i]
```

### Real-world example — shipping cost by distance

Packages:

```text
weight = [2,3,5]
distance index = [0,1,2]
```

Weighted cost:

```text
0*2 + 1*3 + 2*5
=0+3+10
=13
```

Prefix weighted:

```text
[0,0,3,13]
```

Now weighted range contributions can also be obtained by prefix subtraction.

### Recognition

If a formula repeatedly contains:

```text
a[i]
i*a[i]
i²*a[i]
```

consider maintaining corresponding prefix aggregates.

---

## 15.17 Two-Dimensional Prefix Sum

For a matrix, prefix can accumulate a rectangle.

Define:

```text
pref[r+1][c+1]
=
sum of rectangle
(0,0) ... (r,c)
```

Recurrence:

```text
pref[r+1][c+1]
=
matrix[r][c]
+ pref[r][c+1]
+ pref[r+1][c]
- pref[r][c]
```

Why subtract?

```text
top prefix + left prefix
```

counts their common top-left region twice.

### Visual

```text
+-------------------+
| overlap |   top   |
|---------+---------|
|  left   | current |
+-------------------+
```

### Rectangle query

For rows `[r1,r2]`, columns `[c1,c2]`:

```text
sum
=
pref[r2+1][c2+1]
- pref[r1][c2+1]
- pref[r2+1][c1]
+ pref[r1][c1]
```

### Real-world example — sales grid

```text
1 2 3
4 5 6
7 8 9
```

Query bottom-right 2x2:

```text
5 6
8 9
```

Direct:

```text
5+6+8+9=28
```

Inclusion-exclusion with 2D prefix gives the same `28`.

---

## 15.18 Difference Array as Reverse Prefix Thinking

Prefix converts:

```text
individual values -> cumulative state
```

Difference array converts:

```text
range update -> boundary changes
```

For adding `x` to inclusive `[L,R]`:

```text
diff[L] += x
diff[R+1] -= x
```

Then recover final values using prefix sum.

### Real-world example — salary bonus

Base:

```text
[100,100,100,100,100]
```

Give employees `1..3` (0-based) a bonus `+20`.

Boundary updates:

```text
diff[1] += 20
diff[4] -= 20
```

Change array:

```text
[0,20,0,0,-20]
```

Prefix changes:

```text
0,20,20,20,0
```

Add to base:

```text
100,120,120,120,100
```

### Mathematical intuition

```text
+20 starts at L
-20 stops its effect after R
```

---

## 15.19 Prefix of Transformed Values

You do not have to prefix the original array.

First transform each element into the quantity the problem actually asks about.

### Example — count negatives

```text
a = [4,-2,7,-5,-1]
```

Transform:

```text
negative? -> [0,1,0,1,1]
```

Prefix:

```text
[0,0,1,1,2,3]
```

Negatives in `[1,4]`:

```text
pref[5]-pref[1]
=3-0
=3
```

### Other useful transformations

```text
is even?           -> 0/1
a[i] > X?          -> 0/1
a[i] == target?    -> 0/1
sign(a[i])         -> -1/0/+1
a[i]^2             -> squared-value prefix
i*a[i]             -> weighted prefix
```

### Contest question

```text
"What should each element contribute
to the cumulative state?"
```

This is often more important than the prefix technique itself.

---

## 15.20 When Prefix Mathematics Is Not Enough

Prefix sums are ideal when:

```text
array is static
operation is cumulative
queries need range aggregates
```

They are less suitable when values change frequently.

### Example

Suppose:

```text
a=[1,2,3,4]
pref=[0,1,3,6,10]
```

Update:

```text
a[0] = 100
```

Now every later prefix changes:

```text
pref[1], pref[2], pref[3], pref[4]
```

A plain prefix array requires `O(n)` repair.

### Better structures for dynamic data

Depending on the operation:

```text
Fenwick tree
segment tree
```

### Also remember

Not every operation has prefix cancellation:

```text
sum -> yes
XOR -> yes
min/max -> no general inverse
```

---

## 15.21 60-Second Discovery Workflow

```text
PROBLEM
   |
   v
Repeated range / cumulative question?
   |
 +---+---+
NO      YES
         |
         v
What state should prefix store?
   /      |       |       \
 sum    count     XOR    transformed
  |       |        |         |
  +-------+--------+---------+
              |
              v
Can range answer be obtained
by cancelling two prefixes?
              |
          +---+---+
         YES      NO
          |        |
     prefix query  maybe prefix
                   min/max, suffix,
                   Fenwick/segment tree,
                   or another model
```

### Fast questions

```text
1. Are there many range queries?
2. Can I express [L,R] as prefix(R)-prefix(L-1)?
3. What does pref[i] mean exactly?
4. Can I transform values to 0/1 first?
5. Is the useful state sum, XOR, count, parity, modulo?
6. Does equality of two prefix states imply a useful subarray?
7. Do I need a hash map of previous prefixes?
8. Are updates present?
```

---

## 15.22 Codeforces Recognition Map

| Statement clue | Prefix model |
|---|---|
| many static range sums | prefix sum |
| range count of property | transform to 0/1 + prefix |
| range XOR | prefix XOR |
| left vs right split | prefix + total/suffix |
| subarray sum = K | prefix + hash map |
| divisible subarray | prefix modulo + frequency |
| equal number of two categories | map `+1/-1` + equal prefixes |
| even/odd subarray sums | prefix parity |
| repeated weighted range formula | weighted prefixes |
| rectangle sum queries | 2D prefix |
| many range additions, final array | difference array + prefix |
| count condition in range | prefix of transformed values |

---

## 15.23 Common Mistakes

### 1. Off-by-one errors

Use:

```text
pref[0]=0
pref[i+1]=pref[i]+a[i]
```

Then always:

```text
sum(L,R)=pref[R+1]-pref[L]
```

### 2. Forgetting the empty prefix

For hash-map prefix problems:

```text
freq[0]=1
```

represents a valid subarray starting at index `0`.

### 3. Using int for large sums

Use:

```text
long long
```

when `n * max|a[i]|` can exceed 32-bit range.

### 4. Assuming every operation can be subtracted

Prefix max/min do not have a general inverse.

### 5. Negative modulo in C++

Normalize when needed:

```text
r = ((x % m) + m) % m
```

### 6. Mixing prefix conventions

Do not switch midway between:

```text
pref[i] = sum through i
```

and:

```text
pref[i] = sum of first i elements
```

Prefer the `n+1` convention.

### 7. Forgetting updates

Static prefix arrays are not efficient for frequent point updates.

---

## 15.24 Fast Revision Card

```text
========================================================
PART 15 — PREFIX MATHEMATICS
========================================================

CORE
pref[i] = information about first i elements

SUM
pref[0]=0
pref[i+1]=pref[i]+a[i]

RANGE [L,R]
pref[R+1]-pref[L]

WHY n+1?
empty prefix at pref[0]
one formula for every range

COUNT PROPERTY
transform condition -> 0/1
then prefix sum

XOR
px[i+1]=px[i] XOR a[i]
range XOR = px[R+1] XOR px[L]

PREFIX MAX/MIN
good for "so far"
cannot generally subtract prefixes

SPLIT
left = prefix
right = total-prefix

SUBARRAY SUM K
P[j]-P[i]=K
P[i]=P[j]-K

PREFIX + HASH
look for currentPrefix-K

MODULO
same prefix remainder
-> difference divisible by m

PARITY
same parity -> even-sum segment
different parity -> odd-sum segment

2D PREFIX
rectangle
= whole - top - left + overlap

DIFFERENCE ARRAY
diff[L]+=x
diff[R+1]-=x
prefix diff to recover values

TRANSFORM FIRST
condition/value contribution
        ↓
prefix transformed array

SAFETY
static data? prefix is excellent
many updates? consider Fenwick/segment tree

CORE QUESTION
"Can I answer [L,R] by cancelling
two cumulative states?"
========================================================
```
