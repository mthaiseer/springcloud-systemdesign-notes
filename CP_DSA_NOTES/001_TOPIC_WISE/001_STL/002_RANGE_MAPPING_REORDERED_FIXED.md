# Range Mapping / Interval Coverage — Complete Technique Playbook

A CP/DSA guide for interval/range problems using STL, binary search, sweep line, and `set<pair<int,int>>`.

## Clickable Index

### Technique Playbook

- [0. Problem Family](#0-problem-family)
- [1. Core Mental Model](#1-core-mental-model)
- [2. STL Binary Search Helpers](#2-stl-binary-search-helpers)
- [3. Technique 1 — Brute Force Interval Scan](#3-technique-1--brute-force-interval-scan)
- [4. Technique 2 — Offline Point Coverage Check](#4-technique-2--offline-point-coverage-check)
- [5. Technique 3 — Count Number of Intervals Covering a Point](#5-technique-3--count-number-of-intervals-covering-a-point)
- [6. Technique 4 — Intermixed Insert + Point Query using `set<pair<int,int>>`](#6-technique-4--intermixed-insert--point-query-using-setpairintint)
- [7. Technique 5 — Insert and Merge Overlapping Intervals](#7-technique-5--insert-and-merge-overlapping-intervals)
- [8. Technique 6 — Delete / Remove a Range from Coverage](#8-technique-6--delete--remove-a-range-from-coverage)
- [9. Technique 7 — Check Whether Interval `[l, r]` is Fully Covered](#9-technique-7--check-whether-interval-l-r-is-fully-covered)
- [10. Technique 8 — Check Whether New Interval Overlaps Existing Intervals](#10-technique-8--check-whether-new-interval-overlaps-existing-intervals)
- [11. Technique 9 — Sweep Line for Maximum Overlap](#11-technique-9--sweep-line-for-maximum-overlap)
- [12. Technique 10 — Difference Array for Range Add Queries](#12-technique-10--difference-array-for-range-add-queries)
- [13. Technique 11 — Coordinate Compression for Huge Coordinates](#13-technique-11--coordinate-compression-for-huge-coordinates)
- [14. Technique 12 — Ordered Events for Start/End Query Processing](#14-technique-12--ordered-events-for-startend-query-processing)
- [15. Common STL Pair Tricks](#15-common-stl-pair-tricks)
- [16. Quick Decision Table](#16-quick-decision-table)

### Phase-Wise Practice Guide

- [How to Use This](#how-to-use-this)
- [Phase 1 — Basic Interval Recognition](#phase-1--basic-interval-recognition)
  - [1. Check Point Covered by Any Interval](#1-check-point-covered-by-any-interval)
  - [2. Merge Intervals](#2-merge-intervals)
  - [3. Insert Interval](#3-insert-interval)
- [Phase 2 — Offline Binary Search on Starts and Ends](#phase-2--offline-binary-search-on-starts-and-ends)
  - [4. Count Intervals Covering a Point](#4-count-intervals-covering-a-point)
  - [5. Number of Flowers in Full Bloom](#5-number-of-flowers-in-full-bloom)
  - [6. Minimum Interval to Include Each Query](#6-minimum-interval-to-include-each-query)
- [Phase 3 — Online `set<pair<int,int>>` Interval Mapping](#phase-3--online-setpairintint-interval-mapping)
  - [7. My Calendar I](#7-my-calendar-i)
  - [8. Check Whether Range Is Fully Covered](#8-check-whether-range-is-fully-covered)
  - [9. Range Module](#9-range-module)
- [Phase 4 — Delete / Split / Conflict Handling](#phase-4--delete--split--conflict-handling)
  - [10. Remove Covered Intervals](#10-remove-covered-intervals)
  - [11. Non-overlapping Intervals](#11-non-overlapping-intervals)
  - [12. Employee Free Time](#12-employee-free-time)
- [Phase 5 — Sweep Line / Difference Array / Compression](#phase-5--sweep-line--difference-array--compression)
  - [13. Meeting Rooms II](#13-meeting-rooms-ii)
  - [14. Car Pooling](#14-car-pooling)
  - [15. Corporate Flight Bookings](#15-corporate-flight-bookings)
- [Final Phase Summary](#final-phase-summary)
- [Master Rule](#master-rule)
- [Final Checklist Before Coding](#final-checklist-before-coding)

---

---

## 0. Problem Family

You are given a number line and intervals.

Common query types:

```text
+ l r     insert interval [l, r]
? x       check whether point x is covered
? x       count intervals covering point x
? l r     check whether full interval [l, r] is covered
- l r     delete coverage from [l, r]
```

Example:

```text
insert [2, 5]
insert [3, 10]
insert [13, 16]
query 4   -> covered
query 12  -> not covered
```

Number line:

```text
      [2---------5]
        [3----------------10]
                            [13-----16]
----1---2---3---4---5---6---10---12---13---16---->
                ^ covered                ^ not covered
                4                        12
```

---

## 1. Core Mental Model

For point `x`, an interval `[l, r]` covers `x` if:

```text
l <= x <= r
```

So an interval does **not** cover `x` if:

```text
r < x       ends before x
or
l > x       starts after x
```

Therefore:

```text
covered_count = total_intervals - intervals_ending_before_x - intervals_starting_after_x
```

This is the key idea behind the offline binary-search method.

---

## 2. STL Binary Search Helpers

`lower_bound` and `upper_bound` work only on sorted arrays.

```cpp
lower_bound(v.begin(), v.end(), x)
```

Returns iterator to first element `>= x`.

```cpp
upper_bound(v.begin(), v.end(), x)
```

Returns iterator to first element `> x`.

Example:

```text
v = [2, 3, 5, 5, 10]
x = 5

lower_bound(5) -> index 2  first >= 5
upper_bound(5) -> index 4  first > 5
```

Counting formulas:

```cpp
countLessThanX      = lower_bound(v.begin(), v.end(), x) - v.begin();
countLessEqualX     = upper_bound(v.begin(), v.end(), x) - v.begin();
countGreaterThanX   = v.end() - upper_bound(v.begin(), v.end(), x);
countGreaterEqualX  = v.end() - lower_bound(v.begin(), v.end(), x);
countEqualX         = upper_bound(v.begin(), v.end(), x) - lower_bound(v.begin(), v.end(), x);
```

---

## 3. Technique 1 — Brute Force Interval Scan

Use when constraints are small.

### Idea

Store all intervals in a vector. For each point query, scan every interval and check `l <= x <= r`.

### Dry Run

```text
ranges = [ [2,5], [3,10], [13,16] ]
x = 4

Check [2,5]   -> 2 <= 4 <= 5   yes
Answer = covered
```

For `x = 12`:

```text
[2,5]    no
[3,10]   no
[13,16]  no
Answer = not covered
```

### C++ Code

<details>
<summary>Brute force point coverage</summary>

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
    vector<pair<int,int>> ranges = {{2, 5}, {3, 10}, {13, 16}};

    cout << isCoveredBrute(ranges, 4) << "\n";   // 1
    cout << isCoveredBrute(ranges, 12) << "\n";  // 0
}
```

</details>

### Complexity

```text
Insert: O(1)
Query:  O(n)
```

---

## 4. Technique 2 — Offline Point Coverage Check

Use when all intervals are given first, then all point queries come later.

### Idea

Create two sorted arrays:

```text
L = all left endpoints
R = all right endpoints
```

For point `x`:

```text
startAfter = number of l > x
endBefore  = number of r < x
covered    = n - startAfter - endBefore
```

If `covered > 0`, point is covered by at least one interval.

### Dry Run

```text
ranges = [ [2,5], [3,10], [13,16] ]
L = [2, 3, 13]
R = [5, 10, 16]
```

Query `x = 4`:

```text
l > 4:  [13]       count = 1
r < 4:  []         count = 0
covered = 3 - 1 - 0 = 2
Answer = covered
```

Query `x = 12`:

```text
l > 12: [13]       count = 1
r < 12: [5,10]     count = 2
covered = 3 - 1 - 2 = 0
Answer = not covered
```

### C++ Code

<details>
<summary>Offline point coverage using lower_bound / upper_bound</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

struct OfflineCoverage {
    vector<int> L, R;
    int n = 0;

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
    vector<pair<int,int>> ranges = {{2, 5}, {3, 10}, {13, 16}};
    OfflineCoverage oc(ranges);

    cout << oc.isCovered(4) << "\n";   // 1
    cout << oc.isCovered(12) << "\n";  // 0
}
```

</details>

### Complexity

```text
Build: O(n log n)
Query: O(log n)
```

---

## 5. Technique 3 — Count Number of Intervals Covering a Point

This is almost the same as Technique 2, but instead of boolean, return the count.

### Formula

```text
covering(x) = number_of_l <= x - number_of_r < x
```

Why?

```text
started by x - already ended before x
```

### Dry Run

```text
ranges = [ [2,5], [3,10], [13,16] ]
L = [2,3,13]
R = [5,10,16]
```

For `x = 4`:

```text
l <= 4 = 2    intervals [2,5], [3,10] have started
r < 4  = 0    none ended
answer = 2
```

For `x = 5`:

```text
l <= 5 = 2
r < 5  = 0
answer = 2
```

For `x = 12`:

```text
l <= 12 = 2
r < 12  = 2
answer = 0
```

### C++ Code

<details>
<summary>Count intervals covering point</summary>

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
        int started = upper_bound(L.begin(), L.end(), x) - L.begin(); // l <= x
        int endedBefore = lower_bound(R.begin(), R.end(), x) - R.begin(); // r < x
        return started - endedBefore;
    }
};

int main() {
    vector<pair<int,int>> ranges = {{2, 5}, {3, 10}, {13, 16}};
    IntervalCounter counter(ranges);

    cout << counter.countCovering(4) << "\n";   // 2
    cout << counter.countCovering(12) << "\n";  // 0
}
```

</details>

---

## 6. Technique 4 — Intermixed Insert + Point Query using `set<pair<int,int>>`

Use when insert queries and point queries are mixed online.

Maintain disjoint merged intervals in a set:

```cpp
set<pair<int,int>> ranges;
```

Each pair is `{l, r}`.

### Key Idea for Point Query

To check if `x` is covered, find the interval with the largest `l <= x`.

```cpp
auto it = ranges.upper_bound({x, INT_MAX});
```

This gives first interval with `l > x`. So move one step back.

```cpp
--it;
```

Now check:

```cpp
it->first <= x && x <= it->second
```

### Why `{x, INT_MAX}`?

Pairs are compared lexicographically:

```text
first compare first value
if equal, compare second value
```

Using `{x, INT_MAX}` means:

```text
skip all intervals whose l == x
return first interval whose l > x
```

Then previous interval is the best candidate with `l <= x`.

### Dry Run

```text
ranges = { [2,10], [13,16] }
x = 4

upper_bound({4, INF}) -> [13,16]
previous -> [2,10]
4 <= 10 -> covered
```

For `x = 12`:

```text
upper_bound({12, INF}) -> [13,16]
previous -> [2,10]
12 <= 10 false -> not covered
```

### C++ Code

<details>
<summary>Online point coverage with set</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

struct RangeSet {
    set<pair<int,int>> ranges;

    bool isPointCovered(int x) {
        auto it = ranges.upper_bound({x, INT_MAX});

        if (it == ranges.begin()) return false;

        --it;
        return it->first <= x && x <= it->second;
    }

    void insertSimple(int l, int r) {
        ranges.insert({l, r});
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

</details>

---

## 7. Technique 5 — Insert and Merge Overlapping Intervals

Use when intervals can overlap and you want to maintain minimum disjoint intervals.

### Idea

Before inserting `[l, r]`, remove all intervals that overlap with it and merge them.

Two intervals overlap if:

```text
existing_l <= r && existing_r >= l
```

For merged coverage, you may also merge touching intervals:

```text
existing_l <= r + 1 && existing_r + 1 >= l
```

### Dry Run

```text
current = [ [2,5], [8,10], [13,16] ]
insert  = [4,9]

[2,5] overlaps [4,9] -> merge to [2,9]
[8,10] overlaps [2,9] -> merge to [2,10]
[13,16] no overlap

result = [ [2,10], [13,16] ]
```

### C++ Code

<details>
<summary>Insert and merge interval</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

struct RangeSet {
    set<pair<int,int>> ranges;

    void insertRange(int l, int r) {
        if (l > r) swap(l, r);

        // Find first interval that may overlap.
        auto it = ranges.lower_bound({l, INT_MIN});

        // Previous interval may also overlap with [l, r].
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

    void print() {
        for (auto [l, r] : ranges) {
            cout << "[" << l << "," << r << "] ";
        }
        cout << "\n";
    }
};

int main() {
    RangeSet rs;
    rs.insertRange(2, 5);
    rs.insertRange(8, 10);
    rs.insertRange(13, 16);
    rs.insertRange(4, 9);

    rs.print(); // [2,10] [13,16]
}
```

</details>

### Complexity

```text
O(k log n)
```

where `k` is number of intervals removed/merged.

---

## 8. Technique 6 — Delete / Remove a Range from Coverage

Use when you maintain covered ranges and need to remove `[l, r]`.

### Cases

Existing interval `[a, b]`, remove `[l, r]`.

```text
No overlap:
[a-----b]        [l-----r]

Remove middle:
[a-------------b]
      [l---r]
=> [a,l-1] and [r+1,b]

Remove left part:
[a-------------b]
[a----r]
=> [r+1,b]

Remove right part:
[a-------------b]
        [l----b]
=> [a,l-1]

Remove all:
[a-------------b]
[l-------------r]
=> deleted
```

### Dry Run

```text
current = [ [2,10], [13,16] ]
remove  = [4,8]

[2,10] becomes [2,3] and [9,10]
[13,16] unchanged

result = [ [2,3], [9,10], [13,16] ]
```

### C++ Code

<details>
<summary>Remove interval from covered ranges</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

struct RangeSet {
    set<pair<int,int>> ranges;

    void insertRange(int l, int r) {
        auto it = ranges.lower_bound({l, INT_MIN});
        if (it != ranges.begin()) {
            auto p = prev(it);
            if (p->second >= l - 1) it = p;
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

        for (auto p : addBack) ranges.insert(p);
    }

    void print() {
        for (auto [l, r] : ranges) cout << "[" << l << "," << r << "] ";
        cout << "\n";
    }
};

int main() {
    RangeSet rs;
    rs.insertRange(2, 10);
    rs.insertRange(13, 16);

    rs.removeRange(4, 8);
    rs.print(); // [2,3] [9,10] [13,16]
}
```

</details>

---

## 9. Technique 7 — Check Whether Interval `[l, r]` is Fully Covered

Use when intervals are merged and disjoint.

### Idea

Find interval with largest start `<= l`. Then check whether its end covers `r`.

```text
candidate.start <= l and candidate.end >= r
```

### Dry Run

```text
ranges = [ [2,10], [13,16] ]
query [4,8]

candidate with start <= 4 is [2,10]
10 >= 8 -> fully covered
```

Query `[4,12]`:

```text
candidate = [2,10]
10 >= 12 false
not fully covered
```

### C++ Code

<details>
<summary>Check full interval coverage</summary>

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

</details>

---

## 10. Technique 8 — Check Whether New Interval Overlaps Existing Intervals

Use in calendar booking, meeting room, interval conflict detection.

### Overlap Rule

Two closed intervals overlap if:

```text
max(l1, l2) <= min(r1, r2)
```

Equivalent:

```text
l1 <= r2 && l2 <= r1
```

For half-open intervals `[start, end)`, overlap if:

```text
start1 < end2 && start2 < end1
```

### Dry Run

```text
existing = [ [2,5], [8,10] ]
new = [6,7]

No overlap.
```

```text
existing = [ [2,5], [8,10] ]
new = [5,8]

Closed intervals: overlaps both at 5 and 8.
Half-open intervals: [5,8) does not overlap [2,5) or [8,10).
```

### C++ Code

<details>
<summary>Check overlap with set</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

struct CalendarClosedIntervals {
    set<pair<int,int>> ranges;

    bool hasOverlap(int l, int r) {
        auto it = ranges.lower_bound({l, INT_MIN});

        // Current interval may start before/equal r.
        if (it != ranges.end() && it->first <= r) return true;

        // Previous interval may end after/equal l.
        if (it != ranges.begin()) {
            --it;
            if (it->second >= l) return true;
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

</details>

---

## 11. Technique 9 — Sweep Line for Maximum Overlap

Use when you need max active intervals, number of meeting rooms, most crowded time, etc.

### Idea

Convert interval `[l, r]` into events:

```text
(l, +1) start
(r+1, -1) end after r   for closed integer intervals
```

Sort events and scan prefix sum.

### Dry Run

```text
intervals = [ [2,5], [3,10], [13,16] ]

events:
2  +1
6  -1
3  +1
11 -1
13 +1
17 -1
```

Sorted:

```text
2  +1  active = 1
3  +1  active = 2   max = 2
6  -1  active = 1
11 -1  active = 0
13 +1  active = 1
17 -1  active = 0
```

Answer:

```text
maximum overlap = 2
```

### C++ Code

<details>
<summary>Sweep line maximum overlap</summary>

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

    int active = 0, ans = 0;
    for (auto [pos, delta] : events) {
        active += delta;
        ans = max(ans, active);
    }

    return ans;
}

int main() {
    vector<pair<int,int>> intervals = {{2, 5}, {3, 10}, {13, 16}};
    cout << maxOverlapClosedIntervals(intervals) << "\n"; // 2
}
```

</details>

---

## 12. Technique 10 — Difference Array for Range Add Queries

Use when coordinates are small or compressed.

### Problem

Apply many range updates:

```text
add +v to all positions in [l, r]
```

### Idea

For closed interval `[l, r]`:

```cpp
diff[l] += v;
diff[r + 1] -= v;
```

Prefix sum gives final values.

### Dry Run

```text
n = 8
updates:
[2,5] +1
[3,7] +1
```

Diff:

```text
diff[2] += 1
diff[6] -= 1
diff[3] += 1
diff[8] -= 1
```

Prefix result:

```text
idx:    1 2 3 4 5 6 7 8
value:  0 1 2 2 2 1 1 0
```

### C++ Code

<details>
<summary>Difference array range add</summary>

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

    vector<int> a(n + 1);
    for (int i = 1; i <= n; i++) {
        a[i] = a[i - 1] + diff[i];
    }

    for (int i = 1; i <= n; i++) cout << a[i] << " ";
    cout << "\n";
}
```

</details>

---

## 13. Technique 11 — Coordinate Compression for Huge Coordinates

Use when coordinates are huge, but number of unique coordinates is small.

### Idea

Collect all useful coordinates, sort, unique, then map each original coordinate to compressed index.

For interval problems, often collect:

```text
l, r, r+1, query points
```

### Dry Run

```text
coordinates = [1000000000, 5, 10, 5]
unique sorted = [5, 10, 1000000000]

5          -> 0
10         -> 1
1000000000 -> 2
```

### C++ Code

<details>
<summary>Coordinate compression helper</summary>

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

</details>

---

## 14. Technique 12 — Ordered Events for Start/End Query Processing

Use when you have offline queries and want answers while sweeping.

### Example Problem

Given intervals and query points, count active intervals at each query point.

### Event Ordering for Closed Intervals

At same coordinate:

```text
start should happen before query
query should happen before end
```

So `[l, r]` covers both `l` and `r`.

Use event type ordering:

```text
0 = start
1 = query
2 = end
```

### Dry Run

```text
intervals = [ [2,5], [3,10] ]
queries = [2,5,6]
```

Events:

```text
2 start
2 query id0
3 start
5 query id1
5 end
6 query id2
10 end
```

Scan:

```text
x=2 start active=1
x=2 query ans[0]=1
x=3 start active=2
x=5 query ans[1]=2
x=5 end active=1
x=6 query ans[2]=1
```

### C++ Code

<details>
<summary>Offline event sweep for query points</summary>

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

int main() {
    vector<pair<int,int>> intervals = {{2,5}, {3,10}};
    vector<int> queries = {2,5,6};

    auto ans = countCoveringBySweep(intervals, queries);
    for (int x : ans) cout << x << " ";
    cout << "\n"; // 1 2 1
}
```

</details>

---

## 15. Common STL Pair Tricks

### `upper_bound({x, INT_MAX})`

Use to find first interval whose start is greater than `x`.

```cpp
auto it = s.upper_bound({x, INT_MAX});
```

Then `prev(it)` gives interval with largest `l <= x`.

### `lower_bound({l, INT_MIN})`

Use to find first interval whose start is at least `l`.

```cpp
auto it = s.lower_bound({l, INT_MIN});
```

### Why `INT_MIN` and `INT_MAX`?

Because pair comparison checks first value first, then second value.

```text
{1, 2} < {1, 5}  true
{2, 0} < {3, -100} true
```

So:

```text
{x, INT_MAX} is after every pair starting with x
{x, INT_MIN} is before every pair starting with x
```

---

## 16. Quick Decision Table

| Problem Type | Best Technique |
|---|---|
| Small constraints | Brute force scan |
| All intervals first, point queries later | Offline binary search on L/R arrays |
| Need count covering point | `started - endedBefore` |
| Insert and query online | `set<pair<int,int>>` |
| Maintain merged coverage | Insert + merge intervals |
| Remove covered range | Split intervals after deletion |
| Check full `[l,r]` coverage | Find previous interval by start |
| Check conflict / overlap | Check current and previous interval in set |
| Maximum overlap | Sweep line |
| Many range add updates | Difference array |
| Huge coordinates | Coordinate compression |
| Offline intervals + queries | Ordered event sweep |

---

## Final Recall Template

```text
Point x covered by [l,r] if l <= x <= r.
Not covered if r < x or l > x.

Offline:
covered = n - count(r < x) - count(l > x)

Counting:
covering = count(l <= x) - count(r < x)

Online set:
Find interval with largest l <= x using upper_bound({x, INF}) then --it.

Merged intervals:
Always keep set disjoint and sorted.
Insert = merge overlaps.
Delete = split existing intervals.
```

---

# Range Mapping Phase-Wise Practice Guide


---

## How to Use This

```text
Phase 1 -> learn interval meaning, overlap, merge
Phase 2 -> learn offline sorted L/R binary search
Phase 3 -> learn online set<pair<int,int>> interval lookup
Phase 4 -> learn delete, split, conflict, greedy removal
Phase 5 -> learn sweep line, difference array, coordinate compression
```

Core mental model:

```text
Interval [l, r] covers x if l <= x <= r.
Interval does not cover x if r < x or l > x.
```

---

# Phase 1 — Basic Interval Recognition

Goal:

```text
Understand coverage, overlap, and merging.
```

Core question:

```text
Does this interval touch, cover, or conflict with another interval?
```

---

## 1. Check Point Covered by Any Interval

Problem link: Custom basic pattern

### Problem Statement

Given intervals `ranges` and a point `x`, return whether any interval covers `x`.

```text
ranges = [[2,5], [8,10], [13,16]]
x = 4
answer = true
```

### Pattern

```text
Technique: brute force interval scan
Check    : l <= x <= r
Use when : constraints are small
```

### Thinking

```text
A point is covered if it lies inside at least one interval.
```

### Dry Run

```text
ranges = [[2,5], [8,10], [13,16]]
x = 4

Check [2,5]    -> 2 <= 4 <= 5 -> yes
Stop.
answer = true
```

For `x = 12`:

```text
[2,5]    -> no
[8,10]   -> no
[13,16]  -> no
answer = false
```

### Code

<details>
<summary>C++ code</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isCovered(vector<pair<int,int>>& ranges, int x) {
    for (auto [l, r] : ranges) {
        if (l <= x && x <= r) return true;
    }
    return false;
}
```

</details>

---

## 2. Merge Intervals

Problem link: [LeetCode 56 — Merge Intervals](https://leetcode.com/problems/merge-intervals/)

### Problem Statement

Given intervals, merge all overlapping intervals.

```text
intervals = [[1,3], [2,6], [8,10], [15,18]]
answer    = [[1,6], [8,10], [15,18]]
```

### Pattern

```text
Technique: sort by start, then extend current range
Overlap  : next.start <= current.end
Action   : current.end = max(current.end, next.end)
```

### Thinking

```text
After sorting, only the latest merged interval can overlap with the next interval.
```

### Dry Run

```text
Sorted intervals:
[1,3], [2,6], [8,10], [15,18]

current = [1,3]
next [2,6]
2 <= 3 -> overlap -> merge [1,6]

next [8,10]
8 <= 6? no -> push [1,6], start [8,10]

next [15,18]
15 <= 10? no -> push [8,10], start [15,18]

answer = [[1,6], [8,10], [15,18]]
```

### Code

<details>
<summary>C++ code</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> merge(vector<vector<int>>& intervals) {
    sort(intervals.begin(), intervals.end());

    vector<vector<int>> ans;

    for (auto interval : intervals) {
        if (ans.empty() || interval[0] > ans.back()[1]) {
            ans.push_back(interval);
        } else {
            ans.back()[1] = max(ans.back()[1], interval[1]);
        }
    }

    return ans;
}
```

</details>

---

## 3. Insert Interval

Problem link: [LeetCode 57 — Insert Interval](https://leetcode.com/problems/insert-interval/)

### Problem Statement

Given sorted non-overlapping intervals, insert a new interval and merge if needed.

```text
intervals = [[1,3], [6,9]]
newInterval = [2,5]
answer = [[1,5], [6,9]]
```

### Pattern

```text
Technique: three parts
1. intervals ending before new interval
2. overlapping intervals to merge
3. intervals starting after new interval
```

### Thinking

```text
Separate intervals into before, overlap, and after.
Only overlap group changes the new interval.
```

### Dry Run

```text
intervals = [[1,3], [6,9]]
insert = [2,5]

[1,3] overlaps [2,5]
merge -> [1,5]

[6,9] starts after 5
push merged [1,5]
push [6,9]

answer = [[1,5], [6,9]]
```

### Code

<details>
<summary>C++ code</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> insert(vector<vector<int>>& intervals, vector<int>& newInterval) {
    vector<vector<int>> ans;
    int i = 0, n = intervals.size();

    // before new interval
    while (i < n && intervals[i][1] < newInterval[0]) {
        ans.push_back(intervals[i]);
        i++;
    }

    // overlap and merge
    while (i < n && intervals[i][0] <= newInterval[1]) {
        newInterval[0] = min(newInterval[0], intervals[i][0]);
        newInterval[1] = max(newInterval[1], intervals[i][1]);
        i++;
    }
    ans.push_back(newInterval);

    // after new interval
    while (i < n) {
        ans.push_back(intervals[i]);
        i++;
    }

    return ans;
}
```

</details>

---

# Phase 2 — Offline Binary Search on Starts and Ends

Goal:

```text
Answer point queries fast after all intervals are known.
```

Core question:

```text
How many intervals started before x and how many ended before x?
```

---

## 4. Count Intervals Covering a Point

Problem link: Custom basic pattern

### Problem Statement

Given intervals and query points, return how many intervals cover each point.

```text
ranges = [[2,5], [3,10], [13,16]]
queries = [4, 12]
answer = [2, 0]
```

### Pattern

```text
Technique: sorted starts and ends
count(x) = count(l <= x) - count(r < x)
```

### Thinking

```text
Intervals covering x = intervals already started - intervals already ended before x.
```

### Dry Run

```text
L = [2,3,13]
R = [5,10,16]

x = 4
started    = count(l <= 4) = 2
endedBefore = count(r < 4) = 0
answer = 2

x = 12
started    = count(l <= 12) = 2
endedBefore = count(r < 12) = 2
answer = 0
```

### Code

<details>
<summary>C++ code</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> countCovering(vector<pair<int,int>>& ranges, vector<int>& queries) {
    vector<int> L, R;

    for (auto [l, r] : ranges) {
        L.push_back(l);
        R.push_back(r);
    }

    sort(L.begin(), L.end());
    sort(R.begin(), R.end());

    vector<int> ans;
    for (int x : queries) {
        int started = upper_bound(L.begin(), L.end(), x) - L.begin();
        int endedBefore = lower_bound(R.begin(), R.end(), x) - R.begin();
        ans.push_back(started - endedBefore);
    }

    return ans;
}
```

</details>

---

## 5. Number of Flowers in Full Bloom

Problem link: [LeetCode 2251 — Number of Flowers in Full Bloom](https://leetcode.com/problems/number-of-flowers-in-full-bloom/)

### Problem Statement

Each flower blooms from `start` to `end`. For each person arrival time, count flowers in bloom.

```text
flowers = [[1,6], [3,7], [9,12], [4,13]]
people  = [2,3,7,11]
answer  = [1,2,2,2]
```

### Pattern

```text
Same as count intervals covering point.
started = starts <= time
ended   = ends < time
blooming = started - ended
```

### Thinking

```text
A flower is active if it already started and has not ended before the person's time.
```

### Dry Run

```text
starts = [1,3,4,9]
ends   = [6,7,12,13]

person = 7
started = count(start <= 7) = 3
endedBefore = count(end < 7) = 1   // [1,6]
answer = 2
```

### Code

<details>
<summary>C++ code</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> fullBloomFlowers(vector<vector<int>>& flowers, vector<int>& people) {
    vector<int> starts, ends;

    for (auto& f : flowers) {
        starts.push_back(f[0]);
        ends.push_back(f[1]);
    }

    sort(starts.begin(), starts.end());
    sort(ends.begin(), ends.end());

    vector<int> ans;
    for (int t : people) {
        int started = upper_bound(starts.begin(), starts.end(), t) - starts.begin();
        int endedBefore = lower_bound(ends.begin(), ends.end(), t) - ends.begin();
        ans.push_back(started - endedBefore);
    }

    return ans;
}
```

</details>

---

## 6. Minimum Interval to Include Each Query

Problem link: [LeetCode 1851 — Minimum Interval to Include Each Query](https://leetcode.com/problems/minimum-interval-to-include-each-query/)

### Problem Statement

For each query `q`, find the size of the smallest interval `[l, r]` such that `l <= q <= r`.

```text
intervals = [[1,4], [2,4], [3,6], [4,4]]
queries   = [2,3,4,5]
answer    = [3,3,1,4]
```

### Pattern

```text
Sort intervals by l.
Sort queries by value.
Use min-heap by interval length.
Remove intervals whose r < query.
```

### Thinking

```text
For current query q:
1. Add all intervals with l <= q.
2. Remove intervals with r < q.
3. Heap top is smallest active interval covering q.
```

### Dry Run

```text
intervals sorted by l:
[1,4], [2,4], [3,6], [4,4]

query = 4
add intervals with l <= 4:
[1,4] len 4
[2,4] len 3
[3,6] len 4
[4,4] len 1

remove intervals with r < 4: none
heap top = len 1 -> answer = 1
```

### Code

<details>
<summary>C++ code</summary>

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
            int l = intervals[i][0], r = intervals[i][1];
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

</details>

---

# Phase 3 — Online `set<pair<int,int>>` Interval Mapping

Goal:

```text
Handle mixed insert, query, and range operations online.
```

Core question:

```text
Which interval has the largest start <= x or <= l?
```

---

## 7. My Calendar I

Problem link: [LeetCode 729 — My Calendar I](https://leetcode.com/problems/my-calendar-i/)

### Problem Statement

Book event `[start, end)` only if it does not overlap any existing event.

```text
book(10,20) -> true
book(15,25) -> false
book(20,30) -> true
```

### Pattern

```text
Use set<pair<int,int>> sorted by start.
Check next interval and previous interval.
Half-open overlap: a.start < b.end && b.start < a.end
```

### Thinking

```text
Only two intervals can conflict with a new interval in sorted order:
1. first interval with start >= new start
2. interval immediately before it
```

### Dry Run

```text
existing = [10,20)
new = [15,25)

lower_bound({15,25}) -> end
previous = [10,20)
previous.end > new.start -> 20 > 15 -> overlap
answer = false
```

For `[20,30)`:

```text
previous = [10,20)
previous.end > new.start -> 20 > 20 false
answer = true
```

### Code

<details>
<summary>C++ code</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

class MyCalendar {
    set<pair<int,int>> events;

public:
    bool book(int start, int end) {
        auto it = events.lower_bound({start, end});

        if (it != events.end() && it->first < end) return false;

        if (it != events.begin()) {
            auto prevIt = prev(it);
            if (prevIt->second > start) return false;
        }

        events.insert({start, end});
        return true;
    }
};
```

</details>

---

## 8. Check Whether Range Is Fully Covered

Problem link: Custom range module sub-pattern

### Problem Statement

Given merged disjoint intervals, check whether every point in `[l, r]` is covered.

```text
ranges = [[2,10], [13,16]]
query = [4,8]
answer = true
```

### Pattern

```text
Find interval with largest start <= l.
If its end >= r, the full query range is covered.
```

### Thinking

```text
Because intervals are merged and disjoint, one interval must cover the entire [l,r].
Multiple intervals cannot help unless they were already merged.
```

### Dry Run

```text
ranges = [[2,10], [13,16]]
query = [4,8]

upper_bound({4, INF}) -> [13,16]
previous -> [2,10]
10 >= 8 -> true
```

Query `[4,12]`:

```text
previous -> [2,10]
10 >= 12? no
answer = false
```

### Code

<details>
<summary>C++ code</summary>

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

</details>

---

## 9. Range Module

Problem link: [LeetCode 715 — Range Module](https://leetcode.com/problems/range-module/)

### Problem Statement

Design a structure supporting:

```text
addRange(left, right)
queryRange(left, right)
removeRange(left, right)
```

Intervals are half-open: `[left, right)`.

### Pattern

```text
Maintain disjoint merged intervals in set<pair<int,int>>.
add    -> merge overlaps
query  -> find previous interval
remove -> split/delete overlapping intervals
```

### Thinking

```text
Always keep the set normalized:
1. sorted
2. disjoint
3. merged

Then query becomes simple.
```

### Dry Run

```text
addRange(10,20)
ranges = [10,20)

removeRange(14,16)
[10,20) splits into:
[10,14) and [16,20)

queryRange(10,14) -> true
queryRange(13,15) -> false
```

### Code

<details>
<summary>C++ code</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

class RangeModule {
    set<pair<int,int>> ranges;

public:
    void addRange(int left, int right) {
        auto it = ranges.lower_bound({left, INT_MIN});

        if (it != ranges.begin()) {
            auto p = prev(it);
            if (p->second >= left) it = p;
        }

        while (it != ranges.end() && it->first <= right) {
            left = min(left, it->first);
            right = max(right, it->second);
            it = ranges.erase(it);
        }

        ranges.insert({left, right});
    }

    bool queryRange(int left, int right) {
        auto it = ranges.upper_bound({left, INT_MAX});
        if (it == ranges.begin()) return false;

        --it;
        return it->first <= left && right <= it->second;
    }

    void removeRange(int left, int right) {
        auto it = ranges.lower_bound({left, INT_MIN});
        if (it != ranges.begin()) --it;

        vector<pair<int,int>> addBack;

        while (it != ranges.end()) {
            auto [l, r] = *it;

            if (r <= left) {
                ++it;
                continue;
            }
            if (l >= right) break;

            it = ranges.erase(it);

            if (l < left) addBack.push_back({l, left});
            if (right < r) addBack.push_back({right, r});
        }

        for (auto p : addBack) ranges.insert(p);
    }
};
```

</details>

---

# Phase 4 — Delete / Split / Conflict Handling

Goal:

```text
Understand when intervals become useless, must be removed, or must be split.
```

Core question:

```text
Which interval should remain after conflict or deletion?
```

---

## 10. Remove Covered Intervals

Problem link: [LeetCode 1288 — Remove Covered Intervals](https://leetcode.com/problems/remove-covered-intervals/)

### Problem Statement

Remove intervals covered by another interval. Return remaining count.

```text
intervals = [[1,4], [3,6], [2,8]]
answer = 2
```

Because `[3,6]` is covered by `[2,8]`.

### Pattern

```text
Sort by start ascending.
For same start, sort end descending.
Track farthest end seen.
If current.end <= farthestEnd, current is covered.
```

### Thinking

```text
For an interval to cover another, it must start earlier/equal and end later/equal.
Sorting makes earlier start guaranteed.
```

### Dry Run

```text
intervals sorted:
[1,4], [2,8], [3,6]

maxEnd = 0
[1,4] -> 4 > 0 -> keep, maxEnd = 4
[2,8] -> 8 > 4 -> keep, maxEnd = 8
[3,6] -> 6 <= 8 -> covered, remove

answer = 2
```

### Code

<details>
<summary>C++ code</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

int removeCoveredIntervals(vector<vector<int>>& intervals) {
    sort(intervals.begin(), intervals.end(), [](auto& a, auto& b) {
        if (a[0] != b[0]) return a[0] < b[0];
        return a[1] > b[1];
    });

    int ans = 0;
    int maxEnd = -1;

    for (auto& in : intervals) {
        if (in[1] > maxEnd) {
            ans++;
            maxEnd = in[1];
        }
    }

    return ans;
}
```

</details>

---

## 11. Non-overlapping Intervals

Problem link: [LeetCode 435 — Non-overlapping Intervals](https://leetcode.com/problems/non-overlapping-intervals/)

### Problem Statement

Find minimum number of intervals to remove so the rest do not overlap.

```text
intervals = [[1,2], [2,3], [3,4], [1,3]]
answer = 1
```

### Pattern

```text
Greedy by earliest end.
Keep interval with smaller end to leave maximum room for future intervals.
```

### Thinking

```text
When two intervals overlap, removing the one with larger end is better.
```

### Dry Run

```text
sort by end:
[1,2], [2,3], [1,3], [3,4]

keep [1,2], end = 2
[2,3] start >= 2 -> keep, end = 3
[1,3] start < 3 -> overlap -> remove
[3,4] start >= 3 -> keep

removed = 1
```

### Code

<details>
<summary>C++ code</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

int eraseOverlapIntervals(vector<vector<int>>& intervals) {
    sort(intervals.begin(), intervals.end(), [](auto& a, auto& b) {
        return a[1] < b[1];
    });

    int removed = 0;
    int end = intervals[0][1];

    for (int i = 1; i < (int)intervals.size(); i++) {
        if (intervals[i][0] < end) {
            removed++;
        } else {
            end = intervals[i][1];
        }
    }

    return removed;
}
```

</details>

---

## 12. Employee Free Time

Problem link: [LeetCode 759 — Employee Free Time](https://leetcode.com/problems/employee-free-time/)

### Problem Statement

Given employees' working intervals, return common free time intervals.

```text
schedule = [[[1,2],[5,6]], [[1,3]], [[4,10]]]
answer = [[3,4]]
```

### Pattern

```text
Flatten all busy intervals.
Merge busy intervals.
Gaps between merged busy intervals are free time.
```

### Thinking

```text
Common free time exists only when nobody is busy.
So first merge all busy intervals globally.
```

### Dry Run

```text
busy intervals:
[1,2], [5,6], [1,3], [4,10]

sort:
[1,2], [1,3], [4,10], [5,6]

merge busy:
[1,3], [4,10]

gap between [1,3] and [4,10] = [3,4]
```

### Code

<details>
<summary>C++ code</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> employeeFreeTime(vector<vector<vector<int>>>& schedule) {
    vector<vector<int>> busy;

    for (auto& emp : schedule) {
        for (auto& interval : emp) {
            busy.push_back(interval);
        }
    }

    sort(busy.begin(), busy.end());

    vector<vector<int>> merged;
    for (auto in : busy) {
        if (merged.empty() || in[0] > merged.back()[1]) {
            merged.push_back(in);
        } else {
            merged.back()[1] = max(merged.back()[1], in[1]);
        }
    }

    vector<vector<int>> freeTime;
    for (int i = 1; i < (int)merged.size(); i++) {
        freeTime.push_back({merged[i - 1][1], merged[i][0]});
    }

    return freeTime;
}
```

</details>

---

# Phase 5 — Sweep Line / Difference Array / Compression

Goal:

```text
Convert intervals into events or range updates.
```

Core question:

```text
At this coordinate, how many intervals become active or inactive?
```

---

## 13. Meeting Rooms II

Problem link: [LeetCode 253 — Meeting Rooms II](https://leetcode.com/problems/meeting-rooms-ii/)

### Problem Statement

Given meeting intervals, return minimum number of rooms required.

```text
intervals = [[0,30], [5,10], [15,20]]
answer = 2
```

### Pattern

```text
Sweep line.
start event -> +1 room
end event   -> -1 room
maximum active meetings = rooms needed
```

### Thinking

```text
Rooms needed equals maximum number of overlapping meetings at any time.
```

### Dry Run

```text
events:
0 +1
30 -1
5 +1
10 -1
15 +1
20 -1

sorted:
0  +1 -> active = 1, max = 1
5  +1 -> active = 2, max = 2
10 -1 -> active = 1
15 +1 -> active = 2
20 -1 -> active = 1
30 -1 -> active = 0

answer = 2
```

### Code

<details>
<summary>C++ code</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

int minMeetingRooms(vector<vector<int>>& intervals) {
    vector<pair<int,int>> events;

    for (auto& in : intervals) {
        events.push_back({in[0], +1});
        events.push_back({in[1], -1});
    }

    sort(events.begin(), events.end(), [](auto& a, auto& b) {
        if (a.first != b.first) return a.first < b.first;
        return a.second < b.second; // end before start for half-open intervals
    });

    int active = 0, ans = 0;
    for (auto [time, delta] : events) {
        active += delta;
        ans = max(ans, active);
    }

    return ans;
}
```

</details>

---

## 14. Car Pooling

Problem link: [LeetCode 1094 — Car Pooling](https://leetcode.com/problems/car-pooling/)

### Problem Statement

Each trip is `[passengers, from, to]`. Return whether capacity is never exceeded.

```text
trips = [[2,1,5], [3,3,7]]
capacity = 4
answer = false
```

### Pattern

```text
Difference array / sweep line.
from -> +passengers
to   -> -passengers
```

### Thinking

```text
At each location, track current passengers inside the car.
```

### Dry Run

```text
trip [2,1,5]: +2 at 1, -2 at 5
trip [3,3,7]: +3 at 3, -3 at 7

location 1: active = 2
location 3: active = 5 -> exceeds capacity 4
answer = false
```

### Code

<details>
<summary>C++ code</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

bool carPooling(vector<vector<int>>& trips, int capacity) {
    map<int,int> events;

    for (auto& t : trips) {
        int passengers = t[0];
        int from = t[1];
        int to = t[2];

        events[from] += passengers;
        events[to] -= passengers;
    }

    int active = 0;
    for (auto [pos, delta] : events) {
        active += delta;
        if (active > capacity) return false;
    }

    return true;
}
```

</details>

---

## 15. Corporate Flight Bookings

Problem link: [LeetCode 1109 — Corporate Flight Bookings](https://leetcode.com/problems/corporate-flight-bookings/)

### Problem Statement

Each booking `[first, last, seats]` adds seats to every flight from `first` to `last`.

```text
bookings = [[1,2,10], [2,3,20], [2,5,25]]
n = 5
answer = [10,55,45,25,25]
```

### Pattern

```text
Difference array for range add.
For [l, r] add val:
diff[l] += val
diff[r + 1] -= val
```

### Thinking

```text
Instead of updating every flight inside the range, mark only where the addition starts and ends.
Prefix sum reconstructs final values.
```

### Dry Run

```text
n = 5
[1,2] +10 -> diff[1]+=10, diff[3]-=10
[2,3] +20 -> diff[2]+=20, diff[4]-=20
[2,5] +25 -> diff[2]+=25, diff[6]-=25

prefix:
flight 1 = 10
flight 2 = 55
flight 3 = 45
flight 4 = 25
flight 5 = 25
```

### Code

<details>
<summary>C++ code</summary>

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> corpFlightBookings(vector<vector<int>>& bookings, int n) {
    vector<int> diff(n + 2, 0);

    for (auto& b : bookings) {
        int l = b[0], r = b[1], seats = b[2];
        diff[l] += seats;
        diff[r + 1] -= seats;
    }

    vector<int> ans(n);
    int running = 0;

    for (int i = 1; i <= n; i++) {
        running += diff[i];
        ans[i - 1] = running;
    }

    return ans;
}
```

</details>

---

# Final Phase Summary

| Phase | Goal | Problems |
|---|---|---|
| Phase 1 | Basic interval recognition | Point Covered, Merge Intervals, Insert Interval |
| Phase 2 | Offline binary search | Count Covering Point, Full Bloom Flowers, Minimum Interval Query |
| Phase 3 | Online interval set | My Calendar I, Full Range Covered, Range Module |
| Phase 4 | Conflict/delete/split | Remove Covered, Non-overlapping Intervals, Employee Free Time |
| Phase 5 | Sweep/difference/compression | Meeting Rooms II, Car Pooling, Corporate Flight Bookings |

---

# Master Rule

```text
If the problem asks about coverage, overlap, active intervals, range add, or conflict,
range mapping is probably the pattern.
```

# Final Checklist Before Coding

```text
1. Are intervals closed [l,r] or half-open [l,r)?
2. Need point coverage or range coverage?
3. Are queries offline or online?
4. Need count, boolean, or minimum interval?
5. Can intervals overlap, or should they be merged?
6. Need insert, delete, or split?
7. Should I use sorted L/R, set<pair<int,int>>, sweep line, or difference array?
8. At same coordinate, should start happen before query/end?
```

---