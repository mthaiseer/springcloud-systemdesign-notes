# Greedy Algorithms — Visual Reference

> **Style:** visual-first, short explanations, C++ primary, Java only where useful.

---

## Clickable Index

1. [Core Idea](#1-core-idea)
2. [Greedy Recognition Checklist](#2-greedy-recognition-checklist)
3. [Beginner Patterns](#3-beginner-patterns)
   - [Sort + Pick](#31-sort--pick)
   - [Track Best So Far](#32-track-best-so-far)
   - [Earliest Finish Interval](#33-earliest-finish-interval)
4. [Intermediate / FAANG Patterns](#4-intermediate--faang-patterns)
   - [Jump Reachability](#41-jump-reachability)
   - [Gas Station Reset](#42-gas-station-reset)
   - [Heap Greedy](#43-heap-greedy)
   - [Two-Pass Greedy](#44-two-pass-greedy)
5. [Advanced Patterns](#5-advanced-patterns)
   - [Exchange Argument](#51-exchange-argument)
   - [Greedy + Binary Search](#52-greedy--binary-search)
   - [Greedy + DSU](#53-greedy--dsu)
   - [MST: Kruskal](#54-mst-kruskal)
6. [Greedy vs DP](#6-greedy-vs-dp)
7. [Pattern Cheat Sheet](#7-pattern-cheat-sheet)
8. [C++ Template Bank](#8-c-template-bank)
9. [Java Mini-Templates](#9-java-mini-templates)
10. [Practice Roadmap](#10-practice-roadmap)

---

# 1. Core Idea

Greedy means:

```text
At each step:
    choose the best local option
    never undo that choice
```

Visual:

```text
Start
  |
  v
Choose best NOW
  |
  v
Problem becomes smaller
  |
  v
Choose best NOW again
  |
  v
Answer
```

Greedy works when:

| Property | Meaning |
|---|---|
| Greedy choice property | Local best choice can be part of global best answer |
| Optimal substructure | After one choice, remaining problem is still optimal |

---

# 2. Greedy Recognition Checklist

Ask these before coding:

| Question | If YES |
|---|---|
| Can sorting simplify choices? | Try greedy |
| Can I choose earliest/latest/smallest/largest? | Try greedy |
| Does one decision not need to be changed later? | Greedy likely |
| Can I prove by swapping choices? | Greedy likely |
| Does current choice affect future deeply? | Maybe DP |

Decision flow:

```text
Problem asks min/max/possible?
        |
        v
Can sort / choose local best?
        |
    +---+---+
    |       |
   YES      NO
    |       |
Try greedy  Think DP / graph / search
    |
Can prove no future regret?
    |
 +--+--+
 |     |
YES    NO
 |     |
Code   Find counterexample / use DP
```

---

# 3. Beginner Patterns

## 3.1 Sort + Pick

### Form

```text
Sort items by useful value
Pick best valid item one by one
```

### Visual

```text
Before sort:
[7, 2, 5, 1, 9]

After sort:
[1, 2, 5, 7, 9]

Pick direction:
smallest -> biggest
or
biggest -> smallest
```

### Use When

| Problem clue | Sort by |
|---|---|
| Min cost | Cost ascending |
| Max profit | Profit descending |
| Assign smallest resource | Size ascending |

### C++ Example: Assign Cookies

```cpp
#include <bits/stdc++.h>
using namespace std;

int findContentChildren(vector<int>& greed, vector<int>& cookies) {
    sort(greed.begin(), greed.end());
    sort(cookies.begin(), cookies.end());

    int child = 0, cookie = 0;

    while (child < greed.size() && cookie < cookies.size()) {
        if (cookies[cookie] >= greed[child]) {
            child++;
        }
        cookie++;
    }

    return child;
}
```

---

## 3.2 Track Best So Far

### Form

```text
Keep the best previous value
Use it to calculate current answer
```

### Visual: Buy Low, Sell High

```text
prices:     7   1   5   3   6   4
minSoFar:  7   1   1   1   1   1
profit:    0   0   4   2   5   3
best:      0   0   4   4   5   5
```

### C++ Example: Best Time to Buy/Sell Stock

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxProfit(vector<int>& prices) {
    int minPrice = INT_MAX;
    int bestProfit = 0;

    for (int price : prices) {
        minPrice = min(minPrice, price);
        bestProfit = max(bestProfit, price - minPrice);
    }

    return bestProfit;
}
```

---

## 3.3 Earliest Finish Interval

### Form

```text
Sort intervals by end time
Pick the interval that ends earliest
```

### Visual

```text
Intervals:
A: |------|
B:   |--|
C:      |---|
D:          |--|

Sorted by end:
B ends first -> pick B
C overlaps? maybe skip
D compatible -> pick D
```

### Why end time?

```text
Earlier finish = more room for future intervals
```

### C++ Example: Max Non-Overlapping Intervals

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxNonOverlapping(vector<pair<int,int>>& intervals) {
    sort(intervals.begin(), intervals.end(), [](auto& a, auto& b) {
        return a.second < b.second;
    });

    int count = 0;
    int lastEnd = INT_MIN;

    for (auto [start, end] : intervals) {
        if (start >= lastEnd) {
            count++;
            lastEnd = end;
        }
    }

    return count;
}
```

---

# 4. Intermediate / FAANG Patterns

## 4.1 Jump Reachability

### Problem Form

```text
From index i, you can jump nums[i] steps.
Can you reach the end?
```

### Visual

```text
nums:   [2, 3, 1, 1, 4]
index:   0  1  2  3  4
reach:   2 -> 4 -> success
```

### Tactic

```text
Track farthest reachable index
If current index > farthest, fail
```

### C++ Example: Jump Game

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canJump(vector<int>& nums) {
    int farthest = 0;

    for (int i = 0; i < nums.size(); i++) {
        if (i > farthest) return false;
        farthest = max(farthest, i + nums[i]);
    }

    return true;
}
```

---

## 4.2 Gas Station Reset

### Key Idea

If tank becomes negative at station `i`, no station from current start to `i` can be answer.

### Visual

```text
start -----> i
 tank < 0

All stations between start and i fail.
Reset start to i + 1.
```

### C++ Example

```cpp
#include <bits/stdc++.h>
using namespace std;

int canCompleteCircuit(vector<int>& gas, vector<int>& cost) {
    int total = 0;
    int tank = 0;
    int start = 0;

    for (int i = 0; i < gas.size(); i++) {
        int diff = gas[i] - cost[i];
        total += diff;
        tank += diff;

        if (tank < 0) {
            start = i + 1;
            tank = 0;
        }
    }

    return total >= 0 ? start : -1;
}
```

---

## 4.3 Heap Greedy

### Use When

Choices keep changing dynamically.

| Problem type | Heap type |
|---|---|
| Always need smallest | Min-heap |
| Always need largest | Max-heap |
| Need most frequent | Max-heap by count |

### Visual

```text
Available choices:
[task A: 4], [task B: 2], [task C: 7]

Max heap picks:
C first -> A -> B
```

### C++ Example: Connect Ropes Minimum Cost

```cpp
#include <bits/stdc++.h>
using namespace std;

int minCostToConnectRopes(vector<int>& ropes) {
    priority_queue<int, vector<int>, greater<int>> pq;

    for (int rope : ropes) pq.push(rope);

    int cost = 0;

    while (pq.size() > 1) {
        int a = pq.top(); pq.pop();
        int b = pq.top(); pq.pop();

        int merged = a + b;
        cost += merged;
        pq.push(merged);
    }

    return cost;
}
```

### Java PriorityQueue Version

```java
import java.util.*;

class Solution {
    public int minCostToConnectRopes(int[] ropes) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();

        for (int rope : ropes) pq.add(rope);

        int cost = 0;

        while (pq.size() > 1) {
            int a = pq.poll();
            int b = pq.poll();
            int merged = a + b;
            cost += merged;
            pq.add(merged);
        }

        return cost;
    }
}
```

---

## 4.4 Two-Pass Greedy

### Use When

Constraints come from both left and right.

### Visual: Candy Problem

```text
ratings:  1  2  2
left:     1  2  1
right:    1  1  1
answer:   max(left, right)
          1  2  1
```

### C++ Example: Candy

```cpp
#include <bits/stdc++.h>
using namespace std;

int candy(vector<int>& ratings) {
    int n = ratings.size();
    vector<int> candies(n, 1);

    for (int i = 1; i < n; i++) {
        if (ratings[i] > ratings[i - 1]) {
            candies[i] = candies[i - 1] + 1;
        }
    }

    for (int i = n - 2; i >= 0; i--) {
        if (ratings[i] > ratings[i + 1]) {
            candies[i] = max(candies[i], candies[i + 1] + 1);
        }
    }

    return accumulate(candies.begin(), candies.end(), 0);
}
```

---

# 5. Advanced Patterns

## 5.1 Exchange Argument

### Meaning

Prove greedy is correct by showing:

```text
Any optimal answer can be changed into greedy answer
without making it worse.
```

### Visual

```text
Optimal solution:
[wrong first choice] + rest

Swap first choice with greedy choice

Greedy solution:
[best first choice] + rest

Result is still optimal or better.
```

### Common Uses

| Problem | Greedy choice |
|---|---|
| Activity selection | Earliest ending interval |
| Huffman coding | Merge two smallest |
| Kruskal MST | Smallest safe edge |

---

## 5.2 Greedy + Binary Search

### Form

```text
Binary search the answer
Use greedy to check if answer is possible
```

### Visual

```text
Possible answers:
1 2 3 4 5 6 7 8 9 10
F F F F T T T T T T
        ^
      first true
```

### C++ Template

```cpp
#include <bits/stdc++.h>
using namespace std;

bool can(int x) {
    // Greedy feasibility check
    return true;
}

int binarySearchAnswer(int low, int high) {
    int ans = high;

    while (low <= high) {
        int mid = low + (high - low) / 2;

        if (can(mid)) {
            ans = mid;
            high = mid - 1;
        } else {
            low = mid + 1;
        }
    }

    return ans;
}
```

---

## 5.3 Greedy + DSU

### Use When

You greedily connect or group things.

### Visual

```text
Before union:
1     2     3     4

union(1,2), union(3,4)

After:
1---2     3---4

union(2,3)

1---2---3---4
```

### DSU C++ Template

```cpp
#include <bits/stdc++.h>
using namespace std;

class DSU {
public:
    vector<int> parent, size;

    DSU(int n) {
        parent.resize(n);
        size.assign(n, 1);
        iota(parent.begin(), parent.end(), 0);
    }

    int find(int x) {
        if (parent[x] == x) return x;
        return parent[x] = find(parent[x]);
    }

    bool unite(int a, int b) {
        a = find(a);
        b = find(b);

        if (a == b) return false;

        if (size[a] < size[b]) swap(a, b);
        parent[b] = a;
        size[a] += size[b];

        return true;
    }
};
```

---

## 5.4 MST: Kruskal

### Form

```text
Sort edges by weight
Pick smallest edge that does not create cycle
```

### Visual

```text
Edges sorted:
(1-2, 1)   pick
(2-3, 2)   pick
(1-3, 3)   skip, cycle
(3-4, 4)   pick
```

### C++ Example

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Edge {
    int u, v, w;
};

class DSU {
public:
    vector<int> parent, size;

    DSU(int n) {
        parent.resize(n);
        size.assign(n, 1);
        iota(parent.begin(), parent.end(), 0);
    }

    int find(int x) {
        return parent[x] == x ? x : parent[x] = find(parent[x]);
    }

    bool unite(int a, int b) {
        a = find(a);
        b = find(b);
        if (a == b) return false;
        if (size[a] < size[b]) swap(a, b);
        parent[b] = a;
        size[a] += size[b];
        return true;
    }
};

int kruskal(int n, vector<Edge>& edges) {
    sort(edges.begin(), edges.end(), [](Edge& a, Edge& b) {
        return a.w < b.w;
    });

    DSU dsu(n);
    int mstCost = 0;

    for (auto& edge : edges) {
        if (dsu.unite(edge.u, edge.v)) {
            mstCost += edge.w;
        }
    }

    return mstCost;
}
```

---

# 6. Greedy vs DP

| Situation | Greedy | DP |
|---|---|---|
| Local choice always safe | Yes | Maybe not needed |
| Need to try many choices | No | Yes |
| Future depends strongly on current choice | Risky | Better |
| Can prove by exchange argument | Yes | Not required |
| Need max/min with overlapping subproblems | Usually no | Yes |

### Visual Difference

Greedy:

```text
choice -> choice -> choice -> answer
```

DP:

```text
          choice A
        /          
state -> choice B   -> best answer
        \          /
          choice C
```

---

# 7. Pattern Cheat Sheet

| Pattern | Key Move | Data Structure | Level |
|---|---|---|---|
| Sort + pick | Sort then scan | Array | Beginner |
| Best so far | Track min/max | Variables | Beginner |
| Interval scheduling | Sort by end | Array | Beginner |
| Jump reachability | Track farthest | Variable | FAANG |
| Gas station | Reset start | Variables | FAANG |
| Heap greedy | Pick dynamic best | Priority queue | FAANG |
| Two-pass greedy | Left + right constraints | Array | FAANG |
| Binary search + greedy | Check feasibility | Function | Advanced |
| DSU greedy | Merge safely | Union-Find | Advanced |
| MST Kruskal | Pick smallest safe edge | DSU | Advanced |

---

# 8. C++ Template Bank

## Sort by Custom Rule

```cpp
sort(items.begin(), items.end(), [](auto& a, auto& b) {
    return a.key < b.key;
});
```

## Min Heap

```cpp
priority_queue<int, vector<int>, greater<int>> minHeap;
```

## Max Heap

```cpp
priority_queue<int> maxHeap;
```

## Pair Min Heap

```cpp
priority_queue<pair<int,int>, vector<pair<int,int>>, greater<pair<int,int>>> pq;
```

## Interval Sort by End

```cpp
sort(intervals.begin(), intervals.end(), [](auto& a, auto& b) {
    return a[1] < b[1];
});
```

## One-Pass Greedy Skeleton

```cpp
int answer = 0;
int best = 0;

for (int x : nums) {
    // update best local choice
    best = max(best, x);

    // update answer
    answer = max(answer, best);
}
```

## Feasibility Check Skeleton

```cpp
bool can(vector<int>& nums, int limit) {
    int used = 1;
    int current = 0;

    for (int x : nums) {
        if (current + x <= limit) {
            current += x;
        } else {
            used++;
            current = x;
        }
    }

    return used <= allowed;
}
```

---

# 9. Java Mini-Templates

## Java Sort Array

```java
Arrays.sort(nums);
```

## Java Sort Intervals by End

```java
Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));
```

## Java Min Heap

```java
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
```

## Java Max Heap

```java
PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);
```

## Java One-Pass Greedy

```java
int best = 0;
int answer = 0;

for (int x : nums) {
    best = Math.max(best, x);
    answer = Math.max(answer, best);
}
```

---

# 10. Practice Roadmap

## Beginner

| Problem | Pattern |
|---|---|
| Assign Cookies | Sort + pick |
| Best Time to Buy/Sell Stock | Best so far |
| Activity Selection | Interval by end |
| Lemonade Change | Local simulation |

## FAANG / Interview

| Problem | Pattern |
|---|---|
| Jump Game | Farthest reach |
| Jump Game II | Current range expansion |
| Gas Station | Reset start |
| Meeting Rooms II | Min heap |
| Partition Labels | Last occurrence greedy |
| Candy | Two-pass greedy |
| Non-overlapping Intervals | Sort by end |

## Advanced / Competitive

| Problem | Pattern |
|---|---|
| Huffman Coding | Merge two smallest |
| Kruskal MST | DSU + greedy |
| Aggressive Cows | Binary search + greedy |
| Job Sequencing with Deadlines | DSU / sorting |
| Minimum Platforms | Sorting / sweep line |

---

# Final Memory Hook

```text
Greedy = choose now, prove no regret.

Sort if static.
Heap if dynamic.
DSU if merging.
Binary search if answer is hidden.
DP if choices must be compared deeply.
```
