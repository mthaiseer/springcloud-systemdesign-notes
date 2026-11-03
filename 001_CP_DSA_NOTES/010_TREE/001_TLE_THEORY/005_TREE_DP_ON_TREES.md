# 005 — DP on Trees — Visual Self-Learning Notes

> **Based on:** TLE Trees Advanced — DP on Trees  
> **Goal:** Learn the reusable ideas behind subtree DP, pick/not-pick DP, rerooting DP, Tree Distances II, Tree XOR, and small-to-large merging.

---

# Table of Contents

1. [Big Picture](#1-big-picture)
2. [Prerequisites](#2-prerequisites)
3. [How Tree DP Works](#3-how-tree-dp-works)
4. [Standard Technique 1 — Subtree Size](#4-standard-technique-1--subtree-size)
5. [Standard Technique 2 — Sum of Values in Subtree](#5-standard-technique-2--sum-of-values-in-subtree)
6. [Problem 1 — House Robber on Tree](#6-problem-1--house-robber-on-tree)
7. [Rerooting DP — Core Idea](#7-rerooting-dp--core-idea)
8. [Problem 2 — Tree Distances II](#8-problem-2--tree-distances-ii)
9. [Problem 3 — Tree XOR Rerooting Idea](#9-problem-3--tree-xor-rerooting-idea)
10. [Small-to-Large Merging](#10-small-to-large-merging)
11. [Problem 4 — Distinct Colors](#11-problem-4--distinct-colors)
12. [Reusable C++17 Templates](#12-reusable-c17-templates)
13. [Common Mistakes](#13-common-mistakes)
14. [Recognition Guide](#14-recognition-guide)
15. [Final Mental Model](#15-final-mental-model)

---

# 1. Big Picture

The lecture starts with simple subtree calculations and gradually builds toward more advanced tree-DP patterns.

```text
                 TREE DP
                    |
        +-----------+-----------+
        |                       |
   Subtree DP                Rerooting DP
        |                       |
   solve children            solve one root
   combine upward            transfer answer
        |                    root -> child
        |
   +----+-----+
   |          |
size/sum   pick/not-pick
```

Then another important optimization appears:

```text
Small-to-Large Merging
        |
        +--> maintain sets/maps for every subtree
```

The main lesson is not to memorize individual formulas.

The main lesson is:

```text
1. Root the tree.
2. Define exactly what DP[node] means.
3. Solve children first.
4. Combine child answers into parent.
5. If answer is needed for every root:
      derive how answer changes when root moves across one edge.
```

---

# 2. Prerequisites

---

## 2.1 Tree Basics

A tree is a connected graph with no cycle.

For `N` nodes:

```text
number of edges = N - 1
```

Example:

```text
                  1
               /     \
              2       3
            /   \      \
           4     5      6
                / \
               7   8
```

This is an undirected tree.

---

## 2.2 Rooting a Tree

Choose one node as the root.

If root = `1`:

```text
                  1
               /     \
              2       3
            /   \      \
           4     5      6
                / \
               7   8
```

Now every node except root has exactly one parent.

```text
parent[1] = none
parent[2] = 1
parent[3] = 1
parent[4] = 2
parent[5] = 2
parent[6] = 3
parent[7] = 5
parent[8] = 5
```

Children of node `2`:

```text
4, 5
```

Children of node `5`:

```text
7, 8
```

---

## 2.3 Subtree

The subtree of node `v` contains:

```text
v
+
all descendants of v
```

Using the same tree:

```text
                  1
               /     \
              2       3
            /   \      \
           4     5      6
                / \
               7   8
```

Subtree of `5`:

```text
              5
             / \
            7   8
```

So:

```text
subtree(5) = {5, 7, 8}
```

Subtree of `2`:

```text
              2
            /   \
           4     5
                / \
               7   8
```

So:

```text
subtree(2) = {2, 4, 5, 7, 8}
```

---

## 2.4 DFS Parent Check

Because the original graph is undirected:

```text
u ---- v
```

If DFS goes:

```text
u -> v
```

then adjacency list of `v` also contains `u`.

Without a parent check, DFS would immediately go back.

Standard pattern:

```cpp
void dfs(int node, int parent) {
    for (int next : graph[node]) {
        if (next == parent) {
            continue;
        }

        dfs(next, node);
    }
}
```

---

## 2.5 Postorder Thinking

Tree DP normally needs:

```text
children solved before parent
```

Visual:

```text
                  1
               /     \
              2       3
            /   \      \
           4     5      6
                / \
               7   8
```

DFS recursively reaches leaves first.

Conceptually:

```text
4 solved
7 solved
8 solved
5 combines 7 and 8
2 combines 4 and 5
6 solved
3 combines 6
1 combines 2 and 3
```

So tree DP usually has this flow:

```text
            node
          /  |  \
        c1   c2   c3
        |    |    |
      solve solve solve
        \    |    /
          combine
             |
          DP[node]
```

---

## 2.6 What Is a DP State?

Before writing any recurrence, finish this sentence:

> `dp[node]` means __________________________.

Or for multiple states:

> `dp[node][state]` means ___________________.

If the meaning is unclear, the recurrence will also be unclear.

Example:

```text
dp[v] = size of subtree rooted at v
```

or:

```text
dp[v][1] = maximum coins in subtree(v)
           when v IS selected

dp[v][0] = maximum coins in subtree(v)
           when v is NOT selected
```

State definition comes first.

Formula comes second.

---

# 3. How Tree DP Works

The generic structure is:

```cpp
void dfs(int node, int parent) {
    initialize dp[node];

    for (int child : graph[node]) {
        if (child == parent) {
            continue;
        }

        dfs(child, node);

        // combine child's solved answer
        // into node's answer
    }
}
```

The key idea:

```text
child DP is already correct
        |
        v
parent only needs to combine children
```

This is why trees are very natural for DP.

There is no cycle after fixing the parent direction.

---

# 4. Standard Technique 1 — Subtree Size

The lecture begins with subtree size.

---

## 4.1 State

Define:

```text
subSize[v]
=
number of nodes in subtree rooted at v
```

---

## 4.2 Base Contribution

Every node contributes itself:

```text
subSize[v] starts at 1
```

Then add sizes of all child subtrees.

---

## 4.3 Recurrence

```text
subSize[v]
=
1
+
sum(subSize[child])
for every child of v
```

Formula:

```text
subSize[v] = 1 + Σ subSize[c]
```

---

## 4.4 Better Tree Visualization

Consider:

```text
                         1
                    /         \
                   2           3
                /     \      /   \
               4       5    6     7
                      / \
                     8   9
```

Leaves:

```text
subSize[4] = 1
subSize[6] = 1
subSize[7] = 1
subSize[8] = 1
subSize[9] = 1
```

Node `5`:

```text
                     5
                    / \
                   8   9
```

Therefore:

```text
subSize[5]
= 1 + subSize[8] + subSize[9]
= 1 + 1 + 1
= 3
```

Node `2`:

```text
                   2
                /     \
               4       5
                      / \
                     8   9
```

Therefore:

```text
subSize[2]
= 1 + subSize[4] + subSize[5]
= 1 + 1 + 3
= 5
```

---

## 4.5 Compact Dry Run

```text
Leaf 4 -> subSize[4] = 1
Leaf 8 -> subSize[8] = 1
Leaf 9 -> subSize[9] = 1

Node 5:
subSize[5] = 1 + 1 + 1 = 3

Node 2:
subSize[2] = 1 + 1 + 3 = 5
```

---

## 4.6 C++17

```cpp
void dfsSubtreeSize(int node, int parent) {
    subSize[node] = 1;

    for (int child : graph[node]) {
        if (child == parent) {
            continue;
        }

        dfsSubtreeSize(child, node);
        subSize[node] += subSize[child];
    }
}
```

Complexity:

```text
O(N)
```

Each edge is processed a constant number of times.

---

# 5. Standard Technique 2 — Sum of Values in Subtree

Now suppose each node has a value:

```text
value[v]
```

We want:

```text
subSum[v]
=
sum of values in subtree(v)
```

---

## 5.1 State

```text
subSum[v]
=
value[v]
+
sum of all descendant values
```

---

## 5.2 Recurrence

Every node starts with its own value:

```text
subSum[v] = value[v]
```

Then:

```text
subSum[v] += subSum[child]
```

for every child.

Formula:

```text
subSum[v]
=
value[v]
+
Σ subSum[c]
```

---

## 5.3 Visual Example

```text
                        1(5)
                      /      \
                  2(3)        3(4)
                 /   \
             4(10)   5(2)
```

For node `2`:

```text
subtree(2) = {2, 4, 5}
```

Therefore:

```text
subSum[2]
= value[2] + value[4] + value[5]
= 3 + 10 + 2
= 15
```

Using DP:

```text
subSum[4] = 10
subSum[5] = 2

subSum[2]
= value[2] + subSum[4] + subSum[5]
= 3 + 10 + 2
= 15
```

---

## 5.4 C++17

```cpp
void dfsSubtreeSum(int node, int parent) {
    subSum[node] = value[node];

    for (int child : graph[node]) {
        if (child == parent) {
            continue;
        }

        dfsSubtreeSum(child, node);
        subSum[node] += subSum[child];
    }
}
```

Complexity:

```text
O(N)
```

---

# 6. Problem 1 — House Robber on Tree

The lecture's first full DP problem is the tree version of **House Robber**.

Problem idea:

- every node contains some coins,
- choose some nodes,
- two directly connected nodes cannot both be chosen,
- maximize total coins.

This is the **maximum-weight independent set on a tree**.

---

# 6.1 Why One DP Value Is Not Enough

Suppose:

```text
                 v
               /   \
              a     b
```

The answer for subtree `v` depends on whether we choose `v`.

Because:

```text
if v is chosen
=> a cannot be chosen
=> b cannot be chosen
```

But:

```text
if v is NOT chosen
=> each child may be chosen or not chosen
```

So we need two states.

---

# 6.2 State Definition

```text
dp[v][1]
=
maximum coins from subtree(v)
when v IS selected
```

```text
dp[v][0]
=
maximum coins from subtree(v)
when v is NOT selected
```

This state definition is the entire problem.

---

# 6.3 State 1 — Pick the Current Node

Suppose we choose `v`.

```text
                    [v PICKED]
                     /      \
                    x        x
                 child1   child2
                cannot    cannot
                be picked be picked
```

Therefore every child must use state `0`.

So:

```text
dp[v][1]
=
value[v]
+
Σ dp[child][0]
```

---

# 6.4 State 0 — Do Not Pick the Current Node

Suppose we do not choose `v`.

```text
                  [v NOT PICKED]
                     /      \
                    ?        ?
                 child1   child2
```

Each child is free.

For each child:

```text
best child answer
=
max(
    dp[child][0],
    dp[child][1]
)
```

Therefore:

```text
dp[v][0]
=
Σ max(dp[child][0], dp[child][1])
```

---

# 6.5 Complete Recurrence

```text
PICK v:

dp[v][1]
=
value[v]
+
Σ dp[c][0]
```

```text
DON'T PICK v:

dp[v][0]
=
Σ max(dp[c][0], dp[c][1])
```

Final answer:

```text
max(dp[root][0], dp[root][1])
```

---

# 6.6 Visual Example

Consider:

```text
                         1(7)
                      /        \
                   2(1)        3(1)
                  /   \          \
               4(7)   5(8)       6(10)
```

Start from leaves.

For leaf `4`:

```text
dp[4][1] = 7
dp[4][0] = 0
```

For leaf `5`:

```text
dp[5][1] = 8
dp[5][0] = 0
```

For leaf `6`:

```text
dp[6][1] = 10
dp[6][0] = 0
```

---

## Node 2

```text
                   2(1)
                  /   \
               4(7)   5(8)
```

### Pick node 2

Children cannot be picked:

```text
dp[2][1]
=
1 + dp[4][0] + dp[5][0]

= 1 + 0 + 0
= 1
```

### Do not pick node 2

Take best from each child:

```text
dp[2][0]
=
max(0,7) + max(0,8)

= 7 + 8
= 15
```

---

## Node 3

```text
                 3(1)
                   \
                   6(10)
```

Pick node 3:

```text
dp[3][1]
= 1 + dp[6][0]
= 1
```

Don't pick node 3:

```text
dp[3][0]
= max(dp[6][0], dp[6][1])
= max(0,10)
= 10
```

---

## Root 1

```text
                         1(7)
                      /        \
                     2          3
```

Pick root:

```text
dp[1][1]
=
7 + dp[2][0] + dp[3][0]

= 7 + 15 + 10
= 32
```

Don't pick root:

```text
dp[1][0]
=
max(15,1) + max(10,1)

= 15 + 10
= 25
```

Answer:

```text
max(32,25) = 32
```

---

# 6.7 Why This DP Is Correct

At every node there are only two possibilities:

```text
1. select node
2. do not select node
```

If selected:

```text
all children are forced to state 0
```

If not selected:

```text
every child independently chooses its better state
```

The subtrees of different children do not overlap.

Therefore their optimal values can simply be added.

---

# 6.8 C++17

```cpp
void dfsHouseRobber(int node, int parent) {
    dp[node][0] = 0;
    dp[node][1] = value[node];

    for (int child : graph[node]) {
        if (child == parent) {
            continue;
        }

        dfsHouseRobber(child, node);

        dp[node][1] += dp[child][0];
        dp[node][0] += max(dp[child][0], dp[child][1]);
    }
}
```

Answer:

```cpp
long long answer = max(dp[root][0], dp[root][1]);
```

Complexity:

```text
O(N)
```

---

# 7. Rerooting DP — Core Idea

The next major topic in the lecture is **Rerooting DP**.

---

## 7.1 When Do We Need Rerooting?

Suppose the problem asks:

```text
answer if root = 1
answer if root = 2
answer if root = 3
...
answer if root = N
```

Brute force:

```text
run a full DFS for every possible root
```

Complexity:

```text
O(N) per root
x N roots
=
O(N^2)
```

Too slow for large `N`.

---

## 7.2 Rerooting Strategy

The lecture's steps are:

```text
Step 1:
Choose any root.

Step 2:
Calculate the answer for this root.

Step 3:
Move root across one edge.

Step 4:
Derive how the old answer changes.

Step 5:
Repeat recursively.
```

Visual:

```text
Original root:

                  u
                / | \
               /  |  \
              v   a   b
             / \
            c   d


Move root from u to v:


                  v
                / | \
               /  |  \
              c   d   u
                     / \
                    a   b
```

We do **not** recompute everything.

We transform:

```text
answer[u] -> answer[v]
```

in `O(1)` or small time.

---

# 8. Problem 2 — Tree Distances II

The lecture uses CSES **Tree Distances II**.

Problem:

> For every node, find the sum of distances from that node to all other nodes.

Problem link:

https://cses.fi/problemset/task/1133/

---

# 8.1 What We Need

For each node `v`:

```text
ans[v]
=
Σ distance(v, x)
for all nodes x
```

---

# 8.2 Brute Force

For every node:

```text
run DFS/BFS
calculate all distances
```

Complexity:

```text
O(N^2)
```

Too slow.

---

# 8.3 First Root

Choose root `1`.

Example:

```text
                         1
                      /     \
                     2       3
                            / \
                           4   5
```

Distances from `1`:

```text
dist(1,1) = 0
dist(1,2) = 1
dist(1,3) = 1
dist(1,4) = 2
dist(1,5) = 2
```

Therefore:

```text
ans[1]
= 0 + 1 + 1 + 2 + 2
= 6
```

This first answer can be calculated using depths:

```text
ans[1] = Σ depth[v]
```

when rooted at `1`.

---

# 8.4 Also Compute Subtree Sizes

For the tree:

```text
                         1
                      /     \
                     2       3
                            / \
                           4   5
```

Subtree sizes:

```text
subSize[2] = 1
subSize[4] = 1
subSize[5] = 1

subSize[3]
= 1 + 1 + 1
= 3

subSize[1] = 5
```

These sizes tell us how many nodes are affected in each direction when rerooting.

---

# 8.5 Reroot from Parent to Child

Move root:

```text
1 -> 3
```

Old root:

```text
                         1
                      /     \
                     2       3
                            / \
                           4   5
```

New root:

```text
                         3
                      /  |  \
                     4   5   1
                            /
                           2
```

Now divide all nodes into two groups.

---

## Group A — Nodes Inside `subtree(3)`

These nodes are:

```text
3, 4, 5
```

Count:

```text
subSize[3] = 3
```

When root moves from `1` to `3`:

```text
distance to each of these nodes decreases by 1
```

Why?

Because new root is one edge closer.

Total change:

```text
-subSize[3]
```

---

## Group B — Nodes Outside `subtree(3)`

These nodes are:

```text
1, 2
```

Count:

```text
N - subSize[3]
= 5 - 3
= 2
```

When root moves from `1` to `3`:

```text
distance to each of these nodes increases by 1
```

Total change:

```text
+(N - subSize[3])
```

---

# 8.6 Reroot Formula

Therefore:

```text
ans[child]
=
ans[parent]
- subSize[child]
+ (N - subSize[child])
```

Simplify:

```text
ans[child]
=
ans[parent]
+ N
- 2 * subSize[child]
```

This is the key formula from the lecture.

---

# 8.7 Numerical Dry Run

We had:

```text
ans[1] = 6
N = 5
subSize[3] = 3
```

Then:

```text
ans[3]
=
6 - 3 + (5 - 3)

= 6 - 3 + 2

= 5
```

Verify directly from node `3`:

```text
dist(3,1) = 1
dist(3,2) = 2
dist(3,3) = 0
dist(3,4) = 1
dist(3,5) = 1
```

Sum:

```text
1 + 2 + 0 + 1 + 1 = 5
```

Correct.

---

# 8.8 Two DFS Solution

Use two passes.

### DFS 1

Calculate:

```text
subSize[v]
ans[root]
```

### DFS 2

Propagate:

```text
ans[child]
=
ans[parent] + N - 2*subSize[child]
```

---

# 8.9 C++17

```cpp
void dfs1(int node, int parent, int depth) {
    subSize[node] = 1;
    ans[1] += depth;

    for (int child : graph[node]) {
        if (child == parent) {
            continue;
        }

        dfs1(child, node, depth + 1);
        subSize[node] += subSize[child];
    }
}

void dfs2(int node, int parent) {
    for (int child : graph[node]) {
        if (child == parent) {
            continue;
        }

        ans[child] = ans[node] + n - 2LL * subSize[child];
        dfs2(child, node);
    }
}
```

Complexity:

```text
DFS 1: O(N)
DFS 2: O(N)

Total: O(N)
```

---

# 8.10 Recognition Pattern

If a problem asks:

```text
for every node as root...
```

and recomputing from scratch looks like:

```text
O(N^2)
```

ask:

> Can I calculate one root, then update the answer when moving across one edge?

That is the rerooting-DP signal.

---

# 9. Problem 3 — Tree XOR Rerooting Idea

The lecture next applies the same rerooting structure to Codeforces **Tree XOR**.

Problem link:

https://codeforces.com/contest/1882/problem/D

The important lecture idea is not a new rerooting technique.

It is the same pattern with a different **edge contribution**.

---

# 9.1 Edge Cost

For an edge:

```text
parent ---- child
```

define:

```text
edgeCost
=
value[parent] XOR value[child]
```

Let:

```text
C = value[parent] XOR value[child]
```

---

# 9.2 First-Root Contribution

When the tree is rooted at some root, the lecture expresses the root answer using subtree sizes and edge contributions.

Each edge contributes to all nodes below that edge.

For edge:

```text
parent
   |
   | cost C
   |
 child
```

there are:

```text
subSize[child]
```

nodes whose root-to-node path uses this edge.

Therefore this edge contributes:

```text
subSize[child] * C
```

to the root's total.

So the first-root answer has the form:

```text
ans[root]
=
Σ subSize[child] *
  (value[parent] XOR value[child])
```

over tree edges directed away from the chosen root.

---

# 9.3 Why `subSize * edgeCost`?

Example:

```text
                  1
                  |
                C = 7
                  |
                  2
                /   \
               3     4
```

Subtree of `2` contains:

```text
2, 3, 4
```

So:

```text
subSize[2] = 3
```

The edge `1-2` appears in the path from root `1` to:

```text
2
3
4
```

Three times.

Contribution:

```text
3 * 7 = 21
```

That is why:

```text
subSize[child] * edgeCost
```

appears.

---

# 9.4 Reroot Across One Edge

Suppose:

```text
u ---- v
```

and `v` is currently a child of `u`.

Let:

```text
C = value[u] XOR value[v]
S = subSize[v]
```

When rerooting:

```text
u -> v
```

the same two groups appear.

---

## Nodes Inside `subtree(v)`

Count:

```text
S
```

Their contribution through edge `u-v` is removed.

Change:

```text
-S * C
```

---

## Nodes Outside `subtree(v)`

Count:

```text
N - S
```

After rerooting at `v`, those paths now use edge `v-u`.

Change:

```text
+(N - S) * C
```

---

# 9.5 Reroot Formula

Therefore:

```text
ans[v]
=
ans[u]
- S*C
+ (N-S)*C
```

Factor `C`:

```text
ans[v]
=
ans[u]
+
(N - 2*S) * C
```

where:

```text
S = subSize[v]
C = value[u] XOR value[v]
```

Final:

```text
ans[v]
=
ans[u]
+
(N - 2*subSize[v])
*
(value[u] XOR value[v])
```

This is structurally the same as Tree Distances II.

---

# 9.6 Compare the Two Reroot Formulas

Tree Distances II:

```text
ans[v]
=
ans[u]
+
(N - 2*subSize[v])
```

Tree XOR pattern:

```text
ans[v]
=
ans[u]
+
(N - 2*subSize[v])
*
edgeCost
```

The rerooting logic is identical.

Only the per-edge contribution changes.

This is a very important recognition skill.

---

# 10. Small-to-Large Merging

The final major lecture topic is **Small-to-Large Merging**.

Use it when every subtree needs a data structure such as:

```text
set
map
frequency table
```

and child information must be merged into parent information.

---

# 10.1 Example Problem

For every node:

```text
find number of distinct colors in its subtree
```

If we keep a set:

```text
colors[v]
=
all colors appearing in subtree(v)
```

then:

```text
answer[v] = colors[v].size()
```

---

# 10.2 Naive Merging

Suppose:

```text
              v
            /   \
           a     b
```

After solving children:

```text
colors[a] = {...}
colors[b] = {...}
```

We need:

```text
colors[v]
=
{color[v]}
union
colors[a]
union
colors[b]
```

Naively copying every child set into a fresh parent set can repeatedly move many elements.

Worst case can become very expensive.

With `std::set`, naive repeated merging can approach:

```text
O(N^2 log N)
```

in bad tree shapes.

---

# 10.3 Small-to-Large Rule

Always merge:

```text
SMALLER container
into
LARGER container
```

Never the opposite.

Suppose:

```text
A has 100 elements
B has 3 elements
```

Good:

```text
move 3 elements into A
```

Bad:

```text
move 100 elements into B
```

---

# 10.4 Tree Visualization

Suppose:

```text
                         1
                      /     \
                     2       3
                            / \
                           4   5
```

Colors:

```text
1 -> red
2 -> blue
3 -> red
4 -> green
5 -> blue
```

Leaf sets:

```text
S[2] = {blue}
S[4] = {green}
S[5] = {blue}
```

At node `3`:

```text
start = {red}

merge {green}
merge {blue}

S[3] = {red, green, blue}
```

Then at root `1`:

```text
S[1] initially contains its own color

children:
S[2] size = 1
S[3] size = 3
```

Keep the larger set `S[3]`.

Merge the smaller set `S[2]` into it.

---

# 10.5 Why Swapping Helps

Suppose current node owns a smaller set:

```text
S[node].size() = 2
```

and child owns:

```text
S[child].size() = 10
```

Instead of moving 10 elements into 2:

```cpp
if (S[node].size() < S[child].size()) {
    swap(S[node], S[child]);
}
```

Now:

```text
S[node] = large set
S[child] = small set
```

Then move only the small set.

For standard containers, swapping the container objects is constant-time in the usual competitive-programming setting because the internal ownership/pointers are exchanged rather than every element being copied.

---

# 10.6 Why Small-to-Large Is Fast

This proof is extremely important.

Suppose one element is moved from one set to another.

Because we only move small into large:

```text
destination size >= source size
```

After the merge, the set containing that moved element has at least doubled in size.

Example:

```text
1 -> 2 -> 4 -> 8 -> 16 -> 32 -> ... -> N
```

An element's container can double only:

```text
O(log N)
```

times.

Therefore each element is moved at most:

```text
O(log N)
```

times.

Across `N` elements:

```text
O(N log N)
```

element movements.

With `std::set`, each insertion costs:

```text
O(log N)
```

So total:

```text
O(N log^2 N)
```

This is the standard complexity for the `std::set` implementation shown in this style of solution.

---

# 11. Problem 4 — Distinct Colors

The lecture uses CSES **Distinct Colors**.

Problem idea:

> For every node, find how many different colors occur in its subtree.

---

# 11.1 State

Maintain:

```text
set<int> colors[v]
```

such that after DFS:

```text
colors[v]
=
all distinct colors in subtree(v)
```

Then:

```text
answer[v] = colors[v].size()
```

---

# 11.2 Transition

For every child:

```text
1. solve child
2. compare container sizes
3. swap so node owns the larger set
4. insert all elements from smaller child set
```

Finally insert:

```text
color[v]
```

---

# 11.3 Step-by-Step Dry Run

Tree:

```text
                         1(R)
                      /       \
                   2(B)       3(R)
                             /    \
                          4(G)    5(B)
```

Leaves:

```text
S[2] = {B}
answer[2] = 1

S[4] = {G}
answer[4] = 1

S[5] = {B}
answer[5] = 1
```

Node `3`:

```text
own color = R

merge child 4:
{R} + {G}
= {R,G}

merge child 5:
{R,G} + {B}
= {R,G,B}

answer[3] = 3
```

Root `1`:

```text
S[2] = {B}
S[3] = {R,G,B}
```

`S[3]` is larger.

Swap ownership so root uses the larger container.

Then insert `S[2]`.

Result:

```text
S[1] = {R,G,B}

answer[1] = 3
```

---

# 11.4 C++17 Using `set`

```cpp
vector<set<int>> colors;
vector<int> answer;
vector<int> nodeColor;

void dfsDistinctColors(int node, int parent) {
    colors[node].insert(nodeColor[node]);

    for (int child : graph[node]) {
        if (child == parent) {
            continue;
        }

        dfsDistinctColors(child, node);

        if (colors[node].size() < colors[child].size()) {
            swap(colors[node], colors[child]);
        }

        for (int color : colors[child]) {
            colors[node].insert(color);
        }

        colors[child].clear();
    }

    answer[node] = colors[node].size();
}
```

Complexity:

```text
O(N log^2 N)
```

for the `std::set` version.

---

# 12. Reusable C++17 Templates

---

## 12.1 Subtree Size

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> graph;
vector<int> subSize;

void dfs(int node, int parent) {
    subSize[node] = 1;

    for (int child : graph[node]) {
        if (child == parent) {
            continue;
        }

        dfs(child, node);
        subSize[node] += subSize[child];
    }
}
```

---

## 12.2 Subtree Sum

```cpp
vector<long long> value;
vector<long long> subSum;

void dfs(int node, int parent) {
    subSum[node] = value[node];

    for (int child : graph[node]) {
        if (child == parent) {
            continue;
        }

        dfs(child, node);
        subSum[node] += subSum[child];
    }
}
```

---

## 12.3 House Robber on Tree

```cpp
vector<long long> value;
vector<array<long long, 2>> dp;

void dfs(int node, int parent) {
    dp[node][0] = 0;
    dp[node][1] = value[node];

    for (int child : graph[node]) {
        if (child == parent) {
            continue;
        }

        dfs(child, node);

        dp[node][1] += dp[child][0];
        dp[node][0] += max(dp[child][0], dp[child][1]);
    }
}
```

Answer:

```cpp
long long answer = max(dp[1][0], dp[1][1]);
```

---

## 12.4 Tree Distances II

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int n;
vector<vector<int>> graph;
vector<int> subSize;
vector<ll> answer;

void dfs1(int node, int parent, int depth) {
    subSize[node] = 1;
    answer[1] += depth;

    for (int child : graph[node]) {
        if (child == parent) {
            continue;
        }

        dfs1(child, node, depth + 1);
        subSize[node] += subSize[child];
    }
}

void dfs2(int node, int parent) {
    for (int child : graph[node]) {
        if (child == parent) {
            continue;
        }

        answer[child] = answer[node] + n - 2LL * subSize[child];
        dfs2(child, node);
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n;

    graph.assign(n + 1, {});
    subSize.assign(n + 1, 0);
    answer.assign(n + 1, 0);

    for (int i = 0; i < n - 1; i++) {
        int u, v;
        cin >> u >> v;

        graph[u].push_back(v);
        graph[v].push_back(u);
    }

    dfs1(1, 0, 0);
    dfs2(1, 0);

    for (int node = 1; node <= n; node++) {
        cout << answer[node] << ' ';
    }

    cout << '\n';

    return 0;
}
```

---

## 12.5 Tree XOR Reroot Skeleton

```cpp
using ll = long long;

vector<ll> value;
vector<ll> answer;
vector<int> subSize;

void dfsSubtree(int node, int parent) {
    subSize[node] = 1;

    for (int child : graph[node]) {
        if (child == parent) {
            continue;
        }

        dfsSubtree(child, node);

        subSize[node] += subSize[child];

        ll edgeCost = value[node] ^ value[child];
        answer[1] += 1LL * subSize[child] * edgeCost;
    }
}

void dfsReroot(int node, int parent) {
    for (int child : graph[node]) {
        if (child == parent) {
            continue;
        }

        ll edgeCost = value[node] ^ value[child];

        answer[child] =
            answer[node]
            + 1LL * (n - 2 * subSize[child]) * edgeCost;

        dfsReroot(child, node);
    }
}
```

---

## 12.6 Small-to-Large `set` Template

```cpp
vector<set<int>> bag;
vector<int> nodeValue;
vector<int> answer;

void dfs(int node, int parent) {
    bag[node].insert(nodeValue[node]);

    for (int child : graph[node]) {
        if (child == parent) {
            continue;
        }

        dfs(child, node);

        if (bag[node].size() < bag[child].size()) {
            swap(bag[node], bag[child]);
        }

        for (int value : bag[child]) {
            bag[node].insert(value);
        }

        bag[child].clear();
    }

    answer[node] = bag[node].size();
}
```

---

# 13. Common Mistakes

---

## Mistake 1 — Writing Formula Before State Meaning

Bad:

```text
dp[v][1] = ...
```

without knowing what state `1` means.

Always write first:

```text
dp[v][1] = best answer when v is picked
dp[v][0] = best answer when v is not picked
```

---

## Mistake 2 — Forgetting Parent Check

Wrong:

```cpp
for (int child : graph[node]) {
    dfs(child, node);
}
```

This returns to the parent forever.

Correct:

```cpp
if (child == parent) {
    continue;
}
```

---

## Mistake 3 — Combining Child Before Solving It

Wrong order:

```text
use dp[child]
then dfs(child)
```

Correct:

```text
dfs(child)
then use dp[child]
```

Tree DP is usually postorder.

---

## Mistake 4 — House Robber: Picking Child When Parent Is Picked

If parent is selected:

```text
child MUST be not selected
```

So:

```text
dp[v][1] += dp[child][0]
```

not:

```text
max(dp[child][0], dp[child][1])
```

---

## Mistake 5 — Rerooting Without Splitting Nodes into Two Groups

The clean way to derive reroot formulas is always:

```text
Group 1:
nodes inside child subtree

Group 2:
nodes outside child subtree
```

Then ask:

```text
does each distance/contribution go up or down?
by how much?
```

Do not memorize:

```text
N - 2*subSize
```

Derive it from the two groups.

---

## Mistake 6 — Small-to-Large but Merging Large into Small

Wrong:

```text
copy huge child container into tiny parent container
```

Correct:

```text
if parent container is smaller:
    swap

then merge small -> large
```

---

## Mistake 7 — Assuming Swap Copies All Elements

For the standard container approach used here, swapping containers exchanges their internal ownership rather than manually moving every element.

That is why this pattern is useful.

---

# 14. Recognition Guide

| Problem wording | Think |
|---|---|
| size of every subtree | subtree DP |
| sum/value/frequency in subtree | DFS + combine children |
| choose nodes but adjacent choices conflict | pick / not-pick tree DP |
| answer needed for every possible root | rerooting DP |
| sum of distances from every node | rerooting |
| root-to-node edge contribution changes with root | rerooting |
| distinct values/colors in every subtree | small-to-large |
| merge sets/maps from child subtrees | small-to-large |

---

# 14.1 Tree-DP Recognition Checklist

When you see a tree problem, ask in this order:

```text
Q1. Is the answer for a node determined by its children?
    |
    +--> Yes -> subtree DP

Q2. Does current node have multiple choices/states?
    |
    +--> Yes -> dp[node][state]

Q3. Is answer needed for every possible root?
    |
    +--> Yes -> rerooting DP

Q4. Does every subtree maintain a large set/map/frequency structure?
    |
    +--> Yes -> small-to-large merging
```

---

# 15. Final Mental Model

The whole lecture can be remembered as four layers.

---

## Layer 1 — Simple Subtree Accumulation

```text
                  node
                /  |  \
               a   b   c
```

Children solved first.

Then:

```text
DP[node]
=
own contribution
+
combine(DP[a], DP[b], DP[c])
```

Examples:

```text
subtree size
subtree sum
```

---

## Layer 2 — Multiple States

When parent choice changes child choice:

```text
                  node
                /      \
             child    child
```

Use:

```text
dp[node][state]
```

House Robber:

```text
state 1 = pick
state 0 = don't pick
```

---

## Layer 3 — Rerooting

If answer is needed for every root:

```text
parent u
   |
   |
 child v
```

Calculate:

```text
answer[v]
from
answer[u]
```

by splitting nodes into:

```text
inside subtree(v)
outside subtree(v)
```

For distance:

```text
inside  -> -1 each
outside -> +1 each
```

Therefore:

```text
ans[v]
=
ans[u]
- subSize[v]
+ (N - subSize[v])
```

---

## Layer 4 — Small-to-Large

If every subtree keeps a container:

```text
set
map
frequency structure
```

always:

```text
merge SMALL into LARGE
```

because every moved element enters a container at least twice as large.

So each element moves only:

```text
O(log N)
```

times.

---

# One-Screen Revision

```text
TREE DP
=======

1. Root tree.
2. DFS children.
3. Solve children first.
4. Combine upward.


SUBTREE SIZE
============

subSize[v]
=
1 + Σ subSize[child]


SUBTREE SUM
===========

subSum[v]
=
value[v] + Σ subSum[child]


HOUSE ROBBER
============

dp[v][1]
=
value[v] + Σ dp[c][0]

dp[v][0]
=
Σ max(dp[c][0], dp[c][1])

answer
=
max(dp[root][0], dp[root][1])


REROOTING
=========

calculate one root

then move:

parent -> child

derive how answer changes


TREE DISTANCES II
=================

ans[child]
=
ans[parent]
- subSize[child]
+ (N - subSize[child])

=
ans[parent]
+ N
- 2*subSize[child]


TREE XOR REROOT
===============

C = value[parent] XOR value[child]

ans[child]
=
ans[parent]
+
(N - 2*subSize[child]) * C


SMALL TO LARGE
==============

if small/large containers must merge:

always merge smaller into larger

each element moves:
O(log N) times

std::set version:
O(N log^2 N)
```

---

# Practice Order

Recommended order after studying this note:

```text
1. Subtree Size
2. Subtree Sum
3. House Robber on Tree
4. CSES Tree Distances II
5. Codeforces 1882D - Tree XOR
6. CSES Distinct Colors
```

The goal is to recognize the pattern before looking at the recurrence.

For every problem, first write:

```text
What does dp[node] mean?
What information comes from children?
What changes if root moves?
What structure is being merged?
```

If those four questions are clear, most of the code becomes mechanical.
