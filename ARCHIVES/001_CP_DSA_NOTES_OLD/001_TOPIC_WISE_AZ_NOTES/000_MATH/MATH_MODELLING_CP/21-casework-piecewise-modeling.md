# CP Mathematical Modeling Mini-Course

## 20. Casework & Piecewise Mathematical Modeling

> **Goal:** Learn what to do when one formula or one argument does **not** work for every input.
>
> Many Codeforces problems become simple after dividing the input into a few mathematically meaningful cases:
>
> ```text
> one complicated problem
>          ↓
> identify the property that changes behavior
>          ↓
> split into small cases
>          ↓
> solve each case cleanly
>          ↓
> combine
> ```

---

# Chapter Tree

```text
20. CASEWORK & PIECEWISE MODELING
│
├── 20.1 What is casework?
├── 20.2 Why formulas become piecewise
├── 20.3 Find the property that changes behavior
├── 20.4 Split by parity
├── 20.5 Split by sign
├── 20.6 Split by ordering
├── 20.7 Split by equality
├── 20.8 Split by divisibility / remainder
├── 20.9 Split by boundaries
├── 20.10 Small-state exhaustive casework
├── 20.11 Symmetry to remove cases
├── 20.12 Normalize before casework
├── 20.13 Derive one formula per region
├── 20.14 Merge equivalent cases
├── 20.15 Avoid case explosion
├── 20.16 Proving complete coverage
└── 20.17 CF-style workflow
```

---

# 20.1 What Is Casework?

Suppose the answer behaves differently depending on whether `n` is odd or even.

Then there may be no useful single expression at first.

Instead:

```text
if n is even:
    solve using rule A

if n is odd:
    solve using rule B
```

Mathematically:

```text
        { f_even(n), if n is even
f(n) = {
        { f_odd(n),  if n is odd
```

This is **piecewise modeling**.

The goal is not to create many `if` statements.

The goal is to identify the **small number of structural situations** in which the mathematics changes.

---

# 20.2 Why Does Behavior Change?

A case split is justified when some property changes:

```text
available operation
sign
parity
ordering
boundary
remainder
number of remaining objects
```

Example:

```text
|x|
```

has different algebra depending on sign:

```text
|x| = x     if x >= 0
|x| = -x    if x < 0
```

The absolute value itself tells us where the mathematical behavior changes:

```text
x = 0
```

That point is a **boundary between cases**.

---

# 20.3 Do Not Split Randomly

Bad casework:

```text
if n < 10
if n < 100
if n < 1000
...
```

unless those thresholds have mathematical meaning.

Good casework:

```text
n even / odd
x < 0 / x = 0 / x > 0
a < b / a = b / a > b
remainder = 0 / non-zero
inside interval / outside interval
```

Rule:

```text
Every case split should answer:

"Why does the behavior change here?"
```

---

# 20.4 The Casework Engine

```text
FULL INPUT SPACE
       │
       ▼
What property changes the formula/operation?
       │
       ▼
partition input space
       │
 ┌─────┼─────┐
 │     │     │
case A case B case C
 │     │     │
solve solve solve
 │     │     │
 └─────┼─────┘
       ▼
verify cases:
- cover everything
- do not conflict
       │
       ▼
merge equivalent cases
```

---

# 20.5 Split by Parity

Parity creates only two states:

```text
EVEN
ODD
```

This makes it one of the most common CF case splits.

Example:

> Split `n` into two positive integers with the same parity.

Let:

```text
n = a+b
```

If `a` and `b` have the same parity:

```text
even + even = even
odd  + odd  = even
```

Therefore:

```text
n must be even
```

Now split:

```text
n odd:
    impossible

n even:
    possibly construct
```

But even case may require further boundary checking.

---

# 20.6 Parity + Boundary

Suppose both parts must be positive and even.

Need:

```text
n = a+b
a >= 2
b >= 2
```

Therefore:

```text
n >= 4
```

and:

```text
n even
```

So the full condition is:

```text
n even AND n >= 4
```

Notice how two modeling tools combine:

```text
parity
+
lower bound
```

Casework often combines several earlier chapters.

