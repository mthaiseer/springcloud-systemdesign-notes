# Topological Sort using DFS + Longest Path in a DAG

> Self-study note based on the lecture screenshots.  
> Focus: prerequisites → DAG → DFS finish time → topological order → longest path with memoization → longest path with topological DP.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Directed Graph and DAG](#2-directed-graph-and-dag)
3. [Topological Ordering](#3-topological-ordering)
4. [Why Cycles Break Topological Ordering](#4-why-cycles-break-topological-ordering)
5. [DFS Entry and Exit](#5-dfs-entry-and-exit)
6. [Topological Sort using DFS](#6-topological-sort-using-dfs)
7. [Step-by-Step Dry Run](#7-step-by-step-dry-run)
8. [C++17 — DFS Topological Sort](#8-c17--dfs-topological-sort)
9. [Why It Works](#9-why-it-works)
10. [Multiple Components](#10-multiple-components)
11. [Longest Path in a DAG](#11-longest-path-in-a-dag)
12. [Longest-Path Recurrence](#12-longest-path-recurrence)
13. [Memoized DFS Solution](#13-memoized-dfs-solution)
14. [Longest-Path Dry Run](#14-longest-path-dry-run)
15. [Topological DP Solution](#15-topological-dp-solution)
16. [DFS Postorder vs Topological Order](#16-dfs-postorder-vs-topological-order)
17. [Node Count vs Edge Count](#17-node-count-vs-edge-count)
18. [Complexity](#18-complexity)
19. [Common Mistakes](#19-common-mistakes)
20. [Recognition Model](#20-recognition-model)
21. [Final Mental Model](#21-final-mental-model)

---

# 1. Prerequisites

You should know these first:

- directed graph,
- adjacency list,
- DFS recursion,
- visited array,
- DAG,
- memoization / DP.

Learning chain:

```text
Directed Graph
     ↓
No directed cycle
     ↓
    DAG
     ↓
Topological Ordering
     ↓
Dependency order
     ↓
DP on DAG
```

---

# 2. Directed Graph and DAG

## Directed Graph

An edge has a direction.

```text
u → v
```

Possible meanings:

```text
u must happen before v
u is a prerequisite of v
u enables v
```

Adjacency-list representation:

```cpp
graph[u].push_back(v);
```

---

## DAG

DAG means:

```text
Directed Acyclic Graph
```

Example DAG:

```text
1 → 2 → 4
 \      ↑
  → 3 ──┘
```

A cyclic graph is not a DAG:

```text
1 → 2 → 3
↑       ↓
└───────┘
```

because:

```text
1 → 2 → 3 → 1
```

is a directed cycle.

---

# 3. Topological Ordering

A topological ordering is an ordering of vertices such that for every edge:

```text
u → v
```

`u` appears before `v`.

Mathematically:

```text
position[u] < position[v]
```

for every directed edge `u → v`.

---

## Tiny Example

Graph:

```text
1 → 2
1 → 3
2 → 4
3 → 4
```

Valid order:

```text
1 2 3 4
```

Also valid:

```text
1 3 2 4
```

So topological order is usually **not unique**.

---

# 4. Why Cycles Break Topological Ordering

Suppose:

```text
1 → 2
2 → 3
3 → 1
```

Then ordering requires:

```text
1 before 2
2 before 3
3 before 1
```

So:

```text
1 < 2 < 3 < 1
```

Impossible.

Therefore:

```text
Topological ordering exists ⇔ graph is a DAG
```

---

# 5. DFS Entry and Exit

Normal DFS:

```cpp
void dfs(int node) {
    visited[node] = 1;

    for (int next : graph[node]) {
        if (!visited[next]) {
            dfs(next);
        }
    }
}
```

Two important moments:

```text
ENTER node
    ↓
visit all children
    ↓
EXIT node
```

For topological sorting, the important moment is **EXIT**.

Example:

```text
1 → 2 → 4
    ↓
    5
```

DFS may behave like:

```text
enter 1
enter 2
enter 4
exit 4
enter 5
exit 5
exit 2
exit 1
```

---

# 6. Topological Sort using DFS

Core idea:

```text
visit every child first
then push the current node
```

Code pattern:

```cpp
void dfs(int node) {
    visited[node] = 1;

    for (int next : graph[node]) {
        if (!visited[next]) {
            dfs(next);
        }
    }

    topo.push_back(node);
}
```

That produces:

```text
DFS finish order
```

which is the **reverse** of topological order.

So:

```text
DFS postorder
     ↓
reverse()
     ↓
Topological Order
```

---

# 7. Step-by-Step Dry Run

Use this graph:

```text
        1
       / \
      ↓   ↓
      2   3
     / \   \
    ↓   ↓   ↓
    4   5   4
```

Edges:

```text
1 → 2
1 → 3
2 → 4
2 → 5
3 → 4
```

Adjacency order:

```text
1: [2, 3]
2: [4, 5]
3: [4]
4: []
5: []
```

Start:

```text
dfs(1)
```

Compact execution:

```text
enter 1
  enter 2
    enter 4 → push 4
    enter 5 → push 5
  push 2
  enter 3
    4 already visited
  push 3
push 1
```

Postorder:

```text
[4, 5, 2, 3, 1]
```

Reverse it:

```text
[1, 3, 2, 5, 4]
```

Check:

```text
1 → 2   ✓
1 → 3   ✓
2 → 4   ✓
2 → 5   ✓
3 → 4   ✓
```

Valid topological order.

---

# 8. C++17 — DFS Topological Sort

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> graph;
vector<int> visited;
vector<int> topo;

void dfs(int node) {
    visited[node] = 1;

    for (int next : graph[node]) {
        if (!visited[next]) {
            dfs(next);
        }
    }

    topo.push_back(node);
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    graph.resize(n + 1);
    visited.assign(n + 1, 0);

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;

        graph[u].push_back(v);
    }

    for (int node = 1; node <= n; node++) {
        if (!visited[node]) {
            dfs(node);
        }
    }

    reverse(topo.begin(), topo.end());

    for (int node : topo) {
        cout << node << ' ';
    }

    cout << '\n';

    return 0;
}
```

---

# 9. Why It Works

Consider one edge:

```text
u → v
```

## Case 1 — `v` is not visited

DFS goes into `v` first.

```text
u
↓
v
```

`v` finishes before `u`.

So postorder contains:

```text
... v ... u
```

After reversing:

```text
... u ... v
```

Correct.

---

## Case 2 — `v` is already finished

Then `v` is already in the finish-order list.

Since the graph is a DAG, there is no directed back edge that forces a cycle.

After reversal, all edges satisfy:

```text
u before v
```

---

# 10. Multiple Components

The DAG can be disconnected.

Example:

```text
1 → 2

3 → 4
```

Calling only:

```cpp
dfs(1);
```

misses `3` and `4`.

So always do:

```cpp
for (int node = 1; node <= n; node++) {
    if (!visited[node]) {
        dfs(node);
    }
}
```

---

# 11. Longest Path in a DAG

Now use the DAG property for DP.

Example:

```text
1 → 2 → 4
|    \
↓     → 5
3 → 4
```

We want the longest path starting at each node.

For sink `4`:

```text
4
```

Longest path = `1` node.

For `2`:

```text
2 → 4
```

or:

```text
2 → 5
```

Longest = `2` nodes.

For `1`:

```text
1 → 2 → 4
```

Longest = `3` nodes.

---

# 12. Longest-Path Recurrence

Define:

```text
dp[u] = maximum number of nodes
        on a path starting at u
```

If `u` has no children:

```text
dp[u] = 1
```

Otherwise:

```text
dp[u] = 1 + max(dp[v])
```

for every:

```text
u → v
```

Visual:

```text
        u
      / | \
     ↓  ↓  ↓
    v1 v2  v3

choose the child with the best dp
```

So:

```text
dp[u] = 1 + max(
    dp[v1],
    dp[v2],
    dp[v3]
)
```

---

# 13. Memoized DFS Solution

Without memoization, shared subproblems repeat.

Example:

```text
1 → 2 → 4
 \  3 → 4
```

Both `2` and `3` need the answer for `4`.

So store computed values.

```text
dp[u] = -1  → not computed
```

Core function:

```cpp
int longestPathFrom(int node) {
    if (dp[node] != -1) {
        return dp[node];
    }

    int best = 1;

    for (int next : graph[node]) {
        best = max(best, 1 + longestPathFrom(next));
    }

    return dp[node] = best;
}
```

Complete code:

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> graph;
vector<int> dp;

int longestPathFrom(int node) {
    if (dp[node] != -1) {
        return dp[node];
    }

    int best = 1;

    for (int next : graph[node]) {
        best = max(best, 1 + longestPathFrom(next));
    }

    return dp[node] = best;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    graph.resize(n + 1);
    dp.assign(n + 1, -1);

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;

        graph[u].push_back(v);
    }

    int answer = 0;

    for (int node = 1; node <= n; node++) {
        answer = max(answer, longestPathFrom(node));
    }

    cout << answer << '\n';

    return 0;
}
```

> This recurrence assumes the graph is a DAG.

---

# 14. Longest-Path Dry Run

Graph:

```text
1 → 2
1 → 3
2 → 4
2 → 5
3 → 4
```

Initially:

```text
dp[1..5] = -1
```

Call:

```text
longestPathFrom(1)
```

Compact dry run:

```text
4: no child → dp[4] = 1
5: no child → dp[5] = 1
2: max(1+dp[4], 1+dp[5]) = 2 → dp[2] = 2
3: child 4 already known → 1+dp[4] = 2 → dp[3] = 2
1: max(1+dp[2], 1+dp[3]) = 3 → dp[1] = 3
```

Final:

```text
dp[1] = 3
dp[2] = 2
dp[3] = 2
dp[4] = 1
dp[5] = 1
```

Longest path length in nodes:

```text
3
```

Example path:

```text
1 → 2 → 4
```

---

# 15. Topological DP Solution

We can solve longest path without recursive DP.

Suppose topological order is:

```text
[1, 3, 2, 5, 4]
```

For edge:

```text
u → v
```

`dp[u]` depends on `dp[v]`.

So children should be computed first.

Therefore process the topological order **backwards**:

```text
1   3   2   5   4
                ←
```

Initialize:

```text
dp[u] = 1
```

Process:

```text
4 → dp[4] = 1
5 → dp[5] = 1
2 → max(1+dp[4], 1+dp[5]) = 2
3 → 1+dp[4] = 2
1 → max(1+dp[2], 1+dp[3]) = 3
```

Complete code:

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> graph;
vector<int> visited;
vector<int> topo;

void dfs(int node) {
    visited[node] = 1;

    for (int next : graph[node]) {
        if (!visited[next]) {
            dfs(next);
        }
    }

    topo.push_back(node);
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    graph.resize(n + 1);
    visited.assign(n + 1, 0);

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;

        graph[u].push_back(v);
    }

    for (int node = 1; node <= n; node++) {
        if (!visited[node]) {
            dfs(node);
        }
    }

    reverse(topo.begin(), topo.end());

    vector<int> dp(n + 1, 1);
    int answer = 1;

    for (int i = (int)topo.size() - 1; i >= 0; i--) {
        int node = topo[i];

        for (int next : graph[node]) {
            dp[node] = max(dp[node], 1 + dp[next]);
        }

        answer = max(answer, dp[node]);
    }

    cout << answer << '\n';

    return 0;
}
```

---

# 16. DFS Postorder vs Topological Order

This is an important connection from the lecture.

DFS naturally gives:

```text
children before parent
```

Example postorder:

```text
[4, 5, 2, 3, 1]
```

Topological order is:

```text
[1, 3, 2, 5, 4]
```

So:

```text
postorder = reverse topological order
```

For longest-path DP:

```text
dp[parent] needs dp[child]
```

Therefore DFS postorder is already a useful DP processing order.

Two equivalent approaches:

```text
Approach A
postorder
   ↓ reverse
true topo
   ↓ process backwards
DAG DP
```

or simply:

```text
Approach B
postorder
   ↓
DAG DP directly
```

because children already come before parents.

---

# 17. Node Count vs Edge Count

The lecture-style recurrence starts with:

```text
best = 1
```

So it counts **nodes**.

Example:

```text
1 → 2 → 4
```

Nodes:

```text
3
```

Edges:

```text
2
```

Therefore:

```text
edgeCount = nodeCount - 1
```

If the problem asks directly for number of edges, use:

```text
dp[u] = 0                     if sink

dp[u] = 1 + max(dp[v])        otherwise
```

Always check what the answer represents.

---

# 18. Complexity

## Topological Sort

Every node visited once.
Every edge checked once.

```text
Time = O(V + E)
Space = O(V + E)
```

## Memoized Longest Path

Each `dp[u]` computed once.
Every edge contributes once.

```text
Time = O(V + E)
Space = O(V + E)
```

## Topological DP

Topological sort:

```text
O(V + E)
```

DP pass:

```text
O(V + E)
```

Total:

```text
O(V + E)
```

---

# 19. Common Mistakes

| Mistake | Fix |
|---|---|
| Push node before DFS children | Push only on DFS exit |
| Forget `reverse()` | DFS postorder is reverse topo |
| DFS only from node `1` | Run DFS from every unvisited node |
| Apply topo sort to a cyclic graph | Topological order requires DAG |
| Forget memoization in longest path | Cache `dp[u]` |
| Process topo in wrong direction for `dp[u] = 1 + dp[v]` | Children must be ready first |
| Confuse node count and edge count | Decide what `dp[u]` stores |
| Assume only one topo order exists | Multiple valid orders may exist |

---

# 20. Recognition Model

When you see:

```text
prerequisite
dependency
course order
task order
build order
must happen before
DAG
longest dependency chain
maximum chain of tasks
```

Think:

```text
Directed Graph
      ↓
DAG
      ↓
Topological Order
      ↓
DP if optimization is asked
```

### If problem asks only for a valid ordering

Use:

```text
DFS Topological Sort
```

### If problem asks longest chain/path in a DAG

Use:

```text
dp[u] = 1 + max(dp[v])
```

with either:

```text
memoized DFS
```

or:

```text
topological DP
```

---

# 21. Final Mental Model

Remember these four lines:

```text
1. Topological rule:
   u → v  ⇒  u must appear before v

2. DFS trick:
   push node only after all children finish

3. DFS gives reverse topo:
   postorder → reverse → topological order

4. Longest path in DAG:
   dp[u] = 1 + max(dp[v])
```

Visual summary:

```text
        DAG
         │
         ▼
        DFS
         │
         ▼
  push on EXIT
         │
         ▼
    postorder
         │
     reverse()
         ▼
 topological order
         │
         ▼
      DAG DP
         │
         ▼
 dp[u] = 1 + max(dp[v])
         │
         ▼
   longest path
```
