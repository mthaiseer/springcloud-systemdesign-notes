# DFS Tree & Cycle Detection — Directed + Undirected Graphs

> **Goal:** understand DFS tree formation, the 3-state DFS model, back edges, cycle detection in directed and undirected graphs, cycle reconstruction, marking/counting nodes that lie on cycles, and special cases such as self-loops and parallel edges.

```text
DFS + node state
     ↓
find an edge that returns to the active DFS path
     ↓
cycle
```

---

## Table of Contents

1. [What This Lecture Is About](#1-what-this-lecture-is-about)
2. [Prerequisite 1 — Directed vs Undirected Cycles](#2-prerequisite-1--directed-vs-undirected-cycles)
3. [Prerequisite 2 — DFS Tree](#3-prerequisite-2--dfs-tree)
4. [Prerequisite 3 — Three DFS States](#4-prerequisite-3--three-dfs-states)
5. [Prerequisite 4 — Parent Array](#5-prerequisite-4--parent-array)
6. [Directed Graph — Edge Meaning During DFS](#6-directed-graph--edge-meaning-during-dfs)
7. [Directed Cycle Detection](#7-directed-cycle-detection)
8. [Directed Cycle Dry Run](#8-directed-cycle-dry-run)
9. [Directed Cycle Detection C++17](#9-directed-cycle-detection-c17)
10. [Reconstruct / Print a Directed Cycle](#10-reconstruct--print-a-directed-cycle)
11. [DAG and Topological Ordering Connection](#11-dag-and-topological-ordering-connection)
12. [Undirected Graph — Why Parent Must Be Ignored](#12-undirected-graph--why-parent-must-be-ignored)
13. [Undirected Cycle Detection](#13-undirected-cycle-detection)
14. [Undirected Cycle Dry Run](#14-undirected-cycle-dry-run)
15. [Undirected Cycle Detection C++17](#15-undirected-cycle-detection-c17)
16. [Reconstruct / Print an Undirected Cycle](#16-reconstruct--print-an-undirected-cycle)
17. [Self-Loops](#17-self-loops)
18. [Parallel / Multiple Edges](#18-parallel--multiple-edges)
19. [Robust Undirected Input Handling](#19-robust-undirected-input-handling)
20. [Mark / Count Nodes That Belong to Cycles — `cntCycle[]`](#20-mark--count-nodes-that-belong-to-cycles--cntcycle)
21. [Complexity](#21-complexity)
22. [Common Mistakes](#22-common-mistakes)
23. [Recognition Checklist](#23-recognition-checklist)
24. [Final Mental Model](#24-final-mental-model)

---

# 1. What This Lecture Is About

The lecture separates cycle problems into:

```text
Graph Cycles
│
├── Directed Graph
└── Undirected Graph
```

The important distinction is:

```text
Directed:
edge to a node that is still ACTIVE in DFS
→ back edge
→ cycle

Undirected:
every edge appears in both directions,
so the edge back to the parent must be ignored.
```

The lecture also connects directed cycle detection to:

```text
DAG
Topological Ordering
Kahn's Algorithm
```

because:

```text
DAG = Directed Acyclic Graph
```

So:

```text
directed graph has a cycle
        ⇔
graph is not a DAG
```

---

# 2. Prerequisite 1 — Directed vs Undirected Cycles

## Directed Cycle

```text
1 → 2
↑   ↓
└── 3
```

Edges:

```text
1 → 2
2 → 3
3 → 1
```

Following edge direction:

```text
1 → 2 → 3 → 1
```

returns to the start, so this is a directed cycle.

---

## Undirected Cycle

```text
1 ----- 2
 \     /
   \ /
    3
```

Walk:

```text
1 → 2 → 3 → 1
```

returns to the start without reusing the same edge.

---

## Important Non-Cycle

```text
1 ----- 2
```

Adjacency list:

```text
1: [2]
2: [1]
```

The entries `1→2` and `2→1` represent the **same undirected edge**.

So this is not a cycle.

That is why parent handling is required for undirected DFS.

---

# 3. Prerequisite 2 — DFS Tree

Whenever DFS discovers a completely new node through an edge, that edge becomes part of the DFS tree.

Example:

```text
        1
        |
        2
      / | \
     3  5  8
     |  |  |
     4  6  9
        |  |
        7 10
```

A possible DFS tree:

```text
1
|
2
├── 3
│   └── 4
├── 5
│   └── 6
│       └── 7
└── 8
    └── 9
        └── 10
```

An edge used to discover a new node is a:

```text
tree edge
```

The cycle question becomes:

```text
Does some other edge connect us back
into the currently active DFS path?
```

---

# 4. Prerequisite 3 — Three DFS States

The lecture uses a 3-color / 3-state DFS model.

We use:

```text
1 = UNVISITED
2 = ACTIVE / currently inside DFS
3 = FINISHED / DFS fully completed
```

State transition:

```text
UNVISITED
   |
   | enter dfs(node)
   v
ACTIVE
   |
   | all neighbors processed
   v
FINISHED
```

or simply:

```text
1 → 2 → 3
```

---

## Why `visited[]` Alone Is Not Enough for Directed Graphs

Suppose node `v` was already seen.

There are two very different possibilities:

```text
A. v is still in the current recursion path
   → ACTIVE

B. v was completely processed earlier
   → FINISHED
```

Only case A proves a back edge to the current DFS path.

That is why directed cycle detection uses 3 states.

---

## Tiny Example

Current DFS path:

```text
1 → 2 → 5 → 6
```

All four are active:

```text
color[1] = 2
color[2] = 2
color[5] = 2
color[6] = 2
```

If `6` has an edge:

```text
6 → 2
```

then `2` is still active.

So:

```text
6 → 2
```

returns to an ancestor and closes a cycle.

---

# 5. Prerequisite 4 — Parent Array

When DFS discovers `v` from `u`:

```text
u → v
```

store:

```text
parent[v] = u
```

Example DFS path:

```text
1 → 2 → 5 → 6 → 7
```

Parent information:

```text
parent[2] = 1
parent[5] = 2
parent[6] = 5
parent[7] = 6
```

Why useful?

If DFS later finds:

```text
7 → 2
```

walk backward:

```text
7 → 6 → 5 → 2
```

and reconstruct:

```text
2 → 5 → 6 → 7 → 2
```

---

# 6. Directed Graph — Edge Meaning During DFS

Suppose DFS is currently at `u` and examines:

```text
u → v
```

Inspect `color[v]`.

---

## Case 1 — `color[v] == 1`

`v` is unvisited.

So:

```text
u → v
```

becomes a DFS tree edge.

Action:

```text
parent[v] = u
dfs(v)
```

Diagram:

```text
u
|
v   ← newly discovered
```

---

## Case 2 — `color[v] == 2`

`v` is still ACTIVE.

So `v` lies somewhere on the current recursion path.

```text
v
|
a
|
b
|
u
└────────→ v
```

This is the important case:

```text
BACK EDGE
   ↓
CYCLE EXISTS
```

---

## Case 3 — `color[v] == 3`

`v` is already completely processed.

For basic cycle detection:

```text
no current-path cycle is proved by this edge
```

So we do nothing.

---

# 7. Directed Cycle Detection

The entire observation is:

```text
During DFS,
if we find edge u → v
and color[v] == 2,
then v is already in the current DFS path.
```

So:

```text
v → ... → u → v
```

forms a cycle.

ASCII:

```text
       ┌───────────────┐
       ↓               |
      (v) → a → b → (u)
                      |
                      └── back to v
```

Therefore:

```text
ACTIVE → ACTIVE edge
=
directed cycle
```

---

# 8. Directed Cycle Dry Run

Consider:

```text
1 → 2 → 3 → 4
        ↑    |
        └────┘
```

Edges:

```text
1 → 2
2 → 3
3 → 4
4 → 3
```

Initial state:

```text
color[1..4] = 1
```

Compact dry run:

```text
Step 1: dfs(1)
        color[1]=2
        path=[1]

Step 2: edge 1→2, color[2]=1
        parent[2]=1
        color[2]=2
        path=[1,2]

Step 3: edge 2→3, color[3]=1
        parent[3]=2
        color[3]=2
        path=[1,2,3]

Step 4: edge 3→4, color[4]=1
        parent[4]=3
        color[4]=2
        path=[1,2,3,4]

Step 5: edge 4→3
        color[3]=2
        3 is still ACTIVE
        → BACK EDGE
        → cycle found
```

Cycle:

```text
3 → 4 → 3
```

---

# 9. Directed Cycle Detection C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> graph;
vector<int> color;
bool cycleFound = false;

void dfs(int node) {
    color[node] = 2;

    for (int neighbor : graph[node]) {
        if (color[neighbor] == 1) {
            dfs(neighbor);
        } else if (color[neighbor] == 2) {
            cycleFound = true;
        }
    }

    color[node] = 3;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    graph.resize(n + 1);
    color.assign(n + 1, 1);

    for (int i = 0; i < m; i++) {
        int x, y;
        cin >> x >> y;
        graph[x].push_back(y);
    }

    for (int node = 1; node <= n; node++) {
        if (color[node] == 1) {
            dfs(node);
        }
    }

    cout << (cycleFound ? "Cycle exists\n" : "No cycle\n");

    return 0;
}
```

---

# 10. Reconstruct / Print a Directed Cycle

Suppose DFS tree contains:

```text
2
|
5
|
6
|
7
```

and DFS finds:

```text
7 → 2
```

Parent chain:

```text
parent[7] = 6
parent[6] = 5
parent[5] = 2
```

Backtrack:

```text
7 → 6 → 5 → 2
```

Reverse the tree segment:

```text
2 → 5 → 6 → 7
```

Then use the back edge:

```text
7 → 2
```

Final cycle:

```text
2 → 5 → 6 → 7 → 2
```

---

## C++17 Cycle Reconstruction

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> graph;
vector<int> color;
vector<int> parent;

int cycleStart = -1;
int cycleEnd = -1;

bool dfs(int node) {
    color[node] = 2;

    for (int neighbor : graph[node]) {
        if (color[neighbor] == 1) {
            parent[neighbor] = node;

            if (dfs(neighbor)) {
                return true;
            }
        } else if (color[neighbor] == 2) {
            cycleStart = neighbor;
            cycleEnd = node;
            return true;
        }
    }

    color[node] = 3;
    return false;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    graph.resize(n + 1);
    color.assign(n + 1, 1);
    parent.assign(n + 1, -1);

    for (int i = 0; i < m; i++) {
        int x, y;
        cin >> x >> y;
        graph[x].push_back(y);
    }

    for (int node = 1; node <= n && cycleStart == -1; node++) {
        if (color[node] == 1 && dfs(node)) {
            break;
        }
    }

    if (cycleStart == -1) {
        cout << "No cycle\n";
        return 0;
    }

    vector<int> cycle;
    cycle.push_back(cycleStart);

    for (int node = cycleEnd; node != cycleStart; node = parent[node]) {
        cycle.push_back(node);
    }

    cycle.push_back(cycleStart);
    reverse(cycle.begin(), cycle.end());

    for (int node : cycle) {
        cout << node << ' ';
    }

    cout << '\n';
    return 0;
}
```

---

# 11. DAG and Topological Ordering Connection

A directed graph with no directed cycle is a:

```text
DAG
=
Directed Acyclic Graph
```

Therefore:

```text
directed cycle exists
       ↓
not a DAG
```

and:

```text
no directed cycle
       ↓
DAG
```

---

## Topological Ordering

A topological ordering exists only for a DAG.

So:

```text
cycle exists
    ↓
no valid topological ordering
```

The lecture also mentions Kahn's Algorithm.

Kahn's idea:

```text
repeatedly remove nodes with indegree = 0
```

If all nodes are removed:

```text
DAG
```

If some nodes remain:

```text
cycle exists
```

So two common directed-cycle methods are:

```text
1. DFS colors + back edge
2. Kahn's algorithm + indegree
```

---

# 12. Undirected Graph — Why Parent Must Be Ignored

Suppose:

```text
1 ----- 2
```

Adjacency list:

```text
1: [2]
2: [1]
```

DFS does:

```text
1 → 2
```

At node `2`, it sees neighbor `1`.

But `1` is exactly where we came from.

So this is not a cycle.

Therefore:

```text
if (neighbor == parent)
    skip
```

Diagram:

```text
parent
  1
  |
  |
  2
node
```

The edge `2→1` is just the reverse representation of the same undirected edge `1-2`.

---

# 13. Undirected Cycle Detection

For an undirected graph, the key rule is:

```text
visited neighbor
AND
neighbor != parent
       ↓
cycle exists
```

Why?

Because we found another connection to an already visited node that is not simply the edge we used to enter the current node.

---

# 14. Undirected Cycle Dry Run

Consider:

```text
1 ----- 2
 \     /
   \ /
    3
```

Start:

```text
dfs(1, parent=-1)
```

Compact dry run:

```text
Step 1: at 1
        visit 2
        parent[2]=1

Step 2: at 2
        neighbor 1 == parent[2]
        → skip
        visit 3
        parent[3]=2

Step 3: at 3
        neighbor 2 == parent[3]
        → skip

        next neighbor = 1
        1 is already visited
        1 != parent[3]
        → cycle found
```

Cycle:

```text
1 - 2 - 3 - 1
```

---

# 15. Undirected Cycle Detection C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> graph;
vector<int> visited;

bool dfs(int node, int parent) {
    visited[node] = 1;

    for (int neighbor : graph[node]) {
        if (neighbor == parent) {
            continue;
        }

        if (!visited[neighbor]) {
            if (dfs(neighbor, node)) {
                return true;
            }
        } else {
            return true;
        }
    }

    return false;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    graph.resize(n + 1);
    visited.assign(n + 1, 0);

    for (int i = 0; i < m; i++) {
        int x, y;
        cin >> x >> y;

        graph[x].push_back(y);
        graph[y].push_back(x);
    }

    bool cycleFound = false;

    for (int node = 1; node <= n; node++) {
        if (!visited[node] && dfs(node, -1)) {
            cycleFound = true;
            break;
        }
    }

    cout << (cycleFound ? "Cycle exists\n" : "No cycle\n");
    return 0;
}
```

---

# 16. Reconstruct / Print an Undirected Cycle

Suppose the DFS tree is:

```text
1
|
2
|
3
|
4
```

and from `4` we see already visited node `2`.

Then edge:

```text
4 - 2
```

closes:

```text
2 - 3 - 4 - 2
```

Use the parent chain:

```text
4 → 3 → 2
```

Then add the closing edge back to `2`.

So parent tracking serves two purposes:

```text
1. avoid confusing parent edge with a cycle
2. reconstruct the actual cycle
```

---

# 17. Self-Loops

A self-loop is:

```text
u → u
```

or in an undirected graph:

```text
(u,u)
```

Diagram:

```text
   ↺
  (u)
```

This is immediately a cycle.

So while reading input:

```cpp
if (x == y) {
    cycleFound = true;
}
```

No DFS is required to prove it.

---

# 18. Parallel / Multiple Edges

Suppose an undirected graph has two different edges between the same pair:

```text
u ===== v
```

Think:

```text
edge 1: u - v
edge 2: u - v
```

These two physical edges form a 2-edge cycle in a multigraph:

```text
u → v → u
```

using different edges.

This is why the lecture separately considers:

```text
self-loop
multiple edge
normal edge
```

---

## Why Simple Parent-Skip Can Miss Parallel Edges

Adjacency may be:

```text
u: [v,v]
v: [u,u]
```

If DFS enters `v` from `u`, then naïvely skipping every occurrence of:

```text
neighbor == parent
```

can skip both parallel copies.

So duplicate-edge handling during input is useful when the graph is a multigraph.

---

# 19. Robust Undirected Input Handling

For each edge `(x,y)`:

```text
1. detect self-loop
2. normalize pair as (min(x,y), max(x,y))
3. count how many times this pair appears
4. if count > 1 → parallel-edge cycle
5. otherwise add normal adjacency entries
```

Example:

```text
input edge (5,2)
```

normalize:

```text
(2,5)
```

Then both:

```text
2 5
5 2
```

map to the same undirected pair.

---

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> graph;
vector<int> visited;

bool dfs(int node, int parent) {
    visited[node] = 1;

    for (int neighbor : graph[node]) {
        if (neighbor == parent) {
            continue;
        }

        if (!visited[neighbor]) {
            if (dfs(neighbor, node)) {
                return true;
            }
        } else {
            return true;
        }
    }

    return false;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    graph.resize(n + 1);
    visited.assign(n + 1, 0);

    map<pair<int, int>, int> edgeCount;
    bool cycleFound = false;

    for (int i = 0; i < m; i++) {
        int x, y;
        cin >> x >> y;

        if (x == y) {
            cycleFound = true;
            continue;
        }

        if (x > y) {
            swap(x, y);
        }

        edgeCount[{x, y}]++;

        if (edgeCount[{x, y}] > 1) {
            cycleFound = true;
            continue;
        }

        graph[x].push_back(y);
        graph[y].push_back(x);
    }

    if (!cycleFound) {
        for (int node = 1; node <= n; node++) {
            if (!visited[node] && dfs(node, -1)) {
                cycleFound = true;
                break;
            }
        }
    }

    cout << (cycleFound ? "Cycle exists\n" : "No cycle\n");
    return 0;
}
```

---


# 20. Mark / Count Nodes That Belong to Cycles — `cntCycle[]`

The lecture also uses a `cntCycle[]` array after DFS.

This is an important extension of ordinary cycle detection.

```text
Normal cycle detection
        ↓
Does any cycle exist?
YES / NO

cntCycle[] technique
        ↓
Which DFS-tree nodes are covered by a cycle?
How many nodes belong to at least one cycle?
```

> **Important:** `cntCycle[node] > 0` is used to mark that the node is covered by at least one detected cycle. It is **not** the number of distinct simple cycles in the entire graph.

---

## 20.1 Prerequisites

This technique needs four ideas already covered in this note.

### A. DFS Tree

Example:

```text
1
|
2
|
3
|
4
```

Tree edges:

```text
1 - 2
2 - 3
3 - 4
```

---

### B. Parent Array

```text
parent[1] = 0
parent[2] = 1
parent[3] = 2
parent[4] = 3
```

So we can move upward through the DFS tree.

---

### C. Back Edge

Suppose the undirected graph also contains:

```text
4 ----- 2
```

During DFS from `1`:

```text
1
|
2  <────────┐
|           |
3           |
|           |
4 ──────────┘
```

The edge:

```text
4 → 2
```

reaches an ACTIVE ancestor.

Therefore the cycle is:

```text
2 → 3 → 4 → 2
```

The nodes on this DFS-tree path are:

```text
2, 3, 4
```

---

### D. Child-Before-Parent Propagation Order

After DFS we want every child to contribute its value to its parent.

So the safe order is:

```text
leaf → ... → root
```

For the chain:

```text
1 → 2 → 3 → 4
```

we need:

```text
4, 3, 2, 1
```

A simple way is to push a node when DFS **finishes** it:

```cpp
finishOrder.push_back(node);
```

Then `finishOrder` is already child-before-parent.

> In the lecture screenshots the vector is named `prefixOrder`. The key property is not the variable name: propagation must happen **child before parent**. If you store true preorder, iterate that vector in reverse.

---

## 20.2 Core Observation — Use a Difference Array on the DFS Tree

Suppose DFS finds a back edge:

```text
node → ancestor
```

Example:

```text
4 → 2
```

We want to mark the full tree path:

```text
2 → 3 → 4
```

Instead of walking through every node immediately, use two updates:

```text
cntCycle[node]++
cntCycle[parent[ancestor]]--
```

For our example:

```text
node     = 4
ancestor = 2
parent[2] = 1
```

So:

```text
cntCycle[4] += 1
cntCycle[1] -= 1
```

Initial values:

```text
node:       1   2   3   4
cntCycle:  -1   0   0  +1
```

This looks strange initially.

The meaning appears after upward propagation.

---

## 20.3 Why Subtract at `parent[ancestor]`?

We want the marked range to include the ancestor itself.

Desired nodes:

```text
2, 3, 4
```

So the contribution should continue upward through `2`, then stop before `1`.

That is why we subtract at:

```text
parent[2] = 1
```

ASCII:

```text
1       ← stop before here
|
2       ← include
|
3       ← include
|
4       ← include
```

Difference marks:

```text
1 : -1
4 : +1
```

After child-to-parent accumulation, the `+1` travels:

```text
4 → 3 → 2 → 1
```

and is cancelled at node `1`.

---

## 20.4 Step-by-Step Dry Run

Graph:

```text
1 ----- 2
        | \
        |  \
        3---4
```

DFS tree:

```text
1
|
2
|
3
|
4
```

Non-tree/back edge:

```text
4 - 2
```

So cycle:

```text
2 - 3 - 4 - 2
```

### Step 1 — Detect the back edge

At node `4`:

```text
neighbor = 2
color[2] = ACTIVE
```

Therefore:

```text
4 → 2 is a back edge
```

---

### Step 2 — Apply difference updates

```text
cntCycle[4]++
cntCycle[parent[2]]--
```

Since:

```text
parent[2] = 1
```

we get:

```text
cntCycle[4] += 1
cntCycle[1] -= 1
```

State:

```text
node       1    2    3    4
cnt       -1    0    0   +1
```

---

### Step 3 — Propagate from child to parent

Finish order:

```text
4, 3, 2, 1
```

Compact propagation:

```text
node 4: cnt[3] += cnt[4]  → cnt[3] = 1
node 3: cnt[2] += cnt[3]  → cnt[2] = 1
node 2: cnt[1] += cnt[2]  → cnt[1] = 0
node 1: root, stop
```

Final values:

```text
node       1    2    3    4
cnt        0    1    1    1
```

Therefore:

```text
cntCycle[node] > 0
```

for:

```text
2, 3, 4
```

Exactly the cycle nodes.

---

## 20.5 ASCII Visualization

Before propagation:

```text
          cnt=-1
             1
             |
             2
             |
             3
             |
             4
          cnt=+1
             |
             └──── back edge to 2
```

Contribution moves upward:

```text
4 (+1)
↑
3 (+1)
↑
2 (+1)
↑
1 (-1 + 1 = 0)
```

Result:

```text
1  → not on this cycle
2  → on cycle
3  → on cycle
4  → on cycle
```

---

## 20.6 Multiple / Overlapping Cycles

Suppose different back edges cover the same DFS-tree node.

Then a node can receive:

```text
cntCycle[node] = 2
```

or more.

That means:

```text
this node is covered by multiple detected cycle ranges
```

For the common question:

```text
How many nodes belong to at least one cycle?
```

we only care about:

```text
cntCycle[node] > 0
```

So:

```cpp
int nodesInCycle = 0;

for (int node = 1; node <= n; node++) {
    if (cntCycle[node] > 0) {
        nodesInCycle++;
    }
}
```

---

## 20.7 `cntCycle[]` Does NOT Mean “Number of Distinct Cycles”

Do not confuse these questions:

```text
A. Does a cycle exist?
B. Print one cycle.
C. Which nodes lie on a cycle?
D. Count nodes lying on cycles.
E. Count all distinct simple cycles.
```

The `cntCycle[]` propagation technique is useful for **C/D**.

It does not directly solve **E**.

Even the number of DFS back edges is not generally the number of all distinct simple cycles.

---

## 20.8 Clean C++17 — Mark Nodes Covered by Cycles

The following version uses the same 3-state DFS idea as the lecture and is intended for a **simple undirected graph**.

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> graph;
vector<int> color;
vector<int> parentNode;
vector<int> cntCycle;
vector<int> finishOrder;

void dfs(int node, int parent) {
    parentNode[node] = parent;
    color[node] = 2;

    for (int neighbor : graph[node]) {
        if (neighbor == parent) {
            continue;
        }

        if (color[neighbor] == 1) {
            dfs(neighbor, node);
        } else if (color[neighbor] == 2) {
            cntCycle[node]++;

            if (parentNode[neighbor] != 0) {
                cntCycle[parentNode[neighbor]]--;
            }
        }
    }

    color[node] = 3;
    finishOrder.push_back(node);
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    graph.resize(n + 1);
    color.assign(n + 1, 1);
    parentNode.assign(n + 1, 0);
    cntCycle.assign(n + 1, 0);

    for (int i = 0; i < m; i++) {
        int x, y;
        cin >> x >> y;

        graph[x].push_back(y);
        graph[y].push_back(x);
    }

    for (int node = 1; node <= n; node++) {
        if (color[node] == 1) {
            dfs(node, 0);
        }
    }

    for (int node : finishOrder) {
        if (parentNode[node] != 0) {
            cntCycle[parentNode[node]] += cntCycle[node];
        }
    }

    int nodesInCycle = 0;

    for (int node = 1; node <= n; node++) {
        if (cntCycle[node] > 0) {
            nodesInCycle++;
        }
    }

    cout << nodesInCycle << '\n';
    return 0;
}
```

---

## 20.9 Connection to the Screenshot Code

The key propagation line from the lecture is:

```cpp
for (int node : finishOrder) {
    cntCycle[parentNode[node]] += cntCycle[node];
}
```

Conceptually:

```text
child contribution
       ↓
added to parent
       ↓
cycle marking moves upward through DFS tree
```

Then:

```cpp
if (cntCycle[node] > 0) {
    nodesInCycle++;
}
```

counts nodes covered by at least one cycle.

---

## 20.10 Special Cases

The simple code above assumes no problematic duplicate edges.

### Self-loop

```text
u ↺
```

The node `u` is immediately part of a cycle.

### Parallel edges

```text
u ===== v
```

Two parallel undirected edges form a 2-edge cycle in a multigraph.

As explained in Sections 17–19, these cases should be handled explicitly or by using edge IDs instead of skipping every edge whose endpoint equals the parent.

---

## 20.11 Recognition Pattern

When a problem asks:

```text
Which vertices are in cycles?
How many vertices are part of at least one cycle?
Mark vertices covered by cycles.
```

think:

```text
DFS tree
+ parent[]
+ detect back edge
+ difference marks on tree path
+ child → parent propagation
+ cntCycle[node] > 0
```

Mental model:

```text
back edge closes a cycle
        ↓
mark its DFS-tree path cheaply
        ↓
propagate differences upward
        ↓
positive count = covered by a cycle
```

---
# 21. Complexity

For normal DFS cycle detection:

```text
Each vertex is entered once.
Each edge is examined a constant number of times.
```

Therefore:

```text
Time = O(V + E)
```

Memory:

```text
Adjacency list = O(V + E)
color/visited  = O(V)
parent         = O(V)
DFS recursion  = O(V) worst case
```

So:

```text
Space = O(V + E)
```

---

# 22. Common Mistakes

## Mistake 1 — Using only `visited[]` for directed cycle detection

Wrong:

```text
already visited neighbor
→ cycle
```

Correct:

```text
neighbor is ACTIVE in current DFS path
→ cycle
```

Use 3 states.

---

## Mistake 2 — Treating a FINISHED node as an automatic cycle

The important directed-cycle signal is:

```text
color[neighbor] == 2
```

not merely:

```text
neighbor was seen before
```

---

## Mistake 3 — Forgetting parent in undirected DFS

Without:

```cpp
if (neighbor == parent) {
    continue;
}
```

every ordinary undirected edge can look like a fake cycle.

---

## Mistake 4 — Running DFS only from node 1

The graph can be disconnected:

```text
Component A      Component B

1 - 2            5 - 6
                   \ /
                    7
```

Cycle may exist only in another component.

Correct pattern:

```cpp
for (int node = 1; node <= n; node++) {
    if (unvisited(node)) {
        dfs(node);
    }
}
```

---

## Mistake 5 — Missing self-loops

```text
u → u
```

is already a cycle.

---

## Mistake 6 — Missing parallel edges in a multigraph

```text
u ===== v
```

can form a 2-edge cycle.

Handle duplicate edge pairs when the problem allows multiple edges.

---

## Mistake 7 — Mixing color meanings

Keep one fixed convention:

```text
1 = unvisited
2 = active
3 = finished
```

Do not change meanings midway through the implementation.

---


## Mistake 8 — Treating `cntCycle[node]` as the number of distinct cycles

Wrong interpretation:

```text
cntCycle[node] = 2
→ exactly two distinct simple cycles exist
```

Correct interpretation for this technique:

```text
cntCycle[node] > 0
→ node is covered by at least one detected cycle range
```

The array is primarily a **cycle-membership / path-coverage marker** after DFS-tree propagation.

---

# 23. Recognition Checklist

When a problem says:

```text
cycle
loop
acyclic
DAG
topological ordering possible?
circular dependency
dependency loop
```

ask:

```text
1. Directed or undirected?

2. Need only YES/NO,
   or print an actual cycle?

3. Directed?
   → use 3-state DFS
   → edge to ACTIVE node means cycle

4. Undirected?
   → store parent
   → ignore parent edge
   → visited non-parent neighbor means cycle

5. Can self-loops exist?
   → immediate cycle

6. Can parallel edges exist?
   → duplicate undirected pair may form a cycle

7. Graph disconnected?
   → run DFS from every unvisited node

8. Need to count / mark nodes lying on cycles?
   → use parent + back-edge difference marks
   → propagate child to parent
   → cntCycle[node] > 0 means cycle-covered
```

---

# 24. Final Mental Model

## Directed Graph

```text
1 = UNVISITED
2 = ACTIVE
3 = FINISHED
```

For edge:

```text
u → v
```

```text
color[v] == 1
    → DFS tree edge

color[v] == 2
    → back edge
    → CYCLE

color[v] == 3
    → already finished
```

Diagram:

```text
ancestor v
   |
   ↓
   ...
   |
   ↓
   u
   └────────→ v

ACTIVE → ACTIVE
=
cycle
```

---

## Undirected Graph

```text
neighbor == parent
    → ignore

neighbor unvisited
    → dfs(neighbor)

neighbor visited and neighbor != parent
    → cycle
```

---

## Special Cases

```text
self-loop
u → u
    ↓
cycle immediately

parallel edges
u ===== v
    ↓
possible 2-edge cycle in a multigraph
```

---


## Count / Mark Cycle Nodes

For an undirected DFS back edge:

```text
node → ancestor
```

mark:

```text
cntCycle[node]++
cntCycle[parent[ancestor]]--
```

then propagate:

```text
child → parent
```

Finally:

```text
cntCycle[node] > 0
→ node belongs to at least one marked cycle
```

This is different from counting all distinct simple cycles.

---

## One-Line Summary

```text
DIRECTED:
edge to ACTIVE ancestor → cycle

UNDIRECTED:
edge to VISITED non-parent node → cycle
```

That is the core DFS cycle-detection pattern to recognize in contest problems.
