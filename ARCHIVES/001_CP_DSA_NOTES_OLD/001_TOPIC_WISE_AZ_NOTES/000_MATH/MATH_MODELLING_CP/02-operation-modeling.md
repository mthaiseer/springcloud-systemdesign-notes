# CP Mathematical Modeling Mini-Course

## 2. Operation Modeling

> **Goal:** Convert statements like **"in one operation..."** into mathematics.
>
> Instead of mentally simulating every move, learn to describe one operation, then `k` operations, then the final state.

---

# Chapter Tree

```text
2. OPERATION MODELING
│
├── 2.1 Identify what one operation changes
├── 2.2 Write before -> after
├── 2.3 Introduce k = number of operations
├── 2.4 One operation -> k operations
├── 2.5 Build final-state equations
├── 2.6 Derive feasibility conditions
├── 2.7 Reverse operations
├── 2.8 Replace simulation with a formula
├── 2.9 Multiple operation types
├── 2.10 Find what an operation cannot change
└── 2.11 Complete CF-style modeling
```

The central transformation:

```text
"In one operation..."
        ↓
What changes?
        ↓
BEFORE -> AFTER
        ↓
What after k operations?
        ↓
FINAL STATE
        ↓
Equation / condition
        ↓
Algorithm
```

---

# 2.1 Identify What One Operation Changes

Whenever a CF problem says:

> In one operation...

slow down and isolate that sentence.

Ask:

```text
1. Which quantities change?
2. By how much?
3. Which quantities do NOT change?
```

Example:

> In one operation, increase `a` by 2.

Before:

```text
a
```

After:

```text
a + 2
```

So:

```text
a -> a + 2
```

That is the mathematical model of one operation.

---

# 2.2 Write BEFORE -> AFTER

This notation is extremely useful.

Example:

> Increase `a` by 2 and decrease `b` by 1.

Write:

```text
BEFORE          AFTER

a               a + 2
b               b - 1
```

Compact form:

```text
(a, b) -> (a + 2, b - 1)
```

ASCII:

```text
      ONE OPERATION
           │
     ┌─────┴─────┐
     │           │
     a           b
     │           │
    +2          -1
     │           │
     ▼           ▼
   a + 2       b - 1
```

Do this before thinking about algorithms.

---

# 2.3 Introduce k = Number of Operations

If the problem asks:

```text
Can we reach a target?
Minimum operations?
Maximum operations?
What happens after repeated operations?
```

a powerful first move is:

```text
k = number of operations
```

Example:

```text
one operation:

a -> a + 2
```

After:

```text
1 operation: a + 2
2 operations: a + 4
3 operations: a + 6
```

Pattern:

```text
after k operations:

a' = a + 2 * k
```

Visual:

```text
start
  a
  │
 +2
  ▼
a+2
  │
 +2
  ▼
a+4
  │
 +2
  ▼
a+6
  │
 ...
  ▼
a + 2*k
```

Repeated addition becomes multiplication.

---

# 2.4 One Operation -> k Operations

This is the core technique.

If one operation changes:

```text
a -> a + x
```

then after `k` operations:

```text
a' = a + k*x
```

If one operation changes:

```text
a -> a - x
```

then:

```text
a' = a - k*x
```

If:

```text
(a, b) -> (a + x, b - y)
```

then after `k` operations:

```text
a' = a + k*x
b' = b - k*y
```

General picture:

```text
ONE OPERATION

a changes by +x
b changes by -y

       ↓ repeat k times

TOTAL CHANGE

a changes by +k*x
b changes by -k*y

       ↓

FINAL STATE

a' = a + k*x
b' = b - k*y
```

---

# 2.5 Build Final-State Equations

Now suppose the problem asks:

> Can `a` and `b` become equal?

Operation:

```text
(a, b) -> (a + 2, b - 1)
```

After `k` operations:

```text
a' = a + 2*k
b' = b - k
```

For equality:

```text
a' = b'
```

Therefore:

```text
a + 2*k = b - k
```

