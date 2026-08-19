# CP Mathematical Modeling Mini-Course

## 14. Prefix & Difference Modeling

> **Goal:** Learn to transform repeated range/subarray questions into equations on accumulated states.
>
> Core idea:
>
> ```text
> prefix = everything accumulated before here
> range   = difference of two prefixes
> ```

---

# Chapter Tree

```text
14. PREFIX & DIFFERENCE MODELING
│
├── 14.1 Why prefix modeling exists
├── 14.2 Prefix as accumulated state
├── 14.3 Prefix sum construction
├── 14.4 Range = difference of prefixes
├── 14.5 Index conventions
├── 14.6 Subarray equations
├── 14.7 Turn target-sum into prefix relation
├── 14.8 Prefix frequencies
├── 14.9 Counting subarrays
├── 14.10 Prefix parity / remainder / XOR
├── 14.11 Difference arrays
├── 14.12 Range updates as boundary events
├── 14.13 Reconstructing values
├── 14.14 2D intuition
├── 14.15 Prefix vs sliding window
└── 14.16 CF-style modeling workflow
```

Central engine:

```text
ARRAY / SEQUENCE
       │
       ▼
ACCUMULATE INFORMATION
       │
       ▼
prefix[i]
       │
       ▼
range [L,R]
       │
       ▼
prefix[R+1] - prefix[L]
```

For subarray equations:

```text
sum(L..R) = K
       ↓
P[R+1] - P[L] = K
       ↓
P[L] = P[R+1] - K
       ↓
count/search previous prefix states
```

---

# 14.1 Why Prefix Modeling Exists

Suppose:

```text
a = [3,1,4,2,5]
```

You repeatedly need sums such as:

```text
a[1] + a[2] + a[3]
a[0] + ... + a[4]
a[2] + a[3]
```

Recomputing every range repeats work.

Instead store accumulated information once.

```text
original:
3   1   4   2   5

prefix:
0   3   4   8   10   15
```

Then a range is obtained by subtraction.

---

# 14.2 Prefix as a State

Use this definition:

```text
P[i] = sum of first i elements
```

Therefore:

```text
P[0] = 0
P[1] = a[0]
P[2] = a[0]+a[1]
...
P[n] = sum of all n elements
```

Formula:

```text
P[i+1] = P[i] + a[i]
```

Visual:

```text
a:
      3     1     4     2

P:
0 ──> 3 ──> 4 ──> 8 ──> 10
     +3    +1    +4    +2
```

A prefix value represents your accumulated state after consuming some elements.

---

# 14.3 Why Start Prefix With Zero?

The leading zero makes boundaries uniform.

Without it, ranges beginning at index `0` need special handling.

With:

```text
P[0] = 0
```

every half-open range:

```text
[L,R)
```

has sum:

```text
P[R] - P[L]
```

And inclusive range:

```text
[L,R]
```

has sum:

```text
P[R+1] - P[L]
```

This is one of the cleanest indexing conventions in CP.

---

# 14.4 Why Range = Difference of Prefixes

Suppose:

```text
P[R+1]
=
a[0]+a[1]+...+a[L-1]
+
a[L]+...+a[R]
```

And:

```text
P[L]
=
a[0]+a[1]+...+a[L-1]
```

Subtract:

```text
P[R+1] - P[L]
```

Everything before `L` cancels.

Remaining:

```text
a[L]+...+a[R]
```

ASCII:

```text
P[R+1]:
[---------- before L ----------][---- wanted ----]

P[L]:
[---------- before L ----------]

subtract
                    ↓
                           [---- wanted ----]
```

This cancellation is the mathematical reason prefix sums work.

---

# 14.5 Example

Array:

```text
a = [3,1,4,2,5]
```

Prefix:

```text
P = [0,3,4,8,10,15]
```

Find inclusive range:

```text
L = 1
R = 3
```

Wanted:

```text
1+4+2 = 7
```

Formula:

```text
P[R+1] - P[L]
=
P[4] - P[1]
=
10 - 3
=
7
```

---

# 14.6 Prefix Is More Than Sum

The prefix idea means:

```text
"information about everything before position i"
```

It can store:

```text
sum
count
XOR
frequency
number of odd elements
number of zeros
balance
remainder information
```

Example:

```text
oddPrefix[i]
=
number of odd elements among first i elements
```

Then odd count in `[L,R]`:

```text
oddPrefix[R+1] - oddPrefix[L]
```

So the modeling pattern is broader than arithmetic sums.

---

# 14.7 Convert Values Before Prefixing

Suppose problem asks:

> How many positive numbers are in each range?

Create:

```text
b[i] =
1 if a[i] > 0
0 otherwise
```

Then prefix `b`.

Example:

```text
a = [-2,5,7,-1,4]

b = [0,1,1,0,1]
```

Now:

```text
sum of b[L..R]
```

is exactly:

```text
number of positive elements in [L,R]
```

Important modeling habit:

```text
raw data
   ↓
convert each element into contribution
   ↓
prefix contributions
```

---

# 14.8 Subarray as Difference of Two States

Every subarray can be viewed as:

```text
state after R
-
state before L
```

For sums:

```text
sum(L..R)
=
P[R+1]-P[L]
```

This turns a problem about many subarrays into a problem about pairs of prefix states.

That transformation is extremely important.

```text
SUBARRAY PROBLEM
      ↓
PAIR OF PREFIXES
```

---

# 14.9 Target Subarray Sum

Suppose we need:

```text
sum(L..R) = K
```

Using prefix:

```text
P[R+1] - P[L] = K
```

Rearrange:

```text
P[L] = P[R+1] - K
```

Now imagine scanning from left to right.

At current prefix:

```text
cur = P[R+1]
```

we need an earlier prefix equal to:

```text
cur-K
```

So the story becomes:

```text
How many previous prefixes equal cur-K?
```

This leads naturally to a frequency map.

---

# 14.10 Example — Count Subarrays With Sum K

Array:

```text
[1,2,1,2]
```

Target:

```text
K = 3
```

Prefixes:

```text
0,1,3,4,6
```

For each current prefix `cur`, seek:

```text
cur-3
```

```text
cur=1 -> seek -2
cur=3 -> seek 0   ✓
cur=4 -> seek 1   ✓
cur=6 -> seek 3   ✓
```

So there are:

```text
3
```

target-sum subarrays.

The key is not memorizing hashmap code.

The key equation is:

```text
P[j] - P[i] = K
```

then:

```text
P[i] = P[j]-K
```

---

# 14.11 Why Frequency, Not Just Existence?

Suppose the same prefix value occurred multiple times.

Example prefixes:

```text
0,0,0,...
```

If current state needs previous prefix:

```text
0
```

every previous occurrence creates a different subarray.

Therefore store:

```text
frequency[prefix_value]
```

not merely:

```text
seen/not seen
```

when the problem asks for number of subarrays.

Mental distinction:

```text
Does a valid previous state exist?
       -> set / boolean

How many valid previous states exist?
       -> frequency map
```

---

# 14.12 Why freq[0] Starts at 1

Before processing any array element, there is already one prefix:

```text
P[0] = 0
```

So:

```text
freq[0] = 1
```

This allows subarrays beginning at index `0` to be counted naturally.

Example:

```text
a = [3]
K = 3
```

Current prefix:

```text
cur = 3
```

Need:

```text
cur-K = 0
```

The initial empty prefix supplies that `0`.

This is not a coding trick.

It represents a real prefix state:

```text
"before the array begins"
```

---

# 14.13 Prefix Balance

Suppose binary array and you want subarrays with equal number of `0` and `1`.

Transform:

```text
0 -> -1
1 -> +1
```

Then equal zeros and ones means:

```text
subarray sum = 0
```

So:

```text
P[j] - P[i] = 0
```

Therefore:

```text
P[j] = P[i]
```

Now the problem becomes:

```text
count pairs of equal prefix balances
```

This is a beautiful example of:

```text
story condition
   ↓
element contribution
   ↓
prefix state
   ↓
equality relation
```

---

# 14.14 Prefix Parity

Suppose you care whether subarray sum is even or odd.

Only prefix parity matters:

```text
P[i] % 2
```

Subarray sum:

```text
P[j]-P[i]
```

is even when:

```text
P[j] % 2 = P[i] % 2
```

It is odd when their parities differ.

So instead of storing huge prefix sums, state can collapse to:

```text
0 or 1
```

This connects to Chapter 17 state compression later.

---

# 14.15 Prefix Remainder

Suppose you want subarrays whose sum is divisible by `m`.

Condition:

```text
(P[j]-P[i]) % m = 0
```

Therefore:

```text
P[j] % m = P[i] % m
```

So:

```text
same prefix remainder
```

defines a valid pair.

Example:

```text
m = 5
```

Store frequencies of:

```text
prefix_sum % 5
```

Each new remainder pairs with all previous identical remainders.

This combines:

```text
prefix modeling
+
modulo modeling
+
counting
```

---

# 14.16 Negative Remainders in C++

If prefix sums can be negative, normalize:

```cpp
((x % m) + m) % m
```

so remainder lies in:

```text
0 ... m-1
```

This ensures mathematically equivalent remainder classes use the same key.

---

# 14.17 Prefix XOR

XOR has a cancellation property analogous to sum.

Define:

```text
PX[i] = a[0] XOR ... XOR a[i-1]
```

Then:

```text
xor(L..R)
=
PX[R+1] XOR PX[L]
```

Why?

Because:

```text
x XOR x = 0
```

so the common prefix cancels.

Visual:

```text
PX[R+1] = common XOR wanted
PX[L]   = common

XOR them:
common XOR wanted XOR common
= wanted
```

The deeper pattern is:

```text
Find an accumulated operation
with a way to cancel the common prefix.
```

---

# 14.18 Prefix Min Is Different

Suppose:

```text
Pmin[i] = minimum of first i elements
```

Can you recover arbitrary range minimum using:

```text
Pmin[R] - Pmin[L]?
```

No.

Minimum does not have an inverse/cancellation like sum or XOR.

Example:

```text
prefix minimum
```

loses information.

So prefix subtraction works because sum has an inverse:

```text
+ ↔ -
```

XOR cancels itself.

Do not assume every operation supports arbitrary range queries with two prefixes.

---

# 14.19 Difference Array — Reverse View

Prefix answers:

```text
many range queries
```

Difference arrays help with:

```text
many range updates
```

Suppose array:

```text
a = [3,5,8,10]
```

Define difference:

```text
d[0] = a[0]
d[i] = a[i]-a[i-1]
```

Then:

```text
d = [3,2,3,2]
```

Reconstruct with prefix:

```text
3
3+2 = 5
5+3 = 8
8+2 = 10
```

So:

```text
difference
   ↓ prefix
original array
```

---

# 14.20 Difference Means Local Change

Instead of storing:

```text
actual value at every position
```

store:

```text
how much value changes when entering this position
```

Example:

```text
a:
3   5   8   10

changes:
+3  +2  +3  +2
```

Difference arrays turn a global-looking range update into two local boundary changes.

---

# 14.21 Range Addition

Suppose:

> Add `x` to every position in inclusive range `[L,R]`.

Naively:

```text
for i=L..R:
    a[i] += x
```

Difference view:

```text
d[L] += x
d[R+1] -= x
```

if `R+1` exists.

Why?

At `L`:

```text
start adding x
```

After `R`:

```text
stop adding x
```

ASCII:

```text
indices:
0 1 2 3 4 5 6

update [2,5] by +x

difference events:
    +x          -x
     ↓           ↓
0 1 [2 3 4 5]   6
     <--- +x --->
```

This is boundary/event modeling.

---

# 14.22 Why the -x at R+1?

Suppose we add:

```text
+x
```

to `d[L]`.

When prefixing differences, that `+x` would continue forever:

```text
L, L+1, L+2, ...
```

To stop its effect after `R`, place:

```text
-x
```

at:

```text
R+1
```

Then cumulative added effect becomes:

```text
before L: 0
L..R:     x
after R:  0
```

Exactly what we want.

---

# 14.23 Example — Difference Updates

Length:

```text
6
```

Initially all zero:

```text
a = [0,0,0,0,0,0]
```

Apply:

```text
+3 to [1,4]
```

Difference events:

```text
d[1] += 3
d[5] -= 3
```

