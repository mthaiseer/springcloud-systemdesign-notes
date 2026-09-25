# CP Mathematical Modeling Mini-Course

## 4. Algebra -> Smaller Search

> **Goal:** Use algebra to remove unnecessary loops and turn brute force into a smaller search.
>
> The key question is:
>
> **"If I choose some variables, can I calculate the remaining variable instead of searching for it?"**

---

# Chapter Tree

```text
4. ALGEBRA -> SMALLER SEARCH
│
├── 4.1 Start from brute force
├── 4.2 Find the mathematical relationship
├── 4.3 Solve for one variable
├── 4.4 Eliminate one loop
├── 4.5 Substitution
├── 4.6 Reduce 3 variables -> 2
├── 4.7 Reduce 2 variables -> 1
├── 4.8 Pair modeling
├── 4.9 Complement thinking
├── 4.10 Bounds shrink the search
├── 4.11 Count instead of enumerate
├── 4.12 Formula instead of loop
├── 4.13 Constraints guide reduction
└── 4.14 Complete CF-style modeling
```

Central transformation:

```text
BRUTE FORCE
    ↓
What relationship must hold?
    ↓
EQUATION
    ↓
Solve for one unknown
    ↓
CALCULATE instead of SEARCH
    ↓
remove a loop
    ↓
SMALLER COMPLEXITY
```

---

# 4.1 Start From Brute Force

Do not be afraid to first imagine the simplest brute force.

Brute force helps reveal:

```text
What exactly am I searching for?
```

Example:

> Find non-negative integers `x`, `y`, `z` such that:

```text
x + y + z = S
```

Suppose:

```text
0 <= x,y,z <= K
```

Naive search:

```text
for x = 0..K
    for y = 0..K
        for z = 0..K
            if x+y+z == S
                valid
```

Complexity:

```text
O(K^3)
```

Before optimizing, write what the innermost loop is trying to discover:

```text
Given x and y,
what z makes the equation true?
```

That question exposes the algebra.

---

# 4.2 Find the Mathematical Relationship

The relationship is:

```text
x + y + z = S
```

Once `x` and `y` are fixed, `z` is NOT free anymore.

It must be:

```text
z = S - x - y
```

Visual:

```text
x + y + z = S
      │
      │ choose x,y
      ▼
? + known values = S
      │
      ▼
z = S - x - y
```

So searching all possible `z` is unnecessary.

---

# 4.3 Solve for One Variable

General habit:

```text
equation containing several variables
              ↓
choose which variables to search
              ↓
solve equation for the remaining one
```

Examples:

```text
x + y = S

=> y = S - x
```

```text
x + y + z = S

=> z = S - x - y
```

```text
2*x + y = S

=> y = S - 2*x
```

```text
3*x + 2*y = S

=> 2*y = S - 3*x

=> y = (S - 3*x) / 2
```

But remember:

```text
y must satisfy all original requirements
```

For example:

```text
y must be integer
y >= 0
y <= K
```

---

# 4.4 Eliminate One Loop

Return to:

```text
x + y + z = S
```

Naive:

```cpp
for (int x = 0; x <= K; x++) {
    for (int y = 0; y <= K; y++) {
        for (int z = 0; z <= K; z++) {
            if (x + y + z == S) {
                // valid
            }
        }
    }
}
```

Algebra:

```text
z = S - x - y
```

Now:

```cpp
for (int x = 0; x <= K; x++) {
    for (int y = 0; y <= K; y++) {
        int z = S - x - y;

        if (0 <= z && z <= K) {
            // valid
        }
    }
}
```

Complexity:

```text
BEFORE:
O(K^3)

AFTER:
O(K^2)
```

The important reasoning:

```text
z is determined by x and y
          ↓
z is not an independent choice
          ↓
do not loop over z
```

---

# 4.5 Independent vs Dependent Variables

This idea is fundamental.

Suppose:

```text
x + y + z = S
```

At first it looks like there are 3 variables.

But only 2 can be chosen freely.

Once:

```text
x
y
```

are selected:

```text
z
```

is forced.

So:

```text
x, y = independent choices
z    = dependent value
```

Visual:

```text
choose x ───┐
            ├──> z = S-x-y
choose y ───┘
```

Ask in every brute-force problem:

> Are all my loop variables really independent?

---

# 4.6 Substitution

Substitution means replacing a variable using another equation.

