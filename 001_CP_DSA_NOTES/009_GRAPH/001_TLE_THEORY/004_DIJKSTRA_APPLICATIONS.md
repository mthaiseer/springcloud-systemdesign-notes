# Dijkstra & Applications — TLE-Style Self-Study Notes

> Based on **TLE — Dijkstra & Applications, Graphs Intermediate, Level 4**
>
> This version expands the lecture into a self-study note.
>
> Teaching flow:
>
> `Prerequisites → Why BFS fails → Dijkstra idea → Relaxation → Priority Queue → Dry Run → Proof → Limitations → Applications`

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Why BFS Fails on Weighted Graphs](#2-why-bfs-fails-on-weighted-graphs)
3. [Dijkstra — Core Idea](#3-dijkstra--core-idea)
4. [Relaxation](#4-relaxation)
5. [Priority Queue / Min-Heap](#5-priority-queue--min-heap)
6. [Dijkstra Step by Step](#6-dijkstra-step-by-step)
7. [Detailed Dry Run](#7-detailed-dry-run)
8. [Why Dijkstra Works](#8-why-dijkstra-works)
9. [When Dijkstra Does NOT Work](#9-when-dijkstra-does-not-work)
10. [Standard C++17 Template](#10-standard-c17-template)
11. [Application 1 — Reconstruct One Shortest Path](#11-application-1--reconstruct-one-shortest-path)
12. [Application 2 — One Edge Can Be Halved](#12-application-2--one-edge-can-be-halved)
13. [Application 3 — Reach Destination Before Enemy](#13-application-3--reach-destination-before-enemy)
14. [Pattern Comparison](#14-pattern-comparison)
15. [Recognition Checklist](#15-recognition-checklist)
16. [Final Mental Model](#16-final-mental-model)

---

# 1. Prerequisites

These are the minimum concepts needed before Dijkstra.

---

## 1.1 Weighted Graph

An ordinary graph tells us only whether two nodes are connected.

A **weighted graph** also stores a cost on each edge.

Example:

```text
             4
        (1)------(2)
         |        |
       7 |        | 3
         |        |
        (3)------(4)
             2
```

Edges:

```text
1 --4-- 2
1 --7-- 3
2 --3-- 4
3 --2-- 4
```

Meaning:

```text
moving 1 → 2 costs 4
moving 1 → 3 costs 7
...
```

---

## 1.2 Path Cost

A path:

```text
1 → 2 → 4
```

with edge weights:

```text
1 --4-- 2 --3-- 4
```

has total cost:

```text
4 + 3 = 7
```

Another path:

```text
1 --7-- 3 --2-- 4
```

costs:

```text
7 + 2 = 9
```

So shortest by **weight** is:

```text
1 → 2 → 4
```

with cost:

```text
7
```

---

## 1.3 Number of Edges vs Total Weight

These are different metrics.

Example:

```text
             100
        S -------- T
         \
          \ 2
           A
            \
             \ 2
              T
```

Direct route:

```text
S → T

edges = 1
cost = 100
```

Longer route:

```text
S → A → T

edges = 2
cost = 4
```

So:

```text
fewest edges ≠ minimum total weight
```

This is exactly why ordinary BFS can fail.

---

## 1.4 Directed vs Undirected Weighted Edge

Undirected:

```text
u --w-- v
```

Store:

```cpp
adj[u].push_back({v, w});
adj[v].push_back({u, w});
```

Directed:

```text
u --w--> v
```

Store only:

```cpp
adj[u].push_back({v, w});
```

---

## 1.5 Weighted Adjacency List

C++ representation:

```cpp
vector<vector<pair<int, int>>> adj(n + 1);
```

Each pair stores:

```text
{neighbour, weight}
```

Example:

```text
1 --5-- 2
1 --2-- 3
```

becomes:

```text
adj[1] = {(2,5), (3,2)}
```

---

## 1.6 Shortest Distance

Let:

```text
dist[x]
```

mean:

> best distance currently known from the source to node `x`.

Initially:

```text
source = 1

dist[1] = 0
dist[2] = INF
dist[3] = INF
...
```

Why infinity?

Because before exploring:

```text
we do not know any path to those nodes
```

---

## 1.7 Use `long long`

Even if every edge weight fits inside `int`, a complete path may contain many edges.

Example:

```text
100000 edges
each weight = 1e9
```

Total may be around:

```text
1e14
```

So use:

```cpp
using ll = long long;
```

and:

```cpp
const ll INF = (1LL << 62);
```

---

## 1.8 Greedy Idea

A greedy algorithm makes the best-looking safe choice now.

Dijkstra repeatedly chooses:

```text
the unprocessed state with the smallest known distance
```

That is why we need a **min-priority queue**.

---

## 1.9 Min-Heap

We want the smallest distance first.

Store:

```text
(distance, node)
```

Example heap contents:

```text
(2,3)
(5,4)
(9,2)
```

Pop:

```text
(2,3)
```

first.

C++:

```cpp
priority_queue<
    pair<ll, int>,
    vector<pair<ll, int>>,
    greater<pair<ll, int>>
> pq;
```

---

## 1.10 Parent Array

If later we need the actual shortest path, store:

```text
parent[v] = u
```

whenever the best known path to `v` is improved through `u`.

Example:

```text
1 --2-- 3 --4-- 5
```

Store:

```text
parent[3] = 1
parent[5] = 3
```

Then reconstruct backward:

```text
5 ← 3 ← 1
```

Reverse:

```text
1 → 3 → 5
```

---

# 2. Why BFS Fails on Weighted Graphs

The lecture begins with exactly this question.

BFS is designed for:

```text
unweighted graphs
```

or graphs where every edge has the same effective cost.

BFS minimizes:

```text
number of edges
```

It does **not** minimize arbitrary edge-weight sum.

---

## 2.1 Visual Example

```text
              100
        S ------------ T
         \
        1 \
           A
            \
           1 \
              B
               \
              1 \
                 T
```

BFS sees:

```text
S → T
```

using only one edge.

But weighted cost:

```text
100
```

The longer route:

```text
S → A → B → T
```

uses 3 edges but costs:

```text
1 + 1 + 1 = 3
```

Correct weighted shortest path:

```text
3
```

not:

```text
100
```

---

## 2.2 Recognition Rule

```text
Unweighted shortest path
        ↓
       BFS
```

```text
Weighted graph + non-negative weights
        ↓
     Dijkstra
```

---

# 3. Dijkstra — Core Idea

Dijkstra solves:

```text
Single Source Shortest Path
```

Meaning:

```text
one source
→ shortest distance to every reachable node
```

for graphs with suitable non-negative edge weights.

The key idea:

```text
Always expand the node having the smallest known distance first.
```

---

## 3.1 Visual Intuition

Suppose:

```text
dist[A] = 3
dist[B] = 8
dist[C] = 5
```

Which should be processed first?

```text
A
```

because:

```text
3 is smallest
```

Priority queue:

```text
top → (3,A)
       (5,C)
       (8,B)
```

---

# 4. Relaxation

This is the most important operation in shortest-path algorithms.

Suppose:

```text
dist[u] = 7
```

and edge:

```text
u --4-- v
```

Then route through `u` gives:

```text
newDist = dist[u] + 4
        = 7 + 4
        = 11
```

If currently:

```text
dist[v] = 15
```

then:

```text
11 < 15
```

so we found a better route.

Update:

```text
dist[v] = 11
```

This operation is called:

```text
RELAXATION
```

---

## 4.1 Relaxation Formula

For edge:

```text
u --w-- v
```

candidate:

```text
newDist = dist[u] + w
```

If:

```text
newDist < dist[v]
```

then:

```text
dist[v] = newDist
```

and push the new state:

```text
(dist[v], v)
```

into the priority queue.

---

## 4.2 Tiny Dry Run

Before:

```text
dist[u] = 6
dist[v] = 20
weight(u,v) = 5
```

Candidate:

```text
6 + 5 = 11
```

Compare:

```text
11 < 20
```

Update:

```text
dist[v] = 11
```

---

# 5. Priority Queue / Min-Heap

Why not simply scan every node each time?

Because we repeatedly need:

```text
node with minimum current distance
```

A min-heap gives that efficiently.

---

## 5.1 Heap State

Suppose:

```text
pq = {
    (2,4),
    (6,3),
    (9,7)
}
```

Pop:

```text
(2,4)
```

because distance `2` is smallest.

---

## 5.2 Same Node Can Enter Heap Multiple Times

This is important.

Suppose:

```text
dist[5] first becomes 20
```

Push:

```text
(20,5)
```

Later we discover a better path:

```text
dist[5] = 8
```

Push:

```text
(8,5)
```

Now heap may contain both:

```text
(8,5)
(20,5)
```

The old entry is stale.

---

## 5.3 Skip Stale Entries

When popping:

```cpp
auto [currentDist, node] = pq.top();
pq.pop();

if (currentDist != dist[node]) {
    continue;
}
```

Why?

Suppose:

```text
currentDist = 20
dist[node] = 8
```

Then `(20,node)` is an outdated heap entry.

Ignore it.

---

# 6. Dijkstra Step by Step

Initialization:

```text
dist[source] = 0

all other distances = INF

push:
(0, source)
```

Then repeat:

```text
1. Pop node with smallest distance.

2. If heap entry is stale:
       skip it.

3. For every edge:
       node --weight-- neighbour

4. Compute:
       newDist = currentDist + weight

5. If:
       newDist < dist[neighbour]

   update:
       dist[neighbour] = newDist

   push:
       (newDist, neighbour)

6. Continue until heap becomes empty.
```

---

# 7. Detailed Dry Run

Use this graph:

```text
                     4
              (2)--------- (4)
             /  \            |
          2 /    \ 5         | 3
           /      \          |
         (1)      (3)--------+
           \        \
          7 \        \ 1
             \        \
              (5)-----(6)
                   2
```

Let source be:

```text
1
```

Edges:

```text
1-2 = 2
1-5 = 7
2-3 = 5
2-4 = 4
3-6 = 1
5-6 = 2
4-6 = 3
```

Initial:

```text
dist[1]=0

dist[2]=INF
dist[3]=INF
dist[4]=INF
dist[5]=INF
dist[6]=INF

pq=[(0,1)]
```

---

## 7.1 Pop Node 1

```text
current = (0,1)
```

Relax `1→2`:

```text
0+2=2 < INF
dist[2]=2
```

Relax `1→5`:

```text
0+7=7 < INF
dist[5]=7
```

Now:

```text
dist = [0,2,INF,INF,7,INF]

pq = [(2,2), (7,5)]
```

---

## 7.2 Pop Node 2

```text
current = (2,2)
```

Relax `2→3`:

```text
2+5=7
dist[3]=7
```

Relax `2→4`:

```text
2+4=6
dist[4]=6
```

Now:

```text
dist = [0,2,7,6,7,INF]

pq contains:
(6,4)
(7,3)
(7,5)
```

---

## 7.3 Pop Node 4

```text
current = (6,4)
```

Relax `4→6`:

```text
6+3=9
dist[6]=9
```

Now:

```text
dist = [0,2,7,6,7,9]
```

---

## 7.4 Pop Node 3

```text
current = (7,3)
```

Relax `3→6`:

```text
7+1=8
```

Current:

```text
dist[6]=9
```

Better:

```text
8 < 9
```

Update:

```text
dist[6]=8
```

Heap now contains both:

```text
(8,6)
(9,6)
```

The `9` entry is stale.

---

## 7.5 Pop Node 5

```text
current = (7,5)
```

Try:

```text
5→6
```

Candidate:

```text
7+2=9
```

Current:

```text
dist[6]=8
```

No improvement.

---

## 7.6 Pop Node 6

First:

```text
(8,6)
```

is valid.

Later:

```text
(9,6)
```

is stale because:

```text
9 != dist[6]
```

Skip it.

Final shortest distances:

```text
1 → 0
2 → 2
3 → 7
4 → 6
5 → 7
6 → 8
```

---

# 8. Why Dijkstra Works

The lecture presents a contradiction-style proof.

The intuitive reason is:

```text
When the smallest-distance node is chosen,
no other not-yet-processed route can reach it more cheaply
if all usable edge weights are non-negative.
```

---

## 8.1 Contradiction Proof — Simple Version

Assume Dijkstra makes a mistake.

Let:

```text
x
```

be the first node whose finalized shortest distance is wrong.

Let its real shortest distance be:

```text
d
```

Because `x` is the first incorrect node:

```text
every node on the real shortest path before x
has already been handled correctly
```

Take the previous node on that real shortest path:

```text
v → x
```

When `v` was processed, Dijkstra examined edge:

```text
v → x
```

and tried:

```text
dist[v] + weight(v,x)
```

But this is exactly the correct route to `x`.

Therefore `x` should have received its true shortest distance.

Contradiction.

So Dijkstra's greedy choice is correct under its required edge-weight condition.

---

# 9. When Dijkstra Does NOT Work

The lecture explicitly warns about negative cycles.

The deeper rule is:

```text
standard Dijkstra requires non-negative edge weights
```

Why?

Because its greedy assumption says:

```text
once a smallest distance is settled,
a later route cannot make it smaller
```

Negative edges can break that idea.

---

## 9.1 Negative Edge Example

```text
S --2--> A
S --5--> B
B --(-10)--> A
```

Initially:

```text
dist[A]=2
dist[B]=5
```

But route:

```text
S → B → A
```

costs:

```text
5 + (-10) = -5
```

which beats `2`.

This violates the normal Dijkstra reasoning.

---

## 9.2 Negative Cycle

Example:

```text
A --1--> B
B --(-3)--> C
C --1--> A
```

Cycle cost:

```text
1 - 3 + 1 = -1
```

Every extra cycle reduces total cost again:

```text
-1
-2
-3
...
```

There is no finite shortest distance.

Standard Dijkstra is not designed to detect this.

---

# 10. Standard C++17 Template

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll INF = (1LL << 62);

vector<ll> dijkstra(int source,
                    const vector<vector<pair<int, int>>>& adj) {
    int n = (int)adj.size() - 1;

    vector<ll> dist(n + 1, INF);

    priority_queue<
        pair<ll, int>,
        vector<pair<ll, int>>,
        greater<pair<ll, int>>
    > pq;

    dist[source] = 0;
    pq.push({0, source});

    while (!pq.empty()) {
        auto [currentDist, node] = pq.top();
        pq.pop();

        if (currentDist != dist[node]) {
            continue;
        }

        for (auto [neighbour, weight] : adj[node]) {
            ll newDist = currentDist + weight;

            if (newDist < dist[neighbour]) {
                dist[neighbour] = newDist;
                pq.push({newDist, neighbour});
            }
        }
    }

    return dist;
}
```

---

## 10.1 Complexity

With adjacency list + binary heap:

```text
O((N + M) log N)
```

Often written approximately as:

```text
O(M log N)
```

for connected sparse graphs.

Space:

```text
O(N + M)
```

---

## 10.2 Recognition Summary So Far

```text
weighted graph
+
non-negative edges
+
shortest path
        ↓
Dijkstra
```

The hard part in applications is usually not Dijkstra itself.

It is:

```text
What extra information should I compute around Dijkstra?
```

---

# 11. Application 1 — Reconstruct One Shortest Path

The lecture's first application asks us to find the nodes on one shortest path between:

```text
source
and
destination
```

Example in the lecture:

```text
Source = 1
Destination = 5
Output = [1,3,5]
```

The key addition is:

```text
parent[]
```

---

## 11.1 Why Distance Alone Is Not Enough

Ordinary Dijkstra gives:

```text
dist[v]
```

which tells us:

> What is the minimum cost to reach `v`?

But it does not automatically tell us:

> Which previous node gave that best route?

So we store:

```text
parent[v] = u
```

whenever:

```text
u → v
```

improves `dist[v]`.

---

## 11.2 Better Graph Drawing

Consider:

```text
              2
        (1)-------(3)
         |         |
       4 |         | 2
         |         |
        (2)---1---(4)
          \       /
         7 \     / 1
            \   /
             (5)
```

Suppose source:

```text
1
```

destination:

```text
5
```

Imagine shortest route found is:

```text
1 → 3 → 4 → 5
```

Store:

```text
parent[3] = 1
parent[4] = 3
parent[5] = 4
```

Backward:

```text
5 ← 4 ← 3 ← 1
```

Reverse:

```text
1 → 3 → 4 → 5
```

---

## 11.3 Where Do We Update `parent[]`?

Only when relaxation improves the shortest known distance.

```cpp
if (newDist < dist[neighbour]) {
    dist[neighbour] = newDist;
    parent[neighbour] = node;
}
```

Do not change parent when no better path is found.

---

## 11.4 Important: Multiple Shortest Paths

There can be more than one shortest path.

Example:

```text
             (2)
            /   \
         2 /     \ 2
          /       \
        (1)       (4)
          \       /
         2 \     / 2
            \   /
             (3)
```

From `1` to `4`:

```text
1 → 2 → 4
```

cost:

```text
4
```

Also:

```text
1 → 3 → 4
```

cost:

```text
4
```

A single `parent[]` stores:

```text
one
```

of these shortest paths.

That is enough when the requirement is:

```text
construct any one shortest path
```

---

## 11.5 Step-by-Step Algorithm

```text
1. Run Dijkstra from source.
2. Maintain parent[].
3. On successful relaxation:
       parent[v] = u
4. After Dijkstra:
       if destination unreachable:
           no path
5. Start from destination.
6. Follow parent[] backward.
7. Store nodes.
8. Reverse the list.
```

---

## 11.6 Compact Dry Run

Suppose:

```text
1 --2-- 3 --3-- 5

1 --8-- 5
```

Start:

```text
dist[1]=0
parent[1]=-1
```

Relax from `1`:

```text
3 gets 2
parent[3]=1

5 gets 8
parent[5]=1
```

Process `3`:

```text
candidate to 5 = 2+3 = 5

5 < 8
```

Update:

```text
dist[5]=5
parent[5]=3
```

Parent chain:

```text
5 ← 3 ← 1
```

Answer:

```text
1 → 3 → 5
```

---

## 11.7 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll INF = (1LL << 62);

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<vector<pair<int, int>>> adj(n + 1);

    for (int i = 0; i < m; i++) {
        int u, v, w;
        cin >> u >> v >> w;

        adj[u].push_back({v, w});
        adj[v].push_back({u, w});
    }

    int source, destination;
    cin >> source >> destination;

    vector<ll> dist(n + 1, INF);
    vector<int> parent(n + 1, -1);

    priority_queue<
        pair<ll, int>,
        vector<pair<ll, int>>,
        greater<pair<ll, int>>
    > pq;

    dist[source] = 0;
    pq.push({0, source});

    while (!pq.empty()) {
        auto [currentDist, node] = pq.top();
        pq.pop();

        if (currentDist != dist[node]) {
            continue;
        }

        for (auto [neighbour, weight] : adj[node]) {
            ll newDist = currentDist + weight;

            if (newDist < dist[neighbour]) {
                dist[neighbour] = newDist;
                parent[neighbour] = node;
                pq.push({newDist, neighbour});
            }
        }
    }

    if (dist[destination] == INF) {
        cout << "No path\n";
        return 0;
    }

    vector<int> path;

    for (int node = destination; node != -1; node = parent[node]) {
        path.push_back(node);
    }

    reverse(path.begin(), path.end());

    cout << "Shortest distance = " << dist[destination] << '\n';

    for (int node : path) {
        cout << node << ' ';
    }

    cout << '\n';

    return 0;
}
```

---

## 11.8 Complexity

Dijkstra:

```text
O((N + M) log N)
```

Path reconstruction:

```text
O(number of nodes in the path)
```

---

## 11.9 Recognition Pattern

Whenever the problem asks:

```text
shortest distance
+
print the route
```

think:

```text
Dijkstra + parent[]
```

---

# 12. Application 2 — One Edge Can Be Halved

The lecture's second application:

> Find the shortest path from source to destination when you may reduce the weight of one edge to 50%.

This is a very important Dijkstra pattern.

---

## 12.1 Naive Idea

Try every edge as the discounted edge.

For each edge:

```text
change weight
run Dijkstra again
restore weight
```

If there are `M` edges:

```text
M Dijkstra runs
```

Too expensive.

We need to reuse shortest-path information.

---

## 12.2 Main Observation

Suppose we choose edge:

```text
u --w-- v
```

as the discounted edge.

Then an optimal route using this direction has three parts:

```text
source
   ↓
shortest path to u
   ↓
discounted edge u-v
   ↓
shortest path from v to destination
```

Cost:

```text
distFromSource[u]
+
w/2
+
distToDestination[v]
```

That is the key formula.

---

## 12.3 Better Visual Derivation

```text
SOURCE                                  DESTINATION
  S                                         D
  |                                         ^
  | shortest path                           |
  v                                         | shortest path
 [u] ------------ w/2 ------------> [v]
```

So:

```text
total =
shortest(S,u)
+
w/2
+
shortest(v,D)
```

For an undirected edge, the route could also use the edge in the opposite direction:

```text
S → ... → v --w/2-- u → ... → D
```

So also test:

```text
distS[v] + w/2 + distD[u]
```

---

## 12.4 How Do We Get `distD[]`?

The graph is undirected in the lecture application.

So if we run Dijkstra from destination:

```text
distD[x]
```

means:

```text
shortest distance from destination to x
```

which is the same as:

```text
shortest distance from x to destination
```

in an undirected graph.

Therefore we need only:

```text
Dijkstra(source)
Dijkstra(destination)
```

---

## 12.5 Example

Suppose:

```text
source → ... → u
cost = 2
```

Edge:

```text
u --8-- v
```

After discount:

```text
8/2 = 4
```

Suppose:

```text
v → ... → destination
cost = 0
```

Total:

```text
2 + 4 + 0 = 6
```

This matches the lecture example:

```text
6 = 2 + (8/2)
```

---

## 12.6 Full Formula

For every undirected edge:

```text
(u,v,w)
```

candidate 1:

```text
distS[u] + w/2 + distD[v]
```

candidate 2:

```text
distS[v] + w/2 + distD[u]
```

Take minimum over all edges.

Also keep the original shortest path as baseline:

```text
answer = distS[destination]
```

This covers the "allowed to reduce one edge" wording even if no discount is needed.

---

## 12.7 Why This Formula Is Correct

Suppose the optimal route discounts edge:

```text
u-v
```

Before reaching `u`, there is no reason to use anything except the shortest route from source to `u`.

If a cheaper prefix existed:

```text
we could replace the prefix
and improve the whole solution
```

Similarly after `v`:

```text
use the shortest route from v to destination
```

Therefore:

```text
optimal route using edge u-v
=
best prefix
+
discounted edge
+
best suffix
```

Exactly:

```text
distS[u] + w/2 + distD[v]
```

---

## 12.8 Step-by-Step Algorithm

```text
1. Read all edges.
2. Build adjacency list.
3. Run Dijkstra from source.
       → distS[]
4. Run Dijkstra from destination.
       → distD[]
5. answer = distS[destination]
6. For every edge (u,v,w):
       candidate1 = distS[u] + w/2 + distD[v]
       candidate2 = distS[v] + w/2 + distD[u]
       answer = min(answer, candidate1, candidate2)
7. Print answer.
```

---

## 12.9 Better Graph Picture

```text
                  8
             [u]------[v]
            /            \
         2 /              \ 3
          /                \
       [S]                  [D]
```

Without discount:

```text
2 + 8 + 3 = 13
```

Discount middle edge:

```text
2 + 4 + 3 = 9
```

The two-Dijkstra arrays give:

```text
distS[u] = 2
distD[v] = 3
```

Then:

```text
2 + 8/2 + 3
= 9
```

---

## 12.10 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll INF = (1LL << 62);

struct Edge {
    int u;
    int v;
    int w;
};

vector<ll> dijkstra(int source,
                    const vector<vector<pair<int, int>>>& adj) {
    int n = (int)adj.size() - 1;

    vector<ll> dist(n + 1, INF);

    priority_queue<
        pair<ll, int>,
        vector<pair<ll, int>>,
        greater<pair<ll, int>>
    > pq;

    dist[source] = 0;
    pq.push({0, source});

    while (!pq.empty()) {
        auto [currentDist, node] = pq.top();
        pq.pop();

        if (currentDist != dist[node]) {
            continue;
        }

        for (auto [neighbour, weight] : adj[node]) {
            ll newDist = currentDist + weight;

            if (newDist < dist[neighbour]) {
                dist[neighbour] = newDist;
                pq.push({newDist, neighbour});
            }
        }
    }

    return dist;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<vector<pair<int, int>>> adj(n + 1);
    vector<Edge> edges;

    for (int i = 0; i < m; i++) {
        int u, v, w;
        cin >> u >> v >> w;

        edges.push_back({u, v, w});

        adj[u].push_back({v, w});
        adj[v].push_back({u, w});
    }

    int source, destination;
    cin >> source >> destination;

    vector<ll> distS = dijkstra(source, adj);
    vector<ll> distD = dijkstra(destination, adj);

    ll answer = distS[destination];

    for (const Edge& edge : edges) {
        int u = edge.u;
        int v = edge.v;
        ll halfWeight = edge.w / 2;

        if (distS[u] != INF && distD[v] != INF) {
            answer = min(answer, distS[u] + halfWeight + distD[v]);
        }

        if (distS[v] != INF && distD[u] != INF) {
            answer = min(answer, distS[v] + halfWeight + distD[u]);
        }
    }

    cout << answer << '\n';

    return 0;
}
```

---

## 12.11 Complexity

Two Dijkstra runs:

```text
O((N + M) log N)
```

Scanning all edges:

```text
O(M)
```

Total:

```text
O((N + M) log N)
```

not:

```text
O(M * Dijkstra)
```

This is the optimization.

---

## 12.12 Recognition Pattern

Whenever a shortest-path problem says:

```text
you may modify exactly/at most one edge
```

think:

```text
split the optimal path around that special edge
```

Very common form:

```text
best prefix
+
special edge operation
+
best suffix
```

Then often:

```text
Dijkstra from source
+
Dijkstra from destination
+
scan every candidate special edge
```

---

# 13. Application 3 — Reach Destination Before Enemy

The lecture's third application changes the meaning of shortest distance.

We have:

```text
A = your starting node
B = destination
C = enemy starting node
```

Before moving, you must reveal your path.

The enemy may block you if it can reach a node on that path before you do.

The lecture implementation treats a tie as unsafe, so for every node on our route we need:

```text
our arrival time < enemy arrival time
```

---

## 13.1 Story → Mathematical Model

At every node `v`, compare two shortest arrival times.

Your best possible arrival:

```text
myDist[v]
```

Enemy's best possible arrival:

```text
enemyDist[v]
```

A node is safe for you only when:

```text
myDist[v] < enemyDist[v]
```

This becomes the central condition.

---

## 13.2 First Compute Enemy Arrival Times

Run:

```text
Dijkstra(C)
```

This gives:

```text
enemyDist[v]
```

for every node.

Interpretation:

```text
enemyDist[v]
=
earliest possible time enemy can reach v
```

---

## 13.3 Then Run a Restricted Dijkstra from A

Now run Dijkstra from:

```text
A
```

But when trying:

```text
u --w-- v
```

candidate arrival is:

```text
newDist = myDist[u] + w
```

We are allowed to enter `v` only if:

```text
newDist < enemyDist[v]
```

and it also improves our own distance:

```text
newDist < myDist[v]
```

Combined condition:

```text
if newDist < enemyDist[v]
and newDist < myDist[v]
```

then relax.

---

## 13.4 Better Graph Drawing

Example:

```text
                   [B]
                  /   \
               2 /     \ 4
                /       \
              [6]-------[8]
               |    3
             2 |
               |
              [4]
             /   \
          3 /     \ 5
           /       \
         [2]       [7]
          |
        2 |
          |
         [A]

Enemy C is elsewhere:

         [C]
          |
        4 |
          |
         [5]
          |
        3 |
          |
         [6]
```

The question is not simply:

```text
What is your shortest path A → B?
```

It is:

```text
Is there a path A → B
such that at every entered node
you arrive strictly before C can?
```

---

## 13.5 Why We Need Enemy Distances First

Suppose candidate path is:

```text
A → X → Y → B
```

Your arrival times:

```text
A = 0
X = 3
Y = 7
B = 10
```

Enemy earliest arrival:

```text
A = 8
X = 5
Y = 6
B = 20
```

Check:

```text
A: 0 < 8  ✓
X: 3 < 5  ✓
Y: 7 < 6  ✗
```

Even though you eventually reach `B` quickly enough:

```text
10 < 20
```

the route is invalid because enemy can intercept at `Y`.

Therefore:

```text
checking destination alone is not enough
```

We must enforce safety during traversal.

---

## 13.6 Core Relaxation Condition

Normal Dijkstra:

```cpp
if (newDist < dist[v]) {
    ...
}
```

Safe-path Dijkstra:

```cpp
if (newDist < enemyDist[v] && newDist < dist[v]) {
    ...
}
```

One extra condition completely changes the problem.

---

## 13.7 Compact Dry Run

Suppose:

```text
A --2-- X --3-- Y --2-- B
          \
           \ 8
            B
```

Enemy starts at `C`.

Enemy shortest arrival times:

```text
enemyDist[X] = 5
enemyDist[Y] = 8
enemyDist[B] = 12
```

Your traversal:

```text
A → X:
arrival = 2
2 < 5
safe
```

Then:

```text
X → Y:
arrival = 2+3 = 5
5 < 8
safe
```

Then:

```text
Y → B:
arrival = 5+2 = 7
7 < 12
safe
```

So:

```text
Yes
```

Now change:

```text
enemyDist[Y] = 5
```

Your arrival:

```text
5
```

Lecture-style strict condition:

```text
5 < 5
```

is false.

So that route is rejected.

---

## 13.8 Why This Works

`enemyDist[v]` is the earliest possible enemy arrival.

Therefore if:

```text
ourArrival >= enemyDist[v]
```

the enemy can reach `v` no later than us.

Unsafe.

But if:

```text
ourArrival < enemyDist[v]
```

then even the enemy's fastest possible route cannot beat us to `v`.

So only those nodes should be considered during our Dijkstra.

---

## 13.9 Step-by-Step Algorithm

```text
1. Read weighted graph.
2. Run Dijkstra from enemy C.
       → enemyDist[]

3. Initialize your dist[] = INF.
4. dist[A] = 0.
5. Push (0,A).

6. While heap is not empty:
       pop smallest state.

       if node == B:
           return YES

       for every edge node→v with weight w:
           newDist = dist[node] + w

           if newDist < enemyDist[v]
           and newDist < dist[v]:
               dist[v] = newDist
               push (newDist,v)

7. Heap becomes empty:
       return NO
```

---

## 13.10 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll INF = (1LL << 62);

vector<ll> dijkstra(int source,
                    const vector<vector<pair<int, int>>>& adj) {
    int n = (int)adj.size() - 1;

    vector<ll> dist(n + 1, INF);

    priority_queue<
        pair<ll, int>,
        vector<pair<ll, int>>,
        greater<pair<ll, int>>
    > pq;

    dist[source] = 0;
    pq.push({0, source});

    while (!pq.empty()) {
        auto [currentDist, node] = pq.top();
        pq.pop();

        if (currentDist != dist[node]) {
            continue;
        }

        for (auto [neighbour, weight] : adj[node]) {
            ll newDist = currentDist + weight;

            if (newDist < dist[neighbour]) {
                dist[neighbour] = newDist;
                pq.push({newDist, neighbour});
            }
        }
    }

    return dist;
}

bool canReachSafely(
    int start,
    int destination,
    int enemy,
    const vector<vector<pair<int, int>>>& adj) {

    int n = (int)adj.size() - 1;

    vector<ll> enemyDist = dijkstra(enemy, adj);
    vector<ll> dist(n + 1, INF);

    priority_queue<
        pair<ll, int>,
        vector<pair<ll, int>>,
        greater<pair<ll, int>>
    > pq;

    if (0 >= enemyDist[start]) {
        return false;
    }

    dist[start] = 0;
    pq.push({0, start});

    while (!pq.empty()) {
        auto [currentDist, node] = pq.top();
        pq.pop();

        if (currentDist != dist[node]) {
            continue;
        }

        if (node == destination) {
            return true;
        }

        for (auto [neighbour, weight] : adj[node]) {
            ll newDist = currentDist + weight;

            if (newDist < enemyDist[neighbour] &&
                newDist < dist[neighbour]) {
                dist[neighbour] = newDist;
                pq.push({newDist, neighbour});
            }
        }
    }

    return false;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<vector<pair<int, int>>> adj(n + 1);

    for (int i = 0; i < m; i++) {
        int u, v, w;
        cin >> u >> v >> w;

        adj[u].push_back({v, w});
        adj[v].push_back({u, w});
    }

    int A, B, C;
    cin >> A >> B >> C;

    cout << (canReachSafely(A, B, C, adj) ? "Yes\n" : "No\n");

    return 0;
}
```

---

## 13.11 Complexity

Enemy Dijkstra:

```text
O((N + M) log N)
```

Restricted Dijkstra from `A`:

```text
O((N + M) log N)
```

Total:

```text
O((N + M) log N)
```

up to a constant factor of 2.

---

## 13.12 Recognition Pattern

Whenever two agents move in a weighted graph:

```text
you
enemy/fire/monster/virus
```

and the condition is:

```text
reach a node before the danger reaches it
```

think:

```text
1. shortest danger arrival to every node
2. shortest safe traversal for you
3. only enter node v if:
       myArrival < dangerArrival[v]
```

This is a very reusable graph pattern.

---

# 14. Pattern Comparison

| Situation | Main Idea | Extra Data |
|---|---|---|
| Weighted shortest path | Dijkstra | `dist[]` |
| Need actual shortest route | Dijkstra + reconstruction | `parent[]` |
| One special/discounted edge | Dijkstra from both ends + edge scan | `distS[]`, `distD[]` |
| Enemy reaches nodes too | Precompute enemy distances + restricted Dijkstra | `enemyDist[]` |

---

## 14.1 Same Algorithm, Different Modelling

### Basic Dijkstra

```text
state:
node

value:
minimum distance
```

### Path reconstruction

```text
state:
node

extra:
who improved me?
```

### One discounted edge

```text
state:
candidate special edge

formula:
best prefix + special edge + best suffix
```

### Enemy problem

```text
state:
node

extra constraint:
arrive before enemy
```

This is why graph modelling matters more than memorizing one code template.

---

# 15. Recognition Checklist

When reading a shortest-path problem, ask these questions in order.

---

## 15.1 Are all edges equal / unweighted?

Yes:

```text
BFS
```

No:

continue.

---

## 15.2 Are all relevant weights non-negative?

Yes:

```text
Dijkstra is a candidate
```

Negative weights:

```text
standard Dijkstra is unsafe
```

---

## 15.3 One source to many nodes?

Think:

```text
single-source shortest path
→ Dijkstra
```

---

## 15.4 Need the actual route?

Add:

```text
parent[]
```

---

## 15.5 Can one edge be modified?

Look for:

```text
source → u
special edge
v → destination
```

Try:

```text
distS[u]
+
specialCost(u,v)
+
distD[v]
```

---

## 15.6 Is there an enemy / fire / competing traveller?

Compute:

```text
dangerArrival[v]
```

first.

Then restrict your traversal:

```text
myArrival < dangerArrival[v]
```

---

## 15.7 Does the problem say "at most one special action"?

Examples:

```text
discount one edge
free one edge
double one edge
use one coupon
skip one toll
```

Think:

```text
split path around the special action
```

or sometimes:

```text
state-expanded Dijkstra
```

For this lecture's 50% edge problem, the two-Dijkstra + edge-scan formulation is enough.

---

# 16. Final Mental Model

## 16.1 BFS vs Dijkstra

```text
UNWEIGHTED
    ↓
minimum number of edges
    ↓
BFS
```

```text
WEIGHTED + NON-NEGATIVE
    ↓
minimum total weight
    ↓
Dijkstra
```

---

## 16.2 Dijkstra Core

```text
dist[source]=0
others=INF

        ↓

min-heap

        ↓

pop smallest known distance

        ↓

relax every outgoing edge

        ↓

better?
yes → update + push
no  → ignore

        ↓

repeat
```

---

## 16.3 Relaxation Mental Formula

```text
current best to u
        +
edge u→v
        =
candidate path to v
```

So:

```text
newDist = dist[u] + w
```

If:

```text
newDist < dist[v]
```

update.

---

## 16.4 Application 1

```text
Need actual shortest path
        ↓
store parent when relaxation succeeds
        ↓
destination
        ↓
follow parent backward
        ↓
reverse
```

---

## 16.5 Application 2

```text
One edge can be halved
        ↓
run Dijkstra from source
run Dijkstra from destination
        ↓
for each edge u-v:
    prefix
    +
    w/2
    +
    suffix
        ↓
take minimum
```

---

## 16.6 Application 3

```text
Enemy starts at C
        ↓
Dijkstra(C)
        ↓
enemyDist[v]
        ↓
Dijkstra from A
but only enter v when:
myArrival < enemyDist[v]
        ↓
reach B?
Yes / No
```

---

# One-Line Recognition Sheet

```text
Unweighted shortest path
→ BFS

Weighted non-negative shortest path
→ Dijkstra

Dijkstra + actual path
→ parent[]

One modified edge
→ shortest prefix + modified edge + shortest suffix

Enemy/fire arrival competition
→ precompute danger distances, then restricted shortest path

Negative weights / negative cycle
→ do not blindly use standard Dijkstra
```

---

# Final Takeaway

Do not memorize Dijkstra as only this:

```text
priority_queue + dist[]
```

Understand it as:

```text
1. What does dist[x] mean?

2. What is the next safest greedy state?

3. What is the relaxation formula?

4. What extra information does the problem need?
   parent?
   destination distances?
   enemy arrival?
   another state?

5. What condition makes an edge transition valid?
```

That is how Dijkstra becomes a reusable competitive-programming tool instead of a memorized template.
