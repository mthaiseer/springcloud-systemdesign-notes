# Part 19 --- Constructive Mathematics

> **Goal:** learn to turn "print any valid object" problems into
> mathematical constraints, derive a construction, prove that the
> construction always stays valid, and implement it quickly.
>
> **Core workflow:**
> `Required properties → Necessary conditions → Choose a structure → Maintain invariant → Construct → Verify`
>
> **Recognition question:** **I do not need the best answer --- can I
> directly build any answer satisfying all constraints?**

## Table of Contents

-   [19.0 Constructive Mental Model](#190-constructive-mental-model)
-   [19.1 What Constructive Problems Actually
    Ask](#191-what-constructive-problems-actually-ask)
-   [19.2 Necessary vs Sufficient
    Conditions](#192-necessary-vs-sufficient-conditions)
-   [19.3 Invariant-Driven
    Construction](#193-invariant-driven-construction)
-   [19.4 Extremal Construction](#194-extremal-construction)
-   [19.5 Alternating Construction](#195-alternating-construction)
-   [19.6 Pairing Construction](#196-pairing-construction)
-   [19.7 Permutation Construction](#197-permutation-construction)
-   [19.8 Parity Construction](#198-parity-construction)
-   [19.9 Modular Construction](#199-modular-construction)
-   [19.10 Sum and Difference
    Construction](#1910-sum-and-difference-construction)
-   [19.11 Fixed-Sum Construction](#1911-fixed-sum-construction)
-   [19.12 Range and Bound
    Construction](#1912-range-and-bound-construction)
-   [19.13 Gap and Spacing
    Construction](#1913-gap-and-spacing-construction)
-   [19.14 Frequency Construction](#1914-frequency-construction)
-   [19.15 String Construction](#1915-string-construction)
-   [19.16 Matrix and Grid
    Construction](#1916-matrix-and-grid-construction)
-   [19.17 Graph Construction](#1917-graph-construction)
-   [19.18 Construct by Complement](#1918-construct-by-complement)
-   [19.19 Construct Backwards](#1919-construct-backwards)
-   [19.20 Build Small Blocks and
    Repeat](#1920-build-small-blocks-and-repeat)
-   [19.21 Lower Bound + Construction](#1921-lower-bound--construction)
-   [19.22 Impossibility Proofs](#1922-impossibility-proofs)
-   [19.23 How to Search for a
    Construction](#1923-how-to-search-for-a-construction)
-   [19.24 How to Verify a
    Construction](#1924-how-to-verify-a-construction)
-   [19.25 Constructive vs Greedy vs
    DP](#1925-constructive-vs-greedy-vs-dp)
-   [19.26 Codeforces Recognition Map](#1926-codeforces-recognition-map)
-   [19.27 Common Mistakes](#1927-common-mistakes)
-   [19.28 60-Second Constructive
    Workflow](#1928-60-second-constructive-workflow)
-   [19.29 Fast Revision Card](#1929-fast-revision-card)

------------------------------------------------------------------------

## 19.0 Constructive Mental Model

A constructive problem usually says:

``` text
print ANY valid answer
construct a permutation
build a string
output a matrix
find any sequence satisfying ...
```

The key shift is:

``` text
Do not search every possibility.

Understand the constraints
        ↓
design a structure
        ↓
prove the structure works
        ↓
print it
```

### Mathematical model

If the output is `X` and the required properties are:

``` text
P1(X)
P2(X)
P3(X)
```

then your job is simply:

``` text
find one X such that

P1(X) AND P2(X) AND P3(X)
```

You usually do **not** need to optimize anything.

### Real-World Example --- Seating guests

You need any seating arrangement where two noisy guests are not
adjacent.

``` text
quiet = Q
noisy = N

Q Q Q
```

Create gaps:

``` text
_ Q _ Q _ Q _
```

Place noisy guests in separate gaps:

``` text
N Q N Q Q
```

**Idea:** do not enumerate all seatings; build one arrangement whose
structure guarantees validity.

------------------------------------------------------------------------

## 19.1 What Constructive Problems Actually Ask

Optimization:

``` text
find BEST valid answer
```

Constructive:

``` text
find ANY valid answer
```

This changes the mindset.

Instead of:

``` text
Which candidate is optimal?
```

ask:

``` text
What simple pattern automatically satisfies the constraints?
```

### Example

Construct a permutation of `1..5` with no element remaining in its
original position.

One construction:

``` text
original: 1 2 3 4 5
output:   2 3 4 5 1
```

Every value moved.

### Real-World Example --- Assigning lockers

Five students cannot receive their original locker.

``` text
Student:  1 2 3 4 5
Locker:   2 3 4 5 1
```

Nobody gets their old locker.

**Idea:** you only need one valid reassignment, not the "best"
reassignment.

------------------------------------------------------------------------

## 19.2 Necessary vs Sufficient Conditions

A **necessary condition** must hold for every solution.

A **sufficient condition** guarantees that your construction works.

``` text
necessary:
solution => condition

sufficient:
condition => construction works
```

The strongest constructive approach is often:

``` text
derive necessary condition
        +
show construction whenever it holds
        =
complete solution
```

### Example --- separate all `1`s

Suppose a binary string must contain `a` zeros and `b` ones with no
adjacent `1`.

Place zeros:

``` text
_ 0 _ 0 _ 0 _
```

Number of safe gaps:

``` text
a + 1
```

Therefore:

``` text
b <= a + 1
```

is necessary and sufficient.

### Real-World Example --- Parking motorcycles between cars

Cars act as separators:

``` text
_ C _ C _ C _
```

There are `cars + 1` separated motorcycle positions.

If motorcycles exceed those slots, separation is impossible.

**Idea:** count available safe slots, then construct directly when
enough slots exist.

------------------------------------------------------------------------

## 19.3 Invariant-Driven Construction

An invariant is a property you keep true after every construction step.

``` text
start valid
   ↓
add one object
   ↓
invariant still true
   ↓
repeat
```

### Example

Build a binary string with no adjacent `1`.

Invariant:

``` text
constructed prefix never contains "11"
```

At each step, only append `1` when the previous character is not `1`.

### Real-World Example --- Building a brick wall

Rule:

``` text
two weak bricks cannot be adjacent
```

While building:

``` text
Strong Weak Strong Weak ...
```

After placing every brick, check:

``` text
last two bricks are not both weak
```

**Idea:** if every prefix is valid and the invariant is preserved, the
final object is valid.

------------------------------------------------------------------------

## 19.4 Extremal Construction

Sometimes the easiest construction starts from extremes:

``` text
smallest
largest
second smallest
second largest
...
```

### Example

Numbers:

``` text
1 2 3 4 5 6
```

Construct:

``` text
1 6 2 5 3 4
```

This deliberately creates large-small alternation.

### Real-World Example --- Balancing people by height

Line people up:

``` text
shortest, tallest, second-shortest, second-tallest...
```

Example:

``` text
150, 190, 155, 185, 160, 180
```

**Idea:** extremes give you predictable differences and are useful when
neighboring values must be far apart or balanced.

------------------------------------------------------------------------

## 19.5 Alternating Construction

When adjacent equal types cause trouble, alternate categories.

``` text
A B A B A B
```

### Feasibility

If counts are:

``` text
countA = a
countB = b
```

for strict separation of the majority:

``` text
max(a,b) <= min(a,b) + 1
```

### Example

``` text
A=4
B=3

A B A B A B A
```

### Real-World Example --- Scheduling two teams

Two teams should not receive consecutive turns.

``` text
Team A: 4 tasks
Team B: 3 tasks
```

Schedule:

``` text
A B A B A B A
```

**Idea:** alternation converts an adjacency restriction into a simple
count condition.

------------------------------------------------------------------------

## 19.6 Pairing Construction

If objects naturally cancel, balance, or complement one another,
construct in pairs.

### Example

Need pairs with equal sum from `1..6`.

``` text
1 + 6 = 7
2 + 5 = 7
3 + 4 = 7
```

General complementary pairing:

``` text
i pairs with n+1-i
```

### Real-World Example --- Pairing light and heavy boxes

Weights:

``` text
1 2 3 4 5 6
```

Pair:

``` text
1 + 6
2 + 5
3 + 4
```

Each cart receives total weight `7`.

**Idea:** look for a constant such as `x + y = C` and pair complements.

------------------------------------------------------------------------

## 19.7 Permutation Construction

A permutation construction must use every number exactly once.

Checklist:

``` text
range      = 1..n
duplicates = none
missing    = none
extra      = none
```

Useful patterns:

``` text
rotation
reverse
odd then even
extremes
block swaps
```

### Example --- cyclic shift

``` text
1 2 3 4 5
        ↓
2 3 4 5 1
```

### Real-World Example --- Rotating workstations

Employees:

``` text
1 2 3 4 5
```

Move everyone to the next desk:

``` text
2 3 4 5 1
```

Every desk is used exactly once and nobody stays at the original desk.

**Idea:** permutations are often easiest to construct using a
transformation that automatically preserves uniqueness.

------------------------------------------------------------------------

## 19.8 Parity Construction

Parity gives only two states:

``` text
even
odd
```

Useful facts:

``` text
even + even = even
odd  + odd  = even
even + odd  = odd
```

Construction often reduces to controlling counts of odd/even elements.

### Example

Need pairs with even sum.

Valid pair types:

``` text
even + even
odd  + odd
```

So pair numbers inside the same parity group.

### Real-World Example --- Pairing batteries

Imagine batteries are labeled odd/even and a device accepts pairs whose
labels sum to even.

``` text
odd  + odd  -> accepted
even + even -> accepted
odd  + even -> rejected
```

**Idea:** group by parity first; the construction becomes obvious.

------------------------------------------------------------------------

## 19.9 Modular Construction

Modulo partitions numbers into residue classes.

For modulus `m`:

``` text
x = qm + r
0 <= r < m
```

Many constructive constraints depend only on `r`.

### Example

Need pairs whose sum is divisible by `5`.

If:

``` text
x % 5 = r
```

pair with residue:

``` text
(5-r) % 5
```

Residue pairs:

``` text
0 ↔ 0
1 ↔ 4
2 ↔ 3
```

### Real-World Example --- Packing items into boxes of 5

An order leaves remainder `2` after full boxes.

Another leaves remainder `3`.

Together:

``` text
2 + 3 = 5
```

so their leftovers form one complete box.

**Idea:** construct with complementary residues rather than raw values.

------------------------------------------------------------------------

## 19.10 Sum and Difference Construction

Many constructive statements hide equations such as:

``` text
x + y = S
x - y = D
```

Solve:

``` text
x = (S + D) / 2
y = (S - D) / 2
```

Then verify integrality and bounds.

### Example

``` text
S = 10
D = 4

x = 7
y = 3
```

### Real-World Example --- Splitting money

Two people together have `100` lei and one has `20` lei more.

``` text
x + y = 100
x - y = 20
```

Therefore:

``` text
x = 60
y = 40
```

**Idea:** turn verbal relationships into equations, solve, then
construct the requested values.

------------------------------------------------------------------------

## 19.11 Fixed-Sum Construction

If you need `k` values with total `S`, start from a simple baseline.

### Pattern

Give each item the minimum allowed value `L`:

``` text
base sum = kL
remaining = S - kL
```

Then distribute the remainder while respecting upper bounds.

### Example

Construct `4` positive integers summing to `10`.

Start:

``` text
1 1 1 1
sum = 4
remaining = 6
```

One construction:

``` text
7 1 1 1
```

if no upper bound exists.

### Real-World Example --- Dividing 10 candies among 4 children

Everyone must get at least one.

``` text
give 1 each:
1 1 1 1

6 candies remain
```

Distribute the remainder according to any other constraints.

**Idea:** satisfy mandatory minimums first, then distribute free
capacity.

------------------------------------------------------------------------

## 19.12 Range and Bound Construction

When every value must satisfy:

``` text
L <= ai <= R
```

first calculate total capacity.

For `n` elements:

``` text
minimum possible sum = nL
maximum possible sum = nR
```

So target sum `S` is possible only if:

``` text
nL <= S <= nR
```

### Construction

Start:

``` text
L L L ... L
```

Remaining:

``` text
S - nL
```

Add to each element up to:

``` text
R-L
```

until remainder becomes zero.

### Real-World Example --- Filling water bottles

Five bottles must contain between `2L` and `5L`, with total `17L`.

``` text
minimum = 5*2 = 10
maximum = 5*5 = 25
```

`17` is feasible.

Start:

``` text
2 2 2 2 2
```

Distribute remaining `7L`.

**Idea:** bounds become total minimum/maximum capacity.

------------------------------------------------------------------------

## 19.13 Gap and Spacing Construction

Spacing restrictions often become a **slot-counting** problem.

### Example

Place `k` special objects so no two are adjacent among `n` positions.

Maximum number of non-adjacent selected positions:

``` text
ceil(n/2)
```

One pattern:

``` text
X . X . X . X
```

### Real-World Example --- Planting trees

Trees need one empty meter between them.

``` text
T . T . T . T
```

Use alternating positions.

**Idea:** construct the separators first or reserve gaps explicitly.

------------------------------------------------------------------------

## 19.14 Frequency Construction

Sometimes only counts matter.

Suppose symbols have frequencies:

``` text
A -> 4
B -> 3
C -> 2
```

Instead of thinking about individual copies, construct from the counts.

### Example

To avoid equal adjacent symbols, repeatedly place different available
symbols while maintaining:

``` text
largest remaining frequency
<=
all other remaining symbols + 1
```

### Real-World Example --- Mixing colored tiles

Tiles:

``` text
Red  = 4
Blue = 3
Gold = 2
```

You want to avoid adjacent red tiles.

Treat all red tiles as one frequency count rather than as separate
objects.

**Idea:** compress identical objects into frequencies; construct using
counts.

------------------------------------------------------------------------

## 19.15 String Construction

Strings are often built from:

``` text
counts
runs
gaps
prefix constraints
periodic blocks
```

### Example

Need a binary string with `3` ones and `4` zeros, no adjacent ones.

``` text
0 0 0 0
```

Create gaps:

``` text
_0_0_0_0_
```

Insert ones:

``` text
1 0 1 0 1 0 0
```

### Real-World Example --- Work/rest calendar

`W` = work, `R` = rest. No two intense work days may be adjacent.

``` text
R R R R
```

Create slots:

``` text
_ R _ R _ R _ R _
```

Insert `W` days into separate slots.

**Idea:** for adjacency constraints, gaps are often more useful than
positions.

------------------------------------------------------------------------

## 19.16 Matrix and Grid Construction

Grid construction usually relies on a repeating pattern.

Useful patterns:

``` text
checkerboard
stripes
cyclic shifts
row parity
column parity
diagonals
```

### Example --- checkerboard

``` text
0 1 0 1
1 0 1 0
0 1 0 1
1 0 1 0
```

Formula:

``` text
cell[i][j] = (i+j) % 2
```

### Real-World Example --- Floor tiles

You want neighboring floor tiles to have different colors.

``` text
W B W B
B W B W
W B W B
```

**Idea:** find a formula depending on row/column parity instead of
filling cells individually.

------------------------------------------------------------------------

## 19.17 Graph Construction

Graph constructive problems ask you to create vertices/edges satisfying
properties.

Always check global identities.

For an undirected graph:

``` text
sum(degree) = 2E
```

For a tree:

``` text
E = V - 1
connected + V-1 edges => tree
```

### Example --- construct a tree on 5 vertices

A path:

``` text
1 - 2 - 3 - 4 - 5
```

Edges:

``` text
(1,2)
(2,3)
(3,4)
(4,5)
```

### Real-World Example --- Connecting five offices

You need every office connected with the minimum number of cables.

``` text
Office1 -- Office2 -- Office3 -- Office4 -- Office5
```

Five offices need at least four links for a connected tree.

**Idea:** use known structural identities to make validity automatic.

------------------------------------------------------------------------

## 19.18 Construct by Complement

Sometimes constructing the desired set directly is difficult.

Instead construct:

``` text
everything
-
forbidden / unwanted part
```

### Example

Need all numbers `1..10` except multiples of `3`.

Start:

``` text
1 2 3 4 5 6 7 8 9 10
```

Remove:

``` text
3 6 9
```

Result:

``` text
1 2 4 5 7 8 10
```

### Real-World Example --- Guest list

Everyone is invited except three unavailable people.

Instead of rebuilding the whole guest list:

``` text
all employees
-
unavailable employees
=
final guest list
```

**Idea:** if the forbidden set is simpler than the valid set, construct
the complement.

------------------------------------------------------------------------

## 19.19 Construct Backwards

Sometimes the final condition is easier to satisfy than the initial
condition.

Then:

``` text
start from required final state
        ↓
reverse operations
        ↓
recover construction
```

### Example

If the last element must be `1` and every previous element must be
exactly `+2` larger:

``` text
backwards:
1
3 1
5 3 1
7 5 3 1
```

### Real-World Example --- Planning arrival times

A flight departs at `18:00`.

Work backwards:

``` text
18:00 flight
17:00 gate
16:30 security
16:00 airport arrival
```

**Idea:** when the destination constraints are fixed, reverse
construction can remove uncertainty.

------------------------------------------------------------------------

## 19.20 Build Small Blocks and Repeat

If a small valid pattern can be concatenated safely, use blocks.

### Example

Need a string with equal `A` and `B` and no long runs.

Valid block:

``` text
AB
```

Repeat:

``` text
ABABABAB
```

### Mathematical model

Find block `B` such that:

``` text
B is internally valid
and
boundary(B,B) is valid
```

Then repetition is safe.

### Real-World Example --- Repeating shift schedule

A two-day pattern works:

``` text
Day1 = Work
Day2 = Rest
```

Repeat:

``` text
W R | W R | W R
```

**Idea:** prove one block and its boundary; then scale the construction
by repetition.

------------------------------------------------------------------------

## 19.21 Lower Bound + Construction

A powerful constructive proof:

``` text
prove answer cannot beat X
        ↓
construct X
        ↓
X is optimal / tight
```

### Example

Distribute `11` tasks among `2` workers minimizing maximum load.

Lower bound:

``` text
ceil(11/2) = 6
```

Construction:

``` text
5 + 6
```

So maximum load `6` is achievable and optimal.

### Real-World Example --- Packing passengers into taxis

`13` passengers, each taxi holds at most `4`.

Lower bound:

``` text
ceil(13/4) = 4 taxis
```

Construction:

``` text
4 + 4 + 4 + 1
```

**Idea:** once your construction reaches an unavoidable bound, the proof
is complete.

------------------------------------------------------------------------

## 19.22 Impossibility Proofs

Constructive problems often require:

``` text
print answer
or
print -1
```

So you must know when construction is impossible.

Common impossibility sources:

``` text
parity
divisibility
insufficient slots
sum outside bounds
degree constraints
pigeonhole principle
```

### Example

Construct `3` odd integers with even total.

Impossible because:

``` text
odd + odd + odd = odd
```

### Real-World Example --- Seating 5 people into pairs

Every table requires exactly two people.

``` text
5 = 2 + 2 + 1
```

One person must remain unpaired.

**Idea:** before constructing, derive conditions that no valid solution
can violate.

------------------------------------------------------------------------

## 19.23 How to Search for a Construction

When stuck, do not randomly code.

Try tiny instances:

``` text
n = 1
n = 2
n = 3
n = 4
n = 5
```

Write valid answers manually and search for structure.

Look for:

``` text
alternation
rotation
reverse
odd/even split
small-large
repeated block
complement
gaps
cyclic pattern
```

### Example

Suppose valid outputs look like:

``` text
n=2: 2 1
n=3: 2 3 1
n=4: 2 3 4 1
n=5: 2 3 4 5 1
```

Observation:

``` text
cyclic left rotation
```

### Real-World Example --- Discovering a seating pattern

Try small tables first:

``` text
2 guests -> B A
3 guests -> B C A
4 guests -> B C D A
```

The repeated pattern reveals:

``` text
move first person to the end
```

**Idea:** small examples are not just tests; they are a
pattern-discovery tool.

------------------------------------------------------------------------

## 19.24 How to Verify a Construction

Never stop at:

``` text
"It looks correct."
```

Check every required property separately.

### Verification template

``` text
1. correct size?
2. values inside allowed range?
3. uniqueness?
4. required sum?
5. adjacency restrictions?
6. parity/modulo condition?
7. global graph/grid condition?
```

### Example --- permutation

For output:

``` text
2 3 4 5 1
```

verify:

``` text
size = 5                         ✓
all values in [1,5]              ✓
all distinct                     ✓
every original position changed ✓
```

### Real-World Example --- Checking a travel itinerary

Before accepting an itinerary:

``` text
all cities included?        ✓
no duplicate booking?       ✓
times non-overlapping?      ✓
within budget?              ✓
```

**Idea:** verify constraints one by one, not by intuition.

------------------------------------------------------------------------

## 19.25 Constructive vs Greedy vs DP

### Constructive

``` text
Need ANY valid answer.
Find a direct structure.
```

### Greedy

``` text
Need an optimal/valid answer.
Commit to locally safe choices.
```

### DP

``` text
Multiple partial possibilities matter.
Keep states and combine them.
```

### Example

``` text
Print any permutation with property P
-> constructive

Maximize meetings
-> greedy

0/1 knapsack
-> DP
```

### Real-World Example --- Planning meals

``` text
Any meal satisfying calories/protein
-> Constructive

Cheapest set of equal-value items
-> Greedy

Best nutrition combination under budget
with interacting choices
-> DP
```

**Idea:** first identify what the problem is asking before choosing an
algorithm.

------------------------------------------------------------------------

## 19.26 Codeforces Recognition Map

  Statement clue                Candidate constructive model
  ----------------------------- ------------------------------------------
  print any valid sequence      direct construction
  rearrange permutation         rotation / reverse / block swap
  avoid equal adjacent values   alternation / frequency / gaps
  need exact sum                baseline + distribute remainder
  each value in `[L,R]`         total min/max capacity
  parity restriction            split odd/even
  divisible by `m`              residue classes / complementary residues
  pair values                   complement / extremes
  balanced neighboring values   small-large alternation
  grid neighboring constraint   checkerboard / row-column parity
  construct connected graph     tree/path/star
  repeatable local pattern      build block + repeat
  output `-1` if impossible     derive necessary condition first
  final condition easier        construct backwards
  forbidden set simpler         complement construction

### Real-World Example --- Translating a story

Statement:

``` text
"Arrange red and blue lights so
no two red lights are adjacent."
```

Remove story:

``` text
two categories
+
adjacency restriction
        ↓
alternation / gap construction
```

**Idea:** translate the story into structural constraints before
thinking about code.

------------------------------------------------------------------------

## 19.27 Common Mistakes

### 1. Constructing before checking impossibility

Always derive necessary conditions first.

### 2. Verifying only examples

A pattern working for `n=4,5` does not prove it for all `n`.

### 3. Forgetting uniqueness in permutations

Correct range is not enough.

### 4. Breaking the boundary between repeated blocks

``` text
valid block + valid block
```

can still create an invalid junction.

### 5. Integer parity mistakes

Expressions like:

``` text
(S+D)/2
```

require the numerator to be divisible by `2`.

### 6. Ignoring bounds while distributing remainder

Never exceed `R`.

### 7. Solving an optimization problem unnecessarily

If the statement asks for **any** valid answer, do not search for the
best one.

### 8. Overcomplicating a pattern

Try:

``` text
rotation
reverse
alternation
pairing
gaps
blocks
```

before advanced algorithms.

### Real-World Example --- Building shelves

You need shelves between `2m` and `3m` tall with total height `10m`.

A construction might get the total correct but accidentally create:

``` text
4m shelf
```

which violates the per-shelf bound.

**Idea:** satisfying one condition does not guarantee the whole
construction is valid.

------------------------------------------------------------------------

## 19.28 60-Second Constructive Workflow

``` text
PROBLEM
   |
   v
"Print / construct ANY valid answer?"
   |
   v
List exact constraints
   |
   v
Derive impossibility conditions
(parity / bounds / slots / modulo)
   |
   v
Try tiny n manually
   |
   v
Look for structure
   |
   +-----------------------------------+
   |        |       |       |          |
 alternate pair   gaps   blocks    rotation
   |        |       |       |          |
   +----------------+------------------+
                    |
                    v
Choose invariant / formula
                    |
                    v
Construct
                    |
                    v
Verify EVERY property
                    |
             +------+------+
             |             |
           valid         invalid
             |             |
             v             v
           PRINT       repair/search
```

### Fast contest questions

``` text
1. Do I need ANY answer or the BEST answer?
2. What properties must the output satisfy?
3. What conditions make it impossible?
4. Can parity/modulo simplify it?
5. Can I sort/group into categories?
6. Can I alternate?
7. Can I pair complements/extremes?
8. Can I create gaps?
9. Can I repeat a small valid block?
10. Can I rotate/reverse a permutation?
11. Can I construct backwards?
12. What invariant proves every step stays valid?
13. Have I checked all output constraints?
```

### Real-World Example --- Organizing seats

Requirement:

``` text
seat all guests
no two VIPs adjacent
```

Workflow:

``` text
count VIP/non-VIP
      ↓
check enough separators
      ↓
place non-VIPs
      ↓
create gaps
      ↓
place VIPs in gaps
      ↓
verify counts + adjacency
```

**Idea:** constructive solving is constraint engineering, not random
trial-and-error.

------------------------------------------------------------------------

## 19.29 Fast Revision Card

``` text
========================================================
PART 19 — CONSTRUCTIVE MATHEMATICS
========================================================

CONSTRUCTIVE

need ANY valid object
        ↓
derive constraints
        ↓
find simple structure
        ↓
maintain invariant
        ↓
verify all properties

--------------------------------------------------------

CORE QUESTION

"What structure makes the required
properties automatically true?"

--------------------------------------------------------

FIRST: IMPOSSIBILITY

check:
parity
modulo
bounds
slot count
frequency
pigeonhole
graph identities

--------------------------------------------------------

MAIN CONSTRUCTION FORMS

ALTERNATE
A B A B A

PAIR COMPLEMENTS
i <-> n+1-i

EXTREMES
small, large, small, large

ROTATE
1 2 3 4 5
->
2 3 4 5 1

GAPS
_ 0 _ 0 _ 0 _

BLOCKS
AB | AB | AB

CHECKERBOARD
0 1 0
1 0 1
0 1 0

BASELINE + REMAINDER
start all at minimum
then distribute extra

BACKWARDS
final state -> reverse steps

COMPLEMENT
all - forbidden

--------------------------------------------------------

USEFUL CONDITIONS

No adjacent majority symbols:

maxCount <= otherCount + 1

Range sum:

nL <= S <= nR

Complement pair:

x + y = C

Modulo complement:

(r + s) % m = 0

Tree:

E = V - 1
and connected

--------------------------------------------------------

PROOF STYLE

1. Show construction uses legal values.
2. Show required counts/sum are correct.
3. Show invariant/property always holds.
4. Show every required object is included.
5. If impossible, prove why.

--------------------------------------------------------

CONTEST WORKFLOW

ANY valid answer?
      ↓
constraints
      ↓
impossibility test
      ↓
tiny examples
      ↓
pattern
      ↓
invariant
      ↓
construct
      ↓
verify

========================================================
```

### Real-World Example --- Constructive memory hook

Think of arranging a room:

``` text
Need any valid layout
        ↓
measure constraints
        ↓
identify impossible layouts
        ↓
choose a repeating/simple pattern
        ↓
place objects
        ↓
check every rule
```

**Idea:** constructive mathematics is about designing a structure that
makes correctness easy to prove.
