# DP_PROBLEMS_FORMWISE.md — FAANG Dynamic Programming Problem Guide

> Goal: learn DP by **forms**, not by memorizing solutions.  
> For every problem use this framework:
>
> 1. Look for the form of the problem.
> 2. Decide the state and meaning.
> 3. Write: `DP(...) = ______`
> 4. Decide the transitions.
> 5. Check time complexity.
>    - `TC = #States * (1 + Avg cost of transition)`
>    - or `TC = #States * (1 + Avg no. of transitions per state)`
> 6. Code in this order:
>    - pruning
>    - base case
>    - cache check
>    - transitions
>    - save and return

---

## Clickable Index

- [0. Master DP Recognition Map](#0-master-dp-recognition-map)
- [1. Form 0 — Fibonacci / Linear Recurrence](#1-form-0--fibonacci--linear-recurrence)
  - [1.1 Climbing Stairs](#11-climbing-stairs)
- [2. Form 1 — Take / Not Take](#2-form-1--take--not-take)
  - [2.1 House Robber](#21-house-robber)
  - [2.2 Partition Equal Subset Sum](#22-partition-equal-subset-sum)
  - [2.3 Target Sum](#23-target-sum)
- [3. Form 2 — Ending At Index](#3-form-2--ending-at-index)
  - [3.1 Longest Increasing Subsequence](#31-longest-increasing-subsequence)
  - [3.2 Maximum Product Subarray](#32-maximum-product-subarray)
- [4. Form 3 — Matching / Two Strings](#4-form-3--matching--two-strings)
  - [4.1 Longest Common Subsequence](#41-longest-common-subsequence)
  - [4.2 Edit Distance](#42-edit-distance)
  - [4.3 Distinct Subsequences](#43-distinct-subsequences)
- [5. Form 4 — Grid DP](#5-form-4--grid-dp)
  - [5.1 Unique Paths](#51-unique-paths)
  - [5.2 Minimum Path Sum](#52-minimum-path-sum)
- [6. Form 5 — Interval / Left-Right DP](#6-form-5--interval--left-right-dp)
  - [6.1 Palindromic Substrings](#61-palindromic-substrings)
  - [6.2 Burst Balloons](#62-burst-balloons)
- [7. Form 6 — Game DP](#7-form-6--game-dp)
  - [7.1 Predict The Winner](#71-predict-the-winner)
- [8. Form 7 — Partition DP](#8-form-7--partition-dp)
  - [8.1 Word Break](#81-word-break)
  - [8.2 Palindrome Partitioning II](#82-palindrome-partitioning-ii)
- [9. Form 8 — State Machine DP](#9-form-8--state-machine-dp)
  - [9.1 Best Time to Buy and Sell Stock with Cooldown](#91-best-time-to-buy-and-sell-stock-with-cooldown)
- [10. Form 9 — Bitmask DP](#10-form-9--bitmask-dp)
  - [10.1 Matchsticks to Square](#101-matchsticks-to-square)
- [11. Form 10 — Tree DP](#11-form-10--tree-dp)
  - [11.1 House Robber III](#111-house-robber-iii)
- [12. FAANG Hard DP Master Checklist](#12-faang-hard-dp-master-checklist)

---

# 0. Master DP Recognition Map

```text
Question says choose / skip / subset / subsequence
→ Form 1: Take / Not Take

Question says best ending at i
→ Form 2: Ending At Index

Question has two strings / compare i and j
→ Form 3: Matching DP

Question has matrix movement
→ Form 4: Grid DP

Question asks over subarray [l...r], remove/merge/split
→ Form 5: Interval DP

Question says two players / optimal play
→ Form 6: Game DP

Question says split string/array into valid parts
→ Form 7: Partition DP

Question has states like holding/not holding/cooldown
→ Form 8: State Machine DP

Question has small n <= 20 and subset state
→ Form 9: Bitmask DP

Question is on tree and decision depends on children
→ Form 10: Tree DP
```

## Universal Recursive Template

```cpp
int rec(State state) {
    // 1. pruning
    if (invalid(state)) return BAD_VALUE;

    // 2. base case
    if (finished(state)) return BASE_VALUE;

    // 3. cache check
    if (seen[state]) return dp[state];

    // 4. transitions
    int ans = INITIAL_VALUE;
    for (auto next : choices(state)) {
        ans = combine(ans, rec(next));
    }

    // 5. save and return
    seen[state] = true;
    return dp[state] = ans;
}
```

---

# 1. Form 0 — Fibonacci / Linear Recurrence

## Recognition

```text
Current answer depends on previous few answers:
dp[i] = dp[i-1] + dp[i-2]
```

---

## 1.1 Climbing Stairs

### Problem

You are climbing a staircase. It takes `n` steps to reach the top.  
Each time you can climb either `1` or `2` steps.  
Return number of distinct ways.

### Input

```text
n = 5
```

### Expected Output

```text
8
```

### Framework

```text
Form:
Linear recurrence / Fibonacci DP

State:
DP(i) = number of ways to reach step i

DP(i) = DP(i-1) + DP(i-2)

Transitions:
from i-1 jump 1
from i-2 jump 2

TC:
#S = n
Transitions per state = 2
TC = O(n)
SC = O(n), optimized O(1)
```

### Index-by-index dry run

```text
n = 5

dp[0] = 1
dp[1] = 1

i = 2 → dp[2] = dp[1] + dp[0] = 1 + 1 = 2
i = 3 → dp[3] = dp[2] + dp[1] = 2 + 1 = 3
i = 4 → dp[4] = dp[3] + dp[2] = 3 + 2 = 5
i = 5 → dp[5] = dp[4] + dp[3] = 5 + 3 = 8
```

### Top-down C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<int> dp;

    int rec(int i) {
        // pruning
        if (i < 0) return 0;

        // base case
        if (i == 0) return 1;

        // cache check
        if (dp[i] != -1) return dp[i];

        // transitions
        int ans = rec(i - 1) + rec(i - 2);

        // save and return
        return dp[i] = ans;
    }

    int climbStairs(int n) {
        dp.assign(n + 1, -1);
        return rec(n);
    }
};
```

### Bottom-up C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int climbStairs(int n) {
        vector<int> dp(n + 1, 0);
        dp[0] = 1;
        if (n >= 1) dp[1] = 1;

        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }

        return dp[n];
    }
};
```

---

# 2. Form 1 — Take / Not Take

## Recognition

```text
For every item/index:
- take it
- skip it
```

Common words:

```text
subset, subsequence, choose, pick, capacity, target sum, knapsack
```

---

## 2.1 House Robber

### Problem

Given array `nums`, each value is money in a house.  
You cannot rob two adjacent houses. Return maximum money.

### Input

```text
nums = [2,7,9,3,1]
```

### Expected Output

```text
12
```

### Framework

```text
Form:
Take / Not Take with adjacency restriction

State:
DP(i) = max money from index i to end

DP(i) = max(
    skip current = DP(i+1),
    take current = nums[i] + DP(i+2)
)

Transitions:
not take → i+1
take → i+2

TC:
#S = n
Transitions per state = 2
TC = O(n)
SC = O(n)
```

### Dry run

```text
nums = [2,7,9,3,1]

from back:
dp[5] = 0
dp[4] = max(dp[5], 1 + dp[6]) = 1
dp[3] = max(dp[4], 3 + dp[5]) = 3
dp[2] = max(dp[3], 9 + dp[4]) = 10
dp[1] = max(dp[2], 7 + dp[3]) = 10
dp[0] = max(dp[1], 2 + dp[2]) = 12
```

### Top-down C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<int> dp;
    vector<int> a;
    int n;

    int rec(int i) {
        // pruning / base case
        if (i >= n) return 0;

        // cache check
        if (dp[i] != -1) return dp[i];

        // transitions
        int skip = rec(i + 1);
        int take = a[i] + rec(i + 2);

        // save and return
        return dp[i] = max(skip, take);
    }

    int rob(vector<int>& nums) {
        a = nums;
        n = nums.size();
        dp.assign(n, -1);
        return rec(0);
    }
};
```

### Bottom-up C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int rob(vector<int>& nums) {
        int n = nums.size();
        vector<int> dp(n + 2, 0);

        for (int i = n - 1; i >= 0; i--) {
            int skip = dp[i + 1];
            int take = nums[i] + dp[i + 2];
            dp[i] = max(skip, take);
        }

        return dp[0];
    }
};
```

---

## 2.2 Partition Equal Subset Sum

### Problem

Given `nums`, return true if array can be partitioned into two subsets with equal sum.

### Input

```text
nums = [1,5,11,5]
```

### Expected Output

```text
true
```

### Framework

```text
Form:
Take / Not Take + target sum

Observation:
Need subset with sum = total_sum / 2

State:
DP(i, rem) = whether we can make sum rem using items from i...n-1

DP(i, rem) =
    DP(i+1, rem) OR DP(i+1, rem - nums[i])

Transitions:
skip item
take item if nums[i] <= rem

TC:
#S = n * target
Transitions = 2
TC = O(n * target)
SC = O(n * target)
```

### Dry run

```text
nums = [1,5,11,5]
sum = 22
target = 11

Need subset sum 11.

Choices:
i=0, rem=11
take 1 → rem=10
take 5 → rem=5
skip 11
take 5 → rem=0 success

Subset = [1,5,5]
```

### Top-down C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<int> nums;
    vector<vector<int>> dp;
    int n;

    int rec(int i, int rem) {
        // pruning
        if (rem < 0) return 0;

        // base case
        if (rem == 0) return 1;
        if (i == n) return 0;

        // cache check
        if (dp[i][rem] != -1) return dp[i][rem];

        // transitions
        int skip = rec(i + 1, rem);
        int take = rec(i + 1, rem - nums[i]);

        // save and return
        return dp[i][rem] = (skip || take);
    }

    bool canPartition(vector<int>& a) {
        nums = a;
        n = nums.size();

        int sum = accumulate(nums.begin(), nums.end(), 0);
        if (sum % 2) return false;

        int target = sum / 2;
        dp.assign(n, vector<int>(target + 1, -1));

        return rec(0, target);
    }
};
```

### Bottom-up C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    bool canPartition(vector<int>& nums) {
        int sum = accumulate(nums.begin(), nums.end(), 0);
        if (sum % 2) return false;

        int target = sum / 2;
        int n = nums.size();

        vector<vector<bool>> dp(n + 1, vector<bool>(target + 1, false));

        for (int i = 0; i <= n; i++) dp[i][0] = true;

        for (int i = n - 1; i >= 0; i--) {
            for (int rem = 1; rem <= target; rem++) {
                bool skip = dp[i + 1][rem];
                bool take = false;
                if (nums[i] <= rem) take = dp[i + 1][rem - nums[i]];

                dp[i][rem] = skip || take;
            }
        }

        return dp[0][target];
    }
};
```

---

## 2.3 Target Sum

### Problem

Given `nums` and `target`, assign `+` or `-` before every number so expression equals target.  
Return number of ways.

### Input

```text
nums = [1,1,1,1,1]
target = 3
```

### Expected Output

```text
5
```

### Framework

```text
Form:
Take / Not Take variant:
For every number choose +nums[i] or -nums[i]

State:
DP(i, sum) = number of ways from index i when current sum is sum

DP(i, sum) =
    DP(i+1, sum + nums[i])
  + DP(i+1, sum - nums[i])

TC:
#S = n * possible_sum_range
Transitions = 2
TC = O(n * sumRange)
```

### Dry run

```text
[1,1,1,1,1], target = 3

Need four +1 and one -1:
+ + + + - = 3
+ + + - + = 3
+ + - + + = 3
+ - + + + = 3
- + + + + = 3

Answer = 5
```

### Top-down C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<int> nums;
    map<pair<int,int>, int> dp;
    int n, target;

    int rec(int i, int sum) {
        // base case
        if (i == n) {
            return sum == target;
        }

        // cache check
        pair<int,int> key = {i, sum};
        if (dp.count(key)) return dp[key];

        // transitions
        int plus = rec(i + 1, sum + nums[i]);
        int minus = rec(i + 1, sum - nums[i]);

        // save and return
        return dp[key] = plus + minus;
    }

    int findTargetSumWays(vector<int>& a, int t) {
        nums = a;
        target = t;
        n = nums.size();
        dp.clear();
        return rec(0, 0);
    }
};
```

### Bottom-up C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int findTargetSumWays(vector<int>& nums, int target) {
        unordered_map<int,int> dp;
        dp[0] = 1;

        for (int x : nums) {
            unordered_map<int,int> ndp;
            for (auto [sum, ways] : dp) {
                ndp[sum + x] += ways;
                ndp[sum - x] += ways;
            }
            dp = ndp;
        }

        return dp[target];
    }
};
```

---

# 3. Form 2 — Ending At Index

## Recognition

```text
DP(i) = best answer ending exactly at index i
```

Common clues:

```text
subarray ending here
subsequence ending here
best chain ending at i
```

---

## 3.1 Longest Increasing Subsequence

### Problem

Return length of longest strictly increasing subsequence.

### Input

```text
nums = [10,9,2,5,3,7,101,18]
```

### Expected Output

```text
4
```

### Framework

```text
Form:
Ending at i

State:
DP(i) = length of LIS ending exactly at i

DP(i) = 1 + max(DP(j)) where j < i and nums[j] < nums[i]

Transitions:
try all previous j

TC:
#S = n
Avg transitions per state = n
TC = O(n^2)
SC = O(n)
```

### Dry run

```text
nums = [10,9,2,5,3,7,101,18]

i=0, 10 → dp[0]=1
i=1, 9  → no previous smaller → 1
i=2, 2  → no previous smaller → 1
i=3, 5  → previous smaller: 2 → 1+dp[2]=2
i=4, 3  → previous smaller: 2 → 2
i=5, 7  → smaller: 2,5,3 → max(1,2,2)+1=3
i=6,101 → max previous dp + 1 = 4
i=7,18  → max previous dp + 1 = 4

answer = 4
```

### Top-down C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<int> nums, dp;
    int n;

    int rec(int i) {
        // base case handled naturally: every element alone has length 1

        // cache check
        if (dp[i] != -1) return dp[i];

        // transitions
        int ans = 1;
        for (int j = 0; j < i; j++) {
            if (nums[j] < nums[i]) {
                ans = max(ans, 1 + rec(j));
            }
        }

        // save and return
        return dp[i] = ans;
    }

    int lengthOfLIS(vector<int>& a) {
        nums = a;
        n = nums.size();
        dp.assign(n, -1);

        int ans = 0;
        for (int i = 0; i < n; i++) {
            ans = max(ans, rec(i));
        }
        return ans;
    }
};
```

### Bottom-up C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int lengthOfLIS(vector<int>& nums) {
        int n = nums.size();
        vector<int> dp(n, 1);

        int ans = 1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    dp[i] = max(dp[i], dp[j] + 1);
                }
            }
            ans = max(ans, dp[i]);
        }

        return ans;
    }
};
```

### Optimized O(n log n) C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int lengthOfLIS(vector<int>& nums) {
        vector<int> tail;

        for (int x : nums) {
            auto it = lower_bound(tail.begin(), tail.end(), x);
            if (it == tail.end()) tail.push_back(x);
            else *it = x;
        }

        return tail.size();
    }
};
```

---

## 3.2 Maximum Product Subarray

### Problem

Return maximum product of a contiguous subarray.

### Input

```text
nums = [2,3,-2,4]
```

### Expected Output

```text
6
```

### Framework

```text
Form:
Ending at i, but need two states because negative can become positive.

State:
maxDP(i) = maximum product subarray ending at i
minDP(i) = minimum product subarray ending at i

Transitions:
maxDP(i) = max(nums[i], nums[i]*maxDP(i-1), nums[i]*minDP(i-1))
minDP(i) = min(nums[i], nums[i]*maxDP(i-1), nums[i]*minDP(i-1))

TC:
#S = n * 2
Transitions = O(1)
TC = O(n)
```

### Dry run

```text
nums = [2,3,-2,4]

i=0: max=2, min=2, ans=2
i=1: x=3 → max=max(3,6,6)=6, min=min(3,6,6)=3, ans=6
i=2: x=-2 → max=max(-2,-12,-6)=-2, min=min(-2,-12,-6)=-12, ans=6
i=3: x=4 → max=max(4,-8,-48)=4, min=min(4,-8,-48)=-48, ans=6
```

### Top-down style C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<int> nums;
    vector<pair<int,int>> dp;
    vector<int> seen;

    pair<int,int> rec(int i) {
        // base case
        if (i == 0) return {nums[0], nums[0]};

        // cache check
        if (seen[i]) return dp[i];

        // transition
        auto [prevMax, prevMin] = rec(i - 1);
        int x = nums[i];

        int mx = max({x, x * prevMax, x * prevMin});
        int mn = min({x, x * prevMax, x * prevMin});

        seen[i] = 1;
        return dp[i] = {mx, mn};
    }

    int maxProduct(vector<int>& a) {
        nums = a;
        int n = nums.size();
        dp.assign(n, {0,0});
        seen.assign(n, 0);

        int ans = nums[0];
        for (int i = 0; i < n; i++) {
            ans = max(ans, rec(i).first);
        }
        return ans;
    }
};
```

### Bottom-up C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int maxProduct(vector<int>& nums) {
        int currMax = nums[0];
        int currMin = nums[0];
        int ans = nums[0];

        for (int i = 1; i < nums.size(); i++) {
            int x = nums[i];

            int newMax = max({x, x * currMax, x * currMin});
            int newMin = min({x, x * currMax, x * currMin});

            currMax = newMax;
            currMin = newMin;

            ans = max(ans, currMax);
        }

        return ans;
    }
};
```

---

# 4. Form 3 — Matching / Two Strings

## Recognition

```text
Two strings / two arrays
State usually DP(i, j)
Compare s1[i] and s2[j]
```

---

## 4.1 Longest Common Subsequence

### Problem

Return length of longest common subsequence between `text1` and `text2`.

### Input

```text
text1 = "abcde"
text2 = "ace"
```

### Expected Output

```text
3
```

### Framework

```text
Form:
Matching DP

State:
DP(i, j) = LCS length using text1[i...] and text2[j...]

Transitions:
if text1[i] == text2[j]:
    1 + DP(i+1, j+1)
else:
    max(DP(i+1, j), DP(i, j+1))

TC:
#S = n*m
Transitions = O(1)
TC = O(n*m)
SC = O(n*m)
```

### Dry run

```text
abcde
ace

a matches a → 1 + LCS("bcde", "ce")
b != c → skip b or skip c
c matches c → +1
e matches e → +1

answer = 3
```

### Top-down C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    string a, b;
    vector<vector<int>> dp;
    int n, m;

    int rec(int i, int j) {
        // base case
        if (i == n || j == m) return 0;

        // cache check
        if (dp[i][j] != -1) return dp[i][j];

        // transitions
        int ans = 0;
        if (a[i] == b[j]) {
            ans = 1 + rec(i + 1, j + 1);
        } else {
            ans = max(rec(i + 1, j), rec(i, j + 1));
        }

        // save and return
        return dp[i][j] = ans;
    }

    int longestCommonSubsequence(string text1, string text2) {
        a = text1;
        b = text2;
        n = a.size();
        m = b.size();

        dp.assign(n, vector<int>(m, -1));
        return rec(0, 0);
    }
};
```

### Bottom-up C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int longestCommonSubsequence(string a, string b) {
        int n = a.size(), m = b.size();
        vector<vector<int>> dp(n + 1, vector<int>(m + 1, 0));

        for (int i = n - 1; i >= 0; i--) {
            for (int j = m - 1; j >= 0; j--) {
                if (a[i] == b[j]) {
                    dp[i][j] = 1 + dp[i + 1][j + 1];
                } else {
                    dp[i][j] = max(dp[i + 1][j], dp[i][j + 1]);
                }
            }
        }

        return dp[0][0];
    }
};
```

---

## 4.2 Edit Distance

### Problem

Given two words, return minimum operations to convert word1 to word2.  
Operations: insert, delete, replace.

### Input

```text
word1 = "horse"
word2 = "ros"
```

### Expected Output

```text
3
```

### Framework

```text
Form:
Matching DP with operations

State:
DP(i, j) = min operations to convert word1[i...] to word2[j...]

Transitions:
if same:
    DP(i+1, j+1)
else:
    1 + min(
        insert  = DP(i, j+1),
        delete  = DP(i+1, j),
        replace = DP(i+1, j+1)
    )

Base:
if i == n → need insert remaining m-j
if j == m → need delete remaining n-i

TC:
#S = n*m
Transitions = 3
TC = O(n*m)
```

### Dry run

```text
horse → ros

horse
replace h with r: rorse
delete r: rose
delete e: ros

answer = 3
```

### Top-down C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    string a, b;
    vector<vector<int>> dp;
    int n, m;

    int rec(int i, int j) {
        // base case
        if (i == n) return m - j;
        if (j == m) return n - i;

        // cache check
        if (dp[i][j] != -1) return dp[i][j];

        // transitions
        if (a[i] == b[j]) {
            return dp[i][j] = rec(i + 1, j + 1);
        }

        int insertOp = rec(i, j + 1);
        int deleteOp = rec(i + 1, j);
        int replaceOp = rec(i + 1, j + 1);

        return dp[i][j] = 1 + min({insertOp, deleteOp, replaceOp});
    }

    int minDistance(string word1, string word2) {
        a = word1;
        b = word2;
        n = a.size();
        m = b.size();

        dp.assign(n, vector<int>(m, -1));
        return rec(0, 0);
    }
};
```

### Bottom-up C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int minDistance(string a, string b) {
        int n = a.size(), m = b.size();
        vector<vector<int>> dp(n + 1, vector<int>(m + 1, 0));

        for (int i = 0; i <= n; i++) dp[i][m] = n - i;
        for (int j = 0; j <= m; j++) dp[n][j] = m - j;

        for (int i = n - 1; i >= 0; i--) {
            for (int j = m - 1; j >= 0; j--) {
                if (a[i] == b[j]) {
                    dp[i][j] = dp[i + 1][j + 1];
                } else {
                    dp[i][j] = 1 + min({
                        dp[i][j + 1],
                        dp[i + 1][j],
                        dp[i + 1][j + 1]
                    });
                }
            }
        }

        return dp[0][0];
    }
};
```

---

## 4.3 Distinct Subsequences

### Problem

Given strings `s` and `t`, return number of distinct subsequences of `s` equal to `t`.

### Input

```text
s = "rabbbit"
t = "rabbit"
```

### Expected Output

```text
3
```

### Framework

```text
Form:
Matching DP + take/skip character from s

State:
DP(i, j) = number of ways to form t[j...] from s[i...]

Transitions:
skip s[i] always:
    DP(i+1, j)

if s[i] == t[j], take:
    DP(i+1, j+1)

DP(i,j) = skip + take

Base:
if j == m → formed all t → 1
if i == n → s ended but t not formed → 0

TC:
#S = n*m
Transitions = O(1)
TC = O(n*m)
```

### Top-down C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    string s, t;
    vector<vector<long long>> dp;
    int n, m;

    long long rec(int i, int j) {
        // base case
        if (j == m) return 1;
        if (i == n) return 0;

        // cache check
        if (dp[i][j] != -1) return dp[i][j];

        // transitions
        long long skip = rec(i + 1, j);
        long long take = 0;

        if (s[i] == t[j]) {
            take = rec(i + 1, j + 1);
        }

        return dp[i][j] = skip + take;
    }

    int numDistinct(string S, string T) {
        s = S;
        t = T;
        n = s.size();
        m = t.size();

        dp.assign(n, vector<long long>(m, -1));
        return (int)rec(0, 0);
    }
};
```

### Bottom-up C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int numDistinct(string s, string t) {
        int n = s.size(), m = t.size();
        vector<vector<unsigned long long>> dp(n + 1, vector<unsigned long long>(m + 1, 0));

        for (int i = 0; i <= n; i++) dp[i][m] = 1;

        for (int i = n - 1; i >= 0; i--) {
            for (int j = m - 1; j >= 0; j--) {
                dp[i][j] = dp[i + 1][j];
                if (s[i] == t[j]) {
                    dp[i][j] += dp[i + 1][j + 1];
                }
            }
        }

        return (int)dp[0][0];
    }
};
```

---

# 5. Form 4 — Grid DP

## Recognition

```text
Matrix / grid
Move right/down/up/left
State = cell coordinates
```

---

## 5.1 Unique Paths

### Problem

Robot starts at top-left and moves only right or down. Count paths to bottom-right.

### Input

```text
m = 3, n = 7
```

### Expected Output

```text
28
```

### Framework

```text
Form:
Grid DP

State:
DP(r, c) = number of ways to reach destination from cell (r,c)

DP(r,c) = DP(r+1,c) + DP(r,c+1)

TC:
#S = m*n
Transitions = 2
TC = O(m*n)
```

### Top-down C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int R, C;
    vector<vector<int>> dp;

    int rec(int r, int c) {
        // pruning
        if (r >= R || c >= C) return 0;

        // base case
        if (r == R - 1 && c == C - 1) return 1;

        // cache check
        if (dp[r][c] != -1) return dp[r][c];

        // transitions
        int down = rec(r + 1, c);
        int right = rec(r, c + 1);

        return dp[r][c] = down + right;
    }

    int uniquePaths(int m, int n) {
        R = m;
        C = n;
        dp.assign(R, vector<int>(C, -1));
        return rec(0, 0);
    }
};
```

### Bottom-up C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int uniquePaths(int m, int n) {
        vector<vector<int>> dp(m, vector<int>(n, 1));

        for (int r = m - 2; r >= 0; r--) {
            for (int c = n - 2; c >= 0; c--) {
                dp[r][c] = dp[r + 1][c] + dp[r][c + 1];
            }
        }

        return dp[0][0];
    }
};
```

---

## 5.2 Minimum Path Sum

### Problem

Given grid of non-negative numbers, find minimum sum path from top-left to bottom-right.

### Input

```text
grid = [
  [1,3,1],
  [1,5,1],
  [4,2,1]
]
```

### Expected Output

```text
7
```

### Framework

```text
Form:
Grid DP + minimize

State:
DP(r,c) = min path sum from (r,c) to destination

DP(r,c) = grid[r][c] + min(DP(r+1,c), DP(r,c+1))

TC:
#S = m*n
Transitions = 2
TC = O(m*n)
```

### Dry run

```text
Path:
1 → 3 → 1 → 1 → 1
sum = 7
```

### Top-down C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<vector<int>> grid, dp;
    int R, C;
    const int INF = 1e9;

    int rec(int r, int c) {
        // pruning
        if (r >= R || c >= C) return INF;

        // base case
        if (r == R - 1 && c == C - 1) return grid[r][c];

        // cache check
        if (dp[r][c] != -1) return dp[r][c];

        // transitions
        int down = rec(r + 1, c);
        int right = rec(r, c + 1);

        return dp[r][c] = grid[r][c] + min(down, right);
    }

    int minPathSum(vector<vector<int>>& g) {
        grid = g;
        R = grid.size();
        C = grid[0].size();
        dp.assign(R, vector<int>(C, -1));

        return rec(0, 0);
    }
};
```

### Bottom-up C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int minPathSum(vector<vector<int>>& grid) {
        int R = grid.size(), C = grid[0].size();
        vector<vector<int>> dp(R, vector<int>(C, 0));

        for (int r = R - 1; r >= 0; r--) {
            for (int c = C - 1; c >= 0; c--) {
                if (r == R - 1 && c == C - 1) {
                    dp[r][c] = grid[r][c];
                } else {
                    int down = (r + 1 < R) ? dp[r + 1][c] : 1e9;
                    int right = (c + 1 < C) ? dp[r][c + 1] : 1e9;
                    dp[r][c] = grid[r][c] + min(down, right);
                }
            }
        }

        return dp[0][0];
    }
};
```

---

# 6. Form 5 — Interval / Left-Right DP

## Recognition

```text
Solve for every subarray [l...r]
State usually DP(l, r)
Try removing/splitting/choosing boundary
```

---

## 6.1 Palindromic Substrings

### Problem

Count palindromic substrings in string `s`.

### Input

```text
s = "aaa"
```

### Expected Output

```text
6
```

### Framework

```text
Form:
Interval DP

State:
DP(l,r) = whether s[l...r] is palindrome

Transition:
if s[l] == s[r] and inside is palindrome:
    DP(l,r) = true

DP(l,r) = s[l] == s[r] && (r-l <= 2 || DP(l+1,r-1))

TC:
#S = n*n
Transition = O(1)
TC = O(n^2)
```

### Dry run

```text
s = aaa

length 1:
a, a, a → 3

length 2:
aa, aa → 2

length 3:
aaa → 1

total = 6
```

### Top-down C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    string s;
    vector<vector<int>> dp;

    int isPal(int l, int r) {
        // base case
        if (l >= r) return 1;

        // cache check
        if (dp[l][r] != -1) return dp[l][r];

        // transitions
        if (s[l] != s[r]) return dp[l][r] = 0;

        return dp[l][r] = isPal(l + 1, r - 1);
    }

    int countSubstrings(string str) {
        s = str;
        int n = s.size();
        dp.assign(n, vector<int>(n, -1));

        int ans = 0;
        for (int l = 0; l < n; l++) {
            for (int r = l; r < n; r++) {
                ans += isPal(l, r);
            }
        }
        return ans;
    }
};
```

### Bottom-up C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int countSubstrings(string s) {
        int n = s.size();
        vector<vector<bool>> dp(n, vector<bool>(n, false));

        int ans = 0;

        for (int len = 1; len <= n; len++) {
            for (int l = 0; l + len - 1 < n; l++) {
                int r = l + len - 1;

                if (s[l] == s[r] && (len <= 2 || dp[l + 1][r - 1])) {
                    dp[l][r] = true;
                    ans++;
                }
            }
        }

        return ans;
    }
};
```

---

## 6.2 Burst Balloons

### Problem

Given balloons with values. Bursting balloon `i` gives `left * nums[i] * right`.  
Return maximum coins.

### Input

```text
nums = [3,1,5,8]
```

### Expected Output

```text
167
```

### Framework

```text
Form:
Interval DP

Key idea:
Choose which balloon is burst LAST in interval.

State:
DP(l,r) = max coins from bursting all balloons between l and r inclusive

After padding nums with 1 at both ends:
arr = [1] + nums + [1]

Transition:
DP(l,r) = max over k in [l..r]:
    DP(l,k-1) + arr[l-1]*arr[k]*arr[r+1] + DP(k+1,r)

TC:
#S = n*n
Transitions per state = n
TC = O(n^3)
```

### Dry run idea

```text
nums = [3,1,5,8]
arr = [1,3,1,5,8,1]

For interval [l,r], try every k as last balloon.
Why last?
Because when k is last, its left and right boundaries are fixed:
arr[l-1] and arr[r+1]
```

### Top-down C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<int> a;
    vector<vector<int>> dp;

    int rec(int l, int r) {
        // base case
        if (l > r) return 0;

        // cache check
        if (dp[l][r] != -1) return dp[l][r];

        // transitions
        int ans = 0;
        for (int k = l; k <= r; k++) {
            int left = rec(l, k - 1);
            int gain = a[l - 1] * a[k] * a[r + 1];
            int right = rec(k + 1, r);

            ans = max(ans, left + gain + right);
        }

        return dp[l][r] = ans;
    }

    int maxCoins(vector<int>& nums) {
        a.clear();
        a.push_back(1);
        for (int x : nums) a.push_back(x);
        a.push_back(1);

        int n = nums.size();
        dp.assign(n + 2, vector<int>(n + 2, -1));

        return rec(1, n);
    }
};
```

### Bottom-up C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int maxCoins(vector<int>& nums) {
        vector<int> a;
        a.push_back(1);
        for (int x : nums) a.push_back(x);
        a.push_back(1);

        int n = nums.size();
        vector<vector<int>> dp(n + 2, vector<int>(n + 2, 0));

        for (int len = 1; len <= n; len++) {
            for (int l = 1; l + len - 1 <= n; l++) {
                int r = l + len - 1;

                for (int k = l; k <= r; k++) {
                    int coins = dp[l][k - 1]
                              + a[l - 1] * a[k] * a[r + 1]
                              + dp[k + 1][r];

                    dp[l][r] = max(dp[l][r], coins);
                }
            }
        }

        return dp[1][n];
    }
};
```

---

# 7. Form 6 — Game DP

## Recognition

```text
Two players
Both play optimally
Current player chooses a move
```

---

## 7.1 Predict The Winner

### Problem

Two players pick from either end of array. Return true if player 1 can win or tie.

### Input

```text
nums = [1,5,2]
```

### Expected Output

```text
false
```

### Framework

```text
Form:
Game DP + interval

State:
DP(l,r) = maximum score difference current player can achieve over other player from nums[l...r]

Transition:
take left:
    nums[l] - DP(l+1,r)
take right:
    nums[r] - DP(l,r-1)

DP(l,r) = max(takeLeft, takeRight)

Answer:
DP(0,n-1) >= 0

TC:
#S = n*n
Transitions = 2
TC = O(n^2)
```

### Dry run

```text
nums = [1,5,2]

If P1 takes 1:
remaining [5,2], P2 can get advantage 3
score diff = 1 - 3 = -2

If P1 takes 2:
remaining [1,5], P2 can get advantage 4
score diff = 2 - 4 = -2

dp[0][2] = -2
P1 cannot win.
```

### Top-down C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<int> nums;
    vector<vector<int>> dp;
    vector<vector<int>> seen;

    int rec(int l, int r) {
        // base case
        if (l == r) return nums[l];

        // cache check
        if (seen[l][r]) return dp[l][r];

        // transitions
        int takeLeft = nums[l] - rec(l + 1, r);
        int takeRight = nums[r] - rec(l, r - 1);

        seen[l][r] = 1;
        return dp[l][r] = max(takeLeft, takeRight);
    }

    bool predictTheWinner(vector<int>& a) {
        nums = a;
        int n = nums.size();

        dp.assign(n, vector<int>(n, 0));
        seen.assign(n, vector<int>(n, 0));

        return rec(0, n - 1) >= 0;
    }
};
```

### Bottom-up C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    bool predictTheWinner(vector<int>& nums) {
        int n = nums.size();
        vector<vector<int>> dp(n, vector<int>(n, 0));

        for (int i = 0; i < n; i++) dp[i][i] = nums[i];

        for (int len = 2; len <= n; len++) {
            for (int l = 0; l + len - 1 < n; l++) {
                int r = l + len - 1;

                int takeLeft = nums[l] - dp[l + 1][r];
                int takeRight = nums[r] - dp[l][r - 1];

                dp[l][r] = max(takeLeft, takeRight);
            }
        }

        return dp[0][n - 1] >= 0;
    }
};
```

---

# 8. Form 7 — Partition DP

## Recognition

```text
Break string/array into valid pieces
Try every cut position
```

---

## 8.1 Word Break

### Problem

Given string `s` and dictionary, return true if `s` can be segmented into dictionary words.

### Input

```text
s = "leetcode"
wordDict = ["leet","code"]
```

### Expected Output

```text
true
```

### Framework

```text
Form:
Partition DP

State:
DP(i) = whether suffix s[i...] can be segmented

Transition:
for every end from i to n-1:
    word = s[i...end]
    if word in dict and DP(end+1):
        true

TC:
#S = n
Transitions per state = n
Substring cost can be O(n)
TC = O(n^3) naive
With substring optimized/trie: better
```

### Top-down C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    string s;
    unordered_set<string> dict;
    vector<int> dp;
    int n;

    int rec(int i) {
        // base case
        if (i == n) return 1;

        // cache check
        if (dp[i] != -1) return dp[i];

        // transitions
        string curr = "";
        for (int end = i; end < n; end++) {
            curr.push_back(s[end]);

            if (dict.count(curr) && rec(end + 1)) {
                return dp[i] = 1;
            }
        }

        return dp[i] = 0;
    }

    bool wordBreak(string str, vector<string>& wordDict) {
        s = str;
        n = s.size();
        dict.clear();
        for (auto &w : wordDict) dict.insert(w);

        dp.assign(n, -1);
        return rec(0);
    }
};
```

### Bottom-up C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    bool wordBreak(string s, vector<string>& wordDict) {
        unordered_set<string> dict(wordDict.begin(), wordDict.end());
        int n = s.size();

        vector<bool> dp(n + 1, false);
        dp[n] = true;

        for (int i = n - 1; i >= 0; i--) {
            string curr = "";

            for (int end = i; end < n; end++) {
                curr.push_back(s[end]);

                if (dict.count(curr) && dp[end + 1]) {
                    dp[i] = true;
                    break;
                }
            }
        }

        return dp[0];
    }
};
```

---

## 8.2 Palindrome Partitioning II

### Problem

Given string `s`, return minimum cuts needed so every substring part is palindrome.

### Input

```text
s = "aab"
```

### Expected Output

```text
1
```

### Framework

```text
Form:
Partition DP + palindrome precomputation

State:
DP(i) = minimum cuts needed for suffix s[i...]

Transition:
for every end:
    if s[i...end] is palindrome:
        if end == n-1 → 0 cuts
        else 1 + DP(end+1)

TC:
Palindrome precompute = O(n^2)
DP states = n
Transitions = n
TC = O(n^2)
```

### Dry run

```text
s = aab

partitions:
a | a | b → 2 cuts
aa | b    → 1 cut

answer = 1
```

### Top-down C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    string s;
    int n;
    vector<vector<int>> pal;
    vector<int> dp;

    int rec(int i) {
        // base case
        if (i == n) return -1; 
        // -1 because if previous part reaches end, we don't need extra cut

        // cache check
        if (dp[i] != -1) return dp[i];

        // transitions
        int ans = 1e9;
        for (int end = i; end < n; end++) {
            if (pal[i][end]) {
                ans = min(ans, 1 + rec(end + 1));
            }
        }

        return dp[i] = ans;
    }

    int minCut(string str) {
        s = str;
        n = s.size();

        pal.assign(n, vector<int>(n, 0));

        for (int len = 1; len <= n; len++) {
            for (int l = 0; l + len - 1 < n; l++) {
                int r = l + len - 1;
                if (s[l] == s[r] && (len <= 2 || pal[l + 1][r - 1])) {
                    pal[l][r] = 1;
                }
            }
        }

        dp.assign(n, -1);
        return rec(0);
    }
};
```

### Bottom-up C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int minCut(string s) {
        int n = s.size();
        vector<vector<int>> pal(n, vector<int>(n, 0));

        for (int len = 1; len <= n; len++) {
            for (int l = 0; l + len - 1 < n; l++) {
                int r = l + len - 1;
                if (s[l] == s[r] && (len <= 2 || pal[l + 1][r - 1])) {
                    pal[l][r] = 1;
                }
            }
        }

        vector<int> dp(n + 1, 0);
        dp[n] = -1;

        for (int i = n - 1; i >= 0; i--) {
            dp[i] = 1e9;
            for (int end = i; end < n; end++) {
                if (pal[i][end]) {
                    dp[i] = min(dp[i], 1 + dp[end + 1]);
                }
            }
        }

        return dp[0];
    }
};
```

---

# 9. Form 8 — State Machine DP

## Recognition

```text
At every day/index you are in one of few states:
- holding stock
- not holding
- cooldown
- transaction count
```

---

## 9.1 Best Time to Buy and Sell Stock with Cooldown

### Problem

You can buy/sell stock. After selling, cooldown one day. Return max profit.

### Input

```text
prices = [1,2,3,0,2]
```

### Expected Output

```text
3
```

### Framework

```text
Form:
State Machine DP

State:
DP(i, hold, cooldown) = max profit from day i

Cleaner:
DP(i, state)
state 0 = free to buy
state 1 = holding
state 2 = cooldown

Transitions:
free:
    skip → DP(i+1, free)
    buy → -price[i] + DP(i+1, holding)

holding:
    hold → DP(i+1, holding)
    sell → price[i] + DP(i+1, cooldown)

cooldown:
    move to free → DP(i+1, free)

TC:
#S = n*3
Transitions = O(1)
TC = O(n)
```

### Top-down C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<int> prices;
    vector<vector<int>> dp;
    int n;

    int rec(int i, int state) {
        // base case
        if (i == n) return 0;

        // cache check
        if (dp[i][state] != -1) return dp[i][state];

        int ans = 0;

        // state 0: free
        if (state == 0) {
            int skip = rec(i + 1, 0);
            int buy = -prices[i] + rec(i + 1, 1);
            ans = max(skip, buy);
        }

        // state 1: holding
        else if (state == 1) {
            int hold = rec(i + 1, 1);
            int sell = prices[i] + rec(i + 1, 2);
            ans = max(hold, sell);
        }

        // state 2: cooldown
        else {
            ans = rec(i + 1, 0);
        }

        return dp[i][state] = ans;
    }

    int maxProfit(vector<int>& p) {
        prices = p;
        n = prices.size();
        dp.assign(n, vector<int>(3, -1));
        return rec(0, 0);
    }
};
```

### Bottom-up C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int maxProfit(vector<int>& prices) {
        int n = prices.size();
        vector<vector<int>> dp(n + 1, vector<int>(3, 0));

        for (int i = n - 1; i >= 0; i--) {
            dp[i][0] = max(dp[i + 1][0], -prices[i] + dp[i + 1][1]);
            dp[i][1] = max(dp[i + 1][1], prices[i] + dp[i + 1][2]);
            dp[i][2] = dp[i + 1][0];
        }

        return dp[0][0];
    }
};
```

---

# 10. Form 9 — Bitmask DP

## Recognition

```text
n <= 20
Need remember chosen/visited set
State includes mask
```

---

## 10.1 Matchsticks to Square

### Problem

Given matchsticks, return true if they can form a square.

### Input

```text
matchsticks = [1,1,2,2,2]
```

### Expected Output

```text
true
```

### Framework

```text
Form:
Bitmask / subset DP

Observation:
Need 4 sides each sum = total/4

State:
DP(mask, currSideSum, sidesDone)

Simpler memo:
rec(mask, currSum, sidesDone)

Transitions:
choose an unused stick if currSum + stick <= side

TC:
#mask = 2^n
Transitions per state = n
TC = O(n * 2^n) approximately
```

### Top-down C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<int> a;
    unordered_map<int, int> memo;
    int n, side;

    bool rec(int mask, int currSum) {
        // base case
        if (mask == (1 << n) - 1) return currSum == 0;

        // cache check
        int key = mask * 1000 + currSum;
        if (memo.count(key)) return memo[key];

        // transitions
        for (int i = 0; i < n; i++) {
            if (!(mask & (1 << i))) {
                if (currSum + a[i] <= side) {
                    int nextSum = (currSum + a[i]) % side;
                    if (rec(mask | (1 << i), nextSum)) {
                        return memo[key] = true;
                    }
                }
            }
        }

        return memo[key] = false;
    }

    bool makesquare(vector<int>& matchsticks) {
        a = matchsticks;
        n = a.size();

        int sum = accumulate(a.begin(), a.end(), 0);
        if (sum % 4 != 0) return false;

        side = sum / 4;

        sort(a.rbegin(), a.rend());
        if (a[0] > side) return false;

        memo.clear();
        return rec(0, 0);
    }
};
```

### Bottom-up C++

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    bool makesquare(vector<int>& matchsticks) {
        int n = matchsticks.size();
        int sum = accumulate(matchsticks.begin(), matchsticks.end(), 0);

        if (sum % 4 != 0) return false;

        int side = sum / 4;
        int full = 1 << n;

        vector<int> dp(full, -1);
        dp[0] = 0;

        for (int mask = 0; mask < full; mask++) {
            if (dp[mask] == -1) continue;

            for (int i = 0; i < n; i++) {
                if (!(mask & (1 << i))) {
                    int next = dp[mask] + matchsticks[i];

                    if (next <= side) {
                        dp[mask | (1 << i)] = next % side;
                    }
                }
            }
        }

        return dp[full - 1] == 0;
    }
};
```

---

# 11. Form 10 — Tree DP

## Recognition

```text
Tree
Answer at node depends on children
Usually:
DP(node, state)
or return pair values from subtree
```

---

## 11.1 House Robber III

### Problem

Binary tree houses. If rob a node, cannot rob its children. Return max money.

### Input

```text
root = [3,2,3,null,3,null,1]
```

### Expected Output

```text
7
```

### Framework

```text
Form:
Tree DP + take/not take

