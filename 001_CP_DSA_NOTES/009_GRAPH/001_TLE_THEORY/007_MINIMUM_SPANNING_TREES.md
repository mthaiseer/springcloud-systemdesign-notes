# Minimum Spanning Trees (MST)
## TLE-Style Visual Self-Study Notes - Prerequisites -> Kruskal -> Prim -> Properties -> Power Grid

> **Lecture basis:** TLE Graphs Advanced - *Minimum Spanning Trees*.
>
> **Self-study expansion added here:** tiny prerequisite examples, cleaner ASCII graphs, intuitive proofs, one detailed dry run per main algorithm, and a complete modelling walkthrough for **Codeforces 1245D - Shichikuji and Power Grid**.
>
> Goal: understand the idea well enough that the algorithm feels natural instead of memorized.

---

# Table of Contents

1. [What You Need Before MST](#1-what-you-need-before-mst)
2. [Prerequisite - Graph, Weight, Path, Cycle](#2-prerequisite---graph-weight-path-cycle)
3. [Prerequisite - What Is a Tree?](#3-prerequisite---what-is-a-tree)
4. [Prerequisite - What Is a Spanning Tree?](#4-prerequisite---what-is-a-spanning-tree)
5. [Why Every Spanning Tree Has N-1 Edges](#5-why-every-spanning-tree-has-n-1-edges)
6. [What Is a Minimum Spanning Tree?](#6-what-is-a-minimum-spanning-tree)
7. [Prerequisite - DSU for Kruskal](#7-prerequisite---dsu-for-kruskal)
8. [Prerequisite - Min-Heap for Prim](#8-prerequisite---min-heap-for-prim)
9. [Kruskal's Algorithm](#9-kruskals-algorithm)
10. [Why Kruskal Works](#10-why-kruskal-works)
11. [Kruskal C++17](#11-kruskal-c17)
12. [Prim's Algorithm](#12-prims-algorithm)
13. [Why Prim Works](#13-why-prim-works)
14. [Prim C++17](#14-prim-c17)
15. [Prim vs Kruskal](#15-prim-vs-kruskal)
16. [Important MST Properties](#16-important-mst-properties)
17. [Problem - Shichikuji and Power Grid](#17-problem---shichikuji-and-power-grid)
18. [Power Grid Complete C++17](#18-power-grid-complete-c17)
19. [Common Mistakes](#19-common-mistakes)
20. [Final Recognition Model](#20-final-recognition-model)
21. [Practice Checklist](#21-practice-checklist)

---

# 1. What You Need Before MST

Before learning MST, you should be comfortable with these words:

```text
vertex / node
edge
edge weight
connected graph
path
cycle
tree
connected component
DSU
priority queue / min-heap
```

You do **not** need advanced graph theory.

The whole MST idea starts from a very simple question:

```text
"I want to connect every node,
but I want the total connection cost to be as small as possible."
```

Typical real-world picture:

```text
cities       = nodes
roads        = edges
road cost    = edge weight
```

We want:

```text
all cities connected
+
minimum total road cost
```

That is where MST appears.

---

# 2. Prerequisite - Graph, Weight, Path, Cycle

## 2.1 Weighted Graph

Consider:

```text
        5
   (1)----- (2)
    | \       |
  2 |  \ 4    | 3
    |   \     |
   (3)----- (4)
        6
```

The numbers are edge weights.

For example:

```text
edge (1,2) costs 5
edge (1,3) costs 2
edge (1,4) costs 4
edge (2,4) costs 3
edge (3,4) costs 6
```

---

## 2.2 Path

A path is a sequence of connected edges.

Example:

```text
1 -> 2 -> 4
```

Its total weight is:

```text
5 + 3 = 8
```

---

## 2.3 Cycle

A cycle starts somewhere and returns to the same node.

Example:

```text
      1
     / \
    2---3
```

Path:

```text
1 -> 2 -> 3 -> 1
```

is a cycle.

Why is a cycle important for MST?

Because if all nodes inside the cycle are already connected, one edge is unnecessary.

Example:

```text
      1
     / \
    2---3
```

Remove one edge:

```text
1---2---3
```

All three nodes are still connected.

So a minimum connection structure should not waste money on unnecessary cycles.

---

# 3. Prerequisite - What Is a Tree?

A tree is a graph that is:

```text
1. connected
2. has no cycle
```

Example:

```text
        1
       / \
      2   3
     / \
    4   5
```

Every node can reach every other node.

There is no cycle.

Therefore this is a tree.

---

## 3.1 Not a Tree - Disconnected

```text
1---2      3---4
```

No path between component `{1,2}` and component `{3,4}`.

So it is not a tree.

---

## 3.2 Not a Tree - Has a Cycle

```text
1
|\
| \
2--3
```

Connected?

```text
YES
```

Cycle?

```text
YES
```

So it is not a tree.

---

# 4. Prerequisite - What Is a Spanning Tree?

Suppose the original graph is:

```text
        5
   (1)----- (2)
    | \       |
  2 |  \ 4    | 3
    |   \     |
   (3)----- (4)
        6
```

A **spanning tree** is a subgraph that:

```text
uses ALL vertices
+
keeps them connected
+
contains no cycle
```

One possible spanning tree:

```text
   (1)-----5-----(2)
    |
  2 |
    |
   (3)-----6-----(4)
```

Another:

```text
   (1)
    |\
  2 | \4
    |  \
   (3) (4)-----3-----(2)
```

Both connect all four vertices without cycles.

Their costs are different.

That leads to MST.

---

# 5. Why Every Spanning Tree Has N-1 Edges

This is a very important fact.

For:

```text
N nodes
```

every tree has exactly:

```text
N - 1 edges
```

---

## 5.1 Build the Tree Slowly

Start with one node:

```text
[1]
```

Nodes:

```text
1
```

Edges:

```text
0
```

Now add node `2`.

To keep the structure connected without making a cycle, attach it using exactly one edge:

```text
1---2
```

Now:

```text
nodes = 2
edges = 1
```

Add node `3`:

```text
1---2---3
```

Now:

```text
nodes = 3
edges = 2
```

Add node `4`:

```text
1---2---3
        |
        4
```

Now:

```text
nodes = 4
edges = 3
```

Pattern:

```text
N nodes -> N-1 edges
```

---

## 5.2 Why Not Fewer?

With fewer than `N-1` edges, not enough connections exist to join all vertices.

---

## 5.3 Why Not More?

A connected graph with more than `N-1` edges must contain at least one cycle.

So:

```text
tree
=
connected + no cycles
=
exactly N-1 edges
```

This gives us a useful MST check:

```text
MST must contain exactly N-1 chosen edges
```

---

# 6. What Is a Minimum Spanning Tree?

A spanning tree connects all vertices without cycles.

A **minimum spanning tree** is the spanning tree whose total edge weight is minimum.

---

## 6.1 Example

Original graph:

```text
          5
     4---------3
    /| \       |\
  9/ |1 \3     |3 \8
  /  |   \     |   \
 5   |    \    |    6
  \  |     \   |   /
  4\ |      \  |  /7
    \|       \ | /
     1---2----2

```

A cleaner edge list:

```text
1--4 weight 1
1--2 weight 2
2--3 weight 3
1--5 weight 4
4--3 weight 5
2--6 weight 7
3--6 weight 8
4--5 weight 9
```

One minimum spanning tree is:

```text
5
|
4
|
1---2---3---6

weights:
4 + 1 + 2 + 3 + 7 = 17
```

The lecture's page 5 uses this same idea: the full graph has many possible edges, while the MST keeps only the cheap edges required to connect every vertex.

---

## 6.2 The Goal Is NOT Shortest Path

This distinction is critical.

### Shortest path

Question:

```text
"What is the cheapest route from S to T?"
```

### MST

Question:

```text
"What is the cheapest set of edges that connects ALL vertices?"
```

MST is about the **whole network**, not one source-destination route.

---

# 7. Prerequisite - DSU for Kruskal

Kruskal processes edges globally from smallest weight to largest.

Each time we consider an edge:

```text
u --- v
```

we must know:

```text
Are u and v already connected?
```

If yes:

```text
adding this edge creates a cycle
```

If no:

```text
adding this edge safely joins two components
```

This is exactly what DSU answers.

---

## 7.1 DSU Mental Model

Initially every vertex is separate:

```text
[1]   [2]   [3]   [4]   [5]
```

Choose edge:

```text
1---3
```

DSU merges:

```text
[1,3]   [2]   [4]   [5]
```

Choose:

```text
2---4
```

Now:

```text
[1,3]   [2,4]   [5]
```

Choose:

```text
3---4
```

Since `3` belongs to `{1,3}` and `4` belongs to `{2,4}`, merge:

```text
[1,2,3,4]   [5]
```

Now if an edge appears:

```text
1---2
```

both already have the same representative.

Therefore adding it creates a cycle.

Skip it.

---

## 7.2 DSU Operations

```text
find(x)
→ representative/root of x's component

unite(a,b)
→ merge two different components
```

With path compression + union by size:

```text
almost O(1) amortized
```

More precisely:

```text
O(alpha(N))
```

where `alpha` is the inverse Ackermann function and is tiny for practical constraints.

---

## 7.3 Clean DSU

```cpp
class DSU {
private:
    vector<int> parent;
    vector<int> componentSize;

public:
    DSU(int n) {
        parent.resize(n + 1);
        componentSize.assign(n + 1, 1);

        for (int i = 1; i <= n; i++) {
            parent[i] = i;
        }
    }

    int find(int x) {
        if (parent[x] == x) {
            return x;
        }

        return parent[x] = find(parent[x]);
    }

    bool unite(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);

        if (rootA == rootB) {
            return false;
        }

        if (componentSize[rootA] < componentSize[rootB]) {
            swap(rootA, rootB);
        }

        parent[rootB] = rootA;
        componentSize[rootA] += componentSize[rootB];

        return true;
    }
};
```

---

# 8. Prerequisite - Min-Heap for Prim

Prim grows one connected tree.

At each step it wants:

```text
the cheapest edge leaving the current tree
```

A min-heap efficiently gives us the smallest candidate.

---

## 8.1 Tiny Example

Current selected vertices:

```text
{1,2}
```

Edges leaving them:

```text
1 -> 4 cost 9
2 -> 3 cost 4
2 -> 5 cost 7
```

We want:

```text
minimum = 4
```

A min-heap keeps the smallest candidate on top.

---

## 8.2 Important Difference from Dijkstra

Both use a priority queue, but the meaning is different.

### Dijkstra stores

```text
best TOTAL distance from source
```

Relaxation:

```text
dist[v] = min(dist[v], dist[u] + weight)
```

### Prim stores

```text
cheapest SINGLE edge that can attach v to the current MST
```

Update:

```text
bestEdge[v] = min(bestEdge[v], weight)
```

There is no:

```text
dist[u] + weight
```

in Prim.

This is the easiest way to avoid mixing the two algorithms.

---

# 9. Kruskal's Algorithm

The lecture's core idea is:

```text
1. Sort all edges by increasing weight.
2. Consider the cheapest edge first.
3. Add it only if it does NOT create a cycle.
4. Continue until N-1 edges are chosen.
```

DSU performs the cycle check.

---

## 9.1 Graph for the Dry Run

Use this graph:

```text
              28
        (1)--------(2)
        /            \
      10              16
      /                \
    (6)                (3)
     |                  |
    25                 12
     |                  |
    (5)------22--------(4)
      \                /
       \24          18/
        \            /
             (7)
              \
              14
               \
               (2)
```

For clarity, use this edge list instead:

```text
(1,6,10)
(3,4,12)
(2,7,14)
(2,3,16)
(7,4,18)
(4,5,22)
(5,7,24)
(5,6,25)
(1,2,28)
```

This follows the lecture's Kruskal visualization.

---

## 9.2 Step 1 - Start with Isolated Vertices

```text
[1] [2] [3] [4] [5] [6] [7]
```

MST cost:

```text
0
```

Chosen edges:

```text
0
```

---

## 9.3 Step 2 - Sort Edges

Sorted:

```text
10 : 1-6
12 : 3-4
14 : 2-7
16 : 2-3
18 : 7-4
22 : 4-5
24 : 5-7
25 : 5-6
28 : 1-2
```

Now scan from top to bottom.

---

## 9.4 Detailed Dry Run

### Edge `1-6`, weight 10

Different components:

```text
[1] [6]
```

Take it.

```text
1---10---6
```

Cost:

```text
10
```

Components:

```text
{1,6} {2} {3} {4} {5} {7}
```

---

### Edge `3-4`, weight 12

Different components.

Take it.

```text
3---12---4
```

Cost:

```text
10 + 12 = 22
```

Components:

```text
{1,6} {3,4} {2} {5} {7}
```

---

### Edge `2-7`, weight 14

Different components.

Take it.

```text
2---14---7
```

Cost:

```text
36
```

Components:

```text
{1,6} {3,4} {2,7} {5}
```

---

### Edge `2-3`, weight 16

`2` is in `{2,7}`.

`3` is in `{3,4}`.

Different components.

Take it.

```text
2---16---3
```

Now:

```text
{2,3,4,7}
```

Cost:

```text
52
```

---

### Edge `7-4`, weight 18

But:

```text
find(7) == find(4)
```

Both are already inside:

```text
{2,3,4,7}
```

Adding the edge would create a cycle:

```text
2 ----- 3
|       |
7 ----- 4
```

Skip it.

Cost stays:

```text
52
```

---

### Edge `4-5`, weight 22

Different components.

Take it.

Cost:

```text
74
```

Components:

```text
{1,6} {2,3,4,5,7}
```

---

### Edge `5-7`, weight 24

Same component.

Skip.

---

### Edge `5-6`, weight 25

`5` and `6` are in different components.

Take it.

Cost:

```text
99
```

Now all 7 vertices belong to one component.

Chosen edges:

```text
6 = N-1
```

Stop.

---

## 9.5 Final MST

Chosen edges:

```text
1---10---6
6---25---5
5---22---4
4---12---3
3---16---2
2---14---7
```

A cleaner tree drawing:

```text
(1)
 |
10
 |
(6)
 |
25
 |
(5)
 |
22
 |
(4)
 |
12
 |
(3)
 |
16
 |
(2)
 |
14
 |
(7)
```

Total:

```text
10 + 12 + 14 + 16 + 22 + 25
= 99
```

---

## 9.6 Kruskal Pseudocode

```text
sort edges by weight

DSU dsu(N)

mstCost = 0
chosen = 0

for each edge (w,u,v):

    if find(u) == find(v):
        skip

    else:
        unite(u,v)
        mstCost += w
        chosen++

        if chosen == N-1:
            stop
```

---

# 10. Why Kruskal Works

The simple intuition:

```text
Always take the cheapest safe edge.
```

"Safe" means:

```text
it joins two currently different components
```

---

## 10.1 Cut Intuition

Imagine the current DSU components are:

```text
LEFT COMPONENT          RIGHT COMPONENT

{1,2,3}                 {4,5,6}
```

To eventually connect the whole graph, at least one edge must cross:

```text
LEFT <-----> RIGHT
```

If the cheapest available edge crossing this separation has weight `w`, choosing a more expensive crossing edge first cannot help reduce total cost.

So the cheapest safe crossing edge can belong to an MST.

This is the core greedy reason behind Kruskal.

---

## 10.2 Why We Reject Same-Component Edges

Suppose we already have:

```text
1---2---3
```

Now consider:

```text
1---3
```

Adding it creates:

```text
1
|\
| \
2--3
```

A cycle.

The cycle contains one unnecessary edge.

So there is no reason to add a cycle edge to a spanning tree.

DSU detects this immediately:

```text
find(1) == find(3)
```

Skip.

---

# 11. Kruskal C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

struct Edge {
    int u;
    int v;
    ll weight;
};

class DSU {
private:
    vector<int> parent;
    vector<int> componentSize;

public:
    DSU(int n) {
        parent.resize(n + 1);
        componentSize.assign(n + 1, 1);

        for (int i = 1; i <= n; i++) {
            parent[i] = i;
        }
    }

    int find(int x) {
        if (parent[x] == x) {
            return x;
        }

        return parent[x] = find(parent[x]);
    }

    bool unite(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);

        if (rootA == rootB) {
            return false;
        }

        if (componentSize[rootA] < componentSize[rootB]) {
            swap(rootA, rootB);
        }

        parent[rootB] = rootA;
        componentSize[rootA] += componentSize[rootB];

        return true;
    }
};

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<Edge> edges(m);

    for (Edge& edge : edges) {
        cin >> edge.u >> edge.v >> edge.weight;
    }

    sort(edges.begin(), edges.end(), [](const Edge& a, const Edge& b) {
        return a.weight < b.weight;
    });

    DSU dsu(n);

    ll mstCost = 0;
    int chosenEdges = 0;

    for (const Edge& edge : edges) {
        if (!dsu.unite(edge.u, edge.v)) {
            continue;
        }

        mstCost += edge.weight;
        chosenEdges++;

        if (chosenEdges == n - 1) {
            break;
        }
    }

    if (chosenEdges != n - 1) {
        cout << "No MST\n";
    } else {
        cout << mstCost << '\n';
    }

    return 0;
}
```

---

## 11.1 Complexity

Sorting:

```text
O(E log E)
```

DSU processing:

```text
O(E alpha(V))
```

Overall:

```text
O(E log E)
```

Space:

```text
O(V + E)
```

The lecture writes the same idea as:

```text
O(V + E log E)
```

with sorting dominating.

---

# 12. Prim's Algorithm

Prim approaches MST differently.

Kruskal thinks:

```text
"Which is the globally smallest safe edge?"
```

Prim thinks:

```text
"I already have one connected tree.
What is the cheapest edge that expands my tree by one new vertex?"
```

---

## 12.1 Start from Any Vertex

Suppose:

```text
start = 1
```

Initially:

```text
Selected = {1}
```

Now examine edges leaving `1`.

Pick the cheapest one reaching an unselected node.

---

## 12.2 Cleaner Visual

Graph:

```text
          4
     (1)------(2)
      | \      |
    2 |  \6    | 3
      |   \    |
     (3)------(4)
          5
```

Start at `1`.

Candidates:

```text
1-2 : 4
1-3 : 2
1-4 : 6
```

Choose:

```text
1-3 : 2
```

Selected:

```text
{1,3}
```

Graph view:

```text
     [1]------(2)
      |
      |2
      |
     [3]------(4)
```

Now candidate edges leaving selected vertices:

```text
1-2 : 4
1-4 : 6
3-4 : 5
```

Choose:

```text
1-2 : 4
```

Selected:

```text
{1,2,3}
```

Candidates to outside:

```text
2-4 : 3
3-4 : 5
1-4 : 6
```

Choose:

```text
2-4 : 3
```

Done.

MST:

```text
(3)
 |
2
 |
(1)---4---(2)
           |
           3
           |
          (4)
```

Cost:

```text
2 + 4 + 3 = 9
```

---

## 12.3 What `bestEdge[v]` Means

This is the key Prim variable.

```text
bestEdge[v]
=
cheapest edge from ANY selected node to v
```

Example:

```text
selected = {1,3}
```

Suppose:

```text
1 -> 4 costs 8
3 -> 4 costs 5
```

Then:

```text
bestEdge[4] = 5
```

We do **not** care about a path sum from the start.

We care only about the cheapest edge that can attach `4` to the current tree.

---

## 12.4 Prim Step by Step

```text
1. Pick any starting vertex.

2. bestEdge[start] = 0.

3. Repeatedly choose the unselected vertex
   having minimum bestEdge.

4. Add that vertex to the MST.

5. For every unselected neighbor:
       bestEdge[neighbor]
       =
       min(bestEdge[neighbor], edgeWeight)

6. Continue until every vertex is selected.
```

---

## 12.5 Heap Version

Instead of scanning all vertices each time, use:

```text
min-heap
```

Heap stores:

```text
(cost to attach, node)
```

When we pop a node already selected:

```text
skip it
```

This is similar to Dijkstra's stale-entry idea.

---

## 12.6 Detailed Dry Run on Lecture-Style Graph

Use the lecture graph:

```text
              28
        (1)--------(2)
        /            \
      10              16
      /                \
    (6)                (3)
     |                  |
    25                 12
     |                  |
    (5)------22--------(4)
      \                /
       \24          18/
        \            /
             (7)
              \
              14
               \
               (2)
```

Start at:

```text
1
```

### Step 1

Selected:

```text
{1}
```

Outgoing:

```text
1-6 = 10
1-2 = 28
```

Choose:

```text
1-6 = 10
```

Cost:

```text
10
```

---

### Step 2

Selected:

```text
{1,6}
```

Crossing candidates:

```text
6-5 = 25
1-2 = 28
```

Choose:

```text
6-5 = 25
```

Cost:

```text
35
```

---

### Step 3

Selected:

```text
{1,6,5}
```

Candidates:

```text
5-4 = 22
5-7 = 24
1-2 = 28
```

Choose:

```text
5-4 = 22
```

Cost:

```text
57
```

---

### Step 4

Selected:

```text
{1,6,5,4}
```

Candidates:

```text
4-3 = 12
4-7 = 18
5-7 = 24
1-2 = 28
```

Choose:

```text
4-3 = 12
```

Cost:

```text
69
```

---

### Step 5

Selected:

```text
{1,6,5,4,3}
```

Candidates:

```text
3-2 = 16
4-7 = 18
5-7 = 24
1-2 = 28
```

Choose:

```text
3-2 = 16
```

Cost:

```text
85
```

---

### Step 6

Selected:

```text
{1,2,3,4,5,6}
```

Candidates reaching `7`:

```text
2-7 = 14
4-7 = 18
5-7 = 24
```

Choose:

```text
2-7 = 14
```

Final:

```text
99
```

Same MST cost as Kruskal.

The **order of thinking** differs, but both construct an MST.

---

# 13. Why Prim Works

At any moment, split the vertices into:

```text
selected
vs
not selected
```

Example:

```text
Selected                  Not Selected

{1,2,4}                   {3,5,6}
```

Any final spanning tree must eventually use an edge crossing from one side to the other.

Prim chooses the cheapest crossing edge.

That edge is safe to add to some MST.

This is again the cut intuition.

---

# 14. Prim C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

using Edge = pair<int, ll>;
using State = pair<ll, int>;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<vector<Edge>> graph(n + 1);

    for (int i = 0; i < m; i++) {
        int u, v;
        ll weight;
        cin >> u >> v >> weight;

        graph[u].push_back({v, weight});
        graph[v].push_back({u, weight});
    }

    const ll INF = 4e18;

    vector<ll> bestEdge(n + 1, INF);
    vector<int> parent(n + 1, -1);
    vector<int> used(n + 1, 0);

    priority_queue<State, vector<State>, greater<State>> pq;

    bestEdge[1] = 0;
    pq.push({0, 1});

    ll mstCost = 0;
    int selectedNodes = 0;

    while (!pq.empty()) {
        auto [cost, node] = pq.top();
        pq.pop();

        if (used[node]) {
            continue;
        }

        used[node] = 1;
        mstCost += cost;
        selectedNodes++;

        for (auto [neighbor, weight] : graph[node]) {
            if (!used[neighbor] && weight < bestEdge[neighbor]) {
                bestEdge[neighbor] = weight;
                parent[neighbor] = node;
                pq.push({weight, neighbor});
            }
        }
    }

    if (selectedNodes != n) {
        cout << "No MST\n";
    } else {
        cout << mstCost << '\n';
    }

    return 0;
}
```

---

## 14.1 Complexity

With adjacency list + min-heap:

```text
O((V + E) log V)
```

Often written as:

```text
O(E log V)
```

for a connected graph.

Space:

```text
O(V + E)
```

---

# 15. Prim vs Kruskal

| Feature | Kruskal | Prim |
|---|---|---|
| Main viewpoint | Global edges | Grow one tree |
| Core structure | DSU | Min-heap / set |
| Main greedy choice | Smallest safe edge anywhere | Smallest edge leaving current tree |
| Cycle handling | DSU explicitly | Never attach an already selected node |
| Good mental model | Merge components | Expand one component |
| Typical sparse complexity | `O(E log E)` | `O(E log V)` |
| Simplicity | Usually simpler | Similar to Dijkstra implementation |

---

## 15.1 Recognition Shortcut

If input naturally gives:

```text
edge list
```

and you already know DSU:

```text
Kruskal is often easiest
```

If input is adjacency based and you want to grow from vertices:

```text
Prim is natural
```

Both produce the same minimum total MST weight.

The actual chosen tree can differ when multiple MSTs exist.

---

# 16. Important MST Properties

The lecture includes several properties worth remembering.

---

## 16.1 Property 1 - Distinct Edge Weights Give a Unique MST

If every edge weight is different:

```text
the MST is unique
```

Why intuitively?

At every greedy choice, there is no tie between equal-weight candidates.

The cheapest safe choice is forced.

---

### Important Logical Direction

The lecture says:

```text
if all edge weights are distinct
→ MST is unique
```

If weights repeat:

```text
there MAY be multiple MSTs
```

Repeated weights do **not** guarantee multiple MSTs.

---

## 16.2 Property 2 - Minimum Product of Edge Weights

The lecture states that an MST also minimizes the product of edge weights by applying logarithms.

This argument is for **positive edge weights**.

Suppose a spanning tree uses:

```text
w1, w2, ..., wk
```

We want to minimize:

```text
w1 * w2 * ... * wk
```

Take log:

```text
log(w1 * w2 * ... * wk)
```

Using:

```text
log(a*b) = log(a) + log(b)
```

we get:

```text
log(w1) + log(w2) + ... + log(wk)
```

Since `log(x)` is strictly increasing for positive `x`, ordering edge weights by:

```text
w
```

is the same as ordering them by:

```text
log(w)
```

So Kruskal chooses the same edges.

Therefore an MST also minimizes the product among spanning trees.

---

## 16.3 Property 3 - Minimum Possible Maximum Edge

Among all spanning trees, an MST minimizes the largest edge weight.

This is called a **minimum bottleneck spanning tree** property.

Example spanning-tree maximum edges:

```text
Tree A -> max edge 10
Tree B -> max edge 12
Tree C -> max edge 15
```

If the MST has maximum edge:

```text
10
```

there cannot be another spanning tree whose maximum edge is smaller than `10`.

---

### Kruskal Intuition

Kruskal keeps using the smallest edges possible.

Suppose the graph finally becomes connected when Kruskal accepts an edge of weight:

```text
W
```

Before weight `W`, the graph could not yet be connected using only smaller edges.

Therefore no spanning tree can avoid using an edge of weight at least:

```text
W
```

So the largest edge in Kruskal's tree is as small as possible.

---

## 16.4 Property 4 - Maximum Spanning Tree

What if instead of minimum total weight we want:

```text
maximum total spanning-tree weight?
```

Two equivalent approaches:

```text
Approach 1:
sort edges in DESCENDING order
and run Kruskal

Approach 2:
replace each weight w by -w
and run minimum spanning tree
```

The lecture presents the sign-change idea.

---

# 17. Problem - Shichikuji and Power Grid

**Codeforces 1245D - Shichikuji and Power Grid**

```text
https://codeforces.com/problemset/problem/1245/D
```

This is the most important modelling application in the lecture.

---

## 17.1 What the Problem Gives

There are:

```text
N cities
```

City `i` has coordinates:

```text
(x[i], y[i])
```

Two ways to provide electricity.

### Option A - Build a power station in city `i`

Cost:

```text
c[i]
```

### Option B - Connect city `i` to city `j`

Cost:

```text
(k[i] + k[j]) *
(|x[i] - x[j]| + |y[i] - y[j]|)
```

Goal:

```text
provide electricity to every city
with minimum total cost
```

---

# 17.2 First Tiny Example

Suppose:

```text
city 1:
station cost = 5

city 2:
station cost = 8

wire 1-2:
cost = 3
```

Possible plan A:

```text
station at 1 = 5
station at 2 = 8

total = 13
```

Possible plan B:

```text
station at 1 = 5
wire 1-2 = 3

total = 8
```

So city 2 can receive electricity through city 1.

This starts looking like a connectivity problem.

---

# 17.3 The Modelling Difficulty

Normal MST edges connect:

```text
city <----> city
```

But here we also have an action:

```text
"build station in city i"
```

That does not look like a normal city-to-city edge.

We need to convert it into one.

This is the key modelling trick.

---

# 17.4 Add a Super Node `0`

Create one extra imaginary vertex:

```text
0 = electricity source
```

Now represent:

```text
build station in city i
```

as an edge:

```text
0 -----c[i]----- i
```

Why?

If the MST chooses:

```text
0 -- i
```

interpret that as:

```text
build a power station in city i
```

---

## 17.5 City-to-City Edges

For every pair of cities:

```text
i, j
```

create an edge:

```text
i -------- j
```

with weight:

```text
(k[i] + k[j]) *
(|x[i] - x[j]| + |y[i] - y[j]|)
```

Interpretation:

```text
build a wire between i and j
```

---

# 17.6 Better Graph Drawing

Suppose we have 4 cities.

Original decision problem:

```text
For every city:

either
    build station

or
    connect through wires to some powered city
```

Transform to graph:

```text
                  SUPER NODE
                     (0)
                  /  |  |  \
               c1/ c2| c3| c4\
                /    |   |    \
              (1)---(2)---(3)---(4)
               |\   / \   / \   /|
               | \ /   \ /   \ / |
               |  X     X     X  |
               | / \   / \   / \ |
               |/   \ /   \ /   \|
             city-to-city wire edges
```

Now ask:

```text
What is the minimum-cost way to connect
0,1,2,...,N?
```

That is exactly:

```text
MST
```

---

# 17.7 Why the Super Node Model Works

Every valid electricity plan has:

```text
some station cities
+
some wire connections
```

Convert it to graph edges:

```text
station at i
→ edge (0,i)

wire i-j
→ edge (i,j)
```

If every city receives electricity, then every city has a path to node `0`.

So:

```text
0 and all cities are connected
```

We want minimum cost.

Cycles are unnecessary because if the chosen construction contains a cycle, one edge can be removed while keeping the relevant vertices connected.

Therefore an optimal solution can be represented as a spanning tree of:

```text
N + 1 nodes
```

including super node `0`.

So the problem becomes MST.

---

# 17.8 What the Chosen MST Edge Means

```text
edge 0-i
→ build power station at city i

edge i-j
→ connect cities i and j by wire
```

Very important:

```text
The MST itself directly gives both parts of the required output.
```

---

# 17.9 Should We Explicitly Build All O(N^2) Edges?

There are:

```text
N cities
```

Every pair can be connected.

Number of city-city edges:

```text
N(N-1)/2
```

So the graph is dense.

Instead of materializing every edge and sorting all of them for Kruskal, we can use a simple `O(N^2)` Prim implementation.

This is a self-study implementation choice added to the lecture's super-node model.

---

# 17.10 Prim Without Explicitly Creating Node 0

We can simulate all super-node edges during initialization.

For each city `i`:

```text
bestCost[i] = c[i]
parent[i] = 0
```

Meaning initially:

```text
the cheapest known way to connect city i
is to build its own power station
```

Then whenever a city `v` enters the MST, try using a wire:

```text
v ---- u
```

to improve every unused city `u`.

If:

```text
wireCost(v,u) < bestCost[u]
```

then update:

```text
bestCost[u] = wireCost(v,u)
parent[u] = v
```

Interpretation:

```text
connecting u by wire through v
is now cheaper than u's previous plan
```

---

# 17.11 Tiny Dry Run

Three cities.

Station costs:

```text
c1 = 3
c2 = 10
c3 = 8
```

Wire costs:

```text
1-2 = 4
1-3 = 6
2-3 = 2
```

Initial:

```text
city 1: best = 3,  parent = 0
city 2: best = 10, parent = 0
city 3: best = 8,  parent = 0
```

Meaning:

```text
build station in each city
```

---

### Step 1 - Choose City 1

Smallest best cost:

```text
3
```

Choose city `1`.

Since:

```text
parent[1] = 0
```

build station at `1`.

Total:

```text
3
```

Now relax wires from `1`.

For city 2:

```text
wire 1-2 = 4 < 10
```

Update:

```text
best[2] = 4
parent[2] = 1
```

For city 3:

```text
wire 1-3 = 6 < 8
```

Update:

```text
best[3] = 6
parent[3] = 1
```

---

### Step 2 - Choose City 2

Current:

```text
city 2 best = 4
city 3 best = 6
```

Choose city `2`.

Parent:

```text
1
```

So add wire:

```text
1---2
```

Total:

```text
3 + 4 = 7
```

Relax edge `2-3`.

```text
2 < 6
```

Update:

```text
best[3] = 2
parent[3] = 2
```

---

### Step 3 - Choose City 3

Choose cost:

```text
2
```

Parent:

```text
2
```

Add:

```text
2---3
```

Total:

```text
3 + 4 + 2 = 9
```

Final construction:

```text
      station
         |
        (1)
         |
         4
         |
        (2)
         |
         2
         |
        (3)
```

Total:

```text
9
```

Instead of building three stations:

```text
3 + 10 + 8 = 21
```

---

# 17.12 Full Algorithm

```text
1. Read coordinates.

2. Read station cost c[i].

3. Read coefficient k[i].

4. For every city i:
       bestCost[i] = c[i]
       parent[i] = 0

5. Repeat N times:

       a. choose unused city v
          with minimum bestCost[v]

       b. add bestCost[v] to answer

       c. if parent[v] == 0:
              build station at v
          else:
              build wire parent[v] - v

       d. mark v used

       e. for every unused city u:
              wireCost =
                  (k[v] + k[u]) *
                  ManhattanDistance(v,u)

              if wireCost < bestCost[u]:
                  update bestCost[u]
                  parent[u] = v
```

---

# 17.13 Wire Cost Derivation

The lecture gives:

```text
cost(i,j)
=
(k[i] + k[j]) *
(|x[i] - x[j]| + |y[i] - y[j]|)
```

The term:

```text
|x[i] - x[j]| + |y[i] - y[j]|
```

is Manhattan distance.

Example:

```text
city i = (2,3)
city j = (7,5)

distance
=
|2-7| + |3-5|
=
5 + 2
=
7
```

If:

```text
k[i] = 4
k[j] = 2
```

then:

```text
wire cost
=
(4+2) * 7
=
42
```

Use `long long`.

---

# 18. Power Grid Complete C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<ll> x(n + 1);
    vector<ll> y(n + 1);

    for (int city = 1; city <= n; city++) {
        cin >> x[city] >> y[city];
    }

    vector<ll> stationCost(n + 1);
    vector<ll> k(n + 1);

    for (int city = 1; city <= n; city++) {
        cin >> stationCost[city];
    }

    for (int city = 1; city <= n; city++) {
        cin >> k[city];
    }

    vector<ll> bestCost(n + 1);
    vector<int> parent(n + 1, 0);
    vector<int> used(n + 1, 0);

    for (int city = 1; city <= n; city++) {
        bestCost[city] = stationCost[city];
    }

    ll totalCost = 0;

    vector<int> stations;
    vector<pair<int, int>> connections;

    for (int step = 0; step < n; step++) {
        int city = -1;

        for (int candidate = 1; candidate <= n; candidate++) {
            if (used[candidate]) {
                continue;
            }

            if (city == -1 || bestCost[candidate] < bestCost[city]) {
                city = candidate;
            }
        }

        used[city] = 1;
        totalCost += bestCost[city];

        if (parent[city] == 0) {
            stations.push_back(city);
        } else {
            connections.push_back({parent[city], city});
        }

        for (int nextCity = 1; nextCity <= n; nextCity++) {
            if (used[nextCity]) {
                continue;
            }

            ll distance = abs(x[city] - x[nextCity]) + abs(y[city] - y[nextCity]);
            ll wireCost = (k[city] + k[nextCity]) * distance;

            if (wireCost < bestCost[nextCity]) {
                bestCost[nextCity] = wireCost;
                parent[nextCity] = city;
            }
        }
    }

    cout << totalCost << '\n';

    cout << stations.size() << '\n';

    for (int city : stations) {
        cout << city << ' ';
    }

    cout << '\n';

    cout << connections.size() << '\n';

    for (auto [cityA, cityB] : connections) {
        cout << cityA << ' ' << cityB << '\n';
    }

    return 0;
}
```

---

## 18.1 Complexity

We choose one city `N` times.

Each time we scan all `N` cities to:

```text
find minimum
+
relax possible wires
```

Therefore:

```text
Time = O(N^2)
```

Arrays:

```text
Space = O(N)
```

We avoid explicitly storing all:

```text
O(N^2)
```

city-to-city edges.

---

# 19. Common Mistakes

## 19.1 Thinking MST Means Shortest Paths

Wrong mental model:

```text
minimum cost from one source to everyone
```

That is shortest-path territory.

MST:

```text
minimum total cost of the NETWORK itself
```

---

## 19.2 Kruskal - Adding an Edge Inside the Same Component

If:

```text
find(u) == find(v)
```

adding `(u,v)` creates a cycle.

Skip it.

---

## 19.3 Kruskal - Forgetting to Sort by Weight

Kruskal without sorted edges is not Kruskal.

Core order:

```text
smallest -> largest
```

---

## 19.4 Kruskal - Forgetting Disconnected Graph Case

An MST exists only if all vertices can become connected.

Check:

```text
chosenEdges == N-1
```

Otherwise:

```text
no spanning tree exists
```

---

## 19.5 Prim - Using `dist[u] + weight`

That is Dijkstra thinking.

Prim updates:

```text
bestEdge[v] = min(bestEdge[v], weight)
```

not:

```text
bestEdge[u] + weight
```

---

## 19.6 Prim - Adding a Vertex Twice

Priority queues may contain old entries.

Always keep:

```text
used[node]
```

and skip if already selected.

---

## 19.7 Power Grid - Forgetting the Super Node Meaning

Remember:

```text
parent[i] = 0
```

means:

```text
build a station in city i
```

while:

```text
parent[i] = j
```

means:

```text
build wire j-i
```

---

## 19.8 Power Grid - Using `int`

The multiplication:

```text
(k[i] + k[j]) * distance
```

can be large.

Use:

```cpp
long long
```

for coordinates, costs, and the total.

---

# 20. Final Recognition Model

When a problem says:

```text
connect every node
+
choose some weighted edges
+
minimize total selected edge weight
```

think:

```text
MST
```

---

## 20.1 Kruskal Mental Picture

```text
sort ALL edges
      ↓
smallest first
      ↓
different components?
   /            \
 YES             NO
  |               |
take            skip
  |
union
```

---

## 20.2 Prim Mental Picture

```text
start with one node

        selected tree
             |
             v
   cheapest edge going OUT
             |
             v
        add new node
             |
             v
           repeat
```

---

## 20.3 Super Node Mental Picture

When a problem has an alternative like:

```text
either build something independently at node i
or connect i to another node
```

try:

```text
add super node 0

0 -- independentCost[i] -- i
```

Then ordinary connectivity may become:

```text
MST on augmented graph
```

This pattern appears far beyond power stations:

```text
build facility vs connect to facility
airport vs road
server vs network link
water source vs pipe
generator vs power cable
```

---

# 21. Practice Checklist

Before moving to Tree, make sure you can answer these without notes:

```text
[ ] What makes a graph a tree?

[ ] Why does a tree with N vertices have N-1 edges?

[ ] What is the difference between a spanning tree and MST?

[ ] Why does Kruskal need DSU?

[ ] What does find(u) == find(v) mean during Kruskal?

[ ] Why does Kruskal sort edges?

[ ] What does Prim's bestEdge[v] mean?

[ ] How is Prim different from Dijkstra?

[ ] When are MST edges unique?

[ ] What is the minimum-bottleneck property?

[ ] How do we build a maximum spanning tree?

[ ] How can an "independent build cost" become a super-node edge?

[ ] Why does Codeforces 1245D become an MST problem?
```

If these are clear, your MST foundation is strong enough to continue into tree algorithms.

---

# One-Line Revision

```text
MST:
connect every vertex with minimum total edge cost.

Kruskal:
sort edges + DSU + skip cycles.

Prim:
grow one tree using cheapest outgoing edge.

Power Grid:
add super node 0;
0-i means station,
i-j means wire;
find MST.
```
