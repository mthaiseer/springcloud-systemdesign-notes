# CP Mathematical Modeling Mini-Course

> **Goal:** Convert a Codeforces problem from **English/story →
> mathematical model → observation → algorithm**.
>
> **Scope:** Mathematical modeling only. Number Theory and Combinatorics
> are intentionally excluded because they are studied separately.

------------------------------------------------------------------------

## Learning Path

``` text
0. Foundation
      ↓
1. Story → Mathematics
      ↓
2. Operation Modeling
      ↓
3. Conditions & Structure
      ↓
4. Algebra → Smaller Search
      ↓
5. Resource & Optimization Modeling
      ↓
6. Feasibility Modeling
      ↓
7. Collection Modeling
      ↓
8. Constructive Modeling
      ↓
9. Constraints → Model → Algorithm
      ↓
10. Complete CF Modeling Practice
```

------------------------------------------------------------------------

# 0. Foundation

The minimum algebra toolkit needed for CP mathematical modeling.

-   **0.1 Variables & Expressions**
-   **0.2 Basic Equations**
-   **0.3 Rearranging Equations**
-   **0.4 Inequalities**
-   **0.5 Min / Max**
-   **0.6 Absolute Value**
-   **0.7 Floor / Ceil**

**Target:** Make simple algebraic translation automatic before moving to
Codeforces-style modeling.

------------------------------------------------------------------------

# 1. Story → Mathematics

Learn to remove the story and expose the mathematical problem.

-   **1.1 Identify quantities**
-   **1.2 Give quantities variables**
-   **1.3 Translate relationships**
    -   Sum / total
    -   Difference
    -   More / less
    -   At least / at most
    -   Ratio / proportion
-   **1.4 Build equations**
-   **1.5 Build inequalities and bounds**
-   **1.6 Remove irrelevant story information**

**Core transformation:**

``` text
Story → Quantities → Variables → Relationships → Mathematical model
```

------------------------------------------------------------------------

# 2. Operation Modeling

Turn repeated operations into mathematics instead of blindly simulating
them.

-   **2.1 Model one operation**
-   **2.2 Model `k` operations**
-   **2.3 Repeated operations → formula**
-   **2.4 Final-state equations**
-   **2.5 Reverse operations**
-   **2.6 Simulation → mathematics**

Example pattern:

``` text
One operation:
(a, b) → (a + x, b - y)

After k operations:
a' = a + kx
b' = b - ky
```

------------------------------------------------------------------------

# 3. Conditions & Structure

Learn to discover what must remain true.

-   **3.1 Necessary conditions**
-   **3.2 Sufficient conditions**
-   **3.3 Case splitting**
-   **3.4 Parity as state**
-   **3.5 Absolute difference / distance**
-   **3.6 Conservation**
-   **3.7 Invariants**

**Key question:** What changes, and what cannot change?

------------------------------------------------------------------------

# 4. Algebra → Smaller Search

Use equations to eliminate unnecessary brute force.

-   **4.1 Rearrange relationships**
-   **4.2 Substitute variables**
-   **4.3 Eliminate variables**
-   **4.4 Reduce search dimensions**
-   **4.5 Pair modeling**
-   **4.6 Triple modeling**
-   **4.7 Brute force → formula**

Example:

``` text
x + y + z = S

        ↓

z = S - x - y

        ↓

Search x and y; calculate z.
```

Potential transformation:

``` text
O(n³) → O(n²)
```

------------------------------------------------------------------------

# 5. Resource & Optimization Modeling

Translate limited resources into mathematical bounds.

-   **5.1 Resource consumption**
-   **5.2 Capacity**
-   **5.3 Define `k` = number of operations/items**
-   **5.4 Derive lower bounds**
-   **5.5 Derive upper bounds**
-   **5.6 Identify the limiting resource**
-   **5.7 Min / Max answer**

Typical thinking:

``` text
Suppose the answer is k.

What resources are required for k?
What inequalities must k satisfy?
Which condition limits k?
```

------------------------------------------------------------------------

# 6. Feasibility Modeling

Turn optimization problems into yes/no checks.

-   **6.1 Can `k` work?**
-   **6.2 Requirements → conditions**
-   **6.3 Optimization → decision problem**
-   **6.4 Monotonicity**
-   **6.5 Binary Search on Answer connection**

