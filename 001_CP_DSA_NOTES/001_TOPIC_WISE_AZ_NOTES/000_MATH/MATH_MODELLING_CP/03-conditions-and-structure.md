# CP Mathematical Modeling Mini-Course

## 3. Conditions & Structure

> **Goal:** After translating a story and its operations into mathematics, learn to discover the hidden conditions that decide whether a solution is possible.
>
> This chapter focuses on **necessary conditions, sufficient conditions, case splitting, parity, distance, conservation, and invariants**.

---

# Chapter Tree

```text
3. CONDITIONS & STRUCTURE
│
├── 3.1 What is a condition?
├── 3.2 Necessary conditions
├── 3.3 Sufficient conditions
├── 3.4 Necessary vs sufficient
├── 3.5 Combining conditions
├── 3.6 Case splitting
├── 3.7 Parity as state
├── 3.8 Difference / distance
├── 3.9 Conservation
├── 3.10 Invariants
├── 3.11 How to search for invariants
├── 3.12 Impossibility proofs
└── 3.13 Complete CF-style modeling
```

The central idea:

```text
MATHEMATICAL MODEL
        ↓
What MUST be true?
        ↓
NECESSARY CONDITIONS
        ↓
Is that also ENOUGH?
        ↓
SUFFICIENT CONDITIONS
        ↓
Look for structure:
parity / difference / conservation / invariant
        ↓
POSSIBLE or IMPOSSIBLE
```

---

# 3.1 What Is a Condition?

A condition is simply a rule that a valid solution must satisfy.

Example:

> Choose an integer `x` between 1 and 10.

Conditions:

```text
x >= 1
x <= 10
x is an integer
```

A candidate:

```text
x = 7
```

satisfies all conditions.

A candidate:

```text
x = 12
```

fails:

```text
x <= 10
```

Think:

```text
candidate answer
      ↓
check every required condition
      ↓
valid / invalid
```

---

# 3.2 Necessary Conditions

A **necessary condition** is something that MUST be true if a solution exists.

If the condition fails:

```text
solution is impossible
```

But if the condition holds:

```text
solution may or may not exist
```

---

## Example 1 — Equal split

> Can `n` objects be divided equally between two people?

A necessary condition:

```text
n must be even
```

Why?

Each person receives:

```text
n / 2
```

This must be an integer.

So:

```text
n % 2 == 0
```

If:

```text
n = 7
```

impossible immediately.

Visual:

```text
n objects
   │
   │ split equally
   ▼
n/2     n/2
   │
   └── both must be integers
          ↓
       n even
```

---

## Example 2 — Reach using +3

> Start at `a`.
> Every operation adds 3.
> Can you reach `b`?

From Chapter 2:

```text
a + 3*k = b
```

Therefore:

```text
3*k = b - a
```

Necessary conditions:

```text
b >= a
(b - a) % 3 == 0
```

If either fails:

```text
impossible
```

---

# 3.3 Sufficient Conditions

A **sufficient condition** guarantees that a solution exists.

Example:

> Start at `a`.
> Every operation adds exactly 3.
> Reach `b`.

Conditions:

```text
b >= a
(b - a) % 3 == 0
```

Are these merely necessary?

No.

If they hold, choose:

```text
k = (b - a) / 3
```

Then after `k` operations:

```text
a + 3*k = b
```

So these conditions are also sufficient.

Therefore:

```text
b >= a
AND
(b-a) % 3 == 0
```

are:

```text
necessary AND sufficient
```

That means they completely characterize the answer.

---

# 3.4 Necessary vs Sufficient

This distinction is extremely important in CP proofs.

Think:

```text
NECESSARY

solution exists
      ↓
condition MUST hold
```

versus:

```text
SUFFICIENT

condition holds
      ↓
solution MUST exist
```

---

## Example

Suppose:

> Can `n` be written as the sum of two positive odd integers?

If:

```text
odd + odd = even
```

then:

```text
n must be even
```

So:

```text
n even
```

is necessary.

But is every even `n` valid?

Try:

```text
n = 2
```

The only positive odd split would need:

```text
1 + 1 = 2
```

Actually valid.

If the problem instead required **two distinct positive odd integers**, then:

```text
n = 2
```

would fail.

So "n is even" would no longer be sufficient.

This demonstrates:

```text
finding one required property
does NOT automatically finish the proof
```

Always ask:

> If this condition holds, can I actually construct a solution?

---

# 3.5 Combining Conditions

Often several necessary conditions must all hold.

Example:

```text
k >= 0
k <= n
k is integer
k % 2 == 0
```

A valid `k` must satisfy:

```text
Condition 1
AND
Condition 2
AND
Condition 3
AND
Condition 4
```

Visual:

```text
               candidate k
                   │
        ┌──────────┼──────────┐
        │          │          │
      k>=0       k<=n      k even
        │          │          │
        └──────────┼──────────┘
                   │
             ALL must hold
                   ↓
                 VALID
```

If the problem offers alternatives, use OR.

Example:

```text
x can be 2 OR 5
```

Condition:

```text
x == 2 OR x == 5
```

This sounds basic, but explicitly separating AND/OR conditions prevents many CF mistakes.

---

# 3.6 Case Splitting

Sometimes one formula cannot describe every situation cleanly.

Split into a small number of meaningful cases.

Common splits:

```text
x is even / odd

a < b / a == b / a > b

x positive / zero / negative

n divisible by k / not divisible

resource A is limiting / resource B is limiting
```

---

## Example — Sign of difference

Suppose an operation only increases `a`:

```text
a -> a + 3
```

Target `b`.

Cases:

```text
Case 1: b < a
        impossible

Case 2: b == a
        0 operations

Case 3: b > a
        need divisibility check
```

Visual:

```text
compare b with a
      │
 ┌────┼────┐
 │    │    │
b<a  b=a  b>a
 │    │    │
NO   YES   check
           difference
```

---

## Do not over-split

Bad:

```text
case for every possible value
```

Good:

```text
split only when mathematical behavior changes
```

The question is:

> Where does the formula or logic change?

---

# 3.7 Parity as State

Parity means:

```text
EVEN or ODD
```

Instead of caring about the exact value, sometimes only parity matters.

```text
even -> x % 2 == 0
odd  -> x % 2 == 1
```

---

## Basic parity rules

```text
even + even = even
odd  + odd  = even
even + odd  = odd

even - even = even
odd  - odd  = even
even - odd  = odd
odd  - even = odd
```

Multiplication:

```text
even * anything = even
odd  * odd      = odd
```

---

## Why parity is state reduction

Suppose `x` could be:

```text
1, 3, 5, 7, 9, ...
```

For some problems, these are all equivalent:

```text
ODD
```

And:

```text
2, 4, 6, 8, ...
```

are equivalent:

```text
EVEN
```

So infinitely/many possible values collapse to:

```text
2 states
```

Visual:

```text
all integers
    │
 ┌──┴──┐
 │     │
EVEN  ODD
```

---

# 3.8 Parity Under Operations

Suppose:

```text
x -> x + 2
```

Adding 2 does not change parity.

Examples:

```text
3 -> 5 -> 7 -> 9
odd  odd  odd  odd

4 -> 6 -> 8 -> 10
even even even even
```

Therefore:

```text
parity(x) is invariant
```

So if:

```text
start is odd
target is even
```

the target is impossible.

No simulation required.

---

## Operation that flips parity

Suppose:

```text
x -> x + 1
```

Then:

```text
even -> odd
odd  -> even
```

Each operation flips parity.

After:

```text
even number of operations
```

original parity returns.

After:

```text
odd number of operations
```

parity flips.

This can give conditions on `k`.

---

# 3.9 Difference / Distance as Structure

Two variables may be easier to understand through their difference.

Suppose:

```text
a = 10
b = 4
```

Difference:

```text
a - b = 6
```

Distance:

```text
abs(a - b) = 6
```

---

## Track the gap instead of both values

Operation:

```text
(a,b) -> (a+2,b-1)
```

Define:

```text
D = b - a
```

After one operation:

```text
D' = (b-1) - (a+2)
```

Simplify:

```text
D' = b - a - 3
```

Therefore:

```text
D' = D - 3
```

Now the problem is:

```text
gap decreases by 3 each operation
```

