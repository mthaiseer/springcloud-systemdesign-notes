# BFS Application — Shortest Path, BFS Layers & Shortest-Path Algorithm Selection

> **Goal:** understand exactly **when BFS is a shortest-path algorithm**, why BFS processes nodes in **distance layers**, and how to choose between BFS, 0-1 BFS, Dijkstra, Bellman-Ford, and Floyd-Warshall.
>
> Core recognition:
>
> ```text
> Every edge has equal cost
>         +
> shortest path from one source
>         ↓
>        BFS
> ```

---

## Table of Contents

1. [What This Lecture Is Teaching](#1-what-this-lecture-is-teaching)
2. [Prerequisite 1 — Path Cost](#2-prerequisite-1--path-cost)
3. [Prerequisite 2 — Unweighted vs Weighted Graph](#3-prerequisite-2--unweighted-vs-weighted-graph)
4. [Shortest-Path Algorithm Decision Map](#4-shortest-path-algorithm-decision-map)
5. [Why Ordinary BFS Works for Unweighted Graphs](#5-why-ordinary-bfs-works-for-unweighted-graphs)
6. [BFS Layer Concept](#6-bfs-layer-concept)
7. [Lecture Graph as Distance Layers](#7-lecture-graph-as-distance-layers)
8. [Step-by-Step Layer Formation](#8-step-by-step-layer-formation)
9. [Queue View of the Same BFS](#9-queue-view-of-the-same-bfs)
10. [Why Layer Number Equals Shortest Distance](#10-why-layer-number-equals-shortest-distance)
11. [Distance Relaxation in BFS](#11-distance-relaxation-in-bfs)
12. [Detailed Dry Run](#12-detailed-dry-run)
13. [BFS Shortest Path C++17](#13-bfs-shortest-path-c17)
14. [Actual Shortest Path with Parent](#14-actual-shortest-path-with-parent)
15. [0-1 BFS — Why It Is Different](#15-0-1-bfs--why-it-is-different)
16. [Dijkstra — When BFS Is No Longer Enough](#16-dijkstra--when-bfs-is-no-longer-enough)
17. [Bellman-Ford](#17-bellman-ford)
18. [Floyd-Warshall](#18-floyd-warshall)
19. [Comparison Table](#19-comparison-table)
20. [Common Mistakes](#20-common-mistakes)
21. [Recognition Checklist](#21-recognition-checklist)
22. [Final Mental Model](#22-final-mental-model)

---

# 1. What This Lecture Is Teaching

The lecture starts with the general topic:

```text
Shortest Path
```

Then it places the common algorithms under that topic:

```text
Shortest Path
│
├── BFS
├── 0-1 BFS
├── Dijkstra
├── Bellman-Ford
└── Floyd-Warshall
```

The important point is:

```text
There is no single shortest-path algorithm for every graph.
```

The correct algorithm depends mainly on:

```text
edge weights
+
number/type of shortest-path queries
```

---

# 2. Prerequisite 1 — Path Cost

Consider:

```text
A --5-- B --2-- C --5-- D
```

The path:

```text
A → B → C → D
```

has total cost:

```text
5 + 2 + 5 = 12
```

For a weighted graph:

```text
path cost = sum of edge weights
```

So shortest path means:

```text
among all valid paths,
choose the path whose total edge cost is minimum
```

---

## Tiny Example

```text
A --4-- B
 \      /
  2    1
   \  /
    C
```

Path:

```text
A → B
cost = 4
```

Alternative:

```text
A → C → B
cost = 2 + 1 = 3
```

Therefore:

```text
shortest(A,B) = 3
```

This shows why merely counting edges is not enough when weights differ.

---

# 3. Prerequisite 2 — Unweighted vs Weighted Graph

## Unweighted Graph

Example:

```text
1 -- 2 -- 4
|    |
3 -- 5
```

Every edge effectively costs:

```text
1
```

Therefore:

```text
path cost = number of edges
```

Example:

```text
1 → 2 → 4
```

uses 2 edges.

So:

```text
distance = 2
```

---

## Weighted Graph

Example:

```text
1 --7-- 2
 \      /
  2    1
   \  /
    3
```

Now:

```text
1 → 2
cost = 7
```

but:

```text
1 → 3 → 2
cost = 2 + 1 = 3
```

So ordinary BFS cannot simply use number of edges.

---

# 4. Shortest-Path Algorithm Decision Map

This is the most useful prerequisite from the lecture.

```text
                     SHORTEST PATH
                           |
              +------------+-------------+
              |                          |
       all edges = 1                weighted edges
              |                          |
             BFS              +-----------+-----------+
                              |                       |
                         weights 0/1          general weights
                              |                       |
                           0-1 BFS             +------+------+
                                               |             |
                                        non-negative     negative edges
                                               |             |
                                           Dijkstra     Bellman-Ford
```

For all-pairs shortest paths:

```text
need distance between every pair
              ↓
       Floyd-Warshall
```

A compact recognition table:

| Graph / Requirement | Typical Algorithm |
|---|---|
| Unweighted / every edge cost `1` | BFS |
| Edge weights only `0` or `1` | 0-1 BFS |
| Non-negative weighted graph | Dijkstra |
| Negative edges may exist | Bellman-Ford |
| All-pairs shortest path, usually smaller `N` | Floyd-Warshall |

---

# 5. Why Ordinary BFS Works for Unweighted Graphs

Suppose all edges cost:

```text
1
```

Then:

```text
1 edge  = cost 1
2 edges = cost 2
3 edges = cost 3
...
```

Therefore the shortest path is exactly:

```text
the path using the minimum number of edges
```

BFS explores nodes in that exact order:

```text
0 edges away
1 edge away
2 edges away
3 edges away
...
```

So BFS naturally solves the problem.

---

# 6. BFS Layer Concept

Think of the source as the center.

```text
                 Layer 3
            ----------------

                 Layer 2
              ------------

                 Layer 1
                 ------

                [SOURCE]
                 Layer 0
```

Formal meaning:

```text
Layer 0 = source

Layer 1 =
all nodes whose shortest distance from source is 1

Layer 2 =
all nodes whose shortest distance from source is 2

Layer 3 =
all nodes whose shortest distance from source is 3
```

This is the central idea shown by the lecture's red curved boundaries.

---

# 7. Lecture Graph as Distance Layers

Using the node grouping shown in the lecture:

```text
Layer 0:
{1}

Layer 1:
{2, 3}

Layer 2:
{5, 4}

Layer 3:
{6, 7}

Layer 4:
{8}
```

Visual form:

```text
                 DISTANCE FROM SOURCE

      0          1          2          3          4

     {1}      {2,3}      {5,4}      {6,7}       {8}
      |          |          |          |          |
      +----------+----------+----------+----------+
            BFS moves layer by layer →
```

A graph-like visualization:

```text
                  (2) -------- (5) -------- (6)
                 /                           |
               (1)                          (7) ---- (8)
                 \                         /
                  (3) -------- (4) ------

       | L0 |     | L1 |     | L2 |     | L3 |   | L4 |
```

The exact drawing shape does not matter.

What matters is:

```text
distance 0 → distance 1 → distance 2 → distance 3 → distance 4
```

---

# 8. Step-by-Step Layer Formation

Start at node `1`.

## Step 0 — Source

```text
Layer 0 = {1}
```

Distance:

```text
dist[1] = 0
```

---

## Step 1 — Direct Neighbors

From `1`, suppose BFS discovers:

```text
2 and 3
```

So:

```text
Layer 1 = {2,3}
```

Distances:

```text
dist[2] = 1
dist[3] = 1
```

Diagram:

```text
       (2)
      /
    (1)
      \
       (3)
```

---

## Step 2 — One More Edge

From nodes in Layer 1, BFS discovers:

```text
5 and 4
```

So:

```text
Layer 2 = {5,4}
```

Distances:

```text
dist[5] = 2
dist[4] = 2
```

Diagram:

```text
       (2) ---- (5)
      /
    (1)
      \
       (3) ---- (4)
```

---

## Step 3

Next BFS discovers:

```text
6 and 7
```

So:

```text
Layer 3 = {6,7}
```

Distances:

```text
dist[6] = 3
dist[7] = 3
```

---

## Step 4

Finally:

```text
Layer 4 = {8}
```

Therefore:

```text
dist[8] = 4
```

The whole picture becomes:

```text
L0          L1          L2          L3         L4

{1}   →   {2,3}   →   {5,4}   →   {6,7}   →  {8}

 0           1           2           3          4
```

---

# 9. Queue View of the Same BFS

The queue preserves the layer order.

Initial:

```text
queue = [1]
```

Process `1`:

```text
discover 2,3

queue = [2,3]
```

Now every node currently in the queue is at distance:

```text
1
```

Process `2` and `3`:

```text
discover 5,4

queue = [5,4]
```

These are distance:

```text
2
```

Then:

```text
queue = [6,7]
```

distance:

```text
3
```

Then:

```text
queue = [8]
```

distance:

```text
4
```

So queue order becomes:

```text
[ 1 | 2 3 | 5 4 | 6 7 | 8 ]
   0    1      2      3    4
```

The vertical separators are the BFS layers.

---

# 10. Why Layer Number Equals Shortest Distance

Suppose node `x` is first discovered in:

```text
Layer k
```

Then BFS has found a path:

```text
source → ... → x
```

with exactly:

```text
k edges
```

Could there be a shorter path of `k-1` edges?

No.

If such a path existed, node `x` would have been discovered while processing:

```text
Layer k-2
```

and therefore it would already belong to:

```text
Layer k-1
```

Contradiction.

Therefore:

```text
first BFS layer containing x
=
shortest number of edges from source to x
```

This is the proof behind BFS shortest path.

---

# 11. Distance Relaxation in BFS

Suppose BFS is currently at:

```text
node
```

and sees an unvisited:

```text
neighbor
```

The edge costs `1`.

So:

```text
dist[neighbor]
=
dist[node] + 1
```

Example:

```text
dist[node] = 2
```

Then:

```text
node --1--> neighbor
```

gives:

```text
dist[neighbor] = 3
```

Code:

```cpp
if (dist[neighbor] == -1) {
    dist[neighbor] = dist[node] + 1;
    q.push(neighbor);
}
```

Here:

```text
dist == -1
```

also acts as the visited check.

---

# 12. Detailed Dry Run

Consider the lecture-style layering:

```text
L0 = {1}
L1 = {2,3}
L2 = {5,4}
L3 = {6,7}
L4 = {8}
```

Start:

```text
dist[1] = 0
queue = [1]
```

Compact dry run:

```text
Step 1: pop 1
        discover 2,3
        dist[2]=1, dist[3]=1
        queue=[2,3]

Step 2: pop 2
        discover 5
        dist[5]=2
        queue=[3,5]

Step 3: pop 3
        discover 4
        dist[4]=2
        queue=[5,4]

Step 4: pop 5
        discover 6
        dist[6]=3
        queue=[4,6]

Step 5: pop 4
        discover 7
        dist[7]=3
        queue=[6,7]

Step 6: pop 6
        no new node
        queue=[7]

Step 7: pop 7
        discover 8
        dist[8]=4
        queue=[8]

Step 8: pop 8
        no new node
        queue=[]
```

Final distances:

```text
node:  1  2  3  4  5  6  7  8
dist:  0  1  1  2  2  3  3  4
```

Exactly the BFS layers.

---

# 13. BFS Shortest Path C++17

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
        int a, b;
        cin >> a >> b;

        graph[a].push_back(b);
        graph[b].push_back(a);
    }

    int source;
    cin >> source;

    vector<int> dist(n + 1, -1);
    queue<int> q;

    dist[source] = 0;
    q.push(source);

    while (!q.empty()) {
        int node = q.front();
        q.pop();

        for (int neighbor : graph[node]) {
            if (dist[neighbor] == -1) {
                dist[neighbor] = dist[node] + 1;
                q.push(neighbor);
            }
        }
    }

    for (int node = 1; node <= n; node++) {
        cout << node << ": " << dist[node] << '\n';
    }

    return 0;
}
```

---

# 14. Actual Shortest Path with Parent

Distance answers:

```text
How short is the path?
```

Parent answers:

```text
Which path is it?
```

When discovering a node:

```cpp
parent[neighbor] = node;
```

Example:

```text
1 → 3 → 4 → 7 → 8
```

Parent chain:

```text
parent[8] = 7
parent[7] = 4
parent[4] = 3
parent[3] = 1
```

Backtrack:

```text
8 → 7 → 4 → 3 → 1
```

Reverse:

```text
1 → 3 → 4 → 7 → 8
```

---

## C++17

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
        int a, b;
        cin >> a >> b;

        graph[a].push_back(b);
        graph[b].push_back(a);
    }

    int source, target;
    cin >> source >> target;

    vector<int> dist(n + 1, -1);
    vector<int> parent(n + 1, -1);

    queue<int> q;

    dist[source] = 0;
    q.push(source);

    while (!q.empty()) {
        int node = q.front();
        q.pop();

        for (int neighbor : graph[node]) {
            if (dist[neighbor] == -1) {
                dist[neighbor] = dist[node] + 1;
                parent[neighbor] = node;
                q.push(neighbor);
            }
        }
    }

    if (dist[target] == -1) {
        cout << "Not reachable\n";
        return 0;
    }

    vector<int> path;

    for (int node = target; node != -1; node = parent[node]) {
        path.push_back(node);
    }

    reverse(path.begin(), path.end());

    cout << "Distance = " << dist[target] << '\n';

    for (int node : path) {
        cout << node << ' ';
    }

    cout << '\n';

    return 0;
}
```

---

# 15. 0-1 BFS — Why It Is Different

The lecture lists:

```text
0-1 BFS
```

This is for edges whose weights are only:

```text
0 or 1
```

Example:

```text
A --0-- B --1-- C
```

Ordinary BFS treats both edges as equal.

But their costs differ.

0-1 BFS uses a:

```text
deque
```

Rule:

```text
weight 0 edge
→ push_front()

weight 1 edge
→ push_back()
```

Mental model:

```text
0-cost move should be processed immediately
1-cost move goes to the next cost layer
```

This section is only a prerequisite/recognition note here; the detailed 0-1 BFS algorithm belongs in its own note.

---

# 16. Dijkstra — When BFS Is No Longer Enough

Suppose:

```text
A --10-- B
 \       /
  2     2
   \   /
     C
```

BFS thinks:

```text
A → B
```

uses only 1 edge.

But cost:

```text
10
```

Alternative:

```text
A → C → B
```

uses 2 edges but costs:

```text
2 + 2 = 4
```

So:

```text
minimum edges ≠ minimum weighted cost
```

For non-negative general weights, think:

```text
Dijkstra
```

---

# 17. Bellman-Ford

Bellman-Ford is important when:

```text
negative edges may exist
```

Example:

```text
A --4--> B
A --7--> C
C --(-5)--> B
```

Path:

```text
A → C → B
```

cost:

```text
7 + (-5) = 2
```

This beats direct cost:

```text
4
```

Recognition:

```text
negative edge weights
        ↓
Bellman-Ford
```

It can also be used to detect reachable negative cycles.

Detailed mechanics belong in a later shortest-path note.

---

# 18. Floyd-Warshall

Suppose the question repeatedly asks:

```text
distance(a,b)
distance(c,d)
distance(x,y)
...
```

for many pairs.

Instead of running one-source shortest path each time, Floyd-Warshall computes:

```text
shortest distance between every pair of vertices
```

Recognition:

```text
All-Pairs Shortest Path
          ↓
    Floyd-Warshall
```

Typical complexity:

```text
O(N^3)
```

So it is generally suitable only when `N` is small enough.

---

# 19. Comparison Table

| Algorithm | Edge Weights | Main Use | Typical Complexity |
|---|---|---|---|
| BFS | all equal / unweighted | SSSP | `O(V + E)` |
| 0-1 BFS | only `0` and `1` | SSSP | `O(V + E)` |
| Dijkstra | non-negative | SSSP | commonly `O((V+E) log V)` |
| Bellman-Ford | negative edges allowed | SSSP | `O(VE)` |
| Floyd-Warshall | general APSP setting | all pairs | `O(V^3)` |

Do not choose by memorizing names.

Choose by asking:

```text
What kind of edge costs do I have?
What shortest-path query is required?
```

---

# 20. Common Mistakes

## Mistake 1 — “Shortest path means BFS”

Not always.

Correct:

```text
unweighted / equal-weight shortest path
→ BFS
```

Weighted graphs need another algorithm depending on weights.

---

## Mistake 2 — Thinking BFS order itself is always unique

Suppose:

```text
Layer 1 = {2,3}
```

BFS may process:

```text
2 then 3
```

or:

```text
3 then 2
```

depending on adjacency order.

But both remain in the same distance layer.

The important property is:

```text
all Layer d nodes
before Layer d+1
```

---

## Mistake 3 — Updating an already discovered node in ordinary BFS

For an unweighted graph:

```text
first discovery is already optimal
```

So once:

```text
dist[node] != -1
```

you do not need to rediscover it.

---

## Mistake 4 — Counting queue position as distance

Wrong:

```text
distance = position in traversal order
```

Correct:

```text
distance = BFS layer
```

Example:

```text
traversal:
1,2,3,5,4,6,7,8

but distances:
0,1,1,2,2,3,3,4
```

---

## Mistake 5 — Forgetting unreachable nodes

If a node is never discovered:

```text
dist[node] = -1
```

It is not reachable from the source.

---

# 21. Recognition Checklist

When you see a shortest-path problem, ask in this order:

```text
1. Is this really a graph/state graph?

2. What does one edge/move cost?

3. Are all costs exactly equal?
      YES → BFS

4. Are costs only 0 or 1?
      YES → 0-1 BFS

5. Are all weights non-negative?
      YES → Dijkstra

6. Can negative weights occur?
      → Bellman-Ford may be needed

7. Do I need shortest distances for every pair?
      → consider Floyd-Warshall
```

For ordinary BFS specifically:

```text
minimum number of edges
minimum number of moves
minimum number of steps
unweighted shortest path
```

should immediately trigger:

```text
BFS
```

---

# 22. Final Mental Model

The lecture's most important picture is not the queue code.

It is this:

```text
Source

  Layer 0
     |
  Layer 1
     |
  Layer 2
     |
  Layer 3
     |
  Layer 4
```

For the lecture example:

```text
L0      L1        L2        L3       L4

{1} → {2,3} → {5,4} → {6,7} → {8}

 0       1         2         3        4
```

Therefore:

```text
BFS layer number
=
minimum number of edges from source
=
shortest-path distance in an unweighted graph
```

And the algorithm-selection mental model is:

```text
Shortest Path
│
├── equal edge costs      → BFS
├── weights 0/1           → 0-1 BFS
├── non-negative weights  → Dijkstra
├── negative edges        → Bellman-Ford
└── all pairs             → Floyd-Warshall
```

That is the recognition skill to carry into contest problems.
