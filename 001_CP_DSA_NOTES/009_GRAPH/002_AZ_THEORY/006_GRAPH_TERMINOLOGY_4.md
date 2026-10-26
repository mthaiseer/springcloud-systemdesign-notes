# Graph Terminology — Eulerian Path, Eulerian Cycle & Eulerian Graph

> Based on the supplied AlgoZenith **Graph Terminologies — Level 6** screenshots.
>
> The lecture's central idea is:
>
> **Eulerian = use every edge of the graph exactly once.**
>
> The prerequisite section below is added only to make the lecture self-study friendly.

---

## Table of Contents

1. [Prerequisite Refresher](#1-prerequisite-refresher)
2. [Walk, Trail, Path and Cycle](#2-walk-trail-path-and-cycle)
3. [The Central Eulerian Idea](#3-the-central-eulerian-idea)
4. [Eulerian Path](#4-eulerian-path)
5. [Eulerian Cycle](#5-eulerian-cycle)
6. [Eulerian Graph](#6-eulerian-graph)
7. [Eulerian Path vs Eulerian Cycle](#7-eulerian-path-vs-eulerian-cycle)
8. [Step-by-Step Examples](#8-step-by-step-examples)
9. [Common Confusions](#9-common-confusions)
10. [Recognition Checklist](#10-recognition-checklist)
11. [Final Mental Model](#11-final-mental-model)
12. [Hamiltonian Concepts](#12-hamiltonian-concepts)
13. [Hamiltonian Path](#13-hamiltonian-path)
14. [Hamiltonian Cycle](#14-hamiltonian-cycle)
15. [Hamiltonian Graph](#15-hamiltonian-graph)
16. [Hamiltonian Path vs Hamiltonian Cycle](#16-hamiltonian-path-vs-hamiltonian-cycle)
17. [Eulerian vs Hamiltonian](#17-eulerian-vs-hamiltonian--most-important-comparison)
18. [Side-by-Side Diagram](#18-side-by-side-diagram)
19. [One Without the Other](#19-a-graph-can-be-one-without-the-other)
20. [Hamiltonian Recognition Checklist](#20-hamiltonian-recognition-checklist)
21. [Master Mental Model](#21-eulerian--hamiltonian-master-mental-model)
22. [Final Quick Revision](#22-final-quick-revision)

---

# 1. Prerequisite Refresher

## 1.1 Graph

A graph is:

```text
G = (V, E)
```

where:

```text
V = set of vertices / nodes
E = set of edges
```

Example:

```text
      (2)
     /   \
   (1)   (3)
           \
           (4)
```

---

## 1.2 Vertex

A **vertex** is a node.

```text
(1)    (2)    (3)
```

---

## 1.3 Edge

An **edge** connects two vertices.

```text
(1) -------- (2)
```

---

## 1.4 Directed vs Undirected

Undirected:

```text
(1) -------- (2)
```

Directed:

```text
(1) -------> (2)
```

The screenshots focus on the Eulerian idea itself:

```text
use every edge exactly once
```

If the graph is directed, edge directions must also be respected.

---

## 1.5 Degree

For an undirected graph:

```text
degree(v)
=
number of edges incident on vertex v
```

Example:

```text
      (2)
     / | \
   (1)(3)(4)
```

Then:

```text
degree(2) = 3
degree(1) = 1
degree(3) = 1
degree(4) = 1
```

Degree becomes important later when solving Eulerian problems.

---

# 2. Walk, Trail, Path and Cycle

This is the most useful prerequisite distinction.

## 2.1 Walk

A walk is a sequence of vertices where consecutive vertices are connected by edges.

Vertices and edges may repeat.

Example graph:

```text
      (2)
     /   \
   (1)   (3)
     \   /
      (4)
```

Possible walk:

```text
1 -> 2 -> 3 -> 4 -> 1 -> 2
```

---

## 2.2 Trail

A **trail** is a walk in which:

```text
NO EDGE is repeated
```

Vertices may repeat.

Example:

```text
1 -> 2 -> 3 -> 4 -> 1
```

using:

```text
(1,2)
(2,3)
(3,4)
(4,1)
```

Each edge is used once.

This is the idea closest to Eulerian traversal.

---

## 2.3 Path

For standard CP terminology, a simple path does not repeat vertices.

```text
(1)---(2)---(3)---(4)
```

Path:

```text
1 -> 2 -> 3 -> 4
```

---

## 2.4 Cycle

A cycle starts and ends at the same vertex.

```text
      (2)
     /   \
   (1)---(3)
```

Cycle:

```text
1 -> 2 -> 3 -> 1
```

---

## 2.5 Most Important Distinction

```text
PATH
focuses mainly on the route through vertices

TRAIL
focuses on NOT repeating edges

EULERIAN
focuses on USING EVERY EDGE exactly once
```

For Eulerian thinking, the important resource is:

```text
EDGES
```

---

# 3. The Central Eulerian Idea

The lecture introduces:

```text
Eulerian Path
Eulerian Cycle
Eulerian Graph
```

The common condition is:

```text
EVERY EDGE IN G
EXACTLY ONCE
```

That is the heart of the entire topic.

---

## 3.1 Think "Edges", Not "Vertices"

Suppose:

```text
      (2)
     /   \
   (1)   (3)
     \   /
      (4)
```

Edges:

```text
(1,2)
(2,3)
(3,4)
(4,1)
```

Eulerian thinking asks:

```text
Can I traverse all 4 edges
without using any edge twice?
```

It does NOT primarily ask:

```text
Did I visit every vertex exactly once?
```

---

# 4. Eulerian Path

Lecture definition:

```text
Eulerian Path
=
a traversal that uses
EVERY EDGE in G exactly once
```

The start and end vertices do not have to be the same.

---

## 4.1 Example

```text
(1)---(2)---(3)---(4)
```

Edges:

```text
e1 = (1,2)
e2 = (2,3)
e3 = (3,4)
```

Traversal:

```text
1 -> 2 -> 3 -> 4
```

Check:

```text
(1,2) used once
(2,3) used once
(3,4) used once
```

Every edge is used exactly once.

Therefore this is an:

```text
Eulerian Path
```

---

## 4.2 Start and End Can Differ

For the above traversal:

```text
start = 1
end   = 4
```

So:

```text
start != end
```

This is allowed for an Eulerian Path.

---

# 5. Eulerian Cycle

Lecture definition:

```text
Eulerian Cycle
=
a cycle that uses
EVERY EDGE in G exactly once
```

A cycle must return to the starting vertex.

---

## 5.1 Example

```text
      (2)
     /   \
   (1)   (3)
     \   /
      (4)
```

Traversal:

```text
1 -> 2 -> 3 -> 4 -> 1
```

Edges used:

```text
(1,2)
(2,3)
(3,4)
(4,1)
```

Every edge is used exactly once.

Also:

```text
start = 1
end   = 1
```

Therefore:

```text
Eulerian Cycle
```

---

## 5.2 Compact Edge Checklist

```text
Step 1: 1 -> 2  use (1,2)
Step 2: 2 -> 3  use (2,3)
Step 3: 3 -> 4  use (3,4)
Step 4: 4 -> 1  use (4,1)
```

Result:

```text
no edge repeated
no edge missed
returned to start
```

---

# 6. Eulerian Graph

The lecture definition is:

```text
A graph is Eulerian
if it contains an Eulerian Cycle.
```

So:

```text
Eulerian Graph
        |
        v
contains a cycle
that uses every edge exactly once
```

Example:

```text
      (2)
     /   \
   (1)   (3)
     \   /
      (4)
```

Because:

```text
1 -> 2 -> 3 -> 4 -> 1
```

uses every edge exactly once, this graph is:

```text
Eulerian
```

---

# 7. Eulerian Path vs Eulerian Cycle

| Concept | Every edge exactly once? | Return to start? |
|---|---:|---:|
| Eulerian Path | Yes | Not required |
| Eulerian Cycle | Yes | Yes |
| Eulerian Graph | Contains an Eulerian Cycle | — |

Diagram:

```text
Eulerian Path

(1)---(2)---(3)---(4)

1 -> 2 -> 3 -> 4

start != end
```

```text
Eulerian Cycle

      (2)
     /   \
   (1)   (3)
     \   /
      (4)

1 -> 2 -> 3 -> 4 -> 1

start == end
```

---

# 8. Step-by-Step Examples

## 8.1 Example A — Chain

```text
(1)---(2)---(3)
```

Traversal:

```text
1 -> 2 -> 3
```

Dry run:

```text
Step 1: 1 -> 2  use (1,2)
Step 2: 2 -> 3  use (2,3)
```

All edges are used exactly once.

So:

```text
Eulerian Path exists
```

---

## 8.2 Example B — Square

```text
(1)-------(2)
 |         |
 |         |
(4)-------(3)
```

Traversal:

```text
1 -> 2 -> 3 -> 4 -> 1
```

Dry run:

```text
Step 1: 1 -> 2  use (1,2)
Step 2: 2 -> 3  use (2,3)
Step 3: 3 -> 4  use (3,4)
Step 4: 4 -> 1  use (4,1)
```

All edges used once and we return to the start.

Therefore:

```text
Eulerian Cycle exists
```

Hence:

```text
the graph is Eulerian
```

---

## 8.3 Repeating a Vertex Is Not Automatically Wrong

Eulerian terminology is controlled by:

```text
EDGE usage
```

not by:

```text
vertex uniqueness
```

So revisiting a vertex can still be valid if no edge is reused and all required edges are covered.

---

## 8.4 Reusing an Edge Breaks the Rule

Graph:

```text
(1)---(2)---(3)
```

Traversal:

```text
1 -> 2 -> 1 -> 2 -> 3
```

Edge:

```text
(1,2)
```

is used more than once.

Therefore this is:

```text
NOT Eulerian
```

---

## 8.5 Missing an Edge Also Breaks the Rule

Graph:

```text
      (2)
     /   \
   (1)---(3)
```

Traversal:

```text
1 -> 2 -> 3
```

Used:

```text
(1,2)
(2,3)
```

Missed:

```text
(1,3)
```

Therefore:

```text
NOT Eulerian
```

because the lecture rule is:

```text
EVERY edge exactly once
```

---

# 9. Common Confusions

## 9.1 Eulerian Does NOT Mean Every Vertex Exactly Once

Wrong mental model:

```text
visit every vertex exactly once
```

Correct Eulerian mental model:

```text
use every EDGE exactly once
```

---

## 9.2 Repeating Vertices May Be Fine

For Eulerian traversal:

```text
vertex repetition can happen
```

The key condition is:

```text
each edge appears exactly once
```

---

## 9.3 Using Only Some Edges Is Not Enough

If the graph contains:

```text
10 edges
```

but your traversal covers:

```text
9 edges
```

it is not Eulerian.

---

## 9.4 Eulerian Path vs Cycle

```text
Eulerian Path:
start and end may differ

Eulerian Cycle:
start == end
```

Both still require:

```text
every edge exactly once
```

---

# 10. Recognition Checklist

```text
Is the problem talking about EDGES?
        |
        v
Must every edge be used?
        |
        v
Must every edge be used exactly once?
        |
   +----+----+
   |         |
  YES        NO
   |
   v
Eulerian family
```

Then:

```text
Must the traversal return to its start?
        |
   +----+----+
   |         |
  YES        NO
   |         |
   v         v
Eulerian   Eulerian
Cycle      Path
```

Finally:

```text
Does the graph contain an Eulerian Cycle?
        |
        v
YES
        |
        v
Eulerian Graph
```

---

# 11. Final Mental Model

```text
EULERIAN
   |
   v
EVERY EDGE
EXACTLY ONCE
   |
   +-------------------------+
   |                         |
   v                         v
PATH                       CYCLE
start/end                  start/end
may differ                 same
   |                         |
   v                         v
Eulerian Path          Eulerian Cycle
                             |
                             v
                       Eulerian Graph
```

One-line memory rule:

```text
Eulerian thinking = EDGES
```

For this lecture, remember:

```text
Eulerian Path:
every edge exactly once

Eulerian Cycle:
every edge exactly once
and return to start

Eulerian Graph:
contains an Eulerian Cycle
```


---

# 12. Hamiltonian Concepts

Eulerian and Hamiltonian ideas look similar, but they focus on completely different things.

The simplest memory rule is:

```text
Eulerian    -> EDGES
Hamiltonian -> VERTICES
```

---

## 12.1 Prerequisite — Simple Path

A **simple path** does not repeat vertices.

Example:

```text
(1)---(2)---(3)---(4)

1 -> 2 -> 3 -> 4
```

Vertices used:

```text
1, 2, 3, 4
```

Each vertex appears once.

This is the key prerequisite for Hamiltonian terminology.

---

# 13. Hamiltonian Path

A **Hamiltonian Path** is a path that visits:

```text
EVERY VERTEX
EXACTLY ONCE
```

Unlike Eulerian Path, we are now tracking vertices rather than edges.

---

## 13.1 Example

Consider:

```text
      (2)------(4)
     /  \      /
   (1)  (3)---(5)
```

One Hamiltonian Path is:

```text
1 -> 2 -> 4 -> 5 -> 3
```

Vertex checklist:

```text
1 ✔
2 ✔
3 ✔
4 ✔
5 ✔
```

Every vertex is visited exactly once.

Therefore:

```text
1 -> 2 -> 4 -> 5 -> 3
```

is a Hamiltonian Path.

---

## 13.2 Important Observation

A Hamiltonian Path does **not** require every edge to be used.

Suppose the graph contains extra edges.

That is fine.

We only require:

```text
every vertex exactly once
```

Example:

```text
      (2)------(4)
     / | \     |
   (1) | (3)---(5)
       |
      (5)
```

A Hamiltonian solution may leave some edges unused.

That does NOT invalidate it.

---

# 14. Hamiltonian Cycle

A **Hamiltonian Cycle** is a cycle that:

```text
visits every vertex exactly once
and returns to the starting vertex
```

The starting vertex appears again only to close the cycle.

---

## 14.1 Example

```text
      (2)-------(3)
     /           \
   (1)           (4)
     \           /
      (5)-------/
```

Hamiltonian Cycle:

```text
1 -> 2 -> 3 -> 4 -> 5 -> 1
```

Step-by-step:

```text
Step 1: visit 1
Step 2: visit 2
Step 3: visit 3
Step 4: visit 4
Step 5: visit 5
Step 6: return to 1
```

Unique vertices visited:

```text
{1,2,3,4,5}
```

Every vertex appears exactly once before returning to the starting vertex.

Therefore:

```text
Hamiltonian Cycle
```

---

## 14.2 Why Returning to Start Is Allowed

You may see:

```text
1 -> 2 -> 3 -> 4 -> 5 -> 1
```

and think:

```text
"Vertex 1 appears twice!"
```

But the final `1` only closes the cycle.

The Hamiltonian condition means:

```text
each vertex is visited exactly once
before closing the cycle
```

This is standard cycle notation.

---

# 15. Hamiltonian Graph

A graph is called **Hamiltonian** if it contains a Hamiltonian Cycle.

```text
Hamiltonian Graph
        |
        v
contains a cycle
that visits every vertex exactly once
```

Example:

```text
      (2)-------(3)
     /           \
   (1)           (4)
     \           /
      (5)-------/
```

Because:

```text
1 -> 2 -> 3 -> 4 -> 5 -> 1
```

is a Hamiltonian Cycle,

the graph is:

```text
Hamiltonian
```

---

# 16. Hamiltonian Path vs Hamiltonian Cycle

| Concept | Every vertex exactly once? | Return to start? |
|---|---:|---:|
| Hamiltonian Path | Yes | Not required |
| Hamiltonian Cycle | Yes | Yes |
| Hamiltonian Graph | Contains a Hamiltonian Cycle | — |

---

## 16.1 Visual Comparison

### Hamiltonian Path

```text
(1)---(2)---(3)---(4)---(5)

1 -> 2 -> 3 -> 4 -> 5
```

Checklist:

```text
vertex 1 ✔
vertex 2 ✔
vertex 3 ✔
vertex 4 ✔
vertex 5 ✔
```

Start and end may differ.

---

### Hamiltonian Cycle

```text
      (2)-------(3)
     /           \
   (1)           (4)
     \           /
      (5)-------/
```

Traversal:

```text
1 -> 2 -> 3 -> 4 -> 5 -> 1
```

All vertices are covered once and we return to the start.

---

# 17. Eulerian vs Hamiltonian — Most Important Comparison

This distinction is extremely important in graph problems.

| Property | Eulerian | Hamiltonian |
|---|---|---|
| Main focus | Edges | Vertices |
| Must use every edge? | Yes | No |
| Must visit every vertex exactly once? | No | Yes |
| Repeating vertices allowed? | Yes, if edges are not repeated | No in the Hamiltonian path |
| Unused edges allowed? | No | Yes |
| Path version | Eulerian Path | Hamiltonian Path |
| Cycle version | Eulerian Cycle | Hamiltonian Cycle |
| Graph definition | Contains Eulerian Cycle | Contains Hamiltonian Cycle |

---

## 17.1 One Graph — Two Different Questions

Consider:

```text
      (2)
     /   \
   (1)---(3)
     \   /
      (4)
```

### Eulerian question

Ask:

```text
Can I use EVERY EDGE exactly once?
```

You care about:

```text
(1,2)
(2,3)
(1,3)
(1,4)
(3,4)
```

The edges are the resource.

---

### Hamiltonian question

Ask:

```text
Can I visit EVERY VERTEX exactly once?
```

One possible route:

```text
2 -> 1 -> 4 -> 3
```

Vertices:

```text
2,1,4,3
```

Every vertex is visited once.

Unused edges are perfectly fine.

---

# 18. Side-by-Side Diagram

```text
EULERIAN
========

Goal:
use every EDGE exactly once

      (2)
     /   \
   (1)   (3)
     \   /
      (4)

Think:

edge
edge
edge
edge

------------------------------------------------

HAMILTONIAN
===========

Goal:
visit every VERTEX exactly once

(1)---(2)---(3)---(4)

Think:

1 -> 2 -> 3 -> 4
```

---

# 19. A Graph Can Be One Without the Other

Eulerian and Hamiltonian properties are independent.

A graph may be:

```text
Eulerian but not Hamiltonian
Hamiltonian but not Eulerian
both
neither
```

So never assume:

```text
Eulerian => Hamiltonian
```

or:

```text
Hamiltonian => Eulerian
```

They solve different structural questions.

---

# 20. Hamiltonian Recognition Checklist

When reading a graph problem:

```text
Does the problem care about visiting VERTICES?
        |
        v
Must every vertex be visited?
        |
        v
Must every vertex be visited exactly once?
        |
   +----+----+
   |         |
  YES        NO
   |
   v
Hamiltonian family
```

Then ask:

```text
Must we return to the starting vertex?
        |
   +----+----+
   |         |
  YES        NO
   |         |
   v         v
Hamiltonian Hamiltonian
Cycle       Path
```

Finally:

```text
Does the graph contain a Hamiltonian Cycle?
        |
        v
YES
        |
        v
Hamiltonian Graph
```

---

# 21. Eulerian + Hamiltonian Master Mental Model

```text
GRAPH TRAVERSAL
      |
      +-----------------------------+
      |                             |
      v                             v
  EULERIAN                      HAMILTONIAN
      |                             |
      v                             v
   EDGES                         VERTICES
      |                             |
      v                             v
every edge                   every vertex
exactly once                 exactly once
      |                             |
  +---+---+                     +---+---+
  |       |                     |       |
 PATH   CYCLE                 PATH    CYCLE
          |                             |
          v                             v
    Eulerian Graph               Hamiltonian Graph
```

The most important memory sentence:

```text
Eulerian    = cover all EDGES exactly once
Hamiltonian = cover all VERTICES exactly once
```

---

# 22. Final Quick Revision

```text
Eulerian Path
-------------
every edge exactly once

Eulerian Cycle
--------------
every edge exactly once
return to start

Eulerian Graph
--------------
contains an Eulerian Cycle
```

```text
Hamiltonian Path
----------------
every vertex exactly once

Hamiltonian Cycle
-----------------
every vertex exactly once
return to start

Hamiltonian Graph
-----------------
contains a Hamiltonian Cycle
```

### 5-second recognition

```text
"every road / every edge"
        -> Eulerian

"every city / every node"
        -> Hamiltonian
```
