# Part 26. Common Codeforces Mathematical Forms — Rating Index

> **Purpose:** A compact navigation/index page. Forms are grouped by the approximate rating band where they are most useful as a recognition pattern.
>
> **Important:** These are learning tiers, not fixed Codeforces ratings. A form can appear at easier or harder ratings depending on how hidden it is and what other techniques are combined with it.

---

# Tier 1: Core Staples (Div2 A / Div2 B)

**Target Ratings: 800–1300**

These forms are the backbone of early Codeforces problem solving. The goal is to recognize them almost immediately from the statement.

| Form # | Name | Core Concept / Primary Application |
|---|---|---|
| Form 1 | [Sum Constraint](#form-1-sum-constraint) | Target sums, complements, total-sum equations |
| Form 2 | [Difference Constraint](#form-2-difference-constraint) | Directed/absolute difference, distance, min/max gap |
| Form 3 | [Product Constraint](#form-3-product-constraint) | Factor pairs, product decomposition, `P/x` |
| Form 4 | [Ratio Constraint](#form-4-ratio-constraint) | Cross multiplication, proportional relationships |
| Form 5 | [Parity Constraint](#form-5-parity-constraint) | Odd/even states, parity invariants |
| Form 6 | [Divisibility Constraint](#form-6-divisibility-constraint) | `a | b`, multiples, factor checks |
| Form 9 | [Modulo Constraint](#form-9-modulo-constraint) | Cycles, remainder classes, `%` conditions |
| Form 10 | [Equal Frequency](#form-10-equal-frequency) | Frequency counting, balancing counts |
| Form 12 | [Complement Pair](#form-12-complement-pair) | `x+y=K → need=K-x` |
| Form 13 | [Difference Pair](#form-13-difference-pair) | `|x-y|=K`, lookup `x±K` |
| Form 15 | [Consecutive Values](#form-15-consecutive-values) | `x,x+1,...`, adjacency and off-by-one reasoning |
| Form 17 | [Geometric / Doubling](#form-17-geometric--doubling) | `x,2x,4x,...`, repeated multiplication |
| Form 23 | [Invariant](#form-23-invariant) | Quantity preserved by operations |
| Form 25 | [Reachability](#form-25-reachability) | Can one state transform into another? |
| Form 27 | [Bounding](#form-27-bounding) | Lower/upper bounds, feasibility ranges |
| Form 33 | [Permutation Mathematics](#form-33-permutation-mathematics) | `1..n`, positions, missing/duplicate structure |
| Form 34 | [Mex Mathematics](#form-34-mex-mathematics) | Smallest missing non-negative integer |
| Form 36 | [Grid Parity](#form-36-grid-parity) | `(r+c)%2`, checkerboard structure |
| Form 37 | [Cyclic / Modulo Process](#form-37-cyclic--modulo-process) | Circular movement, repeated cyclic operations |

---

# Tier 2: Core Modeling (Div2 B / Early Div2 C)

**Target Ratings: 1200–1500**

These forms usually require one algebraic transformation, grouping idea, or counting observation before the algorithm becomes obvious.

| Form # | Name | Core Concept / Primary Application |
|---|---|---|
| Form 7 | [GCD Constraint](#form-7-gcd-constraint) | Common divisor structure, normalization |
| Form 8 | [LCM Constraint](#form-8-lcm-constraint) | Common multiples, `gcd*lcm=a*b` |
| Form 11 | [Pair Counting](#form-11-pair-counting) | Count valid pairs without `O(n²)` enumeration |
| Form 14 | [Equal Remainders](#form-14-equal-remainders) | Group values by modulo class |
| Form 16 | [Arithmetic Progression](#form-16-arithmetic-progression) | Constant difference, nth term, AP sums |
| Form 19 | [Prefix Equation](#form-19-prefix-equation) | Convert subarray condition into prefix relation |
| Form 24 | [Monovariant](#form-24-monovariant) | Quantity only increases/decreases |
| Form 26 | [Constructive Equation](#form-26-constructive-equation) | Choose variables, derive remaining values |
| Form 28 | [Extremal Principle](#form-28-extremal-principle) | Reason from minimum/maximum elements |
| Form 32 | [Frequency Compression](#form-32-frequency-compression) | Replace array by `value → count` |
| Form 35 | [Interval Mathematics](#form-35-interval-mathematics) | Overlap, intersection, coverage |
| Form 38 | [Binary Search Equation](#form-38-binary-search-equation) | Monotone predicate / binary search on answer |

---

# Tier 3: Strong Div2 C / Div2 D Modeling

**Target Ratings: 1500–1700**

At this level the mathematical form is often hidden inside the story. Multiple observations may need to be combined.

| Form # | Name | Core Concept / Primary Application |
|---|---|---|
| Form 18 | [Median Optimization](#form-18-median-optimization) | Minimize sum of absolute deviations |
| Form 20 | [Contribution Counting](#form-20-contribution-counting) | Count how often each item contributes |
| Form 21 | [Pigeonhole](#form-21-pigeonhole) | More objects than states guarantees collision |
| Form 22 | [Inclusion-Exclusion](#form-22-inclusion-exclusion) | Count overlapping sets without double counting |
| Form 29 | [Coordinate Transformation](#form-29-coordinate-transformation) | Rearrange mixed index/value equations into keys |
| Form 30 | [Bit Independence](#form-30-bit-independence) | Solve XOR/AND/OR contributions bit-by-bit |
| Form 31 | [Prime Factor Independence](#form-31-prime-factor-independence) | Treat each prime exponent independently |

---

# Tier 4: Advanced Mathematical Forms (Div2 D / E)

**Target Ratings: 1700–1900+**

These are especially valuable when a problem looks complicated but collapses after identifying the underlying mathematical structure.

| Form # | Name | Core Concept / Primary Application |
|---|---|---|
| Form 39 | [Stars and Bars](#form-39-stars-and-bars) | Count integer distributions with fixed sum |
| Form 40 | [Diophantine Equation](#form-40-diophantine-equation) | Integer solutions of `ax+by=c`, GCD feasibility |

---

# Form Index

## Form 1. Sum Constraint
`x+y=S → y=S-x`

## Form 2. Difference Constraint
`x-y=D` or `|x-y|=D`

## Form 3. Product Constraint
`xy=P → y=P/x`

## Form 4. Ratio Constraint
`a/b=c/d → ad=bc`

## Form 5. Parity Constraint
Reason using values modulo `2`.

## Form 6. Divisibility Constraint
`a|b ↔ b%a=0`

## Form 7. GCD Constraint
Factor out the common GCD and normalize.

## Form 8. LCM Constraint
`gcd(a,b) * lcm(a,b) = a*b`

## Form 9. Modulo Constraint
`x ≡ r (mod m)`

## Form 10. Equal Frequency
Group/count equal values.

## Form 11. Pair Counting
Count valid `(i,j)` mathematically rather than enumerate all pairs.

## Form 12. Complement Pair
`x+y=K → need=K-x`

## Form 13. Difference Pair
`|x-y|=K → y=x±K`

## Form 14. Equal Remainders
`a%m=b%m → m|(a-b)`

## Form 15. Consecutive Values
`x,x+1,x+2,...`

## Form 16. Arithmetic Progression
`a, a+d, a+2d, ...`

## Form 17. Geometric / Doubling
`x,2x,4x,...`

## Form 18. Median Optimization
`min Σ|a[i]-x| → median`

## Form 19. Prefix Equation
`sum(l..r)=pref[r]-pref[l-1]`

## Form 20. Contribution Counting
Count appearances/contributions instead of enumerating structures.

## Form 21. Pigeonhole
More objects than possible states implies repetition.

## Form 22. Inclusion-Exclusion
`|A∪B|=|A|+|B|-|A∩B|`

## Form 23. Invariant
Find a quantity unchanged by every operation.

## Form 24. Monovariant
Find a quantity that moves only in one direction.

## Form 25. Reachability
Use invariants, parity, GCD and bounds to characterize reachable states.

## Form 26. Constructive Equation
Choose convenient values and derive the rest.

## Form 27. Bounding
Convert inequalities into feasible lower/upper ranges.

## Form 28. Extremal Principle
Inspect the minimum/maximum or another extreme object.

## Form 29. Coordinate Transformation
Example: `a[j]-a[i]=j-i → a[j]-j=a[i]-i`.

## Form 30. Bit Independence
Analyze each bit independently.

## Form 31. Prime Factor Independence
Analyze each prime exponent independently.

## Form 32. Frequency Compression
`array → {value: count}`.

## Form 33. Permutation Mathematics
Exploit uniqueness and the fixed set `1..n`.

## Form 34. Mex Mathematics
`mex=k → 0..k-1 present, k absent`.

## Form 35. Interval Mathematics
Intersection: `[max(L1,L2), min(R1,R2)]`.

## Form 36. Grid Parity
Use `(row+column)%2` and parity of movement.

## Form 37. Cyclic / Modulo Process
Wrap repeated states with modulo.

## Form 38. Binary Search Equation
Turn feasibility into `FFFFTTTT` or `TTTTFFFF`.

## Form 39. Stars and Bars
Count non-negative integer solutions to fixed-sum equations.

## Form 40. Diophantine Equation
`ax+by=c` has integer solutions iff `gcd(a,b)|c`.

---

# Rating Roadmap

```text
800–1000
Direct equation recognition
        ↓
1000–1300
Parity + modulo + factors + invariants + constructive basics
        ↓
1200–1500
GCD + pair counting + prefix equations + intervals + bounds
        ↓
1500–1700
Contribution + transformations + bit/prime independence
        ↓
1700–1900+
Combinatorial and number-theoretic modeling
```

**Revision goal:** Given a Codeforces statement, try to identify one or more form numbers before thinking about implementation.
