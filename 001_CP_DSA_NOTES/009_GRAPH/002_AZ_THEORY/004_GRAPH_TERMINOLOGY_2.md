# Graph Terminology — Paths, Reachability, Components & SCC

> Self-study notes based on the supplied AlgoZenith lecture screenshots.  
> Style: prerequisite-first, step-by-step, compact but complete, with clean ASCII graph diagrams.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Path](#2-path)
3. [Simple Path](#3-simple-path)
4. [Cycle](#4-cycle)
5. [Simple Cycle](#5-simple-cycle)
6. [Reachability](#6-reachability)
7. [Reachability in Undirected Graphs](#7-reachability-in-undirected-graphs)
8. [Reachability in Directed Graphs](#8-reachability-in-directed-graphs)
9. [Connected Components](#9-connected-components)
10. [Connected Graph](#10-connected-graph)
11. [Strongly Connected Components — SCC](#11-strongly-connected-components--scc)
12. [Step-by-Step SCC Example](#12-step-by-step-scc-example)
13. [How These Ideas Connect to Algorithms](#13-how-these-ideas-connect-to-algorithms)
14. [Recognition Checklist](#14-recognition-checklist)
15. [Compact Revision Table](#15-compact-revision-table)
16. [Final Mental Model](#16-final-mental-model)

---

# 1. Prerequisites

Before studying paths, reachability, connected components, and SCCs, we need a few graph basics.

## 1.1 Graph

A graph is:

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
V = {1,2,3,4}

E = {
    (1,2),
    (2,3),
    (3,4)
}
```

Diagram:

```text
(1) ----- (2) ----- (3) ----- (4)
```

## 1.2 Undirected graph

An undirected edge works both ways.

```text
(1) ----- (2)
```

means:

```text
1 can go to 2
2 can go to 1
```

## 1.3 Directed graph

A directed edge has one direction.

```text
(1) -----> (2)
```

means:

```text
1 can go to 2
```

but not necessarily:

```text
2 can go to 1
```

## 1.4 Sequence

A path is written as a sequence of vertices.

Example:

```text
1 → 2 → 3 → 5
```

For this to be valid, every consecutive pair must be connected:

```text
(1,2) must exist
(2,3) must exist
(3,5) must exist
```

---

# 2. Path

A **path** is a sequence of vertices where every consecutive pair is connected by an edge.

Suppose:

```text
Path = v1, v2, v3, ..., vk
```

Then every consecutive pair:

```text
(vi, vi+1)
```

must be an edge.

## 2.1 Example

Graph:

```text
        (2)
       /   \
     (1)   (3)
            |
            |
           (4)
```

Sequence:

```text
1 → 2 → 3 → 4
```

is a valid path because:

```text
1--2 exists
2--3 exists
3--4 exists
```

## 2.2 Path may revisit vertices

Using the same graph:

```text
1 → 2 → 3 → 2 → 1
```

still follows valid edges.

But vertex `2` appears twice, so it is **not a simple path**.

---

# 3. Simple Path

A **simple path** is a path in which no vertex is repeated.

Example:

```text
(1) ----- (2) ----- (3) ----- (4)
```

Path:

```text
1 → 2 → 3 → 4
```

All vertices are distinct.

Therefore:

```text
simple path
```

A non-simple example:

```text
1 → 2 → 3 → 2 → 4
```

Here `2` repeats.

So:

```text
not a simple path
```

Compact visual:

```text
Simple:
1 → 2 → 3 → 4

Not simple:
1 → 2 → 3 → 2 → 4
        ^     ^
        same vertex
```

---

# 4. Cycle

A **cycle** is a path that starts and ends at the same vertex.

If:

```text
v1, v2, ..., vk
```

forms a cycle, then:

```text
v1 = vk
```

Example:

```text
      (1)
     /   \
   (2)---(3)
```

Cycle:

```text
1 → 2 → 3 → 1
```

Start:

```text
1
```

End:

```text
1
```

Therefore it is a cycle.

---

# 5. Simple Cycle

A **simple cycle** is a cycle where no vertex repeats except the starting vertex, which is repeated only as the ending vertex.

Example:

```text
1 → 2 → 3 → 4 → 1
```

Here:

```text
1 appears at start and end
2,3,4 appear exactly once
```

Therefore:

```text
simple cycle
```

Non-simple cycle:

```text
1 → 2 → 3 → 2 → 4 → 1
```

Vertex `2` repeats internally.

Therefore:

```text
cycle
but not a simple cycle
```

Compact comparison:

```text
PATH
start/end may differ

SIMPLE PATH
path + no repeated vertex

CYCLE
path + start = end

SIMPLE CYCLE
cycle + no internal repeated vertex
```

---

# 6. Reachability

The central question is:

```text
Can vertex y be reached from vertex x?
```

We say:

```text
y is reachable from x
```

if some path from `x` to `y` exists.

Conceptually:

```text
Reachable(x, y) = true
```

when a path exists.

## 6.1 Example

Graph:

```text
(1) ----- (2) ----- (3) ----- (6)
          |
          |
         (4) ----- (5)

(7) ----- (8)
```

From `1`:

```text
2 reachable
3 reachable
4 reachable
5 reachable
6 reachable
```

But:

```text
7 not reachable
8 not reachable
```

because `{7,8}` is in another disconnected region.

---

# 7. Reachability in Undirected Graphs

In an undirected graph, reachability is symmetric.

If:

```text
y is reachable from x
```

then:

```text
x is reachable from y
```

Why?

Every edge can be traversed in both directions.

Example:

```text
(1) ----- (2) ----- (3)
```

If:

```text
1 can reach 3
```

then:

```text
3 can reach 1
```

Therefore:

```text
Reachable(x,y) = Reachable(y,x)
```

for an undirected graph.

---

# 8. Reachability in Directed Graphs

In directed graphs, reachability is **not necessarily symmetric**.

Example:

```text
(1) -----> (2) -----> (4)
 ^          |
 |          v
 └-------- (3)
```

Suppose edges are:

```text
1 → 2
2 → 3
3 → 1
2 → 4
3 → 4
```

Then:

```text
1 reaches 4
```

through:

```text
1 → 2 → 4
```

Also:

```text
3 reaches 1
```

directly.

But if `4` has no outgoing path back:

```text
4 cannot reach 1
```

Therefore:

```text
Reachable(1,4) = true
Reachable(4,1) = false
```

This asymmetry is exactly why directed graphs need the idea of SCCs.

---

# 9. Connected Components

Connected components are primarily an undirected-graph concept.

A **connected component** is a maximal group of vertices such that every pair of vertices in the group is mutually reachable.

Example:

```text
Component C1:

      (1)
     /   \
   (2)---(3)
     \   /
      (4)


Component C2:

(5)


Component C3:

(6) ----- (7)
```

Full graph:

```text
      (1)                         (6)----(7)
     /   \
   (2)---(3)          (5)
     \   /
      (4)
```

Components:

```text
C1 = {1,2,3,4}
C2 = {5}
C3 = {6,7}
```

Therefore:

```text
number of connected components = 3
```

## 9.1 Why maximal?

`C1` contains all vertices reachable inside that region.

We cannot add vertex `5` because no path connects `5` to `C1`.

So `C1` is maximal.

## 9.2 Isolated vertex

A vertex with no edge forms a component by itself:

```text
(5)
```

So:

```text
{5}
```

is a connected component.

---

# 10. Connected Graph

An undirected graph is **connected** if it contains exactly one connected component.

Equivalent statements:

```text
#connected_components = 1
```

and:

```text
every vertex can reach every other vertex
```

Connected example:

```text
(1) ----- (2)
 |         |
 |         |
(3) ----- (4)
```

Disconnected example:

```text
(1) ----- (2)

(3) ----- (4)
```

The second graph has two connected components.

---

# 11. Strongly Connected Components — SCC

For directed graphs, direction matters.

A **Strongly Connected Component (SCC)** is a maximal set of vertices where every pair can reach each other.

For vertices `x` and `y`:

```text
x and y are in the same SCC
iff
Reachable(x,y) AND Reachable(y,x)
```

## 11.1 Small example

```text
(1) -----> (2)
 ^          |
 |          v
 └-------- (3)
```

Edges:

```text
1 → 2
2 → 3
3 → 1
```

Now:

```text
1 can reach 2
2 can reach 1 through 2 → 3 → 1

1 can reach 3
3 can reach 1

2 can reach 3
3 can reach 2 through 3 → 1 → 2
```

Therefore:

```text
SCC = {1,2,3}
```

## 11.2 Add a one-way exit

Add:

```text
3 → 4
```

Now:

```text
(1) -----> (2)
 ^          |
 |          v
 └-------- (3) -----> (4)
```

The first three vertices mutually reach each other:

```text
{1,2,3}
```

But:

```text
4 cannot return to 1,2,3
```

So:

```text
SCC1 = {1,2,3}
SCC2 = {4}
```

A single vertex can itself be an SCC.

---

# 12. Step-by-Step SCC Example

Suppose the directed graph contains:

```text
1 → 2
2 → 3
3 → 1

3 → 4

4 → 5

5 → 6
6 → 7
7 → 5
```

Diagram:

```text
      ┌───────────┐
      │           v
(1) -----> (2)    (4) -----> (5) -----> (6)
 ^          |                      ^          |
 |          v                      |          v
 └-------- (3) --------------------┘         (7)
                                              |
                                              └-----> (5)
```

A cleaner conceptual view:

```text
SCC1                 SCC2             SCC3

(1) → (2)            (4)          (5) → (6)
 ↑     ↓                              ↑     ↓
 └----(3)                             └----(7)

{1,2,3}              {4}            {5,6,7}
```

## 12.1 SCC1

Inside:

```text
1 → 2 → 3 → 1
```

everyone can reach everyone.

So:

```text
SCC1 = {1,2,3}
```

## 12.2 SCC2

Vertex `4` is not mutually reachable with either side.

Therefore:

```text
SCC2 = {4}
```

## 12.3 SCC3

Inside:

```text
5 → 6 → 7 → 5
```

every vertex can reach every other.

So:

```text
SCC3 = {5,6,7}
```

## 12.4 Condensed graph

Replace each SCC by one super-node:

```text
[SCC1] -----> [SCC2] -----> [SCC3]
```

This SCC-condensation graph is a DAG.

That fact becomes very useful later in advanced graph problems.

---

# 13. How These Ideas Connect to Algorithms

These definitions directly lead to graph algorithms.

## 13.1 Reachability

Question:

```text
Can x reach y?
```

Typical tools:

```text
DFS
BFS
```

## 13.2 Connected components

Question:

```text
How many disconnected groups are there?
```

Typical pattern:

```text
componentCount = 0

for every vertex:
    if unvisited:
        run DFS/BFS
        componentCount++
```

## 13.3 SCC

Question:

```text
Which vertices are mutually reachable in a directed graph?
```

Typical algorithms:

```text
Kosaraju
Tarjan SCC
```

## 13.4 Cycle-related follow-up topics

These definitions lead naturally into:

```text
cycle detection
DAG
topological sort
bridges
articulation points
Euler path/circuit
Hamiltonian path/cycle
```

---

# 14. Recognition Checklist

When reading a graph problem, ask:

```text
1. Directed or undirected?
2. Is the problem asking whether x can reach y?
3. Is it asking how many disconnected groups exist?
4. Does mutual reachability matter?
5. Are vertices repeated in a route?
6. Does the route start and end at the same vertex?
```

Decision model:

```text
Sequence of connected vertices?
        |
       PATH
        |
   no repeated vertex?
        |
   SIMPLE PATH

start == end?
        |
      CYCLE
        |
 no internal repetition?
        |
   SIMPLE CYCLE
```

Reachability model:

```text
Can x reach y?
     |
     +-- Undirected → DFS/BFS connectivity
     |
     +-- Directed   → directional reachability
```

Component model:

```text
Undirected
   |
Connected Components

Directed
   |
Strongly Connected Components
```

---

# 15. Compact Revision Table

| Concept | Meaning |
|---|---|
| Path | Consecutive vertices are connected |
| Simple Path | Path with no repeated vertex |
| Cycle | Path with same start and end |
| Simple Cycle | Cycle with no internal repeated vertex |
| Reachable(x,y) | A path exists from `x` to `y` |
| Connected Component | Maximal mutually reachable region in an undirected graph |
| Connected Graph | Exactly one connected component |
| SCC | Maximal mutually reachable region in a directed graph |
| SCC condition | `R(x,y) && R(y,x)` |

---

# 16. Final Mental Model

```text
GRAPH
  |
  +--> PATH
  |      |
  |      +--> SIMPLE PATH
  |
  +--> CYCLE
  |      |
  |      +--> SIMPLE CYCLE
  |
  +--> REACHABILITY
         |
         +--> UNDIRECTED
         |       |
         |       +--> CONNECTED COMPONENTS
         |       +--> CONNECTED GRAPH
         |
         +--> DIRECTED
                 |
                 +--> DIRECTIONAL REACHABILITY
                 +--> SCC
```

Key memory rules:

```text
Path:
every consecutive pair has an edge

Simple Path:
no repeated vertex

Cycle:
start = end

Simple Cycle:
start = end + no internal repeated vertex

Reachability:
x → ... → y exists

Connected Component:
undirected mutually reachable region

SCC:
directed mutually reachable region
```

And the most important SCC condition:

```text
x and y are in the same SCC
iff
x reaches y AND y reaches x
```

This terminology is the foundation for:

```text
DFS
BFS
Connected Components
Cycle Detection
Topological Sort
SCC
Bridges
Articulation Points
Trees
Shortest Paths
```
