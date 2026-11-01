# Shortest Path Algorithms Overview — BFS, Implicit Graphs, 0-1 BFS, Dijkstra

> **Self-study competitive programming note** based on the supplied lecture screenshots.  
> Goal: understand **which shortest-path algorithm to choose**, why it works, and how to model hidden/implicit graphs such as grids and knight moves.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [What Is a Shortest Path?](#2-what-is-a-shortest-path)
3. [SSSP vs APSP](#3-sssp-vs-apsp)
4. [Weighted vs Unweighted Graph](#4-weighted-vs-unweighted-graph)
5. [BFS for Unweighted Shortest Path](#5-bfs-for-unweighted-shortest-path)
6. [Why BFS Gives Shortest Distance](#6-why-bfs-gives-shortest-distance)
7. [BFS Dry Run](#7-bfs-dry-run)
8. [Clean C++ — BFS Shortest Path](#8-clean-c--bfs-shortest-path)
9. [Relaxation — The Common Shortest-Path Operation](#9-relaxation--the-common-shortest-path-operation)
10. [Implicit Graphs](#10-implicit-graphs)
11. [Grid as an Implicit Graph](#11-grid-as-an-implicit-graph)
12. [Knight Moves as an Implicit Graph](#12-knight-moves-as-an-implicit-graph)
13. [C++ — BFS on a Grid / Knight Graph](#13-c--bfs-on-a-grid--knight-graph)
14. [Why Ordinary BFS Fails for Different Edge Weights](#14-why-ordinary-bfs-fails-for-different-edge-weights)
15. [0-1 BFS](#15-0-1-bfs)
16. [Why a Deque Works in 0-1 BFS](#16-why-a-deque-works-in-01-bfs)
17. [0-1 BFS Dry Run](#17-01-bfs-dry-run)
18. [Clean C++ — 0-1 BFS](#18-clean-c--01-bfs)
19. [Dijkstra](#19-dijkstra)
20. [Why Dijkstra Uses a Priority Queue](#20-why-dijkstra-uses-a-priority-queue)
21. [Dijkstra Dry Run](#21-dijkstra-dry-run)
22. [Clean C++ — Dijkstra](#22-clean-c--dijkstra)
23. [When Dijkstra Must Not Be Used](#23-when-dijkstra-must-not-be-used)
24. [Shortest Path Algorithm Decision Table](#24-shortest-path-algorithm-decision-table)
25. [Recognition Checklist](#25-recognition-checklist)
26. [Common Mistakes](#26-common-mistakes)
27. [Final Mental Model](#27-final-mental-model)

---

# 1. Prerequisites

Before learning shortest-path algorithms, you should be comfortable with:

```text
Graph
Node / Vertex
Edge
Directed graph
Undirected graph
Weighted edge
Unweighted edge
Adjacency list
Queue
Deque
Priority queue
BFS
```

You should also know:

```text
V = number of vertices
E = number of edges
```

For an undirected graph:

```text
sum of all degrees = 2E
```

So scanning all adjacency lists costs:

```text
O(V + E)
```

---

# 2. What Is a Shortest Path?

A **path** is a sequence of vertices connected by edges.

Example:

```text
A → B → C → D
```

If every edge has weight `1`, the path cost is simply the number of edges.

If the graph is weighted:

```text
A --3--> B --5--> C
```

then the path cost is:

```text
3 + 5 = 8
```

The shortest path is the path with minimum total cost.

---

# 3. SSSP vs APSP

## SSSP

```text
Single Source Shortest Path
```

One source:

```text
S
```

Find:

```text
shortest distance from S to every reachable node
```

Typical algorithms:

```text
BFS
0-1 BFS
Dijkstra
Bellman-Ford
```

## APSP

```text
All Pairs Shortest Path
```

Find:

```text
dist[u][v]
```

for every pair of nodes.

Typical algorithm:

```text
Floyd-Warshall
```

---

# 4. Weighted vs Unweighted Graph

This is the first algorithm-selection question.

## Unweighted / equal-weight graph

Every move costs the same.

Think:

```text
each edge costs 1
```

Use:

```text
BFS
```

## Weighted graph

Different edges may have different costs.

Then:

```text
fewest edges
```

and:

```text
minimum total weight
```

may be different.

So plain BFS may fail.

---

# 5. BFS for Unweighted Shortest Path

For equal edge weights:

```text
all edges = 1
```

BFS gives shortest distances.

Core idea:

```text
dist[source] = 0
push source into queue
```

Then repeatedly:

```text
pop front

for every neighbor:
    if we found a better distance:
        update distance
        push neighbor
```

---

# 6. Why BFS Gives Shortest Distance

BFS explores level by level:

```text
distance 0
    ↓
distance 1
    ↓
distance 2
    ↓
distance 3
```

Example:

```text
             S
          /  |  \
         A   B   C
        / \       \
       D   E       F
```

Distances:

```text
dist[S] = 0

dist[A] = 1
dist[B] = 1
dist[C] = 1

dist[D] = 2
dist[E] = 2
dist[F] = 2
```

That queue ordering is why BFS works for unweighted SSSP.

---

# 7. BFS Dry Run

Graph:

```text
1 -- 2 -- 4
|    |
3 -- 5
```

Source:

```text
1
```

Initial:

```text
dist[1] = 0
all others = INF

queue = [1]
```

Compact dry run:

```text
Process 1 → discover 2,3
dist[2]=1, dist[3]=1
queue=[2,3]

Process 2 → discover 4,5
dist[4]=2, dist[5]=2
queue=[3,4,5]

Process 3 → 5 already has distance 2
queue=[4,5]

Process 4 → no new node
Process 5 → no new node
```

Final:

```text
node : 1 2 3 4 5
dist : 0 1 1 2 2
```

---

# 8. Clean C++ — BFS Shortest Path

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

    int source;
    cin >> source;

    const int INF = 1e9;

    vector<int> dist(n + 1, INF);
    queue<int> q;

    dist[source] = 0;
    q.push(source);

    while (!q.empty()) {
        int node = q.front();
        q.pop();

        for (int next : graph[node]) {
            if (dist[next] > dist[node] + 1) {
                dist[next] = dist[node] + 1;
                q.push(next);
            }
        }
    }

    return 0;
}
```

Complexity:

```text
Time  = O(V + E)
Space = O(V + E)
```

---

# 9. Relaxation — The Common Shortest-Path Operation

Suppose:

```text
u ----w----> v
```

Current distances:

```text
dist[u]
dist[v]
```

Going through `u` gives:

```text
candidate = dist[u] + w
```

If:

```text
dist[v] > candidate
```

update:

```text
dist[v] = candidate
```

Generic formula:

```text
dist[v] = min(dist[v], dist[u] + w)
```

This is called:

```text
RELAXATION
```

It appears in:

```text
BFS
0-1 BFS
Dijkstra
Bellman-Ford
Floyd-Warshall
```

The main difference is **which node/edge gets processed next**.

---

# 10. Implicit Graphs

A graph is not always explicitly given as:

```text
nodes + edge list
```

Sometimes the problem gives:

```text
grid
board
number
string
coordinates
game state
```

and expects you to discover the graph.

Think:

```text
state = node
legal move = edge
```

This is an:

```text
implicit graph
```

---

# 11. Grid as an Implicit Graph

A grid cell:

```text
(row, col)
```

can be one node.

For 4-direction movement:

```text
dr = {-1, 1, 0, 0}
dc = {0, 0, -1, 1}
```

From `(r,c)`:

```text
(r-1,c)
(r+1,c)
(r,c-1)
(r,c+1)
```

become candidate neighbors.

Then check:

```text
inside grid?
not blocked?
```

If yes:

```text
valid graph edge
```

For an `N × M` grid:

```text
V = N × M
```

and each cell has only constant-degree neighbors.

Therefore BFS is:

```text
O(N × M)
```

---

# 12. Knight Moves as an Implicit Graph

The lecture shows chess-style movement.

A knight has 8 possible offsets:

```text
(+1,+2)
(+2,+1)
(+2,-1)
(+1,-2)
(-1,-2)
(-2,-1)
(-2,+1)
(-1,+2)
```

Arrays:

```cpp
int dr[] = {1, 2, 2, 1, -1, -2, -2, -1};
int dc[] = {2, 1, -1, -2, -2, -1, 1, 2};
```

Each board cell is a state.

Each legal knight move costs:

```text
1
```

So:

```text
minimum knight moves = BFS
```

---

# 13. C++ — BFS on a Grid / Knight Graph

```cpp
#include <bits/stdc++.h>
using namespace std;

using State = pair<int, int>;

int n, m;

bool valid(int row, int col) {
    return row >= 0 && row < n && col >= 0 && col < m;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n >> m;

    int startRow, startCol;
    cin >> startRow >> startCol;

    const int INF = 1e9;

    vector<vector<int>> dist(n, vector<int>(m, INF));

    int dr[] = {1, 2, 2, 1, -1, -2, -2, -1};
    int dc[] = {2, 1, -1, -2, -2, -1, 1, 2};

    queue<State> q;

    dist[startRow][startCol] = 0;
    q.push({startRow, startCol});

    while (!q.empty()) {
        auto [row, col] = q.front();
        q.pop();

        for (int dir = 0; dir < 8; dir++) {
            int nr = row + dr[dir];
            int nc = col + dc[dir];

            if (!valid(nr, nc)) {
                continue;
            }

            if (dist[nr][nc] > dist[row][col] + 1) {
                dist[nr][nc] = dist[row][col] + 1;
                q.push({nr, nc});
            }
        }
    }

    return 0;
}
```

---

# 14. Why Ordinary BFS Fails for Different Edge Weights

Consider:

```text
S --10--> A
S --1---> B --1--> A
```

BFS by edge count prefers:

```text
S → A
```

because it uses one edge.

But weighted cost:

```text
10
```

The two-edge route costs:

```text
1 + 1 = 2
```

So:

```text
shortest weighted path = 2
```

Plain BFS is incorrect here.

---

# 15. 0-1 BFS

Special weighted case:

```text
every edge weight is 0 or 1
```

Use:

```text
deque
```

Complexity:

```text
O(V + E)
```

Core rule after relaxation:

```text
weight 0 → push_front(next)
weight 1 → push_back(next)
```

Why?

A `0` edge keeps the same distance.

A `1` edge increases distance by one.

---

# 16. Why a Deque Works in 0-1 BFS

Suppose:

```text
dist[u] = D
```

Then:

```text
0-edge → candidate distance D
1-edge → candidate distance D+1
```

The `0` edge must be processed earlier.

So:

```text
0 → front
1 → back
```

A deque gives exactly those two priority levels.

---

# 17. 0-1 BFS Dry Run

Graph:

```text
S --0--> A
S --1--> B
A --1--> C
B --0--> C
```

Initial:

```text
dist[S] = 0
deque = [S]
```

Process `S`:

```text
S→A weight 0
dist[A]=0
push_front(A)

S→B weight 1
dist[B]=1
push_back(B)
```

Deque:

```text
[A,B]
```

Process `A`:

```text
A→C weight 1
dist[C]=1
push_back(C)
```

Deque:

```text
[B,C]
```

Process `B`:

```text
B→C weight 0

candidate = 1
current dist[C] = 1
```

No improvement.

Final:

```text
dist[S]=0
dist[A]=0
dist[B]=1
dist[C]=1
```

---

# 18. Clean C++ — 0-1 BFS

```cpp
#include <bits/stdc++.h>
using namespace std;

using Edge = pair<int, int>;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<vector<Edge>> graph(n + 1);

    for (int i = 0; i < m; i++) {
        int u, v, weight;
        cin >> u >> v >> weight;

        graph[u].push_back({v, weight});
        graph[v].push_back({u, weight});
    }

    int source;
    cin >> source;

    const int INF = 1e9;

    vector<int> dist(n + 1, INF);
    deque<int> dq;

    dist[source] = 0;
    dq.push_front(source);

    while (!dq.empty()) {
        int node = dq.front();
        dq.pop_front();

        for (auto [next, weight] : graph[node]) {
            if (dist[next] > dist[node] + weight) {
                dist[next] = dist[node] + weight;

                if (weight == 0) {
                    dq.push_front(next);
                } else {
                    dq.push_back(next);
                }
            }
        }
    }

    return 0;
}
```

---

# 19. Dijkstra

Use Dijkstra when:

```text
all edge weights are non-negative
```

Allowed:

```text
0
1
2
5
100
```

Not allowed:

```text
negative weight
```

Core idea:

```text
always process the node with smallest known distance
```

So Dijkstra uses:

```text
min-priority queue
```

---

# 20. Why Dijkstra Uses a Priority Queue

Suppose candidate distances are:

```text
A = 8
B = 3
C = 11
D = 5
```

We should process:

```text
B
```

first.

A normal queue cannot guarantee that.

A min-heap can.

Typical complexity:

```text
O((V + E) log V)
```

Often shortened to:

```text
O(E log V)
```

for sparse connected graphs.

---

# 21. Dijkstra Dry Run

Graph:

```text
S --3--> A
S --12-> B
A --5--> B
A --7--> C
B --2--> C
```

Initial:

```text
dist[S]=0
others=INF

pq={(0,S)}
```

Process `S`:

```text
S→A:
dist[A]=3

S→B:
dist[B]=12
```

Queue:

```text
(3,A), (12,B)
```

Process `A`:

```text
A→B:
candidate = 3+5 = 8
dist[B]=8

A→C:
candidate = 3+7 = 10
dist[C]=10
```

Queue contains:

```text
(8,B), (10,C), (12,B)
```

`(12,B)` is now stale.

Process `B` with distance `8`:

```text
B→C:
candidate = 8+2 = 10
```

No improvement.

Final:

```text
S = 0
A = 3
B = 8
C = 10
```

---

# 22. Clean C++ — Dijkstra

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

    int source;
    cin >> source;

    const ll INF = (1LL << 60);

    vector<ll> dist(n + 1, INF);

    priority_queue<State, vector<State>, greater<State>> pq;

    dist[source] = 0;
    pq.push({0, source});

    while (!pq.empty()) {
        auto [currentDist, node] = pq.top();
        pq.pop();

        if (currentDist != dist[node]) {
            continue;
        }

        for (auto [next, weight] : graph[node]) {
            if (dist[next] > dist[node] + weight) {
                dist[next] = dist[node] + weight;
                pq.push({dist[next], next});
            }
        }
    }

    return 0;
}
```

---

# 23. When Dijkstra Must Not Be Used

Dijkstra assumes:

```text
edge weights >= 0
```

Negative edges can later reduce a distance that looked finalized.

Example:

```text
S --5--> A
S --10-> B
B --(-20)--> A
```

Initially:

```text
dist[A]=5
dist[B]=10
```

But through `B`:

```text
10 + (-20) = -10
```

So `A` becomes smaller later.

Therefore:

```text
negative edge → do not use Dijkstra
```

Think:

```text
Bellman-Ford
```

If there is a reachable negative cycle:

```text
shortest distance may not exist
```

because cost can decrease forever.

---

# 24. Shortest Path Algorithm Decision Table

| Edge weights / problem | Algorithm | Complexity |
|---|---|---:|
| all equal / unweighted | BFS | `O(V+E)` |
| only `0` or `1` | 0-1 BFS | `O(V+E)` |
| all non-negative | Dijkstra | `O((V+E) log V)` |
| negative edges possible | Bellman-Ford | `O(VE)` |
| all-pairs, moderate `V` | Floyd-Warshall | `O(V^3)` |

---

# 25. Recognition Checklist

When you see a shortest-path problem, ask:

```text
1. What is the state/node?

2. What is one transition/edge?

3. What is the cost of that transition?

4. Are all costs equal?

5. Are costs only 0 or 1?

6. Are all costs non-negative?

7. Are negative edges possible?

8. Is it one source or many sources?

9. Is the graph explicit or implicit?

10. What are V and E?
```

Then choose the algorithm.

---

# 26. Common Mistakes

## Mistake 1 — Choosing BFS just because it is a graph

Wrong question:

```text
"Is this a graph?"
```

Better question:

```text
"What are the edge weights?"
```

## Mistake 2 — Building every edge for an implicit graph

For grids and boards:

```text
generate neighbors when needed
```

## Mistake 3 — Using only `visited` when relaxation is required

For weighted shortest-path algorithms, use:

```text
if (newDist < dist[next])
```

## Mistake 4 — Forgetting stale Dijkstra entries

Use:

```cpp
if (currentDist != dist[node]) {
    continue;
}
```

## Mistake 5 — Dijkstra with negative edges

Do not use it.

## Mistake 6 — Confusing fewest edges with minimum weight

Example:

```text
one edge costing 100
```

versus:

```text
three edges costing 1 each
```

Fewest edges:

```text
100-cost route
```

Minimum weighted cost:

```text
3-cost route
```

Different objectives.

---

# 27. Final Mental Model

Think in three layers.

## Layer 1 — Model

```text
What is a state?

What is an edge?
```

Example:

```text
grid cell = state
legal move = edge
```

## Layer 2 — Cost

```text
What does one transition cost?
```

Then:

```text
all 1       → BFS
0/1         → 0-1 BFS
non-negative→ Dijkstra
negative    → Bellman-Ford
```

## Layer 3 — Relaxation

Universal idea:

```text
candidate = dist[current] + edgeWeight
```

If:

```text
candidate < dist[next]
```

then:

```text
dist[next] = candidate
```

---

The contest decision tree:

```text
Shortest Path
    |
    +-- What is the STATE?
    |
    +-- What are the EDGES?
    |
    +-- What are the WEIGHTS?
            |
            +-- all 1       → BFS
            +-- 0/1         → 0-1 BFS
            +-- >= 0        → Dijkstra
            +-- negative    → Bellman-Ford
```

For hidden graph problems:

```text
state = node
legal operation = edge
cost of operation = edge weight
```

Once those three are correct, selecting the shortest-path algorithm becomes much easier.
