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


---

# 10/10 EXPANSION PACK : REAL-WORLD STL PATTERN MASTERY

This expansion upgrades the file from a useful note into a **real training chapter**.  
The goal is not only to know STL, but to recognize the hidden operation inside real-world stories.

---

# PART 19 : STORY → OPERATION → STL MASTER FLOW

## 19.1 Universal Real-World Translation Engine

```text
Real-world problem statement
        |
        v
Remove business nouns
        |
        v
Find repeated action
        |
        v
Ask: what must be fast?
        |
        v
Choose STL
        |
        v
Write invariant
        |
        v
Dry run
        |
        v
Code
```

## 19.2 One Giant Decision Diagram

```text
                                      STORY
                                        |
                     +------------------+------------------+
                     |                                     |
              Static data?                            Dynamic data?
                     |                                     |
          +----------+----------+              +-----------+-----------+
          |                     |              |                       |
     Need sorted?          Need count?     Need nearest?          Need best?
          |                     |              |                       |
    vector + sort        unordered_map       set / multiset       priority_queue
          |                     |              |                       |
   lower_bound?          previous state?   duplicates?             stale values?
          |                     |              |                       |
 binary search         prefix + map        multiset              lazy deletion
```

## 19.3 Real-World Recognition Matrix

```text
Story Domain        Hidden Operation                 STL Pattern
---------------------------------------------------------------------------
Bank fraud          count repeated transaction        unordered_map
API logs            top K frequent error              map + heap
Ticket booking      largest <= budget                 multiset.upper_bound
Server rooms        earliest end reusable             min heap
Calendar            overlap count                     sweep line
Cache               move recent item to front          list + unordered_map
Traffic lights      split intervals dynamically        set + multiset
Stock prices        next greater future value          monotonic stack
Sensor stream       max/min in last K readings         deque
Leaderboard         ordered ranks                      set / map / PBDS
Search engine       word -> document list              unordered_map + vector
Kafka retry         earliest retry timestamp           priority_queue
Rate limiter        expire old timestamps              queue / deque
Median dashboard    dynamic median                     two heaps / multisets
Road network        shortest positive route            Dijkstra heap
```

---

# PART 20 : MORE REAL-WORLD EXAMPLES WITH FULL DRY RUNS

---

## 20.1 API Rate Limiter Per User

### Story

Each request has `(timestamp, userId)`. Allow a request only if that user made fewer than `K` requests in the last `W` seconds.

### Hidden Operation

```text
For each user:
    keep recent timestamps
    remove expired timestamps
    count remaining
```

### STL

```text
unordered_map<userId, queue<int>>
```

### ASCII Diagram

```text
Window W = 10 seconds
K = 3

time line:
0----5----10----15----20

user A timestamps:
[3, 7, 12]

At t=15:
valid window = [6,15]
3 expired
remaining = [7,12]
allow new request
```

### Dry Run

```text
K=3, W=10

Request (3,A):
queue[A] = []
after cleanup = []
size=0 < 3 -> allow
queue[A]=[3]

Request (7,A):
valid window starts at -3
queue[A]=[3]
size=1 < 3 -> allow
queue[A]=[3,7]

Request (12,A):
valid window starts at 2
queue[A]=[3,7]
size=2 < 3 -> allow
queue[A]=[3,7,12]

Request (15,A):
valid window starts at 5
3 is expired
queue[A]=[7,12]
size=2 < 3 -> allow
queue[A]=[7,12,15]
```

### C++17 Template

```cpp
#include <bits/stdc++.h>
using namespace std;

class RateLimiter {
    int K, W;
    unordered_map<string, queue<int>> mp;

public:
    RateLimiter(int k, int w) : K(k), W(w) {}

    bool allow(int t, const string& user) {
        auto &q = mp[user];

        while (!q.empty() && q.front() < t - W + 1) {
            q.pop();
        }

        if ((int)q.size() >= K) return false;

        q.push(t);
        return true;
    }
};
```

### Bugs

```text
Wrong boundary: last W seconds may mean [t-W+1, t] or (t-W, t].
Need clarify inclusive/exclusive.
Do not store all requests forever.
unordered_map<string, queue<int>> works because each user has separate window.
```

