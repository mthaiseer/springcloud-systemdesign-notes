# Graph Foundations — Introduction to Graphs

> **Self-study note based on the TLE Graphs Beginner lecture: _Introduction to Graphs_.**  
> **Goal:** understand the language of graphs before learning BFS, DFS, shortest paths, DSU, MST, SCC, bridges, articulation points, and trees.
>
> **Note style:** prerequisite refresher → intuition → formal idea → small dry run → C++17 → complexity → recognition.
>
> Sections marked **Added prerequisite** are short self-study additions for clarity; the core lecture coverage follows the uploaded TLE PDF.

---

## Table of Contents

1. [Before Graphs: Prerequisites](#1-before-graphs-prerequisites)
2. [What Is a Graph?](#2-what-is-a-graph)
3. [Vertices, Edges and Graph Notation](#3-vertices-edges-and-graph-notation)
4. [Types of Graphs](#4-types-of-graphs)
   - [Directed vs Undirected](#41-directed-vs-undirected)
   - [Weighted vs Unweighted](#42-weighted-vs-unweighted)
   - [Cyclic vs Acyclic](#43-cyclic-vs-acyclic)
   - [Connected vs Disconnected](#44-connected-vs-disconnected)
   - [Complete Graph](#45-complete-graph)
5. [Common Graph Terminology](#5-common-graph-terminology)
   - [Neighbour and Degree](#51-neighbour-and-degree)
   - [In-degree and Out-degree](#52-in-degree-and-out-degree)
   - [Self Loop](#53-self-loop)
   - [Walk](#54-walk)
   - [Trail](#55-trail)
   - [Path / Simple Path](#56-path--simple-path)
   - [Circuit](#57-circuit)
   - [Cycle](#58-cycle)
   - [Connected Component](#59-connected-component)
   - [Articulation Point](#510-articulation-point)
   - [Bridge](#511-bridge)
6. [Important Graph Results](#6-important-graph-results)
7. [Graph Representation](#7-graph-representation)
   - [Adjacency Matrix](#71-adjacency-matrix)
   - [Adjacency List with Vector](#72-adjacency-list-with-vector)
   - [Adjacency List with Set](#73-adjacency-list-with-set)
8. [Directed and Weighted Graph Representation](#8-directed-and-weighted-graph-representation)
9. [Dynamic Graph Query Problem](#9-dynamic-graph-query-problem)
10. [Trade-off Analysis](#10-trade-off-analysis)
11. [How to Choose the Representation](#11-how-to-choose-the-representation)
12. [Common Mistakes](#12-common-mistakes)
13. [Recognition Checklist](#13-recognition-checklist)
14. [Final Mental Model](#14-final-mental-model)

---

# 1. Before Graphs: Prerequisites

## 1.1 Arrays and indices — **Added prerequisite**

Most competitive-programming graphs number vertices like:

```text
0, 1, 2, 3, ..., n-1
```

or:

```text
1, 2, 3, ..., n
```

So a graph often becomes an array where each index represents one vertex.

Example:

```cpp
vector<int> degree(n + 1);
```

Meaning:

```text
degree[1] = degree of vertex 1
degree[2] = degree of vertex 2
...
```

---

## 1.2 `vector` — **Added prerequisite**

A vector is a dynamic array.

```cpp
vector<int> neighbours;
neighbours.push_back(4);
neighbours.push_back(7);
```

Now:

```text
neighbours = [4, 7]
```

For graphs we often need **one neighbour-list for every vertex**:

```cpp
vector<vector<int>> adj(n + 1);
```

Think:

```text
adj[1] = all neighbours of vertex 1
adj[2] = all neighbours of vertex 2
...
```

---

## 1.3 `set` — **Added prerequisite**

A `set` stores unique values in sorted order.

```cpp
set<int> neighbours;
neighbours.insert(5);
neighbours.insert(2);
neighbours.insert(5);
```

Result:

```text
{2, 5}
```

Useful when a graph needs frequent:

- edge insertion,
- edge deletion,
- edge existence checks.

---

## 1.4 Big-O needed for this chapter — **Added prerequisite**

You only need these ideas:

```text
O(1)       → constant work
O(log n)   → very small growth
O(n)       → scan through n items
O(n²)      → n × n table
O(V + E)   → touch every vertex and every edge
```

Here:

```text
V = number of vertices
E = number of edges
```

---

## 1.5 Pair — **Added prerequisite**

For weighted graphs we frequently store:

```cpp
pair<int, int>
```

Example:

```text
(to, weight)
```

```cpp
adj[1].push_back({3, 7});
```

means:

```text
1 ----7---- 3
```

---

# 2. What Is a Graph?

A graph is a collection of:

```text
VERTICES + EDGES
```

A child-simple analogy:

```text
Vertices = houses
Edges    = roads between houses
```

Example:

```text
1 ----- 2
|       |
|       |
3 ----- 4
```

Here:

```text
Vertices = {1, 2, 3, 4}
Edges    = {(1,2), (1,3), (2,4), (3,4)}
```

Graphs model relationships.

Examples:

```text
People       → friendship graph
Cities       → road graph
Web pages    → hyperlink graph
Courses      → prerequisite graph
Computers    → network graph
Cells        → grid graph
States       → state-transition graph
```

The important shift is:

```text
Do not ask: "Does the problem say graph?"
Ask:        "Are there objects and relationships between them?"
```

---

# 3. Vertices, Edges and Graph Notation

A graph is commonly written as:

```text
G = (V, E)
```

where:

```text
V = set of vertices
E = set of edges
```

Example:

```text
V = {1, 2, 3, 4}
E = {(1,2), (2,3), (3,4)}
```

ASCII picture:

```text
1 ----- 2 ----- 3 ----- 4
```

## 3.1 Number of vertices

Usually written as:

```text
n = |V|
```

## 3.2 Number of edges

Usually written as:

```text
m = |E|
```

Competitive-programming input frequently starts with:

```text
n m
```

Example:

```text
5 4
1 2
1 3
3 4
3 5
```

Meaning:

```text
5 vertices
4 edges
```

---

# 4. Types of Graphs

The lecture introduces five important classifications:

```text
1. Directed vs Undirected
2. Weighted vs Unweighted
3. Cyclic vs Acyclic
4. Connected vs Disconnected
5. Complete Graph
```

These classifications can combine.

A graph may be:

```text
undirected + weighted + connected + cyclic
```

There is no rule saying a graph belongs to only one type.

---

## 4.1 Directed vs Undirected

### Undirected graph

An edge works in **both directions**.

```text
1 ----- 2
```

means:

```text
1 can reach 2 directly
2 can reach 1 directly
```

Edge notation:

```text
{1, 2}
```

### Directed graph

The edge has a direction.

```text
1 -----> 2
```

means:

```text
1 can go to 2
```

but this does **not** automatically mean:

```text
2 can go to 1
```

### Tiny example

```text
Instagram follow:
A follows B
```

This need not mean:

```text
B follows A
```

Therefore it naturally behaves like a directed graph.

### C++ representation difference

Undirected:

```cpp
adj[u].push_back(v);
adj[v].push_back(u);
```

Directed:

```cpp
adj[u].push_back(v);
```

This tiny coding difference is extremely important.

---

## 4.2 Weighted vs Unweighted

### Unweighted graph

Edges have no special numerical cost.

```text
1 ----- 2 ----- 3
```

You can conceptually think every edge has equal cost.

### Weighted graph

Each edge has a value such as:

```text
distance
time
cost
fuel
risk
profit/loss
```

Example:

```text
1 ----5---- 2 ----2---- 3
```

The labels `5` and `2` are edge weights.

### Storage

Unweighted:

```cpp
vector<vector<int>> adj;
```

Weighted:

```cpp
vector<vector<pair<int, int>>> adj;
```

where each pair is:

```text
{neighbour, weight}
```

---

## 4.3 Cyclic vs Acyclic

### Cycle intuition

Start somewhere, follow edges, and return to the starting vertex.

```text
1 ----- 2
|       |
|       |
4 ----- 3
```

One cycle is:

```text
1 → 2 → 3 → 4 → 1
```

### Cyclic graph

Contains at least one cycle.

### Acyclic graph

Contains no cycle.

Example:

```text
1 ----- 2 ----- 3
        |
        4
```

No route can leave a vertex and return to it without retracing in a way that forms a cycle.

### Important future connection

```text
Undirected + connected + acyclic = tree
```

We will use this heavily when you start the Tree topic.

---

## 4.4 Connected vs Disconnected

For an undirected graph:

### Connected graph

Every vertex can reach every other vertex through some path.

```text
1 ----- 2 ----- 3
        |
        4
```

All vertices belong to one connected piece.

### Disconnected graph

The graph has multiple separate pieces.

```text
1 ----- 2       4 ----- 5
      
3               6
```

More clearly:

```text
Component 1: 1 ----- 2 ----- 3

Component 2: 4 ----- 5

Component 3: 6
```

So the graph has:

```text
3 connected components
```

---

## 4.5 Complete Graph

A complete graph has an edge between **every pair of distinct vertices**.

For 4 vertices:

```text
Every vertex connects to the other 3 vertices.
```

ASCII idea:

```text
1 ----- 2
| \   / |
|  \ /  |
|  / \  |
| /   \ |
4 ----- 3
```

### Added prerequisite: number of edges

For `n` vertices, every unordered pair contributes one edge:

```text
number of edges = C(n, 2)
                = n(n - 1) / 2
```

Example:

```text
n = 4
edges = 4×3/2 = 6
```

This is useful for constraints because a dense graph can have about `n²` edges.

---

# 5. Common Graph Terminology

The lecture introduces:

```text
Neighbours and Degree
Self Loop
Path and Walk
Cycle
Simple Path
Articulation Points
Bridges
```

It also distinguishes walk, trail, circuit, path and cycle.

---

## 5.1 Neighbour and Degree

Consider:

```text
    2
    |
1---3---4
    |
    5
```

Neighbours of `3`:

```text
{1, 2, 4, 5}
```

Therefore:

```text
degree(3) = 4
```

For an undirected graph:

```text
degree(v) = number of edges touching v
```

In an adjacency list:

```cpp
adj[v].size()
```

usually gives its degree if parallel edges and self-loop conventions are not complicating the problem.

---

## 5.2 In-degree and Out-degree

**Added prerequisite for directed graphs.**

Consider:

```text
1 -----> 3 <----- 2
          |
          v
          4
```

For vertex `3`:

```text
in-degree  = 2
out-degree = 1
```

Meaning:

```text
in-degree  = number of incoming directed edges
out-degree = number of outgoing directed edges
```

These become important in:

- topological sorting,
- DAG problems,
- directed cycle problems.

---

## 5.3 Self Loop

A self loop is an edge from a vertex to itself.

```text
   ┌───┐
   │   |
   v   |
   2───┘
```

Equivalent notation:

```text
(2, 2)
```

The lecture shows a graph where vertex `2` has a self loop.

Self loops matter because they can immediately create cycle-like behaviour depending on the problem definition.

---

## 5.4 Walk

The lecture's hierarchy begins with a **walk**.

A walk is any route along edges.

Vertices may repeat.
Edges may repeat.

Lecture example:

```text
0 → 4 → 0 → 2 → 4 → 5
```

Notice vertex `0` appears more than once.

That is allowed in a walk.

### Mental model

```text
Walk = "just keep walking along valid edges"
```

---

## 5.5 Trail

A trail is a walk with **no repeated edges**.

Vertices can repeat, but an edge cannot be reused.

Lecture example:

```text
0 → 4 → 5 → 0 → 2 → 4
```

The important restriction is:

```text
same EDGE cannot appear twice
```

### Relation

```text
Every trail is a walk.
Not every walk is a trail.
```

---

## 5.6 Path / Simple Path

The lecture defines a path as a trail with **no repeated vertices**.

Lecture example:

```text
3 → 2 → 0 → 4 → 5 → 1
```

Each vertex appears once.

### Mental model

```text
Path = route without revisiting vertices
```

### Relation

```text
Path ⊂ Trail ⊂ Walk
```

ASCII:

```text
WALK
└── TRAIL  (no repeated edges)
    └── PATH (no repeated vertices)
```

---

## 5.7 Circuit

A circuit is a **closed trail**.

Closed means:

```text
start vertex = end vertex
```

and because it is a trail:

```text
no edge repeats
```

Mental model:

```text
Circuit = trail that returns home
```

---

## 5.8 Cycle

The lecture presents a cycle as a **closed path**.

Lecture example:

```text
2 → 0 → 5 → 1 → 4 → 2
```

Start:

```text
2
```

End:

```text
2
```

The intermediate vertices do not repeat.

### Relation

```text
PATH
└── CYCLE when the path closes back to its start
```

A useful combined picture:

```text
Walk
├── Trail
│   ├── Path
│   │   └── Cycle = closed path
│   └── Circuit = closed trail
```

---

## 5.9 Connected Component

**Added prerequisite because it is used repeatedly by the lecture definitions.**

A connected component is one maximal connected piece of an undirected graph.

Example:

```text
1---2---3      4---5       6
```

Components:

```text
{1,2,3}
{4,5}
{6}
```

Total:

```text
3 connected components
```

This idea becomes central in:

- DFS/BFS,
- DSU,
- bridges,
- articulation points,
- MST.

---

## 5.10 Articulation Point

The lecture definition:

> A vertex is an articulation point if removing that vertex increases the number of connected components.

Example:

```text
1 ----- 2 ----- 3
```

Before removing `2`:

```text
1 component
```

Remove `2`:

```text
1       3
```

Now:

```text
2 components
```

Therefore:

```text
2 is an articulation point
```

### Important

This chapter only introduces the concept.

The efficient DFS/low-link algorithm should be studied separately when the corresponding graph topic appears.

---

## 5.11 Bridge

The lecture definition:

> An edge is a bridge if removing that edge increases the number of connected components.

Example:

```text
1 ----- 2 ----- 3
```

Remove edge `(2,3)`:

```text
1 ----- 2       3
```

Components increase from:

```text
1 → 2
```

So:

```text
(2,3) is a bridge
```

### Compare

```text
Articulation Point → remove a VERTEX
Bridge             → remove an EDGE
```

---

# 6. Important Graph Results

The lecture lists three useful results.

---

## 6.1 Undirected graph: minimum degree at least 2 implies a cycle

Lecture result:

```text
If every vertex of an undirected graph has degree at least 2,
then the graph contains a cycle.
```

### Intuition

Start walking from any vertex.

Because every vertex has at least two incident edges, you cannot keep moving forever through completely new vertices in a finite graph.

Eventually you must revisit an earlier vertex.

That creates a cycle.

### Another lecture-style viewpoint

A connected acyclic graph is a tree.

For `n` vertices, a tree has:

```text
n - 1 edges
```

If we force enough connectivity/degree that the structure needs an extra edge, that extra edge closes a loop.

Example:

```text
1 ----- 2 ----- 3 ----- 4
```

This is acyclic.

Add:

```text
4 ----- 1
```

Now:

```text
1 ----- 2
|       |
4 ----- 3
```

A cycle appears.

---

## 6.2 Directed graph: in-degree ≥ 1 and out-degree ≥ 1 for every vertex implies a cycle

Lecture result:

```text
If every vertex has at least one incoming edge
and at least one outgoing edge,
then the directed graph contains a cycle.
```

### Intuition

Pick any vertex and repeatedly follow an outgoing edge.

```text
v1 → v2 → v3 → v4 → ...
```

The graph has finitely many vertices.

Therefore eventually some vertex must repeat.

Example:

```text
1 → 2 → 3 → 1
```

The repeated vertex gives a directed cycle.

---

## 6.3 Sum of degrees in an undirected graph is even

Lecture result:

```text
sum(degree(v)) = 2E
```

This is commonly called the handshaking idea.

### Why?

Take one undirected edge:

```text
u ----- v
```

It contributes:

```text
+1 degree to u
+1 degree to v
```

Total contribution:

```text
2
```

For `E` edges:

```text
sum of all degrees = 2E
```

Since `2E` is even:

```text
sum of all degrees is always even
```

### Small dry run

Graph:

```text
1 ----- 2
 \     /
   \ /
    3
```

Degrees:

```text
deg(1) = 2
deg(2) = 2
deg(3) = 2
```

Sum:

```text
2 + 2 + 2 = 6
```

Edges:

```text
E = 3
```

Check:

```text
2E = 2×3 = 6
```

Correct.

---

# 7. Graph Representation

The lecture compares three representations:

```text
1. Adjacency Matrix
2. Adjacency List with Vector
3. Adjacency List with Set
```

The same graph can be stored in different ways depending on what operations the problem needs.

Suppose the graph is:

```text
0 ----- 2 ----- 3
|     / |
|   /   |
4 ----- |
|
1
```

Conceptually the lecture example has neighbour lists like:

```text
0 : 2, 4
1 : 4
2 : 0, 3, 4
3 : 2
4 : 0, 1, 2
```

---

## 7.1 Adjacency Matrix

Create an `n × n` table.

```text
matrix[u][v] = 1  → edge exists
matrix[u][v] = 0  → edge does not exist
```

Example:

```text
    0 1 2 3 4
  +-----------
0 | 0 0 1 0 1
1 | 0 0 0 0 1
2 | 1 0 0 1 1
3 | 0 0 1 0 0
4 | 1 1 1 0 0
```

For an undirected graph the matrix is symmetric:

```text
matrix[u][v] = matrix[v][u]
```

### C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<vector<int>> adj(n, vector<int>(n, 0));

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;

        adj[u][v] = 1;
        adj[v][u] = 1;
    }

    return 0;
}
```

### Operations

Check edge `(u,v)`:

```cpp
if (adj[u][v]) {
    cout << "Edge exists\n";
}
```

Add:

```cpp
adj[u][v] = 1;
adj[v][u] = 1;
```

Remove:

```cpp
adj[u][v] = 0;
adj[v][u] = 0;
```

### Main strength

```text
Very fast edge lookup: O(1)
```

### Main weakness

```text
Space = O(n²)
```

Even if the graph has very few edges, the whole table exists.

---

## 7.2 Adjacency List with Vector

Instead of storing every possible pair, store only actual neighbours.

```text
0 → [2, 4]
1 → [4]
2 → [0, 3, 4]
3 → [2]
4 → [0, 1, 2]
```

### C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<vector<int>> adj(n);

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;

        adj[u].push_back(v);
        adj[v].push_back(u);
    }

    return 0;
}
```

### Add edge

```cpp
adj[u].push_back(v);
adj[v].push_back(u);
```

Approximate cost:

```text
O(1) amortized
```

### Search edge

Need to scan the neighbours of `u`:

```cpp
bool exists = false;

for (int v : adj[u]) {
    if (v == target) {
        exists = true;
        break;
    }
}
```

Worst case:

```text
O(n)
```

or more precisely:

```text
O(degree(u))
```

### Number of neighbours

```cpp
cout << adj[u].size();
```

Cost:

```text
O(1)
```

### Main strength

For sparse graphs:

```text
Space = O(V + E)
```

The lecture table simplifies this to `O(edges)` for the edge storage itself.

---

## 7.3 Adjacency List with Set

Use a `set` instead of a vector for every vertex.

```cpp
vector<set<int>> adj(n);
```

### C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<set<int>> adj(n);

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;

        adj[u].insert(v);
        adj[v].insert(u);
    }

    return 0;
}
```

### Add

```cpp
adj[u].insert(v);
adj[v].insert(u);
```

### Remove

```cpp
adj[u].erase(v);
adj[v].erase(u);
```

### Search

```cpp
if (adj[u].count(v)) {
    cout << "Edge exists\n";
}
```

### Number of neighbours

```cpp
cout << adj[u].size();
```

### Why use set?

Compared with vector:

```text
search  → faster asymptotically
remove  → faster asymptotically
```

but insertion is no longer plain `O(1)`.

---

# 8. Directed and Weighted Graph Representation

This subsection is an **added prerequisite extension** so the three core representations are usable in later graph chapters.

---

## 8.1 Directed adjacency list

Input edge:

```text
u → v
```

Store only:

```cpp
adj[u].push_back(v);
```

Do not automatically write:

```cpp
adj[v].push_back(u);
```

unless the reverse edge truly exists.

---

## 8.2 Weighted adjacency list

Graph:

```text
1 ----7---- 2
```

Store:

```cpp
vector<vector<pair<int, int>>> adj(n + 1);
```

Add undirected weighted edge:

```cpp
adj[u].push_back({v, weight});
adj[v].push_back({u, weight});
```

Traverse:

```cpp
for (auto [next, weight] : adj[node]) {
    cout << next << ' ' << weight << '\n';
}
```

This representation becomes standard for:

- Dijkstra,
- Bellman-Ford input storage,
- Prim's MST,
- weighted DFS/BFS-style modelling.

---

# 9. Dynamic Graph Query Problem

The lecture asks us to consider a graph with:

```text
N vertices
M initial edges
Q queries
```

Query types:

```text
1 i j → add edge i-j
2 i j → remove edge i-j
3 i j → print whether edge i-j exists
4 i   → print number of neighbours of i
```

This is not just a coding exercise.

The purpose is to ask:

```text
Which graph representation fits the operations?
```

---

## 9.1 Option A — Adjacency matrix

### Add

```text
O(1)
```

### Remove

```text
O(1)
```

### Search

```text
O(1)
```

### Count neighbours

Need to scan an entire row:

```text
O(n)
```

### Problem

Space:

```text
O(n²)
```

Large `n` may make it impossible.

---

## 9.2 Option B — Vector adjacency list

### Add

```text
O(1) amortized
```

### Remove

Need to find the neighbour in the vector:

```text
O(n)
```

### Search

```text
O(n)
```

### Number of neighbours

```text
O(1)
```

because:

```cpp
adj[i].size()
```

is constant time.

---

## 9.3 Option C — Set adjacency list

### Add

```text
O(log n)
```

### Remove

```text
O(log n)
```

### Search

```text
O(log n)
```

### Number of neighbours

```text
O(1)
```

because the container tracks its size.

---

## 9.4 Clean C++17 solution using `set`

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<set<int>> adj(n + 1);

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;
        adj[u].insert(v);
        adj[v].insert(u);
    }

    int q;
    cin >> q;

    while (q--) {
        int type;
        cin >> type;

        if (type == 1) {
            int u, v;
            cin >> u >> v;
            adj[u].insert(v);
            adj[v].insert(u);
        } else if (type == 2) {
            int u, v;
            cin >> u >> v;
            adj[u].erase(v);
            adj[v].erase(u);
        } else if (type == 3) {
            int u, v;
            cin >> u >> v;
            cout << (adj[u].count(v) ? "YES" : "NO") << '\n';
        } else if (type == 4) {
            int u;
            cin >> u;
            cout << adj[u].size() << '\n';
        }
    }

    return 0;
}
```

---

# 10. Trade-off Analysis

The lecture's comparison table can be remembered like this:

| Operation | Adjacency Matrix | Adj. List + Vector | Adj. List + Set |
|---|---:|---:|---:|
| Space | `O(n²)` | `O(E)` edge storage | `O(E)` edge storage |
| Add edge | `O(1)` | `O(1)` | `O(log n)` |
| Remove edge | `O(1)` | `O(n)` | `O(log n)` |
| Search edge | `O(1)` | `O(n)` | `O(log n)` |
| Number of neighbours | `O(n)` | `O(1)` | `O(1)` |

More precise CP thinking:

```text
Vector search/remove = O(degree(u))
Set operations       = O(log degree(u))
```

but the lecture uses the simpler worst-case `n` notation.

---

# 11. How to Choose the Representation

Do not memorize only one representation.

Ask what the problem repeatedly needs.

## Case 1 — Need very fast edge existence checks

```text
"Does u connect to v?"
```

and `n` is small enough:

```text
Adjacency Matrix
```

---

## Case 2 — Mostly traverse neighbours

Typical BFS/DFS problem:

```text
for every neighbour of u ...
```

Use:

```text
Adjacency List + Vector
```

This is the default CP representation for sparse graphs.

---

## Case 3 — Frequent add/remove/search queries

Use:

```text
Adjacency List + Set
```

especially when ordered unique neighbours are acceptable.

---

## Decision table

| Problem clue | Good first choice |
|---|---|
| BFS / DFS | `vector<vector<int>>` |
| Sparse graph | adjacency list |
| Dense graph, small `n` | adjacency matrix may be fine |
| Fast `edge(u,v)` query | matrix / set |
| Frequent deletion | set |
| Weighted graph | `vector<vector<pair<int,int>>>` |
| Need duplicate edges | vector is usually simpler |

---

# 12. Common Mistakes

## Mistake 1 — Forgetting reverse edge in an undirected graph

Wrong:

```cpp
adj[u].push_back(v);
```

Correct:

```cpp
adj[u].push_back(v);
adj[v].push_back(u);
```

---

## Mistake 2 — Adding reverse edge in a directed graph

For:

```text
u → v
```

usually store only:

```cpp
adj[u].push_back(v);
```

---

## Mistake 3 — Confusing walk, trail and path

Remember:

```text
Walk  → repetition allowed
Trail → no repeated edges
Path  → no repeated vertices
```

---

## Mistake 4 — Confusing cycle and circuit

```text
Circuit → closed trail
Cycle   → closed path
```

---

## Mistake 5 — Confusing bridge and articulation point

```text
Bridge             → important EDGE
Articulation point → important VERTEX
```

Both are defined using an increase in connected components after removal.

---

## Mistake 6 — Automatically using adjacency matrix

For:

```text
n = 100000
```

an `n × n` matrix is impossible in normal CP memory limits.

Think about graph density and required operations first.

---

## Mistake 7 — Thinking graph means "draw circles and lines"

In programming, the picture is only for understanding.

The actual graph is usually stored as:

```text
matrix
adjacency list
edge list
```

Later algorithms operate on those structures.

---

# 13. Recognition Checklist

When reading a new problem, ask in this order:

### Step 1 — What are the objects?

These may become vertices.

Examples:

```text
cities
people
cells
numbers
strings
states
courses
```

### Step 2 — What is the relationship/action?

This may become an edge.

Examples:

```text
road
friendship
one operation
transition
can travel
prerequisite
```

### Step 3 — Directed or undirected?

Ask:

```text
If u can go to v, can v automatically go to u?
```

If yes:

```text
undirected
```

Otherwise:

```text
directed
```

### Step 4 — Weighted or unweighted?

Ask:

```text
Does each connection have a cost/value?
```

### Step 5 — Static or dynamic?

```text
Static  → edges do not change
Dynamic → edges are inserted/deleted
```

### Step 6 — What operations dominate?

```text
Traverse neighbours?
Search edge?
Add/remove edge?
Count neighbours?
```

Then choose the representation.

---

# 14. Final Mental Model

Graph fundamentals can be compressed into this picture:

```text
REAL PROBLEM
    |
    v
Objects + Relationships
    |
    v
Graph G = (V, E)
    |
    +--> Directed / Undirected
    +--> Weighted / Unweighted
    +--> Cyclic / Acyclic
    +--> Connected / Disconnected
    |
    v
Choose representation
    |
    +--> Matrix
    +--> Vector adjacency list
    +--> Set adjacency list
    |
    v
Run later graph algorithms
    |
    +--> BFS / DFS
    +--> shortest path
    +--> topological sort
    +--> DSU
    +--> MST
    +--> bridges / articulation points
    +--> SCC
    +--> trees
```

## One-line memory rules

```text
Graph = vertices + edges.
Undirected edge works both ways.
Directed edge has a direction.
Weighted edge carries a value.
Cycle returns to the starting point.
Connected means every vertex belongs to the same reachable piece.
Walk allows repetition.
Trail forbids repeated edges.
Path forbids repeated vertices.
Bridge = critical edge.
Articulation point = critical vertex.
Adjacency matrix = fast lookup, expensive space.
Vector adjacency list = CP default for traversal.
Set adjacency list = useful for dynamic edge operations.
```

---

## Practice before moving forward

You should be able to answer these without looking at the note:

1. What are `V` and `E` in `G = (V,E)`?
2. What changes in code between directed and undirected edges?
3. What is the difference between weighted and unweighted graphs?
4. What makes a graph cyclic?
5. What is a connected component?
6. Walk vs trail vs path?
7. Cycle vs circuit?
8. Bridge vs articulation point?
9. Why is the sum of degrees in an undirected graph equal to `2E`?
10. Matrix vs vector adjacency list vs set adjacency list — when would you choose each?

If these are clear, you are ready for graph traversal and the next TLE graph lecture.
