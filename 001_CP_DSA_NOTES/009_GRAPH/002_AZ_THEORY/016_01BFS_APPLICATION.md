# 0-1 BFS — Application & Implementation, Step by Step

> Self-study competitive-programming note based on the provided AlgoZenith 0-1 BFS application screenshots.
> Focus: prerequisites → SSSP relaxation → why a normal queue is insufficient → deque ordering → visited/finalization → detailed dry run → clean C++17.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Problem Model](#2-problem-model)
3. [Weighted Graph Representation](#3-weighted-graph-representation)
4. [Distance Array](#4-distance-array)
5. [Relaxation](#5-relaxation)
6. [Why Normal BFS Fails](#6-why-normal-bfs-fails)
7. [Key 0-1 Observation](#7-key-0-1-observation)
8. [Why a Deque Works](#8-why-a-deque-works)
9. [Algorithm Step by Step](#9-algorithm-step-by-step)
10. [Detailed Dry Run](#10-detailed-dry-run)
11. [Visited / Finalization Idea](#11-visited--finalization-idea)
12. [Lecture-Style C++17](#12-lecture-style-c17)
13. [Simpler C++17 Without `vis`](#13-simpler-c17-without-vis)
14. [Correctness Intuition](#14-correctness-intuition)
15. [Complexity](#15-complexity)
16. [Common Mistakes](#16-common-mistakes)
17. [Recognition Model](#17-recognition-model)
18. [Final Mental Model](#18-final-mental-model)

---

# 1. Prerequisites

You should know:

- graph basics
- adjacency lists
- weighted edges
- BFS
- `queue`
- `deque`
- shortest path
- distance array
- relaxation
- basic Dijkstra intuition
- visited/finalized node idea

Learning chain:

```text
Unweighted BFS
      ↓
Weighted shortest path
      ↓
Relaxation
      ↓
Dijkstra idea
      ↓
Weights only 0 or 1
      ↓
0-1 BFS
```

## 1.1 Weighted edge

```text
1 --0--> 3
1 --1--> 2
```

The edge value is the movement cost.

```text
1 → 3 costs 0
1 → 2 costs 1
```

## 1.2 SSSP

SSSP = Single Source Shortest Path.

Given a source `S`, find:

```text
dist[S → every node]
```

---

# 2. Problem Model

We have a graph where every edge weight is exactly:

```text
0 or 1
```

Goal:

```text
Find shortest distance from one source to all nodes.
```

Dijkstra would work because all weights are non-negative.

But because the weights are only `0` and `1`, we can replace the priority queue with a deque and get linear complexity.

---

# 3. Weighted Graph Representation

Store:

```text
{neighbor, cost}
```

C++:

```cpp
vector<vector<pair<int, int>>> graph;
```

For an undirected edge:

```cpp
graph[u].push_back({v, cost});
graph[v].push_back({u, cost});
```

For a directed edge, add only the forward edge.

---

# 4. Distance Array

`dist[node]` means:

```text
best distance currently known from source to node
```

Initialize:

```text
dist[source] = 0
dist[others] = INF
```

Example:

```text
source = 1

node:  1  2  3  4
dist:  0  ∞  ∞  ∞
```

---

# 5. Relaxation

Suppose:

```text
node --cost--> next
```

Candidate distance:

```text
dist[node] + cost
```

If:

```text
dist[node] + cost < dist[next]
```

then update:

```text
dist[next] = dist[node] + cost
```

C++:

```cpp
if (dist[next] > dist[node] + cost) {
    dist[next] = dist[node] + cost;
}
```

## Tiny Example

```text
dist[node] = 2
cost = 0
dist[next] = 5
```

Candidate:

```text
2 + 0 = 2
```

Since:

```text
2 < 5
```

update:

```text
dist[next] = 2
```

---

# 6. Why Normal BFS Fails

Normal BFS uses:

```cpp
queue<int> q;
```

and processes in FIFO order.

But FIFO means:

```text
first inserted → first processed
```

It does **not** mean:

```text
smallest distance → first processed
```

Example:

```text
1 --1--> 2
1 --0--> 3
```

If `2` is inserted first:

```text
queue = [2, 3]
```

BFS processes `2` first.

But:

```text
dist[2] = 1
dist[3] = 0
```

So `3` should be processed first for shortest-path ordering.

---

# 7. Key 0-1 Observation

Suppose:

```text
dist[node] = d
```

Every outgoing edge has weight:

```text
0 or 1
```

So every new candidate distance is only:

```text
d
or
d + 1
```

That is the whole reason 0-1 BFS works.

We never suddenly create:

```text
d + 7
d + 100
```

So we only need to maintain two effective distance layers.

---

# 8. Why a Deque Works

A deque supports:

```text
push_front()
push_back()
pop_front()
```

Rule:

```text
cost 0 → push_front
cost 1 → push_back
```

Why?

```text
0-edge → distance stays d
1-edge → distance becomes d+1
```

Visual:

```text
front                                      back
  ↓                                          ↓
[d][d][d][d+1][d+1][d+1]
```

A `0`-cost transition belongs to the current layer.

A `1`-cost transition belongs to the next layer.

---

# 9. Algorithm Step by Step

## Step 1 — Initialize

```text
dist[source] = 0
dist[others] = INF
```

## Step 2 — Create deque

```cpp
deque<int> dq;
dq.push_front(source);
```

## Step 3 — Pop front

```cpp
int node = dq.front();
dq.pop_front();
```

## Step 4 — Scan all edges

For every:

```text
node --cost--> next
```

compute:

```text
newDist = dist[node] + cost
```

## Step 5 — Relax

If:

```text
newDist < dist[next]
```

then:

```text
dist[next] = newDist
```

## Step 6 — Insert by weight

```text
cost = 0 → push_front(next)
cost = 1 → push_back(next)
```

---

# 10. Detailed Dry Run

Use the lecture-style graph:

```text
1 --1-- 2 --1-- 4
 \      /
  0    0
   \  /
    3
```

Edges:

```text
1 ↔ 2, cost 1
1 ↔ 3, cost 0
3 ↔ 2, cost 0
2 ↔ 4, cost 1
```

Source:

```text
1
```

Initial:

```text
dist[1] = 0
dist[2] = INF
dist[3] = INF
dist[4] = INF

deque = [1]
```

### Iteration 1

Pop `1`.

Process `1 → 2` with cost `1`:

```text
0 + 1 = 1
dist[2] = 1
push_back(2)
```

Now:

```text
deque = [2]
```

Process `1 → 3` with cost `0`:

```text
0 + 0 = 0
dist[3] = 0
push_front(3)
```

Now:

```text
deque = [3, 2]
```

Key observation:

```text
3 was discovered later,
but it moves ahead of 2
because dist[3] = 0 < dist[2] = 1
```

### Iteration 2

Pop `3`.

Process `3 → 2` with cost `0`:

```text
candidate = dist[3] + 0
          = 0
```

Current:

```text
dist[2] = 1
```

Better path found:

```text
0 < 1
```

Update:

```text
dist[2] = 0
push_front(2)
```

Deque:

```text
[2, 2]
```

A node may appear more than once because its distance can improve.

### Iteration 3

Pop the newest `2`.

Best known:

```text
dist[2] = 0
```

Process `2 → 4` with cost `1`:

```text
0 + 1 = 1
dist[4] = 1
push_back(4)
```

Deque:

```text
[2, 4]
```

If using a finalization array:

```text
vis[2] = 1
```

### Iteration 4

The older duplicate `2` is popped.

Since:

```text
vis[2] == 1
```

skip it.

Deque:

```text
[4]
```

### Iteration 5

Process `4`.

Final distances:

```text
node:  1  2  3  4
dist:  0  0  0  1
```

Shortest path to `2` is:

```text
1 → 3 → 2
cost = 0 + 0 = 0
```

not the direct edge:

```text
1 → 2
cost = 1
```

---

# 11. Visited / Finalization Idea

The lecture screenshots use:

```cpp
if (vis[node]) {
    continue;
}

vis[node] = 1;
```

Interpretation:

```text
when a node is popped in the correct minimum-distance order,
we finalize it
```

This is similar to Dijkstra.

A node may have entered the deque multiple times because its tentative distance improved.

The `vis[]` check skips stale duplicate processing after finalization.

---

# 12. Lecture-Style C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

const int INF = 1e9;

void zeroOneBfs(int source, const vector<vector<pair<int, int>>>& graph, vector<int>& dist) {
    int n = graph.size() - 1;

    vector<int> vis(n + 1, 0);
    dist.assign(n + 1, INF);

    deque<int> dq;

    dist[source] = 0;
    dq.push_front(source);

    while (!dq.empty()) {
        int node = dq.front();
        dq.pop_front();

        if (vis[node]) {
            continue;
        }

        vis[node] = 1;

        for (auto [next, cost] : graph[node]) {
            if (dist[next] > dist[node] + cost) {
                dist[next] = dist[node] + cost;

                if (cost == 0) {
                    dq.push_front(next);
                } else {
                    dq.push_back(next);
                }
            }
        }
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<vector<pair<int, int>>> graph(n + 1);

    for (int i = 0; i < m; i++) {
        int u, v, cost;
        cin >> u >> v >> cost;

        graph[u].push_back({v, cost});
        graph[v].push_back({u, cost});
    }

    int source;
    cin >> source;

    vector<int> dist;
    zeroOneBfs(source, graph, dist);

    for (int node = 1; node <= n; node++) {
        cout << node << ": " << dist[node] << '\n';
    }

    return 0;
}
```

For a directed graph, remove the reverse insertion.

---

# 13. Simpler C++17 Without `vis`

A very common competitive-programming version does not use a visited array.

```cpp
#include <bits/stdc++.h>
using namespace std;

const int INF = 1e9;

void zeroOneBfs(int source, const vector<vector<pair<int, int>>>& graph, vector<int>& dist) {
    int n = graph.size() - 1;

    dist.assign(n + 1, INF);

    deque<int> dq;

    dist[source] = 0;
    dq.push_front(source);

    while (!dq.empty()) {
        int node = dq.front();
        dq.pop_front();

        for (auto [next, cost] : graph[node]) {
            if (dist[next] > dist[node] + cost) {
                dist[next] = dist[node] + cost;

                if (cost == 0) {
                    dq.push_front(next);
                } else {
                    dq.push_back(next);
                }
            }
        }
    }
}
```

This version is usually easier to remember:

```text
RELAX
   ↓
0 → front
1 → back
```

---

# 14. Correctness Intuition

Dijkstra says:

```text
always process the smallest tentative distance next
```

For general non-negative weights, we need a priority queue.

For 0-1 weights:

```text
from distance d
we only create d or d+1
```

So the deque keeps the correct order:

```text
same distance → front
next distance → back
```

That is why a deque is enough.

---

# 15. Complexity

Let:

```text
V = number of vertices
E = number of edges
```

Time:

```text
O(V + E)
```

Space:

```text
O(V + E)
```

Comparison:

```text
BFS       : O(V + E)
0-1 BFS   : O(V + E)
Dijkstra  : O((V + E) log V)
```

When weights are only `0` and `1`, 0-1 BFS is normally preferred over heap-based Dijkstra.

---

# 16. Common Mistakes

| Mistake | Correct Idea |
|---|---|
| Using `queue<int>` | Use `deque<int>` |
| `0`-edge pushed to back | `0 → push_front` |
| `1`-edge pushed to front | `1 → push_back` |
| No relaxation check | Update only if distance improves |
| Marking first discovery as final | Distance may improve later |
| Forgetting `INF` initialization | Non-source nodes start at `INF` |
| Using 0-1 BFS for weights `2, 5, 10...` | Use Dijkstra / suitable bucket method |
| Storing only neighbor | Store `{neighbor, cost}` |
| Forgetting reverse edge in undirected graph | Add both directions |
| Thinking “BFS” means ordinary queue | 0-1 BFS specifically uses a deque |

---

# 17. Recognition Model

If a problem can model every move as:

```text
FREE  → cost 0
PAID  → cost 1
```

think 0-1 BFS.

Common forms:

```text
same direction = 0, change = 1
same color = 0, different color = 1
teleport = 0, normal move = 1
empty cell = 0, break wall = 1
keep edge direction = 0, reverse edge = 1
minimum number of changes
minimum number of reversals
minimum paid transitions
```

Recognition question:

```text
Are all edge weights only 0 or 1?
```

If yes:

```text
deque + 0-1 BFS
```

---

# 18. Final Mental Model

Do not memorize only the code.

Understand:

```text
current distance = d
```

Then:

```text
0-edge → d
1-edge → d+1
```

Therefore:

```text
0-edge → push_front
1-edge → push_back
```

Visual:

```text
             edge cost
                 │
        ┌────────┴────────┐
        │                 │
        ▼                 ▼
        0                 1
        │                 │
        ▼                 ▼
    same dist          dist + 1
        │                 │
        ▼                 ▼
  push_front()       push_back()
        └────────┬────────┘
                 ▼
            pop_front()
                 ▼
        shortest-path order
```

Core code:

```cpp
if (dist[next] > dist[node] + cost) {
    dist[next] = dist[node] + cost;

    if (cost == 0) {
        dq.push_front(next);
    } else {
        dq.push_back(next);
    }
}
```

Core recognition:

```text
weights only 0 / 1
        ↓
       deque
        ↓
      0-1 BFS
```
