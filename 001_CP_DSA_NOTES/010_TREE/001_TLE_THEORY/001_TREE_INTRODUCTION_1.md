# 001 — Tree Introduction 1
## Beginner → Competitive Programming Foundation

> Goal: understand what a tree is, learn the important rooted-tree words, and write a clean DFS traversal without needing to rewatch the full lecture.
>
> Source basis: **TLE — Introduction to Trees 1**. Small teaching examples and cleaner ASCII diagrams are added only to make the lecture easier to self-study.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [What Is a Tree?](#2-what-is-a-tree)
3. [Tree vs General Graph](#3-tree-vs-general-graph)
4. [Neighbour and Degree](#4-neighbour-and-degree)
5. [Leaf and Non-Leaf Nodes](#5-leaf-and-non-leaf-nodes)
6. [Diameter of a Tree](#6-diameter-of-a-tree)
7. [Core Tree Properties](#7-core-tree-properties)
8. [Rooted vs Unrooted Tree](#8-rooted-vs-unrooted-tree)
9. [Important Terms in a Rooted Tree](#9-important-terms-in-a-rooted-tree)
10. [Three Possible Shapes of a Path](#10-three-possible-shapes-of-a-path)
11. [Tree Is Bipartite](#11-tree-is-bipartite)
12. [Tree Traversal](#12-tree-traversal)
13. [DFS on a Tree](#13-dfs-on-a-tree)
14. [Why Parent Is Enough Instead of visited[]](#14-why-parent-is-enough-instead-of-visited)
15. [Clean C++17 DFS Template](#15-clean-c17-dfs-template)
16. [Complexity](#16-complexity)
17. [Recognition Model](#17-recognition-model)
18. [Final Mental Model](#18-final-mental-model)

---

# 1. Prerequisites

Before trees, only a few graph ideas are needed.

## 1.1 Node / Vertex

A **node** represents one object.

Examples:

- person
- city
- computer
- country

In drawings, we use a circle.

```text
    (1)
```

---

## 1.2 Edge

An **edge** connects two nodes.

```text
(1) -------- (2)
```

This means node `1` and node `2` are directly connected.

For the trees in this lecture, edges are **undirected**.

So:

```text
1 -- 2
```

means we can move both:

```text
1 -> 2
2 -> 1
```

---

## 1.3 Graph

A graph is simply:

```text
nodes + edges
```

Example:

```text
      (2)
     /   \
   (1)---(3)

        (4)---(5)
```

There are two separate groups.

So this graph is **disconnected**.

---

## 1.4 Connected Graph

A graph is **connected** if every node can reach every other node through some path.

Example:

```text
(1)---(2)---(3)---(4)
```

From `1`, we can reach `4`:

```text
1 -> 2 -> 3 -> 4
```

So the graph is connected.

---

## 1.5 Path

A **path** is a sequence of connected nodes.

```text
(1)---(2)---(3)---(4)
```

Path from `1` to `4`:

```text
1 -> 2 -> 3 -> 4
```

The number of edges on this path is `3`.

---

## 1.6 Cycle

A **cycle** means we can start from a node, travel through edges, and come back to the same node without simply reversing one edge immediately.

```text
      (1)
     /   \
   (2)---(3)
```

Cycle:

```text
1 -> 2 -> 3 -> 1
```

A tree is special because it has **no cycle**.

---

# 2. What Is a Tree?

The lecture definition is:

> A tree is a **connected graph without any cycle**.

That gives two conditions.

```text
TREE
 |
 +-- connected
 |
 +-- no cycle
```

Example:

```text
        (1)
       /   \
     (2)   (3)
    /  \
  (4)  (5)
```

Why is this a tree?

1. Every node is connected.
2. There is no cycle.

So it is a tree.

---

## 2.1 Country Analogy from the Lecture

Think of:

```text
country = node
road    = edge
```

A connected group of countries behaves like one continent.

A tree is like one connected continent where between any two countries there is only **one route without repetition**.

That unique-path idea is one of the most important properties of trees.

---

# 3. Tree vs General Graph

Every tree is a graph.

But every graph is **not** a tree.

## Case A — Tree

```text
        (1)
       /   \
     (2)   (3)
      |
     (4)
```

Connected? `YES`

Cycle? `NO`

Therefore:

```text
TREE
```

---

## Case B — Graph with a Cycle

```text
      (1)
     /   \
   (2)---(3)
```

Connected? `YES`

Cycle? `YES`

Therefore:

```text
NOT a tree
```

---

## Case C — Disconnected Graph

```text
(1)---(2)      (3)---(4)
```

Connected? `NO`

Therefore:

```text
NOT a tree
```

---

## Fast Test

Ask two questions:

```text
1. Is the graph connected?
2. Does the graph have a cycle?
```

A tree needs:

```text
connected = YES
cycle     = NO
```

---

# 4. Neighbour and Degree

## 4.1 Neighbour of a Node

A neighbour of node `x` is any node directly connected to `x` by one edge.

Example:

```text
          (2)
           |
(4) ---- (1) ---- (3)
           |
          (5)
```

Neighbours of `1`:

```text
{2, 3, 4, 5}
```

Neighbours of `2`:

```text
{1}
```

---

## 4.2 Degree of a Node

The **degree** of a node is the number of its neighbours.

For the same tree:

```text
          (2)
           |
(4) ---- (1) ---- (3)
           |
          (5)
```

```text
deg(1) = 4
deg(2) = 1
deg(3) = 1
deg(4) = 1
deg(5) = 1
```

So:

```text
degree(node) = number of directly connected nodes
```

---

# 5. Leaf and Non-Leaf Nodes

In an **unrooted tree**, the lecture uses:

```text
leaf node     -> degree = 1
non-leaf node -> degree > 1
```

Example:

```text
          (2)
           |
(4) ---- (1) ---- (3)
           |
          (5)
```

Leaves:

```text
2, 3, 4, 5
```

Internal/non-leaf node:

```text
1
```

### Important rooted-tree detail

After choosing a root, we often describe a leaf as:

```text
node with no children
```

This is safer for rooted-tree problems because the root can have degree `1` yet still not behave like an ordinary leaf in some problem statements.

---

# 6. Diameter of a Tree

The lecture defines the **diameter** as the maximum distance between any two nodes in the tree.

Think:

```text
Find the two nodes that are farthest apart.
```

Example:

```text
(1)---(2)---(3)---(4)---(5)
```

Longest path:

```text
1 -> 2 -> 3 -> 4 -> 5
```

Number of edges:

```text
4
```

Therefore:

```text
diameter = 4
```

---

## 6.1 Branched Example

```text
            (1)
           /   \
         (2)   (3)
        /       \
      (4)       (5)
     /           \
   (6)           (7)
```

One longest path is:

```text
6 -> 4 -> 2 -> 1 -> 3 -> 5 -> 7
```

Distance:

```text
6 edges
```

So:

```text
diameter = 6
```

The lecture also highlights that the endpoints of a diameter are leaf nodes.

---

# 7. Core Tree Properties

These are extremely important.

## Property 1 — Unique Path Between Every Two Nodes

For any nodes `u` and `v`, there is exactly one simple path between them.

Example:

```text
        (1)
       /   \
     (2)   (3)
     |
    (4)
```

Path from `4` to `3`:

```text
4 -> 2 -> 1 -> 3
```

There is no second different simple path.

### Why?

If two different paths existed, together they would create a cycle.

But a tree has no cycle.

Therefore the path is unique.

---

## Property 2 — N Nodes Have Exactly N - 1 Edges

For a tree with `N` nodes:

```text
E = N - 1
```

Examples:

```text
N = 1 -> E = 0
N = 2 -> E = 1
N = 5 -> E = 4
N = 10 -> E = 9
```

### Intuition

Start with one node:

```text
(1)
```

To add another node while keeping the structure connected and cycle-free, attach it with exactly one edge.

```text
(1)---(2)
```

Every new node contributes one new edge.

Starting from 1 node and adding `N-1` more nodes:

```text
edges = N - 1
```

---

## Property 3 — Sum of Degrees = 2(N - 1)

Every undirected edge touches exactly two endpoints.

So every edge contributes `2` to the total degree sum.

For any undirected graph:

```text
sum(degree) = 2E
```

For a tree:

```text
E = N - 1
```

Therefore:

```text
sum(degree) = 2(N - 1)
```

Hence the degree sum is always even.

### Tiny Example

```text
    (1)
   /   \
 (2)   (3)
```

Degrees:

```text
deg(1) = 2
deg(2) = 1
deg(3) = 1
```

Sum:

```text
2 + 1 + 1 = 4
```

Formula:

```text
2(N - 1)
= 2(3 - 1)
= 4
```

Matches.

---

## Property 4 — At Least Two Leaves

A tree with more than one node has at least two leaves.

Example chain:

```text
(1)---(2)---(3)---(4)
```

Leaves:

```text
1 and 4
```

A useful way to remember this from the lecture:

```text
endpoints of a diameter are leaves
```

Since every non-single-node tree has a diameter, it has at least two leaf endpoints.

---

# 8. Rooted vs Unrooted Tree

## 8.1 Unrooted Tree

An unrooted tree has no special starting node.

```text
      (2)
     /   \
   (1)   (3)
          \
          (4)
```

No node is considered "top".

---

## 8.2 Rooted Tree

Choose one node as the **root**.

Suppose root = `1`.

```text
              (1)        <- root
             /   \
           (2)   (3)
          /  \     \
        (4)  (5)   (6)
```

Now the tree has a natural direction:

```text
root
 ↓
children
 ↓
grandchildren
```

Important:

```text
The underlying edges are still undirected.
```

The root is only a way to organize the tree.

---

# 9. Important Terms in a Rooted Tree

Use this one tree for all definitions:

```text
                    (0)
                 /   |   \
               /     |     \
             (1)    (2)    (3)
            /  \    / \      \
          (4) (5) (6) (7)    (8)
          / \       / \        \
        (9)(10)   (11)(12)     (13)
        /           /
      (14)        (15)
```

---

## 9.1 Root

The selected top node.

Here:

```text
root = 0
```

The root has no parent.

We commonly store:

```text
parent[root] = -1
```

---

## 9.2 Parent

The parent of node `x` is the next node on the path from `x` toward the root.

Examples:

```text
parent[1]  = 0
parent[4]  = 1
parent[14] = 9
parent[15] = 11
```

Visual:

```text
(0)
 |
(1)
 |
(4)
 |
(9)
 |
(14)
```

For node `14`, the immediate step toward root `0` is `9`.

Therefore:

```text
parent[14] = 9
```

---

## 9.3 Child

A child of node `x` is a neighbouring node whose parent is `x`.

Examples:

```text
children(0) = {1, 2, 3}
children(1) = {4, 5}
children(6) = {11, 12}
```

Leaf nodes have zero children.

---

## 9.4 Ancestor

Ancestors of `x` are the nodes encountered while moving from `x` toward the root, excluding `x` itself.

For node `14`:

```text
14 -> 9 -> 4 -> 1 -> 0
```

Therefore:

```text
ancestors(14) = {9, 4, 1, 0}
```

---

## 9.5 Descendant

A descendant of `x` is a node lying below `x` in the rooted tree.

For node `1`:

```text
        (1)
       /   \
     (4)   (5)
    / \
  (9)(10)
  /
(14)
```

So:

```text
descendants(1) = {4, 5, 9, 10, 14}
```

---

## 9.6 Level / Depth of a Node

The lecture uses level as distance from the root.

Let root depth be `0`.

```text
                    (0)          depth 0
                 /   |   \
               (1)  (2)  (3)    depth 1
               |          |
              (4)        (8)     depth 2
              |
             (9)                  depth 3
              |
            (14)                  depth 4
```

So:

```text
depth[0]  = 0
depth[1]  = 1
depth[4]  = 2
depth[9]  = 3
depth[14] = 4
```

Formula:

```text
depth[child] = depth[parent] + 1
```

---

## 9.7 Subtree

The subtree of `x` contains:

```text
x + all descendants of x
```

Example: subtree of node `1`:

```text
        (1)
       /   \
     (4)   (5)
    / \
  (9)(10)
  /
(14)
```

Therefore:

```text
subtree(1) = {1, 4, 5, 9, 10, 14}
```

And:

```text
subtree(root) = entire tree
```

---

## 9.8 Subtree Size

Subtree size is the number of nodes inside the subtree.

For the previous example:

```text
subtree(1) = {1, 4, 5, 9, 10, 14}
```

Therefore:

```text
subtreeSize[1] = 6
```

A later standard DFS recurrence will be:

```text
subtreeSize[x] = 1 + sum(subtreeSize[child])
```

The `1` counts node `x` itself.

---

## 9.9 Height of a Tree

The lecture defines tree height as the maximum level of any node.

Using root depth `0`:

```text
height = maximum depth
```

If deepest nodes are at depth `4`:

```text
height = 4
```

---

## 9.10 Lowest Common Ancestor — LCA

For two nodes `x` and `y`, their LCA is the first common node encountered when moving upward toward the root.

Example:

```text
              (0)
             /   \
           (1)   (2)
          /  \
        (4)  (5)
       /  \
     (9)  (10)
```

For nodes `9` and `10`:

```text
path upward from 9  : 9 -> 4 -> 1 -> 0
path upward from 10 : 10 -> 4 -> 1 -> 0
```

First common node:

```text
4
```

Therefore:

```text
LCA(9, 10) = 4
```

Another example:

```text
LCA(9, 5) = 1
```

LCA becomes extremely important later for path queries.

---

# 10. Three Possible Shapes of a Path

The lecture states that a path in a rooted tree can appear in only three forms.

## Form 1 — Only Go Up

Example:

```text
(14)
  |
 (9)
  |
 (4)
  |
 (1)
```

Path:

```text
14 -> 9 -> 4 -> 1
```

Only upward movement.

---

## Form 2 — Only Come Down

```text
(1)
 |
(4)
 |
(9)
 |
(14)
```

Path:

```text
1 -> 4 -> 9 -> 14
```

Only downward movement.

---

## Form 3 — Go Up, Then Come Down

```text
        (1)
       /   \
     (4)   (5)
     |
    (9)
```

Path from `9` to `5`:

```text
9 -> 4 -> 1 -> 5
```

Movement:

```text
9 -> 4 -> 1    UP
1 -> 5         DOWN
```

The turning point is:

```text
LCA(9, 5) = 1
```

This produces one of the most useful tree formulas later:

```text
path(u, v)
=
path(u -> LCA) + path(LCA -> v)
```

---

# 11. Tree Is Bipartite

The lecture states that every tree can be colored using two colors so that no adjacent nodes have the same color.

Use:

```text
R = Red
B = Blue
```

Example:

```text
              R(0)
             /   \
          B(1)   B(2)
          / \      \
       R(3) R(4)   R(5)
           /
         B(6)
```

Every edge connects:

```text
Red <-> Blue
```

Never:

```text
Red <-> Red
Blue <-> Blue
```

---

## 11.1 Easy Coloring Rule

Color by depth parity:

```text
even depth -> color 0
odd depth  -> color 1
```

Because every tree edge changes depth by exactly `1` between parent and child.

So neighbouring depths have opposite parity.

---

# 12. Tree Traversal

Traversal means:

> Visit all nodes of the tree.

The lecture introduces two standard approaches:

```text
DFS = Depth First Search
BFS = Breadth First Search
```

For this note, the lecture implementation focuses on DFS.

---

# 13. DFS on a Tree

DFS means:

```text
Go deep into one branch first.
When that branch finishes, come back.
Then explore another branch.
```

Example:

```text
             (1)
           /  |  \
         (2) (7) (8)
        / \       / \
      (3) (6)   (9) (12)
     / \        / \
   (4) (5)    (10)(11)
```

One possible DFS order is:

```text
1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12
```

Another valid DFS order may exist because the order of neighbours can differ.

Important:

```text
DFS order is not unique.
```

---

## 13.1 DFS Thinking Step by Step

Suppose we are at node `u`.

```text
Step 1: visit u
Step 2: inspect every neighbour v
Step 3: do not go back to parent
Step 4: recursively DFS into every child
```

Pseudocode:

```text
dfs(u, parent):
    visit u

    for each neighbour v of u:
        if v == parent:
            continue

        dfs(v, u)
```

---

# 14. Why Parent Is Enough Instead of visited[]

In a general graph, DFS normally uses:

```text
visited[]
```

because cycles may exist.

But a tree has no cycle.

Suppose:

```text
(1)---(2)---(3)
```

DFS reaches `2` from `1`.

At node `2`, neighbours are:

```text
1 and 3
```

The only previously visited neighbour that can send us backward is the parent `1`.

So simply skip:

```text
if (neighbour == parent)
    continue;
```

Then continue to `3`.

This works because the tree contains no alternative cycle back to an old node.

---

# 15. Clean C++17 DFS Template

This follows the compact code style used in your recent CP notes.

```cpp
#include <bits/stdc++.h>
using namespace std;

void dfs(int node, int parent, const vector<vector<int>>& graph, vector<int>& order) {
    order.push_back(node);

    for (int neighbour : graph[node]) {
        if (neighbour == parent) {
            continue;
        }

        dfs(neighbour, node, graph, order);
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<vector<int>> graph(n + 1);

    for (int i = 0; i < n - 1; i++) {
        int u, v;
        cin >> u >> v;

        graph[u].push_back(v);
        graph[v].push_back(u);
    }

    int root = 1;
    vector<int> dfsOrder;

    dfs(root, -1, graph, dfsOrder);

    for (int node : dfsOrder) {
        cout << node << ' ';
    }

    cout << '\n';
    return 0;
}
```

---

## 15.1 Understand the Code Line by Line

### Graph storage

```cpp
vector<vector<int>> graph(n + 1);
```

`graph[u]` stores all neighbours of `u`.

---

### Tree has N - 1 edges

```cpp
for (int i = 0; i < n - 1; i++) {
```

Because a tree with `n` nodes has exactly:

```text
n - 1 edges
```

---

### Undirected edge

```cpp
graph[u].push_back(v);
graph[v].push_back(u);
```

If:

```text
u -- v
```

then:

```text
v is neighbour of u
u is neighbour of v
```

---

### Start DFS

```cpp
dfs(root, -1, graph, dfsOrder);
```

Root has no parent, so use:

```text
-1
```

---

### Visit current node

```cpp
order.push_back(node);
```

This records the node when DFS first reaches it.

---

### Explore neighbours

```cpp
for (int neighbour : graph[node]) {
```

Look at every directly connected node.

---

### Do not go back to parent

```cpp
if (neighbour == parent) {
    continue;
}
```

This prevents:

```text
parent -> child -> parent -> child -> ...
```

---

### Recursive call

```cpp
dfs(neighbour, node, graph, order);
```

Current node becomes the parent of the neighbour.

---

# 16. Complexity

A tree with `N` nodes has:

```text
N - 1 edges
```

In an adjacency list, each undirected edge is stored twice:

```text
u -> v
v -> u
```

So DFS processes roughly:

```text
N nodes + 2(N - 1) adjacency entries
```

Therefore:

```text
Time Complexity = O(N)
```

The adjacency list needs:

```text
O(N)
```

space for a tree.

The recursion stack can also become:

```text
O(N)
```

for a chain-shaped tree.

---

# 17. Recognition Model

When you see a tree problem, first identify which basic concept is being used.

```text
TREE PROBLEM
   |
   +-- Need relation upward?
   |      -> parent / ancestor
   |
   +-- Need distance from root?
   |      -> depth / level
   |
   +-- Need everything below a node?
   |      -> subtree
   |
   +-- Need number of nodes below?
   |      -> subtree size
   |
   +-- Need common turning point of two paths?
   |      -> LCA
   |
   +-- Need visit/process every node?
   |      -> DFS / BFS
   |
   +-- Need opposite groups?
   |      -> bipartite coloring / depth parity
   |
   +-- Need longest distance?
          -> diameter
```

---

# 18. Final Mental Model

Do not try to memorize twenty disconnected definitions.

Remember this picture:

```text
                          ROOT
                           |
                     parent / child
                           |
                      depth / level
                           |
                   ancestor / descendant
                           |
                        subtree
                           |
                      subtree size

Two nodes
   |
   +-- unique path
   |
   +-- often move UP to LCA, then DOWN

Whole tree
   |
   +-- connected
   +-- no cycle
   +-- N nodes -> N-1 edges
   +-- at least 2 leaves if N > 1
   +-- bipartite
   +-- DFS/BFS visits all nodes
```

The most important beginner pattern is:

```text
Choose a root
    ↓
Run DFS
    ↓
Know parent
    ↓
Know children
    ↓
Know depth
    ↓
Know subtree
    ↓
Build advanced tree ideas later
```

Once this foundation is clear, later topics such as subtree queries, tree DP, diameter, LCA, binary lifting, rerooting, and Euler tour become much easier to understand.
