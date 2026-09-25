# CP Mathematical Modeling Mini-Course

## 8. Remainder / Modulo Modeling

> **Goal:** Learn to use modulo as a **modeling tool**: compress huge values into a few states, translate divisibility conditions, match complementary remainders, and recognize cyclic behavior.
>
> This chapter does **not** try to reteach number theory. The focus is:
>
> **How do I recognize that a story/problem should be represented using remainders?**

---

# Chapter Tree

```text
8. REMAINDER / MODULO MODELING
│
├── 8.1 Modulo as state compression
├── 8.2 Divisibility -> remainder 0
├── 8.3 Same remainder -> divisible difference
├── 8.4 Parity = modulo 2
├── 8.5 Last digit / cyclic states
├── 8.6 Pair-sum remainder complements
├── 8.7 Frequency of remainder classes
├── 8.8 Operations and remainder transitions
├── 8.9 Prefix remainder modeling
├── 8.10 Divisible subarray condition
├── 8.11 Normalize negative modulo
├── 8.12 Reduce before operating
├── 8.13 When modulo loses too much information
├── 8.14 Recognizing modulo problems
└── 8.15 Complete CF-style examples
```

Central engine:

```text
LARGE / IRRELEVANT EXACT VALUES
              │
              ▼
      What property matters?
              │
      ┌───────┴────────┐
      │                │
 divisibility      cyclic behavior
      │                │
      └───────┬────────┘
              ▼
          value % m
              │
              ▼
      only m possible states
              │
              ▼
 frequency / transition /
 complement / prefix model
```

---

# 8.1 Modulo as State Compression

Suppose the only thing that matters is a number's remainder when divided by 5.

Numbers:

```text
2
7
12
17
22
1000000007
```

Many exact values are different.

But modulo 5:

```text
2  % 5 = 2
7  % 5 = 2
12 % 5 = 2
17 % 5 = 2
22 % 5 = 2
```

For a problem depending only on remainder modulo 5, all these numbers belong to the same state:

```text
remainder = 2
```

Instead of potentially billions of values:

```text
0,1,2,3,4,5,6,...
```

we have only:

```text
0,1,2,3,4
```

states.

Visual:

```text
all integers
     │
     │ % 5
     ▼
┌────┬────┬────┬────┬────┐
│ r0 │ r1 │ r2 │ r3 │ r4 │
└────┴────┴────┴────┴────┘
```

This is **state compression**.

---

# 8.2 Divisibility -> Remainder 0

Statement:

> `x` is divisible by `m`.

Translate directly:

```text
x % m == 0
```

Examples:

```text
x divisible by 2
=> x % 2 == 0

x divisible by 7
=> x % 7 == 0
```

This is the first modulo translation to make automatic.

```text
"divisible by m"
       ↓
"remainder 0 modulo m"
```

---

# 8.3 Same Remainder -> Divisible Difference

Suppose:

```text
a % m == b % m
```

Then `a` and `b` have the same remainder modulo `m`.

Write:

```text
a = q1*m + r
b = q2*m + r
```

Subtract:

```text
a-b
=
(q1-q2)*m
```

Therefore:

```text
(a-b) % m == 0
```

So:

```text
a % m == b % m
```

is equivalent to:

```text
a-b is divisible by m
```

This connection appears constantly in CF.

Mental translation:

```text
difference divisible by m
        ↕
same remainder modulo m
```

---

# 8.4 Example — Difference Multiple of 3

Problem:

> Count pairs `(i,j)` such that:

```text
a[i] - a[j]
```

is divisible by 3.

Instead of comparing differences directly:

```text
(a[i]-a[j]) % 3 == 0
```

translate to:

```text
a[i] % 3 == a[j] % 3
```

Now classify all numbers into:

```text
remainder 0
remainder 1
remainder 2
```

If frequencies are:

```text
f0
f1
f2
```

valid pairs come from within the same remainder group.

The huge exact values disappear.

---

# 8.5 Parity Is Just Modulo 2

Parity:

```text
even -> remainder 0 modulo 2
odd  -> remainder 1 modulo 2
```

So parity modeling is simply:

```text
modulo with m = 2
```

Example:

```text
a and b have same parity
```

means:

```text
a % 2 == b % 2
```

Equivalent:

```text
(a-b) % 2 == 0
```

This helps generalize parity intuition to arbitrary moduli.

```text
parity:
2 states

mod 3:
3 states

mod 5:
5 states
```

---

# 8.6 Remainder of a Sum

If:

```text
a % m = r1
b % m = r2
```

