# AlgoZenith Greedy — Class 1 (V4: Understand the Question First)

> **How to study:** Read the **question** first. Predict an answer using the **small example**. Then see the **step-by-step method**, **proof in simple words**, and **C++17**. All numerical examples are illustrative models of the class patterns, not quotations of a particular contest problem.
>
> **Five forms:** pairing products · ordering decaying jobs · median · weighted median · sum × bottleneck selection.

## Contents

1. [Maximum Dot Product — Who Should Pair With Whom?](#1-maximum-dot-product--who-should-pair-with-whom)
2. [Job Ordering — Which Task Should Run First?](#2-job-ordering--which-task-should-run-first)
3. [Median — Where Should Everyone Meet?](#3-median--where-should-everyone-meet)
4. [Weighted Median — Where Should Groups Meet?](#4-weighted-median--where-should-groups-meet)
5. [Team Performance — Which K People Should We Choose?](#5-team-performance--which-k-people-should-we-choose)
6. [Quick Recognition Card](#6-quick-recognition-card)

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

### Why does it always work? Simple exchange proof

Take any **crossed** pair: `a ≤ b` and `c ≤ d` but pair `a` with `d` and `b` with `c`.

```text
Crossed total = a*d + b*c
Aligned total = a*c + b*d

Aligned − Crossed
= ac + bd − ad − bc
= bd − bc − ad + ac        (reorder)
= b(d−c) − a(d−c)         (factor)
= (b−a)(d−c) ≥ 0
```

Both differences are nonnegative. **Undoing any crossed pair never reduces the answer.** Keep removing crossings until both arrays have the same order: that is why sorting is optimal. Equality is possible when values tie.

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
    __int128 ans = 0;
    for (int i = 0; i < n; ++i) ans += (__int128)a[i] * b[i];
    // This example assumes the answer fits in signed long long.
    cout << (long long)ans << '\n';
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

### Derive the ordering rule (numbers → algebra)

To maximize score, minimize total lost points (`Σ D×finish_time`), because all base scores stay the same. For two neighboring jobs `A` then `B`, compared with `B` then `A`, let `p` be time already spent before them. It cancels out:

```text
Loss(A then B) = DA(p+TA) + DB(p+TA+TB)
Loss(B then A) = DB(p+TB) + DA(p+TB+TA)

First loss − second loss = DB*TA − DA*TB
```

**A first is better** when first loss ≤ second loss:

```text
DB*TA ≤ DA*TB
      ⇔ DA/TA ≥ DB/TB       (positive times)
```

With the example: `2/5 = 0.4` and `10/3 ≈ 3.33`; **B first**. Any adjacent pair in the wrong ratio order can be swapped without making the result worse. Repeating swaps sorts every job into the optimal order.

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
    __int128 x = (__int128)a.d * b.t;
    __int128 y = (__int128)b.d * a.t;
    return x != y ? x > y : a.t < b.t;
}
int main() {
    ios::sync_with_stdio(false); cin.tie(nullptr);
    int n; cin >> n;
    vector<Job> v(n);
    for (auto &j : v) cin >> j.s >> j.d >> j.t;
    sort(v.begin(), v.end(), cmp);
    __int128 time = 0, score = 0;
    for (auto j : v) {
        time += j.t;
        score += (__int128)j.s - (__int128)j.d * time;
    }
    // This example assumes the final score fits in signed long long.
    cout << (long long)score << '\n';
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

### Why does it work? A movement proof

Imagine moving the meeting point **a tiny distance right**, without passing someone's position.

- Each person on the **left** walks that much farther → total cost goes **up**.
- Each person on the **right** walks that much less → total cost goes **down**.

For one unit of movement *not crossing a position*, change in cost is `#left − #right`. Before the median, more people are to the right, so moving right helps; after the median, moving right hurts. The change of direction happens at the middle.

**Even count:** `[1,3,7,10]` has middle values `3` and `7`; every `X` between `3` and `7` has the same minimum total distance (`13`). You may choose either middle value in code.

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
    __int128 cost = 0;
    for (long long v : x) {
        __int128 d = (__int128)v - m;
        cost += d < 0 ? -d : d;
    }
    // This example assumes the answer fits in signed long long.
    cout << (long long)cost << '\n';
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

### Why does it work? Simple proof

Imagine a location with weight 3 as **three people standing there**. The unweighted median proof applies to this conceptual expanded population. Instead of moving one person's distance at a time, moving right changes cost by `weight_left − weight_right` (on stretches that cross no location). Hence cost stops decreasing once at least half the total weight is on each side of a median.

### C++17 (positive integer weights)

```cpp
#include <bits/stdc++.h>
using namespace std;
int main() {
    ios::sync_with_stdio(false); cin.tie(nullptr);
    int n; cin >> n;
    vector<pair<long long,long long>> a(n);
    __int128 W = 0;
    for (auto &[x,w] : a) { cin >> x >> w; W += w; }
    sort(a.begin(), a.end());
    __int128 target = (W+1)/2, pref = 0;
    long long median = a.front().first;
    for (auto [x,w] : a) {
        pref += w;
        if (pref >= target) { median = x; break; }
    }
    __int128 cost = 0;
    for (auto [x,w] : a) {
        __int128 d = (__int128)x - median;
        cost += (__int128)w * (d < 0 ? -d : d);
    }
    // This example assumes the answer fits in signed long long.
    cout << (long long)cost << '\n';
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

### Why does this work? Proof in simple words

1. Imagine the **actual best team** has slowest speed `S`.
2. At the moment the scan reaches speed `S`, **every worker in that best team is eligible** (already scanned).
3. Our heap keeps the **largest K efficiencies** among everyone eligible, so its sum is at least as large as the best team's efficiency sum.
4. Multiplying by the nonnegative threshold `S` means our candidate score is **at least the best team's score** at that step. Since every worker in the heap has speed ≥ `S`, the candidate does not exceed that chosen team's actual performance.

So the scan cannot miss a better team. This is a **fix the bottleneck → optimize the sum** proof, not a swapping proof.

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
    __int128 sum = 0, ans = 0;
    for (auto [speed, eff] : workers) {
        pq.push(eff); sum += eff;
        if ((int)pq.size() > k) { sum -= pq.top(); pq.pop(); }
        if ((int)pq.size() == k) ans = max(ans, sum * speed);
    }
    // This example assumes the answer fits in signed long long.
    cout << (long long)ans << '\n';
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

**Important implementation note:** The examples above use `__int128` for intermediate arithmetic but print by casting to `long long` for brevity. If the problem constraints allow a final result outside signed 64-bit, use a `__int128` decimal-printing helper instead. All sorting/rule assumptions stated within each pattern matter.
