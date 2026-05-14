# Binary Search Phase-Wise CP + DSA Handbook

> Built from the uploaded Binary Search handwritten PDFs and the existing binary-search markdown note.
>
> Goal:
>
> ```text
> Read problem -> define search space -> design monotone check -> choose first true / last true -> code fast.
> ```

---

# Clickable Index

## 0. Mental Map
- [0.1 Binary Search Master Map](#01-binary-search-master-map)
- [0.2 First True vs Last True](#02-first-true-vs-last-true)
- [0.3 How to Think About `check(mid)`](#03-how-to-think-about-checkmid)
- [0.4 CP / DSA Recognition Signals](#04-cp--dsa-recognition-signals)

## Phase 1 — Binary Search Foundation
- [1. First 1 in a Monotone Binary Array](#1-first-1-in-a-monotone-binary-array)
- [2. Lower Bound — First Element `>= x`](#2-lower-bound--first-element--x)
- [3. Upper Bound — First Element `> x`](#3-upper-bound--first-element--x)

## Phase 2 — Classic Index Binary Search
- [4. Search Insert Position](#4-search-insert-position)
- [5. Rotation Count in Rotated Sorted Array](#5-rotation-count-in-rotated-sorted-array)
- [6. Peak Element / Bitonic Peak](#6-peak-element--bitonic-peak)

## Phase 3 — Binary Search on Answer
- [7. Painter Partition / Split Array Largest Sum](#7-painter-partition--split-array-largest-sum)
- [8. Factory Machines](#8-factory-machines)
- [9. Aggressive Cows — Maximize Minimum Distance](#9-aggressive-cows--maximize-minimum-distance)
- [10. Minimize Maximum Gap After Adding K Points](#10-minimize-maximum-gap-after-adding-k-points)

## Phase 4 — Kth / Counting / Generated Values
- [11. Kth Pair Sum from Two Arrays](#11-kth-pair-sum-from-two-arrays)
- [12. Kth Smallest in Multiplication Table](#12-kth-smallest-in-multiplication-table)

## Phase 5 — Binary Search on Every Start
- [13. Largest Ones After At Most K Flips](#13-largest-ones-after-at-most-k-flips)
- [14. Count Subarrays with At Most K Zeros](#14-count-subarrays-with-at-most-k-zeros)
- [15. Count Subarrays with At Most K Distinct Elements](#15-count-subarrays-with-at-most-k-distinct-elements)

## Phase 6 — Real Domain Binary Search
- [16. Square Root / Real Binary Search](#16-square-root--real-binary-search)

## Phase 7 — Ternary Search
- [17. Ternary Search Foundation](#17-ternary-search-foundation)
- [18. Freefall Problem](#18-freefall-problem)

## Phase 8 — Drill Problems
- [19. Sum of Cubes](#19-sum-of-cubes)

## Final Revision
- [Binary Search Decision Table](#binary-search-decision-table)
- [Must-Solve Phase Order](#must-solve-phase-order)
- [Template Library](#template-library)
- [Common Mistakes](#common-mistakes)

---

# 0. Mental Map

## 0.1 Binary Search Master Map

```text
Binary Search
│
├── Classic Index Search
│   ├── lower_bound
│   ├── upper_bound
│   ├── rotated array
│   └── peak / bitonic
│
├── Binary Search on Answer
│   ├── minimize maximum
│   ├── maximize minimum
│   ├── minimum time
│   ├── kth smallest
│   └── generated implicit values
│
├── Binary Search on Every Start
│   ├── fix start
│   ├── find farthest valid end
│   └── count all valid subarrays
│
├── Real Domain Binary Search
│   ├── decimal precision
│   └── EPS / fixed iterations
│
└── Ternary Search
    ├── hill shaped function
    └── valley shaped function
```

---

## 0.2 First True vs Last True

### First True

Use when:

```text
N N N N Y Y Y Y
        ^
        answer
```

Example:

```text
minimum time
minimum maximum
first index >= x
```

### Last True

Use when:

```text
Y Y Y Y N N N N
      ^
      answer
```

Example:

```text
maximum minimum distance
maximum valid length
last possible value
```

---

## 0.3 How to Think About `check(mid)`

Your handwritten notes emphasize:

```text
Do not ask:
"Is mid exactly the answer?"

Ask:
"Can answer be <= mid?"
"Can work be completed in mid time?"
"Can distance mid be achieved?"
"Are at least k values <= mid?"
```

A good `check(mid)` creates monotonicity:

```text
NO NO NO YES YES YES
```

or:

```text
YES YES YES NO NO NO
```

---

## 0.4 CP / DSA Recognition Signals

| Problem wording | Pattern |
|---|---|
| first index satisfying condition | First true |
| first element >= x | Lower bound |
| first element > x | Upper bound |
| minimize maximum | Binary search on answer |
| maximize minimum | Binary search on answer |
| minimum time | Binary search on answer |
| kth smallest | Count <= mid |
| cannot generate all values | Count instead of generate |
| fixed start, farthest end | Binary search on every start |
| decimal precision | Real-domain binary search |
| function decreases then increases | Ternary search |
| multiplication overflow risk | Divide-and-check |

---

# Phase 1 — Binary Search Foundation

## 1. First 1 in a Monotone Binary Array

### Problem Statement

Given a monotone binary array where all `0`s come before all `1`s, find the first index containing `1`.

### Input

```text
arr = [0, 0, 0, 0, 0, 0, 1, 1, 1]
```

### Output

```text
6
```

### Why This Pattern Works

The array has a clean boundary:

```text
0 0 0 0 0 0 1 1 1
            ^
            first 1
```

If `arr[mid] == 1`, answer may be at `mid` or left.  
If `arr[mid] == 0`, answer must be on the right.

### C++ Code

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

int main() {
    vector<int> arr = {0,0,0,0,0,0,1,1,1};
    cout << firstOne(arr) << "\n";
}
```

### Step-by-Step Dry Run

```text
arr = [0,0,0,0,0,0,1,1,1]

index:  0 1 2 3 4 5 6 7 8
arr:   [0 0 0 0 0 0 1 1 1]
```

```text
Step 1

lo = 0, hi = 8
mid = 4

index:  0 1 2 3 4 5 6 7 8
arr:   [0 0 0 0 0 0 1 1 1]
                 ^

arr[4] = 0

action:
0 means first 1 is on the right
lo = mid + 1 = 5
```

```text
Step 2

lo = 5, hi = 8
mid = 6

index:  0 1 2 3 4 5 6 7 8
arr:   [0 0 0 0 0 0 1 1 1]
                     ^

arr[6] = 1

action:
ans = 6
try earlier 1
hi = mid - 1 = 5
```

```text
Step 3

lo = 5, hi = 5
mid = 5

arr[5] = 0

action:
lo = 6
```

```text
Stop:
lo = 6, hi = 5

answer = 6
```

### Mental Note

```text
Binary search finds the boundary between two zones.
```

---

## 2. Lower Bound — First Element `>= x`

### Problem Statement

Given sorted array and value `x`, return the first index where `arr[i] >= x`.

### Input

```text
arr = [2, 3, 3, 7, 9, 11, 11, 17, 19]
x = 11
```

### Output

```text
5
```

### Why This Pattern Works

Convert to check array:

```text
check(i) = arr[i] >= x

arr   = [2, 3, 3, 7, 9, 11, 11, 17, 19]
check = [0, 0, 0, 0, 0,  1,  1,  1,  1]
```

This is first true.

### C++ Code

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

int main() {
    vector<int> arr = {2,3,3,7,9,11,11,17,19};
    int x = 11;

    cout << lowerBoundManual(arr, x) << "\n";
}
```

### Step-by-Step Dry Run

```text
arr = [2, 3, 3, 7, 9, 11, 11, 17, 19]
x = 11

index:  0  1  2  3  4   5   6   7   8
arr:   [2, 3, 3, 7, 9, 11, 11, 17, 19]
```

```text
Step 1

lo = 0, hi = 8
mid = 4

arr[4] = 9

9 >= 11 ? no

action:
lo = 5
```

```text
Step 2

lo = 5, hi = 8
mid = 6

arr[6] = 11

11 >= 11 ? yes

action:
ans = 6
hi = 5
```

```text
Step 3

lo = 5, hi = 5
mid = 5

arr[5] = 11

11 >= 11 ? yes

action:
ans = 5
hi = 4
```

```text
Stop:
answer = 5
```

---

## 3. Upper Bound — First Element `> x`

### Problem Statement

Given sorted array and value `x`, return the first index where `arr[i] > x`.

### Input

```text
arr = [2, 3, 3, 7, 9, 11, 11, 17, 19]
x = 11
```

### Output

```text
7
```

### C++ Code

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

int main() {
    vector<int> arr = {2,3,3,7,9,11,11,17,19};
    int x = 11;

    cout << upperBoundManual(arr, x) << "\n";
}
```

### Step-by-Step Dry Run

```text
check(i) = arr[i] > 11

index:  0  1  2  3  4   5   6   7   8
arr:   [2, 3, 3, 7, 9, 11, 11, 17, 19]
check: [0, 0, 0, 0, 0,  0,  0,  1,  1]
```

```text
Step 1

lo = 0, hi = 8
mid = 4
arr[4] = 9

9 > 11 ? no
lo = 5
```

```text
Step 2

lo = 5, hi = 8
mid = 6
arr[6] = 11

11 > 11 ? no
lo = 7
```

```text
Step 3

lo = 7, hi = 8
mid = 7
arr[7] = 17

17 > 11 ? yes
ans = 7
hi = 6
```

```text
answer = 7
```

---

# Phase 2 — Classic Index Binary Search

## 4. Search Insert Position

### Problem Statement

Given sorted array and target, return its index if found. Otherwise return the index where it should be inserted.

### Input

```text
nums = [1, 3, 5, 6]
target = 5
```

### Output

```text
2
```

### Pattern

```text
lower_bound(target)
```

### C++ Code

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

int main() {
    vector<int> nums = {1,3,5,6};
    int target = 5;

    cout << searchInsert(nums, target) << "\n";
}
```

### Step-by-Step Dry Run

```text
nums = [1, 3, 5, 6]
target = 5

index:   0   1   2   3
nums:   [1,  3,  5,  6]
```

```text
Step 1

lo = 0, hi = 3
mid = 1

nums[1] = 3

3 >= 5 ? no
lo = 2
```

```text
Step 2

lo = 2, hi = 3
mid = 2

nums[2] = 5

5 >= 5 ? yes
ans = 2
hi = 1
```

```text
answer = 2
```

---

## 5. Rotation Count in Rotated Sorted Array

### Problem Statement

Given a rotated sorted array with distinct elements, find how many times it was rotated.

Rotation count = index of minimum element.

### Input

```text
arr = [8, 1, 2, 3, 5]
```

### Output

```text
1
```

### Why This Pattern Works

If:

```text
arr[mid] > arr[hi]
```

then minimum is on the right side.

Otherwise:

```text
minimum is at mid or on the left side.
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int rotationCount(vector<int>& arr) {
    int lo = 0;
    int hi = arr.size() - 1;

    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;

        if (arr[mid] > arr[hi]) {
            lo = mid + 1;
        } else {
            hi = mid;
        }
    }

    return lo;
}

int main() {
    vector<int> arr = {8,1,2,3,5};
    cout << rotationCount(arr) << "\n";
}
```

### Step-by-Step Dry Run

```text
arr = [8, 1, 2, 3, 5]

index:  0  1  2  3  4
arr:   [8, 1, 2, 3, 5]
```

```text
Step 1

lo = 0, hi = 4
mid = 2

arr[mid] = 2
arr[hi]  = 5

2 > 5 ? no
hi = mid = 2
```

```text
Step 2

lo = 0, hi = 2
mid = 1

arr[mid] = 1
arr[hi]  = 2

1 > 2 ? no
hi = mid = 1
```

```text
Step 3

lo = 0, hi = 1
mid = 0

arr[mid] = 8
arr[hi]  = 1

8 > 1 ? yes
lo = mid + 1 = 1
```

```text
Stop:
lo = hi = 1

answer = 1
```

---

## 6. Peak Element / Bitonic Peak

### Problem Statement

Given a bitonic array that increases then decreases, find the peak index.

### Input

```text
arr = [1, 3, 5, 9, 7, 5]
```

### Output

```text
3
```

### Why This Pattern Works

Look at slope:

```text
if arr[mid] > arr[mid + 1]
    peak is at mid or left
else
    peak is right
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int findPeak(vector<int>& arr) {
    int lo = 0;
    int hi = arr.size() - 1;

    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;

        if (arr[mid] > arr[mid + 1]) {
            hi = mid;
        } else {
            lo = mid + 1;
        }
    }

    return lo;
}

int main() {
    vector<int> arr = {1,3,5,9,7,5};
    cout << findPeak(arr) << "\n";
}
```

### Step-by-Step Dry Run

```text
arr = [1, 3, 5, 9, 7, 5]

index:  0  1  2  3  4  5
arr:   [1, 3, 5, 9, 7, 5]
```

```text
Step 1

lo = 0, hi = 5
mid = 2

arr[2] = 5
arr[3] = 9

5 > 9 ? no

we are climbing
lo = mid + 1 = 3
```

```text
Step 2

lo = 3, hi = 5
mid = 4

arr[4] = 7
arr[5] = 5

7 > 5 ? yes

we are descending
hi = mid = 4
```

```text
Step 3

lo = 3, hi = 4
mid = 3

arr[3] = 9
arr[4] = 7

9 > 7 ? yes
hi = 3
```

```text
answer = 3
```

---

# Phase 3 — Binary Search on Answer

## 7. Painter Partition / Split Array Largest Sum

### Problem Statement

Given array `arr` and `k`, split array into `k` contiguous parts. Minimize the largest part sum.

### Input

```text
arr = [2, 7, 1, 8, 3, 4, 5]
k = 3
```

### Output

```text
11
```

### Why This Pattern Works

If maximum allowed sum `T` works, any larger value also works.

```text
T:      8  9  10  11  12 ...
check:  N  N   N   Y   Y
```

### Search Space

```text
lo = max(arr)
hi = sum(arr)
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canSplit(vector<int>& arr, int k, long long limit) {
    int parts = 1;
    long long current = 0;

    for (int x : arr) {
        if (current + x <= limit) {
            current += x;
        } else {
            parts++;
            current = x;
        }
    }

    return parts <= k;
}

long long splitArrayLargestSum(vector<int>& arr, int k) {
    long long lo = 0, hi = 0;

    for (int x : arr) {
        lo = max(lo, (long long)x);
        hi += x;
    }

    long long ans = hi;

    while (lo <= hi) {
        long long mid = lo + (hi - lo) / 2;

        if (canSplit(arr, k, mid)) {
            ans = mid;
            hi = mid - 1;
        } else {
            lo = mid + 1;
        }
    }

    return ans;
}

int main() {
    vector<int> arr = {2,7,1,8,3,4,5};
    int k = 3;

    cout << splitArrayLargestSum(arr, k) << "\n";
}
```

### Step-by-Step Dry Run

```text
arr = [2, 7, 1, 8, 3, 4, 5]
k = 3

lo = 8
hi = 30
```

```text
Try mid = 19

arr: [2, 7, 1, 8, 3, 4, 5]

Part 1:
2 + 7 + 1 + 8 = 18
next 3 makes 21 > 19

Part 2:
3 + 4 + 5 = 12

parts = 2 <= 3

check(19) = true
ans = 19
hi = 18
```

```text
Try mid = 13

Part 1:
2 + 7 + 1 = 10
next 8 makes 18 > 13

Part 2:
8 + 3 = 11
next 4 makes 15 > 13

Part 3:
4 + 5 = 9

parts = 3

check(13) = true
ans = 13
hi = 12
```

```text
Try mid = 10

Part 1:
2 + 7 + 1 = 10

Part 2:
8

Part 3:
3 + 4 = 7

Part 4:
5

parts = 4 > 3

check(10) = false
lo = 11
```

```text
Try mid = 11

Part 1:
2 + 7 + 1 = 10

Part 2:
8 + 3 = 11

Part 3:
4 + 5 = 9

parts = 3

check(11) = true
ans = 11
```

```text
answer = 11
```

---

## 8. Factory Machines

### Problem Statement

There are `n` machines. Machine `i` makes one product in `machine[i]` time. Find minimum time to make at least `target` products.

### Input

```text
machines = [2, 3, 7]
target = 10
```

### Output

```text
12
```

### Why This Pattern Works

For time `T`:

```text
products = sum(T / machine[i])
```

If `T` works, all larger times also work.

### C++ Code

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

long long minTime(vector<long long>& machines, long long target) {
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

int main() {
    vector<long long> machines = {2,3,7};
    long long target = 10;

    cout << minTime(machines, target) << "\n";
}
```

### Step-by-Step Dry Run

```text
machines = [2, 3, 7]
target = 10

lo = 0
hi = 2 * 10 = 20
```

```text
Try mid = 10

machine 2: 10 / 2 = 5
machine 3: 10 / 3 = 3
machine 7: 10 / 7 = 1

total = 9

9 >= 10 ? no
lo = 11
```

```text
Try mid = 15

machine 2: 15 / 2 = 7
machine 3: 15 / 3 = 5
machine 7: 15 / 7 = 2

total = 14

14 >= 10 ? yes
ans = 15
hi = 14
```

```text
Try mid = 12

machine 2: 6
machine 3: 4
machine 7: 1

total = 11

11 >= 10 ? yes
ans = 12
hi = 11
```

```text
Try mid = 11

machine 2: 5
machine 3: 3
machine 7: 1

total = 9

9 >= 10 ? no
lo = 12
```

```text
answer = 12
```

---

## 9. Aggressive Cows — Maximize Minimum Distance

### Problem Statement

Given sorted/unsorted stall positions and `k` cows, maximize the minimum distance between any two cows.

### Input

```text
positions = [1, 2, 4, 8, 9]
k = 3
```

### Output

```text
3
```

### Why This Pattern Works

If distance `d` is possible, smaller distances are also possible.

```text
d:      1 2 3 4 5
check:  Y Y Y N N
```

Use last true.

### C++ Code

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

int main() {
    vector<long long> pos = {1,2,4,8,9};
    int k = 3;

    cout << aggressiveCows(pos, k) << "\n";
}
```

### Step-by-Step Dry Run

```text
positions = [1, 2, 4, 8, 9]
k = 3
```

```text
Try distance = 4

place cow at 1

next must be >= 5
place cow at 8

next must be >= 12
not possible

placed = 2 < 3

check(4) = false
```

```text
Try distance = 2

place at 1
next >= 3 -> place at 4
next >= 6 -> place at 8

placed = 3

check(2) = true
```

```text
Try distance = 3

place at 1
next >= 4 -> place at 4
next >= 7 -> place at 8

placed = 3

check(3) = true
```

```text
answer = 3
```

---

## 10. Minimize Maximum Gap After Adding K Points

### Problem Statement

Given sorted positions. Add at most `k` extra points to minimize the maximum adjacent gap.

### Input

```text
positions = [0, 50, 100]
k = 2
```

### Output

```text
25
```

### Why This Pattern Works

Guess allowed maximum gap `x`.

For a gap `d`, extra points needed:

```text
ceil(d / x) - 1
```

If total needed `<= k`, then `x` works.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canLimitGap(vector<long long>& pos, long long k, long long x) {
    if (x == 0) return false;

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

int main() {
    vector<long long> pos = {0,50,100};
    long long k = 2;

    cout << minimizeMaxGap(pos, k) << "\n";
}
```

### Step-by-Step Dry Run

```text
positions = [0, 50, 100]
k = 2

gaps:
50 and 50
```

```text
Try x = 25

gap 50:
ceil(50 / 25) - 1 = 2 - 1 = 1

two gaps:
need = 1 + 1 = 2

need <= k

x = 25 works
```

```text
Try x = 24

gap 50:
ceil(50 / 24) - 1 = 3 - 1 = 2

two gaps:
need = 2 + 2 = 4

4 > 2

x = 24 fails
```

```text
answer = 25
```

---

# Phase 4 — Kth / Counting / Generated Values

## 11. Kth Pair Sum from Two Arrays

### Problem Statement

Given arrays `A` and `B`, all pair sums are generated:

```text
C = A[i] + B[j]
```

Find kth smallest pair sum without building all pairs.

### Input

```text
A = [1, 2, 3]
B = [4, 5, 6]
k = 6
```

### Output

```text
7
```

### Why This Pattern Works

Guess value `x`.

Count how many pairs have sum `<= x`.

If count `>= k`, kth value is `<= x`.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countPairsLE(vector<long long>& A, vector<long long>& B, long long x) {
    long long count = 0;

    for (long long a : A) {
        count += upper_bound(B.begin(), B.end(), x - a) - B.begin();
    }

    return count;
}

long long kthPairSum(vector<long long> A, vector<long long> B, long long k) {
    sort(A.begin(), A.end());
    sort(B.begin(), B.end());

    if (A.size() > B.size()) swap(A, B);

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

int main() {
    vector<long long> A = {1,2,3};
    vector<long long> B = {4,5,6};
    long long k = 6;

    cout << kthPairSum(A, B, k) << "\n";
}
```

### Step-by-Step Dry Run

```text
A = [1, 2, 3]
B = [4, 5, 6]

All sums:
5, 6, 7
6, 7, 8
7, 8, 9

Sorted:
[5, 6, 6, 7, 7, 7, 8, 8, 9]

k = 6
answer = 7
```

```text
Check x = 7

For A[0] = 1:
need B <= 7 - 1 = 6
B values: 4,5,6 -> 3

For A[1] = 2:
need B <= 5
B values: 4,5 -> 2

For A[2] = 3:
need B <= 4
B values: 4 -> 1

total = 3 + 2 + 1 = 6

count >= k
x works
```

```text
Check x = 6

A[0] = 1:
B <= 5 -> 2

A[1] = 2:
B <= 4 -> 1

A[2] = 3:
B <= 3 -> 0

total = 3

3 < 6
x too small
```

```text
answer = 7
```

---

## 12. Kth Smallest in Multiplication Table

### Problem Statement

Given an `n x m` multiplication table, find kth smallest value.

### Input

```text
n = 3
m = 5
k = 7
```

### Output

```text
4
```

### Why This Pattern Works

For guessed value `x`, count how many table values are `<= x`.

In row `i`:

```text
i, 2i, 3i, ...
count = min(m, x / i)
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countLE(long long n, long long m, long long x) {
    long long count = 0;

    for (long long i = 1; i <= n; i++) {
        count += min(m, x / i);
    }

    return count;
}

long long kthInMultiplicationTable(long long n, long long m, long long k) {
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

int main() {
    cout << kthInMultiplicationTable(3, 5, 7) << "\n";
}
```

### Step-by-Step Dry Run

```text
3 x 5 table:

1  2  3  4  5
2  4  6  8 10
3  6  9 12 15

sorted:
1,2,2,3,3,4,4,5,6,6,8,9,10,12,15

k = 7
answer = 4
```

```text
Check x = 5

row 1:
min(5, 5/1) = 5

row 2:
min(5, 5/2) = 2

row 3:
min(5, 5/3) = 1

count = 8

8 >= 7
x works
```

```text
Check x = 3

row 1: 3
row 2: 1
row 3: 1

count = 5

5 < 7
x too small
```

```text
Check x = 4

row 1: 4
row 2: 2
row 3: 1

count = 7

x works
answer = 4
```

---

# Phase 5 — Binary Search on Every Start

## 13. Largest Ones After At Most K Flips

### Problem Statement

Given binary array and `k`, flip at most `k` zeros. Find maximum length subarray containing all ones after flips.

### Input

```text
arr = [1,1,1,0,1,0,0,1,1]
k = 2
```

### Output

```text
6
```

### Why This Pattern Works

Guess length `len`.

Check whether any window of length `len` has zeros `<= k`.

If length `len` works, all smaller lengths also work.

### C++ Code

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
        int r = l + len;
        int zeros = pref[r] - pref[l];

        if (zeros <= k) return true;
    }

    return false;
}

int maxOnesBS(vector<int>& arr, int k) {
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

int main() {
    vector<int> arr = {1,1,1,0,1,0,0,1,1};
    int k = 2;

    cout << maxOnesBS(arr, k) << "\n";
}
```

### Step-by-Step Dry Run

```text
arr = [1,1,1,0,1,0,0,1,1]
k = 2
```

```text
Try len = 6

window 0..5:
[1,1,1,0,1,0]
zeros = 2

valid because zeros <= k

len = 6 works
```

```text
Try len = 7

window 0..6:
[1,1,1,0,1,0,0]
zeros = 3

window 1..7:
[1,1,0,1,0,0,1]
zeros = 3

window 2..8:
[1,0,1,0,0,1,1]
zeros = 3

No valid window.

len = 7 fails
```

```text
answer = 6
```

---

## 14. Count Subarrays with At Most K Zeros

### Problem Statement

Count subarrays with at most `k` zeros.

### Input

```text
arr = [1,0,0]
k = 1
```

### Output

```text
4
```

### Why This Pattern Works

For each start index, valid end indices are monotone:

```text
Y Y Y N N
```

Find farthest valid end using binary search.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countAtMostKZeros(vector<int>& arr, int k) {
    int n = arr.size();

    vector<int> pref(n + 1, 0);

    for (int i = 0; i < n; i++) {
        pref[i + 1] = pref[i] + (arr[i] == 0);
    }

    long long count = 0;

    for (int st = 0; st < n; st++) {
        int lo = st;
        int hi = n - 1;
        int ans = st - 1;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;

            int zeros = pref[mid + 1] - pref[st];

            if (zeros <= k) {
                ans = mid;
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }

        count += ans - st + 1;
    }

    return count;
}

int main() {
    vector<int> arr = {1,0,0};
    int k = 1;

    cout << countAtMostKZeros(arr, k) << "\n";
}
```

### Step-by-Step Dry Run

```text
arr = [1, 0, 0]
k = 1
```

```text
start = 0

subarrays:
[1]       zeros = 0 valid
[1,0]     zeros = 1 valid
[1,0,0]   zeros = 2 invalid

valid count = 2
```

```text
start = 1

[0]     zeros = 1 valid
[0,0]   zeros = 2 invalid

valid count = 1
```

```text
start = 2

[0] zeros = 1 valid

valid count = 1
```

```text
total = 2 + 1 + 1 = 4
```

---

## 15. Count Subarrays with At Most K Distinct Elements

### Problem Statement

Count subarrays with at most `k` distinct elements.

### Input

```text
arr = [1, 2, 1, 2, 3]
k = 2
```

### Output

```text
12
```

### Why This Pattern Works

This is same family as at most `k` zeros.

For counting, sliding window is better than binary search, but the monotone idea is:

```text
for fixed start, valid end positions form a prefix
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countAtMostKDistinct(vector<int>& arr, int k) {
    unordered_map<int,int> freq;
    int l = 0;
    long long ans = 0;

    for (int r = 0; r < arr.size(); r++) {
        freq[arr[r]]++;

        while ((int)freq.size() > k) {
            freq[arr[l]]--;

            if (freq[arr[l]] == 0) {
                freq.erase(arr[l]);
            }

            l++;
        }

        ans += r - l + 1;
    }

    return ans;
}

int main() {
    vector<int> arr = {1,2,1,2,3};
    int k = 2;

    cout << countAtMostKDistinct(arr, k) << "\n";
}
```

### Step-by-Step Dry Run

```text
arr = [1,2,1,2,3]
k = 2
```

```text
r = 0

window = [1]
distinct = {1}

subarrays ending here:
[1]

add 1
ans = 1
```

```text
r = 1

window = [1,2]
distinct = {1,2}

subarrays ending here:
[2]
[1,2]

add 2
ans = 3
```

```text
r = 2

window = [1,2,1]
distinct = {1,2}

subarrays ending here:
[1]
[2,1]
[1,2,1]

add 3
ans = 6
```

```text
r = 3

window = [1,2,1,2]
distinct = {1,2}

add 4
ans = 10
```

```text
r = 4

add 3

window before shrink:
[1,2,1,2,3]
distinct = {1,2,3}

shrink:
remove 1 -> still {1,2,3}
remove 2 -> still {1,2,3}
remove 1 -> now {2,3}

window = [2,3]

subarrays ending here:
[3]
[2,3]

add 2
ans = 12
```

---

# Phase 6 — Real Domain Binary Search

## 16. Square Root / Real Binary Search

### Problem Statement

Find square root of `2` with decimal precision.

### Input

```text
x = 2
```

### Output

```text
1.414213562...
```

### Why This Pattern Works

We search in real numbers.

For real domain:

```cpp
lo = mid;
hi = mid;
```

not:

```cpp
lo = mid + 1;
hi = mid - 1;
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool check(long double mid) {
    return mid * mid >= 2.0;
}

int main() {
    long double lo = 0;
    long double hi = 2;

    for (int it = 0; it < 100; it++) {
        long double mid = (lo + hi) / 2;

        if (check(mid)) {
            hi = mid;
        } else {
            lo = mid;
        }
    }

    cout << fixed << setprecision(15) << (lo + hi) / 2 << "\n";
}
```

### Step-by-Step Dry Run

```text
lo = 0
hi = 2
```

```text
Step 1

mid = 1

1 * 1 >= 2 ? no
lo = mid = 1
```

```text
Step 2

lo = 1, hi = 2
mid = 1.5

1.5 * 1.5 = 2.25 >= 2

hi = mid = 1.5
```

```text
Step 3

lo = 1, hi = 1.5
mid = 1.25

1.25^2 = 1.5625 < 2

lo = 1.25
```

```text
After many iterations:
answer ≈ 1.414213562
```

---

# Phase 7 — Ternary Search

## 17. Ternary Search Foundation

### Problem Statement

Find minimum or maximum of a unimodal function.

A unimodal function is:

```text
decreasing then increasing
```

or:

```text
increasing then decreasing
```

### Why This Pattern Works

Ternary search splits space using two midpoints:

```text
lo ----- m1 ----- m2 ----- hi
```

For minimum:

```text
if f(m1) < f(m2)
    minimum is not strictly right of m2
else
    minimum is not strictly left of m1
```

### C++ Code

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

### Integer Version

```cpp
long long integerTernary(long long lo, long long hi) {
    while (hi - lo > 3) {
        long long m1 = lo + (hi - lo) / 3;
        long long m2 = hi - (hi - lo) / 3;

        if (f(m1) < f(m2)) {
            hi = m2;
        } else {
            lo = m1;
        }
    }

    long long ans = f(lo);

    for (long long x = lo; x <= hi; x++) {
        ans = min(ans, f(x));
    }

    return ans;
}
```

### Step-by-Step Dry Run

```text
Function is valley shaped.

lo ----------------------------- hi
        m1              m2
```

```text
If f(m1) < f(m2):

left side is better
discard right part

hi = m2
```

```text
If f(m1) > f(m2):

right side is better
discard left part

lo = m1
```

---

## 18. Freefall Problem

### Problem Statement

You can increase gravity using operations. Each operation costs `B` time.

If you do `x` operations:

```text
total time = B*x + A / sqrt(x + 1)
```

Minimize total time.

### Input

```text
A = 10
B = 1
```

### Output

```text
7.773502691...
```

### Why This Pattern Works

The function has two parts:

```text
B*x increases
A/sqrt(x+1) decreases
```

So total first decreases, then increases: valley shape.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ld = long double;
using ll = long long;

ll A, B;

ld f(ll x) {
    return (ld)B * x + (ld)A / sqrt((ld)x + 1);
}

int main() {
    cin >> A >> B;

    ll lo = 0;
    ll hi = A / B + 5;

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

### Step-by-Step Dry Run

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
     ≈ 8.071
```

```text
x = 2:
f(2) = 2 + 10/sqrt(3)
     ≈ 7.773
```

```text
x = 3:
f(3) = 3 + 10/sqrt(4)
     = 8
```

```text
Minimum near x = 2
answer ≈ 7.773
```

---

# Phase 8 — Drill Problems

## 19. Sum of Cubes

### Problem Statement

Given `x`, check if:

```text
x = a^3 + b^3
```

for positive integers `a` and `b`.

### Input

```text
x = 35
```

### Output

```text
YES
```

Because:

```text
2^3 + 3^3 = 8 + 27 = 35
```

### Why This Pattern Works

Since:

```text
x <= 1e12
cube root <= 1e4
```

Iterate `a`, then check whether:

```text
x - a^3
```

is a perfect cube.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll cubeRootFloor(ll x) {
    ll lo = 1;
    ll hi = 1000000;
    ll ans = 0;

    while (lo <= hi) {
        ll mid = lo + (hi - lo) / 2;

        if (mid <= x / mid / mid) {
            ans = mid;
            lo = mid + 1;
        } else {
            hi = mid - 1;
        }
    }

    return ans;
}

bool possible(ll x) {
    for (ll a = 1; a * a * a < x; a++) {
        ll rem = x - a * a * a;
        ll b = cubeRootFloor(rem);

        if (b > 0 && b * b * b == rem) {
            return true;
        }
    }

    return false;
}

int main() {
    int t;
    cin >> t;

    while (t--) {
        ll x;
        cin >> x;

        cout << (possible(x) ? "YES" : "NO") << "\n";
    }
}
```

### Step-by-Step Dry Run

```text
x = 35
```

```text
Try a = 1

a^3 = 1
remaining = 35 - 1 = 34

cubeRootFloor(34) = 3
3^3 = 27

27 != 34
not valid
```

```text
Try a = 2

a^3 = 8
remaining = 35 - 8 = 27

cubeRootFloor(27) = 3
3^3 = 27

valid

2^3 + 3^3 = 35

answer = YES
```

### Overflow Note

Avoid:

```cpp
mid * mid * mid <= x
```

Use:

```cpp
mid <= x / mid / mid
```

---

# Binary Search Decision Table

| Problem Signal | Use |
|---|---|
| sorted array | classic binary search |
| first valid index | first true |
| last valid index | last true |
| first `>= x` | lower bound |
| first `> x` | upper bound |
| minimize maximum | first true on answer |
| maximize minimum | last true on answer |
| minimum time | first true on answer |
| kth smallest | count `<= mid` |
| generated matrix / pairs | count instead of generate |
| fixed start, farthest end | binary search on every start |
| decimal answer | real binary search |
| valley / hill function | ternary search |
| multiplication overflow | divide-and-check |

---

# Must-Solve Phase Order

```text
Phase 1:
1. First 1 in Binary Array
2. Lower Bound
3. Upper Bound

Phase 2:
4. Search Insert Position
5. Rotation Count
6. Peak Element

Phase 3:
7. Painter Partition / Split Array Largest Sum
8. Factory Machines
9. Aggressive Cows
10. Minimize Max Gap

Phase 4:
11. Kth Pair Sum
12. Kth Smallest in Multiplication Table

Phase 5:
13. Largest Ones After K Flips
14. Count At Most K Zeros
15. Count At Most K Distinct

Phase 6:
16. Real Domain Binary Search

Phase 7:
17. Ternary Search
18. Freefall

Phase 8:
19. Sum of Cubes
```

---

# Template Library

## First True

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

## Last True

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

## Real Binary Search

```cpp
long double realBS(long double lo, long double hi) {
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

---

# Common Mistakes

## 1. Repeating Same Search Space

Wrong for integer domain:

```cpp
lo = mid;
hi = mid;
```

Correct:

```cpp
lo = mid + 1;
hi = mid - 1;
```

---

## 2. Wrong Mid Formula

Risky:

```cpp
mid = (lo + hi) / 2;
```

Safe:

```cpp
mid = lo + (hi - lo) / 2;
```

---

## 3. Bad Check Function

Bad:

```text
Is mid exactly the answer?
```

Good:

```text
Can answer be <= mid?
Can distance mid be achieved?
Are there at least k values <= mid?
```

---

## 4. Overflow

Wrong:

```cpp
mid * mid * mid <= x
```

Safe:

```cpp
mid <= x / mid / mid
```

---

## 5. Wrong Bounds

Examples:

```text
Painter Partition:
lo = max(arr)
hi = sum(arr)

Factory Machines:
lo = 0
hi = fastest * target

Aggressive Cows:
lo = 0
hi = max_position - min_position

Kth Pair Sum:
lo = smallest possible sum
hi = largest possible sum
```

---

# Final Mental Model

```text
Binary search is not about arrays.
Binary search is about monotonic decisions.

1. What is the answer?
2. Can I guess it?
3. Can I check it?
4. Is the check monotonic?
5. First true or last true?
```

---

END