---

# 20.7 Split by Sign

Suppose operation or formula contains:

```text
|a-b|
```

Define:

```text
d = a-b
```

Then:

```text
if d >= 0:
    |a-b| = a-b

if d < 0:
    |a-b| = b-a
```

Equivalent split:

```text
a >= b
a < b
```

Sign casework often removes absolute values and makes equations linear.

---

# 20.8 Three-Way Sign Split

Sometimes zero behaves specially.

Use:

```text
x < 0
x = 0
x > 0
```

Example product sign:

```text
zero exists
    → product = 0

no zero + odd negative count
    → product < 0

no zero + even negative count
    → product > 0
```

Here the useful states are not actual values but:

```text
ZERO
NEGATIVE
POSITIVE
```

This connects casework with state compression.

---

# 20.9 Split by Ordering

For two values:

```text
a < b
a = b
a > b
```

can produce different operation requirements.

Example:

> Starting from `a`, reach `b` using only `+d`.

Then:

```text
a > b:
    impossible

a = b:
    0 operations

a < b:
    need (b-a) divisible by d
```

This is clean mathematical casework.

---

# 20.10 Normalize to Remove Ordering Cases

Sometimes:

```text
a < b
```

and:

```text
a > b
```

are symmetric.

Instead of solving both, normalize:

```text
small = min(a,b)
large = max(a,b)
```

Now:

```text
small <= large
```

always.

Difference:

```text
large-small
=
|a-b|
```

Three cases may collapse into one.

This is extremely important:

```text
Before adding cases,
ask whether symmetry can remove them.
```

---

# 20.11 Example — Distance

Without normalization:

```text
if a < b:
    distance = b-a

if a > b:
    distance = a-b

if a == b:
    distance = 0
```

After normalization:

```text
distance = |a-b|
```

or:

```text
large-small
```

No casework needed.

Good modeling often **eliminates** cases.

---

# 20.12 Split by Equality

Equality is often a boundary case.

Example:

```text
a = b
```

may mean:

```text
already solved
0 operations
special output
```

while:

```text
a != b
```

requires actual work.

Always test:

```text
already at target?
```

This is one of the first boundary cases to check in operation problems.

---

# 20.13 Split by Remainder

Modulo naturally partitions integers.

For modulo `m`:

```text
n % m = 0
n % m = 1
...
n % m = m-1
```

Example:

> Minimum amount to add to `n` to make it divisible by `m`.

Let:

```text
r = n % m
```

Case:

```text
r = 0:
    add 0
```

Otherwise:

```text
add m-r
```

Piecewise:

```text
add =
0       if r=0
m-r     otherwise
```

---

# 20.14 Merge the Modulo Cases

The previous piecewise expression can be written:

```text
add = (m-r) % m
```

Since:

```text
r = n%m
```

we get:

```text
add = (m - n%m) % m
```

This illustrates an important workflow:

```text
casework
   ↓
understand behavior
   ↓
notice cases follow same structure
   ↓
merge into compact formula
```

Casework can be a **discovery tool**, even when final code has no `if`.

---

# 20.15 Split by Divisibility

Suppose operation removes chunks of size `k`.

Then:

```text
n % k = 0
```

and:

```text
n % k != 0
```

may behave differently.

Example number of groups of capacity `k`:

```text
n/k
```

when divisible.

Otherwise:

```text
n/k + 1
```

This gives:

```text
floor division + leftover case
```

which merges into:

```text
ceil(n/k)
```

For positive integers:

```text
(n+k-1)/k
```

Again:

```text
casework → formula
```

---

# 20.16 Boundary Cases

A formula often works in the middle but fails at extremes.

Common boundaries:

```text
n = 1
n = 2
empty structure
first index
last index
minimum allowed value
maximum allowed value
zero remaining resource
already at target
```

Boundary questions:

```text
Does my operation have a neighbor?

Does a pair exist?

Can I remove two objects?

Can I divide by this quantity?

Does my general construction require n >= 2?
```

---

# 20.17 Example — Pairing

Suppose construction repeatedly pairs objects.

For:

```text
n even
```

everything can be paired.

