# 002 — Tree Introduction 2: DFS Ideas, BFS, Diameter & Ancestor Queries

> **Goal:** turn the TLE lecture into a self-study note that you can understand without replaying the whole video.
>
> We will reuse **one clean tree** throughout the note so every idea connects visually.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [The One Tree We Will Reuse](#2-the-one-tree-we-will-reuse)
3. [DFS Problem 1 — Level of Every Node](#3-dfs-problem-1--level-of-every-node)
4. [DFS Problem 2 — Parent of Every Node](#4-dfs-problem-2--parent-of-every-node)
5. [DFS Problem 3 — Number of Children](#5-dfs-problem-3--number-of-children)
6. [DFS Problem 4 — Subtree Size](#6-dfs-problem-4--subtree-size)
7. [DFS Problem 5 — Farthest Leaf in the Subtree](#7-dfs-problem-5--farthest-leaf-in-the-subtree)
8. [BFS Traversal in a Tree](#8-bfs-traversal-in-a-tree)
9. [Diameter of a Tree](#9-diameter-of-a-tree)
10. [Diameter Approach 1 — Tree DP](#10-diameter-approach-1--tree-dp)
11. [Diameter Approach 2 — Two Traversals](#11-diameter-approach-2--two-traversals)
12. [Ancestor Queries](#12-ancestor-queries)
13. [In-Time / Out-Time Trick](#13-in-time--out-time-trick)
14. [Complete C++17 Templates](#14-complete-c17-templates)
15. [Recognition Guide](#15-recognition-guide)
16. [Final Mental Model](#16-final-mental-model)

---

# 1. Prerequisites

Before solving the lecture problems, these ideas must feel automatic.

## 1.1 Tree

A tree is a graph that is:

- connected,
- has no cycle,
- has exactly `N - 1` edges for `N` nodes,
- has exactly one simple path between every pair of nodes.

Example:

```text
        0
       / \
      1   2
     /     \
    3       4
```

There is exactly one path from `3` to `4`:

```text
3 -> 1 -> 0 -> 2 -> 4
```

---

## 1.2 Rooted Tree

An ordinary tree has no special top node.

When we choose one node as the **root**, the same tree gains direction-like relationships:

```text
              0   <- root
            /   \
           1     2
          / \     \
         3   4     5
```

Now:

```text
parent[1] = 0
parent[3] = 1
parent[4] = 1
parent[5] = 2
```

The edge itself is still undirected in the input.

The words **parent** and **child** come from how we choose to view the tree from the root.

---

## 1.3 Parent and Child

If the rooted tree contains:

```text
        1
       / \
      3   4
```

then:

```text
parent[3] = 1
parent[4] = 1

children(1) = {3, 4}
```

Important:

```text
Every non-root node has exactly ONE parent.
```

---

## 1.4 Level / Depth

The lecture uses **level**.

For a root `0`:

```text
level[0] = 0
```

Every edge we move downward increases the level by `1`.

```text
              0              level 0
            /   \
           1     2            level 1
          / \     \
         3   4     5          level 2
```

So:

```text
level[3] = 2
level[5] = 2
```

Equivalent interpretation:

```text
level[x] = distance(root, x)
```

when every edge has weight `1`.

---

## 1.5 Leaf Node

In a rooted tree, a leaf is a node with **no children**.

```text
        0
       / \
      1   2
     / \
    3   4
```

Leaves:

```text
2, 3, 4
```

Notice something subtle:

```text
degree(2) = 1
```

and node `2` is a leaf.

But for the root, degree `1` does not automatically mean we should apply every non-root leaf shortcut blindly. Rooted-tree logic is safer when based on **children count**.

---

## 1.6 Subtree

The subtree of node `x` contains:

```text
x + all descendants of x
```

Example:

```text
              0
            /   \
           1     2
          / \   / \
         3   4 5   6
            / \
           7   8
```

Subtree of `4`:

```text
       4
      / \
     7   8
```

Therefore:

```text
subtree(4) = {4, 7, 8}
subtreeSize[4] = 3
```

---

## 1.7 Height / Farthest Leaf Distance

For this lecture, the DFS problem asks:

> How far is the farthest leaf inside the subtree of each node?

This is exactly the familiar tree-height idea when measured in edges.

Leaf:

```text
height[leaf] = 0
```

Non-leaf:

```text
height[x] = 1 + max(height[child])
```

Example:

```text
        0
       /
      1
     /
    2
```

Then:

```text
height[2] = 0
height[1] = 1
height[0] = 2
```

---

## 1.8 DFS Recursion Pattern

For trees, a very common DFS function is:

```cpp
void dfs(int node, int parent) {
    for (int neighbour : adj[node]) {
        if (neighbour == parent) {
            continue;
        }

        dfs(neighbour, node);
    }
}
```

Why do we skip `parent`?

Because the adjacency list contains both directions.

For edge:

```text
1 ----- 3
```

we store:

```text
adj[1] contains 3
adj[3] contains 1
```

Without parent-skipping:

```text
1 -> 3 -> 1 -> 3 -> 1 -> ...
```

infinite recursion.

---

## 1.9 Preorder and Postorder Thinking

This is one of the most important prerequisites in tree DFS.

### Preorder idea

Do work **before** visiting children.

Useful for:

```text
level
parent
entry time
```

### Postorder idea

First solve children, then use their answers.

Useful for:

```text
subtree size
height
maximum child value
diameter DP
```

Mental model:

```text
DFS(node)

ENTER node       <- preorder information

solve child 1
solve child 2
solve child 3

COMBINE children <- postorder information
EXIT node
```

---

## 1.10 BFS and Queue

BFS uses a queue:

```text
push root

while queue not empty:
    take front node
    visit its unvisited neighbours
    push them into queue
```

The important property is:

```text
BFS processes nodes level by level.
```

That makes BFS naturally useful for distances in an unweighted tree.

---

# 2. The One Tree We Will Reuse

To avoid messy drawings, almost every section uses this tree.

```text
                           0
                    ┌──────┼──────┐
                    │      │      │
                    1      2      3
                  ┌─┴─┐  ┌─┴─┐    │
                  4   5  6   7    8
                ┌─┴─┐    ┌─┴─┐    │
                9  10   11  12   13
                │
               14
```

Edges:

```text
0-1, 0-2, 0-3
1-4, 1-5
2-6, 2-7
3-8
4-9, 4-10
6-11, 6-12
8-13
9-14
```

## Levels

```text
Level 0:                    0

Level 1:               1    2    3

Level 2:             4  5  6  7   8

Level 3:            9 10 11 12    13

Level 4:           14
```

This tree will help us understand the entire lecture as one connected story.

---

# 3. DFS Problem 1 — Level of Every Node

## Problem

Tree is rooted at `0`.

Find:

```text
level[node]
```

for every node.

Root level:

```text
level[0] = 0
```

---

## 3.1 Observation

Suppose:

```text
parent -> child
```

If:

```text
level[parent] = k
```

then:

```text
level[child] = k + 1
```

Why?

Because the child is exactly one edge farther from the root.

---

## 3.2 Step-by-Step Example

Using our tree:

```text
level[0] = 0
```

Children of `0`:

```text
level[1] = 1
level[2] = 1
level[3] = 1
```

From node `1`:

```text
level[4] = 2
level[5] = 2
```

From node `4`:

```text
level[9]  = 3
level[10] = 3
```

From node `9`:

```text
level[14] = 4
```

So:

```text
node :  0 1 2 3 4 5 6 7 8 9 10 11 12 13 14
level:  0 1 1 1 2 2 2 2 2 3  3  3  3  3  4
```

---

## 3.3 DFS State

Pass the current level into DFS.

```text
dfs(node, parent, level)
```

When moving to a child:

```text
dfs(child, node, level + 1)
```

---

## 3.4 C++17

```cpp
void dfsLevel(int node, int parent, int currentLevel,
              const vector<vector<int>>& adj, vector<int>& level) {
    level[node] = currentLevel;

    for (int neighbour : adj[node]) {
        if (neighbour == parent) {
            continue;
        }

        dfsLevel(neighbour, node, currentLevel + 1, adj, level);
    }
}
```

### Complexity

Each node and edge is processed a constant number of times:

```text
O(N)
```

for a tree.

---

# 4. DFS Problem 2 — Parent of Every Node

## Problem

Tree is rooted at `0`.

Find the parent of every node.

Convention:

```text
parent[0] = -1
```

---

## 4.1 Main Observation

When DFS moves:

```text
currentNode -> neighbour
```

and `neighbour` is not the parent we came from, then:

```text
parent[neighbour] = currentNode
```

---

## 4.2 Visual Example

```text
                 0
              /  |  \
             1   2   3
            / \
           4   5
```

Therefore:

```text
parent[0] = -1
parent[1] = 0
parent[2] = 0
parent[3] = 0
parent[4] = 1
parent[5] = 1
```

---

## 4.3 Connection with Problem 1

Notice the DFS can compute both level and parent at the same time.

When we move:

```text
node -> child
```

we know immediately:

```text
parent[child] = node
level[child]  = level[node] + 1
```

This is a powerful tree pattern:

```text
ONE DFS can collect many simple properties.
```

---

## 4.4 C++17

```cpp
void dfsParent(int node, int parent,
               const vector<vector<int>>& adj, vector<int>& parentOf) {
    parentOf[node] = parent;

    for (int neighbour : adj[node]) {
        if (neighbour == parent) {
            continue;
        }

        dfsParent(neighbour, node, adj, parentOf);
    }
}
```

---

# 5. DFS Problem 3 — Number of Children

## Problem

For each node, find how many children it has in the rooted tree.

---

## 5.1 Do Not Confuse Degree with Children

For a non-root node:

```text
one neighbour = parent
remaining neighbours = children
```

Therefore:

```text
children[node] = degree[node] - 1
```

for non-root nodes.

For the root:

```text
children[root] = degree[root]
```

because the root has no parent.

---

## 5.2 Example

```text
                    0
                 /  |  \
                1   2   3
               / \
              4   5
```

Degrees:

```text
deg(0) = 3
deg(1) = 3   // connected to 0, 4, 5
deg(4) = 1
```

Children:

```text
children(0) = 3
children(1) = 2
children(4) = 0
```

---

## 5.3 DFS Way

Instead of using the degree formula, count how many recursive calls go downward.

```text
count = 0

for each neighbour:
    skip parent
    count++
    dfs(child)
```

---

## 5.4 C++17

```cpp
void dfsChildren(int node, int parent,
                 const vector<vector<int>>& adj, vector<int>& childCount) {
    childCount[node] = 0;

    for (int neighbour : adj[node]) {
        if (neighbour == parent) {
            continue;
        }

        childCount[node]++;
        dfsChildren(neighbour, node, adj, childCount);
    }
}
```

---

# 6. DFS Problem 4 — Subtree Size

This is the first major **postorder tree DP** pattern.

## Problem

Find the number of nodes in the subtree of every node.

---

## 6.1 Start from the Simplest Case

A leaf has no descendants.

But its subtree still contains the leaf itself.

Therefore:

```text
subtreeSize[leaf] = 1
```

---

## 6.2 Build the Formula

Suppose node `x` has children:

```text
c1, c2, c3
```

Its subtree contains:

```text
x itself
+
subtree(c1)
+
subtree(c2)
+
subtree(c3)
```

So:

```text
subtreeSize[x]
    = 1
    + subtreeSize[c1]
    + subtreeSize[c2]
    + subtreeSize[c3]
```

General formula:

```text
subtreeSize[x] = 1 + Σ subtreeSize[child]
```

---

## 6.3 Why Children Must Be Solved First

Suppose:

```text
        4
       / \
      9  10
     /
    14
```

Start from leaves:

```text
subtreeSize[14] = 1
subtreeSize[10] = 1
```

Then node `9`:

```text
subtreeSize[9]
= 1 + subtreeSize[14]
= 1 + 1
= 2
```

Then node `4`:

```text
subtreeSize[4]
= 1 + subtreeSize[9] + subtreeSize[10]
= 1 + 2 + 1
= 4
```

This is why subtree-size calculation happens **after recursive calls return**.

---

## 6.4 Visual Bottom-Up Calculation

```text
                  4 [4]
                 /     \
              9 [2]   10 [1]
               /
            14 [1]
```

The value in `[ ]` is subtree size.

---

## 6.5 C++17

```cpp
void dfsSubtree(int node, int parent,
                const vector<vector<int>>& adj, vector<int>& subtreeSize) {
    subtreeSize[node] = 1;

    for (int neighbour : adj[node]) {
        if (neighbour == parent) {
            continue;
        }

        dfsSubtree(neighbour, node, adj, subtreeSize);
        subtreeSize[node] += subtreeSize[neighbour];
    }
}
```

### Recognition Pattern

Whenever the answer for a node looks like:

```text
my contribution + answers from all children
```

think:

```text
POSTORDER DFS
```

---

# 7. DFS Problem 5 — Farthest Leaf in the Subtree

## Problem

For every node `x`, find the distance from `x` to the farthest leaf inside `x`'s subtree.

This is tree height measured in edges.

---

## 7.1 Base Case

For a leaf:

```text
f(leaf) = 0
```

Why not `1`?

Because the distance from a node to itself is zero edges.

---

## 7.2 Non-Leaf Formula

Suppose node `x` has children:

```text
c1, c2, c3
```

If the farthest leaf is inside child `c2`'s subtree, then:

```text
x -> c2       costs 1 edge
+
f(c2)        remaining distance
```

Therefore:

```text
f(x) = 1 + max(f(child))
```

---

## 7.3 Step-by-Step Example

```text
            4
           / \
          9  10
         /
        14
```

Leaves:

```text
f(14) = 0
f(10) = 0
```

Node `9`:

```text
f(9)
= 1 + f(14)
= 1
```

Node `4`:

```text
f(4)
= 1 + max(f(9), f(10))
= 1 + max(1, 0)
= 2
```

Visualization:

```text
            4 [2]
           /     \
        9 [1]   10 [0]
         /
      14 [0]
```

---

## 7.4 C++17

```cpp
void dfsHeight(int node, int parent,
               const vector<vector<int>>& adj, vector<int>& height) {
    height[node] = 0;

    for (int neighbour : adj[node]) {
        if (neighbour == parent) {
            continue;
        }

        dfsHeight(neighbour, node, adj, height);
        height[node] = max(height[node], 1 + height[neighbour]);
    }
}
```

This tiny recurrence is extremely important because it becomes part of the diameter solution.

---

# 8. BFS Traversal in a Tree

DFS goes deep first.

BFS explores level by level.

---

## 8.1 Visual BFS Order

Using a smaller tree:

```text
                  0
              /   |   \
             1    2    3
            / \       / \
           4   5     6   7
```

BFS visits:

```text
Level 0: 0
Level 1: 1 2 3
Level 2: 4 5 6 7
```

One valid BFS order:

```text
0, 1, 2, 3, 4, 5, 6, 7
```

The exact order **inside the same level** depends on adjacency-list order.

But all level-1 nodes appear before level-2 nodes.

---

## 8.2 Queue Dry Run

Tree:

```text
        0
      /   \
     1     2
    / \     \
   3   4     5
```

Start:

```text
queue = [0]
```

Iter 1:

```text
pop 0
push 1, 2
queue = [1, 2]
```

Iter 2:

```text
pop 1
push 3, 4
queue = [2, 3, 4]
```

Iter 3:

```text
pop 2
push 5
queue = [3, 4, 5]
```

Then:

```text
pop 3
pop 4
pop 5
```

Traversal:

```text
0 1 2 3 4 5
```

---

## 8.3 BFS C++17

```cpp
vector<int> bfsTraversal(int root, const vector<vector<int>>& adj) {
    int n = adj.size();
    vector<int> traversal;
    vector<bool> visited(n, false);
    queue<int> q;

    visited[root] = true;
    q.push(root);

    while (!q.empty()) {
        int node = q.front();
        q.pop();

        traversal.push_back(node);

        for (int neighbour : adj[node]) {
            if (visited[neighbour]) {
                continue;
            }

            visited[neighbour] = true;
            q.push(neighbour);
        }
    }

    return traversal;
}
```

### Complexity

```text
O(N)
```

for a tree.

---

## 8.4 DFS vs BFS

| Need | Natural Choice |
|---|---|
| subtree information | DFS |
| parent / depth | DFS or BFS |
| bottom-up combine | DFS |
| level-order traversal | BFS |
| unweighted distance from one node | BFS or DFS in a tree |
| in-time / out-time | DFS |
| tree DP | DFS |

Important tree fact:

Because there is exactly one path between two nodes, DFS can also compute distances from a source in `O(N)`.

---

# 9. Diameter of a Tree

## 9.1 Definition

Diameter is the maximum distance between any two nodes in the tree.

Distance here means number of edges unless the problem says otherwise.

Example:

```text
          1
         / \
        2   3
       /     \
      4       5
     /         \
    6           7
```

Path:

```text
6 -> 4 -> 2 -> 1 -> 3 -> 5 -> 7
```

contains `6` edges.

Therefore:

```text
diameter = 6
```

---

## 9.2 Important Observation

A diameter is a **path**.

It is not necessarily:

```text
root -> deepest node
```

It may pass through the root, or it may lie completely inside a lower subtree.

Example:

```text
             root
              |
              A
             / \
            B   C
           /     \
          D       E
         /         \
        F           G
```

A long diameter can be:

```text
F -> D -> B -> A -> C -> E -> G
```

The root itself need not be an endpoint.

---

# 10. Diameter Approach 1 — Tree DP

The lecture's first idea is:

> For every node `x`, calculate the longest path whose LCA is `x`.

---

## 10.1 Why the LCA View Helps

Suppose the diameter path passes through node `x`.

Then one endpoint must be reached through one child-subtree and the other endpoint through another child-subtree.

```text
             x
           /   \
          A     B
         /       \
        ...       ...
       leaf1     leaf2
```

Path:

```text
leaf1 -> ... -> A -> x -> B -> ... -> leaf2
```

So we need the two deepest downward branches from `x`.

---

## 10.2 Height of Each Child

Let:

```text
height[child]
```

mean the maximum number of edges from the child to a leaf in its subtree.

Then the branch length through that child from node `x` is:

```text
1 + height[child]
```

Suppose the two best downward branch lengths are:

```text
best1
best2
```

Then:

```text
longest path through x = best1 + best2
```

---

## 10.3 Tiny Example

```text
               0
             /   \
            1     2
           /       \
          3         4
         /           \
        5             6
```

At node `0`:

Left branch:

```text
0 -> 1 -> 3 -> 5
length = 3
```

Right branch:

```text
0 -> 2 -> 4 -> 6
length = 3
```

Therefore:

```text
path through 0 = 3 + 3 = 6
```

---

## 10.4 Only One Child Case

Suppose:

```text
0
|
1
|
2
|
3
```

At node `0` there is only one downward branch.

Then:

```text
best1 = 3
best2 = 0
```

So:

```text
path through 0 = 3
```

This correctly handles chains.

---

## 10.5 DFS Recurrence

For every node:

```text
best1 = largest downward branch
best2 = second-largest downward branch
```

For every child:

```text
branch = 1 + height[child]
```

Update:

```text
best1, best2
```

Then:

```text
height[node] = best1

diameter = max(diameter, best1 + best2)
```

---

## 10.6 Compact Dry Run

Tree:

```text
        0
       / \
      1   2
     /     \
    3       4
   / \
  5   6
```

Bottom-up:

```text
Node 5: height=0, pathThrough=0
Node 6: height=0, pathThrough=0
Node 3: branches=(1,1) -> height=1, pathThrough=2
Node 1: branch=(2)     -> height=2, pathThrough=2
Node 4: height=0
Node 2: branch=(1)     -> height=1
Node 0: branches=(3,2) -> height=3, pathThrough=5
```

Therefore:

```text
diameter = 5
```

One diameter path:

```text
5 -> 3 -> 1 -> 0 -> 2 -> 4
```

---

## 10.7 C++17

```cpp
int diameterAnswer = 0;

int dfsDiameter(int node, int parent, const vector<vector<int>>& adj) {
    int best1 = 0;
    int best2 = 0;

    for (int neighbour : adj[node]) {
        if (neighbour == parent) {
            continue;
        }

        int childHeight = dfsDiameter(neighbour, node, adj);
        int branch = 1 + childHeight;

        if (branch > best1) {
            best2 = best1;
            best1 = branch;
        } else if (branch > best2) {
            best2 = branch;
        }
    }

    diameterAnswer = max(diameterAnswer, best1 + best2);
    return best1;
}
```

### Complexity

```text
O(N)
```

---

# 11. Diameter Approach 2 — Two Traversals

This is often the easiest diameter method to remember.

## 11.1 Algorithm

Pick any node `X`.

Find the farthest node from `X`.

Call it:

```text
Y
```

Then start from `Y` and find the farthest node from `Y`.

Call it:

```text
Z
```

Then:

```text
distance(Y, Z) = diameter
```

---

## 11.2 Visual Intuition

Imagine the tree as a rope network.

Start anywhere:

```text
X
```

Walk as far as possible.

You reach an extreme endpoint:

```text
Y
```

Now start from that extreme endpoint and walk as far as possible again.

You reach the opposite extreme endpoint:

```text
Z
```

So:

```text
Y ------------------------------ Z
          longest path
```

---

## 11.3 Example

```text
                0
               / \
              1   2
             /     \
            3       4
           /         \
          5           6
```

Pick:

```text
X = 1
```

Distances from `1`:

```text
to 5 = 2
to 6 = 4
```

Farthest:

```text
Y = 6
```

Now from `6`:

```text
to 5 = 6
```

So:

```text
Z = 5
```

Diameter:

```text
6 -> 4 -> 2 -> 0 -> 1 -> 3 -> 5
```

Length:

```text
6 edges
```

---

## 11.4 DFS Distance Function

Because a tree has only one path between nodes, DFS is enough.

```cpp
void dfsDistance(int node, int parent, int distance,
                 const vector<vector<int>>& adj, vector<int>& dist) {
    dist[node] = distance;

    for (int neighbour : adj[node]) {
        if (neighbour == parent) {
            continue;
        }

        dfsDistance(neighbour, node, distance + 1, adj, dist);
    }
}
```

---

## 11.5 Complete Two-Traversal Diameter

```cpp
pair<int, int> farthestFrom(int start, const vector<vector<int>>& adj) {
    int n = adj.size();
    vector<int> dist(n, 0);

    dfsDistance(start, -1, 0, adj, dist);

    int farthestNode = start;

    for (int node = 0; node < n; node++) {
        if (dist[node] > dist[farthestNode]) {
            farthestNode = node;
        }
    }

    return {farthestNode, dist[farthestNode]};
}

int treeDiameter(const vector<vector<int>>& adj) {
    auto first = farthestFrom(0, adj);
    auto second = farthestFrom(first.first, adj);

    return second.second;
}
```

### Complexity

Two DFS traversals:

```text
O(N) + O(N) = O(N)
```

---

## 11.6 Which Diameter Approach Should You Use?

### Approach 1 — Tree DP

Use when the problem asks for more information during DFS, for example:

```text
diameter of every subtree
best path passing through nodes
combine child answers
weighted variations
```

### Approach 2 — Two Traversals

Use when the problem simply asks:

```text
find the diameter of the entire tree
```

This is usually the cleanest implementation.

---

# 12. Ancestor Queries

## Problem

Tree is rooted at `0`.

For each query `(x, y)` answer:

```text
Is x an ancestor of y?
```

Example:

```text
                0
              /   \
             1     2
            / \
           3   4
              / \
             5   6
```

Queries:

```text
(1, 6) -> YES
(4, 6) -> YES
(2, 6) -> NO
(6, 4) -> NO
```

---

## 12.1 Slow Method

For every query `(x, y)`:

```text
start from y
keep moving to parent[y]
check whether x appears
```

Worst case for one query:

```text
O(N)
```

For `Q` queries:

```text
O(NQ)
```

With:

```text
N, Q <= 100000
```

this can be too slow.

We need preprocessing.

---

# 13. In-Time / Out-Time Trick

This is one of the most useful DFS tricks in trees.

---

## 13.1 The Core Idea

During DFS, record two times for every node.

```text
in[node]  = time when DFS enters node
out[node] = time when DFS finishes node
```

---

## 13.2 Think of DFS as Opening and Closing a Box

Suppose DFS reaches node `x`.

```text
ENTER x
```

Then DFS completely explores **all descendants of x**.

Only after all descendants finish:

```text
EXIT x
```

So every descendant of `x` lives completely inside the time interval:

```text
[in[x], out[x]]
```

---

## 13.3 Better Visualization

Tree:

```text
              0
            /   \
           1     2
          / \
         3   4
            / \
           5   6
```

Imagine DFS processes children left-to-right.

One possible timestamp sequence:

```text
enter 0   -> 1

  enter 1 -> 2

    enter 3 -> 3
    exit  3 -> 4

    enter 4 -> 5

      enter 5 -> 6
      exit  5 -> 7

      enter 6 -> 8
      exit  6 -> 9

    exit 4  -> 10

  exit 1    -> 11

  enter 2   -> 12
  exit  2   -> 13

exit 0      -> 14
```

Intervals:

```text
0 : [1, 14]
1 : [2, 11]
3 : [3,  4]
4 : [5, 10]
5 : [6,  7]
6 : [8,  9]
2 : [12,13]
```

Look at node `1`:

```text
1 : [2,11]
```

Every descendant interval is completely inside it:

```text
3 : [3,4]
4 : [5,10]
5 : [6,7]
6 : [8,9]
```

Node `2` is not a descendant because:

```text
2 : [12,13]
```

is outside `[2,11]`.

---

## 13.4 Ancestor Formula

If the problem treats a node as **not an ancestor of itself**, then `x` is an ancestor of `y` when:

```text
in[x] < in[y]
AND
out[y] < out[x]
```

Equivalent nested form:

```text
in[x] < in[y] < out[y] < out[x]
```

If a problem allows a node to be its own ancestor, use:

```text
in[x] <= in[y]
AND
out[y] <= out[x]
```

Always check the problem definition.

---

## 13.5 Why the Formula Works

Suppose:

```text
x is ancestor of y
```

DFS enters `x` first:

```text
in[x] < in[y]
```

DFS cannot exit `x` until every descendant, including `y`, is fully processed.

Therefore:

```text
out[y] < out[x]
```

So:

```text
in[x] < in[y] < out[y] < out[x]
```

---

## 13.6 DFS Code for In/Out Time

```cpp
int timer = 0;

void dfsTime(int node, int parent,
             const vector<vector<int>>& adj,
             vector<int>& inTime, vector<int>& outTime) {
    inTime[node] = ++timer;

    for (int neighbour : adj[node]) {
        if (neighbour == parent) {
            continue;
        }

        dfsTime(neighbour, node, adj, inTime, outTime);
    }

    outTime[node] = ++timer;
}
```

---

## 13.7 O(1) Ancestor Query

```cpp
bool isAncestor(int x, int y,
                const vector<int>& inTime,
                const vector<int>& outTime) {
    return inTime[x] < inTime[y] && outTime[y] < outTime[x];
}
```

Preprocessing:

```text
O(N)
```

Each query:

```text
O(1)
```

Total:

```text
O(N + Q)
```

instead of:

```text
O(NQ)
```

---

# 14. Complete C++17 Templates

## 14.1 Build an Undirected Tree

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<vector<int>> adj(n);

    for (int i = 0; i < n - 1; i++) {
        int u, v;
        cin >> u >> v;

        adj[u].push_back(v);
        adj[v].push_back(u);
    }

    return 0;
}
```

Remember:

```text
N nodes -> N - 1 edges
```

---

## 14.2 One DFS Computing Parent, Level, Child Count, Subtree Size and Height

This combines the lecture ideas into one reusable template.

```cpp
void dfs(int node, int parent,
         const vector<vector<int>>& adj,
         vector<int>& parentOf,
         vector<int>& level,
         vector<int>& childCount,
         vector<int>& subtreeSize,
         vector<int>& height) {
    parentOf[node] = parent;
    childCount[node] = 0;
    subtreeSize[node] = 1;
    height[node] = 0;

    for (int neighbour : adj[node]) {
        if (neighbour == parent) {
            continue;
        }

        level[neighbour] = level[node] + 1;
        childCount[node]++;

        dfs(neighbour, node, adj, parentOf, level,
            childCount, subtreeSize, height);

        subtreeSize[node] += subtreeSize[neighbour];
        height[node] = max(height[node], 1 + height[neighbour]);
    }
}
```

Usage:

```cpp
vector<int> parentOf(n);
vector<int> level(n, 0);
vector<int> childCount(n);
vector<int> subtreeSize(n);
vector<int> height(n);

level[0] = 0;
dfs(0, -1, adj, parentOf, level, childCount, subtreeSize, height);
```

Notice the split:

```text
BEFORE recursive call:
    level
    parent
    child count

AFTER recursive call:
    subtree size
    height
```

That distinction is one of the most important tree-DP skills.

---

## 14.3 Complete Ancestor Query Template

```cpp
#include <bits/stdc++.h>
using namespace std;

int timerValue = 0;

void dfsTime(int node, int parent,
             const vector<vector<int>>& adj,
             vector<int>& inTime,
             vector<int>& outTime) {
    inTime[node] = ++timerValue;

    for (int neighbour : adj[node]) {
        if (neighbour == parent) {
            continue;
        }

        dfsTime(neighbour, node, adj, inTime, outTime);
    }

    outTime[node] = ++timerValue;
}

bool isAncestor(int x, int y,
                const vector<int>& inTime,
                const vector<int>& outTime) {
    return inTime[x] < inTime[y] && outTime[y] < outTime[x];
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<vector<int>> adj(n);

    for (int i = 0; i < n - 1; i++) {
        int u, v;
        cin >> u >> v;

        adj[u].push_back(v);
        adj[v].push_back(u);
    }

    vector<int> inTime(n);
    vector<int> outTime(n);

    dfsTime(0, -1, adj, inTime, outTime);

    int q;
    cin >> q;

    while (q--) {
        int x, y;
        cin >> x >> y;

        cout << (isAncestor(x, y, inTime, outTime) ? "YES" : "NO") << '\n';
    }

    return 0;
}
```

---

# 15. Recognition Guide

When reading a new tree problem, translate the wording into one of these forms.

| Problem wording | Think |
|---|---|
| distance from root | level / depth |
| who is directly above this node? | parent |
| how many direct descendants? | child count |
| number of nodes below node `x` | subtree size |
| deepest node below `x` | height / farthest leaf |
| visit by layers | BFS |
| longest path in tree | diameter |
| combine two deepest branches | diameter DP |
| many ancestor queries | in/out time |
| answer from children | postorder DFS |
| pass information downward | preorder DFS |

---

# 16. Final Mental Model

Do not memorize every tree solution independently.

Most introductory tree problems come from one DFS skeleton:

```text
DFS(node, parent)
│
├── information known when ENTERING node
│      parent
│      depth / level
│      in-time
│
├── recursively solve every child
│
└── information known after children RETURN
       subtree size
       height
       best child values
       diameter contribution
       out-time
```

The most important recurrences from this lecture are:

```text
LEVEL
level[child] = level[parent] + 1
```

```text
SUBTREE SIZE
subtreeSize[x] = 1 + Σ subtreeSize[child]
```

```text
HEIGHT / FARTHEST LEAF
height[x] = 1 + max(height[child])
height[leaf] = 0
```

```text
DIAMETER THROUGH x
best downward branch 1 + best downward branch 2
```

```text
ANCESTOR
x is ancestor of y
iff
in[x] < in[y] && out[y] < out[x]
```

If these five models are clear, a large number of beginner/intermediate tree questions become variations of the same small set of ideas.

---

## Suggested Study Order

```text
1. Draw the common tree yourself.
2. Calculate level[] manually.
3. Calculate parent[] manually.
4. Calculate childCount[] manually.
5. Calculate subtreeSize[] from leaves upward.
6. Calculate height[] from leaves upward.
7. Perform one BFS queue dry run.
8. Calculate one diameter using both approaches.
9. Assign in/out times manually to a 7-node tree.
10. Code all of them without looking at the note.
```

The goal is not to remember code character-by-character.

The goal is to see:

```text
TREE
  -> choose root
  -> parent/child relation
  -> DFS direction
  -> combine child answers
  -> derive the required quantity
```

That is the reusable competitive-programming skill.
