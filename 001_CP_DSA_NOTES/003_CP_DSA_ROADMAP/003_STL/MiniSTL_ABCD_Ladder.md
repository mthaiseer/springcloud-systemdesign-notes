# MiniSTL_ABCD_Ladder.md

## Genuine 10/10 STL Pattern Recognition Bible for Codeforces Candidate Master

> **Purpose:** This is not an STL syntax note. This is a **contest pattern recognition book**.  
> **Target:** Codeforces 800 → 2200, especially **Candidate Master**.  
> **Language:** C++17.  
> **Main Goal:** Read a problem and recognize the needed STL pattern in **5–10 seconds**.

---

# 000. Master Index

```text
MiniSTL_ABCD_Ladder.md
│
├── Part 0 : Foundation
│   ├── 0.1 How To Use This Book
│   ├── 0.2 How To Think In STL
│   ├── 0.3 STL Operation Mental Model
│   ├── 0.4 Constraint → Algorithm → STL
│   ├── 0.5 STL Decision Tree
│   ├── 0.6 Pattern Recognition Checklist
│   ├── 0.7 Contest Thinking
│   ├── 0.8 Contest Debugging Checklist
│   ├── 0.9 Common STL Mistakes
│   ├── 0.10 Time Complexity Cheat Sheet
│   ├── 0.11 STL Memory Model
│   ├── 0.12 Recognition Flowcharts
│   ├── 0.13 Container Selection Guide
│   ├── 0.14 When NOT To Use STL
│   └── 0.15 Candidate Master Mindset
│
├── Part 1 : 800–1000 Foundation Patterns
├── Part 2 : 1100–1400 Intermediate Patterns
├── Part 3 : 1500–1800 Core Candidate Patterns
├── Part 4 : 1900–2200 Candidate Master
├── Pattern Recognition Engine
├── Bug Atlas
├── Contest Speed Engine
└── Ultimate Cheat Sheet
```

---

# How Every Chapter Works

Every pattern chapter uses this template:

```text
1. Rating Range
2. Recognition Clues
3. Mental Model
4. ASCII Diagram
5. Brute Force
6. Why Brute Force Fails
7. Optimal STL Pattern
8. Dry Run
9. Generic C++17 Template
10. Variations
11. Common Bugs
12. Complexity
13. Contest Forms
14. Practice Ladder
15. Interview Version
16. One-Picture Summary
```

---

# PART 0 : FOUNDATION

---

## 0.1 How To Use This Book

Most STL notes teach:

```text
vector methods
map methods
set methods
priority_queue syntax
```

This book teaches:

```text
Problem statement
      ↓
Hidden operation
      ↓
Correct pattern
      ↓
Correct STL
      ↓
Safe template
      ↓
Accepted
```

Do not read this book like a dictionary. Use it like a contest training manual.

### Study Loop

```text
Read pattern
      ↓
Understand operation
      ↓
Dry run tiny example
      ↓
Code without looking
      ↓
Solve 5–10 problems
      ↓
Write mistake note
      ↓
Compress into one recognition sentence
```

Example recognition sentence:

```text
Fixed-size window maximum → monotonic deque storing indices.
```

---

## 0.2 How To Think In STL

STL containers are machines.

```text
vector          = random access + sorting machine
string          = character sequence machine
unordered_map   = frequency / lookup machine
map             = ordered key-value machine
set             = ordered unique search machine
multiset        = ordered duplicate machine
stack           = unresolved boundary machine
queue           = BFS wave machine
deque           = sliding window candidate machine
priority_queue  = current best candidate machine
list            = O(1) node movement machine
```

### Beginner vs CM Thinking

Beginner:

```text
I know map. Can I use map here?
```

CM-level:

```text
The repeated operation is count previous equal prefix.
Therefore unordered_map.
```

Beginner:

```text
I need maximum in each window. Maybe sort every window.
```

CM-level:

```text
Each element enters/leaves once. Need max among useful candidates. Deque.
```

---

## 0.3 STL Operation Mental Model

The question is always:

```text
What operation must be fast?
```

```text
Operation                                STL / Pattern
--------------------------------------------------------------
Index access                             vector
Read characters                          string
Sort once                                vector + sort
Frequency                                unordered_map
Ordered frequency                        map
Unique sorted values                     set
Sorted duplicates                        multiset
Nearest >= x                             set.lower_bound
Nearest <= x                             prev(set.upper_bound)
Current min/max repeatedly               priority_queue
FIFO expansion                           queue
Last unresolved candidate                stack
Window max/min                           deque
Interval overlap                         sweep line
Subarray sum count                       prefix + unordered_map
Dynamic median                           two heaps / two multisets
Graph adjacency                          vector<vector<int>>
Weighted graph                           vector<vector<pair<int,int>>>
```

### One Picture

```text
                         PROBLEM
                            |
                What operation repeats?
                            |
      ------------------------------------------------
      |              |              |                |
  Lookup/count    Ordered search   Best candidate    Window
      |              |              |                |
 unordered_map    set/multiset    priority_queue    deque
      |
 previous states
```

---

## 0.4 Constraint → Algorithm → STL

Constraints decide complexity.

```text
N <= 20        O(2^N), backtracking, bitmask
N <= 100       O(N^3) possible
N <= 2,000     O(N^2) possible
N <= 2e5       O(N log N) or O(N)
N <= 1e6       O(N) preferred, O(N log N) careful
N huge         O(log N), math, binary search
```

### Online vs Offline

```text
ONLINE:
updates happen and answer required now
    -> set / multiset / map / heap / deque

OFFLINE:
read all data first, reorder safely
    -> vector + sort / compression / sweep line
```

Example:

```text
All intervals known → sort events.
Insert/delete/query nearest online → set/multiset.
```

---

## 0.5 STL Decision Tree

```text
Need index access?
    -> vector / string

Need sort once?
    -> vector + sort

Need binary search after sorting?
    -> lower_bound / upper_bound

Need frequency?
    -> unordered_map

Need ordered keys?
    -> map

Need unique sorted active values?
    -> set

Need duplicates sorted?
    -> multiset

Need current best repeatedly?
    -> priority_queue

Need BFS?
    -> queue

Need nested / next greater?
    -> stack

Need window max/min?
    -> deque

Need interval overlap?
    -> sweep line

Need dynamic median?
    -> two heaps / two multisets

Need LRU design?
    -> list + unordered_map
```

