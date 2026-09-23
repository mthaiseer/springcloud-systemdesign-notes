# Part 23 — Recurrences

> **Goal:** turn repeated dependencies in Codeforces problems into precise mathematical recurrences, derive them from first/last-step reasoning, choose correct base cases and operators, and then evaluate them efficiently using iteration, DP, prefix optimization, closed forms, or matrix exponentiation.
>
> **Core workflow:** `Story → State definition → First/last-step decomposition → Recurrence → Base cases → Verify tiny states → Optimize evaluation`
>
> **Recognition question:** **What smaller state completely describes everything I need to build the current answer?**

> **V3 real-world standard:** Every section contains a worked scenario with concrete values, state definition, mathematical derivation, manual dry run, ASCII visualization where useful, and explicit Codeforces recognition/mapping.

## Table of Contents

- [23.0 Recurrence Mental Model](#230-recurrence-mental-model)
- [23.1 Anatomy of a Recurrence](#231-anatomy-of-a-recurrence)
- [23.2 Story to Recurrence](#232-story-to-recurrence)
- [23.3 Base Cases and Boundary States](#233-base-cases-and-boundary-states)
- [23.4 First-Step Decomposition](#234-first-step-decomposition)
- [23.5 Last-Step Decomposition](#235-last-step-decomposition)
- [23.6 Additive Recurrences](#236-additive-recurrences)
- [23.7 Multiplicative Recurrences](#237-multiplicative-recurrences)
- [23.8 Min and Max Recurrences](#238-min-and-max-recurrences)
- [23.9 Boolean Recurrences](#239-boolean-recurrences)
- [23.10 Fibonacci-Type Recurrences](#2310-fibonacci-type-recurrences)
- [23.11 Linear Recurrences with Constant Coefficients](#2311-linear-recurrences-with-constant-coefficients)
- [23.12 Recurrence to Iteration](#2312-recurrence-to-iteration)
- [23.13 Memoization and Overlapping States](#2313-memoization-and-overlapping-states)
- [23.14 Space Compression](#2314-space-compression)
- [23.15 Prefix-Sum Optimization of Recurrences](#2315-prefix-sum-optimization-of-recurrences)
- [23.16 Sliding-Window Recurrence Optimization](#2316-sliding-window-recurrence-optimization)
- [23.17 Recurrences with Modulo](#2317-recurrences-with-modulo)
- [23.18 Matrix Form of a Recurrence](#2318-matrix-form-of-a-recurrence)
- [23.19 Matrix Exponentiation Dry Run](#2319-matrix-exponentiation-dry-run)
- [23.20 Characteristic Equation Intuition](#2320-characteristic-equation-intuition)
- [23.21 Divide-and-Conquer Recurrences](#2321-divide-and-conquer-recurrences)
- [23.22 Master-Theorem Recognition](#2322-master-theorem-recognition)
- [23.23 Recurrence Unrolling](#2323-recurrence-unrolling)
- [23.24 Telescoping Recurrences](#2324-telescoping-recurrences)
- [23.25 Recurrence to Closed Form](#2325-recurrence-to-closed-form)
- [23.26 Recurrences Hidden in Counting](#2326-recurrences-hidden-in-counting)
- [23.27 State Expansion for Recurrences](#2327-state-expansion-for-recurrences)
- [23.28 Recurrence vs Dynamic Programming](#2328-recurrence-vs-dynamic-programming)
- [23.29 When a Recurrence Is the Wrong Model](#2329-when-a-recurrence-is-the-wrong-model)
- [23.30 60-Second Recurrence Discovery Workflow](#2330-60-second-recurrence-discovery-workflow)
- [23.31 Codeforces Recognition Map](#2331-codeforces-recognition-map)
- [23.32 Common Mistakes](#2332-common-mistakes)
- [23.33 Fast Revision Card](#2333-fast-revision-card)

---

## 23.0 Recurrence Mental Model

A recurrence defines a state using smaller states.

```text
CURRENT ANSWER
     ↓ depends on
SMALLER ANSWERS
```

Canonical form:

```text
F(n) = expression involving F(n-1), F(n-2), ...
```


### Core modeling question

```text
"What must the LAST step / FIRST step / previous state have been?"
```

That question often reveals the recurrence.

#### Detailed real-world walkthrough — Staircase in an office

Suppose your office is on the top of a staircase with `5` steps. At every move you are allowed to climb either `1` step or `2` steps.

```text
Target = step 5
Allowed moves = +1 or +2
```

Define:

```text
F(n) = number of different move sequences that reach step n
```

Now focus only on the **last move**. If you finish on step `5`, there are only two possibilities:

```text
last jump = 1  -> you were previously on step 4 -> F(4) possibilities
last jump = 2  -> you were previously on step 3 -> F(3) possibilities
```

Therefore:

```text
F(5) = F(4) + F(3)
```

Build from the smallest states:

```text
F(0)=1   // already at the start: one empty way
F(1)=1   // 1
F(2)=2   // 1+1, 2
F(3)=3   // 1+1+1, 1+2, 2+1
F(4)=5
F(5)=8
```

Visual model:

```text
                 STEP 5
                /      \
          last +1      last +2
             /            \
          STEP 4          STEP 3
           F(4)            F(3)
             \              /
              \            /
               F(5)=5+3=8
```

**CF mapping:** whenever every valid construction must end in one of a small number of possible actions, classify solutions by that final action and sum the predecessor states.

---

## 23.1 Anatomy of a Recurrence

Every useful recurrence needs three things:

```text
1. STATE
2. TRANSITION
3. BASE CASE
```

Example:

```text
F(n)=F(n-1)+n
F(0)=0
```

Here:

```text
state      = n
transition = F(n-1)+n
base       = F(0)=0
```

Dry run:

```text
F(1)=0+1=1
F(2)=1+2=3
F(3)=3+3=6
F(4)=6+4=10
```


### Common failure

A recurrence without a base case is incomplete:

```text
F(n)=F(n-1)+n
```

keeps asking for:

```text
F(n-1), F(n-2), ...
```

until some starting value stops the chain.

#### Detailed real-world walkthrough — Increasing daily savings

You start with `0 lei`. On day `1` you save `1 lei`, on day `2` another `2 lei`, and on day `n` another `n lei`.

Question:

```text
How much have you saved after n days?
```

Define the state:

```text
F(n) = total savings after day n
```

To get today's total, you need yesterday's total plus today's deposit:

```text
F(n)=F(n-1)+n
```

The process must start somewhere:

```text
F(0)=0
```

Dry run:

```text
Day 0: F(0)=0
          +1
Day 1: F(1)=1
          +2
Day 2: F(2)=3
          +3
Day 3: F(3)=6
          +4
Day 4: F(4)=10
```

This example exposes all three recurrence ingredients:

```text
STATE      -> F(n) = savings after n days
TRANSITION -> F(n) = F(n-1)+n
BASE CASE  -> F(0) = 0
```

**CF mapping:** before coding a recurrence, force yourself to write these same three lines for the contest state.

---

## 23.2 Story to Recurrence

The main skill is converting the story into smaller subproblems.

### Example — tiling a 2 by n board

Tiles are dominoes:

```text
2×1
```

Let:

```text
F(n)=number of tilings of a 2×n board
```

Look at the leftmost part.

Case 1:

```text
one vertical domino

┌─┐
│ │ + remaining 2×(n-1)
└─┘
```

Contribution:

```text
F(n-1)
```

Case 2:

```text
two horizontal dominoes

══
══

+ remaining 2×(n-2)
```

Contribution:

```text
F(n-2)
```

Cases are disjoint and complete:

```text
F(n)=F(n-1)+F(n-2)
```


### Recognition trigger

Words like:

```text
ways
sequences
build
split
last operation
first operation
previous state
```

often lead naturally to a recurrence.

#### Detailed real-world walkthrough — Filling a 2×5 storage rack

A warehouse has a rack of size `2×5`. Every box occupies exactly `2×1`, but it may be placed vertically or two boxes may be placed horizontally across a `2×2` region.

Define:

```text
F(n) = number of ways to fill a 2×n rack
```

Look at the **left edge**.

```text
Case A: place one vertical box
        consumes width 1
        remaining rack = 2×(n-1)
        ways = F(n-1)

Case B: place two horizontal boxes
        consumes width 2
        remaining rack = 2×(n-2)
        ways = F(n-2)
```

No valid arrangement belongs to both cases, and every valid arrangement belongs to one of them:

```text
F(n)=F(n-1)+F(n-2)
```

For width `5`:

```text
F(0)=1
F(1)=1
F(2)=2
F(3)=3
F(4)=5
F(5)=8
```

Visual:

```text
2×5 rack
┌──┬────────┐
│VV│ 2×4    │  -> F(4)
│VV│remain  │
└──┴────────┘

or

┌────┬──────┐
│HHHH│ 2×3  │  -> F(3)
│HHHH│remain│
└────┴──────┘

total = F(4)+F(3)
```

**CF mapping:** strip away “rack/tiles/boxes”; what remains is a partition of all solutions by the first structural choice.

---

## 23.3 Base Cases and Boundary States

Base cases encode the smallest fully known states.

For staircase counting:

```text
F(0)=1
```

Why `1`, not `0`?

There is exactly one way to complete zero remaining steps:

```text
do nothing
```

This empty construction is often essential.

### Example

```text
F(n)=F(n-1)+F(n-2)

F(0)=1
F(1)=1
```

Then:

```text
F(2)=2
```

If incorrectly using:

```text
F(0)=0
```

you get:

```text
F(2)=1
```

and the entire sequence is wrong.


### Contest checklist

Ask:

```text
What happens at n=0?
What happens at n=1?
Are negative states invalid or zero?
Does an empty construction count?
```

#### Detailed real-world walkthrough — Zero remaining deliveries

A driver must complete a route. Let:

```text
F(r) = number of valid ways to finish when r delivery stops remain
```

Suppose `r=0`.

There is no additional action to perform, but the already-completed route is itself one valid completion:

```text
F(0)=1
```

Why this matters: suppose the final delivery can be completed by a one-stop move. Then:

```text
F(1)=F(0)=1
```

If you incorrectly set:

```text
F(0)=0
```

you would conclude there are zero ways to finish the last stop.

Visual:

```text
1 stop remaining
      |
complete it
      v
0 stops remaining
      |
one successfully completed route
      v
F(0)=1
```

**CF mapping:** in counting DP, the empty construction frequently contributes `1`; in feasibility DP it is often `true`; in optimization DP it may be `0`. Base cases depend on the meaning of the state.

---

## 23.4 First-Step Decomposition

One way to derive a recurrence is to classify solutions by the **first** decision.

Suppose from position `n` you may move:

```text
+1
+3
```

Let:

```text
F(n)=ways to reach target N from position n
```

First move:

```text
n -> n+1
or
n -> n+3
```

Therefore:

```text
F(n)=F(n+1)+F(n+3)
```

with:

```text
F(N)=1
F(n>N)=0
```


### Proof shape

```text
ALL solutions
   /      \
first A  first B
  |        |
smaller  smaller
problem   problem
```

If the groups are disjoint and cover every solution, their counts add.

---

### Detailed Real-World Scenario — Restaurant delivery route — first road choice

A delivery rider starts at checkpoint `0` and must reach checkpoint `6`. From any checkpoint, the rider may move either `+1` km or `+3` km.

Define:

```text
F(p) = number of valid routes from position p to checkpoint 6
```

At position `2`, the **first move** has only two possibilities:

```text
2 -> 3   using +1
2 -> 5   using +3
```

After that first move, the rest of the route is exactly a smaller problem:

```text
routes beginning 2->3 = F(3)
routes beginning 2->5 = F(5)
```

Therefore:

```text
F(2)=F(3)+F(5)
```

Use terminal states:

```text
F(6)=1   // already reached destination
F(p>6)=0 // overshot destination
```

Dry run backward:

```text
F(6)=1
F(5)=F(6)+F(8)=1+0=1
F(4)=F(5)+F(7)=1+0=1
F(3)=F(4)+F(6)=1+1=2
F(2)=F(3)+F(5)=2+1=3
```

ASCII:

```text
position 2
   /    +1     +3
 /       3         5
|         |
F(3)=2   F(5)=1
 \       /
  \     /
   F(2)=3
```

**Mathematical model:** classify every complete route by its first move. The two groups are disjoint and complete, so their counts add.

**CF recognition:** when the statement repeatedly asks “choose one of these actions, then solve the remaining suffix,” first-step decomposition is a natural recurrence.

## 23.5 Last-Step Decomposition

Often it is easier to ask:

```text
"How could I have arrived HERE?"
```

### Example — staircase

To reach stair `n`, the last jump is:

```text
1 -> previous n-1
2 -> previous n-2
```

So:

```text
F(n)=F(n-1)+F(n-2)
```


### Contest habit

If forward choices are confusing, reverse perspective:

```text
"What can the final action be?"
```

The last-step view frequently removes story complexity.

---

### Detailed Real-World Scenario — Metro journey — classify by the final ride

A commuter wants to travel exactly `5` zones. Every ticket ride covers either `1` zone or `2` zones.

Define:

```text
F(n) = number of ordered ride sequences covering exactly n zones
```

Instead of asking what the commuter does first, ask:

```text
What could the FINAL ride be?
```

For `5` zones:

```text
final ride = 1 zone
previous distance = 4
contribution = F(4)

final ride = 2 zones
previous distance = 3
contribution = F(3)
```

Therefore:

```text
F(5)=F(4)+F(3)
```

Dry run:

```text
F(0)=1
F(1)=1
F(2)=2
F(3)=3
F(4)=5
F(5)=8
```

ASCII:

```text
distance 5
   ↑       ↑
 +1        +2
from 4    from 3
 F(4)      F(3)
   \        /
    \      /
     5 + 3
       =
       8
```

**Why useful:** the final action often has fewer possibilities than the full forward story.

**CF recognition:** phrases such as “ways to end at,” “last operation,” “last character,” or “last chosen item” should trigger last-step decomposition.

## 23.6 Additive Recurrences

An additive recurrence appears when independent, disjoint cases combine.

General form:

```text
F(n)=F(n-a)+F(n-b)+...
```

### Example — moves of 1, 2, or 3

```text
F(n)=F(n-1)+F(n-2)+F(n-3)
```

Base:

```text
F(0)=1
F(n<0)=0
```

Dry run:

```text
F(1)=1
F(2)=2
F(3)=4
F(4)=7
```

### Why addition?

The final move is exactly one of:

```text
1
2
3
```

These cases cannot overlap.


### Mathematical trigger

```text
disjoint alternatives
      ↓
sum their counts
```

#### Detailed real-world walkthrough — Courier jumps of 1, 2, or 3 blocks

A courier starts at block `0` and must reach block `4`. Each action moves exactly `1`, `2`, or `3` blocks.

Define:

```text
F(n)=number of ordered move sequences totaling n blocks
```

For the final move into block `4`:

```text
last jump 1 -> previous total 3 -> F(3)
last jump 2 -> previous total 2 -> F(2)
last jump 3 -> previous total 1 -> F(1)
```

Hence:

```text
F(4)=F(3)+F(2)+F(1)
```

Build:

```text
F(0)=1
F(1)=1
F(2)=2
F(3)=4
F(4)=4+2+1=7
```

The seven routes are:

```text
1+1+1+1
1+1+2
1+2+1
2+1+1
2+2
1+3
3+1
```

**CF mapping:** “choose one of several last moves” + “count all ordered ways” almost always means an additive recurrence.

---

## 23.7 Multiplicative Recurrences

Some recursive constructions multiply because independent choices are combined.

Example structure:

```text
F(n)=k * F(n-1)
```

### Example — strings of length n

Alphabet size:

```text
k=3
```

Let:

```text
F(n)=number of length-n strings
```

Choose the first character:

```text
3 choices
```

Then fill remaining `n-1` positions:

```text
F(n-1) choices
```

Therefore:

```text
F(n)=3F(n-1)
```

Base:

```text
F(0)=1
```

So:

```text
F(n)=3^n
```


### Modeling distinction

```text
OR between disjoint cases -> add
AND between independent choices -> multiply
```

#### Detailed real-world walkthrough — Creating a 4-digit access code

A lock allows digits:

```text
0,1,2
```

in every position. Repetition is allowed.

Define:

```text
F(n)=number of codes of length n
```

To create a length-`n` code:

```text
choose first digit: 3 choices
choose remaining n-1 digits: F(n-1) choices
```

For every first-digit choice, all `F(n-1)` suffixes are available:

```text
F(n)=3*F(n-1)
F(0)=1
```

Dry run:

```text
F(1)=3
F(2)=3*3=9
F(3)=3*9=27
F(4)=3*27=81
```

ASCII decision tree:

```text
position 1
 /   |   \
0    1    2
|    |    |
F(3) F(3) F(3)

total = 3*F(3)
```

**CF mapping:** mutually exclusive alternatives are added; independent choices that must occur together multiply.

---

## 23.8 Min and Max Recurrences

Optimization recurrences replace counting addition with `min` or `max`.

### Example — minimum cost to reach position n

Allowed previous positions:

```text
n-1
n-2
```

Cost of entering `n`:

```text
cost[n]
```

Then:

```text
F(n)=cost[n]+min(F(n-1),F(n-2))
```


### Maximum version

If choosing a predecessor yields rewards:

```text
F(n)=reward[n]+max(F(n-1),F(n-2))
```

### Recognition trigger

```text
number of ways -> sum
minimum answer -> min
maximum answer -> max
possible? -> OR / boolean
```

#### Detailed real-world walkthrough — Cheapest route between checkpoints

You must reach checkpoint `4`. You may arrive at checkpoint `i` only from `i-1` or `i-2`. Entry costs are:

```text
checkpoint: 1  2  3  4
cost:       4  2  7  3
```

Define:

```text
F(i)=minimum total cost to reach checkpoint i
```

To reach checkpoint `4`, your final predecessor is either `3` or `2`:

```text
F(4)=cost[4]+min(F(3),F(2))
```

Using base:

```text
F(0)=0
```

one possible dry run is:

```text
F(1)=4
F(2)=2
F(3)=7+min(2,4)=9
F(4)=3+min(9,2)=5
```

The key difference from counting is:

```text
COUNT all predecessor solutions -> +
CHOOSE cheapest predecessor     -> min
```

**CF mapping:** after identifying predecessor states, the problem objective tells you which operator belongs in the recurrence.

---

## 23.9 Boolean Recurrences

Some recurrences answer feasibility rather than count or optimize.

Let:

```text
possible[n]
```

mean whether total `n` can be constructed.

Using pieces of size `3` and `5`:

```text
possible[n]
=
possible[n-3] OR possible[n-5]
```

Base:

```text
possible[0]=true
possible[n<0]=false
```

### Dry run

```text
possible[3]=true
possible[5]=true
possible[6]=true   (3+3)
possible[7]=false
possible[8]=true   (3+5)
```


### Mathematical operator

```text
exists valid previous state
        ↓
logical OR
```

#### Detailed real-world walkthrough — Buying exactly 11 bottles

A store sells only packs of:

```text
3 bottles
5 bottles
```

Question:

```text
Can I buy exactly n bottles?
```

Define:

```text
possible[n] = whether total n can be formed
```

The last pack must be either `3` or `5`:

```text
possible[n]
=
possible[n-3] OR possible[n-5]
```

Base:

```text
possible[0]=true
negative totals=false
```

For `n=11`:

```text
possible[11]
= possible[8] OR possible[6]

possible[8]
= possible[5] OR possible[3]
= true OR true
= true
```

So:

```text
11 = 3+3+5
```

works.

Visual:

```text
11
├─ remove pack 3 -> 8 -> reachable
└─ remove pack 5 -> 6 -> reachable

at least one TRUE
=> 11 reachable
```

**CF mapping:** when the question is YES/NO, replace arithmetic counting with boolean reachability.

---

## 23.10 Fibonacci-Type Recurrences

The classic second-order recurrence:

```text
F(n)=F(n-1)+F(n-2)
```

appears whenever every object/state is formed from two disjoint predecessor types.

### Fibonacci numbers

With:

```text
F(0)=0
F(1)=1
```

we obtain:

```text
0,1,1,2,3,5,8,13,...
```

### Staircase variant

With:

```text
W(0)=1
W(1)=1
```

we obtain:

```text
1,1,2,3,5,8,...
```

Same transition, different base cases.

### Important lesson

Do not identify a problem merely as:

```text
"Fibonacci"
```

Identify:

```text
WHY the two predecessor states are complete and disjoint.
```


---

### Detailed Real-World Scenario — Office staircase — why Fibonacci appears

An office staircase has `6` steps. An employee may climb `1` or `2` steps at a time.

Define:

```text
W(n)=number of ways to reach step n
```

For step `6`, every route ends in exactly one of:

```text
step 5 -> +1 -> step 6
step 4 -> +2 -> step 6
```

Hence:

```text
W(6)=W(5)+W(4)
```

Build the values:

```text
W(0)=1
W(1)=1
W(2)=2
W(3)=3
W(4)=5
W(5)=8
W(6)=13
```

Visual:

```text
                  step 6
                 /                  +1 from 5   +2 from 4
               /                       8 routes        5 routes
               \            /
                \          /
                  13
```

The recurrence is Fibonacci-shaped, but the base cases differ from the classical Fibonacci definition:

```text
classic: F(0)=0, F(1)=1
stairs : W(0)=1, W(1)=1
```

**CF recognition:** do not memorize “stairs = Fibonacci.” Prove that the solution set splits into exactly the `n-1` and `n-2` predecessor groups.

## 23.11 Linear Recurrences with Constant Coefficients

General form:

```text
F(n)=c1F(n-1)+c2F(n-2)+...+ckF(n-k)
```

where coefficients are fixed.

Example:

```text
F(n)=2F(n-1)+3F(n-2)
```

### Dry run

Let:

```text
F(0)=1
F(1)=2
```

Then:

```text
F(2)=2*2+3*1=7
F(3)=2*7+3*2=20
F(4)=2*20+3*7=61
```


### Why important in CF?

For small `n`:

```text
iterate O(nk)
```

For huge `n`:

```text
matrix exponentiation
linear-recurrence techniques
```

may be needed.

---

### Detailed Real-World Scenario — Subscription growth with two customer sources

A service models new subscriptions using two earlier months:

```text
customers(n)
=
2 * customers(n-1)
+
3 * customers(n-2)
```

Suppose:

```text
F(0)=1
F(1)=2
```

Interpretation:

```text
each previous-month unit creates 2 contributions
each two-month-old unit creates 3 contributions
```

Dry run:

```text
F(2)=2*F(1)+3*F(0)
    =2*2+3*1
    =7

F(3)=2*7+3*2
    =20

F(4)=2*20+3*7
    =61
```

ASCII:

```text
F(4)
 |
 +-- 2 × F(3) = 2 × 20 = 40
 |
 +-- 3 × F(2) = 3 × 7  = 21
 |
 +---------------------------
                  total = 61
```

**Mathematical model:**

```text
F(n)=c1F(n-1)+c2F(n-2)
```

with constant coefficients `c1=2`, `c2=3`.

**CF recognition:** if the same fixed weighted combination of the last few states is repeated for very large `n`, matrix exponentiation may later become relevant.

## 23.12 Recurrence to Iteration

A recurrence is a mathematical definition.

It does **not** require recursive code.

Example:

```text
F(n)=F(n-1)+F(n-2)
```

Recursive tree:

```text
F(5)
├─ F(4)
│  ├─ F(3)
│  └─ F(2)
└─ F(3)
   ├─ F(2)
   └─ F(1)
```

Repeated states appear.

Iterative computation:

```text
F(0), F(1)
   ↓
F(2)
   ↓
F(3)
   ↓
...
F(n)
```

Complexity:

```text
naive recursion -> exponential
iteration/DP    -> O(n)
```


---

### Detailed Real-World Scenario — Monthly forecast without repeated recalculation

A company predicts monthly demand with:

```text
F(n)=F(n-1)+F(n-2)
```

Suppose:

```text
F(0)=0
F(1)=1
```

To calculate `F(6)` using naive recursion:

```text
F(6)
├── F(5)
│   ├── F(4)
│   └── F(3)
└── F(4)   <- repeated
    ├── F(3)
    └── F(2)
```

The same monthly forecasts are calculated repeatedly.

Instead calculate sequentially:

```text
Month: 0  1  2  3  4  5  6
F:     0  1  1  2  3  5  8
```

Step by step:

```text
F(2)=F(1)+F(0)=1
F(3)=F(2)+F(1)=2
F(4)=F(3)+F(2)=3
F(5)=F(4)+F(3)=5
F(6)=F(5)+F(4)=8
```

So the recurrence remains the same, but evaluation changes from a repeated recursion tree to one forward pass.

**CF recognition:** deriving `F(n)` recursively does not mean the implementation should literally recurse. Look for repeated states and choose memoization or iteration.

## 23.13 Memoization and Overlapping States

Memoization stores recurrence results.

### Example

```text
F(5)
```

needs:

```text
F(4),F(3)
```

and `F(4)` also needs:

```text
F(3)
```

Without caching:

```text
F(3)
```

is recomputed.

With memoization:

```text
compute F(3) once
store it
reuse it
```

### Complexity

For Fibonacci:

```text
naive recursion: O(2^n) approximately
memoized:        O(n)
```


### Recognition trigger

```text
same state reached by multiple recursive paths
```

means:

```text
overlapping subproblems
```

---

### Detailed Real-World Scenario — Customer forecast cache — repeated subproblems

A forecast follows:

```text
F(n)=F(n-1)+F(n-2)
```

Suppose an API requests `F(6)` recursively.

Without caching:

```text
F(6)
├─ F(5)
│  ├─ F(4)
│  └─ F(3)
└─ F(4)   <- calculated again
   ├─ F(3)
   └─ F(2)
```

`F(4)`, `F(3)`, and `F(2)` appear many times.

With memoization:

```text
request F(4)
   ↓
not cached
   ↓
compute once
   ↓
cache[4] = answer
   ↓
next request F(4)
   ↓
return cache[4]
```

For Fibonacci-like states:

```text
naive recursion -> exponential repeated work
memoization      -> each n solved once -> O(n)
```

Concrete values:

```text
F(0)=0
F(1)=1
F(2)=1
F(3)=2
F(4)=3
F(5)=5
F(6)=8
```

**CF recognition:** draw the first 2–3 levels of the recursion tree. If identical states reappear, cache them or compute bottom-up.

## 23.14 Space Compression

If:

```text
F(n)
```

depends only on the last `k` states, storing the entire array may be unnecessary.

Fibonacci:

```text
F(n)=F(n-1)+F(n-2)
```

needs only:

```text
prev2
prev1
```

Update:

```text
cur = prev1 + prev2
prev2 = prev1
prev1 = cur
```

Space:

```text
O(n) -> O(1)
```


### Important

Space compression is valid only if discarded states will never be required later.

---

### Detailed Real-World Scenario — Temperature forecast — keep only required history

Suppose tomorrow's forecast index depends only on the previous two days:

```text
F(n)=F(n-1)+F(n-2)
```

To compute:

```text
F(8)
```

you do not need to keep:

```text
F(0),F(1),F(2),...,F(7)
```

forever.

At one moment:

```text
prev2 = F(5)=5
prev1 = F(6)=8
```

Compute:

```text
cur = 5+8 = 13 = F(7)
```

Shift:

```text
prev2 <- 8
prev1 <- 13
```

Next:

```text
cur = 8+13 = 21 = F(8)
```

ASCII:

```text
before:
[F(5)=5] [F(6)=8]
    |        |
 prev2     prev1

cur = 5+8 = 13

after shift:
[F(6)=8] [F(7)=13]
```

Memory:

```text
full array -> O(n)
rolling values -> O(1)
```

**CF recognition:** inspect the maximum backward dependency. If state `i` needs only the previous `k` states, consider a rolling buffer of size `k`.

## 23.15 Prefix-Sum Optimization of Recurrences

Sometimes a recurrence contains a range sum:

```text
F(n)=F(n-1)+F(n-2)+...+F(n-k)
```

Naively:

```text
O(k)
```

per state.

Define prefix:

```text
P(i)=F(0)+F(1)+...+F(i)
```

Then:

```text
F(n)
=
P(n-1)-P(n-k-1)
```

with boundary handling.

Now each transition is:

```text
O(1)
```

and all `n` states:

```text
O(n)
```

instead of:

```text
O(nk)
```


### Connection

This directly links **Part 15 Prefix Mathematics** with recurrence optimization.

#### Detailed real-world walkthrough — Rolling seven-day demand

A warehouse defines today's planning score as the sum of the previous `7` daily scores:

```text
F(i)=F(i-1)+F(i-2)+...+F(i-7)
```

Suppose you need this for `100,000` days.

Naively each day adds seven values. For general window size `k`:

```text
O(nk)
```

Define:

```text
P(i)=F(0)+F(1)+...+F(i)
```

Then the previous `k` values are one range sum:

```text
F(i)=P(i-1)-P(i-k-1)
```

Example with `k=3`:

```text
F = [1,1,2,4,...]

F(4)
=F(3)+F(2)+F(1)
=4+2+1
=7

using prefix:
=P(3)-P(0)
=8-1
=7
```

**CF mapping:** if a DP transition literally says “sum a contiguous interval of earlier states,” immediately test whether prefix sums can remove an inner loop.

---

## 23.16 Sliding-Window Recurrence Optimization

For:

```text
F(n)=sum of previous k values
```

maintain:

```text
window = F(n-1)+...+F(n-k)
```

Then:

```text
F(n)=window
```

Advance:

```text
window += F(n)
window -= F(n-k)
```

### Example with k=3

Suppose:

```text
F(0)=1
```

and invalid negative states are `0`.

Then:

```text
F(1)=1
F(2)=2
F(3)=4
F(4)=7
```

For `F(4)`:

```text
window=F(3)+F(2)+F(1)
      =4+2+1
      =7
```


### Recognition trigger

```text
transition = contiguous range of previous DP states
```

suggests:

```text
prefix sum or sliding window
```

---

### Detailed Real-World Scenario — Supermarket rolling demand — full dry run

A supermarket defines each day's demand estimate as the sum of the previous `3` estimates.

Start with:

```text
F(0)=1
```

and treat unavailable negative days as `0`.

Then:

```text
F(1)=F(0)=1

F(2)=F(1)+F(0)
    =1+1
    =2

F(3)=F(2)+F(1)+F(0)
    =2+1+1
    =4

F(4)=F(3)+F(2)+F(1)
    =4+2+1
    =7
```

The naive calculation for `F(4)` is:

```text
previous 3 days

F(1)=1
F(2)=2
F(3)=4
-------
sum = 7
```

Maintain a rolling window instead.

Before computing `F(4)`:

```text
window
=
F(1)+F(2)+F(3)
=
1+2+4
=
7
```

So:

```text
F(4)=window=7
```

Move to the next day:

```text
old window:
F(1)+F(2)+F(3)

remove F(1)
add    F(4)

new window:
F(2)+F(3)+F(4)
=2+4+7
=13
```

Therefore:

```text
F(5)=13
```

ASCII:

```text
Day:       1    2    3        4
Value:     1    2    4        7
           \____|____/
              window
             1+2+4
               =7
                |
                v
              F(4)

slide:

Day:            2    3    4        5
Value:          2    4    7       13
                \____|____/
                  2+4+7
                    =13
```

For general window size `k`:

```text
F(i)=F(i-1)+...+F(i-k)
```

Naive:

```text
O(k) per state
O(nk) total
```

Rolling window:

```text
F(i)=window
window += F(i)
window -= F(i-k)
```

gives:

```text
O(1) per state
O(n) total
```

**CF recognition:** `transition = sum of a contiguous block of previous DP states` strongly suggests prefix sums or a sliding window.

## 23.17 Recurrences with Modulo

Counting recurrences often grow extremely fast.

Problems therefore ask:

```text
answer mod M
```

If:

```text
F(n)=F(n-1)+F(n-2)
```

compute:

```text
F(n)
=
(F(n-1)+F(n-2)) % M
```

because:

```text
(a+b) mod M
=
((a mod M)+(b mod M)) mod M
```

### Example

Modulo:

```text
M=10
```

Fibonacci tail digits:

```text
F(5)=5
F(6)=8
F(7)=13 -> 3 mod 10
```

Transition using residues still gives the correct final residue.


### Warning

Modulo preserves operations such as addition and multiplication, but division requires special care.

---

### Detailed Real-World Scenario — Digital counter — recurrence under modulo

A system counts the number of valid configurations, but the count becomes enormous. The display stores only the last `3` decimal digits:

```text
M=1000
```

Suppose:

```text
F(n)=F(n-1)+F(n-2)
```

and at some point:

```text
F(n-1)=987
F(n-2)=654
```

The true next sum is:

```text
987+654=1641
```

The display needs:

```text
1641 mod 1000 = 641
```

So store:

```text
F(n)=641
```

Why is this safe for future additions?

```text
(a+b) mod M
=
((a mod M)+(b mod M)) mod M
```

Example:

```text
(1987+2654) mod 1000
=4641 mod 1000
=641

(987+654) mod 1000
=1641 mod 1000
=641
```

ASCII:

```text
huge exact counts
      |
      v
take mod M after each transition
      |
      v
small stored residues
      |
      v
same final answer mod M
```

**CF recognition:** if a counting recurrence grows exponentially and the statement asks “mod 1e9+7,” apply modulo during each addition/multiplication, while being careful with subtraction and division.

## 23.18 Matrix Form of a Recurrence

Fibonacci:

```text
F(n)=F(n-1)+F(n-2)
```

can be represented as:

```text
| F(n)   |   |1 1| |F(n-1)|
| F(n-1) | = |1 0| |F(n-2)|
```

Define:

```text
A = |1 1|
    |1 0|
```

Then:

```text
|F(n)  |
|F(n-1)|
=
A^(n-1)
|F(1)|
|F(0)|
```

### Why useful?

Sequential DP:

```text
O(n)
```

Fast exponentiation:

```text
O(log n)
```

matrix multiplications.


### Recognition trigger

```text
huge n
+
fixed-order linear recurrence
+
constant coefficients
```

strongly suggests matrix exponentiation.

#### Detailed real-world walkthrough — Population tracked for 10^18 months

Suppose a simplified population satisfies:

```text
F(n)=F(n-1)+F(n-2)
```

and the problem asks for:

```text
F(10^18) mod M
```

An O(n) loop would require roughly:

```text
10^18 iterations
```

which is impossible.

Store the two required values as a state vector:

```text
V(n)=|F(n)  |
     |F(n-1)|
```

The same transformation is applied every month:

```text
V(n)
=
|1 1| V(n-1)
|1 0|
```

Therefore:

```text
V(n)=A^(n-1)V(1)
```

Instead of applying `A` one month at a time, binary exponentiation computes the huge power using:

```text
O(log n)
```

matrix multiplications.

For `10^18`:

```text
log2(10^18) ≈ 60
```

So an impossible number of sequential steps becomes only about sixty exponent bits.

**CF mapping:** huge index + fixed linear transition is the signature to consider matrix exponentiation.

---

## 23.19 Matrix Exponentiation Dry Run

For Fibonacci:

```text
A = [1 1]
    [1 0]
```

Compute:

```text
A^2
=
[1 1] [1 1]
[1 0] [1 0]

=
[2 1]
[1 1]
```

Then:

```text
A^3
=
A^2 * A

=
[2 1] [1 1]
[1 1] [1 0]

=
[3 2]
[2 1]
```

The powers encode Fibonacci transitions.

### Binary exponentiation

To compute:

```text
A^13
```

use:

```text
13 = 8+4+1
```

and repeated squaring:

```text
A
A^2
A^4
A^8
```

Combine selected powers.

### Complexity

For fixed `2×2` matrix:

```text
O(log n)
```

### Mathematical lesson

A recurrence is repeated application of a transition. Exponentiation accelerates repeated identical linear transitions.

---

### Detailed Real-World Scenario — Repeated monthly transformation — matrix power dry run

Suppose a two-value business state evolves as:

```text
newA = oldA + oldB
newB = oldA
```

Write:

```text
V(n)=|A(n)|
     |B(n)|
```

Then:

```text
V(n)
=
|1 1| V(n-1)
|1 0|
```

Start:

```text
V(1)=|1|
     |0|
```

One transformation:

```text
V(2)
=
|1 1| |1|
|1 0| |0|

=
|1|
|1|
```

Again:

```text
V(3)
=
|1 1| |1|
|1 0| |1|

=
|2|
|1|
```

Instead of applying the transformation `13` times one by one, use:

```text
13 = 8+4+1
```

Compute powers by squaring:

```text
A
A²
A⁴
A⁸
```

and combine:

```text
A^13=A^8*A^4*A
```

**CF recognition:** when the recurrence transition is linear and identical at every step, represent one step as a matrix and exponentiate the transformation.

## 23.20 Characteristic Equation Intuition

For a linear recurrence such as:

```text
F(n)=aF(n-1)+bF(n-2)
```

try a solution shape:

```text
F(n)=r^n
```

Substitute:

```text
r^n
=
a r^(n-1)+b r^(n-2)
```

Divide by:

```text
r^(n-2)
```

to obtain:

```text
r^2 = ar+b
```

or:

```text
r^2-ar-b=0
```

This is the characteristic equation.

### Fibonacci

```text
F(n)=F(n-1)+F(n-2)
```

gives:

```text
r^2-r-1=0
```

### Why learn this for CP?

You usually will not use floating-point closed forms for exact modular answers, but this viewpoint explains:

```text
growth rate
linear recurrence structure
why matrix methods work
```

### Real-world analogy

Repeated proportional growth often leads naturally to exponential forms such as `r^n`.

---

### Detailed Real-World Scenario — Population growth — why try r^n

Suppose population satisfies:

```text
F(n)=F(n-1)+F(n-2)
```

You want to understand how fast it grows.

Assume a pure exponential-shaped solution:

```text
F(n)=r^n
```

Substitute:

```text
r^n=r^(n-1)+r^(n-2)
```

Divide by:

```text
r^(n-2)
```

to get:

```text
r²=r+1
```

or:

```text
r²-r-1=0
```

Roots:

```text
r=(1±sqrt(5))/2
```

The larger root is approximately:

```text
1.618
```

which explains why Fibonacci values grow roughly like:

```text
1.618^n
```

Concrete comparison:

```text
F(10)=55
1.618^10 ≈ 123
```

The exact closed form includes scaling and the second root, but the dominant root explains asymptotic growth.

**CF mapping:** characteristic equations are mainly useful for understanding linear recurrences and growth; for exact modular contest answers, matrix or specialized recurrence methods are usually safer.

## 23.21 Divide-and-Conquer Recurrences

Recurrences also describe algorithm running time.

Classic form:

```text
T(n)=aT(n/b)+f(n)
```

Example merge sort:

```text
T(n)=2T(n/2)+O(n)
```

Interpretation:

```text
2 subproblems
each size n/2
+
linear merge work
```

### Recursion tree

```text
             n
          /     \
        n/2     n/2
       /  \     /  \
     n/4 n/4  n/4 n/4
```

Each level has total merge work:

```text
O(n)
```

Number of levels:

```text
O(log n)
```

Therefore:

```text
T(n)=O(n log n)
```


---

### Detailed Real-World Scenario — Sorting 16 customer records

A company needs to sort `16` customer records. The algorithm divides the records into two equal groups, sorts both groups, and merges them.

Define:

```text
T(n)=work required to sort n records
```

For `16`:

```text
T(16)=2T(8)+16
```

because:

```text
two size-8 recursive sorts
+
16 units of merge work
```

Expand:

```text
T(8)=2T(4)+8
T(4)=2T(2)+4
T(2)=2T(1)+2
```

Recursion tree:

```text
                 16                    level work = 16
              /      \
             8        8                 level work = 16
           /  \      /  \
          4    4    4    4              level work = 16
         / \  / \  / \  / \
        2  2 2  2 2  2 2  2             level work = 16
```

Each level contributes `16` merge work, and there are about:

```text
log2(16)=4
```

levels.

Therefore:

```text
T(n)=O(n log n)
```

**CF recognition:** recurrence modeling also applies to algorithm complexity. Here `T(n)` means work, not number of solutions.

## 23.22 Master-Theorem Recognition

For:

```text
T(n)=aT(n/b)+f(n)
```

compare:

```text
f(n)
```

with:

```text
n^(log_b a)
```

### Typical intuition

```text
recursive leaves dominate
balanced across levels
root/combine work dominates
```

### Examples

Binary search:

```text
T(n)=T(n/2)+O(1)
=> O(log n)
```

Merge sort:

```text
T(n)=2T(n/2)+O(n)
=> O(n log n)
```

Simple divide-and-conquer with:

```text
T(n)=2T(n/2)+O(1)
```

has:

```text
O(n)
```

total work.


### CF use

You usually need the **recognition**, not a formal theorem proof during a contest.

---

### Detailed Real-World Scenario — Company work splitting — recurrence complexity

A company receives a task of size `16`. It splits each task into two equal subtasks until size `1`.

Suppose combining results at a node costs work proportional to the current size.

Then:

```text
T(16)=2T(8)+16
```

Next level:

```text
2T(8)
=2[2T(4)+8]
=4T(4)+16
```

Per-level work:

```text
level 0: 1 task  ×16 work =16
level 1: 2 tasks × 8 work =16
level 2: 4 tasks × 4 work =16
level 3: 8 tasks × 2 work =16
```

Number of levels:

```text
log2(16)=4
```

Total combining work is approximately:

```text
16 * 4
```

giving:

```text
O(n log n)
```

Contrast binary search:

```text
T(n)=T(n/2)+1
```

Only one subproblem survives at each level:

```text
16 -> 8 -> 4 -> 2 -> 1
```

so:

```text
O(log n)
```

**CF recognition:** identify `a`, `b`, and combine work in `T(n)=aT(n/b)+f(n)` before applying a memorized complexity result.

## 23.23 Recurrence Unrolling

Unrolling repeatedly substitutes the recurrence into itself.

### Example

```text
F(n)=F(n-1)+n
```

Expand:

```text
F(n)
=F(n-1)+n

=F(n-2)+(n-1)+n

=F(n-3)+(n-2)+(n-1)+n

...
=F(0)+1+2+...+n
```

If:

```text
F(0)=0
```

then:

```text
F(n)=n(n+1)/2
```


### Why useful?

Unrolling can reveal:

```text
closed form
arithmetic series
geometric series
complexity
hidden prefix sum
```

### Contest question

```text
"What appears after expanding the recurrence 3–4 times?"
```

#### Detailed real-world walkthrough — Daily savings becomes a formula

Suppose:

```text
F(n)=money saved after n days
```

and on day `n` you add `n` lei:

```text
F(n)=F(n-1)+n
F(0)=0
```

Instead of computing all days, expand:

```text
F(n)
=F(n-1)+n

=[F(n-2)+(n-1)] + n

=F(n-2)+(n-1)+n

=F(n-3)+(n-2)+(n-1)+n

...

=F(0)+1+2+...+n
```

Use the arithmetic-series formula:

```text
1+2+...+n
=
n(n+1)/2
```

Therefore:

```text
F(n)=n(n+1)/2
```

For:

```text
n=100
```

you do not need 100 recurrence steps:

```text
F(100)=100*101/2=5050
```

**CF mapping:** expand a simple recurrence three or four levels. If a familiar series appears, the recurrence may collapse to a direct formula.

---

## 23.24 Telescoping Recurrences

Some recurrences collapse after rearranging differences.

Example:

```text
F(n)=F(n-1)+2n
```

Then:

```text
F(n)-F(n-1)=2n
```

Sum for:

```text
1..n
```

Left side telescopes:

```text
[F(1)-F(0)]
+[F(2)-F(1)]
+...
+[F(n)-F(n-1)]

=F(n)-F(0)
```

Right side:

```text
2(1+2+...+n)
=n(n+1)
```

Therefore if:

```text
F(0)=0
```

then:

```text
F(n)=n(n+1)
```


### Connection

Recurrence differences and prefix sums are discrete versions of derivative/integration ideas.

---

### Detailed Real-World Scenario — Electricity meter — telescoping cancellation

A building records a cumulative electricity meter. Suppose the daily increase is:

```text
2n units on day n
```

Let:

```text
F(n)=meter value after day n
F(0)=0
```

Then:

```text
F(n)=F(n-1)+2n
```

Rearrange:

```text
F(n)-F(n-1)=2n
```

Write the first four equations:

```text
F(1)-F(0)=2
F(2)-F(1)=4
F(3)-F(2)=6
F(4)-F(3)=8
```

Add them:

```text
[F(1)-F(0)]
+[F(2)-F(1)]
+[F(3)-F(2)]
+[F(4)-F(3)]
```

Everything in the middle cancels:

```text
-F(1)+F(1)
-F(2)+F(2)
-F(3)+F(3)
```

leaving:

```text
F(4)-F(0)=2+4+6+8=20
```

Since `F(0)=0`:

```text
F(4)=20
```

General:

```text
F(n)=n(n+1)
```

**CF recognition:** when the recurrence naturally gives a difference `F(i)-F(i-1)`, summing over `i` may telescope and eliminate all intermediate states.

## 23.25 Recurrence to Closed Form

Sometimes simulation can be replaced entirely by algebra.

Example:

```text
F(n)=F(n-1)+c
F(0)=A
```

Unroll:

```text
F(n)
=A+c+c+...+c
```

There are `n` copies:

```text
F(n)=A+nc
```

### Geometric recurrence

```text
F(n)=rF(n-1)
F(0)=A
```

gives:

```text
F(n)=A*r^n
```


### Contest value

For `n` up to `10^18`, an O(n) recurrence may be impossible, while a derived formula can be O(1) or O(log n).

---

### Detailed Real-World Scenario — Salary plan — recurrence becomes O(1) formula

An employee starts with a monthly allowance of:

```text
A=3000 lei
```

and receives a fixed increase:

```text
c=200 lei
```

each year.

Define:

```text
F(n)=allowance after n yearly increases
```

Recurrence:

```text
F(n)=F(n-1)+200
F(0)=3000
```

Dry run:

```text
F(1)=3200
F(2)=3400
F(3)=3600
```

Unroll:

```text
F(n)
=F(n-1)+200
=F(n-2)+2*200
...
=F(0)+n*200
```

Therefore:

```text
F(n)=3000+200n
```

For:

```text
n=1,000,000
```

there is no need for one million iterations:

```text
F(n)=3000+200*1,000,000
```

**CF recognition:** constant additive recurrence -> arithmetic closed form; constant multiplicative recurrence -> geometric/power form.

## 23.26 Recurrences Hidden in Counting

Many counting problems are recurrence problems even when no recurrence is stated.

### Example — binary strings without consecutive ones

Let:

```text
F(n)=valid binary strings of length n
```

Classify by ending character.

Ends in `0`:

```text
any valid length n-1 prefix
=> F(n-1)
```

Ends in `1`:

Previous character must be `0`, so append `01` to any valid length `n-2` string:

```text
=> F(n-2)
```

Therefore:

```text
F(n)=F(n-1)+F(n-2)
```

Base:

```text
F(0)=1
F(1)=2
```


### Recognition trigger

Constraints on neighboring symbols often create a small finite set of ending-state recurrences.

#### Detailed real-world walkthrough — Work/rest schedules

A worker plans `n` days.

Represent:

```text
0 = rest
1 = work
```

Rule:

```text
cannot work on two consecutive days
```

Let:

```text
F(n)=number of valid schedules of length n
```

Classify schedules by the final day.

### Case A — final day is rest `0`

Any valid length `n-1` schedule may precede it:

```text
F(n-1)
```

### Case B — final day is work `1`

The previous day must be rest:

```text
...01
```

Remove the final `01`; what remains is any valid schedule of length `n-2`:

```text
F(n-2)
```

Therefore:

```text
F(n)=F(n-1)+F(n-2)
```

For `n=3`, valid schedules are:

```text
000
001
010
100
101
```

So:

```text
F(3)=5
```

**CF mapping:** local adjacency restrictions often become recurrences by classifying strings according to their ending pattern.

---

## 23.27 State Expansion for Recurrences

Sometimes one scalar state is insufficient.

Example: count binary strings where no two `1`s are adjacent.

Define:

```text
Z(n)=valid length-n strings ending in 0
O(n)=valid length-n strings ending in 1
```

Transitions:

```text
Z(n)=Z(n-1)+O(n-1)
O(n)=Z(n-1)
```

Total:

```text
F(n)=Z(n)+O(n)
```

### State diagram

```text
        append 0
   ┌──────────────┐
   v              |
 [0-state] ----> [1-state]
      append 1

[1-state] --append 0--> [0-state]
```

No:

```text
[1-state] --append 1-->
```


### Modeling lesson

If the future depends on information not contained in `n`, add that information to the state.

#### Detailed real-world walkthrough — Machine cannot run high power twice

A machine operates once per hour in:

```text
0 = normal mode
1 = high-power mode
```

Safety rule:

```text
high-power cannot occur in consecutive hours
```

Knowing only:

```text
number of hours processed
```

is not enough to decide whether high-power is legal next. We also need the previous mode.

Define:

```text
Z(n)=valid n-hour schedules ending in normal mode
O(n)=valid n-hour schedules ending in high-power mode
```

Transitions:

```text
To end in 0:
previous may be 0 or 1

Z(n)=Z(n-1)+O(n-1)
```

To end in `1`, previous must be `0`:

```text
O(n)=Z(n-1)
```

State machine:

```text
        append 0
     ┌────────────┐
     ↓            |
   [0] --------> [1]
      append 1

   [1] --0--> [0]
   [1] --1--> FORBIDDEN
```

**CF mapping:** when two histories with the same position have different future options, the state is missing information. Add exactly the information needed to distinguish their futures.

---

## 23.28 Recurrence vs Dynamic Programming

A recurrence is the **mathematical relationship**.

Dynamic programming is one way to **evaluate** it efficiently.

Example recurrence:

```text
F(n)=F(n-1)+F(n-2)
```

Possible evaluation methods:

```text
naive recursion
memoization
bottom-up DP
O(1)-space iteration
matrix exponentiation
closed form
```

Same recurrence.

Different computation.

### Real-world analogy

A recipe describes dependency:

```text
today's stock = yesterday's stock + delivery - sales
```

You can calculate it recursively, iteratively, or derive a formula under special assumptions.

### Important distinction

Do not say:

```text
"recurrence = DP"
```

Instead:

```text
recurrence = model
DP = evaluation strategy
```

#### Detailed real-world walkthrough — Same recurrence, different computation

Suppose monthly users follow:

```text
F(n)=F(n-1)+F(n-2)
```

The mathematical business rule does not change with the input size.

For:

```text
n=30
```

you might simply iterate:

```text
O(n)
```

For repeated queries up to `10^6`, precompute a DP table.

For:

```text
n=10^18
```

the same recurrence may require matrix exponentiation:

```text
O(log n)
```

So:

```text
MODEL:
F(n)=F(n-1)+F(n-2)

EVALUATORS:
recursive
memoized
bottom-up
rolling variables
matrix power
```

**CF mapping:** separating model from evaluation prevents the common mistake of rejecting a correct recurrence merely because its first implementation is too slow.

---

## 23.29 When a Recurrence Is the Wrong Model

Not every repeated-looking process needs a recurrence.

### Case 1 — direct formula exists

```text
1+2+...+n
```

Using:

```text
n(n+1)/2
```

is simpler than computing every prefix.

### Case 2 — state is missing information

If:

```text
F(n)
```

cannot determine legal future choices, the recurrence is invalid.

You may need:

```text
F(n,last)
F(i,j)
F(mask)
```

### Case 3 — subproblems are not independent enough

A recurrence that double-counts overlapping cases is wrong mathematically even if code runs.

### Case 4 — huge n

An O(n) recurrence for:

```text
n=10^18
```

is not computationally usable.

Look for:

```text
closed form
matrix exponentiation
periodicity
```


---

### Detailed Real-World Scenario — Electricity forecast needs more than the day number

Suppose tomorrow's electricity usage depends on:

```text
current usage
AND
whether today is a holiday
```

Imagine two states at day `20`:

```text
State A:
day=20
holiday=0

State B:
day=20
holiday=1
```

Suppose transitions are:

```text
normal day:
tomorrow = current + 10

holiday:
tomorrow = current - 20
```

If we define only:

```text
F(day)
```

both states collapse into `F(20)`, even though their next states differ.

Visual:

```text
(day 20, normal)
       |
      +10
       v
different future

(day 20, holiday)
       |
      -20
       v
different future
```

So the state must retain the information that affects the future:

```text
F(day, holiday)
```

or, in a richer model:

```text
F(day, mode)
```

**CF recognition:** two histories may be merged into one DP/recurrence state only when they have identical future possibilities and contribution behavior.

## 23.30 60-Second Recurrence Discovery Workflow

```text
PROBLEM
   |
   v
What does F(state) mean?
   |
   v
Can I classify by FIRST/LAST decision?
   |
   v
Which smaller states can lead here?
   |
   v
Choose operator
   |
   +-------------------------------+
   |        |         |            |
 count     min       max       possible?
   |        |         |            |
   +        min       max          OR
   +-------------------------------+
                    |
                    v
             write recurrence
                    |
                    v
             define base cases
                    |
                    v
       check tiny states manually
                    |
                    v
How large are state and transition?
   |
   +--------------------------------------+
   |          |          |                |
 small n   repeated    range sum       huge n
   |          |          |                |
 iterate    memo/DP   prefix/window   matrix/formula
```

### Fast contest questions

```text
1. What exactly does F(state) mean?
2. Can I derive it from the first or last action?
3. Are the cases disjoint and complete?
4. Is the operator sum, min, max, or OR?
5. What are the base cases?
6. What happens at state 0?
7. Do multiple paths reach the same state?
8. Does the transition scan a range?
9. Can prefix sums optimize it?
10. Does only the last k states matter?
11. Can I compress memory?
12. Is n too large for O(n)?
13. Is the recurrence linear with constant coefficients?
14. Can I unroll it into a formula?
```

---

### Detailed Real-World Scenario — Contest decoding — derive before optimizing

Suppose a problem says:

> You are at position `0`. In one move you may advance `1`, `2`, or `3` cells. How many ordered ways reach cell `n`?

Run the recurrence checklist.

**1. State**

```text
F(i)=number of ways to reach cell i
```

**2. Last action**

```text
i-1 -> i
i-2 -> i
i-3 -> i
```

**3. Transition**

```text
F(i)=F(i-1)+F(i-2)+F(i-3)
```

**4. Base**

```text
F(0)=1
F(i<0)=0
```

**5. Tiny dry run**

```text
F(1)=1
F(2)=2
F(3)=4
F(4)=7
```

**6. Constraints**

If:

```text
n<=10^6
```

simple O(n) iteration is fine.

If the transition instead summed the previous `k` states and:

```text
n,k<=2*10^5
```

optimize the transition with prefix/sliding window.

If:

```text
n<=10^18
```

and the recurrence has fixed coefficients, consider matrix exponentiation.

**CF habit:** derive correctness first, then let constraints choose the evaluator.

## 23.31 Codeforces Recognition Map

| Statement clue | Recurrence model |
|---|---|
| number of ways | additive recurrence |
| first/last action has several cases | sum predecessor states |
| independent choices at a step | multiplication |
| minimum cost | `min` recurrence |
| maximum reward | `max` recurrence |
| is target reachable? | boolean OR recurrence |
| steps of sizes `1,2` | Fibonacci-type |
| fixed previous `k` states | order-`k` recurrence |
| same state reached repeatedly | memoization/DP |
| only last few states needed | space compression |
| sum of previous range | prefix/sliding-window optimization |
| answer modulo `M` | modular recurrence |
| huge `n`, constant coefficients | matrix exponentiation |
| repeated divide into subproblems | complexity recurrence |
| `T(n)=aT(n/b)+f(n)` | Master-theorem recognition |
| recurrence adds predictable term | unroll/telescope |
| recurrence multiplies by constant | geometric closed form |
| neighboring-symbol restriction | ending-state recurrence |
| future depends on last mode/value | expand state |
| `n≈10^18` | formula/matrix/periodicity, not O(n) |

---

### Detailed Real-World Scenario — Statement clue to recurrence — worked recognition example

Suppose the statement says:

> Build a length-`n` binary string with no adjacent `1`s.

Translate clue by clue:

```text
"number of strings"
        ↓
counting recurrence -> likely addition

"no adjacent 1s"
        ↓
future depends on ending symbol

"length n"
        ↓
smaller state length n-1 / n-2
```

Define:

```text
Z(n)=valid strings ending 0
O(n)=valid strings ending 1
```

Then:

```text
Z(n)=Z(n-1)+O(n-1)
O(n)=Z(n-1)
```

Or eliminate the extra state:

```text
F(n)=F(n-1)+F(n-2)
```

This is how the recognition table should be used: not as memorization, but as a map from wording to mathematical dependency.

## 23.32 Common Mistakes

### 1. Writing a transition before defining the state

Always write:

```text
F(n) = ...
```

in plain English first.

### 2. Missing base cases

A correct transition with wrong bases produces a wrong sequence.

### 3. Double-counting cases

When adding:

```text
case A + case B
```

prove the cases are disjoint.

### 4. Forgetting a case

Also prove the cases cover every valid solution.

### 5. Using sum when the problem asks minimum

Match the operator to the objective:

```text
count -> +
min   -> min
max   -> max
exist -> OR
```

### 6. Naive recursive implementation

A mathematically correct recurrence can still produce exponential code.

### 7. Storing O(n) when O(1) is enough

Check dependency width.

### 8. O(nk) range transitions

Look for prefix sums or a sliding window.

### 9. Overflow

Counting recurrences grow rapidly. Use suitable types and requested modulo.

### 10. Applying modulo to division blindly

Division under modulo needs an inverse when valid.

### 11. Using matrix exponentiation unnecessarily

For `n<=10^6`, ordinary iteration may be simpler and safer.

### 12. Treating an observed sequence as proof

Derive the recurrence from the problem structure.

### 13. Missing state information

If two situations with the same `n` have different futures, `F(n)` is not a sufficient state.

### 14. Confusing recurrence with implementation

The recurrence is the model; recursion is only one coding technique.

---

### Detailed Real-World Scenario — Debugging a wrong recurrence — delivery example

Suppose deliveries can use jumps `1` or `2`, and you write:

```text
F(n)=2*F(n-1)
```

Test tiny state `n=2`.

Actual routes:

```text
1+1
2
```

So:

```text
F(2)=2
```

Your recurrence with `F(1)=1` also gives `2`, so one test is not enough.

Now test `n=3`.

Actual routes:

```text
1+1+1
1+2
2+1
```

Thus:

```text
F(3)=3
```

Wrong recurrence:

```text
2*F(2)=4
```

Counterexample found.

Correct classification by final move:

```text
last 1 -> F(2)
last 2 -> F(1)

F(3)=F(2)+F(1)=2+1=3
```

**CF debugging habit:** after deriving a recurrence, manually enumerate very small inputs. This catches missing cases, overlapping cases, bad bases, and incorrect operators before implementation.

## 23.33 Fast Revision Card

```text
========================================================
PART 23 — RECURRENCES
========================================================

CORE IDEA

current state
depends on
smaller / previous states

F(n) = expression of smaller F(...)

--------------------------------------------

EVERY RECURRENCE NEEDS

1. STATE
2. TRANSITION
3. BASE CASE

--------------------------------------------

DISCOVERY

Ask:

"What can the FIRST action be?"

or:

"What can the LAST action be?"

--------------------------------------------

OPERATOR

count ways:
F = sum(previous)

minimum:
F = min(previous) + cost

maximum:
F = max(previous) + reward

possible:
F = OR(previous)

--------------------------------------------

FIBONACCI SHAPE

F(n)=F(n-1)+F(n-2)

appears when every solution belongs to
exactly one of two predecessor cases

--------------------------------------------

LINEAR RECURRENCE

F(n)
=
c1F(n-1)
+c2F(n-2)
+...
+ckF(n-k)

--------------------------------------------

EVALUATION OPTIONS

naive recursion
      ↓
memoization
      ↓
bottom-up DP
      ↓
space compression
      ↓
prefix/window optimization
      ↓
matrix exponentiation / formula

--------------------------------------------

RANGE TRANSITION

F(n)
=
F(n-1)+...+F(n-k)

naive:
O(k) per state

prefix/window:
O(1) per state

--------------------------------------------

HUGE n

fixed linear recurrence
+
constant coefficients

consider:

matrix exponentiation
O(log n)

--------------------------------------------

UNROLLING

F(n)=F(n-1)+n

=F(0)+1+2+...+n

=n(n+1)/2

--------------------------------------------

STATE EXPANSION

If F(n) does not contain enough information:

F(n,last)
F(i,j)
F(mask)
...

--------------------------------------------

RECURRENCE != RECURSIVE CODE

recurrence:
mathematical dependency

DP / iteration / matrix:
ways to evaluate it

--------------------------------------------

CORE CONTEST QUESTION

"What smaller state completely describes
everything I need to build the current answer?"

Then:

STATE
  ↓
FIRST/LAST decision
  ↓
TRANSITION
  ↓
BASE CASE
  ↓
OPTIMIZE EVALUATION
========================================================
```

### Detailed Real-World Scenario — One complete recurrence modeling example — from story to solution

A vending machine can dispense a drink after receiving tokens worth `1`, `2`, or `3` units. Order matters because tokens are inserted sequentially. How many token sequences total exactly `5` units?

**Step 1 — state**

```text
F(n)=number of ordered token sequences totaling n
```

**Step 2 — final token**

```text
final token 1 -> prefix totals n-1
final token 2 -> prefix totals n-2
final token 3 -> prefix totals n-3
```

**Step 3 — recurrence**

```text
F(n)=F(n-1)+F(n-2)+F(n-3)
```

**Step 4 — bases**

```text
F(0)=1
F(n<0)=0
```

**Step 5 — dry run**

```text
F(1)=1
F(2)=2
F(3)=4
F(4)=7
F(5)=13
```

**Step 6 — visualization**

```text
                    F(5)
              /       |                 last1     last2     last3
            |          |         |
           F(4)       F(3)      F(2)
            7          4         2
              \        |        /
               \       |       /
                 7+4+2
                   =
                  13
```

**Step 7 — complexity**

Direct transition has only three terms:

```text
O(n)
```

and because only the last three states are required:

```text
O(1) auxiliary memory
```

This is the full mental pipeline to practice:

```text
story
 -> state
 -> first/last decomposition
 -> recurrence
 -> bases
 -> tiny dry run
 -> proof
 -> complexity optimization
```

