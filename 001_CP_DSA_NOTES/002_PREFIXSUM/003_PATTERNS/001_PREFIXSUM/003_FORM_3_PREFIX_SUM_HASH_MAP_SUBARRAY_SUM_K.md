# PREFIX SUM PATTERNS

## Pattern 3 — Prefix Sum + Hash Map: Subarray Sum = K

---

## Table of Contents

- [Pattern Overview](#pattern-overview)
- [Core Formula and Derivation](#core-formula-and-derivation)
- [Generic Dry Run](#generic-dry-run)
- [Problem 1 — Subarray Sum Equals K](#problem-1--subarray-sum-equals-k)
- [Problem 2 — Binary Subarrays With Sum](#problem-2--binary-subarrays-with-sum)
- [Problem 3 — Maximum Size Subarray Sum Equals k](#problem-3--maximum-size-subarray-sum-equals-k)
- [Problem 4 — Subarray Sums II](#problem-4--subarray-sums-ii)
- [Problem 5 — Good Subarrays](#problem-5--good-subarrays)
- [Fast Revision Model](#fast-revision-model)

---

## Pattern Overview

### What kind of problem is this?

Use this pattern when the problem asks about **contiguous subarrays with an exact sum/condition** and the left boundary is unknown.

```text
many possible subarrays
        +
exact target condition
        +
negative values may exist
        ↓
PREFIX SUM + HASH MAP
```

Instead of trying every `l` for every `r`, fix the right endpoint and ask:

```text
What OLD prefix do I need
for the subarray ending HERE
to have the required sum?
```

### Core Idea

```text
sum(l,r)
= pref[r] - pref[l-1]
```

If:

```text
sum(l,r) = K
```

then:

```text
pref[r] - pref[l-1] = K

pref[l-1] = pref[r] - K
```

Therefore at current prefix `P`:

```text
need = P - K
```

### Recognition Signals

```text
count subarrays with sum K
longest subarray with sum K
exact contiguous sum
negative values possible
many possible left boundaries
condition can become relation between two prefixes
```

### Complexity

```text
Scan:  O(n) expected
Map:   O(n)
Space: O(n)
```

---

## Core Formula and Derivation

Wanted:

```text
a[l] + a[l+1] + ... + a[r] = K
```

Using prefix sums:

```text
pref[r]
= a[1] + ... + a[l-1] + a[l] + ... + a[r]

pref[l-1]
= a[1] + ... + a[l-1]
```

Subtract:

```text
pref[r] - pref[l-1]
= a[l] + ... + a[r]
= K
```

Rearrange:

```text
pref[l-1]
= pref[r] - K
```

Mental model:

```text
CURRENT PREFIX P
      |
      | need P-K
      v
HASH MAP OF OLD PREFIXES
      |
      v
VALID LEFT BOUNDARIES
```

### Why `freq[0] = 1`?

Before reading any element:

```text
prefix = 0
```

This represents the empty prefix.

Example:

```text
a = [2,3]
K = 5

current prefix after index 2 = 5

need
= 5 - 5
= 0

freq[0] = 1

Therefore [2,3] is counted.
```

---

## Generic Dry Run

```text
a = [1,2,1]
K = 3

start:
prefix = 0
freq = {0:1}
answer = 0

x = 1
prefix = 1
need = 1-3 = -2
found = 0
answer = 0
freq[1]++

x = 2
prefix = 3
need = 3-3 = 0
found = 1
answer = 1
freq[3]++

x = 1
prefix = 4
need = 4-3 = 1
found = 1
answer = 2
freq[4]++

valid subarrays:
[1,2]
[2,1]
```

### Generic Pseudocode

```text
freq[0] = 1
prefix = 0
answer = 0

for x in array:
    prefix += x

    need = prefix - K
    answer += freq[need]

    freq[prefix]++

return answer
```

---

# Problem 1 — Subarray Sum Equals K

Problem Link: [Subarray Sum Equals K](https://leetcode.com/problems/subarray-sum-equals-k/)

### What is the problem asking?

Given an integer array and integer `k`, count the number of contiguous non-empty subarrays whose sum is exactly `k`.

### Observation

```text
EXACT SUBARRAY SUM = K
        +
NEGATIVES MAY EXIST
        ↓
PREFIX SUM + FREQUENCY MAP

At current prefix P:
need an earlier prefix P-K.
```

### Compact Algebra Derivation

```text
For subarray [l..r]:

sum(l,r)
= pref[r] - pref[l-1]

Need:
pref[r] - pref[l-1] = k

Move pref[l-1]:

pref[l-1]
= pref[r] - k

Let:
P = pref[r]

Then:
need = P - k

So every previous prefix equal to P-k
creates one valid subarray ending at r.
```

### Simple Dry Run

```text
Initial:        nums = [1, 1, 1], k = 2
                freq = {0:1}
                prefix = 0, answer = 0

Step 1:         x = 1
                prefix = 1
                need = 1 - 2 = -1
                freq[-1] = 0
                answer = 0
                freq[1]++

Step 2:         x = 1
                prefix = 2
                need = 2 - 2 = 0
                freq[0] = 1
                answer = 1
                freq[2]++

Step 3:         x = 1
                prefix = 3
                need = 3 - 2 = 1
                freq[1] = 1
                answer = 2
                freq[3]++

Final Answer:   2
Final State:    freq = {0:1, 1:1, 2:1, 3:1}
```

### Pseudocode

```text
freq[0] = 1
prefix = 0
answer = 0

for x in nums:
    prefix += x
    answer += freq[prefix - k]
    freq[prefix]++

return answer
```

### C++

```cpp
class Solution {
public:
    int subarraySum(vector<int>& nums, int k) {
        unordered_map<long long, int> freq;
        freq[0] = 1;

        long long prefix = 0;
        int ans = 0;

        for (int x : nums) {
            prefix += x;
            ans += freq[prefix - k];
            freq[prefix]++;
        }

        return ans;
    }
};
```

### Complexity

```text
Time:  O(n) expected
Space: O(n)
```

---

# Problem 2 — Binary Subarrays With Sum

Problem Link: [Binary Subarrays With Sum](https://leetcode.com/problems/binary-subarrays-with-sum/)

### What is the problem asking?

Given a binary array and target `goal`, count contiguous subarrays whose element sum is exactly `goal`.

### Observation

```text
BINARY ARRAY + EXACT SUM = goal
             ↓
PREFIX SUM + FREQUENCY MAP

Zeros can repeat the same prefix,
so one prefix value may represent
multiple valid left boundaries.
```

### Compact Algebra Derivation

```text
Need:

pref[r] - pref[l-1] = goal

Rearrange:

pref[l-1]
= pref[r] - goal

At current prefix P:

need = P - goal

If need appeared c times,
there are c valid subarrays ending here.
```

### Simple Dry Run

```text
Initial:        nums = [1, 0, 1, 0, 1], goal = 2
                freq = {0:1}
                prefix = 0, answer = 0

Step 1:         x = 1 --> prefix = 1
                need = -1 --> +0
                freq[1] = 1

Step 2:         x = 0 --> prefix = 1
                need = -1 --> +0
                freq[1] = 2

Step 3:         x = 1 --> prefix = 2
                need = 0 --> +1
                answer = 1
                freq[2] = 1

Step 4:         x = 0 --> prefix = 2
                need = 0 --> +1
                answer = 2
                freq[2] = 2

Step 5:         x = 1 --> prefix = 3
                need = 1 --> +2
                answer = 4

Final Answer:   4
Final State:    repeated prefix sums created multiple valid left boundaries
```

### Pseudocode

```text
freq[0] = 1
prefix = 0
answer = 0

for bit in nums:
    prefix += bit
    answer += freq[prefix - goal]
    freq[prefix]++

return answer
```

### C++

```cpp
class Solution {
public:
    int numSubarraysWithSum(vector<int>& nums, int goal) {
        unordered_map<int, int> freq;
        freq[0] = 1;

        int prefix = 0;
        int ans = 0;

        for (int x : nums) {
            prefix += x;
            ans += freq[prefix - goal];
            freq[prefix]++;
        }

        return ans;
    }
};
```

### Complexity

```text
Time:  O(n) expected
Space: O(n)
```

---

# Problem 3 — Maximum Size Subarray Sum Equals k

Problem Link: [Maximum Size Subarray Sum Equals k](https://leetcode.com/problems/maximum-size-subarray-sum-equals-k/)

### What is the problem asking?

Given an integer array and target `k`, find the maximum length of a contiguous subarray whose sum equals `k`.

### Observation

```text
LONGEST SUBARRAY WITH SUM K
             ↓
PREFIX SUM + EARLIEST INDEX MAP

For maximum length:
keep the FIRST occurrence of each prefix.
```

### Compact Algebra Derivation

```text
Need:

pref[r] - pref[l-1] = k

Therefore:

pref[l-1]
= pref[r] - k

At current position i:

need = prefix - k

If need first appeared at j:

length = i - j

To maximize length,
j must be as small as possible.

Therefore:
store the EARLIEST index only.
```

### Simple Dry Run

```text
Initial:        nums = [1, -1, 5, -2, 3], k = 3
                first = {0:0}
                prefix = 0, best = 0

Step 1:         i=1, x=1  --> prefix=1
                need=-2 not found
                first[1]=1

Step 2:         i=2, x=-1 --> prefix=0
                need=-3 not found
                keep first[0]=0

Step 3:         i=3, x=5  --> prefix=5
                need=2 not found
                first[5]=3

Step 4:         i=4, x=-2 --> prefix=3
                need=0 found at index 0
                length = 4 - 0 = 4
                best = 4

Step 5:         i=5, x=3  --> prefix=6
                need=3 found at index 4
                length = 1
                best remains 4

Final Answer:   4
Final State:    longest valid subarray = [1, -1, 5, -2]
```

### Pseudocode

```text
first[0] = 0
prefix = 0
best = 0

for i = 1 to n:
    prefix += nums[i]

    need = prefix - k
    if need exists in first:
        best = max(best, i - first[need])

    if prefix not in first:
        first[prefix] = i

return best
```

### C++

```cpp
class Solution {
public:
    int maxSubArrayLen(vector<int>& nums, int k) {
        unordered_map<long long, int> first;
        first[0] = 0;

        long long prefix = 0;
        int best = 0;

        for (int i = 1; i <= (int)nums.size(); ++i) {
            prefix += nums[i - 1];

            long long need = prefix - k;

            if (first.count(need)) {
                best = max(best, i - first[need]);
            }

            if (!first.count(prefix)) {
                first[prefix] = i;
            }
        }

        return best;
    }
};
```

### Complexity

```text
Time:  O(n) expected
Space: O(n)
```

---

# Problem 4 — Subarray Sums II

Problem Link: [Subarray Sums II](https://cses.fi/problemset/task/1661)

### What is the problem asking?

Given `n`, target `x`, and an integer array that may contain negative values, count all contiguous subarrays whose sum is exactly `x`.

### Observation

```text
COUNT SUBARRAYS WITH SUM x
        +
NEGATIVE VALUES POSSIBLE
        ↓
PREFIX SUM + FREQUENCY MAP

Two pointers are unsafe here.
```

### Compact Algebra Derivation

```text
Need:

pref[r] - pref[l-1] = x

Therefore:

pref[l-1]
= pref[r] - x

At current prefix P:

need = P - x

answer += number of previous
prefix sums equal to need.
```

### Simple Dry Run

```text
Initial:        arr = [2, -1, 3, 5, -2], x = 7
                freq = {0:1}
                prefix = 0, answer = 0

Step 1:         +2  --> prefix = 2
                need = -5 --> +0

Step 2:         -1  --> prefix = 1
                need = -6 --> +0

Step 3:         +3  --> prefix = 4
                need = -3 --> +0

Step 4:         +5  --> prefix = 9
                need = 2
                prefix 2 occurred once
                answer = 1       // [-1, 3, 5]

Step 5:         -2  --> prefix = 7
                need = 0
                prefix 0 occurred once
                answer = 2       // whole array

Final Answer:   2
Final State:    every valid subarray came from a previous prefix = currentPrefix - 7
```

### Pseudocode

```text
freq[0] = 1
prefix = 0
answer = 0

for x_i in arr:
    prefix += x_i
    answer += freq[prefix - target]
    freq[prefix]++

print answer
```

### C++

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    long long x;
    cin >> n >> x;

    unordered_map<long long, long long> freq;
    freq[0] = 1;

    long long prefix = 0;
    long long ans = 0;

    for (int i = 0; i < n; ++i) {
        long long value;
        cin >> value;

        prefix += value;
        ans += freq[prefix - x];
        freq[prefix]++;
    }

    cout << ans << '\n';
}
```

### Complexity

```text
Time:  O(n) expected
Space: O(n)
```

---

# Problem 5 — Good Subarrays

Problem Link: [Good Subarrays](https://codeforces.com/problemset/problem/1398/C)

### What is the problem asking?

Given a digit array, count subarrays whose sum equals their length. Transform the condition so that valid subarrays correspond to equal transformed prefix values.

### Observation

```text
SUBARRAY SUM = SUBARRAY LENGTH
             ↓
REARRANGE THE CONDITION
             ↓
EQUAL TRANSFORMED PREFIX KEYS

key[i] = pref[i] - i
```

### Compact Algebra Derivation

```text
Good subarray condition:

pref[r] - pref[l-1]
= r - l + 1

Rewrite right side:

r - l + 1
= r - (l-1)

So:

pref[r] - pref[l-1]
= r - (l-1)

Move index terms with their prefixes:

pref[r] - r
=
pref[l-1] - (l-1)

Define:

key[i] = pref[i] - i

Then:

key[r] = key[l-1]

Therefore equal transformed prefix keys
identify good subarrays.
```

### Simple Dry Run

```text
Initial:        digits = [1, 2, 0]
                Condition: subarray sum = subarray length
                freq = {0:1}
                pref = 0, answer = 0

Transformation:
                pref[r] - pref[l-1] = r-l+1

                pref[r] - r
                    =
                pref[l-1] - (l-1)

Step 1:         i=1, digit=1
                pref = 1
                key = pref-i = 1-1 = 0
                freq[0] = 1 --> answer = 1
                freq[0] = 2

Step 2:         i=2, digit=2
                pref = 3
                key = 3-2 = 1
                freq[1] = 0 --> answer = 1
                freq[1] = 1

Step 3:         i=3, digit=0
                pref = 3
                key = 3-3 = 0
                freq[0] = 2 --> answer = 3
                freq[0] = 3

Valid:          [1]
                [2,0]
                [1,2,0]

Final Answer:   3
Final State:    equal transformed prefixes pref[i]-i identify good subarrays
```

### Pseudocode

```text
freq[0] = 1
pref = 0
answer = 0

for i = 1 to n:
    pref += digit[i]
    key = pref - i

    answer += freq[key]
    freq[key]++

print answer
```

### C++

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        int n;
        string s;
        cin >> n >> s;

        unordered_map<long long, long long> freq;
        freq[0] = 1;

        long long pref = 0;
        long long ans = 0;

        for (int i = 1; i <= n; ++i) {
            pref += s[i - 1] - '0';

            long long key = pref - i;
            ans += freq[key];
            freq[key]++;
        }

        cout << ans << '\n';
    }
}
```

### Complexity

```text
Time:  O(n) expected
Space: O(n)
```

---

# Fast Revision Model

## One Formula

```text
sum(l,r)
= pref[r] - pref[l-1]
```

Exact target:

```text
pref[r] - pref[l-1] = K

        ↓ rearrange

pref[l-1] = pref[r] - K
```

At current prefix `P`:

```text
need = P - K
```

## What changes between the problems?

| Problem | What is actually being asked? | What the map stores |
|---|---|---|
| Subarray Sum Equals K | Count subarrays with exact sum `k` | Prefix frequency |
| Binary Subarrays With Sum | Count binary subarrays with exact `goal` | Prefix frequency |
| Maximum Size Subarray Sum Equals k | Find longest exact-sum subarray | Earliest prefix index |
| Subarray Sums II | Count exact-sum subarrays with negatives | Prefix frequency |
| Good Subarrays | Count subarrays where sum = length | Frequency of `pref[i]-i` |

## Count vs Longest

```text
COUNT
  ↓
How many matching old prefixes?
  ↓
freq[prefix]

LONGEST
  ↓
How far back is the matching prefix?
  ↓
earliestIndex[prefix]
```

## 5-Minute Recognition

```text
1. WHAT?
   Need a contiguous subarray satisfying an exact condition.

2. WRITE RANGE SUM
   pref[r] - pref[l-1]

3. SET CONDITION
   pref[r] - pref[l-1] = K

4. REARRANGE FOR OLD PREFIX
   pref[l-1] = pref[r] - K

5. ASK
   Have I seen this required prefix before?

6. TOOL
   Hash map.
```

## Recognition Rule

```text
EXACT SUBARRAY CONDITION
          ↓
WRITE WITH TWO PREFIX STATES
          ↓
REARRANGE FOR OLD PREFIX
          ↓
LOOK UP OLD STATE IN HASH MAP
```

The key contest question is:

```text
What previous prefix state must exist
for a subarray ending HERE to be valid?
```