---

## 0.6 Pattern Recognition Checklist

Before coding ask:

```text
[ ] Can I sort?
[ ] Need frequency?
[ ] Need previous state?
[ ] Need nearest value?
[ ] Need dynamic ordered data?
[ ] Need min/max repeatedly?
[ ] Need fixed/moving window?
[ ] Need interval overlap?
[ ] Need prefix difference?
[ ] Need online or offline?
[ ] Need graph traversal?
[ ] Need duplicates?
```

### Keyword Mapping

```text
same / count / frequency           -> unordered_map
nearest / at least / lower bound   -> set / multiset
top / largest / smallest repeatedly-> priority_queue
window / subarray size k           -> deque / multiset
next greater / previous smaller    -> stack
overlap / meeting / active         -> sweep line / heap
median                             -> two heaps / two multisets
shortest unweighted                -> queue BFS
shortest weighted non-negative     -> priority_queue Dijkstra
```

---

## 0.7 Contest Thinking

### 3-Pass Reading

```text
Pass 1: Remove story
Pass 2: Extract operations
Pass 3: Filter by constraints
```

### Contest Scratchpad

```cpp
/*
N =
Q =
Need =
Brute =
Bottleneck =
Pattern =
STL =
Complexity =
Edge cases =
*/
```

### Example

```cpp
/*
N=2e5
Need max in every window size k
Brute O(NK)
Bottleneck scanning window
Pattern monotonic deque
STL deque<int> indices
Complexity O(N)
Edge: k=1, all equal, decreasing
*/
```

---

## 0.8 Contest Debugging Checklist

```text
[ ] Did I sort before binary search?
[ ] Did I use long long for sums/products?
[ ] Did I check empty before top/front/back?
[ ] Did I handle duplicates?
[ ] Did I erase one multiset copy?
[ ] Did I use container.lower_bound, not std::lower_bound on set?
[ ] Is comparator strict?
[ ] Did I avoid invalid iterators?
[ ] Did I clean stale heap entries?
[ ] Did I handle 0-index/1-index?
[ ] Did I handle negative modulo?
```

---

## 0.9 Common STL Mistakes

### Mistake: `std::lower_bound` on set

Bad:

```cpp
auto it = lower_bound(s.begin(), s.end(), x); // O(N)
```

Good:

```cpp
auto it = s.lower_bound(x); // O(log N)
```

### Mistake: erase all copies from multiset

Bad:

```cpp
ms.erase(x);
```

Good:

```cpp
auto it = ms.find(x);
if (it != ms.end()) ms.erase(it);
```

### Mistake: non-strict comparator

Bad:

```cpp
return a <= b;
```

Good:

```cpp
return a < b;
```

### Mistake: map operator[] creates key

```cpp
if (mp[x]) { } // creates x if missing
```

Use:

```cpp
if (mp.count(x)) { }
```

---

## 0.10 Time Complexity Cheat Sheet

```text
vector index                         O(1)
vector push_back                     amortized O(1)
vector erase front/middle            O(N)
sort                                 O(N log N)
lower_bound vector                   O(log N)

set/map/multiset insert/find/erase   O(log N)
set.lower_bound                      O(log N)

unordered_map average op             O(1)
unordered_map worst op               O(N)

priority_queue top                   O(1)
priority_queue push/pop              O(log N)

stack/queue push/pop                 O(1)
deque push/pop front/back            O(1)

monotonic stack/deque                O(N)
sweep line                           O(N log N)
Dijkstra heap                        O((V+E) log V)
```

---

## 0.11 STL Memory Model

```text
vector:
[10][20][30][40]
contiguous, cache-friendly

list:
[10] -> [20] -> [30]
pointer chasing, usually slower

set/map:
tree nodes, ordered, O(logN), pointer-heavy

unordered_map:
buckets, average O(1), higher memory

priority_queue:
heap backed by vector
```

### Performance Order Roughly

```text
array/vector
deque
priority_queue
unordered_map
set/map/multiset
list
```

Rule:

```text
If vector + sort is enough, prefer it over set/map.
```

---

## 0.12 Recognition Flowcharts

### Subarray Flowchart

```text
Subarray?
  |
  +-- sum range static -> prefix
  +-- count sum K -> prefix + unordered_map
  +-- all positive + condition -> sliding window
  +-- max/min fixed window -> deque
  +-- median window -> multiset
  +-- boundary next greater -> stack
```

### Interval Flowchart

```text
Intervals?
  |
  +-- merge -> sort by start
  +-- max overlap -> events + sort
  +-- min rooms -> min heap of end times
  +-- online active intervals -> set/multiset/map
```

---

## 0.13 Container Selection Guide

```text
vector:
    arrays, sorting, prefix, DP, graph adjacency, compression

string:
    char scan, frequency, parsing, window

set:
    unique sorted, nearest value, dynamic min/max

multiset:
    sorted duplicates, window median/min/max

map:
    ordered keys, key lower_bound

unordered_map:
    frequency, prefix states, two sum

priority_queue:
    top-K, scheduling, Dijkstra, greedy best

stack:
    next greater, parentheses, histogram

queue:
    BFS, level order, simulation

deque:
    sliding max/min, 0-1 BFS

list:
    LRU with iterator map
```

---

## 0.14 When NOT To Use STL

```text
Do not use set if vector+sort is enough.
Do not use map if unordered_map is enough.
Do not use priority_queue if arbitrary deletion is required, unless lazy deletion works.
Do not use vector.erase(begin()) repeatedly.
Do not use list unless you already have iterators.
Do not use substr repeatedly on large strings.
Do not over-engineer Div2 A/B.
```

---

## 0.15 Candidate Master Mindset

Candidate Master is not about knowing every data structure.

It is about:

```text
recognition speed
complexity discipline
implementation reliability
```

### 5-Second Recognition Goals

```text
next greater              -> monotonic stack
window maximum            -> monotonic deque
count subarray sum K      -> prefix + map
active intervals          -> sweep line
dynamic nearest           -> set.lower_bound
top K                     -> priority_queue
dynamic median            -> two heaps / two multisets
merge intervals           -> sort
Dijkstra                  -> priority_queue
LRU                       -> list + unordered_map
```

---

# PART 1 : 800–1000 FOUNDATION PATTERNS