Rearrange:

```text
3*k = b - a
```

So:

```text
k = (b - a) / 3
```

Now the repeated-operation story has become one equation.

Visual:

```text
(a,b)
  │
  │ k operations
  ▼
(a+2k, b-k)
  │
  │ want equal
  ▼
a+2k = b-k
  │
  ▼
3k = b-a
```

This is a major CP modeling skill.

---

# 2.6 Derive Feasibility Conditions

Finding a formula is not enough.

You must ask:

> Is the resulting `k` actually valid?

From:

```text
3*k = b - a
```

we get:

```text
k = (b - a) / 3
```

But `k` represents a number of operations.

Therefore:

```text
k must be an integer
k >= 0
```

So necessary conditions include:

```text
b - a >= 0

and

(b - a) must be divisible by 3
```

This gives a very useful modeling pattern:

```text
derive k
   ↓
then validate what k represents
   ↓
integer?
non-negative?
within allowed range?
```

Do not stop at the algebra.

---

# 2.7 Resource Limits During Operations

Sometimes the formula says `k` operations are needed, but the process cannot legally perform that many.

Example:

> In one operation, remove 3 stones.
> You start with `n` stones.

After `k` operations:

```text
remaining = n - 3*k
```

You cannot have negative stones:

```text
n - 3*k >= 0
```

Therefore:

```text
3*k <= n
```

So:

```text
k <= floor(n / 3)
```

Visual:

```text
initial n
   │
   │ remove 3 each time
   ▼
n - 3k
   │
   │ must remain valid
   ▼
n - 3k >= 0
   │
   ▼
k <= floor(n/3)
```

This connects operation modeling to bounds.

---

# 2.8 Reverse Operations

Sometimes working forward is complicated.

Ask:

> What operation would undo this?

Example:

Forward:

```text
x -> x + 3
```

Reverse:

```text
x -> x - 3
```

Suppose:

```text
start = a
target = b
```

Forward after `k`:

```text
a + 3*k = b
```

Reverse from target:

```text
b - 3*k = a
```

They are the same relationship.

Reverse thinking becomes much more useful in constructive and transformation problems.

Mental pattern:

```text
START
  │
  │ forward operations
  ▼
TARGET
```

If forward is confusing:

```text
START
  ▲
  │ reverse operations
  │
TARGET
```

---

# 2.9 Replace Simulation with a Formula

Consider:

> Start at `x`.
> Every operation increases the value by 5.
> Find the value after `k` operations.

Simulation:

```text
repeat k times:
    x += 5
```

Complexity:

```text
O(k)
```

But:

```text
each operation contributes +5
```

Total contribution:

```text
5*k
```

So:

```text
final = x + 5*k
```

Complexity:

```text
O(1)
```

Transformation:

```text
REPEATED PROCESS
      ↓
change per operation
      ↓
multiply by number of operations
      ↓
FORMULA
```

This is one of the main reasons mathematical modeling matters.

---

# 2.10 Operations with Different Effects

Not every operation changes only one quantity.

Example:

```text
(a, b) -> (a - 1, b + 2)
```

After `k` operations:

```text
a' = a - k
b' = b + 2*k
```

Suppose target condition is:

```text
a' = b'
```

Then:

```text
a - k = b + 2*k
```

Rearrange:

```text
a - b = 3*k
```

So:

```text
k = (a - b) / 3
```

Again validate:

```text
a - b >= 0

(a - b) divisible by 3
```

---

# 2.11 Operation Changes a Difference

Sometimes it is easier to track a derived quantity instead of both variables.

Suppose:

```text
(a, b) -> (a + 2, b - 1)
```

Define the gap:

```text
D = b - a
```

What happens to `D` after one operation?

New gap:

```text
D' = (b - 1) - (a + 2)
```

Simplify:

```text
D' = b - a - 3
```

Since:

```text
D = b - a
```

we get:

```text
D' = D - 3
```

So instead of tracking two variables:

```text
a and b
```