Example:

```text
x + y = S
x - y = D
```

From the first:

```text
y = S - x
```

Put this into the second:

```text
x - (S - x) = D
```

Simplify:

```text
x - S + x = D

2*x - S = D

2*x = S + D

x = (S + D) / 2
```

Then:

```text
y = S - x
```

A problem that appeared to require searching `x` and `y` may require no search at all.

Transformation:

```text
2 equations
    ↓
substitution
    ↓
1 variable
    ↓
direct formula
```

---

# 4.7 Reduce 3 Variables -> 2

Pattern:

```text
x + y + z = S
```

Solve:

```text
z = S - x - y
```

Then:

```text
search x
search y
calculate z
```

Complexity reduction:

```text
O(n^3)
   ↓
O(n^2)
```

This pattern appears frequently in beginner/intermediate CF problems.

---

# 4.8 Reduce 2 Variables -> 1

Pattern:

```text
x + y = S
```

Solve:

```text
y = S - x
```

Then:

```text
search x
calculate y
```

Instead of:

```text
search x
search y
```

Complexity:

```text
O(n^2)
   ↓
O(n)
```

Example:

> Count pairs `(x,y)` with:

```text
0 <= x <= K
0 <= y <= K
x + y = S
```

Algorithm:

```text
for each x:
    y = S - x

    if 0 <= y <= K:
        count
```

---

# 4.9 Pair Modeling

Many problems ask about pairs:

```text
(a[i], a[j])
```

Typical condition:

```text
a[i] + a[j] = S
```

If one value is:

```text
x
```

the required partner is:

```text
S - x
```

This is called **complement thinking**.

Visual:

```text
x + ? = S
    │
    ▼
? = S - x
```

Instead of asking:

```text
Which second value should I try?
```

ask:

```text
Which exact second value do I need?
```

This idea later connects to:

```text
hash sets
frequency arrays
sorting + two pointers
```

The mathematical model comes first:

```text
partner = target - current
```

---

# 4.10 Complement Thinking

Complement means:

```text
what is missing to reach the target?
```

Examples:

### Sum

```text
x + y = S

missing y = S - x
```

### Required capacity

```text
used + remaining = total

remaining = total - used
```

### Required score

```text
current + needed = target

needed = target - current
```

### Triple

```text
x + y + z = S

z = S - (x+y)
```

Mental trigger:

```text
TARGET TOTAL
     -
WHAT I ALREADY HAVE
     =
WHAT I STILL NEED
```

This is one of the most reusable modeling ideas in CP.

---

# 4.11 Bounds Shrink the Search

Algebra may give a variable, but inequalities can shrink the range even further.

Example:

```text
x + y = S

0 <= x <= K
0 <= y <= K
```

Since:

```text
y = S - x
```

and:

```text
0 <= y <= K
```

we get:

```text
0 <= S - x <= K
```

Break it into two conditions.

From:

```text
0 <= S - x
```

we get:

```text
x <= S
```

From:

```text
S - x <= K
```

we get:

```text
x >= S - K
```

Combine with:

```text
0 <= x <= K
```

So:

```text
max(0, S-K) <= x <= min(K, S)
```

Instead of searching every possible integer, we now know the exact valid range.

Visual:

```text
original x range:

0 ----------------------------- K

equation + y bounds remove invalid parts:

       L ================= R

L = max(0, S-K)
R = min(K, S)
```

---

# 4.12 Count Without Enumerating

Now consider the same pair problem:

```text
x + y = S

0 <= x,y <= K
```

We derived:

```text
L = max(0, S-K)
R = min(K, S)
```

Every integer `x` in:

```text
L <= x <= R
```

produces exactly one valid:

```text
y = S-x
```

How many integer `x` values exist?

```text
R - L + 1
```

if:

```text
L <= R
```

Otherwise:

```text
0
```

So an `O(K)` loop can become `O(1)`.

Complete reduction:

```text
search x and y
    O(K^2)
       ↓
y = S-x
       ↓
search x
    O(K)
       ↓
derive exact x range
       ↓
count range length
       ↓
    O(1)
```

This is a powerful example of progressively improving a model.

---

# 4.13 Range Counting Formula

If integers satisfy:

```text
L <= x <= R
```

then number of integers is:

```text
R - L + 1
```

provided:

```text
L <= R
```

Example:

```text
3 <= x <= 7
```

Values:

```text
3,4,5,6,7
```

Count:

```text
7 - 3 + 1 = 5
```

This tiny formula appears everywhere.

---

# 4.14 Divisibility After Elimination

Suppose:

```text
3*x + 2*y = S
```

Solve for `y`:

```text
y = (S - 3*x) / 2
```

But `y` must be an integer.

Therefore:

```text
S - 3*x
```

must be divisible by 2:

```text
(S - 3*x) % 2 == 0
```

Also perhaps:

```text
y >= 0
```

which means:

```text
S - 3*x >= 0
```

So after eliminating a variable, always validate:

```text
1. integer?
2. within lower bound?
3. within upper bound?
4. any extra condition?
```

---

# 4.15 Formula Instead of Repeated Loop

Example:

> Sum:

```text
1 + 2 + 3 + ... + n
```

A direct loop is:

```text
O(n)
```

But formula:

```text
n * (n + 1) / 2
```

is:

```text
O(1)
```

The modeling lesson is broader than this specific formula:

```text
repeated regular structure
        ↓
look for closed form
```

Other common regular structures:

```text
constant arithmetic increase
complete groups
number of integers in a range
sum of consecutive values
repeated identical contribution
```

You do not need to memorize a huge formula sheet.

Instead ask:

> Is this loop doing mathematically repetitive work?

---

# 4.16 Arithmetic Sequence Intuition

Suppose values are:

```text
a
a+d
a+2d
a+3d
...
```

The `i`-th value can be modeled as:

```text
a + i*d
```

This replaces repeated simulation.

Example:

```text
start = 5
add 3 every step
```

Sequence:

```text
5, 8, 11, 14, 17, ...
```

At step `k`:

```text
value = 5 + 3*k
```

This connects directly to Operation Modeling.

---

# 4.17 Constraints Tell You How Much Reduction You Need

Suppose:

```text
n <= 100
```

Then:

```text
O(n^2) = 10,000
```

may be fine.

Suppose:

```text
n <= 100,000
```

Then:

```text
O(n^2)
```

is usually too large.

So constraints help answer:

```text
How many loops can I afford?
```

Rough CP intuition:

```text
n <= 20
    exponential may sometimes work

n <= 1,000
    O(n^2) may work depending on limits

n <= 100,000
    usually O(n log n) or O(n)

n <= 1,000,000
    usually O(n) or near O(n)
```

These are rough guidelines, not laws.

The modeling workflow:

```text
derive simple brute force
        ↓
estimate complexity
        ↓
compare with constraints
        ↓
too slow?
        ↓
find dependent variable / formula / structure
```

---

# 4.18 Complete CF-Style Example 1 — Triple Sum

Problem:

> Count triples `(x,y,z)` satisfying:

```text
0 <= x,y,z <= K

x + y + z = S
```

Naive:

```text
loop x
loop y
loop z
```

Complexity:

```text
O(K^3)
```

Model:

```text
z = S - x - y
```

Now:

```text
loop x
loop y

z = S-x-y

if 0 <= z <= K:
    count
```

Complexity:

```text
O(K^2)
```

Key observation:

```text
Once x and y are fixed,
there is only ONE possible z.
```

---

# 4.19 Complete CF-Style Example 2 — Pair Sum Range

Problem:

> Count ordered pairs `(x,y)` satisfying:

```text
0 <= x,y <= K
x + y = S
```

Start:

```text
y = S-x
```

Need:

```text
0 <= x <= K
0 <= S-x <= K
```

From the second range:

```text
S-K <= x <= S
```

Combine:

```text
max(0, S-K) <= x <= min(K, S)
```

Define:

```text
L = max(0, S-K)
R = min(K, S)
```

Answer:

```text
if L > R:
    0
else:
    R-L+1
```

Complexity:

```text
O(1)
```

Progression:

```text
O(K^2)
   ↓ eliminate y
O(K)
   ↓ derive bounds
O(1)
```

---

# 4.20 Complete CF-Style Example 3 — Fixed Difference

Problem:

> Count pairs `(x,y)` such that:

```text
1 <= x,y <= n
x - y = D
```

Solve:

```text
x = y + D
```

Now only `y` is free.

Need:

```text
1 <= y <= n
```

and:

```text
1 <= y+D <= n
```

These bounds can be combined to find the valid interval for `y`.

