# Graph Modelling Intro — From Problem Statement to Graph + Algorithm

> Self-study CP foundation note based on the supplied AlgoZenith graph-modelling overview.
> Focus: problem statement → create/recognize graph → identify the graph problem → choose the correct algorithm.

---

## Table of Contents

1. [What Graph Modelling Means](#1-what-graph-modelling-means)
2. [Prerequisites](#2-prerequisites)
3. [Main Modelling Pipeline](#3-main-modelling-pipeline)
4. [Step 1 — Remove the Story](#4-step-1--remove-the-story)
5. [Step 2 — Decide the Nodes](#5-step-2--decide-the-nodes)
6. [Step 3 — Decide the Edges](#6-step-3--decide-the-edges)
7. [Step 4 — Directed or Undirected](#7-step-4--directed-or-undirected)
8. [Step 5 — Weighted or Unweighted](#8-step-5--weighted-or-unweighted)
9. [Step 6 — Identify Source/Target Scope](#9-step-6--identify-sourcetarget-scope)
10. [Step 7 — Identify the Graph Problem Family](#10-step-7--identify-the-graph-problem-family)
11. [Step 8 — Use Constraints](#11-step-8--use-constraints)
12. [Explicit vs Implicit Graphs](#12-explicit-vs-implicit-graphs)
13. [State Graphs](#13-state-graphs)
14. [Grid as a Graph](#14-grid-as-a-graph)
15. [Shortest Path Decision Tree](#15-shortest-path-decision-tree)
16. [DFS/BFS Problems](#16-dfsbfs-problems)
17. [Cycle and Bipartite Problems](#17-cycle-and-bipartite-problems)
18. [Topological Problems](#18-topological-problems)
19. [MST Problems](#19-mst-problems)
20. [Tree Problems](#20-tree-problems)
21. [Worked Example — Friendship](#21-worked-example--friendship)
22. [Worked Example — Course Prerequisites](#22-worked-example--course-prerequisites)
23. [Worked Example — Cheapest Route](#23-worked-example--cheapest-route)
24. [Worked Example — Grid Maze](#24-worked-example--grid-maze)
25. [Worked Example — Extra State](#25-worked-example--extra-state)
26. [C++17 Graph Representation](#26-c17-graph-representation)
27. [Common Modelling Mistakes](#27-common-modelling-mistakes)
28. [Contest Recognition Checklist](#28-contest-recognition-checklist)
29. [Final Mental Model](#29-final-mental-model)

---

# 1. What Graph Modelling Means

A graph problem is not always written as:

```text
You are given N vertices and M edges.
```

The statement may instead talk about:

```text
cities
people
courses
rooms
cells
machines
numbers
strings
operations
dependencies
roads
flights
```

Your job is to convert the story into:

```text
OBJECTS / STATES  →  NODES
RELATIONSHIPS     →  EDGES
COSTS             →  WEIGHTS
QUESTION          →  GRAPH PROPERTY
```

So graph modelling means:

```text
Problem Story
    ↓
Find the objects/states
    ↓
Create nodes
    ↓
Find legal relations/moves
    ↓
Create edges
    ↓
Classify the graph
    ↓
Identify what must be computed
    ↓
Choose the algorithm
```

The key question is:

```text
"What graph is hidden inside this problem?"
```

---

# 2. Prerequisites

You should know these basic terms before graph modelling.

## 2.1 Vertex / Node

A node represents one object or one state.

Examples:

```text
city
person
course
grid cell
number
configuration
```

---

## 2.2 Edge

An edge represents a direct relationship or legal move.

Examples:

```text
road
friendship
dependency
one operation
one grid move
```

---

## 2.3 Directed graph

```text
u → v
```

The relationship has direction.

Examples:

```text
one-way road
A is prerequisite of B
web link
state transition
```

---

## 2.4 Undirected graph

```text
u — v
```

The relation works both ways.

Examples:

```text
two-way road
friendship
physical connection
```

---

## 2.5 Weighted graph

An edge has a cost:

```text
u --5--> v
```

The weight can mean:

```text
distance
time
money
risk
fuel
penalty
```

---

## 2.6 Unweighted graph

All moves effectively cost the same.

Usually treat every edge as:

```text
cost = 1
```

---

## 2.7 Path

A path is a sequence of connected vertices:

```text
1 → 4 → 7 → 9
```

---

## 2.8 Cycle

A cycle returns to a previously visited vertex:

```text
1 → 2 → 3 → 1
```

---

## 2.9 Connected component

Example:

```text
1 -- 2 -- 3

4 -- 5
```

There are two components:

```text
{1,2,3}
{4,5}
```

---

## 2.10 Tree

A tree is a connected undirected graph with:

```text
V vertices
V-1 edges
no cycle
```

---

## 2.11 DAG

DAG means:

```text
Directed Acyclic Graph
```

Typical use:

```text
dependencies
course ordering
task scheduling
```

---

# 3. Main Modelling Pipeline

The lecture roadmap can be remembered as:

```text
Problem Statement
      ↓
Is a graph already given?
      ↓
If not, can I CREATE one?
      ↓
What is a node?
      ↓
What is an edge?
      ↓
Directed / undirected?
      ↓
Weighted / unweighted?
      ↓
What is the actual graph problem?
      ↓
Which algorithm fits?
```

Compact memory:

```text
Story
→ Graph
→ Graph Type
→ Required Property
→ Algorithm
```

---

# 4. Step 1 — Remove the Story

Suppose the statement says:

> Cities are connected by roads. Find the minimum travel time.

Remove the story words:

```text
city         → node
road         → edge
travel time  → edge weight
minimum time → shortest path
```

Now the mathematical problem is:

```text
weighted graph
+
source
+
target
+
minimum path cost
```

This is much easier to reason about than the original story.

---

# 5. Step 2 — Decide the Nodes

Ask:

```text
"What information completely describes my current position/state?"
```

A node may be:

```text
person
city
course
cell
number
string
machine state
```

Sometimes one physical object creates multiple graph states.

Example:

```text
(city, couponUnused)
(city, couponUsed)
```

These must be treated as different nodes if future moves differ.

---

# 6. Step 3 — Decide the Edges

Ask:

```text
"What is ONE legal move or direct relation?"
```

Examples:

```text
road between cities
friendship
course dependency
move to adjacent cell
x → x+1
x → 2x
change one character in a string
```

Usually:

```text
one legal move = one graph edge
```

---

# 7. Step 4 — Directed or Undirected

Use this test:

```text
If I can go from A to B,
can I automatically go from B to A?
```

If yes:

```text
undirected
```

If no:

```text
directed
```

Examples:

```text
friendship              → usually undirected
two-way road            → undirected
one-way road            → directed
prerequisite A before B → directed A → B
x → 2x operation        → directed
```

---

# 8. Step 5 — Weighted or Unweighted

Ask:

```text
"What does one transition cost?"
```

The answer may be:

```text
1
0 or 1
non-negative arbitrary value
possibly negative
```

That often selects the shortest-path algorithm.

```text
All edges cost 1   → BFS
Weights 0 or 1     → 0-1 BFS
Weights >= 0       → Dijkstra
Negative allowed   → Bellman–Ford
All pairs needed   → Floyd–Warshall for moderate V
```

---

# 9. Step 6 — Identify Source/Target Scope

Graph problems may ask for:

## One source

```text
S → every node
```

This is SSSP:

```text
Single Source Shortest Path
```

---

## One source and one target

```text
S → T
```

---

## Multiple sources

Examples:

```text
nearest hospital
nearest fire source
distance from closest monster
```

Typical idea:

```text
push all sources initially
```

---

## All pairs

Need:

```text
distance(i,j)
for many/all pairs
```

This is APSP:

```text
All Pairs Shortest Path
```

---

# 10. Step 7 — Identify the Graph Problem Family

Once the graph is created, ask what property is required.

Common families:

```text
Traversal / Reachability
Connected Components
Cycle Detection
Bipartite Checking
Shortest Path
Topological Ordering
Minimum Spanning Tree
Tree Problems
State-Space Search
```

The important habit is:

```text
Do not choose the algorithm before identifying the problem family.
```

---

# 11. Step 8 — Use Constraints

Correct modelling is not enough.

Check:

```text
N = number of vertices
M = number of edges
```

Typical complexities:

```text
DFS/BFS             O(V + E)
0-1 BFS             O(V + E)
Dijkstra            O((V + E) log V)
Bellman–Ford        O(VE)
Floyd–Warshall      O(V^3)
Topological Sort    O(V + E)
Kruskal MST         O(E log E)
```

So algorithm selection is:

```text
graph structure
+
edge weights
+
required result
+
constraints
=
correct algorithm
```

---

# 12. Explicit vs Implicit Graphs

## Explicit graph

Input directly gives edges:

```text
N M
u1 v1
u2 v2
...
```

---

## Implicit graph

The statement gives operations instead of edges.

Example:

```text
Start = 3

Allowed:
x → x+1
x → 2x

Target = 10
```

The graph is hidden.

Nodes:

```text
possible values of x
```

Edges:

```text
x → x+1
x → 2x
```

ASCII:

```text
        3
       /       4   6
     / \ /     5  8 7 12
```

No edge list was provided, but this is still a graph.

---

# 13. State Graphs

This is one of the most important CP modelling patterns.

Suppose:

> You can use one coupon to make one edge free.

If node is only:

```text
city
```

we lose important information.

Reaching city `u` with coupon unused is different from reaching `u` after using it.

Correct state:

```text
(node, couponUsed)
```

So:

```text
(u,0)
(u,1)
```

are different graph nodes.

ASCII:

```text
Coupon unused layer

1_0 ---- 2_0 ---- 3_0
 |        |        |
 use      use      use
 |        |        |
1_1 ---- 2_1 ---- 3_1

Coupon used layer
```

Recognition signals for state expansion:

```text
remaining fuel
parity
coupon used?
keys collected
last color
direction
bitmask
time phase
number of special operations used
```

If future choices depend on extra information, that information probably belongs in the graph state.

---

# 14. Grid as a Graph

A grid is a hidden graph.

Cell:

```text
(r,c)
```

becomes a node.

Legal neighbor moves become edges:

```text
(r,c) → (r-1,c)
(r,c) → (r+1,c)
(r,c) → (r,c-1)
(r,c) → (r,c+1)
```

Example:

```text
S . #
. . .
# . T
```

Interpretation:

```text
open cell = node
valid movement = edge
wall = blocked transition
```

If every move costs 1:

```text
BFS
```

If each move costs 0/1:

```text
0-1 BFS
```

If costs are arbitrary non-negative:

```text
Dijkstra
```

---

# 15. Shortest Path Decision Tree

This is the shortest-path ladder emphasized in the lecture.

```text
Need shortest path?
        |
        +-- equal/unit edge cost?
        |       └── BFS
        |
        +-- weights only 0 or 1?
        |       └── 0-1 BFS
        |
        +-- all weights non-negative?
        |       └── Dijkstra
        |
        +-- negative edges possible?
        |       └── Bellman–Ford
        |
        +-- shortest path for all pairs?
                └── Floyd–Warshall
                    when V is moderate
```

Compact memory rule:

```text
1        → BFS
0/1      → 0-1 BFS
>= 0     → Dijkstra
negative → Bellman–Ford
all pair → Floyd–Warshall
```

---

# 16. DFS/BFS Problems

DFS/BFS are not only shortest-path tools.

Use them for:

```text
reachability
component counting
component size
flood fill
tree traversal
cycle detection
bipartite checking
```

Complexity:

```text
O(V + E)
```

Example question:

```text
"Can I reach node B from node A?"
```

Usually:

```text
DFS or BFS
```

is enough.

---

# 17. Cycle and Bipartite Problems

## Cycle

Story clues:

```text
circular dependency
loop
can return to same state
```

Typical methods:

```text
undirected → DFS/BFS + parent
directed   → DFS colors or Kahn
```

---

## Bipartite

Question:

```text
Can vertices be divided into two groups
such that every edge crosses between groups?
```

Use:

```text
2-color BFS/DFS
```

Common stories:

```text
two teams
enemy groups
opposite labels
```

---

# 18. Topological Problems

Typical story:

```text
A must happen before B
```

Model:

```text
A → B
```

Examples:

```text
course prerequisites
build order
task dependencies
package dependencies
```

Algorithms:

```text
DFS finish order
Kahn's algorithm
```

For lexicographically smallest valid order:

```text
Kahn + min-heap
```

---

# 19. MST Problems

MST means:

```text
Minimum Spanning Tree
```

Goal:

```text
connect all vertices
with minimum TOTAL edge cost
```

This is different from shortest path.

Compare:

```text
Shortest Path:
minimum cost from S to T

MST:
minimum total cost to connect entire graph
```

Typical stories:

```text
connect all cities with minimum road cost
build minimum cable network
minimum infrastructure cost
```

Algorithms:

```text
Kruskal
Prim
```

---

# 20. Tree Problems

If the graph is guaranteed to be:

```text
connected
N vertices
N-1 edges
```

then it is a tree.

Typical tools:

```text
DFS
BFS
subtree DP
tree diameter
LCA
rerooting
```

Use the tree structure instead of treating it as an arbitrary graph.

---

# 21. Worked Example — Friendship

Problem:

> People have mutual friendships. Find the number of separate friend groups.

Story → variables:

```text
person     → node
friendship → edge
```

Direction:

```text
mutual → undirected
```

Weight:

```text
none
```

Required property:

```text
connected components
```

Algorithm:

```text
DFS/BFS
```

Final model:

```text
undirected unweighted graph
+
count components
```

---

# 22. Worked Example — Course Prerequisites

Problem:

> Course A must be completed before B. Find a valid order.

Model:

```text
course → node
A before B → edge A → B
```

Graph:

```text
directed
```

Required property:

```text
dependency ordering
```

Algorithm:

```text
topological sort
```

If smallest ID must be chosen whenever multiple courses are available:

```text
Kahn + min-heap
```

---

# 23. Worked Example — Cheapest Route

Problem:

> Cities are connected by roads with non-negative tolls. Find minimum toll from S to T.

Model:

```text
city → node
road → edge
toll → weight
```

Requirement:

```text
minimum weighted path
```

Weights:

```text
non-negative
```

Therefore:

```text
Dijkstra
```

Important:

```text
"minimum" alone does not mean Dijkstra.

The edge-weight rules determine Dijkstra.
```

---

# 24. Worked Example — Grid Maze

Problem:

> Move up/down/left/right through open cells. Find minimum moves from S to T.

Model:

```text
open cell (r,c) → node
valid move       → edge
```

Every move:

```text
cost = 1
```

Need:

```text
shortest path
```

Therefore:

```text
BFS
```

---

# 25. Worked Example — Extra State

Problem:

> Travel through weighted roads. You may make one edge free.

Naive node:

```text
city
```

is not enough.

Correct node:

```text
(city, couponUsed)
```

From state `(u,0)` over edge `u → v` with cost `w`:

```text
Do not use coupon:
(u,0) → (v,0) cost w

Use coupon:
(u,0) → (v,1) cost 0
```

From `(u,1)`:

```text
(u,1) → (v,1) cost w
```

Now all weights are non-negative.

So:

```text
Dijkstra on an expanded state graph
```

---

# 26. C++17 Graph Representation

## Unweighted undirected graph

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<vector<int>> graph(n + 1);

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;

        graph[u].push_back(v);
        graph[v].push_back(u);
    }

    return 0;
}
```

## Weighted graph

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<vector<pair<int, ll>>> graph(n + 1);

    for (int i = 0; i < m; i++) {
        int u, v;
        ll weight;
        cin >> u >> v >> weight;

        graph[u].push_back({v, weight});
        graph[v].push_back({u, weight});
    }

    return 0;
}
```

For a directed graph, remove the reverse insertion.

---

# 27. Common Modelling Mistakes

| Mistake | Better question |
|---|---|
| Immediately use DFS because the statement says graph | What property is actually required? |
| Treat every physical object as one node | Does future behavior depend on extra state? |
| Forget edge direction | Can the move be reversed automatically? |
| Ignore edge weights | What is the cost of one transition? |
| Use BFS on arbitrary weighted edges | Are all edge costs equal? |
| Use Dijkstra with negative edges | Can any edge be negative? |
| Confuse MST with shortest path | Optimize one route or total connection cost? |
| Build every implicit edge explicitly | Can neighbors be generated on demand? |
| Ignore N and M | Will the complexity fit? |
| Assume the word graph must appear | Can objects/states + transitions form a graph? |

---

# 28. Contest Recognition Checklist

When a graph statement feels confusing, write:

```text
1. What is one node/state?
2. What is one legal edge/transition?
3. Directed or undirected?
4. What does one edge cost?
5. What is the source?
6. What is the target?
7. One source, multi-source, or all pairs?
8. What property is being asked?
9. What are N and M?
10. Which algorithm matches all these facts?
```

Fast mapping:

```text
"Can reach?"                  → DFS/BFS
"How many groups?"            → Components
"Cycle / circular?"           → Cycle detection
"Two groups?"                 → Bipartite
"Before / after dependency?"  → Topological sort
"Minimum moves?"              → BFS
"0/1 cost?"                   → 0-1 BFS
"Non-negative minimum cost?"  → Dijkstra
"Negative edges?"             → Bellman–Ford
"Every pair distance?"        → Floyd–Warshall
"Connect all cheaply?"        → MST
"N-1 edges + connected?"      → Tree
```

---

# 29. Final Mental Model

The full modelling flow is:

```text
PROBLEM STORY
      ↓
REMOVE THE STORY
      ↓
What is a STATE?
      ↓
STATE = NODE
      ↓
What is ONE LEGAL MOVE / RELATION?
      ↓
MOVE = EDGE
      ↓
Directed?
Weighted?
      ↓
What exact property is required?
      ↓
Which graph problem family?
      ↓
Check constraints
      ↓
Choose algorithm
```

Shortest-path branch:

```text
Shortest Path
    |
    +-- equal/unit weight ----------> BFS
    |
    +-- weights {0,1} -------------> 0-1 BFS
    |
    +-- non-negative weights ------> Dijkstra
    |
    +-- negative edges ------------> Bellman–Ford
    |
    +-- all-pairs, moderate V -----> Floyd–Warshall
```

Broader graph branch:

```text
Graph Problem
    |
    +-- traversal / reachability --> DFS / BFS
    |
    +-- cycle ---------------------> DFS / BFS / Kahn
    |
    +-- bipartite -----------------> 2-color BFS/DFS
    |
    +-- dependency order ----------> Topological Sort
    |
    +-- shortest path -------------> weight-dependent algorithm
    |
    +-- connect all cheaply -------> MST
    |
    +-- special acyclic graph -----> Tree / DAG techniques
```

The key contest habit:

```text
Do NOT ask:

"Which algorithm do I remember?"

Ask:

"What graph did this story create,
and what exact property of that graph must I compute?"
```

That is the foundation of graph problem solving.
