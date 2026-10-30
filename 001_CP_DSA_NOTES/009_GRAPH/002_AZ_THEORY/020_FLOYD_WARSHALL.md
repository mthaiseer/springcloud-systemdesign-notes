# Floyd–Warshall — All-Pairs Shortest Paths (APSP)

> **Self-study CP note** based on the supplied AlgoZenith Floyd–Warshall lecture screenshots.  
> **Lecture-visible ideas preserved:** APSP, comparison with running SSSP from every source, distance matrix, `k → i → j` loops, transition `dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j])`, DP interpretation, path/parent reconstruction idea, `O(V^3)`, and using the final matrix for graph diameter.  
> **Added for self-study:** prerequisites, tiny examples, one complete matrix dry run, negative-edge notes, negative-cycle detection, clean C++17, common mistakes, and recognition rules.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [From SSSP to APSP](#2-from-sssp-to-apsp)
3. [Why Floyd–Warshall Exists](#3-why-floydwarshall-exists)
4. [Distance Matrix](#4-distance-matrix)
5. [Matrix Initialization](#5-matrix-initialization)
6. [The Core Observation](#6-the-core-observation)
7. [The DP State](#7-the-dp-state)
8. [Deriving the Transition](#8-deriving-the-transition)
9. [Why the Loop Order Must Be k-i-j](#9-why-the-loop-order-must-be-k-i-j)
10. [Step-by-Step Algorithm](#10-step-by-step-algorithm)
11. [Detailed Matrix Dry Run](#11-detailed-matrix-dry-run)
12. [Clean C++17 — Floyd–Warshall](#12-clean-c17--floydwarshall)
13. [Multiple Edges Between Same Pair](#13-multiple-edges-between-same-pair)
14. [Directed vs Undirected Graph](#14-directed-vs-undirected-graph)
15. [Negative Edges](#15-negative-edges)
16. [Negative-Cycle Detection](#16-negative-cycle-detection)
17. [Path Reconstruction](#17-path-reconstruction)
18. [Graph Diameter from Floyd–Warshall](#18-graph-diameter-from-floydwarshall)
19. [Complexity](#19-complexity)
20. [Floyd–Warshall vs Repeated Dijkstra vs Bellman–Ford](#20-floydwarshall-vs-repeated-dijkstra-vs-bellmanford)
21. [Boolean / Reachability Variant](#21-boolean--reachability-variant)
22. [Common Mistakes](#22-common-mistakes)
23. [Recognition Model](#23-recognition-model)
24. [Final Mental Model](#24-final-mental-model)

---

# 1. Prerequisites

Before Floyd–Warshall, you should know:

- graph vertices and edges,
- directed vs undirected graphs,
- weighted graphs,
- shortest path,
- SSSP = Single Source Shortest Path,
- APSP = All Pairs Shortest Path,
- adjacency matrix,
- `INF`,
- shortest-path relaxation,
- dynamic programming idea,
- `long long` for path sums.

Recommended learning chain:

```text
BFS
  ↓
0-1 BFS
  ↓
Dijkstra
  ↓
Bellman–Ford
  ↓
Floyd–Warshall
```

The important shift is:

```text
SSSP
source → every node

becomes

APSP
every node → every node
```

---

# 2. From SSSP to APSP

## 2.1 SSSP

Single Source Shortest Path means:

```text
given one source S
find shortest distance from S to every vertex
```

Example:

```text
source = 1

need:
1 → 1
1 → 2
1 → 3
...
1 → N
```

Algorithms:

```text
BFS
0-1 BFS
Dijkstra
Bellman–Ford
```

depending on the edge weights.

---

## 2.2 APSP

All Pairs Shortest Path means:

```text
for every pair (i, j)
find shortest distance from i to j
```

For `N` vertices:

```text
1 → 1, 1 → 2, ..., 1 → N
2 → 1, 2 → 2, ..., 2 → N
...
N → 1, N → 2, ..., N → N
```

That naturally suggests an:

```text
N × N matrix
```

where:

```text
dist[i][j]
```

means:

```text
shortest known distance from i to j
```

---

# 3. Why Floyd–Warshall Exists

Suppose we need APSP.

One option is:

```text
Run an SSSP algorithm from every source.
```

### Repeated Dijkstra

If all weights are non-negative:

```text
one Dijkstra ≈ O(E log V)
```

Run from every vertex:

```text
O(V × E log V)
```

For a dense graph:

```text
E ≈ V²
```

so approximately:

```text
O(V³ log V)
```

### Repeated Bellman–Ford

One Bellman–Ford:

```text
O(VE)
```

From every source:

```text
O(V²E)
```

For dense graphs:

```text
E ≈ V²
```

so:

```text
O(V⁴)
```

### Floyd–Warshall

Floyd–Warshall computes all pairs directly in:

```text
O(V³)
```

That is the key motivation shown in the lecture.

For moderate `V`, especially dense graphs:

```text
Floyd–Warshall can be very clean and practical.
```

---

# 4. Distance Matrix

For `N = 3`, we store:

```text
        TO
       1   2   3
     +------------
F  1 | ?   ?   ?
R  2 | ?   ?   ?
O  3 | ?   ?   ?
M
```

Meaning:

```text
dist[1][2]
= shortest distance from node 1 to node 2
```

For a directed graph:

```text
dist[i][j] may differ from dist[j][i]
```

For an undirected graph:

```text
dist[i][j] = dist[j][i]
```

after all shortest paths are computed.

---

# 5. Matrix Initialization

For every pair `(i, j)`:

```text
if i == j:
    dist[i][j] = 0
else:
    dist[i][j] = INF
```

Then insert all direct edges.

## 5.1 Why diagonal is zero

Distance from a node to itself using no edges:

```text
i → i
```

cost:

```text
0
```

So:

```text
dist[i][i] = 0
```

## 5.2 Why other cells start as INF

If we do not yet know a route:

```text
i → j
```

we write:

```text
INF
```

Meaning:

```text
currently unreachable
```

## 5.3 Tiny initialization example

Graph:

```text
1 --2--> 3
2 --4--> 3
3 --1--> 1
```

Initial matrix:

```text
      1    2    3

1     0   INF   2
2    INF   0    4
3     1   INF   0
```

This represents only:

```text
zero-edge self paths
+
direct edges
```

No intermediate vertices have been used yet.

---

# 6. The Core Observation

Suppose we want:

```text
shortest path from i to j
```

and we are considering whether node `k` can help.

There are two possibilities.

### Option 1 — Do not use k

Keep:

```text
dist[i][j]
```

### Option 2 — Use k

Path:

```text
i → ... → k → ... → j
```

Cost:

```text
dist[i][k] + dist[k][j]
```

Therefore:

```text
dist[i][j]
=
min(
    dist[i][j],
    dist[i][k] + dist[k][j]
)
```

This is the entire Floyd–Warshall transition.

---

# 7. The DP State

A clean formal state is:

```text
dp[k][i][j]
```

Meaning:

```text
shortest path from i to j
using only vertices {1, 2, ..., k}
as intermediate vertices
```

Important:

```text
i and j are endpoints.

k controls which intermediate vertices are allowed.
```

## 7.1 Base case

Before allowing any intermediate vertex:

```text
dp[0][i][j]
```

contains:

```text
0       if i == j
edge    if direct edge i → j exists
INF     otherwise
```

## 7.2 Transition

When we allow vertex `k`, shortest `i → j` path either:

```text
does not use k
```

or:

```text
uses k
```

So:

```text
dp[k][i][j]
=
min(
    dp[k-1][i][j],
    dp[k-1][i][k] + dp[k-1][k][j]
)
```

We compress the DP and keep only one `dist[][]` matrix.

---

# 8. Deriving the Transition

Compare:

```text
current:
i ------------------> j

through k:
i ------> k --------> j
```

Candidate:

```text
candidateThroughK
=
dist[i][k] + dist[k][j]
```

Then:

```text
dist[i][j]
=
min(dist[i][j], candidateThroughK)
```

C++:

```cpp
dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j]);
```

Safer version:

```cpp
if (dist[i][k] != INF && dist[k][j] != INF) {
    dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j]);
}
```

---

# 9. Why the Loop Order Must Be k-i-j

Canonical Floyd–Warshall:

```cpp
for (int k = 1; k <= n; k++) {
    for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= n; j++) {
            ...
        }
    }
}
```

The important part is:

```text
k must be outside
```

because `k` represents the DP stage.

```text
Stage k = 0
No intermediate vertices

Stage k = 1
May use node 1

Stage k = 2
May use nodes 1,2

...

Stage k = N
May use every node
```

After finishing stage `k`:

```text
all shortest paths may use vertices 1...k
as intermediates
```

---

# 10. Step-by-Step Algorithm

Given:

```text
N vertices
M weighted edges
```

1. Create `dist[N][N]`.
2. Set `dist[i][i] = 0`.
3. Set every other cell to `INF`.
4. Insert direct edges.
5. For every intermediate node `k = 1...N`:
6. For every start node `i = 1...N`:
7. For every destination `j = 1...N`:
8. Try route `i → k → j`.
9. Update:

```text
dist[i][j]
=
min(
    dist[i][j],
    dist[i][k] + dist[k][j]
)
```

After all `k`:

```text
dist[i][j]
```

is the shortest distance for every ordered pair.

---

# 11. Detailed Matrix Dry Run

Use this directed graph:

```text
1 --3--> 2
1 --10-> 3
2 --1--> 3
3 --2--> 1
```

Initial matrix:

```text
      1    2    3

1     0    3   10
2    INF   0    1
3     2   INF   0
```

## k = 1

Allow node `1` as an intermediate.

Check:

```text
3 → 2
```

Through `1`:

```text
3 → 1 → 2
= 2 + 3
= 5
```

Update:

```text
dist[3][2]: INF → 5
```

Matrix:

```text
      1    2    3

1     0    3   10
2    INF   0    1
3     2    5    0
```

## k = 2

Allow nodes `{1,2}`.

Check:

```text
1 → 3
```

Through `2`:

```text
1 → 2 → 3
= 3 + 1
= 4
```

Update:

```text
dist[1][3]: 10 → 4
```

Matrix:

```text
      1    2    3

1     0    3    4
2    INF   0    1
3     2    5    0
```

## k = 3

Allow nodes `{1,2,3}`.

Check:

```text
2 → 1
```

Through `3`:

```text
2 → 3 → 1
= 1 + 2
= 3
```

Update:

```text
dist[2][1]: INF → 3
```

Final matrix:

```text
      1    2    3

1     0    3    4
2     3    0    1
3     2    5    0
```

Compact dry run:

```text
Initial:
[0,   3, 10]
[INF, 0,  1]
[2, INF,  0]

k=1:
3→2 via 1 = 2+3 = 5
dist[3][2]: INF → 5

k=2:
1→3 via 2 = 3+1 = 4
dist[1][3]: 10 → 4

k=3:
2→1 via 3 = 1+2 = 3
dist[2][1]: INF → 3

Final:
[0, 3, 4]
[3, 0, 1]
[2, 5, 0]
```

---

# 12. Clean C++17 — Floyd–Warshall

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll INF = 4e18;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<vector<ll>> dist(n + 1, vector<ll>(n + 1, INF));

    for (int i = 1; i <= n; i++) {
        dist[i][i] = 0;
    }

    for (int i = 0; i < m; i++) {
        int u, v;
        ll weight;
        cin >> u >> v >> weight;

        dist[u][v] = min(dist[u][v], weight);
    }

    for (int k = 1; k <= n; k++) {
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                if (dist[i][k] == INF || dist[k][j] == INF) {
                    continue;
                }

                dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j]);
            }
        }
    }

    return 0;
}
```

---

# 13. Multiple Edges Between Same Pair

Suppose:

```text
1 → 2 = 8
1 → 2 = 3
```

Correct initialization:

```cpp
dist[u][v] = min(dist[u][v], weight);
```

So:

```text
dist[1][2] = 3
```

before Floyd–Warshall starts.

---

# 14. Directed vs Undirected Graph

Directed edge:

```text
u → v = w
```

store:

```cpp
dist[u][v] = min(dist[u][v], w);
```

Undirected edge:

```text
u --w-- v
```

store both:

```cpp
dist[u][v] = min(dist[u][v], w);
dist[v][u] = min(dist[v][u], w);
```

---

# 15. Negative Edges

Floyd–Warshall can handle negative edges.

Example:

```text
1 → 2 = 5
2 → 3 = -4
1 → 3 = 10
```

Then:

```text
1 → 2 → 3
= 5 + (-4)
= 1
```

So:

```text
dist[1][3] = 1
```

Negative edge is okay.

```text
negative edge ≠ negative cycle
```

---

# 16. Negative-Cycle Detection

After Floyd–Warshall finishes, inspect the diagonal.

Normally:

```text
dist[i][i] = 0
```

If:

```text
dist[i][i] < 0
```

then there is a negative cycle reachable from `i` and returning to `i`.

C++:

```cpp
bool hasNegativeCycle = false;

for (int i = 1; i <= n; i++) {
    if (dist[i][i] < 0) {
        hasNegativeCycle = true;
    }
}
```

Why?

Because:

```text
i → ... → i
```

has negative total cost.

---

# 17. Path Reconstruction

The lecture screenshots also show a parent/path matrix idea.

Distance answers:

```text
What is the shortest cost?
```

Path reconstruction answers:

```text
Which vertices form that shortest path?
```

A convenient implementation uses:

```text
nextNode[i][j]
```

meaning:

```text
first node to move to when going from i toward j
```

Initialization for direct edge:

```cpp
nextNode[u][v] = v;
```

If route through `k` improves:

```text
i → k → j
```

then:

```cpp
nextNode[i][j] = nextNode[i][k];
```

Build a path:

```cpp
vector<int> buildPath(int source, int target, const vector<vector<int>>& nextNode) {
    if (nextNode[source][target] == -1) {
        return {};
    }

    vector<int> path;
    int current = source;

    while (current != target) {
        path.push_back(current);
        current = nextNode[current][target];
    }

    path.push_back(target);

    return path;
}
```

---

# 18. Graph Diameter from Floyd–Warshall

The lecture mentions:

```text
Diameter = longest shortest path
```

Important:

```text
For each pair, first compute its shortest path.
Then take the maximum among those finite shortest distances.
```

So:

```text
diameter = max(dist[i][j])
```

over reachable pairs.

Example matrix:

```text
0  3  4
3  0  1
2  5  0
```

Maximum finite value:

```text
5
```

Therefore:

```text
diameter = 5
```

C++:

```cpp
ll diameter = 0;

for (int i = 1; i <= n; i++) {
    for (int j = 1; j <= n; j++) {
        if (dist[i][j] != INF) {
            diameter = max(diameter, dist[i][j]);
        }
    }
}
```

For disconnected graphs, check the exact problem definition.

---

# 19. Complexity

Loops:

```text
for k
    for i
        for j
```

Each runs `V` times.

Therefore:

```text
Time = O(V³)
```

Distance matrix:

```text
V × V
```

So:

```text
Space = O(V²)
```

Path reconstruction matrix also remains:

```text
O(V²)
```

---

# 20. Floyd–Warshall vs Repeated Dijkstra vs Bellman–Ford

| Situation | Good standard choice |
|---|---|
| One source, unit edges | BFS |
| One source, weights `{0,1}` | 0-1 BFS |
| One source, non-negative weights | Dijkstra |
| One source, negative edges | Bellman–Ford |
| All pairs, moderate `V` | Floyd–Warshall |
| All pairs, sparse large graph, non-negative weights | Repeated Dijkstra may be better |

Dense graph intuition:

```text
E ≈ V²
```

Repeated Dijkstra:

```text
O(V × E log V)
≈ O(V³ log V)
```

Floyd–Warshall:

```text
O(V³)
```

Sparse graph:

```text
E << V²
```

Repeated Dijkstra may be preferable.

---

# 21. Boolean / Reachability Variant

Useful extension:

Instead of shortest distance, ask:

```text
Can i reach j?
```

Use:

```text
reachable[i][j]
```

Transition:

```text
reachable[i][j]
=
reachable[i][j]
OR
(
    reachable[i][k]
    AND
    reachable[k][j]
)
```

Same skeleton:

```text
for k
    for i
        for j
```

This computes transitive closure.

---

# 22. Common Mistakes

| Mistake | Correct idea |
|---|---|
| Forget `dist[i][i] = 0` | Diagonal starts at zero |
| Overwrite duplicate edges | Use `min(existing, weight)` |
| Put `i` or `j` outside `k` | `k` must be outermost |
| Add `INF + value` blindly | Guard unreachable pieces |
| Use `int` for large path sums | Prefer `long long` |
| Assume negative edges are forbidden | Floyd handles them |
| Ignore negative cycles | Check `dist[i][i] < 0` when relevant |
| Confuse APSP with SSSP | APSP answers every pair |
| Use Floyd automatically for huge sparse graphs | Compare with repeated Dijkstra |

---

# 23. Recognition Model

When reading a problem, ask:

### 1. Do I need shortest paths for many/all pairs?

If yes:

```text
think APSP
```

### 2. Is `N` moderate?

Floyd usually appears when:

```text
O(N³)
```

is feasible.

### 3. Is the graph dense?

If yes:

```text
Floyd–Warshall becomes attractive
```

### 4. Are there many distance queries after one fixed graph?

Example:

```text
distance(a,b)
distance(x,y)
distance(p,q)
...
```

Then:

```text
precompute once in O(V³)
answer each query in O(1)
```

### 5. Does the final task use all pair distances?

Examples:

```text
diameter
best meeting point
pairwise reachability
eccentricity
```

Floyd–Warshall is a strong signal.

---

# 24. Final Mental Model

Floyd–Warshall repeatedly asks:

```text
Can intermediate node k improve the path from i to j?
```

Compare:

```text
current:
i ------------------> j

through k:
i ------> k --------> j
```

Transition:

```text
dist[i][j]
=
min(
    dist[i][j],
    dist[i][k] + dist[k][j]
)
```

Loop order:

```text
for k
    for i
        for j
```

because:

```text
k = DP stage
```

After processing `k`:

```text
vertices 1...k are allowed as intermediate nodes
```

After all `k`:

```text
dist[i][j]
=
shortest distance between every pair
```

Complete pattern:

```text
Initialize matrix
      ↓
dist[i][i] = 0
      ↓
insert direct edges
      ↓
for k
  for i
    for j
      try i → k → j
      ↓
APSP matrix
```

Fast decision tree:

```text
One source?
    |
    +-- all weights 1 ------> BFS
    +-- weights 0/1 --------> 0-1 BFS
    +-- non-negative -------> Dijkstra
    +-- negative allowed ---> Bellman–Ford

All pairs?
    |
    +-- moderate V ---------> Floyd–Warshall
```

The most important idea is not memorizing three loops.

Remember the DP meaning:

```text
At stage k,
should shortest path i → j
stay unchanged,
or become cheaper by going through k?
```

Once this is clear, Floyd–Warshall is easy to reconstruct in a contest.
