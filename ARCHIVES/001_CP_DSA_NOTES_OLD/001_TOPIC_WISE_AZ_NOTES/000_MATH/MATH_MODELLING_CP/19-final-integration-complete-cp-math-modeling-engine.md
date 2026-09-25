# CP Mathematical Modeling Mini-Course

## 19. Final Integration — The Complete CP Math-Modeling Engine

> **Goal:** Combine the entire mini-course into one reusable thinking system for unfamiliar Codeforces problems.
>
> The objective is **not** to memorize 18 techniques.
>
> The objective is to develop one automatic process:
>
> ```text
> STORY
>   ↓
> MATHEMATICAL MODEL
>   ↓
> STRUCTURE
>   ↓
> ALGORITHM
>   ↓
> PROOF
>   ↓
> CODE
> ```

---

# 19.1 The Big Picture

When you open a CF problem, do **not** immediately search your memory for:

```text
binary search?
greedy?
prefix?
DP?
two pointers?
```

First remove the story.

```text
STORY
  ↓
objects
  ↓
variables
  ↓
allowed operations
  ↓
target condition
  ↓
equation / inequality / state
  ↓
structure
  ↓
algorithm
```

The algorithm should come **after** the mathematical structure.

---

# 19.2 The Complete Modeling Tree

```text
                         NEW CF PROBLEM
                               │
                               ▼
                    1. REMOVE THE STORY
                               │
                               ▼
                    What are the objects?
                    What can change?
                    What is the target?
                               │
                               ▼
                    2. DEFINE VARIABLES
                               │
                               ▼
                    current / target / counts
                    positions / resources
                               │
                               ▼
                    3. WRITE THE CONDITION
                               │
             ┌─────────────────┼─────────────────┐
             │                 │                 │
          equality         inequality         optimize
             │                 │                 │
             ▼                 ▼                 ▼
          equation          bounds          answer X
             │                 │                 │
             └─────────────────┼─────────────────┘
                               ▼
                   4. ANALYZE OPERATIONS
                               │
                    What does ONE move do?
                               │
             ┌─────────────────┼──────────────────┐
             │                 │                  │
           delta           invariant          contribution
             │                 │                  │
             └─────────────────┼──────────────────┘
                               ▼
                    5. REDUCE THE STATE
                               │
          ┌──────────┬─────────┼─────────┬───────────┐
          │          │         │         │           │
       parity     modulo    balance   prefix      min/max
          │          │         │         │           │
          └──────────┴─────────┼─────────┴───────────┘
                               ▼
                     6. FIND STRUCTURE
                               │
         ┌────────────┬────────┼────────┬────────────┐
         │            │        │        │            │
       greedy       count    sort     search       direct
         │            │        │        │          formula
         └────────────┴────────┼────────┴────────────┘
                               ▼
                       7. PROVE IT
                               │
                               ▼
                      8. CHECK COMPLEXITY
                               │
                               ▼
                           9. CODE
```

---

# 19.3 Phase 1 — Remove the Story

Suppose statement says:

> Alice has red stones and Bob has blue stones. Every minute Alice may exchange...

Ignore names first.

Rewrite:

```text
R = number of red
B = number of blue

operation:
(R,B) -> (...)

target:
...
```

Your first job is:

```text
prose → symbols
```

If the story remains in your head too long, the mathematical structure stays hidden.

---

# 19.4 Phase 2 — Identify the Four Essentials

For almost every modeling problem, write:

```text
1. STATE
   What describes the current situation?

2. OPERATION
   What exactly can change?

3. TARGET
   What must eventually be true?

4. OBJECTIVE
   Is it feasibility, counting,
   minimum, or maximum?
```

Example:

```text
STATE:
(a,b)

OPERATION:
a += 2
b -= 1

TARGET:
a = b

OBJECTIVE:
minimum operations
```

Now the story has disappeared.

---

# 19.5 Phase 3 — Move the Target Into Mathematics

Common translations:

```text
"equal"
→ A = B
→ A-B = 0

"at least"
→ >=

"at most"
→ <=

"divisible by m"
→ x % m = 0

"same remainder"
→ a % m = b % m

"equal number of X and Y"
→ countX-countY = 0

"within distance k"
→ |a-b| <= k

"all elements satisfy"
→ often min/max constraint

"minimum possible maximum"
→ candidate answer X + feasibility
```

