# Topological Sort using BFS — Kahn's Algorithm + Lexicographically Smallest Order

> Self-study note based on the lecture screenshots.  
> Focus: prerequisites → indegree → Kahn's BFS → step-by-step dry run → cycle detection → lexicographically smallest topological order.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Directed Graph and DAG](#2-directed-graph-and-dag)
3. [Topological Ordering](#3-topological-ordering)
4. [Indegree and Outdegree](#4-indegree-and-outdegree)
5. [Main Idea of Kahn's Algorithm](#5-main-idea-of-kahns-algorithm)
6. [Why Indegree 0 Nodes Come First](#6-why-indegree-0-nodes-come-first)
7. [Step-by-Step Kahn Dry Run](#7-step-by-step-kahn-dry-run)
8. [Kahn's Algorithm — Queue Version](#8-kahns-algorithm--queue-version)
9. [Why Kahn's Algorithm Works](#9-why-kahns-algorithm-works)
10. [Cycle Detection using Kahn's Algorithm](#10-cycle-detection-using-kahns-algorithm)
11. [Why Multiple Topological Orders Exist](#11-why-multiple-topological-orders-exist)
12. [Lexicographically Smallest Topological Order](#12-lexicographically-smallest-topological-order)
13. [Min-Heap Step-by-Step Dry Run](#13-min-heap-step-by-step-dry-run)
14. [Lexicographically Smallest C++17](#14-lexicographically-smallest-c17)
15. [Queue vs Min-Heap](#15-queue-vs-min-heap)
16. [DFS Topo vs Kahn BFS](#16-dfs-topo-vs-kahn-bfs)
17. [Complexity](#17-complexity)
18. [Common Mistakes](#18-common-mistakes)
19. [Recognition Model](#19-recognition-model)
20. [Final Mental Model](#20-final-mental-model)

---

# 1. Prerequisites

Before Kahn's algorithm, you should know:

- directed graph,
- adjacency list,
- DAG,
- queue,
- priority queue / min-heap,
- indegree,
- topological ordering.

Learning chain:

```text
Directed Graph
      ↓
Indegree of every node
      ↓
Find nodes with indegree 0
      ↓
Process them
      ↓
Remove their outgoing effect
      ↓
New indegree-0 nodes appear
      ↓
Topological order
```

---

# 2. Directed Graph and DAG

A directed edge:

```text
u → v
```

means the direction matters.

In dependency problems, it usually means:

```text
u must happen before v
```

Examples:

```text
Course A → Course B
Task A   → Task B
Package A → Package B
```

---

## DAG

DAG:

```text
Directed Acyclic Graph
```

Example:

```text
1 → 2 → 5
↓
3 → 4
↑   ↑
└───┘
```

As long as there is no directed cycle, a topological order can exist.

A graph like:

```text
1 → 2 → 3
↑       ↓
└───────┘
```

contains a cycle, so no valid topological ordering exists.

---

# 3. Topological Ordering

A topological ordering is a linear order of vertices such that:

```text
for every edge u → v

u appears before v
```

Formally:

```text
position[u] < position[v]
```

for every directed edge:

```text
u → v
```

---

## Example

Graph:

```text
1 → 2
1 → 3
2 → 4
3 → 4
2 → 5
```

Valid topological orders include:

```text
1 2 3 5 4
1 3 2 4 5
1 3 2 5 4
```

The exact answer may not be unique.

The only requirement is:

```text
every prerequisite appears before the node depending on it
```

---

# 4. Indegree and Outdegree

For a directed graph:

```text
u → v
```

the edge:

- leaves `u`,
- enters `v`.

So:

```text
outdegree[u] += 1
indegree[v]  += 1
```

---

## Example

```text
1 → 2
1 → 3
3 → 4
2 → 4
```

Indegrees:

```text
node 1 → 0
node 2 → 1
node 3 → 1
node 4 → 2
```

ASCII view:

```text
        indegree
1           0
│\
│ \
↓  ↓
2  3        1, 1
 \ /
  ↓
  4          2
```

---

## Building Indegree

Whenever you read:

```text
u → v
```

do:

```cpp
graph[u].push_back(v);
indegree[v]++;
```

That is the key preprocessing step for Kahn's algorithm.

---

# 5. Main Idea of Kahn's Algorithm

Kahn's algorithm repeatedly removes nodes that currently have:

```text
indegree = 0
```

Why?

Because such a node has no remaining prerequisite.

So it is safe to place it next in the topological order.

Algorithm:

```text
1. Compute indegree of every node.

2. Put all nodes with indegree 0 into a queue.

3. Repeatedly:
      take one node from the queue
      add it to topological order

4. For every edge:
      current → next

   decrease:
      indegree[next]--

5. If indegree[next] becomes 0:
      push next into queue
```

This is often called:

```text
BFS Topological Sort
```

or:

```text
Kahn's Algorithm
```

---

# 6. Why Indegree 0 Nodes Come First

Suppose:

```text
A → B
C → B
```

Then `B` has:

```text
indegree[B] = 2
```

Meaning:

```text
B is waiting for A
B is waiting for C
```

So we cannot place `B` yet.

After processing `A`:

```text
indegree[B] = 1
```

Still one prerequisite remains.

After processing `C`:

```text
indegree[B] = 0
```

Now:

```text
all prerequisites of B are already placed
```

Therefore `B` becomes available.

This is the whole intuition.

---

# 7. Step-by-Step Kahn Dry Run

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

---

## Step 1 — Compute Indegree

```text
indegree[1] = 0
indegree[2] = 1
indegree[3] = 1
indegree[4] = 2
indegree[5] = 1
```

Table:

| Node | Incoming From | Indegree |
|---|---|---:|
| 1 | none | 0 |
| 2 | 1 | 1 |
| 3 | 1 | 1 |
| 4 | 2, 3 | 2 |
| 5 | 2 | 1 |

Only node `1` starts with indegree `0`.

```text
queue = [1]
topo  = []
```

---

## Step 2 — Process 1

Pop:

```text
1
```

Add to answer:

```text
topo = [1]
```

Remove effect of:

```text
1 → 2
1 → 3
```

So:

```text
indegree[2]: 1 → 0
indegree[3]: 1 → 0
```

Both become available.

```text
queue = [2, 3]
```

---

## Step 3 — Process 2

Pop:

```text
2
```

```text
topo = [1, 2]
```

Process edges:

```text
2 → 4
2 → 5
```

Update:

```text
indegree[4]: 2 → 1
indegree[5]: 1 → 0
```

So `5` enters queue.

```text
queue = [3, 5]
```

---

## Step 4 — Process 3

Pop:

```text
3
```

```text
topo = [1, 2, 3]
```

Edge:

```text
3 → 4
```

Update:

```text
indegree[4]: 1 → 0
```

So:

```text
queue = [5, 4]
```

---

## Step 5 — Process 5

```text
topo = [1, 2, 3, 5]
queue = [4]
```

`5` has no outgoing edges.

---

## Step 6 — Process 4

```text
topo = [1, 2, 3, 5, 4]
queue = []
```

Final:

```text
1 2 3 5 4
```

Check:

```text
1 before 2 ✓
1 before 3 ✓
2 before 4 ✓
2 before 5 ✓
3 before 4 ✓
```

Valid topological order.

---

# 8. Kahn's Algorithm — Queue Version

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<vector<int>> graph(n + 1);
    vector<int> indegree(n + 1, 0);

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;

        graph[u].push_back(v);
        indegree[v]++;
    }

    queue<int> q;

    for (int node = 1; node <= n; node++) {
        if (indegree[node] == 0) {
            q.push(node);
        }
    }

    vector<int> topo;

    while (!q.empty()) {
        int node = q.front();
        q.pop();

        topo.push_back(node);

        for (int next : graph[node]) {
            indegree[next]--;

            if (indegree[next] == 0) {
                q.push(next);
            }
        }
    }

    if ((int)topo.size() != n) {
        cout << "Cycle exists\n";
        return 0;
    }

    for (int node : topo) {
        cout << node << ' ';
    }

    cout << '\n';

    return 0;
}
```

---

# 9. Why Kahn's Algorithm Works

The important invariant is:

```text
every node inside the queue currently has indegree 0
```

That means:

```text
all of its prerequisites have already been processed
```

So it is safe to place that node next.

When we process:

```text
u
```

and remove the outgoing dependency:

```text
u → v
```

we do:

```text
indegree[v]--
```

When this becomes:

```text
0
```

all prerequisites of `v` are completed.

Therefore:

```text
v is now safe to process
```

This preserves the topological-order rule at every step.

---

# 10. Cycle Detection using Kahn's Algorithm

Kahn's algorithm gives cycle detection almost for free.

Suppose:

```text
1 → 2 → 3
↑       ↓
└───────┘
```

Indegrees:

```text
1 = 1
2 = 1
3 = 1
```

There is no node with:

```text
indegree = 0
```

So the initial queue is empty.

Nothing can be processed.

Therefore:

```text
topo.size() < n
```

---

## General Rule

After Kahn:

```cpp
if ((int)topo.size() == n) {
    // DAG
} else {
    // directed cycle exists
}
```

Why?

In a directed cycle, every remaining cycle node still has at least one incoming edge from another remaining cycle node.

So none of them can reach indegree `0`.

---

# 11. Why Multiple Topological Orders Exist

Suppose:

```text
1 → 3
2 → 3
```

Initially:

```text
indegree[1] = 0
indegree[2] = 0
```

Both are valid first choices.

Possible answers:

```text
1 2 3
```

and:

```text
2 1 3
```

Both are correct.

So the queue order can affect which valid topological order you obtain.

---

## Important Observation

At any moment:

```text
all indegree-0 nodes are currently legal choices
```

Therefore if we want a special ordering among them, we can control the data structure.

Normal arbitrary order:

```text
queue
```

Lexicographically smallest order:

```text
min-heap
```

---

# 12. Lexicographically Smallest Topological Order

Suppose several valid topological orders exist.

We want the lexicographically smallest.

For example:

```text
[1, 2, 5, 3, 4]
```

is smaller than:

```text
[1, 3, 2, 4, 5]
```

because at the first position where they differ:

```text
2 < 3
```

---

## Greedy Observation

At each step:

```text
choose the smallest currently available indegree-0 node
```

This is safe because every indegree-0 node is a legal next choice.

Choosing the smallest one gives the smallest possible current position.

Repeating this at every position produces the lexicographically smallest valid topological order.

---

## Data Structure

Use:

```cpp
priority_queue<int, vector<int>, greater<int>>
```

This is a min-heap.

The top always gives:

```text
smallest available node
```

---

# 13. Min-Heap Step-by-Step Dry Run

Consider:

```text
4 → 2
4 → 3
2 → 1
3 → 1
2 → 5
```

Graph:

```text
      4
     / \
    ↓   ↓
    2   3
   / \   \
  ↓   ↓   ↓
  1   5   1
```

Indegrees:

```text
1 = 2
2 = 1
3 = 1
4 = 0
5 = 1
```

Initial min-heap:

```text
[4]
```

---

## Iteration 1

Take smallest:

```text
4
```

Answer:

```text
[4]
```

Decrease:

```text
indegree[2]: 1 → 0
indegree[3]: 1 → 0
```

Heap now contains:

```text
[2, 3]
```

Smallest is `2`.

---

## Iteration 2

Take:

```text
2
```

Answer:

```text
[4, 2]
```

Decrease:

```text
indegree[1]: 2 → 1
indegree[5]: 1 → 0
```

Heap:

```text
[3, 5]
```

Choose:

```text
3
```

because:

```text
3 < 5
```

---

## Iteration 3

Take:

```text
3
```

Answer:

```text
[4, 2, 3]
```

Decrease:

```text
indegree[1]: 1 → 0
```

Heap:

```text
[1, 5]
```

Now the smallest available node is:

```text
1
```

---

## Iteration 4

Take:

```text
1
```

Answer:

```text
[4, 2, 3, 1]
```

Heap:

```text
[5]
```

---

## Iteration 5

Take:

```text
5
```

Final:

```text
[4, 2, 3, 1, 5]
```

This is the lexicographically smallest valid topological ordering.

---

# 14. Lexicographically Smallest C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<vector<int>> graph(n + 1);
    vector<int> indegree(n + 1, 0);

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;

        graph[u].push_back(v);
        indegree[v]++;
    }

    priority_queue<int, vector<int>, greater<int>> pq;

    for (int node = 1; node <= n; node++) {
        if (indegree[node] == 0) {
            pq.push(node);
        }
    }

    vector<int> topo;

    while (!pq.empty()) {
        int node = pq.top();
        pq.pop();

        topo.push_back(node);

        for (int next : graph[node]) {
            indegree[next]--;

            if (indegree[next] == 0) {
                pq.push(next);
            }
        }
    }

    if ((int)topo.size() != n) {
        cout << "Cycle exists\n";
        return 0;
    }

    for (int node : topo) {
        cout << node << ' ';
    }

    cout << '\n';

    return 0;
}
```

---

# 15. Queue vs Min-Heap

## Normal Kahn

Use:

```text
queue
```

when the problem says:

```text
find any valid topological ordering
```

Example:

```cpp
queue<int> q;
```

---

## Lexicographically Smallest Kahn

Use:

```text
min-heap
```

when the problem says:

```text
smallest topological ordering
lexicographically smallest
smallest numbered available task first
```

Example:

```cpp
priority_queue<int, vector<int>, greater<int>> pq;
```

---

## Comparison

| Requirement | Data Structure |
|---|---|
| Any valid topological order | `queue<int>` |
| Smallest available node first | min-heap |
| Lexicographically smallest topo | min-heap |
| Largest available node first | max-heap |

---

# 16. DFS Topo vs Kahn BFS

Both produce a valid topological ordering on a DAG.

## DFS Method

Core idea:

```text
visit all children
↓
push node on EXIT
↓
reverse result
```

Code idea:

```cpp
dfs(child);
topo.push_back(node);
```

---

## Kahn Method

Core idea:

```text
indegree = 0
↓
process node
↓
remove outgoing edges
↓
new indegree-0 nodes appear
```

---

## Side-by-Side

| DFS Topological Sort | Kahn's Algorithm |
|---|---|
| recursion / DFS | queue / BFS style |
| uses visited array | uses indegree array |
| push on DFS exit | push when indegree becomes 0 |
| reverse postorder | no reverse needed |
| cycle detection needs DFS state/colors | cycle detection by processed count |
| arbitrary valid order | easy to make lexicographically smallest |

---

# 17. Complexity

Let:

```text
V = number of vertices
E = number of directed edges
```

---

## Normal Queue Kahn

Every node:

```text
enters queue once
leaves queue once
```

Every edge:

```text
processed once
```

Therefore:

```text
Time = O(V + E)
```

Space:

```text
graph     = O(V + E)
indegree  = O(V)
queue     = O(V)
topo      = O(V)
```

So:

```text
Space = O(V + E)
```

---

## Lexicographically Smallest Version

Each node enters and leaves the heap once.

Heap operation:

```text
O(log V)
```

Edge indegree updates remain:

```text
O(E)
```

So a tight bound is:

```text
O(E + V log V)
```

It is also commonly upper-bounded as:

```text
O((V + E) log V)
```

Space remains:

```text
O(V + E)
```

---

# 18. Common Mistakes

| Mistake | Fix |
|---|---|
| Incrementing `indegree[u]` for `u → v` | Increment `indegree[v]` |
| Starting queue with only node 1 | Push **all** nodes whose indegree is 0 |
| Using `visited` unnecessarily | Kahn mainly relies on indegree |
| Forgetting `indegree[next]--` | Remove the processed prerequisite |
| Pushing a node before its indegree becomes 0 | Push only when it becomes exactly 0 |
| Assuming topo order is unique | Many DAGs have multiple valid answers |
| Using normal queue for lexicographically smallest order | Use min-heap |
| Ignoring `topo.size() < n` | That indicates a directed cycle |
| Applying topological sort to an undirected graph | Topological ordering is a directed-graph concept |

---

# 19. Recognition Model

When you see:

```text
prerequisite
dependency
task scheduling
course schedule
build order
must come before
DAG
topological ordering
alien dictionary
dependency resolution
smallest valid ordering
lexicographically smallest order
```

think:

```text
Directed Graph
      ↓
Indegree
      ↓
Kahn's Algorithm
```

If the statement says:

```text
any valid order
```

use:

```text
queue
```

If it says:

```text
lexicographically smallest
smallest numbered available first
minimum possible ordering
```

use:

```text
min-heap
```

---

# 20. Final Mental Model

Remember Kahn's algorithm as:

```text
indegree = number of unfinished prerequisites
```

Then:

```text
indegree 0
    ↓
nothing is blocking this node
    ↓
safe to process
    ↓
remove its outgoing dependencies
    ↓
some new nodes become indegree 0
```

Full picture:

```text
          Directed Graph
                │
                ▼
       compute indegrees
                │
                ▼
       all indegree-0 nodes
                │
        ┌───────┴────────┐
        │                │
        ▼                ▼
      queue           min-heap
        │                │
        ▼                ▼
   any topo      lexicographically
     order         smallest topo
        │                │
        └───────┬────────┘
                ▼
       process outgoing edges
                │
                ▼
        indegree[next]--
                │
                ▼
       if it becomes 0
           add it
```

The most important three rules are:

```text
1. For u → v:
   indegree[v]++

2. Process only indegree-0 nodes.

3. For lexicographically smallest order:
   among all available nodes,
   always choose the smallest one.
```

And for cycle detection:

```text
processed nodes < V
        ↓
directed cycle exists
```
