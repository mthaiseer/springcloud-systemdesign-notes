# Disjoint Set Union (DSU / Union-Find) — TLE-Style Self-Study Notes

> Based on **TLE — Disjoint Set Union, Graphs Intermediate, Level 4**
>
> This note keeps the lecture’s progression but expands it for self-study:
>
> `Dynamic Connectivity → DSU Forest → find() → union() → Union by Size → Union by Rank → Path Compression → O(α(N)) → Applications → Quantity of Strings`
>
> **Teaching additions:** prerequisite explanations, cleaner ASCII component/tree diagrams, detailed dry runs, recognition rules, and readable C++17 templates.

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [The Dynamic Connectivity Problem](#2-the-dynamic-connectivity-problem)
3. [Why Repeated DFS/BFS Is Too Slow](#3-why-repeated-dfsbfs-is-too-slow)
4. [What DSU Stores](#4-what-dsu-stores)
5. [Why DSU Forms a Forest](#5-why-dsu-forms-a-forest)
6. [Operation 1 — find(x)](#6-operation-1--findx)
7. [Operation 2 — unite(a,b)](#7-operation-2--uniteab)
8. [Naive DSU and Its Problem](#8-naive-dsu-and-its-problem)
9. [Optimization 1 — Union by Size](#9-optimization-1--union-by-size)
10. [Why Union by Size Gives Small Height](#10-why-union-by-size-gives-small-height)
11. [Optimization 2 — Union by Rank](#11-optimization-2--union-by-rank)
12. [Size vs Rank](#12-size-vs-rank)
13. [Optimization 3 — Path Compression](#13-optimization-3--path-compression)
14. [Final Optimized DSU](#14-final-optimized-dsu)
15. [Complexity and Inverse Ackermann](#15-complexity-and-inverse-ackermann)
16. [Dynamic Connectivity — Full Example](#16-dynamic-connectivity--full-example)
17. [Counting Connected Components](#17-counting-connected-components)
18. [Storing Data Per Component](#18-storing-data-per-component)
19. [DSU and Kruskal MST](#19-dsu-and-kruskal-mst)
20. [Problem — Quantity of Strings](#20-problem--quantity-of-strings)
21. [Recognition Patterns](#21-recognition-patterns)
22. [Common Mistakes](#22-common-mistakes)
23. [Final Mental Model](#23-final-mental-model)

---

# 1. Prerequisites

Before DSU, understand these ideas.

---

## 1.1 Graph

A graph contains:

```text
vertices / nodes
+
edges / connections
```

Example:

```text
(1) ---- (2)

(3) ---- (4) ---- (5)
```

Here:

```text
Component A = {1,2}
Component B = {3,4,5}
```

---

## 1.2 Connected Component

Two nodes are in the same connected component if some path exists between them.

Example:

```text
(1)---(2)---(3)      (4)---(5)
```

Queries:

```text
sameComponent(1,3) → YES
sameComponent(2,5) → NO
```

---

## 1.3 Dynamic Graph

A **dynamic connectivity** problem changes the graph over time.

Example operations:

```text
add edge (1,2)
ask if 1 and 2 are connected

add edge (3,4)
add edge (2,3)

ask if 1 and 4 are connected
```

The graph evolves after every union.

---

## 1.4 Tree

A tree is a connected graph with no cycle.

Example:

```text
       (1)
      /   \
    (2)   (3)
          |
         (4)
```

For DSU, we use a tree only as an **internal representation of a component**.

Important:

```text
DSU tree ≠ original graph
```

The original graph may contain many edges and cycles.

DSU stores only enough structure to identify:

```text
which component does this node belong to?
```

---

## 1.5 Root / Representative

Each DSU component has one special root.

Example:

```text
        [2]  ← root / representative
       / | \
     [1][3][4]
```

All these nodes belong to the same set because they eventually reach root `2`.

We can identify the whole component by:

```text
representative = 2
```

---

## 1.6 Parent Array

DSU stores:

```cpp
parent[x]
```

Meaning:

```text
the next node above x in the DSU tree
```

A root points to itself:

```text
parent[root] = root
```

Example:

```text
parent[1] = 2
parent[3] = 2
parent[4] = 2
parent[2] = 2
```

Visual:

```text
1 ─┐
3 ─┼──> 2
4 ─┘     ↑
          └── parent[2] = 2
```

---

# 2. The Dynamic Connectivity Problem

The lecture starts with this task:

```text
N nodes
Q queries

N,Q <= 1e5
```

Two operations:

```text
1. Add an edge between i and j.

2. Ask whether i and j
   belong to the same connected component.
```

This is exactly what DSU is designed for.

---

## 2.1 Example

Start:

```text
1   2   3   4   5
```

All separate.

Operation:

```text
unite(1,2)
```

Now:

```text
1 --- 2      3      4      5
```

Query:

```text
same(1,2)
```

Answer:

```text
YES
```

Then:

```text
unite(3,4)
```

Graph:

```text
1 --- 2      3 --- 4      5
```

Then:

```text
unite(3,2)
```

Graph:

```text
1 --- 2 --- 3 --- 4      5
```

Now:

```text
same(4,1) → YES
same(5,4) → NO
```

---

# 3. Why Repeated DFS/BFS Is Too Slow

A simple idea:

```text
Whenever query asks whether a and b are connected:
run DFS/BFS from a
and see whether b is reachable.
```

This works logically.

But not efficiently for:

```text
N,Q <= 1e5
```

One DFS/BFS can cost roughly:

```text
O(N + M)
```

Doing that for many queries can become far too expensive.

The lecture’s motivation is:

```text
Repeated connectivity query
+
only adding/merging components
        ↓
       DSU
```

---

# 4. What DSU Stores

Initially every node is its own component.

For:

```text
1 2 3 4 5
```

we initialize:

```text
parent[1]=1
parent[2]=2
parent[3]=3
parent[4]=4
parent[5]=5
```

Visual:

```text
[1]   [2]   [3]   [4]   [5]
 ↑     ↑     ↑     ↑     ↑
root  root  root  root  root
```

Each component contains one node.

---

## 4.1 After Merging

Suppose:

```text
unite(1,2)
unite(3,4)
```

A possible DSU forest:

```text
Component A              Component B          Component C

   [1]                       [3]                  [5]
    |                         |
   [2]                       [4]
```

or depending on merge rule:

```text
   [2]                       [4]                  [5]
    |                         |
   [1]                       [3]
```

The exact root does not matter.

What matters:

```text
nodes in same component
→ same root
```

---

# 5. Why DSU Forms a Forest

The lecture asks:

> Why is this a tree structure?

Because DSU only merges **different components**.

Suppose:

```text
rootA != rootB
```

We connect one root under the other.

Example:

Before:

```text
Component A             Component B

     [2]                    [4]
    /   \                    |
  [1]   [3]                 [5]
```

Merge their roots:

```text
parent[4] = 2
```

After:

```text
          [2]
        /  |  \
      [1] [3] [4]
               |
              [5]
```

No cycle is created because the two trees were previously disconnected.

Therefore all DSU components together form a:

```text
FOREST
=
collection of trees
```

---

# 6. Operation 1 — find(x)

Goal:

```text
find(x)
=
return the root / representative
of x's component
```

---

## 6.1 Simple Example

DSU tree:

```text
          [5]
           |
          [2]
         /   \
       [1]   [4]
              |
             [3]
```

Then:

```text
find(3)
```

walks:

```text
3 → 4 → 2 → 5
```

Root:

```text
5
```

Therefore:

```text
find(3)=5
find(1)=5
find(4)=5
```

---

## 6.2 How Do We Know We Reached the Root?

A root satisfies:

```text
parent[x] == x
```

So naive recursive `find`:

```cpp
int find(int x) {
    if (parent[x] == x) {
        return x;
    }

    return find(parent[x]);
}
```

---

## 6.3 Same Component Test

Two nodes are connected exactly when:

```text
find(a) == find(b)
```

Example:

```text
find(1)=5
find(3)=5
```

so:

```text
1 and 3 are in same component
```

But:

```text
find(6)=6
```

so:

```text
find(1) != find(6)
```

They are different components.

---

# 7. Operation 2 — unite(a,b)

Goal:

```text
merge component containing a
with
component containing b
```

Important:

Do **not** directly connect `a` under `b`.

First find their component roots.

```text
rootA = find(a)
rootB = find(b)
```

If:

```text
rootA == rootB
```

they are already in the same component.

Nothing to do.

Otherwise connect one root under the other.

---

## 7.1 Basic Union

Before:

```text
Component A           Component B

      [2]                  [5]
     /   \                  |
   [1]   [3]               [4]
```

Call:

```text
unite(1,4)
```

First:

```text
find(1)=2
find(4)=5
```

We merge roots:

```text
parent[5]=2
```

After:

```text
           [2]
         /  |  \
       [1] [3] [5]
                |
               [4]
```

Now:

```text
find(1)=2
find(4)=2
```

Connected.

---

# 8. Naive DSU and Its Problem

Naive DSU:

```cpp
class DSU {
    vector<int> parent;

public:
    DSU(int n) {
        parent.resize(n + 1);

        for (int i = 1; i <= n; i++) {
            parent[i] = i;
        }
    }

    int find(int x) {
        if (parent[x] == x) {
            return x;
        }

        return find(parent[x]);
    }

    void unite(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);

        if (rootA == rootB) {
            return;
        }

        parent[rootB] = rootA;
    }
};
```

Correct?

```text
YES
```

Fast enough in worst case?

```text
NO
```

---

## 8.1 Bad Chain

Bad sequence of unions can produce:

```text
[1]
 |
[2]
 |
[3]
 |
[4]
 |
[5]
 |
[6]
```

Now:

```text
find(6)
```

must walk:

```text
6 → 5 → 4 → 3 → 2 → 1
```

Cost:

```text
O(N)
```

The lecture identifies this as the problem with naive DSU.

---

# 9. Optimization 1 — Union by Size

Idea:

```text
Always attach the smaller component
under the larger component.
```

Why?

Because we do not want a large tree to become deeper unnecessarily.

---

## 9.1 Store Component Size

Initially:

```text
size[1]=1
size[2]=1
...
size[n]=1
```

Important:

```text
size[root]
```

is meaningful for component roots.

---

## 9.2 Example

Component A:

```text
size = 5

       [A]
      / | \
    ...
```

Component B:

```text
size = 2

       [B]
        |
       ...
```

Do:

```text
unite(A,B)
```

Since:

```text
5 > 2
```

attach:

```text
B under A
```

Result:

```text
           [A] size=7
          / | \
        ... [B]
             |
            ...
```

Not the opposite.

---

## 9.3 Step-by-Step Union by Size

```text
1. rootA = find(a)
2. rootB = find(b)

3. If same root:
       return

4. Ensure rootA is the larger tree.
   If size[rootA] < size[rootB]:
       swap(rootA,rootB)

5. Attach rootB below rootA:
       parent[rootB] = rootA

6. Update:
       size[rootA] += size[rootB]
```

---

## 9.4 C++17

```cpp
bool unite(int a, int b) {
    int rootA = find(a);
    int rootB = find(b);

    if (rootA == rootB) {
        return false;
    }

    if (size[rootA] < size[rootB]) {
        swap(rootA, rootB);
    }

    parent[rootB] = rootA;
    size[rootA] += size[rootB];

    return true;
}
```

---

## 9.5 Compact Dry Run

Start:

```text
{1} {2} {3} {4} {5}
```

All sizes:

```text
1 1 1 1 1
```

### `unite(1,2)`

Equal sizes.

Choose root `1`.

```text
[1]
 |
[2]
```

```text
size[1]=2
```

### `unite(3,4)`

```text
[3]
 |
[4]
```

```text
size[3]=2
```

### `unite(1,3)`

Both component sizes are `2`.

Attach root `3` under `1`.

```text
          [1] size=4
         /   \
       [2]   [3]
              |
             [4]
```

### `unite(5,4)`

```text
find(5)=5
find(4)=1
```

Sizes:

```text
size[5]=1
size[1]=4
```

So smaller root `5` goes under `1`.

```text
          [1] size=5
       /   |   \
     [2]  [3]  [5]
           |
          [4]
```

---

# 10. Why Union by Size Gives Small Height

The lecture uses the **doubling argument**.

Suppose a node becomes one level deeper.

That happens only because its component was the smaller component being attached to another component of at least equal size.

If its component size was:

```text
x
```

after merge its new component size is at least:

```text
2x
```

For the same node to become deeper again:

```text
2x → at least 4x
```

Again:

```text
4x → at least 8x
```

So component size grows like:

```text
x
2x
4x
8x
16x
...
```

After `h` such increases:

```text
size >= 2^h
```

But component size can never exceed:

```text
N
```

Therefore:

```text
2^h <= N
```

Taking log:

```text
h <= log2(N)
```

So union by size keeps tree height bounded by roughly:

```text
O(log N)
```

This is the lecture’s key proof.

---

# 11. Optimization 2 — Union by Rank

Union by rank follows a similar idea.

Instead of storing exact component size, store a value representing tree height / rank.

Initially:

```text
rank[x] = 0
```

for each single node.

---

## 11.1 Rule

If:

```text
rank[rootA] < rank[rootB]
```

attach:

```text
A under B
```

If:

```text
rank[rootA] > rank[rootB]
```

attach:

```text
B under A
```

If ranks are equal:

```text
choose one as root
and increase its rank by 1
```

---

## 11.2 Example

Before:

```text
Tree A             Tree B

   [A] rank=2         [B] rank=1
  /   \                |
 ...  ...              ...
```

Attach smaller rank under larger:

```text
        [A] rank=2
       / | \
     ... ... [B]
              |
             ...
```

Rank of `A` does not need to increase.

---

## 11.3 Equal Rank Case

Before:

```text
[A] rank=1       [B] rank=1
 |                |
...              ...
```

Attach `B` under `A`:

```text
       [A]
      /   \
    ...   [B]
           |
          ...
```

Now tree height may increase by one.

So:

```text
rank[A]++
```

---

## 11.4 C++17 Union by Rank

```cpp
bool unite(int a, int b) {
    int rootA = find(a);
    int rootB = find(b);

    if (rootA == rootB) {
        return false;
    }

    if (rank[rootA] < rank[rootB]) {
        swap(rootA, rootB);
    }

    parent[rootB] = rootA;

    if (rank[rootA] == rank[rootB]) {
        rank[rootA]++;
    }

    return true;
}
```

---

# 12. Size vs Rank

Both are optimization strategies for deciding:

```text
which root should become parent?
```

### Union by Size

Store:

```text
number of nodes in component
```

Rule:

```text
smaller size → larger size
```

### Union by Rank

Store:

```text
approximate tree height/rank
```

Rule:

```text
smaller rank → larger rank
```

For competitive programming:

```text
Union by Size + Path Compression
```

is often the easiest because component size is also useful in problems.

Use one strategy.

You do not need both simultaneously.

---

# 13. Optimization 3 — Path Compression

Even with balanced unions, `find(x)` may walk through multiple ancestors.

Path compression makes future `find` operations faster.

The lecture describes it as:

```text
"memoising" find()
```

---

## 13.1 Before Compression

Suppose:

```text
[1] root
 |
[2]
 |
[3]
 |
[4]
 |
[5]
```

Call:

```text
find(5)
```

Path:

```text
5 → 4 → 3 → 2 → 1
```

We discover:

```text
root = 1
```

Instead of leaving the chain unchanged, make all visited nodes point directly to root.

---

## 13.2 After Compression

```text
          [1]
       / / | \
     [2][3][4][5]
```

Now:

```text
parent[2]=1
parent[3]=1
parent[4]=1
parent[5]=1
```

Future finds are much faster.

---

## 13.3 The Important Line

Without compression:

```cpp
return find(parent[x]);
```

With compression:

```cpp
return parent[x] = find(parent[x]);
```

The assignment:

```text
parent[x] =
```

is the optimization.

It rewires the path.

---

## 13.4 Step-by-Step Recursion

Before:

```text
parent[5]=4
parent[4]=3
parent[3]=2
parent[2]=1
parent[1]=1
```

Call:

```text
find(5)
```

Recursive calls:

```text
find(5)
→ find(4)
→ find(3)
→ find(2)
→ find(1)
```

`find(1)` returns:

```text
1
```

While recursion returns:

```text
parent[2]=1
parent[3]=1
parent[4]=1
parent[5]=1
```

Final:

```text
          [1]
       / / | \
     [2][3][4][5]
```

---

# 14. Final Optimized DSU

The lecture combines:

```text
Union by Size / Rank
+
Path Compression
```

Below is a clean reusable union-by-size template.

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

    bool same(int a, int b) {
        return find(a) == find(b);
    }

    int size(int x) {
        return componentSize[find(x)];
    }
};
```

---

# 15. Complexity and Inverse Ackermann

The lecture first motivates union by size/rank as keeping DSU trees shallow, and then combines it with path compression.

With:

```text
Union by Size or Rank
+
Path Compression
```

the amortized time per operation is:

```text
O(α(N))
```

where:

```text
α(N)
=
inverse Ackermann function
```

For practical input sizes it grows unbelievably slowly.

So in competitive programming:

```text
O(α(N)) ≈ almost constant time
```

Do not treat it mathematically as exactly `O(1)`, but practically it is extremely fast.

For `Q` operations:

```text
O(Q α(N))
```

plus initialization:

```text
O(N)
```

---

# 16. Dynamic Connectivity — Full Example

Let:

```text
N = 6
```

Initial DSU:

```text
[1] [2] [3] [4] [5] [6]
```

---

## Query 1

```text
unite(1,2)
```

Result:

```text
[1]       [3] [4] [5] [6]
 |
[2]
```

---

## Query 2

```text
same(1,2)
```

```text
find(1)=1
find(2)=1
```

Answer:

```text
YES
```

---

## Query 3

```text
unite(3,4)
```

```text
[1]       [3]       [5] [6]
 |         |
[2]       [4]
```

---

## Query 4

```text
unite(3,2)
```

Roots:

```text
find(3)=3
find(2)=1
```

Both components have size `2`.

Merge:

```text
          [3]
         /   \
       [4]   [1]
              |
             [2]

[5] [6]
```

---

## Query 5

```text
same(4,1)
```

```text
find(4)=3
find(1)=3
```

Answer:

```text
YES
```

---

## Query 6

```text
same(5,4)
```

```text
find(5)=5
find(4)=3
```

Answer:

```text
NO
```

This is the lecture’s core use case.

---

## 16.1 Full C++17 Dynamic Connectivity

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

    bool same(int a, int b) {
        return find(a) == find(b);
    }
};

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, q;
    cin >> n >> q;

    DSU dsu(n);

    while (q--) {
        int type, a, b;
        cin >> type >> a >> b;

        if (type == 1) {
            dsu.unite(a, b);
        } else {
            cout << (dsu.same(a, b) ? "YES\n" : "NO\n");
        }
    }

    return 0;
}
```

---

# 17. Counting Connected Components

This is one of the lecture’s listed DSU applications.

Initially:

```text
N separate nodes
```

so:

```text
components = N
```

Whenever:

```text
unite(a,b)
```

actually merges two different components:

```text
components--
```

If they were already connected:

```text
do not decrease
```

---

## 17.1 Example

Start:

```text
N=5
components=5
```

`unite(1,2)`:

```text
components=4
```

`unite(3,4)`:

```text
components=3
```

`unite(2,3)`:

```text
components=2
```

`unite(1,4)`:

They are already connected.

```text
components stays 2
```

---

## 17.2 Code Pattern

```cpp
int components = n;

if (dsu.unite(a, b)) {
    components--;
}
```

This is why returning `bool` from `unite()` is useful.

---

# 18. Storing Data Per Component

The lecture lists a deeper DSU idea:

```text
store information about each connected component
```

Possible metadata includes:

```text
size
sum
minimum
maximum
gcd
bitwise AND
bitwise OR
bitwise XOR
```

The important rule:

```text
store component data at the root
```

When two roots merge, combine their data.

---

## 18.1 Component Sum Example

Before:

```text
Component A:
root = 2
sum = 17

Component B:
root = 6
sum = 9
```

After merging `B` into `A`:

```text
sum[2] = 17 + 9 = 26
```

---

## 18.2 Component Minimum

```text
minimum[newRoot]
=
min(minimum[rootA], minimum[rootB])
```

---

## 18.3 Component Maximum

```text
maximum[newRoot]
=
max(maximum[rootA], maximum[rootB])
```

---

## 18.4 Component GCD

```text
gcdValue[newRoot]
=
gcd(gcdValue[rootA], gcdValue[rootB])
```

This works naturally because component merging combines two disjoint groups.

---

## 18.5 General Pattern

If component property can be combined:

```text
data(A ∪ B)
=
combine(data(A), data(B))
```

then DSU can often maintain it.

Examples:

```text
size:
x + y

sum:
x + y

minimum:
min(x,y)

maximum:
max(x,y)

gcd:
gcd(x,y)

AND:
x & y

OR:
x | y

XOR:
x ^ y
```

---

# 19. DSU and Kruskal MST

The lecture explicitly lists:

```text
DSU in Kruskal's algorithm
```

Why?

Kruskal processes edges from smallest weight to largest.

For each edge:

```text
u --w-- v
```

ask:

```text
Are u and v already connected?
```

If:

```text
YES
```

adding the edge would create a cycle.

Skip it.

If:

```text
NO
```

take the edge and:

```text
unite(u,v)
```

So Kruskal needs exactly the two operations DSU provides:

```text
same component?
merge components
```

Mental model:

```text
Kruskal
   ↓
sort edges
   ↓
for each edge
   ↓
find(u) == find(v)?
   ├─ YES → cycle → skip
   └─ NO  → take edge → unite(u,v)
```

---

# 20. Problem — Quantity of Strings

The lecture’s problem-solving example is:

```text
Codeforces 151D — Quantity of Strings
```

Core condition shown in the lecture:

```text
string length = N
alphabet size = M
every substring of length K must be a palindrome
```

We need count how many valid strings exist.

The lecture models equality constraints between string positions using DSU.

---

## 20.1 Prerequisite — Palindrome

A string is a palindrome if it reads the same from both directions.

Example:

```text
abcba
```

Constraints:

```text
position 1 = position 5
position 2 = position 4
```

The center may stand alone.

For a length-4 substring:

```text
a b b a
```

constraints:

```text
1 = 4
2 = 3
```

---

## 20.2 One Palindromic Window Creates Equality Constraints

Suppose:

```text
K=5
```

and a window covers positions:

```text
i, i+1, i+2, i+3, i+4
```

For it to be a palindrome:

```text
i     = i+4
i+1   = i+3
```

Middle:

```text
i+2
```

does not need a partner.

Using DSU:

```text
unite(i, i+4)
unite(i+1, i+3)
```

---

## 20.3 Why DSU Fits This Problem

Suppose constraints say:

```text
position 1 = position 5
position 5 = position 8
```

Then automatically:

```text
position 1 = position 8
```

This is exactly an equivalence relation / connected-component idea.

DSU groups all positions that must contain the same character.

Example:

```text
{1,5,8}
```

is one component.

All three positions must choose one common character.

---

## 20.4 Better Visual Model

Suppose:

```text
N=7
K=3
```

Positions:

```text
 1   2   3   4   5   6   7
[ ] [ ] [ ] [ ] [ ] [ ] [ ]
```

Every length-3 window must be palindrome.

### Window `[1..3]`

```text
1 2 3
|   |
+---+

constraint:
1 = 3
```

### Window `[2..4]`

```text
2 3 4
|   |
+---+

constraint:
2 = 4
```

### Window `[3..5]`

```text
3 4 5
|   |
+---+

constraint:
3 = 5
```

Continue similarly.

DSU starts merging positions forced equal.

---

## 20.5 General Window Algorithm

For every starting position:

```text
left = start
right = start + K - 1
```

While:

```text
left < right
```

do:

```text
unite(left,right)
left++
right--
```

This enforces the palindrome condition for that window.

---

## 20.6 Why Number of Components Determines the Answer

After all equality constraints are added, suppose DSU has:

```text
C connected components
```

Every component must use one single character.

But different components are independent.

If alphabet size is:

```text
M
```

then each component has:

```text
M choices
```

So total number of strings:

```text
M × M × ... × M
```

`C` times.

Therefore:

```text
answer = M^C
```

The lecture’s key transformation is:

```text
string constraints
      ↓
equality constraints between positions
      ↓
DSU components
      ↓
one character choice per component
      ↓
M^(number of components)
```

---

## 20.7 Tiny Example

Suppose after all unions:

```text
Component 1 = {1,3,5}
Component 2 = {2,4,6}
```

So:

```text
C=2
```

If:

```text
M=3
```

each component can choose any of 3 characters.

Choices:

```text
Component 1 → 3 choices
Component 2 → 3 choices
```

Total:

```text
3 × 3 = 9
```

Which equals:

```text
3^2
```

---

## 20.8 Step-by-Step Algorithm

```text
1. Create DSU for positions 0..N-1.

2. For every substring of length K:
       left = start
       right = start+K-1

3. While left < right:
       unite(left,right)
       left++
       right--

4. Count DSU roots:
       components = number of i where find(i)==i

5. Answer:
       M^components mod 1e9+7
```

The lecture implementation uses the direct nested-window approach; with the shown constraints around `N,M,K <= 2000`, this is acceptable as a teaching solution.

---

## 20.9 Clean C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll MOD = 1000000007LL;

class DSU {
private:
    vector<int> parent;
    vector<int> componentSize;

public:
    DSU(int n) {
        parent.resize(n);
        componentSize.assign(n, 1);

        iota(parent.begin(), parent.end(), 0);
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

ll binaryPower(ll base, int exponent) {
    ll result = 1;

    while (exponent > 0) {
        if (exponent & 1) {
            result = result * base % MOD;
        }

        base = base * base % MOD;
        exponent >>= 1;
    }

    return result;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m, k;
    cin >> n >> m >> k;

    DSU dsu(n);

    for (int start = 0; start + k <= n; start++) {
        int left = start;
        int right = start + k - 1;

        while (left < right) {
            dsu.unite(left, right);
            left++;
            right--;
        }
    }

    int components = 0;

    for (int i = 0; i < n; i++) {
        if (dsu.find(i) == i) {
            components++;
        }
    }

    cout << binaryPower(m, components) << '\n';

    return 0;
}
```

---

## 20.10 Recognition Lesson from This Problem

This problem does not look like a graph problem at first.

It talks about:

```text
strings
palindromes
positions
characters
```

But the hidden structure is:

```text
position x must equal position y
```

Repeated equality constraints create groups.

That should trigger:

```text
DSU
```

This is one of the strongest DSU recognition patterns.

---

# 21. Recognition Patterns

Think DSU when the problem repeatedly asks about **groups that only merge**.

---

## Pattern 1 — Dynamic Connectivity

```text
add connection
ask whether x and y are connected
```

Use:

```text
unite(x,y)
find(x)==find(y)
```

---

## Pattern 2 — Count Groups

```text
How many connected groups remain?
```

Use:

```text
components=N
successful union → components--
```

---

## Pattern 3 — Component Size

```text
How many nodes belong to x's group?
```

Use:

```text
componentSize[find(x)]
```

---

## Pattern 4 — Component Aggregate

```text
sum/min/max/gcd/etc. of each group
```

Store metadata at root and combine on union.

---

## Pattern 5 — Equality Constraints

Statements such as:

```text
x must equal y
x and y belong together
these positions must have same value
```

can often be modeled as:

```text
unite(x,y)
```

---

## Pattern 6 — Kruskal / Cycle Detection

Processing edges:

```text
if find(u)==find(v):
    edge creates cycle
else:
    unite(u,v)
```

---

# 22. Common Mistakes

## Mistake 1 — Merge `a` and `b` directly

Wrong:

```cpp
parent[b] = a;
```

Correct concept:

```text
merge ROOTS
```

```cpp
int rootA = find(a);
int rootB = find(b);
```

---

## Mistake 2 — Forget Same-Component Check

Before merging:

```cpp
if (rootA == rootB) {
    return false;
}
```

Otherwise size/rank/component count can become wrong.

---

## Mistake 3 — Use Size of Non-Root

Do not assume:

```text
componentSize[x]
```

is valid for arbitrary `x`.

Use:

```text
componentSize[find(x)]
```

---

## Mistake 4 — Forget Path Compression Assignment

This:

```cpp
return find(parent[x]);
```

finds the root but does not compress.

This:

```cpp
return parent[x] = find(parent[x]);
```

compresses.

---

## Mistake 5 — Increase Rank Every Time

Rank increases only when equal-rank trees are merged.

---

## Mistake 6 — Decrease Component Count on Failed Union

Only do:

```text
components--
```

when roots were different.

---

## Mistake 7 — Use DSU for Deletions Without Extra Technique

Basic DSU naturally supports:

```text
merge / add connectivity
```

It does not directly support arbitrary:

```text
split component
delete edge
```

Those need other/offline techniques.

---

# 23. Final Mental Model

## 23.1 What DSU Really Does

```text
Many objects
   ↓
partitioned into groups
   ↓
groups only MERGE
   ↓
need fast:
    "which group?"
    "same group?"
    "merge groups?"
```

That is DSU.

---

## 23.2 Internal Representation

```text
Original graph/component

1---2---3
 \     /
   ---4
```

DSU does **not** need to store all those edges.

It may represent the entire component simply as:

```text
       [2]
     /  |  \
   [1] [3] [4]
```

One root identifies the whole group.

---

## 23.3 Three Core Ideas

### `find(x)`

```text
Which root represents x?
```

### `unite(a,b)`

```text
Merge the two roots.
```

### `same(a,b)`

```text
find(a)==find(b)?
```

---

## 23.4 Performance Evolution

```text
Naive parent tree
      ↓
can become a chain
      ↓
O(N) find

Union by Size / Rank
      ↓
keep tree shallow
      ↓
roughly O(log N) height

+ Path Compression
      ↓
flatten frequently used paths
      ↓
O(α(N)) amortized per operation
≈ almost constant in practice
```

---

# One-Line Revision

```text
DSU = fast merging + fast representative lookup.

parent[root] = root

find(x)
→ root of x's component

same(a,b)
→ find(a)==find(b)

unite(a,b)
→ merge their roots

Union by Size
→ smaller component under larger

Union by Rank
→ smaller rank under larger rank

Path Compression
→ every visited node points directly to root

Both optimizations
→ O(α(N)) amortized

Connected component count
→ start N, decrement on successful union

Component metadata
→ store at root and combine during union

Kruskal
→ DSU prevents cycles

Equality constraints
→ positions forced equal belong to one DSU component
```

---

# Final Recognition Question

Whenever you see a problem, ask:

```text
"Are many items being grouped together,
and once two groups join, do they stay joined?"
```

If yes, strongly consider:

```text
DSU / Union-Find
```

Then ask:

```text
What should each component store?
```

Possible answers:

```text
only representative
size
number of components
sum
min/max
gcd
bitwise value
or another mergeable property
```

That second question is where most advanced DSU problems begin.
