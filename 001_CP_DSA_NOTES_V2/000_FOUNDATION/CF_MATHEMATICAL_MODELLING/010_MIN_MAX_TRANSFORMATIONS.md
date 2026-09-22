# Part 9 — Min/Max Mathematical Transformations

> **Goal:** turn story phrases such as *at least*, *at most*, *overlap*, *clamp*, *closest boundary*, and *best/worst possible* into short `min` / `max` formulas you can recognize quickly in Codeforces.
>
> **Contest workflow:** `Story → variables → competing bounds → min/max formula → proof → O(1) or simple scan`

## Table of Contents

- [9.0 Min/Max Mental Model](#90-minmax-mental-model)
- [9.1 Clamp a Value into a Range](#91-clamp-a-value-into-a-range)
- [9.2 Interval Intersection and Overlap Length](#92-interval-intersection-and-overlap-length)
- [9.3 Distance from a Point to an Interval](#93-distance-from-a-point-to-an-interval)
- [9.4 Maximum and Minimum Possible Sum](#94-maximum-and-minimum-possible-sum)
- [9.5 Bounding a Quantity from Multiple Constraints](#95-bounding-a-quantity-from-multiple-constraints)
- [9.6 Pairwise Min/Max Identities](#96-pairwise-minmax-identities)
- [9.7 Absolute Difference through Min/Max](#97-absolute-difference-through-minmax)
- [9.8 Common Cancellation Identities](#98-common-cancellation-identities)
- [9.9 Bottleneck Modeling](#99-bottleneck-modeling)
- [9.10 Maximum Feasible / Minimum Required](#910-maximum-feasible--minimum-required)
- [9.11 Minimize the Maximum](#911-minimize-the-maximum)
- [9.12 Maximize the Minimum](#912-maximize-the-minimum)
- [9.13 Prefix/Suffix Min-Max Transformation](#913-prefixsuffix-min-max-transformation)
- [9.14 Contribution of Min/Max over Pairs](#914-contribution-of-minmax-over-pairs)
- [9.15 Contest Recognition Cheat Sheet](#915-contest-recognition-cheat-sheet)
- [9.16 Common Mistakes](#916-common-mistakes)
- [9.17 Fast Revision Card](#917-fast-revision-card)

---

## 9.0 Min/Max Mental Model

### Plain-English intuition

`min` answers:

> **Which competing upper limit stops me first?**

`max` answers:

> **Which competing lower requirement forces me highest?**

```text
UPPER BOUNDS                         LOWER BOUNDS

x <= A                              x >= A
x <= B                              x >= B
x <= C                              x >= C
   │                                   │
   ▼                                   ▼
x <= min(A,B,C)                     x >= max(A,B,C)
```

### Mathematical model

If every constraint must hold:

```text
x <= u1
x <= u2
...
x <= uk

strongest upper bound = min(u1,u2,...,uk)
```

Similarly:

```text
x >= l1
x >= l2
...
x >= lk

strongest lower bound = max(l1,l2,...,lk)
```

### Real-world model — API throughput

A request pipeline can process at most:

```text
gateway     = 1200 req/s
application = 1000 req/s
database    =  700 req/s

end-to-end throughput
= min(1200, 1000, 700)
= 700 req/s
```

The slowest stage is the bottleneck.

### Codeforces trigger words

| Statement language | Think |
|---|---|
| cannot exceed A or B | `min(A,B)` |
| must be at least A and B | `max(A,B)` |
| common part / overlap | `max(left)`, `min(right)` |
| keep inside `[L,R]` | clamp |
| weakest resource | `min(...)` |
| strongest requirement | `max(...)` |
| minimize worst case | `min(max(...))` |
| maximize guaranteed value | `max(min(...))` |

### Daily-life scenario — Road speed limits
Your car supports `130 km/h`, the road allows `100`, and traffic safely allows `80`.
```text
speed <= 130
speed <= 100
speed <= 80
=> speed <= min(130,100,80) = 80
```
**Step-by-step:** all three upper limits must hold → the smallest ceiling is binding → `80 km/h`.

**Memory hook:** many ceilings → `min`; many minimum requirements → `max`.

---

## 9.1 Clamp a Value into a Range

### Problem form

Given value `x`, force it into `[L, R]`.

### Derivation

Three cases exist:

```text
x < L        L <= x <= R        x > R
 │                │               │
 ▼                ▼               ▼
answer=L       answer=x        answer=R
```

First enforce the lower bound:

```text
max(x, L)
```

Then enforce the upper bound:

```text
min(max(x,L), R)
```

Therefore:

```text
clamp(x,L,R) = min(max(x,L),R)
```

Equivalent:

```text
max(L, min(x,R))
```

### Visual dry run

```text
range:      10 ---------------- 20

x = 7
max(7,10) = 10
min(10,20) = 10

x = 16
max(16,10) = 16
min(16,20) = 16

x = 27
max(27,10) = 27
min(27,20) = 20
```

### Real world — rate-limit configuration

Allowed worker count is `[2, 16]`.

```text
requested = 25
actual = min(max(25,2),16)
       = 16
```

### CF recognition

Use when the statement says:

```text
"value cannot go below L"
AND
"value cannot go above R"
```

### Daily-life scenario — Thermostat
A hotel thermostat permits only `[18°C, 26°C]`. A guest requests `30°C`.
```text
L=18, R=26, x=30

Step 1: max(x,L) = max(30,18) = 30
Step 2: min(30,R) = min(30,26) = 26

clamp(30,18,26) = 26
```
For other requests:
```text
15 -> max(15,18)=18 -> min(18,26)=18
22 -> max(22,18)=22 -> min(22,26)=22
30 -> max(30,18)=30 -> min(30,26)=26

             allowed range
        18 ---------------- 26
15 -----|         22         |----- 30
        ↑                     ↑
      push up              push down
```
**Memory hook:** clamp = push up to the floor, then push down to the ceiling.

---

## 9.2 Interval Intersection and Overlap Length

Two closed intervals:

```text
A = [L1, R1]
B = [L2, R2]
```

For a point to belong to both intervals, it must start after **both** left boundaries:

```text
intersectionLeft = max(L1,L2)
```

and finish before **both** right boundaries:

```text
intersectionRight = min(R1,R2)
```

So:

```text
intersection = [max(L1,L2), min(R1,R2)]
```

For continuous length:

```text
overlap = max(0, min(R1,R2) - max(L1,L2))
```

For number of integer points in inclusive integer intervals:

```text
count = max(0, min(R1,R2) - max(L1,L2) + 1)
```

### Visual dry run

```text
A:  2 ----------- 8
B:        5 ------------ 11
          ^       ^
          5       8

left  = max(2,5)  = 5
right = min(8,11) = 8

continuous overlap = 8 - 5 = 3
integer points     = 8 - 5 + 1 = 4   -> {5,6,7,8}
```

### Why the outer `max(0, ...)`?

Disjoint intervals:

```text
A: 2 --- 4
B:          7 --- 9

min(R1,R2) - max(L1,L2)
= 4 - 7
= -3
```

Length cannot be negative, so:

```text
max(0,-3) = 0
```

### Real world — meeting availability

```text
Engineer A: 09:00–12:00
Engineer B: 10:30–13:00

common start = max(09:00,10:30) = 10:30
common end   = min(12:00,13:00) = 12:00
```

### Daily-life scenario — Two people finding meeting time
```text
Alice: 09:00 -------- 12:00
Bob:          10:30 -------- 13:00
```
Step-by-step:
```text
common start = max(09:00,10:30) = 10:30
common end   = min(12:00,13:00) = 12:00
overlap      = 12:00 - 10:30 = 90 minutes
```
Why? Both must have started being free → choose the **later start**. Both must still be free → choose the **earlier end**.

**Memory hook:** intersection = later start + earlier finish.

---

## 9.3 Distance from a Point to an Interval

Given point `x` and interval `[L,R]`.

```text
x < L       L <= x <= R       x > R
distance     distance           distance
L-x          0                  x-R
```

Compact formula:

```text
distance = max(L-x, 0, x-R)
```

Why?

Inside the interval:

```text
L-x <= 0
x-R <= 0

max(negative, 0, negative) = 0
```

Left of interval:

```text
L-x > 0
x-R < 0

max(L-x,0,x-R) = L-x
```

Right is symmetric.

### Example

```text
[L,R] = [10,20]

x=6  -> max(4,0,-14) = 4
x=15 -> max(-5,0,-5) = 0
x=27 -> max(-17,0,7) = 7
```

### Real world — service region

A server supports shard IDs `[100,199]`.  
Shard `207` is `8` positions outside the supported range.

### Daily-life scenario — Parking zone
Parking is legal between positions `[100,200]`.
```text
x=230:
max(100-230, 0, 230-200)
= max(-130,0,30)
= 30

x=150:
max(-50,0,-50) = 0

x=70:
max(30,0,-130) = 30
```
Step-by-step: measure the left deficit and right deficit → include `0` because distance cannot be negative → take the positive one.

**Memory hook:** left deficit, zero, right deficit; at most one deficit is positive.

---

## 9.4 Maximum and Minimum Possible Sum

Suppose each of `n` independent values satisfies:

```text
L <= xi <= R
```

Then:

```text
minimum total = nL
maximum total = nR
```

More generally:

```text
Li <= xi <= Ri

ΣLi <= Σxi <= ΣRi
```

### Visual model

```text
x1 ∈ [L1,R1]
x2 ∈ [L2,R2]
x3 ∈ [L3,R3]

smallest possible sum:
L1 + L2 + L3

largest possible sum:
R1 + R2 + R3
```

### Feasibility test

If a target sum is `S`:

```text
ΣLi <= S <= ΣRi
```

is necessary. When each variable can take every integer in its interval independently, it is also sufficient.

### Example

Three servers may allocate:

```text
x1 ∈ [2,5]
x2 ∈ [1,4]
x3 ∈ [3,7]

minimum = 2+1+3 = 6
maximum = 5+4+7 = 16
```

Target `12` is within the feasible range.

### Daily-life scenario — Grocery budget
```text
Vegetables: [20,40]
Meat:       [30,60]
Milk/eggs:  [10,25]
```
Step-by-step:
```text
minimum bill = 20+30+10 = 60
maximum bill = 40+60+25 = 125

possible total: [60,125]
```
A target of `100` is inside the range. A target of `50` is impossible because even all minimum choices total `60`.

**Memory hook:** minimum total = sum of minimums; maximum total = sum of maximums.

---

## 9.5 Bounding a Quantity from Multiple Constraints

Suppose:

```text
x >= 4
x >= 9
x >= 6
```

All must hold, therefore:

```text
x >= max(4,9,6)
x >= 9
```

Likewise:

```text
x <= 20
x <= 13
x <= 18

x <= min(20,13,18)
x <= 13
```

Together:

```text
lower = max(all lower bounds)
upper = min(all upper bounds)

feasible iff lower <= upper
```

### Example

```text
x >= 4
x >= 7
x <= 12
x <= 10

lower = max(4,7)   = 7
upper = min(12,10) = 10

feasible x ∈ [7,10]
```

### CF recognition

Many problems that look like several `if` statements reduce to maintaining:

```cpp
lo = max(lo, newLowerBound);
hi = min(hi, newUpperBound);
```

### Daily-life scenario — Ride height restrictions
```text
height >= 120
height >= 130
height <= 190
height <= 185
```
Step-by-step:
```text
lower = max(120,130) = 130
upper = min(190,185) = 185

valid range = [130,185]
```
All minimum requirements must hold, so keep the strongest lower bound. All ceilings must hold, so keep the tightest upper bound.

**Memory hook:** constraints collapse to `[max(all lower), min(all upper)]`.

---

## 9.6 Pairwise Min/Max Identities

For any real `a,b`:

```text
min(a,b) + max(a,b) = a + b
```

### Proof by cases

If `a <= b`:

```text
min(a,b) = a
max(a,b) = b

min + max = a+b
```

If `b < a`, the roles swap, but the sum remains `a+b`.

Therefore:

```text
min(a,b) = a+b-max(a,b)
max(a,b) = a+b-min(a,b)
```

### Example

```text
a=7, b=12

min=7
max=12

7+12 = 19 = a+b
```

### Why useful in CP?

If one extremum is expensive or already known, the other may be recovered from the total.

### Daily-life scenario — Two shopping bags
Two bags weigh `7 kg` and `12 kg`.
```text
lighter = min(7,12) = 7
heavier = max(7,12) = 12

lighter + heavier = 7+12 = 19
original total    = 7+12 = 19
```
`min` and `max` only reorder the same two values. If total=`19` and max=`12`, then min=`19-12=7`.

**Memory hook:** min + max preserves the original total.

---

## 9.7 Absolute Difference through Min/Max

For any `a,b`:

```text
|a-b| = max(a,b) - min(a,b)
```

### Proof

If `a >= b`:

```text
max=a
min=b
max-min = a-b = |a-b|
```

If `b > a`:

```text
max=b
min=a
max-min = b-a = |a-b|
```

### Equivalent identities

From:

```text
min + max = a+b
max - min = |a-b|
```

Add the equations:

```text
2max = a+b+|a-b|

max(a,b) = (a+b+|a-b|)/2
```

Subtract:

```text
2min = a+b-|a-b|

min(a,b) = (a+b-|a-b|)/2
```

### Example

```text
a=14, b=6

max = (14+6+8)/2 = 14
min = (14+6-8)/2 = 6
```

### Daily-life scenario — Age gap
Ages are `46` and `38`.
```text
older   = max(46,38) = 46
younger = min(46,38) = 38
gap     = 46-38 = 8

|max-min| is unnecessary because max >= min.
|46-38| = max(46,38)-min(46,38) = 8
```
**Memory hook:** absolute difference = larger − smaller.

---

## 9.8 Common Cancellation Identities

These appear in algebraic simplification.

### Identity 1

```text
max(a,b) - a = max(0,b-a)
```

Case `a>=b`:

```text
LHS = a-a = 0
RHS = max(0,b-a) = 0
```

Case `b>a`:

```text
LHS = b-a
RHS = b-a
```

### Identity 2

```text
a - min(a,b) = max(0,a-b)
```

### Identity 3

```text
min(a,b) = a - max(0,a-b)
```

### Identity 4

```text
max(a,b) = a + max(0,b-a)
```

### Intuition

`max(0, difference)` means:

> pay/add only the **positive deficit**.

### Real world — autoscaling

Current workers `a=6`; required workers `b=10`.

```text
additional workers
= max(0,b-a)
= max(0,10-6)
= 4
```

If current workers were `12`, additional workers would be `0`.

### Daily-life scenario — Prepaid balance top-up
You need `100 lei` and currently have `65 lei`.
```text
shortage = 100-65 = 35
topUp = max(0,35) = 35
```
If you already have `120`:
```text
100-120 = -20
topUp = max(0,-20) = 0
```
Step-by-step: compute `need-have` → a top-up cannot be negative → clamp the deficit at zero.

**Memory hook:** `max(0, need-have)` = pay only the positive shortage.

---

## 9.9 Bottleneck Modeling

When an output requires several resources simultaneously:

```text
units <= resourceA / needA
units <= resourceB / needB
units <= resourceC / needC
```

Therefore:

```text
units <= min(
    resourceA/needA,
    resourceB/needB,
    resourceC/needC
)
```

Maximum complete units:

```text
min(resourceA/needA,
    resourceB/needB,
    resourceC/needC)
```

### Example — deployment replicas

Each replica needs:

```text
2 CPU
4 GB RAM
10 GB disk
```

Cluster has:

```text
20 CPU  -> 20/2  = 10 replicas
36 GB   -> 36/4  = 9 replicas
200 GB  -> 200/10 = 20 replicas
```

So:

```text
max replicas = min(10,9,20) = 9
```

RAM is the bottleneck.

### CF trigger

```text
"each item needs..."
"how many complete groups?"
"limited by..."
```

Think **minimum of capacities**.

### Daily-life scenario — Making sandwiches
Each sandwich needs `2` bread slices, `1` cheese slice, and `3` tomato slices.
```text
20 bread  / 2 = 10 sandwiches
7 cheese  / 1 =  7 sandwiches
30 tomato / 3 = 10 sandwiches

complete sandwiches = min(10,7,10) = 7
```
After seven, cheese is exhausted even though other ingredients remain.

**Memory hook:** a complete product is limited by the resource that runs out first.

---

## 9.10 Maximum Feasible / Minimum Required

This distinction is extremely useful.

### Maximum feasible

You are trying to make `x` as large as possible while satisfying upper bounds:

```text
x <= A
x <= B
x <= C

largest possible x = min(A,B,C)
```

### Minimum required

You are trying to make `x` as small as possible while satisfying lower bounds:

```text
x >= A
x >= B
x >= C

smallest possible x = max(A,B,C)
```

### Memory rule

```text
MAX feasible  -> MIN of upper bounds
MIN required  -> MAX of lower bounds
```

This apparent reversal is a common CF modeling trick.

### Daily-life scenario — Suitcase and delivery rules
Suitcase upper limits:
```text
airline <= 23 kg
bag     <= 30 kg
you     <= 25 kg

maximum feasible = min(23,30,25) = 23 kg
```
Delivery lower requirements:
```text
customer >= 10 boxes
contract >= 12 boxes
economics >= 8 boxes

minimum required = max(10,12,8) = 12 boxes
```
**Memory hook:** maximum allowed → smallest ceiling; minimum required → largest floor.

---

## 9.11 Minimize the Maximum

General form:

```text
minimize  max(cost1, cost2, ..., costk)
```

You care about the **worst** component, then choose a decision that makes that worst component as small as possible.

### Example — split work

`N=10` tasks are split between two workers:

```text
worker A gets x
worker B gets 10-x

completion time = max(x, 10-x)
```

Try values:

```text
x=2 -> max(2,8)=8
x=4 -> max(4,6)=6
x=5 -> max(5,5)=5   <- best
x=7 -> max(7,3)=7
```

Thus balanced work minimizes the maximum load.

### Mathematical observation

For two nonnegative parts with fixed sum:

```text
x + y = S
```

the maximum is minimized when the parts are as equal as possible:

```text
min max(x,y) = ceil(S/2)
```

For integers:

```text
x = floor(S/2)
y = ceil(S/2)
```

### CF recognition

```text
minimize largest...
minimum possible maximum...
split/partition fairly...
```

### Daily-life scenario — Sharing dishes
Two people split `11` plates. If A washes `x`, B washes `11-x`.
```text
A=2, B=9 -> max=9
A=4, B=7 -> max=7
A=5, B=6 -> max=6  <- best
A=6, B=5 -> max=6  <- best
A=8, B=3 -> max=8
```
Step 1: `max(A,B)` measures the worse workload. Step 2: choose the split minimizing that value.
```text
min max(x,11-x) = 6 = ceil(11/2)
```
**Memory hook:** identify the worst case with `max`, then minimize it.

---

## 9.12 Maximize the Minimum

General form:

```text
maximize min(value1, value2, ..., valuek)
```

You are maximizing the guaranteed weakest value.

### Example — equal distribution

Distribute `20` units among `4` machines.

If the objective is:

```text
maximize the minimum allocation
```

then the best guaranteed amount is:

```text
floor(20/4) = 5
```

Why can it not be `6`?

```text
4 machines × 6 = 24 > 20
```

So `6` is impossible, while `5` is constructible.

### Proof pattern

This is a standard:

```text
upper bound + construction
```

1. Prove answer cannot exceed a bound.
2. Show a construction that reaches it.
3. Therefore the bound is optimal.

### CF recognition

```text
maximize minimum...
largest guaranteed...
make the smallest as large as possible...
```

### Daily-life scenario — Sharing chocolates fairly
You have `23` chocolates for `5` children.
```text
Can everyone get 5?
5*5 = 25 > 23 -> impossible

Can everyone get 4?
5*4 = 20 <= 23 -> possible
```
Therefore:
```text
maximum guaranteed minimum
= floor(23/5)
= 4
```
**Memory hook:** make the weakest share as large as feasibility allows.

---

## 9.13 Prefix/Suffix Min-Max Transformation

Sometimes each position needs information from everything before or after it.

Given:

```text
a = [8, 3, 6, 2, 7]
```

Prefix minimum:

```text
prefMin[i] = min(prefMin[i-1], a[i])

index       0  1  2  3  4
a           8  3  6  2  7
prefMin     8  3  3  2  2
```

Prefix maximum:

```text
prefMax[i] = max(prefMax[i-1], a[i])

prefMax     8  8  8  8  8
```

Suffix values are computed symmetrically from right to left.

### Why useful?

A repeated query such as:

```text
minimum before i
maximum after i
```

should not rescan the array each time.

Transform:

```text
O(n) per position -> O(n) preprocessing + O(1) lookup
```

### Common pattern

Check whether:

```text
a[i] > maximum of everything left
AND
a[i] < minimum of everything right
```

Precompute:

```text
prefMax
suffMin
```

### Daily-life scenario — Hottest temperature so far
```text
day          1  2  3  4  5
temperature 18 23 20 27 24
```
Build the answer once:
```text
prefMax[1] = 18
prefMax[2] = max(18,23) = 23
prefMax[3] = max(23,20) = 23
prefMax[4] = max(23,27) = 27
prefMax[5] = max(27,24) = 27

prefMax     = 18 23 23 27 27
```
Now "hottest up to day `i`" is an O(1) lookup instead of rescanning.

**Memory hook:** prefix min/max = extreme seen **so far**; suffix = extreme seen **from here onward**.

---

## 9.14 Contribution of Min/Max over Pairs

For sorted array:

```text
a0 <= a1 <= ... <= a(n-1)
```

Consider the sum of pairwise maximums over all pairs `i<j`.

For `a[j]` to be the maximum, choose any earlier index:

```text
number of choices = j
contribution of a[j] = a[j] * j
```

Therefore:

```text
Σ max(ai,aj) over i<j
=
Σ a[j] * j
```

with zero-based indexing.

For pairwise minimums, `a[i]` is the minimum with every later element:

```text
number of choices = n-i-1
```

Therefore:

```text
Σ min(ai,aj) over i<j
=
Σ a[i] * (n-i-1)
```

### Dry run

```text
a = [2,5,9]

pairs:
(2,5): min=2 max=5
(2,9): min=2 max=9
(5,9): min=5 max=9

sum max = 5+9+9 = 23
formula = 2*0 + 5*1 + 9*2 = 23

sum min = 2+2+5 = 9
formula = 2*2 + 5*1 + 9*0 = 9
```

### Pattern recognition

When asked:

```text
sum over all pairs of min(...)
sum over all pairs of max(...)
```

sorting can convert pair interactions into **contribution counting**.

### Daily-life scenario — Pairing people by height
Sorted heights:
```text
150, 170, 190
```
For each pair, record the taller height:
```text
(150,170) -> 170
(150,190) -> 190
(170,190) -> 190
```
Count contribution instead:
```text
150: 0 smaller values -> 150*0
170: 1 smaller value  -> 170*1
190: 2 smaller values -> 190*2

sum = 0+170+380 = 550
```
Direct enumeration also gives `170+190+190=550`.

**Memory hook:** after sorting, index tells how many pairs make an element the maximum; remaining-right count does the same for minimum.

---

## 9.15 Contest Recognition Cheat Sheet

| Story phrase | Mathematical model |
|---|---|
| no smaller than both `A` and `B` | `>= max(A,B)` |
| no larger than both `A` and `B` | `<= min(A,B)` |
| largest legal value | `min(all upper bounds)` |
| smallest legal value | `max(all lower bounds)` |
| force `x` into `[L,R]` | `min(max(x,L),R)` |
| intersection left endpoint | `max(L1,L2)` |
| intersection right endpoint | `min(R1,R2)` |
| overlap length | `max(0,min(R1,R2)-max(L1,L2))` |
| inclusive integer overlap count | previous expression with `+1` before outer max |
| distance to interval | `max(L-x,0,x-R)` |
| positive deficit | `max(0,need-have)` |
| complete products from resources | minimum resource capacity |
| minimize worst load | `min(max(...))` |
| maximize weakest value | `max(min(...))` |
| pairwise absolute gap | `max-min` |
| all pair maxima after sorting | contribution by right index |
| all pair minima after sorting | contribution by remaining right choices |

### 10-second recognition question

Ask:

```text
1. Is this value limited from ABOVE by several things?
   -> min

2. Is it forced from BELOW by several things?
   -> max

3. Is there an intersection?
   -> max(left), min(right)

4. Is there a deficit that should never be negative?
   -> max(0, deficit)

5. Is the objective about the worst component?
   -> max inside the objective

6. Is the objective about the weakest component?
   -> min inside the objective
```

### Daily-life scenario — Sentence-to-form drill
```text
"Keep thermostat between 18 and 26"
-> clamp(x,18,26)

"When are both people free?"
-> max(starts), min(ends)

"How much money am I short?"
-> max(0,need-have)

"How many complete meals can I make?"
-> min(resource capacities)

"Share work so the busiest person has least work"
-> minimize max(...)

"Share fairly so the least-served gets as much as possible"
-> maximize min(...)
```
**Training goal:** make English sentence → mathematical form an automatic reaction.

---

## 9.16 Common Mistakes

### Mistake 1 — reversing interval boundaries

Wrong:

```text
left  = min(L1,L2)
right = max(R1,R2)
```

That describes the outer span, not the intersection.

Correct:

```text
left  = max(L1,L2)
right = min(R1,R2)
```

### Mistake 2 — forgetting zero for disjoint overlap

Wrong:

```text
overlap = min(R1,R2)-max(L1,L2)
```

Correct:

```text
overlap = max(0, min(R1,R2)-max(L1,L2))
```

### Mistake 3 — forgetting `+1` for inclusive integer points

```text
[L,R] contains R-L+1 integers
```

Example:

```text
[5,8] -> {5,6,7,8} -> 4
8-5+1 = 4
```

### Mistake 4 — confusing maximum feasible with maximum bound

```text
x <= 10
x <= 7
```

`10` is not feasible as a maximum. Both constraints must hold:

```text
x <= min(10,7) = 7
```

### Mistake 5 — integer overflow in transformed formulas

Expressions such as:

```text
a[i] * i
a+b+abs(a-b)
```

may overflow 32-bit `int`. Use `long long` when constraints require it.

### Daily-life scenario — Use physical meaning to catch errors
Two shops are open:
```text
A: 09:00-12:00
B: 14:00-18:00
```
Raw overlap:
```text
min(12,18)-max(9,14)
= 12-14
= -2
```
But negative common opening time is physically impossible:
```text
overlap = max(0,-2) = 0
```
For inclusive house numbers `[5,8]`:
```text
5,6,7,8 -> 4 houses
8-5+1 = 4
```
**Memory hook:** sanity-check formulas against reality: count, distance, duration, and shortage often cannot be negative.

---

## 9.17 Fast Revision Card

```text
========================================================
MIN / MAX MATHEMATICAL TRANSFORMATIONS
========================================================

UPPER bounds:
    x <= A, x <= B
    => x <= min(A,B)

LOWER bounds:
    x >= A, x >= B
    => x >= max(A,B)

CLAMP:
    clamp(x,L,R)
    = min(max(x,L),R)

INTERSECTION:
    L = max(L1,L2)
    R = min(R1,R2)

CONTINUOUS OVERLAP:
    max(0, R-L)

INCLUSIVE INTEGER COUNT:
    max(0, R-L+1)

DISTANCE POINT -> INTERVAL:
    max(L-x, 0, x-R)

PAIR IDENTITIES:
    min(a,b)+max(a,b)=a+b
    |a-b|=max(a,b)-min(a,b)

    max(a,b)=(a+b+|a-b|)/2
    min(a,b)=(a+b-|a-b|)/2

POSITIVE DEFICIT:
    max(0, need-have)

BOTTLENECK:
    complete units = min(capacity1, capacity2, ...)

MAXIMUM FEASIBLE:
    min(all upper bounds)

MINIMUM REQUIRED:
    max(all lower bounds)

OPTIMIZATION:
    minimize worst -> min(max(...))
    maximize weakest -> max(min(...))

SORTED PAIR CONTRIBUTION (0-indexed):
    Σ pairwise max = Σ a[j] * j
    Σ pairwise min = Σ a[i] * (n-i-1)

MENTAL QUESTION:
    "What stops me first?"  -> min
    "What forces me highest?" -> max
========================================================
```

### Daily-life memory hooks
```text
Clamp             -> thermostat range
Intersection      -> two people's free time
Distance to range -> car outside parking zone
Bounds            -> multiple eligibility rules
Positive deficit  -> prepaid top-up
Bottleneck        -> sandwich ingredient running out
Minimize maximum  -> split workload
Maximize minimum  -> share chocolates fairly
Prefix/suffix     -> hottest/coldest seen so far
Pair contribution -> sorted heights
```
Use the real-world picture first, then recall the formula.

