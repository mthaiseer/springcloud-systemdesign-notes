# CP Mathematical Modeling Mini-Course

## 12. Transformation & Operation Modeling

> **Goal:** Turn operation-heavy stories into mathematics.
>
> Instead of asking:
>
> ```text
> "Which operation should I simulate next?"
> ```
>
> ask:
>
> ```text
> "What does one operation do mathematically?"
> "What does k operations do?"
> "Can I work backward from the target?"
> ```

---

# Chapter Tree

```text
12. TRANSFORMATION & OPERATION MODELING
│
├── 12.1 State → operation → new state
├── 12.2 Represent one operation as delta
├── 12.3 Repeating one operation k times
├── 12.4 Operation counts as variables
├── 12.5 Build equations from operations
├── 12.6 Reachability conditions
├── 12.7 Direction and boundary constraints
├── 12.8 Reverse operations
├── 12.9 Target → predecessor thinking
├── 12.10 Minimum operations
├── 12.11 Greedy reverse transformations
├── 12.12 Multiple operation types
├── 12.13 Order-independent operations
├── 12.14 Order-dependent operations
├── 12.15 Replace simulation with net effect
├── 12.16 Operation on arrays / counts
├── 12.17 Potential functions
├── 12.18 Constructive operation sequences
└── 12.19 CF-style modeling workflow
```

Central engine:

```text
INITIAL STATE
      │
      ▼
ONE OPERATION
      │
      ▼
write exact mathematical change
      │
      ▼
what happens after k operations?
      │
      ▼
equation / inequality / state transition
      │
      ▼
compare with TARGET
      │
      ├── impossible?
      ├── reachable?
      └── minimum operations?
```

---

# 12.1 Operations Are Functions

An operation transforms one state into another.

```text
state_before
     ↓
 operation
     ↓
state_after
```

Example:

```text
x -> x + 3
```

This is a function:

```text
f(x) = x + 3
```

Another:

```text
x -> 2x
```

means:

```text
f(x) = 2x
```

For two variables:

```text
(a,b) -> (a+2,b-1)
```

The first modeling step is always:

```text
Write the operation without the story.
```

---

# 12.2 Translate Story Language

Story:

> Alice gives Bob two stones.

If:

```text
A = Alice's stones
B = Bob's stones
```

operation:

```text
A -> A-2
B -> B+2
```

Vector form:

```text
(A,B)
   ↓
(A-2,B+2)
```

Delta:

```text
ΔA = -2
ΔB = +2
```

Once written this way, the story becomes mathematics.

---

# 12.3 Delta Modeling

For additive operations, write:

```text
new = old + delta
```

Examples:

```text
x += 5
Δx = +5
```

```text
x -= 3
Δx = -3
```

```text
(a,b) -> (a+4,b-2)

Δa = +4
Δb = -2
```

This immediately helps with:

```text
invariants
net change
number of operations
reachability
```

---

# 12.4 One Operation vs k Operations

Suppose:

```text
x -> x + 3
```

After one operation:

```text
x + 3
```

After two:

```text
x + 6
```

After three:

```text
x + 9
```

After `k`:

```text
x + 3k
```

So instead of simulating:

```text
x
x+3
x+6
x+9
...
```

jump directly to:

```text
x_final = x_initial + 3k
```

This is one of the most important operation-modeling habits.

---

# 12.5 Repeated Multiplicative Operation

Suppose:

```text
x -> 2x
```

After:

```text
1 operation: 2x
2 operations: 4x
3 operations: 8x
```

After `k`:

```text
x * 2^k
```

General:

```text
x -> c*x
```

after `k` operations:

```text
x*c^k
```

The repeated effect depends on the operation type.

---

# 12.6 Affine Operations

Suppose:

```text
x -> x + d
```

repetition is simple:

```text
x + kd
```

But:

```text
x -> 2x + 1
```

is different.

Sequence:

```text
x
2x+1
4x+3
8x+7
...
```

The operation compounds.

So first classify:

```text
additive?
multiplicative?
affine?
state-dependent?
```

Do not assume every repeated operation becomes `x + k*d`.

---

# 12.7 Operation Count as a Variable

Suppose initial:

```text
x = A
```

Operation:

```text
x += 5
```

Target:

```text
B
```

Let:

```text
k = number of operations
```

Then:

```text
A + 5k = B
```

Therefore:

```text
k = (B-A)/5
```

Now reachability becomes arithmetic.

Need:

```text
B-A >= 0
```

and:

```text
(B-A) % 5 == 0
```

This is much cleaner than simulation.

---

# 12.8 Multiple Operation Counts

Suppose two operations:

```text
Operation A: x += 2
Operation B: x += 5
```

Let:

```text
p = number of A operations
q = number of B operations
```

Then:

```text
x_final
=
x_initial + 2p + 5q
```

To reach target `T`:

```text
2p + 5q
=
T - x_initial
```

with:

```text
p >= 0
q >= 0
```

and integers.

An operation sequence has become an equation.

This is a major modeling transformation:

```text
sequence of operations
       ↓
counts of operation types
       ↓
equation
```

when order does not matter.

---

# 12.9 When Does Order Not Matter?

Suppose operations are:

```text
A: x += 2
B: x += 5
```

Doing A then B:

```text
x + 2 + 5
=
x + 7
```

Doing B then A:

```text
x + 5 + 2
=
x + 7
```

Same result.

So only counts matter.

```text
AABBA
```

and:

```text
BAAAB
```

with the same number of A/B operations produce the same final value.

This lets us discard ordering.

---

# 12.10 Order Can Matter

Now operations:

```text
A: x += 1
B: x *= 2
```

A then B:

```text
(x+1)*2
=
2x+2
```

B then A:

```text
2x+1
```

Different.

Therefore:

```text
operation counts alone are insufficient
```

Order matters.

Mental test:

```text
Does A(B(x)) = B(A(x))?
```

If yes, operations commute for that state model.

If no, sequence/order may matter.

You do not need formal terminology during a contest; just test two orders.

---

# 12.11 Reachability Modeling

A typical problem asks:

> Can state `S` become target `T` using legal operations?

Modeling checklist:

```text
1. Write one operation mathematically.

2. Determine what can increase/decrease.

3. Find invariants.

4. Express repeated operations.

5. Introduce operation counts if useful.

6. Compare with target.

7. Check non-negative integer constraints.

8. Check intermediate-state restrictions.
```

Reachability is rarely just:

```text
equation has a solution
```

because legal intermediate states may matter too.

---

# 12.12 Direction Constraints

Operation:

```text
x += 4
```

Equation:

```text
x + 4k = y
```

Modulo condition:

```text
x % 4 == y % 4
```

But also:

```text
k >= 0
```

which means:

```text
y >= x
```

Example:

```text
x = 9
y = 5
```

Same remainder modulo 4:

```text
9 % 4 = 1
5 % 4 = 1
```

But unreachable using only `+4`.

Always translate:

```text
number of operations >= 0
```

into the corresponding inequality.

---

# 12.13 Resource Constraints During Operations

Suppose operation:

```text
A -= 2
B += 1
```

If counts cannot become negative, then performing `k` operations requires:

```text
A - 2k >= 0
```

Therefore:

```text
k <= floor(A/2)
```

After `k` operations:

```text
A' = A-2k
B' = B+k
```

Now the operation gives both:

```text
equations
+
bounds on k
```

This connects transformation modeling with resource modeling.

---

# 12.14 Reverse Operations

Sometimes forward operations expand the possibilities.

Example:

```text
from x you may:
x -> 2x
x -> x+1
```

Starting from a small number and trying all forward choices creates branching:

```text
              x
            /   \
          2x    x+1
         / \     / \
        ...     ...
```

Instead, start from target `y`.

Reverse operations:

```text
if y came from 2x:
    previous = y/2
    only if y even

if y came from x+1:
    previous = y-1
```

Sometimes reverse direction has far fewer choices.

---

# 12.15 Forward vs Reverse Branching

Forward:

```text
x
├── operation A
└── operation B
```

Two choices repeatedly:

```text
2^k possible sequences
```

Reverse target may reveal which operation must have happened.

Example:

```text
forward:
x -> 2x
x -> x+1
```

If target is odd:

```text
it cannot be result of doubling
```

so the last operation must have been:

```text
+1
```

Thus reverse:

```text
odd y -> y-1
```

can become forced.

This is a common constructive/greedy modeling idea.

---

# 12.16 Target → Predecessor Thinking

Instead of:

```text
What can I do from the current state?
```

ask:

```text
What state could have produced the target?
```

This is especially useful when the target has restrictive properties:

```text
parity
divisibility
size
last digit
structure
```

Example:

```text
operation:
x -> 2x
```

Target:

```text
18
```

Only possible predecessor:

```text
9
```

Target:

```text
17
```

has no integer predecessor under doubling.