instead of:

```text
a increases by 2
AND
b decreases by 1
```

Visual:

```text
2-variable state

(a,b)
  │
  │ reduce state
  ▼
D = b-a
  │
  │ operation
  ▼
D -> D-3
```

This is often much easier to reason about.

---

# 3.10 Conservation

A conserved quantity is something that stays exactly the same during the process.

Example:

> Move one stone from pile A to pile B.

Operation:

```text
(a,b) -> (a-1,b+1)
```

Total before:

```text
a + b
```

Total after:

```text
(a-1) + (b+1)
```

Simplify:

```text
a - 1 + b + 1
= a + b
```

Therefore:

```text
total number of stones is conserved
```

Visual:

```text
PILE A          PILE B
  a               b
   \             /
    \  move 1   /
     \-------->/

after:

a-1             b+1

TOTAL BEFORE = a+b
TOTAL AFTER  = a+b
```

---

## Use conservation to reject impossible targets

Initial:

```text
a + b = 10
```

If operations only move stones between piles, then every reachable state must satisfy:

```text
a' + b' = 10
```

Target:

```text
(7,5)
```

has:

```text
7 + 5 = 12
```

Therefore:

```text
impossible
```

No operation sequence needs to be explored.

---

# 3.11 Invariants

An invariant is any property or quantity that does not change under allowed operations.

Conservation of total is one type of invariant.

Other invariants can include:

```text
parity
remainder
difference modulo something
weighted sum
relative ordering
some count/property
```

---

## Example — Weighted invariant

Operation:

```text
(a,b) -> (a+2,b-1)
```

Try:

```text
a + 2*b
```

After operation:

```text
(a+2) + 2*(b-1)
```

Expand:

```text
a + 2 + 2*b - 2
```

Simplify:

```text
a + 2*b
```

So:

```text
a + 2*b
```

is invariant.

---

## Why this is powerful

Suppose initial state:

```text
a = 2
b = 5
```

Invariant value:

```text
a + 2*b
= 2 + 2*5
= 12
```

Any reachable target `(x,y)` must satisfy:

```text
x + 2*y = 12
```

Suppose target:

```text
x = 4
y = 5
```

Then:

```text
4 + 2*5 = 14
```

Not equal to 12.

Therefore:

```text
target is impossible
```

Instant rejection.

---

# 3.12 How to Search for an Invariant

Do not randomly guess complicated formulas.

Start with simple candidates.

Given an operation, test:

```text
1. total
   a + b

2. difference
   a - b

3. parity
   a % 2
   b % 2
   (a+b) % 2

4. weighted combinations
   a + c*b

5. remainder modulo small operation changes
```

A useful question:

> The operation adds something here and removes something there. Can those changes cancel?

Example:

```text
a += 2
b -= 1
```

Changes:

```text
delta a = +2
delta b = -1
```

Want a weighted sum:

```text
a + c*b
```

Change in it:

```text
+2 + c*(-1)
```

For invariant, total change should be zero:

```text
2 - c = 0
```

Therefore:

```text
c = 2
```

So:

```text
a + 2*b
```

is invariant.

This is a systematic way to discover it.

---

# 3.13 Delta Thinking

A useful notation:

```text
delta = change
```

Suppose:

```text
a -> a + 2
b -> b - 1
```

Then:

```text
delta(a) = +2
delta(b) = -1
```

For:

```text
F = a + 2*b
```

change in `F`:

```text
delta(F)
= delta(a) + 2*delta(b)

= 2 + 2*(-1)

= 0
```

If:

```text
delta(F) = 0
```

then:

```text
F is invariant
```

Visual:

```text
operation
   │
   ├── delta(a) = +2
   └── delta(b) = -1
            │
            ▼
delta(a + 2b)
= +2 + 2*(-1)
= 0
            │
            ▼
       INVARIANT
```

---

# 3.14 Necessary Condition from an Invariant

If `F` is invariant:

```text
F(initial) = F(final)
```

must hold.

Therefore:

```text
F(initial) != F(target)
```

implies:

```text
IMPOSSIBLE
```

This is a necessary condition.

But be careful:

```text
F(initial) = F(target)
```

