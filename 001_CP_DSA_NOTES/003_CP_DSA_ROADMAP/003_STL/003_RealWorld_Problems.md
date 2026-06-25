# MiniSTL_RealWorld_Problems.md

## STL Real-World Pattern Bible for Codeforces + FAANG

> **Goal:** Learn STL by mapping real-world stories to hidden competitive-programming patterns.  
> **Target:** Codeforces 800 → 2200 and FAANG interview readiness.  
> **Language:** C++17.  
> **Main Skill:** Read a story, remove the story, detect the repeated operation, choose STL fast.

---

# 000. Master Index

```text
MiniSTL_RealWorld_Problems.md
│
├── Part 0  : How To Convert Story → STL Pattern
├── Part 1  : Frequency / Counting Problems
├── Part 2  : Sorting / Greedy Problems
├── Part 3  : Prefix / Subarray Problems
├── Part 4  : Two Pointers / Sliding Window
├── Part 5  : Stack / Boundary Problems
├── Part 6  : Deque / Window Maximum-Minimum
├── Part 7  : Heap / Scheduling / Top-K
├── Part 8  : Set / Lower Bound / Nearest Problems
├── Part 9  : Multiset / Dynamic Window Problems
├── Part 10 : Sweep Line / Interval Problems
├── Part 11 : Graph STL Problems
├── Part 12 : Median / Two Multisets
├── Part 13 : Cache Design / LRU / LFU
├── Part 14 : Production Backend STL Examples
├── Part 15 : Codeforces Real-World Pattern Forms
├── Part 16 : FAANG STL Interview Forms
├── Part 17 : Debugging Atlas
└── Part 18 : Final Recognition Cheat Sheet
```

---

# PART 0 : HOW TO CONVERT STORY → STL PATTERN

## 0.1 The Real Trick

Most problems hide STL inside a story.

```text
Story:
    "There are tickets, customers, rooms, logs, users, messages..."

Real operation:
    count / search / order / nearest / max / expire / merge / active / median
```

Your job:

```text
Story
  ↓
Objects
  ↓
Operations
  ↓
Constraints
  ↓
Pattern
  ↓
STL
```

---

## 0.2 Master Conversion Diagram

```text
                    PROBLEM STORY
                         |
              Remove nouns and names
                         |
              Find repeated operation
                         |
       +-----------------+-----------------+
       |                 |                 |
    Count             Ordered           Current Best
       |                 |                 |
unordered_map      set/multiset      priority_queue
       |
previous states

       +-----------------+-----------------+
       |                 |                 |
    Window            Boundary          Intervals
       |                 |                 |
 deque / map          stack             sweep line
```

---

## 0.3 Story Word → STL Pattern

```text
customers with tickets         -> multiset / set.lower_bound
meeting rooms                  -> heap / sweep line
server logs                    -> queue / deque / map
cache                          -> list + unordered_map
leaderboard                    -> set / map / priority_queue
tasks by priority              -> priority_queue
nearest available seat         -> set.lower_bound
stock span                     -> monotonic stack
sliding sensor window          -> deque / multiset
traffic lights                 -> set + multiset
online median                  -> two heaps / two multisets
URL hit analytics              -> unordered_map / heap
search autocomplete            -> map / trie / vector sort
Kafka partition lag            -> priority_queue / map
Redis LRU                      -> list + unordered_map
scheduler                      -> heap
```

---

# PART 1 : FREQUENCY / COUNTING REAL-WORLD PROBLEMS

---

## 1.1 Fraud Duplicate Transactions

### Story

A payment company receives transaction IDs. Count how many duplicate transaction pairs exist.

### Remove Story

```text
transaction ID = value
duplicate pair = previous same value
```

### Recognition

```text
Need count previous same key
→ unordered_map
```

### ASCII Diagram

```text
transactions:
A B A C A

scan:
A -> seen 0 pair
B -> seen 0 pair
A -> seen 1 pair
C -> seen 0 pair
A -> seen 2 pairs

total = 3
```

### Dry Run

