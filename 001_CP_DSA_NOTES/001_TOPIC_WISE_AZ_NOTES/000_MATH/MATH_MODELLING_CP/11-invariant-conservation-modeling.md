# CP Mathematical Modeling Mini-Course

## 11. Invariant & Conservation Modeling

> **Goal:** Learn to recognize when a complicated sequence of operations is controlled by something that **never changes** or changes in a predictable way.
>
> Core question:
>
> **"What property survives every legal operation?"**

---

# Chapter Tree

```text
11. INVARIANT & CONSERVATION MODELING
│
├── 11.1 What is an invariant?
├── 11.2 Why operations hide invariants
├── 11.3 Before → operation → after
├── 11.4 Operation-effect tables
├── 11.5 Sum invariants
├── 11.6 Difference / linear invariants
├── 11.7 Parity invariants
├── 11.8 Count invariants
├── 11.9 Modular invariants
├── 11.10 Conserved quantity vs monotone quantity
├── 11.11 Necessary conditions
├── 11.12 Proving impossibility
├── 11.13 Reachability classes
├── 11.14 Reverse engineering an invariant
├── 11.15 Weighted invariants
└── 11.16 CF-style modeling workflow
```

Central engine:

```text
INITIAL STATE
     │
     ▼
LEGAL OPERATION
     │
     ▼
FINAL STATE

Instead of simulating every path:

For each candidate property P:
    P(before) ? P(after)

If always equal:
    INVARIANT

Then:

P(initial) != P(target)
        ↓
   IMPOSSIBLE
```

---

# 11.1 What Is an Invariant?

An **invariant** is a property or quantity that remains unchanged after every legal operation.

Suppose state is:

```text
(a,b)
```

Operation:

```text
a += 1
b -= 1
```

Before:

```text
sum = a+b
```

After:

```text
(a+1) + (b-1)
= a+b
```

Therefore:

```text
a+b
```

is invariant.

Example:

```text
(5,8)
  ↓ operation
(6,7)
  ↓
(7,6)
```

Sums:

```text
13
13
13
```

The individual values change.

The sum does not.

---

# 11.2 Why Invariants Matter

Imagine a problem allows many operations.

Naive thinking:

```text
operation 1
operation 2
operation 3
...
try every sequence
```

This can create an enormous search space.

Invariant thinking asks:

```text
What can NEVER change,
no matter which sequence I choose?
```

If the target violates that property, you can reject it immediately.

```text
huge operation tree
        ↓
one invariant
        ↓
impossibility proof
```

This is why invariants are powerful in constructive and reachability problems.

---

# 11.3 The Basic Operation Analysis

For each operation, write:

```text
BEFORE
  ↓
CHANGE
  ↓
AFTER
```

Example:

```text
before:
(a,b)

operation:
a += 2
b -= 2

after:
(a+2,b-2)
```

Now test simple properties.

### Sum

```text
(a+2)+(b-2)
= a+b
```

Invariant.

### Difference

```text
(a+2)-(b-2)
= a-b+4
```

Not invariant.

### Parity of sum

Since the sum itself is invariant, its parity is also invariant.

This systematic approach is better than guessing.

---

# 11.4 Operation-Effect Table

A very useful CP technique is to make a tiny table.

Suppose operation:

```text
(a,b) -> (a+1,b-1)
```

Write:

```text
PROPERTY        CHANGE

a                 +1
b                 -1
a+b                0
a-b               +2
(a+b)%2             0
(a-b)%2             0
```

Anything with change:

```text
0
```

is a candidate invariant.

Anything changing predictably may still be useful.

---

# 11.5 Sum Invariant

Common operation:

```text
take x from one place
give x to another
```

Example:

```text
a -= x
b += x
```

Total:

```text
(a-x)+(b+x)
= a+b
```

So total resource is conserved.

Story triggers:

```text
move
transfer
redistribute
swap
give/take
move stones between piles
```

often suggest checking total sum.

But do not assume it; calculate the operation's effect.

