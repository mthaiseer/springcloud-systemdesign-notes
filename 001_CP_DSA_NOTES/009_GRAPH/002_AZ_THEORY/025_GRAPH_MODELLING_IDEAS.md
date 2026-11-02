# Graph Modelling Ideas — Supernodes, Compression and Bitmask States

> **Self-study competitive programming note**
>
> Goal: learn how to convert a problem into a graph **before** choosing BFS/Dijkstra.
>
> Core lecture ideas:
>
> 1. **Many nodes share one common relation** → create a **helper/supernode** instead of a dense clique.
> 2. **A fixed-length binary configuration is the state** → encode the whole configuration as a **bitmask node**.

---

# Table of Contents

1. [Why Graph Modelling Matters](#1-why-graph-modelling-matters)
2. [Prerequisites](#2-prerequisites)
3. [Universal Modelling Template](#3-universal-modelling-template)
4. [Idea 1 — Array Positions as Nodes](#4-idea-1--array-positions-as-nodes)
5. [Naive Same-Value Edges](#5-naive-same-value-edges)
6. [Why the Naive Model Is Too Large](#6-why-the-naive-model-is-too-large)
7. [Supernode Compression](#7-supernode-compression)
8. [Why the Cost Is Preserved](#8-why-the-cost-is-preserved)
9. [Small Dry Run](#9-small-dry-run)
10. [Node and Edge Count](#10-node-and-edge-count)
11. [Complexity](#11-complexity)
12. [C++ — Supernode Graph + Dijkstra](#12-c--supernode-graph--dijkstra)
13. [When to Recognize Supernodes](#13-when-to-recognize-supernodes)
14. [Idea 2 — Binary String as a Graph State](#14-idea-2--binary-string-as-a-graph-state)
15. [Prerequisite — Bitmask](#15-prerequisite--bitmask)
16. [Convert String to Mask](#16-convert-string-to-mask)
17. [One-Bit Flip = One Edge](#17-one-bit-flip--one-edge)
18. [Dry Run — `101`](#18-dry-run--101)
19. [How Many States and Edges?](#19-how-many-states-and-edges)
20. [Banned States](#20-banned-states)
21. [Explicit vs Implicit Bitmask Graph](#21-explicit-vs-implicit-bitmask-graph)
22. [C++ — BFS on Bitmask States](#22-c--bfs-on-bitmask-states)
23. [General Modelling Checklist](#23-general-modelling-checklist)
24. [Common Mistakes](#24-common-mistakes)
25. [Final Recognition Model](#25-final-recognition-model)

---

# 1. Why Graph Modelling Matters

In many CP problems the graph is **not given directly**.

The statement may give:

```text
array
string
grid
teleport rule
same-value rule
configuration
```

Your job is to discover:

```text
What is one node?
What is one legal transition?
What is the edge cost?
How large will the graph be?
```

The correct order is:

```text
STORY
  ↓
STATE / NODE
  ↓
TRANSITION
  ↓
EDGE COST
  ↓
COUNT V AND E
  ↓
CHOOSE ALGORITHM
```

Do **not** choose BFS/Dijkstra first.

---

# 2. Prerequisites

## 2.1 Node / state

A state contains enough information to describe the current situation.

Examples:

```text
array problem        → index i
grid problem         → (row,col)
fuel problem         → (node,fuel)
binary configuration → bitmask
```

---

## 2.2 Edge / transition

An edge represents one legal action.

Example:

```text
i → i+1
```

or:

```text
mask → mask with one bit flipped
```

---

## 2.3 Edge weight

The edge weight must represent exactly what the problem asks us to minimize.

Example:

```text
move to adjacent position → cost a
jump to same value        → cost b
```

---

## 2.4 SSSP

```text
SSSP = Single Source Shortest Path
```

If all edge weights are non-negative:

```text
Dijkstra
```

Typical complexity:

```text
O((V + E) log V)
```

So your modelling directly controls performance.

---

## 2.5 Explicit vs implicit graph

### Explicit

Store adjacency lists:

```cpp
graph[u].push_back({v, w});
```

### Implicit

Generate neighbors only when needed.

Example:

```text
mask
  ↓
flip bit 0
flip bit 1
...
flip bit 19
```

---

# 3. Universal Modelling Template

Before coding, write:

```text
1. State:
2. Transition:
3. Edge cost:
4. Number of states:
5. Number of edges:
6. Algorithm:
```

This catches modelling mistakes early.

---

# 4. Idea 1 — Array Positions as Nodes

From the lecture, consider an array like:

```text
index:  1  2  3  4  5  6  7  8  9 10
value:  1  2  3  3  2  5  6  1  7  2
```

Assume two movement rules.

### Rule A — Adjacent movement

```text
i ↔ i+1
```

Cost:

```text
a
```

### Rule B — Same-value jump

If:

```text
A[i] == A[j]
```

we may jump:

```text
i → j
```

with cost:

```text
b
```

Natural state:

```text
node = index
```

---

# 5. Naive Same-Value Edges

Suppose value `2` appears at:

```text
2, 5, 10
```

Naive modelling connects every pair:

```text
2 ↔ 5
2 ↔ 10
5 ↔ 10
```

For a group with `K` occurrences, pairwise connections are roughly:

```text
K²
```

---

# 6. Why the Naive Model Is Too Large

Worst case:

```text
all N values are equal
```

Then all indices connect to all other indices.

Number of pairs:

```text
N(N-1) / 2
```

Therefore:

```text
E = O(N²)
```

Dijkstra becomes approximately:

```text
O(N² log N)
```

For large `N`, this is too slow and too memory-heavy.

The problem is **not Dijkstra**.

The problem is the graph model.

---

# 7. Supernode Compression

Create one extra helper node for each distinct array value.

For value `2`:

```text
             H(2)
           /  |  \
          /   |   \
         2    5   10
```

Instead of connecting every occurrence to every other occurrence, every index connects only to its helper node.

---

## Cost design

We want:

```text
same-value jump cost = b
```

A clean directed construction is:

```text
i → H(value)     cost 0
H(value) → i     cost b
```

Then:

```text
i → H(value) → j
```

costs:

```text
0 + b = b
```

So the helper node preserves the original same-value jump cost.

> Depending on the exact original problem, the `0` and `b` directions may be reversed. The invariant is: **travelling through the helper must equal the intended original jump cost**.

---

# 8. Why the Cost Is Preserved

Suppose:

```text
A[2] = A[5] = A[10] = 2
```

Original relation:

```text
2 → 10
cost = b
```

Compressed graph:

```text
2 --0--> H(2) --b--> 10
```

Total:

```text
0 + b = b
```

Exactly the same.

Likewise:

```text
5 --0--> H(2) --b--> 2
```

also costs `b`.

So shortest-path meaning is preserved.

---

# 9. Small Dry Run

Take:

```text
A = [1, 2, 1, 3, 2]
```

Let:

```text
adjacent cost a = 4
same-value cost b = 1
```

Position nodes:

```text
P1  P2  P3  P4  P5
 1   2   1   3   2
```

Adjacent edges:

```text
P1 ↔ P2 ↔ P3 ↔ P4 ↔ P5
```

Each direction costs `4`.

Create helper nodes:

```text
H1 for value 1
H2 for value 2
H3 for value 3
```

Value `1` connections:

```text
P1 --0--> H1
P3 --0--> H1

H1 --1--> P1
H1 --1--> P3
```

Compare routes from `P1` to `P3`.

Adjacent route:

```text
P1 → P2 → P3
cost = 4 + 4 = 8
```

Helper route:

```text
P1 → H1 → P3
cost = 0 + 1 = 1
```

Dijkstra naturally chooses `1`.

---

# 10. Node and Edge Count

Let:

```text
N = number of array positions
D = number of distinct values
```

## Nodes

```text
N position nodes
D helper nodes
```

Therefore:

```text
V = N + D
```

Since:

```text
D <= N
```

we have:

```text
V = O(N)
```

---

## Helper edges

Each index contributes:

```text
index → helper
helper → index
```

So:

```text
2N
```

directed edges.

---

## Adjacent edges

There are:

```text
N-1
```

adjacent pairs.

If movement is bidirectional:

```text
2(N-1)
```

directed edges.

---

## Total

```text
E = 2N + 2(N-1)

  = 2N + 2N - 2

  = 4N - 2
```

Therefore:

```text
E = O(N)
```

This is the important compression:

```text
O(N²) possible pairwise relation
              ↓
        O(N) graph edges
```

---

# 11. Complexity

## Naive

```text
V = O(N)
E = O(N²)
```

Dijkstra:

```text
O(N² log N)
```

## Supernode model

```text
V = N + D = O(N)
E = 4N - 2 = O(N)
```

Dijkstra:

```text
O((V+E) log V)
= O(N log N)
```

This is why modelling can matter more than the shortest-path implementation itself.

---

# 12. C++ — Supernode Graph + Dijkstra

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;
using Edge = pair<int, ll>;

vector<ll> dijkstra(int source, const vector<vector<Edge>>& graph) {
    const ll INF = (1LL << 60);

    vector<ll> dist(graph.size(), INF);
    priority_queue<pair<ll, int>, vector<pair<ll, int>>, greater<pair<ll, int>>> pq;

    dist[source] = 0;
    pq.push({0, source});

    while (!pq.empty()) {
        auto [currentDist, node] = pq.top();
        pq.pop();

        if (currentDist != dist[node]) {
            continue;
        }

        for (auto [nextNode, weight] : graph[node]) {
            ll nextDist = currentDist + weight;

            if (nextDist < dist[nextNode]) {
                dist[nextNode] = nextDist;
                pq.push({nextDist, nextNode});
            }
        }
    }

    return dist;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    ll adjacentCost, sameValueCost;
    cin >> n >> adjacentCost >> sameValueCost;

    vector<int> a(n);

    for (int& value : a) {
        cin >> value;
    }

    map<int, int> helperId;

    for (int value : a) {
        if (!helperId.count(value)) {
            helperId[value] = n + helperId.size();
        }
    }

    int totalNodes = n + helperId.size();
    vector<vector<Edge>> graph(totalNodes);

    for (int i = 0; i + 1 < n; i++) {
        graph[i].push_back({i + 1, adjacentCost});
        graph[i + 1].push_back({i, adjacentCost});
    }

    for (int i = 0; i < n; i++) {
        int helper = helperId[a[i]];

        graph[i].push_back({helper, 0});
        graph[helper].push_back({i, sameValueCost});
    }

    vector<ll> dist = dijkstra(0, graph);

    cout << dist[n - 1] << '\n';

    return 0;
}
```

Complexity:

```text
Graph construction:
O(N log D) with map

Dijkstra:
O(N log N)
```

---

# 13. When to Recognize Supernodes

Look for statements such as:

```text
jump between any two positions with the same value
all cells with the same color are connected
all stations on the same line can transfer
all cities of the same company are mutually reachable
all nodes of one category share a transition
```

Naive thought:

```text
connect every pair
```

Better thought:

```text
Can one helper node represent the common property?
```

Recognition:

```text
many-to-many relation
        ↓
shared property
        ↓
supernode
        ↓
compressed graph
```

---

# 14. Idea 2 — Binary String as a Graph State

The second lecture idea uses a fixed-length binary string as the entire graph state.

Example:

```text
start = 01011010010...
end   = 11010011110...
```

For a 20-bit string:

```text
one whole binary string = one graph node
```

A legal operation changes one bit.

That operation becomes one edge.

---

# 15. Prerequisite — Bitmask

Binary strings can be stored as integers.

Example:

```text
101₂
```

means:

```text
1×2² + 0×2¹ + 1×2⁰
= 4 + 0 + 1
= 5
```

So:

```text
"101" ↔ mask 5
```

For `B` bits:

```text
number of possible masks = 2^B
```

For:

```text
B = 20
```

we get:

```text
2^20 = 1,048,576
```

roughly one million possible states.

---

# 16. Convert String to Mask

```cpp
int getMask(const string& s) {
    int mask = 0;

    for (int bit = 0; bit < (int)s.size(); bit++) {
        if (s[bit] == '1') {
            mask |= (1 << bit);
        }
    }

    return mask;
}
```

Example:

```text
s = "101"
```

Set bits:

```text
bit 0
bit 2
```

So:

```text
mask = 1 + 4 = 5
```

---

# 17. One-Bit Flip = One Edge

To flip bit `b`:

```text
nextMask = mask ^ (1 << b)
```

Why XOR?

```text
0 ^ 1 = 1
1 ^ 1 = 0
```

So XOR toggles the selected bit.

For 20 bits:

```text
b = 0..19
```

Therefore each state has at most:

```text
20 neighbors
```

---

# 18. Dry Run — `101`

Current state:

```text
101
```

Flip bit 0:

```text
101 → 100
```

Flip bit 1:

```text
101 → 111
```

Flip bit 2:

```text
101 → 001
```

Graph view:

```text
       101
      / | \
     /  |  \
   100 111 001
```

One operation = one edge.

---

# 19. How Many States and Edges?

For `B` bits:

```text
V = 2^B
```

Each state has at most `B` one-bit flips.

So neighbor checks are:

```text
B × 2^B
```

For `B = 20`:

```text
V = 2^20 ≈ 10^6
```

and:

```text
20 × 2^20 ≈ 2 × 10^7
```

possible directed neighbor checks.

This is why the lecture emphasizes:

```text
20 edges per node
```

---

# 20. Banned States

The lecture code also contains the idea:

```text
ban[mask]
```

Some configurations are forbidden.

Then simply do not enter them.

Conceptually:

```text
all bitmasks
    ↓
remove forbidden states
    ↓
run BFS on remaining graph
```

This is another useful modelling pattern:

```text
invalid configuration
        ↓
blocked graph node
```

---

# 21. Explicit vs Implicit Bitmask Graph

A direct construction is:

```text
for every mask
    for every bit
        connect mask with mask ^ (1<<bit)
```

Conceptually correct.

But storing all those edges may waste memory.

Because neighbors are easy to calculate, generate them on demand:

```cpp
for (int bit = 0; bit < 20; bit++) {
    int nextMask = mask ^ (1 << bit);
}
```

This is an **implicit graph**.

Same graph.
Less memory.
Cleaner code.

---

# 22. C++ — BFS on Bitmask States

If every bit flip costs `1`, use BFS.

```cpp
#include <bits/stdc++.h>
using namespace std;

int getMask(const string& s) {
    int mask = 0;

    for (int bit = 0; bit < (int)s.size(); bit++) {
        if (s[bit] == '1') {
            mask |= (1 << bit);
        }
    }

    return mask;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    const int BITS = 20;
    const int STATES = (1 << BITS);

    string startString, endString;
    cin >> startString >> endString;

    int startMask = getMask(startString);
    int endMask = getMask(endString);

    int bannedCount;
    cin >> bannedCount;

    vector<bool> banned(STATES, false);

    while (bannedCount--) {
        string s;
        cin >> s;
        banned[getMask(s)] = true;
    }

    if (banned[startMask] || banned[endMask]) {
        cout << -1 << '\n';
        return 0;
    }

    vector<int> dist(STATES, -1);
    queue<int> q;

    dist[startMask] = 0;
    q.push(startMask);

    while (!q.empty()) {
        int mask = q.front();
        q.pop();

        if (mask == endMask) {
            break;
        }

        for (int bit = 0; bit < BITS; bit++) {
            int nextMask = mask ^ (1 << bit);

            if (banned[nextMask] || dist[nextMask] != -1) {
                continue;
            }

            dist[nextMask] = dist[mask] + 1;
            q.push(nextMask);
        }
    }

    cout << dist[endMask] << '\n';

    return 0;
}
```

Complexity:

```text
Time  = O(B × 2^B)
Space = O(2^B)
```

For `B = 20`:

```text
Time  ≈ O(20 × 2^20)
Space ≈ O(2^20)
```

---

# 23. General Modelling Checklist

For a new problem, follow this exact order.

## Step 1 — Define the node

Ask:

```text
What completely describes the current situation?
```

Examples:

```text
index
(row,col)
(node,fuel)
bitmask
```

## Step 2 — Define one legal move

```text
What can I do in one operation?
```

That becomes an edge.

## Step 3 — Define edge cost

```text
How much does this move increase the objective?
```

## Step 4 — Look for dense shared relations

Words to notice:

```text
same value
same color
same type
same company
same category
same group
```

If pairwise edges give `O(N²)`:

```text
try a helper node
```

## Step 5 — Look for compact state encoding

If the state contains a small fixed number of binary choices:

```text
think bitmask
```

## Step 6 — Count V and E

Do this before coding.

## Step 7 — Choose algorithm

```text
all edges cost 1  → BFS
0/1 edges         → 0-1 BFS
non-negative      → Dijkstra
negative possible → Bellman-Ford
```

---

# 24. Common Mistakes

## Mistake 1 — Pairwise same-value edges

If one value appears many times, a clique creates quadratic work.

Use:

```text
supernode
```

instead.

---

## Mistake 2 — Wrong supernode cost

Always verify:

```text
cost through helper
=
original intended jump cost
```

with a tiny example.

---

## Mistake 3 — Forgetting helper nodes in V

With `D` distinct values:

```text
V = N + D
```

not just `N`.

---

## Mistake 4 — Building a huge explicit bitmask graph

For `2^20` states, storing every neighbor is usually unnecessary.

Generate:

```text
mask ^ (1<<bit)
```

on demand.

---

## Mistake 5 — Using strings when a bitmask is simpler

Repeated string copying is heavier than integer bit operations.

If the state is naturally binary:

```text
encode it as a mask
```

---

## Mistake 6 — Choosing algorithm before modelling

Correct order:

```text
model
→ count V,E
→ inspect weights
→ choose algorithm
```

---

# 25. Final Recognition Model

## Pattern A — Shared-property compression

```text
many nodes
all connected because of one common property
            ↓
        helper node
            ↓
O(N²) relation can become O(N) edges
```

Example:

```text
positions with the same value
```

---

## Pattern B — Configuration as a node

```text
binary configuration
        ↓
      bitmask
        ↓
   integer node
```

One legal modification becomes one edge.

Example:

```text
101
├── 100
├── 111
└── 001
```

---

## Contest mental model

```text
STORY
  |
  v
What is the STATE?
  |
  v
What is one TRANSITION?
  |
  v
What COST does it add?
  |
  v
Will edges explode?
  |             \
 yes              no
  |                |
SUPER-NODE      normal graph
  |
  v
Can the state be encoded compactly?
  |
bitmask / tuple / expanded state
  |
  v
COUNT V AND E
  |
  v
BFS / 0-1 BFS / DIJKSTRA
```

The main lesson is:

```text
Do not only learn graph algorithms.
Learn how to BUILD the right graph.
```
