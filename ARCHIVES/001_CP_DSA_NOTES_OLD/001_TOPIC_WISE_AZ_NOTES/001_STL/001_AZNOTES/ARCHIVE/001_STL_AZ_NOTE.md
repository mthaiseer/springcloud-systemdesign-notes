# Competitive Programming STL & Problem Solving Notes — ASCII 10/10 CM + FAANG Edition

## Clickable Index

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

## 0A. CM + FAANG Upgrade Layer

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

### CM recognition rules

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

### FAANG implementation expectations

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

### 5 bugs that separate 1400 from CM

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

## 0. Problem Solving Strategy

### Time split in contest

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

### Intuition

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

### Ideation checklist

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

### Example

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

### One-minute mental trick

```text
n <= 100        brute force may pass
n <= 2e5        need O(n log n) or O(n)
n <= 1e6        usually O(n)
q large         precompute or use data structure
range query     prefix / Fenwick / segment tree
dynamic update  Fenwick / segment tree / balanced set
```

---

## 1. Balanced Brackets / Parentheses

### Core idea

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

### Intuition

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

### C++: single bracket type

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

### Dry Run And Mermaid Flow

#### Dry Run: Single bracket counter

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

#### ASCII Dry Run Diagram

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


### Multiple bracket types

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

### Example

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

### C++: stack + map

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

### Dry Run And Mermaid Flow

#### Dry Run: Multiple bracket stack

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

#### ASCII Dry Run Diagram

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


### Range query on balanced parentheses

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

### CM + FAANG edge cases

```text
Empty string: valid if no unmatched brackets.
Mixed characters: either ignore non-brackets or reject based on statement.
Multiple types: counter is not enough; use stack.
Range bracket queries: need prefix depth + range minimum, not stack per query.
```

### One-minute mental trick

```text
One bracket type:
    use counter

Multiple bracket types:
    use stack

Range balanced query:
    prefix depth + range minimum
```

---

## 2. Sliding Window Subarray Maintenance

Sliding window is used when we need answers for every window/subarray of length `k`, such as:

- minimum / maximum in every window
- number of distinct elements
- median / mean of each window
- cost of each window

### General template

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

### Dry Run And Mermaid Flow

#### Dry Run: Fixed size window movement

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

#### ASCII Dry Run Diagram

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


### Intuition

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

### Example

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

### CM + FAANG edge cases

```text
k = 1: every element is its own window.
k = n: only one window.
Negative values: sum window works; variable sliding window may fail.
Duplicates: map/multiset erase must remove one occurrence.
```

### One-minute mental trick

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

## 3. Sliding Window Minimum

### Using multiset

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

### Dry Run And Mermaid Flow

#### Dry Run: Multiset window minimum

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

#### ASCII Dry Run Diagram

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

### Using monotonic deque

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

### Intuition

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

### C++ monotonic deque

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

### Dry Run And Mermaid Flow

#### Dry Run: Monotonic deque minimum

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

#### ASCII Dry Run Diagram

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

### CM + FAANG edge cases

```text
For deque, store indices when expiry is by position.
Store values only when outgoing value is known and duplicates are handled carefully.
For max deque, reverse comparison.
For equal values, choose >= or > based on whether you want stable indices.
```

### One-minute mental trick

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

## 4. Sliding Window Cost / Make All Elements Equal

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

### Intuition

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

### Data structure

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

### Example

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

### C++

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

### Dry Run And Mermaid Flow

#### Dry Run: Median cost using two multisets

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

#### ASCII Dry Run Diagram

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


### One-minute mental trick

```text
Minimize sum of absolute differences:
    choose median

Minimize sum of squared differences:
    choose mean

Dynamic median:
    two multisets / two heaps
```

---

## 5. Mean, Variance, Median, Mode Dashboard

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

### Intuition

Different statistics need different maintained information.

| Query | What to maintain |
|---|---|
| mean | `sum`, `count` |
| variance | `sum`, `squareSum`, `count` |
| median | two balanced multisets |
| mode | frequency map + ordered frequency set |

### Formulas

```text
mean = sum / count
variance = (sum of squares / count) - mean^2
mode = element with highest frequency
median = middle value after sorting
```

### Example

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

### C++ structure

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

### Dry Run And Mermaid Flow

#### Dry Run: Statistics dashboard updates

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

#### ASCII Dry Run Diagram

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


### One-minute mental trick

```text
mean      -> sum
variance  -> sum + squareSum
median    -> two halves
mode      -> frequency + max frequency
```

---

## 6. Prefix Sum and Subarray Sum Equals X

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

### Intuition

If:

```text
current prefix = sum from 0 to r
```

And we want a subarray ending at `r` with sum `x`, then the part before that subarray must be:

```text
current prefix - x
```

So we count how many times that previous prefix appeared.

### Example

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

### C++ count only

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

### Dry Run And Mermaid Flow

#### Dry Run: Count subarrays with target sum

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

#### ASCII Dry Run Diagram

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


### C++ print all ranges

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

### Dry Run And Mermaid Flow

#### Dry Run: Print all target-sum ranges

Input:

```text
a = [1, 2, 3], x = 3
```

