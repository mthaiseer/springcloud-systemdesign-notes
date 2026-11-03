# Directed Graphs & Topological Sort
## TLE-Style Visual Self-Study Notes
### Prerequisites -> Directed Graphs -> DAG -> Cycle Detection -> DFS Topo -> Kahn's Algorithm -> Lexicographically Smallest Topo

> **Lecture basis:** TLE Graphs Advanced - *Directed Graphs & Topo Sort*.
>
> **Self-study expansion added here:** tiny prerequisite examples, cleaner ASCII graphs, explicit DFS-state intuition, detailed dry runs, and clean C++17.
>
> Goal: understand the ideas deeply enough that you can recognize and solve directed-graph dependency problems without memorizing code.

---

# Table of Contents

1. [What You Need Before This Topic](#1-what-you-need-before-this-topic)
2. [Prerequisite - Directed Edge](#2-prerequisite---directed-edge)
3. [Prerequisite - Adjacency List for Directed Graph](#3-prerequisite---adjacency-list-for-directed-graph)
4. [Prerequisite - DFS and BFS](#4-prerequisite---dfs-and-bfs)
5. [Prerequisite - Indegree and Outdegree](#5-prerequisite---indegree-and-outdegree)
6. [Directed Graphs](#6-directed-graphs)
7. [Directed Acyclic Graph - DAG](#7-directed-acyclic-graph---dag)
8. [Undirected vs Directed Cycle Thinking](#8-undirected-vs-directed-cycle-thinking)
9. [Cycle Detection in Directed Graph](#9-cycle-detection-in-directed-graph)
10. [Cycle Detection Dry Run](#10-cycle-detection-dry-run)
11. [Cycle Detection C++17](#11-cycle-detection-c17)
12. [Topological Ordering](#12-topological-ordering)
13. [Why Topological Sort Requires a DAG](#13-why-topological-sort-requires-a-dag)
14. [Topo Sort Using DFS](#14-topo-sort-using-dfs)
15. [DFS Topo Dry Run](#15-dfs-topo-dry-run)
16. [DFS Topo C++17](#16-dfs-topo-c17)
17. [Topo Sort Using BFS - Kahn's Algorithm](#17-topo-sort-using-bfs---kahns-algorithm)
18. [Kahn Dry Run](#18-kahn-dry-run)
19. [Kahn C++17](#19-kahn-c17)
20. [Cycle Detection Using Kahn](#20-cycle-detection-using-kahn)
21. [DFS Topo vs Kahn](#21-dfs-topo-vs-kahn)
22. [Lexicographically Smallest Topological Order](#22-lexicographically-smallest-topological-order)
23. [Lexicographically Smallest Topo C++17](#23-lexicographically-smallest-topo-c17)
24. [Applications](#24-applications)
25. [Common Mistakes](#25-common-mistakes)
26. [Final Recognition Model](#26-final-recognition-model)
27. [Practice Checklist](#27-practice-checklist)

---

# 1. What You Need Before This Topic

The lecture assumes you already know:

```text
basic graph representation
DFS
BFS
```

For this note, we will rebuild the exact minimum foundation needed.

You should understand these terms:

```text
node / vertex
edge
directed edge
adjacency list
DFS
BFS
visited[]
recursion
indegree
outdegree
cycle
DAG
```

The central story of this chapter is:

```text
direction
   ↓
dependency
   ↓
cycle or no cycle?
   ↓
if no cycle
   ↓
topological ordering
```

---

# 2. Prerequisite - Directed Edge

In an undirected graph:

```text
A ----- B
```

means both directions are possible:

```text
A -> B
B -> A
```

But in a directed graph:

```text
A -----> B
```

means:

```text
A can go to B
```

It does **not** automatically mean:

```text
B can go to A
```

---

## 2.1 Real-World Meaning

### Web link

```text
Page A -----> Page B
```

means:

```text
A contains a link to B
```

It does not imply B links back to A.

### Social media following

```text
Alice -----> Bob
```

can mean:

```text
Alice follows Bob
```

Bob does not have to follow Alice.

### Dependency

```text
Task A -----> Task B
```

can mean:

```text
A must happen before B
```

This dependency interpretation is especially important for topological sorting.

---

# 3. Prerequisite - Adjacency List for Directed Graph

Suppose:

```text
1 -----> 2
|
+-----> 3
2 -----> 4
3 -----> 4
```

Adjacency list:

```text
1 -> [2, 3]
2 -> [4]
3 -> [4]
4 -> []
```

Notice:

```text
1 -> 2
```

adds only:

```cpp
graph[1].push_back(2);
```

We do **not** add:

```cpp
graph[2].push_back(1);
```

unless the reverse edge actually exists.

---

## 3.1 C++ Representation

```cpp
int n, m;
cin >> n >> m;

vector<vector<int>> graph(n + 1);

for (int i = 0; i < m; i++) {
    int u, v;
    cin >> u >> v;

    graph[u].push_back(v);
}
```

---

# 4. Prerequisite - DFS and BFS

The lecture lists DFS and BFS as prerequisites.

You do not need advanced versions here.

---

## 4.1 DFS Mental Model

DFS goes deep first.

Graph:

```text
1 -----> 2 -----> 4
|
+-----> 3 -----> 5
```

Possible DFS from `1`:

```text
1
↓
2
↓
4
(back)
1
↓
3
↓
5
```

---

## 4.2 Basic DFS

```cpp
void dfs(int node, const vector<vector<int>>& graph, vector<int>& visited) {
    visited[node] = 1;

    for (int neighbor : graph[node]) {
        if (!visited[neighbor]) {
            dfs(neighbor, graph, visited);
        }
    }
}
```

---

## 4.3 BFS Mental Model

BFS explores level by level.

```text
        1
      /   \
     2     3
    / \     \
   4   5     6
```

BFS from `1`:

```text
1
2 3
4 5 6
```

Typical queue:

```cpp
queue<int> q;
```

Kahn's topological sort will use BFS-style queue processing.

---

# 5. Prerequisite - Indegree and Outdegree

For a directed graph:

```text
u -----> v
```

this edge contributes:

```text
+1 outdegree to u
+1 indegree  to v
```

---

## 5.1 Example

```text
1 -----> 2
|        ^
|        |
v        |
3 ------>4
```

Edges:

```text
1 -> 2
1 -> 3
3 -> 4
4 -> 2
```

Indegree:

```text
indegree[1] = 0
indegree[2] = 2
indegree[3] = 1
indegree[4] = 1
```

Outdegree:

```text
outdegree[1] = 2
outdegree[2] = 0
outdegree[3] = 1
outdegree[4] = 1
```

---

## 5.2 Why Indegree Matters for Topological Sort

If:

```text
indegree[x] = 0
```

then no unfinished prerequisite points into `x`.

So `x` is currently safe to place next in topological order.

This is the key idea of Kahn's algorithm.

---

# 6. Directed Graphs

The lecture defines a directed graph as:

```text
vertices connected by directed edges
```

Each edge has a direction.

Example:

```text
A -----> B
          |
          v
          C -----> D
          ^       |
          |_______|
```

Edges could be:

```text
A -> B
B -> C
C -> D
D -> C
```

Direction changes reachability.

For example:

```text
A can reach D
```

but maybe:

```text
D cannot reach A
```

---

# 7. Directed Acyclic Graph - DAG

A DAG is:

```text
Directed
+
Acyclic
+
Graph
```

Meaning:

```text
directed graph with no directed cycle
```

---

## 7.1 DAG Example

```text
1 -----> 2 -----> 4
 \                 ^
  \               /
   -----> 3 -----
```

Edges:

```text
1 -> 2
1 -> 3
2 -> 4
3 -> 4
```

There is no way to follow arrows and return to the same node.

Therefore:

```text
DAG
```

---

## 7.2 Not a DAG

```text
1 -----> 2
^        |
|        v
4 <----- 3
```

Cycle:

```text
1 -> 2 -> 3 -> 4 -> 1
```

Therefore:

```text
not a DAG
```

---

## 7.3 Why DAGs Matter

The lecture highlights:

```text
DAG
→ allows topological sorting
```

Applications include:

```text
task scheduling
project dependencies
compilation order
package installation
```

---

# 8. Undirected vs Directed Cycle Thinking

This is a common confusion.

---

## 8.1 Undirected Cycle Detection

In undirected graph DFS, if you see a visited neighbor:

```text
neighbor != parent
```

then there is a cycle.

---

## 8.2 Directed Cycle Detection

That logic is not enough.

Consider:

```text
1 -----> 2
 \       |
  \      v
   ----> 3
```

Edges:

```text
1 -> 2
1 -> 3
2 -> 3
```

During DFS:

```text
3 may already be visited
```

but there is no cycle.

So in directed graphs we need a stronger condition:

```text
Was this node visited in the CURRENT active DFS path?
```

That is what:

```text
inStack[]
```

tracks.

---

# 9. Cycle Detection in Directed Graph

The lecture uses two arrays:

```text
visited[]
inStack[]
```

Meaning:

```text
visited[x]
= x has been seen at least once

inStack[x]
= x is currently active in the recursion chain
```

---

## 9.1 Three Useful States

You can think of every node as:

```text
0 = never visited

1 = currently inside DFS recursion stack

2 = DFS fully finished
```

The lecture implements this using two boolean arrays.

Mapping:

```text
visited = false
→ state 0

visited = true and inStack = true
→ state 1

visited = true and inStack = false
→ state 2
```

---

## 9.2 Why `inStack` Detects a Directed Cycle

Suppose current DFS chain is:

```text
1 -> 2 -> 5 -> 7
```

Then:

```text
inStack[1] = true
inStack[2] = true
inStack[5] = true
inStack[7] = true
```

Now from `7` we find:

```text
7 -> 2
```

But `2` is still active.

So:

```text
7 -> 2
```

closes the loop:

```text
2 -> 5 -> 7 -> 2
```

Cycle found.

---

# 10. Cycle Detection Dry Run

Use this graph:

```text
1 -----> 2 -----> 3
          ^       |
          |       v
          +------ 4
```

Edges:

```text
1 -> 2
2 -> 3
3 -> 4
4 -> 2
```

Cycle:

```text
2 -> 3 -> 4 -> 2
```

---

## 10.1 Start DFS(1)

```text
visited[1] = true
inStack[1] = true
```

Active recursion chain:

```text
1
```

---

## 10.2 Go to 2

```text
visited[2] = true
inStack[2] = true
```

Stack:

```text
1 -> 2
```

---

## 10.3 Go to 3

```text
visited[3] = true
inStack[3] = true
```

Stack:

```text
1 -> 2 -> 3
```

---

## 10.4 Go to 4

```text
visited[4] = true
inStack[4] = true
```

Stack:

```text
1 -> 2 -> 3 -> 4
```

---

## 10.5 From 4 See Edge 4 -> 2

Check:

```text
inStack[2] == true
```

Yes.

Therefore:

```text
cycle exists
```

Visual:

```text
2 -----> 3
^        |
|        v
+------- 4
```

---

## 10.6 What Happens in a DAG?

Graph:

```text
1 -> 2 -> 4
 \       ^
  -> 3 --|
```

DFS may reach `4` from `2`.

Later DFS reaches `4` from `3`.

But by then:

```text
visited[4] = true
inStack[4] = false
```

So:

```text
already visited
```

does **not** imply cycle.

Only:

```text
already active in current recursion
```

implies cycle.

---

# 11. Cycle Detection C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

bool hasCycle(int node, const vector<vector<int>>& graph,
              vector<int>& visited, vector<int>& inStack) {
    visited[node] = 1;
    inStack[node] = 1;

    for (int neighbor : graph[node]) {
        if (inStack[neighbor]) {
            return true;
        }

        if (!visited[neighbor] && hasCycle(neighbor, graph, visited, inStack)) {
            return true;
        }
    }

    inStack[node] = 0;
    return false;
}

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
    }

    vector<int> visited(n + 1, 0);
    vector<int> inStack(n + 1, 0);

    bool cycleExists = false;

    for (int node = 1; node <= n; node++) {
        if (!visited[node] && hasCycle(node, graph, visited, inStack)) {
            cycleExists = true;
            break;
        }
    }

    cout << (cycleExists ? "Cycle\n" : "DAG\n");

    return 0;
}
```

---

## 11.1 Complexity

Every node is processed once.

Every directed edge is checked once.

```text
Time  = O(V + E)
Space = O(V)
```

Ignoring adjacency-list storage, extra algorithmic space is:

```text
O(V)
```

plus recursion stack.

---

# 12. Topological Ordering

The lecture definition:

For every directed edge:

```text
u -> v
```

`u` must appear before `v`.

---

## 12.1 Example

Graph:

```text
1 -----> 2
|        |
v        v
3 -----> 4
```

Edges:

```text
1 -> 2
1 -> 3
2 -> 4
3 -> 4
```

Valid topological orders include:

```text
1 2 3 4
1 3 2 4
```

Invalid:

```text
2 1 3 4
```

because:

```text
1 -> 2
```

requires `1` before `2`.

---

## 12.2 Topological Sort Is Not Ordinary Sorting

It does not mean:

```text
ascending numeric order
```

It means:

```text
respect all dependency directions
```

---

# 13. Why Topological Sort Requires a DAG

Suppose:

```text
1 -> 2
2 -> 3
3 -> 1
```

Topological rules require:

```text
1 before 2
2 before 3
3 before 1
```

Combine them:

```text
1 < 2 < 3 < 1
```

Impossible.

Therefore:

```text
directed cycle
→ no valid topological ordering
```

Hence:

```text
topological sort exists only for DAGs
```

The lecture also notes:

```text
every DAG has at least one valid topological order
```

---

# 14. Topo Sort Using DFS

DFS topological sort uses one beautiful idea:

```text
When DFS finishes a node,
all nodes reachable from it have already been processed.
```

Therefore we append the current node:

```text
AFTER all its outgoing dependencies are handled
```

Then reverse the final list.

---

## 14.1 Postorder Intuition

For edge:

```text
u -> v
```

DFS from `u` first explores `v`.

So finish order is:

```text
v before u
```

But topological order needs:

```text
u before v
```

So:

```text
reverse DFS finish order
```

---

## 14.2 Tiny Example

```text
1 -> 2 -> 4
 \       ^
  -> 3 --|
```

Start DFS(1).

DFS may finish:

```text
4
2
3
1
```

That is postorder.

Reverse:

```text
1 3 2 4
```

Valid topo order.

Another DFS-neighbor order may produce:

```text
1 2 3 4
```

Also valid.

---

# 15. DFS Topo Dry Run

Use:

```text
1 -----> 2
|        |
v        v
3 -----> 4
```

Adjacency:

```text
1 -> [2,3]
2 -> [4]
3 -> [4]
4 -> []
```

---

## 15.1 DFS(1)

Active:

```text
1
```

Go to `2`.

---

## 15.2 DFS(2)

Active:

```text
1 -> 2
```

Go to `4`.

---

## 15.3 DFS(4)

No outgoing edge.

Finish `4`.

Append:

```text
answer = [4]
```

Return to `2`.

Finish `2`.

```text
answer = [4,2]
```

---

## 15.4 Back to 1, Visit 3

Go:

```text
1 -> 3
```

From `3`, neighbor `4` already visited.

Finish `3`.

```text
answer = [4,2,3]
```

---

## 15.5 Finish 1

```text
answer = [4,2,3,1]
```

Reverse:

```text
[1,3,2,4]
```

Check all edges:

```text
1 before 2 ✓
1 before 3 ✓
2 before 4 ✓
3 before 4 ✓
```

Valid.

---

# 16. DFS Topo C++17

This code assumes the graph is already known to be a DAG.

```cpp
#include <bits/stdc++.h>
using namespace std;

void dfs(int node, const vector<vector<int>>& graph,
         vector<int>& visited, vector<int>& topo) {
    visited[node] = 1;

    for (int neighbor : graph[node]) {
        if (!visited[neighbor]) {
            dfs(neighbor, graph, visited, topo);
        }
    }

    topo.push_back(node);
}

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
    }

    vector<int> visited(n + 1, 0);
    vector<int> topo;

    for (int node = 1; node <= n; node++) {
        if (!visited[node]) {
            dfs(node, graph, visited, topo);
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

## 16.1 Complexity

```text
DFS     = O(V + E)
reverse = O(V)

Total   = O(V + E)
Space   = O(V)
```

---

# 17. Topo Sort Using BFS - Kahn's Algorithm

Kahn's algorithm uses indegree.

Core observation from the lecture:

```text
Every DAG must have at least one node with indegree 0.
```

Why?

If every node had an incoming edge forever, following prerequisites backward would eventually repeat a node, creating a cycle.

So a DAG always has a starting point.

---

## 17.1 Kahn Core Idea

```text
1. Compute indegree of every node.

2. Put all nodes with indegree 0 into a queue.

3. Remove one node from queue.
   Add it to topo answer.

4. Pretend that node is deleted.
   For every outgoing edge:
       decrease neighbor indegree.

5. If a neighbor becomes indegree 0:
       push it.

6. Repeat.
```

---

## 17.2 Meaning of "Delete a Node"

Suppose:

```text
1 -> 2
1 -> 3
```

Initial:

```text
indegree[1] = 0
indegree[2] = 1
indegree[3] = 1
```

Process `1`.

Conceptually remove:

```text
1 -> 2
1 -> 3
```

Now:

```text
indegree[2] = 0
indegree[3] = 0
```

Both become available.

---

# 18. Kahn Dry Run

Graph:

```text
1 -----> 2
|        |
v        v
3 -----> 4
```

Edges:

```text
1 -> 2
1 -> 3
2 -> 4
3 -> 4
```

---

## 18.1 Compute Indegree

```text
indegree[1] = 0
indegree[2] = 1
indegree[3] = 1
indegree[4] = 2
```

Initial queue:

```text
[1]
```

Topo:

```text
[]
```

---

## 18.2 Process 1

Pop:

```text
1
```

Topo:

```text
[1]
```

Decrease:

```text
indegree[2]: 1 -> 0
indegree[3]: 1 -> 0
```

Queue:

```text
[2,3]
```

---

## 18.3 Process 2

Pop:

```text
2
```

Topo:

```text
[1,2]
```

Decrease:

```text
indegree[4]: 2 -> 1
```

Queue:

```text
[3]
```

---

## 18.4 Process 3

Pop:

```text
3
```

Topo:

```text
[1,2,3]
```

Decrease:

```text
indegree[4]: 1 -> 0
```

Push `4`.

Queue:

```text
[4]
```

---

## 18.5 Process 4

Topo:

```text
[1,2,3,4]
```

Done.

---

# 19. Kahn C++17

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

        for (int neighbor : graph[node]) {
            indegree[neighbor]--;

            if (indegree[neighbor] == 0) {
                q.push(neighbor);
            }
        }
    }

    if ((int)topo.size() != n) {
        cout << "Cycle exists\n";
    } else {
        for (int node : topo) {
            cout << node << ' ';
        }

        cout << '\n';
    }

    return 0;
}
```

---

## 19.1 Complexity

Compute indegrees:

```text
O(E)
```

Queue processing:

```text
O(V + E)
```

Total:

```text
O(V + E)
```

Extra space:

```text
O(V)
```

excluding graph storage.

---

# 20. Cycle Detection Using Kahn

This is one of the lecture's applications.

Suppose graph contains a cycle:

```text
1 -> 2
^    |
|    v
4 <- 3
```

Every node in the cycle has indegree at least 1.

No cycle node can ever become indegree 0.

So Kahn cannot process all nodes.

Therefore:

```text
if topo.size() == V
    graph is DAG

else
    graph contains a directed cycle
```

---

## 20.1 Tiny Example

Cycle:

```text
1 -> 2 -> 3 -> 1
```

Indegree:

```text
1 = 1
2 = 1
3 = 1
```

Initial zero-indegree queue:

```text
empty
```

Kahn processes:

```text
0 nodes
```

But:

```text
V = 3
```

Therefore:

```text
0 != 3
```

Cycle exists.

---

# 21. DFS Topo vs Kahn

| Feature | DFS Topo | Kahn |
|---|---|---|
| Main idea | Reverse finish times | Repeatedly remove indegree-0 nodes |
| Data structure | Recursion / stack | Queue |
| Uses indegree? | No | Yes |
| Easy cycle detection? | Needs `inStack` / color | Yes, by processed count |
| Complexity | `O(V+E)` | `O(V+E)` |
| Lexicographically smallest? | Not naturally | Easy with min-heap |
| Good mental model | Dependencies finish first | Available tasks first |

---

# 22. Lexicographically Smallest Topological Order

The lecture's final problem asks:

```text
Given a DAG,
find the lexicographically smallest topological ordering.
```

Suppose several nodes currently have:

```text
indegree = 0
```

Ordinary Kahn may choose any of them.

For lexicographically smallest answer:

```text
always choose the smallest available node
```

So replace:

```cpp
queue<int>
```

with:

```cpp
priority_queue<int, vector<int>, greater<int>>
```

That is a min-heap.

---

## 22.1 Example

Graph:

```text
1 -----> 2 -----> 4
|        |
v        v
3 -----> 5
```

Suppose at some moment available nodes are:

```text
{2,3}
```

Ordinary queue:

```text
depends on insertion order
```

Min-heap:

```text
chooses 2 first
```

Therefore lexicographically smaller.

---

## 22.2 Detailed Dry Run

Use:

```text
1 -----> 2 -----> 4
|
+-----> 3 -----> 5
```

Indegrees:

```text
1 = 0
2 = 1
3 = 1
4 = 1
5 = 1
```

Heap:

```text
[1]
```

Pop `1`.

Now:

```text
2 and 3 become indegree 0
```

Heap:

```text
[2,3]
```

Pick smallest:

```text
2
```

Then `4` becomes available.

Heap:

```text
[3,4]
```

Pick:

```text
3
```

Then `5` becomes available.

Heap:

```text
[4,5]
```

Pick:

```text
4
```

Then:

```text
5
```

Answer:

```text
1 2 3 4 5
```

---

# 23. Lexicographically Smallest Topo C++17

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

        for (int neighbor : graph[node]) {
            indegree[neighbor]--;

            if (indegree[neighbor] == 0) {
                pq.push(neighbor);
            }
        }
    }

    if ((int)topo.size() != n) {
        cout << "No topological ordering\n";
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

## 23.1 Complexity

Every node may enter the heap once.

Every heap operation costs:

```text
O(log V)
```

Every edge is processed once.

So:

```text
O((V + E) log V)
```

A more precise common bound:

```text
O(V log V + E)
```

if only the node insert/pop operations use the heap.

For competitive programming, remembering:

```text
about O((V+E) log V)
```

is safe.

---

# 24. Applications

The lecture lists several applications.

---

## 24.1 Check Whether Directed Graph Is a DAG

Use either:

```text
DFS + recursion stack
```

or:

```text
Kahn
```

Kahn test:

```text
processed nodes == V
```

---

## 24.2 Task Scheduling

Dependencies:

```text
Design -----> Code -----> Test -----> Deploy
```

A valid execution order must respect arrows.

That is a topological order.

---

## 24.3 Compilation Order

Example:

```text
A.cpp depends on B.hpp
B.hpp depends on C.hpp
```

Conceptually:

```text
C before B before A
```

Dependency graphs are naturally directed.

---

## 24.4 Package Installation

Example:

```text
App
 |
 v
Library A
 |
 v
Library B
```

You must install prerequisites first.

Again:

```text
topological ordering
```

---

# 25. Common Mistakes

## 25.1 Adding Reverse Edge Accidentally

For:

```text
u -> v
```

wrong:

```cpp
graph[u].push_back(v);
graph[v].push_back(u);
```

Correct:

```cpp
graph[u].push_back(v);
```

unless the reverse directed edge also exists.

---

## 25.2 Directed Cycle: Checking Only `visited`

Wrong:

```text
neighbor already visited
→ cycle
```

A visited node may simply belong to an already finished DFS branch.

Correct:

```text
neighbor currently in recursion stack
→ cycle
```

---

## 25.3 Forgetting `inStack[node] = 0`

When DFS finishes a node:

```text
remove it from active recursion path
```

So:

```cpp
inStack[node] = 0;
```

must happen before returning false.

---

## 25.4 Running DFS Topo on a Cyclic Graph Without Checking

DFS finish order can still produce some vector.

But it is not a valid topological order if a cycle exists.

So either:

```text
problem guarantees DAG
```

or:

```text
detect cycle
```

---

## 25.5 Kahn: Forgetting All Initial Zero-Indegree Nodes

Do not push only one.

Push every node satisfying:

```text
indegree[node] == 0
```

---

## 25.6 Kahn: Decrementing Wrong Indegree

For edge:

```text
u -> v
```

when `u` is processed:

```text
indegree[v]--
```

not:

```text
indegree[u]--
```

---

## 25.7 Thinking Topological Order Is Unique

A DAG can have many valid topological orders.

Example:

```text
1 -> 3
2 -> 3
```

Both are valid:

```text
1 2 3
2 1 3
```

If the problem asks for:

```text
lexicographically smallest
```

use min-heap Kahn.

---

# 26. Final Recognition Model

## 26.1 Directed Cycle Pattern

If the problem asks:

```text
Does this dependency graph contain a cycle?
```

think:

```text
DFS:
visited + inStack

or

Kahn:
processed count < V
```

---

## 26.2 Topological Sort Pattern

If the problem says:

```text
A must happen before B
B before C
find a valid order
```

think:

```text
DAG
+
topological sort
```

---

## 26.3 DFS Topo Mental Model

```text
visit children first
      ↓
finish node
      ↓
push node
      ↓
reverse answer
```

---

## 26.4 Kahn Mental Model

```text
compute indegree
      ↓
push all indegree-0 nodes
      ↓
take available node
      ↓
remove its outgoing edges
      ↓
new indegree-0 nodes appear
      ↓
repeat
```

---

## 26.5 Lexicographically Smallest Mental Model

```text
Kahn
+
min-heap instead of normal queue
```

---

# 27. Practice Checklist

Before moving on, make sure you can answer:

```text
[ ] What is the difference between undirected and directed edges?

[ ] How is a directed graph stored in an adjacency list?

[ ] What is indegree?

[ ] What is outdegree?

[ ] What is a DAG?

[ ] Why does a directed cycle prevent topological ordering?

[ ] Why is visited[] alone insufficient for directed cycle detection?

[ ] What exactly does inStack[] mean?

[ ] Why does DFS topo push a node after its children?

[ ] Why do we reverse DFS finishing order?

[ ] Why must a DAG contain at least one zero-indegree node?

[ ] How does Kahn's algorithm simulate deleting a node?

[ ] How can Kahn detect a cycle?

[ ] Why can a DAG have more than one topological order?

[ ] How do we get the lexicographically smallest topo order?
```

If these are clear, your Directed Graph + Topological Sort foundation is ready for advanced graph and tree work.

---

# One-Line Revision

```text
Directed graph:
edges have direction.

DAG:
directed graph with no directed cycle.

Cycle detection:
visited + recursion stack.

DFS topo:
postorder + reverse.

Kahn:
indegree + queue.

Lexicographically smallest topo:
Kahn + min-heap.
```