### Recognition Sentence

```text
Per-key sliding time window -> unordered_map + queue/deque.
```

---

## 20.2 Recent Error Burst Detector

### Story

An observability system receives error timestamps. Trigger alert if more than `K` errors happened in the last `W` seconds.

### Hidden Operation

```text
Remove expired timestamps
Check current window size
```

### STL

```text
queue<int> or deque<int>
```

### ASCII Diagram

```text
W = 60 sec

errors:
10 20 50 80

At t=80:
valid window = [21,80]
10 expired
20 expired if strict > 60
remaining: 50,80
```

### Template

```cpp
class ErrorBurst {
    int W, K;
    queue<int> q;

public:
    ErrorBurst(int w, int k) : W(w), K(k) {}

    bool addError(int t) {
        while (!q.empty() && q.front() < t - W + 1) {
            q.pop();
        }

        q.push(t);
        return (int)q.size() > K;
    }
};
```

### Production Mapping

```text
Prometheus alert window
Nginx 5xx burst detector
Kafka consumer error spike
Payment failure monitor
```

---

## 20.3 Shopping Cart Coupon Pair

### Story

Find if any two item prices add exactly to coupon target.

### Hidden Operation

```text
For current price x, need previous target-x
```

### STL

```text
unordered_set
```

### Diagram

```text
prices = 20 35 15 40
target = 55

x=20 need 35, not seen
seen={20}

x=35 need 20, seen!
```

### Code

```cpp
bool hasPair(vector<int>& price, int target) {
    unordered_set<int> seen;

    for (int x : price) {
        if (seen.count(target - x)) return true;
        seen.insert(x);
    }

    return false;
}
```

### FAANG Form

```text
Two Sum -> unordered_map/unordered_set
```

---

## 20.4 Website Visit Pattern: Most Common 3-Sequence

### Story

Given user website visits, find most common 3-page sequence across users.

### Hidden Operations

```text
group visits by user
sort by timestamp
generate unique 3-sequences per user
count globally
```

### STL

```text
unordered_map<user, vector<pair<time, site>>>
set<tuple<string,string,string>>
map/unordered_map<tuple,count>
```

### ASCII Diagram

```text
User U1:
t1 home
t2 search
t3 product
t4 cart

3-sequences:
(home, search, product)
(home, search, cart)
(home, product, cart)
(search, product, cart)

Count each sequence once per user.
```

### Code Sketch

```cpp
map<tuple<string,string,string>, int> cnt;

for (auto &[user, visits] : byUser) {
    sort(visits.begin(), visits.end());

    set<tuple<string,string,string>> uniqueForUser;

    int m = visits.size();
    for (int i = 0; i < m; i++) {
        for (int j = i + 1; j < m; j++) {
            for (int k = j + 1; k < m; k++) {
                uniqueForUser.insert({
                    visits[i].second,
                    visits[j].second,
                    visits[k].second
                });
            }
        }
    }

    for (auto seq : uniqueForUser) {
        cnt[seq]++;
    }
}
```

### Recognition

```text
Group by user + sort events + count unique patterns -> unordered_map + vector + set.
```

---

## 20.5 CDN Cache Hit Simulation

### Story

A CDN cache has capacity `C`. When a URL is accessed, it becomes most recently used. If capacity is full, evict least recently used URL.

### STL

```text
list<string> recency
unordered_map<string, list<string>::iterator>
```

### ASCII Diagram

```text
Most recent                              Least recent
front                                           back

[/home] -> [/login] -> [/pricing]

access /login:

remove /login from middle
push /login to front

[/login] -> [/home] -> [/pricing]
```

### Dry Run

```text
capacity = 2

put A:
list = [A]

put B:
list = [B,A]

get A:
move A front
list = [A,B]

put C:
capacity full
evict B
list = [C,A]
```

### Code

```cpp
class LRU {
    int cap;
    list<string> order;
    unordered_map<string, list<string>::iterator> pos;

public:
    LRU(int c) : cap(c) {}

    bool access(const string& url) {
        bool hit = pos.count(url);

        if (hit) {
            order.erase(pos[url]);
        } else if ((int)order.size() == cap) {
            string old = order.back();
            order.pop_back();
            pos.erase(old);
        }

        order.push_front(url);
        pos[url] = order.begin();

        return hit;
    }
};
```

