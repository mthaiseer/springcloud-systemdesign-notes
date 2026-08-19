# CP Mathematical Modeling Mini-Course

## 1. Story -> Mathematics

> **Goal:** Learn to strip away Codeforces story text and convert it into a small mathematical model.
>
> This chapter uses plain Markdown, ASCII diagrams, and plain-text formulas so it renders correctly in any Markdown viewer.

---

# Chapter Tree

```text
1. STORY -> MATHEMATICS
│
├── 1.1 Identify the real quantities
├── 1.2 Give quantities variables
├── 1.3 Separate given / changing / required
├── 1.4 Translate relationships
│   ├── Total
│   ├── Difference
│   ├── More / Less
│   ├── At least / At most
│   └── Ratio / Proportion
├── 1.5 Build equations
├── 1.6 Build inequalities and bounds
├── 1.7 Remove irrelevant story
├── 1.8 Translate the target
├── 1.9 Combine multiple conditions
└── 1.10 Complete CF-style translation
```

The central skill:

```text
LONG STORY
    ↓
Who/what actually matters?
    ↓
QUANTITIES
    ↓
VARIABLES
    ↓
RELATIONSHIPS
    ↓
MATH MODEL
    ↓
much smaller problem
```

---

# 1.1 Identify the Real Quantities

A Codeforces statement may contain:

```text
people
games
coins
monsters
cities
days
boxes
operations
```

Do not focus on the nouns.

Ask:

> What NUMBERS describe the situation?

Example:

> Alice has 8 apples. Bob has 3 more apples than Alice.

Story objects:

```text
Alice
Bob
apples
```

Real quantities:

```text
Alice's apple count
Bob's apple count
difference = 3
```

The mathematical problem does not care that they are apples.

It could equally be:

```text
A = 8
B = A + 3
```

So:

```text
STORY OBJECTS
     ↓
NUMERICAL QUANTITIES
```

---

# 1.2 Give Quantities Variables

Once quantities are identified, name them.

Example:

> There are some red balls and blue balls.

Write:

```text
R = number of red balls
B = number of blue balls
```

If the problem asks about operations:

```text
k = number of operations
```

If it asks for a maximum number of teams:

```text
k = number of teams
```

A useful habit:

```text
"What is the answer measuring?"
            ↓
give THAT a variable
```

Example:

> Find the maximum number of days the food can last.

Start with:

```text
d = number of days
```

This becomes extremely useful later.

---

# 1.3 Separate Given / Changing / Required

For every problem, divide information into three boxes.

```text
┌─────────────────┐
│ GIVEN           │
│ fixed input     │
└─────────────────┘

┌─────────────────┐
│ CHANGING        │
│ choices/state   │
└─────────────────┘

┌─────────────────┐
│ REQUIRED        │
│ answer/output   │
└─────────────────┘
```

Example:

> You have `n` coins. Each operation removes 3 coins. Find the maximum number of operations.

```text
GIVEN
n = initial coins
3 = cost per operation

CHANGING
remaining coins

REQUIRED
maximum operations
```

Define:

```text
k = number of operations
```

Then:

```text
coins used = 3 * k
```

Since you cannot use more than `n`:

```text
3 * k <= n
```

The story is already becoming mathematics.

---

# 1.4 Translate Relationships

This is the heart of Story -> Mathematics.

---

## 1.4.1 Total

Words such as:

```text
total
together
combined
sum
in all
```

usually suggest addition.

Example:

> Alice and Bob together have `S` coins.

```text
A + B = S
```

Visual:

```text
Alice        Bob
  A     +     B
   \         /
    \       /
      TOTAL
        S
```

---

## 1.4.2 Remaining

Words such as:

```text
remaining
left
unused
after spending
```

often suggest:

```text
remaining = initial - used
```

Example:

> Start with `n` coins and spend `x`.

```text
remaining = n - x
```

If each operation spends 3 and there are `k` operations:

```text
used = 3 * k

remaining = n - 3 * k
```

---

## 1.4.3 Difference

Example:

> Alice has 5 more coins than Bob.

```text
A = B + 5
```

Equivalent:

```text
A - B = 5
```

If the problem only says their difference is 5 and does not tell which is larger:

```text
abs(A - B) = 5
```

Important distinction:

```text
"A is 5 more than B"
        ↓
A - B = 5

"difference between A and B is 5"
        ↓
abs(A - B) = 5
```

---

## 1.4.4 More / Less

Be careful about direction.

> A is 3 more than B.

```text
A = B + 3
```

NOT:

```text
B = A + 3
```

Visual:

```text
B: [---------]

A: [---------][+++]
                  3

A = B + 3
```

Similarly:

> A is 3 less than B.

```text
A = B - 3
```

