# AlgoZenith Greedy — Class 1 (V5: Prerequisites + Worked Proofs)

> **How to study:** Read the **question** first. Predict an answer using the **small example**. Then see the **step-by-step method**, **proof in simple words**, and **C++17**. All numerical examples are illustrative models of the class patterns, not quotations of a particular contest problem.
>
> **Five forms:** pairing products · ordering decaying jobs · median · weighted median · sum × bottleneck selection.

## Contents

0. [Prerequisites — Tools Used by Every Pattern](#0-prerequisites--tools-used-by-every-pattern)
1. [Maximum Dot Product — Who Should Pair With Whom?](#1-maximum-dot-product--who-should-pair-with-whom)
2. [Job Ordering — Which Task Should Run First?](#2-job-ordering--which-task-should-run-first)
3. [Median — Where Should Everyone Meet?](#3-median--where-should-everyone-meet)
4. [Weighted Median — Where Should Groups Meet?](#4-weighted-median--where-should-groups-meet)
5. [Team Performance — Which K People Should We Choose?](#5-team-performance--which-k-people-should-we-choose)
6. [Quick Recognition Card](#6-quick-recognition-card)

---

# 0. Prerequisites — Tools Used by Every Pattern

These are the **minimum tools** needed to understand the five problems. Read the example before its formula.

| Tool | Meaning in simple words | Tiny example |
|---|---|---|
| Objective function | A number we want largest/smallest | Maximize `2×B[0] + 5×B[1]` |
| Pair contribution | Part of the answer from one position | `2×7=14` |
| Exchange / swap | Switch two choices; see if answer improves | `[7,3] → [3,7]`: `29 → 41` |
| Inversion | Larger element sits before a smaller one | `[1,7,4]`: `7 > 4` |
| Cancellation | Ignore terms unchanged by a swap | `(10+41)-(10+29)=41-29` |
| Completion time | When a task ends, including waiting | A takes 5, B takes 3: A→B finish at `5,8` |
| Ratio comparison | Compare `D/T` without decimals | `2/5 < 10/3` since `2×3 < 10×5` |
| Absolute distance | Distance is never negative | `|3-7|=4` |
| Median | Middle person after sorting | `[1,3,7] → 3` |
| Weighted median | Middle person when positions represent groups | `[1,3,7,7,7] → 7` |
| Prefix weight | Count people up to a location | weights `1,1,3` → prefix `1,2,5` |
| Min-heap Top-K | Keep largest K numbers; remove smallest extra | K=2: `[4,10,20] → [10,20]` |
| Bottleneck | The smallest attribute controls a team | speeds `[6,4]` → minimum is `4` |

### One exchange proof, from start to finish

**Question:** Should we pair small-with-small or cross the partners? Take `A=[2,5]`, `B=[7,3]`.

| Arrangement | Products | Sum |
|---|---|---:|
| Crossed | `2×7 + 5×3` | 29 |
| Aligned | `2×3 + 5×7` | **41** |

1. **Try a swap:** improvement = `41−29=12`.
2. **Explain it algebraically:** `ac+bd−ad−bc = b(d−c)−a(d−c) = (b−a)(d−c)`.
3. **Prove the sign:** if `a≤b` and `c≤d`, both differences are `≥0`, so gain is `≥0`.
4. **Go from local to global:** repeatedly fix inverted partners. Each fix never makes the total worse; eventually both lists have the same order.

**Recognition:** Compare just what changes → subtract OLD from NEW → factor → show the sign → explain why swaps can be repeated.

### How to read the C++ solutions

Every code block uses **`long long` only**, as requested. This is correct **only when inputs, all intermediate products, prefix sums, completion times, and answers fit in signed 64-bit** (`−9.22×10^18` to `+9.22×10^18`). For example, `10^9 × 10^9 = 10^18` fits, but summing `100000` such products does not. Check constraints before submitting; if bounds exceed this, a wider integer type or a problem-specific approach is necessary.

---

# 1. Maximum Dot Product — Who Should Pair With Whom?

### What is the question asking?

You have two lists of numbers `A` and `B`. You **may rearrange B**. Pair the values at matching positions and add their products. **Find the largest possible total.** Think of matching workers' productivity with machine multipliers.

### Example → solve it yourself first

`A = [2, 5]` (fixed), `B = [7, 3]` (can reorder).

| Pairing | First pair | Second pair | Total |
|---|---:|---:|---:|
| Original, crossed | `2×7=14` | `5×3=15` | **29** |
| After swap, aligned | `2×3=6` | `5×7=35` | **41** |

**Observation:** Put the smaller B with the smaller A, and the larger B with the larger A. Gain = `41−29=12`.

### Solution steps

1. Sort `A` ascending.
2. Sort `B` ascending too.
3. Multiply same-index elements and sum them. (For the **minimum**, sort in opposite orders.)

### Proof — why aligned pairing is always at least as good

**Step 1 — Use the same numbers:** small `A=2`, large `A=5`; small `B=3`, large `B=7`.

| Choice | Calculation | Score |
|---|---|---:|
| Crossed | `2×7 + 5×3` | 29 |
| Aligned | `2×3 + 5×7` | **41** |

**Step 2 — Subtract:** `aligned − crossed = 41−29 = 12`.

**Step 3 — Generalize and factor** (`a≤b`, `c≤d`):

```text
Aligned − crossed
= (a*c + b*d) − (a*d + b*c)
= ac + bd − ad − bc
= bd − bc − ad + ac          (reorder)
= b(d−c) − a(d−c)           (common factor)
= (b−a)(d−c)

Numbers: (5−2)(7−3) = 3×4 = 12
```

**Step 4 — Why this is a proof:** `b−a ≥ 0` and `d−c ≥ 0`, so their product is nonnegative for **every** pair. If B has an inversion, swapping that inverted pair never lowers the score. Repeat until B is aligned with sorted A. Thus sorting both the same way is optimal (ties may produce equal scores).

### C++17

```cpp
#include <bits/stdc++.h>
using namespace std;
int main() {
    ios::sync_with_stdio(false); cin.tie(nullptr);
    int n; cin >> n;
    vector<long long> a(n), b(n);
    for (auto &x : a) cin >> x;
    for (auto &x : b) cin >> x;
    sort(a.begin(), a.end());
    sort(b.begin(), b.end());
    long long ans = 0;
    for (int i = 0; i < n; ++i) ans += a[i] * b[i];
    cout << ans << '\n';
}
```

**Recognition:** rearrange two arrays + maximize `Σ A[i]×B[i]` → sort in **same** order. **Complexity:** `O(N log N)`.

---

# 2. Job Ordering — Which Task Should Run First?

### What is the question asking?

One computer runs tasks **one at a time**. Every task gives a base score `S`, but loses `D` points **per minute until it finishes**. Each takes `T` minutes. **Choose an order with the highest total final score.** We assume all tasks must be completed, processing times are positive, and the score formula is linear even if it becomes negative.

### Example → compare two choices

| Task | Time `T` | Score `S` | Loss/min `D` |
|---|---:|---:|---:|
| A | 5 min | 100 | 2 |
| B | 3 min | 100 | 10 |

`Finish time` includes time waiting behind earlier jobs. `Score = S − D × finish_time`.

```text
Order A → B:   0 --[ A:5 ]-- 5 --[ B:3 ]-- 8
Order B → A:   0 --[ B:3 ]-- 3 --[ A:5 ]-- 8
```

| Order | A finishes / score | B finishes / score | Total |
|---|---|---|---:|
| A → B | `5 → 100−2×5=90` | `8 → 100−10×8=20` | **110** |
| B → A | `8 → 100−2×8=84` | `3 → 100−10×3=70` | **154** |

**Observation:** B loses points much faster and is short. Do **B before A** to improve the total by `44`.

### Why compare only two *adjacent* jobs?

Suppose there are also tasks Q (2 min) and R (4 min):

```text
OLD: 0 [Q] 2 [A] 7 [B] 10 [R] 14
NEW: 0 [Q] 2 [B] 5 [A] 10 [R] 14
```

| Job | OLD finish | NEW finish | Did it change? |
|---|---:|---:|---|
| Q | 2 | 2 | No |
| A | 7 | 10 | **Yes** |
| B | 10 | 5 | **Yes** |
| R | 14 | 14 | No |

Only A and B finish at different times. So **to judge this swap, compare only A and B**. Q and R cancel. This is the *exchange proof* idea.

### Proof — derive the best job order step by step

**Step 1 — Try both orders with numbers.** From the table above, `A→B` scores `110`, while `B→A` scores `154`. Therefore `B→A` wins by `44`.

**Step 2 — Remove base scores.** Both orders earn the same base `S_A+S_B=200`. Only the **lost points** differ:

| Order | A's loss | B's loss | Total loss |
|---|---:|---:|---:|
| A→B | `2×5=10` | `10×8=80` | **90** |
| B→A | `2×8=16` | `10×3=30` | **46** |

So maximize final score = minimize `Σ(D × finish time)`.

**Step 3 — Put variables in place.** Let `T_A,T_B` be durations, `D_A,D_B` be loss per minute, and `p` be the time taken by earlier jobs:

```text
Loss(A→B) = DA(p+TA) + DB(p+TA+TB)
Loss(B→A) = DB(p+TB) + DA(p+TB+TA)

Loss(A→B) − Loss(B→A)
= DA*p + DA*TA + DB*p + DB*TA + DB*TB
  − DB*p − DB*TB − DA*p − DA*TB − DA*TA
= DB*TA − DA*TB             (all shared terms cancel)

Numbers: 10×5 − 2×3 = 50−6 = 44
```

**Step 4 — Find the rule.** `A` should go first when its loss is no larger:

```text
DB*TA − DA*TB <= 0
DB*TA <= DA*TB
DA/TA >= DB/TB      (TA, TB are positive)
```

For our input, `2/5 < 10/3`, so **B first**.

**Step 5 — Prove for all jobs.** Swap any two **neighboring** jobs violating descending `D/T`. Earlier jobs are unchanged; later jobs finish at the same time because the pair's total duration is unchanged. The swap cannot worsen total score. Repeat until all jobs follow descending `D/T`.

### Solution steps

1. Compute the idea `D/T` for each task; sort by **decreasing `D/T`**.
2. Compare `Da*Tb` and `Db*Ta` instead of dividing (avoids floating-point error).
3. Walk through the sorted tasks: add their `T` to cumulative time, then add `S − D×time` to score.

### C++17

```cpp
#include <bits/stdc++.h>
using namespace std;
struct Job { long long s, d, t; };
bool cmp(const Job& a, const Job& b) {
    long long x = a.d * b.t;
    long long y = b.d * a.t;
    return x != y ? x > y : a.t < b.t;
}
int main() {
    ios::sync_with_stdio(false); cin.tie(nullptr);
    int n; cin >> n;
    vector<Job> v(n);
    for (auto &j : v) cin >> j.s >> j.d >> j.t;
    sort(v.begin(), v.end(), cmp);
    long long time = 0, score = 0;
    for (auto j : v) {
        time += j.t;
        score += j.s - j.d * time;
    }
    cout << score << '\n';
}
```

**Recognition:** reorder sequential jobs + linear penalty based on **completion time** → compare two neighboring jobs → sort by `D/T`. **Complexity:** `O(N log N)`.

---

# 3. Median — Where Should Everyone Meet?

### What is the question asking?

People live at numbered positions on a straight road. Pick **one meeting point X** so their **total walking distance is as small as possible**. Everyone counts equally.

### Example

Positions: `[1, 3, 7]`.

| Meet at X | Distances | Total |
|---|---|---:|
| 1 | `0+2+6` | 8 |
| **3** | `2+0+4` | **6** |
| 7 | `6+4+0` | 10 |

**Observation:** The best meeting point is the **middle position** (median), not necessarily the average.

### Solution steps

1. Sort positions.
2. Pick `x[n/2]` (0-based): one valid median for either odd or even `n`.
3. Answer = `Σ |x[i] − median|`.

### Proof — see the cost change, then derive it

**Step 1 — Use the example** `[1,3,7]`. The cost of meeting at `X=2` is `1+1+5=7`; at `X=3` it is `2+0+4=6`.

| Person at | Distance at X=2 | Distance at X=3 | Change |
|---|---:|---:|---:|
| 1 | 1 | 2 | +1 |
| 3 | 1 | 0 | −1 |
| 7 | 5 | 4 | −1 |
| **Total** | **7** | **6** | **−1** |

**Step 2 — General rule:** move the meeting point right by a small distance `h`, **without crossing any person's position**. Every left-side person adds `+h`, every right-side person adds `−h`.

```text
newCost − oldCost = h × (#left − #right)
At X=2, h=1:        1 × (1 − 2) = −1
```

**Step 3 — Why the median:** Before the middle there are more people on the right, so moving right reduces cost. After the middle there are more on the left, so moving right increases cost. At the middle, the direction changes, giving the minimum.

**Even count:** `[1,3,7,10]` has two middle values `3,7`. For `X` anywhere between them, two people are on each side, so movement does not change total cost: `F(3)=F(5)=F(7)=13`. Pick either middle value in code.

### C++17

```cpp
#include <bits/stdc++.h>
using namespace std;
int main() {
    ios::sync_with_stdio(false); cin.tie(nullptr);
    int n; cin >> n;
    vector<long long> x(n);
    for (auto &v : x) cin >> v;
    sort(x.begin(), x.end());
    long long m = x[n/2];
    long long cost = 0;
    for (long long v : x) cost += llabs(v - m);
    cout << cost << '\n';
}
```

**Recognition:** minimize `Σ|X − position[i]|` → **median**. (Mean is used for squared distances.) **Complexity:** `O(N log N)`.

---

# 4. Weighted Median — Where Should Groups Meet?

### What is the question asking?

Like the previous problem, but some locations have **more people**. Choose a meeting point so the **total distance walked by all individuals** is smallest.

### Example

| Location | Number of people (weight) |
|---|---:|
| 1 | 1 |
| 3 | 1 |
| 7 | 3 |

Think of the positions as `[1, 3, 7, 7, 7]` (just to understand; **do not create this array in code**).

| Meet at X | People at 1 | People at 3 | 3 people at 7 | Total |
|---|---:|---:|---:|---:|
| 3 | `1×2=2` | `1×0=0` | `3×4=12` | 14 |
| **7** | `1×6=6` | `1×4=4` | `3×0=0` | **10** |

**Observation:** Location `7` has a majority of the people, so meeting there helps the most.

### Solution steps without expanding weights

Total weight = `1+1+3=5`. Middle person's index in a 1-based expanded list = `(5+1)/2 = 3`.

| Location | Weight | Cumulative people |
|---|---:|---:|
| 1 | 1 | 1 |
| 3 | 1 | 2 |
| **7** | 3 | **5 ← first to reach person #3** |

1. Sort `(position, weight)` by position.
2. Compute total weight `W`; target middle person's position = `(W+1)/2` for positive integer weights.
3. Scan cumulative weight. The **first position with prefix weight ≥ target** is a weighted median.
4. Compute `Σ weight[i] × |X − position[i]|`.

### Proof — weighted movement with actual numbers

**Step 1 — Interpret weights as people:** `[1,3,7]` with weights `[1,1,3]` means people at `[1,3,7,7,7]`.

**Step 2 — Move meeting point from `3` to `7`** (4 units):

| Group | People | Change per person | Total change |
|---|---:|---:|---:|
| At 1 | 1 | +4 | +4 |
| At 3 | 1 | +4 | +4 |
| At 7 | 3 | −4 | −12 |
| **Total** | | | **−4** |

So cost changes from `14` to `10`.

**Step 3 — Derive the rule:** for a small rightward movement `h` that crosses no group location,

```text
newCost − oldCost = h × (weight_left − weight_right)
For the open interval (3,7):
weight_left=1+1=2, weight_right=3
h=4: change=4×(2−3)=−4
```

If more weight is to the right, moving right decreases cost; if more is to the left, it increases cost. Therefore the minimum occurs at a **weighted median** (no more than half the total weight strictly on either side).

**Step 4 — Find it without expanding:** total weight `W=5`, target `(W+1)/2=3`. Prefix weights are `1,2,5`; the first prefix reaching `3` is at location **7**. For even integer total weight, this finds one valid weighted median.

### C++17 (positive integer weights)

```cpp
#include <bits/stdc++.h>
using namespace std;
int main() {
    ios::sync_with_stdio(false); cin.tie(nullptr);
    int n; cin >> n;
    vector<pair<long long,long long>> a(n);
    long long W = 0;
    for (auto &[x,w] : a) { cin >> x >> w; W += w; }
    sort(a.begin(), a.end());
    long long target = W/2 + W%2, pref = 0;
    long long median = a.front().first;
    for (auto [x,w] : a) {
        pref += w;
        if (pref >= target) { median = x; break; }
    }
    long long cost = 0;
    for (auto [x,w] : a) cost += w * llabs(x - median);
    cout << cost << '\n';
}
```

**Recognition:** minimize `Σ weight[i]×|X−position[i]|` → **weighted median** using cumulative weights. **Complexity:** `O(N log N)`.

---

# 5. Team Performance — Which K People Should We Choose?

### What is the question asking?

You have workers, each with a `speed` and an `efficiency`. Choose **exactly K workers** to maximize:

`team score = (sum of chosen efficiencies) × (minimum chosen speed)`.

Think of efficiency as contributions that **add up**, while the slowest speed is the team's **bottleneck**. Here speed and efficiency are nonnegative.

### Example: choose K=2

| Worker | Speed | Efficiency |
|---|---:|---:|
| A | 6 | 4 |
| B | 4 | 10 |
| C | 2 | 20 |

Try every team to understand the question:

| Team | Efficiency sum | Min speed | Score |
|---|---:|---:|---:|
| A+B | 14 | 4 | 56 |
| A+C | 24 | 2 | 48 |
| **B+C** | **30** | **2** | **60** |

**Observation:** Fastest workers do not automatically give the best score. A lower minimum speed may be compensated by much greater efficiency.

### How to solve for large N (step by step)

**Step 1: Sort by speed decreasing:** `A(6,4), B(4,10), C(2,20)`.

**Step 2: Pretend current speed is the minimum speed allowed.** Everyone already scanned has speed at least this threshold.

**Step 3: Among those people, keep the K biggest efficiencies** with a min-heap. Calculate `current_speed × heap_sum` when heap size becomes K.

| Speed threshold | Eligible workers | Best 2 efficiencies | Candidate score |
|---|---|---|---:|
| 6 | A | only 1 worker | — |
| 4 | A, B | 4, 10 | `4×14=56` |
| 2 | A, B, C | 10, 20 | `2×30=60` |

Answer = **60**.

### Proof — fix the slowest worker, then maximize the rest

**Step 1 — Test the example:** at speed threshold `4`, eligible workers are A,B. Their best two efficiencies are `4,10`, yielding `4×(4+10)=56`. At threshold `2`, eligible are A,B,C; the top two are `10,20`, yielding `2×(10+20)=60`.

**Step 2 — Write the optimization:** for any team of K people,

```text
Performance = (sum of efficiencies) × (minimum speed)
```

The minimum makes direct selection difficult. Instead, **fix a candidate threshold S**. All eligible workers have `speed >= S`. For that fixed S, maximizing `S × efficiency_sum` means taking the largest K efficiencies (assuming `S >= 0`).

**Step 3 — Why scanning all thresholds is enough:** suppose the truly optimal team has minimum speed `S*` and efficiency sum `E*`.

```text
At threshold S*:
all members of the optimal team are eligible
heapSum >= E*                    (heap keeps top K efficiencies)
S* × heapSum >= S* × E*          (because S* >= 0)
```

The heap's chosen team has actual minimum speed **at least** `S*`, so its real score is at least the threshold score. Thus the scan cannot miss a better answer.

**Step 4 — Data structure:** sorting speeds descending visits thresholds; a size-K **min-heap** discards the smallest efficiency whenever K+1 people have been seen. Note: this proof assumes **nonnegative speed and efficiency** and **exactly K** members.

### Why a *min*-heap?

If K=2 and efficiencies arrive `[4,10,20]`:

```text
push 4  -> [4]         (not enough)
push 10 -> [4,10]      sum 14
push 20 -> [4,10,20]   too many; pop smallest 4
           [10,20]     sum 30
```

It is easy to remove the smallest whenever more than K items are present.

### C++17 (K exactly, nonnegative attributes)

```cpp
#include <bits/stdc++.h>
using namespace std;
int main() {
    ios::sync_with_stdio(false); cin.tie(nullptr);
    int n, k; cin >> n >> k;
    vector<pair<long long,long long>> workers(n); // speed, efficiency
    for (auto &[s,e] : workers) cin >> s >> e;
    sort(workers.rbegin(), workers.rend()); // speed descending
    priority_queue<long long, vector<long long>, greater<long long>> pq;
    long long sum = 0, ans = 0;
    for (auto [speed, eff] : workers) {
        pq.push(eff); sum += eff;
        if ((int)pq.size() > k) { sum -= pq.top(); pq.pop(); }
        if ((int)pq.size() == k) ans = max(ans, sum * speed);
    }
    cout << ans << '\n';
}
```

**Recognition:** choose K + `(sum of attribute A) × (minimum of attribute B)` → sort by B descending + min-heap of top K A. **Complexity:** `O(N log N + N log K)`.

---

# 6. Quick Recognition Card

| Question words | Your first observation | Rule |
|---|---|---|
| Rearrange pairings to maximize sum of products | Crossed pairs can be uncrossed | Sort both same order |
| Run all tasks; score decreases until completion | Only adjacent jobs change finish times | Sort `D/T` decreasing |
| Pick X minimizing everyone's distance | Moving toward majority helps | Median |
| Pick X minimizing weighted distances | More people = stronger pull | Weighted median |
| Choose K; score = sum × minimum | Fix weakest attribute first | Sort bottleneck ↓ + top-K heap |

**The six questions to ask in a contest:** (1) What can I choose/change? (2) What exactly am I maximizing/minimizing? (3) Try 2–3 items. (4) Compare two choices. (5) Why will the same argument hold for all items? (6) Only then code.

**Implementation assumption:** The C++ snippets deliberately use `long long` for clarity. Verify all intermediate arithmetic (not just final answers) stays in 64-bit range; otherwise `long long` is unsafe.