---

# 11.6 Conservation of Total Count

Suppose you have colored balls and an operation only changes colors.

Then:

```text
number of balls
```

may remain constant.

Example:

```text
R + B + G = n
```

If every operation removes one ball and adds one ball:

```text
total count change = -1 + 1 = 0
```

Therefore:

```text
R+B+G
```

is invariant.

The exact colors may change while total objects remain fixed.

---

# 11.7 Parity Invariant

Sometimes exact quantity changes, but parity does not.

Operation:

```text
x += 2
```

Then:

```text
x
x+2
x+4
...
```

Parity never changes.

If initial:

```text
x = 5
```

reachable values have odd parity:

```text
5,7,9,11,...
```

Target:

```text
12
```

has even parity.

Therefore impossible.

The invariant is not:

```text
x
```

It is:

```text
x % 2
```

---

# 11.8 Why Adding an Even Number Preserves Parity

Suppose:

```text
x' = x + 2k
```

Modulo 2:

```text
x' % 2
=
(x + 2k) % 2
=
x % 2
```

So:

```text
parity before = parity after
```

This is a modular invariant.

General idea:

```text
if every operation changes x by
a multiple of m,

then x % m is invariant.
```

---

# 11.9 Modular Invariant

Suppose operation:

```text
x += 6
```

Then:

```text
x % 6
```

never changes.

Starting:

```text
x = 5
```

reachable values:

```text
5
11
17
23
...
```

All satisfy:

```text
x % 6 = 5
```

Target `20`:

```text
20 % 6 = 2
```

So it cannot be reached using only `+6`.

This connects Chapter 8 with invariants.

---

# 11.10 Difference Invariant

Suppose:

```text
a += 1
b += 1
```

Then:

```text
a-b
```

after operation:

```text
(a+1)-(b+1)
= a-b
```

So difference is invariant.

Example:

```text
(3,8)
```

difference:

```text
-5
```

After any number of simultaneous `+1` operations:

```text
(4,9)
(5,10)
(6,11)
...
```

difference remains:

```text
-5
```

Target:

```text
(10,20)
```

difference:

```text
-10
```

Therefore unreachable.

---

# 11.11 Linear Invariants

Sometimes neither:

```text
a+b
```

nor:

```text
a-b
```

works.

Try a weighted expression:

```text
p*a + q*b
```

Example operation:

```text
a += 2
b -= 1
```

Change in:

```text
a+b
```

is:

```text
+2-1 = +1
```

not invariant.

Try:

```text
a + 2b
```

After:

```text
(a+2) + 2(b-1)
=
a+2+2b-2
=
a+2b
```

So:

```text
a+2b
```

is invariant.

This is called a **weighted invariant**.

---

# 11.12 How to Derive a Weighted Invariant

Suppose operation changes:

```text
a by Δa
b by Δb
```

We want:

```text
P = p*a + q*b
```

to remain unchanged.

Its change is:

```text
ΔP = p*Δa + q*Δb
```

For an invariant:

```text
p*Δa + q*Δb = 0
```

Example:

```text
Δa = +2
Δb = -1
```

Need:

```text
2p - q = 0
```

Choose:

```text
p = 1
q = 2
```

So:

```text
P = a + 2b
```

This turns invariant discovery into an equation.

---

# 11.13 Swap Operations

Suppose operation:

```text
swap a[i], a[j]
```

What changes?

Positions change.

But many global properties do not:

```text
sum
multiset of values
minimum value
maximum value
frequency of every value
number of elements
```

For a pure permutation of an array, the multiset is invariant.

So if the target array contains different values/frequencies, it is impossible to obtain using swaps alone.

Story:

```text
you may swap any two elements
```

Immediately think:

```text
positions can change
values cannot be created/destroyed
multiset is invariant
```

---

# 11.14 Permutation Invariant

Example:

Initial:

```text
[1,2,2,5]
```

Target:

```text
[2,5,1,2]
```

Same frequencies:

```text
1 -> 1
2 -> 2
5 -> 1
```

Possible by swaps.

Target:

```text
[1,2,3,4]
```

different multiset.

Impossible by swaps.

This is a stronger invariant than sum.

Both arrays might even have the same sum while still having different multisets.

Use the strongest relevant property.

---

# 11.15 Conserved Quantity vs Monotone Quantity

Not every useful property stays exactly constant.

Suppose operation:

```text
x -= 1
```

Then `x` is not invariant.

But it is monotone:

```text
x never increases
```

So from:

```text
x = 5
```

you cannot reach:

```text
x = 8
```

This is not an invariant proof.

It is a **monotonicity** proof.

Distinguish:

```text
INVARIANT:
P stays equal

MONOTONE:
P only moves one direction
```

Both can prove impossibility.

---

# 11.16 Bounded Monovariant

A quantity that always increases or always decreases is sometimes called a **monovariant**.

Example:

```text
operation always reduces number of mismatches
```

Then:

```text
mismatches
```

may eventually reach `0`.

Useful for proving:

```text
termination
minimum/maximum number of operations
impossibility of returning to an old state
```

Mental questions:

```text
What stays constant?
```

and:

```text
What can only increase/decrease?
```

Ask both.

---

# 11.17 Necessary Conditions

Suppose invariant:

```text
P(state)
```

Initial:

```text
P(initial) = 7
```

Then every reachable state must satisfy:

```text
P(state) = 7
```

Therefore target must satisfy:

```text
P(target) = 7
```

This is a **necessary condition**.

ASCII:

```text
reachable target
      ↓
must preserve invariant
      ↓
P(target) = P(initial)
```

But be careful:

```text
same invariant
```

does not always mean:

```text
reachable
```

It may only be necessary.

---

# 11.18 Necessary Is Not Automatically Sufficient

Suppose operation:

```text
x += 4
```

Initial:

```text
x = 5
```

Target:

```text
x = 1
```

Both:

```text
x % 4 = 1
```

So modular invariant matches.

But using only `+4`, values can only increase.

Therefore target `1` is unreachable.

We need both:

```text
same remainder mod 4
```

and:

```text
target >= initial
```

This is important:

```text
invariant condition
+
direction/bounds/other constraints
```

may be needed.

---

# 11.19 Proving Impossibility

Invariant proofs are often extremely short.

Template:

```text
1. Define property P.

2. Show every legal operation preserves P.

3. Compute P(initial).

4. Compute P(target).

5. They differ.

6. Therefore target is impossible.
```

Example:

```text
operation changes x by +2 or -2
```

Property:

```text
x % 2
```

Every operation preserves parity.

If:

```text
initial = 7
target = 10
```

then:

```text
7 % 2 = 1
10 % 2 = 0
```

Therefore impossible.

---

# 11.20 Reachability Classes

An invariant partitions all states into classes.

Example:

```text
x % 3
```

creates:

```text
class 0:
..., -6,-3,0,3,6,...

class 1:
..., -5,-2,1,4,7,...

class 2:
..., -4,-1,2,5,8,...
```

If operations preserve `x % 3`, you can move only inside your starting class.

Visual:

```text
STATE SPACE

remainder 0:  ●──●──●──●

remainder 1:  ●──●──●──●

remainder 2:  ●──●──●──●

operations never cross rows
```

This is a powerful way to visualize invariants.

---

# 11.21 Operation Effect on Sum

Suppose an operation removes:

```text
x and y
```

and inserts:

```text
z
```

Change in total sum:

```text
Δsum = -x-y+z
```

Do not reason vaguely.

Write the delta.

If the rule says:

```text
z = x+y
```

then:

```text
Δsum = 0
```

sum invariant.

If:

```text
z = x+y-1
```

then:

```text
Δsum = -1
```

sum is not invariant, but decreases predictably by one each operation.

That may still be extremely useful.

---

# 11.22 Predictable Change

Suppose every operation changes quantity `S` by:

```text
-2
```

After `k` operations:

```text
S_final
=
S_initial - 2k
```

Now target gives an equation:

```text
S_initial - S_target
=
2k
```

Therefore:

```text
k =
(S_initial-S_target)/2
```

This can imply:

```text
difference must be non-negative
difference must be divisible by 2
```

So even when a quantity is not invariant, a fixed delta can create strong constraints.

---

# 11.23 Count Difference Invariant

Suppose operation removes:

```text
one A
one B
```

Then:

```text
countA -= 1
countB -= 1
```

Difference:

```text
countA-countB
```

does not change.

Before:

```text
A-B
```

After:

```text
(A-1)-(B-1)
=
A-B
```

This is a common pattern:

```text
two quantities change equally
        ↓
their difference is invariant
```

---

# 11.24 Sum Invariant From Opposite Changes

If:

```text
a changes by +d
b changes by -d
```

then:

```text
a+b
```

is invariant.

If:

```text
a changes by +d
b changes by +d
```

then:

```text
a-b
```

is invariant.

Memorize the reasoning, not merely the patterns:

```text
compute Δ(property)
```

---

# 11.25 Bit / XOR-Like Invariants

Some operations preserve properties other than ordinary sums.

For example, an operation may flip exactly two binary bits.

Then the number of `1`s changes by:

```text
-2
0
or +2
```

Therefore:

```text
parity of number_of_ones
```

is invariant.

Example:

```text
0011
```

has two ones: even.

After flipping exactly two bits, the count of ones remains even.

So a target with an odd number of ones is impossible.

The invariant emerges from the **effect of the operation**, not from the surface story.

---

# 11.26 Coloring as an Invariant Tool

Sometimes assigning colors/categories reveals an invariant.

Example number line colored by parity:

```text
even = white
odd  = black
```

An operation:

```text
x += 2
```

always stays on the same color.

Visual:

```text
0   1   2   3   4   5   6
W   B   W   B   W   B   W

+2:
W ------> W
B ------> B
```

Modulo classes are essentially a generalized coloring.

This is useful for grid/board/movement problems too.

---

# 11.27 Reverse Engineering an Invariant

When you do not immediately see an invariant:

```text
1. Write state variables.

2. Write exact delta of one operation.

3. Test:
   sum
   difference
   parity
   modulo small m
   counts
   weighted sum

4. Look for zero change.

5. If no zero change,
   look for fixed-sign or fixed-size change.
```

Example:

```text
Δa = +3
Δb = -2
```

Try weighted:

```text
P = p*a + q*b
```

Need:

```text
3p - 2q = 0
```

Choose:

```text
p = 2
q = 3
```

So:

```text
2a + 3b
```

is invariant.

---

# 11.28 Multiple Operations

If there are several legal operations, a true invariant must survive **all of them**.

Suppose:

```text
Operation A:
Δa = +1
Δb = -1

Operation B:
Δa = +2
Δb = -2
```

For:

```text
a+b
```

Operation A:

```text
Δ = 0
```

Operation B:

```text
Δ = 0
```

So sum is invariant.

But if an alleged invariant survives only operation A and not B:

```text
it is not a global invariant
```

Always test every legal operation.

---

# 11.29 Complete CF-Style Example 1 — Transfer Stones

There are two piles:

```text
A, B
```

Operation:

```text
move one stone from one pile to the other
```

Total:

```text
A+B
```

Before:

```text
A+B
```

After moving A -> B:

```text
(A-1)+(B+1)
=
A+B
```

Invariant.

Therefore a target:

```text
(C,D)
```

requires:

```text
A+B = C+D
```

But depending on operation restrictions, this condition may not alone be sufficient.

---

# 11.30 Complete CF-Style Example 2 — Add Two

Initial:

```text
x
```

Operation:

```text
x += 2
```

Target:

```text
y
```

Necessary:

```text
x % 2 = y % 2
```

Also:

```text
y >= x
```

If both hold, then:

```text
k = (y-x)/2
```

operations reach `y`.

Here the conditions are sufficient as well.

---

# 11.31 Complete CF-Style Example 3 — Equal Increment

State:

```text
(a,b)
```

Operation:

```text
a++
b++
```

Invariant:

```text
a-b
```

Target:

```text
(c,d)
```

Necessary:

```text
a-b = c-d
```

Also, because only increments are allowed:

```text
c >= a
d >= b
```

And the number of increments must agree:

```text
c-a = d-b
```

This is the full reachability condition.

---

# 11.32 Complete CF-Style Example 4 — Flip Two Bits

Binary string.

Operation:

```text
flip exactly two bits
```

Let:

```text
ones = number of 1s
```

Depending on the two bits:

```text
00 -> 11 : ones +2
11 -> 00 : ones -2
01 -> 10 : ones +0
10 -> 01 : ones +0
```

So:

```text
ones % 2
```

never changes.

Operation-effect table:

```text
selected bits    Δones

00                 +2
11                 -2
01                  0
10                  0
```

All changes are even.

Therefore parity of ones is invariant.

---

# 11.33 Complete CF-Style Example 5 — Weighted Invariant

State:

```text
(a,b)
```

Operation:

```text
a += 2
b -= 3
```

Find:

```text
P = p*a + q*b
```

Need:

```text
2p - 3q = 0
```

Choose:

```text
p = 3
q = 2
```

Then:

```text
P = 3a + 2b
```

Check:

```text
3(a+2) + 2(b-3)
=
3a+6+2b-6
=
3a+2b
```

Invariant.

---

# 11.34 Invariant vs Simulation

Suppose branching factor is:

```text
3 operations/state
```

and up to:

```text
100 operations
```

Trying all operation sequences is impossible.

Before BFS/DFS, ask:

```text
Can some target states be ruled out mathematically?
```

Sometimes invariant completely solves the problem.

Sometimes it only reduces the state space.

Sometimes search is still required.

The modeling habit is:

```text
analyze operations mathematically
BEFORE exploring them algorithmically
```

---

# 11.35 Invariant Checklist

When operations appear, ask:

```text
1. WHAT IS THE STATE?

2. WHAT EXACTLY DOES ONE OPERATION CHANGE?

3. WRITE DELTAS:
   Δa, Δb, Δcount, ...

4. CHECK SUM.

5. CHECK DIFFERENCE.

6. CHECK PARITY.

7. CHECK MODULO m.

8. CHECK COUNTS/FREQUENCIES.

9. CHECK MULTISET IF ONLY REORDERING.

10. TRY WEIGHTED SUM:
    p*a + q*b

11. DOES PROPERTY SURVIVE EVERY OPERATION?

12. IF NOT INVARIANT:
    does it change by a fixed amount?

13. DOES IT ONLY INCREASE/DECREASE?

14. COMPARE INITIAL WITH TARGET.

15. IS THE CONDITION ONLY NECESSARY,
    OR ALSO SUFFICIENT?
```

---

# 11.36 Common Mistakes

## Mistake 1 — Checking only examples

Seeing the sum unchanged in two examples does not prove it.

Use algebra:

```text
P(after) = P(before)
```

for a general operation.

---

## Mistake 2 — Testing only one legal operation

An invariant must survive every allowed operation.

---

## Mistake 3 — Assuming invariant match means reachable

Matching an invariant is usually a necessary condition.

You may still need:

```text
direction
bounds
availability
ordering
```

or another invariant.

---

## Mistake 4 — Looking only for sum

Useful invariants include:

```text
difference
parity
remainder
frequency
multiset
weighted sum
color class
```

---

## Mistake 5 — Simulating too early

When the problem says:

```text
perform this operation any number of times
```

first analyze the operation's mathematical effect.

---

# 11.37 Translation Drills

Do not code.

---

## Drill 1

Operation:

```text
a += 5
b -= 5
```

Invariant:

```text
a+b
```

---

## Drill 2

