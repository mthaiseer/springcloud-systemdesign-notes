# 011 — Graph: Multi-Source BFS — Save Yourself Pattern

> **Goal:** Learn how to solve grid problems where one person must reach an exit before one or more spreading threats/monsters.
>
> Core idea from the lecture:
>
> **Run one BFS from all monsters together**, then run BFS for the person while respecting monster arrival times.

---

## Table of Contents

1. [Problem Pattern](#1-problem-pattern)
2. [Prerequisite 1 — Grid as a Graph](#2-prerequisite-1--grid-as-a-graph)
3. [Prerequisite 2 — Ordinary BFS](#3-prerequisite-2--ordinary-bfs)
4. [Prerequisite 3 — Shortest Path in an Unweighted Grid](#4-prerequisite-3--shortest-path-in-an-unweighted-grid)
5. [Prerequisite 4 — Boundary Cells as Exits](#5-prerequisite-4--boundary-cells-as-exits)
6. [Why the Naive Monster-by-Monster Approach Is Expensive](#6-why-the-naive-monster-by-monster-approach-is-expensive)
7. [Multi-Source BFS](#7-multi-source-bfs)
8. [The Key Safety Inequality](#8-the-key-safety-inequality)
9. [Step-by-Step Example](#9-step-by-step-example)
10. [Person BFS with Monster Times](#10-person-bfs-with-monster-times)
11. [Path Reconstruction](#11-path-reconstruction)
12. [Complete C++17 Implementation](#12-complete-c17-implementation)
13. [Complexity](#13-complexity)
14. [Common Mistakes](#14-common-mistakes)
15. [Recognition Model](#15-recognition-model)
16. [Final Mental Model](#16-final-mental-model)

---

# 1. Problem Pattern

Typical story:

- We have an `N x M` grid.
- Some cells are blocked by walls.
- One cell contains the **person**.
- Several cells contain **monsters / fire / infection / enemies**.
- Every second:
  - the person may move to a neighboring cell,
  - every monster may also move to a neighboring cell.
- We want to know whether the person can reach an **exit** safely.

The lecture picture can be modeled like this:

```text
# # # # # # # # # #
# . . . P . . . M #
# . # . . . # . . #
# . # . . . # . . #
# . . . # # . . . #
# M . . . . . . . #
# # # # # # # # # #
```

Where:

```text
P = person
M = monster
# = wall
. = free cell
```

Main question:

```text
Can P reach some boundary cell before every monster can reach that cell?
```

---

# 2. Prerequisite 1 — Grid as a Graph

A grid is an **implicit graph**.

Every valid cell becomes a node.

For 4-direction movement:

```text
        up
         ^
         |
left <- (r,c) -> right
         |
         v
        down
```

Direction arrays:

```cpp
int dr[4] = {-1, 1, 0, 0};
int dc[4] = {0, 0, -1, 1};
```

From cell `(r, c)`:

```text
(r-1, c)
(r+1, c)
(r, c-1)
(r, c+1)
```

But a neighbor is usable only when:

```text
1. it is inside the grid
2. it is not a wall
```

### Small example

```text
. . #
. P .
# . .
```

From `P` at `(1,1)`:

```text
up    -> (0,1) valid
right -> (1,2) valid
down  -> (2,1) valid
left  -> (1,0) valid
```

So in the graph, `P` has four outgoing edges.

---

# 3. Prerequisite 2 — Ordinary BFS

BFS solves shortest path in an **unweighted graph**.

Why?

Because it explores nodes level by level.

```text
Distance 0: source
Distance 1: all cells 1 move away
Distance 2: all cells 2 moves away
Distance 3: all cells 3 moves away
...
```

### BFS wave diagram

```text
        2 2 2 2 2
        2 1 1 1 2
        2 1 S 1 2
        2 1 1 1 2
        2 2 2 2 2
```

`S` is the source.

The first time BFS reaches a cell, it has found the shortest path to that cell.

### Standard BFS skeleton

```cpp
queue<pair<int, int>> q;
q.push(start);
dist[start.first][start.second] = 0;

while (!q.empty()) {
    auto [r, c] = q.front();
    q.pop();

    for (int k = 0; k < 4; k++) {
        int nr = r + dr[k];
        int nc = c + dc[k];

        if (!valid(nr, nc)) {
            continue;
        }

        if (dist[nr][nc] != -1) {
            continue;
        }

        dist[nr][nc] = dist[r][c] + 1;
        q.push({nr, nc});
    }
}
```

---

# 4. Prerequisite 3 — Shortest Path in an Unweighted Grid

Suppose the person starts at `P`.

```text
P . . .
# # . .
. . . .
```

BFS might produce:

```text
0 1 2 3
# # 3 4
6 5 4 5
```

Meaning:

```text
distPerson[r][c] = minimum number of moves from P to (r,c)
```

This distance array is the foundation of the full problem.

---

# 5. Prerequisite 4 — Boundary Cells as Exits

In many escape problems, any reachable boundary cell is an exit.

A cell `(r,c)` is on the boundary if:

```text
r == 0
or r == N-1
or c == 0
or c == M-1
```

ASCII view:

```text
E E E E E E
E . . . . E
E . . . . E
E . . . . E
E E E E E E
```

Every `E` is an exit candidate.

So after computing distances, we conceptually want:

```text
answer = minimum safe distance among all boundary cells
```

---

# 6. Why the Naive Monster-by-Monster Approach Is Expensive

Suppose there are `X` monsters.

A naive idea is:

```text
Run BFS from monster 1
Run BFS from monster 2
Run BFS from monster 3
...
Run BFS from monster X
```

One BFS costs:

```text
O(N * M)
```

So all monster BFS runs cost:

```text
O(X * N * M)
```

If `X` is large, this is wasteful.

### What do we actually need?

For each cell, we only need:

```text
minimum distance from ANY monster
```

Not the individual distance from every monster.

That observation leads directly to **Multi-Source BFS**.

---

# 7. Multi-Source BFS

Instead of starting BFS from one source, start BFS from **all monster cells at the same time**.

### Key initialization

```text
queue = [M1, M2, M3, ...]

distMonster[M1] = 0
distMonster[M2] = 0
distMonster[M3] = 0
...
```

Then run normal BFS.

### Visual idea

```text
M1  ---> ---> --->
     \           /
      \         /
       \       /
        cell X
       /
      /
M2 ---> --->
```

Whichever monster reaches `X` first determines:

```text
distMonster[X]
```

So:

```text
distMonster[r][c]
= minimum distance from ANY monster to (r,c)
```

### Why this works

All monsters begin at time `0`.

BFS processes all distance-0 cells first, then all distance-1 cells, then distance-2 cells, and so on.

Therefore the first monster wave reaching a cell is automatically the closest monster.

### Multi-source initialization

```cpp
queue<pair<int, int>> q;

for (int r = 0; r < n; r++) {
    for (int c = 0; c < m; c++) {
        if (grid[r][c] == 'M') {
            distMonster[r][c] = 0;
            q.push({r, c});
        }
    }
}
```

Everything after this is ordinary BFS.

---

# 8. The Key Safety Inequality

For an exit `E`, the person survives only when:

```text
Dist(P, E) < minimum Dist(Mi, E)
```

Equivalent notation:

```text
distPerson[E] < distMonster[E]
```

This is the central mathematical model from the lecture.

## Why strict `<` and not `<=`?

Suppose:

```text
distPerson[E]  = 4
distMonster[E] = 4
```

Both arrive at time `4`.

That is unsafe.

So we require:

```text
person arrives strictly earlier
```

Hence:

```text
distPerson[E] < distMonster[E]
```

---

# 9. Step-by-Step Example

Consider:

```text
. . . . . .
. M . # . .
. . . # . P
. # . . . .
. . . M . .
. . . . . .
```

There are two monsters.

## Step 1 — Put both monsters into the queue

```text
queue = [M1, M2]
```

Both have distance `0`.

## Step 2 — Expand monster wave

Conceptually:

```text
0 1 2 3 3 4
1 0 1 # 2 3
2 1 2 # 2 3
3 # 2 1 2 3
3 2 1 0 1 2
4 3 2 1 2 3
```

This matrix means:

```text
distMonster[r][c]
```

for every reachable cell.

## Step 3 — Start BFS from the person

Person starts with:

```text
distPerson[P] = 0
```

But unlike normal BFS, the person may move to `(nr,nc)` only if:

```text
newPersonTime < distMonster[nr][nc]
```

where:

```text
newPersonTime = distPerson[r][c] + 1
```

## Step 4 — Stop when person reaches a safe boundary

If a boundary cell is reached safely, we have found an escape path.

Because BFS explores by increasing person distance, the first safe boundary found is a shortest safe escape.

---

# 10. Person BFS with Monster Times

For every candidate neighbor:

```text
newTime = distPerson[r][c] + 1
```

We accept it only when:

```text
newTime < distMonster[nr][nc]
```

or when no monster can ever reach it:

```text
distMonster[nr][nc] == INF
```

### Safe movement condition

```cpp
int newTime = distPerson[r][c] + 1;

if (newTime >= distMonster[nr][nc]) {
    continue;
}
```

If `distMonster` uses a large `INF`, this works naturally.

### Important observation

You do **not** need to run BFS independently from each exit.

You also do **not** need to test every monster separately.

The two distance fields summarize everything:

```text
Monster distance field = danger arrival time
Person distance field  = person arrival time
```

Safe iff:

```text
personTime < monsterTime
```

---

# 11. Path Reconstruction

Sometimes the problem asks not only whether escape is possible, but also the actual path.

Then store the previous cell / move used to reach each person cell.

For example:

```text
parentMove[nr][nc] = 'U'
parentMove[nr][nc] = 'D'
parentMove[nr][nc] = 'L'
parentMove[nr][nc] = 'R'
```

Suppose BFS found an exit:

```text
P -> Right -> Right -> Down -> Down -> Left -> Exit
```

Store reverse information while BFS runs.

Then reconstruct from exit back to start.

### Example

```text
Start = (3,2)
Exit  = (0,5)

Stored parent chain:

(0,5)
  |
(1,5)
  |
(2,5)
  |
(2,4)
  |
(2,3)
  |
(3,3)
  |
(3,2) = P
```

Reverse the collected moves at the end.

---

# 12. Complete C++17 Implementation

The following implementation follows the lecture model:

- find all monsters,
- push all of them initially,
- compute `distMonster`,
- BFS from the person,
- allow movement only when the person arrives before monsters,
- detect a boundary exit,
- reconstruct the path.

```cpp
#include <bits/stdc++.h>
using namespace std;

const int INF = 1e9;

int n, m;
vector<string> grid;
vector<vector<int>> distMonster;
vector<vector<int>> distPerson;
vector<vector<char>> parentMove;

int dr[4] = {-1, 1, 0, 0};
int dc[4] = {0, 0, -1, 1};
char moveChar[4] = {'U', 'D', 'L', 'R'};

bool valid(int r, int c) {
    return r >= 0 && r < n && c >= 0 && c < m && grid[r][c] != '#';
}

bool isBoundary(int r, int c) {
    return r == 0 || r == n - 1 || c == 0 || c == m - 1;
}

void bfsMonsters() {
    queue<pair<int, int>> q;
    distMonster.assign(n, vector<int>(m, INF));

    for (int r = 0; r < n; r++) {
        for (int c = 0; c < m; c++) {
            if (grid[r][c] == 'M') {
                distMonster[r][c] = 0;
                q.push({r, c});
            }
        }
    }

    while (!q.empty()) {
        auto [r, c] = q.front();
        q.pop();

        for (int k = 0; k < 4; k++) {
            int nr = r + dr[k];
            int nc = c + dc[k];

            if (!valid(nr, nc)) {
                continue;
            }

            if (distMonster[nr][nc] != INF) {
                continue;
            }

            distMonster[nr][nc] = distMonster[r][c] + 1;
            q.push({nr, nc});
        }
    }
}

pair<bool, pair<int, int>> bfsPerson(pair<int, int> start) {
    queue<pair<int, int>> q;
    distPerson.assign(n, vector<int>(m, -1));
    parentMove.assign(n, vector<char>(m, '?'));

    auto [sr, sc] = start;
    distPerson[sr][sc] = 0;
    q.push(start);

    if (isBoundary(sr, sc) && 0 < distMonster[sr][sc]) {
        return {true, start};
    }

    while (!q.empty()) {
        auto [r, c] = q.front();
        q.pop();

        for (int k = 0; k < 4; k++) {
            int nr = r + dr[k];
            int nc = c + dc[k];

            if (!valid(nr, nc)) {
                continue;
            }

            if (distPerson[nr][nc] != -1) {
                continue;
            }

            int newTime = distPerson[r][c] + 1;

            if (newTime >= distMonster[nr][nc]) {
                continue;
            }

            distPerson[nr][nc] = newTime;
            parentMove[nr][nc] = moveChar[k];
            q.push({nr, nc});

            if (isBoundary(nr, nc)) {
                return {true, {nr, nc}};
            }
        }
    }

    return {false, {-1, -1}};
}

string reconstructPath(pair<int, int> start, pair<int, int> finish) {
    string path;

    int r = finish.first;
    int c = finish.second;

    while (make_pair(r, c) != start) {
        char move = parentMove[r][c];
        path.push_back(move);

        if (move == 'U') {
            r++;
        } else if (move == 'D') {
            r--;
        } else if (move == 'L') {
            c++;
        } else if (move == 'R') {
            c--;
        }
    }

    reverse(path.begin(), path.end());
    return path;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n >> m;
    grid.resize(n);

    pair<int, int> start = {-1, -1};

    for (int r = 0; r < n; r++) {
        cin >> grid[r];

        for (int c = 0; c < m; c++) {
            if (grid[r][c] == 'A' || grid[r][c] == 'P') {
                start = {r, c};
            }
        }
    }

    bfsMonsters();

    auto result = bfsPerson(start);

    if (!result.first) {
        cout << "NO\n";
        return 0;
    }

    string path = reconstructPath(start, result.second);

    cout << "YES\n";
    cout << path.size() << '\n';
    cout << path << '\n';

    return 0;
}
```

---

# 13. Complexity

Let:

```text
V = N * M grid cells
```

Each cell has at most 4 neighbors.

So:

```text
E = O(N * M)
```

Monster multi-source BFS:

```text
O(N * M)
```

Person BFS:

```text
O(N * M)
```

Total:

```text
O(N * M)
```

Memory:

```text
O(N * M)
```

This is much better than running one BFS per monster:

```text
O(X * N * M)
```

where `X` is the number of monsters.

---

# 14. Common Mistakes

## Mistake 1 — Running BFS separately from every monster

Wrong idea:

```text
for each monster:
    run BFS
```

Better:

```text
put all monsters into one queue
run one BFS
```

---

## Mistake 2 — Using `<=` for safety

Unsafe:

```text
personTime <= monsterTime
```

Correct:

```text
personTime < monsterTime
```

Equal arrival time is not safe.

---

## Mistake 3 — Checking safety only at the exit

Wrong:

```text
only compare person and monster distances at final exit
```

The person must remain safe at **every intermediate cell**.

Correct transition:

```text
newPersonTime < distMonster[nextCell]
```

---

## Mistake 4 — Forgetting that all monsters start at time 0

Every monster source must be initialized with:

```text
distMonster = 0
```

before BFS starts.

---

## Mistake 5 — Forgetting walls

A wall is not a graph node we can enter.

```text
if grid[r][c] == '#'
    cannot move there
```

---

## Mistake 6 — Forgetting start-on-boundary case

If the person starts on the boundary and the monster is not already there:

```text
answer may be 0 moves
```

---

# 15. Recognition Model

Think **Multi-Source BFS** whenever the problem says something like:

```text
many fires spread simultaneously
many monsters move simultaneously
nearest hospital / nearest zero / nearest source
infection spreads from many cells
find distance to closest special node
multiple starting points with equal cost
```

### Recognition shortcut

If the question is:

```text
For every cell, what is the distance to the nearest source?
```

then think:

```text
MULTI-SOURCE BFS
```

### Escape variant

If the question is:

```text
Can one agent reach safety before spreading danger?
```

think:

```text
1. Multi-source BFS from all dangers
2. BFS from person
3. Allow person only where personTime < dangerTime
```

---

# 16. Final Mental Model

Do not think:

```text
monster 1 distance
monster 2 distance
monster 3 distance
...
```

Think:

```text
ONE combined danger wave
```

ASCII picture:

```text
M1 --->
       \
        \
         X <--- nearest monster arrival time
        /
       /
M2 --->
```

Then overlay the person's BFS:

```text
Person may enter cell X only if:

personArrival(X) < monsterArrival(X)
```

The entire pattern becomes:

```text
All monsters -> queue with distance 0
        |
        v
Multi-source BFS
        |
        v
nearest monster time for every cell
        |
        v
BFS from person
        |
        v
move only if personTime < monsterTime
        |
        v
first safe boundary = shortest safe escape
```

That is the core Multi-Source BFS pattern to remember.
