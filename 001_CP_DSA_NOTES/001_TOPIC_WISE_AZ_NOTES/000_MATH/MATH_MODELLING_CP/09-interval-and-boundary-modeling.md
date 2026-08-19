# CP Mathematical Modeling Mini-Course

## 9. Interval & Boundary Modeling

> **Goal:** Learn to translate words such as **between, inside, overlap, intersect, cover, before, after, distance, range, segment** into precise mathematics.
>
> Many CF implementation and greedy problems are not algorithmically difficult. The difficulty is getting the **boundaries** exactly right.

---

# Chapter Tree

```text
9. INTERVAL & BOUNDARY MODELING
│
├── 9.1 What is an interval?
├── 9.2 Closed vs open boundaries
├── 9.3 Translate English into inequalities
├── 9.4 Intersection of two intervals
├── 9.5 Detect no overlap
├── 9.6 Length vs number of integers
├── 9.7 Clamp a value into a range
├── 9.8 Distance between intervals
├── 9.9 Containment
├── 9.10 Union and merged coverage
├── 9.11 Multiple interval intersection
├── 9.12 Valid answer as an interval
├── 9.13 Shifted intervals
├── 9.14 Endpoint/event thinking
├── 9.15 Off-by-one discipline
└── 9.16 Complete CF-style examples
```

Central engine:

```text
STORY ABOUT RANGES
        ↓
write each range as [L,R]
        ↓
translate words into <= / <
        ↓
combine boundaries with
max(left endpoints)
min(right endpoints)
        ↓
check whether valid
        ↓
length / count / distance / coverage
```

---

# 9.1 What Is an Interval?

An interval describes all values between two boundaries.

Closed interval:

```text
[L, R]
```

means:

```text
L <= x <= R
```

Example:

```text
[3,7]
```

contains:

```text
3,4,5,6,7
```

for integer `x`.

Number line:

```text
----●================●----
    3                7
```

The filled endpoints mean `3` and `7` are included.

---

# 9.2 Open and Half-Open Intervals

Open interval:

```text
(L,R)
```

means:

```text
L < x < R
```

Endpoints are excluded.

```text
----○================○----
    L                R
```

Half-open:

```text
[L,R)
```

means:

```text
L <= x < R
```

and:

```text
(L,R]
```

means:

```text
L < x <= R
```

In Codeforces statements, you often see wording rather than interval notation.

You must translate it.

---

# 9.3 English -> Inequality

Common translations:

```text
at least L
    -> x >= L

at most R
    -> x <= R

more than L
    -> x > L

less than R
    -> x < R

between L and R, inclusive
    -> L <= x <= R

strictly between L and R
    -> L < x < R

from L to R inclusive
    -> L <= x <= R
```

Do not rely on intuition.

Write the inequalities explicitly.

---

# 9.4 Two Conditions Form an Interval

Suppose:

```text
x >= 5
x <= 12
```

Combine:

```text
5 <= x <= 12
```

or:

```text
x in [5,12]
```

Visual:

```text
x >= 5:
-----5====================>

x <= 12:
<===================12-----

both:
-----5==========12---------
```

This is **intersection of constraints**.

---

# 9.5 Intersection of Two Intervals

Suppose:

```text
A = [L1,R1]
B = [L2,R2]
```

A value belongs to both intervals when it satisfies both sets of bounds.

The intersection starts at the later left endpoint:

```text
L = max(L1,L2)
```

and ends at the earlier right endpoint:

```text
R = min(R1,R2)
```

Therefore:

```text
intersection =
[max(L1,L2), min(R1,R2)]
```

provided it is valid.

Visual:

```text
A: ----[==========]---------
       L1         R1

B: --------[==========]-----
           L2         R2

intersection:
--------[======]-------------
        max L   min R
```

This formula should become automatic.

---

# 9.6 When Does an Intersection Exist?

After computing:

```text
L = max(L1,L2)
R = min(R1,R2)
```

for closed intervals:

```text
intersection exists iff L <= R
```

If:

```text
L > R
```

there is no common point.

Example:

```text
[1,4]
[7,10]
```

Then:

```text
L = max(1,7) = 7
R = min(4,10) = 4
```

Since:

```text
7 > 4
```

no intersection.

Visual:

```text
[1----4]     [7----10]
        gap
```

---

# 9.7 Touching at One Point

Intervals:

```text
[1,5]
[5,9]
```

Intersection:

```text
[5,5]
```

For closed intervals this is valid.

They share exactly one point:

```text
5
```

So:

```text
max(L1,L2) <= min(R1,R2)
```

uses:

```text
<=
```

not `<`.

This is a classic boundary bug.