Again:

```text
2-variable search
       ↓
one equation
       ↓
1 free variable
       ↓
range intersection
       ↓
direct count
```

---

# 4.21 Complete CF-Style Example 4 — Resource Equation

Problem:

> You have exactly `S` coins.
>
> An item of type A costs 3.
> An item of type B costs 5.
>
> Find whether there are non-negative counts `x,y` using exactly all coins.

Model:

```text
3*x + 5*y = S
```

Instead of trying every pair:

```text
for x
    for y
```

choose `x`:

```text
5*y = S - 3*x
```

So:

```text
y = (S - 3*x) / 5
```

Check:

```text
S - 3*x >= 0

(S - 3*x) % 5 == 0
```

Now:

```text
O(S^2)
   ↓
O(S)
```

The important idea is not this particular coin problem.

It is:

```text
equation makes one variable dependent
```

---

# 4.22 Search Dimension

Think of every independent loop as one search dimension.

Example:

```text
for x
    for y
        for z
```

has three dimensions.

If:

```text
z = f(x,y)
```

then `z` is dependent.

So only two dimensions remain.

Visual:

```text
3D SEARCH

x × y × z

    ↓ equation determines z

2D SEARCH

x × y
```

Similarly:

```text
x × y
```

can become:

```text
x
```

if:

```text
y = f(x)
```

This is a useful mental model for complexity.

---

# 4.23 General Variable-Elimination Engine

Suppose you have:

```text
variables:
a, b, c, d
```

Ask:

```text
Which variables can I choose freely?
```

Then:

```text
Which variables are forced by equations?
```

Example:

```text
a + b + c + d = S
```

If you choose:

```text
a,b,c
```

then:

```text
d = S-a-b-c
```

So four apparent variables contain only three independent choices.

General idea:

```text
N variables
      +
equations connecting them
      ↓
fewer independent variables
      ↓
smaller search
```

---

# 4.24 Algebra + Data Structures

Algebra often tells you **what value you need**.

A data structure tells you **how to find it quickly**.

Pair sum:

```text
x + y = S
```

Algebra:

```text
y = S-x
```

Now the algorithmic question is:

```text
Can I quickly check whether S-x exists?
```

Possible tools later:

```text
hash set
frequency map
sorted array + binary search
two pointers
```

Separation of responsibilities:

```text
MATH:
What do I need?

y = S-x

ALGORITHM/DATA STRUCTURE:
How do I find/check it quickly?
```

This is why mathematical modeling improves DSA problem solving too.

---

# 4.25 Common Mistakes

## Mistake 1 — Looping over a forced variable

If:

```text
x + y + z = S
```

and `x,y` are fixed:

```text
z is forced
```

Do not search it.

---

## Mistake 2 — Forgetting validity after calculating

Example:

```text
z = S-x-y
```

You still need:

```text
0 <= z <= K
```

Calculated does not mean valid.

---

## Mistake 3 — Integer division hiding impossibility

Given:

```text
2*y = 7
```

C++:

```cpp
int y = 7 / 2;
```

gives:

```text
3
```

But:

```text
2*3 != 7
```

The equation has no integer solution.

Check divisibility before using integer division:

```cpp
if (value % 2 == 0) {
    int y = value / 2;
}
```

---

## Mistake 4 — Ignoring bounds

Derived:

```text
y = S-x
```

but original problem says:

```text
0 <= y <= K
```

Always preserve original constraints.

---

## Mistake 5 — Optimizing before understanding brute force

A useful order is:

```text
correct brute force idea
       ↓
identify repeated/unnecessary work
       ↓
use math to remove it
```

Do not chase `O(1)` before you understand what is being counted.

---

# 4.26 Translation Drills

Do not code.

---

## Drill 1

Given:

```text
a + b = S
```

If `a` is chosen:

```text
b = S-a
```

---

## Drill 2

Given:

```text
a + b + c = S
```

If `a,b` are chosen:

```text
c = S-a-b
```

---

## Drill 3

Given:

```text
2*x + 3*y = S
```

If `x` is chosen:

```text
3*y = S-2*x

y = (S-2*x)/3
```

Validity:

```text
S-2*x >= 0
(S-2*x) % 3 == 0
```

---

## Drill 4

Given:

```text
x + y = 20
0 <= x,y <= 15
```

For `x`:

```text
y = 20-x
```

