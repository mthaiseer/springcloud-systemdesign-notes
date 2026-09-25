# CP Mathematical Modeling Mini-Course

## 0. Foundation

> **Goal:** Build the minimum algebra toolkit needed to translate Codeforces statements into mathematics quickly.
>
> This chapter avoids advanced notation and uses **plain-text formulas + ASCII diagrams** so it renders correctly in any Markdown viewer.

---

# Foundation Tree

```text
0. FOUNDATION
│
├── 0.1 Variables & Expressions
├── 0.2 Basic Equations
├── 0.3 Rearranging Equations
├── 0.4 Inequalities
├── 0.5 Min / Max
├── 0.6 Absolute Value
└── 0.7 Floor / Ceil
```

The main pipeline:

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

A variable is a short name for a quantity.

Example:

```text
number of students      -> n
number of operations    -> k
number of red balls     -> R
number of blue balls    -> B
current position        -> x
target position         -> y
```

Think:

```text
REAL QUANTITY
     ↓
VARIABLE
```

If the problem says:

> Alice has some apples.

Write:

```text
A = number of apples Alice has
```

---

## 0.1.2 What is an expression?

An expression describes a quantity.

Examples:

```text
x + 3
2 * x
n - k
3 * a + 2 * b
```

An equation says two expressions are equal.

Examples:

```text
x + 3 = 10
a + b = S
2 * x = n
```

Visual:

```text
Expression:
x + 5

Equation:
x + 5 = 20
      ^
      equal
```

---

## 0.1.3 Common English -> expression

```text
"3 more than x"      -> x + 3
"3 less than x"      -> x - 3
"twice x"            -> 2 * x
"three times x"      -> 3 * x
"k groups of x"      -> k * x
"remaining"          -> total - used
```

### Example

> There are `n` boxes. Each box contains `k` balls.

```text
number of boxes = n
balls per box   = k

total balls = n * k
```

---

## 0.1.4 Mini drills

### Q1

There are `n` rows, each containing `m` chairs.

```text
Answer: n * m
```

### Q2

Bob has `x` chocolates. Alice has 7 fewer.

```text
Answer: x - 7
```

### Q3

A machine produces `k` items each minute for `t` minutes.

```text
Answer: k * t
```

---

# 0.2 Basic Equations

## 0.2.1 Equation means "this relationship must be true"

Problem:

> Alice and Bob together have 20 coins.

Define:

```text
A = Alice's coins
B = Bob's coins
```

Then:

```text
A + B = 20
```

Visual:

```text
Alice      Bob
  A    +    B
   \       /
    \     /
     TOTAL
       20
```

---

## 0.2.2 Common English -> equation

```text
"total is S"
x + y = S

"difference is D"
x - y = D

"x is twice y"
x = 2 * y

"x is 5 greater than y"
x = y + 5

"x and y are equal"
x = y
```

---

## 0.2.3 Combine multiple statements

Problem:

> Alice has 4 more coins than Bob.
> Together they have 20.

Write:

```text
A = B + 4
A + B = 20
```

Substitute:

```text
A + B = 20

(B + 4) + B = 20

2 * B + 4 = 20

2 * B = 16

B = 8

A = 12
```

Main CP lesson:

```text
Statement 1 -> equation
Statement 2 -> equation
                ↓
             combine
```

---

# 0.3 Rearranging Equations

This is one of the most important CP modeling skills.

The question is:

> If I know all but one quantity, can I calculate the missing one directly?

---

## 0.3.1 Two-variable example

Given:

```text
x + y = S
```

Solve for `y`:

```text
x + y = S

remove x from both sides

y = S - x
```

So:

```text
y = S - x
```

---

## 0.3.2 Three-variable example

Given:

```text
x + y + z = S
```

Solve for `z`:

```text
z = S - x - y
```

---

## 0.3.3 Why this matters in CP

Naive brute force:

```text
for every x
    for every y
        for every z
            check x + y + z == S
```

Complexity:

```text
O(n^3)
```

But:

```text
x + y + z = S

=> z = S - x - y
```

So:

```text
for every x
    for every y
        z = S - x - y
        check whether z is valid
```

Complexity:

```text
O(n^2)
```

Visual:

```text
x + y + z = S
      │
      └── solve for z
              ↓
        z = S - x - y
              ↓
      no loop over z
              ↓
         O(n^3) -> O(n^2)
```

This is one of the clearest examples of:

```text
Math modeling -> faster algorithm
```

---

## 0.3.4 More rearranging

Given:

```text
3 * k = n
```

Then:

```text
k = n / 3
```

Given:

```text
2 * x + 5 = y
```

Then:

```text
2 * x = y - 5

x = (y - 5) / 2
```

---

# 0.4 Inequalities

Equations mean:

```text
exact value
```

Inequalities mean:

```text
allowed range / limit
```

---

## 0.4.1 Core symbols

```text
x < 5    -> x is less than 5
x <= 5   -> x is at most 5
x > 5    -> x is greater than 5
x >= 5   -> x is at least 5
```

Very common translations:

```text
"at least k"       -> x >= k
"at most k"        -> x <= k
"no more than k"   -> x <= k
"cannot exceed k"  -> x <= k
"more than k"      -> x > k
"less than k"      -> x < k
```

---

## 0.4.2 Number-line intuition

For:

```text
x <= 5
```

```text
<====================●-------------------->
                     5

5 is allowed.
Everything smaller is allowed.
```

For:

```text
x > 5
```

```text
<--------------------○====================>
                     5

5 is NOT allowed.
Everything larger is allowed.
```

---

## 0.4.3 Resource example

Problem:

> Each operation consumes 3 coins.
> You have `n` coins.
> How many operations can you perform?

Define:

```text
k = number of operations
```

For `k` operations:

```text
coins needed = 3 * k
```

You cannot use more than `n` coins:

```text
3 * k <= n
```

Therefore:

```text
k <= n / 3
```

Because `k` is an integer:

```text
k <= floor(n / 3)
```

In C++ for positive integers:

```cpp
k <= n / 3;
```

---

# 0.5 Min / Max

This is very important when multiple conditions limit the answer.

---

## 0.5.1 Minimum

```text
min(a, b)
```

means:

```text
the smaller value
```

Example:

```text
min(4, 7) = 4
```

---

## 0.5.2 Maximum

```text
max(a, b)
```

means:

```text
the larger value
```

Example:

```text
max(4, 7) = 7
```

---

## 0.5.3 Limiting-resource idea

Problem:

> Each team needs:
> - 2 red balls
> - 3 blue balls
>
> Available:
> - `R` red balls
> - `B` blue balls
>
> Find the maximum number of teams.

Define:

```text
k = number of teams
```

### Red-ball condition

Each team needs 2 red balls.

For `k` teams:

```text
red needed = 2 * k
```

We only have `R`:

```text
2 * k <= R
```

Therefore:

```text
k <= floor(R / 2)
```

### Blue-ball condition

Each team needs 3 blue balls.

For `k` teams:

```text
blue needed = 3 * k
```

We only have `B`:

```text
3 * k <= B
```

Therefore:

```text
k <= floor(B / 3)
```

### Combine both limits

`k` must satisfy both:

```text
k <= floor(R / 2)

and

k <= floor(B / 3)
```

The maximum valid `k` is the smaller upper bound:

```text
answer = min(
    floor(R / 2),
    floor(B / 3)
)
```

For positive integers in C++:

```cpp
int answer = min(R / 2, B / 3);
```

Visual:

```text
red allows at most 8 teams
blue allows at most 5 teams

Need BOTH resources.

8 -----------┐
             ├── smaller limit wins
5 -----------┘

answer = 5
```

Core pattern:

```text
many upper bounds
      ↓
take minimum
```

And:

```text
many lower bounds
      ↓
take maximum
```

---

## 0.5.4 Lower-bound example

Suppose:

```text
x >= 4
x >= 7
```

To satisfy both:

```text
x >= max(4, 7)
```

So:

```text
x >= 7
```

---

# 0.6 Absolute Value

Absolute value is best understood in CP as:

```text
distance without direction
```

---

## 0.6.1 Basic idea

```text
abs(5)  = 5
abs(-5) = 5
```

The sign is ignored because we only care about size.

---

## 0.6.2 Distance between two positions

Suppose:

```text
a = 3
b = 10
```

Distance:

```text
abs(a - b)
```

So:

```text
abs(3 - 10) = 7
```

If reversed:

```text
abs(10 - 3) = 7
```

Same distance.

Visual:

```text
0---1---2---3---4---5---6---7---8---9---10
            A                           B
            |<--------- 7 ----------->|

distance = abs(3 - 10) = 7
```

---

## 0.6.3 Common CP translations

```text
"difference between a and b"
        -> abs(a - b)

"distance between x and y"
        -> abs(x - y)

"within distance k"
        -> abs(x - y) <= k

"more than k apart"
        -> abs(x - y) > k
```

---

## 0.6.4 Movement example

You are at `x`.
Target is `y`.

Each move changes your position by exactly 1.

Minimum moves:

```text
abs(x - y)
```

This avoids writing two separate cases:

```text
if x < y:
    y - x

if x > y:
    x - y
```

Both become:

```text
abs(x - y)
```

---

# 0.7 Floor / Ceil

Floor and ceil appear constantly because CP answers are often integers.

---

## 0.7.1 Floor

Floor means:

```text
round DOWN
```

Examples:

```text
floor(3.9) = 3
floor(5.0) = 5
```

---

## 0.7.2 Complete groups -> floor

Problem:

> You have 17 candies.
> Each complete box needs 5 candies.
> How many complete boxes can you make?

Visual:

```text
17 candies

[*****]  box 1
[*****]  box 2
[*****]  box 3
[**   ]  incomplete

complete boxes = 3
```

Formula:

```text
floor(17 / 5) = 3
```

General:

```text
complete groups = floor(n / k)
```

For positive integers in C++:

```cpp
int groups = n / k;
```

---

## 0.7.3 Ceil

Ceil means:

```text
round UP
```

Examples:

```text
ceil(3.1) = 4
ceil(5.0) = 5
```

---

## 0.7.4 Minimum containers needed -> ceil

Problem:

> 17 students.
> One bus can hold at most 5 students.
> What is the minimum number of buses?

Visual:

```text
Bus 1: 5
Bus 2: 5
Bus 3: 5
Bus 4: 2

total buses = 4
```

Formula:

```text
ceil(17 / 5) = 4
```

General:

```text
minimum groups needed = ceil(n / k)
```

For positive integers in C++:

```cpp
int buses = (n + k - 1) / k;
```

---

## 0.7.5 Floor vs ceil intuition

Ask:

```text
How many COMPLETE groups fit?
              ↓
            FLOOR
```

Ask:

```text
How many groups are NEEDED
to contain everything?
              ↓
             CEIL
```

Visual:

```text
n items
│
├── complete groups that fit?
│       ↓
│   floor(n / k)
│
└── groups required?
        ↓
    ceil(n / k)
```

---

# 0.8 Foundation Integration Example

Problem:

> There are `R` red balls and `B` blue balls.
> Each team requires 2 red balls and 3 blue balls.
> Find the maximum possible number of teams.

---

## Step 1 — Define the answer

```text
k = number of teams
```

---

## Step 2 — Model red-ball usage

Each team requires:

```text
2 red balls
```

So `k` teams require:

```text
2 * k red balls
```

Available:

```text
R red balls
```

Therefore:

```text
2 * k <= R
```

So:

```text
k <= floor(R / 2)
```

---

## Step 3 — Model blue-ball usage

Each team requires:

```text
3 blue balls
```

So `k` teams require:

```text
3 * k blue balls
```

Available:

```text
B blue balls
```

Therefore:

```text
3 * k <= B
```

So:

```text
k <= floor(B / 3)
```

---

## Step 4 — Combine limits

`k` must satisfy BOTH:

```text
k <= floor(R / 2)

k <= floor(B / 3)
```

So the answer is the smaller upper bound:

```text
answer = min(
    floor(R / 2),
    floor(B / 3)
)
```

For positive integers in C++:

```cpp
int answer = min(R / 2, B / 3);
```

Complete reasoning:

```text
Story
  ↓
k = number of teams
  ↓
2*k <= R
3*k <= B
  ↓
k <= floor(R/2)
k <= floor(B/3)
  ↓
both must hold
  ↓
smaller upper bound wins
  ↓
answer = min(R/2, B/3)
```