```text
freq = {}

x=A
ans += 0
freq[A]=1

x=B
ans += 0
freq[B]=1

x=A
ans += 1
freq[A]=2

x=C
ans += 0
freq[C]=1

x=A
ans += 2
freq[A]=3
```

### C++17 Template

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    unordered_map<string, long long> freq;
    long long ans = 0;

    for (int i = 0; i < n; i++) {
        string id;
        cin >> id;
        ans += freq[id];
        freq[id]++;
    }

    cout << ans << '\n';
}
```

### Bugs

```text
answer may exceed int
unordered_map may need reserve for huge input
do not use map unless sorted output needed
```

### CF Form

```text
count pairs with same value
count pairs with same remainder
count previous equal prefix
```

### FAANG Form

```text
Two Sum frequency
duplicate detection
group by key
```

---

## 1.2 Same Remainder Buckets

### Story

A load balancer assigns requests to `k` servers by `requestId % k`. Count pairs that land on the same server.

### Pattern

```text
same remainder -> frequency map/vector
```

### ASCII

```text
request ids: 4 7 10 13
k = 3

4 % 3 = 1
7 % 3 = 1
10% 3 = 1
13% 3 = 1

All go to bucket 1
pairs = 4C2 = 6
```

### Template

```cpp
long long countSameRemainder(vector<long long>& a, int k) {
    vector<long long> freq(k, 0);
    long long ans = 0;

    for (long long x : a) {
        int r = ((x % k) + k) % k;
        ans += freq[r];
        freq[r]++;
    }

    return ans;
}
```

### Recognition Sentence

```text
Same modulo class -> bucket frequency.
```

---

## 1.3 Log Error Code Frequency

### Story

Given millions of error codes, find the top occurring error code.

### Pattern

```text
frequency -> unordered_map
current max -> track variable
```

### Diagram

```text
logs:
500 404 500 401 500 404

freq:
500 -> 3
404 -> 2
401 -> 1
```

### Template

```cpp
unordered_map<int,int> freq;
int bestCode = -1, bestCount = 0;

for (int code : logs) {
    int c = ++freq[code];
    if (c > bestCount) {
        bestCount = c;
        bestCode = code;
    }
}
```

### Production Use

```text
API gateway error analytics
Kafka consumer failure counts
Redis command stats
Nginx access log aggregation
```

---

# PART 2 : SORTING / GREEDY REAL-WORLD PROBLEMS

---

## 2.1 Boat Rescue / Elevator Pairing

### Story

People have weights. Each boat carries at most two people and max weight `limit`. Minimize boats.

### Hidden Operation

```text
pair lightest with heaviest if possible
```

### STL

```text
vector + sort + two pointers
```

### Diagram

```text
weights sorted:
1 2 2 3
L     R

limit = 3

1 + 3 > 3 -> 3 goes alone
1 + 2 <=3 -> pair
2 goes alone
```

### Template

```cpp
int boats(vector<int>& w, int limit) {
    sort(w.begin(), w.end());

    int l = 0, r = (int)w.size() - 1;
    int ans = 0;

    while (l <= r) {
        if (w[l] + w[r] <= limit) l++;
        r--;
        ans++;
    }

    return ans;
}
```

### Bugs

```text
while l <= r, not l < r
do not forget single remaining person
```

---

## 2.2 Meeting Selection

### Story

Choose max number of meetings that can fit in one room.

### Pattern

```text
sort by earliest end time
```

### Diagram

```text
Meeting intervals:

A: [1----4]
B:   [2--3]
C:      [4--5]

Choose B then C
```

### Template

```cpp
sort(meetings.begin(), meetings.end(), [](auto &a, auto &b) {
    return a.second < b.second;
});

int lastEnd = -1;
int ans = 0;

for (auto [s, e] : meetings) {
    if (s >= lastEnd) {
        ans++;
        lastEnd = e;
    }
}
```

### Recognition

```text
maximize count of non-overlapping intervals -> sort by end.
```

---

## 2.3 Assign Cookies / Resources

### Story

Children need minimum cookie sizes. Cookies have sizes. Maximize satisfied children.

### Pattern

```text
sort both arrays + greedy pointer
```

### Diagram

```text
need:    1 2 3
cookies: 1 1 2 3

