# PREFIX SUM PATTERNS

## Pattern 1 — Basic Range Sum / Static Queries

Pattern Link: [Pattern 1 — Basic Range Sum / Static Queries](https://github.com/mthaiseer/springcloud-systemdesign-notes/blob/main/001_CP_DSA_NOTES_V2/002_PREFIXSUM/002_PREFIXSUM_DIFF_ARRAY_PATTERNWISE_PROBLEMS.md#pattern-1)

---

## Table of Contents

- [Pattern Overview](#pattern-overview)
- [Core Formula and Derivation](#core-formula-and-derivation)
- [Generic Dry Run](#generic-dry-run)
- [Problem 1 — Range Sum Query - Immutable](#problem-1--range-sum-query---immutable)
- [Problem 2 — K Radius Subarray Averages](#problem-2--k-radius-subarray-averages)
- [Problem 3 — Kuriyama Mirai's Stones](#problem-3--kuriyama-mirais-stones)
- [Problem 4 — Static Range Sum Queries](#problem-4--static-range-sum-queries)
- [Fast Revision Model](#fast-revision-model)

---

## Pattern Overview

### What kind of problem is this?

Use this pattern when:

```text
array does not change
        +
many contiguous range-sum queries
        ↓
PREFIX SUM
```

Instead of recalculating every range, precompute cumulative sums once.

### Core Idea

For a 1-indexed array:

```text
pref[0] = 0
pref[i] = pref[i-1] + a[i]

sum(L,R) = pref[R] - pref[L-1]
```

For a 0-indexed array with `pref` of size `n+1`:

```text
pref[0] = 0
pref[i+1] = pref[i] + a[i]

sum(L,R) = pref[R+1] - pref[L]
```

### Recognition Signals

```text
static array
many queries
sum from L to R
fixed window / centered range
original vs sorted range sums
```

### Complexity

```text
Build prefix: O(n)
Each query:   O(1)
Total:        O(n + q)
```

---

## Core Formula and Derivation

Wanted:

```text
a[L] + a[L+1] + ... + a[R]
```

But:

```text
pref[R]
= a[1] + ... + a[L-1] + a[L] + ... + a[R]

pref[L-1]
= a[1] + ... + a[L-1]
```

Subtract:

```text
pref[R] - pref[L-1]
= a[L] + ... + a[R]
```

Mental model:

```text
SUM UNTIL R - SUM BEFORE L
        =
SUM FROM L TO R
```

---

## Generic Dry Run

```text
index:   1   2   3   4   5
a:       2   4   1   5   3

pref:    0   2   6   7  12  15

Query [2,4]

pref[4] - pref[1]
= 12 - 2
= 10

Check:
4 + 1 + 5 = 10
```

### Generic Pseudocode

```text
pref[0] = 0

FOR i = 1..n:
    pref[i] = pref[i-1] + a[i]

FOR each query (L,R):
    answer = pref[R] - pref[L-1]
    output answer
```

---

# Problem 1 — Range Sum Query - Immutable

Problem Link: [Range Sum Query - Immutable](https://leetcode.com/problems/range-sum-query-immutable/description/)

### What is the problem asking?

You are given an array that **never changes**. `sumRange(L,R)` may be called many times, and each call must return the sum of elements from index `L` to `R`.

```text
Given:
a[0..n-1]

Need:
a[L] + a[L+1] + ... + a[R]
```

### Observation

```text
STATIC ARRAY + MANY RANGE SUMS
              ↓
         PREFIX SUM
```

Use:

```text
sum(L,R) = pref[R+1] - pref[L]
```

### Compact Algebra Derivation

```text
Wanted:
a[L] + ... + a[R]

pref[R+1]
= a[0] + ... + a[L-1] + a[L] + ... + a[R]

pref[L]
= a[0] + ... + a[L-1]

Subtract:
pref[R+1] - pref[L]
= a[L] + ... + a[R]
```

### Simple Dry Run

```text
a    = [-2, 0, 3, -5, 2, -1]
pref = [ 0,-2,-2,  1,-4,-2,-3]

Query:
L = 2, R = 4

pref[R+1] - pref[L]
= pref[5] - pref[2]
= -2 - (-2)
= 0

Check:
3 + (-5) + 2 = 0
```

### Pseudocode

```text
BUILD:
pref[0] = 0

FOR i = 0..n-1:
    pref[i+1] = pref[i] + a[i]

QUERY(L,R):
    RETURN pref[R+1] - pref[L]
```

### C++

```cpp
class NumArray {
    vector<long long> pref;

public:
    NumArray(vector<int>& nums) {
        int n = nums.size();
        pref.assign(n + 1, 0);

        for (int i = 0; i < n; ++i) {
            pref[i + 1] = pref[i] + nums[i];
        }
    }

    int sumRange(int left, int right) {
        return static_cast<int>(
            pref[right + 1] - pref[left]
        );
    }
};
```

### Complexity

```text
Build: O(n)
Query: O(1)
Space: O(n)
```

---

# Problem 2 — K Radius Subarray Averages

Problem Link: [K Radius Subarray Averages](https://leetcode.com/problems/k-radius-subarray-averages/)

### What is the problem asking?

For every index `i`, take `k` elements to its left, the element itself, and `k` elements to its right. If that complete window exists, output its integer average; otherwise output `-1`.

```text
center = i

L = i-k
R = i+k

window size = 2k+1
```

### Observation

Each valid answer needs the sum of a fixed contiguous range:

```text
sum(i-k, i+k)
```

So prefix sum gives the window sum in `O(1)`.

Formula:

```text
answer[i]
=
(pref[i+k+1] - pref[i-k]) / (2k+1)
```

Valid only when:

```text
i-k >= 0
i+k < n
```

### Compact Algebra Derivation

```text
L = i-k
R = i+k

window size
= R-L+1
= (i+k) - (i-k) + 1
= 2k+1

range sum
= pref[R+1] - pref[L]
= pref[i+k+1] - pref[i-k]

average
= (pref[i+k+1] - pref[i-k]) / (2k+1)
```

### Simple Dry Run

```text
a = [7,4,3,9,1]
k = 1

pref = [0,7,11,14,23,24]
window = 3

i = 2

L = 1
R = 3

sum
= pref[4] - pref[1]
= 23 - 7
= 16

average
= 16 / 3
= 5
```

Boundary:

```text
i = 0

L = -1
window does not fit

answer[0] = -1
```

### Pseudocode

```text
answer = array filled with -1
window = 2*k + 1

IF window > n:
    RETURN answer

build prefix sum

FOR i = k while i+k < n:
    L = i-k
    R = i+k

    sum = pref[R+1] - pref[L]
    answer[i] = sum / window

RETURN answer
```

### C++

```cpp
class Solution {
public:
    vector<int> getAverages(vector<int>& nums, int k) {
        int n = nums.size();
        vector<int> ans(n, -1);

        long long window = 2LL * k + 1;

        if (window > n) {
            return ans;
        }

        vector<long long> pref(n + 1, 0);

        for (int i = 0; i < n; ++i) {
            pref[i + 1] = pref[i] + nums[i];
        }

        for (int i = k; i + k < n; ++i) {
            int L = i - k;
            int R = i + k;

            long long sum = pref[R + 1] - pref[L];
            ans[i] = static_cast<int>(sum / window);
        }

        return ans;
    }
};
```

### Complexity

```text
Time:  O(n)
Space: O(n)
```

---

# Problem 3 — Kuriyama Mirai's Stones

Problem Link: [Kuriyama Mirai's Stones](https://codeforces.com/problemset/problem/433/B)

### What is the problem asking?

You have one array of stone prices. Queries ask for a range sum either:

```text
type 1 -> in the ORIGINAL array
type 2 -> in the SORTED array
```

So the real problem is:

```text
A = original values
B = sorted(A)

type 1: sum A[L..R]
type 2: sum B[L..R]
```

### Observation

There are **two static arrays**, so build two prefix sums:

```text
A      -> prefA
sorted -> prefB
```

Then:

```text
type 1 -> prefA[R] - prefA[L-1]
type 2 -> prefB[R] - prefB[L-1]
```

### Compact Algebra Derivation

```text
Original array:
sumA(L,R) = prefA[R] - prefA[L-1]

Sorted array:
sumB(L,R) = prefB[R] - prefB[L-1]

Therefore:

type 1 -> prefA[R] - prefA[L-1]
type 2 -> prefB[R] - prefB[L-1]
```

The algebra is unchanged; only the array representation changes.

### Simple Dry Run

```text
A = [6,4,2,7]
B = [2,4,6,7]

prefA = [0,6,10,12,19]
prefB = [0,2, 6,12,19]

Query:
L = 2, R = 3
```

Original:

```text
type = 1

prefA[3] - prefA[1]
= 12 - 6
= 6

range = [4,2]
```

Sorted:

```text
type = 2

prefB[3] - prefB[1]
= 12 - 2
= 10

range = [4,6]
```

### Pseudocode

```text
A = input array
B = sorted copy of A

build prefA
build prefB

FOR each query(type,L,R):

    IF type == 1:
        answer = prefA[R] - prefA[L-1]
    ELSE:
        answer = prefB[R] - prefB[L-1]

    output answer
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

    vector<long long> A(n + 1);
    vector<long long> B(n + 1);

    for (int i = 1; i <= n; ++i) {
        cin >> A[i];
        B[i] = A[i];
    }

    sort(B.begin() + 1, B.end());

    vector<long long> prefA(n + 1, 0);
    vector<long long> prefB(n + 1, 0);

    for (int i = 1; i <= n; ++i) {
        prefA[i] = prefA[i - 1] + A[i];
        prefB[i] = prefB[i - 1] + B[i];
    }

    int q;
    cin >> q;

    while (q--) {
        int type, L, R;
        cin >> type >> L >> R;

        if (type == 1) {
            cout << prefA[R] - prefA[L - 1] << '\n';
        } else {
            cout << prefB[R] - prefB[L - 1] << '\n';
        }
    }
}
```

### Complexity

```text
Sort:    O(n log n)
Prefix:  O(n)
Queries: O(q)

Total: O(n log n + q)
```

---

# Problem 4 — Static Range Sum Queries

Problem Link: [Static Range Sum Queries](https://cses.fi/problemset/task/1646)

### What is the problem asking?

You are given a static array and many queries `[L,R]`. For each query, output the sum of all values between `L` and `R`.

```text
Need:

A[L] + A[L+1] + ... + A[R]
```

### Observation

This is the direct prefix-sum form:

```text
STATIC ARRAY
+
MANY RANGE SUM QUERIES
        ↓
PREFIX SUM
```

Formula:

```text
sum(L,R) = pref[R] - pref[L-1]
```

### Compact Algebra Derivation

```text
Wanted:
A[L] + ... + A[R]

pref[R]
= A[1] + ... + A[L-1] + A[L] + ... + A[R]

pref[L-1]
= A[1] + ... + A[L-1]

Subtract:
pref[R] - pref[L-1]
= A[L] + ... + A[R]
```

### Simple Dry Run

```text
A = [3,2,4,5,1]

pref = [0,3,5,9,14,15]

Query:
L = 2, R = 4

pref[4] - pref[1]
= 14 - 3
= 11

Check:
2 + 4 + 5 = 11
```

### Pseudocode

```text
pref[0] = 0

FOR i = 1..n:
    pref[i] = pref[i-1] + A[i]

FOR each query(L,R):
    output pref[R] - pref[L-1]
```

### C++

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, q;
    cin >> n >> q;

    vector<long long> pref(n + 1, 0);

    for (int i = 1; i <= n; ++i) {
        long long x;
        cin >> x;
        pref[i] = pref[i - 1] + x;
    }

    while (q--) {
        int L, R;
        cin >> L >> R;

        cout << pref[R] - pref[L - 1] << '\n';
    }
}
```

### Complexity

```text
Build:   O(n)
Queries: O(q)
Total:   O(n + q)
Space:   O(n)
```

---

# Fast Revision Model

## One Formula

```text
1-indexed:
sum(L,R) = pref[R] - pref[L-1]

0-indexed with pref[n+1]:
sum(L,R) = pref[R+1] - pref[L]
```

## What changes between the problems?

| Problem | What is actually being asked? | Extra idea |
|---|---|---|
| Range Sum Query - Immutable | Repeatedly sum `[L,R]` in one fixed array | 0-indexed prefix |
| K Radius Subarray Averages | Sum the centered window `[i-k,i+k]`, then divide by `2k+1` | Boundary check + average |
| Kuriyama Mirai's Stones | Sum `[L,R]` in original or sorted order | Two prefix arrays |
| Static Range Sum Queries | Repeatedly sum `[L,R]` in one fixed array | Pure basic form |

## 5-Minute Recognition

```text
1. WHAT?
   Need sum of a contiguous interval.

2. CHANGES?
   Array is static.

3. HOW MANY?
   Many queries / many windows.

4. MODEL
   Range = big prefix - unwanted prefix.

5. FORMULA
   pref[R] - pref[L-1]
```

## Recognition Rule

```text
STATIC + MANY CONTIGUOUS RANGE SUMS
                ↓
             PREFIX SUM
```

The four problems are intentionally similar because they teach the same base form. The important difference is only the **representation of the range**:

```text
[L,R]
[i-k,i+k]
original/sorted [L,R]
```

Once that is recognized, the prefix-sum mechanics should be automatic.
