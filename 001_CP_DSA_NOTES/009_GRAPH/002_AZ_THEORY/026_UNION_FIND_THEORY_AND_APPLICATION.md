# Union Find (DSU) — Theory and Applications

> **Self-study Competitive Programming Note**
>
> Goal: understand **Disjoint Set Union / Union Find** from first principles:
>
> - what problem DSU solves,
> - how `find()` works,
> - how `merge()` / `union()` works,
> - why path compression helps,
> - why union by size/rank helps,
> - why the complexity becomes almost constant,
> - how DSU is used for connected components and offline graph problems.

---

# Table of Contents

1. [What Problem Does Union Find Solve?](#1-what-problem-does-union-find-solve)
2. [Prerequisite — Connected Components](#2-prerequisite--connected-components)
3. [Prerequisite — Representative of a Component](#3-prerequisite--representative-of-a-component)
4. [Core DSU Operations](#4-core-dsu-operations)
5. [Start With Parent Array](#5-start-with-parent-array)
6. [Find Operation — Step by Step](#6-find-operation--step-by-step)
7. [Merge / Union Operation — Step by Step](#7-merge--union-operation--step-by-step)
8. [Small Dry Run](#8-small-dry-run)
9. [Why Naive DSU Can Become Slow](#9-why-naive-dsu-can-become-slow)
10. [Optimization 1 — Path Compression](#10-optimization-1--path-compression)
11. [Path Compression Dry Run](#11-path-compression-dry-run)
12. [Optimization 2 — Union by Size](#12-optimization-2--union-by-size)
13. [Union by Rank](#13-union-by-rank)
14. [Why Union by Size/Rank Works](#14-why-union-by-sizerank-works)
15. [Final Time Complexity](#15-final-time-complexity)
16. [Clean C++17 DSU Template](#16-clean-c17-dsu-template)
17. [Application 1 — Count Connected Components](#17-application-1--count-connected-components)
18. [Application 2 — Dynamic Connectivity](#18-application-2--dynamic-connectivity)
19. [Application 3 — Detect Cycle in an Undirected Graph](#19-application-3--detect-cycle-in-an-undirected-graph)
20. [Application 4 — Kruskal MST](#20-application-4--kruskal-mst)
21. [Application 5 — Offline Connectivity Queries](#21-application-5--offline-connectivity-queries)
22. [Application 6 — Minimum Edge on a Tree Path Idea](#22-application-6--minimum-edge-on-a-tree-path-idea)
23. [How the Tree-Path Idea Becomes an Offline DSU Problem](#23-how-the-tree-path-idea-becomes-an-offline-dsu-problem)
24. [Common Mistakes](#24-common-mistakes)
25. [Recognition Checklist](#25-recognition-checklist)
26. [Final Mental Model](#26-final-mental-model)

---

# 1. What Problem Does Union Find Solve?

Suppose we have nodes:

```text
1  2  3  4  5  6
```

Initially every node is separate:

```text
{1} {2} {3} {4} {5} {6}
```

Now queries arrive:

```text
merge(1, 3)
merge(2, 4)
merge(3, 4)
```

We need to answer questions like:

```text
Are 1 and 4 in the same component?
How many connected components are left?
What is the representative of node 3?
```

This is exactly what **Disjoint Set Union (DSU)** solves.

Other names:

```text
Union Find
Disjoint Set Union
DSU
```

The data structure maintains a collection of:

```text
disjoint sets
```

where every element belongs to exactly one set.

---

# 2. Prerequisite — Connected Components

Consider:

```text
1 -- 2 -- 3

4 -- 5

6
```

There are three connected components:

```text
C1 = {1,2,3}
C2 = {4,5}
C3 = {6}
```

So:

```text
number of components = 3
```

DSU is useful when edges are gradually added and we only care about:

```text
which nodes belong to the same component?
```

DSU does **not** store the actual path between nodes.

It stores only connectivity information.

---

# 3. Prerequisite — Representative of a Component

For each connected component, DSU chooses one node as its:

```text
representative
```

Example:

```text
Component: {1,2,3}

Representative = 1
```

Then:

```text
find(1) = 1
find(2) = 1
find(3) = 1
```

For another component:

```text
Component: {4,5}

Representative = 4
```

Then:

```text
find(4) = 4
find(5) = 4
```

So the key connectivity test is:

```text
x and y are connected
        ⇔
find(x) == find(y)
```

This is the core idea.

---

# 4. Core DSU Operations

Union Find has two important operations.

## Operation 1 — Find

```text
find(x)
```

returns:

```text
the representative/root of x's component
```

---

## Operation 2 — Merge / Union

```text
merge(x, y)
```

combines the two components containing:

```text
x
y
```

If they are already in the same component:

```text
do nothing
```

---

# 5. Start With Parent Array

Initially every node is its own parent.

For:

```text
0 1 2 3
```

we store:

```text
parent = [0,1,2,3]
```

Visual:

```text
0      1      2      3
↑      ↑      ↑      ↑
self   self   self   self
```

Meaning:

```text
parent[0] = 0
parent[1] = 1
parent[2] = 2
parent[3] = 3
```

A node whose parent is itself is a root.

Condition:

```cpp
parent[x] == x
```

means:

```text
x is the representative
```

---

# 6. Find Operation — Step by Step

Suppose DSU structure is:

```text
1 → 3 → 5
```

Here:

```text
parent[1] = 3
parent[3] = 5
parent[5] = 5
```

To compute:

```text
find(1)
```

follow parents:

```text
1
↓
3
↓
5
```

At node `5`:

```text
parent[5] == 5
```

Therefore:

```text
find(1) = 5
```

---

## Recursive form

```cpp
int find(int x) {
    if (parent[x] == x) {
        return x;
    }

    return find(parent[x]);
}
```

Conceptually:

```text
find(x)

if x is root:
    return x

otherwise:
    ask parent for its root
```

---

# 7. Merge / Union Operation — Step by Step

Suppose:

```text
Component A:
1 → 3

Component B:
2 → 4
```

Representatives:

```text
find(1) = 3
find(2) = 4
```

Now:

```text
merge(1, 2)
```

---

## Step 1 — Find roots

```text
rootX = find(1) = 3
rootY = find(2) = 4
```

---

## Step 2 — Already same?

If:

```text
rootX == rootY
```

then the nodes are already connected.

No merge needed.

---

## Step 3 — Connect one root to the other

For a naive union:

```text
parent[rootX] = rootY
```

So:

```text
3 → 4
```

Now both components become one.

Final:

```text
1 → 3 → 4
2 → 4
```

Representative:

```text
4
```

---

# 8. Small Dry Run

Start:

```text
parent = [0,1,2,3,4,5]
components = 6
```

Operations:

```text
merge(1,3)
merge(2,4)
merge(1,2)
```

---

## merge(1,3)

```text
find(1)=1
find(3)=3

different roots
```

Merge:

```text
1 → 3
```

Now:

```text
{1,3} {2} {4} {5} {0}
```

Components:

```text
5
```

---

## merge(2,4)

```text
find(2)=2
find(4)=4
```

Merge:

```text
2 → 4
```

Now:

```text
{1,3} {2,4} {5} {0}
```

Components:

```text
4
```

---

## merge(1,2)

First:

```text
find(1)
1 → 3
root = 3

find(2)
2 → 4
root = 4
```

Merge the roots:

```text
3 → 4
```

Final component:

```text
{1,2,3,4}
```

Now:

```text
find(1) = 4
find(2) = 4
find(3) = 4
find(4) = 4
```

---

# 9. Why Naive DSU Can Become Slow

Suppose we repeatedly attach trees badly:

```text
1 → 2 → 3 → 4 → 5
```

To compute:

```text
find(1)
```

we traverse:

```text
1 → 2 → 3 → 4 → 5
```

Cost:

```text
O(N)
```

If this happens for many operations, DSU becomes slow.

Two optimizations solve this:

```text
1. Path Compression
2. Union by Size / Rank
```

---

# 10. Optimization 1 — Path Compression

Suppose:

```text
1 → 2 → 3 → 4 → 5
```

Calling:

```text
find(1)
```

discovers that root is `5`.

Path compression changes every visited node to point directly to the root.

Before:

```text
1 → 2 → 3 → 4 → 5
```

After:

```text
1 ─┐
2 ─┤
3 ─┼──→ 5
4 ─┘
```

Now future queries are much faster.

---

## Path compression code

```cpp
int find(int x) {
    if (parent[x] == x) {
        return x;
    }

    return parent[x] = find(parent[x]);
}
```

Important line:

```cpp
parent[x] = find(parent[x]);
```

This does two things:

```text
1. Finds the root.
2. Rewrites parent[x] directly to the root.
```

---

# 11. Path Compression Dry Run

Initial:

```text
1 → 2 → 3 → 4 → 5
```

Call:

```text
find(1)
```

Recursive calls:

```text
find(1)
→ find(2)
→ find(3)
→ find(4)
→ find(5)
```

At:

```text
5
```

we have:

```text
parent[5] = 5
```

Return:

```text
5
```

During recursion unwind:

```text
parent[4] = 5
parent[3] = 5
parent[2] = 5
parent[1] = 5
```

Final:

```text
1 → 5
2 → 5
3 → 5
4 → 5
5 → 5
```

One expensive `find` makes later finds cheap.

---

# 12. Optimization 2 — Union by Size

Even with path compression, we should avoid creating tall trees.

Store:

```text
size[root]
```

meaning:

```text
how many nodes belong to this component
```

When merging:

```text
small tree
    ↓
attach under
    ↓
large tree
```

Never do the opposite.

---

## Example

Suppose:

```text
Component A size = 5
Component B size = 2
```

Attach:

```text
B under A
```

not:

```text
A under B
```

Why?

Because only nodes from the smaller component increase their depth.

---

## Union by size code

```cpp
bool merge(int x, int y) {
    x = find(x);
    y = find(y);

    if (x == y) {
        return false;
    }

    if (size[x] < size[y]) {
        swap(x, y);
    }

    parent[y] = x;
    size[x] += size[y];

    return true;
}
```

After the swap:

```text
size[x] >= size[y]
```

So:

```text
y becomes child of x
```

---

# 13. Union by Rank

Another common optimization is:

```text
union by rank
```

`rank[root]` estimates the height of the tree.

Rule:

```text
smaller-rank tree
        ↓
attach to
        ↓
larger-rank tree
```

If both ranks are equal:

```text
choose one root
increase its rank by 1
```

---

## Example

```text
rank[x] = 3
rank[y] = 1
```

Attach:

```text
y → x
```

---

## If equal

```text
rank[x] = 2
rank[y] = 2
```

Attach:

```text
y → x
```

Then:

```text
rank[x] = 3
```

---

## Rank implementation

```cpp
bool merge(int x, int y) {
    x = find(x);
    y = find(y);

    if (x == y) {
        return false;
    }

    if (rank[x] < rank[y]) {
        swap(x, y);
    }

    parent[y] = x;

    if (rank[x] == rank[y]) {
        rank[x]++;
    }

    return true;
}
```

For most CP problems, either:

```text
union by size
```

or:

```text
union by rank
```

is enough.

You do not need both.

---

# 14. Why Union by Size/Rank Works

Consider union by size.

Suppose node `x` becomes deeper because its entire component was attached under another component.

That only happens when:

```text
its component was the smaller one
```

Therefore the new component size is at least:

```text
2 × old size
```

If a node's depth increases `k` times:

```text
component size >= 2^k
```

But component size cannot exceed:

```text
N
```

So:

```text
2^k <= N
```

Take log:

```text
k <= log2(N)
```

Therefore without path compression, union by size/rank already keeps depth around:

```text
O(log N)
```

Path compression makes it even better.

---

# 15. Final Time Complexity

With both:

```text
path compression
+
union by size/rank
```

the amortized complexity of each operation is:

```text
O(α(N))
```

where:

```text
α(N)
```

is the inverse Ackermann function.

For every realistic competitive-programming input:

```text
α(N) <= about 4 or 5
```

So practically:

```text
find ≈ O(1)
merge ≈ O(1)
```

More precisely:

```text
M DSU operations
=
O(M α(N))
```

This is why DSU is extremely fast.

---

# 16. Clean C++17 DSU Template

This version uses:

```text
path compression
+
union by size
```

```cpp
#include <bits/stdc++.h>
using namespace std;

struct DSU {
    vector<int> parent;
    vector<int> size;
    int components;

    DSU(int n) {
        parent.resize(n);
        size.assign(n, 1);
        components = n;

        iota(parent.begin(), parent.end(), 0);
    }

    int find(int x) {
        if (parent[x] == x) {
            return x;
        }

        return parent[x] = find(parent[x]);
    }

    bool merge(int x, int y) {
        x = find(x);
        y = find(y);

        if (x == y) {
            return false;
        }

        if (size[x] < size[y]) {
            swap(x, y);
        }

        parent[y] = x;
        size[x] += size[y];
        components--;

        return true;
    }

    bool same(int x, int y) {
        return find(x) == find(y);
    }

    int componentSize(int x) {
        return size[find(x)];
    }
};
```

This should be your default DSU template.

---

# 17. Application 1 — Count Connected Components

Suppose graph has:

```text
N nodes
M edges
```

Initially:

```text
components = N
```

For every edge:

```text
(u,v)
```

try:

```text
merge(u,v)
```

If merge succeeds:

```text
two different components became one
```

so:

```text
components--
```

If merge fails:

```text
u and v were already connected
```

so number of components does not change.

---

## Example

```text
N = 4

Edges:
(3,4)
(1,3)
(2,3)
(1,2)
```

Start:

```text
components = 4
```

Process:

```text
(3,4) → merge succeeds → 3
(1,3) → merge succeeds → 2
(2,3) → merge succeeds → 1
(1,2) → already connected → 1
```

Final:

```text
1 connected component
```

---

## Code

```cpp
int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    DSU dsu(n + 1);

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;

        dsu.merge(u, v);
    }

    cout << dsu.components - 1 << '\n';

    return 0;
}
```

If using nodes `1..N`, construct carefully so the unused `0` does not affect your count.

A cleaner version is:

```text
store an explicit component counter = N
```

outside or inside DSU.

---

# 18. Application 2 — Dynamic Connectivity

A very common query format is:

```text
1 x y → connect x and y
2 x y → ask whether x and y are connected
```

Example:

```text
merge(1,3)
merge(2,4)
same(1,4)?
merge(3,4)
same(1,4)?
```

Dry run:

```text
merge(1,3)
Components: {1,3} {2} {4}

merge(2,4)
Components: {1,3} {2,4}

same(1,4)
find(1) != find(4)
answer = NO

merge(3,4)
Components: {1,2,3,4}

same(1,4)
find(1) == find(4)
answer = YES
```

---

## Code

```cpp
while (q--) {
    int type, x, y;
    cin >> type >> x >> y;

    if (type == 1) {
        dsu.merge(x, y);
    } else {
        cout << (dsu.same(x, y) ? "YES" : "NO") << '\n';
    }
}
```

---

# 19. Application 3 — Detect Cycle in an Undirected Graph

Consider adding an edge:

```text
u -- v
```

Before adding it, check:

```text
find(u) == find(v)?
```

If yes:

```text
u and v already have a path between them
```

Adding another edge creates a cycle.

So:

```text
same component before insertion
        ↓
new edge creates cycle
```

---

## Code idea

```cpp
bool hasCycle = false;

for (auto [u, v] : edges) {
    if (dsu.same(u, v)) {
        hasCycle = true;
        break;
    }

    dsu.merge(u, v);
}
```

---

# 20. Application 4 — Kruskal MST

DSU is one of the main tools behind Kruskal's Minimum Spanning Tree algorithm.

Process edges in increasing weight.

For every edge:

```text
(u,v,w)
```

If:

```text
u and v are already connected
```

skip it because it creates a cycle.

Otherwise:

```text
take the edge
merge(u,v)
```

Core:

```cpp
sort(edges.begin(), edges.end());

for (auto [weight, u, v] : edges) {
    if (dsu.same(u, v)) {
        continue;
    }

    dsu.merge(u, v);
    mstCost += weight;
}
```

DSU answers:

```text
Will this edge connect two different components?
```

very quickly.

---

# 21. Application 5 — Offline Connectivity Queries

Sometimes queries ask connectivity under a threshold.

Example:

```text
Are u and v connected using only edges with weight <= X?
```

Instead of running DFS/BFS for every query:

1. Sort edges by weight.
2. Sort queries by threshold `X`.
3. Add all edges whose weight is allowed.
4. Use DSU to test connectivity.

---

## Example

Edges:

```text
(1,2,2)
(2,3,5)
(3,4,8)
```

Query:

```text
Are 1 and 3 connected using edges <= 5?
```

Process allowed edges:

```text
weight 2 → merge(1,2)
weight 5 → merge(2,3)
```

Now:

```text
find(1) == find(3)
```

So:

```text
YES
```

The key pattern is:

```text
sort by a monotonic threshold
+
incrementally merge
```

---

# 22. Application 6 — Minimum Edge on a Tree Path Idea

The lecture also shows a tree with weighted edges and a quantity like:

```text
d(i,j)
=
minimum edge value on the path from i to j
```

Example path:

```text
2 --3-- 3 --4-- 4 --1-- 5
```

For:

```text
d(2,4)
```

path edge weights are:

```text
3,4
```

Therefore:

```text
d(2,4) = min(3,4) = 3
```

For:

```text
d(2,5)
```

path edge weights:

```text
3,4,1
```

Therefore:

```text
d(2,5) = min(3,4,1) = 1
```

This is a different type of problem from ordinary connectivity.

But DSU can still help when processed offline.

---

# 23. How the Tree-Path Idea Becomes an Offline DSU Problem

Suppose we want to reason about all pairs whose path minimum is at least some value.

Process tree edges in:

```text
descending weight
```

Why descending?

Because when we are currently processing edge weight `w`, every edge already added has weight:

```text
>= w
```

Therefore nodes connected in the DSU have a path whose minimum edge is at least:

```text
w
```

---

## Crucial contribution formula

Suppose an edge of weight `w` connects:

```text
Component A of size a
Component B of size b
```

Before adding the edge:

```text
A and B are separate
```

After adding it:

```text
every node in A
connects to
every node in B
```

Number of newly connected pairs:

```text
a × b
```

For all those newly connected pairs, this edge weight `w` becomes the minimum edge on their path.

So contribution:

```text
w × a × b
```

This is a powerful DSU counting pattern.

---

## Tiny example

Tree:

```text
1 --4-- 2 --3-- 3
```

Sort descending:

```text
4
3
```

Initially:

```text
{1} {2} {3}
```

Process weight `4`:

```text
merge(1,2)

a=1
b=1

new pairs = 1×1 = 1
contribution = 4×1 = 4
```

Now component:

```text
{1,2}
```

size:

```text
2
```

Process weight `3`:

```text
merge({1,2},3)

a=2
b=1

new pairs = 2×1 = 2
contribution = 3×2 = 6
```

Total:

```text
4 + 6 = 10
```

The pair values are:

```text
d(1,2)=4
d(1,3)=3
d(2,3)=3

sum=10
```

Exactly matches.

---

## Why `sizeA × sizeB`?

Suppose:

```text
A = {1,2,5}
B = {3,4}
```

Then newly connected cross-component pairs are:

```text
(1,3)
(1,4)
(2,3)
(2,4)
(5,3)
(5,4)
```

Count:

```text
3 × 2 = 6
```

So:

```text
number of new pairs
=
sizeA × sizeB
```

This is one of the most important DSU counting formulas.

---

# 24. Common Mistakes

## Mistake 1 — Merging x and y directly

Wrong:

```cpp
parent[x] = y;
```

Correct:

```cpp
x = find(x);
y = find(y);
```

Then merge the roots.

---

## Mistake 2 — Forgetting same-root check

Always:

```cpp
if (x == y) {
    return false;
}
```

Otherwise size/rank may become incorrect.

---

## Mistake 3 — Updating size of the wrong root

If:

```text
parent[y] = x
```

then:

```cpp
size[x] += size[y];
```

not the reverse.

---

## Mistake 4 — Using rank as exact tree height after path compression

After path compression:

```text
rank is not necessarily the exact height
```

Treat it only as a balancing heuristic.

---

## Mistake 5 — Thinking DSU gives the actual path

DSU answers:

```text
connected or not
```

It does not store:

```text
the route/path between nodes
```

Use DFS/BFS/LCA/etc. if actual path information is needed.

---

## Mistake 6 — Using DSU for directed connectivity

Standard DSU represents:

```text
undirected equivalence / connectivity
```

It is not a general solution for directed reachability.

---

# 25. Recognition Checklist

Think DSU when the problem contains:

```text
merge groups
connect two nodes
same component?
number of components
cycle detection
MST / Kruskal
offline threshold queries
activate edges gradually
group size after merges
```

Especially strong signal:

```text
Many queries:
merge(x,y)
same(x,y)?
```

This is almost directly:

```text
Union Find
```

---

## Offline DSU recognition

Think offline DSU when you see:

```text
sort edges by weight
process threshold from small→large
process threshold from large→small
when edge becomes available, connectivity only increases
```

Then:

```text
sort
+
merge
+
answer queries
```

is often the right model.

---

# 26. Final Mental Model

Remember DSU as:

```text
Each component has one ROOT.

find(x)
    ↓
Which component does x belong to?

merge(x,y)
    ↓
Combine their two roots.
```

The optimized structure is:

```text
PATH COMPRESSION
      +
UNION BY SIZE/RANK
      ↓
almost O(1) per operation
```

---

## Visual memory

Before:

```text
1 → 2 → 3 → 4 → 5
```

After path compression:

```text
1 ─┐
2 ─┤
3 ─┼──→ 5
4 ─┘
```

When merging:

```text
small tree
   ↓
large tree
```

---

## Application memory

```text
DSU
│
├── connectivity
├── component count
├── cycle detection
├── Kruskal MST
├── offline threshold queries
└── component-size counting
       │
       └── sizeA × sizeB
```

The key contest idea is:

```text
If the only thing you need to know is
"which group/component does this node belong to?"
and groups only MERGE,

think DSU.
```