then:

```text
(a+b) % m
=
(r1+r2) % m
```

Example modulo 5:

```text
a remainder = 2
b remainder = 4

sum remainder:
(2+4) % 5
= 1
```

This means exact values are unnecessary if the final question only asks about the sum modulo `m`.

---

# 8.7 Pair-Sum Remainder Complements

Problem:

> Count pairs whose sum is divisible by `m`.

We need:

```text
(a+b) % m == 0
```

Suppose:

```text
a % m = r
```

What remainder must `b` have?

We need:

```text
(r + needed) % m == 0
```

Therefore:

```text
needed = (m-r) % m
```

This is the modulo version of complement thinking.

Compare:

```text
ordinary target sum:
needed = S-x
```

with:

```text
remainder target 0:
needed remainder = (m-r)%m
```

---

# 8.8 Example — Sum Divisible by 5

Remainders:

```text
0 1 2 3 4
```

Complement pairs:

```text
0 <-> 0
1 <-> 4
2 <-> 3
3 <-> 2
4 <-> 1
```

Because:

```text
0+0 = 0 mod 5
1+4 = 0 mod 5
2+3 = 0 mod 5
```

Visual:

```text
mod 5

0 ─── 0
1 ─── 4
2 ─── 3
```

Once values are compressed into remainder classes, the counting problem becomes tiny.

---

# 8.9 General Target Remainder

Sometimes we need:

```text
(a+b) % m == T
```

Suppose:

```text
a % m = r
```

Need remainder `s` satisfying:

```text
(r+s) % m = T
```

So:

```text
s = (T-r+m) % m
```

This is a very useful general pattern:

```text
needed remainder
=
(target - current + m) % m
```

---

# 8.10 Frequency of Remainder Classes

Given array:

```text
a[0], a[1], ..., a[n-1]
```

Instead of frequency of exact values:

```text
freq[value]
```

store:

```text
freq[a[i] % m]
```

There are only:

```text
m
```

classes.

Example `m = 4`:

```text
freq[0]
freq[1]
freq[2]
freq[3]
```

This is useful when validity depends only on remainder.

Mental question:

```text
Does the problem care about the number,
or only its remainder?
```

If only remainder matters:

```text
compress immediately
```

---

# 8.11 Operations as Remainder Transitions

Modulo is also useful for repeated operations.

Suppose:

```text
x -> x + 3
```

and only:

```text
x % 5
```

matters.

Then remainder transition is:

```text
r -> (r+3)%5
```

Example:

```text
0 -> 3
3 -> 1
1 -> 4
4 -> 2
2 -> 0
```

Visual:

```text
0 -> 3 -> 1 -> 4 -> 2
^                   |
|___________________|
```

The exact value keeps growing, but remainder moves through only five states.

This is a cyclic-state model.

---

# 8.12 Last Digit = Modulo 10

If a problem asks only about the last decimal digit:

```text
x % 10
```

is the state.

Example:

```text
1234567
```

last digit:

```text
7
```

because:

```text
1234567 % 10 = 7
```

Similarly:

```text
last two digits -> x % 100
last three digits -> x % 1000
```

This is another story-to-math translation.

---

# 8.13 Cyclic Positions

Suppose positions repeat:

```text
0 1 2 3 0 1 2 3 0 ...
```

Cycle length:

```text
4
```

Position after `k` steps:

```text
k % 4
```

If starting from position `s`:

```text
(s+k) % 4
```

General:

```text
cycle length = m
```

then:

```text
state = value % m
```

Trigger words:

```text
cycle
clock
round
repeat
wrap around
every m steps
last digit
days of week
circular position
```

often suggest modulo modeling.

---

# 8.14 Prefix Sums + Modulo

Now an important modeling connection.

Define prefix sum:

```text
P[i] =
a[0] + a[1] + ... + a[i]
```

A subarray sum from:

```text
l to r
```

is:

```text
P[r] - P[l-1]
```

Suppose we want the subarray sum divisible by `m`.

Need:

```text
(P[r] - P[l-1]) % m == 0
```

From Section 8.3:

```text
difference divisible by m
```

means:

```text
P[r] % m == P[l-1] % m
```

This is a major modeling transformation:

```text
subarray sum divisible by m
        ↓
difference of two prefix sums divisible by m
        ↓
two prefix sums have same remainder
```

---

# 8.15 Visualizing Prefix Remainders

Suppose prefix remainders are:

```text
P0 % m = 2
P1 % m = 0
P2 % m = 2
P3 % m = 1
P4 % m = 2
```

