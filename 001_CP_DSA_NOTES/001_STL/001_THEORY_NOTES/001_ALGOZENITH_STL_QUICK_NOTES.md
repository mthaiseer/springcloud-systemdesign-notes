# AlgoZenith — STL Quick Notes

> **Goal:** Fast revision for Competitive Programming.  
> For each topic: **what it is → key methods → tiny example → code pattern → one practice problem**.

## Table of Contents

1. [Vector](#1-vector)
2. [Stack](#2-stack)
3. [Queue](#3-queue)
4. [Deque](#4-deque)
5. [Priority Queue](#5-priority-queue)
6. [Set / Unordered Set](#6-set--unordered-set)
7. [Multiset](#7-multiset)
8. [Map / Unordered Map](#8-map--unordered-map)
9. [Indexed Set (PBDS)](#9-indexed-set-pbds)
10. [STL Sorting](#10-stl-sorting)
11. [Lower Bound / Upper Bound](#11-lower-bound--upper-bound)
12. [Next Permutation](#12-next-permutation)
13. [Random](#13-random)

---

## 1. Vector

### Short Theory
Dynamic array with **O(1)** random access. Use when size changes and you need indexing/traversal.

```cpp
vector<int> a = {5, 2, 8};
```

### Key Methods

| Method | Meaning | Tiny Example |
|---|---|---|
| `push_back(x)` | add at end | `a.push_back(7);` |
| `pop_back()` | remove last | `a.pop_back();` |
| `size()` | number of items | `a.size()` |
| `front()` | first item | `a.front()` |
| `back()` | last item | `a.back()` |
| `empty()` | is empty? | `a.empty()` |
| `clear()` | remove all | `a.clear()` |
| `begin(), end()` | iterator range | `sort(a.begin(),a.end());` |
| `erase(it)` | erase element | `a.erase(a.begin()+1);` |

### Code Pattern
```cpp
vector<int> a;
for (int x : {4, 1, 7}) a.push_back(x);

sort(a.begin(), a.end());

for (int x : a)
    cout << x << ' ';
```

### Practice — Remove Duplicates from Sorted Array
Problem: LeetCode 26 — Remove Duplicates from Sorted Array

**Idea:** keep unique values in the front of the vector.

```cpp
int removeDuplicates(vector<int>& a) {
    if (a.empty()) return 0;

    int k = 1;
    for (int i = 1; i < (int)a.size(); i++)
        if (a[i] != a[i - 1])
            a[k++] = a[i];

    return k;
}
```

**Recognition:** dynamic array + indexing + traversal → `vector`.

---

## 2. Stack

### Short Theory
**LIFO** — Last In, First Out. Useful for matching, undo, monotonic stack, expression processing.

### Key Methods

| Method | Meaning | Tiny Example |
|---|---|---|
| `push(x)` | add on top | `st.push(5);` |
| `pop()` | remove top | `st.pop();` |
| `top()` | read top | `st.top()` |
| `size()` | element count | `st.size()` |
| `empty()` | is empty? | `st.empty()` |

### Code Pattern
```cpp
stack<int> st;
st.push(10);
st.push(20);

cout << st.top(); // 20
st.pop();
```

### Practice — Valid Parentheses
Problem: LeetCode 20 — Valid Parentheses

```cpp
bool isValid(string s) {
    stack<char> st;

    for (char c : s) {
        if (c == '(' || c == '[' || c == '{')
            st.push(c);
        else {
            if (st.empty()) return false;

            char x = st.top();
            st.pop();

            if ((c == ')' && x != '(') ||
                (c == ']' && x != '[') ||
                (c == '}' && x != '{'))
                return false;
        }
    }
    return st.empty();
}
```

**Recognition:** latest unmatched element must be checked first → `stack`.

---

## 3. Queue

### Short Theory
**FIFO** — First In, First Out. Common in BFS and processing events in arrival order.

### Key Methods

| Method | Meaning | Tiny Example |
|---|---|---|
| `push(x)` | add at back | `q.push(5);` |
| `pop()` | remove front | `q.pop();` |
| `front()` | first item | `q.front()` |
| `back()` | last item | `q.back()` |
| `size()` | element count | `q.size()` |
| `empty()` | is empty? | `q.empty()` |

### Code Pattern
```cpp
queue<int> q;
q.push(10);
q.push(20);

cout << q.front(); // 10
q.pop();
```

### Practice — Number of Recent Calls
Problem: LeetCode 933 — Number of Recent Calls

```cpp
class RecentCounter {
    queue<int> q;

public:
    int ping(int t) {
        q.push(t);

        while (!q.empty() && q.front() < t - 3000)
            q.pop();

        return q.size();
    }
};
```

**Recognition:** remove oldest elements first → `queue`.

---

## 4. Deque

### Short Theory
**Double-ended queue**. Insert/remove efficiently from **both front and back**.

### Key Methods

| Method | Meaning | Tiny Example |
|---|---|---|
| `push_back(x)` | add back | `dq.push_back(5);` |
| `push_front(x)` | add front | `dq.push_front(2);` |
| `pop_back()` | remove back | `dq.pop_back();` |
| `pop_front()` | remove front | `dq.pop_front();` |
| `front()` | first item | `dq.front()` |
| `back()` | last item | `dq.back()` |
| `size()` | count | `dq.size()` |

### Code Pattern
```cpp
deque<int> dq;

dq.push_back(2);   // [2]
dq.push_front(1);  // [1,2]
dq.push_back(3);   // [1,2,3]
```

### Practice — Sliding Window Maximum
Problem: LeetCode 239 — Sliding Window Maximum

```cpp
vector<int> maxSlidingWindow(vector<int>& a, int k) {
    deque<int> dq;
    vector<int> ans;

    for (int i = 0; i < (int)a.size(); i++) {
        while (!dq.empty() && dq.front() <= i - k)
            dq.pop_front();

        while (!dq.empty() && a[dq.back()] <= a[i])
            dq.pop_back();

        dq.push_back(i);

        if (i >= k - 1)
            ans.push_back(a[dq.front()]);
    }
    return ans;
}
```

**Recognition:** window + remove from front + maintain candidates at back → `deque`.

---

## 5. Priority Queue

### Short Theory
Returns the highest-priority element first. Default C++ `priority_queue` is a **max heap**.

### Key Methods

| Method | Meaning | Tiny Example |
|---|---|---|
| `push(x)` | insert | `pq.push(7);` |
| `pop()` | remove top | `pq.pop();` |
| `top()` | max/min | `pq.top()` |
| `size()` | count | `pq.size()` |
| `empty()` | is empty? | `pq.empty()` |

### Max / Min Heap
```cpp
priority_queue<int> mx;

priority_queue<int, vector<int>, greater<int>> mn;
```

### Practice — Kth Largest Element
Problem: LeetCode 215 — Kth Largest Element in an Array

```cpp
int findKthLargest(vector<int>& a, int k) {
    priority_queue<int> pq(a.begin(), a.end());

    while (--k)
        pq.pop();

    return pq.top();
}
```

**Recognition:** repeatedly need current smallest/largest → `priority_queue`.

---

## 6. Set / Unordered Set

### Short Theory

```text
set           = unique + sorted
unordered_set = unique + hash table, no ordering
```

### Complexity

| Container | Search/Insert/Erase | Ordered? |
|---|---:|---|
| `set` | `O(log n)` | Yes |
| `unordered_set` | avg. `O(1)` | No |

### Key Methods

| Method | Meaning | Tiny Example |
|---|---|---|
| `insert(x)` | add | `s.insert(5);` |
| `erase(x)` | remove | `s.erase(5);` |
| `find(x)` | iterator to x | `s.find(5)` |
| `count(x)` | exists? | `s.count(5)` |
| `size()` | count | `s.size()` |
| `empty()` | is empty? | `s.empty()` |

### Code Pattern
```cpp
set<int> s = {4, 1, 4, 2};

for (int x : s)
    cout << x << ' '; // 1 2 4
```

### Practice — Contains Duplicate
Problem: LeetCode 217 — Contains Duplicate

```cpp
bool containsDuplicate(vector<int>& a) {
    unordered_set<int> seen;

    for (int x : a) {
        if (seen.count(x))
            return true;
        seen.insert(x);
    }
    return false;
}
```

**Recognition:** uniqueness / fast existence check → `set` or `unordered_set`.

---

## 7. Multiset

### Short Theory
Like `set`, but **duplicates are allowed** and values remain sorted.

```cpp
multiset<int> ms = {2, 2, 5};
```

### Key Methods

| Method | Meaning | Tiny Example |
|---|---|---|
| `insert(x)` | add one copy | `ms.insert(2);` |
| `count(x)` | number of copies | `ms.count(2)` |
| `find(x)` | one occurrence | `ms.find(2)` |
| `erase(x)` | erase all copies | `ms.erase(2);` |
| `erase(it)` | erase one copy | `ms.erase(ms.find(2));` |
| `begin()` | smallest | `*ms.begin()` |
| `rbegin()` | largest | `*ms.rbegin()` |

### Important
```cpp
ms.erase(5);          // removes ALL 5s

auto it = ms.find(5);
if (it != ms.end())
    ms.erase(it);     // removes ONE 5
```

### Practice — Maintain Current Minimum
Given values arriving one by one, support insertion, deletion of one copy, and print minimum.

```cpp
multiset<int> ms;

ms.insert(5);
ms.insert(2);
ms.insert(2);

cout << *ms.begin(); // 2

auto it = ms.find(2);
if (it != ms.end())
    ms.erase(it);

cout << *ms.begin(); // 2
```

**Recognition:** sorted values + duplicates + deletion → `multiset`.

---

## 8. Map / Unordered Map

### Short Theory

Stores:

```text
key -> value
```

Common CP use: **frequency counting**.

| Container | Operations | Ordered? |
|---|---:|---|
| `map` | `O(log n)` | Yes |
| `unordered_map` | avg. `O(1)` | No |

### Key Methods

| Method | Meaning | Tiny Example |
|---|---|---|
| `mp[key]` | access/create | `mp[5]++;` |
| `find(key)` | search | `mp.find(5)` |
| `count(key)` | key exists? | `mp.count(5)` |
| `erase(key)` | delete key | `mp.erase(5);` |
| `size()` | keys count | `mp.size()` |

### Code Pattern
```cpp
unordered_map<int,int> freq;

for (int x : {2, 3, 2, 5})
    freq[x]++;

cout << freq[2]; // 2
```

### Practice — Two Sum
Problem: LeetCode 1 — Two Sum

```cpp
vector<int> twoSum(vector<int>& a, int target) {
    unordered_map<int,int> pos;

    for (int i = 0; i < (int)a.size(); i++) {
        int need = target - a[i];

        if (pos.count(need))
            return {pos[need], i};

        pos[a[i]] = i;
    }
    return {};
}
```

**Recognition:** value → frequency/index/information → `map`.

---

## 9. Indexed Set (PBDS)

### Short Theory
Ordered set with extra operations:

```text
find k-th smallest
count elements < x
```

Useful in advanced CP.

### Setup
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

### Key Methods

| Method | Meaning | Example |
|---|---|---|
| `insert(x)` | insert | `s.insert(10);` |
| `erase(x)` | erase | `s.erase(10);` |
| `find_by_order(k)` | k-th, 0-indexed | `*s.find_by_order(2)` |
| `order_of_key(x)` | count `< x` | `s.order_of_key(10)` |

### Code Pattern
```cpp
indexed_set<int> s;

s.insert(10);
s.insert(30);
s.insert(20);

cout << *s.find_by_order(1); // 20
cout << s.order_of_key(25);  // 2
```

### Practice — Count Smaller Elements
For each query `x`, count stored values strictly smaller than `x`.

```cpp
indexed_set<int> s;

for (int x : {10, 30, 20})
    s.insert(x);

cout << s.order_of_key(25); // 2
```

**Recognition:** ordered set + rank / k-th element → PBDS indexed set.

> For duplicates, store pairs such as `{value, unique_id}`.

---

## 10. STL Sorting

### Short Theory
`sort()` sorts a random-access range in **O(n log n)**.

### Common Forms

| Goal | Code |
|---|---|
| ascending | `sort(a.begin(), a.end());` |
| descending | `sort(a.rbegin(), a.rend());` |
| array | `sort(a, a+n);` |
| custom comparator | `sort(v.begin(),v.end(),cmp);` |

### Comparator
```cpp
sort(v.begin(), v.end(),
     [](auto &a, auto &b) {
         return a.second < b.second;
     });
```

### Practice — Sort People by Score
```cpp
vector<pair<string,int>> a = {
    {"A", 70},
    {"B", 90},
    {"C", 80}
};

sort(a.begin(), a.end(),
     [](const auto& x, const auto& y) {
         return x.second > y.second;
     });
```

Result:

```text
B 90
C 80
A 70
```

**Recognition:** ordering makes the problem easier → sort first.

---

## 11. Lower Bound / Upper Bound

### Short Theory
Works on a **sorted range**.

```text
lower_bound(x) = first position >= x
upper_bound(x) = first position >  x
```

### Example
```text
a = [1,2,2,2,5]
     0 1 2 3 4

lower_bound(2) -> index 1
upper_bound(2) -> index 4
```

### Code
```cpp
vector<int> a = {1, 2, 2, 2, 5};

int L = lower_bound(a.begin(), a.end(), 2) - a.begin();
int R = upper_bound(a.begin(), a.end(), 2) - a.begin();

cout << L << ' ' << R; // 1 4
```

### Count Occurrences
```cpp
int count =
    upper_bound(a.begin(), a.end(), 2) -
    lower_bound(a.begin(), a.end(), 2);
```

### Practice — Search Insert Position
Problem: LeetCode 35 — Search Insert Position

```cpp
int searchInsert(vector<int>& a, int target) {
    return lower_bound(a.begin(), a.end(), target) - a.begin();
}
```

**Recognition:** sorted data + first valid position → `lower_bound`.

---

## 12. Next Permutation

### Short Theory
Transforms a sequence into the **next lexicographically larger permutation**.

```cpp
next_permutation(a.begin(), a.end());
```

### Example
```text
[1,2,3]
   ↓
[1,3,2]
   ↓
[2,1,3]
```

### Generate All Permutations
Start sorted:

```cpp
vector<int> a = {1, 2, 3};

do {
    for (int x : a)
        cout << x << ' ';
    cout << '\n';
} while (next_permutation(a.begin(), a.end()));
```

### Practice — Next Permutation
Problem: LeetCode 31 — Next Permutation

Using STL:

```cpp
void nextPermutation(vector<int>& nums) {
    next_permutation(nums.begin(), nums.end());
}
```

**Recognition:** enumerate/reach lexicographic arrangements → permutation tools.

---

## 13. Random

### Short Theory
For CP, prefer `mt19937` over `rand()`.

Common uses:

```text
stress testing
random test generation
shuffle
randomized algorithms
```

### Setup
```cpp
mt19937 rng(
    chrono::steady_clock::now().time_since_epoch().count()
);
```

### Common Operations

| Goal | Code |
|---|---|
| random engine value | `rng()` |
| integer `[L,R]` | `uniform_int_distribution<int>(L,R)(rng)` |
| shuffle vector | `shuffle(a.begin(),a.end(),rng);` |

### Code Pattern
```cpp
mt19937 rng(
    chrono::steady_clock::now().time_since_epoch().count()
);

int L = 1, R = 100;

int x = uniform_int_distribution<int>(L, R)(rng);
```

### Practice — Random Stress Test Generator
```cpp
mt19937 rng(
    chrono::steady_clock::now().time_since_epoch().count()
);

int rnd(int l, int r) {
    return uniform_int_distribution<int>(l, r)(rng);
}

int main() {
    int n = rnd(1, 10);

    cout << n << '\n';

    for (int i = 0; i < n; i++)
        cout << rnd(1, 100) << ' ';
}
```

**Recognition:** generate many random cases and compare brute vs optimized solution.

---

# STL Recognition Cheat Sheet

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
| rank / k-th smallest | indexed set / PBDS |
| arrange by order | `sort` |
| first `>= x` | `lower_bound` |
| first `> x` | `upper_bound` |
| permutation enumeration | `next_permutation` |
| stress tests / shuffle | `mt19937` |

---

# Complexity Quick Sheet

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

## Revision Rule

For each STL container remember only:

```text
1. What ordering does it maintain?
2. Are duplicates allowed?
3. What can I access quickly?
4. Insert / erase / search complexity?
5. What problem signal tells me to use it?
```
