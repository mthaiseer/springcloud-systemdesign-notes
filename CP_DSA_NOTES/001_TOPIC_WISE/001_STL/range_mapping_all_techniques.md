# Range Mapping / Interval Coverage — Complete Technique Playbook

A CP/DSA guide for interval/range problems using STL, binary search, sweep line, and `set<pair<int,int>>`.

## Clickable Index

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
