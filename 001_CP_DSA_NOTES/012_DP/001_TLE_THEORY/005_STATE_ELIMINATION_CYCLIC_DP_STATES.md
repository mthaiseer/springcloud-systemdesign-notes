# 006 — State Elimination & Cyclic DP States
## Continuation of Dynamic Programming Intermediate Notes

> **Self-study goal:** Learn two DP ideas that appear after basic state construction:
>
> 1. **State Elimination** — remove a DP parameter when it can be computed from the remaining parameters.
> 2. **Cyclic DP States** — handle a recurrence where the current state appears on the right-hand side and creates a cycle.
>
> Follow the same DP construction flow:
>
> **Observation → Story Removed → State → Dependency → Transition → Base Case → Final Subproblem → Complexity → Optimization → Dry Run → C++17**

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Big Picture](#2-big-picture)
3. [Part A — State Elimination](#3-part-a--state-elimination)
4. [Example — Array of 3s, 5s and ?s](#4-example--array-of-3s-5s-and-s)
5. [Naive 3D DP](#5-naive-3d-dp)
6. [Eliminating One State](#6-eliminating-one-state)
7. [Trade-off in State Elimination](#7-trade-off-in-state-elimination)
8. [Part B — Cyclic DP States](#8-part-b--cyclic-dp-states)
9. [Prerequisite — Expected Value](#9-prerequisite--expected-value)
10. [Warm-up — Toss Until First Head](#10-warm-up--toss-until-first-head)
11. [Main Cyclic DP Example](#11-main-cyclic-dp-example)
12. [How to Remove the Self Dependency](#12-how-to-remove-the-self-dependency)
13. [C++17 Pattern](#13-c17-pattern)
14. [Second Cyclic-State Illustration](#14-second-cyclic-state-illustration)
15. [Recognition Checklist](#15-recognition-checklist)
16. [Final Mental Model](#16-final-mental-model)

---

# 1. Prerequisites

Before studying state elimination and cyclic states, make sure these ideas are comfortable.

---

## 1.1 A DP state is a set of independent information

A state parameter should tell us something that cannot already be recovered from the other parameters.

For example:

```text
dp[i][j][k]
```

looks like a 3-dimensional DP.

But suppose:

```text
i = j + k
```

Then once `i` and `j` are known:

```text
k = i - j
```

So `k` is not independent.

That means storing it separately may be unnecessary.

### Mental rule

```text
If one parameter can be calculated
from the other state parameters,
it may be removable.
```

---

## 1.2 Dimension vs number of states

Suppose:

```text
dp[i][j][k]
```

with ranges:

```text
i → O(n)
j → O(n)
k → O(n)
```

Then the number of states can be:

```text
O(n³)
```

If one parameter is eliminated:

```text
dp[i][j]
```

the state count may drop to:

```text
O(n²)
```

This can improve both:

```text
time
memory
```

---

## 1.3 Memoization

For top-down DP:

```cpp
if (memo[state] is already computed)
    return memo[state];
```

State elimination is especially important because memoization stores one answer per state.

Fewer independent state parameters usually means fewer memoized states.

---

## 1.4 Counting processed items

A very useful invariant is:

```text
processed_count
=
count_type_A
+
count_type_B
```

Example:

If the first `i` positions have already been assigned either `3` or `5`, then:

```text
number_of_3s + number_of_5s = i
```

Therefore:

```text
number_of_5s = i - number_of_3s
```

This exact observation is the core of the lecture's state-elimination example.

---

## 1.5 Basic probability

For equally likely events:

```text
Expected value
=
sum(
    probability of outcome
    × value after that outcome
)
```

Example:

```text
two equally likely branches
```

means:

```text
E
=
1/2 × branch1
+
1/2 × branch2
```

---

## 1.6 Expected number of steps

If one transition itself consumes one move, then recurrence often looks like:

```text
E[state]
=
1
+
expected future cost
```

The `1` represents:

```text
the current move
```

---

## 1.7 Algebraic equation solving

Cyclic DP often gives:

```text
E = something + pE
```

The solution is not recursion.

Move the self-dependent term to the left:

```text
E - pE = something
```

Factor:

```text
(1-p)E = something
```

Then:

```text
E = something / (1-p)
```

This algebraic step is the key idea behind cyclic DP states.

---

## 1.8 Geometric series intuition

The lecture ends with the familiar series:

```text
1 + 1/2 + 1/4 + 1/8 + ...
```

whose sum is:

```text
2
```

This is useful intuition for repeated-probability processes.

A process may fail and return to the same state many times, but the expected number of attempts can still be finite.

---

# 2. Big Picture

The lecture contains two different optimization ideas.

```text
DP INTERMEDIATE
│
├── STATE ELIMINATION
│   │
│   ├── identify all state parameters
│   ├── find dependency between parameters
│   └── remove redundant parameter
│
└── CYCLIC DP STATES
    │
    ├── current state depends on itself
    ├── ordinary recursion creates a cycle
    └── use mathematics to eliminate self-dependency
```

The common theme is:

```text
Do not blindly store / recurse on every expression.

First understand the mathematical dependency.
```

---

# 3. Part A — State Elimination

The lecture's rule is:

> Ask whether every parameter in the DP state is really necessary.

Suppose:

```text
dp[a][b][c]
```

and we know:

```text
a + b = c
```

Then:

```text
c = a + b
```

So we can compute `c` whenever needed.

There is no need to store it as a separate DP dimension.

---

## 3.1 What matters?

The important information is:

```text
independent state variables
```

Not:

```text
every variable that appears in the problem
```

This is an important distinction.

---

## 3.2 State-elimination checklist

Whenever your DP becomes:

```text
dp[a][b][c]
```

ask:

```text
1. Are a, b and c independent?

2. Is there an equation connecting them?

3. Can one be computed using the others?

4. If yes, which one should be removed?

5. What are the ranges of each parameter?
```

---

# 4. Example — Array of 3s, 5s and ?s

The lecture gives this problem.

We have an array/string of length:

```text
N
```

Every position contains one of:

```text
3
5
?
```

Every `?` must be replaced by:

```text
3
or
5
```

There are upper limits:

```text
at most k1 threes
at most k2 fives
```

We want:

```text
maximum possible score
```

The lecture associates score with choosing `3` or `5`.

---

## 4.1 Story removed

At each index:

```text
current character = 3
```

then we must place `3`.

If:

```text
current character = 5
```

then we must place `5`.

If:

```text
current character = ?
```

we may choose either:

```text
3
or
5
```

subject to the limits.

---

## 4.2 Information we seem to need

At first, it looks like we need:

```text
index

number of 3s already placed

number of 5s already placed
```

So the naive state is:

```text
dp[index][threes][fives]
```

---

# 5. Naive 3D DP

## 5.1 STATE

Define:

```text
dp[index][threes][fives]
=
maximum score obtainable
from positions [index ... n-1]

when we have already placed

threes copies of 3
and
fives copies of 5
```

---

## 5.2 Base case

When:

```text
index == n
```

all positions have been processed.

So:

```text
dp[n][threes][fives] = 0
```

provided the counts have remained within the allowed limits.

---

## 5.3 Fixed character `3`

If:

```text
arr[index] == '3'
```

we have no choice.

Move to:

```text
index + 1
threes + 1
fives
```

and add the score for placing `3`.

Conceptually:

```text
answer
=
score3[index]
+
dp[index+1][threes+1][fives]
```

---

## 5.4 Fixed character `5`

If:

```text
arr[index] == '5'
```

then:

```text
answer
=
score5[index]
+
dp[index+1][threes][fives+1]
```

---

## 5.5 Character `?`

If:

```text
arr[index] == '?'
```

there are two branches.

### Put `3`

Allowed only if:

```text
threes < k1
```

Candidate:

```text
score3[index]
+
dp[index+1][threes+1][fives]
```

### Put `5`

Allowed only if:

```text
fives < k2
```

Candidate:

```text
score5[index]
+
dp[index+1][threes][fives+1]
```

Take:

```text
maximum of valid choices
```

---

## 5.6 Why this is expensive

The state looks like:

```text
index × threes × fives
```

Possible state count:

```text
O(n × k1 × k2)
```

This may be much larger than necessary.

Now look for dependency between the parameters.

---

# 6. Eliminating One State

This is the most important observation in the first half of the lecture.

Before processing index `i`:

```text
exactly i positions
have already been assigned
```

Every processed position is either:

```text
3
or
5
```

Therefore:

```text
threes + fives = index
```

So:

```text
fives = index - threes
```

That means `fives` is completely determined by:

```text
index
threes
```

Therefore it is not an independent state variable.

---

## 6.1 Before elimination

```text
dp[index][threes][fives]
```

---

## 6.2 After elimination

```text
dp[index][threes]
```

Whenever we need the number of fives:

```text
fives = index - threes
```

---

## 6.3 New STATE

```text
dp[index][threes]
=
maximum score obtainable
from index ... n-1

when exactly `threes` number of 3s
have already been placed
```

We do not store `fives`.

We compute:

```text
fives = index - threes
```

---

## 6.4 Why this is valid

Suppose:

```text
index = 7
threes = 3
```

We already processed 7 positions.

Among those 7:

```text
3 are threes
```

Therefore automatically:

```text
fives = 7 - 3 = 4
```

There is no other possibility.

So these two states:

```text
(index=7, threes=3, fives=4)
(index=7, threes=3, fives=5)
```

cannot both exist.

The second one is impossible because:

```text
3 + 5 != 7
```

This is exactly why storing `fives` wastes a DP dimension.

---

# 6.5 Optimized transition

At every state:

```text
(index, threes)
```

first recover:

```text
fives = index - threes
```

Then apply the same transitions.

---

## Case 1 — Current value is `3`

Check:

```text
threes < k1
```

Then:

```text
answer
=
score3[index]
+
dp[index+1][threes+1]
```

---

## Case 2 — Current value is `5`

Compute:

```text
fives = index - threes
```

Check:

```text
fives < k2
```

Then:

```text
answer
=
score5[index]
+
dp[index+1][threes]
```

Notice:

```text
threes does not change
```

because we placed a `5`.

---

## Case 3 — Current value is `?`

Try both.

### Choose 3

If:

```text
threes < k1
```

then:

```text
take3
=
score3[index]
+
dp[index+1][threes+1]
```

### Choose 5

If:

```text
fives < k2
```

then:

```text
take5
=
score5[index]
+
dp[index+1][threes]
```

Finally:

```text
answer = max(take3, take5)
```

among the valid options.

---

# 6.6 Compact dry run

Suppose:

```text
index = 4
threes = 1
```

Then automatically:

```text
fives = 4 - 1 = 3
```

Suppose current character is:

```text
?
```

and limits are:

```text
k1 = 3
k2 = 4
```

### Option 1 — place `3`

Current threes:

```text
1 < 3
```

allowed.

Next state:

```text
dp[5][2]
```

### Option 2 — place `5`

Current fives:

```text
3 < 4
```

allowed.

Next state:

```text
dp[5][1]
```

No third state parameter is required.

---

# 6.7 Complexity improvement

### Naive

```text
states
=
O(n × k1 × k2)
```

### Eliminated

If we store only:

```text
index
threes
```

then:

```text
states
=
O(n × k1)
```

assuming `threes` is the retained count parameter.

This can be a major improvement.

---

# 6.8 C++17 memoization pattern

The lecture shows the optimized idea as a 2D memo.

A clean generic version is:

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long NEG = -(1LL << 60);

int n, k1, k2;
string a;

vector<long long> score3;
vector<long long> score5;

vector<vector<long long>> memo;
vector<vector<char>> seen;

long long solve(int index, int threes) {
    int fives = index - threes;

    if (threes > k1 || fives > k2) {
        return NEG;
    }

    if (index == n) {
        return 0;
    }

    if (seen[index][threes]) {
        return memo[index][threes];
    }

    seen[index][threes] = 1;

    long long answer = NEG;

    if (a[index] == '3') {
        if (threes < k1) {
            answer =
                score3[index]
                + solve(index + 1, threes + 1);
        }
    }
    else if (a[index] == '5') {
        if (fives < k2) {
            answer =
                score5[index]
                + solve(index + 1, threes);
        }
    }
    else {
        if (threes < k1) {
            answer = max(
                answer,
                score3[index]
                + solve(index + 1, threes + 1)
            );
        }

        if (fives < k2) {
            answer = max(
                answer,
                score5[index]
                + solve(index + 1, threes)
            );
        }
    }

    return memo[index][threes] = answer;
}
```

> The exact input/score representation depends on the original problem statement. The important lecture pattern is the elimination:
>
> ```text
> fives = index - threes
> ```

---

# 7. Trade-off in State Elimination

The lecture gives the hypothetical state:

```text
dp[a][b][c]
```

with a relation such as:

```text
a × k1 + b × k2 = c × k3
```

This means one parameter can be computed from the other two.

So one dimension can potentially be removed.

But which one?

---

## 7.1 Look at state ranges

Suppose:

```text
1 <= a <= 1000

1 <= b <= 100

1 <= c <= 10000
```

The lecture's rule of thumb is:

```text
remove the largest state dimension
```

Here:

```text
c
```

has the largest range.

So eliminating `c` gives the biggest state-space reduction.

---

## 7.2 Why?

### Keep all three

Approximate state count:

```text
1000 × 100 × 10000
```

Huge.

### Remove `c`

Remaining dimensions:

```text
1000 × 100
=
100000
```

Much smaller.

---

## 7.3 Recognition rule

When several state variables are mathematically dependent:

```text
1. identify which parameter is derivable

2. compare their ranges

3. prefer eliminating the largest redundant dimension
```

The central principle remains:

```text
store only independent information
```

---

# 8. Part B — Cyclic DP States

Now the lecture moves to a different difficulty.

Suppose:

```text
dp[i]
```

depends on:

```text
dp[i]
```

itself.

Example shape:

```text
dp[i]
=
something
+
probability × dp[i]
```

This creates a cycle.

---

## 8.1 Why normal recursion breaks

Normal DP expects transitions like:

```text
dp[i]
→ smaller / already solvable states
```

Example:

```text
dp[i]
→ dp[i-1]
```

But cyclic DP can look like:

```text
dp[i]
→ dp[i]
```

So if we recursively call it directly:

```text
solve(i)
→ solve(i)
→ solve(i)
→ ...
```

we never finish.

---

## 8.2 Key idea

When the self-dependency is an algebraic expectation equation:

```text
do not recurse through the self-loop
```

Instead:

```text
write the expectation equation
move the self term to the left
solve mathematically
```

This is the lecture's central cyclic-state technique.

---

# 9. Prerequisite — Expected Value

Suppose there are outcomes:

```text
Outcome 1 with probability p1
Outcome 2 with probability p2
...
```

Then:

```text
E
=
p1 × value1
+
p2 × value2
+
...
```

For expected steps, each branch often contains:

```text
1 + futureExpectation
```

because we spend one move now.

---

## 9.1 Example template

Suppose:

```text
with probability p:
    go to state A

with probability q:
    go to state B
```

where:

```text
p + q = 1
```

Then:

```text
E[current]
=
p × (1 + E[A])
+
q × (1 + E[B])
```

---

# 10. Warm-up — Toss Until First Head

The lecture first demonstrates the self-loop with a coin.

We repeatedly toss a fair coin.

Stop when:

```text
Head
```

If:

```text
Tail
```

we are back in exactly the same situation.

Goal:

```text
expected number of tosses
until the first Head
```

---

## 10.1 Define state

Let:

```text
E
=
expected remaining number of tosses
to get the first Head
```

---

## 10.2 Branches

### Head

Probability:

```text
1/2
```

We use one toss and stop.

Cost:

```text
1 + 0
```

---

### Tail

Probability:

```text
1/2
```

We use one toss.

Then we return to the same state.

Cost:

```text
1 + E
```

---

## 10.3 Equation

Therefore:

```text
E
=
1/2 × (1 + 0)
+
1/2 × (1 + E)
```

Expand:

```text
E
=
1/2
+
1/2
+
E/2
```

So:

```text
E
=
1
+
E/2
```

Move self-dependent term:

```text
E - E/2 = 1
```

Therefore:

```text
E/2 = 1
```

Finally:

```text
E = 2
```

---

## 10.4 What happened?

The recurrence contained:

```text
E
```

on both sides.

We did not recursively call the same state.

We solved the equation.

That is the cyclic-DP pattern.

---

# 11. Main Cyclic DP Example

The lecture gives:

```text
N <= 1e6
```

At each step, one of three actions happens with equal probability:

```text
1. N becomes floor(N / 2)

2. N becomes N - 1

3. N remains unchanged
```

Goal:

```text
expected number of steps
for N to become 0
```

---

# 11.1 STATE

Define:

```text
dp[n]
=
expected number of moves
needed to convert n to 0
```

---

## 11.2 BASE CASE

If:

```text
n = 0
```

we are already done.

Therefore:

```text
dp[0] = 0
```

---

# 11.3 Write the recurrence carefully

For `n > 0`, one move happens first.

Then with probability:

```text
1/3
```

we go to:

```text
n - 1
```

With probability:

```text
1/3
```

we go to:

```text
floor(n / 2)
```

And with probability:

```text
1/3
```

we remain at:

```text
n
```

Therefore:

```text
dp[n]
=
1
+
1/3 × dp[n-1]
+
1/3 × dp[n/2]
+
1/3 × dp[n]
```

The last term is the problem:

```text
1/3 × dp[n]
```

The state depends on itself.

---

# 12. How to Remove the Self Dependency

Start:

```text
dp[n]
=
1
+
1/3 dp[n-1]
+
1/3 dp[n/2]
+
1/3 dp[n]
```

Move:

```text
1/3 dp[n]
```

to the left:

```text
dp[n] - 1/3 dp[n]
=
1
+
1/3 dp[n-1]
+
1/3 dp[n/2]
```

Combine the left side:

```text
2/3 dp[n]
=
1
+
1/3(
    dp[n-1]
    +
    dp[n/2]
)
```

Multiply both sides by:

```text
3/2
```

Then:

```text
dp[n]
=
3/2
+
1/2(
    dp[n-1]
    +
    dp[n/2]
)
```

This is the crucial transformation.

---

## 12.1 Before algebra

```text
dp[n]
depends on

dp[n-1]
dp[n/2]
dp[n]
```

Cyclic.

---

## 12.2 After algebra

```text
dp[n]
=
1.5
+
0.5 × (
    dp[n-1]
    +
    dp[n/2]
)
```

Now it depends only on smaller states:

```text
n-1
n/2
```

The cycle is gone.

---

# 12.3 Dependency graph

Before:

```text
        +----------+
        |          |
        v          |
      dp[n] -------+
       /  \
      /    \
dp[n-1]  dp[n/2]
```

After algebra:

```text
      dp[n]
       /  \
      /    \
dp[n-1]  dp[n/2]
```

Now ordinary memoization works.

---

# 12.4 Dry run

Use:

```text
dp[0] = 0
```

### n = 1

```text
dp[1]
=
3/2
+
1/2(
    dp[0]
    +
    dp[0]
)
```

So:

```text
dp[1]
=
1.5
```

---

### n = 2

```text
dp[2]
=
1.5
+
0.5(
    dp[1]
    +
    dp[1]
)
```

Therefore:

```text
dp[2]
=
1.5 + dp[1]
=
3.0
```

The exact values are less important than the transformation:

```text
self dependency
→ algebra
→ ordinary DP
```

---

# 13. C++17 Pattern

The lecture uses memoized recursion with `double`.

A clean version:

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<double> dp;

double solve(int n) {
    if (n == 0) {
        return 0.0;
    }

    if (dp[n] >= 0.0) {
        return dp[n];
    }

    return dp[n]
        =
        1.5
        +
        0.5 * (
            solve(n - 1)
            +
            solve(n / 2)
        );
}

int main() {
    int n;
    cin >> n;

    dp.assign(n + 1, -1.0);

    cout << fixed << setprecision(10)
         << solve(n)
         << '\n';
}
```

Complexity:

```text
Each n is computed once.

Time  = O(n)
Space = O(n)
```

for memoization up to the requested `n`.

---

# 13.1 General self-loop formula

Suppose:

```text
E[s]
=
1
+
pSelf × E[s]
+
p1 × E[next1]
+
p2 × E[next2]
+
...
```

Move the self term:

```text
E[s] - pSelf × E[s]
=
1
+
p1 × E[next1]
+
p2 × E[next2]
+
...
```

Factor:

```text
(1 - pSelf) × E[s]
=
1
+
Σ pNext × E[next]
```

Therefore:

```text
E[s]
=
(
    1
    +
    Σ pNext × E[next]
)
/
(1 - pSelf)
```

This is the reusable cyclic-expectation template.

---

# 14. Second Cyclic-State Illustration

The later lecture slides consider expected moves to reach a target index.

The important setup shown is:

```text
E(i)
=
expected moves to reach index 1 from i
```

with:

```text
E(1) = 0
```

A direct transition can depend on neighboring states such as:

```text
E(i-1)
E(i+1)
```

which creates dependency in both directions.

The lecture's point is that such recurrences may not follow the simple one-direction DP order:

```text
left → right
```

or:

```text
right → left
```

Instead, mathematics can be used to transform the recurrence into a form where only already-understood quantities remain.

The handwritten derivation ultimately rewrites later expectations in terms of earlier ones, producing relations of the form:

```text
E(4) = 1 + E(3)

E(3) = constant + E(2)

E(2) = constant + E(1)
```

with:

```text
E(1) = 0
```

The key learning point is not the specific numeric constants.

It is:

```text
When DP dependencies form a cycle,
look for an algebraic relation
that removes the backward/self dependency.
```

---

# 14.1 Why this matters for problems such as Sushi

The lecture explicitly points to:

```text
AtCoder DP Contest — Sushi
```

as an example connected with cyclic DP states / expectation.

The lesson to carry forward is:

```text
Expectation DP may contain transitions
that appear circular.

Do not immediately reject DP.

First write the expectation equation
and inspect whether the cycle
can be eliminated mathematically.
```

---

# 15. Recognition Checklist

Use this during contests.

---

## 15.1 State Elimination

If you create:

```text
dp[a][b][c]
```

ask:

```text
Can one state variable be derived
from the other two?
```

Example:

```text
processed = threes + fives
```

Therefore:

```text
fives = processed - threes
```

So:

```text
dp[processed][threes][fives]
```

becomes:

```text
dp[processed][threes]
```

---

## 15.2 State-space checklist

Before accepting a 3D or 4D DP:

```text
1. Write the meaning of every parameter.

2. Write equations/invariants connecting them.

3. Mark which parameters are independent.

4. Remove redundant parameters.

5. Prefer eliminating a redundant dimension
   with the largest range.
```

---

## 15.3 Cyclic-state checklist

If recurrence contains:

```text
dp[x]
```

on both sides:

```text
dp[x]
=
something
+
p × dp[x]
```

do not recurse directly.

Instead:

```text
1. write complete equation

2. move p × dp[x] to the left

3. factor dp[x]

4. divide

5. obtain recurrence using only other states
```

---

## 15.4 Expectation checklist

For every probabilistic branch:

```text
probability × (
    current cost
    +
    future expectation
)
```

If every transition consumes one move:

```text
current cost = 1
```

---

# 15.5 Contest decoding table

| Recurrence / state smell | Ask yourself | Technique |
|---|---|---|
| `dp[a][b][c]` | Is one parameter determined by others? | State elimination |
| `countA + countB = processed` | Do I need both counts? | Keep one count |
| 3D DP is too large | Which dimension is redundant? | Eliminate dependency |
| `dp[i]` appears inside its own recurrence | Is it a probabilistic self-loop? | Algebra |
| `E = A + pE` | Can I isolate `E`? | `E = A/(1-p)` |
| dependencies point both directions | Can equations remove one direction? | Mathematical transformation |

---

# 16. Final Mental Model

## State Elimination

Think:

```text
STATE PARAMETERS
      |
      v
Which are independent?
      |
      v
Find invariant / equation
      |
      v
Derive redundant parameter
      |
      v
Remove one DP dimension
```

Example:

```text
index = threes + fives

therefore

fives = index - threes

dp[index][threes][fives]
        ↓
dp[index][threes]
```

---

## Cyclic DP

Think:

```text
dp[state]
depends on itself
      |
      v
Do NOT recurse through self-loop
      |
      v
Write expectation equation
      |
      v
Move self term to left
      |
      v
Solve algebraically
      |
      v
ordinary acyclic DP
```

Example:

```text
dp[n]
=
1
+
1/3 dp[n-1]
+
1/3 dp[n/2]
+
1/3 dp[n]
```

becomes:

```text
dp[n]
=
3/2
+
1/2(
    dp[n-1]
    +
    dp[n/2]
)
```

---

# Final Revision Box

```text
STATE ELIMINATION
────────────────────────────────────

If a DP parameter can be calculated
from other parameters:

DO NOT STORE IT.

Store only independent state information.


CYCLIC DP
────────────────────────────────────

If:

E = A + pE

then:

E - pE = A

(1-p)E = A

E = A / (1-p)


CORE CONTEST QUESTIONS
────────────────────────────────────

1. Are all my state parameters independent?

2. Is there an invariant connecting them?

3. Can I remove the largest redundant dimension?

4. Does my recurrence contain the current state itself?

5. Can algebra remove the cycle?
```
