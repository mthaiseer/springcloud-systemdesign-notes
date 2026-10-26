# Graph Terminology — Self-Loops, Multiple Edges, Multigraphs & Simple Graphs

> Self-study notes built from the supplied AlgoZenith **Graph Terminologies — Level 4** screenshots.  
> Format: prerequisite-first, step-by-step, compact explanations, and clean ASCII graph diagrams.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [What Exactly Is an Edge?](#2-what-exactly-is-an-edge)
3. [Self-Loop](#3-self-loop)
4. [Multiple / Parallel Edges](#4-multiple--parallel-edges)
5. [Multigraph](#5-multigraph)
6. [Simple Graph](#6-simple-graph)
7. [Simple Graph vs Multigraph](#7-simple-graph-vs-multigraph)
8. [Why These Definitions Matter in Problems](#8-why-these-definitions-matter-in-problems)
9. [Common Traps](#9-common-traps)
10. [Recognition Checklist](#10-recognition-checklist)
11. [Compact Revision Table](#11-compact-revision-table)
12. [Final Mental Model](#12-final-mental-model)

---

# 1. Prerequisites

Before understanding **self-loops**, **multiple edges**, **multigraphs**, and **simple graphs**, we need four basic graph ideas.

---

## 1.1 Graph

A graph is written as:

```text
G = (V, E)
```

where:

```text
V = set of vertices / nodes
E = set (or collection) of edges
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

---

## 1.2 Vertex / Node

A **vertex** is simply a point in the graph.

```text
(1)   (2)   (3)
```

The terms:

```text
vertex
node
```

are usually used interchangeably in competitive programming.

---

## 1.3 Edge

An **edge** connects two vertices.

Example:

```text
(1) ----- (2)
```

The edge can be written as:

```text
(1,2)
```

For an undirected graph:

```text
(1,2) = (2,1)
```

because direction does not matter.

---

## 1.4 Directed vs Undirected

### Undirected

```text
(1) ----- (2)
```

You can move both ways.

```text
1 ↔ 2
```

### Directed

```text
(1) -----> (2)
```

You can move only in the arrow direction.

```text
1 → 2
```

This matters later because:

```text
1 → 2
```

and:

```text
2 → 1
```

are two different directed edges.

---

# 2. What Exactly Is an Edge?

Normally we imagine an edge joining **two different vertices**.

Example:

```text
(1) ----- (2)
```

But graph theory allows special cases.

Two important special cases from this lecture are:

```text
1. edge from a vertex back to itself
2. more than one edge between the same pair of vertices
```

These lead to:

```text
SELF-LOOP
MULTIPLE EDGES
MULTIGRAPH
SIMPLE GRAPH
```

---

# 3. Self-Loop

A **self-loop** is an edge whose two endpoints are the same vertex.

Formally:

```text
(u,u)
```

---

## 3.1 Visual

```text
      ┌─────┐
      │     │
      │    (1) ----- (2)
      │
      └─────┘
```

A cleaner sketch:

```text
      ↺
     (1) ----- (2)
```

The loop begins at vertex `1` and returns to vertex `1`.

---

## 3.2 Example

Suppose:

```text
V = {1,2,3}

E = {
    (1,1),
    (1,2),
    (2,3)
}
```

Diagram:

```text
      ↺
     (1) ----- (2) ----- (3)
```

The edge:

```text
(1,1)
```

is a self-loop.

---

## 3.3 Main recognition rule

Whenever you see:

```text
edge = (u,u)
```

you immediately know:

```text
SELF-LOOP
```

---

## 3.4 Directed self-loop

A directed graph can also contain a self-loop.

```text
   ↺
  (u)
```

That still means:

```text
u → u
```

---

## 3.5 Degree effect of a self-loop

This is useful later.

### Undirected graph

A self-loop contributes **2** to the degree of that vertex.

Why?

The edge touches the same vertex at both ends.

```text
      ↺
     (1)
```

So:

```text
degree contribution = 2
```

### Directed graph

A self-loop contributes:

```text
+1 to indegree
+1 to outdegree
```

because:

```text
u → u
```

both leaves and enters `u`.

---

# 4. Multiple / Parallel Edges

Multiple edges mean that **more than one edge connects the same pair of vertices**.

They are also called:

```text
parallel edges
```

---

## 4.1 Visual

```text
       =====
(A) ========= (B)
       =====
```

More explicitly:

```text
        edge 1
(A) ------------- (B)
 \_______________/
      edge 2
```

Both edges connect:

```text
A and B
```

---

## 4.2 Weighted example

The lecture shows the important idea that two edges may connect the same pair but have different properties.

Example:

```text
A =====5===== B
A =====3===== B
```

There are two distinct edges:

```text
A ↔ B with weight 5
A ↔ B with weight 3
```

Even though the endpoints are identical, the edges are different.

---

## 4.3 Why would multiple edges exist?

Real-world interpretations:

```text
two different roads between the same cities
two flights between the same airports
two communication links between the same servers
two routes with different cost/time/capacity
```

So a problem may legitimately contain:

```text
u v w1
u v w2
```

with:

```text
w1 != w2
```

---

## 4.4 Important CP implication

Do **not** automatically remove duplicate-looking edges unless the problem allows it.

For example:

```text
1 2 10
1 2 3
```

If you erase one edge, you may destroy the optimal answer.

For shortest path:

```text
weight 3
```

is better than:

```text
weight 10
```

But for another problem, both edges may still matter individually.

---

# 5. Multigraph

A **multigraph** is a graph that allows multiple edges between the same pair of vertices.

Depending on the exact convention being used, self-loops may also be allowed. In this lecture, the important contrast is:

```text
MULTIGRAPH
vs
SIMPLE GRAPH
```

---

## 5.1 Example

```text
        ┌────────────┐
        │            │
       (1) ========= (2)
        │            │
        └────(3)─────┘
```

Between `1` and `2`, suppose we have two edges:

```text
edge A
edge B
```

Then the graph is not simple.

It belongs to the multigraph-style category.

---

## 5.2 Another example

```text
      ↺
     (1) ===== (2)
       \       /
        \     /
         (3)
```

Here we may have:

```text
a self-loop
multiple edges
```

This is definitely **not a simple graph**.

---

# 6. Simple Graph

A **simple graph** has:

```text
NO self-loops
AND
NO multiple edges
```

This is the central definition from the lecture.

---

## 6.1 Simple graph example

```text
       (2)
      /   \
    (1)---(3)
```

Edges:

```text
(1,2)
(2,3)
(1,3)
```

Check:

```text
self-loop?       NO
parallel edges?  NO
```

Therefore:

```text
SIMPLE GRAPH
```

---

## 6.2 Not simple because of a self-loop

```text
      ↺
     (1) ----- (2)
```

Reason:

```text
self-loop exists
```

Therefore:

```text
NOT simple
```

---

## 6.3 Not simple because of multiple edges

```text
(1) ======== (2)
 \__________/
```

Reason:

```text
more than one edge connects 1 and 2
```

Therefore:

```text
NOT simple
```

---

## 6.4 Not simple because both exist

```text
      ↺
     (1) ===== (2)
```

This graph has:

```text
self-loop
+
parallel edges
```

So it is certainly:

```text
NOT simple
```

---

# 7. Simple Graph vs Multigraph

The easiest way to remember the difference:

```text
SIMPLE GRAPH
    |
    +-- no self-loop
    |
    +-- no multiple edge
```

Whereas:

```text
MULTIGRAPH
    |
    +-- multiple edges may exist
```

Visual comparison:

```text
SIMPLE GRAPH

      (2)
     /   \
   (1)---(3)


MULTIGRAPH

      (2)
     // \\
   (1)---(3)
```

Here `//` indicates more than one edge between the same two nodes.

---

# 8. Why These Definitions Matter in Problems

These definitions affect both modelling and implementation.

---

## 8.1 Adjacency list

Suppose:

```text
1 -- 2
1 -- 2
```

An adjacency list may contain:

```text
adj[1] = {2,2}
adj[2] = {1,1}
```

The repeated neighbor is **not automatically an error**.

It may represent two different edges.

---

## 8.2 Weighted adjacency list

Suppose:

```text
1 --5-- 2
1 --3-- 2
```

Representation:

```text
adj[1] = {(2,5), (2,3)}
```

Both entries matter.

---

## 8.3 Edge IDs

For algorithms involving edges individually, such as:

```text
bridges
Euler tours
multigraph traversal
```

it is often useful to give every edge an ID.

Example:

```text
edge 0: 1 -- 2
edge 1: 1 -- 2
```

Although endpoints are identical:

```text
edge 0 != edge 1
```

This prevents us from incorrectly treating them as the same physical edge.

---

## 8.4 Self-loop handling

A self-loop may affect:

```text
degree
cycle-related conditions
Euler path/circuit logic
adjacency representation
```

So always notice whether the input permits:

```text
u == v
```

---

# 9. Common Traps

## Trap 1 — Assuming every graph is simple

A problem may give:

```text
u == v
```

or duplicate edges.

Never assume simplicity unless:

```text
the statement guarantees it
```

or the constraints imply it.

---

## Trap 2 — Removing duplicate edges blindly

Input:

```text
1 2 10
1 2 3
```

These may be two meaningful edges.

Do not casually reduce them to:

```text
1--2
```

without understanding the problem.

---

## Trap 3 — Forgetting self-loop degree contribution

Undirected self-loop:

```text
degree += 2
```

Directed self-loop:

```text
indegree += 1
outdegree += 1
```

---

## Trap 4 — Confusing repeated neighbor with repeated vertex

If adjacency is:

```text
adj[1] = {2,2}
```

this does **not** mean there are two copies of vertex `2`.

It means:

```text
two edges connect 1 and 2
```

---

# 10. Recognition Checklist

When you receive a graph problem, check these immediately:

```text
1. Can u == v?
       |
       +--> possible self-loop

2. Can the same (u,v) pair appear more than once?
       |
       +--> possible multiple/parallel edges

3. Are duplicate edges meaningful?
       |
       +--> probably keep edge identity

4. Does the statement say "simple graph"?
       |
       +--> no self-loop + no parallel edge
```

Decision diagram:

```text
Graph
 |
 +-- Does any edge look like (u,u)?
 |        |
 |        YES → Self-loop exists
 |
 +-- Are there 2+ edges between same pair?
          |
          YES → Multiple / parallel edges exist
```

Then:

```text
no self-loop
AND
no parallel edge
        |
        v
   SIMPLE GRAPH
```

Otherwise:

```text
not a simple graph
```

---

# 11. Compact Revision Table

| Term | Meaning |
|---|---|
| Self-loop | Edge from a vertex to itself: `(u,u)` |
| Parallel edges | Two or more distinct edges between the same endpoints |
| Multiple edges | Same idea as parallel edges |
| Multigraph | Graph that permits multiple edges |
| Simple graph | No self-loop and no multiple edges |
| Undirected self-loop degree | contributes `2` |
| Directed self-loop | contributes `1` indegree + `1` outdegree |

---

# 12. Final Mental Model

```text
GRAPH EDGE SPECIAL CASES
          |
          +-------------------------+
          |                         |
          v                         v
     SELF-LOOP                MULTIPLE EDGES
       (u,u)                same endpoints
                                  more than once
          |                         |
          +------------+------------+
                       |
                       v
               NOT A SIMPLE GRAPH
```

Simple graph rule:

```text
SIMPLE GRAPH
=
NO SELF-LOOP
+
NO MULTIPLE EDGE
```

Memory examples:

```text
Self-loop:
     ↺
    (1)

Parallel edges:
(1) ===== (2)

Simple graph:
      (2)
     /   \
   (1)---(3)
```

The fastest contest recognition rule is:

```text
u == v
    → self-loop

same pair repeated
    → parallel/multiple edges

neither exists
    → graph may be simple
```

These ideas become important later in:

```text
degree counting
cycle handling
Euler path/circuit
bridges
adjacency lists
edge IDs
weighted graphs
graph modelling
```
