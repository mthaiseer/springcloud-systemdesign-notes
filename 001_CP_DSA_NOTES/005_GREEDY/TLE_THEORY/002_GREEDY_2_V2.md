# Greedy Algorithms — Level 3
## Greedy 2 — Step-by-Step Mathematical Proof Edition

> **Goal:** make Greedy 2 proofs as explicit as Greedy 1: every important symbolic step is followed immediately by the same step using actual numbers.
>
> **For every problem:**  
> **what it asks → concept simplified → greedy claim → define `G` and `O` → proof line by line → why each step is valid → actual example after each important equation → visual dry run → C++17 → complexity → recognition**
>
> **Important:** not every greedy proof is mainly algebra. Some are proved by **exchange + inequalities**, **dominance**, or **future-space preservation**. We use algebra only where it clarifies the proof.
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

## 0.1 Greedy Proof Goal

A greedy algorithm is:

```text
CLAIM
+
PROOF
```

The proof asks:

```text
Why can an optimal solution
be changed to use the greedy choice
without becoming worse?
```

---

## 0.2 G and O

Use:

```text
G = greedy answer / greedy local contribution
O = competing / OPT contribution
```

For maximization:

```math
G\ge O
```

Often prove:

```math
G-O\ge0
```

For minimization:

```math
G\le O
```

Often prove:

```math
G-O\le0
```

---

## 0.3 Exchange Argument

```text
Greedy wants G
OPT uses O
     |
     v
replace O → G
     |
     v
still feasible?
     |
    YES
     |
     v
objective non-worse?
     |
    YES
     |
     v
greedy choice is safe
```

Always check:

```text
1. feasibility
2. objective
3. repeatability
```

---

## 0.4 Algebra Rules Used Here

### Remove a minus bracket

```text
A - (B + C)
= A - B - C
```

### Cancel equal terms

```text
A + X - A
= X
```

### Factor

```text
ax - ay
= a(x-y)
```

### Sign reasoning

```text
positive × positive = positive
negative × negative = positive
```

### Inequality transitivity

If:

```text
A >= B
B >= C
```

then:

```text
A >= C
```

---

## 0.5 Universal Greedy Proof Template

```text
1. State the greedy choice.
2. Choose one competing / OPT choice.
3. Check what must remain feasible.
4. Define G and O when useful.
5. Write G-O or the key inequality.
6. Expand / cancel / factor.
7. Plug in actual numbers immediately.
8. Use ordering / signs / transitivity.
9. Conclude greedy is non-worse.
10. Explain why repeating the local argument proves the full solution.
```

---

## 0.6 Interval Notation

Activity:

```math
[s_i,e_i]
```

Compatibility:

```math
s_{\text{next}}\ge e_{\text{last}}
```

Example:

```text
[2,3] then [4,6]

4 >= 3
```

---

## 0.7 Ratio / Density

Fractional Knapsack uses:

```math
\rho_i=\frac{v_i}{w_i}
```

Meaning:

```text
value per one unit of weight
```

Example:

```text
value = 12
weight = 1

rho = 12
```

---

## 0.8 Deadline / Slot / Profit

For Job Sequencing:

```text
duration = 1
deadline = d_i
profit   = p_i
```

Job with deadline `3` can use:

```text
slot 1, 2, or 3
```

---

## 0.9 Triangle Inequality

For sorted positive sides:

```math
a\le b\le c
```

a non-degenerate triangle requires:

```math
a+b>c
```

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

## 1.3 Greedy Claim

Let:

```text
G = earliest-finishing compatible activity
O = first activity in some optimal schedule
```

Then:

```math
e_G\le e_O
```

Actual example:

```text
G = [2,3] → e_G = 3
O = [1,5] → e_O = 5

3 <= 5
```

---

## 1.4 Exchange Proof — Every Step Explained

Suppose OPT begins with `O`.

Its next selected activity `F` must satisfy:

```math
s_F\ge e_O
```

Actual example:

```text
F = [5,7]

s_F = 5
e_O = 5

5 >= 5
```