| i | value | prefix | need | positions for need | printed range |
|---:|---:|---:|---:|---|---|
| 0 | 1 | 1 | -2 | none | none |
| 1 | 2 | 3 | 0 | `-1` | `[0,1]` |
| 2 | 3 | 6 | 3 | `1` | `[2,2]` |

#### ASCII Dry Run Diagram

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


### CM + FAANG edge cases

```text
Initialize freq[0] = 1, otherwise subarrays starting at index 0 are missed.
Use long long for prefix.
If all numbers are positive, two pointers may work; with negatives, use prefix+map.
For modulo subarray problems, store prefix % mod carefully: (x % m + m) % m.
```

### One-minute mental trick

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

## 7. Stack Mastery Next Greater Element

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

### Intuition

When scanning from right to left, the stack stores useful candidates for the next greater element.

If a candidate is less than or equal to the current value, it can never be the next greater for this or any earlier element blocked by current.

### Example

```text
a = [2, 1, 3, 2]
```

| i | value | next greater |
|---:|---:|---:|
| 0 | 2 | 3 |
| 1 | 1 | 3 |
| 2 | 3 | none |
| 3 | 2 | none |

### C++

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

### Dry Run And Mermaid Flow

#### Dry Run: Next greater element

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

#### ASCII Dry Run Diagram

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


### CM + FAANG edge cases

```text
Greater vs greater-or-equal changes pop condition.
Circular next greater: scan 2n times and use i % n.
Distance answer: store indices, not values.
Value answer: convert index to value at the end.
```

### One-minute mental trick

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

## 8. Trapping Rain Water

For each bar, trapped water depends on boundary bars.

### Stack approach

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

### Intuition

Water is trapped when we find:

```text
left wall + bottom + right wall
```

The current bar acts as the right wall. The stack top after popping acts as the left wall.

### Example

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

### C++

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

### Dry Run And Mermaid Flow

#### Dry Run: Water trapped by stack

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

#### ASCII Dry Run Diagram

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


### One-minute mental trick

```text
Water needs two walls.

When current height is bigger than stack top:
    current is right wall
    popped is bottom
    new stack top is left wall
```

---

## 9. Range Mapping / Interval Coverage

Queries:

1. Insert range `[l, r]`
2. Check whether point `x` is covered by any range

### Offline version

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

### Intuition

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

### C++

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

### Dry Run And Mermaid Flow

#### Dry Run: Offline interval coverage

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

#### ASCII Dry Run Diagram

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


### Online version: maintain merged intervals

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

### Example

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

### C++

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

### Dry Run And Mermaid Flow

#### Dry Run: Merged interval insert and query

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

#### ASCII Dry Run Diagram

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


### One-minute mental trick

```text
Static intervals + many point queries:
    sort starts and ends

Dynamic insert + point query:
    set of merged intervals

Need range add/query:
    difference array / segment tree
```

---

## 10. Top K Sum

Maintain sum of top `k` elements dynamically.

Two common implementations:

1. Priority queue: good when only removing min/max from top side.
2. Two multisets: better when arbitrary `remove(x)` is needed.

### Two multiset method

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

### Intuition

Always keep the largest `k` values in one bucket.

If a new big value enters, a smaller value may be pushed out.

### Example

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

### C++

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

### Dry Run And Mermaid Flow

#### Dry Run: Maintain top k sum

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

#### ASCII Dry Run Diagram

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


### One-minute mental trick

```text
Need top k dynamically?
    top bucket + rest bucket

Need arbitrary erase?
    multiset

Only insert/extract max?
    priority_queue
```

---

## 11. Priority Queue Notes

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

### Intuition

A priority queue is useful when you only care about the current best item:

```text
largest
smallest
highest priority
lowest cost
```

It is not good when you need to erase arbitrary values unless you use lazy deletion.

### Example use cases

| Problem | Heap type |
|---|---|
| kth largest | min heap of size k |
| Dijkstra shortest path | min heap |
| task scheduler | max heap |
| merge k sorted lists | min heap |

### One-minute mental trick

```text
Need repeatedly best element?
    priority_queue

Need sorted traversal or arbitrary erase?
    set / multiset

Need both min and max?
    multiset
```

---

## 12. Stack and Queue Basics

### Stack

LIFO: last in, first out.

```cpp
stack<int> st;
st.push(x);
st.pop();
int top = st.top();
bool empty = st.empty();
```

### Queue

FIFO: first in, first out.

```cpp
queue<int> q;
q.push(x);
q.pop();
int front = q.front();
bool empty = q.empty();
```

### Visual

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

### Implement stack using two queues

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

### Dry Run And Mermaid Flow

#### Dry Run: Stack using two queues

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

#### ASCII Dry Run Diagram

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


### One-minute mental trick

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

## 13. Contribution Technique

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

### Intuition

When too many objects exist, count contribution from each element instead.

Instead of:

```text
For every subarray, add all elements
```

Think:

```text
For every element, count how many subarrays contain it
```

### Sum of all subarrays

Element `a[i]` appears in:

```text
(i + 1) * (n - i)
```

subarrays.

So:

```text
answer = Σ a[i] * (i + 1) * (n - i)
```

