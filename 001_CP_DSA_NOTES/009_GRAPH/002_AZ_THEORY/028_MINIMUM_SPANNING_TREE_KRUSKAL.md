# Minimum Spanning Tree (MST) — Prerequisites, Kruskal, DSU and Step-by-Step Dry Run

> **Competitive Programming Self-Study Note**
>
> Main idea:
>
> ```text
> Weighted connected graph
>        ↓
> choose edges
>        ↓
> connect all N nodes
>        ↓
> use exactly N - 1 edges
>        ↓
> avoid cycles
>        ↓
> minimum possible total weight
>        ↓
> Minimum Spanning Tree
> ```
>
> This note follows the lecture flow and focuses mainly on **Kruskal's Algorithm + DSU**.

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Tree and Spanning Tree](#2-tree-and-spanning-tree)
3. [What Is an MST?](#3-what-is-an-mst)
4. [Why Exactly N - 1 Edges?](#4-why-exactly-n---1-edges)
5. [MST Recognition](#5-mst-recognition)
6. [Kruskal vs Prim](#6-kruskal-vs-prim)
7. [Kruskal's Main Idea](#7-kruskals-main-idea)
8. [Why DSU Is Needed](#8-why-dsu-is-needed)
9. [Kruskal Step by Step](#9-kruskal-step-by-step)
10. [Detailed Dry Run](#10-detailed-dry-run)
11. [Why Kruskal Works](#11-why-kruskal-works)
12. [DSU Code](#12-dsu-code)
13. [Complete C++17 Kruskal](#13-complete-c17-kruskal)
14. [Detecting No MST](#14-detecting-no-mst)
15. [Complexity](#15-complexity)
16. [Prim's Algorithm Briefly](#16-prims-algorithm-briefly)
17. [Maximum Spanning Tree](#17-maximum-spanning-tree)
18. [Super-Node Modelling](#18-super-node-modelling)
19. [Common MST Variations](#19-common-mst-variations)
20. [Common Mistakes](#20-common-mistakes)
21. [Recognition Checklist](#21-recognition-checklist)
22. [Final Mental Model](#22-final-mental-model)

---

# 1. Prerequisites

Before MST, know these ideas:

```text
1. Graph
2. Node / vertex
3. Edge
4. Weighted edge
5. Connected graph
6. Cycle
7. Tree
8. Connected component
9. DSU / Union Find
10. Sorting
```

We will revise them quickly.

---

## 1.1 Graph

A graph has:

```text
V = vertices / nodes
E = edges
```

Example:

```text
1 ----- 2
|       |
|       |
3 ----- 4
```

Nodes:

```text
{1,2,3,4}
```

Edges:

```text
(1,2)
(1,3)
(2,4)
(3,4)
```

---

## 1.2 Weighted graph

In MST, edges have weights/costs.

```text
1 --4-- 2
|       |
2       5
|       |
3 --1-- 4
```

Meaning:

```text
1-2 costs 4
1-3 costs 2
3-4 costs 1
2-4 costs 5
```

Goal:

```text
connect all nodes with minimum total selected edge cost
```

---

## 1.3 Connected graph

Connected:

```text
1 -- 2 -- 3 -- 4
```

Disconnected:

```text
1 -- 2       3 -- 4
```

A spanning tree over all nodes exists only if the graph is connected.

---

## 1.4 Cycle

Example cycle:

```text
1
|\
| \
2--3
```

Edges:

```text
1-2
2-3
3-1
```

A tree must not contain a cycle.

---

# 2. Tree and Spanning Tree

## 2.1 Tree

A tree is:

```text
connected + acyclic
```

For `N` nodes, a tree has exactly:

```text
N - 1 edges
```

Example:

```text
    1
   / \
  2   3
     / \
    4   5
```

Here:

```text
N = 5
edges = 4 = N - 1
```

---

## 2.2 Spanning tree

A spanning tree of a graph is a subset of the original edges that:

```text
1. contains every node
2. keeps the graph connected
3. has no cycle
```

Therefore:

```text
spanning tree edges = N - 1
```

A graph can have many spanning trees.

---

# 3. What Is an MST?

MST means:

```text
Minimum Spanning Tree
```

Among all possible spanning trees, choose the one with minimum total edge weight.

Formula:

```text
MST cost
=
sum of weights of the selected N - 1 edges
```

---

## Small example

```text
1 --1-- 2
|      /|
4    2  5
|  /    |
3 --3-- 4
```

One spanning tree:

```text
1-2 = 1
2-3 = 2
3-4 = 3
```

Cost:

```text
1 + 2 + 3 = 6
```

Another spanning tree:

```text
1-3 = 4
1-2 = 1
2-4 = 5
```

Cost:

```text
4 + 1 + 5 = 10
```

The MST is the cheaper valid spanning tree.

---

# 4. Why Exactly N - 1 Edges?

Start with `N` separate components:

```text
{1} {2} {3} ... {N}
```

Every accepted edge joins two different components.

Each successful merge reduces component count by 1.

Initially:

```text
components = N
```

Target:

```text
components = 1
```

Required successful merges:

```text
N - 1
```

Therefore:

```text
MST uses N - 1 accepted edges
```

This also explains why DSU fits Kruskal naturally.

---

# 5. MST Recognition

Think MST when the story says:

```text
Connect all cities with minimum road cost.
Connect all computers with minimum cable cost.
Build a minimum-cost network.
Connect all points while minimizing total connection cost.
```

Mathematical model:

```text
weighted undirected graph
+
connect all vertices
+
minimum total selected edge weight
```

That is the classic MST pattern.

---

# 6. Kruskal vs Prim

The lecture lists two standard MST algorithms:

```text
1. Kruskal's Algorithm
2. Prim's Algorithm
```

Kruskal is especially natural when the graph is given as an edge list.

Prim is especially natural when you want to grow a tree from one node using an adjacency list + priority queue.

This note focuses on **Kruskal + DSU**.

---

# 7. Kruskal's Main Idea

Kruskal is greedy.

Core steps:

```text
1. Sort all edges by weight.
2. Process them from smallest to largest.
3. If edge connects two different components:
       take it.
4. If edge would create a cycle:
       skip it.
5. Stop after N - 1 accepted edges.
```

One-line memory:

> **Take the cheapest edge that does not create a cycle.**

---

# 8. Why DSU Is Needed

Suppose current edge is:

```text
u --w-- v
```

Question:

```text
Will adding u-v create a cycle?
```

With DSU:

```text
find(u) == find(v)
```

means:

```text
already connected
→ adding this edge creates a cycle
→ SKIP
```

But:

```text
find(u) != find(v)
```

means:

```text
different components
→ edge safely joins them
→ TAKE
→ UNION
```

---

## Visual

Before:

```text
Component A       Component B

1 -- 2            4 -- 5
     |
     3
```

Candidate:

```text
3 -- 4
```

Since:

```text
find(3) != find(4)
```

we take it.

After:

```text
1 -- 2
     |
     3 -- 4 -- 5
```

---

# 9. Kruskal Step by Step

Suppose input has:

```text
N nodes
M weighted edges
```

Store each edge as:

```text
(weight, u, v)
```

C++:

```cpp
vector<tuple<long long, int, int>> edges;
```

Add an edge:

```cpp
edges.push_back({weight, u, v});
```

---

## Step 1 — Sort

```cpp
sort(edges.begin(), edges.end());
```

Tuple sorting compares weight first, so edges become increasing by weight.

---

## Step 2 — Initialize DSU

Initially:

```text
{1} {2} {3} ... {N}
```

Every node is its own component.

---

## Step 3 — Process an edge

For:

```text
(weight,u,v)
```

check:

```cpp
findRoot(u) != findRoot(v)
```

If true:

```text
TAKE edge
add weight
merge components
chosenEdges++
```

Otherwise:

```text
SKIP
```

---

## Step 4 — Stop at N - 1 edges

```text
chosenEdges == N - 1
```

means MST is complete.

---

# 10. Detailed Dry Run

Consider:

```text
Edges:

1-3 weight 1
1-2 weight 2
2-3 weight 2
3-4 weight 3
2-4 weight 4
```

Sorted order is already:

```text
1 : 1-3
2 : 1-2
2 : 2-3
3 : 3-4
4 : 2-4
```

Initially:

```text
{1} {2} {3} {4}

mstCost = 0
chosenEdges = 0
```

---

## Edge 1: 1-3, weight 1

```text
find(1) != find(3)
```

Take.

```text
mstCost = 1
chosenEdges = 1
```

Components:

```text
{1,3} {2} {4}
```

---

## Edge 2: 1-2, weight 2

```text
find(1) != find(2)
```

Take.

```text
mstCost = 3
chosenEdges = 2
```

Components:

```text
{1,2,3} {4}
```

---

## Edge 3: 2-3, weight 2

Now:

```text
find(2) == find(3)
```

They are already connected.

Taking this edge creates a cycle.

So:

```text
SKIP
```

---

## Edge 4: 3-4, weight 3

```text
find(3) != find(4)
```

Take.

```text
mstCost = 6
chosenEdges = 3
```

Since:

```text
N = 4
N - 1 = 3
```

we are done.

Answer:

```text
MST cost = 6
```

---

## Compact dry run

```text
1-3 (1): different components → TAKE → cost=1
1-2 (2): different components → TAKE → cost=3
2-3 (2): same component       → SKIP
3-4 (3): different components → TAKE → cost=6

chosen = 3 = N-1
answer = 6
```

---

# 11. Why Kruskal Works

Intuition:

```text
Always choose the cheapest currently safe edge.
```

If two current components can be connected by several edges, taking a more expensive edge before a cheaper safe edge cannot improve the total.

This is related to the **cut property**.

Simple version:

```text
For any partition of vertices into two groups,
the cheapest edge crossing that partition is safe for some MST.
```

Kruskal repeatedly applies this greedy idea while DSU tracks components.

---

# 12. DSU Code

Use path compression + union by size.

```cpp
struct DSU {
    vector<int> parent;
    vector<int> size;

    DSU(int n) {
        parent.resize(n + 1);
        size.assign(n + 1, 1);

        for (int i = 1; i <= n; i++) {
            parent[i] = i;
        }
    }

    int findRoot(int x) {
        if (parent[x] == x) {
            return x;
        }

        return parent[x] = findRoot(parent[x]);
    }

    bool merge(int a, int b) {
        int rootA = findRoot(a);
        int rootB = findRoot(b);

        if (rootA == rootB) {
            return false;
        }

        if (size[rootA] < size[rootB]) {
            swap(rootA, rootB);
        }

        parent[rootB] = rootA;
        size[rootA] += size[rootB];

        return true;
    }
};
```

Useful design:

```text
merge(a,b) returns true  → actual merge happened
merge(a,b) returns false → already same component
```

That makes Kruskal cleaner.

---

# 13. Complete C++17 Kruskal

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

struct DSU {
    vector<int> parent;
    vector<int> size;

    DSU(int n) {
        parent.resize(n + 1);
        size.assign(n + 1, 1);

        for (int i = 1; i <= n; i++) {
            parent[i] = i;
        }
    }

    int findRoot(int x) {
        if (parent[x] == x) {
            return x;
        }

        return parent[x] = findRoot(parent[x]);
    }

    bool merge(int a, int b) {
        int rootA = findRoot(a);
        int rootB = findRoot(b);

        if (rootA == rootB) {
            return false;
        }

        if (size[rootA] < size[rootB]) {
            swap(rootA, rootB);
        }

        parent[rootB] = rootA;
        size[rootA] += size[rootB];

        return true;
    }
};

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<tuple<ll, int, int>> edges;

    for (int i = 0; i < m; i++) {
        int u, v;
        ll weight;
        cin >> u >> v >> weight;

        edges.push_back({weight, u, v});
    }

    sort(edges.begin(), edges.end());

    DSU dsu(n);

    ll mstCost = 0;
    int chosenEdges = 0;

    for (auto [weight, u, v] : edges) {
        if (dsu.merge(u, v)) {
            mstCost += weight;
            chosenEdges++;

            if (chosenEdges == n - 1) {
                break;
            }
        }
    }

    if (chosenEdges != n - 1) {
        cout << "No solution\n";
    } else {
        cout << mstCost << '\n';
    }

    return 0;
}
```

---

# 14. Detecting No MST

The lecture code checks whether:

```text
chosenEdges == N - 1
```

If not, there is no spanning tree covering all nodes.

Example:

```text
1 -- 2 -- 3

4 -- 5
```

For `N = 5`, an MST needs:

```text
4 accepted edges
```

But these two components cannot be joined.

So:

```text
No solution
```

---

# 15. Complexity

Let:

```text
V = number of nodes
E = number of edges
```

Sorting edges:

```text
O(E log E)
```

DSU work:

```text
O(E α(V))
```

`α(V)` is effectively constant.

Total:

```text
O(E log E)
```

This is the standard Kruskal complexity to remember.

---

# 16. Prim's Algorithm Briefly

Prim also builds an MST, but from a different viewpoint.

Kruskal:

```text
edge-first
```

Prim:

```text
start from one node
expand the current tree
always take cheapest outgoing edge
```

Typical Prim structure:

```text
adjacency list
+
min priority_queue
+
visited array
```

Typical complexity:

```text
O(E log V)
```

---

## Kruskal vs Prim

| Feature | Kruskal | Prim |
|---|---|---|
| Main focus | Global edge order | Expand one tree |
| Main DS | DSU | Priority queue |
| Input style | Edge list | Adjacency list |
| Cycle handling | DSU | visited |
| Complexity | `O(E log E)` | `O(E log V)` |

---

# 17. Maximum Spanning Tree

One lecture screenshot explicitly shows a Kruskal modification:

```text
start from largest weighted edge
then go smaller
```

This gives a **maximum spanning tree**.

Normal MST:

```cpp
sort(edges.begin(), edges.end());
```

Maximum spanning tree:

```cpp
sort(edges.rbegin(), edges.rend());
```

DSU logic stays the same:

```text
if endpoints are in different components:
    take edge
```

Important lesson:

> Do not memorize Kruskal as "always ascending". The ordering depends on the optimization objective.

---

# 18. Super-Node Modelling

Another lecture screenshot shows a **super node** connected to many original nodes.

This is a very useful modelling pattern.

Suppose every node `i` has two options:

```text
1. connect to another node through a normal graph edge
2. pay individual cost Ci
```

Create one new virtual node:

```text
SUPER
```

Then add:

```text
SUPER -- i
```

with weight:

```text
Ci
```

Now the individual option becomes just another edge choice.

---

## Visual

```text
          SUPER
        /  |  |  \
      C1  C2 C3  C4
      /    |  |    \
     1     2  3     4
```

Then solve MST on:

```text
N + 1 nodes
```

This pattern appears in problems involving:

```text
building a local facility vs connecting to another city
power station vs transmission line
well vs water pipe
server vs network connection
```

---

# 19. Common MST Variations

## 19.1 Need MST cost

Standard Kruskal:

```text
answer += selected edge weight
```

---

## 19.2 Need selected MST edges

Store them:

```cpp
vector<tuple<int, int, ll>> mstEdges;

if (dsu.merge(u, v)) {
    mstEdges.push_back({u, v, weight});
}
```

---

## 19.3 Need largest edge in MST

```cpp
maxEdge = max(maxEdge, weight);
```

Since Kruskal processes increasing weights, the last accepted edge is also the largest edge in that MST.

---

## 19.4 Need K components instead of one

Start:

```text
components = N
```

Each successful union reduces components by 1.

To stop at exactly `K` components:

```text
successful merges = N - K
```

---

## 19.5 Need maximum spanning tree

Sort descending instead of ascending.

---

## 19.6 Need transformed edge costs

Sometimes the graph structure is the same, but cost becomes:

```text
f(weight)
```

Then build the edge list using that transformed cost and run the same MST logic if the problem model still matches a spanning-tree objective.

---

# 20. Common Mistakes

## Mistake 1 — Take every cheap edge

Cheap alone is not enough.

Must avoid cycle:

```cpp
findRoot(u) != findRoot(v)
```

---

## Mistake 2 — Forget sorting

Without sorting, Kruskal's greedy order is gone.

---

## Mistake 3 — Forget N - 1

MST must contain exactly:

```text
N - 1 edges
```

---

## Mistake 4 — Use `int` for total cost

If:

```text
E <= 1e5
weight <= 1e9
```

sum may reach around:

```text
1e14
```

Use:

```cpp
long long
```

---

## Mistake 5 — Forget disconnected graph case

After processing:

```cpp
if (chosenEdges != n - 1)
```

then no spanning tree exists.

---

## Mistake 6 — Add both directions to Kruskal edge list

For undirected edge:

```text
u-v weight w
```

store it once.

Kruskal works on the edge list, not adjacency traversal.

---

## Mistake 7 — Confuse shortest path and MST

Shortest path:

```text
minimum cost from source to destination
```

MST:

```text
minimum total cost to connect ALL vertices
```

Different problem.

---

# 21. Recognition Checklist

Ask:

```text
1. Need to connect all nodes?
2. Edge costs given?
3. Need minimum total connection cost?
4. Can choose a subset of edges?
5. Cycles are unnecessary?
```

If yes:

```text
Think MST
```

Then:

```text
edge-list friendly problem
→ Kruskal + DSU
```

---

## Contest memory pattern

```text
SORT EDGES
    ↓
CHEAPEST FIRST
    ↓
DIFFERENT COMPONENTS?
    ↓
YES → TAKE + UNION
NO  → SKIP
    ↓
STOP AT N - 1 EDGES
```

---

# 22. Final Mental Model

Initially:

```text
{1} {2} {3} {4} {5}
```

Take cheapest safe edge:

```text
1-3
```

Now:

```text
{1,3} {2} {4} {5}
```

Take next safe edge:

```text
2-3
```

Now:

```text
{1,2,3} {4} {5}
```

Any edge fully inside `{1,2,3}`:

```text
SKIP
```

because it creates a cycle.

Keep merging different components until:

```text
one component remains
```

Number of successful merges:

```text
N - 1
```

Therefore remember:

```text
MST
=
Greedy edge ordering
+
DSU cycle detection
+
N - 1 successful merges
```

For modelling-heavy problems:

```text
modify edge order
or
add virtual/super nodes
then run MST machinery.
```
