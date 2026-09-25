# PREFIX SUM PATTERNS

## Pattern 1 — Basic Range Sum / Static Queries

Pattern Link: [Pattern 1 — Basic Range Sum / Static Queries](https://github.com/mthaiseer/springcloud-systemdesign-notes/blob/main/001_CP_DSA_NOTES_V2/002_PREFIXSUM/002_PREFIXSUM_DIFF_ARRAY_PATTERNWISE_PROBLEMS.md#pattern-1)

### Pattern Overview

- **When to Recognize:** The array does **not change**, but you must answer many queries asking for the sum of a contiguous range `[l, r]`. A direct loop over every query can become too slow.
- **Core Idea:** Precompute cumulative sums once. Let `pref[i]` store the sum of the first `i` elements. Then any range sum is obtained by subtracting the prefix before the range: `sum(l, r) = pref[r] - pref[l - 1]`.
- **Why It Works:** `pref[r]` contains everything from index `1..r`; subtracting `pref[l - 1]` removes everything before `l`, leaving exactly `l..r`.
- **Standard Form:** Use a 1-indexed prefix array with `pref[0] = 0` and `pref[i] = pref[i - 1] + a[i]`.
- **Complexity:** Prefix construction takes `O(n)`. Each range query takes `O(1)`, so `q` queries take `O(n + q)` total instead of `O(nq)`.
- **Contest Signal:** Words such as **static array**, **many queries**, **sum from l to r**, **average of a fixed-radius window**, or **original vs sorted range sums** strongly suggest this pattern.

### Algebraic Derivation — Why `pref[r] - pref[l - 1]`?

Suppose we use a **1-indexed** array:

```text
a = [2, 4, 1, 5, 3]

Query:
l = 2
r = 4

Wanted:
a[2] + a[3] + a[4]
= 4 + 1 + 5
= 10
```

By definition:

```text
pref[r]
= a[1] + a[2] + ... + a[r]
```

For `r = 4`:

```text
pref[4]
= a[1] + a[2] + a[3] + a[4]
= 2 + 4 + 1 + 5
= 12
```

Everything before `l` is:

```text
a[1] + a[2] + ... + a[l - 1]
= pref[l - 1]
```

For `l = 2`:

```text
pref[l - 1]
= pref[1]
= 2
```

Subtract:

```text
pref[4] - pref[1]
= (2 + 4 + 1 + 5) - 2
= 4 + 1 + 5
= 10
```

### Algebraic Cancellation

```text
pref[r]
= a[1] + ... + a[l - 1] + a[l] + ... + a[r]

pref[l - 1]
= a[1] + ... + a[l - 1]

pref[r] - pref[l - 1]
= [a[1] + ... + a[l - 1]] + [a[l] + ... + a[r]]
  - [a[1] + ... + a[l - 1]]

= a[l] + ... + a[r]
```

Therefore:

```text
rangeSum(l, r)
= pref[r] - pref[l - 1]

Mental model:

SUM UNTIL r - SUM BEFORE l = SUM FROM l TO r
```

### 0-Indexed Prefix Form

If `pref[i]` means the sum of the **first `i` elements**:

```text
pref[0] = 0
pref[i + 1] = pref[i] + a[i]
```

For range `[left, right]`:

```text
pref[right + 1]
= a[0] + ... + a[left - 1] + a[left] + ... + a[right]

pref[left]
= a[0] + ... + a[left - 1]
```

Subtract:

```text
pref[right + 1] - pref[left]
= a[left] + ... + a[right]
```

So:

```text
0-indexed: sum(left, right) = pref[right + 1] - pref[left]
1-indexed: sum(l, r)        = pref[r] - pref[l - 1]
```

---

### Generic Visual

```text
Array index:    1   2   3   4   5
arr:            2   4   1   5   3

pref[0] = 0
pref:           0   2   6   7  12  15

Query [2, 4]:

pref[4] - pref[1]
   12   -    2
       = 10

Equivalent range:
4 + 1 + 5 = 10
```

### Complete C++ — Generic Static Range Sum

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
        int l, r;
        cin >> l >> r;

        long long rangeSum = pref[r] - pref[l - 1];
        cout << rangeSum << '
';
    }

    return 0;
}
```

---

---

## Problem 1 — Range Sum Query - Immutable

Problem Link: [Range Sum Query - Immutable](https://leetcode.com/problems/range-sum-query-immutable/description/)

### A. Remove Story Nouns

```text
immutable integer array  -> static array
sumRange(left, right)    -> sum of values in interval [left, right]
many calls               -> many range queries
```

Reduced mathematical problem:

```text
Given a static array a[0..n-1].

