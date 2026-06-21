# MiniSTL_Master.md

## Complete STL Pattern Engine for CP, Codeforces, and FAANG Interviews

> **Scope:** Master STL from basics to Codeforces/FAANG pattern usage.  
> **Sources merged conceptually:** AZ STL notes, STL Engine chapters, and STL CFFT forms/tactics.  
> **Language:** C++17 only.  
> **Goal:** Recognize which STL structure solves which problem form, implement quickly, and debug WA/TLE/runtime bugs.

---

# Clickable Index

- [000. How To Use This File](#000-how-to-use-this-file)
- [001. STL Core Mental Model](#001-stl-core-mental-model)
- [002. STL Decision Engine](#002-stl-decision-engine)
- [003. Vector — Dynamic Array Engine](#003-vector--dynamic-array-engine)
- [004. String — Character Buffer Engine](#004-string--character-buffer-engine)
- [005. Pair, Tuple, Struct — Record Engine](#005-pair-tuple-struct--record-engine)
- [006. Stack — Last-In Parser Engine](#006-stack--last-in-parser-engine)
- [007. Queue — First-In Event Engine](#007-queue--first-in-event-engine)
- [008. Deque — Two-End Window Engine](#008-deque--two-end-window-engine)
- [009. Priority Queue — Best-First Engine](#009-priority-queue--best-first-engine)
- [010. Set and Multiset — Ordered Index Engine](#010-set-and-multiset--ordered-index-engine)
- [011. Map and Unordered Map — Key Index Engine](#011-map-and-unordered-map--key-index-engine)
- [012. Iterator Basics](#012-iterator-basics)
- [013. Sort and Custom Comparator](#013-sort-and-custom-comparator)
- [014. Binary Search STL](#014-binary-search-stl)
- [015. Frequency Map Pattern](#015-frequency-map-pattern)
- [016. Prefix/Frequency Hybrid](#016-prefixfrequency-hybrid)
- [017. Two Pointers With STL](#017-two-pointers-with-stl)
- [018. Sliding Window With STL](#018-sliding-window-with-stl)
- [019. Monotonic Stack — Boundary Engine](#019-monotonic-stack--boundary-engine)
- [020. Monotonic Deque — Window Min/Max Engine](#020-monotonic-deque--window-minmax-engine)
- [021. Range Mapping and Interval Engine](#021-range-mapping-and-interval-engine)
- [022. Sweep Line Event Engine](#022-sweep-line-event-engine)
- [023. Top-K and Kth Pattern](#023-top-k-and-kth-pattern)
- [024. Median Maintenance](#024-median-maintenance)
- [025. Codeforces A/B/C/D Forms](#025-codeforces-abcd-forms)
- [026. LeetCode / FAANG Forms](#026-leetcode--faang-forms)
- [027. Common WA/TLE/Runtime Bugs](#027-common-watleruntime-bugs)
- [028. Debugging Checklist](#028-debugging-checklist)
- [099. STL Cheat Sheet](#099-stl-cheat-sheet)

---

# 000. How To Use This File

STL is not just syntax. STL is a set of **ready-made machines**.

```text
Problem requirement
      ↓
Needed operation
      ↓
Required complexity
      ↓
Correct STL container / algorithm
      ↓
Pattern template
```

Read this file in three passes:

```text
Pass 1:
    Learn what each STL structure is good for.

Pass 2:
    Type the templates and dry run them.

Pass 3:
    Solve problems by operation:
        need frequency? map/unordered_map
        need best element repeatedly? priority_queue
        need ordered nearest? set
        need window max? deque
        need next greater? stack
```

---

# 001. STL Core Mental Model

## Why STL Exists

Competitive programming gives you limited time. STL saves you from rebuilding common data structures.

```text
Without STL:
    implement dynamic arrays, heaps, maps, sets, sorting

With STL:
    focus on pattern and logic
```

## Core Mental Model

Every STL container is a machine optimized for certain operations.

```text
vector          -> index access + append
string          -> character buffer
stack           -> last-in-first-out
queue           -> first-in-first-out
deque           -> push/pop both ends
priority_queue  -> repeatedly get best element
set/map         -> ordered search/index
unordered_map   -> fast average key lookup
multiset        -> ordered duplicates
```

## ASCII Map

```text
                  STL ENGINE
                      |
   ------------------------------------------------
   |            |              |                  |
Sequence     Last/First     Ordered Index      Hash Index
   |            |              |                  |
vector       stack          set/map            unordered_map
string       queue          multiset           unordered_set
deque        priority_queue lower_bound        frequency
```

## Recognition Rule

Do not ask:

```text
Which STL do I know?
```

Ask:

```text
What operation do I need fast?
```

---

# 002. STL Decision Engine

## Master Decision Tree

```text
Need random access by index?
    -> vector / string

Need insert/delete only at end?
    -> vector

Need push/pop from both ends?
    -> deque

Need LIFO?
    -> stack

Need FIFO?
    -> queue

Need current max/min repeatedly?
    -> priority_queue

Need ordered unique values?
    -> set

Need ordered duplicates?
    -> multiset

Need key -> value sorted?
    -> map

Need key -> value fastest average?
    -> unordered_map

Need binary search in sorted array?
    -> vector + lower_bound

Need next greater/smaller?
    -> monotonic stack

Need window max/min?
    -> monotonic deque
```

## Operation → STL Table

```text
Operation                          STL
-------------------------------------------------
append values                      vector
sort values                        sort(vector)
count frequency                    unordered_map / map
ordered nearest >= x               set.lower_bound
k largest                          min-heap / priority_queue
current smallest                   min-heap
undo last open bracket             stack
BFS traversal                      queue
sliding window max                 deque
merge intervals                    vector<pair> + sort
events over line                   vector<pair> + sort
median of stream/window            multiset / two heaps
```

## Core Complexity Table

```text
vector push_back           amortized O(1)
vector index               O(1)
vector erase middle        O(N)
sort                       O(N log N)
lower_bound vector         O(log N)
set/map operations         O(log N)
unordered_map operations   average O(1)
priority_queue push/pop    O(log N)
queue/stack push/pop       O(1)
deque push_front/back      O(1)
```

---

# 003. Vector — Dynamic Array Engine

## Why This Exists

`vector` is the most used STL container. It stores items contiguously and supports fast index access.

## Core Mental Model

```text
vector = resizable array

index:  0   1   2   3
       [10][20][30][40]
```

## When To Use

```text
store input array
sort values
prefix sums
DP arrays
graph adjacency lists
binary search after sorting
```

## When NOT To Use

```text
frequent erase from front
frequent insert in middle
need ordered lookup while modifying
```

## C++17 Template

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
Input:
5
4 1 7 2 2

vector after read:
[4,1,7,2,2]

after sort:
[1,2,2,4,7]
```

## Common Pattern: Prefix Sum

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

## Complexity

```text
index access      O(1)
push_back         amortized O(1)
sort              O(N log N)
erase front       O(N)
```

## WA/TLE Bugs

```text
using erase(v.begin()) in loop -> O(N^2)
out-of-bounds index
forgetting long long in prefix sums
not sorting before binary_search/lower_bound
```

---

# 004. String — Character Buffer Engine

## Core Mental Model

```text
string s = "abca"

index: 0 1 2 3
char:  a b c a
```

## Use Cases

```text
frequency counting
palindrome
substring
two pointers
stack parsing
hashing
```

## C++17 Frequency Example

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

    int l = 0, r = (int)s.size() - 1;

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

## Bugs

```text
s[i] out of bounds
mixing getline after cin without consuming newline
substring copies can cost O(length)
```

---

# 005. Pair, Tuple, Struct — Record Engine

## Why This Exists

Many problems need grouped data.

```text
(value, index)
(start, end)
(distance, node)
(x, y)
```

## Pair Sorting

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

## Dry Run

```text
Default pair sorting:
sort by first
if first equal, sort by second

(1,50)
(3,5)
(3,10)
```

## Struct With Comparator

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Student {
    int score;
    int age;
    string name;
};

int main() {
    vector<Student> v = {
        {90, 20, "Ali"},
        {90, 18, "Bob"},
        {95, 19, "Cara"}
    };

    sort(v.begin(), v.end(), [](const Student& a, const Student& b) {
        if (a.score != b.score) return a.score > b.score;
        return a.age < b.age;
    });

    for (auto &s : v) {
        cout << s.name << " " << s.score << " " << s.age << "\n";
    }

    return 0;
}
```

---

# 006. Stack — Last-In Parser Engine

## Core Mental Model

```text
push: add on top
pop: remove from top

bottom [ 1 2 3 ] top
```

## Pattern Recognition

```text
parentheses matching
undo last state
next greater/smaller
monotonic boundary
parser-like nesting
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
read ']' -> matches [, pop -> stack: (
read ')' -> matches (, pop -> empty
valid
```

## Bugs

```text
calling top() on empty stack
forgetting to check stack empty at end
wrong matching order
```

---

# 007. Queue — First-In Event Engine

## Core Mental Model

```text
front -> [A][B][C] <- back

pop from front
push at back
```

## Pattern Recognition

```text
BFS
level-order traversal
first come first served
simulation
multi-source expansion
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

---

# 008. Deque — Two-End Window Engine

## Core Mental Model

```text
push_front  -> [ ][ ][ ] <- push_back
pop_front   -> [ ][ ][ ] <- pop_back
```

## Use Cases

```text
sliding window max/min
0-1 BFS
window simulation
```

## Basic Deque

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

---

# 009. Priority Queue — Best-First Engine

## Core Mental Model

```text
priority_queue = heap

top() always gives highest priority
```

Default is max-heap.

## Pattern Recognition

```text
top K
kth largest
Dijkstra
merge k sorted lists
scheduling
always choose max/min
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

Output:

```text
10 5 1
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
array = [7,2,9,1,5], k=3

push 7 -> [7]
push 2 -> [7,2]
push 9 -> [9,2,7]
push 1 -> [9,2,7,1], pop 9 -> [7,2,1]
push 5 -> [7,5,1,2], pop 7 -> [5,2,1]

answer: 1 2 5
```

---

# 010. Set and Multiset — Ordered Index Engine

## Core Mental Model

```text
set = sorted unique values
multiset = sorted values with duplicates
```

## Pattern Recognition

```text
need nearest greater/equal
need sorted dynamic collection
need erase specific value
need duplicates ordered -> multiset
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

## multiset Erase One Copy

```cpp
auto it = ms.find(x);
if (it != ms.end()) ms.erase(it);
```

Not:

```cpp
ms.erase(x); // removes all copies
```

## Bugs

```text
lower_bound(s.begin(), s.end(), x) on set is O(N)
use s.lower_bound(x) for O(logN)
erasing all duplicates accidentally
decrementing begin()
```

---

# 011. Map and Unordered Map — Key Index Engine

## map vs unordered_map

```text
map           ordered keys, O(logN)
unordered_map hash table, average O(1)
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

    for (auto &[key, count] : freq) {
        cout << key << " " << count << "\n";
    }

    return 0;
}
```

## Bugs

```text
unordered_map can hack/TLE in adversarial cases
operator[] creates key if missing
map is slower but ordered
```

---

# 012. Iterator Basics

## Core Mental Model

```text
begin points to first element
end points one past last element

[ a b c d )
^       ^
begin   end
```

## Example

```cpp
vector<int> a = {1,2,3,4};

auto it = a.begin();
cout << *it; // 1
```

## Iterator Distance

```cpp
int idx = lower_bound(a.begin(), a.end(), x) - a.begin();
```

Works for vector/string, not for set iterator subtraction.

## Bugs

```text
dereferencing end()
invalid iterator after erase
using iterator subtraction on set/map
```

---

# 013. Sort and Custom Comparator

## Core Mental Model

```text
unsorted chaos -> sorted structure
```

Sorting enables:

```text
greedy
binary search
two pointers
interval merging
sweep line
```

## Custom Comparator

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<pair<int,int>> v = {
        {3, 10},
        {1, 50},
        {3, 5}
    };

    sort(v.begin(), v.end(), [](auto &a, auto &b) {
        if (a.first != b.first) return a.first < b.first;
        return a.second > b.second;
    });

    for (auto [x,y] : v) cout << x << " " << y << "\n";
    return 0;
}
```

## Comparator Bug

Comparator must define strict weak ordering.

Bad:

```cpp
return a <= b;
```

Good:

```cpp
return a < b;
```

---

# 014. Binary Search STL

## Core Mental Model

```text
lower_bound = first element >= x
upper_bound = first element > x
```

## ASCII

```text
a = [1,2,2,2,4,7]
x = 2

lower_bound -> first 2 at index 1
upper_bound -> first >2 at index 4
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {1,2,2,2,4,7};

    int x;
    cin >> x;

    int lb = lower_bound(a.begin(), a.end(), x) - a.begin();
    int ub = upper_bound(a.begin(), a.end(), x) - a.begin();

    cout << lb << " " << ub << "\n";
    cout << "count = " << ub - lb << "\n";

    return 0;
}
```

## Bugs

```text
array must be sorted
lower_bound on set using std::lower_bound is O(N)
index may equal n
```

---

# 015. Frequency Map Pattern

## Core Mental Model

```text
value -> count
```

## Count Equal Pairs

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
a = [5,5,2,5]

x=5 freq[5]=0 pairs=0, freq[5]=1
x=5 freq[5]=1 pairs=1, freq[5]=2
x=2 freq[2]=0 pairs=1, freq[2]=1
x=5 freq[5]=2 pairs=3, freq[5]=3
```

---

# 016. Prefix/Frequency Hybrid

## Core Mental Model

```text
sum(l..r) = prefix[r] - prefix[l-1]
```

If:

```text
prefix[r] - prefix[l-1] = k
```

Then:

```text
prefix[l-1] = prefix[r] - k
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

start freq[0]=1, pref=0

i=0 x=1 pref=1 need=-2 ans=0 freq[1]=1
i=1 x=2 pref=3 need=0  ans=1 freq[3]=1
i=2 x=1 pref=4 need=1  ans=2 freq[4]=1
i=3 x=2 pref=6 need=3  ans=3 freq[6]=1
```

---

# 017. Two Pointers With STL

## Core Mental Model

```text
left moves right
right moves left/right
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

    int l = 0, r = n - 1;

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
}
```

## ASCII

```text
[1,2,4,7,9,11]
 ^           ^
 l           r
```

---

# 018. Sliding Window With STL

## Core Mental Model

```text
expand right
shrink left
```

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
}
```

## Dry Run

```text
s = "abca"

r=0 a -> window a    best=1
r=1 b -> window ab   best=2
r=2 c -> window abc  best=3
r=3 a -> duplicate a
remove s[l]=a -> window bca best=3
```

---

# 019. Monotonic Stack — Boundary Engine

## Core Mental Model

Keep stack ordered.

```text
For next greater:
stack stores unresolved decreasing elements.
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

    stack<int> st;

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

## Dry Run

```text
a=[2,1,5,3]

i=0 stack=[0]
i=1 a[1]=1, 2<1 no, stack=[0,1]
i=2 a[2]=5, 1<5 pop -> ans[1]=5
             2<5 pop -> ans[0]=5
             stack=[2]
i=3 a[3]=3, 5<3 no, stack=[2,3]
```

## Complexity

```text
Each element pushed once, popped once -> O(N)
```

---

# 020. Monotonic Deque — Window Min/Max Engine

## Core Mental Model

Deque stores useful candidates only.

For max:

```text
values in deque are decreasing
front is max
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

## Complexity

```text
Each index enters and leaves deque once -> O(N)
```

---

# 021. Range Mapping and Interval Engine

## Core Mental Model

```text
interval = [start, end]
sort by start
merge overlaps
```

## ASCII

```text
[1,4]
   [2,6]
          [8,10]

merge first two -> [1,6]
```

## Merge Intervals

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<pair<int,int>> intervals(n);
    for (auto &[l, r] : intervals) cin >> l >> r;

    sort(intervals.begin(), intervals.end());

    vector<pair<int,int>> merged;

    for (auto [l, r] : intervals) {
        if (merged.empty() || merged.back().second < l) {
            merged.push_back({l, r});
        } else {
            merged.back().second = max(merged.back().second, r);
        }
    }

    for (auto [l, r] : merged) cout << l << " " << r << "\n";
    return 0;
}
```

---

# 022. Sweep Line Event Engine

## Core Mental Model

```text
start event +1
end event   -1
sort events
scan active count
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

    int active = 0, best = 0;

    for (auto [x, delta] : events) {
        active += delta;
        best = max(best, active);
    }

    cout << best << "\n";
    return 0;
}
```

## Edge Note

If intervals are inclusive `[l,r]`, often use `r+1` as end event.

---

# 023. Top-K and Kth Pattern

## Core Mental Model

Keep only the best K candidates.

```text
stream values
    ↓
heap of size K
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

    cout << pq.top() << "\n";
    return 0;
}
```

## Complexity

```text
O(N log K)
```

---

# 024. Median Maintenance

## Core Mental Model

```text
lower half max-heap
upper half min-heap
```

ASCII:

```text
lower: [small values] max on top
upper: [large values] min on top

max(lower) <= min(upper)
sizes differ by at most 1
```

## C++17

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

---

# 025. Codeforces A/B/C/D Forms

## Div2 A

```text
vector
string
sort
set for uniqueness
map frequency
```

Forms:

```text
count characters
min/max
sort and compare
simple set size
```

## Div2 B

```text
map/unordered_map
sort + greedy
priority_queue simple
two pointers
```

## Div2 C

```text
prefix + map
lower_bound
multiset
monotonic stack
deque
```

## Div2 D

```text
priority_queue with custom data
set/multiset advanced
sweep line
range mapping
graph adjacency vector
```

---

# 026. LeetCode / FAANG Forms

```text
Two Sum                         unordered_map
Group Anagrams                  unordered_map<string, vector<string>>
Top K Frequent Elements          unordered_map + priority_queue
Merge Intervals                  vector<pair> + sort
Meeting Rooms                    sort / priority_queue
Sliding Window Maximum           deque
Valid Parentheses                stack
Daily Temperatures               monotonic stack
Kth Largest                      priority_queue
Median Finder                    two heaps
LRU-like ordering                list + unordered_map
```

## Interview Explanation Template

```text
I need operation X fast.
This STL gives operation X in O(...).
The algorithm visits each element ...
So final complexity is ...
```

---

# 027. Common WA/TLE/Runtime Bugs

## WA Bugs

```text
not sorting before lower_bound
wrong comparator
erasing all duplicates from multiset
using max-heap when min-heap needed
off-by-one in windows
forgetting original indices after sorting
```

## TLE Bugs

```text
vector erase front in loop
sort inside loop
std::lower_bound on set iterators
map when unordered_map is enough
O(N²) nested find/count
```

## Runtime Bugs

```text
top/front/back on empty container
iterator invalid after erase
out-of-bounds vector index
decrement begin()
dereference end()
```

## Overflow Bugs

```text
sum in int
comparator subtracting ints: return a-b < 0
priority key overflow
```

---

# 028. Debugging Checklist

Before submit:

```text
[ ] Did I choose STL based on needed operation?
[ ] Is the container empty before top/front/back?
[ ] Is the array sorted before binary_search/lower_bound?
[ ] Am I using member lower_bound for set/map?
[ ] Does comparator use strict weak ordering?
[ ] Did I handle duplicates correctly?
[ ] Did I erase one multiset copy or all copies?
[ ] Is complexity acceptable for max N?
[ ] Do I need long long?
[ ] Did I preserve original index if required?
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
```

## Final One Picture To Remember

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

STL is not memory work.

STL is operation selection.
