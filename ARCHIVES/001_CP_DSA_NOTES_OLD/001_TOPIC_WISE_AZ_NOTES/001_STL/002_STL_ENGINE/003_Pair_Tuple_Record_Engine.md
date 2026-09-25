# 003_Pair_Tuple_Record_Engine.md

> MiniSTLEngine Phase 003  
> Topic: `pair` and `tuple` as a **Record Engine** for CP, DSA, sorting, graph edges, heap states, and real-system thinking.

---

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why Pair And Tuple Matter](#2-why-pair-and-tuple-matter)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Pair Basics](#4-pair-basics)
- [5. Tuple Basics](#5-tuple-basics)
- [6. CP/DSA Recognition](#6-cpdsa-recognition)
- [7. Engine Architecture](#7-engine-architecture)
- [8. Basic Record Engine With Pair](#8-basic-record-engine-with-pair)
- [9. Basic Record Engine With Tuple](#9-basic-record-engine-with-tuple)
- [10. CP Pattern 1: Sorting Pairs](#10-cp-pattern-1-sorting-pairs)
- [11. CP Pattern 2: Interval Records](#11-cp-pattern-2-interval-records)
- [12. CP Pattern 3: Graph Edge Records](#12-cp-pattern-3-graph-edge-records)
- [13. CP Pattern 4: Priority Queue State Records](#13-cp-pattern-4-priority-queue-state-records)
- [14. CP Pattern 5: Coordinate Records](#14-cp-pattern-5-coordinate-records)
- [15. Pair vs Tuple vs Struct](#15-pair-vs-tuple-vs-struct)
- [16. Common Mistakes](#16-common-mistakes)
- [17. Complexity Table](#17-complexity-table)
- [18. Real-World Mapping](#18-real-world-mapping)
- [19. Final Mental Model](#19-final-mental-model)
- [20. Next Step](#20-next-step)

---

# 1. Goal

Learn `pair` and `tuple` not only as STL syntax, but as a:

```text
Record Engine
```

They help you store multiple related values together.

Examples:

```text
(value, index)
(start, end)
(node, distance)
(row, col)
(weight, u, v)
(priority, taskId, timestamp)
```

---

# 2. Why Pair And Tuple Matter

Many CP/DSA problems are not just about single values.

They need compound records:

```text
number + original index
interval start + interval end
graph edge u + v + weight
heap state distance + node
grid cell row + col
event time + event type
```

Normal thinking:

```cpp
pair<int,int> p;
```

Engine thinking:

```text
RecordEngine
    groups related fields
    allows sorting by field order
    allows heap ordering
    allows compact state passing
```

---

# 3. Real-System Mental Model

Real systems store records everywhere:

```text
UserRecord(id, age)
OrderRecord(orderId, amount, timestamp)
LogRecord(time, level, message)
TaskRecord(priority, taskId, retryCount)
EdgeRecord(source, destination, latency)
```

Pair/tuple is the CP version of small records.

```text
Raw Values
   |
   v
Record Engine
   |
   +--> sort records
   +--> compare records
   +--> push into heap
   +--> store graph edges
   +--> process events
```

---

# 4. Pair Basics

A `pair` stores two values.

```cpp
pair<int, int> p = {10, 20};

cout << p.first << endl;   // 10
cout << p.second << endl;  // 20
```

Example meanings:

```text
pair<int,int> = {value, index}
pair<int,int> = {start, end}
pair<int,int> = {row, col}
pair<int,int> = {distance, node}
```

---

# 5. Tuple Basics

A `tuple` stores more than two values.

```cpp
tuple<int, int, int> edge = {5, 1, 2};

int weight, u, v;
tie(weight, u, v) = edge;
```

Alternative C++17 style:

```cpp
auto [w, a, b] = edge;
```

Example meanings:

```text
tuple<int,int,int> = {weight, u, v}
tuple<int,int,int> = {distance, row, col}
tuple<int,int,int> = {time, type, id}
```

---

# 6. CP/DSA Recognition

Use `pair`/`tuple` when problem needs:

```text
value with index
sort by two fields
intervals
coordinates
graph edges
heap states
events
weighted graph
grid BFS state
range queries
```

Hidden mapping:

| Problem clue | Record type |
|---|---|
| original index needed | `{value, index}` |
| intervals | `{start, end}` |
| weighted edge | `{weight, u, v}` |
| Dijkstra | `{distance, node}` |
| grid BFS | `{row, col}` or `{dist, row, col}` |
| sweep line | `{time, type}` |
| top-K with frequency | `{frequency, value}` |

---

# 7. Engine Architecture

```text
MiniRecordEngine
├── pair record
├── tuple record
├── sorting by fields
├── interval record processor
├── graph edge record processor
├── heap state record processor
└── event record processor
```

---

# 8. Basic Record Engine With Pair

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class PairRecordEngine {
private:
    vector<pair<int, int>> records;

public:
    void addRecord(int firstValue, int secondValue) {
        // WHY:
        // pair groups two related values together.
        // Example: {value, index}, {start, end}, {row, col}.
        records.push_back({firstValue, secondValue});
    }

    void sortRecords() {
        // Default pair sorting:
        // first field ascending, if tie then second field ascending.
        sort(records.begin(), records.end());
    }

    void printRecords() {
        for (auto [a, b] : records) {
            cout << "(" << a << ", " << b << ") ";
        }
        cout << endl;
    }
};

int main() {
    PairRecordEngine engine;

    engine.addRecord(5, 2);
    engine.addRecord(1, 9);
    engine.addRecord(5, 1);

    cout << "Before sort:\n";
    engine.printRecords();

    engine.sortRecords();

    cout << "After sort:\n";
    engine.printRecords();

    return 0;
}
```

## Output

```text
Before sort:
(5, 2) (1, 9) (5, 1)

After sort:
(1, 9) (5, 1) (5, 2)
```

---

# 9. Basic Record Engine With Tuple

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class TupleRecordEngine {
private:
    vector<tuple<int, int, int>> records;

public:
    void addRecord(int a, int b, int c) {
        // WHY:
        // tuple is useful when state has 3+ fields.
        // Example: {weight, u, v} or {distance, row, col}.
        records.push_back({a, b, c});
    }

    void sortRecords() {
        // Default tuple sorting:
        // first field, then second field, then third field.
        sort(records.begin(), records.end());
    }

    void printRecords() {
        for (auto [a, b, c] : records) {
            cout << "(" << a << ", " << b << ", " << c << ") ";
        }
        cout << endl;
    }
};

int main() {
    TupleRecordEngine engine;

    engine.addRecord(10, 2, 3);
    engine.addRecord(5, 9, 1);
    engine.addRecord(10, 1, 7);

    cout << "Before sort:\n";
    engine.printRecords();

    engine.sortRecords();

    cout << "After sort:\n";
    engine.printRecords();

    return 0;
}
```

---

# 10. CP Pattern 1: Sorting Pairs

## Problem Type

```text
Sort values but keep original index.
```

## Example

```text
nums = [40, 10, 30]
records = [(40,0), (10,1), (30,2)]
after sort = [(10,1), (30,2), (40,0)]
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> nums = {40, 10, 30};

    vector<pair<int, int>> records;

    for (int i = 0; i < (int)nums.size(); i++) {
        // Store value with original index.
        // Useful when sorting changes order but answer needs old position.
        records.push_back({nums[i], i});
    }

    sort(records.begin(), records.end());

    for (auto [value, index] : records) {
        cout << "value=" << value
             << ", originalIndex=" << index << endl;
    }

    return 0;
}
```

## Pattern Recognition

Use when problem says:

```text
sort but keep original position
restore answer order
rank elements
minimum difference with index
```

---

# 11. CP Pattern 2: Interval Records

## Problem Type

```text
Merge intervals
check overlap
sort meetings
schedule rooms
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<pair<int, int>> mergeIntervals(vector<pair<int, int>>& intervals) {
    if (intervals.empty()) {
        return {};
    }

    // Sort by start time, then end time.
    sort(intervals.begin(), intervals.end());

    vector<pair<int, int>> merged;

    for (auto [start, end] : intervals) {
        if (merged.empty() || start > merged.back().second) {
            // No overlap, create a new merged interval.
            merged.push_back({start, end});
        } else {
            // Overlap exists.
            // Extend the last interval's end if needed.
            merged.back().second = max(merged.back().second, end);
        }
    }

    return merged;
}

int main() {
    vector<pair<int, int>> intervals = {
        {1, 3}, {2, 6}, {8, 10}, {9, 12}
    };

    vector<pair<int, int>> merged = mergeIntervals(intervals);

    for (auto [l, r] : merged) {
        cout << "[" << l << ", " << r << "] ";
    }

    return 0;
}
```

## Dry Run

```text
input:
[1,3], [2,6], [8,10], [9,12]

after sort:
[1,3], [2,6], [8,10], [9,12]

process [1,3]  -> merged = [1,3]
process [2,6]  -> overlap, merged = [1,6]
process [8,10] -> no overlap, add [8,10]
process [9,12] -> overlap, merged = [8,12]

answer:
[1,6], [8,12]
```

---

# 12. CP Pattern 3: Graph Edge Records

## Problem Type

```text
Store weighted graph edges.
```

Useful for:

```text
Kruskal MST
Bellman-Ford
edge sorting
network cost problems
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<tuple<int, int, int>> edges;

    // Store as {weight, u, v}
    // WHY:
    // If we sort edges, they sort by weight automatically.
    edges.push_back({10, 1, 2});
    edges.push_back({5, 2, 3});
    edges.push_back({7, 1, 3});

    sort(edges.begin(), edges.end());

    for (auto [weight, u, v] : edges) {
        cout << "edge "
             << u << " -> " << v
             << " weight=" << weight << endl;
    }

    return 0;
}
```

## Output

```text
edge 2 -> 3 weight=5
edge 1 -> 3 weight=7
edge 1 -> 2 weight=10
```

---

# 13. CP Pattern 4: Priority Queue State Records

## Problem Type

```text
Dijkstra shortest path
task scheduler
top-K with custom ranking
grid BFS with cost
```

## C++ Code: Dijkstra State

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n = 4;

    vector<vector<pair<int, int>>> graph(n + 1);

    auto addEdge = [&](int u, int v, int w) {
        graph[u].push_back({v, w});
        graph[v].push_back({u, w});
    };

    addEdge(1, 2, 5);
    addEdge(1, 3, 2);
    addEdge(3, 4, 1);

    vector<int> dist(n + 1, INT_MAX);

    // Min heap of {distance, node}
    // WHY:
    // Dijkstra repeatedly needs the currently closest unprocessed node.
    priority_queue<
        pair<int, int>,
        vector<pair<int, int>>,
        greater<pair<int, int>>
    > pq;

    dist[1] = 0;
    pq.push({0, 1});

    while (!pq.empty()) {
        auto [currentDist, node] = pq.top();
        pq.pop();

        // Stale state check:
        // If this heap record is older than the best known distance, skip it.
        if (currentDist != dist[node]) {
            continue;
        }

        for (auto [neighbor, weight] : graph[node]) {
            if (dist[neighbor] > currentDist + weight) {
                dist[neighbor] = currentDist + weight;
                pq.push({dist[neighbor], neighbor});
            }
        }
    }

    for (int node = 1; node <= n; node++) {
        cout << "dist[1 -> " << node << "] = " << dist[node] << endl;
    }

    return 0;
}
```

## Key Insight

```text
pair<int,int> = {distance, node}
```

This turns heap into:

```text
ShortestPathCandidateEngine
```

---

# 14. CP Pattern 5: Coordinate Records

## Problem Type

```text
grid BFS
matrix traversal
island problems
maze shortest path
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<vector<int>> grid = {
        {1, 1, 0},
        {0, 1, 1},
        {0, 0, 1}
    };

    int rows = grid.size();
    int cols = grid[0].size();

    queue<pair<int, int>> q;

    vector<vector<int>> visited(rows, vector<int>(cols, 0));

    // pair<int,int> represents {row, col}.
    q.push({0, 0});
    visited[0][0] = 1;

    vector<pair<int, int>> directions = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    while (!q.empty()) {
        auto [r, c] = q.front();
        q.pop();

        cout << "visit (" << r << ", " << c << ")\n";

        for (auto [dr, dc] : directions) {
            int nr = r + dr;
            int nc = c + dc;

            bool inside =
                nr >= 0 && nr < rows &&
                nc >= 0 && nc < cols;

            if (inside && !visited[nr][nc] && grid[nr][nc] == 1) {
                visited[nr][nc] = 1;
                q.push({nr, nc});
            }
        }
    }

    return 0;
}
```

## Mental Model

```text
pair<int,int> = coordinate record
directions vector = movement engine
queue = BFS frontier
```

---

# 15. Pair vs Tuple vs Struct

## Use Pair When

```text
only two fields
meaning is obvious
short CP code
```

Example:

```cpp
pair<int,int> p = {distance, node};
```

## Use Tuple When

```text
three or more fields
still short CP code
```

Example:

```cpp
tuple<int,int,int> state = {cost, row, col};
```

## Use Struct When

```text
many fields
field names improve readability
production-style code
custom comparison needed
```

Example:

```cpp
struct Task {
    int priority;
    int taskId;
    int retryCount;
};
```

---

# 16. Common Mistakes

## Mistake 1: Forgetting Default Sort Order

Pairs sort like:

```text
first ascending
then second ascending
```

Tuples sort like:

```text
field 1
then field 2
then field 3
...
```

---

## Mistake 2: Storing Pair In Wrong Order

For Dijkstra min heap:

```cpp
{distance, node}
```

not:

```cpp
{node, distance}
```

Because heap should sort by distance.

---

## Mistake 3: Confusing Interval Fields

For intervals:

```cpp
{start, end}
```

Keep consistent everywhere.

---

## Mistake 4: Overusing Tuple

This becomes hard to read:

```cpp
tuple<int,int,int,int,int> x;
```

Better use:

```cpp
struct Record;
```

---

# 17. Complexity Table

| Operation | Complexity |
|---|---:|
| access pair fields | O(1) |
| access tuple fields | O(1) |
| sort vector of pairs | O(N log N) |
| sort vector of tuples | O(N log N) |
| push pair into heap | O(log N) |
| compare pair/tuple | O(number of fields checked) |

---

# 18. Real-World Mapping

| CP Record | Real-System Meaning |
|---|---|
| `{value, index}` | ranked item with original position |
| `{start, end}` | calendar/event interval |
| `{distance, node}` | routing candidate |
| `{weight, u, v}` | network edge |
| `{row, col}` | grid/geospatial cell |
| `{time, type}` | event stream record |
| `{priority, taskId}` | scheduler task |

---

# 19. Final Mental Model

`pair` and `tuple` are:

```text
lightweight record containers
```

They are best for:

```text
sorting compound values
heap states
graph edges
intervals
coordinates
events
```

One-line CP rule:

```text
When one value is not enough, create a record with pair or tuple.
```

One-line system rule:

```text
Every engine processes records, not isolated values.
```

---

# 20. Next Step

Next file:

```text
004_Stack_Parser_Engine.md
```

Then:

```text
005_Queue_Event_Buffer_Engine.md
006_Deque_Sliding_Window_Engine.md
007_PriorityQueue_Scheduler_Engine.md
```
