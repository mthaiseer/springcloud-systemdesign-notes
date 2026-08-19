# CP Mathematical Modeling Mini-Course
# 0. Foundation

> **Goal:** Build the minimum algebra toolkit needed to translate Codeforces statements into mathematics quickly.
>
> This chapter is **not** a general school-math course. Every concept is taught only to the depth needed for competitive-programming modeling.

---

# Foundation Tree

```text
0. FOUNDATION
│
├── 0.1 Variables & Expressions
│
├── 0.2 Basic Equations
│
├── 0.3 Rearranging Equations
│
├── 0.4 Inequalities
│
├── 0.5 Min / Max
│
├── 0.6 Absolute Value
│
└── 0.7 Floor / Ceil
```

The overall purpose is:

```text
English statement
      ↓
Identify quantities
      ↓
Give them variables
      ↓
Write relationships
      ↓
Equation / inequality / formula
      ↓
Algorithm
```

---

# 0.1 Variables & Expressions

## 0.1.1 What is a variable?

A variable is simply a **name for a quantity**.

Suppose a problem says:

> Alice has some apples.

Instead of repeatedly saying "number of apples Alice has", write:

```text
A = number of apples Alice has
```

or:

```text
x = number of apples Alice has
```

The letter itself does not matter.

What matters is:

```text
REAL QUANTITY
     ↓
VARIABLE
```

Example:

```text
number of students      → n
number of operations    → k
number of red balls     → R
number of blue balls    → B
current position        → x
target position         → y
```

---

## 0.1.2 Expression vs equation

An **expression** describes a quantity.

Examples:

```text
x + 3
2x
n - k
3a + 2b
```

An **equation** says two quantities are equal.

Examples:

```text
x + 3 = 10
a + b = S
2x = n
```

Visual:

```text
Expression:
    x + 5

Equation:
    x + 5 = 20
            ↑
          equality
```

---

## 0.1.3 Translate common phrases

### "3 more than x"

```text
x
│
├── existing amount
│
└── add 3
     ↓

x + 3
```

So:

```text
3 more than x  →  x + 3
```

### "3 less than x"

```text
x
│
└── remove 3
     ↓

x - 3
```

So:

```text
3 less than x  →  x - 3
```

### "twice x"

```text
x + x
  ↓
 2x
```

### "k groups, each containing x items"

```text
Group 1: x
Group 2: x
Group 3: x
...
Group k: x

Total = x + x + ... + x
              k times

      = kx
```

---

## 0.1.4 CP examples

### Example 1

> There are `n` boxes. Each box contains `k` balls.

Total balls:

\[
nk
\]

### Example 2

> Alice has `x` coins. Bob has 5 more coins than Alice.

```text
Alice = x

Bob:
x + 5
```

So:

\[
B=x+5
\]

### Example 3

> You start with `n` points and lose `p` points.

Remaining:

\[
n-p
\]

---

## 0.1.5 Translation drill

Translate before looking at answers.

### Q1
There are `n` rows, each containing `m` chairs.

### Q2
Bob has `x` chocolates. Alice has 7 fewer.

### Q3
A machine produces `k` items each minute for `t` minutes.

### Answers

```text
Q1 → nm
Q2 → x - 7
Q3 → kt
```

---

# 0.2 Basic Equations

## 0.2.1 What does an equation represent?

An equation describes a relationship that **must be true**.

Example:

> Alice and Bob together have 20 coins.

Define:

```text
A = Alice's coins
B = Bob's coins
```

"Together" means addition:

\[
A+B=20
\]

Visual:

```text
Alice      Bob
  A    +    B
   \       /
    \     /
     TOTAL
       20

A + B = 20
```

---

## 0.2.2 Common English → equation patterns

```text
"total is S"
        ↓
x + y = S

"difference is D"
        ↓
x - y = D
(or |x-y| = D if direction does not matter)

"x is twice y"
        ↓
x = 2y

"x is 5 greater than y"
        ↓
x = y + 5

"x and y are equal"
        ↓
x = y
```

---

## 0.2.3 Multiple statements combine

Problem:

> Alice has 4 more coins than Bob. Together they have 20.

Define:

```text
A = Alice
B = Bob
```

Statement 1:

\[
A=B+4
\]

Statement 2:

\[
A+B=20
\]

Now combine:

```text
A = B + 4
      ↓ substitute

A + B = 20

(B + 4) + B = 20
```

So:

\[
2B+4=20
\]

\[
2B=16
\]

\[
B=8
\]

\[
A=12
\]

The important CP skill is not solving this particular school problem.

The important skill is:

```text
Statement 1 → equation
Statement 2 → equation
              ↓
          combine them
```

---

## 0.2.4 CP-style example

> We need three integers `x, y, z` whose sum is `S`.

