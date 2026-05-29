# 019_SweepLine_Event_Engine.md

> MiniSTLEngine Phase 019  
> Topic: Sweep Line as an **Event Timeline / Active Count / Overlap Engine** for CP, DSA, FAANG interviews, and real-system thinking.

---

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Sweep Line Is An Engine](#2-why-sweep-line-is-an-engine)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Sweep Line Core Behavior](#4-sweep-line-core-behavior)
- [5. Event Types](#5-event-types)
- [6. CP/DSA Recognition](#6-cpdsa-recognition)
- [7. Engine Architecture](#7-engine-architecture)
- [8. Basic Sweep Line Engine](#8-basic-sweep-line-engine)
- [9. Dry Run: Maximum Overlap](#9-dry-run-maximum-overlap)
- [10. CP Pattern 1: Maximum Number Of Overlapping Intervals](#10-cp-pattern-1-maximum-number-of-overlapping-intervals)
- [11. CP Pattern 2: Meeting Rooms Using Events](#11-cp-pattern-2-meeting-rooms-using-events)
- [12. CP Pattern 3: Car Pooling](#12-cp-pattern-3-car-pooling)
- [13. CP Pattern 4: Calendar Booking Conflict](#13-cp-pattern-4-calendar-booking-conflict)
- [14. CP Pattern 5: Range Addition With Ordered Events](#14-cp-pattern-5-range-addition-with-ordered-events)
- [15. CP Pattern 6: Skyline Problem Concept](#15-cp-pattern-6-skyline-problem-concept)
- [16. Sweep Line vs Interval Merge](#16-sweep-line-vs-interval-merge)
- [17. Common Mistakes](#17-common-mistakes)
- [18. Complexity Table](#18-complexity-table)
- [19. Real-World Mapping](#19-real-world-mapping)
- [20. Final Mental Model](#20-final-mental-model)
- [21. Next Step](#21-next-step)

---

# 1. Goal

Learn sweep line not only as:

```text
sort start/end points
```

but as an:

```text
Event Timeline / Active Count / Overlap Engine
```

It helps solve:

```text
maximum overlap
meeting rooms
calendar booking
car pooling
range addition
active sessions
skyline
timeline analytics
```

---

# 2. Why Sweep Line Is An Engine

Intervals describe ranges.

Sweep line converts ranges into events:

```text
start event
end event
```

Then processes all events in sorted order.

Normal thinking:

```text
check every pair of intervals
```

Engine thinking:

```text
SweepLineEventEngine
    converts ranges into timeline events
    scans timeline once
    maintains active state
    answers overlap/concurrency queries
```

Core question:

```text
Do I need to know how many things are active at each point in time?
```

If yes:

```text
sweep line may apply
```

---

# 3. Real-System Mental Model

Sweep line is used in:

```text
calendar concurrency
server active sessions
hotel room occupancy
meeting room allocation
network bandwidth usage
ride-sharing active drivers
video stream segment availability
CPU scheduling windows
```

Architecture:

```text
Intervals / Ranges
       |
       v
Convert To Events
       |
       v
Sort Events By Time
       |
       v
Sweep Timeline
       |
       +--> active += delta
       +--> update answer
       |
       v
Concurrency / Conflict / Timeline Result
```

---

# 4. Sweep Line Core Behavior

Interval:

```text
[start, end]
```

becomes:

```text
(start, +1)
(end, -1)
```

Then scan events:

```text
active += delta
```

Example:

```text
[1,5]
[2,6]
[4,7]
```

Events:

```text
1 +1
2 +1
4 +1
5 -1
6 -1
7 -1
```

Active count:

```text
1 -> 1
2 -> 2
4 -> 3
5 -> 2
6 -> 1
7 -> 0
```

Maximum overlap:

```text
3
```

---

# 5. Event Types

Common event representations:

```cpp
pair<int,int> event = {time, delta};
```

Meaning:

```text
delta = +1 for start
delta = -1 for end
```

For weighted events:

```text
position -> change
```

Example:

```text
passengers +3 at pickup
passengers -3 at dropoff
```

---

# 6. CP/DSA Recognition

Use sweep line when problem says:

```text
maximum overlap
active intervals
meeting rooms
car pooling
calendar booking
timeline
range add
concurrent users
events sorted by time
```

Hidden mapping:

| Problem clue | Sweep line pattern |
|---|---|
| max active intervals | start/end events |
| minimum rooms | active count max |
| car pooling | passenger delta map |
| calendar conflict | active count must not exceed 1 |
| range updates | ordered delta events |
| skyline | event + active heights |
| timeline analytics | sorted events scan |

---

# 7. Engine Architecture

```text
MiniSweepLineEventEngine
├── event converter
├── event sorter
├── active counter
├── max overlap engine
├── conflict detector
├── capacity checker
├── range delta engine
└── skyline active set engine
```

---

# 8. Basic Sweep Line Engine

## Step-by-Step Approach Before Code

```text
Step 1: Convert each interval [start, end] into events.

Step 2: Add start event as +1.

Step 3: Add end event as -1.

Step 4: Sort all events by time.

Step 5: Sweep from left to right.

Step 6: Maintain active count.

Step 7: Track maximum active count.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class SweepLineEventEngine {
public:

    int maxOverlap(vector<pair<int,int>>& intervals) {

        vector<pair<int,int>> events;

        for (auto [start, end] : intervals) {

            // Start event:
            // one interval becomes active.
            events.push_back({start, +1});

            // End event:
            // one interval becomes inactive.
            events.push_back({end, -1});
        }

        // Sort by time.
        // If same time occurs, pair sorting handles delta order too.
        sort(events.begin(), events.end());

        int active = 0;
        int best = 0;

        for (auto [time, delta] : events) {

            active += delta;

            best = max(best, active);
        }

        return best;
    }
};

int main() {

    vector<pair<int,int>> intervals = {
        {1,5},
        {2,6},
        {4,7}
    };

    SweepLineEventEngine engine;

    cout << engine.maxOverlap(intervals)
         << endl;

    return 0;
}
```

---

# 9. Dry Run: Maximum Overlap

Input:

```text
[1,5]
[2,6]
[4,7]
```

Events:

```text
(1,+1)
(5,-1)
(2,+1)
(6,-1)
(4,+1)
(7,-1)
```

Sorted:

```text
(1,+1)
(2,+1)
(4,+1)
(5,-1)
(6,-1)
(7,-1)
```

Sweep:

```text
time=1 active=1 best=1
time=2 active=2 best=2
time=4 active=3 best=3
time=5 active=2 best=3
time=6 active=1 best=3
time=7 active=0 best=3
```

Answer:

```text
3
```

---

# 10. CP Pattern 1: Maximum Number Of Overlapping Intervals

## Problem Type

```text
Find max number of intervals active at the same time.
```

## Step-by-Step Approach Before Code

```text
Step 1: Convert every interval into start/end events.

Step 2: Sort events by time.

Step 3: Sweep events and maintain active count.

Step 4: Update answer with maximum active count.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maximumOverlap(vector<pair<int,int>>& intervals) {

    vector<pair<int,int>> events;

    for (auto [l, r] : intervals) {

        events.push_back({l, +1});
        events.push_back({r, -1});
    }

    sort(events.begin(), events.end());

    int active = 0;
    int answer = 0;

    for (auto [time, delta] : events) {

        active += delta;

        answer = max(answer, active);
    }

    return answer;
}

int main() {

    vector<pair<int,int>> intervals = {
        {1,4},
        {2,5},
        {3,6}
    };

    cout << maximumOverlap(intervals)
         << endl;

    return 0;
}
```

---

# 11. CP Pattern 2: Meeting Rooms Using Events

## Problem Type

```text
Find minimum rooms needed for all meetings.
```

## Step-by-Step Approach Before Code

```text
Step 1: Meeting start increases active room count.

Step 2: Meeting end decreases active room count.

Step 3: Sort all events.

Step 4: Maximum active count = rooms needed.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int minMeetingRoomsEvents(
    vector<pair<int,int>>& meetings
) {
    vector<pair<int,int>> events;

    for (auto [start, end] : meetings) {

        events.push_back({start, +1});
        events.push_back({end, -1});
    }

    // Important:
    // For half-open intervals [start, end),
    // end should be processed before start at same time.
    sort(events.begin(), events.end());

    int active = 0;
    int rooms = 0;

    for (auto [time, delta] : events) {

        active += delta;

        rooms = max(rooms, active);
    }

    return rooms;
}

int main() {

    vector<pair<int,int>> meetings = {
        {0,30},
        {5,10},
        {15,20}
    };

    cout << minMeetingRoomsEvents(meetings)
         << endl;

    return 0;
}
```

---

# 12. CP Pattern 3: Car Pooling

## Problem Type

```text
Given trips [passengers, from, to],
check if car capacity is exceeded.
```

## Step-by-Step Approach Before Code

```text
Step 1: At pickup location:
        active passengers increase.

Step 2: At dropoff location:
        active passengers decrease.

Step 3: Use ordered map to store location -> delta.

Step 4: Sweep locations in sorted order.

Step 5: If active passengers > capacity:
        return false.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool carPooling(
    vector<tuple<int,int,int>>& trips,
    int capacity
) {
    map<int,int> delta;

    for (auto [passengers, from, to] : trips) {

        delta[from] += passengers;

        // At destination, passengers leave.
        delta[to] -= passengers;
    }

    int active = 0;

    for (auto [location, change] : delta) {

        active += change;

        if (active > capacity) {
            return false;
        }
    }

    return true;
}

int main() {

    vector<tuple<int,int,int>> trips = {
        {2,1,5},
        {3,3,7}
    };

    cout << carPooling(trips, 4)
         << endl;

    return 0;
}
```

---

# 13. CP Pattern 4: Calendar Booking Conflict

## Problem Type

```text
Book interval only if no overlap exists.
```

## Step-by-Step Approach Before Code

```text
Step 1: Store existing bookings.

Step 2: For new booking [start, end),
        check every existing booking.

Step 3: Conflict exists if:
        max(start1, start2) < min(end1, end2)

Step 4: If no conflict, add booking.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class MyCalendar {
private:
    vector<pair<int,int>> bookings;

public:

    bool book(int start, int end) {

        for (auto [s, e] : bookings) {

            bool overlap =
                max(start, s) < min(end, e);

            if (overlap) {
                return false;
            }
        }

        bookings.push_back({start, end});

        return true;
    }
};

int main() {

    MyCalendar cal;

    cout << cal.book(10, 20) << endl; // true
    cout << cal.book(15, 25) << endl; // false
    cout << cal.book(20, 30) << endl; // true

    return 0;
}
```

---

# 14. CP Pattern 5: Range Addition With Ordered Events

## Problem Type

```text
Apply range updates over large coordinate values.
```

## Core Idea

Use:

```text
map<position, delta>
```

Because coordinates may be huge and sparse.

## Step-by-Step Approach Before Code

```text
Step 1: For update [l, r] add value x:
        delta[l] += x
        delta[r+1] -= x

Step 2: Sweep positions in sorted order.

Step 3: Active value after prefix accumulation is final value.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    vector<tuple<int,int,int>> updates = {
        {100, 200, 5},
        {150, 300, 10}
    };

    map<int,int> delta;

    for (auto [l, r, value] : updates) {

        delta[l] += value;
        delta[r + 1] -= value;
    }

    int active = 0;

    for (auto [position, change] : delta) {

        active += change;

        cout << "from position "
             << position
             << ", value = "
             << active
             << endl;
    }

    return 0;
}
```

---

# 15. CP Pattern 6: Skyline Problem Concept

## Problem Type

```text
Given buildings [left, right, height],
return skyline outline.
```

## Core Idea

Events:

```text
building start -> add height
building end   -> remove height
```

Maintain active heights using:

```text
multiset
```

Whenever maximum height changes:

```text
skyline key point found
```

## Step-by-Step Approach Before Code

```text
Step 1: Convert each building into two events.

Step 2: Sort events by x-coordinate.

Step 3: Maintain multiset of active heights.

Step 4: At building start, insert height.

Step 5: At building end, erase one height.

Step 6: If current max height changes:
        record skyline point.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<pair<int,int>> getSkyline(
    vector<tuple<int,int,int>>& buildings
) {
    vector<pair<int,int>> events;

    for (auto [left, right, height] : buildings) {

        // Start event uses negative height
        // so starts are processed before ends.
        events.push_back({left, -height});

        // End event uses positive height.
        events.push_back({right, height});
    }

    sort(events.begin(), events.end());

    multiset<int> activeHeights;

    activeHeights.insert(0);

    int previousMax = 0;

    vector<pair<int,int>> skyline;

    for (auto [x, h] : events) {

        if (h < 0) {

            // Building starts.
            activeHeights.insert(-h);

        } else {

            // Building ends.
            auto it = activeHeights.find(h);

            if (it != activeHeights.end()) {
                activeHeights.erase(it);
            }
        }

        int currentMax =
            *activeHeights.rbegin();

        if (currentMax != previousMax) {

            skyline.push_back({x, currentMax});

            previousMax = currentMax;
        }
    }

    return skyline;
}

int main() {

    vector<tuple<int,int,int>> buildings = {
        {2,9,10},
        {3,7,15},
        {5,12,12}
    };

    auto ans = getSkyline(buildings);

    for (auto [x, h] : ans) {
        cout << "(" << x << "," << h << ") ";
    }

    return 0;
}
```

---

# 16. Sweep Line vs Interval Merge

| Feature | Interval Merge | Sweep Line |
|---|---|---|
| Goal | compact overlapping ranges | count/process active events |
| Input | intervals | events |
| Main state | last merged interval | active count/set |
| Example | busy blocks | max concurrent meetings |
| Output | merged ranges | overlap/count/timeline |

Shortcut:

```text
Need compressed ranges?
→ interval merge

Need active count or timeline state?
→ sweep line
```

---

# 17. Common Mistakes

## Mistake 1: Wrong Tie Handling

At same time:

```text
start before end
or
end before start
```

depends on problem.

For meeting rooms with half-open intervals:

```text
end before start
```

allows:

```text
[10,20] and [20,30]
```

to reuse room.

---

## Mistake 2: Closed vs Half-Open Interval Confusion

Closed interval:

```text
[start, end]
```

Half-open interval:

```text
[start, end)
```

Calendar systems usually use:

```text
[start, end)
```

---

## Mistake 3: Forgetting Ordered Processing

Sweep line requires:

```text
events sorted by coordinate/time
```

Use:

```cpp
sort(events.begin(), events.end());
```

or:

```cpp
map<int,int>
```

---

## Mistake 4: Removing All Heights In Skyline

For multiset:

```cpp
activeHeights.erase(height);
```

removes all occurrences.

Correct:

```cpp
activeHeights.erase(activeHeights.find(height));
```

---

# 18. Complexity Table

| Pattern | Complexity |
|---|---:|
| max overlap | O(N log N) |
| meeting rooms | O(N log N) |
| car pooling with map | O(N log N) |
| calendar brute conflict | O(N) per booking |
| ordered range delta | O(N log N) |
| skyline | O(N log N) |

---

# 19. Real-World Mapping

| Sweep Line Concept | Real-System Meaning |
|---|---|
| start event | resource becomes active |
| end event | resource becomes inactive |
| active count | concurrent load |
| max overlap | peak capacity needed |
| car pooling | passenger load tracking |
| meeting rooms | room/server allocation |
| calendar conflict | overlapping reservation |
| skyline | active maximum height/load |
| ordered delta map | sparse timeline update |

---

# 20. Final Mental Model

Sweep line is:

```text
timeline event processing engine
```

Best for:

```text
overlap counting
capacity checking
calendar booking
resource allocation
range deltas
active set problems
```

One-line CP rule:

```text
If intervals become start/end events and active state matters, think sweep line.
```

One-line system rule:

```text
Sweep line powers calendars, resource allocators, occupancy systems, and timeline analytics.
```

---

# 21. Next Step

Next file:

```text
020_Production_STL_Pattern_Decision_Engine.md
```
