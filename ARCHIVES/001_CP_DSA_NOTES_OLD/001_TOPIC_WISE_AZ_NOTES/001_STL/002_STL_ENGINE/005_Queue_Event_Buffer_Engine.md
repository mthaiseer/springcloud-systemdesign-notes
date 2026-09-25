# 005_Queue_Event_Buffer_Engine.md

> MiniSTLEngine Phase 005  
> Topic: `queue` as an **Event Buffer / FIFO Processing Engine** for CP, DSA, BFS, scheduling, and real-system thinking.

---

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Queue Is An Engine](#2-why-queue-is-an-engine)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Queue Core Behavior](#4-queue-core-behavior)
- [5. Queue Operations Cheat Sheet](#5-queue-operations-cheat-sheet)
- [6. CP/DSA Recognition](#6-cpdsa-recognition)
- [7. Engine Architecture](#7-engine-architecture)
- [8. Basic Event Buffer Engine](#8-basic-event-buffer-engine)
- [9. Dry Run: FIFO Processing](#9-dry-run-fifo-processing)
- [10. CP Pattern 1: BFS Traversal](#10-cp-pattern-1-bfs-traversal)
- [11. CP Pattern 2: Multi-Source BFS](#11-cp-pattern-2-multi-source-bfs)
- [12. CP Pattern 3: Rotten Oranges](#12-cp-pattern-3-rotten-oranges)
- [13. CP Pattern 4: Task Processing Simulation](#13-cp-pattern-4-task-processing-simulation)
- [14. CP Pattern 5: Sliding Stream Processing](#14-cp-pattern-5-sliding-stream-processing)
- [15. Queue vs Stack](#15-queue-vs-stack)
- [16. Common Mistakes](#16-common-mistakes)
- [17. Complexity Table](#17-complexity-table)
- [18. Real-World Mapping](#18-real-world-mapping)
- [19. Final Mental Model](#19-final-mental-model)
- [20. Next Step](#20-next-step)

---

# 1. Goal

Learn `queue` not only as FIFO syntax, but as a:

```text
Event Buffer / Processing Engine
```

It helps solve:

```text
BFS traversal
task scheduling
stream/event processing
simulation problems
level-order traversal
multi-source propagation
request buffering
producer-consumer systems
```

---

# 2. Why Queue Is An Engine

Queue follows:

```text
First In, First Out
```

Meaning:

```text
oldest waiting item gets processed first
```

Perfect for:

```text
fair processing
time-order processing
level-by-level traversal
event streams
request buffering
```

Normal thinking:

```cpp
queue<int> q;
```

Engine thinking:

```text
QueueEventBufferEngine
    stores waiting work
    processes oldest item first
    supports fair ordering
    supports stream processing
```

---

# 3. Real-System Mental Model

Queues appear everywhere:

```text
Kafka consumer queue
RabbitMQ
task scheduler
printer queue
CPU process queue
request buffering
network packet processing
BFS traversal engine
```

Architecture:

```text
Incoming Events
      |
      v
QueueEventBufferEngine
      |
      +--> enqueue new event
      +--> dequeue oldest event
      +--> process event
      |
      v
Processed Result
```

---

# 4. Queue Core Behavior

Operations:

```text
push 10
push 20
push 30
```

Queue:

```text
front -> 10 20 30 <- back
```

Pop order:

```text
10 -> 20 -> 30
```

Key idea:

```text
earliest waiting item gets processed first
```

---

# 5. Queue Operations Cheat Sheet

```cpp
queue<int> q;

q.push(10);      // insert at back
q.front();       // read front
q.back();        // read last
q.pop();         // remove front
q.empty();       // check empty
q.size();        // number of elements
```

Important:

```cpp
q.pop();
```

does not return value.

Correct:

```cpp
int x = q.front();
q.pop();
```

---

# 6. CP/DSA Recognition

Use queue when problem says:

```text
level order
minimum steps
shortest path in unweighted graph
spread/propagation
simulation by time
process in arrival order
BFS
flood fill
rotting/spreading
```

Hidden mapping:

| Problem clue | Queue pattern |
|---|---|
| shortest path unweighted | BFS |
| level-by-level traversal | queue |
| spreading infection/fire | multi-source BFS |
| task arrival order | FIFO queue |
| simulation over time | event queue |
| process nearest first in graph | BFS queue |

---

# 7. Engine Architecture

```text
MiniQueueEventBufferEngine
├── enqueue API
├── dequeue API
├── front inspection
├── BFS traversal engine
├── propagation engine
├── task simulation engine
└── stream processing engine
```

---

# 8. Basic Event Buffer Engine

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class QueueEventBufferEngine {
private:
    queue<string> events;

public:

    void enqueueEvent(const string& eventName) {

        // WHY:
        // New event arrives and waits for processing.
        // Queue preserves arrival order.
        events.push(eventName);
    }

    void processNextEvent() {

        if (events.empty()) {
            cout << "No pending events\n";
            return;
        }

        // FIFO:
        // Oldest event gets processed first.
        string current = events.front();

        events.pop();

        cout << "Processing: "
             << current
             << endl;
    }

    string nextEvent() {

        if (events.empty()) {
            return "EMPTY";
        }

        return events.front();
    }

    int pendingEvents() {
        return events.size();
    }
};

int main() {

    QueueEventBufferEngine engine;

    engine.enqueueEvent("LOGIN_REQUEST");
    engine.enqueueEvent("PAYMENT_EVENT");
    engine.enqueueEvent("EMAIL_NOTIFICATION");

    cout << "Next event: "
         << engine.nextEvent()
         << endl;

    engine.processNextEvent();
    engine.processNextEvent();

    cout << "Pending events: "
         << engine.pendingEvents()
         << endl;

    return 0;
}
```

---

# 9. Dry Run: FIFO Processing

Operations:

```text
enqueue LOGIN
enqueue PAYMENT
enqueue EMAIL
```

Queue:

```text
front -> LOGIN PAYMENT EMAIL <- back
```

Process:

```text
process LOGIN
```

Queue:

```text
front -> PAYMENT EMAIL <- back
```

Then:

```text
process PAYMENT
```

Queue:

```text
front -> EMAIL <- back
```

Key idea:

```text
oldest waiting event processed first
```

---

# 10. CP Pattern 1: BFS Traversal

## Problem Type

```text
Traverse graph level by level.
Find shortest path in unweighted graph.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    int n = 5;

    vector<vector<int>> graph(n + 1);

    auto addEdge = [&](int u, int v) {
        graph[u].push_back(v);
        graph[v].push_back(u);
    };

    addEdge(1, 2);
    addEdge(1, 3);
    addEdge(2, 4);
    addEdge(3, 5);

    vector<int> visited(n + 1, 0);

    queue<int> q;

    // Start BFS from node 1.
    q.push(1);
    visited[1] = 1;

    while (!q.empty()) {

        int node = q.front();
        q.pop();

        cout << "Visit: "
             << node
             << endl;

        for (int neighbor : graph[node]) {

            if (!visited[neighbor]) {

                visited[neighbor] = 1;

                // Newly discovered node waits for future processing.
                q.push(neighbor);
            }
        }
    }

    return 0;
}
```

---

# 11. CP Pattern 2: Multi-Source BFS

## Problem Type

```text
Spread from multiple starting points simultaneously.
```

Examples:

```text
fire spread
virus spread
nearest zero
walls and gates
multi-source shortest distance
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    vector<vector<int>> grid = {
        {1, 0, 1},
        {0, 0, 0},
        {1, 0, 1}
    };

    int rows = grid.size();
    int cols = grid[0].size();

    queue<pair<int,int>> q;

    vector<vector<int>> dist(
        rows,
        vector<int>(cols, -1)
    );

    // All source cells enter queue first.
    // BFS expands from all sources together.
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {

            if (grid[r][c] == 1) {

                q.push({r, c});

                dist[r][c] = 0;
            }
        }
    }

    vector<pair<int,int>> dirs = {
        {1,0}, {-1,0}, {0,1}, {0,-1}
    };

    while (!q.empty()) {

        auto [r, c] = q.front();
        q.pop();

        for (auto [dr, dc] : dirs) {

            int nr = r + dr;
            int nc = c + dc;

            bool inside =
                nr >= 0 && nr < rows &&
                nc >= 0 && nc < cols;

            if (inside && dist[nr][nc] == -1) {

                dist[nr][nc] =
                    dist[r][c] + 1;

                q.push({nr, nc});
            }
        }
    }

    cout << "Distance matrix:\n";

    for (auto& row : dist) {
        for (int x : row) {
            cout << x << " ";
        }
        cout << endl;
    }

    return 0;
}
```

## Key Insight

```text
All starting points enter queue first.
BFS expands wave by wave.
```

---

# 12. CP Pattern 3: Rotten Oranges

## Problem Type

```text
Propagation over time.
```

Classic BFS timing problem.

## Mental Model

```text
Queue stores currently active spreaders.
Each BFS level = one time unit.
```

Examples:

```text
virus spread
infection simulation
network propagation
broadcast systems
```

---

# 13. CP Pattern 4: Task Processing Simulation

## Problem Type

```text
Tasks arrive and get processed in order.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    queue<string> tasks;

    tasks.push("UPLOAD_VIDEO");
    tasks.push("SEND_EMAIL");
    tasks.push("GENERATE_REPORT");

    while (!tasks.empty()) {

        string current = tasks.front();

        tasks.pop();

        cout << "Executing: "
             << current
             << endl;
    }

    return 0;
}
```

## Real-System Mapping

This models:

```text
worker queue
job processing
microservice event processing
consumer group
```

---

# 14. CP Pattern 5: Sliding Stream Processing

## Problem Type

```text
Process incoming stream in order.
```

Example:

```text
network packets
log stream
sensor events
```

Queue acts like:

```text
temporary waiting buffer
```

before processing.

---

# 15. Queue vs Stack

| Feature | Queue | Stack |
|---|---|---|
| Order | FIFO | LIFO |
| Processing | oldest first | newest first |
| Common use | BFS/events | parsing/undo |
| Real system | request queue | call stack |
| Traversal | BFS | DFS |

Mental shortcut:

```text
Need fair arrival order?
→ queue

Need latest unresolved item?
→ stack
```

---

# 16. Common Mistakes

## Mistake 1: Calling front On Empty Queue

Wrong:

```cpp
cout << q.front();
```

Correct:

```cpp
if (!q.empty()) {
    cout << q.front();
}
```

---

## Mistake 2: Thinking pop Returns Value

Wrong:

```cpp
int x = q.pop();
```

Correct:

```cpp
int x = q.front();
q.pop();
```

---

## Mistake 3: Using Queue Instead Of Priority Queue

Normal queue:

```text
FIFO
```

Priority queue:

```text
highest priority first
```

Dijkstra needs:

```text
priority_queue
```

not normal queue.

---

## Mistake 4: Forgetting Visited Check In BFS

Wrong BFS can repeatedly push same node.

Always:

```cpp
if (!visited[neighbor]) {
}
```

---

# 17. Complexity Table

| Operation | Complexity |
|---|---:|
| push | O(1) |
| pop | O(1) |
| front | O(1) |
| back | O(1) |
| empty | O(1) |
| BFS traversal | O(V + E) |
| queue space | O(N) |

---

# 18. Real-World Mapping

| Queue Concept | Real-System Meaning |
|---|---|
| enqueue event | incoming request/task |
| dequeue event | processing request |
| FIFO order | fair scheduling |
| BFS wave | propagation/broadcast |
| multi-source BFS | simultaneous spread |
| queue buffer | Kafka/RabbitMQ-like buffer |
| task queue | worker system |
| stream processing | event pipeline |

---

# 19. Final Mental Model

Queue is:

```text
fair event processing engine
```

Best for:

```text
BFS
task scheduling
event buffering
arrival-order processing
propagation systems
simulation problems
```

One-line CP rule:

```text
If problem expands level by level or shortest path in unweighted graph, think queue/BFS.
```

One-line system rule:

```text
Queues decouple producers from consumers and preserve processing order.
```

---

# 20. Next Step

Next file:

```text
006_Deque_Sliding_Window_Engine.md
```

Then:

```text
007_PriorityQueue_Scheduler_Engine.md
008_Set_Ordered_Index_Engine.md
009_Map_KeyValue_Index_Engine.md
```