Need:

```text
0 <= 20-x <= 15
```

Therefore:

```text
5 <= x <= 20
```

Combine with:

```text
0 <= x <= 15
```

Final:

```text
5 <= x <= 15
```

Number of valid ordered pairs:

```text
15-5+1 = 11
```

---

# 4.27 Practice Set

For every problem, write:

```text
BRUTE FORCE:
EQUATION:
DEPENDENT VARIABLE:
VALIDITY CHECK:
NEW COMPLEXITY:
```

---

## Practice A

Count:

```text
0 <= x,y <= n

x+y = S
```

Reduce `O(n^2)` to `O(n)` first.

---

## Practice B

Count:

```text
0 <= x,y,z <= n

x+y+z = S
```

Reduce `O(n^3)` to `O(n^2)`.

---

## Practice C

Find whether non-negative integers satisfy:

```text
4*x + 7*y = S
```

Reduce a two-variable brute force to one loop.

---

## Practice D

Given:

```text
x+y = S
x-y = D
```

Remove the search completely.

---

## Practice E

Count integers:

```text
L <= x <= R
```

without looping.

---

# 4.28 Practice Answers

## A

```text
y = S-x
```

Loop only `x`.

Check:

```text
0 <= y <= n
```

Complexity:

```text
O(n)
```

---

## B

```text
z = S-x-y
```

Loop:

```text
x
y
```

Check:

```text
0 <= z <= n
```

Complexity:

```text
O(n^2)
```

---

## C

From:

```text
4*x + 7*y = S
```

derive:

```text
y = (S-4*x)/7
```

Loop over valid `x`.

Check:

```text
S-4*x >= 0
(S-4*x) % 7 == 0
```

Then `y` is determined.

---

## D

Given:

```text
x+y = S
x-y = D
```

Add equations:

```text
2*x = S+D
```

So:

```text
x = (S+D)/2
```

Then:

```text
y = (S-D)/2
```

For integer solutions, required numerators must produce integers.

No brute force is necessary.

---

## E

If:

```text
L <= R
```

count:

```text
R-L+1
```

Otherwise:

```text
0
```

---

# 4.29 Chapter Mastery Test

You are ready for Chapter 5 when you see:

```text
x+y+z = S
```

and immediately think:

```text
If I know x,y,
z = S-x-y.

Why would I loop over z?
```

You should see:

```text
x+y = S
```

and think:

```text
partner/complement = S-x
```

You should see:

```text
L <= x <= R
```

and think:

```text
number of integer values = R-L+1
```

You should see:

```text
3*x + 2*y = S
```

and think:

```text
choose x
calculate y
check divisibility + bounds
```

Most importantly:

```text
Are all my loop variables
actually independent?
```

---

# 4.30 Final Mental Engine

```text
              SIMPLE BRUTE FORCE
                      │
                      ▼
             WRITE THE EQUATION
                      │
                      ▼
          WHICH VARIABLES ARE FREE?
                      │
                      ▼
       WHICH VARIABLE CAN I CALCULATE?
                      │
                      ▼
              REARRANGE / SUBSTITUTE
                      │
                      ▼
              REMOVE ONE LOOP
                      │
                      ▼
          CHECK INTEGER + BOUNDS
                      │
                      ▼
         CAN BOUNDS GIVE A RANGE?
                      │
                      ▼
       CAN I COUNT RANGE DIRECTLY?
                      │
                      ▼
                FINAL ALGORITHM
```

Complexity ladder:

```text
O(n^3)
   │
   │ eliminate one variable
   ▼
O(n^2)
   │
   │ eliminate another / fast lookup
   ▼
O(n)
   │
   │ derive exact range/formula
   ▼
O(1)
```

Not every problem reaches `O(1)`.

The goal is:

```text
Remove search that mathematics
already determines for you.
```

---

# Next Chapter

```text
4. ALGEBRA -> SMALLER SEARCH
              ↓
5. RESOURCE & OPTIMIZATION MODELING
```

Chapter 5 will focus on problems asking:

```text
maximum?
minimum?
how many operations/items?
how much resource is needed?
what is the limiting resource?
```

The main pattern will be:

```text
Suppose the answer is k
        ↓
What resources does k require?
        ↓
write inequalities
        ↓
derive bounds on k
        ↓
identify limiting condition
        ↓
min / max answer
```
