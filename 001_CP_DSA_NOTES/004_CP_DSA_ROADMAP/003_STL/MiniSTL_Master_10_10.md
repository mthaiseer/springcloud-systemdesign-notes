# MiniSTL_Master_10_10.md

## Complete STL Pattern Engine for CP, Codeforces CM, and FAANG Interviews

> **Goal:** Master C++17 STL not as syntax, but as a **problem-solving decision engine**.
>
> This file expands STL from basics to Codeforces Div2 A/B/C/D patterns, Candidate Master preparation, and FAANG-style interview designs such as LRU Cache and LFU Cache.
>
> **Core promise:** Given a problem, you should quickly recognize the needed operation, choose the correct STL container/algorithm, implement safely, dry run confidently, and debug WA/TLE/runtime issues.

---

# Clickable Index

- [000. How To Use This File](#000-how-to-use-this-file)
- [001. STL Core Mental Model](#001-stl-core-mental-model)
- [002. STL Decision Engine](#002-stl-decision-engine)
- [003. Complexity Table](#003-complexity-table)
- [004. Vector](#004-vector)
- [005. String](#005-string)
- [006. Pair Tuple Struct](#006-pair-tuple-struct)
- [007. Stack](#007-stack)
- [008. Queue](#008-queue)
- [009. Deque](#009-deque)
- [010. Priority Queue](#010-priority-queue)
- [011. Set and Multiset](#011-set-and-multiset)
- [012. Map and Unordered Map](#012-map-and-unordered-map)
- [013. Iterators](#013-iterators)
- [014. Sort and Custom Comparator](#014-sort-and-custom-comparator)
- [015. Binary Search STL](#015-binary-search-stl)
- [016. Frequency Map Pattern](#016-frequency-map-pattern)
- [017. Prefix + Map Pattern](#017-prefix--map-pattern)
- [018. Two Pointers With STL](#018-two-pointers-with-stl)
- [019. Sliding Window With STL](#019-sliding-window-with-stl)
- [020. Monotonic Stack](#020-monotonic-stack)
- [021. Monotonic Deque](#021-monotonic-deque)
- [022. Priority Queue Advanced: Lazy Deletion](#022-priority-queue-advanced-lazy-deletion)
- [023. Multiset Advanced Patterns](#023-multiset-advanced-patterns)
- [024. Range Mapping and Interval Merge](#024-range-mapping-and-interval-merge)
- [025. Sweep Line](#025-sweep-line)
- [026. Top-K and Kth Element](#026-top-k-and-kth-element)
- [027. Median Maintenance](#027-median-maintenance)
- [028. Sliding Window Median](#028-sliding-window-median)
- [029. STL Design Pattern: LRU Cache](#029-stl-design-pattern-lru-cache)
- [030. STL Design Pattern: LFU Cache](#030-stl-design-pattern-lfu-cache)
- [031. Codeforces A/B/C/D Forms](#031-codeforces-abcd-forms)
- [032. FAANG STL Forms](#032-faang-stl-forms)
- [033. Common WA/TLE/Runtime/Overflow Bugs](#033-common-watleruntimeoverflow-bugs)
- [034. Debugging Checklist](#034-debugging-checklist)
- [099. STL Cheat Sheet](#099-stl-cheat-sheet)

---

# 000. How To Use This File

STL is not “containers to memorize.” STL is a **library of operation machines**.

```text
Problem requirement
      ↓
Needed operation
      ↓
Required complexity
      ↓
Correct STL machine
      ↓
Pattern template
      ↓
AC
```

Use this file in 4 passes:

```text
Pass 1:
    Learn each STL machine and its operation model.

Pass 2:
    Type every template manually.

Pass 3:
    Solve problems by pattern:
        frequency
        ordered search
        monotonic boundary
        top-k
        interval merge
        sweep line
        sliding window max/min
        median
        cache design

Pass 4:
    During contests/interviews:
        ask "what operation must be fast?"
```

---

# 001. STL Core Mental Model

## Why STL Exists

In contests and interviews, you should not spend time implementing heaps, balanced trees, hash tables, dynamic arrays, and sorting from scratch.

STL gives you reliable machines:

```text
vector          dynamic array
string          character array
stack           last-in-first-out
queue           first-in-first-out
deque           two-ended queue
priority_queue  heap
set/map         balanced tree
unordered_map   hash table
algorithm       sort/search/reverse/count
```

## Single Core Mental Model

> STL choice is operation choice.

```text
Need index access?
    vector

Need sorted dynamic data?
    set / multiset / map

Need average O(1) lookup?
    unordered_map / unordered_set

Need best element repeatedly?
    priority_queue

Need next greater/smaller boundary?
    stack

Need window max/min?
    deque

Need cache order?
    list + unordered_map
```

## ASCII Big Picture

```text
                         STL ENGINE
                              |
       -------------------------------------------------
       |             |              |                  |
   Sequence       Order/Tree       Hash              Heap
       |             |              |                  |
 vector/string   set/map       unordered_map      priority_queue
 deque           multiset      unordered_set      top-k/dijkstra
       |
 stack/queue for flow control
```

## Contest Intuition

A beginner thinks:

```text
"I know vector and map."
```

A strong solver thinks:

```text
"I need nearest >= x dynamically, so I need set.lower_bound."
"I need top k, so I need heap of size k."
"I need count of previous prefix, so I need unordered_map."
"I need max in sliding window, so I need monotonic deque."
```

---

# 002. STL Decision Engine

## Master Decision Tree

```text
Need random access by index?
    -> vector / string

Need append many values and sort later?
    -> vector + sort

Need erase from front repeatedly?
    -> queue / deque, NOT vector

Need LIFO?
    -> stack

Need FIFO?
    -> queue

Need push/pop both ends?
    -> deque

Need current max/min repeatedly?
    -> priority_queue

Need ordered unique values?
    -> set

Need ordered duplicates?
    -> multiset

Need key-value sorted by key?
    -> map

Need key-value average O(1)?
    -> unordered_map

Need first >= x in sorted array?
    -> lower_bound on vector

Need first >= x dynamically?
    -> set.lower_bound / map.lower_bound

Need next greater/smaller?
    -> monotonic stack

Need window max/min?
    -> monotonic deque

Need LRU cache?
    -> list + unordered_map

Need LFU cache?
    -> unordered_map + frequency lists
```

## Operation Table

```text
Operation                                      Best STL
------------------------------------------------------------------
Store input and sort                          vector
Frequency count                               unordered_map / map
Sorted unique values                          set
Sorted duplicates                             multiset
K largest/smallest                            priority_queue
First greater/equal in static sorted data      lower_bound(vector)
First greater/equal in dynamic data            set.lower_bound
Undo last open bracket                         stack
BFS                                             queue
0-1 BFS / window max                           deque
Next greater element                           monotonic stack
Sliding window maximum                         monotonic deque
Merge intervals                                vector<pair> + sort
Overlap count                                  sweep line events
Median stream                                  two heaps / multiset
Window median                                  multiset / two multisets
LRU cache                                      list + unordered_map
LFU cache                                      unordered_map + list buckets
```

---

# 003. Complexity Table

## Core STL Complexity

```text
vector push_back                 amortized O(1)
vector pop_back                  O(1)
vector index                     O(1)
vector insert/erase middle       O(N)
vector erase front               O(N)

string index                     O(1)
string push_back                 amortized O(1)
string substr                    O(length)

sort                             O(N log N)
reverse                          O(N)
lower_bound vector               O(log N)
binary_search vector             O(log N)

stack push/pop/top               O(1)
queue push/pop/front             O(1)
deque push_front/back            O(1)
deque pop_front/back             O(1)

priority_queue push/pop          O(log N)
priority_queue top               O(1)

set/map insert/find/erase        O(log N)
set/map lower_bound              O(log N)

unordered_map insert/find/erase  average O(1), worst O(N)
```

## TLE Intuition

```text
N <= 2e5:
    vector + sort OK
    map/set O(NlogN) OK
    unordered_map O(N) average OK
    vector erase front in loop NOT OK
    sort inside loop NOT OK
```

---

# 004. Vector

## Why This Exists

`vector` is a resizable contiguous array. It is the default container for arrays, sorting, prefix sums, DP, graph adjacency, and binary search.

## Core Mental Model

```text
index:  0   1   2   3
       [10][20][30][40]
```

Fast:

```text
a[i]
push_back
sort
iterate
```

Slow:

```text
insert middle
erase middle/front
```

## Pattern Recognition

Use vector when:

```text
input array
need sorting
need binary search after sorting
need prefix sums
need DP table
need adjacency list
```

Avoid vector when:

```text
frequent deletion from front
need dynamic sorted insertion/search
need O(1) deletion from middle
```

## Basic Template

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<int> a(n);

    for (int i = 0; i < n; i++) {
        cin >> a[i];
    }

    sort(a.begin(), a.end());

    for (int x : a) {
        cout << x << " ";
    }

    cout << "\n";
    return 0;
}
```

## Dry Run

```text
input: 5
4 1 7 2 2

read:
[4,1,7,2,2]

sort:
[1,2,2,4,7]
```

## Prefix Sum Example

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, q;
    cin >> n >> q;

    vector<long long> pref(n + 1, 0);

    for (int i = 0; i < n; i++) {
        long long x;
        cin >> x;
        pref[i + 1] = pref[i] + x;
    }

    while (q--) {
        int l, r;
        cin >> l >> r; // 0-indexed inclusive

        cout << pref[r + 1] - pref[l] << "\n";
    }

    return 0;
}
```

## Variable Table

```text
a = [3,1,4]

pref[0] = 0
pref[1] = 3
pref[2] = 4
pref[3] = 8

sum(1..2)=pref[3]-pref[1]=8-3=5
```

## Complexity

```text
read O(N)
sort O(N log N)
prefix build O(N)
range query O(1)
```

## Bugs

```text
out-of-bounds index
using int for large sums
erase(v.begin()) repeatedly -> O(N^2)
forgetting 0-index vs 1-index query conversion
```

---

# 005. String

## Core Mental Model

`string` is a vector of characters.

```text
s = "abca"

index: 0 1 2 3
char:  a b c a
```

## Pattern Recognition

Use string with:

```text
frequency
palindrome
substring
stack parsing
two pointers
sliding window
hashing
```

## Frequency Template

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    string s;
    cin >> s;

    vector<int> freq(26, 0);

    for (char c : s) {
        freq[c - 'a']++;
    }

    for (int i = 0; i < 26; i++) {
        if (freq[i] > 0) {
            cout << char('a' + i) << " " << freq[i] << "\n";
        }
    }

    return 0;
}
```

## Palindrome Template

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    string s;
    cin >> s;

    int l = 0;
    int r = (int)s.size() - 1;

    while (l < r) {
        if (s[l] != s[r]) {
            cout << "NO\n";
            return 0;
        }
        l++;
        r--;
    }

    cout << "YES\n";
    return 0;
}
```

## Dry Run

```text
s = "abca"

freq:
a -> 2
b -> 1
c -> 1

palindrome check:
a == a ok
b != c fail
```

## Bugs

```text
getline after cin needs consuming newline
substr creates copy O(length)
char indexing assumes lowercase only
s[i] out of bounds
```

---

# 006. Pair Tuple Struct

## Core Mental Model

Use small records to bind related data.

```text
(value, index)
(start, end)
(distance, node)
(x, y)
```

## Default Pair Sorting

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<pair<int,int>> v = {
        {3, 10},
        {1, 50},
        {3, 5}
    };

    sort(v.begin(), v.end());

    for (auto [a, b] : v) {
        cout << a << " " << b << "\n";
    }

    return 0;
}
```

Output:

```text
1 50
3 5
3 10
```

Because pair sorts by first, then second.

## Struct Comparator

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Item {
    int score;
    int age;
    string name;
};

int main() {
    vector<Item> v = {
        {90, 20, "Ali"},
        {90, 18, "Bob"},
        {95, 19, "Cara"}
    };

    sort(v.begin(), v.end(), [](const Item& a, const Item& b) {
        if (a.score != b.score) return a.score > b.score;
        return a.age < b.age;
    });

    for (auto &x : v) {
        cout << x.name << " " << x.score << " " << x.age << "\n";
    }

    return 0;
}
```

## Pattern Recognition

```text
intervals
events
weighted graph edges
sort by multiple keys
preserve original index
coordinate points
```

## Bugs

```text
comparator must be strict
do not return <=
avoid subtracting in comparator because overflow
```

---

# 007. Stack

## Core Mental Model

Last in, first out.

```text
push 1
push 2
push 3

top -> 3

stack:
bottom [1 2 3] top
```

## Pattern Recognition

Use stack for:

```text
valid parentheses
parser nesting
undo last event
next greater/smaller
monotonic stack
DFS iterative
```

## Valid Parentheses

```cpp
#include <bits/stdc++.h>
using namespace std;

bool match(char open, char close) {
    return (open == '(' && close == ')') ||
           (open == '[' && close == ']') ||
           (open == '{' && close == '}');
}

int main() {
    string s;
    cin >> s;

    stack<char> st;

    for (char c : s) {
        if (c == '(' || c == '[' || c == '{') {
            st.push(c);
        } else {
            if (st.empty() || !match(st.top(), c)) {
                cout << "NO\n";
                return 0;
            }
            st.pop();
        }
    }

    cout << (st.empty() ? "YES\n" : "NO\n");
    return 0;
}
```

## Dry Run

```text
s = "([])"

read '(' -> stack: (
read '[' -> stack: ( [
read ']' -> top '[' matches, pop -> (
read ')' -> top '(' matches, pop -> empty
valid
```

## Bugs

```text
top() on empty stack
forgetting stack must be empty at end
wrong close/open matching
```

---

# 008. Queue

## Core Mental Model

First in, first out.

```text
front -> [A][B][C] <- back
pop A first
```

## Pattern Recognition

```text
BFS
level order traversal
simulation
multi-source expansion
shortest path unweighted
```

## BFS Template

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, m;
    cin >> n >> m;

    vector<vector<int>> g(n);

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;
        --u; --v;

        g[u].push_back(v);
        g[v].push_back(u);
    }

    vector<int> dist(n, -1);
    queue<int> q;

    dist[0] = 0;
    q.push(0);

    while (!q.empty()) {
        int u = q.front();
        q.pop();

        for (int v : g[u]) {
            if (dist[v] == -1) {
                dist[v] = dist[u] + 1;
                q.push(v);
            }
        }
    }

    for (int d : dist) cout << d << " ";
    return 0;
}
```

## ASCII BFS

```text
0
| \
1  2
|
3

queue:
[0]
pop 0 -> push 1,2
[1,2]
pop 1 -> push 3
[2,3]
```

---

# 009. Deque

## Core Mental Model

Deque supports both ends.

```text
push_front -> [ ][ ][ ] <- push_back
pop_front  -> [ ][ ][ ] <- pop_back
```

## Pattern Recognition

```text
sliding window max/min
0-1 BFS
two-end simulation
maintain candidates
```

## Basic Template

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    deque<int> dq;

    dq.push_back(10);
    dq.push_front(5);
    dq.push_back(20);

    cout << dq.front() << " " << dq.back() << "\n";

    dq.pop_front();

    cout << dq.front() << "\n";
    return 0;
}
```

## Complexity

```text
push_front/back O(1)
pop_front/back  O(1)
front/back      O(1)
```

---

# 010. Priority Queue

## Core Mental Model

Priority queue gives the best element repeatedly.

Default:

```text
max-heap
```

```text
push values: 5,1,10
top -> 10
```

## Pattern Recognition

```text
top K
kth largest
Dijkstra
merge k sorted lists
scheduling
always choose current best
```

## Max Heap

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    priority_queue<int> pq;

    pq.push(5);
    pq.push(1);
    pq.push(10);

    while (!pq.empty()) {
        cout << pq.top() << " ";
        pq.pop();
    }

    return 0;
}
```

## Min Heap

```cpp
priority_queue<int, vector<int>, greater<int>> pq;
```

## Top K Smallest

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, k;
    cin >> n >> k;

    priority_queue<int> pq;

    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;

        pq.push(x);

        if ((int)pq.size() > k) {
            pq.pop();
        }
    }

    vector<int> ans;

    while (!pq.empty()) {
        ans.push_back(pq.top());
        pq.pop();
    }

    sort(ans.begin(), ans.end());

    for (int x : ans) cout << x << " ";
    return 0;
}
```

## Dry Run

```text
a=[7,2,9,1,5], k=3

push 7 -> [7]
push 2 -> [7,2]
push 9 -> [9,2,7]
push 1 -> [9,2,7,1], pop 9 -> [7,2,1]
push 5 -> [7,5,1,2], pop 7 -> [5,2,1]

answer: 1 2 5
```

## Bugs

```text
default is max-heap
top() on empty heap
custom comparator reverses intuition
```

---

# 011. Set and Multiset

## Core Mental Model

```text
set      = ordered unique values
multiset = ordered values with duplicates
```

## Pattern Recognition

Use set/multiset when:

```text
dynamic ordered collection
nearest >= x
nearest <= x
insert/delete/search all online
duplicates matter
```

## set lower_bound

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    set<int> s = {1, 4, 7, 10};

    int x;
    cin >> x;

    auto it = s.lower_bound(x);

    if (it == s.end()) cout << "none\n";
    else cout << *it << "\n";

    return 0;
}
```

## Dry Run

```text
s={1,4,7,10}
x=5

lower_bound(5) -> 7
```

## Previous Element

```cpp
auto it = s.lower_bound(x);

if (it != s.begin()) {
    --it;
    cout << *it << "\n";
}
```

## multiset Erase One Copy

```cpp
auto it = ms.find(x);
if (it != ms.end()) {
    ms.erase(it);
}
```

## Important Bug

```cpp
ms.erase(x);
```

removes **all copies** of x.

## Complexity

```text
insert O(logN)
erase O(logN)
find O(logN)
lower_bound O(logN)
```

---

# 012. Map and Unordered Map

## Core Mental Model

```text
key -> value
```

## map vs unordered_map

```text
map:
    ordered by key
    O(logN)

unordered_map:
    hash table
    average O(1)
    no order
```

## Frequency Count

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    unordered_map<int,int> freq;

    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;
        freq[x]++;
    }

    for (auto &[key, value] : freq) {
        cout << key << " " << value << "\n";
    }

    return 0;
}
```

## Dry Run

```text
a=[4,1,4,2]

freq[4]=2
freq[1]=1
freq[2]=1
```

## Important Note

```cpp
freq[x]
```

creates `x` with value `0` if missing.

## When To Use map

```text
need sorted output
need lower_bound by key
need ordered traversal
```

## Bugs

```text
unordered_map may be hacked/TLE
operator[] creates missing key
map slower than unordered_map
```

---

# 013. Iterators

## Core Mental Model

Iterator is a pointer-like object into a container.

```text
[ a b c d )
^       ^
begin   end
```

`end()` points one past the last element.

## Vector Iterator Index

```cpp
vector<int> a = {1,2,4,7};

auto it = lower_bound(a.begin(), a.end(), 4);
int idx = it - a.begin();
```

Works for vector because vector has random-access iterators.

## Set Iterator

```cpp
set<int> s = {1,2,4,7};
auto it = s.lower_bound(4);
```

Do not do:

```cpp
int idx = it - s.begin(); // invalid
```

## Iterator Invalidation

```text
vector push_back may invalidate iterators if reallocation happens
vector erase invalidates iterators after erased position
set/map erase invalidates only erased iterator
```

## Bugs

```text
dereferencing end()
decrementing begin()
using erased iterator
subtracting set/map iterators
```

---

# 014. Sort and Custom Comparator

## Core Mental Model

Sorting creates order. Order enables:

```text
binary search
two pointers
greedy
interval merging
sweep line
duplicate grouping
```

## Basic Sort

```cpp
sort(a.begin(), a.end());
```

## Descending

```cpp
sort(a.rbegin(), a.rend());
```

## Custom Comparator

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<pair<int,int>> v = {
        {3,10},
        {1,50},
        {3,5}
    };

    sort(v.begin(), v.end(), [](auto &a, auto &b) {
        if (a.first != b.first) return a.first < b.first;
        return a.second > b.second;
    });

    for (auto [x,y] : v) {
        cout << x << " " << y << "\n";
    }

    return 0;
}
```

## Dry Run

```text
sort by first ascending
tie: second descending

(1,50)
(3,10)
(3,5)
```

## Strict Weak Ordering

Bad:

```cpp
return a <= b;
```

Good:

```cpp
return a < b;
```

## Comparator Bugs

```text
using <= instead of <
non-transitive comparator
overflow by return a-b < 0
capturing wrong variables
```

---

# 015. Binary Search STL

## Core Mental Model

On sorted range:

```text
lower_bound = first element >= x
upper_bound = first element > x
```

## ASCII

```text
a = [1,2,2,2,4,7]
x = 2

index: 0 1 2 3 4 5
value: 1 2 2 2 4 7
         ^     ^
         LB    UB
```

## Count Occurrences

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {1,2,2,2,4,7};

    int x;
    cin >> x;

    auto l = lower_bound(a.begin(), a.end(), x);
    auto r = upper_bound(a.begin(), a.end(), x);

    cout << r - l << "\n";
    return 0;
}
```

## First >= x Index

```cpp
int idx = lower_bound(a.begin(), a.end(), x) - a.begin();
if (idx == (int)a.size()) {
    // no such element
}
```

## Bugs

```text
array not sorted
idx == n not handled
using std::lower_bound on set -> O(N)
```

---

# 016. Frequency Map Pattern

## Problem Form

Count equal pairs.

## Brute Force

```text
for all i,j -> O(N^2)
```

## STL Pattern

```text
value -> previous count
```

When seeing `x`, it forms `freq[x]` new pairs.

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    unordered_map<int,long long> freq;
    long long pairs = 0;

    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;

        pairs += freq[x];
        freq[x]++;
    }

    cout << pairs << "\n";
    return 0;
}
```

## Dry Run

```text
a=[5,5,2,5]

x=5 freq[5]=0 pairs=0
x=5 freq[5]=1 pairs=1
x=2 freq[2]=0 pairs=1
x=5 freq[5]=2 pairs=3
```

## CF Forms

```text
count pairs with same value
count pairs with same remainder
count anagrams
frequency balance
```

## FAANG Forms

```text
Two Sum
Group Anagrams
First Unique Character
Top K Frequent
```

---

# 017. Prefix + Map Pattern

## Core Mental Model

For subarray sums:

```text
sum(l..r) = pref[r] - pref[l-1]
```

If:

```text
sum(l..r) = k
```

Then:

```text
pref[l-1] = pref[r] - k
```

## Count Subarrays With Sum K

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    long long k;
    cin >> n >> k;

    unordered_map<long long,long long> freq;
    freq[0] = 1;

    long long pref = 0;
    long long ans = 0;

    for (int i = 0; i < n; i++) {
        long long x;
        cin >> x;

        pref += x;
        ans += freq[pref - k];
        freq[pref]++;
    }

    cout << ans << "\n";
    return 0;
}
```

## Dry Run

```text
a=[1,2,1,2], k=3

freq[0]=1, pref=0

i=0 x=1 pref=1 need=-2 ans=0 freq[1]=1
i=1 x=2 pref=3 need=0  ans=1 freq[3]=1
i=2 x=1 pref=4 need=1  ans=2 freq[4]=1
i=3 x=2 pref=6 need=3  ans=3 freq[6]=1
```

## Pattern Recognition

```text
subarray sum equals k
subarray divisible by k
negative numbers exist
count previous prefix
```

## Bugs

```text
forget freq[0]=1
using int prefix
using sliding window when negatives exist
```

---

# 018. Two Pointers With STL

## Core Mental Model

Two pointers work when movement is monotonic.

```text
l moves right
r moves left/right
each moves at most N times
```

## Pair Sum Sorted

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, target;
    cin >> n >> target;

    vector<int> a(n);
    for (int &x : a) cin >> x;

    sort(a.begin(), a.end());

    int l = 0;
    int r = n - 1;

    while (l < r) {
        int sum = a[l] + a[r];

        if (sum == target) {
            cout << "YES\n";
            return 0;
        }

        if (sum < target) l++;
        else r--;
    }

    cout << "NO\n";
    return 0;
}
```

## ASCII

```text
[1,2,4,7,9,11]
 ^           ^
 l           r

sum too small -> move l right
sum too big   -> move r left
```

## Complexity

```text
sort O(NlogN)
scan O(N)
```

---

# 019. Sliding Window With STL

## Core Mental Model

Window is a contiguous segment.

```text
[ a0 a1 a2 a3 a4 ]
      L----R
```

Each element enters once and leaves once.

## Longest Substring Without Repeating Characters

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    string s;
    cin >> s;

    vector<int> cnt(256, 0);
    int l = 0;
    int best = 0;

    for (int r = 0; r < (int)s.size(); r++) {
        cnt[s[r]]++;

        while (cnt[s[r]] > 1) {
            cnt[s[l]]--;
            l++;
        }

        best = max(best, r - l + 1);
    }

    cout << best << "\n";
    return 0;
}
```

## Dry Run

```text
s="abca"

r=0 a window=a best=1
r=1 b window=ab best=2
r=2 c window=abc best=3
r=3 a duplicate
remove old a -> window=bca best=3
```

## Bugs

```text
sliding sum with negative numbers may fail
forgot to shrink until valid
wrong window length r-l+1
```

---

# 020. Monotonic Stack

## Core Mental Model

Monotonic stack keeps candidates in order to find nearest boundary.

For next greater:

```text
stack stores indices whose next greater is not found yet
values are decreasing
```

## ASCII

```text
a=[2,1,5,3]

read 2 -> [2]
read 1 -> [2,1]
read 5 -> pop 1, pop 2, next greater = 5
read 3 -> [5,3]
```

## Next Greater Element

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<int> a(n), ans(n, -1);
    for (int &x : a) cin >> x;

    stack<int> st; // indices

    for (int i = 0; i < n; i++) {
        while (!st.empty() && a[st.top()] < a[i]) {
            ans[st.top()] = a[i];
            st.pop();
        }

        st.push(i);
    }

    for (int x : ans) cout << x << " ";
    return 0;
}
```

## Dry Run Table

```text
a=[2,1,5,3]

i=0 value=2 stack=[]      push 0
i=1 value=1 stack=[0]     2<1 no, push 1
i=2 value=5 stack=[0,1]   1<5 pop ans[1]=5
                           2<5 pop ans[0]=5
                           push 2
i=3 value=3 stack=[2]     5<3 no, push 3

ans=[5,5,-1,-1]
```

## Sub-Patterns

```text
next greater right
next smaller right
previous greater left
previous smaller left
largest rectangle in histogram
sum of subarray minimums
daily temperatures
```

## Complexity

```text
Each element pushed once, popped once -> O(N)
```

---

# 021. Monotonic Deque

## Core Mental Model

For window maximum, deque stores useful candidates in decreasing order.

```text
front = max of current window
back = weakest candidate
```

## ASCII

```text
window values: [1,3,-1]
deque stores indices of decreasing values:
[3, -1]
front = 3
```

## Sliding Window Maximum

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, k;
    cin >> n >> k;

    vector<int> a(n);
    for (int &x : a) cin >> x;

    deque<int> dq;

    for (int i = 0; i < n; i++) {
        while (!dq.empty() && dq.front() <= i - k) {
            dq.pop_front();
        }

        while (!dq.empty() && a[dq.back()] <= a[i]) {
            dq.pop_back();
        }

        dq.push_back(i);

        if (i >= k - 1) {
            cout << a[dq.front()] << " ";
        }
    }

    return 0;
}
```

## Dry Run

```text
a=[1,3,-1,-3,5], k=3

i=0 add 1    dq=[1]
i=1 add 3    pop 1, dq=[3]
i=2 add -1   dq=[3,-1], output 3
i=3 add -3   dq=[3,-1,-3], output 3
i=4 add 5    remove old 3, pop -3,-1, dq=[5], output 5
```

## Bugs

```text
store indices, not values
remove expired indices first
use <= or < depending duplicate behavior
```

---

# 022. Priority Queue Advanced: Lazy Deletion

## Why This Exists

`priority_queue` cannot remove arbitrary elements. Lazy deletion delays removal until the invalid element reaches top.

## Core Mental Model

```text
heap may contain stale elements
validity checked when top is used
```

## ASCII

```text
heap top -> maybe stale
if stale:
    pop and discard
else:
    use it
```

## Example: Max Heap With Deleted Values

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    priority_queue<int> pq;
    unordered_map<int,int> deleted;

    pq.push(5);
    pq.push(10);
    pq.push(7);

    deleted[10]++; // pretend 10 was deleted elsewhere

    while (!pq.empty() && deleted[pq.top()] > 0) {
        deleted[pq.top()]--;
        pq.pop();
    }

    if (!pq.empty()) cout << pq.top() << "\n"; // 7

    return 0;
}
```

## Use Cases

```text
sliding window heap
Dijkstra old distance entries
online max after deletions
frequency heap
```

## Dijkstra Lazy Deletion

```cpp
if (d != dist[u]) continue;
```

This skips stale heap entries.

## Bugs

```text
not cleaning top before use
deleted count goes negative
memory grows with stale elements
```

---

# 023. Multiset Advanced Patterns

## Why This Exists

`multiset` supports sorted duplicates and arbitrary erase in O(logN).

## Pattern Recognition

Use multiset when:

```text
need sorted dynamic window
need median
need min/max with deletion
duplicates exist
need erase one copy
```

## Window Min/Max With Multiset

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, k;
    cin >> n >> k;

    vector<int> a(n);
    for (int &x : a) cin >> x;

    multiset<int> ms;

    for (int i = 0; i < n; i++) {
        ms.insert(a[i]);

        if (i >= k) {
            auto it = ms.find(a[i - k]);
            ms.erase(it);
        }

        if (i >= k - 1) {
            cout << *ms.begin() << " " << *ms.rbegin() << "\n";
        }
    }

    return 0;
}
```

## Dry Run

```text
a=[2,1,5,3], k=2

i=0 ms={2}
i=1 ms={1,2} output min=1 max=2
i=2 insert 5 remove 2 -> {1,5} output 1 5
i=3 insert 3 remove 1 -> {3,5} output 3 5
```

## Bugs

```text
erase(value) removes all copies
erase(find(value)) removes one copy
find can return end if logic wrong
rbegin on empty multiset
```

---

# 024. Range Mapping and Interval Merge

## Core Mental Model

Sort intervals by start, then merge overlapping ranges.

```text
[1,4]
  [2,6]
          [8,10]

merge -> [1,6], [8,10]
```

## Merge Intervals

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<pair<int,int>> intervals(n);

    for (auto &[l, r] : intervals) {
        cin >> l >> r;
    }

    sort(intervals.begin(), intervals.end());

    vector<pair<int,int>> merged;

    for (auto [l, r] : intervals) {
        if (merged.empty() || merged.back().second < l) {
            merged.push_back({l, r});
        } else {
            merged.back().second = max(merged.back().second, r);
        }
    }

    for (auto [l, r] : merged) {
        cout << l << " " << r << "\n";
    }

    return 0;
}
```

## Dry Run

```text
[1,4], [2,6], [8,10]

merged empty -> add [1,4]
[2,6] overlaps because 4 >= 2 -> merge to [1,6]
[8,10] no overlap -> add
```

## Bugs

```text
inclusive vs exclusive endpoints
not sorting first
wrong overlap condition
```

---

# 025. Sweep Line

## Core Mental Model

Convert intervals into events.

```text
start -> +1
end   -> -1
```

Scan events in sorted order.

## ASCII

```text
Interval A: [1,4]
Interval B:   [2,6]

events:
1 +1
2 +1
4 -1
6 -1

active count:
1,2,1,0
max overlap = 2
```

## Max Overlap

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<pair<int,int>> events;

    for (int i = 0; i < n; i++) {
        int l, r;
        cin >> l >> r;

        events.push_back({l, +1});
        events.push_back({r, -1});
    }

    sort(events.begin(), events.end());

    int active = 0;
    int best = 0;

    for (auto [x, delta] : events) {
        active += delta;
        best = max(best, active);
    }

    cout << best << "\n";
    return 0;
}
```

## Inclusive Endpoint Variant

If intervals are `[l, r]` inclusive and you want end after r:

```cpp
events.push_back({l, +1});
events.push_back({r + 1, -1});
```

## Bugs

```text
tie-breaking start/end at same coordinate
inclusive vs exclusive intervals
r+1 overflow
```

---

# 026. Top-K and Kth Element

## Core Mental Model

Do not sort all if you only need K.

```text
Keep heap size K
```

## K Largest With Min Heap

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, k;
    cin >> n >> k;

    priority_queue<int, vector<int>, greater<int>> pq;

    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;

        pq.push(x);

        if ((int)pq.size() > k) {
            pq.pop();
        }
    }

    cout << pq.top() << "\n"; // kth largest
    return 0;
}
```

## Dry Run

```text
a=[3,1,5,2,4], k=2

keep 2 largest

3 -> [3]
1 -> [1,3]
5 -> [1,3,5], pop 1 -> [3,5]
2 -> [2,3,5], pop 2 -> [3,5]
4 -> [3,4,5], pop 3 -> [4,5]

top=4 = 2nd largest
```

## Complexity

```text
O(N log K)
```

---

# 027. Median Maintenance

## Core Mental Model

Maintain two halves.

```text
low  = max heap for smaller half
high = min heap for larger half

max(low) <= min(high)
size difference <= 1
```

## ASCII

```text
low:  [1,2,3] top=3
high: [4,5,6] top=4

median is near boundary
```

## Stream Median Lower Median

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    priority_queue<int> low;
    priority_queue<int, vector<int>, greater<int>> high;

    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;

        if (low.empty() || x <= low.top()) low.push(x);
        else high.push(x);

        if (low.size() > high.size() + 1) {
            high.push(low.top());
            low.pop();
        }

        if (high.size() > low.size()) {
            low.push(high.top());
            high.pop();
        }

        cout << low.top() << "\n";
    }

    return 0;
}
```

## Dry Run

```text
stream: 5,2,10

5 -> low=[5], high=[], median=5
2 -> low=[2], high=[5], median=2 lower median
10 -> low=[5,2], high=[10], median=5
```

## Bugs

```text
not rebalancing sizes
low.top > high.top invariant broken
empty heap top()
```

---

# 028. Sliding Window Median

## Why This Exists

Need median for every window with deletions. Heaps alone make deletion hard unless lazy deletion. Multiset/two multisets are simpler.

## Core Mental Model

```text
left multiset  = smaller half
right multiset = larger half

max(left) <= min(right)
left size >= right size
```

## ASCII

```text
window sorted: [1,2,5,7,9]

left:  [1,2,5]
right: [7,9]

median = max(left) = 5
```

## C++17 Lower Median

```cpp
#include <bits/stdc++.h>
using namespace std;

multiset<int> leftHalf, rightHalf;

void rebalance() {
    while (leftHalf.size() < rightHalf.size()) {
        leftHalf.insert(*rightHalf.begin());
        rightHalf.erase(rightHalf.begin());
    }

    while (leftHalf.size() > rightHalf.size() + 1) {
        auto it = prev(leftHalf.end());
        rightHalf.insert(*it);
        leftHalf.erase(it);
    }
}

void add(int x) {
    if (leftHalf.empty() || x <= *leftHalf.rbegin()) {
        leftHalf.insert(x);
    } else {
        rightHalf.insert(x);
    }
    rebalance();
}

void removeOne(int x) {
    auto it = leftHalf.find(x);
    if (it != leftHalf.end()) {
        leftHalf.erase(it);
    } else {
        it = rightHalf.find(x);
        if (it != rightHalf.end()) rightHalf.erase(it);
    }
    rebalance();
}

int main() {
    int n, k;
    cin >> n >> k;

    vector<int> a(n);
    for (int &x : a) cin >> x;

    for (int i = 0; i < n; i++) {
        add(a[i]);

        if (i >= k) {
            removeOne(a[i-k]);
        }

        if (i >= k - 1) {
            cout << *leftHalf.rbegin() << " ";
        }
    }

    return 0;
}
```

## Dry Run

```text
a=[1,5,2,7], k=3

window [1,5,2] sorted [1,2,5]
left=[1,2], right=[5], median=2

slide remove 1 add 7
window [5,2,7] sorted [2,5,7]
left=[2,5], right=[7], median=5
```

## Bugs

```text
erase(value) removes all duplicates
must rebalance after add/remove
leftHalf can be empty if k=0 invalid
```

---

# 029. STL Design Pattern: LRU Cache

## Why This Exists

LRU is a classic FAANG design using STL.

Requirement:

```text
get(key) O(1)
put(key,value) O(1)
evict least recently used when capacity exceeded
```

## Core Mental Model

Use:

```text
list<pair<key,value>> for usage order
unordered_map<key, iterator> for O(1) access
```

Most recent at front.

```text
front                         back
MRU  [k3] [k1] [k7] [k2]  LRU
```

## Operations

```text
get(k):
    find in map
    move node to front
    return value

put(k,v):
    if exists, update and move front
    else insert front
    if size > cap, remove back
```

## C++17 LRU Cache

```cpp
#include <bits/stdc++.h>
using namespace std;

class LRUCache {
    int cap;

    // Most recent at front, least recent at back
    list<pair<int,int>> order;

    // key -> iterator into list
    unordered_map<int, list<pair<int,int>>::iterator> pos;

public:
    LRUCache(int capacity) {
        cap = capacity;
    }

    int get(int key) {
        if (!pos.count(key)) return -1;

        auto it = pos[key];
        int value = it->second;

        // Move accessed node to front
        order.erase(it);
        order.push_front({key, value});
        pos[key] = order.begin();

        return value;
    }

    void put(int key, int value) {
        if (cap == 0) return;

        if (pos.count(key)) {
            auto it = pos[key];
            order.erase(it);
        }

        order.push_front({key, value});
        pos[key] = order.begin();

        if ((int)order.size() > cap) {
            auto [oldKey, oldValue] = order.back();
            order.pop_back();
            pos.erase(oldKey);
        }
    }
};

int main() {
    LRUCache cache(2);

    cache.put(1, 10);
    cache.put(2, 20);
    cout << cache.get(1) << "\n"; // 10, key 1 becomes most recent
    cache.put(3, 30);             // evicts key 2
    cout << cache.get(2) << "\n"; // -1

    return 0;
}
```

## Dry Run

```text
capacity=2

put(1,10)
order: [1]

put(2,20)
order: [2,1]

get(1)
move 1 front
order: [1,2]

put(3,30)
insert front -> [3,1,2]
evict back 2 -> [3,1]
```

## Complexity

```text
get O(1)
put O(1)
space O(capacity)
```

## Bugs

```text
list iterator invalid after erase
forget to update map iterator after moving
forget to erase evicted key from map
capacity 0 edge case
```

---

# 030. STL Design Pattern: LFU Cache

## Why This Exists

LFU evicts least frequently used item. If tie, evict least recently used among that frequency.

Requirement:

```text
get O(1)
put O(1)
evict least frequency, then oldest in that frequency
```

## Core Mental Model

Use:

```text
key -> {value, frequency}
frequency -> list of keys ordered by recency
key -> iterator into its frequency list
minFreq tracks current least frequency
```

ASCII:

```text
freq 1: [k7, k2]   oldest at back
freq 2: [k5]
freq 3: [k1]

minFreq = 1
evict back of freq 1
```

## C++17 LFU Cache

```cpp
#include <bits/stdc++.h>
using namespace std;

class LFUCache {
    struct Node {
        int value;
        int freq;
        list<int>::iterator it;
    };

    int cap;
    int minFreq;

    unordered_map<int, Node> data;
    unordered_map<int, list<int>> freqList;

    void touch(int key) {
        int f = data[key].freq;

        // Remove key from old frequency list
        freqList[f].erase(data[key].it);

        if (freqList[f].empty()) {
            freqList.erase(f);

            if (minFreq == f) {
                minFreq++;
            }
        }

        // Add to new frequency list front
        int nf = f + 1;
        freqList[nf].push_front(key);

        data[key].freq = nf;
        data[key].it = freqList[nf].begin();
    }

public:
    LFUCache(int capacity) {
        cap = capacity;
        minFreq = 0;
    }

    int get(int key) {
        if (!data.count(key)) return -1;

        int value = data[key].value;
        touch(key);
        return value;
    }

    void put(int key, int value) {
        if (cap == 0) return;

        if (data.count(key)) {
            data[key].value = value;
            touch(key);
            return;
        }

        if ((int)data.size() == cap) {
            // Evict least frequently used and least recently used in that freq
            int evictKey = freqList[minFreq].back();
            freqList[minFreq].pop_back();

            if (freqList[minFreq].empty()) {
                freqList.erase(minFreq);
            }

            data.erase(evictKey);
        }

        // Insert new key with freq 1
        freqList[1].push_front(key);
        data[key] = {value, 1, freqList[1].begin()};
        minFreq = 1;
    }
};

int main() {
    LFUCache cache(2);

    cache.put(1, 10); // freq 1
    cache.put(2, 20); // freq 1
    cout << cache.get(1) << "\n"; // key 1 freq becomes 2
    cache.put(3, 30);             // evicts key 2
    cout << cache.get(2) << "\n"; // -1
    cout << cache.get(3) << "\n"; // 30

    return 0;
}
```

## Dry Run

```text
capacity=2

put(1,10)
freq1: [1], minFreq=1

put(2,20)
freq1: [2,1], minFreq=1

get(1)
remove 1 from freq1 -> [2]
add 1 to freq2 -> [1]
minFreq=1

put(3,30)
capacity full
evict back of freq1 -> key 2
insert 3 to freq1 -> [3]
```

## Complexity

```text
get O(1)
put O(1)
space O(capacity)
```

## Bugs

```text
not updating minFreq
not erasing empty frequency list
forgetting tie-break by recency
invalid iterator after erase
```

---

# 031. Codeforces A/B/C/D Forms

## Div2 A

Typical STL:

```text
vector
string
sort
set
map frequency
```

Forms:

```text
count characters
unique count
sort and compare
min/max
simple frequency
```

Example mental mapping:

```text
"are all elements distinct?" -> set size
"sort and check condition"  -> vector + sort
```

## Div2 B

Typical STL:

```text
unordered_map
map
sort + greedy
priority_queue simple
two pointers
```

Forms:

```text
frequency decisions
pairing after sorting
choosing best order
remove duplicates
```

## Div2 C

Typical STL:

```text
prefix + unordered_map
lower_bound
multiset
monotonic stack
deque
```

Forms:

```text
subarray count
nearest boundary
window max/min
dynamic ordered data
```

## Div2 D

Typical STL:

```text
priority_queue advanced
set/multiset advanced
sweep line
range mapping
graph vector adjacency
lazy deletion
```

Forms:

```text
Dijkstra
event sorting
interval overlap
median/balance
topological patterns
```

## CM-Level Recognition

```text
If problem needs:
    nearest greater boundary -> monotonic stack
    window max/min -> deque
    dynamic nearest value -> set
    dynamic median -> multiset / two heaps
    repeated best candidate -> priority_queue
    interval overlap -> sweep line
```

---

# 032. FAANG STL Forms

## Mapping

```text
Two Sum                         unordered_map
Group Anagrams                  unordered_map<string, vector<string>>
Top K Frequent Elements          unordered_map + priority_queue / bucket
Merge Intervals                  vector<pair> + sort
Meeting Rooms                    sort / priority_queue
Sliding Window Maximum           deque
Valid Parentheses                stack
Daily Temperatures               monotonic stack
Kth Largest                      priority_queue
Median Finder                    two heaps
LRU Cache                        list + unordered_map
LFU Cache                        unordered_map + list buckets
Random Pick Weight               vector prefix + lower_bound
```

## Interview Explanation Template

```text
I need operation X.
Container Y provides X in O(...).
Each element is processed ...
Therefore time complexity is ...
Space complexity is ...
```

Example:

```text
For LRU, I need O(1) access by key and O(1) movement in usage order.
unordered_map gives O(1) key lookup.
list gives O(1) erase/insert when iterator is known.
Together get and put are O(1).
```

---

# 033. Common WA/TLE/Runtime/Overflow Bugs

## WA Bugs

```text
not sorting before binary search
wrong comparator
erasing all duplicates from multiset
using max-heap when min-heap needed
off-by-one in sliding window
not preserving original indices
wrong tie-breaking in sweep line
using unordered_map when output order matters
```

## TLE Bugs

```text
vector erase front in loop
sort inside loop
std::lower_bound on set/map iterators
nested find on vector
map when unordered_map enough and constants matter
priority_queue stale entries not removed
```

## Runtime Bugs

```text
top/front/back on empty container
dereferencing end()
decrementing begin()
iterator invalid after erase
out-of-bounds vector access
```

## Overflow Bugs

```text
sum in int
comparator subtracting values
multiplying int keys
distance with int coordinates
```

---

# 034. Debugging Checklist

Before submit:

```text
[ ] Did I choose STL by operation, not habit?
[ ] Is container empty before top/front/back?
[ ] Is data sorted before lower_bound/binary_search?
[ ] Did I use member lower_bound for set/map?
[ ] Does comparator use strict weak ordering?
[ ] Did I handle duplicates?
[ ] Did I erase one multiset copy or all copies?
[ ] Is the complexity acceptable for max N?
[ ] Do I need long long?
[ ] Did I preserve original index if needed?
[ ] Are stale heap entries cleaned?
[ ] Are iterators updated after list erase/insert?
[ ] Did I handle capacity 0 in LRU/LFU?
```

---

# 099. STL Cheat Sheet

## Container Choice

```text
Need index access              -> vector
Need characters                -> string
Need LIFO                      -> stack
Need FIFO                      -> queue
Need both ends                 -> deque
Need best element              -> priority_queue
Need sorted unique             -> set
Need sorted duplicates         -> multiset
Need key-value sorted          -> map
Need key-value fast average    -> unordered_map
Need O(1) reorder by iterator  -> list
```

## Pattern Choice

```text
frequency                      -> unordered_map/map
next greater/smaller           -> monotonic stack
window max/min                 -> monotonic deque
k largest/smallest             -> priority_queue
ordered nearest                -> set.lower_bound
merge intervals                -> sort + vector
overlap count                  -> sweep line
subarray sum k                 -> prefix + unordered_map
median                         -> multiset / two heaps
sliding window median          -> two multisets
LRU                            -> list + unordered_map
LFU                            -> unordered_map + frequency lists
```

## Complexity

```text
vector index                   O(1)
vector push_back               amortized O(1)
vector erase middle/front      O(N)
sort                           O(NlogN)
lower_bound vector             O(logN)
set/map operations             O(logN)
unordered_map average          O(1)
priority_queue push/pop        O(logN)
stack/queue push/pop           O(1)
deque push/pop ends            O(1)
list erase/insert by iterator  O(1)
```

## One Picture To Remember

```text
PROBLEM
   ↓
What operation must be fast?
   ↓
Choose STL machine
   ↓
Apply known pattern
   ↓
Check complexity + edge cases
   ↓
AC
```

STL is not memorization.

STL is **operation selection**.