This is mathematical modeling.

---

# 0.9 Foundation Translation Cheat Sheet

```text
WORDS                          MATH / MODEL

total                          +
remaining                      total - used
together                       +
difference                     subtraction / abs(a-b)
x more than y                  y + x
x less than y                  y - x
twice x                        2 * x
k groups of x                  k * x

exactly                        =
at least                       >=
at most                        <=
more than                      >
less than                      <

distance                       abs(a-b)
within k                       abs(a-b) <= k

complete groups                floor(n / k)
minimum groups needed          ceil(n / k)

many upper bounds              min(...)
many lower bounds              max(...)
```

---

# 0.10 Foundation Problem-Solving Engine

Before coding:

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
Write expression / equation / inequality
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

# 0.11 Mini Practice Set

Do these without coding first.

---

## Problem 1 — Complete groups

You have `n` candies.

Each child must receive exactly `k` candies.

Question:

```text
What is the maximum number of children
who can receive a complete share?
```

Think:

```text
complete groups
```

Answer:

```text
floor(n / k)
```

For positive integers in C++:

```cpp
n / k
```

---

## Problem 2 — Minimum containers

There are `n` students.

Each bus can carry at most `k` students.

Question:

```text
What is the minimum number of buses?
```

Think:

```text
containers needed
```

Answer:

```text
ceil(n / k)
```

For positive integers in C++:

```cpp
(n + k - 1) / k
```

---

## Problem 3 — Eliminate a variable

Given:

```text
x + y = S
```

If `x` is known:

```text
y = S - x
```

---

## Problem 4 — Distance

You are at coordinate `a`.

Destination is `b`.

Each move changes position by exactly 1.

Minimum moves:

```text
abs(a - b)
```

---

## Problem 5 — Two resources

Each machine requires:

```text
3 units of A
2 units of B
```

Available:

```text
A units of resource A
B units of resource B
```

Define:

```text
k = number of machines
```

Resource conditions:

```text
3 * k <= A
2 * k <= B
```

Therefore:

```text
k <= floor(A / 3)
k <= floor(B / 2)
```

Maximum valid `k`:

```text
answer = min(
    floor(A / 3),
    floor(B / 2)
)
```

For positive integers in C++:

```cpp
int answer = min(A / 3, B / 2);
```

---

# 0.12 Foundation Mastery Test

You are ready for the next chapter when these transformations feel natural.

### Sum

```text
x + y = S
      ↓
y = S - x
```

### Resource bound

```text
3 * k <= n
      ↓
k <= floor(n / 3)
```

### Multiple upper bounds

```text
k <= A
k <= B
      ↓
k <= min(A, B)
```

### Multiple lower bounds

```text
k >= A
k >= B
      ↓
k >= max(A, B)
```

### Distance

```text
distance between a and b
          ↓
      abs(a - b)
```

### Complete groups

```text
complete groups
      ↓
floor(n / k)
```

### Containers required

```text
containers needed
      ↓
ceil(n / k)
```

Most important:

```text
ENGLISH
   ↓
VARIABLES
   ↓
RELATIONSHIPS
   ↓
MATH
```

---

# 0.13 What NOT to do

Do not immediately jump to code.

Bad flow:

```text
Read problem
   ↓
guess algorithm
   ↓
start coding
   ↓
get confused
```

Better flow:

```text
Read problem
   ↓
define variables
   ↓
write relationships
   ↓
simplify
   ↓
find observation
   ↓
algorithm
   ↓
code
```

---

# 0.14 Foundation Summary

The foundation has only seven main tools:

```text
1. Variables
2. Equations
3. Rearranging
4. Inequalities
5. Min / Max
6. Absolute Difference
7. Floor / Ceil
```

But these seven tools appear repeatedly in Codeforces mathematical modeling.

The goal is not to memorize formulas.

The goal is to make this automatic:

```text
English
  ↓
Math
  ↓
Observation
  ↓
Algorithm
```

---

# Next Chapter

```text
0. FOUNDATION
      ↓
1. STORY -> MATHEMATICS
```

The next chapter combines these tools to translate longer Codeforces-style statements into compact mathematical models.