So:

```text
d = [0,3,0,0,0,-3]
```

Prefix reconstruct:

```text
a =
[0,3,3,3,3,0]
```

Correct.

---

# 14.24 Multiple Range Updates

Apply:

```text
+3 to [1,4]
+2 to [3,5]
```

Difference events:

```text
at 1: +3
at 5: -3

at 3: +2
after 5: -2
```

The difference array stores only event boundaries.

After all updates:

```text
prefix once
```

to obtain final values.

Pattern:

```text
many range updates
       ↓
record start/stop events
       ↓
one final prefix pass
```

---

# 14.25 Prefix and Difference Are Dual Ideas

Prefix:

```text
local values
    ↓ accumulate
global state
```

Difference:

```text
global values
    ↓ compare neighbors
local changes
```

ASCII:

```text
difference --PREFIX--> values
values -----DIFF-----> difference
```

Think:

```text
PREFIX = integrate / accumulate
DIFFERENCE = discrete derivative / change
```

You do not need calculus, but the analogy is useful.

---

# 14.26 Event Modeling

Difference arrays are part of a broader idea:

```text
something starts at L
something stops after R
```

Represent only those events:

```text
+effect at L
-effect at R+1
```

Then scan left to right maintaining:

```text
current active effect
```

This appears in:

```text
range additions
active intervals
coverage counts
sweep-line problems
```

---

# 14.27 Coverage Count

Suppose intervals:

```text
[L1,R1]
[L2,R2]
...
```

Want number of intervals covering each integer point.

For each interval:

```text
d[L] += 1
d[R+1] -= 1
```

Prefix:

```text
coverage[x]
```

Example:

```text
[1,3]
[2,4]
```

Events:

```text
1: +1
2: +1
4: -1
5: -1
```

Coverage:

```text
point 1 -> 1
point 2 -> 2
point 3 -> 2
point 4 -> 1
```

---

# 14.28 Prefix of Prefix

Sometimes after constructing prefix sums, you need sums of prefix sums.

Define:

```text
P[i] = prefix of a
Q[i] = prefix of P
```

This can model weighted contributions or repeated accumulation.

But do not use it mechanically.

Always ask:

```text
What quantity does Q[i] represent?
```

For example, an element may contribute to many future prefixes, and prefix-of-prefix captures those repeated contributions.

---

# 14.29 Contribution Interpretation

Suppose:

```text
P[1] + P[2] + ... + P[n]
```

How many times does:

```text
a[0]
```

appear?

It appears in every prefix:

```text
n times
```

`a[1]` appears:

```text
n-1 times
```

and so on.

Thus:

```text
sum of prefixes
=
a[0]*n
+
a[1]*(n-1)
+
...
```

This connects prefix modeling with Chapter 7 contribution modeling.

---

# 14.30 2D Prefix Intuition

For a matrix, define:

```text
P[r][c]
=
sum of rectangle from origin
to before (r,c)
```

A target rectangle can be obtained by:

```text
big prefix
- top extra
- left extra
+ overlap removed twice
```

Conceptually:

```text
wanted
=
whole
- unwanted top
- unwanted left
+ double-subtracted corner
```

This is inclusion-exclusion.

ASCII:

```text
+-------------------+
| double |   top    |
| overlap|  extra   |
+--------+----------+
| left   |  WANTED  |
| extra  |          |
+--------+----------+
```

You do not need to memorize the formula first.

Derive it by removing unwanted regions and restoring the overlap.

---

# 14.31 Prefix vs Sliding Window

Both solve subarray problems, but their models differ.

### Prefix + map

Useful when:

```text
negative values exist
need exact sum relations
need count of many previous states
```

### Sliding window / two pointers

Useful when expanding/shrinking has predictable monotonic behavior.

Example:

```text
all numbers non-negative
```

then increasing right endpoint cannot decrease sum.

That monotonicity may allow a window.

Important:

```text
"subarray" does not automatically mean sliding window.
```

Translate the condition first.

---

# 14.32 Prefix vs Direct Formula

Do not build prefix arrays when there is only one simple range query.

Prefix is useful when:

```text
many queries
many subarrays
need previous accumulated states
need cancellation
```