child 1 gets 1
child 2 gets 2
child 3 gets 3
```

### Template

```cpp
int solve(vector<int>& need, vector<int>& cookies) {
    sort(need.begin(), need.end());
    sort(cookies.begin(), cookies.end());

    int i = 0, j = 0;
    while (i < need.size() && j < cookies.size()) {
        if (cookies[j] >= need[i]) i++;
        j++;
    }
    return i;
}
```

---

# PART 3 : PREFIX / SUBARRAY REAL-WORLD PROBLEMS

---

## 3.1 Bank Balance Target Window

### Story

Daily net changes are given. Count how many continuous periods have total balance change exactly `K`.

### Hidden Math

```text
sum(l..r) = pref[r] - pref[l-1]
Need sum K:
pref[l-1] = pref[r] - K
```

### STL

```text
unordered_map<prefix,count>
```

### Diagram

```text
a = [1, 2, 3, -2, 2]
K = 3

prefix:
0 1 3 6 4 6

At prefix 6:
need 3
seen prefix 3 -> found subarray
```

### Template

```cpp
long long countSubarraySumK(vector<int>& a, long long k) {
    unordered_map<long long,long long> freq;
    freq[0] = 1;

    long long pref = 0, ans = 0;

    for (int x : a) {
        pref += x;
        ans += freq[pref - k];
        freq[pref]++;
    }

    return ans;
}
```

### Bugs

```text
initialize freq[0] = 1
use long long
works with negative numbers
```

---

## 3.2 Equal Login/Logout Days

### Story

Given days encoded as `1 = more logins`, `0 = more logouts`, find longest balanced period.

### Transform

```text
0 -> -1
1 -> +1
balanced means sum = 0
same prefix appears twice
```

### STL

```text
unordered_map<prefix, firstIndex>
```

### Template

```cpp
int longestBalanced(vector<int>& a) {
    unordered_map<int,int> first;
    first[0] = -1;

    int pref = 0, best = 0;

    for (int i = 0; i < a.size(); i++) {
        pref += (a[i] == 1 ? 1 : -1);

        if (first.count(pref)) {
            best = max(best, i - first[pref]);
        } else {
            first[pref] = i;
        }
    }

    return best;
}
```

---

# PART 4 : TWO POINTERS / SLIDING WINDOW

---

## 4.1 Longest User Session Without Duplicate Page

### Story

A user visits pages. Find longest continuous sequence without repeating a page.

### Pattern

```text
sliding window + last position/frequency
```

### Diagram

```text
pages:
A B C A D

window grows:
A
A B
A B C
B C A   remove old A
B C A D
```

### Template

```cpp
int longestUnique(vector<string>& pages) {
    unordered_map<string,int> cnt;
    int l = 0, best = 0;

    for (int r = 0; r < pages.size(); r++) {
        cnt[pages[r]]++;

        while (cnt[pages[r]] > 1) {
            cnt[pages[l]]--;
            l++;
        }

        best = max(best, r - l + 1);
    }

    return best;
}
```

---

## 4.2 API Requests With At Most K Users

### Story

A log stream contains user IDs. Find longest continuous segment with at most `K` distinct users.

### Pattern

```text
sliding window + frequency map
```

### Template

```cpp
int longestAtMostK(vector<int>& a, int k) {
    unordered_map<int,int> cnt;
    int l = 0, best = 0;

    for (int r = 0; r < a.size(); r++) {
        cnt[a[r]]++;

        while ((int)cnt.size() > k) {
            cnt[a[l]]--;
            if (cnt[a[l]] == 0) cnt.erase(a[l]);
            l++;
        }

        best = max(best, r - l + 1);
    }

    return best;
}
```

### Recognition

```text
contiguous + at most K distinct -> sliding window + map.
```

---

# PART 5 : STACK / BOUNDARY PROBLEMS

---

## 5.1 Stock Span / Price Warmer Day

### Story

For each day, find the next day with a higher stock price.

### Pattern

```text
next greater element -> monotonic stack
```

### Diagram

```text
price:
70 60 75 71 80