### Production

```text
CDN cache
database page cache
Redis-like key eviction
HTTP client connection pool
```

---

## 20.6 Kafka Retry Scheduler

### Story

Failed messages must be retried at future timestamps. Always process the earliest retry that is due.

### Hidden Operation

```text
current smallest retryTime repeatedly
```

### STL

```text
priority_queue with greater<>
```

### ASCII Diagram

```text
Retry heap by timestamp:

          105
        /     \
      120     130
     /
   200

At currentTime=110:
top=105 due -> process
next top=120 not due -> stop
```

### Code

```cpp
struct Message {
    int retryTime;
    string id;

    bool operator>(const Message& other) const {
        return retryTime > other.retryTime;
    }
};

priority_queue<Message, vector<Message>, greater<Message>> pq;
```

### Bug

```text
Comparator must produce min-heap by retryTime.
If message priority changes, priority_queue cannot update in-place.
Push new state and ignore stale old state.
```

---

## 20.7 Leaderboard With Dynamic Scores

### Story

Players update scores. Need top player and ordered ranking.

### STL Options

```text
unordered_map<player, score>
set<pair<score, player>>
```

### ASCII Diagram

```text
set sorted ascending:

(100,A) (120,C) (150,B)

top player = rbegin() = (150,B)

Update B from 150 to 90:
erase (150,B)
insert (90,B)

(90,B) (100,A) (120,C)
```

### Code

```cpp
unordered_map<string,int> score;
set<pair<int,string>> board;

void update(string player, int newScore) {
    if (score.count(player)) {
        board.erase({score[player], player});
    }

    score[player] = newScore;
    board.insert({newScore, player});
}

string topPlayer() {
    return board.rbegin()->second;
}
```

### Recognition

```text
Need update + ordered max -> map for current value + set for order.
```

---

## 20.8 Dynamic MEX in Game Inventory

### Story

You have item IDs. After adding/removing items, find the smallest missing ID.

### Pattern

```text
set of missing numbers
frequency array/map
```

### ASCII Diagram

```text
IDs possible: 0 1 2 3 4 5

present:
0 1 3 4

missing set:
{2,5}

MEX = *missing.begin() = 2
```

### Code

```cpp
int N = 200000;
vector<int> freq(N + 2, 0);
set<int> missing;

for (int i = 0; i <= N + 1; i++) missing.insert(i);

void add(int x) {
    if (x > N + 1) return;
    freq[x]++;
    if (freq[x] == 1) missing.erase(x);
}

void removeOne(int x) {
    if (x > N + 1) return;
    freq[x]--;
    if (freq[x] == 0) missing.insert(x);
}

int mex() {
    return *missing.begin();
}
```

### CF Form

```text
Dynamic MEX -> set of missing values.
```

---

## 20.9 File Deduplication by Content Hash

### Story

Given files and content hashes, group duplicate files.

### Pattern

```text
hash -> list of files
```

### STL

```text
unordered_map<string, vector<string>>
```

### ASCII

```text
hash h1 -> fileA, fileC
hash h2 -> fileB
hash h3 -> fileD, fileE, fileF

duplicates = groups with size > 1
```

### Code

```cpp
unordered_map<string, vector<string>> groups;

for (auto &[file, hash] : files) {
    groups[hash].push_back(file);
}

for (auto &[hash, list] : groups) {
    if (list.size() > 1) {
        // duplicate group
    }
}
```

---

## 20.10 Search Autocomplete With Sorted Vector

### Story

Given product names and a typed prefix, return top lexicographic suggestions.

### Pattern

```text
sort strings
lower_bound(prefix)
scan next few matching strings
```

### ASCII Diagram

```text
sorted:
apple
application
banana
car
cart
cat

prefix = "ca"

lower_bound("ca") -> car
scan:
car  yes
cart yes
cat  yes
```

### Code

