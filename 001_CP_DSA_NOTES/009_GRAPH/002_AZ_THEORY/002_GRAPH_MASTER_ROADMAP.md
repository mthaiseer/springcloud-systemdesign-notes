# Graph Master Roadmap — Prerequisites → Algorithms → Advanced Graphs
## AlgoZenith Topic Overview Converted into a Step-by-Step CP / DSA Learning Path
### TLE-Style Self-Study Note with Dependencies, Recognition Rules, ASCII Roadmaps, and C++17

> **Purpose:** This note turns the supplied AlgoZenith Graph Topic Overview screenshots into a practical learning roadmap. Use the earlier **Graph Foundations** note as Level 0.

---

# Table of Contents

1. [Complete Roadmap](#1-complete-roadmap)
2. [Level 0 — Mandatory Prerequisites](#2-level-0--mandatory-prerequisites)
3. [Level 1 — Reachability: DFS and BFS](#3-level-1--reachability-dfs-and-bfs)
4. [Level 2 — DFS/BFS Variants](#4-level-2--dfsbfs-variants)
5. [Level 3 — Topological Ordering](#5-level-3--topological-ordering)
6. [Level 4 — Shortest Paths](#6-level-4--shortest-paths)
7. [Level 5 — DSU](#7-level-5--dsu)
8. [Level 6 — Minimum Spanning Tree](#8-level-6--minimum-spanning-tree)
9. [Level 7 — Trees](#9-level-7--trees)
10. [Level 8 — Bridges and Articulation Points](#10-level-8--bridges-and-articulation-points)
11. [Level 9 — SCC](#11-level-9--scc)
12. [Level 10 — Eulerian and Hamiltonian](#12-level-10--eulerian-and-hamiltonian)
13. [Level 11 — Network Flow](#13-level-11--network-flow)
14. [Level 12 — Advanced Tree / Graph Structures](#14-level-12--advanced-tree--graph-structures)
15. [Recommended Learning Order](#15-recommended-learning-order)
16. [Recognition Map](#16-recognition-map)
17. [Contest Decision Tree](#17-contest-decision-tree)
18. [Complexity Cheat Sheet](#18-complexity-cheat-sheet)
19. [Core C++17 Templates](#19-core-c17-templates)
20. [Mastery Checklist](#20-mastery-checklist)
21. [Final Mental Model](#21-final-mental-model)

---

# 1. Complete Roadmap

The lecture overview can be organized as this dependency graph:

```text
GRAPH FOUNDATIONS
│
├── Reachability
│   ├── DFS
│   └── BFS
│
├── DFS / BFS Variants
│   ├── Connected Components
│   ├── Cycle Detection
│   ├── Bipartite Checking
│   ├── Entry / Exit Time
│   ├── Parent / Depth
│   └── Low-Link Ideas
│
├── Topological Ordering
│   ├── DFS Topological Sort
│   └── Kahn's Algorithm
│
├── Shortest Paths
│   ├── SSSP
│   ├── MSSP
│   └── APSP
│
│   Algorithms:
│   ├── BFS
│   ├── 0-1 BFS
│   ├── Dijkstra
│   ├── Bellman-Ford
│   ├── DAG Shortest Path
│   └── Floyd-Warshall
│
├── DSU
│
├── MST
│   ├── Kruskal
│   └── Prim
│
├── Trees
│   ├── DFS / BFS
│   ├── Diameter
│   ├── Center / Centroid
│   ├── Subtree Concepts
│   ├── Euler Tour
│   ├── Tree DP
│   └── Rerooting
│
├── Bridges / Articulation Points
│
├── SCC
│   └── Condensation DAG
│
├── Eulerian Path / Tour
│
├── Hamiltonian Path / Tour
│   └── Bitmask DP
│
├── Network Flow
│   ├── Max Flow
│   └── Min-Cost Max-Flow
│
└── Advanced Tree Structures
    ├── DS on Trees
    ├── Block-Cut Tree
    └── Euler Tour on Trees
```

The most important lesson is:

```text
Do not learn graph algorithms as isolated templates.
Learn them as a dependency tree.
```

---

# 2. Level 0 — Mandatory Prerequisites

Before graph algorithms, be comfortable with:

```text
C++ STL
├── vector
├── pair
├── queue
├── deque
├── stack
├── set / unordered_set
├── priority_queue
└── sort

Programming
├── recursion
├── functions
├── pass by reference
└── complexity

Graph Basics
├── vertex / edge
├── directed / undirected
├── weighted / unweighted
├── degree / indegree / outdegree
├── path / cycle
├── connected component
├── tree
├── DAG
└── bipartite
```

## Graph representation prerequisite

Default CP representation:

```text
Adjacency List
```

Undirected:

```cpp
vector<vector<int>> graph(n);

for (int i = 0; i < m; i++) {
    int u, v;
    cin >> u >> v;

    graph[u].push_back(v);
    graph[v].push_back(u);
}
```

Directed:

```cpp
graph[u].push_back(v);
```

Weighted:

```cpp
vector<vector<pair<int, int>>> graph(n);
graph[u].push_back({v, weight});
```

## Complexity prerequisite

For adjacency lists:

```text
DFS = O(V + E)
BFS = O(V + E)
```

Reason:

```text
visit every vertex once
+
inspect every edge a constant number of times
```

---

# 3. Level 1 — Reachability: DFS and BFS

The first graph question is usually:

```text
Can x reach y?
```

Example:

```text
x ---- a ---- b ---- y
```

If some path exists, `y` is reachable from `x`.

The two fundamental traversal engines are:

```text
DFS
BFS
```

## DFS

Goes deep before returning.

```text
        1
       / \
      2   3
     / \
    4   5
```

Possible DFS:

```text
1 -> 2 -> 4 -> 5 -> 3
```

Core recursion:

```text
visit current
→ for every unvisited neighbor
→ recursively visit neighbor
```

## BFS

Visits level-by-level.

```text
Level 0: 1
Level 1: 2,3
Level 2: 4,5
```

BFS uses:

```text
queue
```

and is especially important because it gives shortest path in an unweighted graph.

## DFS vs BFS

| Requirement | Tool |
|---|---|
| Reachability | DFS / BFS |
| Components | DFS / BFS |
| Unweighted shortest path | BFS |
| Subtree / recursive structure | DFS |
| Level-by-level processing | BFS |
| Tree DP | DFS |

Key idea:

```text
DFS/BFS are traversal engines.
Many advanced algorithms are variants built on top of them.
```

---

# 4. Level 2 — DFS/BFS Variants

Once basic traversal is automatic, add state.

```text
DFS Variants
├── Connected Components
├── Cycle Detection
├── Bipartite Checking
├── Entry / Exit Time
├── Parent / Depth
├── Subtree Size
└── Low-Link
```

## Connected components

```text
1---2---3      4---5      6
```

Run DFS/BFS from every still-unvisited node.

```text
DFS(1) -> component 1
DFS(4) -> component 2
DFS(6) -> component 3
```

Complexity:

```text
O(V + E)
```

## Cycle detection — undirected

During DFS:

```text
visited neighbor
AND
neighbor != parent
```

indicates a cycle.

## Cycle detection — directed

Use states:

```text
0 = unvisited
1 = currently in recursion stack
2 = fully processed
```

Edge to a state-1 node means:

```text
back edge -> directed cycle
```

## Bipartite checking

Two-color the graph:

```text
color[node] = 0 or 1
```

Every edge must connect opposite colors.

Key theorem:

```text
Undirected graph is bipartite
iff
it contains no odd cycle
```

## Entry / exit time

Record:

```text
tin[u]  = DFS entry time
tout[u] = DFS exit time
```

For rooted trees:

```text
u is ancestor of v
iff

tin[u] <= tin[v]
and
tout[v] <= tout[u]
```

This becomes a prerequisite for Euler Tour, LCA, and subtree flattening.

---

# 5. Level 3 — Topological Ordering

Topological ordering applies to a:

```text
DAG
```

For every edge:

```text
u -> v
```

we need:

```text
u appears before v
```

Example:

```text
A -> C
B -> C
C -> D
```

Valid order:

```text
A, B, C, D
```

## Kahn's Algorithm

Prerequisites:

```text
indegree
queue
```

Flow:

```text
all indegree-0 nodes -> queue
      ↓
remove one node
      ↓
reduce indegree of outgoing neighbors
      ↓
new indegree-0 nodes -> queue
```

If processed count `< V`:

```text
cycle exists
```

Complexity:

```text
O(V + E)
```

## DFS topological sort

Push a node only after all descendants finish.

Then reverse finishing order.

Prerequisite connection:

```text
DFS exit time -> topological order
```

---

# 6. Level 4 — Shortest Paths

The lecture splits shortest paths into:

```text
SSSP
MSSP
APSP
```

## SSSP

Single Source Shortest Path.

```text
one source -> distance to every node
```

## MSSP

Multi-Source Shortest Path.

```text
many sources -> nearest-source distance
```

Typical trick:

```text
push all sources into BFS queue with distance 0
```

## APSP

All-Pairs Shortest Path.

```text
dist[u][v]
for every pair
```

## Algorithm selection

| Graph / Edge Property | Algorithm |
|---|---|
| Unweighted / all weight 1 | BFS |
| Edge weights only 0 or 1 | 0-1 BFS |
| Non-negative weights | Dijkstra |
| Negative edges possible | Bellman-Ford |
| Need negative-cycle detection | Bellman-Ford |
| DAG | Topological DP |
| All pairs, small N | Floyd-Warshall |

This should become automatic.

```text
edge weights
→ choose shortest-path algorithm
```

## BFS shortest path

BFS explores nodes in increasing edge-count distance.

```text
distance 0
→ distance 1
→ distance 2
→ ...
```

Therefore the first discovery of a node is shortest.

## 0-1 BFS

For weights:

```text
0 or 1 only
```

Use:

```text
deque
```

Rule:

```text
weight 0 -> push_front
weight 1 -> push_back
```

Complexity:

```text
O(V + E)
```

## Dijkstra

Use when all edge weights are non-negative.

Relaxation:

```text
if dist[u] + w < dist[v]:
    dist[v] = dist[u] + w
```

Data structure:

```text
min priority_queue
```

Typical complexity:

```text
O(E log V)
```

Do not use standard Dijkstra with arbitrary negative edges.

## Bellman-Ford

Use when negative weights may exist.

Relax every edge:

```text
V - 1 times
```

If another relaxation is still possible:

```text
reachable negative cycle exists
```

Complexity:

```text
O(VE)
```

## Floyd-Warshall

Dynamic programming for APSP.

Transition:

```text
dist[i][j]
=
min(
    dist[i][j],
    dist[i][k] + dist[k][j]
)
```

Complexity:

```text
O(V^3)
```

## DAG shortest path

```text
topological sort
→ relax edges in topological order
```

Complexity:

```text
O(V + E)
```

This connects:

```text
Topological Sort
+
DP / Relaxation
```

---

# 7. Level 5 — DSU

Disjoint Set Union manages changing components.

Operations:

```text
find(x)
union(a,b)
```

Optimizations:

```text
path compression
union by size/rank
```

Amortized complexity:

```text
O(alpha(N))
```

Practically almost constant.

Uses:

```text
dynamic connectivity
cycle detection while adding edges
Kruskal MST
```

---

# 8. Level 6 — Minimum Spanning Tree

MST asks:

```text
Connect all vertices
with minimum total edge weight
without cycles.
```

Result has:

```text
V - 1 edges
```

Main algorithms:

```text
Kruskal
Prim
```

## Kruskal

Prerequisite:

```text
DSU
```

Steps:

```text
sort edges by weight
→ scan smallest to largest
→ if endpoints are in different components:
      take edge
      union components
```

Complexity:

```text
O(E log E)
```

## Prim

Grow one connected tree.

```text
current MST
+
cheapest outgoing edge
```

Usually:

```text
priority_queue
```

Complexity:

```text
O(E log V)
```

## MST vs shortest path

```text
Shortest Path:
minimum route between nodes
```

```text
MST:
minimum total cost to connect all nodes
```

An MST does not guarantee all-pairs shortest paths.

---

# 9. Level 7 — Trees

A tree is:

```text
connected + undirected + acyclic
```

For `N` vertices:

```text
E = N - 1
```

The lecture roadmap includes:

```text
Trees
├── DFS / BFS
├── Diameter
├── Center / Centroid
├── Subtree Concept
└── DP on Tree
```

## Tree traversal

Because there are no cycles, parent checking is enough:

```cpp
void dfs(int node, int parent, const vector<vector<int>>& graph) {
    for (int neighbor : graph[node]) {
        if (neighbor == parent) {
            continue;
        }

        dfs(neighbor, node, graph);
    }
}
```

## Parent, depth, subtree size

During DFS compute:

```text
parent[u]
depth[u]
subtreeSize[u]
```

Subtree size:

```text
subtreeSize[u] = 1

for child v:
    dfs(v)
    subtreeSize[u] += subtreeSize[v]
```

These become prerequisites for advanced tree algorithms.

## Tree diameter

Definition:

```text
longest shortest path between any two tree vertices
```

Method:

```text
1. BFS/DFS from arbitrary s.
2. Farthest node = a.
3. BFS/DFS from a.
4. Farthest distance = diameter.
```

Complexity:

```text
O(N)
```

## Tree center

The center minimizes maximum distance to other vertices.

It lies in the middle of a diameter.

Possible count:

```text
1 or 2 centers
```

## Tree centroid

A centroid satisfies:

```text
remove centroid
→ every remaining component size <= N/2
```

Do not confuse:

```text
Center   -> distance property
Centroid -> size property
```

## Rerooting

Compute answer for one root, then transform it when moving root across an edge.

```text
answer(root=0)
→ answer(child)
→ answer(grandchild)
```

Often reduces:

```text
O(N^2)
```

to:

```text
O(N)
```

## Tree DP

General pattern:

```text
dfs(node, parent)

answer at node
=
combine answers from children
```

Common state:

```text
dp[node][state]
```

---

# 10. Level 8 — Bridges and Articulation Points

## Bridge

An edge whose removal increases connected components.

```text
A --- B ===== C --- D
```

If `B-C` is the only connection between the two regions, it is a bridge.

## Articulation point

A vertex whose removal increases connected components.

```text
     A
     |
B --- X --- C
     |
     D
```

`X` is critical.

## Low-link prerequisite

Need:

```text
tin[u]
low[u]
```

Interpretation:

```text
tin[u] = DFS entry time
```

```text
low[u] = earliest entry time reachable
         from u's DFS subtree
```

Bridge condition for DFS tree edge `u-v`:

```text
low[v] > tin[u]
```

Meaning:

```text
v's subtree cannot reach above u without edge u-v
```

Complexity:

```text
O(V + E)
```

---

# 11. Level 9 — SCC

Strongly Connected Component applies to directed graphs.

Inside one SCC:

```text
u reaches v
AND
v reaches u
```

Main algorithms:

```text
Kosaraju
Tarjan
```

Prerequisites:

```text
DFS
finishing order
transpose graph
low-link idea for Tarjan
```

## Condensation DAG

Compress every SCC into one super-node.

```text
[SCC A] ---> [SCC B] ---> [SCC C]
```

The resulting graph is always a DAG.

Why?

If SCC super-nodes formed a cycle, they would all be mutually reachable and should have been a single SCC.

Powerful transformation:

```text
general directed graph
→ SCC compression
→ DAG
```

---

# 12. Level 10 — Eulerian and Hamiltonian

Memory rule:

```text
Eulerian    -> edges
Hamiltonian -> vertices
```

## Eulerian path / tour

Eulerian path:

```text
uses every edge exactly once
```

Eulerian circuit:

```text
uses every edge exactly once
and returns to start
```

For connected undirected graph:

```text
Euler circuit:
all degrees even
```

```text
Euler path:
exactly 0 or 2 odd-degree vertices
```

Constructive algorithm:

```text
Hierholzer's Algorithm
```

Complexity:

```text
O(E)
```

## Hamiltonian path

Hamiltonian path:

```text
visits every vertex exactly once
```

No simple local criterion solves the general case.

For small `N`, typical CP approach:

```text
Bitmask DP
```

State:

```text
dp[mask][last]
```

Meaning:

```text
can we visit exactly vertices in mask
and end at last?
```

Typical complexity:

```text
O(2^N * N^2)
```

This matches the lecture connection:

```text
Hamiltonian
→ DP + Bitmask
```

---

# 13. Level 11 — Network Flow

Network flow works on directed capacity graphs.

```text
S --10--> A --7--> T
 \       /
  --5--> B --8--> T
```

Question:

```text
How much flow can move from source S to sink T?
```

Prerequisites:

```text
directed graphs
BFS/DFS
residual graph
augmenting path
```

## Max Flow

Goal:

```text
maximize total S -> T flow
```

Algorithms:

```text
Ford-Fulkerson
Edmonds-Karp
Dinic
```

For competitive programming, Dinic is commonly the practical default for many standard constraints.

## Min-Cost Max-Flow

Each edge also has a cost.

Goal:

```text
send desired / maximum flow
with minimum total cost
```

Prerequisites:

```text
Max Flow
Shortest Path
Residual Graph
Negative Reverse Edges
```

Study this only after ordinary Max Flow is comfortable.

---

# 14. Level 12 — Advanced Tree / Graph Structures

The lecture roadmap later mentions:

```text
DS on Trees
Block-Cut Tree
Euler Tour on Trees
```

## Euler Tour on trees

Flatten a subtree into a contiguous array interval.

```text
tin[u] = timer++
```

Then subtree of `u` becomes an interval such as:

```text
[tin[u], tout[u]]
```

This connects trees to:

```text
Fenwick Tree
Segment Tree
```

## DS on Trees

Also called:

```text
small-to-large
DSU on Tree
sack technique
```

Useful for subtree frequency/statistics queries.

Prerequisites:

```text
subtree size
Euler tour
frequency arrays/maps
```

## Block-Cut Tree

Built from:

```text
articulation points
+
biconnected components
```

Prerequisites:

```text
low-link DFS
bridges
articulation points
biconnected components
```

This is an advanced topic.

---

# 15. Recommended Learning Order

Use this order instead of jumping randomly between graph topics.

```text
PHASE A — FOUNDATION
1. Graph terminology
2. Adjacency list / matrix
3. DFS
4. BFS

PHASE B — CORE GRAPH THINKING
5. Connected components
6. Cycle detection
7. Bipartite
8. Multi-source BFS
9. DFS entry / exit time

PHASE C — DIRECTED GRAPH STRUCTURE
10. Topological sort
11. Kahn's algorithm
12. DAG DP
13. SCC
14. Condensation DAG

PHASE D — SHORTEST PATH
15. BFS shortest path
16. 0-1 BFS
17. Dijkstra
18. Bellman-Ford
19. Floyd-Warshall
20. DAG shortest path

PHASE E — CONNECTIVITY / MST
21. DSU
22. Kruskal
23. Prim

PHASE F — TREES
24. Tree DFS/BFS
25. Parent / depth / subtree
26. Diameter
27. Center
28. Centroid
29. Euler tour
30. Tree DP
31. Rerooting

PHASE G — LOW-LINK
32. Bridges
33. Articulation points
34. Biconnected components
35. Block-cut tree

PHASE H — SPECIAL PATHS
36. Eulerian path/tour
37. Hierholzer
38. Hamiltonian path
39. Bitmask DP

PHASE I — ADVANCED
40. Max Flow
41. Min-Cost Max-Flow
42. DS on Trees
43. Advanced graph/tree structures
```

---

# 16. Recognition Map

```text
"Can reach?"
"connected?"
"number of groups?"
        ↓
DFS / BFS
```

```text
"minimum number of moves"
"all edges cost 1"
        ↓
BFS
```

```text
"edge cost is 0 or 1"
        ↓
0-1 BFS
```

```text
"non-negative weighted shortest path"
        ↓
Dijkstra
```

```text
"negative edges"
        ↓
Bellman-Ford
```

```text
"distance between every pair"
"N relatively small"
        ↓
Floyd-Warshall
```

```text
"prerequisites"
"dependency order"
        ↓
Topological Sort
```

```text
"merge groups"
"dynamic connectivity"
        ↓
DSU
```

```text
"minimum cost to connect all nodes"
        ↓
MST
```

```text
"remove edge disconnects graph"
        ↓
Bridge
```

```text
"remove vertex disconnects graph"
        ↓
Articulation Point
```

```text
"mutual reachability in directed graph"
        ↓
SCC
```

```text
"use every edge exactly once"
        ↓
Eulerian / Hierholzer
```

```text
"visit every vertex exactly once"
        ↓
Hamiltonian / Bitmask DP
```

```text
"maximum amount from S to T"
        ↓
Max Flow
```

---

# 17. Contest Decision Tree

```text
GRAPH PROBLEM
│
├── Is it a tree?
│   ├── YES
│   │   ├── distance? -> BFS/DFS
│   │   ├── longest path? -> diameter
│   │   ├── subtree? -> DFS / Euler Tour
│   │   ├── choose/optimize? -> Tree DP
│   │   └── all roots? -> Rerooting
│   │
│   └── NO
│
├── Reachability/components?
│   └── DFS/BFS
│
├── Directed dependency?
│   └── Topological / SCC
│
├── Shortest path?
│   ├── unweighted -> BFS
│   ├── 0/1 -> 0-1 BFS
│   ├── non-negative -> Dijkstra
│   ├── negative -> Bellman-Ford
│   ├── DAG -> Topological DP
│   └── all pairs -> Floyd-Warshall
│
├── Cheapest global connection?
│   └── MST
│
├── Critical edge/vertex?
│   └── Bridges / Articulation
│
├── Every edge once?
│   └── Eulerian / Hierholzer
│
├── Every vertex once?
│   └── Hamiltonian / Bitmask DP
│
└── Capacity / matching style?
    └── Flow
```

---

# 18. Complexity Cheat Sheet

| Algorithm | Typical Complexity |
|---|---:|
| DFS | `O(V + E)` |
| BFS | `O(V + E)` |
| Topological Sort | `O(V + E)` |
| SCC | `O(V + E)` |
| Bridges / Articulation | `O(V + E)` |
| 0-1 BFS | `O(V + E)` |
| Dijkstra | `O(E log V)` |
| Bellman-Ford | `O(VE)` |
| Floyd-Warshall | `O(V^3)` |
| DSU operation | `O(alpha(V))` amortized |
| Kruskal | `O(E log E)` |
| Prim | `O(E log V)` |
| Tree Diameter | `O(V)` |
| Euler Tour | `O(V)` |
| Hierholzer | `O(E)` |
| Hamiltonian Bitmask DP | `O(2^V * V^2)` |

---

# 19. Core C++17 Templates

These are foundation templates only. Detailed topic notes should contain the full dry run and proof.

## DFS

```cpp
void dfs(int node, const vector<vector<int>>& graph, vector<int>& visited) {
    visited[node] = 1;

    for (int neighbor : graph[node]) {
        if (!visited[neighbor]) {
            dfs(neighbor, graph, visited);
        }
    }
}
```

## BFS

```cpp
void bfs(int source, const vector<vector<int>>& graph) {
    int n = graph.size();

    vector<int> visited(n, 0);
    queue<int> q;

    visited[source] = 1;
    q.push(source);

    while (!q.empty()) {
        int node = q.front();
        q.pop();

        for (int neighbor : graph[node]) {
            if (!visited[neighbor]) {
                visited[neighbor] = 1;
                q.push(neighbor);
            }
        }
    }
}
```

## Unweighted BFS distance

```cpp
vector<int> bfsDistance(int source, const vector<vector<int>>& graph) {
    int n = graph.size();

    vector<int> distance(n, -1);
    queue<int> q;

    distance[source] = 0;
    q.push(source);

    while (!q.empty()) {
        int node = q.front();
        q.pop();

        for (int neighbor : graph[node]) {
            if (distance[neighbor] == -1) {
                distance[neighbor] = distance[node] + 1;
                q.push(neighbor);
            }
        }
    }

    return distance;
}
```

## DSU

```cpp
class DSU {
private:
    vector<int> parent;
    vector<int> size;

public:
    DSU(int n) {
        parent.resize(n);
        size.assign(n, 1);
        iota(parent.begin(), parent.end(), 0);
    }

    int find(int node) {
        if (parent[node] == node) {
            return node;
        }

        return parent[node] = find(parent[node]);
    }

    bool unite(int a, int b) {
        a = find(a);
        b = find(b);

        if (a == b) {
            return false;
        }

        if (size[a] < size[b]) {
            swap(a, b);
        }

        parent[b] = a;
        size[a] += size[b];

        return true;
    }
};
```

---

# 20. Mastery Checklist

You are ready to move beyond core graphs when you can implement without notes:

```text
[ ] adjacency list
[ ] DFS
[ ] BFS
[ ] connected components
[ ] cycle detection
[ ] bipartite check
[ ] topological sort
[ ] multi-source BFS
[ ] 0-1 BFS
[ ] Dijkstra
[ ] Bellman-Ford
[ ] Floyd-Warshall
[ ] DSU
[ ] Kruskal
[ ] tree traversal
[ ] diameter
[ ] subtree size
[ ] SCC
[ ] bridges
[ ] articulation points
```

More importantly, you should be able to see an unseen problem and decide:

```text
Graph type
+
Target property
+
Correct algorithm family
```

before coding.

---

# 21. Final Mental Model

Do not memorize graph algorithms as one huge list.

Think in layers:

```text
LAYER 0
Represent the graph
        ↓

LAYER 1
Traverse it
DFS / BFS
        ↓

LAYER 2
Recognize local/global structure
components / cycles / bipartite
        ↓

LAYER 3
Handle direction/dependencies
Topological / SCC
        ↓

LAYER 4
Compute distances
BFS / 0-1 BFS / Dijkstra /
Bellman-Ford / Floyd-Warshall
        ↓

LAYER 5
Build connectivity structures
DSU / MST
        ↓

LAYER 6
Exploit tree structure
Diameter / Subtree / Centroid /
Euler Tour / Tree DP / Rerooting
        ↓

LAYER 7
Find critical connectivity
Bridges / Articulation / Block-Cut
        ↓

LAYER 8
Special traversal requirements
Eulerian / Hamiltonian
        ↓

LAYER 9
Advanced optimization
Max Flow / Min-Cost Max-Flow
```

The overall progression is:

```text
REPRESENT
→ TRAVERSE
→ RECOGNIZE STRUCTURE
→ COMPUTE DISTANCE
→ OPTIMIZE CONNECTIONS
→ EXPLOIT TREE/DAG STRUCTURE
→ HANDLE CRITICAL CONNECTIVITY
→ HANDLE SPECIAL TOURS / FLOW
```

That is the graph roadmap.
