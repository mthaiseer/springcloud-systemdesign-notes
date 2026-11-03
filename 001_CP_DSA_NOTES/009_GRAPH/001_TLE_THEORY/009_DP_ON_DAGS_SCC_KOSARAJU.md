# DP on DAGs + Strongly Connected Components (SCC)
## TLE-Style Visual Self-Study Notes
### Prerequisites → DP on DAG → SCC → Kosaraju → Condensation Graph → Coin Collector

> **Lecture basis:** TLE Graphs Advanced — *DP on DAGs / Strongly Connected Components*.
>
> **Teaching expansion added in this note:** prerequisite recap, cleaner graph drawings, step-by-step proofs/intuition, dry runs, and readable C++17.
>
> Main goal:
>
> ```text
> Directed graph with cycles
>         ↓
> find SCCs
>         ↓
> compress each SCC into one node
>         ↓
> condensation graph = DAG
>         ↓
> run DP on DAG
> ```

---

# Table of Contents

1. [What You Need Before This Topic](#1-what-you-need-before-this-topic)
2. [Prerequisite — Directed Graph](#2-prerequisite--directed-graph)
3. [Prerequisite — DAG](#3-prerequisite--dag)
4. [Prerequisite — Topological Order](#4-prerequisite--topological-order)
5. [Prerequisite — DFS Finish Time](#5-prerequisite--dfs-finish-time)
6. [Prerequisite — Graph Transpose](#6-prerequisite--graph-transpose)
7. [DP on DAGs — Big Idea](#7-dp-on-dags--big-idea)
8. [Why DP on DAG Needs an Order](#8-why-dp-on-dag-needs-an-order)
9. [DP State — Maximum Path Score from Each Node](#9-dp-state--maximum-path-score-from-each-node)
10. [DP on DAG — Recursive Form](#10-dp-on-dag--recursive-form)
11. [DP on DAG — Iterative Topological Form](#11-dp-on-dag--iterative-topological-form)
12. [Longest / Heaviest Path in a DAG](#12-longest--heaviest-path-in-a-dag)
13. [Strongly Connected Component — SCC](#13-strongly-connected-component--scc)
14. [SCC Visual Intuition](#14-scc-visual-intuition)
15. [Why SCC Is a Directed-Graph Concept](#15-why-scc-is-a-directed-graph-concept)
16. [Condensation Graph](#16-condensation-graph)
17. [Why the Condensation Graph Is Always a DAG](#17-why-the-condensation-graph-is-always-a-dag)
18. [Why SCCs Do Not Change After Transpose](#18-why-sccs-do-not-change-after-transpose)
19. [Finish-Time Property Between SCCs](#19-finish-time-property-between-sccs)
20. [Kosaraju’s Algorithm — Core Idea](#20-kosarajus-algorithm--core-idea)
21. [Kosaraju — Step-by-Step Dry Run](#21-kosaraju--step-by-step-dry-run)
22. [Kosaraju — C++17](#22-kosaraju--c17)
23. [Build the Condensation DAG](#23-build-the-condensation-dag)
24. [Problem — Coin Collector](#24-problem--coin-collector)
25. [Coin Collector — Story to Graph Model](#25-coin-collector--story-to-graph-model)
26. [Coin Collector — SCC Compression](#26-coin-collector--scc-compression)
27. [Coin Collector — DP on Condensation DAG](#27-coin-collector--dp-on-condensation-dag)
28. [Coin Collector — Complete C++17](#28-coin-collector--complete-c17)
29. [Complexities](#29-complexities)
30. [Common Mistakes](#30-common-mistakes)
31. [Final Recognition Model](#31-final-recognition-model)
32. [Practice Checklist](#32-practice-checklist)

---

# 1. What You Need Before This Topic

The lecture builds on these ideas:

```text
Directed graphs
DFS
DAG
Topological sorting
DFS finishing order
```

You should already know:

```text
u -> v
```

means a **directed edge** from `u` to `v`.

You should also know:

```text
DAG = Directed Acyclic Graph
```

and that a DAG allows topological ordering.

This chapter adds three powerful ideas:

```text
1. DP on DAG
2. Strongly Connected Components
3. Condensation of a directed graph
```

---

# 2. Prerequisite — Directed Graph

Example:

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

Direction matters.

From `1` we can reach `4`.

But there may be no path from `4` back to `1`.

---

## 2.1 Directed Reachability

We say:

```text
u reaches v
```

if there exists some directed path:

```text
u -> ... -> v
```

Example:

```text
1 -> 2 -> 5 -> 7
```

Then:

```text
1 reaches 7
```

But unless reverse paths exist:

```text
7 may not reach 1
```

This two-way reachability is the heart of SCC.

---

# 3. Prerequisite — DAG

A DAG has:

```text
directed edges
+
no directed cycle
```

Valid DAG:

```text
1 -----> 2 -----> 4
 \                 ^
  \               /
   -----> 3 -----
```

Not a DAG:

```text
1 -----> 2
^        |
|        v
4 <----- 3
```

because:

```text
1 -> 2 -> 3 -> 4 -> 1
```

is a cycle.

---

# 4. Prerequisite — Topological Order

For every edge:

```text
u -> v
```

a topological ordering places:

```text
u before v
```

Example:

```text
1 -----> 2
|        |
v        v
3 -----> 4
```

Possible topo orders:

```text
1 2 3 4
1 3 2 4
```

Why useful?

Because dependency answers become available in a controlled order.

That is exactly why topological order helps DP.

---

# 5. Prerequisite — DFS Finish Time

This idea becomes crucial for Kosaraju.

Consider DFS:

```text
1 -> 2 -> 4
 \       ^
  -> 3 --|
```

A possible DFS flow:

```text
dfs(1)
  dfs(2)
    dfs(4)
    finish 4
  finish 2

  dfs(3)
  finish 3

finish 1
```

Finish order:

```text
4, 2, 3, 1
```

If we push a node when DFS finishes:

```text
stack = [4, 2, 3, 1]
```

The latest finishing node is:

```text
1
```

The lecture uses this finish-time ordering in Kosaraju.

---

# 6. Prerequisite — Graph Transpose

Transpose means:

```text
reverse every directed edge
```

Original:

```text
1 -----> 2
2 -----> 3
1 -----> 4
```

Transpose:

```text
1 <----- 2
2 <----- 3
1 <----- 4
```

Equivalent edge list:

```text
Original       Transpose

1 -> 2         2 -> 1
2 -> 3         3 -> 2
1 -> 4         4 -> 1
```

C++:

```cpp
for (int u = 1; u <= n; u++) {
    for (int v : graph[u]) {
        reverseGraph[v].push_back(u);
    }
}
```

---

# 7. DP on DAGs — Big Idea

The lecture defines DP on DAG as computing DP values in a valid dependency order.

Why does a DAG help?

Because:

```text
no cycles
```

means dependencies do not loop forever.

Typical uses:

```text
longest path
counting paths
maximum score
minimum cost
other optimization problems
```

---

## 7.1 Tree DP vs DAG DP

In a rooted tree:

```text
        u
      /   \
     c1   c2
```

we naturally know:

```text
children first
parent later
```

For example:

```text
dp[u] = a[u] + max(dp[c1], dp[c2])
```

But a DAG has:

```text
no single natural root
```

and one node may have multiple parents.

Example:

```text
1 -----> 4
 \       ^
  \     /
   -> 2
```

So we need a systematic dependency order.

That order is:

```text
topological order
```

---

# 8. Why DP on DAG Needs an Order

Suppose:

```text
u -> c1
u -> c2
u -> c3
```

and:

```text
dp[u] = a[u] + max(dp[c1], dp[c2], dp[c3])
```

To compute `dp[u]`, the child answers must already exist.

So:

```text
dp[c1]
dp[c2]
dp[c3]
```

must be known first.

This gives two standard methods:

```text
Method 1:
DFS + memoization

Method 2:
topological order + iterative transitions
```

---

# 9. DP State — Maximum Path Score from Each Node

The lecture uses the model:

```text
Each node u has value a[u].

Find the maximum score of any path
starting from u.
```

Define:

```text
dp[u] =
maximum sum of node values on any path starting from u
```

If outgoing neighbors are:

```text
c1, c2, c3
```

then:

```text
dp[u] = a[u] + max(dp[c1], dp[c2], dp[c3])
```

If `u` has no outgoing edge:

```text
dp[u] = a[u]
```

---

## 9.1 Visual Example

```text
          (1)[5]
          /    \
         v      v
      (2)[2]   (3)[4]
         \      /
          v    v
          (4)[7]
```

Node values:

```text
a[1] = 5
a[2] = 2
a[3] = 4
a[4] = 7
```

Compute:

```text
dp[4] = 7
dp[2] = 2 + 7 = 9
dp[3] = 4 + 7 = 11
dp[1] = 5 + max(9,11)
      = 16
```

So best path from `1`:

```text
1 -> 3 -> 4

score = 5 + 4 + 7
      = 16
```

---

# 10. DP on DAG — Recursive Form

Recursive DFS naturally computes children first.

```cpp
long long dfs(int node, const vector<vector<int>>& graph,
              const vector<long long>& value, vector<long long>& dp) {
    if (dp[node] != -1) {
        return dp[node];
    }

    long long bestChild = 0;

    for (int neighbor : graph[node]) {
        bestChild = max(bestChild, dfs(neighbor, graph, value, dp));
    }

    dp[node] = value[node] + bestChild;
    return dp[node];
}
```

This requires:

```text
graph must be DAG
```

Otherwise recursion can loop through a cycle.

---

# 11. DP on DAG — Iterative Topological Form

Suppose topo order is:

```text
1 2 3 4
```

and edges go forward.

If we define:

```text
dp[u] = best path starting from u
```

then we often process the topo order **backwards**:

```text
4, 3, 2, 1
```

because outgoing neighbors should be known first.

Example:

```cpp
for (int i = n - 1; i >= 0; i--) {
    int node = topo[i];

    dp[node] = value[node];

    for (int neighbor : graph[node]) {
        dp[node] = max(dp[node], value[node] + dp[neighbor]);
    }
}
```

---

# 12. Longest / Heaviest Path in a DAG

The lecture also gives:

```text
Find the heaviest path from a given source
in a weighted DAG.
```

Path weight:

```text
sum of edge weights
```

Suppose:

```text
1 --3--> 2 --5--> 4
 \                    ^
  \--4--> 3 --2------/
```

Possible paths:

```text
1 -> 2 -> 4 = 3 + 5 = 8

1 -> 3 -> 4 = 4 + 2 = 6
```

Answer:

```text
8
```

---

## 12.1 Transition

If:

```text
u -> v with weight w
```

and `dp[u]` is best score to reach `u`:

```text
dp[v] = max(dp[v], dp[u] + w)
```

Process nodes in topological order.

---

## 12.2 C++ Skeleton

```cpp
const long long NEG = -(1LL << 60);

vector<long long> dp(n + 1, NEG);
dp[source] = 0;

for (int node : topo) {
    if (dp[node] == NEG) {
        continue;
    }

    for (auto [neighbor, weight] : graph[node]) {
        dp[neighbor] = max(dp[neighbor], dp[node] + weight);
    }
}
```

---

# 13. Strongly Connected Component — SCC

Lecture definition:

A strongly connected component is a **maximal set of nodes** such that for every pair:

```text
u, v
```

there is a path:

```text
u -> ... -> v
```

and also:

```text
v -> ... -> u
```

So inside one SCC:

```text
every node can reach every other node
```

---

# 14. SCC Visual Intuition

Consider:

```text
     +-------------------+
     |                   |
     v                   |
    (1) -----> (2)       |
     ^          |         |
     |          v         |
    (4) <----- (3) -------+
```

This group is strongly connected:

```text
{1,2,3,4}
```

because every node can eventually reach every other node.

Now suppose:

```text
(2) -----> (5) <----> (9)
            |
            v
           (6)
          /   \
         v     v
        (7) <-> (8)
```

Possible SCC grouping:

```text
SCC A = {1,2,3,4}

SCC B = {5,9}

SCC C = {6,7,8}
```

The important idea is not the drawing itself.

It is:

```text
inside one SCC
→ two-way reachability exists
```

---

# 15. Why SCC Is a Directed-Graph Concept

For undirected graph:

```text
u ----- v
```

if `u` can reach `v`, then `v` automatically reaches `u`.

So we simply use:

```text
connected component
```

No separate "strongly connected" concept is needed.

In directed graphs:

```text
u -----> v
```

one-way reachability does not imply reverse reachability.

So SCC becomes meaningful.

---

# 16. Condensation Graph

Condensation means:

```text
compress every SCC into one super-node
```

Example original graph:

```text
SCC A                     SCC B
+-----------+              +-------+
| 1 -> 2    | -----------> | 5 <->9|
| ^    |    |              +-------+
| |    v    |                  |
| 4 <- 3    |                  v
+-----------+              +-----------+
                           | SCC C     |
                           | 6 -> 7    |
                           | ^    |    |
                           | |    v    |
                           | +--- 8    |
                           +-----------+
```

After compression:

```text
[A] -----> [B] -----> [C]
```

This compressed graph is called:

```text
condensation graph
```

---

# 17. Why the Condensation Graph Is Always a DAG

Suppose condensation had a cycle:

```text
A -> B -> C -> A
```

Because every SCC internally has full two-way reachability, and these SCCs reach each other around the cycle:

```text
A can reach B
B can reach C
C can reach A
```

Then all nodes in:

```text
A, B, C
```

would actually be mutually reachable.

Therefore they should have been:

```text
one larger SCC
```

Contradiction.

Hence condensation cannot have a cycle.

So:

```text
Condensation graph is always a DAG.
```

This fact is extremely important.

It lets us transform:

```text
hard cyclic directed graph
```

into:

```text
DAG of SCCs
```

and then use:

```text
topological sort
DP
```

---

# 18. Why SCCs Do Not Change After Transpose

The lecture explicitly notes:

```text
SCCs remain the same after reversing every edge.
```

Why?

Suppose inside one SCC:

```text
u can reach v
v can reach u
```

After reversing every edge:

```text
the old path u -> ... -> v
becomes a path v -> ... -> u
```

and:

```text
the old path v -> ... -> u
becomes a path u -> ... -> v
```

So two-way reachability remains true.

Therefore:

```text
same SCC partition
```

after transpose.

---

# 19. Finish-Time Property Between SCCs

The lecture gives an important relation:

If condensation has:

```text
SCC A -----> SCC B
```

then the first DFS pass produces a finish-time ordering that allows us to process SCCs in the correct order for Kosaraju.

High-level intuition:

```text
source-like SCCs in the condensation DAG
tend to finish later
```

because DFS can flow from them into downstream SCCs.

After transpose:

```text
all condensation edges reverse
```

so a source SCC becomes sink-like.

That prevents the second DFS from escaping into another unprocessed SCC.

This is the key structural reason Kosaraju works.

---

# 20. Kosaraju’s Algorithm — Core Idea

The lecture presents three steps.

```text
STEP 1
Run DFS on original graph.
Push each node after its DFS finishes.

STEP 2
Transpose the graph.
Reverse every edge.

STEP 3
Process nodes in decreasing finish time.
Run DFS on the transpose.
Each DFS discovers exactly one SCC.
```

Complexity:

```text
O(V + E)
```

---

## 20.1 Mental Model

```text
Original graph
    ↓
find finish order
    ↓
transpose edges
    ↓
take latest-finishing unvisited node
    ↓
DFS cannot leak into another SCC
    ↓
one DFS = one SCC
```

---

# 21. Kosaraju — Step-by-Step Dry Run

Use this cleaner example:

```text
SCC A               SCC B            SCC C

(1) ----> (2)       (4) <----> (5)   (6) <----> (7)
 ^         |          ^                        ^
 |         v          |                        |
 +------- (3) --------+                        |
                         \--------------------> |
```

More explicitly, edges:

```text
1 -> 2
2 -> 3
3 -> 1          // SCC A = {1,2,3}

3 -> 4

4 -> 5
5 -> 4          // SCC B = {4,5}

5 -> 6

6 -> 7
7 -> 6          // SCC C = {6,7}
```

Condensation:

```text
[A] -----> [B] -----> [C]
```

---

## 21.1 First DFS on Original Graph

Start from `1`.

DFS can travel:

```text
A
↓
B
↓
C
```

Inside `C`, nodes finish first.

A possible finish sequence:

```text
7
6
5
4
3
2
1
```

So stack top becomes:

```text
1
```

Think:

```text
latest finish
→ source-side SCC of condensation
```

---

## 21.2 Transpose the Graph

Condensation direction becomes:

```text
[A] <----- [B] <----- [C]
```

Internal SCCs remain SCCs.

---

## 21.3 Process Stack Top: Node 1

Run DFS from `1` on transpose.

Because `A` is now sink-like in condensation:

```text
DFS cannot escape from A to B
```

It visits exactly:

```text
{1,2,3}
```

So:

```text
SCC 1 = {1,2,3}
```

---

## 21.4 Next Unvisited Node

Next relevant stack node belongs to `B`.

DFS on transpose visits:

```text
{4,5}
```

So:

```text
SCC 2 = {4,5}
```

---

## 21.5 Final SCC

Next:

```text
{6,7}
```

So:

```text
SCC 3 = {6,7}
```

Done.

---

# 22. Kosaraju — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

void dfsOrder(int node, const vector<vector<int>>& graph,
              vector<int>& visited, vector<int>& order) {
    visited[node] = 1;

    for (int neighbor : graph[node]) {
        if (!visited[neighbor]) {
            dfsOrder(neighbor, graph, visited, order);
        }
    }

    order.push_back(node);
}

void dfsComponent(int node, const vector<vector<int>>& reverseGraph,
                  vector<int>& componentId, int component) {
    componentId[node] = component;

    for (int neighbor : reverseGraph[node]) {
        if (componentId[neighbor] == -1) {
            dfsComponent(neighbor, reverseGraph, componentId, component);
        }
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<vector<int>> graph(n + 1);
    vector<vector<int>> reverseGraph(n + 1);

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;

        graph[u].push_back(v);
        reverseGraph[v].push_back(u);
    }

    vector<int> visited(n + 1, 0);
    vector<int> order;

    for (int node = 1; node <= n; node++) {
        if (!visited[node]) {
            dfsOrder(node, graph, visited, order);
        }
    }

    reverse(order.begin(), order.end());

    vector<int> componentId(n + 1, -1);
    int componentCount = 0;

    for (int node : order) {
        if (componentId[node] == -1) {
            dfsComponent(node, reverseGraph, componentId, componentCount);
            componentCount++;
        }
    }

    cout << "SCC count = " << componentCount << '\n';

    for (int node = 1; node <= n; node++) {
        cout << "node " << node
             << " -> component " << componentId[node] << '\n';
    }

    return 0;
}
```

---

## 22.1 Why `reverse(order)`?

First DFS stores:

```text
nodes in increasing finish order
```

because each node is pushed when it finishes.

Kosaraju needs:

```text
decreasing finish order
```

So:

```cpp
reverse(order.begin(), order.end());
```

---

# 23. Build the Condensation DAG

After SCC IDs are known:

```text
componentId[u]
```

for every original edge:

```text
u -> v
```

if:

```text
componentId[u] != componentId[v]
```

then condensation has:

```text
componentId[u] -> componentId[v]
```

---

## 23.1 Example

Original:

```text
1 -> 2 -> 3 -> 1
3 -> 4
4 -> 5 -> 4
5 -> 6
```

Components:

```text
A = {1,2,3}
B = {4,5}
C = {6}
```

Condensation:

```text
[A] -> [B] -> [C]
```

---

## 23.2 C++ Construction

```cpp
vector<vector<int>> dag(componentCount);

for (int u = 1; u <= n; u++) {
    for (int v : graph[u]) {
        int cu = componentId[u];
        int cv = componentId[v];

        if (cu != cv) {
            dag[cu].push_back(cv);
        }
    }
}
```

Duplicate edges may appear.

For many DP problems this is harmless.

If needed, deduplicate them.

---

# 24. Problem — Coin Collector

Lecture problem:

```text
CSES — Coin Collector
```

Problem idea:

```text
n rooms
m directed tunnels

Each room has some coins.

Choose any starting room.
Choose any ending room.

Move only along directed tunnels.

Find maximum coins collectable.
```

Important challenge:

```text
graph may contain cycles
```

So normal DAG DP cannot be applied directly.

---

# 25. Coin Collector — Story to Graph Model

Translate story:

```text
room       -> node
tunnel     -> directed edge
coins      -> node value
movement   -> directed path
```

Goal:

```text
maximum node-value sum over any walk/path
```

But cycles create a problem.

Example:

```text
1 -> 2 -> 3
^         |
|         v
+---------+
```

You can move around this SCC and collect all its room coins.

So the important unit is not each individual node.

It is:

```text
entire SCC
```

---

# 26. Coin Collector — SCC Compression

Suppose:

```text
SCC A = {1,2,3}
coins = 5 + 7 + 3
      = 15

SCC B = {4,5}
coins = 10 + 8
      = 18

SCC C = {6}
coins = 10
```

Compress:

```text
[A:15] -----> [B:18]
   |
   v
[C:10]
```

Now:

```text
condensation graph is a DAG
```

So the original cyclic problem becomes:

```text
maximum path sum on DAG
```

Exactly the DP idea from the first half of the lecture.

---

# 27. Coin Collector — DP on Condensation DAG

Define:

```text
componentCoins[c]
=
sum of coins of all original nodes in SCC c
```

Define:

```text
dp[c]
=
maximum coins collectable
on a path ending at component c
```

If:

```text
u -> v
```

in condensation DAG:

```text
dp[v] = max(dp[v], dp[u] + componentCoins[v])
```

Initialization:

```text
dp[c] = componentCoins[c]
```

because we may start at any SCC.

Answer:

```text
max(dp[c])
```

---

## 27.1 Dry Run

DAG:

```text
       [A:15]
       /    \
      v      v
   [B:18]  [C:10]
      |
      v
   [D:7]
```

Initialize:

```text
dp[A] = 15
dp[B] = 18
dp[C] = 10
dp[D] = 7
```

Process topologically.

From `A`:

```text
dp[B] = max(18, 15 + 18)
      = 33

dp[C] = max(10, 15 + 10)
      = 25
```

From `B`:

```text
dp[D] = max(7, 33 + 7)
      = 40
```

Answer:

```text
40
```

Best route:

```text
A -> B -> D
```

---

# 28. Coin Collector — Complete C++17

Problem:
`https://cses.fi/problemset/task/1686`

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

void dfsOrder(int node, const vector<vector<int>>& graph,
              vector<int>& visited, vector<int>& order) {
    visited[node] = 1;

    for (int neighbor : graph[node]) {
        if (!visited[neighbor]) {
            dfsOrder(neighbor, graph, visited, order);
        }
    }

    order.push_back(node);
}

void dfsComponent(int node, const vector<vector<int>>& reverseGraph,
                  vector<int>& componentId, int component) {
    componentId[node] = component;

    for (int neighbor : reverseGraph[node]) {
        if (componentId[neighbor] == -1) {
            dfsComponent(neighbor, reverseGraph, componentId, component);
        }
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<ll> coins(n + 1);

    for (int node = 1; node <= n; node++) {
        cin >> coins[node];
    }

    vector<vector<int>> graph(n + 1);
    vector<vector<int>> reverseGraph(n + 1);

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;

        graph[u].push_back(v);
        reverseGraph[v].push_back(u);
    }

    vector<int> visited(n + 1, 0);
    vector<int> order;

    for (int node = 1; node <= n; node++) {
        if (!visited[node]) {
            dfsOrder(node, graph, visited, order);
        }
    }

    reverse(order.begin(), order.end());

    vector<int> componentId(n + 1, -1);
    int componentCount = 0;

    for (int node : order) {
        if (componentId[node] == -1) {
            dfsComponent(node, reverseGraph, componentId, componentCount);
            componentCount++;
        }
    }

    vector<ll> componentCoins(componentCount, 0);

    for (int node = 1; node <= n; node++) {
        componentCoins[componentId[node]] += coins[node];
    }

    vector<vector<int>> dag(componentCount);
    vector<int> indegree(componentCount, 0);

    for (int u = 1; u <= n; u++) {
        for (int v : graph[u]) {
            int cu = componentId[u];
            int cv = componentId[v];

            if (cu != cv) {
                dag[cu].push_back(cv);
            }
        }
    }

    for (int node = 0; node < componentCount; node++) {
        sort(dag[node].begin(), dag[node].end());
        dag[node].erase(unique(dag[node].begin(), dag[node].end()), dag[node].end());

        for (int neighbor : dag[node]) {
            indegree[neighbor]++;
        }
    }

    queue<int> q;

    for (int component = 0; component < componentCount; component++) {
        if (indegree[component] == 0) {
            q.push(component);
        }
    }

    vector<ll> dp = componentCoins;
    ll answer = 0;

    while (!q.empty()) {
        int component = q.front();
        q.pop();

        answer = max(answer, dp[component]);

        for (int neighbor : dag[component]) {
            dp[neighbor] = max(
                dp[neighbor],
                dp[component] + componentCoins[neighbor]
            );

            indegree[neighbor]--;

            if (indegree[neighbor] == 0) {
                q.push(neighbor);
            }
        }
    }

    cout << answer << '\n';

    return 0;
}
```

---

# 29. Complexities

## 29.1 DP on DAG

Topo + transitions:

```text
O(V + E)
```

---

## 29.2 Kosaraju

First DFS:

```text
O(V + E)
```

Transpose construction:

```text
O(V + E)
```

Second DFS:

```text
O(V + E)
```

Total:

```text
O(V + E)
```

---

## 29.3 Condensation

Scanning original edges:

```text
O(E)
```

If sorting each adjacency list to remove duplicates, additional sorting cost appears.

---

## 29.4 Coin Collector

Core SCC + DAG DP:

```text
approximately O(V + E)
```

with extra logarithmic sorting cost if duplicate condensation edges are removed using sorting.

---

# 30. Common Mistakes

## 30.1 Trying DAG DP Directly on a Cyclic Graph

Wrong:

```text
directed graph
→ immediately topo sort
```

If cycles exist:

```text
topological order does not exist
```

Correct pattern:

```text
find SCCs
→ condense
→ DAG
→ DP
```

---

## 30.2 Confusing Connected Component and SCC

Undirected:

```text
connected component
```

Directed:

```text
strongly connected component
```

For SCC:

```text
u reaches v
AND
v reaches u
```

---

## 30.3 Forgetting Push-on-Exit in First Kosaraju DFS

Wrong:

```cpp
order.push_back(node);
dfs(children);
```

Correct:

```cpp
dfs(children);
order.push_back(node);
```

because we need:

```text
finish time
```

---

## 30.4 Forgetting to Transpose

Kosaraju requires:

```text
first DFS on original
second DFS on transposed graph
```

The second DFS is not run on the original graph.

---

## 30.5 Using the Wrong Processing Order

First DFS stores finish order.

Second phase needs:

```text
decreasing finish time
```

So if using vector push-on-exit:

```cpp
reverse(order.begin(), order.end());
```

---

## 30.6 Adding Edges Inside the Same SCC to Condensation

For:

```text
componentId[u] == componentId[v]
```

the edge stays internal to one compressed node.

Do not add a self-edge to condensation.

---

## 30.7 Forgetting to Sum SCC Values

In Coin Collector:

```text
component value
=
sum of all room coins in that SCC
```

Not maximum.

Not minimum.

Sum.

---

## 30.8 Using `int` for Large DP Sums

For path sums / coins:

```cpp
long long
```

is safer.

---

# 31. Final Recognition Model

## 31.1 DP on DAG

If problem says:

```text
directed acyclic graph
+
maximum / minimum / count paths
```

think:

```text
topological order
+
DP
```

---

## 31.2 SCC

If problem says:

```text
directed graph
+
mutual reachability
```

think:

```text
SCC
```

---

## 31.3 Condensation

If graph contains complicated cycles but the final task resembles DAG DP:

```text
SCC
→ compress each SCC
→ condensation DAG
→ DP
```

---

## 31.4 Kosaraju Mental Model

```text
DFS original
   ↓
record finish order
   ↓
transpose
   ↓
process decreasing finish order
   ↓
one DFS = one SCC
```

---

## 31.5 Coin Collector Mental Model

```text
cycles allow collecting an entire SCC
            ↓
sum coins inside each SCC
            ↓
compress SCCs
            ↓
DAG
            ↓
maximum path-sum DP
```

---

# 32. Practice Checklist

Before moving forward, verify that you can explain:

```text
[ ] What is a DAG?

[ ] Why can we run DP efficiently on a DAG?

[ ] Why does topological order help DP?

[ ] What does dp[u] represent in a longest-path style problem?

[ ] What is an SCC?

[ ] Why must reachability work both ways inside an SCC?

[ ] Why is SCC mainly a directed-graph concept?

[ ] What is a transpose graph?

[ ] Why do SCCs stay unchanged after transpose?

[ ] What is DFS finish time?

[ ] What are the 3 steps of Kosaraju?

[ ] Why does the second DFS find exactly one SCC?

[ ] What is a condensation graph?

[ ] Why must condensation be a DAG?

[ ] How do we build condensation edges?

[ ] Why does Coin Collector first compress SCCs?

[ ] Why do we sum all coins inside each SCC?

[ ] How does DAG DP solve the compressed Coin Collector problem?
```

If these are clear, the central idea of this lecture is complete:

```text
GENERAL DIRECTED GRAPH
        ↓
       SCC
        ↓
CONDENSATION DAG
        ↓
      DP
```
