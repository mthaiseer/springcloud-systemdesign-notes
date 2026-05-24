# DP_PROBLEMS_FORMWISE.md

# DP Problems Form-Wise Master Guide for FAANG

> Goal: master DP by **forms**, not by memorizing problems.

This guide uses the same framework for every problem:

```text
1. Look for the form of the problem
2. Decide the state & meaning
3. DP(...) = _______
4. Decide the transitions
5. Check time complexity

TC → #States * (1 + Avg cost of transition)

or

TC → #States * (1 + Avg no. of transitions per state)

6. Code order:
   - pruning
   - base case
   - cache check
   - transitions
   - save and return
```

---

# Clickable Index

## 0. DP Interview Form Map
- [0.1 How to recognize DP form fast](#01-how-to-recognize-dp-form-fast)
- [0.2 Universal memoization template](#02-universal-memoization-template)
- [0.3 Universal tabulation conversion](#03-universal-tabulation-conversion)

## Form 1: Take / Not Take DP
- [1.1 House Robber](#11-house-robber)
- [1.2 Partition Equal Subset Sum](#12-partition-equal-subset-sum)
- [1.3 Target Sum](#13-target-sum)
- [1.4 0/1 Knapsack](#14-01-knapsack)

## Form 2: Ending At Index DP
- [2.1 Longest Increasing Subsequence](#21-longest-increasing-subsequence)
- [2.2 Maximum Product Subarray](#22-maximum-product-subarray)
- [2.3 Word Break](#23-word-break)
- [2.4 Palindromic Substrings](#24-palindromic-substrings)

## Form 3: Matching / Two String DP
- [3.1 Longest Common Subsequence](#31-longest-common-subsequence)
- [3.2 Edit Distance](#32-edit-distance)
- [3.3 Distinct Subsequences](#33-distinct-subsequences)
- [3.4 Regular Expression Matching](#34-regular-expression-matching)

## Form 4: Grid DP
- [4.1 Unique Paths II](#41-unique-paths-ii)
- [4.2 Minimum Path Sum](#42-minimum-path-sum)
- [4.3 Dungeon Game](#43-dungeon-game)

## Form 5: Interval / Left-Right DP
- [5.1 Palindrome Partitioning II](#51-palindrome-partitioning-ii)
- [5.2 Burst Balloons](#52-burst-balloons)
- [5.3 Matrix Chain Multiplication](#53-matrix-chain-multiplication)
- [5.4 Minimum Cost to Cut a Stick](#54-minimum-cost-to-cut-a-stick)

## Form 6: Game DP
- [6.1 Predict the Winner](#61-predict-the-winner)
- [6.2 Stone Game II](#62-stone-game-ii)

## Form 7: State Machine DP
- [7.1 Best Time to Buy and Sell Stock with Cooldown](#71-best-time-to-buy-and-sell-stock-with-cooldown)
- [7.2 Best Time to Buy and Sell Stock IV](#72-best-time-to-buy-and-sell-stock-iv)

## Form 8: Bitmask DP
- [8.1 Partition to K Equal Sum Subsets](#81-partition-to-k-equal-sum-subsets)
- [8.2 Minimum Cost Assignment](#82-minimum-cost-assignment)

## Form 9: Tree DP
- [9.1 House Robber III](#91-house-robber-iii)
- [9.2 Binary Tree Maximum Path Sum](#92-binary-tree-maximum-path-sum)

## Form 10: Digit DP
- [10.1 Count Numbers With Unique Digits Pattern](#101-count-numbers-with-unique-digits-pattern)
- [10.2 Count Digit One](#102-count-digit-one)

## Form 11: Advanced Optimization DP
- [11.1 Coin Change II](#111-coin-change-ii)
- [11.2 Longest Palindromic Subsequence](#112-longest-palindromic-subsequence)
- [11.3 Scramble String](#113-scramble-string)

## Final Interview Checklist
- [DP recognition checklist](#dp-recognition-checklist)
- [FAANG DP hard problem attack plan](#faang-dp-hard-problem-attack-plan)

---

# 0. DP Interview Form Map

## 0.1 How to recognize DP form fast

| Signal in problem | Likely DP Form |
|---|---|
| choose / skip / subset / capacity | Take / Not Take |
| best ending at `i` | Ending At Index |
| two strings / convert / match | Matching DP |
| matrix movement | Grid DP |
| choose left or right / split interval | Interval DP |
| players alternate turns | Game DP |
| buy/sell/hold/cooldown | State Machine DP |
| subset of items already used | Bitmask DP |
| binary tree subtree answer | Tree DP |
| count numbers under bound | Digit DP |

---

## 0.2 Universal memoization template

```cpp
int rec(state) {
    // 1. pruning
    if (invalid) return BAD_VALUE;

    // 2. base case
    if (finished) return BASE_VALUE;

    // 3. cache check
    if (seen[state]) return dp[state];

    // 4. transitions
    int ans = INITIAL_VALUE;

    for (choice in choices) {
        if (valid(choice)) {
            ans = combine(ans, rec(next_state));
        }
    }

    // 5. save and return
    seen[state] = true;
    return dp[state] = ans;
}
```

---

## 0.3 Universal tabulation conversion

```text
Memo:
current state depends on smaller/future states.

Tabulation:
fill states in the order dependencies are already solved.
```

| Memo direction | Tabulation order |
|---|---|
| `rec(i)` depends on `rec(i+1)` | fill `i` from `n-1` down to `0` |
| `rec(i)` depends on `rec(i-1)` | fill `i` from `0` to `n-1` |
| `rec(l,r)` depends on smaller intervals | fill by interval length |
| `rec(mask)` depends on bigger mask | fill mask descending or memo |

---

# Form 1: Take / Not Take DP

## Pattern

Use when each item has two choices:

```text
take current item
or
skip current item
```

Common problems:

```text
Knapsack
Subset Sum
Partition Equal Subset Sum
Target Sum
House Robber
Choose K items
```

---

# 1.1 House Robber

## Problem

Given money in houses, cannot rob adjacent houses. Return maximum money.

## Input

```text
nums = [2,7,9,3,1]
```

## Expected Output

```text
12
```

## Framework

### Look for the form

At every house:

```text
rob this house
or
skip this house
```

So this is **Form 1: Take / Not Take DP**.

### State & meaning

```text
DP(i) = maximum money we can rob from houses i...n-1
```

### Transition

```text
skip = DP(i+1)
take = nums[i] + DP(i+2)

DP(i) = max(skip, take)
```

### Time complexity

```text
#States = n
Avg transitions/state = 2

TC = n * (1 + 2) = O(n)
SC = O(n)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<int> dp;

    int rec(int i, vector<int>& nums) {
        // pruning
        if (i > (int)nums.size()) return 0;

        // base case
        if (i >= (int)nums.size()) return 0;

        // cache check
        if (dp[i] != -1) return dp[i];

        // transitions
        int skip = rec(i + 1, nums);
        int take = nums[i] + rec(i + 2, nums);

        // save and return
        return dp[i] = max(skip, take);
    }

    int rob(vector<int>& nums) {
        dp.assign(nums.size(), -1);
        return rec(0, nums);
    }
};
```

## Index-by-index dry run

```text
nums = [2,7,9,3,1]

DP(4) = max(DP(5), nums[4] + DP(6))
      = max(0, 1 + 0) = 1

DP(3) = max(DP(4), nums[3] + DP(5))
      = max(1, 3 + 0) = 3

DP(2) = max(DP(3), nums[2] + DP(4))
      = max(3, 9 + 1) = 10

DP(1) = max(DP(2), nums[1] + DP(3))
      = max(10, 7 + 3) = 10

DP(0) = max(DP(1), nums[0] + DP(2))
      = max(10, 2 + 10) = 12
```

---

# 1.2 Partition Equal Subset Sum

## Problem

Given array, check whether it can be partitioned into two subsets with equal sum.

## Input

```text
nums = [1,5,11,5]
```

## Expected Output

```text
true
```

## Framework

### Look for the form

We need choose some numbers whose sum is `total / 2`.

At every index:

```text
take nums[i]
or
skip nums[i]
```

So this is **Form 1: Take / Not Take DP**.

### State & meaning

```text
DP(i, sum) = whether we can make target sum using nums[i...n-1]
```

### Transition

```text
skip = DP(i+1, sum)
take = DP(i+1, sum + nums[i])

DP(i, sum) = skip OR take
```

Alternative remaining-sum version:

```text
DP(i, rem) = can we make rem using i...n-1
```

### Time complexity

```text
#States = n * target
Transitions = 2

TC = O(n * target)
SC = O(n * target)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<vector<int>> dp;

    bool rec(int i, int rem, vector<int>& nums) {
        // pruning
        if (rem < 0) return false;

        // base case
        if (rem == 0) return true;
        if (i == (int)nums.size()) return false;

        // cache check
        if (dp[i][rem] != -1) return dp[i][rem];

        // transitions
        bool skip = rec(i + 1, rem, nums);
        bool take = rec(i + 1, rem - nums[i], nums);

        // save and return
        return dp[i][rem] = (skip || take);
    }

    bool canPartition(vector<int>& nums) {
        int total = accumulate(nums.begin(), nums.end(), 0);
        if (total % 2) return false;

        int target = total / 2;
        dp.assign(nums.size(), vector<int>(target + 1, -1));

        return rec(0, target, nums);
    }
};
```

## Index-by-index dry run

```text
nums = [1,5,11,5]
total = 22
target = 11

DP(0,11)
  skip 1 -> DP(1,11)
  take 1 -> DP(1,10)

DP(1,11)
  take 5 -> DP(2,6)
  skip 5 -> DP(2,11)

DP(2,11)
  take 11 -> DP(3,0) = true

Answer = true
```

---

# 1.3 Target Sum

## Problem

Assign `+` or `-` before each number so expression equals target.

## Input

```text
nums = [1,1,1,1,1], target = 3
```

## Expected Output

```text
5
```

## Framework

### Look for the form

For each number:

```text
put +
or
put -
```

So this is **Form 1: Choice DP**.

### State & meaning

```text
DP(i, sum) = number of ways to reach target from index i with current sum
```

### Transition

```text
DP(i, sum) = DP(i+1, sum + nums[i]) + DP(i+1, sum - nums[i])
```

### Time complexity

Let `S = sum(nums)`.

```text
sum range = -S...S
#States = n * 2S
Transitions = 2

TC = O(n*S)
SC = O(n*S)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    map<pair<int,int>, int> dp;

    int rec(int i, int sum, vector<int>& nums, int target) {
        // pruning
        // no pruning needed here

        // base case
        if (i == (int)nums.size()) {
            return sum == target;
        }

        // cache check
        pair<int,int> key = {i, sum};
        if (dp.count(key)) return dp[key];

        // transitions
        int plus = rec(i + 1, sum + nums[i], nums, target);
        int minus = rec(i + 1, sum - nums[i], nums, target);

        // save and return
        return dp[key] = plus + minus;
    }

    int findTargetSumWays(vector<int>& nums, int target) {
        dp.clear();
        return rec(0, 0, nums, target);
    }
};
```

## Dry run

```text
nums = [1,1,1,1,1], target = 3

Need final sum 3.

One valid path:
+1 +1 +1 +1 -1 = 3

There are 5 ways because exactly one of the five positions gets -1.
```

---

# 1.4 0/1 Knapsack

## Problem

Given weights and values, maximize value with capacity `W`.

## Input

```text
wt = [1,3,4,5]
val = [1,4,5,7]
W = 7
```

## Expected Output

```text
9
```

## Framework

### Look for the form

For each item:

```text
take item
or
skip item
```

So this is **Form 1: Take / Not Take DP**.

### State & meaning

```text
DP(i, cap) = maximum value from items i...n-1 with remaining capacity cap
```

### Transition

```text
skip = DP(i+1, cap)
take = val[i] + DP(i+1, cap - wt[i]) if wt[i] <= cap

DP(i, cap) = max(skip, take)
```

### Time complexity

```text
#States = n * W
Transitions = 2

TC = O(nW)
SC = O(nW)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int knapsack(vector<int>& wt, vector<int>& val, int W) {
    int n = wt.size();
    vector<vector<int>> dp(n, vector<int>(W + 1, -1));

    function<int(int,int)> rec = [&](int i, int cap) {
        // pruning
        if (cap < 0) return INT_MIN / 2;

        // base case
        if (i == n) return 0;

        // cache check
        if (dp[i][cap] != -1) return dp[i][cap];

        // transitions
        int skip = rec(i + 1, cap);
        int take = INT_MIN / 2;
        if (wt[i] <= cap) {
            take = val[i] + rec(i + 1, cap - wt[i]);
        }

        // save and return
        return dp[i][cap] = max(skip, take);
    };

    return rec(0, W);
}
```

## Dry run

```text
wt  = [1,3,4,5]
val = [1,4,5,7]
W = 7

Best choice:
take item weight 3 value 4
take item weight 4 value 5

total weight = 7
total value = 9
```

---

# Form 2: Ending At Index DP

## Pattern

Use when answer is naturally:

```text
best answer ending at index i
```

Common problems:

```text
LIS
Maximum subarray
Maximum product subarray
Word break
Partition DP
Palindrome ending at i
```

---

# 2.1 Longest Increasing Subsequence

## Problem

Return length of longest strictly increasing subsequence.

## Input

```text
nums = [10,9,2,5,3,7,101,18]
```

## Expected Output

```text
4
```

## Framework

### Look for the form

We ask:

```text
What is the best increasing subsequence ending at i?
```

So this is **Form 2: Ending At Index DP**.

### State & meaning

```text
DP(i) = length of LIS ending exactly at index i
```

### Transition

```text
DP(i) = 1 + max(DP(j)) for all j < i and nums[j] < nums[i]
```

If no valid j:

```text
DP(i) = 1
```

### Time complexity

```text
#States = n
Avg transition scan = n

TC = O(n^2)
SC = O(n)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int lengthOfLIS(vector<int>& nums) {
        int n = nums.size();
        vector<int> dp(n, 1);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    dp[i] = max(dp[i], 1 + dp[j]);
                }
            }
        }

        return *max_element(dp.begin(), dp.end());
    }
};
```

## Index-by-index dry run

```text
nums = [10,9,2,5,3,7,101,18]

i=0, 10: dp[0]=1
i=1, 9 : no previous smaller -> dp[1]=1
i=2, 2 : no previous smaller -> dp[2]=1
i=3, 5 : previous smaller 2 -> dp[3]=2
i=4, 3 : previous smaller 2 -> dp[4]=2
i=5, 7 : previous smaller 2,5,3 -> dp[5]=3
i=6,101: can extend 7 -> dp[6]=4
i=7,18 : can extend 7 -> dp[7]=4

answer = 4
```

---

# 2.2 Maximum Product Subarray

## Problem

Find maximum product of a contiguous subarray.

## Input

```text
nums = [2,3,-2,4]
```

## Expected Output

```text
6
```

## Framework

### Look for the form

For each index:

```text
best product ending here
```

But negative numbers can flip min to max.

So this is **Form 2: Ending At Index DP with two states**.

### State & meaning

```text
maxDP(i) = maximum product subarray ending at i
minDP(i) = minimum product subarray ending at i
```

### Transition

```text
maxDP(i) = max(nums[i], nums[i]*maxDP(i-1), nums[i]*minDP(i-1))
minDP(i) = min(nums[i], nums[i]*maxDP(i-1), nums[i]*minDP(i-1))
```

### Time complexity

```text
#States = n * 2
Transitions = O(1)

TC = O(n)
SC = O(1)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int maxProduct(vector<int>& nums) {
        int mx = nums[0];
        int mn = nums[0];
        int ans = nums[0];

        for (int i = 1; i < nums.size(); i++) {
            int x = nums[i];

            int newMx = max({x, x * mx, x * mn});
            int newMn = min({x, x * mx, x * mn});

            mx = newMx;
            mn = newMn;

            ans = max(ans, mx);
        }

        return ans;
    }
};
```

## Dry run

```text
nums = [2,3,-2,4]

i=0:
mx=2, mn=2, ans=2

i=1, x=3:
mx=max(3,6,6)=6
mn=min(3,6,6)=3
ans=6

i=2, x=-2:
mx=max(-2,-12,-6)=-2
mn=min(-2,-12,-6)=-12
ans=6

i=3, x=4:
mx=max(4,-8,-48)=4
mn=min(4,-8,-48)=-48
ans=6
```

---

# 2.3 Word Break

## Problem

Given string `s` and dictionary, return if `s` can be segmented into dictionary words.

## Input

```text
s = "leetcode"
wordDict = ["leet","code"]
```

## Expected Output

```text
true
```

## Framework

### Look for the form

We need decide:

```text
Can prefix ending at i be broken?
```

So this is **Form 2: Ending At Index / Partition DP**.

### State & meaning

```text
DP(i) = whether s[0...i-1] can be segmented
```

### Transition

```text
DP(i) = true if there exists j < i:
         DP(j) == true
         and s[j...i-1] is in dictionary
```

### Time complexity

```text
#States = n
Transitions per state = n splits
Substring cost = O(n) in C++ if copied

TC = O(n^3) naive
With string_view/trie/hash optimization closer to O(n^2 * L)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    bool wordBreak(string s, vector<string>& wordDict) {
        unordered_set<string> dict(wordDict.begin(), wordDict.end());
        int n = s.size();

        vector<int> dp(n + 1, 0);
        dp[0] = 1;

        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) {
                string part = s.substr(j, i - j);
                if (dp[j] && dict.count(part)) {
                    dp[i] = 1;
                    break;
                }
            }
        }

        return dp[n];
    }
};
```

## Dry run

```text
s = "leetcode"

dp[0] = true

i=4:
j=0, s[0..3] = "leet"
dp[0] true and "leet" in dict
dp[4] = true

i=8:
j=4, s[4..7] = "code"
dp[4] true and "code" in dict
dp[8] = true
```

---

# 2.4 Palindromic Substrings

## Problem

Count palindromic substrings.

## Input

```text
s = "aaa"
```

## Expected Output

```text
6
```

## Framework

### Look for the form

For substring `l...r`:

```text
is this substring palindrome?
```

This is **2D substring DP / Interval boolean DP**.

### State & meaning

```text
DP(l,r) = whether s[l...r] is palindrome
```

### Transition

```text
DP(l,r) = s[l] == s[r] and (r-l <= 2 or DP(l+1,r-1))
```

### Time complexity

```text
#States = n^2
Transitions = O(1)

TC = O(n^2)
SC = O(n^2)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int countSubstrings(string s) {
        int n = s.size();
        vector<vector<int>> dp(n, vector<int>(n, 0));
        int ans = 0;

        for (int len = 1; len <= n; len++) {
            for (int l = 0; l + len - 1 < n; l++) {
                int r = l + len - 1;

                if (s[l] == s[r] && (len <= 2 || dp[l + 1][r - 1])) {
                    dp[l][r] = 1;
                    ans++;
                }
            }
        }

        return ans;
    }
};
```

## Dry run

```text
s = "aaa"

len=1:
"a", "a", "a" => 3

len=2:
"aa", "aa" => 2

len=3:
"aaa" => true because outer chars same and inner "a" true

total = 6
```

---

# Form 3: Matching / Two String DP

## Pattern

Use when comparing two strings/sequences.

Common states:

```text
DP(i,j) = answer using suffix s1[i...] and s2[j...]
```

or

```text
DP(i,j) = answer using prefix s1[0...i-1] and s2[0...j-1]
```

---

# 3.1 Longest Common Subsequence

## Problem

Find LCS length of two strings.

## Input

```text
text1 = "abcde"
text2 = "ace"
```

## Expected Output

```text
3
```

## Framework

### Look for the form

Two strings, choose matching characters.

So this is **Form 3: Matching DP**.

### State & meaning

```text
DP(i,j) = LCS length of text1[i...] and text2[j...]
```

### Transition

```text
if text1[i] == text2[j]:
    DP(i,j) = 1 + DP(i+1,j+1)
else:
    DP(i,j) = max(DP(i+1,j), DP(i,j+1))
```

### Time complexity

```text
#States = n*m
Transitions = O(1)

TC = O(nm)
SC = O(nm)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<vector<int>> dp;

    int rec(int i, int j, string& a, string& b) {
        // pruning
        // none

        // base case
        if (i == a.size() || j == b.size()) return 0;

        // cache check
        if (dp[i][j] != -1) return dp[i][j];

        // transitions
        int ans;
        if (a[i] == b[j]) {
            ans = 1 + rec(i + 1, j + 1, a, b);
        } else {
            ans = max(rec(i + 1, j, a, b), rec(i, j + 1, a, b));
        }

        // save and return
        return dp[i][j] = ans;
    }

    int longestCommonSubsequence(string text1, string text2) {
        dp.assign(text1.size(), vector<int>(text2.size(), -1));
        return rec(0, 0, text1, text2);
    }
};
```

## Dry run

```text
abcde
ace

match a -> +1
then compare bcde with ce

skip b
match c -> +1
then compare de with e

skip d
match e -> +1

answer = 3
```

---

# 3.2 Edit Distance

## Problem

Minimum operations to convert word1 to word2.

Operations:

```text
insert
delete
replace
```

## Input

```text
word1 = "horse"
word2 = "ros"
```

## Expected Output

```text
3
```

## Framework

### Look for the form

Two strings, convert one into another.

So this is **Form 3: Matching DP**.

### State & meaning

```text
DP(i,j) = minimum operations to convert word1[i...] to word2[j...]
```

### Transition

If same:

```text
DP(i,j) = DP(i+1,j+1)
```

Else:

```text
insert  = 1 + DP(i, j+1)
delete  = 1 + DP(i+1, j)
replace = 1 + DP(i+1, j+1)

DP(i,j) = min(insert, delete, replace)
```

### Time complexity

```text
#States = n*m
Transitions = 3

TC = O(nm)
SC = O(nm)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<vector<int>> dp;

    int rec(int i, int j, string& a, string& b) {
        // pruning
        // none

        // base case
        if (i == a.size()) return b.size() - j;
        if (j == b.size()) return a.size() - i;

        // cache check
        if (dp[i][j] != -1) return dp[i][j];

        // transitions
        if (a[i] == b[j]) {
            return dp[i][j] = rec(i + 1, j + 1, a, b);
        }

        int insertOp = 1 + rec(i, j + 1, a, b);
        int deleteOp = 1 + rec(i + 1, j, a, b);
        int replaceOp = 1 + rec(i + 1, j + 1, a, b);

        // save and return
        return dp[i][j] = min({insertOp, deleteOp, replaceOp});
    }

    int minDistance(string word1, string word2) {
        dp.assign(word1.size(), vector<int>(word2.size(), -1));
        return rec(0, 0, word1, word2);
    }
};
```

## Dry run

```text
horse -> ros

replace h with r:
rorse -> ros

delete r:
rose -> ros

delete e:
ros -> ros

answer = 3
```

---

# 3.3 Distinct Subsequences

## Problem

Count number of distinct subsequences of `s` equal to `t`.

## Input

```text
s = "rabbbit"
t = "rabbit"
```

## Expected Output

```text
3
```

## Framework

### Look for the form

Two strings, matching subsequence.

So this is **Form 3: Matching DP with counting**.

### State & meaning

```text
DP(i,j) = number of ways to form t[j...] from s[i...]
```

### Transition

If `s[i] == t[j]`:

```text
take s[i] as match: DP(i+1,j+1)
skip s[i]:          DP(i+1,j)

DP(i,j) = take + skip
```

Else:

```text
DP(i,j) = DP(i+1,j)
```

### Time complexity

```text
#States = n*m
Transitions = O(1)

TC = O(nm)
SC = O(nm)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<vector<long long>> dp;

    long long rec(int i, int j, string& s, string& t) {
        // pruning
        if (s.size() - i < t.size() - j) return 0;

        // base case
        if (j == t.size()) return 1;
        if (i == s.size()) return 0;

        // cache check
        if (dp[i][j] != -1) return dp[i][j];

        // transitions
        long long ans = rec(i + 1, j, s, t); // skip

        if (s[i] == t[j]) {
            ans += rec(i + 1, j + 1, s, t); // take
        }

        // save and return
        return dp[i][j] = ans;
    }

    int numDistinct(string s, string t) {
        dp.assign(s.size(), vector<long long>(t.size(), -1));
        return (int)rec(0, 0, s, t);
    }
};
```

## Dry run

```text
s = rabbbit
t = rabbit

The extra b gives 3 ways to choose which two b's from three b's:

ra(bb)it
ra(b b third skipped)it
ra(first skipped bb)it

answer = 3
```

---

# 3.4 Regular Expression Matching

## Problem

Implement regex matching with:

```text
.  matches any single character
*  matches zero or more of previous element
```

## Input

```text
s = "aab"
p = "c*a*b"
```

## Expected Output

```text
true
```

## Framework

### Look for the form

String vs pattern matching.

So this is **Form 3: Matching DP with automata-like transitions**.

### State & meaning

```text
DP(i,j) = whether s[i...] matches p[j...]
```

### Transition

Let:

```text
firstMatch = i < n and (s[i] == p[j] or p[j] == '.')
```

If next pattern char is `*`:

```text
zero use: DP(i, j+2)
one/more use: firstMatch && DP(i+1, j)
```

Else:

```text
firstMatch && DP(i+1, j+1)
```

### Time complexity

```text
#States = n*m
Transitions = O(1)

TC = O(nm)
SC = O(nm)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<vector<int>> dp;

    bool rec(int i, int j, string& s, string& p) {
        // pruning
        // none

        // base case
        if (j == p.size()) return i == s.size();

        // cache check
        if (dp[i][j] != -1) return dp[i][j];

        // transitions
        bool firstMatch = (i < s.size() && (s[i] == p[j] || p[j] == '.'));

        bool ans;
        if (j + 1 < p.size() && p[j + 1] == '*') {
            bool zeroUse = rec(i, j + 2, s, p);
            bool oneOrMoreUse = firstMatch && rec(i + 1, j, s, p);
            ans = zeroUse || oneOrMoreUse;
        } else {
            ans = firstMatch && rec(i + 1, j + 1, s, p);
        }

        // save and return
        return dp[i][j] = ans;
    }

    bool isMatch(string s, string p) {
        dp.assign(s.size() + 1, vector<int>(p.size() + 1, -1));
        return rec(0, 0, s, p);
    }
};
```

## Dry run

```text
s = aab
p = c*a*b

c* can be used zero times.
Remaining:
s = aab
p = a*b

a* matches aa.
Remaining:
s = b
p = b

b matches b.
answer = true
```

---

# Form 4: Grid DP

## Pattern

Use when moving inside a matrix.

Common states:

```text
DP(r,c) = answer from cell r,c
```

or

```text
DP(r,c) = answer to reach cell r,c
```

---

# 4.1 Unique Paths II

## Problem

Count paths from top-left to bottom-right with obstacles.

## Input

```text
grid = [
 [0,0,0],
 [0,1,0],
 [0,0,0]
]
```

## Expected Output

```text
2
```

## Framework

### Look for the form

Movement in grid: right/down.

So this is **Form 4: Grid DP**.

### State & meaning

```text
DP(r,c) = number of ways to reach bottom-right from cell (r,c)
```

### Transition

```text
DP(r,c) = DP(r+1,c) + DP(r,c+1)
```

if cell is obstacle:

```text
DP(r,c) = 0
```

### Time complexity

```text
#States = rows * cols
Transitions = 2

TC = O(RC)
SC = O(RC)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int R, C;
    vector<vector<int>> dp;

    int rec(int r, int c, vector<vector<int>>& grid) {
        // pruning
        if (r < 0 || c < 0 || r >= R || c >= C) return 0;
        if (grid[r][c] == 1) return 0;

        // base case
        if (r == R - 1 && c == C - 1) return 1;

        // cache check
        if (dp[r][c] != -1) return dp[r][c];

        // transitions
        int down = rec(r + 1, c, grid);
        int right = rec(r, c + 1, grid);

        // save and return
        return dp[r][c] = down + right;
    }

    int uniquePathsWithObstacles(vector<vector<int>>& obstacleGrid) {
        R = obstacleGrid.size();
        C = obstacleGrid[0].size();
        dp.assign(R, vector<int>(C, -1));

        return rec(0, 0, obstacleGrid);
    }
};
```

## Dry run

```text
Grid:
0 0 0
0 1 0
0 0 0

Paths:
Right Right Down Down
Down Down Right Right

answer = 2
```

---

# 4.2 Minimum Path Sum

## Problem

Find minimum path sum from top-left to bottom-right.

## Input

```text
grid = [
 [1,3,1],
 [1,5,1],
 [4,2,1]
]
```

## Expected Output

```text
7
```

## Framework

### Look for the form

Grid movement with minimization.

So this is **Form 4: Grid DP**.

### State & meaning

```text
DP(r,c) = minimum cost from cell (r,c) to bottom-right
```

### Transition

```text
DP(r,c) = grid[r][c] + min(DP(r+1,c), DP(r,c+1))
```

### Time complexity

```text
#States = R*C
Transitions = 2

TC = O(RC)
SC = O(RC)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int minPathSum(vector<vector<int>>& grid) {
        int R = grid.size();
        int C = grid[0].size();

        vector<vector<int>> dp(R, vector<int>(C, 0));

        for (int r = R - 1; r >= 0; r--) {
            for (int c = C - 1; c >= 0; c--) {
                if (r == R - 1 && c == C - 1) {
                    dp[r][c] = grid[r][c];
                } else {
                    int down = (r + 1 < R) ? dp[r + 1][c] : INT_MAX / 2;
                    int right = (c + 1 < C) ? dp[r][c + 1] : INT_MAX / 2;
                    dp[r][c] = grid[r][c] + min(down, right);
                }
            }
        }

        return dp[0][0];
    }
};
```

## Dry run

```text
grid:
1 3 1
1 5 1
4 2 1

Best path:
1 -> 3 -> 1 -> 1 -> 1

sum = 7
```

---

# 4.3 Dungeon Game

## Problem

Knight starts at top-left and needs minimum initial health to reach princess at bottom-right. Health must always be at least 1.

## Input

```text
dungeon = [
 [-2,-3,3],
 [-5,-10,1],
 [10,30,-5]
]
```

## Expected Output

```text
7
```

## Framework

### Look for the form

Grid with future survival constraint.

The important trick:

```text
Work backward from princess to start.
```

So this is **Form 4: Reverse Grid DP**.

### State & meaning

```text
DP(r,c) = minimum health needed before entering cell (r,c)
```

### Transition

```text
needAfter = min(DP(r+1,c), DP(r,c+1))
DP(r,c) = max(1, needAfter - dungeon[r][c])
```

### Time complexity

```text
#States = R*C
Transitions = 2

TC = O(RC)
SC = O(RC)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int calculateMinimumHP(vector<vector<int>>& dungeon) {
        int R = dungeon.size();
        int C = dungeon[0].size();
        const int INF = 1e9;

        vector<vector<int>> dp(R + 1, vector<int>(C + 1, INF));
        dp[R][C - 1] = 1;
        dp[R - 1][C] = 1;

        for (int r = R - 1; r >= 0; r--) {
            for (int c = C - 1; c >= 0; c--) {
                int needAfter = min(dp[r + 1][c], dp[r][c + 1]);
                dp[r][c] = max(1, needAfter - dungeon[r][c]);
            }
        }

        return dp[0][0];
    }
};
```

## Dry run

```text
At princess cell -5:
need after = 1
need before = max(1, 1 - (-5)) = 6

At cell 30:
need after = 6
need before = max(1, 6 - 30) = 1

At start, final computed answer = 7
```

---

# Form 5: Interval / Left-Right DP

## Pattern

Use when problem is over a range:

```text
[l...r]
```

and choices:

```text
remove/choose left
remove/choose right
split at k
choose last operation in interval
```

---

# 5.1 Palindrome Partitioning II

## Problem

Return minimum cuts needed to partition string into palindromes.

## Input

```text
s = "aab"
```

## Expected Output

```text
1
```

## Framework

### Look for the form

We split string into palindrome pieces.

This is **Form 5: Partition / Interval DP**.

### State & meaning

```text
DP(i) = minimum cuts needed for suffix s[i...n-1]
```

### Transition

Try every ending `j` where `s[i...j]` is palindrome:

```text
DP(i) = min(1 + DP(j+1))
```

At end:

```text
DP(n) = -1
```

Why `-1`? Because last piece should not add a cut.

### Time complexity

```text
Palindrome precompute = O(n^2)
#States = n
Transitions per state = O(n)

TC = O(n^2)
SC = O(n^2)
```

## C++ Code

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
            dp[i] = INT_MAX / 2;

            for (int j = i; j < n; j++) {
                if (pal[i][j]) {
                    dp[i] = min(dp[i], 1 + dp[j + 1]);
                }
            }
        }

        return dp[0];
    }
};
```

## Dry run

```text
s = "aab"

palindromes:
"a", "a", "b", "aa"

DP(3) = -1
DP(2) = 1 + DP(3) = 0        // "b"
DP(1) = 1 + DP(2) = 1        // "a|b"
DP(0):
  "a"  -> 1 + DP(1) = 2
  "aa" -> 1 + DP(2) = 1

answer = 1 => "aa|b"
```

---

# 5.2 Burst Balloons

## Problem

Burst balloons to maximize coins. If burst `k`, gain:

```text
nums[left] * nums[k] * nums[right]
```

where left and right are nearest remaining balloons.

## Input

```text
nums = [3,1,5,8]
```

## Expected Output

```text
167
```

## Framework

### Look for the form

Hard insight:

```text
Do not choose first burst.
Choose the last balloon burst inside interval.
```

This makes neighbors fixed.

So this is **Form 5: Interval DP / choose last operation**.

### State & meaning

Add boundary 1s:

```text
arr = [1,3,1,5,8,1]
```

```text
DP(l,r) = max coins from bursting balloons strictly between l and r
```

### Transition

Choose `k` as the last balloon burst between `l` and `r`:

```text
DP(l,r) = max over k:
          DP(l,k) + DP(k,r) + arr[l] * arr[k] * arr[r]
```

### Time complexity

```text
#States = n^2
Transitions per state = n

TC = O(n^3)
SC = O(n^2)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<vector<int>> dp;
    vector<int> a;

    int rec(int l, int r) {
        // pruning
        if (l + 1 >= r) return 0;

        // base case
        // same as pruning: no balloon inside

        // cache check
        if (dp[l][r] != -1) return dp[l][r];

        // transitions
        int ans = 0;

        for (int k = l + 1; k <= r - 1; k++) {
            int coins = rec(l, k) + rec(k, r) + a[l] * a[k] * a[r];
            ans = max(ans, coins);
        }

        // save and return
        return dp[l][r] = ans;
    }

    int maxCoins(vector<int>& nums) {
        a.clear();
        a.push_back(1);
        for (int x : nums) a.push_back(x);
        a.push_back(1);

        int n = a.size();
        dp.assign(n, vector<int>(n, -1));

        return rec(0, n - 1);
    }
};
```

## Dry run intuition

```text
nums = [3,1,5,8]
a = [1,3,1,5,8,1]

DP(0,5) tries last burst:
k=1 => last is 3
k=2 => last is 1
k=3 => last is 5
k=4 => last is 8

For each k:
left interval and right interval are independent.
```

Key interview line:

```text
If choosing first operation makes neighbors unstable,
try choosing last operation.
```

---

# 5.3 Matrix Chain Multiplication

## Problem

Given dimensions array, find minimum multiplication cost.

## Input

```text
arr = [40,20,30,10,30]
```

## Expected Output

```text
26000
```

## Framework

### Look for the form

We split interval of matrices.

So this is **Form 5: Interval DP / split at k**.

### State & meaning

Matrices:

```text
A1 = 40x20
A2 = 20x30
A3 = 30x10
A4 = 10x30
```

```text
DP(i,j) = minimum cost to multiply matrices Ai...Aj
```

### Transition

Split at `k`:

```text
DP(i,j) = min over k:
          DP(i,k) + DP(k+1,j) + arr[i-1] * arr[k] * arr[j]
```

### Time complexity

```text
#States = n^2
Transitions = n

TC = O(n^3)
SC = O(n^2)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int matrixMultiplication(vector<int>& arr) {
    int n = arr.size();
    vector<vector<int>> dp(n, vector<int>(n, -1));

    function<int(int,int)> rec = [&](int i, int j) {
        // pruning
        // none

        // base case
        if (i == j) return 0;

        // cache check
        if (dp[i][j] != -1) return dp[i][j];

        // transitions
        int ans = INT_MAX;

        for (int k = i; k < j; k++) {
            int cost = rec(i, k) + rec(k + 1, j) + arr[i - 1] * arr[k] * arr[j];
            ans = min(ans, cost);
        }

        // save and return
        return dp[i][j] = ans;
    };

    return rec(1, n - 1);
}
```

## Dry run

```text
arr = [40,20,30,10,30]

Best parenthesization:
(A1 * (A2 * A3)) * A4

cost:
A2*A3 = 20*30*10 = 6000
A1*(A2A3) = 40*20*10 = 8000
result*A4 = 40*10*30 = 12000

total = 26000
```

---

# 5.4 Minimum Cost to Cut a Stick

## Problem

Given stick length `n` and cut positions, return min total cutting cost.

## Input

```text
n = 7
cuts = [1,3,4,5]
```

## Expected Output

```text
16
```

## Framework

### Look for the form

Every cut splits an interval.

So this is **Form 5: Interval DP / choose first cut inside interval**.

### State & meaning

Add boundaries:

```text
points = [0,1,3,4,5,7]
```

```text
DP(l,r) = minimum cost to complete cuts strictly between points[l] and points[r]
```

### Transition

Choose cut `k` first:

```text
DP(l,r) = min over k in (l,r):
          points[r] - points[l] + DP(l,k) + DP(k,r)
```

### Time complexity

```text
#States = m^2
Transitions = m

TC = O(m^3)
SC = O(m^2)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<int> p;
    vector<vector<int>> dp;

    int rec(int l, int r) {
        // pruning/base
        if (l + 1 >= r) return 0;

        // cache check
        if (dp[l][r] != -1) return dp[l][r];

        // transitions
        int ans = INT_MAX;

        for (int k = l + 1; k <= r - 1; k++) {
            int cost = (p[r] - p[l]) + rec(l, k) + rec(k, r);
            ans = min(ans, cost);
        }

        // save and return
        return dp[l][r] = ans;
    }

    int minCost(int n, vector<int>& cuts) {
        p = cuts;
        p.push_back(0);
        p.push_back(n);
        sort(p.begin(), p.end());

        int m = p.size();
        dp.assign(m, vector<int>(m, -1));

        return rec(0, m - 1);
    }
};
```

## Dry run

```text
n = 7
cuts = [1,3,4,5]
points = [0,1,3,4,5,7]

If first cut is 3:
cost = 7
left interval [0,3] has cut 1
right interval [3,7] has cuts 4,5

DP combines independent intervals.
answer = 16
```

---

# Form 6: Game DP

## Pattern

Use when players play optimally.

Common state:

```text
DP(l,r) = best score difference current player can achieve from nums[l...r]
```

This avoids tracking both players separately.

---

# 6.1 Predict the Winner

## Problem

Two players pick from ends. Return if player 1 can win.

## Input

```text
nums = [1,5,2]
```

## Expected Output

```text
false
```

## Framework

### Look for the form

Two players alternate, pick left/right.

So this is **Form 6: Game DP**.

### State & meaning

```text
DP(l,r) = maximum score difference current player can get over opponent from nums[l...r]
```

### Transition

```text
pickLeft  = nums[l] - DP(l+1,r)
pickRight = nums[r] - DP(l,r-1)

DP(l,r) = max(pickLeft, pickRight)
```

### Time complexity

```text
#States = n^2
Transitions = 2

TC = O(n^2)
SC = O(n^2)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<vector<int>> dp;

    int rec(int l, int r, vector<int>& nums) {
        // pruning/base case
        if (l > r) return 0;

        // cache check
        if (dp[l][r] != INT_MIN) return dp[l][r];

        // transitions
        int pickLeft = nums[l] - rec(l + 1, r, nums);
        int pickRight = nums[r] - rec(l, r - 1, nums);

        // save and return
        return dp[l][r] = max(pickLeft, pickRight);
    }

    bool PredictTheWinner(vector<int>& nums) {
        int n = nums.size();
        dp.assign(n, vector<int>(n, INT_MIN));

        return rec(0, n - 1, nums) >= 0;
    }
};
```

## Dry run

```text
nums = [1,5,2]

DP(1,1)=5
DP(2,2)=2

DP(1,2):
pick 5 => 5 - DP(2,2) = 3
pick 2 => 2 - DP(1,1) = -3
DP(1,2)=3

DP(0,1):
pick 1 => 1 - 5 = -4
pick 5 => 5 - 1 = 4
DP(0,1)=4

DP(0,2):
pick 1 => 1 - DP(1,2)= -2
pick 2 => 2 - DP(0,1)= -2

Player 1 loses.
```

---

# 6.2 Stone Game II

## Problem

Players take piles. On each turn, take `X` piles where `1 <= X <= 2M`, then `M = max(M, X)`. Return max stones Alice can get.

## Input

```text
piles = [2,7,9,4,4]
```

## Expected Output

```text
10
```

## Framework

### Look for the form

Game + changing parameter `M`.

So this is **Form 6: Game DP with parameter**.

### State & meaning

```text
DP(i,M) = maximum stones current player can get from piles[i...]
```

### Transition

If current player takes `X` piles:

```text
taken = suffix[i] - suffix[i+X]
opponentGets = DP(i+X, max(M,X))

currentGets = suffix[i] - opponentGets
```

So:

```text
DP(i,M) = max over X in [1,2M]:
          suffix[i] - DP(i+X, max(M,X))
```

### Time complexity

```text
#States = n*n
Transitions up to n

TC = O(n^3)
SC = O(n^2)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<vector<int>> dp;
    vector<int> suffix;
    int n;

    int rec(int i, int M) {
        // pruning/base case
        if (i >= n) return 0;

        // if can take all
        if (i + 2 * M >= n) return suffix[i];

        // cache check
        if (dp[i][M] != -1) return dp[i][M];

        // transitions
        int ans = 0;

        for (int X = 1; X <= 2 * M; X++) {
            int opponent = rec(i + X, max(M, X));
            int current = suffix[i] - opponent;
            ans = max(ans, current);
        }

        // save and return
        return dp[i][M] = ans;
    }

    int stoneGameII(vector<int>& piles) {
        n = piles.size();
        suffix.assign(n + 1, 0);

        for (int i = n - 1; i >= 0; i--) {
            suffix[i] = piles[i] + suffix[i + 1];
        }

        dp.assign(n, vector<int>(n + 1, -1));

        return rec(0, 1);
    }
};
```

## Dry run intuition

```text
piles = [2,7,9,4,4]

Alice starts at i=0, M=1.
Can take X=1 or X=2.

If Alice takes 1:
remaining starts at 7, opponent state = DP(1,1)

If Alice takes 2:
remaining starts at 9, opponent state = DP(2,2)

Alice maximizes:
suffix[0] - opponentBest
```

---

# Form 7: State Machine DP

## Pattern

Use when state is not only index but also mode:

```text
holding stock or not
cooldown or not
transactions used
```

---

# 7.1 Best Time to Buy and Sell Stock with Cooldown

## Problem

Buy/sell stock with one-day cooldown after selling.

## Input

```text
prices = [1,2,3,0,2]
```

## Expected Output

```text
3
```

## Framework

### Look for the form

At each day we have a mode:

```text
holding stock
not holding stock
cooldown
```

So this is **Form 7: State Machine DP**.

### State & meaning

```text
DP(i, holding) = max profit from day i onward
```

### Transition

If holding:

```text
sell = prices[i] + DP(i+2, 0)
hold = DP(i+1, 1)
```

If not holding:

```text
buy = -prices[i] + DP(i+1, 1)
skip = DP(i+1, 0)
```

### Time complexity

```text
#States = n*2
Transitions = 2

TC = O(n)
SC = O(n)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<vector<int>> dp;

    int rec(int i, int hold, vector<int>& prices) {
        // pruning/base
        if (i >= prices.size()) return 0;

        // cache check
        if (dp[i][hold] != -1) return dp[i][hold];

        // transitions
        int ans;

        if (hold) {
            int sell = prices[i] + rec(i + 2, 0, prices);
            int keepHolding = rec(i + 1, 1, prices);
            ans = max(sell, keepHolding);
        } else {
            int buy = -prices[i] + rec(i + 1, 1, prices);
            int skip = rec(i + 1, 0, prices);
            ans = max(buy, skip);
        }

        // save and return
        return dp[i][hold] = ans;
    }

    int maxProfit(vector<int>& prices) {
        dp.assign(prices.size(), vector<int>(2, -1));
        return rec(0, 0, prices);
    }
};
```

## Dry run

```text
prices = [1,2,3,0,2]

Best:
buy day 0 at 1
sell day 1 at 2 => profit 1
cooldown day 2
buy day 3 at 0
sell day 4 at 2 => profit 2

total = 3
```

---

# 7.2 Best Time to Buy and Sell Stock IV

## Problem

At most `k` transactions.

## Input

```text
k = 2
prices = [3,2,6,5,0,3]
```

## Expected Output

```text
7
```

## Framework

### Look for the form

State needs:

```text
day
holding/not holding
transactions left
```

So this is **Form 7: State Machine DP with transaction count**.

### State & meaning

```text
DP(i, hold, cap) = max profit from day i with hold state and cap sells remaining
```

### Transition

If holding:

```text
sell = prices[i] + DP(i+1, 0, cap-1)
hold = DP(i+1, 1, cap)
```

If not holding:

```text
buy = -prices[i] + DP(i+1, 1, cap)
skip = DP(i+1, 0, cap)
```

### Time complexity

```text
#States = n * 2 * k
Transitions = 2

TC = O(nk)
SC = O(nk)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    vector<vector<vector<int>>> dp;

    int rec(int i, int hold, int cap, vector<int>& prices) {
        // pruning/base
        if (i == prices.size() || cap == 0) return 0;

        // cache check
        if (dp[i][hold][cap] != -1) return dp[i][hold][cap];

        // transitions
        int ans;

        if (hold) {
            int sell = prices[i] + rec(i + 1, 0, cap - 1, prices);
            int keep = rec(i + 1, 1, cap, prices);
            ans = max(sell, keep);
        } else {
            int buy = -prices[i] + rec(i + 1, 1, cap, prices);
            int skip = rec(i + 1, 0, cap, prices);
            ans = max(buy, skip);
        }

        // save and return
        return dp[i][hold][cap] = ans;
    }

    int maxProfit(int k, vector<int>& prices) {
        int n = prices.size();
        dp.assign(n, vector<vector<int>>(2, vector<int>(k + 1, -1)));
        return rec(0, 0, k, prices);
    }
};
```

## Dry run

```text
prices = [3,2,6,5,0,3], k=2

Best:
buy at 2, sell at 6 => profit 4
buy at 0, sell at 3 => profit 3

total = 7
```

---

# Form 8: Bitmask DP

## Pattern

Use when `n` is small, usually `n <= 20`, and we need track selected/used items.

Common state:

```text
DP(mask) = best answer after selected items in mask
```

---

# 8.1 Partition to K Equal Sum Subsets

## Problem

Can array be partitioned into `k` subsets with equal sum?

## Input

```text
nums = [4,3,2,3,5,2,1], k = 4
```

## Expected Output

```text
true
```

## Framework

### Look for the form

Need know which elements are already used.

So this is **Form 8: Bitmask DP**.

### State & meaning

```text
DP(mask) = whether used elements in mask can lead to valid partitioning
```

We also track current bucket sum by:

```text
currentSum = sum(mask) % target
```

### Transition

Try adding unused element `i` if it does not exceed target:

```text
if currentSum + nums[i] <= target:
    DP(mask | (1<<i)) = true
```

### Time complexity

```text
#States = 2^n
Transitions = n

TC = O(n * 2^n)
SC = O(2^n)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    bool canPartitionKSubsets(vector<int>& nums, int k) {
        int total = accumulate(nums.begin(), nums.end(), 0);
        if (total % k != 0) return false;

        int target = total / k;
        int n = nums.size();

        sort(nums.rbegin(), nums.rend());
        if (nums[0] > target) return false;

        int N = 1 << n;
        vector<int> dp(N, -1);
        dp[0] = 0;

        for (int mask = 0; mask < N; mask++) {
            if (dp[mask] == -1) continue;

            for (int i = 0; i < n; i++) {
                if (mask & (1 << i)) continue;

                int nextSum = dp[mask] + nums[i];

                if (nextSum <= target) {
                    int nextMask = mask | (1 << i);
                    dp[nextMask] = nextSum % target;
                }
            }
        }

        return dp[N - 1] == 0;
    }
};
```

## Dry run intuition

```text
nums = [4,3,2,3,5,2,1], k=4
total = 20
target = 5

Valid buckets:
[5]
[4,1]
[3,2]
[3,2]

Bitmask tracks which numbers are already used.
```

---

# 8.2 Minimum Cost Assignment

## Problem

Assign each worker one job with minimum total cost.

## Input

```text
cost = [
 [9,2,7],
 [6,4,3],
 [5,8,1]
]
```

## Expected Output

```text
9
```

## Framework

### Look for the form

Need know which jobs already assigned.

So this is **Form 8: Bitmask DP**.

### State & meaning

```text
DP(worker, mask) = min cost from this worker onward when jobs in mask are already used
```

But worker can be derived:

```text
worker = popcount(mask)
```

So:

```text
DP(mask) = min cost after assigning jobs in mask to first popcount(mask) workers
```

### Transition

Assign an unused job `j` to current worker:

```text
DP(mask) = min(cost[worker][j] + DP(mask | (1<<j)))
```

### Time complexity

```text
#States = 2^n
Transitions = n

TC = O(n * 2^n)
SC = O(2^n)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int minAssignmentCost(vector<vector<int>>& cost) {
    int n = cost.size();
    int N = 1 << n;
    vector<int> dp(N, -1);

    function<int(int)> rec = [&](int mask) {
        // base case
        if (mask == N - 1) return 0;

        // cache check
        if (dp[mask] != -1) return dp[mask];

        // transitions
        int worker = __builtin_popcount((unsigned)mask);
        int ans = INT_MAX;

        for (int job = 0; job < n; job++) {
            if (!(mask & (1 << job))) {
                ans = min(ans, cost[worker][job] + rec(mask | (1 << job)));
            }
        }

        // save and return
        return dp[mask] = ans;
    };

    return rec(0);
}
```

## Dry run

```text
cost:
worker0: [9,2,7]
worker1: [6,4,3]
worker2: [5,8,1]

Best:
worker0 -> job1 cost 2
worker1 -> job0 cost 6
worker2 -> job2 cost 1

total = 9
```

---

# Form 9: Tree DP

## Pattern

Use when answer depends on child subtrees.

Common state:

```text
DP(node) = answer for subtree rooted at node
```

Sometimes need multiple values:

```text
take node
skip node
```

---

# 9.1 House Robber III

## Problem

Rob houses arranged as binary tree. Cannot rob directly connected nodes.

## Framework

### Look for the form

For each tree node:

```text
rob node
or
skip node
```

But children are affected.

So this is **Form 9: Tree DP with take/skip state**.

### State & meaning

```text
DP(node) = {skipNode, takeNode}
```

Where:

```text
takeNode = node->val + left.skip + right.skip
skipNode = max(left.skip,left.take) + max(right.skip,right.take)
```

### Time complexity

```text
#States = number of nodes
Transitions = O(1)

TC = O(n)
SC = O(height)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct TreeNode {
    int val;
    TreeNode *left;
    TreeNode *right;
    TreeNode(int x) : val(x), left(nullptr), right(nullptr) {}
};

class Solution {
public:
    pair<int,int> dfs(TreeNode* node) {
        // base case
        if (!node) return {0, 0};

        // transitions on children
        auto L = dfs(node->left);
        auto R = dfs(node->right);

        int skip = max(L.first, L.second) + max(R.first, R.second);
        int take = node->val + L.first + R.first;

        return {skip, take};
    }

    int rob(TreeNode* root) {
        auto ans = dfs(root);
        return max(ans.first, ans.second);
    }
};
```

## Dry run

```text
Tree:
    3
   / \
  2   3
   \   \
    3   1

For leaf 3:
skip=0, take=3

For node 2:
take = 2 + child.skip = 2
skip = max(0,3) = 3

For right node 3:
take = 3
skip = 1

Root 3:
take = 3 + left.skip 3 + right.skip 1 = 7
skip = max(3,2) + max(1,3) = 6

answer = 7
```

---

# 9.2 Binary Tree Maximum Path Sum

## Problem

Find maximum path sum in binary tree. Path can start and end anywhere.

## Framework

### Look for the form

At each node, we need:

```text
best path going upward
and
best global path passing through node
```

So this is **Form 9: Tree DP with local/global answer**.

### State & meaning

```text
DP(node) = maximum gain path starting at node and going downward to one side
```

### Transition

```text
leftGain = max(0, DP(left))
rightGain = max(0, DP(right))

global answer = max(global answer, node->val + leftGain + rightGain)

return node->val + max(leftGain, rightGain)
```

### Time complexity

```text
#States = nodes
Transitions = O(1)

TC = O(n)
SC = O(height)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct TreeNode {
    int val;
    TreeNode *left;
    TreeNode *right;
    TreeNode(int x) : val(x), left(nullptr), right(nullptr) {}
};

class Solution {
public:
    int ans;

    int dfs(TreeNode* node) {
        // base case
        if (!node) return 0;

        // transitions
        int leftGain = max(0, dfs(node->left));
        int rightGain = max(0, dfs(node->right));

        int pathThroughNode = node->val + leftGain + rightGain;
        ans = max(ans, pathThroughNode);

        return node->val + max(leftGain, rightGain);
    }

    int maxPathSum(TreeNode* root) {
        ans = INT_MIN;
        dfs(root);
        return ans;
    }
};
```

## Dry run

```text
Tree:
   -10
   /  \
  9   20
     /  \
    15   7

At 15: gain=15, ans=15
At 7 : gain=7, ans=15

At 20:
leftGain=15, rightGain=7
pathThroughNode=20+15+7=42
gain upward=20+max(15,7)=35

At root -10:
path through root = -10 + 9 + 35 = 34

answer = 42
```

---

# Form 10: Digit DP

## Pattern

Use when counting numbers:

```text
<= N
with digit constraints
```

Common state:

```text
DP(pos, tight, started, otherInfo)
```

---

# 10.1 Count Numbers With Unique Digits Pattern

## Problem

Count numbers from `0` to `N` where all digits are unique.

## Input

```text
N = 100
```

## Expected Output

```text
91
```

## Framework

### Look for the form

Counting numbers under a limit with digit constraints.

So this is **Form 10: Digit DP**.

### State & meaning

```text
DP(pos, mask, tight, started) =
number of valid numbers from current digit position
```

Where:

```text
pos = current index in digits
mask = used digits
tight = whether prefix is equal to N prefix
started = whether number has started
```

### Transition

Try digit `d` from 0 to limit:

```text
newTight = tight && (d == limit)
if not started and d == 0:
    continue as not started
else:
    digit must not be used in mask
```

### Time complexity

```text
#States = digits * 2^10 * 2 * 2
Transitions = 10

TC = O(digits * 2^10 * 10)
SC = O(digits * 2^10 * 2 * 2)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class DigitDPUnique {
public:
    string s;
    long long dp[20][1 << 10][2][2];
    bool seen[20][1 << 10][2][2];

    long long rec(int pos, int mask, int tight, int started) {
        // base case
        if (pos == s.size()) {
            return 1; // count number formed, including 0
        }

        // cache check
        if (seen[pos][mask][tight][started]) {
            return dp[pos][mask][tight][started];
        }

        seen[pos][mask][tight][started] = true;

        // transitions
        int limit = tight ? s[pos] - '0' : 9;
        long long ans = 0;

        for (int d = 0; d <= limit; d++) {
            int ntight = tight && (d == limit);

            if (!started && d == 0) {
                ans += rec(pos + 1, mask, ntight, 0);
            } else {
                if (mask & (1 << d)) continue;
                ans += rec(pos + 1, mask | (1 << d), ntight, 1);
            }
        }

        return dp[pos][mask][tight][started] = ans;
    }

    long long countUnique(long long N) {
        s = to_string(N);
        memset(seen, 0, sizeof(seen));
        return rec(0, 0, 1, 0);
    }
};
```

## Dry run intuition

```text
N = 100

Valid numbers:
0..99 except repeated digits:
11,22,33,44,55,66,77,88,99

100 is invalid because digit 0 repeats.

Total 0..100 = 101
Invalid repeated = 10
Valid = 91
```

---

# 10.2 Count Digit One

## Problem

Count total number of digit `1` appearing in all non-negative integers <= n.

## Input

```text
n = 13
```

## Expected Output

```text
6
```

## Framework

### Look for the form

Counting digit occurrences under bound.

So this is **Form 10: Digit DP**.

### State & meaning

```text
DP(pos, countOnes, tight) = total ways/count contribution from position pos
```

Better return pair:

```text
{numberCount, onesCount}
```

For each suffix:

```text
numberCount = how many numbers can be formed
onesCount = total ones inside those numbers
```

### Transition

For each digit `d`:

```text
child = rec(pos+1,...)
totalNumbers += child.numberCount
totalOnes += child.onesCount + (d == 1 ? child.numberCount : 0)
```

### Time complexity

```text
#States = digits * tight
Transitions = 10

TC = O(digits * 10)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    string s;
    pair<long long,long long> dp[20][2];
    bool seen[20][2];

    pair<long long,long long> rec(int pos, int tight) {
        // base case
        if (pos == s.size()) {
            return {1, 0}; // one number formed, zero ones in empty suffix
        }

        // cache check
        if (seen[pos][tight]) return dp[pos][tight];
        seen[pos][tight] = true;

        // transitions
        int limit = tight ? s[pos] - '0' : 9;

        long long totalNums = 0;
        long long totalOnes = 0;

        for (int d = 0; d <= limit; d++) {
            int ntight = tight && (d == limit);

            auto child = rec(pos + 1, ntight);

            totalNums += child.first;
            totalOnes += child.second;

            if (d == 1) {
                totalOnes += child.first;
            }
        }

        return dp[pos][tight] = {totalNums, totalOnes};
    }

    int countDigitOne(int n) {
        s = to_string(n);
        memset(seen, 0, sizeof(seen));
        return (int)rec(0, 1).second;
    }
};
```

## Dry run

```text
n = 13

Numbers with ones:
1  -> one 1
10 -> one 1
11 -> two 1s
12 -> one 1
13 -> one 1

total = 1 + 1 + 2 + 1 + 1 = 6
```

---

# Form 11: Advanced Optimization DP

## Pattern

Use when simple DP is known, then optimize:

```text
space optimization
precomputation
state compression
monotonic structure
binary search
```

---

# 11.1 Coin Change II

## Problem

Number of combinations to make amount using unlimited coins.

## Input

```text
amount = 5
coins = [1,2,5]
```

## Expected Output

```text
4
```

## Framework

### Look for the form

For each coin:

```text
take coin again
or
skip to next coin
```

This is **Take / Not Take with unlimited take**.

### State & meaning

```text
DP(i, amount) = number of ways to make amount using coins i...n-1
```

### Transition

```text
skip = DP(i+1, amount)
take = DP(i, amount - coins[i]) if amount >= coins[i]

DP(i, amount) = skip + take
```

### Time complexity

```text
#States = n * amount
Transitions = 2

TC = O(n * amount)
SC = O(n * amount)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int change(int amount, vector<int>& coins) {
        vector<long long> dp(amount + 1, 0);
        dp[0] = 1;

        for (int coin : coins) {
            for (int sum = coin; sum <= amount; sum++) {
                dp[sum] += dp[sum - coin];
            }
        }

        return dp[amount];
    }
};
```

## Dry run

```text
amount = 5
coins = [1,2,5]

Using coin 1:
ways: 1 way for every amount

Using coin 2:
amount 2: [1+1], [2]
amount 4: [1+1+1+1], [2+1+1], [2+2]

Using coin 5:
amount 5 adds [5]

Total combinations:
[1,1,1,1,1]
[1,1,1,2]
[1,2,2]
[5]

answer = 4
```

---

# 11.2 Longest Palindromic Subsequence

## Problem

Find length of longest palindromic subsequence.

## Input

```text
s = "bbbab"
```

## Expected Output

```text
4
```

## Framework

### Look for the form

Subsequence inside interval `l...r`.

So this is **Interval DP / Matching self DP**.

### State & meaning

```text
DP(l,r) = longest palindromic subsequence length inside s[l...r]
```

### Transition

If same:

```text
DP(l,r) = 2 + DP(l+1,r-1)
```

Else:

```text
DP(l,r) = max(DP(l+1,r), DP(l,r-1))
```

### Time complexity

```text
#States = n^2
Transitions = O(1)

TC = O(n^2)
SC = O(n^2)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int longestPalindromeSubseq(string s) {
        int n = s.size();
        vector<vector<int>> dp(n, vector<int>(n, 0));

        for (int i = 0; i < n; i++) dp[i][i] = 1;

        for (int len = 2; len <= n; len++) {
            for (int l = 0; l + len - 1 < n; l++) {
                int r = l + len - 1;

                if (s[l] == s[r]) {
                    dp[l][r] = 2 + (len == 2 ? 0 : dp[l + 1][r - 1]);
                } else {
                    dp[l][r] = max(dp[l + 1][r], dp[l][r - 1]);
                }
            }
        }

        return dp[0][n - 1];
    }
};
```

## Dry run

```text
s = bbbab

Longest palindromic subsequence:
bbbb

length = 4

DP expands by interval length.
```

---

# 11.3 Scramble String

## Problem

Check if one string is a scramble of another.

## Input

```text
s1 = "great"
s2 = "rgeat"
```

## Expected Output

```text
true
```

## Framework

### Look for the form

Two substrings and split point.

So this is **Advanced Interval + Matching DP**.

### State & meaning

```text
DP(i,j,len) = whether s1 substring starting i of length len
              can scramble to s2 substring starting j of length len
```

### Transition

Try split length `k`:

No swap:

```text
DP(i,j,k) && DP(i+k,j+k,len-k)
```

Swap:

```text
DP(i,j+len-k,k) && DP(i+k,j,len-k)
```

### Time complexity

```text
#States = n*n*n
Transitions = n

TC = O(n^4)
SC = O(n^3)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    string A, B;
    unordered_map<string, bool> memo;

    bool rec(int i, int j, int len) {
        // base case
        string key = to_string(i) + "," + to_string(j) + "," + to_string(len);
        if (memo.count(key)) return memo[key];

        string a = A.substr(i, len);
        string b = B.substr(j, len);

        if (a == b) return memo[key] = true;

        // pruning: different char counts impossible
        string sa = a, sb = b;
        sort(sa.begin(), sa.end());
        sort(sb.begin(), sb.end());
        if (sa != sb) return memo[key] = false;

        // transitions
        for (int k = 1; k < len; k++) {
            bool noSwap = rec(i, j, k) && rec(i + k, j + k, len - k);
            bool swapCase = rec(i, j + len - k, k) && rec(i + k, j, len - k);

            if (noSwap || swapCase) {
                return memo[key] = true;
            }
        }

        return memo[key] = false;
    }

    bool isScramble(string s1, string s2) {
        A = s1;
        B = s2;
        memo.clear();

        if (s1.size() != s2.size()) return false;
        return rec(0, 0, s1.size());
    }
};
```

## Dry run

```text
s1 = great
s2 = rgeat

Split:
great -> gr | eat
rgeat -> rg | eat

gr can scramble to rg by swapping g and r.
eat matches eat.

answer = true
```

---

# DP Recognition Checklist

Before writing code, ask:

```text
1. Is there a choice?
   take/skip, left/right, split, match/skip?

2. What is the smallest state that represents the future?

3. What does DP(...) exactly mean?

4. What are valid transitions?

5. What are invalid states?

6. What is the base case?

7. Is answer DP(0), max(DP(i)), or DP(0,n-1)?

8. Complexity:
   #states * transitions/state
```

---

# FAANG DP Hard Problem Attack Plan

For any hard DP problem:

```text
Step 1:
Ignore optimization first.

Step 2:
Write brute force recursion with state.

Step 3:
Add memoization.

Step 4:
Calculate states and transitions.

Step 5:
Only then optimize:
- precompute palindrome?
- reduce dimension?
- convert to bottom-up?
- space optimize?
- binary search?
- bitmask?
```

## Hard DP clue table

| Problem clue | Attack |
|---|---|
| "minimum cuts / partitions" | Try all split points |
| "burst / remove / merge" | Choose last operation |
| "two players" | Score difference DP |
| "two strings" | `DP(i,j)` |
| "stock" | State machine |
| "small n <= 20" | Bitmask |
| "tree" | Return multiple values from subtree |
| "number <= N" | Digit DP |
| "palindrome substring" | Precompute palindrome table |
| "print answer" | Store parent/choice |

---

# Final Mastery Plan

To ace FAANG DP:

```text
Phase 1:
Master Forms 1 to 4.
These cover most medium interviews.

Phase 2:
Master Interval, Game, State Machine.
These cover hard interview DP.

Phase 3:
Master Bitmask, Tree, Digit.
These cover advanced / top-tier interviews.

Phase 4:
Practice mixed random problems.
Goal: identify form in 10-20 seconds.
```

## Minimum problem count per form

| Form | Minimum problems |
|---|---:|
| Take / Not Take | 15 |
| Ending At Index | 15 |
| Matching DP | 12 |
| Grid DP | 8 |
| Interval DP | 12 |
| Game DP | 6 |
| State Machine DP | 8 |
| Bitmask DP | 6 |
| Tree DP | 6 |
| Digit DP | 5 |

---

# One-line DP mantra

```text
Form -> State Meaning -> Transition -> Base Case -> Complexity -> Code
```
