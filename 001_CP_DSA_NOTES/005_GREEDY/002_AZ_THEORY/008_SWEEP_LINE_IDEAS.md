# AlgoZenith — Sweep Line Ideas
## Optimized TLE-Style Notes — Offline Queries + Maximum Active Value + Union / Intersection Length

> **Source:** supplied AlgoZenith **Sweep Line Ideas** lecture screenshots.
>
> **Goal:** understand how to convert interval problems into sorted events, maintain only the currently active information, and answer queries or accumulate lengths while sweeping from left to right.
>
> **Problem flow used throughout**
>
> ```text
> What it asks
> → small dry run
> → core observation
> → derivation with inline numbers
> → event representation
> → detailed sweep dry run
> → algorithm
> → C++17
> → complexity
> → recognition model
> ```
>
> **Variable rule**
>
> - Explanations/code use descriptive names such as `leftEndpoint`, `queryCoordinate`, `activeValues`.
> - Proofs/derivations use shorter readable names such as `L`, `R`, `Y`, `X`, `cnt`.
> - Coordinates, values, lengths, and answers use `long long`.

---

# Clickable Table of Contents

- [0. Class Map](#0-class-map)
- [1. Prerequisites](#1-prerequisites)
  - [1.1 Sweep Line in One Sentence](#11-sweep-line-in-one-sentence)
  - [1.2 Why Events Are Enough](#12-why-events-are-enough)
  - [1.3 Interval Events](#13-interval-events)
  - [1.4 Active State](#14-active-state)
  - [1.5 Event Tuple](#15-event-tuple)
  - [1.6 Offline vs Online Queries](#16-offline-vs-online-queries)
  - [1.7 Same-Coordinate Ordering](#17-same-coordinate-ordering)
  - [1.8 Why Multiset](#18-why-multiset)
  - [1.9 Maximum Active Value](#19-maximum-active-value)
  - [1.10 Constant State Between Consecutive Events](#110-constant-state-between-consecutive-events)
  - [1.11 Union of Intervals](#111-union-of-intervals)
  - [1.12 Intersection of All Intervals](#112-intersection-of-all-intervals)
  - [1.13 Sweep-Line View of Union and Intersection](#113-sweep-line-view-of-union-and-intersection)
  - [1.14 Overflow](#114-overflow)
  - [1.15 Recognition Checklist](#115-recognition-checklist)
- [2. Problem 1 — Maximum X Among Segments Covering Query Y](#2-problem-1--maximum-x-among-segments-covering-query-y)
  - [2.1 What It Asks](#21-what-it-asks)
  - [2.2 Small Dry Run First](#22-small-dry-run-first)
  - [2.3 Why the Naive Approach Is Too Slow](#23-why-the-naive-approach-is-too-slow)
  - [2.4 Core Observation](#24-core-observation)
  - [2.5 Event Construction](#25-event-construction)
  - [2.6 Why START → QUERY → END](#26-why-start--query--end)
  - [2.7 Multiset Invariant](#27-multiset-invariant)
  - [2.8 Detailed Sweep Dry Run](#28-detailed-sweep-dry-run)
  - [2.9 Algorithm](#29-algorithm)
  - [2.10 C++17](#210-c17)
  - [2.11 Complexity](#211-complexity)
  - [2.12 Recognition Model](#212-recognition-model)
- [3. Problem 2 — Length of Union and Intersection of N Segments](#3-problem-2--length-of-union-and-intersection-of-n-segments)
  - [3.1 What It Asks](#31-what-it-asks)
  - [3.2 Direct Meaning of Union and Intersection](#32-direct-meaning-of-union-and-intersection)
  - [3.3 Small Dry Run First](#33-small-dry-run-first)
  - [3.4 Core Observation](#34-core-observation)
  - [3.5 Deriving the Union Condition](#35-deriving-the-union-condition)
  - [3.6 Deriving the Intersection Condition](#36-deriving-the-intersection-condition)
  - [3.7 Event Construction](#37-event-construction)
  - [3.8 Detailed Sweep Dry Run](#38-detailed-sweep-dry-run)
  - [3.9 Algorithm](#39-algorithm)
  - [3.10 C++17](#310-c17)
  - [3.11 Complexity](#311-complexity)
  - [3.12 Recognition Model](#312-recognition-model)
- [4. Final Mental Model](#4-final-mental-model)

---

# 0. Class Map

The lecture develops two important sweep-line patterns.

```text
                         SWEEP LINE IDEAS
                                |
                 +--------------+--------------+
                 |                             |
                 v                             v
          POINT QUERIES                 LENGTH OF REGIONS
                 |                             |
        interval has value X                   |
                 |                             |
                 v                             v
       START / QUERY / END              START +1 / END -1
                 |                             |
                 v                             v
          active multiset                activeCount
                 |                             |
                 v                  +----------+----------+
       maximum active X             |                     |
                                    v                     v
                              activeCount > 0       activeCount == N
                                    |                     |
                                    v                     v
                              UNION LENGTH        INTERSECTION LENGTH
```

The shared skeleton is:

```text
interval information
       |
       v
convert to events
       |
       v
sort by coordinate
       |
       v
sweep left → right
       |
       v
maintain only what is active NOW
```

---

# 1. Prerequisites

## 1.1 Sweep Line in One Sentence

Sweep line means:

```text
sort all coordinates where the state changes
and process those changes from left to right.
```

Imagine a vertical line moving through the number line.

```text
0----1----2----3----4----5----6----7----8----9----10---->

                         |
                         |
                      sweep
```

The sweep does not care about every coordinate.

It cares about:

```text
start of segment
end of segment
query coordinate
```

---

## 1.2 Why Events Are Enough

Suppose we have:

```text
segment [2,8]
```

Its active status changes only at:

```text
2
8
```

Between them:

```text
2-----------------------------8
        same segment active
```

Nothing new happens at:

```text
3
4
5
6
7
```

unless another segment/query has an event there.

So large coordinate values such as:

```text
1e9
```

do not force us to iterate over:

```text
1,2,3,...,1e9
```

We process only:

```text
O(number of events)
```

coordinates.

---

## 1.3 Interval Events

For length-style interval processing, think:

```text
[L,R)

L → +1
R → -1
```

Example:

```text
[2,6)

2------------------6
^                  ^
+1                -1
```

For point-query processing with inclusive intervals:

```text
[L,R]
```

we use semantic event types:

```text
L → START
R → END
```

because event ordering at the same coordinate matters.

---

## 1.4 Active State

The most important question in sweep line is:

```text
"What information do I need
about the intervals active right now?"
```

Examples:

```text
only number of active intervals
→ counter

maximum X among active intervals
→ ordered multiset

all active X values
→ multiset

sum of active values
→ running sum
```

For this lecture:

```text
Problem 1:
active state = multiset of X values

Problem 2:
active state = number of active segments
```

---

## 1.5 Event Tuple

A useful event representation is:

```text
(coordinate, type, payload)
```

Example:

```text
segment:
[L,R], value X
```

can become:

```text
(L, START, X)
(R, END,   X)
```

A query:

```text
coordinate Y
query index q
```

becomes:

```text
(Y, QUERY, q)
```

ASCII:

```text
event
├── coordinate
├── type
└── payload
```

Example:

```text
segment [2,6], X=5
query Y=3, queryIndex=0

events:

(2, START, 5)
(6, END,   5)
(3, QUERY, 0)
```

After sorting:

```text
(2, START, 5)
(3, QUERY, 0)
(6, END,   5)
```

---

## 1.6 Offline vs Online Queries

The lecture explicitly contrasts:

```text
OFFLINE
vs
ONLINE
```

### Offline

All queries are already known.

So we are allowed to:

```text
collect them
reorder them
sort them by coordinate
answer them in sweep order
```

Then restore original order using:

```text
queryIndex
```

Example:

```text
original queries:

Y = 8
Y = 3
Y = 6
```

Sweep may process:

```text
3
6
8
```

but answers are stored as:

```text
answer[originalIndex]
```

---

### Online

Queries arrive one by one and usually must be answered immediately.

You cannot freely reorder future queries because they are not all known yet.

So:

```text
offline query
→ sorting is available

online query
→ usually need a dynamic data structure
```

This lecture's query solution is an:

```text
offline sweep-line solution
```

---

## 1.7 Same-Coordinate Ordering

This is one of the most important parts of the lecture.

Suppose the interval is inclusive:

```text
[L,R]
```

and query is at:

```text
Y
```

We want:

```text
L <= Y <= R
```

Therefore if:

```text
Y = L
```

the segment must already be active.

So:

```text
START before QUERY
```

If:

```text
Y = R
```

the segment must still be active during the query.

So:

```text
QUERY before END
```

Hence:

```text
START < QUERY < END
```

Use event types:

```text
START = 0
QUERY = 1
END   = 2
```

Then ordinary sorting by:

```text
(coordinate, type)
```

produces the correct order.

### Visual

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

### Inline Example 1

Segment:

```text
[3,7], X=8
```

Query:

```text
Y=3
```

At coordinate `3`:

```text
START X=8
then QUERY
```

So query sees:

```text
8
```

Correct because:

```text
3 <= 3 <= 7
```

### Inline Example 2

Same segment:

```text
[3,7], X=8
```

Query:

```text
Y=7
```

At `7`:

```text
QUERY
then END X=8
```

So query still sees `8`.

Correct because:

```text
3 <= 7 <= 7
```

---

## 1.8 Why Multiset

Problem 1 needs:

```text
insert X
remove X
find current maximum X
```

Also multiple segments can have the same value.

Example:

```text
active X values:

2, 5, 5, 7
```

Therefore:

```cpp
multiset<long long>
```

is suitable.

It supports:

```text
insert       O(log N)
erase one    O(log N)
maximum      *rbegin()
```

Why not `set`?

Because:

```text
set:
2,5,5,7
becomes
2,5,7
```

One duplicate disappears.

`multiset` keeps both copies.

---

## 1.9 Maximum Active Value

If:

```text
activeValues = {2,4,7,7}
```

then:

```cpp
*activeValues.rbegin()
```

is:

```text
7
```

If no segment covers the query:

```text
activeValues is empty
```

the lecture code returns:

```text
0
```

So:

```text
if activeValues.empty()
    answer = 0
else
    answer = maximum active X
```

---

## 1.10 Constant State Between Consecutive Events

For length problems, suppose sorted event coordinates are:

```text
x0 < x1 < x2 < ...
```

After processing the event at:

```text
xi
```

nothing changes until:

```text
xi+1
```

Therefore the active count is constant on:

```text
[xi, xi+1)
```

ASCII:

```text
xi==========================xi+1
       same activeCount
```

Gap length:

```text
xi+1 - xi
```

That gap can be added completely or ignored completely.

---

## 1.11 Union of Intervals

The union means:

```text
points covered by AT LEAST ONE segment
```

Example:

```text
A: [2,6]
B: [4,8]
```

Union:

```text
[2,8]
```

ASCII:

```text
A:  [--------------]
B:        [--------------]

U:  [--------------------]
    2                    8
```

Sweep-line condition:

```text
activeCount > 0
```

because at least one interval is active.

---

## 1.12 Intersection of All Intervals

The intersection of all `N` intervals means:

```text
points covered by EVERY segment
```

For intervals:

```text
[L1,R1]
[L2,R2]
...
[LN,RN]
```

the common intersection is:

```text
[max(Li), min(Ri)]
```

provided:

```text
max(Li) <= min(Ri)
```

Otherwise:

```text
intersection = empty
```

### Inline Example

```text
[2,8]
[3,7]
[4,10]
```

Largest left endpoint:

```text
max(2,3,4)
=
4
```

Smallest right endpoint:

```text
min(8,7,10)
=
7
```

So intersection:

```text
[4,7]
```

ASCII:

```text
[2----------------8]
   [3----------7]
      [4----------------10]

      [4------7]
      intersection
```

### No Intersection Example

```text
[2,5]
[6,9]
```

Then:

```text
maxLeft  = 6
minRight = 5
```

Since:

```text
6 > 5
```

intersection is empty.

---

## 1.13 Sweep-Line View of Union and Intersection

The direct formula above finds the intersection of all intervals.

But the lecture uses sweep line to connect union and intersection through:

```text
activeCount
```

### Union

A point belongs to the union when:

```text
activeCount > 0
```

### Intersection of All N

A point belongs to every interval when:

```text
activeCount == N
```

So between consecutive event coordinates:

```text
if activeCount > 0
    add gap to unionLength

if activeCount == N
    add gap to intersectionLength
```

This single sweep computes both.

---

## 1.14 Overflow

Use:

```cpp
long long
```

for:

```text
L
R
Y
X
event coordinates
union length
intersection length
query answers
```

Even if one coordinate fits in `int`, accumulated lengths or values may exceed it.

---

## 1.15 Recognition Checklist

Think sweep line when you see:

```text
N intervals / segments
large coordinates
point queries
covering a point
active segments
maximum among active values
union length
intersection length
overlap count
```

Then ask:

```text
1. What are the events?
2. What is the correct tie order?
3. What active state must I maintain?
4. What do I do between two events?
```

---

# 2. Problem 1 — Maximum X Among Segments Covering Query Y

## 2.1 What It Asks

The lecture gives:

```text
N segments
```

Each segment has:

```text
L, R, X
```

and covers:

```text
[L,R]
```

There are:

```text
Q queries
```

Each query gives a coordinate:

```text
Y
```

For every query, find the maximum:

```text
X
```

among all segments satisfying:

```text
L <= Y <= R
```

If no segment covers `Y`, the lecture implementation returns:

```text
0
```

The constraints shown are large enough that scanning every segment for every query is not intended.

---

## 2.2 Small Dry Run First

Segments:

```text
A: [2,7],  X=5
B: [4,8],  X=8
C: [6,10], X=4
D: [12,14],X=2
```

Queries:

```text
Y = 3
Y = 6
Y = 9
Y = 11
```

Visual:

```text
x:
2   3   4   5   6   7   8   9   10  11  12  13  14
|---|---|---|---|---|---|---|---|---|---|---|---|

A X=5:
[===================]

B X=8:
        [===================]

C X=4:
                [===================]

D X=2:
                                        [=======]
```

### Query `Y=3`

Active:

```text
A only
```

Answer:

```text
5
```

### Query `Y=6`

Active:

```text
A → 5
B → 8
C → 4
```

Maximum:

```text
8
```

### Query `Y=9`

Active:

```text
C → 4
```

Answer:

```text
4
```

### Query `Y=11`

Active:

```text
none
```

Answer:

```text
0
```

So:

```text
answers:
5 8 4 0
```

---

## 2.3 Why the Naive Approach Is Too Slow

Naive:

```text
for each query Y:

    answer = 0

    for each segment [L,R], X:

        if L <= Y <= R:
            answer = max(answer, X)
```

Cost:

```text
O(N * Q)
```

If:

```text
N ≈ 1e5
Q ≈ 1e5
```

then:

```text
N * Q
≈
1e10
```

Too large.

We need to avoid rescanning all segments.

---

## 2.4 Core Observation

When sweeping from left to right:

```text
at L:
segment becomes active

at R:
segment stops being active
```

At query coordinate:

```text
Y
```

we only need segments active at that exact moment.

Therefore:

```text
maintain all active X values
```

Then answer:

```text
maximum active X
```

The problem becomes:

```text
START → insert X
QUERY → maximum X
END   → erase X
```

---

## 2.5 Event Construction

For each segment:

```text
[L,R], X
```

create:

```text
(L, START, X)
(R, END,   X)
```

For query `q`:

```text
Yq
```

create:

```text
(Yq, QUERY, q)
```

Use:

```text
START = 0
QUERY = 1
END   = 2
```

### Example

Segment:

```text
[2,7], X=5
```

becomes:

```text
(2,0,5)
(7,2,5)
```

Query:

```text
Y=6
queryIndex=1
```

becomes:

```text
(6,1,1)
```

---

## 2.6 Why START → QUERY → END

We need:

```text
L <= Y <= R
```

So at `Y=L`:

```text
START must happen before QUERY
```

At `Y=R`:

```text
QUERY must happen before END
```

Therefore:

```text
0 = START
1 = QUERY
2 = END
```

### Boundary Dry Run

Segment:

```text
[3,7], X=10
```

Queries:

```text
Y=3
Y=7
```

Sorted events:

```text
(3, START, 10)
(3, QUERY, q0)

(7, QUERY, q1)
(7, END,   10)
```

At `3`:

```text
insert 10
query sees 10
```

At `7`:

```text
query sees 10
then erase 10
```

Both boundaries are included.

---

## 2.7 Multiset Invariant

After all events before the current query have been processed:

```text
activeValues
```

contains exactly the `X` values for segments satisfying:

```text
L <= currentCoordinate <= R
```

provided the tie order is:

```text
START < QUERY < END
```

### Inline Example

Suppose current coordinate is:

```text
Y=6
```

and active segments have:

```text
X = 5,8,4
```

Then:

```text
activeValues =
{4,5,8}
```

Maximum:

```text
*activeValues.rbegin()
=
8
```

No need to know which inactive segments exist.

---

## 2.8 Detailed Sweep Dry Run

Use the earlier example.

Segments:

```text
A: [2,7],  X=5
B: [4,8],  X=8
C: [6,10], X=4
```

Queries:

```text
q0: Y=3
q1: Y=6
q2: Y=8
q3: Y=11
```

Events:

```text
A:
(2, START, 5)
(7, END,   5)

B:
(4, START, 8)
(8, END,   8)

C:
(6, START, 4)
(10,END,   4)

Queries:
(3, QUERY, q0)
(6, QUERY, q1)
(8, QUERY, q2)
(11,QUERY, q3)
```

Sorted order:

```text
2   START 5
3   QUERY q0
4   START 8
6   START 4
6   QUERY q1
7   END   5
8   QUERY q2
8   END   8
10  END   4
11  QUERY q3
```

Notice the important ties:

```text
at 6:
START 4 before QUERY

at 8:
QUERY before END 8
```

Now sweep.

### `x = 2`

```text
START 5
```

Active:

```text
{5}
```

---

### `x = 3`

```text
QUERY q0
```

Active:

```text
{5}
```

Answer:

```text
5
```

---

### `x = 4`

```text
START 8
```

Active:

```text
{5,8}
```

---

### `x = 6`

First:

```text
START 4
```

Active:

```text
{4,5,8}
```

Then:

```text
QUERY q1
```

Maximum:

```text
8
```

---

### `x = 7`

```text
END 5
```

Erase one `5`.

Active:

```text
{4,8}
```

---

### `x = 8`

First:

```text
QUERY q2
```

Because query happens before end:

```text
active = {4,8}
```

Answer:

```text
8
```

Then:

```text
END 8
```

Active:

```text
{4}
```

---

### `x = 10`

```text
END 4
```

Active:

```text
{}
```

---

### `x = 11`

```text
QUERY q3
```

No active segment.

Answer:

```text
0
```

Final answers in original query order:

```text
q0 = 5
q1 = 8
q2 = 8
q3 = 0
```

### Sweep Visualization

```text
coordinate:

2       3       4       6       7       8       10      11
|-------|-------|-------|-------|-------|-------|-------|

S5      Q       S8      S4,Q    E5      Q,E8    E4      Q

active values:

{5}
        {5}
                {5,8}
                        {4,5,8}
                                {4,8}
                                        {4,8} → {4}
                                                {}
                                                        {}
```

---

## 2.9 Algorithm

```text
events = []

for each segment:

    add:
        (L, START, X)

    add:
        (R, END, X)

for each query:

    add:
        (Y, QUERY, queryIndex)

sort events by:

    coordinate
    then event type

activeValues = multiset

for each event:

    if START:
        insert X

    else if END:
        erase one X

    else QUERY:

        if activeValues empty:
            answer[queryIndex] = 0

        else:
            answer[queryIndex] =
                largest active value

print answers
in original query order
```

---

## 2.10 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Event {
    long long coordinate;
    int type;
    long long payload;
};

bool eventOrder(
    const Event& firstEvent,
    const Event& secondEvent
) {
    if (firstEvent.coordinate != secondEvent.coordinate)
        return firstEvent.coordinate < secondEvent.coordinate;

    return firstEvent.type < secondEvent.type;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    const int START = 0;
    const int QUERY = 1;
    const int END   = 2;

    int numberOfSegments;
    cin >> numberOfSegments;

    vector<Event> events;

    for (int segmentIndex = 0;
         segmentIndex < numberOfSegments;
         ++segmentIndex) {

        long long leftEndpoint;
        long long rightEndpoint;
        long long segmentValue;

        cin >> leftEndpoint
            >> rightEndpoint
            >> segmentValue;

        events.push_back({
            leftEndpoint,
            START,
            segmentValue
        });

        events.push_back({
            rightEndpoint,
            END,
            segmentValue
        });
    }

    int numberOfQueries;
    cin >> numberOfQueries;

    vector<long long> answers(numberOfQueries);

    for (int queryIndex = 0;
         queryIndex < numberOfQueries;
         ++queryIndex) {

        long long queryCoordinate;
        cin >> queryCoordinate;

        events.push_back({
            queryCoordinate,
            QUERY,
            queryIndex
        });
    }

    sort(
        events.begin(),
        events.end(),
        eventOrder
    );

    multiset<long long> activeValues;

    for (const Event& currentEvent : events) {

        if (currentEvent.type == START) {
            activeValues.insert(
                currentEvent.payload
            );
        }

        else if (currentEvent.type == END) {
            auto position =
                activeValues.find(
                    currentEvent.payload
                );

            if (position != activeValues.end())
                activeValues.erase(position);
        }

        else {
            int queryIndex =
                currentEvent.payload;

            if (activeValues.empty()) {
                answers[queryIndex] = 0;
            }
            else {
                answers[queryIndex] =
                    *activeValues.rbegin();
            }
        }
    }

    for (long long answer : answers)
        cout << answer << '\n';

    return 0;
}
```

### Why the Code Matches the Lecture

The lecture code uses the same three-way logic:

```text
type 0
→ insert value

type 2
→ erase value

otherwise type 1
→ answer query with *multiset.rbegin()
```

The rewritten version only replaces short pair access such as:

```text
.F
.S.F
.S.S
```

with descriptive names.

---

## 2.11 Complexity

Events:

```text
2N + Q
```

Sorting:

```text
O((N+Q) log(N+Q))
```

Each segment causes:

```text
one insertion
one deletion
```

Each costs:

```text
O(log N)
```

Each maximum query:

```text
O(1)
```

after locating the largest multiset element through `rbegin()`.

Overall:

```text
O((N+Q) log(N+Q))
```

up to the multiset update terms of the same logarithmic order.

Space:

```text
O(N+Q)
```

---

## 2.12 Recognition Model

If you see:

```text
N weighted intervals [L,R] with value X

Q point queries Y

for each Y:
find maximum X among intervals containing Y
```

recognize:

```text
offline sweep line
+
multiset
```

Mental trigger:

```text
covering point Y
→ active at Y

need maximum X
→ ordered active structure

queries known beforehand
→ put queries into event list
```

---

# 3. Problem 2 — Length of Union and Intersection of N Segments

## 3.1 What It Asks

The second lecture idea starts with:

```text
N intervals
[Li,Ri]
```

and asks about two regions:

```text
1. union of all intervals
2. intersection of all intervals
```

The sweep-line implementation computes:

```text
unionLength
intersectionLength
```

using one running active count.

---

## 3.2 Direct Meaning of Union and Intersection

### Union

Covered by at least one interval.

```text
activeCount > 0
```

### Intersection

Covered by all `N` intervals.

```text
activeCount == N
```

Visually:

```text
Interval A:   [-------------]
Interval B:       [-------------]
Interval C:          [------]

Union:        [------------------]

Intersection:          [----]
```

---

## 3.3 Small Dry Run First

Use two overlapping intervals:

```text
A: [2,6)
B: [3,10)
```

ASCII:

```text
2   3           6               10
|---|-----------|---------------|

A:  [===========)
B:      [=======================)
```

### Union

Covered by at least one:

```text
[2,10)
```

Length:

```text
10 - 2
=
8
```

### Intersection

Covered by both:

```text
[3,6)
```

Length:

```text
6 - 3
=
3
```

Now add a disconnected interval:

```text
C: [12,14)
```

Then:

```text
union:
[2,10) ∪ [12,14)

union length:
8 + 2 = 10
```

But intersection of all three:

```text
empty
```

because no point belongs to:

```text
A, B, and C
```

simultaneously.

This matches the event sequence illustrated in the lecture:

```text
2:+1
3:+1
6:-1
10:-1
12:+1
14:-1
```

---

## 3.4 Core Observation

Between two consecutive event coordinates:

```text
activeCount
```

does not change.

Suppose:

```text
currentX = 3
nextX    = 6
```

and after the event at `3`:

```text
activeCount = 2
```

Then throughout:

```text
[3,6)
```

exactly two intervals are active.

Gap length:

```text
6 - 3
=
3
```

Therefore:

```text
if activeCount > 0:
    gap belongs to union

if activeCount == N:
    gap belongs to intersection of all N
```

---

## 3.5 Deriving the Union Condition

Union means:

```text
covered by at least one interval
```

Translate directly:

```text
number of active intervals >= 1
```

Equivalent:

```text
activeCount > 0
```

So for every gap:

```text
[currentX,nextX)
```

if:

```text
cnt > 0
```

then:

```text
unionLength += nextX - currentX
```

### Inline Example

At:

```text
x=6
```

suppose one interval remains active:

```text
cnt = 1
```

Next event:

```text
x=10
```

Then:

```text
[6,10)
```

is part of the union.

Contribution:

```text
10 - 6
=
4
```

---

## 3.6 Deriving the Intersection Condition

Intersection of all `N` intervals means:

```text
every interval covers the point
```

At a sweep position, if:

```text
cnt = N
```

then all intervals are simultaneously active.

Therefore:

```text
if activeCount == N:
    intersectionLength +=
        nextX - currentX
```

### Inline Example

Let:

```text
N = 2
```

At:

```text
x=3
```

count becomes:

```text
2
```

Next event:

```text
x=6
```

So:

```text
[3,6)
```

belongs to both intervals.

Contribution:

```text
6 - 3
=
3
```

---

## 3.7 Event Construction

For each interval:

```text
[L,R)
```

create:

```text
(L,+1)
(R,-1)
```

Example:

```text
[2,6)
[3,10)
[12,14)
```

Events:

```text
(2,+1)
(6,-1)

(3,+1)
(10,-1)

(12,+1)
(14,-1)
```

Sorted:

```text
(2,+1)
(3,+1)
(6,-1)
(10,-1)
(12,+1)
(14,-1)
```

ASCII:

```text
x:       2    3    6    10   12   14
event:  +1   +1   -1    -1   +1   -1
```

---

## 3.8 Detailed Sweep Dry Run

Use:

```text
N = 3

A = [2,6)
B = [3,10)
C = [12,14)
```

Events:

```text
2:+1
3:+1
6:-1
10:-1
12:+1
14:-1
```

Start:

```text
activeCount = 0
unionLength = 0
intersectionLength = 0
```

### Event `x=2`

Apply:

```text
+1
```

Now:

```text
cnt = 1
```

Next event:

```text
3
```

Gap:

```text
[2,3)
```

Since:

```text
cnt > 0
```

union gets:

```text
3 - 2 = 1
```

Since:

```text
cnt != N
1 != 3
```

intersection gets:

```text
0
```

---

### Event `x=3`

Apply:

```text
+1
```

Now:

```text
cnt = 2
```

Next:

```text
6
```

Gap:

```text
[3,6)
```

Union contribution:

```text
6 - 3
=
3
```

Intersection of all 3:

```text
2 != 3
```

so:

```text
0
```

---

### Event `x=6`

Apply:

```text
-1
```

Now:

```text
cnt = 1
```

Next:

```text
10
```

Union contribution:

```text
10 - 6
=
4
```

Intersection:

```text
0
```

---

### Event `x=10`

Apply:

```text
-1
```

Now:

```text
cnt = 0
```

Next:

```text
12
```

No interval is active.

So:

```text
union contribution = 0
intersection contribution = 0
```

---

### Event `x=12`

Apply:

```text
+1
```

Now:

```text
cnt = 1
```

Next:

```text
14
```

Union contribution:

```text
14 - 12
=
2
```

Intersection contribution:

```text
0
```

---

### Totals

Union:

```text
1 + 3 + 4 + 2
=
10
```

Intersection of all 3:

```text
0
```

### Table

| Current event | Count after event | Next x | Gap | Union? | All-3 intersection? |
|---|---:|---:|---:|---:|---:|
| `2:+1` | `1` | `3` | `1` | `+1` | `0` |
| `3:+1` | `2` | `6` | `3` | `+3` | `0` |
| `6:-1` | `1` | `10` | `4` | `+4` | `0` |
| `10:-1` | `0` | `12` | `2` | `0` | `0` |
| `12:+1` | `1` | `14` | `2` | `+2` | `0` |

### Count Visualization

```text
count

2       +-------------+
        |             |
1   +---+             +----------------+-------+
    |                                  |       |
0 --+------------------+---------------+-------+----> x
    2   3              6              10      12  14

union:
    [----------------------------------)
    2                                 10

                                     [------)
                                     12    14
```

No point reaches:

```text
count = 3
```

so all-three intersection is empty.

---

### Second Mini Example — Non-Empty Intersection

Use only:

```text
A = [2,6)
B = [3,10)
```

Now:

```text
N = 2
```

Events:

```text
2:+1
3:+1
6:-1
10:-1
```

Counts:

```text
[2,3)  → 1
[3,6)  → 2
[6,10) → 1
```

Union:

```text
cnt > 0
→ [2,10)

length = 8
```

Intersection:

```text
cnt == N == 2
→ [3,6)

length = 3
```

ASCII:

```text
2   3           6               10
|---|-----------|---------------|

count:
1   2           1

union:
[===============================)

intersection:
    [===========)
```

---

## 3.9 Algorithm

```text
events = []

for each interval [L,R):

    events.push(L,+1)
    events.push(R,-1)

sort events

activeCount = 0

unionLength = 0
intersectionLength = 0

for i from 0 to events.size()-2:

    activeCount += events[i].delta

    gapLength =
        events[i+1].coordinate
        -
        events[i].coordinate

    if activeCount > 0:
        unionLength += gapLength

    if activeCount == numberOfIntervals:
        intersectionLength += gapLength
```

The last event has no next event, so there is no gap after it to process.

---

## 3.10 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfIntervals;
    cin >> numberOfIntervals;

    vector<pair<long long, int>> events;

    for (int intervalIndex = 0;
         intervalIndex < numberOfIntervals;
         ++intervalIndex) {

        long long leftEndpoint;
        long long rightEndpoint;

        cin >> leftEndpoint
            >> rightEndpoint;

        events.push_back({
            leftEndpoint,
            +1
        });

        events.push_back({
            rightEndpoint,
            -1
        });
    }

    sort(events.begin(), events.end());

    long long activeCount = 0;

    long long unionLength = 0;
    long long intersectionLength = 0;

    for (int index = 0;
         index + 1 < (int)events.size();
         ++index) {

        activeCount +=
            events[index].second;

        long long currentCoordinate =
            events[index].first;

        long long nextCoordinate =
            events[index + 1].first;

        long long gapLength =
            nextCoordinate
            -
            currentCoordinate;

        if (activeCount > 0) {
            unionLength += gapLength;
        }

        if (activeCount == numberOfIntervals) {
            intersectionLength += gapLength;
        }
    }

    cout << unionLength
         << ' '
         << intersectionLength
         << '\n';

    return 0;
}
```

### Important Note About Equal Coordinates

The lecture uses sorted:

```text
(coordinate, delta)
```

events.

If an end and start happen at the same coordinate, their order may briefly change `activeCount`.

But:

```text
nextCoordinate - currentCoordinate
=
0
```

between equal coordinates.

So that temporary state contributes zero length.

For pure continuous-length calculations, the answer remains correct.

---

## 3.11 Complexity

There are:

```text
2N events
```

Sorting:

```text
O(N log N)
```

Sweep:

```text
O(N)
```

Total:

```text
O(N log N)
```

Space:

```text
O(N)
```

---

## 3.12 Recognition Model

If the question asks:

```text
total length covered by at least one interval
```

translate:

```text
activeCount > 0
```

If it asks:

```text
total length common to every one of N intervals
```

translate:

```text
activeCount == N
```

Then:

```text
condition true on current gap
→ add nextX - currentX
```

This is the same sweep-line engine with a different condition.

---

# 4. Final Mental Model

The two lecture ideas are really one pattern.

```text
                          INTERVAL PROBLEM
                                |
                                v
                        identify events
                                |
                 +--------------+--------------+
                 |                             |
                 v                             v
             POINT QUERY                   LENGTH QUERY
                 |                             |
                 v                             v
       START / QUERY / END              START +1 / END -1
                 |                             |
                 v                             v
        maintain active X               maintain activeCount
                 |                             |
                 v                   +---------+---------+
       maximum active value           |                   |
                                      v                   v
                                active > 0          active == N
                                      |                   |
                                      v                   v
                                  UNION            INTERSECTION
```

## Event Ordering for Inclusive Point Queries

```text
[L,R]

same coordinate:

START
  |
  v
QUERY
  |
  v
END
```

This guarantees:

```text
L <= Y <= R
```

is respected.

---

## Gap Formula for Length Problems

After processing the event at:

```text
currentX
```

the state stays unchanged until:

```text
nextX
```

Therefore:

```text
gapLength
=
nextX - currentX
```

Then:

```text
if activeCount > 0
→ gap belongs to union

if activeCount == N
→ gap belongs to intersection of all N
```

---

## Contest Recognition

When you see:

```text
large coordinates
+
intervals
+
queries / overlap / coverage
```

do not think:

```text
iterate every coordinate
```

Think:

```text
EVENTS
```

Then ask:

```text
1. Where does state change?
2. What must be active?
3. What is the tie order?
4. Do I answer at an event,
   or accumulate the gap to the next event?
```

That gives the two main forms from this lecture:

```text
FORM A
point query
→ answer from active data structure

FORM B
interval length
→ answer from activeCount over event gaps
```

> **Core lesson:** sweep line reduces a huge coordinate space to a sorted sequence of meaningful changes. The data structure stores only what is active at the current sweep position.