### Example

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

### C++

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

### Dry Run And Mermaid Flow

#### Dry Run: Sum of all subarrays by contribution

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

#### ASCII Dry Run Diagram

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


### Sum of all subsequences

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

### Dry Run And Mermaid Flow

#### Dry Run: Sum of all subsequences

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

#### ASCII Dry Run Diagram

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


### Product of all subarrays sum

For each ending position, maintain sum of products of all subarrays ending there.

```text
sop = sop * a[i] + a[i]
ans += sop
```

### Example

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

### C++

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

### Dry Run And Mermaid Flow

#### Dry Run: Sum product of all subarrays

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

#### ASCII Dry Run Diagram

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


### One-minute mental trick

```text
Too many subarrays/subsequences?
    don't generate them

Ask:
    how many times does this element/pair contribute?
```

---

## 14. Pattern Matching / Coordinate Geometry Printing

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

### Intuition

Pattern problems are coordinate geometry problems.

Instead of guessing spaces, ask:

```text
At coordinate (i, j), should I print star?
```

### Template

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

### Dry Run And Mermaid Flow

#### Dry Run: Coordinate based star printing

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

#### ASCII Dry Run Diagram

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

### Common conditions

| Pattern | Condition |
|---|---|
| main diagonal | `i == j` |
| anti diagonal | `i + j == n - 1` |
| border | `i == 0 || j == 0 || i == n-1 || j == m-1` |
| upper triangle | `i <= j` |
| lower triangle | `i >= j` |

### Repeating columns

To repeat after every `k` columns, use modulo:

```cpp
func(i, j % k, rows, k);
```

### One-minute mental trick

```text
Pattern printing:
    stop thinking in spaces
    think in coordinates

Ask:
    when should this cell be star?
```

---

## 15. Molecular Formula Parser

Problem type: parse chemical formula like:

```text
K4(ON(SO3)2)2
```

Expected output is sorted by element names with counts.

### Key ideas

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

### Intuition

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

### C++ parser

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

### Dry Run And Mermaid Flow

#### Dry Run: Parse chemical formula

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

#### ASCII Dry Run Diagram

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


### One-minute mental trick

```text
Nested parentheses/brackets:
    recursion or stack

Need sorted output:
    map

Default count:
    if no number, count = 1
```

---



## 15A. Largest Rectangle in Histogram

Find largest rectangle area in histogram.

### Core Idea

For every bar:

```text
height = current bar
width = nearest smaller on left/right
```

Use monotonic increasing stack.

---

### Invariant

```text
Stack always stores increasing heights.
```

---

### ASCII Flow

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

### Formula

For popped index:

```text
height = h[idx]
right = current index
left = new stack top
width = right - left - 1
area = height * width
```

---

### Example

```text
heights = [2,1,5,6,2,3]
```

Maximum rectangle:

```text
5 x 2 = 10
```

---

### C++ Code

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

### Dry Run

| i | Current | Stack | Action | Max Area |
|---|---|---|---|---|
| 0 | 2 | 0 | push | 2 |
| 1 | 1 | empty | pop 2 | 2 |
| 2 | 5 | 1,2 | push | 5 |
| 3 | 6 | 1,2,3 | push | 6 |
| 4 | 2 | 1 | pop 6, pop 5 | 10 |
| 5 | 3 | 1,4,5 | push | 10 |

---

### Mental Trick

```text
Nearest smaller left/right
+
Monotonic increasing stack
=
Largest histogram rectangle
```


## 16. Choosing the Right STL

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

### STL decision diagram — ASCII

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

### One-minute mental trick

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

## 17. Common Mistakes

- Using `mset.erase(x)` when you only want to erase one copy. Use `mset.erase(mset.find(x))`.
- Calling `top()`, `front()`, `back()`, `pop()` on an empty STL container.
- Forgetting `i + k` is usually exclusive in window ranges.
- For sliding window, forgetting to remove `arr[i-k]`.
- For prefix sums, forgetting to initialize `freq[0] = 1`.
- For maps/multisets, forgetting logarithmic complexity.
- In custom comparators, never return true for equal items; it may cause runtime issues.

### Mistake examples

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

### One-minute mental trick

```text
Before using STL top/front/back:
    check empty

Before erase in multiset:
    erase iterator, not value

Before prefix-map subarray count:
    freq[0] = 1
```

---

## 18. Final Revision Flow

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

### Quick pattern recognition table

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

## 19. Minimal C++ Setup

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

## 20. One-Minute CP Mental Checklist

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

### Final memory hook

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

## 21. Final Golden Rules

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

## 22. CM + FAANG Final Revision Grid

### Pattern ladder

```text
800-1000  : vector, sort, map frequency, prefix sum basics
1100-1300 : stack/queue, two pointers, simple sliding window
1400-1600 : monotonic stack/deque, prefix+map, intervals
1700-1900 : two multisets, top-k dynamic, contribution, parser
1900+     : combine 2-3 patterns + edge-case heavy implementation
FAANG     : explain invariant clearly and write bug-free code fast
```

### Fast decision tree

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

### Final contest checklist

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

### One picture

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
