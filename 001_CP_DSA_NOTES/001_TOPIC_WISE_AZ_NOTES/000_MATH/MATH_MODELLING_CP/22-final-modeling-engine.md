# CP Mathematical Modeling Mini-Course

# 21. Final Modeling Engine — CF 800 → 1900

> **Purpose:** Turn the entire course into one practical contest workflow.
>
> You do **not** need to consciously run all 20 chapters on every problem.
> The goal is to train the transformations until the useful ones become automatic.

---

# 21.0 The Entire Course in One Picture

```text
                         PROBLEM STATEMENT
                                │
                                ▼
                    REMOVE STORY / NAMES
                                │
                                ▼
              OBJECTS + VARIABLES + OPERATIONS
                                │
                                ▼
                    EXACT TARGET CONDITION
                                │
                                ▼
              EQUATIONS / INEQUALITIES / BOUNDS
                                │
                                ▼
                    SIMPLIFY THE MODEL
                                │
          ┌─────────────────────┼─────────────────────┐
          │                     │                     │
      eliminate             compress              normalize
      variables              state               ordering
          │                     │                     │
          └─────────────────────┼─────────────────────┘
                                ▼
                     IDENTIFY STRUCTURE
                                │
       ┌──────────┬──────────┬──┴───┬──────────┬──────────┐
       │          │          │      │          │          │
    modulo    invariant   counting prefix   greedy    min/max
       │          │          │      │          │          │
       └──────────┴──────────┴──┬───┴──────────┴──────────┘
                                │
                         constructive?
                         casework?
                         monotone?
                                │
                                ▼
                       CHOOSE ALGORITHM
                                │
                                ▼
                       PROVE CORRECTNESS
                                │
                                ▼
                      CHECK COMPLEXITY
                                │
                                ▼
                         TEST BOUNDARIES
                                │
                                ▼
                              C++
```

---

# 21.1 The First Rule

Do not begin with:

```text
Is this binary search?
Is this greedy?
Is this DP?
Is this two pointers?
```

Begin with:

```text
What is this problem saying mathematically?
```

Algorithms operate on a model.

Your first job is to build that model.

---

# 21.2 The Three Layers

Think in three layers.

```text
LAYER 1 — STORY
Alice, monsters, coins, houses, attacks...

              ↓ translate

LAYER 2 — MATHEMATICS
variables, counts, differences,
equations, bounds, parity, states...

              ↓ recognize structure

LAYER 3 — ALGORITHM
formula, greedy, prefix,
sorting, binary search, DP...
```

A common mistake is jumping:

```text
STORY → ALGORITHM
```

Train:

```text
STORY → MATHEMATICS → ALGORITHM
```

---

# 21.3 Phase A — Understand the Contract

Before solving, identify:

```text
INPUT:
What am I given?

OUTPUT:
What exactly must I print?

OBJECTIVE:
feasibility?
count?
minimum?
maximum?
construction?

OPERATIONS:
What am I allowed to change?
```

Write the problem in one sentence without story names.

Example:

```text
Given counts R and B,
find the maximum number of groups,
where each group consumes
2 red and 3 blue.
```

That is already much closer to mathematics.

---

# 21.4 Phase B — Define Variables

Name only quantities that matter.

Example:

```text
R = available red
B = available blue
k = number of groups
```

One group consumes:

```text
2 red
3 blue
```

For `k` groups:

```text
2k <= R
3k <= B
```

Therefore:

```text
k <= R/2
k <= B/3
```

So:

```text
k = min(R/2, B/3)
```

The story disappeared because the variables exposed the resource constraints.

---

# 21.5 Phase C — Translate Words Into Relations

Build this reflex:

```text
exactly
→ =

at least
→ >=

at most
→ <=

different
→ !=

between
→ lower <= x <= upper

divisible by m
→ x % m == 0

same remainder
→ a % m == b % m

same parity
→ a % 2 == b % 2

distance
→ absolute / coordinate difference

minimum number of groups/containers
→ ceiling

maximum number possible
→ upper bounds / bottleneck

equal counts
→ difference = 0

range sum
→ prefix difference
```