State:
For every node return:
take = max money if we rob this node
skip = max money if we don't rob this node

Transitions:
take = node->val + left.skip + right.skip
skip = max(left.take,left.skip) + max(right.take,right.skip)

TC:
#S = n nodes
Transitions = O(1)
TC = O(n)
```

### Dry run

```text
        3
       / \
      2   3
       \   \
        3   1

leaf 3 → take=3, skip=0
node 2 → take=2, skip=3
leaf 1 → take=1, skip=0
node 3(right) → take=3, skip=1
root 3:
take = 3 + left.skip(3) + right.skip(1) = 7
skip = max(2,3) + max(3,1) = 6

answer = 7
```

### Top-down / DFS C++

```cpp
#include <bits/stdc++.h>
using namespace std;

struct TreeNode {
    int val;
    TreeNode *left;
    TreeNode *right;
    TreeNode() : val(0), left(nullptr), right(nullptr) {}
    TreeNode(int x) : val(x), left(nullptr), right(nullptr) {}
};

class Solution {
public:
    pair<int,int> dfs(TreeNode* node) {
        // base case
        if (!node) return {0, 0};

        // transitions
        auto L = dfs(node->left);
        auto R = dfs(node->right);

        int take = node->val + L.second + R.second;
        int skip = max(L.first, L.second) + max(R.first, R.second);

        return {take, skip};
    }