At 800–1000, STL is mostly about clean implementation, sorting, counting, and basic search.

---

## 1.1 Vector Patterns

### Recognition

```text
array input
need index
need min/max
need sort
need prefix
need DP storage
```

### Mental Model

```text
index:  0   1   2   3
       [5] [1] [9] [2]
```

### Template

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;
    vector<int> a(n);
    for (int &x : a) cin >> x;

    sort(a.begin(), a.end());

    for (int x : a) cout << x << ' ';
    cout << '\n';
}
```

### Common Bugs

```text
out of bounds
int overflow in sum
forget sort before binary search
erase front repeatedly
```

### Practice Forms

```text
find max/min
sort and compare
remove duplicates
count pairs after sorting
prefix sum
```

---

## 1.2 String Patterns

### Recognition

```text
characters
palindrome
anagram
frequency
substring
parsing
```

### Frequency Template

```cpp
vector<int> freq(26, 0);
for (char c : s) freq[c - 'a']++;
```

### Palindrome Template

```cpp
bool ok = true;
for (int l = 0, r = (int)s.size() - 1; l < r; l++, r--) {
    if (s[l] != s[r]) ok = false;
}
```

### Bugs

```text
uppercase/lowercase assumption
getline after cin
substr copies
signed char indexing
```

---

## 1.3 Sorting Patterns

### Recognition

```text
choose smallest/largest
compare after reordering
pairing
greedy
minimum operations after ordering
```

### Mental Model

```text
chaos -> sorted order -> structure
```

### Template

```cpp
sort(a.begin(), a.end());
```

Descending:

```cpp
sort(a.rbegin(), a.rend());
```

### Contest Forms

```text
sort and pair smallest with largest
sort and greedily choose
sort by first value
sort strings lexicographically
```

---

## 1.4 Pair & Tuple Patterns

### Recognition

```text
value + index
start + end
score + name
distance + node
```

### Template

```cpp
vector<pair<int,int>> v;
sort(v.begin(), v.end());
```

Default pair sorting:

```text
first ascending, then second ascending
```

### Custom Sort

```cpp
sort(v.begin(), v.end(), [](auto &a, auto &b) {
    if (a.first != b.first) return a.first < b.first;
    return a.second > b.second;
});
```

---

## 1.5 Basic Map

### Recognition

```text
frequency with ordered keys
count occurrences
value -> count
```

### Template

```cpp
map<int,int> mp;
for (int x : a) mp[x]++;
for (auto [key, cnt] : mp) {
    cout << key << " " << cnt << "\n";
}
```

Use map when ordered traversal matters.

---

## 1.6 Basic Set

### Recognition

```text
unique values
sorted unique
does value exist?
minimum/maximum unique active value
```

### Template

```cpp
set<int> s;
for (int x : a) s.insert(x);
cout << s.size() << "\n";
```

### Min/Max

```cpp
int mn = *s.begin();
int mx = *s.rbegin();
```

Check non-empty first.

---

## 1.7 Prefix Sum Patterns

### Recognition

```text
many range sum queries
sum l..r
static array
```

### Formula

```text
sum(l..r) = pref[r+1] - pref[l]
```

### Template

```cpp
vector<long long> pref(n + 1, 0);
for (int i = 0; i < n; i++) {
    pref[i + 1] = pref[i] + a[i];
}
long long sum = pref[r + 1] - pref[l];
```

### Bugs

```text
off-by-one
int overflow
1-index vs 0-index
```

---

## 1.8 Binary Search STL

### Recognition

```text
sorted array
first >= x
first > x
count occurrences
position to insert
```

### Template

```cpp
auto it = lower_bound(a.begin(), a.end(), x);
int idx = it - a.begin();
```

### Count x

```cpp
auto l = lower_bound(a.begin(), a.end(), x);
auto r = upper_bound(a.begin(), a.end(), x);
cout << r - l << "\n";
```

---

## 1.9 lower_bound / upper_bound

```text
lower_bound(x) = first >= x
upper_bound(x) = first > x
```

### Last <= x

```cpp
auto it = upper_bound(a.begin(), a.end(), x);
if (it != a.begin()) {
    --it;
    cout << *it;
}
```

### Mistake

Do not call `std::lower_bound` on set.

Use:

```cpp
s.lower_bound(x);
```

---

## 1.10 Beginner Recognition Drills

```text
Need unique count                      -> set
Need count each value                  -> map/unordered_map
Need sorted output                     -> vector + sort
Need sum over many ranges              -> prefix
Need first element >= x in sorted list  -> lower_bound
Need compare pairs                     -> pair + sort
Need char count                        -> vector<int> freq(26)
Need preserve index after sort         -> pair(value,index)
```

---

## 1.11 800–1000 Practice Ladder

```text
1. vector input/output
2. max/min in array
3. sort and compare
4. frequency of values
5. frequency of characters
6. unique count
7. prefix range sum
8. lower_bound count occurrences
9. pair sorting by value/index
10. simple greedy after sort
```

---

# PART 2 : 1100–1400 INTERMEDIATE PATTERNS

At 1100–1400, STL starts becoming a pattern engine.

---

## 2.1 Frequency Patterns

### Recognition

```text
same value
same remainder
same difference
same normalized key
previous occurrence
```

### Template

```cpp
unordered_map<long long,long long> freq;
long long ans = 0;

for (long long x : a) {
    ans += freq[x];
    freq[x]++;
}
```

### Remainder Pair Form

```cpp
int r = ((x % k) + k) % k;
ans += freq[r];
freq[r]++;
```

### Common Bugs

```text
negative modulo
long long answer
operator[] creates key
unordered_map worst case
```

---

## 2.2 unordered_map vs map

```text
unordered_map:
    average O(1)
    no order
    best for frequency

map:
    O(logN)
    ordered keys
    lower_bound by key
```

Use `unordered_map` for speed unless order is needed.

### Safe Hash Template

```cpp
struct custom_hash {
    static uint64_t splitmix64(uint64_t x) {
        x += 0x9e3779b97f4a7c15;
        x = (x ^ (x >> 30)) * 0xbf58476d1ce4e5b9;
        x = (x ^ (x >> 27)) * 0x94d049bb133111eb;
        return x ^ (x >> 31);
    }
    size_t operator()(uint64_t x) const {
        static const uint64_t FIXED_RANDOM =
            chrono::steady_clock::now().time_since_epoch().count();
        return splitmix64(x + FIXED_RANDOM);
    }
};
```

---

## 2.3 Two Pointers

### Recognition

```text
sorted array
pair sum
remove duplicates
minimum/maximum pair condition
both pointers move monotonically
```

### Template

```cpp
sort(a.begin(), a.end());