Whenever two prefix states have the same remainder:

```text
their difference is divisible by m
```

For remainder `2`:

```text
P0
P2
P4
```

Any pair among these defines a divisible-sum segment between them.

So:

```text
subarray counting
```

becomes:

```text
count equal prefix remainder pairs
```

This connects:

```text
prefix sums
+
same remainder
+
frequency counting
```

---

# 8.16 The Empty Prefix

When working with prefix sums, include the prefix before the array starts:

```text
prefix sum = 0
```

Its remainder:

```text
0 % m = 0
```

Why?

Because a subarray starting from index 0 may itself have sum divisible by `m`.

Example:

```text
a = [2,3]
m = 5
```

Full prefix sum:

```text
2+3 = 5
```

Remainder:

```text
0
```

It must match the initial empty prefix remainder `0`.

So initialize:

```text
freq[0] = 1
```

before processing.

This is not a trick to memorize blindly.

It represents:

```text
prefix before taking any elements
```

---

# 8.17 Online Prefix-Remainder Contribution

Maintain:

```text
prefix_mod
```

For each element:

```text
prefix_mod =
(prefix_mod + a[i]) % m
```

Suppose this remainder has appeared:

```text
f
```

times before.

Then the current prefix creates:

```text
f
```

new subarrays whose sum is divisible by `m`.

Why?

Each previous prefix with the same remainder can be the left boundary.

So:

```text
answer += freq[prefix_mod]
freq[prefix_mod]++
```

This combines Chapter 7's contribution technique with modulo modeling.

---

# 8.18 Example — Divisible Subarrays

Array:

```text
[2, 3, 1]
```

Let:

```text
m = 5
```

Start:

```text
prefix = 0
freq[0] = 1
```

Process `2`:

```text
prefix = 2
remainder = 2

previous remainder-2 prefixes = 0
new valid subarrays = 0
```

Process `3`:

```text
prefix = 5
remainder = 0

previous remainder-0 prefixes = 1
new valid subarrays = 1
```

This represents:

```text
[2,3]
```

Process `1`:

```text
prefix = 6
remainder = 1

previous remainder-1 prefixes = 0
```

Answer:

```text
1
```

---

# 8.19 Negative Modulo Normalization

Mathematically, we often want remainders in:

```text
0 ... m-1
```

In C++, negative `%` can produce a negative result.

Example:

```cpp
-3 % 5
```

is:

```text
-3
```

in C++.

To normalize:

```cpp
((x % m) + m) % m
```

Example:

```text
x = -3
m = 5

-3 % 5 = -3
-3 + 5 = 2
2 % 5 = 2
```

So normalized remainder:

```text
2
```

Useful pattern:

```cpp
int r = ((x % m) + m) % m;
```

For subtraction:

```cpp
((a % m - b % m) + m) % m
```

or generally normalize after the subtraction.

---

# 8.20 Why Add m Once?

Suppose normalized:

```text
0 <= a%m < m
0 <= b%m < m
```

Then:

```text
(a%m) - (b%m)
```

lies in:

```text
-(m-1) ... (m-1)
```

So adding one `m` is enough to make it non-negative:

```text
(a%m - b%m + m)
```

Then `% m` puts it into:

```text
0 ... m-1
```

This explains the standard expression instead of merely memorizing it.

---

# 8.21 Reduce Before Operating

Modulo arithmetic allows:

```text
(a+b) % m
=
((a%m) + (b%m)) % m
```

and:

```text
(a*b) % m
=
((a%m) * (b%m)) % m
```

Conceptually:

```text
huge numbers
    ↓
reduce to remainder states
    ↓
perform operation
    ↓
reduce again
```

Example:

```text
1000000008 % 7
```

If only modulo 7 matters, there is no need to carry the huge exact number through every step.

---

# 8.22 Modulo Does Not Preserve Everything

Important warning.

Suppose:

```text
a % 5 = b % 5
```

This tells us:

```text
a-b divisible by 5
```

But it does NOT tell us:

```text
a == b
```

Example:

```text
7 % 5 = 2
12 % 5 = 2
```

but:

```text
7 != 12
```

Modulo intentionally loses information.

Use it only when the lost information is irrelevant to the problem.

Mental check:

```text
If I replace x by x%m,
do I still know everything needed
to decide validity?
```

If no:

```text
do not compress that aggressively
```

---

# 8.23 Modulo + Inequalities Warning

Suppose the problem asks:

```text
a < b
```

Knowing only:

```text
a % m
b % m
```

is not enough.

Example:

```text
a = 12
b = 7
```

