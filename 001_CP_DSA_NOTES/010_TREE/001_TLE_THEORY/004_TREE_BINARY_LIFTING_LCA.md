# 004 — Binary Lifting & LCA — Visual Self-Learning Notes

> **Source:** TLE / Trees 3 lecture — Binary Lifting, LCA, Distance Queries, and Path Minimum Queries  
> **Goal:** Understand the ideas well enough to solve problems without rewatching the full lecture.

---

## Table of Contents

1. [What this chapter teaches](#1-what-this-chapter-teaches)
2. [Prerequisites](#2-prerequisites)
3. [Why normal parent climbing is too slow](#3-why-normal-parent-climbing-is-too-slow)
4. [Binary Lifting Idea](#4-binary-lifting-idea)
5. [Building the Binary Lifting Table](#5-building-the-binary-lifting-table)
6. [Finding the k-th Ancestor](#6-finding-the-k-th-ancestor)
7. [Lowest Common Ancestor — LCA](#7-lowest-common-ancestor--lca)
8. [Distance Between Two Nodes](#8-distance-between-two-nodes)
9. [Minimum Value on a Path](#9-minimum-value-on-a-path)
10. [Complete C++17 Template](#10-complete-c17-template)
11. [Common Mistakes](#11-common-mistakes)
12. [Recognition Guide](#12-recognition-guide)
13. [Final Mental Model](#13-final-mental-model)

---

# 1. What this chapter teaches

The lecture is built around four connected ideas:

```text
Binary Lifting
      |
      +--> k-th ancestor
      |
      +--> LCA(a, b)
             |
             +--> distance(a, b)
             |
             +--> path aggregate
                    |
                    +--> minimum / maximum / sum / xor ...
```

The most important idea is:

> Instead of moving upward **one parent at a time**, precompute jumps of size  
> `1, 2, 4, 8, 16, ...`.

That turns many tree queries from slow repeated climbing into `O(log N)` work.

---

# 2. Prerequisites

## 2.1 Rooted Tree

A normal tree is an undirected connected graph with no cycle.

Once we choose a root, every edge gets a parent-child meaning.

Example:

```text
                1
              /   \
             2     3
            / \   / \
           4   5 6   7
              / \
             8   9
```

If root = `1`:

```text
parent[1] = 1      // convenient convention
parent[2] = 1
parent[3] = 1
parent[4] = 2
parent[5] = 2
parent[6] = 3
parent[7] = 3
parent[8] = 5
parent[9] = 5
```

---

## 2.2 Depth / Level

`depth[v]` = number of edges from root to node `v`.

For the same tree:

```text
depth 0:              1

depth 1:          2       3

depth 2:        4   5   6   7

depth 3:           8 9
```

So:

```text
depth[1] = 0
depth[2] = 1
depth[5] = 2
depth[8] = 3
```

Depth is extremely important because LCA first needs both nodes to stand at the **same depth**.

---

## 2.3 Ancestor

If we repeatedly move from a node to its parent, every node visited is an ancestor.

For node `8`:

```text
8
|
5      <- 1st ancestor
|
2      <- 2nd ancestor
|
1      <- 3rd ancestor
```

Therefore:

```text
1st ancestor of 8 = 5
2nd ancestor of 8 = 2
3rd ancestor of 8 = 1
```

---

## 2.4 Lowest Common Ancestor

The **Lowest Common Ancestor** of nodes `a` and `b` is the deepest node that is an ancestor of both.

Example:

```text
                1
              /   \
             2     3
            / \   / \
           4   5 6   7
              / \
             8   9
```

Examples:

```text
LCA(8, 9) = 5
LCA(4, 8) = 2
LCA(6, 9) = 1
LCA(2, 8) = 2
```

Notice the last case:

> A node can be the ancestor of the other node, so the LCA may be one of the query nodes itself.

---

## 2.5 Powers of Two

Binary lifting depends on powers of two:

```text
2^0 = 1
2^1 = 2
2^2 = 4
2^3 = 8
2^4 = 16
...
```

Why are these useful?

Because every integer can be written as a sum of powers of two.

Example:

```text
13 = 8 + 4 + 1
   = 2^3 + 2^2 + 2^0
```

So if we know how to jump:

```text
1 ancestor
2 ancestors
4 ancestors
8 ancestors
...
```

then we can jump exactly `13` levels using only 3 jumps.

---

## 2.6 Binary Representation

Example:

```text
k = 13

13 in binary = 1101

bit position:    3 2 1 0
                 1 1 0 1

meaning:
2^3 = 8  -> use
2^2 = 4  -> use
2^1 = 2  -> skip
2^0 = 1  -> use
```

Total movement:

```text
8 + 4 + 1 = 13
```

This is the heart of the k-th ancestor query.

---

## 2.7 DFS Preprocessing

We normally run DFS from the root to calculate:

```text
parent[v]
depth[v]
up[v][j]
```

Basic DFS idea:

```cpp
void dfs(int node, int parent) {
    up[node][0] = parent;

    for (int next : graph[node]) {
        if (next == parent) {
            continue;
        }

        depth[next] = depth[node] + 1;
        dfs(next, node);
    }
}
```

Later we will extend this DFS to fill the full binary lifting table.

---

# 3. Why normal parent climbing is too slow

Suppose we receive:

```text
query(node = 10, k = 7)
```

Naive solution:

```text
10 -> parent -> parent -> parent -> ... 7 times
```

That takes:

```text
O(k)
```

In the worst case:

```text
k ≈ N
```

If:

```text
N = 100000
Q = 100000
```

then repeated parent climbing may approach:

```text
O(N * Q)
```

which is too slow.

---

## 3.1 Visual Example

Imagine a chain:

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

Question:

```text
Find the 6-th ancestor of node 8.
```

Naive:

```text
8 -> 7 -> 6 -> 5 -> 4 -> 3 -> 2
```

Six moves.

Binary lifting:

```text
6 = 4 + 2

8 --jump 4--> 4 --jump 2--> 2
```

Only two jumps.

---

# 4. Binary Lifting Idea

We store:

```text
up[node][j]
```

meaning:

> the `2^j`-th ancestor of `node`.

Therefore:

```text
up[node][0] = 1st ancestor
up[node][1] = 2nd ancestor
up[node][2] = 4th ancestor
up[node][3] = 8th ancestor
...
```

---

## 4.1 Example Table

Consider this tree:

```text
                1
              /   \
             2     3
            / \     \
           4   5     6
              /
             7
            /
           8
```

For node `8`:

```text
8 -> 7 -> 5 -> 2 -> 1
```

So:

```text
up[8][0] = 7       // 1 step
up[8][1] = 5       // 2 steps
up[8][2] = 1       // 4 steps
```

Visual:

```text
8
| 1
7
| 1
5
| 1
2
| 1
1

8 --2^0=1--> 7
8 --2^1=2--> 5
8 --2^2=4--> 1
```

---

# 5. Building the Binary Lifting Table

## 5.1 Base Case

The first column is simply the normal parent.

```text
up[node][0] = parent[node]
```

---

## 5.2 Main Recurrence

Suppose we want:

```text
up[node][j]
```

which means move `2^j` levels upward.

Break it into two equal jumps:

```text
2^j = 2^(j-1) + 2^(j-1)
```

So:

```text
node
  |
  | 2^(j-1)
  v
mid = up[node][j-1]
  |
  | 2^(j-1)
  v
answer
```

Therefore:

```text
up[node][j] = up[ up[node][j-1] ][j-1]
```

This is the most important binary lifting formula.

---

## 5.3 Tiny Numerical Example

Suppose:

```text
up[8][0] = 7
up[7][0] = 5
```

Then:

```text
up[8][1]
= up[ up[8][0] ][0]
= up[7][0]
= 5
```

Meaning:

```text
2nd ancestor of 8 = 5
```

Now:

```text
up[8][2]
= up[ up[8][1] ][1]
= up[5][1]
= 1
```

Meaning:

```text
4th ancestor of 8 = 1
```

---

## 5.4 Why Only `log N` Columns?

If tree has `N` nodes, maximum useful jump is around `N`.

We need powers:

```text
1, 2, 4, 8, ... <= N
```

Number of powers:

```text
O(log N)
```

For:

```text
N <= 100000
```

we have:

```text
2^16 = 65536
2^17 = 131072
```

So roughly 18 columns are enough.

In reusable code, compute:

```cpp
LOG = 1;

while ((1 << LOG) <= n) {
    LOG++;
}
```

---

## 5.5 Preprocessing Code

```cpp
void dfs(int node, int parent) {
    up[node][0] = parent;

    for (int j = 1; j < LOG; j++) {
        up[node][j] = up[up[node][j - 1]][j - 1];
    }

    for (int next : graph[node]) {
        if (next == parent) {
            continue;
        }

        depth[next] = depth[node] + 1;
        dfs(next, node);
    }
}
```

Complexity:

```text
Each node fills LOG entries.

O(N log N)
```

Memory:

```text
O(N log N)
```

---

# 6. Finding the k-th Ancestor

## 6.1 Problem

Given:

```text
node = x
k
```

Find the node that is exactly `k` edges above `x`.

---

## 6.2 Main Observation

Write `k` in binary.

Example:

```text
k = 13 = 8 + 4 + 1
```

Therefore:

```text
jump 8
jump 4
jump 1
```

We never need to move one edge at a time.

---

## 6.3 Visual Dry Run

Tree path:

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
|
9
|
10
```

Query:

```text
node = 10
k = 7
```

Binary:

```text
7 = 4 + 2 + 1
  = 111₂
```

Dry run:

```text
start = 10

bit 2 is ON -> jump 4:
10 -> 6

bit 1 is ON -> jump 2:
6 -> 4

bit 0 is ON -> jump 1:
4 -> 3

answer = 3
```

---

## 6.4 Code

```cpp
int kthAncestor(int node, int k) {
    for (int j = 0; j < LOG; j++) {
        if (k & (1 << j)) {
            node = up[node][j];
        }
    }

    return node;
}
```

Time:

```text
O(log N)
```

---

## 6.5 Recognition Pattern

Whenever a problem says:

```text
k-th parent
k-th ancestor
move k levels upward
jump k times through parent links
```

think:

```text
Binary Lifting
```

---

# 7. Lowest Common Ancestor — LCA

## 7.1 Problem

Given two nodes `a` and `b`, find their deepest common ancestor.

Example:

```text
                1
              /   \
             2     3
            / \   / \
           4   5 6   7
              / \
             8   9
```

Query:

```text
a = 8
b = 4
```

Ancestors of `8`:

```text
8 -> 5 -> 2 -> 1
```

Ancestors of `4`:

```text
4 -> 2 -> 1
```

First common node while moving upward:

```text
2
```

So:

```text
LCA(8, 4) = 2
```

---

## 7.2 Key Difficulty

The two nodes may be at different depths.

Example:

```text
                1
               /
              2
             / \
            3   6
           /
          4
         /
        5
```

Suppose:

```text
a = 5
b = 6
```

Depths:

```text
depth[5] = 4
depth[6] = 2
```

We should not move both upward immediately.

First:

```text
make their depths equal
```

---

# 7.3 Step 1 — Lift the Deeper Node

Difference:

```text
diff = depth[a] - depth[b]
```

Here:

```text
diff = 4 - 2 = 2
```

Move `a` upward by 2:

```text
5 -> 4 -> 3
```

Now:

```text
a = 3
b = 6
```

Both are at depth 2.

Use binary lifting to perform this in `O(log N)`.

---

## 7.4 Important Edge Case

After equalizing depths:

```text
if (a == b)
```

then that node itself is the LCA.

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

Query:

```text
LCA(4, 2)
```

Lift 4 by 2 levels:

```text
4 -> 2
```

Now both are `2`.

Therefore:

```text
LCA = 2
```

---

# 7.5 Step 2 — Lift Both Nodes Together

Now suppose:

```text
a != b
```

Both have equal depth.

We want to move them upward while keeping them **below the LCA**.

Consider:

```text
                1
              /   \
             2     3
            /       \
           4         5
          /           \
         8             9
```

Query:

```text
LCA(8, 9)
```

The answer is:

```text
1
```

If we jump too far and place both directly at `1`, we lose the useful boundary.

Instead, from the largest power of two downward:

```text
if up[a][j] != up[b][j]
    move a upward by 2^j
    move b upward by 2^j
```

After all such jumps:

```text
a and b are different,
but parent[a] == parent[b]
```

That parent is the LCA.

---

## 7.6 Why Iterate from Large Jump to Small Jump?

Suppose:

```text
j = large
```

If:

```text
up[a][j] != up[b][j]
```

then jumping that far still keeps the nodes on different branches.

So that jump is safe.

But if:

```text
up[a][j] == up[b][j]
```

then that ancestor is already common.

Jumping there may go to or above the LCA.

So we do not take that jump.

This is similar to finding the last safe position before crossing a boundary.

---

# 7.7 Detailed LCA Dry Run

Tree:

```text
                       1
                    /     \
                   2       3
                 /   \   /   \
                4     5 6     7
               /             / \
              8             9  10
             /
            11
```

Query:

```text
LCA(11, 9)
```

Depths:

```text
depth[11] = 4
depth[9]  = 3
```

### Step A — Equalize Depth

```text
diff = 1

11 --1 jump--> 8
```

Now:

```text
a = 8
b = 9

depth[a] = depth[b] = 3
```

### Step B — Compare Large Jumps

Ancestors:

```text
8 -> 4 -> 2 -> 1
9 -> 7 -> 3 -> 1
```

Suppose `j = 1` means jump 2.

```text
up[8][1] = 2
up[9][1] = 3
```

They are different.

So jump both:

```text
a = 2
b = 3
```

Next smaller jump `j = 0`:

```text
up[2][0] = 1
up[3][0] = 1
```

They are equal.

Do not jump.

Now:

```text
parent[2] = parent[3] = 1
```

Therefore:

```text
LCA(11, 9) = 1
```

---

# 7.8 Standard LCA Code

```cpp
int lca(int a, int b) {
    if (depth[a] < depth[b]) {
        swap(a, b);
    }

    int diff = depth[a] - depth[b];

    for (int j = 0; j < LOG; j++) {
        if (diff & (1 << j)) {
            a = up[a][j];
        }
    }

    if (a == b) {
        return a;
    }

    for (int j = LOG - 1; j >= 0; j--) {
        if (up[a][j] != up[b][j]) {
            a = up[a][j];
            b = up[b][j];
        }
    }

    return up[a][0];
}
```

Complexity:

```text
O(log N) per query
```

---

## 7.9 Lecture Progression: Binary Search View vs Final Binary Lifting View

The lecture also visualizes an `F F F T T T` style boundary:

```text
lower ancestors: still different
higher ancestors: become equal
```

That viewpoint can lead to a binary-search-style LCA solution.

Conceptually:

```text
jump height small     -> ancestors may differ
jump height large     -> ancestors eventually meet
```

But the standard competitive-programming implementation above directly scans binary lifting powers from high to low and finishes in:

```text
O(log N)
```

after:

```text
O(N log N)
```

preprocessing.

---

# 8. Distance Between Two Nodes

The lecture next uses LCA for distance queries.

Suppose:

```text
L = LCA(a, b)
```

Path from `a` to `b` is:

```text
a -> ... -> L -> ... -> b
```

Distance:

```text
distance(a, L) + distance(L, b)
```

Since depth counts edges from root:

```text
distance(a, L) = depth[a] - depth[L]

distance(b, L) = depth[b] - depth[L]
```

Therefore:

```text
dist(a, b)
= depth[a] - depth[L]
  + depth[b] - depth[L]

= depth[a] + depth[b] - 2 * depth[L]
```

Final formula:

```text
dist(a, b) =
depth[a] + depth[b] - 2 * depth[LCA(a, b)]
```

---

## 8.1 Visual Derivation

```text
                       root
                         |
                         |
                         L        <- LCA
                       /   \
                      /     \
                     /       \
                    a         b
```

Let:

```text
root -> L = dL
root -> a = da
root -> b = db
```

Then:

```text
L -> a = da - dL
L -> b = db - dL
```

So:

```text
a -> b
= (da - dL) + (db - dL)
= da + db - 2*dL
```

---

## 8.2 Example

Tree:

```text
                1
              /   \
             2     3
            / \     \
           4   5     6
              /
             7
```

Query:

```text
distance(4, 7)
```

Depths:

```text
depth[4] = 2
depth[7] = 3
```

LCA:

```text
LCA(4, 7) = 2
depth[2] = 1
```

Therefore:

```text
distance
= 2 + 3 - 2*1
= 5 - 2
= 3
```

Actual path:

```text
4 -> 2 -> 5 -> 7
```

3 edges. Correct.

---

## 8.3 Code

```cpp
int distanceBetweenNodes(int a, int b) {
    int common = lca(a, b);

    return depth[a] + depth[b] - 2 * depth[common];
}
```

Time:

```text
O(log N)
```

---

# 9. Minimum Value on a Path

The final lecture idea generalizes binary lifting.

Until now each jump stored only:

```text
ancestor
```

But a jump can store additional information about the whole segment.

Examples:

```text
minimum value
maximum value
sum
xor
gcd
```

The lecture specifically demonstrates:

```text
minimum value on a path
```

---

# 9.1 Core Decomposition

For query:

```text
minPath(a, b)
```

let:

```text
L = LCA(a, b)
```

Then:

```text
path(a, b)
=
path(a, L)
+
path(L, b)
```

Therefore:

```text
minPath(a, b)
=
min(
    minPath(a, L),
    minPath(b, L)
)
```

This is exactly why LCA is useful for path queries.

---

# 9.2 Add Another Binary Lifting Table

Earlier:

```text
up[node][j]
```

stored:

```text
2^j-th ancestor
```

Now also store:

```text
mn[node][j]
```

meaning:

> minimum value encountered while jumping `2^j` levels upward from `node`.

Conceptually:

```text
jump[node][j] =
{
    ancestor after 2^j steps,
    minimum on those 2^j steps
}
```

---

## 9.3 Visual Example

Suppose edge weights are:

```text
1
|
| 8
2
|
| 4
3
|
| 6
4
|
| 2
5
```

For node `5`:

```text
up[5][0] = 4
mn[5][0] = 2
```

Jump of 2:

```text
5 -> 4 -> 3

edge values = {2, 6}

up[5][1] = 3
mn[5][1] = min(2, 6) = 2
```

Jump of 4:

```text
5 -> 4 -> 3 -> 2 -> 1

edge values = {2, 6, 4, 8}

up[5][2] = 1
mn[5][2] = 2
```

---

# 9.4 Recurrence for the Minimum Table

We already know:

```text
up[node][j]
=
up[ up[node][j-1] ][j-1]
```

For minimum:

First half:

```text
node
-- 2^(j-1) -->
mid
```

has minimum:

```text
mn[node][j-1]
```

Second half:

```text
mid
-- 2^(j-1) -->
ancestor
```

has minimum:

```text
mn[mid][j-1]
```

Therefore:

```text
mn[node][j]
=
min(
    mn[node][j-1],
    mn[ up[node][j-1] ][j-1]
)
```

This is the exact same doubling idea as the ancestor recurrence.

---

# 9.5 Important Interpretation

The lecture’s main abstraction is:

```text
Binary lifting is not only about parents.

It is about precomputing information for jumps of:
1, 2, 4, 8, 16, ...
```

So you can store:

```text
ancestor
minimum
maximum
sum
xor
...
```

as long as the information can be combined across two half-jumps.

---

# 9.6 Querying Minimum While Lifting

Suppose we want to move node `a` upward by `diff`.

Maintain:

```text
answer = INF
```

For every set bit:

```text
answer = min(answer, mn[a][j])
a = up[a][j]
```

So while jumping, we simultaneously collect the minimum.

---

## 9.7 Example Dry Run

Suppose:

```text
a = 9
LCA = 2
depth[9] = 5
depth[2] = 1
```

Need to move:

```text
diff = 4
```

Binary:

```text
4 = 2^2
```

So one jump:

```text
answer = min(answer, mn[9][2])
a = up[9][2]
```

Now `a` reaches node `2`.

The stored `mn[9][2]` already summarizes all four edges crossed.

---

# 9.8 Edge Values vs Node Values

The lecture demonstrates the general path-minimum technique.

Two common variants exist:

### Edge-weight version

```text
mn[node][0] = weight(node -> parent[node])
```

This is the most natural version when edges carry values.

### Node-value version

If nodes carry values, initialization changes slightly because you must decide whether both endpoints are included.

The binary lifting idea itself remains identical:

```text
combine information over 2^(j-1)
+
combine information over next 2^(j-1)
```

---

# 10. Complete C++17 Template

This template includes:

- binary lifting preprocessing
- k-th ancestor
- LCA
- distance query

```cpp
#include <bits/stdc++.h>
using namespace std;

class BinaryLifting {
private:
    int n;
    int LOG;
    vector<vector<int>> graph;
    vector<vector<int>> up;
    vector<int> depth;

    void dfs(int node, int parent) {
        up[node][0] = parent;

        for (int j = 1; j < LOG; j++) {
            up[node][j] = up[up[node][j - 1]][j - 1];
        }

        for (int next : graph[node]) {
            if (next == parent) {
                continue;
            }

            depth[next] = depth[node] + 1;
            dfs(next, node);
        }
    }

public:
    BinaryLifting(int nodes) {
        n = nodes;
        LOG = 1;

        while ((1 << LOG) <= n) {
            LOG++;
        }

        graph.assign(n + 1, {});
        up.assign(n + 1, vector<int>(LOG));
        depth.assign(n + 1, 0);
    }

    void addEdge(int u, int v) {
        graph[u].push_back(v);
        graph[v].push_back(u);
    }

    void build(int root = 1) {
        depth[root] = 0;
        dfs(root, root);
    }

    int kthAncestor(int node, int k) {
        for (int j = 0; j < LOG; j++) {
            if (k & (1 << j)) {
                node = up[node][j];
            }
        }

        return node;
    }

    int lca(int a, int b) {
        if (depth[a] < depth[b]) {
            swap(a, b);
        }

        int diff = depth[a] - depth[b];

        for (int j = 0; j < LOG; j++) {
            if (diff & (1 << j)) {
                a = up[a][j];
            }
        }

        if (a == b) {
            return a;
        }

        for (int j = LOG - 1; j >= 0; j--) {
            if (up[a][j] != up[b][j]) {
                a = up[a][j];
                b = up[b][j];
            }
        }

        return up[a][0];
    }

    int distanceBetweenNodes(int a, int b) {
        int common = lca(a, b);

        return depth[a] + depth[b] - 2 * depth[common];
    }
};
```

---

# 10.1 Example Usage

```cpp
int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    BinaryLifting tree(n);

    for (int i = 0; i < n - 1; i++) {
        int u, v;
        cin >> u >> v;

        tree.addEdge(u, v);
    }

    tree.build(1);

    cout << tree.kthAncestor(8, 2) << '\n';
    cout << tree.lca(8, 9) << '\n';
    cout << tree.distanceBetweenNodes(8, 9) << '\n';

    return 0;
}
```

---

# 10.2 Path-Minimum Extension Skeleton

For edge-weight queries:

```cpp
vector<vector<int>> up;
vector<vector<long long>> mn;
```

Base:

```cpp
up[node][0] = parent;
mn[node][0] = edgeWeightToParent;
```

Transition:

```cpp
for (int j = 1; j < LOG; j++) {
    int mid = up[node][j - 1];

    up[node][j] = up[mid][j - 1];
    mn[node][j] = min(mn[node][j - 1], mn[mid][j - 1]);
}
```

While lifting:

```cpp
long long answer = LLONG_MAX;

for (int j = LOG - 1; j >= 0; j--) {
    if (needJumpBy2PowerJ) {
        answer = min(answer, mn[node][j]);
        node = up[node][j];
    }
}
```

The same pattern works for other associative aggregates.

---

# 11. Common Mistakes

## Mistake 1 — Forgetting to equalize depths

Wrong:

```text
Immediately lift both nodes together.
```

Correct:

```text
1. Make depths equal.
2. Then lift both.
```

---

## Mistake 2 — Forgetting `a == b`

After equalizing depth:

```cpp
if (a == b) {
    return a;
}
```

Without this, ancestor-descendant queries can fail.

---

## Mistake 3 — Moving both nodes when their big ancestors are equal

Correct condition:

```cpp
if (up[a][j] != up[b][j]) {
    a = up[a][j];
    b = up[b][j];
}
```

Why?

Because if the ancestors are already equal, that jump may cross the LCA.

---

## Mistake 4 — Wrong recurrence

Wrong:

```text
up[node][j] = up[node][j - 1]
```

Correct:

```text
up[node][j]
=
up[ up[node][j - 1] ][j - 1]
```

Think:

```text
half jump + half jump
```

---

## Mistake 5 — Too Small LOG

For `N = 100000`:

```text
LOG = 10
```

is not enough.

Use dynamic computation or a safe constant.

---

## Mistake 6 — Root Parent Handling

A convenient convention is:

```text
parent[root] = root
```

Then every jump remains valid:

```text
up[root][j] = root
```

and we avoid special `-1` checks everywhere.

---

## Mistake 7 — Confusing `depth` with subtree size

Binary lifting uses:

```text
depth[node]
```

for vertical position.

It does not mean:

```text
number of nodes in subtree
```

They are completely different concepts.

---

# 12. Recognition Guide

## Pattern A — k-th Ancestor

Words like:

```text
k-th parent
k-th ancestor
jump k levels upward
```

Use:

```text
Binary lifting
```

---

## Pattern B — LCA

Words like:

```text
lowest common ancestor
first common manager
first common parent
meeting point of two upward paths
```

Use:

```text
LCA
```

---

## Pattern C — Distance on Tree

Words like:

```text
distance between u and v
number of edges on path
path length
```

Think:

```text
L = LCA(u, v)

distance =
depth[u] + depth[v] - 2*depth[L]
```

---

## Pattern D — Path Minimum / Maximum

Words like:

```text
minimum edge on path
maximum edge on path
minimum value while going upward
```

Think:

```text
Binary lifting + aggregate table
```

---

## Pattern E — Many Static Tree Queries

If:

```text
tree does not change
Q is large
queries repeatedly ask ancestor/path information
```

precomputation is usually worth it.

Think:

```text
O(N log N) preprocessing
+
O(log N) per query
```

---

# 13. Final Mental Model

Do not memorize Binary Lifting as a complicated 2D table.

Remember this picture:

```text
ONE-STEP PARENT

node -> parent
```

becomes:

```text
POWER-OF-TWO JUMPS

node --1--> ...
node --2--> ...
node --4--> ...
node --8--> ...
node --16--> ...
```

The table stores those jumps.

Then every large upward movement becomes a combination of powers of two.

```text
13 levels
=
8 + 4 + 1
```

That gives:

```text
k-th ancestor
      |
      v
Binary Lifting
      |
      +--> LCA
             |
             +--> Distance
             |
             +--> Path Minimum
             |
             +--> Path Maximum
             |
             +--> Other jump-based aggregates
```

The entire topic can be reduced to three formulas:

```text
1. up[v][0] = parent[v]

2. up[v][j]
   = up[ up[v][j-1] ][j-1]

3. dist(a,b)
   = depth[a] + depth[b]
     - 2 * depth[LCA(a,b)]
```

And one LCA strategy:

```text
Step 1: make depths equal

Step 2: if nodes are equal -> answer

Step 3: from large jump to small jump,
        move both only when their jumped ancestors differ

Step 4: parent of either node = LCA
```

---

## One-Screen Revision

```text
BINARY LIFTING
==============

up[v][j] = 2^j-th ancestor of v

up[v][0] = parent[v]

up[v][j]
= up[ up[v][j-1] ][j-1]


K-TH ANCESTOR
==============

write k in binary
jump for every set bit


LCA
===

1. deeper node -> lift to same depth
2. if same node -> return
3. j from LOG-1 to 0:
      if up[a][j] != up[b][j]:
          a = up[a][j]
          b = up[b][j]
4. return up[a][0]


DISTANCE
========

L = LCA(a,b)

dist(a,b)
= depth[a] + depth[b] - 2*depth[L]


PATH AGGREGATE
==============

store extra information for each 2^j jump:

ancestor
minimum / maximum / sum / xor / ...

combine two 2^(j-1) jumps
to build one 2^j jump
```