Do not keep these conditions only in English.

Write them.

---

# 21.6 Phase D — Inspect One Operation

For operation problems, make this automatic:

```text
BEFORE
  ↓
ONE OPERATION
  ↓
AFTER
```

Then calculate:

```text
Δsum
Δdifference
Δcount
Δposition
Δremainder
Δparity
```

Example:

```text
(a,b) -> (a+3,b+1)
```

Difference:

```text
d = a-b
```

After:

```text
d' = (a+3)-(b+1)
   = d+2
```

So one operation changes the difference by exactly `2`.

That immediately suggests:

```text
direction
parity
reachability
operation count
```

---

# 21.7 Phase E — Search for What Does NOT Change

Ask:

```text
What stays invariant?
```

Common candidates:

```text
parity
remainder modulo m
sum
difference
gcd
xor
relative ordering
count difference
```

But remember:

```text
invariant condition
may be necessary
without being sufficient
```

After finding an invariant, also check:

```text
direction
bounds
available resources
construction/reachability
```

---

# 21.8 Phase F — Eliminate Variables

If equations contain several unknowns, reduce them.

Example:

```text
x+y = S
x-y = D
```

Add:

```text
2x = S+D
```

so:

```text
x = (S+D)/2
```

Then:

```text
y = (S-D)/2
```

Now feasibility becomes:

```text
S+D must be even
S-D must be even
x,y satisfy required bounds
```

Algebra can turn search into direct checking.

---

# 21.9 Phase G — Compress the State

Ask:

```text
Does the exact value matter?
```

Maybe only this matters:

```text
odd/even
remainder
positive/zero/negative
less/equal/greater
frequency
difference
minimum/maximum
```

Example:

```text
x = 1,3,5,7,...
```

may all behave identically if the operation only cares about parity.

Then replace infinitely many values by:

```text
ODD
EVEN
```

This is one of the strongest ways to simplify CF problems.

---

# 21.10 Phase H — Normalize

Before creating many cases, try:

```text
min/max
sort
swap
absolute difference
translate coordinates
subtract a baseline
```

Example:

Instead of:

```text
a < b
a = b
a > b
```

if roles are symmetric:

```text
L = min(a,b)
R = max(a,b)
```

Now:

```text
L <= R
```

always.

Normalization prevents unnecessary case explosion.

---

# 21.11 Phase I — Identify the Mathematical Structure

Now scan the reduced model.

```text
Does it look like...
```

### Equality / balance?

```text
A = B
A-B = 0
```

Think:

```text
difference
balance
prefix state
```

### Divisibility?

Think:

```text
modulo
gcd
remainder classes
```

### Repeated fixed change?

Think:

```text
delta
arithmetic progression
reachability
```

### Resource consumption?

Think:

```text
inequalities
capacity
bottleneck
min/max
```

### Total/count?

Think:

```text
contribution
frequency
combinations
```

### Range/subarray?

Think:

```text
prefix
difference
frequency of prefix states
```

### Movement?

Think:

```text
coordinates
displacement
distance
parity of moves
```

### Optimization?

Think:

```text
extremes
greedy
candidate answer X
monotonicity
```

### Print any valid object?

Think:

```text
construction
baseline
blocks
pairing
repair
```

### Formula changes by region?

Think:

```text
casework
piecewise
normalize/merge
```

---

# 21.12 The 60-Second Modeling Scan

After reading and checking examples:

```text
1. What are the variables?

2. What exactly must become true?

3. Can I write =, <=, >=, %, abs?

4. What does one operation change?

5. What stays unchanged?

6. Can I eliminate a variable?

7. Does exact value matter,
   or only parity/remainder/category?

8. Can I normalize using sort/min/max?

9. What are the boundaries/extremes?

10. Is there a direct formula,
    contribution, greedy, prefix,
    construction, or monotone predicate?
```

You will not always use all ten.

They are prompts for finding the hidden structure.

---

# 21.13 Tiny Cases Before Heavy Thinking

Use:

```text
n=1
n=2
n=3
```

Then targeted cases:

```text
all equal
minimum input
maximum input
already valid
barely impossible
one operation away
odd/even
zero remainder
one leftover
two extremes
```

Do not generate examples randomly.

Ask each example a question.

Example:

```text
Does parity matter?
Does ordering matter?
What happens after one move?
Where does formula change?
```

---

# 21.14 Build a Before/After Table

When stuck on operations:

```text
quantity       before     after     change
------------------------------------------
sum              S        S'        ?
difference       D        D'        ?
parity           p        p'        ?
mod m            r        r'        ?
bad count        b        b'        ?
```

Search for:

```text
fixed delta
invariant
monotone measure
decreasing error
```

This often exposes the solution.

---

# 21.15 Constraints Are a Filter

Only after obtaining a model, inspect complexity.

Typical rough guide:

```text
n <= 20
→ exponential may be possible

n <= 2e3
→ O(n^2) may be possible

n <= 2e5
→ usually O(n), O(n log n)

n <= 1e6
→ usually near-linear

value <= 1e18
→ cannot iterate through value range
```

Also read aggregate constraints:

```text
sum of n over all test cases <= 2e5
```

means your total work can often be:

```text
O(sum n)
```

rather than:

```text
O(t * maximum n)
```

---

# 21.16 Direct Formula Comes First

Before reaching for an algorithm, ask:

```text
Can algebra solve it directly?
```

Examples:

```text
number of full groups
→ min(resource_i / need_i)

minimum containers
→ ceil(n/k)

distance
→ abs(a-b)

range
→ max-min

number of cross-category pairs
→ countA * countB
```

A direct formula is usually simpler and safer than simulation.

---

# 21.17 Contribution Engine

If the answer is a total:

```text
answer = ?
```

ask:

```text
What does one element/object contribute?
```

Example: count odd-even pairs.

```text
O = odd count
E = even count
```

Each odd pairs with every even:

```text
answer = O*E
```

General pattern:

```text
global total
     ↓
local contribution
     ↓
sum contributions
```

---

# 21.18 Prefix Engine

For a subarray:

```text
condition(L..R)
```

try expressing it as:

```text
state(R+1)-state(L)
```

Example target sum `K`:

```text
P[R+1]-P[L] = K
```

Rearrange:

```text
P[L] = P[R+1]-K
```

Now ask:

```text
Have I seen the required previous prefix state?
```

This turns many subarray problems into:

```text
prefix + map/set/frequency
```

---

# 21.19 Greedy Engine

A greedy idea needs more than:

```text
"this seems best"
```

Ask:

```text
What is the local choice?

Why can an optimal solution be changed
to use my choice without becoming worse?
```

Typical proof mechanisms:

```text
exchange
dominance
earliest finish
cheapest first
largest gain
bottleneck
preserving future feasibility
```

If you cannot explain why the choice is safe, keep investigating.

---

# 21.20 Extremal Engine

When problem asks about:

```text
maximum
minimum
worst
best
all pairs
all elements
```

ask:

```text
Can only a few extremes determine the answer?
```

Examples:

```text
maximum difference
→ max-min

maximum product of pair
→ max(two largest product,
      two smallest product)

minimum adjacent difference
→ sort + neighboring values
```

Do not inspect every combination if extremes dominate.

---

# 21.21 Feasibility Engine

Sometimes the key question is only:

```text
Can it be done?
```

Model:

```text
necessary conditions
       ↓
are they sufficient?
       ↓
YES/NO
```

Typical feasibility checks:

```text
enough total resource?
correct parity?
correct remainder?
inside lower/upper bounds?
reachable direction?
enough capacity?
```

A constructive proof is often the cleanest proof of sufficiency.

---

# 21.22 Answer-Space Engine

For:

```text
minimize answer
maximize answer
```

if direct optimization is difficult:

```text
call candidate answer X
```

Ask:

```text
Can I efficiently check possible(X)?
```

Then test monotonicity.

Minimum problem:

```text
F F F F T T T T
        ↑
     first true
```

Maximum problem:

```text
T T T T F F F F
      ↑
   last true
```

Only after proving this structure should you use binary search on the answer.

---

# 21.23 Constructive Engine

If asked:

```text
print any valid...
```

use:

```text
required properties
      ↓
necessary conditions
      ↓
simplest baseline
      ↓
remaining deficit / violations
      ↓
distribute / pair / alternate / repair
      ↓
preserve future feasibility
      ↓
verify every condition
```

Useful starting constructions:

```text
1 2 3 ... n
n ... 3 2 1
A B A B ...
1 n 2 n-1 ...
baseline + leftover
pairs
small valid blocks
all simple except forced last value
```

---

# 21.24 Casework Engine

If one formula fails:

```text
Where does behavior change?
```

Check:

```text
parity
sign
ordering
equality
remainder
boundary
threshold
```

Then:

```text
raw cases
   ↓
solve
   ↓
check complete coverage
   ↓
normalize
   ↓
merge equivalent cases
```

Do not fear temporary casework.

Use it to discover the simpler rule.

---

# 21.25 Necessary vs Sufficient — Always Separate Them

Suppose you derive:

```text
target requires even difference
```

That proves:

```text
odd difference → impossible
```

It does **not** prove:

```text
even difference → possible
```

Ask separately:

```text
NECESSARY:
Why must every solution satisfy this?

SUFFICIENT:
If this holds, can I always produce/reach a solution?
```

This single habit prevents many wrong answers.

---

# 21.26 Proof Selection Engine

Once you think you have the solution, choose the simplest proof.

```text
Direct equation?
→ algebra

Property never changes?
→ invariant

Need to show existence?
→ construction

Greedy?
→ exchange / dominance

Only extremes matter?
→ extremal argument

Candidate answer?
→ monotonicity

Finite categories?
→ exhaustive state proof

Impossible?
→ contradiction / violated necessary condition
```

You usually need only a short proof, but you need the correct one.

---

# 21.27 The Counterexample Attack

Before coding, attack your own idea.

Try:

```text
smallest input
largest conceptual extreme
all equal
strictly increasing
strictly decreasing
odd/even boundary
remainder 0
remainder m-1
already solved
barely feasible
barely infeasible
one huge value
duplicates
negative values if allowed
```

For greedy:

```text
Can taking the locally best option
block a better future solution?
```

For formulas:

```text
What happens exactly at equality/boundary?
```

---

# 21.28 The Implementation Translation

Your final reasoning should be simple enough to map directly to code.

Example reasoning:

```text
Need same parity.
```

Code:

```cpp
if ((a & 1) == (b & 1)) {
    ...
}
```

Reasoning:

```text
Need k <= R/2 and k <= B/3.
```

Code:

```cpp
long long k = min(R / 2, B / 3);
```

Reasoning:

```text
distance must be divisible by d.
```

Code:

```cpp
if ((b - a) % d == 0) ...
```

If the code is much more complicated than your proof, inspect whether your model is incomplete.

---

# 21.29 Overflow Is Part of Modeling

Before coding arithmetic, estimate magnitude.

If:

```text
a,b <= 1e9
```

then:

```text
a*b <= 1e18
```

so use:

```cpp
long long
```

If expressions may exceed `long long`, rearrange or use a wider intermediate when appropriate.

Do not treat overflow as merely a syntax issue.

It is a constraint on the mathematical representation.

---

# 21.30 Contest Problem Reading Workflow

Use three passes.

## Pass 1 — Contract

Read:

```text
input
output
goal
operations
constraints
```

Do not solve yet.

## Pass 2 — Examples

For each example:

```text
Why is this output correct?
Which condition is active?
What changed?
```

## Pass 3 — Model

Write:

```text
variables
equations
operation delta
invariants
bounds
```

Then search for structure.

---

# 21.31 Contest Scratchpad — Full Version