Modulo 5:

```text
a % 5 = 2
b % 5 = 2
```

Remainders are equal, but:

```text
a > b
```

Exact magnitude information was lost.

Modulo is strongest for:

```text
divisibility
cyclic state
remainder relations
```

not arbitrary inequalities.

---

# 8.24 Recognizing Modulo Modeling

Trigger phrases:

```text
divisible by m
multiple of m
same parity
same remainder
difference divisible
sum divisible
last digit
cycle length
wrap around
every m operations
repeat after m
clock position
remainder classes
```

Also watch equations:

```text
a-b divisible by m
      ↓
a%m == b%m
```

```text
a+b divisible by m
      ↓
remainders complement
```

```text
subarray sum divisible by m
      ↓
equal prefix remainders
```

These should become automatic translations.

---

# 8.25 Complete CF-Style Example 1 — Pair Difference

Problem:

> Count pairs `i < j` such that:

```text
a[i] - a[j]
```

is divisible by `m`.

Translate:

```text
a[i] % m == a[j] % m
```

Group by remainder.

For each remainder `r` with frequency `f[r]`:

```text
pairs =
f[r] * (f[r]-1) / 2
```

Total:

```text
sum over r
```

The exact values are irrelevant.

---

# 8.26 Complete CF-Style Example 2 — Pair Sum

Problem:

> Count pairs `i < j` such that:

```text
(a[i] + a[j]) % m == 0
```

Process current:

```text
r = a[j] % m
```

Required earlier remainder:

```text
need = (m-r) % m
```

Contribution:

```text
freq[need]
```

Then:

```text
freq[r]++
```

Every pair is counted exactly once because the current element owns the pair as its right endpoint.

---

# 8.27 Complete CF-Style Example 3 — Reachability

Problem:

> Start at `x`.
> Every operation adds `d`.
> Can you reach `y`?

After `k` operations:

```text
x + k*d = y
```

Therefore:

```text
y-x = k*d
```

Necessary:

```text
(y-x) % d == 0
```

If only additions are allowed, also:

```text
y >= x
```

So modulo captures divisibility, but direction/bounds still matter.

This is an important example of combining modulo with other conditions.

---

# 8.28 Complete CF-Style Example 4 — Divisible Subarrays

Problem:

> Count subarrays whose sum is divisible by `m`.

Subarray sum:

```text
P[r] - P[l-1]
```

Need:

```text
(P[r] - P[l-1]) % m == 0
```

Equivalent:

```text
P[r] % m
=
P[l-1] % m
```

Therefore:

```text
count pairs of equal prefix remainders
```

Online:

```text
freq[0] = 1

for each element:
    update prefix remainder
    answer += freq[prefix remainder]
    freq[prefix remainder]++
```

The modeling chain is the important part:

```text
subarray
   ↓
difference of prefixes
   ↓
divisible difference
   ↓
same remainder
   ↓
frequency counting
```

---

# 8.29 Complete CF-Style Example 5 — Cyclic State

Problem:

> There are `m` positions arranged in a circle:
>
> `0,1,...,m-1`.
>
> Start at `s`.
> Move forward `k` positions.
> Where do you finish?

Without modulo:

```text
s+k
```

may exceed the final position.

Circular normalization:

```text
answer = (s+k) % m
```

Example:

```text
m = 5
s = 4
k = 3
```

Raw:

```text
7
```

Circular position:

```text
7 % 5 = 2
```

---

# 8.30 Modeling Checklist

When modulo seems relevant, ask:

```text
1. WHAT IS m?

2. WHAT DOES remainder represent?

3. DO EXACT VALUES MATTER,
   OR ONLY REMAINDERS?

4. IS THE CONDITION:
   divisible?
   same remainder?
   complementary remainder?
   cyclic state?

5. CAN I GROUP BY remainder?

6. IF COUNTING PAIRS:
   what remainder partner is needed?

7. IF SUBARRAY:
   can I use prefix differences?

8. IF OPERATIONS:
   how does remainder transition?

9. DO I ALSO NEED:
   bounds?
   sign?
   ordering?
   exact magnitude?

10. CAN NEGATIVE VALUES OCCUR?
    normalize modulo if needed.
```

---

# 8.31 Common Mistakes

## Mistake 1 — Treating equal remainder as equal value

```text
7 % 5 == 12 % 5
```

does not mean:

```text
7 == 12
```

---

## Mistake 2 — Forgetting complement normalization

For:

```text
needed = target-current
```

in remainder space, use normalization:

```text
needed = (target-current+m) % m
```