    int rob(TreeNode* root) {
        auto ans = dfs(root);
        return max(ans.first, ans.second);
    }
};
```

### Bottom-up Note

For tree DP, normal bottom-up means postorder traversal.  
DFS already computes children before parent, so it is bottom-up naturally.

---

# 12. FAANG Hard DP Master Checklist

## 12.1 The 10-second form detection checklist

```text
1. Is it choose/skip?
   → Take / Not Take

2. Is it best ending at i?
   → Ending At Index

3. Two strings?
   → Matching DP

4. Grid movement?
   → Grid DP

5. Subarray [l...r]?
   → Interval DP

6. Two players?
   → Game DP

7. Split into valid chunks?
   → Partition DP

8. Stock/holding/cooldown?
   → State Machine DP

9. n <= 20 and chosen set?
   → Bitmask DP

10. Tree?
   → Tree DP
```

## 12.2 State design checklist

Before coding, write:

```text
DP(______) = __________________________________
```

Bad state:

```text
dp[i] means answer
```

Good state:

```text
dp[i] = maximum money we can rob from houses i...n-1
```

## 12.3 Transition checklist

Ask:

```text
At this state, what are my choices?
After choice, where do I move?
What value do I add?
Am I minimizing, maximizing, counting, or checking possible?
```

## 12.4 Base case checklist

```text
Counting:
finished valid → 1
invalid → 0