---

# 9.8 Continuous Length vs Integer Count

This distinction is extremely important.

For continuous interval:

```text
[L,R]
```

geometric length is:

```text
R-L
```

Example:

```text
[3,7]
```

length:

```text
7-3 = 4
```

But number of integers inside:

```text
3,4,5,6,7
```

is:

```text
R-L+1
```

So:

```text
integer count = R-L+1
```

if:

```text
L <= R
```

Keep these separate:

```text
CONTINUOUS LENGTH:
R-L

NUMBER OF INTEGER POINTS:
R-L+1
```

---

# 9.9 Example — Integer Intersection Count

Intervals:

```text
[2,8]
[5,10]
```

Intersection:

```text
[5,8]
```

Number of integer values:

```text
8-5+1 = 4
```

Values:

```text
5,6,7,8
```

Formula:

```text
L = max(L1,L2)
R = min(R1,R2)

if L <= R:
    count = R-L+1
else:
    count = 0
```

---

# 9.10 Clamp a Value Into a Range

Sometimes a value must be forced into:

```text
[L,R]
```

If:

```text
x < L
```

use:

```text
L
```

If:

```text
x > R
```

use:

```text
R
```

Otherwise keep `x`.

Formula:

```text
clamped =
min(max(x,L),R)
```

Example:

```text
range [3,10]

x = 1  -> 3
x = 7  -> 7
x = 15 -> 10
```

Visual:

```text
          [3==========10]
x=1  ---> 3
x=7  --------->7
x=15 ------------------->10
```

This appears in geometry, simulation, and nearest-point problems.

---

# 9.11 Distance From a Point to an Interval

Given point `x` and interval:

```text
[L,R]
```

Cases:

```text
x < L
    distance = L-x

L <= x <= R
    distance = 0

x > R
    distance = x-R
```

ASCII:

```text
x       [L==========R]
|-------|
 distance

or

[L======x=====R]
distance = 0

or

[L==========R]       x
            |--------|
             distance
```

Equivalent using clamping:

```text
nearest = clamp(x,L,R)
distance = abs(x-nearest)
```

---

# 9.12 Distance Between Two Intervals

Intervals:

```text
A = [L1,R1]
B = [L2,R2]
```

If they overlap:

```text
distance = 0
```

If A is completely before B:

```text
R1 < L2
```

distance:

```text
L2-R1
```

If B is completely before A:

```text
R2 < L1
```

distance:

```text
L1-R2
```

Cases:

```text
A before B:
[L1----R1]       [L2----R2]
          <----->
         L2-R1

overlap:
[L1---------R1]
      [L2---------R2]

distance = 0

B before A:
[L2----R2]       [L1----R1]
          <----->
         L1-R2
```

---

# 9.13 A Compact Interval-Distance Formula

For closed intervals:

```text
distance =
max(
    0,
    L2-R1,
    L1-R2
)
```

Why?

If overlapping, both gap expressions are non-positive, so answer is `0`.

If A is before B:

```text
L2-R1 > 0
```

If B is before A:

```text
L1-R2 > 0
```

This is a good example of converting cases into one formula after understanding the cases.

---

# 9.14 Containment

Interval A:

```text
[L1,R1]
```

is completely inside B:

```text
[L2,R2]
```

when:

```text
L2 <= L1
AND
R1 <= R2
```

Visual:

```text
B: [----------------------]
A:      [==========]

condition:
B.left <= A.left
A.right <= B.right
```

Do not compare lengths alone.

A shorter interval is not necessarily inside another interval.

---

# 9.15 Overlap vs Containment

These are different.

Overlap only needs:

```text
max(L1,L2) <= min(R1,R2)
```

Containment of A inside B needs:

```text
L2 <= L1
AND
R1 <= R2
```

Example:

```text
A = [1,6]
B = [4,10]
```

They overlap:

```text
[4,6]
```

But neither contains the other.

Always identify what the statement actually asks.

---

# 9.16 Union Length of Two Intervals

Suppose continuous intervals:

```text
A = [L1,R1]
B = [L2,R2]
```

Individual lengths:

```text
lenA = R1-L1
lenB = R2-L2
```

If they overlap, adding lengths counts overlap twice.

Use:

```text
union length
=
lenA + lenB - intersection length
```

Intersection length:

```text
max(
    0,
    min(R1,R2) - max(L1,L2)
)
```

So:

```text
union =
(R1-L1)
+
(R2-L2)
-
intersection
```

This is continuous-length modeling.

For integer-point counting, endpoint handling differs.

---

# 9.17 Multiple Interval Intersection

Suppose there are many constraints:

