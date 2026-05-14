# Heap / Priority Queue Phase-Wise Practice Guide

> Goal: Master heap and priority queue patterns for FAANG interviews, online assessments, and CP contests.
>
> Heap is not just a data structure. It is a **decision engine**:
>
> ```text
> Keep the current best candidate on top.
> Repeatedly take the best available choice.
> ```

---

# Clickable Index

## 0. Mental Map
- [0.1 Priority Queue Recognition Map](#01-priority-queue-recognition-map)
- [0.2 C++ Heap Basics](#02-c-heap-basics)
- [0.3 Min Heap vs Max Heap](#03-min-heap-vs-max-heap)

## Phase 1 — Basic Heap Operations
- [1. Kth Largest Element in an Array](#1-kth-largest-element-in-an-array)
- [2. Last Stone Weight](#2-last-stone-weight)

## Phase 2 — Top K / K Smallest / K Largest
- [3. Top K Frequent Elements](#3-top-k-frequent-elements)
- [4. K Closest Points to Origin](#4-k-closest-points-to-origin)

## Phase 3 — Streaming Heap
- [5. Kth Largest Element in a Stream](#5-kth-largest-element-in-a-stream)
- [6. Find Median from Data Stream](#6-find-median-from-data-stream)

## Phase 4 — K-Way Merge
- [7. Merge K Sorted Lists](#7-merge-k-sorted-lists)
- [8. Find K Pairs with Smallest Sums](#8-find-k-pairs-with-smallest-sums)

## Phase 5 — Scheduling / Greedy Heap
- [9. Meeting Rooms II](#9-meeting-rooms-ii)
- [10. Task Scheduler](#10-task-scheduler)

## Phase 6 — Heap + Greedy Optimization
- [11. Minimum Cost to Connect Sticks](#11-minimum-cost-to-connect-sticks)
- [12. IPO](#12-ipo)

## Phase 7 — Heap + Lazy Deletion
- [13. Sliding Window Median](#13-sliding-window-median)

## Final Revision
- [Priority Queue Pattern Decision Table](#priority-queue-pattern-decision-table)
- [Must-Solve Order](#must-solve-order)
- [One-Line Mental Model](#one-line-mental-model)

---

# 0. Mental Map

## 0.1 Priority Queue Recognition Map

```text
Need current maximum repeatedly?
→ max heap

Need current minimum repeatedly?
→ min heap

Need top K largest?
→ min heap of size K

Need top K smallest?
→ max heap of size K

Need streaming median?
→ two heaps

Need merge K sorted things?
→ min heap with one item from each list

Need schedule rooms/tasks/resources?
→ min heap by ending time / cooldown time

Need repeatedly combine smallest?
→ min heap

Need choose best affordable/profitable?
→ sort + max heap
```

---

## 0.2 C++ Heap Basics

### Max Heap

```cpp
priority_queue<int> pq;
```

Top is largest.

### Min Heap

```cpp
priority_queue<int, vector<int>, greater<int>> pq;
```

Top is smallest.

### Pair Heap

```cpp
priority_queue<pair<int,int>> pq;
```

Default compares first, then second.

### Custom Min Heap for Pair

```cpp
priority_queue<pair<int,int>, vector<pair<int,int>>, greater<pair<int,int>>> pq;
```

---

## 0.3 Min Heap vs Max Heap

```text
Max heap:
top = largest

Min heap:
top = smallest
```

Important trick:

```text
Top K largest
→ keep only K largest elements
→ remove smallest among them
→ use min heap

Top K smallest
→ keep only K smallest elements
→ remove largest among them
→ use max heap
```

---

# Phase 1 — Basic Heap Operations

## 1. Kth Largest Element in an Array

### Problem Statement

Given an integer array `nums` and an integer `k`, return the kth largest element.

### Input

```text
nums = [3, 2, 1, 5, 6, 4]
k = 2
```

### Output

```text
5
```

### Pattern

```text
Top K largest using min heap of size K.
```

### Why This Pattern Works

We only care about the largest `k` elements.

Keep a min heap of size `k`.

```text
heap top = smallest among current top k largest
```

If heap size becomes more than `k`, remove the smallest.
At the end, the heap contains exactly the largest `k` values.
The smallest among them is the kth largest.

### Result

```text
kth largest = 5
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int findKthLargest(vector<int>& nums, int k) {
    priority_queue<int, vector<int>, greater<int>> pq;

    for (int x : nums) {
        pq.push(x);

        if ((int)pq.size() > k) {
            pq.pop();
        }
    }

    return pq.top();
}

int main() {
    vector<int> nums = {3, 2, 1, 5, 6, 4};
    int k = 2;

    cout << findKthLargest(nums, k) << "\n";
}
```

### Step-by-Step Dry Run

```text
nums = [3, 2, 1, 5, 6, 4]
k = 2

min heap stores current top 2 largest elements
heap top = smallest among selected top 2
```

```text
i = 0, current = 3

index:   0   1   2   3   4   5
nums:   [3,  2,  1,  5,  6,  4]
         ^

heap before: []
action      : push 3
heap after : [3]
size <= k  : no pop
```

```text
i = 1, current = 2

index:   0   1   2   3   4   5
nums:   [3,  2,  1,  5,  6,  4]
             ^

heap before: [3]
action      : push 2
heap after : [2, 3]
size <= k  : no pop
```

```text
i = 2, current = 1

index:   0   1   2   3   4   5
nums:   [3,  2,  1,  5,  6,  4]
                 ^

heap before: [2, 3]
action      : push 1
heap after : [1, 3, 2]
size > k   : pop 1
heap after : [2, 3]
```

```text
i = 3, current = 5

index:   0   1   2   3   4   5
nums:   [3,  2,  1,  5,  6,  4]
                     ^

heap before: [2, 3]
action      : push 5
heap after : [2, 3, 5]
size > k   : pop 2
heap after : [3, 5]
```

```text
i = 4, current = 6

index:   0   1   2   3   4   5
nums:   [3,  2,  1,  5,  6,  4]
                         ^

heap before: [3, 5]
action      : push 6
heap after : [3, 5, 6]
size > k   : pop 3
heap after : [5, 6]
```

```text
i = 5, current = 4

index:   0   1   2   3   4   5
nums:   [3,  2,  1,  5,  6,  4]
                             ^

heap before: [5, 6]
action      : push 4
heap after : [4, 6, 5]
size > k   : pop 4
heap after : [5, 6]

answer = heap.top() = 5
```

### Attractive Note

```text
Min heap of size K acts like a gate.
Only elements strong enough to survive inside the heap remain.
The weakest survivor is the kth largest.
```

---

## 2. Last Stone Weight

### Problem Statement

You are given stones with weights. Each turn, smash the two heaviest stones.

If both are equal, both disappear.
If different, the smaller one disappears and the bigger one becomes `big - small`.

Return the final remaining stone weight, or `0`.

### Input

```text
stones = [2, 7, 4, 1, 8, 1]
```

### Output

```text
1
```

### Pattern

```text
Repeatedly take maximum two values.
Use max heap.
```

### Why This Pattern Works

Every step needs the two heaviest stones. A max heap gives the largest stone in `O(log n)`.

### Result

```text
final stone = 1
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int lastStoneWeight(vector<int>& stones) {
    priority_queue<int> pq;

    for (int x : stones) {
        pq.push(x);
    }

    while (pq.size() > 1) {
        int a = pq.top(); pq.pop();
        int b = pq.top(); pq.pop();

        if (a != b) {
            pq.push(a - b);
        }
    }

    return pq.empty() ? 0 : pq.top();
}

int main() {
    vector<int> stones = {2, 7, 4, 1, 8, 1};
    cout << lastStoneWeight(stones) << "\n";
}
```

### Step-by-Step Dry Run

```text
stones = [2, 7, 4, 1, 8, 1]

max heap stores stones
top = heaviest stone
```

```text
Initial heap: [8, 7, 4, 2, 1, 1]
```

```text
Round 1

heap before: [8, 7, 4, 2, 1, 1]

pop heaviest 1: 8
pop heaviest 2: 7

8 != 7
new stone = 8 - 7 = 1

push 1

heap after: [4, 2, 1, 1, 1]
```

```text
Round 2

heap before: [4, 2, 1, 1, 1]

pop heaviest 1: 4
pop heaviest 2: 2

new stone = 4 - 2 = 2

push 2

heap after: [2, 1, 1, 1]
```

```text
Round 3

heap before: [2, 1, 1, 1]

pop heaviest 1: 2
pop heaviest 2: 1

new stone = 1

push 1

heap after: [1, 1, 1]
```

```text
Round 4

heap before: [1, 1, 1]

pop heaviest 1: 1
pop heaviest 2: 1

equal, both destroyed

heap after: [1]
```

```text
Only one stone remains.
answer = 1
```

### Attractive Note

```text
Whenever a problem says:
"repeatedly choose the largest/smallest"
think heap immediately.
```

---

# Phase 2 — Top K / K Smallest / K Largest

## 3. Top K Frequent Elements

### Problem Statement

Given an integer array `nums` and an integer `k`, return the `k` most frequent elements.

### Input

```text
nums = [1, 1, 1, 2, 2, 3]
k = 2
```

### Output

```text
[1, 2]
```

### Pattern

```text
Frequency map + min heap of size K.
```

### Why This Pattern Works

We need top `k` by frequency, not value.

Steps:

```text
1. Count frequency.
2. Keep only k best frequency candidates.
3. Heap top stores weakest among current top k.
```

### Result

```text
top 2 frequent = [1, 2]
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> topKFrequent(vector<int>& nums, int k) {
    unordered_map<int, int> freq;

    for (int x : nums) {
        freq[x]++;
    }

    priority_queue<
        pair<int,int>,
        vector<pair<int,int>>,
        greater<pair<int,int>>
    > pq;

    for (auto [num, count] : freq) {
        pq.push({count, num});

        if ((int)pq.size() > k) {
            pq.pop();
        }
    }

    vector<int> ans;

    while (!pq.empty()) {
        ans.push_back(pq.top().second);
        pq.pop();
    }

    return ans;
}

int main() {
    vector<int> nums = {1, 1, 1, 2, 2, 3};
    int k = 2;

    vector<int> ans = topKFrequent(nums, k);

    for (int x : ans) cout << x << " ";
}
```

### Step-by-Step Dry Run

```text
nums = [1, 1, 1, 2, 2, 3]
k = 2
```

Frequency count:

```text
num:    1   2   3
freq:   3   2   1
```

Heap stores:

```text
[count:num]
min heap by count
```

```text
Process num = 1, count = 3

heap before: []
action      : push [3:1]
heap after : [3:1]
size <= k  : no pop
```

```text
Process num = 2, count = 2

heap before: [3:1]
action      : push [2:2]
heap after : [2:2, 3:1]
size <= k  : no pop
```

```text
Process num = 3, count = 1

heap before: [2:2, 3:1]
action      : push [1:3]
heap after : [1:3, 3:1, 2:2]
size > k   : pop [1:3]

heap after : [2:2, 3:1]
```

```text
Remaining heap:
[2:2, 3:1]

Numbers:
2 and 1

answer = [2, 1]
```

Order may vary, but the valid set is:

```text
[1, 2]
```

### Attractive Note

```text
Heap does not care about original value.
You decide the ranking key.
Here the ranking key is frequency.
```

---

## 4. K Closest Points to Origin

### Problem Statement

Given points on a 2D plane, return the `k` closest points to origin `(0,0)`.

### Input

```text
points = [[1,3], [-2,2]]
k = 1
```

### Output

```text
[[-2,2]]
```

### Pattern

```text
Top K smallest distance using max heap of size K.
```

### Why This Pattern Works

We need `k` smallest distances.

Use a max heap of size `k`.

```text
heap top = farthest among selected closest k
```

If a new point makes size > `k`, remove farthest.

### Result

```text
closest point = [-2, 2]
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> kClosest(vector<vector<int>>& points, int k) {
    priority_queue<pair<int, int>> pq;

    for (int i = 0; i < points.size(); i++) {
        int x = points[i][0];
        int y = points[i][1];

        int dist = x * x + y * y;

        pq.push({dist, i});

        if ((int)pq.size() > k) {
            pq.pop();
        }
    }

    vector<vector<int>> ans;

    while (!pq.empty()) {
        ans.push_back(points[pq.top().second]);
        pq.pop();
    }

    return ans;
}

int main() {
    vector<vector<int>> points = {{1,3}, {-2,2}};
    int k = 1;

    auto ans = kClosest(points, k);

    for (auto &p : ans) {
        cout << "[" << p[0] << "," << p[1] << "] ";
    }
}
```

### Step-by-Step Dry Run

```text
points = [[1,3], [-2,2]]
k = 1
```

Distance table:

```text
index:     0        1
point:   [1,3]   [-2,2]
dist:      10       8
```

Heap stores:

```text
[dist:index]
max heap by distance
```

```text
i = 0, point = [1,3]

distance = 1^2 + 3^2 = 10

heap before: []
action      : push [10:0]
heap after : [10:0]
size <= k  : no pop
```

```text
i = 1, point = [-2,2]

distance = (-2)^2 + 2^2 = 8

heap before: [10:0]
action      : push [8:1]
heap after : [10:0, 8:1]

size > k   : pop top [10:0]
heap after : [8:1]
```

```text
Remaining heap:
[8:1]

answer = points[1] = [-2,2]
```

### Attractive Note

```text
For K closest, keep a max heap.
The farthest selected point sits on top and gets removed first.
```

---

# Phase 3 — Streaming Heap

## 5. Kth Largest Element in a Stream

### Problem Statement

Design a class that continuously receives numbers and returns the kth largest after each insertion.

### Input

```text
k = 3
initial = [4, 5, 8, 2]
add values = [3, 5, 10, 9, 4]
```

### Output

```text
[4, 5, 5, 8, 8]
```

### Pattern

```text
Streaming top K using min heap of size K.
```

### Why This Pattern Works

Same as kth largest in array, but input arrives one by one.

Maintain exactly the top `k` largest elements seen so far.

### Result

```text
after each add, heap top is kth largest
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class KthLargest {
public:
    int k;
    priority_queue<int, vector<int>, greater<int>> pq;

    KthLargest(int k, vector<int>& nums) {
        this->k = k;

        for (int x : nums) {
            add(x);
        }
    }

    int add(int val) {
        pq.push(val);

        if ((int)pq.size() > k) {
            pq.pop();
        }

        return pq.top();
    }
};

int main() {
    vector<int> nums = {4, 5, 8, 2};
    KthLargest kth(3, nums);

    cout << kth.add(3) << "\n";
    cout << kth.add(5) << "\n";
    cout << kth.add(10) << "\n";
    cout << kth.add(9) << "\n";
    cout << kth.add(4) << "\n";
}
```

### Step-by-Step Dry Run

```text
k = 3
initial nums = [4, 5, 8, 2]

min heap keeps top 3 largest
```

Build initial heap:

```text
add 4:
heap = [4]

add 5:
heap = [4, 5]

add 8:
heap = [4, 5, 8]

add 2:
heap before pop = [2, 4, 8, 5]
size > 3, pop 2
heap = [4, 5, 8]

Current kth largest = 4
```

Process stream:

```text
add(3)

heap before: [4, 5, 8]
push 3
heap before pop: [3, 4, 8, 5]
pop 3
heap after : [4, 5, 8]

return 4
```

```text
add(5)

heap before: [4, 5, 8]
push 5
heap before pop: [4, 5, 8, 5]
pop 4
heap after : [5, 5, 8]

return 5
```

```text
add(10)

heap before: [5, 5, 8]
push 10
heap before pop: [5, 5, 8, 10]
pop 5
heap after : [5, 10, 8]

return 5
```

```text
add(9)

heap before: [5, 10, 8]
push 9
heap before pop: [5, 9, 8, 10]
pop 5
heap after : [8, 9, 10]

return 8
```

```text
add(4)

heap before: [8, 9, 10]
push 4
heap before pop: [4, 8, 10, 9]
pop 4
heap after : [8, 9, 10]

return 8
```

### Attractive Note

```text
Streaming heap = online filter.
Each new value enters the filter.
Only the strongest K values survive.
```

---

## 6. Find Median from Data Stream

### Problem Statement

Design a data structure that supports:

```text
addNum(num)
findMedian()
```

### Input

```text
add 1
add 2
findMedian
add 3
findMedian
```

### Output

```text
1.5
2
```

### Pattern

```text
Two heaps:
left max heap  = smaller half
right min heap = larger half
```

### Why This Pattern Works

Median depends on the middle.

Keep numbers split into two balanced halves:

```text
left half  <= right half
left size >= right size by at most 1
```

Then:

```text
odd count  → median = left.top()
even count → median = average(left.top(), right.top())
```

### Result

```text
median after [1,2] = 1.5
median after [1,2,3] = 2
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class MedianFinder {
public:
    priority_queue<int> leftMax;
    priority_queue<int, vector<int>, greater<int>> rightMin;

    void addNum(int num) {
        if (leftMax.empty() || num <= leftMax.top()) {
            leftMax.push(num);
        } else {
            rightMin.push(num);
        }

        if (leftMax.size() > rightMin.size() + 1) {
            rightMin.push(leftMax.top());
            leftMax.pop();
        } else if (rightMin.size() > leftMax.size()) {
            leftMax.push(rightMin.top());
            rightMin.pop();
        }
    }

    double findMedian() {
        if (leftMax.size() == rightMin.size()) {
            return ((double)leftMax.top() + rightMin.top()) / 2.0;
        }

        return leftMax.top();
    }
};

int main() {
    MedianFinder mf;

    mf.addNum(1);
    mf.addNum(2);
    cout << mf.findMedian() << "\n";

    mf.addNum(3);
    cout << mf.findMedian() << "\n";
}
```

### Step-by-Step Dry Run

```text
stream = [1, 2, 3]

leftMax  = smaller half, top is largest of smaller half
rightMin = larger half, top is smallest of larger half
```

```text
add 1

left before : []
right before: []

1 goes to left

left after  : [1]
right after : []

median = left.top = 1
```

```text
add 2

left before : [1]
right before: []

2 > left.top(1)
2 goes to right

left after  : [1]
right after : [2]

sizes equal
median = (1 + 2) / 2 = 1.5
```

```text
add 3

left before : [1]
right before: [2]

3 > left.top(1)
3 goes to right

right size > left size
move right.top() = 2 to left

left after  : [2, 1]
right after : [3]

median = left.top = 2
```

### Attractive Note

```text
Two heaps create a moving middle line.
Left heap protects the lower half.
Right heap protects the upper half.
The median lives at the boundary.
```

---

# Phase 4 — K-Way Merge

## 7. Merge K Sorted Lists

### Problem Statement

Given `k` sorted linked lists, merge them into one sorted linked list.

### Input

```text
lists = [
  [1,4,5],
  [1,3,4],
  [2,6]
]
```

### Output

```text
[1,1,2,3,4,4,5,6]
```

### Pattern

```text
K-way merge using min heap.
```

### Why This Pattern Works

Each list is already sorted.
The next smallest element must be among the current heads of all lists.

Use a min heap to store current head from each list.

### Result

```text
merged list = [1,1,2,3,4,4,5,6]
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct ListNode {
    int val;
    ListNode* next;

    ListNode(int x) : val(x), next(nullptr) {}
};

struct Compare {
    bool operator()(ListNode* a, ListNode* b) {
        return a->val > b->val;
    }
};

ListNode* mergeKLists(vector<ListNode*>& lists) {
    priority_queue<ListNode*, vector<ListNode*>, Compare> pq;

    for (auto node : lists) {
        if (node) pq.push(node);
    }

    ListNode dummy(0);
    ListNode* tail = &dummy;

    while (!pq.empty()) {
        ListNode* node = pq.top();
        pq.pop();

        tail->next = node;
        tail = tail->next;

        if (node->next) {
            pq.push(node->next);
        }
    }

    return dummy.next;
}
```

### Step-by-Step Dry Run

```text
lists:
L0: 1 -> 4 -> 5
L1: 1 -> 3 -> 4
L2: 2 -> 6
```

Initial heap stores first node from each list:

```text
heap = [1(L0), 1(L1), 2(L2)]
result = []
```

```text
Step 1

heap before: [1(L0), 1(L1), 2(L2)]
pop        : 1(L0)
result     : [1]

push next from L0 = 4

heap after : [1(L1), 2(L2), 4(L0)]
```

```text
Step 2

heap before: [1(L1), 2(L2), 4(L0)]
pop        : 1(L1)
result     : [1, 1]

push next from L1 = 3

heap after : [2(L2), 4(L0), 3(L1)]
```

```text
Step 3

heap before: [2(L2), 3(L1), 4(L0)]
pop        : 2(L2)
result     : [1, 1, 2]

push next from L2 = 6

heap after : [3(L1), 4(L0), 6(L2)]
```

```text
Step 4

heap before: [3(L1), 4(L0), 6(L2)]
pop        : 3(L1)
result     : [1, 1, 2, 3]

push next from L1 = 4

heap after : [4(L0), 6(L2), 4(L1)]
```

```text
Continue similarly:

pop 4(L0) -> result [1,1,2,3,4], push 5
pop 4(L1) -> result [1,1,2,3,4,4]
pop 5(L0) -> result [1,1,2,3,4,4,5]
pop 6(L2) -> result [1,1,2,3,4,4,5,6]
```

### Attractive Note

```text
K-way merge rule:
The global next smallest is always one of the current K heads.
```

---

## 8. Find K Pairs with Smallest Sums

### Problem Statement

Given two sorted arrays `nums1` and `nums2`, return `k` pairs with the smallest sums.

### Input

```text
nums1 = [1, 7, 11]
nums2 = [2, 4, 6]
k = 3
```

### Output

```text
[[1,2], [1,4], [1,6]]
```

### Pattern

```text
K-way merge on pair sums.
```

### Why This Pattern Works

For each `nums1[i]`, the pairs with `nums2` are sorted:

```text
(nums1[i], nums2[0])
(nums1[i], nums2[1])
(nums1[i], nums2[2])
```

This becomes merging multiple sorted pair streams.

### Result

```text
3 smallest pairs = [[1,2], [1,4], [1,6]]
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> kSmallestPairs(vector<int>& nums1, vector<int>& nums2, int k) {
    vector<vector<int>> ans;

    if (nums1.empty() || nums2.empty()) return ans;

    using T = tuple<int, int, int>; // sum, i, j

    priority_queue<T, vector<T>, greater<T>> pq;

    for (int i = 0; i < nums1.size() && i < k; i++) {
        pq.push({nums1[i] + nums2[0], i, 0});
    }

    while (!pq.empty() && ans.size() < k) {
        auto [sum, i, j] = pq.top();
        pq.pop();

        ans.push_back({nums1[i], nums2[j]});

        if (j + 1 < nums2.size()) {
            pq.push({nums1[i] + nums2[j + 1], i, j + 1});
        }
    }

    return ans;
}

int main() {
    vector<int> nums1 = {1, 7, 11};
    vector<int> nums2 = {2, 4, 6};
    int k = 3;

    auto ans = kSmallestPairs(nums1, nums2, k);

    for (auto &p : ans) {
        cout << "[" << p[0] << "," << p[1] << "] ";
    }
}
```

### Step-by-Step Dry Run

```text
nums1 = [1, 7, 11]
nums2 = [2, 4, 6]
k = 3
```

Pair streams:

```text
For nums1[0] = 1:
[1,2]=3, [1,4]=5, [1,6]=7

For nums1[1] = 7:
[7,2]=9, [7,4]=11, [7,6]=13

For nums1[2] = 11:
[11,2]=13, [11,4]=15, [11,6]=17
```

Initial heap:

```text
heap = [
  sum 3  -> (0,0) = [1,2]
  sum 9  -> (1,0) = [7,2]
  sum 13 -> (2,0) = [11,2]
]
ans = []
```

```text
Step 1

heap before: [3:(0,0), 9:(1,0), 13:(2,0)]
pop        : 3:(0,0) => [1,2]
ans        : [[1,2]]

push next in same stream:
(0,1) => [1,4], sum = 5

heap after : [5:(0,1), 13:(2,0), 9:(1,0)]
```

```text
Step 2

heap before: [5:(0,1), 9:(1,0), 13:(2,0)]
pop        : 5:(0,1) => [1,4]
ans        : [[1,2], [1,4]]

push next:
(0,2) => [1,6], sum = 7

heap after : [7:(0,2), 13:(2,0), 9:(1,0)]
```

```text
Step 3

heap before: [7:(0,2), 9:(1,0), 13:(2,0)]
pop        : 7:(0,2) => [1,6]
ans        : [[1,2], [1,4], [1,6]]

ans size = k
stop
```

### Attractive Note

```text
When multiple sorted streams compete,
min heap lets the smallest current stream-head win.
```

---

# Phase 5 — Scheduling / Greedy Heap

## 9. Meeting Rooms II

### Problem Statement

Given meeting intervals, return the minimum number of rooms required.

### Input

```text
intervals = [[0,30], [5,10], [15,20]]
```

### Output

```text
2
```

### Pattern

```text
Sort by start time + min heap of end times.
```

### Why This Pattern Works

A room becomes free when its meeting end time is <= current meeting start.

Min heap keeps earliest ending meeting on top.

```text
If earliest end <= current start:
reuse that room

Else:
need new room
```

### Result

```text
minimum rooms = 2
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int minMeetingRooms(vector<vector<int>>& intervals) {
    sort(intervals.begin(), intervals.end());

    priority_queue<int, vector<int>, greater<int>> pq;

    for (auto &in : intervals) {
        int start = in[0];
        int end = in[1];

        if (!pq.empty() && pq.top() <= start) {
            pq.pop();
        }

        pq.push(end);
    }

    return pq.size();
}

int main() {
    vector<vector<int>> intervals = {{0,30}, {5,10}, {15,20}};
    cout << minMeetingRooms(intervals) << "\n";
}
```

### Step-by-Step Dry Run

```text
intervals = [[0,30], [5,10], [15,20]]

sorted by start:
[[0,30], [5,10], [15,20]]

min heap stores meeting end times
```

```text
Meeting 1: [0,30]

heap before: []
no room free

action: allocate room ending at 30
heap after: [30]

rooms used = 1
```

```text
Meeting 2: [5,10]

heap before: [30]
earliest end = 30

condition:
30 <= 5 ? no

action: need new room ending at 10
heap after: [10, 30]

rooms used = 2
```

```text
Meeting 3: [15,20]

heap before: [10, 30]
earliest end = 10

condition:
10 <= 15 ? yes

action:
pop 10, reuse that room
push 20

heap after: [20, 30]

rooms used = 2
```

```text
answer = heap size = 2
```

### Attractive Note

```text
Meeting room heap answers:
Which room frees the earliest?
```

---

## 10. Task Scheduler

### Problem Statement

Given tasks represented by characters and cooldown `n`, return the least number of intervals needed to finish all tasks.

### Input

```text
tasks = ["A","A","A","B","B","B"]
n = 2
```

### Output

```text
8
```

One valid order:

```text
A B idle A B idle A B
```

### Pattern

```text
Max heap by remaining frequency + cooldown simulation.
```

### Why This Pattern Works

We always want to execute the task with the highest remaining frequency to avoid future idle time.

### Result

```text
minimum intervals = 8
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int leastInterval(vector<char>& tasks, int n) {
    vector<int> freq(26, 0);

    for (char c : tasks) {
        freq[c - 'A']++;
    }

    priority_queue<int> pq;

    for (int f : freq) {
        if (f > 0) pq.push(f);
    }

    int time = 0;

    while (!pq.empty()) {
        vector<int> temp;
        int cycle = n + 1;

        while (cycle > 0 && !pq.empty()) {
            int cur = pq.top();
            pq.pop();

            cur--;
            time++;
            cycle--;

            if (cur > 0) temp.push_back(cur);
        }

        for (int x : temp) {
            pq.push(x);
        }

        if (pq.empty()) break;

        time += cycle;
    }

    return time;
}

int main() {
    vector<char> tasks = {'A','A','A','B','B','B'};
    int n = 2;

    cout << leastInterval(tasks, n) << "\n";
}
```

### Step-by-Step Dry Run

```text
tasks = A A A B B B
n = 2

frequency:
A = 3
B = 3

max heap = [3(A), 3(B)]
cycle length = n + 1 = 3
```

```text
Cycle 1

heap before: [A:3, B:3]
cycle slots: _ _ _

slot 1:
pop A:3
execute A
remaining A = 2
temp = [A:2]
time = 1

slot 2:
pop B:3
execute B
remaining B = 2
temp = [A:2, B:2]
time = 2

slot 3:
heap empty
idle
time = 3

push temp back:
heap = [A:2, B:2]

schedule so far:
A B idle
```

```text
Cycle 2

heap before: [A:2, B:2]
cycle slots: _ _ _

slot 1:
execute A
remaining A = 1
time = 4

slot 2:
execute B
remaining B = 1
time = 5

slot 3:
idle
time = 6

push temp:
heap = [A:1, B:1]

schedule:
A B idle A B idle
```

```text
Cycle 3

heap before: [A:1, B:1]

slot 1:
execute A
remaining A = 0
time = 7

slot 2:
execute B
remaining B = 0
time = 8

heap empty
stop

answer = 8
```

### Attractive Note

```text
Most frequent tasks are the biggest threat.
Schedule them early to reduce idle gaps.
```

---

# Phase 6 — Heap + Greedy Optimization

## 11. Minimum Cost to Connect Sticks

### Problem Statement

You are given sticks with lengths. You can connect two sticks with cost equal to their sum. Return minimum total cost to connect all sticks.

### Input

```text
sticks = [2, 4, 3]
```

### Output

```text
14
```

### Pattern

```text
Repeatedly combine two smallest values.
Use min heap.
```

### Why This Pattern Works

To minimize total cost, small sticks should be combined first.

This is same idea as Huffman coding.

### Result

```text
minimum cost = 14
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int connectSticks(vector<int>& sticks) {
    priority_queue<int, vector<int>, greater<int>> pq;

    for (int x : sticks) {
        pq.push(x);
    }

    int cost = 0;

    while (pq.size() > 1) {
        int a = pq.top(); pq.pop();
        int b = pq.top(); pq.pop();

        int sum = a + b;
        cost += sum;

        pq.push(sum);
    }

    return cost;
}

int main() {
    vector<int> sticks = {2, 4, 3};
    cout << connectSticks(sticks) << "\n";
}
```

### Step-by-Step Dry Run

```text
sticks = [2, 4, 3]

min heap = [2, 4, 3]
total cost = 0
```

```text
Round 1

heap before: [2, 3, 4]

pop smallest 1: 2
pop smallest 2: 3

connect = 2 + 3 = 5
cost = 0 + 5 = 5

push 5

heap after: [4, 5]
```

```text
Round 2

heap before: [4, 5]

pop smallest 1: 4
pop smallest 2: 5

connect = 4 + 5 = 9
cost = 5 + 9 = 14

push 9

heap after: [9]
```

```text
Only one stick remains.
answer = 14
```

### Attractive Note

```text
If combining creates future cost,
combine smallest first.
```

---

## 12. IPO

### Problem Statement

You are given projects with capital requirements and profits.

You can do at most `k` projects.
You start with capital `w`.

Each time, choose one project you can afford and gain its profit.

Return maximized capital.

### Input

```text
k = 2
w = 0
profits = [1, 2, 3]
capital = [0, 1, 1]
```

### Output

```text
4
```

### Pattern

```text
Sort by capital + max heap by profit.
```

### Why This Pattern Works

At each step:

```text
1. Add all currently affordable projects.
2. Choose the most profitable among them.
```

Sorting lets us discover affordable projects.
Max heap lets us choose best profit.

### Result

```text
maximum capital = 4
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int findMaximizedCapital(int k, int w, vector<int>& profits, vector<int>& capital) {
    vector<pair<int,int>> projects;

    for (int i = 0; i < profits.size(); i++) {
        projects.push_back({capital[i], profits[i]});
    }

    sort(projects.begin(), projects.end());

    priority_queue<int> pq;
    int i = 0;
    int n = projects.size();

    while (k--) {
        while (i < n && projects[i].first <= w) {
            pq.push(projects[i].second);
            i++;
        }

        if (pq.empty()) break;

        w += pq.top();
        pq.pop();
    }

    return w;
}

int main() {
    int k = 2;
    int w = 0;
    vector<int> profits = {1, 2, 3};
    vector<int> capital = {0, 1, 1};

    cout << findMaximizedCapital(k, w, profits, capital) << "\n";
}
```

### Step-by-Step Dry Run

```text
k = 2
w = 0

projects as [capital, profit]:
[0,1], [1,2], [1,3]

sort by capital:
[0,1], [1,2], [1,3]
```

```text
Round 1

current capital w = 0

affordable projects:
[0,1]

push profit 1 into max heap

heap before choosing: [1]
choose profit = 1

w = 0 + 1 = 1
remaining k = 1
```

```text
Round 2

current capital w = 1

new affordable projects:
[1,2]
[1,3]

push profits 2 and 3

heap before choosing: [3, 2]
choose profit = 3

w = 1 + 3 = 4
remaining k = 0
```

```text
answer = 4
```

### Attractive Note

```text
Sort unlocks choices.
Heap selects the best unlocked choice.
```

---

# Phase 7 — Heap + Lazy Deletion

## 13. Sliding Window Median

### Problem Statement

Given an array and window size `k`, return median of each sliding window.

### Input

```text
nums = [1, 3, -1, -3, 5, 3, 6, 7]
k = 3
```

### Output

```text
[1, -1, -1, 3, 5, 6]
```

### Pattern

```text
Two heaps + lazy deletion.
```

### Why This Pattern Works

Median needs two balanced halves like streaming median.

But sliding window also removes old elements.

Direct deletion from heap is not easy, so we use:

```text
delayed map
```

When an outdated element reaches heap top, remove it then.

### Result

```text
medians = [1, -1, -1, 3, 5, 6]
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class DualHeap {
public:
    priority_queue<int> small;
    priority_queue<int, vector<int>, greater<int>> large;
    unordered_map<int, int> delayed;

    int k;
    int smallSize = 0;
    int largeSize = 0;

    DualHeap(int k) {
        this->k = k;
    }

    void pruneSmall() {
        while (!small.empty()) {
            int x = small.top();

            if (delayed.count(x)) {
                delayed[x]--;

                if (delayed[x] == 0) {
                    delayed.erase(x);
                }

                small.pop();
            } else {
                break;
            }
        }
    }

    void pruneLarge() {
        while (!large.empty()) {
            int x = large.top();

            if (delayed.count(x)) {
                delayed[x]--;

                if (delayed[x] == 0) {
                    delayed.erase(x);
                }

                large.pop();
            } else {
                break;
            }
        }
    }

    void makeBalance() {
        if (smallSize > largeSize + 1) {
            large.push(small.top());
            small.pop();

            smallSize--;
            largeSize++;

            pruneSmall();
        } else if (smallSize < largeSize) {
            small.push(large.top());
            large.pop();

            smallSize++;
            largeSize--;

            pruneLarge();
        }
    }

    void insert(int num) {
        if (small.empty() || num <= small.top()) {
            small.push(num);
            smallSize++;
        } else {
            large.push(num);
            largeSize++;
        }

        makeBalance();
    }

    void erase(int num) {
        delayed[num]++;

        if (num <= small.top()) {
            smallSize--;

            if (num == small.top()) {
                pruneSmall();
            }
        } else {
            largeSize--;

            if (!large.empty() && num == large.top()) {
                pruneLarge();
            }
        }

        makeBalance();
    }

    double getMedian() {
        if (k % 2 == 1) {
            return small.top();
        }

        return ((long long)small.top() + large.top()) / 2.0;
    }
};

vector<double> medianSlidingWindow(vector<int>& nums, int k) {
    DualHeap dh(k);
    vector<double> ans;

    for (int i = 0; i < k; i++) {
        dh.insert(nums[i]);
    }

    ans.push_back(dh.getMedian());

    for (int i = k; i < nums.size(); i++) {
        dh.insert(nums[i]);
        dh.erase(nums[i - k]);
        ans.push_back(dh.getMedian());
    }

    return ans;
}

int main() {
    vector<int> nums = {1, 3, -1, -3, 5, 3, 6, 7};
    int k = 3;

    vector<double> ans = medianSlidingWindow(nums, k);

    for (double x : ans) {
        cout << x << " ";
    }
}
```

### Step-by-Step Dry Run

```text
nums = [1, 3, -1, -3, 5, 3, 6, 7]
k = 3

small = max heap for lower half
large = min heap for upper half

For odd k:
median = small.top()
```

```text
Initial window [1, 3, -1]

Insert 1:
small = [1]
large = []

Insert 3:
3 > small.top(1), goes to large
small = [1]
large = [3]

Insert -1:
-1 <= small.top(1), goes to small
small = [1, -1]
large = [3]

Window: [1, 3, -1]
sorted: [-1, 1, 3]
median = small.top = 1

answer = [1]
```

```text
Slide window:
remove 1
add -3

Before:
small = [1, -1]
large = [3]

Add -3:
-3 <= small.top(1)
small = [1, -1, -3]

Erase 1:
mark delayed[1]++
1 is at small.top, prune it

small after prune = [-1, -3]
large = [3]

Window: [3, -1, -3]
sorted: [-3, -1, 3]
median = -1

answer = [1, -1]
```

```text
Slide window:
remove 3
add 5

Add 5:
5 > small.top(-1), goes large
large = [3, 5]

Erase 3:
mark delayed[3]++
3 is at large.top, prune it

large after prune = [5]
small = [-1, -3]

Window: [-1, -3, 5]
sorted: [-3, -1, 5]
median = -1

answer = [1, -1, -1]
```

```text
Continue:

Window [-3,5,3] sorted [-3,3,5] median = 3
Window [5,3,6] sorted [3,5,6] median = 5
Window [3,6,7] sorted [3,6,7] median = 6

answer = [1, -1, -1, 3, 5, 6]
```

### Attractive Note

```text
Lazy deletion means:
Do not remove immediately.
Mark it dead.
When it reaches the top, throw it away.
```

---

# Priority Queue Pattern Decision Table

| Problem Signal | Heap Pattern |
|---|---|
| kth largest | min heap size k |
| kth smallest | max heap size k |
| top k frequent | frequency + min heap size k |
| k closest | max heap size k |
| stream kth largest | min heap size k |
| median stream | two heaps |
| merge k sorted lists | min heap of heads |
| smallest pairs | k-way merge heap |
| meeting rooms | min heap of end times |
| task scheduling | max heap frequency |
| connect sticks | min heap combine smallest |
| choose max profit affordable | sort + max heap |
| sliding median | two heaps + lazy deletion |

---

# Must-Solve Order

```text
1. Kth Largest Element in an Array
2. Last Stone Weight
3. Top K Frequent Elements
4. K Closest Points to Origin
5. Kth Largest Element in a Stream
6. Find Median from Data Stream
7. Merge K Sorted Lists
8. Find K Pairs with Smallest Sums
9. Meeting Rooms II
10. Task Scheduler
11. Minimum Cost to Connect Sticks
12. IPO
13. Sliding Window Median
```

---

# One-Line Mental Model

```text
Priority queue is useful when the current best candidate must be accessed repeatedly.
```

---

# Final Revision Notes

```text
Top K largest
→ min heap of size K

Top K smallest
→ max heap of size K

Repeatedly take largest
→ max heap

Repeatedly take smallest
→ min heap

Streaming median
→ two heaps

Merge K sorted things
→ min heap

Scheduling resources
→ min heap by finish time

Greedy unlock + choose best
→ sort + max heap

Hard deletion from heap
→ lazy deletion
```