```cpp
vector<string> suggest(vector<string>& words, string prefix) {
    sort(words.begin(), words.end());

    vector<string> ans;
    auto it = lower_bound(words.begin(), words.end(), prefix);

    while (it != words.end() && ans.size() < 3) {
        if (it->substr(0, prefix.size()) == prefix) {
            ans.push_back(*it);
            ++it;
        } else {
            break;
        }
    }

    return ans;
}
```

### Bug

```text
substr creates copy, acceptable for small interview problem.
For high scale, compare by indices or trie.
```

---

# PART 21 : ADVANCED STL REAL-WORLD PATTERNS

---

## 21.1 Time-Based Key Value Store

### Story

Store `(key, value, timestamp)`. For `get(key, t)`, return value with largest timestamp `<= t`.

### STL

```text
unordered_map<string, vector<pair<int,string>>>
upper_bound
```

### Diagram

```text
key = "price"

timestamps:
10 -> A
20 -> B
30 -> C

get(price, 25)
upper_bound(25) points to 30
previous = 20 -> B
```

### Code

```cpp
class TimeMap {
    unordered_map<string, vector<pair<int,string>>> mp;

public:
    void set(string key, string value, int timestamp) {
        mp[key].push_back({timestamp, value});
    }

    string get(string key, int timestamp) {
        auto &v = mp[key];

        auto it = upper_bound(v.begin(), v.end(), make_pair(timestamp, string(1, char(127))));

        if (it == v.begin()) return "";

        --it;
        return it->second;
    }
};
```

### Recognition

```text
Static increasing timestamps per key -> vector + upper_bound.
```

---

## 21.2 Range Module / Calendar Booking

### Story

Maintain booked intervals. Check overlap before inserting a new interval.

### STL

```text
map<start,end>
```

### Diagram

```text
booked:
[10,20] [30,40]

new [18,25]
previous interval [10,20] overlaps because 20 > 18

new [20,30]
no overlap in half-open interval logic
```

### Code

```cpp
map<int,int> intervals;

bool book(int l, int r) {
    auto it = intervals.lower_bound(l);

    if (it != intervals.end() && it->first < r) return false;

    if (it != intervals.begin()) {
        auto prevIt = prev(it);
        if (prevIt->second > l) return false;
    }

    intervals[l] = r;
    return true;
}
```

### Bug

```text
Be clear about [l,r) vs [l,r].
For [l,r), overlap if previous.end > l and next.start < r.
```

---

## 21.3 Exam Room Seat Allocation

### Story

Always seat a student to maximize distance from nearest occupied seat.

### STL

```text
set of occupied seats
priority_queue of candidate intervals with lazy validation
```

### ASCII

```text
N = 10
occupied: 0, 9

candidate interval:
(0,9) -> best seat 4

after seat 4:
intervals:
(0,4), (4,9)
```

### Recognition

```text
Need best interval repeatedly + dynamic invalidation -> heap + set + lazy validation.
```

---

## 21.4 Snapshot Array

### Story

Array supports set(index,value), snap(), get(index,snapId).

### STL

```text
vector<vector<pair<snapId,value>>>
upper_bound
```

### Diagram

```text
index 2 history:
snap 0 -> 5
snap 3 -> 9
snap 7 -> 4

get(2,5)
upper_bound(5) -> snap 7
previous -> snap 3 -> value 9
```

### Code

```cpp
class SnapshotArray {
    int snapId = 0;
    vector<vector<pair<int,int>>> hist;

public:
    SnapshotArray(int length) {
        hist.resize(length);
        for (auto &v : hist) v.push_back({0, 0});
    }

    void set(int index, int val) {
        if (hist[index].back().first == snapId) {
            hist[index].back().second = val;
        } else {
            hist[index].push_back({snapId, val});
        }
    }

    int snap() {
        return snapId++;
    }

    int get(int index, int id) {
        auto &v = hist[index];
        auto it = upper_bound(v.begin(), v.end(), make_pair(id, INT_MAX));
        --it;
        return it->second;
    }
};
```

---

# PART 22 : DEEP DRY RUN GALLERY

---

## 22.1 Prefix Map Deep Dry Run

Problem:

```text
a = [1, -1, 2, 3, -2, 2]
K = 3
```

Diagram:

