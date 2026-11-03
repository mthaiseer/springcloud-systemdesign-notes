# Graphs Intermediate — Problem Solving
## TLE-Style Step-by-Step Self-Study Notes

> Based on the TLE **Graphs Intermediate - Problem Solving** lecture.
>
> Problems covered:
>
> 1. **Codeforces 2014E — Rendez-vous de Marian et Robin**
> 2. **Codeforces 1609D — Social Network**
> 3. **USACO Gold — Closing the Farm**
>
> This version adds the prerequisites needed to understand each problem, cleaner ASCII graphs, detailed modelling, one strong dry run per main problem, and readable C++17.

---

# Table of Contents

1. [What This Lecture Is Really Teaching](#1-what-this-lecture-is-really-teaching)
2. [Prerequisite 1 — Weighted Graph](#2-prerequisite-1--weighted-graph)
3. [Prerequisite 2 — Shortest Path and Dijkstra](#3-prerequisite-2--shortest-path-and-dijkstra)
4. [Prerequisite 3 — State in a Graph](#4-prerequisite-3--state-in-a-graph)
5. [Prerequisite 4 — DSU / Connected Components](#5-prerequisite-4--dsu--connected-components)
6. [Prerequisite 5 — Redundant Edge](#6-prerequisite-5--redundant-edge)
7. [Prerequisite 6 — Offline Reverse Processing](#7-prerequisite-6--offline-reverse-processing)
8. [Problem 1 — Rendez-vous de Marian et Robin](#8-problem-1--rendez-vous-de-marian-et-robin)
9. [Problem 2 — Social Network](#9-problem-2--social-network)
10. [Problem 3 — Closing the Farm](#10-problem-3--closing-the-farm)
11. [Pattern Comparison](#11-pattern-comparison)
12. [Common Mistakes](#12-common-mistakes)
13. [Final Mental Model](#13-final-mental-model)

---

# 1. What This Lecture Is Really Teaching

The three problems look unrelated.

```text
Problem 1 → people travelling with horses
Problem 2 → social connections
Problem 3 → barns closing
```

But each problem asks us to **change the graph model** before applying a known algorithm.

The main lesson is:

```text
Do not ask only:

"What algorithm is this?"

First ask:

"What should my graph/state/component represent?"
```

The three modelling patterns are:

```text
Problem 1
Normal graph
   ↓
State-expanded graph
   ↓
Dijkstra


Problem 2
Edges arriving one by one
   ↓
DSU components + redundant-edge count
   ↓
Greedy choose largest components


Problem 3
Vertex deletions are difficult
   ↓
Reverse time
   ↓
Vertex additions
   ↓
DSU
```

This is the real competitive-programming skill.

---

# 2. Prerequisite 1 — Weighted Graph

A weighted graph has a cost on every edge.

Example:

```text
       4
  (1)----- (2)
   |        |
 10|        |2
   |        |
  (3)----- (4)
       3
```

Meaning:

```text
1 → 2 costs 4
1 → 3 costs 10
2 → 4 costs 2
3 → 4 costs 3
```

A route's total cost is the sum of its edges.

Example:

```text
1 → 2 → 4

cost = 4 + 2
     = 6
```

---

## 2.1 Undirected Edge

If an edge is undirected:

```text
u ----- v
```

you can travel both ways.

Adjacency-list representation:

```cpp
vector<vector<pair<int, int>>> graph(n + 1);

graph[u].push_back({v, weight});
graph[v].push_back({u, weight});
```

---

# 3. Prerequisite 2 — Shortest Path and Dijkstra

Dijkstra finds shortest distances from one source when all edge weights are non-negative.

---

## 3.1 Relaxation

Suppose:

```text
dist[u] = 7
edge u → v costs 3
dist[v] = 15
```

Going through `u` gives:

```text
7 + 3 = 10
```

Since:

```text
10 < 15
```

update:

```text
dist[v] = 10
```

This is called **relaxation**.

---

## 3.2 Priority Queue

Dijkstra repeatedly takes the currently cheapest state.

```cpp
priority_queue<
    pair<long long, int>,
    vector<pair<long long, int>>,
    greater<pair<long long, int>>
> pq;
```

But later we will need a state like:

```text
(node, horseState)
```

so the queue will store more information.

---

## 3.3 Standard Dijkstra Skeleton

```cpp
using ll = long long;

const ll INF = 4e18;

vector<ll> dijkstra(
    int source,
    const vector<vector<pair<int, int>>>& graph
) {
    int n = graph.size() - 1;

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

        for (auto [nextNode, weight] : graph[node]) {
            ll newDist = currentDist + weight;

            if (newDist < dist[nextNode]) {
                dist[nextNode] = newDist;
                pq.push({newDist, nextNode});
            }
        }
    }

    return dist;
}
```

You do **not** need to memorize every character.

Remember:

```text
pop smallest distance
→ try all outgoing edges
→ relax neighbor
```

---

# 4. Prerequisite 3 — State in a Graph

This is the most important prerequisite for Problem 1.

Sometimes:

```text
"Where am I?"
```

is not enough to describe the situation.

We also need:

```text
"What condition am I in?"
```

Then the graph node becomes:

```text
(original node, state)
```

---

## 4.1 Tiny Example

Suppose you can travel in:

```text
mode 0 = walking
mode 1 = riding
```

Then physical vertex `3` becomes two algorithm states:

```text
(3,0) = at vertex 3 without horse
(3,1) = at vertex 3 with horse
```

These are different because future edge costs are different.

---

## 4.2 Better Visual

Original graph:

```text
(1) ----4---- (2)
```

State-expanded graph:

```text
NO HORSE LAYER

(1,0) ----4---- (2,0)


HORSE LAYER

(1,1) ----2---- (2,1)
```

The physical road is the same.

But its cost depends on state.

This technique is often called:

```text
state-expanded graph
layered graph
Dijkstra with state
```

---

# 5. Prerequisite 4 — DSU / Connected Components

Problems 2 and 3 use DSU.

DSU supports:

```text
find(x)
→ which connected component contains x?

unite(a,b)
→ merge components containing a and b
```

---

## 5.1 DSU Visual

Initially:

```text
[1]   [2]   [3]   [4]   [5]
```

After:

```text
unite(1,2)
unite(3,4)
```

we may have:

```text
Component A        Component B        Component C

    [1]                [3]                [5]
     |                  |
    [2]                [4]
```

After:

```text
unite(2,3)
```

```text
         [1]
        /   \
      [2]   [3]
             |
            [4]

         [5]
```

---

## 5.2 Component Size

DSU can store:

```text
size[root]
```

Example:

```text
Component root 1:
{1,2,3,4}

size[1] = 4
```

This is essential in **Social Network**.

---

## 5.3 Clean DSU Template

```cpp
class DSU {
private:
    vector<int> parent;
    vector<int> componentSize;

public:
    DSU(int n) {
        parent.resize(n + 1);
        componentSize.assign(n + 1, 1);

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
        int rootA = find(a);
        int rootB = find(b);

        if (rootA == rootB) {
            return false;
        }

        if (componentSize[rootA] < componentSize[rootB]) {
            swap(rootA, rootB);
        }

        parent[rootB] = rootA;
        componentSize[rootA] += componentSize[rootB];

        return true;
    }

    int size(int x) {
        return componentSize[find(x)];
    }
};
```

---

# 6. Prerequisite 5 — Redundant Edge

Suppose:

```text
1 --- 2 --- 3
```

Now add edge:

```text
1 --- 3
```

Before adding it, `1` and `3` were already connected.

So this new edge does **not** reduce the number of connected components.

It is redundant for connectivity.

Visual:

```text
    1
   / \
  2---3
```

The third edge creates a cycle.

For DSU:

```cpp
if (dsu.find(u) == dsu.find(v)) {
    // redundant edge
}
```

This becomes extremely important in Problem 2.

---

# 7. Prerequisite 6 — Offline Reverse Processing

Problem 3 removes vertices.

DSU is very good at:

```text
adding connections
merging components
```

DSU is not naturally good at:

```text
deleting a vertex
splitting a component
```

So we use a common trick:

```text
process time backwards
```

---

## 7.1 Simple Example

Forward process:

```text
Start:
1 --- 2 --- 3

Delete 2:

1     3
```

Deletion split one component into two.

Hard for DSU.

Reverse the process:

```text
Start with only:
1     3

Add 2 back:

1 --- 2 --- 3
```

Now we only perform unions.

Easy for DSU.

This pattern is:

```text
offline deletion
      ↓
reverse order
      ↓
addition
      ↓
DSU
```

---

# 8. Problem 1 — Rendez-vous de Marian et Robin

**Problem:** Codeforces 2014E  
**Link:** https://codeforces.com/contest/2014/problem/E

---

## 8.1 What Is the Problem Asking?

There is an undirected weighted graph.

```text
Marian starts at node 1.
Robin starts at node N.
```

Some vertices contain horses.

If a person reaches a horse vertex, they can mount a horse instantly.

After mounting:

```text
every future edge takes half its normal time
```

The horse stays for the rest of the journey.

They may meet only at a vertex.

Goal:

```text
Find the earliest possible meeting time.
```

If they can never meet:

```text
answer = -1
```

---

## 8.2 First Small Example

```text
1 --------10-------- 2
^                     ^
Marian               Robin

Horse at node 1
```

Marian immediately takes horse.

Travel time:

```text
10 / 2 = 5
```

Robin can wait at node 2.

So they meet at:

```text
time = 5
```

---

## 8.3 Why Normal Dijkstra Is Not Enough

Suppose we only store:

```text
dist[u]
```

But arriving at node `u` in two ways is not equivalent:

```text
Case A:
arrive at u without horse

Case B:
arrive at u with horse
```

Future edge costs differ.

Example:

```text
u ----10---- v
```

Without horse:

```text
cost = 10
```

With horse:

```text
cost = 5
```

Therefore:

```text
node alone is not enough state
```

---

## 8.4 Story → Variables

For each physical node `u`, create two states:

```text
(u,0) = at u without horse
(u,1) = at u with horse
```

Distance:

```text
dist[u][0]
dist[u][1]
```

This is exactly the two-layer idea drawn in the lecture.

---

## 8.5 Better Graph Drawing

Suppose original edge:

```text
u -------- w -------- v
```

Expanded states:

```text
WITHOUT HORSE LAYER

(u,0) -------- w -------- (v,0)


WITH HORSE LAYER

(u,1) ------ w/2 ------- (v,1)
```

If `u` has a horse:

```text
(u,0)
   |
   | cost 0
   v
(u,1)
```

So at a horse node we may move permanently from:

```text
no horse
→
horse
```

Never the reverse.

---

## 8.6 One Physical Graph → Two Logical Layers

Example:

```text
Original:

        8
   1 ------- 2
   |         |
  10         6
   |         |
   3 ------- 4
        4

Horse at 2
```

State graph:

```text
NO HORSE

(1,0) --8-- (2,0)
  |           |
 10           6
  |           |
(3,0) --4-- (4,0)

             |
             | take horse, cost 0
             v

WITH HORSE

(1,1) --4-- (2,1)
  |           |
  5           3
  |           |
(3,1) --2-- (4,1)
```

This is much cleaner than trying to mix both costs in one node.

---

## 8.7 Transition Rule

Suppose we are at:

```text
(node u, horseState)
```

First determine whether we can have a horse here.

```text
newState = horseState OR hasHorse[u]
```

Then for every edge:

```text
u --w-- v
```

cost is:

```text
w       if newState = 0
w / 2   if newState = 1
```

---

## 8.8 Why We Need Two Dijkstra Runs

Marian starts at:

```text
1
```

Robin starts at:

```text
N
```

We need shortest travel time from **both directions**.

Run:

```text
Dijkstra from 1
Dijkstra from N
```

Each run keeps two states per vertex.

Let:

```text
distFrom1[u][0/1]
distFromN[u][0/1]
```

---

## 8.9 Best Arrival Time at One Meeting Vertex

At vertex `u`, Marian's best time is:

```text
marian[u]
=
min(
    distFrom1[u][0],
    distFrom1[u][1]
)
```

Robin's best time:

```text
robin[u]
=
min(
    distFromN[u][0],
    distFromN[u][1]
)
```

---

## 8.10 Why `max(...)`?

Suppose at vertex `u`:

```text
Marian arrives at time 12.
Robin arrives at time 8.
```

Robin may wait.

Actual meeting occurs at:

```text
max(12,8)
=
12
```

So for every vertex:

```text
meetingTime(u)
=
max(marian[u], robin[u])
```

And globally:

```text
answer
=
min over all u of meetingTime(u)
```

This is the key final formula.

---

## 8.11 Mathematical Model

For each vertex `u`:

```text
A(u) = shortest time Marian can reach u
B(u) = shortest time Robin can reach u
```

Since the earlier person may wait:

```text
T(u) = max(A(u), B(u))
```

Try all possible meeting vertices:

```text
answer = min T(u)
             u
```

---

## 8.12 Detailed Dry Run

Consider:

```text
1 ----4---- 2 ----8---- 3 ----2---- 4 ----6---- 5

Horses at:
2 and 4

Marian starts at 1.
Robin starts at 5.
```

### Marian

From `1`:

```text
1 → 2
cost 4
```

At `2`, get horse.

Then:

```text
2 → 3 costs 8/2 = 4
3 → 4 costs 2/2 = 1
4 → 5 costs 6/2 = 3
```

Best arrival times:

```text
node 1 = 0
node 2 = 4
node 3 = 8
node 4 = 9
node 5 = 12
```

### Robin

From `5`:

```text
5 → 4 costs 6
```

At `4`, get horse.

Then:

```text
4 → 3 costs 2/2 = 1
3 → 2 costs 8/2 = 4
2 → 1 costs 4/2 = 2
```

Best:

```text
node 5 = 0
node 4 = 6
node 3 = 7
node 2 = 11
node 1 = 13
```

### Try Meeting Points

```text
u=1: max(0,13)  = 13
u=2: max(4,11)  = 11
u=3: max(8,7)   = 8
u=4: max(9,6)   = 9
u=5: max(12,0)  = 12
```

Minimum:

```text
answer = 8
```

Meet at node:

```text
3
```

---

## 8.13 Algorithm

```text
1. Read graph.
2. Mark horse vertices.

3. Run state-Dijkstra from node 1.
4. Run state-Dijkstra from node N.

5. For each vertex u:
       time1 = min(dist1[u][0], dist1[u][1])
       timeN = min(distN[u][0], distN[u][1])

       candidate = max(time1, timeN)

6. Answer = minimum candidate.

7. If answer remains INF:
       print -1
```

---

## 8.14 Complexity

There are:

```text
2N states
```

Each original edge can be relaxed in both states.

So:

```text
O((N + M) log N)
```

per Dijkstra up to constant factors.

We run it twice:

```text
still O((N + M) log N)
```

asymptotically.

---

## 8.15 Clean C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll INF = 4e18;

using Edge = pair<int, int>;
using State = tuple<ll, int, int>;

vector<array<ll, 2>> dijkstra(
    int source,
    const vector<vector<Edge>>& graph,
    const vector<int>& hasHorse
) {
    int n = graph.size() - 1;

    vector<array<ll, 2>> dist(n + 1, {INF, INF});

    priority_queue<State, vector<State>, greater<State>> pq;

    int startState = hasHorse[source] ? 1 : 0;

    dist[source][startState] = 0;
    pq.push({0, source, startState});

    while (!pq.empty()) {
        auto [currentDist, node, horseState] = pq.top();
        pq.pop();

        if (currentDist != dist[node][horseState]) {
            continue;
        }

        int nextState = horseState || hasHorse[node];

        if (nextState != horseState && currentDist < dist[node][nextState]) {
            dist[node][nextState] = currentDist;
            pq.push({currentDist, node, nextState});
        }

        for (auto [nextNode, weight] : graph[node]) {
            ll edgeCost = nextState ? weight / 2 : weight;
            ll newDist = currentDist + edgeCost;

            if (newDist < dist[nextNode][nextState]) {
                dist[nextNode][nextState] = newDist;
                pq.push({newDist, nextNode, nextState});
            }
        }
    }

    return dist;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        int n, m, h;
        cin >> n >> m >> h;

        vector<int> hasHorse(n + 1, 0);

        for (int i = 0; i < h; i++) {
            int node;
            cin >> node;
            hasHorse[node] = 1;
        }

        vector<vector<Edge>> graph(n + 1);

        for (int i = 0; i < m; i++) {
            int u, v, w;
            cin >> u >> v >> w;

            graph[u].push_back({v, w});
            graph[v].push_back({u, w});
        }

        auto fromStart = dijkstra(1, graph, hasHorse);
        auto fromEnd = dijkstra(n, graph, hasHorse);

        ll answer = INF;

        for (int node = 1; node <= n; node++) {
            ll marian = min(fromStart[node][0], fromStart[node][1]);
            ll robin = min(fromEnd[node][0], fromEnd[node][1]);

            if (marian == INF || robin == INF) {
                continue;
            }

            answer = min(answer, max(marian, robin));
        }

        cout << (answer == INF ? -1 : answer) << '\n';
    }

    return 0;
}
```

---

## 8.16 Recognition Model

If a shortest-path problem says:

```text
after collecting something,
future movement cost/rules change
```

think:

```text
Dijkstra + state
```

Examples:

```text
horse acquired or not
coupon used or not
free edge used or not
fuel amount
key collected
day/night mode
special power active
```

The physical vertex is not enough.

---

# 9. Problem 2 — Social Network

**Problem:** Codeforces 1609D  
**Link:** https://codeforces.com/problemset/problem/1609/D

---

## 9.1 What Is the Problem Asking?

There are:

```text
N people
```

Initially nobody knows anybody.

Conditions arrive one by one:

```text
x y
```

After processing the first `i` conditions, `x` and `y` from every required pair must be connected by some chain of acquaintances.

Exactly `i` introductions may be made.

After each condition, we want:

```text
maximum possible number of acquaintances
one person can have
```

---

## 9.2 First Important Translation

A connected group of people is a component.

Suppose:

```text
Component size = S
```

If everyone in this component can be arranged so one chosen person is connected to all others, that person can have:

```text
S - 1
```

acquaintances.

Therefore we want to maximize the size of the component we can effectively build.

---

## 9.3 DSU Handles Required Connectivity

For each required pair:

```text
(x,y)
```

Check:

```text
rootX = find(x)
rootY = find(y)
```

Two cases.

---

## 9.4 Case 1 — Different Components

Example:

```text
Component A          Component B

{1,2,3}              {4,5}
```

Condition:

```text
3 must connect to 4
```

They are currently disconnected.

We must spend one introduction connecting these components.

After union:

```text
{1,2,3,4,5}
```

No spare introduction was created.

---

## 9.5 Case 2 — Already Same Component

Suppose:

```text
1 --- 2 --- 3
```

Condition says:

```text
1 and 3 must be connected
```

They already are.

The condition is already satisfied.

But we are allowed exactly one new introduction for this step.

That introduction can be used somewhere else.

So we gain:

```text
one extra / spare edge
```

In code:

```cpp
if (find(x) == find(y)) {
    extraEdges++;
}
```

This is the crucial observation in the lecture diagrams.

---

## 9.6 What Can One Extra Edge Do?

Suppose current component sizes are:

```text
6, 4, 2, 2, 1
```

With:

```text
extraEdges = 0
```

we can only use the largest existing component:

```text
6
```

Maximum acquaintances:

```text
6 - 1 = 5
```

---

With:

```text
extraEdges = 1
```

one spare edge can connect two components.

Choose the two largest:

```text
6 + 4 = 10
```

Maximum acquaintances:

```text
10 - 1 = 9
```

---

With:

```text
extraEdges = 2
```

two spare edges can connect three components.

Choose:

```text
6 + 4 + 2 = 12
```

Maximum acquaintances:

```text
12 - 1 = 11
```

General rule:

```text
extraEdges = k

we can combine:
k + 1 components
```

---

## 9.7 Why Choose the Largest Components?

Suppose sizes:

```text
6, 4, 2, 2, 1
```

and we may join 3 components.

Candidate:

```text
6 + 4 + 2 = 12
```

Any replacement with a smaller component cannot improve the sum.

So greedily choose the:

```text
extraEdges + 1 largest components
```

---

## 9.8 Better Component Drawing

After some requirements, imagine DSU contains:

```text
Component A, size 6

     [1]
   / /|\ \
 [2][3][4][5][6]


Component B, size 4

     [7]
    / | \
  [8][9][10]


Component C, size 2

 [11]---[12]


Component D, size 1

 [13]
```

If:

```text
extraEdges = 2
```

we can spend two spare introductions to connect three components:

```text
A ---- B ---- C
```

Total people:

```text
6 + 4 + 2 = 12
```

A chosen person can have at most:

```text
12 - 1 = 11
```

acquaintances.

---

## 9.9 Lecture Sample-Style Evolution

Suppose requirements produce component sizes over time.

### Step 1

```text
unite(1,2)
```

Components:

```text
{1,2}, {3}, {4}, {5}, ...
```

Largest size:

```text
2
```

Answer:

```text
2 - 1 = 1
```

---

### Step 2

```text
unite(2,3)
```

Components:

```text
{1,2,3}, {4}, {5}, ...
```

Largest:

```text
3
```

Answer:

```text
3 - 1 = 2
```

---

### Step 3

```text
unite(3,4)
```

Largest:

```text
4
```

Answer:

```text
3
```

---

### Step 4 — Redundant Requirement

Suppose requirement is:

```text
1 4
```

But `1` and `4` already belong to the same component.

So:

```text
extraEdges = 1
```

Now we may connect the largest **two** components.

If sizes are:

```text
4, 1, 1, 1...
```

use:

```text
4 + 1 = 5
```

Answer:

```text
5 - 1 = 4
```

This explains why a redundant edge can increase the answer.

---

## 9.10 Algorithm

After every new requirement `(x,y)`:

```text
1. If x and y are in different components:
       unite(x,y)

2. Else:
       extraEdges++

3. Collect sizes of all current DSU components.

4. Sort sizes descending.

5. takeCount = extraEdges + 1

6. Sum the largest `takeCount` component sizes.

7. answer = sum - 1
```

Because `N <= 1000`, recomputing and sorting component sizes each query is acceptable and keeps the solution easy to understand.

---

## 9.11 Detailed Dry Run

Let:

```text
N = 7
```

Requirements:

```text
1 2
3 4
2 4
7 6
6 5
1 7
```

### Query 1 — `(1,2)`

Different components.

Merge:

```text
{1,2}, {3}, {4}, {5}, {6}, {7}
```

Sizes:

```text
2,1,1,1,1,1
```

Extra:

```text
0
```

Take largest 1:

```text
2
```

Answer:

```text
2 - 1 = 1
```

---

### Query 2 — `(3,4)`

Merge:

```text
{1,2}, {3,4}, {5}, {6}, {7}
```

Sizes:

```text
2,2,1,1,1
```

Extra:

```text
0
```

Largest 1:

```text
2
```

Answer:

```text
1
```

---

### Query 3 — `(2,4)`

`2` belongs to `{1,2}`.

`4` belongs to `{3,4}`.

Merge:

```text
{1,2,3,4}, {5}, {6}, {7}
```

Sizes:

```text
4,1,1,1
```

Answer:

```text
4 - 1 = 3
```

---

### Query 4 — `(7,6)`

Merge:

```text
{1,2,3,4}, {6,7}, {5}
```

Sizes:

```text
4,2,1
```

Answer:

```text
4 - 1 = 3
```

---

### Query 5 — `(6,5)`

Merge:

```text
{1,2,3,4}, {5,6,7}
```

Sizes:

```text
4,3
```

Answer:

```text
4 - 1 = 3
```

---

### Query 6 — `(1,7)`

Different components, so merge:

```text
{1,2,3,4,5,6,7}
```

Size:

```text
7
```

Answer:

```text
7 - 1 = 6
```

Outputs:

```text
1
1
3
3
3
6
```

---

## 9.12 Complexity

For each query:

```text
DSU operation ≈ O(α(N))
```

Then collecting component sizes:

```text
O(N)
```

Sorting them:

```text
O(N log N)
```

For `D` requirements:

```text
O(D × N log N)
```

With:

```text
N <= 1000
D <= N-1
```

this is fine.

---

## 9.13 Clean C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

class DSU {
private:
    vector<int> parent;
    vector<int> componentSize;

public:
    DSU(int n) {
        parent.resize(n + 1);
        componentSize.assign(n + 1, 1);

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
        int rootA = find(a);
        int rootB = find(b);

        if (rootA == rootB) {
            return false;
        }

        if (componentSize[rootA] < componentSize[rootB]) {
            swap(rootA, rootB);
        }

        parent[rootB] = rootA;
        componentSize[rootA] += componentSize[rootB];

        return true;
    }

    int rootSize(int root) const {
        return componentSize[root];
    }
};

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, d;
    cin >> n >> d;

    DSU dsu(n);

    int extraEdges = 0;

    while (d--) {
        int x, y;
        cin >> x >> y;

        if (!dsu.unite(x, y)) {
            extraEdges++;
        }

        vector<int> sizes;

        for (int node = 1; node <= n; node++) {
            if (dsu.find(node) == node) {
                sizes.push_back(dsu.rootSize(node));
            }
        }

        sort(sizes.rbegin(), sizes.rend());

        int componentsToJoin = min(
            (int)sizes.size(),
            extraEdges + 1
        );

        int people = 0;

        for (int i = 0; i < componentsToJoin; i++) {
            people += sizes[i];
        }

        cout << people - 1 << '\n';
    }

    return 0;
}
```

---

## 9.14 Recognition Model

Think of this pattern when you see:

```text
edges added online
+
DSU
+
some edges connect already-connected nodes
```

Ask:

```text
"What useful resource does a redundant edge give me?"
```

Sometimes it becomes:

```text
extra operation
extra connection
extra cycle edge
budget to join more components
```

Then component sizes may matter.

---

# 10. Problem 3 — Closing the Farm

**Problem:** USACO Gold — Closing the Farm  
**Link:** https://usaco.org/index.php?page=viewproblem2&cpid=646

---

## 10.1 What Is the Problem Asking?

We have:

```text
N barns
M bidirectional roads
```

Barns are closed one by one in a given order.

When a barn closes:

```text
the barn disappears
+
all roads touching it disappear
```

We need to know whether all currently open barns are connected at each required stage.

---

## 10.2 Why the Direct Approach Is Awkward

Imagine:

```text
1 --- 2 --- 3
      |
      4
```

Close barn `2`.

Now:

```text
1     3

      4
```

One deletion split one connected component into three.

DSU cannot easily perform:

```text
"undo this union"
```

So direct forward simulation is not DSU-friendly.

---

## 10.3 Key Observation — Reverse the Process

Forward:

```text
open graph
→ close barn
→ close barn
→ close barn
```

Reverse:

```text
all inactive
→ open barn
→ open barn
→ open barn
```

Opening a barn only adds connectivity.

That is perfect for DSU.

---

## 10.4 Better Visual

Suppose original graph:

```text
1 --- 2 --- 3
      |
      4 --- 5
```

Closure order:

```text
3, 5, 2, 1, 4
```

Forward is difficult:

```text
delete 3
delete 5
delete 2
...
```

Reverse order:

```text
4, 1, 2, 5, 3
```

Start:

```text
1 inactive
2 inactive
3 inactive
4 inactive
5 inactive
```

Then activate one at a time.

---

## 10.5 What Happens When a Barn Activates?

Suppose we activate:

```text
barn x
```

Mark:

```text
active[x] = true
```

Initially the new barn forms one new component:

```text
components++
```

Then inspect every original neighbor `v`.

If:

```text
active[v] == true
```

the road:

```text
x --- v
```

is now active.

So:

```text
unite(x,v)
```

If the union succeeds:

```text
components--
```

---

## 10.6 Connectivity Condition

Among currently active barns:

```text
fully connected
```

means:

```text
exactly one connected component
```

So:

```text
components == 1
```

---

## 10.7 Detailed Dry Run — Official Sample Shape

Graph:

```text
1 --- 2 --- 3 --- 4
```

Closure order:

```text
3, 4, 1, 2
```

We process reverse:

```text
2, 1, 4, 3
```

---

### Reverse Step 1 — Activate `2`

Active graph:

```text
[2]
```

Components:

```text
1
```

Connected?

```text
YES
```

This corresponds to the final forward state before the last closure.

Store:

```text
answer[3] = YES
```

---

### Reverse Step 2 — Activate `1`

Original edge:

```text
1 --- 2
```

Both active.

Union.

```text
1 --- 2
```

Components:

```text
1
```

Connected:

```text
YES
```

Store:

```text
answer[2] = YES
```

---

### Reverse Step 3 — Activate `4`

Node `3` is still inactive.

So active graph:

```text
1 --- 2      4
```

Components:

```text
2
```

Connected:

```text
NO
```

Store:

```text
answer[1] = NO
```

---

### Reverse Step 4 — Activate `3`

Now both neighbors `2` and `4` are active.

Activate:

```text
1 --- 2 --- 3 --- 4
```

The new node initially adds a component:

```text
components: 2 → 3
```

Union `(3,2)`:

```text
3 → 2
```

Union `(3,4)`:

```text
2 → 1
```

Connected:

```text
YES
```

Store:

```text
answer[0] = YES
```

---

### Output in Original Order

```text
answer[0] = YES
answer[1] = NO
answer[2] = YES
answer[3] = YES
```

Output:

```text
YES
NO
YES
YES
```

---

## 10.8 Why the Indexing Works

Let closure order be:

```text
order[0], order[1], ..., order[N-1]
```

When processing:

```text
i = N-1 down to 0
```

we activate:

```text
order[i]
```

After this activation, the active set equals the farm state:

```text
before order[i] is closed
```

Therefore:

```text
answer[i]
```

is exactly the answer required for that forward stage.

---

## 10.9 Algorithm

```text
1. Read graph.
2. Read closure order.

3. Mark every barn inactive.
4. components = 0.

5. For i from N-1 down to 0:

       x = order[i]

       activate x
       components++

       for every neighbor v of x:

           if v is active:
               if unite(x,v) succeeds:
                   components--

       answer[i] = (components == 1)

6. Print answer[0..N-1].
```

---

## 10.10 Complexity

Every vertex activates once.

Every edge is inspected from adjacency lists.

DSU operations are almost constant.

Total:

```text
O((N + M) α(N))
```

which is effectively near-linear.

---

## 10.11 Clean C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

class DSU {
private:
    vector<int> parent;
    vector<int> componentSize;

public:
    DSU(int n) {
        parent.resize(n + 1);
        componentSize.assign(n + 1, 1);

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
        int rootA = find(a);
        int rootB = find(b);

        if (rootA == rootB) {
            return false;
        }

        if (componentSize[rootA] < componentSize[rootB]) {
            swap(rootA, rootB);
        }

        parent[rootB] = rootA;
        componentSize[rootA] += componentSize[rootB];

        return true;
    }
};

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<vector<int>> graph(n + 1);

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;

        graph[u].push_back(v);
        graph[v].push_back(u);
    }

    vector<int> order(n);

    for (int i = 0; i < n; i++) {
        cin >> order[i];
    }

    DSU dsu(n);

    vector<int> active(n + 1, 0);
    vector<string> answer(n);

    int components = 0;

    for (int i = n - 1; i >= 0; i--) {
        int node = order[i];

        active[node] = 1;
        components++;

        for (int neighbor : graph[node]) {
            if (!active[neighbor]) {
                continue;
            }

            if (dsu.unite(node, neighbor)) {
                components--;
            }
        }

        answer[i] = (components == 1 ? "YES" : "NO");
    }

    for (const string& result : answer) {
        cout << result << '\n';
    }

    return 0;
}
```

---

## 10.12 Recognition Model

If a problem says:

```text
delete nodes
delete edges
close cities
remove roads
destroy connections
```

and all deletions are known beforehand, ask:

```text
Can I process the operations backwards?
```

Reverse may turn:

```text
deletion
```

into:

```text
addition
```

Then DSU often becomes possible.

This is one of the most important offline graph patterns.

---

# 11. Pattern Comparison

| Problem | Hidden Difficulty | Modelling Trick | Main Algorithm |
|---|---|---|---|
| Rendez-vous de Marian et Robin | Edge cost changes after horse | `(node, horseState)` | Dijkstra |
| Social Network | Redundant edges still have value | DSU + spare-edge count + component sizes | DSU + Greedy |
| Closing the Farm | DSU cannot delete/split easily | Reverse deletions into additions | Offline DSU |

---

## 11.1 Same Question, Different Representation

### Problem 1

Do not model:

```text
only node
```

Model:

```text
node + state
```

---

### Problem 2

Do not model:

```text
only edges
```

Model:

```text
components + their sizes + number of redundant edges
```

---

### Problem 3

Do not process:

```text
forward deletions
```

Model time backwards:

```text
reverse additions
```

---

# 12. Common Mistakes

---

## 12.1 Problem 1 — Using One Distance Per Node

Wrong:

```cpp
dist[node]
```

because:

```text
arrived without horse
!=
arrived with horse
```

Use:

```cpp
dist[node][2]
```

---

## 12.2 Problem 1 — Forgetting Waiting

Meeting at node `u` is not:

```text
distA + distB
```

It is:

```text
max(distA, distB)
```

because one person may wait.

Then minimize over all meeting vertices.

---

## 12.3 Problem 1 — Taking Minimum State Too Early

During Dijkstra, keep both states.

Do not collapse:

```text
min(dist[u][0], dist[u][1])
```

inside the algorithm.

The two states have different future transitions.

Only combine them when evaluating the final arrival time.

---

## 12.4 Problem 2 — Treating Redundant Edge as Useless

In this problem:

```text
already connected pair
```

does not mean:

```text
ignore the operation completely
```

It gives:

```text
one spare introduction
```

That spare edge lets us join one additional component.

---

## 12.5 Problem 2 — Taking `extraEdges` Components

With:

```text
k spare edges
```

you can connect:

```text
k + 1 components
```

because:

```text
1 edge joins 2 components
2 edges can join 3 components
3 edges can join 4 components
```

---

## 12.6 Problem 2 — Forgetting `-1`

If a connected group contains:

```text
S people
```

one person can know at most:

```text
S - 1
```

other people.

So output:

```text
sumOfChosenComponentSizes - 1
```

---

## 12.7 Problem 3 — Trying to Delete from DSU

Standard DSU is not designed for component splitting.

Do not fight the data structure.

Reverse the process.

---

## 12.8 Problem 3 — Union with Inactive Neighbor

When activating node `u`, only union with:

```text
active neighbors
```

An original road does not exist in the current reverse graph if the other endpoint has not been activated yet.

---

# 13. Final Mental Model

The lecture's three strongest recognition rules are:

```text
1. Same vertex but different future behaviour?
   → add STATE

2. Components merge and redundant edges matter?
   → DSU + COMPONENT INFORMATION

3. Deletions are hard but known in advance?
   → REVERSE TIME
```

---

# 13.1 Problem 1 in One Picture

```text
Physical node u
      |
      +----------------------+
      |                      |
      v                      v
  (u, no horse)         (u, horse)
      |                      |
   edge cost w           edge cost w/2
      |                      |
      +---- horse node ------+
            cost 0
```

Then:

```text
Dijkstra from 1
+
Dijkstra from N
+
min over meeting node:
max(arrival1, arrivalN)
```

---

# 13.2 Problem 2 in One Picture

```text
DSU components:

size:
[6] [4] [2] [2] [1]

extraEdges = 2
       |
       v
can combine 3 components

choose largest:

6 + 4 + 2 = 12

answer:
12 - 1 = 11
```

---

# 13.3 Problem 3 in One Picture

```text
FORWARD

connected graph
    ↓ delete
smaller graph
    ↓ delete
smaller graph

DSU dislikes this.


REVERSE

empty/inactive
    ↓ add node
small graph
    ↓ add node
larger graph
    ↓ add node
larger graph

DSU loves this.
```

---

# 13.4 Competitive Programming Checklist

When reading a graph problem, ask in this order:

```text
1. What does a node really represent?

2. Is one physical node enough,
   or do I need (node,state)?

3. Is the graph changing?

4. Are components only merging?
   → DSU

5. Are operations deleting things?
   → Can I reverse them?

6. Do redundant edges create some resource?

7. What information should each component store?

8. Is the final answer:
   shortest path?
   connected components?
   component sizes?
   min/max over meeting points?
```

This modelling checklist is more valuable than memorizing any one solution.

---

# Practice Order

Use this order:

```text
1. Closing the Farm
   → easiest modelling transformation:
     reverse deletion → addition

2. Social Network
   → DSU + second-layer observation:
     redundant edges become resource

3. Rendez-vous de Marian et Robin
   → state-expanded shortest path
```

For mastery, solve each once without looking at code after understanding the model.
