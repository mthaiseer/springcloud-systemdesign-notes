# BFS-Based Shortest Path Ideas — Graph Formulation, State BFS, Multi-Source BFS

> **Self-study CP note** based on the supplied AlgoZenith lecture screenshots.  
> Main lecture ideas preserved:
>
> 1. **Number of shortest paths**
> 2. **State formulation**
> 3. **Multi-source BFS**
> 4. **Algorithm change**
>
> Main grid state:
>
> ```text
> (row, col, wallsDestroyed)
> ```
>
> The goal is to understand **how to model the graph first**, then choose BFS/0-1 BFS/Dijkstra/etc.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Why BFS Solves Unweighted SSSP](#2-why-bfs-solves-unweighted-sssp)
3. [Relaxation](#3-relaxation)
4. [Lecture Roadmap](#4-lecture-roadmap)
5. [Idea 1 — Number of Shortest Paths](#5-idea-1--number-of-shortest-paths)
6. [Dry Run — Counting Shortest Paths](#6-dry-run--counting-shortest-paths)
7. [C++ — Distance + Number of Shortest Paths](#7-c--distance--number-of-shortest-paths)
8. [Parent Array — Reconstruct One Shortest Path](#8-parent-array--reconstruct-one-shortest-path)
9. [Idea 2 — State Formulation](#9-idea-2--state-formulation)
10. [Why `(row,col)` Can Be Incomplete](#10-why-rowcol-can-be-incomplete)
11. [Grid Problem — Destroy Up to K Walls](#11-grid-problem--destroy-up-to-k-walls)
12. [Correct State Definition](#12-correct-state-definition)
13. [Transitions](#13-transitions)
14. [Detailed Dry Run](#14-detailed-dry-run)
15. [C++ — BFS with State](#15-c--bfs-with-state)
16. [Alternative State — Walls Remaining](#16-alternative-state--walls-remaining)
17. [Idea 3 — Multi-Source BFS](#17-idea-3--multi-source-bfs)
18. [Multi-Source BFS Dry Run](#18-multi-source-bfs-dry-run)
19. [C++ — Multi-Source BFS](#19-c--multi-source-bfs)
20. [Idea 4 — When the Algorithm Changes](#20-idea-4--when-the-algorithm-changes)
21. [BFS / 0-1 BFS / Dijkstra / Bellman-Ford](#21-bfs--01-bfs--dijkstra--bellman-ford)
22. [Grid Modelling Checklist](#22-grid-modelling-checklist)
23. [Common Mistakes](#23-common-mistakes)
24. [Recognition Patterns](#24-recognition-patterns)
25. [Final Mental Model](#25-final-mental-model)

---

# 1. Prerequisites

You should already know:

```text
Graph = nodes + edges
BFS
queue
distance array
grid traversal
4-direction movement
shortest path in an unweighted graph
```

Also remember:

```text
SSSP = Single Source Shortest Path
```

Meaning:

```text
one source
→ shortest distance to every reachable state
```

---

## 1.1 Grid as a graph

A cell:

```text
(i,j)
```

can be a node.

For 4-direction movement:

```text
up    = (-1, 0)
down  = (+1, 0)
left  = (0, -1)
right = (0, +1)
```

One legal move becomes one graph edge.

---

# 2. Why BFS Solves Unweighted SSSP

BFS processes states level by level:

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
      / | \
     A  B  C
     |     |
     D ----E
```

Distances:

```text
dist[S] = 0

dist[A] = 1
dist[B] = 1
dist[C] = 1

dist[D] = 2
dist[E] = 2
```

So when every edge costs exactly `1`, the first discovery gives the shortest distance.

Complexity:

```text
O(V + E)
```

---

# 3. Relaxation

The lecture also connects BFS to the common shortest-path idea of **relaxation**.

Suppose:

```text
X ----w----> Y
```

Current distances:

```text
dist[X] = d1
dist[Y] = d2
```

Going through `X` gives:

```text
d1 + w
```

So:

```text
dist[Y] = min(dist[Y], dist[X] + w)
```

This is relaxation.

For BFS:

```text
w = 1
```

therefore:

```text
candidate = dist[X] + 1
```

The same idea later appears in:

```text
0-1 BFS
Dijkstra
Bellman-Ford
Floyd-Warshall
```

---

# 4. Lecture Roadmap

The screenshots organize BFS shortest-path modelling into four useful upgrades:

```text
BFS / SSSP
    |
    +-- 1. Number of shortest paths
    |
    +-- 2. State formulation
    |
    +-- 3. Multi-source BFS
    |
    +-- 4. Algorithm change
```

This is a strong contest framework.

When you see a shortest-path problem, ask:

```text
What exactly is the state?
Do I need only distance?
Are there multiple starting points?
Are all edges really weight 1?
```

---

# 5. Idea 1 — Number of Shortest Paths

Normal BFS stores:

```text
dist[v]
```

If the problem asks:

```text
How many different shortest paths reach v?
```

also store:

```text
ways[v]
```

where:

```text
ways[v] = number of shortest paths from source to v
```

Initialize:

```text
dist[source] = 0
ways[source] = 1
```

---

## Case 1 — First time reaching `v`

If:

```text
dist[v] == INF
```

then:

```text
dist[v] = dist[u] + 1
ways[v] = ways[u]
```

Reason:

```text
Every shortest path reaching u
can be extended through u → v.
```

---

## Case 2 — Another equally short route reaches `v`

If:

```text
dist[v] == dist[u] + 1
```

then:

```text
ways[v] += ways[u]
```

because another shortest route has been found.

---

## Case 3 — Longer route

If:

```text
dist[v] < dist[u] + 1
```

ignore it.

It is not shortest.

---

# 6. Dry Run — Counting Shortest Paths

Graph:

```text
       A
      / \
     B   C
      \ /
       D
```

Source:

```text
A
```

Two shortest routes reach `D`:

```text
A → B → D
A → C → D
```

Initial:

```text
dist[A] = 0
ways[A] = 1
queue = [A]
```

Process `A`:

```text
B: dist=1, ways=1
C: dist=1, ways=1
queue = [B,C]
```

Process `B`:

```text
D discovered first time

dist[D] = 2
ways[D] = 1
```

Process `C`:

```text
candidate distance to D = dist[C] + 1
                        = 1 + 1
                        = 2
```

Current:

```text
dist[D] = 2
```

Same shortest distance, so:

```text
ways[D] += ways[C]
ways[D] = 1 + 1 = 2
```

Final:

```text
dist[D] = 2
ways[D] = 2
```

---

# 7. C++ — Distance + Number of Shortest Paths

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

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
    vector<ll> ways(n + 1, 0);
    queue<int> q;

    dist[source] = 0;
    ways[source] = 1;
    q.push(source);

    while (!q.empty()) {
        int node = q.front();
        q.pop();

        for (int next : graph[node]) {
            if (dist[next] == INF) {
                dist[next] = dist[node] + 1;
                ways[next] = ways[node];
                q.push(next);
            } else if (dist[next] == dist[node] + 1) {
                ways[next] += ways[node];
            }
        }
    }

    return 0;
}
```

If the problem asks for modulo counting:

```cpp
ways[next] = (ways[next] + ways[node]) % MOD;
```

---

# 8. Parent Array — Reconstruct One Shortest Path

Distance tells:

```text
HOW SHORT?
```

Parent tells:

```text
WHICH PATH?
```

Suppose BFS discovers:

```text
S → A → C → F
```

Store:

```text
parent[A] = S
parent[C] = A
parent[F] = C
```

To reconstruct:

```text
F
↑
C
↑
A
↑
S
```

Reverse the result:

```text
S → A → C → F
```

For a state graph, the parent must also be a **state**, not merely a physical node.

---

# 9. Idea 2 — State Formulation

This is one of the most important modelling skills in CP.

A beginner often thinks:

```text
node = physical location
```

But the correct definition is:

> A state contains the minimum information needed to determine all future legal moves.

Examples:

```text
(node)
(row, col)
(row, col, wallsDestroyed)
(city, fuelRemaining)
(node, parity)
(node, couponUsed)
(mask, currentNode)
```

---

# 10. Why `(row,col)` Can Be Incomplete

Suppose we may destroy at most `K` walls.

We reach the same cell `(i,j)` by two routes.

Route A:

```text
distance = 5
wallsDestroyed = 2
```

Route B:

```text
distance = 6
wallsDestroyed = 0
```

These states are different.

Route B is one step longer, but it has more wall-destruction capacity available for the future.

So this is insufficient:

```text
dist[i][j]
```

We need:

```text
dist[i][j][wallsDestroyed]
```

---

# 11. Grid Problem — Destroy Up to K Walls

The lecture uses a grid containing:

```text
S = start
F = finish
# = wall
. = free cell
```

Two related questions appear in the lecture:

```text
1. Find the minimum number of walls that must be destroyed to reach F.

2. Find the shortest path if at most K walls may be destroyed.
```

The second one demonstrates state formulation.

The optimization target is:

```text
minimum number of MOVES
```

subject to:

```text
wallsDestroyed <= K
```

Each movement still costs:

```text
1
```

So the shortest-path engine remains:

```text
BFS
```

but the state becomes larger.

---

# 12. Correct State Definition

Use:

```text
state = (row, col, wallsDestroyed)
```

Meaning:

```text
I am at (row,col)
and I have destroyed exactly wallsDestroyed walls.
```

Define:

```text
dist[row][col][wallsDestroyed]
```

as:

```text
minimum moves needed to reach (row,col)
after destroying exactly wallsDestroyed walls
```

For an `N × M` grid:

```text
row             = 0..N-1
col             = 0..M-1
wallsDestroyed  = 0..K
```

Maximum number of states:

```text
N × M × (K+1)
```

---

# 13. Transitions

Current state:

```text
(r, c, used)
```

Neighbor:

```text
(nr, nc)
```

## Case A — Free cell

If:

```text
grid[nr][nc] != '#'
```

then:

```text
(r,c,used)
    ↓
(nr,nc,used)
```

No extra wall is destroyed.

---

## Case B — Wall

If:

```text
grid[nr][nc] == '#'
```

then:

```text
newUsed = used + 1
```

Allowed only if:

```text
newUsed <= K
```

Transition:

```text
(r,c,used)
    ↓
(nr,nc,used+1)
```

---

## Compact formula

```text
newUsed = used + (grid[nr][nc] == '#')
```

Then check:

```text
newUsed <= K
```

---

# 14. Detailed Dry Run

Grid:

```text
S # .
. # F
```

Let:

```text
K = 1
```

Coordinates:

```text
S = (0,0)
F = (1,2)
```

Start:

```text
state = (0,0,0)
distance = 0
queue = [(0,0,0)]
```

### Iteration 1

From `(0,0,0)`:

Down to `(1,0)` is free:

```text
(1,0,0), dist=1
```

Right to `(0,1)` is a wall:

```text
newUsed = 0 + 1 = 1
```

Allowed:

```text
(0,1,1), dist=1
```

Queue:

```text
[(1,0,0), (0,1,1)]
```

### Iteration 2

Process `(1,0,0)`.

Right `(1,1)` is a wall:

```text
(1,1,1), dist=2
```

### Iteration 3

Process `(0,1,1)`.

Right `(0,2)` is free:

```text
(0,2,1), dist=2
```

Down `(1,1)` is a wall, but:

```text
used = 1
K = 1
```

Destroying another wall would give:

```text
2 > K
```

so that transition is forbidden.

### Continue

From `(0,2,1)` move down:

```text
(1,2,1), dist=3
```

This is finish.

Answer:

```text
3 moves
```

The important point:

```text
BFS is running on STATES,
not merely on cells.
```

---

# 15. C++ — BFS with State

```cpp
#include <bits/stdc++.h>
using namespace std;

struct State {
    int row;
    int col;
    int used;
};

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m, k;
    cin >> n >> m >> k;

    vector<string> grid(n);
    State start{-1, -1, 0};
    State finish{-1, -1, 0};

    for (int i = 0; i < n; i++) {
        cin >> grid[i];

        for (int j = 0; j < m; j++) {
            if (grid[i][j] == 'S') {
                start = {i, j, 0};
            }

            if (grid[i][j] == 'F') {
                finish = {i, j, 0};
            }
        }
    }

    const int INF = 1e9;

    vector<vector<vector<int>>> dist(
        n,
        vector<vector<int>>(m, vector<int>(k + 1, INF))
    );

    int dr[] = {-1, 1, 0, 0};
    int dc[] = {0, 0, -1, 1};

    queue<State> q;

    dist[start.row][start.col][0] = 0;
    q.push(start);

    while (!q.empty()) {
        State current = q.front();
        q.pop();

        for (int dir = 0; dir < 4; dir++) {
            int nr = current.row + dr[dir];
            int nc = current.col + dc[dir];

            if (nr < 0 || nr >= n || nc < 0 || nc >= m) {
                continue;
            }

            int newUsed = current.used;

            if (grid[nr][nc] == '#') {
                newUsed++;
            }

            if (newUsed > k) {
                continue;
            }

            if (dist[nr][nc][newUsed] != INF) {
                continue;
            }

            dist[nr][nc][newUsed] =
                dist[current.row][current.col][current.used] + 1;

            q.push({nr, nc, newUsed});
        }
    }

    int answer = INF;

    for (int used = 0; used <= k; used++) {
        answer = min(answer, dist[finish.row][finish.col][used]);
    }

    if (answer == INF) {
        cout << -1 << '\n';
    } else {
        cout << answer << '\n';
    }

    return 0;
}
```

Complexity:

```text
States = O(N × M × K)

Time  = O(N × M × K)
Space = O(N × M × K)
```

Each state has at most four outgoing transitions.

---

# 16. Alternative State — Walls Remaining

Instead of:

```text
(row, col, wallsDestroyed)
```

you can use:

```text
(row, col, wallsRemaining)
```

Start with:

```text
wallsRemaining = K
```

Entering a wall:

```text
wallsRemaining--
```

Both formulations are valid.

Choose the representation that makes the transition easiest to reason about.

---

# 17. Idea 3 — Multi-Source BFS

Sometimes there are many starting points.

Examples:

```text
nearest hospital
nearest zero
fire spreads from many cells
monsters start at several cells
nearest special node
```

Do not necessarily run BFS separately from every source.

Instead:

```text
push ALL sources into the queue initially
```

with:

```text
distance = 0
```

Then run one BFS.

Conceptually:

```text
          SUPER SOURCE
          /    |    \
        S1     S2    S3
```

All real sources start simultaneously.

---

# 18. Multi-Source BFS Dry Run

Line graph:

```text
0 -- 1 -- 2 -- 3 -- 4 -- 5
```

Sources:

```text
0 and 5
```

Initialize:

```text
dist[0] = 0
dist[5] = 0
queue = [0,5]
```

First wave:

```text
0 → 1
5 → 4
```

So:

```text
dist[1] = 1
dist[4] = 1
```

Second wave:

```text
1 → 2
4 → 3
```

So:

```text
dist[2] = 2
dist[3] = 2
```

Final:

```text
index : 0 1 2 3 4 5
dist  : 0 1 2 2 1 0
```

Each node gets its shortest distance from the nearest source.

---

# 19. C++ — Multi-Source BFS

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

    int sourceCount;
    cin >> sourceCount;

    vector<int> dist(n + 1, -1);
    queue<int> q;

    for (int i = 0; i < sourceCount; i++) {
        int source;
        cin >> source;

        dist[source] = 0;
        q.push(source);
    }

    while (!q.empty()) {
        int node = q.front();
        q.pop();

        for (int next : graph[node]) {
            if (dist[next] != -1) {
                continue;
            }

            dist[next] = dist[node] + 1;
            q.push(next);
        }
    }

    return 0;
}
```

---

# 20. Idea 4 — When the Algorithm Changes

Sometimes the state model is correct, but BFS is not the correct shortest-path engine.

Always ask:

```text
What are the edge weights?
```

This connects the lecture to the broader SSSP family.

---

# 21. BFS / 0-1 BFS / Dijkstra / Bellman-Ford

| Edge weights | Algorithm | Typical complexity |
|---|---|---|
| all equal / `1` | BFS | `O(V+E)` |
| only `0` or `1` | 0-1 BFS | `O(V+E)` |
| all non-negative | Dijkstra | `O((V+E) log V)` |
| negative edges possible | Bellman-Ford | `O(VE)` |
| all-pairs, moderate `V` | Floyd-Warshall | `O(V^3)` |

---

## Crucial wall-grid distinction

### Problem A

```text
Find the minimum number of walls destroyed.
```

Model movement cost as:

```text
free cell = 0
wall      = 1
```

Now edge weights are only:

```text
0 or 1
```

Natural algorithm:

```text
0-1 BFS
```

---

### Problem B

```text
Find the minimum number of MOVES
if at most K walls may be destroyed.
```

Every movement costs:

```text
1
```

But wall usage affects future feasibility.

So:

```text
state = (row,col,wallsDestroyed)
```

and use:

```text
ordinary BFS on the expanded graph
```

This is a very important modelling difference.

---

# 22. Grid Modelling Checklist

When you see a grid shortest-path problem, ask:

```text
1. What exactly is one state?
2. What are the legal moves?
3. Does every move cost the same?
4. Is there an extra resource/constraint?
5. Does reaching the same cell with different resource values matter?
6. One source or many sources?
7. Need only distance?
8. Need number of shortest paths?
9. Need path reconstruction?
10. What are N, M, K constraints?
```

---

# 23. Common Mistakes

## Mistake 1 — Using only `visited[row][col]`

Wrong when the actual state is:

```text
(row,col,used)
```

Correct state marking:

```text
visited[row][col][used]
```

or:

```text
dist[row][col][used] != INF
```

---

## Mistake 2 — Keeping only the minimum walls used per cell

This can be wrong if the optimization target is minimum moves.

Two paths can trade off:

```text
fewer moves + more walls used

vs

more moves + fewer walls used
```

Both may matter for the future.

---

## Mistake 3 — Running BFS once per source

For nearest-source problems, prefer:

```text
one multi-source BFS
```

when all sources should expand simultaneously.

---

## Mistake 4 — Counting every path

When counting shortest paths, add:

```text
ways[next] += ways[node]
```

only when:

```text
dist[next] == dist[node] + 1
```

---

## Mistake 5 — Assuming every grid shortest path means BFS

Check edge costs:

```text
all equal       → BFS
0/1             → 0-1 BFS
non-negative    → Dijkstra
negative        → Bellman-Ford style reasoning
```

---

# 24. Recognition Patterns

## Pattern A — Minimum moves

Phrases:

```text
minimum moves
fewest steps
minimum operations
shortest sequence
```

If every transition costs 1:

```text
BFS
```

---

## Pattern B — Count shortest ways

Phrases:

```text
number of shortest paths
number of minimum-length ways
ways to reach with minimum moves
```

Think:

```text
BFS + dist + ways
```

---

## Pattern C — Extra resource

Phrases:

```text
at most K walls
one coupon
limited fuel
keys collected
special move can be used K times
parity matters
```

Think:

```text
expanded state
```

Usually:

```text
(node, resource)
```

---

## Pattern D — Many starting points

Phrases:

```text
nearest hospital
nearest fire
closest special node
many monsters
many zeros
```

Think:

```text
multi-source BFS
```

---

## Pattern E — Movement cost changes

If a transition does not always cost 1:

```text
re-check the shortest-path algorithm
```

Do not force BFS.

---

# 25. Final Mental Model

The lecture can be compressed into this decision process:

```text
Shortest Path Problem
        |
        +-- What is the STATE?
        |
        +-- What is one TRANSITION?
        |
        +-- What is its COST?
        |
        +-- One source or MANY sources?
        |
        +-- Need only distance?
        |       |
        |       +-- count shortest paths?
        |       +-- reconstruct a path?
        |
        +-- Extra resource / condition?
                |
                └── expand the state
```

Then choose the engine:

```text
Unit edges         → BFS
0/1 edges          → 0-1 BFS
Non-negative       → Dijkstra
Negative edges     → Bellman-Ford
All-pairs          → Floyd-Warshall
```

For the wall-destruction grid:

```text
Physical location:
(row,col)

But future possibilities also depend on:
how many walls have already been destroyed
```

Therefore:

```text
state = (row,col,wallsDestroyed)
```

And because every move still costs one step:

```text
BFS over the expanded state graph
```

The deepest lesson is:

```text
BFS itself is usually easy.

The hard part is deciding:
"What exactly is a node/state?"
```

Once the state is correct, the algorithm often becomes straightforward.