int l = 0, r = n - 1;
while (l < r) {
    long long sum = a[l] + a[r];
    if (sum == target) {
        // found
        break;
    } else if (sum < target) {
        l++;
    } else {
        r--;
    }
}
```

### Mental Model

```text
smallest ---------------- biggest
   l                          r
```

Each move eliminates impossible pairs.

---

## 2.4 Sliding Window

### Recognition

```text
contiguous subarray
positive numbers
longest/shortest window satisfying condition
at most K distinct
no repeated characters
```

### Template

```cpp
int l = 0;
for (int r = 0; r < n; r++) {
    // add a[r]

    while (/* invalid */) {
        // remove a[l]
        l++;
    }

    // update answer
}
```

### Important Warning

Sliding window with sum works cleanly when numbers are non-negative. With negatives, use prefix map or other methods.

---

## 2.5 Custom Comparator

### Recognition

```text
sort by score desc
tie by index asc
sort intervals
sort by ending time
```

### Template

```cpp
sort(v.begin(), v.end(), [](auto &a, auto &b) {
    if (a.score != b.score) return a.score > b.score;
    return a.id < b.id;
});
```

### Rules

```text
comp(a,a) must be false
never use <=
avoid subtraction due to overflow
tie-break clearly
```

---

## 2.6 Greedy + Sort

### Recognition

```text
choose minimum possible
sort by deadline/end/cost
pair smallest with largest
maximize count
```

### Mental Model

Sorting reveals local choices.

```text
unsorted chaos -> sorted structure -> greedy choice
```

### Common Forms

```text
activity selection          -> sort by end
boats/gondola pairing       -> sort + two pointers
minimum platforms/rooms     -> sort events or heap
assign cookies/resources    -> sort both arrays
```

---

## 2.7 Coordinate Compression

### Recognition

```text
values up to 1e9
only relative order matters
need indexable ranks
Fenwick/segment tree later
```

### Template

```cpp
vector<int> vals = a;
sort(vals.begin(), vals.end());
vals.erase(unique(vals.begin(), vals.end()), vals.end());

for (int x : a) {
    int id = lower_bound(vals.begin(), vals.end(), x) - vals.begin();
}
```

### Dry Run

```text
a = [100, 5, 100, 20]
vals = [5,20,100]
100 -> 2
5   -> 0
20  -> 1
```

---

## 2.8 Offline Sorting

### Recognition

```text
all queries known
can reorder queries
answer depends on threshold
move pointer once
```

### Example Form

```text
For each query x, count numbers <= x.
```

Template:

```cpp
sort(a.begin(), a.end());

for (int x : queries) {
    int cnt = upper_bound(a.begin(), a.end(), x) - a.begin();
}
```

More advanced: sort queries by threshold and maintain active data.

---

## 2.9 Intermediate Recognition Drills

```text
same remainder pairs             -> frequency map
pair sum sorted                  -> two pointers
longest substring no repeat      -> sliding window + freq
sort by end time                 -> greedy + custom sort
huge values but only order       -> coordinate compression
queries can be reordered         -> offline sorting
need first >= x after sorting    -> lower_bound
need count <= x                  -> upper_bound
```

---

## 2.10 1100–1400 Practice Ladder

```text
1. frequency pairs
2. remainder grouping
3. two-sum after sorting
4. longest valid window
5. at most K distinct
6. custom comparator sorting
7. greedy after sorting
8. coordinate compression basics
9. offline count queries
10. lower_bound variants
```

---

# PART 3 : 1500–1800 CORE CANDIDATE PATTERNS

At 1500–1800, STL patterns become the main problem-solving weapon.

---

## 3.1 Prefix Map

### Recognition

```text
count subarrays with sum K
count subarrays divisible by K
equal 0/1
longest subarray with property
previous prefix needed
```

### Core Formula

```text
sum(l..r) = pref[r] - pref[l-1]

Need sum K:
pref[l-1] = pref[r] - K
```

### Template

```cpp
unordered_map<long long,long long> freq;
freq[0] = 1;

long long pref = 0, ans = 0;

for (long long x : a) {
    pref += x;
    ans += freq[pref - k];
    freq[pref]++;
}
```

### Variations

```text
subarray divisible by k -> freq[pref % k]
equal 0/1 -> convert 0 to -1
longest -> store first index
```

---

## 3.2 Monotonic Stack

### Recognition

```text
next greater
next smaller
previous greater
previous smaller
span
histogram
nearest boundary
```

### Mental Model

```text
stack stores unresolved indices
each element enters once and leaves once
```

### Next Greater Template

```cpp
vector<int> ans(n, -1);
stack<int> st;

for (int i = 0; i < n; i++) {
    while (!st.empty() && a[st.top()] < a[i]) {
        ans[st.top()] = a[i];
        st.pop();
    }
    st.push(i);
}
```

### Matrix

```text
next greater right:
    scan left to right, pop while < current

next smaller right:
    scan left to right, pop while > current

previous greater:
    pop while <= current, top is answer

previous smaller:
    pop while >= current, top is answer
```

---

## 3.3 Monotonic Deque

### Recognition

```text
maximum/minimum in every window
fixed size k
moving window
old elements expire
```

### Template: Sliding Window Maximum

```cpp
deque<int> dq;

for (int i = 0; i < n; i++) {
    while (!dq.empty() && dq.front() <= i - k) dq.pop_front();
    while (!dq.empty() && a[dq.back()] <= a[i]) dq.pop_back();

    dq.push_back(i);

    if (i >= k - 1) {
        cout << a[dq.front()] << " ";
    }
}
```

### Bugs

```text
store indices, not values
remove expired before output
duplicates require careful <= / <
```

---

## 3.4 Heap / Top-K

### Recognition

```text
top K
kth largest
smallest repeatedly
largest repeatedly
merge k sorted lists
scheduling
```

### Kth Largest Template

```cpp
priority_queue<int, vector<int>, greater<int>> pq;

