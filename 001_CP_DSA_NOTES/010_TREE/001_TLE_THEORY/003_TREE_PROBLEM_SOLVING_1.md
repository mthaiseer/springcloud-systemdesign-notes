# 003 — Trees Problem Solving 1
## TLE-Style Self-Learning Notes — Step by Step

> Goal: learn how to **model tree problems**, not memorize three solutions.
>
> Problems covered:
> 1. **Kuro and Walking Route** — subtree counting + complement counting
> 2. **Tree Cutting** — postorder DFS + greedy + binary search on answer
> 3. **13th Labour of Heracles** — degree contribution + greedy sorting

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [A Tree Picture We Will Reuse](#2-a-tree-picture-we-will-reuse)
3. [Problem 1 — Kuro and Walking Route](#3-problem-1--kuro-and-walking-route)
4. [Problem 2 — Tree Cutting](#4-problem-2--tree-cutting)
5. [Problem 3 — 13th Labour of Heracles](#5-problem-3--13th-labour-of-heracles)
6. [Pattern Comparison](#6-pattern-comparison)
7. [Final Mental Model](#7-final-mental-model)

---

# 1. Prerequisites

Before solving the lecture problems, be comfortable with these ideas.

---

## 1.1 What is a tree?

A tree is a connected graph with no cycle.

For `N` nodes:

```text
number of edges = N - 1
```

Most important property:

> Between any two nodes in a tree, there is exactly **one simple path**.

Example:

```text
                 1
              /     \
             2       3
           /   \    / \
          4     5  6   7
```

Path from `4` to `7` is forced:

```text
4 -> 2 -> 1 -> 3 -> 7
```

There is no second route.

This unique-path property is the reason many tree problems become counting problems.

---

## 1.2 Rooting an undirected tree

The original tree has no parent/child direction.

If we choose node `1` as root:

```text
                 1        <- root
              /     \
             2       3
           /   \    / \
          4     5  6   7
```

Now:

```text
parent[2] = 1
parent[4] = 2
children(2) = {4, 5}
```

### Important

Changing the root does **not** change the edges.

It only changes how we describe:

```text
parent
child
ancestor
subtree
```

This is extremely important in Problem 1.

---

## 1.3 Subtree

For a rooted tree, the subtree of node `u` contains:

```text
u + every descendant below u
```

Example:

```text
                 1
              /     \
             2       3
           /   \    / \
          4     5  6   7
```

Subtree of `2`:

```text
             2
           /   \
          4     5
```

So:

```text
subtreeSize[2] = 3
```

Formula:

```text
subtreeSize[u] = 1 + sum(subtreeSize[child])
```

---

## 1.4 Postorder DFS — children first, node later

Subtree information must come **from children upward**.

```text
        u
      /   \
     a     b
```

We cannot know `subtreeSize[u]` before knowing the sizes of `a` and `b`.

So DFS does:

```text
DFS(a)
DFS(b)
compute u
```

Template:

```cpp
int dfs(int node, int parent) {
    int subtreeSize = 1;

    for (int next : graph[node]) {
        if (next == parent) {
            continue;
        }

        subtreeSize += dfs(next, node);
    }

    return subtreeSize;
}
```

Mental model:

```text
GO DOWN  -> collect information
COME BACK -> combine information
```

---

## 1.5 Degree of a node

`degree[u]` = number of edges touching node `u`.

```text
                 1
              /     \
             2       3
           /   \      \
          4     5      6
```

Degrees:

```text
deg(1) = 2
deg(2) = 3
deg(3) = 2
deg(4) = 1
deg(5) = 1
deg(6) = 1
```

Leaf nodes usually have degree `1` in an unrooted tree.

---

## 1.6 Ordered pairs

`(u, v)` and `(v, u)` are different when order matters.

With `N` nodes:

```text
choose start = N ways
choose different end = N - 1 ways

Total ordered pairs = N * (N - 1)
```

Example, `N = 3`:

```text
(1,2) (1,3)
(2,1) (2,3)
(3,1) (3,2)

Total = 3 * 2 = 6
```

This is the starting point of Problem 1.

---

## 1.7 Complement counting

Sometimes directly counting valid cases is difficult.

Use:

```text
valid = total - invalid
```

This is especially powerful when the invalid cases have a clean product form:

```text
invalid = choicesForStart * choicesForEnd
```

---

## 1.8 Binary Search on Answer

Suppose we ask:

```text
Can we make every component have size at least X?
```

If `X = 10` is possible, then smaller values are also possible:

```text
1 2 3 ... 9 10 | 11 12 ...
T T T ... T  T  | maybe false
```

This monotonic behavior allows binary search.

We need a checker:

```text
check(X) = can we satisfy the condition for this X?
```

Then search for the **largest True**.

This is Problem 2.

---

## 1.9 Why `long long` is necessary

Tree counting often produces values around `N^2`.

For example:

```text
N = 300000
N * (N - 1) ~= 9 * 10^10
```

This does not fit in 32-bit `int`.

Use:

```cpp
using ll = long long;
```

---

# 2. A Tree Picture We Will Reuse

A clean tree is easier to reason about than a crowded sketch.

```text
                              1
                       ┌──────┴──────┐
                       │             │
                       2             3
                  ┌────┴────┐   ┌────┴────┐
                  │         │   │         │
                  4         5   6         7
                            │
                            8
```

If rooted at `1`:

```text
subtree(2) = {2, 4, 5, 8}
subtree(5) = {5, 8}
subtree(3) = {3, 6, 7}
```

Whenever a problem talks about a subtree, mentally draw a boundary around it.

```text
                       1
                  ┌────┴────┐
            ┌─────2─────┐    3
            │   /   \   │   / \
            │  4     5  │  6   7
            │        │   │
            │        8   │
            └────────────┘

            subtree(2)
```

---

# 3. Problem 1 — Kuro and Walking Route

**Codeforces 979C — Kuro and Walking Route**

Problem link:

<https://codeforces.com/problemset/problem/979/C>

---

## 3.1 What is the problem asking?

We have a tree with two special nodes:

```text
x = Flowrisa
y = Beetopia
```

For an ordered pair `(u, v)`, Kuro walks along the unique path from `u` to `v`.

A route is forbidden when the path reaches:

```text
x first
then later y
```

We need the number of allowed ordered pairs.

---

## 3.2 Remove the story

Forget flowers and bees.

Mathematically:

> Count ordered pairs `(u, v)` whose unique tree path does **not** contain `x` before `y`.

This is easier through complement counting:

```text
answer = all ordered pairs - bad ordered pairs
```

Total:

```text
all = N * (N - 1)
```

Now only count the bad pairs.

---

## 3.3 Key modelling trick — root the tree at `y`

The lecture's central idea is:

```text
make y the root
```

Why?

Because then the path from `x` upward eventually reaches `y`.

Visualize:

```text
                              y
                              │
                              z
                              │
                         ┌────┴────┐
                         │         │
                         a         b
                         │
                         x
                       /   \
                      p     q
                    /  \     \
                   ... ...    ...
```

Here:

```text
z = direct child of y lying on the path y -> ... -> x
```

The branch containing `x` is exactly `subtree(z)`.

And `subtree(x)` is the group of possible bad starting nodes.

---

## 3.4 Which starting nodes can create a bad route?

For the path to visit `x` and then `y`, the start `u` must be on the `x` side.

More precisely:

```text
u must belong to subtree(x)
```

Why?

Because with the tree rooted at `y`, every node inside `subtree(x)` must travel upward through `x` before it can reach `y`.

So:

```text
number of bad starts = subtreeSize[x]
```

---

## 3.5 Which ending nodes can create a bad route?

Now look at `z`.

```text
                              y
                         ┌────┼────┐
                         │    │    │
                         z    r    s
                         │
                         ...
                         │
                         x
```

If the destination is still inside `subtree(z)`, the path may stop before reaching `y`.

To guarantee that the route crosses `y` after crossing `x`, destination `v` must be **outside** `subtree(z)`.

Number of nodes outside `subtree(z)`:

```text
N - subtreeSize[z]
```

Therefore:

```text
number of bad ends = N - subtreeSize[z]
```

---

## 3.6 Bad-pair formula

Start and end choices are independent:

```text
badPairs = subtreeSize[x] * (N - subtreeSize[z])
```

Therefore:

```text
answer = N * (N - 1)
       - subtreeSize[x] * (N - subtreeSize[z])
```

This is the complete mathematical model.

---

## 3.7 Small dry run

Consider:

```text
                         y = 1
                       /       \
                      2         6
                    /   \
                   3     5
                   │
                  x = 4
```

Root = `y = 1`.

Path from `y` to `x`:

```text
1 -> 2 -> 3 -> 4
```

So:

```text
z = 2
```

Subtree sizes:

```text
subtree(x=4) = {4}
size = 1

subtree(z=2) = {2,3,4,5}
size = 4
```

`N = 6`.

Total ordered pairs:

```text
6 * 5 = 30
```

Bad pairs:

```text
1 * (6 - 4)
= 1 * 2
= 2
```

Those destinations are outside subtree(2):

```text
{1, 6}
```

So:

```text
answer = 30 - 2 = 28
```

---

## 3.8 How do we find `z`?

After rooting at `y`, store `parent[]`.

Start at `x` and move upward:

```text
z = x

while parent[z] != y:
    z = parent[z]
```

When the loop stops, `z` is the direct child of `y` on the path to `x`.

Example:

```text
x = 4

4 -> parent 3
3 -> parent 2
parent[2] = 1 = y

z = 2
```

---

## 3.9 Why this solution works

A bad path must look like:

```text
u ... x ... z ... y ... v
```

For that to happen:

1. `u` must lie below `x` when rooted at `y`.
2. `v` must lie outside the whole `z` branch.
3. Trees have unique paths, so there is no alternative route that avoids this order.

Therefore every pair counted by

```text
subtreeSize[x] * (N - subtreeSize[z])
```

is bad, and every bad pair belongs to this product.

---

## 3.10 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

vector<vector<int>> graph;
vector<int> parentNode;
vector<int> subtreeSize;

void dfs(int node, int parent) {
    parentNode[node] = parent;
    subtreeSize[node] = 1;

    for (int next : graph[node]) {
        if (next == parent) {
            continue;
        }

        dfs(next, node);
        subtreeSize[node] += subtreeSize[next];
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, x, y;
    cin >> n >> x >> y;

    graph.assign(n + 1, {});
    parentNode.assign(n + 1, 0);
    subtreeSize.assign(n + 1, 0);

    for (int i = 0; i < n - 1; i++) {
        int u, v;
        cin >> u >> v;
        graph[u].push_back(v);
        graph[v].push_back(u);
    }

    dfs(y, 0);

    int z = x;
    while (parentNode[z] != y) {
        z = parentNode[z];
    }

    ll totalPairs = 1LL * n * (n - 1);
    ll badPairs = 1LL * subtreeSize[x] * (n - subtreeSize[z]);

    cout << totalPairs - badPairs << '\n';

    return 0;
}
```

### Complexity

```text
DFS                O(N)
walk x -> y        O(N) worst case
Total              O(N)
Memory             O(N)
```

---

## 3.11 Recognition pattern

Think of this approach when you see:

```text
Tree
+ special nodes x and y
+ count paths / ordered pairs
+ path must pass through special nodes in an order
```

Ask:

```text
Can I root at one special node?
Can the bad starts become one subtree?
Can the bad ends become the complement of another subtree?
```

Pattern:

```text
TREE PATH COUNTING
      ↓
ROOT SMARTLY
      ↓
SUBTREE + COMPLEMENT
      ↓
MULTIPLY CHOICES
```

---

# 4. Problem 2 — Tree Cutting

**Codeforces 1946C — Tree Cutting**

Problem link:

<https://codeforces.com/problemset/problem/1946/C>

---

## 4.1 What is the problem asking?

We are given a tree with `N` nodes.

We must remove exactly `K` edges.

Removing `K` edges from a tree creates:

```text
K + 1 connected components
```

We want to maximize:

```text
minimum component size
```

In other words:

```text
maximize X
such that every resulting component has at least X nodes
```

---

## 4.2 First important observation

The answer is a number `X`.

Instead of directly constructing the best cuts, ask:

```text
For a fixed X,
can we create at least K + 1 components,
each containing at least X nodes?
```

That becomes a YES/NO problem.

---

## 4.3 Why binary search is possible

Suppose `X = 5` is possible.

Then these are automatically possible:

```text
1, 2, 3, 4
```

because requiring fewer nodes per component is easier.

So:

```text
X:      1 2 3 4 5 6 7 8 ...
check:  T T T T T F F F ...
```

We need:

```text
last True
```

That is Binary Search on Answer.

---

## 4.4 Search bounds

There are `K + 1` final components.

Each needs at least `X` nodes.

Therefore:

```text
(K + 1) * X <= N
```

So:

```text
X <= floor(N / (K + 1))
```

Good search interval:

```text
low = 1
high = N / (K + 1)
```

---

## 4.5 How to implement `check(X)`

Root the tree anywhere, say node `1`.

Process it bottom-up.

For each node, calculate how many currently unassigned nodes its subtree contains.

Example:

```text
                              1
                        ┌─────┴─────┐
                        2           3
                   ┌────┴────┐      │
                   4         5      6
                 /   \              │
                7     8             9
```

Suppose:

```text
X = 3
```

Process leaves upward.

Whenever accumulated subtree size reaches at least `3`, we can make that region one component.

Conceptually:

```text
             4
           /   \
          7     8

size = 3

        CUT HERE
           X
           │
           4
         /   \
        7     8
```

After creating the component, return `0` upward because those nodes have already been consumed.

---

## 4.6 Why greedy cutting works

Suppose a subtree has already accumulated at least `X` nodes.

Keeping those nodes and carrying them farther upward is unnecessary.

We already have enough nodes for a valid component.

Cutting as soon as possible:

```text
creates one valid component
and leaves maximum remaining nodes available to ancestors
```

This maximizes the number of components we can create for the chosen `X`.

---

## 4.7 Postorder recurrence

For each node:

```text
currentSize = 1

for each child:
    currentSize += leftoverFromChild
```

After processing all children:

```text
if currentSize >= X:
    complete one component
    return 0
else:
    return currentSize
```

### Important implementation detail

The lecture slide describes cutting when subtree size is "greater than x".

For the actual problem, every component must contain **at least `X` nodes**, so the implementation condition is:

```text
currentSize >= X
```

not strictly `>`.

---

## 4.8 What about the root?

The root has no parent edge to cut.

The cleanest mental model is:

> Count how many valid groups of size at least `X` the postorder DFS can form, including the final root-side group.

If the greedy DFS can create at least:

```text
K + 1 groups
```

then `X` is feasible.

Why "at least" and not exactly?

If we form more than `K + 1` valid components, we can restore some cut edges and merge adjacent components.

Merging cannot make a component smaller.

So we can reduce the number of components to exactly `K + 1`.

---

## 4.9 Detailed dry run

Tree:

```text
                              1
                        ┌─────┴─────┐
                        2           3
                   ┌────┴────┐      │
                   4         5      6
                 /   \
                7     8
```

Let:

```text
N = 8
K = 1
X = 3
```

Need at least:

```text
K + 1 = 2 components
```

Postorder:

```text
7 -> size 1, return 1
8 -> size 1, return 1

4 -> 1 + 1 + 1 = 3
   -> form component {4,7,8}
   -> components = 1
   -> return 0

5 -> size 1, return 1

2 -> 1 + 0 + 1 = 2
   -> not enough
   -> return 2

6 -> size 1, return 1

3 -> 1 + 1 = 2
   -> return 2

1 -> 1 + 2 + 2 = 5
   -> form another component
   -> components = 2
```

Therefore:

```text
components = 2 >= K + 1
check(3) = true
```

The two regions can be viewed as:

```text
Component A                Component B

     4                         1
   /   \                     /   \
  7     8                   2     3
                             \     \
                              5     6

size = 3                  size = 5
```

---

## 4.10 Binary-search dry run

Suppose:

```text
N = 12
K = 2
```

Upper bound:

```text
12 / 3 = 4
```

Search `[1,4]`:

```text
Iter 1: low=1, high=4 -> mid=2 -> check(2)=true  -> answer=2, low=3
Iter 2: low=3, high=4 -> mid=3 -> check(3)=true  -> answer=3, low=4
Iter 3: low=4, high=4 -> mid=4 -> check(4)=false -> high=3

answer = 3
```

---

## 4.11 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> graph;
int requiredSize;
int componentCount;

int dfs(int node, int parent) {
    int currentSize = 1;

    for (int next : graph[node]) {
        if (next == parent) {
            continue;
        }

        currentSize += dfs(next, node);
    }

    if (currentSize >= requiredSize) {
        componentCount++;
        return 0;
    }

    return currentSize;
}

bool check(int x, int k) {
    requiredSize = x;
    componentCount = 0;

    dfs(1, 0);

    return componentCount >= k + 1;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        int n, k;
        cin >> n >> k;

        graph.assign(n + 1, {});

        for (int i = 0; i < n - 1; i++) {
            int u, v;
            cin >> u >> v;
            graph[u].push_back(v);
            graph[v].push_back(u);
        }

        int low = 1;
        int high = n / (k + 1);
        int answer = 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (check(mid, k)) {
                answer = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        cout << answer << '\n';
    }

    return 0;
}
```

### Complexity

Each `check(X)` performs one DFS:

```text
O(N)
```

Binary search tries `O(log N)` values.

Total:

```text
O(N log N) per test-case family
```

with total `N` bounded by the problem constraints.

Memory:

```text
O(N)
```

---

## 4.12 Recognition pattern

Think of this pattern when you see:

```text
Tree
+ remove/cut edges
+ maximize minimum component property
```

Very common transformation:

```text
MAXIMIZE MINIMUM
      ↓
BINARY SEARCH X
      ↓
CHECK(X)
      ↓
POSTORDER DFS
      ↓
GREEDILY CUT COMPLETED SUBTREES
```

---

# 5. Problem 3 — 13th Labour of Heracles

**Codeforces 1466D — 13th Labour of Heracles**

Problem link:

<https://codeforces.com/problemset/problem/1466/D>

---

## 5.1 Why the original story looks difficult

The statement talks about:

```text
edge colors
colored subgraphs
connected components
maximum component values
```

That makes it look like a complicated graph-coloring problem.

The key is to forget the coloring mechanics and discover what controls how many times each vertex weight can contribute.

That quantity is:

```text
degree of the vertex
```

---

## 5.2 Start from `k = 1`

If every edge has one color, the whole tree is one connected component.

So the first answer is simply:

```text
baseSum = w[1] + w[2] + ... + w[N]
```

Every node contributes once.

---

## 5.3 How many extra times can one node contribute?

Consider a node of degree `d`.

```text
                 a
                 │
            b -- u -- c
                 │
                 d
```

Here:

```text
degree[u] = 4
```

Its weight is already counted once in the base sum.

As the edge coloring is split into more color groups, node `u` can appear repeatedly because multiple incident edges can belong to different useful colored subgraphs.

Maximum total appearances:

```text
degree[u]
```

One appearance already exists in the base sum.

Therefore the number of **extra** contributions is:

```text
degree[u] - 1
```

This is the central formula.

---

## 5.4 Why exactly `degree[u] - 1` extra copies?

Suppose `degree[u] = 1`.

```text
u -- v
```

Leaf `u` has no opportunity for an additional contribution:

```text
extra = 1 - 1 = 0
```

Suppose `degree[u] = 3`.

```text
      a
      │
b --- u --- c
```

Then `u` can contribute at most three times in total.

Since it is already in the initial sum:

```text
extra copies = 3 - 1 = 2
```

---

## 5.5 Total number of extra copies

A tree has:

```text
sum(degree) = 2 * (N - 1)
```

Therefore:

```text
sum(degree[u] - 1)
= 2(N - 1) - N
= N - 2
```

So there are exactly:

```text
N - 2 extra contributions
```

Initial answer count:

```text
1
```

After adding `N - 2` extras:

```text
1 + (N - 2) = N - 1 answers
```

Exactly what the problem asks for: answers for `k = 1 ... N-1`.

This is a useful correctness sanity check.

---

## 5.6 Which extra contribution should we take first?

Each extra copy of node `u` adds:

```text
w[u]
```

to the running answer.

To maximize every prefix answer, always take the largest available weight first.

So build a list:

```text
repeat w[u], degree[u] - 1 times
```

Then sort descending.

---

## 5.7 Visual contribution model

Tree:

```text
                      (1, w=3)
                      /      \
                     /        \
              (2, w=5)      (3, w=4)
                                │
                                │
                             (4, w=6)
```

Degrees:

```text
node 1: degree 2 -> extra copies = 1 -> add weight 3 once
node 2: degree 1 -> extra copies = 0
node 3: degree 2 -> extra copies = 1 -> add weight 4 once
node 4: degree 1 -> extra copies = 0
```

Base:

```text
3 + 5 + 4 + 6 = 18
```

Extra contributions:

```text
[3, 4]
```

Sort descending:

```text
[4, 3]
```

Running answers:

```text
k=1: 18
k=2: 18 + 4 = 22
k=3: 22 + 3 = 25
```

Output:

```text
18 22 25
```

---

## 5.8 Another visualization — why high-degree heavy nodes matter

```text
                           [10]
                         /  |  \
                        /   |   \
                      [2]  [3]  [4]
                             \
                             [1]
```

If node `[10]` has degree `3`:

```text
extra copies of 10 = 2
```

That gives:

```text
+10
+10
```

A heavy high-degree node is therefore very valuable.

The problem becomes:

```text
collect all repeatable weights
sort largest first
```

---

## 5.9 Step-by-step algorithm

### Step 1 — Read weights

Compute:

```text
currentSum = sum(weights)
```

This is answer for `k = 1`.

### Step 2 — Read edges

For every edge `(u,v)`:

```text
degree[u]++
degree[v]++
```

### Step 3 — Build extra contribution list

For every node `u`:

```text
repeat degree[u] - 1 times:
    extras.push_back(weight[u])
```

### Step 4 — Sort descending

```text
largest contribution first
```

### Step 5 — Build answers

```text
print currentSum

for each extraWeight:
    currentSum += extraWeight
    print currentSum
```

---

## 5.10 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        int n;
        cin >> n;

        vector<ll> weight(n + 1);
        vector<int> degree(n + 1, 0);

        ll currentSum = 0;

        for (int i = 1; i <= n; i++) {
            cin >> weight[i];
            currentSum += weight[i];
        }

        for (int i = 0; i < n - 1; i++) {
            int u, v;
            cin >> u >> v;
            degree[u]++;
            degree[v]++;
        }

        vector<ll> extra;

        for (int node = 1; node <= n; node++) {
            for (int count = 1; count < degree[node]; count++) {
                extra.push_back(weight[node]);
            }
        }

        sort(extra.rbegin(), extra.rend());

        cout << currentSum;

        for (ll value : extra) {
            currentSum += value;
            cout << ' ' << currentSum;
        }

        cout << '\n';
    }

    return 0;
}
```

### Complexity

Number of extra elements:

```text
N - 2
```

Therefore:

```text
build degrees     O(N)
build extras      O(N)
sort extras       O(N log N)

Total             O(N log N)
Memory            O(N)
```

---

## 5.11 Recognition pattern

Think of this pattern when you see:

```text
Tree
+ node weights
+ repeated contribution
+ edge operations / coloring / splitting
```

Ask:

```text
How many times can each node participate?
Is that number related to degree?
Can every extra appearance be treated independently?
```

Typical transformation:

```text
COMPLICATED TREE STORY
        ↓
COUNT CONTRIBUTION OF EACH NODE
        ↓
EXTRA COUNT = degree - 1
        ↓
SORT CONTRIBUTIONS GREEDILY
```

---

# 6. Pattern Comparison

| Problem | Hidden Core | Main Tool | Key Formula / Check |
|---|---|---|---|
| Kuro and Walking Route | Count forbidden ordered paths | Rooting + subtree sizes + complement | `bad = sub[x] * (N - sub[z])` |
| Tree Cutting | Maximize minimum component size | Binary search + postorder greedy | `check(X): groups >= K+1` |
| 13th Labour of Heracles | Repeated weighted contribution | Degree counting + sorting | extra copies = `degree[u] - 1` |

---

## How to decode them in a contest

### Problem 1

Story says:

```text
walk from u to v
special x then y is forbidden
```

Decode to:

```text
count paths whose unique route crosses x before y
```

Then:

```text
root at y -> subtree counting
```

---

### Problem 2

Story says:

```text
cut K edges
maximize smallest remaining component
```

Decode to:

```text
maximize minimum
```

Then immediately test:

```text
binary search on answer?
```

Checker becomes:

```text
postorder subtree accumulation
```

---

### Problem 3

Story says:

```text
edge coloring + subgraph values
```

Decode to:

```text
how many times can weight[u] appear?
```

Answer:

```text
degree[u] times total
= degree[u] - 1 additional times
```

Then greedy sorting finishes the problem.

---

# 7. Final Mental Model

Do not memorize these three solutions separately.

Remember these three tree transformations:

```text
1. PATH COUNTING

   special nodes
       ↓
   root strategically
       ↓
   subtree / complement counting
```

```text
2. MAXIMIZE MINIMUM AFTER CUTS

   answer X
      ↓
   binary search
      ↓
   check(X)
      ↓
   postorder greedy
```

```text
3. REPEATED NODE CONTRIBUTION

   complicated edge operation
          ↓
   count participation per node
          ↓
   degree - 1 extra copies
          ↓
   sort greedily
```

The deeper lesson is:

> In tree problems, first decide what information should flow **upward from children**, what part becomes a **subtree**, and whether the problem can be rewritten as **counting, feasibility, or contribution**.

That modelling step is much more valuable than memorizing DFS code.