```text
PROBLEM IN ONE LINE:
____________________________________

OBJECTS:
____________________________________

VARIABLES:
____________________________________

TARGET:
____________________________________

OPERATIONS:
____________________________________

ONE OPERATION CHANGES:
____________________________________

EQUATIONS / INEQUALITIES:
____________________________________

BOUNDS:
____________________________________

INVARIANT:
____________________________________

CAN I ELIMINATE VARIABLES?
____________________________________

CAN I COMPRESS STATE?
____________________________________

CAN I NORMALIZE?
____________________________________

IMPORTANT CASES / BOUNDARIES:
____________________________________

DIRECT FORMULA?
____________________________________

CONTRIBUTION / PREFIX?
____________________________________

GREEDY / EXTREMES?
____________________________________

CONSTRUCTIVE?
____________________________________

CANDIDATE ANSWER X?
____________________________________

NECESSARY CONDITION:
____________________________________

SUFFICIENT CONDITION:
____________________________________

PROOF:
____________________________________

COMPLEXITY:
____________________________________
```

Do not fill every line mechanically.

Use the relevant sections.

---

# 21.32 Contest Scratchpad — Fast Version

Eventually reduce it to:

```text
STATE:
______

TARGET:
______

ONE MOVE / CHOICE:
______

KEY RELATION:
______

WHAT MATTERS?
______

STRUCTURE:
______

PROOF:
______

O(?):
______
```

This should fit on a small piece of paper.

---

# 21.33 When You Are Completely Stuck

Run this recovery sequence:

```text
1. Stop thinking about named algorithms.

2. Rewrite the story in one sentence.

3. Define variables.

4. Write the target mathematically.

5. Create n=1,2,3 cases.

6. Perform one operation manually.

7. Compare before/after.

8. Track:
   sum
   difference
   parity
   remainder
   min/max

9. Ask what information is irrelevant.

10. Normalize ordering.

11. Identify impossible cases.

12. Ask whether the remaining condition
    is also sufficient.

13. Only now reconsider algorithms.
```

This is the core "unstuck engine."

---

# 21.34 If You Have an O(n²) Idea

Do not immediately abandon it.

First ask:

```text
What is the inner loop repeatedly searching/counting?
```

Maybe it can become:

```text
frequency table
prefix
hash map
sorted search
two pointers
contribution
formula
```

Example:

```text
for every i:
    scan all j to count category X
```

If category counts are global or prefix-based, precompute them.

The slow solution often reveals exactly what information must be summarized.

---

# 21.35 If You Have Simulation

Ask:

```text
Do I need every intermediate state,
or only the final effect?
```

Repeated operation:

```text
x += d
```

performed `k` times gives:

```text
x_final = x + kd
```

Replace:

```text
simulation
```

with:

```text
equation
```

whenever intermediate states do not matter.

---

# 21.36 If You Have Too Many Cases

Try:

```text
sort
min/max
abs
modulo
parity count
sign count
frequency
symmetry
state compression
```

Example:

```text
(E,E)
(E,O)
(O,E)
(O,O)
```

may collapse to:

```text
same parity
different parity
```

Then perhaps further to:

```text
(a-b)%2 == 0
```

The goal is not zero cases.

The goal is only **essential** cases.

---

# 21.37 If Greedy Feels Right but You Cannot Prove It

Ask:

```text
What would an optimal solution do differently?
```

Then attempt:

```text
swap its first differing choice
with my greedy choice
```

If the solution remains feasible and no worse:

```text
greedy choice is safe
```

If not, your greedy may be wrong or the proof requires another invariant.

---

# 21.38 If Binary Search Feels Right

Do not code yet.

Write:

```text
possible(X) = __________________
```

Then explicitly prove:

```text
possible(X)
⇒ possible(X-1)
```

for maximum-feasible style,

or the corresponding easier-direction implication for your problem.

Also derive:

```text
LOW
HIGH
```

from mathematics.

Only then implement.

---

# 21.39 If Construction Feels Random

Stop choosing arbitrary values.

Ask:

```text
Can I make one condition automatic?
```

Examples:

```text
need distinct
→ permutation

need alternating categories
→ ABAB...

need equal pair sums
→ pair extremes

need exact sum
→ baseline + leftover

need zero total
→ x,-x blocks

need final equation
→ choose easy prefix, force last
```

A good construction is designed, not guessed.

---

# 21.40 Rating 800–1000 Modeling

Typical hidden structure:

```text
direct arithmetic
conditions
parity
min/max
simple counting
ceil/floor
one-operation effect
tiny casework
```