For:

```text
n odd
```

one object remains.

So the structural split is:

```text
even:
    all objects form pairs

odd:
    pairs + one leftover
```

The leftover may require:

```text
special handling
```

or make the construction impossible.

---

# 20.18 Boundary First, General Case Second

A clean contest strategy:

```text
1. Handle tiny/special cases.
2. Then assume useful general conditions.
3. Solve the general case without repeatedly
   checking those exceptions.
```

Example:

```cpp
if (n == 1) {
    ...
    return 0;
}

// From here, n >= 2.
```

This can simplify both reasoning and implementation.

But only create special cases that are genuinely needed.

---

# 20.19 Split by Relative Position to an Interval

Suppose interval:

```text
[L,R]
```

and point:

```text
x
```

There are three natural regions:

```text
x < L
L <= x <= R
x > R
```

Distance from `x` to the interval:

```text
if x < L:
    L-x

if L <= x <= R:
    0

if x > R:
    x-R
```

ASCII:

```text
      L================R
------|================|------
  x< L      inside        x>R
```

This is a classic piecewise model.

---

# 20.20 Interval Formula From Casework

The previous distance can also be written conceptually as:

```text
max(L-x, 0, x-R)
```

Check:

```text
x < L:
L-x positive
x-R negative
→ L-x

inside:
both outside distances <= 0
→ 0

x > R:
x-R positive
→ x-R
```

Casework can reveal a min/max formula.

---

# 20.21 Split by Which Constraint Is Active

Suppose:

```text
answer = min(A,B)
```

Then behavior depends on:

```text
A <= B
```

or:

```text
B < A
```

Example resource production:

```text
k <= floor(R/2)
k <= floor(B/3)
```

Therefore:

```text
k = min(R/2, B/3)
```

You could think piecewise:

```text
if red is bottleneck:
    answer = R/2

if blue is bottleneck:
    answer = B/3
```

The `min` expression compresses the cases.

This connects casework with extremal modeling.

---

# 20.22 Bottleneck Casework

Whenever multiple constraints bound the same variable:

```text
x <= A
x <= B
x <= C
```

the active case is whichever bound is smallest:

```text
x <= min(A,B,C)
```

Instead of manually writing:

```text
if A smallest...
if B smallest...
if C smallest...
```

use:

```text
min
```

This avoids unnecessary case explosion.

---

# 20.23 Split by Maximum

Likewise:

```text
x >= A
x >= B
x >= C
```

means:

```text
x >= max(A,B,C)
```

The active lower bound depends on which requirement is largest.

Again, `max` merges several cases.

---

# 20.24 Small-State Exhaustive Casework

Sometimes after compression, there are only a few states.

Example two parity bits:

```text
a parity ∈ {E,O}
b parity ∈ {E,O}
```

Only four cases:

```text
(E,E)
(E,O)
(O,E)
(O,O)
```

A table may reveal the pattern faster than algebra.

Example sum parity:

```text
a   b   a+b
------------
E   E    E
E   O    O
O   E    O
O   O    E
```

Pattern:

```text
same parity
→ even sum

different parity
→ odd sum
```

Four cases collapse into one relation.

---

# 20.25 Operation-Effect Tables

For small state spaces, make a table.

Suppose operation selects two parity states.

```text
before       after/result
-------------------------
E,E          ...
E,O          ...
O,E          ...
O,O          ...
```

This is often easier than trying to mentally simulate all possibilities.

Tables are especially useful for:

```text
parity
sign
remainder mod 2/3
small category states
```

---

# 20.26 Exhaustive Casework Is Not Brute Force

If mathematical state compression reduces the world to:

```text
4 states
```

checking all 4 is perfectly good reasoning.

Do not confuse:

```text
exhausting a constant number of mathematical states
```

with:

```text
brute forcing n input elements/combinations
```

CF solutions frequently rely on tiny exhaustive state analysis.

---

# 20.27 Casework by Number of Special Objects

Suppose only a few objects have a special property.

Useful split:

```text
count = 0
count = 1
count >= 2
```

Why?

Behavior may change after enough special objects exist.

Example:

```text
0 negatives
1 negative
2+ negatives
```

or:

```text
0 zeros
at least 1 zero
```

Do not automatically create cases for every exact count.

Group counts that behave identically.

---

# 20.28 Threshold Casework

Suppose you need at least `k` resources.

Then:

```text
available < k
```

and:

```text
available >= k
```

are the meaningful regions.

The threshold:

```text
k
```

is where feasibility changes.

This is the simplest form of piecewise feasibility.

---

# 20.29 Piecewise Linear Behavior

Many CP formulas are linear inside regions.

Example:

```text
f(x) = |x-a| + |x-b|
```

Assume:

```text
a <= b
```

Split number line:

```text
x <= a
a <= x <= b
x >= b
```

### Case 1

```text
x <= a
```

Then:

```text
f(x)
= (a-x)+(b-x)
= a+b-2x
```

### Case 2

```text
a <= x <= b
```

Then:

```text
f(x)
= (x-a)+(b-x)
= b-a
```

### Case 3

```text
x >= b
```

Then:

```text
f(x)
= (x-a)+(x-b)
= 2x-a-b
```

The absolute-value expression becomes simple after splitting at its breakpoints.

---

# 20.30 Breakpoints

A **breakpoint** is a value where the formula's behavior changes.

Common breakpoints:

```text
0
L
R
a
b
k
multiples of m
```

For absolute values:

```text
|x-a|
```

breakpoint:

```text
x=a
```

For:

```text
min(x,k)
```

breakpoint:

```text
x=k
```

For:

```text
floor(x/k)
```

behavior changes at multiples of `k`.

When a formula looks complicated, ask:

```text
Where does its definition change?
```

Those are natural case boundaries.

---

# 20.31 Normalize Before Splitting

Suppose problem is symmetric in `a,b`.

First:

```text
if (a > b)
    swap(a,b);
```

Now assume:

```text
a <= b
```

Then derive cases under that assumption.

This can cut the number of cases in half.

Mathematical version:

```text
Let:
L = min(a,b)
R = max(a,b)
```

Normalization is one of the strongest defenses against case explosion.

---

# 20.32 Sort to Normalize Many Values

For three values:

```text
a,b,c
```

there are six possible orderings.

Instead of six cases:

```text
sort them
```

and assume:

```text
a <= b <= c
```

Now reason about:

```text
smallest
middle
largest
```

rather than original names.

If the problem is symmetric under permutation, this loses nothing.

---

# 20.33 Symmetry

Two cases are symmetric if swapping labels transforms one into the other.

Example:

```text
a > b
```

and:

```text
b > a
```

If rules treat `a` and `b` identically, solve one case and mirror it.

Write:

```text
WLOG assume a <= b
```

where WLOG means:

```text
without loss of generality
```

Use this only when the symmetry is real.

---

# 20.34 When WLOG Is Invalid

Suppose operation treats variables differently:

```text
a += 1
b += 2
```

Then swapping `a` and `b` changes the problem.

You cannot simply assume:

```text
a <= b
```

unless you also correctly transform the operations.

Before using symmetry, ask:

```text
Are the roles interchangeable?
```

---

# 20.35 Case Explosion

Bad modeling:

```text
if a even and b even and c even...
if a even and b even and c odd...
...
```

For `k` binary properties:

```text
2^k
```

cases appear.

Before enumerating them, search for an aggregate:

```text
number of odd values
sum parity
xor
frequency
minimum/maximum
```

Example:

Instead of 8 parity cases for 3 numbers, sum parity depends only on:

```text
oddCount % 2
```

Compression beats case explosion.

---

# 20.36 Merge Equivalent Cases

Suppose analysis gives:

```text
case A → answer 1
case B → answer 1
case C → answer 2
case D → answer 1
```

Then ask what A/B/D share.

Maybe:

```text
all have at least one even number
```

Then replace three cases with one property.

Your first case split is not necessarily your final formula.

---

# 20.37 Casework as a Discovery Tool

Use this workflow:

```text
1. enumerate tiny meaningful states
2. solve each
3. compare answers
4. detect common property
5. replace table by general rule
```

