# AlgoZenith Greedy — Class 1
## V6 · Detailed prerequisites, question-first solutions, and line-by-line proofs

> **Purpose:** Understand *what the question is asking*, discover the greedy choice using small numbers, and then **derive and prove** the rule rather than memorizing it.
>
> **Reading order for each problem:** Question → Input/goal → Tiny example → What changes? → Observation → Numerical proof → General algebra (every step) → Why local implies global → C++17.
>
> The scenarios below are **teaching examples** for the five mathematical patterns, not copied contest statements. Code uses **`long long` throughout**, as requested; see §0.14 for required overflow assumptions.

## Clickable contents

- [0. Prerequisites — Learn These Before Greedy Proofs](#0-prerequisites--learn-these-before-greedy-proofs)
  - [0.1 Problem statement → variables → objective](#01-problem-statement--variables--objective)
  - [0.2 Greedy choice versus greedy proof](#02-greedy-choice-versus-greedy-proof)
  - [0.3 Contributions and unchanged terms](#03-contributions-and-unchanged-terms)
  - [0.4 Exchange proof and adjacent swaps](#04-exchange-proof-and-adjacent-swaps)
  - [0.5 Inversions and sorted order](#05-inversions-and-sorted-order)
  - [0.6 Factoring four terms](#06-factoring-four-terms)
  - [0.7 Inequalities and sign changes](#07-inequalities-and-sign-changes)
  - [0.8 Ratios and cross multiplication](#08-ratios-and-cross-multiplication)
  - [0.9 Processing time, waiting time, completion time](#09-processing-time-waiting-time-completion-time)
  - [0.10 Absolute distance and median](#010-absolute-distance-and-median)
  - [0.11 Weights and prefix weights](#011-weights-and-prefix-weights)
  - [0.12 Top-K and the min-heap](#012-top-k-and-the-min-heap)
  - [0.13 Bottleneck versus additive sum](#013-bottleneck-versus-additive-sum)
  - [0.14 `long long` limits and assumptions](#014-long-long-limits-and-assumptions)
- [1. Maximum Dot Product — Who Pairs With Whom?](#1-maximum-dot-product--who-pairs-with-whom)
- [2. Job Scheduling — Which Job Should Finish Earlier?](#2-job-scheduling--which-job-should-finish-earlier)
- [3. Median — Where Should People Meet?](#3-median--where-should-people-meet)
- [4. Weighted Median — Where Should Groups Meet?](#4-weighted-median--where-should-groups-meet)
- [5. Team Performance — Which K Workers Should Be Chosen?](#5-team-performance--which-k-workers-should-be-chosen)
- [6. All Five Patterns — Recognition and Revision](#6-all-five-patterns--recognition-and-revision)

---

# 0. Prerequisites — Learn These Before Greedy Proofs

## 0.1 Problem statement → variables → objective

**The first question is NOT “which algorithm?” It is “what is being optimized?”**

**Mini question:** Two workers have strengths `2` and `5`. Two machines have multipliers `7` and `3`. We may choose which worker gets which machine. Maximize total output.

| Story word | Variable | Actual values |
|---|---|---|
| Worker strengths | `A[i]` | `[2,5]` |
| Machine multipliers | `B[i]` | `[7,3]` |
| Output of one pair | `A[i] × B[i]` | `2×7=14` |
| Total output | `sum(A[i]×B[i])` | `14+15=29` |
| Allowed decision | Rearrange machines (`B`) | `[7,3]` or `[3,7]` |
| Goal | **Maximize** total output | Larger sum wins |

**Model:** `answer = Σ A[i] × B[i]`. No sorting rule yet; first identify what can change and what cannot.

**Recognition habit:** underline the **decision** (`rearrange B`) and the **objective** (`maximum sum of products`).

## 0.2 Greedy choice versus greedy proof

A **greedy choice** is what we *try*. A **proof** is why that choice cannot miss a better solution.

Example:

| Candidate choice | Calculation | Score |
|---|---|---:|
| Pair `2` with `7`, `5` with `3` | `2×7 + 5×3` | 29 |
| Pair `2` with `3`, `5` with `7` | `2×3 + 5×7` | **41** |

- **Observation from numbers:** matching small with small improved this example.
- **Not yet a proof:** other numbers might behave differently.
- **Proof plan:** replace these four numbers by `a ≤ b` and `c ≤ d`; prove the aligned arrangement is **never worse**.

A correct greedy explanation needs both the small experiment *and* a general argument.

## 0.3 Contributions and unchanged terms

**Question:** Why can we compare only two changed positions instead of recomputing the entire answer?

Fix `A = [1,2,5,8]` and swap only the middle two partners in `B`.

| Position | A | B before | Product before | B after | Product after |
|---|---:|---:|---:|---:|---:|
| 1 | 1 | 3 | 3 | 3 | 3 |
| **2** | 2 | **7** | **14** | **4** | **8** |
| **3** | 5 | **4** | **20** | **7** | **35** |
| 4 | 8 | 6 | 48 | 6 | 48 |
| **Total** | | | **85** | | **94** |

**Every arithmetic step of cancellation:**

```text
After - Before
= (3 + 8 + 35 + 48) - (3 + 14 + 20 + 48)
= (3 - 3) + (8 - 14) + (35 - 20) + (48 - 48)
= 0 + (-6) + 15 + 0
= +9
```

Or calculate **only affected terms**:

```text
new pair:  2×4 + 5×7 =  8 + 35 = 43
old pair:  2×7 + 5×4 = 14 + 20 = 34
gain = 43 - 34 = 9
```

**Lesson:** when the objective is a sum of contributions and a change affects only two contributions, all others cancel. **Be careful:** in scheduling, a nonadjacent swap can affect jobs *between* the swapped jobs; we therefore use **adjacent** swaps.

## 0.4 Exchange proof and adjacent swaps

An exchange proof starts with a supposedly optimal solution and transforms it into the greedy structure *without making its value worse*.

```text
Some valid solution
      |
      v
Find a pair violating the greedy order
      |
      v
Swap the pair
      |
      v
Prove new answer >= old (for maximization)
      |
      v
Repeat until no bad pair remains
      |
      v
Greedy-ordered solution is at least as good
```

**Tiny example:** `A=[2,5]`, `B=[7,3]`. Swapping the partners changes `29 → 41`. The algebra in §0.6 proves this is safe for *all* ordered pairs, not only these values.

**Why adjacent for jobs?** Suppose a single CPU handles `Q=2`, `A=5`, `B=3`, `R=4` minutes.

```text
OLD: 0 [ Q ] 2 [    A    ] 7 [ B ] 10 [  R  ] 14
NEW: 0 [ Q ] 2 [ B ] 5 [    A    ] 10 [  R  ] 14
```

| Job | Finishes OLD | Finishes NEW | Changed? |
|---|---:|---:|---|
| Q | 2 | 2 | No |
| A | 7 | 10 | **Yes** |
| B | 10 | 5 | **Yes** |
| R | 14 | 14 | No |

The pair takes `5+3=8` minutes in *both* orders. Later job `R` starts at `10` either way. **Only A and B matter for this comparison.**

## 0.5 Inversions and sorted order

An **inversion** in an ascending array means an earlier element exceeds a later element.

```text
B before = [1, 7, 4, 9]
                ^  ^
                7 > 4    <-- inversion
B after  = [1, 4, 7, 9]
```

If `A=[1,2,5,8]` is already sorted:

| | Before | After |
|---|---:|---:|
| A=2 paired with | 7 → `14` | 4 → `8` |
| A=5 paired with | 4 → `20` | 7 → `35` |
| Pair sum | 34 | **43** |

Gain: `43−34 = (5−2)(7−4) = 9`.

**Why does repeated swapping reach sorted order?** Every unsorted array contains an **adjacent inversion**. Swapping an adjacent inversion removes at least that inversion. Keep doing it: eventually no adjacent inversion remains, so the array is sorted. If each swap is safe, a sorted optimum exists.

## 0.6 Factoring four terms

This is the algebra tool behind the **maximum dot product** proof.

**Start with numbers** (aligned minus crossed):

```text
(2×3 + 5×7) - (2×7 + 5×3)
= 6 + 35 - 14 - 15
= 12
```

**Now derive a formula, one operation per line.** Assume `a≤b` and `c≤d`.

| Algebra | Why? | Match to numbers |
|---|---|---|
| `(ac+bd)−(ad+bc)` | aligned − crossed | `(6+35)−(14+15)` |
| `ac+bd−ad−bc` | remove brackets | `6+35−14−15` |
| `bd−bc−ad+ac` | rearrange terms | `35−15−14+6` |
| `b(d−c)−a(d−c)` | factor out `b`, then `−a` | `5(7−3)−2(7−3)` |
| `(b−a)(d−c)` | factor `(d−c)` | `(5−2)(7−3)` |
| `≥ 0` | both brackets nonnegative | `3×4=12` |

The important skill is **not memorizing the final formula**; it is spotting the repeated difference `(d−c)` and factoring it.

## 0.7 Inequalities and sign changes

You need these operations when deriving which job goes first.

**Rule A — Adding/removing same term** does not change comparison:

```text
7 + 5 <= 10 + 5   <=>   7 <= 10
```

**Rule B — Multiply by a negative number: reverse inequality:**

```text
-8 <= -3
multiply both sides by -1:
 8 >= 3          (notice reversal)
```

**Rule C — Divide by a positive number: direction stays unchanged:**

```text
6 >= 4
6/2 >= 4/2  --> 3 >= 2
```

**Worked algebra relevant to scheduling:**

```text
D_B*T_A - D_A*T_B <= 0
            D_B*T_A <= D_A*T_B     (add D_A*T_B)
            D_A*T_B >= D_B*T_A     (swap sides)
                D_A/T_A >= D_B/T_B (divide by T_A*T_B > 0)
```

**Numbers:** `D_A=2,T_A=5,D_B=10,T_B=3` gives `50−6 = 44 > 0`, so the condition for A to go first **fails**.

## 0.8 Ratios and cross multiplication

**Question:** Do we sort jobs by `D` (loss per minute), `T` (time), or their ratio `D/T`?

For two jobs `A` and `B` with **positive times**:

```text
D_A/T_A >= D_B/T_B
multiply by positive T_A*T_B:
D_A*T_B >= D_B*T_A
```

**Tiny example:**

| Job | D | T | D/T |
|---|---:|---:|---:|
| A | 2 | 5 | `0.4` |
| B | 10 | 3 | `3.333...` |

Compare **without decimals**:

```text
A before B?  2×3 >= 10×5 ?
             6 >= 50 ? NO
so B before A.
```

**Why not compare with `double`?** Multiplication of bounded integers can preserve an exact comparison. Floating-point ratios can introduce rounding. **But:** `D*T` must fit in `long long` if using `long long` products.

## 0.9 Processing time, waiting time, completion time

**Real-world question:** A computer executes jobs one at a time. When does each job actually finish?

- `T` (processing time) = how long the job itself runs.
- `waiting` = total processing time of jobs before it.
- `C` (completion time) = `waiting + T`.

| Order A→B | Waiting | Duration | Completion |
|---|---:|---:|---:|
| A | 0 | 5 | `0+5=5` |
| B | 5 | 3 | `5+3=8` |

| Order B→A | Waiting | Duration | Completion |
|---|---:|---:|---:|
| B | 0 | 3 | `0+3=3` |
| A | 3 | 5 | `3+5=8` |

```text
A then B:  0 [===== A =====] 5 [=== B ===] 8
B then A:  0 [=== B ===] 3 [===== A =====] 8
```

**Prefix sum observation:** completion times are **running sums of durations**. If order is `Q(2), A(5), B(3)`, running times are `2, 7, 10`.

**Critical distinction:** both orders finish all work at time `8`, but *different jobs* finish first. This matters when each job loses a different amount per minute.

## 0.10 Absolute distance and median

**Question:** Three friends live along a straight road at `1, 3, 7`. Where should they meet to minimize *combined walking distance*?

Absolute distance: `|X−a|` (never negative). Example `|3−7|=4`.

| Meet X | From 1 | From 3 | From 7 | Total |
|---|---:|---:|---:|---:|
| 1 | 0 | 2 | 6 | 8 |
| 2 | 1 | 1 | 5 | 7 |
| **3** | **2** | **0** | **4** | **6** |
| 4 | 3 | 1 | 3 | 7 |

**Why moving right changes cost:** at `X=2`, there is one person to the left (`1`) and two to the right (`3,7`). Move from `X=2` to `X=3`:

```text
person at 1: +1 extra distance
person at 3: -1 less distance
person at 7: -1 less distance
change = +1 -1 -1 = -1
```

If a rightward move of length `h` **crosses no person's location**:

```text
change in total distance
= (+h for each left person) + (-h for each right person)
= h × (#left - #right)
```

The cost decreases while **more people are to the right** and increases once **more people are to the left**. The turning region is the **median**. At a person's exact location, handle the one-sided movement; do not apply the open-interval formula blindly across multiple positions.

**Even count:** `[1,3,7,10]` has middle values `3` and `7`. Any meeting point in `[3,7]` yields the same minimum total `13`. Statistical median `(3+7)/2=5` is one choice, not the only minimizer.

## 0.11 Weights and prefix weights

**Question:** What if one location represents **three people**, not one?

| Location | People (weight) | People seen so far (prefix weight) |
|---|---:|---:|
| 1 | 1 | 1 |
| 3 | 1 | 2 |
| 7 | 3 | 5 |

Think *conceptually* of `[1,3,7,7,7]`, so the middle person (#3) is at **7**.

**How to avoid expanding:** total weight `W=1+1+3=5`. Middle 1-based index is `(W+1)/2=3`. Prefix weights `1,2,5`; first prefix ≥3 occurs at **location 7**.

**Weighted distance:** `weight × distance`.

```text
Meet at X=3:  1×2 + 1×0 + 3×4 = 14
Meet at X=7:  1×6 + 1×4 + 3×0 = 10
```

A weighted median balances **weight**, not just number of distinct locations. The general proof appears in Pattern 4.

## 0.12 Top-K and the min-heap

**Question:** As more candidates arrive, how do we efficiently keep the best **K** efficiencies?

Example: `K=2`, efficiencies arrive `4, 10, 20`.

| Arrives | Temporarily have | Remove? | Kept (shown sorted) | Sum |
|---|---|---|---|---:|
| 4 | `[4]` | No; only 1 | `[4]` | 4 |
| 10 | `[4,10]` | No; exactly 2 | `[4,10]` | 14 |
| 20 | `[4,10,20]` | Remove smallest `4` | `[10,20]` | **30** |

Use a **min-heap** because the smallest of the currently kept candidates sits at `top()` and is easiest to discard.

```cpp
priority_queue<long long, vector<long long>, greater<long long>> pq;
long long sum = 0;

pq.push(value);
sum += value;
if ((int)pq.size() > K) {
    sum -= pq.top();
    pq.pop();
}
```

**Why it keeps the largest K (tiny proof):** before a new value, the heap has the best K seen so far. After inserting one value, the best K among `K+1` candidates are obtained by dropping the smallest. Earlier discarded values cannot suddenly become better than the kept values. Repeating preserves the property.

*The table shows heap elements sorted for readability; its internal array is not sorted.*

## 0.13 Bottleneck versus additive sum

Some objectives combine two different types of information:

```text
team score = (sum of chosen efficiencies) × (minimum chosen speed)
```

| Team | Efficiencies added | Minimum speed | Score |
|---|---:|---:|---:|
| Workers (6,4) and (4,10) | `4+10=14` | 4 | 56 |
| Workers (4,10) and (2,20) | `10+20=30` | 2 | **60** |

- **Additive component:** efficiencies combine by summation.
- **Bottleneck component:** a single slow worker determines the minimum.
- We cannot independently maximize both parts: faster teams may have smaller efficiency sums.

**Breakthrough:** try a candidate **minimum allowed speed S**. Among everyone with speed ≥ S, choose the K largest efficiencies. Sweep S from high to low.

**Important assumption:** speed and efficiency are nonnegative and exactly K workers are required. Full correctness proof appears in Pattern 5.

## 0.14 `long long` limits and assumptions

All implementations in this note deliberately use **`long long`** (signed 64-bit).

| Expression | Must fit in signed `long long` |
|---|---|
| Dot product | Each `A[i]×B[i]`, and every running sum |
| Scheduling comparator | `D_A×T_B`, `D_B×T_A` |
| Scheduling score | Cumulative time, `D×completion`, total score |
| Median | Every coordinate difference and sum of absolute distances |
| Weighted median | Total/prefix weight, `weight×distance`, total cost |
| Team performance | Heap sum, `(heap sum)×speed` |

`long long` maximum is approximately `9.22×10^18`. One `10^9×10^9=10^18` product fits; a sum of `100000` such products **does not**. Ensure every intermediate operation, not only the final answer, fits the type. These are **constraint-dependent examples**: if bounds are larger, use a wider type or a different safe method for that specific contest problem.

---

# 1. Maximum Dot Product — Who Pairs With Whom?

## 1.1 What is the question asking?

We have two equally sized arrays. We may rearrange which `B` value pairs with each `A` value. **Return the largest total of pair products.**

**Real-world model:** assign machine multipliers to workers to maximize combined productivity.

**Input model:** `A=[2,5]`, `B=[7,3]`. **Expected maximum = 41**.

## 1.2 Solve the tiny example BEFORE the formula

| Pairing | Worker 2 | Worker 5 | Total |
|---|---:|---:|---:|
| Original | `2×7=14` | `5×3=15` | 29 |
| Swapped | `2×3=6` | `5×7=35` | **41** |

```text
BEFORE (crossed pairing): 2 ---> 7
                          5 ---> 3       sum = 29
AFTER  (aligned pairing): 2 ---> 3
                          5 ---> 7       sum = 41
```

**Observation:** smaller with smaller and larger with larger seems better. **Why ALWAYS?**

## 1.3 Proof — every algebra step + matching numbers

Let `a≤b` be the two A values; `c≤d` the two B values.

**Step 1 — Name both possibilities.**

```text
Crossed = a*d + b*c           example = 2×7 + 5×3 = 29
Aligned = a*c + b*d           example = 2×3 + 5×7 = 41
```

**Step 2 — Subtract to measure improvement.**

```text
Gain = Aligned - Crossed       example = 41 - 29 = +12
     = (ac+bd) - (ad+bc)
```

**Step 3 — Expand the minus bracket.**

```text
= ac + bd - ad - bc           numbers: 6 + 35 - 14 - 15
```

**Step 4 — Rearrange so a common difference appears.**

```text
= bd - bc - ad + ac           numbers: 35 - 15 - 14 + 6
```

**Step 5 — Factor each pair.**

```text
= b(d-c) - a(d-c)             numbers: 5(7-3) - 2(7-3)
```

**Step 6 — Factor the shared bracket.**

```text
= (b-a)(d-c)                 numbers: (5-2)(7-3)=3×4=12
```

**Step 7 — Prove the sign, NOT just the numerical example.**

```text
b >= a  --> b-a >= 0
 d >= c --> d-c >= 0
therefore (b-a)(d-c) >= 0
```

So **aligned ≥ crossed for every ordered pair**, including negatives; strict improvement occurs when both inequalities are strict.

**Step 8 — Why does this prove sorting ALL N elements?** Sort A ascending. Whenever the B partners contain an adjacent inversion (`B[i]>B[i+1]`), exchange them. Since `A[i]≤A[i+1]`, our proof says the exchange does not decrease the total. Repeated exchanges remove every inversion, leaving B ascending. Therefore a maximum exists with the two arrays sorted the same way.

## 1.4 Algorithm and dry run

```text
1. Sort A ascending.
2. Sort B ascending.
3. Add A[i]*B[i] for every i.
```

| i | A sorted | B sorted | Product | Running sum |
|---|---:|---:|---:|---:|
| 0 | 2 | 3 | 6 | 6 |
| 1 | 5 | 7 | 35 | **41** |

**For minimum dot product:** match one array ascending and the other descending (the opposite exchange argument).

## 1.5 C++17 (`long long`)

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;
    vector<long long> a(n), b(n);
    for (long long &v : a) cin >> v;
    for (long long &v : b) cin >> v;

    sort(a.begin(), a.end());
    sort(b.begin(), b.end());

    long long answer = 0;
    for (int i = 0; i < n; ++i) {
        answer += a[i] * b[i];
    }
    cout << answer << '\n';
}
```

**Complexity:** `O(N log N)` time, `O(log N)` typical sort stack space. **Edge cases:** repeated values give equal-scoring swaps; negative values still obey the theorem; check overflow bounds.

**CF recognition:** “Rearrange pairings” + “maximize sum of products” → **rearrangement inequality / exchange proof**.

---

# 2. Job Scheduling — Which Job Should Finish Earlier?

## 2.1 What is the question asking?

A single computer runs jobs **one after another**. Every job has:

| Symbol | Meaning | Job A | Job B |
|---|---|---:|---:|
| `S` | Base score if finished at time 0 | 100 | 100 |
| `D` | Points lost **per minute until finishing** | 2 | 10 |
| `T` | Minutes the job needs to run | 5 | 3 |
| `C` | Time when job *finishes* | Depends on order | Depends on order |

**Score of job i:** `S[i] − D[i]×C[i]`.

**Goal:** process **every job**, in an order that maximizes the **sum of final scores**. The score formula is linear and is allowed to become negative; times are positive.

**First insight:** job score depends on **finish time**, not just its own processing duration.

## 2.2 Try BOTH orders with numbers

```text
ORDER A then B:    0 [===== A =====] 5 [=== B ===] 8
ORDER B then A:    0 [=== B ===] 3 [===== A =====] 8
```

| Order | A finishes | A score | B finishes | B score | Total |
|---|---:|---:|---:|---:|---:|
| A → B | 5 | `100−2×5=90` | 8 | `100−10×8=20` | **110** |
| B → A | 8 | `100−2×8=84` | 3 | `100−10×3=70` | **154** |

**Observation:** B before A wins by `154−110=44` because B loses points quickly and finishes in just 3 minutes. But “high D first” alone is NOT the general formula. We need a proof that considers **both D and T**.

## 2.3 Simplify the objective before deriving

For two jobs:

```text
TotalScore = (S_A − D_A*C_A) + (S_B − D_B*C_B)
           = (S_A + S_B) − (D_A*C_A + D_B*C_B)
```

`S_A+S_B = 100+100 = 200` in **both** orders. So maximize total score ⇔ **minimize total lost points**.

| Order | A loses | B loses | Total loss | Score = 200−loss |
|---|---:|---:|---:|---:|
| A → B | `2×5=10` | `10×8=80` | 90 | 110 |
| B → A | `2×8=16` | `10×3=30` | **46** | **154** |

Instead of comparing full scores, compare only losses; all base `S` terms cancel.

## 2.4 Why comparing adjacent A and B is enough

Imagine other jobs: `Q=2 min` before the pair, `R=4 min` after it.

```text
OLD: 0 [Q] 2 [===== A =====] 7 [=== B ===] 10 [R] 14
NEW: 0 [Q] 2 [=== B ===] 5 [===== A =====] 10 [R] 14
```

| Job | OLD finishes | NEW finishes | Score changes? |
|---|---:|---:|---|
| Q | 2 | 2 | No |
| A | 7 | 10 | **Yes** |
| B | 10 | 5 | **Yes** |
| R | 14 | 14 | No |

The pair still occupies `5+3=8` minutes. Jobs before/after the adjacent pair have the same completion times. The relative-order decision is determined only by A's and B's penalties.

Let `p` be the total processing time of *all jobs before* the pair. In this diagram, **p = 2**. We retain it in the algebra to prove it cancels.

## 2.5 Derivation A — numerical comparison with a nonzero prefix

**Why include p?** A proof with `p=0` only shows the first two jobs. We need the rule to work anywhere in a long schedule.

At `p=2`:

| Pair order | Completion of A | A loss | Completion of B | B loss | Pair loss |
|---|---:|---:|---:|---:|---:|
| A → B | `2+5=7` | `2×7=14` | `2+5+3=10` | `10×10=100` | **114** |
| B → A | `2+3+5=10` | `2×10=20` | `2+3=5` | `10×5=50` | **70** |

Difference: `114−70=44`. Compare with `p=0`: `90−46=44`. **The same 44-point advantage appears no matter when A and B begin.**

## 2.6 Derivation B — every algebra step, with numbers alongside

Set `T_A=5`, `D_A=2`, `T_B=3`, `D_B=10`, `p=2`.

**Step 1 — Write completion times.**

```text
A then B: C_A = p+T_A        = 2+5   = 7
          C_B = p+T_A+T_B    = 2+5+3 = 10

B then A: C_B = p+T_B        = 2+3   = 5
          C_A = p+T_B+T_A    = 2+3+5 = 10
```

**Step 2 — Write losses using `loss = D×C`.**

```text
L_AB = D_A(p+T_A) + D_B(p+T_A+T_B)
     = 2(2+5) + 10(2+5+3) = 14+100 = 114

L_BA = D_B(p+T_B) + D_A(p+T_B+T_A)
     = 10(2+3) + 2(2+3+5) = 50+20 = 70
```

**Step 3 — Expand brackets one at a time.**

```text
L_AB = D_A*p + D_A*T_A + D_B*p + D_B*T_A + D_B*T_B
     =    4  +    10   +   20   +    50   +    30     = 114

L_BA = D_B*p + D_B*T_B + D_A*p + D_A*T_B + D_A*T_A
     =   20  +    30   +   4    +    6    +    10     = 70
```

**Step 4 — Subtract and cancel matching terms explicitly.**

```text
L_AB - L_BA
= (D_A*p - D_A*p)            --> 0    (4-4)
+ (D_B*p - D_B*p)            --> 0    (20-20)
+ (D_A*T_A - D_A*T_A)        --> 0    (10-10)
+ (D_B*T_B - D_B*T_B)        --> 0    (30-30)
+ D_B*T_A - D_A*T_B          --> 50-6
= D_B*T_A - D_A*T_B
= 10×5 - 2×3
= 44
```

**Meaning:** only the *extra delay* each job causes to the other survives. The shared start time `p`, both base scores, and both jobs' own processing-time penalties cancel.

**Step 5 — Decide when A should come before B.** Since we **minimize loss**:

```text
A first is at least as good
iff L_AB <= L_BA
iff L_AB - L_BA <= 0
iff D_B*T_A - D_A*T_B <= 0
iff D_B*T_A <= D_A*T_B
iff D_A*T_B >= D_B*T_A
iff D_A/T_A >= D_B/T_B    (divide by T_A*T_B > 0)
```

**Map to numbers:** `D_A/T_A=2/5=0.4` and `D_B/T_B=10/3≈3.33`; A fails the condition, so **B should go first**.

**Step 6 — Explain the ratio in words.** `D/T` balances **urgency (`D`)** against **how long the job occupies the CPU (`T`)**. Bigger urgency per unit of processing time belongs earlier.

**Step 7 — Prove optimality for N jobs.** If two adjacent jobs violate decreasing `D/T`, swapping them cannot increase total loss (Step 5). All other jobs are unaffected (§2.4). Repeated swaps eliminate these ratio inversions, producing a schedule sorted by **decreasing `D/T`** without worsening the answer.

**Tie:** if `D_A/T_A = D_B/T_B`, difference is zero, so either order is equally good.

## 2.7 Algorithm and worked dry run

1. Sort jobs by decreasing `D/T`, but use integer comparison `a.d*b.t > b.d*a.t`.
2. Walk in sorted order, maintaining `time += T` (cumulative finish time).
3. Add `S − D×time` for each job.

Our sorted order is B then A:

| Run | T | D | Completion `time` | Score added | Total score |
|---|---:|---:|---:|---:|---:|
| B | 3 | 10 | 3 | `100−10×3=70` | 70 |
| A | 5 | 2 | 8 | `100−2×8=84` | **154** |

## 2.8 C++17 (`long long`)

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Job {
    long long s, d, t;
};

bool cmp(const Job &a, const Job &b) {
    long long left = a.d * b.t;
    long long right = b.d * a.t;
    if (left != right) return left > right;
    return a.t < b.t; // deterministic tie-break
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;
    vector<Job> jobs(n);
    for (Job &j : jobs) cin >> j.s >> j.d >> j.t;

    sort(jobs.begin(), jobs.end(), cmp);

    long long finishTime = 0;
    long long answer = 0;
    for (const Job &j : jobs) {
        finishTime += j.t;
        answer += j.s - j.d * finishTime;
    }
    cout << answer << '\n';
}
```

**Complexity:** `O(N log N)` time. **Assumptions:** `T>0`, all jobs required, linear score as defined (even if negative), and every `long long` multiplication/sum fits. A different score model can require a different order.

**CF recognition:** choose order of sequential jobs + penalty depends linearly on **completion** → **adjacent exchange** → compare `D/T`.

---

# 3. Median — Where Should People Meet?

## 3.1 What is the question asking?

Friends live at positions along **one straight road**. Choose **one meeting point X** to minimize the sum of everyone's distances. Each person counts **once**.

**Input model:** `[1,3,7]`. **Return:** an optimal location `X=3`; minimum total distance `6`.

## 3.2 Tiny example with a table

| Meeting at X | Distance from 1 | From 3 | From 7 | Total |
|---|---:|---:|---:|---:|
| 1 | 0 | 2 | 6 | 8 |
| 2 | 1 | 1 | 5 | 7 |
| **3** | **2** | **0** | **4** | **6** |
| 4 | 3 | 1 | 3 | 7 |
| 7 | 6 | 4 | 0 | 10 |

**Observation:** the middle position `3` minimizes the sum, even though the mean is `(1+3+7)/3 = 11/3`.

## 3.3 Proof — movement, step by step

Define `F(X)=|X−1|+|X−3|+|X−7|`.

**Step 1 — Compare moving from X=2 to X=3.**

| Person lives at | Distance at 2 | Distance at 3 | Change |
|---|---:|---:|---:|
| 1 | 1 | 2 | `+1` |
| 3 | 1 | 0 | `−1` |
| 7 | 5 | 4 | `−1` |
| **Sum** | **7** | **6** | **−1** |

Two people benefit while one loses: moving right makes total distance smaller.

**Step 2 — Generalize the small move.** Let `L` be number of people strictly left of X and `R` number strictly right, for a move of `h>0` that **does not cross any person's position**.

```text
New cost - old cost
= (L people × +h) + (R people × -h)
= L*h - R*h
= h*(L-R)
```

At `X=2`, `L=1, R=2, h=1`:

```text
Δ = 1×(1−2) = −1
F(3) = F(2) + Δ = 7−1 = 6
```

**Step 3 — What changes after passing the middle person?** Move from `X=3` to `X=4`. Now the person at `3` is on the left for the rightward movement:

```text
left people = {1,3} -> L=2
right people = {7} -> R=1
Δ = 1×(2−1) = +1
F(4) = F(3)+1 = 7
```

Cost decreases **up to** the middle and increases **after** it.

**Step 4 — Why this is a general proof.** With sorted locations, as X moves to the right, the number of people on the left can only increase and the number on the right can only decrease. Thus the slope `L−R` changes from negative to zero/positive once we reach the middle location(s). A global minimum occurs at a **median**.

**Step 5 — Even N (important special case).** For `[1,3,7,10]`:

```text
X=3: |3-1|+|3-3|+|3-7|+|3-10| = 2+0+4+7 = 13
X=5: |5-1|+|5-3|+|5-7|+|5-10| = 4+2+2+5 = 13
X=7: |7-1|+|7-3|+|7-7|+|7-10| = 6+4+0+3 = 13
```

Between `3` and `7`, exactly two people are left and two are right; `L−R=0`, so all X in **[3,7]** give the same minimum. For integer X, any integer `3..7` works; choosing `a[n/2]` selects the upper median, `7`.

## 3.4 Algorithm and dry run

1. Sort positions.
2. Pick one median: `m=a[n/2]` (0-based; works for odd or even n).
3. Sum `|a[i]−m|`.

| Position | Median `m=3` | Distance | Running sum |
|---|---:|---:|---:|
| 1 | 3 | 2 | 2 |
| 3 | 3 | 0 | 2 |
| 7 | 3 | 4 | **6** |

## 3.5 C++17 (`long long`)

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
    long long answer = 0;
    for (long long x : a) {
        answer += llabs(x - median);
    }
    cout << answer << '\n';
}
```

**Complexity:** `O(N log N)` time. **Assumptions:** `n≥1`; all coordinate differences and total distance fit in `long long` (subtraction must fit *before* calling `llabs`).

**CF recognition:** choose a point on a line + minimize `Σ |X−a[i]|` → **median**, not mean.

---

# 4. Weighted Median — Where Should Groups Meet?

## 4.1 What is the question asking?

We still choose one meeting location X to minimize total walking distance. This time each location has **a group of people**. A location with weight `w=3` stands for **three** people, so its distance must be counted three times.

**Input:** positions `[1,3,7]`, weights `[1,1,3]`. **Return:** `X=7`, minimum weighted distance `10`.

## 4.2 Understand weights using actual people

| Location | Number of people | Contribution if meet at X |
|---|---:|---|
| 1 | 1 | `1×|X−1|` |
| 3 | 1 | `1×|X−3|` |
| 7 | 3 | `3×|X−7|` |

**Conceptually:** positions are `[1,3,7,7,7]`; do **not** physically expand them in the algorithm.

| Meeting X | People at 1 | At 3 | At 7 | Total |
|---|---:|---:|---:|---:|
| 3 | `1×2=2` | `1×0=0` | `3×4=12` | 14 |
| 5 | `1×4=4` | `1×2=2` | `3×2=6` | 12 |
| **7** | **`1×6=6`** | **`1×4=4`** | **`3×0=0`** | **10** |
| 8 | `1×7=7` | `1×5=5` | `3×1=3` | 15 |

**Observation:** moving toward the group of 3 helps *three* people at once; it outweighs the extra travel for the two others.

## 4.3 Proof A — derive the weighted cost change

Let `F(X) = Σ w[i]×|X−x[i]|`.

**Step 1 — Move from X=3 to X=7 (distance 4).**

| Group at | Weight | Change per person | Group's total change |
|---|---:|---:|---:|
| 1 | 1 | `+4` | +4 |
| 3 | 1 | `+4` | +4 |
| 7 | 3 | `−4` | −12 |
| **Total** | **5** | | **−4** |

Thus `F(7)−F(3)=−4` and `14→10`.

**Step 2 — General move of length h.** Suppose the small move crosses no location. Let `W_L` = sum of weights on the left, `W_R` = sum on the right.

```text
NewCost - OldCost
= (+h for each of W_L people) + (-h for each of W_R people)
= W_L*h - W_R*h
= h*(W_L - W_R)
```

**Step 3 — Substitute example on interval (3,7).**

```text
W_L = 1+1 = 2
W_R = 3
h   = 7−3 = 4
Δ   = 4×(2−3) = −4
```

**Step 4 — Why weighted median?** As X passes locations from left to right, weight moves from the right side to the left side. The cost slopes go from negative (`W_L<W_R`) to positive (`W_L>W_R`). The minimum is at a point with **no more than half of total weight strictly on either side**. This is the *weighted median*.

**Endpoint detail:** at a location, the group's own weight is counted on the side it moves away from for a one-sided step. Formula `h(W_L−W_R)` is safest on open stretches between locations; calculate endpoint crossings piecewise.

## 4.4 Proof B — find the weighted median with prefix weights

**Step 1 — Expand only in your imagination:** `[1,3,7,7,7]` has 5 people; 3rd person is at 7.

**Step 2 — Count weight instead of making 5 copies:**

```text
W = 1+1+3 = 5
middle person's 1-based index = ceil(W/2) = 3
```

| x | w | Prefix sum | Has the middle person been reached? |
|---|---:|---:|---|
| 1 | 1 | 1 | No: `1<3` |
| 3 | 1 | 2 | No: `2<3` |
| **7** | **3** | **5** | **Yes: `5≥3`** |

So **weighted median = 7**. For even positive integer total weight, choosing the first prefix reaching `ceil(W/2)` selects a valid lower weighted median; there may be an entire optimal interval.

**Why is this equivalent to median?** Repeating each point conceptually `w` times reduces the problem to ordinary median. The prefix calculation identifies the middle person's location **without constructing the repeated array**.

## 4.5 Algorithm and C++17 (`long long`)

1. Sort `(x,w)` by x.
2. Add weights to get `W`, set `target = W/2 + W%2` (safe integer ceiling, avoiding `W+1` overflow).
3. Scan prefix weights; the first x with `prefix≥target` is a weighted median.
4. Compute `Σ w×|x−median|`.

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

    long long answer = 0;
    for (auto [x, w] : a) {
        answer += w * llabs(x - median);
    }
    cout << answer << '\n';
}
```

**Complexity:** `O(N log N)` time, `O(N)` storage. **Assumptions:** `n≥1`, weights are positive integers, W>0, and all differences/products/sums fit in `long long`. Weights 0 can be supported if total weight is positive, but all-zero input has no unique meaningful median.

**CF recognition:** minimize **weighted** sum of absolute distances → **weighted median / sort + prefix weights**.

---

# 5. Team Performance — Which K Workers Should Be Chosen?

## 5.1 What is the question asking?

There are N workers, each with two values: `speed` and `efficiency`. Choose **exactly K** workers to maximize:

```text
Performance = (sum of selected efficiencies)
              × (minimum selected speed)
```

**Real-world intuition:** skills add up, but the slowest team speed limits the combined result. This is a *mathematical model*: use the exact formula given in the problem.

**Input example:** choose K=2 among:

| Worker | Speed | Efficiency |
|---|---:|---:|
| A | 6 | 4 |
| B | 4 | 10 |
| C | 2 | 20 |

## 5.2 Brute force for three workers — understand what to maximize

| Choose team | Efficiency sum | Minimum speed | Product |
|---|---:|---:|---:|
| A+B | `4+10=14` | `min(6,4)=4` | `14×4=56` |
| A+C | `4+20=24` | `min(6,2)=2` | `24×2=48` |
| **B+C** | **`10+20=30`** | **`min(4,2)=2`** | **`30×2=60`** |

**Observation:** taking the fastest two gives 56, but choosing B and C gives **60**. Neither “take K fastest” nor “take K most efficient” is a general proof of the optimum.

## 5.3 Discover the threshold trick, one question at a time

**Question 1 — Why not directly optimize sum and minimum simultaneously?** Choosing a worker can increase efficiency sum but decrease the minimum speed. The two factors pull in different directions.

**Question 2 — What if we FIX a candidate minimum allowed speed S?** Only workers with `speed≥S` are eligible. Since S is now fixed and nonnegative, the best *threshold score* `S×sumEfficiencies` comes from the **largest K efficiencies among eligible workers**.

**Question 3 — Which thresholds must we try?** The actual minimum speed of any team equals the speed of at least one chosen worker, so check the workers' speed values.

Sort speeds descending: `A(6,4)`, `B(4,10)`, `C(2,20)`.

| Current S | Eligible | Largest 2 efficiencies | Top-2 sum | Threshold score |
|---|---|---|---:|---:|
| 6 | A | Only 1 worker | — | — |
| 4 | A,B | 4,10 | 14 | `4×14=56` |
| 2 | A,B,C | 10,20 | 30 | **`2×30=60`** |

Maximum = **60**. The top K values can be maintained with a min-heap as each new worker becomes eligible.

## 5.4 Why the min-heap is exactly what we need

Let K=2 and efficiencies arrive `4,10,20`.

| Insert | Heap after removing excess smallest | Heap sum |
|---|---|---:|
| 4 | `[4]` | 4 (not enough workers) |
| 10 | `[4,10]` | 14 |
| 20 | `[10,20]` (discard `4`) | **30** |

**Small proof for the data structure:** if you already hold the K largest efficiencies among processed workers, then after the next value arrives you need only compare it against the smallest held. If it is bigger, replace the smallest; if it is smaller, discard it. A min-heap supports that choice quickly.

## 5.5 General correctness proof — threshold S* and optimum team

The proof is **not** an adjacent-swap proof. It works by considering the bottleneck of the unknown optimal solution.

**Step 1 — Imagine an unknown truly best team**, call it OPT. Let:

```text
S* = minimum speed among OPT's K workers
E* = sum of efficiencies of OPT's K workers
OPT score = S* × E*
```

**Step 2 — What happens when our descending-speed scan reaches S*?** Every worker in OPT has speed ≥ S*, so **every OPT worker has already become eligible**.

**Step 3 — What does our heap know?** It keeps the **K largest efficiencies among all eligible workers**, not just those in OPT. Therefore:

```text
heapSum >= E*
```

**Step 4 — Multiply by S*.** Since S* is nonnegative:

```text
S* × heapSum >= S* × E* = OPT score
```

This means our scan considers a threshold score at least as large as the unknown optimum's score.

**Step 5 — Is the threshold score valid, or did we overestimate?** The K workers currently in the heap all have speed ≥ S*. Therefore their **actual** minimum speed is at least S*, so their **actual** team score is **at least** `S*×heapSum`, assuming efficiencies are nonnegative. Every recorded threshold score is thus a *lower bound on the actual score of some feasible team*, never an overestimate.

**Step 6 — Conclusion.** At threshold S* we obtain a threshold score ≥ OPT, and every threshold score is ≤ some feasible team's actual score ≤ OPT. Therefore the best threshold score equals OPT. Our algorithm is optimal.

**Numerical mapping:** If OPT is B+C, then `S*=2`, `E*=10+20=30`, score `2×30=60`. At S=2 our heap indeed contains efficiencies `10,20` and evaluates 60.

**Handling equal speeds:** scan workers with equal speeds in any order; by the time the last worker with that speed is included, every candidate with that speed is eligible. The proof still applies.

## 5.6 Algorithm and C++17 (`long long`)

1. Sort `(speed,efficiency)` descending by speed.
2. Push each efficiency into a **min-heap** and keep the heap size ≤K by removing its smallest element.
3. Maintain `sum` of heap efficiencies.
4. Whenever heap size is K, try `sum × currentSpeed`.

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, k;
    cin >> n >> k;
    vector<pair<long long, long long>> workers(n); // speed, efficiency
    for (auto &[s, e] : workers) cin >> s >> e;
    sort(workers.rbegin(), workers.rend()); // descending speed

    priority_queue<long long, vector<long long>, greater<long long>> minHeap;
    long long sum = 0;
    long long answer = 0;

    for (auto [speed, eff] : workers) {
        minHeap.push(eff);
        sum += eff;
        if ((int)minHeap.size() > k) {
            sum -= minHeap.top();
            minHeap.pop();
        }
        if ((int)minHeap.size() == k) {
            answer = max(answer, sum * speed);
        }
    }
    cout << answer << '\n';
}
```

**Complexity:** `O(N log N + N log K)` time, `O(N+K)` space for workers and heap. **Assumptions:** `1≤K≤N`, speed/efficiency nonnegative, exactly K chosen, and all intermediate products/sums fit in `long long`.

**CF recognition:** exact K selections + **(sum of attribute 1) × (minimum of attribute 2)** → fix the bottleneck threshold + top-K min-heap.

---

# 6. All Five Patterns — Recognition and Revision

| What a new problem is asking | First thing to test | Greedy result | Proof type |
|---|---|---|---|
| Rearrange pairings to maximize Σ products | Swap a crossed pair | Align sorted orders | Factored pair gain ≥0 |
| Order jobs to maximize linear time-decayed score | Compare adjacent jobs | Descending `D/T` | Swap loss = `D_B*T_A−D_A*T_B` |
| Pick meeting point minimizing total distance | Move X slightly left/right | Ordinary median | Cost change `h(L−R)` |
| Pick meeting point minimizing weighted distance | Treat weights as repeated people | Weighted median | Cost change `h(W_L−W_R)` |
| Pick K for (sum efficiencies)×(minimum speed) | Fix candidate speed threshold | Descending speed + top-K heap | Threshold hits OPT's bottleneck |

## Universal proof-writing template for contests

```text
1. QUESTION: What can I choose? What must be maximized/minimized?
2. SMALL INPUT: Try two options; write the actual totals.
3. MODEL: Replace story words by variables.
4. COMPARE: new − old (or loss1 − loss2).
5. CANCEL: Which contributions are identical?
6. DERIVE: Expand brackets → group → factor → sign/inequality.
7. JUSTIFY: Why can the local argument be repeated for all N?
8. IMPLEMENT: Sort/heap/prefix, and check type constraints.
```

## Quick self-test (try without looking at the answers)

1. `A=[1,4]`, `B=[8,2]`. Which pairing maximizes the sum? **Answer:** `1×2+4×8=34`, because aligned ≥ crossed.
2. Jobs A: `(D=3,T=6)` and B: `(D=4,T=2)`. Who goes first? **Answer:** B (`4/2=2 > 3/6=0.5`).
3. Locations `[1,2,20]`. Where meet? **Answer:** `2` (median).
4. Positions `[1,5]`, weights `[1,3]`. Which weighted median? **Answer:** `5`.
5. Workers `(speed,eff)=(5,3),(3,10),(2,15)`, K=2. Highest score? **Answer:** team with speeds 3 and 2 gives `(10+15)×2=50`; other teams: `(3+10)×3=39`, `(3+15)×2=36`.

> **Final idea:** examples build intuition; algebra proves the choice; the local-to-global argument explains why the greedy algorithm finds the optimum. **Don't memorize only the sorting direction.**
