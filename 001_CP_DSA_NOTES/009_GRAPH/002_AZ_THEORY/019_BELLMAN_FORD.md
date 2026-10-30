# Bellman–Ford — SSSP with Negative Edges

> **Self-study CP note** based on the supplied AlgoZenith Bellman–Ford lecture screenshot.  
> **Lecture-visible ideas preserved:** SSSP, Dijkstra vs Bellman–Ford, edge relaxation, repeat relaxation `V - 1` times, `O(VE)`, and one extra relaxation pass to detect whether a distance can still decrease.  
> **Added for self-study:** prerequisites, proofs, dry runs, negative-cycle explanation, clean C++17 implementation, comparisons, and recognition patterns.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [SSSP Roadmap: BFS → 0-1 BFS → Dijkstra → Bellman-Ford](#2-sssp-roadmap-bfs--0-1-bfs--dijkstra--bellman-ford)
3. [What Bellman-Ford Solves](#3-what-bellman-ford-solves)
4. [Graph Representation for Bellman-Ford](#4-graph-representation-for-bellman-ford)
5. [Prerequisite: Distance Array](#5-prerequisite-distance-array)
6. [Prerequisite: Edge Relaxation](#6-prerequisite-edge-relaxation)
7. [Why Dijkstra Is Not Enough](#7-why-dijkstra-is-not-enough)
8. [Main Bellman-Ford Idea](#8-main-bellman-ford-idea)
9. [Why Exactly V-1 Rounds?](#9-why-exactly-v-1-rounds)
10. [Step-by-Step Bellman-Ford](#10-step-by-step-bellman-ford)
11. [Detailed Dry Run](#11-detailed-dry-run)
12. [Negative Cycle](#12-negative-cycle)
13. [How Negative-Cycle Detection Works](#13-how-negative-cycle-detection-works)
14. [Clean C++17 — Basic Bellman-Ford](#14-clean-c17--basic-bellman-ford)
15. [Clean C++17 — With Negative-Cycle Detection](#15-clean-c17--with-negative-cycle-detection)
16. [Early-Stopping Optimization](#16-early-stopping-optimization)
17. [Complexity](#17-complexity)
18. [BFS vs 0-1 BFS vs Dijkstra vs Bellman-Ford](#18-bfs-vs-0-1-bfs-vs-dijkstra-vs-bellman-ford)
19. [Common Mistakes](#19-common-mistakes)
20. [Recognition Model](#20-recognition-model)
21. [Final Mental Model](#21-final-mental-model)

---

# 1. Prerequisites

Before Bellman–Ford, you should know:

- directed and undirected weighted graphs,
- vertices and edges,
- single-source shortest path,
- `dist[]`,
- infinity initialization,
- edge relaxation,
- why BFS works for equal weights,
- why 0-1 BFS works for weights `{0,1}`,
- why Dijkstra requires non-negative weights,
- negative edge vs negative cycle,
- `long long` for path sums.

Learning chain:

```text
Graph
  ↓
Shortest Path
  ↓
dist[]
  ↓
Relaxation
  ↓
Negative edges appear
  ↓
Dijkstra is unsafe
  ↓
Bellman–Ford
```

---

# 2. SSSP Roadmap: BFS → 0-1 BFS → Dijkstra → Bellman-Ford

The lecture starts from:

```text
SSSP
```

which means:

```text
Single Source Shortest Path
```

You have:

```text
one source S
```

and want:

```text
shortest distance from S to every node
```

The algorithm depends mainly on edge weights.

```text
All edges = 1
      ↓
     BFS

Weights are only 0 or 1
      ↓
    0-1 BFS

All weights >= 0
      ↓
   Dijkstra

Negative edges may exist
      ↓
 Bellman-Ford
```

Very important:

```text
Bellman-Ford supports negative edges.

It does NOT give a finite shortest-path answer
when a reachable negative cycle can keep reducing the cost.
```

---

# 3. What Bellman-Ford Solves

Input:

```text
G = (V, E)
```

where:

- `V` = number of vertices,
- `E` = number of edges,
- edges may have negative weights.

Goal:

```text
dist[v] = shortest distance from source S to v
```

provided no relevant reachable negative cycle makes the answer unbounded below.

Example:

```text
S --4--> A
S --5--> B
A ---2--> B
```

The edge:

```text
A → B = -2
```

is perfectly valid for Bellman–Ford.

From `S`:

```text
direct S → B = 5

via A:
S → A → B
= 4 + (-2)
= 2
```

Therefore:

```text
dist[B] = 2
```

---

# 4. Graph Representation for Bellman-Ford

Dijkstra is often written around an adjacency list because it repeatedly expands the cheapest node.

Bellman–Ford is naturally written as an **edge list** because the core operation is:

```text
relax every edge
```

Represent one edge as:

```text
(from, to, weight)
```

C++:

```cpp
struct Edge {
    int from;
    int to;
    long long weight;
};
```

Example:

```text
1 → 2, weight = 5
2 → 3, weight = -4
1 → 3, weight = 10
```

Store:

```cpp
edges.push_back({1, 2, 5});
edges.push_back({2, 3, -4});
edges.push_back({1, 3, 10});
```

For an undirected edge:

```text
u --w-- v
```

store both:

```text
u → v with w
v → u with w
```

---

# 5. Prerequisite: Distance Array

Bellman–Ford maintains:

```text
dist[node]
```

Meaning:

```text
best known cost from source to node
```

Initialization:

```text
dist[source] = 0

dist[all other nodes] = INF
```

Example for source `1`:

```text
nodes:  1    2    3    4

dist:   0   INF  INF  INF
```

Why `INF`?

Because initially:

```text
we have not found any path
```

to those nodes.

---

# 6. Prerequisite: Edge Relaxation

This is the most important Bellman–Ford prerequisite.

Suppose we have:

```text
u ----w----> v
```

and currently:

```text
dist[u] = known shortest candidate to u
dist[v] = current best candidate to v
```

A route to `v` through `u` costs:

```text
dist[u] + w
```

So compare:

```text
dist[u] + w < dist[v] ?
```

If YES:

```text
dist[v] = dist[u] + w
```

This operation is called:

```text
RELAXATION
```

---

## 6.1 Tiny example

Suppose:

```text
dist[u] = 7
edge u → v = 3
dist[v] = 15
```

New candidate:

```text
7 + 3 = 10
```

Compare:

```text
10 < 15
```

Yes.

Update:

```text
dist[v] = 10
```

---

## 6.2 Negative-weight relaxation

Suppose:

```text
dist[u] = 8
u → v = -5
dist[v] = 6
```

Candidate:

```text
8 + (-5)
= 3
```

Compare:

```text
3 < 6
```

Yes.

So:

```text
dist[v] = 3
```

Bellman–Ford handles this naturally.

---

## 6.3 Critical INF guard

Never calculate:

```text
INF + weight
```

as though `INF` were a real distance.

Correct check:

```cpp
if (dist[from] != INF && dist[from] + weight < dist[to]) {
    dist[to] = dist[from] + weight;
}
```

Meaning:

```text
Only relax from a node that is actually reachable.
```

---

# 7. Why Dijkstra Is Not Enough

Dijkstra uses this greedy idea:

```text
Take the currently smallest-distance unprocessed node
and finalize it.
```

This is valid when:

```text
all edge weights >= 0
```

because going farther cannot later create a cheaper route back.

Negative edges break that guarantee.

Example:

```text
S --2--> A
S --5--> B
B ---10--> A
```

Initially:

```text
dist[A] = 2
dist[B] = 5
```

But path:

```text
S → B → A
```

costs:

```text
5 + (-10)
= -5
```

So the true answer for `A` is:

```text
-5
```

A naive greedy finalization of `A = 2` would be wrong.

Bellman–Ford does not finalize nodes greedily.

Instead it repeatedly asks:

```text
Can any edge improve any distance?
```

---

# 8. Main Bellman-Ford Idea

The lecture writes the core plan:

```text
for V - 1 times:
    relax all edges
```

That is Bellman–Ford.

Pseudocode:

```text
dist[source] = 0

repeat V - 1 times:
    for every edge (u, v, w):
        if dist[u] is reachable:
            dist[v] = min(dist[v], dist[u] + w)
```

Conceptually:

```text
Round 1
    ↓
best paths using at most 1 edge can propagate

Round 2
    ↓
best paths using at most 2 edges can propagate

Round 3
    ↓
best paths using at most 3 edges can propagate

...

Round V - 1
    ↓
enough for every simple shortest path
```

---

# 9. Why Exactly V-1 Rounds?

This is the key proof.

A simple path cannot repeat a vertex.

With:

```text
V vertices
```

the longest possible simple path contains:

```text
V vertices
```

and therefore at most:

```text
V - 1 edges
```

Example with `5` vertices:

```text
1 → 2 → 3 → 4 → 5
```

Vertices:

```text
5
```

Edges:

```text
4 = V - 1
```

So if there is no useful negative cycle, some shortest path can be chosen to be simple.

Therefore:

```text
a shortest path needs at most V - 1 edges
```

Bellman–Ford gives enough opportunities for those improvements to propagate.

---

## 9.1 Another mental model

Imagine the correct shortest path is:

```text
S → A → B → C → D
```

It has:

```text
4 edges
```

Relaxation information may need to travel:

```text
S → A
then
A → B
then
B → C
then
C → D
```

Repeated full-edge scans guarantee enough propagation even if the edge-list order is inconvenient.

---

# 10. Step-by-Step Bellman-Ford

Given:

```text
V vertices
E edges
source S
```

---

## Step 1 — Initialize

```text
dist[S] = 0
dist[others] = INF
```

---

## Step 2 — Repeat V-1 rounds

```text
for round = 1 ... V-1
```

---

## Step 3 — Scan every edge

For:

```text
u → v with weight w
```

try:

```text
dist[u] + w
```

---

## Step 4 — Relax

If:

```text
dist[u] != INF
```

and:

```text
dist[u] + w < dist[v]
```

then:

```text
dist[v] = dist[u] + w
```

---

## Step 5 — Optional early stop

If an entire round performs:

```text
0 updates
```

then distances are already stable.

Stop.

---

## Step 6 — Detect a negative cycle

After `V - 1` rounds:

```text
scan all edges one more time
```

If some reachable edge can still improve:

```text
dist[u] + w < dist[v]
```

then:

```text
reachable negative cycle exists
```

---

# 11. Detailed Dry Run

Use this graph:

```text
        4
S ------------> A
|               |
|5             -2
|               |
v               v
B ------------> C
       3

A ------------> B
       1
```

Edges in this order:

```text
S → A = 4
S → B = 5
A → B = 1
A → C = -2
B → C = 3
```

There are:

```text
V = 4
```

so at most:

```text
V - 1 = 3 rounds
```

are needed.

---

## Initial state

```text
dist[S] = 0
dist[A] = INF
dist[B] = INF
dist[C] = INF
```

Table:

| Node | S | A | B | C |
|---|---:|---:|---:|---:|
| Initial | 0 | INF | INF | INF |

---

## Round 1

### Edge 1

```text
S → A = 4
```

Candidate:

```text
0 + 4 = 4
```

Update:

```text
A: INF → 4
```

### Edge 2

```text
S → B = 5
```

Candidate:

```text
0 + 5 = 5
```

Update:

```text
B: INF → 5
```

### Edge 3

```text
A → B = 1
```

Candidate:

```text
4 + 1 = 5
```

Current:

```text
B = 5
```

No improvement.

### Edge 4

```text
A → C = -2
```

Candidate:

```text
4 + (-2)
= 2
```

Update:

```text
C: INF → 2
```

### Edge 5

```text
B → C = 3
```

Candidate:

```text
5 + 3 = 8
```

Current:

```text
C = 2
```

No update.

End of Round 1:

```text
S = 0
A = 4
B = 5
C = 2
```

---

## Round 2

Scan all five edges again.

No edge produces a smaller distance.

So:

```text
updated = false
```

We may stop early.

Final:

```text
dist[S] = 0
dist[A] = 4
dist[B] = 5
dist[C] = 2
```

The shortest route to `C` is:

```text
S → A → C
```

Cost:

```text
4 + (-2)
= 2
```

---

## Compact dry run

```text
Initial: S=0, A=INF, B=INF, C=INF

Round 1:
S→A(4): A=4
S→B(5): B=5
A→B(1): candidate=5, no change
A→C(-2): C=2
B→C(3): candidate=8, no change

After R1: [0,4,5,2]

Round 2:
no relaxation succeeds

STOP
```

---

# 12. Negative Cycle

A negative cycle is a cycle whose total edge weight is:

```text
< 0
```

Example:

```text
A --2--> B
B ---5--> C
C --1--> A
```

Cycle cost:

```text
2 + (-5) + 1
= -2
```

Every time we go around the cycle:

```text
path cost decreases by 2
```

So:

```text
once around  → -2
twice around → -4
three times  → -6
...
```

There is no finite minimum.

The cost can keep moving toward:

```text
-∞
```

---

# 13. How Negative-Cycle Detection Works

Without a negative cycle:

```text
V - 1 rounds are enough.
```

Therefore, after those rounds:

```text
no shortest distance should still improve
```

Now perform **one extra edge scan**.

If an edge:

```text
u → v with weight w
```

still satisfies:

```text
dist[u] + w < dist[v]
```

then the graph contains a reachable structure that lets the distance keep decreasing.

That implies:

```text
a reachable negative cycle
```

This is exactly the lecture idea:

```text
relax all edges again
if distance decreases
→ negative cycle
```

---

## 13.1 Important word: reachable

Suppose a negative cycle exists in a disconnected part of the graph that the source cannot reach.

Then for source `S`:

```text
that cycle does not affect SSSP
```

That is why the check still uses:

```cpp
dist[from] != INF
```

---

# 14. Clean C++17 — Basic Bellman-Ford

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll INF = 4e18;

struct Edge {
    int from;
    int to;
    ll weight;
};

vector<ll> bellmanFord(int n, int source, const vector<Edge>& edges) {
    vector<ll> dist(n + 1, INF);
    dist[source] = 0;

    for (int round = 1; round <= n - 1; round++) {
        for (const Edge& edge : edges) {
            if (dist[edge.from] == INF) {
                continue;
            }

            ll newDist = dist[edge.from] + edge.weight;

            if (newDist < dist[edge.to]) {
                dist[edge.to] = newDist;
            }
        }
    }

    return dist;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<Edge> edges;

    for (int i = 0; i < m; i++) {
        int u, v;
        ll weight;
        cin >> u >> v >> weight;

        edges.push_back({u, v, weight});
    }

    int source;
    cin >> source;

    vector<ll> dist = bellmanFord(n, source, edges);

    for (int node = 1; node <= n; node++) {
        if (dist[node] == INF) {
            cout << node << ": INF\n";
        } else {
            cout << node << ": " << dist[node] << '\n';
        }
    }

    return 0;
}
```

---

# 15. Clean C++17 — With Negative-Cycle Detection

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll INF = 4e18;

struct Edge {
    int from;
    int to;
    ll weight;
};

bool bellmanFord(int n, int source, const vector<Edge>& edges, vector<ll>& dist) {
    dist.assign(n + 1, INF);
    dist[source] = 0;

    for (int round = 1; round <= n - 1; round++) {
        bool updated = false;

        for (const Edge& edge : edges) {
            if (dist[edge.from] == INF) {
                continue;
            }

            ll newDist = dist[edge.from] + edge.weight;

            if (newDist < dist[edge.to]) {
                dist[edge.to] = newDist;
                updated = true;
            }
        }

        if (!updated) {
            break;
        }
    }

    for (const Edge& edge : edges) {
        if (dist[edge.from] == INF) {
            continue;
        }

        if (dist[edge.from] + edge.weight < dist[edge.to]) {
            return false;
        }
    }

    return true;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<Edge> edges;

    for (int i = 0; i < m; i++) {
        int u, v;
        ll weight;
        cin >> u >> v >> weight;

        edges.push_back({u, v, weight});
    }

    int source;
    cin >> source;

    vector<ll> dist;

    bool noReachableNegativeCycle = bellmanFord(n, source, edges, dist);

    if (!noReachableNegativeCycle) {
        cout << "Reachable negative cycle exists\n";
        return 0;
    }

    for (int node = 1; node <= n; node++) {
        if (dist[node] == INF) {
            cout << node << ": INF\n";
        } else {
            cout << node << ": " << dist[node] << '\n';
        }
    }

    return 0;
}
```

---

# 16. Early-Stopping Optimization

Basic Bellman–Ford always performs:

```text
V - 1 rounds
```

But sometimes distances stabilize much earlier.

Use:

```cpp
bool updated = false;
```

Whenever a relaxation succeeds:

```cpp
updated = true;
```

After the round:

```cpp
if (!updated) {
    break;
}
```

Why safe?

Because:

```text
if one complete scan changes nothing,
another identical scan cannot suddenly create a new shorter route
```

when all current distances are already stable.

Worst-case complexity remains:

```text
O(VE)
```

but many practical cases finish earlier.

---

# 17. Complexity

The lecture writes:

```text
Bellman-Ford = O(VE)
```

Why?

Outer rounds:

```text
V - 1
```

Each round scans:

```text
E edges
```

Therefore:

```text
(V - 1) × E
≈ V × E
```

So:

```text
Time = O(VE)
```

Space:

```text
dist[] = O(V)
edges  = O(E)
```

Total:

```text
O(V + E)
```

---

## 17.1 Why O(V³) sometimes appears

If the graph is dense:

```text
E = O(V²)
```

Then:

```text
O(VE)
=
O(V × V²)
=
O(V³)
```

That explains the `O(V³)` note visible in the lecture.

---

# 18. BFS vs 0-1 BFS vs Dijkstra vs Bellman-Ford

| Edge weights | Best standard choice | Core data structure | Typical complexity |
|---|---|---|---|
| All `1` | BFS | `queue` | `O(V + E)` |
| Only `0` and `1` | 0-1 BFS | `deque` | `O(V + E)` |
| All non-negative | Dijkstra | min-heap | `O((V+E) log V)` |
| Negative edges allowed | Bellman-Ford | edge list | `O(VE)` |

Fast recognition:

```text
weight = 1
→ BFS

weight ∈ {0,1}
→ 0-1 BFS

weight >= 0
→ Dijkstra

negative weights possible
→ Bellman-Ford
```

---

## 18.1 Important correction

Bellman–Ford is **not a version of 0-1 BFS**.

They solve different weight regimes:

```text
0-1 BFS:
weights only 0 or 1

Bellman-Ford:
arbitrary positive/zero/negative weights
```

So think of them as separate branches of SSSP.

---

# 19. Common Mistakes

| Mistake | Correct idea |
|---|---|
| Run only one edge scan | Need up to `V-1` rounds |
| Forget `dist[u] != INF` | Never relax from unreachable node |
| Use Dijkstra with negative edges | Use Bellman-Ford |
| Think negative edge = negative cycle | A negative edge alone is fine |
| Detect cycle after fewer than `V-1` rounds | Use the extra pass after normal relaxation |
| Forget reachability in negative-cycle check | Ignore unreachable negative cycles for source-based SSSP |
| Use `int` for large path totals | Use `long long` |
| Store only adjacency list and complicate scans | Edge list is simplest for Bellman-Ford |
| Confuse number of vertices and number of rounds | At most `V-1` relaxation rounds |
| Assume every problem with negative weights has finite answer | Reachable negative cycle can make answer unbounded |

---

# 20. Recognition Model

When you read a problem, ask these questions in order.

### Question 1

Is this a shortest-path problem?

Look for:

```text
minimum cost
minimum distance
minimum time
minimum score
cheapest transformation
```

If yes:

```text
SSSP?
```

---

### Question 2

What are the edge weights?

```text
all equal?
0/1?
non-negative arbitrary?
negative possible?
```

---

### Question 3

Do negative weights exist?

If YES:

```text
Dijkstra warning
```

Think:

```text
Bellman-Ford
```

especially when constraints allow:

```text
O(VE)
```

---

### Question 4

Does the problem ask about:

```text
negative cycles
arbitrage
cost decreasing forever
unbounded shortest path
```

Bellman–Ford is a major signal.

---

# 21. Final Mental Model

Bellman–Ford is extremely simple once **relaxation** is clear.

```text
1. dist[source] = 0

2. Repeat V-1 times:
      scan every edge
      try to improve its destination

3. Scan every edge once more:
      if anything still improves
      → reachable negative cycle
```

The core line is:

```text
dist[v] = min(dist[v], dist[u] + w)
```

but only when:

```text
dist[u] != INF
```

Remember why `V - 1`:

```text
A simple path through V vertices
contains at most V - 1 edges.
```

Remember why the extra round detects a negative cycle:

```text
After enough rounds,
a normal shortest path cannot improve anymore.

If it still improves,
some cycle is allowing the cost to keep decreasing.
```

Final SSSP decision tree:

```text
Shortest path from one source
          |
          +-- all weights = 1 ------> BFS
          |
          +-- weights ∈ {0,1} ------> 0-1 BFS
          |
          +-- all weights >= 0 -----> Dijkstra
          |
          +-- negative edges -------> Bellman-Ford
```

That recognition tree is more important in contests than memorizing four independent code templates.