This is especially effective for CF 800–1400 problems.

Example:

```text
E,E → YES
E,O → NO
O,E → NO
O,O → YES
```

Observe:

```text
same parity → YES
```

Final solution has no four-case table.

---

# 20.38 Casework + Algebra

Suppose:

```text
|a-b| = k
```

Split:

```text
a >= b:
a-b = k
→ a = b+k

a < b:
b-a = k
→ b = a+k
```

Or merge:

```text
difference magnitude = k
```

Casework can help derive algebra, then algebra can remove casework.

---

# 20.39 Casework + Modulo

Suppose behavior repeats every 3.

Instead of cases for every `n`, use:

```text
r = n % 3
```

Then only:

```text
r=0
r=1
r=2
```

Example:

```text
n:
1 2 3 4 5 6 7 8 9 ...

r:
1 2 0 1 2 0 1 2 0 ...
```

Infinite input space becomes three cases.

---

# 20.40 Casework + Constructive Modeling

Suppose a construction uses pairs.

Then:

```text
n even
```

may use only pairs.

For:

```text
n odd
```

you need:

```text
one special block
+
remaining pairs
```

Example architecture:

```text
if n < minimum:
    impossible

if n even:
    repeat block A

if n odd:
    use block B once
    then repeat block A
```

This is a very common constructive pattern.

---

# 20.41 Casework + Greedy

A greedy decision may depend on state.

Example:

```text
if resource A is scarcer:
    consume B differently

if resource B is scarcer:
    symmetric choice
```

Often this can be simplified by sorting/normalizing resources:

```text
small = min(A,B)
large = max(A,B)
```

Then one greedy rule handles both.

---

# 20.42 Casework + Extremes

Suppose maximum product of two array elements.

Cases might initially seem:

```text
positive-positive
negative-negative
positive-negative
zero
```

But extremal modeling compresses this to:

```text
candidate 1:
largest * second largest

candidate 2:
smallest * second smallest

answer:
max(candidate1,candidate2)
```

This is a perfect example of:

```text
case reasoning
      ↓
identify only relevant extremes
      ↓
merge into compact formula
```

---

# 20.43 Casework + Feasibility

Suppose there are two operation types, and one is available only when:

```text
x >= k
```

Then the state naturally splits:

```text
x < k
x >= k
```

This threshold changes the legal move set.

Case boundaries should often come directly from:

```text
where available actions change
```

---

# 20.44 Casework + State Compression

Suppose operation behavior depends only on:

```text
x % 2
```

Do not case on:

```text
x=1
x=2
x=3
...
```

Case on:

```text
EVEN
ODD
```

General principle:

```text
CASEWORK SHOULD HAPPEN
ON THE SMALLEST RELEVANT STATE.
```

This is one of the most important lessons of this chapter.

---

# 20.45 Complete Example — Reach Target With +d

Problem model:

```text
start = a
target = b
operation:
a += d
```

Case 1:

```text
a = b
```

Answer:

```text
0
```

Case 2:

```text
a > b
```

Impossible because value only increases.

Case 3:

```text
a < b
```

Need:

```text
b-a = kd
```

for integer:

```text
k >= 0
```

So:

```text
(b-a) % d = 0
```

Then:

```text
k = (b-a)/d
```

Final piecewise model:

```text
if b < a:
    impossible

else if (b-a)%d != 0:
    impossible

else:
    answer = (b-a)/d
```

Notice `a=b` is automatically handled:

```text
b-a = 0
```

So after algebra, three cases reduce to two checks.

---

# 20.46 Complete Example — Distance to Interval

Given:

```text
x
[L,R]
```

Want minimum movement to enter interval.

Cases:

```text
x < L:
    answer = L-x

L <= x <= R:
    answer = 0

x > R:
    answer = x-R
```

Visual:

```text
<------|================|------>
       L                R

 left       inside        right
```

This is a true piecewise function because behavior changes at:

```text
L
R
```

---

# 20.47 Complete Example — Split Sum Into k Odd Numbers

Need:

```text
x1+...+xk=n
```

all positive odd.

Conditions:

```text
n >= k
```

and:

```text
n%2 = k%2
```

Instead of many cases, classify by:

```text
enough total?
same parity?
```

Truth table:

```text
enough?   parity?   possible?
-----------------------------
NO        any       NO
YES       NO        NO
YES       YES       YES
```

When YES, construction:

```text
1 1 ... 1 [n-(k-1)]
```

Casework identifies feasibility; construction proves sufficiency.

---

# 20.48 Complete Example — Maximum Product Pair

Sorted:

```text
a1 <= a2 <= ... <= an
```

Potential best pair can come from:

```text
two largest positives
```

or:

```text
two most negative values
```

Candidates:

```text
A = a[n-1] * a[n-2]
B = a[0]   * a[1]
```

Answer:

```text
max(A,B)
```

Instead of handling every sign configuration separately, extrema summarize all meaningful cases.

---

# 20.49 Complete Example — Absolute Value Optimization

Minimize:

```text
|x-a| + |x-b|
```

Assume:

```text
a <= b
```

Casework:

```text
x < a:
    moving x right decreases total

a <= x <= b:
    total = b-a

x > b:
    moving x left decreases total
```

Therefore every:

```text
x in [a,b]
```

is optimal.

Casework does more than compute values; it can expose an entire optimal region.

---

# 20.50 How to Choose the Right Split

Ask:

```text
Where does something change?
```

Specifically:

```text
Where does sign change?
Where does parity matter?
Where does a remainder reset?
Where does ordering reverse?
Where does an operation become legal?
Where does a min/max switch winner?
Where does an absolute value change formula?
Where does a construction gain a leftover?
```

Those points define your cases.

---

# 20.51 Complete-Coverage Proof

Suppose you split:

```text
a < b
a = b
a > b
```

These cases are:

### Exhaustive

Every pair satisfies exactly one.

### Disjoint

No pair can satisfy two simultaneously.

That is ideal casework.

Whenever possible, cases should form a **partition**:

```text
every input belongs somewhere
and nowhere twice
```

---

# 20.52 Boundary Ownership

Be precise with inequalities.

Bad:

```text
case 1: x < 5
case 2: x > 5
```

What about:

```text
x = 5?
```

Correct partition:

```text
x < 5
x = 5
x > 5
```

or:

```text
x <= 5
x > 5
```

depending on behavior.

Many implementation bugs are missing boundary ownership.

---

# 20.53 Case Table Template

When unsure, write:

```text
PROPERTY A | PROPERTY B | RESULT
-----------|------------|-------
...        | ...        | ...
...        | ...        | ...
```

Then ask:

```text
Can rows be merged?
Can symmetry remove columns?
Can parity/count/min/max summarize the table?
```

This is a powerful paper technique during practice.

---

# 20.54 Piecewise Formula Template

Write:

```text
          { expression A, condition A
answer =  { expression B, condition B
          { expression C, condition C
```

Then verify:

```text
1. Conditions cover every valid input.
2. Conditions do not contradict.
3. Formula is correct inside each region.
4. Boundaries are assigned correctly.
```

Only then convert it to C++.

---

# 20.55 Casework Before Code

Do not discover cases by repeatedly adding:

```cpp
if (...)
else if (...)
else if (...)
```

while debugging.

First write the mathematical partition.

Example:

```text
CASE A:
x < L

CASE B:
L <= x <= R

CASE C:
x > R
```

Then implementation becomes mechanical.

---

# 20.56 Common Mistakes

## Mistake 1 — Too many cases

Search for symmetry, min/max, parity count, modulo, or normalization.

---

## Mistake 2 — Missing one case

Check complete coverage explicitly.

---

## Mistake 3 — Overlapping cases with different answers

Make boundaries precise.

---

## Mistake 4 — Splitting on irrelevant values

Case on properties that change behavior.

---

## Mistake 5 — Ignoring equality

`<` and `>` do not cover `=`.

---

## Mistake 6 — Using WLOG without symmetry

Verify the variables really are interchangeable.

---

## Mistake 7 — Keeping casework after a formula becomes obvious

Merge when possible.

---

## Mistake 8 — Forcing one formula too early

