# Range Mapping Phase-Wise Practice Guide

## How to Use This

```text
Phase 1 -> learn interval meaning and point coverage
Phase 2 -> learn offline binary search with L/R endpoints
Phase 3 -> learn online interval set operations
Phase 4 -> learn merge/delete/full coverage thinking
Phase 5 -> learn sweep line, difference array, and coordinate compression
```

---

## Clickable Index

- [How to Use This](#how-to-use-this)
- [Phase 1 — Recognition Problems](#phase-1--recognition-problems)
  - [1. Check if a Point is Covered by Any Interval — Brute Force](#1-check-if-a-point-is-covered-by-any-interval--brute-force)
  - [2. Count Covered Points in Small Coordinate Range](#2-count-covered-points-in-small-coordinate-range)
- [Phase 2 — Offline Binary Search on Endpoints](#phase-2--offline-binary-search-on-endpoints)
  - [3. Offline Point Coverage Check](#3-offline-point-coverage-check)
  - [4. Count Number of Intervals Covering a Point](#4-count-number-of-intervals-covering-a-point)
  - [5. Minimum Interval to Include Each Query](#5-minimum-interval-to-include-each-query)
- [Phase 3 — Online Range Mapping with set<pair<int,int>>](#phase-3--online-range-mapping-with-setpairintint)
  - [6. Online Insert Interval and Point Query](#6-online-insert-interval-and-point-query)
  - [7. Check Whether a New Interval Overlaps Existing Intervals](#7-check-whether-a-new-interval-overlaps-existing-intervals)
- [Phase 4 — Merged Interval Maintenance](#phase-4--merged-interval-maintenance)
  - [8. Insert and Merge Overlapping Intervals](#8-insert-and-merge-overlapping-intervals)
  - [9. Remove a Range from Covered Intervals](#9-remove-a-range-from-covered-intervals)
  - [10. Check Whether Full Range is Covered](#10-check-whether-full-range-is-covered)
- [Phase 5 — Sweep Line / Difference Array / Compression](#phase-5--sweep-line--difference-array--compression)
  - [11. Maximum Number of Overlapping Intervals](#11-maximum-number-of-overlapping-intervals)
  - [12. Range Addition Using Difference Array](#12-range-addition-using-difference-array)
  - [13. Coordinate Compression for Huge Ranges](#13-coordinate-compression-for-huge-ranges)
  - [14. Offline Event Sweep for Query Points](#14-offline-event-sweep-for-query-points)
- [Final Phase Summary](#final-phase-summary)
- [Master Rule](#master-rule)
- [Final Checklist Before Coding](#final-checklist-before-coding)

---

# Phase 1 — Recognition Problems

Goal:

```text
Understand what interval coverage means on a number line.
```

Core question:

```text
Does [l, r] contain x?
```

---

## 1. Check if a Point is Covered by Any Interval — Brute Force

### Problem Statement

Given intervals and a point `x`, check whether any interval covers `x`.

Example:

```text
ranges = [[2,5], [8,10], [13,16]]
x = 4
answer = true
```

### Pattern

```text
Interval [l, r] covers x if l <= x <= r.
Scan every interval.
```

### Thinking

```text
Small constraints -> direct scan is enough.
Every interval gets one chance to prove x is covered.
```

### Dry Run

```text
ranges = [[2,5], [8,10], [13,16]]
x = 4

Check [2,5]   -> 2 <= 4 <= 5  yes
Answer = covered
```

For `x = 12`:

```text
Check [2,5]    -> no
Check [8,10]   -> no
Check [13,16]  -> no
Answer = not covered
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isCoveredBrute(vector<pair<int,int>>& ranges, int x) {
    for (auto [l, r] : ranges) {
        if (l <= x && x <= r) return true;
    }
    return false;
}

int main() {
    vector<pair<int,int>> ranges = {{2,5}, {8,10}, {13,16}};

    cout << isCoveredBrute(ranges, 4) << "\n";   // 1
    cout << isCoveredBrute(ranges, 12) << "\n";  // 0
}
```

---

## 2. Count Covered Points in Small Coordinate Range

### Problem Statement

Given intervals with small coordinates, count how many integer points are covered by at least one interval.

Example:

```text
ranges = [[2,5], [4,7], [10,11]]
covered points = {2,3,4,5,6,7,10,11}
answer = 8
```

### Pattern

```text
Small coordinate range -> mark covered points using array.
```

### Thinking

```text
Instead of storing intervals, directly mark the number line.
```

### Dry Run

```text
range [2,5] marks 2 3 4 5
range [4,7] marks 4 5 6 7
range [10,11] marks 10 11

covered points:
2 3 4 5 6 7 10 11
answer = 8
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int countCoveredSmall(vector<pair<int,int>>& ranges, int maxCoord) {
    vector<int> covered(maxCoord + 1, 0);

    for (auto [l, r] : ranges) {
        for (int x = l; x <= r; x++) {
            covered[x] = 1;
        }
    }

    int ans = 0;
    for (int x = 0; x <= maxCoord; x++) {
        ans += covered[x];
    }
    return ans;
}

int main() {
    vector<pair<int,int>> ranges = {{2,5}, {4,7}, {10,11}};
    cout << countCoveredSmall(ranges, 20) << "\n"; // 8
}
```

---

# Phase 2 — Offline Binary Search on Endpoints

Goal:

```text
Answer point queries faster when all intervals are known before queries.
```

Core question:

```text
How many intervals started before x and how many already ended before x?
```

---

## 3. Offline Point Coverage Check

### Problem Statement

Given all intervals first, answer whether point `x` is covered.

Example:

```text
ranges = [[2,5], [3,10], [13,16]]
x = 4  -> covered
x = 12 -> not covered
```

### Pattern

```text
Sort left endpoints L.
Sort right endpoints R.

covered = total - count(r < x) - count(l > x)
```

### Thinking

```text
An interval does not cover x if:
1. it ends before x: r < x
2. it starts after x: l > x
```

### Dry Run

```text
ranges = [[2,5], [3,10], [13,16]]
L = [2,3,13]
R = [5,10,16]
```

For `x = 4`:

```text
count(l > 4) = 1   -> [13]
count(r < 4) = 0
covered = 3 - 1 - 0 = 2
answer = covered
```

For `x = 12`:

```text
count(l > 12) = 1  -> [13]
count(r < 12) = 2  -> [5,10]
covered = 3 - 1 - 2 = 0
answer = not covered
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct OfflineCoverage {
    vector<int> L, R;
    int n;

    OfflineCoverage(vector<pair<int,int>>& ranges) {
        n = ranges.size();
        for (auto [l, r] : ranges) {
            L.push_back(l);
            R.push_back(r);
        }
        sort(L.begin(), L.end());
        sort(R.begin(), R.end());
    }

    bool isCovered(int x) {
        int startAfter = L.end() - upper_bound(L.begin(), L.end(), x); // l > x
        int endBefore = lower_bound(R.begin(), R.end(), x) - R.begin(); // r < x
        int covered = n - startAfter - endBefore;
        return covered > 0;
    }
};

int main() {
    vector<pair<int,int>> ranges = {{2,5}, {3,10}, {13,16}};
    OfflineCoverage oc(ranges);

    cout << oc.isCovered(4) << "\n";   // 1
    cout << oc.isCovered(12) << "\n";  // 0
}
```

---

## 4. Count Number of Intervals Covering a Point

### Problem Statement

Given intervals and point queries, return how many intervals cover each point.

Example:

```text
ranges = [[2,5], [3,10], [13,16]]
x = 4
answer = 2
```

### Pattern

```text
covering(x) = count(l <= x) - count(r < x)
```

### Thinking

```text
Intervals covering x are:
started by x - already ended before x
```

### Dry Run

```text
L = [2,3,13]
R = [5,10,16]
```

For `x = 4`:

```text
started = count(l <= 4) = 2
endedBefore = count(r < 4) = 0
answer = 2
```

For `x = 12`:

```text
started = count(l <= 12) = 2
endedBefore = count(r < 12) = 2
answer = 0
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct IntervalCounter {
    vector<int> L, R;

    IntervalCounter(vector<pair<int,int>>& ranges) {
        for (auto [l, r] : ranges) {
            L.push_back(l);
            R.push_back(r);
        }
        sort(L.begin(), L.end());
        sort(R.begin(), R.end());
    }

    int countCovering(int x) {
        int started = upper_bound(L.begin(), L.end(), x) - L.begin();      // l <= x
        int endedBefore = lower_bound(R.begin(), R.end(), x) - R.begin(); // r < x
        return started - endedBefore;
    }
};

int main() {
    vector<pair<int,int>> ranges = {{2,5}, {3,10}, {13,16}};
    IntervalCounter counter(ranges);

    cout << counter.countCovering(4) << "\n";   // 2
    cout << counter.countCovering(12) << "\n";  // 0
}
```

---

## 5. Minimum Interval to Include Each Query

### Problem Statement

For each query point, find the length of the smallest interval that contains it.

Example:

```text
intervals = [[1,4], [2,4], [3,6], [4,4]]
queries   = [2,3,4,5]
answer    = [3,3,1,4]
```

### Pattern

```text
Sort intervals by start.
Sort queries.
Use min-heap by interval length.
Remove intervals whose end < query.
```

### Thinking

```text
For each query q:
1. Add all intervals with l <= q.
2. Remove intervals with r < q.
3. Heap top is the smallest valid interval.
```

### Dry Run

```text
intervals sorted by l:
[1,4], [2,4], [3,6], [4,4]

query q = 4
add all intervals with l <= 4:
[1,4] length 4
[2,4] length 3
[3,6] length 4
[4,4] length 1

remove intervals with r < 4:
none

smallest length = 1 from [4,4]
answer for 4 = 1
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> minInterval(vector<vector<int>>& intervals, vector<int>& queries) {
    sort(intervals.begin(), intervals.end());

    vector<pair<int,int>> qs;
    for (int i = 0; i < (int)queries.size(); i++) {
        qs.push_back({queries[i], i});
    }
    sort(qs.begin(), qs.end());

    priority_queue<pair<int,int>, vector<pair<int,int>>, greater<pair<int,int>>> pq;
    vector<int> ans(queries.size(), -1);
    int i = 0;

    for (auto [q, id] : qs) {
        while (i < (int)intervals.size() && intervals[i][0] <= q) {
            int l = intervals[i][0];
            int r = intervals[i][1];
            pq.push({r - l + 1, r});
            i++;
        }

        while (!pq.empty() && pq.top().second < q) {
            pq.pop();
        }

        if (!pq.empty()) ans[id] = pq.top().first;
    }

    return ans;
}
```

---

# Phase 3 — Online Range Mapping with set<pair<int,int>>

Goal:

```text
Handle insert/query operations while processing input online.
```

Core question:

```text
What is the interval with the largest start <= x?
```

---

## 6. Online Insert Interval and Point Query

### Problem Statement

Support two operations:

```text
insert [l, r]
query x -> is x covered?
```

Example:

```text
insert [2,10]
insert [13,16]
query 4  -> true
query 12 -> false
```

### Pattern

```text
Use set<pair<int,int>>.
Find candidate interval using upper_bound({x, INT_MAX}).
Move one step back.
```

### Thinking

```text
upper_bound({x, INT_MAX}) gives first interval with start > x.
Previous interval has largest start <= x.
Only that interval can cover x.
```

### Dry Run

```text
ranges = {[2,10], [13,16]}
x = 4

upper_bound({4, INF}) -> [13,16]
previous interval -> [2,10]
4 <= 10 -> covered
```

For `x = 12`:

```text
upper_bound({12, INF}) -> [13,16]
previous interval -> [2,10]
12 <= 10 false
answer = not covered
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct RangeSet {
    set<pair<int,int>> ranges;

    void insertSimple(int l, int r) {
        ranges.insert({l, r});
    }

    bool isPointCovered(int x) {
        auto it = ranges.upper_bound({x, INT_MAX});
        if (it == ranges.begin()) return false;

        --it;
        return it->first <= x && x <= it->second;
    }
};

int main() {
    RangeSet rs;
    rs.insertSimple(2, 10);
    rs.insertSimple(13, 16);

    cout << rs.isPointCovered(4) << "\n";   // 1
    cout << rs.isPointCovered(12) << "\n";  // 0
}
```

---

## 7. Check Whether a New Interval Overlaps Existing Intervals

### Problem Statement

Given already booked intervals, check whether a new interval overlaps any existing interval.

Example:

```text
existing = [[2,5], [8,10]]
new = [6,7]
answer = false

new = [5,8]
answer = true for closed intervals
```

### Pattern

```text
Only two candidates matter:
1. first interval with start >= l
2. previous interval before it
```

### Thinking

```text
Closed intervals overlap if:
l1 <= r2 && l2 <= r1
```

### Dry Run

```text
existing = {[2,5], [8,10]}
new = [6,7]

lower_bound({6, -INF}) -> [8,10]
Check [8,10]: 8 <= 7 false
Check previous [2,5]: 5 >= 6 false
answer = no overlap
```

For `new = [5,8]`:

```text
lower_bound({5, -INF}) -> [8,10]
Check [8,10]: 8 <= 8 true -> overlap
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct CalendarClosedIntervals {
    set<pair<int,int>> ranges;

    bool hasOverlap(int l, int r) {
        auto it = ranges.lower_bound({l, INT_MIN});

        if (it != ranges.end() && it->first <= r) return true;

        if (it != ranges.begin()) {
            auto prevIt = prev(it);
            if (prevIt->second >= l) return true;
        }

        return false;
    }

    bool book(int l, int r) {
        if (hasOverlap(l, r)) return false;
        ranges.insert({l, r});
        return true;
    }
};
```

---

# Phase 4 — Merged Interval Maintenance

Goal:

```text
Maintain disjoint merged intervals after insert/delete operations.
```

Core question:

```text
Which intervals overlap, touch, split, or fully cover the query?
```

---

## 8. Insert and Merge Overlapping Intervals

### Problem Statement

Insert interval `[l, r]` into a set of disjoint intervals and merge overlaps.

Example:

```text
current = [[2,5], [8,10], [13,16]]
insert  = [4,9]
answer  = [[2,10], [13,16]]
```

### Pattern

```text
Find first possible overlap.
Merge while interval.start <= r + 1.
Erase old intervals.
Insert merged interval.
```

### Thinking

```text
If an old interval overlaps new interval, both become one bigger interval.
```

### Dry Run

```text
insert [4,9]

[2,5] overlaps -> merge [2,9]
[8,10] overlaps -> merge [2,10]
[13,16] no overlap

result = [[2,10], [13,16]]
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct RangeSet {
    set<pair<int,int>> ranges;

    void insertRange(int l, int r) {
        if (l > r) swap(l, r);

        auto it = ranges.lower_bound({l, INT_MIN});

        if (it != ranges.begin()) {
            auto prevIt = prev(it);
            if (prevIt->second >= l - 1) {
                it = prevIt;
            }
        }

        while (it != ranges.end() && it->first <= r + 1) {
            if (it->second < l - 1) {
                ++it;
                continue;
            }

            l = min(l, it->first);
            r = max(r, it->second);
            it = ranges.erase(it);
        }

        ranges.insert({l, r});
    }
};
```

---

## 9. Remove a Range from Covered Intervals

### Problem Statement

Remove coverage `[l, r]` from existing merged intervals.

Example:

```text
current = [[2,10], [13,16]]
remove  = [4,8]
answer  = [[2,3], [9,10], [13,16]]
```

### Pattern

```text
For every overlapping interval [a,b]:
left leftover  -> [a, l-1]
right leftover -> [r+1, b]
```

### Thinking

```text
Delete may shrink, split, or remove intervals completely.
```

### Dry Run

```text
remove [4,8]

existing [2,10]:
left part before remove = [2,3]
right part after remove = [9,10]

existing [13,16]: no overlap

result = [[2,3], [9,10], [13,16]]
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct RangeSet {
    set<pair<int,int>> ranges;

    void removeRange(int l, int r) {
        vector<pair<int,int>> addBack;

        auto it = ranges.lower_bound({l, INT_MIN});
        if (it != ranges.begin()) --it;

        while (it != ranges.end()) {
            auto [a, b] = *it;

            if (b < l) {
                ++it;
                continue;
            }
            if (a > r) break;

            it = ranges.erase(it);

            if (a < l) addBack.push_back({a, l - 1});
            if (r < b) addBack.push_back({r + 1, b});
        }

        for (auto p : addBack) {
            ranges.insert(p);
        }
    }
};
```

---

## 10. Check Whether Full Range is Covered

### Problem Statement

Given merged intervals, check if query range `[l, r]` is fully covered.

Example:

```text
ranges = [[2,10], [13,16]]
query [4,8]  -> true
query [4,12] -> false
```

### Pattern

```text
Find interval with largest start <= l.
Check if its end >= r.
```

### Thinking

```text
Because intervals are merged and disjoint, one interval must fully contain [l, r].
```

### Dry Run

```text
query [4,8]

upper_bound({4, INF}) -> [13,16]
previous -> [2,10]
10 >= 8 -> fully covered
```

For `[4,12]`:

```text
previous -> [2,10]
10 >= 12 false
answer = not fully covered
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct RangeSet {
    set<pair<int,int>> ranges;

    bool isRangeCovered(int l, int r) {
        auto it = ranges.upper_bound({l, INT_MAX});
        if (it == ranges.begin()) return false;

        --it;
        return it->first <= l && r <= it->second;
    }
};
```

---

# Phase 5 — Sweep Line / Difference Array / Compression

Goal:

```text
Solve range problems by converting intervals into events or compressed coordinates.
```

Core question:

```text
Can interval operations become prefix sums over ordered points?
```

---

## 11. Maximum Number of Overlapping Intervals

### Problem Statement

Given intervals, find the maximum number of intervals active at the same point.

Example:

```text
intervals = [[2,5], [3,10], [13,16]]
answer = 2
```

### Pattern

```text
Sweep line events:
start -> +1
end + 1 -> -1 for closed integer intervals
```

### Thinking

```text
Active count changes only at interval boundaries.
```

### Dry Run

```text
[2,5]   -> (2,+1), (6,-1)
[3,10]  -> (3,+1), (11,-1)
[13,16] -> (13,+1), (17,-1)

sorted events:
2 +1  active=1
3 +1  active=2  max=2
6 -1  active=1
11 -1 active=0
13 +1 active=1
17 -1 active=0

answer = 2
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxOverlapClosedIntervals(vector<pair<int,int>>& intervals) {
    vector<pair<int,int>> events;

    for (auto [l, r] : intervals) {
        events.push_back({l, +1});
        events.push_back({r + 1, -1});
    }

    sort(events.begin(), events.end());

    int active = 0;
    int ans = 0;

    for (auto [x, delta] : events) {
        active += delta;
        ans = max(ans, active);
    }

    return ans;
}
```

---

## 12. Range Addition Using Difference Array

### Problem Statement

Apply many updates:

```text
add value v to all points in [l, r]
```

Example:

```text
n = 8
updates:
[2,5] +1
[3,7] +1

final = [0,1,2,2,2,1,1,0]
```

### Pattern

```text
diff[l] += v
diff[r + 1] -= v
prefix sum gives final array
```

### Thinking

```text
Range update becomes two point updates.
```

### Dry Run

```text
diff[2] += 1
diff[6] -= 1

diff[3] += 1
diff[8] -= 1

prefix:
idx:   1 2 3 4 5 6 7 8
value: 0 1 2 2 2 1 1 0
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n = 8;
    vector<int> diff(n + 2, 0);

    auto addRange = [&](int l, int r, int val) {
        diff[l] += val;
        diff[r + 1] -= val;
    };

    addRange(2, 5, 1);
    addRange(3, 7, 1);

    vector<int> a(n + 1, 0);
    for (int i = 1; i <= n; i++) {
        a[i] = a[i - 1] + diff[i];
    }

    for (int i = 1; i <= n; i++) {
        cout << a[i] << " ";
    }
    cout << "\n";
}
```

---

## 13. Coordinate Compression for Huge Ranges

### Problem Statement

Coordinates are huge, but only few points matter. Compress them into small indices.

Example:

```text
coordinates = [1000000000, 5, 10, 5]
compressed:
5 -> 0
10 -> 1
1000000000 -> 2
```

### Pattern

```text
collect values
sort
unique
lower_bound to get compressed index
```

### Thinking

```text
When actual value is huge but relative order matters, replace value by rank.
```

### Dry Run

```text
original values:
[1000000000, 5, 10, 5]

sort:
[5, 5, 10, 1000000000]

unique:
[5, 10, 1000000000]

mapping:
5 -> 0
10 -> 1
1000000000 -> 2
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Compressor {
    vector<int> vals;

    void add(int x) {
        vals.push_back(x);
    }

    void build() {
        sort(vals.begin(), vals.end());
        vals.erase(unique(vals.begin(), vals.end()), vals.end());
    }

    int get(int x) {
        return lower_bound(vals.begin(), vals.end(), x) - vals.begin();
    }
};

int main() {
    Compressor comp;
    comp.add(1000000000);
    comp.add(5);
    comp.add(10);
    comp.add(5);
    comp.build();

    cout << comp.get(5) << "\n";          // 0
    cout << comp.get(10) << "\n";         // 1
    cout << comp.get(1000000000) << "\n"; // 2
}
```

---

## 14. Offline Event Sweep for Query Points

### Problem Statement

Given intervals and query points, count how many intervals cover each query point.

Example:

```text
intervals = [[2,5], [3,10]]
queries = [2,5,6]
answer = [1,2,1]
```

### Pattern

```text
Create events:
start = 0
query = 1
end   = 2

For closed intervals, order at same coordinate:
start before query before end
```

### Thinking

```text
Sweep from left to right.
active = number of intervals currently covering this coordinate.
```

### Dry Run

```text
events:
2 start
2 query id0
3 start
5 query id1
5 end
6 query id2
10 end

scan:
x=2 start active=1
x=2 query ans[0]=1
x=3 start active=2
x=5 query ans[1]=2
x=5 end active=1
x=6 query ans[2]=1
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Event {
    int x;
    int type; // 0=start, 1=query, 2=end
    int id;

    bool operator<(const Event& other) const {
        if (x != other.x) return x < other.x;
        return type < other.type;
    }
};

vector<int> countCoveringBySweep(vector<pair<int,int>>& intervals, vector<int>& queries) {
    vector<Event> events;

    for (auto [l, r] : intervals) {
        events.push_back({l, 0, -1});
        events.push_back({r, 2, -1});
    }

    for (int i = 0; i < (int)queries.size(); i++) {
        events.push_back({queries[i], 1, i});
    }

    sort(events.begin(), events.end());

    vector<int> ans(queries.size());
    int active = 0;

    for (auto e : events) {
        if (e.type == 0) active++;
        else if (e.type == 1) ans[e.id] = active;
        else active--;
    }

    return ans;
}
```

---

# Final Phase Summary

| Phase | Goal | Problems |
|---|---|---|
| Phase 1 | Understand interval coverage | Brute point check, count covered points |
| Phase 2 | Offline endpoint thinking | Point coverage, count covering, minimum interval query |
| Phase 3 | Online set thinking | Insert/query point, overlap check |
| Phase 4 | Merged interval maintenance | Insert merge, delete range, full coverage check |
| Phase 5 | Event/prefix thinking | Max overlap, difference array, compression, event sweep |

---

# Master Rule

```text
If the problem is about l <= x <= r,
range mapping / interval coverage is probably the pattern.
```

---

# Final Checklist Before Coding

```text
1. Are intervals closed [l, r] or half-open [l, r)?
2. Are queries offline or online?
3. Do I need boolean coverage or count coverage?
4. Are intervals overlapping or should they be merged?
5. Do I need insert/delete operations?
6. Are coordinates small, huge, or compressible?
7. Should I use brute force, binary search, set, sweep line, or difference array?
8. For set<pair<int,int>>, do I need upper_bound({x, INT_MAX}) or lower_bound({l, INT_MIN})?
```