```text
x in [L1,R1]
x in [L2,R2]
x in [L3,R3]
...
```

To satisfy all:

```text
x >= every Li
```

so:

```text
x >= max(Li)
```

And:

```text
x <= every Ri
```

so:

```text
x <= min(Ri)
```

Therefore:

```text
global L = max of all left endpoints
global R = min of all right endpoints
```

Feasible iff:

```text
L <= R
```

Visual:

```text
all lower bounds
      ↓
take MAX

all upper bounds
      ↓
take MIN

valid interval:
[MAX_LEFT, MIN_RIGHT]
```

Notice the connection to Chapter 5:

```text
lower bounds -> max
upper bounds -> min
```

---

# 9.18 Feasible Answer as an Interval

Suppose a candidate `k` must satisfy:

```text
k >= 3
k >= 7
k <= 20
k <= 12
```

Combine lower bounds:

```text
k >= max(3,7)
k >= 7
```

Combine upper bounds:

```text
k <= min(20,12)
k <= 12
```

Therefore:

```text
7 <= k <= 12
```

The feasible answer set is:

```text
[7,12]
```

This connects:

```text
conditions
resource bounds
feasibility
interval modeling
```

into one picture.

---

# 9.19 Shifted Intervals

Suppose:

```text
x in [L,R]
```

What values can:

```text
x + d
```

take?

Simply shift both endpoints:

```text
x+d in [L+d, R+d]
```

Example:

```text
x in [3,7]
d = 5
```

Then:

```text
x+5 in [8,12]
```

Similarly:

```text
x-d in [L-d,R-d]
```

This is useful for operation/reachability problems.

---

# 9.20 Sum of Two Independent Intervals

Suppose:

```text
x in [A,B]
y in [C,D]
```

What is the possible range of:

```text
x+y
```

Minimum occurs with both minima:

```text
A+C
```

Maximum occurs with both maxima:

```text
B+D
```

So:

```text
x+y in [A+C, B+D]
```

assuming independent choices over continuous ranges or all integer values in those integer ranges.

This can quickly prove impossibility.

Example:

```text
x in [2,5]
y in [10,20]
```

Then:

```text
x+y in [12,25]
```

Target:

```text
30
```

is impossible.

---

# 9.21 Difference of Two Intervals

Suppose:

```text
x in [A,B]
y in [C,D]
```

For:

```text
x-y
```

minimum:

```text
A-D
```

maximum:

```text
B-C
```

Therefore:

```text
x-y in [A-D, B-C]
```

Why?

To minimize:

```text
smallest x - largest y
```

To maximize:

```text
largest x - smallest y
```

This is useful when translating bounds through equations.

---

# 9.22 Endpoint Thinking

For many interval problems, only endpoints matter.

Example:

> Do intervals overlap?

You do not need to inspect every point.

Only:

```text
L1,R1,L2,R2
```

matter.

Likewise:

```text
intersection
containment
distance
merged coverage
```

can often be decided entirely from endpoints.

Mental trigger:

```text
The story describes a whole range.

Can I represent the entire range
with only two numbers?
```

Usually:

```text
[L,R]
```

---

# 9.23 Event Thinking

For many intervals, another useful representation is:

```text
start event at L
end event at R
```

Example:

```text
[2,6]
```

can be viewed as:

```text
start at 2
end at 6
```

For many intervals:

```text
[1,4]
[3,8]
[7,10]
```

events occur at:

```text
1 start
3 start
4 end
7 start
8 end
10 end
```

This viewpoint later leads to:

```text
sorting endpoints
sweep line
difference arrays
event processing
```

But first understand the model:

```text
interval
=
active between start and end
```

---

# 9.24 Inclusive Endpoint Events

Boundary convention matters.

Suppose closed intervals:

```text
[1,5]
[5,8]
```

They overlap at:

```text
5
```

If processing events at the same coordinate, whether "start" or "end" is processed first can change the result.

This is why you must know whether intervals are:

```text
closed
open
half-open
```

before designing event logic.

Do not memorize event ordering without understanding the boundary semantics.

---

# 9.25 Half-Open Intervals in Programming

Programming often uses:

```text
[L,R)
```

because length is simply:

```text
R-L
```

and adjacent intervals:

```text
[0,3)
[3,7)
```

do not overlap.

They represent:

```text
[0,3):
0,1,2

[3,7):
3,4,5,6
```

This convention is common in:

```text
array ranges
STL iterators
prefix sums
```

For example, prefix sums often model subarray:

```text
[L,R)
```

as:

```text
prefix[R] - prefix[L]
```

Boundary notation can simplify equations.

---

# 9.26 Off-by-One Discipline