Model first, optimize second.

---

# 14.33 Complete CF-Style Example 1 — Range Sums

Array:

```text
[4,2,7,1,3]
```

Prefix:

```text
[0,4,6,13,14,17]
```

Sum `[1,3]`:

```text
P[4]-P[1]
=
14-4
=
10
```

Check:

```text
2+7+1 = 10
```

---

# 14.34 Complete CF-Style Example 2 — Equal 0 and 1

Array:

```text
[0,1,0,1]
```

Transform:

```text
[-1,+1,-1,+1]
```

Prefixes:

```text
0,-1,0,-1,0
```

Equal prefix values imply zero-sum transformed subarrays.

Frequencies:

```text
0 occurs 3 times
-1 occurs 2 times
```

Number of pairs:

```text
C(3,2) + C(2,2)
=
3 + 1
=
4
```

So there are 4 subarrays with equal zeros and ones.

---

# 14.35 Complete CF-Style Example 3 — Divisible Sum

Want subarray sum divisible by:

```text
m = 3
```

Suppose prefix sums are:

```text
0,1,3,6,10
```

Remainders:

```text
0,1,0,0,1
```

Equal remainder pairs produce divisible subarray sums.

Counts:

```text
remainder 0 -> 3 occurrences
remainder 1 -> 2 occurrences
```

Pairs:

```text
C(3,2) + C(2,2)
=
3+1
=
4
```

---

# 14.36 Complete CF-Style Example 4 — Range Additions

Length:

```text
5
```

Updates:

```text
+2 on [0,2]
+3 on [1,4]
```

Events:

```text
first:
d[0] += 2
d[3] -= 2

second:
d[1] += 3
```

The stop for second update is after array end, so no in-array subtraction is needed if the difference array has only length 5.

Difference:

```text
[2,3,0,-2,0]
```

Prefix:

```text
[2,5,5,3,3]
```

---

# 14.37 Complete CF-Style Example 5 — Count Odd Elements in Ranges

Convert:

```text
b[i] = a[i] % 2
```

For:

```text
a = [4,7,2,9,5]
```

we get:

```text
b = [0,1,0,1,1]
```

Prefix:

```text
P = [0,0,1,1,2,3]
```

Odd count in `[1,3]`:

```text
P[4]-P[1]
=
2-0
=
2
```

The modeling trick was first converting each element into the contribution we care about.

---

# 14.38 Modeling Checklist

When you see arrays/ranges/subarrays:

```text
1. WHAT DOES EACH ELEMENT CONTRIBUTE?

2. CAN I CONVERT EACH ELEMENT
   TO A SIMPLE VALUE FIRST?

3. WHAT INFORMATION ABOUT
   "EVERYTHING BEFORE HERE"
   WOULD BE USEFUL?

4. DEFINE PREFIX STATE.

5. CAN THE RANGE BE OBTAINED
   BY CANCELLING TWO PREFIXES?

6. FOR TARGET SUBARRAY CONDITION:
   write
   P[j] - P[i] = condition

7. REARRANGE:
   what previous prefix do I need?

8. DO I NEED:
   existence?
   frequency?
   minimum/maximum index?

9. IS ONLY PARITY/REMAINDER NEEDED?

10. FOR MANY RANGE UPDATES:
    can I record only start/stop events?

11. CHECK INDEX CONVENTION:
    [L,R] or [L,R)?

12. INCLUDE THE EMPTY PREFIX
    WHEN APPROPRIATE.
```

---

# 14.39 Common Mistakes

## Mistake 1 — Memorizing `P[R]-P[L]`

The correct indices depend on your prefix definition.

Define first:

```text
P[i] = first i elements
```

Then derive:

```text
[L,R] -> P[R+1]-P[L]
```

---

## Mistake 2 — Forgetting the empty prefix

For subarrays starting at index `0`:

```text
P[0] = 0
```

is essential.

---

## Mistake 3 — Storing full sums when only remainder matters

Compress state when possible.

---

## Mistake 4 — Using sliding window with arbitrary negatives

Negative numbers can destroy the monotonic movement required by a normal sum window.

Prefix equations may still work.

---

