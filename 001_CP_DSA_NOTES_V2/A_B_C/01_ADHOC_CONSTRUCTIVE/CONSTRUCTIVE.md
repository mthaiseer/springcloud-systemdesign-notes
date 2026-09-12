# Codeforces Div2 A/B/C --- Constructive Algorithms Pattern Handbook

**Reorganized edition: all Div2 A patterns first, then Div2 B, then Div2
C.**

**Primary goal:** recognize when Codeforces asks you to build *any*
valid object, then choose a construction whose shape makes the
constraints true by design.

The previous edition had older B/C sections before the newer A/B/C
expansion, which made the learning order look misaligned. This edition
removes that duplication and follows one continuous progression:

``` text
FOUNDATION
    ↓
DIV2 A
    ↓
DIV2 B
    ↓
DIV2 C
    ↓
PROOFS + FAILURE ANALYSIS
    ↓
MIXED RECOGNITION + PRACTICE
```

------------------------------------------------------------------------

## Table of Contents

1.  Constructive Mental Model
2.  Constructive 60-Second Scanner
3.  Part A --- Div2 A Constructive Patterns
    -   A1. Direct Formula Construction
    -   A2. Identity / Reverse / Rotation
    -   A3. Repeating Safe Value
    -   A4. Tiny Case Split Construction
4.  Part B --- Div2 B Constructive Patterns
    -   B1. Base + Correction
    -   B2. Alternating Construction
    -   B3. Cyclic Construction
    -   B4. Pair Swap
    -   B5. Rotation / Cyclic Shift
    -   B6. Block Construction
    -   B7. Construct by Frequency
    -   B8. Sort + Construct
5.  Part C --- Div2 C Constructive Patterns
    -   C1. Greedy Local Repair
    -   C2. Invariant-Guided Construction
    -   C3. Reverse Construction
    -   C4. Permutation by Position Classes
    -   C5. Extreme / Zig-Zag Construction
    -   C6. Greedy Matching Construction
    -   C7. Constructive + GCD / Divisibility
    -   C8. Constructive + XOR / Bits
    -   C9. Prefix/Suffix-Guided Construction
    -   C10. Build the Answer, Not the Operations
    -   C11. Construct Operations Backward
    -   C12. Make Constraints Automatic
6.  Shared Impossibility Proofs
7.  A/B/C Pattern Recognition Matrix
8.  Constructive Proof Patterns
9.  Failure Analysis
10. 60-Second Constructive Contest Decoder
11. Recommended Learning Order
12. Practice Method
13. Final A/B/C Constructive Map

------------------------------------------------------------------------

# 1. Constructive Mental Model

## Constructive

A constructive problem asks you to **build one valid answer**.

Instead of searching every possibility:

``` text
candidate 1
candidate 2
candidate 3
...
```

you design a structure where the constraints are automatically
satisfied.

Example:

``` text
Need adjacent values different.

Construction:

1 2 1 2 1 2
```

Every adjacent pair differs **by design**.

------------------------------------------------------------------------

The central pipeline is:

``` text
WHAT MUST I OUTPUT?
        ↓
ANY valid answer?
        ↓
find necessary condition
        ↓
try simplest shape
        ↓
all same / alternating / cycle / permutation / blocks
        ↓
small correction if needed
        ↓
prove impossible cases
        ↓
print
```

------------------------------------------------------------------------

------------------------------------------------------------------------

# 2. Constructive 60-Second Scanner

``` text
0–10 sec
Does it say:
"print any"
"construct"
"find a permutation"
"output an array"?

10–20 sec
Write only the constraints the output must satisfy.

20–30 sec
Try:
all same
identity
reverse
alternating
cycle
pair swap

30–40 sec
If almost valid:
look for parity/remainder correction.

40–50 sec
Find impossible cases:
parity / modulo / frequency / range / invariant.

50–60 sec
Can I explain in one sentence WHY my shape always works?
```

------------------------------------------------------------------------

------------------------------------------------------------------------

# Part A --- Div2 A Constructive Patterns

Div2 A constructive problems usually have **one visible constraint** and
one very simple construction.

## A1. Direct Formula Construction

### Signal

The output is almost determined by `n`, an index, or a simple
transformation.

Typical statement language:

``` text
construct
print a permutation
find another array
for every i ...
```

### Mental model

Try to turn the condition into a formula:

``` text
b[i] = f(i)
```

or:

``` text
b[i] = C - a[i]
```

### Example --- complement construction

Suppose `a` is a permutation of `1..n` and you want another permutation
whose values move in the opposite direction.

Try:

``` text
b[i] = n + 1 - a[i]
```

Dry run:

``` text
n = 5

a:  1  4  2  5  3
    ↓  ↓  ↓  ↓  ↓
b:  5  2  4  1  3

mapping:
1 ↔ 5
2 ↔ 4
3 ↔ 3
```

Why this is attractive:

``` text
range preserved:
1 <= a[i] <= n
        ↓
1 <= n+1-a[i] <= n

uniqueness preserved:
different a[i]
        ↓
different n+1-a[i]
```

### Recognition sentence

> If every output position can be computed independently, search for a
> direct formula before simulation.

### C++ template

``` cpp
for (int x : a) {
    cout << n + 1 - x << ' ';
}
```

------------------------------------------------------------------------

### Additional Practice --- A1 Direct Formula Construction

