# AlgoZenith Greedy — Class 1
## V9 · Clear variable names, simpler scheduling, TLE-style proofs

> **Goal:** first understand **what the question wants**. Next use **small numbers** to discover a choice. Finally prove why it works for *all* valid inputs.
>
> **Proof layout:** **Question in plain English → Define the data → Small table/timeline → Observation → One proof step at a time → Numbers → General rule.**
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

**Scheduling example:** A takes 5 minutes and loses 2 points each minute. B takes 3 minutes and loses 10 points each minute. To test whether A should go first, ask whether the extra loss caused by A delaying B is smaller than the extra loss caused by B delaying A:

```text
Extra loss if A goes first = lossPerMinuteJobB × durationJobA
                           = 10 × 5 = 50

Extra loss if B goes first = lossPerMinuteJobA × durationJobB
                           = 2 × 3 = 6
```

Since `50 > 6`, **B should go first**. The ratio comparison in the next section is simply another way to express this same test.

## 0.8 Ratio comparison without division

**Question:** How do we compare urgency against job duration without decimal divisions?

| Job | Loss per minute | Duration | Loss-per-minute / duration |
|---|---:|---:|---:|
| A | 2 | 5 | `2/5 = 0.4` |
| B | 10 | 3 | `10/3 ≈ 3.33` |

Suppose we want to check whether job A's ratio is at least B's ratio.

```text
lossPerMinuteJobA / durationJobA
  >= lossPerMinuteJobB / durationJobB
```

Use the real values first:

```text
2/5 >= 10/3 ?
```

Because both durations are positive, multiply both sides by `5 × 3`:

```text
2 × 3 >= 10 × 5 ?
6 >= 50 ?   NO
```

So **job B has the larger ratio** and should go first in this score-decay model. In C++, use cross-products (when they fit `long long`), rather than floating-point comparison.

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

**Real-world example:** A computer can run **only one task at a time**. Every task starts with some points. Until a task finishes, it **loses points every minute**.

**Your decision:** Choose the order of the tasks.

**Your goal:** Finish every task while keeping the **largest combined final score**.

| Meaning | Clear variable name | Job A | Job B |
|---|---|---:|---:|
| Starting points | `baseScoreJobA` / `baseScoreJobB` | 100 | 100 |
| Minutes needed to run | `durationJobA` / `durationJobB` | 5 | 3 |
| Points lost per minute until finished | `lossPerMinuteJobA` / `lossPerMinuteJobB` | 2 | 10 |

**Important:** A task loses points for its **entire completion time**, including time waiting for earlier tasks. All jobs must run; the score formula is linear and may become negative.

For either task:

```text
finalScore = baseScore - lossPerMinute × completionTime
```

For example, if job A completes at minute 5:

```text
finalScoreJobA = 100 - 2 × 5 = 90
```

## 2.2 Try both possible orders — only TWO jobs first

**Choice 1 — A runs before B**

```text
Time:    0 -------- 5 -------- 8
Task:       A (5)       B (3)
Ends:        A           B
```

| Job | Finishes at minute | Points left |
|---|---:|---:|
| A | 5 | `100 − 2×5 = 90` |
| B | 8 | `100 − 10×8 = 20` |
| **Total** | | **110** |

**Choice 2 — B runs before A**

```text
Time:    0 ------ 3 ------------ 8
Task:       B (3)         A (5)
Ends:        B             A
```

| Job | Finishes at minute | Points left |
|---|---:|---:|
| B | 3 | `100 − 10×3 = 70` |
| A | 8 | `100 − 2×8 = 84` |
| **Total** | | **154** |

**Observation:** B first gives **44 more points** (`154 − 110 = 44`). Now we need to prove a rule that also works for other durations and loss rates.

## 2.3 Why can we compare only LOST points?

Every job gets the same **starting points**, whichever order we choose.

```text
baseScoreJobA = 100
baseScoreJobB = 100

totalBaseScore = baseScoreJobA + baseScoreJobB
               = 100 + 100
               = 200
```

The order changes **when each job finishes**, so it changes the **points lost**, not the starting points.

| Order | Loss from A | Loss from B | Total lost | Final score |
|---|---:|---:|---:|---:|
| A then B | `2×5=10` | `10×8=80` | **90** | `200−90=110` |
| B then A | `2×8=16` | `10×3=30` | **46** | `200−46=154` |

**Conclusion:** The starting total is fixed at 200. Therefore:

```text
Maximize final points = Minimize lost points
```

## 2.4 Why do we swap only TWO neighboring jobs?

First, notice that **A and B together always take 8 minutes**, whichever goes first:

```text
A then B takes 5 + 3 = 8 minutes
B then A takes 3 + 5 = 8 minutes
```

Now imagine other tasks on the same computer. Job **Q runs before** A and B, and job **R runs after** A and B. We are **not swapping Q or R**.

| Task | Q | A | B | R |
|---|---:|---:|---:|---:|
| Processing duration (minutes) | 2 | 5 | 3 | 4 |

**Before the swap:**

```text
0 --[Q:2]-- 2 --[A:5]-- 7 --[B:3]-- 10 --[R:4]-- 14
```

**After swapping only A and B:**

```text
0 --[Q:2]-- 2 --[B:3]-- 5 --[A:5]-- 10 --[R:4]-- 14
```

| Job | Finishes before swap | Finishes after swap | Changes? |
|---|---:|---:|---|
| Q | 2 | 2 | No |
| **A** | **7** | **10** | **Yes** |
| **B** | **10** | **5** | **Yes** |
| R | 14 | 14 | No |

**Why?** Q is finished before the pair starts. Both orders of A and B end at minute `2+8=10`, so R starts at minute 10 and finishes at minute 14 either way. **Only A's and B's completion times change.**

This is why we compare **neighboring** jobs. Swapping jobs far apart could change the finishing times of jobs between them.

## 2.5 Discover the greedy rule without long algebra

This is the most important intuition.

Every task must spend its **own processing time** running. That part of its loss happens either way. What changes is the **extra waiting caused by the other task**.

**Choice A first:** B waits for A's 5 minutes.

```text
extraLossWhenAFirst = lossPerMinuteJobB × durationJobA
                    = 10 × 5
                    = 50
```

**Choice B first:** A waits for B's 3 minutes.

```text
extraLossWhenBFirst = lossPerMinuteJobA × durationJobB
                    = 2 × 3
                    = 6
```

| Put first | Extra waiting loss imposed on the other job |
|---|---:|
| A | 50 points |
| **B** | **6 points** |

**Choose B first**, because making A wait costs only 6 extra points, while making B wait costs 50. Difference = `50−6=44`, exactly the score improvement we saw earlier.

## 2.6 Mathematical proof — one idea at a time

We will show that **this extra-waiting comparison works even if other jobs run before A and B**.

### Step 1 — What is common to both orders?

Let `timeBeforePair` mean the number of minutes the computer has already worked before reaching A and B. In our four-job example, Q takes 2 minutes, so:

```text
timeBeforePair = 2
```

Both A and B wait for Q, no matter which goes first. They also each use their own processing time. Those parts of the loss are **identical** in both orders.

Call the total of those identical losses `sharedLoss`:

```text
sharedLoss = loss from both jobs waiting for earlier tasks
           + loss from A's own processing time
           + loss from B's own processing time
```

**Write that with descriptive variables:**

```text
sharedLoss = (lossPerMinuteJobA + lossPerMinuteJobB) × timeBeforePair
           + lossPerMinuteJobA × durationJobA
           + lossPerMinuteJobB × durationJobB
```

**Substitute the numbers:**

```text
sharedLoss = (2 + 10) × 2 + 2 × 5 + 10 × 3
           = 24 + 10 + 30
           = 64
```

### Step 2 — Calculate loss if A runs before B

The **only additional penalty** is making B wait during A's five minutes.

```text
totalLossIfAFirst = sharedLoss + extraLossWhenAFirst
```

**Numerical example:**

```text
totalLossIfAFirst = 64 + 10 × 5
                  = 64 + 50
                  = 114
```

### Step 3 — Calculate loss if B runs before A

Here the additional penalty is making A wait during B's three minutes.

```text
totalLossIfBFirst = sharedLoss + extraLossWhenBFirst
```

**Numerical example:**

```text
totalLossIfBFirst = 64 + 2 × 3
                  = 64 + 6
                  = 70
```

### Step 4 — Compare the choices and cancel the shared part

```text
lossDifference = totalLossIfAFirst - totalLossIfBFirst
               = 114 - 70
               = 44
```

**General form:**

```text
lossDifference = (sharedLoss + extraLossWhenAFirst)
               - (sharedLoss + extraLossWhenBFirst)
```

Since `sharedLoss` appears once with `+` and once with `−`, it cancels:

```text
lossDifference = extraLossWhenAFirst - extraLossWhenBFirst
```

**Replace these names by the meaning of each extra loss:**

```text
lossDifference = (lossPerMinuteJobB × durationJobA)
               - (lossPerMinuteJobA × durationJobB)
```

**Numerical check:** `10×5 − 2×3 = 50 − 6 = 44`.

**Why this matters:** `timeBeforePair` disappeared. The comparison works anywhere in the schedule, not just at the beginning.

### Step 5 — Determine when A should run first

We want to **minimize** loss. So A should run first if the extra loss it causes is no greater than the extra loss B would cause.

```text
extraLossWhenAFirst <= extraLossWhenBFirst
```

Substitute each descriptive formula:

```text
lossPerMinuteJobB × durationJobA
   <= lossPerMinuteJobA × durationJobB
```

**Numerical check:** `50 <= 6` is **false**, so B runs first in this example.

### Step 6 — Turn this into the general sorting rule

Start with the comparison from Step 5:

```text
lossPerMinuteJobB × durationJobA
  <= lossPerMinuteJobA × durationJobB
```

Both durations are **positive**. Divide both sides by `durationJobA × durationJobB`:

```text
lossPerMinuteJobB / durationJobB
  <= lossPerMinuteJobA / durationJobA
```

Read the larger ratio first:

```text
lossPerMinuteJobA / durationJobA
  >= lossPerMinuteJobB / durationJobB
```

**Numbers:** `2/5 = 0.4` for A, and `10/3 ≈ 3.33` for B. So B must come before A.

**Greedy rule: sort jobs by DECREASING `(loss per minute)/(processing duration)`.**

### Step 7 — Why is this rule optimal for all jobs?

Take any schedule with two neighboring jobs in the **wrong ratio order**. From Steps 4–6, swapping this pair cannot increase total loss. From §2.4, jobs outside the pair are unaffected. Repeatedly fix neighboring wrong-order pairs until every job is in decreasing ratio order. Thus the greedy order is optimal, not just a guess.

## 2.7 Solution steps and dry run

1. Sort every job by decreasing `lossPerMinute / duration`.
2. In C++, compare `jobA.lossPerMinute * jobB.duration` with `jobB.lossPerMinute * jobA.duration`, rather than dividing (assuming safe `long long` products).
3. Walk through jobs in the chosen order. Increase completion time by the job's duration and add its final score.

| Job executed | Completion time | Job's final score | Total so far |
|---|---:|---:|---:|
| B | 3 | `100 − 10×3 = 70` | 70 |
| A | 8 | `100 − 2×8 = 84` | **154** |

## 2.8 C++17 with full variable names (`long long`)

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Job {
    long long baseScore;
    long long lossPerMinute;
    long long duration;
};

bool shouldRunFirst(const Job& jobA, const Job& jobB) {
    // Compare loss rates / durations without floating-point division.
    long long firstCrossProduct  = jobA.lossPerMinute * jobB.duration;
    long long secondCrossProduct = jobB.lossPerMinute * jobA.duration;

    if (firstCrossProduct != secondCrossProduct) {
        return firstCrossProduct > secondCrossProduct;
    }
    return jobA.duration < jobB.duration; // either order is optimal for equal ratios
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfJobs;
    cin >> numberOfJobs;

    vector<Job> jobs(numberOfJobs);
    for (Job& job : jobs) {
        cin >> job.baseScore >> job.lossPerMinute >> job.duration;
    }

    sort(jobs.begin(), jobs.end(), shouldRunFirst);

    long long completionTime = 0;
    long long totalFinalScore = 0;

    for (const Job& job : jobs) {
        completionTime += job.duration;
        long long jobFinalScore = job.baseScore - job.lossPerMinute * completionTime;
        totalFinalScore += jobFinalScore;
    }

    cout << totalFinalScore << '\n';
}
```

**Complexity:** `O(N log N)` time. **Assumptions:** duration is positive, every job is completed, score loss is linear even below zero, and all intermediate products/sums fit in `long long`.

**Recognition:** sequential jobs + penalty until completion → compare the **extra waiting loss** of neighboring jobs → sort by decreasing loss-rate/duration.

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

2. Scheduling: choose the order with smaller extra waiting loss.
   A first: lossPerMinuteJobB × durationJobA
   B first: lossPerMinuteJobA × durationJobB
   Sort descending by lossPerMinute / duration.

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