Target skill:

```text
translate quickly
avoid overthinking
implement cleanly
```

Many problems should reduce to a few equations or branches.

---

# 21.41 Rating 1000–1200 Modeling

Expect combinations:

```text
parity + bounds
frequency + counting
sorting + simple greedy
operation + difference
modulo + construction
```

Target skill:

```text
identify the controlling quantity
```

rather than simulate the whole story.

---

# 21.42 Rating 1200–1400 Modeling

Expect:

```text
2 observations
```

Examples:

```text
state compression + counting

sorting + extremal choice

prefix + transformed values

invariant + feasibility

construction + parity
```

The solution is often simple after the correct representation appears.

---

# 21.43 Rating 1400–1600 Modeling

Expect more interaction:

```text
normalize
    +
case split
    +
greedy/invariant
```

or:

```text
prefix
    +
remainder
    +
frequency
```

or:

```text
extremal bound
    +
constructive sufficiency
```

Target skill:

```text
chain observations
```

instead of waiting for one magic trick.

---

# 21.44 Rating 1600–1900 Modeling

Often the individual tools are familiar, but the composition is harder.

Possible chain:

```text
story
  ↓
state compression
  ↓
derive feasibility
  ↓
greedy check
  ↓
monotonicity
  ↓
binary search answer
```

or:

```text
operation
  ↓
invariant
  ↓
classify states
  ↓
casework
  ↓
construct answer
```

or:

```text
count contribution
  ↓
prefix/suffix summary
  ↓
extremal candidate
```

At this level, ask:

```text
What is observation #1?
What does it simplify?
What new observation becomes visible afterward?
```

---

# 21.45 The Observation Ladder

When a 1700 problem seems impossible, do not expect to see the final solution instantly.

Think:

```text
Observation 1
     ↓
reduces problem
     ↓
Observation 2
     ↓
reduces again
     ↓
Observation 3
     ↓
standard algorithm
```

Example:

```text
exact values irrelevant
     ↓
compress to parity
     ↓
only mismatch counts matter
     ↓
pair mismatches
     ↓
answer
```

This is how many harder CF problems unfold.

---

# 21.46 Do Not Memorize Problems — Memorize Transformations

Bad notebook entry:

```text
Problem X uses prefix sum.
```

Better:

```text
"equal number of A and B"
      ↓
A = +1
B = -1
      ↓
zero-sum subarray
      ↓
equal prefix balances
```

Another:

```text
"maximum minimum distance"
      ↓
candidate distance X
      ↓
can place enough objects?
      ↓
greedy feasibility
      ↓
monotone
      ↓
binary search
```

These transformations transfer to new problems.

---

# 21.47 Post-Solve Review Template

After solving:

```text
PROBLEM:
________________

RATING:
________________

STORY REMOVED:
________________

KEY VARIABLE/STATE:
________________

CRITICAL EQUATION:
________________

OBSERVATION 1:
________________

OBSERVATION 2:
________________

WHY ALGORITHM FOLLOWS:
________________

PROOF IDEA:
________________

WHAT I SHOULD RECOGNIZE NEXT TIME:
________________
```

Keep this short.

---

# 21.48 Editorial Review Template

If you could not solve:

```text
Do NOT write:
"I didn't know this trick."
```

Find the first missed transformation.

Examples:

```text
I did not convert equal counts to balance.

I tracked values instead of parity.

I simulated instead of writing x+kd.

I missed that min/max controlled feasibility.

I did not try candidate answer X.

I found necessity but not sufficiency.

I created 8 cases instead of sorting.

I tried to construct randomly instead of using blocks.
```

That is the skill gap to train.

---

# 21.49 Practice Loop

For each problem:

```text
READ
 ↓
MODEL
 ↓
SOLVE
 ↓
PROVE
 ↓
CODE
 ↓
TEST
 ↓
REVIEW TRANSFORMATION
```

If stuck and you read the editorial:

```text
EDITORIAL
   ↓
identify first missed observation
   ↓
close editorial
   ↓
re-derive solution yourself
   ↓
implement
   ↓
revisit later
```

