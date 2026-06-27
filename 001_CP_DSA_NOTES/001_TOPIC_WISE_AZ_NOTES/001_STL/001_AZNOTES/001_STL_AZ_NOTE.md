# Ultimate STL Pattern Recognition Guide — CP CM + FAANG 10/10 Edition

> Single combined Markdown note from your uploaded STL, Monotonic Stack, Range Mapping, Range Mapping Phase-Wise, and Priority Queue guides.  
> Goal: build **pattern recognition**, **clean C++17 implementation**, **dry-run confidence**, and **contest/interview decision speed**.

---

## How To Use This Guide

```text
DAY 1-2  : Core STL + sliding window + prefix + stack basics
DAY 3-4  : Monotonic stack recognition + boundary + contribution
DAY 5-6  : Priority queue / heap FAANG patterns
DAY 7-8  : Range mapping / intervals / sweep line
EVERY DAY: 3 dry runs by hand + 5 implementation reps
```

### Master Learning Flow

```text
Problem statement
      |
      v
Extract operations
      |
      +--> repeated range sum/count?        -> prefix / Fenwick / segment tree
      +--> fixed window?                    -> sliding window
      +--> window min/max?                  -> monotonic deque
      +--> nearest greater/smaller?         -> monotonic stack
      +--> largest rectangle/boundary?      -> monotonic stack + NSE/PSE
      +--> current best min/max repeatedly? -> priority_queue
      +--> top K / kth?                     -> bounded heap
      +--> dynamic median/top-k sum?        -> two heaps / two multisets
      +--> interval coverage?               -> sorted endpoints / set intervals
      +--> nested grammar/brackets?         -> stack / recursion parser
      +--> too many subarrays?              -> contribution technique
```

### One-Page STL Decision Tree

```text
Need order?
  |
  +-- no order, count/lookup fast
  |      -> unordered_map / unordered_set
  |
  +-- sorted order / predecessor / successor
  |      -> set / multiset / map
  |
  +-- only current max/min
  |      -> priority_queue
  |
  +-- LIFO / nested / nearest boundary
  |      -> stack / monotonic stack
  |
  +-- FIFO / BFS / stream order
  |      -> queue
  |
  +-- both ends / window min-max
  |      -> deque / monotonic deque
  |
  +-- random access / sorting / prefix
         -> vector
```

### CP CM Pattern Recognition Table

| Problem signal | First idea | STL / DS | Target complexity |
|---|---|---|---|
| `n,q <= 2e5`, static range query | precompute | prefix / sparse table | O(n), O(1)/O(log n) |
| dynamic update + range query | tree structure | Fenwick / segment tree | O(log n) |
| subarray sum equals X, negatives allowed | previous prefix | map/unordered_map | O(n log n)/O(n) |
| all positive + target sum | grow/shrink | sliding window | O(n) |
| every window min/max | maintain candidates | deque | O(n) |
| every window median/cost | two halves | multiset | O(n log k) |
| next greater/smaller | unresolved stack | stack | O(n) |
| histogram / subarray minimum contribution | nearest smaller boundary | monotonic stack | O(n) |
| kth largest/top k | survival heap | priority_queue | O(n log k) |
| merge k sorted lists | one head from each | min heap | O(N log k) |
| intervals point query offline | endpoints | vector + binary search | O(log n) |
| intervals dynamic merge/delete | ordered disjoint intervals | set<pair<int,int>> | O(log n + merged) |

### FAANG Answer Template

```text
1. Clarify input and edge cases.
2. State brute force.
3. Identify bottleneck.
4. State invariant.
5. Choose STL.
6. Code cleanly.
7. Dry run sample.
8. Give complexity.
9. Mention edge cases.
```

### CM Debug Checklist

```text
Before submit:
[ ] long long for prefix/contribution/cost?
[ ] erase only one occurrence from multiset?
[ ] duplicates handled in monotonic stack equality?
[ ] heap stale states ignored?
[ ] window contains exactly k elements when answering?
[ ] lower_bound vs upper_bound correct for inclusive boundaries?
[ ] coordinate compression preserves segment lengths if needed?
[ ] custom hash needed for unordered_map on Codeforces?
```

---

## Master Problem Pattern Cards

### Card 1 — Fixed Window Minimum

Problem:

```text
Given array a and k, output minimum of every subarray of length k.
```

ASCII invariant:

```text
Window: [i-k+1 ........ i]
Deque : increasing values, front is minimum

new x arrives
   |
   +-- pop back while back > x
   +-- push x
   +-- remove outgoing if it is at front
```

Dry run:

```text
a = [4,2,1,5,3], k = 3

insert 4 -> dq [4]
insert 2 -> pop 4 -> dq [2]
insert 1 -> pop 2 -> dq [1]        answer 1
insert 5 -> dq [1,5]               answer 1
insert 3 -> pop 5 -> dq [1,3]      answer 1
```

### Card 2 — Next Greater Element

Problem:

```text
For every element, find first greater element on right.
```

ASCII invariant:

```text
Stack holds unresolved elements in decreasing order.
Current bigger element resolves smaller elements.

[2,1,5,3]
 ^ ^ |
 | | +-- 5 resolves 1 and 2
```

### Card 3 — Kth Largest

Problem:

```text
Return kth largest element in array.
```

ASCII invariant:

```text
min heap of size k = strongest k survivors
heap top = weakest survivor = kth largest
```

### Card 4 — Interval Point Coverage

Problem:

```text
Given intervals and query point x, count covering intervals.
```

ASCII invariant:

```text
covering(x) = intervals started by x - intervals ended before x
            = count(l <= x) - count(r < x)
```

### Card 5 — Dynamic Merged Intervals

Problem:

```text
Insert/delete covered ranges and query coverage.
```

ASCII invariant:

```text
set always stores disjoint sorted intervals:
[1,5] [10,20] [30,40]

insert [4,12]
      overlaps [1,5] and [10,20]
      result [1,20] [30,40]
```

---


---

# Part 1 — Core STL + Problem Solving Notes


## Competitive Programming STL & Problem Solving Notes — ASCII 10/10 CM + FAANG Edition

### Clickable Index

