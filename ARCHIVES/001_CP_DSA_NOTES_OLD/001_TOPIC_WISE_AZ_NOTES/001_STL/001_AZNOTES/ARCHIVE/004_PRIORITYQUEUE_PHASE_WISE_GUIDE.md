# Heap / Priority Queue — FAANG + CP Phase-Wise Problem Guide

> Goal: Master heap / priority queue for **FAANG interviews first**, then move into **advanced CP contest patterns**.
>
> Heap is a **current-best-candidate engine**:
>
> ```text
> Whenever you repeatedly need the current min/max/best candidate,
> priority_queue should come to mind.
> ```

---

# Clickable Index

## 0. Mental Map
- [0.1 Heap Recognition Map](#01-heap-recognition-map)
- [0.2 Min Heap vs Max Heap](#02-min-heap-vs-max-heap)
- [0.3 C++ Priority Queue Templates](#03-c-priority-queue-templates)
- [0.4 FAANG vs CP Split](#04-faang-vs-cp-split)

## FAANG Track

## Phase F1 — Basic Heap Thinking
- [1. Kth Largest Element in an Array](#1-kth-largest-element-in-an-array)
- [2. Last Stone Weight](#2-last-stone-weight)

## Phase F2 — Top K Patterns
- [3. Top K Frequent Elements](#3-top-k-frequent-elements)
- [4. K Closest Points to Origin](#4-k-closest-points-to-origin)

## Phase F3 — Streaming Heap
- [5. Kth Largest Element in a Stream](#5-kth-largest-element-in-a-stream)
- [6. Find Median from Data Stream](#6-find-median-from-data-stream)

## Phase F4 — K-Way Merge
- [7. Merge K Sorted Lists](#7-merge-k-sorted-lists)
- [8. Find K Pairs with Smallest Sums](#8-find-k-pairs-with-smallest-sums)

## Phase F5 — Scheduling / Resource Allocation
- [9. Meeting Rooms II](#9-meeting-rooms-ii)
- [10. Task Scheduler](#10-task-scheduler)

## Phase F6 — Greedy Heap
- [11. Minimum Cost to Connect Sticks](#11-minimum-cost-to-connect-sticks)
- [12. IPO](#12-ipo)

## CP Advanced Track

## Phase CP1 — Heap + Graph Shortest Path
- [13. Network Delay Time](#13-network-delay-time)

## Phase CP2 — 0/1 Edge Alternative Awareness
- [14. Minimum Cost to Make Valid Path in Grid](#14-minimum-cost-to-make-valid-path-in-grid)

## Phase CP3 — Lazy Deletion / Stale States
- [15. Sliding Window Median](#15-sliding-window-median)

## Phase CP4 — Sweep Line + Heap
- [16. The Skyline Problem](#16-the-skyline-problem)

## Phase CP5 — Heap + Binary Search / Ordered Generation
- [17. Kth Smallest Element in a Sorted Matrix](#17-kth-smallest-element-in-a-sorted-matrix)

## Final Revision
- [Heap Pattern Decision Table](#heap-pattern-decision-table)
- [Must-Solve Order](#must-solve-order)
- [One-Line Mental Model](#one-line-mental-model)

---

# 0. Mental Map

## 0.1 Heap Recognition Map

```text
Need current maximum repeatedly?
→ max heap

Need current minimum repeatedly?
→ min heap

Need top K largest?
→ min heap of size K

Need top K smallest?
→ max heap of size K

Need kth largest?
→ min heap size K

Need kth smallest?
→ max heap size K OR min heap ordered generation

Need streaming median?
→ two heaps

Need merge K sorted sources?
→ min heap with one head from each source

Need schedule rooms/resources?
→ min heap by end time

Need cooldown / repeated most frequent?
→ max heap by frequency

Need shortest path with positive weights?
→ min heap Dijkstra

Need active intervals with current max?
→ sweep line + max heap

Need delete from heap but cannot?
→ lazy deletion
```

---

## 0.2 Min Heap vs Max Heap

```text
Max heap:
top = largest

Min heap:
top = smallest
```

Important inversion:

```text
Top K largest
→ keep only K largest elements
→ smallest among them is weakest
→ use min heap

Top K smallest
→ keep only K smallest elements
→ largest among them is weakest
→ use max heap
```

---

## 0.3 C++ Priority Queue Templates

### Max Heap

```cpp
priority_queue<int> pq;
```

### Min Heap

```cpp
priority_queue<int, vector<int>, greater<int>> pq;
```

### Max Heap of Pairs

```cpp
priority_queue<pair<int,int>> pq;
```

### Min Heap of Pairs

```cpp
priority_queue<pair<int,int>, vector<pair<int,int>>, greater<pair<int,int>>> pq;
```

### Min Heap of Tuples

```cpp
using T = tuple<int,int,int>;
priority_queue<T, vector<T>, greater<T>> pq;
```

---

## 0.4 FAANG vs CP Split

```text
FAANG Heap:
Top K
Streaming
Median
K-way merge
Scheduling
Greedy heap

CP Advanced Heap:
Dijkstra
0/1 BFS awareness
Lazy stale states
Sweep line + heap
Ordered generation
Heap + binary search
```

For interviews:

```text
Pattern recognition + clean code + explanation
```

For CP:

```text
Modeling + speed + hidden transformation
```

---

# FAANG TRACK

---

# Phase F1 — Basic Heap Thinking

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
heap top = smallest among current top K largest
```

When heap size exceeds `k`, remove the smallest.  
At the end, heap contains exactly the largest `k` values.  
The top is the kth largest.

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
heap top = weakest survivor among top 2
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
Min heap of size K is a survival gate.
Only the strongest K values remain.
The weakest survivor is the answer.
```

---

## 2. Last Stone Weight

### Problem Statement

You are given stones. Each turn, smash the two heaviest stones.

If equal, both disappear.  
If different, the new stone has weight `heavier - lighter`.

Return final stone weight or `0`.

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
Repeatedly take two maximum values.
Use max heap.
```

### Why This Pattern Works

Every operation asks for the two heaviest stones.  
A max heap gives the current heaviest in `O(log n)`.

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

new stone = 2 - 1 = 1

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
answer = 1
```

### Attractive Note

```text
If the problem repeatedly asks for the largest or smallest item,
heap is usually the cleanest structure.
```

---

# Phase F2 — Top K Patterns

## 3. Top K Frequent Elements

### Problem Statement

Given an array `nums`, return the `k` most frequent elements.

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

The ranking key is frequency.

Keep a min heap of size `k`.

```text
heap top = currently weakest among top K frequent elements
```

When size exceeds `k`, remove the lowest frequency.

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

Frequency table:

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
Remaining heap contains numbers:
2 and 1

Valid answer = [1, 2]
```

### Attractive Note

```text
Heap ranking key can be anything:
value, frequency, distance, time, profit.
Here the key is frequency.
```

---

## 4. K Closest Points to Origin

### Problem Statement

Given points on a 2D plane, return the `k` closest points to `(0, 0)`.

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

Keep a max heap of size `k`.

```text
heap top = farthest among selected closest K
```

If heap size exceeds `k`, remove the farthest.

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
index:      0        1
point:    [1,3]   [-2,2]
dist:       10       8
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
answer = points[1] = [-2,2]
```

### Attractive Note

```text
For K closest, keep a max heap.
The worst selected candidate stays on top and gets removed first.
```

---

# Phase F3 — Streaming Heap

## 5. Kth Largest Element in a Stream

### Problem Statement

Design a class that receives numbers continuously and returns the kth largest after each insertion.

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

Maintain the top `k` largest elements seen so far.

```text
heap top = kth largest so far
```

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

Build initial:

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

current kth largest = 4
```

Stream:

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
Streaming top K is an online filter.
Every new number enters.
Only the strongest K survive.
```

---

## 6. Find Median from Data Stream

### Problem Statement

Design a data structure supporting:

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

Median lives between two halves.

Maintain:

```text
left half <= right half
left size >= right size
left size - right size <= 1
```

Then:

```text
odd count  → left.top()
even count → average(left.top(), right.top())
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

leftMax  = smaller half
rightMin = larger half
```

```text
add 1

left before : []
right before: []

1 goes to left

left after  : [1]
right after : []

median = 1
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
Median is always on the boundary.
```

---

# Phase F4 — K-Way Merge

## 7. Merge K Sorted Lists

### Problem Statement

Given `k` sorted linked lists, merge them into one sorted list.

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

Each list is sorted.  
The global next smallest must be one of the current heads.

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
L0: 1 -> 4 -> 5
L1: 1 -> 3 -> 4
L2: 2 -> 6
```

Initial:

```text
heap = [1(L0), 1(L1), 2(L2)]
result = []
```

```text
Step 1

heap before: [1(L0), 1(L1), 2(L2)]
pop        : 1(L0)
result     : [1]
push next  : 4(L0)
heap after : [1(L1), 2(L2), 4(L0)]
```

```text
Step 2

heap before: [1(L1), 2(L2), 4(L0)]
pop        : 1(L1)
result     : [1, 1]
push next  : 3(L1)
heap after : [2(L2), 4(L0), 3(L1)]
```

```text
Step 3

heap before: [2(L2), 3(L1), 4(L0)]
pop        : 2(L2)
result     : [1, 1, 2]
push next  : 6(L2)
heap after : [3(L1), 4(L0), 6(L2)]
```

```text
Continue:

pop 3(L1) -> result [1,1,2,3], push 4(L1)
pop 4(L0) -> result [1,1,2,3,4], push 5(L0)
pop 4(L1) -> result [1,1,2,3,4,4]
pop 5(L0) -> result [1,1,2,3,4,4,5]
pop 6(L2) -> result [1,1,2,3,4,4,5,6]
```

### Attractive Note

```text
K sorted lists become K sorted streams.
Heap picks the smallest stream head each time.
```

---

## 8. Find K Pairs with Smallest Sums

### Problem Statement

Given two sorted arrays, return `k` pairs with the smallest sums.

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
K-way merge on pair streams.
```

### Why This Pattern Works

For every `nums1[i]`, pairs with `nums2` form a sorted stream:

```text
(nums1[i], nums2[0])
(nums1[i], nums2[1])
(nums1[i], nums2[2])
```

Merge these streams using min heap.

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

Streams:

```text
i = 0: [1,2]=3, [1,4]=5, [1,6]=7
i = 1: [7,2]=9, [7,4]=11, [7,6]=13
i = 2: [11,2]=13, [11,4]=15, [11,6]=17
```

Initial heap:

```text
heap = [
  3:(0,0),
  9:(1,0),
  13:(2,0)
]
ans = []
```

```text
Step 1

heap before: [3:(0,0), 9:(1,0), 13:(2,0)]
pop        : 3:(0,0) => [1,2]
ans        : [[1,2]]

push next from same stream:
5:(0,1) => [1,4]

heap after : [5:(0,1), 13:(2,0), 9:(1,0)]
```

```text
Step 2

heap before: [5:(0,1), 9:(1,0), 13:(2,0)]
pop        : 5:(0,1) => [1,4]
ans        : [[1,2], [1,4]]

push next:
7:(0,2) => [1,6]

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
When many sorted streams compete,
min heap gives the next global best candidate.
```

---

# Phase F5 — Scheduling / Resource Allocation

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

A room is reusable if its end time is `<= current start`.

Min heap gives the earliest ending room.

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

sorted:
[[0,30], [5,10], [15,20]]

heap stores room end times
```

```text
Meeting 1: [0,30]

heap before: []
action      : allocate new room ending at 30
heap after : [30]
rooms used : 1
```

```text
Meeting 2: [5,10]

heap before: [30]
earliest end = 30

condition:
30 <= 5 ? no

action      : allocate new room ending at 10
heap after : [10, 30]
rooms used : 2
```

```text
Meeting 3: [15,20]

heap before: [10, 30]
earliest end = 10

condition:
10 <= 15 ? yes

action:
pop 10, reuse room
push 20

heap after : [20, 30]
rooms used : 2
```

```text
answer = 2
```

### Attractive Note

```text
Min heap answers:
Which room becomes free first?
```

---

## 10. Task Scheduler

### Problem Statement

Given tasks and cooldown `n`, return least intervals to finish all tasks.

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
Max heap by remaining frequency.
```

### Why This Pattern Works

Most frequent tasks create the biggest cooldown risk.  
Scheduling them first reduces future idle gaps.

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

max heap = [A:3, B:3]
cycle length = n + 1 = 3
```

```text
Cycle 1

heap before: [A:3, B:3]
slots: _ _ _

slot 1:
execute A
A remaining = 2
temp = [A:2]
time = 1

slot 2:
execute B
B remaining = 2
temp = [A:2, B:2]
time = 2

slot 3:
heap empty
idle
time = 3

push temp back:
heap = [A:2, B:2]

schedule:
A B idle
```

```text
Cycle 2

heap before: [A:2, B:2]

slot 1: execute A, A remaining = 1, time = 4
slot 2: execute B, B remaining = 1, time = 5
slot 3: idle, time = 6

heap = [A:1, B:1]

schedule:
A B idle A B idle
```

```text
Cycle 3

heap before: [A:1, B:1]

slot 1:
execute A
time = 7

slot 2:
execute B
time = 8

heap empty
stop

answer = 8
```

### Attractive Note

```text
Most frequent task is the biggest threat.
Heap always attacks the biggest threat first.
```

---

# Phase F6 — Greedy Heap

## 11. Minimum Cost to Connect Sticks

### Problem Statement

Connect sticks. Cost of connecting two sticks is their sum. Return minimum total cost.

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

Small values should be combined first because combined sticks may be paid again later.

This is the Huffman coding greedy idea.

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

min heap = [2, 3, 4]
cost = 0
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
answer = 14
```

### Attractive Note

```text
If merging creates future cost,
merge the cheapest items first.
```

---

## 12. IPO

### Problem Statement

You can do at most `k` projects.  
Each project has required capital and profit.  
Start with capital `w`.  
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

At each round:

```text
1. Unlock all projects with capital <= current money.
2. Pick the maximum profit among unlocked projects.
```

Sorting finds newly unlocked projects.  
Max heap picks best profit.

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

projects [capital, profit]:
[0,1], [1,2], [1,3]
```

Sorted:

```text
[0,1], [1,2], [1,3]
```

```text
Round 1

current capital = 0

affordable:
[0,1]

heap before choice: [1]
choose profit = 1

w = 0 + 1 = 1
remaining k = 1
```

```text
Round 2

current capital = 1

new affordable:
[1,2]
[1,3]

push profits 2 and 3

heap before choice: [3, 2]
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
Heap chooses the best unlocked choice.
```

---

# CP ADVANCED TRACK

---

# Phase CP1 — Heap + Graph Shortest Path

## 13. Network Delay Time

### Problem Statement

You are given directed weighted edges `times[i] = [u, v, w]`.  
A signal starts from node `k`.  
Return how long it takes to reach all nodes. If impossible, return `-1`.

### Input

```text
n = 4
times = [[2,1,1], [2,3,1], [3,4,1]]
k = 2
```

### Output

```text
2
```

### Pattern

```text
Dijkstra using min heap.
```

### Why This Pattern Works

All edge weights are positive.  
Dijkstra always expands the currently closest unprocessed node.

```text
heap top = node with smallest known distance
```

### Result

```text
network delay = 2
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int networkDelayTime(vector<vector<int>>& times, int n, int k) {
    vector<vector<pair<int,int>>> adj(n + 1);

    for (auto &e : times) {
        int u = e[0], v = e[1], w = e[2];
        adj[u].push_back({v, w});
    }

    const int INF = 1e9;
    vector<int> dist(n + 1, INF);

    priority_queue<
        pair<int,int>,
        vector<pair<int,int>>,
        greater<pair<int,int>>
    > pq;

    dist[k] = 0;
    pq.push({0, k});

    while (!pq.empty()) {
        auto [d, u] = pq.top();
        pq.pop();

        if (d != dist[u]) continue;

        for (auto [v, w] : adj[u]) {
            if (dist[u] + w < dist[v]) {
                dist[v] = dist[u] + w;
                pq.push({dist[v], v});
            }
        }
    }

    int ans = 0;

    for (int i = 1; i <= n; i++) {
        if (dist[i] == INF) return -1;
        ans = max(ans, dist[i]);
    }

    return ans;
}

int main() {
    int n = 4, k = 2;
    vector<vector<int>> times = {{2,1,1}, {2,3,1}, {3,4,1}};

    cout << networkDelayTime(times, n, k) << "\n";
}
```

### Step-by-Step Dry Run

```text
n = 4
start = 2

edges:
2 -> 1 cost 1
2 -> 3 cost 1
3 -> 4 cost 1
```

Initial:

```text
dist:
node:  1   2   3   4
dist: INF  0  INF INF

heap = [(0,2)]
```

```text
Step 1

heap before: [(0,2)]
pop        : node 2, dist 0

relax neighbors:
2 -> 1 cost 1
dist[1] = 1

2 -> 3 cost 1
dist[3] = 1

dist:
node:  1   2   3   4
dist:  1   0   1  INF

heap after: [(1,1), (1,3)]
```

```text
Step 2

heap before: [(1,1), (1,3)]
pop        : node 1, dist 1

node 1 has no outgoing edges

heap after: [(1,3)]
```

```text
Step 3

heap before: [(1,3)]
pop        : node 3, dist 1

relax:
3 -> 4 cost 1
dist[4] = dist[3] + 1 = 2

dist:
node:  1   2   3   4
dist:  1   0   1   2

heap after: [(2,4)]
```

```text
Step 4

pop node 4, dist 2
no outgoing edges

max distance = 2
answer = 2
```

### Attractive Note

```text
Dijkstra heap is a frontier of promises.
The smallest promise is trusted first.
```

---

# Phase CP2 — 0/1 Edge Alternative Awareness

## 14. Minimum Cost to Make Valid Path in Grid

### Problem Statement

Each grid cell has a direction.  
Moving in that direction costs `0`.  
Changing direction costs `1`.  
Find minimum cost from top-left to bottom-right.

### Input

```text
grid =
[
  [1,1,3],
  [3,2,2],
  [1,1,4]
]
```

Direction meaning:

```text
1 = right
2 = left
3 = down
4 = up
```

### Output

```text
0
```

### Pattern

```text
0/1 BFS using deque.
```

### Why This Pattern Works

Edge weights are only `0` or `1`.

```text
cost 0 → push_front
cost 1 → push_back
```

This keeps states ordered by distance without full Dijkstra.

### Result

```text
minimum cost = 0
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int minCost(vector<vector<int>>& grid) {
    int n = grid.size();
    int m = grid[0].size();

    vector<vector<int>> dist(n, vector<int>(m, 1e9));

    vector<int> dr = {0, 0, 0, 1, -1};
    vector<int> dc = {0, 1, -1, 0, 0};

    deque<pair<int,int>> dq;

    dist[0][0] = 0;
    dq.push_front({0,0});

    while (!dq.empty()) {
        auto [r, c] = dq.front();
        dq.pop_front();

        for (int dir = 1; dir <= 4; dir++) {
            int nr = r + dr[dir];
            int nc = c + dc[dir];

            if (nr < 0 || nr >= n || nc < 0 || nc >= m) continue;

            int cost = (grid[r][c] == dir ? 0 : 1);

            if (dist[r][c] + cost < dist[nr][nc]) {
                dist[nr][nc] = dist[r][c] + cost;

                if (cost == 0) {
                    dq.push_front({nr, nc});
                } else {
                    dq.push_back({nr, nc});
                }
            }
        }
    }

    return dist[n - 1][m - 1];
}

int main() {
    vector<vector<int>> grid = {
        {1,1,3},
        {3,2,2},
        {1,1,4}
    };

    cout << minCost(grid) << "\n";
}
```

### Step-by-Step Dry Run

```text
grid:
1 1 3
3 2 2
1 1 4

Start = (0,0)
End   = (2,2)

1 = right
2 = left
3 = down
4 = up
```

Best zero-cost path:

```text
(0,0) direction right → (0,1)
(0,1) direction right → (0,2)
(0,2) direction down  → (1,2)
(1,2) direction left  → (1,1)
(1,1) direction left  → (1,0)
(1,0) direction down  → (2,0)
(2,0) direction right → (2,1)
(2,1) direction right → (2,2)
```

```text
All moves followed cell direction.
Total cost = 0
answer = 0
```

### Attractive Note

```text
0/1 BFS is the bridge between BFS and Dijkstra.
Use it when edge costs are only 0 or 1.
```

---

# Phase CP3 — Lazy Deletion / Stale States

## 15. Sliding Window Median

### Problem Statement

Given `nums` and window size `k`, return median of each sliding window.

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

Median requires balanced halves.  
Sliding window requires deletion.

Heap cannot delete arbitrary values efficiently.  
So we mark deleted values in a map and remove them only when they reach heap top.

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

                if (delayed[x] == 0) delayed.erase(x);

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

                if (delayed[x] == 0) delayed.erase(x);

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
        if (k % 2 == 1) return small.top();

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

    auto ans = medianSlidingWindow(nums, k);

    for (double x : ans) cout << x << " ";
}
```

### Step-by-Step Dry Run

```text
nums = [1, 3, -1, -3, 5, 3, 6, 7]
k = 3

small = max heap lower half
large = min heap upper half

For odd k:
median = small.top()
```

```text
Initial window [1, 3, -1]

Insert 1:
small = [1]
large = []

Insert 3:
3 > small.top(1)
large = [3]

Insert -1:
-1 <= small.top(1)
small = [1, -1]

Window sorted: [-1, 1, 3]
median = 1

answer = [1]
```

```text
Slide:
remove 1
add -3

Before:
small = [1, -1]
large = [3]

Add -3:
goes to small

Erase 1:
delayed[1]++
1 is at small.top
prune 1

After prune:
small = [-1, -3]
large = [3]

Window [3, -1, -3]
sorted [-3, -1, 3]
median = -1

answer = [1, -1]
```

```text
Slide:
remove 3
add 5

Add 5:
goes to large

Erase 3:
delayed[3]++
3 is at large.top
prune 3

small = [-1, -3]
large = [5]

Window [-1, -3, 5]
median = -1

answer = [1, -1, -1]
```

```text
Remaining:
[-3,5,3] median = 3
[5,3,6] median = 5
[3,6,7] median = 6

answer = [1, -1, -1, 3, 5, 6]
```

### Attractive Note

```text
Lazy deletion:
Do not remove now.
Mark dead.
When it reaches the top, throw it away.
```

---

# Phase CP4 — Sweep Line + Heap

## 16. The Skyline Problem

### Problem Statement

Given buildings `[left, right, height]`, return the skyline key points.

### Input

```text
buildings = [
  [2,9,10],
  [3,7,15],
  [5,12,12],
  [15,20,10],
  [19,24,8]
]
```

### Output

```text
[[2,10], [3,15], [7,12], [12,0], [15,10], [20,8], [24,0]]
```

### Pattern

```text
Sweep line + max heap of active buildings.
```

### Why This Pattern Works

At each x-coordinate, skyline height is the maximum height among active buildings.

Heap stores active buildings by height.

Remove expired buildings where `right <= current x`.

### Result

```text
skyline changes when current max height changes
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> getSkyline(vector<vector<int>>& buildings) {
    vector<pair<int,int>> events;

    for (auto &b : buildings) {
        int l = b[0], r = b[1], h = b[2];

        events.push_back({l, -h});
        events.push_back({r, h});
    }

    sort(events.begin(), events.end());

    multiset<int> heights;
    heights.insert(0);

    int prevMax = 0;
    vector<vector<int>> ans;

    for (auto [x, h] : events) {
        if (h < 0) {
            heights.insert(-h);
        } else {
            heights.erase(heights.find(h));
        }

        int curMax = *heights.rbegin();

        if (curMax != prevMax) {
            ans.push_back({x, curMax});
            prevMax = curMax;
        }
    }

    return ans;
}

int main() {
    vector<vector<int>> buildings = {
        {2,9,10},
        {3,7,15},
        {5,12,12},
        {15,20,10},
        {19,24,8}
    };

    auto ans = getSkyline(buildings);

    for (auto &p : ans) {
        cout << "[" << p[0] << "," << p[1] << "] ";
    }
}
```

### Step-by-Step Dry Run

```text
buildings:
[2,9,10]
[3,7,15]
[5,12,12]
[15,20,10]
[19,24,8]
```

Events:

```text
x = 2  start height 10
x = 3  start height 15
x = 5  start height 12
x = 7  end height 15
x = 9  end height 10
x = 12 end height 12
x = 15 start height 10
x = 19 start height 8
x = 20 end height 10
x = 24 end height 8
```

Active heights:

```text
x = 2
before: [0]
add 10
after : [0,10]
max changes 0 -> 10
add key point [2,10]
```

```text
x = 3
before: [0,10]
add 15
after : [0,10,15]
max changes 10 -> 15
add key point [3,15]
```

```text
x = 5
before: [0,10,15]
add 12
after : [0,10,12,15]
max stays 15
no key point
```

```text
x = 7
remove 15
active: [0,10,12]
max changes 15 -> 12
add key point [7,12]
```

```text
x = 12
remove 12
active: [0]
max changes 12 -> 0
add key point [12,0]
```

```text
Continue:
x=15 add 10 -> [15,10]
x=20 remove 10, max becomes 8 -> [20,8]
x=24 remove 8, max becomes 0 -> [24,0]
```

### Attractive Note

```text
Sweep line moves left to right.
Heap/multiset answers:
What is the tallest active building right now?
```

---

# Phase CP5 — Heap + Binary Search / Ordered Generation

## 17. Kth Smallest Element in a Sorted Matrix

### Problem Statement

Given an `n x n` matrix where each row and column is sorted, return the kth smallest element.

### Input

```text
matrix =
[
  [1,  5,  9],
  [10, 11, 13],
  [12, 13, 15]
]
k = 8
```

### Output

```text
13
```

### Pattern

```text
Min heap ordered generation from sorted rows.
```

### Why This Pattern Works

Each row is sorted.

Treat each row as a sorted stream.  
Use min heap to repeatedly take the next smallest available value.

### Result

```text
8th smallest = 13
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int kthSmallest(vector<vector<int>>& matrix, int k) {
    int n = matrix.size();

    using T = tuple<int,int,int>; // value, row, col

    priority_queue<T, vector<T>, greater<T>> pq;

    for (int r = 0; r < n; r++) {
        pq.push({matrix[r][0], r, 0});
    }

    int ans = -1;

    while (k--) {
        auto [val, r, c] = pq.top();
        pq.pop();

        ans = val;

        if (c + 1 < n) {
            pq.push({matrix[r][c + 1], r, c + 1});
        }
    }

    return ans;
}

int main() {
    vector<vector<int>> matrix = {
        {1, 5, 9},
        {10, 11, 13},
        {12, 13, 15}
    };

    int k = 8;

    cout << kthSmallest(matrix, k) << "\n";
}
```

### Step-by-Step Dry Run

```text
matrix:
row0: 1   5   9
row1: 10  11  13
row2: 12  13  15

k = 8
```

Initial heap:

```text
[value,row,col]

[1,0,0]
[10,1,0]
[12,2,0]
```

```text
pop 1st:
pop [1,0,0]
ans = 1
push next from row0 = [5,0,1]

heap: [5,10,12]
```

```text
pop 2nd:
pop [5,0,1]
ans = 5
push [9,0,2]

heap: [9,10,12]
```

```text
pop 3rd:
pop [9,0,2]
ans = 9
row0 finished

heap: [10,12]
```

```text
pop 4th:
pop [10,1,0]
ans = 10
push [11,1,1]

heap: [11,12]
```

```text
pop 5th:
pop [11,1,1]
ans = 11
push [13,1,2]

heap: [12,13]
```

```text
pop 6th:
pop [12,2,0]
ans = 12
push [13,2,1]

heap: [13,13]
```

```text
pop 7th:
pop [13,1,2]
ans = 13

heap: [13]
```

```text
pop 8th:
pop [13,2,1]
ans = 13

answer = 13
```

### Attractive Note

```text
Sorted matrix can be seen as multiple sorted streams.
Heap generates values in sorted order without flattening everything.
```

---

# Heap Pattern Decision Table

| Signal | Pattern |
|---|---|
| kth largest | min heap size K |
| kth smallest | max heap size K or ordered min heap |
| top k frequent | frequency + min heap |
| k closest | max heap size K |
| stream kth largest | min heap size K |
| running median | two heaps |
| merge k sorted lists | min heap of heads |
| k smallest pairs | k-way merge |
| meeting rooms | min heap by end time |
| cooldown tasks | max heap by frequency |
| connect sticks | min heap combine smallest |
| choose best affordable project | sort + max heap |
| positive weighted shortest path | Dijkstra min heap |
| 0/1 edge weights | deque 0/1 BFS |
| sliding deletion | lazy deletion |
| active intervals max | sweep line + heap/multiset |

---

# Must-Solve Order

## FAANG First

```text
1. Kth Largest Element in an Array
2. Top K Frequent Elements
3. K Closest Points to Origin
4. Kth Largest Element in a Stream
5. Find Median from Data Stream
6. Merge K Sorted Lists
7. Meeting Rooms II
8. Task Scheduler
9. Minimum Cost to Connect Sticks
10. IPO
```

## CP Advanced Later

```text
11. Network Delay Time / Dijkstra
12. Minimum Cost Valid Path / 0-1 BFS
13. Sliding Window Median
14. Skyline Problem
15. Kth Smallest in Sorted Matrix
```

---

# One-Line Mental Model

```text
Priority queue is the right tool when the current best candidate must be repeatedly selected.
```

---

# Final Revision Notes

```text
Top K largest
→ min heap size K

Top K smallest
→ max heap size K

Repeated largest
→ max heap

Repeated smallest
→ min heap

Streaming median
→ two heaps

Merge sorted sources
→ min heap

Scheduling rooms
→ min heap end time

Cooldown tasks
→ max heap frequency

Best affordable option
→ sort + max heap

Shortest weighted path
→ min heap Dijkstra

0/1 weights
→ deque, not heap

Delete old heap elements
→ lazy deletion

Active interval maximum
→ sweep line + heap/multiset
```