Do not proceed until the target has a precise mathematical form.

---

# 19.6 Phase 4 — Analyze One Operation

This is one of the strongest habits in CP.

Ask:

```text
What changes after exactly ONE operation?
```

Suppose:

```text
a += 3
b += 1
```

Track difference:

```text
d = a-b
```

Then:

```text
d' = (a+3)-(b+1)
   = d+2
```

Immediately you know:

```text
difference changes by exactly +2
```

This can reveal:

```text
parity
divisibility
direction
minimum operations
reachability
```

---

# 19.7 Phase 5 — Look for an Invariant

Ask:

```text
What cannot change?
```

Common candidates:

```text
parity
remainder mod m
sum
difference
gcd
relative order
x+y parity
frequency parity
```

Example:

```text
x -> x+6
```

Then:

```text
x % 6
```

is invariant.

So reaching `y` requires:

```text
x % 6 = y % 6
```

But remember:

```text
invariant condition
```

may be necessary without being sufficient.

Also check:

```text
direction
bounds
available resources
```

---

# 19.8 Phase 6 — Ask What Information Is Actually Needed

This is state compression.

Instead of:

```text
full integer x
```

maybe only:

```text
x % 2
```

matters.

Instead of:

```text
(A,B)
```

maybe:

```text
A-B
```

matters.

Instead of an entire array, maybe only:

```text
countEven
countOdd
```

matters.

Ask:

```text
If two states have the same compressed representation,
can the rest of the problem distinguish them?
```

If no, compression is safe.

---

# 19.9 Phase 7 — Try Contribution Modeling

If the answer is a total:

```text
sum
count
number of pairs
total cost
```

ask:

```text
What does ONE object contribute?
```

Then:

```text
answer = sum of contributions
```

Example:

Count pairs where one element is odd and the other even.

Let:

```text
O = odd count
E = even count
```

Each odd can pair with every even:

```text
answer = O*E
```

No pair enumeration is necessary.

---

# 19.10 Phase 8 — Try Prefix / Balance Modeling

If the statement contains:

```text
subarray
prefix
range
equal counts in segment
sum in segment
divisible segment
```

ask:

```text
Can the segment be represented
as a difference of two accumulated states?
```

Core:

```text
sum(L..R)
=
P[R+1]-P[L]
```

Target sum:

```text
P[R+1]-P[L] = K
```

Rearrange:

```text
P[L] = P[R+1]-K
```

Now the problem becomes:

```text
What previous prefix state do I need?
```

---

# 19.11 Phase 9 — Try Coordinate Modeling

If the story contains:

```text
position
movement
grid
meeting
distance
```

write:

```text
dx = targetX-startX
dy = targetY-startY
```

Then ask:

```text
What can one legal move change?
```

Typical results:

```text
line:
|dx|

orthogonal grid:
|dx|+|dy|

8-direction:
max(|dx|,|dy|)

exact number of unit steps:
distance + parity condition
```

Do not memorize the metric before examining the move set.

---

# 19.12 Phase 10 — Try Extremal Modeling

If the problem says:

```text
maximum difference
worst case
all elements
minimum possible maximum
largest/smallest
```

ask:

```text
Which extreme controls the answer?
```

Common reductions:

```text
maximum difference
→ max-min

maximum pair sum
→ largest + second largest

maximum product with negatives
→ max(top2 product, bottom2 product)

minimum pair difference
→ adjacent after sorting

all a[i] <= X
→ max(a) <= X
```

---

# 19.13 Phase 11 — Try Answer-Space Modeling

If direct optimization is difficult:

```text
minimum X
maximum X
```

call the answer:

```text
X
```

Then ask:

```text
If X were given,
could I check whether it works?
```

Define:

```text
possible(X)
```

Then test monotonicity.

For minimum:

```text
F F F T T T
      ^
   first true
```

For maximum:

```text
T T T F F F
    ^
 last true
```

Only then think about binary search.

---

# 19.14 The 60-Second Modeling Scan

During a contest, after understanding the statement, run this scan:

```text
TARGET?
    |
    +-- equality?
    |      → difference / balance
    |
    +-- divisibility?
    |      → modulo
    |
    +-- odd/even?
    |      → parity
    |
    +-- repeated operation?
    |      → operation delta + invariant
    |
    +-- total/count?
    |      → contribution / frequency
    |
    +-- subarray/range?
    |      → prefix / difference
    |
    +-- movement?
    |      → coordinates / distance
    |
    +-- worst/best?
    |      → min/max/extremes
    |
    +-- minimum/maximum answer?
           → possible(X) + monotonicity
```