Whenever integers and ranges appear, explicitly write a tiny example.

Suppose:

```text
L = 3
R = 5
```

Integers:

```text
3,4,5
```

Count:

```text
3
```

Formula:

```text
R-L+1
= 5-3+1
= 3
```

If your formula says:

```text
R-L = 2
```

you know that is continuous length, not integer count.

Tiny examples catch off-by-one errors quickly.

---

# 9.27 Common Boundary Words

Translate carefully:

```text
before x
    usually < x

at or before x
    <= x

after x
    > x

at or after x
    >= x

inside [L,R]
    L <= x <= R

strictly inside
    L < x < R

no later than x
    <= x

no earlier than x
    >= x
```

In contests, a single word can change:

```text
<
```

to:

```text
<=
```

and break the solution.

---

# 9.28 Complete CF-Style Example 1 — Common Valid Values

Problem:

> `x` must satisfy:

```text
A <= x <= B
C <= x <= D
```

Find number of integer values of `x`.

Intersection:

```text
L = max(A,C)
R = min(B,D)
```

If:

```text
L > R
```

answer:

```text
0
```

Otherwise:

```text
R-L+1
```

---

# 9.29 Complete CF-Style Example 2 — Common Meeting Time

Person A is available:

```text
[L1,R1]
```

Person B:

```text
[L2,R2]
```

Common availability:

```text
[max(L1,L2), min(R1,R2)]
```

If start exceeds end:

```text
no common time
```

The scheduling story reduces to interval intersection.

---

# 9.30 Complete CF-Style Example 3 — Distance

Two objects may occupy any point in:

```text
A = [L1,R1]
B = [L2,R2]
```

Minimum possible distance:

```text
0
```

if intervals overlap.

Otherwise the distance is the gap between the closest endpoints.

Formula:

```text
max(
    0,
    L2-R1,
    L1-R2
)
```

---

# 9.31 Complete CF-Style Example 4 — Candidate Range

Suppose operations imply:

```text
k >= ceil(A/3)
```

and resources imply:

```text
k <= B/2
```

Then candidate `k` exists iff:

```text
ceil(A/3) <= B/2
```

The valid interval is:

```text
[ceil(A/3), B/2]
```

This shows how interval modeling can finish a feasibility proof.

---

# 9.32 Complete CF-Style Example 5 — Merge Two Intervals

Given:

```text
A = [L1,R1]
B = [L2,R2]
```

If they overlap/touch under the problem's convention, their merged interval is:

```text
[min(L1,L2), max(R1,R2)]
```

Example:

```text
[2,7]
[5,10]
```

merge:

```text
[2,10]
```

Visual:

```text
[2=======7]
      [5=======10]

merged:
[2===============10]
```

But first verify that merging is allowed; disjoint intervals cannot always be represented by one interval without also covering the gap.

---

# 9.33 Complete CF-Style Example 6 — Many Constraints

Problem gives:

```text
x >= a
x >= b
x >= c

x <= p
x <= q
```

Do not keep five separate conditions mentally.

Compress:

```text
lower = max(a,b,c)
upper = min(p,q)
```

Then:

```text
valid iff lower <= upper
```

If maximizing `x`:

```text
answer = upper
```

if feasible.

If minimizing `x`:

```text
answer = lower
```

if feasible.

This is constraint compression.

---

# 9.34 Interval Modeling Checklist

When ranges appear:

```text
1. WHAT ARE THE ENDPOINTS?

2. ARE ENDPOINTS INCLUDED?
   < or <= ?

3. INTEGER POINTS OR CONTINUOUS LENGTH?

4. NEED INTERSECTION?
   left  = max(lefts)
   right = min(rights)

5. IS INTERSECTION VALID?
   closed: left <= right

6. NEED COUNT?
   integer closed interval:
   right-left+1

7. NEED DISTANCE?
   overlap -> 0
   otherwise gap

8. NEED CONTAINMENT?

9. NEED UNION / MERGING?

10. CAN MANY CONDITIONS BE COMPRESSED
    INTO ONE [LOWER, UPPER]?
```

---

# 9.35 Common Mistakes

## Mistake 1 — Using `R-L` for integer count

For:

```text
[3,7]
```

integer count is:

```text
7-3+1 = 5
```

not `4`.

---

## Mistake 2 — Using `<` instead of `<=` for closed overlap

```text
[1,5]
[5,8]
```

share point `5`.

So closed intervals overlap when:

```text
max(L1,L2) <= min(R1,R2)
```

---

## Mistake 3 — Confusing overlap and containment

Overlap:

```text
some common region
```

Containment:

```text
entire interval inside another
```

