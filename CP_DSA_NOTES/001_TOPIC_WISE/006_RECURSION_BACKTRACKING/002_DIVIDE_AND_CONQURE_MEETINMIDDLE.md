# 🚀 Divide & Conquer + Meet in the Middle — Phase-Wise Problem Handbook
## CP + FAANG Edition with Clickable Index, Input/Output, C++ Code, and Index-by-Index Dry Runs

> This version replaces repeated generic sections with **real phase-wise problems** and **pattern-focused notes**.

---

# 📚 Clickable Index

## Core Maps
- [0. Master Pattern Map](#0-master-pattern-map)
- [0.1 D&C vs MITM Decision Tree](#01-dc-vs-mitm-decision-tree)
- [0.2 Complexity Cheat Sheet](#02-complexity-cheat-sheet)


## Phase 1 — Divide & Conquer Foundations

- [Problem 1: Merge Sort](#problem-1-merge-sort) — Easy — `Split array → sort left → sort right → merge`
- [Problem 2: Binary Search as Divide & Conquer](#problem-2-binary-search-as-divide-conquer) — Easy — `Sorted search space → remove half each step`

## Phase 2 — Merge Step Counting

- [Problem 3: Count Inversions](#problem-3-count-inversions) — Medium — `Merge Sort + count cross inversions`
- [Problem 4: Reverse Pairs](#problem-4-reverse-pairs) — Medium/Hard — `Merge Sort + count before merge`
- [Problem 5: Bubble Sort Swap Parity](#problem-5-bubble-sort-swap-parity) — Medium — `Inversion parity`

## Phase 3 — Fast Multiplication

- [Problem 6: Karatsuba Multiplication](#problem-6-karatsuba-multiplication) — Medium — `Reduce 4 recursive multiplications to 3`

## Phase 4 — Meet in the Middle Foundations

- [Problem 7: Generate All Subset Sums](#problem-7-generate-all-subset-sums) — Easy — `Bitmask enumeration`
- [Problem 8: Subset Sum Exists](#problem-8-subset-sum-exists) — Medium — `MITM + binary search`

## Phase 5 — MITM Optimization Problems

- [Problem 9: Maximum Subset Sum Less Than or Equal to S](#problem-9-maximum-subset-sum-less-than-or-equal-to-s) — Medium — `MITM + upper_bound`
- [Problem 10: Count Subsets With Sum Less Than or Equal to K](#problem-10-count-subsets-with-sum-less-than-or-equal-to-k) — Medium — `MITM + upper_bound count`

## Phase 6 — Pair Sum MITM

- [Problem 11: Classical Four Number Sum](#problem-11-classical-four-number-sum) — Medium — `Pair sums + hash map`
- [Problem 12: CSES Four Values](#problem-12-cses-four-values) — Medium — `Pair sum + store indices`

## Phase 7 — Modulo MITM

- [Problem 13: Maximum Subsequence Sum Modulo M](#problem-13-maximum-subsequence-sum-modulo-m) — Hard — `MITM + modulo + upper_bound`

## Phase 8 — Advanced Transformation

- [Problem 14: 4 Reversals Pattern](#problem-14-4-reversals-pattern) — Hard — `State transformation + limited operations`

---

# 0. Master Pattern Map

| Topic | Main Idea | Best Trigger |
|---|---|---|
| Divide & Conquer | Split → solve recursively → merge | Problem naturally splits into halves |
| Merge Sort Counting | Count information while merging sorted halves | Pair counting / inversions / reverse pairs |
| Karatsuba | Reduce recursive multiplications | Large number multiplication |
| Meet in the Middle | Split exponential search into two halves | `n ≤ 40`, subset/combinations |
| Pair Sum MITM | Precompute pair sums | Four-sum / k-sum variants |
| Modulo MITM | Store subset sums modulo `m` | Max subset modulo problems |

---

# 0.1 D&C vs MITM Decision Tree

```text
Is the problem recursive over a range?
        |
        +-- YES --> Can merge answers from left and right?
        |               |
        |               +-- YES --> Divide & Conquer
        |
        +-- NO --> Is it subset/combinations with n around 30-45?
                        |
                        +-- YES --> Meet in the Middle
```

---

# 0.2 Complexity Cheat Sheet

| Pattern | Brute Force | Optimized |
|---|---:|---:|
| Merge Sort | O(n²) sorting alternatives | O(n log n) |
| Count Inversions | O(n²) | O(n log n) |
| Reverse Pairs | O(n²) | O(n log n) |
| Four Sum | O(n⁴) | O(n²) |
| Subset Sum n=40 | O(2⁴⁰) | O(2²⁰ log 2²⁰) |
| Max Subset Modulo | O(2ⁿ) | O(2^(n/2) log 2^(n/2)) |

---


# Phase 1 — Divide & Conquer Foundations


# Problem 1: Merge Sort

**Difficulty:** Easy  

**Pattern:** `Split array → sort left → sort right → merge`


## 🧾 Problem Statement

Given an array of integers, sort it in non-decreasing order using divide and conquer.


## 📥 Input Example

```text
n = 6
arr = [5, 3, 8, 1, 2, 7]
```


## 📤 Expected Output

```text
[1, 2, 3, 5, 7, 8]
```


## 🧠 Brute Force Thinking

Use repeated selection of minimum element or bubble sort. Complexity O(n²).


## ⚡ Optimal Idea

Merge Sort splits array into two halves, sorts each half recursively, then merges two sorted halves in O(n).


## 💻 C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

void mergeSort(vector<int>& arr, int l, int r) {
    if (l >= r) return;

    int mid = l + (r - l) / 2;

    mergeSort(arr, l, mid);
    mergeSort(arr, mid + 1, r);

    vector<int> temp;
    int i = l, j = mid + 1;

    while (i <= mid && j <= r) {
        if (arr[i] <= arr[j]) temp.push_back(arr[i++]);
        else temp.push_back(arr[j++]);
    }

    while (i <= mid) temp.push_back(arr[i++]);
    while (j <= r) temp.push_back(arr[j++]);

    for (int k = l; k <= r; k++) {
        arr[k] = temp[k - l];
    }
}
```


## 📊 Index-by-Index Dry Run

| Step | State / Index | Observation | Action |
|---:|---|---|---|

| 0 | Initial array | [5,3,8,1,2,7] | Split into [5,3,8] and [1,2,7] |

| 1 | Left half | [5,3,8] | Split into [5] and [3,8] |

| 2 | Sort [3,8] | 3 <= 8 | Merged as [3,8] |

| 3 | Merge [5] + [3,8] | 3 < 5 | Take 3 |

| 4 | Continue | 5 < 8 | Take 5, then 8 → [3,5,8] |

| 5 | Right half | [1,2,7] | Already becomes [1,2,7] after merges |

| 6 | Final merge | [3,5,8] + [1,2,7] | Take 1,2,3,5,7,8 |


## ⏱ Complexity

Time O(n log n), Space O(n).


## 🧩 Pattern Trigger

Use this when the problem can be solved by **splitting a range**, solving halves, and merging useful information.


---


# Problem 2: Binary Search as Divide & Conquer

**Difficulty:** Easy  

**Pattern:** `Sorted search space → remove half each step`


## 🧾 Problem Statement

Given a sorted array and target x, return the index of x or -1 if not found.


## 📥 Input Example

```text
arr = [1, 3, 5, 7, 9, 11]
x = 7
```


## 📤 Expected Output

```text
3
```


## 🧠 Brute Force Thinking

Scan every index. Complexity O(n).


## ⚡ Optimal Idea

Because array is sorted, compare target with middle. If target is smaller, search left half; otherwise search right half.


## 💻 C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int binarySearch(vector<int>& arr, int x) {
    int l = 0, r = (int)arr.size() - 1;

    while (l <= r) {
        int mid = l + (r - l) / 2;

        if (arr[mid] == x) return mid;
        else if (arr[mid] < x) l = mid + 1;
        else r = mid - 1;
    }

    return -1;
}
```


## 📊 Index-by-Index Dry Run

| Step | State / Index | Observation | Action |
|---:|---|---|---|

| 0 | l=0 r=5 | mid=2 arr[mid]=5 | 5 < 7 → move right |

| 1 | l=3 r=5 | mid=4 arr[mid]=9 | 9 > 7 → move left |

| 2 | l=3 r=3 | mid=3 arr[mid]=7 | Found target |


## ⏱ Complexity

Time O(log n), Space O(1).


## 🧩 Pattern Trigger

Use this when the problem can be solved by **splitting a range**, solving halves, and merging useful information.


---


# Phase 2 — Merge Step Counting


# Problem 3: Count Inversions

**Difficulty:** Medium  

**Pattern:** `Merge Sort + count cross inversions`


## 🧾 Problem Statement

Count pairs (i, j) such that i < j and arr[i] > arr[j].


## 📥 Input Example

```text
arr = [5, 3, 2, 4, 1]
```


## 📤 Expected Output

```text
8
```


## 🧠 Brute Force Thinking

Check all pairs i < j. Complexity O(n²).


## ⚡ Optimal Idea

During merge, if left[i] > right[j], then all elements from i to mid in left half are greater than right[j]. Add mid - i + 1.


## 💻 C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countInv(vector<int>& arr, int l, int r) {
    if (l >= r) return 0;

    int mid = l + (r - l) / 2;
    long long ans = 0;

    ans += countInv(arr, l, mid);
    ans += countInv(arr, mid + 1, r);

    vector<int> temp;
    int i = l, j = mid + 1;

    while (i <= mid && j <= r) {
        if (arr[i] <= arr[j]) {
            temp.push_back(arr[i++]);
        } else {
            ans += (mid - i + 1);
            temp.push_back(arr[j++]);
        }
    }

    while (i <= mid) temp.push_back(arr[i++]);
    while (j <= r) temp.push_back(arr[j++]);

    for (int k = l; k <= r; k++) arr[k] = temp[k - l];

    return ans;
}
```


## 📊 Index-by-Index Dry Run

| Step | State / Index | Observation | Action |
|---:|---|---|---|

| 0 | Array | [5,3,2,4,1] | Split into [5,3,2] and [4,1] |

| 1 | Sort/count [5,3] | 5 > 3 | Add 1 |

| 2 | Merge [3,5] with [2] | 3 > 2 | Add 2 because [3,5] both > 2 |

| 3 | Left inversions | [5,3,2] | Total left = 3 |

| 4 | Right [4,1] | 4 > 1 | Add 1 |

| 5 | Final merge [2,3,5] + [1,4] | 2 > 1 | Add 3 |

| 6 | Continue | 5 > 4 | Add 1 |

| 7 | Total | 3 + 1 + 4 | 8 |


## ⏱ Complexity

Time O(n log n), Space O(n).


## 🧩 Pattern Trigger

Use this when the problem can be solved by **splitting a range**, solving halves, and merging useful information.


---


# Problem 4: Reverse Pairs

**Difficulty:** Medium/Hard  

**Pattern:** `Merge Sort + count before merge`


## 🧾 Problem Statement

Count pairs (i, j) such that i < j and arr[i] > 2 * arr[j].


## 📥 Input Example

```text
arr = [1, 3, 2, 3, 1]
```


## 📤 Expected Output

```text
2
```


## 🧠 Brute Force Thinking

Check every pair. Complexity O(n²).


## ⚡ Optimal Idea

Before merging two sorted halves, for every i in left half, move pointer j in right half while arr[i] > 2*arr[j]. Add j - (mid+1).


## 💻 C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long reversePairs(vector<long long>& arr, int l, int r) {
    if (l >= r) return 0;

    int mid = l + (r - l) / 2;
    long long ans = 0;

    ans += reversePairs(arr, l, mid);
    ans += reversePairs(arr, mid + 1, r);

    int j = mid + 1;
    for (int i = l; i <= mid; i++) {
        while (j <= r && arr[i] > 2LL * arr[j]) j++;
        ans += j - (mid + 1);
    }

    vector<long long> temp;
    int i = l;
    j = mid + 1;

    while (i <= mid && j <= r) {
        if (arr[i] <= arr[j]) temp.push_back(arr[i++]);
        else temp.push_back(arr[j++]);
    }

    while (i <= mid) temp.push_back(arr[i++]);
    while (j <= r) temp.push_back(arr[j++]);

    for (int k = l; k <= r; k++) arr[k] = temp[k - l];

    return ans;
}
```


## 📊 Index-by-Index Dry Run

| Step | State / Index | Observation | Action |
|---:|---|---|---|

| 0 | Sorted halves example | left=[1,2,3], right=[1,3] | Count cross pairs |

| 1 | i=0 val=1 | 1 > 2*1? false | Add 0 |

| 2 | i=1 val=2 | 2 > 2*1? false | Add 0 |

| 3 | i=2 val=3 | 3 > 2*1? true | j moves one, add 1 |

| 4 | Other recursive pair | 3 > 2*1 | Add another 1 |

| 5 | Total | 2 | Answer |


## ⏱ Complexity

Time O(n log n), Space O(n).


## 🧩 Pattern Trigger

Use this when the problem can be solved by **splitting a range**, solving halves, and merging useful information.


---


# Problem 5: Bubble Sort Swap Parity

**Difficulty:** Medium  

**Pattern:** `Inversion parity`


## 🧾 Problem Statement

Given an array, determine whether the number of swaps bubble sort performs is even or odd.


## 📥 Input Example

```text
arr = [3, 1, 2]
```


## 📤 Expected Output

```text
Even
```


## 🧠 Brute Force Thinking

Simulate bubble sort and count swaps. Complexity O(n²).


## ⚡ Optimal Idea

Bubble sort swap count equals inversion count. So only count inversion parity.


## 💻 C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long mergeInv(vector<int>& arr, int l, int r) {
    if (l >= r) return 0;

    int mid = l + (r - l) / 2;
    long long inv = mergeInv(arr, l, mid) + mergeInv(arr, mid + 1, r);

    vector<int> temp;
    int i = l, j = mid + 1;

    while (i <= mid && j <= r) {
        if (arr[i] <= arr[j]) temp.push_back(arr[i++]);
        else {
            inv += mid - i + 1;
            temp.push_back(arr[j++]);
        }
    }

    while (i <= mid) temp.push_back(arr[i++]);
    while (j <= r) temp.push_back(arr[j++]);

    for (int k = l; k <= r; k++) arr[k] = temp[k - l];

    return inv;
}

string swapParity(vector<int> arr) {
    long long inv = mergeInv(arr, 0, (int)arr.size() - 1);
    return (inv % 2 == 0 ? "Even" : "Odd");
}
```


## 📊 Index-by-Index Dry Run

| Step | State / Index | Observation | Action |
|---:|---|---|---|

| 0 | Array | [3,1,2] | Inversions: (3,1), (3,2) |

| 1 | Count | 2 | Even |

| 2 | Bubble swaps | swap 3/1 → [1,3,2] | 1 swap |

| 3 | Bubble swaps | swap 3/2 → [1,2,3] | 2 swaps |

| 4 | Parity | 2 swaps | Even |


## ⏱ Complexity

Time O(n log n), Space O(n).


## 🧩 Pattern Trigger

Use this when the problem can be solved by **splitting a range**, solving halves, and merging useful information.


---


# Phase 3 — Fast Multiplication


# Problem 6: Karatsuba Multiplication

**Difficulty:** Medium  

**Pattern:** `Reduce 4 recursive multiplications to 3`


## 🧾 Problem Statement

Multiply two large integers faster than normal O(n²) multiplication.


## 📥 Input Example

```text
x = 1234
y = 5678
```


## 📤 Expected Output

```text
7006652
```


## 🧠 Brute Force Thinking

Grade-school multiplication uses four sub-products after splitting: ac, ad, bc, bd.


## ⚡ Optimal Idea

Use ac, bd, and (a+b)(c+d). Then middle term ad+bc = (a+b)(c+d) - ac - bd.


## 💻 C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long karatsuba(long long x, long long y) {
    if (x < 10 || y < 10) return x * y;

    int n = max((int)to_string(x).size(), (int)to_string(y).size());
    int m = n / 2;

    long long power = 1;
    for (int i = 0; i < m; i++) power *= 10;

    long long a = x / power;
    long long b = x % power;
    long long c = y / power;
    long long d = y % power;

    long long ac = karatsuba(a, c);
    long long bd = karatsuba(b, d);
    long long abcd = karatsuba(a + b, c + d);

    long long middle = abcd - ac - bd;

    return ac * power * power + middle * power + bd;
}
```


## 📊 Index-by-Index Dry Run

| Step | State / Index | Observation | Action |
|---:|---|---|---|

| 0 | Split x=1234 | a=12, b=34 | m=2 |

| 1 | Split y=5678 | c=56, d=78 | m=2 |

| 2 | ac | 12*56 | 672 |

| 3 | bd | 34*78 | 2652 |

| 4 | abcd | (12+34)*(56+78)=46*134 | 6164 |

| 5 | middle | 6164-672-2652 | 2840 |

| 6 | answer | 672*10000 + 2840*100 + 2652 | 7006652 |


## ⏱ Complexity

Time O(n^log2(3)) ≈ O(n^1.585).


## 🧩 Pattern Trigger

Use this when the problem can be solved by **splitting a range**, solving halves, and merging useful information.


---


# Phase 4 — Meet in the Middle Foundations


# Problem 7: Generate All Subset Sums

**Difficulty:** Easy  

**Pattern:** `Bitmask enumeration`


## 🧾 Problem Statement

Given a small array, generate all possible subset sums.


## 📥 Input Example

```text
arr = [2, 5, 7]
```


## 📤 Expected Output

```text
[0, 2, 5, 7, 7, 9, 12, 14]
```


## 🧠 Brute Force Thinking

Use recursion pick/not-pick. Equivalent to bitmask enumeration.


## ⚡ Optimal Idea

Each bit in mask represents whether index i is selected.


## 💻 C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<long long> subsetSums(vector<int>& arr) {
    int n = arr.size();
    vector<long long> sums;

    for (int mask = 0; mask < (1 << n); mask++) {
        long long sum = 0;

        for (int i = 0; i < n; i++) {
            if (mask & (1 << i)) {
                sum += arr[i];
            }
        }

        sums.push_back(sum);
    }

    return sums;
}
```


## 📊 Index-by-Index Dry Run

| Step | State / Index | Observation | Action |
|---:|---|---|---|

| 0 | mask=000 | {} | sum=0 |

| 1 | mask=001 | {2} | sum=2 |

| 2 | mask=010 | {5} | sum=5 |

| 3 | mask=011 | {2,5} | sum=7 |

| 4 | mask=100 | {7} | sum=7 |

| 5 | mask=101 | {2,7} | sum=9 |

| 6 | mask=110 | {5,7} | sum=12 |

| 7 | mask=111 | {2,5,7} | sum=14 |


## ⏱ Complexity

Time O(n * 2^n), Space O(2^n).


## 🧩 Pattern Trigger

Use this when the problem can be solved by **splitting a range**, solving halves, and merging useful information.


---


# Problem 8: Subset Sum Exists

**Difficulty:** Medium  

**Pattern:** `MITM + binary search`


## 🧾 Problem Statement

Given n ≤ 40 numbers and target S, determine whether any subset has sum exactly S.


## 📥 Input Example

```text
arr = [3, 34, 4, 12, 5, 2]
S = 9
```


## 📤 Expected Output

```text
YES  // subset [4,5]
```


## 🧠 Brute Force Thinking

Try all subsets: O(2^n), impossible for n=40.


## ⚡ Optimal Idea

Split into two halves. Generate sums of both halves. Sort right sums. For each left sum x, binary search S-x in right.


## 💻 C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<long long> gen(vector<int>& a) {
    int n = a.size();
    vector<long long> sums;

    for (int mask = 0; mask < (1 << n); mask++) {
        long long sum = 0;
        for (int i = 0; i < n; i++) {
            if (mask & (1 << i)) sum += a[i];
        }
        sums.push_back(sum);
    }

    return sums;
}

bool subsetSumExists(vector<int>& arr, long long S) {
    int n = arr.size();
    vector<int> left(arr.begin(), arr.begin() + n / 2);
    vector<int> right(arr.begin() + n / 2, arr.end());

    vector<long long> L = gen(left);
    vector<long long> R = gen(right);

    sort(R.begin(), R.end());

    for (long long x : L) {
        if (binary_search(R.begin(), R.end(), S - x)) {
            return true;
        }
    }

    return false;
}
```


## 📊 Index-by-Index Dry Run

| Step | State / Index | Observation | Action |
|---:|---|---|---|

| 0 | Split | left=[3,34,4], right=[12,5,2] | Target=9 |

| 1 | Left sums | 0,3,34,37,4,7,38,41 | Generated |

| 2 | Right sums | 0,12,5,17,2,14,7,19 | Sort it |

| 3 | Try x=0 | need=9 | not found |

| 4 | Try x=3 | need=6 | not found |

| 5 | Try x=4 | need=5 | found in right |

| 6 | Answer | YES | 4 + 5 = 9 |


## ⏱ Complexity

Time O(2^(n/2) log 2^(n/2)), Space O(2^(n/2)).


## 🧩 Pattern Trigger

Use this when the problem has **subset/combinations**, `n` is too large for `2^n`, but small enough for `2^(n/2)`.


---


# Phase 5 — MITM Optimization Problems


# Problem 9: Maximum Subset Sum Less Than or Equal to S

**Difficulty:** Medium  

**Pattern:** `MITM + upper_bound`


## 🧾 Problem Statement

Find maximum subset sum ≤ S for n ≤ 40.


## 📥 Input Example

```text
arr = [3, 34, 4, 12, 5, 2]
S = 10
```


## 📤 Expected Output

```text
10  // subset [3,5,2] or [4,5]
```


## 🧠 Brute Force Thinking

Try all subsets and take max ≤ S. Complexity O(2^n).


## ⚡ Optimal Idea

Split array, generate sums. For each left sum x, find largest right sum ≤ S-x using upper_bound.


## 💻 C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<long long> genSums(vector<int>& a) {
    int n = a.size();
    vector<long long> sums;

    for (int mask = 0; mask < (1 << n); mask++) {
        long long sum = 0;
        for (int i = 0; i < n; i++) {
            if (mask & (1 << i)) sum += a[i];
        }
        sums.push_back(sum);
    }

    return sums;
}

long long maxSubsetLE(vector<int>& arr, long long S) {
    int n = arr.size();

    vector<int> left(arr.begin(), arr.begin() + n / 2);
    vector<int> right(arr.begin() + n / 2, arr.end());

    vector<long long> L = genSums(left);
    vector<long long> R = genSums(right);

    sort(R.begin(), R.end());

    long long ans = 0;

    for (long long x : L) {
        if (x > S) continue;

        long long need = S - x;

        auto it = upper_bound(R.begin(), R.end(), need);

        if (it == R.begin()) {
            ans = max(ans, x);
        } else {
            --it;
            ans = max(ans, x + *it);
        }
    }

    return ans;
}
```


## 📊 Index-by-Index Dry Run

| Step | State / Index | Observation | Action |
|---:|---|---|---|

| 0 | Split | left=[3,34,4], right=[12,5,2] | S=10 |

| 1 | Sorted right sums | [0,2,5,7,12,14,17,19] | Only ≤ need used |

| 2 | x=0 | need=10 | best right=7 → ans=7 |

| 3 | x=3 | need=7 | best right=7 → ans=10 |

| 4 | x=34 | skip | x>S |

| 5 | x=4 | need=6 | best right=5 → ans=max(10,9)=10 |

| 6 | Final | 10 | Maximum valid subset sum |


## ⏱ Complexity

Time O(2^(n/2) log 2^(n/2)), Space O(2^(n/2)).


## 🧩 Pattern Trigger

Use this when the problem has **subset/combinations**, `n` is too large for `2^n`, but small enough for `2^(n/2)`.


---


# Problem 10: Count Subsets With Sum Less Than or Equal to K

**Difficulty:** Medium  

**Pattern:** `MITM + upper_bound count`


## 🧾 Problem Statement

Count number of subsets whose sum is ≤ K.


## 📥 Input Example

```text
arr = [1, 2, 3, 4]
K = 5
```


## 📤 Expected Output

```text
9
```


## 🧠 Brute Force Thinking

Enumerate all subsets and count valid. O(2^n).


## ⚡ Optimal Idea

For each left sum x, count how many right sums are ≤ K-x using upper_bound.


## 💻 C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<long long> gen(vector<int>& a) {
    int n = a.size();
    vector<long long> sums;

    for (int mask = 0; mask < (1 << n); mask++) {
        long long sum = 0;
        for (int i = 0; i < n; i++) {
            if (mask & (1 << i)) sum += a[i];
        }
        sums.push_back(sum);
    }

    return sums;
}

long long countSubsetsLE(vector<int>& arr, long long K) {
    int n = arr.size();

    vector<int> left(arr.begin(), arr.begin() + n / 2);
    vector<int> right(arr.begin() + n / 2, arr.end());

    vector<long long> L = gen(left);
    vector<long long> R = gen(right);

    sort(R.begin(), R.end());

    long long ans = 0;

    for (long long x : L) {
        ans += upper_bound(R.begin(), R.end(), K - x) - R.begin();
    }

    return ans;
}
```


## 📊 Index-by-Index Dry Run

| Step | State / Index | Observation | Action |
|---:|---|---|---|

| 0 | Split | left=[1,2], right=[3,4] | K=5 |

| 1 | Left sums | [0,1,2,3] | Generated |

| 2 | Right sums sorted | [0,3,4,7] | Generated |

| 3 | x=0 | need=5 | right sums ≤5: 0,3,4 → add 3 |

| 4 | x=1 | need=4 | 0,3,4 → add 3 |

| 5 | x=2 | need=3 | 0,3 → add 2 |

| 6 | x=3 | need=2 | 0 → add 1 |

| 7 | Total | 3+3+2+1 | 9 |


## ⏱ Complexity

Time O(2^(n/2) log 2^(n/2)), Space O(2^(n/2)).


## 🧩 Pattern Trigger

Use this when the problem has **subset/combinations**, `n` is too large for `2^n`, but small enough for `2^(n/2)`.


---


# Phase 6 — Pair Sum MITM


# Problem 11: Classical Four Number Sum

**Difficulty:** Medium  

**Pattern:** `Pair sums + hash map`


## 🧾 Problem Statement

Given an array and target X, find if there exist four distinct indices i,j,k,l such that sum is X.


## 📥 Input Example

```text
arr = [1, 5, 1, 0, 6, 0]
X = 7
```


## 📤 Expected Output

```text
YES  // 1 + 5 + 1 + 0 = 7
```


## 🧠 Brute Force Thinking

Four nested loops. Complexity O(n^4).


## ⚡ Optimal Idea

Store pair sums. For current pair, search complement X - pairSum from previously stored pairs with non-overlapping indices.


## 💻 C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool fourSumExists(vector<int>& arr, int X) {
    int n = arr.size();
    unordered_map<int, vector<pair<int,int>>> mp;

    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {

            int cur = arr[i] + arr[j];
            int need = X - cur;

            if (mp.count(need)) {
                for (auto [a, b] : mp[need]) {
                    if (a != i && a != j && b != i && b != j) {
                        return true;
                    }
                }
            }
        }

        for (int k = 0; k < i; k++) {
            mp[arr[k] + arr[i]].push_back({k, i});
        }
    }

    return false;
}
```


## 📊 Index-by-Index Dry Run

| Step | State / Index | Observation | Action |
|---:|---|---|---|

| 0 | i=0 | No previous pairs | Nothing to check |

| 1 | i=1 | Check pair (1,5)=6 need=1 | No previous |

| 2 | After i=1 | Store (0,1) sum=6 | mp[6]={(0,1)} |

| 3 | i=2 | pair (2,3 later) etc | Need complement from old pairs |

| 4 | Found combination | old pair sum 6 + new pair sum 1 | Total 7 |


## ⏱ Complexity

Average O(n²) with hashing, worst can be higher due to duplicate pair lists.


## 🧩 Pattern Trigger

Use this when the problem has **subset/combinations**, `n` is too large for `2^n`, but small enough for `2^(n/2)`.


---


# Problem 12: CSES Four Values

**Difficulty:** Medium  

**Pattern:** `Pair sum + store indices`


## 🧾 Problem Statement

Find four distinct indices whose values sum to X.


## 📥 Input Example

```text
n = 8, X = 15
arr = [3, 2, 5, 8, 1, 3, 2, 3]
```


## 📤 Expected Output

```text
YES, one answer: indices of values 2 + 8 + 2 + 3 = 15
```


## 🧠 Brute Force Thinking

O(n^4), too slow.


## ⚡ Optimal Idea

Use hashmap from pair sum to a pair of indices seen so far. When processing new pair, check complement.


## 💻 C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> fourValues(vector<int>& arr, int X) {
    int n = arr.size();
    unordered_map<int, pair<int,int>> seen;

    for (int j = 0; j < n; j++) {

        for (int k = j + 1; k < n; k++) {
            int need = X - arr[j] - arr[k];

            if (seen.count(need)) {
                auto [a, b] = seen[need];
                return {a, b, j, k};
            }
        }

        for (int i = 0; i < j; i++) {
            seen[arr[i] + arr[j]] = {i, j};
        }
    }

    return {};
}
```


## 📊 Index-by-Index Dry Run

| Step | State / Index | Observation | Action |
|---:|---|---|---|

| 0 | j=0 | seen empty | No answer |

| 1 | j=1 val=2 | Check pairs with k>1 | Then store pair (0,1)=5 |

| 2 | j=3 val=8 | Check with k=6 val=2 | need=5 |

| 3 | seen[5] | pair (0,1) values 3+2 | Distinct indices |

| 4 | Answer | 3 + 2 + 8 + 2 = 15 | Return indices |


## ⏱ Complexity

Time O(n²), Space O(n²).


## 🧩 Pattern Trigger

Use this when the problem has **subset/combinations**, `n` is too large for `2^n`, but small enough for `2^(n/2)`.


---


# Phase 7 — Modulo MITM


# Problem 13: Maximum Subsequence Sum Modulo M

**Difficulty:** Hard  

**Pattern:** `MITM + modulo + upper_bound`


## 🧾 Problem Statement

Given n ≤ 40 numbers and m, find maximum subset sum modulo m.


## 📥 Input Example

```text
arr = [3, 3, 9, 9, 5]
m = 7
```


## 📤 Expected Output

```text
6
```


## 🧠 Brute Force Thinking

Try all subsets and compute sum % m. Complexity O(2^n).


## ⚡ Optimal Idea

Generate subset sums modulo m for both halves. For each left value x, find largest right value y such that x+y < m, and also consider wrap-around.


## 💻 C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<long long> genMod(vector<int>& a, long long m) {
    int n = a.size();
    vector<long long> res;

    for (int mask = 0; mask < (1 << n); mask++) {
        long long sum = 0;

        for (int i = 0; i < n; i++) {
            if (mask & (1 << i)) {
                sum = (sum + a[i]) % m;
            }
        }

        res.push_back(sum);
    }

    return res;
}

long long maxSubsetModulo(vector<int>& arr, long long m) {
    int n = arr.size();

    vector<int> left(arr.begin(), arr.begin() + n / 2);
    vector<int> right(arr.begin() + n / 2, arr.end());

    vector<long long> L = genMod(left, m);
    vector<long long> R = genMod(right, m);

    sort(R.begin(), R.end());

    long long ans = 0;

    for (long long x : L) {
        long long need = m - 1 - x;

        auto it = upper_bound(R.begin(), R.end(), need);

        if (it != R.begin()) {
            --it;
            ans = max(ans, (x + *it) % m);
        }

        ans = max(ans, x % m);
    }

    return ans;
}
```


## 📊 Index-by-Index Dry Run

| Step | State / Index | Observation | Action |
|---:|---|---|---|

| 0 | m=7 | Goal max possible is 6 | Cannot exceed m-1 |

| 1 | Split | left=[3,3], right=[9,9,5] | Modulo values used |

| 2 | Left mod sums | 0,3,3,6 | Generated |

| 3 | Right mod sums | include 0,2,4,5,6... | Sorted |

| 4 | x=6 | need=0 | choose y=0 → ans=6 |

| 5 | Final | 6 | Best possible modulo |


## ⏱ Complexity

Time O(2^(n/2) log 2^(n/2)), Space O(2^(n/2)).


## 🧩 Pattern Trigger

Use this when the problem has **subset/combinations**, `n` is too large for `2^n`, but small enough for `2^(n/2)`.


---


# Phase 8 — Advanced Transformation


# Problem 14: 4 Reversals Pattern

**Difficulty:** Hard  

**Pattern:** `State transformation + limited operations`


## 🧾 Problem Statement

Given a sequence transformation problem where at most 4 reversals are allowed, reason using reachable states and invariants.


## 📥 Input Example

```text
start = [1,2,3,4]
target = [3,2,1,4]
```


## 📤 Expected Output

```text
YES  // reverse segment [1..3]
```


## 🧠 Brute Force Thinking

Try all reversal combinations. For length n, one reversal has O(n²) choices, four reversals O(n^8).


## ⚡ Optimal Idea

Use invariants and controlled state generation. For small operation count, generate states after 2 reversals from start and after 2 reversals from target, then match using MITM.


## 💻 C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> revSeg(vector<int> a, int l, int r) {
    reverse(a.begin() + l, a.begin() + r + 1);
    return a;
}

set<vector<int>> generateTwoReversals(vector<int> start) {
    int n = start.size();
    set<vector<int>> states;

    states.insert(start);

    for (int l1 = 0; l1 < n; l1++) {
        for (int r1 = l1; r1 < n; r1++) {
            vector<int> one = revSeg(start, l1, r1);
            states.insert(one);

            for (int l2 = 0; l2 < n; l2++) {
                for (int r2 = l2; r2 < n; r2++) {
                    vector<int> two = revSeg(one, l2, r2);
                    states.insert(two);
                }
            }
        }
    }

    return states;
}

bool canTransformInFour(vector<int> start, vector<int> target) {
    auto A = generateTwoReversals(start);
    auto B = generateTwoReversals(target);

    for (auto& state : A) {
        if (B.count(state)) return true;
    }

    return false;
}
```


## 📊 Index-by-Index Dry Run

| Step | State / Index | Observation | Action |
|---:|---|---|---|

| 0 | Start | [1,2,3,4] | Target [3,2,1,4] |

| 1 | One reversal | reverse indices 0..2 | [3,2,1,4] |

| 2 | Match target | YES | Within 4 reversals |

| 3 | MITM idea | 2 reversals from start + 2 from target | If common state exists, answer YES |


## ⏱ Complexity

For n small/moderate: generate O(n^4) states for two reversals. MITM avoids O(n^8).


## 🧩 Pattern Trigger

Use this when the problem can be solved by **splitting a range**, solving halves, and merging useful information.


---


# Final Revision Strategy

## How to Practice This File

1. Read the problem statement only.
2. Guess the pattern in 5 seconds.
3. Write brute force first.
4. Identify the optimization trigger.
5. Code the template from memory.
6. Dry run using the table.
7. Re-solve after 3 days without looking.

---

# FAANG + CP Readiness Checklist

## Divide & Conquer
- [ ] Can write merge sort from memory
- [ ] Can count inversions during merge
- [ ] Can explain why `mid - i + 1` works
- [ ] Can solve reverse pairs
- [ ] Can derive Karatsuba formula

## Meet in the Middle
- [ ] Can generate subset sums by bitmask
- [ ] Can split array into halves
- [ ] Can binary search complement
- [ ] Can count subsets using `upper_bound`
- [ ] Can solve Four Values / Four Sum
- [ ] Can solve max subset modulo `m`

---

# One-Line Mental Triggers

| If you see... | Think... |
|---|---|
| `n <= 40` and subset | Meet in the Middle |
| Pair counting with order | Merge Sort counting |
| Four values / four sum | Pair sums |
| Maximum ≤ K | Sort one side + upper_bound |
| Exact target | Hashing or binary_search |
| Modulo maximum | MITM + modulo compression |
| Swap parity | Inversion parity |

---

🔥 End of handbook.
