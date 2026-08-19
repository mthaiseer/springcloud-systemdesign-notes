# CP Mathematical Modeling Mini-Course

## 16. Min/Max & Extremal Modeling

> **Goal:** Learn to recognize when a problem that mentions many values is actually controlled by only a few extreme values.
>
> Core habit:
>
> ```text
> Before comparing every pair or simulating everything,
> ask:
>
> "Which smallest/largest value creates the strongest constraint?"
> ```

---

# Chapter Tree

```text
16. MIN/MAX & EXTREMAL MODELING
│
├── 16.1 What is an extremal value?
├── 16.2 Why extremes control many problems
├── 16.3 Range = maximum - minimum
├── 16.4 Maximum pairwise difference
├── 16.5 Minimum / maximum as constraints
├── 16.6 Bottleneck modeling
├── 16.7 Top-k / bottom-k candidates
├── 16.8 Top two / bottom two
├── 16.9 Excluding one element
├── 16.10 Extremes after sorting
├── 16.11 Minimize the maximum
├── 16.12 Maximize the minimum
├── 16.13 Extremal contradiction proofs
├── 16.14 Extreme contribution
├── 16.15 Extremes + greedy
├── 16.16 Extremes + binary search
└── 16.17 CF-style modeling workflow
```

Central engine:

```text
MANY VALUES
    │
    ▼
What is the objective / condition?
    │
    ▼
Which element makes it hardest?
    │
    ├── smallest?
    ├── largest?
    ├── second smallest?
    └── second largest?
    │
    ▼
Can every other value be bounded
using that extreme?
    │
   YES
    ▼
Reduce the whole problem
to a few extreme values
```

---

# 16.1 What Is an Extreme?

For:

```text
a = [7,2,10,4,8]
```

the basic extremes are:

```text
minimum = 2
maximum = 10
```

Sometimes we also need:

```text
second minimum = 4
second maximum = 8
```

or:

```text
k smallest
k largest
```

The key idea is not merely finding min/max.

It is recognizing that the answer may depend **only** on them.

---

# 16.2 Maximum Distance on a Number Line

Given positions:

```text
2,7,4,11,5
```

Naive thought:

```text
check every pair
```

But for any:

```text
x <= y
```

we know:

```text
min <= x <= y <= max
```

Therefore:

```text
y-x <= max-min
```

The extreme pair achieves:

```text
max-min
```

So:

```text
maximum pairwise distance
=
maximum - minimum
```

This is an extremal proof.

---

# 16.3 Range / Spread

For values:

```text
a1,a2,...,an
```

the spread is:

```text
max(a) - min(a)
```

If a problem asks:

```text
how far apart can two values be?
```

or:

```text
smallest interval containing all values?
```

or:

```text
difference between strongest and weakest?
```

your first candidate should be:

```text
max - min
```

---

# 16.4 Extreme Constraint

Suppose every value must satisfy:

```text
a[i] <= X
```

You do not need to check the full array conceptually.

The strongest condition is:

```text
max(a) <= X
```

Similarly:

```text
a[i] >= X for all i
```

is equivalent to:

```text
min(a) >= X
```

This is a major modeling compression:

```text
n inequalities
    ↓
one extreme inequality
```

---

# 16.5 "For All" Often Means Look at the Worst Case

Statement:

> Every worker must finish within T hours.

If worker `i` requires:

```text
time[i]
```

then condition:

```text
time[i] <= T
for every i
```

is equivalent to:

```text
max(time) <= T
```

Whenever you see:

```text
for every
all elements
no element may exceed
everyone must satisfy
```

ask:

```text
Which element is hardest to satisfy?
```

That is the bottleneck.

---

# 16.6 Bottleneck Modeling

Suppose several tasks finish in:

```text
3,7,5,11,4
```

and all tasks run independently in parallel.

When are all complete?

Not:

```text
sum = 30
```

but:

```text
max = 11
```

because the slowest task determines completion.

Visual:

```text
task A: ###
task B: #######
task C: #####
task D: ###########
task E: ####

all finished
           ^
           |
       longest task
```

The answer is controlled by the bottleneck.

---

# 16.7 Sum vs Max

A common modeling decision:

### Sequential work

If all work must happen one after another:

```text
total time = sum(times)
```

### Parallel work

If work happens simultaneously and you wait for all:

```text
total time = max(times)
```

This simple distinction appears constantly in story problems.

---

# 16.8 Minimize the Maximum

Suppose you choose a configuration and its quality is determined by the worst value:

```text
cost =
max(c1,c2,...,ck)
```

Objective:

```text
minimize cost
```

This is:

```text
minimize the maximum
```

Mental questions:

```text
What creates the current maximum?

Can I reduce that bottleneck?

Can I test whether maximum <= X is feasible?
```

The last question often leads to binary search on answer.

---

# 16.9 Maximize the Minimum

Similarly:

```text
score =
min(s1,s2,...,sk)
```

and you want:

```text
maximize score
```

Then the weakest component controls the answer.

Ask:

```text
Can I make every component >= X?
```

This converts:

```text
maximize minimum
```

into a feasibility question.

Pattern:

```text
maximize minimum
       ↓
Can minimum >= X?
       ↓
binary search X
```

when feasibility is monotonic.

---

# 16.10 Extremes After Sorting

Sorted:

```text
a[0] <= a[1] <= ... <= a[n-1]
```

Now extremes become explicit:

```text
minimum        = a[0]
second minimum = a[1]

maximum        = a[n-1]
second maximum = a[n-2]
```

Many problems that seem to require arbitrary combinations only need a few boundary values after sorting.

But do not sort automatically if a linear scan can find the required extremes.

---

# 16.11 Why Second Extreme Appears

Suppose you need the maximum value **excluding one particular maximum element**.

If the excluded element is the unique maximum:

```text
answer = second maximum
```

Otherwise:

```text
answer = maximum
```

This is why problems involving deletion/exclusion often require:

```text
largest
second largest
count of largest
```

rather than the whole sorted array.

---

# 16.12 Example — Maximum Excluding Each Index

Array:

```text
[3,8,5,8,2]
```

Maximum:

```text
8
```

It occurs twice.

If you remove either `8`, another `8` remains.

So maximum after removing any single index is still:

```text
8
```

Now:

```text
[3,8,5,6,2]
```

Maximum:

```text
8
```

unique.

If you remove that `8`:

```text
new maximum = 6
```

So useful state:

```text
max1
max2
frequency(max1)
```

---

# 16.13 Top Two Candidates

Why do "top two" frequently appear?

Suppose a choice cannot use the same element twice.

If the best element is unavailable for one role, the next possible candidate is often:

```text
second best
```

Example:

```text
choose two different values
to maximize their sum
```

Clearly:

```text
largest + second largest
```

Why?

Any pair cannot contain two values greater than those top two, because no such values exist.

---

# 16.14 Bottom Two Candidates

Similarly:

```text
minimum sum of two distinct elements
=
smallest + second smallest
```

assuming ordinary unrestricted pair selection.

This seems trivial, but it illustrates the extremal method:

```text
objective over huge candidate set
       ↓
prove only boundary candidates matter
```

---

# 16.15 Maximum Product Needs More Care

Suppose integers may be negative.

To maximize product of two elements, do **not** blindly choose two largest.

Example:

```text
[-10,-9,2,3]
```

Products:

```text
3*2 = 6
(-10)*(-9) = 90
```

Therefore candidate maximum product is:

```text
max(
    largest * second_largest,
    smallest * second_smallest
)
```

Why only these?

A large positive product can come from:

```text
two largest positives
```

or:

```text
two most negative values
```

This is a classic "few extreme candidates" model.

---

# 16.16 Extremes Depend on the Expression

Do not mechanically use min/max.

Inspect the expression.

For:

```text
a[i] - a[j]
```

maximum comes from:

```text
max(a) - min(a)
```

For:

```text
a[i] + a[j]
```

maximum comes from:

```text
two largest
```

For:

```text
a[i] * a[j]
```

with negatives:

```text
two largest OR two smallest
```

The algebra determines which extremes can dominate.

---

# 16.17 Absolute Difference

Maximum:

```text
|a[i]-a[j]|
```

is:

```text
max(a)-min(a)
```

because the greatest separation is between the two extremes.

Minimum absolute difference is different.

After sorting, it is found among adjacent elements:

```text
min(
 a[1]-a[0],
 a[2]-a[1],
 ...
)
```

Why adjacent?

If:

```text
a <= b <= c
```

then:

```text
c-a >= b-a
c-a >= c-b
```

So a non-adjacent pair cannot beat both gaps inside it.

This is an extremal + sorting argument.

---

# 16.18 Example — Closest Pair on Number Line

Values:

```text
8,1,14,6,4
```

Sort:

```text
1,4,6,8,14
```

Adjacent gaps:

```text
3,2,2,6
```

Minimum:

```text
2
```

No need to test all pairs.

This is important:

```text
maximum distance -> global extremes

minimum distance -> neighboring values after sorting
```

Different objective, different extremal structure.

---

# 16.19 Removing One Extreme

Suppose spread is:

```text
max-min
```

and you may remove exactly one element to minimize spread.

Which removals could matter?

Removing an interior element does not change:

```text
min
max
```

therefore spread stays the same.

Only useful candidates are:

```text
remove current minimum
remove current maximum
```

After sorting:

```text
candidate 1 = a[n-1] - a[1]
candidate 2 = a[n-2] - a[0]
```

Answer:

```text
min(candidate1,candidate2)
```

This is a powerful extremal reduction.

---

# 16.20 General Principle — Interior Values May Be Irrelevant

If objective depends only on:

```text
minimum
maximum
```

then changing/removing an interior value may do nothing.

Ask:

```text
Which elements can actually change the objective?
```

This can shrink:

```text
n possible actions
```

to:

```text
2 or 4 meaningful candidates
```

---

# 16.21 Extremal Contradiction

A useful proof style:

Assume some solution violates your claimed extreme bound.

Example:

Claim:

```text
maximum pair difference <= max-min
```

Suppose there exists:

```text
a[j]-a[i] > max-min
```

But:

```text
a[j] <= max
a[i] >= min
```

so:

```text
a[j]-a[i] <= max-min
```

Contradiction.

This style is often very short and useful in CF reasoning.

---

# 16.22 Choose the Hardest Element First

Suppose each resource/action must handle every requirement.

Requirements:

```text
r[0],r[1],...,r[n-1]
```

If capability:

```text
C
```

must satisfy all:

```text
C >= r[i]
```

then only strongest requirement matters:

```text
C >= max(r)
```

Instead of thinking about all elements, test the hardest one.

This is the extremal version of constraint compression.

---

# 16.23 Choose the Weakest Guarantee

Suppose every selected item gives some quality:

```text
q[i]
```

and group quality is:

```text
minimum q[i]
```

Then adding a very strong item does not help if the weakest stays unchanged.

So optimization effort should focus on:

```text
raising the minimum
```

not making already-large values larger.

This is useful for understanding max-min problems.

---

# 16.24 Extreme Contributions

Sometimes an element's contribution depends on how many values are smaller/larger.

After sorting:

```text
a[0] <= ... <= a[n-1]
```

an extreme element may participate in many maximum/minimum expressions.

Example:

In every pair containing the global maximum:

```text
max(pair) = global maximum
```

This can simplify counting contributions.

This connects Chapter 16 with contribution modeling.

---

# 16.25 Extremes + Greedy

Suppose a problem asks to repeatedly reduce the largest value.

Natural candidate:

```text
always operate on current maximum
```

But do not accept it automatically.

Ask:

```text
Does reducing anything smaller help the current bottleneck?

Can an exchange argument show
working on the maximum is safe?
```

Extremes often suggest greedy choices, but the greedy still needs proof.

---

# 16.26 Extremes + Heap

If the extreme changes repeatedly:

```text
take current maximum
modify it
put it back
repeat
```

a heap may be the right data structure.

Model first:

```text
decision always depends on current extreme
```

Then implementation:

```text
priority_queue
```

Do not start from "this looks like heap."

Start from the mathematical dependency.

---

# 16.27 Extremes + Binary Search

Suppose objective:

```text
minimize maximum load
```

Try a candidate answer:

```text
X
```

Question:

```text
Can every load be made <= X?
```

If:

```text
X works
```

then any larger value usually works.

So feasibility may look:

```text
false false false true true true
```

This monotonicity allows binary search.

Similarly:

```text
maximize minimum distance
```

test:

```text
Can every required distance be >= X?
```

This is a common 1500-1900 modeling transition.

---

# 16.28 Example — Split Work

Suppose `n` tasks must be assigned so that maximum workload is minimized.