Reverse thinking exposes this instantly.

---

# 12.17 Minimum Operations — Fixed Delta

Initial:

```text
A
```

Operation:

```text
x += d
```

Target:

```text
B
```

If reachable:

```text
k = (B-A)/d
```

There is no optimization left.

The equation directly gives the operation count.

Example:

```text
A = 7
B = 31
d = 4

k = (31-7)/4
  = 24/4
  = 6
```

---

# 12.18 Minimum Operations — Variable Gain

Suppose each operation can increase `x` by at most:

```text
d
```

Need increase:

```text
B-A
```

At least:

```text
ceil((B-A)/d)
```

operations are necessary.

If every amount from `1` to `d` can be chosen, this lower bound may also be achievable.

This pattern is:

```text
required change
÷
maximum progress per operation
```

with ceiling.

---

# 12.19 Lower Bound From Maximum Progress

Suppose one operation fixes at most:

```text
3 mismatches
```

and currently there are:

```text
10 mismatches
```

Then any solution needs at least:

```text
ceil(10/3) = 4
```

operations.

This does not automatically prove 4 operations are achievable.

It gives a **lower bound**.

Important distinction:

```text
minimum possible >= lower bound
```

versus:

```text
minimum possible = lower bound
```

You need a construction/proof for equality.

---

# 12.20 Potential Function

For minimum-operation problems, define a quantity measuring distance from target.

Examples:

```text
P = |x-target|

P = number of mismatches

P = sum of deficits

P = max value - min value
```

Then ask:

```text
How much can one operation reduce P?
```

If:

```text
P decreases by at most d
```

then:

```text
operations >= ceil(P/d)
```

This is closely related to invariants, but here the property changes toward the goal.

---

# 12.21 Exact Change Per Operation

Even stronger:

If every operation changes `P` by exactly:

```text
-d
```

then after `k` operations:

```text
P_final
=
P_initial - kd
```

If target requires:

```text
P_final = 0
```

then:

```text
k = P_initial/d
```

and divisibility may become necessary.

---

# 12.22 Array Operation Modeling

Suppose operation:

> Choose index `i` and add 1 to `a[i]`.

If target array is:

```text
b
```

and only increments are allowed, each position is independent.

For index `i`:

```text
required operations
=
b[i]-a[i]
```

provided:

```text
b[i] >= a[i]
```

Total:

```text
sum(b[i]-a[i])
```

No simulation is necessary.

Why?

One operation affects exactly one coordinate by a fixed amount.

---

# 12.23 Coupled Array Operations

Now suppose operation:

> Choose `i` and add 1 to both `a[i]` and `a[i+1]`.

Coordinates are no longer independent.

Operation count at one index affects two positions.

Let:

```text
x[i] = number of times operation i is used
```

Then final values satisfy equations such as:

```text
b[0] = a[0] + x[0]

b[1] = a[1] + x[0] + x[1]

b[2] = a[2] + x[1] + x[2]
...
```

This is the deeper modeling technique:

```text
operation counts become unknown variables
```

and final-state requirements become equations.

---

# 12.24 Greedy From Forced Operation Counts

Consider:

```text
operation i affects positions i and i+1
```

At position `0`, perhaps only operation `0` can change it.

Then target requirement at position `0` may force:

```text
x[0]
```

Once `x[0]` is known, position `1` may force:

```text
x[1]
```

and so on.

Visual:

```text
position 0
   ↓ forces
operation 0 count
   ↓ determines contribution to
position 1
   ↓ forces
operation 1 count
   ↓
...
```

Many "greedy" array-operation problems are actually forced equation solving from one boundary.

---

# 12.25 Operation Matrices / Vectors Intuition

You do not need linear algebra for most <=1900 problems, but this viewpoint is useful.

State:

```text
(a,b,c)
```

Operation A changes:

```text
(+1,-1,0)
```

Operation B changes:

```text
(0,+2,-2)
```

If used:

```text
x times A
y times B
```

net change:

```text
x*(+1,-1,0)
+
y*(0,+2,-2)
```

Final:

```text
initial + net_change = target
```

This turns many operations into arithmetic on change vectors.

---

# 12.26 Example — Two Operation Types

Initial:

```text
(a,b)
```

Operations:

```text
A: (+2,-1)
B: (-1,+3)
```

Use A `x` times and B `y` times.

Final:

```text
a' = a + 2x - y
b' = b - x + 3y
```

Target:

```text
(c,d)
```

Equations:

```text
2x - y = c-a
-x + 3y = d-b
```

with:

```text
x,y >= 0
```

If operation order has no intermediate-state restrictions and changes simply add, reachability can reduce to solving these equations.

---

# 12.27 Net Effect Instead of Simulation

Suppose operation sequence is known:

```text
+3
-1
+3
+3
-1
```

Instead of updating `x` five times:

```text
net change
=
3-1+3+3-1
=
7
```

Final:

```text
x+7
```

General:

```text
final
=
initial
+
sum of operation deltas
```

This is useful when only final state matters.

But if intermediate states have constraints such as:

```text
x must never become negative
```

order may matter again.

---

# 12.28 Final State vs Intermediate Validity

Example:

Initial:

```text
x = 1
```

Operations:

```text
A: x -= 2
B: x += 2
```

Suppose state must never be negative.

Sequence:

```text
A then B
```

starts:

```text
1 -> -1
```

illegal.

Sequence:

```text
B then A
```

is:

```text
1 -> 3 -> 1
```

legal.

Both have net change:

```text
0
```

but legality differs.

Therefore always ask:

```text
Does the problem care only about final state,
or must every intermediate state be valid?
```

---

# 12.29 Replace-One Operations

Suppose operation:

> Replace `x` by `x-d`.

Repeated sequence:

```text
x
x-d
x-2d
x-3d
...
```

After `k`:

```text
x-kd
```

If we stop when:

```text
x-kd <= T
```

then:

```text
kd >= x-T
```

Minimum:

```text
k = ceil((x-T)/d)
```

This is a direct inequality model.

---

# 12.30 Doubling / Halving Problems

Operations involving:

```text
*2
/2
```

often suggest reverse reasoning.

Suppose forward:

```text
x -> 2x
```

To reach `y`:

```text
y = x*2^k
```

So:

```text
y/x
```

must be a power of two, assuming exact integer divisibility.

Alternatively reverse:

```text
while y > x:
    y /= 2
```

only when allowed and divisible.

The equation and reverse view reinforce each other.

---

# 12.31 Operation Effects on Counts

Suppose state contains counts:

```text
A, B, C
```

Operation:

```text
consume 2 A
consume 1 B
produce 1 C
```

After `k` operations:

```text
A' = A - 2k
B' = B - k
C' = C + k
```

Feasibility requires:

```text
A - 2k >= 0
B - k >= 0
```

Therefore:

```text
k <= A/2
k <= B
```

Maximum possible operations:

```text
min(A/2, B)
```

This combines operation modeling with Chapter 5 resource modeling.

---

# 12.32 Reverse a Complex Story

Suppose:

> Starting from 1, repeatedly either multiply by 2 or add 1. Determine whether target `n` can be reached.

Forward state tree branches.

But because `+1` alone can reach every positive integer, reachability is trivial.

If the problem instead asks for **minimum operations**, reverse thinking becomes useful:

```text
target n
```

If even:

```text
possibly reverse *2 using /2
```

If odd and greater than 1:

```text
must reverse +1 using -1
```

This can dramatically reduce the target.

The key lesson is not this particular greedy rule; it is:

```text
inspect the inverse operations
```

when forward choices explode.

---

# 12.33 Constructive Modeling

Sometimes the problem asks you to output operations.

Do not start generating randomly.

First derive:

```text
What must be true at the end?
```

Then determine operation counts or forced choices.

Typical constructive workflow:

```text
target condition
      ↓
derive required changes
      ↓
choose operations achieving those changes
      ↓
verify bounds/invariants
      ↓
output sequence
```

Construction should come from the mathematical model.

---

# 12.34 Necessary vs Sufficient Again

Suppose operation preserves parity.

Then:

```text
same parity
```

may be necessary.

But reachability can require more.

Example:

```text
operation x += 6
```

Initial:

```text
5
```

Target:

```text
11
```

Same parity and same modulo 6:

```text
reachable
```

Target:

```text
-1
```

same modulo 6 mathematically, but only `+6` is allowed:

```text
unreachable
```

A complete transformation model usually combines:

```text
invariant
+
direction
+
operation-count integrality
+
bounds
```

---

# 12.35 Complete CF-Style Example 1 — Add d

Problem:

> Start with `a`.
> In one operation add `d`.
> Can you reach `b`?

After `k`:

```text
a + kd = b
```

Thus:

```text
b-a = kd
```

Need:

```text
b >= a
```

and:

```text
(b-a) % d == 0
```

Then:

```text
k = (b-a)/d
```

No simulation.

---

# 12.36 Complete CF-Style Example 2 — Consume Resources

Resources:

```text
R red
B blue
```

One product needs:

```text
2 red
3 blue
```

After making `k`:

```text
R' = R-2k
B' = B-3k
```

Need:

```text
R-2k >= 0
B-3k >= 0
```

So:

```text
k <= R/2
k <= B/3
```

Maximum:

```text
min(R/2, B/3)
```

This is operation modeling → inequalities → optimization.

---

# 12.37 Complete CF-Style Example 3 — Equalize Two Values

Suppose operation increments the smaller value by 1.

State:

```text
a,b
```

Goal:

```text
a = b
```

Assume:

```text
a <= b
```

Required change:

```text
b-a
```

Each operation reduces the gap by:

```text
1
```

Therefore:

```text
operations = b-a
```

More generally:

```text
potential = |a-b|
```

and each legal operation reduces potential predictably.

---

# 12.38 Complete CF-Style Example 4 — Increment by At Most d

Start:

```text
a
```

Need reach at least:

```text
b
```

Each operation adds at most:

```text
d
```

Required increase:

```text
max(0,b-a)
```

Lower bound:

```text
ceil(max(0,b-a)/d)
```

If any increment from `0..d` is legal, this bound is achievable.

So answer:

```text
ceil(max(0,b-a)/d)
```

---

# 12.39 Complete CF-Style Example 5 — Array Increment

Initial:

```text
a = [2,1,4]
```

Target:

```text
b = [5,1,7]
```

Operation:

```text
choose one position and increment it by 1
```

Required:

```text
index 0: 5-2 = 3
index 1: 1-1 = 0
index 2: 7-4 = 3
```

Total:

```text
6 operations
```

If any:

```text
b[i] < a[i]
```

target is impossible because decrements are unavailable.

---

# 12.40 Complete CF-Style Example 6 — Reverse Reduction

Forward operations:

```text
x -> x+1
x -> 2x
```

Suppose target is much larger than start and we seek a short sequence.

Reverse candidates:

```text
y -> y-1
y -> y/2   if y even
```

Why can reverse be attractive?

```text
forward:
small -> many larger possibilities

reverse:
large target -> often strongly constrained predecessor
```

This is a modeling direction choice, not merely an implementation trick.

---

# 12.41 Operation Modeling Checklist

When a problem contains operations:

```text
1. DEFINE THE STATE.

2. REMOVE THE STORY.
   Write operation algebraically.

3. WRITE DELTA / TRANSFORMATION.

4. AFTER k OPERATIONS, WHAT IS THE STATE?

5. CAN OPERATION COUNTS BE VARIABLES?

6. DOES ORDER MATTER?

7. WHAT IS INVARIANT?

8. WHAT ONLY INCREASES/DECREASES?

9. WHAT ARE THE RESOURCE/BOUNDARY LIMITS?

10. CAN I START FROM THE TARGET?

11. WHAT ARE THE INVERSE OPERATIONS?

12. FOR MINIMUM OPERATIONS:
    what is my distance/potential?

13. HOW MUCH CAN ONE operation improve it?

14. IS A LOWER BOUND ACHIEVABLE?

15. MUST INTERMEDIATE STATES REMAIN VALID?
```

---

# 12.42 Common Mistakes

## Mistake 1 — Simulating before deriving the effect

Always first ask:

```text
after k operations, can I write a formula?
```

---

## Mistake 2 — Ignoring operation counts as variables

Instead of:

```text
A B A A B ...
```

try:

```text
x = count of A
y = count of B
```

when order is irrelevant.

---

## Mistake 3 — Assuming order never matters

Check:

```text
A then B
```

against:

```text
B then A
```

especially with multiplication, division, min/max, replacement, or intermediate constraints.

---

## Mistake 4 — Solving only the final equation

A mathematical final state may require:

```text
negative operation count
```

or illegal intermediate states.

Check legality.

---

## Mistake 5 — Forward search when reverse is simpler

If the forward operation increases possibilities, inspect inverse operations from the target.

---

## Mistake 6 — Confusing lower bound with answer

```text
need at least 5 operations
```

does not prove:

```text
5 operations are sufficient
```

Construct or prove achievability.

---

# 12.43 Translation Drills

Do not code.

---

## Drill 1

Operation:

```text
x += 7
```

After `k`:

```text
x + 7k
```

---

## Drill 2

Operations:

```text
A: x += 2
B: x += 3
```