stack unresolved:
70
70 60
75 resolves 60 and 70
```

### Template

```cpp
vector<int> nextGreater(vector<int>& a) {
    int n = a.size();
    vector<int> ans(n, -1);
    stack<int> st;

    for (int i = 0; i < n; i++) {
        while (!st.empty() && a[st.top()] < a[i]) {
            ans[st.top()] = i;
            st.pop();
        }
        st.push(i);
    }

    return ans;
}
```

---

## 5.2 Histogram Server Capacity

### Story

Each bar is capacity. Find largest rectangle capacity block.

### Pattern

```text
nearest smaller left/right -> monotonic stack
```

### Diagram

```text
heights:
2 1 5 6 2 3

For height 5:
left smaller = index 1
right smaller = index 4
width = 4 - 1 - 1 = 2
area = 5 * 2 = 10
```

### Template

```cpp
long long largestRectangle(vector<int>& h) {
    stack<int> st;
    long long best = 0;
    int n = h.size();

    for (int i = 0; i <= n; i++) {
        int cur = (i == n ? 0 : h[i]);

        while (!st.empty() && h[st.top()] > cur) {
            int height = h[st.top()];
            st.pop();
            int left = st.empty() ? -1 : st.top();
            int width = i - left - 1;
            best = max(best, 1LL * height * width);
        }

        st.push(i);
    }

    return best;
}
```

---

# PART 6 : DEQUE / WINDOW MAXIMUM-MINIMUM

---

## 6.1 Sensor Window Maximum

### Story

A temperature sensor emits values every second. For each 5-second window, find the maximum.

### Pattern

```text
fixed window max -> monotonic deque
```

### Diagram

```text
values: 2 1 3 4 6
k=3

i=0 dq=[0] -> 2
i=1 dq=[0,1] -> 2,1
i=2 remove smaller: pop 1, pop 0, push 2
     dq=[2], max=3
```

### Template

```cpp
vector<int> maxSlidingWindow(vector<int>& a, int k) {
    deque<int> dq;
    vector<int> ans;

    for (int i = 0; i < a.size(); i++) {
        while (!dq.empty() && dq.front() <= i - k) dq.pop_front();
        while (!dq.empty() && a[dq.back()] <= a[i]) dq.pop_back();

        dq.push_back(i);

        if (i >= k - 1) ans.push_back(a[dq.front()]);
    }

    return ans;
}
```

### Bugs

```text
store index, not value
expire old indices
handle k=1
```

---

## 6.2 0-1 BFS Road Network

### Story

Road cost is either 0 or 1. Find shortest path.

### Pattern

```text
0 cost -> push_front
1 cost -> push_back
deque
```

### Template

```cpp
vector<int> dist(n, INT_MAX);
deque<int> dq;

dist[src] = 0;
dq.push_front(src);

while (!dq.empty()) {
    int u = dq.front();
    dq.pop_front();

    for (auto [v, w] : g[u]) {
        if (dist[v] > dist[u] + w) {
            dist[v] = dist[u] + w;
            if (w == 0) dq.push_front(v);
            else dq.push_back(v);
        }
    }
}
```

---

# PART 7 : HEAP / SCHEDULING / TOP-K

---

## 7.1 CPU Task Scheduler

### Story

Tasks arrive with processing time. Always run the shortest available task.

### Pattern

```text
current best/minimum repeatedly -> min heap
```

### Diagram

```text
available tasks:
A: 8ms
B: 2ms
C: 5ms

min heap top = B
```

### Template

```cpp
priority_queue<pair<int,int>, vector<pair<int,int>>, greater<pair<int,int>>> pq;
// {processingTime, taskId}
```

---

## 7.2 Top K Error Codes

### Story

Find top K most frequent API error codes.

### Pattern

```text
frequency map + min heap size K
```

### Template

```cpp
vector<int> topKFrequent(vector<int>& a, int k) {
    unordered_map<int,int> freq;
    for (int x : a) freq[x]++;

    priority_queue<pair<int,int>, vector<pair<int,int>>, greater<pair<int,int>>> pq;

    for (auto [x, c] : freq) {
        pq.push({c, x});
        if (pq.size() > k) pq.pop();
    }

    vector<int> ans;
    while (!pq.empty()) {
        ans.push_back(pq.top().second);
        pq.pop();
    }

    reverse(ans.begin(), ans.end());
    return ans;
}
```

---

## 7.3 Meeting Rooms

### Story

Given meetings, find minimum rooms required.

### Pattern

```text
sort by start + min heap of end times
```

### Diagram

```text
Meeting starts:
[1,4], [2,5], [6,8]