```text
i      x      pref      need=pref-K      freq before        ans add
---------------------------------------------------------------------
-      -       0             -           {0:1}              -
0      1       1            -2           no                 0
1     -1       0            -3           no                 0
2      2       2            -1           no                 0
3      3       5             2           freq[2]=1          +1
4     -2       3             0           freq[0]=2          +2
5      2       5             2           freq[2]=1          +1

total ans = 4
```

Visual:

```text
prefix equal difference K:

pref[r] - pref[l-1] = K

pref[r] = 5
K = 3
need old prefix = 2
```

---

## 22.2 Monotonic Stack Deep Dry Run

Problem:

```text
Next greater element
a = [2, 1, 4, 3, 5]
```

Dry run:

```text
i=0, x=2
stack=[]
push 0
stack=[0(2)]

i=1, x=1
a[top]=2 < 1? no
push 1
stack=[0(2),1(1)]

i=2, x=4
a[1]=1 < 4 -> ans[1]=4, pop
a[0]=2 < 4 -> ans[0]=4, pop
push 2
stack=[2(4)]

i=3, x=3
a[2]=4 < 3? no
push 3
stack=[2(4),3(3)]

i=4, x=5
a[3]=3 < 5 -> ans[3]=5
a[2]=4 < 5 -> ans[2]=5
push 4
stack=[4(5)]
```

Result:

```text
2 -> 4
1 -> 4
4 -> 5
3 -> 5
5 -> -1
```

ASCII:

```text
2  1  4  3  5
|  |  |  |  |
+--+--4  +--5
```

---

## 22.3 Deque Deep Dry Run

Problem:

```text
a = [1, 3, -1, -3, 5, 3, 6, 7]
k = 3
```

Dry run:

```text
i=0 x=1
dq=[]
push 0
dq=[0:1]

i=1 x=3
pop back 0 because 1 <= 3
push 1
dq=[1:3]

i=2 x=-1
push 2
dq=[1:3,2:-1]
window [0..2], max=3

i=3 x=-3
push 3
dq=[1:3,2:-1,3:-3]
window [1..3], max=3

i=4 x=5
expire indices <=1: pop 1
pop 3 because -3 <= 5
pop 2 because -1 <= 5
push 4
dq=[4:5]
window [2..4], max=5
```

Visual invariant:

```text
Deque values always decreasing:

front                         back
max candidate                 weaker candidates
[5] -> [3] -> [1]
```

---

## 22.4 Heap Deep Dry Run: Top K Frequent

Input:

```text
errors = [500,404,500,401,404,500]
k = 2
```

Frequency:

```text
500 -> 3
404 -> 2
401 -> 1
```

Min heap size K:

```text
push (3,500)
heap=[(3,500)]

push (2,404)
heap=[(2,404),(3,500)]

push (1,401)
heap=[(1,401),(3,500),(2,404)]
size > 2 -> pop smallest (1,401)

remaining:
(2,404), (3,500)
```

Answer:

```text
500, 404
```

---

## 22.5 Sweep Line Deep Dry Run

Intervals:

```text
[1,4), [2,5), [4,6)
```

Events:

```text
(1,+1)
(4,-1)
(2,+1)
(5,-1)
(4,+1)
(6,-1)
```

Sorted:

```text
(1,+1)
(2,+1)
(4,-1)
(4,+1)
(5,-1)
(6,-1)
```

Run:

```text
time=1 active=1 best=1
time=2 active=2 best=2
time=4 end first: active=1
time=4 start: active=2
time=5 active=1
time=6 active=0
```

For half-open intervals `[l,r)`, ending at 4 and starting at 4 do not overlap.

---

# PART 23 : 100 MORE 5-SECOND RECOGNITION DRILLS