Used `p` and `q` times:

```text
x_final = x + 2p + 3q
```

---

## Drill 3

Operation:

```text
A -= 3
B += 2
```

After `k`:

```text
A' = A-3k
B' = B+2k
```

Resource condition:

```text
k <= A/3
```

---

## Drill 4

Forward:

```text
x -> 2x
```

Reverse:

```text
y -> y/2
```

only when:

```text
y % 2 == 0
```

---

## Drill 5

Operations:

```text
x += 1
x *= 2
```

Order matters because:

```text
(x+1)*2 != 2x+1
```

---

# 12.44 Practice Set

For each problem write:

```text
STATE:
ONE OPERATION:
DELTA/TRANSFORMATION:
STATE AFTER k:
OPERATION-COUNT VARIABLES:
INVARIANTS:
DIRECTION/BOUNDS:
REVERSE OPERATION:
MIN-OPS POTENTIAL:
```

---

## Practice A

> Start at `a`. Each operation subtracts `d`. Can you reach `b`?

---

## Practice B

> You have `R` red and `B` blue stones. Each operation consumes 3 red and 2 blue. What is the maximum number of operations?

---

## Practice C

> Start at `x`. Operations are `+4` and `+7`. Express reachability of target `y` using operation counts.

---

## Practice D

> An array operation increments exactly one element by 1. Determine the number of operations needed to transform `a` into `b`, if possible.

---

## Practice E

> Operation A is `x += 1`; operation B is `x *= 3`. Does order matter?

---

# 12.45 Practice Answers

## A

After `k`:

```text
a-kd = b
```

Need:

```text
a >= b
```

and:

```text
(a-b) % d == 0
```

Then:

```text
k = (a-b)/d
```

---

## B

After `k`:

```text
R-3k >= 0
B-2k >= 0
```

Therefore:

```text
k <= R/3
k <= B/2
```

Maximum:

```text
min(R/3, B/2)
```

---

## C

Let:

```text
p = number of +4
q = number of +7
```

Then:

```text
4p + 7q = y-x
```

with:

```text
p,q >= 0
```

integers.

---

## D

Need:

```text
b[i] >= a[i]
```

for every `i`.

If so:

```text
operations
=
sum_i (b[i]-a[i])
```

---

## E

Yes.

A then B:

```text
3(x+1)
=
3x+3
```

B then A:

```text
3x+1
```

Different.

---

# 12.46 Chapter Mastery Test

You are ready for the next chapter when you see:

```text
"You may perform these operations any number of times"
```

and naturally do:

```text
operation
   ↓
algebraic transformation
   ↓
after k operations
```

instead of immediately writing a loop.

You should recognize:

```text
fixed additive operation
    -> initial + k*delta
```

and:

```text
multiple additive operations
    -> operation counts as variables
```

and ask:

```text
Does order matter?
```

You should also be comfortable switching:

```text
INITIAL -> TARGET
```

to:

```text
TARGET -> PREDECESSOR
```

when reverse operations are more constrained.

---

# 12.47 Final Mental Engine

```text
               OPERATION STORY
                      │
                      ▼
                 DEFINE STATE
                      │
                      ▼
            WRITE ONE TRANSFORMATION
                      │
                      ▼
          ADDITIVE / MULTIPLICATIVE /
             STATE-DEPENDENT?
                      │
                      ▼
              REPEAT k TIMES
                      │
          ┌───────────┼───────────┐
          │           │           │
      one type    many types    order matters
          │           │           │
       formula     counts as     sequence /
                   variables     reverse
          │           │           │
          └───────────┼───────────┘
                      ▼
                 TARGET EQUATION
                      │
                      ▼
       invariants + bounds + integrality
                      │
                      ▼
             reachable / impossible
                      │
                      ▼
              minimum operations?
                      │
                      ▼
             potential / reverse /
             forced construction
```

The core habit:

```text
Operations are not instructions
you must immediately simulate.

They are mathematical transformations
you can compose, count, invert,
and turn into equations.
```

---

# Next Chapter

```text
12. TRANSFORMATION & OPERATION MODELING
                    ↓
13. GREEDY MATHEMATICAL MODELING
```

Chapter 13 will focus on recognizing when a local choice can be justified mathematically:

```text
sort first
take cheapest/largest
pair extremes
exchange reasoning
bottlenecks
deadline/capacity choices
prove why the greedy choice is safe
```

The emphasis will be on deriving the greedy rule rather than memorizing greedy patterns.
