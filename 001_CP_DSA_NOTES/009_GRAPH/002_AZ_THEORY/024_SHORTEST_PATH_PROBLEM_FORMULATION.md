# Shortest Path Problem Formulation — From Story to Graph

> **Self-study competitive programming note**
>
> This note focuses on the most important skill behind shortest-path problems:
>
> **How do we convert a story into a graph with the correct state and edge cost?**
>
> The lecture examples are normalized into clear CP notation while preserving the same modelling ideas:
>
> - SSSP vs APSP
> - choosing BFS / 0-1 BFS / Dijkstra / Bellman-Ford / Floyd-Warshall
> - arrow-grid shortest path
> - converting an implicit grid into a graph
> - assigning edge cost `0` or `1`
> - adding extra information to the state
> - fuel/resource state: `(node, fuel)`
> - Dijkstra on an expanded state graph

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [The Real Difficulty in Shortest Path Problems](#2-the-real-difficulty-in-shortest-path-problems)
3. [Shortest Path Problem Classification](#3-shortest-path-problem-classification)
4. [The Universal Modelling Template](#4-the-universal-modelling-template)
5. [Prerequisite: What Is a State?](#5-prerequisite-what-is-a-state)
6. [Prerequisite: What Is an Edge?](#6-prerequisite-what-is-an-edge)
7. [Prerequisite: Edge Cost](#7-prerequisite-edge-cost)
8. [Prerequisite: Relaxation](#8-prerequisite-relaxation)
9. [Example 1 — Arrow Grid](#9-example-1--arrow-grid)
10. [Step 1 — Identify the State](#10-step-1--identify-the-state)
11. [Step 2 — Generate Possible Moves](#11-step-2--generate-possible-moves)
12. [Step 3 — Convert the Rule into Edge Cost](#12-step-3--convert-the-rule-into-edge-cost)
13. [Step 4 — Why This Becomes 0-1 BFS](#13-step-4--why-this-becomes-0-1-bfs)
14. [Arrow Grid Dry Run](#14-arrow-grid-dry-run)
15. [C++ — Arrow Grid with 0-1 BFS](#15-c--arrow-grid-with-0-1-bfs)
16. [The Important Modelling Lesson](#16-the-important-modelling-lesson)
17. [When Position Alone Is Not Enough](#17-when-position-alone-is-not-enough)
18. [State Expansion](#18-state-expansion)
19. [Example 2 — Travelling with Fuel](#19-example-2--travelling-with-fuel)
20. [Why `node` Alone Is an Incorrect State](#20-why-node-alone-is-an-incorrect-state)
21. [Correct State — `(node, fuel)`](#21-correct-state--node-fuel)
22. [Fuel Transition 1 — Buy / Refill Fuel](#22-fuel-transition-1--buy--refill-fuel)
23. [Fuel Transition 2 — Travel to a Neighbor](#23-fuel-transition-2--travel-to-a-neighbor)
24. [Expanded Graph Visualization](#24-expanded-graph-visualization)
25. [Fuel Example Dry Run](#25-fuel-example-dry-run)
26. [Why Dijkstra Is the Natural Choice](#26-why-dijkstra-is-the-natural-choice)
27. [C++ — Dijkstra on `(node, fuel)`](#27-c--dijkstra-on-node-fuel)
28. [General Pattern — Add Missing Information to the State](#28-general-pattern--add-missing-information-to-the-state)
29. [Common Expanded-State Forms](#29-common-expanded-state-forms)
30. [How to Estimate Complexity Before Coding](#30-how-to-estimate-complexity-before-coding)
31. [Shortest Path Algorithm Selection Table](#31-shortest-path-algorithm-selection-table)
32. [Step-by-Step Contest Workflow](#32-step-by-step-contest-workflow)
33. [Common Modelling Mistakes](#33-common-modelling-mistakes)
34. [Final Recognition Model](#34-final-recognition-model)

---

# 1. Prerequisites

Before solving shortest-path formulation problems, know these basics.

## 1.1 Graph terminology

A graph has:

```text
vertices / nodes
edges
```

Example:

```text
1 ---- 2
|      |
|      |
3 ---- 4
```

Here:

```text
V = 4
E = 4
```

---

## 1.2 Directed vs undirected edge

Directed:

```text
A ----> B
```

You can move:

```text
A → B
```

but not automatically:

```text
B → A
```

Undirected:

```text
A ----- B
```

means both:

```text
A → B
B → A
```

---

## 1.3 Weighted graph

An edge can have a cost.

```text
A --5--> B
```

Meaning:

```text
cost(A → B) = 5
```

A path cost is the sum of its edge costs.

Example:

```text
A --2--> B --4--> C
```

Then:

```text
cost(A → B → C)
= 2 + 4
= 6
```

---

## 1.4 SSSP

```text
Single Source Shortest Path
```

Given one source `S`, find shortest distances from `S`.

```text
S → every node
```

Algorithms commonly used:

```text
BFS
0-1 BFS
Dijkstra
Bellman-Ford
```

---

## 1.5 APSP

```text
All Pairs Shortest Path
```

Find:

```text
dist[u][v]
```

for many/all pairs.

Typical algorithm:

```text
Floyd-Warshall
```

for moderate `V`.

---

# 2. The Real Difficulty in Shortest Path Problems

Many students think:

```text
"I know BFS."
"I know Dijkstra."
```

but still struggle in contests.

Why?

Because the hardest part is often **not the algorithm**.

The hard part is:

```text
STORY
  ↓
STATE
  ↓
TRANSITION
  ↓
EDGE COST
  ↓
GRAPH
  ↓
ALGORITHM
```

If the graph model is wrong, even perfect Dijkstra code gives the wrong answer.

So the first question should not be:

```text
"Which algorithm do I use?"
```

The first question should be:

```text
"What exactly is one state?"
```

---

# 3. Shortest Path Problem Classification

The lecture organizes shortest path roughly like this:

```text
Shortest Path
│
├── SSSP
│   ├── BFS
│   ├── 0-1 BFS
│   ├── Dijkstra
│   └── Bellman-Ford
│
└── APSP
    └── Floyd-Warshall
```

A practical decision tree:

```text
What are the edge costs?
        |
        +-- all equal / all 1
        |       → BFS
        |
        +-- only 0 or 1
        |       → 0-1 BFS
        |
        +-- all >= 0
        |       → Dijkstra
        |
        +-- negative edges possible
                → Bellman-Ford
```

For:

```text
all-pairs shortest paths
```

with moderate number of vertices:

```text
Floyd-Warshall
```

---

# 4. The Universal Modelling Template

For almost every shortest-path problem, write these four lines before coding:

```text
1. State:
2. Transition:
3. Edge cost:
4. Algorithm:
```

Example:

```text
State:
    current grid cell (r,c)

Transition:
    move to one of 4 neighboring cells

Edge cost:
    0 if following the current arrow
    1 if changing the arrow

Algorithm:
    0-1 BFS
```

This simple template prevents many modelling mistakes.

---

# 5. Prerequisite: What Is a State?

A **state** must contain enough information to describe your current situation.

Simple graph:

```text
state = node
```

Grid:

```text
state = (row, col)
```

Graph with remaining fuel:

```text
state = (node, fuel)
```

Grid where we may break up to `K` walls:

```text
state = (row, col, wallsBroken)
```

Very important rule:

> If two situations can have different future possibilities, they should usually be different states.

---

# 6. Prerequisite: What Is an Edge?

An edge represents one legal transition.

Example:

```text
state A
   |
   | one legal action
   v
state B
```

Grid movement:

```text
(r,c) → (r+1,c)
```

Buying one unit of fuel:

```text
(node,fuel) → (node,fuel+1)
```

Driving to another city:

```text
(u,fuel) → (v,fuel-distance)
```

The graph may never appear explicitly in the input.

You may generate edges while running the algorithm.

This is an:

```text
implicit graph
```

---

# 7. Prerequisite: Edge Cost

The edge cost must represent **exactly what the problem asks us to minimize**.

If the problem asks:

```text
minimum moves
```

then usually:

```text
each move cost = 1
```

If the problem asks:

```text
minimum number of arrow changes
```

then:

```text
follow existing arrow = 0
change arrow          = 1
```

If the problem asks:

```text
minimum money spent on fuel
```

then:

```text
buying fuel = monetary cost
travelling  = 0 additional money
```

This is the central modelling idea.

---

# 8. Prerequisite: Relaxation

Suppose we have:

```text
u --w--> v
```

Current shortest known distances:

```text
dist[u]
dist[v]
```

Going through `u` gives:

```text
candidate = dist[u] + w
```

If:

```text
candidate < dist[v]
```

update:

```text
dist[v] = candidate
```

Compactly:

```text
dist[v] = min(dist[v], dist[u] + w)
```

This is called:

```text
relaxation
```

It appears in almost every shortest-path algorithm.

---

# 9. Example 1 — Arrow Grid

Consider a grid where each cell contains a direction.

Example:

```text
→ ↓ →
↑ → ↓
↑ ← ↓
→ → E
```

Start:

```text
S
```

Destination:

```text
E
```

The objective is to reach `E` while changing the minimum number of direction signs.

At every cell:

- moving in the direction already written costs `0`
- moving in another allowed direction means changing that sign, so cost is `1`

This is not a normal unweighted BFS problem.

It is a:

```text
0/1 weighted shortest-path problem
```

---

# 10. Step 1 — Identify the State

At any moment, what information determines where we are?

Only the cell.

Therefore:

```text
state = (row, col)
```

If grid has:

```text
N rows
M columns
```

then number of states:

```text
V = N × M
```

---

# 11. Step 2 — Generate Possible Moves

Suppose four moves are allowed:

```text
UP
RIGHT
DOWN
LEFT
```

Use:

```cpp
int dr[] = {-1, 0, 1, 0};
int dc[] = {0, 1, 0, -1};
```

From:

```text
(r,c)
```

we generate:

```text
(r-1,c)
(r,c+1)
(r+1,c)
(r,c-1)
```

if inside the grid.

---

# 12. Step 3 — Convert the Rule into Edge Cost

Suppose current cell contains:

```text
RIGHT
```

Then:

```text
move RIGHT → cost 0
move UP    → cost 1
move DOWN  → cost 1
move LEFT  → cost 1
```

So each grid move becomes a weighted edge.

Example:

```text
          1
          ↑
          |
      1 ← X → 0
          |
          ↓
          1
```

This is the hidden graph inside the grid.

---

## Mathematical form

For current cell `u` and chosen next cell `v`:

```text
cost(u,v) =
    0, if move follows current sign
    1, otherwise
```

We want:

```text
minimum sum of edge costs
```

from:

```text
S → E
```

That sum equals:

```text
minimum number of sign changes
```

This is exactly the story-to-graph conversion.

---

# 13. Step 4 — Why This Becomes 0-1 BFS

Every edge cost is:

```text
0 or 1
```

Therefore:

```text
0-1 BFS
```

is the natural algorithm.

Why not ordinary BFS?

Because ordinary BFS minimizes:

```text
number of edges
```

but here we minimize:

```text
number of changed signs
```

A longer route may require fewer changes.

---

# 14. Arrow Grid Dry Run

Consider:

```text
S → ↓
↑ → E
```

Assume:

```text
S is at (0,0)
E is at (1,2)
```

Suppose current arrows are:

```text
(0,0) RIGHT
(0,1) DOWN
(1,1) RIGHT
```

One route:

```text
(0,0)
  → (0,1)
  → (1,1)
  → (1,2)
```

Costs:

```text
(0,0) RIGHT → move RIGHT
cost = 0

(0,1) DOWN → move DOWN
cost = 0

(1,1) RIGHT → move RIGHT
cost = 0
```

Total:

```text
0
```

No arrow needs to change.

---

Now suppose `(0,1)` contains `LEFT`.

Then:

```text
(0,1) → (1,1)
```

does not follow the sign.

Cost:

```text
1
```

Total route cost:

```text
0 + 1 + 0 = 1
```

Meaning:

```text
minimum one sign change
```

---

# 15. C++ — Arrow Grid with 0-1 BFS

The exact character symbols can vary by problem. The important pattern is the cost assignment.

```cpp
#include <bits/stdc++.h>
using namespace std;

using State = pair<int, int>;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<string> grid(n);

    for (string& row : grid) {
        cin >> row;
    }

    int startRow, startCol;
    int endRow, endCol;

    cin >> startRow >> startCol;
    cin >> endRow >> endCol;

    const int INF = 1e9;

    vector<vector<int>> dist(n, vector<int>(m, INF));

    int dr[] = {-1, 0, 1, 0};
    int dc[] = {0, 1, 0, -1};
    char direction[] = {'U', 'R', 'D', 'L'};

    deque<State> dq;

    dist[startRow][startCol] = 0;
    dq.push_front({startRow, startCol});

    while (!dq.empty()) {
        auto [row, col] = dq.front();
        dq.pop_front();

        for (int dir = 0; dir < 4; dir++) {
            int nextRow = row + dr[dir];
            int nextCol = col + dc[dir];

            if (nextRow < 0 || nextRow >= n || nextCol < 0 || nextCol >= m) {
                continue;
            }

            int weight = (grid[row][col] == direction[dir] ? 0 : 1);

            if (dist[nextRow][nextCol] > dist[row][col] + weight) {
                dist[nextRow][nextCol] = dist[row][col] + weight;

                if (weight == 0) {
                    dq.push_front({nextRow, nextCol});
                } else {
                    dq.push_back({nextRow, nextCol});
                }
            }
        }
    }

    cout << dist[endRow][endCol] << '\n';

    return 0;
}
```

Complexity:

```text
V = N × M
E <= 4 × N × M

Time  = O(N × M)
Space = O(N × M)
```

---

# 16. The Important Modelling Lesson

The grid itself is not the algorithm.

The transformation is:

```text
CELL
  ↓
NODE
```

and:

```text
MOVE
  ↓
EDGE
```

and:

```text
change sign?
  ↓
EDGE WEIGHT
```

So:

```text
grid story
    ↓
weighted graph
    ↓
0-1 BFS
```

This is graph modelling.

---

# 17. When Position Alone Is Not Enough

Now consider another class of problems.

Suppose we travel between cities.

Each road consumes fuel.

At some city you might arrive with:

```text
0 fuel
```

or:

```text
5 units of fuel
```

Are those situations equivalent?

No.

From the same city:

```text
(city,0 fuel)
```

and:

```text
(city,5 fuel)
```

have different future possibilities.

Therefore:

```text
state = city
```

is incomplete.

---

# 18. State Expansion

We solve this by adding the missing information.

Instead of:

```text
node
```

use:

```text
(node, extraInformation)
```

For fuel:

```text
(node, fuel)
```

This creates an expanded graph.

Original node:

```text
City 2
```

becomes many states:

```text
(2,0)
(2,1)
(2,2)
...
(2,K)
```

where:

```text
K = tank capacity
```

---

# 19. Example 2 — Travelling with Fuel

Suppose we have cities connected by roads.

A road:

```text
u ----d---- v
```

requires:

```text
d units of fuel
```

Suppose each city `u` has fuel price:

```text
price[u]
```

Tank capacity:

```text
K
```

Goal:

```text
reach destination with minimum money spent
```

This is a shortest-path problem, but not on the original graph directly.

---

# 20. Why `node` Alone Is an Incorrect State

Imagine arriving at city `u`.

Situation A:

```text
fuel = 0
```

Situation B:

```text
fuel = 5
```

Even though both are at the same city, they are not equivalent.

Maybe a road needs:

```text
4 fuel
```

Then:

```text
(u,5)
```

can use it immediately.

But:

```text
(u,0)
```

cannot.

So the shortest-path state must remember fuel.

---

# 21. Correct State — `(node, fuel)`

Define:

```text
state = (u,f)
```

where:

```text
u = current city
f = current fuel
```

Meaning:

```text
"I am at city u with exactly f fuel."
```

Distance:

```text
dist[u][f]
```

means:

```text
minimum money required to reach
city u with f fuel remaining
```

This is the key formulation.

---

# 22. Fuel Transition 1 — Buy / Refill Fuel

Suppose we are at:

```text
(u,f)
```

and:

```text
f < K
```

We may buy one unit of fuel.

New state:

```text
(u,f+1)
```

If one unit costs:

```text
price[u]
```

then edge:

```text
(u,f)
  |
  | price[u]
  v
(u,f+1)
```

Relaxation:

```text
dist[u][f+1]
=
min(
    dist[u][f+1],
    dist[u][f] + price[u]
)
```

---

# 23. Fuel Transition 2 — Travel to a Neighbor

Suppose road:

```text
u ----d---- v
```

requires:

```text
d fuel
```

If:

```text
f >= d
```

we may travel.

Transition:

```text
(u,f)
    |
    | monetary cost = 0
    v
(v,f-d)
```

Why is edge cost `0`?

Because the money was already counted when we bought the fuel.

Driving consumes fuel, but does not buy fuel.

So:

```text
dist[v][f-d]
=
min(
    dist[v][f-d],
    dist[u][f]
)
```

---

# 24. Expanded Graph Visualization

Suppose:

```text
K = 3
```

For one city `A`:

```text
(A,0)
  |
 price[A]
  v
(A,1)
  |
 price[A]
  v
(A,2)
  |
 price[A]
  v
(A,3)
```

Now suppose road:

```text
A --2 fuel--> B
```

Then:

```text
(A,2) --0--> (B,0)

(A,3) --0--> (B,1)
```

because:

```text
3 - 2 = 1
```

This is the hidden state graph.

---

# 25. Fuel Example Dry Run

Suppose:

```text
Tank capacity K = 3

price[A] = 5
price[B] = 2

Road:
A --2 fuel--> B
```

Start:

```text
(A,0)
```

Goal:

```text
reach B
```

---

## Step 1

Buy one fuel at `A`.

```text
(A,0) → (A,1)

cost = 5
```

So:

```text
dist[A][1] = 5
```

---

## Step 2

Buy one more.

```text
(A,1) → (A,2)

additional cost = 5
```

Total:

```text
10
```

So:

```text
dist[A][2] = 10
```

---

## Step 3

Now road needs:

```text
2 fuel
```

So:

```text
(A,2) → (B,0)
```

Driving edge cost:

```text
0 money
```

Therefore:

```text
dist[B][0] = 10
```

The answer is the minimum among destination fuel states:

```text
min(
    dist[B][0],
    dist[B][1],
    ...
    dist[B][K]
)
```

---

# 26. Why Dijkstra Is the Natural Choice

Expanded graph edges have costs like:

```text
0
price[u]
```

Fuel prices are non-negative.

Therefore all expanded-graph edge weights are:

```text
>= 0
```

So Dijkstra works.

Important:

```text
We are not running Dijkstra only on city.
```

We run it on:

```text
(city,fuel)
```

states.

That distinction is crucial.

---

# 27. C++ — Dijkstra on `(node, fuel)`

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

struct Road {
    int to;
    int fuelNeeded;
};

struct State {
    ll cost;
    int node;
    int fuel;

    bool operator>(const State& other) const {
        return cost > other.cost;
    }
};

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m, capacity;
    cin >> n >> m >> capacity;

    vector<ll> price(n + 1);

    for (int node = 1; node <= n; node++) {
        cin >> price[node];
    }

    vector<vector<Road>> graph(n + 1);

    for (int i = 0; i < m; i++) {
        int u, v, fuelNeeded;
        cin >> u >> v >> fuelNeeded;

        graph[u].push_back({v, fuelNeeded});
        graph[v].push_back({u, fuelNeeded});
    }

    int source, destination;
    cin >> source >> destination;

    const ll INF = (1LL << 60);

    vector<vector<ll>> dist(n + 1, vector<ll>(capacity + 1, INF));

    priority_queue<State, vector<State>, greater<State>> pq;

    dist[source][0] = 0;
    pq.push({0, source, 0});

    while (!pq.empty()) {
        State current = pq.top();
        pq.pop();

        ll currentCost = current.cost;
        int node = current.node;
        int fuel = current.fuel;

        if (currentCost != dist[node][fuel]) {
            continue;
        }

        // Transition 1: buy one unit of fuel.
        if (fuel < capacity) {
            ll nextCost = currentCost + price[node];

            if (dist[node][fuel + 1] > nextCost) {
                dist[node][fuel + 1] = nextCost;
                pq.push({nextCost, node, fuel + 1});
            }
        }

        // Transition 2: travel using already purchased fuel.
        for (const Road& road : graph[node]) {
            if (fuel < road.fuelNeeded) {
                continue;
            }

            int nextNode = road.to;
            int nextFuel = fuel - road.fuelNeeded;

            if (dist[nextNode][nextFuel] > currentCost) {
                dist[nextNode][nextFuel] = currentCost;
                pq.push({currentCost, nextNode, nextFuel});
            }
        }
    }

    ll answer = INF;

    for (int fuel = 0; fuel <= capacity; fuel++) {
        answer = min(answer, dist[destination][fuel]);
    }

    if (answer == INF) {
        cout << -1 << '\n';
    } else {
        cout << answer << '\n';
    }

    return 0;
}
```

---

# 28. General Pattern — Add Missing Information to the State

The fuel problem teaches a very general CP pattern.

If:

```text
node alone
```

does not determine future choices, add whatever information is missing.

General form:

```text
state = (position, extra state)
```

Examples:

```text
(node, fuel)

(node, couponsUsed)

(node, parity)

(node, remainingDiscounts)

(row, col, keysMask)

(row, col, wallsBroken)

(node, previousColor)

(node, timeModK)
```

This is one of the most important graph-modelling ideas in competitive programming.

---

# 29. Common Expanded-State Forms

## 29.1 Limited free edges

Problem:

```text
you may use at most K roads for free
```

State:

```text
(node, freeEdgesUsed)
```

---

## 29.2 One discount coupon

State:

```text
(node, couponUsed)
```

where:

```text
couponUsed ∈ {0,1}
```

---

## 29.3 Break up to K walls

State:

```text
(row, col, wallsBroken)
```

---

## 29.4 Alternating colors

State:

```text
(node, lastColor)
```

---

## 29.5 Collecting keys

State:

```text
(row, col, keyMask)
```

Example:

```text
6 keys
```

means:

```text
2^6 = 64
```

possible key masks.

---

# 30. How to Estimate Complexity Before Coding

This is essential for expanded-state shortest path.

Suppose original graph has:

```text
N nodes
```

Fuel can be:

```text
0..K
```

Then total states:

```text
N × (K+1)
```

So roughly:

```text
V' = O(NK)
```

For each state we may:

- buy fuel once
- inspect original neighbors

So Dijkstra becomes approximately:

```text
O((NK + EK) log(NK))
```

often simplified as:

```text
O(EK log(NK))
```

depending on graph structure.

Always estimate expanded state count before coding.

---

# 31. Shortest Path Algorithm Selection Table

| Edge cost structure | Best first thought |
|---|---|
| Every edge costs `1` | BFS |
| Every edge has same cost | BFS |
| Edge costs only `0/1` | 0-1 BFS |
| All edge costs non-negative | Dijkstra |
| Negative edges possible | Bellman-Ford |
| APSP, moderate `V` | Floyd-Warshall |

But remember:

> Choose the algorithm **after** building the correct state graph.

For example, fuel problem:

Original road cost may represent distance.

But the objective is:

```text
minimum money
```

So the expanded graph's edge weights are based on:

```text
money spent
```

That is what determines the algorithm.

---

# 32. Step-by-Step Contest Workflow

Use this exact process.

## Step 1 — Remove the story

Ask:

```text
What am I minimizing?
```

Examples:

```text
moves
time
money
number of changes
number of walls broken
```

---

## Step 2 — Define the state

Ask:

```text
What information completely describes my current situation?
```

Examples:

```text
node
(row,col)
(node,fuel)
(row,col,k)
```

---

## Step 3 — Define transitions

Ask:

```text
From this state, what legal next states exist?
```

---

## Step 4 — Assign edge cost

Ask:

```text
How much does this transition increase the objective?
```

This is often the most important step.

---

## Step 5 — Inspect edge weights

Now classify:

```text
1 only?
0/1?
non-negative?
negative possible?
```

---

## Step 6 — Choose the shortest-path algorithm

```text
all 1       → BFS
0/1         → 0-1 BFS
nonnegative → Dijkstra
negative    → Bellman-Ford
```

---

## Step 7 — Estimate state count

Example:

```text
N × M grid
```

states:

```text
N × M
```

Example:

```text
N nodes × K fuel values
```

states:

```text
N × (K+1)
```

Check whether this fits constraints.

---

## Step 8 — Write relaxation

Generic:

```text
newDist = dist[current] + edgeCost

if (newDist < dist[next]) {
    update
}
```

Once this is correct, implementation becomes much easier.

---

# 33. Common Modelling Mistakes

## Mistake 1 — Choosing algorithm before defining edge cost

Wrong:

```text
"This is a grid, so BFS."
```

Correct:

```text
"What does each move cost?"
```

The arrow grid is a grid, but because costs are:

```text
0/1
```

we use:

```text
0-1 BFS
```

---

## Mistake 2 — Minimizing the wrong quantity

Suppose problem asks:

```text
minimum money
```

Do not put road distance directly as Dijkstra weight unless road distance is the money cost.

The edge weight must match:

```text
the objective
```

---

## Mistake 3 — Forgetting hidden state

Fuel example:

Wrong:

```text
dist[node]
```

Correct:

```text
dist[node][fuel]
```

because fuel changes what we can do next.

---

## Mistake 4 — Treating two non-equivalent situations as the same state

At same city:

```text
(city,0 fuel)
```

and:

```text
(city,10 fuel)
```

are different states.

---

## Mistake 5 — Building a huge explicit graph unnecessarily

You do not need to create every expanded edge beforehand.

Generate transitions:

```text
on demand
```

inside BFS / 0-1 BFS / Dijkstra.

---

## Mistake 6 — Using visited too aggressively

In weighted shortest path, a state can be discovered before its best distance is known.

For Dijkstra, the safe common pattern is:

```cpp
if (currentCost != dist[node][state]) {
    continue;
}
```

rather than marking a state when first inserted.

---

## Mistake 7 — Ignoring constraints after state expansion

Original:

```text
N = 100000
```

may look fine.

But if:

```text
K = 1000
```

then:

```text
N × K = 100,000,000
```

states.

That is likely too large.

Always calculate state count.

---

# 34. Final Recognition Model

The most important takeaway is not memorizing five shortest-path algorithms.

Use this mental model:

```text
STORY
  |
  v
What am I minimizing?
  |
  v
Define STATE
  |
  v
Define TRANSITIONS
  |
  v
Assign EDGE COST
  |
  v
Inspect edge weights
  |
  +-- 1       → BFS
  |
  +-- 0 / 1   → 0-1 BFS
  |
  +-- >= 0    → Dijkstra
  |
  +-- negative
             → Bellman-Ford
```

And when the current position is not enough:

```text
STATE EXPANSION
```

Example:

```text
node
```

becomes:

```text
(node,fuel)
```

or:

```text
(node,couponUsed)
```

or:

```text
(row,col,wallsBroken)
```

The strongest competitive-programming question to ask is:

```text
"What information changes my future choices?"
```

Put that information into the state.

Then:

```text
legal action = edge
objective increase = edge cost
```

Once the model is correct, the shortest-path algorithm is usually the easy part.