We also know:

```math
e_O\ge e_G
```

Actual example:

```text
5 >= 3
```

Combine the inequalities:

```math
s_F\ge e_O\ge e_G
```

Actual example:

```text
5 >= 5 >= 3
```

Therefore:

```math
s_F\ge e_G
```

Actual example:

```text
5 >= 3
```

So every activity that could follow `O` can also follow `G`.

Exchange:

```text
O → G
```

preserves feasibility.

---

## 1.5 Count Comparison

Suppose OPT has:

```text
1 first activity + y later activities
```

Then:

```math
O_{\text{count}}=1+y
```

Actual example:

```text
O schedule:
[1,5], [5,7]

y = 1

O_count
= 1+1
= 2
```

After replacing `O` by `G`:

```math
G_{\text{count}}=1+y
```

Actual example:

```text
G schedule:
[2,3], [5,7]

G_count
= 1+1
= 2
```

Difference:

```math
G_{\text{count}}-O_{\text{count}}
=
(1+y)-(1+y)
=
0
```

Actual example:

```text
2-2
= 0
```

So an optimal schedule exists that starts with `G`.

Repeat on the remaining timeline.

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

## 2.3 Greedy Claim

Suppose:

```text
H = higher-density item
L = lower-density item
```

Then:

```math
\rho_H\ge\rho_L
```

Actual example:

```text
H:
v_H = 12
w_H = 1
rho_H = 12

L:
v_L = 6
w_L = 3
rho_L = 2

12 >= 2
```

---

## 2.4 Exchange Setup

Suppose another solution uses:

```text
delta weight of L
```

while the same amount of `H` is still available.

Exchange:

```text
remove delta weight of L
add    delta weight of H
```

Weight change:

```math
-\delta+\delta=0
```

Actual example:

```text
delta = 1

-1 + 1
= 0
```

So capacity feasibility is unchanged.

---

## 2.5 Mathematical Proof — Every Step Explained

Let:

```text
O = old total value
G = value after exchange
```

Lower-density value removed:

```math
\delta\rho_L
```

Actual example:

```text
1×2
= 2
```

Higher-density value added:

```math
\delta\rho_H
```

Actual example:

```text
1×12
= 12
```

Therefore:

```math
G
=
O-\delta\rho_L+\delta\rho_H
```

Actual example:

```text
Let O = 20

G
= 20 - 1×2 + 1×12
= 30
```

Subtract `O`:

```math
G-O
=
(O-\delta\rho_L+\delta\rho_H)-O
```

Actual example:

```text
30-20
=
(20-2+12)-20
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

= -2 + 12
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

= 10
```

We know:

```math
\delta\ge0
```

Actual example:

```text
1 >= 0
```

and:

```math
\rho_H-\rho_L\ge0
```

Actual example:

```text
12-2
= 10
>= 0
```

Therefore:

```math
G-O\ge0
```

Actual example:

```text
30-20
= 10
>= 0
```

So replacing equal weight of a lower-density item with higher-density material cannot decrease value.

Repeat until all higher-density material is used first.

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

## 3.3 Greedy Claim

Let:

```text
P = current prefix sum
F = any future continuation sum
```

Suppose:

```math
P<0
```

---

## 3.4 Mathematical Proof — Every Step Explained

If we keep the prefix:

```math
O=P+F
```

Actual example:

```text
P = -4
F = 9

O
= -4+9
= 5
```

If we drop the prefix:

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
9-5
=
9-(-4+9)
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
9-(-4)-9
```

Cancel `+F` and `-F`:

```math
G-O
=
-P
```

Actual example:

```text
4
=
-(-4)
```

Since:

```math
P<0
```

then:

```math
-P>0
```

Actual example:

```text
-(-4)
= 4
> 0
```

Therefore:

```math
G-O>0
```

So starting fresh is strictly better than carrying a negative prefix.

---

## 3.5 Why Keep a Non-Negative Prefix?

If:

```math
P\ge0
```

then:

```math
P+F\ge F
```

Actual example:

```text
P = 3
F = 9