This is not a rigid checklist.

With practice, it becomes automatic pattern recognition.

---

# 19.15 The Constraint Scan

After building a candidate model, read constraints again.

Example:

```text
n <= 2*10^5
```

Likely acceptable:

```text
O(n)
O(n log n)
```

Usually not:

```text
O(n^2)
```

If:

```text
value <= 10^18
```

ask:

```text
Do I need to iterate over values?
```

Usually not.

Look for:

```text
formula
modulo
gcd
binary search
compressed state
```

Constraints do not give the solution, but they reject impossible approaches.

---

# 19.16 Tiny-Case Engine

Before coding, create tiny cases.

Start with:

```text
n = 1
```

Then:

```text
n = 2
```

Then special structures:

```text
all equal
strictly increasing
strictly decreasing
all odd
all even
one extreme
already at target
impossible parity
minimum values
maximum values
```

The goal is not random testing.

Use tiny cases to discover:

```text
what changes?
what remains?
what controls the answer?
```

---

# 19.17 Operation Problems — Complete Engine

For operation-based problems:

```text
CURRENT STATE
      ↓
TARGET STATE
      ↓
difference / deficit
      ↓
ONE operation delta
      ↓
invariant?
      ↓
necessary conditions
      ↓
are they sufficient?
      ↓
number of operations / construction
```

Example:

```text
current x
target y
operation x += d
```

Difference:

```text
y-x
```

Need:

```text
y-x = k*d
```

Thus:

```text
y >= x
```

and:

```text
(y-x) % d = 0
```

Then:

```text
k = (y-x)/d
```

Story completely disappears.

---

# 19.18 Counting Problems — Complete Engine

For counting:

```text
WHAT constitutes one valid object?
      ↓
Can I classify elements?
      ↓
How many choices from each class?
      ↓
multiply / combinations / contribution
```

Example:

Even-sum pair.

Classes:

```text
E
O
```

Valid:

```text
E-E
O-O
```

Answer:

```text
C(E,2)+C(O,2)
```

No nested loops.

---

# 19.19 Subarray Problems — Complete Engine

```text
SUBARRAY CONDITION
       ↓
convert element contributions if needed
       ↓
define prefix state
       ↓
write:
state(R)-state(L)=target
       ↓
rearrange
       ↓
what previous state is needed?
       ↓
frequency / set / earliest index
```

Example equal 0 and 1:

```text
0 -> -1
1 -> +1
```

Target:

```text
subarray sum = 0
```

Therefore:

```text
equal prefix balances
```

---

# 19.20 Optimization Problems — Complete Engine

```text
OBJECTIVE
   ↓
Can direct formula solve it?
   |
   +-- YES → use formula
   |
   +-- NO
        ↓
What controls objective?
min/max/extreme?
        ↓
Can greedy prove a choice?
        |
        +-- YES → greedy
        |
        +-- NO / candidate answer useful
               ↓
           call answer X
               ↓
           possible(X)
               ↓
           monotonic?
               ↓
        answer-space search
```

Do not jump directly to binary search.

---

# 19.21 Greedy Problems — Complete Engine

When a local choice looks attractive:

```text
candidate greedy choice
       ↓
why is delaying/replacing it never better?
       ↓
exchange / dominance / bottleneck /
forced-choice argument
       ↓
try to break with tiny cases
       ↓
proof
```

Remember:

```text
"looks best now"
```

is not a proof.

---

# 19.22 A Combined Example

Suppose:

> You have positions of houses. Choose `k` houses so the minimum distance between any consecutive chosen houses is as large as possible.

Translate:

```text
positions
choose k
maximize minimum gap
```

Modeling chain:

```text
maximize minimum
      ↓
candidate distance X
      ↓
possible(X):
can I choose k houses with gaps >= X?
      ↓
sort positions
      ↓
greedy choose earliest possible next house
      ↓
larger X is harder
      ↓
T T T F F
      ↓
last true
```

Techniques combined:

```text
sorting
+ coordinate distance
+ extremal objective
+ greedy
+ monotonicity
+ answer-space search
```

Real CF problems often combine multiple chapters.

---

