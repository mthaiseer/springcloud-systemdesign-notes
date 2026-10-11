# AlgoZenith — Sweep Line Introduction
## Optimized TLE-Style Notes — Events + Active State + Offline Queries

> **Source:** supplied AlgoZenith **Sweep Line Introduction** lecture screenshots.
>
> **Main idea:** instead of checking every coordinate on a huge number line, process only coordinates where something **starts, ends, or is queried**.
>
> **Problem flow**
>
> ```text
> What it asks
> → small dry run
> → core observation
> → derivation
> → detailed event dry run
> → algorithm
> → C++17
> → complexity
> → recognition model
> ```
>
> **Variable rule**
>
> - Explanations/code: use descriptive names such as `activePeople`, `eventCoordinate`, `activeValues`.
> - Proofs/dry runs: short readable names such as `L`, `R`, `x`, `cnt`.
> - Use `long long` for coordinates, lengths, values, and sums.

---

# Clickable Table of Contents

- [0. Class Map](#0-class-map)
- [1. Prerequisites](#1-prerequisites)
  - [1.1 What Sweep Line Means](#11-what-sweep-line-means)
  - [1.2 Why Only Event Coordinates Matter](#12-why-only-event-coordinates-matter)
  - [1.3 Interval → Start / End Events](#13-interval--start--end-events)
  - [1.4 Active Count](#14-active-count)
  - [1.5 Why State Is Constant Between Events](#15-why-state-is-constant-between-events)
  - [1.6 Gap Contribution](#16-gap-contribution)
  - [1.7 Same-Coordinate Event Ordering](#17-same-coordinate-event-ordering)
  - [1.8 Sweep Line vs Difference Array](#18-sweep-line-vs-difference-array)
  - [1.9 Offline Queries](#19-offline-queries)
  - [1.10 Active Multiset](#110-active-multiset)
  - [1.11 Top-K Active Values](#111-top-k-active-values)
  - [1.12 Overflow](#112-overflow)
  - [1.13 Recognition Checklist](#113-recognition-checklist)
- [2. Problem 1 — Total Duration With At Least K People](#2-problem-1--total-duration-with-at-least-k-people)
  - [2.1 What It Asks](#21-what-it-asks)
  - [2.2 Small Dry Run](#22-small-dry-run)
  - [2.3 Core Observation](#23-core-observation)
  - [2.4 Event Transformation](#24-event-transformation)
  - [2.5 Why `nextX - currentX`](#25-why-nextx---currentx)
  - [2.6 Detailed Dry Run](#26-detailed-dry-run)
  - [2.7 Algorithm](#27-algorithm)
  - [2.8 C++17](#28-c17)
  - [2.9 Complexity](#29-complexity)
  - [2.10 Recognition Model](#210-recognition-model)
- [3. Problem 2 — Maximum Simultaneous Customers](#3-problem-2--maximum-simultaneous-customers)
  - [3.1 What It Asks](#31-what-it-asks)
  - [3.2 Small Dry Run](#32-small-dry-run)
  - [3.3 Core Observation](#33-core-observation)
  - [3.4 Detailed Dry Run](#34-detailed-dry-run)
  - [3.5 Algorithm](#35-algorithm)
  - [3.6 C++17](#36-c17)
  - [3.7 Complexity](#37-complexity)
  - [3.8 Recognition Model](#38-recognition-model)
- [4. Problem 3 — Offline Queries: Sum of Top-K Active Values](#4-problem-3--offline-queries-sum-of-top-k-active-values)
  - [4.1 What It Asks](#41-what-it-asks)
  - [4.2 Small Dry Run](#42-small-dry-run)
  - [4.3 Why It Becomes Sweep Line](#43-why-it-becomes-sweep-line)
  - [4.4 Three Event Types](#44-three-event-types)
  - [4.5 Why Event Type Order Matters](#45-why-event-type-order-matters)
  - [4.6 Active Multiset Invariant](#46-active-multiset-invariant)
  - [4.7 Detailed Dry Run](#47-detailed-dry-run)
  - [4.8 Algorithm](#48-algorithm)
  - [4.9 C++17](#49-c17)
  - [4.10 Complexity](#410-complexity)
  - [4.11 Recognition Model](#411-recognition-model)
- [Final Mental Model](#final-mental-model)

---

# 0. Class Map

```text
SWEEP LINE
│
├── Basic interval overlap
│   ├── start → +1
│   ├── end   → -1
│   └── maintain active count
│
├── Maximum simultaneous active intervals
│   └── max(active count)
│
└── Offline point queries
    ├── start event
    ├── query event
    ├── end event
    └── maintain active values in multiset
```

Main transformation:

```text
continuous coordinate line
        |
        v
only interesting coordinates
        |
        v
       EVENTS
        |
        v
sort events
        |
        v
sweep left → right
        |
        v
maintain ACTIVE STATE
```

---

# 1. Prerequisites

## 1.1 What Sweep Line Means

Imagine a vertical line moving from left to right.

```text
0----1----2----3----4----5----6----7----8----9---->

                    |
                    |
                 sweep line
```

Whenever the sweep reaches an important coordinate, something changes:

```text
interval starts
interval ends
query must be answered
```

So we process only those coordinates.

---

## 1.2 Why Only Event Coordinates Matter

Suppose there is no start/end event between:

```text
x = 5
```

and:

```text
x = 8
```

Then active intervals cannot change inside that gap.

```text
5---------------------------8
      no event here
```

Therefore:

```text
state on [5,8)
is constant
```

This is why sweep line works well even when coordinates are huge:

```text
coordinate values may be 1e9
but number of events is only O(N)
```

---

## 1.3 Interval → Start / End Events

For an interval:

```text
[L,R)
```

create:

```text
(L,+1)   // start
(R,-1)   // end
```

Example:

```text
[2,8)

2------------------------8
^                        ^
+1                      -1
```

Two intervals:

```text
[1,5)
[2,8)
```

become:

```text
1:+1
5:-1

2:+1
8:-1
```

Sorted:

```text
1:+1
2:+1
5:-1
8:-1
```

---

## 1.4 Active Count

Maintain:

```text
activeCount
```

Initially:

```text
0
```

At a start:

```text
activeCount++
```

At an end:

```text
activeCount--
```

Example:

```text
1:+1
2:+1
5:-1
8:-1
```

Count becomes:

```text
before 1 → 0
after 1  → 1
after 2  → 2
after 5  → 1
after 8  → 0
```

ASCII:

```text
count

2       +-----------+
        |           |
1   +---+           +-----------+
0 --+---------------------------+----> x
    1   2           5           8
```

---

## 1.5 Why State Is Constant Between Events

Let two consecutive event coordinates be:

```text
currentX
nextX
```

After processing the event at `currentX`, nothing changes until `nextX`.

Therefore:

```text
active state on [currentX,nextX)
=
state after currentX event
```

Example:

```text
at x=2:
activeCount becomes 2

next event:
x=5
```

So:

```text
activeCount = 2
```

for the whole interval:

```text
[2,5)
```

---

## 1.6 Gap Contribution

If a problem asks for total duration where:

```text
activeCount >= K
```

then for a valid gap:

```text
contribution
=
nextX - currentX
```

Example:

```text
currentX = 2
nextX    = 5
activeCount = 2
K = 2
```

Since:

```text
2 >= 2
```

contribution is:

```text
5 - 2
=
3
```

ASCII:

```text
2====================5
      cnt = 2

length = 3
```

---

## 1.7 Same-Coordinate Event Ordering

Tie ordering depends on the problem meaning.

### Duration problems

If two events happen at the same coordinate:

```text
x
```

the distance between them is:

```text
0
```

So for continuous length, tie order does not change the measured duration if the sweep is implemented correctly.

A clean version is:

```text
group all deltas at the same coordinate
```

---

### Inclusive point-query problems

If interval is:

```text
[L,R]
```

and a query occurs at:

```text
y = L
```

the interval must already be active.

So:

```text
START before QUERY
```

If:

```text
y = R
```

the interval must still be active.

So:

```text
QUERY before END
```

Therefore:

```text
START < QUERY < END
```

The lecture uses:

```text
0 = START
1 = QUERY
2 = END
```

Sorting `(coordinate,type)` automatically gives:

```text
START
QUERY
END
```

---

## 1.8 Sweep Line vs Difference Array

Difference array:

```text
diff[L] += 1
diff[R] -= 1
```

Then prefix sum reconstructs active count.

Sweep line is the same idea on sparse coordinates.

```text
Difference Array:
store every coordinate

Sweep Line:
store only coordinates where state changes
```

Use sweep line when:

```text
coordinate range is huge
but number of intervals is manageable
```

---

## 1.9 Offline Queries

Suppose all queries are known before processing.

Instead of:

```text
for each query
    scan all intervals
```

convert queries into events too.

```text
START event
QUERY event
END event
```

Sort everything by coordinate.

Then answer each query when the sweep reaches its coordinate.

Because queries are reordered during processing, store:

```text
original query index
```

and write the answer back there.

---

## 1.10 Active Multiset

Sometimes active state is not only a count.

Suppose each active interval has a value:

```text
Xi
```

Then:

```text
START → insert Xi
END   → erase one Xi
```

Use:

```cpp
multiset<long long>
```

because duplicate values may exist.

Example:

```text
active values:
3, 5, 5, 9
```

A `set` would lose one `5`.

To erase only one occurrence:

```cpp
auto it = activeValues.find(value);

if (it != activeValues.end())
    activeValues.erase(it);
```

---

## 1.11 Top-K Active Values

If a query asks:

```text
sum of largest K active values
```

and `K` is small, iterate backward from the multiset.

Example:

```text
active:
{1,3,5,7}

K = 2
```

Largest two:

```text
7 + 5
=
12
```

ASCII:

```text
1   3   5   7
        ^   ^
        top 2
```

If fewer than `K` values are active, use all active values.

---

## 1.12 Overflow

Use:

```cpp
long long
```

for:

```text
coordinates
duration
interval values
query answers
top-K sum
```

---

## 1.13 Recognition Checklist

If the statement has:

```text
intervals
arrival / departure
people entering / leaving
coverage
overlap count
maximum simultaneous activity
queries at points
```

think:

```text
sweep line
```

Then ask:

```text
What must I maintain about currently active intervals?
```

Possible active state:

```text
count
sum
minimum
maximum
set
multiset
top K
```

---

# 2. Problem 1 — Total Duration With At Least K People

## 2.1 What It Asks

The lecture starts with:

```text
N people
```

Person `i` is present in:

```text
[Li,Ri]
```

Given:

```text
K
```

find total duration where:

```text
number of people present >= K
```

The important word is:

```text
total duration
```

So we need lengths of valid sweep gaps.

---

## 2.2 Small Dry Run

Take:

```text
K = 2
```

Intervals:

```text
A: [1,5)
B: [2,8)
C: [7,10)
D: [9,11)
```

Visual:

```text
1   2       5       7   8   9   10  11
|---|-------|-------|---|---|---|---|

A:  [===============)
B:      [=======================)
C:                      [===========)
D:                              [=======)
```

Counts:

```text
[1,2)   → 1
[2,5)   → 2   ✓
[5,7)   → 1
[7,8)   → 2   ✓
[8,9)   → 1
[9,10)  → 2   ✓
[10,11) → 1
```

Answer:

```text
(5-2)
+
(8-7)
+
(10-9)

=
3 + 1 + 1

=
5
```

---

## 2.3 Core Observation

The number of present people changes only at:

```text
Li
Ri
```

So:

```text
Li → +1
Ri → -1
```

After sorting events:

```text
maintain activePeople
```

Between two consecutive event coordinates:

```text
activePeople is constant
```

If:

```text
activePeople >= K
```

add that whole gap.

---

## 2.4 Event Transformation

Intervals:

```text
[1,5)
[2,8)
[7,10)
[9,11)
```

Events:

```text
(1,+1)
(5,-1)

(2,+1)
(8,-1)

(7,+1)
(10,-1)

(9,+1)
(11,-1)
```

Sorted:

```text
1:+1
2:+1
5:-1
7:+1
8:-1
9:+1
10:-1
11:-1
```

ASCII:

```text
x:       1   2   5   7   8   9   10  11
event:  +1  +1  -1  +1  -1  +1  -1  -1
```

---

## 2.5 Why `nextX - currentX`

Suppose after event at:

```text
currentX = 2
```

we have:

```text
activePeople = 2
```

Next event is:

```text
nextX = 5
```

No event occurs inside:

```text
(2,5)
```

Therefore active count stays:

```text
2
```

throughout:

```text
[2,5)
```

Since:

```text
2 >= K
```

add:

```text
5 - 2
=
3
```

That is the entire sweep-line derivation.

---

## 2.6 Detailed Dry Run

Events:

```text
(1,+1)
(2,+1)
(5,-1)
(7,+1)
(8,-1)
(9,+1)
(10,-1)
(11,-1)
```

Start:

```text
activePeople = 0
answer = 0
```

| Event | Count after event | Next x | `count >= 2`? | Add |
|---|---:|---:|---|---:|
| `(1,+1)` | `1` | `2` | no | `0` |
| `(2,+1)` | `2` | `5` | yes | `3` |
| `(5,-1)` | `1` | `7` | no | `0` |
| `(7,+1)` | `2` | `8` | yes | `1` |
| `(8,-1)` | `1` | `9` | no | `0` |
| `(9,+1)` | `2` | `10` | yes | `1` |
| `(10,-1)` | `1` | `11` | no | `0` |

Final:

```text
answer = 5
```

Count graph:

```text
count

2       +-------+       +-+       +-+
        |       |       | |       | |
1   +---+       +-------+ +-------+ +---+
0 --+--------------------------------------> x
    1   2       5       7 8       9 10 11
```

---

## 2.7 Algorithm

```text
events = []

for each [L,R):

    add (L,+1)
    add (R,-1)

sort events

activePeople = 0
answer = 0

for each event i except last:

    activePeople += events[i].delta

    if activePeople >= K:

        answer +=
            events[i+1].x
            -
            events[i].x
```

---

## 2.8 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfPeople;
    long long requiredPeople;

    cin >> numberOfPeople >> requiredPeople;

    vector<pair<long long, int>> events;

    for (int person = 0;
         person < numberOfPeople;
         ++person) {

        long long left, right;
        cin >> left >> right;

        events.push_back({left, +1});
        events.push_back({right, -1});
    }

    sort(events.begin(), events.end());

    long long activePeople = 0;
    long long totalDuration = 0;

    for (int index = 0;
         index + 1 < (int)events.size();
         ++index) {

        activePeople += events[index].second;

        if (activePeople >= requiredPeople) {
            totalDuration +=
                events[index + 1].first
                -
                events[index].first;
        }
    }

    cout << totalDuration << '\n';
    return 0;
}
```

---

## 2.9 Complexity

Events:

```text
2N
```

Therefore:

```text
sorting:
O(N log N)

sweep:
O(N)

total:
O(N log N)

space:
O(N)
```

---

## 2.10 Recognition Model

Problem says:

```text
intervals
+
total length/time
where overlap >= K
```

Think:

```text
start +1
end   -1
sort
running count
```

Then:

```text
if count >= K
add nextX - currentX
```

---

# 3. Problem 2 — Maximum Simultaneous Customers

## 3.1 What It Asks

The lecture opens the CSES problem:

```text
Restaurant Customers
```

Each customer has:

```text
arrival time
leaving time
```

Find:

```text
maximum number of customers
inside at the same time
```

Same sweep line.

Only the objective changes.

---

## 3.2 Small Dry Run

Customers:

```text
A: [1,5)
B: [2,8)
C: [3,6)
```

Events:

```text
1:+1
2:+1
3:+1
5:-1
6:-1
8:-1
```

Running count:

```text
1
2
3
2
1
0
```

Maximum:

```text
3
```

---

## 3.3 Core Observation

Use:

```text
arrival → +1
leave   → -1
```

At every event:

```text
activeCustomers += delta
```

Then:

```text
maximumCustomers =
max(maximumCustomers, activeCustomers)
```

No gap length is required because the question asks only for:

```text
maximum count
```

---

## 3.4 Detailed Dry Run

Input:

```text
[1,4)
[2,6)
[3,5)
```

Events:

```text
(1,+1)
(2,+1)
(3,+1)
(4,-1)
(5,-1)
(6,-1)
```

| Event | Active | Maximum |
|---|---:|---:|
| `(1,+1)` | `1` | `1` |
| `(2,+1)` | `2` | `2` |
| `(3,+1)` | `3` | `3` |
| `(4,-1)` | `2` | `3` |
| `(5,-1)` | `1` | `3` |
| `(6,-1)` | `0` | `3` |

Answer:

```text
3
```

ASCII:

```text
active

3           +---+
            |   |
2       +---+   +---+
        |           |
1   +---+           +---+
0 --+-------------------+----> time
    1   2   3   4   5   6
```

---

## 3.5 Algorithm

```text
events = []

for each customer:

    add (arrival,+1)
    add (leaving,-1)

sort events

active = 0
answer = 0

for each event:

    active += delta
    answer = max(answer, active)
```

The CSES screenshot in the lecture states that all arrival and leaving times are distinct, so tie handling is not needed for that exact problem.

---

## 3.6 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfCustomers;
    cin >> numberOfCustomers;

    vector<pair<long long, int>> events;

    for (int customer = 0;
         customer < numberOfCustomers;
         ++customer) {

        long long arrivalTime, leavingTime;
        cin >> arrivalTime >> leavingTime;

        events.push_back({arrivalTime, +1});
        events.push_back({leavingTime, -1});
    }

    sort(events.begin(), events.end());

    int activeCustomers = 0;
    int maximumCustomers = 0;

    for (const auto& event : events) {
        activeCustomers += event.second;

        maximumCustomers =
            max(maximumCustomers, activeCustomers);
    }

    cout << maximumCustomers << '\n';
    return 0;
}
```

---

## 3.7 Complexity

```text
Time:
O(N log N)

Space:
O(N)
```

---

## 3.8 Recognition Model

If the problem asks:

```text
maximum number of overlapping intervals
```

or:

```text
maximum simultaneous customers/people
```

think:

```text
start +1
end   -1
sort
running count
take maximum
```

---

# 4. Problem 3 — Offline Queries: Sum of Top-K Active Values

## 4.1 What It Asks

The lecture's advanced sweep-line example has:

```text
N intervals
```

Each interval has:

```text
Li, Ri, Xi
```

where `Xi` is the interval's value.

For query coordinate:

```text
Y
```

find:

```text
sum of top K Xi values
among intervals covering Y
```

The board illustrates:

```text
K = 2
```

and shows small `K`, which makes direct top-K scanning from a multiset practical.

---

## 4.2 Small Dry Run

Intervals:

```text
A: [1,9],  X=1
B: [3,10], X=3
C: [5,7],  X=2
```

Query:

```text
Y = 6
K = 2
```

At `6`, all three are active:

```text
values:
1, 3, 2
```

Largest two:

```text
3 + 2
=
5
```

ASCII:

```text
1   3   5   6   7   9   10
|---|---|---|---|---|---|

A: [=======================]  X=1
B:     [=======================] X=3
C:         [=======]             X=2
                ^
                Y=6
```

Active:

```text
{1,2,3}
```

Answer:

```text
5
```

---

## 4.3 Why It Becomes Sweep Line

Naive:

```text
for each query Y:
    scan all N intervals
    test Li <= Y <= Ri
    collect values
    choose top K
```

Cost:

```text
O(NQ)
```

Instead, each interval changes activity only at:

```text
Li
Ri
```

Each query matters only at:

```text
Y
```

So combine:

```text
interval start
interval end
query
```

into one event list.

---

## 4.4 Three Event Types

Lecture event ordering:

```text
0 = START
1 = QUERY
2 = END
```

For interval:

```text
[L,R], value X
```

events:

```text
(L, START, X)
(R, END,   X)
```

For query:

```text
(Y, K, queryIndex)
```

event:

```text
(Y, QUERY, K, queryIndex)
```

Visual:

```text
          coordinate x
               |
      +--------+--------+
      |        |        |
    START    QUERY     END
```

---

## 4.5 Why Event Type Order Matters

Intervals are treated as inclusive at a query point:

```text
[L,R]
```

At:

```text
Y = L
```

the interval must be visible to the query.

So:

```text
START before QUERY
```

At:

```text
Y = R
```

the interval must still be visible.

So:

```text
QUERY before END
```

Hence:

```text
START < QUERY < END
```

Numeric event types:

```text
0 < 1 < 2
```

give the required ordering automatically.

ASCII:

```text
same coordinate x

START
  |
  v
QUERY
  |
  v
END
```

---

## 4.6 Active Multiset Invariant

At current sweep coordinate:

```text
activeValues
```

must contain exactly the `Xi` values of intervals covering that coordinate.

Operations:

```text
START:
insert Xi

END:
erase one Xi

QUERY:
read largest K values
```

Example:

```text
activeValues =
{1,2,3,5}

K = 3
```

Take:

```text
5 + 3 + 2
=
10
```

---

## 4.7 Detailed Dry Run

Use:

```text
K = 2
```

Intervals:

```text
A: [1,9],  X=1
B: [3,10], X=3
C: [5,7],  X=2
```

Queries:

```text
Y1 = 2
Y2 = 6
Y3 = 10
```

Events:

```text
1  START 1
9  END   1

3  START 3
10 END   3

5  START 2
7  END   2

2  QUERY #0
6  QUERY #1
10 QUERY #2
```

Sorted with:

```text
START < QUERY < END
```

gives:

```text
1   START 1
2   QUERY
3   START 3
5   START 2
6   QUERY
7   END 2
9   END 1
10  QUERY
10  END 3
```

Sweep:

```text
x=1:
START 1
active = {1}

x=2:
QUERY
top 2 = 1
answer = 1

x=3:
START 3
active = {1,3}

x=5:
START 2
active = {1,2,3}

x=6:
QUERY
top 2 = 3+2 = 5

x=7:
END 2
active = {1,3}

x=9:
END 1
active = {3}

x=10:
QUERY first
active = {3}
answer = 3

then END 3
active = {}
```

Final:

```text
Y=2  → 1
Y=6  → 5
Y=10 → 3
```

ASCII:

```text
x:      1   2   3   5   6   7   9   10
        |   |   |   |   |   |   |   |
event:  S   Q   S   S   Q   E   E   Q,E

active:
        {1}
            → Q: 1
                {1,3}
                    {1,2,3}
                        → Q: 5
                            {1,3}
                                {3}
                                    → Q: 3
```

---

## 4.8 Algorithm

```text
events = []

for each interval:

    add START event at L
    add END event at R

for each query:

    add QUERY event at Y
    remember original query index

sort by:
    coordinate
    then event type

activeValues = multiset

for each event:

    START:
        insert value

    END:
        erase one value

    QUERY:
        sum largest K values
        store answer at original index
```

---

## 4.9 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Event {
    long long coordinate;
    int type;

    long long value;
    int index;
};

bool eventOrder(
    const Event& first,
    const Event& second
) {
    if (first.coordinate != second.coordinate)
        return first.coordinate < second.coordinate;

    return first.type < second.type;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfIntervals;
    cin >> numberOfIntervals;

    vector<Event> events;

    for (int index = 0;
         index < numberOfIntervals;
         ++index) {

        long long left, right, value;
        cin >> left >> right >> value;

        events.push_back({
            left,
            0,
            value,
            index
        });

        events.push_back({
            right,
            2,
            value,
            index
        });
    }

    int numberOfQueries;
    cin >> numberOfQueries;

    vector<long long> answers(numberOfQueries);

    for (int queryIndex = 0;
         queryIndex < numberOfQueries;
         ++queryIndex) {

        long long coordinate;
        int requiredCount;

        cin >> coordinate >> requiredCount;

        events.push_back({
            coordinate,
            1,
            requiredCount,
            queryIndex
        });
    }

    sort(
        events.begin(),
        events.end(),
        eventOrder
    );

    multiset<long long> activeValues;

    for (const Event& event : events) {
        if (event.type == 0) {
            activeValues.insert(event.value);
        }

        else if (event.type == 2) {
            auto it =
                activeValues.find(event.value);

            if (it != activeValues.end())
                activeValues.erase(it);
        }

        else {
            int requiredCount = event.value;

            long long currentAnswer = 0;
            int usedValues = 0;

            for (auto it = activeValues.rbegin();
                 it != activeValues.rend() &&
                 usedValues < requiredCount;
                 ++it) {

                currentAnswer += *it;
                usedValues++;
            }

            answers[event.index] =
                currentAnswer;
        }
    }

    for (long long answer : answers)
        cout << answer << '\n';

    return 0;
}
```

---

## 4.10 Complexity

Let:

```text
N = number of intervals
Q = number of queries
K = requested top count
```

Events:

```text
2N + Q
```

Sorting:

```text
O((N+Q) log(N+Q))
```

Each insertion/removal:

```text
O(log N)
```

Each query:

```text
O(K)
```

for scanning the largest `K` active values.

Total:

```text
O((N+Q) log(N+Q) + N log N + QK)
```

Space:

```text
O(N+Q)
```

---

## 4.11 Recognition Model

Problem says:

```text
interval [L,R]
has value X

query at Y:
consider intervals covering Y
```

Think:

```text
offline sweep line
```

Then:

```text
START → add X
END   → remove X
QUERY → use current active values
```

If query asks:

```text
top K
```

and `K` is small:

```text
multiset
+
reverse iteration
```

---

# Final Mental Model

```text
                      SWEEP LINE
                          |
                          v
                convert to EVENTS
                          |
         +----------------+----------------+
         |                |                |
         v                v                v
       START            QUERY             END
     add state        read state       remove state
         \                |                /
          \               |               /
           +--------------+--------------+
                          |
                          v
                sort by coordinate
                          |
                          v
                 sweep left → right
                          |
                          v
                maintain active state
                          |
          +---------------+---------------+
          |               |               |
          v               v               v
       COUNT          MAX COUNT         MULTISET
          |               |               |
          v               v               v
 duration where     max simultaneous   top-K query
 count >= K            overlap
```

Core duration formula:

```text
if activeCount >= K:

    answer +=
        nextEventCoordinate
        -
        currentEventCoordinate
```

Core inclusive-query tie order:

```text
START
  ↓
QUERY
  ↓
END
```

The most important contest question is:

```text
"What active information do I need
while sweeping from left to right?"
```

If only overlap count matters:

```text
counter
```

If values of active intervals matter:

```text
set / multiset / another ordered structure
```

> **Core lesson:** sweep line converts a continuous interval problem into a sorted list of state changes. Between consecutive event coordinates, nothing changes.