```text
001 per-user recent requests                    -> unordered_map + queue
002 alert in last 60 sec                         -> queue/deque
003 two prices hit coupon target                 -> unordered_set
004 content hash duplicate files                 -> unordered_map<vector>
005 dynamic leaderboard update                   -> map + set
006 dynamic MEX                                  -> set of missing values
007 retry by earliest timestamp                  -> min heap
008 task with highest priority                   -> priority_queue
009 seat nearest greater/equal request           -> set.lower_bound
010 ticket highest affordable                    -> multiset.upper_bound + prev

011 cache least recently used                    -> list + unordered_map
012 top K URLs by hits                           -> map + heap
013 search prefix suggestions                    -> sort + lower_bound
014 time-based key lookup                        -> vector + upper_bound
015 calendar non-overlap booking                 -> map intervals
016 snapshot get previous value                  -> vector history + upper_bound
017 meeting room count                           -> heap or sweep line
018 traffic lights segment split                 -> set + multiset
019 max active sessions                          -> sweep line
020 merge booked ranges                          -> sort + merge

021 stock next warmer day                        -> monotonic stack
022 largest rectangle of servers                 -> monotonic stack
023 max temperature last K readings              -> deque
024 min temperature last K readings              -> deque
025 median dashboard stream                      -> two heaps
026 sliding median of prices                     -> two multisets
027 BFS social hops                              -> queue
028 0-1 road cost                                -> deque
029 weighted delivery cost                       -> Dijkstra heap
030 group anagrams                               -> unordered_map

031 first unique event                           -> queue + frequency
032 logger rate limiter                          -> unordered_map timestamp
033 moving average                               -> queue
034 min stack                                    -> stack of pair/min
035 max stack                                    -> list + map
036 LFU cache                                    -> frequency buckets
037 Twitter feed top tweets                      -> heap
038 merge K sorted logs                          -> heap
039 random pick weight                           -> prefix + lower_bound
040 range module                                 -> map intervals

041 closest active point                         -> set
042 count values <= x static                     -> sorted vector + upper_bound
043 count values <= x dynamic                    -> Fenwick/PBDS prep
044 compress city IDs                            -> vector sort unique
045 remove duplicate usernames                   -> set
046 count previous same difference               -> unordered_map
047 normalized fraction key                      -> map pair
048 nearest lower active value                   -> prev(lower_bound)
049 erase one duplicate                          -> multiset.find erase(it)
050 stale distance state                         -> if(d!=dist[u]) continue

051 stale heap frequency                         -> compare heap value to current map
052 active interval IDs                          -> set
053 earliest expiring token                      -> min heap
054 bounded recent events                        -> deque
055 sorted unique output                         -> set/vector unique
056 offline threshold query                      -> sort queries + pointer
057 all query values known                       -> offline sort
058 dynamic ordered key-value                    -> map
059 sparse DP states                             -> unordered_map
060 dense DP states                              -> vector

061 matrix BFS                                   -> queue<pair<int,int>>
062 topological zero indegree                    -> queue
063 shortest path with states                    -> priority_queue tuple
064 online median with delete                    -> two multisets
065 stream median insert-only                    -> two heaps
066 word frequency                               -> unordered_map<string,int>
067 char frequency                               -> vector<int>(26)
068 sorted duplicates window                     -> multiset
069 window unique                                -> map count / set
070 positive sum window                          -> sliding window

071 negative sum window                          -> prefix map
072 histogram boundary                           -> stack
073 next smaller                                 -> stack
074 previous greater                             -> stack
075 next greater circular                        -> stack over 2N
076 intervals inclusive                          -> event r+1
077 intervals half-open                          -> end before start
078 long strings substring repeated              -> avoid substr copies
079 pair sorted by second                        -> custom comparator
080 tie score descending id ascending            -> strict comparator

081 large unordered_map TLE risk                 -> custom_hash
082 vector erase front TLE                       -> deque
083 need arbitrary delete from heap              -> lazy deletion or multiset
084 need sorted traversal                        -> map/set
085 no sorted traversal                          -> unordered_map
086 need rank order                              -> PBDS/Fenwick prep
087 interval contains point                      -> map.upper_bound
088 split range                                  -> set boundaries
089 current min and max duplicate                -> multiset
090 current max only                             -> priority_queue

091 BFS layer processing                         -> queue size loop
092 multi-source BFS                             -> queue all sources initially
093 deduplicate generated states                 -> unordered_set
094 event processing by time                     -> priority_queue
095 restore original index after sort            -> pair(value,index)
096 compare sorted arrays                        -> vector + sort
097 lower_bound answer index                     -> iterator - begin
098 lower_bound on set                           -> s.lower_bound
099 reverse sorted order                         -> rbegin/rend
100 final rule                                   -> operation first, STL second
```