Minimizing:
finished valid → 0
invalid → INF

Maximizing:
finished valid → 0
invalid → -INF

Boolean:
finished valid → true
invalid → false
```

## 12.5 Complexity checklist

Always write:

```text
#States = ?
Avg transitions per state = ?
Cost of each transition = ?

TC = #S * (1 + Avg transition cost)
```

Examples:

```text
LIS:
#S = n
Transitions = n
TC = O(n^2)

LCS:
#S = n*m
Transitions = O(1)
TC = O(n*m)

Burst Balloons:
#S = n*n
Transitions = n
TC = O(n^3)

Bitmask:
#S = 2^n
Transitions = n
TC = O(n*2^n)
```

## 12.6 Top-down to bottom-up conversion

```text
Top-down:
rec(i) depends on rec(i+1)
Bottom-up:
iterate i from n-1 down to 0

Top-down:
rec(i,j) depends on rec(i+1,j), rec(i,j+1)
Bottom-up:
iterate i from n-1 down
iterate j from m-1 down

Top-down:
rec(l,r) depends on smaller intervals
Bottom-up:
iterate by length from 1 to n
```

## 12.7 Final FAANG DP Problem Ladder

### Phase 1 — Foundation

```text
Climbing Stairs
House Robber
Min Cost Climbing Stairs
Coin Change
Partition Equal Subset Sum
```

### Phase 2 — Subsequence / Matching

```text
Longest Increasing Subsequence
Longest Common Subsequence
Edit Distance
Distinct Subsequences
Interleaving String
```

### Phase 3 — Grid / Matrix

```text
Unique Paths
Unique Paths II
Minimum Path Sum
Dungeon Game
Cherry Pickup
```

### Phase 4 — Partition / Interval

```text
Word Break
Palindrome Partitioning II
Burst Balloons
Matrix Chain Multiplication
Minimum Cost to Cut a Stick
```

### Phase 5 — State Machine

```text
Best Time to Buy and Sell Stock I
Best Time to Buy and Sell Stock II
Best Time to Buy and Sell Stock with Cooldown
Best Time to Buy and Sell Stock III
Best Time to Buy and Sell Stock IV
```

### Phase 6 — Advanced

```text
Regular Expression Matching
Wildcard Matching
Scramble String
Longest Valid Parentheses
Maximum Profit in Job Scheduling
```

### Phase 7 — Hard / Specialist

```text
Bitmask DP assignment problems
TSP style DP
Digit DP counting numbers
Tree DP rerooting
DP with monotonic queue
DP with binary search optimization
```

---

# Final Mental Model

```text
DP mastery = form recognition + state meaning + transition clarity.

If you can say:

"This is Form X.
DP(...) means Y.
Transition is Z.
#States = A.
Transitions per state = B.
TC = A*B."

Then you can solve most FAANG DP problems.
```
