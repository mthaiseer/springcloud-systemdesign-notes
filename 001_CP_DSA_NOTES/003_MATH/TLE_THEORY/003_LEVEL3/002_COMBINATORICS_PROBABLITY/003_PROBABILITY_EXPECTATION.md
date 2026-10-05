# Combinatorics & Probability — Level 3
## Probability & Expectation — Simplified Notes

> **Goal:** understand what each probability/expectation formula means before using it.
>
> **Study flow:** **notation → concept simplified → derivation → example → C++ / implementation idea → recognition model**.
>
> **Math rendering:** display equations use fenced `math` blocks only to avoid Markdown/LaTeX rendering errors.
>
> **Lecture scope:** probability basics, conditional probability, dependent vs independent events, expectation, linearity of expectation, expectation problems, Candy Lottery, and probability-bound/random-sampling problems.

---

# Clickable Table of Contents

- [0. Mathematical Notation & Prerequisites](#0-mathematical-notation--prerequisites)
  - [0.1 Outcome and Sample Space](#01-outcome-and-sample-space)
  - [0.2 Event](#02-event)
  - [0.3 Probability Notation](#03-probability-notation)
  - [0.4 Complement](#04-complement)
  - [0.5 Intersection and Union](#05-intersection-and-union)
  - [0.6 Conditional Probability Notation](#06-conditional-probability-notation)
  - [0.7 Random Variable](#07-random-variable)
  - [0.8 Expectation Notation](#08-expectation-notation)
  - [0.9 Sigma Notation](#09-sigma-notation)
- [1. Probability Basics](#1-probability-basics)
- [2. Complement Probability](#2-complement-probability)
- [3. Conditional Probability](#3-conditional-probability)
- [4. Independent vs Dependent Events](#4-independent-vs-dependent-events)
- [5. Expectation — Weighted Average](#5-expectation--weighted-average)
- [6. Linearity of Expectation](#6-linearity-of-expectation)
- [7. Expected Trials Until First Success](#7-expected-trials-until-first-success)
- [8. Problem 1 — Expected Tosses Until First Head](#8-problem-1--expected-tosses-until-first-head)
- [9. Problem 2 — Expected Tosses Until Two Consecutive Heads](#9-problem-2--expected-tosses-until-two-consecutive-heads)
- [10. Problem 3 — Interviews Needed to Hire 10 Candidates](#10-problem-3--interviews-needed-to-hire-10-candidates)
- [11. Problem 4 — Candy Lottery](#11-problem-4--candy-lottery)
- [12. Probability Bounds & Repeated Random Trials](#12-probability-bounds--repeated-random-trials)
- [13. Majority Element — Random Sampling Idea](#13-majority-element--random-sampling-idea)
- [14. Good Line Segment — Practice Prompt](#14-good-line-segment--practice-prompt)
- [15. Final Recognition Sheet](#15-final-recognition-sheet)
- [16. Final Don't-Memorize Model](#16-final-dont-memorize-model)

---

# 0. Mathematical Notation & Prerequisites

Probability notation looks abstract at first, but every symbol has a simple meaning.

---

## 0.1 Outcome and Sample Space

An **outcome** is one possible result of an experiment.

Example:

```text
Toss one coin.
```

Possible outcomes:

```text
H
T
```

The set of all possible outcomes is called the **sample space**.

Common notation:

```math
S
```

Example:

```math
S=\{H,T\}
```

For two dice:

```text
(1,1), (1,2), ..., (6,6)
```

There are:

```text
6 × 6 = 36
```

possible outcomes.

---

## 0.2 Event

An **event** is a set of outcomes that we care about.

Example:

```text
Event A = "coin shows Head"
```

Then:

```math
A=\{H\}
```

For two dice:

```text
Event B = "sum is 7"
```

Favourable outcomes:

```text
(1,6)
(2,5)
(3,4)
(4,3)
(5,2)
(6,1)
```

---

## 0.3 Probability Notation

Notation:

```math
P(A)
```

Read:

```text
"probability of event A"
```

For equally likely outcomes:

```math
P(A)
=
\frac{\text{favourable outcomes}}
{\text{total possible outcomes}}
```

Probability always lies between:

```math
0\le P(A)\le1
```

Meaning:

```text
0 → impossible
1 → certain
```

---

### Example — Fair Coin

Event:

```text
A = Head
```

Favourable:

```text
1
```

Total:

```text
2
```

So:

```math
P(A)=\frac12
```

---

## 0.4 Complement

The complement means:

```text
event A does NOT happen
```

Notation often used:

```math
A^c
```

or informally:

```text
not A
```

Since either `A` happens or it does not:

```math
P(A)+P(A^c)=1
```

Therefore:

```math
P(A^c)=1-P(A)
```

and:

```math
P(A)=1-P(A^c)
```

---

### Example

Probability of at least one Head in 3 fair tosses.

Direct counting is possible, but complement is easier.

Complement:

```text
no Head
= TTT
```

Probability:

```math
P(TTT)
=
\frac18
```

Therefore:

```math
P(\text{at least one H})
=
1-\frac18
=
\frac78
```

---

## 0.5 Intersection and Union

### Intersection

Notation:

```math
A\cap B
```

Read:

```text
"A AND B"
```

Meaning:

```text
both events happen
```

---

### Union

Notation:

```math
A\cup B
```

Read:

```text
"A OR B"
```

Meaning:

```text
A happens, or B happens, or both
```

---

### Example

Suppose:

```text
A = card is a King
B = card is a face card
```

Every King is a face card.

So:

```text
King AND face card
```

is simply:

```text
King
```

---

## 0.6 Conditional Probability Notation

Notation:

```math
P(A\mid B)
```

Read:

```text
"probability of A given B"
```

Meaning:

```text
We already know B happened.
Inside that smaller world, how likely is A?
```

Formula:

```math
P(A\mid B)
=
\frac{P(A\cap B)}{P(B)}
```

provided:

```text
P(B) > 0
```

The vertical bar:

```text
|
```

means:

```text
"given that"
```

---

## 0.7 Random Variable

A **random variable** gives a numerical value to each outcome.

Notation:

```math
X
```

Example:

```text
Coin toss:

H → X = 100
T → X = 10
```

Here `X` represents:

```text
money won
```

Another example:

```text
Die roll:

1 → X=1
2 → X=2
...
6 → X=6
```

---

## 0.8 Expectation Notation

Notation:

```math
E[X]
```

Read:

```text
"expected value of X"
```

Expectation is a **probability-weighted average**.

If possible values are:

```text
x1, x2, ..., xk
```

then:

```math
E[X]
=
\sum_i x_iP(X=x_i)
```

Meaning:

```text
value × probability
for every possible value,
then add them
```

Important:

```text
Expected value does NOT need to be an outcome itself.
```

Example:

```text
expected die value = 3.5
```

even though a die never shows `3.5`.

---

## 0.9 Sigma Notation

Notation:

```math
\sum_{i=1}^{k}a_i
```

means:

```text
a1 + a2 + ... + ak
```

Example:

```math
\sum_{i=1}^{3}i
=
1+2+3
=
6
```

In expectation:

```math
E[X]
=
\sum_i x_iP(X=x_i)
```

means:

```text
add value × probability
over every possible outcome/value
```

---

# 1. Probability Basics

## 1.1 Concept Simplified

Probability asks:

```text
How much of the possible-outcome space is favourable?
```

When all outcomes are equally likely:

```math
P(A)
=
\frac{\text{favourable}}
{\text{total}}
```

---

## 1.2 Example — Head on One Coin

Outcomes:

```text
H
T
```

Total:

```text
2
```

Favourable:

```text
H
```

So:

```math
P(H)=\frac12
```

---

## 1.3 Example — Green Ball

Bag contains:

```text
3 red
2 blue
5 green
```

Total balls:

```text
3+2+5=10
```

Favourable green balls:

```text
5
```

Therefore:

```math
P(\text{green})
=
\frac5{10}
=
\frac12
```

---

## 1.4 Example — Sum 7 With Two Dice

Total outcomes:

```text
6 × 6 = 36
```

Favourable:

```text
(1,6)
(2,5)
(3,4)
(4,3)
(5,2)
(6,1)
```

Count:

```text
6
```

Therefore:

```math
P(\text{sum}=7)
=
\frac6{36}
=
\frac16
```

---

## 1.5 C++ — Simple Probability

```cpp
double probability(
    long long favourable,
    long long total
) {
    return (double)favourable / total;
}
```

---

## 1.6 Recognition Model

```text
equally likely outcomes
        |
        v
count total outcomes
        |
        v
count favourable outcomes
        |
        v
favourable / total
```

**Memory anchor:** probability = favourable share of the outcome space.

---

# 2. Complement Probability

## 2.1 Concept Simplified

Sometimes:

```text
event A
```

has many possible cases.

But:

```text
A does NOT happen
```

has only one or a few cases.

Then calculate the complement.

Formula:

```math
P(A)=1-P(A^c)
```

---

## 2.2 Example — At Least One Head in 3 Tosses

Event:

```text
at least one Head
```

Direct favourable outcomes:

```text
HHH
HHT
HTH
HTT
THH
THT
TTH
```

Count:

```text
7
```

Total:

```text
8
```

So:

```text
7/8
```

But complement is much easier:

```text
no Head
= TTT
```

Probability:

```text
1/8
```

Therefore:

```math
P(\text{at least one Head})
=
1-\frac18
=
\frac78
```

---

## 2.3 Recognition Model

When you see:

```text
at least one
at least once
not all
one or more
```

ask:

```text
Is the opposite event easier?
```

Then:

```text
answer = 1 - opposite
```

---

# 3. Conditional Probability

## 3.1 Concept Simplified

Normal probability asks:

```text
Out of ALL outcomes, how often does A happen?
```

Conditional probability asks:

```text
We already know B happened.

Out of only the B outcomes,
how often does A also happen?
```

So the sample space shrinks from:

```text
all outcomes
```

to:

```text
B outcomes
```

---

## 3.2 Formula Derivation

Inside event `B`, the favourable part for `A` is:

```text
A AND B
```

So:

```math
P(A\mid B)
=
\frac{P(A\cap B)}{P(B)}
```

---

## 3.3 Example — Grid With Black and Blue Marks

Lecture-style example:

```text
30 cells total
6 contain black
5 contain blue
3 contain both black and blue
```

Suppose:

```text
A = blue
B = black
```

Then:

```math
P(B)=\frac6{30}
```

and:

```math
P(A\cap B)=\frac3{30}
```

Therefore:

```math
P(A\mid B)
=
\frac{3/30}{6/30}
```

Cancel:

```math
P(A\mid B)
=
\frac36
=
\frac12
```

Interpretation:

```text
Once we know the selected cell is black,
only 6 black cells matter.

Of those 6,
3 are also blue.

So probability = 3/6.
```

---

## 3.4 Example — King Given Face Card

Deck:

```text
52 cards
```

Face cards:

```text
J, Q, K
for each of 4 suits
```

Total face cards:

```text
12
```

Kings among face cards:

```text
4
```

Therefore:

```math
P(\text{King}\mid\text{Face})
=
\frac4{12}
=
\frac13
```

Using formula:

```math
P(\text{King}\mid\text{Face})
=
\frac{P(\text{King}\cap\text{Face})}
{P(\text{Face})}
```

Since every King is a face card:

```text
King AND Face = King
```

So:

```math
\frac{4/52}{12/52}
=
\frac13
```

---

## 3.5 Recognition Model

```text
given that B happened
        |
        v
throw away outcomes outside B
        |
        v
new denominator = B
        |
        v
favourable = A AND B
```

**Memory anchor:** conditional probability = probability inside a reduced universe.

---

# 4. Independent vs Dependent Events

## 4.1 Independent Events

Two events are independent if:

```text
knowing one happened
does not change the probability of the other
```

Formula:

```math
P(A\cap B)
=
P(A)P(B)
```

Equivalent idea:

```math
P(B\mid A)=P(B)
```

---

### Example — Two Coin Tosses

Event:

```text
A = first toss is Head
B = second toss is Head
```

First toss does not affect second toss.

So:

```math
P(A)=\frac12
```

```math
P(B)=\frac12
```

Therefore:

```math
P(A\cap B)
=
\frac12\cdot\frac12
=
\frac14
```

---

## 4.2 Dependent Events

Events are dependent when the first outcome changes the probability of the next event.

General multiplication rule:

```math
P(A\cap B)
=
P(A)P(B\mid A)
```

---

### Example — Balls Without Replacement

Bag:

```text
5 red
2 black
```

Total:

```text
7
```

Probability first ball is red:

```math
P(R_1)=\frac57
```

If first ball was red:

```text
4 red remain
6 balls remain
```

So:

```math
P(R_2\mid R_1)
=
\frac46
```

But if first ball was black:

```text
5 red remain
6 balls remain
```

So:

```math
P(R_2\mid B_1)
=
\frac56
```

The second probability depends on the first outcome.

Therefore the draws are dependent.

---

## 4.3 Recognition Model

```text
Does event A change the probability of B?
        |
   +----+----+
   |         |
  NO        YES
   |         |
independent dependent
   |         |
P(A)P(B)   P(A)P(B|A)
```

---

# 5. Expectation — Weighted Average

## 5.1 Concept Simplified

Expectation asks:

```text
If I repeat this experiment many times,
what average numerical value should I expect?
```

Formula:

```math
E[X]
=
\sum_i x_iP(X=x_i)
```

Think:

```text
value × how often it happens
```

---

## 5.2 Example — Coin Prize

Fair coin:

```text
Head → Rs.100
Tail → Rs.10
```

Each occurs with probability:

```text
1/2
```

So:

```math
E[X]
=
100\cdot\frac12
+
10\cdot\frac12
```

```math
E[X]
=
50+5
=
55
```

Expected winning:

```text
Rs.55
```

---

## 5.3 Lecture Tree Example — Two Tosses

Suppose the lecture's two-toss payoff tree has final values:

```text
HH → 200
HT → 110
TH → 110
TT → 20
```

Each path has probability:

```text
1/4
```

So:

```math
E[X]
=
200\cdot\frac14
+
110\cdot\frac14
+
110\cdot\frac14
+
20\cdot\frac14
```

Sum:

```text
200 + 110 + 110 + 20 = 440
```

Therefore:

```math
E[X]
=
\frac{440}{4}
=
110
```

---

## 5.4 Example — Expected Die Value

Fair die values:

```text
1,2,3,4,5,6
```

Each probability:

```text
1/6
```

So:

```math
E[D]
=
\frac16(1+2+3+4+5+6)
```

```text
1+2+3+4+5+6 = 21
```

Therefore:

```math
E[D]
=
\frac{21}{6}
=
3.5
```

---

## 5.5 C++ — Discrete Expectation

```cpp
double expectation(
    const vector<double>& value,
    const vector<double>& probability
) {
    double ans = 0.0;

    for (int i = 0; i < (int)value.size(); ++i) {
        ans += value[i] * probability[i];
    }

    return ans;
}
```

---

## 5.6 Recognition Model

```text
outcomes have numerical values
        |
        v
each value has a probability
        |
        v
value × probability
        |
        v
sum everything
```

**Memory anchor:** expectation = weighted average.

---

# 6. Linearity of Expectation

## 6.1 Main Formula

```math
E[X+Y]
=
E[X]+E[Y]
```

More generally:

```math
E[X_1+\cdots+X_n]
=
E[X_1]+\cdots+E[X_n]
```

Also:

```math
E[aX+b]
=
aE[X]+b
```

---

## 6.2 Concept Simplified

If the total value is:

```text
part 1 + part 2 + part 3
```

then expected total is:

```text
expected part 1
+
expected part 2
+
expected part 3
```

This often avoids listing the full combined sample space.

---

## 6.3 Example — Coin Value + Die Value

Let coin value be:

```text
H → 1
T → 2
```

Then:

```math
E[C]
=
1\cdot\frac12
+
2\cdot\frac12
=
1.5
```

For fair die:

```math
E[D]
=
3.5
```

Therefore:

```math
E[C+D]
=
E[C]+E[D]
```

```math
E[C+D]
=
1.5+3.5
=
5
```

This avoids manually enumerating all:

```text
2 × 6 = 12
```

coin-die outcomes.

---

## 6.4 Important Recognition Point

The lecture emphasizes the algebraic property:

```math
E[X+Y]=E[X]+E[Y]
```

When a total quantity can be decomposed into simpler random quantities, expectation can be computed piece by piece.

---

## 6.5 Recognition Model

```text
Expected value of a SUM
        |
        v
split into simpler components
        |
        v
take expectation separately
        |
        v
add
```

**Memory anchor:** expectation distributes over addition.

---

# 7. Expected Trials Until First Success

This is a very useful expectation pattern from the lecture.

Suppose every independent trial succeeds with probability:

```math
p
```

Question:

```text
How many trials do we expect until the first success?
```

---

## 7.1 Recurrence Derivation

Let:

```math
E
```

be the expected number of trials.

We always perform one trial.

With probability:

```math
p
```

we succeed immediately.

With probability:

```math
1-p
```

we fail and are back in the same situation.

Therefore:

```math
E
=
p\cdot1
+
(1-p)(1+E)
```

Expand:

```math
E
=
p
+
1-p
+
(1-p)E
```

So:

```math
E
=
1+(1-p)E
```

Move the expectation term:

```math
pE=1
```

Therefore:

```math
E=\frac1p
```

---

## 7.2 Concept Simplified

```text
success probability = p
```

then:

```text
expected waiting time
≈ inverse of success probability
```

Example:

```text
p = 1/3
```

Expected attempts:

```text
3
```

---

## 7.3 Recognition Model

```text
repeat independent trial
until first success
        |
        v
success probability = p
        |
        v
expected trials = 1/p
```

---

# 8. Problem 1 — Expected Tosses Until First Head

## 8.1 What It Asks

Fair coin.

Keep tossing until the first Head.

Find:

```text
expected number of tosses
```

---

## 8.2 Observation

A Head occurs with:

```math
p=\frac12
```

This is exactly:

```text
repeat until first success
```

So immediately:

```math
E=\frac1p=2
```

---

## 8.3 Step-by-Step Recurrence

Let:

```math
E
```

be expected tosses.

After one toss:

### Head

Probability:

```text
1/2
```

Cost:

```text
1 toss
```

### Tail

Probability:

```text
1/2
```

We used one toss and are back at the start:

```text
1 + E
```

Therefore:

```math
E
=
\frac12(1)
+
\frac12(1+E)
```

Simplify:

```math
E
=
\frac12+\frac12+\frac12E
```

```math
E
=
1+\frac12E
```

Move:

```math
\frac12E=1
```

So:

```math
E=2
```

---

## 8.4 Infinite-Series View From the Lecture

First Head on toss `i` has probability:

```math
\left(\frac12\right)^i
```

because we need:

```text
i-1 tails
then one head
```

Therefore:

```math
E
=
\sum_{i=1}^{\infty}
i\left(\frac12\right)^i
=
2
```

The recurrence is usually easier to use in contests.

---

## 8.5 Recognition Model

```text
waiting for first Head
        |
        v
first-success waiting problem
        |
        v
p = 1/2
        |
        v
E = 1/p = 2
```

---

# 9. Problem 2 — Expected Tosses Until Two Consecutive Heads

## 9.1 What It Asks

Keep tossing a fair coin until:

```text
HH
```

appears.

Find expected tosses.

---

## 9.2 Concept Simplified — We Need States

Unlike "first Head", after one Head we are **closer** to the target.

So the process has memory:

```text
State E0:
currently no useful trailing Head

State E1:
last toss was Head
```

Goal:

```text
HH
```

---

## 9.3 Derivation

Let:

```math
E_0
```

= expected remaining tosses from no partial match.

Let:

```math
E_1
```

= expected remaining tosses when we already have one trailing Head.

---

### State E0

One toss always occurs.

If Head:

```text
probability 1/2
→ move to E1
```

If Tail:

```text
probability 1/2
→ remain in E0
```

Therefore:

```math
E_0
=
1
+
\frac12E_1
+
\frac12E_0
```

---

### State E1

Again one toss occurs.

If Head:

```text
HH completed
→ 0 more tosses
```

If Tail:

```text
pattern broken
→ return to E0
```

Therefore:

```math
E_1
=
1
+
\frac12\cdot0
+
\frac12E_0
```

So:

```math
E_1
=
1+\frac12E_0
```

---

### Solve

From first equation:

```math
E_0
=
1+\frac12E_1+\frac12E_0
```

So:

```math
\frac12E_0
=
1+\frac12E_1
```

Multiply by `2`:

```math
E_0
=
2+E_1
```

Substitute:

```math
E_1
=
1+\frac12E_0
```

Then:

```math
E_0
=
2+1+\frac12E_0
```

```math
E_0
=
3+\frac12E_0
```

Therefore:

```math
\frac12E_0=3
```

So:

```math
E_0=6
```

Answer:

```text
6 tosses
```

---

## 9.4 Recognition Model

```text
waiting for a pattern
        |
        v
progress can be partially preserved
        |
        v
define states for matched prefix
        |
        v
write expectation recurrence
        |
        v
solve simultaneous equations
```

**Memory anchor:** pattern waiting → expectation DP/state equations.

---

# 10. Problem 3 — Interviews Needed to Hire 10 Candidates

## 10.1 What It Asks

Each interviewed candidate is hired with probability:

```math
\frac13
```

How many interviews are expected to hire:

```text
10 people
```

---

## 10.2 Observation

Expected interviews for **one hire**:

```math
\frac{1}{1/3}=3
```

Let:

```text
H1 = interviews needed for first hire
H2 = interviews needed after that for second hire
...
H10
```

Total interviews:

```math
H_1+H_2+\cdots+H_{10}
```

Use linearity.

---

## 10.3 Derivation

For every hire:

```math
E[H_i]=3
```

Therefore:

```math
E[H_1+\cdots+H_{10}]
=
E[H_1]+\cdots+E[H_{10}]
```

```math
=
3+3+\cdots+3
```

10 times:

```math
=30
```

Answer:

```text
30 interviews
```

---

## 10.4 Recognition Model

```text
need r successes
success probability p
        |
        v
each success needs expected 1/p trials
        |
        v
linearity
        |
        v
expected total = r/p
```

For the lecture problem:

```text
r = 10
p = 1/3

10 / (1/3)
= 30
```

---

# 11. Problem 4 — Candy Lottery

**Problem Link:** https://cses.fi/problemset/task/1727

The lecture derives the expected maximum by first finding the probability distribution of the maximum.

---

## 11.1 What It Asks

There are:

```text
n children
```

Each independently receives a uniformly random number of candies from:

```text
1 ... k
```

Find:

```text
expected maximum candies received by any child
```

Let:

```math
M=\max(X_1,X_2,\ldots,X_n)
```

We need:

```math
E[M]
```

---

## 11.2 Mathematical Notation

```math
P(M=i)
```

means:

```text
probability that the maximum is exactly i
```

Then:

```math
E[M]
=
\sum_{i=1}^{k}iP(M=i)
```

So the main task is computing:

```math
P(M=i)
```

---

## 11.3 Concept Simplified

Counting:

```text
maximum EXACTLY i
```

directly is awkward.

Instead use:

```text
maximum <= i
```

Then subtract:

```text
maximum <= i-1
```

So:

```text
max exactly i
=
(max <= i)
-
(max <= i-1)
```

---

## 11.4 Probability Maximum Is At Most i

Each child can receive:

```text
1,2,...,i
```

That is:

```text
i choices
```

For `n` children:

```text
i^n outcomes
```

Total possible distributions:

```text
k^n
```

Therefore:

```math
P(M\le i)
=
\frac{i^n}{k^n}
```

---

## 11.5 Probability Maximum Is Exactly i

Use subtraction:

```math
P(M=i)
=
P(M\le i)-P(M\le i-1)
```

Therefore:

```math
P(M=i)
=
\frac{i^n-(i-1)^n}{k^n}
```

---

## 11.6 Final Expectation Formula

```math
E[M]
=
\sum_{i=1}^{k}
i
\frac{i^n-(i-1)^n}{k^n}
```

---

## 11.7 Step-by-Step Example — n = 2, k = 3

Two children.

Each receives:

```text
1,2,3
```

Total distributions:

```text
3² = 9
```

List:

```text
(1,1) → max 1

(1,2) → max 2
(2,1) → max 2
(2,2) → max 2

(1,3) → max 3
(2,3) → max 3
(3,1) → max 3
(3,2) → max 3
(3,3) → max 3
```

Counts:

```text
max = 1 → 1 outcome
max = 2 → 3 outcomes
max = 3 → 5 outcomes
```

Probabilities:

```math
P(M=1)=\frac19
```

```math
P(M=2)=\frac39
```

```math
P(M=3)=\frac59
```

Expectation:

```math
E[M]
=
1\cdot\frac19
+
2\cdot\frac39
+
3\cdot\frac59
```

```math
E[M]
=
\frac{1+6+15}{9}
=
\frac{22}{9}
```

Approximately:

```text
2.444444
```

---

## 11.8 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, k;
    cin >> n >> k;

    long double ans = 0.0L;

    for (int i = 1; i <= k; ++i) {
        long double pLeI =
            pow((long double)i / k, n);

        long double pLePrev =
            pow((long double)(i - 1) / k, n);

        long double pExactly =
            pLeI - pLePrev;

        ans += i * pExactly;
    }

    cout << fixed << setprecision(6)
         << (double)ans << '\n';
}
```

---

## 11.9 Complexity

Loop over:

```text
i = 1 ... k
```

Time:

```text
O(k)
```

ignoring the internal floating-point exponentiation cost.

Extra space:

```text
O(1)
```

---

## 11.10 Recognition Model

```text
expected maximum
        |
        v
need distribution of maximum
        |
        v
"max exactly i" is hard
        |
        v
count "max <= i"
        |
        v
subtract "max <= i-1"
        |
        v
P(max=i)
        |
        v
Σ i × P(max=i)
```

**Memory anchor:** exact maximum = cumulative up to `i` minus cumulative up to `i-1`.

---

# 12. Probability Bounds & Repeated Random Trials

The lecture finishes with problems where we do not need a deterministic guarantee from one trial.

Instead:

```text
one random attempt succeeds with reasonably high probability
```

Then repeat independently to make failure very small.

---

## 12.1 Core Rule

Suppose one trial fails with probability at most:

```math
q
```

If we perform:

```text
k independent trials
```

then probability that **all** fail is at most:

```math
q^k
```

---

## 12.2 Example — Failure Probability Below 1/2

Suppose:

```math
q<\frac12
```

After `k` independent trials:

```math
P(\text{all fail})
<
\left(\frac12\right)^k
```

For:

```text
k = 10
```

```math
P(\text{all fail})
<
\frac1{1024}
<
0.001
```

For:

```text
k = 20
```

the failure probability becomes much smaller again.

---

## 12.3 Recognition Model

```text
single random attempt
has constant success chance
        |
        v
repeat independently
        |
        v
failure probabilities multiply
        |
        v
exponentially small total failure
```

---

# 13. Majority Element — Random Sampling Idea

The lecture's bounded-probability example is the Majority Element problem.

---

## 13.1 What It Asks

Given an array, a majority element is guaranteed to exist.

Definition:

```text
majority element
=
value occurring more than n/2 times
```

Goal:

```text
find it
```

---

## 13.2 Key Probability Observation

If majority element occurs more than:

```text
n/2
```

times, then a uniformly random array position contains it with probability:

```math
P(\text{pick majority})
>
\frac12
```

Therefore one random sample already has success probability:

```text
> 1/2
```

Failure probability:

```text
< 1/2
```

---

## 13.3 Repeat Sampling

Take `k` independent random indices.

If every sample misses the majority:

```math
P(\text{all misses})
<
\left(\frac12\right)^k
```

For:

```text
k = 10
```

failure:

```text
< 0.001
```

approximately as shown in the lecture.

---

## 13.4 Verification Step

When a value is sampled:

```text
count its occurrences
```

If:

```text
count > n/2
```

it is the majority.

This check prevents returning a wrong sampled value.

---

## 13.5 C++ — Repeated Random Sampling

```cpp
#include <bits/stdc++.h>
using namespace std;

long long randomizedMajority(
    const vector<long long>& a,
    int trials = 30
) {
    mt19937 rng(
        chrono::steady_clock::now()
            .time_since_epoch()
            .count()
    );

    uniform_int_distribution<int>
        pick(0, (int)a.size() - 1);

    for (int t = 0; t < trials; ++t) {
        long long candidate = a[pick(rng)];

        int count = 0;

        for (long long x : a) {
            if (x == candidate)
                ++count;
        }

        if (count > (int)a.size() / 2)
            return candidate;
    }

    return -1; // extremely unlikely if majority exists
}
```

---

## 13.6 Complexity

If we perform:

```text
k trials
```

and verify each candidate in O(n):

```text
O(kn)
```

For fixed small `k`:

```text
effectively O(n)
```

Extra space:

```text
O(1)
```

---

## 13.7 Recognition Model

```text
wanted structure occupies
a constant fraction of candidates
        |
        v
random sample hits it
with constant probability
        |
        v
verify candidate
        |
        v
repeat enough times
to reduce failure
```

**Memory anchor:** large target fraction → random sampling + verification can work.

---

# 14. Good Line Segment — Practice Prompt

The lecture ends with another bounded-probability problem:

```text
Given N points,
find a line segment passing through
at least N/3 points.

The answer is guaranteed to exist.
```

The lecture slide provides the problem statement and constraints but does not develop its complete solution in the supplied material.

So the useful recognition question to carry forward is:

```text
If at least N/3 points lie on the target line,
what is the probability that random sampling
selects useful points from that large subset?
```

This is intended as practice for the same broad idea:

```text
large guaranteed subset
+
random sampling
+
probability bound
```

---

# 15. Final Recognition Sheet

| Problem signal | Think |
|---|---|
| equally likely outcomes | favourable / total |
| "at least one" | try complement |
| "given that B happened" | conditional probability |
| `A AND B` | intersection |
| first event changes second probability | dependent events |
| independent events both occur | multiply probabilities |
| outcomes have numeric rewards | expectation |
| total value is sum of parts | linearity of expectation |
| repeat until first success, probability `p` | expected trials `1/p` |
| wait for a pattern like `HH` | expectation states / recurrence |
| need `r` repeated successes with same `p` | linearity, roughly `r/p` |
| expected maximum | derive distribution of maximum |
| `max exactly i` | `max <= i` minus `max <= i-1` |
| random trial has constant success probability | repeat to shrink failure |
| target occupies `> n/2` elements | random sampling hits it with `>1/2` |

---

# 16. Final Don't-Memorize Model

```text
PROBABILITY
-----------
equally likely outcomes:

P(A)
=
favourable / total


COMPLEMENT
----------
P(A)
=
1 - P(not A)

useful for:
"at least one"


CONDITIONAL PROBABILITY
-----------------------
P(A | B)

means:
probability of A
inside the B-world

P(A | B)
=
P(A and B) / P(B)


INDEPENDENCE
------------
A does not affect B

P(A and B)
=
P(A)P(B)


DEPENDENCE
----------
A changes probability of B

P(A and B)
=
P(A)P(B | A)


EXPECTATION
-----------
weighted average

E[X]
=
Σ value × probability


LINEARITY
---------
E[X+Y]
=
E[X]+E[Y]


FIRST SUCCESS
-------------
success probability = p

expected trials:
1/p


PATTERN WAITING
---------------
partial progress matters

define states
→ expectation recurrence
→ solve equations


EXPECTED MAXIMUM
----------------
exact maximum i
=
(max <= i)
-
(max <= i-1)

then:

E[max]
=
Σ i × P(max=i)


RANDOMIZED BOUND
----------------
single failure probability <= q

k independent failures:
q^k

constant success chance
+
repetition
→ exponentially small failure
```

> **Final memory anchor:**  
> **Probability measures likelihood. Conditional probability changes the sample space. Expectation is a weighted average. Linearity breaks a hard total into easy pieces. Repetition turns a constant success probability into a very small failure probability.**
