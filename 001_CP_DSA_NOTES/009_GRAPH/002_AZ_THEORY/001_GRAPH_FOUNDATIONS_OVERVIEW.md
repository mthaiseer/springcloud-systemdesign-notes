# Graph Foundations — Complete Overview
## Nodes, Edges, Degree, Paths, Reachability, Components, DAG, Tree, Bipartite, Hamiltonian & Eulerian
### TLE-Style Self-Study Notes with Prerequisites, ASCII Diagrams, Dry Runs, and C++17

> **Source scope**
>
> These notes are built from the supplied AlgoZenith **Graph Overview / Introduction to Graph** lecture screenshots.
>
> The lecture moves from vertices and edges into graph classification, degree, paths, reachability, connected components, strongly connected components, multigraph/simple graph, subgraphs, DAGs, trees, bipartite graphs, Hamiltonian paths, and Eulerian paths.
>
> Sections marked **Prerequisite Expansion** add implementation details needed to solve graph problems in C++.

---

# Table of Contents

1. [What Is a Graph?](#1-what-is-a-graph)
2. [Why Graphs Matter](#2-why-graphs-matter)
3. [Prerequisite — Sets, Pairs, and Notation](#3-prerequisite--sets-pairs-and-notation)
4. [Vertices and Edges](#4-vertices-and-edges)
5. [Formal Definition: G = (V, E)](#5-formal-definition-g--v-e)
6. [Graph Classification](#6-graph-classification)
7. [Weighted vs Unweighted Graph](#7-weighted-vs-unweighted-graph)
8. [Directed vs Undirected Graph](#8-directed-vs-undirected-graph)
9. [Degree in an Undirected Graph](#9-degree-in-an-undirected-graph)
10. [Handshake Lemma](#10-handshake-lemma)
11. [Indegree and Outdegree](#11-indegree-and-outdegree)
12. [Minimum and Maximum Number of Edges](#12-minimum-and-maximum-number-of-edges)
13. [Path](#13-path)
14. [Walk, Trail, Path, Cycle](#14-walk-trail-path-cycle)
15. [Reachability](#15-reachability)
16. [Connected Graph](#16-connected-graph)
17. [Connected Components](#17-connected-components)
18. [Strongly Connected Components](#18-strongly-connected-components)
19. [Weak Connectivity](#19-weak-connectivity)
20. [Self Loop](#20-self-loop)
21. [Multiple Edges and Multigraph](#21-multiple-edges-and-multigraph)
22. [Simple Graph](#22-simple-graph)
23. [Subgraph](#23-subgraph)
24. [Vertex-Induced Subgraph](#24-vertex-induced-subgraph)
25. [Edge-Induced Subgraph](#25-edge-induced-subgraph)
26. [DAG — Directed Acyclic Graph](#26-dag--directed-acyclic-graph)
27. [Tree](#27-tree)
28. [Rooted Tree](#28-rooted-tree)
29. [Bipartite Graph](#29-bipartite-graph)
30. [Hamiltonian Path and Cycle](#30-hamiltonian-path-and-cycle)
31. [Eulerian Path and Cycle](#31-eulerian-path-and-cycle)
32. [Hamiltonian vs Eulerian](#32-hamiltonian-vs-eulerian)
33. [Prerequisite Expansion — Graph Representation](#33-prerequisite-expansion--graph-representation)
34. [Adjacency Matrix](#34-adjacency-matrix)
35. [Adjacency List](#35-adjacency-list)
36. [Weighted Adjacency List](#36-weighted-adjacency-list)
37. [Complexity of Representations](#37-complexity-of-representations)
38. [Graph Traversal Preview](#38-graph-traversal-preview)
39. [Common Mistakes](#39-common-mistakes)
40. [Recognition Checklist](#40-recognition-checklist)
41. [Final Mental Model](#41-final-mental-model)
42. [One-Minute Revision](#42-one-minute-revision)

---

# 1. What Is a Graph?

A graph represents:

```text
objects
+
relationships between objects
```

Objects are:

```text
vertices / nodes
```

Relationships are:

```text
edges
```

Example:

```text
A ----- B
 \     /
  \   /
    C
```

```text
Vertices = {A, B, C}
Edges    = {A-B, A-C, B-C}
```

---

# 2. Why Graphs Matter

Many systems naturally become graphs.

```text
People       = vertices
Friendships  = edges
```

```text
Cities       = vertices
Roads        = edges
```

```text
Web pages    = vertices
Hyperlinks   = directed edges
```

```text
Courses      = vertices
Prerequisite = directed edge
```

Main question:

```text
What is connected to what?
```

---

# 3. Prerequisite — Sets, Pairs, and Notation

A graph is written using two sets:

```text
V = set of vertices
E = set of edges
```

Example:

```text
V = {1,2,3,4}
```

Undirected:

```text
E = {
    {1,2},
    {2,3},
    {3,4}
}
```

Directed:

```text
E = {
    (1,2),
    (2,3),
    (3,1)
}
```

Important:

```text
Undirected:
{1,2} = {2,1}
```

```text
Directed:
(1,2) != (2,1)
```

---

# 4. Vertices and Edges

```text
1 ----- 2
        |
        |
        3
```

```text
V = {1,2,3}
```

```text
E = {
    {1,2},
    {2,3}
}
```

An edge says that the two corresponding objects have some relation.

---

# 5. Formal Definition: G = (V, E)

A graph is formally:

```text
G = (V,E)
```

Example:

```text
        2
       / \
      1---3
```

Then:

```text
V = {1,2,3}
```

```text
E = {
    {1,2},
    {2,3},
    {1,3}
}
```

---

# 6. Graph Classification

The lecture first classifies graphs using two independent properties.

```text
Graph
├── Edge Direction
│   ├── Directed
│   └── Undirected
│
└── Edge Weight
    ├── Weighted
    └── Unweighted
```

Possible combinations:

```text
directed + weighted
directed + unweighted
undirected + weighted
undirected + unweighted
```

---

# 7. Weighted vs Unweighted Graph

## Unweighted

```text
1 ----- 2
```

The edge means only:

```text
connection exists
```

## Weighted

```text
1 --5-- 2
```

The value `5` can mean:

```text
distance
cost
time
capacity
risk
```

Example:

```text
1 --7-- 2
2 --2-- 3
1 --9-- 3
```

Mental model:

```text
Unweighted:
Can I go from u to v?
```

```text
Weighted:
I can go from u to v, and the edge has value w.
```

---

# 8. Directed vs Undirected Graph

## Undirected

```text
u ----- v
```

Connection works both ways.

Equivalent conceptual movement:

```text
u -> v
v -> u
```

## Directed

```text
u -----> v
```

Only guarantees:

```text
u can go to v
```

not necessarily:

```text
v can go to u
```

Example:

```text
1 -> 2 -> 3
```

Then:

```text
1 reaches 2
1 reaches 3
```

but `3` may not reach `1`.

---

# 9. Degree in an Undirected Graph

Degree:

```text
number of incident edges
```

Example:

```text
      2
     / \
    1---3---4
```

```text
deg(1)=2
deg(2)=2
deg(3)=3
deg(4)=1
```

Minimum degree:

```text
0
```

Maximum degree in a simple undirected graph with `N` vertices:

```text
N - 1
```

---

# 10. Handshake Lemma

For every undirected graph:

```text
Σ deg(v) = 2|E|
```

Why?

Each edge:

```text
u ----- v
```

contributes:

```text
+1 to deg(u)
+1 to deg(v)
```

So every edge contributes exactly `2`.

Example triangle:

```text
    1
   / \
  2---3
```

```text
deg(1)=2
deg(2)=2
deg(3)=2
```

```text
sum = 6
```

```text
|E|=3
2|E|=6
```

Correct.

---

# 11. Indegree and Outdegree

For directed graphs:

```text
indegree(v)
=
number of edges entering v
```

```text
outdegree(v)
=
number of edges leaving v
```

Example:

```text
1 --> 2 --> 3
      ^
      |
      4
```

For vertex `2`:

```text
indegree(2)=2
outdegree(2)=1
```

Important formulas:

```text
Σ indegree(v) = |E|
Σ outdegree(v) = |E|
```

---

# 12. Minimum and Maximum Number of Edges

For a simple undirected graph with `N` vertices:

```text
minimum = 0
```

Maximum:

```text
C(N,2)
=
N(N-1)/2
```

because every unordered pair can have at most one edge.

If the graph must be connected:

```text
minimum connected edges = N - 1
```

---

# 13. Path

A path is a sequence of vertices where consecutive vertices are connected.

```text
1 ----- 2 ----- 3 ----- 4
```

Path:

```text
1 -> 2 -> 3 -> 4
```

For directed graphs, direction must be respected.

---

# 14. Walk, Trail, Path, Cycle

## Walk

Vertices and edges may repeat.

```text
1 -> 2 -> 3 -> 2 -> 4
```

## Trail

Edges do not repeat.

Vertices may repeat.

## Path

Vertices do not repeat.

Therefore edges do not repeat.

## Cycle

A closed path.

```text
1 -> 2 -> 3 -> 1
```

---

# 15. Reachability

Vertex `v` is reachable from `u` iff:

```text
there exists some path from u to v
```

Example:

```text
1 -> 2 -> 3 -> 4
```

Then:

```text
4 is reachable from 1
```

Directed reachability is not necessarily symmetric.

---

# 16. Connected Graph

For an undirected graph:

```text
connected
```

means:

```text
every vertex can reach every other vertex
```

Connected:

```text
1 ----- 2
|       |
|       |
4 ----- 3
```

Disconnected:

```text
1 ----- 2

3 ----- 4
```

---

# 17. Connected Components

A connected component is:

```text
a maximal connected region of an undirected graph
```

Example:

```text
1---2---3      4---5      6
```

Components:

```text
{1,2,3}
{4,5}
{6}
```

Total:

```text
3 components
```

---

# 18. Strongly Connected Components

SCC is a directed-graph concept.

A set of vertices is strongly connected if:

```text
every vertex can reach every other vertex
```

Example SCC:

```text
1 -> 2
^    |
|    v
3 <- 4
```

If each vertex reaches all others, they belong to the same SCC.

Example with two SCCs:

```text
SCC 1                    SCC 2

1 -> 2                   4 -> 5
^    |                    ^    |
|    v                    |    v
3 <- +                    6 <- +

        3 --------> 4
```

There may be a one-way edge between SCCs without merging them.

---

# 19. Weak Connectivity

A directed graph is weakly connected if:

```text
after removing arrow directions,
the graph becomes connected
```

Example:

```text
1 -> 2 -> 3
```

Ignoring arrows:

```text
1 -- 2 -- 3
```

So it is weakly connected.

---

# 20. Self Loop

A self loop connects a vertex to itself.

```text
    ↺
    1
```

Formally:

```text
(1,1)
```

In an undirected graph, a self loop contributes `2` to the degree of that vertex.

---

# 21. Multiple Edges and Multigraph

If more than one edge can exist between the same pair:

```text
u ===== v
```

we can have a multigraph.

Example:

```text
u ---- v
u ---- v
u ---- v
```

These are parallel edges.

---

# 22. Simple Graph

A simple graph has:

```text
no self loops
no parallel edges
```

Between two different vertices:

```text
at most one edge
```

This is the default model in many algorithm problems unless stated otherwise.

---

# 23. Subgraph

If:

```text
G = (V,E)
```

then:

```text
G' = (V',E')
```

is a subgraph when:

```text
V' ⊆ V
E' ⊆ E
```

and every selected edge has both endpoints inside `V'`.

Example:

```text
Original:

1 --- 2 --- 3
      |    / \
      |   /   \
      4 ------ 5
```

Choose:

```text
V'={2,3,4}
```

and:

```text
E'={
    {2,3},
    {2,4},
    {3,4}
}
```

Subgraph:

```text
2
|\
| \
4--3
```

---

# 24. Vertex-Induced Subgraph

Choose a set of vertices `V'`.

Then include every original edge whose endpoints both belong to `V'`.

Mental rule:

```text
Choose vertices first.
Edges are forced.
```

---

# 25. Edge-Induced Subgraph

Choose a set of edges `E'`.

The resulting graph contains the endpoints of those selected edges.

Mental rule:

```text
Choose edges first.
Vertices come from those edges.
```

---

# 26. DAG — Directed Acyclic Graph

DAG:

```text
Directed Acyclic Graph
```

Requirements:

```text
1. directed
2. no directed cycle
```

Example:

```text
1 -> 2 -> 4
 \       ^
  -> 3 --|
```

No path following arrows returns to the same starting vertex.

So it is a DAG.

Non-DAG:

```text
1 -> 2
^    |
|    v
4 <- 3
```

Cycle:

```text
1 -> 2 -> 3 -> 4 -> 1
```

So not a DAG.

---

# 27. Tree

A tree is:

```text
undirected
connected
acyclic
```

Example:

```text
        1
      /   \
     2     3
    / \     \
   4   5     6
```

For `N` vertices:

```text
|E| = N - 1
```

Other equivalent properties:

```text
exactly one simple path between every pair
```

```text
adding one edge creates one cycle
```

```text
removing any edge disconnects the tree
```

---

# 28. Rooted Tree

Choose one tree vertex as the root.

```text
        1   <- root
      /   \
     2     3
    / \   / \
   4   5 6   7
```

Now we can define:

```text
parent
child
ancestor
descendant
depth
subtree
```

---

# 29. Bipartite Graph

A graph is bipartite if vertices can be divided into two groups:

```text
Left
Right
```

and every edge crosses between the groups.

```text
Left              Right

1 ---------------- A
2 ---------------- B
3 ---------------- A
4 ---------------- C
```

No edge may exist inside one side.

Equivalent view:

```text
2-color the graph
so adjacent vertices have different colors
```

Key theorem:

```text
Undirected graph is bipartite
iff
it has no odd cycle
```

A triangle has length `3`, so it is not bipartite.

---

# 30. Hamiltonian Path and Cycle

Hamiltonian Path:

```text
visits every vertex exactly once
```

Example:

```text
1 -> 2 -> 3 -> 4 -> 5
```

Hamiltonian Cycle:

```text
visits every vertex exactly once
and returns to the start
```

Memory:

```text
Hamiltonian -> vertices
```

---

# 31. Eulerian Path and Cycle

Eulerian Path:

```text
uses every edge exactly once
```

Vertices may repeat.

Eulerian Cycle:

```text
uses every edge exactly once
and returns to the start
```

Memory:

```text
Eulerian -> edges
```

---

# 32. Hamiltonian vs Eulerian

| Concept | Exactly Once |
|---|---|
| Hamiltonian Path | Every vertex |
| Eulerian Path | Every edge |
| Hamiltonian Cycle | Every vertex + return |
| Eulerian Cycle | Every edge + return |

Memory:

```text
Hamiltonian -> nodes
Eulerian    -> edges
```

---

# 33. Prerequisite Expansion — Graph Representation

> The lecture screenshots point to graph representation as the next prerequisite. This section adds the standard C++ implementation needed for solving problems.

Main representations:

```text
1. adjacency matrix
2. adjacency list
```

---

# 34. Adjacency Matrix

For `N` vertices:

```text
matrix[N][N]
```

For unweighted graph:

```text
matrix[u][v] = 1
```

means an edge exists.

Example:

```text
1 --- 2
 \   /
   3
```

Matrix:

```text
    1 2 3
1   0 1 1
2   1 0 1
3   1 1 0
```

C++17:

```cpp
int n;
cin >> n;

vector<vector<int>> graph(n, vector<int>(n, 0));
```

Undirected edge:

```cpp
int u, v;
cin >> u >> v;

graph[u][v] = 1;
graph[v][u] = 1;
```

---

# 35. Adjacency List

Store only actual neighbors.

```text
1 --- 2
 \   /
   3
```

Adjacency list:

```text
1: 2 3
2: 1 3
3: 1 2
```

C++17:

```cpp
int n, m;
cin >> n >> m;

vector<vector<int>> graph(n);

for (int i = 0; i < m; i++) {
    int u, v;
    cin >> u >> v;

    graph[u].push_back(v);
    graph[v].push_back(u);
}
```

For directed graph:

```cpp
graph[u].push_back(v);
```

only.

---

# 36. Weighted Adjacency List

Store:

```text
(neighbor, weight)
```

C++17:

```cpp
int n, m;
cin >> n >> m;

vector<vector<pair<int, int>>> graph(n);

for (int i = 0; i < m; i++) {
    int u, v, weight;
    cin >> u >> v >> weight;

    graph[u].push_back({v, weight});
    graph[v].push_back({u, weight});
}
```

For directed graph, omit the reverse insertion.

---

# 37. Complexity of Representations

## Adjacency Matrix

Space:

```text
O(N^2)
```

Check edge `(u,v)`:

```text
O(1)
```

Iterate all possible neighbors of `u`:

```text
O(N)
```

## Adjacency List

Space:

```text
O(N + E)
```

Iterate neighbors:

```text
O(deg(u))
```

Usually preferred for sparse graphs.

---

# 38. Graph Traversal Preview

The next major tools are:

```text
DFS
BFS
```

They solve:

```text
reachability
connected components
cycle detection
bipartite checking
shortest path in unweighted graphs
```

Basic DFS:

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

Mental flow:

```text
visit current
→ inspect every neighbor
→ recurse into unvisited neighbor
```

---

# 39. Common Mistakes

## 1. Treating directed edges as undirected

If:

```text
u -> v
```

do not automatically add:

```text
v -> u
```

## 2. Confusing degree with indegree/outdegree

```text
Undirected -> degree
Directed   -> indegree + outdegree
```

## 3. Wrong degree formulas

Undirected:

```text
Σ degree = 2E
```

Directed:

```text
Σ indegree = E
Σ outdegree = E
```

## 4. Confusing path and reachability

```text
Path = actual sequence
Reachability = whether some path exists
```

## 5. Confusing component and SCC

```text
Connected Component -> undirected
SCC                 -> directed mutual reachability
```

## 6. Confusing Hamiltonian and Eulerian

```text
Hamiltonian -> vertices
Eulerian    -> edges
```

## 7. Calling every acyclic graph a tree

Tree must be:

```text
undirected + connected + acyclic
```

A disconnected acyclic graph is a forest.

## 8. Assuming DAG must be connected

DAG only means:

```text
directed + acyclic
```

It may be disconnected.

---

# 40. Recognition Checklist

When reading a graph problem, ask:

```text
1. Directed or undirected?
2. Weighted or unweighted?
3. Self loops possible?
4. Multiple edges possible?
5. Connected or disconnected?
6. Need path or just reachability?
7. Need every vertex or every edge?
8. Any cycle restriction?
9. Tree?
10. DAG?
11. Bipartite?
12. What are N and M?
```

Then choose representation.

Usually:

```text
Adjacency List
```

---

# 41. Final Mental Model

```text
GRAPH
│
├── Structure
│   ├── vertices
│   └── edges
│
├── Edge Properties
│   ├── directed / undirected
│   └── weighted / unweighted
│
├── Local Properties
│   ├── degree
│   ├── indegree
│   └── outdegree
│
├── Movement
│   ├── path
│   └── reachability
│
├── Global Structure
│   ├── connected components
│   ├── SCC
│   ├── DAG
│   ├── tree
│   └── bipartite
│
└── Special Traversals
    ├── Hamiltonian -> every vertex
    └── Eulerian    -> every edge
```

---

# 42. One-Minute Revision

```text
GRAPH = G(V,E)

V = vertices
E = edges

----------------------------------

CLASSIFICATION

directed / undirected
weighted / unweighted

----------------------------------

UNDIRECTED DEGREE

deg(v)
=
incident edges

Σdeg(v)=2E

----------------------------------

DIRECTED DEGREE

indegree
outdegree

Σindegree=E
Σoutdegree=E

----------------------------------

PATH

sequence of connected vertices

REACHABLE

v reachable from u
iff path u -> ... -> v exists

----------------------------------

CONNECTED COMPONENT

maximal connected part
of an undirected graph

SCC

mutual reachability
in a directed graph

----------------------------------

SELF LOOP

(v,v)

MULTIGRAPH

parallel edges allowed

SIMPLE GRAPH

no self loops
no parallel edges

----------------------------------

SUBGRAPH

V' subset V
E' subset E

----------------------------------

DAG

Directed
Acyclic
Graph

----------------------------------

TREE

Undirected
Connected
Acyclic

N vertices:
E=N-1

----------------------------------

BIPARTITE

2-colorable
no odd cycle

----------------------------------

HAMILTONIAN

every VERTEX exactly once

EULERIAN

every EDGE exactly once

----------------------------------

STANDARD STORAGE

Adjacency List

Space:
O(N+E)
```
