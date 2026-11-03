# Graph Problem Solving — TLE-Style Self-Study Notes

> Practice set: TLE Graphs Beginner — Problem Solving
>
> Problems:
> 1. Codeforces 1093D — Beautiful Graph
> 2. Codeforces 1037D — Valid BFS?
> 3. Codeforces 1833E — Round Dance
> 4. CSES 1667 — Message Route
>
> **Goal:** Learn `story → graph model → observation → traversal → answer`.

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Problem-Solving Pipeline](#2-problem-solving-pipeline)
3. [Problem 1 — Beautiful Graph](#3-problem-1--beautiful-graph)
4. [Problem 2 — Valid BFS?](#4-problem-2--valid-bfs)
5. [Problem 3 — Round Dance](#5-problem-3--round-dance)
6. [Problem 4 — Message Route](#6-problem-4--message-route)
7. [Pattern Comparison](#7-pattern-comparison)
8. [Recognition Checklist](#8-recognition-checklist)
9. [Final Mental Model](#9-final-mental-model)

---

# 1. Prerequisites

## 1.1 Graph = Nodes + Edges

```text
          1
         / \
        /   \
       2-----3
       |
       4
```

Nodes are objects. Edges are connections.

For an undirected edge:

```text
u ----- v
```

store both directions:

```cpp
adj[u].push_back(v);
adj[v].push_back(u);
```

---

## 1.2 Adjacency List

```text
1 → 2, 3
2 → 1, 3, 4
3 → 1, 2
4 → 2
```

C++:

```cpp
vector<vector<int>> adj(n + 1);
```

---

## 1.3 DFS

DFS goes deep into one branch before returning.

```text
             1
           /   \
          2     3
         / \
        4   5
```

Possible DFS:

```text
1 → 2 → 4 → 5 → 3
```

```cpp
void dfs(int node, const vector<vector<int>>& adj, vector<int>& visited) {
    visited[node] = 1;

    for (int neighbour : adj[node]) {
        if (!visited[neighbour]) {
            dfs(neighbour, adj, visited);
        }
    }
}
```

---

## 1.4 BFS

BFS visits level by level.

```text
               1
            /  |  \
           2   3   4
          / \
         5   6
```

```text
Level 0: 1
Level 1: 2 3 4
Level 2: 5 6
```

Queue:

```text
Pop 1 → push 2,3,4 → [2,3,4]
Pop 2 → push 5,6   → [3,4,5,6]
Pop 3              → [4,5,6]
...
```

---

## 1.5 Connected Components

```text
Component A          Component B          Component C

  1 ----- 2             5 ----- 6              8
  |                       |
  3                       7
```

Pattern:

```text
for every node:
    if unvisited:
        new component
        DFS/BFS through it
```

---

## 1.6 Bipartite Graph

Two colors:

```text
0 = BLUE
1 = RED
```

Every edge must connect different colors.

```text
BLUE 1 -------- 2 RED
  |               |
  |               |
RED  4 -------- 3 BLUE
```

Odd cycle fails:

```text
          1 BLUE
         /      \
      RED 2 ---- 3 RED
```

Edge `2-3` joins the same color.

Therefore:

```text
bipartite ⇔ no odd-length cycle
```

for an undirected graph.

---

## 1.7 Parity

```text
odd + even = odd
even + odd = odd

odd + odd = even
even + even = even
```

For values `1,2,3`:

```text
1 → odd
2 → even
3 → odd
```

So:

```text
odd value  → 2 choices {1,3}
even value → 1 choice  {2}
```

---

## 1.8 Product Rule

Independent components multiply.

```text
component A = X ways
component B = Y ways

total = X * Y
```

---

## 1.9 Powers of Two

If `k` nodes each have 2 choices:

```text
2^k
```

Fast power:

```cpp
long long modPow(long long base, long long exponent, long long mod) {
    long long result = 1;

    while (exponent > 0) {
        if (exponent & 1LL) {
            result = result * base % mod;
        }

        base = base * base % mod;
        exponent >>= 1;
    }

    return result;
}
```

---

## 1.10 Tree

A tree is:

```text
connected + no cycle
```

For `N` nodes:

```text
edges = N - 1
```

There is exactly one simple path between every pair of nodes.

---

## 1.11 Parent Array

BFS path:

```text
1 → 4 → 5 → 8
```

Store:

```text
parent[4] = 1
parent[5] = 4
parent[8] = 5
```

Reconstruct backward:

```text
8 ← 5 ← 4 ← 1
```

then reverse:

```text
1 → 4 → 5 → 8
```

---

## 1.12 Position / Rank Array

Candidate order:

```text
[1, 3, 2, 5, 4]
```

Create:

```text
position[1] = 0
position[3] = 1
position[2] = 2
position[5] = 3
position[4] = 4
```

This lets us sort neighbours according to the required traversal.

---

# 2. Problem-Solving Pipeline

```text
STORY
  ↓
What are the NODES?
  ↓
What are the EDGES?
  ↓
What property is required?
  ↓
+-------------------------------+
|               |               |
v               v               v
2-coloring    shortest path    components
|               |               |
Bipartite       BFS            DFS/BFS
```

Then ask:

```text
Do I need to count ways?
Do components behave independently?
Does traversal order matter?
Do I need the actual path?
Does each component have a special shape?
```

---

# 3. Problem 1 — Beautiful Graph

**Codeforces 1093D**

```text
https://codeforces.com/contest/1093/problem/D
```

## 3.1 What Is Asked?

Assign one value to every node:

```text
1, 2, or 3
```

For every edge:

```text
value[u] + value[v]
```

must be odd.

Count all valid assignments modulo:

```text
998244353
```

---

## 3.2 Story → Mathematical Model

Values by parity:

```text
1 → ODD
2 → EVEN
3 → ODD
```

Odd sum requires:

```text
ODD + EVEN
```

Therefore every edge must connect opposite parity.

That means:

```text
the graph must be bipartite
```

---

## 3.3 Better Graph Drawing

Original component:

```text
                 [1]
               /     \
              /       \
            [2]       [3]
             |         |
             |         |
            [4]-------[5]
```

Two-color it:

```text
                 [1 O]
               /       \
              /         \
          [2 E]         [3 E]
             |             |
             |             |
          [4 O] -------- [5 O]
```

Legend:

```text
O → must receive 1 or 3 → 2 choices
E → must receive 2      → 1 choice
```

---

## 3.4 Count One Component

Suppose:

```text
color-0 nodes = c0
color-1 nodes = c1
```

Two orientations exist.

Orientation A:

```text
color 0 → ODD
color 1 → EVEN

ways = 2^c0
```

Orientation B:

```text
color 0 → EVEN
color 1 → ODD

ways = 2^c1
```

So:

```text
componentWays = 2^c0 + 2^c1
```

---

## 3.5 Tiny Dry Run

```text
1 ----- 2
```

Color:

```text
1(0) ----- 2(1)
```

Counts:

```text
c0=1
c1=1
```

Ways:

```text
2^1 + 2^1 = 4
```

Assignments:

```text
1,2
3,2
2,1
2,3
```

---

## 3.6 Isolated Node

```text
[1]
```

Counts:

```text
c0=1
c1=0
```

Formula:

```text
2^1 + 2^0 = 3
```

Correct choices:

```text
1,2,3
```

---

## 3.7 Multiple Components

```text
Component A            Component B

1 ----- 2             4 ----- 5
                          \
                           6
```

Independent components multiply:

```text
total = ways(A) * ways(B)
```

---

## 3.8 Why Odd Cycle Gives Answer 0

```text
          1 O
         /   \
        /     \
      2 E --- 3 E
```

Edge `2-3` joins same parity side.

Impossible.

Therefore:

```text
if any component is not bipartite:
    answer = 0
```

---

## 3.9 Algorithm

```text
answer = 1

for every uncolored node:
    start a new component
    color start = 0
    count c0 and c1

    DFS/BFS:
        uncolored neighbour → opposite color
        same-color neighbour → impossible

    componentWays = 2^c0 + 2^c1
    answer *= componentWays
```

---

## 3.10 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll MOD = 998244353;

ll modPow(ll base, ll exponent) {
    ll result = 1;

    while (exponent > 0) {
        if (exponent & 1LL) {
            result = result * base % MOD;
        }

        base = base * base % MOD;
        exponent >>= 1;
    }

    return result;
}

bool dfs(int node, int currentColor,
         const vector<vector<int>>& adj,
         vector<int>& color,
         ll countColor[2]) {
    color[node] = currentColor;
    countColor[currentColor]++;

    for (int neighbour : adj[node]) {
        if (color[neighbour] == -1) {
            if (!dfs(neighbour, 1 - currentColor, adj, color, countColor)) {
                return false;
            }
        } else if (color[neighbour] == color[node]) {
            return false;
        }
    }

    return true;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        int n, m;
        cin >> n >> m;

        vector<vector<int>> adj(n + 1);

        for (int i = 0; i < m; i++) {
            int u, v;
            cin >> u >> v;

            adj[u].push_back(v);
            adj[v].push_back(u);
        }

        vector<int> color(n + 1, -1);

        ll answer = 1;
        bool possible = true;

        for (int node = 1; node <= n; node++) {
            if (color[node] != -1) {
                continue;
            }

            ll countColor[2] = {0, 0};

            if (!dfs(node, 0, adj, color, countColor)) {
                possible = false;
                break;
            }

            ll componentWays =
                (modPow(2, countColor[0]) + modPow(2, countColor[1])) % MOD;

            answer = answer * componentWays % MOD;
        }

        cout << (possible ? answer : 0) << '\n';
    }

    return 0;
}
```

Complexity:

```text
O(N + M)
```

Main recognition:

```text
opposite property on every edge
→ bipartite
→ count both color classes
```

---

# 4. Problem 2 — Valid BFS?

**Codeforces 1037D**

```text
https://codeforces.com/contest/1037/problem/D
```

## 4.1 What Is Asked?

Given:

```text
a tree
+
a candidate sequence
```

determine whether that sequence can be produced by a BFS starting from node `1`.

Important:

```text
BFS may process neighbours in arbitrary order.
```

So one tree can have many valid BFS orders.

---

## 4.2 Queue Restriction

Tree:

```text
               1
             /   \
            2     3
           /
          4
```

Valid:

```text
1,2,3,4
```

Also valid:

```text
1,3,2,4
```

But:

```text
1,2,4,3
```

is invalid.

Why?

After processing `1`:

```text
queue = [2,3]
```

After `2` discovers `4`:

```text
queue = [3,4]
```

So `3` must come before `4`.

---

## 4.3 Candidate Order → Rank

Candidate:

```text
1 3 2 7 6 5 4
```

Create:

```text
position[1]=0
position[3]=1
position[2]=2
position[7]=3
position[6]=4
position[5]=5
position[4]=6
```

Smaller position means earlier in the desired BFS.

---

## 4.4 Better Graph Drawing

```text
                    1
                 /     \
                2       3
              /  \     / \
             4    5   6   7
```

Candidate:

```text
1 3 2 7 6 5 4
```

Desired child order becomes:

```text
1 → [3,2]
3 → [7,6]
2 → [5,4]
```

BFS:

```text
queue=[1]

pop 1 → push 3,2
queue=[3,2]

pop 3 → push 7,6
queue=[2,7,6]

pop 2 → push 5,4
queue=[7,6,5,4]
```

Generated order:

```text
1 3 2 7 6 5 4
```

Matches.

---

## 4.5 Main Trick

Sort every adjacency list by:

```text
position[neighbour]
```

Then run ordinary BFS.

Why does this work?

Because if the candidate is valid, its order tells us how BFS should resolve every legal neighbour-order choice.

If this best-matching simulation still differs:

```text
candidate is impossible
```

---

## 4.6 Algorithm

```text
1. Read tree.
2. Read candidate order.
3. If first node != 1 → No.
4. Build position[].
5. Sort each adjacency list by position[].
6. Run BFS from 1.
7. Compare BFS output with candidate.
8. Same → Yes.
9. Different → No.
```

---

## 4.7 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<vector<int>> adj(n + 1);

    for (int i = 0; i < n - 1; i++) {
        int u, v;
        cin >> u >> v;

        adj[u].push_back(v);
        adj[v].push_back(u);
    }

    vector<int> order(n);
    vector<int> position(n + 1);

    for (int i = 0; i < n; i++) {
        cin >> order[i];
        position[order[i]] = i;
    }

    if (order[0] != 1) {
        cout << "No\n";
        return 0;
    }

    for (int node = 1; node <= n; node++) {
        sort(adj[node].begin(), adj[node].end(), [&](int a, int b) {
            return position[a] < position[b];
        });
    }

    vector<int> visited(n + 1, 0);
    vector<int> bfsOrder;

    queue<int> q;
    q.push(1);
    visited[1] = 1;

    while (!q.empty()) {
        int node = q.front();
        q.pop();

        bfsOrder.push_back(node);

        for (int neighbour : adj[node]) {
            if (!visited[neighbour]) {
                visited[neighbour] = 1;
                q.push(neighbour);
            }
        }
    }

    cout << (bfsOrder == order ? "Yes\n" : "No\n");

    return 0;
}
```

Complexity:

```text
O(N log N)
```

Main recognition:

```text
candidate traversal
→ convert sequence to rank
→ reorder neighbours
→ simulate
```

---

# 5. Problem 3 — Round Dance

**Codeforces 1833E**

```text
https://codeforces.com/contest/1833/problem/E
```

## 5.1 What Is Asked?

There are `N` people.

In a true round dance, every person has exactly two neighbours.

But each person remembers only one neighbour:

```text
a[i]
```

Find:

```text
minimum possible dances
maximum possible dances
```

---

## 5.2 Story → Graph

Each person:

```text
node
```

Remembered neighbour:

```text
undirected edge
```

If:

```text
a[i] = j
```

add:

```text
i ----- j
```

---

## 5.3 Duplicate Edge

If:

```text
a[1]=2
a[2]=1
```

we still have only one undirected relation:

```text
1 ----- 2
```

A convenient implementation:

```cpp
vector<set<int>> adj(n + 1);
```

---

## 5.4 Two Useful Component Shapes

### Closed Cycle

```text
        1
       / \
      2---3
```

Every node degree:

```text
2
```

This component is already a closed dance.

It cannot be merged with another component.

---

### Open Path

```text
1 ----- 2 ----- 3 ----- 4
```

Endpoints have degree:

```text
1
```

They still have free neighbour slots.

So this component can be merged with another open component.

---

## 5.5 Better Visual Model

Suppose:

```text
Component A — cycle

        1
       / \
      2---3


Component B — path

4 ----- 5 ----- 6


Component C — path

7 ----- 8
```

Component A is locked.

B and C can be joined into one larger round dance.

So:

```text
cycles are mandatory separate dances
paths can all merge into one dance
```

---

## 5.6 Maximum Number of Dances

To maximize:

```text
keep every connected component separate
```

If:

```text
cycles = C
paths = P
```

then:

```text
maximum = C + P
```

---

## 5.7 Minimum Number of Dances

Every closed cycle stays separate.

All path components can merge into one dance if at least one exists.

Therefore:

```text
minimum = C + min(P,1)
```

Equivalent:

```text
minimum = C + (P > 0 ? 1 : 0)
```

---

## 5.8 Dry Run

Graph:

```text
1 ----- 2

3 ----- 4

5 ----- 6
```

All three components are open paths.

```text
C=0
P=3
```

Maximum:

```text
0+3=3
```

Minimum:

```text
0+1=1
```

Answer:

```text
1 3
```

---

## 5.9 Cycle Detection by Degree

Traverse one component.

Start:

```text
isCycle = true
```

For every node:

```text
if degree != 2:
    isCycle = false
```

At end:

```text
all degree 2 → closed cycle
otherwise    → open component
```

---

## 5.10 Algorithm

```text
1. Build unique undirected graph.
2. visited[] = false.
3. cycles=0, paths=0.
4. For each unvisited node:
       DFS/BFS component.
       Check whether every node has degree 2.
       yes → cycles++
       no  → paths++
5. max = cycles + paths.
6. min = cycles + (paths>0 ? 1 : 0).
```

---

## 5.11 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

void dfs(int node,
         const vector<set<int>>& adj,
         vector<int>& visited,
         bool& isCycle) {
    visited[node] = 1;

    if (adj[node].size() != 2) {
        isCycle = false;
    }

    for (int neighbour : adj[node]) {
        if (!visited[neighbour]) {
            dfs(neighbour, adj, visited, isCycle);
        }
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        int n;
        cin >> n;

        vector<set<int>> adj(n + 1);

        for (int i = 1; i <= n; i++) {
            int neighbour;
            cin >> neighbour;

            adj[i].insert(neighbour);
            adj[neighbour].insert(i);
        }

        vector<int> visited(n + 1, 0);

        int cycles = 0;
        int paths = 0;

        for (int node = 1; node <= n; node++) {
            if (visited[node]) {
                continue;
            }

            bool isCycle = true;

            dfs(node, adj, visited, isCycle);

            if (isCycle) {
                cycles++;
            } else {
                paths++;
            }
        }

        int minimum = cycles + (paths > 0 ? 1 : 0);
        int maximum = cycles + paths;

        cout << minimum << ' ' << maximum << '\n';
    }

    return 0;
}
```

Main recognition:

```text
min/max groups
→ connected components
→ which components are locked?
→ which components can merge?
```

---

# 6. Problem 4 — Message Route

**CSES 1667**

```text
https://cses.fi/problemset/task/1667
```

## 6.1 What Is Asked?

We have an undirected unweighted network.

Need a route:

```text
1 → ... → N
```

with the minimum number of computers.

If impossible:

```text
IMPOSSIBLE
```

We must also print an actual shortest route.

---

## 6.2 Recognition

Keywords:

```text
unweighted
minimum route
```

immediately suggest:

```text
BFS
```

Need actual route:

```text
BFS + parent[]
```

---

## 6.3 Better Graph Drawing

```text
                   2
                  / \
                 /   \
                1-----3
                |     |
                |     |
                4-----5
```

Source:

```text
1
```

Destination:

```text
5
```

Possible routes:

```text
1 → 3 → 5
1 → 4 → 5
1 → 2 → 3 → 5
```

Shortest number of edges:

```text
2
```

Shortest number of computers:

```text
3
```

---

## 6.4 BFS Levels

```text
Level 0:
    1

Level 1:
    2,3,4

Level 2:
    5
```

The first time BFS reaches node `5`, its distance is minimum.

---

## 6.5 Parent Recording

Suppose:

```text
1 discovers 3
3 discovers 5
```

Store:

```text
parent[3]=1
parent[5]=3
```

Backward chain:

```text
5
↑
3
↑
1
```

Reverse:

```text
1 → 3 → 5
```

---

## 6.6 Algorithm

```text
1. Build adjacency list.
2. dist[] = -1.
3. parent[] = -1.
4. dist[1]=0.
5. Push 1.

6. BFS:
       pop node

       for each neighbour:
           if dist[neighbour] == -1:
               dist[neighbour] = dist[node]+1
               parent[neighbour] = node
               push neighbour

7. If dist[N] == -1:
       IMPOSSIBLE

8. Otherwise:
       follow parent[] from N to 1
       reverse path
       print it
```

---

## 6.7 Detailed Dry Run

Graph:

```text
1 ----- 2 ----- 5
|       |
|       |
3 ----- 4
```

Start:

```text
queue=[1]
dist[1]=0
```

Iteration 1:

```text
pop 1

discover 2:
dist[2]=1
parent[2]=1

discover 3:
dist[3]=1
parent[3]=1

queue=[2,3]
```

Iteration 2:

```text
pop 2

discover 5:
dist[5]=2
parent[5]=2

discover 4:
dist[4]=2
parent[4]=2

queue=[3,5,4]
```

Destination:

```text
5 reached at distance 2
```

Parent chain:

```text
5 ← 2 ← 1
```

Reverse:

```text
1 → 2 → 5
```

Number of computers:

```text
3
```

---

## 6.8 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<vector<int>> adj(n + 1);

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;

        adj[u].push_back(v);
        adj[v].push_back(u);
    }

    vector<int> dist(n + 1, -1);
    vector<int> parent(n + 1, -1);

    queue<int> q;

    dist[1] = 0;
    q.push(1);

    while (!q.empty()) {
        int node = q.front();
        q.pop();

        for (int neighbour : adj[node]) {
            if (dist[neighbour] != -1) {
                continue;
            }

            dist[neighbour] = dist[node] + 1;
            parent[neighbour] = node;
            q.push(neighbour);
        }
    }

    if (dist[n] == -1) {
        cout << "IMPOSSIBLE\n";
        return 0;
    }

    vector<int> path;

    for (int node = n; node != -1; node = parent[node]) {
        path.push_back(node);
    }

    reverse(path.begin(), path.end());

    cout << path.size() << '\n';

    for (int node : path) {
        cout << node << ' ';
    }

    cout << '\n';

    return 0;
}
```

---

## 6.9 Why `path.size()` Is Correct

```text
1 → 4 → 7 → 10
```

Edges:

```text
3
```

Nodes/computers:

```text
4
```

Therefore:

```text
number of nodes = number of edges + 1
```

CSES asks for number of computers, so print:

```text
path.size()
```

---

## 6.10 Complexity

```text
BFS = O(N + M)
Path reconstruction = O(N) worst case
```

Overall:

```text
O(N + M)
```

Main recognition:

```text
minimum path in unweighted graph
→ BFS

need actual route
→ parent[]
```

---

# 7. Pattern Comparison

| Problem | Hidden Pattern | Main Tool | Extra State |
|---|---|---|---|
| Beautiful Graph | opposite parity on every edge | Bipartite DFS/BFS | `color[]`, side counts |
| Valid BFS? | verify traversal ordering | Rank + BFS | `position[]` |
| Round Dance | classify connected components | DFS/BFS | degree, cycle/path counts |
| Message Route | shortest unweighted path | BFS | `dist[]`, `parent[]` |

The important lesson:

```text
Traversal is often easy.
Choosing the right extra state is the real problem.
```

```text
Beautiful Graph
DFS + color + count

Valid BFS?
BFS + desired rank

Round Dance
DFS + component shape

Message Route
BFS + parent + distance
```

---

# 8. Recognition Checklist

## 8.1 Adjacent nodes must have opposite properties?

Examples:

```text
different teams
opposite parity
different colors
```

Think:

```text
BIPARTITE
```

---

## 8.2 Two bipartite sides have different numbers of choices?

Think:

```text
count side 0
count side 1
derive formula
```

Beautiful Graph:

```text
2^c0 + 2^c1
```

---

## 8.3 Components are independent?

Solve each component separately.

For counting:

```text
multiply component answers
```

---

## 8.4 Candidate BFS/DFS order?

Ask:

```text
What ordering freedom does traversal have?
Can target sequence become a rank?
```

Then try:

```text
sort neighbours by target rank
simulate
```

---

## 8.5 Component has a special shape?

Check node degrees.

```text
all degree 2 → cycle
endpoints degree 1 → path
connected + N-1 edges → tree
```

---

## 8.6 Minimum path in an unweighted graph?

Think:

```text
BFS
```

Need actual path?

```text
BFS + parent[]
```

---

# 9. Final Mental Model

## Beautiful Graph

```text
edge sum must be odd
        ↓
opposite parity
        ↓
bipartite
        ↓
count color classes
        ↓
2^c0 + 2^c1
```

## Valid BFS?

```text
candidate traversal
        ↓
convert sequence to rank
        ↓
sort adjacency by rank
        ↓
run BFS
        ↓
compare
```

## Round Dance

```text
remembered neighbour graph
        ↓
connected components
        ↓
cycle or open path?
        ↓
cycle = locked
path = mergeable
        ↓
minimum / maximum
```

## Message Route

```text
minimum route
        ↓
unweighted graph
        ↓
BFS
        ↓
parent[]
        ↓
reconstruct shortest path
```

The reusable CP skill is:

```text
STORY
  ↓
GRAPH MODEL
  ↓
GRAPH PROPERTY
  ↓
TRAVERSAL
  ↓
EXTRA STATE
  ↓
ANSWER
```