The mathematical model is simply:

\[
x+y+z=S
\]

This equation may later let us eliminate one variable.

---

## 0.2.5 Translation drill

### Q1
Two numbers `x` and `y` have sum `S`.

### Q2
`a` is 3 times `b`.

### Q3
After adding 4 to `x`, it becomes equal to `y`.

### Answers

```text
Q1 → x + y = S
Q2 → a = 3b
Q3 → x + 4 = y
```

---

# 0.3 Rearranging Equations

This is one of the most important skills in CP modeling.

The goal is:

> If I know all but one quantity, can I calculate the missing one directly?

---

## 0.3.1 Simple rearrangement

Given:

\[
x+y=S
\]

We want `y`.

Think of a balance:

```text
x + y = S

remove x from the left
and remove x from the right

x + y - x = S - x

y = S - x
```

So:

\[
\boxed{y=S-x}
\]

---

## 0.3.2 Three-variable example

Given:

\[
x+y+z=S
\]

Find `z`.

```text
x + y + z = S

remove x and y from both sides

z = S - x - y
```

So:

\[
\boxed{z=S-x-y}
\]

This matters enormously in brute-force reduction.

---

## 0.3.3 From O(n³) to O(n²)

Suppose you want all triples satisfying:

\[
x+y+z=S
\]

Naive thinking:

```text
try every x
    try every y
        try every z
            check x+y+z == S
```

That is roughly:

```text
n × n × n = O(n³)
```

But mathematics says:

\[
z=S-x-y
\]

So:

```text
try every x
    try every y
        z = S - x - y
        check whether z is valid
```

Now:

```text
n × n = O(n²)
```

ASCII transformation:

```text
x + y + z = S
      │
      └──── solve for z
               ↓
         z = S-x-y
               ↓
     no need to loop over z
               ↓
          O(n³) → O(n²)
```

This is exactly how math modeling becomes algorithmic optimization.

---

## 0.3.4 Rearranging multiplication

Given:

\[
3k=n
\]

Then:

\[
k=n/3
\]

Given:

\[
2x+5=y
\]

Then:

\[
2x=y-5
\]

\[
x=(y-5)/2
\]

---

## 0.3.5 CP drill

### Q1

\[
a+b=100
\]

Express `b`.

### Q2

\[
x+y+z=50
\]

Express `y`.

### Q3

\[
3k+2=n
\]

Express `k`.

### Answers

\[
b=100-a
\]

\[
y=50-x-z
\]

\[
k=\frac{n-2}{3}
\]

---

# 0.4 Inequalities

Equations mean:

```text
exactly equal
```

Inequalities mean:

```text
allowed range / limit
```

---

## 0.4.1 Core symbols

```text
x < 5   → x is less than 5
x ≤ 5   → x is at most 5
x > 5   → x is greater than 5
x ≥ 5   → x is at least 5
```

Very important translations:

```text
"at least k"       → x ≥ k
"at most k"        → x ≤ k
"no more than k"   → x ≤ k
"cannot exceed k"  → x ≤ k
"minimum k"        → x ≥ k
"maximum k"        → x ≤ k
```

---

## 0.4.2 Number-line picture

For:

\[
x\le 5
\]

```text
<====================●-------------------->
                     5

all values left of 5 are allowed
5 itself is allowed
```

For:

\[
x>5
\]

```text
<--------------------○====================>
                     5

5 itself is NOT allowed
all values greater than 5 are allowed
```

---

## 0.4.3 Resource example

> Each operation consumes 3 coins. You have `n` coins.

Suppose you perform `k` operations.

Coins required:

\[
3k
\]

You cannot use more than `n` coins:

\[
3k\le n
\]

So:

\[
k\le n/3
\]

This is the beginning of **answer bounding**.

---

## 0.4.4 Multiple constraints

Suppose each team needs:

```text
2 red balls
1 blue ball
```

Available:

```text
R red
B blue
```

For `k` teams:

Red requirement:

\[
2k\le R
\]

Blue requirement:

\[
k\le B
\]

Both must be true.

ASCII:

```text
            k teams
              │
      ┌───────┴────────┐
      │                │
 need 2k red        need k blue
      │                │
 2k ≤ R             k ≤ B
      └───────┬────────┘
              │
         BOTH required
```

---

# 0.5 Min / Max

`min` and `max` become important when multiple conditions compete.

---

## 0.5.1 Minimum

```text
min(a,b)
```

means:

> the smaller of `a` and `b`

Example:

```text
min(4,7) = 4
```

---

## 0.5.2 Maximum

```text
max(a,b)
```

means:

> the larger of `a` and `b`

Example:

```text
max(4,7) = 7
```

---

## 0.5.3 Limiting-resource idea

Recall:

\[
2k\le R
\]

Therefore:

\[
k\le \lfloor R/2\rfloor
\]

And:

\[
k\le B
\]

So `k` must respect both limits:

```text
red limit   = floor(R/2)
blue limit  = B

answer cannot exceed either
```

Therefore:

\[
\boxed{k=\min(\lfloor R/2\rfloor,B)}
\]

Visual:

```text
Maximum allowed by red  = 8
Maximum allowed by blue = 5

Need BOTH resources.

8 ─────────┐
           ├── smaller limit wins → 5
5 ─────────┘
```

This is a common CP pattern:

```text
many upper bounds
      ↓
take minimum
```

Similarly:

```text
many lower bounds
      ↓
take maximum
```

---

## 0.5.4 Lower-bound example

Suppose an answer `x` must satisfy:

\[
x\ge 4
\]

and:

\[
x\ge 7
\]

To satisfy both:

\[
x\ge \max(4,7)=7
\]

---

# 0.6 Absolute Value

Absolute value is best understood in CP as:

> **distance without caring about direction**

---

## 0.6.1 Simple idea

\[
|5|=5
\]

\[
|-5|=5
\]

The sign disappears because we care about magnitude.

---

## 0.6.2 Distance between two positions

Suppose:

```text
a = 3
b = 10
```

Distance:

\[
|a-b|
\]

\[
|3-10|=7
\]

If reversed:

\[
|10-3|=7
\]

Same distance.

ASCII number line:

```text
0---1---2---3---4---5---6---7---8---9---10
            A                           B
            |<--------- 7 ----------->|

distance = |3 - 10| = 7
```

---

## 0.6.3 Common CP translations

```text
"difference between a and b"
            ↓
          |a-b|

"distance between positions x and y"
            ↓
          |x-y|

"within distance k"
            ↓
         |x-y| ≤ k

"more than k apart"
            ↓
         |x-y| > k
```

---

## 0.6.4 Movement example

You are at position `x`.
You want to reach position `y`.

If one operation moves you exactly 1 position, number of steps needed:

\[
|x-y|
\]

This avoids casework:

```text
if x < y: y-x
if x > y: x-y
```

Both are represented by:

\[
|x-y|
\]

---

# 0.7 Floor / Ceil

Floor and ceil appear constantly in CP because values are often integers.

---

## 0.7.1 Floor

Floor means:

> round DOWN to the nearest integer

Notation:

\[
\lfloor x\rfloor
\]

Examples:

\[
\lfloor 3.9\rfloor=3
\]

\[
\lfloor 5\rfloor=5
\]

---

## 0.7.2 Complete groups → floor

Problem:

> You have 17 candies. Each full box requires 5 candies. How many complete boxes?

```text
17 candies

[*****]  box 1
[*****]  box 2
[*****]  box 3
[**   ]  incomplete

complete boxes = 3
```

Mathematically:

\[
\left\lfloor\frac{17}{5}\right\rfloor=3
\]

General:

\[
\boxed{\left\lfloor\frac nk\right\rfloor}
\]

means:

> number of complete groups of size `k` that fit inside `n`.

For positive integers, C++ integer division already does this:

```cpp
int groups = n / k;
```

---

## 0.7.3 Ceil

Ceil means:

> round UP to the nearest integer

Notation:

\[
\lceil x\rceil
\]

Examples:

\[
\lceil 3.1\rceil=4
\]

\[
\lceil 5\rceil=5
\]

---

## 0.7.4 Minimum containers needed → ceil

Problem:

> 17 students. One bus can hold at most 5 students. What is the minimum number of buses?

```text
Bus 1: 5
Bus 2: 5
Bus 3: 5
Bus 4: 2

total buses = 4
```

Mathematically:

\[
\left\lceil\frac{17}{5}\right\rceil=4
\]

For positive integers:

```cpp
(n + k - 1) / k
```

So:

```cpp
int buses = (n + k - 1) / k;
```

---

## 0.7.5 Floor vs ceil intuition

Ask:

```text
"How many COMPLETE groups fit?"
              ↓
            FLOOR
```

Ask:

```text
"How many groups are NEEDED
to contain everything?"
              ↓
             CEIL
```

Visual:

```text
n items
│
├── fit complete groups?
│       ↓
│     floor(n/k)
│
└── containers required?
        ↓
      ceil(n/k)
```

---

# Foundation Integration

Now combine everything.

Suppose:

> There are `R` red balls and `B` blue balls. Each team requires 2 red balls and 3 blue balls. Find the maximum possible number of teams.

## Step 1 — define answer

```text
k = number of teams
```

## Step 2 — model red resource

Each team uses 2 red.

`k` teams use:

\[
2k
\]

Available:

\[
R
\]

Therefore:

\[
2k\le R
\]

So:

\[
k\le \left\lfloor\frac R2\right\rfloor
\]

## Step 3 — model blue resource

Each team uses 3 blue.

`k` teams use:

\[
3k
\]

Therefore:

\[
3k\le B
\]

So:

\[
k\le \left\lfloor\frac B3\right\rfloor
\]

## Step 4 — combine limits

`k` must satisfy both:

\[
k\le \left\lfloor\frac R2\right\rfloor
\]

and:

\[
k\le \left\lfloor\frac B3\right\rfloor
\]

Therefore:

\[
\boxed{
k=
\min
\left(
\left\lfloor\frac R2\right\rfloor,
\left\lfloor\frac B3\right\rfloor
\right)
}
\]

Complete reasoning:

```text
Story
  ↓
k = number of teams
  ↓
2k ≤ R
3k ≤ B
  ↓
k ≤ floor(R/2)
k ≤ floor(B/3)
  ↓
both must hold
  ↓
smaller upper bound wins
  ↓
min(floor(R/2), floor(B/3))
```

This is mathematical modeling.

---

# Foundation Translation Cheat Sheet

```text
WORDS                           MATH

total                           +
remaining                       total - used
together                        +
difference                      subtraction / |a-b|
x more than y                   y + x
x less than y                   y - x
twice x                         2x
k groups of x                   kx

exactly                         =
at least                        ≥
at most                         ≤
more than                       >
less than                       <

distance                        |a-b|
within k                        |a-b| ≤ k

complete groups                 floor(n/k)
minimum groups needed           ceil(n/k)

many upper bounds               min(...)
many lower bounds               max(...)
```

---

# Foundation Problem-Solving Engine

Before coding, run this:

```text
READ
 │
 ▼
What quantities exist?
 │
 ▼
Give each one a variable
 │
 ▼
What relationships are stated?
 │
 ├── total?
 ├── difference?
 ├── limit?
 ├── repeated groups?
 └── distance?
 │
 ▼
Write expression/equation/inequality
 │
 ▼
Can I rearrange it?
 │
 ▼
Can I calculate one variable directly?
 │
 ▼
Do I need floor / ceil / min / max?
 │
 ▼
Only now think about code
```

---

# Mini Practice Set

Do these **without coding first**.

## Problem 1

You have `n` candies. Each child receives exactly `k` candies.

**Question:** Maximum number of children who can receive a complete share?

Hint:

```text
complete groups
```

---

## Problem 2

There are `n` students. Each bus can carry at most `k`.

**Question:** Minimum buses required?

Hint:

```text
containers needed
```

---

## Problem 3

Two numbers `x` and `y` satisfy:

\[
x+y=S
\]

If you choose `x`, express `y`.

---

## Problem 4

You are at coordinate `a`, and your destination is `b`.

Each move changes your coordinate by exactly `1`.

**Question:** Minimum moves?

---

## Problem 5

Each machine requires:

```text
3 units of resource A
2 units of resource B
```

You have:

```text
A units of resource A
B units of resource B
```

Define:

```text
k = number of machines
```

Write:

1. inequality for resource A
2. inequality for resource B
3. formula for maximum `k`

---

# Mini Practice Answers

## Problem 1

\[
\boxed{\left\lfloor n/k\right\rfloor}
\]

---

## Problem 2

\[
\boxed{\left\lceil n/k\right\rceil}
\]

For positive integers:

```cpp
(n + k - 1) / k
```

---

## Problem 3

\[
\boxed{y=S-x}
\]

---

## Problem 4

\[
\boxed{|a-b|}
\]

---

## Problem 5

Resource A:

\[
3k\le A
\]

Resource B:

\[
2k\le B
\]

Therefore:

\[
\boxed{
k=
\min
\left(
\left\lfloor A/3\right\rfloor,
\left\lfloor B/2\right\rfloor
\right)
}
\]

---

# Foundation Mastery Test

You are ready for **1. Story → Mathematics** when these feel natural:

```text
x + y = S
      ↓
y = S - x
```

```text
3k ≤ n
      ↓
k ≤ floor(n/3)
```

```text
several upper bounds
      ↓
take min
```

```text
several lower bounds
      ↓
take max
```

```text
distance between a,b
      ↓
|a-b|
```

```text
complete groups
      ↓
floor
```

```text
containers required
      ↓
ceil
```

Most importantly:

```text
ENGLISH
   ↓
VARIABLES
   ↓
RELATIONSHIPS
   ↓
MATH
```

Do not rush to implementation until this translation becomes clear.

---

# Next Chapter

```text
0. FOUNDATION
      ↓
1. STORY → MATHEMATICS
```

In the next chapter, these individual tools are combined to translate longer Codeforces-style statements into compact mathematical models.
