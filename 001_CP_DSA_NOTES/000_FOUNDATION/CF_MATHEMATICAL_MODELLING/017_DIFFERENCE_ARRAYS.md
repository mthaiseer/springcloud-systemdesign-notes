# Part 16 — Difference Arrays

> **Goal:** learn to model repeated **range updates** as changes only at interval boundaries, then reconstruct the final values with a prefix sum.
>
> **Core transformation:** `Update every element in [L,R] → mark where effect starts and stops → prefix-sum once`
>
> **Recognition question:** **Can I represent a whole range update using only its two boundaries?**

## Table of Contents

- [16.0 Difference Array Mental Model](#160-difference-array-mental-model)
- [16.1 From Values to Differences](#161-from-values-to-differences)
- [16.2 Reconstructing the Original Array](#162-reconstructing-the-original-array)
- [16.3 Range Addition Using Two Boundary Updates](#163-range-addition-using-two-boundary-updates)
- [16.4 Why diff[L] += x and diff[R+1] -= x Works](#164-why-diffl--x-and-diffr1--x-works)
- [16.5 Multiple Range Updates](#165-multiple-range-updates)
- [16.6 Difference Array from an Existing Array](#166-difference-array-from-an-existing-array)
- [16.7 Difference Array with n+1 Storage](#167-difference-array-with-n1-storage)
- [16.8 Range Increment and Decrement](#168-range-increment-and-decrement)
- [16.9 Coverage and Overlap Counting](#169-coverage-and-overlap-counting)
- [16.10 Maximum Simultaneous Activity](#1610-maximum-simultaneous-activity)
- [16.11 Difference Arrays on Coordinates and Timelines](#1611-difference-arrays-on-coordinates-and-timelines)
- [16.12 Inclusive vs Half-Open Intervals](#1612-inclusive-vs-half-open-intervals)
- [16.13 Range Assignment Is Not Simple Difference Addition](#1613-range-assignment-is-not-simple-difference-addition)
- [16.14 Range Arithmetic Progression Updates](#1614-range-arithmetic-progression-updates)
- [16.15 Second-Order Difference Arrays](#1615-second-order-difference-arrays)
- [16.16 Two-Dimensional Difference Arrays](#1616-two-dimensional-difference-arrays)
- [16.17 Difference Array plus Prefix Query Pipeline](#1617-difference-array-plus-prefix-query-pipeline)
- [16.18 Difference Array vs Prefix Sum](#1618-difference-array-vs-prefix-sum)
- [16.19 Difference Array vs Fenwick and Segment Tree](#1619-difference-array-vs-fenwick-and-segment-tree)
- [16.20 Coordinate Compression with Difference Arrays](#1620-coordinate-compression-with-difference-arrays)
- [16.21 60-Second Discovery Workflow](#1621-60-second-discovery-workflow)
- [16.22 Codeforces Recognition Map](#1622-codeforces-recognition-map)
- [16.23 Common Mistakes](#1623-common-mistakes)
- [16.24 Fast Revision Card](#1624-fast-revision-card)

---

## 16.0 Difference Array Mental Model

Suppose we repeatedly add values to whole ranges.

Naive update:

```text
add +5 to [2,6]

a[2] += 5
a[3] += 5
a[4] += 5
a[5] += 5
a[6] += 5
```

That is `O(length of range)`.

Difference-array thinking:

```text
effect STARTS at 2
effect STOPS after 6
```

Represent only those changes:

```text
diff[2] += 5
diff[7] -= 5
```

Then one prefix sweep spreads the effect.

### Real-world example — salary bonus

Employees:

```text
0   1   2   3   4
```

Everyone starts with:

```text
100
```

Employees `1..3` receive `+20`.

Instead of writing:

```text
employee 1 += 20
employee 2 += 20
employee 3 += 20
```

record:

```text
+20 begins at employee 1
-20 begins after employee 3
```

So:

```text
diff[1] += 20
diff[4] -= 20
```

Prefix the changes:

```text
index:    0   1   2   3   4
change:   0  +20   0   0  -20

running:
          0   20  20  20   0
```

Final salaries:

```text
100,120,120,120,100
```

### Core memory hook

```text
DIFFERENCE ARRAY = STORE CHANGES AT BOUNDARIES
PREFIX SUM       = SPREAD THOSE CHANGES
```

---

## 16.1 From Values to Differences

For array:

```text
a = [5,8,8,12,10]
```

define:

```text
diff[0] = a[0]
diff[i] = a[i]-a[i-1]    for i>0
```

Step by step:

```text
diff[0] = 5
diff[1] = 8-5   = 3
diff[2] = 8-8   = 0
diff[3] = 12-8  = 4
diff[4] = 10-12 = -2
```

Therefore:

```text
diff = [5,3,0,4,-2]
```

### Interpretation

```text
5   -> start at 5
+3  -> rise by 3
 0  -> no change
+4  -> rise by 4
-2  -> fall by 2
```

### Real-world example — temperature changes

Temperatures:

```text
Mon 20
Tue 23
Wed 23
Thu 27
Fri 25
```

Difference representation:

```text
Mon: 20
Tue: +3
Wed:  0
Thu: +4
Fri: -2
```

Instead of storing each absolute temperature conceptually, we can describe:

```text
starting temperature + daily changes
```

---

## 16.2 Reconstructing the Original Array

Since:

```text
diff[i] = a[i]-a[i-1]
```

we have:

```text
a[i] = a[i-1]+diff[i]
```

Thus the original array is the prefix sum of `diff`.

Example:

```text
diff = [5,3,0,4,-2]
```

Reconstruct:

```text
a[0] = 5
a[1] = 5+3      = 8
a[2] = 8+0      = 8
a[3] = 8+4      = 12
a[4] = 12+(-2)  = 10
```

Result:

```text
[5,8,8,12,10]
```

### Mathematical telescoping

```text
diff[0]+diff[1]+...+diff[i]

= a[0]
+ (a[1]-a[0])
+ (a[2]-a[1])
+ ...
+ (a[i]-a[i-1])

= a[i]
```

Everything inside cancels.

### Memory hook

```text
difference = discrete derivative
prefix sum = discrete integration
```

---

## 16.3 Range Addition Using Two Boundary Updates

To add `x` to inclusive range `[L,R]`:

```text
diff[L]   += x
diff[R+1] -= x
```

### Real-world example — road speed reduction

Road segments:

```text
0  1  2  3  4  5
```

A construction zone covers segments `2..4` and reduces allowed speed by `10`.

Model:

```text
x = -10
L = 2
R = 4
```

Boundary changes:

```text
diff[2] += -10
diff[5] -= -10
```

The second operation is:

```text
diff[5] += 10
```

Running effect:

```text
segment:  0   1    2    3    4   5
effect:   0   0  -10  -10  -10   0
```

Exactly the required range.

### Complexity

For `q` updates:

```text
mark q updates: O(q)
one prefix:     O(n)

total:          O(n+q)
```

instead of potentially:

```text
O(n*q)
```

---

## 16.4 Why diff[L] += x and diff[R+1] -= x Works

Suppose:

```text
diff[L] += x
```

When prefixing:

```text
position L     -> running change increases by x
position L+1   -> still includes x
...
position R     -> still includes x
```

Without another marker, `x` would continue forever.

So place:

```text
diff[R+1] -= x
```

Then at `R+1`:

```text
running change
= previous running change - x
```

and the effect disappears.

### Visual proof

```text
indices:   ... L ---------------- R  R+1 ...
diff:          +x                  -x
                |                   |
prefix:         +x +x +x ... +x     0
                <---- range ---->
```

### Real-world analogy — turn a tap on and off

```text
time L:   turn water ON  (+flow)
time R+1: turn water OFF (-flow)
```

Between those events, water keeps flowing.

Difference arrays use the same start/stop model.

---

## 16.5 Multiple Range Updates

Updates accumulate naturally.

Start:

```text
a = [0,0,0,0,0]
```

Operations:

```text
+10 on [1,3]
+5  on [2,4]
-3  on [0,2]
```

Boundary marks:

```text
+10 [1,3]:
diff[1] += 10
diff[4] -= 10

+5 [2,4]:
diff[2] += 5
diff[5] -= 5

-3 [0,2]:
diff[0] -= 3
diff[3] += 3
```

Using `n+1=6` slots:

```text
index:  0    1    2    3     4    5
diff:  -3   10    5    3   -10   -5
```

Prefix:

```text
i=0: -3
i=1: -3+10 = 7
i=2:  7+5  = 12
i=3: 12+3  = 15
i=4: 15-10 = 5
```

Final:

```text
[-3,7,12,15,5]
```

### Direct verification

```text
index 0: -3
index 1: +10-3       = 7
index 2: +10+5-3     = 12
index 3: +10+5       = 15
index 4: +5          = 5
```

---

## 16.6 Difference Array from an Existing Array

If the initial array is not zero:

```text
a = [10,20,30,40]
```

build:

```text
diff[0]=10
diff[1]=20-10=10
diff[2]=30-20=10
diff[3]=40-30=10
```

So:

```text
diff=[10,10,10,10]
```

Now add `+5` to `[1,2]`:

```text
diff[1] += 5
diff[3] -= 5
```

New:

```text
[10,15,10,5]
```

Prefix:

```text
10
10+15 = 25
25+10 = 35
35+5  = 40
```

Final:

```text
[10,25,35,40]
```

### Real-world example — existing salaries plus temporary department raise

Existing:

```text
1000,1200,1400,1600
```

Employees `1..2` receive `+100`.

Difference representation lets the raise be inserted using only two boundary changes.

---

## 16.7 Difference Array with n+1 Storage

For an array of size `n`, allocate:

```text
diff[n+1]
```

Then every inclusive update can safely write:

```text
diff[L]   += x
diff[R+1] -= x
```

even when:

```text
R=n-1
```

because:

```text
R+1=n
```

is the extra sentinel slot.

### Example

```text
n=5
update [2,4] by +7
```

Use:

```text
diff[2] += 7
diff[5] -= 7
```

Only indices `0..4` are reconstructed.

### Memory hook

This is analogous to prefix arrays using `n+1`:

```text
extra boundary slot removes special cases
```

---

## 16.8 Range Increment and Decrement

Difference arrays do not care whether `x` is positive or negative.

### Increment

```text
add +4 on [L,R]

diff[L]   += 4
diff[R+1] -= 4
```

### Decrement

```text
add -4 on [L,R]

diff[L]   -= 4
diff[R+1] += 4
```

### Real-world example — discount period

Daily price adjustment:

```text
days 0..6
```

A sale reduces price by `15` on days `2..5`.

```text
diff[2] += -15
diff[6] -= -15
```

Equivalent:

```text
diff[2] -= 15
diff[6] += 15
```

Running adjustment:

```text
0,0,-15,-15,-15,-15,0
```

---

## 16.9 Coverage and Overlap Counting

Difference arrays can count how many intervals cover each point.

For each inclusive interval `[L,R]`:

```text
diff[L]++
diff[R+1]--
```

Prefix gives coverage count.

### Real-world example — Wi-Fi coverage

Routers cover hallway positions:

```text
router A: [1,4]
router B: [3,6]
router C: [5,7]
```

Boundary events:

```text
A: +1 at 1, -1 at 5
B: +1 at 3, -1 at 7
C: +1 at 5, -1 at 8
```

Coverage:

```text
position: 0 1 2 3 4 5 6 7
count:    0 1 1 2 2 2 2 1
```

### Interpretation

At position `3`:

```text
A + B = 2 routers
```

At position `5`:

```text
B + C = 2 routers
```

### CP trigger

```text
how many intervals cover each point?
maximum overlap?
covered at least k times?
```

Think:

```text
difference events + prefix
```

---

## 16.10 Maximum Simultaneous Activity

Once coverage is reconstructed:

```text
answer = max(prefix coverage)
```

### Real-world example — meeting-room demand

Meetings use one room each:

```text
[1,4]
[2,5]
[3,6]
```

Boundary events:

```text
+1 at 1, -1 after 4
+1 at 2, -1 after 5
+1 at 3, -1 after 6
```

Coverage:

```text
time:    1 2 3 4 5 6
active:  1 2 3 3 2 1
```

Maximum:

```text
3
```

Therefore at least:

```text
3 rooms
```

are needed.

### Mathematical model

```text
active(t)
=
Σ starts up to t
-
Σ endings before/equal boundary
```

Difference events encode this compactly.

---

## 16.11 Difference Arrays on Coordinates and Timelines

The indices do not need to represent array positions.

They can represent:

```text
time
road coordinate
floor number
day
temperature bucket
score
```

### Real-world example — road maintenance

Road kilometer markers:

```text
0..10
```

Maintenance operations:

```text
+2 workers on km [2,6]
+3 workers on km [5,8]
```

Difference marks:

```text
at 2: +2
at 7: -2

at 5: +3
at 9: -3
```

Prefix gives workers present at each kilometer.

### Modeling insight

Difference arrays are really:

```text
EVENTS AT BOUNDARIES
```

not merely an "array trick".

---

## 16.12 Inclusive vs Half-Open Intervals

This is one of the most important indexing distinctions.

### Inclusive `[L,R]`

Effect includes both endpoints:

```text
L, L+1, ..., R
```

Use:

```text
diff[L]++
diff[R+1]--
```

### Half-open `[L,R)`

Effect includes:

```text
L, L+1, ..., R-1
```

Use:

```text
diff[L]++
diff[R]--
```

### Visual

```text
inclusive [2,5]:

2 3 4 5
* * * *

stop at 6
```

```text
half-open [2,5):

2 3 4
* * *

stop at 5
```

### Real-world analogy

Hotel stay:

```text
check-in day 2
check-out day 5
```

Typically room is occupied nights:

```text
2,3,4
```

This behaves like:

```text
[2,5)
```

The checkout boundary is where occupancy stops.

---

## 16.13 Range Assignment Is Not Simple Difference Addition

Difference arrays naturally support additive updates:

```text
add x to [L,R]
```

But assignment:

```text
set every a[i] in [L,R] = x
```

is different.

### Why?

Suppose:

```text
a=[1,2,3,4]
```

Operation:

```text
set [1,2] = 10
```

Required changes are:

```text
index 1: +8
index 2: +7
```

There is no single constant `+x` applying across the entire range.

### Lesson

Basic difference array is ideal for operations expressible as:

```text
a[i] += constant
```

over a range.

For dynamic range assignment, other techniques such as lazy segment trees may be appropriate.

---

## 16.14 Range Arithmetic Progression Updates

A more advanced difference model handles updates whose added value changes linearly.

Suppose on `[L,R]` we add:

```text
1,2,3,4,...
```

### Example

Start:

```text
[0,0,0,0,0]
```

Add progression on `[1,4]`:

```text
index: 0 1 2 3 4
add:   0 1 2 3 4
```

Notice first differences of the added sequence:

```text
0, +1, +1, +1, +1
```

The **difference of a linear sequence is constant**.

This motivates higher-order difference techniques.

### Mathematical principle

```text
constant values
  -> first difference mostly zero

linear values
  -> first difference constant

quadratic values
  -> second difference constant
```

This is the discrete version of derivatives.

---

## 16.15 Second-Order Difference Arrays

Let:

```text
d1[i] = a[i]-a[i-1]
```

and:

```text
d2[i] = d1[i]-d1[i-1]
```

Then:

```text
prefix d2 -> d1
prefix d1 -> a
```

Two integrations reconstruct the values.

### Real-world example — steadily increasing daily production

Production:

```text
10,12,14,16,18
```

First differences:

```text
10,2,2,2,2
```

Ignoring the initial anchor, the growth rate is constant:

```text
+2 per day
```

Second differences inside the sequence are:

```text
0,0,0
```

### Key intuition

```text
value
  ↓ difference
rate of change
  ↓ difference
change of rate
```

Higher-order difference arrays are useful when range updates themselves follow polynomial patterns.

---

## 16.16 Two-Dimensional Difference Arrays

For rectangle additions in a grid, use boundary events in two dimensions.

To add `x` to rectangle:

```text
(r1,c1) ... (r2,c2)
```

inclusive, mark:

```text
diff[r1][c1]       += x
diff[r1][c2+1]     -= x
diff[r2+1][c1]     -= x
diff[r2+1][c2+1]   += x
```

Then take prefix sums across both dimensions.

### Why four corners?

Think:

```text
+x starts from top-left
-x cancels after right edge
-x cancels after bottom edge
+x restores double cancellation beyond both
```

### Real-world example — rainfall adjustment on a map

Grid:

```text
0 0 0
0 0 0
0 0 0
```

Add `+5` to rectangle rows `0..1`, columns `1..2`.

Desired:

```text
0 5 5
0 5 5
0 0 0
```

Boundary marks on an extra-sized diff grid:

```text
(r1,c1)     +=5
(r1,c2+1)   -=5
(r2+1,c1)   -=5
(r2+1,c2+1) +=5
```

2D prefix reconstruction produces exactly the rectangle.

---

## 16.17 Difference Array plus Prefix Query Pipeline

Sometimes a problem has two offline phases:

```text
1. many range updates
2. many range queries after all updates
```

Pipeline:

```text
range updates
     ↓
difference array
     ↓ prefix
final array
     ↓
prefix sum
     ↓
range queries
```

### Real-world example — store prices

Initial prices:

```text
100,100,100,100
```

Offline updates:

```text
+10 to [0,2]
-5  to [1,3]
```

Final:

```text
110,105,105,95
```

Now build ordinary prefix:

```text
[0,110,215,320,415]
```

Sum prices `[1,3]`:

```text
415-110=305
```

### Important idea

Difference and prefix are not competing techniques.

They can form a pipeline:

```text
difference -> reconstruct -> prefix query structure
```

---

## 16.18 Difference Array vs Prefix Sum

They solve opposite-looking problems.

### Prefix sum

Input:

```text
values
```

Transform into:

```text
cumulative totals
```

Best for:

```text
many range queries
```

### Difference array

Input:

```text
range updates
```

Transform into:

```text
boundary changes
```

Best for:

```text
many offline range updates
```

### Duality

```text
VALUES --difference--> CHANGES
VALUES <--prefix------ CHANGES
```

or:

```text
prefix = integrate
difference = differentiate
```

### Memory hook

```text
QUERY many ranges?  -> PREFIX
UPDATE many ranges? -> DIFFERENCE
```

when operations are offline/static enough.

---

## 16.19 Difference Array vs Fenwick and Segment Tree

Difference arrays are extremely efficient when updates can be processed offline.

### Difference array

```text
range update: O(1)
final rebuild: O(n)
```

But you usually cannot immediately answer arbitrary online queries before reconstruction.

### Fenwick / segment tree

Useful when operations are interleaved:

```text
update
query
update
query
...
```

### Example

If input says:

```text
update [1,5] +3
query sum [2,4]
update [0,3] +2
query sum [1,5]
```

you may need an online data structure.

### Recognition

```text
all updates first, answer later
    -> difference array is strong candidate

updates and queries mixed online
    -> consider Fenwick/segment tree
```

---

## 16.20 Coordinate Compression with Difference Arrays

If coordinates are huge:

```text
1 <= coordinate <= 10^9
```

you cannot allocate an array of size `10^9`.

But if only a small number of boundaries matter, compress them.

### Example

Intervals:

```text
[100,200]
[1000000,2000000]
```

Interesting boundaries include:

```text
100
201
1000000
2000001
```

Sort unique boundaries and assign compact indices.

### Important warning

When physical lengths matter, compressed adjacent indices may represent very different real distances.

Example:

```text
100 -> 201       length 101
201 -> 1000000   enormous gap
```

So for total covered length, retain original coordinates and multiply coverage state by actual coordinate gaps.

---

## 16.21 60-Second Discovery Workflow

```text
PROBLEM
   |
   v
Many updates over intervals?
   |
 +---+---+
NO      YES
         |
         v
Is update additive / composable?
         |
      +--+--+
     NO    YES
            |
            v
Can all updates be processed
before final answers?
            |
        +---+---+
       NO      YES
        |       |
 Fenwick /      DIFFERENCE
 segment tree      |
                   v
         mark start and stop
                   |
                   v
              prefix once
                   |
                   v
              final state
```

### Fast questions

```text
1. Does an operation affect every position in [L,R]?
2. Is the same value x added throughout the range?
3. Can I replace touching every element with start/stop events?
4. Is the interval inclusive or half-open?
5. Do I need only the final array?
6. Do I need overlap counts rather than actual values?
7. Are coordinates huge and compressible?
8. Are updates and queries interleaved online?
```

---

## 16.22 Codeforces Recognition Map

| Statement clue | Difference model |
|---|---|
| add x to many ranges | `+x` at L, `-x` after R |
| increment/decrement interval | boundary update |
| final array after q range operations | difference + prefix |
| number of intervals covering each point | `+1/-1` events |
| maximum overlap | prefix coverage + max |
| active people/processes over time | timeline events |
| road/coordinate interval effects | coordinate difference |
| rectangle additions | 2D difference |
| linear/progression range update | higher-order difference |
| huge sparse coordinates | compression + difference |
| updates then later static queries | diff → rebuild → prefix |
| online mixed update/query | likely Fenwick/segment tree instead |

---

## 16.23 Common Mistakes

### 1. Wrong stopping boundary

For inclusive `[L,R]`:

```text
diff[R+1] -= x
```

not:

```text
diff[R] -= x
```

### 2. Forgetting the extra slot

Allocate:

```text
n+1
```

when using `R+1`.

### 3. Mixing inclusive and half-open intervals

```text
[L,R]  -> stop at R+1
[L,R)  -> stop at R
```

### 4. Forgetting to prefix the diff array

Boundary markers are not the final values.

### 5. Ignoring the initial array

Either:

```text
build diff from initial a
```

or reconstruct update effects and add them to `a`.

### 6. Using int when updates accumulate

Use `long long` when:

```text
q * |x|
```

may exceed 32-bit range.

### 7. Trying to use basic difference for range assignment

Basic difference naturally models additive updates.

### 8. Using a huge coordinate array

Compress sparse coordinates when necessary.

### 9. Forgetting physical gaps after compression

For covered length, use actual coordinate differences.

---

## 16.24 Fast Revision Card

```text
========================================================
PART 16 — DIFFERENCE ARRAYS
========================================================

CORE IDEA

range update [L,R] by +x

instead of:
a[L]   += x
a[L+1] += x
...
a[R]   += x

store only:

diff[L]   += x
diff[R+1] -= x

then prefix diff.

WHY?

+x = effect starts
-x = effect stops

--------------------------------------------

DIFFERENCE DEFINITION

diff[0] = a[0]
diff[i] = a[i]-a[i-1]

RECONSTRUCT

a = prefix(diff)

--------------------------------------------

INCLUSIVE [L,R]

diff[L]   += x
diff[R+1] -= x

HALF-OPEN [L,R)

diff[L] += x
diff[R] -= x

--------------------------------------------

MULTIPLE UPDATES

mark every boundary O(1)
prefix once O(n)

total O(n+q)

--------------------------------------------

COVERAGE

for each interval:
diff[L]++
diff[R+1]--

prefix -> number covering each point

max(prefix) -> maximum overlap

--------------------------------------------

2D RECTANGLE ADD

diff[r1][c1]       += x
diff[r1][c2+1]     -= x
diff[r2+1][c1]     -= x
diff[r2+1][c2+1]   += x

then 2D prefix.

--------------------------------------------

DUALITY

values --difference--> changes
values <--prefix------ changes

difference = discrete derivative
prefix     = discrete integration

--------------------------------------------

WHEN?

many offline range updates
        -> difference array

many static range queries
        -> prefix sum

mixed online updates + queries
        -> Fenwick / segment tree

--------------------------------------------

CORE QUESTION

"Can this whole interval operation
be represented by where its effect
STARTS and STOPS?"
========================================================
```