---

## Mistake 3 — Forgetting the empty prefix

For divisible-subarray counting:

```text
freq[0] = 1
```

represents the prefix before the array begins.

---

## Mistake 4 — Using modulo when magnitude matters

Modulo cannot by itself answer:

```text
which number is larger?
```

---

## Mistake 5 — Forgetting extra conditions

From:

```text
(y-x) % d == 0
```

you cannot conclude reachability by repeated `+d` unless:

```text
y >= x
```

also holds.

---

# 8.32 Translation Drills

Do not code.

---

## Drill 1

```text
a-b divisible by 7
```

Translate:

```text
a % 7 == b % 7
```

---

## Drill 2

Current remainder modulo 8:

```text
r = 3
```

Need pair sum divisible by 8.

Partner remainder:

```text
(8-3)%8 = 5
```

---

## Drill 3

Current remainder:

```text
r = 0
```

Modulo:

```text
m = 6
```

Partner for divisible sum:

```text
(6-0)%6 = 0
```

---

## Drill 4

Cycle length:

```text
7
```

Start:

```text
5
```

Move:

```text
10
```

Final:

```text
(5+10)%7
= 15%7
= 1
```

---

## Drill 5

Subarray sum divisible by `m`.

Translate:

```text
P[r] % m == P[l-1] % m
```

---

# 8.33 Practice Set

For each problem write:

```text
MODULUS:
STATE:
ORIGINAL CONDITION:
REMAINDER CONDITION:
FREQUENCY/COMPLEMENT/TRANSITION:
EXTRA NON-MODULO CONDITIONS:
```

---

## Practice A

> Count pairs whose difference is divisible by 4.

---

## Practice B

> Count pairs whose sum is divisible by 6.

---

## Practice C

> Start at `a`, repeatedly add 5, reach `b`.

---

## Practice D

> Count subarrays whose sum is divisible by 7.

---

## Practice E

> A pointer moves around `m` circular positions. Starting at `s`, move `k` steps.

---

# 8.34 Practice Answers

## A

```text
group by:

a[i] % 4
```

Pairs must come from the same remainder class.

---

## B

For remainder `r`:

```text
need = (6-r)%6
```

Use frequencies while processing or count complementary remainder groups carefully.

---

## C

After `k` operations:

```text
a + 5*k = b
```

Need:

```text
b >= a
(b-a) % 5 == 0
```

---

## D

Use prefix sums.

Need:

```text
P[r] % 7 == P[l-1] % 7
```

Count equal prefix remainder pairs.

Include:

```text
freq[0] = 1
```

for the empty prefix.

---

## E

Final position:

```text
(s+k) % m
```

---

# 8.35 Chapter Mastery Test

You are ready for the next chapter when you automatically translate:

```text
difference divisible by m
```

into:

```text
same remainder modulo m
```

and:

```text
sum divisible by m
```

into:

```text
complementary remainder classes
```

and:

```text
subarray sum divisible by m
```

into:

```text
equal prefix remainders
```

You should also see:

```text
cycle / wrap around
```

and think:

```text
state modulo cycle length
```

Most importantly, you should ask:

```text
Does the exact value matter?

Or can I replace millions of possible values
with only m remainder states?
```

---

# 8.36 Final Mental Engine

```text
             PROBLEM CONDITION
                    │
                    ▼
       DIVISIBILITY / CYCLE PRESENT?
                    │
                    ▼
               CHOOSE m
                    │
                    ▼
              STATE = x % m
                    │
          ┌─────────┼─────────┐
          │         │         │
       SAME       SUM       OPERATION
     REMAINDER  COMPLEMENT  TRANSITION
          │         │         │
          ▼         ▼         ▼
      frequency   needed r   new state
          │         │         │
          └─────────┼─────────┘
                    ▼
              PREFIX CASE?
                    │
                    ▼
          difference of prefixes
                    │
                    ▼
           equal remainder states
                    │
                    ▼
               FINAL MODEL
```

The core habit:

```text
Modulo is not only an arithmetic operator.

It is a way to replace exact values
with the small state that actually matters.
```

---

# Next Chapter

```text
8. REMAINDER / MODULO MODELING
               ↓
9. INTERVAL & BOUNDARY MODELING
```

Chapter 9 will focus on translating:

```text
ranges
overlaps
coverage
valid intersections
distance between intervals
endpoint events
inclusive vs exclusive boundaries
```

into clean mathematical conditions.

This is useful for many implementation, greedy, geometry-like, scheduling, and 1200-1900 CF problems.
