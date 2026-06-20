# 🚀 Binary Search Phase-Wise FAANG + CP Handbook

> Difficulty-wise practice guide with clickable index, brute-force thinking, binary-search intuition, C++ templates, and step-by-step dry runs.

---

# 📌 How to Use This Note

```text
For every problem, learn in this order:

1. Problem statement
2. Input / output
3. Detailed example
4. Key observation
5. Brute-force thinking
6. Brute-force C++ code
7. Why brute force fails
8. Binary-search form
9. How check(mid) works
10. Optimal C++ code
11. Step-by-step dry run
12. Recognition signal
```

---

# ✅ Clickable Index

## 0. Binary Search Master Map
- [0.1 Binary Search Core Idea](#01-binary-search-core-idea)
- [0.2 First True vs Last True](#02-first-true-vs-last-true)
- [0.3 Forms of Binary Search](#03-forms-of-binary-search)
- [0.4 Universal Checklist](#04-universal-checklist)

## Phase 1 — Easy Foundation: Index Boundary
- [1. First 1 in Binary Array](#1-first-1-in-binary-array)
- [2. Lower Bound](#2-lower-bound)
- [3. Upper Bound](#3-upper-bound)
- [4. Search Insert Position](#4-search-insert-position)

## Phase 2 — Medium: Classic Array Binary Search
- [5. Search in Rotated Sorted Array](#5-search-in-rotated-sorted-array)
- [6. Find Minimum in Rotated Sorted Array](#6-find-minimum-in-rotated-sorted-array)
- [7. Peak Element](#7-peak-element)

## Phase 3 — Medium/Hard: Binary Search on Answer
- [8. Split Array Largest Sum / Painter Partition](#8-split-array-largest-sum--painter-partition)
- [9. Factory Machines / Minimum Time](#9-factory-machines--minimum-time)
- [10. Aggressive Cows](#10-aggressive-cows)
- [11. Minimize Maximum Gap After Adding K Points](#11-minimize-maximum-gap-after-adding-k-points)

## Phase 4 — Hard: Kth / Counting Problems
- [12. Kth Smallest Pair Sum](#12-kth-smallest-pair-sum)
- [13. Kth Smallest in Multiplication Table](#13-kth-smallest-in-multiplication-table)

## Phase 5 — CP Form: Binary Search on Every Start
- [14. Count Subarrays with At Most K Zeros](#14-count-subarrays-with-at-most-k-zeros)
- [15. Maximum Ones After At Most K Flips](#15-maximum-ones-after-at-most-k-flips)

## Phase 6 — Real Domain Binary Search
- [16. Square Root with Precision](#16-square-root-with-precision)

## Phase 7 — Ternary Search / Unimodal Function
- [17. Freefall / Minimum Time Function](#17-freefall--minimum-time-function)

## Final Revision
- [Binary Search Recognition Table](#binary-search-recognition-table)
- [Template Library](#template-library)
- [Common Mistakes](#common-mistakes)
- [Practice Order](#practice-order)

---

# 0. Binary Search Master Map

## 0.1 Binary Search Core Idea

```text
Binary Search is not only for sorted arrays.

Binary Search is for finding a boundary:

INVALID INVALID INVALID VALID VALID VALID
                        ^
                        first valid
```

or:

```text
VALID VALID VALID INVALID INVALID INVALID
                  ^
                  last valid
```

---

## 0.2 First True vs Last True

### First True

Use when answer space looks like:

```text
false false false true true true
                  ^
                  answer
```

Examples:

```text
minimum possible maximum value
minimum time
first index >= x
kth smallest using count <= mid
```

### Last True

Use when answer space looks like:

```text
true true true false false false
              ^
              answer
```

Examples:

```text
maximum minimum distance
maximum valid length
last index satisfying condition
```

---

## 0.3 Forms of Binary Search

```text
Binary Search
│
├── Form 1: Classic Index Search
│   ├── lower_bound
│   ├── upper_bound
│   ├── search insert position
│   └── first/last occurrence
│
├── Form 2: Modified Sorted Array Search
│   ├── rotated sorted array
│   ├── minimum in rotated array
│   └── peak element
│
├── Form 3: Binary Search on Answer
│   ├── minimize maximum
│   ├── maximize minimum
│   ├── minimum time
│   └── capacity / partition problems
│
├── Form 4: Kth / Counting
│   ├── kth smallest pair sum
│   ├── kth multiplication table
│   └── implicit matrix problems
│
├── Form 5: Binary Search on Every Start
│   ├── farthest valid end
│   └── count valid subarrays
│
├── Form 6: Real Domain Binary Search
│   └── precision answer
│
└── Form 7: Ternary Search
    └── hill / valley function
```

---

## 0.4 Universal Checklist

```text
Before writing code, ask:

1. What is the answer variable?
2. Can I guess answer = mid?
3. Can I check whether mid works?
4. Is check(mid) monotonic?
5. Am I finding first true or last true?
6. What are safe lo and hi?
7. Is overflow possible?
```

---

# Phase 1 — Easy Foundation: Index Boundary

---

# 1. First 1 in Binary Array

## Difficulty

```text
Easy
```

## Form

```text
First true in 0/1 monotone array
```

## Problem Statement

Given a binary sorted array containing `0`s followed by `1`s, find the first index where value is `1`.

## Input

```text
arr = [0, 0, 0, 0, 1, 1, 1]
```

## Output

```text
4
```


## Detailed Example

```text
arr = [0, 0, 0, 0, 1, 1, 1]
index  0  1  2  3  4  5  6
```

We need the **first** index where the value changes from `0` to `1`.

```text
Indexes 0..3 are false/invalid because arr[i] = 0.
Indexes 4..6 are true/valid because arr[i] = 1.
Answer = 4.
```

## Observation

```text
The condition arr[i] == 1 is monotonic:
false false false false true true true

Once we see a 1, all elements after it are also 1.
So the problem is not just searching value 1; it is searching the boundary.
```

## Brute-Force Code

```cpp
int firstOneBrute(vector<int>& arr) {
    int n = arr.size();

    for (int i = 0; i < n; i++) {
        if (arr[i] == 1) {
            return i;
        }
    }

    return -1;
}
```

## Brute-Force Detailed Walkthrough

```text
arr = [0, 0, 0, 0, 1, 1, 1]

 i   arr[i]   action
 0     0      not 1, continue
 1     0      not 1, continue
 2     0      not 1, continue
 3     0      not 1, continue
 4     1      first 1 found, return 4
```

## Brute-Force Thinking

```text
Scan from left to right.
The first time arr[i] == 1, return i.
```

## Brute-Force Code Idea

```cpp
for (int i = 0; i < n; i++) {
    if (arr[i] == 1) return i;
}
return -1;
```

## Why Brute Force Fails

```text
Time = O(n)
For very large arrays, we can do better because the array is monotonic.
```

## How Binary Search Works

Convert the array into a boundary problem:

```text
0 0 0 0 1 1 1
        ^
        first true
```

At `mid`:

```text
if arr[mid] == 1:
    mid may be answer, but try left
else:
    answer must be right
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int firstOne(vector<int>& arr) {
    int n = arr.size();
    int lo = 0, hi = n - 1;
    int ans = -1;

    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;

        if (arr[mid] == 1) {
            ans = mid;
            hi = mid - 1;
        } else {
            lo = mid + 1;
        }
    }

    return ans;
}
```

## Step-by-Step Index Dry Run

```text
arr = [0, 0, 0, 0, 1, 1, 1]
index  0  1  2  3  4  5  6
```

### Step 1

```text
lo = 0, hi = 6
mid = 3
arr[3] = 0

0 is invalid.
Move right.
lo = mid + 1 = 4
```

### Step 2

```text
lo = 4, hi = 6
mid = 5
arr[5] = 1

1 is valid.
ans = 5
Try earlier 1.
hi = mid - 1 = 4
```

### Step 3

```text
lo = 4, hi = 4
mid = 4
arr[4] = 1

ans = 4
hi = 3

Stop because lo > hi.
Answer = 4
```

## Recognition Signal

```text
first position where condition becomes true
0/1 monotone array
boundary between false and true
```

---

# 2. Lower Bound

## Difficulty

```text
Easy
```

## Form

```text
First index where arr[i] >= x
```

## Problem Statement

Given sorted array `arr` and target `x`, return the first index where `arr[i] >= x`.

## Input

```text
arr = [2, 3, 3, 7, 9, 11, 11, 17]
x = 11
```

## Output

```text
5
```


## Detailed Example

```text
arr = [2, 3, 3, 7, 9, 11, 11, 17]
x = 11
```

Lower bound means the **first position where value is at least x**.

```text
Values before index 5 are < 11.
arr[5] = 11, so answer = 5.
```

## Observation

```text
Condition: arr[i] >= x

For x = 11:
2   3   3   7   9   11   11   17
F   F   F   F   F    T    T    T
                    ^
                    first true
```

Lower bound is useful for:

```text
1. first occurrence position
2. insertion position
3. count of elements < x
4. binary-search counting problems
```

## Brute-Force Code

```cpp
int lowerBoundBrute(vector<int>& arr, int x) {
    int n = arr.size();

    for (int i = 0; i < n; i++) {
        if (arr[i] >= x) {
            return i;
        }
    }

    return n;
}
```

## Brute-Force Detailed Walkthrough

```text
x = 11

 i   arr[i]   arr[i] >= 11?
 0     2      false
 1     3      false
 2     3      false
 3     7      false
 4     9      false
 5    11      true -> return 5
```

## Brute-Force Thinking

```text
Scan from left and find first element >= x.
```

## Why Brute Force Fails

```text
O(n), but sorted array gives monotonic condition.
```

## How Binary Search Works

Check condition:

```text
arr[i] >= x
```

For `x = 11`:

```text
arr   = [2, 3, 3, 7, 9, 11, 11, 17]
check = [F, F, F, F, F,  T,  T,  T]
                         ^
                         first true
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int lowerBoundManual(vector<int>& arr, int x) {
    int n = arr.size();
    int lo = 0, hi = n - 1;
    int ans = n;

    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;

        if (arr[mid] >= x) {
            ans = mid;
            hi = mid - 1;
        } else {
            lo = mid + 1;
        }
    }

    return ans;
}
```

## Step-by-Step Index Dry Run

```text
arr = [2, 3, 3, 7, 9, 11, 11, 17]
x = 11
index  0  1  2  3  4   5   6   7
```

### Step 1

```text
lo = 0, hi = 7
mid = 3
arr[3] = 7

7 >= 11 ? false
Move right.
lo = 4
```

### Step 2

```text
lo = 4, hi = 7
mid = 5
arr[5] = 11

11 >= 11 ? true
ans = 5
Try left.
hi = 4
```

### Step 3

```text
lo = 4, hi = 4
mid = 4
arr[4] = 9

9 >= 11 ? false
lo = 5

Stop.
Answer = 5
```

## Recognition Signal

```text
first >= x
insert position
sorted array with duplicates
```

---

# 3. Upper Bound

## Difficulty

```text
Easy
```

## Form

```text
First index where arr[i] > x
```

## Problem Statement

Given sorted array `arr` and target `x`, return first index where `arr[i] > x`.

## Input

```text
arr = [2, 3, 3, 7, 9, 11, 11, 17]
x = 11
```

## Output

```text
7
```


## Detailed Example

```text
arr = [2, 3, 3, 7, 9, 11, 11, 17]
x = 11
```

Upper bound means the **first position where value is strictly greater than x**.

```text
arr[5] = 11 is not greater than 11.
arr[6] = 11 is not greater than 11.
arr[7] = 17 is greater than 11.
Answer = 7.
```

## Observation

```text
Condition: arr[i] > x

For x = 11:
2   3   3   7   9   11   11   17
F   F   F   F   F    F    F    T
                              ^
                              first true
```

Upper bound is useful for:

```text
count of elements <= x = upper_bound(arr, x) - arr.begin()
count of x = upper_bound(x) - lower_bound(x)
```

## Brute-Force Code

```cpp
int upperBoundBrute(vector<int>& arr, int x) {
    int n = arr.size();

    for (int i = 0; i < n; i++) {
        if (arr[i] > x) {
            return i;
        }
    }

    return n;
}
```

## Brute-Force Detailed Walkthrough

```text
x = 11

 i   arr[i]   arr[i] > 11?
 0     2      false
 1     3      false
 2     3      false
 3     7      false
 4     9      false
 5    11      false
 6    11      false
 7    17      true -> return 7
```

## Brute-Force Thinking

```text
Scan left to right and return first index where value > x.
```

## How Binary Search Works

```text
arr[i] > x
```

For `x = 11`:

```text
arr   = [2, 3, 3, 7, 9, 11, 11, 17]
check = [F, F, F, F, F,  F,  F,  T]
                                 ^
                                 first true
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int upperBoundManual(vector<int>& arr, int x) {
    int n = arr.size();
    int lo = 0, hi = n - 1;
    int ans = n;

    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;

        if (arr[mid] > x) {
            ans = mid;
            hi = mid - 1;
        } else {
            lo = mid + 1;
        }
    }

    return ans;
}
```

## Step-by-Step Index Dry Run

```text
arr = [2, 3, 3, 7, 9, 11, 11, 17]
x = 11
```

```text
Step 1:
lo = 0, hi = 7
mid = 3
arr[3] = 7
7 > 11 ? false
lo = 4
```

```text
Step 2:
lo = 4, hi = 7
mid = 5
arr[5] = 11
11 > 11 ? false
lo = 6
```

```text
Step 3:
lo = 6, hi = 7
mid = 6
arr[6] = 11
11 > 11 ? false
lo = 7
```

```text
Step 4:
lo = 7, hi = 7
mid = 7
arr[7] = 17
17 > 11 ? true
ans = 7
hi = 6

Answer = 7
```

## Recognition Signal

```text
first > x
count of <= x
right boundary of duplicates
```

---

# 4. Search Insert Position

## Difficulty

```text
Easy / FAANG Warmup
```

## Form

```text
Lower bound application
```

## Problem Statement

Given sorted array and target, return index if target exists. Otherwise return index where it should be inserted.

## Input

```text
nums = [1, 3, 5, 6]
target = 5
```

## Output

```text
2
```


## Detailed Example

```text
nums = [1, 3, 5, 6]
target = 2
```

Target `2` is not present. It should be inserted before `3`.

```text
[1, 2, 3, 5, 6]
    ^
    index 1
```

## Observation

```text
Search Insert Position = Lower Bound
Answer = first index where nums[i] >= target
```

Cases:

```text
target = 5 -> answer 2 because nums[2] = 5
target = 2 -> answer 1 because 2 should come before 3
target = 7 -> answer 4 because 7 goes after all elements
target = 0 -> answer 0 because 0 goes before all elements
```

## Brute-Force Code

```cpp
int searchInsertBrute(vector<int>& nums, int target) {
    int n = nums.size();

    for (int i = 0; i < n; i++) {
        if (nums[i] >= target) {
            return i;
        }
    }

    return n;
}
```

## Brute-Force Detailed Walkthrough

```text
nums = [1, 3, 5, 6]
target = 2

 i   nums[i]   nums[i] >= 2?
 0      1      false
 1      3      true -> return 1
```

## Brute-Force Thinking

```text
Move from left until nums[i] >= target.
That index is insertion point.
```

## How Binary Search Works

```text
Insertion point = first index where nums[i] >= target.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int searchInsert(vector<int>& nums, int target) {
    int lo = 0, hi = nums.size() - 1;
    int ans = nums.size();

    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;

        if (nums[mid] >= target) {
            ans = mid;
            hi = mid - 1;
        } else {
            lo = mid + 1;
        }
    }

    return ans;
}
```

## Step-by-Step Index Dry Run

```text
nums = [1, 3, 5, 6]
target = 5
index   0  1  2  3
```

```text
Step 1:
lo = 0, hi = 3
mid = 1
nums[1] = 3
3 >= 5 ? false
lo = 2
```

```text
Step 2:
lo = 2, hi = 3
mid = 2
nums[2] = 5
5 >= 5 ? true
ans = 2
hi = 1

Answer = 2
```

## Recognition Signal

```text
sorted array
insert position
first >= target
```

---

# Phase 2 — Medium: Classic Array Binary Search

---

# 5. Search in Rotated Sorted Array

## Difficulty

```text
Medium / FAANG Classic
```

## Form

```text
Modified binary search on sorted half
```

## Problem Statement

Given rotated sorted array with distinct elements, find index of target.

## Input

```text
nums = [4,5,6,7,0,1,2]
target = 0
```

## Output

```text
4
```


## Detailed Example

```text
nums = [4, 5, 6, 7, 0, 1, 2]
target = 0
```

This array was originally sorted:

```text
[0, 1, 2, 4, 5, 6, 7]
```

Then it was rotated:

```text
[4, 5, 6, 7, 0, 1, 2]
```

Target `0` is at index `4`.

## Observation

```text
Even though the full array is not sorted, at least one half around mid is always sorted.

At every step:
1. Check if nums[mid] is target.
2. Identify sorted half.
3. Check if target lies inside sorted half.
4. Discard the other half.
```

## Brute-Force Code

```cpp
int searchRotatedBrute(vector<int>& nums, int target) {
    for (int i = 0; i < (int)nums.size(); i++) {
        if (nums[i] == target) {
            return i;
        }
    }

    return -1;
}
```

## Brute-Force Detailed Walkthrough

```text
nums = [4,5,6,7,0,1,2]
target = 0

 i   nums[i]   match?
 0      4      no
 1      5      no
 2      6      no
 3      7      no
 4      0      yes -> return 4
```

Brute force works, but ignores the rotated sorted structure.

## Brute-Force Thinking

```text
Scan all elements and compare with target.
```

## Why Brute Force Fails

```text
O(n), but array has rotated sorted structure.
```

## How Binary Search Works

At every `mid`, one side is always sorted.

```text
If nums[lo] <= nums[mid], left half is sorted.
Else right half is sorted.
```

Then check whether target lies inside the sorted half.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int searchRotated(vector<int>& nums, int target) {
    int lo = 0, hi = nums.size() - 1;

    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;

        if (nums[mid] == target) return mid;

        if (nums[lo] <= nums[mid]) {
            // left half sorted
            if (nums[lo] <= target && target < nums[mid]) {
                hi = mid - 1;
            } else {
                lo = mid + 1;
            }
        } else {
            // right half sorted
            if (nums[mid] < target && target <= nums[hi]) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
    }

    return -1;
}
```

## Step-by-Step Index Dry Run

```text
nums = [4,5,6,7,0,1,2]
target = 0
index   0 1 2 3 4 5 6
```

### Step 1

```text
lo = 0, hi = 6
mid = 3
nums[mid] = 7

nums[lo] = 4, nums[mid] = 7
left half [4,5,6,7] is sorted.

Does target 0 lie between 4 and 7?
No.

Search right half.
lo = 4
```

### Step 2

```text
lo = 4, hi = 6
mid = 5
nums[mid] = 1

nums[lo] = 0, nums[mid] = 1
left half [0,1] is sorted.

Does target 0 lie between 0 and 1?
Yes.

Search left.
hi = 4
```

### Step 3

```text
lo = 4, hi = 4
mid = 4
nums[mid] = 0

Found target.
Answer = 4
```

## Recognition Signal

```text
rotated sorted array
find target
one half sorted at every step
```

---

# 6. Find Minimum in Rotated Sorted Array

## Difficulty

```text
Medium
```

## Form

```text
Boundary search using nums[mid] vs nums[hi]
```

## Problem Statement

Find the minimum element in a rotated sorted array.

## Input

```text
nums = [8, 9, 1, 2, 3, 4]
```

## Output

```text
1
```


## Detailed Example

```text
nums = [8, 9, 1, 2, 3, 4]
```

The minimum element is the rotation point.

```text
8  9  1  2  3  4
      ^
      minimum = 1
```

## Observation

```text
Compare nums[mid] with nums[hi].

If nums[mid] > nums[hi]:
    mid is in the left high-value part.
    minimum must be to the right.

Else:
    mid is in the right sorted part.
    minimum can be mid or to the left.
```

## Brute-Force Code

```cpp
int findMinBrute(vector<int>& nums) {
    int mn = nums[0];

    for (int x : nums) {
        mn = min(mn, x);
    }

    return mn;
}
```

## Brute-Force Detailed Walkthrough

```text
nums = [8, 9, 1, 2, 3, 4]

start mn = 8
see 8 -> mn = 8
see 9 -> mn = 8
see 1 -> mn = 1
see 2 -> mn = 1
see 3 -> mn = 1
see 4 -> mn = 1

answer = 1
```

## Brute-Force Thinking

```text
Scan and find minimum.
```

## How Binary Search Works

```text
if nums[mid] > nums[hi]:
    minimum is right
else:
    minimum is at mid or left
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int findMin(vector<int>& nums) {
    int lo = 0, hi = nums.size() - 1;

    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;

        if (nums[mid] > nums[hi]) {
            lo = mid + 1;
        } else {
            hi = mid;
        }
    }

    return nums[lo];
}
```

## Step-by-Step Index Dry Run

```text
nums = [8, 9, 1, 2, 3, 4]
index   0  1  2  3  4  5
```

```text
Step 1:
lo = 0, hi = 5
mid = 2
nums[mid] = 1, nums[hi] = 4
1 > 4 ? false
minimum is mid or left
hi = 2
```

```text
Step 2:
lo = 0, hi = 2
mid = 1
nums[mid] = 9, nums[hi] = 1
9 > 1 ? true
minimum is right
lo = 2

Stop lo == hi.
Answer = nums[2] = 1
```

## Recognition Signal

```text
rotation count
minimum in rotated sorted array
compare mid with hi
```

---

# 7. Peak Element

## Difficulty

```text
Medium / FAANG Classic
```

## Form

```text
Binary search on slope
```

## Problem Statement

Find any peak element. Peak means `nums[i] > nums[i-1]` and `nums[i] > nums[i+1]`.

## Input

```text
nums = [1, 2, 3, 1]
```

## Output

```text
2
```


## Detailed Example

```text
nums = [1, 2, 3, 1]
```

Index `2` is peak because:

```text
nums[2] = 3
left neighbor = 2
right neighbor = 1
3 > 2 and 3 > 1
```

## Observation

```text
If nums[mid] < nums[mid + 1], slope is going up.
A peak must exist on the right.

If nums[mid] > nums[mid + 1], slope is going down.
A peak exists at mid or on the left.
```

This works because outside boundaries can be imagined as `-infinity`.

## Brute-Force Code

```cpp
int findPeakBrute(vector<int>& nums) {
    int n = nums.size();

    for (int i = 0; i < n; i++) {
        bool leftOk = (i == 0 || nums[i] > nums[i - 1]);
        bool rightOk = (i == n - 1 || nums[i] > nums[i + 1]);

        if (leftOk && rightOk) {
            return i;
        }
    }

    return -1;
}
```

## Brute-Force Detailed Walkthrough

```text
nums = [1, 2, 3, 1]

index 0: right neighbor 2 is bigger -> not peak
index 1: right neighbor 3 is bigger -> not peak
index 2: 3 > 2 and 3 > 1 -> peak, return 2
```

## Brute-Force Thinking

```text
Check every index and compare with neighbors.
```

## How Binary Search Works

Compare `nums[mid]` and `nums[mid + 1]`.

```text
if nums[mid] < nums[mid + 1]:
    we are climbing, peak is right
else:
    we are falling, peak is at mid or left
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int findPeakElement(vector<int>& nums) {
    int lo = 0, hi = nums.size() - 1;

    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;

        if (nums[mid] < nums[mid + 1]) {
            lo = mid + 1;
        } else {
            hi = mid;
        }
    }

    return lo;
}
```

## Step-by-Step Index Dry Run

```text
nums = [1, 2, 3, 1]
index   0  1  2  3
```

```text
Step 1:
lo = 0, hi = 3
mid = 1
nums[1] = 2, nums[2] = 3
2 < 3 ? true
climbing
lo = 2
```

```text
Step 2:
lo = 2, hi = 3
mid = 2
nums[2] = 3, nums[3] = 1
3 < 1 ? false
falling
hi = 2

Answer = 2
```

## Recognition Signal

```text
peak
mountain
bitonic
compare mid with mid + 1
```

---

# Phase 3 — Medium/Hard: Binary Search on Answer

---

# 8. Split Array Largest Sum / Painter Partition

## Difficulty

```text
Medium / Hard FAANG
```

## Form

```text
Binary search on answer: minimize maximum
```

## Problem Statement

Split array into `k` non-empty contiguous parts. Minimize the largest sum among all parts.

## Input

```text
nums = [7, 2, 5, 10, 8]
k = 2
```

## Output

```text
18
```


## Detailed Example

```text
nums = [7, 2, 5, 10, 8]
k = 2
```

Possible splits:

```text
[7] | [2,5,10,8]       max = 25
[7,2] | [5,10,8]       max = 23
[7,2,5] | [10,8]       max = 18  <- best
[7,2,5,10] | [8]       max = 24
```

Answer is `18`.

## Observation

```text
We are minimizing the maximum partition sum.

If limit = 18 works, any limit > 18 also works.
If limit = 15 fails, any limit < 15 also fails.

So the answer space is:
false false false true true true
                  ^
                  first true
```

## Brute-Force Code

```cpp
void generateSplits(vector<int>& nums, int idx, int partsLeft,
                    long long currentSum, long long currentMax,
                    long long& best) {
    int n = nums.size();

    if (idx == n) {
        if (partsLeft == 1) {
            best = min(best, max(currentMax, currentSum));
        }
        return;
    }

    // Option 1: continue current partition
    generateSplits(nums, idx + 1, partsLeft,
                   currentSum + nums[idx], currentMax, best);

    // Option 2: cut before nums[idx], if we still can create more parts
    if (partsLeft > 1) {
        generateSplits(nums, idx + 1, partsLeft - 1,
                       nums[idx], max(currentMax, currentSum), best);
    }
}

long long splitArrayBrute(vector<int>& nums, int k) {
    long long best = LLONG_MAX;
    generateSplits(nums, 1, k, nums[0], 0, best);
    return best;
}
```

## Brute-Force Detailed Walkthrough

```text
For k = 2, choose one cut position:

cut after index 0: [7] and [2,5,10,8]
max sum = max(7,25) = 25

cut after index 1: [7,2] and [5,10,8]
max sum = max(9,23) = 23

cut after index 2: [7,2,5] and [10,8]
max sum = max(14,18) = 18

cut after index 3: [7,2,5,10] and [8]
max sum = max(24,8) = 24

minimum among [25,23,18,24] = 18
```

## Brute-Force Thinking

```text
Try every possible way to split array into k parts.
Calculate max partition sum.
Take minimum.
```

## Why Brute Force Fails

```text
Number of partitions grows combinatorially.
For n large, impossible.
```

## How Binary Search Works

Answer is maximum allowed partition sum.

```text
Can we split array into <= k parts such that each part sum <= mid?
```

If yes:

```text
mid works, try smaller.
```

If no:

```text
mid too small, try bigger.
```

Search space:

```text
lo = max(nums)
hi = sum(nums)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canSplit(vector<int>& nums, int k, long long limit) {
    int parts = 1;
    long long sum = 0;

    for (int x : nums) {
        if (sum + x <= limit) {
            sum += x;
        } else {
            parts++;
            sum = x;
        }
    }

    return parts <= k;
}

long long splitArray(vector<int>& nums, int k) {
    long long lo = 0, hi = 0;

    for (int x : nums) {
        lo = max(lo, (long long)x);
        hi += x;
    }

    long long ans = hi;

    while (lo <= hi) {
        long long mid = lo + (hi - lo) / 2;

        if (canSplit(nums, k, mid)) {
            ans = mid;
            hi = mid - 1;
        } else {
            lo = mid + 1;
        }
    }

    return ans;
}
```

## Step-by-Step Dry Run

```text
nums = [7, 2, 5, 10, 8]
k = 2

lo = max(nums) = 10
hi = sum(nums) = 32
```

### Try mid = 21

```text
Can each part have sum <= 21?

Part 1: 7 + 2 + 5 = 14
Next 10 makes 24 > 21, so split.

Part 2: 10 + 8 = 18

parts = 2 <= k
21 works.
Try smaller.
hi = 20
ans = 21
```

### Try mid = 15

```text
Part 1: 7 + 2 + 5 = 14
Next 10 makes 24 > 15, split.

Part 2: 10
Next 8 makes 18 > 15, split.

Part 3: 8

parts = 3 > k
15 too small.
lo = 16
```

### Try mid = 18

```text
Part 1: 7 + 2 + 5 = 14
Next 10 makes 24 > 18, split.

Part 2: 10 + 8 = 18

parts = 2
18 works.
ans = 18
```

```text
Answer = 18
```

## Recognition Signal

```text
minimize largest
split into k groups
contiguous partition
capacity problem
```

---

# 9. Factory Machines / Minimum Time

## Difficulty

```text
Medium CP Classic
```

## Form

```text
Binary search on answer: minimum time
```

## Problem Statement

Given machines where machine `i` produces one item in `machines[i]` time, find minimum time to produce at least `target` items.

## Input

```text
machines = [2, 3, 7]
target = 10
```

## Output

```text
12
```


## Detailed Example

```text
machines = [2, 3, 7]
target = 10
```

At time `12`:

```text
machine 2 produces floor(12/2) = 6 items
machine 3 produces floor(12/3) = 4 items
machine 7 produces floor(12/7) = 1 item

total = 11 items
```

So time `12` is enough. Time `11` produces only `9`, so answer is `12`.

## Observation

```text
If time t can produce target items, any time greater than t also works.

not enough, not enough, enough, enough, enough
                        ^
                        first true
```

## Brute-Force Code

```cpp
long long minimumTimeBrute(vector<long long>& machines, long long target) {
    for (long long t = 0; ; t++) {
        long long made = 0;

        for (long long m : machines) {
            made += t / m;
        }

        if (made >= target) {
            return t;
        }
    }
}
```

## Brute-Force Detailed Walkthrough

```text
machines = [2,3,7]

time 1: 1/2 + 1/3 + 1/7 = 0 + 0 + 0 = 0
time 2: 2/2 + 2/3 + 2/7 = 1 + 0 + 0 = 1
time 3: 1 + 1 + 0 = 2
...
time 11: 5 + 3 + 1 = 9, not enough
time 12: 6 + 4 + 1 = 11, enough -> answer 12
```

## Brute-Force Thinking

```text
Simulate time from 1 upward.
For each time t, count produced items.
Stop when count >= target.
```

## Why Brute Force Fails

```text
target and time can be very large.
Need logarithmic search over answer.
```

## How Binary Search Works

For a guessed time `mid`:

```text
items = sum(mid / machines[i])
```

If items >= target, time works.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canMake(vector<long long>& machines, long long target, long long time) {
    long long made = 0;

    for (long long m : machines) {
        made += time / m;
        if (made >= target) return true;
    }

    return false;
}

long long minimumTime(vector<long long>& machines, long long target) {
    long long fastest = *min_element(machines.begin(), machines.end());
    long long lo = 0;
    long long hi = fastest * target;
    long long ans = hi;

    while (lo <= hi) {
        long long mid = lo + (hi - lo) / 2;

        if (canMake(machines, target, mid)) {
            ans = mid;
            hi = mid - 1;
        } else {
            lo = mid + 1;
        }
    }

    return ans;
}
```

## Step-by-Step Dry Run

```text
machines = [2, 3, 7]
target = 10

lo = 0
hi = 2 * 10 = 20
```

### Try mid = 10

```text
machine 2 makes 10 / 2 = 5
machine 3 makes 10 / 3 = 3
machine 7 makes 10 / 7 = 1

total = 9
9 >= 10 ? false
lo = 11
```

### Try mid = 15

```text
15/2 = 7
15/3 = 5
15/7 = 2

total = 14
14 >= 10 ? true
ans = 15
hi = 14
```

### Try mid = 12

```text
12/2 = 6
12/3 = 4
12/7 = 1

total = 11
works
ans = 12
hi = 11
```

### Try mid = 11

```text
11/2 = 5
11/3 = 3
11/7 = 1

total = 9
not enough
lo = 12

Answer = 12
```

## Recognition Signal

```text
minimum time
machines/workers produce items
if time works, larger time also works
```

---

# 10. Aggressive Cows

## Difficulty

```text
Medium / CP Classic
```

## Form

```text
Binary search on answer: maximize minimum
```

## Problem Statement

Given stall positions and `k` cows, place cows so that minimum distance between any two cows is maximized.

## Input

```text
positions = [1, 2, 4, 8, 9]
k = 3
```

## Output

```text
3
```


## Detailed Example

```text
positions = [1, 2, 4, 8, 9]
k = 3
```

Try minimum distance `3`:

```text
place cow at 1
next must be >= 4 -> place at 4
next must be >= 7 -> place at 8
placed 3 cows, so distance 3 works
```

Try minimum distance `4`:

```text
place at 1
next must be >= 5 -> place at 8
next must be >= 12 -> impossible
only 2 cows placed, so distance 4 fails
```

Answer = `3`.

## Observation

```text
We are maximizing the minimum distance.

If distance d works, all smaller distances also work.
If distance d fails, all larger distances also fail.

true true true false false
          ^
          last true
```

## Brute-Force Code

```cpp
long long aggressiveCowsBrute(vector<long long>& pos, int k) {
    sort(pos.begin(), pos.end());
    int n = pos.size();
    long long best = 0;

    // Simple brute force over all distances.
    for (long long d = 0; d <= pos.back() - pos.front(); d++) {
        int placed = 1;
        long long last = pos[0];

        for (int i = 1; i < n; i++) {
            if (pos[i] - last >= d) {
                placed++;
                last = pos[i];
            }
        }

        if (placed >= k) best = d;
    }

    return best;
}
```

## Brute-Force Detailed Walkthrough

```text
Try d = 1 -> can place at 1,2,4 -> works
Try d = 2 -> can place at 1,4,8 -> works
Try d = 3 -> can place at 1,4,8 -> works
Try d = 4 -> can place at 1,8 only -> fails

Best working d = 3
```

## Brute-Force Thinking

```text
Try all ways to choose k stalls.
For each placement, compute minimum pair distance.
Take maximum.
```

## Why Brute Force Fails

```text
Choosing k positions from n is combinatorial.
```

## How Binary Search Works

Guess distance `d`.

```text
Can we place at least k cows such that each cow is at least d away from previous cow?
```

If `d` works, smaller distances also work.

```text
Y Y Y N N N
      ^
      last true
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canPlace(vector<long long>& pos, int k, long long dist) {
    int placed = 1;
    long long last = pos[0];

    for (int i = 1; i < pos.size(); i++) {
        if (pos[i] - last >= dist) {
            placed++;
            last = pos[i];
        }
    }

    return placed >= k;
}

long long aggressiveCows(vector<long long>& pos, int k) {
    sort(pos.begin(), pos.end());

    long long lo = 0;
    long long hi = pos.back() - pos.front();
    long long ans = 0;

    while (lo <= hi) {
        long long mid = lo + (hi - lo) / 2;

        if (canPlace(pos, k, mid)) {
            ans = mid;
            lo = mid + 1;
        } else {
            hi = mid - 1;
        }
    }

    return ans;
}
```

## Step-by-Step Dry Run

```text
positions = [1, 2, 4, 8, 9]
k = 3

lo = 0
hi = 8
```

### Try dist = 4

```text
Place cow at 1.
Next position must be >= 5.
Place cow at 8.
Next position must be >= 12.
No more stall.

placed = 2 < 3
Distance 4 not possible.
hi = 3
```

### Try dist = 1

```text
Place at 1.
Next >= 2, place at 2.
Next >= 3, place at 4.

placed = 3
Distance 1 possible.
ans = 1
lo = 2
```

### Try dist = 2

```text
Place at 1.
Next >= 3, place at 4.
Next >= 6, place at 8.

placed = 3
Distance 2 possible.
ans = 2
lo = 3
```

### Try dist = 3

```text
Place at 1.
Next >= 4, place at 4.
Next >= 7, place at 8.

placed = 3
Distance 3 possible.
ans = 3
lo = 4

Answer = 3
```

## Recognition Signal

```text
maximize minimum distance
place items greedily
if distance d works, smaller also works
```

---

# 11. Minimize Maximum Gap After Adding K Points

## Difficulty

```text
Hard / CP
```

## Form

```text
Binary search on answer: minimize maximum gap
```

## Problem Statement

Given sorted positions on a line, add at most `k` new points. Minimize the maximum gap between adjacent points.

## Input

```text
positions = [0, 50, 100]
k = 2
```

## Output

```text
25
```


## Detailed Example

```text
positions = [0, 50, 100]
k = 2
```

Add points at `25` and `75`:

```text
0, 25, 50, 75, 100
```

Now every adjacent gap is `25`, so answer is `25`.

## Observation

```text
For a gap d and allowed maximum gap x:
needed points = ceil(d / x) - 1

Example:
d = 50, x = 25
ceil(50 / 25) - 1 = 2 - 1 = 1
One point is enough: 0, 25, 50

Example:
d = 50, x = 24
ceil(50 / 24) - 1 = 3 - 1 = 2
Need two points because one point cannot keep both sub-gaps <= 24.
```

## Brute-Force Code

```cpp
long long minimizeMaxGapBrute(vector<long long> pos, long long k) {
    sort(pos.begin(), pos.end());

    long long hi = 0;
    for (int i = 1; i < (int)pos.size(); i++) {
        hi = max(hi, pos[i] - pos[i - 1]);
    }

    for (long long x = 1; x <= hi; x++) {
        long long need = 0;

        for (int i = 1; i < (int)pos.size(); i++) {
            long long d = pos[i] - pos[i - 1];
            need += (d + x - 1) / x - 1;
        }

        if (need <= k) {
            return x;
        }
    }

    return hi;
}
```

## Brute-Force Detailed Walkthrough

```text
positions = [0,50,100], k = 2

Try x = 24:
gap 50 needs 2 points
gap 50 needs 2 points
total need = 4 > 2 -> fail

Try x = 25:
gap 50 needs 1 point
gap 50 needs 1 point
total need = 2 <= 2 -> works

answer = 25
```

## Brute-Force Thinking

```text
Keep inserting point into largest gap.
```

## Why Brute Force Can Fail

```text
Greedy simulation may be expensive for large k.
Also final answer should be searched directly.
```

## How Binary Search Works

Guess maximum allowed gap `x`.

For original gap `d`, number of points needed:

```text
ceil(d / x) - 1
```

Integer-safe:

```text
(d + x - 1) / x - 1
```

If total points needed <= k, `x` works.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canLimitGap(vector<long long>& pos, long long k, long long x) {
    long long need = 0;

    for (int i = 1; i < pos.size(); i++) {
        long long d = pos[i] - pos[i - 1];
        need += (d + x - 1) / x - 1;

        if (need > k) return false;
    }

    return need <= k;
}

long long minimizeMaxGap(vector<long long>& pos, long long k) {
    sort(pos.begin(), pos.end());

    long long lo = 1;
    long long hi = 0;

    for (int i = 1; i < pos.size(); i++) {
        hi = max(hi, pos[i] - pos[i - 1]);
    }

    long long ans = hi;

    while (lo <= hi) {
        long long mid = lo + (hi - lo) / 2;

        if (canLimitGap(pos, k, mid)) {
            ans = mid;
            hi = mid - 1;
        } else {
            lo = mid + 1;
        }
    }

    return ans;
}
```

## Step-by-Step Dry Run

```text
positions = [0, 50, 100]
k = 2
```

### Try x = 25

```text
Gap 0 to 50 = 50
Need = ceil(50 / 25) - 1 = 2 - 1 = 1

Gap 50 to 100 = 50
Need = 1

Total need = 2
2 <= k
x = 25 works.
```

### Try x = 24

```text
Gap 50:
ceil(50 / 24) - 1 = 3 - 1 = 2

Two gaps need:
2 + 2 = 4

4 > k
x = 24 does not work.
```

```text
Answer = 25
```

## Recognition Signal

```text
minimize maximum distance/gap
add k points
ceil formula
```

---

# Phase 4 — Hard: Kth / Counting Problems

---

# 12. Kth Smallest Pair Sum

## Difficulty

```text
Hard / FAANG + CP
```

## Form

```text
Binary search on answer + count <= mid
```

## Problem Statement

Given two sorted arrays `A` and `B`, find kth smallest value among all pair sums `A[i] + B[j]`.

## Input

```text
A = [1, 2, 3]
B = [4, 5, 6]
k = 6
```

## Output

```text
7
```


## Detailed Example

```text
A = [1, 2, 3]
B = [4, 5, 6]
k = 6
```

All pair sums:

```text
        B=4  B=5  B=6
A=1      5    6    7
A=2      6    7    8
A=3      7    8    9
```

Sorted:

```text
[5, 6, 6, 7, 7, 7, 8, 8, 9]
```

The 6th smallest is `7`.

## Observation

```text
Instead of generating all pair sums, count how many pair sums are <= x.

If count <= x is at least k, then kth value is <= x.
If count is less than k, x is too small.
```

## Brute-Force Code

```cpp
long long kthPairSumBrute(vector<long long>& A, vector<long long>& B, long long k) {
    vector<long long> sums;

    for (long long a : A) {
        for (long long b : B) {
            sums.push_back(a + b);
        }
    }

    sort(sums.begin(), sums.end());
    return sums[k - 1];
}
```

## Brute-Force Detailed Walkthrough

```text
Generate:
1+4=5
1+5=6
1+6=7
2+4=6
2+5=7
2+6=8
3+4=7
3+5=8
3+6=9

After sorting:
1st = 5
2nd = 6
3rd = 6
4th = 7
5th = 7
6th = 7

answer = 7
```

## Brute-Force Thinking

```text
Generate all pair sums.
Sort them.
Return kth value.
```

## Why Brute Force Fails

```text
There are n * m pairs.
If n and m are large, memory and time explode.
```

## How Binary Search Works

Guess value `x`.

```text
Count how many pair sums <= x.
```

For every `a` in `A`:

```text
Need b <= x - a
Use upper_bound in B.
```

If count >= k, kth value is <= x.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countPairsLE(vector<long long>& A, vector<long long>& B, long long x) {
    long long cnt = 0;

    for (long long a : A) {
        cnt += upper_bound(B.begin(), B.end(), x - a) - B.begin();
    }

    return cnt;
}

long long kthPairSum(vector<long long> A, vector<long long> B, long long k) {
    sort(A.begin(), A.end());
    sort(B.begin(), B.end());

    long long lo = A.front() + B.front();
    long long hi = A.back() + B.back();
    long long ans = hi;

    while (lo <= hi) {
        long long mid = lo + (hi - lo) / 2;

        if (countPairsLE(A, B, mid) >= k) {
            ans = mid;
            hi = mid - 1;
        } else {
            lo = mid + 1;
        }
    }

    return ans;
}
```

## Step-by-Step Dry Run

```text
A = [1, 2, 3]
B = [4, 5, 6]
k = 6
```

All pair sums for understanding:

```text
1+4=5, 1+5=6, 1+6=7
2+4=6, 2+5=7, 2+6=8
3+4=7, 3+5=8, 3+6=9

Sorted:
[5, 6, 6, 7, 7, 7, 8, 8, 9]
6th = 7
```

### Try x = 7

```text
For a = 1:
Need b <= 7 - 1 = 6
B values <= 6 = [4,5,6]
count = 3

For a = 2:
Need b <= 5
B values <= 5 = [4,5]
count = 2

For a = 3:
Need b <= 4
B values <= 4 = [4]
count = 1

Total count = 3 + 2 + 1 = 6
6 >= k
x = 7 works.
```

### Try x = 6

```text
For a = 1: B <= 5 -> 2
For a = 2: B <= 4 -> 1
For a = 3: B <= 3 -> 0

Total = 3
3 < 6
x = 6 too small.

Answer = 7
```

## Recognition Signal

```text
kth smallest
cannot generate all pairs
count values <= mid
upper_bound inside check
```

---

# 13. Kth Smallest in Multiplication Table

## Difficulty

```text
Hard / CP + FAANG
```

## Form

```text
Implicit matrix + count <= mid
```

## Problem Statement

Given an `n x m` multiplication table, find kth smallest value.

## Input

```text
n = 3
m = 5
k = 7
```

## Output

```text
4
```


## Detailed Example

```text
n = 3, m = 5, k = 7
```

Table:

```text
1  2  3  4  5
2  4  6  8 10
3  6  9 12 15
```

Sorted values:

```text
1, 2, 2, 3, 3, 4, 4, 5, 6, 6, 8, 9, 10, 12, 15
```

The 7th smallest is `4`.

## Observation

```text
Row i is:
i, 2i, 3i, ..., m*i

How many values in row i are <= x?
Need j*i <= x
So j <= x/i
There are floor(x/i) such columns, capped by m.

count in row i = min(m, x / i)
```

## Brute-Force Code

```cpp
long long kthMultiplicationTableBrute(long long n, long long m, long long k) {
    vector<long long> values;

    for (long long i = 1; i <= n; i++) {
        for (long long j = 1; j <= m; j++) {
            values.push_back(i * j);
        }
    }

    sort(values.begin(), values.end());
    return values[k - 1];
}
```

## Brute-Force Detailed Walkthrough

```text
Generate row by row:
row 1: 1,2,3,4,5
row 2: 2,4,6,8,10
row 3: 3,6,9,12,15

Sort everything:
1,2,2,3,3,4,4,5,6,6,8,9,10,12,15

7th value = 4
```

## Brute-Force Thinking

```text
Generate all n*m values.
Sort them.
Return kth.
```

## Why Brute Force Fails

```text
n*m can be huge.
But values are countable without materializing table.
```

## How Binary Search Works

Guess value `x`.

In row `i`:

```text
values are i, 2i, 3i, ..., m*i
count <= x is min(m, x / i)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countLE(long long n, long long m, long long x) {
    long long cnt = 0;

    for (long long i = 1; i <= n; i++) {
        cnt += min(m, x / i);
    }

    return cnt;
}

long long kthMultiplicationTable(long long n, long long m, long long k) {
    long long lo = 1;
    long long hi = n * m;
    long long ans = hi;

    while (lo <= hi) {
        long long mid = lo + (hi - lo) / 2;

        if (countLE(n, m, mid) >= k) {
            ans = mid;
            hi = mid - 1;
        } else {
            lo = mid + 1;
        }
    }

    return ans;
}
```

## Step-by-Step Dry Run

```text
n = 3, m = 5, k = 7

Table:
1  2  3  4  5
2  4  6  8 10
3  6  9 12 15
```

Sorted values:

```text
1,2,2,3,3,4,4,5,6,6,8,9,10,12,15
7th = 4
```

### Try x = 5

```text
Row 1: min(5, 5/1) = 5
Row 2: min(5, 5/2) = 2
Row 3: min(5, 5/3) = 1

count = 8
8 >= 7
x = 5 works.
Try smaller.
```

### Try x = 3

```text
Row 1: min(5, 3/1) = 3
Row 2: min(5, 3/2) = 1
Row 3: min(5, 3/3) = 1

count = 5
5 < 7
x = 3 too small.
```

### Try x = 4

```text
Row 1: 4
Row 2: 2
Row 3: 1

count = 7
7 >= 7
Answer = 4
```

## Recognition Signal

```text
kth smallest in generated matrix/table
cannot build matrix
count <= x row by row
```

---

# Phase 5 — CP Form: Binary Search on Every Start

---

# 14. Count Subarrays with At Most K Zeros

## Difficulty

```text
Medium CP
```

## Form

```text
Binary search on every start
```

## Problem Statement

Given binary array, count subarrays having at most `k` zeros.

## Input

```text
arr = [1, 0, 0]
k = 1
```

## Output

```text
4
```


## Detailed Example

```text
arr = [1, 0, 0]
k = 1
```

All subarrays:

```text
[1]       zeros = 0 valid
[1,0]     zeros = 1 valid
[1,0,0]   zeros = 2 invalid
[0]       zeros = 1 valid
[0,0]     zeros = 2 invalid
[0]       zeros = 1 valid
```

Valid count = `4`.

## Observation

```text
For a fixed start, as end moves right, zero count never decreases.
So validity changes like:
valid valid valid invalid invalid

That means we can binary search the farthest valid end for each start.
```

## Brute-Force Code

```cpp
long long countAtMostKZerosBrute(vector<int>& arr, int k) {
    int n = arr.size();
    long long ans = 0;

    for (int l = 0; l < n; l++) {
        int zeros = 0;

        for (int r = l; r < n; r++) {
            if (arr[r] == 0) zeros++;

            if (zeros <= k) {
                ans++;
            }
        }
    }

    return ans;
}
```

## Brute-Force Detailed Walkthrough

```text
l = 0:
r = 0 -> [1], zeros 0 -> valid, ans = 1
r = 1 -> [1,0], zeros 1 -> valid, ans = 2
r = 2 -> [1,0,0], zeros 2 -> invalid

l = 1:
r = 1 -> [0], zeros 1 -> valid, ans = 3
r = 2 -> [0,0], zeros 2 -> invalid

l = 2:
r = 2 -> [0], zeros 1 -> valid, ans = 4
```

## Brute-Force Thinking

```text
Try every subarray.
Count zeros.
If zeros <= k, add to answer.
```

## Why Brute Force Fails

```text
O(n^2) subarrays.
Need faster.
```

## How Binary Search Works

For each start index `st`, valid end positions are monotonic.

```text
valid valid valid invalid invalid
                  ^
                  farthest valid end
```

Use prefix zeros to check zeros in O(1).

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countAtMostKZeros(vector<int>& arr, int k) {
    int n = arr.size();
    vector<int> pref(n + 1, 0);

    for (int i = 0; i < n; i++) {
        pref[i + 1] = pref[i] + (arr[i] == 0);
    }

    long long ans = 0;

    for (int st = 0; st < n; st++) {
        int lo = st, hi = n - 1;
        int best = st - 1;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int zeros = pref[mid + 1] - pref[st];

            if (zeros <= k) {
                best = mid;
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }

        ans += best - st + 1;
    }

    return ans;
}
```

## Step-by-Step Index Dry Run

```text
arr = [1, 0, 0]
k = 1
index  0  1  2
```

Prefix zeros:

```text
pref[0] = 0
pref[1] = 0
pref[2] = 1
pref[3] = 2
```

### Start st = 0

```text
Possible ends:
end = 0 -> [1]       zeros = 0 valid
end = 1 -> [1,0]     zeros = 1 valid
end = 2 -> [1,0,0]   zeros = 2 invalid

Farthest valid end = 1
Subarrays from st = 0:
[1]
[1,0]
count = 2
```

### Start st = 1

```text
end = 1 -> [0]     zeros = 1 valid
end = 2 -> [0,0]   zeros = 2 invalid

count = 1
```

### Start st = 2

```text
end = 2 -> [0] zeros = 1 valid

count = 1
```

```text
Total = 2 + 1 + 1 = 4
```

## Recognition Signal

```text
for every start, find farthest valid end
subarray condition becomes invalid after some point
prefix + binary search
```

---

# 15. Maximum Ones After At Most K Flips

## Difficulty

```text
Medium / FAANG
```

## Form

```text
Binary search on length
```

## Problem Statement

Given binary array and `k`, flip at most `k` zeros. Find maximum length subarray of all ones after flips.

## Input

```text
arr = [1,1,1,0,1,0,0,1,1]
k = 2
```

## Output

```text
6
```


## Detailed Example

```text
arr = [1,1,1,0,1,0,0,1,1]
k = 2
```

Choose window `0..5`:

```text
[1,1,1,0,1,0]
```

It has two zeros. Flip both zeros to ones:

```text
[1,1,1,1,1,1]
```

Length = `6`.

No window of length `7` has at most two zeros, so answer is `6`.

## Observation

```text
If length L is possible, all smaller lengths are also possible.
If length L is impossible, all larger lengths are also impossible.

possible possible possible impossible impossible
                      ^
                      last true
```

## Brute-Force Code

```cpp
int longestOnesBrute(vector<int>& arr, int k) {
    int n = arr.size();
    int best = 0;

    for (int l = 0; l < n; l++) {
        int zeros = 0;

        for (int r = l; r < n; r++) {
            if (arr[r] == 0) zeros++;

            if (zeros <= k) {
                best = max(best, r - l + 1);
            }
        }
    }

    return best;
}
```

## Brute-Force Detailed Walkthrough

```text
Check subarrays and keep longest with <= 2 zeros.

For l = 0:
r = 0..5 gives [1,1,1,0,1,0], zeros = 2, length = 6
r = 6 gives [1,1,1,0,1,0,0], zeros = 3, invalid

For other l values, no valid subarray longer than 6.

answer = 6
```

## Brute-Force Thinking

```text
Try all subarrays.
Count zeros.
If zeros <= k, update max length.
```

## Better Binary Search Thinking

Guess length `len`.

```text
Is there any window of length len with zeros <= k?
```

If length works, smaller lengths also work.

```text
Y Y Y Y N N N
        ^
        last true
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canMakeLength(vector<int>& arr, int k, int len) {
    int n = arr.size();
    vector<int> pref(n + 1, 0);

    for (int i = 0; i < n; i++) {
        pref[i + 1] = pref[i] + (arr[i] == 0);
    }

    for (int l = 0; l + len <= n; l++) {
        int r = l + len - 1;
        int zeros = pref[r + 1] - pref[l];

        if (zeros <= k) return true;
    }

    return false;
}

int longestOnesBS(vector<int>& arr, int k) {
    int n = arr.size();
    int lo = 0, hi = n;
    int ans = 0;

    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;

        if (canMakeLength(arr, k, mid)) {
            ans = mid;
            lo = mid + 1;
        } else {
            hi = mid - 1;
        }
    }

    return ans;
}
```

## Step-by-Step Dry Run

```text
arr = [1,1,1,0,1,0,0,1,1]
k = 2
index  0 1 2 3 4 5 6 7 8
```

### Try len = 6

```text
Window 0..5:
[1,1,1,0,1,0]
zeros = 2

2 <= k
len = 6 works.
```

### Try len = 7

```text
Window 0..6:
[1,1,1,0,1,0,0]
zeros = 3 invalid

Window 1..7:
[1,1,0,1,0,0,1]
zeros = 3 invalid

Window 2..8:
[1,0,1,0,0,1,1]
zeros = 3 invalid

No valid window.
len = 7 does not work.
```

```text
Answer = 6
```

## Recognition Signal

```text
maximum length
window valid/invalid
can check fixed length
```

---

# Phase 6 — Real Domain Binary Search

---

# 16. Square Root with Precision

## Difficulty

```text
Medium
```

## Form

```text
Real domain binary search
```

## Problem Statement

Find square root of a number with precision.

## Input

```text
x = 2
```

## Output

```text
1.41421356237
```


## Detailed Example

```text
x = 2
```

We need a decimal value `y` such that:

```text
y * y ≈ 2
```

The answer is approximately:

```text
1.41421356237
```

## Observation

```text
For candidate mid:
if mid * mid >= x, mid is high enough, move left.
if mid * mid < x, mid is too small, move right.
```

Real binary search does not use `mid + 1` or `mid - 1`.

## Brute-Force Code

```cpp
long double sqrtPrecisionBrute(long double x) {
    long double ans = 0;
    long double step = 0.000001;

    for (long double y = 0; y * y <= x; y += step) {
        ans = y;
    }

    return ans;
}
```

## Brute-Force Detailed Walkthrough

```text
Try y = 0.0 -> square = 0
Try y = 1.0 -> square = 1
Try y = 1.4 -> square = 1.96
Try y = 1.41 -> square = 1.9881
Try y = 1.414 -> square = 1.999396
Try y = 1.415 -> square = 2.002225, crossed 2

answer is near 1.414
```

## Brute-Force Thinking

```text
Try decimal values one by one.
Very slow and inaccurate.
```

## How Binary Search Works

Search over real values.

```text
check(mid): mid * mid >= x
```

Unlike integer binary search:

```text
lo = mid
hi = mid
```

because there is no next real number.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long double sqrtPrecision(long double x) {
    long double lo = 0;
    long double hi = max((long double)1.0, x);

    for (int it = 0; it < 100; it++) {
        long double mid = (lo + hi) / 2;

        if (mid * mid >= x) {
            hi = mid;
        } else {
            lo = mid;
        }
    }

    return (lo + hi) / 2;
}
```

## Step-by-Step Dry Run

```text
x = 2
lo = 0
hi = 2
```

```text
Step 1:
mid = 1
1 * 1 = 1 < 2
Need bigger.
lo = 1
```

```text
Step 2:
mid = 1.5
1.5 * 1.5 = 2.25 >= 2
Need smaller.
hi = 1.5
```

```text
Step 3:
mid = 1.25
1.25 * 1.25 = 1.5625 < 2
lo = 1.25
```

```text
After many iterations:
answer ≈ 1.41421356237
```

## Recognition Signal

```text
decimal answer
precision required
continuous monotonic function
```

---

# Phase 7 — Ternary Search / Unimodal Function

---

# 17. Freefall / Minimum Time Function

## Difficulty

```text
Hard CP
```

## Form

```text
Ternary search on unimodal function
```

## Problem Statement

You can perform `x` operations. Each operation costs `B` time. Falling time becomes:

```text
A / sqrt(x + 1)
```

Total time:

```text
f(x) = B*x + A / sqrt(x + 1)
```

Find minimum value.

## Input

```text
A = 10
B = 1
```

## Output

```text
approximately 7.7735
```


## Detailed Example

```text
A = 10
B = 1
f(x) = x + 10 / sqrt(x + 1)
```

Evaluate small values:

```text
x = 0 -> 10.000
x = 1 -> 8.071
x = 2 -> 7.773
x = 3 -> 8.000
```

Minimum is near `x = 2`.

## Observation

```text
The function has two forces:

B*x increases as x increases.
A/sqrt(x+1) decreases as x increases.

Total first decreases, then increases.
This is a unimodal / valley-shaped function.
```

## Brute-Force Code

```cpp
long double freefallBrute(long double A, long double B) {
    auto f = [&](long long x) {
        return B * x + A / sqrt((long double)x + 1);
    };

    long long hi = (long long)(A / B) + 10;
    long double best = f(0);

    for (long long x = 0; x <= hi; x++) {
        best = min(best, f(x));
    }

    return best;
}
```

## Brute-Force Detailed Walkthrough

```text
A = 10, B = 1

x = 0: f = 0 + 10/sqrt(1) = 10
x = 1: f = 1 + 10/sqrt(2) = 8.071
x = 2: f = 2 + 10/sqrt(3) = 7.773
x = 3: f = 3 + 10/sqrt(4) = 8

Best among these = 7.773 at x = 2
```

## Brute-Force Thinking

```text
Try all possible x and compute f(x).
```

## Why Brute Force Fails

```text
x can be huge.
But function is valley-shaped.
```

## How Ternary Search Works

The function has two parts:

```text
B*x increases as x increases.
A/sqrt(x+1) decreases as x increases.
```

So total first decreases, then increases.

```text
\      /
 \    /
  \__/   minimum
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ld = long double;
using ll = long long;

ld A, B;

ld f(ll x) {
    return B * x + A / sqrt((ld)x + 1);
}

int main() {
    cin >> A >> B;

    ll lo = 0;
    ll hi = (ll)(A / B) + 10;

    while (hi - lo > 3) {
        ll m1 = lo + (hi - lo) / 3;
        ll m2 = hi - (hi - lo) / 3;

        if (f(m1) < f(m2)) {
            hi = m2;
        } else {
            lo = m1;
        }
    }

    ld ans = f(lo);

    for (ll x = lo; x <= hi; x++) {
        ans = min(ans, f(x));
    }

    cout << fixed << setprecision(15) << ans << "\n";
}
```

## Step-by-Step Dry Run

```text
A = 10
B = 1

f(x) = x + 10 / sqrt(x + 1)
```

```text
x = 0:
f(0) = 0 + 10/sqrt(1) = 10
```

```text
x = 1:
f(1) = 1 + 10/sqrt(2)
     ≈ 1 + 7.071
     = 8.071
```

```text
x = 2:
f(2) = 2 + 10/sqrt(3)
     ≈ 2 + 5.773
     = 7.773
```

```text
x = 3:
f(3) = 3 + 10/sqrt(4)
     = 3 + 5
     = 8
```

```text
Minimum near x = 2.
Answer ≈ 7.773
```

## Recognition Signal

```text
one term increasing
one term decreasing
function first decreases then increases
minimum/maximum of unimodal function
```

---

# Binary Search Recognition Table

| Problem Signal | Pattern | Search Type |
|---|---|---|
| first index satisfying condition | first true | classic BS |
| first element >= x | lower bound | first true |
| first element > x | upper bound | first true |
| rotated sorted array | sorted half check | modified BS |
| find peak | compare mid and mid+1 | slope BS |
| minimize maximum | can answer <= mid? | first true |
| maximize minimum | can answer >= mid? | last true |
| minimum time | can produce in mid time? | first true |
| kth smallest | count <= mid | first true |
| generated matrix/table | count without building | answer BS |
| every start find farthest end | valid ends are monotone | last true per start |
| decimal answer | precision search | real BS |
| hill/valley function | unimodal | ternary search |

---

# Template Library

## First True Template

```cpp
long long firstTrue(long long lo, long long hi) {
    long long ans = hi + 1;

    while (lo <= hi) {
        long long mid = lo + (hi - lo) / 2;

        if (check(mid)) {
            ans = mid;
            hi = mid - 1;
        } else {
            lo = mid + 1;
        }
    }

    return ans;
}
```

## Last True Template

```cpp
long long lastTrue(long long lo, long long hi) {
    long long ans = lo - 1;

    while (lo <= hi) {
        long long mid = lo + (hi - lo) / 2;

        if (check(mid)) {
            ans = mid;
            lo = mid + 1;
        } else {
            hi = mid - 1;
        }
    }

    return ans;
}
```

## Real Binary Search Template

```cpp
long double realBinarySearch(long double lo, long double hi) {
    for (int it = 0; it < 100; it++) {
        long double mid = (lo + hi) / 2;

        if (check(mid)) {
            hi = mid;
        } else {
            lo = mid;
        }
    }

    return (lo + hi) / 2;
}
```

## Ternary Search Template

```cpp
long double ternarySearch(long double lo, long double hi) {
    for (int it = 0; it < 200; it++) {
        long double m1 = lo + (hi - lo) / 3;
        long double m2 = hi - (hi - lo) / 3;

        if (f(m1) < f(m2)) {
            hi = m2;
        } else {
            lo = m1;
        }
    }

    return f((lo + hi) / 2);
}
```

---

# Common Mistakes

## Mistake 1 — Bad Check Function

Wrong thinking:

```text
check(mid) asks whether mid is exactly answer.
```

Correct thinking:

```text
check(mid) asks whether mid is possible.
```

Examples:

```text
Can max sum be <= mid?
Can time mid produce enough items?
Can distance mid be maintained?
Are at least k values <= mid?
```

---

## Mistake 2 — Wrong First True / Last True Direction

```text
Minimize maximum → first true
Maximize minimum → last true
Minimum time     → first true
Maximum length   → last true
```

---

## Mistake 3 — Integer vs Real Boundary Update

Integer binary search:

```cpp
lo = mid + 1;
hi = mid - 1;
```

Real binary search:

```cpp
lo = mid;
hi = mid;
```

---

## Mistake 4 — Overflow

Risky:

```cpp
mid * mid * mid <= x
```

Safe:

```cpp
mid <= x / mid / mid
```

---

## Mistake 5 — Wrong Bounds

| Problem | lo | hi |
|---|---|---|
| Split array | max(nums) | sum(nums) |
| Factory machines | 0 | fastest * target |
| Aggressive cows | 0 | max position - min position |
| Kth pair sum | minA + minB | maxA + maxB |
| Multiplication table | 1 | n * m |
| Real sqrt | 0 | max(1, x) |

---

# Practice Order

```text
Phase 1:
1. First 1 in Binary Array
2. Lower Bound
3. Upper Bound
4. Search Insert Position

Phase 2:
5. Search in Rotated Sorted Array
6. Find Minimum in Rotated Sorted Array
7. Peak Element

Phase 3:
8. Split Array Largest Sum
9. Factory Machines
10. Aggressive Cows
11. Minimize Maximum Gap

Phase 4:
12. Kth Smallest Pair Sum
13. Kth Smallest in Multiplication Table

Phase 5:
14. Count Subarrays with At Most K Zeros
15. Maximum Ones After K Flips

Phase 6:
16. Square Root with Precision

Phase 7:
17. Freefall / Ternary Search
```

---

# Final Mental Model

```text
Binary Search = Boundary Search

Step 1: What answer am I searching?
Step 2: Can I guess it?
Step 3: Can I check it?
Step 4: Is check monotonic?
Step 5: First true or last true?
```

```text
For FAANG:
Master classic + answer BS + kth counting.

For CP:
Master answer BS + every-start BS + real/ternary search.
```

---

# END
