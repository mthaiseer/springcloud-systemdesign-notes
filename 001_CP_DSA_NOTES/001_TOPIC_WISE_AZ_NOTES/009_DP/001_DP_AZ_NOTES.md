# 001 DP Master Guide for CP / DSA

> Built around your exact framework:
>
> 1. Look for the form of the problem
> 2. Decide the state and meaning
> 3. Write `DP(...) = _______`
> 4. Decide transitions
> 5. Check time complexity
> 6. Code in order: pruning → base case → cache check → transitions → save and return

---

## Clickable Index

- [0. The Universal DP Framework](#0-the-universal-dp-framework)
- [1. How to Classify DP Problems by Form](#1-how-to-classify-dp-problems-by-form)
- [2. DP Coding Template](#2-dp-coding-template)
- [3. Form 0 — Fibonacci / Stairs](#3-form-0--fibonacci--stairs)
- [4. Form 1 — Subset Sum Possible](#4-form-1--subset-sum-possible)
- [5. Form 1 — 0/1 Knapsack](#5-form-1--01-knapsack)
- [6. Form 1 — Vacation / No Consecutive Same Activity](#6-form-1--vacation--no-consecutive-same-activity)
- [7. Form 1 — Boredom / Delete and Earn](#7-form-1--boredom--delete-and-earn)
- [8. Form 2 — LIS O(N²)](#8-form-2--lis-on²)
- [9. Form 2 — LIS O(N log N)](#9-form-2--lis-on-log-n)
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
Step 1: Look for the form of the problem.
Step 2: Decide the state and meaning.
Step 3: Write DP(...) = _______.
Step 4: Decide transitions.
Step 5: Check time complexity.
Step 6: Code.
```

Time complexity formula:

```text
TC = #States * (1 + average cost of transition)
```

or:

```text
TC = #States * (1 + average number of transitions per state)
```

Code order:

```text
1. pruning
2. base case
3. cache check
4. transitions
5. save and return
```

---

# 1. How to Classify DP Problems by Form

| Form | Recognition clue | Common state |
|---|---|---|
| Form 0 | Fibonacci / stairs / simple recurrence | `dp(n)` |
| Form 1 | choose / not choose, subset, knapsack, activity choice | `dp(index, remaining/state)` |
| Form 2 | best answer ending at index, partition ending at index | `dp(i)` |
| Form 3 | two strings / matching / edit / alignment | `dp(i, j)` |
| Form 4 | substring / subarray / interval / left-right | `dp(l, r)` |
| Form 5 | two-player game / win-loss | `dp(state)` |
| Grid DP | moving in grid | `dp(r, c)` |
| Bitmask DP | small `n`, all subsets | `dp(mask, last)` |
| Tree DP | answer from children/subtree | `dp(node)` |



## Form-by-form thinking upgrade

Use this table before solving any DP problem. The goal is to recognize the **shape** quickly, then write the state.

| Form | How to recognize | First question to ask | Usual direction | Bottom-up order |
|---|---|---|---|---|
| Form 0 | Simple recurrence like stairs/fibonacci | What smaller values build this value? | `rec(x)` or `rec(n)` | smallest/base to target |
| Form 1 | choose / skip / activity choice | What index am I at, and what restriction remains? | `rec(level, state)` | from last index back to first |
| Form 2 | best answer ending at index / prefix partition | What is the answer for prefix ending at `i`? | `dp[i]` | left to right |
| Form 3 | two strings / two arrays | What suffix/prefix of both objects remains? | `rec(i, j)` | from end to start, or prefix table |
| Form 4 | interval / substring / range | What is answer for `[l..r]`? | `rec(l, r)` | increasing interval length |
| Form 5 | game / turns / win-loss | Can I move to a losing state? | `rec(state)` | from smallest state to target |
| Grid DP | move inside grid | Which cells can reach this cell / destination? | `rec(r, c)` | reverse movement direction |
| Bitmask DP | subsets, `n <= 20` | Which set is already visited? | `rec(mask, last)` | increasing masks |
| Tree DP | answer from subtree/children | What does each child return to parent? | `dfs(node, parent)` | postorder DFS |

### How to write top-down and bottom-up for any form

```text
Top-down:
1. Start from the original full problem.
2. Recursively ask smaller subproblems.
3. Stop at base cases.
4. Cache repeated states.
5. Return answer upward.

Bottom-up:
1. Identify all states.
2. Start from base states.
3. Fill states in an order where dependencies are already known.
4. Final answer is read from the target state.
```

Decision tree:

```text
Does problem say choose subset/items?        -> Form 1
Does answer depend on ending index i?       -> Form 2
Does it use two strings/arrays?             -> Form 3
Does it ask every substring/interval?       -> Form 4
Does it have alternate turns?               -> Form 5
Does it move in a grid?                     -> Grid DP
Is n <= 20 and subset states appear?        -> Bitmask DP
Is input a tree?                            -> Tree DP
```

---

# 2. DP Coding Template

```cpp
int rec(State state) {
    // 1. pruning
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

Return value guide:

| Problem type | Invalid state | Finished valid state |
|---|---:|---:|
| Count ways | `0` | `1` |
| Possible / impossible | `false` | `true` |
| Minimize | `INF` | `0` |
| Maximize | `-INF` | `0` |
| Win/Lose game | losing if no valid move | depends on move result |

---

# 3. Form 0 — Fibonacci / Stairs

## Problem

Given `n`, count ways to reach stair `n` from stair `0`. At each step, you can jump `1` or `2`.

## Input

```text
5
```

## Expected Output

```text
8
```

## Framework

### 1. Look for the form

Simple recurrence / Fibonacci style.

```text
Form 0: Simple recurrence DP
```

### 2. Decide state and meaning

```text
DP(x) = number of ways to reach stair n starting from stair x
```

### 3. DP expression

```text
DP(x) = DP(x + 1) + DP(x + 2)
```

### 4. Transitions

```text
from x:
  jump 1 -> x + 1
  jump 2 -> x + 2
```

Plain diagram:

```text
DP(0)
├── DP(1)
│   ├── DP(2)
│   └── DP(3)
└── DP(2)
    ├── DP(3)
    └── DP(4)
```

### 5. Time complexity

```text
#States = n + 1
Transitions per state = 2
TC = O(n * 2) = O(n)
SC = O(n)
```

## Decision Tree

```text
Problem asks count ways to reach n?
├── Can move by fixed small jumps? YES
│   └── State = current stair x
│       └── DP(x) = ways from x to n
└── Transition = try every allowed jump
```

## Step-by-Step Working

```text
1. Stand at stair x.
2. If x > n, this path is invalid -> 0 ways.
3. If x == n, one valid path is completed -> 1 way.
4. Otherwise try jump 1 and jump 2.
5. Add both answers.
6. Cache DP(x), because many paths reach the same x.
```

### 6. Code

### Top-down C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n;
vector<int> dp;

int rec(int x) {
    // pruning
    if (x > n) return 0;

    // base case
    if (x == n) return 1;

    // cache check
    if (dp[x] != -1) return dp[x];

    // transitions
    int ans = 0;
    ans += rec(x + 1);
    ans += rec(x + 2);

    // save and return
    return dp[x] = ans;
}

int main() {
    cin >> n;
    dp.assign(n + 2, -1);
    cout << rec(0) << "\n";
    return 0;
}
```

### Bottom-up C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<long long> dp(n + 2, 0);
    dp[n] = 1;

    for (int x = n - 1; x >= 0; x--) {
        dp[x] = dp[x + 1] + dp[x + 2];
    }

    cout << dp[0] << "
";
    return 0;
}
```



## Top-down vs Bottom-up working

### Top-down call tree

```text
rec(0)
├── rec(1)
│   ├── rec(2)
│   │   ├── rec(3)
│   │   └── rec(4)
│   └── rec(3)  <- reused from cache after first calculation
└── rec(2)      <- reused from cache
```

Top-down meaning:

```text
I am standing at stair x.
How many ways can I finish?
Answer = ways after taking 1 step + ways after taking 2 steps.
```

### Bottom-up fill order

```text
Base:
dp[n] = 1
values after n = 0

Fill:
for x from n-1 down to 0:
    dp[x] = dp[x+1] + dp[x+2]
```

Bottom-up table for `n = 5`:

```text
x      5  4  3  2  1  0
dp[x]  1  1  2  3  5  8
```

## Index-by-index dry run for `n = 5`

```text
DP(5) = 1
DP(4) = DP(5) + DP(6) = 1 + 0 = 1
DP(3) = DP(4) + DP(5) = 1 + 1 = 2
DP(2) = DP(3) + DP(4) = 2 + 1 = 3
DP(1) = DP(2) + DP(3) = 3 + 2 = 5
DP(0) = DP(1) + DP(2) = 5 + 3 = 8
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

Because subset `{2, 4}` has sum `6`.

## Framework

### 1. Look for the form

Problem asks to choose some elements.

```text
Form 1: Take / Not Take
```

### 2. Decide state and meaning

```text
DP(level, rem) = can we make sum rem using elements from level to n-1?
```

### 3. DP expression

```text
DP(level, rem) = DP(level + 1, rem) OR DP(level + 1, rem - a[level])
```

### 4. Transitions

```text
not take a[level] -> DP(level + 1, rem)
take a[level]     -> DP(level + 1, rem - a[level])
```

Diagram:

```text
DP(level, rem)
├── skip current item  -> DP(level + 1, rem)
└── take current item  -> DP(level + 1, rem - a[level])
```

### 5. Time complexity

```text
#States = n * T
Transitions per state = 2
TC = O(n * T * 2) = O(nT)
SC = O(nT)
```

## Decision Tree

```text
Need to check if some subset makes target T?
├── Each element can be used at most once? YES
│   └── Form = Take / Not Take
│       └── State = (level, remaining target)
└── Answer type = possible / impossible
```

## Step-by-Step Working

```text
1. At index level, decide whether to use a[level].
2. Skip keeps rem unchanged.
3. Take reduces rem by a[level].
4. If rem becomes 0 at the end, subset is valid.
5. If rem becomes negative, path is invalid.
6. Use OR because any valid path is enough.
```

### 6. Code

### Top-down C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n, T;
vector<int> a;
vector<vector<int>> dp;

int rec(int level, int rem) {
    // pruning
    if (rem < 0) return 0;

    // base case
    if (level == n) return rem == 0;

    // cache check
    if (dp[level][rem] != -1) return dp[level][rem];

    // transitions
    int ans = 0;
    ans |= rec(level + 1, rem); // not take
    if (rem >= a[level]) {
        ans |= rec(level + 1, rem - a[level]); // take
    }

    // save and return
    return dp[level][rem] = ans;
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

### Bottom-up C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, T;
    cin >> n >> T;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];

    vector<vector<int>> dp(n + 1, vector<int>(T + 1, 0));
    dp[0][0] = 1;

    for (int i = 0; i < n; i++) {
        for (int sum = 0; sum <= T; sum++) {
            if (!dp[i][sum]) continue;
            dp[i + 1][sum] = 1; // skip
            if (sum + a[i] <= T) dp[i + 1][sum + a[i]] = 1; // take
        }
    }

    cout << (dp[n][T] ? "YES" : "NO") << "
";
    return 0;
}
```



## Top-down vs Bottom-up working

### Top-down decision tree

```text
rec(level, rem)
├── skip a[level] -> rec(level+1, rem)
└── take a[level] -> rec(level+1, rem-a[level])
```

Top-down meaning:

```text
At index level, I decide whether this element participates in the subset.
The remaining target rem tells me how much more sum I need.
```

### Bottom-up table meaning

```text
dp[i][sum] = possible to make sum using first i elements
```

Fill direction:

```text
dp[0][0] = true
for i from 0 to n-1:
    carry skip: dp[i+1][sum] = true
    carry take: dp[i+1][sum+a[i]] = true
```

Mini table idea for `a=[3,2,4,7]`, `T=6`:

```text
After 0 items: {0}
After 3:       {0,3}
After 2:       {0,2,3,5}
After 4:       {0,2,3,4,5,6,7,9}
Target 6 exists -> YES
```

## Index-by-index dry run

```text
a = [3, 2, 4, 7], T = 6

DP(0, 6)
├── skip 3 -> DP(1, 6)
│   ├── skip 2 -> DP(2, 6)
│   └── take 2 -> DP(2, 4)
│       └── take 4 -> DP(3, 0) -> true
└── take 3 -> DP(1, 3)

Answer = true
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

Format:

```text
n W
weight value
weight value
...
```

## Expected Output

```text
7
```

Take item 1 and item 2: weight `2 + 3 = 5`, value `3 + 4 = 7`.

## Framework

### 1. Look for the form

Each item has two choices: take or not take.

```text
Form 1: Take / Not Take
```

### 2. Decide state and meaning

```text
DP(level, capLeft) = max value possible using items from level to n-1 with remaining capacity capLeft
```

### 3. DP expression

```text
DP(level, capLeft) = max(
    DP(level + 1, capLeft),
    value[level] + DP(level + 1, capLeft - weight[level])
)
```

### 4. Transitions

```text
skip item -> same capacity
take item -> capacity reduces by weight[level]
```

### 5. Time complexity

```text
#States = n * W
Transitions per state = 2
TC = O(nW)
SC = O(nW)
```

## Decision Tree

```text
Need maximize value under capacity?
├── Each item can be chosen once? YES
│   └── Form = 0/1 Take / Not Take
│       └── State = (level, capacity left)
└── Answer type = maximize
```

## Step-by-Step Working

```text
1. At item level, capacity left is capLeft.
2. Option 1: skip item, value does not change.
3. Option 2: take item if weight fits.
4. Taking item adds value[level] and reduces capacity.
5. Choose maximum of skip/take.
6. Cache because same (level, capLeft) repeats.
```

### 6. Code

### Top-down C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n, W;
vector<int> wt, val;
vector<vector<int>> dp;

int rec(int level, int capLeft) {
    // pruning
    if (capLeft < 0) return -1000000000;

    // base case
    if (level == n) return 0;

    // cache check
    if (dp[level][capLeft] != -1) return dp[level][capLeft];

    // transitions
    int ans = rec(level + 1, capLeft); // skip

    if (wt[level] <= capLeft) {
        ans = max(ans, val[level] + rec(level + 1, capLeft - wt[level]));
    }

    // save and return
    return dp[level][capLeft] = ans;
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

### Bottom-up C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, W;
    cin >> n >> W;
    vector<int> wt(n), val(n);
    for (int i = 0; i < n; i++) cin >> wt[i] >> val[i];

    vector<vector<int>> dp(n + 1, vector<int>(W + 1, 0));

    for (int i = 0; i < n; i++) {
        for (int cap = 0; cap <= W; cap++) {
            dp[i + 1][cap] = max(dp[i + 1][cap], dp[i][cap]); // skip
            if (cap + wt[i] <= W) {
                dp[i + 1][cap + wt[i]] = max(dp[i + 1][cap + wt[i]], dp[i][cap] + val[i]);
            }
        }
    }

    cout << dp[n][W] << "
";
    return 0;
}
```



## Top-down vs Bottom-up working

### Top-down decision tree

```text
rec(level, capLeft)
├── skip item[level]
└── take item[level] if weight <= capLeft
```

Top-down meaning:

```text
At item level, capacity left is capLeft.
I either ignore the item or consume capacity and gain value.
```

### Bottom-up table meaning

```text
dp[i][cap] = max value using first i items with capacity cap
```

Fill direction:

```text
for each item i:
    for each capacity cap:
        skip = dp[i][cap]
        take = value[i] + dp[i][cap-weight[i]]
```

Table intuition for capacity `5`:

```text
Item 0 (wt=2,val=3): best at cap 2..5 becomes 3
Item 1 (wt=3,val=4): cap 5 can combine item0+item1 = 7
Item 2 (wt=4,val=5): cap 5 max remains 7
```

## Index-by-index dry run

```text
items:
0: wt=2 val=3
1: wt=3 val=4
2: wt=4 val=5
W=5

DP(0,5)
├── skip item0 -> DP(1,5)
│   ├── take item1 -> 4 + DP(2,2) = 4
│   └── take item2 -> 5 + DP(3,1) = 5
│   best = 5
└── take item0 -> 3 + DP(1,3)
    └── take item1 -> 3 + 4 + DP(2,0) = 7

Answer = 7
```

---

# 6. Form 1 — Vacation / No Consecutive Same Activity

## Problem

For `n` days, each day has 3 activity scores. You cannot choose the same activity on consecutive days. Maximize total score.

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

Choose activity C, B, C: `70 + 50 + 90 = 210`.

## Framework

### 1. Look for the form

Every day you choose one option under a restriction.

```text
Form 1: Choice DP with previous state
```

### 2. Decide state and meaning

```text
DP(day, prev) = max score from day to n-1 if previous day activity was prev
```

`prev = 0,1,2` for activities. Use `prev = 3` as no previous activity.

### 3. DP expression

```text
DP(day, prev) = max(points[day][act] + DP(day + 1, act))
for all act != prev
```

### 4. Transitions

```text
try activity 0 if prev != 0
try activity 1 if prev != 1
try activity 2 if prev != 2
```

### 5. Time complexity

```text
#States = n * 4
Transitions per state = 3
TC = O(n * 4 * 3) = O(n)
SC = O(n * 4)
```

## Decision Tree

```text
Need choose one activity each day?
├── Choice depends on previous day? YES
│   └── State = (day, previous activity)
└── Answer type = maximize total score
```

## Step-by-Step Working

```text
1. At each day, try activity A/B/C.
2. Reject the activity if it equals previous activity.
3. Add today's points.
4. Move to next day with current activity as previous.
5. Take maximum among valid activities.
```

### 6. Code

### Top-down C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n;
vector<array<int, 3>> points;
vector<vector<int>> dp;

int rec(int day, int prev) {
    // pruning: none needed

    // base case
    if (day == n) return 0;

    // cache check
    if (dp[day][prev] != -1) return dp[day][prev];

    // transitions
    int ans = 0;
    for (int act = 0; act < 3; act++) {
        if (act == prev) continue;
        ans = max(ans, points[day][act] + rec(day + 1, act));
    }

    // save and return
    return dp[day][prev] = ans;
}

int main() {
    cin >> n;
    points.resize(n);

    for (int i = 0; i < n; i++) {
        cin >> points[i][0] >> points[i][1] >> points[i][2];
    }

    dp.assign(n + 1, vector<int>(4, -1));

    cout << rec(0, 3) << "\n";
    return 0;
}
```

### Bottom-up C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;
    vector<array<int, 3>> points(n);
    for (int i = 0; i < n; i++) cin >> points[i][0] >> points[i][1] >> points[i][2];

    vector<array<int, 3>> dp(n);
    for (int act = 0; act < 3; act++) dp[0][act] = points[0][act];

    for (int day = 1; day < n; day++) {
        for (int act = 0; act < 3; act++) {
            dp[day][act] = points[day][act];
            int bestPrev = 0;
            for (int prev = 0; prev < 3; prev++) {
                if (prev == act) continue;
                bestPrev = max(bestPrev, dp[day - 1][prev]);
            }
            dp[day][act] += bestPrev;
        }
    }

    cout << max({dp[n - 1][0], dp[n - 1][1], dp[n - 1][2]}) << "
";
    return 0;
}
```



## Top-down vs Bottom-up working

### Top-down state tree

```text
rec(day, prev)
├── choose A if prev != A
├── choose B if prev != B
└── choose C if prev != C
```

Top-down meaning:

```text
The current day depends only on previous activity.
So previous activity is enough state; full history is not needed.
```

### Bottom-up table meaning

```text
dp[day][act] = best score up to this day if I choose act today
```

Fill direction:

```text
dp[0][act] = points[0][act]
for day from 1 to n-1:
    dp[day][act] = points[day][act] + max(dp[day-1][other act])
```

Index-by-index idea:

```text
Day 0: choose each activity directly.
Day 1: for each activity, take best previous different activity.
Day 2: repeat same rule.
Answer = max of last day row.
```

## Index-by-index dry run

```text
Day 0: 10 40 70
Day 1: 20 50 80
Day 2: 30 60 90

Start DP(0, none)
Try A: 10 + DP(1, A)
Try B: 40 + DP(1, B)
Try C: 70 + DP(1, C)

If choose C on day 0:
DP(1, C): can choose A or B
choose B = 50 + DP(2, B)
DP(2, B): can choose A or C -> choose C = 90
Total = 70 + 50 + 90 = 210
```

---

# 7. Form 1 — Boredom / Delete and Earn

## Problem

Given numbers. If you take value `x`, you earn `x * frequency[x]`, but you cannot take `x - 1` or `x + 1`. Maximize points.

## Input

```text
6
1 2 2 3 3 3
```

## Expected Output

```text
9
```

Take all `3`s: `3 * 3 = 9`.

## Framework

### 1. Look for the form

Choosing value `x` blocks neighboring values.

```text
Form 1 transformed by value frequency
```

### 2. Decide state and meaning

```text
DP(x) = max points considering values from x to maxValue
```

### 3. DP expression

```text
DP(x) = max(
    DP(x + 1),
    freq[x] * x + DP(x + 2)
)
```

### 4. Transitions

```text
skip x -> x + 1
take x -> gain freq[x] * x and jump to x + 2
```

### 5. Time complexity

```text
#States = maxValue
Transitions per state = 2
TC = O(maxValue)
SC = O(maxValue)
```

## Decision Tree

```text
Choosing x deletes x-1 and x+1?
├── Convert array to frequency/gain by value
├── Looks like house robber on values
│   └── State = value x
└── Transition = skip x OR take x and jump x+2
```

## Step-by-Step Working

```text
1. Count frequency of every value.
2. Convert each value x into gain[x] = x * freq[x].
3. For every x, choose skip or take.
4. Skip x means go to x+1.
5. Take x means add gain[x] and go to x+2.
6. Choose maximum.
```

### 6. Code

### Top-down C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n, mx;
vector<long long> freq, dp;

long long rec(int x) {
    // pruning / base case
    if (x > mx) return 0;

    // cache check
    if (dp[x] != -1) return dp[x];

    // transitions
    long long skip = rec(x + 1);
    long long take = freq[x] * x + rec(x + 2);

    // save and return
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

### Bottom-up C++ Code

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

    vector<long long> gain(mx + 1, 0);
    for (int x : a) gain[x] += x;

    vector<long long> dp(mx + 1, 0);
    if (mx >= 1) dp[1] = gain[1];

    for (int x = 2; x <= mx; x++) {
        dp[x] = max(dp[x - 1], gain[x] + dp[x - 2]);
    }

    cout << dp[mx] << "
";
    return 0;
}
```



## Top-down vs Bottom-up working

### Top-down decision tree by value

```text
rec(x)
├── skip value x -> rec(x+1)
└── take value x -> gain[x] + rec(x+2)
```

Top-down meaning:

```text
The original array becomes a value-frequency problem.
Taking x deletes x-1 and x+1, so after taking x we jump to x+2.
```

### Bottom-up table meaning

```text
dp[x] = best score considering values from 1..x
```

Fill direction:

```text
dp[0] = 0
dp[1] = gain[1]
for x from 2 to maxValue:
    dp[x] = max(dp[x-1], gain[x] + dp[x-2])
```

For input `[1,2,2,3,3,3]`:

```text
gain[1]=1, gain[2]=4, gain[3]=9

dp[1]=1
dp[2]=max(1,4)=4
dp[3]=max(4,9+1)=10
```

## Index-by-index dry run

```text
a = [1, 2, 2, 3, 3, 3]
freq[1] = 1 -> gain 1
freq[2] = 2 -> gain 4
freq[3] = 3 -> gain 9

DP(3) = max(skip DP(4)=0, take 9 + DP(5)=9) = 9
DP(2) = max(skip DP(3)=9, take 4 + DP(4)=4) = 9
DP(1) = max(skip DP(2)=9, take 1 + DP(3)=10) = 10
```

Note: In this exact input, taking `1` and `3` is allowed, so best is `10`, not `9`. Correct expected output:

```text
10
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

## Framework

### 1. Look for the form

Question asks for best subsequence ending at each index.

```text
Form 2: Ending at i
```

### 2. Decide state and meaning

```text
DP(i) = length of LIS ending exactly at index i
```

### 3. DP expression

```text
DP(i) = 1 + max(DP(j)) for all j < i and a[j] < a[i]
```

If no valid `j`, then:

```text
DP(i) = 1
```

### 4. Transitions

```text
For every previous index j:
if a[j] < a[i], then extend LIS ending at j.
```

### 5. Time complexity

```text
#States = n
Average transition cost = scan all previous j = O(n)
TC = O(n * n) = O(n²)
SC = O(n)
```

## Decision Tree

```text
Need longest increasing subsequence?
├── Subsequence keeps relative order
├── Best answer can end at each index i
│   └── State = LIS ending at i
└── Transition = extend from previous smaller a[j]
```

## Step-by-Step Working

```text
1. Let dp[i] start as 1 because every element alone is LIS length 1.
2. For each previous j < i, check if a[j] < a[i].
3. If yes, extend LIS ending at j.
4. dp[i] = max(dp[i], dp[j] + 1).
5. Final answer is max dp[i].
```

### 6. Code

### Original / Primary C++ Code

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

### Top-down C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n;
vector<int> a, dp;

int rec(int i) {
    if (dp[i] != -1) return dp[i];
    int ans = 1;
    for (int j = 0; j < i; j++) {
        if (a[j] < a[i]) ans = max(ans, rec(j) + 1);
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

    cout << ans << "
";
    return 0;
}
```

### Bottom-up C++ Code

The code above in the original `### 6. Code` section is the standard bottom-up version.



## Top-down vs Bottom-up working

### Top-down thinking

For LIS, bottom-up is usually cleaner, but top-down meaning can be:

```text
rec(i) = LIS ending exactly at i
rec(i) depends on all rec(j), j < i and a[j] < a[i]
```

Dependency tree:

```text
rec(5) for value 4
├── rec(1) value 1
├── rec(3) value 2
└── rec(4) value 3
```

### Bottom-up fill order

```text
for i from 0 to n-1:
    dp[i] = 1
    check all j < i
    if a[j] < a[i], dp[i] = max(dp[i], dp[j]+1)
```

Why left-to-right works:

```text
When computing dp[i], every previous dp[j] is already known.
```

## Index-by-index dry run

```text
a = [5, 1, 6, 2, 3, 4]

i=0, a[i]=5 -> dp[0]=1
state: [1]

i=1, a[i]=1 -> no previous smaller -> dp[1]=1
state: [1,1]

i=2, a[i]=6 -> 5<6 and 1<6 -> max(1+1,1+1)=2
state: [1,1,2]

i=3, a[i]=2 -> 1<2 -> dp[3]=2
state: [1,1,2,2]

i=4, a[i]=3 -> 1<3, 2<3 -> dp[4]=3
state: [1,1,2,2,3]

i=5, a[i]=4 -> 1<4,2<4,3<4 -> dp[5]=4
state: [1,1,2,2,3,4]

Answer = 4
```

---

# 9. Form 2 — LIS O(N log N)

## Problem

Same LIS problem, but optimized.

## Input

```text
9
1 5 7 10 9 6 8 2 3
```

## Expected Output

```text
4
```

## Framework

### 1. Look for the form

Still LIS, but optimized by maintaining best partial solutions.

```text
Form 2 optimized: Ending-at-i idea + binary search on tails
```

### 2. Decide state and meaning

```text
tail[len] = smallest possible ending value of an increasing subsequence of length len + 1
```

### 3. DP expression

Not normal recursive DP. This is optimized partial-solution maintenance:

```text
For each x:
  find first tail[pos] >= x
  tail[pos] = x
```

### 4. Transitions

```text
x extends tail if x is larger than all tails
otherwise x replaces first tail >= x
```

### 5. Time complexity

```text
#States = n processed elements
Average transition cost = binary search = log n
TC = O(n log n)
SC = O(n)
```

## Decision Tree

```text
Need LIS length only, not actual subsequence?
├── O(N²) too slow?
├── Maintain best possible tail for each length
│   └── Use binary search lower_bound
└── Answer = tail.size()
```

## Step-by-Step Working

```text
1. Process elements from left to right.
2. tail[len] stores the smallest ending value for subsequence length len+1.
3. For x, find first tail[pos] >= x.
4. Replace tail[pos] with x.
5. If no such position exists, append x.
6. Smaller ending value is better for future extension.
```

### 6. Code

### Original / Primary C++ Code

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

### Top-down Reference C++ Code — O(N²)

```cpp
#include <bits/stdc++.h>
using namespace std;

int n;
vector<int> a, dp;

int rec(int i) {
    if (dp[i] != -1) return dp[i];
    int ans = 1;
    for (int j = 0; j < i; j++) {
        if (a[j] < a[i]) ans = max(ans, rec(j) + 1);
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
    cout << ans << "
";
    return 0;
}
```

### Bottom-up Optimized C++ Code

The original code in this section is the optimized left-to-right `tail` solution.



## Step-by-step optimized working

This is not classic recursive DP. It is a greedy + binary-search optimization of LIS.

### Meaning of `tail`

```text
tail[k] = smallest possible ending value of an increasing subsequence of length k+1
```

Why replace works:

```text
Smaller tail value is better because it gives more chance to extend later.
Length does not decrease when replacing.
```

### Index-by-index decision rule

```text
For each x:
1. Find first position where tail[pos] >= x.
2. Replace tail[pos] with x.
3. If no such position, append x.
```

Bottom-up style:

```text
Process array left to right.
Maintain best tail values after each prefix.
Answer = tail.size().
```

## Index-by-index dry run

```text
a = [1, 5, 7, 10, 9, 6, 8, 2, 3]

tail after each index:
0: x=1  -> [1]
1: x=5  -> [1,5]
2: x=7  -> [1,5,7]
3: x=10 -> [1,5,7,10]
4: x=9  -> [1,5,7,9]
5: x=6  -> [1,5,6,9]
6: x=8  -> [1,5,6,8]
7: x=2  -> [1,2,6,8]
8: x=3  -> [1,2,3,8]

Answer = 4
```

---

# 10. Form 2 — Palindrome Partition Minimum Parts

## Problem

Given a string, split it into the minimum number of palindromic parts.

## Input

```text
abacddaba
```

## Expected Output

```text
3
```

One split: `aba | cddc?` does not match this exact string. For this input one valid minimum style is found by DP. Use the code as source of truth.

Better simple input:

```text
aab
```

Expected output:

```text
2
```

Split: `aa | b`.

## Framework

### 1. Look for the form

We choose the last partition ending at index `i`.

```text
Form 2: Partition ending at i
```

It also uses Form 4 helper: palindrome table `pal[l][r]`.

### 2. Decide state and meaning

```text
DP(i) = minimum palindromic parts needed for prefix s[0..i]
```

### 3. DP expression

```text
DP(i) = min(1 + DP(j - 1))
for every j <= i where s[j..i] is palindrome
```

If `j == 0`:

```text
DP(i) = 1
```

### 4. Transitions

```text
Choose the start j of the last palindrome segment.
```

Diagram:

```text
s[0 .......... i]
       j ----- i   <- last palindrome segment
answer = DP(j-1) + 1
```

### 5. Time complexity

```text
Palindrome precompute states = n²
DP states = n
Transitions per state = n
TC = O(n²)
SC = O(n²)
```

## Decision Tree

```text
Need minimum cuts/parts for a string?
├── Last segment choice matters
├── Need fast palindrome check? YES
│   ├── Precompute pal[l][r]
│   └── DP over prefix ending at i
└── Transition = choose start j of last palindrome
```

## Step-by-Step Working

```text
1. Precompute pal[l][r] for all substrings.
2. Let dp[i] = min parts for prefix s[0..i].
3. For every end i, try every start j.
4. If s[j..i] is palindrome, it can be the last part.
5. Candidate = 1 if j == 0 else dp[j-1] + 1.
6. Take minimum candidate.
```

### 6. Code

### Original / Primary C++ Code

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

### Top-down C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

string s;
int n;
vector<vector<int>> pal;
vector<int> dp;
const int INF = 1e9;

int rec(int i) {
    if (i == n) return 0;
    if (dp[i] != -1) return dp[i];

    int ans = INF;
    for (int j = i; j < n; j++) {
        if (pal[i][j]) ans = min(ans, 1 + rec(j + 1));
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
    cout << rec(0) << "
";
    return 0;
}
```

### Bottom-up C++ Code

The original code in this section is the standard bottom-up prefix DP.



## Top-down vs Bottom-up working

### Top-down partition tree

```text
rec(i) = min parts for suffix s[i..n-1]
Try every end position j:
    if s[i..j] is palindrome:
        answer = 1 + rec(j+1)
```

Tree idea:

```text
rec(0)
├── take s[0..0] if palindrome -> rec(1)
├── take s[0..1] if palindrome -> rec(2)
└── take s[0..2] if palindrome -> rec(3)
```

### Bottom-up prefix table

```text
dp[i] = minimum parts for prefix s[0..i]
```

Fill direction:

```text
for i from 0 to n-1:
    try all j from 0 to i
    if s[j..i] is palindrome:
        dp[i] = min(dp[i], dp[j-1] + 1)
```

Dependency:

```text
dp[i] depends on earlier prefix dp[j-1]
So left-to-right prefix order works.
```

## Index-by-index dry run for `aab`

```text
s = a a b
index: 0 1 2

Palindrome segments:
[0,0] = a true
[1,1] = a true
[2,2] = b true
[0,1] = aa true
[1,2] = ab false
[0,2] = aab false

DP[0]: s[0..0] = a -> 1
DP[1]:
  j=0, s[0..1]=aa true -> 1
  j=1, s[1..1]=a true -> DP[0]+1 = 2
  DP[1]=1
DP[2]:
  j=2, s[2..2]=b true -> DP[1]+1 = 2
  DP[2]=2

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

LCS is `ace`.

## Framework

### 1. Look for the form

Two strings and matching characters.

```text
Form 3: Matching DP
```

### 2. Decide state and meaning

```text
DP(i, j) = LCS length of s[i..n-1] and t[j..m-1]
```

### 3. DP expression

```text
if s[i] == t[j]:
    DP(i, j) = 1 + DP(i + 1, j + 1)
else:
    DP(i, j) = max(DP(i + 1, j), DP(i, j + 1))
```

### 4. Transitions

```text
match both characters
or skip character from s
or skip character from t
```

### 5. Time complexity

```text
#States = n * m
Transitions per state = O(1)
TC = O(nm)
SC = O(nm)
```

## Decision Tree

```text
Two strings and subsequence matching?
├── Characters match? take both
├── Characters do not match? skip one side
└── State = (i, j), current positions in both strings
```

## Step-by-Step Working

```text
1. Compare s[i] and t[j].
2. If equal, take this character and move both pointers.
3. If not equal, try skipping s[i].
4. Also try skipping t[j].
5. Take maximum length.
6. Stop when either string is exhausted.
```

### 6. Code

### Top-down C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

string s, t;
int n, m;
vector<vector<int>> dp;

int rec(int i, int j) {
    // pruning/base case
    if (i == n || j == m) return 0;

    // cache check
    if (dp[i][j] != -1) return dp[i][j];

    // transitions
    int ans = 0;
    if (s[i] == t[j]) {
        ans = 1 + rec(i + 1, j + 1);
    } else {
        ans = max(rec(i + 1, j), rec(i, j + 1));
    }

    // save and return
    return dp[i][j] = ans;
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

### Bottom-up C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    string s, t;
    cin >> s >> t;
    int n = s.size(), m = t.size();

    vector<vector<int>> dp(n + 1, vector<int>(m + 1, 0));

    for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= m; j++) {
            if (s[i - 1] == t[j - 1]) dp[i][j] = 1 + dp[i - 1][j - 1];
            else dp[i][j] = max(dp[i - 1][j], dp[i][j - 1]);
        }
    }

    cout << dp[n][m] << "
";
    return 0;
}
```



## Top-down vs Bottom-up working

### Top-down recursion tree

```text
rec(i,j)
If s[i] == t[j]:
    take both -> rec(i+1,j+1)
Else:
    skip s[i] -> rec(i+1,j)
    skip t[j] -> rec(i,j+1)
```

Meaning:

```text
I am comparing suffix s[i..] with suffix t[j..].
The answer only depends on these two pointers.
```

### Bottom-up table meaning

```text
dp[i][j] = LCS length of s[0..i-1] and t[0..j-1]
```

Fill direction:

```text
for i from 1..n:
    for j from 1..m:
        if s[i-1] == t[j-1]: dp[i][j] = 1 + dp[i-1][j-1]
        else dp[i][j] = max(dp[i-1][j], dp[i][j-1])
```

Table dependency:

```text
dp[i][j] uses:
left      dp[i][j-1]
up        dp[i-1][j]
diagonal  dp[i-1][j-1]
```

## Index-by-index dry run

```text
s = abcde
t = ace

DP(0,0): s[0]=a, t[0]=a match
=> 1 + DP(1,1)

DP(1,1): s[1]=b, t[1]=c no match
=> max(DP(2,1), DP(1,2))

DP(2,1): s[2]=c, t[1]=c match
=> 1 + DP(3,2)

DP(3,2): s[3]=d, t[2]=e no match
=> max(DP(4,2), DP(3,3))

DP(4,2): s[4]=e, t[2]=e match
=> 1 + DP(5,3) = 1

Total = a + c + e = 3
```

---

# 12. Form 3 — Edit Distance

## Problem

Given two strings, find the minimum operations to convert `s` to `t`. Operations: insert, delete, replace.

## Input

```text
horse
ros
```

## Expected Output

```text
3
```

## Framework

### 1. Look for the form

Two strings with edit/matching operations.

```text
Form 3: Matching DP
```

### 2. Decide state and meaning

```text
DP(i, j) = minimum cost to convert s[i..] to t[j..]
```

### 3. DP expression

```text
if s[i] == t[j]:
    DP(i, j) = DP(i + 1, j + 1)
else:
    DP(i, j) = 1 + min(
        DP(i + 1, j),     // delete
        DP(i, j + 1),     // insert
        DP(i + 1, j + 1)  // replace
    )
```

### 4. Transitions

```text
delete from s -> move i
insert into s -> move j
replace       -> move both
```

### 5. Time complexity

```text
#States = n * m
Transitions per state = 3
TC = O(nm)
SC = O(nm)
```

## Decision Tree

```text
Two strings and conversion operations?
├── If chars equal -> no operation
├── Else try delete / insert / replace
└── State = (i, j), suffixes still not processed
```

## Step-by-Step Working

```text
1. If s is exhausted, insert remaining chars of t.
2. If t is exhausted, delete remaining chars of s.
3. If s[i] == t[j], move both pointers free.
4. Otherwise try delete, insert, replace.
5. Add 1 operation cost.
6. Take minimum.
```

### 6. Code

### Top-down C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

string s, t;
int n, m;
vector<vector<int>> dp;

int rec(int i, int j) {
    // base cases
    if (i == n) return m - j;
    if (j == m) return n - i;

    // cache check
    if (dp[i][j] != -1) return dp[i][j];

    // transitions
    if (s[i] == t[j]) {
        return dp[i][j] = rec(i + 1, j + 1);
    }

    int del = 1 + rec(i + 1, j);
    int ins = 1 + rec(i, j + 1);
    int rep = 1 + rec(i + 1, j + 1);

    // save and return
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

### Bottom-up C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    string s, t;
    cin >> s >> t;
    int n = s.size(), m = t.size();

    vector<vector<int>> dp(n + 1, vector<int>(m + 1, 0));

    for (int i = 0; i <= n; i++) dp[i][0] = i;
    for (int j = 0; j <= m; j++) dp[0][j] = j;

    for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= m; j++) {
            if (s[i - 1] == t[j - 1]) dp[i][j] = dp[i - 1][j - 1];
            else {
                int del = dp[i - 1][j];
                int ins = dp[i][j - 1];
                int rep = dp[i - 1][j - 1];
                dp[i][j] = 1 + min({del, ins, rep});
            }
        }
    }

    cout << dp[n][m] << "
";
    return 0;
}
```



## Top-down vs Bottom-up working

### Top-down operation tree

```text
rec(i,j)
├── delete  s[i] -> rec(i+1,j)
├── insert  t[j] -> rec(i,j+1)
└── replace s[i] -> rec(i+1,j+1)
```

If characters match:

```text
No operation needed -> rec(i+1,j+1)
```

### Bottom-up table meaning

```text
dp[i][j] = min operations to convert first i chars of s to first j chars of t
```

Base:

```text
dp[i][0] = i   // delete all
dp[0][j] = j   // insert all
```

Fill:

```text
if s[i-1] == t[j-1]: dp[i][j] = dp[i-1][j-1]
else dp[i][j] = 1 + min(delete, insert, replace)
```

## Index-by-index dry run summary

```text
horse -> ros

horse
replace h -> r: rorse
remove r: rose
remove e: ros

Minimum operations = 3
```

---

# 13. Form 4 — Palindrome Table

## Problem

Given a string, answer whether every substring `s[l..r]` is palindrome.

## Input

```text
abba
```

## Expected Output

```text
pal[0][3] = true
```

## Framework

### 1. Look for the form

The problem asks about substrings / intervals.

```text
Form 4: Interval DP
```

### 2. Decide state and meaning

```text
DP(l, r) = true if s[l..r] is palindrome
```

### 3. DP expression

```text
DP(l, r) = s[l] == s[r] AND DP(l + 1, r - 1)
```

Base:

```text
length 1 -> true
length 2 -> s[l] == s[r]
```

### 4. Transitions

```text
Outer characters must match.
Inner substring must be palindrome.
```

### 5. Time complexity

```text
#States = n² intervals
Transition cost = O(1)
TC = O(n²)
SC = O(n²)
```

## Decision Tree

```text
Need answer for every substring s[l..r]?
├── Interval DP
├── Outer chars must match
└── Inner substring must already be palindrome
```

## Step-by-Step Working

```text
1. All length-1 substrings are palindrome.
2. Length-2 substring is palindrome if both chars match.
3. For length >= 3, compare outer chars.
4. If outer chars match, check inner dp[l+1][r-1].
5. Fill by increasing length.
```

### 6. Code

### Original / Primary C++ Code

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

### Top-down C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

string s;
int n;
vector<vector<int>> dp;

int rec(int l, int r) {
    if (l >= r) return 1;
    if (dp[l][r] != -1) return dp[l][r];
    return dp[l][r] = (s[l] == s[r] && rec(l + 1, r - 1));
}

int main() {
    cin >> s;
    n = s.size();
    dp.assign(n, vector<int>(n, -1));
    cout << (rec(0, n - 1) ? "YES" : "NO") << "
";
    return 0;
}
```

### Bottom-up C++ Code

The original code in this section is the standard bottom-up interval DP.



## Top-down vs Bottom-up working

### Top-down interval logic

```text
rec(l,r)
true if:
1. s[l] == s[r]
2. rec(l+1,r-1) is true
```

Dependency tree:

```text
rec(0,3) for abba
└── check s[0]==s[3]
    └── rec(1,2) for bb
```

### Bottom-up interval order

```text
Fill by length:
length 1 first
length 2 second
length 3 third
...
```

Why by length:

```text
dp[l][r] depends on dp[l+1][r-1], which is a smaller interval.
```

## Index-by-index dry run for `abba`

```text
Length 1:
a, b, b, a -> true

Length 2:
ab false
bb true
ba false

Length 3:
abb false
bba false

Length 4:
abba:
s[0] == s[3] and dp[1][2] is true
=> true
```

---

# 14. Form 4 — Matrix Chain Multiplication

## Problem

Given matrix dimensions, find minimum multiplication cost.

If matrices are:

```text
A1 = 10 x 20
A2 = 20 x 30
A3 = 30 x 40
```

Input dimensions array is:

```text
10 20 30 40
```

## Input

```text
4
10 20 30 40
```

## Expected Output

```text
18000
```

## Framework

### 1. Look for the form

We solve intervals of matrices.

```text
Form 4: Interval DP
```

### 2. Decide state and meaning

```text
DP(l, r) = minimum cost to multiply matrices from l to r
```

### 3. DP expression

```text
DP(l, r) = min over k:
    DP(l, k) + DP(k + 1, r) + dim[l] * dim[k + 1] * dim[r + 1]
```

### 4. Transitions

```text
Choose last split point k.
```

Diagram:

```text
Matrices l ........ r
Split:
l .... k | k+1 .... r
```

### 5. Time complexity

```text
#States = n² intervals
Transitions per state = n split points
TC = O(n³)
SC = O(n²)
```

## Decision Tree

```text
Need best way to split/multiply interval?
├── Interval DP
├── State = matrix range [l..r]
└── Transition = choose split k
```

## Step-by-Step Working

```text
1. A single matrix has cost 0.
2. For interval [l..r], try every split k.
3. Left side cost = dp[l][k].
4. Right side cost = dp[k+1][r].
5. Merge cost depends on dimensions.
6. Take minimum over all splits.
```

### 6. Code

### Original / Primary C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int m;
    cin >> m; // number of dimensions
    vector<int> dim(m);
    for (int i = 0; i < m; i++) cin >> dim[i];

    int n = m - 1; // number of matrices
    const int INF = 1e9;
    vector<vector<int>> dp(n, vector<int>(n, 0));

    for (int len = 2; len <= n; len++) {
        for (int l = 0; l + len - 1 < n; l++) {
            int r = l + len - 1;
            dp[l][r] = INF;

            for (int k = l; k < r; k++) {
                int cost = dp[l][k] + dp[k + 1][r]
                         + dim[l] * dim[k + 1] * dim[r + 1];
                dp[l][r] = min(dp[l][r], cost);
            }
        }
    }

    cout << dp[0][n - 1] << "\n";
    return 0;
}
```

### Top-down C++ Code

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
    cout << rec(0, n - 1) << "
";
    return 0;
}
```

### Bottom-up C++ Code

The original code in this section is the standard bottom-up interval DP.



## Top-down vs Bottom-up working

### Top-down split tree

```text
rec(l,r)
Try every split k:
    left cost  = rec(l,k)
    right cost = rec(k+1,r)
    merge cost = dim[l] * dim[k+1] * dim[r+1]
```

Tree idea:

```text
rec(0,2)
├── split k=0: A1 | A2 A3
└── split k=1: A1 A2 | A3
```

### Bottom-up interval order

```text
length 1: cost = 0
length 2: compute pair multiplication
length 3: use length 1 and 2 answers
```

Why by length:

```text
Every split creates smaller intervals, so smaller lengths must be ready first.
```

## Index-by-index dry run

```text
A1: 10x20
A2: 20x30
A3: 30x40

Option 1: (A1 A2) A3
A1A2 cost = 10*20*30 = 6000
result = 10x30
then result*A3 cost = 10*30*40 = 12000
total = 18000

Option 2: A1 (A2 A3)
A2A3 cost = 20*30*40 = 24000
result = 20x40
then A1*result cost = 10*20*40 = 8000
total = 32000

Answer = 18000
```

---

# 15. Form 5 — Divisor Game

## Problem

Given number `x`. A player can move from `x` to `x - d`, where `d` is a divisor of `x` and `1 <= d < x`. The player who cannot move loses. Determine whether the current player wins.

## Input

```text
6
```

## Expected Output

```text
WIN
```

## Framework

### 1. Look for the form

There are two players alternating turns.

```text
Form 5: Game DP
```

### 2. Decide state and meaning

```text
DP(x) = can current player win when number is x?
```

### 3. DP expression

```text
DP(x) = true if there exists a valid divisor d such that DP(x - d) == false
```

### 4. Transitions

```text
for each divisor d of x:
    move to x - d
```

### 5. Time complexity

```text
#States = n
Transitions per state = number of divisors / scan up to sqrt(n)
TC = O(n * sqrt(n)) if divisors generated by scan
SC = O(n)
```

## Decision Tree

```text
Two players alternate turns?
├── Current wins if any move makes opponent lose
├── Current loses if all moves make opponent win
└── State = current number x
```

## Step-by-Step Working

```text
1. Base: x=1 has no valid move, so lose.
2. For current x, list valid divisors d.
3. Move to x-d.
4. If any child state is losing, current is winning.
5. Otherwise current is losing.
```

### 6. Code

### Top-down C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n;
vector<int> dp;

int rec(int x) {
    // base case
    if (x == 1) return 0; // losing

    // cache check
    if (dp[x] != -1) return dp[x];

    // transitions
    int win = 0;
    for (int d = 1; d * d <= x; d++) {
        if (x % d == 0) {
            vector<int> divisors = {d, x / d};
            for (int move : divisors) {
                if (move >= 1 && move < x) {
                    if (rec(x - move) == 0) win = 1;
                }
            }
        }
    }

    // save and return
    return dp[x] = win;
}

int main() {
    cin >> n;
    dp.assign(n + 1, -1);
    cout << (rec(n) ? "WIN" : "LOSE") << "\n";
    return 0;
}
```

### Bottom-up C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;
    vector<int> dp(n + 1, 0); // 0 = LOSE, 1 = WIN

    for (int x = 2; x <= n; x++) {
        for (int d = 1; d * d <= x; d++) {
            if (x % d != 0) continue;
            vector<int> divisors = {d, x / d};
            for (int move : divisors) {
                if (move >= 1 && move < x && dp[x - move] == 0) {
                    dp[x] = 1;
                }
            }
        }
    }

    cout << (dp[n] ? "WIN" : "LOSE") << "
";
    return 0;
}
```



## Top-down vs Bottom-up working

### Top-down game tree

```text
rec(x)
Current player wins if any move sends opponent to losing state.
```

Decision tree:

```text
rec(6)
├── move 1 -> rec(5)
├── move 2 -> rec(4)
└── move 3 -> rec(3)

If any child is LOSE, rec(6) = WIN.
```

### Bottom-up game table

```text
dp[x] = WIN/LOSE for number x
```

Fill direction:

```text
dp[1] = LOSE
for x from 2 to n:
    dp[x] = WIN if exists divisor d where dp[x-d] == LOSE
```

Game DP rule:

```text
WIN  = I can force opponent into LOSE
LOSE = all moves give opponent WIN
```

## Index-by-index dry run for `x = 6`

```text
DP(1) = lose

DP(2): divisor 1 -> move to 1 losing, so DP(2)=win
DP(3): divisor 1 -> move to 2 winning, no other valid divisor, so DP(3)=lose
DP(4): divisor 1 -> 3 losing, so DP(4)=win
DP(5): divisor 1 -> 4 winning, so DP(5)=lose
DP(6): divisors 1,2,3
  move 1 -> DP(5)=lose => current can win
Answer = WIN
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

Path: `1 -> 3 -> 1 -> 1 -> 1`.

## Framework

### 1. Look for the form

Grid movement with choices right/down.

```text
Grid DP
```

### 2. Decide state and meaning

```text
DP(r, c) = minimum cost to reach destination from cell (r, c)
```

### 3. DP expression

```text
DP(r, c) = grid[r][c] + min(DP(r + 1, c), DP(r, c + 1))
```

### 4. Transitions

```text
move down  -> r + 1, c
move right -> r, c + 1
```

### 5. Time complexity

```text
#States = n * m
Transitions per state = 2
TC = O(nm)
SC = O(nm)
```

## Decision Tree

```text
Grid + move restrictions?
├── Can move only right/down
├── State = cell (r,c)
└── Transition = right or down
```

## Step-by-Step Working

```text
1. At each cell, pay grid[r][c].
2. Move right or down.
3. Invalid outside-grid move returns INF.
4. Destination returns its own cost.
5. Current answer = current cost + min(right, down).
```

### 6. Code

### Top-down C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n, m;
vector<vector<int>> grid, dp;
const int INF = 1e9;

int rec(int r, int c) {
    // pruning
    if (r >= n || c >= m) return INF;

    // base case
    if (r == n - 1 && c == m - 1) return grid[r][c];

    // cache check
    if (dp[r][c] != -1) return dp[r][c];

    // transitions
    int ans = grid[r][c] + min(rec(r + 1, c), rec(r, c + 1));

    // save and return
    return dp[r][c] = ans;
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

### Bottom-up C++ Code

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

    cout << dp[0][0] << "
";
    return 0;
}
```



## Top-down vs Bottom-up working

### Top-down recursion tree

```text
rec(r,c)
├── go down  -> rec(r+1,c)
└── go right -> rec(r,c+1)
```

Meaning:

```text
From this cell, the best path is current cost + cheaper of right/down future.
```

### Bottom-up table order

Because top-down starts from `(0,0)` and moves to destination, bottom-up starts from destination and moves backward.

```text
for r from n-1 down to 0:
    for c from m-1 down to 0:
        dp[r][c] = grid[r][c] + min(dp[r+1][c], dp[r][c+1])
```

Dependency:

```text
dp[r][c] uses:
right dp[r][c+1]
down  dp[r+1][c]
```

## Index-by-index dry run

```text
grid:
1 3 1
1 5 1
4 2 1

From bottom-right:
DP(2,2)=1
DP(2,1)=2+1=3
DP(2,0)=4+3=7
DP(1,2)=1+1=2
DP(1,1)=5+min(3,2)=7
DP(1,0)=1+min(7,7)=8
DP(0,2)=1+2=3
DP(0,1)=3+min(7,3)=6
DP(0,0)=1+min(8,6)=7
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

## Framework

### 1. Look for the form

Need to track visited subset. `n` is small.

```text
Bitmask DP
```

### 2. Decide state and meaning

```text
DP(mask, last) = minimum cost to start at 0, visit set mask, and currently be at last
```

### 3. DP expression

```text
DP(mask, last) = min over next not in mask:
    cost[last][next] + DP(mask | (1 << next), next)
```

Base:

```text
if mask contains all cities:
    return cost[last][0]
```

### 4. Transitions

```text
try every unvisited city as next
```

### 5. Time complexity

```text
#States = 2^n * n
Transitions per state = n
TC = O(2^n * n²)
SC = O(2^n * n)
```

## Decision Tree

```text
Need visit all nodes/cities exactly once?
├── n is small, usually <= 20
├── Need remember visited set
│   └── State = (mask, last)
└── Transition = go to unvisited next city
```

## Step-by-Step Working

```text
1. mask stores which cities are visited.
2. last stores current city.
3. Try every city not inside mask.
4. Add travel cost from last to next.
5. Mark next as visited using mask | (1<<next).
6. When all cities are visited, return to city 0.
```

### 6. Code

### Top-down C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n;
vector<vector<int>> cost;
vector<vector<int>> dp;
const int INF = 1e9;

int rec(int mask, int last) {
    // base case
    if (mask == (1 << n) - 1) return cost[last][0];

    // cache check
    if (dp[mask][last] != -1) return dp[mask][last];

    // transitions
    int ans = INF;
    for (int nxt = 0; nxt < n; nxt++) {
        if ((mask & (1 << nxt)) == 0) {
            ans = min(ans, cost[last][nxt] + rec(mask | (1 << nxt), nxt));
        }
    }

    // save and return
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

### Bottom-up C++ Code

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
    int FULL = (1 << n) - 1;
    vector<vector<int>> dp(1 << n, vector<int>(n, INF));
    dp[1][0] = 0;

    for (int mask = 0; mask <= FULL; mask++) {
        for (int last = 0; last < n; last++) {
            if (dp[mask][last] == INF) continue;
            for (int nxt = 0; nxt < n; nxt++) {
                if (mask & (1 << nxt)) continue;
                int nmask = mask | (1 << nxt);
                dp[nmask][nxt] = min(dp[nmask][nxt], dp[mask][last] + cost[last][nxt]);
            }
        }
    }

    int ans = INF;
    for (int last = 0; last < n; last++) {
        ans = min(ans, dp[FULL][last] + cost[last][0]);
    }

    cout << ans << "
";
    return 0;
}
```



## Top-down vs Bottom-up working

### Top-down state tree

```text
rec(mask,last)
Try every unvisited next city:
    rec(mask | (1<<next), next)
```

Meaning:

```text
mask tells which cities are already visited.
last tells current position.
Together they fully describe the future.
```

### Bottom-up mask order

```text
dp[mask][last] = min cost to reach state mask ending at last
```

Fill direction:

```text
Start: dp[0001][0] = 0
For every mask:
    for every last inside mask:
        try every next not in mask
```

State expansion:

```text
0001,last=0
├── 0011,last=1
├── 0101,last=2
└── 1001,last=3
```

## Index-by-index dry run idea

```text
Start: mask = 0001, last = 0
Try city 1 -> mask 0011, last 1
Try city 2 -> mask 0101, last 2
Try city 3 -> mask 1001, last 3

When mask = 1111:
return cost[last][0]
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

## Framework

### 1. Look for the form

Input is a tree. Answer can be built from child subtrees.

```text
Tree DP
```

### 2. Decide state and meaning

```text
DP(node) = maximum downward path length starting from node
```

Global answer:

```text
diameter = max of best two child heights + 2
```

### 3. DP expression

```text
DP(node) = 1 + max(DP(child))
```

For diameter through node:

```text
best1 + best2 + 2
```

### 4. Transitions

```text
DFS children.
Keep top two downward heights.
```

### 5. Time complexity

```text
#States = n nodes
Transitions total = n - 1 edges
TC = O(n)
SC = O(n)
```

## Decision Tree

```text
Input is a tree and answer uses child subtrees?
├── Root the tree anywhere
├── DFS returns best downward height
├── Node answer may combine two best children
└── Global answer = max path through any node
```

## Step-by-Step Working

```text
1. Root tree at node 1.
2. DFS into every child except parent.
3. Each child returns its best downward height.
4. Keep best two child heights.
5. Diameter through current node = best1 + best2 + 2.
6. Return best1 + 1 to parent.
```

### 6. Code

### Top-down C++ Code

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

    // path through node using two best child branches
    diameter = max(diameter, best1 + best2 + 2);

    // downward height
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

### Bottom-up / Postorder C++ Code

Tree DP bottom-up means **postorder DFS**: children are solved before parent. The original code already follows this bottom-up return style.

```cpp
#include <bits/stdc++.h>
using namespace std;

int n, diameter = 0;
vector<vector<int>> g;
vector<int> parent, order, height;

int main() {
    cin >> n;
    g.assign(n + 1, {});
    for (int i = 0; i < n - 1; i++) {
        int u, v;
        cin >> u >> v;
        g[u].push_back(v);
        g[v].push_back(u);
    }

    parent.assign(n + 1, 0);
    height.assign(n + 1, 0);

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

    reverse(order.begin(), order.end()); // postorder-like processing

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

    cout << diameter << "
";
    return 0;
}
```



## Top-down vs Bottom-up working

### Top-down DFS call tree

```text
dfs(1,0)
├── dfs(2,1)
└── dfs(3,1)
    ├── dfs(4,3)
    └── dfs(5,3)
```

Top-down traversal goes from root to leaves, but the DP value is computed while returning back.

### Bottom-up return order

```text
Leaves return height 0 first:
2, 4, 5

Then node 3 computes from children 4 and 5.
Then node 1 computes from children 2 and 3.
```

Tree-like dry run:

```text
Return height to parent:

    1 returns 2
   / \
  2   3 returns 1
     / \
    4   5

At every node:
1. collect child heights
2. keep best two
3. update diameter = best1 + best2 + 2
4. return best1 + 1
```

## Index-by-index dry run

```text
Tree:
    1
   / \
  2   3
     / \
    4   5

DFS leaves:
DP(2)=0, DP(4)=0, DP(5)=0

At node 3:
best child heights = 0 and 0
diameter through 3 = 0 + 0 + 2 = 2
DP(3)=1

At node 1:
child heights: node2=0, node3=1
diameter through 1 = 0 + 1 + 2 = 3
DP(1)=2

Answer = 3
```

---

# 19. Final One-Page Revision

## Universal checklist before coding

```text
1. Form:
2. State:
3. Meaning:
4. DP(...) = ______:
5. Transitions:
6. Base case:
7. Invalid/pruning:
8. TC:
9. Code order:
   - pruning
   - base case
   - cache check
   - transitions
   - save and return
```

## Form recognition cheat sheet

```text
choose / not choose             -> Form 1
ending at i                     -> Form 2
two strings / matching          -> Form 3
substring / interval l..r       -> Form 4
current player win/lose         -> Form 5
grid movement                   -> Grid DP
small n + all subsets           -> Bitmask DP
tree/subtree                    -> Tree DP
```

## Time complexity formula

```text
TC = #States * (1 + average cost of transition)
```

or:

```text
TC = #States * (1 + average number of transitions per state)
```



## Top-down vs Bottom-up conversion checklist

```text
Top-down state:  rec(parameters)
Bottom-up state: dp[parameters]

Top-down base case becomes bottom-up initialization.
Top-down transition becomes bottom-up recurrence.
Top-down dependency direction tells bottom-up loop order.
```

| Top-down pattern | Bottom-up loop order |
|---|---|
| `rec(i)` depends on `rec(i+1)` | loop `i` from right to left |
| `rec(i)` depends on previous `j < i` | loop `i` from left to right |
| `rec(i,j)` strings suffix | loop `i`, `j` from end to start |
| interval `rec(l,r)` | loop by increasing length |
| tree `dfs(node)` | postorder DFS return |
| grid to destination | start from destination and go backward |
| bitmask adds bits | loop masks from small to large |
```

## Mental rules

```text
DP = recursion + cache.
State must uniquely describe the future.
If same state repeats, cache it.
If state has too many dimensions, find a derived variable and remove it.
For interval DP, fill by length.
For matching DP, move pointers.
For game DP, current wins if any move sends opponent to losing state.
For Form 1, think take / not take.
For Form 2, think best answer ending here.
```