heap holds end times:
start 1 -> room ending 4
start 2 -> 2 < 4, need new room
start 6 -> 6 >= 4, reuse room
```

### Template

```cpp
int minRooms(vector<pair<int,int>>& intervals) {
    sort(intervals.begin(), intervals.end());

    priority_queue<int, vector<int>, greater<int>> ends;

    int best = 0;

    for (auto [s, e] : intervals) {
        while (!ends.empty() && ends.top() <= s) ends.pop();
        ends.push(e);
        best = max(best, (int)ends.size());
    }

    return best;
}
```

---

# PART 8 : SET / LOWER_BOUND / NEAREST PROBLEMS

---

## 8.1 Concert Tickets

### Story

Customers ask for ticket price limit `x`. Give the most expensive ticket with price `<= x`.

### Pattern

```text
dynamic sorted duplicates -> multiset
largest <= x -> prev(upper_bound)
```

### Diagram

```text
tickets:
5 7 7 10

customer x=8

upper_bound(8) -> 10
previous -> 7
erase one 7
```

### Template

```cpp
multiset<int> tickets;

int getTicket(int x) {
    auto it = tickets.upper_bound(x);
    if (it == tickets.begin()) return -1;
    --it;
    int ans = *it;
    tickets.erase(it);
    return ans;
}
```

### Bugs

```text
use multiset because duplicate prices exist
erase iterator, not value
check begin before decrement
```

---

## 8.2 Nearest Free Seat

### Story

Seats become free and occupied. For a requested seat `x`, find nearest available seat >= x.

### Pattern

```text
set.lower_bound
```

### Template

```cpp
set<int> freeSeats;

int assign(int x) {
    auto it = freeSeats.lower_bound(x);
    if (it == freeSeats.end()) return -1;
    int seat = *it;
    freeSeats.erase(it);
    return seat;
}
```

---

## 8.3 Traffic Lights

### Story

Insert traffic lights on a road. After each insertion, output longest segment without traffic light.

### Pattern

```text
set positions + multiset segment lengths
```

### Diagram

```text
road: 0 ---------------- 10
insert 4

positions: 0,4,10
lengths: 4,6
max = 6

insert 7
positions: 0,4,7,10
lengths: 4,3,3
max = 4
```

### Template

```cpp
set<int> pos;
multiset<int> len;

void addLight(int x) {
    auto r = pos.upper_bound(x);
    auto l = prev(r);

    int oldLen = *r - *l;
    len.erase(len.find(oldLen));

    len.insert(x - *l);
    len.insert(*r - x);
    pos.insert(x);
}
```

---

# PART 9 : MULTISET / DYNAMIC WINDOW

---

## 9.1 Sliding Window Median

### Story

For every K-minute window of stock prices, output median.

### Pattern

```text
median + delete -> two multisets
```

### Diagram

```text
low  = smaller half
high = larger half

low:  [1 2 3]
high: [5 8]

median = max(low) = 3
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

# PART 10 : SWEEP LINE / INTERVAL PROBLEMS

---

## 10.1 Server Active Connections

### Story

Connections have start and end times. Find max concurrent connections.

### Pattern

```text
events + sort + active count
```

### Diagram

```text
[1----5]
  [2------7]
      [5---8]

events:
1 +1
2 +1
5 -1 or +1 depending interval rule
```

### Template

```cpp
int maxOverlap(vector<pair<int,int>>& intervals) {
    vector<pair<int,int>> events;

    for (auto [l, r] : intervals) {
        events.push_back({l, +1});
        events.push_back({r, -1});
    }

    sort(events.begin(), events.end());

    int active = 0, best = 0;
    for (auto [t, d] : events) {
        active += d;
        best = max(best, active);
    }

    return best;
}
```