we can track only:

```text
D
```

Visual:

```text
(a,b)
  │
  │ operation
  ▼
(a+2,b-1)

Instead track:

D = b-a
  │
  │ operation
  ▼
D-3
```

This is called **state reduction**.

It becomes very powerful in harder problems.

---

# 2.12 What Cannot Change?

Operation modeling is not only about what changes.

Also ask:

> Is there some expression that remains constant?

Example operation:

```text
(a, b) -> (a + 2, b - 1)
```

Try:

```text
a + 2*b
```

Before:

```text
a + 2*b
```

After:

```text
(a + 2) + 2*(b - 1)
```

Simplify:

```text
a + 2 + 2*b - 2
= a + 2*b
```

So:

```text
a + 2*b
```

does not change.

Visual:

```text
BEFORE
a + 2*b
    │
    │ operation
    ▼
(a+2) + 2*(b-1)
    │
    ▼
a + 2*b
```

This quantity is an **invariant**.

We study invariants much more deeply in Chapter 3.

For now, build the habit:

```text
What changes?
What stays unchanged?
```

---

# 2.13 Multiple Operation Types

Suppose there are two allowed operations:

```text
Operation A: x -> x + 2
Operation B: x -> x + 3
```

Let:

```text
a = number of Operation A
b = number of Operation B
```

Total increase:

```text
2*a + 3*b
```

If:

```text
start = S
target = T
```

then:

```text
S + 2*a + 3*b = T
```

or:

```text
2*a + 3*b = T - S
```

Now the operation sequence has become an equation.

Important modeling idea:

```text
different operation types
        ↓
one variable for each operation count
```

Example:

```text
operation A used x times
operation B used y times
operation C used z times
```

Then model the total effect.

---

# 2.14 Order vs Count

A very important question:

> Does the ORDER of operations matter, or only HOW MANY times each is used?

Example:

```text
A: x -> x + 2
B: x -> x + 3
```

Perform:

```text
A then B:

x -> x+2 -> x+5
```

Perform:

```text
B then A:

x -> x+3 -> x+5
```

Same result.

So order does not matter.

Only counts matter:

```text
final = x + 2*a + 3*b
```

This can turn a sequence problem into a counting/equation problem.

Always ask:

```text
Does order matter?
      │
   ┌──┴──┐
   │     │
  YES    NO
   │     │
track   track only
state   operation counts
```

---

# 2.15 Complete CF-Style Example 1

Problem:

> You start with value `a`.
> In one operation, increase it by 3.
> Can you reach exactly `b`?

---

## Step 1 — Define operations

```text
k = number of operations
```

---

## Step 2 — Model one operation

```text
a -> a + 3
```

---

## Step 3 — Model k operations

```text
final = a + 3*k
```

---

## Step 4 — Require target

```text
a + 3*k = b
```

Therefore:

```text
3*k = b - a
```

So:

```text
k = (b - a) / 3
```

---

## Step 5 — Validate k

Need:

```text
b >= a
```

and:

```text
b - a divisible by 3
```

So the mathematical condition is:

```text
b >= a
AND
(b - a) % 3 == 0
```

No simulation is necessary.

---

# 2.16 Complete CF-Style Example 2

Problem:

> There are `a` red balls and `b` blue balls.
> In one operation, remove 1 red and 2 blue.
> Find the maximum number of operations.

Define:

```text
k = operations
```

After `k` operations:

```text
red remaining  = a - k
blue remaining = b - 2*k
```

Both must stay non-negative:

```text
a - k >= 0
b - 2*k >= 0
```

Therefore:

```text
k <= a
k <= floor(b / 2)
```

Both upper bounds must hold:

```text
answer = min(a, b / 2)
```

for positive integer quantities.

Visual:

```text
k operations
     │
 ┌───┴────┐
 │        │
need k   need 2k
 red      blue
 │        │
k <= a   k <= b/2
 └───┬────┘
     │
 smaller limit
     ↓
min(a, b/2)
```

---