---

# PART 24 : CODEFORCES 800–2200 REAL-WORLD FORMS EXPANDED

## 24.1 800–1000 Forms

```text
001 count unique countries                       -> set
002 sort soldiers by height                      -> sort
003 count same shirt colors                      -> map
004 prefix candies eaten                         -> prefix
005 first affordable item in sorted prices        -> lower_bound
006 remove duplicate numbers                     -> sort + unique
007 compare two sorted teams                     -> vector sort
008 count lowercase letters                      -> vector<int> freq(26)
009 restore original index after sorting          -> pair
010 find min/max difference                      -> sort
011 count distinct days                          -> set
012 count same rating                            -> map
013 sort strings                                 -> sort
014 group equal numbers                          -> map
015 static range sum                             -> prefix
```

## 24.2 1100–1400 Forms

```text
016 same remainder groups                        -> frequency vector
017 two pointers pair sum                        -> sort + two pointers
018 longest no duplicate segment                 -> sliding window
019 at most K types                              -> sliding window + map
020 greedy resources                             -> sort both
021 custom comparator by deadline                -> sort lambda
022 coordinate compression                       -> vector sort unique
023 count pairs by complement                    -> unordered_map
024 offline count <= x                           -> sort + upper_bound
025 assign smallest sufficient item              -> multiset/lower_bound
026 remove expired events                        -> queue
027 count anagram groups                         -> map sorted-string key
028 schedule by earliest finish                  -> sort by end
029 compare frequency profiles                   -> vector freq
030 normalize modulo                             -> ((x%k)+k)%k
```

## 24.3 1500–1800 Forms

```text
031 prefix sum K                                 -> unordered_map
032 prefix divisible by K                        -> remainder map
033 next greater                                 -> stack
034 next smaller                                 -> stack
035 histogram rectangle                          -> stack
036 sliding max                                  -> deque
037 sliding min                                  -> deque
038 K largest                                    -> min heap size K
039 Top K frequent                               -> freq + heap
040 meeting rooms                                -> min heap
041 merge intervals                              -> sort + merge
042 max overlap                                  -> sweep line
043 ticket allocation                            -> multiset
044 nearest active value                         -> set.lower_bound
045 lazy heap deletion                           -> heap + del map
046 Dijkstra                                     -> priority_queue
047 0-1 BFS                                      -> deque
048 multi-source BFS                             -> queue
049 active interval set                          -> set
050 current min/max duplicate                    -> multiset
```

## 24.4 1900–2200 Forms

```text
051 sliding median                               -> two multisets
052 traffic lights                               -> set + multiset
053 dynamic MEX                                  -> set missing
054 offline activation queries                   -> sort queries + DS
055 range map containment                        -> map.upper_bound
056 Dijkstra with expanded state                 -> priority_queue tuple
057 event tie-breaking                           -> custom event sort
058 heap stale frequency                         -> lazy validation
059 split/merge intervals dynamically            -> map
060 order statistic preparation                  -> compression + Fenwick/PBDS
061 closest pair active set                      -> set
062 interval union length                        -> sweep line
063 dynamic leaderboard                          -> set + map
064 calendar booking                             -> map intervals
065 two heaps with lazy deletion                 -> advanced median
066 graph topological scheduling                 -> queue
067 shortest path with coupons                   -> Dijkstra state
068 DSU + maps                                   -> component frequency
069 rollback/offline flavor                      -> vector events + stack history
070 Mo's algorithm prep                          -> vector queries + frequency
```

---

# PART 25 : FAANG INTERVIEW DEEPER MAPPING

## 25.1 Grouped by STL

