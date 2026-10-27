# DFS Applications — Connected Components, Component Queries & Pair Counting

> **Lecture goal:** use one DFS preprocessing pass to understand the connected-component structure of an undirected graph, then answer many questions efficiently.
>
> Core transformation:
>
> ```text
> basic DFS
>     ↓
> give every connected component an ID
>     ↓
> reuse that ID for:
>     • number of components
>     • same-component queries
>     • component sizes
>     • counting pairs from different components
> ```

---

## Table of Contents

1. [What This Lecture Is Really Teaching](#1-what-this-lecture-is-really-teaching)
2. [Prerequisite 1 — Explicit vs Implicit Graph](#2-prerequisite-1--explicit-vs-implicit-graph)
3. [Prerequisite 2 — Edge List to Adjacency List](#3-prerequisite-2--edge-list-to-adjacency-list)
4. [Prerequisite 3 — Basic DFS](#4-prerequisite-3--basic-dfs)
5. [Prerequisite 4 — Connected Components](#5-prerequisite-4--connected-components)
6. [Application 1 — Count Connected Components](#6-application-1--count-connected-components)
7. [Why Boolean `visited` Is Sometimes Not Enough](#7-why-boolean-visited-is-sometimes-not-enough)
8. [Component Coloring / Component IDs](#8-component-coloring--component-ids)
9. [Step-by-Step Component Labeling Dry Run](#9-step-by-step-component-labeling-dry-run)
10. [Application 2 — Same Component Queries](#10-application-2--same-component-queries)
11. [Why Preprocessing Makes Queries Fast](#11-why-preprocessing-makes-queries-fast)
12. [Application 3 — Component Sizes](#12-application-3--component-sizes)
13. [Application 4 — Which New Connections Reduce the Number of Components?](#13-application-4--which-new-connections-reduce-the-number-of-components)
14. [Counting Cross-Component Pairs — Direct Method](#14-counting-cross-component-pairs--direct-method)
15. [Lecture Example: Component Sizes 5, 3, 4](#15-lecture-example-component-sizes-5-3-4)
16. [Alternative Formula Using Combinations](#16-alternative-formula-using-combinations)
17. [Alternative Algebraic Derivation](#17-alternative-algebraic-derivation)
18. [Best O(C) Counting Method](#18-best-oc-counting-method)
19. [Complete C++17 — Component IDs + Queries](#19-complete-c17--component-ids--queries)
20. [Complete C++17 — Count Cross-Component Pairs](#20-complete-c17--count-cross-component-pairs)
21. [Complexity](#21-complexity)
22. [Common Mistakes](#22-common-mistakes)
23. [Recognition Checklist](#23-recognition-checklist)
24. [Final Mental Model](#24-final-mental-model)

---

# 1. What This Lecture Is Really Teaching

There are usually **three layers** in a graph problem.

```text
Problem statement
      │
      ▼
Where is the graph?
      │
      └── Graph formulation
              │
              ▼
Which graph idea solves it?
              │
              └── DFS / Connected Components
                      │
                      ▼
How should we code it?
                      │
                      └── adjacency list + component labels
```

The important competitive-programming skill is not only knowing DFS.

It is recognizing:

```text
"This problem is really asking about connected components."
```

Typical clues:

```text
group
network
island
region
cluster
same group?
can x reach y?
how many disconnected groups?
if I connect x and y, does connectivity change?
```

---

# 2. Prerequisite 1 — Explicit vs Implicit Graph

The lecture separates graphs into two broad ways they may appear.

```text
Graph
├── Explicit
└── Implicit
```

---

## 2.1 Explicit Graph

The problem directly gives vertices and edges.

Example:

```text
N = 7
M = 6

Edges:
1 2
2 3
3 4
2 6
6 7
4 5
```

You can immediately draw:

```text
(1) -- (2) -- (3) -- (4) -- (5)
        |
       (6) -- (7)
```

The graph is already visible in the input.

---

## 2.2 Implicit Graph

The problem may **not** give a list of graph edges directly.

Instead, the problem gives some state and a rule saying when two states are connected.

General model:

```text
state/object   → vertex
valid move     → edge
```

For example, if a problem says:

```text
"From state X, you may move to states satisfying some rule."
```

then those states and moves form a graph even if the word **graph** never appears.

The important thought is:

```text
Do not search only for the word "graph".

Search for:
"objects + relationships / transitions"
```

---

# 3. Prerequisite 2 — Edge List to Adjacency List

The lecture starts from an edge list and converts it into an adjacency list.

Suppose the undirected edges are:

```text
1 2
2 3
3 4
4 2
2 6
6 7
```

Graph:

```text
        (1)
         |
        (2) ----- (6) ----- (7)
       /   \
     (3)---(4)
```

---

## Edge List

Input representation:

```text
(1,2)
(2,3)
(3,4)
(4,2)
(2,6)
(6,7)
```

---

## Adjacency List

Convert it to:

```text
1 → [2]
2 → [1,3,4,6]
3 → [2,4]
4 → [3,2]
5 → []
6 → [2,7]
7 → [6]
```

For every undirected edge:

```text
a -- b
```

store both directions:

```cpp
g[a].push_back(b);
g[b].push_back(a);
```

---

## Why DFS Wants an Adjacency List

At node `u`, DFS asks:

```text
"Who are all the neighbors of u?"
```

Adjacency list answers exactly that:

```cpp
for (int neighbor : g[node]) {
    ...
}
```

---

# 4. Prerequisite 3 — Basic DFS

Basic DFS skeleton:

```text
DFS(x)

1. mark x visited
2. inspect every neighbor y of x
3. if y is not visited:
       DFS(y)
```

Graph:

```text
        (2)
       /   \
     (1)   (3)
            |
           (4)
          /   \
        (5)   (6)
               |
              (7)
```

Possible DFS flow:

```text
2 → 1 → back → 3 → 4 → 5 → back → 6 → 7
```

Recursive view:

```text
dfs(2)
├── dfs(1)
└── dfs(3)
    └── dfs(4)
        ├── dfs(5)
        └── dfs(6)
            └── dfs(7)
```

---

## Basic C++17 DFS

```cpp
vector<vector<int>> g;
vector<int> visited;

void dfs(int node) {
    visited[node] = 1;

    for (int neighbor : g[node]) {
        if (!visited[neighbor]) {
            dfs(neighbor);
        }
    }
}
```

This answers:

> Which vertices are reachable from this starting vertex?

---

# 5. Prerequisite 4 — Connected Components

In an **undirected graph**, a connected component is a maximal group of vertices where every pair is connected through some path.

Example:

```text
Component 1             Component 2         Component 3

    (1)                    (6)                 (10)
   /   \                    |                  /  \
 (2)---(5)                 (7)              (11)--(12)
  |     |
 (3)---(4)
```

Component sizes:

```text
C1 size = 5
C2 size = 2
C3 size = 3
```

The crucial DFS fact is:

```text
One DFS started anywhere inside one component
visits the entire component.
```

Therefore:

```text
number of times we need to start a new DFS
=
number of connected components
```

---

# 6. Application 1 — Count Connected Components

Start with:

```text
all nodes unvisited
componentCount = 0
```

Loop through all vertices:

```text
for i = 1 ... N

    if i is unvisited:
        start DFS(i)
        componentCount++
```

---

## Example

```text
Component 1        Component 2        Component 3

(1)--(2)           (6)--(7)           (10)--(11)
 |    |
(3)--(4)--(5)
```

Compact dry run:

```text
i=1 → unvisited → DFS visits {1,2,3,4,5} → components=1
i=2 → visited
i=3 → visited
i=4 → visited
i=5 → visited

i=6 → unvisited → DFS visits {6,7} → components=2
i=7 → visited

i=10 → unvisited → DFS visits {10,11} → components=3
```

Answer:

```text
3 connected components
```

---

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int n, m;
vector<vector<int>> g;
vector<int> visited;

void dfs(int node) {
    visited[node] = 1;

    for (int neighbor : g[node]) {
        if (!visited[neighbor]) {
            dfs(neighbor);
        }
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n >> m;

    g.resize(n + 1);

    for (int i = 0; i < m; i++) {
        int a, b;
        cin >> a >> b;

        g[a].push_back(b);
        g[b].push_back(a);
    }

    visited.assign(n + 1, 0);

    int componentCount = 0;

    for (int i = 1; i <= n; i++) {
        if (!visited[i]) {
            dfs(i);
            componentCount++;
        }
    }

    cout << componentCount << '\n';

    return 0;
}
```

---

# 7. Why Boolean `visited` Is Sometimes Not Enough

For counting components, this is enough:

```text
visited[u] = 0 or 1
```

But consider queries:

```text
Are vertices a and b in the same connected component?
```

If DFS has finished, both might simply have:

```text
visited[a] = 1
visited[b] = 1
```

That does **not** tell us whether they belong to:

```text
the same component
```

or:

```text
different components
```

So we upgrade the meaning of the array.

Instead of:

```text
0 = unvisited
1 = visited
```

store:

```text
0 = not assigned yet
1 = component 1
2 = component 2
3 = component 3
...
```

This is the central application from the lecture.

---

# 8. Component Coloring / Component IDs

Think of each component as receiving one color.

```text
Component 1 → color 1
Component 2 → color 2
Component 3 → color 3
```

Example:

```text
Color 1             Color 2              Color 3

 (1)---(2)            (6)                 (10)
  |     |              |                  /  \
 (3)---(4)            (7)               (11)(12)
       /
     (5)
```

Store:

```text
component[1] = 1
component[2] = 1
component[3] = 1
component[4] = 1
component[5] = 1

component[6] = 2
component[7] = 2

component[10] = 3
component[11] = 3
component[12] = 3
```

Now the array carries much more information than a boolean `visited`.

---

## DFS Change

Basic DFS:

```cpp
visited[node] = 1;
```

Component DFS:

```cpp
component[node] = componentId;
```

and recursively pass the same ID:

```cpp
dfs(neighbor, componentId);
```

---

## C++17

```cpp
vector<vector<int>> g;
vector<int> component;

void dfs(int node, int componentId) {
    component[node] = componentId;

    for (int neighbor : g[node]) {
        if (component[neighbor] == 0) {
            dfs(neighbor, componentId);
        }
    }
}
```

Outer loop:

```cpp
int componentId = 0;

for (int i = 1; i <= n; i++) {
    if (component[i] == 0) {
        componentId++;
        dfs(i, componentId);
    }
}
```

---

# 9. Step-by-Step Component Labeling Dry Run

Graph:

```text
Component A                   Component B

      (1)                         (6)
     /   \                         |
   (2)---(5)                      (7)
    |     |
   (3)---(4)
```

Initially:

```text
component = [0,0,0,0,0,0,0]
componentId = 0
```

### Start at vertex 1

```text
1 is unassigned
componentId = 1
dfs(1, 1)
```

DFS spreads the same label:

```text
component[1] = 1
component[2] = 1
component[3] = 1
component[4] = 1
component[5] = 1
```

Array now:

```text
vertex:     1 2 3 4 5 6 7
component:  1 1 1 1 1 0 0
```

### Continue loop

Vertices `2..5` already have labels.

At `6`:

```text
component[6] = 0
```

so:

```text
componentId = 2
dfs(6, 2)
```

Result:

```text
vertex:     1 2 3 4 5 6 7
component:  1 1 1 1 1 2 2
```

Done.

---

# 10. Application 2 — Same Component Queries

Suppose there are many queries:

```text
(a, b)
```

asking:

> Are `a` and `b` in the same connected component?

Once component IDs are precomputed:

```text
same component
⇔
component[a] == component[b]
```

That is all.

---

## Example

Suppose:

```text
vertex:     1 2 3 4 5 6 7 8 9
component:  1 1 1 1 1 2 2 3 3
```

Queries:

```text
(1,4)
component[1] = 1
component[4] = 1
→ SAME

(2,7)
component[2] = 1
component[7] = 2
→ DIFFERENT

(8,9)
component[8] = 3
component[9] = 3
→ SAME
```

---

## Query Code

```cpp
if (component[a] == component[b]) {
    cout << "Same component\n";
} else {
    cout << "Different component\n";
}
```

---

# 11. Why Preprocessing Makes Queries Fast

Suppose:

```text
Q ≤ 100000
```

queries.

A bad approach would be:

```text
for every query (a,b):
    run DFS from a
    check whether b is reached
```

Each DFS can cost:

```text
O(N + M)
```

So total can become:

```text
O(Q(N + M))
```

which is too large.

---

## Better Strategy

### Preprocessing once

```text
Run DFS over the whole graph
Assign every node a component ID

Cost:
O(N + M)
```

### Every query

```text
compare component[a] and component[b]

Cost:
O(1)
```

Total:

```text
O(N + M + Q)
```

This is a major competitive-programming pattern:

```text
Do expensive graph work once.
Store useful information.
Answer many queries cheaply.
```

---

# 12. Application 3 — Component Sizes

While DFS is assigning a component ID, we can also count how many vertices belong to it.

```cpp
componentSize[componentId]++;
```

Example:

```text
Component 1 size = 5
Component 2 size = 3
Component 3 size = 4
```

Graph picture:

```text
        C1                        C2                  C3
   +-----------+             +---------+         +---------+
   | 5 vertices|             |3 vertices|         |4 vertices|
   +-----------+             +---------+         +---------+
```

These sizes become useful for counting pairs.

---

## C++17 DFS with Size

```cpp
vector<vector<int>> g;
vector<int> component;
vector<int> componentSize;

void dfs(int node, int componentId) {
    component[node] = componentId;
    componentSize[componentId]++;

    for (int neighbor : g[node]) {
        if (component[neighbor] == 0) {
            dfs(neighbor, componentId);
        }
    }
}
```

---

# 13. Application 4 — Which New Connections Reduce the Number of Components?

Suppose we add one new undirected edge:

```text
(i, j)
```

There are two cases.

---

## Case 1 — Same Component

```text
      (i) -------- ... -------- (j)
       \________________________/
          path already exists
```

If `i` and `j` are already in the same connected component, adding edge `(i,j)` does **not** merge two components.

So:

```text
number of connected components stays the same
```

---

## Case 2 — Different Components

Before:

```text
Component A               Component B

    (i)                        (j)
   /   \                      /   \
 (...) (...)                (...) (...)
```

Add:

```text
i -------- j
```

After:

```text
Component A + Component B
become ONE component
```

Therefore:

```text
connected-component count decreases by 1
```

So the key condition is:

```text
component[i] != component[j]
```

---

# 14. Counting Cross-Component Pairs — Direct Method

Suppose component sizes are:

```text
s1, s2, s3, ..., sk
```

How many unordered pairs of vertices come from **different components**?

Between component 1 and component 2:

```text
s1 × s2
```

Between component 1 and component 3:

```text
s1 × s3
```

Between component 2 and component 3:

```text
s2 × s3
```

Therefore:

```text
answer = Σ(si × sj), for i < j
```

This exactly counts pairs:

```text
(i,j)
```

whose endpoints lie in different connected components.

---

# 15. Lecture Example: Component Sizes 5, 3, 4

The lecture diagram has three components with sizes:

```text
5, 3, 4
```

Visual:

```text
Component 1           Component 2           Component 3
 size = 5              size = 3              size = 4

  ○──○                   ○                    ○──○
  | /                    |                    |  |
  ○──○                   ○──○                 ○──○
     |
     ○
```

Count cross-component pairs.

---

## Step 1 — C1 with C2

Every one of the 5 nodes in C1 can pair with every one of the 3 nodes in C2:

```text
5 × 3 = 15
```

---

## Step 2 — C1 with C3

```text
5 × 4 = 20
```

---

## Step 3 — C2 with C3

```text
3 × 4 = 12
```

---

## Total

```text
15 + 20 + 12
= 47
```

So:

```text
answer = 47
```

This is the `5×3 + 5×4 + 3×4` calculation shown in the lecture.

---

# 16. Alternative Formula Using Combinations

Total vertices:

```text
N = s1 + s2 + ... + sk
```

All unordered pairs of vertices:

```text
C(N,2)
=
N(N-1)/2
```

But this includes:

```text
pairs inside the same component
+
pairs from different components
```

Inside component `i`:

```text
C(si,2)
=
si(si-1)/2
```

Therefore:

```text
different-component pairs
=
C(N,2) - Σ C(si,2)
```

---

## Lecture Example Again

Sizes:

```text
5, 3, 4
```

Total:

```text
N = 5 + 3 + 4 = 12
```

All pairs:

```text
C(12,2)
=
12×11/2
=
66
```

Pairs inside components:

```text
C(5,2) = 10
C(3,2) = 3
C(4,2) = 6
```

Subtract:

```text
66 - 10 - 3 - 6
=
47
```

Same answer.

---

## Visual Meaning

```text
ALL vertex pairs
        │
        ├── pairs inside C1
        ├── pairs inside C2
        ├── pairs inside C3
        └── pairs crossing components  ← WANT THIS
```

So:

```text
crossing
=
all
-
inside
```

---

# 17. Alternative Algebraic Derivation

The lecture also sketches the identity:

```text
(Σ si)²
=
Σ si² + 2Σ(si sj), i < j
```

Let:

```text
N = Σ si
```

Then:

```text
N²
=
Σ si² + 2Σ(si sj)
```

Move the square-sum term:

```text
N² - Σ si²
=
2Σ(si sj)
```

Divide by 2:

```text
Σ(si sj)
=
(N² - Σ si²) / 2
```

That is another formula for cross-component pairs.

---

## Numerical Dry Run

For:

```text
s = [5,3,4]
```

First:

```text
N = 5+3+4 = 12
N² = 144
```

Squares:

```text
5² + 3² + 4²
=
25 + 9 + 16
=
50
```

Then:

```text
(144 - 50) / 2
=
94 / 2
=
47
```

Again:

```text
answer = 47
```

---

# 18. Best O(C) Counting Method

We do **not** need a double loop over all component pairs.

Suppose sizes are:

```text
[5, 3, 4]
```

Maintain:

```text
seen = total vertices in previous components
answer = 0
```

Process each component:

```text
size 5:
answer += 0 × 5 = 0
seen = 5

size 3:
answer += 5 × 3 = 15
seen = 8

size 4:
answer += 8 × 4 = 32
seen = 12
```

Final:

```text
answer = 15 + 32 = 47
```

Why did `8 × 4` work?

Because those 8 earlier vertices are:

```text
5 from C1
+
3 from C2
```

So:

```text
8 × 4
=
5×4 + 3×4
```

This gives all cross-component pairs exactly once.

---

## Formula

```cpp
long long answer = 0;
long long seen = 0;

for (long long size : sizes) {
    answer += seen * size;
    seen += size;
}
```

Time:

```text
O(number of components)
```

---

# 19. Complete C++17 — Component IDs + Queries

This version preprocesses the graph once, then answers many queries:

```text
"Are a and b in the same connected component?"
```

```cpp
#include <bits/stdc++.h>
using namespace std;

int n, m;
vector<vector<int>> g;
vector<int> component;

void dfs(int node, int componentId) {
    component[node] = componentId;

    for (int neighbor : g[node]) {
        if (component[neighbor] == 0) {
            dfs(neighbor, componentId);
        }
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n >> m;

    g.resize(n + 1);

    for (int i = 0; i < m; i++) {
        int a, b;
        cin >> a >> b;

        g[a].push_back(b);
        g[b].push_back(a);
    }

    component.assign(n + 1, 0);

    int componentId = 0;

    for (int i = 1; i <= n; i++) {
        if (component[i] == 0) {
            componentId++;
            dfs(i, componentId);
        }
    }

    int q;
    cin >> q;

    while (q--) {
        int a, b;
        cin >> a >> b;

        if (component[a] == component[b]) {
            cout << "Same component\n";
        } else {
            cout << "Different component\n";
        }
    }

    return 0;
}
```

---

## Why This Code Is Strong

Preprocessing:

```text
O(N + M)
```

Each query:

```text
O(1)
```

For `Q` queries:

```text
Total = O(N + M + Q)
```

---

# 20. Complete C++17 — Count Cross-Component Pairs

This version computes component sizes using DFS and then counts unordered vertex pairs whose endpoints belong to different components.

```cpp
#include <bits/stdc++.h>
using namespace std;

int n, m;
vector<vector<int>> g;
vector<int> component;
vector<long long> componentSize;

void dfs(int node, int componentId) {
    component[node] = componentId;
    componentSize[componentId]++;

    for (int neighbor : g[node]) {
        if (component[neighbor] == 0) {
            dfs(neighbor, componentId);
        }
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n >> m;

    g.resize(n + 1);

    for (int i = 0; i < m; i++) {
        int a, b;
        cin >> a >> b;

        g[a].push_back(b);
        g[b].push_back(a);
    }

    component.assign(n + 1, 0);
    componentSize.assign(n + 1, 0);

    int componentCount = 0;

    for (int i = 1; i <= n; i++) {
        if (component[i] == 0) {
            componentCount++;
            dfs(i, componentCount);
        }
    }

    long long answer = 0;
    long long seen = 0;

    for (int id = 1; id <= componentCount; id++) {
        answer += seen * componentSize[id];
        seen += componentSize[id];
    }

    cout << answer << '\n';

    return 0;
}
```

---

## Equivalent Combination Version

Once component sizes are known:

```cpp
long long answer = 1LL * n * (n - 1) / 2;

for (int id = 1; id <= componentCount; id++) {
    long long size = componentSize[id];
    answer -= size * (size - 1) / 2;
}
```

Both produce the same result.

---

# 21. Complexity

## Build adjacency list

For `M` edges:

```text
O(M)
```

## DFS over all components

Every node is visited once and every undirected edge is inspected from both endpoints:

```text
O(N + M)
```

## Same-component queries

For `Q` queries:

```text
O(Q)
```

because each query is only:

```text
component[a] == component[b]
```

## Count cross-component pairs

If there are `C` components:

```text
O(C)
```

So overall:

```text
O(N + M + Q)
```

for the query version, or:

```text
O(N + M)
```

for component-pair counting after DFS.

Memory:

```text
O(N + M)
```

---

# 22. Common Mistakes

## Mistake 1 — Running DFS for every query

Bad:

```text
query (a,b)
→ DFS from a
→ search for b
```

For many queries this becomes too slow.

Correct:

```text
DFS once
→ assign component IDs
→ O(1) per query
```

---

## Mistake 2 — Using only boolean visited for component queries

This:

```text
visited[a] = 1
visited[b] = 1
```

does not tell you whether they are in the same component.

Store:

```text
component[a]
component[b]
```

instead.

---

## Mistake 3 — Forgetting disconnected vertices

An isolated vertex is also a connected component.

Example:

```text
(1)--(2)      (3)
```

There are:

```text
2 components
```

not 1.

So always loop:

```cpp
for (int i = 1; i <= n; i++) {
    if (component[i] == 0) {
        ...
    }
}
```

---

## Mistake 4 — Forgetting reverse edge

For undirected graph:

```cpp
g[a].push_back(b);
g[b].push_back(a);
```

---

## Mistake 5 — Double counting cross-component pairs

If you compute:

```text
C1×C2
and
C2×C1
```

you counted the same unordered pairs twice.

Use only:

```text
i < j
```

or use the `seen × size` method.

---

## Mistake 6 — Using `int` for pair counts

If:

```text
N ≈ 100000
```

then number of pairs can be around:

```text
N(N-1)/2 ≈ 5×10^9
```

which exceeds signed 32-bit `int`.

Use:

```cpp
long long
```

---

# 23. Recognition Checklist

When you see a graph problem, ask:

```text
1. Is the graph explicit or implicit?

2. Is it undirected?

3. Does the problem care about groups of mutually reachable nodes?

4. Are there many queries asking:
   "same group?"
   "connected?"
   "reachable through some path?"

5. Does adding an edge matter only when its endpoints
   belong to different components?

6. Do I need component sizes?

7. Am I counting pairs across different components?
```

Then think:

```text
DFS
+
component ID
+
component size
```

---

## Pattern Map

```text
Need number of groups
        ↓
count DFS starts

Need group of every node
        ↓
component[node] = id

Need many connectivity queries
        ↓
component[a] == component[b]

Need size of every group
        ↓
componentSize[id]++

Need pairs from different groups
        ↓
Σ(si × sj), i<j

Equivalent:
C(N,2) - ΣC(si,2)
```

---

# 24. Final Mental Model

The important progression is:

```text
STEP 1
Basic DFS

visited[node] = 1
```

Then upgrade it:

```text
STEP 2
Component DFS

component[node] = componentId
```

Then accumulate:

```text
STEP 3
Component size

componentSize[componentId]++
```

Now several questions become trivial:

```text
How many components?
→ number of DFS starts

Are a and b connected?
→ component[a] == component[b]

Does adding edge (a,b) merge components?
→ component[a] != component[b]

How many vertices are in component x?
→ componentSize[component[x]]

How many pairs lie in different components?
→ C(N,2) - ΣC(size,2)

or

→ Σ(size_i × size_j), i<j
```

The main competitive-programming lesson is:

```text
Do not repeatedly solve reachability from scratch.

Precompute the connected-component structure once,
store the information,
and reuse it.
```