Typical question:

``` text
If k works, do all smaller k also work?
```

------------------------------------------------------------------------

# 7. Collection Modeling

Replace unnecessary element-level detail with useful aggregate
information.

-   **7.1 Values → frequencies**
-   **7.2 State reduction**
-   **7.3 Contribution of one element**
-   **7.4 Total contribution**
-   **7.5 Prefix relationships**
-   **7.6 Suffix relationships**

**Goal:** Keep only the information that actually affects the answer.

------------------------------------------------------------------------

# 8. Constructive Modeling

Work backward from the required result.

-   **8.1 Start from the required answer**
-   **8.2 Determine conditions that must hold**
-   **8.3 Build objects satisfying those conditions**
-   **8.4 Reverse thinking**
-   **8.5 Greedy choice from inequalities**
-   **8.6 Prove the construction works**

Typical thinking:

``` text
What must the final object look like?
        ↓
What conditions guarantee that?
        ↓
How can I construct something satisfying them?
```

------------------------------------------------------------------------

# 9. Constraints → Model → Algorithm

Connect mathematical modeling to algorithm selection.

-   **9.1 Read constraints mathematically**
-   **9.2 Estimate allowed complexity**
-   **9.3 Start with brute force**
-   **9.4 Identify the mathematical bottleneck**
-   **9.5 Simplify the model**
-   **9.6 Derive the algorithm**

Core process:

``` text
Brute force
    ↓
Why is it slow?
    ↓
Which search/work is unnecessary?
    ↓
Can mathematics eliminate it?
    ↓
Efficient algorithm
```

------------------------------------------------------------------------

# 10. Complete CF Modeling Practice

Apply the modeling engine to progressively harder Codeforces-style
problems.

-   **10.1 800--1000:** Direct translation
-   **10.2 1000--1200:** Equations + operations
-   **10.3 1200--1400:** Bounds + parity + cases
-   **10.4 1400--1600:** Invariants + variable elimination
-   **10.5 1600--1800:** Feasibility + optimization + constructive
    modeling
-   **10.6 1800--1900:** Mixed modeling + proof

> Ratings are approximate. Codeforces difficulty and required techniques
> vary from problem to problem.

------------------------------------------------------------------------

# The CP Mathematical Modeling Engine

``` text
                    CF PROBLEM
                        │
                        ▼
                 Remove the story
                        │
                        ▼
              Identify quantities
                        │
                        ▼
                Define variables
                        │
                        ▼
              Write relationships
                 /      |      \
                /       |       \
          Equation  Inequality  Operation
                \       |       /
                 \      |      /
                        ▼
                 Find structure
          ┌─────────────┼─────────────┐
          │             │             │
       Invariant      Bounds        Cases
          │             │             │
          └─────────────┼─────────────┘
                        ▼
              Simplify algebraically
                        │
                        ▼
          Eliminate variable / search?
                        │
                        ▼
          Feasibility / Optimization
                        │
                        ▼
                    Algorithm
                        │
                        ▼
                 Proof + Code
```

------------------------------------------------------------------------

# How Each Lesson Will Work

Each lesson should be practice-oriented:

1.  **Tiny concept**
2.  **Two simple examples**
3.  **English → Math translation drills**
4.  **CP-style modeling exercises**
5.  **You solve before seeing the solution**
6.  **Reasoning review**
7.  **Mini challenge**
8.  **Short takeaway**

The objective is not formula memorization. The objective is to make this
automatic:

``` text
English → Math → Observation → Algorithm
```

------------------------------------------------------------------------

# Completion Goal

By the end of this mini-course, when reading a new CP problem you should
naturally ask:

-   What are the actual quantities?
-   What variables should represent them?
-   What equations or inequalities describe the problem?
-   What does one operation do?
-   What happens after `k` operations?
-   What stays invariant?
-   What are the lower and upper bounds?
-   Can I eliminate a variable?
-   Can I replace simulation with a formula?
-   Can I turn optimization into a feasibility check?
-   What algorithm follows from this model?

The course is complete when this reasoning becomes part of your normal
problem-solving process rather than a separate checklist.