## Mistake 5 — Difference update without stopping it

For inclusive `[L,R]`:

```text
+d at L
-d at R+1
```

The second event stops the effect.

---

## Mistake 6 — Assuming all operations cancel like sums

Prefix min/max cannot generally recover arbitrary range min/max by subtraction.

---

# 14.40 Translation Drills

Do not code.

### Drill 1

```text
sum(L..R)
```

Translate:

```text
P[R+1]-P[L]
```

### Drill 2

```text
sum(L..R) = K
```

Translate:

```text
P[R+1]-P[L] = K
```

then:

```text
P[L] = P[R+1]-K
```

### Drill 3

```text
subarray sum divisible by m
```

Translate:

```text
P[R+1] % m = P[L] % m
```

### Drill 4

```text
add x to [L,R]
```

Difference events:

```text
d[L] += x
d[R+1] -= x
```

### Drill 5

```text
equal zeros and ones
```

Transform:

```text
0 -> -1
1 -> +1
```

then seek:

```text
zero-sum subarrays
```

---

# 14.41 Practice Set

For each problem write:

```text
RAW CONDITION:
ELEMENT CONTRIBUTION:
PREFIX DEFINITION:
RANGE/SUBARRAY EQUATION:
WHAT PREVIOUS STATE IS NEEDED?:
DATA TO STORE:
```

### Practice A

Count subarrays with sum exactly `K`.

### Practice B

Count subarrays with sum divisible by `m`.

### Practice C

Answer many queries asking number of negative elements in `[L,R]`.

### Practice D

Perform many additions `+x` over ranges `[L,R]`, then output final array.

### Practice E

Find subarrays containing equal numbers of `A` and `B`.

---

# 14.42 Practice Answers

## A

```text
P[j]-P[i] = K
```

Need previous:

```text
P[i] = P[j]-K
```

Store prefix frequencies.

## B

Need:

```text
P[j] % m = P[i] % m
```

Store remainder frequencies.

## C

Transform:

```text
b[i] =
1 if a[i] < 0
0 otherwise
```

Prefix `b`.

Range answer:

```text
P[R+1]-P[L]
```

## D

For each update:

```text
d[L] += x
d[R+1] -= x
```

Then prefix the difference array once.

## E

Transform:

```text
A -> +1
B -> -1
```

Equal counts mean transformed subarray sum:

```text
0
```

Therefore count equal prefix balances.

---

# 14.43 Chapter Mastery Test

You are ready for the next chapter when you see:

```text
subarray / range / accumulated condition
```

and naturally ask:

```text
Can I represent this as
difference between two prefix states?
```

For:

```text
sum(L..R) = K
```

you should immediately be able to derive:

```text
P[R+1]-P[L] = K
```

then:

```text
P[L] = P[R+1]-K
```

You should also recognize:

```text
many range queries
      -> prefix

many range updates
      -> difference events

subarray counting
      -> pairs/frequencies of prefix states
```

---

# 14.44 Final Mental Engine

```text
                ARRAY STORY
                     │
                     ▼
          WHAT DOES EACH ELEMENT
               CONTRIBUTE?
                     │
                     ▼
              PREFIX STATE
                     │
          ┌──────────┴──────────┐
          │                     │
      range query           subarray condition
          │                     │
          ▼                     ▼
   subtract prefixes       write equation
                                │
                                ▼
                       rearrange for old prefix
                                │
                                ▼
                       set / map / frequency

                RANGE UPDATES
                     │
                     ▼
               boundary events
                     │
             +x at L, -x at R+1
                     │
                     ▼
                prefix once
                     │
                     ▼
                 final array
```

The core habit:

```text
A subarray is not an isolated object.

It is the DIFFERENCE
between two accumulated states.
```

---

# Next Chapter

```text
14. PREFIX & DIFFERENCE MODELING
                 ↓
15. COORDINATE & DISTANCE MODELING
```

Chapter 15 will focus on turning movement/geometry stories into simple numeric models:

```text
number line
absolute difference
coordinates
Manhattan distance
movement constraints
translation/normalization
meeting/reachability
grid geometry
```

The emphasis will remain on mathematical translation for Codeforces problems rather than advanced computational geometry.