# 19.23 Another Combined Example

Suppose:

> Count subarrays where numbers of odd and even elements are equal.

Translate each element:

```text
odd  -> +1
even -> -1
```

Condition:

```text
sum of transformed subarray = 0
```

Prefix:

```text
P[R+1]-P[L]=0
```

Therefore:

```text
P[R+1]=P[L]
```

Count equal prefix states.

Combined tools:

```text
state compression
+ contribution transformation
+ balance
+ prefix
+ frequency counting
```

---

# 19.24 Another Combined Example

Suppose:

> Starting from `(x1,y1)`, reach `(x2,y2)` in exactly `k` orthogonal moves.

Normalize:

```text
dx = x2-x1
dy = y2-y1
```

Minimum required moves:

```text
D = |dx|+|dy|
```

Necessary:

```text
D <= k
```

Extra moves:

```text
k-D
```

Orthogonal backtracking consumes 2 moves.

Therefore:

```text
(k-D) % 2 = 0
```

Final condition:

```text
D <= k
AND
(k-D) even
```

Combined tools:

```text
coordinates
+ lower bound
+ parity
+ constructive sufficiency
```

---

# 19.25 Necessary vs Sufficient

This distinction is essential.

Suppose you find:

```text
same parity is required
```

That proves:

```text
different parity → impossible
```

It does NOT automatically prove:

```text
same parity → possible
```

Always label conditions:

```text
NECESSARY:
must be true

SUFFICIENT:
if true, guarantees solution
```

Your final solution needs both directions when claiming an iff condition.

---

# 19.26 Proof Engine

For your proposed condition/algorithm, try one of these proof styles:

```text
1. ALGEBRA
   derive directly from equations

2. INVARIANT
   impossible states violate preserved property

3. CONSTRUCTION
   explicitly show how to achieve it

4. EXCHANGE
   replace an optimal choice with greedy choice

5. EXTREMAL
   show no candidate can exceed an extreme bound

6. CONTRADICTION
   assume a better/invalid situation and derive impossibility

7. MONOTONICITY
   reuse a solution at weaker/easier threshold
```

Most CF proofs are short once the correct model is found.

---

# 19.27 Complexity Comes After the Model

Bad workflow:

```text
constraints say 2e5
→ need O(n log n)
→ maybe sorting?
```

Better:

```text
derive mathematical structure
      ↓
derive algorithm
      ↓
calculate complexity
      ↓
compare with constraints
```

Constraints help validate the model, not replace it.

---

# 19.28 Implementation Is the Last Translation

There are really two translations:

```text
English
   ↓
Mathematics
   ↓
Algorithm
   ↓
C++
```

If you struggle heavily during implementation, ask:

```text
Is my mathematical model precise enough?
```

Often coding confusion comes from unresolved modeling ambiguity.

---

# 19.29 Contest Scratchpad Template

Use this on paper/editor:

```text
OBJECTS:
____________________

STATE / VARIABLES:
____________________

OPERATION:
____________________

TARGET:
____________________

OBJECTIVE:
____________________

ONE MOVE CHANGES:
____________________

INVARIANT:
____________________

EQUATION / INEQUALITY:
____________________

CAN STATE COMPRESS?
____________________

EXTREMES?
____________________

PREFIX / BALANCE?
____________________

CANDIDATE ANSWER X?
____________________

PROOF IDEA:
____________________

COMPLEXITY:
____________________
```

You do not need to fill every field.

The purpose is to force the story into structure.

---

# 19.30 Fast Contest Version

Eventually compress the scratchpad mentally to:

```text
1. What is changing?
2. What must become true?
3. What does one move do?
4. What stays unchanged?
5. What quantity should I track?
6. Can I reduce the state?
7. What controls the answer?
8. Can I prove the candidate?
9. Does complexity fit?
```

This is the core CP modeling engine.

---

# 19.31 When You Are Stuck

If you have understood the English but have no approach, do this:

```text
STOP thinking about algorithms.
```

Then:

```text
A. create n=1 / n=2 cases
B. write variables
C. write target equation
D. perform one operation manually
E. write before/after values
F. subtract them
G. check parity/remainder
H. inspect min/max
I. ask what exact information matters
J. only then reconsider algorithms
```

This often exposes the missing structure.

---

# 19.32 Example Stuck Table

Suppose operation transforms some state.

Create:

```text
before        after       change
--------------------------------
sum = ?       sum = ?     Δsum = ?
diff = ?      diff = ?    Δdiff = ?
parity = ?    parity = ?  changed?
mod m = ?     mod m = ?   changed?
min/max = ?   ...         ...
```

You are experimentally searching for:

```text
invariant
monotonic quantity
fixed delta
compressed state
```

This is far more useful than random simulation.

---

# 19.33 Rating 800–1100

Modeling often reduces to:

```text
direct translation
simple arithmetic
parity
min/max
counting
one operation effect
```

Train yourself to solve these quickly and cleanly.

Do not over-engineer.

---

# 19.34 Rating 1100–1400

Expect combinations such as:

```text
frequency + parity
sorting + extremes
greedy + simple proof
prefix + transformed values
operation + invariant
```

The main challenge is recognizing which basic model is hidden by the story.

---

# 19.35 Rating 1400–1600

Problems increasingly combine:

```text
two or three observations
```

Example:

```text
sort
+ greedy
+ parity condition
```

or:

```text
prefix
+ remainder
+ hashmap
```

or:

```text
extremal bound
+ constructive proof
```

Do not expect a single keyword to reveal the whole solution.

---

# 19.36 Rating 1600–1900

Modeling becomes more layered:

```text
compress state
      ↓
derive invariant
      ↓
transform objective
      ↓
greedy feasibility
      ↓
binary search answer
```

or:

```text
contribution
+ combinatorics
+ prefix/suffix information
```

The individual tools may still be basic.

The difficulty is connecting them.

---

# 19.37 How to Review a Solved Problem

After solving, do not record only:

```text
"used binary search"
```

Record the modeling chain:

```text
STORY:
maximize minimum distance

TRANSLATION:
candidate distance X

PREDICATE:
can place k objects with gap >= X

INNER STRUCTURE:
greedy earliest placement

MONOTONICITY:
larger X is harder

ALGORITHM:
sort + greedy check + binary search
```

This is what transfers to new problems.

---

# 19.38 How to Review a Failed Problem

If you needed editorial, identify the **first missed observation**.

Not:

```text
"I didn't know the solution."
```

Instead:

```text
I failed to transform equal counts into balance.

I failed to notice only parity matters.

I failed to consider the difference A-B.

I failed to turn minimum answer into possible(X).

I failed to prove that only extremes matter.
```

That gives you a specific skill to train.

---

# 19.39 Personal Modeling Notebook Format

For every useful problem, keep only:

```text
PROBLEM:
rating / link

STORY IN ONE LINE:
________________

KEY VARIABLES:
________________

CRITICAL EQUATION:
________________

HIDDEN STRUCTURE:
________________

WHY MY FIRST APPROACH FAILED:
________________

TRANSFERABLE PATTERN:
________________
```

Do not copy entire editorials.

Build a library of transformations.

---

# 19.40 The Pattern Library You Want in Your Head

Not:

```text
Problem 1234B solution = ...
```

But:

```text
equal counts
→ difference/balance

repeated +d
→ divisibility + direction

range sum
→ prefix difference

maximum pair distance
→ extremes

exact k moves
→ minimum distance + parity

minimum possible maximum
→ possible(X)

order irrelevant
→ frequencies

divisible subarray
→ equal prefix remainders
```

This is transferable problem-solving skill.

---

# 19.41 Complete Decision Tree

```text
START
  │
  ▼
Can I state the target mathematically?
  │
  ├── NO → reread / tiny examples / remove story
  │
  └── YES
       │
       ▼
Is there an operation?
       │
       ├── YES → compute one-operation delta
       │          │
       │          ├→ invariant?
       │          ├→ monotonic quantity?
       │          └→ operation count equation?
       │
       ▼
Does target involve equality?
       │
       └→ difference / balance = 0
       │
       ▼
Divisibility / periodicity?
       │
       └→ modulo / gcd / remainder classes
       │
       ▼
Odd/even?
       │
       └→ parity
       │
       ▼
Order irrelevant?
       │
       └→ frequencies / multiset / counts
       │
       ▼
Range/subarray?
       │
       └→ prefix / difference / balance
       │
       ▼
Movement?
       │
       └→ coordinates / displacement / distance
       │
       ▼
Worst/best/all-elements constraint?
       │
       └→ min/max / extremes / bottleneck
       │
       ▼
Optimization difficult directly?
       │
       └→ candidate X / possible(X) / monotonicity
       │
       ▼
Can a local choice be proved safe?
       │
       └→ greedy
       │
       ▼
PROVE
       │
       ▼
CHECK COMPLEXITY
       │
       ▼
CODE
```

