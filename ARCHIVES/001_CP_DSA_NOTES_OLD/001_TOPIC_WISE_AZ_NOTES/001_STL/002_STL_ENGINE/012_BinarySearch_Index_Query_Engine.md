# 012_BinarySearch_Index_Query_Engine.md

> MiniSTLEngine Phase 012  
> Topic: Binary Search as an **Index Query / Decision Boundary Engine** for CP, DSA, FAANG interviews, and real-system thinking.

---

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Binary Search Is An Engine](#2-why-binary-search-is-an-engine)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Binary Search Core Behavior](#4-binary-search-core-behavior)
- [5. STL Binary Search Cheat Sheet](#5-stl-binary-search-cheat-sheet)
- [6. CP/DSA Recognition](#6-cpdsa-recognition)
- [7. Engine Architecture](#7-engine-architecture)
- [8. Basic Binary Search Engine](#8-basic-binary-search-engine)
- [9. Dry Run: Search Exact Value](#9-dry-run-search-exact-value)
- [10. CP Pattern 1: Exact Search](#10-cp-pattern-1-exact-search)
- [11. CP Pattern 2: Lower Bound](#11-cp-pattern-2-lower-bound)
- [12. CP Pattern 3: Upper Bound](#12-cp-pattern-3-upper-bound)
- [13. CP Pattern 4: First True / Decision Boundary](#13-cp-pattern-4-first-true--decision-boundary)
- [14. CP Pattern 5: Binary Search On Answer](#14-cp-pattern-5-binary-search-on-answer)
- [15. CP Pattern 6: Search In Rotated Sorted Array](#15-cp-pattern-6-search-in-rotated-sorted-array)
- [16. CP Pattern 7: Peak Element](#16-cp-pattern-7-peak-element)
- [17. Common Mistakes](#17-common-mistakes)
- [18. Complexity Table](#18-complexity-table)
- [19. Real-World Mapping](#19-real-world-mapping)
- [20. Final Mental Model](#20-final-mental-model)
- [21. Next Step](#21-next-step)

---

# 1. Goal

Learn binary search not only as:

```text
search in sorted array
```

but as an:

```text
Index Query / Decision Boundary Engine
```

It helps solve:

```text
exact search
lower_bound
upper_bound
first true
last false
minimum feasible answer
maximum feasible answer
search in rotated sorted array
capacity planning
rate limit threshold
```

---

# 2. Why Binary Search Is An Engine

Binary search reduces search space by half each step.

Normal thinking:

```text
find x in sorted array
```

Engine thinking:

```text
BinarySearchIndexEngine
    uses monotonic order
    cuts search space in half
    finds boundary / answer efficiently
```

Core question:

```text
Can I define left side as false and right side as true?
```

If yes:

```text
binary search may apply
```

---

# 3. Real-System Mental Model

Real systems use binary-search-like thinking for:

```text
database index lookup
log offset lookup
time-series query
capacity planning
threshold tuning
load testing breakpoint
first bad deployment
feature rollout debugging
```

Architecture:

```text
Search Space
     |
     v
BinarySearchIndexEngine
     |
     +--> check middle
     +--> discard impossible half
     +--> converge to answer/boundary
     |
     v
Target / Boundary / Feasible Answer
```

---

# 4. Binary Search Core Behavior

Sorted array:

```text
index:  0  1  2  3  4  5
value:  2  4  7  9  12 15
```

Target:

```text
9
```

Process:

```text
mid = 2 -> value 7 < 9 -> go right
mid = 4 -> value 12 > 9 -> go left
mid = 3 -> value 9 found
```

Complexity:

```text
O(log N)
```

---

# 5. STL Binary Search Cheat Sheet

```cpp
vector<int> v = {1, 2, 4, 4, 7};

binary_search(v.begin(), v.end(), 4); // true

lower_bound(v.begin(), v.end(), 4);   // first >= 4

upper_bound(v.begin(), v.end(), 4);   // first > 4
```

Index:

```cpp
int idx = lower_bound(v.begin(), v.end(), x) - v.begin();
```

---

# 6. CP/DSA Recognition

Use binary search when problem says:

```text
sorted array
minimum possible value
maximum possible value
can/cannot condition
first bad version
capacity
threshold
allocate pages
ship packages
koko eating bananas
answer is monotonic
```

Hidden mapping:

| Problem clue | Binary search form |
|---|---|
| sorted values | exact/lower/upper bound |
| first valid index | first true |
| minimum feasible answer | binary search on answer |
| maximum feasible answer | binary search on answer |
| capacity/time/rate | feasibility check |
| rotated sorted | modified binary search |
| peak | direction-based binary search |

---

# 7. Engine Architecture

```text
MiniBinarySearchIndexQueryEngine
├── exact search
├── lower bound query
├── upper bound query
├── first true engine
├── last false engine
├── answer space search
├── rotated index search
└── peak finder
```

---

# 8. Basic Binary Search Engine

## Step-by-Step Approach Before Code

```text
Step 1: Set left = 0 and right = n - 1.

Step 2: While left <= right:
        compute mid safely.

Step 3: If arr[mid] == target:
        return mid.

Step 4: If arr[mid] < target:
        target must be on right side,
        so left = mid + 1.

Step 5: If arr[mid] > target:
        target must be on left side,
        so right = mid - 1.

Step 6: If loop ends:
        target does not exist.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class BinarySearchEngine {
public:

    int search(vector<int>& nums, int target) {

        int left = 0;
        int right = (int)nums.size() - 1;

        while (left <= right) {

            // WHY:
            // Avoid overflow compared to (left + right) / 2.
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {

                return mid;
            }

            if (nums[mid] < target) {

                // Target can only be on right side.
                left = mid + 1;

            } else {

                // Target can only be on left side.
                right = mid - 1;
            }
        }

        return -1;
    }
};

int main() {

    vector<int> nums = {2, 4, 7, 9, 12, 15};

    BinarySearchEngine engine;

    cout << engine.search(nums, 9) << endl;  // 3

    cout << engine.search(nums, 8) << endl;  // -1

    return 0;
}
```

---

# 9. Dry Run: Search Exact Value

Input:

```text
nums = [2, 4, 7, 9, 12, 15]
target = 9
```

Initial:

```text
left = 0
right = 5
```

Step 1:

```text
mid = 2
nums[2] = 7

7 < 9
go right

left = 3
right = 5
```

Step 2:

```text
mid = 4
nums[4] = 12

12 > 9
go left

left = 3
right = 3
```

Step 3:

```text
mid = 3
nums[3] = 9

found
answer = 3
```

---

# 10. CP Pattern 1: Exact Search

## Problem Type

```text
Find whether target exists in sorted array.
```

## Step-by-Step Approach Before Code

```text
Step 1: Use binary search on sorted array.

Step 2: If mid value equals target, return true.

Step 3: If mid value is smaller, discard left half.

Step 4: If mid value is larger, discard right half.

Step 5: If search space ends, return false.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool exists(vector<int>& nums, int target) {

    int left = 0;
    int right = (int)nums.size() - 1;

    while (left <= right) {

        int mid = left + (right - left) / 2;

        if (nums[mid] == target) {
            return true;
        }

        if (nums[mid] < target) {
            left = mid + 1;
        } else {
            right = mid - 1;
        }
    }

    return false;
}

int main() {

    vector<int> nums = {1, 3, 5, 7, 9};

    cout << exists(nums, 5) << endl;
    cout << exists(nums, 6) << endl;

    return 0;
}
```

---

# 11. CP Pattern 2: Lower Bound

## Problem Type

```text
Find first index where nums[index] >= target.
```

This is useful for:

```text
ceiling query
insert position
first valid candidate
```

## Step-by-Step Approach Before Code

```text
Step 1: Use search range [0, n].

Step 2: Maintain answer boundary:
        left is possible insertion position.

Step 3: If nums[mid] >= target:
        mid may be answer,
        move right = mid.

Step 4: Else nums[mid] < target:
        answer must be right of mid,
        move left = mid + 1.

Step 5: At end, left is first index with nums[index] >= target.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int lowerBoundManual(vector<int>& nums, int target) {

    int left = 0;
    int right = nums.size();

    while (left < right) {

        int mid = left + (right - left) / 2;

        if (nums[mid] >= target) {

            // mid can be answer,
            // but maybe there is earlier valid index.
            right = mid;

        } else {

            left = mid + 1;
        }
    }

    return left;
}

int main() {

    vector<int> nums = {1, 2, 4, 4, 7};

    cout << lowerBoundManual(nums, 4) << endl; // 2
    cout << lowerBoundManual(nums, 5) << endl; // 4

    return 0;
}
```

---

# 12. CP Pattern 3: Upper Bound

## Problem Type

```text
Find first index where nums[index] > target.
```

Useful for:

```text
count <= x
range frequency
last occurrence
```

## Step-by-Step Approach Before Code

```text
Step 1: Use search range [0, n].

Step 2: If nums[mid] > target:
        mid may be answer,
        move right = mid.

Step 3: Else nums[mid] <= target:
        answer must be after mid,
        move left = mid + 1.

Step 4: At end, left is first index with nums[index] > target.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int upperBoundManual(vector<int>& nums, int target) {

    int left = 0;
    int right = nums.size();

    while (left < right) {

        int mid = left + (right - left) / 2;

        if (nums[mid] > target) {

            right = mid;

        } else {

            left = mid + 1;
        }
    }

    return left;
}

int main() {

    vector<int> nums = {1, 2, 4, 4, 7};

    cout << upperBoundManual(nums, 4) << endl; // 4
    cout << upperBoundManual(nums, 5) << endl; // 4

    return 0;
}
```

---

# 13. CP Pattern 4: First True / Decision Boundary

## Problem Type

```text
Find first position where condition becomes true.
```

Example:

```text
false false false true true true
```

Answer:

```text
first true index
```

## Step-by-Step Approach Before Code

```text
Step 1: Define a monotonic boolean function possible(x).

Step 2: The pattern must look like:
        false false false true true true

Step 3: Binary search on index/answer.

Step 4: If possible(mid) is true:
        mid may be answer,
        move right = mid.

Step 5: If possible(mid) is false:
        answer must be right,
        move left = mid + 1.

Step 6: left is first true.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool possible(int x) {

    // Example condition:
    // first true starts from x >= 7.
    return x >= 7;
}

int firstTrue(int low, int high) {

    while (low < high) {

        int mid = low + (high - low) / 2;

        if (possible(mid)) {

            // mid works,
            // but maybe smaller answer also works.
            high = mid;

        } else {

            // mid does not work,
            // answer must be larger.
            low = mid + 1;
        }
    }

    return low;
}

int main() {

    cout << firstTrue(0, 20) << endl; // 7

    return 0;
}
```

---

# 14. CP Pattern 5: Binary Search On Answer

## Problem Type

```text
Find minimum capacity/speed/time such that task is possible.
```

Examples:

```text
Koko Eating Bananas
Ship Packages Within D Days
Allocate Books
Minimum Time To Complete Trips
```

## Example

Given package weights and days, find minimum ship capacity.

## Step-by-Step Approach Before Code

```text
Step 1: Identify answer range:
        low = minimum possible answer
        high = maximum possible answer.

Step 2: Write possible(capacity):
        returns true if this capacity can finish task.

Step 3: Observe monotonicity:
        if capacity X works,
        any capacity > X also works.

Step 4: Binary search first true.

Step 5: Return minimum feasible capacity.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canShip(
    vector<int>& weights,
    int days,
    int capacity
) {
    int usedDays = 1;
    int currentLoad = 0;

    for (int w : weights) {

        if (w > capacity) {

            return false;
        }

        if (currentLoad + w <= capacity) {

            currentLoad += w;

        } else {

            usedDays++;
            currentLoad = w;
        }
    }

    return usedDays <= days;
}

int shipWithinDays(
    vector<int>& weights,
    int days
) {
    int low = 0;
    int high = 0;

    for (int w : weights) {
        low = max(low, w);
        high += w;
    }

    while (low < high) {

        int mid = low + (high - low) / 2;

        if (canShip(weights, days, mid)) {

            // Capacity works.
            // Try smaller capacity.
            high = mid;

        } else {

            // Capacity too small.
            low = mid + 1;
        }
    }

    return low;
}

int main() {

    vector<int> weights = {
        1,2,3,4,5,6,7,8,9,10
    };

    int days = 5;

    cout << shipWithinDays(weights, days)
         << endl;

    return 0;
}
```

---

# 15. CP Pattern 6: Search In Rotated Sorted Array

## Problem Type

```text
Sorted array rotated at some pivot.
Find target.
```

Example:

```text
[4,5,6,7,0,1,2]
target = 0
```

## Step-by-Step Approach Before Code

```text
Step 1: Use normal binary search framework.

Step 2: At every mid, one half is sorted:
        either left half [left..mid]
        or right half [mid..right].

Step 3: Check which half is sorted.

Step 4: If target lies in sorted half:
        move into that half.

Step 5: Otherwise:
        move into the other half.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int searchRotated(
    vector<int>& nums,
    int target
) {
    int left = 0;
    int right = (int)nums.size() - 1;

    while (left <= right) {

        int mid = left + (right - left) / 2;

        if (nums[mid] == target) {
            return mid;
        }

        // Left half is sorted.
        if (nums[left] <= nums[mid]) {

            if (nums[left] <= target &&
                target < nums[mid]) {

                right = mid - 1;

            } else {

                left = mid + 1;
            }

        } else {

            // Right half is sorted.
            if (nums[mid] < target &&
                target <= nums[right]) {

                left = mid + 1;

            } else {

                right = mid - 1;
            }
        }
    }

    return -1;
}

int main() {

    vector<int> nums = {4,5,6,7,0,1,2};

    cout << searchRotated(nums, 0) << endl; // 4

    return 0;
}
```

---

# 16. CP Pattern 7: Peak Element

## Problem Type

```text
Find any peak element.
```

Peak:

```text
nums[i] > nums[i+1] and nums[i] > nums[i-1]
```

## Step-by-Step Approach Before Code

```text
Step 1: Compare nums[mid] and nums[mid+1].

Step 2: If nums[mid] < nums[mid+1]:
        slope is going up,
        peak must exist on right.

Step 3: Else:
        slope is going down,
        peak exists on left including mid.

Step 4: Continue until left == right.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int findPeakElement(vector<int>& nums) {

    int left = 0;
    int right = (int)nums.size() - 1;

    while (left < right) {

        int mid = left + (right - left) / 2;

        if (nums[mid] < nums[mid + 1]) {

            // We are going uphill,
            // so a peak must exist on the right.
            left = mid + 1;

        } else {

            // We are going downhill,
            // so mid or left side has a peak.
            right = mid;
        }
    }

    return left;
}

int main() {

    vector<int> nums = {1, 2, 1, 3, 5, 6, 4};

    cout << findPeakElement(nums) << endl;

    return 0;
}
```

---

# 17. Common Mistakes

## Mistake 1: Infinite Loop

Wrong updates:

```cpp
left = mid;
right = mid;
```

can cause infinite loop.

Use carefully:

```cpp
left = mid + 1
right = mid
```

or:

```cpp
left = mid + 1
right = mid - 1
```

depending on template.

---

## Mistake 2: Overflow In Mid

Wrong:

```cpp
int mid = (left + right) / 2;
```

Better:

```cpp
int mid = left + (right - left) / 2;
```

---

## Mistake 3: Using Binary Search Without Monotonicity

Binary search on answer requires:

```text
false false false true true true
```

or:

```text
true true true false false false
```

If condition is random:

```text
binary search does not apply
```

---

## Mistake 4: Confusing Lower And Upper Bound

```text
lower_bound(x) = first >= x
upper_bound(x) = first > x
```

---

# 18. Complexity Table

| Pattern | Complexity |
|---|---:|
| exact search | O(log N) |
| lower_bound | O(log N) |
| upper_bound | O(log N) |
| first true | O(log N) |
| binary search on answer | O(log Range * checkCost) |
| rotated search | O(log N) |
| peak element | O(log N) |

---

# 19. Real-World Mapping

| Binary Search Concept | Real-System Meaning |
|---|---|
| sorted array search | database index lookup |
| lower_bound | first matching record |
| upper_bound | range end lookup |
| first true | first stable deployment version |
| answer search | capacity planning |
| threshold search | rate limit tuning |
| log offset lookup | Kafka-like offset search |
| time-series lookup | metrics range query |

---

# 20. Final Mental Model

Binary search is:

```text
decision boundary search engine
```

Best for:

```text
sorted data
monotonic conditions
minimum feasible answer
maximum feasible answer
threshold finding
index queries
```

One-line CP rule:

```text
If answer space is monotonic, binary search the boundary.
```

One-line system rule:

```text
Binary search is how systems quickly locate boundaries in indexes, logs, metrics, and capacity ranges.
```

---

# 21. Next Step

Next file:

```text
013_Sort_Ranking_Engine.md
```

Then:

```text
014_TwoPointer_Stream_Merge_Engine.md
015_SlidingWindow_Metrics_Engine.md
016_MonotonicStack_Boundary_Engine.md
```