Operation:

```text
a += 3
b += 3
```

Invariant:

```text
a-b
```

---

## Drill 3

Operation:

```text
x += 8
```

Invariant:

```text
x % 8
```

and therefore parity too.

---

## Drill 4

Operation:

```text
a += 2
b -= 1
```

Weighted invariant:

```text
a + 2b
```

---

## Drill 5

Operation:

```text
swap any two array elements
```

Invariant:

```text
multiset/frequency of values
```

---

# 11.38 Practice Set

For each problem write:

```text
STATE:
OPERATION DELTA:
CANDIDATE PROPERTY:
PROPERTY CHANGE:
INVARIANT / MONOVARIANT:
NECESSARY TARGET CONDITION:
IS IT SUFFICIENT?:
```

---

## Practice A

```text
(a,b) -> (a-4,b+4)
```

Find a simple invariant.

---

## Practice B

```text
x -> x+6
```

Starting from `5`, can you reach `29`? Can you reach `30`?

---

## Practice C

```text
(a,b) -> (a+2,b+2)
```

What relation between `a` and `b` is preserved?

---

## Practice D

An operation flips exactly two bits in a binary string.

What simple property of the number of ones is preserved?

---

## Practice E

```text
(a,b) -> (a+3,b-2)
```

Find a positive weighted linear invariant.

---

# 11.39 Practice Answers

## A

```text
a+b
```

because:

```text
(a-4)+(b+4) = a+b
```

---

## B

Invariant:

```text
x % 6
```

Initial:

```text
5 % 6 = 5
```

For `29`:

```text
29 % 6 = 5
```

and:

```text
29 >= 5
```

So reachable:

```text
5 -> 11 -> 17 -> 23 -> 29
```

For `30`:

```text
30 % 6 = 0
```

Different remainder.

Impossible.

---

## C

```text
a-b
```

because both increase equally.

---

## D

Parity of:

```text
number_of_ones
```

because the count changes only by:

```text
-2, 0, +2
```

---

## E

Need:

```text
3p - 2q = 0
```

Choose:

```text
p = 2
q = 3
```

Invariant:

```text
2a + 3b
```

---

# 11.40 Chapter Mastery Test

You are ready for the next chapter when you see:

```text
"You may perform the following operation
any number of times..."
```

and your first reaction is NOT:

```text
simulate it
```

but:

```text
What does one operation change?
```

Then:

```text
Δsum?
Δdifference?
Δparity?
Δremainder?
Δcounts?
```

You should be able to reason:

```text
operation changes x by multiples of m
        ↓
x % m is invariant
```

and:

```text
two quantities change equally
        ↓
their difference may be invariant
```

and:

```text
one gains exactly what another loses
        ↓
their sum may be invariant
```

---

# 11.41 Final Mental Engine

```text
                OPERATION PROBLEM
                       │
                       ▼
                 DEFINE STATE
                       │
                       ▼
             WRITE ONE OPERATION
                       │
                       ▼
                 COMPUTE DELTAS
                       │
          ┌────────────┼────────────┐
          │            │            │
         sum       difference     modulo
          │            │            │
          └────────────┼────────────┘
                       ▼
               CHANGE = 0 ?
                  /          \
                YES          NO
                 │            │
             INVARIANT    fixed/one-way
                 │          change?
                 │            │
                 ▼            ▼
         compare initial   equation /
           and target      monovariant
                 │
                 ▼
          necessary condition
                 │
                 ▼
       sufficient or need more?
```

The core habit is:

```text
Do not follow the changing values first.

Search for the property
that the operations cannot change.
```

---

# Next Chapter

```text
11. INVARIANT & CONSERVATION MODELING
                    ↓
12. TRANSFORMATION & OPERATION MODELING
```

Chapter 12 will focus on turning operations themselves into equations:

```text
before -> after
net change
k operations
reverse operations
reachability
minimum operations
operation counts as variables
```

This is especially useful for constructive and operation-heavy Codeforces problems.