Different conditions.

---

## Mistake 4 — Ignoring boundary wording

```text
at most
```

and:

```text
less than
```

are not the same.

---

## Mistake 5 — Mixing continuous and discrete models

Ask:

```text
Am I measuring geometric length,
or counting integer positions?
```

before choosing `R-L` versus `R-L+1`.

---

# 9.36 Translation Drills

Do not code.

---

## Drill 1

```text
x at least 4 and at most 9
```

Model:

```text
4 <= x <= 9
```

---

## Drill 2

Intervals:

```text
[2,7]
[5,11]
```

Intersection:

```text
[max(2,5), min(7,11)]
=
[5,7]
```

Integer count:

```text
7-5+1 = 3
```

---

## Drill 3

Intervals:

```text
[1,3]
[6,9]
```

Distance:

```text
6-3 = 3
```

---

## Drill 4

Constraints:

```text
x >= 3
x >= 8
x <= 15
x <= 12
```

Compressed:

```text
8 <= x <= 12
```

---

## Drill 5

```text
x in [2,5]
y in [10,20]
```

Possible sum range:

```text
x+y in [12,25]
```

---

# 9.37 Practice Set

For each problem write:

```text
INTERVAL(S):
BOUNDARY TYPE:
INTERSECTION:
FEASIBILITY CONDITION:
COUNT/LENGTH/DISTANCE:
```

---

## Practice A

> Count integers satisfying both:

```text
2 <= x <= 20
7 <= x <= 15
```

---

## Practice B

> Determine whether closed intervals `[L1,R1]` and `[L2,R2]` overlap.

---

## Practice C

> Find minimum distance between two closed intervals.

---

## Practice D

> `k` must satisfy three lower bounds and two upper bounds. Compress them.

---

## Practice E

> Array index `i` can be selected only if:

```text
i >= L
i <= R
0 <= i < n
```

Find the final valid index interval.

---

# 9.38 Practice Answers

## A

Intersection:

```text
[7,15]
```

Count:

```text
15-7+1 = 9
```

---

## B

Overlap iff:

```text
max(L1,L2) <= min(R1,R2)
```

---

## C

```text
distance =
max(
    0,
    L2-R1,
    L1-R2
)
```

---

## D

If lower bounds are:

```text
L1,L2,L3
```

and upper bounds:

```text
R1,R2
```

then:

```text
L = max(L1,L2,L3)
R = min(R1,R2)
```

Feasible iff:

```text
L <= R
```

---

## E

Original index domain:

```text
[0,n-1]
```

Additional:

```text
[L,R]
```

Final:

```text
[max(0,L), min(n-1,R)]
```

if the left endpoint does not exceed the right endpoint.

---

# 9.39 Chapter Mastery Test

You are ready for the next chapter when you automatically see:

```text
x >= several lower bounds
x <= several upper bounds
```

and compress them to:

```text
[max(lower bounds), min(upper bounds)]
```

You should see two ranges and immediately think:

```text
intersection left  = max(left endpoints)
intersection right = min(right endpoints)
```

You should distinguish:

```text
continuous length = R-L

integer count = R-L+1
```

and recognize that:

```text
touching endpoints
```

may or may not count depending on whether boundaries are inclusive.

Most importantly:

```text
Turn vague words like
"inside", "overlap", "between", "possible range"

into exact inequalities first.
```

---

# 9.40 Final Mental Engine

```text
               RANGE STORY
                    │
                    ▼
              WRITE [L,R]
                    │
                    ▼
          TRANSLATE BOUNDARIES
             < / <= / > / >=
                    │
                    ▼
        MANY LOWER / UPPER BOUNDS?
                    │
                    ▼
       L = max(all lower bounds)
       R = min(all upper bounds)
                    │
                    ▼
               L <= R ?
              /        \
            NO          YES
            │            │
       impossible    valid interval
                         │
              ┌──────────┼──────────┐
              │          │          │
            count      length     distance
              │          │          │
              ▼          ▼          ▼
           R-L+1       R-L       gap / 0
```

The core habit:

```text
An interval problem is usually
a boundary problem.

Represent the whole range
with its endpoints,
then reason with max and min.
```

---

# Next Chapter

```text
9. INTERVAL & BOUNDARY MODELING
                ↓
10. RATE, TIME & WORK MODELING
```

Chapter 10 will focus on translating stories involving:

```text
speed
production rate
work per unit time
distance
time
parallel workers/machines
meeting/catching
minimum completion time
```

into equations such as:

```text
work = rate * time
distance = speed * time
production(T) = sum(T / machine_time)
relative speed
```

with emphasis on modeling rather than memorizing formulas.