for (int x : a) {
    pq.push(x);
    if ((int)pq.size() > k) pq.pop();
}

cout << pq.top() << "\n";
```

### Rule

```text
K largest -> min heap of size K
K smallest -> max heap of size K
```

---

## 3.5 Set lower_bound

### Recognition

```text
nearest >= x
nearest <= x
dynamic insert/delete
active values
ordered unique values
```

### Template

```cpp
set<int> s;

auto it = s.lower_bound(x); // first >= x
if (it != s.end()) {
    // use *it
}

auto it2 = s.upper_bound(x); // first > x
if (it2 != s.begin()) {
    --it2; // largest <= x
}
```

### Bugs

```text
dereference end
decrement begin
std::lower_bound on set
duplicates require multiset
```

---

## 3.6 Multiset Window

### Recognition

```text
window min/max with deletions
duplicates matter
dynamic sorted multiset
```

### Template

```cpp
multiset<int> ms;

for (int i = 0; i < n; i++) {
    ms.insert(a[i]);

    if (i >= k) {
        auto it = ms.find(a[i-k]);
        if (it != ms.end()) ms.erase(it);
    }

    if (i >= k - 1) {
        int mn = *ms.begin();
        int mx = *ms.rbegin();
    }
}
```

---

## 3.7 Merge Intervals

### Recognition

```text
overlapping intervals
combine ranges
union of intervals
```

### Template

```cpp
sort(intervals.begin(), intervals.end());

vector<pair<int,int>> merged;

for (auto [l, r] : intervals) {
    if (merged.empty() || merged.back().second < l) {
        merged.push_back({l, r});
    } else {
        merged.back().second = max(merged.back().second, r);
    }
}
```

---

## 3.8 Sweep Line

### Recognition

```text
maximum overlap
active intervals
number of ongoing events
range add offline
meeting rooms
```

### Template

```cpp
vector<pair<int,int>> events;

for (auto [l, r] : intervals) {
    events.push_back({l, +1});
    events.push_back({r, -1});
}

sort(events.begin(), events.end());

int active = 0, best = 0;
for (auto [x, delta] : events) {
    active += delta;
    best = max(best, active);
}
```

### Tie Rule

For half-open intervals `[l,r)`, end before start at same coordinate.

Since `-1 < +1`, pair sorting handles this if end is `-1`.

---

## 3.9 Lazy Heap

### Recognition

```text
heap but need delete old elements
sliding window heap
Dijkstra stale entries
frequency heap with updates
```

### Lazy Deletion Template

```cpp
priority_queue<int> pq;
unordered_map<int,int> del;

auto clean = [&]() {
    while (!pq.empty() && del[pq.top()] > 0) {
        del[pq.top()]--;
        pq.pop();
    }
};
```

### Dijkstra Stale Pattern

```cpp
if (d != dist[u]) continue;
```

---

## 3.10 1500–1800 Recognition Drills

```text
count subarrays sum K              -> prefix map
next greater right                 -> monotonic stack
window maximum                     -> monotonic deque
kth largest                        -> heap size K
nearest active value               -> set.lower_bound
window min/max with duplicates     -> multiset
merge overlapping ranges           -> sort intervals
max number of active intervals     -> sweep line
heap with deletion                 -> lazy deletion
```

---

## 3.11 1500–1800 Practice Ladder

```text
1. subarray sum K
2. subarray divisible by K
3. next greater element
4. histogram rectangle
5. sliding window maximum
6. top K frequent
7. concert tickets style lower_bound
8. sliding window min/max
9. merge intervals
10. meeting rooms / overlap count
11. lazy Dijkstra/heap cleanup
```

---

# PART 4 : 1900–2200 CANDIDATE MASTER

These are the patterns that often separate strong specialist from Candidate Master.

---

## 4.1 Dynamic Median

### Recognition

```text
stream median
insert numbers and output middle
window median
dynamic median after add/remove
```

### Two Heap Model

```text
low  = max heap for smaller half
high = min heap for larger half

max(low) <= min(high)
size(low) >= size(high)
```

### Insert-Only Template

```cpp
priority_queue<int> low;
priority_queue<int, vector<int>, greater<int>> high;

void add(int x) {
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
}
```

---

## 4.2 Two Multisets

### Recognition

```text
median with deletion
sliding window median
dynamic lower/upper halves
```

### Template

```cpp
multiset<int> low, high;

void rebalance() {
    while (low.size() < high.size()) {
        low.insert(*high.begin());
        high.erase(high.begin());
    }
    while (low.size() > high.size() + 1) {
        auto it = prev(low.end());
        high.insert(*it);
        low.erase(it);
    }
}

void add(int x) {
    if (low.empty() || x <= *low.rbegin()) low.insert(x);
    else high.insert(x);
    rebalance();
}

void removeOne(int x) {
    auto it = low.find(x);
    if (it != low.end()) low.erase(it);
    else {
        it = high.find(x);
        if (it != high.end()) high.erase(it);
    }
    rebalance();
}

int median() {
    return *low.rbegin();
}
```

---

## 4.3 Advanced Sweep Line

### Recognition

```text
events with tie-breaking
inclusive vs exclusive intervals
range updates
active set of intervals
process coordinates in order
```

### Inclusive Range Trick

For `[l,r]`:

```cpp
events.push_back({l, +1});
events.push_back({r + 1, -1});
```

If `r+1` may overflow, use explicit event type ordering.

---

## 4.4 Offline Queries

### Recognition

```text
queries can be sorted
threshold queries
process additions once
answer independent queries
```

### Mental Model

```text
sort data
sort queries
move pointer once
maintain data structure
answer query
```

### Template Shape

```cpp
sort(items.begin(), items.end());
sort(queries.begin(), queries.end());

int ptr = 0;
for (auto q : queries) {
    while (ptr < n && items[ptr].value <= q.limit) {
        // add item to structure
        ptr++;
    }
    // answer query
}
```

---

## 4.5 Compression + Fenwick Preparation

Even when Fenwick is not STL, STL prepares it.

```cpp
vector<int> vals = a;
sort(vals.begin(), vals.end());
vals.erase(unique(vals.begin(), vals.end()), vals.end());