Instead of directly constructing the optimal maximum, ask:

```text
For a proposed limit X,
can I assign tasks so no workload exceeds X?
```

Necessary immediate bound:

```text
X >= max(single_task)
```

because no assignment can hide a task larger than `X`.

The largest individual task gives the first lower bound.

Extremes frequently provide search bounds.

---

# 16.29 Lower and Upper Bounds From Extremes

For many optimization problems:

```text
answer >= some extreme
answer <= some other simple extreme
```

Example partitioning positive values into groups:

```text
lower bound = max element
upper bound = sum of all elements
```

Why?

No group containing the largest element can have sum below it.

Putting everything into one group gives total sum.

So:

```text
max(a) <= answer <= sum(a)
```

These bounds are useful for binary search.

---

# 16.30 Min/Max Algebra

Useful identities:

```text
max(a,b)
=
(a+b+|a-b|)/2
```

```text
min(a,b)
=
(a+b-|a-b|)/2
```

You rarely need these directly in basic CP, but they show the relationship between:

```text
min/max
absolute difference
```

Another useful identity:

```text
max(a,b) - min(a,b)
=
|a-b|
```

---

# 16.31 Extremal Pigeonhole Intuition

Sometimes extremes combine with counting.

Suppose `n` sorted numbers lie between:

```text
L and R
```

There are:

```text
n-1
```

adjacent gaps whose total is:

```text
R-L
```

Therefore at least one gap is at most roughly the average:

```text
(R-L)/(n-1)
```

and at least one gap is at least the average.

This is an extremal/averaging argument.

It can prove existence without finding the exact object immediately.

---

# 16.32 Complete CF-Style Example 1 — Maximum Difference

Array:

```text
[5,11,2,9,4]
```

Minimum:

```text
2
```

Maximum:

```text
11
```

Maximum absolute pair difference:

```text
11-2 = 9
```

---

# 16.33 Complete CF-Style Example 2 — Remove One to Minimize Spread

Sorted:

```text
[1,3,7,8,10]
```

Current spread:

```text
10-1 = 9
```

Only useful removals:

```text
remove 1:
10-3 = 7

remove 10:
8-1 = 7
```

Removing:

```text
3,7,8
```

leaves extremes:

```text
1 and 10
```

so spread remains:

```text
9
```

Answer:

```text
7
```

---

# 16.34 Complete CF-Style Example 3 — Maximum Product of Two

Array:

```text
[-12,-8,1,4,7]
```

Largest two:

```text
7*4 = 28
```

Smallest two:

```text
(-12)*(-8) = 96
```

Maximum:

```text
96
```

Only four extreme values were relevant.

---

# 16.35 Complete CF-Style Example 4 — Parallel Completion

Machines finish in:

```text
[4,9,3,7]
```

All start together.

Completion time:

```text
max = 9
```

The slowest machine is the bottleneck.

---

# 16.36 Complete CF-Style Example 5 — Minimum Pair Difference

Array:

```text
[12,3,8,4,20]
```

Sort:

```text
3,4,8,12,20
```

Adjacent gaps:

```text
1,4,4,8
```

Minimum difference:

```text
1
```

No non-adjacent pair can be closer than all adjacent gaps between them.

---

# 16.37 Modeling Checklist

When you see an optimization or all-elements condition:

```text
1. WHAT IS THE OBJECTIVE?

2. DOES IT USE:
   max?
   min?
   absolute difference?
   worst case?
   weakest element?

3. WHICH ELEMENT CREATES
   THE STRONGEST CONSTRAINT?

4. CAN "FOR ALL i" BECOME:
   max <= X
   or
   min >= X?

5. DO I NEED:
   min/max only?
   top two?
   bottom two?
   counts of extremes?

6. IF ONE ELEMENT IS REMOVED,
   WHICH removals can actually
   change the objective?

7. AFTER SORTING, CAN ONLY
   boundary/adjacent candidates matter?

8. FOR MIN-MAX / MAX-MIN:
   can I test candidate X?

9. WHAT SIMPLE LOWER/UPPER
   BOUNDS COME FROM EXTREMES?

10. CAN I PROVE ALL OTHER
    CANDIDATES ARE DOMINATED?
```

---

# 16.38 Common Mistakes

## Mistake 1 — Checking all pairs for maximum difference

Usually:

```text
max-min
```

is enough.

---

## Mistake 2 — Using two largest for maximum product with negatives

Also test:

```text
two smallest
```

---

## Mistake 3 — Sorting when only min/max are needed

A single scan is:

```text
O(n)
```

instead of:

```text
O(n log n)
```

---

## Mistake 4 — Thinking every removal matters

If the objective depends only on extremes, interior removals may be irrelevant.

---

## Mistake 5 — Confusing min-max with max-min

```text
minimize maximum
```

focuses on reducing the worst value.

```text
maximize minimum
```

focuses on improving the weakest value.

---

## Mistake 6 — Seeing an extreme and assuming greedy

An extreme may identify the bottleneck, but a repeated greedy action still needs proof.

---

# 16.39 Translation Drills

Do not code.

### Drill 1

```text
maximum |a[i]-a[j]|
```

Translate:

```text
max(a)-min(a)
```

### Drill 2

```text
a[i] <= X for every i
```

Translate:

```text
max(a) <= X
```

### Drill 3

```text
a[i] >= X for every i
```

Translate:

```text
min(a) >= X
```

### Drill 4

```text
maximum product of two integers
```

Candidates:

```text
largest * second largest
smallest * second smallest
```

### Drill 5

```text
remove one element to reduce max-min
```

First candidates:

```text
remove minimum
remove maximum
```

---

# 16.40 Practice Set

For each problem write:

```text
OBJECTIVE:
EXTREME THAT CONTROLS IT:
OTHER EXTREMES NEEDED:
WHY INTERIOR VALUES DO/DON'T MATTER:
CANDIDATE FORMULA:
PROOF IDEA:
```

### Practice A

Find maximum absolute difference between two array elements.

### Practice B

Find maximum product of two distinct elements, values may be negative.

### Practice C

Remove one element to minimize the remaining range.

### Practice D

Determine whether every element is at most `K`.

### Practice E

Find the minimum absolute difference between two distinct values.

---

# 16.41 Practice Answers

## A

```text
max(a)-min(a)
```

## B

```text
max(
 max1*max2,
 min1*min2
)
```

## C

After sorting:

```text
min(
 a[n-1]-a[1],
 a[n-2]-a[0]
)
```

for the normal case `n >= 2`, with small `n` handled according to the problem definition.

## D

Equivalent to:

```text
max(a) <= K
```

## E

Sort and inspect adjacent differences:

```text
min(a[i]-a[i-1])
```

---

# 16.42 Chapter Mastery Test

You are ready for the next chapter when you see:

```text
all pairs
all elements
worst case
maximum difference
minimum possible maximum
maximum possible minimum
```

and immediately ask:

```text
"Which extreme controls this?"
```

You should be able to distinguish:

```text
maximum pair distance
    -> min + max

maximum pair sum
    -> top two

maximum pair product with negatives
    -> top two OR bottom two

minimum pair distance
    -> adjacent after sorting

remove one to change range
    -> remove one of the extremes
```

---

# 16.43 Final Mental Engine

```text
                 MANY VALUES
                      │
                      ▼
             WRITE THE OBJECTIVE
                      │
                      ▼
              FIND THE BOTTLENECK
                      │
       ┌──────────────┼──────────────┐
       │              │              │
    smallest       largest       adjacency
       │              │              │
       ▼              ▼              ▼
  weakest bound   worst bound    closest pair
       │              │              │
       └──────────────┼──────────────┘
                      ▼
            DO I NEED MORE EXTREMES?
                      │
          top2 / bottom2 / counts
                      │
                      ▼
         ELIMINATE INTERIOR CANDIDATES
                      │
                      ▼
               SMALL FORMULA /
             SMALL CANDIDATE SET
```

The core habit:

```text
When the answer is controlled by
the best, worst, closest, or farthest case,
do not treat every value equally.

Find the element that controls the bound.
```

---

# Next Chapter

```text
16. MIN/MAX & EXTREMAL MODELING
                 ↓
17. STATE COMPRESSION & EQUIVALENCE MODELING
```

Chapter 17 will focus on recognizing when the full value is unnecessary and only a small property matters:

```text
parity
remainder
sign
comparison state
frequency class
difference/balance
equivalent states
```

This is one of the most useful ways to turn complicated CF stories into tiny mathematical state spaces.
