# AlgoZenith — STL Quick Notes
## Visual Self-Learning Version

> **Goal:** Learn STL as a problem-solving toolbox for Competitive Programming.  
> For each topic: **prerequisites → what it is → when to use → visual model → step-by-step example → code pattern → recognition signal → common mistake**.

# Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. Vector](#1-vector)
- [2. Stack](#2-stack)
- [3. Queue](#3-queue)
- [4. Deque](#4-deque)
- [5. Priority Queue](#5-priority-queue)
- [6. Set / Unordered Set](#6-set--unordered-set)
- [7. Multiset](#7-multiset)
- [8. Map / Unordered Map](#8-map--unordered-map)
- [9. Indexed Set (PBDS)](#9-indexed-set-pbds)
- [10. STL Sorting](#10-stl-sorting)
- [11. Lower Bound / Upper Bound](#11-lower-bound--upper-bound)
- [12. Next Permutation](#12-next-permutation)
- [13. Random / mt19937](#13-random--mt19937)
- [14. STL Recognition Cheat Sheet](#14-stl-recognition-cheat-sheet)
- [15. Complexity Quick Sheet](#15-complexity-quick-sheet)
- [16. Final Mental Model](#16-final-mental-model)

---

# 0. Prerequisites

Before STL, be comfortable with these C++ basics.

## 0.1 Arrays and Indexing

```cpp
int a[5] = {4, 1, 7, 2, 9};
```

Indices:

```text
index:  0   1   2   3   4
value:  4   1   7   2   9
```

Access:

```cpp
cout << a[2]; // 7
```

---

## 0.2 Loops

Index-based:

```cpp
for (int i = 0; i < n; i++) {
    cout << a[i] << ' ';
}
```

Range-based:

```cpp
for (int x : a) {
    cout << x << ' ';
}
```

---

## 0.3 References

For large objects, avoid copying.

Prefer:

```cpp
for (const auto& x : container) {
    ...
}
```

instead of unnecessary copies.

---

## 0.4 Iterators

Think of an iterator as:

```text
"pointer-like position inside an STL container"
```

Example:

```cpp
vector<int> a = {10, 20, 30};

auto it = a.begin();

cout << *it; // 10
```

Visual:

```text
a:

[10] [20] [30]
 ^
 |
begin()
```

`end()` points **one position after** the final element:

```text
[10] [20] [30] [end]
```

---

## 0.5 Complexity Thinking

Always ask:

```text
What operation do I need repeatedly?
```

Examples:

```text
random access?
fast min/max?
membership test?
sorted order?
frequency counting?
rank?
```

Then choose STL based on the required operation.

---

## 0.6 Common STL Decision Questions

For any container, remember:

```text
1. What ordering does it maintain?
2. Are duplicates allowed?
3. What can I access quickly?
4. Insert / erase / search complexity?
5. What problem signal suggests this container?
```

---

# 1. Vector

## 1.1 What Is a Vector?

A `vector` is a dynamic array.

```cpp
vector<int> a = {5, 2, 8};
```

Visual:

```text
index:  0   1   2
value:  5   2   8
```

Unlike a fixed C-style array, its size can grow.

---

## 1.2 When to Use

Think `vector` when you need:

```text
indexing
traversal
dynamic size
sorting
binary search
```

---

## 1.3 Key Operations

| Method | Meaning | Complexity |
|---|---|---:|
| `push_back(x)` | add at end | amortized `O(1)` |
| `pop_back()` | remove last | `O(1)` |
| `a[i]` | random access | `O(1)` |
| `size()` | item count | `O(1)` |
| `front()` | first item | `O(1)` |
| `back()` | last item | `O(1)` |
| `empty()` | empty? | `O(1)` |
| `clear()` | remove all | `O(n)` |
| `erase(it)` | erase at position | `O(n)` |

---

## 1.4 ASCII Growth Example

Start:

```text
[]
```

```cpp
a.push_back(4);
```

```text
[4]
```

```cpp
a.push_back(1);
```

```text
[4, 1]
```

```cpp
a.push_back(7);
```

```text
[4, 1, 7]
```

---

## 1.5 Sort a Vector

```cpp
sort(a.begin(), a.end());
```

Before:

```text
[4,1,7]
```

After:

```text
[1,4,7]
```

---

## 1.6 Practice — Remove Duplicates from Sorted Array

Problem:
LeetCode 26 — Remove Duplicates from Sorted Array

Input:

```text
[1,1,2,2,3]
```

Goal:

```text
keep unique values in front

[1,2,3,_,_]
```

---

## 1.7 Step-by-Step

Because array is sorted:

```text
equal values are adjacent
```

Maintain:

```text
k = next position for a new unique value
```

ASCII:

```text
input:

1 1 2 2 3
^

unique front initially:
[1]

k = 1
```

Scan:

```text
second 1:
same as previous
skip

2:
different
a[k] = 2
k++

2:
same
skip

3:
different
a[k] = 3
k++
```

Final valid prefix:

```text
[1,2,3]
```

---

## 1.8 C++17

```cpp
int removeDuplicates(vector<int>& a) {
    if (a.empty()) {
        return 0;
    }

    int k = 1;

    for (int i = 1; i < (int)a.size(); i++) {
        if (a[i] != a[i - 1]) {
            a[k] = a[i];
            k++;
        }
    }

    return k;
}
```

---

## 1.9 Recognition Signal

```text
dynamic array
index access
sort/traverse
```

Think:

```text
vector
```

---

## 1.10 Common Mistake

Erasing from the middle repeatedly can be expensive:

```cpp
a.erase(a.begin() + i);
```

because later elements shift.

---

# 2. Stack

## 2.1 What Is a Stack?

Stack follows:

```text
LIFO
Last In, First Out
```

Visual:

```text
top
 |
 v

[30]
[20]
[10]
```

`30` leaves first.

---

## 2.2 When to Use

Think stack when:

```text
latest unmatched item first
nested structure
undo
monotonic stack
expression evaluation
```

---

## 2.3 Key Operations

```cpp
stack<int> st;

st.push(10);
st.push(20);

cout << st.top(); // 20

st.pop();
```

---

## 2.4 ASCII Push / Pop

Start:

```text
empty
```

Push 10:

```text
[10] <- top
```

Push 20:

```text
[20] <- top
[10]
```

Pop:

```text
[10] <- top
```

---

## 2.5 Practice — Valid Parentheses

Problem:
LeetCode 20 — Valid Parentheses

Need:

```text
latest opening bracket
must match current closing bracket
```

Example:

```text
([])
```

Stack flow:

```text
(  -> push

stack:
(

[  -> push

stack:
(
[ <- top

]  -> matches [
pop

stack:
(

)  -> matches (
pop

empty
=> valid
```

---

## 2.6 C++17

```cpp
bool isValid(string s) {
    stack<char> st;

    for (char c : s) {
        if (c == '(' || c == '[' || c == '{') {
            st.push(c);
        } else {
            if (st.empty()) {
                return false;
            }

            char open = st.top();
            st.pop();

            if ((c == ')' && open != '(') ||
                (c == ']' && open != '[') ||
                (c == '}' && open != '{')) {
                return false;
            }
        }
    }

    return st.empty();
}
```

---

## 2.7 Recognition Signal

```text
latest unresolved element must be processed first
```

Think:

```text
stack
```

---

# 3. Queue

## 3.1 What Is a Queue?

Queue follows:

```text
FIFO
First In, First Out
```

Visual:

```text
FRONT                  BACK
  |                      |
  v                      v

[10] [20] [30]
```

`10` leaves first.

---

## 3.2 When to Use

Think queue when:

```text
process in arrival order
BFS
events
remove oldest first
```

---

## 3.3 Key Operations

```cpp
queue<int> q;

q.push(10);
q.push(20);

cout << q.front(); // 10

q.pop();
```

---

## 3.4 ASCII Flow

```text
push 10:

FRONT
 |
 v

[10]

push 20:

FRONT        BACK
 |             |
 v             v

[10] [20]

pop:

[20]
```

---

## 3.5 Practice — Number of Recent Calls

Problem:
LeetCode 933 — Number of Recent Calls

Keep timestamps inside:

```text
[t - 3000, t]
```

Anything older should leave from the front.

ASCII:

```text
queue:

oldest ---------------------- newest

100   300   3500   4000

when t = 4000:

valid range:
[1000, 4000]

100  -> expired
300  -> expired

3500, 4000 remain
```

---

## 3.6 C++17

```cpp
class RecentCounter {
    queue<int> q;

public:
    int ping(int t) {
        q.push(t);

        while (!q.empty() && q.front() < t - 3000) {
            q.pop();
        }

        return q.size();
    }
};
```

---

## 3.7 Recognition Signal

```text
oldest event first
BFS
arrival order
```

Think:

```text
queue
```

---

# 4. Deque

## 4.1 What Is a Deque?

Deque means:

```text
double-ended queue
```

You can insert/remove at both ends.

```text
FRONT <--------------------> BACK
```

---

## 4.2 Key Operations

```cpp
deque<int> dq;

dq.push_back(2);
dq.push_front(1);
dq.push_back(3);
```

Result:

```text
[1,2,3]
```

---

## 4.3 When to Use

Think deque when:

```text
need both ends
sliding window
monotonic deque
0-1 BFS
```

---

## 4.4 Practice — Sliding Window Maximum

Problem:
LeetCode 239 — Sliding Window Maximum

Core idea:

```text
FRONT:
remove expired indices

BACK:
remove weaker candidates
```

For maximum, keep values decreasing.

---

## 4.5 ASCII Example

```text
a = [1,3,-1,-3,5]
k = 3
```

At `i = 2`:

```text
window:
[1,3,-1]

deque:
[1:3, 2:-1]

front value = 3
```

At `i = 4`:

```text
window:
[-1,-3,5]
```

Old index for `3` expires.

Then `5` arrives:

```text
-3 <= 5 -> pop back
-1 <= 5 -> pop back
```

Deque:

```text
[4:5]
```

Maximum:

```text
5
```

---

## 4.6 C++17

```cpp
vector<int> maxSlidingWindow(vector<int>& a, int k) {
    deque<int> dq;
    vector<int> ans;

    for (int i = 0; i < (int)a.size(); i++) {
        while (!dq.empty() && dq.front() <= i - k) {
            dq.pop_front();
        }

        while (!dq.empty() && a[dq.back()] <= a[i]) {
            dq.pop_back();
        }

        dq.push_back(i);

        if (i >= k - 1) {
            ans.push_back(a[dq.front()]);
        }
    }

    return ans;
}
```

---

## 4.7 Recognition Signal

```text
window
remove from front
maintain candidates at back
```

Think:

```text
deque
```

---

# 5. Priority Queue

## 5.1 What Is It?

`priority_queue` gives the highest-priority element first.

Default:

```text
max heap
```

Example:

```cpp
priority_queue<int> pq;
```

Insert:

```text
5, 2, 8, 1
```

Top:

```text
8
```

---

## 5.2 Visual Heap Idea

Conceptually:

```text
      8
     / \
    5   2
   /
  1
```

Top:

```text
8
```

---

## 5.3 Max Heap vs Min Heap

Max heap:

```cpp
priority_queue<int> mx;
```

Min heap:

```cpp
priority_queue<int,
               vector<int>,
               greater<int>> mn;
```

---

## 5.4 When to Use

Think priority queue when:

```text
repeatedly need current maximum
repeatedly need current minimum
top-k
Dijkstra
scheduling by priority
```

---

## 5.5 Practice — Kth Largest

Problem:
LeetCode 215 — Kth Largest Element in an Array

Example:

```text
a = [3,2,1,5,6,4]
k = 2
```

Max heap order:

```text
top = 6
pop

top = 5
```

Answer:

```text
5
```

---

## 5.6 C++17

```cpp
int findKthLargest(vector<int>& a, int k) {
    priority_queue<int> pq(a.begin(), a.end());

    while (--k) {
        pq.pop();
    }

    return pq.top();
}
```

---

## 5.7 Recognition Signal

```text
repeatedly need best current min/max
```

Think:

```text
priority_queue
```

---

# 6. Set / Unordered Set

## 6.1 Core Difference

```text
set
---
unique
sorted

unordered_set
-------------
unique
hash-based
no ordering
```

---

## 6.2 Complexity

| Container | Search / Insert / Erase | Ordered? |
|---|---:|---|
| `set` | `O(log n)` | Yes |
| `unordered_set` | average `O(1)` | No |

---

## 6.3 Visual

Insert:

```text
4, 1, 4, 2
```

Into `set`:

```text
1 2 4
```

Duplicate `4` disappears.

Into `unordered_set`:

```text
same unique values,
but iteration order is unspecified
```

---

## 6.4 Practice — Contains Duplicate

Problem:
LeetCode 217 — Contains Duplicate

Maintain:

```text
seen values
```

For each `x`:

```text
if x already exists
=> duplicate found
```

---

## 6.5 C++17

```cpp
bool containsDuplicate(vector<int>& a) {
    unordered_set<int> seen;

    for (int x : a) {
        if (seen.count(x)) {
            return true;
        }

        seen.insert(x);
    }

    return false;
}
```

---

## 6.6 Recognition Signal

Use `set` when:

```text
uniqueness + sorted order
```

Use `unordered_set` when:

```text
uniqueness + fast membership
```

---

# 7. Multiset

## 7.1 What Is It?

`multiset` is like `set`, but duplicates are allowed.

```cpp
multiset<int> ms = {2, 2, 5};
```

Stored:

```text
2 2 5
```

---

## 7.2 When to Use

Think multiset when you need:

```text
sorted values
duplicates
insert
erase one copy
current min/max
```

---

## 7.3 Important Erase Difference

This:

```cpp
ms.erase(5);
```

removes:

```text
ALL copies of 5
```

But:

```cpp
auto it = ms.find(5);

if (it != ms.end()) {
    ms.erase(it);
}
```

removes only:

```text
ONE copy
```

---

## 7.4 ASCII Example

Start:

```text
[2,2,5]
```

```cpp
ms.erase(2);
```

Result:

```text
[5]
```

All `2`s removed.

Instead:

```cpp
auto it = ms.find(2);
ms.erase(it);
```

Result:

```text
[2,5]
```

Only one removed.

---

## 7.5 Current Minimum / Maximum

Minimum:

```cpp
*ms.begin()
```

Maximum:

```cpp
*ms.rbegin()
```

Example:

```text
ms = [2,2,5]

min = 2
max = 5
```

---

## 7.6 Recognition Signal

```text
sorted
duplicates allowed
deletion needed
```

Think:

```text
multiset
```

---

# 8. Map / Unordered Map

## 8.1 What Is a Map?

Stores:

```text
key -> value
```

Example:

```text
2 -> 3
5 -> 1
7 -> 4
```

---

## 8.2 Common CP Use — Frequency

```cpp
unordered_map<int, int> freq;

for (int x : a) {
    freq[x]++;
}
```

Example:

```text
a = [2,3,2,5]

freq:

2 -> 2
3 -> 1
5 -> 1
```

---

## 8.3 map vs unordered_map

| Container | Operation | Ordered? |
|---|---:|---|
| `map` | `O(log n)` | Yes |
| `unordered_map` | average `O(1)` | No |

---

## 8.4 Practice — Two Sum

Problem:
LeetCode 1 — Two Sum

For current value:

```text
x
```

Need:

```text
target - x
```

Store previous values with their indices.

ASCII:

```text
target = 9

current = 7

need = 2

map contains:
2 -> index 0

answer:
[0, currentIndex]
```

---

## 8.5 C++17

```cpp
vector<int> twoSum(vector<int>& a, int target) {
    unordered_map<int, int> pos;

    for (int i = 0; i < (int)a.size(); i++) {
        int need = target - a[i];

        if (pos.count(need)) {
            return {pos[need], i};
        }

        pos[a[i]] = i;
    }

    return {};
}
```

---

## 8.6 Recognition Signal

```text
value -> frequency
value -> index
key -> information
```

Think:

```text
map / unordered_map
```

---

# 9. Indexed Set (PBDS)

## 9.1 What Is It?

PBDS indexed set behaves like an ordered set with extra rank operations.

Main operations:

```text
find k-th smallest
count elements < x
```

---

## 9.2 Setup

```cpp
#include <ext/pb_ds/assoc_container.hpp>
#include <ext/pb_ds/tree_policy.hpp>

using namespace __gnu_pbds;

template<class T>
using indexed_set = tree<
    T,
    null_type,
    less<T>,
    rb_tree_tag,
    tree_order_statistics_node_update
>;
```

---

## 9.3 Visual Example

Insert:

```text
10
30
20
```

Sorted logical order:

```text
index:  0   1   2
value: 10  20  30
```

Then:

```cpp
*s.find_by_order(1)
```

returns:

```text
20
```

And:

```cpp
s.order_of_key(25)
```

counts:

```text
values < 25
```

Those are:

```text
10, 20
```

Answer:

```text
2
```

---

## 9.4 Two Core Operations

```cpp
find_by_order(k)
```

means:

```text
k-th smallest
0-indexed
```

```cpp
order_of_key(x)
```

means:

```text
how many values are strictly less than x
```

---

## 9.5 Duplicates

Normal indexed set is unique.

For duplicates, store:

```cpp
pair<int, int>
```

Example:

```text
(value, unique_id)
```

So two equal values become:

```text
(5,0)
(5,1)
```

and can coexist.

---

## 9.6 Recognition Signal

```text
ordered set
rank
k-th smallest
count < x
```

Think:

```text
PBDS / indexed set
```

---

# 10. STL Sorting

## 10.1 What Does sort() Do?

`sort()` rearranges a random-access range.

Time:

```text
O(n log n)
```

---

## 10.2 Common Forms

Ascending:

```cpp
sort(a.begin(), a.end());
```

Descending:

```cpp
sort(a.rbegin(), a.rend());
```

Array:

```cpp
sort(a, a + n);
```

Custom comparator:

```cpp
sort(v.begin(), v.end(), cmp);
```

---

## 10.3 Visual Example

Before:

```text
[5,1,4,2]
```

Ascending:

```text
[1,2,4,5]
```

Descending:

```text
[5,4,2,1]
```

---

## 10.4 Custom Comparator

Suppose:

```text
name, score
```

Need:

```text
higher score first
```

Comparator:

```cpp
return x.second > y.second;
```

---

## 10.5 Practice — Sort People by Score

Input:

```text
A 70
B 90
C 80
```

After descending score:

```text
B 90
C 80
A 70
```

---

## 10.6 C++17

```cpp
vector<pair<string, int>> a = {
    {"A", 70},
    {"B", 90},
    {"C", 80}
};

sort(a.begin(), a.end(),
     [](const auto& x, const auto& y) {
         return x.second > y.second;
     });
```

---

## 10.7 Recognition Signal

```text
ordering makes structure easier
greedy after sorting
group equal values
binary search later
```

Think:

```text
sort
```

---

# 11. Lower Bound / Upper Bound

## 11.1 Prerequisite

The range must be sorted.

Example:

```text
a = [1,2,2,2,5]
```

---

## 11.2 Meaning

```text
lower_bound(x)
=
first position with value >= x
```

```text
upper_bound(x)
=
first position with value > x
```

---

## 11.3 ASCII Example

```text
a = [1,2,2,2,5]
     0 1 2 3 4
```

For `x = 2`:

```text
lower_bound(2)
        |
        v
[1, 2, 2, 2, 5]
    ^

index = 1
```

`upper_bound(2)`:

```text
[1, 2, 2, 2, 5]
             ^

index = 4
```

---

## 11.4 Count Occurrences

All `2`s occupy:

```text
[lower_bound, upper_bound)
```

So:

```text
count
=
upper_bound - lower_bound
```

For example:

```text
4 - 1 = 3
```

---

## 11.5 C++17

```cpp
vector<int> a = {1, 2, 2, 2, 5};

int L =
    lower_bound(a.begin(), a.end(), 2) - a.begin();

int R =
    upper_bound(a.begin(), a.end(), 2) - a.begin();

cout << L << ' ' << R;
```

Output:

```text
1 4
```

---

## 11.6 Practice — Search Insert Position

Problem:
LeetCode 35 — Search Insert Position

We need:

```text
first position >= target
```

That is exactly:

```text
lower_bound
```

---

## 11.7 C++17

```cpp
int searchInsert(vector<int>& a, int target) {
    return lower_bound(a.begin(), a.end(), target) - a.begin();
}
```

---

## 11.8 Recognition Signal

```text
sorted data
first valid position
first >= x
first > x
count equal values
```

Think:

```text
lower_bound / upper_bound
```

---

# 12. Next Permutation

## 12.1 What Is It?

`next_permutation` transforms a sequence into the next lexicographically larger permutation.

Example:

```text
1 2 3
  ↓
1 3 2
  ↓
2 1 3
```

---

## 12.2 What Does Lexicographically Mean?

Compare from left to right.

Example:

```text
1 3 2
2 1 3
```

At first differing position:

```text
1 < 2
```

So:

```text
1 3 2
```

comes before:

```text
2 1 3
```

---

## 12.3 Generate All Permutations

Important:

```text
start sorted
```

Why?

Because sorted order is the smallest lexicographic permutation.

Then repeatedly call:

```cpp
next_permutation(...)
```

---

## 12.4 ASCII Flow for `[1,2,3]`

```text
123
 ↓
132
 ↓
213
 ↓
231
 ↓
312
 ↓
321
```

Then no larger permutation remains.

---

## 12.5 C++17

```cpp
vector<int> a = {1, 2, 3};

do {
    for (int x : a) {
        cout << x << ' ';
    }

    cout << '\n';

} while (next_permutation(a.begin(), a.end()));
```

---

## 12.6 Recognition Signal

```text
enumerate permutations
next lexicographic arrangement
try all arrangements for small n
```

Think:

```text
next_permutation
```

---

# 13. Random / mt19937

## 13.1 Why Random in CP?

Useful for:

```text
stress testing
random test generation
shuffle
randomized algorithms
```

---

## 13.2 Why mt19937?

Prefer:

```cpp
mt19937
```

over old `rand()` because it provides a much better random engine for CP use.

---

## 13.3 Setup

```cpp
mt19937 rng(
    chrono::steady_clock::now()
        .time_since_epoch()
        .count()
);
```

---

## 13.4 Random Integer in [L,R]

```cpp
int x =
    uniform_int_distribution<int>(L, R)(rng);
```

Visual:

```text
L -------------------------- R
        random x
            ^
```

---

## 13.5 Shuffle

```cpp
shuffle(a.begin(), a.end(), rng);
```

Example:

```text
before:
1 2 3 4 5

possible after:
3 1 5 2 4
```

---

## 13.6 Stress Testing Pattern

Very useful for CP learning.

```text
random test
    |
    v
run brute solution
    |
    v
run optimized solution
    |
    v
compare answers
```

If different:

```text
print failing testcase
```

---

## 13.7 Random Generator

```cpp
mt19937 rng(
    chrono::steady_clock::now()
        .time_since_epoch()
        .count()
);

int rnd(int l, int r) {
    return uniform_int_distribution<int>(l, r)(rng);
}
```

Example:

```cpp
int n = rnd(1, 10);
```

---

## 13.8 Recognition Signal

```text
generate many tests
compare brute vs optimized
shuffle input
randomized solution
```

Think:

```text
mt19937
```

---

# 14. STL Recognition Cheat Sheet

| Problem Signal | Think |
|---|---|
| dynamic array / indexing | `vector` |
| latest element first | `stack` |
| oldest element first / BFS | `queue` |
| both ends / sliding window | `deque` |
| repeatedly min/max | `priority_queue` |
| unique + sorted | `set` |
| unique + fast lookup | `unordered_set` |
| sorted + duplicates | `multiset` |
| key → value / frequency | `map`, `unordered_map` |
| rank / k-th smallest | PBDS indexed set |
| arrange by order | `sort` |
| first `>= x` | `lower_bound` |
| first `> x` | `upper_bound` |
| permutation enumeration | `next_permutation` |
| stress testing / shuffle | `mt19937` |

---

# 15. Complexity Quick Sheet

| STL Tool | Typical Complexity |
|---|---|
| `vector[i]` | `O(1)` |
| `vector::push_back` | amortized `O(1)` |
| `stack/queue/deque` end operations | `O(1)` |
| `priority_queue::push/pop` | `O(log n)` |
| `set/map/multiset` | `O(log n)` |
| `unordered_set/map` | average `O(1)` |
| PBDS insert/erase/rank | `O(log n)` |
| `sort` | `O(n log n)` |
| `lower_bound/upper_bound` on vector | `O(log n)` |
| `next_permutation` | `O(n)` |

---

# 16. Final Mental Model

Do not memorize STL as isolated syntax.

Choose the tool from the operation you need.

```text
PROBLEM
   |
   v
What do I need repeatedly?
   |
   +--> index access ----------> vector
   |
   +--> latest first ----------> stack
   |
   +--> oldest first ----------> queue
   |
   +--> both ends -------------> deque
   |
   +--> min/max repeatedly ----> priority_queue
   |
   +--> unique ----------------> set / unordered_set
   |
   +--> sorted duplicates -----> multiset
   |
   +--> key -> information ----> map / unordered_map
   |
   +--> rank / kth ------------> PBDS
   |
   +--> ordering --------------> sort
   |
   +--> first valid position --> lower_bound / upper_bound
   |
   +--> permutations ----------> next_permutation
   |
   +--> random testing --------> mt19937
```

## Final Revision Rule

For each STL tool, remember only:

```text
1. What ordering does it maintain?
2. Are duplicates allowed?
3. What can I access quickly?
4. Insert / erase / search complexity?
5. What problem signal tells me to use it?
```

That is enough to recognize the correct STL tool quickly in contests.