auto getId = [&](int x) {
    return lower_bound(vals.begin(), vals.end(), x) - vals.begin() + 1;
};
```

### Recognition

```text
large coordinates
need ranks
inversions
offline range queries
value order matters, actual value not important
```

---

## 4.6 Graph STL Patterns

```text
unweighted graph:
vector<vector<int>> g;
queue<int> q;

weighted graph:
vector<vector<pair<int,int>>> g;
priority_queue<pair<ll,int>, vector<pair<ll,int>>, greater<pair<ll,int>>> pq;

edge list:
vector<tuple<int,int,int>> edges;

topological sort:
queue<int> zeroIndegree;

DSU storage:
vector<int> parent, sz;
```

---

## 4.7 Dijkstra Heap

### Recognition

```text
weighted graph
non-negative weights
shortest path
current closest node
```

### Template

```cpp
using ll = long long;
const ll INF = 4e18;

vector<ll> dist(n, INF);
priority_queue<pair<ll,int>, vector<pair<ll,int>>, greater<pair<ll,int>>> pq;

dist[src] = 0;
pq.push({0, src});

while (!pq.empty()) {
    auto [d, u] = pq.top();
    pq.pop();

    if (d != dist[u]) continue;

    for (auto [v, w] : g[u]) {
        if (dist[v] > d + w) {
            dist[v] = d + w;
            pq.push({dist[v], v});
        }
    }
}
```

---

## 4.8 Range Mapping

### Recognition

```text
maintain disjoint intervals
insert/delete ranges
find interval containing point
ordered map of starts
```

### Map Idea

```text
start -> end
```

Find interval with start <= x:

```cpp
auto it = mp.upper_bound(x);
if (it != mp.begin()) {
    --it;
    if (it->second >= x) {
        // x is inside [it->first, it->second]
    }
}
```

---

## 4.9 STL Micro Optimizations

```cpp
ios::sync_with_stdio(false);
cin.tie(nullptr);
```

For unordered_map:

```cpp
mp.reserve(1 << 20);
mp.max_load_factor(0.7);
```

Avoid:

```text
copying vectors in loops
passing large containers by value
substr in nested loop
map if unordered_map enough
endl causing flush
```

Use:

```cpp
const vector<int>&
'\n' instead of endl
emplace_back
```

---

## 4.10 CM Recognition Drills

```text
median with delete                         -> two multisets
offline threshold queries                  -> sort queries + pointer
dynamic interval containing x              -> map upper_bound
weighted shortest path                     -> Dijkstra heap
large values need rank                     -> coordinate compression
active intervals with tie-breaking         -> advanced sweep line
heap with outdated states                  -> stale check
insert/delete/query median                 -> multiset halves
```

---

## 4.11 1900–2200 Practice Ladder

```text
1. sliding median
2. dynamic order statistics preparation
3. traffic lights / range split
4. offline threshold queries
5. sweep line with event tie
6. Dijkstra with states
7. graph shortest path with stale heap
8. interval map containment
9. compression + Fenwick prep
10. STL optimization under pressure
```

---

# PATTERN RECOGNITION ENGINE

---

## Problem → STL

```text
Need count previous same thing
    -> unordered_map

Need sorted unique active values
    -> set

Need sorted duplicate active values
    -> multiset

Need current minimum/maximum repeatedly
    -> priority_queue

Need nearest greater/smaller boundary
    -> stack

Need moving window max/min
    -> deque

Need interval overlap count
    -> sweep line

Need merge intervals
    -> vector + sort

Need median dynamically
    -> two heaps / two multisets

Need shortest path weighted
    -> priority_queue Dijkstra

Need BFS layers
    -> queue

Need cache recency
    -> list + unordered_map
```

---

## Keyword → STL

```text
"frequency"             unordered_map
"same remainder"        unordered_map
"previous prefix"       unordered_map
"nearest"               set
"at least x"            lower_bound
"at most x"             upper_bound + prev
"duplicates sorted"     multiset
"top k"                 priority_queue
"window maximum"        deque
"next greater"          stack
"active intervals"      sweep line
"median"                two heaps / multiset
"shortest path"         queue / priority_queue
"offline queries"       sort + pointer
```

---

## Constraint → STL

```text
N <= 2e5 and static:
    vector + sort / prefix / binary search

N <= 2e5 and dynamic:
    set / multiset / map / heap

Q <= 2e5:
    O(logN) or O(1) per query

Values up to 1e9:
    compression / map / unordered_map

Need all pairs but N large:
    count with map instead of nested loops
