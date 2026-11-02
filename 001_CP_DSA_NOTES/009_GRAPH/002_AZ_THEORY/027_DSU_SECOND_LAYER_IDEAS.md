# DSU Second-Layer Ideas — Team / Player Queries and Component Metadata

> **Self-study Competitive Programming Note**
>
> This note continues from basic Union Find / DSU.
>
> Main lesson:
>
> **DSU does not only answer "are these nodes connected?"**
>
> We can store extra information at each component root:
>
> - number of players,
> - frequency of values,
> - number of distinct values,
> - minimum / maximum,
> - sum,
> - other component-level information.
>
> The most important modelling idea in this lecture is:
>
> ```text
> PLAYER
>   ↓
> original team id
>   ↓
> DSU find()
>   ↓
> current merged-team root
>   ↓
> component metadata
> ```

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Problem Model from the Lecture](#2-problem-model-from-the-lecture)
3. [The Main Modelling Idea](#3-the-main-modelling-idea)
4. [Why Players Are Not Necessarily DSU Nodes](#4-why-players-are-not-necessarily-dsu-nodes)
5. [Data We Need](#5-data-we-need)
6. [DSU Find with Path Compression](#6-dsu-find-with-path-compression)
7. [DSU Merge with Rank](#7-dsu-merge-with-rank)
8. [Second Layer — Store Metadata at the Root](#8-second-layer--store-metadata-at-the-root)
9. [Query 1 — Put / Move Player x into Team a](#9-query-1--put--move-player-x-into-team-a)
10. [Query 2 — Merge Team a and Team b](#10-query-2--merge-team-a-and-team-b)
11. [Query 3 — Are Players x and y in the Same Team?](#11-query-3--are-players-x-and-y-in-the-same-team)
12. [Query 4 — Size of the Team Containing Player x](#12-query-4--size-of-the-team-containing-player-x)
13. [Complete Dry Run](#13-complete-dry-run)
14. [Why We Do Not Update Every Player After a Merge](#14-why-we-do-not-update-every-player-after-a-merge)
15. [Generic Metadata Merge](#15-generic-metadata-merge)
16. [Distinct-Value / Frequency-Map Extension](#16-distinct-value--frequency-map-extension)
17. [Small-to-Large Idea for Maps](#17-small-to-large-idea-for-maps)
18. [A Clean Base Implementation](#18-a-clean-base-implementation)
19. [Extended DSU with Frequency Maps](#19-extended-dsu-with-frequency-maps)
20. [Complexity](#20-complexity)
21. [Common Mistakes](#21-common-mistakes)
22. [Recognition Patterns](#22-recognition-patterns)
23. [Final Mental Model](#23-final-mental-model)

---

# 1. Prerequisites

Before this topic, be comfortable with:

```text
1. Connected components
2. DSU parent array
3. find(x)
4. union / merge(x,y)
5. Path compression
6. Union by size or rank
```

We will quickly revise only what is needed.

---

## 1.1 Connected component

If:

```text
Team 1 and Team 2 are merged
```

they now behave like one bigger group.

Example:

```text
Before

Team 1: {P1, P2}
Team 2: {P3}
Team 3: {P4, P5}

merge(1,2)
```

After:

```text
Component A = Team 1 + Team 2
            = {P1,P2,P3}

Component B = Team 3
            = {P4,P5}
```

We need a fast way to know which component a team currently belongs to.

That is DSU.

---

## 1.2 Representative / root

Each DSU component has one root.

Example:

```text
1 ─┐
   ├──→ 2
3 ─┘
```

Here:

```text
root = 2
```

Therefore:

```text
find(1) = 2
find(2) = 2
find(3) = 2
```

---

## 1.3 Basic connectivity test

Two DSU nodes are in the same component exactly when:

```text
find(a) == find(b)
```

That simple equality is the foundation for the lecture's team queries.

---

# 2. Problem Model from the Lecture

The screenshots define two kinds of entities:

```text
N teams
M players
```

The DSU is built over:

```text
TEAMS
```

Players are separate objects that can belong to a team.

The visible query types are:

```text
1 x a
    Put / move player x into team a

2 a b
    Merge team a and team b

3 x y
    Check whether player x and player y are in the same team/component

4 x
    Return the size of the team containing player x
```

A later part of the lecture adds a second layer of metadata, including a frequency map and a query involving distinct values.

---

# 3. The Main Modelling Idea

This is the most important part.

A player stores a team ID:

```text
player x
   ↓
pteam[x]
```

But that team may later have been merged with other teams.

Therefore:

```text
pteam[x]
```

is not necessarily the current DSU root.

To obtain the current team component:

```text
currentTeam = find(pteam[x])
```

So conceptually:

```text
Player x
   |
   v
pteam[x] = some original team
   |
   v
find(pteam[x])
   |
   v
CURRENT TEAM ROOT
```

This gives us two layers:

```text
Layer 1:
player → team id

Layer 2:
team id → DSU root
```

That is why this is a useful "second-layer DSU" modelling pattern.

---

# 4. Why Players Are Not Necessarily DSU Nodes

A tempting design is:

```text
make every player a DSU node
```

But the lecture models teams themselves as DSU nodes.

Why is this useful?

Because the permanent union operation is:

```text
merge two TEAMS
```

not:

```text
merge two individual players
```

Players can also be assigned or moved independently.

So we keep:

```text
pteam[x] = team associated with player x
```

and use DSU only for permanent team unions.

---

## Visual model

Suppose:

```text
Players:
P1  P2  P3  P4

Teams:
T1  T2  T3
```

Assignments:

```text
P1 → T1
P2 → T1
P3 → T2
P4 → T3
```

Now:

```text
merge(T1,T2)
```

We do NOT need to change:

```text
P1 → T1
P2 → T1
P3 → T2
```

because:

```text
find(T1) == find(T2)
```

will now be true.

---

# 5. Data We Need

For the base problem:

```cpp
vector<int> pteam;
vector<int> parent;
vector<int> rankValue;
vector<int> teamSize;
```

Meaning:

```text
pteam[x]
    team id currently stored for player x
    -1 means player x is not assigned

parent[t]
    DSU parent of team t

rankValue[root]
    balancing information for DSU

teamSize[root]
    number of PLAYERS in that whole merged component
```

Important:

```text
teamSize is meaningful at the ROOT.
```

Do not think of:

```text
teamSize[t]
```

as correct for every non-root team node.

Use:

```text
teamSize[find(t)]
```

---

# 6. DSU Find with Path Compression

The visible lecture code uses path compression.

```cpp
int findRoot(int x) {
    if (parent[x] == x) {
        return x;
    }

    return parent[x] = findRoot(parent[x]);
}
```

---

## Step by step

Suppose:

```text
T1 → T2 → T4
```

and:

```text
T4 is root
```

Call:

```text
findRoot(T1)
```

Traversal:

```text
T1
↓
T2
↓
T4
```

Answer:

```text
T4
```

Path compression changes the structure to:

```text
T1 ─┐
T2 ─┼──→ T4
T4 ─┘
```

Future queries become very fast.

---

# 7. DSU Merge with Rank

The lecture also shows balancing using rank.

Basic structure:

```cpp
void mergeTeams(int x, int y) {
    int rootX = findRoot(x);
    int rootY = findRoot(y);

    if (rootX == rootY) {
        return;
    }

    if (rankValue[rootX] > rankValue[rootY]) {
        swap(rootX, rootY);
    }

    parent[rootX] = rootY;

    if (rankValue[rootX] == rankValue[rootY]) {
        rankValue[rootY]++;
    }
}
```

Conceptually:

```text
smaller-rank root
       ↓
attach below
       ↓
larger-rank root
```

This helps keep the DSU tree shallow.

---

# 8. Second Layer — Store Metadata at the Root

Normal DSU stores:

```text
parent
rank / size
```

But we can attach more information to each root.

Examples:

```text
number of players
sum of player values
minimum
maximum
frequency map
number of distinct values
```

This is the main "second-layer" idea.

---

## Example

Suppose current component root is `T2`.

Component contains:

```text
T1
T2
T4
```

and players:

```text
P1,P2,P3,P7
```

At root:

```text
teamSize[T2] = 4
```

If players have values:

```text
P1 value 5
P2 value 8
P3 value 5
P7 value 2
```

we could also store:

```text
freq[T2] = {
    2:1,
    5:2,
    8:1
}
```

Then:

```text
distinct values = freq[T2].size() = 3
```

---

# 9. Query 1 — Put / Move Player x into Team a

Visible query:

```text
1 x a
```

Meaning:

```text
put player x into team a
```

The lecture code also handles the case where player `x` already belonged to another team.

---

## Case A — player is currently unassigned

Suppose:

```text
pteam[x] = -1
```

Then:

```text
pteam[x] = a

root = findRoot(a)

teamSize[root]++
```

---

## Case B — player already belongs to another team

Suppose:

```text
pteam[x] = oldTeam
```

We must first remove that player from the size of the old current component.

Step 1:

```text
oldRoot = findRoot(pteam[x])
```

Step 2:

```text
teamSize[oldRoot]--
```

Step 3:

```text
pteam[x] = a
```

Step 4:

```text
newRoot = findRoot(a)
teamSize[newRoot]++
```

---

## Compact dry run

Initial:

```text
P5 → T1

teamSize[root(T1)] = 3
teamSize[root(T3)] = 2
```

Query:

```text
1 5 3
```

Meaning:

```text
move P5 to T3
```

Execution:

```text
oldRoot = find(T1)
teamSize[oldRoot]: 3 → 2

pteam[5] = T3

newRoot = find(T3)
teamSize[newRoot]: 2 → 3
```

Final:

```text
P5 → T3
```

---

## Important observation

We update:

```text
player membership count
```

but we do NOT split DSU components.

Team union is permanent.

Moving one player between teams is not the same as undoing a DSU merge.

---

# 10. Query 2 — Merge Team a and Team b

Visible query:

```text
2 a b
```

Meaning:

```text
merge team a and team b
```

Steps:

```text
rootA = findRoot(a)
rootB = findRoot(b)

if rootA == rootB:
    nothing to do

otherwise:
    choose surviving root
    connect roots
    combine metadata
```

---

## Metadata update

Suppose:

```text
teamSize[rootA] = 3
teamSize[rootB] = 5
```

After merging:

```text
new size = 3 + 5 = 8
```

If `rootB` becomes the new root:

```cpp
teamSize[rootB] += teamSize[rootA];
```

After that:

```text
teamSize[rootB] = 8
```

---

## Complete merge idea

```cpp
void mergeTeams(int x, int y) {
    int rootX = findRoot(x);
    int rootY = findRoot(y);

    if (rootX == rootY) {
        return;
    }

    if (rankValue[rootX] > rankValue[rootY]) {
        swap(rootX, rootY);
    }

    parent[rootX] = rootY;

    if (rankValue[rootX] == rankValue[rootY]) {
        rankValue[rootY]++;
    }

    teamSize[rootY] += teamSize[rootX];
}
```

The crucial rule is:

```text
METADATA MUST FOLLOW THE SURVIVING ROOT.
```

---

# 11. Query 3 — Are Players x and y in the Same Team?

Visible query:

```text
3 x y
```

We are given players, not teams.

So first obtain their team IDs:

```text
teamX = pteam[x]
teamY = pteam[y]
```

---

## Step 1 — unassigned check

If either player is unassigned:

```text
pteam[x] == -1
or
pteam[y] == -1
```

then they cannot currently belong to the same team.

Answer:

```text
NO
```

---

## Step 2 — convert stored team IDs to current roots

```text
rootX = findRoot(teamX)
rootY = findRoot(teamY)
```

---

## Step 3 — compare roots

```text
rootX == rootY
```

means:

```text
YES
```

otherwise:

```text
NO
```

---

## Example

Assignments:

```text
P1 → T1
P2 → T3
```

Suppose earlier:

```text
merge(T1,T3)
```

Even though:

```text
pteam[1] = T1
pteam[2] = T3
```

we get:

```text
find(T1) = T1
find(T3) = T1
```

Therefore:

```text
P1 and P2 are in the SAME merged team.
```

This is exactly why we call `find()`.

---

# 12. Query 4 — Size of the Team Containing Player x

Visible query:

```text
4 x
```

Meaning:

```text
how many players belong to the merged team containing x?
```

Steps:

```text
team = pteam[x]
```

If:

```text
team == -1
```

then player is not currently assigned.

The lecture code outputs:

```text
0
```

Otherwise:

```text
root = findRoot(team)
answer = teamSize[root]
```

---

## Example

Suppose:

```text
T1 contains P1,P2
T2 contains P3,P4,P5
```

Then:

```text
merge(T1,T2)
```

New component has:

```text
5 players
```

Even if:

```text
pteam[P1] = T1
```

query:

```text
4 P1
```

does:

```text
root = find(T1)
teamSize[root] = 5
```

Answer:

```text
5
```

---

# 13. Complete Dry Run

Let:

```text
Teams = T1,T2,T3
Players = P1,P2,P3,P4
```

Initially:

```text
pteam = [-1,-1,-1,-1]

teamSize:
T1=0
T2=0
T3=0
```

---

## Query 1

```text
1 P1 T1
```

Put P1 into T1.

Result:

```text
P1 → T1

size(T1)=1
```

---

## Query 2

```text
1 P2 T2
```

Result:

```text
P1 → T1
P2 → T2

size(T1)=1
size(T2)=1
```

---

## Query 3

```text
3 P1 P2
```

Compute:

```text
find(T1)=T1
find(T2)=T2
```

Different roots.

Answer:

```text
NO
```

---

## Query 4

```text
2 T1 T2
```

Merge teams.

Suppose T2 becomes root.

Before:

```text
size(T1)=1
size(T2)=1
```

After:

```text
T1 → T2

size(T2)=2
```

Notice:

```text
pteam[P1] is STILL T1
pteam[P2] is STILL T2
```

We do not rewrite P1.

---

## Query 5

```text
3 P1 P2
```

Now:

```text
find(pteam[P1])
= find(T1)
= T2

find(pteam[P2])
= find(T2)
= T2
```

Same root.

Answer:

```text
YES
```

---

## Query 6

```text
4 P1
```

We compute:

```text
find(T1)=T2
```

Then:

```text
teamSize[T2]=2
```

Answer:

```text
2
```

---

## Query 7 — move P1

```text
1 P1 T3
```

Old component:

```text
find(T1)=T2
```

Decrease:

```text
size(T2): 2 → 1
```

Assign:

```text
pteam[P1]=T3
```

Increase:

```text
size(T3): 0 → 1
```

Now:

```text
P1 → T3
P2 → T2
```

---

# 14. Why We Do Not Update Every Player After a Merge

Suppose:

```text
Team A has 100000 players
Team B has 100000 players
```

If we merge the teams and then rewrite:

```text
pteam[player]
```

for every player in one component, that merge could cost:

```text
O(component size)
```

Many merges could become very expensive.

Instead:

```text
leave pteam[player] unchanged
```

and only change:

```text
DSU parent of the team root
```

Now a player's current component is recovered lazily:

```text
find(pteam[player])
```

This changes an expensive mass update into:

```text
almost O(1)
```

per query.

---

## Visual comparison

### Bad

```text
merge(T1,T2)

Update every player:
P1 team = T2
P2 team = T2
P3 team = T2
P4 team = T2
...
```

Potentially:

```text
O(M)
```

---

### DSU way

```text
parent[root(T1)] = root(T2)
```

Only one structural update.

Players keep:

```text
P1 → T1
P2 → T1
...
```

But:

```text
find(T1) = T2
```

So everything still works.

---

# 15. Generic Metadata Merge

This is the deeper DSU pattern.

Suppose each component root stores:

```text
size
sum
minimum
maximum
```

Before merge:

```text
Root A:
size = SA
sum  = XA
min  = LA
max  = HA

Root B:
size = SB
sum  = XB
min  = LB
max  = HB
```

If B becomes the new root:

```text
size[B] = SA + SB

sum[B] = XA + XB

min[B] = min(LA, LB)

max[B] = max(HA, HB)
```

So union is not only:

```text
parent[A] = B
```

It is also:

```text
combine metadata(A,B)
```

---

## Generic mental template

```cpp
rootA = find(a);
rootB = find(b);

if (rootA == rootB) {
    return;
}

// choose surviving root

parent[rootA] = rootB;

// transfer component information
metadata[rootB] = combine(metadata[rootB], metadata[rootA]);
```

---

# 16. Distinct-Value / Frequency-Map Extension

The later screenshots show:

```cpp
vector<map<int, int>> numFreq;
```

and merging frequency information from one root into another.

The board also mentions a query for the number of distinct numbers/values in the team.

The screenshots do not fully show how each player's number/value is originally supplied, so the following section explains the visible DSU metadata idea generically.

---

## Suppose every player has a value

Example:

```text
P1 → value 7
P2 → value 3
P3 → value 7
P4 → value 5
```

If:

```text
P1,P2,P3
```

are in the same component:

```text
freq[root] = {
    3 : 1,
    7 : 2
}
```

Then:

```text
distinct values
=
freq[root].size()
=
2
```

---

## Merging two frequency maps

Suppose:

```text
freq[rootA] = {
    2:1,
    5:2
}

freq[rootB] = {
    5:1,
    8:3
}
```

When A merges into B:

```text
2: 0+1 = 1
5: 1+2 = 3
8: 3
```

Result:

```text
freq[rootB] = {
    2:1,
    5:3,
    8:3
}
```

Number of distinct values:

```text
3
```

---

## Visible lecture-style merge

The screenshots show the idea:

```cpp
for (auto [value, count] : numFreq[rootX]) {
    numFreq[rootY][value] += count;
}

numFreq[rootX].clear();
```

This is the important second-layer operation:

```text
DSU union
+
merge the root-owned maps
```

---

# 17. Small-to-Large Idea for Maps

When metadata is itself a container such as:

```text
set
map
unordered_map
vector
```

blindly merging a huge container into a small one repeatedly can be expensive.

A standard optimization is:

```text
small-to-large merging
```

Rule:

```text
always move the smaller container into the larger container
```

---

## Example

Suppose:

```text
Map A has 10000 keys
Map B has 20 keys
```

Better:

```text
move 20 entries into Map A
```

instead of:

```text
move 10000 entries into Map B
```

---

## Important distinction

DSU union-by-rank chooses roots based on DSU tree shape.

Small-to-large chooses based on:

```text
metadata container size
```

In some problems you design the union so both goals line up.

In others, you explicitly swap metadata containers before merging.

---

## Example

```cpp
if (numFreq[rootX].size() > numFreq[rootY].size()) {
    swap(numFreq[rootX], numFreq[rootY]);
}

for (auto [value, count] : numFreq[rootX]) {
    numFreq[rootY][value] += count;
}

numFreq[rootX].clear();
```

Now the smaller map is moved into the larger one.

---

# 18. A Clean Base Implementation

This implementation follows the visible four-query model.

```cpp
#include <bits/stdc++.h>
using namespace std;

struct TeamDSU {
    vector<int> parent;
    vector<int> rankValue;
    vector<int> teamSize;

    TeamDSU(int n) {
        parent.resize(n + 1);
        rankValue.assign(n + 1, 1);
        teamSize.assign(n + 1, 0);

        for (int i = 1; i <= n; i++) {
            parent[i] = i;
        }
    }

    int findRoot(int x) {
        if (parent[x] == x) {
            return x;
        }

        return parent[x] = findRoot(parent[x]);
    }

    void mergeTeams(int x, int y) {
        int rootX = findRoot(x);
        int rootY = findRoot(y);

        if (rootX == rootY) {
            return;
        }

        if (rankValue[rootX] > rankValue[rootY]) {
            swap(rootX, rootY);
        }

        parent[rootX] = rootY;
        teamSize[rootY] += teamSize[rootX];

        if (rankValue[rootX] == rankValue[rootY]) {
            rankValue[rootY]++;
        }
    }
};

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    TeamDSU dsu(n);

    vector<int> playerTeam(m + 1, -1);

    int q;
    cin >> q;

    while (q--) {
        int type;
        cin >> type;

        if (type == 1) {
            int player, team;
            cin >> player >> team;

            if (playerTeam[player] != -1) {
                int oldRoot = dsu.findRoot(playerTeam[player]);
                dsu.teamSize[oldRoot]--;
            }

            playerTeam[player] = team;

            int newRoot = dsu.findRoot(team);
            dsu.teamSize[newRoot]++;
        } else if (type == 2) {
            int teamA, teamB;
            cin >> teamA >> teamB;

            dsu.mergeTeams(teamA, teamB);
        } else if (type == 3) {
            int playerX, playerY;
            cin >> playerX >> playerY;

            int teamX = playerTeam[playerX];
            int teamY = playerTeam[playerY];

            if (teamX == -1 || teamY == -1) {
                cout << "No\n";
                continue;
            }

            teamX = dsu.findRoot(teamX);
            teamY = dsu.findRoot(teamY);

            cout << (teamX == teamY ? "Yes" : "No") << '\n';
        } else if (type == 4) {
            int player;
            cin >> player;

            int team = playerTeam[player];

            if (team == -1) {
                cout << 0 << '\n';
                continue;
            }

            team = dsu.findRoot(team);

            cout << dsu.teamSize[team] << '\n';
        }
    }

    return 0;
}
```

---

# 19. Extended DSU with Frequency Maps

The lecture's later code extends root metadata with something like:

```cpp
vector<map<int, int>> numFreq;
```

A generic DSU version is:

```cpp
struct DSU {
    vector<int> parent;
    vector<int> rankValue;
    vector<int> componentSize;
    vector<map<int, int>> freq;

    DSU(int n) {
        parent.resize(n + 1);
        rankValue.assign(n + 1, 1);
        componentSize.assign(n + 1, 0);
        freq.resize(n + 1);

        for (int i = 1; i <= n; i++) {
            parent[i] = i;
        }
    }

    int findRoot(int x) {
        if (parent[x] == x) {
            return x;
        }

        return parent[x] = findRoot(parent[x]);
    }

    void merge(int x, int y) {
        int rootX = findRoot(x);
        int rootY = findRoot(y);

        if (rootX == rootY) {
            return;
        }

        if (rankValue[rootX] > rankValue[rootY]) {
            swap(rootX, rootY);
        }

        parent[rootX] = rootY;
        componentSize[rootY] += componentSize[rootX];

        for (auto [value, count] : freq[rootX]) {
            freq[rootY][value] += count;
        }

        freq[rootX].clear();

        if (rankValue[rootX] == rankValue[rootY]) {
            rankValue[rootY]++;
        }
    }

    int distinctCount(int x) {
        int root = findRoot(x);
        return (int)freq[root].size();
    }
};
```

The central idea is more important than this exact code:

```text
ROOT owns component metadata.
```

Whenever roots merge:

```text
metadata must merge too.
```

---

# 20. Complexity

With:

```text
path compression
+
union by rank
```

ordinary DSU operations are amortized:

```text
O(α(N))
```

which is effectively constant for competitive-programming constraints.

---

## Base four-query model

### Query 1 — move player

Uses a constant number of `find` calls:

```text
O(α(N))
```

### Query 2 — merge teams

```text
O(α(N))
```

if only size metadata is maintained.

### Query 3 — same component

```text
O(α(N))
```

### Query 4 — component size

```text
O(α(N))
```

---

## Frequency-map extension

If merging maps:

```text
for every entry in one map:
    insert/update other map
```

then the cost also depends on map sizes.

With `std::map`, one key update costs roughly:

```text
O(log K)
```

where `K` is the current map size.

This is why small-to-large merging becomes important.

---

# 21. Common Mistakes

## Mistake 1 — Comparing stored team IDs directly

Wrong:

```cpp
playerTeam[x] == playerTeam[y]
```

Why wrong?

Because:

```text
T1 and T2 may already have been merged.
```

Correct:

```cpp
findRoot(playerTeam[x]) == findRoot(playerTeam[y])
```

---

## Mistake 2 — Updating every player during team merge

Do not do:

```text
for all players in merged team:
    rewrite playerTeam[player]
```

That destroys the main benefit of DSU.

Keep old team references and resolve them through `find()`.

---

## Mistake 3 — Reading metadata from a non-root

Wrong:

```cpp
teamSize[playerTeam[x]]
```

Correct:

```cpp
int root = findRoot(playerTeam[x]);
cout << teamSize[root];
```

Component metadata belongs to the representative.

---

## Mistake 4 — Forgetting to transfer metadata during union

If:

```text
rootX → rootY
```

then size must become:

```cpp
teamSize[rootY] += teamSize[rootX];
```

The same principle applies to:

```text
sum
frequency
min
max
set/map
```

---

## Mistake 5 — Decrementing an old raw team instead of its root

When moving a player:

Wrong:

```cpp
teamSize[playerTeam[x]]--;
```

Correct:

```cpp
int oldRoot = findRoot(playerTeam[x]);
teamSize[oldRoot]--;
```

The original team may no longer be the component representative.

---

## Mistake 6 — Forgetting unassigned players

The lecture uses:

```text
-1
```

for a player that is not assigned to any team.

Always check this before calling:

```cpp
findRoot(playerTeam[x])
```

---

## Mistake 7 — Losing metadata when the root changes

If union balancing decides:

```text
rootA becomes child of rootB
```

then metadata must end at:

```text
rootB
```

Your root-selection logic and metadata logic must agree.

---

# 22. Recognition Patterns

Think about this DSU pattern when a problem says:

```text
There are groups / teams / clans / networks.
Groups can be permanently merged.
Objects belong to those groups.
Queries ask about objects through their groups.
```

Examples:

```text
Are player x and y in the same team?
How many members are in x's group?
What is the sum of values in x's component?
How many distinct colors exist in x's component?
What is the min/max value in the component?
```

The modelling pattern is:

```text
OBJECT
  ↓
group id
  ↓
DSU root
  ↓
root metadata
```

---

## Strong recognition signal

If operations contain:

```text
MERGE components
+
QUERY some aggregate of a component
```

think:

```text
DSU + metadata at root
```

---

## Another strong signal

If an object keeps an old group identifier even after groups merge:

```text
do not update all objects
```

Instead:

```text
find(oldGroupId)
```

This is often the intended optimization.

---

# 23. Final Mental Model

Basic DSU:

```text
node
 ↓
root
```

This lecture:

```text
player
  ↓
stored team
  ↓
DSU root
  ↓
component information
```

So there are two separate responsibilities:

```text
playerTeam[x]
    tells us which DSU node player x references

find(team)
    tells us the current merged component

metadata[root]
    tells us information about that component
```

---

## The key rule

Whenever components merge:

```text
PARENTS merge
+
METADATA merges
```

---

## Visual memory

```text
P1 ─→ T1 ─┐
P2 ─→ T1 ─┤
          ├── DSU ROOT R
P3 ─→ T2 ─┤
P4 ─→ T2 ─┘
             │
             ├── size = 4
             ├── sum = ...
             ├── min/max = ...
             └── freq = {...}
```

Queries never need to update all players after a union.

They only do:

```text
player
→ stored team
→ find(root)
→ answer from root metadata
```

That is the main **second-layer DSU idea** to remember.
