# PREFIX SUM PATTERNS

## Pattern 4 — Prefix Modulo / Divisibility

---

## Table of Contents

- [Pattern Overview](#pattern-overview)
- [Core Formula and Derivation](#core-formula-and-derivation)
- [Generic Dry Run](#generic-dry-run)
- [Problem 1 — Subarray Sums Divisible by K](#problem-1--subarray-sums-divisible-by-k)
- [Problem 2 — Continuous Subarray Sum](#problem-2--continuous-subarray-sum)
- [Problem 3 — Make Sum Divisible by P](#problem-3--make-sum-divisible-by-p)
- [Problem 4 — Subarray Divisibility](#problem-4--subarray-divisibility)
- [Fast Revision Model](#fast-revision-model)

---

## Pattern Overview

### What kind of problem is this?

Use this pattern when a contiguous subarray condition involves **divisibility or remainder**.

```text
SUBARRAY
   +
SUM % K condition
   ↓
PREFIX MODULO
```

The important idea is not the full prefix sum. It is its remainder:

```text
rem = prefix % K
```

### Core Idea

For subarray `[l..r]`:

```text
sum(l,r)
= pref[r] - pref[l-1]
```

If the subarray must be divisible by `K`:

```text
(pref[r] - pref[l-1]) % K = 0
```

Therefore:

```text
pref[r] % K
=
pref[l-1] % K
```

So:

```text
SAME PREFIX REMAINDER
        ↓
DIFFERENCE IS DIVISIBLE BY K
```

### Recognition Signals

```text
subarray sum divisible by K
sum is a multiple of K
sum % K == 0
count divisible subarrays
existence of divisible subarray
remove subarray to make total divisible
```

### Important Storage Choice

```text
COUNT
→ frequency of remainder

EXISTENCE + length
→ earliest index of remainder

MINIMUM length
→ latest useful index
```

### Negative Remainders

In C++:

```text
-2 % 5 = -2
```

Normalize:

```text
rem = ((prefix % K) + K) % K
```

### Complexity

```text
Scan:  O(n)
Space: O(K) or O(n)
```

---

## Core Formula and Derivation

Need:

```text
sum(l,r) % K = 0
```

Replace range sum:

```text
(pref[r] - pref[l-1]) % K = 0
```

This means:

```text
pref[r] - pref[l-1]
= q*K
```

Move `pref[l-1]`:

```text
pref[r]
= pref[l-1] + q*K
```

Take modulo `K`:

```text
pref[r] % K
=
pref[l-1] % K
```

Mental model:

```text
CURRENT PREFIX
      ↓
CURRENT REMAINDER
      ↓
HAVE I SEEN THIS REMAINDER?
      ↓
YES → DIFFERENCE IS DIVISIBLE BY K
```

### Why `freq[0] = 1`?

The empty prefix has:

```text
sum = 0
remainder = 0
```

Example:

```text
a = [2,3]
K = 5

prefix = 5
rem = 0

Earlier empty prefix:
rem = 0

same remainder
→ whole subarray [2,3] is divisible by 5
```

---

## Generic Dry Run

```text
a = [4,1,5]
K = 5

start:
prefix = 0
freq[0] = 1
answer = 0

x = 4
prefix = 4
rem = 4
freq[4] = 0
answer = 0
freq[4]++

x = 1
prefix = 5
rem = 0
freq[0] = 1
answer = 1
freq[0]++

x = 5
prefix = 10
rem = 0
freq[0] = 2
answer = 3
freq[0]++
```

Why two new subarrays at the last step?

```text
previous rem 0 from empty prefix
→ [4,1,5] sum = 10

previous rem 0 after [4,1]
→ [5] sum = 5
```

### Generic Pseudocode

```text
freq[0] = 1
prefix = 0
answer = 0

for x in array:
    prefix += x

    rem = ((prefix % K) + K) % K

    answer += freq[rem]
    freq[rem]++

return answer
```

---

# Problem 1 — Subarray Sums Divisible by K

Problem Link: [Subarray Sums Divisible by K](https://leetcode.com/problems/subarray-sums-divisible-by-k/)

### What is the problem asking?

Given an integer array `nums` and an integer `k`, count the number of non-empty contiguous subarrays whose sum is divisible by `k`.

### Observation

```text
SUBARRAY SUM DIVISIBLE BY K
            ↓
TWO PREFIXES NEED SAME REMAINDER
            ↓
COUNT PREVIOUS REMAINDERS
```

### Compact Algebra Derivation

```text
Need:

sum(l,r) % k = 0

But:

sum(l,r)
= pref[r] - pref[l-1]

Therefore:

(pref[r] - pref[l-1]) % k = 0

So:

pref[r] % k
=
pref[l-1] % k

Meaning:

same prefix remainder
→ divisible subarray between them
```

### Simple Dry Run

```text
Initial:        nums = [4, 5, 0, -2, -3, 1]
                k = 5

                freq[0] = 1
                prefix = 0
                ans = 0

Step 1:         x = 4
                prefix = 4
                rem = 4
                freq[4] = 0
                ans = 0
                freq[4] becomes 1

Step 2:         x = 5
                prefix = 9
                rem = 9 % 5 = 4

                Previous remainder 4 exists once
                ans += 1
                ans = 1

                Why?
                previous prefix = 4
                current prefix  = 9
                9 - 4 = 5 --> divisible by 5

                freq[4] becomes 2

Step 3:         x = 0
                prefix = 9
                rem = 4

                freq[4] = 2
                ans += 2
                ans = 3

Step 4:         x = -2
                prefix = 7
                rem = 2
                freq[2] = 0
                ans = 3
                freq[2] becomes 1

Step 5:         x = -3
                prefix = 4
                rem = 4

                freq[4] = 3
                ans += 3
                ans = 6

Step 6:         x = 1
                prefix = 5
                rem = 0

                freq[0] = 1
                ans += 1
                ans = 7

Final Answer:   7

Final State:    Same prefix remainder --> divisible difference
```

### Pseudocode

```text
freq[0] = 1
prefix = 0
ans = 0

for x in nums:
    prefix += x
    rem = ((prefix % k) + k) % k
    ans += freq[rem]
    freq[rem]++

return ans
```

### C++

```cpp
class Solution {
public:
    int subarraysDivByK(vector<int>& nums, int k) {
        vector<int> freq(k, 0);
        freq[0] = 1;

        long long prefix = 0;
        int ans = 0;

        for (int x : nums) {
            prefix += x;
            int rem = (int)((prefix % k + k) % k);

            ans += freq[rem];
            freq[rem]++;
        }

        return ans;
    }
};
```

### Complexity

```text
Time:  O(n)
Space: O(K) or O(n)
```

---

# Problem 2 — Continuous Subarray Sum

Problem Link: [Continuous Subarray Sum](https://leetcode.com/problems/continuous-subarray-sum/)

### What is the problem asking?

Given `nums` and `k`, determine whether there exists a contiguous subarray of length at least `2` whose sum is a multiple of `k`.

### Observation

```text
EXISTS DIVISIBLE SUBARRAY
        +
LENGTH >= 2
        ↓
SAME REMAINDER + EARLIEST INDEX
```

### Compact Algebra Derivation

```text
Need:

(pref[r] - pref[l-1]) % k = 0

Therefore:

pref[r] % k
=
pref[l-1] % k

So equal remainders are enough.

But length must be >= 2.

If current index = i
and earlier same remainder was at j:

length = i - j

Need:

i - j >= 2

To maximize possible distance,
keep the EARLIEST j.
```

### Simple Dry Run

```text
Initial:        nums = [23, 2, 4, 6, 7]
                k = 6

                first[0] = -1
                prefix = 0

Step 1:         i = 0, x = 23
                prefix = 23
                rem = 23 % 6 = 5

                remainder 5 not seen
                first[5] = 0

Step 2:         i = 1, x = 2
                prefix = 25
                rem = 25 % 6 = 1

                remainder 1 not seen
                first[1] = 1

Step 3:         i = 2, x = 4
                prefix = 29
                rem = 29 % 6 = 5

                remainder 5 was first seen at index 0

                distance = 2 - 0 = 2
                distance >= 2 --> VALID

                Prefix difference:
                29 - 23 = 6
                6 % 6 = 0

                Corresponding subarray:
                nums[1 ... 2] = [2, 4]
                sum = 6

Final Answer:   true

Final State:    first occurrence of remainder 5 stays at index 0
```

### Pseudocode

```text
first[0] = -1
prefix = 0

for i = 0 to n - 1:
    prefix += nums[i]
    rem = prefix % k

    if rem exists in first:
        if i - first[rem] >= 2:
            return true
    else:
        first[rem] = i

return false
```

### C++

```cpp
class Solution {
public:
    bool checkSubarraySum(vector<int>& nums, int k) {
        unordered_map<int, int> first;
        first[0] = -1;

        long long prefix = 0;

        for (int i = 0; i < (int)nums.size(); ++i) {
            prefix += nums[i];
            int rem = (int)(prefix % k);

            if (first.count(rem)) {
                if (i - first[rem] >= 2) {
                    return true;
                }
            } else {
                first[rem] = i;
            }
        }

        return false;
    }
};
```

### Complexity

```text
Time:  O(n)
Space: O(K) or O(n)
```

---

# Problem 3 — Make Sum Divisible by P

Problem Link: [Make Sum Divisible by P](https://leetcode.com/problems/make-sum-divisible-by-p/)

### What is the problem asking?

Given a positive integer array `nums` and integer `p`, remove the shortest contiguous subarray so that the sum of the remaining elements is divisible by `p`. Removing the entire array is not allowed.

### Observation

```text
REMOVE SHORTEST SUBARRAY
SO REMAINING SUM % P = 0
        ↓
DERIVE REQUIRED PREVIOUS REMAINDER
        ↓
STORE LATEST INDEX
```

### Compact Algebra Derivation

```text
Let:

total % p = target

We remove subarray X.

Need remaining sum divisible by p:

(total - X) % p = 0

Therefore:

X % p = target

For current prefix remainder cur
and previous remainder prev:

(cur - prev) % p = target

Solve for prev:

prev
= (cur - target) mod p

Normalized:

need
= (cur - target + p) % p

For minimum length,
store the LATEST index of each remainder.
```

### Simple Dry Run

```text
Initial:        nums = [3, 1, 4, 2]
                p = 6

                total = 10
                target = 10 % 6 = 4

Goal:           Remove a subarray whose sum % 6 = 4

Why?
                remaining = total - removed

                remaining % 6 = 0
                (10 - removed) % 6 = 0

                Therefore:
                removed % 6 = 4

Initial Map:    last[0] = -1
                prefix = 0
                ans = 4

Step 1:         i = 0, x = 3
                prefix = 3
                cur = 3

                need = (3 - 4 + 6) % 6
                     = 5

                remainder 5 not found
                last[3] = 0

Step 2:         i = 1, x = 1
                prefix = 4
                cur = 4

                need = (4 - 4 + 6) % 6
                     = 0

                last[0] = -1
                length = 1 - (-1) = 2
                ans = 2

                Candidate removed subarray:
                [3, 1]
                sum = 4

                last[4] = 1

Step 3:         i = 2, x = 4
                prefix = 8
                cur = 8 % 6 = 2

                need = (2 - 4 + 6) % 6
                     = 4

                last[4] = 1
                length = 2 - 1 = 1
                ans = 1

                Candidate removed subarray:
                [4]
                sum = 4

                last[2] = 2

Step 4:         i = 3, x = 2
                prefix = 10
                cur = 4

                need = 0
                candidate length = 3 - (-1) = 4
                ans remains 1

Final Answer:   1

Final State:    Remove [4]
                remaining = [3, 1, 2]
                sum = 6 --> divisible by 6
```

### Pseudocode

```text
target = totalSum(nums) % p

if target == 0:
    return 0

last[0] = -1
prefix = 0
ans = n

for i = 0 to n - 1:
    prefix = (prefix + nums[i]) % p

    need = (prefix - target + p) % p

    if need exists in last:
        ans = min(ans, i - last[need])

    last[prefix] = i

if ans == n:
    return -1

return ans
```

### C++

```cpp
class Solution {
public:
    int minSubarray(vector<int>& nums, int p) {
        long long total = 0;
        for (int x : nums) total += x;

        int target = (int)(total % p);
        if (target == 0) return 0;

        unordered_map<int, int> last;
        last[0] = -1;

        long long prefix = 0;
        int n = nums.size();
        int ans = n;

        for (int i = 0; i < n; ++i) {
            prefix = (prefix + nums[i]) % p;
            int cur = (int)prefix;

            int need = (cur - target + p) % p;

            if (last.count(need)) {
                ans = min(ans, i - last[need]);
            }

            last[cur] = i;
        }

        return ans == n ? -1 : ans;
    }
};
```

### Complexity

```text
Time:  O(n)
Space: O(K) or O(n)
```

---

# Problem 4 — Subarray Divisibility

Problem Link: [Subarray Divisibility](https://cses.fi/problemset/task/1662)

### What is the problem asking?

Given an array of `n` integers, count the number of contiguous subarrays whose sum is divisible by `n`.

### Observation

```text
COUNT SUBARRAYS DIVISIBLE BY n
        +
NEGATIVE VALUES POSSIBLE
        ↓
NORMALIZED PREFIX REMAINDERS
        ↓
COUNT EQUAL REMAINDERS
```

### Compact Algebra Derivation

```text
Here divisor = n.

Need:

sum(l,r) % n = 0

Using prefix sums:

(pref[r] - pref[l-1]) % n = 0

Therefore:

pref[r] % n
=
pref[l-1] % n

Because values may be negative,
normalize:

rem
= ((prefix % n) + n) % n

Then count equal remainder pairs.
```

### Simple Dry Run

```text
Initial:        n = 5
                arr = [3, 1, 2, 7, 4]

                divisor = n = 5
                freq[0] = 1
                prefix = 0
                ans = 0

Step 1:         x = 3
                prefix = 3
                rem = 3

                ans += freq[3] = 0
                ans = 0
                freq[3] = 1

Step 2:         x = 1
                prefix = 4
                rem = 4

                ans += freq[4] = 0
                ans = 0
                freq[4] = 1

Step 3:         x = 2
                prefix = 6
                rem = 1

                ans += freq[1] = 0
                ans = 0
                freq[1] = 1

Step 4:         x = 7
                prefix = 13
                rem = 3

                freq[3] = 1
                ans += 1
                ans = 1

                Equal remainder pair:
                earlier prefix remainder = 3
                current prefix remainder = 3

                Difference:
                13 - 3 = 10
                10 % 5 = 0

Step 5:         x = 4
                prefix = 17
                rem = 2

                ans += freq[2] = 0
                ans = 1
                freq[2] = 1

Final Answer:   1

Final State:    Count pairs of equal prefix remainders

Negative-value reminder:

                prefix = -2
                n = 5

                C++: -2 % 5 = -2

                Normalize:
                ((-2 % 5) + 5) % 5
                = 3
```

### Pseudocode

```text
freq[0] = 1
prefix = 0
ans = 0

for x in arr:
    prefix += x
    rem = ((prefix % n) + n) % n

    ans += freq[rem]
    freq[rem]++

print ans
```

### C++

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<long long> freq(n, 0);
    freq[0] = 1;

    long long prefix = 0;
    long long ans = 0;

    for (int i = 0; i < n; ++i) {
        long long x;
        cin >> x;

        prefix += x;
        int rem = (int)((prefix % n + n) % n);

        ans += freq[rem];
        freq[rem]++;
    }

    cout << ans << '\n';
}
```

### Complexity

```text
Time:  O(n)
Space: O(K) or O(n)
```

---

# Fast Revision Model

## One Formula

```text
sum(l,r)
= pref[r] - pref[l-1]
```

Divisible by `K`:

```text
(pref[r] - pref[l-1]) % K = 0

              ↓

pref[r] % K
=
pref[l-1] % K
```

## What changes between the problems?

| Problem | What is actually being asked? | Map / array stores |
|---|---|---|
| Subarray Sums Divisible by K | Count divisible subarrays | Remainder frequency |
| Continuous Subarray Sum | Does a divisible subarray of length ≥ 2 exist? | Earliest remainder index |
| Make Sum Divisible by P | Remove shortest subarray | Latest remainder index |
| Subarray Divisibility | Count sums divisible by `n` | Normalized remainder frequency |

## Same Remainder vs Required Remainder

### Divisible subarray

```text
need:
currentRem == previousRem
```

### Remove subarray with remainder `target`

```text
(cur - prev) % P = target

prev
= (cur - target + P) % P
```

## 5-Minute Recognition

```text
1. WHAT?
   Subarray + divisibility / modulo.

2. WRITE RANGE SUM
   pref[r] - pref[l-1]

3. APPLY MODULO
   (pref[r] - pref[l-1]) % K

4. REARRANGE
   Find relation between prefix remainders.

5. CHOOSE STORAGE
   count      → frequency
   existence  → earliest index
   minimum    → latest index
```

## Recognition Rule

```text
SUBARRAY + MODULO CONDITION
          ↓
PREFIX SUM
          ↓
PREFIX REMAINDER
          ↓
RELATION BETWEEN TWO REMAINDERS
          ↓
FREQUENCY / INDEX MAP
```

The main equation to remember:

```text
(pref[r] - pref[l-1]) % K = 0

              ⇕

pref[r] % K = pref[l-1] % K
```