3+9
= 12
>= 9
```

So:

```text
P < 0  → discard
P >= 0 → keep
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

## 4.3 Proof A — Highest Profit First

Suppose:

```text
G = higher-profit feasible job
O = lower-profit scheduled job
```

We know:

```math
p_G\ge p_O
```

Actual example:

```text
p_G = 30
p_O = 20

30 >= 20
```

Let:

```text
O_total = current schedule profit
```

After exchange:

```math
G_{\text{total}}
=
O_{\text{total}}-p_O+p_G
```

Actual example:

```text
O_total = 40

G_total
= 40-20+30
= 50
```

Subtract old total:

```math
G_{\text{total}}-O_{\text{total}}
=
(O_{\text{total}}-p_O+p_G)-O_{\text{total}}
```

Actual example:

```text
50-40
=
(40-20+30)-40
```

Cancel totals:

```math
G_{\text{total}}-O_{\text{total}}
=
p_G-p_O
```

Actual example:

```text
10
=
30-20
```

Since:

```math
p_G-p_O\ge0
```

Actual example:

```text
30-20
= 10
>= 0
```

we get:

```math
G_{\text{total}}\ge O_{\text{total}}
```

So if the higher-profit job can legally use that slot, replacing the lower-profit job cannot hurt.

---

## 4.4 Proof B — Latest Legal Slot

Suppose job `J` can use either:

```text
early slot s
late  slot t
```

with:

```math
s<t\le d_J
```

Actual example:

```text
s = 1
t = 4
d_J = 4

1 < 4 <= 4
```

Profit is unchanged:

```math
G_{\text{profit}}-O_{\text{profit}}
=
p_J-p_J
=
0
```

Actual example:

```text
30-30
= 0
```

So moving the same job later does not reduce profit.

But it frees the earlier slot.

Visual:

```text
slots:   1    2    3    4

early:
         [J]  [ ]  [ ]  [ ]

late:
         [ ]  [ ]  [ ]  [J]
          ^
          preserved
```

Why useful?

A future job may have:

```text
deadline = 1
```

and can use only slot `1`.

Thus latest placement preserves more flexibility.

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

## 5.3 If the Largest Pair Fails, All Pairs Fail

Let:

```text
a,b = two largest sides below c
u,v = any other pair below c
```

Then:

```math
u\le a
```

and:

```math
v\le b
```

Actual example:

```text
sorted:
[1,2,3,5,7,15]

c = 15
a = 5
b = 7

choose another pair:
u = 3
v = 7

3 <= 5
7 <= 7
```

Add the inequalities:

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

Suppose greedy companions fail:

```math
a+b\le c
```

Actual example:

```text
5+7
<= 15

12 <= 15
```

Combine:

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

Actual example:

```text
10 <= 15
```

So no other pair can form a triangle with `c`.

Discard `c`.

---

## 5.4 If the Largest Pair Works, It Is Best for This c

Suppose:

```math
a+b>c
```

Then `a,b,c` is valid.

For any other pair:

```math
u+v\le a+b
```

Add `c` to both sides:

```math
u+v+c
\le
a+b+c
```

Actual example:

```text
c = 7
a = 3
b = 5

another pair:
u = 2
v = 5

u+v+c
= 2+5+7
= 14

a+b+c
= 3+5+7
= 15

14 <= 15
```

So `a,b,c` gives maximum perimeter for that fixed largest side.

---

## 5.5 Why the First Valid Triple From the Right Is Globally Best

Scan largest side first.

If a larger side fails with its two largest companions:

```text
no triangle using that side exists
```

When the first valid triple appears:

```text
x[i-2], x[i-1], x[i]
```

it uses:

```text
largest surviving c
+
largest two companions
```

Any triangle entirely to the left uses no larger sides.

Therefore it cannot have larger perimeter.

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
| Activity Selection | earliest finish | exchange + transitivity | `s_F >= e_O >= e_G` |
| Fractional Knapsack | highest density | algebraic exchange | `G-O = delta(rho_H-rho_L) >= 0` |
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