---

## 10.2 Merge Booking Ranges

### Story

Merge overlapping hotel bookings.

### Pattern

```text
sort by start + merge
```

### Template

```cpp
vector<pair<int,int>> mergeIntervals(vector<pair<int,int>>& v) {
    sort(v.begin(), v.end());

    vector<pair<int,int>> res;

    for (auto [l, r] : v) {
        if (res.empty() || res.back().second < l) {
            res.push_back({l, r});
        } else {
            res.back().second = max(res.back().second, r);
        }
    }

    return res;
}
```

---

# PART 11 : GRAPH STL PROBLEMS

---

## 11.1 Social Network BFS

### Story

Find minimum friendship hops from user A to user B.

### Pattern

```text
unweighted shortest path -> queue BFS
```

### Template

```cpp
vector<int> dist(n, -1);
queue<int> q;

dist[src] = 0;
q.push(src);

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
```

---

## 11.2 Delivery Route Shortest Path

### Story

Roads have positive weights. Find shortest delivery cost.

### Pattern

```text
Dijkstra -> priority_queue
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

### Bug

```text
If weights can be negative, Dijkstra is invalid.
```

---

# PART 12 : CACHE DESIGN / LRU / LFU

---

## 12.1 LRU Cache

### Story

Evict least recently used page.

### Pattern

```text
list stores recency order
unordered_map stores key -> iterator
```

### Diagram

```text
Most recent                         Least recent
front                                      back
[9] -> [4] -> [7] -> [2]

map:
9 -> iterator to node 9
4 -> iterator to node 4
```

### Template

```cpp
class LRUCache {
    int cap;
    list<pair<int,int>> dll; // {key,value}
    unordered_map<int, list<pair<int,int>>::iterator> mp;

public:
    LRUCache(int capacity) : cap(capacity) {}

    int get(int key) {
        if (!mp.count(key)) return -1;

        auto it = mp[key];
        int val = it->second;

        dll.erase(it);
        dll.push_front({key, val});
        mp[key] = dll.begin();

        return val;
    }

