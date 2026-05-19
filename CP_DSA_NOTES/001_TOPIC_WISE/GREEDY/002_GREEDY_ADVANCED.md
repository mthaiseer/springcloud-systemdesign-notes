# 011 Greedy Algorithms — Phase Wise CP + FAANG Master Note

> Goal: cover greedy patterns for **Codeforces A/B/C/D**, **LeetCode/FAANG interviews**, and advanced competitive programming.
>
> Main language: **C++**.
>
> Style: pattern → problem → input/output → idea → code → dry run table.

---

# Clickable Index

## Core

0. [How To Use This Note](#0-how-to-use-this-note)
1. [Greedy Core Idea](#1-greedy-core-idea)
2. [Greedy Recognition Decision Tree](#2-greedy-recognition-decision-tree)
3. [Phase Wise Roadmap](#3-phase-wise-roadmap)
4. [Pattern Summary Table](#4-pattern-summary-table)
5. [Proof Toolkit](#5-proof-toolkit)

## Phase 1 — Beginner Greedy Foundation

6. [Pattern 1: Sort + Pick Smallest/Largest](#6-pattern-1-sort--pick-smallestlargest)
   - [Problem 1: Assign Cookies](#problem-1-assign-cookies)
7. [Pattern 2: Running Min/Max](#7-pattern-2-running-minmax)
   - [Problem 2: Best Time to Buy and Sell Stock](#problem-2-best-time-to-buy-and-sell-stock)
8. [Pattern 3: Local Simulation Greedy](#8-pattern-3-local-simulation-greedy)
   - [Problem 3: Lemonade Change](#problem-3-lemonade-change)
9. [Pattern 4: Earliest Finish Interval](#9-pattern-4-earliest-finish-interval)
   - [Problem 4: Activity Selection](#problem-4-activity-selection)

## Phase 2 — FAANG Interview Greedy

10. [Pattern 5: Forward Reachability](#10-pattern-5-forward-reachability)
    - [Problem 5: Jump Game](#problem-5-jump-game)
11. [Pattern 6: Reset When Invalid](#11-pattern-6-reset-when-invalid)
    - [Problem 6: Gas Station](#problem-6-gas-station)
12. [Pattern 7: Boundary Expansion](#12-pattern-7-boundary-expansion)
    - [Problem 7: Partition Labels](#problem-7-partition-labels)
13. [Pattern 8: Two Pointers Greedy](#13-pattern-8-two-pointers-greedy)
    - [Problem 8: Boats to Save People](#problem-8-boats-to-save-people)
14. [Pattern 9: Heap Greedy](#14-pattern-9-heap-greedy)
    - [Problem 9: Meeting Rooms II](#problem-9-meeting-rooms-ii)
15. [Pattern 10: Frequency Greedy](#15-pattern-10-frequency-greedy)
    - [Problem 10: Task Scheduler](#problem-10-task-scheduler)
16. [Pattern 11: Stack Greedy](#16-pattern-11-stack-greedy)
    - [Problem 11: Remove K Digits](#problem-11-remove-k-digits)
17. [Pattern 12: Replacement Greedy](#17-pattern-12-replacement-greedy)
    - [Problem 12: Furthest Building You Can Reach](#problem-12-furthest-building-you-can-reach)

## Phase 3 — Codeforces A/B/C Greedy

18. [Pattern 13: Sorting + Prefix/Suffix Observation](#18-pattern-13-sorting--prefixsuffix-observation)
    - [Problem 13: Maximum Units on a Truck](#problem-13-maximum-units-on-a-truck)
19. [Pattern 14: Parity / Modulo Greedy](#19-pattern-14-parity--modulo-greedy)
    - [Problem 14: Make Array Even](#problem-14-make-array-even)
20. [Pattern 15: String Greedy](#20-pattern-15-string-greedy)
    - [Problem 15: Reorganize String](#problem-15-reorganize-string)
21. [Pattern 16: Constructive Greedy](#21-pattern-16-constructive-greedy)
    - [Problem 16: Construct Beautiful Permutation](#problem-16-construct-beautiful-permutation)
22. [Pattern 17: Greedy With Invariant](#22-pattern-17-greedy-with-invariant)
    - [Problem 17: Candy](#problem-17-candy)

## Phase 4 — Codeforces D / Advanced Greedy

23. [Pattern 18: Greedy + Binary Search on Answer](#23-pattern-18-greedy--binary-search-on-answer)
    - [Problem 18: Aggressive Cows](#problem-18-aggressive-cows)
24. [Pattern 19: Greedy + DSU](#24-pattern-19-greedy--dsu)
    - [Problem 19: Kruskal MST](#problem-19-kruskal-mst)
25. [Pattern 20: Huffman / Merge Two Smallest](#25-pattern-20-huffman--merge-two-smallest)
    - [Problem 20: Minimum Cost to Connect Ropes](#problem-20-minimum-cost-to-connect-ropes)
26. [Pattern 21: Job Sequencing With Deadlines](#26-pattern-21-job-sequencing-with-deadlines)
    - [Problem 21: Job Sequencing](#problem-21-job-sequencing)
27. [Pattern 22: Ordered Set / Multiset Greedy](#27-pattern-22-ordered-set--multiset-greedy)
    - [Problem 22: Concert Tickets](#problem-22-concert-tickets)

## Final Sections

28. [Greedy vs DP Decision Table](#28-greedy-vs-dp-decision-table)
29. [Common Counterexamples](#29-common-counterexamples)
30. [Reusable C++ Templates](#30-reusable-c-templates)
31. [Practice Ladder](#31-practice-ladder)
32. [Final Cheat Sheet](#32-final-cheat-sheet)

---

# 0. How To Use This Note

For each greedy problem, ask:

| Step | Question |
|---:|---|
| 1 | What is the objective? Max, min, possible, count, lexicographically smallest? |
| 2 | Can I sort or scan once? |
| 3 | What is the local choice? |
| 4 | Why does this local choice not hurt the future? |
| 5 | Can I prove by exchange, staying ahead, cut property, or contradiction? |
| 6 | Can I find a counterexample? If yes, greedy is wrong. |

---

# 1. Greedy Core Idea

Greedy means making the **best safe choice now** and never undoing it.

```text
Current state -> choose safe best option -> reduce problem -> repeat
```

Greedy works only when the local decision can be proven safe.

---

# 2. Greedy Recognition Decision Tree

```text
Problem asks max/min/possible?
|
+-- Can sorting create a useful order?
|   |
|   +-- Sort by value / end / profit / deadline / size
|
+-- Need best available item dynamically?
|   |
|   +-- Use heap / multiset
|
+-- Need to know if answer X works?
|   |
|   +-- Binary search answer + greedy check
|
+-- Need to remove bad previous choices?
|   |
|   +-- Stack greedy / replacement greedy
|
+-- Need graph cheapest safe edge?
    |
    +-- DSU / MST greedy
```

---

# 3. Phase Wise Roadmap

| Phase | Target | Patterns | Level |
|---|---|---|---|
| Phase 1 | Foundation | Sort, running min/max, simulation, interval | CF A/B, LC Easy |
| Phase 2 | FAANG | Reachability, reset, boundary, heap, stack, two pointers | LC Medium |
| Phase 3 | CF A/B/C | Constructive, parity, string, prefix/suffix, invariant | CF 800–1400 |
| Phase 4 | CF D / Advanced | Binary search + greedy, DSU, multiset, deadlines | CF 1500–1900+ |

---

# 4. Pattern Summary Table

| Pattern | Core Choice | Data Structure | Common Signal |
|---|---|---|---|
| Sort + Pick | Smallest/biggest valid | Array | Match items |
| Running Min/Max | Best seen so far | Variables | Profit, distance |
| Local Simulation | Spend smallest resource | Counters | Change/payment |
| Interval Greedy | Earliest ending | Sort | Non-overlap |
| Reachability | Farthest index | Variable | Can reach |
| Reset Greedy | Restart after failure | Variable | Prefix invalid |
| Boundary Expansion | Extend segment end | Last index map | Partition/string |
| Two Pointers | Pair lightest/heaviest | Sort + pointers | Pairing |
| Heap | Best available now | Priority queue | Dynamic choice |
| Frequency | Use most frequent first | Count/heap | Scheduling/string |
| Stack Greedy | Remove worse previous | Stack/string | Lexicographic/min number |
| Replacement | Use cheap resource first, replace later | Heap | Ladders/bricks |
| Constructive | Build answer by rule | Array/string | Need construct |
| Invariant | Maintain property always | Array | Local constraints |
| Binary Search + Greedy | Check feasibility | Sort/check | Maximize min / minimize max |
| DSU Greedy | Add safe edge/slot | DSU | MST/deadline slots |
| Multiset Greedy | Best <= x / >= x | multiset | Tickets, matching |

---

# 5. Proof Toolkit

| Proof | Meaning | Used In |
|---|---|---|
| Exchange Argument | Replace optimal choice with greedy choice without hurting answer | Intervals, sorting |
| Staying Ahead | Greedy state is always at least as good | Jump Game |
| Contradiction | If greedy fails, no skipped option could work | Gas Station |
| Cut Property | Cheapest safe edge across a cut is valid | MST |
| Invariant | Property remains true after every step | Candy, constructive |

---

# 6. Pattern 1: Sort + Pick Smallest/Largest

## When to Use

Use when you need to match small things to small requirements or pick maximum value first.

## Problem 1: Assign Cookies

### Problem Statement

Each child has a greed value. Each cookie has a size. A child is content if cookie size >= greed. Maximize content children.

### Input

```text
greed = [1,2,3]
cookies = [1,1,2]
```

### Output

```text
2
```

### Idea

Sort both arrays. Give the smallest possible cookie to the least greedy child.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int findContentChildren(vector<int>& greed, vector<int>& cookies) {
    sort(greed.begin(), greed.end());
    sort(cookies.begin(), cookies.end());

    int child = 0;
    int cookie = 0;

    while (child < (int)greed.size() && cookie < (int)cookies.size()) {
        if (cookies[cookie] >= greed[child]) {
            child++;
        }
        cookie++;
    }

    return child;
}
```

### Index-by-Index Dry Run Table

| Step | child index | cookie index | Need | Cookie | Action | Content Count |
|---:|---:|---:|---:|---:|---|---:|
| 1 | 0 | 0 | 1 | 1 | Assign | 1 |
| 2 | 1 | 1 | 2 | 1 | Too small, skip cookie | 1 |
| 3 | 1 | 2 | 2 | 2 | Assign | 2 |

---

# 7. Pattern 2: Running Min/Max

## When to Use

Use when current answer depends on best previous value.

## Problem 2: Best Time to Buy and Sell Stock

### Problem Statement

Given stock prices, choose one buy day and one sell day after it to maximize profit.

### Input

```text
prices = [7,1,5,3,6,4]
```

### Output

```text
5
```

### Idea

Track minimum price so far. At each day, calculate current profit.

### C++ Code

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

### Index-by-Index Dry Run Table

| Day | Price | Min So Far | Profit Today | Best Profit |
|---:|---:|---:|---:|---:|
| 0 | 7 | 7 | 0 | 0 |
| 1 | 1 | 1 | 0 | 0 |
| 2 | 5 | 1 | 4 | 4 |
| 3 | 3 | 1 | 2 | 4 |
| 4 | 6 | 1 | 5 | 5 |
| 5 | 4 | 1 | 3 | 5 |

---

# 8. Pattern 3: Local Simulation Greedy

## When to Use

Use when every step has fixed rules and you should spend the smallest/cheapest resource first.

## Problem 3: Lemonade Change

### Problem Statement

Each lemonade costs 5. Customers pay using 5, 10, or 20. Return true if you can give correct change to all customers.

### Input

```text
bills = [5,5,5,10,20]
```

### Output

```text
true
```

### Idea

Always keep counts of 5 and 10. For 20, prefer giving 10+5 because it preserves more 5s.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool lemonadeChange(vector<int>& bills) {
    int five = 0, ten = 0;

    for (int bill : bills) {
        if (bill == 5) {
            five++;
        } else if (bill == 10) {
            if (five == 0) return false;
            five--;
            ten++;
        } else {
            if (ten > 0 && five > 0) {
                ten--;
                five--;
            } else if (five >= 3) {
                five -= 3;
            } else {
                return false;
            }
        }
    }

    return true;
}
```

### Index-by-Index Dry Run Table

| i | Bill | five before | ten before | Action | five after | ten after |
|---:|---:|---:|---:|---|---:|---:|
| 0 | 5 | 0 | 0 | Take 5 | 1 | 0 |
| 1 | 5 | 1 | 0 | Take 5 | 2 | 0 |
| 2 | 5 | 2 | 0 | Take 5 | 3 | 0 |
| 3 | 10 | 3 | 0 | Give 5 | 2 | 1 |
| 4 | 20 | 2 | 1 | Give 10+5 | 1 | 0 |

---

# 9. Pattern 4: Earliest Finish Interval

## When to Use

Use when you need maximum number of non-overlapping intervals.

## Problem 4: Activity Selection

### Problem Statement

Given intervals, choose maximum number of non-overlapping intervals.

### Input

```text
intervals = [[1,3],[2,5],[4,7],[6,9]]
```

### Output

```text
2
```

### Idea

Sort by end time. Pick interval with earliest end because it leaves maximum room for future.

### C++ Code

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

### Index-by-Index Dry Run Table

| Step | Interval | lastEnd before | Condition | Action | Count | lastEnd after |
|---:|---|---:|---|---|---:|---:|
| 1 | [1,3] | -inf | 1 >= -inf | Pick | 1 | 3 |
| 2 | [2,5] | 3 | 2 >= 3 false | Skip | 1 | 3 |
| 3 | [4,7] | 3 | 4 >= 3 | Pick | 2 | 7 |
| 4 | [6,9] | 7 | 6 >= 7 false | Skip | 2 | 7 |

---

# 10. Pattern 5: Forward Reachability

## When to Use

Use when you only need to know whether a position is reachable, not the exact path.

## Problem 5: Jump Game

### Problem Statement

Given nums[i] = maximum jump length from index i. Can you reach the last index?

### Input

```text
nums = [2,3,1,1,4]
```

### Output

```text
true
```

### Idea

Track farthest reachable index. If current index is greater than farthest, fail.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canJump(vector<int>& nums) {
    int farthest = 0;

    for (int i = 0; i < (int)nums.size(); i++) {
        if (i > farthest) return false;
        farthest = max(farthest, i + nums[i]);
    }

    return true;
}
```

### Index-by-Index Dry Run Table

| i | nums[i] | farthest before | i > farthest? | farthest after |
|---:|---:|---:|---|---:|
| 0 | 2 | 0 | No | 2 |
| 1 | 3 | 2 | No | 4 |
| 2 | 1 | 4 | No | 4 |
| 3 | 1 | 4 | No | 4 |
| 4 | 4 | 4 | No | 8 |

---

# 11. Pattern 6: Reset When Invalid

## When to Use

Use when a prefix becomes invalid and every start inside that failed prefix is impossible.

## Problem 6: Gas Station

### Problem Statement

Given gas[i] and cost[i], find starting station to complete circle, or -1.

### Input

```text
gas  = [1,2,3,4,5]
cost = [3,4,5,1,2]
```

### Output

```text
3
```

### Idea

If tank becomes negative at i, no station from current start to i can be valid. Reset start to i+1.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int canCompleteCircuit(vector<int>& gas, vector<int>& cost) {
    int total = 0;
    int tank = 0;
    int start = 0;

    for (int i = 0; i < (int)gas.size(); i++) {
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

### Index-by-Index Dry Run Table

| i | gas | cost | diff | tank before reset | Action | start | total |
|---:|---:|---:|---:|---:|---|---:|---:|
| 0 | 1 | 3 | -2 | -2 | Reset | 1 | -2 |
| 1 | 2 | 4 | -2 | -2 | Reset | 2 | -4 |
| 2 | 3 | 5 | -2 | -2 | Reset | 3 | -6 |
| 3 | 4 | 1 | 3 | 3 | Continue | 3 | -3 |
| 4 | 5 | 2 | 3 | 6 | Continue | 3 | 0 |

---

# 12. Pattern 7: Boundary Expansion

## When to Use

Use when a segment must include all related elements.

## Problem 7: Partition Labels

### Problem Statement

Partition a string so each character appears in at most one part. Return lengths.

### Input

```text
s = "ababcbacadefegdehijhklij"
```

### Output

```text
[9,7,8]
```

### Idea

Precompute last occurrence. While scanning, current partition end is max last occurrence of all seen chars.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> partitionLabels(string s) {
    vector<int> last(26);

    for (int i = 0; i < (int)s.size(); i++) {
        last[s[i] - 'a'] = i;
    }

    vector<int> ans;
    int start = 0, end = 0;

    for (int i = 0; i < (int)s.size(); i++) {
        end = max(end, last[s[i] - 'a']);

        if (i == end) {
            ans.push_back(end - start + 1);
            start = i + 1;
        }
    }

    return ans;
}
```

### Index-by-Index Dry Run Table

| i | char | last[char] | end before | end after | Close partition? |
|---:|---|---:|---:|---:|---|
| 0 | a | 8 | 0 | 8 | No |
| 1 | b | 5 | 8 | 8 | No |
| 2 | a | 8 | 8 | 8 | No |
| 3 | b | 5 | 8 | 8 | No |
| 4 | c | 7 | 8 | 8 | No |
| 5 | b | 5 | 8 | 8 | No |
| 6 | a | 8 | 8 | 8 | No |
| 7 | c | 7 | 8 | 8 | No |
| 8 | a | 8 | 8 | 8 | Yes, length 9 |

---

# 13. Pattern 8: Two Pointers Greedy

## When to Use

Use after sorting when pairing smallest with largest gives a safe choice.

## Problem 8: Boats to Save People

### Problem Statement

Each boat can carry at most two people and weight <= limit. Minimize boats.

### Input

```text
people = [3,2,2,1]
limit = 3
```

### Output

```text
3
```

### Idea

Sort weights. Always put heaviest person on a boat. If lightest can join, pair them.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int numRescueBoats(vector<int>& people, int limit) {
    sort(people.begin(), people.end());

    int left = 0;
    int right = (int)people.size() - 1;
    int boats = 0;

    while (left <= right) {
        if (people[left] + people[right] <= limit) {
            left++;
        }
        right--;
        boats++;
    }

    return boats;
}
```

### Index-by-Index Dry Run Table

Sorted: `[1,2,2,3]`

| Step | left | right | Pair Weight | Action | Boats |
|---:|---:|---:|---:|---|---:|
| 1 | 0 | 3 | 1 + 3 = 4 | 3 alone | 1 |
| 2 | 0 | 2 | 1 + 2 = 3 | Pair 1 and 2 | 2 |
| 3 | 1 | 1 | 2 | 2 alone | 3 |

---

# 14. Pattern 9: Heap Greedy

## When to Use

Use when the best candidate changes dynamically while scanning.

## Problem 9: Meeting Rooms II

### Problem Statement

Given meeting intervals, find minimum number of rooms required.

### Input

```text
intervals = [[0,30],[5,10],[15,20]]
```

### Output

```text
2
```

### Idea

Sort by start time. Min-heap stores end times of active meetings. Reuse room if earliest end <= current start.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int minMeetingRooms(vector<vector<int>>& intervals) {
    sort(intervals.begin(), intervals.end());

    priority_queue<int, vector<int>, greater<int>> pq;

    for (auto& meeting : intervals) {
        int start = meeting[0];
        int end = meeting[1];

        if (!pq.empty() && pq.top() <= start) {
            pq.pop();
        }

        pq.push(end);
    }

    return pq.size();
}
```

### Index-by-Index Dry Run Table

| Step | Meeting | Heap Before | Can Reuse? | Heap After | Rooms |
|---:|---|---|---|---|---:|
| 1 | [0,30] | [] | No | [30] | 1 |
| 2 | [5,10] | [30] | No | [10,30] | 2 |
| 3 | [15,20] | [10,30] | Yes, pop 10 | [20,30] | 2 |

---

# 15. Pattern 10: Frequency Greedy

## When to Use

Use when frequency distribution decides the answer.

## Problem 10: Task Scheduler

### Problem Statement

Given tasks and cooldown n, find least intervals to finish all tasks.

### Input

```text
tasks = ['A','A','A','B','B','B']
n = 2
```

### Output

```text
8
```

### Idea

Most frequent task creates the frame. If maxFreq = 3, there are maxFreq-1 gaps. Fill gaps with other tasks.

Formula:

```text
answer = max(totalTasks, (maxFreq - 1) * (n + 1) + countMaxFreq)
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int leastInterval(vector<char>& tasks, int n) {
    vector<int> freq(26, 0);
    for (char c : tasks) freq[c - 'A']++;

    int maxFreq = *max_element(freq.begin(), freq.end());
    int countMax = 0;

    for (int f : freq) {
        if (f == maxFreq) countMax++;
    }

    int frame = (maxFreq - 1) * (n + 1) + countMax;
    return max((int)tasks.size(), frame);
}
```

### Index-by-Index Dry Run Table

| Value | Meaning | Result |
|---|---|---:|
| totalTasks | 6 tasks | 6 |
| maxFreq | A and B appear 3 times | 3 |
| countMax | A and B both max | 2 |
| frame | (3-1)*(2+1)+2 | 8 |
| answer | max(6,8) | 8 |

Schedule:

```text
A B idle A B idle A B
```

---

# 16. Pattern 11: Stack Greedy

## When to Use

Use when you can delete/remove previous worse choices to improve current answer.

## Problem 11: Remove K Digits

### Problem Statement

Remove k digits from a number string to make the smallest possible number.

### Input

```text
num = "1432219"
k = 3
```

### Output

```text
"1219"
```

### Idea

Maintain increasing stack. If current digit is smaller than stack top, remove top while k > 0.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

string removeKdigits(string num, int k) {
    string st;

    for (char c : num) {
        while (!st.empty() && k > 0 && st.back() > c) {
            st.pop_back();
            k--;
        }
        st.push_back(c);
    }

    while (k > 0 && !st.empty()) {
        st.pop_back();
        k--;
    }

    int pos = 0;
    while (pos < (int)st.size() && st[pos] == '0') pos++;

    string ans = st.substr(pos);
    return ans.empty() ? "0" : ans;
}
```

### Index-by-Index Dry Run Table

| i | digit | stack before | k before | Action | stack after | k after |
|---:|---|---|---:|---|---|---:|
| 0 | 1 | "" | 3 | push | "1" | 3 |
| 1 | 4 | "1" | 3 | push | "14" | 3 |
| 2 | 3 | "14" | 3 | pop 4, push 3 | "13" | 2 |
| 3 | 2 | "13" | 2 | pop 3, push 2 | "12" | 1 |
| 4 | 2 | "12" | 1 | push | "122" | 1 |
| 5 | 1 | "122" | 1 | pop 2, push 1 | "121" | 0 |
| 6 | 9 | "121" | 0 | push | "1219" | 0 |

---

# 17. Pattern 12: Replacement Greedy

## When to Use

Use when you first spend cheap resource, then later replace the largest previous cost with expensive resource.

## Problem 12: Furthest Building You Can Reach

### Problem Statement

Given building heights, bricks, and ladders. Climb up using bricks or ladders. Return furthest index reachable.

### Input

```text
heights = [4,2,7,6,9,14,12]
bricks = 5
ladders = 1
```

### Output

```text
4
```

### Idea

Use bricks for climbs first. Store climbs in max-heap. If bricks become negative, replace biggest brick climb with ladder.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int furthestBuilding(vector<int>& heights, int bricks, int ladders) {
    priority_queue<int> usedBricks;

    for (int i = 0; i + 1 < (int)heights.size(); i++) {
        int climb = heights[i + 1] - heights[i];
        if (climb <= 0) continue;

        bricks -= climb;
        usedBricks.push(climb);

        if (bricks < 0) {
            if (ladders == 0) return i;
            bricks += usedBricks.top();
            usedBricks.pop();
            ladders--;
        }
    }

    return heights.size() - 1;
}
```

### Index-by-Index Dry Run Table

| i -> i+1 | Climb | Bricks After Use | Heap | Need Replacement? | Ladders Left | Reach |
|---|---:|---:|---|---|---:|---:|
| 4 -> 2 | -2 | 5 | [] | No | 1 | 1 |
| 2 -> 7 | 5 | 0 | [5] | No | 1 | 2 |
| 7 -> 6 | -1 | 0 | [5] | No | 1 | 3 |
| 6 -> 9 | 3 | -3 | [5,3] | Replace 5 by ladder | 0 | 4 |
| 9 -> 14 | 5 | -3 | [5,3] | No ladder left | 0 | Stop at 4 |

---

# 18. Pattern 13: Sorting + Prefix/Suffix Observation

## When to Use

Use when sorting by gain/value and taking prefix gives optimal answer.

## Problem 13: Maximum Units on a Truck

### Problem Statement

Each box type has number of boxes and units per box. Truck can carry limited boxes. Maximize units.

### Input

```text
boxTypes = [[1,3],[2,2],[3,1]]
truckSize = 4
```

### Output

```text
8
```

### Idea

Sort by units per box descending. Take high-value boxes first.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maximumUnits(vector<vector<int>>& boxTypes, int truckSize) {
    sort(boxTypes.begin(), boxTypes.end(), [](auto& a, auto& b) {
        return a[1] > b[1];
    });

    int ans = 0;

    for (auto& box : boxTypes) {
        int count = box[0];
        int units = box[1];
        int take = min(count, truckSize);

        ans += take * units;
        truckSize -= take;

        if (truckSize == 0) break;
    }

    return ans;
}
```

### Index-by-Index Dry Run Table

Sorted by units: `[[1,3],[2,2],[3,1]]`

| Step | Boxes | Units/Box | Truck Left | Take | Gain | Total |
|---:|---:|---:|---:|---:|---:|---:|
| 1 | 1 | 3 | 4 | 1 | 3 | 3 |
| 2 | 2 | 2 | 3 | 2 | 4 | 7 |
| 3 | 3 | 1 | 1 | 1 | 1 | 8 |

---

# 19. Pattern 14: Parity / Modulo Greedy

## When to Use

Use when only even/odd or modulo class matters, not exact values.

## Problem 14: Make Array Even

### Problem Statement

Given array, count minimum operations to make all numbers even if one operation divides an even number by 2 until odd/even condition changes.

### Input

```text
arr = [40, 6, 7, 8]
```

### Output

```text
3
```

### Idea

For each even number, divide by 2 until it becomes odd. Count divisions.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int operationsToMakeOdd(vector<int>& arr) {
    int ops = 0;

    for (int x : arr) {
        while (x % 2 == 0) {
            x /= 2;
            ops++;
        }
    }

    return ops;
}
```

### Index-by-Index Dry Run Table

| i | x | Divisions | Final Odd | Total Ops |
|---:|---:|---:|---:|---:|
| 0 | 40 | 3: 40->20->10->5 | 5 | 3 |
| 1 | 6 | 1: 6->3 | 3 | 4 |
| 2 | 7 | 0 | 7 | 4 |
| 3 | 8 | 3: 8->4->2->1 | 1 | 7 |

> Note: This pattern is about parity decisions. Exact problem rules may vary, but the thinking is same: reduce values to modulo classes.

---

# 20. Pattern 15: String Greedy

## When to Use

Use when choosing characters by frequency or lexicographic order.

## Problem 15: Reorganize String

### Problem Statement

Rearrange string so no two adjacent characters are same. Return empty if impossible.

### Input

```text
s = "aab"
```

### Output

```text
"aba"
```

### Idea

Always pick the most frequent character that is not equal to previous character.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

string reorganizeString(string s) {
    vector<int> freq(26, 0);
    for (char c : s) freq[c - 'a']++;

    priority_queue<pair<int,char>> pq;
    for (int i = 0; i < 26; i++) {
        if (freq[i] > 0) pq.push({freq[i], char('a' + i)});
    }

    string ans;
    pair<int,char> prev = {0, '#'};

    while (!pq.empty()) {
        auto [cnt, ch] = pq.top();
        pq.pop();

        ans.push_back(ch);
        cnt--;

        if (prev.first > 0) pq.push(prev);
        prev = {cnt, ch};
    }

    if ((int)ans.size() != (int)s.size()) return "";
    return ans;
}
```

### Index-by-Index Dry Run Table

For `s = "aab"`

| Step | Heap Choice | Previous Held | Answer | Next Previous |
|---:|---|---|---|---|
| 1 | a(2) | none | a | a(1) |
| 2 | b(1) | a(1) | ab | b(0), push a(1) |
| 3 | a(1) | b(0) | aba | a(0) |

---

# 21. Pattern 16: Constructive Greedy

## When to Use

Use when the problem asks you to build any valid answer.

## Problem 16: Construct Beautiful Permutation

### Problem Statement

Construct a permutation from 1 to n such that no adjacent values differ by 1.

### Input

```text
n = 5
```

### Output

```text
2 4 1 3 5
```

### Idea

Put all even numbers first, then odd numbers. Adjacent differences usually avoid 1 except small impossible cases.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> beautifulPermutation(int n) {
    if (n == 2 || n == 3) return {};

    vector<int> ans;
    for (int x = 2; x <= n; x += 2) ans.push_back(x);
    for (int x = 1; x <= n; x += 2) ans.push_back(x);

    return ans;
}
```

### Index-by-Index Dry Run Table

For `n = 5`

| Phase | Added | Answer So Far |
|---|---:|---|
| Even | 2 | [2] |
| Even | 4 | [2,4] |
| Odd | 1 | [2,4,1] |
| Odd | 3 | [2,4,1,3] |
| Odd | 5 | [2,4,1,3,5] |

---

# 22. Pattern 17: Greedy With Invariant

## When to Use

Use when local constraints must be true from left and right.

## Problem 17: Candy

### Problem Statement

Children have ratings. Each child gets at least one candy. Higher rating than neighbor gets more candy. Minimize candies.

### Input

```text
ratings = [1,0,2]
```

### Output

```text
5
```

### Idea

Left pass handles increasing from left. Right pass handles increasing from right.

### C++ Code

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

### Index-by-Index Dry Run Table

Ratings: `[1,0,2]`

| Pass | i | Condition | Candies |
|---|---:|---|---|
| Init | - | all 1 | [1,1,1] |
| Left | 1 | 0 > 1 false | [1,1,1] |
| Left | 2 | 2 > 0 true | [1,1,2] |
| Right | 1 | 0 > 2 false | [1,1,2] |
| Right | 0 | 1 > 0 true | [2,1,2] |

Answer = 5.

---

# 23. Pattern 18: Greedy + Binary Search on Answer

## When to Use

Use when answer is numeric and feasibility is monotonic.

## Problem 18: Aggressive Cows

### Problem Statement

Place cows in stalls to maximize minimum distance between any two cows.

### Input

```text
positions = [1,2,4,8,9]
cows = 3
```

### Output

```text
3
```

### Idea

Binary search distance d. Greedily place cows left to right if gap >= d.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canPlace(vector<int>& positions, int cows, int dist) {
    int placed = 1;
    int last = positions[0];

    for (int i = 1; i < (int)positions.size(); i++) {
        if (positions[i] - last >= dist) {
            placed++;
            last = positions[i];
        }
    }

    return placed >= cows;
}

int aggressiveCows(vector<int>& positions, int cows) {
    sort(positions.begin(), positions.end());

    int low = 1;
    int high = positions.back() - positions.front();
    int ans = 0;

    while (low <= high) {
        int mid = low + (high - low) / 2;

        if (canPlace(positions, cows, mid)) {
            ans = mid;
            low = mid + 1;
        } else {
            high = mid - 1;
        }
    }

    return ans;
}
```

### Binary Search Dry Run Table

| low | high | mid | canPlace(mid)? | ans | Move |
|---:|---:|---:|---|---:|---|
| 1 | 8 | 4 | false | 0 | high = 3 |
| 1 | 3 | 2 | true | 2 | low = 3 |
| 3 | 3 | 3 | true | 3 | low = 4 |

### Greedy Check for d = 3

| Stall | Last Placed | Gap | Action | Placed |
|---:|---:|---:|---|---:|
| 1 | 1 | - | First cow | 1 |
| 2 | 1 | 1 | Skip | 1 |
| 4 | 1 | 3 | Place | 2 |
| 8 | 4 | 4 | Place | 3 |
| 9 | 8 | 1 | Skip | 3 |

---

# 24. Pattern 19: Greedy + DSU

## When to Use

Use when choosing cheapest/safest edge or latest available slot.

## Problem 19: Kruskal MST

### Problem Statement

Given weighted undirected graph, find minimum total weight to connect all nodes.

### Input

```text
n = 4
edges = [(0,1,10), (0,2,6), (0,3,5), (1,3,15), (2,3,4)]
```

### Output

```text
19
```

### Idea

Sort edges by weight. Add edge only if it does not create cycle.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct DSU {
    vector<int> parent, sz;

    DSU(int n) {
        parent.resize(n);
        sz.assign(n, 1);
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
        if (sz[a] < sz[b]) swap(a, b);
        parent[b] = a;
        sz[a] += sz[b];
        return true;
    }
};

struct Edge {
    int u, v, w;
};

int kruskalMST(int n, vector<Edge>& edges) {
    sort(edges.begin(), edges.end(), [](Edge& a, Edge& b) {
        return a.w < b.w;
    });

    DSU dsu(n);
    int total = 0;

    for (auto& e : edges) {
        if (dsu.unite(e.u, e.v)) {
            total += e.w;
        }
    }

    return total;
}
```

### Index-by-Index Dry Run Table

Sorted edges: `(2,3,4), (0,3,5), (0,2,6), (0,1,10), (1,3,15)`

| Step | Edge | Same Component? | Action | Total |
|---:|---|---|---|---:|
| 1 | 2-3 weight 4 | No | Take | 4 |
| 2 | 0-3 weight 5 | No | Take | 9 |
| 3 | 0-2 weight 6 | Yes | Skip | 9 |
| 4 | 0-1 weight 10 | No | Take | 19 |
| 5 | 1-3 weight 15 | Yes | Skip | 19 |

---

# 25. Pattern 20: Huffman / Merge Two Smallest

## When to Use

Use when repeated merging cost equals sum of merged values.

## Problem 20: Minimum Cost to Connect Ropes

### Problem Statement

Given rope lengths, connect all ropes. Cost of connecting two ropes is their sum. Minimize total cost.

### Input

```text
ropes = [4,3,2,6]
```

### Output

```text
29
```

### Idea

Always merge two smallest ropes first.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int minCostRopes(vector<int>& ropes) {
    priority_queue<int, vector<int>, greater<int>> pq;
    for (int x : ropes) pq.push(x);

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

### Index-by-Index Dry Run Table

| Step | Pick | Merge | Heap After | Cost |
|---:|---|---:|---|---:|
| 1 | 2,3 | 5 | [4,5,6] | 5 |
| 2 | 4,5 | 9 | [6,9] | 14 |
| 3 | 6,9 | 15 | [15] | 29 |

---

# 26. Pattern 21: Job Sequencing With Deadlines

## When to Use

Use when each job takes one unit time and has deadline + profit.

## Problem 21: Job Sequencing

### Problem Statement

Schedule jobs before deadlines to maximize profit.

### Input

```text
jobs = [(1,4,20), (2,1,10), (3,1,40), (4,1,30)]
// (id, deadline, profit)
```

### Output

```text
60
```

### Idea

Sort by profit descending. Place each job as late as possible before its deadline.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Job {
    int id;
    int deadline;
    int profit;
};

pair<int,int> jobSequencing(vector<Job>& jobs) {
    sort(jobs.begin(), jobs.end(), [](Job& a, Job& b) {
        return a.profit > b.profit;
    });

    int maxDeadline = 0;
    for (auto& job : jobs) maxDeadline = max(maxDeadline, job.deadline);

    vector<int> slot(maxDeadline + 1, -1);
    int count = 0, profit = 0;

    for (auto& job : jobs) {
        for (int t = job.deadline; t >= 1; t--) {
            if (slot[t] == -1) {
                slot[t] = job.id;
                count++;
                profit += job.profit;
                break;
            }
        }
    }

    return {count, profit};
}
```

### Index-by-Index Dry Run Table

Sorted by profit: job3(40), job4(30), job1(20), job2(10)

| Step | Job | Deadline | Profit | Slot Tried | Action | Total Profit |
|---:|---|---:|---:|---|---|---:|
| 1 | 3 | 1 | 40 | 1 | Place at 1 | 40 |
| 2 | 4 | 1 | 30 | 1 full | Skip | 40 |
| 3 | 1 | 4 | 20 | 4 | Place at 4 | 60 |
| 4 | 2 | 1 | 10 | 1 full | Skip | 60 |

---

# 27. Pattern 22: Ordered Set / Multiset Greedy

## When to Use

Use when you need the best available value <= x or >= x.

## Problem 22: Concert Tickets

### Problem Statement

Given ticket prices and customers' max prices. For each customer, assign the most expensive ticket they can afford, or -1.

### Input

```text
tickets = [5,3,7,8,5]
customers = [4,8,3]
```

### Output

```text
3 8 -1
```

### Idea

Use multiset. For customer x, find first ticket > x using upper_bound, then step back.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> assignTickets(vector<int>& tickets, vector<int>& customers) {
    multiset<int> ms(tickets.begin(), tickets.end());
    vector<int> ans;

    for (int x : customers) {
        auto it = ms.upper_bound(x);
        if (it == ms.begin()) {
            ans.push_back(-1);
        } else {
            --it;
            ans.push_back(*it);
            ms.erase(it);
        }
    }

    return ans;
}
```

### Index-by-Index Dry Run Table

Initial multiset: `{3,5,5,7,8}`

| Customer | upper_bound | Chosen Ticket | Multiset After | Output |
|---:|---:|---:|---|---|
| 4 | 5 | 3 | {5,5,7,8} | 3 |
| 8 | end | 8 | {5,5,7} | 8 |
| 3 | 5 | -1 | {5,5,7} | -1 |

---

# 28. Greedy vs DP Decision Table

| Situation | Greedy | DP |
|---|---|---|
| One safe local choice exists | Yes | Not needed |
| Need best among many previous states | Usually no | Yes |
| Sorting gives natural order | Often yes | Maybe |
| Choices strongly affect future | Risky | Often yes |
| Can prove by exchange | Yes | Usually not needed |
| Counterexample exists | No | Maybe |
| Need all combinations/subsets | No | Yes/backtracking |

---

# 29. Common Counterexamples

## Coin Change Counterexample

Coins: `[1,3,4]`, amount = `6`

Greedy picks:

```text
4 + 1 + 1 = 3 coins
```

Optimal:

```text
3 + 3 = 2 coins
```

Lesson: greedy largest coin first is not always valid.

## 0/1 Knapsack Counterexample

Choosing highest value/weight ratio can fail because items cannot be split.

Lesson: if future combinations matter heavily, use DP.

---

# 30. Reusable C++ Templates

## Sort by End Time

```cpp
sort(intervals.begin(), intervals.end(), [](auto& a, auto& b) {
    return a[1] < b[1];
});
```

## Sort by Profit Descending

```cpp
sort(items.begin(), items.end(), [](auto& a, auto& b) {
    return a.profit > b.profit;
});
```

## Min Heap

```cpp
priority_queue<int, vector<int>, greater<int>> pq;
```

## Max Heap

```cpp
priority_queue<int> pq;
```

## Multiset Best <= x

```cpp
auto it = ms.upper_bound(x);
if (it == ms.begin()) {
    // no value <= x
} else {
    --it;
    int chosen = *it;
    ms.erase(it);
}
```

## Binary Search + Greedy Check

```cpp
int low = 0, high = 1e9, ans = 0;

while (low <= high) {
    int mid = low + (high - low) / 2;

    if (can(mid)) {
        ans = mid;
        low = mid + 1;
    } else {
        high = mid - 1;
    }
}
```

## Stack Greedy

```cpp
string st;

for (char c : s) {
    while (!st.empty() && shouldRemove(st.back(), c)) {
        st.pop_back();
    }
    st.push_back(c);
}
```

## DSU Template

```cpp
struct DSU {
    vector<int> parent, sz;

    DSU(int n) {
        parent.resize(n);
        sz.assign(n, 1);
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
        if (sz[a] < sz[b]) swap(a, b);
        parent[b] = a;
        sz[a] += sz[b];
        return true;
    }
};
```

---

# 31. Practice Ladder

## Phase 1 — Foundation

| Problem | Pattern | Difficulty |
|---|---|---|
| Assign Cookies | Sort + Pick | Easy |
| Lemonade Change | Local Simulation | Easy |
| Best Time to Buy/Sell Stock | Running Min/Max | Easy |
| Activity Selection | Interval Greedy | Easy |
| Minimum Number of Arrows | Interval Greedy | Medium |

## Phase 2 — FAANG

| Problem | Pattern | Difficulty |
|---|---|---|
| Jump Game | Forward Reachability | Medium |
| Jump Game II | Level Reach Greedy | Medium |
| Gas Station | Reset Greedy | Medium |
| Partition Labels | Boundary Greedy | Medium |
| Boats to Save People | Two Pointers Greedy | Medium |
| Meeting Rooms II | Heap Greedy | Medium |
| Task Scheduler | Frequency Greedy | Medium |
| Remove K Digits | Stack Greedy | Medium |
| Furthest Building | Replacement Greedy | Medium |
| Candy | Two-pass invariant | Hard |

## Phase 3 — Codeforces A/B/C

| Problem Type | Pattern |
|---|---|
| Construct permutation | Constructive greedy |
| Balance parity | Parity/modulo greedy |
| Choose max score after sorting | Prefix greedy |
| Pair small with large | Two pointers greedy |
| String rearrangement | Frequency/string greedy |
| Min/max operations | Invariant greedy |

## Phase 4 — Codeforces D / Advanced

| Problem Type | Pattern |
|---|---|
| Maximize minimum distance | Binary search + greedy |
| Minimize maximum load | Binary search + greedy |
| Cheapest connected graph | Kruskal / DSU |
| Assign latest slot | DSU deadline greedy |
| Ticket/customer matching | multiset greedy |
| Merge cost | Huffman greedy |

---

# 32. Final Cheat Sheet

## Greedy Signals

```text
max/min
possible/can reach
non-overlap
earliest/latest
smallest/largest
minimum resources
best available dynamically
construct any valid answer
```

## Greedy Thinking Order

```text
1. Try sorting.
2. Try one-pass scan.
3. Try two pointers after sorting.
4. Try heap if best candidate changes.
5. Try stack if removing previous bad choice helps.
6. Try multiset if need best <= x or >= x.
7. Try binary search if answer is numeric and monotonic.
8. Prove safe choice.
9. Search for counterexample.
```

## One-Line Memory Rule

> Greedy works when you can make one choice now and prove it never hurts the future.

