# 017_Subarray_XOR_Problems.md — MiniPrefixSumDifferenceEngine

# Subarray XOR Problems

> Subarray XOR problems are the XOR version of prefix-sum hashmap problems.
>
> Core identity:
>
> ```text
> prefixXor[r] ^ prefixXor[l-1] = target
> ```
>
> Rearranged:
>
> ```text
> prefixXor[l-1] = prefixXor[r] ^ target
> ```

---

## Clickable Index

1. [What Are Subarray XOR Problems?](#1-what-are-subarray-xor-problems)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [XOR Properties Recap](#3-xor-properties-recap)
4. [Prefix XOR Algebra](#4-prefix-xor-algebra)
5. [Subarray XOR Equal K Formula](#5-subarray-xor-equal-k-formula)
6. [HashMap Frequency Model](#6-hashmap-frequency-model)
7. [Why `freq[0] = 1` Is Needed](#7-why-freq0--1-is-needed)
8. [Count vs Longest XOR Difference](#8-count-vs-longest-xor-difference)
9. [Step-by-Step Dry Run — Count XOR K](#9-step-by-step-dry-run--count-xor-k)
10. [Step-by-Step Dry Run — Zero XOR](#10-step-by-step-dry-run--zero-xor)
11. [Problem Form 1 — Count Subarrays XOR K](#11-problem-form-1--count-subarrays-xor-k)
12. [Problem Form 2 — Count Zero XOR Subarrays](#12-problem-form-2--count-zero-xor-subarrays)
13. [Problem Form 3 — Longest Subarray XOR K](#13-problem-form-3--longest-subarray-xor-k)
14. [Problem Form 4 — Count Equal Prefix XOR Pairs](#14-problem-form-4--count-equal-prefix-xor-pairs)
15. [Problem Form 5 — XOR Target Event Window](#15-problem-form-5--xor-target-event-window)
16. [Real World Model 1 — Toggle Event Window](#16-real-world-model-1--toggle-event-window)
17. [Real World Model 2 — Feature Flag State Segment](#17-real-world-model-2--feature-flag-state-segment)
18. [Real World Model 3 — Parity-Based Log Segment](#18-real-world-model-3--parity-based-log-segment)
19. [Real World Model 4 — Lightweight Integrity Segment](#19-real-world-model-4--lightweight-integrity-segment)
20. [Decision Tree](#20-decision-tree)
21. [Common Mistakes](#21-common-mistakes)
22. [Complexity](#22-complexity)
23. [Reusable C++ Templates](#23-reusable-c-templates)
24. [CP / FAANG Problem Forms](#24-cp--faang-problem-forms)
25. [Practice Checklist](#25-practice-checklist)
26. [Next Step](#26-next-step)

---

## 1. What Are Subarray XOR Problems?

Given an array:

```text
nums[]
```

A subarray XOR problem asks about:

```text
nums[l] ^ nums[l+1] ^ ... ^ nums[r]
```

Common targets:

```text
count subarrays with XOR K
count subarrays with XOR 0
longest subarray with XOR K
find if any subarray has XOR K
```

---

## 2. Why This Topic Matters

This pattern appears in:

```text
Codeforces
AtCoder
LeetCode
Google OA
Amazon OA
bitmask parity problems
toggle-state problems
prefix state problems
```

It teaches:

```text
XOR prefix cancellation
hashmap over prefix XOR states
counting previous states
```

---

## 3. XOR Properties Recap

Important XOR rules:

```text
x ^ x = 0
x ^ 0 = x
x ^ y = y ^ x
(x ^ y) ^ z = x ^ (y ^ z)
```

Most important:

```text
same value cancels itself
```

Example:

```text
7 ^ 3 ^ 7 = 3
```

---

## 4. Prefix XOR Algebra

Define:

```text
prefixXor[i] = nums[0] ^ nums[1] ^ ... ^ nums[i]
```

Subarray XOR:

```text
xor(l..r) = prefixXor[r] ^ prefixXor[l-1]
```

Because common prefix cancels.

Using 1-indexed prefix:

```text
pref[0] = 0
pref[i] = pref[i-1] ^ nums[i-1]
xor(l,r) = pref[r+1] ^ pref[l]
```

---

## 5. Subarray XOR Equal K Formula

We want:

```text
prefixXor[r] ^ prefixXor[l-1] = K
```

Rearrange using XOR both sides with `K`:

```text
prefixXor[l-1] = prefixXor[r] ^ K
```

At current index:

```text
current = prefixXor[r]
need = current ^ K
```

If `need` appeared before, then subarrays ending here have XOR `K`.

---

## 6. HashMap Frequency Model

For counting:

```text
freq[prefixXor] = how many times this prefix XOR appeared
```

At each value:

```text
prefix ^= x
need = prefix ^ K
answer += freq[need]
freq[prefix]++
```

---

## 7. Why `freq[0] = 1` Is Needed

Before reading any element:

```text
prefixXor = 0
```

This is empty prefix.

Example:

```text
nums = [5]
K = 5
```

At index 0:

```text
prefix = 5
need = 5 ^ 5 = 0
```

So `freq[0]` must exist to count subarray starting at index 0.

---

## 8. Count vs Longest XOR Difference

| Goal | Store |
|---|---|
| Count subarrays XOR K | frequency |
| Longest subarray XOR K | earliest index |
| Existence of XOR K | set |
| Range XOR query | prefix array |

---

## 9. Step-by-Step Dry Run — Count XOR K

Input:

```text
nums = [4, 2, 2, 6, 4]
K = 6
```

Initialize:

```text
freq[0] = 1
prefix = 0
answer = 0
```

Table:

| i | x | prefix | need = prefix ^ K | freq[need] before | answer |
|---|---:|---:|---:|---:|---:|
| 0 | 4 | 4 | 2 | 0 | 0 |
| 1 | 2 | 6 | 0 | 1 | 1 |
| 2 | 2 | 4 | 2 | 0 | 1 |
| 3 | 6 | 2 | 4 | 2 | 3 |
| 4 | 4 | 6 | 0 | 1 | 4 |

Final answer:

```text
4
```

---

## 10. Step-by-Step Dry Run — Zero XOR

Input:

```text
nums = [1, 2, 3, 1, 2, 3]
K = 0
```

For XOR zero:

```text
need = prefix ^ 0 = prefix
```

So we count repeated prefix XOR values.

Prefix sequence:

```text
0
1
3
0
1
3
0
```

Repeated prefix XOR means subarray between them has XOR 0.

---

## 11. Problem Form 1 — Count Subarrays XOR K

### Problem

Count subarrays whose XOR equals `K`.

Input:

```text
nums = [4, 2, 2, 6, 4]
K = 6
```

Output:

```text
4
```

---

### Pattern Recognition

Use this when:

```text
continuous subarray
XOR equals target
count all
```

Pattern:

```text
Prefix XOR + Frequency HashMap
```

---

### Step-by-Step Working

For each index:

```text
prefix ^= nums[i]
need = prefix ^ K
answer += freq[need]
freq[prefix]++
```

Why?

```text
previousPrefix ^ currentPrefix = K
previousPrefix = currentPrefix ^ K
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countSubarraysXorK(vector<int>& nums, int k) {
    unordered_map<int, long long> freq;

    // Empty prefix XOR.
    freq[0] = 1;

    int prefix = 0;
    long long answer = 0;

    for (int x : nums) {
        prefix ^= x;

        int need = prefix ^ k;

        answer += freq[need];

        freq[prefix]++;
    }

    return answer;
}

int main() {
    vector<int> nums = {4, 2, 2, 6, 4};
    int k = 6;

    cout << countSubarraysXorK(nums, k) << "\n";

    return 0;
}
```

---

### Complexity

```text
Time  : O(N)
Space : O(N)
```

---

## 12. Problem Form 2 — Count Zero XOR Subarrays

### Problem

Count subarrays whose XOR is zero.

Input:

```text
nums = [1, 2, 3, 1, 2, 3]
```

---

### Pattern Recognition

XOR zero means:

```text
same prefix XOR repeated
```

If:

```text
prefixXor[i] == prefixXor[j]
```

then subarray between them has XOR:

```text
0
```

---

### Step-by-Step Simulation

Prefix XOR values:

```text
0
1
3
0
1
3
0
```

Repeated values:

```text
0 appears 3 times
1 appears 2 times
3 appears 2 times
```

Pairs:

```text
C(3,2) + C(2,2) + C(2,2)
= 3 + 1 + 1
= 5
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countZeroXorSubarrays(vector<int>& nums) {
    unordered_map<int, long long> freq;

    freq[0] = 1;

    int prefix = 0;
    long long answer = 0;

    for (int x : nums) {
        prefix ^= x;

        // Same prefix XOR creates zero-XOR subarray.
        answer += freq[prefix];

        freq[prefix]++;
    }

    return answer;
}

int main() {
    vector<int> nums = {1, 2, 3, 1, 2, 3};

    cout << countZeroXorSubarrays(nums) << "\n";

    return 0;
}
```

---

## 13. Problem Form 3 — Longest Subarray XOR K

### Problem

Find longest subarray whose XOR equals `K`.

Input:

```text
nums = [4, 2, 2, 6, 4]
K = 6
```

---

### Pattern Recognition

For count:

```text
store frequency
```

For longest:

```text
store earliest index
```

---

### Step-by-Step Working

At each index:

```text
prefix ^= nums[i]
need = prefix ^ K
if need exists:
    length = i - firstIndex[need]
```

Store prefix only if not seen before.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestSubarrayXorK(vector<int>& nums, int k) {
    unordered_map<int, int> firstIndex;

    // Empty prefix XOR at index -1.
    firstIndex[0] = -1;

    int prefix = 0;
    int best = 0;

    for (int i = 0; i < nums.size(); i++) {
        prefix ^= nums[i];

        int need = prefix ^ k;

        if (firstIndex.count(need)) {
            best = max(best, i - firstIndex[need]);
        }

        if (!firstIndex.count(prefix)) {
            firstIndex[prefix] = i;
        }
    }

    return best;
}

int main() {
    vector<int> nums = {4, 2, 2, 6, 4};
    int k = 6;

    cout << longestSubarrayXorK(nums, k) << "\n";

    return 0;
}
```

---

## 14. Problem Form 4 — Count Equal Prefix XOR Pairs

### Problem

Given prefix XOR states, count pairs with equal prefix XOR.

Each equal pair represents a zero-XOR subarray.

---

### Pattern Recognition

If prefix XOR `v` appears `cnt` times:

```text
number of pairs = cnt * (cnt - 1) / 2
```

---

### Problem Simulation

Prefix XOR values:

```text
0, 1, 3, 0, 1, 3, 0
```

Counts:

```text
0 -> 3
1 -> 2
3 -> 2
```

Answer:

```text
3 + 1 + 1 = 5
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countEqualPrefixXorPairs(vector<int>& nums) {
    unordered_map<int, long long> freq;

    int prefix = 0;

    freq[0]++;

    for (int x : nums) {
        prefix ^= x;
        freq[prefix]++;
    }

    long long answer = 0;

    for (auto &[value, cnt] : freq) {
        answer += cnt * (cnt - 1) / 2;
    }

    return answer;
}

int main() {
    vector<int> nums = {1, 2, 3, 1, 2, 3};

    cout << countEqualPrefixXorPairs(nums) << "\n";

    return 0;
}
```

---

## 15. Problem Form 5 — XOR Target Event Window

### Problem

Given event codes represented as integers, count continuous event windows whose XOR equals target.

---

### Pattern Recognition

Event XOR target is just:

```text
subarray XOR equals K
```

---

### Problem Simulation

Events:

```text
[7, 3, 4, 7]
target = 4
```

Valid windows include:

```text
[4]
[7,3]
```

because:

```text
7 ^ 3 = 4
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countEventWindowsXorTarget(vector<int>& events, int target) {
    unordered_map<int, long long> freq;

    freq[0] = 1;

    int prefix = 0;
    long long answer = 0;

    for (int code : events) {
        prefix ^= code;

        answer += freq[prefix ^ target];

        freq[prefix]++;
    }

    return answer;
}

int main() {
    vector<int> events = {7, 3, 4, 7};

    cout << countEventWindowsXorTarget(events, 4) << "\n";

    return 0;
}
```

---

## 16. Real World Model 1 — Toggle Event Window

### Scenario

A system stores toggle events as bitmasks.

Example:

```text
feature A toggle = 1
feature B toggle = 2
feature C toggle = 4
```

Question:

```text
How many continuous event windows produce target toggle state?
```

---

### Problem Simulation

Events:

```text
[1, 2, 3, 1]
target = 3
```

Window:

```text
[1,2]
```

produces:

```text
1 ^ 2 = 3
```

Another window may also produce target.

---

### System Mapping

Used in:

```text
feature-flag state transitions
permission toggles
configuration delta replay
state-diff analysis
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countToggleWindows(vector<int>& toggles, int targetState) {
    unordered_map<int, long long> freq;

    freq[0] = 1;

    int prefix = 0;
    long long answer = 0;

    for (int mask : toggles) {
        prefix ^= mask;

        answer += freq[prefix ^ targetState];

        freq[prefix]++;
    }

    return answer;
}
```

---

## 17. Real World Model 2 — Feature Flag State Segment

### Scenario

Feature flags are stored as bitmasks.

A stream of events toggles flags.

Question:

```text
Longest segment that transforms system into target flag state.
```

---

### Problem Simulation

Events:

```text
A, B, A, C
```

Masks:

```text
[1, 2, 1, 4]
```

Target:

```text
B ^ C = 6
```

Segment:

```text
[2, 1, 4, 1] maybe after order/stream arrangement
```

The prefix XOR earliest-index method finds longest target-XOR segment.

---

### System Mapping

Used in:

```text
feature rollout analysis
state transition replay
configuration audit
permission state debugging
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestFlagTargetSegment(vector<int>& masks, int target) {
    unordered_map<int, int> firstIndex;

    firstIndex[0] = -1;

    int prefix = 0;
    int best = 0;

    for (int i = 0; i < masks.size(); i++) {
        prefix ^= masks[i];

        int need = prefix ^ target;

        if (firstIndex.count(need)) {
            best = max(best, i - firstIndex[need]);
        }

        if (!firstIndex.count(prefix)) {
            firstIndex[prefix] = i;
        }
    }

    return best;
}
```

---

## 18. Real World Model 3 — Parity-Based Log Segment

### Scenario

A log stream records parity-style events.

Example:

```text
open/close
login/logout
lock/unlock
enable/disable
```

Question:

```text
How many windows end in neutral state?
```

Neutral state means:

```text
XOR = 0
```

---

### Problem Simulation

Events:

```text
[1, 2, 3]
```

Since:

```text
1 ^ 2 ^ 3 = 0
```

whole segment is neutral.

---

### System Mapping

Used in:

```text
session parity analysis
state consistency checks
toggle-event validation
audit log reconstruction
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countNeutralLogSegments(vector<int>& events) {
    unordered_map<int, long long> freq;

    freq[0] = 1;

    int prefix = 0;
    long long answer = 0;

    for (int e : events) {
        prefix ^= e;

        answer += freq[prefix];

        freq[prefix]++;
    }

    return answer;
}
```

---

## 19. Real World Model 4 — Lightweight Integrity Segment

### Scenario

A stream of IDs should cancel in pairs inside a valid segment.

Question:

```text
How many windows have XOR 0?
```

This means all IDs inside the window cancel by parity.

---

### Problem Simulation

IDs:

```text
[101, 202, 101, 202]
```

Whole window XOR:

```text
101 ^ 202 ^ 101 ^ 202 = 0
```

So it is a neutral integrity segment.

---

### System Mapping

Used in:

```text
lightweight duplicate validation
odd/even occurrence checks
stream integrity analysis
log-pair cancellation
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countIntegrityNeutralSegments(vector<int>& ids) {
    unordered_map<int, long long> freq;

    freq[0] = 1;

    int prefix = 0;
    long long answer = 0;

    for (int id : ids) {
        prefix ^= id;

        answer += freq[prefix];

        freq[prefix]++;
    }

    return answer;
}
```

---

## 20. Decision Tree

```text
Subarray XOR problem?
|
+-- Need count XOR K?
|   |
|   +-- freq[prefixXor]
|
+-- Need longest XOR K?
|   |
|   +-- firstIndex[prefixXor]
|
+-- Need XOR 0?
|   |
|   +-- repeated prefix XOR
|
+-- Need range XOR queries?
|   |
|   +-- prefix XOR array
|
+-- Need max XOR?
    |
    +-- XOR trie
```

---

## 21. Common Mistakes

### Mistake 1 — Using Sum Formula

Wrong:

```text
prefix - K
```

Correct for XOR:

```text
prefix ^ K
```

---

### Mistake 2 — Forgetting `freq[0] = 1`

This misses subarrays starting at index 0.

---

### Mistake 3 — Updating Frequency Before Counting

Correct:

```cpp
answer += freq[prefix ^ k];
freq[prefix]++;
```

---

### Mistake 4 — Using `+` Instead Of `^`

XOR is:

```cpp
^
```

not addition.

---

### Mistake 5 — Confusing XOR Target With OR/AND

Prefix trick works for XOR because XOR has inverse property.

It does not work the same way for AND/OR.

---

## 22. Complexity

For count / longest target XOR:

```text
Time  : O(N)
Space : O(N)
```

For range XOR query:

```text
Build : O(N)
Query : O(1)
```

---

## 23. Reusable C++ Templates

### Template 1 — Count Subarrays XOR K

```cpp
long long countXorK(vector<int>& nums, int k) {
    unordered_map<int, long long> freq;

    freq[0] = 1;

    int prefix = 0;
    long long ans = 0;

    for (int x : nums) {
        prefix ^= x;

        ans += freq[prefix ^ k];

        freq[prefix]++;
    }

    return ans;
}
```

---

### Template 2 — Count Zero XOR

```cpp
long long countZeroXor(vector<int>& nums) {
    unordered_map<int, long long> freq;

    freq[0] = 1;

    int prefix = 0;
    long long ans = 0;

    for (int x : nums) {
        prefix ^= x;

        ans += freq[prefix];

        freq[prefix]++;
    }

    return ans;
}
```

---

### Template 3 — Longest XOR K

```cpp
int longestXorK(vector<int>& nums, int k) {
    unordered_map<int, int> firstIndex;

    firstIndex[0] = -1;

    int prefix = 0;
    int best = 0;

    for (int i = 0; i < nums.size(); i++) {
        prefix ^= nums[i];

        int need = prefix ^ k;

        if (firstIndex.count(need)) {
            best = max(best, i - firstIndex[need]);
        }

        if (!firstIndex.count(prefix)) {
            firstIndex[prefix] = i;
        }
    }

    return best;
}
```

---

## 24. CP / FAANG Problem Forms

---

### Problem 1 — Count Subarrays XOR K

#### Recognition

```text
count subarrays whose XOR equals K
```

#### Pattern

```text
prefix XOR + frequency hashmap
```

#### Step-by-Step Working

```text
prefix ^= x
need = prefix ^ K
answer += freq[need]
freq[prefix]++
```

#### Commented C++ Code

```cpp
long long solve(vector<int>& nums, int k) {
    unordered_map<int, long long> freq;
    freq[0] = 1;

    int prefix = 0;
    long long ans = 0;

    for (int x : nums) {
        prefix ^= x;
        ans += freq[prefix ^ k];
        freq[prefix]++;
    }

    return ans;
}
```

---

### Problem 2 — Count Zero XOR Subarrays

#### Recognition

```text
subarray XOR equals 0
```

#### Pattern

```text
same prefix XOR repeated
```

#### Commented C++ Code

```cpp
long long solve(vector<int>& nums) {
    unordered_map<int, long long> freq;
    freq[0] = 1;

    int prefix = 0;
    long long ans = 0;

    for (int x : nums) {
        prefix ^= x;
        ans += freq[prefix];
        freq[prefix]++;
    }

    return ans;
}
```

---

### Problem 3 — Longest Subarray XOR K

#### Recognition

```text
maximum length subarray with XOR K
```

#### Pattern

```text
prefix XOR + earliest index
```

#### Commented C++ Code

```cpp
int solve(vector<int>& nums, int k) {
    unordered_map<int, int> first;
    first[0] = -1;

    int prefix = 0;
    int best = 0;

    for (int i = 0; i < nums.size(); i++) {
        prefix ^= nums[i];

        int need = prefix ^ k;

        if (first.count(need)) {
            best = max(best, i - first[need]);
        }

        if (!first.count(prefix)) {
            first[prefix] = i;
        }
    }

    return best;
}
```

---

### Problem 4 — Equal Prefix XOR Pair Count

#### Recognition

```text
count pairs of equal prefix XOR
```

#### Pattern

```text
combination over frequency
```

#### Commented C++ Code

```cpp
long long pairCount(unordered_map<int, long long>& freq) {
    long long ans = 0;

    for (auto &[x, c] : freq) {
        ans += c * (c - 1) / 2;
    }

    return ans;
}
```

---

### Problem 5 — Toggle State Target Window

#### Recognition

```text
events toggle bitmask state
target final XOR
```

#### Pattern

```text
prefix XOR over event masks
```

#### Commented C++ Code

```cpp
long long targetToggleWindows(vector<int>& masks, int target) {
    unordered_map<int, long long> freq;
    freq[0] = 1;

    int prefix = 0;
    long long ans = 0;

    for (int mask : masks) {
        prefix ^= mask;

        ans += freq[prefix ^ target];

        freq[prefix]++;
    }

    return ans;
}
```

---

## 25. Practice Checklist

Before solving:

```text
1. Is this XOR over a subarray?
2. Need target XOR K?
3. Need count or longest?
4. For count, did I use frequency?
5. For longest, did I use earliest index?
6. Did I initialize prefix XOR 0?
7. Did I use need = prefix ^ K?
8. Did I count before inserting current prefix?
9. Is it zero XOR? Use repeated prefix XOR.
10. Is it max XOR? Consider XOR trie.
```

---

## 26. Next Step

```text
018_Prefix_Bitmask_Parity.md
```

Next file covers:

```text
bitmask parity
odd/even character counts
wonderful substrings
palindrome-anagram substrings
state compression
