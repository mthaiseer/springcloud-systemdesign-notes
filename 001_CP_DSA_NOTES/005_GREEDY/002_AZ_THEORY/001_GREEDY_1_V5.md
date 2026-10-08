# AlgoZenith Greedy — Class 1
## V8 · TLE-style explanations: one idea at a time

> **Goal:** first understand **what the question wants**. Next use **small numbers** to discover a choice. Finally prove why it works for *all* valid inputs.
>
> **Proof layout:** **Question → Example → Observation → General statement → One algebra step → What that step means → Actual numbers → Why it is always true.**
>
> The examples are teaching models of the five class patterns, not quotes from contest statements. C++17 uses `long long`, assuming **every intermediate operation** fits signed 64-bit (see §0.14).

## Clickable Table of Contents

- [0. Prerequisites — With Small Examples](#0-prerequisites--with-small-examples)
  - [0.1 Understand the question and objective](#01-understand-the-question-and-objective)
  - [0.2 What is a contribution?](#02-what-is-a-contribution)
  - [0.3 Compare only what changes](#03-compare-only-what-changes)
  - [0.4 Exchange proof](#04-exchange-proof)
  - [0.5 Inversion and sorting](#05-inversion-and-sorting)
  - [0.6 Remove brackets, cancel, and factor](#06-remove-brackets-cancel-and-factor)
  - [0.7 Inequalities](#07-inequalities)
  - [0.8 Ratio comparison without division](#08-ratio-comparison-without-division)
  - [0.9 Waiting time and completion time](#09-waiting-time-and-completion-time)
  - [0.10 Absolute distance and median](#010-absolute-distance-and-median)
  - [0.11 Weight and prefix weight](#011-weight-and-prefix-weight)
  - [0.12 Keeping Top-K with a min-heap](#012-keeping-top-k-with-a-min-heap)
  - [0.13 Bottleneck multiplied by a sum](#013-bottleneck-multiplied-by-a-sum)
  - [0.14 Safe use of `long long`](#014-safe-use-of-long-long)
- [1. Maximum Dot Product — Which Values Should Be Paired?](#1-maximum-dot-product--which-values-should-be-paired)
- [2. Job Scheduling — Which Job Should Run First?](#2-job-scheduling--which-job-should-run-first)
- [3. Median — Where Should People Meet?](#3-median--where-should-people-meet)
- [4. Weighted Median — Where Should Groups Meet?](#4-weighted-median--where-should-groups-meet)
- [5. Team Performance — Which K Workers Should We Choose?](#5-team-performance--which-k-workers-should-we-choose)
- [6. Recognition and Revision](#6-recognition-and-revision)

---

# 0. Prerequisites — With Small Examples

## 0.1 Understand the question and objective

**Always answer these three questions before choosing an algorithm:**

1. What am I **allowed to change**?
2. What must remain **valid**?
3. What number am I trying to **maximize or minimize**?

**Mini example:** Two workers have strengths `2, 5`. Two machines have multipliers `7, 3`. You may rearrange the machine assignments to maximize combined output.

| Story | Variables | Numbers |
|---|---|---|
| Worker strengths | `A` | `[2,5]` |
| Machine multipliers | `B` | `[7,3]` |
| Allowed choice | Rearrange `B` | `[7,3]` or `[3,7]` |
| Objective | Maximize `sum(A[i]*B[i])` | Compare total outputs |

**Lesson:** translate the story into a decision and an objective. **Do not start by guessing “sort.”**

## 0.2 What is a contribution?

One element or one pair adds a **part** to the total answer.

For paired arrays:

```text
A = [2, 5]
B = [7, 3]

position 1 contributes 2×7 = 14
position 2 contributes 5×3 = 15

total = 14 + 15 = 29
```

**Lesson:** if a choice changes only two contributions, investigate those two contributions first.

## 0.3 Compare only what changes

**Question:** Why can a proof about the *whole* answer use only two positions?

Suppose fixed `A=[1,2,5,8]` and you exchange `7` and `4` in `B`.

| Position | A | B before | Product before | B after | Product after |
|---|---:|---:|---:|---:|---:|
| 1 | 1 | 3 | 3 | 3 | 3 |
| **2** | 2 | **7** | **14** | **4** | **8** |
| **3** | 5 | **4** | **20** | **7** | **35** |
| 4 | 8 | 6 | 48 | 6 | 48 |

Unchanged parts are `3` and `48`. They contribute equally in both arrangements.

Compare only the changed pair:

```text
before = 14 + 20 = 34
after  =  8 + 35 = 43
gain   = 43 - 34 = 9
```

The full total changes from `85` to `94`, also a gain of `9`.

**Lesson:** unaffected contributions cancel. This is called **local pair comparison**.

**Warning:** in scheduling, swapping non-neighboring jobs can also change the jobs *between* them. Use **adjacent swaps** for the two-job scheduling proof.

## 0.4 Exchange proof

An **exchange proof** explains why a local greedy choice is safe.

**Question:** If someone gives us an optimal solution with a different choice, can we replace just that choice with the greedy choice **without making the result worse**?

Tiny example:

```text
crossed pairing: 2×7 + 5×3 = 29
aligned pairing: 2×3 + 5×7 = 41
```

Replacement gives `41 ≥ 29`. This numerical example suggests the rule, but **does not prove it for all numbers**.

To complete an exchange proof:

1. State the two choices.
2. Show the replacement is still **allowed**.
3. Prove the new answer is **no worse** using variables.
4. Repeat the safe replacement until the solution follows the greedy rule.

**Memory:** *Can I exchange one bad choice for a greedy choice safely?*

## 0.5 Inversion and sorting

An **inversion** means a larger value appears before a smaller value when ascending order is desired.

```text
before: [1, 7, 4, 9]
              ↑  ↑
              7 > 4

after:  [1, 4, 7, 9]
```

If the other array is sorted `A=[1,2,5,8]`:

| Pairing at the changed positions | Contribution |
|---|---:|
| Before | `2×7 + 5×4 = 34` |
| After | `2×4 + 5×7 = 43` |

Gain = `9`.

**Why does removing inversions finish?** Every unsorted array has at least one **adjacent** inversion. Swapping adjacent inversions repeatedly eventually produces sorted order.

**Lesson:** if every inversion-removing swap cannot hurt the answer, sorting gives an optimal arrangement.

## 0.6 Remove brackets, cancel, and factor

We need three basic algebra skills.

### A. Remove a minus bracket

```text
20 - (7 + 3)
= 20 - 7 - 3
= 10
```

A minus in front of a bracket changes the sign of **every** term inside.

### B. Cancel equal terms

```text
(10 + 41) - (10 + 29)
= 10 + 41 - 10 - 29
= 41 - 29
= 12
```

The common `+10` and `−10` cancel.

### C. Factor a repeated difference

Start with:

```text
5×7 - 5×3
```

Both terms contain `5`:

```text
= 5×(7-3)
= 5×4
= 20
```

A more important example for greedy proofs:

```text
5×(7-3) - 2×(7-3)
```

Both terms contain `(7-3)`:

```text
= (5-2)×(7-3)
= 3×4
= 12
```

**Lesson:** expand → find repeated terms → factor. The proof in Pattern 1 uses exactly this.

## 0.7 Inequalities

An inequality compares two values, e.g., `5 ≤ 8`.

### Add or subtract the same quantity

```text
5 <= 8
add 2 to both sides
7 <= 10
```

Direction does **not** change.

### Multiply by a negative number

```text
-8 <= -3
multiply by -1
8 >= 3
```

Direction **reverses**.

### Divide by a positive number

```text
12 >= 6
divide by 3
4 >= 2
```

Direction stays the same.

**Important in scheduling:** If `D_B*T_A <= D_A*T_B`, we may divide by positive `T_A` and `T_B` without reversing the inequality. This will reveal the `D/T` rule in Pattern 2.

## 0.8 Ratio comparison without division

Suppose two jobs have:

| Job | Loss rate D | Time T | Ratio D/T |
|---|---:|---:|---:|
| A | 2 | 5 | 0.4 |
| B | 10 | 3 | 3.33… |

To compare `D_A/T_A` with `D_B/T_B`, use **cross multiplication** because both times are positive.

Question: Is `D_A/T_A ≥ D_B/T_B`?

```text
2/5 >= 10/3 ?
```

Multiply both sides by `5×3` (positive):

```text
2×3 >= 10×5 ?
6 >= 50 ?  NO
```

So B has the bigger ratio.

**In C++**, use `a.d*b.t > b.d*a.t` when these products fit `long long`. Do not use floating-point division unless the model specifically requires it.

## 0.9 Waiting time and completion time

**Question:** If one computer runs A for `5` minutes and B for `3` minutes, when does each finish?

| Order A then B | Waiting | Processing | Completion |
|---|---:|---:|---:|
| A | 0 | 5 | 5 |
| B | 5 | 3 | 8 |

| Order B then A | Waiting | Processing | Completion |
|---|---:|---:|---:|
| B | 0 | 3 | 3 |
| A | 3 | 5 | 8 |

```text
A then B:  0 -- A (5) -- 5 -- B (3) -- 8
B then A:  0 -- B (3) -- 3 -- A (5) -- 8
```

**Processing time:** duration of the job itself.

**Waiting time:** time spent waiting for previous jobs.

**Completion time:** `waiting + processing` = running sum of durations.

**Lesson:** both orders finish all work at 8, but **which job finishes earlier** changes.

## 0.10 Absolute distance and median

Absolute distance means distance on a straight number line:

```text
|3-7| = 4
|7-3| = 4
```

**Question:** Friends live at positions `[1,3,7]`. Where should they meet to minimize combined walking distance?

| Meeting point X | Total distance |
|---|---:|
| 1 | `0+2+6=8` |
| **3** | **`2+0+4=6`** |
| 7 | `6+4+0=10` |

The middle value `3` is the **median**.

When walking the meeting point slightly to the right without crossing anyone:

- each person left of X walks farther;
- each person right of X walks less.

This is the proof idea for Pattern 3.

## 0.11 Weight and prefix weight

**Question:** What if there are **three people at position 7**, not just one?

| Position | Number of people | Cumulative people |
|---|---:|---:|
| 1 | 1 | 1 |
| 3 | 1 | 2 |
| 7 | 3 | 5 |

Conceptually: `[1,3,7,7,7]`.

The middle (third) person is at `7`, so the **weighted median** is `7`.

**Prefix weight** simply means the number of people counted so far (`1,2,5`). It avoids making a huge array of repeated people.

## 0.12 Keeping Top-K with a min-heap

**Question:** After seeing many efficiencies, how can we keep the **largest K** values?

Let `K=2`; values arrive `4,10,20`.

| Value arrives | Values kept | Reason |
|---|---|---|
| 4 | `[4]` | fewer than 2 |
| 10 | `[4,10]` | exactly 2 |
| 20 | `[10,20]` | discard the smallest `4` |

**Why a min-heap?** The smallest currently kept value is available at `top()`, so we can discard it quickly.

```cpp
priority_queue<long long, vector<long long>, greater<long long>> pq;
```

We maintain a separate running sum of the values in the heap.

## 0.13 Bottleneck multiplied by a sum

Some objectives have two very different parts:

```text
team score = (sum of efficiencies) × (minimum team speed)
```

| Workers: (speed,efficiency) | Sum efficiency | Minimum speed | Score |
|---|---:|---:|---:|
| `(6,4)` and `(4,10)` | 14 | 4 | 56 |
| `(4,10)` and `(2,20)` | 30 | 2 | **60** |

More efficiency can compensate for a lower minimum speed.

**Trick:** Fix a possible **minimum speed S**, then choose the largest K efficiencies among workers with speed at least S.

## 0.14 Safe use of `long long`

`long long` can hold values up to approximately `9.22×10^18`.

```text
10^9 × 10^9 = 10^18         fits
100000 × 10^18 = 10^23     does not fit
```

Every calculation must fit, including **intermediate multiplication**, coordinate subtraction, completion-time sums, and running totals. The code in this note uses `long long` only, as requested; do not apply it unchanged if constraints require larger intermediates.

---

# 1. Maximum Dot Product — Which Values Should Be Paired?

## 1.1 What is the question asking?

There are two equal-length arrays. You may **rearrange the pairing**. Multiply each matched pair and add their products.

**Goal:** Return the **maximum possible sum**.

**Real-world model:** match workers' strengths to machine multipliers to maximize production.

Example:

```text
A = [2, 5]
B = [7, 3]
```

## 1.2 Try two possible arrangements

| Pairing | First output | Second output | Total |
|---|---:|---:|---:|
| Crossed | `2×7=14` | `5×3=15` | 29 |
| Aligned | `2×3=6` | `5×7=35` | **41** |

**Observation:** Pair small with small and large with large. This improves this example by `41−29=12`.

But an example alone is **not** a proof. Next prove the same choice works for arbitrary numbers.

## 1.3 Proof — one operation at a time

Let `a≤b` be the two values from A and `c≤d` from B.

**Step 1 — Name the two possibilities.**

Crossed contribution:

```text
O = a×d + b×c
```

Actual numbers:

```text
O = 2×7 + 5×3 = 29
```

Aligned contribution:

```text
G = a×c + b×d
```

Actual numbers:

```text
G = 2×3 + 5×7 = 41
```

**Step 2 — Measure gain: new minus old.**

```text
G - O = (ac + bd) - (ad + bc)
```

Actual numbers:

```text
41 - 29 = (6 + 35) - (14 + 15) = 12
```

**Step 3 — Remove the brackets.** The second bracket has a minus, so both its terms turn negative.

```text
G - O = ac + bd - ad - bc
```

Actual numbers:

```text
= 6 + 35 - 14 - 15 = 12
```

**Step 4 — Put terms with b together, then terms with a.**

```text
G - O = bd - bc - ad + ac
```

Actual numbers:

```text
= 35 - 15 - 14 + 6 = 12
```

**Step 5 — Factor each pair.**

```text
G - O = b(d-c) - a(d-c)
```

Actual numbers:

```text
= 5(7-3) - 2(7-3) = 20-8 = 12
```

**Step 6 — Factor the repeated `(d-c)`.**

```text
G - O = (b-a)(d-c)
```

Actual numbers:

```text
= (5-2)(7-3) = 3×4 = 12
```

**Step 7 — Why is the gain never negative?**

We assumed `b≥a`, so `b−a≥0`. We also assumed `d≥c`, so `d−c≥0`.

**A nonnegative number times a nonnegative number is nonnegative.** Therefore `G−O≥0`: aligned is never worse than crossed (ties can keep the sum equal).

## 1.4 Why does a proof for TWO values solve N values?

1. Sort A ascending.
2. If B is not ascending, some neighboring values of B are inverted.
3. Swap those neighboring B values. The two-pair proof shows the dot product does not decrease.
4. Continue until B is sorted ascending.

Therefore an optimal pairing exists with **both arrays sorted in the same order**.

**For minimum dot product**, use opposite orders.

## 1.5 Algorithm and dry run

```text
1. Sort A ascending.
2. Sort B ascending.
3. Add products of matching positions.
```

| Position | A | B | Product | Running total |
|---|---:|---:|---:|---:|
| 0 | 2 | 3 | 6 | 6 |
| 1 | 5 | 7 | 35 | **41** |

## 1.6 C++17 (`long long`)

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;
    vector<long long> a(n), b(n);
    for (long long &x : a) cin >> x;
    for (long long &x : b) cin >> x;

    sort(a.begin(), a.end());
    sort(b.begin(), b.end());

    long long ans = 0;
    for (int i = 0; i < n; ++i) {
        ans += a[i] * b[i];
    }
    cout << ans << '\n';
}
```

**Time:** `O(N log N)`. **Space:** sorting stack plus input arrays. **Assumption:** all products and running sums fit `long long`.

**Recognition:** rearrange pairings + maximize sum of products → same-order sorting + exchange proof.

---

# 2. Job Scheduling — Which Job Should Run First?

## 2.1 What is the question asking?

One computer runs all jobs, **one at a time**. Every job has:

- `T`: minutes needed to process it;
- `D`: points lost *per minute until it finishes*;
- `S`: its starting/base score.

The job's final score is `S − D×completionTime`.

**Goal:** Choose an order that **maximizes total final score**. All jobs must be completed, `T>0`, and score may become negative because the mathematical model is linear.

## 2.2 Start with a small real example

| Job | Processing time T | Base score S | Loss per minute D |
|---|---:|---:|---:|
| A | 5 | 100 | 2 |
| B | 3 | 100 | 10 |

**Order A then B:**

```text
0 -- A (5) -- 5 -- B (3) -- 8
```

| Job | Finishes at | Score |
|---|---:|---:|
| A | 5 | `100−2×5=90` |
| B | 8 | `100−10×8=20` |
| **Total** | | **110** |

**Order B then A:**

```text
0 -- B (3) -- 3 -- A (5) -- 8
```

| Job | Finishes at | Score |
|---|---:|---:|
| B | 3 | `100−10×3=70` |
| A | 8 | `100−2×8=84` |
| **Total** | | **154** |

**Observation:** B first gives `154−110=44` more points.

**Why?** B is much more expensive to delay, and it is short. But we still need a rule that works for every pair of jobs.

## 2.3 First simplification — maximize score = minimize loss

Both orders earn the same combined **base score**:

```text
S_A + S_B = 100 + 100 = 200
```

Only the **lost points** change.

| Order | A loses | B loses | Total loss |
|---|---:|---:|---:|
| A → B | `2×5=10` | `10×8=80` | **90** |
| B → A | `2×8=16` | `10×3=30` | **46** |

`200−90=110` and `200−46=154`.

Therefore **minimizing total loss** is the same as maximizing total score.

## 2.4 Why we swap only TWO neighboring jobs

What if A and B appear in a long schedule?

Suppose job Q takes 2 minutes and R takes 4 minutes:

```text
OLD: 0 [Q] 2 [A] 7 [B] 10 [R] 14
NEW: 0 [Q] 2 [B] 5 [A] 10 [R] 14
```

| Job | OLD completion | NEW completion |
|---|---:|---:|
| Q | 2 | 2 |
| **A** | **7** | **10** |
| **B** | **10** | **5** |
| R | 14 | 14 |

**Key observation:** Q is unchanged. R still starts at minute 10 because A and B occupy `5+3=8` minutes in either order.

Thus only A's and B's losses change. This justifies an **adjacent-pair exchange proof**.

## 2.5 Discover the rule using EXTRA waiting time (easiest proof)

Imagine both jobs start at the same time. In both orders, each job must spend its **own T minutes** processing; those own-time costs are common.

**If A runs first**, B must wait 5 extra minutes.

Additional loss caused to B:

```text
D_B × T_A = 10 × 5 = 50
```

**If B runs first**, A must wait 3 extra minutes.

Additional loss caused to A:

```text
D_A × T_B = 2 × 3 = 6
```

Which extra loss is smaller?

```text
6 < 50
```

Therefore **B should go first**. This also explains the 44-point gain: `50−6=44`.

This comparison already contains the full greedy rule. The next section proves it algebraically, including earlier jobs.

## 2.6 General proof — one short equation, then numbers

Let `p` be time already spent on jobs before A and B. In our four-job schedule, `p=2`.

### Step 1 — Work out the completion times

**A then B:**

```text
C_A = p + T_A
C_B = p + T_A + T_B
```

Actual numbers:

```text
C_A = 2+5   = 7
C_B = 2+5+3 = 10
```

**B then A:**

```text
C_B = p + T_B
C_A = p + T_B + T_A
```

Actual numbers:

```text
C_B = 2+3   = 5
C_A = 2+3+5 = 10
```

### Step 2 — Write total loss in each order

Loss = `D × completionTime`.

**A then B:**

```text
L_AB = D_A(p+T_A) + D_B(p+T_A+T_B)
```

Actual numbers:

```text
L_AB = 2(2+5) + 10(2+5+3)
     = 14 + 100
     = 114
```

**B then A:**

```text
L_BA = D_B(p+T_B) + D_A(p+T_B+T_A)
```

Actual numbers:

```text
L_BA = 10(2+3) + 2(2+3+5)
     = 50 + 20
     = 70
```

### Step 3 — Separate SHARED cost from EXTRA waiting cost

Instead of subtracting ten crowded terms, collect the parts appearing in **both** orders.

The **shared cost** is:

```text
Common = (D_A+D_B)×p + D_A×T_A + D_B×T_B
```

Actual numbers:

```text
Common = (2+10)×2 + 2×5 + 10×3
       = 24 + 10 + 30
       = 64
```

So we can rewrite the two losses as:

**A then B:**

```text
L_AB = Common + D_B×T_A
```

Actual numbers:

```text
L_AB = 64 + 10×5 = 64 + 50 = 114
```

**B then A:**

```text
L_BA = Common + D_A×T_B
```

Actual numbers:

```text
L_BA = 64 + 2×3 = 64 + 6 = 70
```

**Why is this allowed?** Expanding either right-hand side gives the original loss expression. We have only **grouped** identical terms, not removed them.

### Step 4 — Cancel what is identical

Subtract the two losses:

```text
L_AB - L_BA = D_B×T_A - D_A×T_B
```

The `Common` term cancels.

Actual numbers:

```text
114 - 70 = 10×5 - 2×3
         = 50 - 6
         = 44
```

**Meaning:** changing the order affects only the **extra waiting loss** each job causes the other. It does not depend on `p`.

### Step 5 — When is A first better?

A first is better when its extra waiting penalty is **no larger** than B first's extra waiting penalty.

```text
D_B×T_A <= D_A×T_B
```

Actual numbers:

```text
10×5 <= 2×3 ?
50 <= 6 ?  NO
```

So A first is not optimal for these values.

### Step 6 — Derive a sorting ratio from the comparison

We start with the **general** condition for A first:

```text
D_B×T_A <= D_A×T_B
```

Divide both sides by positive `T_A`:

```text
D_B <= D_A×T_B/T_A
```

Then divide both sides by positive `T_B`:

```text
D_B/T_B <= D_A/T_A
```

Read it in descending order:

```text
D_A/T_A >= D_B/T_B
```

Actual ratios:

```text
A: 2/5  = 0.4
B: 10/3 = 3.33...
```

B's ratio is larger, so **B runs first**.

**Why is dividing safe?** Both processing times are strictly positive, so the inequality direction does not change.

### Step 7 — Why does this prove the order for ALL jobs?

Suppose a full schedule contains two **adjacent** jobs in the wrong order of `D/T`.

- Swap them: the pair's combined time stays the same.
- Earlier and later jobs keep their completion times.
- The pair's loss cannot increase, by Step 5.

Repeat until no adjacent ratio inversion remains. We obtain jobs sorted by **decreasing `D/T`**, without worsening the total score.

This is an **exchange proof of optimality**, not just a helpful heuristic.

## 2.7 Algorithm and dry run

1. Sort jobs by decreasing `D/T` using cross-products (to avoid floating-point comparisons).
2. Maintain a running `finishTime`, initially 0.
3. Add `S - D*finishTime` for every job.

| Job in greedy order | New finish time | Score added | Running total |
|---|---:|---:|---:|
| B | 3 | `100−10×3=70` | 70 |
| A | 8 | `100−2×8=84` | **154** |

## 2.8 C++17 (`long long`)

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Job {
    long long s, d, t;
};

bool earlier(const Job& a, const Job& b) {
    long long left  = a.d * b.t;
    long long right = b.d * a.t;
    if (left != right) return left > right;
    return a.t < b.t;  // ties in ratio: either order is fine
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;
    vector<Job> jobs(n);
    for (Job& j : jobs) cin >> j.s >> j.d >> j.t;

    sort(jobs.begin(), jobs.end(), earlier);

    long long finishTime = 0;
    long long totalScore = 0;
    for (const Job& j : jobs) {
        finishTime += j.t;
        totalScore += j.s - j.d * finishTime;
    }
    cout << totalScore << '\n';
}
```

**Time:** `O(N log N)`. **Assumptions:** `T>0`, every job runs, linear loss continues even below zero score, and every intermediate multiplication/sum fits `long long`.

**Recognition:** sequence of jobs + penalty based on finish time → adjacent swap → decreasing `D/T`.

---
# 3. Median — Where Should People Meet?

## 3.1 What is the question asking?

Friends live along **one straight road**. Choose any meeting point `X` that makes their **total walking distance as small as possible**.

Each person counts once.

**Input example:** positions `[1,3,7]`.

**Goal:** minimize `|X-1| + |X-3| + |X-7|`.

## 3.2 Try actual meeting points

| Meet at X | From 1 | From 3 | From 7 | Total |
|---|---:|---:|---:|---:|
| 1 | 0 | 2 | 6 | 8 |
| 2 | 1 | 1 | 5 | 7 |
| **3** | **2** | **0** | **4** | **6** |
| 4 | 3 | 1 | 3 | 7 |
| 7 | 6 | 4 | 0 | 10 |

**Observation:** Meeting at `3`, the **middle position**, gives the smallest total distance.

Why is the middle always best? We can prove it by **moving the meeting point**.

## 3.3 Proof — move the meeting point a little

### Step 1 — Compare X=2 with X=3

We move the meeting point **one step to the right**.

| Person at | Distance before (X=2) | After (X=3) | Change |
|---|---:|---:|---:|
| 1 | 1 | 2 | +1 |
| 3 | 1 | 0 | −1 |
| 7 | 5 | 4 | −1 |
| **Total** | **7** | **6** | **−1** |

**What happened?** One person walks 1 unit farther, but two people walk 1 unit less.

Therefore total distance decreases by `1`.

### Step 2 — Define the two groups

For a meeting point X **between data positions**:

```text
L = number of people to the left of X
R = number of people to the right of X
h = small distance we move X to the right
```

We choose a move that does not pass any person's position in its interior.

### Step 3 — Calculate the increase from the left people

Every left-side person walks `h` more units.

Total increase:

```text
L × h
```

For X between 2 and 3, `L=1` and `h=1`:

```text
1 × 1 = +1
```

### Step 4 — Calculate the decrease from the right people

Every right-side person walks `h` fewer units.

Total decrease:

```text
R × h
```

For our example, `R=2` and `h=1`:

```text
2 × 1 = 2 less distance
```

### Step 5 — Combine the two changes

New total minus old total:

```text
change = L×h - R×h
```

Factor the repeated `h`:

```text
change = h×(L-R)
```

Actual numbers:

```text
change = 1×(1-2)
       = -1
```

This agrees with the table: `6−7=−1`.

### Step 6 — Why does the median follow?

- **Before the median**, more people are on the right: `R>L`. Moving right **reduces** the total.
- **After the median**, more people are on the left: `L>R`. Moving right **increases** the total.
- The point where this changes is a **median**.

**Endpoint detail:** if X sits exactly at a person's position, count that person on the side it is left behind by the movement. For longer moves, divide the move into pieces between consecutive positions. The simple `h(L-R)` expression applies on each such piece.

## 3.4 What happens when N is even?

Positions:

```text
[1, 3, 7, 10]
     ↑  ↑
   middle two
```

| Meeting point X | Total distance |
|---|---:|
| 3 | `2+0+4+7 = 13` |
| 5 | `4+2+2+5 = 13` |
| 7 | `6+4+0+3 = 13` |

Between `3` and `7`, **two people are to the left and two to the right**.

A small move right increases distance for two people and decreases it for two people. These changes cancel.

Therefore **every real X in `[3,7]` minimizes total absolute distance**. If X must be an integer, every integer from 3 through 7 is valid.

The *statistical* median is `(3+7)/2=5`, but for this optimization problem **3, 4, 5, 6, and 7 are all optimal**.

## 3.5 Algorithm and dry run

1. Sort the positions.
2. Choose `a[n/2]` as one valid median (zero-based indexing).
3. Add absolute distances from that median.

| Position | Median | Distance | Running total |
|---|---:|---:|---:|
| 1 | 3 | 2 | 2 |
| 3 | 3 | 0 | 2 |
| 7 | 3 | 4 | **6** |

## 3.6 C++17 (`long long`)

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;
    vector<long long> a(n);
    for (long long &x : a) cin >> x;

    sort(a.begin(), a.end());
    long long median = a[n / 2];

    long long ans = 0;
    for (long long x : a) {
        ans += llabs(x - median);
    }
    cout << ans << '\n';
}
```

**Time:** `O(N log N)`. **Assumptions:** `n≥1`, every `x−median` subtraction and its absolute value fit `long long`, and the sum fits too.

**Recognition:** choose a point on a line + minimize `sum(|X−a[i]|)` → median, **not mean**.

---

# 4. Weighted Median — Where Should Groups Meet?

## 4.1 What is the question asking?

The meeting-place problem is the same, but now a location may represent **more than one person**.

**Goal:** choose X to minimize *everyone's combined walking distance*.

| Location | Number of people (weight) |
|---|---:|
| 1 | 1 |
| 3 | 1 |
| 7 | 3 |

A group of 3 traveling distance 4 contributes `3×4=12` to the total.

## 4.2 Try a few meeting points

| Meet at X | Group at 1 | Group at 3 | Group at 7 | Total |
|---|---:|---:|---:|---:|
| 3 | `1×2=2` | `1×0=0` | `3×4=12` | 14 |
| 5 | `1×4=4` | `1×2=2` | `3×2=6` | 12 |
| **7** | **`1×6=6`** | **`1×4=4`** | **`3×0=0`** | **10** |
| 8 | `1×7=7` | `1×5=5` | `3×1=3` | 15 |

**Observation:** Walking toward the group of 3 helps *three* people at once. Its benefit can outweigh the added distance for two people elsewhere.

## 4.3 Proof — moving right with groups

### Step 1 — See what changes from X=3 to X=7

Meeting point moves `4` units to the right.

| Group at | People | Change **per person** | Total group change |
|---|---:|---:|---:|
| 1 | 1 | +4 | +4 |
| 3 | 1 | +4 | +4 |
| 7 | 3 | −4 | −12 |
| **Total** | | | **−4** |

The total distance falls from `14` to `10`.

### Step 2 — Define weight on each side

Between consecutive occupied locations:

```text
W_L = total number of people to the left
W_R = total number of people to the right
h   = how far X moves right
```

### Step 3 — Work out the total change

The left groups walk farther:

```text
increase = W_L × h
```

The right groups walk less:

```text
decrease = W_R × h
```

Combine them:

```text
change = W_L×h - W_R×h
```

Factor out `h`:

```text
change = h×(W_L-W_R)
```

Actual numbers for the interval from 3 to 7:

```text
W_L = 1+1 = 2
W_R = 3
h   = 4

change = 4×(2-3) = -4
```

This matches `10−14=−4`.

### Step 4 — Why is the weighted median optimal?

- More **total weight** to the right → moving right decreases cost.
- More total weight to the left → moving right increases cost.
- The minimum occurs where **no more than half the total weight lies strictly on either side**.

That location is a **weighted median**.

**Note:** `h(W_L−W_R)` is applied between data positions; handle any occupied endpoints by viewing the movement piecewise.

## 4.4 Find weighted median WITHOUT creating repeated people

**Step 1 — Imagine the repeated positions (for understanding only).**

```text
location 1 has 1 person
location 3 has 1 person
location 7 has 3 people

conceptual positions:
[1, 3, 7, 7, 7]
       ↑
third person (middle) = 7
```

**Step 2 — Count total people.**

```text
W = 1+1+3 = 5
```

**Step 3 — Find the middle person's 1-based index.**

```text
target = ceil(W/2)
       = 3
```

For positive integer weights in code, calculate `target = W/2 + W%2` to avoid overflow from `W+1`.

**Step 4 — Use prefix weights instead of expanding.**

| Location | Weight | Prefix people | Have we reached person #3? |
|---|---:|---:|---|
| 1 | 1 | 1 | No |
| 3 | 1 | 2 | No |
| **7** | **3** | **5** | **Yes** |

First prefix reaching 3 is at location **7**. That is a weighted median.

For even W, this method chooses a valid **lower weighted median**. A whole interval of meeting locations can be equally optimal.

## 4.5 Why prefix weight is equivalent to repeating locations

Expanding `[1,3,7]` with weights `[1,1,3]` gives `[1,3,7,7,7]`.

The unweighted median proof from Pattern 3 already tells us that its middle value minimizes distance.

The prefix sum finds the very same middle person's location **without storing W repeated elements**.

This completes the proof of the algorithm.

## 4.6 Algorithm and C++17 (`long long`)

1. Sort pairs `(position, weight)` by position.
2. Calculate total weight `W>0`.
3. Find the first position with cumulative weight at least `ceil(W/2)`.
4. Calculate weighted distances to that position.

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;
    vector<pair<long long, long long>> a(n); // position, weight

    long long totalWeight = 0;
    for (auto &[x, w] : a) {
        cin >> x >> w;
        totalWeight += w;
    }
    sort(a.begin(), a.end());

    long long target = totalWeight / 2 + totalWeight % 2;
    long long prefix = 0;
    long long median = a.front().first;

    for (auto [x, w] : a) {
        prefix += w;
        if (prefix >= target) {
            median = x;
            break;
        }
    }

    long long ans = 0;
    for (auto [x, w] : a) {
        ans += w * llabs(x - median);
    }
    cout << ans << '\n';
}
```

**Time:** `O(N log N)`. **Assumptions:** `n≥1`, weights positive integers and `W>0`, every difference, product, prefix sum and result fits `long long`.

**Recognition:** minimize weighted sum of absolute distances → weighted median + cumulative weights.

---

# 5. Team Performance — Which K Workers Should We Choose?

## 5.1 What is the question asking?

Each worker has two values:

- `speed` — affects the team's **minimum** speed;
- `efficiency` — is added to teammates' efficiencies.

Choose **exactly K** workers to maximize:

```text
team score = (sum of selected efficiencies)
             × (minimum speed among selected workers)
```

**Example:** Choose exactly `K=2` workers.

| Worker | Speed | Efficiency |
|---|---:|---:|
| A | 6 | 4 |
| B | 4 | 10 |
| C | 2 | 20 |

This is a mathematical score formula; actual team speed need not behave this way in real life.

## 5.2 Understand the goal by checking every team

| Team | Sum efficiencies | Minimum speed | Score |
|---|---:|---:|---:|
| A+B | `4+10=14` | 4 | 56 |
| A+C | `4+20=24` | 2 | 48 |
| **B+C** | **`10+20=30`** | **2** | **60** |

**Observation:** Picking the two fastest workers is not enough. A smaller minimum speed might be compensated by a much larger efficiency sum.

So how can we avoid checking every group of K workers?

## 5.3 Discover the threshold idea

### Step 1 — Pretend we already know the minimum required speed

Suppose a selected team must have speed **at least 4** for every member.

Eligible workers:

```text
A (6,4)
B (4,10)
```

Only A and B qualify. Their score at threshold 4 is:

```text
4 × (4+10) = 56
```

### Step 2 — Lower the speed threshold to 2

Now workers A, B, and C are all eligible.

Choose the **two largest efficiencies**: `10` and `20`.

```text
2 × (10+20) = 60
```

Better answer: `60`.

### Step 3 — Why does fixing speed help?

With **S fixed and nonnegative**, the value we maximize is:

```text
S × (sum of K efficiencies)
```

Since S is fixed, choosing the **largest K efficiencies** maximizes this expression.

No need to optimize speed and efficiency simultaneously at that step.

## 5.4 Sort speed descending and try each threshold

Sort workers by speed from largest to smallest:

```text
A(6,4) → B(4,10) → C(2,20)
```

At each worker, the current speed becomes the minimum **eligible threshold**.

| Speed threshold S | Eligible | Best 2 efficiencies | Threshold score |
|---|---|---|---:|
| 6 | A | only one worker | — |
| 4 | A, B | 4, 10 | `4×14=56` |
| 2 | A, B, C | 10, 20 | **`2×30=60`** |

Answer is **60**.

## 5.5 Why use a min-heap?

We need to keep the **largest K efficiencies** among workers encountered so far.

Let `K=2`:

| Efficiency arrives | Heap contents (shown sorted) | Running sum | Explanation |
|---|---|---:|---|
| 4 | `[4]` | 4 | not enough workers |
| 10 | `[4,10]` | 14 | now we have 2 |
| 20 | `[10,20]` | 30 | inserted 20, discarded smallest 4 |

The heap is a **min-heap**, because we repeatedly need to throw away the smallest kept efficiency.

A heap is **not stored internally in sorted order**; the table sorts values for teaching only.

## 5.6 Proof — why we cannot miss the best team

This pattern uses a **bottleneck-threshold proof**, not a pair swap.

### Step 1 — Imagine the unknown optimal team

Call the true best team `OPT`.

Let:

```text
S* = minimum speed in OPT
E* = sum of efficiencies in OPT
```

Its actual score is:

```text
OPT score = S* × E*
```

In our tiny example the best team is B+C:

```text
S* = 2
E* = 10+20 = 30
OPT score = 2×30 = 60
```

### Step 2 — What happens when the algorithm reaches speed S*?

Every worker in OPT has speed **at least S*** (because S* is OPT's minimum).

Therefore, by the time we reach speed S*, **all workers of OPT are eligible**.

For our numbers, at `S*=2`, workers A, B and C are eligible—including B and C.

### Step 3 — Compare efficiency sums

Our heap keeps the **K largest efficiencies** from all eligible workers.

So its efficiency sum cannot be smaller than the efficiency sum of OPT:

```text
heapSum >= E*
```

Actual numbers:

```text
heapSum = 10+20 = 30
E* = 30
30 >= 30
```

### Step 4 — Multiply by the fixed threshold

Because `S*≥0`, multiplying preserves the comparison:

```text
S*×heapSum >= S*×E*
```

Actual numbers:

```text
2×30 >= 2×30
60 >= 60
```

So at this threshold the algorithm gets a candidate score **at least as large as OPT's score**.

### Step 5 — Is the candidate valid?

The heap contains **K actual eligible workers**, all with speed at least S*.

Their real minimum speed is therefore at least S*.

Because their efficiencies are nonnegative, their **real score** is at least the computed threshold score.

So the algorithm never invents an impossibly high candidate score: every candidate is no greater than the actual score of some real team.

### Step 6 — Conclusion

The scan includes the threshold `S*` belonging to the best team. At that threshold it can match or beat the best team's score, and every candidate corresponds to a feasible team with at least that score.

Therefore the maximum candidate score **equals the true optimum**.

**Equal speeds:** workers with the same speed may be visited in any order; after all workers with speed S have been processed, every worker eligible at that threshold has been considered.

## 5.7 Algorithm and C++17 (`long long`)

1. Sort workers by decreasing speed.
2. Add each efficiency to a min-heap and to a running sum.
3. If heap size exceeds K, remove its smallest efficiency and subtract it from the sum.
4. Once the heap holds K workers, try `currentSpeed × heapSum`.

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, k;
    cin >> n >> k;

    vector<pair<long long, long long>> workers(n); // speed, efficiency
    for (auto &[speed, eff] : workers) cin >> speed >> eff;
    sort(workers.rbegin(), workers.rend()); // speed descending

    priority_queue<long long, vector<long long>, greater<long long>> pq;
    long long sum = 0;
    long long ans = 0;

    for (auto [speed, eff] : workers) {
        pq.push(eff);
        sum += eff;
        if ((int)pq.size() > k) {
            sum -= pq.top();
            pq.pop();
        }
        if ((int)pq.size() == k) {
            ans = max(ans, sum * speed);
        }
    }
    cout << ans << '\n';
}
```

**Time:** `O(N log N + N log K)`. **Assumptions:** `1≤K≤N`, nonnegative speeds and efficiencies, exactly K selected, and all intermediate operations fit `long long`.

**Recognition:** choose exactly K + `(sum of attribute A) × (minimum attribute B)` → sort by B descending + Top-K heap.

---

# 6. Recognition and Revision

## Which greedy proof should I try?

| What the problem asks | First observation | Greedy answer | Proof method |
|---|---|---|---|
| Maximize products after rearranging pairings | Uncross two pairs | Align both sorts | Exchange + factor |
| Order sequential jobs with completion-time loss | One job delays the other | Decreasing `D/T` | Adjacent exchange |
| Choose X minimizing sum of absolute distances | Moving toward majority helps | Median | Left/right movement |
| Choose X minimizing weighted distances | People/weights matter, not distinct points | Weighted median | Weighted movement + prefix weight |
| Select K maximizing sum × minimum | Fix a possible bottleneck | Descending bottleneck + Top-K min-heap | Threshold includes OPT |

## Five compact memory formulas

```text
1. Dot product gain from uncrossing:
   (b-a)(d-c) >= 0

2. Scheduling: A first if its caused delay loss is smaller:
   D_B*T_A <= D_A*T_B
   equivalently D_A/T_A >= D_B/T_B

3. Moving meeting point right (between positions):
   change = h*(count_left-count_right)

4. Weighted movement right (between positions):
   change = h*(weight_left-weight_right)

5. Team score at speed threshold S:
   S * sum(K largest eligible efficiencies)
```

## Self-test — Think before looking at the answer

**1. Dot product:** `A=[1,4]`, `B=[8,2]`. Maximum total?

<details>
<summary>Answer</summary>

Sorted pairing: `1×2+4×8=34`.

</details>

**2. Scheduling:** A has `D=3,T=6`; B has `D=4,T=2`. Which runs first?

<details>
<summary>Answer</summary>

`3/6=0.5`, `4/2=2`; B first.

</details>

**3. Median:** Meet at positions `[1,2,20]`. Best X?

<details>
<summary>Answer</summary>

`X=2`.

</details>

**4. Weighted median:** Positions `[1,5]`, weights `[1,3]`. Best X?

<details>
<summary>Answer</summary>

Expanded conceptually: `[1,5,5,5]`. Meeting at `5` minimizes total weighted distance.

</details>

**5. Team performance:** K=2; workers `(speed,efficiency)` are `(5,3)`, `(3,10)`, `(2,15)`. Best score?

<details>
<summary>Answer</summary>

Choose speeds 3 and 2: `(10+15)×2=50`. Other pairs give `39` and `36`.

</details>

> **Final habit for CF:** Understand the question → Try tiny choices → Identify what changes → Prove one local or threshold choice is safe → Justify global optimality → Code.