---

# 19.42 Final Pre-Code Checklist

Before typing C++:

```text
[ ] I know exactly what the target means.

[ ] I know what each variable represents.

[ ] I can explain one operation mathematically.

[ ] I know why my condition is necessary.

[ ] I know why it is sufficient.

[ ] I tested tiny cases.

[ ] I tested boundary cases.

[ ] I know which information is irrelevant.

[ ] I know why my greedy/search/count works.

[ ] Complexity fits the full test file.

[ ] Integer overflow is considered.
```

If several boxes are unclear, coding is premature.

---

# 19.43 The Entire Mini-Course in One Tree

```text
CP MATH MODELING
│
├── FOUNDATION
│   ├── variables
│   ├── equations
│   ├── inequalities
│   └── tiny cases
│
├── STORY → MATHEMATICS
│   ├── remove names
│   ├── define state
│   └── define target
│
├── OPERATION MODELING
│   ├── one-move delta
│   ├── repeated operations
│   └── reachability
│
├── INVARIANTS
│   ├── parity
│   ├── modulo
│   ├── gcd
│   └── preserved quantities
│
├── COUNTING / CONTRIBUTION
│   ├── classify
│   ├── count choices
│   └── sum contributions
│
├── GREEDY MODELING
│   ├── bottleneck
│   ├── exchange
│   └── dominance
│
├── PREFIX / DIFFERENCE
│   ├── accumulated state
│   ├── range = prefix difference
│   └── boundary events
│
├── COORDINATES
│   ├── displacement
│   ├── distance
│   └── parity of movement
│
├── EXTREMES
│   ├── min/max
│   ├── top/bottom candidates
│   └── bottleneck
│
├── STATE COMPRESSION
│   ├── parity/remainder
│   ├── difference/balance
│   ├── frequencies
│   └── equivalence classes
│
└── ANSWER SPACE
    ├── bounds
    ├── possible(X)
    ├── monotonicity
    └── first/last feasible
```

---

# 19.44 The Ultimate 9 Questions

For any unfamiliar CF problem, ask:

```text
1. WHAT ARE THE VARIABLES?

2. WHAT EXACTLY IS THE TARGET?

3. WHAT DOES ONE OPERATION / CHOICE DO?

4. WHAT STAYS UNCHANGED?

5. WHAT QUANTITY ACTUALLY MATTERS?

6. CAN I WRITE AN EQUATION OR INEQUALITY?

7. WHICH CANDIDATES CAN I ELIMINATE?

8. WHY IS MY REMAINING APPROACH CORRECT?

9. DOES IT FIT THE CONSTRAINTS?
```

If you repeatedly train these nine questions, mathematical translation becomes faster.

---

# 19.45 Final Mental Model

```text
              ENGLISH STORY
                   │
                   ▼
             REMOVE THE STORY
                   │
                   ▼
        VARIABLES + TARGET + RULES
                   │
                   ▼
          EQUATION / INEQUALITY
                   │
                   ▼
             ONE-STEP EFFECT
                   │
                   ▼
      INVARIANT / BALANCE / BOUND
                   │
                   ▼
             COMPRESS STATE
                   │
                   ▼
       CONTRIBUTION / PREFIX /
       COORDINATE / EXTREMES
                   │
                   ▼
        DIRECT / GREEDY / SEARCH
                   │
                   ▼
                 PROOF
                   │
                   ▼
              COMPLEXITY
                   │
                   ▼
                  C++
```

The most important principle of this entire course:

```text
DO NOT START BY ASKING:

"Which algorithm is this?"

START BY ASKING:

"What is this story saying mathematically?"
```

Once the mathematical structure is visible, the algorithm is often much easier to recognize.

---

# Course Completion

You now have the full **CP Mathematical Modeling Mini-Course**.

The next step is not another theory chapter.

The next step should be:

```text
REAL CF PROBLEM
      ↓
you perform the modeling workflow
      ↓
no code initially
      ↓
derive the mathematical model
      ↓
identify the algorithm
      ↓
then implement
```

That is how this material becomes contest skill rather than notes you have merely read.
