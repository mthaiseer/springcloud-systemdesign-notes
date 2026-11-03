# Graph Traversals & Bipartite Graphs — TLE-Style Self-Study Notes

> **Goal:** Understand graph traversal from first principles, not by memorizing code.
>
> Teaching order:
>
> `Graph basics → visited array → recursion/stack → queue → DFS → BFS → connected components → shortest path in unweighted graph → grids → multi-source BFS → bipartite graphs → CF application → bonus binary lifting`

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [The One Mental Model for Graph Traversal](#2-the-one-mental-model-for-graph-traversal)
3. [Depth First Search — DFS](#3-depth-first-search--dfs)
4. [Breadth First Search — BFS](#4-breadth-first-search--bfs)
5. [DFS vs BFS](#5-dfs-vs-bfs)
6. [Application 1 — Number of Connected Components](#6-application-1--number-of-connected-components)
7. [Application 2 — Size of Every Connected Component](#7-application-2--size-of-every-connected-component)
8. [Application 3 — Shortest Distance in an Unweighted Graph](#8-application-3--shortest-distance-in-an-unweighted-graph)
9. [Grids Are Graphs](#9-grids-are-graphs)
10. [Multi-Source BFS — Rotten Oranges](#10-multi-source-bfs--rotten-oranges)
11. [Bipartite Graphs](#11-bipartite-graphs)
12. [Why Odd Cycles Break Bipartiteness](#12-why-odd-cycles-break-bipartiteness)
13. [Tree Is Always Bipartite](#13-tree-is-always-bipartite)
14. [Codeforces 862B — Mahmoud and Ehab](#14-codeforces-862b--mahmoud-and-ehab)
15. [Bonus — Binary Lifting / K-th Ancestor](#15-bonus--binary-lifting--k-th-ancestor)
16. [Complexity Summary](#16-complexity-summary)
17. [Recognition Checklist](#17-recognition-checklist)
18. [Final Mental Model](#18-final-mental-model)

---

# 1. Prerequisites

You should understand these ideas before DFS/BFS.

---

## 1.1 Graph = Nodes + Edges

A graph is simply:

- **Node / vertex** = an object
- **Edge** = a connection between two objects

Example:

```text
        1
       / \
      /   \
     2-----3
     |
     |
     4
```

Nodes:

```text
{1, 2, 3, 4}
```

Edges:

```text
(1,2), (1,3), (2,3), (2,4)
```

Think of:

```text
people  → nodes
friendships → edges

cities → nodes
roads  → edges
```

---

## 1.2 Adjacency List

For traversal, the most common representation is an **adjacency list**.

For the graph:

```text
        1
       / \
      2---3
      |
      4
```

we store:

```text
1 → 2, 3
2 → 1, 3, 4
3 → 1, 2
4 → 2
```

C++:

```cpp
vector<vector<int>> adj(n + 1);

adj[1].push_back(2);
adj[2].push_back(1);
```

For an undirected edge `(u, v)`:

```cpp
adj[u].push_back(v);
adj[v].push_back(u);
```

---

## 1.3 Why We Need `visited[]`

Graphs can contain cycles.

Example:

```text
1 ----- 2
|       |
|       |
4 ----- 3
```

Suppose we start from `1`.

Without a visited array:

```text
1 → 2 → 3 → 4 → 1 → 2 → 3 → ...
```

Infinite loop.

So we store:

```text
visited[node] = true
```

meaning:

> "I already discovered this node. Do not process it as a new node again."

Typical declaration:

```cpp
vector<int> visited(n + 1, 0);
```

---

## 1.4 Recursion

DFS is easiest to understand using recursion.

Small recursion:

```cpp
void go(int x) {
    cout << x << '\n';

    if (x == 0) {
        return;
    }

    go(x - 1);
}
```

For:

```text
go(3)
```

call flow:

```text
go(3)
  ↓
go(2)
  ↓
go(1)
  ↓
go(0)
```

Then functions return upward:

```text
go(0) finishes
↑
go(1) finishes
↑
go(2) finishes
↑
go(3) finishes
```

DFS behaves similarly: go to a neighbour, then completely explore that neighbour before returning.

---

## 1.5 Stack vs Queue

### Stack — LIFO

Last In, First Out.

```text
push 1
push 2
push 3

TOP
 ↓
[3]
[2]
[1]
```

The next removed item is `3`.

DFS naturally behaves like a **stack**.

---

### Queue — FIFO

First In, First Out.

```text
front                   back
  ↓                       ↓
[1] [2] [3] [4]
```

The next removed item is `1`.

BFS uses a **queue**.

---

## 1.6 Connected Component

A connected component is a group of nodes where every node can reach every other node inside that group.

```text
Component 1           Component 2          Component 3

  1---2                   5---6                 8
  |                       |
  3---4                   7
```

Number of connected components:

```text
3
```

---

## 1.7 Distance in an Unweighted Graph

In an **unweighted graph**, every edge costs the same:

```text
one edge = one step
```

Example:

```text
S --- A --- B --- T
 \             /
  ----- C -----
```

Possible paths:

```text
S → A → B → T = 3 edges
S → C → T     = 2 edges
```

Shortest distance:

```text
2
```

This is why BFS is powerful: it visits nodes in increasing number of edges from the source.

---

# 2. The One Mental Model for Graph Traversal

Every traversal has the same skeleton:

```text
1. Start from a node.
2. Mark it visited.
3. Look at its neighbours.
4. For every unvisited neighbour:
       visit it.
5. Continue until nothing remains.
```

Only the **order** changes.

```text
DFS → go deep first
BFS → go level by level
```

Visual:

```text
               1
            /  |  \
           2   3   4
          / \      |
         5   6     7
```

DFS may do:

```text
1 → 2 → 5 → 6 → 3 → 4 → 7
```

BFS may do:

```text
1 → 2 → 3 → 4 → 5 → 6 → 7
```

The exact order can change depending on adjacency-list order.

---

# 3. Depth First Search — DFS

## 3.1 What DFS Means

DFS means:

> Pick one route and keep going deeper until you cannot continue. Then come back and try another route.

Think of exploring rooms in a maze.

```text
START
  |
  v
  A
 / \
B   C
|
D
|
E
```

DFS may explore:

```text
A → B → D → E
```

before returning to visit `C`.

---

## 3.2 Better DFS Graph Drawing

Consider:

```text
           3
          / \
         2---4
        / \ / \
       0---1---5
```

Assume adjacency order makes DFS choose:

```text
3 → 2 → 4 → 5 → 1 → 0
```

DFS tree:

```text
3
└── 2
    └── 4
        └── 5
            └── 1
                └── 0
```

Notice:

The original graph has many edges, but DFS creates a **DFS traversal tree** from the edges it actually uses to discover new nodes.

---

## 3.3 DFS Algorithm

```text
dfs(node):

    mark node visited

    process node

    for each neighbour:
        if neighbour is not visited:
            dfs(neighbour)
```

---

## 3.4 DFS C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

void dfs(int node, const vector<vector<int>>& adj, vector<int>& visited) {
    visited[node] = 1;

    cout << node << ' ';

    for (int neighbour : adj[node]) {
        if (!visited[neighbour]) {
            dfs(neighbour, adj, visited);
        }
    }
}
```

---

## 3.5 Detailed DFS Dry Run

Graph:

```text
        1
       / \
      2   3
     / \
    4   5
```

Adjacency list:

```text
1 → 2, 3
2 → 1, 4, 5
3 → 1
4 → 2
5 → 2
```

Start:

```text
dfs(1)
```

Compact call flow:

```text
dfs(1): visit 1 → choose 2
dfs(2): visit 2 → 1 already visited → choose 4
dfs(4): visit 4 → 2 already visited → return
dfs(2): next choose 5
dfs(5): visit 5 → 2 already visited → return
dfs(2): finished → return
dfs(1): next choose 3
dfs(3): visit 3 → 1 already visited → return
dfs(1): finished
```

Traversal:

```text
1 → 2 → 4 → 5 → 3
```

Visited changes:

```text
after 1: [1]
after 2: [1,2]
after 4: [1,2,4]
after 5: [1,2,4,5]
after 3: [1,2,3,4,5]
```

---

## 3.6 Why DFS Traversal Is Not Unique

Suppose:

```text
adj[1] = {2, 3}
```

then DFS may begin:

```text
1 → 2 → ...
```

But if:

```text
adj[1] = {3, 2}
```

DFS may begin:

```text
1 → 3 → ...
```

Both are valid.

Traversal order depends on neighbour order.

---

## 3.7 DFS Complexity

Using adjacency list:

```text
Every node is visited once.
Every edge is inspected a constant number of times.
```

Therefore:

```text
Time  = O(V + E)
Space = O(V)
```

Space includes:

- `visited[]`
- recursion stack

---

# 4. Breadth First Search — BFS

## 4.1 What BFS Means

BFS means:

> Visit everything one step away first, then everything two steps away, then everything three steps away.

Graph:

```text
               S
            /  |  \
           A   B   C
          / \      |
         D   E     F
```

Levels from `S`:

```text
Level 0: S

Level 1: A B C

Level 2: D E F
```

This level structure is the key reason BFS finds shortest paths in an unweighted graph.

---

## 4.2 BFS Uses a Queue

Start:

```text
queue = [S]
```

Process `S`.

Its neighbours enter the queue:

```text
queue = [A, B, C]
```

Then:

```text
A is processed before B and C's children.
```

So an entire level naturally finishes before the next level.

---

## 4.3 BFS Algorithm

```text
1. Push source into queue.
2. Mark source visited immediately.
3. While queue is not empty:
       a. pop front node
       b. process it
       c. inspect all neighbours
       d. if a neighbour is unvisited:
              mark visited
              push it into queue
```

---

## 4.4 Why Mark Visited When Pushing?

This is very important.

Bad idea:

```text
mark visited only when node is popped
```

Suppose:

```text
    A
   / \
  B---C
   \ /
    D
```

Both `B` and `C` may discover `D`.

If `D` is not marked when first pushed:

```text
B pushes D
C also pushes D
```

Queue:

```text
[..., D, D]
```

In a dense graph this can create many unnecessary duplicate entries.

Correct:

```cpp
visited[neighbour] = 1;
q.push(neighbour);
```

Mark at the moment the node is **discovered**.

---

## 4.5 BFS C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

void bfs(int source, const vector<vector<int>>& adj) {
    int n = adj.size() - 1;

    vector<int> visited(n + 1, 0);
    queue<int> q;

    visited[source] = 1;
    q.push(source);

    while (!q.empty()) {
        int node = q.front();
        q.pop();

        cout << node << ' ';

        for (int neighbour : adj[node]) {
            if (!visited[neighbour]) {
                visited[neighbour] = 1;
                q.push(neighbour);
            }
        }
    }
}
```

---

## 4.6 Detailed BFS Dry Run

Graph:

```text
               1
            /  |  \
           2   3   4
          / \
         5   6
```

Start:

```text
visited[1] = true
queue = [1]
```

Compact state transitions:

```text
Pop 1 → push 2,3,4 → queue = [2,3,4]
Pop 2 → push 5,6   → queue = [3,4,5,6]
Pop 3 → no new     → queue = [4,5,6]
Pop 4 → no new     → queue = [5,6]
Pop 5 → no new     → queue = [6]
Pop 6 → no new     → queue = []
```

Traversal:

```text
1 → 2 → 3 → 4 → 5 → 6
```

Levels:

```text
Level 0: 1
Level 1: 2 3 4
Level 2: 5 6
```

---

## 4.7 BFS Complexity

```text
Time  = O(V + E)
Space = O(V)
```

---

# 5. DFS vs BFS

| Question | DFS | BFS |
|---|---|---|
| Main idea | Go deep | Go level-by-level |
| Main tool | Recursion / stack | Queue |
| Unweighted shortest path | Not naturally | Yes |
| Connected components | Yes | Yes |
| Grid traversal | Yes | Yes |
| Cycle detection | Yes | Yes |
| Bipartite check | Yes | Yes |
| Tree subtree work | Very common | Sometimes |
| Level/order distance | Less natural | Very natural |

Remember:

```text
"Can I reach it?"               → DFS or BFS
"How many components?"          → DFS or BFS
"Minimum edges from source?"    → BFS
"Go through subtree/branch?"    → DFS is natural
```

---

# 6. Application 1 — Number of Connected Components

## 6.1 Problem

Given an undirected graph, count its connected components.

Example:

```text
Component 1        Component 2       Component 3

1 --- 2            5 --- 6              8
|     |                /
3 --- 4               7
```

Answer:

```text
3
```

---

## 6.2 Core Observation

One DFS starting from an unvisited node visits exactly **one entire connected component**.

Therefore:

```text
for every node:
    if node is unvisited:
        we discovered a NEW component
        answer++
        dfs(node)
```

---

## 6.3 Dry Run

Initially:

```text
visited = {}
components = 0
```

Process nodes:

```text
node 1 unvisited → components=1 → dfs(1) visits {1,2,3,4}
node 2 visited   → skip
node 3 visited   → skip
node 4 visited   → skip

node 5 unvisited → components=2 → dfs(5) visits {5,6,7}
node 6 visited   → skip
node 7 visited   → skip

node 8 unvisited → components=3 → dfs(8)
```

Answer:

```text
3
```

---

## 6.4 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

void dfs(int node, const vector<vector<int>>& adj, vector<int>& visited) {
    visited[node] = 1;

    for (int neighbour : adj[node]) {
        if (!visited[neighbour]) {
            dfs(neighbour, adj, visited);
        }
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<vector<int>> adj(n + 1);

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;

        adj[u].push_back(v);
        adj[v].push_back(u);
    }

    vector<int> visited(n + 1, 0);

    int components = 0;

    for (int node = 1; node <= n; node++) {
        if (!visited[node]) {
            components++;
            dfs(node, adj, visited);
        }
    }

    cout << components << '\n';

    return 0;
}
```

Complexity:

```text
O(V + E)
```

---

# 7. Application 2 — Size of Every Connected Component

Same idea, but count how many nodes DFS visits.

---

## 7.1 Modify DFS

```cpp
void dfs(int node, const vector<vector<int>>& adj, vector<int>& visited, int& size) {
    visited[node] = 1;
    size++;

    for (int neighbour : adj[node]) {
        if (!visited[neighbour]) {
            dfs(neighbour, adj, visited, size);
        }
    }
}
```

---

## 7.2 Main Loop

```cpp
for (int node = 1; node <= n; node++) {
    if (!visited[node]) {
        int componentSize = 0;

        dfs(node, adj, visited, componentSize);

        cout << componentSize << ' ';
    }
}
```

Example:

```text
Component A: 1 2 3 4  → size 4
Component B: 5 6      → size 2
Component C: 7        → size 1
```

Output:

```text
4 2 1
```

---

# 8. Application 3 — Shortest Distance in an Unweighted Graph

## 8.1 Problem

Given source `S`, find minimum number of edges from `S` to every node.

Example:

```text
          A
         / \
        S   C --- D
         \ /
          B
```

Distances from `S`:

```text
S = 0
A = 1
B = 1
C = 2
D = 3
```

---

## 8.2 Why BFS Works

BFS discovers:

```text
distance 0 nodes
then distance 1 nodes
then distance 2 nodes
then distance 3 nodes
...
```

The first time we discover a node, we have found its minimum-edge path.

---

## 8.3 Distance Formula

If BFS moves:

```text
node → neighbour
```

then:

```text
dist[neighbour] = dist[node] + 1
```

---

## 8.4 C++17

```cpp
vector<int> bfsDistances(int source, const vector<vector<int>>& adj) {
    int n = adj.size() - 1;

    vector<int> dist(n + 1, -1);
    queue<int> q;

    dist[source] = 0;
    q.push(source);

    while (!q.empty()) {
        int node = q.front();
        q.pop();

        for (int neighbour : adj[node]) {
            if (dist[neighbour] == -1) {
                dist[neighbour] = dist[node] + 1;
                q.push(neighbour);
            }
        }
    }

    return dist;
}
```

Using `dist[node] == -1` also acts like an unvisited check.

---

## 8.5 Why Ordinary BFS Does Not Solve Weighted Shortest Path

Suppose:

```text
      100
S -------- A
 \        /
  \1    1/
   \    /
     B
```

Path by number of edges:

```text
S → A = 1 edge
```

but cost:

```text
100
```

Alternative:

```text
S → B → A = 2 edges
```

cost:

```text
1 + 1 = 2
```

BFS minimizes:

```text
number of edges
```

not arbitrary edge weights.

For non-negative weighted shortest path, think:

```text
Dijkstra
```

---

# 9. Grids Are Graphs

This is one of the most important modelling ideas.

A grid cell is a graph node.

Example:

```text
(0,0) --- (0,1) --- (0,2)
  |         |         |
(1,0) --- (1,1) --- (1,2)
  |         |         |
(2,0) --- (2,1) --- (2,2)
```

For cell:

```text
(x, y)
```

4-direction neighbours are:

```text
UP:    (x - 1, y)
DOWN:  (x + 1, y)
LEFT:  (x, y - 1)
RIGHT: (x, y + 1)
```

Visual:

```text
              (x-1,y)
                 |
                 |
(x,y-1) ---- (x,y) ---- (x,y+1)
                 |
                 |
              (x+1,y)
```

---

## 9.1 Direction Arrays

Instead of writing four cases manually:

```cpp
int dx[] = {-1, 1, 0, 0};
int dy[] = {0, 0, -1, 1};
```

Then:

```cpp
for (int dir = 0; dir < 4; dir++) {
    int newX = x + dx[dir];
    int newY = y + dy[dir];
}
```

Dry run for `(x, y) = (2, 3)`:

```text
dir=0 → (1,3)
dir=1 → (3,3)
dir=2 → (2,2)
dir=3 → (2,4)
```

---

## 9.2 Boundary Check

A valid cell satisfies:

```text
0 <= x < rows
0 <= y < cols
```

C++:

```cpp
bool inside(int x, int y, int rows, int cols) {
    return x >= 0 && x < rows && y >= 0 && y < cols;
}
```

---

## 9.3 Grid Traversal Recognition

When you see:

- island
- maze
- cells
- neighbouring squares
- infection/spreading
- minimum steps
- connected region

think:

```text
"This grid is secretly a graph."
```

Then choose:

```text
DFS → region / connectivity
BFS → minimum steps / spreading time
```

---

# 10. Multi-Source BFS — Rotten Oranges

## 10.1 Problem Model

Grid contains:

```text
0 = empty
1 = fresh orange
2 = rotten orange
```

Each second, every rotten orange makes adjacent fresh oranges rotten.

We need minimum time until all possible oranges become rotten.

---

## 10.2 Why Normal "One Source BFS" Is Not Enough

There may be many rotten oranges initially.

Example:

```text
2 1 1 0 2
1 1 1 1 1
0 1 1 1 0
```

Both rotten oranges start spreading at time `0`.

Running BFS separately from each source would repeat work.

Instead:

> Put **all starting rotten oranges into the queue before BFS begins**.

That is **multi-source BFS**.

---

## 10.3 Think of an Artificial Super-Source

Visual idea:

```text
               SUPER SOURCE
              /     |      \
             0      0       0
            /       |        \
        rotten A rotten B rotten C
```

All starting sources are distance `0`.

Then ordinary BFS handles everything.

You do **not** need to actually create the super-source node in code.

---

## 10.4 Level-by-Level Infection

Example:

```text
Time 0

2 1 1
1 1 0
0 1 2
```

All initial `2`s enter the queue.

After one BFS layer:

```text
Time 1

2 2 1
2 1 0
0 2 2
```

After another layer:

```text
Time 2

2 2 2
2 2 0
0 2 2
```

Answer:

```text
2 seconds
```

---

## 10.5 Why Brute Force Can TLE

Bad approach:

```text
for every rotten orange:
    run a BFS
```

If the grid has `R × C` cells:

```text
one BFS = O(R*C)
number of sources can also be O(R*C)
```

Worst case:

```text
O((R*C)^2)
```

Multi-source BFS visits each cell only a constant number of times:

```text
O(R*C)
```

---

## 10.6 Step-by-Step Algorithm

```text
1. Scan the entire grid.
2. Push every initially rotten orange into the queue.
3. Count fresh oranges.
4. If fresh == 0:
       answer = 0.
5. Process BFS one complete level at a time.
6. Whenever a fresh neighbour becomes rotten:
       fresh--
       push it.
7. Each completed spreading layer = one second.
8. At the end:
       if fresh > 0 → impossible
       otherwise return elapsed time.
```

---

## 10.7 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int orangesRotting(vector<vector<int>>& grid) {
    int rows = grid.size();
    int cols = grid[0].size();

    queue<pair<int, int>> q;
    int fresh = 0;

    for (int row = 0; row < rows; row++) {
        for (int col = 0; col < cols; col++) {
            if (grid[row][col] == 2) {
                q.push({row, col});
            } else if (grid[row][col] == 1) {
                fresh++;
            }
        }
    }

    if (fresh == 0) {
        return 0;
    }

    int dx[] = {-1, 1, 0, 0};
    int dy[] = {0, 0, -1, 1};

    int minutes = 0;

    while (!q.empty() && fresh > 0) {
        int levelSize = q.size();
        minutes++;

        while (levelSize--) {
            auto [x, y] = q.front();
            q.pop();

            for (int dir = 0; dir < 4; dir++) {
                int newX = x + dx[dir];
                int newY = y + dy[dir];

                if (newX < 0 || newX >= rows || newY < 0 || newY >= cols) {
                    continue;
                }

                if (grid[newX][newY] != 1) {
                    continue;
                }

                grid[newX][newY] = 2;
                fresh--;

                q.push({newX, newY});
            }
        }
    }

    return fresh == 0 ? minutes : -1;
}
```

---

# 11. Bipartite Graphs

## 11.1 Child-Simple Meaning

Imagine every node must wear one of two shirt colours:

```text
BLUE or RED
```

Rule:

> Every edge must connect different colours.

Valid:

```text
BLUE ----- RED
  |         |
  |         |
 RED ----- BLUE
```

Invalid:

```text
BLUE ----- BLUE
```

because an edge connects the same colour.

---

## 11.2 Formal Meaning

A graph is bipartite if its vertices can be split into two sets:

```text
Set A
Set B
```

such that every edge goes:

```text
A ↔ B
```

and never:

```text
A ↔ A
B ↔ B
```

---

## 11.3 Better Bipartite Drawing

```text
LEFT SET                     RIGHT SET

   1 ● ---------------------- ● 4
     |\                      /|
     | \                    / |
     |  \                  /  |
   2 ● ---------------------- ● 5
     \                      /
      \                    /
   3 ● ------------------ ● 6
```

Possible coloring:

```text
LEFT  = BLUE
RIGHT = RED
```

No edge exists inside one side.

---

## 11.4 Two-Coloring Algorithm

Use:

```text
color[node] = -1 → uncolored
color[node] = 0  → BLUE
color[node] = 1  → RED
```

If current node has color:

```text
0
```

then an unvisited neighbour must receive:

```text
1
```

Formula:

```cpp
color[neighbour] = 1 - color[node];
```

---

## 11.5 DFS Bipartite Check

```cpp
bool dfsColor(int node, int currentColor,
              const vector<vector<int>>& adj,
              vector<int>& color) {
    color[node] = currentColor;

    for (int neighbour : adj[node]) {
        if (color[neighbour] == -1) {
            if (!dfsColor(neighbour, 1 - currentColor, adj, color)) {
                return false;
            }
        } else if (color[neighbour] == color[node]) {
            return false;
        }
    }

    return true;
}
```

---

## 11.6 Full Graph Can Be Disconnected

Do not only run DFS from node `1`.

Example:

```text
1 --- 2          3 --- 4
                  \   /
                    5
```

There are two components.

So:

```cpp
bool isBipartite(const vector<vector<int>>& adj) {
    int n = adj.size() - 1;

    vector<int> color(n + 1, -1);

    for (int node = 1; node <= n; node++) {
        if (color[node] == -1) {
            if (!dfsColor(node, 0, adj, color)) {
                return false;
            }
        }
    }

    return true;
}
```

---

## 11.7 Bipartite Dry Run

Graph:

```text
1 ----- 2
|       |
|       |
4 ----- 3
```

Start:

```text
color[1] = BLUE
```

Then:

```text
1 BLUE → 2 RED
1 BLUE → 4 RED
2 RED  → 3 BLUE
```

Check edge:

```text
3 BLUE ↔ 4 RED
```

valid.

Final:

```text
BLUE = {1,3}
RED  = {2,4}
```

Graph is bipartite.

---

# 12. Why Odd Cycles Break Bipartiteness

## 12.1 Even Cycle Works

Cycle length 4:

```text
1 ----- 2
|       |
|       |
4 ----- 3
```

Color:

```text
1 BLUE
2 RED
3 BLUE
4 RED
```

The final edge:

```text
4 RED → 1 BLUE
```

works.

---

## 12.2 Odd Cycle Fails

Triangle:

```text
      1
     / \
    2---3
```

Start:

```text
1 = BLUE
```

Then:

```text
2 = RED
3 = RED
```

But edge:

```text
2 ----- 3
```

connects:

```text
RED ----- RED
```

Conflict.

Therefore:

```text
odd cycle → not bipartite
```

And the important equivalence for undirected graphs is:

```text
Graph is bipartite
        ⇔
Graph has no odd-length cycle
```

---

# 13. Tree Is Always Bipartite

A tree:

```text
- is connected
- has no cycle
```

Therefore it has no odd cycle.

So every tree is bipartite.

---

## 13.1 Tree Coloring by Depth

Tree:

```text
            1
          /   \
         2     3
        / \     \
       4   5     6
```

Color by depth parity:

```text
Depth 0: 1       → BLUE
Depth 1: 2,3     → RED
Depth 2: 4,5,6   → BLUE
```

Every tree edge connects:

```text
even depth ↔ odd depth
```

So different colours are automatic.

---

# 14. Codeforces 862B — Mahmoud and Ehab

Problem:

```text
https://codeforces.com/problemset/problem/862/B
```

---

## 14.1 What Is Asked?

You are given a tree.

Add as many new edges as possible while keeping the graph:

```text
simple + bipartite
```

Return maximum number of edges that can be added.

---

## 14.2 Remove the Story

Because the input is a tree:

```text
the graph is already bipartite
```

Run DFS and split nodes into:

```text
color 0 set → size = X
color 1 set → size = Y
```

Any valid bipartite edge must go between these sets.

Maximum possible edges in a complete bipartite graph:

```text
X * Y
```

---

## 14.3 But Some Edges Already Exist

A tree with `N` nodes has exactly:

```text
N - 1
```

edges.

Therefore:

```text
answer = X * Y - (N - 1)
```

---

## 14.4 Visual Proof

Suppose:

```text
BLUE SET             RED SET

  A ● ---------------- ● D
  B ● ---------------- ● E
  C ● ---------------- ● F
```

If:

```text
X = 3
Y = 3
```

maximum cross-set pairs:

```text
3 * 3 = 9
```

Suppose tree already has:

```text
N - 1 = 5
```

edges.

Then new edges:

```text
9 - 5 = 4
```

---

## 14.5 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

void dfs(int node, int parent, int currentColor,
         const vector<vector<int>>& adj,
         ll colorCount[2]) {
    colorCount[currentColor]++;

    for (int neighbour : adj[node]) {
        if (neighbour == parent) {
            continue;
        }

        dfs(neighbour, node, 1 - currentColor, adj, colorCount);
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<vector<int>> adj(n + 1);

    for (int i = 0; i < n - 1; i++) {
        int u, v;
        cin >> u >> v;

        adj[u].push_back(v);
        adj[v].push_back(u);
    }

    ll colorCount[2] = {0, 0};

    dfs(1, 0, 0, adj, colorCount);

    ll allPossibleBipartiteEdges = colorCount[0] * colorCount[1];
    ll existingTreeEdges = n - 1;

    cout << allPossibleBipartiteEdges - existingTreeEdges << '\n';

    return 0;
}
```

Complexity:

```text
O(N)
```

---

# 15. Bonus — Binary Lifting / K-th Ancestor

The final part of the lecture introduces an important tree-query idea.

---

## 15.1 Problem

Given a rooted tree and queries:

```text
(node, k)
```

find the node's:

```text
k-th ancestor
```

Example:

```text
          1
          |
          2
          |
          3
          |
          4
          |
          5
```

For node `5`:

```text
1st ancestor = 4
2nd ancestor = 3
3rd ancestor = 2
4th ancestor = 1
```

A naive query climbs one parent at a time:

```text
O(k)
```

If both `N` and number of queries are large, this can be too slow.

---

## 15.2 Key Idea — Write k as Powers of Two

Example:

```text
k = 13
```

Binary decomposition:

```text
13 = 8 + 4 + 1
   = 2^3 + 2^2 + 2^0
```

So if we can jump:

```text
1 ancestor
2 ancestors
4 ancestors
8 ancestors
16 ancestors
...
```

we can construct any `k`.

---

## 15.3 Precompute Power-of-Two Ancestors

Define:

```text
up[node][j]
```

as:

```text
the 2^j-th ancestor of node
```

Examples:

```text
up[x][0] = 1st ancestor
up[x][1] = 2nd ancestor
up[x][2] = 4th ancestor
up[x][3] = 8th ancestor
```

---

## 15.4 Recurrence

To find the `2^j`-th ancestor:

1. jump `2^(j-1)` ancestors
2. from there, jump another `2^(j-1)`

Therefore:

```text
up[node][j]
=
up[ up[node][j-1] ][j-1]
```

Visual:

```text
node
  |
  | 2^(j-1)
  v
middle
  |
  | 2^(j-1)
  v
answer

total jump = 2^j
```

---

## 15.5 Example

Chain:

```text
1
|
2
|
3
|
4
|
5
|
6
|
7
|
8
```

For node `8`:

```text
up[8][0] = 7   // 1 step
up[8][1] = 6   // 2 steps
up[8][2] = 4   // 4 steps
```

Find 5th ancestor:

```text
5 = 4 + 1
```

So:

```text
8 --4 steps--> 4
4 --1 step ---> 3
```

Answer:

```text
3
```

---

## 15.6 Binary Lifting Skeleton

```cpp
const int LOG = 20;

vector<vector<int>> up;

void build(int n, const vector<int>& parent) {
    up.assign(n + 1, vector<int>(LOG, 0));

    for (int node = 1; node <= n; node++) {
        up[node][0] = parent[node];
    }

    for (int j = 1; j < LOG; j++) {
        for (int node = 1; node <= n; node++) {
            int middle = up[node][j - 1];

            if (middle != 0) {
                up[node][j] = up[middle][j - 1];
            }
        }
    }
}
```

Query:

```cpp
int kthAncestor(int node, int k) {
    for (int bit = 0; bit < LOG; bit++) {
        if (k & (1 << bit)) {
            node = up[node][bit];

            if (node == 0) {
                return -1;
            }
        }
    }

    return node;
}
```

Complexity:

```text
Preprocessing = O(N log N)
Each query    = O(log N)
```

---

# 16. Complexity Summary

| Technique | Time | Extra Space |
|---|---:|---:|
| DFS traversal | `O(V + E)` | `O(V)` |
| BFS traversal | `O(V + E)` | `O(V)` |
| Count connected components | `O(V + E)` | `O(V)` |
| Component sizes | `O(V + E)` | `O(V)` |
| Unweighted shortest path | `O(V + E)` | `O(V)` |
| Grid BFS/DFS | `O(R*C)` | `O(R*C)` |
| Multi-source grid BFS | `O(R*C)` | `O(R*C)` |
| Bipartite check | `O(V + E)` | `O(V)` |
| CF 862B tree coloring | `O(N)` | `O(N)` |
| Binary lifting build | `O(N log N)` | `O(N log N)` |
| K-th ancestor query | `O(log N)` | `O(1)` |

---

# 17. Recognition Checklist

When reading a new problem, ask these questions.

### Question 1 — Is this secretly a graph?

Look for:

```text
objects + connections
cities + roads
people + friendships
cells + neighbours
states + transitions
```

---

### Question 2 — Do I only need reachability?

Think:

```text
DFS / BFS
```

---

### Question 3 — Do I need number or size of groups?

Think:

```text
Connected Components
```

Pattern:

```text
for every unvisited node:
    answer++
    traverse whole component
```

---

### Question 4 — Minimum number of moves in an unweighted graph?

Think:

```text
BFS
```

---

### Question 5 — Many sources start at the same time?

Think:

```text
Multi-Source BFS
```

Examples:

```text
infection
fire spreading
nearest hospital
nearest zero
rotten oranges
```

---

### Question 6 — Grid?

Immediately translate:

```text
cell = node
neighbour movement = edge
```

Then use:

```cpp
dx[]
dy[]
```

---

### Question 7 — Can nodes be split into two opposite groups?

Think:

```text
Bipartite / 2-coloring
```

Typical clues:

```text
enemy teams
two groups
alternate colors
no same-group edge
odd cycle
```

---

### Question 8 — Tree + ancestor queries?

Think:

```text
Binary Lifting
```

---

# 18. Final Mental Model

Do not memorize DFS and BFS as two code templates.

Remember the picture:

```text
                    GRAPH TRAVERSAL
                          |
             +------------+------------+
             |                         |
            DFS                       BFS
             |                         |
       go deep first             level by level
             |                         |
     recursion / stack                queue
             |                         |
     components, tree            shortest path
      exploration                unweighted graph
             |                         |
             +------------+------------+
                          |
                      GRIDS TOO
                          |
          cell = node, movement = edge
                          |
             +------------+------------+
             |                         |
       single source              many sources
             |                         |
            BFS                  multi-source BFS

                          |
                     2-COLORING
                          |
                     BIPARTITE
                          |
                 no odd-length cycle
```

The most important habit is:

```text
Story
  ↓
What are the nodes?
  ↓
What are the edges?
  ↓
What does "distance / group / color / reach" mean?
  ↓
Choose DFS / BFS / multi-source BFS / bipartite coloring
```

That modelling step is more important than memorizing any traversal code.