For many queries [L,R], compute:

a[L] + a[L+1] + ... + a[R]

Need each query faster than O(n).
```

### B. Extract Variables

```text
n       = number of elements
a[i]    = value at index i
L       = left query boundary
R       = right query boundary
pref[i] = sum of first i elements
```

Prefix definition:

```text
pref[0] = 0
pref[i+1] = pref[i] + a[i]
```

Target:

```text
rangeSum(L,R)
```

### C. Identify the Mathematical Form

```text
STATIC ARRAY
     +
MANY RANGE SUM QUERIES
     ↓
PREFIX SUM
```

We need:

```text
sum(L,R)
=
sum(0,R) - sum(0,L-1)
```

With the `n+1` prefix convention:

```text
sum(L,R)
=
pref[R+1] - pref[L]
```

### D. Algebraic Derivation

Start from the quantity we want:

```text
a[L] + a[L+1] + ... + a[R]
```

Prefix through `R`:

```text
pref[R+1]
=
a[0] + a[1] + ... + a[L-1]
+
a[L] + ... + a[R]
```

Prefix before `L`:

```text
pref[L]
=
a[0] + a[1] + ... + a[L-1]
```

Subtract:

```text
pref[R+1] - pref[L]

=
[a[0] + ... + a[L-1] + a[L] + ... + a[R]]
-
[a[0] + ... + a[L-1]]

=
a[L] + ... + a[R]
```

Therefore:

```text
sum(L,R) = pref[R+1] - pref[L]
```

### E. Dry Run — Different Cases

Use:

```text
a    = [-2, 0, 3, -5, 2, -1]
index   0  1  2   3  4   5

pref = [0, -2, -2, 1, -4, -2, -3]
```

#### Case 1 — Range starts at index 0

```text
L = 0
R = 2

sum(0,2)
= pref[3] - pref[0]
= 1 - 0
= 1

check:
-2 + 0 + 3 = 1
```

#### Case 2 — Middle range

```text
L = 2
R = 4

sum(2,4)
= pref[5] - pref[2]
= -2 - (-2)
= 0

check:
3 + (-5) + 2 = 0
```

#### Case 3 — Single element

```text
L = 3
R = 3

sum(3,3)
= pref[4] - pref[3]
= -4 - 1
= -5
```

#### Case 4 — Entire array

```text
L = 0
R = 5

sum(0,5)
= pref[6] - pref[0]
= -3
```

### F. Complexity

```text
build prefix = O(n)
each query   = O(1)
q queries    = O(q)