    void put(int key, int value) {
        if (mp.count(key)) {
            dll.erase(mp[key]);
        } else if ((int)dll.size() == cap) {
            auto [oldKey, oldVal] = dll.back();
            dll.pop_back();
            mp.erase(oldKey);
        }

        dll.push_front({key, value});
        mp[key] = dll.begin();
    }
};
```

### Production

```text
Redis LRU approximation
HTTP cache
database page cache
CDN object cache
```

---

# PART 13 : PRODUCTION BACKEND STL EXAMPLES

## 13.1 Redis

```text
Redis concept                  STL mental model
------------------------------------------------
key lookup                     unordered_map
sorted set                     set/map-like ordered structure
LRU eviction                   list + map
expiry wheel                   priority_queue / buckets
pub-sub subscribers            unordered_map<channel, vector<client>>
```

## 13.2 Kafka

```text
Kafka concept                  STL mental model
------------------------------------------------
topic partitions               vector<queue<Message>>
consumer lag priority          priority_queue
offset map                     unordered_map<partition, offset>
rebalance assignments          map/set
retry delay queue              priority_queue by timestamp
```

## 13.3 Search Engine

```text
Search concept                 STL mental model
------------------------------------------------
inverted index                 unordered_map<word, vector<docId>>
top results                    priority_queue
autocomplete                   map / trie / sorted vector
dedup URLs                     unordered_set
ranking by score               heap
```

## 13.4 Operating System

```text
OS concept                     STL mental model
------------------------------------------------
ready queue                    queue
priority scheduler             priority_queue
LRU page replacement            list + unordered_map
file descriptor table           vector / unordered_map
timer events                    priority_queue
```

---

# PART 14 : CODEFORCES REAL-WORLD PATTERN FORMS

## 14.1 800–1000

```text
001 count unique colors                    -> set
002 count soldiers by strength             -> map
003 sort coins by value                     -> vector + sort
004 prefix chocolate sum                    -> prefix vector
005 compare sorted teams                    -> sort
006 remove duplicate usernames              -> set
007 frequency of letters                    -> vector<int> freq
008 min max distance after sorting           -> sort
009 first item at least price                -> lower_bound
010 pair value with original index           -> vector<pair<int,int>>
```

## 14.2 1100–1400

```text
011 two people fit in boat                  -> sort + two pointers
012 same modulo pairs                       -> frequency
013 longest segment at most K types         -> sliding window
014 assign resources greedily               -> sort both arrays
015 answer threshold queries offline         -> sort + upper_bound
016 compress large city IDs                 -> coordinate compression
017 count pair complement                   -> unordered_map
018 sort tasks by deadline                  -> custom comparator
019 remove repeated page visits             -> sliding window + map
020 static range sum queries                -> prefix
```

## 14.3 1500–1800

```text
021 next greater monster                    -> stack
022 largest histogram wall                  -> monotonic stack
023 max in each battle window               -> deque
024 kth strongest warrior                   -> heap
025 merge overlapping spells                -> sort intervals
026 max active portals                      -> sweep line
027 nearest available ticket                -> multiset upper_bound
028 heap with deleted old events            -> lazy heap
029 subarray sum equal target               -> prefix map
030 shortest route positive weights         -> Dijkstra heap
```

## 14.4 1900–2200

```text
031 sliding median                          -> two multisets
032 traffic lights longest segment          -> set + multiset
033 dynamic interval containing point        -> map.upper_bound
034 offline queries with activation          -> sort queries + DS
035 Dijkstra over states                     -> priority_queue
036 active interval set                      -> set
037 event tie-breaking problem               -> sweep line
038 coordinate compression for Fenwick prep   -> vector + lower_bound
039 dynamic MEX                              -> set of missing
040 priority with stale frequency            -> heap + lazy validation
```

---

# PART 15 : FAANG STL INTERVIEW FORMS

```text
001 Two Sum                                  -> unordered_map
002 Contains Duplicate                       -> unordered_set
003 Group Anagrams                           -> unordered_map<string, vector<string>>
004 Top K Frequent                           -> unordered_map + heap
005 Kth Largest                              -> min heap
006 Merge Intervals                          -> sort
007 Insert Interval                          -> vector scan / merge
008 Meeting Rooms                            -> sort
009 Meeting Rooms II                         -> min heap
010 Employee Free Time                       -> sweep line / heap
011 Sliding Window Maximum                   -> deque
012 Longest Substring Without Repeat         -> sliding window + map
013 Minimum Window Substring                 -> sliding window + map
014 Valid Parentheses                        -> stack
015 Daily Temperatures                       -> monotonic stack
016 Next Greater Element                     -> stack
017 Largest Rectangle Histogram              -> stack
018 Median Finder                            -> two heaps
019 Sliding Window Median                    -> two multisets
020 LRU Cache                                -> list + unordered_map
021 LFU Cache                                -> freq buckets + maps
022 Random Pick Weight                       -> prefix + lower_bound
023 Time Based Key Value Store               -> unordered_map + vector + upper_bound
024 Design Twitter                           -> unordered_map + heap
025 Merge K Sorted Lists                     -> heap
026 Reorganize String                        -> frequency + heap
027 Task Scheduler                           -> heap / greedy counts
028 Network Delay Time                       -> Dijkstra heap
029 Cheapest Flights with K Stops            -> queue / heap states
030 Word Ladder                              -> BFS queue + unordered_set
031 Clone Graph                              -> unordered_map + queue/DFS
032 Course Schedule                          -> graph + queue topo
033 Alien Dictionary                         -> graph + topo
034 Accounts Merge                           -> DSU + unordered_map
035 Find Median from Data Stream             -> two heaps
036 Design Hit Counter                       -> queue / deque
037 Moving Average from Data Stream          -> queue
038 Logger Rate Limiter                      -> unordered_map
039 First Unique Number                      -> queue + freq map
040 Min Stack                                -> stack pairs
041 Max Stack                                -> list + map
042 Snapshot Array                           -> vector of pairs + upper_bound
043 Range Module                             -> map intervals
044 My Calendar I                            -> map / set
045 My Calendar II                           -> sweep line / map
046 Exam Room                                -> set / priority_queue
047 Design Search Autocomplete               -> map/trie + heap
048 Design In-Memory File System             -> map tree
049 Find Duplicate File                      -> unordered_map content -> paths
050 Analyze User Website Pattern             -> map + set + sorting
```

---

# PART 16 : DEBUGGING ATLAS BY STL

## 16.1 vector

```text
WA:
    wrong index
    forgot 0-index conversion