does not always guarantee reachability.

It may be necessary but not sufficient.

Always ask:

> Can I actually construct the target if the invariant matches?

---

# 3.15 Impossibility Proofs

Many CF problems become easier when you stop asking:

> How do I construct the answer?

and first ask:

> What would make it impossible?

Common impossibility certificates:

```text
wrong parity
wrong invariant
insufficient resource
wrong remainder
outside bounds
wrong total
negative required operations
non-integer operation count
```

Visual:

```text
TARGET
  │
  ▼
Check cheap necessary conditions
  │
  ├── fails one?
  │       ↓
  │   IMPOSSIBLE
  │
  └── all pass
          ↓
     deeper reasoning
```

This is often faster than searching directly for a construction.

---

# 3.16 Complete CF-Style Example 1 — Parity

Problem:

> Start with integer `x`.
> In one operation you may add 2 or subtract 2.
> Can you reach `y`?

Each operation changes the value by an even number.

Therefore parity never changes.

Necessary condition:

```text
x and y must have the same parity
```

Equivalent:

```text
x % 2 == y % 2
```

Is it sufficient?

If they have the same parity:

```text
abs(x-y)
```

is even.

We can repeatedly add/subtract 2 until reaching `y`.

So yes.

Final characterization:

```text
possible iff x % 2 == y % 2
```

Here the parity condition is:

```text
necessary AND sufficient
```

---

# 3.17 Complete CF-Style Example 2 — Conservation

Problem:

> Two piles contain `a` and `b` stones.
> In one operation, move one stone from one pile to the other.
> Can you reach piles `x` and `y`?

Total is conserved.

Initial total:

```text
a + b
```

Target total:

```text
x + y
```

Necessary:

```text
a + b = x + y
```

Assuming piles may be adjusted one stone at a time without other restrictions, this is also sufficient for non-negative target sizes: move stones until the first pile has `x`; the second automatically becomes `y`.

So:

```text
possible iff

a + b == x + y
```

The entire operation sequence disappears behind conservation.

---

# 3.18 Complete CF-Style Example 3 — Weighted Invariant

Problem:

> Operation:
>
> `a += 2`
> `b -= 1`
>
> Can `(a,b)` reach `(x,y)`?

One invariant:

```text
a + 2*b
```

Therefore necessary:

```text
a + 2*b = x + 2*y
```

But we also need to consider direction.

After `k` operations:

```text
x = a + 2*k
y = b - k
```

So:

```text
k = (x-a) / 2
```

Need:

```text
x >= a
x-a divisible by 2
k >= 0
```

and the same `k` must satisfy:

```text
y = b-k
```

This example teaches:

```text
invariant is powerful
but may not be the only condition
```

---

# 3.19 Complete CF-Style Example 4 — Case Split

Problem:

> Starting at `a`, each operation adds 3.
> Find whether `b` is reachable.

Case 1:

```text
b < a
```

Impossible.

Case 2:

```text
b == a
```

Possible with 0 operations.

Case 3:

```text
b > a
```

Need:

```text
(b-a) % 3 == 0
```

Compact:

```text
if b < a:
    NO
else:
    YES iff (b-a) % 3 == 0
```

Case splitting follows where the mathematical behavior changes.

---

# 3.20 Conditions Checklist

After building a mathematical model, ask:

```text
1. BOUNDS
   Must something be >= 0?
   Must it stay <= n?

2. INTEGER
   Must some derived value be an integer?

3. PARITY
   Does odd/even matter?

4. DIFFERENCE
   Is only the gap important?

5. TOTAL
   Is some total conserved?

6. INVARIANT
   Does some expression remain unchanged?

7. CASES
   Does behavior change for <, =, >?

8. NECESSARY
   What MUST be true?

9. SUFFICIENT
   If it is true, can I construct/prove a solution?
```

---

# 3.21 Translation Drills

Do not code.

---

## Drill 1

> Start with an odd number.
> Every operation adds 4.

What parity can every reachable value have?

Answer:

```text
odd
```

because adding an even number does not change parity.

---

## Drill 2

> Move one item from pile A to pile B.

What is conserved?

Answer:

```text
A + B
```

---

## Drill 3

