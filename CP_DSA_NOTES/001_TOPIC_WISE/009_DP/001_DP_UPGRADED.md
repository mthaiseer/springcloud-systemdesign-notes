# 001 DP Master Guide for CP / DSA — Upgraded

> Same file structure, upgraded with:
>
> - deeper explanation of each DP form
> - step-by-step working for every problem
> - top-down + bottom-up C++ code
> - index-by-index dry runs with plain tree-like diagrams
> - no Mermaid, so GitHub/Markdown rendering should be safe

---

## Clickable Index

- [0. The Universal DP Framework](#0-the-universal-dp-framework)
- [1. How to Classify DP Problems by Form](#1-how-to-classify-dp-problems-by-form)
- [2. DP Coding Templates](#2-dp-coding-templates)
- [3. Form 0 — Fibonacci / Stairs](#3-form-0--fibonacci--stairs)
- [4. Form 1 — Subset Sum Possible](#4-form-1--subset-sum-possible)
- [5. Form 1 — 0/1 Knapsack](#5-form-1--01-knapsack)
- [6. Form 1 — Vacation / No Consecutive Same Activity](#6-form-1--vacation--no-consecutive-same-activity)
- [7. Form 1 — Boredom / Delete and Earn](#7-form-1--boredom--delete-and-earn)
- [8. Form 2 — LIS O(N²)](#8-form-2--lis-on²)
- [9. Form 2 — LIS O(N-log-N)](#9-form-2--lis-on-log-n)
- [10. Form 2 — Palindrome Partition Minimum Parts](#10-form-2--palindrome-partition-minimum-parts)
- [11. Form 3 — LCS](#11-form-3--lcs)
- [12. Form 3 — Edit Distance](#12-form-3--edit-distance)
- [13. Form 4 — Palindrome Table](#13-form-4--palindrome-table)
- [14. Form 4 — Matrix Chain Multiplication](#14-form-4--matrix-chain-multiplication)
- [15. Form 5 — Divisor Game](#15-form-5--divisor-game)
- [16. Grid DP — Minimum Path Sum](#16-grid-dp--minimum-path-sum)
- [17. Bitmask DP — TSP Minimum Cost](#17-bitmask-dp--tsp-minimum-cost)
- [18. Tree DP — Tree Diameter](#18-tree-dp--tree-diameter)
- [19. Final One-Page Revision](#19-final-one-page-revision)

---

# 0. The Universal DP Framework

Use this for **every DP problem**.

```text
Step 1: Identify the form.
Step 2: Decide the state.
Step 3: Write exact state meaning.
Step 4: Write recurrence.
Step 5: Decide base cases.
Step 6: Decide invalid/pruning cases.
Step 7: Count states and transitions.
Step 8: Code top-down first.
Step 9: Convert to bottom-up if needed.
```

## The most important DP sentence

```text
State must contain enough information to answer the future independently.
```

Example:

```text
Wrong: dp(i) when the answer also depends on remaining capacity.
Right: dp(i, capLeft).
```

## Top-down mental model

```text
Ask the question recursively.
Cache repeated answers.
```

## Bottom-up mental model

```text
Fill smaller/deeper states first.
Then build bigger states from them.
```

## Time complexity formula

```text
TC = number of states * transition cost per state
```

---

# 1. How to Classify DP Problems by Form

| Form | Recognition clue | Common state | Main thinking |
|---|---|---|---|
| Form 0 | simple recurrence / stairs | `dp(x)` | answer from nearby states |
| Form 1 | choose / not choose | `dp(i, rem)` | take or skip current item |
| Form 2 | best ending at index | `dp(i)` | answer ending exactly at `i` |
| Form 3 | two strings / matching | `dp(i, j)` | match / skip / edit pointers |
| Form 4 | interval / substring | `dp(l, r)` | solve inside interval |
| Form 5 | two-player game | `dp(state)` | winning if opponent can be forced to lose |
| Grid DP | move in matrix | `dp(r, c)` | answer from neighboring cells |
| Bitmask DP | small `n`, subsets | `dp(mask, last)` | visited set + current position |
| Tree DP | subtree relation | `dp(node)` | combine child answers |

## Master decision tree

```text
Problem asks subset/items/choose?       -> Form 1
Problem asks answer ending at i?        -> Form 2
Problem has two strings/arrays?         -> Form 3
Problem asks substring/interval?        -> Form 4
Problem has two players alternating?    -> Form 5
Problem is grid movement?               -> Grid DP
Problem has n <= 20 and visited subset? -> Bitmask DP
Problem input is tree/subtree?          -> Tree DP
Otherwise: dry run small input and find repeated state.
```

---

# 2. DP Coding Templates

## Top-down template

```cpp
int rec(State state) {
    // 1. pruning / invalid state
    if (invalid(state)) return INVALID_VALUE;

    // 2. base case
    if (finished(state)) return BASE_VALUE;

    // 3. cache check
    if (dp[state] != UNVISITED) return dp[state];

    // 4. transitions
    int ans = INITIAL_VALUE;
    for (auto next : possibleTransitions(state)) {
        ans = combine(ans, rec(next));
    }

    // 5. save and return
    return dp[state] = ans;
}
```

## Bottom-up template

```cpp
// 1. initialize dp with base values
// 2. choose correct loop order
// 3. fill states so required smaller/deeper states are already known
// 4. answer from final state
```

## Return value guide

| Problem type | Invalid state | Base valid state | Initial answer |
|---|---:|---:|---:|
| Count ways | `0` | `1` | `0` |
| Possible / impossible | `false` | `true` | `false` |
| Minimize | `INF` | `0` | `INF` |
| Maximize | `-INF` | `0` | `-INF` |
| Game DP | no valid move = losing | depends | losing |

---

# 3. Form 0 — Fibonacci / Stairs

## Problem

Given `n`, count ways to reach stair `n` from stair `0`. You can jump `1` or `2` steps.

## Input

```text
5
```

## Expected Output

```text
8
```

## Form idea

```text
Form 0 = simple recurrence.
Current answer depends on fixed nearby future states.
```

## Step-by-step working

```text
1. State:
   dp(x) = number of ways to reach n from stair x.

2. Choices from x:
   jump 1 -> x + 1
   jump 2 -> x + 2

3. Recurrence:
   dp(x) = dp(x + 1) + dp(x + 2)

4. Base:
   if x == n, return 1
   because we found one valid way.

5. Invalid:
   if x > n, return 0
   because we crossed target.
```

## Recursion tree view

```text
dp(0)
├── jump 1 -> dp(1)
│   ├── jump 1 -> dp(2)
│   │   ├── jump 1 -> dp(3)
│   │   └── jump 2 -> dp(4)
│   └── jump 2 -> dp(3)
└── jump 2 -> dp(2)
    ├── already computed dp(3)
    └── already computed dp(4)
```

## Top-down C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n;
vector<int> dp;

int rec(int x) {
    if (x > n) return 0;
    if (x == n) return 1;
    if (dp[x] != -1) return dp[x];

    int ans = rec(x + 1) + rec(x + 2);
    return dp[x] = ans;
}

int main() {
    cin >> n;
    dp.assign(n + 2, -1);
    cout << rec(0) << "\n";
    return 0;
}
```

## Bottom-up C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<int> dp(n + 2, 0);
    dp[n] = 1;

    for (int x = n - 1; x >= 0; x--) {
        dp[x] = dp[x + 1] + dp[x + 2];
    }

    cout << dp[0] << "\n";
    return 0;
}
```

## Index-by-index dry run for `n = 5`

```text
Target: 5

Fill from back:

index:  0  1  2  3  4  5  6
dp:     ?  ?  ?  ?  ?  1  0

x = 4:
dp[4] = dp[5] + dp[6] = 1 + 0 = 1

index:  0  1  2  3  4  5  6
dp:     ?  ?  ?  ?  1  1  0

x = 3:
dp[3] = dp[4] + dp[5] = 1 + 1 = 2

x = 2:
dp[2] = dp[3] + dp[4] = 2 + 1 = 3

x = 1:
dp[1] = dp[2] + dp[3] = 3 + 2 = 5

x = 0:
dp[0] = dp[1] + dp[2] = 5 + 3 = 8

Final dp:
index:  0  1  2  3  4  5  6
dp:     8  5  3  2  1  1  0
```

---

# 4. Form 1 — Subset Sum Possible

## Problem

Given an array and target `T`, check whether some subset has sum `T`.

## Input

```text
4 6
3 2 4 7
```

## Expected Output

```text
YES
```

Subset `{2, 4}` gives sum `6`.

## Form idea

```text
Form 1 = choose / not choose.
At each index, either take current element or skip it.
```

## Step-by-step working

```text
1. State:
   dp(i, rem) = can we make sum rem using elements from i to n-1?

2. Choices:
   skip a[i] -> dp(i + 1, rem)
   take a[i] -> dp(i + 1, rem - a[i])

3. Recurrence:
   dp(i, rem) = skip OR take

4. Base:
   if rem == 0, return true
   if i == n, return false unless rem == 0

5. Invalid:
   if rem < 0, return false
```

## Decision tree view

```text
dp(0,6), a[0]=3
├── skip 3 -> dp(1,6), a[1]=2
│   ├── skip 2 -> dp(2,6), a[2]=4
│   └── take 2 -> dp(2,4), a[2]=4
│       └── take 4 -> dp(3,0) = TRUE
└── take 3 -> dp(1,3), a[1]=2
```

## Top-down C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n, T;
vector<int> a;
vector<vector<int>> dp;

int rec(int i, int rem) {
    if (rem < 0) return 0;
    if (rem == 0) return 1;
    if (i == n) return 0;
    if (dp[i][rem] != -1) return dp[i][rem];

    int skip = rec(i + 1, rem);
    int take = rec(i + 1, rem - a[i]);

    return dp[i][rem] = (skip || take);
}

int main() {
    cin >> n >> T;
    a.resize(n);
    for (int i = 0; i < n; i++) cin >> a[i];

    dp.assign(n + 1, vector<int>(T + 1, -1));
    cout << (rec(0, T) ? "YES" : "NO") << "\n";
    return 0;
}
```

## Bottom-up C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, T;
    cin >> n >> T;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];

    vector<vector<int>> dp(n + 1, vector<int>(T + 1, 0));

    for (int i = 0; i <= n; i++) dp[i][0] = 1;

    for (int i = n - 1; i >= 0; i--) {
        for (int rem = 1; rem <= T; rem++) {
            int skip = dp[i + 1][rem];
            int take = 0;
            if (rem >= a[i]) take = dp[i + 1][rem - a[i]];
            dp[i][rem] = skip || take;
        }
    }

    cout << (dp[0][T] ? "YES" : "NO") << "\n";
    return 0;
}
```

## Index-by-index dry run

```text
a = [3, 2, 4, 7], T = 6

Need dp[0][6].

Tree path that succeeds:

i=0, rem=6, a[i]=3
├── skip 3
│   i=1, rem=6, a[i]=2
│   ├── skip 2
│   │   i=2, rem=6, a[i]=4
│   │   ├── skip 4 -> rem stays 6
│   │   └── take 4 -> rem becomes 2
│   └── take 2
│       i=2, rem=4, a[i]=4
│       └── take 4
│           i=3, rem=0 -> TRUE
└── take 3
    i=1, rem=3

Answer: TRUE because path skip 3 -> take 2 -> take 4 reaches rem 0.
```

---

# 5. Form 1 — 0/1 Knapsack

## Problem

Each item has weight and value. Capacity is `W`. Each item can be taken at most once. Maximize total value.

## Input

```text
3 5
2 3
3 4
4 5
```

## Expected Output

```text
7
```

Take item `0` and item `1`: weight `2 + 3 = 5`, value `3 + 4 = 7`.

## Form idea

```text
Form 1 = take / not take with capacity constraint.
```

## Step-by-step working

```text
1. State:
   dp(i, cap) = max value using items i..n-1 with remaining capacity cap.

2. Choices:
   skip item i -> dp(i + 1, cap)
   take item i -> val[i] + dp(i + 1, cap - wt[i])

3. Recurrence:
   dp(i, cap) = max(skip, take)

4. Base:
   if i == n, return 0

5. Invalid:
   if cap < 0, return -INF
```

## Top-down C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n, W;
vector<int> wt, val;
vector<vector<int>> dp;
const int NEG = -1e9;

int rec(int i, int cap) {
    if (cap < 0) return NEG;
    if (i == n) return 0;
    if (dp[i][cap] != -1) return dp[i][cap];

    int skip = rec(i + 1, cap);
    int take = NEG;
    if (wt[i] <= cap) take = val[i] + rec(i + 1, cap - wt[i]);

    return dp[i][cap] = max(skip, take);
}

int main() {
    cin >> n >> W;
    wt.resize(n);
    val.resize(n);
    for (int i = 0; i < n; i++) cin >> wt[i] >> val[i];

    dp.assign(n + 1, vector<int>(W + 1, -1));
    cout << rec(0, W) << "\n";
    return 0;
}
```

## Bottom-up C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, W;
    cin >> n >> W;
    vector<int> wt(n), val(n);
    for (int i = 0; i < n; i++) cin >> wt[i] >> val[i];

    vector<vector<int>> dp(n + 1, vector<int>(W + 1, 0));

    for (int i = n - 1; i >= 0; i--) {
        for (int cap = 0; cap <= W; cap++) {
            int skip = dp[i + 1][cap];
            int take = -1e9;
            if (wt[i] <= cap) take = val[i] + dp[i + 1][cap - wt[i]];
            dp[i][cap] = max(skip, take);
        }
    }

    cout << dp[0][W] << "\n";
    return 0;
}
```

## Index-by-index dry run

```text
items:
0: weight=2 value=3
1: weight=3 value=4
2: weight=4 value=5
Capacity W=5

Tree:

dp(0,5)
├── skip item0
│   dp(1,5)
│   ├── skip item1
│   │   dp(2,5)
│   │   ├── skip item2 -> 0
│   │   └── take item2 -> 5 + dp(3,1) = 5
│   │   best = 5
│   └── take item1
│       4 + dp(2,2) = 4
│   best = 5
└── take item0
    3 + dp(1,3)
    ├── skip item1 -> 3 + dp(2,3) = 3
    └── take item1 -> 3 + 4 + dp(2,0) = 7
    best = 7

Answer = 7
```

---

# 6. Form 1 — Vacation / No Consecutive Same Activity

## Problem

For `n` days, each day has 3 activity scores. You cannot choose the same activity on consecutive days. Maximize score.

## Input

```text
3
10 40 70
20 50 80
30 60 90
```

## Expected Output

```text
210
```

## Step-by-step working

```text
1. State:
   dp(day, prev) = max score from day..n-1 if previous activity was prev.

2. Choices:
   choose act = 0, 1, 2 where act != prev.

3. Recurrence:
   dp(day, prev) = max(points[day][act] + dp(day + 1, act))

4. Base:
   if day == n, return 0.

5. Special previous value:
   prev = 3 means no previous activity.
```

## Top-down C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n;
vector<array<int, 3>> points;
vector<vector<int>> dp;

int rec(int day, int prev) {
    if (day == n) return 0;
    if (dp[day][prev] != -1) return dp[day][prev];

    int ans = 0;
    for (int act = 0; act < 3; act++) {
        if (act == prev) continue;
        ans = max(ans, points[day][act] + rec(day + 1, act));
    }
    return dp[day][prev] = ans;
}

int main() {
    cin >> n;
    points.resize(n);
    for (int i = 0; i < n; i++) cin >> points[i][0] >> points[i][1] >> points[i][2];

    dp.assign(n + 1, vector<int>(4, -1));
    cout << rec(0, 3) << "\n";
    return 0;
}
```

## Bottom-up C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;
    vector<array<int, 3>> points(n);
    for (int i = 0; i < n; i++) cin >> points[i][0] >> points[i][1] >> points[i][2];

    vector<vector<int>> dp(n + 1, vector<int>(4, 0));

    for (int day = n - 1; day >= 0; day--) {
        for (int prev = 0; prev <= 3; prev++) {
            int ans = 0;
            for (int act = 0; act < 3; act++) {
                if (act == prev) continue;
                ans = max(ans, points[day][act] + dp[day + 1][act]);
            }
            dp[day][prev] = ans;
        }
    }

    cout << dp[0][3] << "\n";
    return 0;
}
```

## Index-by-index dry run

```text
Day 0: A=10 B=40 C=70
Day 1: A=20 B=50 C=80
Day 2: A=30 B=60 C=90

Start: dp(0, none)
├── choose A: 10 + dp(1, A)
├── choose B: 40 + dp(1, B)
└── choose C: 70 + dp(1, C)
    ├── day1 choose A: 20 + dp(2, A)
    └── day1 choose B: 50 + dp(2, B)
        ├── day2 choose A: 30
        └── day2 choose C: 90
        best from here = 50 + 90 = 140

Total with C on day0 = 70 + 140 = 210
Answer = 210
```

---

# 7. Form 1 — Boredom / Delete and Earn

## Problem

Given numbers. If you take value `x`, you earn `x * frequency[x]`, but cannot take `x - 1` or `x + 1`. Maximize points.

## Input

```text
6
1 2 2 3 3 3
```

## Expected Output

```text
10
```

Take `1` and all `3`s: `1 + 9 = 10`.

## Step-by-step working

```text
1. Transform array into frequency by value.
2. gain[x] = x * freq[x]
3. Now it becomes House Robber on values.
4. If take x, next allowed is x + 2.
5. If skip x, next is x + 1.
```

## Recurrence

```text
dp(x) = max(dp(x + 1), gain[x] + dp(x + 2))
```

## Top-down C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n, mx;
vector<long long> freq, dp;

long long rec(int x) {
    if (x > mx) return 0;
    if (dp[x] != -1) return dp[x];

    long long skip = rec(x + 1);
    long long take = freq[x] * x + rec(x + 2);
    return dp[x] = max(skip, take);
}

int main() {
    cin >> n;
    vector<int> a(n);
    mx = 0;
    for (int i = 0; i < n; i++) {
        cin >> a[i];
        mx = max(mx, a[i]);
    }

    freq.assign(mx + 2, 0);
    for (int x : a) freq[x]++;

    dp.assign(mx + 3, -1);
    cout << rec(1) << "\n";
    return 0;
}
```

## Bottom-up C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;
    vector<int> a(n);
    int mx = 0;
    for (int i = 0; i < n; i++) {
        cin >> a[i];
        mx = max(mx, a[i]);
    }

    vector<long long> freq(mx + 2, 0), dp(mx + 3, 0);
    for (int x : a) freq[x]++;

    for (int x = mx; x >= 1; x--) {
        long long skip = dp[x + 1];
        long long take = freq[x] * x + dp[x + 2];
        dp[x] = max(skip, take);
    }

    cout << dp[1] << "\n";
    return 0;
}
```

## Index-by-index dry run

```text
a = [1, 2, 2, 3, 3, 3]

freq/gain table:
value:  1  2  3
freq:   1  2  3
gain:   1  4  9

Tree:

dp(1)
├── skip 1 -> dp(2)
│   ├── skip 2 -> dp(3) = 9
│   └── take 2 -> 4 + dp(4) = 4
│   best = 9
└── take 1 -> 1 + dp(3)
    dp(3) = max(skip 3 = 0, take 3 = 9)
    total = 1 + 9 = 10

Answer = 10
```

---

# 8. Form 2 — LIS O(N²)

## Problem

Find the length of the longest increasing subsequence.

## Input

```text
6
5 1 6 2 3 4
```

## Expected Output

```text
4
```

LIS is `1 2 3 4`.

## Form idea

```text
Form 2 = best answer ending exactly at index i.
```

## Step-by-step working

```text
1. State:
   dp[i] = LIS length ending exactly at i.

2. For each previous j < i:
   if a[j] < a[i], we can extend j -> i.

3. Recurrence:
   dp[i] = 1 + max(dp[j]) for all j < i and a[j] < a[i]

4. Base:
   dp[i] = 1 for every i.
```

## Top-down C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n;
vector<int> a, dp;

int rec(int i) {
    if (dp[i] != -1) return dp[i];

    int ans = 1;
    for (int j = 0; j < i; j++) {
        if (a[j] < a[i]) {
            ans = max(ans, rec(j) + 1);
        }
    }
    return dp[i] = ans;
}

int main() {
    cin >> n;
    a.resize(n);
    for (int i = 0; i < n; i++) cin >> a[i];

    dp.assign(n, -1);
    int ans = 0;
    for (int i = 0; i < n; i++) ans = max(ans, rec(i));

    cout << ans << "\n";
    return 0;
}
```

## Bottom-up C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];

    vector<int> dp(n, 1);
    int ans = 1;

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < i; j++) {
            if (a[j] < a[i]) {
                dp[i] = max(dp[i], dp[j] + 1);
            }
        }
        ans = max(ans, dp[i]);
    }

    cout << ans << "\n";
    return 0;
}
```

## Index-by-index dry run

```text
a = [5, 1, 6, 2, 3, 4]
index: 0  1  2  3  4  5

At i=0, a[i]=5:
no previous index
LIS ending here = [5]
dp = [1, ?, ?, ?, ?, ?]

At i=1, a[i]=1:
previous 5 is not < 1
LIS ending here = [1]
dp = [1, 1, ?, ?, ?, ?]

At i=2, a[i]=6:
valid previous:
├── j=0, 5 < 6 -> dp[0]+1 = 2
└── j=1, 1 < 6 -> dp[1]+1 = 2
dp[2] = 2
dp = [1, 1, 2, ?, ?, ?]

At i=3, a[i]=2:
valid previous:
└── j=1, 1 < 2 -> dp[1]+1 = 2
dp = [1, 1, 2, 2, ?, ?]

At i=4, a[i]=3:
valid previous:
├── j=1, 1 < 3 -> 2
└── j=3, 2 < 3 -> 3
dp = [1, 1, 2, 2, 3, ?]

At i=5, a[i]=4:
valid previous:
├── j=1, 1 < 4 -> 2
├── j=3, 2 < 4 -> 3
└── j=4, 3 < 4 -> 4
dp = [1, 1, 2, 2, 3, 4]

Answer = 4
```

---

# 9. Form 2 — LIS O(N log N)

## Problem

Same LIS problem, optimized.

## Input

```text
9
1 5 7 10 9 6 8 2 3
```

## Expected Output

```text
4
```

## Form idea

```text
This is not classical DP table.
It compresses Form 2 by keeping best possible tail value for every LIS length.
```

## Step-by-step working

```text
1. tail[len] = smallest possible ending value of an increasing subsequence of length len + 1.
2. For each x, find first tail[pos] >= x.
3. Replace it with x.
4. If no such position, append x.
5. tail size = LIS length.
```

## Top-down note

```text
LIS O(N log N) is naturally iterative.
Top-down recursion is not the preferred form for this optimized method.
Use the O(N²) top-down version from previous section if recursion is required.
```

## Bottom-up / iterative C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];

    vector<int> tail;

    for (int x : a) {
        auto it = lower_bound(tail.begin(), tail.end(), x);
        if (it == tail.end()) tail.push_back(x);
        else *it = x;
    }

    cout << tail.size() << "\n";
    return 0;
}
```

## Index-by-index dry run

```text
a = [1, 5, 7, 10, 9, 6, 8, 2, 3]

i=0, x=1:
tail empty, append 1
tail = [1]

i=1, x=5:
first >= 5 not found, append
tail = [1, 5]

i=2, x=7:
append
tail = [1, 5, 7]

i=3, x=10:
append
tail = [1, 5, 7, 10]

i=4, x=9:
first >= 9 is 10, replace
tail = [1, 5, 7, 9]

i=5, x=6:
first >= 6 is 7, replace
tail = [1, 5, 6, 9]

i=6, x=8:
first >= 8 is 9, replace
tail = [1, 5, 6, 8]

i=7, x=2:
first >= 2 is 5, replace
tail = [1, 2, 6, 8]

i=8, x=3:
first >= 3 is 6, replace
tail = [1, 2, 3, 8]

Answer = length(tail) = 4
```

---

# 10. Form 2 — Palindrome Partition Minimum Parts

## Problem

Given a string, split it into the minimum number of palindromic parts.

## Input

```text
aab
```

## Expected Output

```text
2
```

Split: `aa | b`.

## Step-by-step working

```text
1. First precompute pal[l][r].
2. State:
   dp[i] = minimum palindromic parts for prefix s[0..i].
3. Choose last segment s[j..i].
4. If s[j..i] is palindrome:
   dp[i] = min(dp[i], 1 + dp[j - 1])
5. If j == 0:
   dp[i] = 1
```

## Top-down C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

string s;
int n;
vector<vector<int>> pal;
vector<int> dp;
const int INF = 1e9;

int rec(int i) {
    if (i < 0) return 0;
    if (dp[i] != -1) return dp[i];

    int ans = INF;
    for (int j = 0; j <= i; j++) {
        if (pal[j][i]) {
            ans = min(ans, 1 + rec(j - 1));
        }
    }
    return dp[i] = ans;
}

int main() {
    cin >> s;
    n = s.size();
    pal.assign(n, vector<int>(n, 0));

    for (int len = 1; len <= n; len++) {
        for (int l = 0; l + len - 1 < n; l++) {
            int r = l + len - 1;
            if (len == 1) pal[l][r] = 1;
            else if (len == 2) pal[l][r] = (s[l] == s[r]);
            else pal[l][r] = (s[l] == s[r] && pal[l + 1][r - 1]);
        }
    }

    dp.assign(n, -1);
    cout << rec(n - 1) << "\n";
    return 0;
}
```

## Bottom-up C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    string s;
    cin >> s;
    int n = s.size();

    vector<vector<int>> pal(n, vector<int>(n, 0));
    for (int len = 1; len <= n; len++) {
        for (int l = 0; l + len - 1 < n; l++) {
            int r = l + len - 1;
            if (len == 1) pal[l][r] = 1;
            else if (len == 2) pal[l][r] = (s[l] == s[r]);
            else pal[l][r] = (s[l] == s[r] && pal[l + 1][r - 1]);
        }
    }

    const int INF = 1e9;
    vector<int> dp(n, INF);

    for (int i = 0; i < n; i++) {
        for (int j = 0; j <= i; j++) {
            if (pal[j][i]) {
                if (j == 0) dp[i] = 1;
                else dp[i] = min(dp[i], dp[j - 1] + 1);
            }
        }
    }

    cout << dp[n - 1] << "\n";
    return 0;
}
```

## Index-by-index dry run for `aab`

```text
s:     a  a  b
index: 0  1  2

Palindrome table true cells:
[0,0] = a
[1,1] = a
[2,2] = b
[0,1] = aa

Now fill dp:

i=0, prefix = "a"
├── j=0, s[0..0] palindrome
dp[0] = 1

i=1, prefix = "aa"
├── j=0, s[0..1] = "aa" palindrome -> 1
└── j=1, s[1..1] = "a" palindrome -> dp[0] + 1 = 2
dp[1] = 1

i=2, prefix = "aab"
├── j=0, s[0..2] = "aab" not palindrome
├── j=1, s[1..2] = "ab" not palindrome
└── j=2, s[2..2] = "b" palindrome -> dp[1] + 1 = 2
dp[2] = 2

Answer = 2
```

---

# 11. Form 3 — LCS

## Problem

Given two strings, find the length of their Longest Common Subsequence.

## Input

```text
abcde
ace
```

## Expected Output

```text
3
```

## Form idea

```text
Form 3 = two pointers over two strings.
At every state, decide match or skip.
```

## Step-by-step working

```text
1. State:
   dp(i, j) = LCS length of s[i..] and t[j..].

2. If characters match:
   take both and move to i+1, j+1.

3. If characters do not match:
   skip from s OR skip from t.

4. Base:
   if i == n or j == m, answer is 0.
```

## Top-down C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

string s, t;
int n, m;
vector<vector<int>> dp;

int rec(int i, int j) {
    if (i == n || j == m) return 0;
    if (dp[i][j] != -1) return dp[i][j];

    if (s[i] == t[j]) {
        return dp[i][j] = 1 + rec(i + 1, j + 1);
    }

    return dp[i][j] = max(rec(i + 1, j), rec(i, j + 1));
}

int main() {
    cin >> s >> t;
    n = s.size();
    m = t.size();
    dp.assign(n, vector<int>(m, -1));
    cout << rec(0, 0) << "\n";
    return 0;
}
```

## Bottom-up C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    string s, t;
    cin >> s >> t;
    int n = s.size(), m = t.size();

    vector<vector<int>> dp(n + 1, vector<int>(m + 1, 0));

    for (int i = n - 1; i >= 0; i--) {
        for (int j = m - 1; j >= 0; j--) {
            if (s[i] == t[j]) dp[i][j] = 1 + dp[i + 1][j + 1];
            else dp[i][j] = max(dp[i + 1][j], dp[i][j + 1]);
        }
    }

    cout << dp[0][0] << "\n";
    return 0;
}
```

## Index-by-index dry run

```text
s = a b c d e
    0 1 2 3 4

t = a c e
    0 1 2

Start dp(0,0): s[0]=a, t[0]=a
└── match a -> 1 + dp(1,1)

At dp(1,1): s[1]=b, t[1]=c
├── skip s[1]=b -> dp(2,1)
└── skip t[1]=c -> dp(1,2)

Best path:
dp(2,1): s[2]=c, t[1]=c
└── match c -> 1 + dp(3,2)

dp(3,2): s[3]=d, t[2]=e
├── skip d -> dp(4,2)
└── skip e -> dp(3,3)

Best path:
dp(4,2): s[4]=e, t[2]=e
└── match e -> 1 + dp(5,3)

Total = a + c + e = 3
```

---

# 12. Form 3 — Edit Distance

## Problem

Given two strings, find minimum operations to convert `s` to `t`. Operations: insert, delete, replace.

## Input

```text
horse
ros
```

## Expected Output

```text
3
```

## Step-by-step working

```text
1. State:
   dp(i, j) = minimum cost to convert s[i..] to t[j..].

2. If s[i] == t[j]:
   no operation, move both.

3. Else choose minimum:
   delete s[i]  -> dp(i + 1, j)
   insert t[j]  -> dp(i, j + 1)
   replace      -> dp(i + 1, j + 1)

4. Base:
   if s finished, insert remaining t chars.
   if t finished, delete remaining s chars.
```

## Top-down C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

string s, t;
int n, m;
vector<vector<int>> dp;

int rec(int i, int j) {
    if (i == n) return m - j;
    if (j == m) return n - i;
    if (dp[i][j] != -1) return dp[i][j];

    if (s[i] == t[j]) return dp[i][j] = rec(i + 1, j + 1);

    int del = 1 + rec(i + 1, j);
    int ins = 1 + rec(i, j + 1);
    int rep = 1 + rec(i + 1, j + 1);

    return dp[i][j] = min({del, ins, rep});
}

int main() {
    cin >> s >> t;
    n = s.size();
    m = t.size();
    dp.assign(n, vector<int>(m, -1));
    cout << rec(0, 0) << "\n";
    return 0;
}
```

## Bottom-up C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    string s, t;
    cin >> s >> t;
    int n = s.size(), m = t.size();

    vector<vector<int>> dp(n + 1, vector<int>(m + 1, 0));

    for (int i = 0; i <= n; i++) dp[i][m] = n - i;
    for (int j = 0; j <= m; j++) dp[n][j] = m - j;

    for (int i = n - 1; i >= 0; i--) {
        for (int j = m - 1; j >= 0; j--) {
            if (s[i] == t[j]) dp[i][j] = dp[i + 1][j + 1];
            else {
                int del = 1 + dp[i + 1][j];
                int ins = 1 + dp[i][j + 1];
                int rep = 1 + dp[i + 1][j + 1];
                dp[i][j] = min({del, ins, rep});
            }
        }
    }

    cout << dp[0][0] << "\n";
    return 0;
}
```

## Index-by-index dry run

```text
s = horse
t = ros

One optimal path:

horse
├── replace h with r
│   rorse
├── delete first r after o? classical sequence simplifies to:
│   rose
└── delete e
    ros

Operations = 3

DP decision view:

dp(0,0): h vs r mismatch
├── delete h
├── insert r
└── replace h->r  <-- good path
    dp(1,1): o vs o match
    └── dp(2,2): r vs s mismatch
        ├── delete r
        ├── insert s
        └── replace r->s
```

---

# 13. Form 4 — Palindrome Table

## Problem

Given a string, compute whether every substring `s[l..r]` is palindrome.

## Input

```text
abba
```

## Expected Output

```text
YES
```

## Form idea

```text
Form 4 = interval DP.
Answer for interval [l, r] depends on smaller interval [l+1, r-1].
```

## Step-by-step working

```text
1. State:
   dp[l][r] = true if s[l..r] is palindrome.

2. Base:
   length 1 -> true
   length 2 -> s[l] == s[r]

3. Recurrence:
   dp[l][r] = s[l] == s[r] AND dp[l+1][r-1]

4. Fill order:
   increasing length.
```

## Top-down C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

string s;
vector<vector<int>> dp;

int rec(int l, int r) {
    if (l >= r) return 1;
    if (dp[l][r] != -1) return dp[l][r];
    return dp[l][r] = (s[l] == s[r] && rec(l + 1, r - 1));
}

int main() {
    cin >> s;
    int n = s.size();
    dp.assign(n, vector<int>(n, -1));
    cout << (rec(0, n - 1) ? "YES" : "NO") << "\n";
    return 0;
}
```

## Bottom-up C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    string s;
    cin >> s;
    int n = s.size();

    vector<vector<int>> dp(n, vector<int>(n, 0));

    for (int len = 1; len <= n; len++) {
        for (int l = 0; l + len - 1 < n; l++) {
            int r = l + len - 1;
            if (len == 1) dp[l][r] = 1;
            else if (len == 2) dp[l][r] = (s[l] == s[r]);
            else dp[l][r] = (s[l] == s[r] && dp[l + 1][r - 1]);
        }
    }

    cout << (dp[0][n - 1] ? "YES" : "NO") << "\n";
    return 0;
}
```

## Index-by-index dry run for `abba`

```text
s:     a b b a
index: 0 1 2 3

Length 1:
[0,0] a -> true
[1,1] b -> true
[2,2] b -> true
[3,3] a -> true

Length 2:
[0,1] ab -> false
[1,2] bb -> true
[2,3] ba -> false

Length 3:
[0,2] abb -> a != b -> false
[1,3] bba -> b != a -> false

Length 4:
[0,3] abba
├── s[0] == s[3] -> a == a
└── inside [1,2] = bb -> true
Answer = true
```

---

# 14. Form 4 — Matrix Chain Multiplication

## Problem

Given matrix dimensions, find minimum multiplication cost.

## Input

```text
4
10 20 30 40
```

## Expected Output

```text
18000
```

## Step-by-step working

```text
Matrices:
A0 = 10 x 20
A1 = 20 x 30
A2 = 30 x 40

State:
dp[l][r] = minimum cost to multiply matrices l..r.

Transition:
Choose split k:
A[l..k] and A[k+1..r]

Cost:
dp[l][k] + dp[k+1][r] + dim[l] * dim[k+1] * dim[r+1]
```

## Top-down C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n;
vector<int> dim;
vector<vector<int>> dp;
const int INF = 1e9;

int rec(int l, int r) {
    if (l == r) return 0;
    if (dp[l][r] != -1) return dp[l][r];

    int ans = INF;
    for (int k = l; k < r; k++) {
        int cost = rec(l, k) + rec(k + 1, r) + dim[l] * dim[k + 1] * dim[r + 1];
        ans = min(ans, cost);
    }
    return dp[l][r] = ans;
}

int main() {
    int m;
    cin >> m;
    dim.resize(m);
    for (int i = 0; i < m; i++) cin >> dim[i];

    n = m - 1;
    dp.assign(n, vector<int>(n, -1));
    cout << rec(0, n - 1) << "\n";
    return 0;
}
```

## Bottom-up C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int m;
    cin >> m;
    vector<int> dim(m);
    for (int i = 0; i < m; i++) cin >> dim[i];

    int n = m - 1;
    const int INF = 1e9;
    vector<vector<int>> dp(n, vector<int>(n, 0));

    for (int len = 2; len <= n; len++) {
        for (int l = 0; l + len - 1 < n; l++) {
            int r = l + len - 1;
            dp[l][r] = INF;
            for (int k = l; k < r; k++) {
                int cost = dp[l][k] + dp[k + 1][r] + dim[l] * dim[k + 1] * dim[r + 1];
                dp[l][r] = min(dp[l][r], cost);
            }
        }
    }

    cout << dp[0][n - 1] << "\n";
    return 0;
}
```

## Index-by-index dry run

```text
Dimensions: [10, 20, 30, 40]
Matrices:
A0 = 10x20
A1 = 20x30
A2 = 30x40

Need dp[0][2].

Split tree:

dp[0][2]
├── split k=0: A0 | A1 A2
│   left cost  = dp[0][0] = 0
│   right cost = dp[1][2]
│   multiply   = 10 * 20 * 40 = 8000
│   dp[1][2]   = 20 * 30 * 40 = 24000
│   total      = 32000
└── split k=1: A0 A1 | A2
    left cost  = dp[0][1]
    right cost = dp[2][2] = 0
    multiply   = 10 * 30 * 40 = 12000
    dp[0][1]   = 10 * 20 * 30 = 6000
    total      = 18000

Answer = min(32000, 18000) = 18000
```

---

# 15. Form 5 — Divisor Game

## Problem

Given number `x`, player can move from `x` to `x - d`, where `d` is a divisor of `x` and `1 <= d < x`. Player unable to move loses.

## Input

```text
6
```

## Expected Output

```text
WIN
```

## Form idea

```text
Form 5 = game DP.
Current state is winning if there exists one move to a losing state.
```

## Step-by-step working

```text
1. State:
   dp[x] = can current player win from number x?

2. Move:
   choose divisor d of x, move to x-d.

3. Recurrence:
   dp[x] = true if any dp[x-d] == false.

4. Base:
   dp[1] = false because no valid move.
```

## Top-down C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n;
vector<int> dp;

int rec(int x) {
    if (x == 1) return 0;
    if (dp[x] != -1) return dp[x];

    for (int d = 1; d * d <= x; d++) {
        if (x % d == 0) {
            int d1 = d, d2 = x / d;
            if (d1 < x && rec(x - d1) == 0) return dp[x] = 1;
            if (d2 < x && rec(x - d2) == 0) return dp[x] = 1;
        }
    }
    return dp[x] = 0;
}

int main() {
    cin >> n;
    dp.assign(n + 1, -1);
    cout << (rec(n) ? "WIN" : "LOSE") << "\n";
    return 0;
}
```

## Bottom-up C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;
    vector<int> dp(n + 1, 0);

    dp[1] = 0;
    for (int x = 2; x <= n; x++) {
        for (int d = 1; d * d <= x; d++) {
            if (x % d == 0) {
                int d1 = d, d2 = x / d;
                if (d1 < x && dp[x - d1] == 0) dp[x] = 1;
                if (d2 < x && dp[x - d2] == 0) dp[x] = 1;
            }
        }
    }

    cout << (dp[n] ? "WIN" : "LOSE") << "\n";
    return 0;
}
```

## Index-by-index dry run for `x = 6`

```text
dp[1] = LOSE

x=2:
├── divisor 1 -> 2-1 = 1, dp[1]=LOSE
└── current can move opponent to lose
dp[2] = WIN

x=3:
├── divisor 1 -> dp[2]=WIN
└── no losing child
dp[3] = LOSE

x=4:
├── divisor 1 -> dp[3]=LOSE
└── winning move exists
dp[4] = WIN

x=5:
├── divisor 1 -> dp[4]=WIN
└── no losing child
dp[5] = LOSE

x=6:
valid divisors: 1, 2, 3
├── move 1 -> dp[5]=LOSE -> winning move
├── move 2 -> dp[4]=WIN
└── move 3 -> dp[3]=LOSE -> also winning

dp[6] = WIN
```

---

# 16. Grid DP — Minimum Path Sum

## Problem

Given grid costs, move only right or down from `(0,0)` to `(n-1,m-1)`. Find minimum path sum.

## Input

```text
3 3
1 3 1
1 5 1
4 2 1
```

## Expected Output

```text
7
```

## Step-by-step working

```text
1. State:
   dp(r, c) = minimum cost from cell (r,c) to destination.

2. Choices:
   move down  -> dp(r+1, c)
   move right -> dp(r, c+1)

3. Recurrence:
   dp(r,c) = grid[r][c] + min(dp(r+1,c), dp(r,c+1))

4. Base:
   destination returns grid value.

5. Invalid:
   outside grid returns INF.
```

## Top-down C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n, m;
vector<vector<int>> grid, dp;
const int INF = 1e9;

int rec(int r, int c) {
    if (r >= n || c >= m) return INF;
    if (r == n - 1 && c == m - 1) return grid[r][c];
    if (dp[r][c] != -1) return dp[r][c];

    return dp[r][c] = grid[r][c] + min(rec(r + 1, c), rec(r, c + 1));
}

int main() {
    cin >> n >> m;
    grid.assign(n, vector<int>(m));
    dp.assign(n, vector<int>(m, -1));
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) cin >> grid[i][j];
    }
    cout << rec(0, 0) << "\n";
    return 0;
}
```

## Bottom-up C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, m;
    cin >> n >> m;
    vector<vector<int>> grid(n, vector<int>(m));
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) cin >> grid[i][j];
    }

    const int INF = 1e9;
    vector<vector<int>> dp(n + 1, vector<int>(m + 1, INF));
    dp[n - 1][m - 1] = grid[n - 1][m - 1];

    for (int r = n - 1; r >= 0; r--) {
        for (int c = m - 1; c >= 0; c--) {
            if (r == n - 1 && c == m - 1) continue;
            dp[r][c] = grid[r][c] + min(dp[r + 1][c], dp[r][c + 1]);
        }
    }

    cout << dp[0][0] << "\n";
    return 0;
}
```

## Index-by-index dry run

```text
grid:
(0,0)=1  (0,1)=3  (0,2)=1
(1,0)=1  (1,1)=5  (1,2)=1
(2,0)=4  (2,1)=2  (2,2)=1

Fill from destination:

dp(2,2) = 1

dp(2,1) = 2 + dp(2,2) = 3
dp(2,0) = 4 + dp(2,1) = 7

dp(1,2) = 1 + dp(2,2) = 2
dp(1,1) = 5 + min(dp(2,1)=3, dp(1,2)=2) = 7
dp(1,0) = 1 + min(dp(2,0)=7, dp(1,1)=7) = 8

dp(0,2) = 1 + dp(1,2) = 3
dp(0,1) = 3 + min(dp(1,1)=7, dp(0,2)=3) = 6
dp(0,0) = 1 + min(dp(1,0)=8, dp(0,1)=6) = 7

Final dp grid:
7 6 3
8 7 2
7 3 1
```

---

# 17. Bitmask DP — TSP Minimum Cost

## Problem

Given `n <= 20` cities and cost matrix, start from city `0`, visit all cities exactly once, and return to city `0` with minimum cost.

## Input

```text
4
0 10 15 20
10 0 35 25
15 35 0 30
20 25 30 0
```

## Expected Output

```text
80
```

## Step-by-step working

```text
1. State:
   dp(mask, last) = min cost after visiting cities in mask and currently at last.

2. mask tells which cities are already visited.
3. last tells current city.
4. Try every unvisited next city.
5. Base:
   if all visited, return cost[last][0].
```

## Top-down C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n;
vector<vector<int>> cost, dp;
const int INF = 1e9;

int rec(int mask, int last) {
    if (mask == (1 << n) - 1) return cost[last][0];
    if (dp[mask][last] != -1) return dp[mask][last];

    int ans = INF;
    for (int nxt = 0; nxt < n; nxt++) {
        if ((mask & (1 << nxt)) == 0) {
            ans = min(ans, cost[last][nxt] + rec(mask | (1 << nxt), nxt));
        }
    }
    return dp[mask][last] = ans;
}

int main() {
    cin >> n;
    cost.assign(n, vector<int>(n));
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) cin >> cost[i][j];
    }

    dp.assign(1 << n, vector<int>(n, -1));
    cout << rec(1, 0) << "\n";
    return 0;
}
```

## Bottom-up C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;
    vector<vector<int>> cost(n, vector<int>(n));
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) cin >> cost[i][j];
    }

    const int INF = 1e9;
    vector<vector<int>> dp(1 << n, vector<int>(n, INF));
    dp[1][0] = 0;

    for (int mask = 0; mask < (1 << n); mask++) {
        for (int last = 0; last < n; last++) {
            if (dp[mask][last] == INF) continue;
            for (int nxt = 0; nxt < n; nxt++) {
                if (mask & (1 << nxt)) continue;
                int newMask = mask | (1 << nxt);
                dp[newMask][nxt] = min(dp[newMask][nxt], dp[mask][last] + cost[last][nxt]);
            }
        }
    }

    int full = (1 << n) - 1;
    int ans = INF;
    for (int last = 0; last < n; last++) {
        ans = min(ans, dp[full][last] + cost[last][0]);
    }

    cout << ans << "\n";
    return 0;
}
```

## Index-by-index dry run

```text
Cities: 0,1,2,3
Start at 0.

mask binary meaning:
0001 = visited {0}
0011 = visited {0,1}
0101 = visited {0,2}
1001 = visited {0,3}
1111 = all visited

Start:
dp(0001, 0)
├── go 1 -> cost 10 + dp(0011, 1)
├── go 2 -> cost 15 + dp(0101, 2)
└── go 3 -> cost 20 + dp(1001, 3)

Example branch:
dp(0011, 1)
├── go 2 -> dp(0111, 2)
└── go 3 -> dp(1011, 3)

When mask = 1111:
return to city 0.

Best tour cost = 80
```

---

# 18. Tree DP — Tree Diameter

## Problem

Given a tree, find its diameter length in edges.

## Input

```text
5
1 2
1 3
3 4
3 5
```

## Expected Output

```text
3
```

Diameter path can be `2 -> 1 -> 3 -> 4`.

## Form idea

```text
Tree DP = compute answer from child subtrees.
DFS returns useful information to parent.
```

## Step-by-step working

```text
1. Root the tree at any node, usually 1.
2. State:
   height[node] = longest downward path from node to a leaf.
3. For each node, collect child heights.
4. Best diameter passing through node = largest child height + second largest child height + 2.
5. Global answer is max over all nodes.
```

## Tree diagram

```text
        1
       / \
      2   3
         / \
        4   5
```

## Top-down DFS C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n;
vector<vector<int>> g;
int diameter = 0;

int dfs(int node, int parent) {
    int best1 = -1;
    int best2 = -1;

    for (int child : g[node]) {
        if (child == parent) continue;

        int h = dfs(child, node);

        if (h > best1) {
            best2 = best1;
            best1 = h;
        } else if (h > best2) {
            best2 = h;
        }
    }

    diameter = max(diameter, best1 + best2 + 2);
    return best1 + 1;
}

int main() {
    cin >> n;
    g.assign(n + 1, {});

    for (int i = 0; i < n - 1; i++) {
        int u, v;
        cin >> u >> v;
        g[u].push_back(v);
        g[v].push_back(u);
    }

    dfs(1, 0);
    cout << diameter << "\n";
    return 0;
}
```

## Bottom-up style using parent order C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;
    vector<vector<int>> g(n + 1);

    for (int i = 0; i < n - 1; i++) {
        int u, v;
        cin >> u >> v;
        g[u].push_back(v);
        g[v].push_back(u);
    }

    vector<int> parent(n + 1, 0), order;
    stack<int> st;
    st.push(1);
    parent[1] = -1;

    while (!st.empty()) {
        int u = st.top();
        st.pop();
        order.push_back(u);
        for (int v : g[u]) {
            if (v == parent[u]) continue;
            parent[v] = u;
            st.push(v);
        }
    }

    vector<int> height(n + 1, 0);
    int diameter = 0;

    reverse(order.begin(), order.end());
    for (int u : order) {
        int best1 = -1, best2 = -1;
        for (int v : g[u]) {
            if (parent[v] != u) continue;
            int h = height[v];
            if (h > best1) {
                best2 = best1;
                best1 = h;
            } else if (h > best2) {
                best2 = h;
            }
        }
        diameter = max(diameter, best1 + best2 + 2);
        height[u] = best1 + 1;
    }

    cout << diameter << "\n";
    return 0;
}
```

## Index-by-index dry run

```text
Tree rooted at 1:

        1
       / \
      2   3
         / \
        4   5

Postorder processing:

Node 2:
├── no child
├── best1 = -1, best2 = -1
├── diameter through 2 = 0
└── height[2] = 0

Node 4:
└── height[4] = 0

Node 5:
└── height[5] = 0

Node 3:
├── child 4 height = 0
├── child 5 height = 0
├── best1 = 0, best2 = 0
├── diameter through 3 = 0 + 0 + 2 = 2
└── height[3] = 1

Node 1:
├── child 2 height = 0
├── child 3 height = 1
├── best1 = 1, best2 = 0
├── diameter through 1 = 1 + 0 + 2 = 3
└── height[1] = 2

Answer = 3
```

---

# 19. Final One-Page Revision

## Universal DP checklist

```text
1. Form:
2. State:
3. State meaning:
4. Recurrence:
5. Base case:
6. Invalid/pruning:
7. Transition count:
8. Number of states:
9. TC:
10. SC:
11. Top-down code:
12. Bottom-up code:
13. Dry run:
```

## Form recognition cheat sheet

```text
simple recurrence               -> Form 0
choose / not choose             -> Form 1
ending at index i               -> Form 2
two strings / matching          -> Form 3
substring / interval l..r       -> Form 4
current player win/lose         -> Form 5
grid movement                   -> Grid DP
small n + subset state          -> Bitmask DP
tree / subtree combination      -> Tree DP
```

## Conversion rule: top-down to bottom-up

```text
Top-down state: rec(i, ...)
Bottom-up loop order: reverse of recursion direction.

If rec(i) depends on rec(i+1), loop i from n-1 to 0.
If rec(i) depends on previous j < i, loop i from 0 to n-1.
If rec(l,r) depends on smaller interval, loop by length.
If rec(r,c) depends on down/right, loop from bottom-right to top-left.
If tree DP depends on children, process postorder.
```

## Most important mental models

```text
DP = recursion + memory.
State must define future completely.
Transition = choice.
Base case = smallest solved question.
Bottom-up = answer smaller states before bigger states.
Dry run = draw decision tree and mark repeated states.
```
