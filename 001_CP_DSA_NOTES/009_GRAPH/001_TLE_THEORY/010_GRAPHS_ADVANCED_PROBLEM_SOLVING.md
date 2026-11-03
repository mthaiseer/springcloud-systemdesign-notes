# Graphs Advanced — Problem Solving
## TLE-Style Visual Self-Study Notes
### Prerequisites → Second Best MST → Fox and Names → Ralph and Mushrooms

> This note follows the uploaded TLE **Graphs Advanced Problem Solving** lecture.
>
> The lecture covers three advanced patterns:
>
> ```text
> 1. Second Best MST
>       MST + one extra edge + path maximum query
>
> 2. Fox and Names
>       lexicographic constraints → directed graph → topological sort
>
> 3. Ralph and Mushrooms
>       cycles → SCC compression → DAG DP
> ```
>
> The explanations below are expanded for self-study and the graph drawings are redrawn in cleaner ASCII.

---

# Table of Contents

1. [Prerequisite Map](#1-prerequisite-map)
2. [Prerequisite — Spanning Tree and MST](#2-prerequisite--spanning-tree-and-mst)
3. [Prerequisite — Kruskal + DSU](#3-prerequisite--kruskal--dsu)
4. [Prerequisite — Tree Path and Maximum Edge Query](#4-prerequisite--tree-path-and-maximum-edge-query)
5. [Prerequisite — LCA and Binary Lifting Idea](#5-prerequisite--lca-and-binary-lifting-idea)
6. [Prerequisite — Lexicographical Ordering](#6-prerequisite--lexicographical-ordering)
7. [Prerequisite — Topological Sort](#7-prerequisite--topological-sort)
8. [Prerequisite — SCC and Condensation DAG](#8-prerequisite--scc-and-condensation-dag)
9. [Prerequisite — Triangular Numbers](#9-prerequisite--triangular-numbers)
10. [Problem 1 — Second Best MST](#10-problem-1--second-best-mst)
11. [Second Best MST — Main Observation](#11-second-best-mst--main-observation)
12. [Why Adding One Non-MST Edge Creates Exactly One Cycle](#12-why-adding-one-non-mst-edge-creates-exactly-one-cycle)
13. [Why We Remove the Maximum Edge on That Cycle](#13-why-we-remove-the-maximum-edge-on-that-cycle)
14. [Second Best MST — Step-by-Step Dry Run](#14-second-best-mst--step-by-step-dry-run)
15. [Second Best MST — Efficient Algorithm](#15-second-best-mst--efficient-algorithm)
16. [Second Best MST — Binary-Lifting Path Maximum](#16-second-best-mst--binary-lifting-path-maximum)
17. [Second Best MST — C++17](#17-second-best-mst--c17)
18. [Problem 2 — Fox and Names](#18-problem-2--fox-and-names)
19. [Fox and Names — Story to Graph](#19-fox-and-names--story-to-graph)
20. [First Different Character Rule](#20-first-different-character-rule)
21. [The Prefix Trap](#21-the-prefix-trap)
22. [Fox and Names — Step-by-Step Dry Run](#22-fox-and-names--step-by-step-dry-run)
23. [Fox and Names — C++17](#23-fox-and-names--c17)
24. [Problem 3 — Ralph and Mushrooms](#24-problem-3--ralph-and-mushrooms)
25. [Ralph — Understand One Edge First](#25-ralph--understand-one-edge-first)
26. [Deriving the Repeated-Edge Gain Formula](#26-deriving-the-repeated-edge-gain-formula)
27. [Why SCC Is the Correct Graph Model](#27-why-scc-is-the-correct-graph-model)
28. [Internal SCC Edges vs Cross-SCC Edges](#28-internal-scc-edges-vs-cross-scc-edges)
29. [Ralph — Condensation Graph](#29-ralph--condensation-graph)
30. [Ralph — DAG DP State](#30-ralph--dag-dp-state)
31. [Ralph — Step-by-Step Dry Run](#31-ralph--step-by-step-dry-run)
32. [Ralph — Complete C++17](#32-ralph--complete-c17)
33. [Complexity Summary](#33-complexity-summary)
34. [Pattern Recognition Table](#34-pattern-recognition-table)
35. [Common Mistakes](#35-common-mistakes)
36. [Final Mental Model](#36-final-mental-model)
37. [Practice Links](#37-practice-links)

---

# 1. Prerequisite Map

These three problems are not three unrelated tricks.

They combine earlier graph tools.

```text
                     GRAPH TOOLBOX
                          |
        +-----------------+-----------------+
        |                 |                 |
        v                 v                 v
     MST/DSU        Topological Sort       SCC
        |                 |                 |
        v                 v                 v
 Binary Lifting     Ordering Constraints   Condensation
        |                                   |
        v                                   v
 Second Best MST                     DAG Dynamic Programming
                                            |
                                            v
                                  Ralph and Mushrooms
```

The important skill is:

```text
recognize what structure the story hides
```

not memorizing code.

---

# 2. Prerequisite — Spanning Tree and MST

Consider a connected undirected weighted graph.

A **spanning tree**:

```text
1. contains every vertex
2. is connected
3. has no cycle
4. has exactly V - 1 edges
```

Example graph:

```text
          4
    (1)-------(2)
     | \       |
   2 |  \3     | 5
     |   \     |
    (3)-------(4)
          1
```

One spanning tree:

```text
    (1)       (2)
     |         |
   2 |         | 5
     |         |
    (3)-------(4)
          1
```

Cost:

```text
2 + 1 + 5 = 8
```

Another spanning tree may have a different cost.

The **Minimum Spanning Tree (MST)** is the spanning tree with minimum total edge weight.

---

# 3. Prerequisite — Kruskal + DSU

Kruskal:

```text
1. Sort edges by increasing weight.
2. Start with no edges.
3. Consider edges one by one.
4. Add edge (u,v) only if u and v are in different components.
5. Stop after choosing V-1 edges.
```

Why DSU?

DSU answers quickly:

```text
find(u) == find(v) ?
```

If yes:

```text
adding edge u-v creates a cycle
```

If no:

```text
safe to merge the two components
```

---

## 3.1 Compact DSU

```cpp
struct DSU {
    vector<int> parent;
    vector<int> size;

    DSU(int n) {
        parent.resize(n + 1);
        size.assign(n + 1, 1);

        for (int i = 1; i <= n; i++) {
            parent[i] = i;
        }
    }

    int find(int x) {
        if (parent[x] == x) {
            return x;
        }

        return parent[x] = find(parent[x]);
    }

    bool unite(int a, int b) {
        a = find(a);
        b = find(b);

        if (a == b) {
            return false;
        }

        if (size[a] < size[b]) {
            swap(a, b);
        }

        parent[b] = a;
        size[a] += size[b];
        return true;
    }
};
```

---

# 4. Prerequisite — Tree Path and Maximum Edge Query

In a tree, between any two nodes there is exactly **one simple path**.

Example:

```text
          (1)
         /   \
      4 /     \ 2
       /       \
     (2)       (3)
       \       /
      6 \     / 5
         (4)
```

Ignore the visual crossing; think of a valid tree path such as:

```text
2 --4-- 1 --2-- 3
```

The maximum edge weight from `2` to `3` is:

```text
max(4,2) = 4
```

For Second Best MST we repeatedly need:

```text
max edge weight on MST path u -> v
```

Doing a DFS for every edge would be too slow.

We need:

```text
LCA + Binary Lifting
```

---

# 5. Prerequisite — LCA and Binary Lifting Idea

Root the MST.

Example:

```text
             (1)
            /   \
         4 /     \ 7
          /       \
        (2)       (3)
       /   \
    5 /     \ 2
     /       \
   (4)       (5)
```

Binary lifting stores:

```text
up[j][v]
=
2^j-th ancestor of v
```

For path maximum, also store:

```text
mx[j][v]
=
maximum edge weight while jumping
2^j levels upward from v
```

So a big upward jump gives us both:

```text
where we land
+
largest edge seen during the jump
```

This turns path maximum queries into:

```text
O(log V)
```

---

## 5.1 Example

Path:

```text
4 --5-- 2 --4-- 1 --7-- 3
```

Maximum:

```text
max(5,4,7) = 7
```

Binary lifting finds that maximum without walking every edge individually.

---

# 6. Prerequisite — Lexicographical Ordering

Compare two words from left to right.

Example:

```text
abc
abd
```

First differing position:

```text
c vs d
```

Therefore:

```text
c < d
```

Everything after the first differing character is irrelevant.

---

## 6.1 Another Example

```text
January
February
```

First letters differ:

```text
J vs F
```

If `January` must come before `February` in the unknown alphabet:

```text
J < F
```

Graph interpretation:

```text
J -----> F
```

Meaning:

```text
J must appear before F
```

---

# 7. Prerequisite — Topological Sort

A constraint:

```text
a must come before b
```

can be represented as:

```text
a -> b
```

Many ordering constraints create a directed graph.

If graph is a DAG:

```text
a topological ordering exists
```

If graph has a directed cycle:

```text
a -> b -> c -> a
```

then:

```text
a < b < c < a
```

which is impossible.

So:

```text
ordering constraints
→ directed graph
→ topological sorting
```

---

# 8. Prerequisite — SCC and Condensation DAG

An SCC is a maximal group where every node can reach every other node.

Example:

```text
       +----------------+
       |                |
       v                |
      (1) ---> (2) ---> (3)
       ^                |
       |                |
       +----------------+

SCC A = {1,2,3}
```

Suppose:

```text
SCC A -----> SCC B -----> SCC C
```

Compress each SCC into one node.

The result is always a DAG:

```text
[A] -----> [B] -----> [C]
```

This pattern is essential for Ralph and Mushrooms:

```text
cyclic directed graph
→ SCC
→ condensation DAG
→ DP
```

---

# 9. Prerequisite — Triangular Numbers

We need this for the mushroom regrowth formula.

Triangular numbers:

```text
1       = 1
1+2     = 3
1+2+3   = 6
1+2+3+4 = 10
```

Formula:

```text
1 + 2 + ... + k
=
k(k+1)/2
```

Also:

```text
1² + 2² + ... + k²
=
k(k+1)(2k+1)/6
```

These formulas will appear naturally later.

---

# 10. Problem 1 — Second Best MST

We first find the normal MST.

Then we want a different spanning tree with the next best cost.

The lecture's key observation is:

```text
Second Best Tree
=
MST
+ one non-MST edge
- one MST edge
```

Why can we think this way?

Because replacing one edge is the smallest possible change to the MST structure.

---

# 11. Second Best MST — Main Observation

Suppose the MST is:

```text
(1) --2-- (2) --3-- (3)
                \
                 \4
                  \
                  (4)
```

Now consider a non-MST edge:

```text
1 --5-- 4
```

Add it:

```text
(1) --2-- (2) --3-- (3)
 |              \
 |5              \4
 |                \
 +-------------- (4)
```

The graph now has exactly one cycle.

To return to a spanning tree:

```text
remove exactly one edge from that cycle
```

---

# 12. Why Adding One Non-MST Edge Creates Exactly One Cycle

Before adding:

```text
MST is a tree
```

So there is exactly one path:

```text
u ---- ... ---- v
```

Now add new edge:

```text
u -------- v
```

The old tree path plus the new edge forms one cycle.

Visual:

```text
u ----- a ----- b ----- v
 \                     /
  \------ new edge ----/
```

Exactly one cycle appears.

---

# 13. Why We Remove the Maximum Edge on That Cycle

Suppose new edge has weight:

```text
w
```

and the existing MST path `u -> v` has edge weights:

```text
2, 7, 4, 5
```

Adding `w` creates a cycle.

To keep total cost as small as possible, remove the largest old edge:

```text
max = 7
```

Candidate cost:

```text
candidate
=
mstCost + w - 7
```

General formula:

```text
candidate
=
mstCost
+
weight(newEdge)
-
maxEdgeOnMSTPath(u,v)
```

This is the entire mathematical model.

---

# 14. Second Best MST — Step-by-Step Dry Run

Graph:

```text
                4
        (1)-----------(3)
         | \           |
       1 |  \3         | 5
         |   \         |
        (2)---2-------(4)
```

Edges:

```text
1-2 : 1
2-4 : 2
1-4 : 3
1-3 : 4
3-4 : 5
```

---

## 14.1 Build MST with Kruskal

Sorted:

```text
1-2 : 1   TAKE
2-4 : 2   TAKE
1-4 : 3   SKIP — cycle
1-3 : 4   TAKE
```

MST:

```text
        (3)
         |
         |4
         |
(1)--1--(2)
        |
        |2
        |
       (4)
```

Cost:

```text
1 + 2 + 4 = 7
```

---

## 14.2 Try Non-MST Edge `1-4 : 3`

MST path:

```text
1 -> 2 -> 4

weights = [1,2]
```

Maximum:

```text
2
```

Candidate:

```text
7 + 3 - 2
= 8
```

---

## 14.3 Try Non-MST Edge `3-4 : 5`

MST path:

```text
3 -> 1 -> 2 -> 4

weights = [4,1,2]
```

Maximum:

```text
4
```

Candidate:

```text
7 + 5 - 4
= 8
```

So:

```text
second candidate cost = 8
```

---

# 15. Second Best MST — Efficient Algorithm

Naive idea:

```text
remove every MST edge
rebuild MST
```

This is too slow for large graphs.

Efficient idea:

```text
1. Build one MST with Kruskal.
2. Mark MST edges.
3. Build the MST as a tree.
4. Preprocess LCA/Binary Lifting.
5. For every non-MST edge (u,v,w):
       maxEdge = maximum edge on MST path u-v

       candidate =
           mstCost + w - maxEdge

6. Take minimum valid candidate.
```

Complexity:

```text
Kruskal:
O(E log E)

Binary-lifting preprocessing:
O(V log V)

All non-MST edge queries:
O(E log V)
```

Total:

```text
O(E log E + E log V)
```

---

# 16. Second Best MST — Binary-Lifting Path Maximum

Store:

```text
up[j][v]
=
2^j ancestor

mx[j][v]
=
largest edge while moving from v
to up[j][v]
```

To query:

```text
max edge on path u-v
```

do:

```text
1. Lift deeper node until both have same depth.
2. Track maximum edge while lifting.
3. Lift both upward from largest power to smallest.
4. Stop just below LCA.
5. Include the final one-edge jumps to LCA.
```

---

# 17. Second Best MST — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

struct DSU {
    vector<int> parent;
    vector<int> size;

    DSU(int n) {
        parent.resize(n + 1);
        size.assign(n + 1, 1);

        for (int i = 1; i <= n; i++) {
            parent[i] = i;
        }
    }

    int find(int x) {
        if (parent[x] == x) {
            return x;
        }

        return parent[x] = find(parent[x]);
    }

    bool unite(int a, int b) {
        a = find(a);
        b = find(b);

        if (a == b) {
            return false;
        }

        if (size[a] < size[b]) {
            swap(a, b);
        }

        parent[b] = a;
        size[a] += size[b];
        return true;
    }
};

struct Edge {
    int u;
    int v;
    ll weight;
    int id;
    bool inMST;
};

const int LOG = 20;

vector<vector<pair<int, ll>>> tree;
vector<array<int, LOG>> up;
vector<array<ll, LOG>> mx;
vector<int> depth;

void dfs(int node, int parent, ll edgeWeight) {
    up[node][0] = parent;
    mx[node][0] = edgeWeight;

    for (int j = 1; j < LOG; j++) {
        up[node][j] = up[up[node][j - 1]][j - 1];
        mx[node][j] = max(mx[node][j - 1], mx[up[node][j - 1]][j - 1]);
    }

    for (auto [neighbor, weight] : tree[node]) {
        if (neighbor == parent) {
            continue;
        }

        depth[neighbor] = depth[node] + 1;
        dfs(neighbor, node, weight);
    }
}

ll getMaxOnPath(int u, int v) {
    ll answer = 0;

    if (depth[u] < depth[v]) {
        swap(u, v);
    }

    int diff = depth[u] - depth[v];

    for (int j = LOG - 1; j >= 0; j--) {
        if (diff & (1 << j)) {
            answer = max(answer, mx[u][j]);
            u = up[u][j];
        }
    }

    if (u == v) {
        return answer;
    }

    for (int j = LOG - 1; j >= 0; j--) {
        if (up[u][j] != up[v][j]) {
            answer = max(answer, mx[u][j]);
            answer = max(answer, mx[v][j]);

            u = up[u][j];
            v = up[v][j];
        }
    }

    answer = max(answer, mx[u][0]);
    answer = max(answer, mx[v][0]);

    return answer;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<Edge> edges(m);

    for (int i = 0; i < m; i++) {
        cin >> edges[i].u >> edges[i].v >> edges[i].weight;
        edges[i].id = i;
        edges[i].inMST = false;
    }

    sort(edges.begin(), edges.end(), [](const Edge& a, const Edge& b) {
        return a.weight < b.weight;
    });

    DSU dsu(n);
    tree.assign(n + 1, {});

    ll mstCost = 0;
    int chosen = 0;

    for (Edge& edge : edges) {
        if (dsu.unite(edge.u, edge.v)) {
            edge.inMST = true;
            mstCost += edge.weight;
            chosen++;

            tree[edge.u].push_back({edge.v, edge.weight});
            tree[edge.v].push_back({edge.u, edge.weight});
        }
    }

    if (chosen != n - 1) {
        cout << "No spanning tree\n";
        return 0;
    }

    up.resize(n + 1);
    mx.resize(n + 1);
    depth.assign(n + 1, 0);

    for (int j = 0; j < LOG; j++) {
        up[1][j] = 1;
        mx[1][j] = 0;
    }

    dfs(1, 1, 0);

    const ll INF = (1LL << 62);
    ll secondBest = INF;

    for (const Edge& edge : edges) {
        if (edge.inMST) {
            continue;
        }

        ll maxEdge = getMaxOnPath(edge.u, edge.v);
        ll candidate = mstCost + edge.weight - maxEdge;

        secondBest = min(secondBest, candidate);
    }

    if (secondBest == INF) {
        cout << "No second spanning tree\n";
    } else {
        cout << secondBest << '\n';
    }

    return 0;
}
```

> If a particular problem defines "second best" as **strictly larger than the MST cost**, filter candidates with `candidate > mstCost`.
>
> If it means simply a different spanning tree, equal-cost alternatives may matter.

---

# 18. Problem 2 — Fox and Names

We are given names that are already sorted according to an **unknown alphabet**.

Goal:

```text
recover one alphabet ordering
```

This is not really a string-sorting problem.

It is:

```text
extract ordering constraints
→ graph
→ topological sort
```

---

# 19. Fox and Names — Story to Graph

Suppose consecutive names are:

```text
maria
mark
```

Compare left to right:

```text
m a r i a
m a r k
      ^
```

First difference:

```text
i vs k
```

Because first word comes earlier:

```text
i < k
```

Graph:

```text
i -----> k
```

Meaning:

```text
i must appear before k
```

---

# 20. First Different Character Rule

For two consecutive words:

```text
A
B
```

find the first index `i` where:

```text
A[i] != B[i]
```

Then add:

```text
A[i] -> B[i]
```

and stop comparing those words.

Why stop?

Lexicographic order is decided at the first differing character.

---

## 20.1 Better Visual

Words:

```text
abcx
abdy
```

Comparison:

```text
a = a
b = b
c != d   ← first difference
```

Constraint:

```text
c -> d
```

Characters `x` and `y` tell us nothing about alphabet order.

---

# 21. The Prefix Trap

Suppose the input order says:

```text
abcd
abc
```

The second word is a complete prefix of the first.

Normal lexicographic order requires:

```text
abc
```

to come before:

```text
abcd
```

So:

```text
abcd before abc
```

is impossible under every alphabet.

Therefore:

```text
if longer word comes first
and shorter word is its full prefix
→ Impossible
```

---

# 22. Fox and Names — Step-by-Step Dry Run

Suppose:

```text
baa
abcd
abca
cab
cad
```

Compare consecutive pairs.

---

## Pair 1

```text
baa
abcd
```

First character:

```text
b != a
```

Constraint:

```text
b -> a
```

---

## Pair 2

```text
abcd
abca
```

Compare:

```text
a = a
b = b
c = c
d != a
```

Constraint:

```text
d -> a
```

---

## Pair 3

```text
abca
cab
```

First:

```text
a != c
```

Constraint:

```text
a -> c
```

---

## Pair 4

```text
cab
cad
```

Compare:

```text
c = c
a = a
b != d
```

Constraint:

```text
b -> d
```

Graph:

```text
b -----> d -----> a -----> c
 \                    ^
  +-------------------+
```

More precisely:

```text
b -> a
b -> d
d -> a
a -> c
```

This graph is acyclic.

So a topological ordering exists.

The remaining alphabet letters can appear wherever they do not violate these edges.

---

# 23. Fox and Names — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<string> words(n);

    for (string& word : words) {
        cin >> word;
    }

    vector<vector<int>> graph(26);
    vector<int> indegree(26, 0);
    vector<vector<int>> hasEdge(26, vector<int>(26, 0));

    for (int i = 0; i + 1 < n; i++) {
        string& first = words[i];
        string& second = words[i + 1];

        int limit = min(first.size(), second.size());
        int pos = 0;

        while (pos < limit && first[pos] == second[pos]) {
            pos++;
        }

        if (pos == limit) {
            if (first.size() > second.size()) {
                cout << "Impossible\n";
                return 0;
            }

            continue;
        }

        int u = first[pos] - 'a';
        int v = second[pos] - 'a';

        if (!hasEdge[u][v]) {
            hasEdge[u][v] = 1;
            graph[u].push_back(v);
            indegree[v]++;
        }
    }

    queue<int> q;

    for (int letter = 0; letter < 26; letter++) {
        if (indegree[letter] == 0) {
            q.push(letter);
        }
    }

    string answer;

    while (!q.empty()) {
        int letter = q.front();
        q.pop();

        answer += char('a' + letter);

        for (int neighbor : graph[letter]) {
            indegree[neighbor]--;

            if (indegree[neighbor] == 0) {
                q.push(neighbor);
            }
        }
    }

    if (answer.size() != 26) {
        cout << "Impossible\n";
    } else {
        cout << answer << '\n';
    }

    return 0;
}
```

---

## 23.1 Recognition Pattern

Whenever you see:

```text
unknown ordering
+
examples already sorted
```

ask:

```text
Can each example create "x must come before y"?
```

If yes:

```text
directed graph + topological sort
```

---

# 24. Problem 3 — Ralph and Mushrooms

This is the deepest problem in the lecture.

We have:

```text
directed graph
edge contains mushrooms
reusing an edge gives fewer mushrooms
cycles may be useful
start from a given node
maximize total mushrooms
```

At first this looks like:

```text
graph + strange edge mathematics
```

The solution separates it into two layers:

```text
Layer 1:
How much can one repeatedly used edge give?

Layer 2:
Where can repeated use actually happen in the graph?
```

Layer 2 leads directly to:

```text
SCC
```

---

# 25. Ralph — Understand One Edge First

Suppose one edge initially has:

```text
x = 9
```

Collection sequence from the statement/lecture:

```text
1st use: 9

2nd use:
9 - 1 = 8

3rd use:
9 - 1 - 2 = 6

4th use:
9 - 1 - 2 - 3 = 3

5th use:
9 - 1 - 2 - 3 - 4 = -1
→ cannot collect negative mushrooms
```

So total useful collection:

```text
9 + 8 + 6 + 3
= 26
```

The decreases are:

```text
0, 1, 3, 6, 10, ...
```

These are triangular numbers.

---

# 26. Deriving the Repeated-Edge Gain Formula

For usage number indexed from `i = 0`:

```text
gain(i)
=
x - i(i+1)/2
```

because:

```text
i(i+1)/2
=
1 + 2 + ... + i
```

We continue while:

```text
x - i(i+1)/2 >= 0
```

equivalently:

```text
i(i+1)/2 <= x
```

Let `k` be the largest integer satisfying:

```text
k(k+1)/2 <= x
```

Then:

```text
F(x)
=
Σ from i=0 to k
[x - i(i+1)/2]
```

---

## 26.1 Expand It

```text
F(x)
=
(k+1)x
-
1/2 * Σ(i² + i)
```

Use:

```text
Σ i
=
k(k+1)/2
```

and:

```text
Σ i²
=
k(k+1)(2k+1)/6
```

So:

```text
F(x)
=
(k+1)x
-
k(k+1)(k+2)/6
```

This is the clean formula.

---

## 26.2 Check with `x = 9`

Find largest `k`:

```text
k=3:

3*4/2 = 6 <= 9

k=4:

4*5/2 = 10 > 9
```

So:

```text
k = 3
```

Formula:

```text
F(9)
=
(3+1)*9
-
3*4*5/6

=
36 - 10

=
26
```

Matches:

```text
9 + 8 + 6 + 3 = 26
```

---

## 26.3 C++ Function

```cpp
long long totalGain(long long x) {
    long long k = (long long)((sqrtl(1.0L + 8.0L * x) - 1.0L) / 2.0L);

    while ((k + 1) * (k + 2) / 2 <= x) {
        k++;
    }

    while (k * (k + 1) / 2 > x) {
        k--;
    }

    return (k + 1) * x - k * (k + 1) * (k + 2) / 6;
}
```

The correction loops protect against floating-point rounding.

---

# 27. Why SCC Is the Correct Graph Model

Question:

```text
When can we repeatedly come back and use an edge?
```

Answer:

```text
when we are inside a mutually reachable region
```

That is exactly an SCC.

Suppose:

```text
       +------------------+
       |                  |
       v                  |
      (1) --a--> (2) --b--> (3)
       ^                  |
       |                  |
       +-------c----------+
```

Inside this SCC:

```text
1 reaches 2
2 reaches 3
3 reaches 1
```

So we can circulate.

This is why internal SCC edges can be exploited repeatedly according to `F(weight)`.

---

# 28. Internal SCC Edges vs Cross-SCC Edges

This distinction is the heart of the solution.

---

## 28.1 Internal Edge

```text
u and v belong to same SCC
```

Example:

```text
[SCC A]

(1) ----w----> (2)
 ^              |
 |              |
 +--------------+
```

Because we can come back:

```text
the edge may be traversed repeatedly
```

Contribution:

```text
F(w)
```

---

## 28.2 Cross-SCC Edge

```text
SCC A ----w----> SCC B
```

After SCC compression, graph is a DAG.

So once we leave `A` for `B`:

```text
we can never return to A
```

Therefore that cross-SCC edge contributes only:

```text
w
```

once.

This gives the model:

```text
Inside SCC:
use F(w)

Between SCCs:
use w
```

---

# 29. Ralph — Condensation Graph

Original graph:

```text
       SCC A                     SCC B
  +---------------+         +-------------+
  |               |         |             |
  | (1) -> (2)    | --7-->  | (4) -> (5) |
  |  ^      |     |         |  ^      |   |
  |  |      v     |         |  |      v   |
  | (3) <---+     |         |  +------(6) |
  +---------------+         +-------------+
          |
          | 4
          v
       SCC C
      +-----+
      | (7) |
      +-----+
```

Compress:

```text
        [A]
       /   \
    7 /     \ 4
     v       v
    [B]     [C]
```

Now it is a DAG.

---

## 29.1 SCC Internal Value

For each SCC:

```text
value[SCC]
=
sum of F(weight)
for every edge completely inside this SCC
```

Example:

```text
SCC A internal edges:
weights = 9, 4, 6

value[A]
=
F(9) + F(4) + F(6)
```

Cross edges are not included in `value[A]`.

They are handled in DP transitions.

---

# 30. Ralph — DAG DP State

Suppose source lies in component:

```text
sourceComponent
```

Define:

```text
dp[c]
=
maximum mushrooms obtainable
if we start inside SCC c
```

Then:

```text
dp[c]
=
value[c]
+
max over outgoing edges
(
    edgeWeight + dp[nextComponent]
)
```

If no outgoing condensation edge:

```text
dp[c] = value[c]
```

This is standard:

```text
DP on DAG
```

---

## 30.1 Transition Picture

```text
                 7
            +---------> [B]
            |
[A value=20]
            |
            +---------> [C]
                 4
```

Suppose:

```text
dp[B] = 30
dp[C] = 15
```

Then:

```text
dp[A]
=
20 + max(7+30, 4+15)

=
20 + max(37,19)

=
57
```

---

# 31. Ralph — Step-by-Step Dry Run

Suppose SCC compression produces:

```text
           8
     [A] -------> [B]
      |            |
    5 |            | 6
      v            v
     [C] -------> [D]
           4
```

Internal SCC gains:

```text
value[A] = 20
value[B] = 13
value[C] = 7
value[D] = 5
```

We start in `A`.

---

## 31.1 Sink Component D

```text
dp[D] = 5
```

---

## 31.2 Component B

Only transition:

```text
B --6--> D
```

So:

```text
dp[B]
=
13 + 6 + 5

=
24
```

---

## 31.3 Component C

```text
C --4--> D
```

So:

```text
dp[C]
=
7 + 4 + 5

=
16
```

---

## 31.4 Component A

Two choices:

```text
A -> B:
8 + dp[B]
= 8 + 24
= 32

A -> C:
5 + dp[C]
= 5 + 16
= 21
```

Add internal gain:

```text
dp[A]
=
20 + max(32,21)

=
52
```

Answer:

```text
52
```

---

# 32. Ralph — Complete C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

struct Edge {
    int from;
    int to;
    ll weight;
};

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

ll totalGain(ll x) {
    ll k = (ll)((sqrtl(1.0L + 8.0L * x) - 1.0L) / 2.0L);

    while ((k + 1) * (k + 2) / 2 <= x) {
        k++;
    }

    while (k * (k + 1) / 2 > x) {
        k--;
    }

    return (k + 1) * x - k * (k + 1) * (k + 2) / 6;
}

ll dfsDP(int component, const vector<vector<pair<int, ll>>>& dag,
         const vector<ll>& componentValue, vector<ll>& dp) {
    if (dp[component] != -1) {
        return dp[component];
    }

    ll bestNext = 0;

    for (auto [neighbor, weight] : dag[component]) {
        bestNext = max(
            bestNext,
            weight + dfsDP(neighbor, dag, componentValue, dp)
        );
    }

    dp[component] = componentValue[component] + bestNext;
    return dp[component];
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<Edge> edges;
    edges.reserve(m);

    vector<vector<int>> graph(n + 1);
    vector<vector<int>> reverseGraph(n + 1);

    for (int i = 0; i < m; i++) {
        int u, v;
        ll weight;

        cin >> u >> v >> weight;

        edges.push_back({u, v, weight});
        graph[u].push_back(v);
        reverseGraph[v].push_back(u);
    }

    int source;
    cin >> source;

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

    vector<ll> componentValue(componentCount, 0);
    vector<vector<pair<int, ll>>> dag(componentCount);

    for (const Edge& edge : edges) {
        int fromComponent = componentId[edge.from];
        int toComponent = componentId[edge.to];

        if (fromComponent == toComponent) {
            componentValue[fromComponent] += totalGain(edge.weight);
        } else {
            dag[fromComponent].push_back({toComponent, edge.weight});
        }
    }

    vector<ll> dp(componentCount, -1);

    cout << dfsDP(
        componentId[source],
        dag,
        componentValue,
        dp
    ) << '\n';

    return 0;
}
```

---

## 32.1 Complete Transformation

The entire problem becomes:

```text
DIRECTED GRAPH WITH CYCLES
          |
          v
       FIND SCC
          |
          v
+---------------------------+
| internal edge u -> v      |
| same SCC                  |
| add F(weight) to SCC      |
+---------------------------+
          |
          v
+---------------------------+
| cross-SCC edge            |
| keep original weight      |
+---------------------------+
          |
          v
     CONDENSATION DAG
          |
          v
        DAG DP
          |
          v
       ANSWER
```

---

# 33. Complexity Summary

## Second Best MST

```text
Kruskal:
O(E log E)

LCA preprocessing:
O(V log V)

Each non-MST edge:
O(log V)

Total:
O(E log E + E log V)
```

---

## Fox and Names

Only 26 alphabet nodes exist.

Comparing strings:

```text
O(total characters)
```

Topological sort:

```text
O(26 + edges)
```

Overall essentially:

```text
O(total input characters)
```

---

## Ralph and Mushrooms

Kosaraju:

```text
O(V + E)
```

Condensation construction:

```text
O(E)
```

DAG DP:

```text
O(number of SCCs + condensation edges)
```

Overall:

```text
O(V + E)
```

apart from constant-time mathematical work per edge.

---

# 34. Pattern Recognition Table

| Problem Signal | Hidden Structure | Main Tool |
|---|---|---|
| "second best spanning tree" | Replace one MST edge | Kruskal + LCA/Binary Lifting |
| "unknown alphabet/order" | precedence constraints | Directed graph + Topological Sort |
| "directed graph with useful cycles" | cyclic regions act as units | SCC |
| "compress cycles then optimize" | condensation DAG | SCC + DAG DP |
| repeated decreasing reward | arithmetic sequence / triangular numbers | Math derivation |

---

# 35. Common Mistakes

## 35.1 Second Best MST — Rebuilding MST for Every Edge

Too slow:

```text
for each edge:
    rebuild MST
```

Better:

```text
build MST once
+
answer path maximum queries
```

---

## 35.2 Second Best MST — Remove Any Edge from the Cycle

Wrong.

To obtain the best candidate after adding a non-MST edge:

```text
remove the maximum-weight edge
on the created MST cycle
```

---

## 35.3 Fox and Names — Compare Every Different Character

Wrong:

```text
abc
ade

b -> d
c -> e
```

Only first difference matters:

```text
b -> d
```

Nothing can be concluded from `c` and `e`.

---

## 35.4 Fox and Names — Forget Prefix Invalidity

```text
abcd
abc
```

is immediately:

```text
Impossible
```

---

## 35.5 Fox and Names — Forget Cycle Detection

If topo sort processes fewer than 26 letters:

```text
cycle exists
→ Impossible
```

---

## 35.6 Ralph — Apply `F(w)` to Cross-SCC Edges

Wrong.

`F(w)` is for edges that can be revisited.

For condensation edge:

```text
SCC A -> SCC B
```

you cannot return.

So contribution is only:

```text
w
```

---

## 35.7 Ralph — Run DP on Original Cyclic Graph

Wrong.

First:

```text
SCC compression
```

Then:

```text
DAG DP
```

---

## 35.8 Ralph — Forget Internal Edge Gain

Each internal edge contributes:

```text
F(weight)
```

to the value of its SCC.

The SCC value is the sum across all its internal directed edges.

---

# 36. Final Mental Model

## Problem 1 — Second Best MST

```text
MST
+
non-MST edge
=
one cycle

remove maximum edge from that cycle

candidate
=
mstCost + newWeight - maxPathEdge
```

Recognition:

```text
MST + alternative tree
→ tree path query
→ LCA/Binary Lifting
```

---

## Problem 2 — Fox and Names

```text
sorted words
→ first differing character
→ ordering constraint
→ directed edge
→ topological sort
```

Recognition:

```text
"x before y"
→ graph edge x -> y
```

---

## Problem 3 — Ralph and Mushrooms

```text
reusable directed cycles
→ SCC

inside SCC:
F(weight)

between SCCs:
weight once

compress
→ DAG
→ DP
```

The deepest transferable pattern is:

```text
messy cyclic graph problem
        ↓
compress SCCs
        ↓
clean DAG problem
        ↓
DP / greedy / topo processing
```

---

# 37. Practice Links

## Second Best MST

```text
https://cp-algorithms.com/graph/second_best_mst.html
```

---

## Fox and Names

```text
https://codeforces.com/problemset/problem/510/C
```

---

## Ralph and Mushrooms

```text
https://codeforces.com/problemset/problem/894/E
```

---

# Final Self-Check

Before moving on, you should be able to answer these without looking:

```text
[ ] Why does adding one non-MST edge create one cycle?

[ ] Why do we remove the maximum edge from that cycle?

[ ] Why is a path-maximum query needed for Second Best MST?

[ ] Why does LCA/Binary Lifting make that fast?

[ ] In lexicographic comparison, why does only the first differing character matter?

[ ] Why is "abcd" before "abc" impossible?

[ ] Why does a topological-sort cycle mean the alphabet is impossible?

[ ] What is the repeated mushroom sequence for one edge?

[ ] Why are triangular numbers involved?

[ ] Why can internal SCC edges be exploited repeatedly?

[ ] Why can a cross-SCC edge only be used once in the condensed DAG?

[ ] Why is the condensation graph a DAG?

[ ] What is the DP state in Ralph and Mushrooms?
```

If all of these are clear, the lecture's three patterns are not isolated tricks anymore.

They become reusable models:

```text
MST replacement
Ordering constraints
SCC compression + DAG DP
```