total        = O(n + q)
space        = O(n)
```

### G. Complete C++

```cpp
class NumArray {
private:
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

---

## Problem 2 — K Radius Subarray Averages

Problem Link: [K Radius Subarray Averages](https://leetcode.com/problems/k-radius-subarray-averages/)

### A. Remove Story Nouns

```text
k-radius average      -> fixed-size centered range
center i              -> current index
radius k              -> take k values left and k values right
invalid center        -> complete range does not fit
average               -> range sum / number of elements
```

Reduced mathematical problem:

```text
For every index i:

left  = i-k
right = i+k

If [left,right] is inside the array:

answer[i]
=
sum(left,right) / numberOfElements

Otherwise:

answer[i] = -1
```

### B. Extract Variables

```text
n       = array length
a[i]    = value at index i
i       = current center
k       = radius
L       = i-k
R       = i+k
w       = window size
pref[i] = prefix sum
```

### C. Identify the Mathematical Form

The interval around `i` is:

```text
i-k ........ i ........ i+k
 ↑                         ↑
 L                         R
```

Number of elements:

```text
w = R-L+1
```

Substitute:

```text
w
= (i+k) - (i-k) + 1
= i+k-i+k+1
= 2k+1
```

So:

```text
answer[i]
=
sum(i-k, i+k) / (2k+1)
```

### D. Algebraic Derivation

Prefix range formula:

```text
sum(L,R)
=
pref[R+1] - pref[L]
```

Substitute:

```text
L = i-k
R = i+k
```

Then:

```text
sum
=
pref[(i+k)+1] - pref[i-k]

=
pref[i+k+1] - pref[i-k]
```

Therefore:

```text
answer[i]
=
(pref[i+k+1] - pref[i-k])
/
(2k+1)
```

Now derive when the center is valid.

Need:

```text
L >= 0
R < n
```

Substitute:

```text
i-k >= 0
i+k < n
```

Therefore only those `i` can receive an average.

### E. Dry Run — Different Cases

Use:

```text
a = [7, 4, 3, 9, 1]
k = 1

pref = [0, 7, 11, 14, 23, 24]

window = 2k+1
       = 3
```

#### Case 1 — Invalid left boundary

```text
i = 0

L = 0-1 = -1
R = 0+1 = 1

L < 0

answer[0] = -1
```

#### Case 2 — Valid center

```text
i = 1

L = 0
R = 2

sum
= pref[3] - pref[0]
= 14

average
= 14 / 3
= 4
```

#### Case 3 — Valid middle center

```text
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

#### Case 4 — Invalid right boundary

```text
i = 4

L = 3
R = 5

R >= n

answer[4] = -1
```

Final:

```text
[-1, 4, 5, 4, -1]
```

#### Case 5 — k = 0

```text
k = 0

L = i
R = i
w = 1

answer[i]
= sum(i,i) / 1
= a[i]
```

### F. Complexity

```text
prefix build = O(n)
all centers  = O(n)

total        = O(n)
space        = O(n)
```

### G. Complete C++

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

---

## Problem 3 — Kuriyama Mirai's Stones

Problem Link: [Kuriyama Mirai's Stones](https://codeforces.com/problemset/problem/433/B)

### A. Remove Story Nouns

```text
stones / prices         -> array values
original order          -> array A
sorted prices           -> sorted copy B
query type 1            -> range sum on A
query type 2            -> range sum on B
many queries            -> prefix sums
```

Reduced mathematical problem:

```text
Given array A.

Create:
B = sorted(A)

For each query (type,L,R):

type 1 -> sum A[L..R]
type 2 -> sum B[L..R]
```

### B. Extract Variables

```text
n             = number of values
A[i]          = original array
B[i]          = sorted array
type          = query representation
L,R           = query boundaries
prefA[i]      = prefix sum of A
prefB[i]      = prefix sum of B
```

### C. Identify the Mathematical Form

There are two static representations:

```text
             values
               |
       -----------------
       |               |
   original          sorted
       |               |
    prefA            prefB
       |               |
 type = 1          type = 2
```

The range formula itself does not change:

```text
range(L,R)
=
pref[R] - pref[L-1]
```

Only the prefix array changes.

### D. Algebraic Derivation

Original representation:

```text
prefA[R]
=
A[1] + ... + A[L-1]
+
A[L] + ... + A[R]

prefA[L-1]
=
A[1] + ... + A[L-1]
```

Subtract:

```text
prefA[R] - prefA[L-1]
=
A[L] + ... + A[R]
```

Sorted representation:

```text
prefB[R]
=
B[1] + ... + B[L-1]
+
B[L] + ... + B[R]

prefB[L-1]
=
B[1] + ... + B[L-1]
```

Therefore:

```text
prefB[R] - prefB[L-1]
=
B[L] + ... + B[R]
```

Final mathematical model:

```text
type = 1
→ prefA[R] - prefA[L-1]

type = 2
→ prefB[R] - prefB[L-1]
```

### E. Dry Run — Different Cases

Use:

```text
A = [6, 4, 2, 7]
B = [2, 4, 6, 7]

prefA = [0, 6, 10, 12, 19]
prefB = [0, 2,  6, 12, 19]
```

#### Case 1 — Same range, original order

```text
type = 1
L = 2
R = 3

answer
= prefA[3] - prefA[1]
= 12 - 6
= 6

check:
4 + 2 = 6
```

#### Case 2 — Same range, sorted order

```text
type = 2
L = 2
R = 3

answer
= prefB[3] - prefB[1]
= 12 - 2
= 10

check:
4 + 6 = 10
```

#### Case 3 — Entire array

```text
L = 1
R = 4

type 1:
prefA[4] - prefA[0]
= 19

type 2:
prefB[4] - prefB[0]
= 19
```

Sorting changes positions but not the total sum.

#### Case 4 — Single element

```text
type = 1
L = R = 3

prefA[3] - prefA[2]
= 12 - 10
= 2
```

### F. Complexity

```text
sort B           = O(n log n)
build prefixes   = O(n)
each query       = O(1)

total            = O(n log n + q)
space            = O(n)
```

### G. Complete C++

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

    return 0;
}
```

---

## Problem 4 — Static Range Sum Queries

Problem Link: [Static Range Sum Queries](https://cses.fi/problemset/task/1646)

### A. Remove Story Nouns

```text
values                  -> array A
queries                 -> intervals [L,R]
sum from position a..b  -> range sum
array never changes     -> static array
```

Reduced mathematical problem:

```text
Given static A[1..n].

For every query [L,R], compute:

A[L] + A[L+1] + ... + A[R]
```

### B. Extract Variables

```text
n       = array size
q       = number of queries
A[i]    = value at position i
L,R     = query boundaries
pref[i] = sum A[1..i]
```

### C. Identify the Mathematical Form

```text
many queries
     +
static values
     +
contiguous sum
     ↓
prefix sum
```

Define:

```text
pref[0] = 0

pref[i]
=
pref[i-1] + A[i]
```

### D. Algebraic Derivation

Wanted:

```text
A[L] + A[L+1] + ... + A[R]
```

But:

```text
pref[R]
=
A[1] + ... + A[L-1]
+
A[L] + ... + A[R]
```

And:

```text
pref[L-1]
=
A[1] + ... + A[L-1]
```

Subtract:

```text
pref[R] - pref[L-1]

=
[A[1] + ... + A[L-1] + A[L] + ... + A[R]]
-
[A[1] + ... + A[L-1]]

=
A[L] + ... + A[R]
```

Therefore:

```text
sum(L,R)
=
pref[R] - pref[L-1]
```

### E. Dry Run — Different Cases

Use:

```text
A = [3, 2, 4, 5, 1]

pref = [0, 3, 5, 9, 14, 15]
```

#### Case 1 — Middle range

```text
L = 2
R = 4

answer
= pref[4] - pref[1]
= 14 - 3
= 11

check:
2 + 4 + 5 = 11
```

#### Case 2 — Starts at 1

```text
L = 1
R = 3

answer
= pref[3] - pref[0]
= 9 - 0
= 9
```

This is why:

```text
pref[0] = 0
```

is useful.

#### Case 3 — Single element

```text
L = 4
R = 4

answer
= pref[4] - pref[3]
= 14 - 9
= 5
```

#### Case 4 — Entire array

```text
L = 1
R = 5

answer
= pref[5] - pref[0]
= 15
```

### F. Complexity

```text
prefix build = O(n)
each query   = O(1)
q queries    = O(q)

total        = O(n + q)
space        = O(n)
```

### G. Complete C++

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

    return 0;
}
```

---

## Fast Revision Model

For this entire form, remember:

```text
1. REMOVE STORY
      ↓
   static array + many interval sums

2. DEFINE VARIABLES
      ↓
   L, R, pref[]

3. WRITE TARGET
      ↓
   A[L] + ... + A[R]

4. FIND BIGGER KNOWN QUANTITY
      ↓
   pref[R]

5. REMOVE UNWANTED PART
      ↓
   pref[L-1]

6. ALGEBRA
      ↓
   pref[R] - pref[L-1]

7. IMPLEMENT
      ↓
   O(n) build + O(1) query
```

### Recognition Rule

```text
STATIC + MANY CONTIGUOUS RANGE SUMS
                ↓
            PREFIX SUM
```

### Indexing Rule

```text
1-indexed:
sum(L,R)
=
pref[R] - pref[L-1]

0-indexed with pref size n+1:
sum(L,R)
=
pref[R+1] - pref[L]
```