Do not turn editorials into passive reading.

---

# 21.50 Speed Comes From Compression

Fast contestants are not necessarily reading English faster.

They often compress familiar phrases immediately.

Example:

```text
"same parity"
```

becomes:

```text
(a-b)%2 == 0
```

almost instantly.

```text
"minimum groups of size k"
```

becomes:

```text
ceil(n/k)
```

```text
"range sum"
```

becomes:

```text
prefix[R]-prefix[L]
```

```text
"maximum minimum"
```

triggers:

```text
candidate X + feasibility?
```

Practice builds these translations.

---

# 21.51 Final Decision Tree

```text
                         NEW PROBLEM
                              │
                              ▼
                    What must I output?
                              │
         ┌────────────────────┼────────────────────┐
         │                    │                    │
       value                YES/NO               object
         │                    │                    │
         │               feasibility          construction
         │                    │                    │
         └────────────────────┼────────────────────┘
                              ▼
                     Define variables/state
                              │
                              ▼
                    Write exact conditions
                              │
                              ▼
                    Is there an operation?
                       │             │
                      YES            NO
                       │             │
                 one-step delta      │
                 invariant?          │
                       └──────┬──────┘
                              ▼
                    Can algebra reduce it?
                              │
                              ▼
                  Can state be compressed?
                              │
                              ▼
                    Can input normalize?
                              │
                              ▼
                What structure is present?
                              │
       ┌───────────┬──────────┼──────────┬───────────┐
       │           │          │          │           │
     modulo      count      prefix     extrema     cases
       │           │          │          │           │
       └───────────┴──────────┼──────────┴───────────┘
                              │
                     optimization?
                              │
                ┌─────────────┴─────────────┐
                │                           │
             direct?                    candidate X?
                │                           │
              greedy?                  monotonic?
                │                           │
                └─────────────┬─────────────┘
                              ▼
                         PROVE IT
                              │
                              ▼
                      complexity fits?
                              │
                              ▼
                     boundary attack
                              │
                              ▼
                             CODE
```

---

# 21.52 The 12 Questions to Internalize

For any unfamiliar problem:

```text
1. What are the objects?

2. What variables describe them?

3. What exactly is the target?

4. What equations/inequalities express it?

5. What does one operation change?

6. What stays unchanged?

7. Can I eliminate variables?

8. What information actually matters?

9. Can I normalize the state/order?

10. Where does behavior change?

11. What known structure is now visible?

12. Why is the resulting algorithm correct?
```

These are more valuable than memorizing a huge list of solution templates.

---

# 21.53 The 20-Chapter Map

```text
CP MATHEMATICAL MODELING
│
├── 00 Foundation
│
├── 01 Story → Mathematics
│
├── 02 Operation Modeling
│
├── 03 Conditions & Structure
│
├── 04 Algebra → Smaller Search
│
├── 05 Resource & Optimization Modeling
│
├── 06 Feasibility Modeling
│
├── 07 Counting & Contribution Modeling
│
├── 08 Remainder / Modulo Modeling
│
├── 09 Interval & Boundary Modeling
│
├── 10 Rate, Time & Work Modeling
│
├── 11 Invariant & Conservation Modeling
│
├── 12 Transformation & Operation Modeling
│
├── 13 Greedy Mathematical Modeling
│
├── 14 Prefix / Difference Modeling
│
├── 15 Coordinate & Distance Modeling
│
├── 16 Min/Max & Extremal Modeling
│
├── 17 State Compression & Equivalence Modeling
│
├── 18 Bounds / Monotonicity / Answer-Space Modeling
│
├── 19 Constructive Mathematical Modeling
│
├── 20 Casework & Piecewise Modeling
│
└── 21 Final Modeling Engine
```

These chapters are not independent boxes.

Real problems combine them.

Example:

```text
Story
 ↓
Operation modeling
 ↓
Invariant
 ↓
State compression
 ↓
Casework
 ↓
Construction
```

Another:

```text
Story
 ↓
Resource inequalities
 ↓
Feasibility
 ↓
Greedy
 ↓
Monotonicity
 ↓
Binary search
```

---

