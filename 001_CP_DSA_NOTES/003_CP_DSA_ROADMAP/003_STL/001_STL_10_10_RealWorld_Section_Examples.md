# MiniSTL_Master_10_10_Expanded.md

## 60–90 KB STL Pattern Engine for Codeforces CM + FAANG Interviews

> **Purpose:** Master STL as a problem-solving engine, not a syntax list.  
> **Level:** Beginner-friendly → CF Div2 A/B/C/D → Candidate Master readiness → FAANG interview patterns.  
> **Language:** C++17 only.

---

# Clickable Index

- [000. Core Mental Model](#000-core-mental-model)
- [001. STL Decision Engine](#001-stl-decision-engine)
- [002. Complexity and Container Choice](#002-complexity-and-container-choice)
- [003. Vector Engine](#003-vector-engine)
- [004. String Engine](#004-string-engine)
- [005. Pair Tuple Struct Engine](#005-pair-tuple-struct-engine)
- [006. Stack Engine](#006-stack-engine)
- [007. Queue Engine](#007-queue-engine)
- [008. Deque Engine](#008-deque-engine)
- [009. Priority Queue Engine](#009-priority-queue-engine)
- [010. Set Multiset Engine](#010-set-multiset-engine)
- [011. Map Unordered Map Engine](#011-map-unordered-map-engine)
- [012. Iterators and Invalidation](#012-iterators-and-invalidation)
- [013. Sort Comparator Engine](#013-sort-comparator-engine)
- [014. STL Binary Search](#014-stl-binary-search)
- [015. Frequency Pattern](#015-frequency-pattern)
- [016. Prefix Map Pattern](#016-prefix-map-pattern)
- [017. Two Pointers STL Pattern](#017-two-pointers-stl-pattern)
- [018. Sliding Window STL Pattern](#018-sliding-window-stl-pattern)
- [019. Monotonic Stack Variants](#019-monotonic-stack-variants)
- [020. Monotonic Deque Variants](#020-monotonic-deque-variants)
- [021. Priority Queue Advanced and Lazy Deletion](#021-priority-queue-advanced-and-lazy-deletion)
- [022. Multiset Advanced and Window Median](#022-multiset-advanced-and-window-median)
- [023. Range Mapping and Interval Merge](#023-range-mapping-and-interval-merge)
- [024. Sweep Line Engine](#024-sweep-line-engine)
- [025. Top-K and Kth Engine](#025-top-k-and-kth-engine)
- [026. Median Maintenance](#026-median-maintenance)
- [027. LRU Cache STL Design](#027-lru-cache-stl-design)
- [028. LFU Cache STL Design](#028-lfu-cache-stl-design)
- [029. CF A/B/C/D Forms](#029-cf-abcd-forms)
- [030. FAANG Forms](#030-faang-forms)
- [031. STL Bug Encyclopedia](#031-stl-bug-encyclopedia)
- [032. Debugging Playbook](#032-debugging-playbook)
- [033. Practice Ladder](#033-practice-ladder)
- [099. Final Cheat Sheet](#099-final-cheat-sheet)

---

# 000. Core Mental Model

STL is not about memorizing containers.

STL is about mapping a **needed operation** to the correct **machine**.

```text
Problem statement
      ↓
What operation must be fast?
      ↓
Which STL gives that operation?
      ↓
Which known pattern uses that STL?
      ↓
Can it survive constraints?
      ↓
Implement + debug
```

## One Sentence

> STL mastery means choosing the right data structure before writing code.

## The Operation Machine View

```text
vector          -> random access + sorting
string          -> character sequence
stack           -> last opened, first closed
queue           -> first discovered, first processed
deque           -> both ends + monotonic windows
priority_queue  -> best candidate repeatedly
set             -> ordered unique search
multiset        -> ordered duplicates
map             -> ordered key-value
unordered_map   -> average O(1) key-value
list            -> O(1) node movement when iterator known
```

## One Picture

```text
                    STL MASTER MAP
                           |
       -------------------------------------------------
       |             |              |                  |
   Sequence       Ordering        Hashing             Heap
       |             |              |                  |
 vector/string   set/map       unordered_map      priority_queue
 deque           multiset      unordered_set      top-k/dijkstra
       |
 stack/queue/list for flow and design
```

---



## 10/10 Deep Add-on: How To Think Before Choosing STL

Most STL mistakes happen before code. You pick a container because you remember syntax, not because you identified the operation.

```text
Wrong thinking:
    "This problem has numbers, use vector."

Correct thinking:
    "What operation repeats 2e5 times?"
```

### Operation-First Diagram

```text
Problem text
   |
   v
Repeated operation?
   |
   +--> count previous same thing      -> unordered_map
   +--> find nearest sorted value      -> set / multiset
   +--> get current minimum/maximum    -> priority_queue
   +--> remove expired window values   -> deque / multiset
   +--> close last opened thing        -> stack
   +--> process in discovery order     -> queue
   +--> split / merge intervals        -> set + multiset / sweep line
```

### 5-Second Contest Habit

```text
1. Underline verbs: count, choose, nearest, maximum, delete, expire, merge.
2. Underline constraints: N, Q, value range.
3. Ask: static or dynamic?
4. Ask: duplicates matter?
5. Ask: do I need order?
```
# 001. STL Decision Engine

## Master Decision Tree

```text
Need index access?
    -> vector / string

Need sorted after reading all input?
    -> vector + sort

Need dynamic ordered search?
    -> set / multiset / map

Need frequency lookup?
    -> unordered_map / map

Need current best repeatedly?
    -> priority_queue

Need next greater/smaller?
    -> monotonic stack

Need window max/min?
    -> monotonic deque

Need window median?
    -> two multisets

Need stream median?
    -> two heaps

Need LRU?
    -> list + unordered_map

Need LFU?
    -> unordered_map + frequency lists
```

## Operation Table

```text
Operation                              STL
--------------------------------------------------------
append + index                         vector
character scan                         string
sort and pair                          vector + sort
unique ordered values                   set
ordered duplicates                      multiset
fast average count                      unordered_map
ordered count                           map
smallest/largest repeatedly             priority_queue
BFS                                     queue
DFS iterative                           stack
sliding max/min                         deque
nearest greater boundary                stack
range overlap                           vector events + sort
median                                  two heaps / multiset
cache recency                           list + unordered_map
cache frequency                         unordered_map + list buckets
```

## Contest Recognition

```text
"previous same value"       -> unordered_map
"nearest >= x"              -> set.lower_bound
"erase one duplicate"       -> multiset.find + erase(iterator)
"top k"                     -> heap of size k
"next greater"              -> monotonic stack
"max in every window"       -> monotonic deque
"meeting rooms"             -> sweep line / min heap
"LRU cache"                 -> list + unordered_map
```

---



## 10/10 Deep Add-on: Story Word → STL Decision Map

```text
"frequency", "same", "duplicate"       -> unordered_map / vector freq
"nearest", "at least", "not greater"    -> set / multiset lower_bound
"top", "k largest", "current best"      -> priority_queue
"continuous segment", "at most K"        -> sliding window + map
"next greater", "nearest smaller"        -> monotonic stack
"window maximum"                         -> monotonic deque
"meeting rooms", "active users"          -> heap / sweep line
"cache", "recently used"                 -> list + unordered_map
"median after insert/delete"              -> two multisets
```

### Mini Decision Dry Run

```text
Statement: Give every customer the most expensive ticket <= budget.

Verbs: give, most expensive, <= budget, remove ticket.
Dynamic? yes, ticket removed.
Need order? yes.
Duplicates? yes.
Answer: multiset + upper_bound + erase(iterator).
```
# 002. Complexity and Container Choice

## Complexity Table

```text
vector index                         O(1)
vector push_back                     amortized O(1)
vector insert/erase middle/front     O(N)
sort                                 O(N log N)
lower_bound on vector                O(log N)

string substr                        O(length)
stack push/pop/top                   O(1)
queue push/pop/front                 O(1)
deque push_front/back                O(1)
deque pop_front/back                 O(1)

priority_queue top                   O(1)
priority_queue push/pop              O(log N)

set/map find/insert/erase            O(log N)
set/map lower_bound                  O(log N)
unordered_map average find/insert    O(1)
unordered_map worst case             O(N)

list insert/erase by iterator        O(1)
```

## Constraint Thinking

```text
N <= 2e5:
    O(N log N) is usually fine
    O(N) is excellent
    O(N^2) is usually dead
```

## Common TLE Translation

```text
vector erase front in loop:
    O(N) each * N = O(N^2)

sort inside loop:
    O(N log N) each * N = O(N^2 log N)

std::lower_bound on set:
    iterator traversal O(N), not O(logN)
```

Use:

```cpp
s.lower_bound(x);
```

not:

```cpp
lower_bound(s.begin(), s.end(), x);
```

---



## 10/10 Deep Add-on: Constraint → STL Survival Check

```text
N <= 100             O(N^3) sometimes okay
N <= 2,000           O(N^2) okay
N <= 2e5             O(N log N) usually okay
N <= 1e6             O(N) / O(N log N) with care
Q <= 2e5 dynamic     set/map/heap/Fenwick-style thinking
```

### TLE Diagram

```text
vector erase(begin()) inside loop

[1 2 3 4 5] erase 1 -> shift 4 items
[2 3 4 5]   erase 2 -> shift 3 items
[3 4 5]     erase 3 -> shift 2 items

Total shifts = O(N^2)
Use deque if removing from front.
```
# 003. Vector Engine

## Why This Exists

`vector` is the default CP container because it is simple, cache-friendly, sortable, and indexable.

## Core Mental Model

```text
index:  0   1   2   3
       [10][20][30][40]
```

## When To Use

```text
input array
sort values
prefix sum
DP
graph adjacency
binary search
coordinate compression
interval list
event list
```

## When Not To Use

```text
frequent erase from front
frequent insertion in middle
dynamic ordered search
```

## Basic Vector Code

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

    for (int x : a) cout << x << " ";
    cout << "\n";

    return 0;
}
```

## Dry Run

```text
Input:
5
4 1 7 2 2

read:
[4,1,7,2,2]

sort:
[1,2,2,4,7]
```

## Pattern 1: Prefix Sum

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

## Pattern 2: Coordinate Compression

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<int> a(n), vals;

    for (int i = 0; i < n; i++) {
        cin >> a[i];
        vals.push_back(a[i]);
    }

    sort(vals.begin(), vals.end());
    vals.erase(unique(vals.begin(), vals.end()), vals.end());

    for (int x : a) {
        int compressed = lower_bound(vals.begin(), vals.end(), x) - vals.begin();
        cout << compressed << " ";
    }

    return 0;
}
```

## Coordinate Compression Dry Run

```text
a = [100, 5, 100, 20]

vals sorted unique = [5,20,100]

5   -> 0
20  -> 1
100 -> 2

compressed array = [2,0,2,1]
```

## Bugs

```text
out of bounds index
forgetting long long for sums
erase from front repeatedly
not sorting before lower_bound
not using unique correctly:
    sort first, then erase(unique(...), end)
```

---



## 10/10 Real-World Example Pack: Vector as Sorted Search Machine

### Story

A store has product prices. Many customers ask how many products cost at most X.

### Remove The Story

```text
Business nouns disappear.
Keep only the operation:
sort once, answer first index greater than X
```

### Pattern Recognition

```text
Need: sort once, answer first index greater than X
Choose: vector + sort + upper_bound
Invariant: after every step, the container represents exactly the useful state.
```

### ASCII Mental Model

```text
prices unsorted:  [7, 1, 4, 4, 10]
sorted:           [1, 4, 4, 7, 10]
query X=4         upper_bound -> index 3
answer            3 products
```

### Step-by-Step Dry Run

```text
sort prices
query 4: first >4 is 7 at index 3 -> count=3
query 8: first >8 is 10 at index 4 -> count=4
```

### C++17 CP Template

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    int n,q; cin>>n>>q;
    vector<int> a(n);
    for(int &x:a) cin>>x;
    sort(a.begin(), a.end());
    while(q--){
        int x; cin>>x;
        int cnt = upper_bound(a.begin(), a.end(), x) - a.begin();
        cout << cnt << "\n";
    }
}
```

### Common WA / TLE Bugs

```text
forget sort before binary search
using lower_bound when question asks <= x
int overflow when storing prefix sums in int
```

### Codeforces Forms

```text
count <= x queries
coordinate compression
offline sort + pointer
pair after sorting
```

### FAANG Forms

```text
K closest values after sorting
random pick weight with prefix vector
merge intervals input storage
```
# 004. String Engine

## Core Mental Model

`string` is a vector-like container of characters.

```text
s = "abca"

index: 0 1 2 3
char:  a b c a
```

## Pattern Recognition

```text
frequency
palindrome
anagram
substring/window
stack parsing
rolling hash later
```

## Frequency Code

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
        if (freq[i]) {
            cout << char('a' + i) << " " << freq[i] << "\n";
        }
    }

    return 0;
}
```

## Palindrome Code

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

## Group Anagrams Key

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    unordered_map<string, vector<string>> groups;

    for (int i = 0; i < n; i++) {
        string s;
        cin >> s;

        string key = s;
        sort(key.begin(), key.end());

        groups[key].push_back(s);
    }

    for (auto &[key, words] : groups) {
        for (string &w : words) cout << w << " ";
        cout << "\n";
    }

    return 0;
}
```

## Bugs

```text
getline after cin issue
substr copies O(length)
assuming only lowercase when input may contain uppercase
signed char indexing issue for cnt[256]
```

---



## 10/10 Real-World Example Pack: String as Character Frequency and Window Machine

### Story

A login system receives session characters. Find the longest substring with no repeated character.

### Remove The Story

```text
Business nouns disappear.
Keep only the operation:
maintain a valid character window with no duplicate
```

### Pattern Recognition

```text
Need: maintain a valid character window with no duplicate
Choose: string + vector<int> count + sliding window
Invariant: after every step, the container represents exactly the useful state.
```

### ASCII Mental Model

```text
s = abca

window:
[a] valid
[a b] valid
[a b c] valid
[a b c a] duplicate a
 shrink -> [b c a]
```

### Step-by-Step Dry Run

```text
r=0 a count=1 best=1
r=1 b count=1 best=2
r=2 c count=1 best=3
r=3 a count=2 invalid
remove s[l]=a, l=1
window bca best=3
```

### C++17 CP Template

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    string s; cin>>s;
    vector<int> cnt(256,0);
    int l=0,best=0;
    for(int r=0;r<(int)s.size();r++){
        unsigned char ch=s[r];
        cnt[ch]++;
        while(cnt[ch]>1){
            cnt[(unsigned char)s[l]]--;
            l++;
        }
        best=max(best,r-l+1);
    }
    cout<<best<<"\n";
}
```

### Common WA / TLE Bugs

```text
using 26-size freq when input has uppercase/digits
forgeting to shrink until valid
substr inside nested loops causing O(N^3)
```

### Codeforces Forms

```text
longest distinct substring
count anagrams
palindrome check
minimum window characters
```

### FAANG Forms

```text
Longest Substring Without Repeat
Group Anagrams
Minimum Window Substring
Valid Parentheses on string
```
# 005. Pair Tuple Struct Engine

## Core Mental Model

Group related values.

```text
(value, index)
(distance, node)
(start, end)
(x, y)
```

## Default Pair Sorting

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<pair<int,int>> v = {{3,10}, {1,50}, {3,5}};

    sort(v.begin(), v.end());

    for (auto [a,b] : v) {
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

## Custom Struct Sorting

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
        {90,20,"Ali"},
        {90,18,"Bob"},
        {95,19,"Cara"}
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

## Bugs

```text
comparator must be strict
never return a <= b
avoid return a.score - b.score due to overflow
```

---



## 10/10 Real-World Example Pack: Pair as Sortable Object

### Story

A ranking page sorts players by score descending, and if tied, by lower penalty.

### Remove The Story

```text
Business nouns disappear.
Keep only the operation:
sort objects by multiple keys
```

### Pattern Recognition

```text
Need: sort objects by multiple keys
Choose: vector<pair/struct> + custom comparator
Invariant: after every step, the container represents exactly the useful state.
```

### ASCII Mental Model

```text
players:
Ali  score=90 penalty=20
Bob  score=90 penalty=10
Cara score=95 penalty=30

order: Cara, Bob, Ali
```

### Step-by-Step Dry Run

```text
compare Cara vs Ali -> 95 > 90, Cara first
Ali vs Bob score tie -> penalty 10 wins, Bob before Ali
```

### C++17 CP Template

```cpp
#include <bits/stdc++.h>
using namespace std;
struct Player{string name; int score, penalty;};
int main(){
    vector<Player> v={{"Ali",90,20},{"Bob",90,10},{"Cara",95,30}};
    sort(v.begin(), v.end(), [](const Player&a,const Player&b){
        if(a.score!=b.score) return a.score>b.score;
        return a.penalty<b.penalty;
    });
    for(auto &p:v) cout<<p.name<<"\n";
}
```

### Common WA / TLE Bugs

```text
comparator returns <= not <
subtracting values may overflow
forget tie-breaker creates unstable-looking output
```

### Codeforces Forms

```text
sort by score/index
sort intervals
sort events with type tie-break
store distance,node in Dijkstra
```

### FAANG Forms

```text
Meeting Rooms intervals
K closest points
Task scheduling objects
Leaderboard ranking
```
# 006. Stack Engine

## Core Mental Model

Last in, first out.

```text
push 1
push 2
push 3

top = 3

bottom [1 2 3] top
```

## Pattern Recognition

```text
valid parentheses
undo last
nested structures
next greater/smaller
histogram
DFS iterative
```

## Valid Parentheses

```cpp
#include <bits/stdc++.h>
using namespace std;

bool match(char a, char b) {
    return (a == '(' && b == ')') ||
           (a == '[' && b == ']') ||
           (a == '{' && b == '}');
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
read ']' -> top [ matches, pop -> (
read ')' -> top ( matches, pop -> empty
valid
```

## Bugs

```text
top on empty stack
forget final empty check
confusing open and close matching
```

---



## 10/10 Real-World Example Pack: Stack as Unresolved Boundary Machine

### Story

Daily temperatures arrive. For each day, find how many days until a warmer day.

### Remove The Story

```text
Business nouns disappear.
Keep only the operation:
resolve previous smaller values when a bigger value appears
```

### Pattern Recognition

```text
Need: resolve previous smaller values when a bigger value appears
Choose: monotonic stack of indices
Invariant: after every step, the container represents exactly the useful state.
```

### ASCII Mental Model

```text
temp: 73 71 75 72
stack unresolved indices
73 waits
71 waits
75 resolves 71 and 73
72 waits
```

### Step-by-Step Dry Run

```text
i=0 push 73
i=1 71 not warmer than 73 push
i=2 75 warmer than 71 -> ans[1]=1
    75 warmer than 73 -> ans[0]=2
    push 75
i=3 push 72
```

### C++17 CP Template

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin>>n; vector<int> t(n), ans(n,0);
    for(int &x:t) cin>>x;
    stack<int> st;
    for(int i=0;i<n;i++){
        while(!st.empty() && t[st.top()]<t[i]){
            int j=st.top(); st.pop();
            ans[j]=i-j;
        }
        st.push(i);
    }
    for(int x:ans) cout<<x<<" ";
}
```

### Common WA / TLE Bugs

```text
top on empty
wrong inequality with duplicates
storing value instead of index when distance is needed
```

### Codeforces Forms

```text
next greater element
nearest smaller boundary
histogram rectangle
parentheses/nesting
```

### FAANG Forms

```text
Daily Temperatures
Largest Rectangle Histogram
Valid Parentheses
Min Stack
```
# 007. Queue Engine

## Core Mental Model

First in, first out.

```text
front -> [A][B][C] <- back
```

## Pattern Recognition

```text
BFS
level order
multi-source expansion
simulation
shortest path in unweighted graph/grid
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

## Multi-Source BFS Mental Model

```text
push all sources with dist=0
then normal BFS expands waves
```

```text
S . . .
. . . .
. . S .

Both S expand together.
```

---



## 10/10 Real-World Example Pack: Queue as BFS Wave Machine

### Story

In a social network, find minimum friendship hops from source user to every other user.

### Remove The Story

```text
Business nouns disappear.
Keep only the operation:
process nodes in discovery order, first visit is shortest
```

### Pattern Recognition

```text
Need: process nodes in discovery order, first visit is shortest
Choose: queue + vector distance
Invariant: after every step, the container represents exactly the useful state.
```

### ASCII Mental Model

```text
source S
level 0: S
level 1: friends of S
level 2: friends of friends

queue preserves wave order
```

### Step-by-Step Dry Run

```text
dist[S]=0 push S
pop S -> push A,B with dist=1
pop A -> push C with dist=2
first time reaching C is shortest
```

### C++17 CP Template

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n,m; cin>>n>>m;
    vector<vector<int>> g(n);
    for(int i=0;i<m;i++){int u,v;cin>>u>>v;--u;--v;g[u].push_back(v);g[v].push_back(u);}
    vector<int> dist(n,-1); queue<int> q;
    dist[0]=0; q.push(0);
    while(!q.empty()){
        int u=q.front(); q.pop();
        for(int v:g[u]) if(dist[v]==-1){dist[v]=dist[u]+1; q.push(v);}
    }
    for(int d:dist) cout<<d<<" ";
}
```

### Common WA / TLE Bugs

```text
not marking visited before push causing duplicates
using BFS on weighted graph
forget multi-source initialization
```

### Codeforces Forms

```text
shortest path unweighted
grid level expansion
fire/monster spread
topological queue
```

### FAANG Forms

```text
Word Ladder
Rotting Oranges
Shortest Path in Binary Matrix
Course Schedule topo
```
# 008. Deque Engine

## Core Mental Model

Deque supports both ends.

```text
push_front -> [ ][ ][ ] <- push_back
pop_front  -> [ ][ ][ ] <- pop_back
```

## Uses

```text
sliding window max/min
0-1 BFS
two-ended simulation
monotonic deque
```

## Basic Code

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

## 0-1 BFS Preview

When edge weights are only 0 or 1:

```text
weight 0 -> push_front
weight 1 -> push_back
```

This gives Dijkstra-like behavior in O(V+E).

---



## 10/10 Real-World Example Pack: Deque as Window Maximum Machine

### Story

A monitoring dashboard shows max CPU usage in every 5-minute window.

### Remove The Story

```text
Business nouns disappear.
Keep only the operation:
keep candidates in decreasing order and expire old indices
```

### Pattern Recognition

```text
Need: keep candidates in decreasing order and expire old indices
Choose: monotonic deque
Invariant: after every step, the container represents exactly the useful state.
```

### ASCII Mental Model

```text
values: 2 1 3 4
k=3

dq stores indices whose values decrease
when 3 arrives, 1 and 2 are useless -> pop back
```

### Step-by-Step Dry Run

```text
i=0 dq=[0:2]
i=1 dq=[0:2,1:1]
i=2 value=3 pop 1, pop 0, dq=[2:3], answer=3
i=3 value=4 pop 2, dq=[3:4], answer=4
```

### C++17 CP Template

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n,k; cin>>n>>k; vector<int>a(n); for(int&x:a)cin>>x;
    deque<int> dq;
    for(int i=0;i<n;i++){
        while(!dq.empty() && dq.front()<=i-k) dq.pop_front();
        while(!dq.empty() && a[dq.back()]<=a[i]) dq.pop_back();
        dq.push_back(i);
        if(i>=k-1) cout<<a[dq.front()]<<" ";
    }
}
```

### Common WA / TLE Bugs

```text
storing values instead of indices
expiring after answering
wrong duplicate inequality for required behavior
```

### Codeforces Forms

```text
sliding max/min
0-1 BFS
front/back simulation
recent timestamp window
```

### FAANG Forms

```text
Sliding Window Maximum
Hit Counter
Moving Average
0-1 shortest path variants
```
# 009. Priority Queue Engine

## Core Mental Model

Heap gives best element repeatedly.

```text
priority_queue<int> = max heap
top() = largest
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

## Min Heap

```cpp
priority_queue<int, vector<int>, greater<int>> pq;
```

## Custom Heap For Pair

```cpp
priority_queue<pair<int,int>, vector<pair<int,int>>, greater<pair<int,int>>> pq;
```

This gives smallest pair lexicographically.

## Pattern Recognition

```text
top k
kth largest
Dijkstra
merge k sorted lists
scheduling
always choose cheapest/largest
```

## Bugs

```text
default is max heap
top on empty
forget stale entries in Dijkstra
custom comparator backwards
```

---



## 10/10 Real-World Example Pack: Heap as Current Best Machine

### Story

A server always processes the currently shortest available job.

### Remove The Story

```text
Business nouns disappear.
Keep only the operation:
repeatedly extract minimum processing time
```

### Pattern Recognition

```text
Need: repeatedly extract minimum processing time
Choose: min priority_queue
Invariant: after every step, the container represents exactly the useful state.
```

### ASCII Mental Model

```text
jobs: A=8, B=2, C=5
heap top is B
process B, then C, then A
```

### Step-by-Step Dry Run

```text
push (8,A)
push (2,B) -> top B
push (5,C) -> top B
pop B -> top C
pop C -> top A
```

### C++17 CP Template

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    priority_queue<pair<int,string>, vector<pair<int,string>>, greater<pair<int,string>>> pq;
    pq.push({8,"A"}); pq.push({2,"B"}); pq.push({5,"C"});
    while(!pq.empty()){
        auto [t,id]=pq.top(); pq.pop();
        cout<<id<<" ";
    }
}
```

### Common WA / TLE Bugs

```text
default heap is max heap
forget greater<> for min heap
not handling stale entries in graph/top-k updates
```

### Codeforces Forms

```text
k largest
Dijkstra
minimum rooms
scheduling
lazy deletion
```

### FAANG Forms

```text
Merge K Lists
Kth Largest
Network Delay Time
Task Scheduler
```
# 010. Set Multiset Engine

## Core Mental Model

```text
set      = sorted unique
multiset = sorted duplicates
```

## Ordered Search

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

## Previous Value

```cpp
auto it = s.lower_bound(x);
if (it != s.begin()) {
    --it;
    cout << *it << "\n";
}
```

## Erase One Copy From Multiset

```cpp
auto it = ms.find(x);
if (it != ms.end()) ms.erase(it);
```

## Danger

```cpp
ms.erase(x);
```

removes all copies.

## Use Cases

```text
nearest value
dynamic sorted data
window min/max with deletion
median
ordered duplicates
```

---



## 10/10 Real-World Example Pack: Multiset as Dynamic Ticket Machine

### Story

Customers ask for the most expensive ticket they can afford. Sold ticket disappears.

### Remove The Story

```text
Business nouns disappear.
Keep only the operation:
find largest value <= x with duplicates and deletion
```

### Pattern Recognition

```text
Need: find largest value <= x with duplicates and deletion
Choose: multiset + upper_bound + erase(iterator)
Invariant: after every step, the container represents exactly the useful state.
```

### ASCII Mental Model

```text
tickets: 5 7 7 10
request x=8
upper_bound(8) -> 10
previous -> 7
erase one 7
remaining: 5 7 10
```

### Step-by-Step Dry Run

```text
x=8 it=first >8 = 10
--it gives 7
erase iterator only one copy
next x=7 gives remaining 7
```

### C++17 CP Template

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n,q; cin>>n>>q; multiset<int> ms;
    for(int i=0;i<n;i++){int x;cin>>x;ms.insert(x);}
    while(q--){
        int x; cin>>x;
        auto it=ms.upper_bound(x);
        if(it==ms.begin()) cout<<-1<<"\n";
        else{--it; cout<<*it<<"\n"; ms.erase(it);}
    }
}
```

### Common WA / TLE Bugs

```text
using set when duplicates exist
ms.erase(value) removes all copies
decrement begin
dereference end
```

### Codeforces Forms

```text
Concert Tickets
nearest value
dynamic MEX
sliding window ordered values
```

### FAANG Forms

```text
My Calendar
Exam Room
Range Module
Contains Nearby Almost Duplicate
```
# 011. Map Unordered Map Engine

## Core Mental Model

```text
key -> value
```

## map

```text
ordered keys
O(logN)
lower_bound available
```

## unordered_map

```text
hash table
average O(1)
no sorted order
```

## Frequency Code

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

    for (auto &[key, val] : freq) {
        cout << key << " " << val << "\n";
    }

    return 0;
}
```

## Custom Hash For Safety

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

unordered_map<long long, int, custom_hash> safeMap;
```

## Bugs

```text
operator[] creates key
unordered_map worst-case can TLE
map slower but ordered
```

---



## 10/10 Real-World Example Pack: Hash Map as Previous State Memory

### Story

A fraud system counts how many transaction pairs have the same account id.

### Remove The Story

```text
Business nouns disappear.
Keep only the operation:
count previous equal key
```

### Pattern Recognition

```text
Need: count previous equal key
Choose: unordered_map<key,count>
Invariant: after every step, the container represents exactly the useful state.
```

### ASCII Mental Model

```text
ids: A B A A

A old=0 pairs+=0
B old=0 pairs+=0
A old=1 pairs+=1
A old=2 pairs+=2
total=3
```

### Step-by-Step Dry Run

```text
freq empty
read A -> ans 0 freq[A]=1
read B -> ans 0 freq[B]=1
read A -> ans 1 freq[A]=2
read A -> ans 3 freq[A]=3
```

### C++17 CP Template

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; cin>>n; unordered_map<string,long long> freq;
    long long ans=0;
    for(int i=0;i<n;i++){ string x; cin>>x; ans+=freq[x]; freq[x]++; }
    cout<<ans<<"\n";
}
```

### Common WA / TLE Bugs

```text
answer overflow if int
operator[] creates key accidentally
unordered_map collision TLE without reserve/custom hash in hard CF
```

### Codeforces Forms

```text
frequency pairs
two sum
prefix sum count
group by key
```

### FAANG Forms

```text
Two Sum
Group Anagrams
Subarray Sum Equals K
Logger Rate Limiter
```
# 012. Iterators and Invalidation

## Core Mental Model

Iterator is pointer-like.

```text
[ a b c d )
^       ^
begin   end
```

## Important Rules

```text
end() is not valid to dereference
begin() cannot be decremented
vector erase invalidates iterators at/after erase point
set/map erase invalidates only erased iterator
list erase invalidates erased iterator only
```

## Vector Lower Bound Index

```cpp
int idx = lower_bound(a.begin(), a.end(), x) - a.begin();
```

## Set Lower Bound

```cpp
auto it = s.lower_bound(x);
```

Do not subtract set iterators.

## Safe Erase While Iterating Map

```cpp
for (auto it = mp.begin(); it != mp.end(); ) {
    if (it->second == 0) {
        it = mp.erase(it);
    } else {
        ++it;
    }
}
```

---



## 10/10 Real-World Example Pack: Iterator as Safe Handle

### Story

A multiset stores duplicate prices. You must remove only the selected sold ticket.

### Remove The Story

```text
Business nouns disappear.
Keep only the operation:
find exact iterator and erase only that node
```

### Pattern Recognition

```text
Need: find exact iterator and erase only that node
Choose: multiset iterator erase
Invariant: after every step, the container represents exactly the useful state.
```

### ASCII Mental Model

```text
ms = {7,7,7}
ms.erase(7)       -> removes all 7s
it=ms.find(7)
ms.erase(it)      -> removes one 7
```

### Step-by-Step Dry Run

```text
find first 7 -> iterator points to one node
erase(iterator) removes one node
other equal nodes remain valid
```

### C++17 CP Template

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    multiset<int> ms={7,7,7};
    auto it=ms.find(7);
    if(it!=ms.end()) ms.erase(it);
    cout<<ms.size()<<"\n"; // 2
}
```

### Common WA / TLE Bugs

```text
dereference end()
--begin()
using erased iterator
vector erase invalidates following iterators
```

### Codeforces Forms

```text
safe multiset deletion
map cleanup loop
lower_bound boundary checks
traffic lights segment deletion
```

### FAANG Forms

```text
LRU list iterators
Range Module map iterators
Calendar interval neighbors
```
# 013. Sort Comparator Engine

## Why Sorting Matters

Sorting creates structure.

```text
chaos -> order -> binary search/two pointers/greedy
```

## Comparator Rules

Comparator must answer:

```text
Should a come before b?
```

It must be strict.

Bad:

```cpp
return a <= b;
```

Good:

```cpp
return a < b;
```

## Multi-Key Sort

```cpp
sort(v.begin(), v.end(), [](auto &a, auto &b) {
    if (a.first != b.first) return a.first < b.first;
    return a.second > b.second;
});
```

## Interval Sort

```cpp
sort(intervals.begin(), intervals.end(), [](auto &a, auto &b) {
    if (a.first != b.first) return a.first < b.first;
    return a.second < b.second;
});
```

## Bugs

```text
non-strict comparator
inconsistent comparator
overflow in subtraction comparator
forget tie-breaker
```

---



## 10/10 Real-World Example Pack: Comparator as Greedy Rule

### Story

A meeting planner wants maximum non-overlapping meetings in one room.

### Remove The Story

```text
Business nouns disappear.
Keep only the operation:
sort by earliest finishing time
```

### Pattern Recognition

```text
Need: sort by earliest finishing time
Choose: vector intervals + comparator
Invariant: after every step, the container represents exactly the useful state.
```

### ASCII Mental Model

```text
meetings:
A [1,4]
B [2,3]
C [3,5]

sort by end: B, A, C
choose B then C
```

### Step-by-Step Dry Run

```text
lastEnd=-inf
B starts 2 >= -inf choose, lastEnd=3
A starts 1 <3 skip
C starts 3 >=3 choose
```

### C++17 CP Template

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    vector<pair<int,int>> v={{1,4},{2,3},{3,5}};
    sort(v.begin(), v.end(), [](auto&a,auto&b){return a.second<b.second;});
    int last=-1e9, ans=0;
    for(auto [s,e]:v) if(s>=last){ans++; last=e;}
    cout<<ans<<"\n";
}
```

### Common WA / TLE Bugs

```text
wrong greedy sort key
non-strict comparator
not defining interval boundary rule: s>=last or s>last
```

### Codeforces Forms

```text
activity selection
deadline sorting
event tie-breaking
custom ranking
```

### FAANG Forms

```text
Merge Intervals
Meeting Rooms
Queue Reconstruction
Non-overlapping Intervals
```
# 014. STL Binary Search

## Core Mental Model

```text
lower_bound = first >= x
upper_bound = first > x
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

## Dry Run

```text
a=[1,2,2,2,4,7], x=2

lower_bound -> index 1
upper_bound -> index 4
count = 3
```

## First Greater Than X

```cpp
auto it = upper_bound(a.begin(), a.end(), x);
```

## Last Less Than X

```cpp
auto it = lower_bound(a.begin(), a.end(), x);
if (it != a.begin()) {
    --it;
    cout << *it;
}
```

---



## 10/10 Real-World Example Pack: Binary Search as First Valid Position

### Story

A warehouse has sorted box sizes. For each item size x, find the first box that can fit it.

### Remove The Story

```text
Business nouns disappear.
Keep only the operation:
first value >= x
```

### Pattern Recognition

```text
Need: first value >= x
Choose: lower_bound on sorted vector
Invariant: after every step, the container represents exactly the useful state.
```

### ASCII Mental Model

```text
boxes: 2 4 4 7 10
item x=5
lower_bound(5) -> 7

item x=4
lower_bound(4) -> first 4
```

### Step-by-Step Dry Run

```text
x=5 search range all
first index whose value >=5 is index 3
answer boxes[3]=7
```

### C++17 CP Template

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    vector<int> box={2,4,4,7,10};
    int x; cin>>x;
    auto it=lower_bound(box.begin(), box.end(), x);
    if(it==box.end()) cout<<"none\n";
    else cout<<*it<<"\n";
}
```

### Common WA / TLE Bugs

```text
not sorted
confusing lower_bound and upper_bound
using std::lower_bound on set instead of s.lower_bound
```

### Codeforces Forms

```text
count <= x
first >= x
coordinate compression
offline query threshold
```

### FAANG Forms

```text
Random Pick Weight
Snapshot Array
Time Based Key Value Store
Search Insert Position
```
# 015. Frequency Pattern

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
    long long ans = 0;

    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;

        ans += freq[x];
        freq[x]++;
    }

    cout << ans << "\n";
    return 0;
}
```

## Dry Run

```text
a=[5,5,2,5]

x=5 old freq=0 ans=0
x=5 old freq=1 ans=1
x=2 old freq=0 ans=1
x=5 old freq=2 ans=3
```

## Variants

```text
same value
same remainder
same normalized difference
same sorted-string key
same prefix sum
```

---



## 10/10 Real-World Example Pack: Frequency as Bucket Counting

### Story

Requests are assigned to servers by requestId % k. Count request pairs landing on same server.

### Remove The Story

```text
Business nouns disappear.
Keep only the operation:
count previous same remainder
```

### Pattern Recognition

```text
Need: count previous same remainder
Choose: vector<long long> freq(k)
Invariant: after every step, the container represents exactly the useful state.
```

### ASCII Mental Model

```text
ids: 4 7 10 13, k=3
remainders: 1 1 1 1
pairs: 0+1+2+3=6
```

### Step-by-Step Dry Run

```text
read 4 r=1 old=0 ans=0 freq=1
read 7 r=1 old=1 ans=1 freq=2
read 10 old=2 ans=3
read 13 old=3 ans=6
```

### C++17 CP Template

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n,k; cin>>n>>k; vector<long long> freq(k); long long ans=0;
    for(int i=0;i<n;i++){ long long x; cin>>x; int r=((x%k)+k)%k; ans+=freq[r]; freq[r]++; }
    cout<<ans<<"\n";
}
```

### Common WA / TLE Bugs

```text
negative modulo not normalized
using map when k small vector is faster
int overflow in pair count
```

### Codeforces Forms

```text
same value pairs
same modulo pairs
complement pairs
frequency after normalization
```

### FAANG Forms

```text
Two Sum variants
Subarray divisible by K
Group shifted strings
Anagram grouping
```
# 016. Prefix Map Pattern

## Core Mental Model

```text
sum(l..r) = pref[r] - pref[l-1]
```

To find sum K:

```text
pref[l-1] = pref[r] - K
```

## Count Subarrays Sum K

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

pref=1 need=-2 ans=0
pref=3 need=0  ans=1
pref=4 need=1  ans=2
pref=6 need=3  ans=3
```

## Variants

```text
subarray divisible by k:
    use pref % k frequency

equal 0/1 count:
    convert 0 to -1, count prefix equal

longest subarray:
    store first index of prefix
```

---



## 10/10 Real-World Example Pack: Prefix as Past State Lookup

### Story

Bank daily deltas are given. Count continuous periods whose total delta is exactly K.

### Remove The Story

```text
Business nouns disappear.
Keep only the operation:
current prefix needs previous prefix = pref - K
```

### Pattern Recognition

```text
Need: current prefix needs previous prefix = pref - K
Choose: unordered_map<prefix,count>
Invariant: after every step, the container represents exactly the useful state.
```

### ASCII Mental Model

```text
a: 1 2 3 -2, K=3
prefix: 0 1 3 6 4
at pref=3 need 0 -> found [1,2]
at pref=6 need 3 -> found [3]
```

### Step-by-Step Dry Run

```text
freq[0]=1
pref=1 need=-2 ans=0
pref=3 need=0 ans=1
pref=6 need=3 ans=2
pref=4 need=1 ans=3
```

### C++17 CP Template

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n; long long k; cin>>n>>k;
    unordered_map<long long,long long> freq; freq[0]=1;
    long long pref=0, ans=0;
    for(int i=0;i<n;i++){ long long x; cin>>x; pref+=x; ans+=freq[pref-k]; freq[pref]++; }
    cout<<ans<<"\n";
}
```

### Common WA / TLE Bugs

```text
forget freq[0]=1
using sliding window when negatives exist
int prefix overflow
```

### Codeforces Forms

```text
count subarray sum K
longest balanced 0/1
subarrays divisible by k
prefix xor
```

### FAANG Forms

```text
Subarray Sum Equals K
Contiguous Array
Path Sum prefix
Continuous Subarray Sum
```
# 017. Two Pointers STL Pattern

## Core Mental Model

Both pointers move monotonically.

```text
l -> right
r -> left/right
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

## Dry Run

```text
a=[1,2,4,7,9,11], target=13

l=0 r=5 sum=12 <13 -> l++
l=1 r=5 sum=13 -> found
```

---



## 10/10 Real-World Example Pack: Two Pointers as Sorted Pair Machine

### Recognition First

```text
Before coding, ask:
1. What is changing after each step?
2. What must be queried fast?
3. What invariant must remain true?
```

### ASCII Mental Model

```text
Input stream / operations
        |
        v
Maintain useful state only
        |
        v
Answer from container top/boundary/middle
```

### Deep Dry-Run Habit

```text
For every operation write:
before state
operation effect
expired/deleted items
rebalanced state
answer
```

### Contest Debug Checklist

```text
Boundary empty?
Duplicate values?
Need index or value?
Is deletion exact or lazy?
Does answer require before or after update?
```

### Real-World Forms

```text
Backend: logs, caches, sessions, schedulers, intervals, dashboards
CF: dynamic queries, windows, nearest values, active intervals, top candidates
FAANG: design data structures, calendar, cache, median, top-k, stream processing
```
# 018. Sliding Window STL Pattern

## Core Mental Model

Contiguous window.

```text
expand right
shrink left until valid
```

## Longest Substring Without Repeat

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

## Bugs

```text
sliding window sum fails with negatives
wrong while condition
forget to decrement leaving char
```

---



## 10/10 Real-World Example Pack: Sliding Window as Valid Segment Machine

### Recognition First

```text
Before coding, ask:
1. What is changing after each step?
2. What must be queried fast?
3. What invariant must remain true?
```

### ASCII Mental Model

```text
Input stream / operations
        |
        v
Maintain useful state only
        |
        v
Answer from container top/boundary/middle
```

### Deep Dry-Run Habit

```text
For every operation write:
before state
operation effect
expired/deleted items
rebalanced state
answer
```

### Contest Debug Checklist

```text
Boundary empty?
Duplicate values?
Need index or value?
Is deletion exact or lazy?
Does answer require before or after update?
```

### Real-World Forms

```text
Backend: logs, caches, sessions, schedulers, intervals, dashboards
CF: dynamic queries, windows, nearest values, active intervals, top candidates
FAANG: design data structures, calendar, cache, median, top-k, stream processing
```
# 019. Monotonic Stack Variants

## Core Mental Model

Monotonic stack stores unresolved candidates.

```text
Each element enters once and leaves once.
```

## Variant 1: Next Greater Right

```cpp
while (!st.empty() && a[st.top()] < a[i]) {
    ans[st.top()] = a[i];
    st.pop();
}
st.push(i);
```

## Full Code

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

## Variant 2: Previous Smaller

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
        while (!st.empty() && a[st.top()] >= a[i]) {
            st.pop();
        }

        if (!st.empty()) ans[i] = a[st.top()];

        st.push(i);
    }

    for (int x : ans) cout << x << " ";
    return 0;
}
```

## Variant 3: Daily Temperatures

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<int> t(n), ans(n, 0);
    for (int &x : t) cin >> x;

    stack<int> st;

    for (int i = 0; i < n; i++) {
        while (!st.empty() && t[st.top()] < t[i]) {
            int j = st.top();
            st.pop();
            ans[j] = i - j;
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

i=0 stack=[]
push 0

i=1 a[0]=2 < 1 no
push 1

i=2 a[1]=1 < 5 -> ans[1]=5 pop
    a[0]=2 < 5 -> ans[0]=5 pop
push 2

i=3 a[2]=5 < 3 no
push 3

ans=[5,5,-1,-1]
```

## Largest Rectangle Histogram Template

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<long long> h(n);
    for (auto &x : h) cin >> x;

    stack<int> st;
    long long best = 0;

    for (int i = 0; i <= n; i++) {
        long long cur = (i == n ? 0 : h[i]);

        while (!st.empty() && h[st.top()] >= cur) {
            long long height = h[st.top()];
            st.pop();

            int left = st.empty() ? -1 : st.top();
            long long width = i - left - 1;

            best = max(best, height * width);
        }

        st.push(i);
    }

    cout << best << "\n";
    return 0;
}
```

## Recognition

```text
nearest greater/smaller
first warmer day
span
histogram
subarray min/max contribution
```

---



## 10/10 Real-World Example Pack: Monotonic Stack as Boundary Engine

### Recognition First

```text
Before coding, ask:
1. What is changing after each step?
2. What must be queried fast?
3. What invariant must remain true?
```

### ASCII Mental Model

```text
Input stream / operations
        |
        v
Maintain useful state only
        |
        v
Answer from container top/boundary/middle
```

### Deep Dry-Run Habit

```text
For every operation write:
before state
operation effect
expired/deleted items
rebalanced state
answer
```

### Contest Debug Checklist

```text
Boundary empty?
Duplicate values?
Need index or value?
Is deletion exact or lazy?
Does answer require before or after update?
```

### Real-World Forms

```text
Backend: logs, caches, sessions, schedulers, intervals, dashboards
CF: dynamic queries, windows, nearest values, active intervals, top candidates
FAANG: design data structures, calendar, cache, median, top-k, stream processing
```
# 020. Monotonic Deque Variants

## Core Mental Model

Deque keeps useful candidates for a moving window.

For maximum:

```text
values in deque are decreasing
front is maximum
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
        while (!dq.empty() && dq.front() <= i - k) dq.pop_front();

        while (!dq.empty() && a[dq.back()] <= a[i]) dq.pop_back();

        dq.push_back(i);

        if (i >= k - 1) {
            cout << a[dq.front()] << " ";
        }
    }

    return 0;
}
```

## Sliding Window Minimum

Change condition:

```cpp
while (!dq.empty() && a[dq.back()] >= a[i]) dq.pop_back();
```

## Dry Run

```text
a=[1,3,-1,-3,5], k=3

i=0 dq=[1]
i=1 pop 1, dq=[3]
i=2 dq=[3,-1], output 3
i=3 dq=[3,-1,-3], output 3
i=4 remove expired 3, pop -3,-1, dq=[5], output 5
```

## Bugs

```text
store indices not values
remove expired before output
<= vs < affects duplicates but both can be valid depending use
```

---



## 10/10 Real-World Example Pack: Monotonic Deque as Expiring Best Engine

### Recognition First

```text
Before coding, ask:
1. What is changing after each step?
2. What must be queried fast?
3. What invariant must remain true?
```

### ASCII Mental Model

```text
Input stream / operations
        |
        v
Maintain useful state only
        |
        v
Answer from container top/boundary/middle
```

### Deep Dry-Run Habit

```text
For every operation write:
before state
operation effect
expired/deleted items
rebalanced state
answer
```

### Contest Debug Checklist

```text
Boundary empty?
Duplicate values?
Need index or value?
Is deletion exact or lazy?
Does answer require before or after update?
```

### Real-World Forms

```text
Backend: logs, caches, sessions, schedulers, intervals, dashboards
CF: dynamic queries, windows, nearest values, active intervals, top candidates
FAANG: design data structures, calendar, cache, median, top-k, stream processing
```
# 021. Priority Queue Advanced and Lazy Deletion

## Why Lazy Deletion Exists

`priority_queue` cannot erase arbitrary elements.

Solution:

```text
mark deleted
remove when stale item reaches top
```

## Lazy Max Example

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    priority_queue<int> pq;
    unordered_map<int,int> deleted;

    pq.push(5);
    pq.push(10);
    pq.push(7);

    deleted[10]++;

    while (!pq.empty() && deleted[pq.top()] > 0) {
        deleted[pq.top()]--;
        pq.pop();
    }

    cout << pq.top() << "\n"; // 7
    return 0;
}
```

## Dijkstra Stale Entry Pattern

```cpp
if (d != dist[u]) continue;
```

## Use Cases

```text
sliding window heap
Dijkstra
online max after deletion
frequency heap
top-k with updates
```

## Bugs

```text
not cleaning before using top
deleted count mismatch
heap memory grows
```

---



## 10/10 Real-World Example Pack: Lazy Heap as Stale Candidate Filter

### Recognition First

```text
Before coding, ask:
1. What is changing after each step?
2. What must be queried fast?
3. What invariant must remain true?
```

### ASCII Mental Model

```text
Input stream / operations
        |
        v
Maintain useful state only
        |
        v
Answer from container top/boundary/middle
```

### Deep Dry-Run Habit

```text
For every operation write:
before state
operation effect
expired/deleted items
rebalanced state
answer
```

### Contest Debug Checklist

```text
Boundary empty?
Duplicate values?
Need index or value?
Is deletion exact or lazy?
Does answer require before or after update?
```

### Real-World Forms

```text
Backend: logs, caches, sessions, schedulers, intervals, dashboards
CF: dynamic queries, windows, nearest values, active intervals, top candidates
FAANG: design data structures, calendar, cache, median, top-k, stream processing
```
# 022. Multiset Advanced and Window Median

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
            auto it = ms.find(a[i-k]);
            ms.erase(it);
        }

        if (i >= k - 1) {
            cout << *ms.begin() << " " << *ms.rbegin() << "\n";
        }
    }

    return 0;
}
```

## Two Multisets Median

```cpp
#include <bits/stdc++.h>
using namespace std;

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

int main() {
    int n, k;
    cin >> n >> k;

    vector<int> a(n);
    for (int &x : a) cin >> x;

    for (int i = 0; i < n; i++) {
        add(a[i]);

        if (i >= k) removeOne(a[i-k]);

        if (i >= k - 1) {
            cout << *low.rbegin() << " ";
        }
    }

    return 0;
}
```

## Dry Run

```text
a=[1,5,2,7], k=3

window [1,5,2] sorted [1,2,5]
low=[1,2], high=[5], median=2

slide to [5,2,7]
remove 1, add 7
sorted [2,5,7]
low=[2,5], high=[7], median=5
```

---



## 10/10 Real-World Example Pack: Two Multisets as Dynamic Median Machine

### Recognition First

```text
Before coding, ask:
1. What is changing after each step?
2. What must be queried fast?
3. What invariant must remain true?
```

### ASCII Mental Model

```text
Input stream / operations
        |
        v
Maintain useful state only
        |
        v
Answer from container top/boundary/middle
```

### Deep Dry-Run Habit

```text
For every operation write:
before state
operation effect
expired/deleted items
rebalanced state
answer
```

### Contest Debug Checklist

```text
Boundary empty?
Duplicate values?
Need index or value?
Is deletion exact or lazy?
Does answer require before or after update?
```

### Real-World Forms

```text
Backend: logs, caches, sessions, schedulers, intervals, dashboards
CF: dynamic queries, windows, nearest values, active intervals, top candidates
FAANG: design data structures, calendar, cache, median, top-k, stream processing
```
# 023. Range Mapping and Interval Merge

## Merge Intervals

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<pair<int,int>> intervals(n);
    for (auto &[l,r] : intervals) cin >> l >> r;

    sort(intervals.begin(), intervals.end());

    vector<pair<int,int>> merged;

    for (auto [l,r] : intervals) {
        if (merged.empty() || merged.back().second < l) {
            merged.push_back({l,r});
        } else {
            merged.back().second = max(merged.back().second, r);
        }
    }

    for (auto [l,r] : merged) {
        cout << l << " " << r << "\n";
    }

    return 0;
}
```

## Dry Run

```text
[1,4], [2,6], [8,10]

add [1,4]
[2,6] overlaps -> merge [1,6]
[8,10] no overlap -> add
```

## Insert Interval Form

```text
add new interval
merge with neighbors
```

Best STL:

```text
vector + sort if offline
set/map if online intervals
```

---



## 10/10 Real-World Example Pack: Map as Interval Ownership Machine

### Recognition First

```text
Before coding, ask:
1. What is changing after each step?
2. What must be queried fast?
3. What invariant must remain true?
```

### ASCII Mental Model

```text
Input stream / operations
        |
        v
Maintain useful state only
        |
        v
Answer from container top/boundary/middle
```

### Deep Dry-Run Habit

```text
For every operation write:
before state
operation effect
expired/deleted items
rebalanced state
answer
```

### Contest Debug Checklist

```text
Boundary empty?
Duplicate values?
Need index or value?
Is deletion exact or lazy?
Does answer require before or after update?
```

### Real-World Forms

```text
Backend: logs, caches, sessions, schedulers, intervals, dashboards
CF: dynamic queries, windows, nearest values, active intervals, top candidates
FAANG: design data structures, calendar, cache, median, top-k, stream processing
```
# 024. Sweep Line Engine

## Core Mental Model

Turn ranges into events.

```text
start +1
end   -1
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

## Tie-Breaking

For half-open intervals `[l,r)`:

```text
end at r should happen before start at r sometimes
```

For inclusive intervals `[l,r]`, use:

```cpp
events.push_back({l, +1});
events.push_back({r + 1, -1});
```

## Difference Array Compression Variant

```text
range add:
diff[l] += val
diff[r+1] -= val
prefix diff gives active value
```

---



## 10/10 Real-World Example Pack: Sweep Line as Active Counter

### Recognition First

```text
Before coding, ask:
1. What is changing after each step?
2. What must be queried fast?
3. What invariant must remain true?
```

### ASCII Mental Model

```text
Input stream / operations
        |
        v
Maintain useful state only
        |
        v
Answer from container top/boundary/middle
```

### Deep Dry-Run Habit

```text
For every operation write:
before state
operation effect
expired/deleted items
rebalanced state
answer
```

### Contest Debug Checklist

```text
Boundary empty?
Duplicate values?
Need index or value?
Is deletion exact or lazy?
Does answer require before or after update?
```

### Real-World Forms

```text
Backend: logs, caches, sessions, schedulers, intervals, dashboards
CF: dynamic queries, windows, nearest values, active intervals, top candidates
FAANG: design data structures, calendar, cache, median, top-k, stream processing
```
# 025. Top-K and Kth Engine

## Kth Largest

Use min heap size K.

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

        if ((int)pq.size() > k) pq.pop();
    }

    cout << pq.top() << "\n";
    return 0;
}
```

## Top K Frequent

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, k;
    cin >> n >> k;

    unordered_map<int,int> freq;

    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;
        freq[x]++;
    }

    priority_queue<pair<int,int>, vector<pair<int,int>>, greater<pair<int,int>>> pq;

    for (auto &[val, cnt] : freq) {
        pq.push({cnt, val});
        if ((int)pq.size() > k) pq.pop();
    }

    vector<int> ans;

    while (!pq.empty()) {
        ans.push_back(pq.top().second);
        pq.pop();
    }

    for (int x : ans) cout << x << " ";
    return 0;
}
```

## Complexity

```text
frequency O(N)
heap O(M logK), M=unique values
```

---



## 10/10 Real-World Example Pack: Top-K as Bounded Heap

### Recognition First

```text
Before coding, ask:
1. What is changing after each step?
2. What must be queried fast?
3. What invariant must remain true?
```

### ASCII Mental Model

```text
Input stream / operations
        |
        v
Maintain useful state only
        |
        v
Answer from container top/boundary/middle
```

### Deep Dry-Run Habit

```text
For every operation write:
before state
operation effect
expired/deleted items
rebalanced state
answer
```

### Contest Debug Checklist

```text
Boundary empty?
Duplicate values?
Need index or value?
Is deletion exact or lazy?
Does answer require before or after update?
```

### Real-World Forms

```text
Backend: logs, caches, sessions, schedulers, intervals, dashboards
CF: dynamic queries, windows, nearest values, active intervals, top candidates
FAANG: design data structures, calendar, cache, median, top-k, stream processing
```
# 026. Median Maintenance

## Two Heap Model

```text
low  max-heap: smaller half
high min-heap: larger half

max(low) <= min(high)
```

## Code

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
stream 5,2,10,1

5 -> low=[5], high=[] median=5
2 -> low=[2], high=[5] median=2
10 -> low=[5,2], high=[10] median=5
1 -> low=[2,1], high=[5,10] median=2
```

---



## 10/10 Real-World Example Pack: Two Heaps as Stream Median Machine

### Recognition First

```text
Before coding, ask:
1. What is changing after each step?
2. What must be queried fast?
3. What invariant must remain true?
```

### ASCII Mental Model

```text
Input stream / operations
        |
        v
Maintain useful state only
        |
        v
Answer from container top/boundary/middle
```

### Deep Dry-Run Habit

```text
For every operation write:
before state
operation effect
expired/deleted items
rebalanced state
answer
```

### Contest Debug Checklist

```text
Boundary empty?
Duplicate values?
Need index or value?
Is deletion exact or lazy?
Does answer require before or after update?
```

### Real-World Forms

```text
Backend: logs, caches, sessions, schedulers, intervals, dashboards
CF: dynamic queries, windows, nearest values, active intervals, top candidates
FAANG: design data structures, calendar, cache, median, top-k, stream processing
```
# 027. LRU Cache STL Design

## Core Mental Model

Need:

```text
O(1) key lookup
O(1) move to most recent
O(1) evict least recent
```

Use:

```text
list<pair<key,value>>
unordered_map<key, iterator>
```

## ASCII

```text
front                         back
MRU  [3] [1] [7] [2]  LRU
```

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class LRUCache {
    int cap;
    list<pair<int,int>> order;
    unordered_map<int, list<pair<int,int>>::iterator> pos;

public:
    LRUCache(int capacity) {
        cap = capacity;
    }

    int get(int key) {
        if (!pos.count(key)) return -1;

        auto it = pos[key];
        int value = it->second;

        order.erase(it);
        order.push_front({key, value});
        pos[key] = order.begin();

        return value;
    }

    void put(int key, int value) {
        if (cap == 0) return;

        if (pos.count(key)) {
            order.erase(pos[key]);
        }

        order.push_front({key, value});
        pos[key] = order.begin();

        if ((int)order.size() > cap) {
            int oldKey = order.back().first;
            order.pop_back();
            pos.erase(oldKey);
        }
    }
};

int main() {
    LRUCache cache(2);

    cache.put(1,10);
    cache.put(2,20);
    cout << cache.get(1) << "\n";
    cache.put(3,30);
    cout << cache.get(2) << "\n";

    return 0;
}
```

## Dry Run

```text
capacity=2

put(1): [1]
put(2): [2,1]
get(1): [1,2]
put(3): [3,1,2] -> evict 2 -> [3,1]
```

## Bugs

```text
forget cap=0
forget to erase evicted key from map
iterator invalid after erase
not updating map after move
```

---



## 10/10 Real-World Example Pack: LRU as Recency Linked List

### Recognition First

```text
Before coding, ask:
1. What is changing after each step?
2. What must be queried fast?
3. What invariant must remain true?
```

### ASCII Mental Model

```text
Input stream / operations
        |
        v
Maintain useful state only
        |
        v
Answer from container top/boundary/middle
```

### Deep Dry-Run Habit

```text
For every operation write:
before state
operation effect
expired/deleted items
rebalanced state
answer
```

### Contest Debug Checklist

```text
Boundary empty?
Duplicate values?
Need index or value?
Is deletion exact or lazy?
Does answer require before or after update?
```

### Real-World Forms

```text
Backend: logs, caches, sessions, schedulers, intervals, dashboards
CF: dynamic queries, windows, nearest values, active intervals, top candidates
FAANG: design data structures, calendar, cache, median, top-k, stream processing
```
# 028. LFU Cache STL Design

## Core Mental Model

Evict:

```text
lowest frequency
if tie, least recently used among that frequency
```

Use:

```text
key -> node {value, freq, iterator}
freq -> list of keys
minFreq
```

## ASCII

```text
freq 1: [7,2]   back is least recent
freq 2: [5]
freq 3: [1]

minFreq = 1
evict back of freq 1
```

## Code

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
    unordered_map<int, list<int>> buckets;

    void touch(int key) {
        int f = data[key].freq;

        buckets[f].erase(data[key].it);

        if (buckets[f].empty()) {
            buckets.erase(f);
            if (minFreq == f) minFreq++;
        }

        int nf = f + 1;
        buckets[nf].push_front(key);

        data[key].freq = nf;
        data[key].it = buckets[nf].begin();
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
            int evictKey = buckets[minFreq].back();
            buckets[minFreq].pop_back();

            if (buckets[minFreq].empty()) {
                buckets.erase(minFreq);
            }

            data.erase(evictKey);
        }

        buckets[1].push_front(key);
        data[key] = {value, 1, buckets[1].begin()};
        minFreq = 1;
    }
};

int main() {
    LFUCache cache(2);

    cache.put(1,10);
    cache.put(2,20);
    cout << cache.get(1) << "\n";
    cache.put(3,30);
    cout << cache.get(2) << "\n";
    cout << cache.get(3) << "\n";

    return 0;
}
```

## Dry Run

```text
capacity=2

put(1)
freq1: [1], minFreq=1

put(2)
freq1: [2,1], minFreq=1

get(1)
remove 1 from freq1 -> [2]
put 1 into freq2 -> [1]
minFreq still 1

put(3)
evict back of freq1 -> 2
insert 3 into freq1 -> [3]
```

## Bugs

```text
not updating minFreq
not maintaining LRU order inside same frequency
iterator invalidation
forget cap=0
```

---



## 10/10 Real-World Example Pack: LFU as Frequency Bucket Machine

### Recognition First

```text
Before coding, ask:
1. What is changing after each step?
2. What must be queried fast?
3. What invariant must remain true?
```

### ASCII Mental Model

```text
Input stream / operations
        |
        v
Maintain useful state only
        |
        v
Answer from container top/boundary/middle
```

### Deep Dry-Run Habit

```text
For every operation write:
before state
operation effect
expired/deleted items
rebalanced state
answer
```

### Contest Debug Checklist

```text
Boundary empty?
Duplicate values?
Need index or value?
Is deletion exact or lazy?
Does answer require before or after update?
```

### Real-World Forms

```text
Backend: logs, caches, sessions, schedulers, intervals, dashboards
CF: dynamic queries, windows, nearest values, active intervals, top candidates
FAANG: design data structures, calendar, cache, median, top-k, stream processing
```
# 029. CF A/B/C/D Forms

## Div2 A

```text
vector, string, sort, set, map
```

Examples:

```text
count unique
sort and compare
frequency characters
min/max
```

## Div2 B

```text
unordered_map
sort + greedy
two pointers
priority_queue simple
```

Examples:

```text
pairing values
count remainders
frequency decisions
```

## Div2 C

```text
prefix + map
lower_bound
multiset
monotonic stack
deque
```

Examples:

```text
subarray count
window max/min
nearest boundary
dynamic ordered constraints
```

## Div2 D / CM

```text
priority_queue lazy deletion
sweep line
two multisets
range mapping
Dijkstra heap
advanced comparator
```

Recognition:

```text
online updates
dynamic median
events
interval overlap
stale heap entries
```

---



## 10/10 Deep Add-on: Rating-Wise STL Recognition

```text
800-1000:
    count, sort, set unique, simple map

1100-1400:
    two pointers, prefix, lower_bound, greedy sort

1500-1800:
    monotonic stack/deque, heap, sweep line, multiset

1900-2200:
    lazy deletion, two multisets, map intervals, offline queries, state Dijkstra
```

### During Contest

```text
If statement says "after each query" -> think dynamic container.
If statement says "for every window" -> think deque/multiset.
If statement says "nearest" -> think lower_bound.
If statement says "maximum after insertions" -> think set + multiset / heap.
```
# 030. FAANG Forms

## Mapping Table

```text
Two Sum                         unordered_map
Group Anagrams                  unordered_map<string, vector<string>>
Top K Frequent                  unordered_map + heap
Merge Intervals                 vector + sort
Meeting Rooms                   sort + priority_queue
Sliding Window Maximum          deque
Valid Parentheses               stack
Daily Temperatures              monotonic stack
Median Finder                   two heaps
LRU Cache                       list + unordered_map
LFU Cache                       unordered_map + lists
Random Pick Weight              prefix vector + lower_bound
```

## Interview Explanation Pattern

```text
I need operation X fast.
This STL gives X in O(...).
Each element is processed ...
So total time is ...
Extra memory is ...
```

---



## 10/10 Deep Add-on: FAANG STL Translation

```text
Design question usually means combine containers:
LRU  -> list + unordered_map
LFU  -> unordered_map + freq lists
Twitter feed -> unordered_map + heap
TimeMap -> unordered_map + vector + upper_bound
Calendar -> map / sweep line
Median -> two heaps / two multisets
```

### Interview Answer Formula

```text
1. State required operations.
2. Choose container combination.
3. State invariant.
4. Walk through one example.
5. Give complexity.
6. Mention edge cases.
```
# 031. STL Bug Encyclopedia

## Vector

```text
erase front in loop -> TLE
out-of-bounds
prefix int overflow
```

## String

```text
substr copies
getline after cin
wrong char range
```

## Stack/Queue

```text
top/front on empty
forgot final stack empty check
```

## Priority Queue

```text
default max heap
stale entries
top on empty
```

## Set/Map

```text
erase all duplicates
decrement begin
dereference end
std::lower_bound on set is O(N)
```

## Comparator

```text
<= instead of <
non-transitive
overflow from subtraction
```

## LRU/LFU

```text
iterator invalidation
not updating map iterator
not updating minFreq
capacity zero
```

---



## 10/10 Deep Add-on: Bug Pattern Recognition

```text
WA after using set/multiset:
    check begin/end before prev/next
    check erase(value) vs erase(iterator)

TLE after using vector:
    check erase/insert inside loop

WA after using heap:
    check min/max direction
    check stale entries

WA after using stack/deque:
    check < vs <=
    check value vs index
```
# 032. Debugging Playbook

## Before Submit

```text
[ ] Did I choose STL by operation?
[ ] Is complexity okay for max N?
[ ] Is data sorted before binary search?
[ ] Did I use set.lower_bound, not std::lower_bound?
[ ] Did I check empty before top/front/back?
[ ] Did I erase one multiset copy correctly?
[ ] Did I preserve original index if needed?
[ ] Is comparator strict?
[ ] Are heap stale entries cleaned?
[ ] Did I handle duplicates?
[ ] Did I handle capacity 0 in cache?
[ ] Did I use long long where needed?
```

## TLE Investigation

```text
1. Search for erase(begin()).
2. Search for sort inside loop.
3. Search for nested vector find/count.
4. Search for lower_bound on set with std::lower_bound.
5. Check map vs unordered_map.
6. Check heap stale cleanup.
```

---



## 10/10 Deep Add-on: 60-Second STL Debug Ritual

```text
1. Print container state on tiny input.
2. Test empty container.
3. Test duplicates.
4. Test all equal values.
5. Test negative values if modulo/prefix exists.
6. Test one element.
7. Test max constraints mentally for overflow/TLE.
```
# 033. Practice Ladder

## Beginner STL

```text
vector input/output
sort array
frequency count
valid parentheses
set unique count
```

## CF A/B

```text
sort and compare
map frequency decisions
pair after sorting
basic lower_bound
priority_queue simple top-k
```

## CF C

```text
prefix + unordered_map
monotonic stack
sliding window max
multiset window min/max
sweep line overlap
```

## CF D / CM

```text
lazy deletion heap
two multiset median
range mapping
Dijkstra with priority_queue
advanced comparator
interval events
```

## FAANG

```text
LRU Cache
LFU Cache
Median Finder
Sliding Window Maximum
Top K Frequent
Merge Intervals
Meeting Rooms
Daily Temperatures
```

---



## 10/10 Deep Add-on: How To Practice This File

```text
For each section:
    Day 1: read mental model + dry run
    Day 2: code template from memory
    Day 3: solve 3 CF/LC forms
    Day 4: write bug notes
    Day 5: explain pattern in 60 seconds
```

### Pattern Mastery Test

```text
You mastered a section only when:
    you can identify it from story words
    you can draw the invariant
    you can code without looking
    you know 3 common bugs
```
# 099. Final Cheat Sheet

## Container Choice

```text
vector              index + sort + prefix
string              char problems
stack               LIFO / parser / monotonic
queue               BFS / FIFO
deque               both ends / window max
priority_queue      best repeatedly
set                 sorted unique
multiset            sorted duplicates
map                 ordered key-value
unordered_map       fast average key-value
list                O(1) node movement
```

## Pattern Choice

```text
frequency               unordered_map
subarray sum k           prefix + map
next greater             monotonic stack
window max/min           monotonic deque
top k                    heap size k
kth largest              min heap size k
dynamic nearest          set.lower_bound
dynamic median           two heaps / two multisets
merge intervals          vector + sort
overlap count            sweep line
LRU                      list + unordered_map
LFU                      unordered_map + frequency lists
```

## Complexity

```text
sort                    O(NlogN)
heap push/pop           O(logN)
set/map op              O(logN)
unordered_map avg       O(1)
vector erase front      O(N)
monotonic stack/deque   O(N)
LRU get/put             O(1)
LFU get/put             O(1)
```

## One Picture To Remember

```text
PROBLEM
   ↓
What operation must be fast?
   ↓
Choose STL machine
   ↓
Apply pattern
   ↓
Check bugs
   ↓
AC
```

STL mastery = operation selection + safe implementation.


---

# Appendix A. Monotonic Stack Pattern Matrix

```text
Need next greater right:
    scan left -> right
    pop while stack value < current

Need next smaller right:
    scan left -> right
    pop while stack value > current

Need previous greater left:
    scan left -> right
    pop while stack value <= current
    top is previous greater

Need previous smaller left:
    scan left -> right
    pop while stack value >= current
    top is previous smaller
```

## Template Matrix

```cpp
// Previous smaller
while (!st.empty() && a[st.top()] >= a[i]) st.pop();
ans[i] = st.empty() ? -1 : st.top();
st.push(i);
```

```cpp
// Previous greater
while (!st.empty() && a[st.top()] <= a[i]) st.pop();
ans[i] = st.empty() ? -1 : st.top();
st.push(i);
```

```cpp
// Next smaller
for (int i = 0; i < n; i++) {
    while (!st.empty() && a[st.top()] > a[i]) {
        ans[st.top()] = i;
        st.pop();
    }
    st.push(i);
}
```

```cpp
// Next greater
for (int i = 0; i < n; i++) {
    while (!st.empty() && a[st.top()] < a[i]) {
        ans[st.top()] = i;
        st.pop();
    }
    st.push(i);
}
```

## Equal Element Trap

```text
Use < or <= depending whether equal values should block.

For strictly greater:
    pop while < current

For greater or equal:
    pop while <= current
```

---

# Appendix B. Sweep Line Tie-Break Matrix

## Meeting Rooms

Intervals often behave as:

```text
[start, end)
```

If one meeting ends at 10 and another starts at 10, they do not overlap.

Tie rule:

```text
end event before start event
```

One way:

```cpp
events.push_back({start, +1});
events.push_back({end, -1});
sort(events.begin(), events.end());
```

Because `-1` sorts before `+1` at same coordinate.

## Inclusive Ranges

For inclusive `[l,r]`, use:

```cpp
events.push_back({l, +1});
events.push_back({r + 1, -1});
```

If `r+1` may overflow, use pair sorting with custom endpoint logic.

---

# Appendix C. Ordered Set Problem Forms

## Form 1: Smallest value >= x

```cpp
auto it = s.lower_bound(x);
```

## Form 2: Largest value <= x

```cpp
auto it = s.upper_bound(x);
if (it != s.begin()) {
    --it;
}
```

## Form 3: Maintain active intervals

```text
insert start/end
remove expired
query nearest
```

## Form 4: Dynamic mex

Maintain set of missing values.

```cpp
set<int> missing;
for (int i = 0; i <= n; i++) missing.insert(i);

// when value x appears first time:
missing.erase(x);

// mex:
*missing.begin()
```

---

# Appendix D. Heap Problem Forms

## Form 1: Always take largest

```cpp
priority_queue<int> pq;
```

## Form 2: Always take smallest

```cpp
priority_queue<int, vector<int>, greater<int>> pq;
```

## Form 3: Dijkstra

```cpp
priority_queue<pair<ll,int>, vector<pair<ll,int>>, greater<pair<ll,int>>> pq;
```

## Form 4: K largest

```text
min heap size k
```

## Form 5: K smallest

```text
max heap size k
```

## Form 6: Lazy deletion

```text
mark invalid
clean top before using
```

---

# Appendix E. LRU vs LFU Comparison

```text
LRU evicts by recency.
LFU evicts by frequency, then recency.
```

## LRU Data Structures

```text
list<pair<key,value>>
unordered_map<key, list iterator>
```

## LFU Data Structures

```text
unordered_map<key, Node>
unordered_map<freq, list<key>>
minFreq
```

## Design Interview Explanation

```text
LRU:
    Hash map gives O(1) access.
    Doubly linked list gives O(1) move to front and remove from back.

LFU:
    Hash map gives O(1) key access.
    Frequency buckets group keys by count.
    Each bucket is a list to preserve recency within same frequency.
    minFreq tells which bucket to evict from.
```

---

# Appendix F. More FAANG Mini Templates

## Meeting Rooms II

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<pair<int,int>> intervals(n);
    for (auto &[s,e] : intervals) cin >> s >> e;

    sort(intervals.begin(), intervals.end());

    priority_queue<int, vector<int>, greater<int>> ends;

    int ans = 0;

    for (auto [s,e] : intervals) {
        while (!ends.empty() && ends.top() <= s) {
            ends.pop();
        }

        ends.push(e);
        ans = max(ans, (int)ends.size());
    }

    cout << ans << "\n";
    return 0;
}
```

## Daily Temperatures

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<int> t(n), ans(n,0);
    for (int &x : t) cin >> x;

    stack<int> st;

    for (int i = 0; i < n; i++) {
        while (!st.empty() && t[st.top()] < t[i]) {
            int j = st.top();
            st.pop();
            ans[j] = i - j;
        }
        st.push(i);
    }

    for (int x : ans) cout << x << " ";
    return 0;
}
```

## Random Pick Weight

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<int> pref(n);
    for (int i = 0; i < n; i++) {
        int w;
        cin >> w;
        pref[i] = w + (i ? pref[i-1] : 0);
    }

    int x;
    cin >> x; // assume 1 <= x <= pref.back()

    int idx = lower_bound(pref.begin(), pref.end(), x) - pref.begin();
    cout << idx << "\n";

    return 0;
}
```

---

# Appendix G. CM-Level STL Recognition Drills

## Drill 1

```text
Problem: For each element, find the next element to the right that is greater.

Recognition:
    nearest greater boundary
STL:
    stack
Pattern:
    monotonic stack
```

## Drill 2

```text
Problem: Count subarrays with sum exactly K, values can be negative.

Recognition:
    prefix difference
STL:
    unordered_map
Pattern:
    prefix + frequency
```

## Drill 3

```text
Problem: Maintain numbers under insert/delete and output median.

Recognition:
    dynamic median
STL:
    multiset / two heaps
Pattern:
    balanced halves
```

## Drill 4

```text
Problem: Merge overlapping booking intervals.

Recognition:
    intervals offline
STL:
    vector<pair> + sort
Pattern:
    interval merge
```

## Drill 5

```text
Problem: Max number of active intervals at once.

Recognition:
    events over coordinate
STL:
    vector<pair> + sort
Pattern:
    sweep line
```

---

# Appendix H. Final CM STL Mindset

```text
CF A/B:
    vector, string, sort, frequency

CF C:
    prefix map, binary search, monotonic stack/deque, set

CF D:
    priority_queue, multiset, sweep line, lazy deletion, graph adjacency

FAANG:
    hashmap, stack, heap, deque, list+hashmap design
```

The jump from beginner to CM is not learning more containers.

It is learning **which container matches the hidden operation**.


---

# Appendix I. Iterator Invalidation Deep Table

Iterator invalidation is one of the most common hidden runtime bugs in C++ STL.

## Vector

```text
push_back:
    may invalidate all iterators if capacity changes

erase:
    invalidates erased iterator and everything after it

insert:
    may invalidate all iterators if reallocation happens
```

Bad:

```cpp
for (auto it = v.begin(); it != v.end(); it++) {
    if (*it == 0) {
        v.erase(it); // it becomes invalid
    }
}
```

Better:

```cpp
for (auto it = v.begin(); it != v.end(); ) {
    if (*it == 0) {
        it = v.erase(it);
    } else {
        ++it;
    }
}
```

## Set / Map

```text
erase only invalidates erased iterator
other iterators stay valid
```

Safe erase:

```cpp
for (auto it = s.begin(); it != s.end(); ) {
    if (*it % 2 == 0) {
        it = s.erase(it);
    } else {
        ++it;
    }
}
```

## Unordered Map

```text
rehashing may invalidate iterators
erase invalidates erased iterator
```

If you insert many values and keep iterators, be careful.

## List

```text
insert/erase does not invalidate other iterators
```

This is why `list` is used for LRU.

---

# Appendix J. Custom Comparator Deep Dive

## Mental Model

Comparator answers:

```text
Should a come before b?
```

It must be strict.

## Strict Weak Ordering Rules

```text
comp(a,a) must be false
if comp(a,b) true, comp(b,a) must be false
ordering must be transitive
```

## Bad Comparator

```cpp
sort(a.begin(), a.end(), [](int x, int y) {
    return x <= y;
});
```

Why bad?

```text
comp(5,5) returns true
This violates strictness.
Sort behavior becomes undefined.
```

## Good Comparator

```cpp
sort(a.begin(), a.end(), [](int x, int y) {
    return x < y;
});
```

## Multi-Key Comparator

```cpp
sort(v.begin(), v.end(), [](auto &a, auto &b) {
    if (a.score != b.score) return a.score > b.score;
    if (a.penalty != b.penalty) return a.penalty < b.penalty;
    return a.name < b.name;
});
```

## Comparator Overflow Trap

Bad:

```cpp
return a.x - b.x < 0;
```

If `a.x` and `b.x` are large, subtraction can overflow.

Good:

```cpp
return a.x < b.x;
```

## Priority Queue Comparator Trap

For `priority_queue`, comparator feels reversed.

```cpp
struct Cmp {
    bool operator()(const Node& a, const Node& b) const {
        return a.dist > b.dist; // min-heap by dist
    }
};
```

Meaning:

```text
return true means a has lower priority than b
```

---

# Appendix K. Hashing and unordered_map Safety

## Problem

`unordered_map` is average O(1), but in adversarial cases it can degrade.

## When To Use unordered_map

```text
frequency
prefix sums
Two Sum
Group Anagrams
counting previous states
```

## When To Prefer map

```text
need ordered traversal
need lower_bound by key
small constraints where O(logN) is fine
want deterministic ordering
```

## Reserve Optimization

```cpp
unordered_map<long long,int> mp;
mp.reserve(1 << 20);
mp.max_load_factor(0.7);
```

## Custom Hash Template

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

Use:

```cpp
unordered_map<long long,int,custom_hash> mp;
```

## Common Bug

```cpp
if (mp[x]) ...
```

This creates key `x`.

Use:

```cpp
if (mp.count(x)) ...
```

when you only want to check existence.

---

# Appendix L. Graph STL Forms

STL is also the backbone of graph problems.

## Adjacency List

```cpp
vector<vector<int>> g(n);
```

## Weighted Graph

```cpp
vector<vector<pair<int,int>>> g(n);
```

## Edge List

```cpp
vector<tuple<int,int,int>> edges;
```

## BFS

```cpp
queue<int> q;
```

## Dijkstra

```cpp
priority_queue<pair<long long,int>,
               vector<pair<long long,int>>,
               greater<pair<long long,int>>> pq;
```

## Topological Sort

```cpp
queue<int> q; // nodes with indegree 0
```

## DSU Storage

```cpp
vector<int> parent(n), sz(n);
```

## Dijkstra Full Skeleton

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;
const ll INF = 4e18;

int main() {
    int n, m;
    cin >> n >> m;

    vector<vector<pair<int,int>>> g(n);

    for (int i = 0; i < m; i++) {
        int u, v, w;
        cin >> u >> v >> w;
        --u; --v;
        g[u].push_back({v,w});
    }

    vector<ll> dist(n, INF);

    priority_queue<pair<ll,int>,
                   vector<pair<ll,int>>,
                   greater<pair<ll,int>>> pq;

    dist[0] = 0;
    pq.push({0,0});

    while (!pq.empty()) {
        auto [d,u] = pq.top();
        pq.pop();

        if (d != dist[u]) continue; // stale entry

        for (auto [v,w] : g[u]) {
            if (dist[v] > d + w) {
                dist[v] = d + w;
                pq.push({dist[v], v});
            }
        }
    }

    for (ll d : dist) cout << d << " ";
    return 0;
}
```

## Recognition

```text
unweighted shortest path -> queue BFS
weighted non-negative    -> priority_queue Dijkstra
DAG ordering             -> queue topo sort
MST Kruskal              -> vector edges + sort + DSU
```

---

# Appendix M. STL for DP and State Compression

STL appears in DP when state is sparse or dynamic.

## Dense DP

Use vector.

```cpp
vector<int> dp(n + 1, INF);
```

## 2D DP

```cpp
vector<vector<int>> dp(n, vector<int>(m, 0));
```

## Sparse DP

Use map or unordered_map.

```cpp
unordered_map<long long, long long> dp;
```

## DP With Bitmask

```cpp
vector<int> dp(1 << n, INF);
```

## DP State As Tuple Key

For ordered map:

```cpp
map<tuple<int,int,int>, long long> dp;
```

For unordered_map, encode state:

```cpp
long long key = 1LL * i * 1000000 + j;
```

Be careful about collisions.

## Recognition

```text
state count small and dense -> vector
state sparse -> map/unordered_map
need ordered states -> map
bitmask states -> vector size 2^n
```

---

# Appendix N. STL Problem Form: Coordinate Compression + Fenwick Prep

Even before Fenwick/segment tree, STL helps compress coordinates.

## Why Compression

Values may be huge:

```text
a[i] up to 1e9
```

But only N distinct values exist.

## Template

```cpp
vector<int> vals = a;

sort(vals.begin(), vals.end());
vals.erase(unique(vals.begin(), vals.end()), vals.end());

for (int x : a) {
    int id = lower_bound(vals.begin(), vals.end(), x) - vals.begin();
}
```

## Dry Run

```text
a=[1000000000, 5, 1000000000, 20]

vals sorted unique:
[5,20,1000000000]

compressed:
1000000000 -> 2
5          -> 0
1000000000 -> 2
20         -> 1
```

## Use Cases

```text
Fenwick tree
segment tree
inversion count
offline queries
range compression
large coordinate sweep line
```

---

# Appendix O. Offline Query Pattern With STL

## Mental Model

Sometimes answer queries faster by sorting them.

```text
sort data
sort queries
move pointer once
```

## Example Form

```text
Given array and queries x:
count how many numbers <= x.
```

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, q;
    cin >> n >> q;

    vector<int> a(n);
    for (int &x : a) cin >> x;

    sort(a.begin(), a.end());

    while (q--) {
        int x;
        cin >> x;

        int cnt = upper_bound(a.begin(), a.end(), x) - a.begin();
        cout << cnt << "\n";
    }

    return 0;
}
```

## Complexity

```text
sort O(NlogN)
each query O(logN)
```

If queries need cumulative state, sort queries and use pointer.

---

# Appendix P. More Dry Runs

## lower_bound Dry Run

```text
a=[1,3,3,5,8]
x=3

lower_bound:
first >=3 is index 1

upper_bound:
first >3 is index 3

count of 3 = 3-1 = 2
```

## multiset Erase Dry Run

```text
ms={1,2,2,2,5}

ms.erase(2):
    ms={1,5}

erase one:
    it=ms.find(2)
    ms.erase(it)
    ms={1,2,2,5}
```

## LRU Dry Run With Map

```text
capacity=2

list: []
map: {}

put(1,10)
list: [1]
map: 1 -> iterator to list node

put(2,20)
list: [2,1]

get(1)
move 1 to front
list: [1,2]

put(3,30)
list before evict: [3,1,2]
evict back 2
list: [3,1]
map has keys 1,3
```

## LFU Dry Run With Buckets

```text
capacity=2

put(1)
freq1: [1]
minFreq=1

put(2)
freq1: [2,1]
minFreq=1

get(1)
freq1: [2]
freq2: [1]
minFreq=1

put(3)
evict back of freq1 = 2
freq1: [3]
freq2: [1]
```

---

# Appendix Q. One-Line Recognition Cheat Codes

```text
"nearest greater"               stack
"nearest smaller"               stack
"every window maximum"          deque
"dynamic median"                two multisets
"stream median"                 two heaps
"k largest"                     min heap size k
"k smallest"                    max heap size k
"count previous same"           unordered_map
"subarray sum k"                prefix + unordered_map
"ordered nearest"               set.lower_bound
"merge ranges"                  sort intervals
"max overlaps"                  sweep line
"evict least recent"            list + unordered_map
"evict least frequent"          frequency lists
```

---

# Appendix R. STL Interview Narration Bank

## Hash Map

```text
I use an unordered_map because I need average O(1) lookup by key.
This trades extra O(N) memory for faster lookup.
```

## Heap

```text
I use a priority_queue because I repeatedly need the current best candidate.
Each insertion/removal is O(logN).
```

## Monotonic Stack

```text
The stack stores unresolved indices.
Each index is pushed once and popped once, so even though there is a while loop, total time is O(N).
```

## Deque

```text
The deque stores candidate indices for the current window.
Expired indices leave from the front, weaker candidates leave from the back.
Each index enters and leaves once, so total time is O(N).
```

## LRU

```text
The unordered_map gives O(1) access to nodes.
The list gives O(1) movement and deletion when we have the iterator.
Together, get and put are O(1).
```

## LFU

```text
The key map stores value, frequency, and iterator.
Frequency buckets store keys ordered by recency.
minFreq tells us where to evict in O(1).
```

---

# Appendix S. Final CM Checklist

```text
[ ] Can I explain why vector erase front is O(N)?
[ ] Can I write lower_bound and upper_bound without bugs?
[ ] Can I erase one copy from multiset?
[ ] Can I implement next greater element?
[ ] Can I implement sliding window maximum?
[ ] Can I implement top-k with heap size k?
[ ] Can I handle stale heap entries?
[ ] Can I merge intervals?
[ ] Can I sweep events with tie-breaking?
[ ] Can I implement LRU from memory?
[ ] Can I explain LFU structure?
[ ] Can I choose map vs unordered_map under pressure?
[ ] Can I avoid iterator invalidation bugs?
```



---

# Appendix T. Final 20 STL Micro-Patterns

## 1. Remove Duplicates From Sorted Vector

```cpp
sort(a.begin(), a.end());
a.erase(unique(a.begin(), a.end()), a.end());
```

## 2. Count x In Sorted Vector

```cpp
auto l = lower_bound(a.begin(), a.end(), x);
auto r = upper_bound(a.begin(), a.end(), x);
long long cnt = r - l;
```

## 3. First Index With Value At Least x

```cpp
int idx = lower_bound(a.begin(), a.end(), x) - a.begin();
```

## 4. Check Existence In Set

```cpp
if (s.count(x)) {
    // exists
}
```

## 5. Check Existence In unordered_map Without Creating Key

```cpp
if (mp.find(x) != mp.end()) {
    // exists
}
```

## 6. Frequency Increment

```cpp
freq[x]++;
```

## 7. Frequency Decrement And Cleanup

```cpp
freq[x]--;
if (freq[x] == 0) freq.erase(x);
```

## 8. Sort Intervals

```cpp
sort(intervals.begin(), intervals.end());
```

## 9. Sort By Second

```cpp
sort(v.begin(), v.end(), [](auto &a, auto &b) {
    return a.second < b.second;
});
```

## 10. Min Heap Pair

```cpp
priority_queue<pair<int,int>, vector<pair<int,int>>, greater<pair<int,int>>> pq;
```

## 11. Reverse Vector

```cpp
reverse(a.begin(), a.end());
```

## 12. Sum Vector

```cpp
long long sum = accumulate(a.begin(), a.end(), 0LL);
```

## 13. Max Element

```cpp
int mx = *max_element(a.begin(), a.end());
```

## 14. Min Element

```cpp
int mn = *min_element(a.begin(), a.end());
```

## 15. Index Of Max

```cpp
int idx = max_element(a.begin(), a.end()) - a.begin();
```

## 16. Next Permutation

```cpp
sort(a.begin(), a.end());
do {
    // use permutation
} while (next_permutation(a.begin(), a.end()));
```

## 17. Erase One multiset Value

```cpp
auto it = ms.find(x);
if (it != ms.end()) ms.erase(it);
```

## 18. Largest In multiset

```cpp
int mx = *ms.rbegin();
```

## 19. Smallest In multiset

```cpp
int mn = *ms.begin();
```

## 20. Safe Previous In Set

```cpp
auto it = s.lower_bound(x);
if (it != s.begin()) {
    --it;
    // *it is previous value
}
```

---

# Appendix U. Final Rating Standard

A 10/10 STL file for CM and FAANG should let you:

```text
1. Choose the right STL from problem wording.
2. Know exact complexity.
3. Recognize common CF A/B/C/D forms.
4. Implement monotonic stack/deque from memory.
5. Implement priority_queue lazy deletion.
6. Handle multiset duplicates correctly.
7. Explain LRU and LFU in interviews.
8. Avoid iterator invalidation.
9. Debug WA/TLE/runtime bugs quickly.
10. Convert STL knowledge into patterns, not syntax.
```

This file is designed around those ten outcomes.


## 10/10 Final One-Picture STL Recognition Map

```text
                    PROBLEM STORY
                          |
                 remove business nouns
                          |
                 find repeated operation
                          |
   ---------------------------------------------------
   |          |          |          |          |       |
 Count     Nearest     Best      Window    Boundary  Interval
   |          |          |          |          |       |
unordered  set/       heap      deque/     stack    sort events
 map       multiset             multiset            sweep line
   |
prefix/frequency

   ---------------------------------------------------
   |              |               |                 |
 Median          Cache           Graph             Offline
   |              |               |                 |
two heaps/       list+map        queue/heap        sort queries
multisets                         BFS/Dijkstra      + pointer
```

### Final Rule

```text
Never memorize STL alone.
Memorize: story word -> hidden operation -> invariant -> container -> bug list.
```