Sometimes a clean 2–3 case solution is better than an obscure formula.

---

# 20.57 Casework Scratchpad

For a new problem:

```text
WHAT PROPERTY CHANGES BEHAVIOR?
____________________________

BREAKPOINTS:
____________________________

RAW CASES:
1. _________________________
2. _________________________
3. _________________________

DO THEY COVER EVERYTHING?
____________________________

ANY OVERLAP?
____________________________

CAN I NORMALIZE?
min/max/sort/swap:
____________________________

CAN I COMPRESS?
parity/remainder/sign/count:
____________________________

CAN CASES BE MERGED?
____________________________

FINAL CASES:
____________________________
```

---

# 20.58 The Anti-Case-Explosion Checklist

Before creating many branches, try:

```text
1. min(a,b) / max(a,b)

2. sort values

3. absolute difference

4. parity count

5. remainder class

6. sign count

7. frequency vector

8. min/max bottleneck

9. symmetry / WLOG

10. algebraic rearrangement
```

Often 8 cases collapse to 1–3.

---

# 20.59 Five-Second Recognition Triggers

```text
odd/even
    → parity cases

positive/negative/zero
    → sign cases

a compared with b
    → ordering cases

absolute value
    → split at zero/breakpoint

inside/outside interval
    → left / inside / right

n % k
    → remainder cases

pair/block construction
    → even / odd leftover

min/max expression
    → active constraint

few compressed states
    → tiny exhaustive table
```

---

# 20.60 CF-Style Workflow

When one approach seems to work only sometimes:

```text
STEP 1
Find an input where behavior changes.

STEP 2
Ask what property differs:
parity?
sign?
ordering?
boundary?
remainder?
resource threshold?

STEP 3
Split on that property.

STEP 4
Solve each case independently.

STEP 5
Check tiny boundary values.

STEP 6
Prove the cases cover all inputs.

STEP 7
Search for symmetry/normalization.

STEP 8
Merge cases with identical logic.

STEP 9
Only then write if/else code.
```

---

# 20.61 Chapter Mastery Test

You are ready for the final modeling chapter when you can see:

```text
"This formula fails for some inputs"
```

and respond with:

```text
Where exactly does behavior change?
```

Then naturally test:

```text
parity
sign
ordering
equality
remainder
boundary
threshold
```

You should also automatically try to reduce casework using:

```text
min/max
sorting
absolute value
modulo
frequency
symmetry
state compression
```

---

# 20.62 Final Mental Engine

```text
                ONE FORMULA FAILS
                       │
                       ▼
             WHERE DOES BEHAVIOR CHANGE?
                       │
        ┌──────────────┼──────────────┐
        │              │              │
      parity          sign         ordering
        │              │              │
     modulo         boundary       threshold
        └──────────────┼──────────────┘
                       ▼
                CREATE RAW CASES
                       │
                       ▼
                 SOLVE EACH CASE
                       │
                       ▼
              CHECK ALL BOUNDARIES
                       │
                       ▼
             COMPLETE + DISJOINT?
                       │
                       ▼
              CAN WE NORMALIZE?
                       │
            ┌──────────┼──────────┐
            │          │          │
          sort       min/max    symmetry
            │          │          │
            └──────────┼──────────┘
                       ▼
                 MERGE CASES
                       │
                       ▼
              SIMPLE FINAL MODEL
```

The core habit:

```text
Do not fear casework.

First use cases to understand
why behavior changes.

Then compress, normalize, and merge
until only the essential cases remain.
```

---

# Next Chapter

```text
20. CASEWORK & PIECEWISE MODELING
                 ↓
21. FINAL MODELING ENGINE
```

Chapter 21 will combine the full course into one practical contest system:

```text
read story
→ identify objects
→ define variables
→ write constraints
→ derive equations
→ eliminate variables
→ inspect operations
→ detect invariants
→ classify/compress states
→ test boundaries/cases
→ derive feasibility
→ identify optimization structure
→ choose algorithm
→ prove correctness
→ test implementation
```

It will also include a **CF 800 → 1900 modeling decision tree** and a reusable scratchpad for solving unfamiliar problems.