```

---

## 100 Recognition Drills

```text
001 fixed-size window maximum -> deque
002 fixed-size window minimum -> deque
003 count subarrays sum K -> prefix + unordered_map
004 count subarrays divisible by K -> prefix remainder map
005 next greater right -> monotonic stack
006 previous smaller left -> monotonic stack
007 largest rectangle histogram -> monotonic stack
008 kth largest -> min heap size K
009 top K frequent -> frequency + heap
010 nearest value >= x -> set.lower_bound
011 largest value <= x -> prev(set.upper_bound)
012 sorted duplicates under delete -> multiset
013 dynamic median with delete -> two multisets
014 stream median insert only -> two heaps
015 merge intervals -> sort by start
016 max overlap intervals -> sweep line
017 meeting rooms -> min heap of end times
018 unweighted shortest path -> queue BFS
019 weighted shortest path -> priority_queue Dijkstra
020 process queries by threshold -> offline sort
021 huge values only order matters -> coordinate compression
022 LRU cache -> list + unordered_map
023 frequency of characters -> vector<int> freq
024 unique values -> set
025 count occurrences sorted vector -> lower_bound + upper_bound
026 pair sum sorted -> two pointers
027 longest no repeat substring -> sliding window + freq
028 at most K distinct -> sliding window + map
029 all positive subarray sum <= K -> sliding window
030 negative subarray sum K -> prefix map
031 dynamic min/max with duplicates -> multiset
032 current best task -> priority_queue
033 stale heap entries -> lazy deletion
034 active coordinate events -> sweep line
035 ordered keys traversal -> map
036 existence lookup -> unordered_set
037 range sum static -> prefix
038 many first >= x queries static -> sorted vector + lower_bound
039 many first >= x queries dynamic -> set.lower_bound
040 erase one duplicate -> multiset.find + erase(iterator)
041 preserve index after sorting -> pair(value,index)
042 sort by two keys -> custom comparator
043 comparator tie -> strict weak ordering
044 graph adjacency -> vector<vector<int>>
045 weighted adjacency -> vector<vector<pair<int,int>>>
046 topo zero indegree -> queue
047 repeated minimum edge -> priority_queue
048 dynamic interval containing point -> map.upper_bound
049 online insert/delete/query max -> multiset
050 online insert/delete/query kth -> advanced tree/Fenwick prep
051 inversion count prep -> compression
052 rank queries static -> sort + lower_bound
053 group anagrams -> unordered_map<string, vector<string>>
054 previous occurrence index -> unordered_map<value,index>
055 normalized pair key -> map/unordered_map
056 count equal differences -> unordered_map
057 count pairs by sum complement -> unordered_map
058 range add offline -> difference/events
059 half-open interval overlap -> end before start
060 inclusive interval overlap -> r+1 event
061 min platforms -> sweep/heap
062 choose earliest finishing -> sort by end
063 pair smallest/largest -> sort + two pointers
064 remove expired window values -> deque/multiset
065 sliding median -> two multisets
066 BFS multi-source -> queue with all sources
067 0-1 BFS -> deque
068 Dijkstra stale check -> if(d!=dist[u]) continue
069 need full sorted order once -> vector + sort
070 need dynamic sorted order -> set/multiset/map
071 no order only lookup -> unordered_map
072 substr too slow -> indices/hash/window
073 need max prefix -> vector prefix/suffix
074 need next non-smaller -> stack with inequality choice
075 equal values affect monotonic -> choose < vs <=
076 answer all queries after sorting -> offline
077 need active count -> events
078 need active set of ids -> set
079 need active min end time -> min heap
080 need evict least recent -> list + map
081 need evict least frequent -> freq buckets
082 repeated sorted insert but no delete -> vector maybe enough offline
083 count <= x -> upper_bound
084 count < x -> lower_bound
085 first > x -> upper_bound
086 first >= x -> lower_bound
087 last < x -> prev(lower_bound)
088 last <= x -> prev(upper_bound)
089 dynamic mex -> set of missing values
090 duplicate window max with erase -> multiset
091 unique window condition -> set/map counts
092 current largest frequency -> heap with stale freq
093 reordering allowed -> sort
094 constraints huge -> math/binary search
095 state sparse DP -> unordered_map/map
096 dense DP -> vector
097 bitmask state -> vector size 1<<n
098 interval union length -> sweep line
099 closest pair among active sorted -> set
100 CM shortcut -> operation first, STL second
```

---

## 200 CF Pattern Forms

Instead of listing exact problem names, use forms. Master forms, then map to problems.

```text
A-level forms:
1. sort and compare
2. count unique
3. char frequency
4. min/max difference
5. prefix range sum

B-level forms:
6. frequency decisions
7. pair after sorting
8. remainder grouping
9. two pointers
10. lower_bound queries

C-level forms:
11. prefix map count
12. monotonic stack boundary
13. sliding window max
14. sweep line overlap
15. multiset active values

D-level / CM forms:
16. lazy heap
17. dynamic median
18. offline queries
19. graph with heap
20. range mapping
21. compression + Fenwick prep
22. event tie-breaking
23. two multisets
24. active interval set
25. map upper_bound containment
```

Repeat each form with different stories: monsters, tickets, meetings, roads, arrays, strings, games.

---

## FAANG Pattern Mapping

```text
Two Sum                         unordered_map
Group Anagrams                  unordered_map<string, vector<string>>
Top K Frequent                  unordered_map + heap
Merge Intervals                 vector + sort
Meeting Rooms                   sort + heap
Sliding Window Maximum          deque
Valid Parentheses               stack
Daily Temperatures              monotonic stack
Median Finder                   two heaps
LRU Cache                       list + unordered_map
LFU Cache                       unordered_map + frequency lists
Random Pick Weight              prefix + lower_bound
Dijkstra Network Delay          priority_queue
```

---

# BUG ATLAS

---

## WA Atlas

```text
wrong inequality
wrong tie-break
duplicates ignored
forgot sort
wrong index conversion
negative modulo
overflow
wrong initialization
did not handle empty result
using value instead of index in deque
```

---

## TLE Atlas

```text
O(N^2) for N=2e5
sort inside loop
vector.erase(begin()) repeatedly
std::lower_bound on set
map where unordered_map enough
substr in nested loop
not cleaning heap stale entries
copying large containers
endl flushing
```

---

## MLE Atlas

```text
vector<vector<int>>(huge)
storing all substrings
map with too many states
heap grows forever with lazy deletion
copying large vectors in recursion
using long long matrix unnecessarily
```

---

## Iterator Bugs

```cpp
// safe erase from map/set
for (auto it = s.begin(); it != s.end(); ) {
    if (bad(*it)) it = s.erase(it);
    else ++it;
}
```

Rules:

```text
vector erase invalidates current and after
set/map erase invalidates only erased iterator
unordered_map rehash invalidates iterators
list keeps other iterators valid
```

---

## Comparator Bugs

Bad:

```cpp
return a <= b;
```

Good:

```cpp
return a < b;
```

Bad:

```cpp
return a.x - b.x < 0;
```

Good:

```cpp
return a.x < b.x;
```

Priority queue comparator reminder:

```text
return true means a has lower priority than b
```

---

## Overflow Bugs

Use `long long` for:

```text
sum of array
pair count
product
distance
Dijkstra dist
prefix sum
answer count
```

Template:

```cpp
using ll = long long;
```

---

## Duplicate Bugs

```text
set removes duplicates
multiset keeps duplicates
ms.erase(x) removes all copies
lower_bound / upper_bound behavior changes with duplicates
monotonic stack equality condition matters
```

---

## Debugging Playbook

```text
WA:
    test tiny, all equal, increasing, decreasing, duplicates

TLE:
    inspect nested loops, erase, sort inside loop, wrong lower_bound

RE:
    empty access, end dereference, begin decrement, out of bounds

MLE:
    inspect dimensions, copies, heaps, maps