---

## 1.4.5 At least / At most

These words should become automatic.

```text
at least 5
    ↓
>= 5

at most 5
    ↓
<= 5
```

Example:

> A team must contain at least 3 players.

```text
players >= 3
```

Example:

> A bus can carry at most `k` people.

```text
people_in_bus <= k
```

---

## 1.4.6 Ratio / Proportion

Example:

> There are twice as many boys as girls.

Define:

```text
B = boys
G = girls
```

Then:

```text
B = 2 * G
```

Example:

> Red and blue balls are in ratio 2:3.

Think:

```text
red  = 2 parts
blue = 3 parts
```

Introduce a common multiplier:

```text
R = 2 * k
B = 3 * k
```

Visual:

```text
R: [k][k]

B: [k][k][k]

ratio = 2 : 3
```

---

# 1.5 Build Equations

Once relationships are translated, combine them.

Example:

> Alice has 4 more coins than Bob.
> Together they have 20 coins.

Define:

```text
A = Alice
B = Bob
```

Translate statement 1:

```text
A = B + 4
```

Translate statement 2:

```text
A + B = 20
```

Now the entire story is:

```text
A = B + 4
A + B = 20
```

At this point, forget Alice and Bob.

You now have an algebra problem.

Substitute:

```text
(B + 4) + B = 20

2 * B + 4 = 20

2 * B = 16

B = 8
A = 12
```

The key transition:

```text
STORY
  ↓
EQUATIONS
  ↓
STORY NO LONGER MATTERS
```

---

# 1.6 Build Inequalities and Bounds

Many CP problems are not asking for an exact equality.

They ask:

```text
maximum possible
minimum possible
can we afford?
can we fit?
is this enough?
```

These often become inequalities.

---

## Example 1 — Resource limit

> Each operation requires 4 energy.
> You have `E` energy.

Define:

```text
k = number of operations
```

Energy required:

```text
4 * k
```

Available:

```text
E
```

Therefore:

```text
4 * k <= E
```

So:

```text
k <= floor(E / 4)
```

---

## Example 2 — Minimum requirement

> Each group must contain at least 3 people.

For a group size `x`:

```text
x >= 3
```

---

## Example 3 — Range

> Choose an integer `x` between 1 and `n`, inclusive.

Translate:

```text
1 <= x <= n
```

This compact expression replaces a whole English sentence.

---

# 1.7 Remove Irrelevant Story

This is one of the biggest improvements you can make in CF reading.

A story may sound complicated while the mathematical structure is tiny.

Example:

> A wizard has `n` mana crystals. To cast a fire spell he consumes exactly 3 crystals. He wants to cast as many spells as possible before entering a dungeon.

Relevant information:

```text
n crystals
3 per spell
maximize spells
```

Irrelevant for the math:

```text
wizard
fire
dungeon
```

Reduced model:

```text
k = spells

3 * k <= n

answer = floor(n / 3)
```

Mental transformation:

```text
Wizard + mana + fire + dungeon
             ↓
       n items available
       3 used per action
             ↓
       floor(n / 3)
```

---

# 1.8 Translate the Target

Understanding the output is as important as understanding the input.

Look for words such as:

```text
find maximum
find minimum
determine whether possible
count how many
construct
find final value
```

Translate the target before solving.

---

## Maximum

> Maximum number of teams.

Write:

```text
k = number of teams

maximize k
```

Then derive upper bounds on `k`.

---

## Minimum

> Minimum number of operations.

Write:

```text
k = number of operations

minimize k
```

Then ask:

```text
What must k be large enough to accomplish?
```

---

## Possible / Impossible

> Is it possible to reach the target?

Convert into:

```text
Does there exist a valid k/state
satisfying all conditions?
```

This is a feasibility problem.

---

## Count

> How many pairs satisfy the condition?

Translate:

```text
count all (x, y)
such that CONDITION is true
```

---

# 1.9 Combine Multiple Conditions

A problem often gives several rules.

Do not hold them all in English.

Write each one separately.

Example:

> Choose integers `x` and `y`.
> Their sum must be 10.
> `x` must be at least 2.
> `y` must be at most 7.

Translate line by line:

```text
x + y = 10

x >= 2

y <= 7
```

Now substitute:

```text
y = 10 - x
```

The condition:

```text
y <= 7
```

becomes:

```text
10 - x <= 7
```

Therefore:

```text
x >= 3
```

So all conditions together become:

```text
x >= 3
y = 10 - x
```

This demonstrates an important process:

```text
many English conditions
        ↓
separate math conditions
        ↓
combine / simplify
        ↓
smaller model
```

---

# 1.10 Complete CF-Style Example 1

Problem:

> There are `k` students. A classroom can contain at most `m` students. Find the minimum number of classrooms required.

