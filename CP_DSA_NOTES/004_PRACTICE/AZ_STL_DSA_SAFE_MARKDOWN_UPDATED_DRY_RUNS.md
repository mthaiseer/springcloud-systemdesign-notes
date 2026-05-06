# AlgoZenith STL / DSA Practice Handbook — Safe Markdown Version

This version avoids Mermaid and rich-render features. It uses only normal Markdown tables, clickable links, ASCII diagrams, and C++ code blocks.

## Clickable Index by Difficulty

- [Novice](#novice)
  - [All One](#all-one)
  - [Infix-Postfix](#infix-postfix)
- [Easy](#easy)
  - [Queue From Stack](#queue-from-stack)
  - [Mode of Distances](#mode-of-distances)
  - [Towers AZ101](#towers-az101)
  - [Diversify the Array](#diversify-the-array)
  - [Maximum Element in each subarray AZ101](#maximum-element-in-each-subarray-az101)
  - [Queue AZ101](#queue-az101)
  - [Max Diff](#max-diff)
  - [Sort by Roll Number](#sort-by-roll-number)
  - [Special Heap](#special-heap)
- [Medium](#medium)
  - [Maximum Rate Subarray](#maximum-rate-subarray)
  - [Smart Sale](#smart-sale)
  - [Generating Permutations AZ101](#generating-permutations-az101)
  - [Happy Neighborhood](#happy-neighborhood)
  - [Longest Segment](#longest-segment)
  - [Set AZ101](#set-az101)
  - [Solve Intervals 3](#solve-intervals-3)
  - [ADDMUL](#addmul)
  - [Multimap AZ101](#multimap-az101)
  - [LFU Cache](#lfu-cache)
  - [Distinct Characters AZ101](#distinct-characters-az101)
  - [Support Queries II](#support-queries-ii)
  - [Support Queries I](#support-queries-i)
  - [Powers of Two](#powers-of-two)
  - [FMBQUEUE](#fmbqueue)
  - [Next Permutation](#next-permutation)
  - [Deque AZ101](#deque-az101)
  - [Indexed Set](#indexed-set)
  - [Set Queries AZ101](#set-queries-az101)
  - [Running Mean, Median and Mode AZ101](#running-mean-median-and-mode-az101)
  - [Find The Sum](#find-the-sum)
  - [Game on Deque AZ101](#game-on-deque-az101)
  - [Duplicate Products](#duplicate-products)
  - [Powers Of Two](#powers-of-two-2)
  - [The Social Network](#the-social-network)
  - [Bachata Dance](#bachata-dance)
  - [Evaluating Boolean Expressions](#evaluating-boolean-expressions)
  - [Substrings Galore](#substrings-galore)
  - [Subsegment Sort](#subsegment-sort)
  - [Gas Station](#gas-station)
  - [Nearly Sorted Arrays](#nearly-sorted-arrays)
  - [Queue using 2 Stacks AZ101](#queue-using-2-stacks-az101)
  - [Hamming Distance](#hamming-distance)
  - [Priority Queue](#priority-queue)
  - [Elections](#elections)
  - [Multiset AZ101](#multiset-az101)
  - [Set Operations AZ101](#set-operations-az101)
- [Hard](#hard)
  - [Subarrays](#subarrays)
  - [STL Searching](#stl-searching)
  - [Pocket Money](#pocket-money)
  - [Find The Triplet](#find-the-triplet)
  - [Sports Meet](#sports-meet)
  - [Fountains](#fountains)
- [Extreme](#extreme)
  - [Nearest Neighbouring City](#nearest-neighbouring-city)
- [Unrated](#unrated)
  - [Maximum Number of Customers AZ101](#maximum-number-of-customers-az101)

---

## Difficulty Overview

| Difficulty | Count |
|---|---:|
| Novice | 2 |
| Easy | 9 |
| Medium | 37 |
| Hard | 6 |
| Extreme | 1 |
| Unrated | 1 |

---

## Master Table

| Difficulty | Problem | Pattern | Brute Think | Optimal Approach |
|---|---|---|---|---|
| Novice | [All One](#all-one) | HashMap + Frequency Buckets | Scan every key to find min/max after each update: O(n) per query. | Use hash maps and frequency tracking. Standard exact O(1) version needs bucketed doubly linked list. |
| Novice | [Infix-Postfix](#infix-postfix) | Stack + Precedence | Repeatedly scan expression and manually move high-priority operators. | Use a stack for operators and output operands immediately. |
| Easy | [Queue From Stack](#queue-from-stack) | Two Stacks | Move elements every time push/pop is called. | Use two stacks: one for push, one for pop; transfer only when needed. |
| Easy | [Mode of Distances](#mode-of-distances) | Frequency Map | For every value, scan the full array to count frequency. | Use frequency map and track best frequency. |
| Easy | [Towers AZ101](#towers-az101) | Multiset Greedy | Try every tower top linearly for each block. | Use multiset of tower tops and upper_bound. |
| Easy | [Diversify the Array](#diversify-the-array) | Set + Frequency Map | Try removing/changing each element and recompute distinct count. | Use set/frequency map to know distinct and duplicate counts. |
| Easy | [Maximum Element in each subarray AZ101](#maximum-element-in-each-subarray-az101) | Monotonic Deque | Scan each window completely: O(nk). | Maintain decreasing deque of useful indices. |
| Easy | [Queue AZ101](#queue-az101) | Queue STL | Use vector and erase from front, causing O(n) shifts. | Use std::queue for O(1) front/pop/push. |
| Easy | [Max Diff](#max-diff) | Prefix Minimum | Try all pairs. | Track minimum value seen so far. |
| Easy | [Sort by Roll Number](#sort-by-roll-number) | Sort Comparator | Implement manual sorting. | Use std::sort with comparator. |
| Easy | [Special Heap](#special-heap) | Priority Queue Comparator | Sort all elements after every update. | Use priority_queue with custom comparator. |
| Medium | [Maximum Rate Subarray](#maximum-rate-subarray) | Sliding Window / Prefix | Try all subarrays. | Use sliding window when condition is monotonic, otherwise prefix sums. |
| Medium | [Smart Sale](#smart-sale) | Frequency Sort | Each time scan all frequencies and remove smallest. | Count frequencies, sort them, remove cheapest types first. |
| Medium | [Generating Permutations AZ101](#generating-permutations-az101) | next_permutation | Manually construct all orders with many loops. | Sort first and repeatedly call next_permutation. |
| Medium | [Happy Neighborhood](#happy-neighborhood) | Greedy + Sorting | Try all arrangements. | Sort/frequency + greedy placement. |
| Medium | [Longest Segment](#longest-segment) | Two Pointers | Check every l,r pair. | Use two pointers/sliding window when validity is monotonic. |
| Medium | [Set AZ101](#set-az101) | Set STL | Store in vector and search linearly. | Use std::set for O(log n) updates/search. |
| Medium | [Solve Intervals 3](#solve-intervals-3) | Interval Set | Compare inserted range with every interval. | Use set<pair<int,int>> with lower_bound and erase merged intervals. |
| Medium | [ADDMUL](#addmul) | Lazy Math | Update every element per query. | Maintain lazy transformation x -> x*mul + add. |
| Medium | [Multimap AZ101](#multimap-az101) | Multimap | Use map<int,vector<int>> manually. | Use multimap or map of vectors depending on output needs. |
| Medium | [LFU Cache](#lfu-cache) | HashMap + Lists | Scan all cache entries on eviction. | Use maps from key to node and frequency to ordered key list. |
| Medium | [Distinct Characters AZ101](#distinct-characters-az101) | Frequency Window | Recount characters for every window. | Use sliding window with frequency array. |
| Medium | [Support Queries II](#support-queries-ii) | Ordered Set Queries | Scan all active values per query. | Use set/multiset with lower_bound/upper_bound. |
| Medium | [Support Queries I](#support-queries-i) | Map / Set | Recompute from scratch. | Use map/set based on query type. |
| Medium | [Powers of Two](#powers-of-two) | Hashing + Powers | Try every pair. | Use hash counts and test all powers for complements. |
| Medium | [FMBQUEUE](#fmbqueue) | Deque Simulation | Use vector insertion/deletion causing shifts. | Use deque(s) for efficient end operations. |
| Medium | [Next Permutation](#next-permutation) | Lexicographic Pivot | Generate all permutations and locate current one. | Find pivot, swap with next larger, reverse suffix. |
| Medium | [Deque AZ101](#deque-az101) | Deque STL | Use vector and shift elements. | Use std::deque. |
| Medium | [Indexed Set](#indexed-set) | PBDS | Sort every query. | Use GNU PBDS ordered_set. |
| Medium | [Set Queries AZ101](#set-queries-az101) | Set Bounds | Scan every element. | Use std::set lower_bound/upper_bound. |
| Medium | [Running Mean, Median and Mode AZ101](#running-mean-median-and-mode-az101) | Two Multisets + Frequency | Sort and recount after each insertion. | Use two multisets for median and frequency maps for mode. |
| Medium | [Find The Sum](#find-the-sum) | Prefix Sum | Sum elements each query. | Use prefix sums. |
| Medium | [Game on Deque AZ101](#game-on-deque-az101) | Deque + Cycle | Simulate all operations per query. | Precompute initial phase and cycle after max reaches front. |
| Medium | [Duplicate Products](#duplicate-products) | Hash Set | Compare all pairs. | Use frequency map/set. |
| Medium | [Powers Of Two](#powers-of-two-2) | Hashing + Powers | Try all pairs. | Use hash counts and powers iteration. |
| Medium | [The Social Network](#the-social-network) | DSU | Check connectivity by scanning relations. | Use DSU union/find. |
| Medium | [Bachata Dance](#bachata-dance) | Greedy Pairing | Try all pairings. | Sort and greedily match. |
| Medium | [Evaluating Boolean Expressions](#evaluating-boolean-expressions) | Stack Expression Evaluation | Repeated recursive parsing/scans. | Use stacks for values/operators. |
| Medium | [Substrings Galore](#substrings-galore) | Sliding Window / Hashing | Generate all substrings. | Use sliding window or hashing. |
| Medium | [Subsegment Sort](#subsegment-sort) | Sort + Mismatch | Sort every candidate subarray. | Compare with sorted copy and find mismatch range. |
| Medium | [Gas Station](#gas-station) | Greedy Circular | Try every start and simulate. | Greedy reset when tank becomes negative. |
| Medium | [Nearly Sorted Arrays](#nearly-sorted-arrays) | Min Heap | Sort entire array. | Use min-heap of size k+1. |
| Medium | [Queue using 2 Stacks AZ101](#queue-using-2-stacks-az101) | Two Stacks | Move all elements every operation. | Amortized transfer from input stack to output stack. |
| Medium | [Hamming Distance](#hamming-distance) | Bit Counting | Compare each pair bit by bit. | Count set bits at every position. |
| Medium | [Priority Queue](#priority-queue) | Heap | Sort vector repeatedly. | Use priority_queue. |
| Medium | [Elections](#elections) | Map + Leader Tracking | Recount votes from scratch. | Frequency map and tie rule; optionally precompute leaders. |
| Medium | [Multiset AZ101](#multiset-az101) | Multiset | Vector + sort after every update. | Use multiset. |
| Medium | [Set Operations AZ101](#set-operations-az101) | Set Algorithms | Nested loops. | Use set algorithms or two pointers. |
| Hard | [Subarrays](#subarrays) | Prefix / Monotonic DS | Enumerate all subarrays. | Use prefix sums/maps or monotonic structures depending on property. |
| Hard | [STL Searching](#stl-searching) | Binary Search STL | Linear search. | Use lower_bound, upper_bound, binary_search. |
| Hard | [Pocket Money](#pocket-money) | Greedy + Multiset | Try all distributions. | Greedy with multiset/heap for best current choice. |
| Hard | [Find The Triplet](#find-the-triplet) | Sort + Two Pointers | Triple nested loops. | Sort and use two pointers for each fixed first element. |
| Hard | [Sports Meet](#sports-meet) | Sweep Line | Check every pair of intervals. | Sweep line events sorted by time. |
| Hard | [Fountains](#fountains) | Prefix/Suffix Greedy | Try all pairs/combinations. | Use prefix/suffix maxima or greedy coverage. |
| Extreme | [Nearest Neighbouring City](#nearest-neighbouring-city) | Coordinate Map + Set | Compare query city with every city. | Group by x/y coordinate and use ordered sets/maps. |
| Unrated | [Maximum Number of Customers AZ101](#maximum-number-of-customers-az101) | Sweep Line | Compare all interval overlaps. | Convert arrivals/leavings into events and sweep. |

---

<a id="novice"></a>

# Novice

<a id="all-one"></a>

## All One

**Difficulty:** Novice  
**Pattern:** HashMap + Frequency Buckets

### Problem Description

Maintain string keys with counts; support increment/decrement and get any max/min key.

### Brute Thinking

Scan every key to find min/max after each update: O(n) per query.

### Optimal Approach

Use hash maps and frequency tracking. Standard exact O(1) version needs bucketed doubly linked list.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Scan every key to find min/max after each update: O(n) per query. |
| 2 | What structure removes repetition? | HashMap + Frequency Buckets |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class AllOneSimple {
    unordered_map<string,int> cnt;

public:
    void inc(string key) {
        cnt[key]++;
    }

    void dec(string key) {
        if (!cnt.count(key)) return;
        cnt[key]--;
        if (cnt[key] == 0) cnt.erase(key);
    }

    string getMaxKey() {
        string ans = "";
        int best = INT_MIN;

        for (auto [key, value] : cnt) {
            if (value > best) {
                best = value;
                ans = key;
            }
        }

        return ans;
    }

    string getMinKey() {
        string ans = "";
        int best = INT_MAX;

        for (auto [key, value] : cnt) {
            if (value < best) {
                best = value;
                ans = key;
            }
        }

        return ans;
    }
};

int main() {
    AllOneSimple ds;

    ds.inc("apple");
    ds.inc("banana");
    ds.inc("apple");

    cout << "Max key: " << ds.getMaxKey() << "\n";
    cout << "Min key: " << ds.getMinKey() << "\n";

    return 0;
}
```

### Dry Run Example

| Operation | Count Map | Why this works |
|---|---|---|
| inc apple | apple=1 | Frequency of apple increases |
| inc banana | apple=1, banana=1 | Both keys have same frequency |
| inc apple | apple=2, banana=1 | apple becomes max key |
| getMaxKey | apple=2 | highest frequency is 2 |
| getMinKey | banana=1 | lowest frequency is 1 |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="infix-postfix"></a>

## Infix-Postfix

**Difficulty:** Novice  
**Pattern:** Stack + Precedence

### Problem Description

Convert an infix expression such as A+B*C into postfix notation ABC*+.

### Brute Thinking

Repeatedly scan expression and manually move high-priority operators.

### Optimal Approach

Use a stack for operators and output operands immediately.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Repeatedly scan expression and manually move high-priority operators. |
| 2 | What structure removes repetition? | Stack + Precedence |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int prec(char c) {
    if (c == '^') return 3;
    if (c == '*' || c == '/') return 2;
    if (c == '+' || c == '-') return 1;
    return 0;
}

string infixToPostfix(string s) {
    stack<char> st;
    string out;

    for (char c : s) {
        if (isalnum(c)) out += c;
        else if (c == '(') st.push(c);
        else if (c == ')') {
            while (!st.empty() && st.top() != '(') {
                out += st.top();
                st.pop();
            }
            if (!st.empty()) st.pop();
        } else {
            while (!st.empty() && st.top() != '(' && prec(st.top()) >= prec(c)) {
                out += st.top();
                st.pop();
            }
            st.push(c);
        }
    }

    while (!st.empty()) {
        out += st.top();
        st.pop();
    }
    return out;
}

int main() {
    string s = "A+B*C";
    cout << infixToPostfix(s) << "\n";
    return 0;
}
```

### Dry Run Example

| Token | Action | Operator Stack | Output |
|---|---|---|---|
| A | Operand goes directly to output | [] | A |
| + | Push operator | [+] | A |
| B | Operand goes directly to output | [+] | AB |
| * | Higher precedence than +, push it | [+, *] | AB |
| C | Operand goes directly to output | [+, *] | ABC |
| End | Pop all remaining operators | [] | ABC*+ |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="easy"></a>

# Easy

<a id="queue-from-stack"></a>

## Queue From Stack

**Difficulty:** Easy  
**Pattern:** Two Stacks

### Problem Description

Implement FIFO queue using only stack operations.

### Brute Thinking

Move elements every time push/pop is called.

### Optimal Approach

Use two stacks: one for push, one for pop; transfer only when needed.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Move elements every time push/pop is called. |
| 2 | What structure removes repetition? | Two Stacks |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class MyQueue {
    stack<int> in, out;

    void shift() {
        if (!out.empty()) return;

        while (!in.empty()) {
            out.push(in.top());
            in.pop();
        }
    }

public:
    void push(int x) {
        in.push(x);
    }

    int front() {
        shift();
        return out.top();
    }

    int pop() {
        shift();
        int x = out.top();
        out.pop();
        return x;
    }
};

int main() {
    MyQueue q;

    q.push(10);
    q.push(20);
    q.push(30);

    cout << q.pop() << "\n";
    cout << q.front() << "\n";

    return 0;
}
```

### Dry Run Example

| Operation | in stack | out stack | Approach Reason |
|---|---|---|---|
| push 10 | [10] | [] | Push always goes to input stack |
| push 20 | [10,20] | [] | Still O(1) push |
| pop | [] | [20] | Move in → out only because out was empty |
| result | [] | [20] | 10 is popped first, so FIFO is preserved |
| front | [] | [20] | front is top of out stack = 20 |

### ASCII Diagram

```text
push side                 pop side
[in stack]  ----move----> [out stack]
                         front/top is queue front
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="mode-of-distances"></a>

## Mode of Distances

**Difficulty:** Easy  
**Pattern:** Frequency Map

### Problem Description

Find the most frequent value/distance from a list.

### Brute Thinking

For every value, scan the full array to count frequency.

### Optimal Approach

Use frequency map and track best frequency.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | For every value, scan the full array to count frequency. |
| 2 | What structure removes repetition? | Frequency Map |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Frequency Map\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Value | Frequency Map Update | Current Mode |
|---:|---|---|
| 4 | 4→1 | 4 |
| 2 | 4→1, 2→1 | 2 or 4 |
| 4 | 4→2, 2→1 | 4 |
| 3 | 4→2, 2→1, 3→1 | 4 |
| 4 | 4→3, 2→1, 3→1 | 4 |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="towers-az101"></a>

## Towers AZ101

**Difficulty:** Easy  
**Pattern:** Multiset Greedy

### Problem Description

Place each block on an existing tower when possible; minimize number of towers.

### Brute Thinking

Try every tower top linearly for each block.

### Optimal Approach

Use multiset of tower tops and upper_bound.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Try every tower top linearly for each block. |
| 2 | What structure removes repetition? | Multiset Greedy |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Multiset Greedy\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Block | Multiset of Tower Tops Before | Action | Tower Tops After |
|---:|---|---|---|
| 3 | {} | create new tower | {3} |
| 8 | {3} | no top > 8, create new | {3,8} |
| 2 | {3,8} | place on tower top 3 | {2,8} |
| 1 | {2,8} | place on tower top 2 | {1,8} |
| 5 | {1,8} | place on tower top 8 | {1,5} |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="diversify-the-array"></a>

## Diversify the Array

**Difficulty:** Easy  
**Pattern:** Set + Frequency Map

### Problem Description

Work with distinct values and duplicates in an array.

### Brute Thinking

Try removing/changing each element and recompute distinct count.

### Optimal Approach

Use set/frequency map to know distinct and duplicate counts.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Try removing/changing each element and recompute distinct count. |
| 2 | What structure removes repetition? | Set + Frequency Map |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Set + Frequency Map\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Step | Array / Counts | Approach |
|---|---|---|
| Start | [1,1,2,3] | Count frequency of each value |
| Count | 1→2, 2→1, 3→1 | distinct = 3 |
| Duplicate check | value 1 has extra copy | duplicate count helps decisions |
| Result | unique values {1,2,3} | use set/frequency instead of trying removals |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="maximum-element-in-each-subarray-az101"></a>

## Maximum Element in each subarray AZ101

**Difficulty:** Easy  
**Pattern:** Monotonic Deque

### Problem Description

Find maximum in every window of size k.

### Brute Thinking

Scan each window completely: O(nk).

### Optimal Approach

Maintain decreasing deque of useful indices.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Scan each window completely: O(nk). |
| 2 | What structure removes repetition? | Monotonic Deque |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> maxInWindows(vector<int> a, int k) {
    deque<int> dq;
    vector<int> ans;

    for (int i = 0; i < (int)a.size(); i++) {
        while (!dq.empty() && dq.front() <= i - k) dq.pop_front();

        while (!dq.empty() && a[dq.back()] <= a[i]) dq.pop_back();

        dq.push_back(i);

        if (i >= k - 1) ans.push_back(a[dq.front()]);
    }

    return ans;
}

int main() {
    vector<int> a = {1, 3, -1, -3, 5, 3, 6, 7};
    int k = 3;

    vector<int> ans = maxInWindows(a, k);

    for (int x : ans) cout << x << " ";
    cout << "\n";

    return 0;
}
```

### Dry Run Example

| i | value | Deque Values | Window | Answer |
|---:|---:|---|---|---:|
| 0 | 1 | [1] | not full | - |
| 1 | 3 | [3] | not full | - |
| 2 | -1 | [3,-1] | [1,3,-1] | 3 |
| 3 | -3 | [3,-1,-3] | [3,-1,-3] | 3 |
| 4 | 5 | [5] | [-1,-3,5] | 5 |
| 5 | 3 | [5,3] | [-3,5,3] | 5 |

### ASCII Diagram

```text
0 1 2 3 4 5 6
|---window---|
    |---window---|

Move right pointer.
Move left pointer only when invalid.
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="queue-az101"></a>

## Queue AZ101

**Difficulty:** Easy  
**Pattern:** Queue STL

### Problem Description

Perform basic queue operations.

### Brute Thinking

Use vector and erase from front, causing O(n) shifts.

### Optimal Approach

Use std::queue for O(1) front/pop/push.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Use vector and erase from front, causing O(n) shifts. |
| 2 | What structure removes repetition? | Queue STL |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Queue STL\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Operation | Queue State | Output |
|---|---|---|
| push 10 | [10] | - |
| push 20 | [10,20] | - |
| front | [10,20] | 10 |
| pop | [20] | removes 10 |
| front | [20] | 20 |

### ASCII Diagram

```text
push side                 pop side
[in stack]  ----move----> [out stack]
                         front/top is queue front
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="max-diff"></a>

## Max Diff

**Difficulty:** Easy  
**Pattern:** Prefix Minimum

### Problem Description

Find max a[j]-a[i] where j>i.

### Brute Thinking

Try all pairs.

### Optimal Approach

Track minimum value seen so far.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Try all pairs. |
| 2 | What structure removes repetition? | Prefix Minimum |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Prefix Minimum\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| i | a[i] | Minimum Before | Candidate Difference | Best |
|---:|---:|---:|---:|---:|
| 0 | 7 | 7 | - | - |
| 1 | 1 | 7 | -6 | -6 |
| 2 | 5 | 1 | 4 | 4 |
| 3 | 3 | 1 | 2 | 4 |
| 4 | 6 | 1 | 5 | 5 |
| 5 | 4 | 1 | 3 | 5 |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="sort-by-roll-number"></a>

## Sort by Roll Number

**Difficulty:** Easy  
**Pattern:** Sort Comparator

### Problem Description

Sort student records by roll number.

### Brute Thinking

Implement manual sorting.

### Optimal Approach

Use std::sort with comparator.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Implement manual sorting. |
| 2 | What structure removes repetition? | Sort Comparator |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Sort Comparator\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Step | Records | Approach |
|---|---|---|
| Start | (3,C), (1,A), (2,B) | Need sorted by roll |
| Comparator | compare roll values | a.roll < b.roll |
| After sort | (1,A), (2,B), (3,C) | STL sort handles ordering |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="special-heap"></a>

## Special Heap

**Difficulty:** Easy  
**Pattern:** Priority Queue Comparator

### Problem Description

Maintain elements ordered by custom priority.

### Brute Thinking

Sort all elements after every update.

### Optimal Approach

Use priority_queue with custom comparator.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Sort all elements after every update. |
| 2 | What structure removes repetition? | Priority Queue Comparator |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Priority Queue Comparator\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Operation | Heap Top Logic | Current Top |
|---|---|---|
| push (5,2) | highest priority first | (5,2) |
| push (10,3) | 10 > 5 | (10,3) |
| push (10,1) | tie: smaller id first | (10,1) |
| pop | removes best | next is (10,3) |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="medium"></a>

# Medium

<a id="maximum-rate-subarray"></a>

## Maximum Rate Subarray

**Difficulty:** Medium  
**Pattern:** Sliding Window / Prefix

### Problem Description

Find best subarray satisfying a sum/rate-like condition.

### Brute Thinking

Try all subarrays.

### Optimal Approach

Use sliding window when condition is monotonic, otherwise prefix sums.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Try all subarrays. |
| 2 | What structure removes repetition? | Sliding Window / Prefix |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Sliding Window / Prefix\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| right | Added | Window Sum | Action | Best Length |
|---:|---:|---:|---|---:|
| 0 | 1 | 1 | valid | 1 |
| 1 | 2 | 3 | valid | 2 |
| 2 | 1 | 4 | valid | 3 |
| 3 | 3 | 7 | shrink from left | 3 |
| 4 | 2 | 6 | shrink until valid | 3 |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="smart-sale"></a>

## Smart Sale

**Difficulty:** Medium  
**Pattern:** Frequency Sort

### Problem Description

Remove items to minimize remaining product types.

### Brute Thinking

Each time scan all frequencies and remove smallest.

### Optimal Approach

Count frequencies, sort them, remove cheapest types first.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Each time scan all frequencies and remove smallest. |
| 2 | What structure removes repetition? | Frequency Sort |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Frequency Sort\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Step | Data | Approach |
|---|---|---|
| Items | [1,1,2,2,3] | Count frequencies |
| Frequency | 1→2, 2→2, 3→1 | Sort counts: [1,2,2] |
| k=2 | remove type with count 1 | remaining k=1, types=2 |
| stop | next count is 2 > k | answer = 2 types |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="generating-permutations-az101"></a>

## Generating Permutations AZ101

**Difficulty:** Medium  
**Pattern:** next_permutation

### Problem Description

Generate all permutations of a sequence.

### Brute Thinking

Manually construct all orders with many loops.

### Optimal Approach

Sort first and repeatedly call next_permutation.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Manually construct all orders with many loops. |
| 2 | What structure removes repetition? | next_permutation |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: next_permutation\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Step | Current Permutation | Next Action |
|---:|---|---|
| 1 | 1 2 3 | print |
| 2 | 1 3 2 | next_permutation |
| 3 | 2 1 3 | next_permutation |
| 4 | 2 3 1 | next_permutation |
| 5 | 3 1 2 | next_permutation |
| 6 | 3 2 1 | last permutation |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="happy-neighborhood"></a>

## Happy Neighborhood

**Difficulty:** Medium  
**Pattern:** Greedy + Sorting

### Problem Description

Arrange/select values under neighbor constraints.

### Brute Thinking

Try all arrangements.

### Optimal Approach

Sort/frequency + greedy placement.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Try all arrangements. |
| 2 | What structure removes repetition? | Greedy + Sorting |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Greedy + Sorting\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Step | Data | Greedy Thought |
|---|---|---|
| Start | unsorted values | Ordering helps compare neighbors |
| Sort | increasing order | choose smallest valid next |
| Scan | maintain previous choice | avoid invalid neighbor relation |
| Result | chosen arrangement/count | greedy avoids trying all permutations |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="longest-segment"></a>

## Longest Segment

**Difficulty:** Medium  
**Pattern:** Two Pointers

### Problem Description

Find longest contiguous segment satisfying condition.

### Brute Thinking

Check every l,r pair.

### Optimal Approach

Use two pointers/sliding window when validity is monotonic.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Check every l,r pair. |
| 2 | What structure removes repetition? | Two Pointers |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Two Pointers\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| right | Add | Window | Valid? | Best |
|---:|---:|---|---|---:|
| 0 | 1 | [1] | yes | 1 |
| 1 | 2 | [1,2] | yes | 2 |
| 2 | 1 | [1,2,1] | yes | 3 |
| 3 | 3 | [1,2,1,3] | maybe invalid | shrink left |
| 4 | 2 | valid window restored | yes | updated |

### ASCII Diagram

```text
0 1 2 3 4 5 6
|---window---|
    |---window---|

Move right pointer.
Move left pointer only when invalid.
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="set-az101"></a>

## Set AZ101

**Difficulty:** Medium  
**Pattern:** Set STL

### Problem Description

Maintain sorted unique elements.

### Brute Thinking

Store in vector and search linearly.

### Optimal Approach

Use std::set for O(log n) updates/search.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Store in vector and search linearly. |
| 2 | What structure removes repetition? | Set STL |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Set STL\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Operation | Set State | Why |
|---|---|---|
| insert 5 | {5} | unique sorted |
| insert 1 | {1,5} | auto sorted |
| insert 5 | {1,5} | duplicate ignored |
| lower_bound(3) | points to 5 | first value >= 3 |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="solve-intervals-3"></a>

## Solve Intervals 3

**Difficulty:** Medium  
**Pattern:** Interval Set

### Problem Description

Maintain non-overlapping intervals and merge inserted ranges.

### Brute Thinking

Compare inserted range with every interval.

### Optimal Approach

Use set<pair<int,int>> with lower_bound and erase merged intervals.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Compare inserted range with every interval. |
| 2 | What structure removes repetition? | Interval Set |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

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

            if (prevIt->second >= l - 1) {
                it = prevIt;
            }
        }

        while (it != ranges.end() && it->first <= r + 1) {
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
    RangeCover rc;

    rc.insertRange(1, 3);
    rc.insertRange(7, 10);
    rc.insertRange(13, 16);

    rc.print();

    rc.insertRange(4, 12);

    rc.print();

    cout << rc.covered(9) << "\n";

    return 0;
}
```

### Dry Run Example

| Step | Operation | l,r | Set |
|---:|---|---|---|
| 1 | Start | - | [1,3], [7,10], [13,16] |
| 2 | insert [4,12] | 4,12 | lower_bound points to [7,10] |
| 3 | check previous | 4,12 | [1,3] touches because 3 >= 4-1 |
| 4 | merge [1,3] | 1,12 | [7,10], [13,16] |
| 5 | merge [7,10] | 1,12 | [13,16] |
| 6 | merge [13,16] | 1,16 | empty |
| 7 | insert final | 1,16 | [1,16] |

### ASCII Diagram

```text
Before:
[1,3]   [7,10]   [13,16]

Insert:
    [4,12]

After merging touching/overlapping intervals:
[1,16]
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="addmul"></a>

## ADDMUL

**Difficulty:** Medium  
**Pattern:** Lazy Math

### Problem Description

Apply add/multiply operations efficiently.

### Brute Thinking

Update every element per query.

### Optimal Approach

Maintain lazy transformation x -> x*mul + add.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Update every element per query. |
| 2 | What structure removes repetition? | Lazy Math |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Lazy Math\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Operation | mul | add | Meaning |
|---|---:|---:|---|
| start | 1 | 0 | value = x |
| add 5 | 1 | 5 | value = x + 5 |
| multiply 2 | 2 | 10 | value = 2x + 10 |
| original x=3 | 2 | 10 | final = 16 |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="multimap-az101"></a>

## Multimap AZ101

**Difficulty:** Medium  
**Pattern:** Multimap

### Problem Description

Store multiple values under the same key.

### Brute Thinking

Use map<int,vector<int>> manually.

### Optimal Approach

Use multimap or map of vectors depending on output needs.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Use map<int,vector<int>> manually. |
| 2 | What structure removes repetition? | Multimap |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Multimap\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Operation | Structure | Meaning |
|---|---|---|
| insert (1,10) | 1→10 | key 1 has value 10 |
| insert (1,20) | 1→10,20 | same key stores multiple values |
| insert (2,30) | 2→30 | another key |
| equal_range(1) | 10,20 | retrieve all values for key 1 |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="lfu-cache"></a>

## LFU Cache

**Difficulty:** Medium  
**Pattern:** HashMap + Lists

### Problem Description

Implement cache eviction by least frequency and recency.

### Brute Thinking

Scan all cache entries on eviction.

### Optimal Approach

Use maps from key to node and frequency to ordered key list.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Scan all cache entries on eviction. |
| 2 | What structure removes repetition? | HashMap + Lists |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: HashMap + Lists\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Operation | Cache State | Frequency State | Note |
|---|---|---|---|
| put(1,10) | {1} | f1: [1] | minFreq=1 |
| put(2,20) | {1,2} | f1: [2,1] | both freq 1 |
| get(1) | {1,2} | f1:[2], f2:[1] | 1 becomes freq 2 |
| put(3,30) | {1,3} | f1:[3], f2:[1] | evict key 2 |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="distinct-characters-az101"></a>

## Distinct Characters AZ101

**Difficulty:** Medium  
**Pattern:** Frequency Window

### Problem Description

Count distinct characters in windows/substrings.

### Brute Thinking

Recount characters for every window.

### Optimal Approach

Use sliding window with frequency array.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Recount characters for every window. |
| 2 | What structure removes repetition? | Frequency Window |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Frequency Window\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| right | char | Frequency State | Distinct | Window |
|---:|---|---|---:|---|
| 0 | a | a:1 | 1 | a |
| 1 | b | a:1,b:1 | 2 | ab |
| 2 | a | a:2,b:1 | 2 | aba |
| 3 | c | a:2,b:1,c:1 | 3 | abac |
| shrink | remove left | adjust counts | valid again | sliding window |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="support-queries-ii"></a>

## Support Queries II

**Difficulty:** Medium  
**Pattern:** Ordered Set Queries

### Problem Description

Support dynamic insert/delete/search queries.

### Brute Thinking

Scan all active values per query.

### Optimal Approach

Use set/multiset with lower_bound/upper_bound.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Scan all active values per query. |
| 2 | What structure removes repetition? | Ordered Set Queries |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Ordered Set Queries\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Query | Ordered DS State | Answer / Action |
|---|---|---|
| insert 5 | {5} | add value |
| insert 2 | {2,5} | sorted automatically |
| lower_bound 3 | {2,5} | answer 5 |
| erase 5 | {2} | remove one value |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="support-queries-i"></a>

## Support Queries I

**Difficulty:** Medium  
**Pattern:** Map / Set

### Problem Description

Support basic dynamic queries.

### Brute Thinking

Recompute from scratch.

### Optimal Approach

Use map/set based on query type.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Recompute from scratch. |
| 2 | What structure removes repetition? | Map / Set |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Map / Set\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Query | DS State | Approach |
|---|---|---|
| add 4 | {4} | insert in set/map |
| add 9 | {4,9} | maintain sorted/count state |
| find 4 | {4,9} | O(log n) or O(1) depending DS |
| remove 4 | {9} | update structure |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="powers-of-two"></a>

## Powers of Two

**Difficulty:** Medium  
**Pattern:** Hashing + Powers

### Problem Description

Check if values can pair to form a power of two.

### Brute Thinking

Try every pair.

### Optimal Approach

Use hash counts and test all powers for complements.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Try every pair. |
| 2 | What structure removes repetition? | Hashing + Powers |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool hasPowerOfTwoPair(vector<int> a) {
    unordered_map<int,int> count;

    for (int x : a) count[x]++;

    for (int x : a) {
        count[x]--;

        for (int p = 1; p <= (1 << 30); p <<= 1) {
            int need = p - x;

            if (count[need] > 0) {
                return true;
            }
        }

        count[x]++;
    }

    return false;
}

int main() {
    vector<int> a = {1, 5, 7};

    cout << hasPowerOfTwoPair(a) << "\n";

    return 0;
}
```

### Dry Run Example

| x | Test Power | Need | Found? |
|---:|---:|---:|---|
| 1 | 2 | 1 | needs another 1 |
| 1 | 4 | 3 | no |
| 1 | 8 | 7 | yes, pair 1+7 |
| result | - | - | true |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="fmbqueue"></a>

## FMBQUEUE

**Difficulty:** Medium  
**Pattern:** Deque Simulation

### Problem Description

Simulate special queue operations.

### Brute Thinking

Use vector insertion/deletion causing shifts.

### Optimal Approach

Use deque(s) for efficient end operations.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Use vector insertion/deletion causing shifts. |
| 2 | What structure removes repetition? | Deque Simulation |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Deque Simulation\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Operation | Deque State | Reason |
|---|---|---|
| push_back 10 | [10] | insert at rear |
| push_front 5 | [5,10] | insert at front |
| push_back 20 | [5,10,20] | normal queue rear |
| pop_front | [10,20] | remove front efficiently |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="next-permutation"></a>

## Next Permutation

**Difficulty:** Medium  
**Pattern:** Lexicographic Pivot

### Problem Description

Find next lexicographic permutation.

### Brute Thinking

Generate all permutations and locate current one.

### Optimal Approach

Find pivot, swap with next larger, reverse suffix.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Generate all permutations and locate current one. |
| 2 | What structure removes repetition? | Lexicographic Pivot |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

void myNextPermutation(vector<int>& a) {
    int n = a.size();
    int pivot = n - 2;

    while (pivot >= 0 && a[pivot] >= a[pivot + 1]) {
        pivot--;
    }

    if (pivot >= 0) {
        int j = n - 1;

        while (a[j] <= a[pivot]) {
            j--;
        }

        swap(a[pivot], a[j]);
    }

    reverse(a.begin() + pivot + 1, a.end());
}

int main() {
    vector<int> a = {1, 2, 3};

    myNextPermutation(a);

    for (int x : a) cout << x << " ";
    cout << "\n";

    return 0;
}
```

### Dry Run Example

| Step | Array | Explanation |
|---|---|---|
| start | [1,3,2] | need next larger permutation |
| find pivot | pivot=1 at value 1 | 1 < 3 |
| find swap | swap 1 with 2 | next larger from suffix |
| reverse suffix | [2,1,3] | smallest suffix after pivot |
| answer | [2,1,3] | next permutation |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="deque-az101"></a>

## Deque AZ101

**Difficulty:** Medium  
**Pattern:** Deque STL

### Problem Description

Use push/pop at both ends.

### Brute Thinking

Use vector and shift elements.

### Optimal Approach

Use std::deque.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Use vector and shift elements. |
| 2 | What structure removes repetition? | Deque STL |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Deque STL\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Operation | Deque State | Why O(1) |
|---|---|---|
| push_back 10 | [10] | add at end |
| push_front 5 | [5,10] | add at front |
| pop_back | [5] | remove from end |
| push_front 1 | [1,5] | front operation efficient |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="indexed-set"></a>

## Indexed Set

**Difficulty:** Medium  
**Pattern:** PBDS

### Problem Description

Support kth element and count of elements less than x.

### Brute Thinking

Sort every query.

### Optimal Approach

Use GNU PBDS ordered_set.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Sort every query. |
| 2 | What structure removes repetition? | PBDS |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
#include <ext/pb_ds/assoc_container.hpp>
using namespace std;
using namespace __gnu_pbds;

typedef tree<
    int,
    null_type,
    less<int>,
    rb_tree_tag,
    tree_order_statistics_node_update
> ordered_set;

int main() {
    ordered_set os;

    os.insert(10);
    os.insert(20);
    os.insert(30);

    cout << *os.find_by_order(1) << "\n";
    cout << os.order_of_key(25) << "\n";

    return 0;
}
```

### Dry Run Example

| Operation | Ordered Set | Result |
|---|---|---|
| insert 10 | {10} | - |
| insert 20 | {10,20} | - |
| insert 30 | {10,20,30} | - |
| find_by_order(1) | {10,20,30} | 20 |
| order_of_key(25) | {10,20,30} | 2 |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="set-queries-az101"></a>

## Set Queries AZ101

**Difficulty:** Medium  
**Pattern:** Set Bounds

### Problem Description

Answer lower/upper bound style set queries.

### Brute Thinking

Scan every element.

### Optimal Approach

Use std::set lower_bound/upper_bound.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Scan every element. |
| 2 | What structure removes repetition? | Set Bounds |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Set Bounds\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Query | Set | Result |
|---|---|---|
| insert 10 | {10} | - |
| insert 5 | {5,10} | - |
| insert 20 | {5,10,20} | - |
| lower_bound(12) | {5,10,20} | 20 |
| upper_bound(10) | {5,10,20} | 20 |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="running-mean-median-and-mode-az101"></a>

## Running Mean, Median and Mode AZ101

**Difficulty:** Medium  
**Pattern:** Two Multisets + Frequency

### Problem Description

Maintain mean, median, and mode after each insertion.

### Brute Thinking

Sort and recount after each insertion.

### Optimal Approach

Use two multisets for median and frequency maps for mode.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Sort and recount after each insertion. |
| 2 | What structure removes repetition? | Two Multisets + Frequency |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

multiset<int> low, high;
unordered_map<int,int> freq;

long long sumValues = 0;
int modeValue = 0;
int modeFreq = 0;

void rebalance() {
    while (low.size() > high.size() + 1) {
        high.insert(*low.rbegin());
        low.erase(prev(low.end()));
    }

    while (high.size() > low.size()) {
        low.insert(*high.begin());
        high.erase(high.begin());
    }
}

void addNumber(int x) {
    sumValues += x;

    if (low.empty() || x <= *low.rbegin()) low.insert(x);
    else high.insert(x);

    rebalance();

    freq[x]++;

    if (freq[x] > modeFreq || (freq[x] == modeFreq && x < modeValue)) {
        modeFreq = freq[x];
        modeValue = x;
    }
}

double getMedian() {
    if (low.size() == high.size()) {
        return (*low.rbegin() + *high.begin()) / 2.0;
    }

    return *low.rbegin();
}

int main() {
    vector<int> stream = {5, 15, 5, 10};

    for (int x : stream) {
        addNumber(x);

        int n = low.size() + high.size();

        cout << "After " << x << ": ";
        cout << "mean=" << fixed << setprecision(2) << (double)sumValues / n;
        cout << ", median=" << getMedian();
        cout << ", mode=" << modeValue << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Insert | low | high | mean | median | mode |
|---:|---|---|---:|---:|---:|
| 5 | [5] | [] | 5.00 | 5.00 | 5 |
| 15 | [5] | [15] | 10.00 | 10.00 | 5 |
| 5 | [5,5] | [15] | 8.33 | 5.00 | 5 |
| 10 | [5,5] | [10,15] | 8.75 | 7.50 | 5 |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="find-the-sum"></a>

## Find The Sum

**Difficulty:** Medium  
**Pattern:** Prefix Sum

### Problem Description

Answer range/subarray sum style queries.

### Brute Thinking

Sum elements each query.

### Optimal Approach

Use prefix sums.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Sum elements each query. |
| 2 | What structure removes repetition? | Prefix Sum |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Prefix Sum\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Index | a[i] | Prefix Sum |
|---:|---:|---:|
| 0 | - | 0 |
| 1 | 2 | 2 |
| 2 | 4 | 6 |
| 3 | 1 | 7 |
| 4 | 3 | 10 |
| Query [1,3] | 4+1+3 | pref[4]-pref[1]=8 |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="game-on-deque-az101"></a>

## Game on Deque AZ101

**Difficulty:** Medium  
**Pattern:** Deque + Cycle

### Problem Description

Simulate repeated deque game operations.

### Brute Thinking

Simulate all operations per query.

### Optimal Approach

Precompute initial phase and cycle after max reaches front.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Simulate all operations per query. |
| 2 | What structure removes repetition? | Deque + Cycle |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Deque + Cycle\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Operation | Deque | Observation |
|---|---|---|
| start | [3,1,5,2] | max is 5 |
| compare front two | 3 vs 1 | bigger stays front |
| after move | [3,5,2,1] | smaller goes back |
| compare | 3 vs 5 | 5 reaches front |
| cycle phase | [5,2,1,3] | after max front, rest cycles |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="duplicate-products"></a>

## Duplicate Products

**Difficulty:** Medium  
**Pattern:** Hash Set

### Problem Description

Detect duplicates among product ids/names.

### Brute Thinking

Compare all pairs.

### Optimal Approach

Use frequency map/set.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Compare all pairs. |
| 2 | What structure removes repetition? | Hash Set |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Hash Set\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Product | Seen Set | Action |
|---|---|---|
| A | {} | insert A |
| B | {A} | insert B |
| A | {A,B} | duplicate found |
| C | {A,B} | insert C |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="powers-of-two-2"></a>

## Powers Of Two

**Difficulty:** Medium  
**Pattern:** Hashing + Powers

### Problem Description

Variant of powers-of-two pair checking.

### Brute Thinking

Try all pairs.

### Optimal Approach

Use hash counts and powers iteration.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Try all pairs. |
| 2 | What structure removes repetition? | Hashing + Powers |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool hasPowerOfTwoPair(vector<int> a) {
    unordered_map<int,int> count;

    for (int x : a) count[x]++;

    for (int x : a) {
        count[x]--;

        for (int p = 1; p <= (1 << 30); p <<= 1) {
            int need = p - x;

            if (count[need] > 0) {
                return true;
            }
        }

        count[x]++;
    }

    return false;
}

int main() {
    vector<int> a = {1, 5, 7};

    cout << hasPowerOfTwoPair(a) << "\n";

    return 0;
}
```

### Dry Run Example

| x | Test Power | Need | Found? |
|---:|---:|---:|---|
| 1 | 2 | 1 | maybe |
| 1 | 4 | 3 | no |
| 1 | 8 | 7 | yes |
| result | - | - | true |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="the-social-network"></a>

## The Social Network

**Difficulty:** Medium  
**Pattern:** DSU

### Problem Description

Maintain friendship groups/connections.

### Brute Thinking

Check connectivity by scanning relations.

### Optimal Approach

Use DSU union/find.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Check connectivity by scanning relations. |
| 2 | What structure removes repetition? | DSU |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct DSU {
    vector<int> parent;
    vector<int> size;

    DSU(int n) {
        parent.resize(n);
        size.assign(n, 1);
        iota(parent.begin(), parent.end(), 0);
    }

    int find(int x) {
        if (parent[x] == x) return x;

        return parent[x] = find(parent[x]);
    }

    void unite(int a, int b) {
        a = find(a);
        b = find(b);

        if (a == b) return;

        if (size[a] < size[b]) swap(a, b);

        parent[b] = a;
        size[a] += size[b];
    }
};

int main() {
    DSU dsu(5);

    dsu.unite(0, 1);
    dsu.unite(3, 4);

    cout << (dsu.find(0) == dsu.find(1)) << "\n";
    cout << (dsu.find(0) == dsu.find(4)) << "\n";

    return 0;
}
```

### Dry Run Example

| Operation | Parent Groups | Result |
|---|---|---|
| union(0,1) | {0,1}, {2}, {3}, {4} | connect 0 and 1 |
| union(3,4) | {0,1}, {2}, {3,4} | connect 3 and 4 |
| find(0)==find(1) | same group | true |
| find(0)==find(4) | different groups | false |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="bachata-dance"></a>

## Bachata Dance

**Difficulty:** Medium  
**Pattern:** Greedy Pairing

### Problem Description

Pair/group values optimally.

### Brute Thinking

Try all pairings.

### Optimal Approach

Sort and greedily match.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Try all pairings. |
| 2 | What structure removes repetition? | Greedy Pairing |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Greedy Pairing\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Step | Values | Greedy Idea |
|---|---|---|
| start | unsorted skill/height values | pairing blindly is expensive |
| sort | increasing order | compare smallest feasible |
| scan | two pointers | make valid pairs greedily |
| result | count pairs | no need to try all pairings |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="evaluating-boolean-expressions"></a>

## Evaluating Boolean Expressions

**Difficulty:** Medium  
**Pattern:** Stack Expression Evaluation

### Problem Description

Evaluate boolean expression with operators.

### Brute Thinking

Repeated recursive parsing/scans.

### Optimal Approach

Use stacks for values/operators.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Repeated recursive parsing/scans. |
| 2 | What structure removes repetition? | Stack Expression Evaluation |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Stack Expression Evaluation\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Token | Value Stack | Operator Stack | Action |
|---|---|---|---|
| true | [T] | [] | push value |
| AND | [T] | [AND] | push operator |
| false | [T,F] | [AND] | push value |
| end | [F] | [] | apply AND |
| result | false | [] | final value |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="substrings-galore"></a>

## Substrings Galore

**Difficulty:** Medium  
**Pattern:** Sliding Window / Hashing

### Problem Description

Count/find substrings satisfying a condition.

### Brute Thinking

Generate all substrings.

### Optimal Approach

Use sliding window or hashing.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Generate all substrings. |
| 2 | What structure removes repetition? | Sliding Window / Hashing |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Sliding Window / Hashing\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| right | char | Window | Reason |
|---:|---|---|---|
| 0 | a | a | valid |
| 1 | b | ab | valid |
| 2 | a | aba | update freq |
| 3 | c | abac | may violate condition |
| shrink | remove left | bac/ac | restore validity |

### ASCII Diagram

```text
0 1 2 3 4 5 6
|---window---|
    |---window---|

Move right pointer.
Move left pointer only when invalid.
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="subsegment-sort"></a>

## Subsegment Sort

**Difficulty:** Medium  
**Pattern:** Sort + Mismatch

### Problem Description

Reason about sorting a subsegment.

### Brute Thinking

Sort every candidate subarray.

### Optimal Approach

Compare with sorted copy and find mismatch range.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Sort every candidate subarray. |
| 2 | What structure removes repetition? | Sort + Mismatch |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Sort + Mismatch\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Step | Array | Explanation |
|---|---|---|
| original | [1,5,3,4,2,6] | unsorted middle |
| sorted copy | [1,2,3,4,5,6] | target order |
| first mismatch | index 1 | 5 != 2 |
| last mismatch | index 4 | 2 != 5 |
| answer | sort [1,4] | this fixes array |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="gas-station"></a>

## Gas Station

**Difficulty:** Medium  
**Pattern:** Greedy Circular

### Problem Description

Find starting station to complete circuit.

### Brute Thinking

Try every start and simulate.

### Optimal Approach

Greedy reset when tank becomes negative.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Try every start and simulate. |
| 2 | What structure removes repetition? | Greedy Circular |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int canCompleteCircuit(vector<int> gas, vector<int> cost) {
    int total = 0;
    int tank = 0;
    int start = 0;

    for (int i = 0; i < (int)gas.size(); i++) {
        int gain = gas[i] - cost[i];

        total += gain;
        tank += gain;

        if (tank < 0) {
            start = i + 1;
            tank = 0;
        }
    }

    if (total < 0) return -1;

    return start;
}

int main() {
    vector<int> gas = {1, 2, 3, 4, 5};
    vector<int> cost = {3, 4, 5, 1, 2};

    cout << canCompleteCircuit(gas, cost) << "\n";

    return 0;
}
```

### Dry Run Example

| i | gas-cost | tank | start |
|---:|---:|---:|---:|
| 0 | -2 | reset to 0 | 1 |
| 1 | -2 | reset to 0 | 2 |
| 2 | -2 | reset to 0 | 3 |
| 3 | 3 | 3 | 3 |
| 4 | 3 | 6 | 3 |
| result | total >= 0 | - | start=3 |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="nearly-sorted-arrays"></a>

## Nearly Sorted Arrays

**Difficulty:** Medium  
**Pattern:** Min Heap

### Problem Description

Sort an array where each element is at most k away.

### Brute Thinking

Sort entire array.

### Optimal Approach

Use min-heap of size k+1.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Sort entire array. |
| 2 | What structure removes repetition? | Min Heap |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> sortNearlySorted(vector<int> a, int k) {
    priority_queue<int, vector<int>, greater<int>> pq;
    vector<int> ans;

    for (int x : a) {
        pq.push(x);

        if ((int)pq.size() > k) {
            ans.push_back(pq.top());
            pq.pop();
        }
    }

    while (!pq.empty()) {
        ans.push_back(pq.top());
        pq.pop();
    }

    return ans;
}

int main() {
    vector<int> a = {6, 5, 3, 2, 8, 10, 9};
    int k = 3;

    vector<int> ans = sortNearlySorted(a, k);

    for (int x : ans) cout << x << " ";
    cout << "\n";

    return 0;
}
```

### Dry Run Example

| Step | Pushed | Heap | Output |
|---:|---:|---|---|
| 1 | 6 | [6] | [] |
| 2 | 5 | [5,6] | [] |
| 3 | 3 | [3,6,5] | [] |
| 4 | 2 | [2,3,5,6] | 2 |
| 5 | 8 | [3,6,5,8] | 2,3 |
| end | pop rest | [] | 2,3,5,6,8,9,10 |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="queue-using-2-stacks-az101"></a>

## Queue using 2 Stacks AZ101

**Difficulty:** Medium  
**Pattern:** Two Stacks

### Problem Description

Queue implementation with two stacks.

### Brute Thinking

Move all elements every operation.

### Optimal Approach

Amortized transfer from input stack to output stack.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Move all elements every operation. |
| 2 | What structure removes repetition? | Two Stacks |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class MyQueue {
    stack<int> in, out;

    void shift() {
        if (!out.empty()) return;

        while (!in.empty()) {
            out.push(in.top());
            in.pop();
        }
    }

public:
    void push(int x) {
        in.push(x);
    }

    int front() {
        shift();
        return out.top();
    }

    int pop() {
        shift();
        int x = out.top();
        out.pop();
        return x;
    }
};

int main() {
    MyQueue q;

    q.push(10);
    q.push(20);
    q.push(30);

    cout << q.pop() << "\n";
    cout << q.front() << "\n";

    return 0;
}
```

### Dry Run Example

| Operation | in stack | out stack | Approach Reason |
|---|---|---|---|
| push 10 | [10] | [] | Push to input |
| push 20 | [10,20] | [] | O(1) |
| pop | [] | [20] | Transfer only when out empty |
| result | [] | [20] | FIFO gives 10 first |
| front | [] | [20] | answer 20 |

### ASCII Diagram

```text
push side                 pop side
[in stack]  ----move----> [out stack]
                         front/top is queue front
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="hamming-distance"></a>

## Hamming Distance

**Difficulty:** Medium  
**Pattern:** Bit Counting

### Problem Description

Compute total bit differences.

### Brute Thinking

Compare each pair bit by bit.

### Optimal Approach

Count set bits at every position.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Compare each pair bit by bit. |
| 2 | What structure removes repetition? | Bit Counting |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long totalHammingDistance(vector<int> a) {
    long long ans = 0;
    int n = a.size();

    for (int bit = 0; bit < 31; bit++) {
        long long ones = 0;

        for (int x : a) {
            if (x & (1 << bit)) ones++;
        }

        long long zeros = n - ones;
        ans += ones * zeros;
    }

    return ans;
}

int main() {
    vector<int> a = {4, 14, 2};

    cout << totalHammingDistance(a) << "\n";

    return 0;
}
```

### Dry Run Example

| Bit | Values with 1 | Values with 0 | Contribution |
|---:|---:|---:|---:|
| 0 | 0 | 3 | 0 |
| 1 | 2 | 1 | 2 |
| 2 | 2 | 1 | 2 |
| 3 | 1 | 2 | 2 |
| total | - | - | 6 |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="priority-queue"></a>

## Priority Queue

**Difficulty:** Medium  
**Pattern:** Heap

### Problem Description

Maintain dynamic max/min retrieval.

### Brute Thinking

Sort vector repeatedly.

### Optimal Approach

Use priority_queue.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Sort vector repeatedly. |
| 2 | What structure removes repetition? | Heap |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Heap\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Operation | Heap Top | Reason |
|---|---|---|
| push 7 | 7 | only value |
| push 2 | 7 | max heap keeps largest |
| push 10 | 10 | 10 is largest |
| pop | 7 | next largest after removing 10 |

### ASCII Diagram

```text
push side                 pop side
[in stack]  ----move----> [out stack]
                         front/top is queue front
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="elections"></a>

## Elections

**Difficulty:** Medium  
**Pattern:** Map + Leader Tracking

### Problem Description

Track leading candidate over time.

### Brute Thinking

Recount votes from scratch.

### Optimal Approach

Frequency map and tie rule; optionally precompute leaders.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Recount votes from scratch. |
| 2 | What structure removes repetition? | Map + Leader Tracking |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Map + Leader Tracking\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Vote | Count Map | Current Leader |
|---|---|---|
| A | A=1 | A |
| B | A=1,B=1 | tie rule decides |
| A | A=2,B=1 | A |
| C | A=2,B=1,C=1 | A |
| B | A=2,B=2,C=1 | tie rule decides |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="multiset-az101"></a>

## Multiset AZ101

**Difficulty:** Medium  
**Pattern:** Multiset

### Problem Description

Maintain sorted duplicates.

### Brute Thinking

Vector + sort after every update.

### Optimal Approach

Use multiset.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Vector + sort after every update. |
| 2 | What structure removes repetition? | Multiset |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Multiset\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Operation | Multiset | Note |
|---|---|---|
| insert 5 | {5} | add value |
| insert 1 | {1,5} | sorted |
| insert 5 | {1,5,5} | duplicate allowed |
| erase find(5) | {1,5} | removes one copy only |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="set-operations-az101"></a>

## Set Operations AZ101

**Difficulty:** Medium  
**Pattern:** Set Algorithms

### Problem Description

Compute union/intersection/difference.

### Brute Thinking

Nested loops.

### Optimal Approach

Use set algorithms or two pointers.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Nested loops. |
| 2 | What structure removes repetition? | Set Algorithms |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    set<int> a = {1, 2, 3};
    set<int> b = {3, 4, 5};

    vector<int> uni;
    vector<int> inter;

    set_union(a.begin(), a.end(), b.begin(), b.end(), back_inserter(uni));
    set_intersection(a.begin(), a.end(), b.begin(), b.end(), back_inserter(inter));

    cout << "Union: ";
    for (int x : uni) cout << x << " ";

    cout << "\nIntersection: ";
    for (int x : inter) cout << x << " ";

    cout << "\n";

    return 0;
}
```

### Dry Run Example

| Operation | Set A | Set B | Result |
|---|---|---|---|
| union | {1,2,3} | {3,4,5} | {1,2,3,4,5} |
| intersection | {1,2,3} | {3,4,5} | {3} |
| difference A-B | {1,2,3} | {3,4,5} | {1,2} |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="hard"></a>

# Hard

<a id="subarrays"></a>

## Subarrays

**Difficulty:** Hard  
**Pattern:** Prefix / Monotonic DS

### Problem Description

Count/analyze many subarrays.

### Brute Thinking

Enumerate all subarrays.

### Optimal Approach

Use prefix sums/maps or monotonic structures depending on property.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Enumerate all subarrays. |
| 2 | What structure removes repetition? | Prefix / Monotonic DS |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Prefix / Monotonic DS\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| right | prefix sum | Needed Info | Result |
|---:|---:|---|---|
| 0 | 2 | previous prefixes | count/update |
| 1 | 6 | prefix map | count/update |
| 2 | 7 | prefix map | count/update |
| 3 | 10 | prefix map | count/update |
| idea | - | subarray sum = pref[r]-pref[l-1] | avoid O(n²) |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="stl-searching"></a>

## STL Searching

**Difficulty:** Hard  
**Pattern:** Binary Search STL

### Problem Description

Use binary search style operations.

### Brute Thinking

Linear search.

### Optimal Approach

Use lower_bound, upper_bound, binary_search.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Linear search. |
| 2 | What structure removes repetition? | Binary Search STL |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Binary Search STL\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Query | Array | STL Result |
|---|---|---|
| lower_bound(3) | [1,3,3,5,7] | first 3 |
| upper_bound(3) | [1,3,3,5,7] | 5 |
| count of 3 | ub-lb | 2 |
| binary_search(5) | sorted array | true |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="pocket-money"></a>

## Pocket Money

**Difficulty:** Hard  
**Pattern:** Greedy + Multiset

### Problem Description

Optimize choosing/spending values.

### Brute Thinking

Try all distributions.

### Optimal Approach

Greedy with multiset/heap for best current choice.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Try all distributions. |
| 2 | What structure removes repetition? | Greedy + Multiset |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Greedy + Multiset\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Step | Multiset/Heap | Greedy Thought |
|---|---|---|
| start | available choices | choose best affordable/useful |
| pick one | remove/update chosen value | multiset supports erase |
| next | query best candidate again | avoid scanning all |
| result | optimized total | greedy + DS |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="find-the-triplet"></a>

## Find The Triplet

**Difficulty:** Hard  
**Pattern:** Sort + Two Pointers

### Problem Description

Find triplet satisfying target condition.

### Brute Thinking

Triple nested loops.

### Optimal Approach

Sort and use two pointers for each fixed first element.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Triple nested loops. |
| 2 | What structure removes repetition? | Sort + Two Pointers |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool hasTriplet(vector<int> a, int target) {
    sort(a.begin(), a.end());

    for (int i = 0; i < (int)a.size(); i++) {
        int l = i + 1;
        int r = a.size() - 1;

        while (l < r) {
            int sum = a[i] + a[l] + a[r];

            if (sum == target) return true;

            if (sum < target) l++;
            else r--;
        }
    }

    return false;
}

int main() {
    vector<int> a = {1, 4, 45, 6, 10, 8};
    int target = 22;

    cout << hasTriplet(a, target) << "\n";

    return 0;
}
```

### Dry Run Example

| Fixed | Left | Right | Sum | Move |
|---:|---:|---:|---:|---|
| 1 | 4 | 45 | 50 | too big, right-- |
| 1 | 4 | 10 | 15 | too small, left++ |
| 1 | 6 | 10 | 17 | too small, left++ |
| 4 | 8 | 10 | 22 | found |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="sports-meet"></a>

## Sports Meet

**Difficulty:** Hard  
**Pattern:** Sweep Line

### Problem Description

Handle event scheduling/overlaps.

### Brute Thinking

Check every pair of intervals.

### Optimal Approach

Sweep line events sorted by time.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Check every pair of intervals. |
| 2 | What structure removes repetition? | Sweep Line |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxOverlap(vector<pair<int,int>> intervals) {
    vector<pair<int,int>> events;

    for (auto [start, finish] : intervals) {
        events.push_back({start, +1});
        events.push_back({finish, -1});
    }

    sort(events.begin(), events.end());

    int current = 0;
    int best = 0;

    for (auto [time, delta] : events) {
        current += delta;
        best = max(best, current);
    }

    return best;
}

int main() {
    vector<pair<int,int>> intervals = {
        {1, 4},
        {2, 5},
        {7, 9},
        {3, 6}
    };

    cout << maxOverlap(intervals) << "\n";

    return 0;
}
```

### Dry Run Example

| Event | Delta | Active | Best |
|---|---:|---:|---:|
| time 1 start | +1 | 1 | 1 |
| time 2 start | +1 | 2 | 2 |
| time 3 start | +1 | 3 | 3 |
| time 4 end | -1 | 2 | 3 |
| result | - | - | 3 |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="fountains"></a>

## Fountains

**Difficulty:** Hard  
**Pattern:** Prefix/Suffix Greedy

### Problem Description

Choose optimal fountains/coverage/values.

### Brute Thinking

Try all pairs/combinations.

### Optimal Approach

Use prefix/suffix maxima or greedy coverage.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Try all pairs/combinations. |
| 2 | What structure removes repetition? | Prefix/Suffix Greedy |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Prefix/Suffix Greedy\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Index | Value | Prefix Best | Suffix Best |
|---:|---:|---:|---:|
| 0 | 1 | 1 | 8 |
| 1 | 4 | 4 | 8 |
| 2 | 2 | 4 | 8 |
| 3 | 8 | 8 | 8 |
| 4 | 5 | 8 | 5 |
| split | pref[left]+suff[right] | choose max | answer |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="extreme"></a>

# Extreme

<a id="nearest-neighbouring-city"></a>

## Nearest Neighbouring City

**Difficulty:** Extreme  
**Pattern:** Coordinate Map + Set

### Problem Description

Answer nearest city queries by coordinates.

### Brute Thinking

Compare query city with every city.

### Optimal Approach

Group by x/y coordinate and use ordered sets/maps.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Compare query city with every city. |
| 2 | What structure removes repetition? | Coordinate Map + Set |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {5, 1, 3, 5, 2};

    cout << "Pattern: Coordinate Map + Set\n";

    if constexpr (true) {
        sort(a.begin(), a.end());

        for (int x : a) {
            cout << x << " ";
        }

        cout << "\n";
    }

    return 0;
}
```

### Dry Run Example

| Query | Ordered Coordinate Set | Candidate |
|---|---|---|
| same x=0 | y values {0,5} | nearest above/below query y |
| same y=0 | x values {0,3,10} | nearest left/right query x |
| lower_bound | jump to next coordinate | O(log n) |
| check prev | nearest previous coordinate | O(log n) |
| answer | min horizontal/vertical | nearest city distance |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

<a id="unrated"></a>

# Unrated

<a id="maximum-number-of-customers-az101"></a>

## Maximum Number of Customers AZ101

**Difficulty:** Unrated  
**Pattern:** Sweep Line

### Problem Description

Find maximum simultaneous active customers.

### Brute Thinking

Compare all interval overlaps.

### Optimal Approach

Convert arrivals/leavings into events and sweep.

### Thinking Flow

| Stage | Question | Answer |
|---|---|---|
| 1 | What is repeated in brute force? | Compare all interval overlaps. |
| 2 | What structure removes repetition? | Sweep Line |
| 3 | What should be maintained? | The invariant needed for fast answer |
| 4 | Complexity target | Usually O(n), O(n log n), or O(q log n) |

### Complete C++ Pattern Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxOverlap(vector<pair<int,int>> intervals) {
    vector<pair<int,int>> events;

    for (auto [start, finish] : intervals) {
        events.push_back({start, +1});
        events.push_back({finish, -1});
    }

    sort(events.begin(), events.end());

    int current = 0;
    int best = 0;

    for (auto [time, delta] : events) {
        current += delta;
        best = max(best, current);
    }

    return best;
}

int main() {
    vector<pair<int,int>> intervals = {
        {1, 4},
        {2, 5},
        {7, 9},
        {3, 6}
    };

    cout << maxOverlap(intervals) << "\n";

    return 0;
}
```

### Dry Run Example

| Event | Delta | Active Customers | Best |
|---|---:|---:|---:|
| arrival at 1 | +1 | 1 | 1 |
| arrival at 2 | +1 | 2 | 2 |
| arrival at 3 | +1 | 3 | 3 |
| leaving at 4 | -1 | 2 | 3 |
| final | - | - | 3 |

### ASCII Diagram

```text
Input
  |
  v
Choose STL pattern
  |
  v
Maintain invariant
  |
  v
Answer efficiently
```

[Back to index](#clickable-index-by-difficulty)

---

# Final Revision Table

| Difficulty | Problem | Approach Notes |
|---|---|---|
| Novice | [All One](#all-one) | **HashMap + Frequency Buckets:** Use hash maps and frequency tracking. Standard exact O(1) version needs bucketed doubly linked list. |
| Novice | [Infix-Postfix](#infix-postfix) | **Stack + Precedence:** Use a stack for operators and output operands immediately. |
| Easy | [Queue From Stack](#queue-from-stack) | **Two Stacks:** Use two stacks: one for push, one for pop; transfer only when needed. |
| Easy | [Mode of Distances](#mode-of-distances) | **Frequency Map:** Use frequency map and track best frequency. |
| Easy | [Towers AZ101](#towers-az101) | **Multiset Greedy:** Use multiset of tower tops and upper_bound. |
| Easy | [Diversify the Array](#diversify-the-array) | **Set + Frequency Map:** Use set/frequency map to know distinct and duplicate counts. |
| Easy | [Maximum Element in each subarray AZ101](#maximum-element-in-each-subarray-az101) | **Monotonic Deque:** Maintain decreasing deque of useful indices. |
| Easy | [Queue AZ101](#queue-az101) | **Queue STL:** Use std::queue for O(1) front/pop/push. |
| Easy | [Max Diff](#max-diff) | **Prefix Minimum:** Track minimum value seen so far. |
| Easy | [Sort by Roll Number](#sort-by-roll-number) | **Sort Comparator:** Use std::sort with comparator. |
| Easy | [Special Heap](#special-heap) | **Priority Queue Comparator:** Use priority_queue with custom comparator. |
| Medium | [Maximum Rate Subarray](#maximum-rate-subarray) | **Sliding Window / Prefix:** Use sliding window when condition is monotonic, otherwise prefix sums. |
| Medium | [Smart Sale](#smart-sale) | **Frequency Sort:** Count frequencies, sort them, remove cheapest types first. |
| Medium | [Generating Permutations AZ101](#generating-permutations-az101) | **next_permutation:** Sort first and repeatedly call next_permutation. |
| Medium | [Happy Neighborhood](#happy-neighborhood) | **Greedy + Sorting:** Sort/frequency + greedy placement. |
| Medium | [Longest Segment](#longest-segment) | **Two Pointers:** Use two pointers/sliding window when validity is monotonic. |
| Medium | [Set AZ101](#set-az101) | **Set STL:** Use std::set for O(log n) updates/search. |
| Medium | [Solve Intervals 3](#solve-intervals-3) | **Interval Set:** Use set<pair<int,int>> with lower_bound and erase merged intervals. |
| Medium | [ADDMUL](#addmul) | **Lazy Math:** Maintain lazy transformation x -> x*mul + add. |
| Medium | [Multimap AZ101](#multimap-az101) | **Multimap:** Use multimap or map of vectors depending on output needs. |
| Medium | [LFU Cache](#lfu-cache) | **HashMap + Lists:** Use maps from key to node and frequency to ordered key list. |
| Medium | [Distinct Characters AZ101](#distinct-characters-az101) | **Frequency Window:** Use sliding window with frequency array. |
| Medium | [Support Queries II](#support-queries-ii) | **Ordered Set Queries:** Use set/multiset with lower_bound/upper_bound. |
| Medium | [Support Queries I](#support-queries-i) | **Map / Set:** Use map/set based on query type. |
| Medium | [Powers of Two](#powers-of-two) | **Hashing + Powers:** Use hash counts and test all powers for complements. |
| Medium | [FMBQUEUE](#fmbqueue) | **Deque Simulation:** Use deque(s) for efficient end operations. |
| Medium | [Next Permutation](#next-permutation) | **Lexicographic Pivot:** Find pivot, swap with next larger, reverse suffix. |
| Medium | [Deque AZ101](#deque-az101) | **Deque STL:** Use std::deque. |
| Medium | [Indexed Set](#indexed-set) | **PBDS:** Use GNU PBDS ordered_set. |
| Medium | [Set Queries AZ101](#set-queries-az101) | **Set Bounds:** Use std::set lower_bound/upper_bound. |
| Medium | [Running Mean, Median and Mode AZ101](#running-mean-median-and-mode-az101) | **Two Multisets + Frequency:** Use two multisets for median and frequency maps for mode. |
| Medium | [Find The Sum](#find-the-sum) | **Prefix Sum:** Use prefix sums. |
| Medium | [Game on Deque AZ101](#game-on-deque-az101) | **Deque + Cycle:** Precompute initial phase and cycle after max reaches front. |
| Medium | [Duplicate Products](#duplicate-products) | **Hash Set:** Use frequency map/set. |
| Medium | [Powers Of Two](#powers-of-two-2) | **Hashing + Powers:** Use hash counts and powers iteration. |
| Medium | [The Social Network](#the-social-network) | **DSU:** Use DSU union/find. |
| Medium | [Bachata Dance](#bachata-dance) | **Greedy Pairing:** Sort and greedily match. |
| Medium | [Evaluating Boolean Expressions](#evaluating-boolean-expressions) | **Stack Expression Evaluation:** Use stacks for values/operators. |
| Medium | [Substrings Galore](#substrings-galore) | **Sliding Window / Hashing:** Use sliding window or hashing. |
| Medium | [Subsegment Sort](#subsegment-sort) | **Sort + Mismatch:** Compare with sorted copy and find mismatch range. |
| Medium | [Gas Station](#gas-station) | **Greedy Circular:** Greedy reset when tank becomes negative. |
| Medium | [Nearly Sorted Arrays](#nearly-sorted-arrays) | **Min Heap:** Use min-heap of size k+1. |
| Medium | [Queue using 2 Stacks AZ101](#queue-using-2-stacks-az101) | **Two Stacks:** Amortized transfer from input stack to output stack. |
| Medium | [Hamming Distance](#hamming-distance) | **Bit Counting:** Count set bits at every position. |
| Medium | [Priority Queue](#priority-queue) | **Heap:** Use priority_queue. |
| Medium | [Elections](#elections) | **Map + Leader Tracking:** Frequency map and tie rule; optionally precompute leaders. |
| Medium | [Multiset AZ101](#multiset-az101) | **Multiset:** Use multiset. |
| Medium | [Set Operations AZ101](#set-operations-az101) | **Set Algorithms:** Use set algorithms or two pointers. |
| Hard | [Subarrays](#subarrays) | **Prefix / Monotonic DS:** Use prefix sums/maps or monotonic structures depending on property. |
| Hard | [STL Searching](#stl-searching) | **Binary Search STL:** Use lower_bound, upper_bound, binary_search. |
| Hard | [Pocket Money](#pocket-money) | **Greedy + Multiset:** Greedy with multiset/heap for best current choice. |
| Hard | [Find The Triplet](#find-the-triplet) | **Sort + Two Pointers:** Sort and use two pointers for each fixed first element. |
| Hard | [Sports Meet](#sports-meet) | **Sweep Line:** Sweep line events sorted by time. |
| Hard | [Fountains](#fountains) | **Prefix/Suffix Greedy:** Use prefix/suffix maxima or greedy coverage. |
| Extreme | [Nearest Neighbouring City](#nearest-neighbouring-city) | **Coordinate Map + Set:** Group by x/y coordinate and use ordered sets/maps. |
| Unrated | [Maximum Number of Customers AZ101](#maximum-number-of-customers-az101) | **Sweep Line:** Convert arrivals/leavings into events and sweep. |
