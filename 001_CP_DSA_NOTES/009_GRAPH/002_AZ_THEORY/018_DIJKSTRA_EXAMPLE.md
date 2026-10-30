# Dijkstra Example — Step-by-Step Modelling and Dry Run

> Self-study competitive-programming note based on the supplied AlgoZenith **Dijkstra Example / Problem 2** screenshots.  
> The screenshots show the weighted graph and an `A < B` / edge-cost derivation, but not the complete written problem statement.  
> So this note keeps the lecture-visible modelling and explains the Dijkstra reasoning without inventing missing constraints.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [What the Example Is Teaching](#2-what-the-example-is-teaching)
3. [Read the Weighted Graph Correctly](#3-read-the-weighted-graph-correctly)
4. [Convert the Story into a Graph](#4-convert-the-story-into-a-graph)
5. [Important A-B Distance Derivation](#5-important-a-b-distance-derivation)
6. [Why This Becomes Dijkstra](#6-why-this-becomes-dijkstra)
7. [Dijkstra State](#7-dijkstra-state)
8. [Relaxation](#8-relaxation)
9. [Step-by-Step Algorithm](#9-step-by-step-algorithm)
10. [Worked Dry Run on the Visible Graph](#10-worked-dry-run-on-the-visible-graph)
11. [Why Greedy Finalization Works](#11-why-greedy-finalization-works)
12. [Clean C++17 Template](#12-clean-c17-template)
13. [Complexity](#13-complexity)
14. [Common Mistakes](#14-common-mistakes)
15. [Recognition Model](#15-recognition-model)
16. [Final Mental Model](#16-final-mental-model)

---

# 1. Prerequisites

Before this example, be comfortable with:

- weighted graphs,
- adjacency lists,
- shortest path,
- `dist[]`,
- relaxation,
- min-priority queue,
- Dijkstra for non-negative edge weights,
- `long long` for path cost.

Learning chain:

```text
weighted graph
     ↓
choose source
     ↓
dist[source] = 0
     ↓
take minimum-distance node
     ↓
relax its edges
     ↓
repeat
```

---

## 1.1 Weighted graph

An edge

```text
u ----w---- v
```

means travelling from `u` to `v` costs:

```text
w
```

A path cost is the sum of all edge weights used.

Example:

```text
A --3-- B --4-- C
```

Path:

```text
A → B → C
```

Cost:

```text
3 + 4 = 7
```

---

## 1.2 Shortest-path distance

For source `S`:

```text
dist[v]
```

means:

```text
minimum cost currently known
to reach v from S
```

Initialization:

```text
dist[S] = 0
dist[all other nodes] = INF
```

---

# 2. What the Example Is Teaching

The lecture screenshot shows a graph with a marked **start** node and several non-negative edge costs such as:

```text
3, 4, 5, 7, 10, 12, 5
```

It also shows operations/labels such as:

```text
x + 3
x + x
```

The important idea is not to follow a visually attractive path.

Instead:

```text
model every legal transition as an edge
              ↓
give that transition its correct cost
              ↓
run Dijkstra from the start state
```

This is a common competitive-programming pattern:

```text
original story
     ↓
states
     ↓
weighted transitions
     ↓
shortest-path problem
```

---

# 3. Read the Weighted Graph Correctly

A simplified version of the visible lecture graph is:

```text
                   3
              A -------- B
              |          |
              |5         |4
              |          |
              +--------> C --------10-------- R1
                   5     |
                         |7
                         |
                         D --------10-------- R2
                         |
                        12
                         |
                         E --------5--------- R3
```

The exact node names are not shown in the screenshot, so we use:

```text
A, B, C, D, E, R1, R2, R3
```

for explanation.

The marked start is around the left side.

The important observation:

```text
There may be many ways to reach the same state.
Dijkstra keeps only the cheapest discovered distance.
```

---

# 4. Convert the Story into a Graph

Whenever a problem contains operations like:

```text
x → x + 3
x → x + x
```

or movement between special states, use this translation:

```text
state/value before operation
          ↓
        NODE

legal operation
          ↓
        EDGE

cost of operation
          ↓
     EDGE WEIGHT
```

Example:

```text
x --cost 3--> x + 3
```

means:

```text
from state x,
we can reach state x + 3
by paying 3
```

Once all valid operations are represented as non-negative weighted edges, the story disappears.

You are left with:

```text
Find the shortest distance from START.
```

That is exactly the form Dijkstra wants.

---

# 5. Important A-B Distance Derivation

The second lecture screenshot considers two values/positions:

```text
A < B
```

with a required connection/path cost `x`.

The board shows the important easy case:

```text
if |A - B| >= x
```

then moving a distance `x` from the smaller side is sufficient in the construction being discussed.

For `A < B`:

```text
min(A, B) = A
```

so the candidate position/value shown in the lecture is:

```text
A + x
```

or generally:

```text
min(A, B) + x
```

---

## 5.1 Why the absolute difference matters

Distance between the two endpoints is:

```text
|A - B|
```

Since `A < B`:

```text
|A - B| = B - A
```

If:

```text
B - A >= x
```

there is enough separation to place/move `x` units while staying between the endpoints.

Visual:

```text
A -------------------------- B
|--------- x ---------|

A + x
```

---

## 5.2 The second algebraic relation on the board

The lecture also writes:

```text
(t - A) + (t - B) = x
```

Expand:

```text
t - A + t - B = x
```

Combine the two `t` terms:

```text
2t - A - B = x
```

Move `A + B` to the right:

```text
2t = A + B + x
```

Divide by `2`:

```text
t = (A + B + x) / 2
```

This is the exact algebraic form visible in the screenshot.

### Tiny numeric example

Take:

```text
A = 3
B = 7
x = 6
```

Then:

```text
t = (3 + 7 + 6) / 2
  = 16 / 2
  = 8
```

Check:

```text
(t - A) + (t - B)
= (8 - 3) + (8 - 7)
= 5 + 1
= 6
= x
```

So the formula is consistent.

> In an actual contest problem, always check integer/parity and domain constraints from the complete statement before using this candidate directly.

---

# 6. Why This Becomes Dijkstra

The graph contains different edge costs:

```text
3
4
5
7
10
12
...
```

Therefore ordinary BFS is not valid.

BFS assumes:

```text
every edge has equal cost
```

Here:

```text
one edge may cost 3
another may cost 12
```

The number of edges does **not** determine the cheapest route.

Example:

```text
Route 1:
one edge of cost 12
total = 12

Route 2:
three edges of costs 3, 4, 2
total = 9
```

More edges can still be cheaper.

So we need:

```text
Dijkstra
```

provided all edge costs are non-negative.

---

# 7. Dijkstra State

Maintain:

```text
dist[node]
```

and a min-priority queue storing:

```text
(distance, node)
```

Initial state:

```text
dist[start] = 0

all other distances = INF

pq = {(0, start)}
```

The queue always gives us the currently cheapest state.

---

# 8. Relaxation

Suppose we are at:

```text
current
```

and have an edge:

```text
current --weight--> next
```

Current best known cost:

```text
dist[current]
```

Candidate route:

```text
dist[current] + weight
```

If:

```text
dist[current] + weight < dist[next]
```

then improve:

```text
dist[next] = dist[current] + weight
```

and push the improved state.

C++:

```cpp
ll newDist = currentDist + weight;

if (newDist < dist[next]) {
    dist[next] = newDist;
    pq.push({newDist, next});
}
```

---

# 9. Step-by-Step Algorithm

## Step 1 — Build the graph

For every legal transition:

```text
u → v with cost w
```

store:

```cpp
graph[u].push_back({v, w});
```

For an undirected edge also add:

```cpp
graph[v].push_back({u, w});
```

---

## Step 2 — Initialize

```text
dist[start] = 0
dist[others] = INF
```

Push:

```text
(0, start)
```

---

## Step 3 — Pop the minimum state

```text
(currentDist, node)
```

---

## Step 4 — Ignore stale states

If:

```text
currentDist != dist[node]
```

then a better route to this node was already discovered.

Skip it.

---

## Step 5 — Relax all neighbors

For every:

```text
node --weight--> next
```

calculate:

```text
newDist = currentDist + weight
```

If smaller, update.

---

## Step 6 — Continue

Repeat until:

```text
priority queue is empty
```

or, if the problem asks for only one target and standard Dijkstra conditions hold, until the target is safely popped with its shortest distance.

---

# 10. Worked Dry Run on the Visible Graph

We will use the clearly visible top-left part:

```text
        3
   S -------- B
   |          |
  5|          |4
   |          |
   +--------> C
        5
```

Edges:

```text
S → B = 3
S → C = 5
B → C = 4
```

Assume source:

```text
S
```

---

## Initial

```text
dist[S] = 0
dist[B] = INF
dist[C] = INF

pq = {(0,S)}
```

---

## Iteration 1

Pop:

```text
(0,S)
```

Relax `S → B`:

```text
0 + 3 = 3

dist[B] = 3
```

Relax `S → C`:

```text
0 + 5 = 5

dist[C] = 5
```

Now:

```text
pq = {(3,B), (5,C)}
```

---

## Iteration 2

Pop:

```text
(3,B)
```

Try `B → C`:

```text
3 + 4 = 7
```

Current:

```text
dist[C] = 5
```

Compare:

```text
7 < 5 ? NO
```

So do nothing.

This is the key Dijkstra habit:

```text
Never update just because an edge exists.

Update only if the complete new path is cheaper.
```

---

## Iteration 3

Pop:

```text
(5,C)
```

So final shortest distances for this small part are:

```text
dist[S] = 0
dist[B] = 3
dist[C] = 5
```

Even though:

```text
S → B → C
```

exists, its cost is:

```text
3 + 4 = 7
```

which is worse than direct:

```text
S → C = 5
```

---

## Extend to the next visible edge

The lecture also shows an edge of weight:

```text
C → D = 7
```

Then:

```text
dist[D]
= dist[C] + 7
= 5 + 7
= 12
```

If another route later reaches `D` with cost `< 12`, Dijkstra replaces `12` with that smaller value.

---

# 11. Why Greedy Finalization Works

Dijkstra repeatedly chooses:

```text
the unprocessed node with minimum tentative distance
```

Why is this safe?

Because every edge weight is:

```text
>= 0
```

Suppose node `u` currently has the globally smallest distance:

```text
dist[u]
```

Any alternative route reaching `u` later must first travel through some unprocessed node with distance at least:

```text
dist[u]
```

Then it adds a non-negative edge cost.

So it cannot produce something smaller than `dist[u]`.

That is why the minimum-distance node can be finalized.

---

# 12. Clean C++17 Template

This is the recommended compact format.

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll INF = 4e18;

vector<ll> dijkstra(int source, const vector<vector<pair<int, int>>>& graph) {
    int n = graph.size() - 1;

    vector<ll> dist(n + 1, INF);

    priority_queue<pair<ll, int>,
                   vector<pair<ll, int>>,
                   greater<pair<ll, int>>> pq;

    dist[source] = 0;
    pq.push({0, source});

    while (!pq.empty()) {
        auto [currentDist, node] = pq.top();
        pq.pop();

        if (currentDist != dist[node]) {
            continue;
        }

        for (auto [next, weight] : graph[node]) {
            ll newDist = currentDist + weight;

            if (newDist < dist[next]) {
                dist[next] = newDist;
                pq.push({newDist, next});
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

    vector<vector<pair<int, int>>> graph(n + 1);

    for (int i = 0; i < m; i++) {
        int u, v, weight;
        cin >> u >> v >> weight;

        graph[u].push_back({v, weight});
        graph[v].push_back({u, weight});
    }

    int source;
    cin >> source;

    vector<ll> dist = dijkstra(source, graph);

    for (int node = 1; node <= n; node++) {
        if (dist[node] == INF) {
            cout << node << ": INF\n";
        } else {
            cout << node << ": " << dist[node] << '\n';
        }
    }

    return 0;
}
```

---

## If the graph is directed

Do only:

```cpp
graph[u].push_back({v, weight});
```

Do **not** automatically add:

```cpp
graph[v].push_back({u, weight});
```

---

# 13. Complexity

With adjacency list + binary min-heap:

```text
Time:
O((V + E) log V)
```

Usually simplified to:

```text
O(E log V)
```

for typical connected sparse graphs.

Space:

```text
O(V + E)
```

---

# 14. Common Mistakes

| Mistake | Correct idea |
|---|---|
| Choosing route with fewest edges | Compare total edge cost |
| Using BFS for arbitrary weights | Use Dijkstra for non-negative weighted graph |
| Updating every neighbor blindly | Relax only if new path is cheaper |
| Using a max-heap | Need minimum distance first |
| Storing only node in PQ | Store `(distance, node)` |
| Forgetting stale PQ entries | `if (currentDist != dist[node]) continue;` |
| Using `int` for large total cost | Prefer `long long` |
| Using Dijkstra with negative weights | Dijkstra's finalization property fails |
| Treating story operations separately | Convert each operation into graph transitions |
| Memorizing `A-B` formula without checking cases | Derive it from the actual constraints |

---

# 15. Recognition Model

When a problem contains:

```text
states
+
many possible transitions
+
different non-negative costs
+
minimum total cost
```

think:

```text
STATE GRAPH
    ↓
weighted edges
    ↓
Dijkstra
```

Typical story words:

```text
minimum cost
minimum time
minimum energy
minimum operations with costs
cheapest route
minimum transformation cost
minimum distance
```

---

# 16. Final Mental Model

For this lecture example:

```text
Story / operations
        ↓
convert to nodes and weighted edges
        ↓
source = START
        ↓
dist[start] = 0
        ↓
always take smallest tentative distance
        ↓
relax neighboring transitions
        ↓
push improvements
        ↓
shortest costs obtained
```

The line to remember is:

```text
newDist = dist[current] + edgeWeight
```

then:

```text
if newDist < dist[next]
    update next
```

And for the visible `A < B` derivation:

```text
(t - A) + (t - B) = x
```

gives:

```text
2t = A + B + x
```

therefore:

```text
t = (A + B + x) / 2
```

The competitive-programming skill is not merely writing Dijkstra.

It is:

```text
recognize the hidden graph
        +
derive the correct transition cost
        +
then run Dijkstra
```
