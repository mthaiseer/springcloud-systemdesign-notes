# 014_TwoPointer_Stream_Merge_Engine.md

> MiniSTLEngine Phase 014  
> Topic: Two Pointers as a **Stream Merge / Pair Matching / Boundary Movement Engine** for CP, DSA, FAANG interviews, and real-system thinking.

---

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Two Pointers Is An Engine](#2-why-two-pointers-is-an-engine)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Two Pointer Core Behavior](#4-two-pointer-core-behavior)
- [5. CP/DSA Recognition](#5-cpdsa-recognition)
- [6. Engine Architecture](#6-engine-architecture)
- [7. Basic Two Pointer Engine](#7-basic-two-pointer-engine)
- [8. Dry Run: Opposite Direction Pointers](#8-dry-run-opposite-direction-pointers)
- [9. CP Pattern 1: Two Sum In Sorted Array](#9-cp-pattern-1-two-sum-in-sorted-array)
- [10. CP Pattern 2: Remove Duplicates From Sorted Array](#10-cp-pattern-2-remove-duplicates-from-sorted-array)
- [11. CP Pattern 3: Merge Two Sorted Arrays](#11-cp-pattern-3-merge-two-sorted-arrays)
- [12. CP Pattern 4: Container With Most Water](#12-cp-pattern-4-container-with-most-water)
- [13. CP Pattern 5: Three Sum](#13-cp-pattern-5-three-sum)
- [14. CP Pattern 6: Partition / Stable Write Pointer](#14-cp-pattern-6-partition--stable-write-pointer)
- [15. Two Pointers vs Sliding Window](#15-two-pointers-vs-sliding-window)
- [16. Common Mistakes](#16-common-mistakes)
- [17. Complexity Table](#17-complexity-table)
- [18. Real-World Mapping](#18-real-world-mapping)
- [19. Final Mental Model](#19-final-mental-model)
- [20. Next Step](#20-next-step)

---

# 1. Goal

Learn two pointers not only as:

```text
left and right variables
```

but as a:

```text
Stream Merge / Pair Matching / Boundary Movement Engine
```

It helps solve:

```text
two sum sorted
merge sorted arrays
remove duplicates
partition arrays
palindrome checks
three sum
container with most water
sorted stream processing
```

---

# 2. Why Two Pointers Is An Engine

Two pointers avoid nested loops.

Normal thinking:

```cpp
for i:
    for j:
```

Engine thinking:

```text
TwoPointerEngine
    maintains two controlled positions
    moves only useful pointer
    eliminates impossible states
    reduces O(N^2) to O(N) or O(N log N)
```

Core question:

```text
Can moving one pointer logically eliminate many possibilities?
```

If yes:

```text
two pointers may apply
```

---

# 3. Real-System Mental Model

Real systems use pointer-like scanning for:

```text
merge two sorted log streams
deduplicate sorted records
compare two versions
sync two timelines
stream compaction
matching orders
diff engines
```

Architecture:

```text
Sorted/Structured Data
        |
        v
TwoPointerStreamMergeEngine
        |
        +--> compare left/right
        +--> move one pointer
        +--> emit result
        |
        v
Merged / Matched / Filtered Output
```

---

# 4. Two Pointer Core Behavior

Common forms:

```text
1. Opposite direction:
   left starts at 0
   right starts at n-1

2. Same direction:
   slow pointer
   fast pointer

3. Merge direction:
   pointer i over array A
   pointer j over array B

4. Write pointer:
   read pointer scans
   write pointer writes compacted result
```

---

# 5. CP/DSA Recognition

Use two pointers when problem says:

```text
sorted array
pair sum
palindrome
merge sorted
remove duplicates
partition
move zeros
three sum
closest pair
```

Hidden mapping:

| Problem clue | Two pointer form |
|---|---|
| sorted pair target | opposite pointers |
| merge two sorted | merge pointers |
| remove duplicates | slow/fast pointers |
| palindrome | opposite pointers |
| partition | read/write pointer |
| three sum | sort + two pointers |
| compare two streams | merge pointers |

---

# 6. Engine Architecture

```text
MiniTwoPointerStreamMergeEngine
├── opposite pointer matcher
├── same direction slow/fast pointer
├── sorted stream merger
├── deduplication engine
├── partition/write pointer engine
├── pair sum engine
└── three-sum engine
```

---

# 7. Basic Two Pointer Engine

## Step-by-Step Approach Before Code

```text
Step 1: Decide pointer meaning:
        left/right, slow/fast, or i/j merge pointers.

Step 2: Decide movement rule:
        when to move left?
        when to move right?

Step 3: Prove why moving that pointer is safe.

Step 4: Loop until pointers cross or one stream ends.

Step 5: Build answer while moving pointers.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class TwoPointerEngine {
public:

    bool hasPairWithSum(vector<int>& nums, int target) {

        int left = 0;
        int right = (int)nums.size() - 1;

        while (left < right) {

            int sum = nums[left] + nums[right];

            if (sum == target) {
                return true;
            }

            if (sum < target) {

                // Need bigger sum.
                // Since array is sorted, move left rightward.
                left++;

            } else {

                // Need smaller sum.
                // Move right leftward.
                right--;
            }
        }

        return false;
    }
};

int main() {

    vector<int> nums = {1, 2, 4, 7, 11};

    TwoPointerEngine engine;

    cout << engine.hasPairWithSum(nums, 9) << endl;

    return 0;
}
```

---

# 8. Dry Run: Opposite Direction Pointers

Input:

```text
nums = [1, 2, 4, 7, 11]
target = 9
```

Initial:

```text
left = 0 -> 1
right = 4 -> 11
sum = 12
```

Since:

```text
12 > 9
```

Move right:

```text
right = 3 -> 7
sum = 1 + 7 = 8
```

Since:

```text
8 < 9
```

Move left:

```text
left = 1 -> 2
sum = 2 + 7 = 9
```

Answer:

```text
true
```

---

# 9. CP Pattern 1: Two Sum In Sorted Array

## Problem Type

```text
Given sorted array, find whether any pair sums to target.
```

## Step-by-Step Approach Before Code

```text
Step 1: Put left pointer at start.

Step 2: Put right pointer at end.

Step 3: Compute sum = nums[left] + nums[right].

Step 4: If sum == target:
        found answer.

Step 5: If sum < target:
        move left++ to increase sum.

Step 6: If sum > target:
        move right-- to decrease sum.

Step 7: Stop when left >= right.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool twoSumSorted(vector<int>& nums, int target) {

    int left = 0;
    int right = (int)nums.size() - 1;

    while (left < right) {

        int sum = nums[left] + nums[right];

        if (sum == target) {
            return true;
        }

        if (sum < target) {
            left++;
        } else {
            right--;
        }
    }

    return false;
}

int main() {

    vector<int> nums = {1, 2, 4, 7, 11};

    cout << twoSumSorted(nums, 9) << endl;

    return 0;
}
```

---

# 10. CP Pattern 2: Remove Duplicates From Sorted Array

## Problem Type

```text
Remove duplicates in-place from sorted array.
```

## Step-by-Step Approach Before Code

```text
Step 1: Use read pointer to scan all elements.

Step 2: Use write pointer to place unique elements.

Step 3: If current value is different from previous kept value:
        write it at write pointer.

Step 4: Increment write pointer.

Step 5: Final write pointer = new length.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int removeDuplicates(vector<int>& nums) {

    if (nums.empty()) {
        return 0;
    }

    int write = 1;

    for (int read = 1; read < (int)nums.size(); read++) {

        if (nums[read] != nums[write - 1]) {

            // Found a new unique value.
            // Write it into compacted position.
            nums[write] = nums[read];

            write++;
        }
    }

    return write;
}

int main() {

    vector<int> nums = {1, 1, 2, 2, 3};

    int len = removeDuplicates(nums);

    for (int i = 0; i < len; i++) {
        cout << nums[i] << " ";
    }

    return 0;
}
```

## Dry Run

```text
nums = [1,1,2,2,3]

write = 1

read=1 -> nums[1]=1 same as nums[0], skip
read=2 -> nums[2]=2 new, write at index 1
array = [1,2,2,2,3], write=2

read=3 -> nums[3]=2 same as nums[1], skip
read=4 -> nums[4]=3 new, write at index 2
array = [1,2,3,2,3], write=3

new length = 3
```

---

# 11. CP Pattern 3: Merge Two Sorted Arrays

## Problem Type

```text
Merge two sorted arrays into one sorted output.
```

## Step-by-Step Approach Before Code

```text
Step 1: Use pointer i for array A.

Step 2: Use pointer j for array B.

Step 3: Compare A[i] and B[j].

Step 4: Add smaller value to result.

Step 5: Move pointer of chosen value.

Step 6: Append remaining values from unfinished array.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> mergeSorted(
    vector<int>& a,
    vector<int>& b
) {
    int i = 0;
    int j = 0;

    vector<int> result;

    while (i < (int)a.size() &&
           j < (int)b.size()) {

        if (a[i] <= b[j]) {

            result.push_back(a[i]);
            i++;

        } else {

            result.push_back(b[j]);
            j++;
        }
    }

    while (i < (int)a.size()) {
        result.push_back(a[i]);
        i++;
    }

    while (j < (int)b.size()) {
        result.push_back(b[j]);
        j++;
    }

    return result;
}

int main() {

    vector<int> a = {1, 4, 7};
    vector<int> b = {2, 3, 8};

    vector<int> merged =
        mergeSorted(a, b);

    for (int x : merged) {
        cout << x << " ";
    }

    return 0;
}
```

---

# 12. CP Pattern 4: Container With Most Water

## Problem Type

```text
Find max area between two vertical lines.
```

## Step-by-Step Approach Before Code

```text
Step 1: Put left at 0 and right at n-1.

Step 2: Area = min(height[left], height[right]) * width.

Step 3: Update best answer.

Step 4: Move the pointer with smaller height.

Step 5: Why?
        Area is limited by smaller height.
        Moving larger height cannot improve height limit.

Step 6: Continue until left >= right.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxArea(vector<int>& height) {

    int left = 0;
    int right = (int)height.size() - 1;

    int best = 0;

    while (left < right) {

        int width = right - left;

        int currentHeight =
            min(height[left], height[right]);

        best = max(
            best,
            width * currentHeight
        );

        if (height[left] < height[right]) {

            // Left is limiting height.
            // Move it hoping for taller boundary.
            left++;

        } else {

            right--;
        }
    }

    return best;
}

int main() {

    vector<int> height = {
        1,8,6,2,5,4,8,3,7
    };

    cout << maxArea(height) << endl;

    return 0;
}
```

---

# 13. CP Pattern 5: Three Sum

## Problem Type

```text
Find all unique triplets with sum 0.
```

## Step-by-Step Approach Before Code

```text
Step 1: Sort array.

Step 2: Fix first element nums[i].

Step 3: For remaining part:
        use two pointers left=i+1 and right=n-1.

Step 4: If sum == 0:
        store triplet and skip duplicates.

Step 5: If sum < 0:
        move left++.

Step 6: If sum > 0:
        move right--.

Step 7: Skip duplicate fixed values.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> threeSum(vector<int>& nums) {

    sort(nums.begin(), nums.end());

    vector<vector<int>> answer;

    int n = nums.size();

    for (int i = 0; i < n; i++) {

        if (i > 0 && nums[i] == nums[i - 1]) {
            continue;
        }

        int left = i + 1;
        int right = n - 1;

        while (left < right) {

            int sum =
                nums[i] + nums[left] + nums[right];

            if (sum == 0) {

                answer.push_back({
                    nums[i],
                    nums[left],
                    nums[right]
                });

                left++;
                right--;

                while (left < right &&
                       nums[left] == nums[left - 1]) {
                    left++;
                }

                while (left < right &&
                       nums[right] == nums[right + 1]) {
                    right--;
                }

            } else if (sum < 0) {

                left++;

            } else {

                right--;
            }
        }
    }

    return answer;
}

int main() {

    vector<int> nums = {
        -1,0,1,2,-1,-4
    };

    auto ans = threeSum(nums);

    for (auto triplet : ans) {
        for (int x : triplet) {
            cout << x << " ";
        }
        cout << endl;
    }

    return 0;
}
```

---

# 14. CP Pattern 6: Partition / Stable Write Pointer

## Problem Type

```text
Move elements satisfying condition to front.
```

Examples:

```text
move zeros
remove element
partition array
filter stream
```

## Step-by-Step Approach Before Code

```text
Step 1: Use read pointer to scan all values.

Step 2: Use write pointer for next valid position.

Step 3: If nums[read] should be kept:
        write nums[read] to nums[write].

Step 4: Increment write.

Step 5: Remaining positions can be ignored or filled.
```

## C++ Code: Move Zeroes

```cpp
#include <bits/stdc++.h>
using namespace std;

void moveZeroes(vector<int>& nums) {

    int write = 0;

    for (int read = 0; read < (int)nums.size(); read++) {

        if (nums[read] != 0) {

            // Keep non-zero elements in original relative order.
            nums[write] = nums[read];
            write++;
        }
    }

    while (write < (int)nums.size()) {

        nums[write] = 0;
        write++;
    }
}

int main() {

    vector<int> nums = {0, 1, 0, 3, 12};

    moveZeroes(nums);

    for (int x : nums) {
        cout << x << " ";
    }

    return 0;
}
```

---

# 15. Two Pointers vs Sliding Window

| Feature | Two Pointers | Sliding Window |
|---|---|---|
| Main idea | move two positions | maintain continuous range |
| Common array state | sorted or structured | contiguous subarray |
| Movement | left/right or read/write | expand/shrink |
| Examples | two sum, merge, palindrome | longest substring, max sum |
| Window size | not always a window | active window is key |

Shortcut:

```text
Pair/match/merge?
→ two pointers

Contiguous subarray with condition?
→ sliding window
```

---

# 16. Common Mistakes

## Mistake 1: Using Two Pointers Without Sorting

For pair sum pointer logic:

```text
array must be sorted
```

Otherwise movement rules are invalid.

---

## Mistake 2: Moving Wrong Pointer

For two sum sorted:

```text
sum too small -> move left
sum too large -> move right
```

---

## Mistake 3: Duplicate Handling In Three Sum

Need to skip duplicates after finding triplet and for fixed index.

---

## Mistake 4: Confusing Write Pointer Meaning

Always define:

```text
write = next position where valid value should be placed
```

---

# 17. Complexity Table

| Pattern | Complexity |
|---|---:|
| two sum sorted | O(N) |
| remove duplicates | O(N) |
| merge two sorted arrays | O(N + M) |
| container with most water | O(N) |
| three sum | O(N^2) after sort |
| move zeroes | O(N) |

---

# 18. Real-World Mapping

| Two Pointer Concept | Real-System Meaning |
|---|---|
| merge two sorted arrays | merge sorted log streams |
| remove duplicates | stream compaction |
| slow/fast pointer | read/write pipeline |
| opposite pointers | pair matching engine |
| three sum | matching multiple constraints |
| palindrome scan | symmetric validation |
| sorted stream merge | ETL merge process |
| partition | filtering pipeline |

---

# 19. Final Mental Model

Two pointers is:

```text
controlled boundary movement engine
```

Best for:

```text
sorted pair search
merging streams
deduplication
partitioning
palindrome checks
matching problems
```

One-line CP rule:

```text
If moving one pointer eliminates many cases safely, think two pointers.
```

One-line system rule:

```text
Two pointers power stream merging, deduplication, filtering, and ordered matching systems.
```

---

# 20. Next Step

Next file:

```text
015_SlidingWindow_Metrics_Engine.md
```

Then:

```text
016_MonotonicStack_Boundary_Engine.md
017_MonotonicDeque_Window_MinMax_Engine.md
018_RangeMapping_Interval_Engine.md
```