```text
unordered_map:
    Two Sum
    Group Anagrams
    Subarray Sum Equals K
    Logger Rate Limiter
    Time Based KV Store
    Clone Graph visited map
    Accounts Merge email owner

set/map:
    My Calendar
    Range Module
    Exam Room
    Leaderboard
    Time intervals
    Dynamic nearest value

priority_queue:
    Merge K Sorted Lists
    Top K Frequent
    Kth Largest
    Task Scheduler
    Reorganize String
    Network Delay Time
    Twitter Feed

stack:
    Valid Parentheses
    Min Stack
    Daily Temperatures
    Largest Rectangle
    Decode String
    Asteroid Collision

deque:
    Sliding Window Maximum
    Moving Average
    Hit Counter
    0-1 BFS variant
    Recent events window

list + unordered_map:
    LRU Cache
    Max Stack
    LFU Cache buckets

vector + binary search:
    Random Pick Weight
    Snapshot Array
    TimeMap
    Search Suggestions
```

## 25.2 FAANG Problem → Recognition Sentence

```text
Two Sum:
    Need previous complement -> unordered_map.

Group Anagrams:
    Need group by normalized key -> unordered_map.

Top K Frequent:
    Need count + top K -> unordered_map + heap.

Sliding Window Maximum:
    Need max in every fixed window -> monotonic deque.

Median Finder:
    Need middle of stream -> two heaps.

LRU Cache:
    Need O(1) lookup and O(1) recency move -> list + unordered_map.

TimeMap:
    Need previous timestamp <= t -> vector + upper_bound.

My Calendar:
    Need detect interval overlap dynamically -> ordered map.

Range Module:
    Need maintain disjoint intervals -> map.

Network Delay:
    Need shortest weighted path -> Dijkstra heap.
```

---

# PART 26 : ASCII DIAGRAM LIBRARY

## 26.1 Frequency Map

```text
input stream:
A B A C B A

map:
A -> 3
B -> 2
C -> 1
```

## 26.2 Prefix Map

```text
a:       2  -1   3  -2
pref: 0  2   1   4   2

same prefix appears again:
subarray sum between them = 0
```

## 26.3 Two Pointers

```text
sorted:
1 2 4 6 9
L       R

if sum too small -> L++
if sum too big   -> R--
```

## 26.4 Sliding Window

```text
l
|        
A B C A D
      |
      r

window moves only forward
```

## 26.5 Stack

```text
top
 |
[3]
[5]
[9]

unresolved previous elements
```

## 26.6 Deque

```text
front                         back
max candidate                 weaker future candidates
[9] -> [6] -> [2]
```

## 26.7 Heap

```text
          2
        /   \
       5     8
      / \
     9   10

top = current minimum
```

## 26.8 Set lower_bound

```text
set:
1 4 7 10 15

lower_bound(8) -> 10
upper_bound(10) -> 15
prev(upper_bound(10)) -> 10
```

## 26.9 Multiset

```text
multiset:
5 5 7 7 7 10

erase(7) removes all 7s
erase(find(7)) removes one 7
```

## 26.10 Sweep Line

```text
time:
1   2   3   4   5   6
|---A---|
    |---B-------|
        |---C---|

events count active intervals
```

## 26.11 Two Heaps Median

```text
low max heap        high min heap
smaller half        larger half

    3                   5
   / \                 / \
  1   2               7   8

median = top(low)
```

## 26.12 LRU

```text
front                                      back
most recent                            least recent

[C] -> [A] -> [D] -> [B]
                 ^
                 map points to node
```

---

# PART 27 : FINAL 10/10 STUDY LOOP

For every real-world problem, write this scratchpad:

```text
Story:
Objects:
Repeated operation:
Can sort?
Online/offline:
Need duplicates?
Need nearest?
Need current best?
Need window expiry?
Need previous state?
STL:
Invariant:
Dry run:
Complexity:
Bug risk:
```

Example:

```text
Story: ticket customer asks max affordable ticket
Objects: ticket prices, customer budget
Repeated operation: find largest <= x and delete one copy
Can sort? dynamic deletion after each query
Need duplicates? yes
STL: multiset
Invariant: multiset contains currently available tickets
Bug risk: erase(value) removes all copies
```

---

# FINAL 10/10 SUMMARY

```text
Real world problem
       ↓
Remove nouns
       ↓
Find operation
       ↓
Select STL
       ↓
Maintain invariant
       ↓
Dry run
       ↓
Code safe template
       ↓
Check bugs
```

The strongest STL users do not ask:

```text
Which STL should I use?
```

They ask:

```text
What operation must be fast?
```

That one question converts stories into accepted solutions.
