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

### 6. Code

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

### 6. Code

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

### 6. Code

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

### 6. Code

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

### 6. Code

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

### 6. Code

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

### 6. Code

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

### 6. Code

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

### 6. Code

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

### 6. Code

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

### 6. Code

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

### 6. Code

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

### 6. Code

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

### 6. Code

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

### 6. Code

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

### 6. Code

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

