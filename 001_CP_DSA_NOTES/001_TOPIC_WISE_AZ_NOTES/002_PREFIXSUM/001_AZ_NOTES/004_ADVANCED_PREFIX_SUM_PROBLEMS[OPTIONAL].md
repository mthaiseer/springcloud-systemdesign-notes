# Advanced Prefix Sum Pattern Guide — FAANG + CP Expert/CM

> Goal: Do not memorize formulas. Learn how to **transform a condition into prefix states**.
>
> Style:
>
> - Problem statement
> - Input / Output
> - Recognition signal
> - Don't memorize thinking
> - C++ code
> - Index-by-index dry run
> - Final pattern takeaway

---

# Clickable Index

## 0. How To Think
- [0.1 Prefix Transformation Mindset](#01-prefix-transformation-mindset)
- [0.2 Decision Checklist](#02-decision-checklist)

## Phase 1 — Prefix + Deque
- [1. Shortest Subarray With Sum At Least K](#1-shortest-subarray-with-sum-at-least-k)
- [2. Constrained Subsequence Sum](#2-constrained-subsequence-sum)

## Phase 2 — Prefix XOR
- [3. Count Subarrays With XOR K](#3-count-subarrays-with-xor-k)
- [4. Maximum XOR of Two Numbers Using Prefix Trie](#4-maximum-xor-of-two-numbers-using-prefix-trie)

## Phase 3 — Prefix Bitmask / Parity
- [5. Number of Wonderful Substrings](#5-number-of-wonderful-substrings)
- [6. Longest Awesome Substring](#6-longest-awesome-substring)

## Phase 4 — Ordered Prefix / Merge Sort
- [7. Count of Range Sum](#7-count-of-range-sum)
- [8. Reverse Pairs Style Prefix Counting](#8-reverse-pairs-style-prefix-counting)

## Phase 5 — Coordinate Compression + Difference
- [9. Range Addition With Huge Coordinates](#9-range-addition-with-huge-coordinates)
- [10. Maximum Population Year / Event Sweep](#10-maximum-population-year--event-sweep)

## Phase 6 — Prefix + Binary Search
- [11. Minimum Size Subarray Sum](#11-minimum-size-subarray-sum)
- [12. Maximum Length Subarray With Sum At Most K](#12-maximum-length-subarray-with-sum-at-most-k)

## Phase 7 — 2D Advanced Prefix
- [13. Number of Submatrices That Sum to Target](#13-number-of-submatrices-that-sum-to-target)
- [14. Max Sum Rectangle No Larger Than K](#14-max-sum-rectangle-no-larger-than-k)

## Final Revision
- [Pattern Recognition Table](#pattern-recognition-table)
- [Contest Templates](#contest-templates)

---

# 0. How To Think

## 0.1 Prefix Transformation Mindset

Basic prefix says:

```text
sum(l..r) = pref[r] - pref[l - 1]
```

Advanced prefix asks:

```text
Can I rewrite the problem condition using two prefix states?
```

Examples:

```text
sum(l..r) = K
=> pref[r] - pref[l - 1] = K
=> pref[l - 1] = pref[r] - K
```

```text
sum(l..r) divisible by K
=> pref[r] % K == pref[l - 1] % K
```

```text
xor(l..r) = K
=> pxor[r] ^ pxor[l - 1] = K
=> pxor[l - 1] = pxor[r] ^ K
```

```text
all character counts even / at most one odd
=> parity bitmask repeated / differs by one bit
```

Do not memorize the problem. Memorize this question:

```text
What old prefix state do I need for current prefix state?
```

---

## 0.2 Decision Checklist

```text
1. Need sum of range?
   -> prefix sum

2. Need count of subarrays with target?
   -> prefix + hashmap

3. Need divisible / remainder?
   -> prefix modulo

4. Need XOR?
   -> prefix xor

5. Need odd/even count of characters?
   -> prefix bitmask

6. Need shortest/longest with inequality?
   -> prefix + deque / binary search / ordered set

7. Need many range updates?
   -> difference array / sweep line

8. Coordinates are huge?
   -> coordinate compression + events

9. Matrix rectangle problem?
   -> 2D prefix, maybe compress rows/cols
```

---

# Phase 1 — Prefix + Deque

Use when prefix sums are not enough because you need an inequality like:

```text
pref[j] - pref[i] >= K
```

You need the best old prefix quickly.

---

# 1. Shortest Subarray With Sum At Least K

## Problem Statement

Given an integer array `nums` and integer `k`, return the length of the shortest non-empty subarray with sum at least `k`.

Array may contain negative numbers.

## Input / Output

```text
Input : nums = [2, -1, 2], k = 3
Output: 3
```

```text
Input : nums = [84, -37, 32, 40, 95], k = 167
Output: 3
```

## Recognition Signal

```text
shortest subarray
sum >= k
negative numbers exist
sliding window fails
```

## Don't Memorize Thinking

With only positive numbers, sliding window works.

With negative numbers:

```text
window sum can decrease when we expand
window sum can increase when we shrink
```

So we use prefix:

```text
sum(i..j-1) = pref[j] - pref[i]
Need pref[j] - pref[i] >= k
```

For each current `j`, we want the **smallest index distance** with a valid old prefix.

Deque stores candidate prefix indices in increasing prefix value order.

Why increasing prefix value?

```text
Smaller pref[i] gives larger pref[j] - pref[i]
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int shortestSubarray(vector<int>& nums, int k) {
    int n = nums.size();
    vector<long long> pref(n + 1, 0);

    for (int i = 0; i < n; i++) {
        pref[i + 1] = pref[i] + nums[i];
    }

    deque<int> dq;
    int ans = n + 1;

    for (int j = 0; j <= n; j++) {
        while (!dq.empty() && pref[j] - pref[dq.front()] >= k) {
            ans = min(ans, j - dq.front());
            dq.pop_front();
        }

        while (!dq.empty() && pref[j] <= pref[dq.back()]) {
            dq.pop_back();
        }

        dq.push_back(j);
    }

    return ans == n + 1 ? -1 : ans;
}

int main() {
    vector<int> nums = {84, -37, 32, 40, 95};
    int k = 167;
    cout << shortestSubarray(nums, k) << "\n";
}
```

## Dry Run — Index by Index

```text
nums = [84, -37, 32, 40, 95], k = 167

index nums:   0    1    2    3    4
nums      :  [84, -37,  32,  40,  95]

pref index:  0    1    2    3    4    5
pref      : [0,  84,  47,  79, 119, 214]
```

```text
j = 0, pref[j] = 0
dq before : []
valid?    : none
cleanup   : none
push      : 0
dq after  : [0:0]
ans       : INF
```

```text
j = 1, pref[j] = 84
dq before : [0:0]
check front:
pref[1] - pref[0] = 84 - 0 = 84 < 167
not valid

cleanup:
pref[1] <= pref[0]? 84 <= 0 no

push      : 1
dq after  : [0:0, 1:84]
ans       : INF
```

```text
j = 2, pref[j] = 47
dq before : [0:0, 1:84]

check front:
47 - 0 = 47 < 167

cleanup from back:
pref[2] <= pref[1]? 47 <= 84 yes
pop 1 because prefix 84 is worse than 47

pref[2] <= pref[0]? 47 <= 0 no

push      : 2
dq after  : [0:0, 2:47]
ans       : INF
```

```text
j = 3, pref[j] = 79
dq before : [0:0, 2:47]

check front:
79 - 0 = 79 < 167

cleanup:
79 <= 47? no

push      : 3
dq after  : [0:0, 2:47, 3:79]
ans       : INF
```

```text
j = 4, pref[j] = 119
dq before : [0:0, 2:47, 3:79]

check front:
119 - 0 = 119 < 167

cleanup:
119 <= 79? no

push      : 4
dq after  : [0:0, 2:47, 3:79, 4:119]
ans       : INF
```

```text
j = 5, pref[j] = 214
dq before : [0:0, 2:47, 3:79, 4:119]

check front:
214 - 0 = 214 >= 167
candidate length = 5 - 0 = 5
ans = 5
pop front 0

check new front:
214 - 47 = 167 >= 167
candidate length = 5 - 2 = 3
ans = 3
pop front 2

check new front:
214 - 79 = 135 < 167
stop

cleanup:
214 <= 119? no

push      : 5
dq after  : [3:79, 4:119, 5:214]
ans       : 3

Final answer = 3
Subarray from index 2 to 4:
[32, 40, 95] = 167
```

## Pattern Takeaway

```text
Inequality with negative numbers
=> prefix + monotonic deque
```

---

# 2. Constrained Subsequence Sum

## Problem Statement

Given `nums` and integer `k`, return the maximum sum of a non-empty subsequence such that for every two adjacent chosen elements, their indices differ by at most `k`.

## Input / Output

```text
Input : nums = [10, 2, -10, 5, 20], k = 2
Output: 37
```

Best subsequence:

```text
10 + 2 + 5 + 20 = 37
```

## Recognition Signal

```text
maximum score ending at i
can connect from previous k positions
need max previous dp quickly
```

## Don't Memorize Thinking

This is prefix-like because each state depends on a window of previous states:

```text
dp[i] = nums[i] + max(0, max dp[j]) where i-k <= j < i
```

Deque keeps best `dp` values in decreasing order.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int constrainedSubsetSum(vector<int>& nums, int k) {
    int n = nums.size();
    vector<int> dp(n);
    deque<int> dq;
    int ans = nums[0];

    for (int i = 0; i < n; i++) {
        if (!dq.empty() && dq.front() < i - k) {
            dq.pop_front();
        }

        dp[i] = nums[i];
        if (!dq.empty()) {
            dp[i] += max(0, dp[dq.front()]);
        }

        ans = max(ans, dp[i]);

        while (!dq.empty() && dp[i] >= dp[dq.back()]) {
            dq.pop_back();
        }

        dq.push_back(i);
    }

    return ans;
}

int main() {
    vector<int> nums = {10, 2, -10, 5, 20};
    int k = 2;
    cout << constrainedSubsetSum(nums, k) << "\n";
}
```

## Dry Run — Index by Index

```text
nums = [10, 2, -10, 5, 20], k = 2
dp[i] = best valid subsequence sum ending at i
dq stores indices with decreasing dp value
```

```text
i = 0, nums[0] = 10
dq before : []
expired?  : none
dp[0]     : 10
ans       : 10
cleanup   : none
push      : 0
dq after  : [0:10]
```

```text
i = 1, nums[1] = 2
dq before : [0:10]
expired?  : 0 < 1-2? no
best prev : dp[0] = 10
dp[1]     : 2 + max(0,10) = 12
ans       : 12
cleanup   : dp[1]=12 >= dp[0]=10, pop 0
push      : 1
dq after  : [1:12]
```

```text
i = 2, nums[2] = -10
dq before : [1:12]
expired?  : 1 < 0? no
best prev : 12
dp[2]     : -10 + 12 = 2
ans       : 12
cleanup   : 2 >= 12? no
push      : 2
dq after  : [1:12, 2:2]
```

```text
i = 3, nums[3] = 5
dq before : [1:12, 2:2]
expired?  : 1 < 1? no
best prev : 12
dp[3]     : 5 + 12 = 17
ans       : 17
cleanup   : 17 >= 2 pop 2
cleanup   : 17 >= 12 pop 1
push      : 3
dq after  : [3:17]
```

```text
i = 4, nums[4] = 20
dq before : [3:17]
expired?  : 3 < 2? no
best prev : 17
dp[4]     : 20 + 17 = 37
ans       : 37
cleanup   : 37 >= 17 pop 3
push      : 4
dq after  : [4:37]

Final answer = 37
```

## Pattern Takeaway

```text
Need best previous value inside distance k
=> monotonic deque
```

---

# Phase 2 — Prefix XOR

XOR prefix is like sum prefix, but subtraction becomes XOR again.

```text
pxor[i] = nums[0] ^ nums[1] ^ ... ^ nums[i]
xor(l..r) = pxor[r] ^ pxor[l - 1]
```

Because:

```text
x ^ x = 0
x ^ 0 = x
```

---

# 3. Count Subarrays With XOR K

## Problem Statement

Given an array `nums` and integer `k`, count subarrays whose XOR equals `k`.

## Input / Output

```text
Input : nums = [4, 2, 2, 6, 4], k = 6
Output: 4
```

## Recognition Signal

```text
subarray XOR equals target
```

## Don't Memorize Thinking

For current prefix XOR `px`:

```text
oldPrefix ^ px = k
oldPrefix = px ^ k
```

So count how many previous prefix XOR values equal `px ^ k`.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int countSubarrayXor(vector<int>& nums, int k) {
    unordered_map<int, int> freq;
    freq[0] = 1;

    int px = 0;
    int ans = 0;

    for (int x : nums) {
        px ^= x;
        ans += freq[px ^ k];
        freq[px]++;
    }

    return ans;
}

int main() {
    vector<int> nums = {4, 2, 2, 6, 4};
    int k = 6;
    cout << countSubarrayXor(nums, k) << "\n";
}
```

## Dry Run — Index by Index

```text
nums = [4, 2, 2, 6, 4], k = 6
freq = {0:1}
px = 0, ans = 0
```

```text
i = 0, x = 4
px after : 0 ^ 4 = 4
need     : px ^ k = 4 ^ 6 = 2
freq[2]  : 0
ans      : 0
freq     : {0:1, 4:1}
```

```text
i = 1, x = 2
px after : 4 ^ 2 = 6
need     : 6 ^ 6 = 0
freq[0]  : 1
ans      : 1
freq[6]++ -> {0:1, 4:1, 6:1}

Valid:
[4,2] xor = 6
```

```text
i = 2, x = 2
px after : 6 ^ 2 = 4
need     : 4 ^ 6 = 2
freq[2]  : 0
ans      : 1
freq[4]  : 2
```

```text
i = 3, x = 6
px after : 4 ^ 6 = 2
need     : 2 ^ 6 = 4
freq[4]  : 2
ans      : 1 + 2 = 3
freq[2]  : 1

Two valid subarrays end here:
[2,2,6] and [6]
```

```text
i = 4, x = 4
px after : 2 ^ 4 = 6
need     : 6 ^ 6 = 0
freq[0]  : 1
ans      : 4
freq[6]  : 2

Final answer = 4
```

## Pattern Takeaway

```text
Subarray XOR target
=> prefix XOR + hashmap
```

---

# 4. Maximum XOR of Two Numbers Using Prefix Trie

## Problem Statement

Given an array, find the maximum XOR of any two numbers.

## Input / Output

```text
Input : nums = [3, 10, 5, 25, 2, 8]
Output: 28
```

Because:

```text
5 ^ 25 = 28
```

## Recognition Signal

```text
maximum XOR
want opposite bits greedily
```

## Don't Memorize Thinking

For XOR, to maximize a bit:

```text
current bit 0 wants previous bit 1
current bit 1 wants previous bit 0
```

Trie stores binary representation of previous numbers.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Node {
    Node* child[2];
    Node() {
        child[0] = child[1] = nullptr;
    }
};

class Trie {
public:
    Node* root = new Node();

    void insert(int x) {
        Node* cur = root;
        for (int b = 31; b >= 0; b--) {
            int bit = (x >> b) & 1;
            if (!cur->child[bit]) cur->child[bit] = new Node();
            cur = cur->child[bit];
        }
    }

    int bestXor(int x) {
        Node* cur = root;
        int ans = 0;

        for (int b = 31; b >= 0; b--) {
            int bit = (x >> b) & 1;
            int want = 1 - bit;

            if (cur->child[want]) {
                ans |= (1 << b);
                cur = cur->child[want];
            } else {
                cur = cur->child[bit];
            }
        }

        return ans;
    }
};

int findMaximumXOR(vector<int>& nums) {
    Trie trie;
    trie.insert(nums[0]);

    int ans = 0;

    for (int i = 1; i < nums.size(); i++) {
        ans = max(ans, trie.bestXor(nums[i]));
        trie.insert(nums[i]);
    }

    return ans;
}

int main() {
    vector<int> nums = {3, 10, 5, 25, 2, 8};
    cout << findMaximumXOR(nums) << "\n";
}
```

## Dry Run — Short 5-bit View

Use 5-bit binary:

```text
3  = 00011
10 = 01010
5  = 00101
25 = 11001
2  = 00010
8  = 01000
```

```text
Start:
insert 3 = 00011
ans = 0
```

```text
i = 1, x = 10 = 01010
Trie has: 3 = 00011

10 ^ 3 = 01010 ^ 00011 = 01001 = 9
best = 9
ans = 9
insert 10
```

```text
i = 2, x = 5 = 00101
Trie has: 3, 10

5 ^ 3  = 00101 ^ 00011 = 00110 = 6
5 ^ 10 = 00101 ^ 01010 = 01111 = 15
best = 15
ans = 15
insert 5
```

```text
i = 3, x = 25 = 11001
Trie has: 3, 10, 5

25 ^ 3  = 11001 ^ 00011 = 11010 = 26
25 ^ 10 = 11001 ^ 01010 = 10011 = 19
25 ^ 5  = 11001 ^ 00101 = 11100 = 28

best = 28
ans = 28
insert 25
```

```text
i = 4, x = 2
best with previous numbers <= 27
ans remains 28

i = 5, x = 8
best with previous numbers <= 17
ans remains 28

Final answer = 28
```

## Pattern Takeaway

```text
Maximum XOR
=> try opposite bits from high to low
=> binary trie
```

---

# Phase 3 — Prefix Bitmask / Parity

Use when only odd/even counts matter.

Each character toggles one bit.

```text
mask ^= (1 << characterIndex)
```

Same mask means all character counts between are even.

Masks differing by one bit means exactly one odd character.

---

# 5. Number of Wonderful Substrings

## Problem Statement

A wonderful substring has at most one character with odd frequency.

Given a string containing only letters `a` to `j`, return the number of wonderful substrings.

## Input / Output

```text
Input : word = "aba"
Output: 4
```

Wonderful substrings:

```text
"a", "b", "a", "aba"
```

## Recognition Signal

```text
at most one odd frequency
characters limited to 10
substring count
```

## Don't Memorize Thinking

Odd/even frequency is parity.

For current mask:

```text
same old mask => all even between
old mask differing by one bit => exactly one odd between
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long wonderfulSubstrings(string word) {
    vector<long long> freq(1 << 10, 0);
    freq[0] = 1;

    int mask = 0;
    long long ans = 0;

    for (char c : word) {
        int bit = c - 'a';
        mask ^= (1 << bit);

        ans += freq[mask];

        for (int b = 0; b < 10; b++) {
            ans += freq[mask ^ (1 << b)];
        }

        freq[mask]++;
    }

    return ans;
}

int main() {
    string word = "aba";
    cout << wonderfulSubstrings(word) << "\n";
}
```

## Dry Run — Index by Index

```text
word = "aba"
bits:
a = bit 0
b = bit 1

freq[0] = 1
mask = 0, ans = 0
```

```text
i = 0, c = 'a'
mask before: 00
toggle a   : 00 ^ 01 = 01

same mask count:
freq[01] = 0

one-bit difference:
mask ^ 01 = 00 -> freq[00] = 1
mask ^ 10 = 11 -> freq[11] = 0
other bits = 0

ans = 1
freq[01]++

Valid substring:
"a"
```

```text
i = 1, c = 'b'
mask before: 01
toggle b   : 01 ^ 10 = 11

same mask:
freq[11] = 0

one-bit difference:
11 ^ 01 = 10 -> 0
11 ^ 10 = 01 -> freq[01] = 1

ans = 2
freq[11]++

Valid substring:
"b"
```

```text
i = 2, c = 'a'
mask before: 11
toggle a   : 11 ^ 01 = 10

same mask:
freq[10] = 0

one-bit difference:
10 ^ 01 = 11 -> freq[11] = 1
10 ^ 10 = 00 -> freq[00] = 1

ans += 2
ans = 4
freq[10]++

New valid substrings:
"a"
"aba"

Final answer = 4
```

## Pattern Takeaway

```text
At most one odd count
=> parity bitmask + same/different-by-one-bit lookup
```

---

# 6. Longest Awesome Substring

## Problem Statement

Given a string of digits, return the length of the longest substring that can be rearranged into a palindrome.

## Input / Output

```text
Input : s = "3242415"
Output: 5
```

Substring:

```text
"24241"
```

## Recognition Signal

```text
can rearrange into palindrome
at most one odd digit count
longest substring
```

## Don't Memorize Thinking

Palindrome rearrangement condition:

```text
at most one character has odd count
```

Same bitmask idea.

For longest length, store earliest index of each mask.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestAwesome(string s) {
    const int INF = 1e9;
    vector<int> first(1 << 10, INF);
    first[0] = -1;

    int mask = 0;
    int ans = 0;

    for (int i = 0; i < s.size(); i++) {
        int bit = s[i] - '0';
        mask ^= (1 << bit);

        if (first[mask] != INF) {
            ans = max(ans, i - first[mask]);
        }

        for (int b = 0; b < 10; b++) {
            int candidate = mask ^ (1 << b);
            if (first[candidate] != INF) {
                ans = max(ans, i - first[candidate]);
            }
        }

        if (first[mask] == INF) {
            first[mask] = i;
        }
    }

    return ans;
}

int main() {
    string s = "3242415";
    cout << longestAwesome(s) << "\n";
}
```

## Dry Run — Key Steps

```text
s = "3242415"
Need longest substring with at most one odd digit count.
first[0] = -1
mask = 0
```

```text
i = 0, digit = 3
mask toggles bit 3
mask = 0000001000

same mask seen? no
one-bit diff:
mask ^ bit3 = 0 seen at -1
length = 0 - (-1) = 1
ans = 1

store first[mask] = 0
```

```text
i = 1, digit = 2
mask has odd {3,2}

same mask seen? no
one-bit diff may find old masks:
remove bit2 -> mask with only {3}, seen at 0
length = 1 - 0 = 1
ans = 1

store current mask at 1
```

```text
i = 2, digit = 4
mask has odd {3,2,4}
No useful longer match.
store current mask.
ans = 1
```

```text
i = 3, digit = 2
previous odd {3,2,4}
toggle 2 => odd {3,4}

Check one-bit diff:
remove bit4 => odd {3}
mask with only {3} seen at index 0
length = 3 - 0 = 3

substring s[1..3] = "242"
can form palindrome
ans = 3
```

```text
i = 4, digit = 4
odd {3,4}
toggle 4 => odd {3}

same mask odd {3} seen at index 0
length = 4 - 0 = 4

one-bit diff:
toggle bit3 => 0 seen at -1
length = 4 - (-1) = 5

substring s[0..4] = "32424"
has only digit 3 odd
can rearrange into palindrome
ans = 5
```

```text
i = 5, digit = 1
No longer than 5.

i = 6, digit = 5
No longer than 5.

Final answer = 5
```

## Pattern Takeaway

```text
Longest palindrome-rearrange substring
=> parity mask + earliest index
```

---

# Phase 4 — Ordered Prefix / Merge Sort

Use when you need count of prefix differences in a numeric range:

```text
lower <= pref[j] - pref[i] <= upper
```

HashMap is not enough because this is a range, not exact equality.

---

# 7. Count of Range Sum

## Problem Statement

Given integer array `nums`, return number of range sums lying in `[lower, upper]`.

## Input / Output

```text
Input : nums = [-2, 5, -1], lower = -2, upper = 2
Output: 3
```

Valid ranges:

```text
[0..0] = -2
[2..2] = -1
[0..2] = 2
```

## Recognition Signal

```text
count subarray sums in range [lower, upper]
negative numbers
```

## Don't Memorize Thinking

Subarray sum:

```text
pref[j] - pref[i]
```

Need:

```text
lower <= pref[j] - pref[i] <= upper
```

Rearrange for old prefix:

```text
pref[j] - upper <= pref[i] <= pref[j] - lower
```

So for each current prefix, count old prefixes inside a range.

Can solve with:
- merge sort counting
- Fenwick + coordinate compression
- ordered multiset in some languages

## C++ Code — Merge Sort Counting

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    long long lower, upper;

    long long mergeCount(vector<long long>& pref, int l, int r) {
        if (r - l <= 1) return 0;

        int mid = (l + r) / 2;
        long long ans = mergeCount(pref, l, mid) + mergeCount(pref, mid, r);

        int lo = mid, hi = mid;

        for (int i = l; i < mid; i++) {
            while (lo < r && pref[lo] - pref[i] < lower) lo++;
            while (hi < r && pref[hi] - pref[i] <= upper) hi++;
            ans += hi - lo;
        }

        inplace_merge(pref.begin() + l, pref.begin() + mid, pref.begin() + r);
        return ans;
    }

    int countRangeSum(vector<int>& nums, int low, int up) {
        lower = low;
        upper = up;

        vector<long long> pref(nums.size() + 1, 0);
        for (int i = 0; i < nums.size(); i++) {
            pref[i + 1] = pref[i] + nums[i];
        }

        return (int)mergeCount(pref, 0, pref.size());
    }
};

int main() {
    vector<int> nums = {-2, 5, -1};
    Solution sol;
    cout << sol.countRangeSum(nums, -2, 2) << "\n";
}
```

## Dry Run — Prefix Values

```text
nums = [-2, 5, -1]
lower = -2, upper = 2

pref:
index:  0   1   2   3
pref : [0, -2,  3,  2]
```

Subarray sum = later prefix - earlier prefix.

```text
All pairs i < j:

i=0, j=1:
pref[1] - pref[0] = -2 - 0 = -2 valid

i=0, j=2:
3 - 0 = 3 invalid

i=0, j=3:
2 - 0 = 2 valid

i=1, j=2:
3 - (-2) = 5 invalid

i=1, j=3:
2 - (-2) = 4 invalid

i=2, j=3:
2 - 3 = -1 valid

Answer = 3
```

## Merge Sort Counting Intuition

During merge step:

```text
left half contains earlier prefix candidates
right half contains later prefix candidates
```

For each left prefix `pref[i]`, find right prefixes satisfying:

```text
lower <= pref[j] - pref[i] <= upper
```

Because right half is sorted, use two pointers.

## Pattern Takeaway

```text
Count subarray sums inside numeric range
=> prefix + sorted counting
=> merge sort / Fenwick compression
```

---

# 8. Reverse Pairs Style Prefix Counting

## Problem Statement

Count pairs `(i, j)` such that:

```text
i < j and nums[i] > 2 * nums[j]
```

This is not pure prefix sum, but the counting technique is the same as ordered prefix counting.

## Input / Output

```text
Input : nums = [1, 3, 2, 3, 1]
Output: 2
```

## Recognition Signal

```text
count pairs with inequality
need sorted previous/future values
```

## Don't Memorize Thinking

When exact hashmap fails because condition is inequality:

```text
value1 > 2 * value2
```

Use sorted merge counting.

This skill transfers to prefix range counting.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int mergeCount(vector<int>& a, int l, int r) {
        if (r - l <= 1) return 0;

        int mid = (l + r) / 2;
        int ans = mergeCount(a, l, mid) + mergeCount(a, mid, r);

        int j = mid;
        for (int i = l; i < mid; i++) {
            while (j < r && (long long)a[i] > 2LL * a[j]) {
                j++;
            }
            ans += j - mid;
        }

        inplace_merge(a.begin() + l, a.begin() + mid, a.begin() + r);
        return ans;
    }

    int reversePairs(vector<int>& nums) {
        return mergeCount(nums, 0, nums.size());
    }
};

int main() {
    vector<int> nums = {1, 3, 2, 3, 1};
    Solution sol;
    cout << sol.reversePairs(nums) << "\n";
}
```

## Dry Run — Pair Check

```text
nums = [1, 3, 2, 3, 1]

Check pairs:
i=0, nums[i]=1:
  1 > 2*3? no
  1 > 2*2? no
  1 > 2*3? no
  1 > 2*1? no

i=1, nums[i]=3:
  3 > 2*2? no
  3 > 2*3? no
  3 > 2*1? yes -> pair (1,4)

i=2, nums[i]=2:
  2 > 2*3? no
  2 > 2*1? no

i=3, nums[i]=3:
  3 > 2*1? yes -> pair (3,4)

Answer = 2
```

## Pattern Takeaway

```text
Inequality pair counting
=> sorted merge counting
```

---

# Phase 5 — Coordinate Compression + Difference

Use when array index range is huge.

```text
coordinate up to 1e9
number of events only 2e5
```

Do not create array of size 1e9. Compress only important points.

---

# 9. Range Addition With Huge Coordinates

## Problem Statement

You are given range updates:

```text
[l, r, x]
```

Coordinates can be up to `1e9`.

Find the maximum value at any coordinate after all updates.

## Input / Output

```text
updates = [
  [10, 20, 5],
  [15, 25, 7],
  [30, 40, 3]
]

Output: 12
```

Because ranges `[10,20]` and `[15,25]` overlap from `15..20`.

## Recognition Signal

```text
huge coordinate range
range add
need max overlap/value
```

## Don't Memorize Thinking

Difference array idea:

```text
diff[l] += x
diff[r + 1] -= x
```

But `r + 1` may be huge. Use map events:

```text
events[l] += x
events[r + 1] -= x
```

Then scan sorted coordinates.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long maxAfterHugeRangeUpdates(vector<array<long long,3>>& updates) {
    map<long long, long long> events;

    for (auto &u : updates) {
        long long l = u[0], r = u[1], x = u[2];
        events[l] += x;
        events[r + 1] -= x;
    }

    long long cur = 0;
    long long best = LLONG_MIN;

    for (auto &[coord, delta] : events) {
        cur += delta;
        best = max(best, cur);
    }

    return best;
}

int main() {
    vector<array<long long,3>> updates = {
        {10, 20, 5},
        {15, 25, 7},
        {30, 40, 3}
    };

    cout << maxAfterHugeRangeUpdates(updates) << "\n";
}
```

## Dry Run — Event by Event

```text
updates:
[10,20,+5]
[15,25,+7]
[30,40,+3]
```

```text
Create events:

[10,20,+5]:
events[10] += 5
events[21] -= 5

[15,25,+7]:
events[15] += 7
events[26] -= 7

[30,40,+3]:
events[30] += 3
events[41] -= 3
```

```text
Sorted events:
coord:  10   15   21   26   30   41
delta: +5   +7   -5   -7   +3   -3
```

```text
Scan:

coord = 10
cur = 0 + 5 = 5
best = 5

coord = 15
cur = 5 + 7 = 12
best = 12

coord = 21
cur = 12 - 5 = 7
best = 12

coord = 26
cur = 7 - 7 = 0
best = 12

coord = 30
cur = 0 + 3 = 3
best = 12

coord = 41
cur = 3 - 3 = 0
best = 12

Final answer = 12
```

## Pattern Takeaway

```text
Huge coordinates + range add
=> map events / coordinate compression
```

---

# 10. Maximum Population Year / Event Sweep

## Problem Statement

Given birth and death years:

```text
[birth, death]
```

Person is alive from `birth` to `death - 1`.

Find earliest year with maximum population.

## Input / Output

```text
logs = [[1993,1999], [2000,2010]]
Output: 1993
```

```text
logs = [[1950,1961], [1960,1971], [1970,1981]]
Output: 1960
```

## Recognition Signal

```text
active intervals
earliest max overlap
start/end events
```

## Don't Memorize Thinking

Population changes only at birth/death years.

```text
birth: +1
death: -1
```

Scan years in order.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maximumPopulation(vector<vector<int>>& logs) {
    map<int, int> events;

    for (auto &log : logs) {
        int birth = log[0];
        int death = log[1];

        events[birth]++;
        events[death]--;
    }

    int cur = 0;
    int best = 0;
    int answerYear = 0;

    for (auto &[year, delta] : events) {
        cur += delta;
        if (cur > best) {
            best = cur;
            answerYear = year;
        }
    }

    return answerYear;
}

int main() {
    vector<vector<int>> logs = {{1950,1961}, {1960,1971}, {1970,1981}};
    cout << maximumPopulation(logs) << "\n";
}
```

## Dry Run — Event Sweep

```text
logs:
[1950,1961]
[1960,1971]
[1970,1981]
```

```text
events:
1950: +1
1961: -1
1960: +1
1971: -1
1970: +1
1981: -1
```

Sorted:

```text
year : 1950 1960 1961 1970 1971 1981
delta:  +1   +1   -1   +1   -1   -1
```

```text
year = 1950
cur = 1
best = 1
answer = 1950

year = 1960
cur = 2
best = 2
answer = 1960

year = 1961
cur = 1
best remains 2

year = 1970
cur = 2
cur == best, but earliest year already 1960
answer remains 1960

year = 1971
cur = 1

year = 1981
cur = 0

Final answer = 1960
```

## Pattern Takeaway

```text
Intervals active over time
=> sweep line difference events
```

---

# Phase 6 — Prefix + Binary Search

Use when prefix is sorted/monotonic.

Usually requires positive numbers.

---

# 11. Minimum Size Subarray Sum

## Problem Statement

Given positive integers `nums` and integer `target`, return minimal length of a subarray whose sum is at least `target`.

## Input / Output

```text
Input : target = 7, nums = [2,3,1,2,4,3]
Output: 2
```

Subarray:

```text
[4,3]
```

## Recognition Signal

```text
positive numbers
sum >= target
minimum length
```

## Don't Memorize Thinking

Since all numbers are positive, prefix sum is increasing.

For each start `i`, need smallest `j` such that:

```text
pref[j] - pref[i] >= target
pref[j] >= pref[i] + target
```

Use `lower_bound`.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int minSubArrayLen(int target, vector<int>& nums) {
    int n = nums.size();

    vector<long long> pref(n + 1, 0);
    for (int i = 0; i < n; i++) {
        pref[i + 1] = pref[i] + nums[i];
    }

    int ans = n + 1;

    for (int i = 0; i < n; i++) {
        long long need = pref[i] + target;
        auto it = lower_bound(pref.begin(), pref.end(), need);

        if (it != pref.end()) {
            int j = it - pref.begin();
            ans = min(ans, j - i);
        }
    }

    return ans == n + 1 ? 0 : ans;
}

int main() {
    vector<int> nums = {2,3,1,2,4,3};
    int target = 7;
    cout << minSubArrayLen(target, nums) << "\n";
}
```

## Dry Run — Prefix + lower_bound

```text
nums = [2,3,1,2,4,3], target = 7

pref index:  0  1  2  3  4   5   6
pref      : [0, 2, 5, 6, 8, 12, 15]
```

```text
i = 0
pref[i] = 0
need = 0 + 7 = 7
lower_bound(pref, 7) -> index 4, value 8
length = 4 - 0 = 4
subarray [0..3] = [2,3,1,2], sum 8
ans = 4
```

```text
i = 1
pref[i] = 2
need = 2 + 7 = 9
lower_bound(pref, 9) -> index 5, value 12
length = 5 - 1 = 4
ans = 4
```

```text
i = 2
pref[i] = 5
need = 5 + 7 = 12
lower_bound(pref, 12) -> index 5, value 12
length = 5 - 2 = 3
subarray [2..4] = [1,2,4]
ans = 3
```

```text
i = 3
pref[i] = 6
need = 13
lower_bound -> index 6, value 15
length = 6 - 3 = 3
ans = 3
```

```text
i = 4
pref[i] = 8
need = 15
lower_bound -> index 6, value 15
length = 6 - 4 = 2
subarray [4..5] = [4,3]
ans = 2
```

```text
i = 5
pref[i] = 12
need = 19
not found

Final answer = 2
```

## Pattern Takeaway

```text
Positive nums + sum at least target
=> increasing prefix + lower_bound
```

---

# 12. Maximum Length Subarray With Sum At Most K

## Problem Statement

Given positive integers `nums` and integer `k`, return maximum length of a subarray whose sum is at most `k`.

## Input / Output

```text
Input : nums = [1,2,1,0,1,1,0], k = 4
Output: 5
```

Subarray:

```text
[1,0,1,1,0]
```

## Recognition Signal

```text
positive/non-negative numbers
sum <= k
maximum length
```

## Don't Memorize Thinking

Use prefix increasing.

For each right end `r`, we need earliest `l` such that:

```text
pref[r+1] - pref[l] <= k
pref[l] >= pref[r+1] - k
```

Since prefix is sorted, use lower_bound to find smallest valid `l`.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxLenAtMostK(vector<int>& nums, int k) {
    int n = nums.size();

    vector<long long> pref(n + 1, 0);
    for (int i = 0; i < n; i++) {
        pref[i + 1] = pref[i] + nums[i];
    }

    int ans = 0;

    for (int r = 0; r < n; r++) {
        long long need = pref[r + 1] - k;
        auto it = lower_bound(pref.begin(), pref.begin() + r + 2, need);

        int l = it - pref.begin();
        ans = max(ans, r - l + 1);
    }

    return ans;
}

int main() {
    vector<int> nums = {1,2,1,0,1,1,0};
    int k = 4;
    cout << maxLenAtMostK(nums, k) << "\n";
}
```

## Dry Run — Index by Index

```text
nums = [1,2,1,0,1,1,0], k = 4

pref index: 0  1  2  3  4  5  6  7
pref      :[0, 1, 3, 4, 4, 5, 6, 6]
```

```text
r = 0, nums[0]=1
pref[r+1] = pref[1] = 1
need = 1 - 4 = -3
lower_bound(-3) -> l = 0
length = 0 - 0 + 1 = 1
ans = 1
```

```text
r = 1, nums[1]=2
pref[2] = 3
need = -1
lower_bound(-1) -> l = 0
length = 2
ans = 2
```

```text
r = 2, nums[2]=1
pref[3] = 4
need = 0
lower_bound(0) -> l = 0
length = 3
ans = 3
```

```text
r = 3, nums[3]=0
pref[4] = 4
need = 0
lower_bound(0) -> l = 0
length = 4
ans = 4
```

```text
r = 4, nums[4]=1
pref[5] = 5
need = 1
lower_bound(1) -> l = 1
length = 4 - 1 + 1 = 4
ans = 4
```

```text
r = 5, nums[5]=1
pref[6] = 6
need = 2
lower_bound(2) -> l = 2
length = 5 - 2 + 1 = 4
ans = 4
```

```text
r = 6, nums[6]=0
pref[7] = 6
need = 2
lower_bound(2) -> l = 2
length = 6 - 2 + 1 = 5
ans = 5

Subarray [2..6] = [1,0,1,1,0], sum = 3
Final answer = 5
```

## Pattern Takeaway

```text
Positive/non-negative nums + inequality
=> monotonic prefix + binary search
```

---

# Phase 7 — 2D Advanced Prefix

2D advanced problems often reduce matrix rows into 1D arrays.

Trick:

```text
fix top row and bottom row
compress columns into column sums
then solve 1D prefix problem
```

---

# 13. Number of Submatrices That Sum to Target

## Problem Statement

Given a matrix and target, return number of non-empty submatrices that sum to target.

## Input / Output

```text
matrix = [
  [0,1,0],
  [1,1,1],
  [0,1,0]
]
target = 0

Output: 4
```

## Recognition Signal

```text
count submatrices with exact sum
2D exact target
```

## Don't Memorize Thinking

A submatrix is defined by:

```text
top row, bottom row, left column, right column
```

Fix top and bottom rows.

Then each column becomes sum between those rows.

Now problem becomes:

```text
count subarrays in compressed column array with sum target
```

Use prefix + hashmap.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int numSubmatrixSumTarget(vector<vector<int>>& matrix, int target) {
    int n = matrix.size();
    int m = matrix[0].size();

    int ans = 0;

    for (int top = 0; top < n; top++) {
        vector<int> colSum(m, 0);

        for (int bottom = top; bottom < n; bottom++) {
            for (int c = 0; c < m; c++) {
                colSum[c] += matrix[bottom][c];
            }

            unordered_map<int, int> freq;
            freq[0] = 1;

            int pref = 0;
            for (int x : colSum) {
                pref += x;
                ans += freq[pref - target];
                freq[pref]++;
            }
        }
    }

    return ans;
}

int main() {
    vector<vector<int>> matrix = {
        {0,1,0},
        {1,1,1},
        {0,1,0}
    };
    int target = 0;

    cout << numSubmatrixSumTarget(matrix, target) << "\n";
}
```

## Dry Run — Row Compression

```text
matrix:
row\col  0  1  2
0       [0, 1, 0]
1       [1, 1, 1]
2       [0, 1, 0]

target = 0
```

```text
top = 0
colSum = [0,0,0]
```

```text
bottom = 0
Add row 0:
colSum = [0,1,0]

Now count subarrays with sum 0 in [0,1,0]

freq = {0:1}
pref = 0

c = 0, x = 0
pref = 0
need = pref - target = 0
freq[0] = 1
ans += 1
freq[0] = 2

Valid submatrix:
row 0, col 0 only
```

```text
c = 1, x = 1
pref = 1
need = 1
freq[1] = 0
freq[1] = 1

c = 2, x = 0
pref = 1
need = 1
freq[1] = 1
ans += 1

Valid submatrix:
row 0, col 2 only
```

```text
bottom = 1
Add row 1:
previous colSum = [0,1,0]
row 1           = [1,1,1]
new colSum      = [1,2,1]

Count subarrays sum 0:
none
```

```text
bottom = 2
Add row 2:
previous colSum = [1,2,1]
row 2           = [0,1,0]
new colSum      = [1,3,1]

Count subarrays sum 0:
none
```

```text
top = 1
reset colSum = [0,0,0]

bottom = 1:
colSum = [1,1,1]
no zero-sum subarray

bottom = 2:
colSum = [1,2,1]
no zero-sum subarray
```

```text
top = 2
reset colSum = [0,0,0]

bottom = 2:
colSum = [0,1,0]

Same as row 0:
two zero single-cell submatrices:
(row 2, col 0)
(row 2, col 2)

Total answer = 4
```

## Pattern Takeaway

```text
2D exact target count
=> fix row pair
=> compress columns
=> 1D prefix hashmap
```

---

# 14. Max Sum Rectangle No Larger Than K

## Problem Statement

Given a matrix and integer `k`, find the maximum sum of a rectangle no larger than `k`.

## Input / Output

```text
matrix = [[1,0,1],
          [0,-2,3]]
k = 2

Output: 2
```

## Recognition Signal

```text
max rectangle sum <= k
negative numbers
2D + ordered prefix
```

## Don't Memorize Thinking

Same row compression.

For 1D compressed array, need maximum subarray sum <= k.

For each prefix `pref`:

```text
subarray sum = pref - old
want pref - old <= k
=> old >= pref - k
```

So find smallest old prefix >= `pref - k`.

Use `set.lower_bound`.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxSumSubmatrix(vector<vector<int>>& matrix, int k) {
    int n = matrix.size();
    int m = matrix[0].size();

    int ans = INT_MIN;

    for (int top = 0; top < n; top++) {
        vector<int> colSum(m, 0);

        for (int bottom = top; bottom < n; bottom++) {
            for (int c = 0; c < m; c++) {
                colSum[c] += matrix[bottom][c];
            }

            set<int> seen;
            seen.insert(0);

            int pref = 0;

            for (int x : colSum) {
                pref += x;

                auto it = seen.lower_bound(pref - k);
                if (it != seen.end()) {
                    ans = max(ans, pref - *it);
                }

                seen.insert(pref);
            }
        }
    }

    return ans;
}

int main() {
    vector<vector<int>> matrix = {
        {1,0,1},
        {0,-2,3}
    };
    int k = 2;

    cout << maxSumSubmatrix(matrix, k) << "\n";
}
```

## Dry Run — Row Compression + Ordered Prefix

```text
matrix:
row\col  0   1   2
0       [1,  0,  1]
1       [0, -2,  3]

k = 2
```

```text
top = 0
colSum = [0,0,0]
```

```text
bottom = 0
Add row 0:
colSum = [1,0,1]

Now solve max subarray sum <= 2.

seen = {0}
pref = 0
ans = -INF
```

```text
c = 0, x = 1
pref = 1
need old >= pref - k = 1 - 2 = -1
lower_bound(-1) in {0} = 0
candidate = pref - old = 1 - 0 = 1
ans = 1
insert pref 1
seen = {0,1}
```

```text
c = 1, x = 0
pref = 1
need = -1
lower_bound(-1) = 0
candidate = 1
ans = 1
insert 1
```

```text
c = 2, x = 1
pref = 2
need = 0
lower_bound(0) = 0
candidate = 2
ans = 2
insert 2

Rectangle row 0, cols 0..2 has sum 2
```

```text
bottom = 1
Add row 1:
previous colSum = [1,0,1]
row 1           = [0,-2,3]
new colSum      = [1,-2,4]

Solve max subarray <= 2:

seen={0}, pref=0

c=0, x=1:
pref=1, need=-1, old=0, candidate=1, ans=2

c=1, x=-2:
pref=-1, need=-3, old=0, candidate=-1, ans=2

c=2, x=4:
pref=3, need=1, lower_bound(1) = 1
candidate=3-1=2, ans=2
```

```text
top = 1
bottom = 1
colSum = [0,-2,3]

Best <= 2 is 1 or 0 or -2.
ans remains 2.

Final answer = 2
```

## Pattern Takeaway

```text
2D max rectangle <= k
=> row compression
=> 1D ordered prefix lower_bound
```

---

# Pattern Recognition Table

| Signal | Think |
|---|---|
| exact subarray sum | prefix + hashmap |
| divisible by k | prefix modulo |
| xor equals k | prefix xor + hashmap |
| odd/even counts | prefix bitmask |
| at most one odd | same mask or one-bit-different mask |
| shortest sum >= k with negatives | prefix + monotonic deque |
| max previous inside k distance | monotonic deque |
| count range sum between lower/upper | prefix + merge sort / Fenwick |
| huge coordinate range updates | map events / coordinate compression |
| active intervals | sweep line |
| positive nums + inequality | prefix + binary search / sliding window |
| 2D exact target | row compression + 1D prefix hashmap |
| 2D max <= k | row compression + ordered prefix |

---

# Contest Templates

## Prefix XOR Count

```cpp
int countXor(vector<int>& a, int k) {
    unordered_map<int,int> freq;
    freq[0] = 1;
    int px = 0, ans = 0;

    for (int x : a) {
        px ^= x;
        ans += freq[px ^ k];
        freq[px]++;
    }
    return ans;
}
```

## Prefix Bitmask Wonderful Count

```cpp
long long countAtMostOneOdd(string s) {
    vector<long long> freq(1 << 10, 0);
    freq[0] = 1;

    int mask = 0;
    long long ans = 0;

    for (char c : s) {
        mask ^= 1 << (c - 'a');
        ans += freq[mask];

        for (int b = 0; b < 10; b++) {
            ans += freq[mask ^ (1 << b)];
        }

        freq[mask]++;
    }

    return ans;
}
```

## Shortest Subarray At Least K With Negatives

```cpp
int shortestSubarray(vector<int>& nums, int k) {
    int n = nums.size();
    vector<long long> pref(n + 1);

    for (int i = 0; i < n; i++) {
        pref[i + 1] = pref[i] + nums[i];
    }

    deque<int> dq;
    int ans = n + 1;

    for (int i = 0; i <= n; i++) {
        while (!dq.empty() && pref[i] - pref[dq.front()] >= k) {
            ans = min(ans, i - dq.front());
            dq.pop_front();
        }

        while (!dq.empty() && pref[i] <= pref[dq.back()]) {
            dq.pop_back();
        }

        dq.push_back(i);
    }

    return ans == n + 1 ? -1 : ans;
}
```

## Sweep Line Events

```cpp
long long maxOverlap(vector<array<long long,3>>& updates) {
    map<long long,long long> events;

    for (auto [l, r, x] : updates) {
        events[l] += x;
        events[r + 1] -= x;
    }

    long long cur = 0, best = LLONG_MIN;

    for (auto [coord, delta] : events) {
        cur += delta;
        best = max(best, cur);
    }

    return best;
}
```

## 2D Row Compression + Prefix HashMap

```cpp
int countSubmatrixTarget(vector<vector<int>>& matrix, int target) {
    int n = matrix.size(), m = matrix[0].size();
    int ans = 0;

    for (int top = 0; top < n; top++) {
        vector<int> colSum(m, 0);

        for (int bottom = top; bottom < n; bottom++) {
            for (int c = 0; c < m; c++) {
                colSum[c] += matrix[bottom][c];
            }

            unordered_map<int,int> freq;
            freq[0] = 1;

            int pref = 0;
            for (int x : colSum) {
                pref += x;
                ans += freq[pref - target];
                freq[pref]++;
            }
        }
    }

    return ans;
}
```

---

# Final Mental Model

```text
Prefix sum is not one formula.

It is a way to convert a range problem into:

current state - old state
current state ^ old state
current mask compared with old mask
current prefix searched in old ordered prefixes
current event delta accumulated over time
```

Every advanced problem asks:

"What old state do I need?"