# 2.17 Complete CF-Style Example 3

Problem:

> You have numbers `a` and `b`.
> In one operation:
>
> - increase `a` by 2
> - decrease `b` by 1
>
> Determine whether they can become equal.

Define:

```text
k = operations
```

After `k`:

```text
a' = a + 2*k
b' = b - k
```

Need:

```text
a' = b'
```

So:

```text
a + 2*k = b - k
```

Therefore:

```text
3*k = b - a
```

Conditions:

```text
b >= a
```

and:

```text
(b - a) % 3 == 0
```

This entire repeated process is reduced to a divisibility and direction check.

---

# 2.18 Complete CF-Style Example 4 — Two Operation Types

Problem:

> Start at value `0`.
>
> Operation A adds 2.
> Operation B adds 5.
>
> Can some combination reach `n`?

Define:

```text
x = number of A operations
y = number of B operations
```

Then:

```text
2*x + 5*y = n
```

with:

```text
x >= 0
y >= 0
x,y integers
```

The original story:

```text
choose operations in some sequence
```

has become:

```text
find non-negative integers x,y
such that

2*x + 5*y = n
```

That is a much cleaner mathematical model.

Do not worry yet about the best algorithm for solving every such equation.

The goal of this chapter is the translation.

---

# 2.19 Operation Modeling Checklist

Whenever you see:

> In one operation...

write:

```text
1. BEFORE:
   What is the current state?

2. AFTER:
   What exactly changes?

3. k:
   Let k = number of operations.

4. AFTER k:
   Write the final state.

5. TARGET:
   What must the final state satisfy?

6. EQUATION:
   Combine final state + target.

7. VALIDATE:
   Is k integer?
   Is k >= 0?
   Are resources still valid?

8. SIMPLIFY:
   Can simulation disappear?
```

Compact version:

```text
ONE OP
  ↓
DELTA
  ↓
k OPS
  ↓
FINAL STATE
  ↓
TARGET CONDITION
  ↓
EQUATION
  ↓
VALID k?
```

---

# 2.20 Common Mistakes

## Mistake 1 — Simulating immediately

Problem:

```text
x increases by 3 each operation.
Need value after k operations.
```

Unnecessary:

```cpp
while (k--) {
    x += 3;
}
```

Model first:

```text
final = x + 3*k
```

---

## Mistake 2 — Forgetting that k must be an integer

From:

```text
3*k = 10
```

you cannot say:

```text
k = 10/3
```

and treat it as a valid number of operations.

Operations are discrete:

```text
k must be integer
```

So exact reachability fails.

---

## Mistake 3 — Forgetting k >= 0

Suppose:

```text
a + 3*k = b
```

If:

```text
b < a
```

then algebra gives negative `k`.

But:

```text
negative operations make no sense
```

So the target is unreachable using only `+3`.

---

## Mistake 4 — Ignoring resource validity

A final-state equation may look correct while an intermediate/resource rule makes the operation impossible.

Always check:

```text
remaining quantities >= 0
operation preconditions
allowed ranges
```

---

## Mistake 5 — Tracking too much state

If only:

```text
difference = b - a
```

matters, you may not need to track `a` and `b` separately.

Ask:

```text
Can I replace several variables
with one useful quantity?
```

---

# 2.21 Translation Drills

Do not code.

Only build the mathematical model.

---

## Drill 1

> Start with `x`.
> Each operation adds 4.
> What is the value after `k` operations?

Answer:

```text
final = x + 4*k
```

---

## Drill 2

> Start with `n` stones.
> Each operation removes 5.
> What remains after `k` operations?

Answer:

```text
remaining = n - 5*k
```

Validity:

```text
n - 5*k >= 0
```

---

## Drill 3

> One operation changes `(a,b)` to `(a+1,b-2)`.

After `k` operations:

```text
a' = a + k
b' = b - 2*k
```

---

## Drill 4

> Start at `a`.
> Each operation adds 7.
> Reach exactly `b`.

Model:

```text
a + 7*k = b
```

So:

```text
7*k = b - a
```

Validity:

```text
b >= a
(b-a) % 7 == 0
```

---

## Drill 5

> Operation A adds 2.
> Operation B subtracts 3.

Let:

```text
x = count of A
y = count of B
```

Starting from `S`, final value:

```text
final = S + 2*x - 3*y
```

If target is `T`:

```text
S + 2*x - 3*y = T
```

---

# 2.22 Practice Set

For each problem, write:

```text
STATE:
ONE OPERATION:
k / operation counts:
AFTER OPERATIONS:
TARGET:
FINAL EQUATION / BOUNDS:
```

---

## Practice A

> Start with `x`.
> Each operation decreases it by 4.
> Can you reach exactly 0?

---

## Practice B

> You have `A` apples and `B` bananas.
> Each package consumes 2 apples and 3 bananas.
> Find the maximum number of packages.

---

## Practice C

> Two values are `a` and `b`.
> One operation increases `a` by 1 and decreases `b` by 2.
> Can they become equal?

---

## Practice D

> Start at 0.
> You may add either 3 or 5 in one operation.
> Model reaching target `n`.

---

## Practice E

> Start with `n`.
> Each operation subtracts 6.
> Find the maximum number of legal operations.

---

# 2.23 Practice Answers

## A

```text
k = operations

final = x - 4*k

target:
x - 4*k = 0

therefore:
4*k = x

valid if:
x >= 0
x % 4 == 0
```

---

## B

```text
k = packages

apples:
2*k <= A

bananas:
3*k <= B

therefore:
k <= A/2
k <= B/3

answer:
min(A/2, B/3)
```

using integer division for non-negative integer resources.

---

## C

```text
after k operations:

a' = a + k
b' = b - 2*k
```

Need:

```text
a + k = b - 2*k
```

Therefore:

```text
3*k = b - a
```

Valid if:

```text
b >= a
(b-a) % 3 == 0
```

---

## D

Let:

```text
x = number of +3 operations
y = number of +5 operations
```

Then:

```text
3*x + 5*y = n
```

with:

```text
x >= 0
y >= 0
x,y integers
```

---

## E

After `k`:

```text
remaining = n - 6*k
```

Legal:

```text
n - 6*k >= 0
```

Therefore:

```text
k <= floor(n/6)
```

Maximum:

```text
answer = n / 6
```

for non-negative integers.

---

# 2.24 Chapter Mastery Test

You are ready for the next chapter when this transformation feels natural:

```text
"In one operation,
a += 2
b -= 1"
```

immediately becomes:

```text
after k:

a' = a + 2*k
b' = b - k
```

and:

```text
"make them equal"
```

becomes:

```text
a + 2*k = b - k
```

You should also automatically ask:

```text
Is k >= 0?
Is k an integer?
Are resources valid?
Does order matter?
Can I track only a difference?
What stays unchanged?
```

---

# 2.25 Final Mental Engine

```text
              "IN ONE OPERATION..."
                       │
                       ▼
              WRITE THE DELTA
                       │
                       ▼
                BEFORE -> AFTER
                       │
                       ▼
               INTRODUCE k
                       │
                       ▼
               AFTER k OPS
                       │
                       ▼
              FINAL CONDITION
                       │
                       ▼
             EQUATION / BOUNDS
                       │
                       ▼
                VALIDATE k
                       │
                       ▼
        FORMULA / CONDITION / ALGORITHM
```

The goal is to stop seeing repeated operations as a long simulation.

See them as:

```text
initial state
+
(number of operations * change per operation)
=
final state
```

---

# Next Chapter

```text
2. OPERATION MODELING
          ↓
3. CONDITIONS & STRUCTURE
```

Chapter 3 develops the ideas briefly introduced here:

```text
necessary conditions
sufficient conditions
case splitting
parity
absolute difference
conservation
invariants
```

These help answer a deeper question:

> Even before constructing the operations, what MUST be true for a solution to exist?