### Step 1 — Identify quantities

```text
students = k
capacity per classroom = m
answer = number of classrooms
```

Define:

```text
c = classrooms
```

### Step 2 — Translate capacity

`c` classrooms can hold:

```text
c * m
```

To fit all students:

```text
c * m >= k
```

### Step 3 — Translate target

We want:

```text
minimum c
```

Therefore:

```text
c = ceil(k / m)
```

For positive integers in C++:

```cpp
int c = (k + m - 1) / m;
```

Complete transformation:

```text
classroom story
      ↓
k items must fit
m items per container
      ↓
c * m >= k
      ↓
minimum c
      ↓
ceil(k / m)
```

---

# 1.11 Complete CF-Style Example 2

Problem:

> Three non-negative integers `x`, `y`, `z` must sum to `S`.
> Each value cannot exceed `K`.
> Count valid triples.

Do not code yet.

### Step 1 — Translate sum

```text
x + y + z = S
```

### Step 2 — Translate ranges

```text
0 <= x <= K
0 <= y <= K
0 <= z <= K
```

### Step 3 — Eliminate one variable

From:

```text
x + y + z = S
```

derive:

```text
z = S - x - y
```

Now instead of trying all:

```text
x
y
z
```

we can try:

```text
x
y
```

and calculate:

```text
z = S - x - y
```

Then check:

```text
0 <= z <= K
```

Visual:

```text
STORY
  ↓
x + y + z = S
0 <= x,y,z <= K
  ↓
z = S - x - y
  ↓
loop x
loop y
calculate z
  ↓
check z
```

The story became an algorithm through mathematics.

---

# 1.12 Complete CF-Style Example 3

Problem:

> You stand at position `x` on a number line.
> Your friend stands at position `y`.
> In one move you can move one unit left or right.
> Find the minimum moves needed to reach your friend.

Strip story:

```text
start = x
target = y
move size = 1
```

Distance:

```text
abs(x - y)
```

Since each move covers one unit:

```text
minimum moves = abs(x - y)
```

Visual:

```text
x ------------------------ y
|<------ distance -------->|

distance = abs(x - y)
```

Again:

```text
people/story
    ↓
positions
    ↓
distance
```

---

# 1.13 The Story-Stripping Method

For every new problem, use this exact sequence.

```text
STEP 1
What is GIVEN?

STEP 2
What is REQUIRED?

STEP 3
What quantities can CHANGE?

STEP 4
Give quantities variables.

STEP 5
Translate every important sentence.

STEP 6
Write equations / inequalities.

STEP 7
Delete the story mentally.

STEP 8
Simplify the mathematical model.

STEP 9
Only now search for the algorithm.
```

Compact version:

```text
GIVEN
  ↓
REQUIRED
  ↓
VARIABLES
  ↓
RELATIONSHIPS
  ↓
MATH
  ↓
SIMPLIFY
  ↓
ALGORITHM
```

---

# 1.14 Translation Dictionary

```text
ENGLISH                         MODEL

together                        a + b
total                           sum
remaining                       initial - used
each                            multiplication
per                             multiplication / rate

A is x more than B              A = B + x
A is x less than B              A = B - x
difference                      subtraction / abs

exactly                         =
at least                        >=
at most                         <=
cannot exceed                   <=
no less than                    >=
no more than                    <=

between 1 and n inclusive       1 <= x <= n

twice                           2 * x
three times                     3 * x

ratio a:b                       a*k and b*k

maximum                         derive upper bounds
minimum                         derive lower requirements
possible?                       feasibility
how many?                       counting

complete groups                 floor division
containers required             ceil division
distance                        abs(a - b)
```

---

# 1.15 Common Translation Mistakes

## Mistake 1 — Reversing "more than"

Wrong:

```text
A is 5 more than B

B = A + 5
```

Correct:

```text
A = B + 5
```

Visual:

```text
B: [-------]

A: [-------][+++++]
                  5
```

---

## Mistake 2 — Confusing at least and at most

```text
at least 5  -> >= 5

at most 5   -> <= 5
```

Memory trick:

```text
AT LEAST 5
5 is the bottom.
Can go higher.

5,6,7,8,...

=> >= 5
```

```text
AT MOST 5
5 is the top.
Can go lower.

...,2,3,4,5

=> <= 5
```

---

## Mistake 3 — Coding before modeling

Bad:

```text
read
 ↓
guess
 ↓
code
```

Better:

```text
read
 ↓
translate
 ↓
simplify
 ↓
observe
 ↓
code
```

---

## Mistake 4 — Treating samples as the definition

Samples help verify understanding.

But the statement defines the problem.

Use:

```text
Statement
   ↓
build your model
   ↓
Samples
   ↓
verify model
```

