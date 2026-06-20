# 018_RangeMapping_Interval_Engine.md

> MiniSTLEngine Phase 018  
> Topic: Intervals and Range Mapping as a **Timeline / Booking / Range Query Engine** for CP, DSA, FAANG interviews, and real-system thinking.

---

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Interval Problems Are An Engine](#2-why-interval-problems-are-an-engine)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Core Interval Behavior](#4-core-interval-behavior)
- [5. Interval Problem Categories](#5-interval-problem-categories)
- [6. CP/DSA Recognition](#6-cpdsa-recognition)
- [7. Engine Architecture](#7-engine-architecture)
- [8. Basic Interval Merge Engine](#8-basic-interval-merge-engine)
- [9. Dry Run: Merge Intervals](#9-dry-run-merge-intervals)
- [10. CP Pattern 1: Merge Intervals](#10-cp-pattern-1-merge-intervals)
- [11. CP Pattern 2: Insert Interval](#11-cp-pattern-2-insert-interval)
- [12. CP Pattern 3: Non-Overlapping Intervals](#12-cp-pattern-3-non-overlapping-intervals)
- [13. CP Pattern 4: Meeting Rooms](#13-cp-pattern-4-meeting-rooms)
- [14. CP Pattern 5: Interval Intersection](#14-cp-pattern-5-interval-intersection)
- [15. CP Pattern 6: Difference Array Range Update](#15-cp-pattern-6-difference-array-range-update)
- [16. CP Pattern 7: Coordinate Compression](#16-cp-pattern-7-coordinate-compression)
- [17. Intervals vs Sweep Line](#17-intervals-vs-sweep-line)
- [18. Common Mistakes](#18-common-mistakes)
- [19. Complexity Table](#19-complexity-table)
- [20. Real-World Mapping](#20-real-world-mapping)
- [21. Final Mental Model](#21-final-mental-model)
- [22. Next Step](#22-next-step)

---

# 1. Goal

Learn interval/range problems not only as:

```text
[start, end]
```

but as a:

```text
Timeline / Booking / Range Query Engine
```

It helps solve:

```text
merge intervals
calendar booking
meeting rooms
range updates
timeline overlap
coordinate compression
resource allocation
time window analytics
```

---

# 2. Why Interval Problems Are An Engine

Intervals represent:

```text
active ranges in time or space
```

Normal thinking:

```text
just merge overlapping ranges
```

Engine thinking:

```text
RangeMappingIntervalEngine
    models active ranges
    merges overlapping activity
    tracks conflicts
    compresses coordinates
    performs bulk range updates
```

Core question:

```text
Do objects represent continuous ranges?
```

If yes:

```text
interval/range techniques may apply
```

---

# 3. Real-System Mental Model

Real systems constantly use intervals:

```text
calendar booking
CPU scheduling
hotel reservation
seat booking
network bandwidth windows
time-series ranges
video streaming segments
memory allocation
```

Architecture:

```text
Incoming Ranges
      |
      v
RangeMappingIntervalEngine
      |
      +--> merge overlaps
      +--> detect conflicts
      +--> map ranges
      +--> apply updates
      |
      v
Optimized Timeline / Allocation
```

---

# 4. Core Interval Behavior

Example:

```text
[1,3]
[2,6]
[8,10]
```

Visual:

```text
1----3
  2-------6

merge -> 1-----------6

8------10
```

Key observation:

```text
sort by start time
```

After sorting:

```text
overlap decisions become local
```

---

# 5. Interval Problem Categories

| Category | Example |
|---|---|
| merge overlaps | merge intervals |
| conflict detection | meeting rooms |
| range insertion | insert interval |
| interval intersection | common busy time |
| range updates | difference array |
| coordinate mapping | compression |
| active interval tracking | sweep line |

---

# 6. CP/DSA Recognition

Use interval/range techniques when problem says:

```text
time range
booking
meeting
overlap
merge
active intervals
segments
range update
continuous block
```

Hidden mapping:

| Problem clue | Pattern |
|---|---|
| merge ranges | sort + merge |
| detect overlap | compare adjacent intervals |
| minimum meeting rooms | active interval count |
| bulk range updates | difference array |
| huge coordinates | coordinate compression |
| common ranges | interval intersection |

---

# 7. Engine Architecture

```text
MiniRangeMappingIntervalEngine
├── merge engine
├── overlap detector
├── booking engine
├── intersection engine
├── range update engine
├── coordinate compression engine
└── timeline allocator
```

---

# 8. Basic Interval Merge Engine

## Step-by-Step Approach Before Code

```text
Step 1: Sort intervals by start time.

Step 2: Create empty merged result.

Step 3: Traverse intervals one by one.

Step 4: If no overlap with previous merged interval:
        add new interval.

Step 5: Else:
        extend previous merged interval end.

Step 6: Final merged list contains compressed ranges.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class IntervalMergeEngine {
public:

    vector<pair<int,int>> mergeIntervals(
        vector<pair<int,int>>& intervals
    ) {

        // Step 1:
        // Sort by start time.
        sort(intervals.begin(), intervals.end());

        vector<pair<int,int>> merged;

        for (auto [start, end] : intervals) {

            // Step 2:
            // If merged empty OR no overlap,
            // create new merged interval.
            if (merged.empty() ||
                start > merged.back().second) {

                merged.push_back({start, end});

            } else {

                // Step 3:
                // Overlap exists.
                // Extend current merged interval.
                merged.back().second =
                    max(merged.back().second, end);
            }
        }

        return merged;
    }
};

int main() {

    vector<pair<int,int>> intervals = {
        {1,3},
        {2,6},
        {8,10},
        {9,12}
    };

    IntervalMergeEngine engine;

    auto merged =
        engine.mergeIntervals(intervals);

    for (auto [l, r] : merged) {

        cout << "[" << l << "," << r << "] ";
    }

    return 0;
}
```

---

# 9. Dry Run: Merge Intervals

Input:

```text
[1,3]
[2,6]
[8,10]
[9,12]
```

After sorting:

```text
same order
```

Step:

```text
merged = []

take [1,3]
merged = [1,3]

take [2,6]
overlap with [1,3]
merge -> [1,6]

take [8,10]
no overlap
merged = [1,6], [8,10]

take [9,12]
overlap with [8,10]
merge -> [8,12]
```

Answer:

```text
[1,6]
[8,12]
```

---

# 10. CP Pattern 1: Merge Intervals

## Problem Type

```text
Merge all overlapping intervals.
```

## Step-by-Step Approach Before Code

```text
Step 1: Sort by start time.

Step 2: Compare current interval with last merged interval.

Step 3: If overlap:
        extend end.

Step 4: Else:
        start new merged interval.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<pair<int,int>> mergeIntervals(
    vector<pair<int,int>>& intervals
) {
    sort(intervals.begin(), intervals.end());

    vector<pair<int,int>> merged;

    for (auto [l, r] : intervals) {

        if (merged.empty() ||
            l > merged.back().second) {

            merged.push_back({l, r});

        } else {

            merged.back().second =
                max(merged.back().second, r);
        }
    }

    return merged;
}

int main() {

    vector<pair<int,int>> intervals = {
        {1,4},
        {2,5},
        {7,9}
    };

    auto ans = mergeIntervals(intervals);

    for (auto [l, r] : ans) {
        cout << "[" << l << "," << r << "] ";
    }

    return 0;
}
```

---

# 11. CP Pattern 2: Insert Interval

## Problem Type

```text
Insert a new interval into sorted non-overlapping intervals.
```

## Step-by-Step Approach Before Code

```text
Step 1: Add all intervals ending before new interval starts.

Step 2: Merge all overlapping intervals with new interval.

Step 3: Add merged interval.

Step 4: Add remaining intervals.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<pair<int,int>> insertInterval(
    vector<pair<int,int>>& intervals,
    pair<int,int> newInterval
) {
    vector<pair<int,int>> answer;

    int i = 0;
    int n = intervals.size();

    while (i < n &&
           intervals[i].second < newInterval.first) {

        answer.push_back(intervals[i]);
        i++;
    }

    while (i < n &&
           intervals[i].first <= newInterval.second) {

        newInterval.first =
            min(newInterval.first, intervals[i].first);

        newInterval.second =
            max(newInterval.second, intervals[i].second);

        i++;
    }

    answer.push_back(newInterval);

    while (i < n) {

        answer.push_back(intervals[i]);
        i++;
    }

    return answer;
}

int main() {

    vector<pair<int,int>> intervals = {
        {1,3},
        {6,9}
    };

    auto ans =
        insertInterval(intervals, {2,5});

    for (auto [l, r] : ans) {
        cout << "[" << l << "," << r << "] ";
    }

    return 0;
}
```

---

# 12. CP Pattern 3: Non-Overlapping Intervals

## Problem Type

```text
Minimum intervals to remove to make intervals non-overlapping.
```

## Greedy Insight

Keep interval with smaller end because:

```text
it leaves more room for future intervals
```

## Step-by-Step Approach Before Code

```text
Step 1: Sort intervals by end time.

Step 2: Keep first interval.

Step 3: For every next interval:
        if overlap exists:
            remove current interval.
        else:
            keep it and update previous end.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int eraseOverlapIntervals(
    vector<pair<int,int>>& intervals
) {
    sort(
        intervals.begin(),
        intervals.end(),
        [](auto& a, auto& b) {
            return a.second < b.second;
        }
    );

    int removed = 0;

    int previousEnd = intervals[0].second;

    for (int i = 1; i < (int)intervals.size(); i++) {

        if (intervals[i].first < previousEnd) {

            removed++;

        } else {

            previousEnd = intervals[i].second;
        }
    }

    return removed;
}

int main() {

    vector<pair<int,int>> intervals = {
        {1,2},
        {2,3},
        {3,4},
        {1,3}
    };

    cout << eraseOverlapIntervals(intervals)
         << endl;

    return 0;
}
```

---

# 13. CP Pattern 4: Meeting Rooms

## Problem Type

```text
Minimum meeting rooms required.
```

## Core Idea

Track active meetings.

Use:

```text
min-heap of ending times
```

## Step-by-Step Approach Before Code

```text
Step 1: Sort meetings by start time.

Step 2: Min-heap stores active meeting end times.

Step 3: If earliest ending meeting finishes before current starts:
        reuse room.

Step 4: Push current meeting end.

Step 5: Heap size = active rooms.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int minMeetingRooms(
    vector<pair<int,int>>& meetings
) {
    sort(meetings.begin(), meetings.end());

    priority_queue<
        int,
        vector<int>,
        greater<int>
    > pq;

    for (auto [start, end] : meetings) {

        if (!pq.empty() &&
            pq.top() <= start) {

            pq.pop();
        }

        pq.push(end);
    }

    return pq.size();
}

int main() {

    vector<pair<int,int>> meetings = {
        {0,30},
        {5,10},
        {15,20}
    };

    cout << minMeetingRooms(meetings)
         << endl;

    return 0;
}
```

---

# 14. CP Pattern 5: Interval Intersection

## Problem Type

```text
Find intersections between two interval lists.
```

## Step-by-Step Approach Before Code

```text
Step 1: Use two pointers over both lists.

Step 2: Overlap start = max(starts).

Step 3: Overlap end = min(ends).

Step 4: If start <= end:
        valid intersection exists.

Step 5: Move interval with smaller ending time.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<pair<int,int>> intervalIntersection(
    vector<pair<int,int>>& A,
    vector<pair<int,int>>& B
) {
    int i = 0;
    int j = 0;

    vector<pair<int,int>> answer;

    while (i < (int)A.size() &&
           j < (int)B.size()) {

        int start =
            max(A[i].first, B[j].first);

        int end =
            min(A[i].second, B[j].second);

        if (start <= end) {

            answer.push_back({start, end});
        }

        if (A[i].second < B[j].second) {

            i++;

        } else {

            j++;
        }
    }

    return answer;
}

int main() {

    vector<pair<int,int>> A = {
        {0,2},
        {5,10},
        {13,23}
    };

    vector<pair<int,int>> B = {
        {1,5},
        {8,12},
        {15,24}
    };

    auto ans = intervalIntersection(A, B);

    for (auto [l, r] : ans) {
        cout << "[" << l << "," << r << "] ";
    }

    return 0;
}
```

---

# 15. CP Pattern 6: Difference Array Range Update

## Problem Type

```text
Apply many range increment updates efficiently.
```

Instead of updating every index:

```text
update only boundaries
```

## Core Idea

For update:

```text
add val to [l, r]
```

Do:

```text
diff[l] += val
diff[r+1] -= val
```

Later:

```text
prefix sum reconstructs final array
```

## Step-by-Step Approach Before Code

```text
Step 1: Create diff array initialized with 0.

Step 2: For update [l,r]:
        diff[l] += val
        diff[r+1] -= val if valid.

Step 3: Prefix sum rebuilds final values.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> rangeUpdate(
    int n,
    vector<tuple<int,int,int>>& updates
) {
    vector<int> diff(n + 1, 0);

    for (auto [l, r, val] : updates) {

        diff[l] += val;

        if (r + 1 < n) {
            diff[r + 1] -= val;
        }
    }

    vector<int> result(n);

    result[0] = diff[0];

    for (int i = 1; i < n; i++) {

        result[i] =
            result[i - 1] + diff[i];
    }

    return result;
}

int main() {

    vector<tuple<int,int,int>> updates = {
        {1,3,2},
        {2,4,3}
    };

    auto ans = rangeUpdate(5, updates);

    for (int x : ans) {
        cout << x << " ";
    }

    return 0;
}
```

---

# 16. CP Pattern 7: Coordinate Compression

## Problem Type

```text
Coordinates are huge:
10^9 scale
```

Need compact indices.

## Core Idea

Map:

```text
huge sparse coordinates
```

to:

```text
small dense indices
```

## Step-by-Step Approach Before Code

```text
Step 1: Collect all coordinates.

Step 2: Sort and remove duplicates.

Step 3: Map each coordinate to compressed index.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    vector<int> coords = {
        1000000000,
        500,
        999999999,
        500
    };

    vector<int> values = coords;

    sort(values.begin(), values.end());

    values.erase(
        unique(values.begin(), values.end()),
        values.end()
    );

    unordered_map<int,int> compressed;

    for (int i = 0; i < (int)values.size(); i++) {

        compressed[values[i]] = i;
    }

    for (int x : coords) {

        cout << x
             << " -> "
             << compressed[x]
             << endl;
    }

    return 0;
}
```

---

# 17. Intervals vs Sweep Line

| Feature | Intervals | Sweep Line |
|---|---|---|
| Main idea | process ranges | process events |
| Typical operation | merge/overlap | active count |
| Data representation | [start,end] | start/end events |
| Example | merge intervals | max overlap |
| Used for | bookings | concurrent activity |

Shortcut:

```text
Need merged ranges?
→ interval processing

Need active count over timeline?
→ sweep line
```

---

# 18. Common Mistakes

## Mistake 1: Forgetting To Sort

Most interval problems require:

```text
sort by start time
```

before processing.

---

## Mistake 2: Wrong Overlap Condition

Correct overlap:

```text
current.start <= previous.end
```

depending on inclusive/exclusive boundaries.

---

## Mistake 3: Updating Entire Range

Use difference array for many updates.

Instead of:

```text
O(N * updates)
```

use:

```text
O(updates + N)
```

---

## Mistake 4: Coordinate Compression Ordering

Must:

```text
sort unique values
```

before assigning compressed indices.

---

# 19. Complexity Table

| Pattern | Complexity |
|---|---:|
| merge intervals | O(N log N) |
| insert interval | O(N) |
| meeting rooms | O(N log N) |
| interval intersection | O(N + M) |
| range updates | O(U + N) |
| coordinate compression | O(N log N) |

---

# 20. Real-World Mapping

| Interval Concept | Real-System Meaning |
|---|---|
| merge intervals | booking compaction |
| overlap detection | calendar conflict |
| meeting rooms | server/resource allocation |
| interval intersection | shared availability |
| difference array | bulk updates |
| coordinate compression | sparse ID mapping |
| active ranges | streaming sessions |
| timeline engine | scheduler/calendar |

---

# 21. Final Mental Model

Intervals are:

```text
continuous active range engines
```

Best for:

```text
bookings
timelines
resource allocation
range updates
calendar systems
```

One-line CP rule:

```text
If objects represent continuous ranges in time/space, think intervals.
```

One-line system rule:

```text
Interval engines power calendars, schedulers, bookings, resource allocation, and time-range analytics.
```

---

# 22. Next Step

Next file:

```text
019_SweepLine_Event_Engine.md
```

Then:

```text
020_Production_STL_Pattern_Decision_Engine.md
```