- [Clickable Index](#clickable-index)
- [0. Problem Solving Strategy](#0-problem-solving-strategy)
- [1. Balanced Brackets / Parentheses](#1-balanced-brackets-parentheses)
- [2. Sliding Window Subarray Maintenance](#2-sliding-window-subarray-maintenance)
- [3. Sliding Window Minimum](#3-sliding-window-minimum)
- [4. Sliding Window Cost / Make All Elements Equal](#4-sliding-window-cost-make-all-elements-equal)
- [5. Mean, Variance, Median, Mode Dashboard](#5-mean-variance-median-mode-dashboard)
- [6. Prefix Sum and Subarray Sum Equals X](#6-prefix-sum-and-subarray-sum-equals-x)
- [7. Stack Mastery Next Greater Element](#7-stack-mastery-next-greater-element)
- [8. Trapping Rain Water](#8-trapping-rain-water)
- [9. Range Mapping / Interval Coverage](#9-range-mapping-interval-coverage)
- [10. Top K Sum](#10-top-k-sum)
- [11. Priority Queue Notes](#11-priority-queue-notes)
- [12. Stack and Queue Basics](#12-stack-and-queue-basics)
- [13. Contribution Technique](#13-contribution-technique)
- [14. Pattern Matching / Coordinate Geometry Printing](#14-pattern-matching-coordinate-geometry-printing)
- [15. Molecular Formula Parser](#15-molecular-formula-parser)
- [15A. Largest Rectangle in Histogram](#15a-largest-rectangle-in-histogram)
- [16. Choosing the Right STL](#16-choosing-the-right-stl)
- [17. Common Mistakes](#17-common-mistakes)
- [18. Final Revision Flow](#18-final-revision-flow)
- [19. Minimal C++ Setup](#19-minimal-c++-setup)
- [20. One-Minute CP Mental Checklist](#20-one-minute-cp-mental-checklist)
- [21. Final Golden Rules](#21-final-golden-rules)

> Upgraded from the original STL AZ note: same core content, Mermaid diagrams converted to ASCII, plus CM/FAANG decision rules, failure traps, pattern recognition cues, and contest-ready revision blocks.

---

### 0A. CM + FAANG Upgrade Layer

This section turns the note from “STL syntax notes” into a pattern-recognition playbook. Use it before contests, FAANG screens, and hard implementation rounds.

```text
PROBLEM STATEMENT
      |
      v
CONSTRAINTS + OPERATIONS
      |
      v
Can I maintain answer while moving?
      |
      +-- fixed/variable window ----> sliding window / deque / multiset
      +-- prefix relationship ------> prefix sum + map
      +-- nearest greater/smaller --> monotonic stack
      +-- dynamic median/top-k -----> two multisets / heaps
      +-- intervals coverage -------> sorted endpoints / merged set
      +-- nested grammar -----------> stack / recursion / parser
      +-- contribution count -------> count appearances, not objects
```

#### CM recognition rules

| Signal in problem | First pattern to test | STL tool | Typical complexity |
|---|---|---|---|
| “every subarray/window of size k” | sliding window | deque / multiset / map | O(n) or O(n log k) |
| “next greater/smaller” | monotonic stack | stack/vector | O(n) |
| “sum equals x, negative allowed” | prefix + frequency | map/unordered_map | O(n log n) / O(n) |
| “minimum absolute deviation” | median | two multisets | O(n log k) |
| “top k with deletes” | two buckets | multiset | O(log n) |
| “merge/cover intervals” | ordered interval set | set<pair<int,int>> | O(log n + merges) |
| “nested brackets/formula” | parser | stack/recursion/map | O(n log alphabet) |
| “too many subarrays/subsequences” | contribution | arithmetic/modular math | O(n) |

#### FAANG implementation expectations

```text
Interview-grade answer =
    brute force explanation
  + bottleneck
  + optimal invariant
  + clean C++ code
  + edge cases
  + complexity
  + dry run
```

#### 5 bugs that separate 1400 from CM

```text
1. multiset.erase(x) removes all x?
   In C++, multiset.erase(value) removes all matching values.
   Use ms.erase(ms.find(x)) for one occurrence.

2. int overflow in contribution/prefix/cost.
   Use long long when values, n, or products can reach 1e9 * 2e5.

3. unordered_map hacked / worst case.
   For Codeforces, prefer map or custom_hash when adversarial input is possible.

4. monotonic stack equality wrong.
   Decide whether duplicates should be popped based on strict/non-strict requirement.

5. window remove before answer vs after answer.
   Keep one invariant: after insert and optional remove, window represents [i-k+1, i].
```

---

### 0. Problem Solving Strategy

#### Time split in contest

```text
ASCII FLOW

Read Problem
  |
  v
Understand statement


  |
  v
Input format


  |
  v
Sample cases


  |
  v
Ideate


  |
  v
Formulate code structure


  |
  v
Implement


  |
  v
Debug
```

| Step | Goal | Time |
|---|---|---:|
| Read | statement + input + samples | 5 min |
| Ideate | brute force, pattern, constraints, eliminate | 25 min |
| Formulate | decide variables, functions, STL, parameters | 1-2 min |
| Code | implementation | 15-20 min |
| Debug | test and fix | 10 min |

#### Intuition

Most CP problems are not about instantly writing code. They are about finding the hidden pattern.

```text
Understand problem
   ↓
Try brute force
   ↓
Check constraints
   ↓
Find bottleneck
   ↓
Replace bottleneck using known pattern
```

#### Ideation checklist

```text
ASCII MIND MAP

  └─ Problem
      └─ similar problem
      └─ bad approaches
      └─ force
      └─ and conquer
      └─ variables
      └─ parameters
      └─ choice
      └─ tests
      └─ cases
```

#### Example

Problem:

```text
Given n numbers and q queries asking sum(l, r).
```

Brute force:

```text
For each query, loop from l to r.
O(nq)
```

If `n, q <= 2e5`, brute force fails.

Pattern:

```text
Repeated range sum on static array = prefix sum.
```

Optimized:

```text
Build prefix once in O(n)
Answer each query in O(1)
```

#### One-minute mental trick

```text
n <= 100        brute force may pass
n <= 2e5        need O(n log n) or O(n)
n <= 1e6        usually O(n)
q large         precompute or use data structure
range query     prefix / Fenwick / segment tree
dynamic update  Fenwick / segment tree / balanced set
```

---

### 1. Balanced Brackets / Parentheses

#### Core idea

For only `(` and `)`, maintain `depth`:

- `(` increases depth.
- `)` decreases depth.
- At the end, depth must be `0`.
- During scanning, depth must never become negative.

```text
ASCII FLOW

Start depth = 0
  |
  v
Read char?


  | open bracket
  v
depth plus 1


  | close bracket
  v
depth minus 1


  |
  v
depth negative??


  | yes
  v
Invalid


  | no
  v


  |
  v


  | end
  v
depth equals 0??


  | yes
  v
Valid


  | no
  v
Invalid
```

#### Intuition

`depth` means how many open brackets are waiting to be closed.

Example:

```text
s = (()())
```

| char | depth |
|---|---:|
| `(` | 1 |
| `(` | 2 |
| `)` | 1 |
| `(` | 2 |
| `)` | 1 |
| `)` | 0 |

Valid.

Invalid example:

```text
s = ())( 
```

At the third character, `depth` becomes `-1`, meaning we closed more brackets than opened.

#### C++: single bracket type

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isBalancedParentheses(const string& s) {
    int depth = 0;

    for (char ch : s) {
        if (ch == '(') depth++;
        else if (ch == ')') depth--;

        if (depth < 0) return false;
    }

    return depth == 0;
}
```

#### Dry Run And ASCII Flow

##### Dry Run: Single bracket counter

Input:

```text
s = (()())
```

| Character | Action | Depth |
|---|---|---:|
| `(` | open, add one | 1 |
| `(` | open, add one | 2 |
| `)` | close, subtract one | 1 |
| `(` | open, add one | 2 |
| `)` | close, subtract one | 1 |
| `)` | close, subtract one | 0 |

Result: depth never becomes negative and final depth is zero, so valid.

##### ASCII Dry Run Diagram

```text
ASCII FLOW

Start depth zero
  |
  v
Read character


  |
  v
Open bracket?


  | Yes
  v
Increase depth


  | No
  v
Decrease depth


  |
  v
More characters?


  |
  v
Depth negative?


  | Yes
  v
Invalid


  | No
  v


  | Yes
  v


  | No
  v
Depth zero?


  | Yes
  v
Valid


  | No
  v
```


#### Multiple bracket types

For `()`, `{}`, `[]`, use stack. The last opened bracket must match the first incoming closing bracket.

```text
ASCII FLOW

Scan string
  |
  v
Opening bracket??


  | yes
  v
Push to stack


  | closing bracket
  v
Stack empty??


  | yes
  v
Invalid


  | no
  v
Top matches closing??


  | yes
  v
Pop stack


  | no
  v


  |
  v


  |
  v


  | end
  v
Stack empty??


  | yes
  v
Valid


  | no
  v
```

#### Example

```text
[{()}]
```

Stack behavior:

```text
[      push [
{      push {
(      push (
)      top is (, pop
}      top is {, pop
]      top is [, pop
empty  valid
```

Bad example:

```text
[(])
```

At `]`, stack top is `(`, so mismatch.

#### C++: stack + map

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isBalanced(const string& s) {
    map<char, char> closeToOpen = {
        {')', '('},
        {'}', '{'},
        {']', '['}
    };

    stack<char> st;

    for (char ch : s) {
        if (ch == '(' || ch == '{' || ch == '[') {
            st.push(ch);
        } else if (closeToOpen.count(ch)) {
            if (st.empty() || st.top() != closeToOpen[ch]) return false;
            st.pop();
        }
    }

    return st.empty();
}
```

#### Dry Run And ASCII Flow

##### Dry Run: Multiple bracket stack

Input:

```text
s = [{()}]
```

| Character | Stack before | Action | Stack after |
|---|---|---|---|
| `[` | empty | push | `[` |
| `{` | `[` | push | `[ {` |
| `(` | `[ {` | push | `[ { (` |
| `)` | `[ { (` | match and pop | `[ {` |
| `}` | `[ {` | match and pop | `[` |
| `]` | `[` | match and pop | empty |

Result: stack is empty, so valid.

##### ASCII Dry Run Diagram

```text
ASCII FLOW

Start empty stack
  |
  v
Read character


  |
  v
Opening bracket?


  | Yes
  v
Push to stack


  | No
  v
Stack empty?


  | Yes
  v
Invalid


  | No
  v
Top matches closing?


  | Yes
  v
Pop stack


  | No
  v


  |
  v
More characters?


  |
  v


  | Yes
  v


  | No
  v
Stack empty?


  | Yes
  v
Valid


  | No
  v
```


#### Range query on balanced parentheses

For a range `[l, r]` in a parentheses string, using prefix depth:

- `depth[i]` = balance after processing index `i`.
- Range is balanced if:
  - `depth[l-1] == depth[r]`
  - minimum depth inside range never goes below `depth[l-1]`.

```text
ASCII FLOW

Query l,r
  |
  v
base = depth before l


  |
  v
depth at r equals base??


  | no
  v
Not balanced


  | yes
  v
minimum depth in l..r >= base??


  | yes
  v
Balanced


  | no
  v
```

#### CM + FAANG edge cases

```text
Empty string: valid if no unmatched brackets.
Mixed characters: either ignore non-brackets or reject based on statement.
Multiple types: counter is not enough; use stack.
Range bracket queries: need prefix depth + range minimum, not stack per query.
```

#### One-minute mental trick

```text
One bracket type:
    use counter

Multiple bracket types:
    use stack

Range balanced query:
    prefix depth + range minimum
```

---

### 2. Sliding Window Subarray Maintenance

Sliding window is used when we need answers for every window/subarray of length `k`, such as:

- minimum / maximum in every window
- number of distinct elements
- median / mean of each window
- cost of each window

#### General template

```text
ASCII FLOW

Loop i from 0 to n-1
  |
  v
Insert current value


  |
  v
i-k >= 0??


  | yes
  v
Remove outgoing value


  | no
  v
Skip remove


  |
  v
i >= k-1??


  |
  v


  | yes
  v
Answer current window


  | no
  v


  |
  v
```

```cpp
for (int i = 0; i < n; i++) {
    ds.insert(arr[i]);

    if (i - k >= 0) {
        ds.erase(arr[i - k]);
    }

    if (i >= k - 1) {
        cout << ds.answer() << '\n';
    }
}
```

#### Dry Run And ASCII Flow

##### Dry Run: Fixed size window movement

Input:

```text
a = [4, 2, 1, 5, 3], k = 3
```

| i | Insert | Remove | Current window | Answer ready |
|---:|---:|---|---|---|
| 0 | 4 | none | `[4]` | no |
| 1 | 2 | none | `[4, 2]` | no |
| 2 | 1 | none | `[4, 2, 1]` | yes |
| 3 | 5 | 4 | `[2, 1, 5]` | yes |
| 4 | 3 | 2 | `[1, 5, 3]` | yes |

##### ASCII Dry Run Diagram

```text
ASCII FLOW

Loop index i
  |
  v
Insert current element


  |
  v
Window too large?


  | Yes
  v
Remove outgoing element


  | No
  v
Skip removal


  |
  v
Window size is k?


  |
  v


  | Yes
  v
Compute answer


  | No
  v
Continue


  |
  v


  |
  v
```


#### Intuition

A fixed-size window moves like this:

```text
[0 ... k-1]
 [1 ... k]
  [2 ... k+1]
```

Every step:

```text
add new right element
remove old left element
calculate answer
```

#### Example

```text
a = [4, 2, 1, 5, 3]
k = 3
```

Windows:

```text
[4, 2, 1]
[2, 1, 5]
[1, 5, 3]
```

If asking minimum:

```text
1, 1, 1
```

#### CM + FAANG edge cases

```text
k = 1: every element is its own window.
k = n: only one window.
Negative values: sum window works; variable sliding window may fail.
Duplicates: map/multiset erase must remove one occurrence.
```

#### One-minute mental trick

```text
Fixed length subarray?
    sliding window

Need min/max?
    monotonic deque

Need median/cost?
    two multisets

Need frequency/distinct?
    map/unordered_map

Need sum?
    running sum
```

---

### 3. Sliding Window Minimum

#### Using multiset

Operations needed:

- insert incoming element
- remove outgoing element
- get minimum

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> slidingWindowMinMultiset(vector<int>& a, int k) {
    multiset<int> ms;
    vector<int> ans;

    for (int i = 0; i < (int)a.size(); i++) {
        ms.insert(a[i]);

        if (i - k >= 0) {
            ms.erase(ms.find(a[i - k])); // erase only one occurrence
        }

        if (i >= k - 1) {
            ans.push_back(*ms.begin());
        }
    }

    return ans;
}
```

#### Dry Run And ASCII Flow

##### Dry Run: Multiset window minimum

Input:

```text
a = [4, 2, 1, 5, 3], k = 3
```

| i | Insert | Remove | Multiset | Minimum |
|---:|---:|---|---|---|
| 0 | 4 | none | `{4}` | not ready |
| 1 | 2 | none | `{2,4}` | not ready |
| 2 | 1 | none | `{1,2,4}` | 1 |
| 3 | 5 | 4 | `{1,2,5}` | 1 |
| 4 | 3 | 2 | `{1,3,5}` | 1 |

##### ASCII Dry Run Diagram

```text
ASCII FLOW

Insert current value
  |
  v
Need remove old value?


  | Yes
  v
Erase one occurrence


  | No
  v
Skip erase


  |
  v
Window ready?


  |
  v


  | Yes
  v
Minimum is begin of multiset


  | No
  v
Continue
```


Complexity: `O(n log k)`.

#### Using monotonic deque

Maintain elements in increasing order. The minimum is always at the front.

```text
ASCII FLOW

Insert x
  |
  v
back > x??


  | yes
  v
pop back


  |
  v


  | no
  v
push back x


  |
  v
Front is current minimum

Remove outgoing x
  |
  v
front == x??


  | yes
  v
pop front


  | no
  v
Do nothing
```

#### Intuition

If a new element is smaller than previous elements, those previous elements can never become minimum while the new smaller element is still inside the window.

Example:

```text
arriving values: 4, 2, 1
```

Deque:

```text
insert 4 -> [4]
insert 2 -> remove 4 -> [2]
insert 1 -> remove 2 -> [1]
```

The old bigger values are useless for minimum.

#### C++ monotonic deque

```cpp
#include <bits/stdc++.h>
using namespace std;

struct MonotoneMinDeque {
    deque<int> dq;

    void insert(int x) {
        while (!dq.empty() && dq.back() > x) dq.pop_back();
        dq.push_back(x);
    }

    void erase(int x) {
        if (!dq.empty() && dq.front() == x) dq.pop_front();
    }

    int getMin() const {
        return dq.front();
    }
};

vector<int> slidingWindowMin(vector<int>& a, int k) {
    MonotoneMinDeque ds;
    vector<int> ans;

    for (int i = 0; i < (int)a.size(); i++) {
        ds.insert(a[i]);

        if (i - k >= 0) ds.erase(a[i - k]);

        if (i >= k - 1) ans.push_back(ds.getMin());
    }

    return ans;
}
```

#### Dry Run And ASCII Flow

##### Dry Run: Monotonic deque minimum

Input:

```text
a = [4, 2, 1, 5, 3], k = 3
```

| i | x | Deque action | Deque after insert | Window min |
|---:|---:|---|---|---|
| 0 | 4 | push 4 | `[4]` | not ready |
| 1 | 2 | pop 4, push 2 | `[2]` | not ready |
| 2 | 1 | pop 2, push 1 | `[1]` | 1 |
| 3 | 5 | push 5 | `[1,5]` | 1 |
| 4 | 3 | pop 5, push 3 | `[1,3]` | 1 |

##### ASCII Dry Run Diagram

```text
ASCII FLOW

Insert x
  |
  v
Back greater than x?


  | Yes
  v
Pop back


  |
  v


  | No
  v
Push x


  |
  v
Outgoing equals front?


  | Yes
  v
Pop front


  | No
  v
Keep front


  |
  v
Front is minimum


  |
  v
```


Complexity: `O(n)`.

#### CM + FAANG edge cases

```text
For deque, store indices when expiry is by position.
Store values only when outgoing value is known and duplicates are handled carefully.
For max deque, reverse comparison.
For equal values, choose >= or > based on whether you want stable indices.
```

#### One-minute mental trick

```text
Sliding window min/max:
    multiset = easy O(n log k)
    deque = optimal O(n)

For minimum:
    increasing deque

For maximum:
    decreasing deque
```

---

### 4. Sliding Window Cost / Make All Elements Equal

Problem: for every window of size `k`, find minimum cost to make all elements equal, where cost is sum of absolute differences.

For values:

```text
a1, a2, ..., ak
```

Minimize:

```text
|x-a1| + |x-a2| + ... + |x-ak|
```

The minimum occurs at the median.

```text
ASCII FLOW

Window values
  |
  v
Find median


  |
  v
Cost = sum abs ai - median


  |
  v
Maintain two multisets
```

#### Intuition

For absolute difference, the median is best because it balances how many values are on the left and right.

Example:

```text
[1, 2, 10]
```

Try making all equal to:

```text
1:  |1-1| + |2-1| + |10-1| = 10
2:  |1-2| + |2-2| + |10-2| = 9
10: |1-10| + |2-10| + |10-10| = 17
```

Median `2` gives the minimum cost.

#### Data structure

Maintain two multisets:

- `lo`: smaller half, contains the median at `*lo.rbegin()`
- `hi`: larger half
- `leftSum`: sum of `lo`
- `rightSum`: sum of `hi`

Balance rule:

```text
lo.size() == hi.size() OR lo.size() == hi.size() + 1
```

Cost:

```text
leftCost  = median * lo.size() - leftSum
rightCost = rightSum - median * hi.size()
totalCost = leftCost + rightCost
```

#### Example

```text
window = [1, 2, 10, 20, 30]
median = 10
```

Cost:

```text
|1-10| + |2-10| + |10-10| + |20-10| + |30-10|
= 9 + 8 + 0 + 10 + 20
= 47
```

With sets:

```text
lo = [1, 2, 10]
hi = [20, 30]
median = 10

leftCost = 10*3 - 13 = 17
rightCost = 50 - 10*2 = 30
total = 47
```

#### C++

```cpp
#include <bits/stdc++.h>
using namespace std;

struct SlidingCost {
    multiset<long long> lo, hi;
    long long leftSum = 0, rightSum = 0;

    long long median() const {
        return *lo.rbegin();
    }

    void rebalance() {
        while (lo.size() < hi.size()) {
            auto it = hi.begin();
            long long x = *it;
            hi.erase(it);
            rightSum -= x;
            lo.insert(x);
            leftSum += x;
        }

        while (lo.size() > hi.size() + 1) {
            auto it = prev(lo.end());
            long long x = *it;
            lo.erase(it);
            leftSum -= x;
            hi.insert(x);
            rightSum += x;
        }
    }

    void insert(long long x) {
        if (lo.empty() || x <= median()) {
            lo.insert(x);
            leftSum += x;
        } else {
            hi.insert(x);
            rightSum += x;
        }
        rebalance();
    }

    void erase(long long x) {
        auto itLo = lo.find(x);
        if (itLo != lo.end()) {
            lo.erase(itLo);
            leftSum -= x;
        } else {
            auto itHi = hi.find(x);
            hi.erase(itHi);
            rightSum -= x;
        }
        rebalance();
    }

    long long cost() const {
        long long m = median();
        long long leftCost = m * (long long)lo.size() - leftSum;
        long long rightCost = rightSum - m * (long long)hi.size();
        return leftCost + rightCost;
    }
};
```

#### Dry Run And ASCII Flow

##### Dry Run: Median cost using two multisets

Input:

```text
window = [1, 2, 10, 20, 30]
```

| Set | Values | Sum |
|---|---|---:|
| lo | `1, 2, 10` | 13 |
| hi | `20, 30` | 50 |

| Step | Formula | Value |
|---|---|---:|
| median | max of lo | 10 |
| left cost | `10 * 3 - 13` | 17 |
| right cost | `50 - 10 * 2` | 30 |
| total | `17 + 30` | 47 |

##### ASCII Dry Run Diagram

```text
ASCII FLOW

Insert or erase value
  |
  v
Put in lo or hi


  |
  v
Rebalance sizes


  |
  v
Median is max of lo


  |
  v
Compute left cost


  |
  v
Compute right cost


  |
  v
Return total cost
```


#### One-minute mental trick

```text
Minimize sum of absolute differences:
    choose median

Minimize sum of squared differences:
    choose mean

Dynamic median:
    two multisets / two heaps
```

---

### 5. Mean, Variance, Median, Mode Dashboard

Design a dynamic structure supporting:

- `insert(x)`
- `remove(x)`
- `mean()`
- `variance()`
- `median()`
- `mode()`

```text
ASCII FLOW

Data Dashboard
  |
  v
insert x


  |
  v
remove x


  |
  v
mean


  |
  v
variance


  |
  v
median


  |
  v
mode


  |
  v
Update sum squareSum frequency halves


  |
  v
```

#### Intuition

Different statistics need different maintained information.

| Query | What to maintain |
|---|---|
| mean | `sum`, `count` |
| variance | `sum`, `squareSum`, `count` |
| median | two balanced multisets |
| mode | frequency map + ordered frequency set |

#### Formulas

```text
mean = sum / count
variance = (sum of squares / count) - mean^2
mode = element with highest frequency
median = middle value after sorting
```

#### Example

Values:

```text
[1, 2, 2, 5]
```

Mean:

```text
(1+2+2+5)/4 = 2.5
```

Variance:

```text
squareSum = 1 + 4 + 4 + 25 = 34
variance = 34/4 - 2.5^2 = 8.5 - 6.25 = 2.25
```

Median:

```text
(2+2)/2 = 2
```

Mode:

```text
2
```

#### C++ structure

```cpp
#include <bits/stdc++.h>
using namespace std;

struct DataDashboard {
    long long sum = 0, squareSum = 0;
    int count = 0;

    map<int, int> freq;
    multiset<pair<int, int>> freqOrder; // {frequency, value}

    multiset<int> lo, hi; // median halves

    void rebalanceMedian() {
        while (lo.size() < hi.size()) {
            int x = *hi.begin();
            hi.erase(hi.begin());
            lo.insert(x);
        }
        while (lo.size() > hi.size() + 1) {
            int x = *lo.rbegin();
            lo.erase(prev(lo.end()));
            hi.insert(x);
        }
    }

    void addToMedian(int x) {
        if (lo.empty() || x <= *lo.rbegin()) lo.insert(x);
        else hi.insert(x);
        rebalanceMedian();
    }

    void removeFromMedian(int x) {
        auto itLo = lo.find(x);
        if (itLo != lo.end()) lo.erase(itLo);
        else hi.erase(hi.find(x));
        rebalanceMedian();
    }

    void insert(int x) {
        count++;
        sum += x;
        squareSum += 1LL * x * x;

        if (freq[x] > 0) freqOrder.erase(freqOrder.find({freq[x], x}));
        freq[x]++;
        freqOrder.insert({freq[x], x});

        addToMedian(x);
    }

    void remove(int x) {
        count--;
        sum -= x;
        squareSum -= 1LL * x * x;

        freqOrder.erase(freqOrder.find({freq[x], x}));
        freq[x]--;
        if (freq[x] > 0) freqOrder.insert({freq[x], x});
        else freq.erase(x);

        removeFromMedian(x);
    }

    double mean() const {
        return (double)sum / count;
    }

    double variance() const {
        double mu = mean();
        return (double)squareSum / count - mu * mu;
    }

    double median() const {
        if (count % 2 == 1) return *lo.rbegin();
        return (*lo.rbegin() + *hi.begin()) / 2.0;
    }

    int mode() const {
        return freqOrder.rbegin()->second;
    }
};
```

#### Dry Run And ASCII Flow

##### Dry Run: Statistics dashboard updates

Input:

```text
insert 1, insert 2, insert 2, insert 5
```

| Operation | Count | Sum | Square sum | Mode |
|---|---:|---:|---:|---|
| insert 1 | 1 | 1 | 1 | 1 |
| insert 2 | 2 | 3 | 5 | 1 or 2 |
| insert 2 | 3 | 5 | 9 | 2 |
| insert 5 | 4 | 10 | 34 | 2 |

Final mean is `10 / 4 = 2.5`.
Final variance is `34 / 4 - 2.5 * 2.5 = 2.25`.

##### ASCII Dry Run Diagram

```text
ASCII FLOW

Insert or remove x
  |
  v
Update count


  |
  v
Update sum


  |
  v
Update square sum


  |
  v
Update frequency map


  |
  v
Update ordered frequency set


  |
  v
Update median halves


  |
  v
Queries are ready
```


#### One-minute mental trick

```text
mean      -> sum
variance  -> sum + squareSum
median    -> two halves
mode      -> frequency + max frequency
```

---

### 6. Prefix Sum and Subarray Sum Equals X

A subarray is a continuous segment of an array.

Number of subarrays in an array of size `n`:

```text
n * (n + 1) / 2
```

Using prefix sums:

```text
sum(l, r) = pref[r] - pref[l-1]
```

To count subarrays with sum `x`, for every `r` find how many previous prefix sums equal:

```text
pref[l-1] = pref[r] - x
```

```text
ASCII FLOW

Compute running prefix sum
  |
  v
Need previous prefix = current - x


  |
  v
Add frequency of current-x to answer


  |
  v
Store current prefix in map
```

#### Intuition

If:

```text
current prefix = sum from 0 to r
```

And we want a subarray ending at `r` with sum `x`, then the part before that subarray must be:

```text
current prefix - x
```

So we count how many times that previous prefix appeared.

#### Example

```text
a = [1, 2, 3, -2, 5]
x = 3
```

Subarrays with sum 3:

```text
[1,2]
[3]
[2,3,-2]
[-2,5]
```

Answer:

```text
4
```

#### C++ count only

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countSubarraysWithSumX(vector<int>& a, long long x) {
    map<long long, long long> freq;
    freq[0] = 1;

    long long pref = 0, ans = 0;

    for (int v : a) {
        pref += v;
        ans += freq[pref - x];
        freq[pref]++;
    }

    return ans;
}
```

#### Dry Run And ASCII Flow

##### Dry Run: Count subarrays with target sum

Input:

```text
a = [1, 2, 3, -2, 5], x = 3
```

| i | value | prefix | need | previous need count | answer |
|---:|---:|---:|---:|---:|---:|
| 0 | 1 | 1 | -2 | 0 | 0 |
| 1 | 2 | 3 | 0 | 1 | 1 |
| 2 | 3 | 6 | 3 | 1 | 2 |
| 3 | -2 | 4 | 1 | 1 | 3 |
| 4 | 5 | 9 | 6 | 1 | 4 |

##### ASCII Dry Run Diagram

```text
ASCII FLOW

Initialize frequency of zero prefix
  |
  v
Add value to prefix


  |
  v
Need equals prefix minus target


  |
  v
Add frequency of need


  |
  v
Store current prefix


  |
  v
More elements?


  | Yes
  v


  | No
  v
Return answer
```


#### C++ print all ranges

```cpp
#include <bits/stdc++.h>
using namespace std;

void printSubarraysWithSumX(vector<int>& a, long long x) {
    map<long long, vector<int>> pos;
    pos[0].push_back(-1);

    long long pref = 0;

    for (int i = 0; i < (int)a.size(); i++) {
        pref += a[i];

        long long need = pref - x;
        for (int leftMinusOne : pos[need]) {
            cout << "[" << leftMinusOne + 1 << ", " << i << "]\n";
        }

        pos[pref].push_back(i);
    }
}
```

#### Dry Run And ASCII Flow

##### Dry Run: Print all target-sum ranges

Input:

```text
a = [1, 2, 3], x = 3
```

| i | value | prefix | need | positions for need | printed range |
|---:|---:|---:|---:|---|---|
| 0 | 1 | 1 | -2 | none | none |
| 1 | 2 | 3 | 0 | `-1` | `[0,1]` |
| 2 | 3 | 6 | 3 | `1` | `[2,2]` |

##### ASCII Dry Run Diagram

```text
ASCII FLOW

Keep map from prefix to positions
  |
  v
Update prefix


  |
  v
Find need prefix


  |
  v
Need exists?


  | Yes
  v
Print all ranges


  | No
  v
Print none


  |
  v
Store current index


  |
  v
```


#### CM + FAANG edge cases

```text
Initialize freq[0] = 1, otherwise subarrays starting at index 0 are missed.
Use long long for prefix.
If all numbers are positive, two pointers may work; with negatives, use prefix+map.
For modulo subarray problems, store prefix % mod carefully: (x % m + m) % m.
```

#### One-minute mental trick

```text
Subarray sum:
    prefix sum

Count subarray sum = x:
    map previous prefixes

All positive numbers only:
    sliding window may work

Negative numbers allowed:
    prefix + map is safer
```

---

### 7. Stack Mastery Next Greater Element

For each index, find the next greater element to the right.

Use a monotonic stack of indices. Traverse from right to left.

```text
ASCII FLOW

Start from right
  |
  v
top value <= current??


  | yes
  v
pop


  |
  v


  | no
  v
stack empty??


  | yes
  v
nge of i = n


  | no
  v
nge of i = top


  |
  v
push i


  |
  v


  |
  v
```

#### Intuition

When scanning from right to left, the stack stores useful candidates for the next greater element.

If a candidate is less than or equal to the current value, it can never be the next greater for this or any earlier element blocked by current.

#### Example

```text
a = [2, 1, 3, 2]
```

| i | value | next greater |
|---:|---:|---:|
| 0 | 2 | 3 |
| 1 | 1 | 3 |
| 2 | 3 | none |
| 3 | 2 | none |

#### C++

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> nextGreaterIndex(vector<int>& a) {
    int n = a.size();
    vector<int> nge(n, n);
    stack<int> st;

    for (int i = n - 1; i >= 0; i--) {
        while (!st.empty() && a[st.top()] <= a[i]) {
            st.pop();
        }

        if (!st.empty()) nge[i] = st.top();

        st.push(i);
    }

    return nge;
}
```

#### Dry Run And ASCII Flow

##### Dry Run: Next greater element

Input:

```text
a = [2, 1, 3, 2]
```

| i | value | Stack before | Action | Next greater |
|---:|---:|---|---|---|
| 3 | 2 | empty | push 2 | none |
| 2 | 3 | 2 | pop 2, push 3 | none |
| 1 | 1 | 3 | top is greater, push 1 | 3 |
| 0 | 2 | 3, 1 | pop 1, top is greater, push 2 | 3 |

##### ASCII Dry Run Diagram

```text
ASCII FLOW

Scan from right
  |
  v
Current value


  |
  v
Top less or equal current?


  | Yes
  v
Pop stack


  |
  v


  | No
  v
Stack empty?


  | Yes
  v
No next greater


  | No
  v
Top is answer


  |
  v
Push current


  |
  v
```


#### CM + FAANG edge cases

```text
Greater vs greater-or-equal changes pop condition.
Circular next greater: scan 2n times and use i % n.
Distance answer: store indices, not values.
Value answer: convert index to value at the end.
```

#### One-minute mental trick

```text
Next greater/smaller:
    monotonic stack

To the right:
    scan right to left

To the left:
    scan left to right

Greater:
    pop smaller/equal

Smaller:
    pop greater/equal
```

---

### 8. Trapping Rain Water

For each bar, trapped water depends on boundary bars.

#### Stack approach

When current bar is greater than the stack top, the popped bar can become the bottom of trapped water.

```text
ASCII FLOW

Loop i from 0 to n-1
  |
  v
top height < current height??


  | yes
  v
bottom = pop


  |
  v
stack empty??


  | yes
  v
break


  | no
  v
left = stack.top


  |
  v
width = i-left-1


  |
  v
bounded height = min left current - bottom


  |
  v
ans += width*height


  |
  v


  | no
  v
push i
```

#### Intuition

Water is trapped when we find:

```text
left wall + bottom + right wall
```

The current bar acts as the right wall. The stack top after popping acts as the left wall.

#### Example

```text
height = [3, 0, 2, 0, 4]
```

Water trapped:

```text
index 1: min(3,4)-0 = 3
index 2: min(3,4)-2 = 1
index 3: min(3,4)-0 = 3
total = 7
```

#### C++

```cpp
#include <bits/stdc++.h>
using namespace std;

int trapRainWater(vector<int>& h) {
    int n = h.size();
    int ans = 0;
    stack<int> st;

    for (int i = 0; i < n; i++) {
        while (!st.empty() && h[st.top()] < h[i]) {
            int bottom = st.top();
            st.pop();

            if (st.empty()) break;

            int left = st.top();
            int width = i - left - 1;
            int height = min(h[left], h[i]) - h[bottom];
            ans += width * height;
        }
        st.push(i);
    }

    return ans;
}
```

#### Dry Run And ASCII Flow

##### Dry Run: Water trapped by stack

Input:

```text
height = [3, 0, 2, 0, 4]
```

| i | height | Important action | Water added |
|---:|---:|---|---:|
| 0 | 3 | push index 0 | 0 |
| 1 | 0 | push index 1 | 0 |
| 2 | 2 | pop bottom index 1 | 2 |
| 3 | 0 | push index 3 | 0 |
| 4 | 4 | pop bottoms and use left wall | 5 |

Total water is `7`.

##### ASCII Dry Run Diagram

```text
ASCII FLOW

Read current bar
  |
  v
Current higher than stack top?


  | Yes
  v
Pop bottom


  |
  v
Stack empty?


  | Yes
  v
Stop inner loop


  | No
  v
New top is left wall


  |
  v
Compute width


  |
  v
Compute bounded height


  |
  v
Add water


  |
  v


  | No
  v
Push current index
```


#### One-minute mental trick

```text
Water needs two walls.

When current height is bigger than stack top:
    current is right wall
    popped is bottom
    new stack top is left wall
```

---

### 9. Range Mapping / Interval Coverage

Queries:

1. Insert range `[l, r]`
2. Check whether point `x` is covered by any range

#### Offline version

Store all left endpoints and right endpoints separately, then sort.

For point `x`:

```text
covered intervals = total intervals - intervals ending before x - intervals starting after x
```

```text
ending before x = count(r < x)
starting after x = count(l > x)
```

Use `lower_bound` and `upper_bound`.

#### Intuition

An interval covers `x` if:

```text
l <= x <= r
```

So an interval does not cover `x` if:

```text
r < x
```

or:

```text
l > x
```

#### C++

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isCoveredOffline(vector<pair<int,int>>& ranges, int x) {
    vector<int> L, R;

    for (auto [l, r] : ranges) {
        L.push_back(l);
        R.push_back(r);
    }

    sort(L.begin(), L.end());
    sort(R.begin(), R.end());

    int n = ranges.size();
    int startingAfter = n - (upper_bound(L.begin(), L.end(), x) - L.begin());
    int endingBefore = lower_bound(R.begin(), R.end(), x) - R.begin();

    return n - startingAfter - endingBefore > 0;
}
```

#### Dry Run And ASCII Flow

##### Dry Run: Offline interval coverage

Input:

```text
ranges = [1,3], [6,8], x = 2
```

| Data | Values |
|---|---|
| sorted starts | `1, 6` |
| sorted ends | `3, 8` |
| intervals starting after 2 | 1 |
| intervals ending before 2 | 0 |
| covered count | `2 - 1 - 0 = 1` |

Result: covered.

##### ASCII Dry Run Diagram

```text
ASCII FLOW

Sort starts and ends
  |
  v
Query point x


  |
  v
Count starts greater than x


  |
  v
Count ends less than x


  |
  v
Covered count equals total minus both


  |
  v
Covered count positive?


  | Yes
  v
Covered


  | No
  v
Not covered
```


#### Online version: maintain merged intervals

Use `set<pair<int,int>>` containing non-overlapping intervals.

```text
ASCII FLOW

Insert l,r
  |
  v
Already covered??


  | yes
  v
Do nothing


  | no
  v
Find intervals that overlap


  |
  v
Merge them into l,r


  |
  v
Erase old intervals


  |
  v
Insert merged interval

Query x
  |
  v
Find interval with start <= x


  |
  v
end >= x??


  | yes
  v
Covered


  | no
  v
Not covered
```

#### Example

Insert:

```text
[1, 3]
[6, 8]
[2, 7]
```

After merging:

```text
[1, 8]
```

Query:

```text
x = 5 -> covered
x = 9 -> not covered
```

#### C++

```cpp
#include <bits/stdc++.h>
using namespace std;

struct RangeCover {
    set<pair<int,int>> ranges;

    bool covered(int x) {
        auto it = ranges.upper_bound({x, INT_MAX});
        if (it == ranges.begin()) return false;
        --it;
        return it->second >= x;
    }

    void insertRange(int l, int r) {
        auto it = ranges.lower_bound({l, INT_MIN});

        if (it != ranges.begin()) {
            auto prevIt = prev(it);
            if (prevIt->second >= l - 1) it = prevIt;
        }

        while (it != ranges.end() && it->first <= r + 1) {
            l = min(l, it->first);
            r = max(r, it->second);
            it = ranges.erase(it);
        }

        ranges.insert({l, r});
    }
};
```

#### Dry Run And ASCII Flow

##### Dry Run: Merged interval insert and query

Input:

```text
insert [1,3], insert [6,8], insert [2,7], query 5
```

| Operation | Intervals after operation |
|---|---|
| insert `[1,3]` | `[1,3]` |
| insert `[6,8]` | `[1,3] [6,8]` |
| insert `[2,7]` | `[1,8]` |
| query `5` | covered |

##### ASCII Dry Run Diagram

```text
ASCII FLOW

Insert range
  |
  v
Find overlap candidate


  |
  v
Overlaps?


  | Yes
  v
Merge boundaries


  |
  v
Erase old interval


  |
  v


  | No
  v
Insert merged interval

Query point
  |
  v
Find previous interval


  |
  v
End covers point?


  | Yes
  v
Covered


  | No
  v
Not covered
```


#### One-minute mental trick

```text
Static intervals + many point queries:
    sort starts and ends

Dynamic insert + point query:
    set of merged intervals

Need range add/query:
    difference array / segment tree
```

---

### 10. Top K Sum

Maintain sum of top `k` elements dynamically.

Two common implementations:

1. Priority queue: good when only removing min/max from top side.
2. Two multisets: better when arbitrary `remove(x)` is needed.

#### Two multiset method

- `top`: contains the largest `k` elements.
- `rest`: contains the overflow elements.
- `sumTop`: sum of elements in `top`.

```text
ASCII FLOW

Insert x
  |
  v
Put into top


  |
  v
Balance


  |
  v
top size > k??


  | yes
  v
Move smallest top to rest


  | no
  v
top size < k and rest nonempty??


  | yes
  v
Move largest rest to top


  | no
  v
Done


  |
  v


  |
  v
```

#### Intuition

Always keep the largest `k` values in one bucket.

If a new big value enters, a smaller value may be pushed out.

#### Example

```text
k = 3
values = [5, 1, 10, 3, 8]
```

Top 3:

```text
10, 8, 5
```

Sum:

```text
23
```

#### C++

```cpp
#include <bits/stdc++.h>
using namespace std;

struct TopKSum {
    int k;
    long long sumTop = 0;
    multiset<int> top, rest;

    TopKSum(int k) : k(k) {}

    void balance() {
        while ((int)top.size() > k) {
            auto it = top.begin();
            int x = *it;
            sumTop -= x;
            top.erase(it);
            rest.insert(x);
        }

        while ((int)top.size() < k && !rest.empty()) {
            auto it = prev(rest.end());
            int x = *it;
            rest.erase(it);
            top.insert(x);
            sumTop += x;
        }

        while (!top.empty() && !rest.empty() && *top.begin() < *rest.rbegin()) {
            int smallTop = *top.begin();
            int bigRest = *rest.rbegin();

            top.erase(top.begin());
            rest.erase(prev(rest.end()));

            top.insert(bigRest);
            rest.insert(smallTop);

            sumTop += bigRest - smallTop;
        }
    }

    void insert(int x) {
        top.insert(x);
        sumTop += x;
        balance();
    }

    void remove(int x) {
        auto itTop = top.find(x);
        if (itTop != top.end()) {
            top.erase(itTop);
            sumTop -= x;
        } else {
            auto itRest = rest.find(x);
            if (itRest != rest.end()) rest.erase(itRest);
        }
        balance();
    }

    long long getSum() const {
        return sumTop;
    }
};
```

#### Dry Run And ASCII Flow

##### Dry Run: Maintain top k sum

Input:

```text
k = 3, insert values [5, 1, 10, 3, 8]
```

| Insert | top | rest | sumTop |
|---:|---|---|---:|
| 5 | `5` | empty | 5 |
| 1 | `1,5` | empty | 6 |
| 10 | `1,5,10` | empty | 16 |
| 3 | `3,5,10` | `1` | 18 |
| 8 | `5,8,10` | `1,3` | 23 |

##### ASCII Dry Run Diagram

```text
ASCII FLOW

Insert value
  |
  v
Put into top


  |
  v
Balance size


  |
  v
Top larger than k?


  | Yes
  v
Move smallest top to rest


  | No
  v
Rest has larger value?


  |
  v


  | Yes
  v
Swap smallest top and largest rest


  | No
  v
sumTop is answer


  |
  v
```


#### One-minute mental trick

```text
Need top k dynamically?
    top bucket + rest bucket

Need arbitrary erase?
    multiset

Only insert/extract max?
    priority_queue
```

---

### 11. Priority Queue Notes

Default C++ `priority_queue<int>` is a max heap.

```cpp
priority_queue<int> maxHeap;
```

For min heap:

```cpp
priority_queue<int, vector<int>, greater<int>> minHeap;
```

Trick: you can insert negative values in a max heap to simulate a min heap.

```cpp
priority_queue<int> pq;
pq.push(-x);      // insert x
int minimum = -pq.top();
```

Always check before popping:

```cpp
if (!pq.empty()) {
    pq.pop();
}
```

#### Intuition

A priority queue is useful when you only care about the current best item:

```text
largest
smallest
highest priority
lowest cost
```

It is not good when you need to erase arbitrary values unless you use lazy deletion.

#### Example use cases

| Problem | Heap type |
|---|---|
| kth largest | min heap of size k |
| Dijkstra shortest path | min heap |
| task scheduler | max heap |
| merge k sorted lists | min heap |

#### One-minute mental trick

```text
Need repeatedly best element?
    priority_queue

Need sorted traversal or arbitrary erase?
    set / multiset

Need both min and max?
    multiset
```

---

### 12. Stack and Queue Basics

#### Stack

LIFO: last in, first out.

```cpp
stack<int> st;
st.push(x);
st.pop();
int top = st.top();
bool empty = st.empty();
```

#### Queue

FIFO: first in, first out.

```cpp
queue<int> q;
q.push(x);
q.pop();
int front = q.front();
bool empty = q.empty();
```

#### Visual

```text
ASCII FLOW

3 top
2
1 bottom
1 front
  |
  v
2
```

#### Implement stack using two queues

Costly pop version:

```cpp
#include <bits/stdc++.h>
using namespace std;

struct StackUsingQueues {
    queue<int> q1, q2;

    void push(int x) {
        q1.push(x);
    }

    int pop() {
        while (q1.size() > 1) {
            q2.push(q1.front());
            q1.pop();
        }
        int ans = q1.front();
        q1.pop();
        swap(q1, q2);
        return ans;
    }

    int top() {
        while (q1.size() > 1) {
            q2.push(q1.front());
            q1.pop();
        }
        int ans = q1.front();
        q2.push(ans);
        q1.pop();
        swap(q1, q2);
        return ans;
    }

    bool empty() const {
        return q1.empty();
    }
};
```

#### Dry Run And ASCII Flow

##### Dry Run: Stack using two queues

Input:

```text
push 1, push 2, push 3, pop
```

| Step | q1 | q2 | Action |
|---|---|---|---|
| start | `1,2,3` | empty | need pop |
| move | `3` | `1,2` | move until one left |
| pop | empty | `1,2` | pop 3 |
| swap | `1,2` | empty | restore q1 |

Returned value is `3`.

##### ASCII Dry Run Diagram

```text
ASCII FLOW

Pop stack using queues
  |
  v
q1 size greater than one?


  | Yes
  v
Move front from q1 to q2


  |
  v


  | No
  v
Remaining front is stack top


  |
  v
Pop it


  |
  v
Swap q1 and q2


  |
  v
Return value
```


#### One-minute mental trick

```text
Stack:
    reverse order
    nested structures
    monotonic problems

Queue:
    process in arrival order
    BFS
    level order
```

---

### 13. Contribution Technique

Instead of enumerating every subarray/subsequence, calculate how much each element contributes to the final answer.

```text
ASCII MIND MAP

  └─ Contribution Technique
    └─ of all subarrays
    └─ of all subsequences
    └─ contribution
      └─ counts
    └─ contribution
      └─ of subarrays
```

#### Intuition

When too many objects exist, count contribution from each element instead.

Instead of:

```text
For every subarray, add all elements
```

Think:

```text
For every element, count how many subarrays contain it
```

#### Sum of all subarrays

Element `a[i]` appears in:

```text
(i + 1) * (n - i)
```

subarrays.

So:

```text
answer = Σ a[i] * (i + 1) * (n - i)
```

#### Example

```text
a = [1, 2, 3]
```

All subarrays:

```text
[1]       sum 1
[1,2]     sum 3
[1,2,3]   sum 6
[2]       sum 2
[2,3]     sum 5
[3]       sum 3
total = 20
```

Contribution:

```text
1 appears 3 times -> 1*3 = 3
2 appears 4 times -> 2*4 = 8
3 appears 3 times -> 3*3 = 9
total = 20
```

#### C++

```cpp
long long sumOfAllSubarrays(vector<int>& a) {
    long long ans = 0;
    int n = a.size();

    for (int i = 0; i < n; i++) {
        ans += 1LL * a[i] * (i + 1) * (n - i);
    }

    return ans;
}
```

#### Dry Run And ASCII Flow

##### Dry Run: Sum of all subarrays by contribution

Input:

```text
a = [1, 2, 3]
```

| i | value | left choices | right choices | contribution |
|---:|---:|---:|---:|---:|
| 0 | 1 | 1 | 3 | 3 |
| 1 | 2 | 2 | 2 | 8 |
| 2 | 3 | 3 | 1 | 9 |

Total is `20`.

##### ASCII Dry Run Diagram

```text
ASCII FLOW

Pick index i
  |
  v
Count left choices


  |
  v
Count right choices


  |
  v
Multiply by value


  |
  v
Add to answer


  |
  v
More elements?


  | Yes
  v


  | No
  v
Return total
```


#### Sum of all subsequences

Each element appears in `2^(n-1)` subsequences.

```text
answer = Σ a[i] * 2^(n-1)
```

```cpp
long long sumOfAllSubsequences(vector<int>& a) {
    int n = a.size();
    long long ways = 1LL << (n - 1); // only safe for small n

    long long ans = 0;
    for (int x : a) ans += x * ways;

    return ans;
}
```

#### Dry Run And ASCII Flow

##### Dry Run: Sum of all subsequences

Input:

```text
a = [1, 2, 3]
```

| Element | Appears in how many subsequences | Contribution |
|---:|---:|---:|
| 1 | 4 | 4 |
| 2 | 4 | 8 |
| 3 | 4 | 12 |

Total is `24`.

##### ASCII Dry Run Diagram

```text
ASCII FLOW

For each element
  |
  v
Each appears in power of two choices


  |
  v
Multiply element by ways


  |
  v
Add contribution


  |
  v
Return total
```


#### Product of all subarrays sum

For each ending position, maintain sum of products of all subarrays ending there.

```text
sop = sop * a[i] + a[i]
ans += sop
```

#### Example

```text
a = [2, 3, 4]
```

At `2`:

```text
subarrays ending here: [2]
sop = 2
```

At `3`:

```text
previous products extended by 3: [2,3] product 6
new subarray: [3] product 3
sop = 6 + 3 = 9
```

At `4`:

```text
extend previous ending products by 4: 9*4 = 36
new [4] = 4
sop = 40
```

Total:

```text
2 + 9 + 40 = 51
```

#### C++

```cpp
long long sumProductOfAllSubarrays(vector<int>& a) {
    long long ans = 0;
    long long sop = 0;

    for (long long x : a) {
        sop = sop * x + x;
        ans += sop;
    }

    return ans;
}
```

#### Dry Run And ASCII Flow

##### Dry Run: Sum product of all subarrays

Input:

```text
a = [2, 3, 4]
```

| x | Previous sop | New sop | Answer |
|---:|---:|---:|---:|
| 2 | 0 | 2 | 2 |
| 3 | 2 | 9 | 11 |
| 4 | 9 | 40 | 51 |

Formula: `sop = sop * x + x`.

##### ASCII Dry Run Diagram

```text
ASCII FLOW

Read x
  |
  v
Extend previous products


  |
  v
Add new single element subarray


  |
  v
Update sop


  |
  v
Add sop to answer


  |
  v
More elements?


  | Yes
  v


  | No
  v
Return answer
```


#### One-minute mental trick

```text
Too many subarrays/subsequences?
    don't generate them

Ask:
    how many times does this element/pair contribute?
```

---

### 14. Pattern Matching / Coordinate Geometry Printing

For star-pattern questions, first define the canvas:

- rows
- columns
- coordinates `(i, j)`

Then write a function deciding whether a coordinate prints `*` or space.

```text
ASCII FLOW

Pattern Question
  |
  v
Define canvas rows x cols


  |
  v
Loop over i rows


  |
  v
Loop over j cols


  |
  v
func i,j true??


  | yes
  v
print star


  | no
  v
print space
```

#### Intuition

Pattern problems are coordinate geometry problems.

Instead of guessing spaces, ask:

```text
At coordinate (i, j), should I print star?
```

#### Template

```cpp
#include <bits/stdc++.h>
using namespace std;

bool printStar(int i, int j, int rows, int cols) {
    // Example: main diagonal
    return i == j;
}

int main() {
    int rows = 5, cols = 5;

    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            cout << (printStar(i, j, rows, cols) ? '*' : ' ');
        }
        cout << '\n';
    }
}
```

#### Dry Run And ASCII Flow

##### Dry Run: Coordinate based star printing

Input:

```text
rows = 5, cols = 5, condition i equals j
```

| Cell | Condition | Printed |
|---|---|---|
| `(0,0)` | true | star |
| `(0,1)` | false | space |
| `(1,1)` | true | star |
| `(2,2)` | true | star |

##### ASCII Dry Run Diagram

```text
ASCII FLOW

Loop row i
  |
  v
Loop column j


  |
  v
Condition true?


  | Yes
  v
Print star


  | No
  v
Print space


  |
  v
More columns?


  |
  v


  | Yes
  v


  | No
  v
Next row
```


Output:

```text
*
 *
  *
   *
    *
```

#### Common conditions

| Pattern | Condition |
|---|---|
| main diagonal | `i == j` |
| anti diagonal | `i + j == n - 1` |
| border | `i == 0 || j == 0 || i == n-1 || j == m-1` |
| upper triangle | `i <= j` |
| lower triangle | `i >= j` |

#### Repeating columns

To repeat after every `k` columns, use modulo:

```cpp
func(i, j % k, rows, k);
```

#### One-minute mental trick

```text
Pattern printing:
    stop thinking in spaces
    think in coordinates

Ask:
    when should this cell be star?
```

---

### 15. Molecular Formula Parser

Problem type: parse chemical formula like:

```text
K4(ON(SO3)2)2
```

Expected output is sorted by element names with counts.

#### Key ideas

- Use recursion for bracketed subproblems.
- Use `map<string,int>` because final output needs sorted keys.
- Parse element names: capital letter followed by lowercase letters.
- Parse number after element/bracket; default count is `1`.
- Merge maps by adding counts.

```text
ASCII FLOW

parse range l..r
  |
  v
Current char?


  | Capital letter
  v
Parse element


  |
  v
Parse number if any


  |
  v
Add to map


  | open parenthesis
  v
Find matching bracket


  |
  v
Parse inside recursively


  |
  v
Parse multiplier


  |
  v
Multiply inside map


  |
  v
Merge


  |
  v


  |
  v
```

#### Intuition

Parentheses create a smaller independent formula. Solve inside first, then multiply.

Example:

```text
Mg(OH)2
```

Inside parentheses:

```text
O:1, H:1
```

Multiplier:

```text
2
```

After multiplying:

```text
O:2, H:2
```

Add outside:

```text
Mg:1
```

Final:

```text
H2MgO2
```

#### C++ parser

```cpp
#include <bits/stdc++.h>
using namespace std;

class FormulaParser {
public:
    string s;
    int n;

    int readNumber(int& i) {
        int num = 0;
        while (i < n && isdigit(s[i])) {
            num = num * 10 + (s[i] - '0');
            i++;
        }
        return num == 0 ? 1 : num;
    }

    string readElement(int& i) {
        string name;
        name += s[i++]; // capital letter
        while (i < n && islower(s[i])) {
            name += s[i++];
        }
        return name;
    }

    map<string, int> parse(int& i) {
        map<string, int> result;

        while (i < n && s[i] != ')') {
            if (isupper(s[i])) {
                string element = readElement(i);
                int count = readNumber(i);
                result[element] += count;
            } else if (s[i] == '(') {
                i++; // skip '('
                auto inside = parse(i);
                i++; // skip ')'
                int multiplier = readNumber(i);

                for (auto [element, count] : inside) {
                    result[element] += count * multiplier;
                }
            }
        }

        return result;
    }

    string countOfAtoms(string formula) {
        s = formula;
        n = s.size();
        int i = 0;
        auto counts = parse(i);

        string ans;
        for (auto [element, count] : counts) {
            ans += element;
            if (count > 1) ans += to_string(count);
        }
        return ans;
    }
};
```

#### Dry Run And ASCII Flow

##### Dry Run: Parse chemical formula

Input:

```text
formula = Mg(OH)2
```

| Step | Token | Action | Count map |
|---:|---|---|---|
| 1 | `Mg` | add element | `Mg:1` |
| 2 | `(` | parse inside | inside map starts |
| 3 | `O` | add inside | `O:1` |
| 4 | `H` | add inside | `O:1 H:1` |
| 5 | `)2` | multiply inside | `O:2 H:2` |
| 6 | merge | merge maps | `H:2 Mg:1 O:2` |

##### ASCII Dry Run Diagram

```text
ASCII FLOW

Start parse
  |
  v
Current character?


  | Capital
  v
Read element


  |
  v
Read number


  |
  v
Add count


  | Open parenthesis
  v
Parse inside recursively


  |
  v
Read multiplier


  |
  v
Multiply inside map


  |
  v
Merge map


  |
  v
More characters?


  |
  v


  | Yes
  v


  | No
  v
Return map
```


#### One-minute mental trick

```text
Nested parentheses/brackets:
    recursion or stack

Need sorted output:
    map

Default count:
    if no number, count = 1
```

---


### 15A. Largest Rectangle in Histogram

Find largest rectangle area in histogram.

#### Core Idea

For every bar:

```text
height = current bar
width = nearest smaller on left/right
```

Use monotonic increasing stack.

---

#### Invariant

```text
Stack always stores increasing heights.
```

---

#### ASCII Flow

```text
ASCII FLOW

Loop through bars
  |
  v
Current height smaller than stack top?


  | Yes
  v
Pop height


  |
  v
Calculate width


  |
  v
Update max area


  |
  v


  | No
  v
Push current index
```

---

#### Formula

For popped index:

```text
height = h[idx]
right = current index
left = new stack top
width = right - left - 1
area = height * width
```

---

#### Example

```text
heights = [2,1,5,6,2,3]
```

Maximum rectangle:

```text
5 x 2 = 10
```

---

#### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int largestRectangleArea(vector<int>& h) {
    stack<int> st;
    int n = h.size();
    int ans = 0;

    for (int i = 0; i <= n; i++) {
        int cur = (i == n ? 0 : h[i]);

        while (!st.empty() && h[st.top()] > cur) {
            int height = h[st.top()];
            st.pop();

            int right = i;
            int left = st.empty() ? -1 : st.top();

            int width = right - left - 1;

            ans = max(ans, height * width);
        }

        st.push(i);
    }

    return ans;
}
```

---

#### Dry Run

| i | Current | Stack | Action | Max Area |
|---|---|---|---|---|
| 0 | 2 | 0 | push | 2 |
| 1 | 1 | empty | pop 2 | 2 |
| 2 | 5 | 1,2 | push | 5 |
| 3 | 6 | 1,2,3 | push | 6 |
| 4 | 2 | 1 | pop 6, pop 5 | 10 |
| 5 | 3 | 1,4,5 | push | 10 |

---

#### Mental Trick

```text
Nearest smaller left/right
+
Monotonic increasing stack
=
Largest histogram rectangle
```


### 16. Choosing the Right STL

| Need | STL / Technique |
|---|---|
| LIFO | `stack` |
| FIFO | `queue` |
| min/max top only | `priority_queue` |
| sorted values + duplicates + arbitrary erase | `multiset` |
| key-value frequency | `map` / `unordered_map` |
| range coverage / merged intervals | `set<pair<int,int>>` |
| window min/max in O(n) | monotonic deque |
| next greater/smaller | monotonic stack |
| subarray sum equals x | prefix sum + map |
| median dynamically | two multisets |
| top k dynamically with remove | two multisets |

#### STL decision diagram — ASCII

```text
ASCII FLOW

What do you need?
  |
  v
Order matters??


  | LIFO
  v
stack


  | FIFO
  v
queue


  | sorted order
  v
set / multiset


  | top priority only
  v
priority_queue


  | key-value count
  v
map / unordered_map


  | range query/update
  v
Fenwick / Segment Tree


  | window min/max
  v
monotonic deque
```

#### One-minute mental trick

```text
Can I delete arbitrary element?
    yes -> set/multiset
    no  -> heap may work

Need order statistics?
    PBDS / Fenwick / segment tree

Need frequency?
    map

Need nearest greater/smaller?
    stack
```

---

### 17. Common Mistakes

- Using `mset.erase(x)` when you only want to erase one copy. Use `mset.erase(mset.find(x))`.
- Calling `top()`, `front()`, `back()`, `pop()` on an empty STL container.
- Forgetting `i + k` is usually exclusive in window ranges.
- For sliding window, forgetting to remove `arr[i-k]`.
- For prefix sums, forgetting to initialize `freq[0] = 1`.
- For maps/multisets, forgetting logarithmic complexity.
- In custom comparators, never return true for equal items; it may cause runtime issues.

#### Mistake examples

Wrong:

```cpp
multiset<int> ms = {5, 5, 5};
ms.erase(5); // removes all 5s
```

Correct:

```cpp
auto it = ms.find(5);
if (it != ms.end()) {
    ms.erase(it); // removes one 5
}
```

Wrong:

```cpp
stack<int> st;
cout << st.top(); // crash / undefined behavior
```

Correct:

```cpp
if (!st.empty()) {
    cout << st.top();
}
```

#### One-minute mental trick

```text
Before using STL top/front/back:
    check empty

Before erase in multiset:
    erase iterator, not value

Before prefix-map subarray count:
    freq[0] = 1
```

---

### 18. Final Revision Flow

```text
ASCII FLOW

New Problem
  |
  v
Is it about subarray/window??


  | fixed size k
  v
Sliding Window


  | sum x
  v
Prefix Sum + Map


  |
  v
Nearest greater/smaller??


  | yes
  v
Monotonic Stack


  |
  v
Dynamic median/top k??


  | median
  v
Two Multisets


  | top k
  v
Two Multisets / PQ


  |
  v
Intervals??


  | coverage
  v
Sorted endpoints or set of merged ranges


  |
  v
Nested formula/brackets??


  | yes
  v
Stack or Recursion


  |
  v
Too many subarrays/subsequences??


  | yes
  v
Contribution Technique
```

#### Quick pattern recognition table

| Problem clue | Think |
|---|---|
| fixed size k | sliding window |
| range sum query | prefix sum |
| subarray sum equals x | prefix sum + map |
| next greater/smaller | monotonic stack |
| min/max in every window | monotonic deque |
| dynamic median | two multisets |
| top k sum | two multisets |
| intervals merging | set of intervals |
| nested brackets/formula | stack / recursion |
| all subarrays/subsequences | contribution technique |

---

### 19. Minimal C++ Setup

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    // solve here

    return 0;
}
```

---

### 20. One-Minute CP Mental Checklist

Before coding, ask:

```text
1. What is n?
2. What is q?
3. Is data static or dynamic?
4. Are there range queries?
5. Are there updates?
6. Are values positive, negative, or mixed?
7. Is order important?
8. Do I need min/max/median/mode?
9. Can I process offline?
10. Which STL gives needed operations?
```

#### Final memory hook

```text
ASCII MIND MAP

  └─ CP Mental Tricks
    └─ sum
      └─ sum
    └─ update
      └─ array
    └─ range query
      └─ tree
      └─ window
    └─ greater
      └─ stack
      └─ multisets
    └─ K
      └─ multisets
      └─ endpoints
      └─ merged ranges
    └─ parsing
    └─ many objects
```

---

### 21. Final Golden Rules

```text
Start brute force.
Use constraints to reject brute force.
Name the pattern.
Choose STL by required operations.
Keep templates short.
Test edge cases.
Never trust first AC-looking code without dry run.
```

---

### 22. CM + FAANG Final Revision Grid

#### Pattern ladder

```text
800-1000  : vector, sort, map frequency, prefix sum basics
1100-1300 : stack/queue, two pointers, simple sliding window
1400-1600 : monotonic stack/deque, prefix+map, intervals
1700-1900 : two multisets, top-k dynamic, contribution, parser
1900+     : combine 2-3 patterns + edge-case heavy implementation
FAANG     : explain invariant clearly and write bug-free code fast
```

#### Fast decision tree

```text
Need best element only?
  ├─ arbitrary delete? yes -> multiset
  └─ no -> priority_queue

Need ordered unique values?
  ├─ duplicates? yes -> multiset
  └─ no -> set

Need frequency?
  ├─ ordered keys? yes -> map
  └─ speed only? -> unordered_map/custom_hash

Need window min/max?
  ├─ simplest accepted? -> multiset O(n log k)
  └─ optimal? -> monotonic deque O(n)

Need median/cost/top-k with delete?
  └─ two multisets / two buckets

Need all ranges/subarrays count?
  ├─ sum relation -> prefix + map
  └─ occurrence count -> contribution
```

#### Final contest checklist

```text
Before submit:
[ ] Did I use long long where multiplication/prefix appears?
[ ] Did I handle empty stack/queue/set before top/front/begin?
[ ] Did I erase one duplicate from multiset, not all duplicates?
[ ] Did I initialize prefix frequency with freq[0] = 1?
[ ] Did I test n = 1, k = 1, all equal, strictly increasing, strictly decreasing?
[ ] Did I verify 0-based vs 1-based index in output?
[ ] Did I understand strict < vs <= in monotonic logic?
[ ] Did I avoid O(n^2) when n can be 2e5?
```

#### One picture

```text
CP STL MASTERY

Array movement       -> sliding window -> deque/multiset/map
Range memory         -> prefix sum     -> map/frequency
Nearest boundary     -> mono stack     -> next greater/histogram/water
Dynamic order        -> multiset       -> median/top-k/intervals
Best next state      -> heap           -> Dijkstra/scheduler/kth
Nested structure     -> stack/parser   -> brackets/formula
Too many objects     -> contribution   -> count each element's role
```


---

# Part 2 — Monotonic Stack Phase-Wise Mastery


## Monotonic Stack Phase-Wise Practice Guide

### Clickable Index

- [How to Use This](#how-to-use-this)
- [Phase 1 — Recognition Problems](#phase-1--recognition-problems)
  - [1. Next Greater Element](#1-next-greater-element)
  - [2. Daily Temperatures](#2-daily-temperatures)
  - [3. Final Prices With a Special Discount](#3-final-prices-with-a-special-discount)
  - [4. Stock Span](#4-stock-span)
- [Phase 2 — Boundary / Range Thinking](#phase-2--boundary--range-thinking)
  - [5. Largest Rectangle in Histogram](#5-largest-rectangle-in-histogram)
  - [6. Maximal Rectangle](#6-maximal-rectangle)
  - [7. Height of Soldiers / Maximum of Minimum for Every Window Size](#7-height-of-soldiers--maximum-of-minimum-for-every-window-size)
- [Phase 3 — Contribution Thinking](#phase-3--contribution-thinking)
  - [8. Sum of Subarray Minimums](#8-sum-of-subarray-minimums)
  - [9. Maximum Subarray Min-Product](#9-maximum-subarray-min-product)
- [Phase 4 — Greedy + Monotonic Stack](#phase-4--greedy--monotonic-stack)
  - [10. Remove K Digits](#10-remove-k-digits)
  - [11. Remove Duplicate Letters](#11-remove-duplicate-letters)
- [Phase 5 — Advanced / Hidden Monotonic Stack](#phase-5--advanced--hidden-monotonic-stack)
  - [12. Trapping Rain Water](#12-trapping-rain-water)
  - [13. Maximum Width Ramp](#13-maximum-width-ramp)
  - [14. Number of Visible People in a Queue](#14-number-of-visible-people-in-a-queue)
  - [15. Steps to Make Array Non-Decreasing](#15-steps-to-make-array-non-decreasing)
- [Final Phase Summary](#final-phase-summary)
- [Master Rule](#master-rule)
- [Final Checklist Before Coding](#final-checklist-before-coding)

---

### How to Use This

```text
Phase 1 -> learn stack direction and pop condition
Phase 2 -> learn boundary/range thinking
Phase 3 -> learn contribution thinking
Phase 4 -> learn greedy + stack
Phase 5 -> learn advanced hidden stack forms
```

---

## Phase 1 — Recognition Problems

Goal:

```text
Recognize when current element resolves previous unresolved elements.
```

Core question:

```text
What is the stack waiting for?
```

---

### 1. Next Greater Element

#### Problem Idea

For every element, find the first greater element on the right.

Example:

```text
nums = [2, 1, 5, 3]
ans  = [5, 5, -1, -1]
```

#### Pattern

```text
Stack type: decreasing stack
Pop when : current > stack top
Meaning  : current is next greater for popped elements
```

#### Thinking

```text
Small values are waiting for a bigger value.
When bigger value comes, it resolves them.
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> nextGreater(vector<int>& nums) {
    int n = nums.size();
    vector<int> ans(n, -1);
    stack<int> st; // store indices

    for (int i = 0; i < n; i++) {
        while (!st.empty() && nums[i] > nums[st.top()]) {
            ans[st.top()] = nums[i];
            st.pop();
        }
        st.push(i);
    }

    return ans;
}
```
#### Dry Run — Array Diagram

```text
nums = [2, 1, 5, 3]
stack stores index:value
```

##### i = 0, current = 2

```text
index:   0   1   2   3
nums:   [2,  1,  5,  3]
         ^

stack before: []
action      : push 0
stack after : [0:2]
answer      : [-1, -1, -1, -1]
```

##### i = 1, current = 1

```text
index:   0   1   2   3
nums:   [2,  1,  5,  3]
             ^

stack before: [0:2]
condition   : 1 > 2? no
action      : push 1
stack after : [0:2, 1:1]
answer      : [-1, -1, -1, -1]
```

##### i = 2, current = 5

```text
index:   0   1   2   3
nums:   [2,  1,  5,  3]
                 ^

stack before: [0:2, 1:1]

5 > 1 -> pop 1 -> answer[1] = 5
5 > 2 -> pop 0 -> answer[0] = 5

action      : push 2
stack after : [2:5]
answer      : [5, 5, -1, -1]
```

Visual:

```text
[2, 1, 5, 3]
 ^  ^  |
 |  |  +-- resolves both 2 and 1
 +-- answer = 5
    +-- answer = 5
```

##### i = 3, current = 3

```text
index:   0   1   2   3
nums:   [2,  1,  5,  3]
                     ^

stack before: [2:5]
condition   : 3 > 5? no
action      : push 3
stack after : [2:5, 3:3]
answer      : [5, 5, -1, -1]
```

#### Dry Run After Code

```text
nums = [2, 1, 5, 3]
ans  = [-1, -1, -1, -1]
stack stores indices, values are decreasing from bottom to top

i = 0, nums[i] = 2
stack = []
push 0
stack = [0:2]
ans = [-1, -1, -1, -1]

i = 1, nums[i] = 1
1 > 2? no
push 1
stack = [0:2, 1:1]
ans = [-1, -1, -1, -1]

i = 2, nums[i] = 5
5 > 1 -> pop index 1 -> ans[1] = 5
5 > 2 -> pop index 0 -> ans[0] = 5
push 2
stack = [2:5]
ans = [5, 5, -1, -1]

i = 3, nums[i] = 3
3 > 5? no
push 3
stack = [2:5, 3:3]
ans = [5, 5, -1, -1]
```


---

### 2. Daily Temperatures

#### Problem Idea

For each day, find how many days until a warmer temperature.

Example:

```text
temp = [73, 74, 75, 71, 69, 72, 76, 73]
ans  = [1, 1, 4, 2, 1, 1, 0, 0]
```

#### Pattern

```text
Stack type: decreasing stack
Pop when : current temperature > stack top temperature
Meaning  : warmer day found
```

#### Thinking

Same as Next Greater Element, but answer is distance:

```text
answer[index] = currentIndex - oldIndex
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> dailyTemperatures(vector<int>& temp) {
    int n = temp.size();
    vector<int> ans(n, 0);
    stack<int> st; // unresolved indices

    for (int i = 0; i < n; i++) {
        while (!st.empty() && temp[i] > temp[st.top()]) {
            int j = st.top();
            st.pop();
            ans[j] = i - j;
        }
        st.push(i);
    }

    return ans;
}
```
#### Dry Run — Array Diagram

```text
temp = [73, 74, 75, 71, 69, 72, 76, 73]
stack stores index:temperature
```

##### i = 0, current = 73

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
        ^

stack before: []
action      : push 0
stack after : [0:73]
answer      : [0, 0, 0, 0, 0, 0, 0, 0]
```

##### i = 1, current = 74

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
            ^

stack before: [0:73]
74 > 73 -> pop 0 -> answer[0] = 1 - 0 = 1
action      : push 1
stack after : [1:74]
answer      : [1, 0, 0, 0, 0, 0, 0, 0]
```

##### i = 2, current = 75

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
                ^

stack before: [1:74]
75 > 74 -> pop 1 -> answer[1] = 2 - 1 = 1
action      : push 2
stack after : [2:75]
answer      : [1, 1, 0, 0, 0, 0, 0, 0]
```

##### i = 3, current = 71

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
                    ^

stack before: [2:75]
condition   : 71 > 75? no
action      : push 3
stack after : [2:75, 3:71]
answer      : [1, 1, 0, 0, 0, 0, 0, 0]
```

##### i = 4, current = 69

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
                        ^

stack before: [2:75, 3:71]
condition   : 69 > 71? no
action      : push 4
stack after : [2:75, 3:71, 4:69]
answer      : [1, 1, 0, 0, 0, 0, 0, 0]
```

##### i = 5, current = 72

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
                            ^

stack before: [2:75, 3:71, 4:69]

72 > 69 -> pop 4 -> answer[4] = 5 - 4 = 1
72 > 71 -> pop 3 -> answer[3] = 5 - 3 = 2
72 > 75? no

action      : push 5
stack after : [2:75, 5:72]
answer      : [1, 1, 0, 2, 1, 0, 0, 0]
```

Visual:

```text
[73, 74, 75, 71, 69, 72, 76, 73]
              ^   ^   |
              |   |   +-- 72 resolves 71 and 69
              |   +------ answer[4] = 1
              +---------- answer[3] = 2
```

##### i = 6, current = 76

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
                                ^

stack before: [2:75, 5:72]

76 > 72 -> pop 5 -> answer[5] = 6 - 5 = 1
76 > 75 -> pop 2 -> answer[2] = 6 - 2 = 4

action      : push 6
stack after : [6:76]
answer      : [1, 1, 4, 2, 1, 1, 0, 0]
```

##### i = 7, current = 73

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
                                    ^

stack before: [6:76]
condition   : 73 > 76? no
action      : push 7
stack after : [6:76, 7:73]
answer      : [1, 1, 4, 2, 1, 1, 0, 0]
```

#### Dry Run After Code

```text
temp = [73, 74, 75, 71, 69, 72, 76, 73]
ans  = [0, 0, 0, 0, 0, 0, 0, 0]
stack stores unresolved day indices

i = 0, 73 -> push 0
stack = [0:73]

i = 1, 74
74 > 73 -> pop 0 -> ans[0] = 1 - 0 = 1
push 1
stack = [1:74]
ans = [1,0,0,0,0,0,0,0]

i = 2, 75
75 > 74 -> pop 1 -> ans[1] = 2 - 1 = 1
push 2
stack = [2:75]
ans = [1,1,0,0,0,0,0,0]

i = 3, 71 -> push 3
stack = [2:75, 3:71]

i = 4, 69 -> push 4
stack = [2:75, 3:71, 4:69]

i = 5, 72
72 > 69 -> pop 4 -> ans[4] = 1
72 > 71 -> pop 3 -> ans[3] = 2
72 > 75? no -> push 5
stack = [2:75, 5:72]
ans = [1,1,0,2,1,0,0,0]

i = 6, 76
76 > 72 -> pop 5 -> ans[5] = 1
76 > 75 -> pop 2 -> ans[2] = 4
push 6
ans = [1,1,4,2,1,1,0,0]

i = 7, 73 -> no pop -> push 7
final ans = [1,1,4,2,1,1,0,0]
```


---

### 3. Final Prices With a Special Discount

#### Problem Idea

For each price, subtract the first smaller or equal price on the right.

Example:

```text
prices = [8, 4, 6, 2, 3]
ans    = [4, 2, 4, 2, 3]
```

Because:

```text
8 gets discount 4
4 gets discount 2
6 gets discount 2
2 gets no discount
3 gets no discount
```

#### Pattern

```text
Stack type: increasing stack
Pop when : current <= stack top
Meaning  : current is next smaller/equal discount
```

#### Thinking

Each price waits for a smaller or equal price after it.

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> finalPrices(vector<int>& prices) {
    int n = prices.size();
    vector<int> ans = prices;
    stack<int> st; // indices waiting for discount

    for (int i = 0; i < n; i++) {
        while (!st.empty() && prices[i] <= prices[st.top()]) {
            ans[st.top()] = prices[st.top()] - prices[i];
            st.pop();
        }
        st.push(i);
    }

    return ans;
}
```
#### Dry Run — Array Diagram

```text
prices = [8, 4, 6, 2, 3]
stack stores index:price
answer starts as original prices
```

##### i = 0, current = 8

```text
index :   0   1   2   3   4
prices:  [8,  4,  6,  2,  3]
          ^

stack before: []
action      : push 0
stack after : [0:8]
answer      : [8, 4, 6, 2, 3]
```

##### i = 1, current = 4

```text
index :   0   1   2   3   4
prices:  [8,  4,  6,  2,  3]
              ^

stack before: [0:8]
4 <= 8 -> pop 0 -> answer[0] = 8 - 4 = 4
action      : push 1
stack after : [1:4]
answer      : [4, 4, 6, 2, 3]
```

##### i = 2, current = 6

```text
index :   0   1   2   3   4
prices:  [8,  4,  6,  2,  3]
                  ^

stack before: [1:4]
condition   : 6 <= 4? no
action      : push 2
stack after : [1:4, 2:6]
answer      : [4, 4, 6, 2, 3]
```

##### i = 3, current = 2

```text
index :   0   1   2   3   4
prices:  [8,  4,  6,  2,  3]
                      ^

stack before: [1:4, 2:6]
2 <= 6 -> pop 2 -> answer[2] = 6 - 2 = 4
2 <= 4 -> pop 1 -> answer[1] = 4 - 2 = 2
action      : push 3
stack after : [3:2]
answer      : [4, 2, 4, 2, 3]
```

Visual:

```text
[8, 4, 6, 2, 3]
    ^  ^  |
    |  |  +-- current 2 gives discount to 6 and 4
    |  +----- answer[2] = 4
    +-------- answer[1] = 2
```

##### i = 4, current = 3

```text
index :   0   1   2   3   4
prices:  [8,  4,  6,  2,  3]
                          ^

stack before: [3:2]
condition   : 3 <= 2? no
action      : push 4
stack after : [3:2, 4:3]
answer      : [4, 2, 4, 2, 3]
```

#### Dry Run After Code

```text
prices = [8, 4, 6, 2, 3]
ans    = [8, 4, 6, 2, 3]
stack stores indices waiting for discount

i = 0, price = 8
push 0
stack = [0:8]

i = 1, price = 4
4 <= 8 -> pop 0 -> ans[0] = 8 - 4 = 4
push 1
stack = [1:4]
ans = [4,4,6,2,3]

i = 2, price = 6
6 <= 4? no
push 2
stack = [1:4, 2:6]

i = 3, price = 2
2 <= 6 -> pop 2 -> ans[2] = 6 - 2 = 4
2 <= 4 -> pop 1 -> ans[1] = 4 - 2 = 2
push 3
stack = [3:2]
ans = [4,2,4,2,3]

i = 4, price = 3
3 <= 2? no -> push 4
final ans = [4,2,4,2,3]
```


---

### 4. Stock Span

#### Problem Idea

For each day, find how many consecutive days before it had price less than or equal to current price.

Example:

```text
prices = [100, 80, 60, 70, 60, 75, 85]
ans    = [1,   1,  1,  2,  1,  4,  6]
```

#### Pattern

```text
Stack type: decreasing stack
Pop when : stack top price <= current price
Meaning  : current price absorbs smaller/equal previous prices
```

#### Thinking

Find previous greater element.

```text
span = currentIndex - previousGreaterIndex
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> stockSpan(vector<int>& prices) {
    int n = prices.size();
    vector<int> ans(n);
    stack<int> st; // indices of previous greater prices

    for (int i = 0; i < n; i++) {
        while (!st.empty() && prices[st.top()] <= prices[i]) {
            st.pop();
        }

        int previousGreater = st.empty() ? -1 : st.top();
        ans[i] = i - previousGreater;

        st.push(i);
    }

    return ans;
}
```
#### Dry Run — Array Diagram

```text
prices = [100, 80, 60, 70, 60, 75, 85]
stack stores index:price of previous greater blockers
```

##### i = 0, current = 100

```text
index : [0,   1,  2,  3,  4,  5,  6]
price : [100, 80, 60, 70, 60, 75, 85]
         ^

stack before          : []
previous greater index: -1
span                  : 0 - (-1) = 1
action                : push 0
stack after           : [0:100]
answer                : [1, _, _, _, _, _, _]
```

##### i = 1, current = 80

```text
index : [0,   1,  2,  3,  4,  5,  6]
price : [100, 80, 60, 70, 60, 75, 85]
              ^

stack before          : [0:100]
condition             : 80 >= 100? no
previous greater index: 0
span                  : 1 - 0 = 1
action                : push 1
stack after           : [0:100, 1:80]
answer                : [1, 1, _, _, _, _, _]
```

##### i = 2, current = 60

```text
index : [0,   1,  2,  3,  4,  5,  6]
price : [100, 80, 60, 70, 60, 75, 85]
                  ^

stack before          : [0:100, 1:80]
condition             : 60 >= 80? no
previous greater index: 1
span                  : 2 - 1 = 1
action                : push 2
stack after           : [0:100, 1:80, 2:60]
answer                : [1, 1, 1, _, _, _, _]
```

##### i = 3, current = 70

```text
index : [0,   1,  2,  3,  4,  5,  6]
price : [100, 80, 60, 70, 60, 75, 85]
                      ^

stack before: [0:100, 1:80, 2:60]
70 >= 60 -> pop 2
70 >= 80? no

previous greater index: 1
span                  : 3 - 1 = 2
action                : push 3
stack after           : [0:100, 1:80, 3:70]
answer                : [1, 1, 1, 2, _, _, _]
```

Visual:

```text
[100, 80, 60, 70, 60, 75, 85]
          <--- span --->
              60  70
blocked by 80
```

##### i = 4, current = 60

```text
index : [0,   1,  2,  3,  4,  5,  6]
price : [100, 80, 60, 70, 60, 75, 85]
                          ^

stack before          : [0:100, 1:80, 3:70]
condition             : 60 >= 70? no
previous greater index: 3
span                  : 4 - 3 = 1
action                : push 4
stack after           : [0:100, 1:80, 3:70, 4:60]
answer                : [1, 1, 1, 2, 1, _, _]
```

##### i = 5, current = 75

```text
index : [0,   1,  2,  3,  4,  5,  6]
price : [100, 80, 60, 70, 60, 75, 85]
                              ^

stack before: [0:100, 1:80, 3:70, 4:60]
75 >= 60 -> pop 4
75 >= 70 -> pop 3
75 >= 80? no

previous greater index: 1
span                  : 5 - 1 = 4
action                : push 5
stack after           : [0:100, 1:80, 5:75]
answer                : [1, 1, 1, 2, 1, 4, _]
```

##### i = 6, current = 85

```text
index : [0,   1,  2,  3,  4,  5,  6]
price : [100, 80, 60, 70, 60, 75, 85]
                                  ^

stack before: [0:100, 1:80, 5:75]
85 >= 75 -> pop 5
85 >= 80 -> pop 1
85 >= 100? no

previous greater index: 0
span                  : 6 - 0 = 6
action                : push 6
stack after           : [0:100, 6:85]
answer                : [1, 1, 1, 2, 1, 4, 6]
```

#### Dry Run After Code

```text
prices = [100, 80, 60, 70, 60, 75, 85]
stack keeps previous greater blockers

i = 0, price = 100
prevGreater = -1 -> span = 1
stack = [0:100]
ans = [1]

i = 1, price = 80
80 >= 100? no
prevGreater = 0 -> span = 1
stack = [0:100, 1:80]

i = 2, price = 60
60 >= 80? no
prevGreater = 1 -> span = 1
stack = [0:100, 1:80, 2:60]

i = 3, price = 70
70 >= 60 -> pop 2
70 >= 80? no
prevGreater = 1 -> span = 3 - 1 = 2
stack = [0:100, 1:80, 3:70]

i = 4, price = 60
prevGreater = 3 -> span = 1
stack = [0:100, 1:80, 3:70, 4:60]

i = 5, price = 75
75 >= 60 -> pop 4
75 >= 70 -> pop 3
75 >= 80? no
prevGreater = 1 -> span = 5 - 1 = 4
stack = [0:100, 1:80, 5:75]

i = 6, price = 85
85 >= 75 -> pop 5
85 >= 80 -> pop 1
85 >= 100? no
prevGreater = 0 -> span = 6
final ans = [1,1,1,2,1,4,6]
```


---

## Phase 2 — Boundary / Range Thinking

Goal:

```text
For each element, find the maximum range where it remains useful/minimum/maximum.
```

Core question:

```text
How far can this element expand before being blocked?
```

---

### 5. Largest Rectangle in Histogram

#### Problem Idea

Find the largest rectangle inside histogram bars.

Example:

```text
heights = [2, 1, 5, 6, 2, 3]
answer = 10
```

#### Pattern

```text
Stack type: increasing stack
Pop when : current height < stack top height
Meaning  : current is right smaller boundary
```

#### Thinking

Each bar wants to be the minimum height of a rectangle.

For every bar:

```text
width = nextSmallerIndex - previousSmallerIndex - 1
area  = height * width
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int largestRectangleArea(vector<int>& h) {
    int n = h.size();
    stack<int> st; // indices
    long long ans = 0;

    for (int i = 0; i <= n; i++) {
        int curr = (i == n ? 0 : h[i]); // sentinel 0

        while (!st.empty() && h[st.top()] > curr) {
            int height = h[st.top()];
            st.pop();

            int right = i;
            int left = st.empty() ? -1 : st.top();
            int width = right - left - 1;

            ans = max(ans, 1LL * height * width);
        }

        st.push(i);
    }

    return (int)ans;
}
```
#### Dry Run — Array Diagram

```text
heights = [2, 1, 5, 6, 2, 3]
add sentinel 0 at end
extended = [2, 1, 5, 6, 2, 3, 0]
stack stores index:height
```

##### i = 0, current = 2

```text
index : [0, 1, 2, 3, 4, 5, 6]
height: [2, 1, 5, 6, 2, 3, 0]
         ^

stack before: []
action      : push 0
stack after : [0:2]
answer      : 0
```

##### i = 1, current = 1

```text
index : [0, 1, 2, 3, 4, 5, 6]
height: [2, 1, 5, 6, 2, 3, 0]
            ^

stack before: [0:2]
1 < 2 -> pop 0

height = 2
right  = 1
left   = -1
width  = 1 - (-1) - 1 = 1
area   = 2 * 1 = 2

action      : push 1
stack after : [1:1]
answer      : 2
```

##### i = 2, current = 5

```text
index : [0, 1, 2, 3, 4, 5, 6]
height: [2, 1, 5, 6, 2, 3, 0]
               ^

stack before: [1:1]
condition   : 5 < 1? no
action      : push 2
stack after : [1:1, 2:5]
answer      : 2
```

##### i = 3, current = 6

```text
index : [0, 1, 2, 3, 4, 5, 6]
height: [2, 1, 5, 6, 2, 3, 0]
                  ^

stack before: [1:1, 2:5]
condition   : 6 < 5? no
action      : push 3
stack after : [1:1, 2:5, 3:6]
answer      : 2
```

##### i = 4, current = 2

```text
index : [0, 1, 2, 3, 4, 5, 6]
height: [2, 1, 5, 6, 2, 3, 0]
                     ^

stack before: [1:1, 2:5, 3:6]

2 < 6 -> pop 3
height = 6, right = 4, left = 2, width = 1, area = 6
answer = 6

2 < 5 -> pop 2
height = 5, right = 4, left = 1, width = 2, area = 10
answer = 10

2 < 1? no
action      : push 4
stack after : [1:1, 4:2]
answer      : 10
```

Visual:

```text
[2, 1, 5, 6, 2, 3]
       |-----|  |
       5  6     +-- current 2 is right smaller boundary
height = 5, width = 2, area = 10
```

##### i = 5, current = 3

```text
index : [0, 1, 2, 3, 4, 5, 6]
height: [2, 1, 5, 6, 2, 3, 0]
                        ^

stack before: [1:1, 4:2]
condition   : 3 < 2? no
action      : push 5
stack after : [1:1, 4:2, 5:3]
answer      : 10
```

##### i = 6, current = 0 sentinel

```text
index : [0, 1, 2, 3, 4, 5, 6]
height: [2, 1, 5, 6, 2, 3, 0]
                           ^

stack before: [1:1, 4:2, 5:3]

0 < 3 -> pop 5 -> height=3, left=4, right=6, width=1, area=3
0 < 2 -> pop 4 -> height=2, left=1, right=6, width=4, area=8
0 < 1 -> pop 1 -> height=1, left=-1, right=6, width=6, area=6

action      : push 6
stack after : [6:0]
answer      : 10
```

#### Dry Run After Code

```text
heights = [2, 1, 5, 6, 2, 3]
scan with sentinel 0 at i = 6
stack stores increasing heights by index

i = 0, curr = 2 -> push 0 -> stack [0:2]

i = 1, curr = 1
1 < 2 -> pop 0
height = 2, right = 1, left = -1, width = 1, area = 2
ans = 2
push 1 -> stack [1:1]

i = 2, curr = 5 -> push 2 -> stack [1:1, 2:5]
i = 3, curr = 6 -> push 3 -> stack [1:1, 2:5, 3:6]

i = 4, curr = 2
2 < 6 -> pop 3
height = 6, right = 4, left = 2, width = 1, area = 6
ans = 6
2 < 5 -> pop 2
height = 5, right = 4, left = 1, width = 2, area = 10
ans = 10
push 4 -> stack [1:1, 4:2]

i = 5, curr = 3 -> push 5 -> stack [1:1, 4:2, 5:3]

i = 6, curr = 0
pop 5 -> area = 3 * 1 = 3
pop 4 -> area = 2 * 4 = 8
pop 1 -> area = 1 * 6 = 6
final ans = 10
```


---

### 6. Maximal Rectangle

#### Problem Idea

Given a binary matrix, find the largest rectangle containing only `1`s.

Example:

```text
1 0 1 0 0
1 0 1 1 1
1 1 1 1 1
1 0 0 1 0

answer = 6
```

#### Pattern

```text
Convert each row into histogram.
Then apply Largest Rectangle in Histogram.
```

#### Thinking

For each row:

```text
if matrix[r][c] == '1': height[c]++
else height[c] = 0
```

Then solve histogram for that height array.

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int largestRectangleArea(vector<int>& h) {
    int n = h.size();
    stack<int> st;
    int ans = 0;

    for (int i = 0; i <= n; i++) {
        int curr = (i == n ? 0 : h[i]);

        while (!st.empty() && h[st.top()] > curr) {
            int height = h[st.top()];
            st.pop();

            int left = st.empty() ? -1 : st.top();
            int width = i - left - 1;

            ans = max(ans, height * width);
        }

        st.push(i);
    }

    return ans;
}

int maximalRectangle(vector<vector<char>>& matrix) {
    if (matrix.empty()) return 0;

    int rows = matrix.size();
    int cols = matrix[0].size();
    vector<int> height(cols, 0);
    int ans = 0;

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (matrix[r][c] == '1') height[c]++;
            else height[c] = 0;
        }

        ans = max(ans, largestRectangleArea(height));
    }

    return ans;
}
```
#### Dry Run — Matrix to Array Diagram

```text
matrix:
1 0 1 0 0
1 0 1 1 1
1 1 1 1 1
1 0 0 1 0

height starts as [0, 0, 0, 0, 0]
```

##### row = 0

```text
row 0 : [1, 0, 1, 0, 0]
height before: [0, 0, 0, 0, 0]

c = 0 -> 1 -> height[0] = 1
c = 1 -> 0 -> height[1] = 0
c = 2 -> 1 -> height[2] = 1
c = 3 -> 0 -> height[3] = 0
c = 4 -> 0 -> height[4] = 0

height after : [1, 0, 1, 0, 0]
histogram max area = 1
answer = 1
```

##### row = 1

```text
row 1 : [1, 0, 1, 1, 1]
height before: [1, 0, 1, 0, 0]

c = 0 -> 1 -> height[0] = 2
c = 1 -> 0 -> height[1] = 0
c = 2 -> 1 -> height[2] = 2
c = 3 -> 1 -> height[3] = 1
c = 4 -> 1 -> height[4] = 1

height after : [2, 0, 2, 1, 1]
histogram max area = 3
answer = 3
```

Visual:

```text
height:
[2, 0, 2, 1, 1]
          |-----|
          min height = 1, width = 3, area = 3
```

##### row = 2

```text
row 2 : [1, 1, 1, 1, 1]
height before: [2, 0, 2, 1, 1]

height after : [3, 1, 3, 2, 2]
histogram max area = 6
answer = 6
```

Visual:

```text
height:
[3, 1, 3, 2, 2]
       |--------|
       columns 2,3,4
       min height = 2
       width = 3
       area = 6
```

##### row = 3

```text
row 3 : [1, 0, 0, 1, 0]
height before: [3, 1, 3, 2, 2]

height after : [4, 0, 0, 3, 0]
histogram max area = 4
answer remains 6
```

#### Dry Run After Code

```text
matrix:
1 0 1 0 0
1 0 1 1 1
1 1 1 1 1
1 0 0 1 0

Start height = [0,0,0,0,0]

row 0 = [1,0,1,0,0]
height = [1,0,1,0,0]
largest histogram area = 1
ans = 1

row 1 = [1,0,1,1,1]
height = [2,0,2,1,1]
largest histogram area = 3
ans = 3

row 2 = [1,1,1,1,1]
height = [3,1,3,2,2]
largest histogram area = 6
ans = 6

row 3 = [1,0,0,1,0]
height = [4,0,0,3,0]
largest histogram area = 4
ans remains 6

final answer = 6
```


---

### 7. Height of Soldiers / Maximum of Minimum for Every Window Size

#### Problem Idea

For every window size `x`, find the maximum among all window minimums.

Example:

```text
H = [10, 20, 30, 50, 10, 70, 30]
```

For each window size:

```text
size 1 -> max of minimums among all windows of size 1
size 2 -> max of minimums among all windows of size 2
...
```

#### Pattern

```text
Same as histogram range boundary.
For each element, find the largest window where it is minimum.
```

#### Thinking

For each `H[i]`:

```text
left  = previous smaller index
right = next smaller index
length = right - left - 1

ans[length] = max(ans[length], H[i])
```

Then fill missing values from right to left:

```text
ans[i] = max(ans[i], ans[i + 1])
```

Why?

If a height can be minimum for a bigger window, it can also be candidate for smaller window.

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> maxOfMinimums(vector<int>& a) {
    int n = a.size();
    vector<int> left(n), right(n);
    stack<int> st;

    // previous smaller
    for (int i = 0; i < n; i++) {
        while (!st.empty() && a[st.top()] >= a[i]) {
            st.pop();
        }
        left[i] = st.empty() ? -1 : st.top();
        st.push(i);
    }

    while (!st.empty()) st.pop();

    // next smaller
    for (int i = n - 1; i >= 0; i--) {
        while (!st.empty() && a[st.top()] >= a[i]) {
            st.pop();
        }
        right[i] = st.empty() ? n : st.top();
        st.push(i);
    }

    vector<int> ans(n + 1, 0);

    for (int i = 0; i < n; i++) {
        int len = right[i] - left[i] - 1;
        ans[len] = max(ans[len], a[i]);
    }

    for (int len = n - 1; len >= 1; len--) {
        ans[len] = max(ans[len], ans[len + 1]);
    }

    vector<int> result;
    for (int len = 1; len <= n; len++) {
        result.push_back(ans[len]);
    }

    return result;
}
```
#### Dry Run — Array Diagram

```text
a = [10, 20, 30, 50, 10, 70, 30]
stack stores index:value
```

##### Build previous smaller

```text
index:  0   1   2   3   4   5   6
a    : [10, 20, 30, 50, 10, 70, 30]
```

```text
i=0 current=10 -> stack [] -> left[0]=-1 -> push 0
stack after: [0:10]

i=1 current=20 -> 20 > 10 -> left[1]=0 -> push 1
stack after: [0:10, 1:20]

i=2 current=30 -> 30 > 20 -> left[2]=1 -> push 2
stack after: [0:10, 1:20, 2:30]

i=3 current=50 -> 50 > 30 -> left[3]=2 -> push 3
stack after: [0:10, 1:20, 2:30, 3:50]

i=4 current=10
50 >= 10 -> pop 3
30 >= 10 -> pop 2
20 >= 10 -> pop 1
10 >= 10 -> pop 0
left[4] = -1 -> push 4

i=5 current=70 -> 70 > 10 -> left[5]=4 -> push 5
i=6 current=30 -> 70 >= 30 pop 5 -> left[6]=4 -> push 6

left = [-1, 0, 1, 2, -1, 4, 4]
```

##### Build next smaller

```text
scan from right to left

i=6 current=30 -> right[6]=7 -> push 6
i=5 current=70 -> right[5]=6 -> push 5
i=4 current=10 -> pop 5, pop 6 -> right[4]=7 -> push 4
i=3 current=50 -> right[3]=4 -> push 3
i=2 current=30 -> pop 3 -> right[2]=4 -> push 2
i=1 current=20 -> pop 2 -> right[1]=4 -> push 1
i=0 current=10 -> pop 1, pop 4 -> right[0]=7 -> push 0

right = [7, 4, 4, 4, 7, 6, 7]
```

##### Map each value to its maximum window length

```text
i=0 value=10 -> len = 7 - (-1) - 1 = 7 -> ans[7]=10
i=1 value=20 -> len = 4 - 0 - 1 = 3 -> ans[3]=20
i=2 value=30 -> len = 4 - 1 - 1 = 2 -> ans[2]=30
i=3 value=50 -> len = 4 - 2 - 1 = 1 -> ans[1]=50
i=4 value=10 -> len = 7 - (-1) - 1 = 7 -> ans[7]=10
i=5 value=70 -> len = 6 - 4 - 1 = 1 -> ans[1]=70
i=6 value=30 -> len = 7 - 4 - 1 = 2 -> ans[2]=30

before fill:
ans by len = [70, 30, 20, 0, 0, 0, 10]
```

##### Fill missing lengths

```text
len 6 -> max(0,10)=10
len 5 -> max(0,10)=10
len 4 -> max(0,10)=10
len 3 -> max(20,10)=20

final result = [70, 30, 20, 10, 10, 10, 10]
```

#### Dry Run After Code

```text
a = [10, 20, 30, 50, 10, 70, 30]

previous smaller left indices:
left = [-1, 0, 1, 2, -1, 4, 4]

next smaller right indices:
right = [7, 4, 4, 4, 7, 6, 7]

For each i, len = right[i] - left[i] - 1:
i=0, value=10, len=7 -> ans[7] = 10
i=1, value=20, len=3 -> ans[3] = 20
i=2, value=30, len=2 -> ans[2] = 30
i=3, value=50, len=1 -> ans[1] = 50
i=4, value=10, len=7 -> ans[7] = 10
i=5, value=70, len=1 -> ans[1] = 70
i=6, value=30, len=2 -> ans[2] = 30

before fill:
ans by len = [70, 30, 20, 0, 0, 0, 10]

fill right to left:
len 6 -> max(0,10)=10
len 5 -> 10
len 4 -> 10
len 3 -> max(20,10)=20

final result = [70, 30, 20, 10, 10, 10, 10]
```


---

## Phase 3 — Contribution Thinking

Goal:

```text
Instead of finding answer for every subarray, count how much each element contributes.
```

Core question:

```text
For how many subarrays is this element the minimum/maximum?
```

---

### 8. Sum of Subarray Minimums

#### Problem Idea

Find sum of minimum value of every subarray.

Example:

```text
arr = [3, 1, 2, 4]
answer = 17
```

#### Pattern

```text
Previous smaller + next smaller/equal
Contribution = value * leftChoices * rightChoices
```

#### Thinking

For each element `arr[i]`:

```text
leftChoices  = number of choices to extend left
rightChoices = number of choices to extend right
contribution = arr[i] * leftChoices * rightChoices
```

Tie handling:

```text
previous strictly smaller -> pop >=
next smaller or equal     -> pop >
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int sumSubarrayMins(vector<int>& arr) {
    const int MOD = 1e9 + 7;
    int n = arr.size();
    vector<int> left(n), right(n);
    stack<int> st;

    // previous strictly smaller distance
    for (int i = 0; i < n; i++) {
        while (!st.empty() && arr[st.top()] >= arr[i]) {
            st.pop();
        }
        left[i] = st.empty() ? i + 1 : i - st.top();
        st.push(i);
    }

    while (!st.empty()) st.pop();

    // next smaller or equal distance
    for (int i = n - 1; i >= 0; i--) {
        while (!st.empty() && arr[st.top()] > arr[i]) {
            st.pop();
        }
        right[i] = st.empty() ? n - i : st.top() - i;
        st.push(i);
    }

    long long ans = 0;
    for (int i = 0; i < n; i++) {
        ans = (ans + 1LL * arr[i] * left[i] * right[i]) % MOD;
    }

    return ans;
}
```
#### Dry Run — Array Diagram

```text
arr = [3, 1, 2, 4]
stack stores index:value
```

##### Build left distances

```text
index:  0   1   2   3
arr  : [3,  1,  2,  4]
```

```text
i = 0, current = 3
stack before: []
left[0] = 1
stack after : [0:3]
left        : [1, _, _, _]

i = 1, current = 1
stack before: [0:3]
3 >= 1 -> pop 0
left[1] = 2
stack after : [1:1]
left        : [1, 2, _, _]

i = 2, current = 2
stack before: [1:1]
1 >= 2? no
left[2] = 2 - 1 = 1
stack after : [1:1, 2:2]
left        : [1, 2, 1, _]

i = 3, current = 4
stack before: [1:1, 2:2]
2 >= 4? no
left[3] = 3 - 2 = 1
stack after : [1:1, 2:2, 3:4]
left        : [1, 2, 1, 1]
```

##### Build right distances

```text
i = 3, current = 4 -> right[3] = 1 -> stack [3:4]
i = 2, current = 2 -> 4 > 2 pop -> right[2] = 2 -> stack [2:2]
i = 1, current = 1 -> 2 > 1 pop -> right[1] = 3 -> stack [1:1]
i = 0, current = 3 -> 1 > 3? no -> right[0] = 1 -> stack [1:1, 0:3]

right = [1, 3, 2, 1]
```

##### Contribution

```text
index:      0   1   2   3
arr:       [3,  1,  2,  4]
left:      [1,  2,  1,  1]
right:     [1,  3,  2,  1]

i = 0 -> 3 * 1 * 1 = 3
i = 1 -> 1 * 2 * 3 = 6
i = 2 -> 2 * 1 * 2 = 4
i = 3 -> 4 * 1 * 1 = 4

answer = 17
```

Visual:

```text
[3, 1, 2, 4]
 |-----------|
      1 is minimum for 2 left choices * 3 right choices
```

#### Dry Run After Code

```text
arr = [3, 1, 2, 4]

left distance using previous strictly smaller:
i=0, 3 -> left[0]=1
i=1, 1 -> pop 3 -> left[1]=2
i=2, 2 -> previous smaller is 1 -> left[2]=1
i=3, 4 -> previous smaller is 2 -> left[3]=1
left = [1,2,1,1]

right distance using next smaller/equal:
i=3, 4 -> right[3]=1
i=2, 2 -> pop 4 -> right[2]=2
i=1, 1 -> pop 2 -> right[1]=3
i=0, 3 -> next smaller/equal is 1 -> right[0]=1
right = [1,3,2,1]

contribution:
3 * 1 * 1 = 3
1 * 2 * 3 = 6
2 * 1 * 2 = 4
4 * 1 * 1 = 4

answer = 3 + 6 + 4 + 4 = 17
```


---

### 9. Maximum Subarray Min-Product

#### Problem Idea

For every subarray:

```text
min(subarray) * sum(subarray)
```

Return maximum value.

Example:

```text
nums = [1, 2, 3, 2]
answer = 14
```

Subarray `[2,3,2]`:

```text
minimum = 2
sum = 7
product = 14
```

#### Pattern

```text
Histogram boundary + prefix sum
```

#### Thinking

For each element as minimum:

```text
find max range where nums[i] is minimum
range sum using prefix sum
candidate = nums[i] * rangeSum
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxSumMinProduct(vector<int>& nums) {
    const int MOD = 1e9 + 7;
    int n = nums.size();

    vector<long long> pref(n + 1, 0);
    for (int i = 0; i < n; i++) {
        pref[i + 1] = pref[i] + nums[i];
    }

    stack<int> st;
    long long ans = 0;

    for (int i = 0; i <= n; i++) {
        long long curr = (i == n ? 0 : nums[i]);

        while (!st.empty() && nums[st.top()] > curr) {
            int idx = st.top();
            st.pop();

            int left = st.empty() ? -1 : st.top();
            int right = i;

            long long rangeSum = pref[right] - pref[left + 1];
            ans = max(ans, rangeSum * nums[idx]);
        }

        st.push(i);
    }

    return ans % MOD;
}
```
#### Dry Run — Array Diagram

```text
nums = [1, 2, 3, 2]
prefix = [0, 1, 3, 6, 8]
stack stores index:value
sentinel 0 is used at the end
```

##### i = 0, current = 1

```text
index:  0   1   2   3   4
nums : [1,  2,  3,  2,  0]
        ^

stack before: []
action      : push 0
stack after : [0:1]
answer      : 0
```

##### i = 1, current = 2

```text
stack before: [0:1]
condition   : 2 < 1? no
action      : push 1
stack after : [0:1, 1:2]
answer      : 0
```

##### i = 2, current = 3

```text
stack before: [0:1, 1:2]
condition   : 3 < 2? no
action      : push 2
stack after : [0:1, 1:2, 2:3]
answer      : 0
```

##### i = 3, current = 2

```text
index:  0   1   2   3   4
nums : [1,  2,  3,  2,  0]
                    ^

stack before: [0:1, 1:2, 2:3]
2 < 3 -> pop 2

left     = 1
right    = 3
rangeSum = prefix[3] - prefix[2] = 6 - 3 = 3
product  = 3 * 3 = 9

action      : push 3
stack after : [0:1, 1:2, 3:2]
answer      : 9
```

##### i = 4, current = 0 sentinel

```text
stack before: [0:1, 1:2, 3:2]

0 < 2 -> pop 3
rangeSum = prefix[4] - prefix[2] = 8 - 3 = 5
product  = 2 * 5 = 10
answer   = 10

0 < 2 -> pop 1
rangeSum = prefix[4] - prefix[1] = 8 - 1 = 7
product  = 2 * 7 = 14
answer   = 14

0 < 1 -> pop 0
rangeSum = prefix[4] - prefix[0] = 8
product  = 1 * 8 = 8
answer   = 14
```

Visual:

```text
[1, 2, 3, 2]
    |------|
    min = 2, sum = 7, product = 14
```

#### Dry Run After Code

```text
nums = [1, 2, 3, 2]
prefix = [0, 1, 3, 6, 8]

Use increasing stack. When current is smaller, finalize popped value as minimum.

i = 0, curr = 1 -> push 0 -> stack [0:1]
i = 1, curr = 2 -> push 1 -> stack [0:1, 1:2]
i = 2, curr = 3 -> push 2 -> stack [0:1, 1:2, 2:3]

i = 3, curr = 2
2 < 3 -> pop index 2
left = 1, right = 3
rangeSum = prefix[3] - prefix[2] = 6 - 3 = 3
product = 3 * nums[2] = 3 * 3 = 9
ans = 9
push 3 -> stack [0:1, 1:2, 3:2]

i = 4, sentinel curr = 0
pop index 3 value 2
left = 1, right = 4
rangeSum = prefix[4] - prefix[2] = 8 - 3 = 5
product = 5 * 2 = 10
ans = 10

pop index 1 value 2
left = 0, right = 4
rangeSum = prefix[4] - prefix[1] = 8 - 1 = 7
product = 7 * 2 = 14
ans = 14

pop index 0 value 1
rangeSum = prefix[4] - prefix[0] = 8
product = 8 * 1 = 8

final answer = 14
```


---

## Phase 4 — Greedy + Monotonic Stack

Goal:

```text
Use stack to remove bad previous decisions when a better current option appears.
```

Core question:

```text
Can current element improve answer by removing previous elements?
```

---

### 10. Remove K Digits

#### Problem Idea

Remove `k` digits to make the smallest possible number.

Example:

```text
num = "1432219", k = 3
answer = "1219"
```

#### Pattern

```text
Stack type: increasing stack
Pop when : previous digit > current digit and k > 0
Meaning  : remove bad larger previous digit
```

#### Thinking

A bigger digit before a smaller digit makes the number larger.

So remove it if allowed.

#### Code

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

    int i = 0;
    while (i < (int)st.size() && st[i] == '0') i++;

    string ans = st.substr(i);
    return ans.empty() ? "0" : ans;
}
```
#### Dry Run — String Diagram

```text
num = "1432219"
k = 3
stack stores chosen digits as a string
```

##### read char = '1'

```text
chars: [1, 4, 3, 2, 2, 1, 9]
        ^

stack before: ""
action      : push 1
stack after : "1"
k           : 3
```

##### read char = '4'

```text
chars: [1, 4, 3, 2, 2, 1, 9]
           ^

stack before: "1"
condition   : 1 > 4? no
action      : push 4
stack after : "14"
k           : 3
```

##### read char = '3'

```text
chars: [1, 4, 3, 2, 2, 1, 9]
              ^

stack before: "14"
4 > 3 and k > 0 -> pop 4
k becomes 2
action      : push 3
stack after : "13"
```

Visual:

```text
1 4 3 2 2 1 9
  ^ |
  | +-- smaller 3 arrives
  +---- remove 4
```

##### read char = '2'

```text
stack before: "13"
3 > 2 and k > 0 -> pop 3
k becomes 1
action      : push 2
stack after : "12"
```

##### read char = second '2'

```text
stack before: "12"
condition   : 2 > 2? no
action      : push 2
stack after : "122"
k           : 1
```

##### read char = '1'

```text
stack before: "122"
2 > 1 and k > 0 -> pop last 2
k becomes 0
action      : push 1
stack after : "121"
```

##### read char = '9'

```text
stack before: "121"
condition   : k == 0, cannot pop
action      : push 9
stack after : "1219"
```

Final:

```text
answer = "1219"
```

#### Dry Run After Code

```text
num = "1432219", k = 3
stack string keeps digits increasing

read 1 -> stack = "1", k = 3
read 4 -> 1 > 4? no -> stack = "14", k = 3
read 3 -> 4 > 3 -> pop 4 -> stack = "1", k = 2 -> push 3 -> "13"
read 2 -> 3 > 2 -> pop 3 -> stack = "1", k = 1 -> push 2 -> "12"
read 2 -> 2 > 2? no -> push -> "122"
read 1 -> 2 > 1 -> pop last 2 -> stack = "12", k = 0 -> push 1 -> "121"
read 9 -> k = 0, push -> "1219"

remove leading zeros: none
answer = "1219"
```


---

### 11. Remove Duplicate Letters

#### Problem Idea

Remove duplicate letters so every letter appears once and result is lexicographically smallest.

Example:

```text
s = "cbacdcbc"
answer = "acdb"
```

#### Pattern

```text
Stack type: increasing lexicographic stack
Pop when : stack top > current char AND stack top appears later
Meaning  : remove bad bigger char safely
```

#### Thinking

You can remove a previous bigger character only if it appears again later.

Need:

```text
frequency count
visited array
stack string
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

string removeDuplicateLetters(string s) {
    vector<int> freq(26, 0), used(26, 0);

    for (char c : s) freq[c - 'a']++;

    string st;

    for (char c : s) {
        int id = c - 'a';
        freq[id]--;

        if (used[id]) continue;

        while (!st.empty() && st.back() > c && freq[st.back() - 'a'] > 0) {
            used[st.back() - 'a'] = 0;
            st.pop_back();
        }

        st.push_back(c);
        used[id] = 1;
    }

    return st;
}
```
#### Dry Run — String Diagram

```text
s = "cbacdcbc"
initial frequency:
c = 4, b = 2, a = 1, d = 1

stack stores chosen letters
```

##### i = 0, current = c

```text
chars: [c, b, a, c, d, c, b, c]
        ^

freq[c] after decrement: 3
stack before: ""
used[c]     : false
action      : push c
stack after : "c"
```

##### i = 1, current = b

```text
chars: [c, b, a, c, d, c, b, c]
           ^

freq[b] after decrement: 1
stack before: "c"

c > b and freq[c] > 0 -> pop c
action      : push b
stack after : "b"
```

##### i = 2, current = a

```text
chars: [c, b, a, c, d, c, b, c]
              ^

freq[a] after decrement: 0
stack before: "b"

b > a and freq[b] > 0 -> pop b
action      : push a
stack after : "a"
```

##### i = 3, current = c

```text
stack before: "a"
condition   : a > c? no
action      : push c
stack after : "ac"
```

##### i = 4, current = d

```text
stack before: "ac"
condition   : c > d? no
action      : push d
stack after : "acd"
```

##### i = 5, current = c

```text
used[c] = true
action  : skip
stack   : "acd"
```

##### i = 6, current = b

```text
stack before: "acd"
d > b? yes
freq[d] > 0? no
cannot pop d because d does not appear later
action      : push b
stack after : "acdb"
```

##### i = 7, current = c

```text
used[c] = true
action  : skip
answer  : "acdb"
```

#### Dry Run After Code

```text
s = "cbacdcbc"
initial freq: c=4, b=2, a=1, d=1
stack = ""

read c: freq[c]=3, used[c]=false -> push c
stack = "c"

read b: freq[b]=1
c > b and c appears later -> pop c
push b
stack = "b"

read a: freq[a]=0
b > a and b appears later -> pop b
push a
stack = "a"

read c: freq[c]=2 -> push c
stack = "ac"

read d: freq[d]=0 -> c > d? no -> push d
stack = "acd"

read c: already used -> skip
stack = "acd"

read b: freq[b]=0
d > b but d does not appear later -> cannot pop d
push b
stack = "acdb"

read c: already used -> skip
answer = "acdb"
```


---

## Phase 5 — Advanced / Hidden Monotonic Stack

Goal:

```text
Solve problems where monotonic stack is not obvious from statement.
```

Core question:

```text
Who blocks whom?
Who removes whom?
Who becomes useless?
```

---

### 12. Trapping Rain Water

#### Problem Idea

Given heights, calculate trapped rain water.

Example:

```text
height = [0,1,0,2,1,0,1,3,2,1,2,1]
answer = 6
```

#### Pattern

```text
Stack type: decreasing stack
Pop when : current height > stack top height
Meaning  : current is right wall
```

#### Thinking

When current bar is higher, it may form a container with a previous left wall.

```text
water = width * boundedHeight
boundedHeight = min(leftWall, rightWall) - bottom
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int trap(vector<int>& height) {
    int n = height.size();
    stack<int> st;
    int water = 0;

    for (int i = 0; i < n; i++) {
        while (!st.empty() && height[i] > height[st.top()]) {
            int bottom = st.top();
            st.pop();

            if (st.empty()) break;

            int left = st.top();
            int width = i - left - 1;
            int boundedHeight = min(height[left], height[i]) - height[bottom];

            water += width * boundedHeight;
        }

        st.push(i);
    }

    return water;
}
```
#### Dry Run — Array Diagram

```text
height = [2, 0, 2]
stack stores index:height
```

##### i = 0, current = 2

```text
index :   0   1   2
height:  [2,  0,  2]
          ^

stack before: []
action      : push 0
stack after : [0:2]
water       : 0
```

##### i = 1, current = 0

```text
index :   0   1   2
height:  [2,  0,  2]
              ^

stack before: [0:2]
condition   : 0 > 2? no
action      : push 1
stack after : [0:2, 1:0]
water       : 0
```

##### i = 2, current = 2

```text
index :   0   1   2
height:  [2,  0,  2]
                  ^

stack before: [0:2, 1:0]

2 > 0 -> pop bottom index 1

left wall index  = 0
right wall index = 2
bottom height    = 0

width         = 2 - 0 - 1 = 1
boundedHeight = min(2, 2) - 0 = 2
water added   = 1 * 2 = 2

2 > height[0]? 2 > 2? no
action      : push 2
stack after : [0:2, 2:2]
water       : 2
```

Visual:

```text
index :  0   1   2
height: [2,  0,  2]
         |~~~~~~~|
             water
```

#### Dry Run After Code

```text
Use small example: height = [2, 0, 2]
stack stores indices of decreasing bars

i = 0, height = 2
stack empty -> push 0
stack = [0:2], water = 0

i = 1, height = 0
0 > 2? no -> push 1
stack = [0:2, 1:0], water = 0

i = 2, height = 2
2 > 0 -> pop bottom index 1
left wall index = 0
right wall index = 2
width = 2 - 0 - 1 = 1
boundedHeight = min(2,2) - 0 = 2
water += 1 * 2 = 2

2 > height[0]? no because equal
push 2
stack = [0:2, 2:2]
final water = 2
```


---

### 13. Maximum Width Ramp

#### Problem Idea

Find maximum `j - i` such that:

```text
i < j and nums[i] <= nums[j]
```

Example:

```text
nums = [6, 0, 8, 2, 1, 5]
answer = 4
```

Ramp:

```text
i = 1, nums[i] = 0
j = 5, nums[j] = 5
width = 4
```

#### Pattern

```text
Build decreasing stack of candidate left indices.
Scan from right and resolve candidates.
```

#### Thinking

Only keep useful left candidates.

If a previous value is smaller, bigger previous values are not useful as left boundary.

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxWidthRamp(vector<int>& nums) {
    int n = nums.size();
    stack<int> st;

    // candidate left indices with decreasing values
    for (int i = 0; i < n; i++) {
        if (st.empty() || nums[i] < nums[st.top()]) {
            st.push(i);
        }
    }

    int ans = 0;

    // scan from right to maximize width
    for (int j = n - 1; j >= 0; j--) {
        while (!st.empty() && nums[st.top()] <= nums[j]) {
            ans = max(ans, j - st.top());
            st.pop();
        }
    }

    return ans;
}
```
#### Dry Run — Array Diagram

```text
nums = [6, 0, 8, 2, 1, 5]
stack stores candidate left index:value
```

##### Build decreasing candidate stack

```text
index:   0   1   2   3   4   5
nums :  [6,  0,  8,  2,  1,  5]
```

```text
i = 0, current = 6 -> push 0 -> stack [0:6]
i = 1, current = 0 -> 0 < 6 -> push 1 -> stack [0:6, 1:0]
i = 2, current = 8 -> 8 < 0? no -> skip
i = 3, current = 2 -> 2 < 0? no -> skip
i = 4, current = 1 -> 1 < 0? no -> skip
i = 5, current = 5 -> 5 < 0? no -> skip

candidate stack = [0:6, 1:0]
```

##### Scan from right

```text
j = 5, current = 5
index:   0   1   2   3   4   5
nums :  [6,  0,  8,  2,  1,  5]
                          ^

stack before: [0:6, 1:0]
nums[1] = 0 <= 5 -> width = 5 - 1 = 4
answer = 4
pop 1
nums[0] = 6 <= 5? no

stack after : [0:6]
answer      : 4
```

Visual:

```text
[6, 0, 8, 2, 1, 5]
    |------------|
    i = 1        j = 5
    0 <= 5
    width = 4
```

```text
j = 4, current = 1 -> nums[0]=6 <= 1? no
j = 3, current = 2 -> nums[0]=6 <= 2? no
j = 2, current = 8 -> nums[0]=6 <= 8 yes
width = 2 - 0 = 2, answer remains 4, pop 0

stack empty
final answer = 4
```

#### Dry Run After Code

```text
nums = [6, 0, 8, 2, 1, 5]

Build decreasing candidate-left stack:
i=0, 6 -> push 0 -> [0:6]
i=1, 0 -> 0 < 6 -> push 1 -> [0:6, 1:0]
i=2, 8 -> not smaller -> skip
i=3, 2 -> not smaller than 0 -> skip
i=4, 1 -> skip
i=5, 5 -> skip

Scan from right:
j=5, nums[j]=5
nums[1]=0 <= 5 -> width = 5 - 1 = 4, ans = 4, pop 1
nums[0]=6 <= 5? no

j=4, nums[j]=1 -> 6 <= 1? no
j=3, nums[j]=2 -> 6 <= 2? no
j=2, nums[j]=8
nums[0]=6 <= 8 -> width = 2 - 0 = 2, ans remains 4, pop 0

stack empty -> final answer = 4
```


---

### 14. Number of Visible People in a Queue

#### Problem Idea

Each person can see people to the right until a taller or equal person blocks the view.

Example:

```text
heights = [10, 6, 8, 5, 11, 9]
answer  = [3, 1, 2, 1, 1, 0]
```

#### Pattern

```text
Stack type: decreasing stack from right to left
Pop when : current height > stack top
Meaning  : current can see popped shorter people
```

#### Thinking

From right to left:

```text
Pop all shorter people: current can see them.
If one taller/equal remains: current can also see that blocker.
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> canSeePersonsCount(vector<int>& heights) {
    int n = heights.size();
    vector<int> ans(n, 0);
    stack<int> st; // heights to the right

    for (int i = n - 1; i >= 0; i--) {
        while (!st.empty() && heights[i] > st.top()) {
            ans[i]++;
            st.pop();
        }

        if (!st.empty()) ans[i]++; // sees first taller/equal blocker

        st.push(heights[i]);
    }

    return ans;
}
```
#### Dry Run — Array Diagram

```text
heights = [10, 6, 8, 5, 11, 9]
scan from right to left
stack stores visible blocker heights
```

##### i = 5, current = 9

```text
index  :   0   1  2  3   4   5
heights: [10, 6, 8, 5, 11, 9]
                              ^

stack before: []
action      : push 9
stack after : [9]
answer      : [0, 0, 0, 0, 0, 0]
```

##### i = 4, current = 11

```text
stack before: [9]
11 > 9 -> see 9, pop 9
answer[4] = 1
action      : push 11
stack after : [11]
answer      : [0, 0, 0, 0, 1, 0]
```

##### i = 3, current = 5

```text
stack before: [11]
5 > 11? no
sees blocker 11 -> answer[3] = 1
action      : push 5
stack after : [11, 5]
answer      : [0, 0, 0, 1, 1, 0]
```

##### i = 2, current = 8

```text
stack before: [11, 5]
8 > 5 -> see 5, pop 5
8 > 11? no
sees blocker 11 -> answer[2] = 2
action      : push 8
stack after : [11, 8]
answer      : [0, 0, 2, 1, 1, 0]
```

##### i = 1, current = 6

```text
stack before: [11, 8]
6 > 8? no
sees blocker 8 -> answer[1] = 1
action      : push 6
stack after : [11, 8, 6]
answer      : [0, 1, 2, 1, 1, 0]
```

##### i = 0, current = 10

```text
stack before: [11, 8, 6]
10 > 6 -> see 6, pop 6
10 > 8 -> see 8, pop 8
10 > 11? no
sees blocker 11 -> answer[0] = 3
action      : push 10
stack after : [11, 10]
answer      : [3, 1, 2, 1, 1, 0]
```

Visual:

```text
[10, 6, 8, 5, 11, 9]
  |  |  |      |
  |  |  |      +-- 11 blocks
  |  |  +--------- sees 8
  |  +------------ sees 6
  +--------------- answer[0] = 3
```

#### Dry Run After Code

```text
heights = [10, 6, 8, 5, 11, 9]
scan right to left
stack stores visible blockers to the right

i = 5, h = 9
stack empty -> ans[5] = 0 -> push 9
stack = [9]

i = 4, h = 11
11 > 9 -> see 9, pop, ans[4] = 1
stack empty -> push 11
stack = [11]

i = 3, h = 5
5 > 11? no
sees blocker 11 -> ans[3] = 1
push 5 -> stack = [11,5]

i = 2, h = 8
8 > 5 -> see 5, pop, ans[2] = 1
8 > 11? no
sees blocker 11 -> ans[2] = 2
push 8 -> stack = [11,8]

i = 1, h = 6
6 > 8? no
sees blocker 8 -> ans[1] = 1
push 6 -> stack = [11,8,6]

i = 0, h = 10
10 > 6 -> see 6, pop, ans[0] = 1
10 > 8 -> see 8, pop, ans[0] = 2
10 > 11? no
sees blocker 11 -> ans[0] = 3

final ans = [3,1,2,1,1,0]
```


---

### 15. Steps to Make Array Non-Decreasing

#### Problem Idea

In one step, remove every element `nums[i]` where:

```text
nums[i - 1] > nums[i]
```

Return number of steps until array becomes non-decreasing.

Example:

```text
nums = [5, 3, 4, 4, 7, 3, 6, 11, 8, 5, 11]
answer = 3
```

#### Pattern

```text
Monotonic stack + DP state
```

#### Thinking

Each element may die after some number of rounds.

Stack stores:

```text
(value, stepsToDie)
```

When current is greater/equal, it removes smaller/equal previous dependencies.

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int totalSteps(vector<int>& nums) {
    int ans = 0;
    stack<pair<int,int>> st; // {value, steps needed to remove}

    for (int x : nums) {
        int steps = 0;

        while (!st.empty() && x >= st.top().first) {
            steps = max(steps, st.top().second);
            st.pop();
        }

        if (st.empty()) {
            steps = 0;
        } else {
            steps += 1;
        }

        ans = max(ans, steps);
        st.push({x, steps});
    }

    return ans;
}
```
#### Dry Run — Array Diagram

```text
nums = [5, 3, 4, 4, 7, 3, 6, 11, 8, 5, 11]
stack stores value:stepsToDie
```

##### x = 5

```text
processed: [5]
            ^

stack before: []
steps       : 0
action      : push {5,0}
stack after : [5:0]
answer      : 0
```

##### x = 3

```text
processed: [5, 3]
               ^

stack before: [5:0]
3 >= 5? no
steps       : 1
action      : push {3,1}
stack after : [5:0, 3:1]
answer      : 1
```

##### x = 4

```text
processed: [5, 3, 4]
                  ^

stack before: [5:0, 3:1]
4 >= 3 -> pop {3,1}
steps = max(0, 1) = 1
4 >= 5? no
bigger left exists -> steps = 1 + 1 = 2

action      : push {4,2}
stack after : [5:0, 4:2]
answer      : 2
```

##### x = 4

```text
stack before: [5:0, 4:2]
4 >= 4 -> pop {4,2}
steps = 2
4 >= 5? no
bigger left exists -> steps = 3
action      : push {4,3}
stack after : [5:0, 4:3]
answer      : 3
```

##### x = 7

```text
stack before: [5:0, 4:3]
7 >= 4 -> pop {4,3}, steps = 3
7 >= 5 -> pop {5,0}, steps = 3
stack empty -> steps = 0
action      : push {7,0}
stack after : [7:0]
answer      : 3
```

##### x = 3

```text
stack before: [7:0]
3 >= 7? no
steps       : 1
action      : push {3,1}
stack after : [7:0, 3:1]
answer      : 3
```

##### x = 6

```text
stack before: [7:0, 3:1]
6 >= 3 -> pop {3,1}, steps = 1
6 >= 7? no
bigger left exists -> steps = 2
action      : push {6,2}
stack after : [7:0, 6:2]
answer      : 3
```

##### x = 11

```text
stack before: [7:0, 6:2]
11 >= 6 -> pop {6,2}, steps = 2
11 >= 7 -> pop {7,0}, steps = 2
stack empty -> steps = 0
action      : push {11,0}
stack after : [11:0]
answer      : 3
```

##### remaining values

```text
x = 8
stack before: [11:0]
8 >= 11? no -> steps = 1
push {8,1}

x = 5
stack before: [11:0, 8:1]
5 >= 8? no -> steps = 1
push {5,1}

x = 11
stack before: [11:0, 8:1, 5:1]
11 >= 5 -> pop {5,1}
11 >= 8 -> pop {8,1}
11 >= 11 -> pop {11,0}
stack empty -> steps = 0

final answer = 3
```

#### Dry Run After Code

```text
nums = [5, 3, 4, 4, 7, 3, 6, 11, 8, 5, 11]
stack stores {value, stepsToDie}

x = 5
stack empty -> steps = 0
push {5,0}, ans = 0

x = 3
3 >= 5? no
left bigger exists, so steps = 1
push {3,1}, ans = 1

x = 4
4 >= 3 -> steps = max(0,1)=1, pop {3,1}
4 >= 5? no
left bigger exists -> steps = 1 + 1 = 2
push {4,2}, ans = 2

x = 4
4 >= 4 -> steps = max(0,2)=2, pop {4,2}
4 >= 5? no
left bigger exists -> steps = 2 + 1 = 3
push {4,3}, ans = 3

x = 7
7 >= 4 -> steps = 3, pop
7 >= 5 -> steps = 3, pop
stack empty -> steps = 0
push {7,0}, ans = 3

x = 3
3 >= 7? no -> steps = 1
push {3,1}, ans = 3

x = 6
6 >= 3 -> steps = 1, pop
6 >= 7? no -> steps = 2
push {6,2}, ans = 3

x = 11
pop {6,2}, pop {7,0}, stack empty -> steps = 0
push {11,0}, ans = 3

x = 8 -> blocked by 11 -> steps = 1
x = 5 -> blocked by 8 -> steps = 1
x = 11 -> pops 5 and 8, blocked by 11 -> steps = 2

final answer = 3
```


---

## Final Phase Summary

| Phase   | Goal                    | Problems                                                                         |
| ------- | ----------------------- | -------------------------------------------------------------------------------- |
| Phase 1 | Recognize pop condition | Next Greater, Daily Temperatures, Final Prices, Stock Span                       |
| Phase 2 | Boundary/range thinking | Histogram, Maximal Rectangle, Height of Soldiers                                 |
| Phase 3 | Contribution thinking   | Sum of Subarray Minimums, Max Subarray Min Product                               |
| Phase 4 | Greedy stack            | Remove K Digits, Remove Duplicate Letters                                        |
| Phase 5 | Hidden stack            | Trapping Rain Water, Maximum Width Ramp, Visible People, Steps to Non-decreasing |

---

## Master Rule

```text
If current element makes previous elements useless,
monotonic stack is probably the pattern.
```

## Final Checklist Before Coding

```text
1. Stack stores index or value?
2. Increasing or decreasing?
3. Pop condition?
4. What to calculate when popping?
5. What remains after popping?
6. Need tie handling: <, <=, >, >= ?
```


---

# Part 3 — Heap / Priority Queue FAANG + CP Mastery


## Heap / Priority Queue — FAANG + CP Phase-Wise Problem Guide

> Goal: Master heap / priority queue for **FAANG interviews first**, then move into **advanced CP contest patterns**.
>
> Heap is a **current-best-candidate engine**:
>
> ```text
> Whenever you repeatedly need the current min/max/best candidate,
> priority_queue should come to mind.
> ```

---

## Clickable Index

### 0. Mental Map
- [0.1 Heap Recognition Map](#01-heap-recognition-map)
- [0.2 Min Heap vs Max Heap](#02-min-heap-vs-max-heap)
- [0.3 C++ Priority Queue Templates](#03-c-priority-queue-templates)
- [0.4 FAANG vs CP Split](#04-faang-vs-cp-split)

### FAANG Track

### Phase F1 — Basic Heap Thinking
- [1. Kth Largest Element in an Array](#1-kth-largest-element-in-an-array)
- [2. Last Stone Weight](#2-last-stone-weight)

### Phase F2 — Top K Patterns
- [3. Top K Frequent Elements](#3-top-k-frequent-elements)
- [4. K Closest Points to Origin](#4-k-closest-points-to-origin)

### Phase F3 — Streaming Heap
- [5. Kth Largest Element in a Stream](#5-kth-largest-element-in-a-stream)
- [6. Find Median from Data Stream](#6-find-median-from-data-stream)

### Phase F4 — K-Way Merge
- [7. Merge K Sorted Lists](#7-merge-k-sorted-lists)
- [8. Find K Pairs with Smallest Sums](#8-find-k-pairs-with-smallest-sums)

### Phase F5 — Scheduling / Resource Allocation
- [9. Meeting Rooms II](#9-meeting-rooms-ii)
- [10. Task Scheduler](#10-task-scheduler)

### Phase F6 — Greedy Heap
- [11. Minimum Cost to Connect Sticks](#11-minimum-cost-to-connect-sticks)
- [12. IPO](#12-ipo)

### CP Advanced Track

### Phase CP1 — Heap + Graph Shortest Path
- [13. Network Delay Time](#13-network-delay-time)

### Phase CP2 — 0/1 Edge Alternative Awareness
- [14. Minimum Cost to Make Valid Path in Grid](#14-minimum-cost-to-make-valid-path-in-grid)

### Phase CP3 — Lazy Deletion / Stale States
- [15. Sliding Window Median](#15-sliding-window-median)

### Phase CP4 — Sweep Line + Heap
- [16. The Skyline Problem](#16-the-skyline-problem)

### Phase CP5 — Heap + Binary Search / Ordered Generation
- [17. Kth Smallest Element in a Sorted Matrix](#17-kth-smallest-element-in-a-sorted-matrix)

### Final Revision
- [Heap Pattern Decision Table](#heap-pattern-decision-table)
- [Must-Solve Order](#must-solve-order)
- [One-Line Mental Model](#one-line-mental-model)

---

## 0. Mental Map

### 0.1 Heap Recognition Map

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

### 0.2 Min Heap vs Max Heap

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

### 0.3 C++ Priority Queue Templates

#### Max Heap

```cpp
priority_queue<int> pq;
```

#### Min Heap

```cpp
priority_queue<int, vector<int>, greater<int>> pq;
```

#### Max Heap of Pairs

```cpp
priority_queue<pair<int,int>> pq;
```

#### Min Heap of Pairs

```cpp
priority_queue<pair<int,int>, vector<pair<int,int>>, greater<pair<int,int>>> pq;
```

#### Min Heap of Tuples

```cpp
using T = tuple<int,int,int>;
priority_queue<T, vector<T>, greater<T>> pq;
```

---

### 0.4 FAANG vs CP Split

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

## FAANG TRACK

---

## Phase F1 — Basic Heap Thinking

### 1. Kth Largest Element in an Array

#### Problem Statement

Given an integer array `nums` and an integer `k`, return the kth largest element.

#### Input

```text
nums = [3, 2, 1, 5, 6, 4]
k = 2
```

#### Output

```text
5
```

#### Pattern

```text
Top K largest using min heap of size K.
```

#### Why This Pattern Works

We only care about the largest `k` elements.

Keep a min heap of size `k`.

```text
heap top = smallest among current top K largest
```

When heap size exceeds `k`, remove the smallest.  
At the end, heap contains exactly the largest `k` values.  
The top is the kth largest.

#### Result

```text
kth largest = 5
```

#### C++ Code

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

#### Step-by-Step Dry Run

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

#### Attractive Note

```text
Min heap of size K is a survival gate.
Only the strongest K values remain.
The weakest survivor is the answer.
```

---

### 2. Last Stone Weight

#### Problem Statement

You are given stones. Each turn, smash the two heaviest stones.

If equal, both disappear.  
If different, the new stone has weight `heavier - lighter`.

Return final stone weight or `0`.

#### Input

```text
stones = [2, 7, 4, 1, 8, 1]
```

#### Output

```text
1
```

#### Pattern

```text
Repeatedly take two maximum values.
Use max heap.
```

#### Why This Pattern Works

Every operation asks for the two heaviest stones.  
A max heap gives the current heaviest in `O(log n)`.

#### Result

```text
final stone = 1
```

#### C++ Code

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

#### Step-by-Step Dry Run

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

#### Attractive Note

```text
If the problem repeatedly asks for the largest or smallest item,
heap is usually the cleanest structure.
```

---

## Phase F2 — Top K Patterns

### 3. Top K Frequent Elements

#### Problem Statement

Given an array `nums`, return the `k` most frequent elements.

#### Input

```text
nums = [1, 1, 1, 2, 2, 3]
k = 2
```

#### Output

```text
[1, 2]
```

#### Pattern

```text
Frequency map + min heap of size K.
```

#### Why This Pattern Works

The ranking key is frequency.

Keep a min heap of size `k`.

```text
heap top = currently weakest among top K frequent elements
```

When size exceeds `k`, remove the lowest frequency.

#### Result

```text
top 2 frequent = [1, 2]
```

#### C++ Code

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

#### Step-by-Step Dry Run

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

#### Attractive Note

```text
Heap ranking key can be anything:
value, frequency, distance, time, profit.
Here the key is frequency.
```

---

### 4. K Closest Points to Origin

#### Problem Statement

Given points on a 2D plane, return the `k` closest points to `(0, 0)`.

#### Input

```text
points = [[1,3], [-2,2]]
k = 1
```

#### Output

```text
[[-2,2]]
```

#### Pattern

```text
Top K smallest distance using max heap of size K.
```

#### Why This Pattern Works

We need `k` smallest distances.

Keep a max heap of size `k`.

```text
heap top = farthest among selected closest K
```

If heap size exceeds `k`, remove the farthest.

#### Result

```text
closest point = [-2, 2]
```

#### C++ Code

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

#### Step-by-Step Dry Run

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

#### Attractive Note

```text
For K closest, keep a max heap.
The worst selected candidate stays on top and gets removed first.
```

---

## Phase F3 — Streaming Heap

### 5. Kth Largest Element in a Stream

#### Problem Statement

Design a class that receives numbers continuously and returns the kth largest after each insertion.

#### Input

```text
k = 3
initial = [4, 5, 8, 2]
add values = [3, 5, 10, 9, 4]
```

#### Output

```text
[4, 5, 5, 8, 8]
```

#### Pattern

```text
Streaming top K using min heap of size K.
```

#### Why This Pattern Works

Maintain the top `k` largest elements seen so far.

```text
heap top = kth largest so far
```

#### Result

```text
after each add, heap top is kth largest
```

#### C++ Code

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

#### Step-by-Step Dry Run

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

#### Attractive Note

```text
Streaming top K is an online filter.
Every new number enters.
Only the strongest K survive.
```

---

### 6. Find Median from Data Stream

#### Problem Statement

Design a data structure supporting:

```text
addNum(num)
findMedian()
```

#### Input

```text
add 1
add 2
findMedian
add 3
findMedian
```

#### Output

```text
1.5
2
```

#### Pattern

```text
Two heaps:
left max heap  = smaller half
right min heap = larger half
```

#### Why This Pattern Works

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

#### Result

```text
median after [1,2] = 1.5
median after [1,2,3] = 2
```

#### C++ Code

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

#### Step-by-Step Dry Run

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

#### Attractive Note

```text
Two heaps create a moving middle line.
Median is always on the boundary.
```

---

## Phase F4 — K-Way Merge

### 7. Merge K Sorted Lists

#### Problem Statement

Given `k` sorted linked lists, merge them into one sorted list.

#### Input

```text
lists = [
  [1,4,5],
  [1,3,4],
  [2,6]
]
```

#### Output

```text
[1,1,2,3,4,4,5,6]
```

#### Pattern

```text
K-way merge using min heap.
```

#### Why This Pattern Works

Each list is sorted.  
The global next smallest must be one of the current heads.

#### Result

```text
merged list = [1,1,2,3,4,4,5,6]
```

#### C++ Code

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

#### Step-by-Step Dry Run

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

#### Attractive Note

```text
K sorted lists become K sorted streams.
Heap picks the smallest stream head each time.
```

---

### 8. Find K Pairs with Smallest Sums

#### Problem Statement

Given two sorted arrays, return `k` pairs with the smallest sums.

#### Input

```text
nums1 = [1, 7, 11]
nums2 = [2, 4, 6]
k = 3
```

#### Output

```text
[[1,2], [1,4], [1,6]]
```

#### Pattern

```text
K-way merge on pair streams.
```

#### Why This Pattern Works

For every `nums1[i]`, pairs with `nums2` form a sorted stream:

```text
(nums1[i], nums2[0])
(nums1[i], nums2[1])
(nums1[i], nums2[2])
```

Merge these streams using min heap.

#### Result

```text
3 smallest pairs = [[1,2], [1,4], [1,6]]
```

#### C++ Code

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

#### Step-by-Step Dry Run

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

#### Attractive Note

```text
When many sorted streams compete,
min heap gives the next global best candidate.
```

---

## Phase F5 — Scheduling / Resource Allocation

### 9. Meeting Rooms II

#### Problem Statement

Given meeting intervals, return the minimum number of rooms required.

#### Input

```text
intervals = [[0,30], [5,10], [15,20]]
```

#### Output

```text
2
```

#### Pattern

```text
Sort by start time + min heap of end times.
```

#### Why This Pattern Works

A room is reusable if its end time is `<= current start`.

Min heap gives the earliest ending room.

#### Result

```text
minimum rooms = 2
```

#### C++ Code

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

#### Step-by-Step Dry Run

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

#### Attractive Note

```text
Min heap answers:
Which room becomes free first?
```

---

### 10. Task Scheduler

#### Problem Statement

Given tasks and cooldown `n`, return least intervals to finish all tasks.

#### Input

```text
tasks = ["A","A","A","B","B","B"]
n = 2
```

#### Output

```text
8
```

One valid order:

```text
A B idle A B idle A B
```

#### Pattern

```text
Max heap by remaining frequency.
```

#### Why This Pattern Works

Most frequent tasks create the biggest cooldown risk.  
Scheduling them first reduces future idle gaps.

#### Result

```text
minimum intervals = 8
```

#### C++ Code

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

#### Step-by-Step Dry Run

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

#### Attractive Note

```text
Most frequent task is the biggest threat.
Heap always attacks the biggest threat first.
```

---

## Phase F6 — Greedy Heap

### 11. Minimum Cost to Connect Sticks

#### Problem Statement

Connect sticks. Cost of connecting two sticks is their sum. Return minimum total cost.

#### Input

```text
sticks = [2, 4, 3]
```

#### Output

```text
14
```

#### Pattern

```text
Repeatedly combine two smallest values.
Use min heap.
```

#### Why This Pattern Works

Small values should be combined first because combined sticks may be paid again later.

This is the Huffman coding greedy idea.

#### Result

```text
minimum cost = 14
```

#### C++ Code

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

#### Step-by-Step Dry Run

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

#### Attractive Note

```text
If merging creates future cost,
merge the cheapest items first.
```

---

### 12. IPO

#### Problem Statement

You can do at most `k` projects.  
Each project has required capital and profit.  
Start with capital `w`.  
Return maximized capital.

#### Input

```text
k = 2
w = 0
profits = [1, 2, 3]
capital = [0, 1, 1]
```

#### Output

```text
4
```

#### Pattern

```text
Sort by capital + max heap by profit.
```

#### Why This Pattern Works

At each round:

```text
1. Unlock all projects with capital <= current money.
2. Pick the maximum profit among unlocked projects.
```

Sorting finds newly unlocked projects.  
Max heap picks best profit.

#### Result

```text
maximum capital = 4
```

#### C++ Code

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

#### Step-by-Step Dry Run

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

#### Attractive Note

```text
Sort unlocks choices.
Heap chooses the best unlocked choice.
```

---

## CP ADVANCED TRACK

---

## Phase CP1 — Heap + Graph Shortest Path

### 13. Network Delay Time

#### Problem Statement

You are given directed weighted edges `times[i] = [u, v, w]`.  
A signal starts from node `k`.  
Return how long it takes to reach all nodes. If impossible, return `-1`.

#### Input

```text
n = 4
times = [[2,1,1], [2,3,1], [3,4,1]]
k = 2
```

#### Output

```text
2
```

#### Pattern

```text
Dijkstra using min heap.
```

#### Why This Pattern Works

All edge weights are positive.  
Dijkstra always expands the currently closest unprocessed node.

```text
heap top = node with smallest known distance
```

#### Result

```text
network delay = 2
```

#### C++ Code

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

#### Step-by-Step Dry Run

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

#### Attractive Note

```text
Dijkstra heap is a frontier of promises.
The smallest promise is trusted first.
```

---

## Phase CP2 — 0/1 Edge Alternative Awareness

### 14. Minimum Cost to Make Valid Path in Grid

#### Problem Statement

Each grid cell has a direction.  
Moving in that direction costs `0`.  
Changing direction costs `1`.  
Find minimum cost from top-left to bottom-right.

#### Input

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

#### Output

```text
0
```

#### Pattern

```text
0/1 BFS using deque.
```

#### Why This Pattern Works

Edge weights are only `0` or `1`.

```text
cost 0 → push_front
cost 1 → push_back
```

This keeps states ordered by distance without full Dijkstra.

#### Result

```text
minimum cost = 0
```

#### C++ Code

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

#### Step-by-Step Dry Run

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

#### Attractive Note

```text
0/1 BFS is the bridge between BFS and Dijkstra.
Use it when edge costs are only 0 or 1.
```

---

## Phase CP3 — Lazy Deletion / Stale States

### 15. Sliding Window Median

#### Problem Statement

Given `nums` and window size `k`, return median of each sliding window.

#### Input

```text
nums = [1, 3, -1, -3, 5, 3, 6, 7]
k = 3
```

#### Output

```text
[1, -1, -1, 3, 5, 6]
```

#### Pattern

```text
Two heaps + lazy deletion.
```

#### Why This Pattern Works

Median requires balanced halves.  
Sliding window requires deletion.

Heap cannot delete arbitrary values efficiently.  
So we mark deleted values in a map and remove them only when they reach heap top.

#### Result

```text
medians = [1, -1, -1, 3, 5, 6]
```

#### C++ Code

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

#### Step-by-Step Dry Run

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

#### Attractive Note

```text
Lazy deletion:
Do not remove now.
Mark dead.
When it reaches the top, throw it away.
```

---

## Phase CP4 — Sweep Line + Heap

### 16. The Skyline Problem

#### Problem Statement

Given buildings `[left, right, height]`, return the skyline key points.

#### Input

```text
buildings = [
  [2,9,10],
  [3,7,15],
  [5,12,12],
  [15,20,10],
  [19,24,8]
]
```

#### Output

```text
[[2,10], [3,15], [7,12], [12,0], [15,10], [20,8], [24,0]]
```

#### Pattern

```text
Sweep line + max heap of active buildings.
```

#### Why This Pattern Works

At each x-coordinate, skyline height is the maximum height among active buildings.

Heap stores active buildings by height.

Remove expired buildings where `right <= current x`.

#### Result

```text
skyline changes when current max height changes
```

#### C++ Code

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

#### Step-by-Step Dry Run

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

#### Attractive Note

```text
Sweep line moves left to right.
Heap/multiset answers:
What is the tallest active building right now?
```

---

## Phase CP5 — Heap + Binary Search / Ordered Generation

### 17. Kth Smallest Element in a Sorted Matrix

#### Problem Statement

Given an `n x n` matrix where each row and column is sorted, return the kth smallest element.

#### Input

```text
matrix =
[
  [1,  5,  9],
  [10, 11, 13],
  [12, 13, 15]
]
k = 8
```

#### Output

```text
13
```

#### Pattern

```text
Min heap ordered generation from sorted rows.
```

#### Why This Pattern Works

Each row is sorted.

Treat each row as a sorted stream.  
Use min heap to repeatedly take the next smallest available value.

#### Result

```text
8th smallest = 13
```

#### C++ Code

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

#### Step-by-Step Dry Run

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

#### Attractive Note

```text
Sorted matrix can be seen as multiple sorted streams.
Heap generates values in sorted order without flattening everything.
```

---

## Heap Pattern Decision Table

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

## Must-Solve Order

### FAANG First

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

### CP Advanced Later

```text
11. Network Delay Time / Dijkstra
12. Minimum Cost Valid Path / 0-1 BFS
13. Sliding Window Median
14. Skyline Problem
15. Kth Smallest in Sorted Matrix
```

---

## One-Line Mental Model

```text
Priority queue is the right tool when the current best candidate must be repeatedly selected.
```

---

## Final Revision Notes

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


---

# Part 4 — Range Mapping / Interval Coverage Technique Playbook


## Range Mapping / Interval Coverage — Complete Technique Playbook

A CP/DSA guide for interval/range problems using STL, binary search, sweep line, and `set<pair<int,int>>`.

### Clickable Index

- [0. Problem Family](#0-problem-family)
- [1. Core Mental Model](#1-core-mental-model)
- [2. STL Binary Search Helpers](#2-stl-binary-search-helpers)
- [3. Technique 1 — Brute Force Interval Scan](#3-technique-1--brute-force-interval-scan)
- [4. Technique 2 — Offline Point Coverage Check](#4-technique-2--offline-point-coverage-check)
- [5. Technique 3 — Count Number of Intervals Covering a Point](#5-technique-3--count-number-of-intervals-covering-a-point)
- [6. Technique 4 — Intermixed Insert + Point Query using `set<pair<int,int>>`](#6-technique-4--intermixed-insert--point-query-using-setpairintint)
- [7. Technique 5 — Insert and Merge Overlapping Intervals](#7-technique-5--insert-and-merge-overlapping-intervals)
- [8. Technique 6 — Delete / Remove a Range from Coverage](#8-technique-6--delete--remove-a-range-from-coverage)
- [9. Technique 7 — Check Whether Interval `[l, r]` is Fully Covered](#9-technique-7--check-whether-interval-l-r-is-fully-covered)
- [10. Technique 8 — Check Whether New Interval Overlaps Existing Intervals](#10-technique-8--check-whether-new-interval-overlaps-existing-intervals)
- [11. Technique 9 — Sweep Line for Maximum Overlap](#11-technique-9--sweep-line-for-maximum-overlap)
- [12. Technique 10 — Difference Array for Range Add Queries](#12-technique-10--difference-array-for-range-add-queries)
- [13. Technique 11 — Coordinate Compression for Huge Coordinates](#13-technique-11--coordinate-compression-for-huge-coordinates)
- [14. Technique 12 — Ordered Events for Start/End Query Processing](#14-technique-12--ordered-events-for-startend-query-processing)
- [15. Common STL Pair Tricks](#15-common-stl-pair-tricks)
- [16. Quick Decision Table](#16-quick-decision-table)

---

### 0. Problem Family

You are given a number line and intervals.

Common query types:

```text
+ l r     insert interval [l, r]
? x       check whether point x is covered
? x       count intervals covering point x
? l r     check whether full interval [l, r] is covered
- l r     delete coverage from [l, r]
```

Example:

```text
insert [2, 5]
insert [3, 10]
insert [13, 16]
query 4   -> covered
query 12  -> not covered
```

Number line:

```text
      [2---------5]
        [3----------------10]
                            [13-----16]
----1---2---3---4---5---6---10---12---13---16---->
                ^ covered                ^ not covered
                4                        12
```

---

### 1. Core Mental Model

For point `x`, an interval `[l, r]` covers `x` if:

```text
l <= x <= r
```

So an interval does **not** cover `x` if:

```text
r < x       ends before x
or
l > x       starts after x
```

Therefore:

```text
covered_count = total_intervals - intervals_ending_before_x - intervals_starting_after_x
```

This is the key idea behind the offline binary-search method.

---

### 2. STL Binary Search Helpers

`lower_bound` and `upper_bound` work only on sorted arrays.

```cpp
lower_bound(v.begin(), v.end(), x)
```

Returns iterator to first element `>= x`.

```cpp
upper_bound(v.begin(), v.end(), x)
```

Returns iterator to first element `> x`.

Example:

```text
v = [2, 3, 5, 5, 10]
x = 5

lower_bound(5) -> index 2  first >= 5
upper_bound(5) -> index 4  first > 5
```

Counting formulas:

```cpp
countLessThanX      = lower_bound(v.begin(), v.end(), x) - v.begin();
countLessEqualX     = upper_bound(v.begin(), v.end(), x) - v.begin();
countGreaterThanX   = v.end() - upper_bound(v.begin(), v.end(), x);
countGreaterEqualX  = v.end() - lower_bound(v.begin(), v.end(), x);
countEqualX         = upper_bound(v.begin(), v.end(), x) - lower_bound(v.begin(), v.end(), x);
```

---

### 3. Technique 1 — Brute Force Interval Scan

Use when constraints are small.

#### Idea

Store all intervals in a vector. For each point query, scan every interval and check `l <= x <= r`.

#### Dry Run

```text
ranges = [ [2,5], [3,10], [13,16] ]
x = 4

Check [2,5]   -> 2 <= 4 <= 5   yes
Answer = covered
```

For `x = 12`:

```text
[2,5]    no
[3,10]   no
[13,16]  no
Answer = not covered
```

#### C++ Code

<details>
<summary>Brute force point coverage</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isCoveredBrute(vector<pair<int,int>>& ranges, int x) {
    for (auto [l, r] : ranges) {
        if (l <= x && x <= r) return true;
    }
    return false;
}

int main() {
    vector<pair<int,int>> ranges = {{2, 5}, {3, 10}, {13, 16}};

    cout << isCoveredBrute(ranges, 4) << "\n";   // 1
    cout << isCoveredBrute(ranges, 12) << "\n";  // 0
}
```

</details>

#### Complexity

```text
Insert: O(1)
Query:  O(n)
```

---

### 4. Technique 2 — Offline Point Coverage Check

Use when all intervals are given first, then all point queries come later.

#### Idea

Create two sorted arrays:

```text
L = all left endpoints
R = all right endpoints
```

For point `x`:

```text
startAfter = number of l > x
endBefore  = number of r < x
covered    = n - startAfter - endBefore
```

If `covered > 0`, point is covered by at least one interval.

#### Dry Run

```text
ranges = [ [2,5], [3,10], [13,16] ]
L = [2, 3, 13]
R = [5, 10, 16]
```

Query `x = 4`:

```text
l > 4:  [13]       count = 1
r < 4:  []         count = 0
covered = 3 - 1 - 0 = 2
Answer = covered
```

Query `x = 12`:

```text
l > 12: [13]       count = 1
r < 12: [5,10]     count = 2
covered = 3 - 1 - 2 = 0
Answer = not covered
```

#### C++ Code

<details>
<summary>Offline point coverage using lower_bound / upper_bound</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

struct OfflineCoverage {
    vector<int> L, R;
    int n = 0;

    OfflineCoverage(vector<pair<int,int>>& ranges) {
        n = ranges.size();
        for (auto [l, r] : ranges) {
            L.push_back(l);
            R.push_back(r);
        }
        sort(L.begin(), L.end());
        sort(R.begin(), R.end());
    }

    bool isCovered(int x) {
        int startAfter = L.end() - upper_bound(L.begin(), L.end(), x); // l > x
        int endBefore = lower_bound(R.begin(), R.end(), x) - R.begin(); // r < x
        int covered = n - startAfter - endBefore;
        return covered > 0;
    }
};

int main() {
    vector<pair<int,int>> ranges = {{2, 5}, {3, 10}, {13, 16}};
    OfflineCoverage oc(ranges);

    cout << oc.isCovered(4) << "\n";   // 1
    cout << oc.isCovered(12) << "\n";  // 0
}
```

</details>

#### Complexity

```text
Build: O(n log n)
Query: O(log n)
```

---

### 5. Technique 3 — Count Number of Intervals Covering a Point

This is almost the same as Technique 2, but instead of boolean, return the count.

#### Formula

```text
covering(x) = number_of_l <= x - number_of_r < x
```

Why?

```text
started by x - already ended before x
```

#### Dry Run

```text
ranges = [ [2,5], [3,10], [13,16] ]
L = [2,3,13]
R = [5,10,16]
```

For `x = 4`:

```text
l <= 4 = 2    intervals [2,5], [3,10] have started
r < 4  = 0    none ended
answer = 2
```

For `x = 5`:

```text
l <= 5 = 2
r < 5  = 0
answer = 2
```

For `x = 12`:

```text
l <= 12 = 2
r < 12  = 2
answer = 0
```

#### C++ Code

<details>
<summary>Count intervals covering point</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

struct IntervalCounter {
    vector<int> L, R;

    IntervalCounter(vector<pair<int,int>>& ranges) {
        for (auto [l, r] : ranges) {
            L.push_back(l);
            R.push_back(r);
        }
        sort(L.begin(), L.end());
        sort(R.begin(), R.end());
    }

    int countCovering(int x) {
        int started = upper_bound(L.begin(), L.end(), x) - L.begin(); // l <= x
        int endedBefore = lower_bound(R.begin(), R.end(), x) - R.begin(); // r < x
        return started - endedBefore;
    }
};

int main() {
    vector<pair<int,int>> ranges = {{2, 5}, {3, 10}, {13, 16}};
    IntervalCounter counter(ranges);

    cout << counter.countCovering(4) << "\n";   // 2
    cout << counter.countCovering(12) << "\n";  // 0
}
```

</details>

---

### 6. Technique 4 — Intermixed Insert + Point Query using `set<pair<int,int>>`

Use when insert queries and point queries are mixed online.

Maintain disjoint merged intervals in a set:

```cpp
set<pair<int,int>> ranges;
```

Each pair is `{l, r}`.

#### Key Idea for Point Query

To check if `x` is covered, find the interval with the largest `l <= x`.

```cpp
auto it = ranges.upper_bound({x, INT_MAX});
```

This gives first interval with `l > x`. So move one step back.

```cpp
--it;
```

Now check:

```cpp
it->first <= x && x <= it->second
```

#### Why `{x, INT_MAX}`?

Pairs are compared lexicographically:

```text
first compare first value
if equal, compare second value
```

Using `{x, INT_MAX}` means:

```text
skip all intervals whose l == x
return first interval whose l > x
```

Then previous interval is the best candidate with `l <= x`.

#### Dry Run

```text
ranges = { [2,10], [13,16] }
x = 4

upper_bound({4, INF}) -> [13,16]
previous -> [2,10]
4 <= 10 -> covered
```

For `x = 12`:

```text
upper_bound({12, INF}) -> [13,16]
previous -> [2,10]
12 <= 10 false -> not covered
```

#### C++ Code

<details>
<summary>Online point coverage with set</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

struct RangeSet {
    set<pair<int,int>> ranges;

    bool isPointCovered(int x) {
        auto it = ranges.upper_bound({x, INT_MAX});

        if (it == ranges.begin()) return false;

        --it;
        return it->first <= x && x <= it->second;
    }

    void insertSimple(int l, int r) {
        ranges.insert({l, r});
    }
};

int main() {
    RangeSet rs;
    rs.insertSimple(2, 10);
    rs.insertSimple(13, 16);

    cout << rs.isPointCovered(4) << "\n";   // 1
    cout << rs.isPointCovered(12) << "\n";  // 0
}
```

</details>

---

### 7. Technique 5 — Insert and Merge Overlapping Intervals

Use when intervals can overlap and you want to maintain minimum disjoint intervals.

#### Idea

Before inserting `[l, r]`, remove all intervals that overlap with it and merge them.

Two intervals overlap if:

```text
existing_l <= r && existing_r >= l
```

For merged coverage, you may also merge touching intervals:

```text
existing_l <= r + 1 && existing_r + 1 >= l
```

#### Dry Run

```text
current = [ [2,5], [8,10], [13,16] ]
insert  = [4,9]

[2,5] overlaps [4,9] -> merge to [2,9]
[8,10] overlaps [2,9] -> merge to [2,10]
[13,16] no overlap

result = [ [2,10], [13,16] ]
```

#### C++ Code

<details>
<summary>Insert and merge interval</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

struct RangeSet {
    set<pair<int,int>> ranges;

    void insertRange(int l, int r) {
        if (l > r) swap(l, r);

        // Find first interval that may overlap.
        auto it = ranges.lower_bound({l, INT_MIN});

        // Previous interval may also overlap with [l, r].
        if (it != ranges.begin()) {
            auto prevIt = prev(it);
            if (prevIt->second >= l - 1) {
                it = prevIt;
            }
        }

        while (it != ranges.end() && it->first <= r + 1) {
            if (it->second < l - 1) {
                ++it;
                continue;
            }

            l = min(l, it->first);
            r = max(r, it->second);
            it = ranges.erase(it);
        }

        ranges.insert({l, r});
    }

    void print() {
        for (auto [l, r] : ranges) {
            cout << "[" << l << "," << r << "] ";
        }
        cout << "\n";
    }
};

int main() {
    RangeSet rs;
    rs.insertRange(2, 5);
    rs.insertRange(8, 10);
    rs.insertRange(13, 16);
    rs.insertRange(4, 9);

    rs.print(); // [2,10] [13,16]
}
```

</details>

#### Complexity

```text
O(k log n)
```

where `k` is number of intervals removed/merged.

---

### 8. Technique 6 — Delete / Remove a Range from Coverage

Use when you maintain covered ranges and need to remove `[l, r]`.

#### Cases

Existing interval `[a, b]`, remove `[l, r]`.

```text
No overlap:
[a-----b]        [l-----r]

Remove middle:
[a-------------b]
      [l---r]
=> [a,l-1] and [r+1,b]

Remove left part:
[a-------------b]
[a----r]
=> [r+1,b]

Remove right part:
[a-------------b]
        [l----b]
=> [a,l-1]

Remove all:
[a-------------b]
[l-------------r]
=> deleted
```

#### Dry Run

```text
current = [ [2,10], [13,16] ]
remove  = [4,8]

[2,10] becomes [2,3] and [9,10]
[13,16] unchanged

result = [ [2,3], [9,10], [13,16] ]
```

#### C++ Code

<details>
<summary>Remove interval from covered ranges</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

struct RangeSet {
    set<pair<int,int>> ranges;

    void insertRange(int l, int r) {
        auto it = ranges.lower_bound({l, INT_MIN});
        if (it != ranges.begin()) {
            auto p = prev(it);
            if (p->second >= l - 1) it = p;
        }

        while (it != ranges.end() && it->first <= r + 1) {
            if (it->second < l - 1) {
                ++it;
                continue;
            }
            l = min(l, it->first);
            r = max(r, it->second);
            it = ranges.erase(it);
        }
        ranges.insert({l, r});
    }

    void removeRange(int l, int r) {
        vector<pair<int,int>> addBack;

        auto it = ranges.lower_bound({l, INT_MIN});
        if (it != ranges.begin()) --it;

        while (it != ranges.end()) {
            auto [a, b] = *it;

            if (b < l) {
                ++it;
                continue;
            }
            if (a > r) break;

            it = ranges.erase(it);

            if (a < l) addBack.push_back({a, l - 1});
            if (r < b) addBack.push_back({r + 1, b});
        }

        for (auto p : addBack) ranges.insert(p);
    }

    void print() {
        for (auto [l, r] : ranges) cout << "[" << l << "," << r << "] ";
        cout << "\n";
    }
};

int main() {
    RangeSet rs;
    rs.insertRange(2, 10);
    rs.insertRange(13, 16);

    rs.removeRange(4, 8);
    rs.print(); // [2,3] [9,10] [13,16]
}
```

</details>

---

### 9. Technique 7 — Check Whether Interval `[l, r]` is Fully Covered

Use when intervals are merged and disjoint.

#### Idea

Find interval with largest start `<= l`. Then check whether its end covers `r`.

```text
candidate.start <= l and candidate.end >= r
```

#### Dry Run

```text
ranges = [ [2,10], [13,16] ]
query [4,8]

candidate with start <= 4 is [2,10]
10 >= 8 -> fully covered
```

Query `[4,12]`:

```text
candidate = [2,10]
10 >= 12 false
not fully covered
```

#### C++ Code

<details>
<summary>Check full interval coverage</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

struct RangeSet {
    set<pair<int,int>> ranges;

    bool isRangeCovered(int l, int r) {
        auto it = ranges.upper_bound({l, INT_MAX});
        if (it == ranges.begin()) return false;

        --it;
        return it->first <= l && r <= it->second;
    }
};
```

</details>

---

### 10. Technique 8 — Check Whether New Interval Overlaps Existing Intervals

Use in calendar booking, meeting room, interval conflict detection.

#### Overlap Rule

Two closed intervals overlap if:

```text
max(l1, l2) <= min(r1, r2)
```

Equivalent:

```text
l1 <= r2 && l2 <= r1
```

For half-open intervals `[start, end)`, overlap if:

```text
start1 < end2 && start2 < end1
```

#### Dry Run

```text
existing = [ [2,5], [8,10] ]
new = [6,7]

No overlap.
```

```text
existing = [ [2,5], [8,10] ]
new = [5,8]

Closed intervals: overlaps both at 5 and 8.
Half-open intervals: [5,8) does not overlap [2,5) or [8,10).
```

#### C++ Code

<details>
<summary>Check overlap with set</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

struct CalendarClosedIntervals {
    set<pair<int,int>> ranges;

    bool hasOverlap(int l, int r) {
        auto it = ranges.lower_bound({l, INT_MIN});

        // Current interval may start before/equal r.
        if (it != ranges.end() && it->first <= r) return true;

        // Previous interval may end after/equal l.
        if (it != ranges.begin()) {
            --it;
            if (it->second >= l) return true;
        }

        return false;
    }

    bool book(int l, int r) {
        if (hasOverlap(l, r)) return false;
        ranges.insert({l, r});
        return true;
    }
};
```

</details>

---

### 11. Technique 9 — Sweep Line for Maximum Overlap

Use when you need max active intervals, number of meeting rooms, most crowded time, etc.

#### Idea

Convert interval `[l, r]` into events:

```text
(l, +1) start
(r+1, -1) end after r   for closed integer intervals
```

Sort events and scan prefix sum.

#### Dry Run

```text
intervals = [ [2,5], [3,10], [13,16] ]

events:
2  +1
6  -1
3  +1
11 -1
13 +1
17 -1
```

Sorted:

```text
2  +1  active = 1
3  +1  active = 2   max = 2
6  -1  active = 1
11 -1  active = 0
13 +1  active = 1
17 -1  active = 0
```

Answer:

```text
maximum overlap = 2
```

#### C++ Code

<details>
<summary>Sweep line maximum overlap</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxOverlapClosedIntervals(vector<pair<int,int>>& intervals) {
    vector<pair<int,int>> events;

    for (auto [l, r] : intervals) {
        events.push_back({l, +1});
        events.push_back({r + 1, -1});
    }

    sort(events.begin(), events.end());

    int active = 0, ans = 0;
    for (auto [pos, delta] : events) {
        active += delta;
        ans = max(ans, active);
    }

    return ans;
}

int main() {
    vector<pair<int,int>> intervals = {{2, 5}, {3, 10}, {13, 16}};
    cout << maxOverlapClosedIntervals(intervals) << "\n"; // 2
}
```

</details>

---

### 12. Technique 10 — Difference Array for Range Add Queries

Use when coordinates are small or compressed.

#### Problem

Apply many range updates:

```text
add +v to all positions in [l, r]
```

#### Idea

For closed interval `[l, r]`:

```cpp
diff[l] += v;
diff[r + 1] -= v;
```

Prefix sum gives final values.

#### Dry Run

```text
n = 8
updates:
[2,5] +1
[3,7] +1
```

Diff:

```text
diff[2] += 1
diff[6] -= 1
diff[3] += 1
diff[8] -= 1
```

Prefix result:

```text
idx:    1 2 3 4 5 6 7 8
value:  0 1 2 2 2 1 1 0
```

#### C++ Code

<details>
<summary>Difference array range add</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n = 8;
    vector<int> diff(n + 2, 0);

    auto addRange = [&](int l, int r, int val) {
        diff[l] += val;
        diff[r + 1] -= val;
    };

    addRange(2, 5, 1);
    addRange(3, 7, 1);

    vector<int> a(n + 1);
    for (int i = 1; i <= n; i++) {
        a[i] = a[i - 1] + diff[i];
    }

    for (int i = 1; i <= n; i++) cout << a[i] << " ";
    cout << "\n";
}
```

</details>

---

### 13. Technique 11 — Coordinate Compression for Huge Coordinates

Use when coordinates are huge, but number of unique coordinates is small.

#### Idea

Collect all useful coordinates, sort, unique, then map each original coordinate to compressed index.

For interval problems, often collect:

```text
l, r, r+1, query points
```

#### Dry Run

```text
coordinates = [1000000000, 5, 10, 5]
unique sorted = [5, 10, 1000000000]

5          -> 0
10         -> 1
1000000000 -> 2
```

#### C++ Code

<details>
<summary>Coordinate compression helper</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Compressor {
    vector<int> vals;

    void add(int x) {
        vals.push_back(x);
    }

    void build() {
        sort(vals.begin(), vals.end());
        vals.erase(unique(vals.begin(), vals.end()), vals.end());
    }

    int get(int x) {
        return lower_bound(vals.begin(), vals.end(), x) - vals.begin();
    }
};

int main() {
    Compressor comp;
    comp.add(1000000000);
    comp.add(5);
    comp.add(10);
    comp.add(5);
    comp.build();

    cout << comp.get(5) << "\n";          // 0
    cout << comp.get(10) << "\n";         // 1
    cout << comp.get(1000000000) << "\n"; // 2
}
```

</details>

---

### 14. Technique 12 — Ordered Events for Start/End Query Processing

Use when you have offline queries and want answers while sweeping.

#### Example Problem

Given intervals and query points, count active intervals at each query point.

#### Event Ordering for Closed Intervals

At same coordinate:

```text
start should happen before query
query should happen before end
```

So `[l, r]` covers both `l` and `r`.

Use event type ordering:

```text
0 = start
1 = query
2 = end
```

#### Dry Run

```text
intervals = [ [2,5], [3,10] ]
queries = [2,5,6]
```

Events:

```text
2 start
2 query id0
3 start
5 query id1
5 end
6 query id2
10 end
```

Scan:

```text
x=2 start active=1
x=2 query ans[0]=1
x=3 start active=2
x=5 query ans[1]=2
x=5 end active=1
x=6 query ans[2]=1
```

#### C++ Code

<details>
<summary>Offline event sweep for query points</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Event {
    int x;
    int type; // 0=start, 1=query, 2=end
    int id;

    bool operator<(const Event& other) const {
        if (x != other.x) return x < other.x;
        return type < other.type;
    }
};

vector<int> countCoveringBySweep(vector<pair<int,int>>& intervals, vector<int>& queries) {
    vector<Event> events;

    for (auto [l, r] : intervals) {
        events.push_back({l, 0, -1});
        events.push_back({r, 2, -1});
    }

    for (int i = 0; i < (int)queries.size(); i++) {
        events.push_back({queries[i], 1, i});
    }

    sort(events.begin(), events.end());

    vector<int> ans(queries.size());
    int active = 0;

    for (auto e : events) {
        if (e.type == 0) active++;
        else if (e.type == 1) ans[e.id] = active;
        else active--;
    }

    return ans;
}

int main() {
    vector<pair<int,int>> intervals = {{2,5}, {3,10}};
    vector<int> queries = {2,5,6};

    auto ans = countCoveringBySweep(intervals, queries);
    for (int x : ans) cout << x << " ";
    cout << "\n"; // 1 2 1
}
```

</details>

---

### 15. Common STL Pair Tricks

#### `upper_bound({x, INT_MAX})`

Use to find first interval whose start is greater than `x`.

```cpp
auto it = s.upper_bound({x, INT_MAX});
```

Then `prev(it)` gives interval with largest `l <= x`.

#### `lower_bound({l, INT_MIN})`

Use to find first interval whose start is at least `l`.

```cpp
auto it = s.lower_bound({l, INT_MIN});
```

#### Why `INT_MIN` and `INT_MAX`?

Because pair comparison checks first value first, then second value.

```text
{1, 2} < {1, 5}  true
{2, 0} < {3, -100} true
```

So:

```text
{x, INT_MAX} is after every pair starting with x
{x, INT_MIN} is before every pair starting with x
```

---

### 16. Quick Decision Table

| Problem Type | Best Technique |
|---|---|
| Small constraints | Brute force scan |
| All intervals first, point queries later | Offline binary search on L/R arrays |
| Need count covering point | `started - endedBefore` |
| Insert and query online | `set<pair<int,int>>` |
| Maintain merged coverage | Insert + merge intervals |
| Remove covered range | Split intervals after deletion |
| Check full `[l,r]` coverage | Find previous interval by start |
| Check conflict / overlap | Check current and previous interval in set |
| Maximum overlap | Sweep line |
| Many range add updates | Difference array |
| Huge coordinates | Coordinate compression |
| Offline intervals + queries | Ordered event sweep |

---

### Final Recall Template

```text
Point x covered by [l,r] if l <= x <= r.
Not covered if r < x or l > x.

Offline:
covered = n - count(r < x) - count(l > x)

Counting:
covering = count(l <= x) - count(r < x)

Online set:
Find interval with largest l <= x using upper_bound({x, INF}) then --it.

Merged intervals:
Always keep set disjoint and sorted.
Insert = merge overlaps.
Delete = split existing intervals.
```


---

# Part 5 — Range Mapping Phase-Wise Practice Guide


## Range Mapping Phase-Wise Practice Guide

### How to Use This

```text
Phase 1 -> learn interval meaning and point coverage
Phase 2 -> learn offline binary search with L/R endpoints
Phase 3 -> learn online interval set operations
Phase 4 -> learn merge/delete/full coverage thinking
Phase 5 -> learn sweep line, difference array, and coordinate compression
```

---

### Clickable Index

- [How to Use This](#how-to-use-this)
- [Phase 1 — Recognition Problems](#phase-1--recognition-problems)
  - [1. Check if a Point is Covered by Any Interval — Brute Force](#1-check-if-a-point-is-covered-by-any-interval--brute-force)
  - [2. Count Covered Points in Small Coordinate Range](#2-count-covered-points-in-small-coordinate-range)
- [Phase 2 — Offline Binary Search on Endpoints](#phase-2--offline-binary-search-on-endpoints)
  - [3. Offline Point Coverage Check](#3-offline-point-coverage-check)
  - [4. Count Number of Intervals Covering a Point](#4-count-number-of-intervals-covering-a-point)
  - [5. Minimum Interval to Include Each Query](#5-minimum-interval-to-include-each-query)
- [Phase 3 — Online Range Mapping with set<pair<int,int>>](#phase-3--online-range-mapping-with-setpairintint)
  - [6. Online Insert Interval and Point Query](#6-online-insert-interval-and-point-query)
  - [7. Check Whether a New Interval Overlaps Existing Intervals](#7-check-whether-a-new-interval-overlaps-existing-intervals)
- [Phase 4 — Merged Interval Maintenance](#phase-4--merged-interval-maintenance)
  - [8. Insert and Merge Overlapping Intervals](#8-insert-and-merge-overlapping-intervals)
  - [9. Remove a Range from Covered Intervals](#9-remove-a-range-from-covered-intervals)
  - [10. Check Whether Full Range is Covered](#10-check-whether-full-range-is-covered)
- [Phase 5 — Sweep Line / Difference Array / Compression](#phase-5--sweep-line--difference-array--compression)
  - [11. Maximum Number of Overlapping Intervals](#11-maximum-number-of-overlapping-intervals)
  - [12. Range Addition Using Difference Array](#12-range-addition-using-difference-array)
  - [13. Coordinate Compression for Huge Ranges](#13-coordinate-compression-for-huge-ranges)
  - [14. Offline Event Sweep for Query Points](#14-offline-event-sweep-for-query-points)
- [Final Phase Summary](#final-phase-summary)
- [Master Rule](#master-rule)
- [Final Checklist Before Coding](#final-checklist-before-coding)

---

## Phase 1 — Recognition Problems

Goal:

```text
Understand what interval coverage means on a number line.
```

Core question:

```text
Does [l, r] contain x?
```

---

### 1. Check if a Point is Covered by Any Interval — Brute Force

#### Problem Statement

Given intervals and a point `x`, check whether any interval covers `x`.

Example:

```text
ranges = [[2,5], [8,10], [13,16]]
x = 4
answer = true
```

#### Pattern

```text
Interval [l, r] covers x if l <= x <= r.
Scan every interval.
```

#### Thinking

```text
Small constraints -> direct scan is enough.
Every interval gets one chance to prove x is covered.
```

#### Dry Run

```text
ranges = [[2,5], [8,10], [13,16]]
x = 4

Check [2,5]   -> 2 <= 4 <= 5  yes
Answer = covered
```

For `x = 12`:

```text
Check [2,5]    -> no
Check [8,10]   -> no
Check [13,16]  -> no
Answer = not covered
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isCoveredBrute(vector<pair<int,int>>& ranges, int x) {
    for (auto [l, r] : ranges) {
        if (l <= x && x <= r) return true;
    }
    return false;
}

int main() {
    vector<pair<int,int>> ranges = {{2,5}, {8,10}, {13,16}};

    cout << isCoveredBrute(ranges, 4) << "\n";   // 1
    cout << isCoveredBrute(ranges, 12) << "\n";  // 0
}
```

---

### 2. Count Covered Points in Small Coordinate Range

#### Problem Statement

Given intervals with small coordinates, count how many integer points are covered by at least one interval.

Example:

```text
ranges = [[2,5], [4,7], [10,11]]
covered points = {2,3,4,5,6,7,10,11}
answer = 8
```

#### Pattern

```text
Small coordinate range -> mark covered points using array.
```

#### Thinking

```text
Instead of storing intervals, directly mark the number line.
```

#### Dry Run

```text
range [2,5] marks 2 3 4 5
range [4,7] marks 4 5 6 7
range [10,11] marks 10 11

covered points:
2 3 4 5 6 7 10 11
answer = 8
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int countCoveredSmall(vector<pair<int,int>>& ranges, int maxCoord) {
    vector<int> covered(maxCoord + 1, 0);

    for (auto [l, r] : ranges) {
        for (int x = l; x <= r; x++) {
            covered[x] = 1;
        }
    }

    int ans = 0;
    for (int x = 0; x <= maxCoord; x++) {
        ans += covered[x];
    }
    return ans;
}

int main() {
    vector<pair<int,int>> ranges = {{2,5}, {4,7}, {10,11}};
    cout << countCoveredSmall(ranges, 20) << "\n"; // 8
}
```

---

## Phase 2 — Offline Binary Search on Endpoints

Goal:

```text
Answer point queries faster when all intervals are known before queries.
```

Core question:

```text
How many intervals started before x and how many already ended before x?
```

---

### 3. Offline Point Coverage Check

#### Problem Statement

Given all intervals first, answer whether point `x` is covered.

Example:

```text
ranges = [[2,5], [3,10], [13,16]]
x = 4  -> covered
x = 12 -> not covered
```

#### Pattern

```text
Sort left endpoints L.
Sort right endpoints R.

covered = total - count(r < x) - count(l > x)
```

#### Thinking

```text
An interval does not cover x if:
1. it ends before x: r < x
2. it starts after x: l > x
```

#### Dry Run

```text
ranges = [[2,5], [3,10], [13,16]]
L = [2,3,13]
R = [5,10,16]
```

For `x = 4`:

```text
count(l > 4) = 1   -> [13]
count(r < 4) = 0
covered = 3 - 1 - 0 = 2
answer = covered
```

For `x = 12`:

```text
count(l > 12) = 1  -> [13]
count(r < 12) = 2  -> [5,10]
covered = 3 - 1 - 2 = 0
answer = not covered
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct OfflineCoverage {
    vector<int> L, R;
    int n;

    OfflineCoverage(vector<pair<int,int>>& ranges) {
        n = ranges.size();
        for (auto [l, r] : ranges) {
            L.push_back(l);
            R.push_back(r);
        }
        sort(L.begin(), L.end());
        sort(R.begin(), R.end());
    }

    bool isCovered(int x) {
        int startAfter = L.end() - upper_bound(L.begin(), L.end(), x); // l > x
        int endBefore = lower_bound(R.begin(), R.end(), x) - R.begin(); // r < x
        int covered = n - startAfter - endBefore;
        return covered > 0;
    }
};

int main() {
    vector<pair<int,int>> ranges = {{2,5}, {3,10}, {13,16}};
    OfflineCoverage oc(ranges);

    cout << oc.isCovered(4) << "\n";   // 1
    cout << oc.isCovered(12) << "\n";  // 0
}
```

---

### 4. Count Number of Intervals Covering a Point

#### Problem Statement

Given intervals and point queries, return how many intervals cover each point.

Example:

```text
ranges = [[2,5], [3,10], [13,16]]
x = 4
answer = 2
```

#### Pattern

```text
covering(x) = count(l <= x) - count(r < x)
```

#### Thinking

```text
Intervals covering x are:
started by x - already ended before x
```

#### Dry Run

```text
L = [2,3,13]
R = [5,10,16]
```

For `x = 4`:

```text
started = count(l <= 4) = 2
endedBefore = count(r < 4) = 0
answer = 2
```

For `x = 12`:

```text
started = count(l <= 12) = 2
endedBefore = count(r < 12) = 2
answer = 0
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct IntervalCounter {
    vector<int> L, R;

    IntervalCounter(vector<pair<int,int>>& ranges) {
        for (auto [l, r] : ranges) {
            L.push_back(l);
            R.push_back(r);
        }
        sort(L.begin(), L.end());
        sort(R.begin(), R.end());
    }

    int countCovering(int x) {
        int started = upper_bound(L.begin(), L.end(), x) - L.begin();      // l <= x
        int endedBefore = lower_bound(R.begin(), R.end(), x) - R.begin(); // r < x
        return started - endedBefore;
    }
};

int main() {
    vector<pair<int,int>> ranges = {{2,5}, {3,10}, {13,16}};
    IntervalCounter counter(ranges);

    cout << counter.countCovering(4) << "\n";   // 2
    cout << counter.countCovering(12) << "\n";  // 0
}
```

---

### 5. Minimum Interval to Include Each Query

#### Problem Statement

For each query point, find the length of the smallest interval that contains it.

Example:

```text
intervals = [[1,4], [2,4], [3,6], [4,4]]
queries   = [2,3,4,5]
answer    = [3,3,1,4]
```

#### Pattern

```text
Sort intervals by start.
Sort queries.
Use min-heap by interval length.
Remove intervals whose end < query.
```

#### Thinking

```text
For each query q:
1. Add all intervals with l <= q.
2. Remove intervals with r < q.
3. Heap top is the smallest valid interval.
```

#### Dry Run

```text
intervals sorted by l:
[1,4], [2,4], [3,6], [4,4]

query q = 4
add all intervals with l <= 4:
[1,4] length 4
[2,4] length 3
[3,6] length 4
[4,4] length 1

remove intervals with r < 4:
none

smallest length = 1 from [4,4]
answer for 4 = 1
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> minInterval(vector<vector<int>>& intervals, vector<int>& queries) {
    sort(intervals.begin(), intervals.end());

    vector<pair<int,int>> qs;
    for (int i = 0; i < (int)queries.size(); i++) {
        qs.push_back({queries[i], i});
    }
    sort(qs.begin(), qs.end());

    priority_queue<pair<int,int>, vector<pair<int,int>>, greater<pair<int,int>>> pq;
    vector<int> ans(queries.size(), -1);
    int i = 0;

    for (auto [q, id] : qs) {
        while (i < (int)intervals.size() && intervals[i][0] <= q) {
            int l = intervals[i][0];
            int r = intervals[i][1];
            pq.push({r - l + 1, r});
            i++;
        }

        while (!pq.empty() && pq.top().second < q) {
            pq.pop();
        }

        if (!pq.empty()) ans[id] = pq.top().first;
    }

    return ans;
}
```

---

## Phase 3 — Online Range Mapping with set<pair<int,int>>

Goal:

```text
Handle insert/query operations while processing input online.
```

Core question:

```text
What is the interval with the largest start <= x?
```

---

### 6. Online Insert Interval and Point Query

#### Problem Statement

Support two operations:

```text
insert [l, r]
query x -> is x covered?
```

Example:

```text
insert [2,10]
insert [13,16]
query 4  -> true
query 12 -> false
```

#### Pattern

```text
Use set<pair<int,int>>.
Find candidate interval using upper_bound({x, INT_MAX}).
Move one step back.
```

#### Thinking

```text
upper_bound({x, INT_MAX}) gives first interval with start > x.
Previous interval has largest start <= x.
Only that interval can cover x.
```

#### Dry Run

```text
ranges = {[2,10], [13,16]}
x = 4

upper_bound({4, INF}) -> [13,16]
previous interval -> [2,10]
4 <= 10 -> covered
```

For `x = 12`:

```text
upper_bound({12, INF}) -> [13,16]
previous interval -> [2,10]
12 <= 10 false
answer = not covered
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct RangeSet {
    set<pair<int,int>> ranges;

    void insertSimple(int l, int r) {
        ranges.insert({l, r});
    }

    bool isPointCovered(int x) {
        auto it = ranges.upper_bound({x, INT_MAX});
        if (it == ranges.begin()) return false;

        --it;
        return it->first <= x && x <= it->second;
    }
};

int main() {
    RangeSet rs;
    rs.insertSimple(2, 10);
    rs.insertSimple(13, 16);

    cout << rs.isPointCovered(4) << "\n";   // 1
    cout << rs.isPointCovered(12) << "\n";  // 0
}
```

---

### 7. Check Whether a New Interval Overlaps Existing Intervals

#### Problem Statement

Given already booked intervals, check whether a new interval overlaps any existing interval.

Example:

```text
existing = [[2,5], [8,10]]
new = [6,7]
answer = false

new = [5,8]
answer = true for closed intervals
```

#### Pattern

```text
Only two candidates matter:
1. first interval with start >= l
2. previous interval before it
```

#### Thinking

```text
Closed intervals overlap if:
l1 <= r2 && l2 <= r1
```

#### Dry Run

```text
existing = {[2,5], [8,10]}
new = [6,7]

lower_bound({6, -INF}) -> [8,10]
Check [8,10]: 8 <= 7 false
Check previous [2,5]: 5 >= 6 false
answer = no overlap
```

For `new = [5,8]`:

```text
lower_bound({5, -INF}) -> [8,10]
Check [8,10]: 8 <= 8 true -> overlap
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct CalendarClosedIntervals {
    set<pair<int,int>> ranges;

    bool hasOverlap(int l, int r) {
        auto it = ranges.lower_bound({l, INT_MIN});

        if (it != ranges.end() && it->first <= r) return true;

        if (it != ranges.begin()) {
            auto prevIt = prev(it);
            if (prevIt->second >= l) return true;
        }

        return false;
    }

    bool book(int l, int r) {
        if (hasOverlap(l, r)) return false;
        ranges.insert({l, r});
        return true;
    }
};
```

---

## Phase 4 — Merged Interval Maintenance

Goal:

```text
Maintain disjoint merged intervals after insert/delete operations.
```

Core question:

```text
Which intervals overlap, touch, split, or fully cover the query?
```

---

### 8. Insert and Merge Overlapping Intervals

#### Problem Statement

Insert interval `[l, r]` into a set of disjoint intervals and merge overlaps.

Example:

```text
current = [[2,5], [8,10], [13,16]]
insert  = [4,9]
answer  = [[2,10], [13,16]]
```

#### Pattern

```text
Find first possible overlap.
Merge while interval.start <= r + 1.
Erase old intervals.
Insert merged interval.
```

#### Thinking

```text
If an old interval overlaps new interval, both become one bigger interval.
```

#### Dry Run

```text
insert [4,9]

[2,5] overlaps -> merge [2,9]
[8,10] overlaps -> merge [2,10]
[13,16] no overlap

result = [[2,10], [13,16]]
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct RangeSet {
    set<pair<int,int>> ranges;

    void insertRange(int l, int r) {
        if (l > r) swap(l, r);

        auto it = ranges.lower_bound({l, INT_MIN});

        if (it != ranges.begin()) {
            auto prevIt = prev(it);
            if (prevIt->second >= l - 1) {
                it = prevIt;
            }
        }

        while (it != ranges.end() && it->first <= r + 1) {
            if (it->second < l - 1) {
                ++it;
                continue;
            }

            l = min(l, it->first);
            r = max(r, it->second);
            it = ranges.erase(it);
        }

        ranges.insert({l, r});
    }
};
```

---

### 9. Remove a Range from Covered Intervals

#### Problem Statement

Remove coverage `[l, r]` from existing merged intervals.

Example:

```text
current = [[2,10], [13,16]]
remove  = [4,8]
answer  = [[2,3], [9,10], [13,16]]
```

#### Pattern

```text
For every overlapping interval [a,b]:
left leftover  -> [a, l-1]
right leftover -> [r+1, b]
```

#### Thinking

```text
Delete may shrink, split, or remove intervals completely.
```

#### Dry Run

```text
remove [4,8]

existing [2,10]:
left part before remove = [2,3]
right part after remove = [9,10]

existing [13,16]: no overlap

result = [[2,3], [9,10], [13,16]]
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct RangeSet {
    set<pair<int,int>> ranges;

    void removeRange(int l, int r) {
        vector<pair<int,int>> addBack;

        auto it = ranges.lower_bound({l, INT_MIN});
        if (it != ranges.begin()) --it;

        while (it != ranges.end()) {
            auto [a, b] = *it;

            if (b < l) {
                ++it;
                continue;
            }
            if (a > r) break;

            it = ranges.erase(it);

            if (a < l) addBack.push_back({a, l - 1});
            if (r < b) addBack.push_back({r + 1, b});
        }

        for (auto p : addBack) {
            ranges.insert(p);
        }
    }
};
```

---

### 10. Check Whether Full Range is Covered

#### Problem Statement

Given merged intervals, check if query range `[l, r]` is fully covered.

Example:

```text
ranges = [[2,10], [13,16]]
query [4,8]  -> true
query [4,12] -> false
```

#### Pattern

```text
Find interval with largest start <= l.
Check if its end >= r.
```

#### Thinking

```text
Because intervals are merged and disjoint, one interval must fully contain [l, r].
```

#### Dry Run

```text
query [4,8]

upper_bound({4, INF}) -> [13,16]
previous -> [2,10]
10 >= 8 -> fully covered
```

For `[4,12]`:

```text
previous -> [2,10]
10 >= 12 false
answer = not fully covered
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct RangeSet {
    set<pair<int,int>> ranges;

    bool isRangeCovered(int l, int r) {
        auto it = ranges.upper_bound({l, INT_MAX});
        if (it == ranges.begin()) return false;

        --it;
        return it->first <= l && r <= it->second;
    }
};
```

---

## Phase 5 — Sweep Line / Difference Array / Compression

Goal:

```text
Solve range problems by converting intervals into events or compressed coordinates.
```

Core question:

```text
Can interval operations become prefix sums over ordered points?
```

---

### 11. Maximum Number of Overlapping Intervals

#### Problem Statement

Given intervals, find the maximum number of intervals active at the same point.

Example:

```text
intervals = [[2,5], [3,10], [13,16]]
answer = 2
```

#### Pattern

```text
Sweep line events:
start -> +1
end + 1 -> -1 for closed integer intervals
```

#### Thinking

```text
Active count changes only at interval boundaries.
```

#### Dry Run

```text
[2,5]   -> (2,+1), (6,-1)
[3,10]  -> (3,+1), (11,-1)
[13,16] -> (13,+1), (17,-1)

sorted events:
2 +1  active=1
3 +1  active=2  max=2
6 -1  active=1
11 -1 active=0
13 +1 active=1
17 -1 active=0

answer = 2
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxOverlapClosedIntervals(vector<pair<int,int>>& intervals) {
    vector<pair<int,int>> events;

    for (auto [l, r] : intervals) {
        events.push_back({l, +1});
        events.push_back({r + 1, -1});
    }

    sort(events.begin(), events.end());

    int active = 0;
    int ans = 0;

    for (auto [x, delta] : events) {
        active += delta;
        ans = max(ans, active);
    }

    return ans;
}
```

---

### 12. Range Addition Using Difference Array

#### Problem Statement

Apply many updates:

```text
add value v to all points in [l, r]
```

Example:

```text
n = 8
updates:
[2,5] +1
[3,7] +1

final = [0,1,2,2,2,1,1,0]
```

#### Pattern

```text
diff[l] += v
diff[r + 1] -= v
prefix sum gives final array
```

#### Thinking

```text
Range update becomes two point updates.
```

#### Dry Run

```text
diff[2] += 1
diff[6] -= 1

diff[3] += 1
diff[8] -= 1

prefix:
idx:   1 2 3 4 5 6 7 8
value: 0 1 2 2 2 1 1 0
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n = 8;
    vector<int> diff(n + 2, 0);

    auto addRange = [&](int l, int r, int val) {
        diff[l] += val;
        diff[r + 1] -= val;
    };

    addRange(2, 5, 1);
    addRange(3, 7, 1);

    vector<int> a(n + 1, 0);
    for (int i = 1; i <= n; i++) {
        a[i] = a[i - 1] + diff[i];
    }

    for (int i = 1; i <= n; i++) {
        cout << a[i] << " ";
    }
    cout << "\n";
}
```

---

### 13. Coordinate Compression for Huge Ranges

#### Problem Statement

Coordinates are huge, but only few points matter. Compress them into small indices.

Example:

```text
coordinates = [1000000000, 5, 10, 5]
compressed:
5 -> 0
10 -> 1
1000000000 -> 2
```

#### Pattern

```text
collect values
sort
unique
lower_bound to get compressed index
```

#### Thinking

```text
When actual value is huge but relative order matters, replace value by rank.
```

#### Dry Run

```text
original values:
[1000000000, 5, 10, 5]

sort:
[5, 5, 10, 1000000000]

unique:
[5, 10, 1000000000]

mapping:
5 -> 0
10 -> 1
1000000000 -> 2
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Compressor {
    vector<int> vals;

    void add(int x) {
        vals.push_back(x);
    }

    void build() {
        sort(vals.begin(), vals.end());
        vals.erase(unique(vals.begin(), vals.end()), vals.end());
    }

    int get(int x) {
        return lower_bound(vals.begin(), vals.end(), x) - vals.begin();
    }
};

int main() {
    Compressor comp;
    comp.add(1000000000);
    comp.add(5);
    comp.add(10);
    comp.add(5);
    comp.build();

    cout << comp.get(5) << "\n";          // 0
    cout << comp.get(10) << "\n";         // 1
    cout << comp.get(1000000000) << "\n"; // 2
}
```

---

### 14. Offline Event Sweep for Query Points

#### Problem Statement

Given intervals and query points, count how many intervals cover each query point.

Example:

```text
intervals = [[2,5], [3,10]]
queries = [2,5,6]
answer = [1,2,1]
```

#### Pattern

```text
Create events:
start = 0
query = 1
end   = 2

For closed intervals, order at same coordinate:
start before query before end
```

#### Thinking

```text
Sweep from left to right.
active = number of intervals currently covering this coordinate.
```

#### Dry Run

```text
events:
2 start
2 query id0
3 start
5 query id1
5 end
6 query id2
10 end

scan:
x=2 start active=1
x=2 query ans[0]=1
x=3 start active=2
x=5 query ans[1]=2
x=5 end active=1
x=6 query ans[2]=1
```

#### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Event {
    int x;
    int type; // 0=start, 1=query, 2=end
    int id;

    bool operator<(const Event& other) const {
        if (x != other.x) return x < other.x;
        return type < other.type;
    }
};

vector<int> countCoveringBySweep(vector<pair<int,int>>& intervals, vector<int>& queries) {
    vector<Event> events;

    for (auto [l, r] : intervals) {
        events.push_back({l, 0, -1});
        events.push_back({r, 2, -1});
    }

    for (int i = 0; i < (int)queries.size(); i++) {
        events.push_back({queries[i], 1, i});
    }

    sort(events.begin(), events.end());

    vector<int> ans(queries.size());
    int active = 0;

    for (auto e : events) {
        if (e.type == 0) active++;
        else if (e.type == 1) ans[e.id] = active;
        else active--;
    }

    return ans;
}
```

---

## Final Phase Summary

| Phase | Goal | Problems |
|---|---|---|
| Phase 1 | Understand interval coverage | Brute point check, count covered points |
| Phase 2 | Offline endpoint thinking | Point coverage, count covering, minimum interval query |
| Phase 3 | Online set thinking | Insert/query point, overlap check |
| Phase 4 | Merged interval maintenance | Insert merge, delete range, full coverage check |
| Phase 5 | Event/prefix thinking | Max overlap, difference array, compression, event sweep |

---

## Master Rule

```text
If the problem is about l <= x <= r,
range mapping / interval coverage is probably the pattern.
```

---

## Final Checklist Before Coding

```text
1. Are intervals closed [l, r] or half-open [l, r)?
2. Are queries offline or online?
3. Do I need boolean coverage or count coverage?
4. Are intervals overlapping or should they be merged?
5. Do I need insert/delete operations?
6. Are coordinates small, huge, or compressible?
7. Should I use brute force, binary search, set, sweep line, or difference array?
8. For set<pair<int,int>>, do I need upper_bound({x, INT_MAX}) or lower_bound({l, INT_MIN})?
```


---

# Final Ultimate Revision Sheet

## 30-Second STL Picker

```text
vector        -> store, sort, prefix, binary search
string        -> parsing, stack simulation, hashing
stack         -> brackets, NGE/NSE, histogram, parsing
queue         -> BFS, first-in processing
priority_queue-> current best, top k, scheduling, Dijkstra
set           -> sorted unique, predecessor/successor, intervals
multiset      -> sorted duplicates, arbitrary erase, median, top-k sum
map           -> ordered key frequency, prefix count, sorted output
unordered_map -> average O(1) frequency/lookup, beware hacks
 deque        -> monotonic window min/max, both-end operations
```

## Final CM/FAANG Golden Rules

```text
1. Constraints decide data structure.
2. Operation list decides STL.
3. Invariant decides correctness.
4. Dry run catches off-by-one.
5. Edge cases decide AC vs WA.
```

## Last-Minute Equality Rules

```text
Next greater strictly greater:
    pop while <= current when scanning right-to-left
    or resolve while current > stack top when scanning left-to-right

Next greater or equal:
    adjust equality carefully

Subarray minimum contribution:
    use one strict and one non-strict boundary to avoid duplicate counting

Intervals inclusive [l,r]:
    r < x ended before x
    l > x starts after x
```

## Final Practice Order

```text
1. Prefix sum + map
2. Sliding window + deque
3. Monotonic stack NGE/NSE
4. Histogram + subarray minimum contribution
5. Heap top-k + streaming median
6. Heap scheduling + Dijkstra
7. Range mapping offline endpoints
8. Dynamic merged intervals with set
```

---

**End of Ultimate STL Pattern Recognition Guide.**