Not:

```text
Samples
   ↓
guess hidden pattern
```

---

# 1.16 Translation Drills

Do not code these.

Your only job is to produce the mathematical model.

---

## Drill 1

> Alice and Bob have `S` candies in total.
> Alice has `x` candies.

Find Bob's candies.

Model:

```text
A + B = S

A = x

B = S - x
```

---

## Drill 2

> A worker produces `r` items per hour for `h` hours.

Model:

```text
total = r * h
```

---

## Drill 3

> A car has fuel for at most `d` kilometers.

If it travels `x` kilometers:

```text
x <= d
```

---

## Drill 4

> There are twice as many red balls as blue balls.

Model:

```text
R = 2 * B
```

---

## Drill 5

> `x` and `y` are no more than `k` apart.

Model:

```text
abs(x - y) <= k
```

---

## Drill 6

> `n` people must be split into groups containing at most `k` people.
> Find the minimum number of groups.

Model:

```text
g = number of groups

g * k >= n

minimize g

answer = ceil(n / k)
```

---

# 1.17 Practice — You Should Solve These Yourself

Do not look for an algorithm first.

For each problem write:

```text
GIVEN:
REQUIRED:
VARIABLES:
RELATIONSHIPS:
FINAL MODEL:
```

---

## Practice A

> A farmer has `n` kilograms of food.
> Each animal eats exactly `k` kilograms.
> Find the maximum number of animals that can be fully fed.

---

## Practice B

> Two numbers have sum `S`.
> The first number is `d` larger than the second.

---

## Practice C

> A robot starts at `x` and must reach `y`.
> Each move changes its position by one.

---

## Practice D

> A factory has `A` units of metal and `B` units of plastic.
> Each product needs 3 metal and 2 plastic.
> Find the maximum number of products.

---

## Practice E

> Choose non-negative integers `x`, `y`, `z` such that their sum is `S`.

Your goal is to eliminate one variable.

---

# 1.18 Practice Answers

## A

```text
GIVEN:
n food
k food per animal

REQUIRED:
maximum animals

VARIABLE:
a = animals

RELATIONSHIP:
a * k <= n

FINAL:
a <= floor(n / k)

answer = floor(n / k)
```

---

## B

Define:

```text
x = first
y = second
```

Model:

```text
x + y = S

x = y + d
```

Substitute:

```text
y + d + y = S

2 * y + d = S
```

---

## C

```text
start = x
target = y

distance = abs(x - y)

answer = abs(x - y)
```

---

## D

Define:

```text
p = products
```

Metal:

```text
3 * p <= A
```

Plastic:

```text
2 * p <= B
```

Therefore:

```text
p <= floor(A / 3)
p <= floor(B / 2)
```

Maximum:

```text
answer = min(A / 3, B / 2)
```

for positive integer quantities.

---

## E

Given:

```text
x + y + z = S
```

Rearrange:

```text
z = S - x - y
```

So:

```text
choose x
choose y
calculate z
```

instead of independently choosing all three.

---

# 1.19 Chapter Mastery Test

Before moving to Operation Modeling, you should be able to read:

> There are `R` red objects and `B` blue objects. Each package requires 2 red and 3 blue. Find the maximum packages.

and quickly reduce it to:

```text
k = packages

2*k <= R
3*k <= B

k <= R/2
k <= B/3

answer = min(R/2, B/3)
```

You should also see:

> `x + y + z = S`

and immediately think:

```text
z = S - x - y
```

And:

> minimum containers, capacity `k`, `n` items

should suggest:

```text
containers * k >= n

answer = ceil(n / k)
```

---

# 1.20 Final Mental Engine

When a CF statement looks long:

```text
DON'T THINK

"How do I code this story?"
```

Think:

```text
What numbers actually matter?
          ↓
What should I call them?
          ↓
What relationship does each sentence give me?
          ↓
Can I write =, <=, >=, abs, min, max?
          ↓
Can I remove a variable?
          ↓
What is the problem NOW?
```

Your target transformation is:

```text
LONG CF STORY

      ↓ strip names/theme

QUANTITIES

      ↓ assign variables

RELATIONSHIPS

      ↓ translate

EQUATIONS / INEQUALITIES

      ↓ simplify

SMALL MATHEMATICAL PROBLEM

      ↓ derive

ALGORITHM
```

---

# Next Chapter

```text
1. STORY -> MATHEMATICS
           ↓
2. OPERATION MODELING
```

In Chapter 2, we take statements such as:

```text
"In one operation, increase a by 2
and decrease b by 1."
```

and learn to transform:

```text
one operation
      ↓
k operations
      ↓
final-state equation
      ↓
invariant / feasibility / algorithm
```
