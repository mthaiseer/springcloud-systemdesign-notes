# Greedy Algorithms — Level 3
## Greedy 2 — Step-by-Step Mathematical Proof Edition

> **Goal:** make Greedy 2 proofs as explicit as Greedy 1: every important symbolic step is followed immediately by the same step using actual numbers.
>
> **For every problem:**  
> **what it asks → concept simplified → greedy claim → define `G` and `O` → proof line by line → why each step is valid → actual example after each important equation → visual dry run → C++17 → complexity → recognition**
>
> **Proof rule:** start with a **real numerical example first**. Once the idea is obvious, write the general symbols. This is the same style used in Greedy 1.
>
> **Important:** not every greedy proof needs long algebra. Use the simplest valid proof: **exchange, one inequality, dominance, or future-space preservation**.
>
> Display equations use fenced `math` blocks to avoid Markdown/LaTeX rendering issues.

---

# Clickable Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. Activity Selection](#1-activity-selection)
- [2. Fractional Knapsack](#2-fractional-knapsack)
- [3. Kadane's Algorithm](#3-kadanes-algorithm)
- [4. Job Sequencing](#4-job-sequencing)
- [5. Maximum Perimeter Triangle](#5-maximum-perimeter-triangle)
- [6. Proof Pattern Summary](#6-proof-pattern-summary)
- [7. Recognition Checklist](#7-recognition-checklist)
- [8. Compact Revision Card](#8-compact-revision-card)

---

# 0. Prerequisites

> **Goal of this section:** understand every symbol and greedy-proof tool before starting the problems.
>
> Keep the mental model:
>
> ```text
> real example first
>      ↓
> understand the choice
>      ↓
> write symbols
>      ↓
> prove greedy is safe
> ```

---

## 0.1 What Is a Greedy Choice?

A greedy algorithm makes the **best-looking safe choice now** and never goes back.

Example:

```text
Activity Selection:

Among all currently possible activities,
pick the one that finishes earliest.
```

But:

```text
"looks best"
```

is not enough.

We must prove:

```text
this choice cannot make the final answer worse
```

So:

```text
Greedy
=
Choice
+
Proof
```

Visual:

```text
Current state
    |
    v
Choose candidate G
    |
    v
Can G be proved safe?
   / \
 YES  NO
  |    |
take  do not trust yet
 G
```

---

## 0.2 Constraint vs Objective

Every greedy problem has:

```text
Constraint
→ what must stay valid

Objective
→ what we want to maximize/minimize
```

Example — Job Sequencing:

```text
Constraint:
a job must finish by its deadline

Objective:
maximize total profit
```

Example — Activity Selection:

```text
Constraint:
selected activities cannot overlap

Objective:
maximize number of selected activities
```

This distinction is important because a greedy exchange must preserve the **constraint** while improving or preserving the **objective**.

---

## 0.3 Greedy Answer G vs Other / OPT Answer O

Use:

```text
G = greedy answer / greedy local contribution
O = another valid answer / OPT contribution
```

For maximization:

```math
G\ge O
```

A convenient way to prove it:

```math
G-O\ge0
```

Actual example:

```text
G = 30
O = 20

G-O
= 30-20
= 10
>= 0
```

Therefore:

```text
G >= O
```

For minimization:

```math
G\le O
```

Often prove:

```math
G-O\le0
```

Actual example:

```text
G = 2
O = 7

G-O
= 2-7
= -5
<= 0
```

Therefore:

```text
G <= O
```

---

## 0.4 Exchange Argument

This is the most reusable greedy proof idea.

### Concept Simplified

Suppose an optimal solution chooses:

```text
O
```

but greedy wants:

```text
G
```

Try replacing:

```text
O → G
```

Then check:

```text
1. Is the solution still valid?
2. Is the answer same or better?
```

If both are true:

```text
an optimal solution can also use G
```

Visual:

```text
OPT uses O
    |
    v
Greedy wants G
    |
    v
replace O → G
    |
    v
still valid?
    |
   YES
    |
    v
answer non-worse?
    |
   YES
    |
    v
G is safe
```

### Tiny Example

Suppose:

```text
OPT uses profit 20
Greedy can use profit 30
```

Exchange:

```text
20 → 30
```

Difference:

```text
30-20
= 10
>= 0
```

So for maximization:

```text
greedy is not worse
```

---

## 0.5 How to Prove Any Greedy Choice

Use this checklist:

```text
STEP 1
State the greedy choice clearly.

STEP 2
Assume OPT makes a different choice.

STEP 3
Change only that one local choice.

STEP 4
Check feasibility.

STEP 5
Compare objective:
G vs O

STEP 6
Use:
- subtraction
- inequality
- exchange
- dominance
- future-space argument

STEP 7
Conclude greedy is non-worse.

STEP 8
Repeat the argument if needed.
```

Important:

```text
Do not force algebra everywhere.
```

Sometimes the cleanest proof is:

```text
anything possible after O
is also possible after G
```

That is enough.

---

## 0.6 Algebra Rules Used in These Notes

You only need a few.

### Remove a Minus Bracket

```text
A - (B + C)
= A - B - C
```

Example:

```text
20 - (7 + 3)
= 20 - 7 - 3
= 10
```

---

### Cancel Equal Terms

```text
A + X - A
= X
```

Example:

```text
40 - 20 + 30 - 40
= 30 - 20
= 10
```

---

### Factor a Common Term

```text
ax - ay
= a(x-y)
```

Example:

```text
5×12 - 5×2

= 5(12-2)

= 5×10

= 50
```

---

### Sign Reasoning

```text
positive × positive = positive

negative × negative = positive

positive × negative = negative
```

Example:

```text
(-4)(-2)
= 8
>= 0
```

---

## 0.7 Inequality Rules

### Rule 1 — Transitivity

If:

```text
A >= B
B >= C
```

then:

```text
A >= C
```

Example:

```text
5 >= 5
5 >= 3

therefore:
5 >= 3
```

This is used in Activity Selection.

---

### Rule 2 — Add the Same Value

If:

```text
A <= B
```

then:

```text
A + C <= B + C
```

Example:

```text
7 <= 8
```

Add `7`:

```text
14 <= 15
```

This is used in Maximum Perimeter Triangle.

---

### Rule 3 — Multiply by a Non-Negative Value

If:

```text
A >= B
```

and:

```text
x >= 0
```

then:

```text
xA >= xB
```

Example:

```text
12 >= 2
x = 1

1×12 >= 1×2

12 >= 2
```

This is the core idea behind Fractional Knapsack.

---

## 0.8 Interval / Activity Notation

An activity is written:

```math
[s_i,e_i]
```

where:

```text
s_i = start time
e_i = end / finish time
```

Example:

```text
Activity A = [2,5]

start  = 2
finish = 5
```

Visual:

```text
time ------------------------------------>

        [-------------]
        2             5
      start         finish
```

### Non-Overlapping Condition

Suppose:

```text
A = [2,3]
B = [4,6]
```

For B to come after A:

```math
s_B\ge e_A
```

Actual:

```text
4 >= 3
```

So they are compatible.

Visual:

```text
A:      [---]
B:            [-----]

         3 <= 4
```

Overlapping example:

```text
A = [2,5]
B = [4,6]

4 < 5
```

Visual:

```text
A:      [-------]
B:          [-------]
             overlap
```

---

## 0.9 Finish Time vs Start Time vs Duration

For Activity Selection, do not confuse:

```text
start time
finish time
duration
```

Example:

```text
A = [1,10]

start    = 1
finish   = 10
duration = 9
```

Another:

```text
B = [2,3]

start    = 2
finish   = 3
duration = 1
```

Even though A starts earlier:

```text
1 < 2
```

B finishes much earlier:

```text
3 < 10
```

Greedy uses:

```text
earliest FINISH time
```

because that releases the timeline earlier.

---

## 0.10 Ratio / Density

Fractional Knapsack uses:

```math
\rho_i=\frac{v_i}{w_i}
```

where:

```text
rho_i = density
v_i   = value
w_i   = weight
```

Meaning:

```text
how much value do I get
for 1 unit of weight?
```

Example:

```text
Item A:
value  = 12
weight = 1

density
= 12/1
= 12
```

Item B:

```text
value  = 6
weight = 3

density
= 6/3
= 2
```

Interpretation:

```text
1 unit weight of A gives 12 value
1 unit weight of B gives 2 value
```

Therefore:

```text
A is better per unit of capacity
```

---

## 0.11 Fraction of an Item

Fractional Knapsack allows part of an item.

Example:

```text
item weight = 30
remaining capacity = 20
```

Fraction taken:

```math
\frac{20}{30}
=
\frac23
```

If full item value is:

```text
120
```

value obtained:

```math
120\cdot\frac23
=
80
```

So:

```text
take 20 weight
get 80 value
```

This ability to take fractions is exactly why the density exchange proof works.

---

## 0.12 Running Sum / Prefix Contribution

Kadane uses:

```text
cur = running sum
best = best subarray sum seen so far
```

Example:

```text
array:
[5,-2,6]
```

Running sums:

```text
5
5 + (-2) = 3
3 + 6    = 9
```

Important:

```text
negative ELEMENT
does not mean
negative RUNNING SUM
```

Here:

```text
-2 is negative
but cur = 3 > 0
```

So we keep the prefix.

Reset only when:

```text
cur < 0
```

because a negative total prefix hurts every future continuation.

---

## 0.13 Job Sequencing — Duration / Deadline / Profit

Each job has:

```text
duration = 1
deadline = d
profit   = p
```

### Duration = 1

Means:

```text
the job occupies exactly one time slot
```

Example:

```text
slot 1
slot 2
slot 3
```

A job occupies only one of them.

---

### Deadline = d

Means:

```text
the job must be completed
NO LATER THAN slot d
```

If:

```text
deadline = 3
```

legal slots are:

```text
1, 2, 3
```

Illegal:

```text
4, 5, ...
```

Visual:

```text
slot:      1     2     3     4
           [ ]   [ ]   [ ]   [ ]

deadline 3:
           <----------->
              legal

slot 4:
                       X
```

So remember:

```text
deadline d
=
schedule this 1-unit job
in any slot <= d
```

---

### Profit = p

Means:

```text
if the job is successfully scheduled,
we earn p
```

Example:

```text
Job A:
deadline = 3
profit   = 50
```

If scheduled in slot:

```text
1, 2, or 3
```

we earn:

```text
50
```

If not scheduled:

```text
profit contribution = 0
```

---

## 0.14 Why Job Sequencing Uses the Latest Legal Slot

Suppose:

```text
Job A:
deadline = 3
profit   = 50
```

Free slots:

```text
1, 2, 3
```

A can use all three.

Now another job:

```text
Job B:
deadline = 1
profit   = 40
```

B can use only:

```text
slot 1
```

Bad placement:

```text
slot:   1    2    3

        A
```

Now B cannot run.

Better:

```text
slot:   1    2    3

        B         A
```

Total profit:

```text
40 + 50
= 90
```

So:

```text
flexible job
→ put as late as possible

tight-deadline job
→ preserve early slot
```

Memory:

```text
profit decides WHICH job
deadline decides WHERE
```

---

## 0.15 Free Slot + `upper_bound`

In Job Sequencing, we often need:

```text
largest free slot <= deadline
```

Suppose free slots are:

```text
{1,3,5,7,8}
```

and:

```text
deadline = 6
```

Wanted:

```text
5
```

because:

```text
5 <= 6
```

and it is the largest free legal slot.

In C++:

```cpp
auto it = freeSlots.upper_bound(6);
```

`upper_bound(6)` gives:

```text
first value > 6
```

which is:

```text
7
```

Move one step back:

```cpp
--it;
```

Now:

```text
5
```

Visual:

```text
free slots:

1   3   5   7   8
        ^   ^
        |   |
      answer upper_bound(6)
```

---

## 0.16 Triangle Inequality

For three positive lengths:

```math
a\le b\le c
```

a non-degenerate triangle must satisfy:

```math
a+b>c
```

Example:

```text
3,5,7

3+5
= 8
> 7
```

Valid.

Invalid:

```text
3,5,8

3+5
= 8
```

But we need:

```text
8 > 8
```

which is false.

So:

```text
3,5,8
```

is degenerate, not a valid non-degenerate triangle.

---

## 0.17 Why Only `a+b>c` Is Checked After Sorting

For:

```math
a\le b\le c
```

the largest side is:

```text
c
```

The hardest inequality to satisfy is:

```math
a+b>c
```

The others are automatically true because sides are positive:

```text
a+c > b
b+c > a
```

Example:

```text
a=3
b=5
c=7
```

Check:

```text
3+5 > 7   ✓
3+7 > 5   ✓
5+7 > 3   ✓
```

So after sorting, just test:

```text
a+b>c
```

---

## 0.18 Final Prerequisite Recognition Map

| If you see... | Think... |
|---|---|
| non-overlapping intervals | start / finish / compatibility |
| maximize interval count | earliest finish |
| value + weight + fractions | density = value / weight |
| maximum contiguous sum | running sum / negative prefix |
| unit jobs + deadline + profit | latest legal slot |
| `set.upper_bound(d)` | first slot `> d`, then step back |
| three positive side lengths | triangle inequality |
| greedy vs OPT | exchange argument |
| numerical objective comparison | `G-O` |
| future options / flexibility | structural greedy proof |

---

# 1. Activity Selection

## 1.1 What It Asks

Select the maximum number of non-overlapping activities.

Objective:

```math
\max(\text{selected count})
```

---

## 1.2 Concept Simplified

To leave maximum space for future activities:

```text
choose the compatible activity
that finishes earliest
```

---

## 1.3 Greedy Claim + Proof — Example First

Let:

```text
G = greedy first activity
O = first activity in some optimal schedule
```

Greedy chooses the activity that **finishes earliest**.

### Step 1 — Understand it with real intervals

Suppose:

```text
G = [2,3]
O = [1,5]
```

So:

```text
G finishes at 3
O finishes at 5
```

Greedy finishes earlier:

```text
3 <= 5
```

Now suppose OPT's next activity is:

```text
F = [5,7]
```

Because `F` comes after `O`:

```text
F starts at 5
O ends at 5

5 >= 5
```

Now replace `O` by `G`.

Does `F` still fit?

```text
F starts at 5
G ends at 3

5 >= 3
```

Yes.

So:

```text
OPT:
[1,5] → [5,7]

can become:

[2,3] → [5,7]
```

Same number of activities.

That is the exchange argument.

---

### Step 2 — Write the same idea with symbols

Greedy finishes no later than OPT's first activity:

```math
e_G\le e_O
```

Actual example:

```text
3 <= 5
```

Any activity `F` that comes after `O` must satisfy:

```math
s_F\ge e_O
```

Actual example:

```text
5 >= 5
```

Since:

```text
s_F >= e_O
and
e_O >= e_G
```

we get:

```math
s_F\ge e_G
```

Actual example:

```text
5 >= 3
```

Meaning:

```text
anything that fits after O
also fits after G
```

Therefore replacing:

```text
O → G
```

does not remove any future activity.

---

## 1.4 Count Comparison — Why OPT Stays Optimal

Suppose OPT contains:

```text
1 first activity
+
y future activities
```

So:

```math
O_{\text{count}}=1+y
```

Actual example:

```text
O schedule:
[1,5], [5,7]

O_count
= 1 + 1
= 2
```

After replacing `O` with `G`, all the same future activities still fit:

```math
G_{\text{count}}=1+y
```

Actual example:

```text
G schedule:
[2,3], [5,7]

G_count
= 1 + 1
= 2
```

Compare:

```math
G_{\text{count}}-O_{\text{count}}
=
(1+y)-(1+y)
=
0
```

Actual example:

```text
2 - 2
= 0
```

So the exchange keeps the optimal count.

Therefore:

```text
there exists an optimal solution
whose first choice is the greedy choice
```

Then repeat the same reasoning on the remaining timeline.

---

## 1.6 Full Dry Run

Activities:

```text
[1,5]
[2,3]
[4,6]
```

Sort by end:

```text
[2,3]
[1,5]
[4,6]
```

Process:

```text
pick [2,3]
lastEnd = 3

[1,5]:
1 < 3
skip

[4,6]:
4 >= 3
pick
```

Answer:

```text
2
```

Visual:

```text
time -------------------------------->

[1-----------5]
   [2--3]        [4---6]
     ✓             ✓
```

---

## 1.7 C++17

```cpp
int maxActivities(vector<pair<long long,long long>> a) {
    sort(a.begin(), a.end(),
         [](const auto& x, const auto& y) {
             if (x.second != y.second)
                 return x.second < y.second;
             return x.first < y.first;
         });

    long long lastEnd = LLONG_MIN;
    int count = 0;

    for (auto [start, finish] : a) {
        if (start >= lastEnd) {
            ++count;
            lastEnd = finish;
        }
    }

    return count;
}
```

Complexity:

```text
O(N log N)
```

Recognition:

```text
maximum count of non-overlapping intervals
→ earliest finish
```

---

# 2. Fractional Knapsack

## 2.1 What It Asks

Each item:

```text
value  = v_i
weight = w_i
```

Capacity:

```text
W
```

Fractions are allowed.

---

## 2.2 Concept Simplified

The scarce resource is:

```text
weight
```

So compare:

```math
\rho_i=\frac{v_i}{w_i}
```

Greedy:

```text
highest value per unit weight first
```

---

## 2.3 Greedy Claim + Proof — Example First

Greedy chooses the item with the largest:

```math
\rho=\frac{value}{weight}
```

where `rho` means **value per one unit of weight**.

### Step 1 — Understand it with real numbers

Suppose:

```text
High-density item H:
value  = 12
weight = 1
density = 12

Low-density item L:
value  = 6
weight = 3
density = 2
```

Compare the **same 1 unit of weight**.

From `L`:

```text
1 unit gives:
1 × 2
= 2 value
```

From `H`:

```text
1 unit gives:
1 × 12
= 12 value
```

Same weight:

```text
1 unit
```

but greedy gets:

```text
12 instead of 2
```

Improvement:

```text
12 - 2
= 10
```

So if higher-density material is still available, using lower-density material first cannot be better.

---

### Step 2 — General exchange

Let:

```text
H = higher-density item
L = lower-density item
delta = amount of weight we exchange
```

We know:

```math
\rho_H\ge\rho_L
```

Actual example:

```text
12 >= 2
```

Suppose old solution has value:

```text
O
```

Remove `delta` weight of `L`.

Value removed:

```math
\delta\rho_L
```

Actual example:

```text
delta = 1

1 × 2
= 2
```

Add the same `delta` weight of `H`.

Value added:

```math
\delta\rho_H
```

Actual example:

```text
1 × 12
= 12
```

Capacity does not change:

```math
-\delta+\delta=0
```

Actual example:

```text
-1 + 1
= 0
```

So feasibility is preserved.

---

## 2.4 Mathematical Derivation — Every Line With Numbers

New value:

```math
G
=
O-\delta\rho_L+\delta\rho_H
```

Actual example:

```text
O = 20

G
= 20 - 1×2 + 1×12
= 30
```

Compare new and old:

```math
G-O
=
(O-\delta\rho_L+\delta\rho_H)-O
```

Actual example:

```text
30 - 20
=
(20 - 2 + 12) - 20
```

Cancel `+O` and `-O`:

```math
G-O
=
-\delta\rho_L+\delta\rho_H
```

Actual example:

```text
10
=
-1×2 + 1×12
=
-2 + 12
```

Factor `delta`:

```math
G-O
=
\delta(\rho_H-\rho_L)
```

Actual example:

```text
10
=
1(12-2)
=
10
```

Now:

```text
delta >= 0
```

and:

```text
rho_H - rho_L >= 0
```

Therefore:

```math
G-O\ge0
```

Actual example:

```text
30 - 20
= 10
>= 0
```

So exchanging equal weight from a lower-density item to a higher-density item never decreases value.

Repeat until all higher-density material is taken first.

---

## 2.5 Why Fractions Matter

The proof depends on this operation:

```text
remove exactly delta weight from L
add exactly delta weight from H
```

That is possible because fractions are allowed.

In 0/1 Knapsack:

```text
whole item or nothing
```

so this exchange may be impossible.

Therefore:

```text
Fractional Knapsack
→ density greedy works

0/1 Knapsack
→ density greedy is not generally correct
```

---

## 2.6 Full Dry Run

```text
A: value=60,  weight=10, density=6
B: value=100, weight=20, density=5
C: value=120, weight=30, density=4

capacity = 50
```

Take A:

```text
value = 60
remaining = 40
```

Take B:

```text
value = 160
remaining = 20
```

Take `20/30` of C:

```math
\frac{20}{30}
=
\frac23
```

Value:

```math
120\cdot\frac23
=
80
```

Final:

```text
60+100+80
= 240
```

---

## 2.7 Why This Fails for 0/1 Knapsack

The proof requires:

```text
exchange exactly delta weight
```

In 0/1 Knapsack:

```text
whole item or nothing
```

So this equal-weight exchange may be impossible.

---

## 2.8 C++17

```cpp
struct Item {
    long double value;
    long double weight;
};

long double fractionalKnapsack(
    vector<Item> items,
    long double capacity
) {
    sort(items.begin(), items.end(),
         [](const Item& a, const Item& b) {
             return a.value / a.weight >
                    b.value / b.weight;
         });

    long double ans = 0;

    for (const Item& item : items) {
        if (capacity <= 0)
            break;

        long double take =
            min(capacity, item.weight);

        ans += take *
               (item.value / item.weight);

        capacity -= take;
    }

    return ans;
}
```

Complexity:

```text
O(N log N)
```

Recognition:

```text
capacity + fractions allowed
→ maximize value per unit resource
```

---

# 3. Kadane's Algorithm

## 3.1 What It Asks

Find:

```math
\max_{L\le R}
\sum_{i=L}^{R}a_i
```

for a contiguous subarray.

---

## 3.2 Concept Simplified

A negative running prefix hurts every future continuation.

So:

```text
if running sum < 0
discard it
```

---

## 3.3 Greedy Claim + Proof — Example First

Greedy rule:

```text
If running sum becomes negative,
discard that whole running prefix.
```

### Step 1 — Understand it with numbers

Suppose:

```text
prefix sum P = -4
future sum F = 9
```

If we KEEP the negative prefix:

```text
P + F
= -4 + 9
= 5
```

If we DROP the prefix:

```text
F
= 9
```

Compare:

```text
9 > 5
```

So keeping `-4` only hurts the future answer.

---

### Step 2 — General mathematical proof

Keep prefix:

```math
O=P+F
```

Actual example:

```text
O
= -4 + 9
= 5
```

Drop prefix:

```math
G=F
```

Actual example:

```text
G
= 9
```

Compare:

```math
G-O
=
F-(P+F)
```

Actual example:

```text
9 - 5
=
9 - (-4+9)
```

Remove bracket:

```math
G-O
=
F-P-F
```

Actual example:

```text
4
=
9 - (-4) - 9
```

Cancel `+F` and `-F`:

```math
G-O=-P
```

Actual example:

```text
4
=
-(-4)
```

If:

```math
P<0
```

then:

```math
-P>0
```

Therefore:

```math
G-O>0
```

So dropping a negative prefix is strictly better.

---

## 3.4 Why Keep a Non-Negative Prefix?

Now suppose:

```text
P = 3
F = 9
```

Keep it:

```text
P + F
= 3 + 9
= 12
```

Drop it:

```text
F
= 9
```

So:

```text
12 >= 9
```

In general, if:

```math
P\ge0
```

then:

```math
P+F\ge F
```

Therefore:

```text
P < 0  → discard
P >= 0 → keep
```

Important:

```text
negative ELEMENT
is not the same as
negative RUNNING SUM
```

---

## 3.6 Full Dry Run

Array:

```text
[1,2,-9,2,3,-1,4]
```

| `x` | `cur` after add | `best` | Action |
|---:|---:|---:|---|
| 1 | 1 | 1 | keep |
| 2 | 3 | 3 | keep |
| -9 | -6 | 3 | reset |
| 2 | 2 | 3 | keep |
| 3 | 5 | 5 | keep |
| -1 | 4 | 5 | keep |
| 4 | 8 | 8 | keep |

Answer:

```text
8
```

---

## 3.7 Negative Element vs Negative Prefix

```text
[5,-2,6]
```

Running sums:

```text
5
3
9
```

`-2` is negative, but the running sum stays positive.

So do **not** discard every negative element.

Discard only:

```text
negative total prefix
```

---

## 3.8 C++17

```cpp
long long kadane(const vector<long long>& a) {
    long long cur = 0;
    long long best = LLONG_MIN;

    for (long long x : a) {
        cur += x;
        best = max(best, cur);

        if (cur < 0)
            cur = 0;
    }

    return best;
}
```

Complexity:

```text
O(N)
```

Recognition:

```text
maximum contiguous subarray sum
→ negative running history is dominated
```

---

# 4. Job Sequencing

## 4.1 What It Asks

Each job has:

```text
duration = 1
deadline = d_i
profit   = p_i
```

Goal:

```math
\max(\text{total profit})
```

---

## 4.2 Two Greedy Decisions

```text
WHICH job?
→ highest profit first

WHERE?
→ latest free slot <= deadline
```

---

## 4.3 Proof A — Highest Profit First, Example First

Suppose two feasible jobs can use the same slot:

```text
Greedy job G:
profit = 30

OPT job O:
profit = 20
```

Suppose OPT's total profit is:

```text
40
```

Replace the `20`-profit job with the `30`-profit job:

```text
new total
= 40 - 20 + 30
= 50
```

Difference:

```text
50 - 40
= 10
```

So the higher-profit job is better.

### General form

We know:

```math
p_G\ge p_O
```

After exchange:

```math
G_{\text{total}}
=
O_{\text{total}}-p_O+p_G
```

Actual example:

```text
50
=
40 - 20 + 30
```

Subtract the old total:

```math
G_{\text{total}}-O_{\text{total}}
=
p_G-p_O
```

Actual example:

```text
50 - 40
=
30 - 20
=
10
```

Since:

```math
p_G-p_O\ge0
```

we get:

```math
G_{\text{total}}\ge O_{\text{total}}
```

So if the higher-profit job can legally replace the lower-profit job, profit cannot decrease.

---

## 4.4 Proof B — Why Use the Latest Legal Slot? Example First

Suppose:

```text
Job J:
deadline = 4
profit = 30
```

Free slots:

```text
1 and 4
```

Both are legal.

Place J at slot `1`:

```text
profit = 30
```

Place J at slot `4`:

```text
profit = 30
```

Profit difference:

```text
30 - 30
= 0
```

So moving J later does **not** hurt profit.

But now compare flexibility.

Early placement:

```text
slot 1 used
slot 4 free
```

Late placement:

```text
slot 1 free
slot 4 used
```

Suppose another job has:

```text
deadline = 1
```

That job can use only slot `1`.

Therefore:

```text
placing flexible job J later
preserves the early slot
for a tighter-deadline job
```

### General form

If:

```math
s<t\le d_J
```

then both `s` and `t` are legal for J.

The same job earns the same profit:

```math
G-O
=
p_J-p_J
=
0
```

So objective does not get worse.

But using `t` leaves `s` free.

Therefore the latest legal slot is never worse and may preserve more future choices.

---

## 4.5 Full Dry Run

```text
J1: deadline=1, profit=10
J2: deadline=2, profit=20
J3: deadline=2, profit=30
```

Sort by profit:

```text
J3, J2, J1
```

Slots:

```text
{1,2}
```

Schedule J3:

```text
latest <= 2
→ slot 2
```

Schedule J2:

```text
latest <= 2
→ slot 1
```

J1:

```text
no slot
```

Profit:

```text
30+20
= 50
```

---

## 4.6 `set` / `upper_bound`

Free slots:

```text
{1,3,5,7,8}
```

Deadline:

```text
6
```

```cpp
auto it = freeSlots.upper_bound(6);
```

points to:

```text
7
```

Previous iterator gives:

```text
5
```

which is the largest free slot `<=6`.

---

## 4.7 C++17

```cpp
struct Job {
    int deadline;
    long long profit;
};

long long maxJobProfit(vector<Job> jobs) {
    sort(jobs.begin(), jobs.end(),
         [](const Job& a, const Job& b) {
             return a.profit > b.profit;
         });

    int n = (int)jobs.size();
    int maxDeadline = 0;

    for (const Job& job : jobs)
        maxDeadline = max(maxDeadline, job.deadline);

    int maxSlot = min(maxDeadline, n);

    set<int> freeSlots;

    for (int t = 1; t <= maxSlot; ++t)
        freeSlots.insert(t);

    long long ans = 0;

    for (const Job& job : jobs) {
        int d = min(job.deadline, maxSlot);

        auto it = freeSlots.upper_bound(d);

        if (it == freeSlots.begin())
            continue;

        --it;

        ans += job.profit;
        freeSlots.erase(it);
    }

    return ans;
}
```

Complexity:

```text
O(N log N)
```

Recognition:

```text
unit jobs + deadlines + profit
→ highest profit first
→ latest legal slot
```

---

# 5. Maximum Perimeter Triangle

## 5.1 What It Asks

Choose three positive sides forming a non-degenerate triangle and maximize:

```math
a+b+c
```

After sorting:

```math
a\le b\le c
```

validity:

```math
a+b>c
```

---

## 5.2 Concept Simplified

Fix largest side:

```text
c
```

The best companions are:

```text
the two largest available sides below c
```

Why?

They maximize:

```text
a+b
```

So they give:

```text
1. best chance to satisfy a+b>c
2. largest perimeter for this fixed c
```

---

## 5.3 Proof — Example First

Sorted array:

```text
[1,2,3,5,7,15]
```

Fix the largest side:

```text
c = 15
```

The **best possible companions** are the two largest sides below it:

```text
a = 5
b = 7
```

Their sum:

```text
5 + 7
= 12
```

But:

```text
12 <= 15
```

So even the largest possible pair fails.

Any other pair is smaller.

Example:

```text
u = 3
v = 7

u+v
= 3+7
= 10
```

And:

```text
10 <= 12 <= 15
```

So it also fails.

Therefore:

```text
if the two largest companions fail,
every smaller pair fails
```

Discard `15`.

Now try:

```text
c = 7
```

Largest companions:

```text
3 and 5
```

Check:

```text
3+5
= 8
> 7
```

Valid triangle.

Perimeter:

```text
3+5+7
= 15
```

---

## 5.4 General Proof — If Best Pair Fails, All Fail

For fixed largest side `c`:

```text
a,b = two largest available sides below c
u,v = any other pair below c
```

Because `a,b` are the largest:

```math
u\le a
```

and:

```math
v\le b
```

Actual example:

```text
3 <= 5
7 <= 7
```

Add them:

```math
u+v\le a+b
```

Actual example:

```text
3+7
<=
5+7

10 <= 12
```

Suppose even the best pair fails:

```math
a+b\le c
```

Actual example:

```text
12 <= 15
```

Then:

```math
u+v\le a+b\le c
```

Actual example:

```text
10 <= 12 <= 15
```

Therefore:

```math
u+v\le c
```

So `u,v,c` cannot form a valid triangle.

---

## 5.5 If Best Pair Works, It Has Best Perimeter for This c

Suppose:

```math
a+b>c
```

Then `a,b,c` is valid.

For every other pair:

```math
u+v\le a+b
```

Add the same `c` to both sides:

```math
u+v+c\le a+b+c
```

Actual example:

```text
c = 7
a = 3
b = 5

other pair:
u = 2
v = 5

2+5+7
= 14

3+5+7
= 15

14 <= 15
```

So `a,b,c` has maximum perimeter for this fixed `c`.

Because we scan `c` from largest to smallest, the **first valid triple from the right** is globally optimal.

---

## 5.6 Full Dry Run

```text
[2,3,15,5,1,7]
```

Sort:

```text
[1,2,3,5,7,15]
```

Try:

```text
5+7 > 15 ?
12 > 15 ?
NO
```

Discard `15`.

Next:

```text
3+5 > 7 ?
8 > 7 ?
YES
```

Perimeter:

```text
3+5+7
= 15
```

---

## 5.7 C++17

```cpp
long long maxTrianglePerimeter(
    vector<long long> a
) {
    sort(a.begin(), a.end());

    for (int i = (int)a.size() - 1; i >= 2; --i) {
        if (a[i - 2] + a[i - 1] > a[i]) {
            return a[i - 2]
                 + a[i - 1]
                 + a[i];
        }
    }

    return -1;
}
```

Complexity:

```text
O(N log N)
```

Recognition:

```text
max triangle perimeter
→ sort
→ test consecutive triples from largest side downward
```

---

# 6. Proof Pattern Summary

| Problem | Greedy Choice | Proof Type | Core Comparison |
|---|---|---|---|
| Activity Selection | earliest finish | exchange / future-space | anything after `O` also fits after earlier-finishing `G` |
| Fractional Knapsack | highest density | equal-weight exchange | `G-O = delta(rho_H-rho_L) >= 0` |
| Kadane | discard negative prefix | dominance algebra | `G-O = -P > 0` |
| Job Sequencing — job | highest profit | algebraic exchange | `G-O = p_G-p_O >= 0` |
| Job Sequencing — slot | latest legal slot | future-space dominance | profit change `=0` |
| Max Perimeter Triangle | largest valid triple | inequality dominance | `u+v <= a+b` |

---

# 7. Recognition Checklist

```text
1. What is the objective?
2. What resource is scarce?
3. What exactly is my greedy choice?
4. What competing choice does OPT make?
5. Can I exchange it?
6. Is feasibility preserved?
7. Can I write G-O or a useful inequality?
8. Can I plug in actual numbers immediately?
9. Does greedy preserve more future options?
10. Can I find a counterexample?
```

---

# 8. Compact Revision Card

```text
ACTIVITY SELECTION
------------------
e_G <= e_O

future:
s_F >= e_O

therefore:
s_F >= e_G

same future activities still fit


FRACTIONAL KNAPSACK
-------------------
rho = value/weight

G
= O - delta*rho_L
    + delta*rho_H

G-O
= delta(rho_H-rho_L)
>= 0


KADANE
------
P < 0

keep:
O=P+F

drop:
G=F

G-O
= F-(P+F)
= -P
> 0


JOB SEQUENCING
--------------
high profit:

G-O
= p_G-p_O
>= 0

latest slot:

same job profit
→ difference = 0

but earlier slot stays free


TRIANGLE
--------
u <= a
v <= b

therefore:
u+v <= a+b

if:
a+b <= c

then:
u+v <= c

so all smaller pairs fail
```

---

# Final Mental Model

```text
Greedy choice
    |
    v
Competing choice
    |
    +---------------------+
    |                     |
objective numeric?    structure/future?
    |                     |
    v                     v
write G-O            use inequality /
expand/factor        exchange / dominance
    |                     |
    +----------+----------+
               |
               v
     feasibility preserved?
               |
              YES
               |
               v
      objective non-worse?
               |
              YES
               |
               v
          greedy is safe
```

> **Core lesson:** use the same discipline as Greedy 1, but do not force algebra everywhere. In Greedy 2, the strongest proof may be an algebraic `G-O` comparison, an inequality chain, or an exchange showing that the greedy choice preserves more future flexibility.