Operation:

```text
a += 3
b -= 1
```

Find a weighted invariant of form:

```text
a + c*b
```

Change:

```text
+3 - c
```

Set to zero:

```text
3 - c = 0
```

So:

```text
c = 3
```

Invariant:

```text
a + 3*b
```

---

## Drill 4

> Start at `x`.
> Operations add or subtract 6.
> Reach `y`.

A necessary condition:

```text
x and y have the same remainder modulo 6
```

For unrestricted repeated +/-6 moves, this is also sufficient.

---

# 3.22 Practice Set

For each problem identify:

```text
STATE:
CONDITIONS:
PARITY / DIFFERENCE / TOTAL:
INVARIANT:
NECESSARY:
SUFFICIENT?:
```

---

## Practice A

> Start at integer `x`.
> Each operation adds 2.
> Can you reach `y`?

---

## Practice B

> Piles contain `a`, `b`, and `c` stones.
> One operation moves one stone from one pile to another.
> What quantity is invariant?

---

## Practice C

Operation:

```text
a += 4
b -= 2
```

Find a simple weighted invariant.

---

## Practice D

> `x` and `y` must be positive integers with `x + y = S`.
> What condition must `S` satisfy for such a pair to exist?

---

## Practice E

> Start with `a`.
> Every operation subtracts 5.
> Reach exactly `b`.

Find necessary and sufficient conditions.

---

# 3.23 Practice Answers

## A

After `k`:

```text
x + 2*k = y
```

Therefore:

```text
y >= x
(y-x) % 2 == 0
```

These are necessary and sufficient.

---

## B

Moving stones does not create or destroy stones.

Invariant:

```text
a + b + c
```

---

## C

Operation changes:

```text
delta(a) = +4
delta(b) = -2
```

Try:

```text
a + c*b
```

Need:

```text
4 - 2*c = 0
```

So:

```text
c = 2
```

Invariant:

```text
a + 2*b
```

---

## D

Since:

```text
x >= 1
y >= 1
```

their minimum possible sum is:

```text
2
```

Therefore:

```text
S >= 2
```

For integer `S >= 2`, choose:

```text
x = 1
y = S-1
```

So the condition is also sufficient.

---

## E

After `k`:

```text
a - 5*k = b
```

Therefore:

```text
5*k = a-b
```

Need:

```text
a >= b
(a-b) % 5 == 0
```

These are necessary and sufficient.

---

# 3.24 Chapter Mastery Test

You are ready for Chapter 4 when you naturally ask:

```text
What MUST be true?
```

and then:

```text
Is that also ENOUGH?
```

You should recognize:

```text
add/subtract even number
        ↓
parity may stay fixed
```

```text
move resources between places
        ↓
total may be conserved
```

```text
two changing quantities
        ↓
try difference or weighted sum
```

```text
operation deltas cancel
        ↓
invariant
```

```text
invariant differs at target
        ↓
impossible immediately
```

---

# 3.25 Final Mental Engine

```text
            MATHEMATICAL MODEL
                    │
                    ▼
             What MUST hold?
                    │
          ┌─────────┼─────────┐
          │         │         │
        bounds    parity    integer
          │         │         │
          └─────────┼─────────┘
                    ▼
        What changes each operation?
                    │
                    ▼
          What stays unchanged?
                    │
          ┌─────────┼─────────┐
          │         │         │
        total   difference  invariant
          │         │         │
          └─────────┼─────────┘
                    ▼
         NECESSARY CONDITIONS
                    │
                    ▼
          Are they SUFFICIENT?
                    │
             ┌──────┴──────┐
             │             │
            YES            NO
             │             │
         solution      find another
       characterized     condition
```

The deeper habit is:

```text
Do not only search for a solution.

Search for the STRUCTURE
that every solution must obey.
```

---

# Next Chapter

```text
3. CONDITIONS & STRUCTURE
             ↓
4. ALGEBRA -> SMALLER SEARCH
```

Chapter 4 focuses on turning equations into algorithmic improvements:

```text
substitution
variable elimination
pair/triple modeling
search-dimension reduction
brute force -> formula
O(n^3) -> O(n^2) -> sometimes O(n)
```