1.  [Codeforces 1831A --- Twin
    Permutations](https://codeforces.com/problemset/problem/1831/A) ---
    complement/direct mapping.
2.  [Codeforces 1983A --- Array
    Divisibility](https://codeforces.com/problemset/problem/1983/A) ---
    construct values directly from the index/divisibility rule.
3.  [Codeforces 1325A --- EhAb AnD
    gCd](https://codeforces.com/problemset/problem/1325/A) --- direct
    mathematical construction.
4.  [Codeforces 1337A --- Ichihime and
    Triangle](https://codeforces.com/problemset/problem/1337/A) ---
    choose a direct valid triple.
5.  [Codeforces 1998A --- Find K Distinct Points with Fixed
    Center](https://codeforces.com/problemset/problem/1998/A) --- direct
    symmetric construction.

**Extra source:** CSES has fewer pure A-style constructive tasks; use
[CSES Permutations](https://cses.fi/problemset/task/1070/) after A2 as a
bridge.

------------------------------------------------------------------------

## A2. Identity / Reverse / Rotation

These are the first permutation shapes to test.

``` text
identity:
1 2 3 4 5

reverse:
5 4 3 2 1

left rotation:
2 3 4 5 1

pair swap:
2 1 4 3 6 5
```

### Real-world mapping

Think of seating people:

``` text
identity   -> everyone keeps seat
reverse    -> mirror the row
rotation   -> everyone moves one seat
pair swap  -> swap neighbors
```

### When to try which?

``` text
need increasing structure      -> identity
need opposite order            -> reverse
need p[i] != i                 -> rotation / pair swap
need local partner relation    -> pair swap
need cyclic relation           -> rotation
```

### Important contest habit

Do **not** start by inventing a complicated permutation.

Try:

``` text
identity
reverse
rotation
pair swap
```

first.

------------------------------------------------------------------------

### Additional Practice --- A2 Identity / Reverse / Rotation

1.  [Codeforces 1454A --- Special
    Permutation](https://codeforces.com/problemset/problem/1454/A) ---
    cyclic shift/derangement.
2.  [Codeforces 1711A --- Perfect
    Permutation](https://codeforces.com/problemset/problem/1711/A) ---
    permutation construction.
3.  [Codeforces 1741B --- Funny
    Permutation](https://codeforces.com/problemset/problem/1741/B) ---
    permutation rearrangement with impossible small cases.
4.  [Codeforces 2001B --- Generate
    Permutation](https://codeforces.com/problemset/problem/2001/B) ---
    direct permutation shape.
5.  [CSES --- Permutations](https://cses.fi/problemset/task/1070/) ---
    classic even/odd permutation construction.

------------------------------------------------------------------------

## A3. Repeating Safe Value

If values may repeat and only validity matters, ask whether one safe
value can solve everything.

Example:

``` text
Need sum = 6
Allowed value 1

1 1 1 1 1 1
```

The construction is intentionally boring.

### Key lesson

``` text
"any valid answer"
does NOT mean
"most elegant / shortest / optimal answer"
```

### Recognition sentence

> If repetition is allowed, test whether one universally safe building
> block solves the entire instance.

------------------------------------------------------------------------

### Additional Practice --- A3 Repeating Safe Value

1.  [Codeforces 1845A --- Forbidden
    Integer](https://codeforces.com/problemset/problem/1845/A) ---
    repeated safe building block.
2.  [Codeforces 1343B --- Balanced
    Array](https://codeforces.com/problemset/problem/1343/B) ---
    repetitive base structure plus balance.
3.  [Codeforces 1665A --- GCD vs
    LCM](https://codeforces.com/problemset/problem/1665/A) --- simple
    repeated values chosen to force the relation.
4.  [Codeforces 1916A ---
    2023](https://codeforces.com/problemset/problem/1916/A) --- complete
    a product with simple fillers.
5.  [Codeforces 1886A --- Sum of
    Three](https://codeforces.com/problemset/problem/1886/A) ---
    fixed/simple building blocks with case handling.

------------------------------------------------------------------------

## A4. Tiny Case Split Construction

Sometimes one construction works except for a tiny case.

Example structure:

``` text
if n == 1:
    special
else:
    standard construction
```

or:

``` text
if n is even:
    construction E
else:
    construction O
```

ASCII:

``` text
              n
              |
       +------+------+
       |             |
      even          odd
       |             |
 construction E  construction O
```

### Why A problems use this

The construction itself is easy; the challenge is noticing the one
exceptional case.

### Recognition sentence

> If your simple construction fails only at n=1, n=2, or one parity,
> isolate that case instead of abandoning the construction.

------------------------------------------------------------------------

------------------------------------------------------------------------

### Additional Practice --- A4 Tiny Case Split Construction

1.  [Codeforces 1741B --- Funny
    Permutation](https://codeforces.com/problemset/problem/1741/B) ---
    tiny impossible cases + standard construction.
2.  [Codeforces 1845A --- Forbidden
    Integer](https://codeforces.com/problemset/problem/1845/A) ---
    branch by forbidden value/parity.
3.  [Codeforces 1352B --- Same Parity
    Summands](https://codeforces.com/problemset/problem/1352/B) ---
    parity case split.
4.  [Codeforces 1343B --- Balanced
    Array](https://codeforces.com/problemset/problem/1343/B) ---
    feasibility by `n mod 4`.
5.  [CSES --- Permutations](https://cses.fi/problemset/task/1070/) ---
    `n=2,3` impossible; otherwise direct construction.

------------------------------------------------------------------------

# Part B --- Div2 B Constructive Patterns

B problems often add a second condition:

``` text
simple construction
        +
parity / modulo / adjacency / permutation constraint
```

------------------------------------------------------------------------

## B1. Base + Correction

This is one of the most important constructive patterns.

### Core idea

Build most of the answer with a safe value:

``` text
b b b b b ...
```

Then fix the leftover:

``` text
b b b b c
```

Algebra:

``` text
S = q*b + r
```

The real problem becomes:

``` text
How do I represent the small remainder r?
```

### Dry run

Need:

``` text
S = 13
```

Suppose values 2 and 3 are available.

All 2s:

``` text
2+2+2+2+2+2 = 12
```

Gap:

``` text
13 - 12 = 1
```

Instead of adding 1, replace one `2` by `3`:

``` text
3+2+2+2+2+2 = 13
```

ASCII:

``` text
BASE
[2][2][2][2][2][2]
             sum 12
                |
                | need +1
                v
CORRECT
[3][2][2][2][2][2]
             sum 13
```

### Pattern recognition

``` text
one repeated value almost works
        ↓
failure is only parity/remainder
        ↓
replace/add one small correction
```

------------------------------------------------------------------------

### Additional Practice --- B1 Base + Correction

1.  [Codeforces 1845A --- Forbidden
    Integer](https://codeforces.com/problemset/problem/1845/A)
2.  [Codeforces 1352B --- Same Parity
    Summands](https://codeforces.com/problemset/problem/1352/B)
3.  [Codeforces 1343B --- Balanced
    Array](https://codeforces.com/problemset/problem/1343/B)
4.  [Codeforces 1690A --- Print a
    Pedestal](https://codeforces.com/problemset/problem/1690/A)
5.  [Codeforces 1886A --- Sum of
    Three](https://codeforces.com/problemset/problem/1886/A)

Focus: build a uniform base, identify the remainder/parity failure, then
patch only the small leftover.

------------------------------------------------------------------------

## B2. Alternating Construction

Use when adjacent elements should differ or two classes must interleave.

``` text
A B A B A B
```

Index formula:

``` cpp
ans[i] = (i % 2 == 0 ? A : B);
```

### Why it works

Every adjacent edge is:

``` text
A-B
or
B-A
```

Never:

``` text
A-A
B-B
```

ASCII:

``` text
A -- B -- A -- B -- A -- B
 \__/ \__/ \__/ \__/ \__/
 every edge automatically valid
```

### Recognition sentence

> Local "neighbors must differ" constraints should immediately trigger
> alternating or cyclic shapes.

------------------------------------------------------------------------

### Additional Practice --- B2 Alternating Construction

1.  [Codeforces 1335B --- Construct the
    String](https://codeforces.com/problemset/problem/1335/B)
2.  [Codeforces 1400A --- String
    Similarity](https://codeforces.com/problemset/problem/1400/A)
3.  [Codeforces 2131B --- Alternating
    Series](https://codeforces.com/problemset/problem/2131/B)
4.  [Codeforces 1509A --- Average
    Height](https://codeforces.com/problemset/problem/1509/A) ---
    parity-class arrangement.
5.  [Codeforces 1890A --- Doremy's Paint
    3](https://codeforces.com/problemset/problem/1890/A) ---
    frequency/alternation feasibility.

------------------------------------------------------------------------

## B3. Cyclic Construction

Generalize alternating from period 2 to period `k`.

``` text
1 2 3 1 2 3 1 2 3
```

Formula:

``` cpp
ans[i] = (i % k) + 1;
```

### Mental model

``` text
position modulo k
        ↓
determines class
```

Useful for:

``` text
colors
periodic restrictions
k distinct local states
repeating strings
```

------------------------------------------------------------------------

### Additional Practice --- B3 Cyclic Construction

1.  [Codeforces 1335B --- Construct the
    String](https://codeforces.com/problemset/problem/1335/B)
2.  [Codeforces 1348B --- Phoenix and
    Beauty](https://codeforces.com/problemset/problem/1348/B) ---
    repeated block/cycle construction.
3.  [Codeforces 1927E --- Klever
    Permutation](https://codeforces.com/problemset/problem/1927/E) ---
    structured classes/periodic placement.
4.  [Codeforces 1454A --- Special
    Permutation](https://codeforces.com/problemset/problem/1454/A) ---
    cyclic permutation.
5.  [CSES --- Permutations](https://cses.fi/problemset/task/1070/) ---
    useful bridge from simple periodic thinking to parity classes.

------------------------------------------------------------------------

## B4. Pair Swap

Identity:

``` text
1 2 3 4 5 6
```

Pair swap:

``` text
2 1 4 3 6 5
```

### Why it is powerful

For each pair:

``` text
positions: i   i+1
values:   i+1   i
```

Therefore:

``` text
p[i] != i
p[i+1] != i+1
```

automatically.

### Boundary issue

Odd `n` can leave one element:

``` text
2 1 | 4 3 | 5
              ^
           unpaired
```

That immediately tells you to:

``` text
special-case
use rotation
use a 3-element block
or prove impossible
```

### Recognition sentence

> When a permutation needs a local "not itself" condition, pair swaps
> are one of the first templates to test.

------------------------------------------------------------------------

### Additional Practice --- B4 Pair Swap

1.  [Codeforces 1741B --- Funny
    Permutation](https://codeforces.com/problemset/problem/1741/B)
2.  [Codeforces 1454A --- Special
    Permutation](https://codeforces.com/problemset/problem/1454/A)
3.  [Codeforces 1711A --- Perfect
    Permutation](https://codeforces.com/problemset/problem/1711/A)
4.  [Codeforces 2001B --- Generate
    Permutation](https://codeforces.com/problemset/problem/2001/B)
5.  [Codeforces 1352G --- Special
    Permutation](https://codeforces.com/problemset/problem/1352/G) ---
    harder permutation-shape practice.

------------------------------------------------------------------------

## B5. Rotation / Cyclic Shift

``` text
1 2 3 4 5
        ↓
2 3 4 5 1
```

Every value moves.

For `n > 1`:

``` text
p[i] != i
```

### Compare pair swap vs rotation

``` text
PAIR SWAP
2 1 4 3 6 5
local independent blocks

ROTATION
2 3 4 5 6 1
one global cycle
```

Use rotation when odd size makes pair swapping awkward.

------------------------------------------------------------------------

### Additional Practice --- B5 Rotation / Cyclic Shift

1.  [Codeforces 1454A --- Special
    Permutation](https://codeforces.com/problemset/problem/1454/A)
2.  [Codeforces 1711A --- Perfect
    Permutation](https://codeforces.com/problemset/problem/1711/A)
3.  [Codeforces 1365C --- Rotation
    Matching](https://codeforces.com/problemset/problem/1365/C) ---
    rotation as the central state.
4.  [Codeforces 2001B --- Generate
    Permutation](https://codeforces.com/problemset/problem/2001/B)
5.  [CSES --- Permutations](https://cses.fi/problemset/task/1070/) ---
    compare cyclic-shift thinking with parity-block construction.

------------------------------------------------------------------------

## B6. Block Construction

Split the answer into chunks:

``` text
[1 2 3] [4 5 6] [7 8 9]
```

Transform each chunk:

``` text
[3 2 1] [6 5 4] [9 8 7]
```

### Why blocks help

A global problem becomes repeated local problems.

``` text
BIG ARRAY
   |
   +-- block 1 -> solve
   +-- block 2 -> solve
   +-- block 3 -> solve
```

### Recognition triggers

``` text
condition only spans k positions
groups of fixed size
pair/triple operations
independent segments
```

------------------------------------------------------------------------

### Additional Practice --- B6 Block Construction

1.  [Codeforces 1348B --- Phoenix and
    Beauty](https://codeforces.com/problemset/problem/1348/B)
2.  [Codeforces 1927E --- Klever
    Permutation](https://codeforces.com/problemset/problem/1927/E)
3.  [Codeforces 1862B --- Sequence
    Game](https://codeforces.com/problemset/problem/1862/B) --- insert
    local corrective elements.
4.  [Codeforces 1837D --- Bracket
    Coloring](https://codeforces.com/problemset/problem/1837/D) ---
    partition/construct classes.
5.  [Codeforces 2118B --- Make It
    Permutation](https://codeforces.com/problemset/problem/2118/B) ---
    structured permutation construction.

------------------------------------------------------------------------

## B7. Construct by Frequency

Suppose equal adjacent values are forbidden.

Input multiset:

``` text
A A A B B C
```

Frequency view:

``` text
A:3
B:2
C:1
```

A useful construction idea:

``` text
place dominant values into separated slots

_ B _ C _ B _
^   ^   ^
A   A   A
```

Result:

``` text
A B A C A B
```

### Necessary feasibility clue

If the maximum frequency is too large:

``` text
maxFreq > others + 1
```

separation is impossible.

This pattern bridges:

``` text
frequency counting
        +
constructive placement
```

------------------------------------------------------------------------

### Additional Practice --- B7 Construct by Frequency

1.  [Codeforces 1890A --- Doremy's Paint
    3](https://codeforces.com/problemset/problem/1890/A)
2.  [Codeforces 1506D --- Epic
    Transformation](https://codeforces.com/problemset/problem/1506/D)
3.  [Codeforces 1438B --- Valerii Against
    Everyone](https://codeforces.com/problemset/problem/1438/B)
4.  [Codeforces 1348B --- Phoenix and
    Beauty](https://codeforces.com/problemset/problem/1348/B)
5.  [Codeforces 672B --- Different is
    Good](https://codeforces.com/problemset/problem/672/B)

Focus: reduce values to counts first; construct only after identifying
the dominant frequency.

------------------------------------------------------------------------

## B8. Sort + Construct

Sometimes arbitrary input order hides the structure.

Example:

``` text
8 1 6 3 5 2
```

Sort:

``` text
1 2 3 5 6 8
```

Then build:

``` text
1 8 2 6 3 5
```

or pair neighbors depending on the required property.

### Recognition sentence

> If the output may reorder values freely, sorting can expose the
> building blocks before construction begins.

------------------------------------------------------------------------

------------------------------------------------------------------------

### Additional Practice --- B8 Sort + Construct

1.  [Codeforces 1783A --- Make it
    Beautiful](https://codeforces.com/problemset/problem/1783/A)
2.  [Codeforces 1614B --- Divan and a New
    Project](https://codeforces.com/problemset/problem/1614/B)
3.  [Codeforces 1509A --- Average
    Height](https://codeforces.com/problemset/problem/1509/A)
4.  [Codeforces 1929A --- Sasha and the Beautiful
    Array](https://codeforces.com/problemset/problem/1929/A)
5.  [Codeforces 1833B --- Restore the
    Weather](https://codeforces.com/problemset/problem/1833/B)

------------------------------------------------------------------------

# Part C --- Div2 C Constructive Patterns

C problems commonly combine a construction with a proof or second
technique.

Typical shape:

``` text
constructive
    +
math / greedy / invariant / bits / sorting / number theory
```

------------------------------------------------------------------------

## C1. Greedy Local Repair

### Signal

You process left to right and want to permanently finalize the prefix.

``` text
valid valid valid BAD ? ? ?
                  ^
               repair
```

### Framework

At index `i`:

``` text
1. Is current value valid with the fixed prefix?
2. If yes -> keep it.
3. If no  -> make smallest safe change.
4. Never revisit the prefix.
```

ASCII:

``` text
before:
[✓][✓][✓][X][?][?]

repair:
[✓][✓][✓][✓][?][?]
             ^
       smallest safe choice

continue...
```

### Proof checklist

Local repair is safe when:

``` text
prefix remains valid
current repair satisfies local condition
repair cannot damage earlier positions
smallest safe choice leaves suffix flexibility
```

### Recognition sentence

> If only the current boundary can become invalid, repair that boundary
> rather than rebuilding the entire answer.

------------------------------------------------------------------------

### Additional Practice --- C1 Greedy Local Repair

1.  [Codeforces 1794B --- Not
    Dividing](https://codeforces.com/problemset/problem/1794/B)
2.  [Codeforces 1862B --- Sequence
    Game](https://codeforces.com/problemset/problem/1862/B)
3.  [Codeforces 1624C --- Division by Two and
    Permutation](https://codeforces.com/problemset/problem/1624/C)
4.  [Codeforces 2110C ---
    Racing](https://codeforces.com/problemset/problem/2110/C)
5.  [Codeforces 1837D --- Bracket
    Coloring](https://codeforces.com/problemset/problem/1837/D)

------------------------------------------------------------------------

## C2. Invariant-Guided Construction

Sometimes you first discover what **must** remain true.

Example abstractly:

``` text
required total XOR = X
```

Then construction must preserve that invariant.

Or:

``` text
required parity = odd
```

Then choose components whose parity combination is odd.

Pipeline:

``` text
find invariant/necessary condition
        ↓
choose construction family
        ↓
ensure every construction step preserves condition
```

### Example parity design

Need an odd total.

``` text
even + even + even = even   X

odd + even + even = odd     ✓
```

So the structure becomes:

``` text
one odd correction
+
remaining even blocks
```

This is **math guiding construction**.

------------------------------------------------------------------------

### Additional Practice --- C2 Invariant-Guided Construction

1.  [Codeforces 1909B --- Make Almost Equal With
    Mod](https://codeforces.com/problemset/problem/1909/B)
2.  [Codeforces 1837D --- Bracket
    Coloring](https://codeforces.com/problemset/problem/1837/D)
3.  [Codeforces 1375C --- Element
    Extermination](https://codeforces.com/problemset/problem/1375/C)
4.  [Codeforces 1542B --- Plus and
    Multiply](https://codeforces.com/problemset/problem/1542/B)
5.  [Codeforces 1433D --- Districts
    Connection](https://codeforces.com/problemset/problem/1433/D)

------------------------------------------------------------------------

## C3. Reverse Construction

Forward building may have many choices.

``` text
start
 / | \
?  ?  ?
```

But the target may reveal the previous step.

``` text
target
  |
forced predecessor
  |
forced predecessor
```

### Example mental model

If an operation forward is:

``` text
x -> 2x
```

then when target is even:

``` text
target -> target/2
```

is naturally forced backward.

If construction constraints depend on the suffix:

``` text
a[i] depends on a[i+1]
```

build:

``` text
a[n]
a[n-1]
...
a[1]
```

### Recognition sentence

> If choosing the next element forward creates many branches, ask
> whether the last element or predecessor is more constrained.

------------------------------------------------------------------------

### Additional Practice --- C3 Reverse Construction

1.  [Codeforces 1624C --- Division by Two and
    Permutation](https://codeforces.com/problemset/problem/1624/C)
2.  [Codeforces 1862B --- Sequence
    Game](https://codeforces.com/problemset/problem/1862/B)
3.  [Codeforces 1909B --- Make Almost Equal With
    Mod](https://codeforces.com/problemset/problem/1909/B)
4.  [Codeforces 1352G --- Special
    Permutation](https://codeforces.com/problemset/problem/1352/G)
5.  [Codeforces 1927E --- Klever
    Permutation](https://codeforces.com/problemset/problem/1927/E)

Use these to practice asking whether the target/final structure is
easier to reason about than forward simulation.

------------------------------------------------------------------------

## C4. Permutation by Position Classes

Split positions by a property:

``` text
odd positions
even positions
```

or:

``` text
i % k = 0
i % k = 1
...
```

Then assign values separately.

ASCII:

``` text
positions:
1 2 3 4 5 6 7 8

class:
O E O E O E O E

construct:
A B A B A B A B
```

This becomes stronger when values also have classes:

``` text
odd values
even values
```

Then you can match:

``` text
position class ↔ value class
```

### Typical C combination

``` text
parity
+
permutation
+
constructive assignment
```

------------------------------------------------------------------------

### Additional Practice --- C4 Permutation by Position Classes

1.  [Codeforces 1927E --- Klever
    Permutation](https://codeforces.com/problemset/problem/1927/E)
2.  [Codeforces 1352G --- Special
    Permutation](https://codeforces.com/problemset/problem/1352/G)
3.  [Codeforces 1822D ---
    Super-Permutation](https://codeforces.com/problemset/problem/1822/D)
4.  [Codeforces 2118B --- Make It
    Permutation](https://codeforces.com/problemset/problem/2118/B)
5.  [CSES --- Permutations](https://cses.fi/problemset/task/1070/) ---
    canonical parity-class construction.

------------------------------------------------------------------------

## C5. Extreme / Zig-Zag Construction

Sort:

``` text
1 2 3 4 5 6 7 8
```

Take:

``` text
smallest, largest, second-smallest, second-largest...
```

Result:

``` text
1 8 2 7 3 6 4 5
```

ASCII:

``` text
1 2 3 4 5 6 7 8
^             ^
|-------------|
  take both

  ^         ^
  |---------|
   next pair
```

### Why?

This intentionally creates large local differences.

Opposite strategy---pair nearby values---creates small local
differences.

So ask:

``` text
Do I want neighbors:
far apart?  -> extremes
close?      -> sorted neighbors
```

------------------------------------------------------------------------

### Additional Practice --- C5 Extreme / Zig-Zag Construction

1.  [Codeforces 1783A --- Make it
    Beautiful](https://codeforces.com/problemset/problem/1783/A)
2.  [Codeforces 1352G --- Special
    Permutation](https://codeforces.com/problemset/problem/1352/G)
3.  [Codeforces 1929A --- Sasha and the Beautiful
    Array](https://codeforces.com/problemset/problem/1929/A)
4.  [Codeforces 1833B --- Restore the
    Weather](https://codeforces.com/problemset/problem/1833/B)
5.  [Codeforces 1614B --- Divan and a New
    Project](https://codeforces.com/problemset/problem/1614/B)

------------------------------------------------------------------------

## C6. Greedy Matching Construction

Suppose you must assign values from one array to positions/requirements
of another.

Common pattern:

``` text
sort requirements
sort available values
        ↓
match smallest feasible
or
match extremes
```

Why sorting helps:

``` text
hardest / smallest / largest requirements
become ordered
```

Then greedy assignment can be proved by exchange.

### Exchange proof skeleton

Assume two assignments:

``` text
A -> x
B -> y
```

Compare with swapped:

``` text
A -> y
B -> x
```

Show one ordering is never worse.

Then that ordering is safe globally.

------------------------------------------------------------------------

### Additional Practice --- C6 Greedy Matching Construction

1.  [Codeforces 1833B --- Restore the
    Weather](https://codeforces.com/problemset/problem/1833/B)
2.  [Codeforces 1909C --- Heavy
    Intervals](https://codeforces.com/problemset/problem/1909/C)
3.  [Codeforces 1624C --- Division by Two and
    Permutation](https://codeforces.com/problemset/problem/1624/C)
4.  [Codeforces 1506D --- Epic
    Transformation](https://codeforces.com/problemset/problem/1506/D)
5.  [Codeforces 1348B --- Phoenix and
    Beauty](https://codeforces.com/problemset/problem/1348/B)

------------------------------------------------------------------------

## C7. Constructive + GCD / Divisibility

The desired relation may be:

``` text
gcd(a[i], a[j]) > 1
a[i] divides a[j]
a[i] does not divide a[j]
```

Instead of random numbers, choose values with deliberately controlled
prime factors.

Example design language:

``` text
want common gcd > 1
        ↓
give both values factor 2

want gcd = 1
        ↓
choose coprime components

want divisibility chain
        ↓
x, 2x, 4x, 8x...
```

ASCII:

``` text
12 = 2² * 3
18 = 2  * 3²
      ^    ^
 shared prime factors
```

### Recognition sentence

> If the required relation is divisibility/GCD, construct through prime
> factors rather than through decimal values.

------------------------------------------------------------------------

### Additional Practice --- C7 Constructive + GCD / Divisibility

1.  [Codeforces 1325A --- EhAb AnD
    gCd](https://codeforces.com/problemset/problem/1325/A)
2.  [Codeforces 1665A --- GCD vs
    LCM](https://codeforces.com/problemset/problem/1665/A)
3.  [Codeforces 1389A --- LCM
    Problem](https://codeforces.com/problemset/problem/1389/A)
4.  [Codeforces 1933D --- Turtle Tenacity: Continual
    Mods](https://codeforces.com/problemset/problem/1933/D)
5.  [Codeforces 1542B --- Plus and
    Multiply](https://codeforces.com/problemset/problem/1542/B)

------------------------------------------------------------------------

## C8. Constructive + XOR / Bits

XOR construction is easiest when you use cancellation:

``` text
x ^ x = 0
```

Need total XOR `T`:

``` text
T ^ x ^ x = T
```

So equal pairs are neutral blocks.

ASCII:

``` text
TARGET
  T
  |
  + [x][x] -> contributes 0
  |
  + [y][y] -> contributes 0
```

This is exactly:

``` text
base answer
+
neutral blocks
```

At bit level, you can also choose numbers to set specific bits
deliberately.

------------------------------------------------------------------------

### Additional Practice --- C8 Constructive + XOR / Bits

1.  [Codeforces 2108B --- SUMdamental
    Decomposition](https://codeforces.com/problemset/problem/2108/B)
2.  [Codeforces 2119C --- A Good
    Problem](https://codeforces.com/problemset/problem/2119/C)
3.  [Codeforces 2245C ---
    MEXOR](https://codeforces.com/problemset/problem/2245/C)
4.  [Codeforces 1909B --- Make Almost Equal With
    Mod](https://codeforces.com/problemset/problem/1909/B)
5.  [Codeforces 1998A --- Find K Distinct Points with Fixed
    Center](https://codeforces.com/problemset/problem/1998/A) --- useful
    neutral-pair construction intuition.

------------------------------------------------------------------------

## C9. Prefix/Suffix-Guided Construction

Sometimes a position must satisfy something relative to everything
before it.

Maintain a prefix state:

``` text
prefix sum
prefix max
prefix xor
prefix gcd
```

Then choose the next value based on that state.

Example abstractly:

``` text
need a[i] > prefixSum
```

Choose:

``` text
a[i] = prefixSum + 1
```

Now the condition is true by design.

ASCII:

``` text
constructed prefix
[---------]
sum = S

next:
[S + 1]

new element dominates old total
```

This is a common transition from B constructive into C.

------------------------------------------------------------------------

### Additional Practice --- C9 Prefix/Suffix-Guided Construction

1.  [Codeforces 1916C --- Training Before the
    Olympiad](https://codeforces.com/problemset/problem/1916/C)
2.  [Codeforces 1923C --- Find
    B](https://codeforces.com/problemset/problem/1923/C)
3.  [Codeforces 1348B --- Phoenix and
    Beauty](https://codeforces.com/problemset/problem/1348/B)
4.  [Codeforces 1375C --- Element
    Extermination](https://codeforces.com/problemset/problem/1375/C)
5.  [Codeforces 1907D --- Jumping Through
    Segments](https://codeforces.com/problemset/problem/1907/D) ---
    interval/prefix feasibility bridge.

------------------------------------------------------------------------

## C10. Build the Answer, Not the Operations

A frequent trap:

``` text
problem describes many allowed operations
```

and you start simulating them.

But if it asks only for the final object, ask:

``` text
Can I directly construct a reachable final state?
```

Compare:

``` text
WRONG MENTAL PATH

initial
  ↓ op
state
  ↓ op
state
  ↓ op
...
final

BETTER

necessary final properties
        ↓
direct construction
```

Only output operations when the statement explicitly requires the
operation sequence.

------------------------------------------------------------------------

### Additional Practice --- C10 Build the Answer, Not the Operations

1.  [Codeforces 1862B --- Sequence
    Game](https://codeforces.com/problemset/problem/1862/B)
2.  [Codeforces 1794B --- Not
    Dividing](https://codeforces.com/problemset/problem/1794/B)
3.  [Codeforces 2085B --- Serval and Final
    MEX](https://codeforces.com/problemset/problem/2085/B)
4.  [Codeforces 1348B --- Phoenix and
    Beauty](https://codeforces.com/problemset/problem/1348/B)
5.  [Codeforces 1433D --- Districts
    Connection](https://codeforces.com/problemset/problem/1433/D)

------------------------------------------------------------------------

## C11. Construct Operations Backward

If operations themselves must be printed, sometimes the final desired
structure is easier to create backward.

Mental model:

``` text
desired final state
        ↓
what last operation could create this?
        ↓
remove that requirement
        ↓
repeat
        ↓
reverse operation list
```

This is constructive + reverse thinking.

------------------------------------------------------------------------

### Additional Practice --- C11 Construct Operations Backward

1.  [Codeforces 1624C --- Division by Two and
    Permutation](https://codeforces.com/problemset/problem/1624/C)
2.  [Codeforces 2085B --- Serval and Final
    MEX](https://codeforces.com/problemset/problem/2085/B)
3.  [Codeforces 1862B --- Sequence
    Game](https://codeforces.com/problemset/problem/1862/B)
4.  [Codeforces 1909B --- Make Almost Equal With
    Mod](https://codeforces.com/problemset/problem/1909/B)
5.  [Codeforces 1542B --- Plus and
    Multiply](https://codeforces.com/problemset/problem/1542/B)

These are bridge exercises: explicitly write the desired final property
first, then reason backward before coding.

------------------------------------------------------------------------

## C12. Make Constraints Automatic

This is the deepest constructive habit.

Do not repeatedly check:

``` text
is condition 1 valid?
is condition 2 valid?
is condition 3 valid?
```

Choose a shape where conditions are consequences.

Example:

``` text
Need neighbors different.

Choose:
1 2 1 2 1 2

Now:
a[i] != a[i+1]
```

is not something you *check*.

It is something guaranteed by the pattern.

For C, aim for:

``` text
constraint
    ↓
structural property
    ↓
template guaranteeing property
```

------------------------------------------------------------------------

------------------------------------------------------------------------

### Additional Practice --- C12 Make Constraints Automatic

1.  [Codeforces 1352G --- Special
    Permutation](https://codeforces.com/problemset/problem/1352/G)
2.  [Codeforces 1822D ---
    Super-Permutation](https://codeforces.com/problemset/problem/1822/D)
3.  [Codeforces 1837D --- Bracket
    Coloring](https://codeforces.com/problemset/problem/1837/D)
4.  [Codeforces 1927E --- Klever
    Permutation](https://codeforces.com/problemset/problem/1927/E)
5.  [CSES --- Permutations](https://cses.fi/problemset/task/1070/)

For each, ask: "What shape makes the condition true without checking
every pair/position?"

------------------------------------------------------------------------

# Shared Impossibility Proofs

A constructive problem has two jobs:

``` text
1. Detect impossible cases.
2. Construct all possible cases.
```

Learn these impossibility patterns.

------------------------------------------------------------------------

## 7.1 Parity contradiction

Need odd sum but all usable numbers are even:

``` text
even + even + ... + even = even
```

Impossible.

------------------------------------------------------------------------

## 7.2 Modulo contradiction

Every usable piece:

``` text
≡ 0 mod 3
```

Target:

``` text
≡ 1 mod 3
```

Impossible.

------------------------------------------------------------------------

## 7.3 Frequency dominance

Need no equal adjacent elements.

Suppose:

``` text
A A A A A B C
```

Five `A`, only two non-A elements.

Slots:

``` text
_ B _ C _
```

Only 3 safe slots for A if no two A may touch.

Need 5.

Impossible.

General condition for separating a dominant value:

``` text
maxFreq <= others + 1
```

------------------------------------------------------------------------

## 7.4 Invariant mismatch

Start parity:

``` text
odd
```

Every operation preserves parity.

Target:

``` text
even
```

Impossible.

------------------------------------------------------------------------

## 7.5 Range contradiction

Allowed pieces are at least `L`.

Using `m` pieces:

``` text
sum >= mL
```

If the target is smaller, impossible.

Likewise with upper bounds.

------------------------------------------------------------------------

------------------------------------------------------------------------

------------------------------------------------------------------------

# A/B/C Pattern Recognition Matrix

  Signal in statement              First construction to test        Typical level
  -------------------------------- --------------------------------- ---------------
  print any valid array            all same / simple formula         A
  construct permutation            identity / reverse / rotation     A
  complement/opposite values       direct formula                    A
  parity differs by case           even/odd case split               A/B
  repeated value almost works      base + correction                 B
  adjacent values differ           alternating                       B
  period / k classes               cyclic                            B
  avoid fixed points               pair swap / rotation              B
  local constraints in groups      blocks                            B
  dominant frequency               separated-slot construction       B/C
  arbitrary values can reorder     sort + construct                  B/C
  valid prefix + first violation   local repair                      C
  reachability constraint          invariant-guided construction     C
  forward branching                reverse construction              C
  positions split by parity/mod    class-based assignment            C
  maximize local differences       zig-zag extremes                  C
  assignment between arrays        sort + greedy matching            C
  gcd/divisibility relation        prime-factor construction         C
  target XOR                       neutral XOR blocks / bit design   C
  next value depends on prefix     prefix-guided construction        C

------------------------------------------------------------------------

------------------------------------------------------------------------

# Constructive Proof Patterns

A construction is incomplete until you can explain why it works.

## Proof 1 --- By Formula

``` text
We choose b[i] = ...
Therefore condition becomes ...
which is always true.
```

## Proof 2 --- By Parity / Modulo

``` text
Base blocks contribute 0 mod k.
Correction contributes r mod k.
Therefore total has required remainder.
```

## Proof 3 --- By Local Structure

``` text
Every adjacent pair is A-B or B-A.
Therefore equal adjacent values never occur.
```

## Proof 4 --- By Blocks

``` text
Each block independently satisfies the condition.
No condition crosses block boundaries.
Therefore the full construction is valid.
```

## Proof 5 --- By Greedy Prefix

``` text
Before step i, prefix is valid.
Our choice at i preserves the prefix and satisfies boundary (i-1,i).
Therefore prefix through i is valid.
Inductively, the entire array is valid.
```

## Proof 6 --- By Invariant

``` text
Necessary invariant is I.
Our construction has invariant I.
Every other explicit constraint is satisfied.
Therefore construction is valid.
```

------------------------------------------------------------------------

------------------------------------------------------------------------

# Failure Analysis --- Why a Construction Does Not Work

When a template fails, classify the failure.

``` text
FAILED CONSTRUCTION
        |
        +-- parity mismatch?
        +-- wrong remainder?
        +-- leftover unpaired element?
        +-- boundary between blocks?
        +-- duplicate/frequency overload?
        +-- range violation?
        +-- fixed point?
        +-- gcd/divisibility failure?
        +-- prefix condition broken?
```

Do not immediately throw away the whole idea.

Often:

``` text
good base
+
one identified failure
=
small correction
```

------------------------------------------------------------------------

------------------------------------------------------------------------

# 60-Second Constructive Contest Decoder

``` text
0–10 sec
What exactly must I output?
Any valid object or optimal object?

10–20 sec
Write constraints only.
Remove story.

20–30 sec
Try shapes:
same
formula
identity
reverse
rotation
pair swap
alternating
cycle

30–40 sec
What fails?
parity?
remainder?
boundary?
leftover?
frequency?

40–50 sec
Can one correction / block / greedy repair fix it?

50–60 sec
Proof:
Why is EVERY constraint automatic?
```

------------------------------------------------------------------------

------------------------------------------------------------------------

# Recommended Learning Order Inside Constructive

``` text
STAGE 1 — Div2 A
direct formula
identity / reverse
rotation
repeating safe value
tiny case split

        ↓

STAGE 2 — Div2 B
base + correction
alternating
cyclic
pair swap
blocks
frequency placement
sort + construct

        ↓

STAGE 3 — Div2 C
greedy local repair
invariant-guided construction
reverse construction
position/value classes
zig-zag extremes
greedy matching

        ↓

STAGE 4 — C combinations
constructive + math
constructive + gcd
constructive + XOR
constructive + prefix/suffix
constructive + greedy
constructive + sorting
```

------------------------------------------------------------------------

------------------------------------------------------------------------

# Practice Method

For each constructive problem, before coding, fill this:

``` text
OUTPUT:
____________________________

CONSTRAINTS:
1.
2.
3.

SIMPLEST SHAPE:
____________________________

WHY DOES IT FAIL?
____________________________

CORRECTION:
____________________________

IMPOSSIBLE CASE:
____________________________

ONE-SENTENCE PROOF:
____________________________
```

After solving, store only:

``` text
TRIGGER  -> TEMPLATE -> CORRECTION -> PROOF
```

Example:

``` text
adjacent different
    ->
alternating
    ->
none
    ->
every edge joins opposite classes
```

This is much more useful for contest recall than memorizing the full
editorial.

------------------------------------------------------------------------

------------------------------------------------------------------------

# Final A/B/C Constructive Map

``` text
DIV2 A
"Can I print a direct/simple shape?"
        |
        +-- formula
        +-- same value
        +-- identity
        +-- reverse
        +-- rotation
        +-- tiny case split

DIV2 B
"Simple shape almost works — what correction is missing?"
        |
        +-- parity/remainder correction
        +-- alternating/cyclic
        +-- pair swap
        +-- blocks
        +-- frequency placement
        +-- sort + construct

DIV2 C
"What second idea controls the construction?"
        |
        +-- greedy local repair
        +-- invariant
        +-- reverse thinking
        +-- position classes
        +-- extreme arrangement
        +-- matching
        +-- gcd/divisibility
        +-- XOR/bits
        +-- prefix/suffix
```

The target recognition habit is:

``` text
A:
"What simple answer works?"

B:
"What simple pattern works, and what small correction is needed?"

C:
"What invariant / greedy / math idea tells me HOW to construct?"
```

------------------------------------------------------------------------