```

---

# CONTEST SPEED ENGINE

---

## Contest Workflow

```text
1. Read statement quickly.
2. Extract actual data structure problem.
3. Mark N, Q, value range.
4. Write brute force in words.
5. Identify bottleneck.
6. Match bottleneck to STL pattern.
7. Write skeleton.
8. Dry run sample.
9. Code.
10. Run edge cases mentally.
11. Submit.
12. If WA, debug by category.
```

---

## 5-Second Recognition

Train these until automatic:

```text
nearest >= x          -> lower_bound
same prefix           -> unordered_map
next greater          -> stack
window max            -> deque
active intervals      -> sweep line
dynamic median        -> two multisets
weighted shortest     -> Dijkstra heap
top K                 -> heap
merge ranges          -> sort
huge values rank      -> compression
```

---

## Skeleton Templates

### Fast IO

```cpp
ios::sync_with_stdio(false);
cin.tie(nullptr);
```

### Vector Input

```cpp
int n;
cin >> n;
vector<long long> a(n);
for (auto &x : a) cin >> x;
```

### Frequency

```cpp
unordered_map<long long,long long> freq;
for (auto x : a) freq[x]++;
```

### Sort + Unique

```cpp
sort(v.begin(), v.end());
v.erase(unique(v.begin(), v.end()), v.end());
```

### Safe Multiset Erase

```cpp
auto it = ms.find(x);
if (it != ms.end()) ms.erase(it);
```

### Safe Set Previous

```cpp
auto it = s.upper_bound(x);
if (it != s.begin()) {
    --it;
}
```

---

## Implementation Order

```text
Simple pattern:
    read -> process -> output

Complex pattern:
    helper add()
    helper remove()
    helper rebalance()
    helper query()
```

This is critical for median, LFU/LRU, lazy heap, and sliding window.

---

## Dry Run Framework

For each pattern, dry run:

```text
current i
current value
data structure state
answer update
expired/removed items
edge condition
```

Example for deque:

```text
i=4, a[i]=5
remove expired front
remove smaller back
push index
front is max
```

---

## Edge Case Checklist

```text
N=1
all equal
increasing
decreasing
duplicates
negative numbers
zero
large values
empty answer
k=1
k=n
x less than all
x greater than all
```

---

## Pre-submit Checklist

```text
[ ] complexity
[ ] long long
[ ] sort before binary search
[ ] empty check
[ ] iterator safe
[ ] duplicates
[ ] tie-break
[ ] modulo fixed
[ ] no vector erase front
[ ] no std lower_bound on set
[ ] no endl spam
```

---

## Post-contest Review System

After each missed problem:

```text
Problem:
Rating:
Pattern:
STL:
Why I missed it:
Bug:
Recognition clue:
New rule:
Similar problems:
```

Example:

```text
Problem: Sliding median
Pattern: two multisets
Why missed: tried heap but needed deletion
Rule: median + deletion -> multiset halves
```

---

# ULTIMATE CHEAT SHEET

---

## STL Decision Tree

```text
Need index?
    vector

Need frequency?
    unordered_map

Need order?
    map / set / multiset

Need nearest?
    set.lower_bound

Need duplicates?
    multiset

Need current best?
    priority_queue

Need next greater?
    stack

Need window max?
    deque

Need interval overlap?
    sweep line

Need median?
    two heaps / two multisets

Need graph shortest?
    queue / priority_queue

Need cache?
    list + unordered_map
```

---

## Complexity Table

```text
vector sort              O(NlogN)
binary search            O(logN)
unordered_map avg        O(1)
map/set/multiset         O(logN)
priority_queue push/pop  O(logN)
monotonic stack/deque    O(N)
sweep line               O(NlogN)
two pointers             O(N)
prefix map               O(N)
Dijkstra                 O((V+E)logV)
```

---

## Container Comparison

```text
vector:
    fastest static sequence

set:
    sorted unique dynamic

multiset:
    sorted duplicates dynamic

map:
    ordered key-value

unordered_map:
    fastest average lookup

priority_queue:
    best element only

deque:
    both ends + window

stack:
    unresolved boundaries

queue:
    BFS wave

list:
    O(1) node movement when iterator known
```

---

## Recognition Keywords

```text
count previous        map
same remainder        map
nearest               set
at least              lower_bound
current best          heap
top k                 heap
window max            deque
next greater          stack
overlap               sweep
median                two heaps/multisets
offline               sort queries
huge values           compression
```

---

## Common CF Forms By Rating

```text
800:
    sort, vector, frequency, strings

1000:
    map, set, prefix, lower_bound

1200:
    two pointers, greedy sort, custom comparator

1400:
    sliding window, unordered_map, compression

1600:
    monotonic stack, deque, heap, set lower_bound

1800:
    prefix map, sweep line, multiset, lazy heap

1900:
    two multisets, dynamic median, offline queries

2100:
    advanced sweep, range mapping, graph heap, compression prep
```

---

## Common WA Causes

```text
wrong inequality
wrong tie-break
forgot duplicates
overflow
empty access
wrong lower_bound
off-by-one
negative modulo
not sorting
not clearing data
```

---

## Rating-wise STL Roadmap

```text
800–1000:
    vector, string, sort, pair, map, set, prefix

1100–1400:
    unordered_map, two pointers, sliding window, comparator, compression

1500–1800:
    prefix map, monotonic stack/deque, heap, set, multiset, sweep line

1900–2200:
    two multisets, lazy heap, offline queries, Dijkstra heap, range mapping
```

---

## One-page CM Mind Map

```text
                         PROBLEM
                            |
                    Extract operation
                            |
     --------------------------------------------------
     |          |            |            |            |
  Count      Order        Best         Window       Boundary
     |          |            |            |            |
 unordered  set/map      heap         deque        stack
   map      multiset
     |
 previous states

     --------------------------------------------------
     |          |            |            |
  Intervals   Graph       Median       Offline
     |          |            |            |
 sweep line  queue/heap  two sets      sort queries
```

---

## Final Revision Sheet

Before every contest, revise:

```text
1. lower_bound / upper_bound meanings
2. multiset erase one copy
3. monotonic stack inequality matrix
4. deque window max template
5. prefix map formula
6. heap stale entry cleanup
7. sweep line tie-breaking
8. two multiset median rebalance
9. Dijkstra stale check
10. pre-submit checklist
```

---

# Final Message

STL mastery is not memorizing containers.

STL mastery is:

```text
Problem → Operation → Pattern → STL → Template → AC
```

If you train this ladder seriously, your contest brain becomes faster because every new problem starts looking like a known operation.

That is the real path from 800 to Candidate Master.
