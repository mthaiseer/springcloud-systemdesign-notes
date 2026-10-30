# Dijkstra's Algorithm — SSSP with Non-Negative Weights

> Self-study competitive-programming note based on the provided AlgoZenith Dijkstra concept + code screenshots.  
> Focus: prerequisites → SSSP decision tree → why BFS/0-1 BFS are not enough → priority queue → relaxation → finalization → negative-weight failure → clean C++17.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [SSSP Decision Tree](#2-sssp-decision-tree)
3. [What Dijkstra Solves](#3-what-dijkstra-solves)
4. [Why Ordinary BFS Is Not Enough](#4-why-ordinary-bfs-is-not-enough)
5. [Why 0-1 BFS Is Not Enough](#5-why-0-1-bfs-is-not-enough)
6. [The Main Dijkstra Idea](#6-the-main-dijkstra-idea)
7. [Distance Array](#7-distance-array)
8. [Relaxation](#8-relaxation)
9. [Why We Need a Min-Priority Queue](#9-why-we-need-a-min-priority-queue)
10. [Visited / Finalization](#10-visited--finalization)
11. [Step-by-Step Algorithm](#11-step-by-step-algorithm)
12. [Detailed Dry Run](#12-detailed-dry-run)
13. [Why Negative Edges Break Dijkstra](#13-why-negative-edges-break-dijkstra)
14. [Lecture-Style C++17](#14-lecture-style-c17)
15. [Recommended Compact C++17](#15-recommended-compact-c17)
16. [Complexity](#16-complexity)
17. [Common Mistakes](#17-common-mistakes)
18. [Recognition Model](#18-recognition-model)
19. [Final Mental Model](#19-final-mental-model)

---

# 1. Prerequisites

You should know:

- graph basics,
- directed vs undirected graph,
- weighted edges,
- adjacency list,
- BFS,
- 0-1 BFS,
- shortest-path distance,
- relaxation,
- `priority_queue`,
- min-heap,
- visited / finalized node idea.

Learning chain:

```text
Unweighted graph
      ↓
BFS

Weights only 0 / 1
      ↓
0-1 BFS

General non-negative weights
      ↓
Dijkstra

Negative edges may exist
      ↓
Bellman-Ford / another suitable method
```

## 1.1 Weighted edge

```text
1 --3--> 3
1 --1--> 2
2 --5--> 3
```

The number on an edge is its travel cost.

Path:

```text
1 → 2 → 3
```

Cost:

```text
1 + 5 = 6
```

Direct path:

```text
1 → 3
```

Cost:

```text
3
```

So the direct path is shorter.

## 1.2 SSSP

SSSP means:

```text
Single Source Shortest Path
```

Given one source node `S`, compute:

```text
shortest distance from S to every node
```

---

# 2. SSSP Decision Tree

The lecture compares the main SSSP tools:

```text
                         SSSP
                          │
            ┌─────────────┼─────────────┐
            │             │             │
            ▼             ▼             ▼
         BFS          0-1 BFS       Dijkstra
     equal weights    weights 0/1   non-negative
                                      weights

                          │
                          ▼
                    Bellman-Ford
                  negative edges allowed
```

Useful recognition rule:

```text
All edges equal?              → BFS
Only weights 0 and 1?         → 0-1 BFS
All weights non-negative?     → Dijkstra
Negative edge exists?         → Do NOT use ordinary Dijkstra
```

---

# 3. What Dijkstra Solves

Dijkstra solves shortest paths from one source when edge weights are:

```text
non-negative
```

Examples:

```text
0, 1, 2, 7, 100, ...
```

It works for directed or undirected graphs as long as the relevant edge weights are non-negative.

---

# 4. Why Ordinary BFS Is Not Enough

BFS is perfect when every edge effectively costs the same.

For example:

```text
every edge cost = 1
```

Then fewer edges means smaller distance.

But consider:

```text
1 --10--> 2
1 --1--> 3
3 --1--> 2
```

Path A:

```text
1 → 2
cost = 10
```

Path B:

```text
1 → 3 → 2
cost = 1 + 1 = 2
```

Path B uses more edges but has smaller total cost.

So BFS level count is not enough.

---

# 5. Why 0-1 BFS Is Not Enough

0-1 BFS depends on:

```text
edge weight ∈ {0, 1}
```

From distance `d`, new distances are only:

```text
d
d + 1
```

So a deque is enough.

But with Dijkstra we may have:

```text
weight = 2
weight = 5
weight = 100
```

From distance `d`, candidates may be:

```text
d + 2
d + 5
d + 100
```

Now we need a structure that always gives us the smallest tentative distance.

That is a min-priority queue.

---

# 6. The Main Dijkstra Idea

Core idea:

```text
Always process the currently reachable
UNFINALIZED node with minimum distance.
```

Suppose:

```text
node 2 → 8
node 3 → 3
node 4 → 12
node 5 → 5
```

Process:

```text
node 3
```

because `3` is the minimum tentative distance.

Then relax all outgoing edges from node 3.

---

# 7. Distance Array

Define:

```text
dist[node]
```

Meaning:

```text
smallest distance found so far
from source to node
```

Initialization:

```text
dist[source] = 0
dist[others] = INF
```

Example:

```text
source = 1

node:   1   2   3   4
dist:   0   ∞   ∞   ∞
```

---

# 8. Relaxation

Suppose:

```text
node --weight--> next
```

Candidate route:

```text
dist[node] + weight
```

If:

```text
dist[node] + weight < dist[next]
```

update:

```text
dist[next] = dist[node] + weight
```

C++:

```cpp
if (dist[next] > dist[node] + weight) {
    dist[next] = dist[node] + weight;
}
```

Tiny example:

```text
dist[node] = 7
weight = 4
dist[next] = 20

candidate = 7 + 4 = 11
11 < 20

so dist[next] = 11
```

After improvement, push the new state into the priority queue.

---

# 9. Why We Need a Min-Priority Queue

We repeatedly need:

```text
smallest current distance
```

Store:

```text
{distance, node}
```

Example:

```text
{3, 5}
```

means:

```text
node = 5
distance = 3
```

Recommended C++:

```cpp
priority_queue<pair<long long, int>,
               vector<pair<long long, int>>,
               greater<pair<long long, int>>> pq;
```

The top always has the smallest distance.

---

# 10. Visited / Finalization

The lecture code uses:

```cpp
if (vis[node]) {
    continue;
}

vis[node] = 1;
```

Interpretation:

```text
when a node is popped for the first valid time
with globally minimum tentative distance,
its shortest distance is finalized
```

A node may appear multiple times.

Example:

```text
first:
dist[4] = 15
push {15,4}

later:
dist[4] = 7
push {7,4}
```

Priority queue contains both states.

The better one is processed first, and the stale one is skipped later.

---

# 11. Step-by-Step Algorithm

## Step 1 — Initialize

```text
dist[source] = 0
dist[others] = INF
```

## Step 2 — Push source

```text
pq = {(0, source)}
```

## Step 3 — Extract minimum

Pop:

```text
(distance, node)
```

with smallest distance.

## Step 4 — Skip stale/finalized node

If already finalized, skip it.

Otherwise mark it finalized.

## Step 5 — Relax outgoing edges

For every:

```text
node --weight--> next
```

check:

```text
dist[node] + weight < dist[next]
```

## Step 6 — Update and push

If improved:

```text
dist[next] = dist[node] + weight
```

then push:

```text
{dist[next], next}
```

## Step 7 — Repeat

Continue until the priority queue becomes empty.

---

# 12. Detailed Dry Run

Use:

```text
        4
   1 --------> 2
   |           |
  1|           |2
   v           v
   3 --------> 4
        5

and 3 → 2 with weight 1
```

Edges:

```text
1 → 2, weight 4
1 → 3, weight 1
3 → 4, weight 5
2 → 4, weight 2
3 → 2, weight 1
```

Source:

```text
1
```

Initial:

```text
dist[1] = 0
dist[2] = INF
dist[3] = INF
dist[4] = INF

pq = {(0,1)}
```

### Iteration 1

Pop `(0,1)`.

Relax `1 → 2`:

```text
0 + 4 = 4

dist[2] = 4
push (4,2)
```

Relax `1 → 3`:

```text
0 + 1 = 1

dist[3] = 1
push (1,3)
```

Priority queue logically:

```text
(1,3)
(4,2)
```

### Iteration 2

Pop `(1,3)`.

Relax `3 → 4`:

```text
1 + 5 = 6

dist[4] = 6
push (6,4)
```

Relax `3 → 2`:

```text
1 + 1 = 2
```

Current:

```text
dist[2] = 4
```

Better:

```text
2 < 4
```

Update:

```text
dist[2] = 2
push (2,2)
```

Queue logically:

```text
(2,2)
(4,2)   ← old state
(6,4)
```

### Iteration 3

Pop `(2,2)`.

Relax `2 → 4`:

```text
2 + 2 = 4
```

Current:

```text
dist[4] = 6
```

Update:

```text
dist[4] = 4
push (4,4)
```

Queue:

```text
(4,2)   ← stale
(4,4)
(6,4)   ← stale later
```

### Iteration 4

Pop old `(4,2)`.

Node 2 is already finalized, so skip.

### Iteration 5

Pop `(4,4)` and finalize node 4.

Final distances:

```text
node:   1  2  3  4
dist:   0  2  1  4
```

Shortest path to 4:

```text
1 → 3 → 2 → 4
```

Cost:

```text
1 + 1 + 2 = 4
```

---

# 13. Why Negative Edges Break Dijkstra

The lecture explicitly introduces a negative-edge example.

Dijkstra relies on:

```text
once the smallest tentative node is finalized,
its distance will never improve later
```

That is safe only when every future edge adds:

```text
weight >= 0
```

With a negative edge, a later route can reduce an already-finalized distance.

Example:

```text
suppose dist[2] was finalized as 5

later we discover:
dist[x] = 12
x → 2 has weight -10
```

New candidate:

```text
12 + (-10) = 2
```

Now:

```text
2 < 5
```

So the supposedly finalized answer was wrong.

Therefore:

```text
ordinary Dijkstra requires non-negative edge weights
```

If negative edges exist, consider an algorithm such as Bellman-Ford depending on the problem.

---

# 14. Lecture-Style C++17

The lecture uses a custom priority rule and a visited array.

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll INF = 4e18;

struct CompareState {
    bool operator()(const pair<ll, int>& a, const pair<ll, int>& b) const {
        return a.first > b.first;
    }
};

void dijkstra(int source, const vector<vector<pair<int, int>>>& graph, vector<ll>& dist) {
    int n = graph.size() - 1;

    vector<int> vis(n + 1, 0);
    dist.assign(n + 1, INF);

    priority_queue<pair<ll, int>, vector<pair<ll, int>>, CompareState> pq;

    dist[source] = 0;
    pq.push({0, source});

    while (!pq.empty()) {
        auto [currentDist, node] = pq.top();
        pq.pop();

        if (vis[node]) {
            continue;
        }

        vis[node] = 1;

        for (auto [next, weight] : graph[node]) {
            if (dist[next] > dist[node] + weight) {
                dist[next] = dist[node] + weight;
                pq.push({dist[next], next});
            }
        }
    }
}
```

---

# 15. Recommended Compact C++17

This version avoids a separate `vis[]` array and skips stale entries directly.

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll INF = 4e18;

void dijkstra(int source, const vector<vector<pair<int, int>>>& graph, vector<ll>& dist) {
    int n = graph.size() - 1;

    dist.assign(n + 1, INF);

    priority_queue<pair<ll, int>, vector<pair<ll, int>>, greater<pair<ll, int>>> pq;

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
}
```

Full undirected template:

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll INF = 4e18;

void dijkstra(int source, const vector<vector<pair<int, int>>>& graph, vector<ll>& dist) {
    int n = graph.size() - 1;

    dist.assign(n + 1, INF);

    priority_queue<pair<ll, int>, vector<pair<ll, int>>, greater<pair<ll, int>>> pq;

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

    vector<ll> dist;
    dijkstra(source, graph, dist);

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

# 16. Complexity

Using adjacency list + binary heap:

```text
Time = O((V + E) log V)
```

Often written as:

```text
O(E log V)
```

for sparse connected graphs.

Space:

```text
O(V + E)
```

Comparison:

```text
BFS
weights: equal
Time: O(V + E)

0-1 BFS
weights: only 0 and 1
Time: O(V + E)

Dijkstra
weights: non-negative
Time: O((V + E) log V)

Bellman-Ford
negative edges allowed
Time: O(VE)
```

---

# 17. Common Mistakes

| Mistake | Correct Idea |
|---|---|
| Using normal max-heap | Use min-heap |
| Storing `{node, distance}` then comparing wrong field | Prefer `{distance, node}` |
| Using `int` for huge path sums | Use `long long` |
| Forgetting relaxation | Update only on better distance |
| Marking node visited when pushed | Finalize when correctly popped |
| Ignoring duplicate PQ entries | Skip visited/outdated entries |
| Using Dijkstra with negative edges | Use a suitable algorithm such as Bellman-Ford |
| Forgetting reverse edge in undirected graph | Add both directions |
| Using BFS for arbitrary weights | BFS is for equal edge cost |
| Using 0-1 BFS for weights beyond 0/1 | Use Dijkstra for general non-negative weights |

---

# 18. Recognition Model

When you see:

```text
minimum travel cost
minimum time
minimum distance
minimum toll
minimum fuel cost
shortest path in weighted graph
```

ask:

```text
What are the edge weights?
```

Then:

```text
All equal?
→ BFS

Only 0 and 1?
→ 0-1 BFS

General non-negative weights?
→ Dijkstra

Negative edges?
→ Dijkstra is unsafe
```

---

# 19. Final Mental Model

Dijkstra is not mainly about memorizing a priority queue.

The reasoning is:

```text
1. dist[node] = best known cost

2. Always select the smallest tentative distance

3. Once that node is safely minimum,
   finalize it

4. Relax all outgoing edges

5. Any improved neighbor goes back
   into the min-priority queue
```

Visual:

```text
                source
                  │
                  ▼
          dist[source] = 0
                  │
                  ▼
        push (0, source) into
          MIN priority queue
                  │
                  ▼
         pop smallest distance
                  │
                  ▼
          finalize that node
                  │
                  ▼
           relax neighbors
                  │
          ┌───────┴────────┐
          │                │
      no improvement    improvement
          │                │
          ▼                ▼
       ignore       update dist[next]
                           │
                           ▼
                  push new state
```

Core relaxation:

```cpp
ll newDist = currentDist + weight;

if (newDist < dist[next]) {
    dist[next] = newDist;
    pq.push({newDist, next});
}
```

Most important recognition rule:

```text
non-negative weighted shortest path
                ↓
             Dijkstra
```
