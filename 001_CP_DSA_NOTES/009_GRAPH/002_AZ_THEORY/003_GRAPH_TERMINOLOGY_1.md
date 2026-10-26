# Graph Terminology — Foundations for Graph Algorithms

> Self-study notes built from the supplied AlgoZenith lecture screenshots, with prerequisite explanations and clean ASCII graph diagrams added for clarity.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [What Is a Graph?](#2-what-is-a-graph)
3. [Vertices and Edges](#3-vertices-and-edges)
4. [Formal Graph Notation — G(V, E)](#4-formal-graph-notation--gv-e)
5. [Weighted vs Unweighted Graph](#5-weighted-vs-unweighted-graph)
6. [Directed vs Undirected Graph](#6-directed-vs-undirected-graph)
7. [Labelled vs Unlabelled Graph](#7-labelled-vs-unlabelled-graph)
8. [Sparse vs Dense Graph](#8-sparse-vs-dense-graph)
9. [Degree in an Undirected Graph](#9-degree-in-an-undirected-graph)
10. [Handshaking Lemma](#10-handshaking-lemma)
11. [In-Degree and Out-Degree in a Directed Graph](#11-in-degree-and-out-degree-in-a-directed-graph)
12. [Directed Degree Identities](#12-directed-degree-identities)
13. [How to Read a Graph Statement Quickly](#13-how-to-read-a-graph-statement-quickly)
14. [Compact Revision Table](#14-compact-revision-table)
15. [Final Mental Model](#15-final-mental-model)

---

# 1. Prerequisites

Before learning BFS, DFS, shortest paths, trees, SCC, MST, and other graph algorithms, we need a precise language for describing a graph.

The minimum prerequisites are:

- sets,
- ordered and unordered pairs,
- counting,
- basic summation notation,
- understanding that a graph is a model of relationships.

---

## 1.1 Sets

A **set** is a collection of distinct objects.

Example:

```text
V = {1, 2, 3, 4}
```

Here `V` contains four elements.

So:

```text
|V| = 4
```

The notation `|V|` means:

```text
number of elements in set V
```

This becomes important because in graphs:

```text
|V| = number of vertices
|E| = number of edges
```

---

## 1.2 Pair of vertices

An edge connects two vertices.

For an undirected graph:

```text
(1, 2)
```

means that vertex `1` and vertex `2` are connected.

Visually:

```text
(1) ----- (2)
```

For a directed graph:

```text
(1, 2)
```

usually means:

```text
1 → 2
```

Visually:

```text
(1) -----> (2)
```

Therefore, direction changes the meaning of the pair.

---

## 1.3 Why graphs appear in problems

Graphs are useful whenever objects have relationships.

Examples:

```text
Cities       → roads
People       → friendships
Computers    → network links
Courses      → prerequisites
Web pages    → hyperlinks
States       → possible transitions
```

The important abstraction is:

```text
OBJECTS       → VERTICES
RELATIONSHIPS → EDGES
```

---

# 2. What Is a Graph?

A graph is a collection of:

```text
vertices / nodes
+
edges / connections
```

ASCII picture:

```text
        (2)
       /   \
     (1)   (4)
       \   /
        (3)
```

The circles are **vertices**.

The connecting lines are **edges**.

---

## 2.1 Graph as a mathematical model

Suppose:

```text
V = {1, 2, 3, 4}
```

and the edges are:

```text
E = {
    (1, 2),
    (1, 3),
    (2, 3),
    (3, 4)
}
```

Diagram:

```text
(1) ----- (2)
  \       /
   \     /
     (3) ----- (4)
```

This entire object is a graph.

We write:

```text
G = (V, E)
```

---

# 3. Vertices and Edges

## 3.1 Vertex

A **vertex** is one object in the graph.

Other common names:

```text
vertex
node
```

For example:

```text
V = {1, 2, 3, 4}
```

means the graph has four vertices.

```text
|V| = 4
```

---

## 3.2 Edge

An **edge** represents a relationship between two vertices.

Example:

```text
(1, 2)
```

Diagram:

```text
(1) ----- (2)
```

Another graph:

```text
        (2)
       /   \
     (1)   (4)
       \   /
        (3)
```

Possible edge set:

```text
E = {
    (1, 2),
    (1, 3),
    (2, 4),
    (3, 4)
}
```

Then:

```text
|E| = 4
```

---

# 4. Formal Graph Notation — G(V, E)

A graph is commonly written as:

```text
G(V, E)
```

or:

```text
G = (V, E)
```

where:

```text
V = set of vertices
E = set of edges
```

Example:

```text
V = {1, 2, 3, 4}

E = {
    (1, 2),
    (2, 3),
    (3, 1),
    (3, 4)
}
```

Diagram:

```text
        (2)
       /   \
     (1)---(3)----(4)
```

Therefore:

```text
|V| = 4
|E| = 4
```

---

## 4.1 Common variable names in problems

Competitive-programming problems usually use:

```text
N = number of vertices
M = number of edges
```

So often:

```text
N = |V|
M = |E|
```

Example:

```text
N = 4
M = 4
```

---

# 5. Weighted vs Unweighted Graph

One major classification is based on **edge weight**.

```text
Graph
├── Unweighted
└── Weighted
```

---

## 5.1 Unweighted graph

An unweighted edge simply tells us:

```text
a connection exists
```

Example:

```text
(1) ----- (2)
```

No cost/distance/value is written on the edge.

Graph:

```text
        (2)
       /   \
     (1)   (4)
       \   /
        (3)
```

Typical interpretation:

```text
1 is connected to 2
1 is connected to 3
...
```

Every edge is treated equally unless the problem says otherwise.

---

## 5.2 Weighted graph

A weighted graph stores a value on each edge.

Example:

```text
        5
(1) -------- (2)
              \
             3 \
                (4)
```

The numbers may represent:

```text
distance
cost
time
price
capacity
risk
score
```

Example edge:

```text
1 ----5---- 2
```

means:

```text
weight(1, 2) = 5
```

---

## 5.3 Tiny comparison

Unweighted:

```text
(1) ----- (2)
```

Weighted:

```text
      7
(1) ----- (2)
```

The structure is the same.

Only the edge carries extra information.

---

# 6. Directed vs Undirected Graph

Another major classification is based on **edge direction**.

```text
Graph
├── Undirected
└── Directed
```

---

## 6.1 Undirected graph

An undirected edge works in both directions.

```text
(1) ----- (2)
```

Conceptually:

```text
1 ↔ 2
```

If `1` is connected to `2`, then `2` is also connected to `1`.

Examples:

```text
friendship
two-way road
physical cable
```

---

## 6.2 Directed graph

A directed graph has arrows.

```text
(1) -----> (2)
```

This means:

```text
1 can go to 2
```

but it does **not automatically mean**:

```text
2 can go to 1
```

Example:

```text
(1) -----> (2)
             |
             v
            (3)
```

Edges:

```text
1 → 2
2 → 3
```

From `1`, we can reach `3`.

But from `3`, there may be no path back to `1`.

---

## 6.3 Directed and weighted can exist together

These classifications are independent.

A graph can be:

```text
directed + weighted
directed + unweighted
undirected + weighted
undirected + unweighted
```

Example:

```text
        5
(1) --------> (2)
               |
             3 |
               v
              (3)
```

This graph is:

```text
directed
weighted
```

---

# 7. Labelled vs Unlabelled Graph

The lecture also distinguishes graphs based on whether the vertices are explicitly identified.

---

## 7.1 Labelled graph

Vertices have labels.

Example:

```text
        (2)
       /   \
     (1)---(4)
       \   /
        (3)
```

The identity of every vertex matters:

```text
1, 2, 3, 4
```

---

## 7.2 Same shape, different labels

Consider:

```text
Graph A

        (2)
       /   \
     (1)   (4)
       \   /
        (3)
```

and:

```text
Graph B

        (7)
       /   \
     (5)   (9)
       \   /
        (8)
```

Structurally they may have the same shape even though labels differ.

This idea is useful when distinguishing:

```text
graph structure
vs
vertex identity
```

For most competitive-programming problems, vertices are explicitly labelled, commonly:

```text
0 ... N-1
```

or:

```text
1 ... N
```

---

# 8. Sparse vs Dense Graph

This classification compares:

```text
number of vertices N
number of edges M
```

---

## 8.1 Maximum number of edges

For a simple undirected graph with `N` vertices:

```text
maximum edges = N(N - 1) / 2
```

Why?

Every pair of different vertices can have at most one edge.

Example for `N = 4`:

```text
maximum edges
= 4 × 3 / 2
= 6
```

A fully connected graph:

```text
      (1)------(2)
       |\      /|
       | \    / |
       |  \  /  |
       |   \/   |
       |   /\   |
       |  /  \  |
       | /    \ |
      (3)------(4)
```

Every vertex connects to every other vertex.

---

## 8.2 Sparse graph

A sparse graph has relatively few edges.

Example:

```text
(1) ----- (2) ----- (3) ----- (4)
```

Here:

```text
N = 4
M = 3
```

Only a small fraction of all possible edges exists.

In competitive programming, when:

```text
M is around O(N)
```

the graph is usually considered sparse.

---

## 8.3 Dense graph

A dense graph has many edges.

Example:

```text
      (1)------(2)
       |\      /|
       | \    / |
       |  \  /  |
       |   \/   |
       |   /\   |
       |  /  \  |
       | /    \ |
      (3)------(4)
```

For a simple undirected graph, maximum density approaches:

```text
M ≈ N(N - 1) / 2
```

which is:

```text
O(N²)
```

---

## 8.4 Why sparse vs dense matters

It affects both:

```text
algorithm choice
data structure choice
```

Mental model:

```text
few edges              many edges

Sparse ---------------------------- Dense
M ≈ O(N)                           M ≈ O(N²)
```

Many real competitive-programming graph problems are sparse, so adjacency lists are commonly preferred.

---

# 9. Degree in an Undirected Graph

For an undirected graph:

```text
degree(v)
```

means:

```text
number of edges touching vertex v
```

Example:

```text
        (2)
       / | \
     (1) | (4)
         |
        (3)
```

Edges:

```text
(1,2)
(2,3)
(2,4)
```

Then:

```text
deg(1) = 1
deg(2) = 3
deg(3) = 1
deg(4) = 1
```

---

## 9.1 Another example

```text
(1) ----- (2)
           |
           |
          (3) ----- (4)
```

Degrees:

```text
deg(1) = 1
deg(2) = 2
deg(3) = 2
deg(4) = 1
```

---

# 10. Handshaking Lemma

For an undirected graph:

```text
Σ deg(v) = 2|E|
```

This is one of the most important basic graph identities.

---

## 10.1 Why does every edge contribute 2?

Consider one edge:

```text
(1) ----- (2)
```

This edge contributes:

```text
+1 to deg(1)
+1 to deg(2)
```

Therefore one edge contributes:

```text
2
```

to the total degree sum.

If there are `|E|` edges:

```text
total degree contribution = 2|E|
```

Hence:

```text
Σ deg(v) = 2|E|
```

---

## 10.2 Dry run

Graph:

```text
        (2)
       / | \
     (1) | (4)
         |
        (3)
```

Degrees:

```text
deg(1) = 1
deg(2) = 3
deg(3) = 1
deg(4) = 1
```

Sum:

```text
Σ deg(v)
= 1 + 3 + 1 + 1
= 6
```

Number of edges:

```text
|E| = 3
```

Therefore:

```text
2|E|
= 2 × 3
= 6
```

So:

```text
Σ deg(v) = 2|E|
```

Verified.

---

# 11. In-Degree and Out-Degree in a Directed Graph

For directed graphs, ordinary degree splits into two quantities:

```text
in-degree
out-degree
```

---

## 11.1 In-degree

```text
inDegree(v)
```

is the number of edges entering vertex `v`.

Example:

```text
(1) -----> (2) <----- (3)
```

For vertex `2`:

```text
inDegree(2) = 2
```

---

## 11.2 Out-degree

```text
outDegree(v)
```

is the number of edges leaving vertex `v`.

Example:

```text
          (2)
         /
        v
(1) --------> (3)
        \
         v
          (4)
```

More clearly:

```text
      (2)
       ^
       |
(1) --> (3)

and

(1) --> (4)
```

If vertex `1` has three outgoing arrows:

```text
outDegree(1) = 3
```

---

## 11.3 Step-by-step example

Graph:

```text
(1) -----> (2) -----> (3)
            |
            v
           (4)
```

Edges:

```text
1 → 2
2 → 3
2 → 4
```

Now calculate.

Vertex `1`:

```text
inDegree(1)  = 0
outDegree(1) = 1
```

Vertex `2`:

```text
inDegree(2)  = 1
outDegree(2) = 2
```

Vertex `3`:

```text
inDegree(3)  = 1
outDegree(3) = 0
```

Vertex `4`:

```text
inDegree(4)  = 1
outDegree(4) = 0
```

---

# 12. Directed Degree Identities

Every directed edge has:

```text
one source
one destination
```

For:

```text
u → v
```

it contributes:

```text
+1 to outDegree(u)
+1 to inDegree(v)
```

Therefore:

```text
Σ outDegree(v) = |E|
```

and:

```text
Σ inDegree(v) = |E|
```

So together:

```text
Σ inDegree(v) + Σ outDegree(v) = 2|E|
```

---

## 12.1 Dry run

Graph:

```text
(1) -----> (2) -----> (3)
            |
            v
           (4)
```

There are:

```text
|E| = 3
```

In-degrees:

```text
0 + 1 + 1 + 1 = 3
```

Therefore:

```text
Σ inDegree(v) = |E| = 3
```

Out-degrees:

```text
1 + 2 + 0 + 0 = 3
```

Therefore:

```text
Σ outDegree(v) = |E| = 3
```

Combined:

```text
3 + 3 = 6 = 2|E|
```

---

# 13. How to Read a Graph Statement Quickly

Whenever a new graph problem appears, identify these properties first.

```text
1. How many vertices?
2. How many edges?
3. Directed or undirected?
4. Weighted or unweighted?
5. Are vertices 0-indexed or 1-indexed?
6. Is the graph sparse or dense?
7. Are multiple edges possible?
8. Are self-loops possible?
```

A compact mental tree:

```text
GRAPH
│
├── Vertices = N
│
├── Edges = M
│
├── Direction
│   ├── Directed
│   └── Undirected
│
├── Weight
│   ├── Weighted
│   └── Unweighted
│
├── Density
│   ├── Sparse
│   └── Dense
│
└── Degree
    ├── Undirected → degree
    └── Directed
        ├── in-degree
        └── out-degree
```

---

# 14. Compact Revision Table

| Concept | Meaning |
|---|---|
| Vertex / Node | One object in the graph |
| Edge | Relationship between two vertices |
| `G = (V, E)` | Graph with vertex set `V` and edge set `E` |
| `|V|` | Number of vertices |
| `|E|` | Number of edges |
| Weighted | Edges carry values |
| Unweighted | Edges only indicate connection |
| Directed | Edge has direction |
| Undirected | Edge works both ways |
| Sparse | Relatively few edges, often around `O(N)` |
| Dense | Many edges, approaching `O(N²)` |
| `deg(v)` | Number of incident edges in an undirected graph |
| `inDegree(v)` | Number of incoming directed edges |
| `outDegree(v)` | Number of outgoing directed edges |
| Handshaking lemma | `Σ deg(v) = 2|E|` |
| Directed identity | `Σ inDegree = Σ outDegree = |E|` |

---

# 15. Final Mental Model

When you see a graph, first separate **structure** from **properties**.

```text
                 GRAPH
                   |
          +--------+--------+
          |                 |
       Vertices            Edges
          |                 |
       objects          relationships
                            |
                  +---------+---------+
                  |                   |
              direction             weight
                  |                   |
          directed/undirected   weighted/unweighted
```

Then ask:

```text
How many vertices?       → N = |V|
How many edges?          → M = |E|
How connected is each?   → degree
How many arrows enter?   → in-degree
How many arrows leave?   → out-degree
Few or many edges?       → sparse / dense
```

The key identities to remember are:

```text
Undirected:
Σ deg(v) = 2|E|

Directed:
Σ inDegree(v)  = |E|
Σ outDegree(v) = |E|
```

These ideas become the language used by nearly every later graph topic:

```text
DFS
BFS
Connected Components
Cycle Detection
Topological Sort
Shortest Paths
MST
SCC
Bridges
Articulation Points
Trees
```

Once the terminology is automatic, those algorithms become much easier to reason about.