# 21.54 CF 800 → 1900 Mixed Modeling Drill Method

For every practice problem, before coding, classify it only **after** deriving the model.

Record:

```text
PRIMARY MODEL:
________________

SECONDARY MODEL:
________________

KEY TRANSFORMATION:
________________
```

Examples:

```text
PRIMARY:
Invariant

SECONDARY:
Parity

KEY TRANSFORMATION:
Track a-b instead of a and b.
```

or:

```text
PRIMARY:
Answer-space

SECONDARY:
Greedy feasibility

KEY TRANSFORMATION:
Optimize X → check possible(X).
```

This teaches combinations rather than isolated chapters.

---

# 21.55 Suggested Drill Progression

```text
800–1000
→ fast translation + direct formulas

1000–1200
→ operation / parity / counting / simple construction

1200–1400
→ combine two modeling ideas

1400–1600
→ chained observations + proof

1600–1900
→ compressed model + multiple interacting ideas
```

Do not require yourself to identify a chapter name during a contest.

The chapter labels are for training.

During contests, you want the transformations to happen naturally.

---

# 21.56 Final Pre-Code Gate

Before implementation, you should be able to answer:

```text
WHAT am I computing?

WHY does this formula/algorithm represent
the problem?

WHY are impossible cases impossible?

WHY are possible cases actually achievable?

WHY is greedy/search/construction correct?

WHAT are the boundary cases?

WHAT is the complexity?

CAN arithmetic overflow?
```

If you cannot explain these, spend another minute on the model before coding.

---

# 21.57 Final Contest Card

```text
┌──────────────────────────────────────────┐
│          CF MODELING ENGINE              │
├──────────────────────────────────────────┤
│ 1. Remove story                          │
│ 2. Define state                          │
│ 3. Write target mathematically           │
│ 4. Analyze one operation                 │
│ 5. Find invariant / bounds               │
│ 6. Eliminate / compress / normalize      │
│ 7. Test tiny + boundary cases            │
│ 8. Identify structure                    │
│ 9. Derive algorithm                      │
│10. Prove necessary + sufficient          │
│11. Check complexity + overflow           │
│12. Code                                  │
└──────────────────────────────────────────┘
```

---

# 21.58 Final Unstuck Card

```text
STUCK?
  │
  ▼
STOP SEARCHING FOR ALGORITHM NAMES
  │
  ▼
write variables
  │
  ▼
write target equation
  │
  ▼
try n=1,2,3
  │
  ▼
one operation: before → after
  │
  ▼
track:
sum / difference / parity /
modulo / min / max
  │
  ▼
compress state
  │
  ▼
normalize order
  │
  ▼
find impossible cases
  │
  ▼
ask what remains sufficient
  │
  ▼
algorithm usually becomes clearer
```

---

# 21.59 What Mastery Looks Like

At first:

```text
story
 ↓
confusion
 ↓
try algorithms
```

After training:

```text
story
 ↓
variables
 ↓
equation
 ↓
structure
 ↓
algorithm
```

Later:

```text
story
 ↓
pattern/model almost immediately
```

That speed is built from repeated modeling practice, not from memorizing every previous problem.

---

# 21.60 Final Principle

The purpose of mathematical modeling in competitive programming is to transform:

```text
a large story with many details
```

into:

```text
a small mathematical object
```

that you know how to reason about.

Keep this chain in your head:

```text
STORY
  ↓
STATE
  ↓
TARGET
  ↓
RELATIONS
  ↓
SIMPLIFY
  ↓
STRUCTURE
  ↓
PROOF
  ↓
ALGORITHM
  ↓
CODE
```

And when stuck, return to:

```text
"What is the smallest mathematical
description of this problem?"
```

That question is the center of the entire course.

---

# Course Complete

```text
00 → 21
CP MATHEMATICAL MODELING
COMPLETE
```

The next phase should be **practice**, not more theory:

```text
CF problem
   ↓
model it without code
   ↓
derive observations
   ↓
prove approach
   ↓
implement
   ↓
record the key transformation
```

That is how these notes turn into faster problem interpretation and stronger contest problem solving.