TLE:
    erase(begin()) repeatedly
    insert in middle repeatedly

MLE:
    vector<vector<int>> too huge

Fix:
    use deque for front pop
    use reserve when size known
```

## 16.2 unordered_map

```text
WA:
    operator[] creates missing key
    negative modulo key not normalized

TLE:
    hash collision
    too many rehashes

Fix:
    reserve()
    custom_hash
    use vector when key range small
```

## 16.3 set / multiset

```text
WA:
    erase(value) removes all in multiset
    decrement begin
    dereference end

TLE:
    std::lower_bound on set

Fix:
    use s.lower_bound()
    erase(iterator)
    check boundaries
```

## 16.4 priority_queue

```text
WA:
    stale entries not cleaned
    wrong comparator direction

TLE:
    heap grows forever

Fix:
    lazy deletion with validation
    if (d != dist[u]) continue
```

## 16.5 stack

```text
WA:
    wrong inequality for equal values
    forgot remaining stack elements

Fix:
    decide < vs <= based on strict/non-strict boundary
```

## 16.6 deque

```text
WA:
    storing values instead of indices
    old index not expired
    wrong pop inequality with duplicates

Fix:
    store indices
    expire before answer
```

---

# PART 17 : 5-SECOND RECOGNITION DRILLS

```text
Need previous same value              -> unordered_map
Need previous same prefix             -> unordered_map
Need first >= x dynamic               -> set.lower_bound
Need largest <= x dynamic duplicate   -> multiset + upper_bound
Need max of every window              -> deque
Need median with deletion             -> two multisets
Need top K                            -> heap
Need merge ranges                     -> sort intervals
Need max overlap                      -> sweep line
Need next greater                     -> stack
Need shortest unweighted              -> queue BFS
Need shortest weighted positive       -> Dijkstra heap
Need cache recency                    -> list + unordered_map
Need dynamic interval contains point  -> map.upper_bound
Need large value rank                 -> compression
Need offline threshold query          -> sort query + pointer
Need stale best candidate             -> heap + lazy deletion
```

---

# PART 18 : FINAL CHEAT SHEET

```text
Story                         Hidden Operation              STL
---------------------------------------------------------------------------
tickets                       largest <= x                  multiset
meeting rooms                 earliest ending room           min heap
logs                          frequency                     unordered_map
cache                         recency movement               list + map
leaderboard                   ordered ranking                set/map/heap
stock warmer day              next greater                   stack
sensor window max             fixed window max               deque
traffic lights                split intervals                set + multiset
subarray target               previous prefix                unordered_map
Dijkstra route                current min distance           priority_queue
server concurrency            active intervals               sweep line
median stream                 middle of dynamic values        two heaps
median window                 middle + deletion              two multisets
autocomplete                  sorted prefix traversal         map/trie
top errors                    frequency + top K              heap
```

---

# Final One-Picture Summary

```text
                     REAL WORLD STORY
                            |
                    Remove the story
                            |
                    Find operation
                            |
     ------------------------------------------------------
     |          |          |          |          |          |
  Count      Nearest     Best      Window    Boundary   Interval
     |          |          |          |          |          |
 unordered    set       heap      deque      stack      sweep
   map      multiset
     |
 previous state

     ------------------------------------------------------
     |          |          |          |
   Median     Graph      Cache      Offline
     |          |          |          |
 two sets   queue/heap  list+map   sort+pointer
```

STL mastery means every story becomes an operation, and every operation points to one reliable container.
